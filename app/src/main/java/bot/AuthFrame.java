package bot;

import bot.ocrlib.OCRType;
import bot.ui.Theme;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import javax.swing.*;
import orsc.util.Utils;

final class AuthFrame extends JFrame {

  private Theme theme = Theme.RUNEDARK;
  private boolean loadSettings = false;
  private final String resourceLocation =
      "app"
          + File.separator
          + "src"
          + File.separator
          + "main"
          + File.separator
          + "java"
          + File.separator
          + "bot"
          + File.separator
          + "res"
          + File.separator
          + "";
  private final Window parent;
  private JPasswordField password;
  private final JTextField username;
  private final JTextField scriptName;
  private final JTextField scriptArgs;
  private final JTextField spellId;
  private final JTextField attackItems;
  private final JTextField strengthItems;
  private final JTextField defenseItems;
  private final JTextField positionX;
  private final JTextField positionY;
  private JTextField ocrServer = null;
  private Checkbox autoLogin,
      sideBar,
      logWindow,
      debug,
      botPaint,
      disableGraphics,
      interlace,
      screenRefresh,
      helpMenu,
      showVersion,
      newIcons,
      newUi,
      keepOpen;

  private final Choice themeChoice = new Choice();
  private final Choice initChoice = new Choice();
  private final Choice serverIpChoice = new Choice();
  private final Choice ocrChoice = new Choice();
  private final Button okButton;
  private final Panel optionsPanel = new Panel();
  private final Panel optionsPanel2 = new Panel();
  final JPanel inputPanel = new JPanel();
  final JPanel buttonPanel = new JPanel();
  Button cancelButton = new Button("Cancel");

  final JTextField[] textFields = {
    new JTextField(),
    username = new JTextField(),
    password = new JPasswordField(),
    new JTextField(), // only show label
    scriptName = new JTextField(),
    scriptArgs = new JTextField(),
    new JTextField(), // only show label
    positionX = new JTextField(),
    positionY = new JTextField(),
    new JTextField(), // only show label
    spellId = new JTextField(),
    attackItems = new JTextField(),
    strengthItems = new JTextField(),
    defenseItems = new JTextField()
  };

  // Build out all of our text field options
  final String[] optionLabels = { // 12
    "Login Credentials:", // only show label
    "Username:",
    "Password:",
    "Startup Options:", // only show label
    "Script Name:",
    "Script Args:",
    "Position (-1 to center):",
    "X Coord (640 ea)",
    "Y Coord (395 ea)",
    "Hotkeys:", // only show label
    "SpellId (F8):",
    "Swap Item (F5):",
    "Swap Item (F6):",
    "Swap Item (F7):",
  };
  Panel[] textFieldPanels = new Panel[optionLabels.length];

  // Build out OCR options
  String[] ocrLabels = {
    "OCR Options:", // only show label
    "OCR Type:",
    "Sleep Server:",
  };
  Component[] ocrComponents = {
    new JTextField(), ocrChoice, ocrServer = new JTextField(),
  };
  JPanel[] ocrPanels = new JPanel[ocrLabels.length];

  // Build out our choice selectors (comboboxes)
  String[] choiceLabels = {"Theme Selector: ", "Server Port: ", "Server Ip: "};
  Choice[] choices = {themeChoice, initChoice, serverIpChoice};
  // the strings representing our choices go here
  String[][] choiceNames = {
    Arrays.stream(Theme.values()).map(Theme::getName).toArray(String[]::new),
    {"Coleslaw", "Uranium", "Custom"},
    {"game.openrsc.com", "Custom"}
  };
  Panel[] choicePanels = new Panel[choiceLabels.length];

  // Build out all of our checkbox options
  Component[] checkBoxes = {
    autoLogin = new Checkbox(" Auto-login", false),
    sideBar = new Checkbox(" Show Side Bar", true),
    logWindow = new Checkbox(" Open Log Window", false),
    debug = new Checkbox(" Open Debugger", false),
    botPaint = new Checkbox(" Show Bot Paint", true),
    disableGraphics = new Checkbox(" Disable Graphics", false),
    newUi = new Checkbox(" Custom Game UI", false),
    newIcons = new Checkbox(" New Skillbar Icons", false),
    keepOpen = new Checkbox(" Keep Inv Open", false),
    interlace = new Checkbox(" Interlace Mode", false),
    screenRefresh = new Checkbox(" 60s Screen Refresh", true),
    helpMenu = new Checkbox(" Show Help Menu", false),
    showVersion = new Checkbox(" Show Version", false)
  };

  Panel[] checkBoxPanels = new Panel[checkBoxes.length];

  AuthFrame(final String title, final String message, final Window parent) {
    super(title);
    this.parent = parent;
    this.setIconImage(Utils.getImage("res/logos/idlersc.icon.png").getImage());

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
    optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
    optionsPanel2.setLayout(new BoxLayout(optionsPanel2, BoxLayout.Y_AXIS));

    password.setEchoChar('*');
    for (int i = 0; i < textFieldPanels.length; i++) {
      textFieldPanels[i] = new Panel();
    }

    for (int i = 0; i < textFields.length; i++) {
      textFieldPanels[i].setLayout(new BoxLayout(textFieldPanels[i], BoxLayout.X_AXIS));
      Label optionLabel = new Label(optionLabels[i], Label.LEFT);
      optionLabel.setMaximumSize(new Dimension(110, 30));
      textFieldPanels[i].add(optionLabel);
      if (i == 0 || i == 3 || i == 6 || i == 9) {
        optionLabel.setMaximumSize(new Dimension(140, 30));
        continue;
      }
      setElementSizing(textFields[i], new Dimension(80, 23));
      textFields[i].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(0, 0, 0, 255)));
      textFieldPanels[i].add(textFields[i]);
    }

    for (int i = 0; i < choicePanels.length; i++) {
      choicePanels[i] = new Panel();
    }

    for (int i = 0; i < choiceLabels.length; i++) {
      choicePanels[i].setLayout(new BoxLayout(choicePanels[i], BoxLayout.Y_AXIS));
      Label optionLabel2 = new Label(choiceLabels[i], Label.CENTER);
      setElementSizing(optionLabel2, new Dimension(110, 15));
      choicePanels[i].add(optionLabel2);

      setElementSizing(choices[i], new Dimension(140, 20));
      for (final String choiceName : choiceNames[i]) {
        choices[i].add(choiceName);
      }
      choicePanels[i].add(choices[i]);
    }

    for (int i = 0; i < ocrPanels.length; i++) {
      ocrPanels[i] = new JPanel();
    }

    for (final OCRType ocrType : OCRType.VALUES) {
      ocrChoice.add(ocrType.getName());
    }
    ocrChoice.addItemListener(
        e -> {
          ocrServer.setEditable(getOCRType() == OCRType.REMOTE);
        });
    ocrServer.setEditable(false);
    ocrServer.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(0, 0, 0, 255)));

    for (int i = 0; i < ocrComponents.length; i++) {
      ocrPanels[i].setLayout(new BoxLayout(ocrPanels[i], BoxLayout.X_AXIS));
      Label ocrLabel = new Label(ocrLabels[i], Label.LEFT);
      ocrLabel.setMaximumSize(new Dimension(110, 30));
      ocrPanels[i].add(ocrLabel);
      if (i == 0) {
        ocrLabel.setMaximumSize(new Dimension(140, 30));
        continue;
      }
      setElementSizing(ocrComponents[i], new Dimension(80, 23));
      ocrPanels[i].add(ocrComponents[i]);
    }

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
    for (Panel subPanel : choicePanels) {
      optionsPanel.add(subPanel);
    }
    for (Panel subPanel : textFieldPanels) {
      optionsPanel.add(subPanel);
    }
    optionsPanel2.add(new JLabel(Utils.getImage("res/logos/idlersc.icon.png"), JLabel.LEFT));
    for (JPanel subPanel : ocrPanels) {
      optionsPanel2.add(subPanel);
    }
    for (Panel subPanel : checkBoxPanels) {
      optionsPanel2.add(subPanel);
    }

    okButton = new Button("Save");
    cancelButton = new Button("Cancel");
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    // Set Sizing
    Panel gapPanel = new Panel();
    gapPanel.setMaximumSize(new Dimension(10, 550));
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
    setElementSizing(optionsPanel, new Dimension(190, 550));
    setElementSizing(optionsPanel2, new Dimension(170, 550));
    setElementSizing(this, new Dimension(415, 550)); // authframe size

    // combine our panels (top part of window)
    inputPanel.add(optionsPanel);
    inputPanel.add(gapPanel);
    inputPanel.add(optionsPanel2);

    // Add the 3 main panels to the frame
    if (labelPanel != null) {
      add(labelPanel, BorderLayout.NORTH);
    }
    inputPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 0, 4, theme.getPrimaryBackground()));
    add(inputPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);

    // Add action listeners
    cancelButton.addActionListener(
        e -> {
          setDefaultValues();
          dispose();
        });

    themeChoice.addItemListener(
        e -> {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            theme = Theme.getFromName(themeChoice.getSelectedItem());
            colorizeComponents();
          }
        });

    pack();
    setResizable(false);
  }

  private void setDefaultValues() {
    username.setText("");
    password.setText("");
    scriptName.setText("");
    scriptArgs.setText("");
    positionX.setText("-1");
    positionY.setText("-1");
    spellId.setText("");
    attackItems.setText("");
    strengthItems.setText("");
    defenseItems.setText("");
    themeChoice.select(0);
    initChoice.select(0);
    serverIpChoice.select(0);
    ocrChoice.select(0);
    ocrServer.setText("");
    autoLogin.setState(false);
    sideBar.setState(true);
    logWindow.setState(false);
    debug.setState(false);
    botPaint.setState(true);
    disableGraphics.setState(false);
    screenRefresh.setState(true);
    interlace.setState(false);
    helpMenu.setState(false);
    showVersion.setState(false);
    newIcons.setState(false);
    newUi.setState(false);
    keepOpen.setState(true);
  }

  public void storeAuthData(AuthFrame auth) {
    final Properties p = new Properties();
    final String u = auth.getUsername();
    if (u == null || u.isEmpty()) {
      System.out.println("Username field blank, not saving account information");
      Main.setUsername("username");
      return;
    }
    Main.setUsername(u);

    p.put("username", u);
    p.put("password", auth.getPassword());
    p.put("script-name", auth.getScriptName());
    p.put("script-arguments", auth.getScriptArgs());
    p.put("init-cache", auth.getInitCache());
    p.put("x-position", auth.getPositionX());
    p.put("y-position", auth.getPositionY());
    p.put("spell-id", auth.getSpellId());
    p.put("attack-items", auth.getAttackItems());
    p.put("strength-items", auth.getStrengthItems());
    p.put("defence-items", auth.getDefenseItems());
    p.put("ocr-type", auth.getOCRType().getName());
    p.put("ocr-server", auth.getOCRServer());
    p.put("auto-login", auth.getAutoLogin());
    p.put("sidebar", auth.getSideBar());
    p.put("log-window", auth.getLogWindow());
    p.put("debug", auth.getDebugger());
    p.put("botpaint", auth.getBotPaint()); // true disables bot paint
    p.put("disable-gfx", auth.getDisableGraphics());
    p.put("interlace", auth.getInterlace());
    p.put("screen-refresh", auth.getScreenRefresh());
    p.put("help", auth.getHelpMenu());
    p.put("version", auth.getShowVersion());
    p.put("new-icons", auth.getNewIcons());
    p.put("new-ui", auth.getNewUi());
    p.put("keep-open", auth.getKeepOpen());
    p.put("server-ip", auth.getServerIpChoice());
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
  /**
   * Call all four component sizing methods
   *
   * @param component component to be resized
   * @param size Dimension object representing the x and y size
   */
  private void setElementSizing(Component component, Dimension size) {
    component.setMaximumSize(size);
    component.setMinimumSize(size);
    component.setPreferredSize(size);
    component.setSize(size);
  }

  @Override
  public void setVisible(final boolean visible) {
    if (visible) {
      this.setIconImage(Utils.getImage("res/logos/idlersc.icon.png").getImage());
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
        if (account == null || account.isEmpty()) account = Main.config.getUsername();
        final File file = accountPath.resolve(account + ".properties").toFile();
        try (final FileInputStream stream = new FileInputStream(file)) {
          p.load(stream);
          // ALWAYS make properties lowercase
          username.setText(p.getProperty("username", ""));
          if (!username.getText().isEmpty()) username.setEditable(false);
          password.setText(p.getProperty("password", ""));
          scriptName.setText(p.getProperty("script-name", ""));
          scriptArgs.setText(p.getProperty("script-arguments", ""));
          initChoice.select(p.getProperty("init-cache", "Coleslaw"));
          serverIpChoice.select(p.getProperty("server-ip", "game.openrsc.com"));
          positionX.setText(p.getProperty("x-position", "-1"));
          positionY.setText(p.getProperty("y-position", "-1"));
          spellId.setText(p.getProperty("spell-id", "-1"));
          attackItems.setText(p.getProperty("attack-items", ""));
          strengthItems.setText(p.getProperty("defence-items", ""));
          defenseItems.setText(p.getProperty("strength-items", ""));
          ocrChoice.select(p.getProperty("ocr-type", OCRType.INTERNAL.getName()));
          ocrServer.setText(p.getProperty("ocr-server", ""));
          autoLogin.setState(Boolean.parseBoolean(p.getProperty("auto-login", "false")));
          sideBar.setState(Boolean.parseBoolean(p.getProperty("sidebar", "false")));
          logWindow.setState(Boolean.parseBoolean(p.getProperty("log-window", "false")));
          debug.setState(Boolean.parseBoolean(p.getProperty("debug", "false")));
          botPaint.setState(Boolean.parseBoolean(p.getProperty("bot-paint", "true")));
          disableGraphics.setState(Boolean.parseBoolean(p.getProperty("disable-gfx", "false")));
          interlace.setState(Boolean.parseBoolean(p.getProperty("interlace", "false")));
          helpMenu.setState(Boolean.parseBoolean(p.getProperty("help", "false")));
          showVersion.setState(Boolean.parseBoolean(p.getProperty("version", "false")));
          themeChoice.select(p.getProperty("theme", Theme.RUNEDARK.getName()));
          theme = Theme.getFromName(p.getProperty("theme", Theme.RUNEDARK.getName()));
          newIcons.setState(Boolean.parseBoolean(p.getProperty("new-icons", "false")));
          newUi.setState(Boolean.parseBoolean(p.getProperty("new-ui", "false")));
          keepOpen.setState(Boolean.parseBoolean(p.getProperty("keep-open", "false")));
          screenRefresh.setState(Boolean.parseBoolean(p.getProperty("screen-refresh", "true")));

        } catch (final Throwable t) {
          System.out.println("Error loading account " + account + ": " + t);
        }
        colorizeComponents();

      } else {
        setDefaultValues();
      }
      setLocationRelativeTo(parent);
      toFront();
      requestFocus();
    }
    super.setVisible(visible);
  }

  private void colorizeComponents() {
    // This is kind of slow at recoloring. Not really a big deal, but doesn't look great.

    for (Component component : inputPanel.getComponents())
      component.setBackground(theme.getPrimaryBackground());
    optionsPanel.setBackground(theme.getPrimaryBackground());
    optionsPanel.setForeground(theme.getPrimaryForeground());
    optionsPanel2.setBackground(theme.getPrimaryBackground());
    optionsPanel2.setForeground(theme.getPrimaryForeground());
    inputPanel.setBackground(theme.getPrimaryBackground());
    buttonPanel.setBackground(theme.getPrimaryBackground().brighter());
    buttonPanel.setForeground(theme.getPrimaryForeground().brighter());

    okButton.setBackground(theme.getSecondaryBackground());
    cancelButton.setBackground(theme.getSecondaryBackground());
    okButton.setForeground(theme.getSecondaryForeground());
    cancelButton.setForeground(theme.getSecondaryForeground());
    for (Panel choicePanel : choicePanels) {
      choicePanel.setForeground(theme.getPrimaryForeground());
      choicePanel.setBackground(theme.getPrimaryBackground());
      for (Component component : choicePanel.getComponents()) {
        if (component instanceof Choice) {
          component.setBackground(theme.getSecondaryBackground());
          component.setForeground(theme.getSecondaryForeground());
        }
        if (component instanceof Label) {
          component.setBackground(theme.getPrimaryBackground());
          component.setForeground(theme.getPrimaryForeground());
        }
        if (component instanceof JTextField) {
          component.setForeground(theme.getSecondaryForeground());
          component.setBackground(theme.getSecondaryBackground());
        }
      }

      for (Panel textFieldPanel : textFieldPanels) {
        textFieldPanel.setForeground(theme.getPrimaryForeground());
        textFieldPanel.setBackground(theme.getPrimaryBackground());

        for (Component component : textFieldPanel.getComponents()) {
          if (component instanceof Label) {
            component.setBackground(theme.getPrimaryBackground());
            component.setForeground(theme.getPrimaryForeground());
          }
          if (component instanceof JTextField) {
            component.setForeground(theme.getSecondaryForeground());
            component.setBackground(theme.getSecondaryBackground());
          }
        }
      }

      for (JPanel panel : ocrPanels) {
        panel.setBackground(theme.getPrimaryBackground());
        panel.setForeground(theme.getPrimaryForeground());
        for (Component component : panel.getComponents()) {
          if (component instanceof Choice) {
            component.setBackground(theme.getSecondaryBackground());
            component.setForeground(theme.getSecondaryForeground());
          }
          if (component instanceof Label) {
            component.setBackground(theme.getPrimaryBackground());
            component.setForeground(theme.getPrimaryForeground());
          }
          if (component instanceof JTextField) {
            component.setForeground(theme.getSecondaryForeground());
            component.setBackground(theme.getSecondaryBackground());
          }
        }
      }
    }

    for (Component checkBox : checkBoxes) {
      checkBox.setBackground(theme.getPrimaryBackground());
      checkBox.setForeground(theme.getPrimaryForeground());
    }
  }

  public void setLoadSettings(boolean set) {
    loadSettings = set;
  }

  synchronized String getUsername() {
    return username.getText();
  }

  synchronized String getPassword() {
    return password.getText();
  }

  synchronized String getThemeName() {
    theme = Theme.getFromName(themeChoice.getSelectedItem());
    return themeChoice.getSelectedItem();
  }

  synchronized String getScriptName() {
    return scriptName.getText();
  }

  synchronized String getScriptArgs() {
    return scriptArgs.getText();
  }

  synchronized String getInitCache() {
    return initChoice.getSelectedItem();
  }

  synchronized String getServerIpChoice() {
    return serverIpChoice.getSelectedItem();
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

  synchronized String getPositionX() {
    return positionX.getText();
  }

  synchronized String getPositionY() {
    return positionY.getText();
  }

  synchronized OCRType getOCRType() {
    return OCRType.fromName(ocrChoice.getSelectedItem());
  }

  synchronized String getOCRServer() {
    return ocrServer.getText();
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

  synchronized String getScreenRefresh() {
    return Boolean.toString(screenRefresh.getState());
  }

  synchronized String getHelpMenu() {
    return Boolean.toString(helpMenu.getState());
  }

  synchronized String getShowVersion() {
    return Boolean.toString(showVersion.getState());
  }

  synchronized String getNewIcons() {
    return Boolean.toString(newIcons.getState());
  }

  synchronized String getNewUi() {
    return Boolean.toString(newUi.getState());
  }

  synchronized String getKeepOpen() {
    return Boolean.toString(keepOpen.getState());
  }

  synchronized void addActionListener(final ActionListener al) {
    okButton.addActionListener(al);
  }
}
