package bot;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.swing.*;

final class AuthFrame extends Frame {
  public boolean loadAccountSettings = false;
  private static final Dimension fieldSize = new Dimension(125, 25);
  private final Window parent;
  private final TextField username,
      password,
      scriptName,
      scriptArgs,
      initCache; // , spellId, attackItem, strengthItem, defenseItem;
  private Checkbox autoLogin,
      logWindow,
      debug,
      botPaint,
      disableGraphics,
      interlace,
      localOcr,
      helpMenu,
      showVersion;

  final Choice themeChoice = new Choice();
  private String account;
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
      for (String s : str) {
        labelPanel.add(new Label(s));
      }
    }
    final Panel optionsPanel = new Panel();
    optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

    // Build out all of our text field options
    String[] optionLabels = {
      "Login Credentials:", // only show label
      "Username: ",
      "Password: ",
      "Startup Options: ", // only show label
      "Script Name: ",
      "Script Args: ",
      "Cache Version"
    };
    TextField[] textFields = {
      new TextField(),
      username = new TextField(),
      password = new TextField(),
      new TextField(),
      scriptName = new TextField(),
      scriptArgs = new TextField(),
      initCache = new TextField()
    };
    Panel[] subPanels = new Panel[20];

    for (int i = 0; i < subPanels.length; i++) {
      subPanels[i] = new Panel();
    }

    int index = 0;
    password.setEchoChar('*');

    for (int i = 0; i < textFields.length; i++) {
      subPanels[index].setLayout(new BoxLayout(subPanels[index], BoxLayout.X_AXIS));
      Label optionLabel = new Label(optionLabels[i], Label.LEFT);
      optionLabel.setMaximumSize(new Dimension(100, 30));
      subPanels[index].add(optionLabel);
      if (index == 0 || index == 3) {
        index++;
        continue;
      }
      textFields[i].setMaximumSize(fieldSize);
      subPanels[index].add(textFields[i]);
      index++;
    }

    subPanels[index].setLayout(new BoxLayout(subPanels[index], BoxLayout.X_AXIS));
    Label optionLabel2 = new Label("Theme Selector", Label.LEFT);
    optionLabel2.setMaximumSize(new Dimension(85, 30));
    subPanels[index].add(optionLabel2);
    String[] themeNames = Main.getThemeNames();
    themeChoice.setMaximumSize(new Dimension(140, 25));
    for (final String themeName : themeNames) {
      themeChoice.add(themeName);
    }
    subPanels[index].add(themeChoice);
    index++;

    // Build out all of our checkbox options
    String[] checkboxLabels = {
      "Auto-login:",
      "Log Window: ",
      "Open Debugger: ",
      "Show Bot Paint: ",
      "Disable Graphics",
      "Interlace Mode",
      "Local-OCR",
      "Help Menu",
      "Show Version"
    };
    Checkbox[] checkBoxes = {
      autoLogin = new Checkbox("Toggle Autolog", false),
      logWindow = new Checkbox("Show Log Window", false),
      debug = new Checkbox("Show", false),
      botPaint = new Checkbox("Show Bot Paint", true),
      disableGraphics = new Checkbox("Disable Graphics", false),
      interlace = new Checkbox("Use Interlace", false),
      localOcr = new Checkbox("Use Local OCR", false),
      helpMenu = new Checkbox("Show Help Menu", false),
      showVersion = new Checkbox("Show Version", false)
    };

    for (int i = 0; i < checkBoxes.length; i++) {
      subPanels[index].setLayout(new BoxLayout(subPanels[index], BoxLayout.X_AXIS));
      Label optionLabel = new Label(checkboxLabels[i], Label.LEFT);
      optionLabel.setMaximumSize(new Dimension(100, 30));
      subPanels[index].add(optionLabel);
      checkBoxes[i].setMaximumSize(fieldSize);
      subPanels[index].add(checkBoxes[i]);
      index++;
    }

    // Build out the order of the layout now
    for (Panel subPanel : subPanels) {
      optionsPanel.add(subPanel);
    }

    final Panel inputPanel = new Panel();
    final Panel buttonPanel = new Panel();
    final Button cancelButton = new Button("Cancel");
    okButton = new Button("OK");

    inputPanel.setLayout(new BorderLayout());
    inputPanel.add(optionsPanel, BorderLayout.CENTER);
    buttonPanel.add(okButton);

    cancelButton.addActionListener(e -> close());
    buttonPanel.add(cancelButton);

    if (labelPanel != null) {
      add(labelPanel, BorderLayout.NORTH);
    }

    add(inputPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
    setMinimumSize(new Dimension(300, 300));

    pack();
    setResizable(false);
  }

  private void close() {
    username.setText("");
    password.setText("");
    scriptName.setText("");
    scriptArgs.setText("");
    initCache.setText("coleslaw");
    autoLogin.setState(false);
    logWindow.setState(false);
    debug.setState(false);
    botPaint.setState(true);
    disableGraphics.setState(false);
    interlace.setState(false);
    helpMenu.setState(false);
    showVersion.setState(false);
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

  synchronized String getScriptName() {
    return scriptName.getText();
  }

  synchronized String getScriptArgs() {
    return scriptArgs.getText();
  }

  synchronized String getInitCache() {
    return initCache.getText();
  }

  synchronized String getAutoLogin() {
    return Boolean.toString(autoLogin.getState());
  }

  synchronized String getLogWindow() {
    return Boolean.toString(logWindow.getState());
  }

  synchronized String getDebugger() {
    return Boolean.toString(debug.getState());
  }

  synchronized String getBotPaint() {
    return Boolean.toString(botPaint.getState());
  }

  synchronized String getDisableGraphics() {
    return Boolean.toString(disableGraphics.getState());
  }

  synchronized String getInterlace() {
    return Boolean.toString(interlace.getState());
  }

  synchronized String getLocalOcr() {
    return Boolean.toString(localOcr.getState());
  }

  synchronized String getHelpMenu() {
    return Boolean.toString(helpMenu.getState());
  }

  synchronized String getShowVersion() {
    return Boolean.toString(showVersion.getState());
  }

  synchronized void addActionListener(final ActionListener al) {
    okButton.addActionListener(al);
  }

  @Override
  public void setVisible(final boolean visible) {
    if (visible) {
      if (loadAccountSettings) {
        //Make sure our accounts folder exists
        final Properties p = new Properties();
        Path accountPath = Paths.get("accounts");
        try {
          Files.createDirectories(accountPath);
        } catch (IOException e2) {
          System.err.println("Failed to create directory: " + e2.getMessage());
          e2.printStackTrace();
          return;
        }

        //Now we can parse it
        final File file =
          accountPath.resolve(EntryFrame.getAccount() + ".properties").toFile();
        try (final FileInputStream stream = new FileInputStream(file)) {
          p.load(stream);

          username.setText(p.getProperty("username", ""));
          password.setText(p.getProperty("password", ""));
          scriptName.setText(p.getProperty("script-name", ""));
          scriptArgs.setText(p.getProperty("script-arguments", ""));
          initCache.setText(p.getProperty("init-cache", ""));
          autoLogin.setState(Boolean.parseBoolean(p.getProperty("auto-login", "false")));
          logWindow.setState(Boolean.parseBoolean(p.getProperty("log-window", "false")));
          debug.setState(Boolean.parseBoolean(p.getProperty("debug", "false")));
          botPaint.setState(Boolean.parseBoolean(p.getProperty("botpaint", "true")));
          disableGraphics.setState(Boolean.parseBoolean(p.getProperty("disable-gfx", "false")));
          interlace.setState(Boolean.parseBoolean(p.getProperty("interlace", "false")));
          helpMenu.setState(Boolean.parseBoolean(p.getProperty("help", "false")));
          showVersion.setState(Boolean.parseBoolean(p.getProperty("version", "false")));
          themeChoice.select(p.getProperty("theme", "RuneDark Theme"));

        } catch (final Throwable t) {
          System.out.println("Error loading account " + EntryFrame.getAccount() + ": " + t);
        }
      } else {
        themeChoice.select(0);
        username.setText("");
        password.setText("");
        scriptName.setText("");
        scriptArgs.setText("");
        initCache.setText("coleslaw");
        autoLogin.setState(false);
        logWindow.setState(false);
        debug.setState(false);
        botPaint.setState(true);
        disableGraphics.setState(false);
        interlace.setState(false);
        helpMenu.setState(false);
        showVersion.setState(false);
        themeChoice.select("RuneDark Theme");
      }
      setLocationRelativeTo(parent);
      toFront();
      requestFocus();
    }
    super.setVisible(visible);
  }
}
