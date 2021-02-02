package bot.debugger;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class DebuggerSectionJTable extends JTable {
    private String[] columnNames = null;
    private DefaultTableModel model = null;

    public DebuggerSectionJTable(String[] columnNames) {
        this.columnNames = columnNames;
        this.model = new DefaultTableModel(this.columnNames, 0);

        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allow single row selected at a time
        this.setAutoCreateRowSorter(true); // Automatically create a table row sorter
        this.getTableHeader().setReorderingAllowed(false); // Disable reordering columns
        this.getTableHeader().setResizingAllowed(false); // Disable resizing columns
        this.getTableHeader().setFont(this.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));

        this.setModel(model);
    }

    public void addRow(String[] row) {
        this.model.addRow(row);
    }

    public void removeAllRows() {
        this.clearSelection();
        int rowCount = this.model.getRowCount();

        for(int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
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
}
