import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class XmlSorterDialog extends DialogWrapper {

    private static final String TITLE = "Xml Sort Option";
    private static final String OK_BUTTON_TEXT = "Sort";

    private JPanel mMainPanel;
    private JCheckBox mInsertSpaceCheckBox;
    private JComboBox mInputCaseBox;
    private JCheckBox mInsertXmlInfoCheckBox;
    private JCheckBox mDeleteCommentCheckBox;
    private JComboBox mCodeIndentBox;

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
        mInsertXmlInfoCheckBox.setSelected(true);
        mDeleteCommentCheckBox.setSelected(false);
        // indent 4
        mCodeIndentBox.setSelectedIndex(1);

        // TODO: CamelCase対応
        mInputCaseBox.setVisible(false);
//        mInputCaseBox.setEnabled(true);
//        mInsertSpaceCheckBox.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                mInputCaseBox.setEnabled(mInsertSpaceCheckBox.isSelected());
//            }
//        });
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
