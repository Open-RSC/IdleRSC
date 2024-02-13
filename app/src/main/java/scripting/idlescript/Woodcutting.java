package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;
import models.entities.SceneryId;

/**
 * <b>AIO Woodcutting Bot</b>
 *
 * <p>Does banking automatically<br>
 *
 * @author Searos, Kaila
 */
public class Woodcutting extends IdleScript {
  private static final Controller c = Main.getController();
  private final JCheckBox bank = new JCheckBox("Bank", true);
  private final JComboBox<String> destination =
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
  private JFrame scriptFrame = null;
  private boolean guiSetup = false;
  private boolean scriptStarted = false;
  private int treeId = -1;
  private int logId = -1;
  private final int[] treeIds = {
    SceneryId.LEAFY_TREE.getId(),
    SceneryId.TREE_OAK.getId(),
    SceneryId.TREE_WILLOW.getId(),
    SceneryId.TREE_MAPLE.getId(),
    SceneryId.TREE_YEW.getId(),
    SceneryId.TREE_MAGIC.getId()
  };
  private final int[] logIds = {
    ItemId.LOGS.getId(),
    ItemId.OAK_LOGS.getId(),
    ItemId.WILLOW_LOGS.getId(),
    ItemId.MAPLE_LOGS.getId(),
    ItemId.YEW_LOGS.getId(),
    ItemId.MAGIC_LOGS.getId()
  };
  private int treesX = 0;
  private int treesY = 0;
  private int bankSelX = -1;
  private int bankSelY = -1;
  private int totalLogs = 0;
  private int bankedLogs = 0;
  private int inventLogs = 0;
  private final int[] bankX = {220, 150, 103, 220, 216, 283, 503, 582, 566, 588};
  private final int[] bankY = {635, 504, 511, 365, 450, 569, 452, 576, 600, 754};
  private boolean bankTime = false;
  private boolean chopTime = false;
  private final int[] bankerIds = {95, 224, 268, 485, 540, 617};

  private final int[] axes = {12, 87, 88, 203, 204, 405, 1263};

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
      c.setBatchBarsOn();
      scriptStart();
    }

    return 1000; // start() must return an int value now.
  }

  private void startWalking(int x, int y) {
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

  private boolean isAxe(int id) {
    for (int axe : axes) {
      if (axe == id) return true;
    }
    return false;
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount() == 30) {
        bankTime = true;
        chopTime = false;
      }
      if (c.getInventoryItemCount() <= 29) {
        bankTime = false;
      }

      if (c.getNearestObjectById(treeId) != null && chopTime && !c.isBatching()) {
        c.setStatus("@red@Clicking tree");
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        int[] treeCoords = c.getNearestObjectById(treeId);
        c.atObject(treeCoords[0], treeCoords[1]);
        c.sleep(1200); // more sleep to let batching catch up!
        inventLogs = c.getInventoryItemCount(logId);
        c.waitForBatching(true);
      } else { // added else so when getNearestObjectById == null this function doesn't repeat
        // and
        c.setStatus("@red@No trees!");
        c.sleep(
            2000); // added sleep to this function to stop cpu overflow issue going to high usage
      }

      if (bank.isSelected() && bankTime) {
        while (c.getNearestNpcByIds(bankerIds, true) == null) {
          c.setStatus("@red@Going bank");
          startWalking(bankSelX, bankSelY);
        }
        if (!c.isInBank()) {
          c.setStatus("@red@Opening bank");
          c.openBank();
          c.sleep(100);
        }
      }

      if (c.isInBank()) {
        c.setStatus("@red@Banking");
        totalLogs = totalLogs + c.getInventoryItemCount(logId);
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 0 && !isAxe(itemId) && itemId != 1263) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
          c.sleep(100);
        }
        bankedLogs = c.getBankItemCount(logId);
        inventLogs = c.getInventoryItemCount(logId);
        c.sleep(100);
        c.closeBank();
        bankTime = false;
        c.sleep(1000);
      }
      if (c.getInventoryItemCount() == 0 && c.isInBank()) {
        c.setStatus("@red@Closing bank");
        c.closeBank();
      }
      if (!bankTime && !chopTime) {
        c.setStatus("@red@Walking to trees");
        if (c.getNearestObjectById(treeId) == null) {
          startWalking(treesX, treesY);
          c.sleep(
              340); // added sleep, this one probably not needed, but small sleep after pathwalking
          // is fine
        } else {
          chopTime = true;
        }
      }
    }
    scriptStarted = false;
    guiSetup = false;
  }

  private void setupGUI() {
    JLabel header = new JLabel("Woodcutting");
    JLabel treeLabel = new JLabel("Tree Type:");
    JComboBox<String> treeField =
        new JComboBox<>(new String[] {"Normal", "Oak", "Willow", "Maple", "Yew", "Magic"});
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          treeId = treeIds[treeField.getSelectedIndex()];
          logId = logIds[treeField.getSelectedIndex()];
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          chopTime = true;
          c.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
          c.displayMessage(
              "@red@Saving position - This is where I'll cut "
                  + treeField.getSelectedItem()
                  + " trees");
          treesX = c.currentX();
          treesY = c.currentY();
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
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      c.drawBoxAlpha(7, 7, 132, 21 + 14 + 14, 0xFF0000, 64);
      c.drawString("@red@Woodcutter @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      c.drawString(
          "@red@Logs Collected: @yel@" + (totalLogs + inventLogs), 10, 21 + 14, 0xFFFFFF, 1);
      c.drawString("@red@Logs in bank: @yel@" + bankedLogs, 10, 21 + 14 + 14, 0xFFFFFF, 1);
    }
  }
}
