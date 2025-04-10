package bot.ui.components;

import java.awt.event.ActionListener;
import javax.swing.*;

public class ComboBoxPanel extends JPanel {
  JLabel label;
  JComboBox<String> comboBox;

  public ComboBoxPanel(String labelText, String tooltip) {
    super();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    label = new JLabel(labelText);
    comboBox = new JComboBox<>();

    // Ensure alignment to the left
    label.setAlignmentX(LEFT_ALIGNMENT);
    comboBox.setAlignmentX(LEFT_ALIGNMENT);

    add(label);
    add(comboBox);
    if (tooltip != null && !tooltip.isEmpty()) setToolTipText(tooltip);
  }

  public void addItem(String item) {
    comboBox.addItem(item);
  }

  public void addActionListener(ActionListener actionListener) {
    comboBox.addActionListener(actionListener);
  }

  public String getSelectedItem() {
    return (String) comboBox.getSelectedItem();
  }

  public void setSelectedItem(String item) {
    comboBox.setSelectedItem(item);
  }

  public void setSelectedIndex(int index) {
    comboBox.setSelectedIndex(index);
  }

  public JComboBox<String> getComboBox() {
    return comboBox;
  }

  public JLabel getLabel() {
    return label;
  }

  public String getLabelText() {
    return label.getText();
  }

  public void setLabelText(String text) {
    label.setText(text);
  }
}
