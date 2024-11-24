package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.tree.DecisionTreeLeafNode;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Prunes a trained decision tree in a post-pruning way.
 */
public class ReducedErrorPruner {

    /*if error (parent) < error (child){then prune the child node}
    else dont prune the child node
     */

    int numberOfNodes = 0;

    /**
     * The starting classification accuracy.
     */
    private double classificationAccuracy;

    /**
     * The decision tree along the pruning procedure.
     */
    private DecisionTree decisionTree;

    /**
     * Prunes the given decision tree in-place.
     *
     * @param trainedDecisionTree The decision tree to prune.
     * @param validationExamples  the examples to validate the pruning with.
     * @param labelAttributeId    The label attribute.
     */
    public void prune(DecisionTree trainedDecisionTree, Collection<Object[]> validationExamples, int labelAttributeId) {
        pruneTree(trainedDecisionTree, validationExamples, labelAttributeId);
    }

    /**
     * Recursive method for pruning the tree along certain paths.
     *
     * @param currentDecisionTreeNode the current tree node to start from.
     * @param validationExamples      the validation examples.
     * @param labelAttributeIndex     the label attribute index
     */
    private void pruneTree(DecisionTreeNode currentDecisionTreeNode, Collection<Object[]> validationExamples, int labelAttributeIndex) {

        numberOfNodes++;
        System.out.println(numberOfNodes);
        /// Anker start
        System.out.println("in pruneTree");

        if (currentDecisionTreeNode.isLeafNode()) {
            System.out.println("in pruneTree - isLeafNode");
            System.out.println("jetzt haben wir den ersten leaf knoten gefunden");
            numberOfNodes--;
            return;
        }


        for (DecisionTreeNode childNode : currentDecisionTreeNode.getSplits().values()) {
            System.out.println("in pruneTree - for loop");
            pruneTree(childNode, validationExamples, labelAttributeIndex);
        }

        System.out.println("in pruneTree - after for loop");
        classificationAccuracy = getTreeAccuracy(currentDecisionTreeNode, validationExamples, labelAttributeIndex);
        System.out.println("in pruneTree - classificationAccuracy: " + classificationAccuracy);

        String dominantClass = ID3Utils.getDominantClass(currentDecisionTreeNode.getElements(), labelAttributeIndex);
        System.out.println("in pruneTree - dominantClass");
        DecisionTreeLeafNode prunedLeafNode = new DecisionTreeLeafNode(currentDecisionTreeNode.getParent(), currentDecisionTreeNode.getElements(), dominantClass);
        System.out.println("in pruneTree - prunedLeafNode");

        double classificationAccuracyAfterPruning = getTreeAccuracyAfterPruning(currentDecisionTreeNode, prunedLeafNode, validationExamples, labelAttributeIndex);
        System.out.println("in pruneTree - classificationAccuracyAfterPruning");

        if (classificationAccuracyAfterPruning >= classificationAccuracy) {
            System.out.println("in pruneTree - if");
            if (currentDecisionTreeNode.getParent() != null) {
                currentDecisionTreeNode.getParent().addSplit(null, prunedLeafNode);
            }
        }
    }

    /**
     * Calculate the accuracy of the current tree structure on the validation set.
     * This method walks through the current tree to make predictions and check accuracy.
     */
    private double getTreeAccuracy(DecisionTreeNode node, Collection<Object[]> validationExamples, int labelAttributeId) {
        int correctPredictions = 0;
        for (Object[] example : validationExamples) {
            DecisionTreeNode currentNode = node;
            while (currentNode != null && !currentNode.isLeafNode()) {
                String attributeValue = example[currentNode.getAttributeIndex()].toString();
                currentNode = currentNode.getSplits().get(attributeValue);
            }
            String predictedLabel = currentNode.getElements().iterator().next()[labelAttributeId].toString();
            String actualLabel = example[labelAttributeId].toString();
            if (predictedLabel.equals(actualLabel)) {
                correctPredictions++;
            }
        }
        return (double) correctPredictions / validationExamples.size();
    }

    /**
     * Calculate the accuracy of the tree if we prune the given node.
     * This will simulate the pruning by checking how the accuracy changes with the pruned leaf node.
     */
    private double getTreeAccuracyAfterPruning(DecisionTreeNode node, DecisionTreeLeafNode prunedLeafNode, Collection<Object[]> validationExamples, int labelAttributeId) {
        int correctPredictions = 0;
        for (Object[] example : validationExamples) {
            DecisionTreeNode currentNode = prunedLeafNode; // Use the pruned leaf node instead of traversing the tree
            String predictedLabel = currentNode.getElements().iterator().next()[labelAttributeId].toString();
            String actualLabel = example[labelAttributeId].toString();
            if (predictedLabel.equals(actualLabel)) {
                correctPredictions++;
            }
        }
        return (double) correctPredictions / validationExamples.size();
    }


    /**
     * public void pruneTree(DecisionTreeNode node, Collection<Object[]> validationExamples, int labelAttributeId) {
     *     // If the node is a leaf node, return (we can't prune leaves)
     *     if (node.isLeafNode()) {
     *         return;
     *     }
     *
     *     // Prune the child nodes recursively first
     *     for (DecisionTreeNode childNode : node.getSplits().values()) {
     *         pruneTree(childNode, validationExamples, labelAttributeId);
     *     }
     *
     *     // Now, attempt to prune this node by turning it into a leaf
     *     // Let's first calculate the accuracy of the current tree
     *     double currentAccuracy = getTreeAccuracy(node, validationExamples, labelAttributeId);
     *
     *     // Convert the current node into a leaf node with the most common class in its data
     *     String dominantClass = getDominantClass(node.getElements(), labelAttributeId);
     *     DecisionTreeLeafNode prunedLeafNode = new DecisionTreeLeafNode(node.getParent(), node.getElements(), dominantClass);
     *
     *     // Calculate the accuracy of the tree after pruning this node
     *     double prunedAccuracy = getTreeAccuracyAfterPruning(node, prunedLeafNode, validationExamples, labelAttributeId);
     *
     *     // If pruning improves or maintains the accuracy, then prune the node
     *     if (prunedAccuracy >= currentAccuracy) {
     *         // Replace the current node with the pruned leaf node
     *         if (node.getParent() != null) {
     *             node.getParent().addSplit(node.getAttributeIndex() + "", prunedLeafNode);
     *         }
     *     }
     * }
     *
     *
     *  * Helper method to calculate the accuracy of the tree (or subtree) with the current structure.
     *
     *

    private double getTreeAccuracy(DecisionTreeNode node, Collection<Object[]> validationExamples, int labelAttributeId) {
     *DecisionTree tree = new DecisionTree(node);
     *return ID3Utils.getClassificationAccuracy(tree, validationExamples, labelAttributeId);
     *}
     *
             *
      *  * Helper method to calculate the accuracy after pruning a node.
      *  * In this case, the node is pruned by replacing it with a leaf node.
      *
             *

    private double getTreeAccuracyAfterPruning(DecisionTreeNode node, DecisionTreeLeafNode prunedLeafNode, Collection<Object[]> validationExamples, int labelAttributeId) {
     *     // Create a new tree with the pruned leaf node
     *DecisionTree prunedTree = new DecisionTree(prunedLeafNode);
     *return ID3Utils.getClassificationAccuracy(prunedTree, validationExamples, labelAttributeId);
     *}
     */

    /*
    private void pruneTree(DecisionTreeNode currentDecisionTreeNode, Collection<Object[]> validationExamples, int labelAttributeIndex) {
        // If the current node is a leaf node, no pruning is needed
        if (currentDecisionTreeNode.isLeafNode()) {
            return;
        }

        // Recursively prune all child nodes
        for (DecisionTreeNode child : currentDecisionTreeNode.getSplits().values()) {
            pruneTree(child, validationExamples, labelAttributeIndex);
        }

        // Store the original splits (children) to restore if needed
        Map<String, DecisionTreeNode> originalSplits = new HashMap<>(currentDecisionTreeNode.getSplits());

        // Prune the current node by converting it to a leaf node
        String dominantClass = ID3Utils.getDominantClass(currentDecisionTreeNode.getElements(), labelAttributeIndex);

        // Convert the current node to a leaf node (by removing its splits)
        currentDecisionTreeNode.splits.clear(); // Clear the splits
        currentDecisionTreeNode.splits.put(dominantClass, new DecisionTreeNode(currentDecisionTreeNode, currentDecisionTreeNode.getElements(), -1));

        // Evaluate the accuracy after pruning
        double accuracyAfterPruning = ID3Utils.getClassificationAccuracy(decisionTree, validationExamples, labelAttributeIndex);

        // If pruning results in worse accuracy, revert the changes
        if (accuracyAfterPruning < classificationAccuracy) {
            // Restore the original splits (undo pruning)
            currentDecisionTreeNode.splits = originalSplits;
        }
    }

     */

}
