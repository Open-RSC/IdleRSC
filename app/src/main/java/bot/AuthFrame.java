package bot;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.swing.*;

final class AuthFrame extends JFrame {
  private Color backgroundColor = Main.getColorCode(1, 0);
  private Color textColor = Main.getColorCode(0, 0);
  private boolean loadSettings = false;
  private final String resourceLocation = "app/src/main/java/bot/res/";
  private static final Dimension fieldSize = new Dimension(100, 25);
  private final Window parent;
  private JPasswordField password;
  private final JTextField username,
      scriptName,
      scriptArgs,
      initCache,
      spellId,
      attackItems,
      strengthItems,
      defenseItems;
  private Checkbox autoLogin,
      sideBar,
      logWindow,
      debug,
      botPaint,
      disableGraphics,
      interlace,
      localOcr,
      helpMenu,
      showVersion;

  private final Choice themeChoice = new Choice();
  private final Button okButton;

  AuthFrame(final String title, final String message, final Window parent) {
    super(title);
    this.parent = parent;
    setIconImage(new ImageIcon(resourceLocation + "logos/idlersc.icon.png").getImage());

    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(final WindowEvent e) {
            setDefaultValues();
            dispose();
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
    final Panel optionsPanel2 = new Panel();
    optionsPanel2.setLayout(new BoxLayout(optionsPanel2, BoxLayout.Y_AXIS));

    // Build out all of our text field options
    String[] optionLabels = { // 12
      "Login Credentials:", // only show label
      "Username: ",
      "Password: ",
      "Startup Options: ", // only show label
      "Script Name: ",
      "Script Args: ",
      "Cache Version:", // todo change to choice menu soon
      "Hotkeys: ", // only show label
      "SpellId (F8): ",
      "Attack Item (F5): ",
      "Strength Item (F6): ",
      "Defense Item (F7):"
    };
    JTextField[] textFields = {
      new JTextField(),
      username = new JTextField(),
      password = new JPasswordField(),
      new JTextField(), // only show label
      scriptName = new JTextField(),
      scriptArgs = new JTextField(),
      initCache = new JTextField(),
      new JTextField(), // only show label
      spellId = new JTextField(),
      attackItems = new JTextField(),
      strengthItems = new JTextField(),
      defenseItems = new JTextField()
    };

    password.setEchoChar('*');
    Panel[] textFieldPanels = new Panel[optionLabels.length];
    for (int i = 0; i < textFieldPanels.length; i++) {
      textFieldPanels[i] = new Panel();
    }

    for (int i = 0; i < textFields.length; i++) {
      textFieldPanels[i].setLayout(new BoxLayout(textFieldPanels[i], BoxLayout.X_AXIS));
      Label optionLabel = new Label(optionLabels[i], Label.LEFT);
      optionLabel.setMaximumSize(new Dimension(100, 30));
      textFieldPanels[i].add(optionLabel);
      if (i == 0 || i == 3 || i == 7) {
        continue;
      }
      textFields[i].setMaximumSize(fieldSize);
      textFields[i].setBorder(
          BorderFactory.createMatteBorder(0, 0, 2, 2, new java.awt.Color(0, 0, 0, 255)));
      textFieldPanels[i].add(textFields[i]);
    }

    // Build out our choice selectors (comboboxes)
    String[] choiceLabels = {"Theme Selector:"};
    Choice[] choices = {themeChoice};

    Panel[] choicePanels = new Panel[choiceLabels.length];
    for (int i = 0; i < choicePanels.length; i++) {
      choicePanels[i] = new Panel();
    }

    for (int i = 0; i < choiceLabels.length; i++) {
      choicePanels[i].setLayout(new BoxLayout(choicePanels[i], BoxLayout.Y_AXIS));
      Label optionLabel2 = new Label(choiceLabels[i], Label.CENTER);
      optionLabel2.setMaximumSize(new Dimension(120, 30));
      choicePanels[i].add(optionLabel2);
      String[] themeNames = Main.getThemeNames();
      choices[i].setMaximumSize(new Dimension(140, 30));
      for (final String themeName : themeNames) {
        choices[i].add(themeName);
      }
      choicePanels[i].add(themeChoice);
    }

    //    HashMap<String, Integer> themeTypes = new HashMap<String, Integer>();
    //    int keyValue = 49;
    //    for (int i=0; i<themeNames.length; i++) {
    //      if(keyValue+i > 57) keyValue = (65-i);
    //      themeTypes.put(themeNames[i], keyValue+i);
    //    }

    // Build out all of our checkbox options
    Checkbox[] checkBoxes = {
      autoLogin = new Checkbox(" Auto-login", false),
      sideBar = new Checkbox(" Show Side Bar", true),
      logWindow = new Checkbox(" Open Log Window", false),
      debug = new Checkbox(" Open Debugger", false),
      botPaint = new Checkbox(" Show Bot Paint", true),
      disableGraphics = new Checkbox(" Disable Graphics", false),
      interlace = new Checkbox(" Interlace Mode", false),
      localOcr = new Checkbox(" Use Local-OCR", false),
      helpMenu = new Checkbox(" Show Help Menu", false),
      showVersion = new Checkbox(" Show Version", false)
    };

    Panel[] checkBoxPanels = new Panel[checkBoxes.length];
    for (int i = 0; i < checkBoxPanels.length; i++) {
      checkBoxPanels[i] = new Panel();
    }

    Dimension checkBoxDim = new Dimension(120, 30);
    for (int i = 0; i < checkBoxes.length; i++) {
      checkBoxPanels[i].setLayout(new BoxLayout(checkBoxPanels[i], BoxLayout.X_AXIS));
      checkBoxes[i].setMaximumSize(checkBoxDim);
      checkBoxPanels[i].add(checkBoxes[i]);
    }

    // Build out the order of the layout now
    for (Panel subPanel : textFieldPanels) {
      optionsPanel.add(subPanel);
    }
    optionsPanel2.add(
        new JLabel(
            new ImageIcon(resourceLocation + "logos/idlersc.icon.png", "Idlersc"), JLabel.LEFT));
    for (Panel subPanel : choicePanels) {
      optionsPanel2.add(subPanel);
    }
    for (Panel subPanel : checkBoxPanels) {
      optionsPanel2.add(subPanel);
    }

    // Build out the 2 main panels
    final JPanel inputPanel = new JPanel();
    final JPanel buttonPanel = new JPanel();
    okButton = new Button("Save");
    final Button cancelButton = new Button("Cancel");
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    // Generate gaps todo change to grid bag layout soon

    // Set Sizing
    Panel gapPanel = new Panel();
    gapPanel.setMaximumSize(new Dimension(25, 450));
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
    optionsPanel.setMinimumSize(new Dimension(250, 450));
    optionsPanel2.setMinimumSize(new Dimension(200, 450));

    inputPanel.add(optionsPanel);
    inputPanel.add(gapPanel);
    inputPanel.add(optionsPanel2);

    // colorize our elements
    optionsPanel.setBackground(backgroundColor);
    optionsPanel.setForeground(textColor);
    optionsPanel2.setBackground(backgroundColor);
    optionsPanel2.setForeground(textColor);
    inputPanel.setBackground(backgroundColor);
    buttonPanel.setBackground(backgroundColor.darker());
    buttonPanel.setForeground(textColor);
    okButton.setForeground(Color.BLACK);
    cancelButton.setForeground(Color.BLACK);

    // Add the 3 main panels to the frame
    if (labelPanel != null) {
      add(labelPanel, BorderLayout.NORTH);
    }
    inputPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 0, 4, backgroundColor.darker()));
    add(inputPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
    setMinimumSize(new Dimension(405, 450));

    // Add action listeners
    cancelButton.addActionListener(
        e -> {
          setDefaultValues();
          dispose();
        });

    pack();
    setResizable(false);
  }

  private void setDefaultValues() {
    username.setText("");
    password.setText("");
    scriptName.setText("");
    scriptArgs.setText("");
    initCache.setText("coleslaw");
    spellId.setText("");
    attackItems.setText("");
    strengthItems.setText("");
    defenseItems.setText("");
    themeChoice.select(0);
    autoLogin.setState(false);
    sideBar.setState(true);
    logWindow.setState(false);
    debug.setState(false);
    botPaint.setState(true);
    disableGraphics.setState(false);
    localOcr.setState(false);
    interlace.setState(false);
    helpMenu.setState(false);
    showVersion.setState(false);
  }

  public void storeAuthData(AuthFrame auth) {
    final Properties p = new Properties();
    final String u = auth.getUsername();
    Main.setUsername(u);

    p.put("username", u);
    p.put("password", auth.getPassword());
    p.put("script-name", auth.getScriptName());
    p.put("script-arguments", auth.getScriptArgs());
    p.put("init-cache", auth.getInitCache());
    p.put("spell-id", auth.getSpellId());
    p.put("attack-items", auth.getAttackItems());
    p.put("strength-items", auth.getStrengthItems());
    p.put("defence-items", auth.getDefenseItems());
    p.put("auto-login", auth.getAutoLogin());
    p.put("sidebar", auth.getSideBar());
    p.put("log-window", auth.getLogWindow());
    p.put("debug", auth.getDebugger());
    p.put("botpaint", auth.getBotPaint()); // true disables bot paint
    p.put("disable-gfx", auth.getDisableGraphics());
    p.put("interlace", auth.getInterlace());
    p.put("local-ocr", auth.getLocalOcr());
    p.put("help", auth.getHelpMenu());
    p.put("version", auth.getShowVersion());
    p.put("theme", auth.getThemeName());

    // Make sure our accounts folder exists
    Path accountPath = Paths.get("accounts");
    try {
      Files.createDirectories(accountPath);
      // Files.createFile(Paths.get("accounts", u + ".properties"));
    } catch (IOException e2) {
      System.err.println("Failed to create directory: " + e2.getMessage());
      e2.printStackTrace();
      return;
    }

    // Now we can parse it
    final File file = accountPath.resolve(u + ".properties").toFile();
    try (final FileOutputStream out = new FileOutputStream(file)) {
      p.store(out, null);
    } catch (final Throwable t) {
      System.out.println("Error saving account details: " + t);
    }
  }

  @Override
  public void setVisible(final boolean visible) {
    if (visible) {
      setIconImage(new ImageIcon(resourceLocation + "logos/idlersc.icon.png").getImage());
      if (loadSettings) {
        // Make sure our accounts folder exists
        final Properties p = new Properties();
        Path accountPath = Paths.get("accounts");
        try {
          Files.createDirectories(accountPath);
        } catch (IOException e2) {
          System.err.println("Failed to create directory: " + e2.getMessage());
          e2.printStackTrace();
          return;
        }
        // Now we can parse it
        String account = EntryFrame.getAccount();
        if (account == null) account = Main.config.getUsername();
        final File file = accountPath.resolve(account + ".properties").toFile();
        try (final FileInputStream stream = new FileInputStream(file)) {
          p.load(stream);
          // ALWAYS make properties lowercase
          username.setText(p.getProperty("username", ""));
          if (!username.getText().isEmpty()) username.setEditable(false);
          password.setText(p.getProperty("password", ""));
          scriptName.setText(p.getProperty("script-name", ""));
          scriptArgs.setText(p.getProperty("script-arguments", ""));
          initCache.setText(p.getProperty("init-cache", ""));
          spellId.setText(p.getProperty("spell-id", "-1"));
          attackItems.setText(p.getProperty("attack-items", ""));
          strengthItems.setText(p.getProperty("defence-items", ""));
          defenseItems.setText(p.getProperty("strength-items", ""));
          autoLogin.setState(Boolean.parseBoolean(p.getProperty("auto-login", "false")));
          sideBar.setState(Boolean.parseBoolean(p.getProperty("sidebar", "false")));
          logWindow.setState(Boolean.parseBoolean(p.getProperty("log-window", "false")));
          debug.setState(Boolean.parseBoolean(p.getProperty("debug", "false")));
          botPaint.setState(Boolean.parseBoolean(p.getProperty("bot-paint", "true")));
          disableGraphics.setState(Boolean.parseBoolean(p.getProperty("disable-gfx", "false")));
          interlace.setState(Boolean.parseBoolean(p.getProperty("interlace", "false")));
          helpMenu.setState(Boolean.parseBoolean(p.getProperty("help", "false")));
          showVersion.setState(Boolean.parseBoolean(p.getProperty("version", "false")));
          themeChoice.select(p.getProperty("theme", "RuneDark Theme"));

        } catch (final Throwable t) {
          System.out.println("Error loading account " + account + ": " + t);
        }
      } else {
        setDefaultValues();
      }
      setLocationRelativeTo(parent);
      toFront();
      requestFocus();
    }
    super.setVisible(visible);
  }

  public void setLoadSettings(boolean set) {
    loadSettings = set;
  }

  public boolean getLoadSettings() {
    return loadSettings;
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

  synchronized String getSpellId() {
    return spellId.getText();
  }

  synchronized String getAttackItems() {
    return attackItems.getText();
  }

  synchronized String getStrengthItems() {
    return strengthItems.getText();
  }

  synchronized String getDefenseItems() {
    return defenseItems.getText();
  }

  synchronized String getAutoLogin() {
    return Boolean.toString(autoLogin.getState());
  }

  synchronized String getLogWindow() {
    return Boolean.toString(logWindow.getState());
  }

  synchronized String getSideBar() {
    return Boolean.toString(sideBar.getState());
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
}
