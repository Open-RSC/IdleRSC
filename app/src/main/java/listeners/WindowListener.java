package listeners;

import bot.Main;
import controller.Controller;
import java.awt.*;
import javax.swing.*;

/**
 * WindowListener is a listener which updates other windows based on what has been clicked on the
 * side panel.
 *
 * <p>WindowListener is always running, and it runs as a separate thread from the main bot.
 *
 * @author Dvorak
 */
public class WindowListener implements Runnable {

  final JFrame rscFrame;
  final JComponent botFrame;
  final JPanel consoleFrame;
  final JMenuBar menuBar;
  final JMenu themeMenu, settingsMenu;
  final JButton buttonClear;
  final JCheckBox autoscrollLogsCheckbox;
  final JScrollPane scroller;
  final JTextArea logArea;
  final Controller controller;
  JButton[] buttonArray;
  JCheckBox[] checkBoxArray;

  public WindowListener(
      JComponent _botFrame,
      JPanel _consoleFrame,
      JFrame _rscFrame,
      JMenu _settingsMenu,
      JMenu _themeMenu,
      JMenuBar _menuBar,
      JScrollPane _scroller,
      JTextArea _logArea,
      Controller _controller,
      JButton _buttonClear,
      JCheckBox _autoscrollLogsCheckbox,
      JButton[] _buttonArray,
      JCheckBox[] _checkBoxArray) {

    botFrame = _botFrame;
    consoleFrame = _consoleFrame;
    rscFrame = _rscFrame;
    settingsMenu = _settingsMenu;
    themeMenu = _themeMenu;
    menuBar = _menuBar;
    scroller = _scroller;
    logArea = _logArea;
    controller = _controller;
    buttonClear = _buttonClear;
    autoscrollLogsCheckbox = _autoscrollLogsCheckbox;
    buttonArray = _buttonArray;
    checkBoxArray = _checkBoxArray;
  }

  @Override
  public void run() {
    Color[] colors = null;
    int prevWidth = rscFrame.getWidth();
    int prevHeight = rscFrame.getHeight();
    boolean consolePrevious = Main.isLogWindowOpen();
    boolean sidePrevious = Main.isSideWindowOpen();
    try {
      while (true) {

        // set custom UI mode when logged it (relog resets the mode)
        if (controller.isLoggedIn() && Main.isCustomUiSelected() && !controller.getCustomUiMode()) {
          Thread.sleep(640);
          controller.setCustomUiMode(true);
        }

        // Update size of JFrame when log window is opened and closed
        if (consolePrevious != Main.isLogWindowOpen()) {
          if (Main.isLogWindowOpen()) {
            controller.log("IdleRSC: Showing Log Window!", "gre");
            consoleFrame.setVisible(true);
            rscFrame.setSize(rscFrame.getWidth(), rscFrame.getHeight() + 188);
            if (!controller.isDrawEnabled()) refreshGraphics(50);
          } else {
            controller.log("IdleRSC: Hiding Log Window!", "gre");
            consoleFrame.setVisible(false);
            rscFrame.setSize(rscFrame.getWidth(), rscFrame.getHeight() - 188);
            if (!controller.isDrawEnabled()) refreshGraphics(50);
          }
          consolePrevious = Main.isLogWindowOpen();
        }
        // Update size of JFrame when side window is opened and closed
        if (sidePrevious != Main.isSideWindowOpen()) {
          if (Main.isSideWindowOpen()) {
            controller.log("IdleRSC: Showing Side Bar!", "gre");
            botFrame.setVisible(true);
            rscFrame.setSize(rscFrame.getWidth() + 122, rscFrame.getHeight());
            if (!controller.isDrawEnabled()) refreshGraphics(50);
          } else {
            controller.log("IdleRSC: Hiding Side Bar!", "gre");
            botFrame.setVisible(false);
            rscFrame.setSize(rscFrame.getWidth() - 122, rscFrame.getHeight());
            if (!controller.isDrawEnabled()) refreshGraphics(50);
          }
          sidePrevious = Main.isSideWindowOpen();
        }

        // Resize window if it goes below a certain size (crashes when small height and you
        // teleport)
        if (Main.isLogWindowOpen() && rscFrame.getHeight() < 593) {
          rscFrame.setSize(rscFrame.getWidth(), 593);
        } else if (rscFrame.getHeight() < 405) rscFrame.setSize(rscFrame.getWidth(), 405);
        if (Main.isSideWindowOpen() && rscFrame.getWidth() < 655) {
          rscFrame.setSize(655, rscFrame.getHeight());
        } else if (rscFrame.getWidth() < 533) rscFrame.setSize(533, rscFrame.getHeight());

        // Refresh JFrame when resizing happens to prevent white screen
        if ((rscFrame.getWidth() != prevWidth || rscFrame.getHeight() != prevHeight)) {
          if (!controller.isDrawEnabled()) refreshGraphics(300);
          prevWidth = rscFrame.getWidth();
          prevHeight = rscFrame.getHeight();
        }

        // update our theme when themeName string is changed in Main

        if (colors == null
            || colors[0] != Main.primaryBG
            || colors[1] != Main.primaryFG
            || colors[2] != Main.secondaryBG
            || colors[3] != Main.secondaryFG) {
          colors = new Color[] {Main.primaryBG, Main.primaryFG, Main.secondaryBG, Main.secondaryFG};
          botFrame.setBackground(colors[0]);
          rscFrame.getContentPane().setBackground(colors[0]);
          botFrame.setBorder(BorderFactory.createLineBorder(colors[0]));
          themeMenu.setForeground(colors[1]);
          themeMenu.getPopupMenu().setBackground(colors[0]);
          settingsMenu.setForeground(colors[1]); // text color
          menuBar.setBackground(colors[0]);
          menuBar.setBorder(BorderFactory.createLineBorder(colors[0]));
          buttonClear.setBackground(colors[2]);
          buttonClear.setForeground(colors[3]);
          autoscrollLogsCheckbox.setBackground(colors[0]);
          autoscrollLogsCheckbox.setForeground(colors[1]);
          logArea.setBackground(colors[0].brighter());
          logArea.setForeground(colors[1]);
          scroller.setBackground(colors[0]);
          scroller.setForeground(colors[1]);
          consoleFrame.setBackground(colors[0]);
          consoleFrame.setForeground(colors[1]);

          for (Component menuComponent : settingsMenu.getMenuComponents()) {
            menuComponent.setBackground(colors[0]);
            menuComponent.setForeground(colors[1]);
          }

          for (JButton jButton : buttonArray) {
            jButton.setBackground(colors[2]);
            jButton.setForeground(colors[3]);
          }
          for (JCheckBox jCheckbox : checkBoxArray) {
            jCheckbox.setBackground(colors[0]);
            jCheckbox.setForeground(colors[1]);
          }
        }

        Thread.sleep(60);
      }
    } catch (InterruptedException e) {
      // Restore interrupted status
      Thread.currentThread().interrupt();
    }
  }
  /**
   * turn on graphics for X number of ticks and then disable again <br>
   * Useful to prevent applet white screen on rscFrame changes
   *
   * @param pauseTicks int number of ticks to wait while graphics on
   */
  private void refreshGraphics(int pauseTicks) {
    controller.setDrawing(true, pauseTicks);
  }
}
