package scripting.idlescript;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Woodcutting by Searos
 *
 * @author Searos
 */
public class Woodcutting extends IdleScript {
  JCheckBox bank = new JCheckBox("Bank", true);
  JComboBox<String> destination =
      new JComboBox<String>(
          new String[] {
            "Draynor",
            "Varrock West",
            "Varrock East",
            "Draynor",
            "EdgeVille",
            "Falador",
            "Seers",
            "North Ardy",
            "South Ardy",
            "Yanille"
          });
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  int treeId = -1;
  int logId = -1;
  int[] treeIds = {1, 306, 307, 308, 309, 310};
  int[] logIds = {14, 632, 633, 634, 635, 636};
  int saveX = 0;
  int saveY = 0;
  int bankSelX = -1;
  int bankSelY = -1;
  int totalLogs = 0;
  int bankedLogs = 0;
  int[] bankX = {220, 150, 103, 220, 216, 283, 503, 582, 566, 588};
  int[] bankY = {635, 504, 511, 365, 450, 569, 452, 576, 600, 754};
  boolean bankTime = false;
  boolean chopTime = false;
  int[] bankerIds = {95, 224, 268, 485, 540, 617};

  int[] axes = {12, 87, 88, 203, 204, 405, 1263};

  public int start(String parameters[]) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }

    if (scriptStarted) {
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void startWalking(int x, int y) {
    // shitty autowalk
    int newX = x;
    int newY = y;
    while (controller.currentX() != x || controller.currentY() != y) {
      if (controller.currentX() - x > 20) {
        newX = controller.currentX() - 20;
      }
      if (controller.currentY() - y > 20) {
        newY = controller.currentY() - 20;
      }
      if (controller.currentX() - x < -20) {
        newX = controller.currentX() + 20;
      }
      if (controller.currentY() - y < -20) {
        newY = controller.currentY() + 20;
      }
      if (Math.abs(controller.currentX() - x) <= 20) {
        newX = x;
      }
      if (Math.abs(controller.currentY() - y) <= 20) {
        newY = y;
      }
      if (!controller.isTileEmpty(newX, newY)) {
        controller.walkToAsync(newX, newY, 2);
        controller.sleep(1000);
      } else {
        controller.walkToAsync(newX, newY, 2);
        controller.sleep(1000);
      }
    }
  }

  public boolean isAxe(int id) {
    for (int i = 0; i < axes.length; i++) {
      if (axes[i] == id) return true;
    }

    return false;
  }

  public void scriptStart() {
    while (controller.isRunning()) {
      if (controller.getInventoryItemCount() == 30) {
        bankTime = true;
        chopTime = false;
      }
      if (controller.getInventoryItemCount() <= 29) {
        bankTime = false;
      }
      if (controller.getNearestObjectById(treeId) != null
          && chopTime
          && !controller
              .isBatching()) { // bot spams  getNearestObjectById and goes to 25% cpu, this specific
        // one
        controller.sleepHandler(98, true);
        int[] treeCoords = controller.getNearestObjectById(treeId);
        controller.atObject(treeCoords[0], treeCoords[1]);
        controller.sleep(1200); // more sleep to let batching catch up!
        while (controller.isBatching() && controller.getInventoryItemCount() < 30) {
          controller.sleep(1000);
        }
      } else { // added else so when getNearestObjectById == null this function doesn't repeat and
        // overflow cpu usage
        controller.sleep(
            2000); // added sleep to this function to stop cpu overflow issue going to high usage
        // (IMPORTANT)
      }
      if (bank.isSelected() && bankTime) {
        while (controller.getNearestNpcByIds(bankerIds, true) == null) {
          startWalking(bankSelX, bankSelY);
        }
        controller.setStatus("@red@Banking");
        if (!controller.isInBank()) { // changed from while to if, might fix occasional bank break?
          controller.openBank();
          controller.sleep(100);
        }
      }
      if (controller
          .isInBank()) { // && controller.getInventoryItemCount() > 0  //removed, not needed
        totalLogs = totalLogs + controller.getInventoryItemCount(logId);
        for (int itemId : controller.getInventoryItemIds()) {
          if (itemId != 0 && !isAxe(itemId) && itemId != 1263) {
            controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
          }
          controller.sleep(100);
        }
        bankedLogs = controller.getBankItemCount(logId);
        controller.sleep(100);
        controller.closeBank();
        bankTime = false;
        controller.sleep(1000); // added
      }
      if (controller.getInventoryItemCount() == 0 && controller.isInBank()) {
        controller.closeBank();
        controller.sleep(100);
      }
      if (!bankTime && !chopTime) {
        // controller.sleep(1000);
        if (controller.getNearestObjectById(treeId) == null) { // changed to if
          startWalking(saveX, saveY);
          controller.sleep(
              340); // added sleep, this one probably not needed, but small sleep after pathwalking
          // is fine
        } else { // changed to else to remove 2nd getNearestObjectById check
          // if (controller.getNearestObjectById(treeId) != null) { //removed
          controller.setStatus("@red@Chopping");
          chopTime = true;
          return;
        }
      }
    }
    scriptStarted = false;
    guiSetup = false;
  }

  public void setupGUI() {
    JLabel header = new JLabel("Woodcutting");
    JLabel treeLabel = new JLabel("Tree Type:");
    JComboBox<String> treeField =
        new JComboBox<String>(new String[] {"Normal", "Oak", "Willow", "Maple", "Yew", "Magic"});
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            treeId = treeIds[treeField.getSelectedIndex()];
            logId = treeIds[treeField.getSelectedIndex()];
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            scriptStarted = true;
            chopTime = true;
            controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
            controller.displayMessage("@red@Saving position");
            saveX = controller.currentX();
            saveY = controller.currentY();
            bankSelX = bankX[destination.getSelectedIndex()];
            bankSelY = bankY[destination.getSelectedIndex()];
            controller.displayMessage("@red@Woodcutter started");
          }
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(treeLabel);
    scriptFrame.add(treeField);
    scriptFrame.add(bank);
    scriptFrame.add(destination);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      controller.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@Woodcutter @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Logs Collected: @yel@" + String.valueOf(this.totalLogs), 10, 21 + 14, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Logs in bank: @yel@" + String.valueOf(this.bankedLogs),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
}