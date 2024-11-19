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
        int efficientAttributeIndex = selectEfficientAttribute(examples, labelIndex);
        ArrayList<Object[]> examplesList = new ArrayList<>(examples);
        Map<Object, List<Object[]>> partitions;
        DecisionTree root = null;
        DecisionTreeNode currentNode = null;

        /* Only create a root node if the parent is null */
        if (parent == null) {
            root = new DecisionTree(efficientAttributeIndex, examplesList);
            currentNode = root;
        } else {
            currentNode = new DecisionTreeNode(parent, examplesList, efficientAttributeIndex);
        }

        // DEBUG
        StringBuilder depth = new StringBuilder();
        depth.append("-".repeat(Math.max(0, currentDepth)));
        System.out.println(depth + "Current Depth: " + currentDepth);
        System.out.println(depth + "Efficient Attribute Index: " + efficientAttributeIndex);

        /* If the examples are empty, return a leaf node with the dominant class of the parent node */
        if (examplesList.isEmpty()) {
            return new DecisionTreeLeafNode(parent, examplesList, getDominantClass(parent.getElements(), labelIndex));
        }

        /* If all examples have the same label, return a leaf node with the corresponding label */
        if (allLabelsSame(examplesList, labelIndex)) {
            return new DecisionTreeLeafNode(parent, examplesList, examplesList.get(0)[labelIndex].toString());
        }

        /* If the maximum depth of the tree is reached, return a leaf node with the most common label */
        if (currentDepth == maximumDepth) {
            return new DecisionTreeLeafNode(parent, examplesList, getDominantClass(examplesList, labelIndex));
        }

        /* Otherwise, recursively create a new decision tree node */
        partitions = partitionExamples(examplesList, efficientAttributeIndex);
        System.out.println(depth + "* of Partitions: " + partitions.size());

        /* If there are multiple partitions, create a new split for each partition */
        if (partitions.size() > 1) {
            for (Map.Entry<Object, List<Object[]>> entry : partitions.entrySet()) {
                System.out.println(depth + "* of Partitions: " + partitions.size());
                System.out.println(depth + "Adding Split for Key: " + entry.getKey());
                String attribute = entry.getKey().toString();
                DecisionTreeNode node = createTree(entry.getValue(), labelIndex, maximumDepth, currentDepth + 1, currentNode);
                currentNode.addSplit(attribute, node);
            }
        /* If there is only one partition, return a leaf node with the most common label */
        } else if (partitions.size() == 1) {
            System.out.println(depth + "Only one Partition found. Returning Leaf Node with Dominant Class.");
            return new DecisionTreeLeafNode(currentNode, examplesList, getDominantClass(examplesList, labelIndex));
        }

        return currentNode;
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

        for (Object[] example : examples) {
            String key = example[efficientAttributeIndex].toString();
            /* Creates a map where key is attributeIndex and value contains all examples with the given attributeIndex */
            partitions.computeIfAbsent(key, (k -> new ArrayList<>())).add(example);
        }

        return partitions;
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
            List<Double> informationGains = calcInformationGain(examples, labelIndex);
            attributeIndex = informationGains.indexOf(Collections.max(informationGains));
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
        int correctPredictions = 0;

        /* Predict the class of each example and compare it to the actual class */
        for (Object[] example : validationExamples) {
            String actualLabel = example[labelIndex].toString();
            String predictedLabel = decisionTree.predict(example);

            if (actualLabel.equals(predictedLabel)) {
                correctPredictions++;
            }
        }

        /* Return the classification accuracy as ratio of correct predictions to total predictions */
        return (double) correctPredictions / validationExamples.size();
    }
}