package de.uni_trier.wi2.pki.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DecisionTree extends DecisionTreeNode {

    /**
     * Constructor for decision tree.
     *
     * @param attributeIndex Index for current attribute.
     * @param elements       Elements for current node.
     */
    public DecisionTree(int attributeIndex, Collection<Object[]> elements) {
        super(null, elements, attributeIndex);
    }

    /**
     * Predict the class of a single example.
     *
     * @param example the attribute array of the example to predict.
     * @return the predicted class as a string
     */
    public String predict(Object[] example) {
        DecisionTreeNode currentNode = this;

        /* Traverse the tree until a leaf node is reached */
        while (currentNode instanceof DecisionTree treeNode) {
            String attributeValue = (String) example[treeNode.getAttributeIndex()];
            currentNode = treeNode.getSplits().get(attributeValue);
        }

        /* Return the class of the leaf node if a leaf node is found */
        if (currentNode instanceof DecisionTreeLeafNode) {
            return ((DecisionTreeLeafNode) currentNode).getLabelClass();
        }
        throw new IllegalStateException("Prediction failed: no leaf node found.");
    }

    /**
     * Predict the class of multiple examples.
     *
     * @param examples the list of examples
     * @return a list of string values that represent the predicted classes
     */
    public List<String> predictAll(List<Object[]> examples) {
        List<String> results = new ArrayList<>();

        /* Predict the class of each example */
        for (Object[] example : examples) {
            results.add(predict(example));
        }
        return results;
    }
}