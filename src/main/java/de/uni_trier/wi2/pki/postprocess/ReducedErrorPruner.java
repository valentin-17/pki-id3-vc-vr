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
        List<Object[]> validationExamplesList = new ArrayList<>(validationExamples);

        if (currentDecisionTreeNode.isLeafNode()) {
            return;
        }

        for (DecisionTreeNode child : currentDecisionTreeNode.getSplits().values()) {
            pruneTree(child, validationExamples, labelAttributeIndex);
        }

        classificationAccuracy = CrossValidator.evaluateModel(decisionTree, validationExamplesList, labelAttributeIndex);

        DecisionTreeLeafNode leafNode = new DecisionTreeLeafNode(currentDecisionTreeNode, currentDecisionTreeNode.getElements(), ID3Utils.getDominantClass(currentDecisionTreeNode.getElements(), labelAttributeIndex));
        double classificationAccuracyAfterPruning = CrossValidator.evaluateModel(decisionTree, validationExamplesList, labelAttributeIndex);

        if (classificationAccuracyAfterPruning > classificationAccuracy) {
            currentDecisionTreeNode.getParent().addSplit(null, leafNode);
        }
    }

}
