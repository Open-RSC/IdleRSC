package bot.ui;

import bot.Config;
import bot.Main;
import bot.ui.components.*;
import callbacks.DrawCallback;
import controller.Controller;
import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

/**
 * JPanel wrapper class for everything on the right panel of the main bot window
 *
 * @author Kaila
 */
public class BottomPanel extends JPanel implements UiContract {

  private static Controller c;
  private static Config config;

  // This is the list of all components so the themes can update
  private static LinkedList<JComponent> checkboxes;
  private static LinkedList<JComponent> panels;

  private static ConsolePanel consolePanel;
  private static CustomCheckBox autoLoginCheckbox,
      logPanelCheckbox,
      render3DCheckbox,
      botPaintCheckbox,
      sidePanelCheckbox;

  private static CustomCheckBox graphicsCheckbox;

  /** Creates a JPanel specifically for the BottomPanel. */
  public BottomPanel() {
    super();
    c = Main.getController();
    config = Main.config;
    populateOptions();
  }

  /** Initialization method for the Class, only call this when constructing a new object */
  private void populateOptions() {

    setLayout(new BorderLayout());
    consolePanel = new ConsolePanel();

    // set up the top checkbox panel
    JPanel checkboxPanel = new JPanel();
    checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.X_AXIS));

    // Set up component lists
    checkboxes = new LinkedList<>();
    panels = new LinkedList<>();
    panels.add(this);
    panels.add(checkboxPanel);

    // Add everything to the main JPanel
    add(consolePanel, BorderLayout.SOUTH);
    add(checkboxPanel, BorderLayout.NORTH);

    // NEW components go here, define in the class as static private
    JComponent[] checkboxComponents =
        new JComponent[] {
          autoLoginCheckbox =
              new CustomCheckBox("Auto-Login", "Auto-login, bypassing the login window"),
          graphicsCheckbox = new CustomCheckBox("Rendering", "Render Graphics"),
          botPaintCheckbox = new CustomCheckBox("Bot Layer", "Draw bot paint"),
          render3DCheckbox = new CustomCheckBox("3D Render", "Render terrain and objects"),
          logPanelCheckbox = new CustomCheckBox("Console", "Show the log panel"),
          sidePanelCheckbox = new CustomCheckBox("SidePanel", "Show the side panel")
        };

    // Add components in-order
    for (JComponent elem : checkboxComponents) {
      elem.setFont(new Font("SansSerif", Font.PLAIN, 13));
      elem.setPreferredSize(new Dimension(90, 18));
      elem.setFocusable(false);
      checkboxPanel.add(elem);

      // add to the list of components
      checkboxes.add(elem);
    }

    // Apply Theming
    applyTheme();

    // Update checkbox states
    loadSettingsFromConfig();

    // Button Action Listeners
    autoLoginCheckbox.addActionListener(
        e -> {
          if (autoLoginCheckbox.isSelected()) c.login();
        });
    render3DCheckbox.addActionListener(
        e -> {
          if (c != null) {
            c.setRender3D(render3DCheckbox.isSelected());
          }
        });
    botPaintCheckbox.addActionListener(
        e -> {
          if (c != null) {
            c.setBotPaint(botPaintCheckbox.isSelected());
          }
        });
    graphicsCheckbox.addActionListener(
        e -> {
          if (c != null) {
            c.setDrawing(graphicsCheckbox.isSelected(), 0);
            if (graphicsCheckbox.isSelected()) {
              DrawCallback.setNextRefresh(-1);
            } else if (config.getScreenRefresh()) {
              DrawCallback.setNextRefresh(
                  (System.currentTimeMillis() + 25000L + (long) (Math.random() * 10000)));
            }
          }
        });
    sidePanelCheckbox.addActionListener(
        e -> {
          if (c != null) {
            JFrame rscFrame = Main.rscFrame;

            int prevWidth = rscFrame.getWidth();
            int prevHeight = rscFrame.getHeight();
            setMinimumWindowSize();

            boolean isSelected = sidePanelSelected();
            c.log("IdleRSC: " + (isSelected ? "Showing" : "Hiding") + " Side Panel!", "gre");
            Main.sidePanel.setVisible(isSelected);

            if (prevWidth == rscFrame.getWidth())
              rscFrame.setSize(prevWidth + (isSelected ? 140 : -140), prevHeight);
          }
        });
    logPanelCheckbox.addActionListener(
        e -> {
          if (c != null) {
            JFrame rscFrame = Main.rscFrame;

            int prevWidth = rscFrame.getWidth();
            int prevHeight = rscFrame.getHeight();
            setMinimumWindowSize();

            boolean isSelected = logPanelSelected();
            c.log("IdleRSC: " + (isSelected ? "Showing" : "Hiding") + " Log Panel!", "gre");
            consolePanel.setVisible(isSelected);

            if (prevHeight == rscFrame.getHeight())
              rscFrame.setSize(prevWidth, prevHeight + (isSelected ? 187 : -187));
          }
        });
  }

  public void loadSettingsFromConfig() {
    autoLoginCheckbox.setSelected(config.isAutoLogin());
    render3DCheckbox.setSelected(config.isRender3DEnabled());
    graphicsCheckbox.setSelected(config.isGraphicsEnabled());
    logPanelCheckbox.setSelected(config.isLogWindowVisible());
    sidePanelCheckbox.setSelected(config.isSidePanelVisible());
    botPaintCheckbox.setSelected(config.isBotPaintVisible());
    consolePanel.setVisible(config.isLogWindowVisible());

    if (c != null) {
      if (c.isDrawEnabled() != graphicsCheckbox.isSelected())
        c.setDrawing(graphicsCheckbox.isSelected(), 0);
      if (c.isRender3DEnabled() != render3DCheckbox.isSelected())
        c.setRender3D(render3DCheckbox.isSelected());
      if (c.getShowBotPaint() != botPaintCheckbox.isSelected())
        c.setBotPaint(botPaintCheckbox.isSelected());
      if (c.isAutoLogin() != autoLoginSelected()) c.setAutoLogin(autoLoginSelected());
    }
  }

  private void setMinimumWindowSize() {
    JFrame rscFrame = Main.rscFrame;

    Dimension newMinSize =
        new Dimension(sidePanelSelected() ? 655 : 533, logPanelSelected() ? 611 : 423);

    if (!rscFrame.getMinimumSize().equals(newMinSize)) rscFrame.setMinimumSize(newMinSize);
  }

  /** Invoke this method to have this panel update the theme colors. */
  public void applyTheme() {
    // Checkbox Theming
    for (JComponent elem : checkboxes) {
      elem.setBackground(Main.primaryBG);
      elem.setForeground(Main.primaryFG);
    }
    // Panel theming
    for (JComponent elem : panels) {
      elem.setBackground(Main.primaryBG);
    }
  }

  /**
   * The consoleFrame (log window) Object
   *
   * @return JPanel - consoleFrame panel
   */
  public static ConsolePanel getConsolePanel() {
    return consolePanel;
  }

  /**
   * Checks if the "keep inv open" toggle is enabled.
   *
   * @return boolean - true if log panel is open
   */
  public static boolean logPanelSelected() {
    return logPanelCheckbox.isSelected();
  }

  /**
   * Checks if the side panel toggle is enabled.
   *
   * @return true if the side panel is open
   */
  public static boolean sidePanelSelected() {
    return sidePanelCheckbox.isSelected();
  }

  /**
   * Sets the "autologin" toggle
   *
   * @param b - set true when autologin is on, false when not
   */
  public static void setAutoLogin(boolean b) {
    autoLoginCheckbox.setSelected(b);
  }

  /**
   * Checks if the "autologin" toggle is enabled.
   *
   * @return boolean with whether or not autologin is enabled.
   */
  public static boolean autoLoginSelected() {
    return autoLoginCheckbox.isSelected();
  }
}
