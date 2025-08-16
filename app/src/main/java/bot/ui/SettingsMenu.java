package bot.ui;

import bot.Config;
import bot.Main;
import bot.ui.components.*;
import bot.ui.settingsframe.SettingsFrame;
import callbacks.DrawCallback;
import controller.Controller;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import javax.swing.*;

/**
 * JMenu wrapper class for settings menu dropdown (left lab of top frame)
 *
 * @author Kaila
 */
public class SettingsMenu extends JMenu implements UiContract {

  private static Controller c;
  private static Config config;

  // This is the list of all components so the themes can update
  private static LinkedList<JComponent> menuitems;
  private static LinkedList<JComponent> panels;

  // Define component types
  private static CustomCheckBoxMenuItem customUiMode, interlaceCheckbox, debugCheckbox, keepInvOpen;

  /**
   * Creates a JMenu specifically for the Settings Dropdown Menu.
   *
   * @param s String -- The title of the SettingsMenu. This is what's displayed on the JMenuBar.
   */
  public SettingsMenu(String s) {
    super(s);
    c = Main.getController();
    config = Main.config;
    populateOptions();
  }

  /** Initialization method for the Class, only call this when constructing a new object */
  private void populateOptions() {

    // Define component types
    JMenuItem resetXpButton;
    JMenuItem accOpp;

    // Set up component lists
    menuitems = new LinkedList<>();
    panels = new LinkedList<>();
    panels.add(this);

    // Add the object to this component array in order for it to add to settings dropdown
    JComponent[] components =
        new JComponent[] {
          accOpp = new JMenuItem("Account Settings", KeyEvent.VK_F4), // S key
          resetXpButton = new JMenuItem("Reset XP Counter"),
          keepInvOpen =
              new CustomCheckBoxMenuItem(
                  "Keep Inventory Open", "Keeps the inventory open in Custom UI mode"),
          customUiMode =
              new CustomCheckBoxMenuItem(
                  "Custom In-game UI", "Changes the UI to be more similar to a modern day RS UI"),
          interlaceCheckbox =
              new CustomCheckBoxMenuItem(
                  "Interlace Mode", "Enable interlace mode for the game window"),
          debugCheckbox =
              new CustomCheckBoxMenuItem(
                  "Console Debug Messages", "Show debug messages in the log window")
        };
    accOpp.setToolTipText("Open the account settings window");
    resetXpButton.setToolTipText("Reset the xp tracker");

    // Add elements to settings menu
    for (JComponent elem : components) {
      elem.setFont(new Font("SansSerif", Font.PLAIN, 14));
      elem.setFocusable(false);
      add(elem);
      // add to list
      menuitems.add(elem);
    }

    // Update checkbox/etc state on initialization, using the config class
    loadSettingsFromConfig();

    // Apply Theming
    applyTheme();
    setBorder(BorderFactory.createEmptyBorder());
    getPopupMenu().setBorder(BorderFactory.createEmptyBorder());

    // Should force settings popup menu to be on top of the game window... Hopefully.
    getPopupMenu().setLightWeightPopupEnabled(false);

    // Set action listeners below for each of the buttons
    accOpp.addActionListener(
        e -> {
          SettingsFrame authFrame =
              new SettingsFrame("Editing an account", config.getUsername(), Main.getRscFrame());

          setPopupMenuVisible(false);
          authFrame.setVisible(true);
        });

    resetXpButton.addActionListener(e -> DrawCallback.resetXpCounter());

    keepInvOpen.addActionListener(
        e -> {
          setPopupMenuVisible(false);
          c.setKeepInventoryOpenMode(keepInvOpen.isSelected());
        });

    customUiMode.addActionListener(
        e -> {
          setPopupMenuVisible(false);
          c.setCustomUiMode(customUiMode.isSelected());
        });

    interlaceCheckbox.addActionListener(
        e -> {
          if (c != null) {
            c.setInterlacer(interlaceCheckbox.isSelected());
          }
        });

    debugCheckbox.addActionListener(
        e -> {
          if (c != null) {
            if (debugCheckbox.isSelected()) // no action needed, main checks flag
            c.logAsClient("IdleRSC: Turning On Console Debugging Messages", "gre");
            else c.logAsClient("IdleRSC: Turning Off Console Debugging Messages", "gre");
          }
        });
  }

  public void loadSettingsFromConfig() {
    keepInvOpen.setSelected(config.getKeepOpen());
    customUiMode.setSelected(config.getNewUi());
    interlaceCheckbox.setSelected(config.isGraphicsInterlacingEnabled());
    debugCheckbox.setSelected(config.isDebug());

    if (c != null) {
      if (c.getCustomUiMode() != customUiSelected()) c.setCustomUiMode(config.getNewUi());
      if (c.isInterlacing() != interlaceSelected()) c.setInterlacer(interlaceSelected());
    }
  }

  /** Invoke this method to have the panel refresh theming */
  public void applyTheme() {
    // menu item theming
    for (JComponent elem : menuitems) {
      elem.setBackground(Main.primaryBG);
      elem.setForeground(Main.primaryFG);
    }
    // Panel theming
    for (JComponent elem : panels) {
      elem.setBackground(Main.primaryBG);
    }
    setForeground(Main.primaryFG);
    setBackground(Main.primaryBG);
    // getPopupMenu().setBackground(Main.primaryBG);

  }

  /**
   * Checks if the "keep inv open" toggle is enabled.
   *
   * @return boolean - true if keepInvOpen mode is on
   */
  public static boolean invOpenSelected() {
    return debugCheckbox.isSelected();
  }

  /**
   * Checks if the "customUi mode" toggle is enabled.
   *
   * @return boolean - true if customUI mode is on
   */
  public static boolean customUiSelected() {
    return customUiMode.isSelected();
  }

  /**
   * Checks if the "Interlace mode" toggle is enabled.
   *
   * @return true if interlacing mode is on
   */
  public static boolean interlaceSelected() {
    return interlaceCheckbox.isSelected();
  }

  /**
   * Checks if the "debug messages mode" toggle is enabled.
   *
   * @return true is debug messages enabled
   */
  public static boolean debugSelected() {
    return debugCheckbox.isSelected();
  }
}
