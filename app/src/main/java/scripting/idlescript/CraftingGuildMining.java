package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;

/**
 * MineGoldCraftinGuild by Searos
 *
 * @author Searos bugfixes by kaila
 */
public class CraftingGuildMining extends IdleScript {
  private static final Controller c = Main.getController();
  private final int[] gold = {112, 113};
  private final int[] clay = {114, 115};
  private final int[] silver = {195, 196}; // remove 197
  private int bankedSilver = 0;
  private int bankedGold = 0;
  private int bankedClay = 0;
  private int minedSilver = 0;
  private int minedGold = 0;
  private int minedClay = 0;
  private final int DEW_CROWN = ItemId.CROWN_OF_DEW.getId();
  private JFrame scriptFrame = null;
  private boolean useDewCrown = false;
  private boolean guiSetup = false;
  private boolean timeToBank = false;
  private boolean scriptStarted = false;
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
    while (c.isRunning() && scriptStarted) {
      scriptStart();
    }
    return 1000; // start() must return a int value now.
  }

  public void startWalking(int x, int y) {
    // shitty autowalk
    int newX = x;
    int newY = y;
    while (c.currentX() != x || c.currentY() != y) {
      if (c.currentX() - x > 23) {
        newX = c.currentX() - 20;
      }
      if (c.currentY() - y > 23) {
        newY = c.currentY() - 20;
      }
      if (c.currentX() - x < -23) {
        newX = c.currentX() + 20;
      }
      if (c.currentY() - y < -23) {
        newY = c.currentY() + 20;
      }
      if (Math.abs(c.currentX() - x) <= 23) {
        newX = x;
      }
      if (Math.abs(c.currentY() - y) <= 23) {
        newY = y;
      }
      if (!c.isTileEmpty(newX, newY)) {
        c.walkToAsync(newX, newY, 2);
        c.sleep(640);
      } else {
        c.walkToAsync(newX, newY, 0);
        c.sleep(640);
      }
    }
  }

  public void scriptStart() {
    // this is for gold rocks
    if (miningMode == 0 // just mine gold
        || (miningMode == 1 // mine gold then silver
                && c.getNearestObjectById(112) != null
                && c.getNearestObjectById(113) != null) // has rocks
            && c.getInventoryItemCount() < 30) {
      c.setStatus("Mining Gold");
      for (int objId : gold) {
        if (objId != 0) {
          if (c.getInventoryItemCount() < 30 && c.getNearestObjectById(objId) != null) {
            c.atObject(c.getNearestObjectById(objId)[0], c.getNearestObjectById(objId)[1]);
            c.waitForBatching(true);
          }
        }
      }
    } else {
      c.sleep(340); // should fix high cpu when all rocks depleted
    }
    // this is for silver rocks
    if (((miningMode == 1 // mine gold then silver
                && c.getNearestObjectById(112) == null // no gold rocks avail
                && c.getNearestObjectById(113) == null)
            || (miningMode == 2) // just mine silver, don't check gold ore or clay
            || (miningMode == 3 // mine silver, then clay (don't check clay)
                && c.getNearestObjectById(195) != null // has rocks
                && c.getNearestObjectById(196) != null))
        && c.getInventoryItemCount() < 30) { // also check inv
      c.setStatus("Mining Silver");
      for (int objId : silver) {
        if (objId != 0) {
          if (c.getInventoryItemCount() < 30 && c.getNearestObjectById(objId) != null) {
            c.atObject(c.getNearestObjectById(objId)[0], c.getNearestObjectById(objId)[1]);
            c.sleep(1280);
            c.waitForBatching(true);
          }
        }
      }
    } else {
      c.sleep(340); // should fix high cpu when all rocks depleted
    }
    // this is for mining clay
    if (((miningMode == 3 // mine silver then clay
                && c.getNearestObjectById(195) == null // no silver rocks avail
                && c.getNearestObjectById(196) == null)
            || (miningMode == 4)) // just mine clay, don't check gold ore or silver
        && c.getInventoryItemCount() < 30
        && !timeToBank) { // also check inv
      c.setStatus("Mining Clay");
      if (useDewCrown && !c.isItemIdEquipped(DEW_CROWN) && c.isItemInInventory(DEW_CROWN)) {
        c.equipItem(c.getInventoryItemSlotIndex(DEW_CROWN));
        c.sleep(640);
      }
      for (int objId : clay) {
        if (objId != 0) {
          if (c.getInventoryItemCount() < 30 && c.getNearestObjectById(objId) != null) {
            c.atObject(c.getNearestObjectById(objId)[0], c.getNearestObjectById(objId)[1]);
            c.sleep(1280);
            c.waitForBatching(true);
          }
        }
      }
    } else {
      c.sleep(340); // should fix high cpu when all rocks depleted
    }
    if (timeToBank || (!c.isAuthentic() && c.getInventoryItemCount() == 30)) {
      c.setStatus("Banking");
      timeToBank = false;
      if (c.getInventoryItemCount() == 30 && c.getNearestObjectById(942) != null) {
        if (!c.isInBank()) {
          c.atObject(c.getNearestObjectById(942)[0], c.getNearestObjectById(942)[1]);
          c.sleep(640);
        }
        if (c.isInBank()) {
          minedSilver = minedSilver + c.getInventoryItemCount(383);
          minedGold = minedGold + c.getInventoryItemCount(152);
          minedClay = minedClay + c.getInventoryItemCount(149);
          if (c.getInventoryItemCount() > 1) {
            for (int itemId : c.getInventoryItemIds()) {
              if (itemId != 0) {
                c.depositItem(itemId, c.getInventoryItemCount(itemId));
              }
            }
          }
          if (useDewCrown && !c.isItemIdEquipped(DEW_CROWN) && c.getBankItemCount(DEW_CROWN) > 0) {
            c.withdrawItem(DEW_CROWN, 1);
            c.sleep(640);
          }
          bankedSilver = c.getBankItemCount(383);
          bankedGold = c.getBankItemCount(152);
          bankedClay = c.getBankItemCount(149);
          c.closeBank();
        }
      }
    }

    if (c.isAuthentic() && c.getInventoryItemCount() == 30) {
      c.setStatus("Leaving Guild");
      while (c.currentY() > 600) {
        c.openDoor(347, 601);
        c.sleep(430);
      }
      while (c.getNearestNpcById(95, false) == null) {
        c.setStatus("Walking to Bank");
        startWalking(c.getNearestBank()[0], c.getNearestBank()[1]);
      }
      while (c.getNearestNpcById(95, false) != null && c.getInventoryItemCount() == 30) {
        c.setStatus("Banking");
        while (!c.isInBank()) {
          c.openBank();
          c.sleep(640);
        }
        while (c.isInBank() && c.getInventoryItemCount() > 1) {
          for (int itemId : c.getInventoryItemIds()) {
            if (itemId != 0) {
              c.depositItem(itemId, c.getInventoryItemCount(itemId));
            }
          }
          bankedSilver = c.getBankItemCount(383);
          bankedGold = c.getBankItemCount(152);
        }
      }
      while (c.currentX() != 347 && c.currentY() != 600) {
        c.setStatus("Walking to Guild");
        startWalking(347, 600);
      }
      while (c.currentY() < 601) {
        c.setStatus("Entering guild");
        c.openDoor(347, 601);
        c.sleep(430);
      }
    }
    c.sleep(640);
  }
  // the crown of dew shatters
  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("The crown of dew shatters")) {
      timeToBank = true; // bank if crown breaks
    }
  }

  private int miningMode = 0;

  public void setupGUI() {
    JLabel header = new JLabel("Mine rocks at crafting guild ");
    JLabel blankLabel = new JLabel("and bank at the nearby chest");
    JLabel miningModeLabel = new JLabel("\tMining Mode:");
    JComboBox<String> miningModeField =
        new JComboBox<>(
            new String[] {
              "Only Mine Gold",
              "Mine Gold, then Silver",
              "Only Mine Silver",
              "Mine Silver, then Clay",
              "Only Mine Clay"
            });
    JCheckBox dewCrownCheckBox = new JCheckBox("Use crown of Dew?");
    JButton startScriptButton = new JButton("Start");

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(blankLabel);
    scriptFrame.add(miningModeLabel);
    scriptFrame.add(miningModeField);
    scriptFrame.add(dewCrownCheckBox);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();

    dewCrownCheckBox.setEnabled(false);

    miningModeField.addActionListener(
        e -> {
          dewCrownCheckBox.setEnabled(
              miningModeField.getSelectedIndex() == 3 || miningModeField.getSelectedIndex() == 4);
        });

    startScriptButton.addActionListener(
        e -> {
          miningMode = miningModeField.getSelectedIndex(); // 0 gold,
          useDewCrown = dewCrownCheckBox.isSelected();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          c.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos and Kaila");
          c.displayMessage("@whi@MineGold started");
        });
  }

  @Override
  public void paintInterrupt() {
    if (controller
        != null) { // 0 = mine gold, 1 = gold then silver, 2 = silver, 3 = silver then clay, 4 =
      // clay
      c.drawString(
          "@gre@MineCraftingGuild by @cya@Searos @gre@and @mag@Kaila", 10, 21 - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", 10, 21, 0xFFFFFF, 1);
      if (miningMode == 0) { // mine gold
        c.drawString("@whi@Gold in Bank: @yel@" + bankedGold, 10, 35, 0xFFFFFF, 1);
        c.drawString("@whi@Mined Gold: @yel@" + minedGold, 10, 49, 0xFFFFFF, 1);
      } else if (miningMode == 1) { // gold then silver
        c.drawString("@whi@Gold in Bank: @yel@" + bankedGold, 10, 35, 0xFFFFFF, 1);
        c.drawString("@whi@Mined Gold: @yel@" + minedGold, 10, 35 + 14, 0xFFFFFF, 1);
        c.drawString("@whi@Silver in bank: @yel@" + bankedSilver, 10, 35 + (14 * 2), 0xFFFFFF, 1);
        c.drawString("@whi@Mined Silver: @yel@" + minedSilver, 10, 35 + (14 * 3), 0xFFFFFF, 1);
      } else if (miningMode == 2) { // just silver
        c.drawString("@whi@Silver in bank: @yel@" + bankedSilver, 10, 35, 0xFFFFFF, 1);
        c.drawString("@whi@Mined Silver: @yel@" + minedSilver, 10, 35 + 14, 0xFFFFFF, 1);
      } else if (miningMode == 3) { // silver then clay
        c.drawString("@whi@Silver in bank: @yel@" + bankedSilver, 10, 35, 0xFFFFFF, 1);
        c.drawString("@whi@Mined Silver: @yel@" + minedSilver, 10, 35 + 14, 0xFFFFFF, 1);
        c.drawString("@whi@Clay in bank: @yel@" + bankedClay, 10, 35 + (14 * 2), 0xFFFFFF, 1);
        c.drawString("@whi@Mined Clay: @yel@" + minedClay, 10, 35 + (14 * 3), 0xFFFFFF, 1);
      } else if (miningMode == 4) {
        c.drawString("@whi@Clay in bank: @yel@" + bankedClay, 10, 35, 0xFFFFFF, 1);
        c.drawString("@whi@Mined Clay: @yel@" + minedClay, 10, 35 + 14, 0xFFFFFF, 1);
      }
    }
  }
}
