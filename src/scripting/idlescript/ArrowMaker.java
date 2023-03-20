package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
  int[] arrowHeads = {669, 670, 671, 672, 673, 674, 381};
  int[] completed = {11, 638, 640, 642, 644, 646, 637};
  int selectedArrowHead = -1;
  int completeSelected = -1;
  JComboBox<String> arrowHead =
      new JComboBox<String>(
          new String[] {"Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Headless"});
  boolean scriptStarted = false;
  boolean guiSetup = false;
  boolean headless = false;
  int startAmount = 0;
  JFrame scriptFrame = null;

  public int start(String parameters[]) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }

    if (scriptStarted) {
      while (controller.isRunning()) {
        scriptStart();
      }
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    if (headless) {
      while (controller.isRunning()) {
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

  public static void centerWindow(Window frame) {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
    frame.setLocation(x, y);
  }

  public void setupGUI() {
    JLabel header = new JLabel("Select Arrowhead");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            selectedArrowHead = arrowHeads[arrowHead.getSelectedIndex()];
            if (arrowHead.getSelectedIndex() == 6) {
              headless = true;
            }
            scriptStarted = true;
            completeSelected = completed[arrowHead.getSelectedIndex()];
            startAmount = controller.getInventoryItemCount(completeSelected);
            controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
            controller.displayMessage("@red@ArrowMaker started");
          }
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(arrowHead);
    scriptFrame.add(startScriptButton);
    scriptFrame.setVisible(true);
    centerWindow(scriptFrame);
    scriptFrame.pack();
    scriptFrame.requestFocus();
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      controller.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@Arrow Maker @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Arrows Made: @yel@"
              + String.valueOf(
                  this.controller.getInventoryItemCount(completeSelected) - startAmount),
          10,
          35,
          0xFFFFFF,
          1);
    }
  }
}
