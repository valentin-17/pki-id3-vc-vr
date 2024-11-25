package de.uni_trier.wi2.pki.tree;

import java.util.*;

/**
 * Class for representing a node in the decision tree.
 */
public class DecisionTreeNode {



    private Collection<Object[]> elements;

    /**
     * The parent node in the decision tree.
     */
    protected DecisionTreeNode parent;

    /**
     * The attribute index to check.
     */
    protected int attributeIndex;

    /**
     * The checked split condition values and the nodes for these conditions.
     */
    HashMap<String, DecisionTreeNode> splits;

    /**
     * Constructor for decision tree node.
     *
     * @param parent         Parent node of current node.
     * @param elements       Elements for current node.
     * @param attributeIndex Index for current attribute.
     */

    public DecisionTreeNode(DecisionTreeNode parent, Collection<Object[]> elements, int attributeIndex) {
        this.parent = parent;
        this.attributeIndex = attributeIndex;
        this.elements = elements;
        splits = new HashMap<>();
    }

    /**
     * Adds a split to the current node.
     *
     * @param Object     CSV attribute for which a split is made.
     * @param decisionTreeNode Child decision tree node.
     */
    public void addSplit(String Object, DecisionTreeNode decisionTreeNode) {
        decisionTreeNode.parent = this;
        splits.put(Object, decisionTreeNode);
    }

    /**
     * Returns the leaf node used for classification.
     *
     * @param example the attributes of a single example.
     * @return the leaf node.
     */
    protected DecisionTreeNode getClassificationNode(Object[] example) {
        DecisionTreeNode currentNode = this;

        while (currentNode != null) {

            if (currentNode.isLeafNode()) {
                return currentNode;
            }
            int attributeIndex = currentNode.getAttributeIndex();
            if (attributeIndex < 0 || attributeIndex >= example.length) {
                throw new IllegalArgumentException("Invalid attribute index: " + attributeIndex);
            }

            String attributeValue = (String) example[attributeIndex];
            currentNode = currentNode.getSplits().get(attributeValue);

        }
        return null;
    }

    /**
     * Returns the index of the attribute used in the current node.
     *
     * @return Index of the attribute used in the current node.
     */
    public int getAttributeIndex() {
        return attributeIndex;
    }

    /**
     * Returns parent node of current node.
     *
     * @return Parent node of current node.
     */
    public DecisionTreeNode getParent() {
        return parent;
    }

    /**
     * Returns the checked split condition values and the nodes for these conditions.
     *
     * @return The checked split condition values and the nodes for these conditions.
     */
    public HashMap<String, DecisionTreeNode> getSplits() {
        return splits;
    }

    /**
     * Returns elements of current node.
     *
     * @return Elements of current node.
     */

    public Collection<Object[]> getElements() {
        return elements;
    }

    /**
     * Checks if current node is a leaf node.
     *
     * @return True, if leaf node.
     */
    public boolean isLeafNode() {
        return false;
    }
}
