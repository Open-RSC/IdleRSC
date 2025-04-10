package bot.ui.components;

import java.awt.*;
import javax.swing.*;

public class ColorPickerPanel extends JPanel {
  private final JTextField hexField;
  private final JButton colorButton;
  private Color selectedColor = Color.BLACK;

  public ColorPickerPanel(String labelText, String tooltip, Color defaultColor) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    JLabel label = new JLabel(labelText);
    hexField = new JTextField(7);
    String defaultHex = colorToHex(defaultColor);

    hexField.setText(validateHex(defaultHex) ? defaultHex.toUpperCase() : "#FFFFFF");

    colorButton = createColorPickerButton();

    hexField.addActionListener(e -> updateColorFromHex());
    colorButton.addActionListener(e -> openColorPicker());

    add(label);

    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
    innerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    innerPanel.add(hexField);
    innerPanel.add(colorButton);

    if (tooltip != null && !tooltip.isEmpty()) setToolTipText(tooltip);
    add(innerPanel);
  }

  private JButton createColorPickerButton() {
    JButton colorButton = new JButton("\uD83C\uDFA8");
    colorButton.setFocusable(false);
    colorButton.setBorderPainted(false);
    colorButton.setMargin(new Insets(1, 2, 2, 2));
    colorButton.setBackground(Color.decode(hexField.getText()));
    colorButton.setToolTipText("Open the color picker");

    return colorButton;
  }

  private Color getContrastingColor(Color color) {
    double luminance =
        (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
    return (luminance > 0.5) ? Color.BLACK : Color.WHITE;
  }

  public static String colorToHex(Color color) {
    return String.format("#%06X", (0xFFFFFF & color.getRGB()));
  }

  private void updateColorFromHex() {
    String text = hexField.getText().trim();
    if (validateHex(text)) {
      selectedColor = Color.decode(text);
    } else {
      hexField.setText("#000000"); // Reset to default if invalid
    }
  }

  private void openColorPicker() {
    Color newColor = JColorChooser.showDialog(this, "Pick a Color", selectedColor);
    if (newColor != null) {
      selectedColor = newColor;
      hexField.setText(String.format("#%06X", (0xFFFFFF & newColor.getRGB())));
      colorButton.setBackground(selectedColor);

      System.out.println(selectedColor);
      System.out.println(getContrastingColor(selectedColor));
      colorButton.setForeground(getContrastingColor(selectedColor));
    }
  }

  public static boolean validateHex(String hex) {
    return hex.matches("^#([A-Fa-f0-9]{6})$");
  }

  public String getHexColor() {
    return hexField.getText();
  }

  public void setHexColor(String hex) {
    if (validateHex(hex)) {
      hexField.setText(hex.toUpperCase());
      selectedColor = Color.decode(hex);
      colorButton.setBackground(selectedColor);
      colorButton.setForeground(getContrastingColor(selectedColor));
    }
  }

  public Color getColor() {
    return selectedColor;
  }
}
