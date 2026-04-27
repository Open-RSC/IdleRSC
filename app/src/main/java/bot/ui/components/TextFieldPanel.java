package bot.ui.components;

import bot.ui.components.models.TextFieldPanelType;
import java.awt.*;
import javax.swing.*;

public class TextFieldPanel extends JPanel {
  JLabel label;
  JTextField textField;
  boolean passwordVisible = false;

  public TextFieldPanel(
      String defaultText,
      String labelText,
      String tooltip,
      TextFieldPanelType type,
      Runnable buttonCallback) {
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

    switch (type) {
      case BUTTON:
        textField = new JTextField();
        inputPanel.add(textField);
        JButton btn = createStandardButton(buttonCallback);
        inputPanel.add(btn);

        break;
      case PASSWORD:
        textField = new JPasswordField();
        JButton viewPassword = createViewPasswordButton();
        inputPanel.add(textField);
        inputPanel.add(viewPassword);
        break;
      default:
        textField = new JTextField();
        inputPanel.add(textField);
    }

    add(inputPanel);
    if (defaultText != null && !defaultText.isEmpty()) textField.setText(defaultText);
    if (tooltip != null && !tooltip.isEmpty()) setToolTipText(tooltip);
  }

  public TextFieldPanel(String labelText, String tooltip) {
    this(null, labelText, tooltip, TextFieldPanelType.NORMAL, null);
  }

  private JButton createStandardButton(Runnable callback) {
    //noinspection UnnecessaryUnicodeEscape
    JButton btn = new JButton("\u2693");

    btn.addActionListener((e) -> callback.run());
    btn.setFocusable(false);
    btn.setBorderPainted(false);
    btn.setMargin(new Insets(1, 2, 1, 2));
    btn.setToolTipText("Set to current window position");
    return btn;
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

  @Override
  public void updateUI() {
    super.updateUI();

    setBackground(UIManager.getColor("Panel.background"));
    setForeground(UIManager.getColor("Panel.foreground"));

    if (label != null) {
      label.setForeground(UIManager.getColor("Label.foreground"));
      label.setBackground(UIManager.getColor("Label.background"));
    }

    if (textField != null) {
      textField.setBackground(UIManager.getColor("TextField.background"));
      textField.setForeground(UIManager.getColor("TextField.foreground"));
      textField.setCaretColor(UIManager.getColor("TextField.caretForeground"));
    }

    for (Component c : getComponents()) updateComponentColors(c);
  }

  private void updateComponentColors(Component c) {
    if (c instanceof JPanel) {
      c.setBackground(UIManager.getColor("Panel.background"));
      c.setForeground(UIManager.getColor("Panel.foreground"));
      for (Component child : ((JPanel) c).getComponents()) {
        updateComponentColors(child);
      }
    } else if (c instanceof JButton) {
      c.setBackground(UIManager.getColor("Button.background"));
      c.setForeground(UIManager.getColor("Button.foreground"));
    } else if (c instanceof JTextField) {
      c.setBackground(UIManager.getColor("TextField.background"));
      c.setForeground(UIManager.getColor("TextField.foreground"));
      ((JTextField) c).setCaretColor(UIManager.getColor("TextField.caretForeground"));
    } else if (c instanceof JLabel) {
      c.setForeground(UIManager.getColor("Label.foreground"));
      c.setBackground(UIManager.getColor("Label.background"));
    }
  }
}
