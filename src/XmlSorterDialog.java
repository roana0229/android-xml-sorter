import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class XmlSorterDialog extends DialogWrapper {

    private static final String TITLE = "Xml Sort Option";
    private static final String OK_BUTTON_TEXT = "Sort";

    private JPanel mMainPanel;
    private JCheckBox mInsertSpaceCheckBox;
    private JComboBox mInputCaseBox;
    private JComboBox mPrefixSpacePositionBox;
    private JCheckBox mInsertXmlInfoCheckBox;
    private JCheckBox mDeleteCommentCheckBox;
    private JComboBox mCodeIndentBox;
    private JLabel mPrefixSpacePositionLabel;

    protected XmlSorterDialog(@Nullable Project project) {
        super(project, true);
        setTitle(TITLE);
        setOKButtonText(OK_BUTTON_TEXT);
        initComponent();
        init();
    }

    private void initComponent() {
        mInsertSpaceCheckBox.setSelected(true);
        // Snake Case
        mInputCaseBox.setSelectedIndex(0);
        mPrefixSpacePositionBox.setSelectedIndex(0);
        mInsertXmlInfoCheckBox.setSelected(true);
        mDeleteCommentCheckBox.setSelected(false);
        // indent 4
        mCodeIndentBox.setSelectedIndex(1);

        // TODO: CamelCase対応
        mInputCaseBox.setEnabled(false);

        mInsertSpaceCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // TODO: CamelCase対応
//                mInputCaseBox.setEnabled(mInsertSpaceCheckBox.isSelected());
                mPrefixSpacePositionLabel.setEnabled(mInsertSpaceCheckBox.isSelected());
                mPrefixSpacePositionBox.setEnabled(mInsertSpaceCheckBox.isSelected());
            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mMainPanel;
    }

    public boolean enableInsertSpace() {
        return mInsertSpaceCheckBox.isSelected();
    }

    public boolean isSnakeCase() {
        return mInputCaseBox.getSelectedIndex() == 0;
    }

    public int getPrefixSpacePosition() {
        return Integer.parseInt(mPrefixSpacePositionBox.getSelectedItem().toString());
    }

    public boolean enableInsertXmlInfo() {
        return mInsertXmlInfoCheckBox.isSelected();
    }

    public boolean enableDeleteComment() {
        return mDeleteCommentCheckBox.isSelected();
    }

    public int getCodeIndent() {
        return Integer.parseInt(mCodeIndentBox.getSelectedItem().toString());
    }

}
