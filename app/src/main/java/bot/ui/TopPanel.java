package bot.ui;

import bot.Main;
import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

/**
 * JMenuBar wrapper class for everything on the top Panel of the main bot window
 *
 * @author Kaila
 */
public class TopPanel extends JMenuBar implements UiContract {

  // This is the list of all components so the themes can update
  private static LinkedList<JComponent> dropdowns;
  private static LinkedList<JComponent> panels;

  private static SettingsMenu settingsMenu;
  private static ThemesMenu themeMenu;

  /** Creates a JPanel specifically for the TopPanel. */
  public TopPanel() {
    super();
    populateOptions();
  }

  /** Initialization method for the Class, only call this when constructing a new object */
  private void populateOptions() {

    // Set up component lists
    dropdowns = new LinkedList<>();
    panels = new LinkedList<>();
    panels.add(this);

    JComponent[] elements = {
      settingsMenu = new SettingsMenu("Settings"), themeMenu = new ThemesMenu("Themes")
    };

    // Add components in-order
    for (JComponent elem : elements) {
      elem.setPreferredSize(new Dimension(60, 20));
      elem.setFont(new Font("SansSerif", Font.PLAIN, 14));
      add(elem);
      elem.setFocusable(false);
      // add to the list of components for theming
      dropdowns.add(elem);
    }

    // Theming
    applyTheme();
    setBorder(BorderFactory.createEmptyBorder());
  }

  /** Invoke this method to have this panel update the theme colors. */
  public void applyTheme() {
    // Dropdown Theming
    for (JComponent elem : dropdowns) {
      elem.setBackground(Main.primaryBG);
      elem.setForeground(Main.primaryFG);
    }
    // Panel theming
    for (JComponent elem : panels) {
      elem.setBackground(Main.primaryBG);
    }
    setBackground(Main.primaryBG);
    setForeground(Main.primaryFG);
  }

  public static SettingsMenu getSettingsMenu() {
    return settingsMenu;
  }

  public static ThemesMenu getThemeMenu() {
    return themeMenu;
  }
}
