package org.roana0229.android_xml_sorter;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class XmlSorterDialog extends DialogWrapper
{

    private static final String TITLE          = "Xml Sort Option";
    private static final String OK_BUTTON_TEXT = "Sort";

    private static final String PC_KEY_PREFIX_SPACE_POS     = "PC_KEY_PREFIX_SPACE_POS";
    private static final String PC_KEY_SPACE_BETWEEN_PREFIX = "PC_KEY_SPACE_BETWEEN_PREFIX";
    private static final String PC_KEY_INSERT_XML_INFO      = "PC_KEY_INSERT_XML_INFO";
    private static final String PC_KEY_DELETE_COMMENT       = "PC_KEY_DELETE_COMMENT";
    private static final String PC_KEY_INPUT_CASE           = "PC_KEY_INPUT_CASE";
    private static final String PC_KEY_CODE_INDENT          = "PC_KEY_CODE_INDENT";

    private JPanel    mMainPanel;
    private JCheckBox mInsertSpaceCheckBox;
    private JComboBox mInputCaseBox;
    private JComboBox mPrefixSpacePositionBox;
    private JCheckBox mInsertXmlInfoCheckBox;
    private JCheckBox mDeleteCommentCheckBox;
    private JComboBox mCodeIndentBox;
    private JLabel    mPrefixSpacePositionLabel;

    protected XmlSorterDialog(@Nullable Project project)
    {
        super(project, true);
        setTitle(TITLE);
        setOKButtonText(OK_BUTTON_TEXT);
        initComponent();
        init();
    }

    private void initComponent()
    {
        PropertiesComponent pc = PropertiesComponent.getInstance();
        mInsertSpaceCheckBox.setSelected(pc.getBoolean(PC_KEY_SPACE_BETWEEN_PREFIX, true));
        // Snake Case
        mInputCaseBox.setSelectedIndex(pc.getInt(PC_KEY_INPUT_CASE, 0));
        mPrefixSpacePositionBox.setSelectedIndex(pc.getInt(PC_KEY_PREFIX_SPACE_POS, 0));
        mInsertXmlInfoCheckBox.setSelected(pc.getBoolean(PC_KEY_INSERT_XML_INFO, true));
        mDeleteCommentCheckBox.setSelected(pc.getBoolean(PC_KEY_DELETE_COMMENT, false));
        // indent 4
        mCodeIndentBox.setSelectedIndex(pc.getInt(PC_KEY_CODE_INDENT, 1));

        ActionListener actionListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                mInputCaseBox.setEnabled(mInsertSpaceCheckBox.isSelected());
                mPrefixSpacePositionLabel.setEnabled(mInsertSpaceCheckBox.isSelected());
                mPrefixSpacePositionBox.setEnabled(mInsertSpaceCheckBox.isSelected());
            }
        };
        mInsertSpaceCheckBox.addActionListener(actionListener);
        // initial invoke
        actionListener.actionPerformed(null);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel()
    {
        return mMainPanel;
    }

    public boolean enableInsertSpace()
    {
        return mInsertSpaceCheckBox.isSelected();
    }

    public boolean isSnakeCase()
    {
        return mInputCaseBox.getSelectedIndex() == 0;
    }

    public int getPrefixSpacePosition()
    {
        return Integer.parseInt(mPrefixSpacePositionBox.getSelectedItem().toString());
    }

    public boolean enableInsertXmlInfo()
    {
        return mInsertXmlInfoCheckBox.isSelected();
    }

    public boolean enableDeleteComment()
    {
        return mDeleteCommentCheckBox.isSelected();
    }

    public int getCodeIndent()
    {
        return Integer.parseInt(mCodeIndentBox.getSelectedItem().toString());
    }

    @Override
    protected void doOKAction()
    {
        save();
        super.doOKAction();
    }

    public void save()
    {
        PropertiesComponent pc = PropertiesComponent.getInstance();
        pc.setValue(PC_KEY_SPACE_BETWEEN_PREFIX, mInsertSpaceCheckBox.isSelected(), true);
        pc.setValue(PC_KEY_INPUT_CASE, mInputCaseBox.getSelectedIndex(), 0);
        pc.setValue(PC_KEY_PREFIX_SPACE_POS, mPrefixSpacePositionBox.getSelectedIndex(), 0);
        pc.setValue(PC_KEY_INSERT_XML_INFO, mInsertXmlInfoCheckBox.isSelected(), true);
        pc.setValue(PC_KEY_DELETE_COMMENT, mDeleteCommentCheckBox.isSelected(), false);
        pc.setValue(PC_KEY_CODE_INDENT, mCodeIndentBox.getSelectedIndex(), 1);
    }

}
