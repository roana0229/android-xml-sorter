import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

import java.util.ArrayList;

class CommentedNode {
    ArrayList<Node> comments;
    Node node;

    CommentedNode(@NotNull Node node, ArrayList<Node> comments) {
        this.comments = comments;
        this.node = node;
    }

    static class Comparator implements java.util.Comparator<CommentedNode> {
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
                final String node1Name = node1.getAttributes().getNamedItem("name").getTextContent();
                final String node2Name = node2.getAttributes().getNamedItem("name").getTextContent();
                return node1Name.compareTo(node2Name);
            }
        }
    }
}