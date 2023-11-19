package bot;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

final class AuthFrame extends Frame {
  private static final Dimension fieldSize = new Dimension(120, 25);

  private final Window parent;

  private final TextField username;
  private final TextField password;

  private final Button okButton;

  AuthFrame(final String title, final String message, final Window parent) {
    super(title);

    this.parent = parent;

    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(final WindowEvent e) {
            close();
          }
        });

    Panel labelPanel = null;
    if (message != null) {
      labelPanel = new Panel();
      final String[] str = message.split("\n");
      labelPanel.setLayout(new GridLayout(str.length, 0));
      for (int i = 0; i < str.length; ++i) {
        labelPanel.add(new Label(str[i]));
      }
    }

    final Panel userPanel = new Panel();
    userPanel.add(new Label("Username:"));
    username = new TextField();
    username.setPreferredSize(fieldSize);
    userPanel.add(username);

    final Panel pwdPanel = new Panel();
    pwdPanel.add(new Label("Password:"));
    password = new TextField();
    password.setPreferredSize(fieldSize);
    password.setEchoChar('*');
    pwdPanel.add(password);

    final Panel inputPanel = new Panel();
    inputPanel.setLayout(new GridLayout(2, 1));
    inputPanel.add(userPanel);
    inputPanel.add(pwdPanel);

    final Panel buttonPanel = new Panel();

    okButton = new Button("OK");
    buttonPanel.add(okButton);

    final Button cancelButton = new Button("Cancel");
    cancelButton.addActionListener(e -> close());
    buttonPanel.add(cancelButton);

    if (labelPanel != null) {
      add(labelPanel, BorderLayout.NORTH);
    }

    add(inputPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);

    pack();
    setResizable(false);
  }

  private void close() {
    username.setText("");
    password.setText("");
    dispose();
  }

  synchronized String getUsername() {
    return username.getText();
  }

  synchronized String getPassword() {
    return password.getText();
  }

  synchronized void addActionListener(final ActionListener al) {
    okButton.addActionListener(al);
  }

  @Override
  public void setVisible(final boolean visible) {
    if (visible) {
      username.setText("");
      password.setText("");
      setLocationRelativeTo(parent);
      toFront();
      requestFocus();
    }
    super.setVisible(visible);
  }
}
