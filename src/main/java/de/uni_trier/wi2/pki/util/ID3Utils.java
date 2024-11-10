package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.tree.DecisionTreeLeafNode;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.util.*;
import java.util.stream.Collectors;

import static de.uni_trier.wi2.pki.util.EntropyUtils.calcInformationGain;

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

        /* validating the given input data for any numeric values */
        if (!validateInput(examples)) {
            throw new IllegalArgumentException("Invalid input");
        }

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
        ArrayList<Object[]> examplesList = new ArrayList<>(examples);
        int efficientAttributeIndex = selectEfficientAttribute(examplesList, labelIndex);
        DecisionTreeNode rootNode = new DecisionTreeNode(parent, examplesList, efficientAttributeIndex);

        System.out.println("Efficient Attribute Index: " + efficientAttributeIndex);

        /* If the examples are empty, return a leaf node with an appropriate class */
        // TODO: figure out what an appropriate class is
        if (examplesList.isEmpty()) {
            return new DecisionTreeLeafNode(rootNode, examplesList, getDominantClass(examplesList, labelIndex));
        }

        /* If all examples have the same label, return a leaf node with the corresponding label */
        if (examplesList.stream().allMatch(o -> o[labelIndex].equals(examplesList.get(0)[labelIndex]))) {
            return new DecisionTreeLeafNode(rootNode, examplesList, (String) examplesList.get(0)[labelIndex]);
        }

        /* If the maximum depth of the tree is reached, return a leaf node with the most common label */
        if (currentDepth == maximumDepth) {
            return new DecisionTreeLeafNode(rootNode, examplesList, getDominantClass(examplesList, labelIndex));
        }
        /* Otherwise, recursively create a new decision tree node */
        else {
            Map<Object, List<Object[]>> partitions = partitionExamples(examplesList, efficientAttributeIndex);
            for (Map.Entry<Object, List<Object[]>> entry : partitions.entrySet()) {
                rootNode.addSplit(entry.getKey().toString(), createTree(entry.getValue(), labelIndex, maximumDepth, currentDepth + 1, rootNode));
            }
        }

        return rootNode;
    }

    /**
     * Partitions the examples based on the attribute index.
     *
     * @param examplesList             The examples to partition.
     * @param efficientAttributeIndex  The index of the attribute to partition by.
     * @return A map of the partitioned examples.
     */
    private static Map<Object, List<Object[]>> partitionExamples(List<Object[]> examplesList, int efficientAttributeIndex) {
        Map<Object, List<Object[]>> partitions = new HashMap<>();

        for (Object[] example : examplesList) {
            Object attributeValue = example[efficientAttributeIndex];
            partitions.computeIfAbsent(attributeValue, (k -> new ArrayList<>())).add(example);
        }

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
        return calcInformationGain(examples, labelIndex)
                /* Find the index of the maximum information gain */
                .indexOf(Collections.max(calcInformationGain(examples, labelIndex)));
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
                .collect(Collectors.groupingBy(o -> (String) o[labelIndex], Collectors.counting()))
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