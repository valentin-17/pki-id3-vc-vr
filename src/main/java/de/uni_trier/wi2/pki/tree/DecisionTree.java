package de.uni_trier.wi2.pki.tree;

import java.util.*;

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
        DecisionTreeNode currentNode = getClassificationNode(example);
        if (currentNode instanceof DecisionTreeLeafNode) {
            return ((DecisionTreeLeafNode) currentNode).getLabelClass();
        }
        else {
            return "No leaf node found";
        }
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