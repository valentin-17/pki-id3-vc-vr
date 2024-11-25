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
        this.decisionTree = trainedDecisionTree;
        pruneTree(trainedDecisionTree, validationExamples, labelAttributeId);
    }

    /**
     * Recursive method for pruning the tree along certain paths.
     *
     * @param currentDecisionTreeNode the current tree node to start from.
     * @param validationExamples      the validation examples.
     * @param labelAttributeIndex     the label attribute index
     */

    public void pruneTree(DecisionTreeNode currentDecisionTreeNode, Collection<Object[]> validationExamples, int labelAttributeIndex) {
        if (currentDecisionTreeNode == null || currentDecisionTreeNode.isLeafNode()) {
            return;
        }

        // Collect child nodes into a temporary list to avoid concurrent modification
        List<DecisionTreeNode> childNodesSet = new ArrayList<>(currentDecisionTreeNode.getSplits().values());

        // Recursively prune each child node
        for (DecisionTreeNode childNode : childNodesSet) {
            pruneTree(childNode, validationExamples, labelAttributeIndex);
        }

        // Attempt to prune this node
        classificationAccuracy = ID3Utils.getClassificationAccuracy(decisionTree, validationExamples, labelAttributeIndex);

        String dominantClass = ID3Utils.getDominantClass(currentDecisionTreeNode.getElements(), labelAttributeIndex);
        DecisionTreeLeafNode prunedLeafNode = new DecisionTreeLeafNode(currentDecisionTreeNode.getParent(), currentDecisionTreeNode.getElements(), dominantClass);

        double prunedAccuracy = getTreeAccuracyAfterPruning(currentDecisionTreeNode, prunedLeafNode, validationExamples, labelAttributeIndex);

        if (prunedAccuracy >= classificationAccuracy) {
            if (currentDecisionTreeNode.getParent() != null) {
                currentDecisionTreeNode.getParent().addSplit(currentDecisionTreeNode.getAttributeIndex() + "", prunedLeafNode);
            } /*else {
                System.err.println("***********Warning: Attempted to prune root node.");

            }
            */
        }
    }


    private double getTreeAccuracy(DecisionTreeNode node, Collection<Object[]> validationExamples, int labelAttributeId) {
        int correctPredictions = 0;

        for (Object[] example : validationExamples) {
            DecisionTreeNode currentNode = node;

            // Traverse the tree for this example
            while (currentNode != null && !currentNode.isLeafNode()) {
                String attributeValue = example[currentNode.getAttributeIndex()].toString();

                // Handle case where no split exists for the attribute value
                if (!currentNode.getSplits().containsKey(attributeValue)) {
                    System.err.println("----Warning: Missing split for attribute value: " + attributeValue);
                    currentNode = null; // Break out of the loop
                    break;
                }

                currentNode = currentNode.getSplits().get(attributeValue);
            }

            // If we successfully reached a leaf node, check the prediction
            if (currentNode != null && currentNode.isLeafNode()) {
                String predictedLabel = currentNode.getElements().iterator().next()[labelAttributeId].toString();
                String actualLabel = example[labelAttributeId].toString();
                if (predictedLabel.equals(actualLabel)) {
                    correctPredictions++;
                }
            } else {
                System.err.println("Warning: Could not classify example due to incomplete tree traversal.");
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
            String predictedLabel = prunedLeafNode.getElements().iterator().next()[labelAttributeId].toString();
            String actualLabel = example[labelAttributeId].toString();
            if (predictedLabel.equals(actualLabel)) {
                correctPredictions++;
            }
        }
        return (double) correctPredictions / validationExamples.size();
    }
}