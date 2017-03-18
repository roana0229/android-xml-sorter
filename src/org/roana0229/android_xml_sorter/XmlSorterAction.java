package org.roana0229.android_xml_sorter;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class XmlSorterAction extends AnAction {

    private static final String ERROR_GROUP = "ErrorMessage";
    private static final String ERROR_TITLE = "Error";

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        event.getPresentation().setEnabledAndVisible(isResourceXmlFile(file));
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = getEventProject(event);
        final Editor editor = event.getData(PlatformDataKeys.EDITOR);
        final String lineSeparator = System.getProperty("line.separator");

        XmlSorterDialog dialog = new XmlSorterDialog(project);
        if (!dialog.showAndGet()) {
            return;
        }

        // get options
        boolean enableInsertSpaceDiffPrefix = dialog.enableInsertSpace();
        boolean isSnakeCase = true;
        int prefixSpacePosition = 0;
        if (enableInsertSpaceDiffPrefix) {
            isSnakeCase = dialog.isSnakeCase();
            prefixSpacePosition = dialog.getPrefixSpacePosition();
        }
        boolean enableInsertXmlEncoding = dialog.enableInsertXmlInfo();
        boolean enableDeleteComment = dialog.enableDeleteComment();
        int codeIndent = dialog.getCodeIndent();

        // get content
        final String content = editor.getDocument().getText();
        final String simpleContent = XmlSorterUtil.replaceAllByRegex(content, ">" + lineSeparator + "*\\s+?<", "><");

        // content convert document
        Document document;
        try {
            document = XmlSorterUtil.parseStringToDocument(simpleContent);
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification(ERROR_GROUP, ERROR_TITLE, e.getLocalizedMessage(), NotificationType.ERROR));
            return;
        }

        // sort
        ArrayList<CommentedNode> targetNodes = XmlSorterUtil.getNodesAsList(document);
        Collections.sort(targetNodes, new CommentedNode.Comparator());
        XmlSorterUtil.removeChildNodes(document);

        // insert space
        if (enableInsertSpaceDiffPrefix) {
            targetNodes = XmlSorterUtil.insertDiffPrefixSpace(document, targetNodes, prefixSpacePosition, isSnakeCase);
        }

        // apply sort
        for (CommentedNode node : targetNodes) {

            // don't write comment if `enableDeleteComment` is true
            if (!enableDeleteComment) {
                ArrayList<Node> comments = node.comments;
                if (comments != null) {
                    for (Node comment : comments) {
                        document.getDocumentElement().appendChild(comment);
                    }
                }
            }
            document.getDocumentElement().appendChild(node.node);
        }

        // document convert content
        String printString;
        try {
            printString = XmlSorterUtil.prettyStringFromDocument(document, codeIndent, enableInsertXmlEncoding);
        } catch (IOException e) {
            Notifications.Bus.notify(new Notification(ERROR_GROUP, ERROR_TITLE, e.getLocalizedMessage(), NotificationType.ERROR));
            return;
        }

        // write option
        if (enableInsertSpaceDiffPrefix) {
            printString = XmlSorterUtil.replaceAllByRegex(printString, "(" + lineSeparator + ")\\s+<space/>", "$1");
        }

        // eliminate line breaks before/after xliff declaration
        printString = XmlSorterUtil.replaceAllByRegex(printString,  lineSeparator + "\\s+<xliff:", "<xliff:");
        printString = XmlSorterUtil.replaceAllByRegex(printString, "(</xliff:\\w+>)" + lineSeparator + "\\s+", "$1");

        // write
        final String finalPrintString = lineSeparator.equals("\n") ? printString : printString.replace(lineSeparator, "\n");
        new WriteCommandAction.Simple(project) {
            @Override
            protected void run() throws Throwable {
                editor.getDocument().setText(finalPrintString);
            }
        }.execute();
    }

    private static boolean isResourceXmlFile(@Nullable VirtualFile file) {
        if (file == null || !file.getName().endsWith(".xml")) return false;
        XMLStreamReader xml = null;
        try {
            xml = XMLInputFactory.newInstance().createXMLStreamReader(file.getInputStream());
            while (xml.hasNext()) {
                if (xml.next() == XMLStreamConstants.START_ELEMENT) {
                    return "resources".equals(xml.getLocalName());
                }
            }
        } catch (XMLStreamException | IOException e) {
            return false;
        } finally {
            if (xml != null) {
                try {
                    xml.close();
                } catch (XMLStreamException e) {
                    // Ignore
                }
            }
        }
        return false;
    }

}
