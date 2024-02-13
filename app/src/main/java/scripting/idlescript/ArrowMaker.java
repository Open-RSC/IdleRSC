package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * ArrowMaker by Searos
 *
 * @author Searos
 */
public class ArrowMaker extends IdleScript {
  final int[] arrowHeads = {669, 670, 671, 672, 673, 674, 381};
  final int[] completed = {11, 638, 640, 642, 644, 646, 637};
  int selectedArrowHead = -1;
  int completeSelected = -1;
  final JComboBox<String> arrowHead =
      new JComboBox<>(
          new String[] {"Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Headless"});
  boolean scriptStarted = false;
  boolean guiSetup = false;
  boolean headless = false;
  int startAmount = 0;
  JFrame scriptFrame = null;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }

    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    if (headless) {
      while (controller.isRunning()) {
        if (controller.getNeedToMove()) controller.moveCharacter();
        if (controller.getShouldSleep()) controller.sleepHandler(true);

        while (controller.getInventoryItemCount(280) > 9
            && controller.getInventoryItemCount(selectedArrowHead) > 9) {
          controller.useItemOnItemBySlot(
              controller.getInventoryItemSlotIndex(selectedArrowHead),
              controller.getInventoryItemSlotIndex(280));
          controller.sleep(100);
          while (controller.isBatching()) {
            controller.sleep(100);
          }
        }
        if (controller.getInventoryItemCount(280) < 9
            || controller.getInventoryItemCount(selectedArrowHead) < 9) {
          if (controller.getInventoryItemCount(280) < 9) {
            controller.displayMessage("Not enough Arrow Shafts");
          }
          if (controller.getInventoryItemCount(selectedArrowHead) < 9) {
            controller.displayMessage("Not enough Feathers");
          }
          scriptStarted = false;
          guiSetup = false;
          controller.stop();
        }
      }
    }
    if (!headless) {
      while (controller.isRunning()) {
        if (controller.getNeedToMove()) controller.moveCharacter();
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        while (controller.getInventoryItemCount(637) > 9
            && controller.getInventoryItemCount(selectedArrowHead) > 9) {
          controller.useItemOnItemBySlot(
              controller.getInventoryItemSlotIndex(selectedArrowHead),
              controller.getInventoryItemSlotIndex(637));
          controller.sleep(100);
          while (controller.isBatching()) {
            controller.sleep(100);
          }
        }
        if (controller.getInventoryItemCount(637) < 9
            || controller.getInventoryItemCount(selectedArrowHead) < 9) {
          if (controller.getInventoryItemCount(637) < 9) {
            controller.displayMessage("Not enough Headless Arrows");
          }
          if (controller.getInventoryItemCount(selectedArrowHead) < 9) {
            controller.displayMessage("Not enough " + arrowHead.getSelectedItem() + " Arrowheads");
          }
          scriptStarted = false;
          guiSetup = false;
          controller.stop();
        }
      }
    }
  }

  public void setupGUI() {
    JLabel header = new JLabel("Select Arrowhead");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          selectedArrowHead = arrowHeads[arrowHead.getSelectedIndex()];
          if (arrowHead.getSelectedIndex() == 6) {
            headless = true;
          }
          scriptStarted = true;
          completeSelected = completed[arrowHead.getSelectedIndex()];
          startAmount = controller.getInventoryItemCount(completeSelected);
          controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
          controller.displayMessage("@red@ArrowMaker started");
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(arrowHead);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      controller.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@Arrow Maker @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Arrows Made: @yel@"
              + (this.controller.getInventoryItemCount(completeSelected) - startAmount),
          10,
          35,
          0xFFFFFF,
          1);
    }
  }
}
