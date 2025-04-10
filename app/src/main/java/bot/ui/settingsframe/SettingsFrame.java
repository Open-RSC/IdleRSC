package bot.ui.settingsframe;

import bot.Main;
import bot.ocrlib.OCRType;
import bot.ui.Theme;
import bot.ui.components.ColorPickerPanel;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import orsc.util.Utils;

public class SettingsFrame extends JFrame {
  private Theme theme = loadTheme();
  private boolean loadSettings = true;
  private final Window parent;
  private String accountName;

  private final SpringLayout sl = new SpringLayout();
  private JPanel panel;
  private JTabbedPane tabPane;
  private AccountTab accountTab;
  private DisplayTab displayTab;
  private ScriptTab scriptTab;

  private Color[] colors;
  private String customPrimaryBG, customPrimaryFG, customSecondaryBG, customSecondaryFG;
  private JButton saveBtn, cancelBtn;

  public SettingsFrame(final String title, final String accountName, final Window parent) {
    setTitle(title);
    if (accountName != null && !accountName.isEmpty()) {
      setTitle(title + " - " + accountName);
      this.accountName = accountName;
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
      if (loadSettings) {
        loadSettings();
      } else {
        setDefaultValues();
      }
      setConstraints();

      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      pack();
      setLocationRelativeTo(parent);
      toFront();
      requestFocus();
    }
    super.setVisible(v);
  }

  private void initializeComponents() {
    // Define component colors
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

    // Initialize components
    tabPane = new JTabbedPane();
    tabPane.setBorder(BorderFactory.createEmptyBorder());
    panel = new JPanel(sl);
    accountTab = new AccountTab(sl);
    displayTab = new DisplayTab(sl);
    scriptTab = new ScriptTab(sl);

    saveBtn = new JButton("Save");
    saveBtn.addActionListener(
        e -> {
          storeAuthData(this);
          // This is only called if SettingsFrame was opened from a running rscFrame.
          if (parent.equals(Main.getRscFrame())) Main.reloadProperties();
          dispose();
        });
    cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(
        e -> {
          dispose();
        });

    // Add tab components to the tabPane
    final Map<String, JPanel> tabs = new LinkedHashMap<>();
    tabs.put("Account", accountTab);
    tabs.put("Display", displayTab);
    tabs.put("Script", scriptTab);

    tabs.forEach((name, panel) -> tabPane.addTab(name, panel));

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
          protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
            int tabCount = tabPane.getTabCount();
            int xOffset = 0;

            for (int i = 0; i < tabCount; i++) {
              Rectangle rect = rects[i];

              // Shift each tab by the specified gap
              rect.x = xOffset;
              xOffset += rect.width + TAB_GAP;

              // Paint the tab
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

    // Add 'top-level' components to the main panel, then add that panel to SettingsFrame
    panel.add(saveBtn);
    panel.add(cancelBtn);
    panel.add(tabPane);
    add(panel);
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

    // Must be called within setConstraints so it's set recursively after applying a new theme
    // This is set here instead of in the DisplayPanel setConstraints so it has access to theme.
    setThemeChoiceActionListener();
  }

  private void setThemeChoiceActionListener() {
    displayTab.themeChoice.addActionListener(
        e -> {
          if (displayTab.themeChoice.getComboBox().getItemCount() < 1) return;
          if (theme.getName().equalsIgnoreCase(displayTab.themeChoice.getSelectedItem())) return;

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

          theme = Theme.getFromName(displayTab.themeChoice.getSelectedItem());
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
          getContentPane().removeAll();
          initializeComponents();

          displayTab.primaryBGPanel.setHexColor(customPrimaryBG);
          displayTab.primaryFGPanel.setHexColor(customPrimaryFG);
          displayTab.secondaryBGPanel.setHexColor(customSecondaryBG);
          displayTab.secondaryFGPanel.setHexColor(customSecondaryFG);

          if (loadSettings) {
            loadSettings();
          } else {
            setDefaultValues();
          }
          displayTab.themeChoice.setSelectedItem(theme.getName());
          setConstraints();
          tabPane.setSelectedIndex(1);
          revalidate();
          repaint();
        });
  }

  private Theme loadTheme() {
    final Properties p = new Properties();
    Path accountPath = Paths.get("accounts");
    try {
      Files.createDirectories(accountPath);
    } catch (Throwable e2) {
      System.err.println("Failed to create directory: " + e2.getMessage());
      e2.printStackTrace();
      return Theme.RUNEDARK;
    }

    if (accountName == null || accountName.isEmpty()) {
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

    final File file = accountPath.resolve(accountName + ".properties").toFile();
    try (final FileInputStream stream = new FileInputStream(file)) {
      p.load(stream);
      Theme t = Theme.getFromName(p.getProperty("theme", "RuneDark"));
      String pbgHex = "#" + p.getProperty("custom-primary-background");
      String pfgHex = "#" + p.getProperty("custom-primary-foreground");
      String sbgHex = "#" + p.getProperty("custom-secondary-background");
      String sfgHex = "#" + p.getProperty("custom-secondary-foreground");

      customPrimaryBG = pbgHex;
      customPrimaryFG = pfgHex;
      customSecondaryBG = sbgHex;
      customSecondaryFG = sfgHex;

      colors =
          t.equals(Theme.CUSTOM)
              ? new Color[] {
                ColorPickerPanel.validateHex(pbgHex)
                    ? Color.decode(pbgHex)
                    : Theme.RUNEDARK.getPrimaryBackground(),
                ColorPickerPanel.validateHex(pfgHex)
                    ? Color.decode(pfgHex)
                    : Theme.RUNEDARK.getPrimaryForeground(),
                ColorPickerPanel.validateHex(sbgHex)
                    ? Color.decode(sbgHex)
                    : Theme.RUNEDARK.getSecondaryBackground(),
                ColorPickerPanel.validateHex(sfgHex)
                    ? Color.decode(sfgHex)
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
      System.out.println("Error loading account " + accountName + ": " + t);
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
      System.err.println("Failed to create directory: " + e2.getMessage());
      e2.printStackTrace();
      return;
    }
    // Now we can parse it
    if (accountName == null || accountName.isEmpty()) return;
    final File file = accountPath.resolve(accountName + ".properties").toFile();
    try (final FileInputStream stream = new FileInputStream(file)) {
      p.load(stream);
      // ALWAYS make properties lowercase
      accountTab.username.setText(p.getProperty("username", ""));
      if (!accountTab.username.getText().isEmpty())
        accountTab.username.getTextField().setEnabled(false);
      accountTab.password.setText(p.getProperty("password", ""));
      scriptTab.scriptName.setText(p.getProperty("script-name", ""));
      scriptTab.scriptArgs.setText(p.getProperty("script-arguments", ""));
      accountTab.initChoice.setSelectedItem(p.getProperty("init-cache", "Coleslaw"));
      accountTab.serverIpChoice.setSelectedItem(p.getProperty("server-ip", "game.openrsc.com"));
      displayTab.startPosX.setText(p.getProperty("x-position", "-1"));
      displayTab.startPosY.setText(p.getProperty("y-position", "-1"));
      scriptTab.spellId.setText(p.getProperty("spell-id", "-1"));
      scriptTab.attackItems.setText(p.getProperty("attack-items", ""));
      scriptTab.strengthItems.setText(p.getProperty("defence-items", ""));
      scriptTab.defenseItems.setText(p.getProperty("strength-items", ""));
      accountTab.ocrChoice.setSelectedItem(p.getProperty("ocr-type", OCRType.INTERNAL.getName()));
      accountTab.ocrServer.setText(p.getProperty("ocr-server", ""));
      accountTab.autoLogin.setSelected(Boolean.parseBoolean(p.getProperty("auto-login", "false")));
      accountTab.sideBar.setSelected(Boolean.parseBoolean(p.getProperty("sidebar", "false")));
      accountTab.logWindow.setSelected(Boolean.parseBoolean(p.getProperty("log-window", "false")));
      accountTab.debug.setSelected(Boolean.parseBoolean(p.getProperty("debug", "false")));
      displayTab.botPaint.setSelected(Boolean.parseBoolean(p.getProperty("bot-paint", "true")));
      displayTab.disableGraphics.setSelected(
          Boolean.parseBoolean(p.getProperty("disable-gfx", "false")));
      displayTab.interlace.setSelected(Boolean.parseBoolean(p.getProperty("interlace", "false")));
      accountTab.helpMenu.setSelected(Boolean.parseBoolean(p.getProperty("help", "false")));
      accountTab.showVersion.setSelected(Boolean.parseBoolean(p.getProperty("version", "false")));
      displayTab.themeChoice.setSelectedItem(p.getProperty("theme", Theme.RUNEDARK.getName()));
      displayTab.newIcons.setSelected(Boolean.parseBoolean(p.getProperty("new-icons", "false")));
      displayTab.newUi.setSelected(Boolean.parseBoolean(p.getProperty("new-ui", "false")));
      displayTab.keepOpen.setSelected(Boolean.parseBoolean(p.getProperty("keep-open", "false")));
      displayTab.screenRefresh.setSelected(
          Boolean.parseBoolean(p.getProperty("screen-refresh", "true")));

      customPrimaryBG =
          customPrimaryBG != null
              ? customPrimaryBG
              : "#" + p.getProperty("custom-primary-background");
      customPrimaryFG =
          customPrimaryFG != null
              ? customPrimaryFG
              : "#" + p.getProperty("custom-primary-foreground");
      customSecondaryBG =
          customSecondaryBG != null
              ? customSecondaryBG
              : "#" + p.getProperty("custom-secondary-background");
      customSecondaryFG =
          customSecondaryFG != null
              ? customSecondaryFG
              : "#" + p.getProperty("custom-secondary-foreground");

      displayTab.primaryBGPanel.setHexColor(
          ColorPickerPanel.validateHex(customPrimaryBG)
              ? customPrimaryBG
              : ColorPickerPanel.colorToHex(Theme.RUNEDARK.getPrimaryBackground()));
      displayTab.primaryFGPanel.setHexColor(
          ColorPickerPanel.validateHex(customPrimaryFG)
              ? customPrimaryFG
              : ColorPickerPanel.colorToHex(Theme.RUNEDARK.getPrimaryForeground()));
      displayTab.secondaryBGPanel.setHexColor(
          ColorPickerPanel.validateHex(customSecondaryBG)
              ? customSecondaryBG
              : ColorPickerPanel.colorToHex(Theme.RUNEDARK.getSecondaryBackground()));
      displayTab.secondaryFGPanel.setHexColor(
          ColorPickerPanel.validateHex(customSecondaryFG)
              ? customSecondaryFG
              : ColorPickerPanel.colorToHex(Theme.RUNEDARK.getSecondaryForeground()));
    } catch (final Throwable t) {
      System.out.println("Error loading account " + accountName + ": " + t);
    }
  }

  public void storeAuthData(SettingsFrame auth) {
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
    p.put("x-position", auth.getStartPosX());
    p.put("y-position", auth.getStartPosY());
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
    p.put("custom-primary-background", auth.getPrimaryBGString());
    p.put("custom-primary-foreground", auth.getPrimaryFGString());
    p.put("custom-secondary-background", auth.getSecondaryBGString());
    p.put("custom-secondary-foreground", auth.getSecondaryFGString());

    // Make sure our accounts folder exists
    Path accountPath = Paths.get("accounts");
    try {
      Files.createDirectories(accountPath);
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

  private void setDefaultValues() {
    accountTab.username.setText("");
    accountTab.password.setText("");
    scriptTab.scriptName.setText("");
    scriptTab.scriptArgs.setText("");
    displayTab.startPosX.setText("-1");
    displayTab.startPosY.setText("-1");
    scriptTab.spellId.setText("");
    scriptTab.attackItems.setText("");
    scriptTab.strengthItems.setText("");
    scriptTab.defenseItems.setText("");
    displayTab.themeChoice.setSelectedIndex(0);
    accountTab.initChoice.setSelectedIndex(0);
    accountTab.serverIpChoice.setSelectedIndex(0);
    accountTab.ocrChoice.setSelectedIndex(0);
    accountTab.ocrServer.setText("");
    accountTab.autoLogin.setSelected(false);
    accountTab.sideBar.setSelected(true);
    accountTab.logWindow.setSelected(false);
    accountTab.debug.setSelected(false);
    displayTab.botPaint.setSelected(true);
    displayTab.disableGraphics.setSelected(false);
    displayTab.screenRefresh.setSelected(true);
    displayTab.interlace.setSelected(false);
    accountTab.helpMenu.setSelected(false);
    accountTab.showVersion.setSelected(false);
    displayTab.newIcons.setSelected(false);
    displayTab.newUi.setSelected(false);
    displayTab.keepOpen.setSelected(true);
    displayTab.primaryBGPanel.setHexColor(
        ColorPickerPanel.colorToHex(Theme.RUNEDARK.getPrimaryBackground()));
    displayTab.primaryFGPanel.setHexColor(
        ColorPickerPanel.colorToHex(Theme.RUNEDARK.getPrimaryForeground()));
    displayTab.secondaryBGPanel.setHexColor(
        ColorPickerPanel.colorToHex(Theme.RUNEDARK.getSecondaryBackground()));
    displayTab.secondaryFGPanel.setHexColor(
        ColorPickerPanel.colorToHex(Theme.RUNEDARK.getSecondaryForeground()));
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
    return displayTab.startPosX.getText();
  }

  synchronized String getStartPosY() {
    return displayTab.startPosY.getText();
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
}
