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

        // tmp
        DecisionTreeLeafNode leafNode = null;
        return leafNode.getLabelClass();
    }

    /**
     * Predict the class of multiple examples.
     *
     * @param examples the list of examples
     * @return a list of string values that represent the predicted classes
     */
    public List<String> predictAll(List<Object[]> examples) {
        ArrayList<String> results = new ArrayList<>();

        return results;
    }

    /**
     * Print the tree.
     *
     * @return the tree as a string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        buildString(sb, this, 0);
        return sb.toString();
    }

    /**
     * Build the string representation of the tree.
     *
     * @param sb    the string builder to append to
     * @param node  the current node
     * @param depth the current depth
     */
    private void buildString(StringBuilder sb, DecisionTreeNode node, int depth) {
        if (node == null) {
            return;
        }

        //sb.append("  ".repeat(Math.max(0, depth)));
        sb.append(node).append("\n");

        if (node instanceof DecisionTreeLeafNode) {
            return;
        }
        if (node.splits != null) {
            for (Map.Entry<String, DecisionTreeNode> entry : node.splits.entrySet()) {
                buildString(sb, entry.getValue(), depth + 1);
            }
        }
    }
}