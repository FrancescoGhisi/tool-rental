package tool.rental.presentation;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import tool.rental.domain.controllers.FriendsRankController;
import tool.rental.utils.PresentationFrame;
import tool.rental.utils.TableConfigurator;
import tool.rental.utils.ToastError;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FriendsRankFrame extends PresentationFrame {
    private final FriendsRankController controller = new FriendsRankController(this);
    private JTable friendsTable;
    private JScrollPane JScrollPanel;
    private JPanel MainPanel;
    private final TableConfigurator tableConfigurator;


    public FriendsRankFrame() throws ToastError {
        this.tableConfigurator = new TableConfigurator(friendsTable);
        setupPageLayout();
        setMainPanel();
        setupTable();
    }

    private void setupPageLayout() {
        setTitle("Rank de amigos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(this.userScreen.widthFraction(40), this.userScreen.heightFraction(40));
        setLocationRelativeTo(null);
    }

    private void setMainPanel() {
        setContentPane(this.MainPanel);
    }

    public void setupTable() throws ToastError {
        String[] columns = {
                "Posição",
                "Nome",
                "Identidade",
                "Ferramentas atualmente emprestadas",
                "Total de ferramentas emprestadas"
        };
        tableConfigurator.setup(columns, null);
        this.loadData();
    }

    private void loadData() throws ToastError {
        List<String[]> summary = this.controller.getRentalSummary();
        tableConfigurator.insertRows(summary, true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        MainPanel = new JPanel();
        MainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(50, 25, 50, 25), -1, -1));
        MainPanel.setBackground(new Color(-14539224));
        MainPanel.setEnabled(true);
        JScrollPanel = new JScrollPane();
        JScrollPanel.setBackground(new Color(-14408668));
        MainPanel.add(JScrollPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        friendsTable = new JTable();
        friendsTable.setForeground(new Color(-15527649));
        friendsTable.setSelectionForeground(new Color(-4649));
        friendsTable.setShowVerticalLines(true);
        JScrollPanel.setViewportView(friendsTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }

}
