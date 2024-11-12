package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.tree.DecisionTreeLeafNode;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.util.*;
import java.util.stream.Collectors;

import static de.uni_trier.wi2.pki.util.EntropyUtils.calcInformationGain;
import static de.uni_trier.wi2.pki.util.Helpers.printCollectionOfObjectArrays;
import static de.uni_trier.wi2.pki.util.Helpers.printListLikeMapEntries;

/**
 * Utility class for creating a decision tree with the ID3 algorithm.
 */
public class ID3Utils {

    /**
     * Create the decision tree given the example and the index of the label attribute.
     *
     * @param examples   The examples to train with. This is a collection of arrays.
     * @param labelIndex The label of the attribute that should be used as an index.
     * @return The root node of the decision tree
     */
    public static DecisionTree createTree(Collection<Object[]> examples, int labelIndex) {
        return createTree(examples, labelIndex, -1);
    }

    /**
     * Create the decision tree given the example and the index of the label attribute.
     *
     * @param examples     The examples to train with. This is a collection of arrays.
     * @param labelIndex   The label of the attribute that should be used as an index.
     * @param maximumDepth Maximum depth of tree.
     * @return The root node of the decision tree
     */
    public static DecisionTree createTree(Collection<Object[]> examples, int labelIndex, int maximumDepth) {
        return (DecisionTree) createTree(examples, labelIndex, maximumDepth, 1, null);
    }

    private static DecisionTreeNode createTree(Collection<Object[]> examples, int labelIndex, int maximumDepth, int currentDepth, DecisionTreeNode parent) {

        /* validating the given input data for any numeric values */
        if (!validateInput(examples)) {
            throw new IllegalArgumentException("Invalid input");
        }

        int efficientAttributeIndex = selectEfficientAttribute(examples, labelIndex);
        ArrayList<Object[]> examplesList = new ArrayList<>(examples);
        Map<Object, List<Object[]>> partitions;
        DecisionTree dt = new DecisionTree(efficientAttributeIndex, examplesList);

        System.out.println("Current Depth: " + currentDepth);
        System.out.println("Efficient Attribute Index: " + efficientAttributeIndex);

        /* If the examples are empty, return a leaf node with the dominant class of the parent node */
        if (examplesList.isEmpty()) {
            return new DecisionTreeLeafNode(dt, examplesList, getDominantClass(parent.getElements(), labelIndex));
        }

        List<String> allLabelsOfExampleList = examplesList.stream().map(o -> o[labelIndex].toString()).toList();
        System.out.println("All Class Labels of ExamplesList: " + allLabelsOfExampleList);
        System.out.println("Example List: \n" + printCollectionOfObjectArrays(examplesList));

        /* If all examples have the same label, return a leaf node with the corresponding label */
        if (allLabelsSame(examplesList, labelIndex)) {
            return new DecisionTreeLeafNode(dt, examplesList, examplesList.get(0)[labelIndex].toString());
        }

        /* If the maximum depth of the tree is reached, return a leaf node with the most common label */
        if (currentDepth == maximumDepth) {
            return new DecisionTreeLeafNode(dt, examplesList, getDominantClass(examplesList, labelIndex));
        }
        /* Otherwise, recursively create a new decision tree node */
        else {
            partitions = partitionExamples(examplesList, efficientAttributeIndex);
            System.out.println("* of Partitions: " + partitions.size());
            System.out.println("Partitions: \n" + printListLikeMapEntries(partitions));

            /* If there is only one partition, return a leaf node with the most common label */
            if (partitions.size() == 1) {
                return new DecisionTreeLeafNode(dt, examplesList, getDominantClass(examplesList, labelIndex));
            }

            for (Map.Entry<Object, List<Object[]>> entry : partitions.entrySet()) {
                System.out.println("Adding Split for Key: " + entry.getKey());
                System.out.println("Splitted Values: \n" + printCollectionOfObjectArrays(entry.getValue()));
                dt.addSplit(entry.getKey().toString(), createTree(entry.getValue(), labelIndex, maximumDepth, currentDepth + 1, dt));
            }
        }

        return dt;
    }

    /**
     * Checks if all labels are the same.
     *
     * @param examples The examples to check.
     * @param labelIndex   The index of the label attribute.
     * @return true if all labels are the same, false otherwise.
     */
    private static boolean allLabelsSame(ArrayList<Object[]> examples, int labelIndex) {
        return examples.stream()
                .allMatch(o -> o[labelIndex].equals(examples.get(0)[labelIndex]));
    }

    /**
     * Partitions the examples based on the attribute index.
     *
     * @param examples             The examples to partition.
     * @param efficientAttributeIndex  The index of the attribute to partition by.
     * @return A map of the partitioned examples.
     */
    private static Map<Object, List<Object[]>> partitionExamples(Collection<Object[]> examples, int efficientAttributeIndex) {
        Map<Object, List<Object[]>> partitions = new HashMap<>();
        //Set<Object> uniqueValues = new HashSet<>();

        for (Object[] example : examples) {
            Object attributeValue = example[efficientAttributeIndex];
            //uniqueValues.add(attributeValue);
            /* Creates a map where key is attributeValue and value contains all examples with the given attributeValue */
            partitions.computeIfAbsent(attributeValue, (k -> new ArrayList<>())).add(example);
        }

        // Check for homogeneous attribute values
        /*
        if (uniqueValues.size() == 1) {
            // All attribute values are the same, return a single partition
            return Collections.singletonMap(uniqueValues.iterator().next(), new ArrayList<>(examples));
        }
         */

        return partitions;
    }

    /**
     * Checks if the input Collection has invalid attribute types.
     *
     * @param examples The examples to check.
     * @implNote This operation is parallelized.
     * @return true if the input is valid, false otherwise.
     *
     */
    private static boolean validateInput(Collection<Object[]> examples) {
        return examples.parallelStream().anyMatch(o -> Arrays.stream(o).anyMatch(ID3Utils::testAttribute));
    }

    /**
     * Tests if the attribute is numeric.
     *
     * @param attribute The attribute to test.
     * @return true if the attribute is a number, false otherwise.
     */
    private static boolean testAttribute(Object attribute) {
        return attribute instanceof Number;
    }

    /**
     * Selects the most efficient attribute.
     *
     * @param examples   The examples to train with. This is a collection of arrays.
     * @param labelIndex The label of the attribute that should be used as an index.
     * @return the index of the attribute to select next.
     */
    public static int selectEfficientAttribute(Collection<Object[]> examples, int labelIndex) {
        int attributeIndex = 0;

        try {
            if (calcInformationGain(examples, labelIndex) != null) {
                attributeIndex = calcInformationGain(examples, labelIndex)
                        /* Find the index of the maximum information gain */
                        .indexOf(Collections.max(calcInformationGain(examples, labelIndex)));
            }
        } catch (NullPointerException npe) {
            System.out.println("Error in selectEfficientAttribute: " + npe.getMessage());
        }

        return attributeIndex;
    }

    /**
     * Determines the dominant label type for the given examples.
     *
     * @param examples   the examples to evaluate.
     * @param labelIndex the label index
     * @return the class name of the dominant class.
     */
    public static String getDominantClass(Collection<Object[]> examples, int labelIndex) {
        return examples.stream()
                /* Group by the label index and count the occurrences */
                .collect(Collectors.groupingBy(o -> o[labelIndex].toString(), Collectors.counting()))
                /* Find the maximum count */
                .entrySet().stream().max(Map.Entry.comparingByValue())
                /* Return the class name */
                .map(Map.Entry::getKey).orElse(null);
    }

    /**
     * Compute the classification accuracy for the given decision tree and the examples.
     *
     * @param decisionTree       the decision tree to use for predictions.
     * @param validationExamples the examples to evaluate.
     * @param labelIndex         the index of the label attribute.
     * @return the classification accuracy.
     */
    public static double getClassificationAccuracy(DecisionTree decisionTree, Collection<Object[]> validationExamples, int labelIndex) {
        //tmp
        return 0;
    }
}