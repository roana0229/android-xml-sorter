package org.roana0229.android_xml_sorter;

import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlSorterUtil {

    public static String replaceAllByRegex(@NotNull String content, @NotNull String regex, @NotNull String replaceString) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll(replaceString);
    }

    public static Document parseStringToDocument(@NotNull String content) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(content.getBytes("utf-8")));
        return document;
    }

    public static String prettyStringFromDocument(@NotNull Document document, int indent, boolean insertEncoding)
            throws IOException {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, insertEncoding ? "no" : "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
            DOMSource source = new DOMSource(document);
            Writer out = new StringWriter();
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
            return out.toString();
        } catch (TransformerConfigurationException e) {
            throw new IOException(e);
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }

    public static ArrayList<CommentedNode> getNodesAsList(@NotNull Document document) {
        ArrayList<CommentedNode> list = new ArrayList<CommentedNode>();
        final NodeList nodeList = document.getDocumentElement().getChildNodes();
        ArrayList<Node> comments = null;
        for (int i = 0, length = nodeList.getLength(); i < length; i++) {
            final Node node = nodeList.item(i);
            // Add comment and eat-comment tag to comments list
            if (node.getNodeType() == Node.COMMENT_NODE || "eat-comment".equals(node.getNodeName())) {
                if (comments == null) {
                    comments = new ArrayList<Node>();
                }
                comments.add(node);
            } else {
                list.add(new CommentedNode(node, comments));
                comments = null;
            }
        }
        return list;
    }

    public static void removeChildNodes(@NotNull Document document) {
        final NodeList nodeList = document.getDocumentElement().getChildNodes();
        while (true) {
            final Node node = nodeList.item(0);
            if (node == null) break;
            node.getParentNode().removeChild(node);
        }
    }

    public static ArrayList<CommentedNode> insertDiffPrefixSpace(@NotNull Document document,
                                                                 @NotNull ArrayList<CommentedNode> nodeList,
                                                                 int prefixPosition, boolean isSnakeCase) {
        ArrayList<CommentedNode> insertedList = new ArrayList<CommentedNode>();
        String beforePrefix = null;

        for (CommentedNode commentedNode : nodeList) {
            Node node = commentedNode.node;

            final String name = ((Element) node).getAttribute("name");
            String prefix;
            try {
                if (isSnakeCase) {
                    prefix = name.split("_")[prefixPosition - 1];
                } else {
                    Pattern p = Pattern.compile("[A-Z]");
                    prefix = p.split(name)[prefixPosition];
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                prefix = null;
            }

            if (nodeList.indexOf(commentedNode) > 0) {
                if (TextUtils.isEmpty(beforePrefix) || TextUtils.isEmpty(prefix) || !prefix.equals(beforePrefix)) {
                    Element element = document.createElement("space");
                    insertedList.add(new CommentedNode(element, null));
                }
            }

            beforePrefix = prefix;
            insertedList.add(commentedNode);
        }
        return insertedList;
    }

}
