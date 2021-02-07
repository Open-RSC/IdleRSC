package bot.debugger;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class DebuggerSectionJTable extends JTable {
    private String[] columnNames = null;
    private Class[] columnTypes = null;

    private DefaultTableModel model = null;

    private int previousSelectedIndex = -1;

    public DebuggerSectionJTable(String[] columnNames) {
        this.columnNames = columnNames;
        this.model = new DefaultTableModel(this.columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if(columnTypes != null && columnTypes[columnIndex] != null) {
                    return columnTypes[columnIndex];
                }

                return super.getColumnClass(columnIndex);
            }
        };

        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allow single row selected at a time
        this.setAutoCreateRowSorter(true); // Automatically create a table row sorter
        this.getTableHeader().setReorderingAllowed(false); // Disable reordering columns
        this.getTableHeader().setFont(this.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));
        this.setBorder(BorderFactory.createEmptyBorder());

        this.setModel(model);
    }

    public void addRow(Object[] row) {
        this.model.addRow(row);
    }

    public void addRowLater(Object[] row) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                addRow(row);
            }
        });
    }

    public void addRowAndWait(Object[] row) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                addRow(row);
            }
        });
    }

    public void removeAllRows() {
        this.previousSelectedIndex = this.getSelectedRow();

        this.model.getDataVector().removeAllElements();
        this.updateModel();
    }

    public void selectPreviousSelectedRow() {
        if(this.previousSelectedIndex > -1 && this.previousSelectedIndex < this.getRowCount()) {
            this.setRowSelectionInterval(this.previousSelectedIndex, this.previousSelectedIndex);
        }
    }

    public Class[] getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(Class[] columnTypes) {
        this.columnTypes = columnTypes;
        this.prepareColumnFormatting();
    }

    public void updateModel() {
        this.model.fireTableChanged(new TableModelEvent(this.model));
    }

    /**
     * Disable cell editing
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }


    @Override
    public Object getValueAt(int row, int column) {
        try {
            return super.getValueAt(row, column);
        } catch (Error e) {
            return null;
        }
    }

    private void prepareColumnFormatting() {
        if(columnTypes == null) {
            return;
        }

        TableColumnModel columnModel = this.getColumnModel();

        for(int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn tableColumn = columnModel.getColumn(i);
            Class columnType = columnTypes[i];

            if(columnType != null) {
                if(columnType == Integer.class) {
                    tableColumn.setHeaderRenderer(new NumberHeaderCellRenderer());
                    tableColumn.setCellRenderer(new NumberCellRenderer());
                }
            }
        }
    }
}
