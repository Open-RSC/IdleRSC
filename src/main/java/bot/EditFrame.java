package bot;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Properties;
import javax.swing.*;

final class EditFrame extends Frame {
  private static final Dimension fieldSize = new Dimension(120, 25);
  private final Window parent;
  private final TextField username;
  private final TextField password;
  final Choice themeChoice = new Choice();
  private String account;
  private final Button okButton;

  EditFrame(final String title, final String message, final Window parent) {
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
    userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));

    userPanel.add(new Label("Username:"));
    username = new TextField();
    username.setPreferredSize(fieldSize);
    userPanel.add(username);

    // final Panel pwdPanel = new Panel();
    userPanel.add(new Label("Password:"));
    password = new TextField();
    password.setPreferredSize(fieldSize);
    password.setEchoChar('*');
    userPanel.add(password);

    //    userPanel.add(new Label("Theme Name:"));
    // themeName = new Choice();
    //    themeName.setPreferredSize(fieldSize);
    //    userPanel.add(themeName);

    userPanel.add(new Label("Theme Selector"));
    String[] themeNames = Main.getThemeNames();
    themeChoice.setPreferredSize(new Dimension(140, 15));
    for (final String themeName : themeNames) {
      themeChoice.add(themeName);
    }
    // themeChoice.addItemListener(event -> themeName = String.valueOf(event.getItem()));

    userPanel.add(themeChoice);
    userPanel.setMinimumSize(new Dimension(200, 300));

    final Panel inputPanel = new Panel();
    inputPanel.setLayout(new GridLayout(2, 1));
    inputPanel.add(userPanel);
    // inputPanel.add(pwdPanel);

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
    // setResizable(false);
  }

  private void close() {
    username.setText("");
    password.setText("");
    themeChoice.select(0);
    dispose();
  }

  synchronized String getUsername() {
    return username.getText();
  }

  synchronized String getPassword() {
    return password.getText();
  }

  synchronized String getThemeName() {
    return themeChoice.getSelectedItem();
  }

  synchronized void addActionListener(final ActionListener al) {
    okButton.addActionListener(al);
  }

  @Override
  public void setVisible(final boolean visible) {
    if (visible) {

      final Properties p = new Properties();
      final File file =
          Paths.get("accounts").resolve(EntryFrame.getAccount() + ".properties").toFile();
      try (final FileInputStream stream = new FileInputStream(file)) {
        p.load(stream);

        username.setText(p.getProperty("username", ""));
        password.setText(p.getProperty("password", ""));
        themeChoice.select(p.getProperty("theme", ""));
      } catch (final Throwable t) {
        System.out.println("Error loading account " + EntryFrame.getAccount() + ": " + t);
      }
      setLocationRelativeTo(parent);
      toFront();
      requestFocus();
    }
    super.setVisible(visible);
  }
}
