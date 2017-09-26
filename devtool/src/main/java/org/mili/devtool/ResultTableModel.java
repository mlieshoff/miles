package org.mili.devtool;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ResultTableModel extends AbstractTableModel {
    private String[] columnNames = {"Title"};
    private Object[][] data;
    private List<Entry> model = new ArrayList<>();

    public ResultTableModel(List<Entry> results) {
        refresh(results);
    }

    public void refresh(List<Entry> results) {
        model = results;
        data = new Object[model.size()][columnNames.length];
        int i = 0;
        for (Entry entry : model) {
            data[i][0] = entry.getTitle();
            i ++;
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public Entry getEntry(int index) {
        if (index >= model.size() || index < 0) {
            return null;
        }
        return model.get(index);
    }

}
