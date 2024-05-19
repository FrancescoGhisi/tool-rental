package tool.rental.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class TableConfigurator {
    private final JTable table;

    public TableConfigurator(JTable table) {
        this.table = table;
    }

    private DefaultTableModel getTableModel() {
        return (DefaultTableModel) table.getModel();
    }

    private TableColumn getColumn(int index) {
        return table.getColumnModel().getColumn(index);
    }

    public void setup(String... columns) {
        setup(columns, new int[]{0});
    }

    public void setup(String[] columns, int[] hiddenColumns) {
        DefaultTableModel model = getTableModel();

        for (String column : columns) {
            model.addColumn(column);
        }

        for (int idx : hiddenColumns) {
            TableColumn columnModel = getColumn(idx);
            columnModel.setMinWidth(0);
            columnModel.setMaxWidth(0);
        }

        table.setDefaultEditor(Object.class, null);
    }

    public void insertRow(String[] row) {
        DefaultTableModel model = getTableModel();
        model.addRow(row);
    }

    public void insertRows(String[][] rows, boolean resetTable) {
        DefaultTableModel model = getTableModel();

        if (resetTable) {
            model.setNumRows(0);
        }

        for (String[] row : rows) {
            insertRow(row);
        }
    }
}
