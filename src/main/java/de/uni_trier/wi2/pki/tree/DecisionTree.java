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
        if (example == null) {
            throw new IllegalArgumentException("Example cannot be null");
        }

        DecisionTreeNode currentNode = this;

        /* Traverse the tree until a leaf node is reached */
        while (currentNode != null && !currentNode.isLeafNode()) {
            int attributeIndex = currentNode.getAttributeIndex();
            if (attributeIndex < 0 || attributeIndex >= example.length) {
                throw new IllegalArgumentException("Invalid attribute index: " + attributeIndex);
            }

            String attributeValue = example[attributeIndex].toString();
            currentNode = currentNode.getSplits().get(attributeValue);

        }

        /* Return the class of the leaf node if a leaf node is found */
        if (currentNode != null && currentNode.isLeafNode()) {
            return ((DecisionTreeLeafNode) currentNode).getLabelClass();
        }

        /*
         * If no node is found up to this point then the prediction is not possible because there is no path for the given example
         * thus the prediction is wrong and returns a wrong label which is then used to calculate the accuracy
         */
        return "No prediction possible";
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