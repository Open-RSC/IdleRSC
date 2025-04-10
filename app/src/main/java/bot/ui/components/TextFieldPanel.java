package bot.ui.components;

import java.awt.*;
import javax.swing.*;

public class TextFieldPanel extends JPanel {
  JLabel label;
  JTextField textField;
  boolean passwordVisible = false;

  public TextFieldPanel(
      String defaultText, String labelText, String tooltip, boolean isPasswordField) {
    super();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    if (labelText != null && !labelText.isEmpty()) {
      label = new JLabel(labelText);
      label.setAlignmentX(Component.LEFT_ALIGNMENT);
      add(label);
    }

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
    inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    textField = isPasswordField ? new JPasswordField() : new JTextField();
    inputPanel.add(textField);

    if (isPasswordField) {
      JButton viewPassword = createViewPasswordButton();
      inputPanel.add(viewPassword);
    }

    add(inputPanel);
    if (defaultText != null && !defaultText.isEmpty()) textField.setText(defaultText);
    if (tooltip != null && !tooltip.isEmpty()) setToolTipText(tooltip);
  }

  public TextFieldPanel(String labelText, String tooltip, boolean isPasswordField) {
    this(null, labelText, tooltip, isPasswordField);
  }

  public TextFieldPanel(String labelText, String tooltip) {
    this(null, labelText, tooltip, false);
  }

  private JButton createViewPasswordButton() {
    JButton viewPassword = new JButton("\uD83D\uDC41");
    viewPassword.setFocusable(false);
    viewPassword.setBorderPainted(false);
    viewPassword.setMargin(new Insets(1, 2, 1, 2));
    viewPassword.setToolTipText("Show/Hide password");

    viewPassword.addActionListener(
        e -> {
          passwordVisible = !passwordVisible;
          ((JPasswordField) textField).setEchoChar(passwordVisible ? 0 : '*');
        });
    return viewPassword;
  }

  public JTextField getTextField() {
    return textField;
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

  public String getText() {
    return textField.getText();
  }

  public void setText(String text) {
    textField.setText(text);
  }
}
