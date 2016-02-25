import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

    public static String prettyStringFromDocument(@NotNull Document document, int indent) throws IOException {
        OutputFormat outputFormat = new OutputFormat(document);
        outputFormat.setIndenting(true);
        outputFormat.setIndent(indent);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, outputFormat);
        serializer.serialize(document);
        return out.toString();
    }

    public static ArrayList<Node> getNodesAsList(@NotNull Document document) {
        ArrayList<Node> list = new ArrayList<Node>();
        final NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0, length = nodeList.getLength(); i < length; i++) {
            final Node node = nodeList.item(i);
            list.add(node);
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

    public static ArrayList<Node> deleteComment(@NotNull ArrayList<Node> nodeList) {
        ArrayList<Node> insertedList = new ArrayList<Node>();
        for (Node node : nodeList) {
            if (node.getNodeType() == Node.COMMENT_NODE) continue;
            insertedList.add(node);
        }
        return insertedList;
    }

    // TODO: 2番目移行のprefix対応
    public static ArrayList<Node> insertDiffPrefixSpace(@NotNull Document document, @NotNull ArrayList<Node> nodeList, int prefixPosition, boolean isSnakeCase) {
        ArrayList<Node> insertedList = new ArrayList<Node>();
        String beforePrefix = null;

        for (Node node : nodeList) {
            if (node.getNodeType() == Node.COMMENT_NODE) {
                insertedList.add(node);
                continue;
            }

            final String name = ((Element)node).getAttribute("name");
            String prefix;
            try {
                if (isSnakeCase) {
                    prefix = name.split("_")[prefixPosition];
                } else {
                    // TODO: CamelCase対応
                    prefix = null;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                prefix = null;
            }

            if (!TextUtils.isEmpty(beforePrefix) && !TextUtils.isEmpty(prefix) && !prefix.equals(beforePrefix)) {
                Element element = document.createElement("space");
                insertedList.add(element);
            }

            beforePrefix = prefix;
            insertedList.add(node);
        }
        return insertedList;
    }

}
