package bot.ui;

import bot.Main;
import bot.ui.scriptselector.models.PackageInfo;
import controller.Controller;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/**
 * JPanel wrapper class for everything on the right panel of the main bot window
 *
 * @author Kaila
 */
// TODO: make a Tabbed Panel setup, other tabs are xp tracker, loot track.
public class SidePanel extends JPanel implements UiContract {

  private static Controller c;

  // This is the list of all components so the themes can update
  private static LinkedList<JComponent> wrappers;
  private static LinkedList<JComponent> buttons;
  private static LinkedList<JComponent> panels;

  // This boolean switches the pathwalker button to use LocationWalker instead.
  // It is temporary until I feel it is ready to replace it entirely.
  private static final boolean switchToLocationWalkerButton = false;
  private static boolean buttonToggledStatus = false;

  private static JButton scriptButton, pathwalkerButton;

  /** Creates a JMenu specifically for the SidePanel. */
  public SidePanel() {
    super();
    c = Main.getController();
    populateOptions();
  }

  /** Initialization method for the Class, only call this when constructing a new object */
  private void populateOptions() {

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setPreferredSize(new Dimension(140, this.getHeight())); // Increase width

    // Set up component lists
    wrappers = new LinkedList<>();
    buttons = new LinkedList<>();
    panels = new LinkedList<>();
    panels.add(this);

    // NEW components go here, define in the class as static private
    JButton takeScreenshotButton;
    JButton showIdButton;
    JButton openDebuggerButton;
    JButton depositAllButton;
    JComponent[] _buttons =
        new JComponent[] {
          scriptButton = new JButton("Load Script"),
          pathwalkerButton =
              new JButton(switchToLocationWalkerButton ? "LocationWalker" : "PathWalker"),
          takeScreenshotButton = new JButton("Screenshot"),
          showIdButton = new JButton("Show IDs"),
          depositAllButton = new JButton("Deposit All"),
          openDebuggerButton = new JButton("Debugger"),
        };

    Dimension buttonSize = new Dimension(122, 30);
    Dimension wrapperSize = new Dimension(122, 35);

    //  Add Components in-order, apply theming,
    for (JComponent elem : _buttons) {
      elem.setFont(new Font("SansSerif", Font.PLAIN, 12));
      elem.setPreferredSize(buttonSize); // stricter sizing rules for the buttons
      elem.setMaximumSize(buttonSize);
      elem.setMinimumSize(buttonSize);
      elem.setSize(buttonSize);
      elem.setFocusable(false);
      elem.setAlignmentX(Component.LEFT_ALIGNMENT); // Ensure no stretching

      // Hover effect
      elem.addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
              if (elem.isEnabled()) elem.setBackground(Main.secondaryBG.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
              if (elem.isEnabled()) elem.setBackground(Main.secondaryBG);
            }
          });

      // Create a wrapper panel for external padding
      JPanel wrapper = new JPanel();
      wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
      wrapper.setPreferredSize(wrapperSize); // less strict sizing rules

      // add button to wrapper and wrapper to panel
      wrapper.add(elem);
      add(wrapper);

      // add to the list of components
      wrappers.add(wrapper);
      buttons.add(elem);
    }
    applyTheme();
    setVisible(Main.config.isSidePanelVisible());

    // Add action Listeners below
    scriptButton.addActionListener(e -> Main.handleScriptButton());

    pathwalkerButton.addActionListener(
        e -> {
          if (!Main.isRunning()) {
            if (switchToLocationWalkerButton) {
              Main.loadAndRunScript("LocationWalker", PackageInfo.NATIVE);
            } else {
              Main.loadAndRunScript("PathWalker", PackageInfo.APOS);
            }
            Main.config.setScriptArguments(new String[] {""});
            Main.setRunning(true);
          }
        });
    takeScreenshotButton.addActionListener(e -> c.takeScreenshot(""));
    showIdButton.addActionListener(
        e -> {
          c.toggleViewId();
          showIdButton.setText(c.getViewIdState() ? "Hide IDs" : "Show IDs");
        });

    depositAllButton.addActionListener(e -> c.depositAll());
    openDebuggerButton.addActionListener(
        e -> {
          c.log("IdleRSC: Opening Debug Window", "gre");
          Main.getDebugger().open();
        });
  }

  /** Invoke this method to have this panel update the theme colors. */
  public void applyTheme() {
    for (JComponent elem : buttons) {
      elem.setBackground(Main.secondaryBG);
      elem.setForeground(Main.secondaryFG);
      // drop-shadow effect using CompoundBorder
      Border outerShadow = new MatteBorder(0, 0, 2, 2, Main.primaryBG.darker().darker());
      Border innerBorder = new LineBorder(Main.primaryFG.darker(), 2);
      elem.setBorder(new CompoundBorder(outerShadow, innerBorder));
    }
    for (JComponent elem : wrappers) {
      elem.setBackground(Main.primaryBG);
      elem.setBorder(new MatteBorder(2, 4, 2, 4, Main.primaryBG));
    }
    for (JComponent elem : panels) {
      elem.setBackground(Main.primaryBG);
    }
  }

  /**
   * Update the script start button state
   *
   * @param isScriptLoaded boolean - true if script is starting/running
   */
  public static void setScriptButton(boolean isScriptLoaded) {
    scriptButton.setText(isScriptLoaded ? "Stop Script" : "Load Script");
  }

  /**
   * Toggles buttons when scripts are running
   *
   * @param isScriptRunning boolean -- The buttons enabled status
   */
  public static void toggleScriptButtons(boolean isScriptRunning) {
    // Add buttons to this array that need to be disabled when scripts are running
    JButton[] buttons = {pathwalkerButton};

    if (buttonToggledStatus != isScriptRunning) {
      buttonToggledStatus = isScriptRunning;

      for (JButton button : buttons) {
        button.setEnabled(!isScriptRunning);
        if (isScriptRunning) {
          button.setBackground(button.getBackground().darker());
          button.setForeground(button.getForeground().darker());
        } else {
          button.setBackground(button.getBackground().brighter());
          button.setForeground(button.getForeground().brighter());
        }
      }
    }
  }
}
