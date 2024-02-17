package scripting.idlescript.other.AIOAIO.core.gui;

import java.awt.*;
import javax.swing.*;
import scripting.idlescript.other.AIOAIO.AIOAIO;

public class AIOAIO_GUI {
  static JFrame scriptFrame = null;
  static JTabbedPane tabbedPane;

  public static void setupGUI() {
    scriptFrame = new JFrame("AIO AIO v" + AIOAIO.VERSION);
    scriptFrame.setLayout(new BorderLayout());

    tabbedPane = new JTabbedPane();

    // Adding Tasks Panel
    Tasks_GUI tasksPanel = new Tasks_GUI();
    tabbedPane.addTab("Tasks", tasksPanel.getPanel());

    // Adding About Panel
    About_GUI aboutPanel = new About_GUI();
    tabbedPane.addTab("About", aboutPanel.getPanel());

    scriptFrame.add(tabbedPane, BorderLayout.CENTER);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
