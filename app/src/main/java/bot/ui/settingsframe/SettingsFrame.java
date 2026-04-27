package bot.ui.settingsframe;

import bot.Main;
import bot.ocrlib.OCRType;
import bot.ui.EntryFrame;
import bot.ui.Theme;
import bot.ui.components.ColorPickerPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import orsc.util.Utils;

public class SettingsFrame extends JFrame {
  private Theme theme;
  private boolean loadSettings = false;
  private final Window parent;
  private String propFile;

  private final SpringLayout sl = new SpringLayout();
  private JPanel panel;
  private JTabbedPane tabPane;
  private AccountTab accountTab;
  private DisplayTab displayTab;
  private ScriptTab scriptTab;

  private Color[] colors;
  protected String customPrimaryBG, customPrimaryFG, customSecondaryBG, customSecondaryFG;
  private JButton saveBtn, cancelBtn;

  public SettingsFrame(final String title, final String propFile, final Window parent) {
    setTitle(title);
    if (propFile != null && !propFile.isEmpty()) {
      setTitle(title + " - " + propFile);
      this.propFile = propFile;
      loadSettings = true;
    }

    this.parent = parent;
    setIconImage(Utils.getImage("res/logos/idlersc.icon.png").getImage());
  }

  @Override
  public void setVisible(boolean v) {
    if (v) {
      theme = loadTheme();
      initializeComponents();
      setMainButtonActionListeners();
      if (loadSettings) {
        loadSettings();
      } else {
        setDefaultValues();
      }
      setConstraints();
      setActionListeners();
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      pack();
      setLocationRelativeTo(parent);
      toFront();
      requestFocus();
    }
    super.setVisible(v);
  }

  private void setMainButtonActionListeners() {

    JTextField userField = accountTab.username.getTextField();
    JTextField passField = accountTab.password.getTextField();

    DocumentListener dl =
        new DocumentListener() {
          private void update() {
            saveBtn.setEnabled(!userField.getText().isEmpty() && !passField.getText().isEmpty());
          }

          @Override
          public void insertUpdate(DocumentEvent e) {
            update();
          }

          @Override
          public void removeUpdate(DocumentEvent e) {
            update();
          }

          @Override
          public void changedUpdate(DocumentEvent e) {
            update();
          }
        };

    userField.getDocument().addDocumentListener(dl);
    passField.getDocument().addDocumentListener(dl);
  }

  private void initializeComponents() {
    // Define component colors
    setUiManagerRules();

    // Initialize components
    tabPane = new JTabbedPane();
    tabPane.setBorder(BorderFactory.createEmptyBorder());
    panel = new JPanel(sl);
    accountTab = new AccountTab(sl);
    displayTab = new DisplayTab(sl);
    scriptTab = new ScriptTab(sl);

    saveBtn = new JButton("Save");
    saveBtn.setEnabled(false);
    cancelBtn = new JButton("Cancel");

    // Add tab components to the tabPane
    final Map<String, JPanel> tabs = new LinkedHashMap<>();
    tabs.put("Account", accountTab);
    tabs.put("Display", displayTab);
    tabs.put("Script", scriptTab);

    tabs.forEach((name, panel) -> tabPane.addTab(name, panel));
    customizeTabPane();
    // Add 'top-level' components to the main panel, then add that panel to SettingsFrame
    panel.add(saveBtn);
    panel.add(cancelBtn);
    panel.add(tabPane);
    add(panel);
  }

  private void customizeTabPane() {
    tabPane.setUI(
        new BasicTabbedPaneUI() {
          private static final int TAB_GAP = 7; // Gap between tabs

          @Override
          protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            int totalWidth = tabPane.getWidth();
            int tabCount = Math.max(1, tabPane.getTabCount());

            // Account for total gaps between tabs
            int totalGapWidth = (tabCount - 1) * TAB_GAP;

            // Ensure tabs fit properly within the total width
            return (totalWidth - totalGapWidth) / tabCount;
          }

          @Override
          protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            g.setColor(colors[2]);
            int width = tabPane.getWidth();
            int height = tabPane.getHeight();
            Insets insets = tabPane.getInsets();
            int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);

            g.fillRect(
                insets.left,
                insets.top + tabAreaHeight,
                width - insets.left - insets.right,
                height - insets.top - insets.bottom - tabAreaHeight);
          }

          @Override
          protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
            int tabCount = tabPane.getTabCount();
            int xOffset = 0;
            for (int i = 0; i < tabCount; i++) {
              Rectangle rect = rects[i];
              rect.x = xOffset;
              xOffset += rect.width + TAB_GAP;
              paintTab(g, tabPlacement, rect, i, selectedIndex == i);
            }
          }

          private void paintTab(
              Graphics g, int tabPlacement, Rectangle rect, int index, boolean isSelected) {
            // Draw tab background
            g.setColor(isSelected ? colors[2] : colors[0]);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);

            // Draw tab text
            g.setColor(isSelected ? colors[3] : colors[1]);
            String title = tabPane.getTitleAt(index);
            FontMetrics metrics = g.getFontMetrics();
            int textX = rect.x + (rect.width - metrics.stringWidth(title)) / 2;
            int textY = rect.y + (rect.height + metrics.getAscent()) / 2 - 2;
            g.drawString(title, textX, textY);
          }

          @Override
          protected Insets getContentBorderInsets(int tabPlacement) {
            return new Insets(2, 2, 2, 2); // Adjust this if content area needs spacing
          }
        });
  }

  private void setUiManagerRules() {
    UIManager.put("Button.background", colors[2]);
    UIManager.put("Button.foreground", colors[3]);
    UIManager.put("Panel.background", colors[0]);
    UIManager.put("Panel.foreground", colors[0]);
    UIManager.put("TabbedPane.background", colors[2]);
    UIManager.put("TabbedPane.selected", colors[2].brighter());
    UIManager.put("TabbedPane.foreground", colors[3]);
    UIManager.put("Label.background", colors[0]);
    UIManager.put("Label.foreground", colors[1]);
    UIManager.put("ComboBox.selectionForeground", colors[2]);
    UIManager.put("ComboBox.selectionBackground", colors[3]);
    UIManager.put("ComboBox.background", colors[2]);
    UIManager.put("ComboBox.foreground", colors[3]);
    UIManager.put("CheckBox.background", colors[0]);
    UIManager.put("CheckBox.foreground", colors[1]);
    UIManager.put("CheckBox.checkColorBg", colors[2]);
    UIManager.put("CheckBox.checkColorFg", colors[3]);
    UIManager.put("TabbedPane.contentBorderInsets", new Insets(2, 2, 2, 2));
    UIManager.put("TabbedPane.tabInsets", new Insets(6, 0, 6, 0));
    UIManager.put("TabbedPane.darkShadow", colors[3]);
    UIManager.put("TabbedPane.highlight", colors[3]);
    UIManager.put("TabbedPane.shadow", colors[3]);
    UIManager.put("TabbedPane.contentAreaColor", colors[3]);
    UIManager.put("TextField.inactiveForeground", colors[1]);
    UIManager.put("TextField.inactiveBackground", colors[0]);
    UIManager.put("TextField.foreground", colors[3]);
    UIManager.put("TextField.background", colors[2]);
    UIManager.put("TextField.caretForeground", colors[3]);
    UIManager.put("PasswordField.foreground", colors[3]);
    UIManager.put("PasswordField.background", colors[2]);
    UIManager.put("PasswordField.caretForeground", colors[3]);
  }

  /**
   * Sets the SpringLayout constraints and window properties of the SettingsFrame and the main
   * panel's components
   */
  private void setConstraints() {
    setResizable(false);
    Dimension windowDimension = new Dimension(400, 372);
    setMinimumSize(windowDimension);
    setPreferredSize(windowDimension);

    int buttonWidth = ((getPreferredSize().width - 28) / 2);

    // Tab Pane Constraints
    sl.putConstraint(SpringLayout.NORTH, tabPane, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, tabPane, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, tabPane, -4, SpringLayout.EAST, panel);
    sl.putConstraint(SpringLayout.SOUTH, tabPane, -32, SpringLayout.SOUTH, panel);

    // Save Button Constraints
    sl.putConstraint(SpringLayout.NORTH, saveBtn, 4, SpringLayout.SOUTH, tabPane);
    sl.putConstraint(SpringLayout.SOUTH, saveBtn, -4, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.WEST, saveBtn, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, saveBtn, buttonWidth, SpringLayout.WEST, saveBtn);

    // Cancel Button Constraints
    sl.putConstraint(SpringLayout.NORTH, cancelBtn, 4, SpringLayout.SOUTH, tabPane);
    sl.putConstraint(SpringLayout.SOUTH, cancelBtn, -4, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.EAST, cancelBtn, -4, SpringLayout.EAST, panel);
    sl.putConstraint(SpringLayout.WEST, cancelBtn, -buttonWidth, SpringLayout.EAST, cancelBtn);
  }

  public void displayThemeChoice() {
    theme = Theme.getFromName(displayTab.themeChoice.getSelectedItem());
    customPrimaryBG =
        (ColorPickerPanel.validateHex(displayTab.primaryBGPanel.getHexColor()))
            ? displayTab.primaryBGPanel.getHexColor()
            : ColorPickerPanel.colorToHex(Theme.RUNEDARK.getPrimaryBackground());
    customPrimaryFG =
        (ColorPickerPanel.validateHex(displayTab.primaryFGPanel.getHexColor()))
            ? displayTab.primaryFGPanel.getHexColor()
            : ColorPickerPanel.colorToHex(Theme.RUNEDARK.getPrimaryForeground());
    customSecondaryBG =
        (ColorPickerPanel.validateHex(displayTab.secondaryBGPanel.getHexColor()))
            ? displayTab.secondaryBGPanel.getHexColor()
            : ColorPickerPanel.colorToHex(Theme.RUNEDARK.getSecondaryBackground());
    customSecondaryFG =
        (ColorPickerPanel.validateHex(displayTab.secondaryFGPanel.getHexColor()))
            ? displayTab.secondaryFGPanel.getHexColor()
            : ColorPickerPanel.colorToHex(Theme.RUNEDARK.getSecondaryForeground());

    colors =
        theme.equals(Theme.CUSTOM)
            ? new Color[] {
              Color.decode(customPrimaryBG),
              Color.decode(customPrimaryFG),
              Color.decode(customSecondaryBG),
              Color.decode(customSecondaryFG)
            }
            : new Color[] {
              theme.getPrimaryBackground(),
              theme.getPrimaryForeground(),
              theme.getSecondaryBackground(),
              theme.getSecondaryForeground()
            };
    setUiManagerRules();
    SwingUtilities.invokeLater(
        () -> {
          panel.setBackground(UIManager.getColor("Panel.background"));
          saveBtn.setForeground(UIManager.getColor("Button.foreground"));
          saveBtn.setBackground(UIManager.getColor("Button.background"));
          cancelBtn.setForeground(UIManager.getColor("Button.foreground"));
          cancelBtn.setBackground(UIManager.getColor("Button.background"));

          for (Component tabPanel : tabPane.getComponents()) {
            if (tabPanel instanceof AbstractBaseTab) {
              tabPanel.setBackground(UIManager.getColor("Panel.background"));
              tabPanel.setForeground(UIManager.getColor("Panel.foreground"));
              SwingUtilities.updateComponentTreeUI(tabPanel);
            }
          }
          tabPane.repaint();
        });
  }

  private void setActionListeners() {
    saveBtn.addActionListener(
        e -> {
          storeAuthData(this);

          // Run specific logic depending on the frame the SettingsFrame was opened from
          // If opened from the running client, properties will be reloaded
          if (parent.equals(Main.getRscFrame())) Main.reloadProperties();

          // If opened from the EntryFrame, the account list will be refreshed
          if (parent instanceof EntryFrame) {
            EntryFrame entryFrame = (EntryFrame) parent;
            entryFrame.populateAccounts(getUsername());
            entryFrame.getAccountChoice().select(propFile);
            entryFrame.setAccountProp(propFile);
          }

          dispose();
        });
    cancelBtn.addActionListener(e -> dispose());
    displayTab.themeChoice.addActionListener(e -> displayThemeChoice());

    ColorPickerPanel[] colorPickerPanels =
        new ColorPickerPanel[] {
          displayTab.primaryBGPanel,
          displayTab.primaryFGPanel,
          displayTab.secondaryBGPanel,
          displayTab.secondaryFGPanel
        };

    for (ColorPickerPanel picker : colorPickerPanels) {
      picker
          .getHexField()
          .getDocument()
          .addDocumentListener(
              new DocumentListener() {
                private void runCallback() {
                  String text = picker.getHexField().getText().trim();
                  if (ColorPickerPanel.validateHex(text)) {

                    // Trigger action listener when we have a valid hex code
                    for (ActionListener al : picker.getHexField().getActionListeners())
                      al.actionPerformed(
                          new ActionEvent(
                              picker.getHexField(), ActionEvent.ACTION_PERFORMED, null));

                    // Apply theme if custom is set while editing hex colors
                    if (displayTab.themeChoice.getSelectedItem().equalsIgnoreCase("Custom"))
                      SwingUtilities.invokeLater(() -> displayThemeChoice());
                  }
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                  runCallback();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                  runCallback();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                  runCallback();
                }
              });
    }
  }

  private Theme loadTheme() {
    final Properties p = new Properties();
    Path accountPath = Paths.get("accounts");
    try {
      Files.createDirectories(accountPath);
    } catch (Throwable e2) {
      Main.logError("Failed to create directory: ", e2);
      return Theme.RUNEDARK;
    }

    if (propFile == null || propFile.isEmpty()) {
      customPrimaryBG = ColorPickerPanel.colorToHex(Theme.RUNEDARK.getPrimaryBackground());
      customPrimaryFG = ColorPickerPanel.colorToHex(Theme.RUNEDARK.getPrimaryForeground());
      customSecondaryBG = ColorPickerPanel.colorToHex(Theme.RUNEDARK.getSecondaryBackground());
      customSecondaryFG = ColorPickerPanel.colorToHex(Theme.RUNEDARK.getSecondaryForeground());

      colors =
          new Color[] {
            Theme.RUNEDARK.getPrimaryBackground(),
            Theme.RUNEDARK.getPrimaryForeground(),
            Theme.RUNEDARK.getSecondaryBackground(),
            Theme.RUNEDARK.getSecondaryForeground()
          };
      return Theme.RUNEDARK;
    }

    final File file = accountPath.resolve(propFile + ".properties").toFile();
    try (final FileInputStream stream = new FileInputStream(file)) {
      p.load(stream);
      Theme t = Theme.getFromName(getMigratedProperty(p, "theme-selected", "theme", "RuneDark"));
      String[] customThemeColors = getCustomThemeColors(p);

      colors =
          t.equals(Theme.CUSTOM)
              ? new Color[] {
                ColorPickerPanel.validateHex(customThemeColors[0])
                    ? Color.decode(customThemeColors[0])
                    : Theme.RUNEDARK.getPrimaryBackground(),
                ColorPickerPanel.validateHex(customThemeColors[1])
                    ? Color.decode(customThemeColors[1])
                    : Theme.RUNEDARK.getPrimaryForeground(),
                ColorPickerPanel.validateHex(customThemeColors[2])
                    ? Color.decode(customThemeColors[2])
                    : Theme.RUNEDARK.getSecondaryBackground(),
                ColorPickerPanel.validateHex(customThemeColors[0])
                    ? Color.decode(customThemeColors[0])
                    : Theme.RUNEDARK.getSecondaryForeground()
              }
              : new Color[] {
                t.getPrimaryBackground(),
                t.getPrimaryForeground(),
                t.getSecondaryBackground(),
                t.getSecondaryForeground()
              };

      return theme;

    } catch (final Throwable t) {
      Main.logError("Error loading account " + propFile + ":", t);
      return Theme.RUNEDARK;
    }
  }

  private void loadSettings() {
    // Make sure our accounts folder exists
    final Properties p = new Properties();
    Path accountPath = Paths.get("accounts");
    try {
      Files.createDirectories(accountPath);
    } catch (IOException e2) {
      Main.logError("Failed to create directory: ", e2);
      return;
    }
    // Now we can parse it
    if (propFile == null || propFile.isEmpty()) return;
    final File file = accountPath.resolve(propFile + ".properties").toFile();
    try (final FileInputStream stream = new FileInputStream(file)) {
      p.load(stream);

      // Load the values to the tabs
      scriptTab.loadSettings(p);
      accountTab.loadSettings(p);
      displayTab.loadSettings(p);
    } catch (final Throwable t) {
      Main.logError("Error loading account " + propFile + ":", t);
    }
  }

  public void storeAuthData(SettingsFrame auth) {
    final Properties p = new Properties();
    final String u = auth.getUsername();
    final String pass = auth.getPassword();

    if (u == null || u.isEmpty() || pass == null || pass.isEmpty()) {
      Main.logError(
          "Error saving account properties file: Username and password must not be blank");
      Main.setUsername("username");
      return;
    }
    Main.setUsername(u);
    p.put("account-name", u);
    p.put("account-password", auth.getPassword());
    p.put("account-server-custom-address", auth.getCustomIp());
    p.put("account-server-custom-port", String.valueOf(auth.getCustomPort()));
    p.put("account-server-option-address", auth.getServerIpChoice());
    p.put("account-server-option-port", auth.getInitCache());
    p.put("script-name", auth.getScriptName());
    p.put("script-arguments", auth.getScriptArgs());
    p.put("x-position", auth.getStartPosX());
    p.put("y-position", auth.getStartPosY());
    p.put("spell-id", auth.getSpellId());
    p.put("item-switches-attack", auth.getAttackItems());
    p.put("item-switches-strength", auth.getStrengthItems());
    p.put("item-switches-defence", auth.getDefenseItems());
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
    p.put("theme-selected", auth.getThemeName());
    p.put("theme-custom-primary-background", auth.getPrimaryBGString());
    p.put("theme-custom-primary-foreground", auth.getPrimaryFGString());
    p.put("theme-custom-secondary-background", auth.getSecondaryBGString());
    p.put("theme-custom-secondary-foreground", auth.getSecondaryFGString());
    p.put("use-location-walker", auth.getUseLocationWalker());

    // Make sure our accounts folder exists
    Path accountPath = Paths.get("accounts");
    try {
      Files.createDirectories(accountPath);
    } catch (IOException e2) {
      Main.logError("Failed to create directory: ", e2);
      return;
    }

    // Now we can parse it
    if (propFile == null) {
      String propServer =
          getServerIpChoice().equalsIgnoreCase("custom") ? "custom" : "openrsc".toLowerCase();
      String propPort = getInitCache().toLowerCase();
      String propSuffix;

      if (propServer.equalsIgnoreCase("openrsc")) {
        propSuffix = String.format("%s", propPort);
      } else {
        propSuffix = String.format("%s_%s", propServer, propPort.substring(0, 2));
      }

      propFile = String.format("%s@%s", u, propSuffix);

      if (Files.exists(accountPath.resolve(propFile + ".properties"))) {
        Main.logError(String.format("The account file already exists: '%s.properties'", propFile));
        return;
      }
    }
    final File file = accountPath.resolve(propFile + ".properties").toFile();
    try (final PrintWriter writer = new PrintWriter(new FileWriter(file))) {
      SortedMap<String, String> sorted = new TreeMap<>();
      for (String name : p.stringPropertyNames()) sorted.put(name, p.getProperty(name));
      for (Map.Entry<String, String> entry : sorted.entrySet())
        writer.println(entry.getKey() + "=" + entry.getValue());
    } catch (final Throwable t) {
      System.out.println("Error saving account details: " + t);
    }
  }

  private void setDefaultValues() {
    for (Component tab : tabPane.getComponents())
      if (tab instanceof AbstractBaseTab) ((AbstractBaseTab) tab).setDefaultValues();
  }

  synchronized String getUsername() {
    return accountTab.username.getText();
  }

  synchronized String getPassword() {
    return accountTab.password.getText();
  }

  synchronized String getThemeName() {
    theme = Theme.getFromName(displayTab.themeChoice.getSelectedItem());
    return displayTab.themeChoice.getSelectedItem();
  }

  synchronized String getUseLocationWalker() {
    return Boolean.toString(scriptTab.locationWalkerCheckBox.isSelected());
  }

  synchronized String getScriptName() {
    return scriptTab.scriptName.getText();
  }

  synchronized String getScriptArgs() {
    return scriptTab.scriptArgs.getText();
  }

  synchronized String getInitCache() {
    return accountTab.initChoice.getSelectedItem();
  }

  synchronized String getServerIpChoice() {
    return accountTab.serverIpChoice.getSelectedItem();
  }

  synchronized String getPrimaryBGString() {
    return displayTab.primaryBGPanel.getHexColor().substring(1);
  }

  synchronized String getPrimaryFGString() {
    return displayTab.primaryFGPanel.getHexColor().substring(1);
  }

  synchronized String getSecondaryBGString() {
    return displayTab.secondaryBGPanel.getHexColor().substring(1);
  }

  synchronized String getSecondaryFGString() {
    return displayTab.secondaryFGPanel.getHexColor().substring(1);
  }

  synchronized String getCustomIp() {
    if (accountTab.customIp == null || accountTab.customIp.isEmpty()) return "localhost";
    return accountTab.customIp;
  }

  synchronized int getCustomPort() {
    if (accountTab.customIp == null) return 43599;
    return accountTab.customPort;
  }

  synchronized String getSpellId() {
    return scriptTab.spellId.getText();
  }

  synchronized String getAttackItems() {
    return scriptTab.attackItems.getText();
  }

  synchronized String getStrengthItems() {
    return scriptTab.strengthItems.getText();
  }

  synchronized String getDefenseItems() {
    return scriptTab.defenseItems.getText();
  }

  synchronized String getStartPosX() {
    String value = displayTab.startPosX.getText();
    try {
      return String.valueOf(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      return "";
    }
  }

  synchronized String getStartPosY() {
    String value = displayTab.startPosY.getText();
    try {
      return String.valueOf(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      return "";
    }
  }

  synchronized OCRType getOCRType() {
    return OCRType.fromName(accountTab.ocrChoice.getSelectedItem());
  }

  synchronized String getOCRServer() {
    return accountTab.ocrServer.getText();
  }

  synchronized String getAutoLogin() {
    return Boolean.toString(accountTab.autoLogin.isSelected());
  }

  synchronized String getLogWindow() {
    return Boolean.toString(accountTab.logWindow.isSelected());
  }

  synchronized String getSideBar() {
    return Boolean.toString(accountTab.sideBar.isSelected());
  }

  synchronized String getDebugger() {
    return Boolean.toString(accountTab.debug.isSelected());
  }

  synchronized String getBotPaint() {
    return Boolean.toString(displayTab.botPaint.isSelected());
  }

  synchronized String getDisableGraphics() {
    return Boolean.toString(displayTab.disableGraphics.isSelected());
  }

  synchronized String getInterlace() {
    return Boolean.toString(displayTab.interlace.isSelected());
  }

  synchronized String getScreenRefresh() {
    return Boolean.toString(displayTab.screenRefresh.isSelected());
  }

  synchronized String getHelpMenu() {
    return Boolean.toString(accountTab.helpMenu.isSelected());
  }

  synchronized String getShowVersion() {
    return Boolean.toString(accountTab.showVersion.isSelected());
  }

  synchronized String getNewIcons() {
    return Boolean.toString(displayTab.newIcons != null && displayTab.newIcons.isSelected());
  }

  synchronized String getNewUi() {
    return Boolean.toString(displayTab.newUi.isSelected());
  }

  synchronized String getKeepOpen() {
    return Boolean.toString(displayTab.keepOpen.isSelected());
  }

  private String[] getCustomThemeColors(Properties p) {
    String customPrimaryBG =
        "#"
            + getMigratedProperty(
                p, "theme-custom-primary-background", "custom-primary-background", "");
    String customPrimaryFG =
        "#"
            + getMigratedProperty(
                p, "theme-custom-primary-foreground", "custom-primary-foreground", "");
    String customSecondaryBG =
        "#"
            + getMigratedProperty(
                p, "theme-custom-secondary-background", "custom-secondary-background", "");
    String customSecondaryFG =
        "#"
            + getMigratedProperty(
                p, "theme-custom-secondary-foreground", "custom-secondary-foreground", "");

    return new String[] {customPrimaryBG, customPrimaryFG, customSecondaryBG, customSecondaryFG};
  }

  /**
   * Read a config value, preferring the new key but falling back to the old one
   *
   * @param p Properties
   * @param newKey String -- The name of the key we're migrating to
   * @param oldKey String -- The name of the key we're migrating away from
   * @param defaultValue String -- default value
   * @return String -- The value read, or the defaultValue if it was null
   */
  static String getMigratedProperty(
      Properties p, String newKey, String oldKey, String defaultValue) {
    String value = p.getProperty(newKey);
    if (value == null) value = p.getProperty(oldKey);

    return value != null ? value : defaultValue;
  }
}
