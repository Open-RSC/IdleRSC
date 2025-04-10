package bot.ui.components.table;

import java.awt.*;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class NumberCellRenderer extends DefaultTableCellRenderer {
  public NumberCellRenderer() {
    setHorizontalAlignment(JLabel.RIGHT);
  }

  @Override
  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if (value instanceof Number) {
      value = NumberFormat.getNumberInstance().format(value);
    }

    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }
}
