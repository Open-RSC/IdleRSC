package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Woodcutting by Searos
 *
 * <p>Updated by Kaila
 *
 * <p>
 *
 * @author Searos
 *     <p>Kaila
 */
public class Woodcutting extends IdleScript {
  private static final Controller c = Main.getController();
  final JCheckBox bank = new JCheckBox("Bank", true);
  final JComboBox<String> destination =
      new JComboBox<>(
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
  final int[] treeIds = {1, 306, 307, 308, 309, 310};
  int saveX = 0;
  int saveY = 0;
  int bankSelX = -1;
  int bankSelY = -1;
  int totalLogs = 0;
  int bankedLogs = 0;
  final int[] bankX = {220, 150, 103, 220, 216, 283, 503, 582, 566, 588};
  final int[] bankY = {635, 504, 511, 365, 450, 569, 452, 576, 600, 754};
  boolean bankTime = false;
  boolean chopTime = false;
  final int[] bankerIds = {95, 224, 268, 485, 540, 617};

  final int[] axes = {12, 87, 88, 203, 204, 405, 1263};

  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }

    if (scriptStarted) {
      if (!c.isAuthentic() && !orsc.Config.C_BATCH_PROGRESS_BAR) c.toggleBatchBars();
      scriptStart();
    }

    return 1000; // start() must return an int value now.
  }

  public void startWalking(int x, int y) {
    // shitty autowalk
    int newX = x;
    int newY = y;
    while (c.currentX() != x || c.currentY() != y) {
      if (c.currentX() - x > 20) {
        newX = c.currentX() - 20;
      }
      if (c.currentY() - y > 20) {
        newY = c.currentY() - 20;
      }
      if (c.currentX() - x < -20) {
        newX = c.currentX() + 20;
      }
      if (c.currentY() - y < -20) {
        newY = c.currentY() + 20;
      }
      if (Math.abs(c.currentX() - x) <= 20) {
        newX = x;
      }
      if (Math.abs(c.currentY() - y) <= 20) {
        newY = y;
      }
      c.walkToAsync(newX, newY, 2);
      c.sleep(1000);
    }
  }

  public boolean isAxe(int id) {
    for (int axe : axes) {
      if (axe == id) return true;
    }
    return false;
  }

  public void scriptStart() {
    if (!c.isAuthentic() && !orsc.Config.C_BATCH_PROGRESS_BAR) c.toggleBatchBars();
    while (c.isRunning()) {
      if (c.getInventoryItemCount() == 30) {
        bankTime = true;
        chopTime = false;
      }
      if (c.getInventoryItemCount() <= 29) {
        bankTime = false;
      }
      if (c.getNearestObjectById(treeId) != null
          && chopTime
          && !c
              .isBatching()) { // bot spams  getNearestObjectById and goes to 25% cpu, this specific
        // one
        c.sleepHandler(98, true);
        int[] treeCoords = c.getNearestObjectById(treeId);
        c.atObject(treeCoords[0], treeCoords[1]);
        c.sleep(1200); // more sleep to let batching catch up!
        batchingWaitScript();
      } else { // added else so when getNearestObjectById == null this function doesn't repeat and
        // overflow cpu usage
        c.sleep(
            2000); // added sleep to this function to stop cpu overflow issue going to high usage
        // (IMPORTANT)
      }
      if (bank.isSelected() && bankTime) {
        while (c.getNearestNpcByIds(bankerIds, true) == null) {
          startWalking(bankSelX, bankSelY);
        }
        c.setStatus("@red@Banking");
        if (!c.isInBank()) { // changed from while to if, might fix occasional bank break?
          c.openBank();
          c.sleep(100);
        }
      }
      if (c.isInBank()) { // && c.getInventoryItemCount() > 0  //removed, not needed
        totalLogs = totalLogs + c.getInventoryItemCount(logId);
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 0 && !isAxe(itemId) && itemId != 1263) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
          c.sleep(100);
        }
        bankedLogs = c.getBankItemCount(logId);
        c.sleep(100);
        c.closeBank();
        bankTime = false;
        c.sleep(1000); // added
      }
      if (c.getInventoryItemCount() == 0 && c.isInBank()) {
        c.closeBank();
        c.sleep(100);
      }
      if (!bankTime && !chopTime) {
        // c.sleep(1000);
        if (c.getNearestObjectById(treeId) == null) { // changed to if
          startWalking(saveX, saveY);
          c.sleep(
              340); // added sleep, this one probably not needed, but small sleep after pathwalking
          // is fine
        } else { // changed too else to remove 2nd getNearestObjectById check
          // if (c.getNearestObjectById(treeId) != null) { //removed
          c.setStatus("@red@Chopping");
          chopTime = true;
        }
      }
    }
    scriptStarted = false;
    guiSetup = false;
  }

  public void batchingWaitScript() {
    while (c.isBatching() && c.getInventoryItemCount() < 30) {
      c.sleep(1000);
    }
  }

  public void setupGUI() {
    JLabel header = new JLabel("Woodcutting");
    JLabel treeLabel = new JLabel("Tree Type:");
    JComboBox<String> treeField =
        new JComboBox<>(new String[] {"Normal", "Oak", "Willow", "Maple", "Yew", "Magic"});
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          treeId = treeIds[treeField.getSelectedIndex()];
          logId = treeIds[treeField.getSelectedIndex()];
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          chopTime = true;
          c.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
          c.displayMessage("@red@Saving position");
          saveX = c.currentX();
          saveY = c.currentY();
          bankSelX = bankX[destination.getSelectedIndex()];
          bankSelY = bankY[destination.getSelectedIndex()];
          c.displayMessage("@red@Woodcutter started");
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
    if (c != null) {
      c.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
      c.drawString("@red@Woodcutter @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      c.drawString("@red@Logs Collected: @yel@" + totalLogs, 10, 21 + 14, 0xFFFFFF, 1);
      c.drawString("@red@Logs in bank: @yel@" + bankedLogs, 10, 21 + 14 + 14, 0xFFFFFF, 1);
    }
  }
}
