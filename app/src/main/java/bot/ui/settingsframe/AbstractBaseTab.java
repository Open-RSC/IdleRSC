package bot.ui.settingsframe;

import java.awt.*;
import java.util.Properties;
import javax.swing.*;

public abstract class AbstractBaseTab extends JPanel {
  final SpringLayout sl;
  int compWidth = 180;
  int compHeight = 41;
  protected Component[] tabComponents;

  AbstractBaseTab(SpringLayout springLayout) {
    super(springLayout);
    sl = springLayout;

    initializeComponents();
    if (tabComponents != null) for (Component c : tabComponents) add(c);

    setConstraints();
  }

  /** Setter for child tabs to pass components to base */
  protected void setTabComponents(Component... components) {
    this.tabComponents = components;
  }

  /**
   * Set setCompWidth
   *
   * @param width int
   */
  void setCompWidth(int width) {
    compWidth = width;
  }

  /**
   * Returns compWidth
   *
   * @return int
   */
  int getCompWidth() {
    return compWidth;
  }

  /**
   * Set compHeight
   *
   * @param height int
   */
  void setCompHeight(int height) {
    compHeight = height;
  }

  /**
   * Returns compHeight
   *
   * @return int
   */
  int getCompHeight() {
    return compHeight;
  }

  /** Set SpringLayout constraints in here <br> */
  abstract void setConstraints();

  /**
   * Initialize components,add any action listeners in here, and add them via setTabComponents()
   * <br>
   */
  abstract void initializeComponents();

  /** Handles loading the properties to the tab's components */
  abstract void loadSettings(Properties p);

  /** Sets the components to their default value */
  abstract void setDefaultValues();

  /**
   * Read a config value, preferring the new key but falling back to the old one
   *
   * @param p Properties
   * @param newKey String -- The name of the key we're migrating to
   * @param oldKey String -- The name of the key we're migrating away from
   * @param defaultValue String -- default value
   * @return String -- The value read, or the defaultValue if it was null
   */
  String getMigratedProperty(Properties p, String newKey, String oldKey, String defaultValue) {
    String value = p.getProperty(newKey);
    if (value == null) {
      value = p.getProperty(oldKey);
    }
    return value != null ? value : defaultValue;
  }
}
