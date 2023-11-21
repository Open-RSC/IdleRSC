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
  final JMenu themeMenu, menu;
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
      JMenu _menu,
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
    menu = _menu;
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
    String themeName = Main.getThemeName();
    boolean consolePrevious = Main.isLogWindowOpen();
    boolean sidePrevious = Main.isSideWindowOpen();

    while (true) {
      if (consolePrevious != Main.isLogWindowOpen()) {
        if (Main.isLogWindowOpen()) {
          consoleFrame.setVisible(true);
          rscFrame.setSize(rscFrame.getWidth(), rscFrame.getHeight() + 188); // 320?
          // rscFrame.setMinimumSize(new Dimension(655, 605));
        } else {
          consoleFrame.setVisible(false);
          rscFrame.setSize(rscFrame.getWidth(), rscFrame.getHeight() - 188);
          // rscFrame.setMinimumSize(new Dimension(655, 405));
        }
        consolePrevious = Main.isLogWindowOpen();
      }
      if (sidePrevious != Main.isSideWindowOpen()) {
        if (Main.isSideWindowOpen()) {
          botFrame.setVisible(true);
          rscFrame.setSize(rscFrame.getWidth() + 122, rscFrame.getHeight()); // 320?
          // rscFrame.setMinimumSize(new Dimension(655, 605));
        } else {
          botFrame.setVisible(false);
          rscFrame.setSize(rscFrame.getWidth() - 122, rscFrame.getHeight());
          // rscFrame.setMinimumSize(new Dimension(655, 405));
        }
        sidePrevious = Main.isSideWindowOpen();
      }
      // update our theme when themeName string is changed in Main
      if (!themeName.equals(Main.getThemeName())) {
        Color[] colors = Main.getThemeElements(Main.getThemeName()); // back, front

        botFrame.setBackground(colors[0]);
        rscFrame.getContentPane().setBackground(colors[0]);
        botFrame.setBorder(BorderFactory.createLineBorder(colors[0]));
        themeMenu.setForeground(colors[1]);
        menu.setForeground(colors[1]); // text color
        menuBar.setBackground(colors[0]);
        menuBar.setBorder(BorderFactory.createLineBorder(colors[0]));
        for (JButton jButton : buttonArray) {
          jButton.setBackground(colors[0].darker());
          jButton.setForeground(colors[1]);
        }

        for (JCheckBox jCheckbox : checkBoxArray) {
          jCheckbox.setBackground(colors[0]);
          jCheckbox.setForeground(colors[1]);
        }
        buttonClear.setBackground(colors[0].darker());
        buttonClear.setForeground(colors[1]);
        autoscrollLogsCheckbox.setBackground(colors[0]);
        autoscrollLogsCheckbox.setForeground(colors[1]);
        logArea.setBackground(colors[0].brighter());
        logArea.setForeground(colors[1]);
        scroller.setBackground(colors[0]);
        scroller.setForeground(colors[1]);

        consoleFrame.setBackground(colors[0]);
        consoleFrame.setForeground(colors[1]);

        themeName = Main.getThemeName();
      }
      //      if (consoleFrame.isVisible()) {
      //        if (!consoleFrame.getSize().equals(new Dimension(rscFrame.getWidth(), 225))) {
      //          consoleFrame.setSize(rscFrame.getWidth(), 225);
      //        }
      //        if (!consoleFrame
      //            .getLocation()
      //            .equals(
      //                new Point(
      //                    rscFrame.getLocation().x, rscFrame.getLocation().y +
      // rscFrame.getHeight()))) {
      //          consoleFrame.setLocation(
      //              rscFrame.getLocation().x, rscFrame.getLocation().y + rscFrame.getHeight());
      //        }
      //      }
      try {
        Thread.sleep(40);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
