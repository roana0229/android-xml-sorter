package org.roana0229.android_xml_sorter;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import static org.roana0229.android_xml_sorter.XmlSorterDialog.*;

public class XmlInstantSorterAction extends XmlSorterAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = getEventProject(event);
        XmlSorterDialog dialog = new XmlSorterDialog(project);

        PropertiesComponent pc = PropertiesComponent.getInstance();
        execute(event,
                pc.getInt(PC_KEY_INPUT_CASE, 0) == 0,
                dialog.getPrefixSpacePositionValueAt(pc.getInt(PC_KEY_PREFIX_SPACE_POS, 0)),
                pc.getBoolean(PC_KEY_SPACE_BETWEEN_PREFIX, true),
                pc.getBoolean(PC_KEY_INSERT_XML_INFO, true),
                pc.getBoolean(PC_KEY_DELETE_COMMENT, false),
                dialog.getCodeIndentValueAt(pc.getInt(PC_KEY_CODE_INDENT, 1)));
    }

}
