package bot.ui;

import bot.Main;
import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

/**
 * JPanel wrapper class for everything in the Console panel
 *
 * @author Kaila
 */
public class ConsolePanel extends JPanel implements UiContract {

  // This is the list of all components so the themes can update
  private static LinkedList<JComponent> primaryComponents;

  // Log area components
  private static JTextArea logArea;
  private static JButton buttonClear;

  /** Creates a JMenu specifically for the Console Panel. */
  public ConsolePanel() {
    super();
    populateOptions();
  }

  /** Initialization method for the Class, only call this when constructing a new object */
  private void populateOptions() {

    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();

    // add components to tracking
    primaryComponents = new LinkedList<>();

    // Components
    JCheckBox autoscroll = new JCheckBox("Lock scroll to bottom", true);
    JScrollPane scroller = new JScrollPane(logArea);
    buttonClear = new JButton("Clear");

    logArea = new JTextArea(9, 44);

    logArea.setEditable(false);

    // add components to tracking
    primaryComponents.add(autoscroll);
    primaryComponents.add(logArea);
    primaryComponents.add(scroller);
    primaryComponents.add(this);

    // build out gridbag
    constraints.gridy = 1;
    constraints.insets = new Insets(5, 5, 5, 5);
    constraints.anchor = GridBagConstraints.SOUTHEAST;
    constraints.gridx = 2;
    constraints.weightx = 0.5;
    add(autoscroll, constraints);

    constraints.gridy = 1;
    constraints.insets = new Insets(0, 5, 5, 5);
    constraints.anchor = GridBagConstraints.SOUTHWEST;
    constraints.gridx = 1;
    constraints.weightx = 0.5;
    add(buttonClear, constraints);

    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 4;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.weightx = 1.0;
    constraints.weighty = 1.0;
    add(new JScrollPane(logArea), constraints);

    // action listeners
    buttonClear.addActionListener(evt -> clearLog());
    setSize(480, 320);

    // Theming
    applyTheme();
    setVisible(Main.config.isSidePanelVisible());
  }

  /** Invoke this method to have this panel update the theme colors. */
  public void applyTheme() {
    for (JComponent elem : primaryComponents) {
      elem.setBackground(Main.primaryBG);
      elem.setForeground(Main.primaryFG);
    }
    buttonClear.setBackground(Main.secondaryBG);
    buttonClear.setForeground(Main.secondaryFG);
    logArea.setBackground(Main.primaryBG.brighter());
  }

  /** Clears the log window. */
  public static void clearLog() {
    logArea.setText("");
  }

  /**
   * Gives access to the text area of the log window
   *
   * @return JTextArea - the component text field
   */
  public static JTextArea getLogArea() {
    return logArea;
  }

  public static void addLogMessage(String text) {
    if (getLogArea() == null) return;
    getLogArea().append(text + "\n");
    if (BottomPanel.logPanelSelected()) logArea.setCaretPosition(logArea.getDocument().getLength());
  }
}
