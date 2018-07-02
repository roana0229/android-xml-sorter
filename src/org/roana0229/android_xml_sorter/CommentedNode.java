package org.roana0229.android_xml_sorter;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;

class CommentedNode {
    private ArrayList<Node> comments;
    private Node node;

    CommentedNode(@NotNull Node node, ArrayList<Node> comments) {
        this.comments = comments;
        this.node = node;
    }

    public ArrayList<Node> getComments() {
        return comments;
    }

    public Node getNode() {
        return node;
    }

    static class Comparator implements java.util.Comparator<CommentedNode> {
        private final boolean separateNonTranslatable;

        public Comparator(boolean separateNonTranslatable) {
            this.separateNonTranslatable = separateNonTranslatable;
        }

        @Override
        public int compare(CommentedNode commentedNode1, CommentedNode commentedNode2) {
            Node node1 = commentedNode1.node, node2 = commentedNode2.node;
            if (node1.getNodeType() == Node.COMMENT_NODE && node2.getNodeType() == Node.COMMENT_NODE) {
                return 0;
            } else if (node1.getNodeType() == Node.COMMENT_NODE && node2.getNodeType() != Node.COMMENT_NODE) {
                return -1;
            } else if (node1.getNodeType() != Node.COMMENT_NODE && node2.getNodeType() == Node.COMMENT_NODE) {
                return 1;
            } else {
                NamedNodeMap attributes1 = node1.getAttributes();
                NamedNodeMap attributes2 = node2.getAttributes();
                if (separateNonTranslatable) {
                    Node translatable1 = attributes1.getNamedItem("translatable");
                    Node translatable2 = attributes2.getNamedItem("translatable");
                    final boolean isNotTranslatable1 = translatable1 != null && "false".equals(translatable1.getTextContent());
                    final boolean isNotTranslatable2 = translatable2 != null && "false".equals(translatable2.getTextContent());
                    if (isNotTranslatable1 && !isNotTranslatable2) {
                        return 1;
                    } else if (!isNotTranslatable1 && isNotTranslatable2) {
                        return -1;
                    }
                }
                final String node1Name = attributes1.getNamedItem("name").getTextContent();
                final String node2Name = attributes2.getNamedItem("name").getTextContent();
                return node1Name.compareTo(node2Name);
            }
        }
    }
}