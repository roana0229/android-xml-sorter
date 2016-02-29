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

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class XmlSorterAction extends AnAction {

    private static final String ERROR_GROUP = "ErrorMessage";
    private static final String ERROR_TITLE = "Error";

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        event.getPresentation().setVisible(isXmlFile(file));
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = getEventProject(event);
        final Editor editor = event.getData(PlatformDataKeys.EDITOR);

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
        final String simpleContent = XmlSorterUtil.replaceAllByRegex(content, ">\n*\\s+?<", "><");

        // content convert document
        Document document;
        try {
            document = XmlSorterUtil.parseStringToDocument(simpleContent);
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification(ERROR_GROUP, ERROR_TITLE, e.getLocalizedMessage(), NotificationType.ERROR));
            return;
        }

        // sort
        ArrayList<Node> targetNodes = XmlSorterUtil.getNodesAsList(document);
        Collections.sort(targetNodes, new NodeComparator());
        XmlSorterUtil.removeChildNodes(document);

        // delete comment
        if (enableDeleteComment) {
            targetNodes = XmlSorterUtil.deleteComment(targetNodes);
        }

        // insert space
        if (enableInsertSpaceDiffPrefix) {
            targetNodes = XmlSorterUtil.insertDiffPrefixSpace(document, targetNodes, prefixSpacePosition, isSnakeCase);
        }

        // apply sort
        for (Node node : targetNodes) {
            document.getDocumentElement().appendChild(node);
        }

        // document convert content
        String printString;
        try {
            printString = XmlSorterUtil.prettyStringFromDocument(document, codeIndent);
        } catch (IOException e) {
            Notifications.Bus.notify(new Notification(ERROR_GROUP, ERROR_TITLE, e.getLocalizedMessage(), NotificationType.ERROR));
            return;
        }

        // write option
        if (!enableInsertXmlEncoding) {
            printString = XmlSorterUtil.replaceAllByRegex(printString, "<\\?xml.*\\?>\n", "");
        }
        if (enableInsertSpaceDiffPrefix) {
            printString = XmlSorterUtil.replaceAllByRegex(printString, "\n\\s+<space/>", "\n");
        }

        // write
        final String finalPrintString = printString;
        new WriteCommandAction.Simple(project) {
            @Override
            protected void run() throws Throwable {
                editor.getDocument().setText(finalPrintString);
            }
        }.execute();
    }

    private static boolean isXmlFile(@Nullable VirtualFile file) {
        return file != null && file.getName().endsWith(".xml");
    }

    private class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node node1, Node node2) {
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
