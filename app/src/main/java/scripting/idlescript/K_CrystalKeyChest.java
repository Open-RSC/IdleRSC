package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * <b>Crystal Key Chest Opener.</b>
 *
 * <p>Start by Crystal chest or in Catherby Bank! Only works on Coleslaw for the time Being.
 * Utilizes the White Wolf Mountain Agility Shortcut.
 *
 * @see scripting.idlescript.K_kailaScript
 * @author kaila.
 */
/*
 * todo add, "if fail agility shortcut" fallback? - currently bot may break.
 */
public final class K_CrystalKeyChest extends K_kailaScript {
  private static int totalDragonstones = 0;
  private static int KeysInBank = 0;
  private static int DragonstonesInBank = 0;
  private static final int[] loot = {542, 408, 526, 527};
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
      c.displayMessage("@gre@Crystal Key Chest Opener - By Kaila");
      c.displayMessage("@gre@Start by Crystal chest or in Catherby Bank!");

      if (c.isInBank()) c.closeBank();
      if (c.currentX() > 400) {
        bank();
        BankToGrape();
        c.sleep(1380);
      }
      scriptStart();
    }

    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      lootScript();
      int[] coords = c.getNearestItemById(542); // always pick up keys
      if (coords != null) {
        c.setStatus("@yel@Looting Key..");
        c.dropItem(c.getInventoryItemSlotIndex(179));
        c.pickupItem(coords[0], coords[1], 542, true, true);
        c.sleep(618);
      }
      if (c.getInventoryItemCount() == 30) {
        if (c.getInventoryItemCount(35) > 0) {
          c.dropItem(c.getInventoryItemSlotIndex(35));
          c.sleep(400);
        }
        if (c.getInventoryItemCount(36) > 0) {
          c.dropItem(c.getInventoryItemSlotIndex(36));
          c.sleep(400);
        }
        if (c.getInventoryItemCount(33) > 0) {
          c.dropItem(c.getInventoryItemSlotIndex(33));
          c.sleep(400);
        }
        if (c.getInventoryItemCount(34) > 0) {
          c.dropItem(c.getInventoryItemSlotIndex(34));
          c.sleep(400);
        }
        if (c.getInventoryItemCount(32) > 0) {
          c.dropItem(c.getInventoryItemSlotIndex(32));
          c.sleep(400);
        }
        if (c.getInventoryItemCount(31) > 0) {
          c.dropItem(c.getInventoryItemSlotIndex(31));
          c.sleep(400);
        }
        if (c.getInventoryItemCount() == 30) {
          c.setStatus("@gre@Banking..");
          GrapeToBank();
          bank();
          BankToGrape();
          c.sleep(618);
        }
      }
      if (c.getInventoryItemCount(526) > 0 && c.getInventoryItemCount(527) > 0) {
        c.setStatus("@gre@Combining Keys..");
        c.useItemOnItemBySlot(c.getInventoryItemSlotIndex(526), c.getInventoryItemSlotIndex(527));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(525) == 0) {
        c.setStatus("@gre@No Keys, Banking..");
        GrapeToBank();
        bank();
        BankToGrape();
        c.sleep(618);
      }
      if (c.getInventoryItemCount() < 30 && c.getInventoryItemCount(525) > 0) {
        c.setStatus("@gre@Using Key on Chest..");
        c.useItemIdOnObject(367, 497, 525);
        c.sleep(2000);
      }
    }
  }

  private void lootScript() {
    for (int lootId : loot) {
      int[] coords = c.getNearestItemById(lootId);
      if (coords != null) {
        c.setStatus("@yel@Looting..");
        c.pickupItem(coords[0], coords[1], lootId, true, true);
        c.sleep(618);
      }
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalDragonstones = totalDragonstones + c.getInventoryItemCount(542);
      for (int itemId : c.getInventoryItemIds()) {
        c.depositItem(itemId, c.getInventoryItemCount(itemId));
      }
      c.sleep(1400); // increased sleep here to prevent double banking

      if (c.getInventoryItemCount(525) < 14) { // crystal keys
        c.withdrawItem(525, 14 - c.getInventoryItemCount(525));
        c.sleep(1000);
      }
      KeysInBank = c.getBankItemCount(525);
      DragonstonesInBank = c.getBankItemCount(542);

      c.closeBank();
    }
  }

  private void GrapeToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(371, 495);
    c.walkTo(375, 498);
    c.walkTo(385, 498);
    c.walkTo(389, 502);
    c.walkTo(394, 502);
    c.sleep(340);
    c.atObject(395, 502); // agility shortcut
    c.sleep(4000);
    c.walkTo(397, 501);
    c.walkTo(406, 501);
    c.walkTo(409, 497);
    c.walkTo(419, 497);
    c.walkTo(429, 497);
    c.walkTo(439, 497);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToGrape() {
    c.setStatus("@gre@Walking to Crystal Chest..");
    c.walkTo(439, 497);
    c.walkTo(429, 497);
    c.walkTo(419, 497);
    c.walkTo(409, 497);
    c.walkTo(406, 501);
    c.walkTo(397, 501);
    c.sleep(340);
    c.atObject(397, 502); // agility shortcut
    c.sleep(4000);
    c.walkTo(389, 502);
    c.walkTo(385, 498);
    c.walkTo(375, 498);
    c.walkTo(371, 495);
    c.walkTo(367, 496);
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Crystal Key Chest Opener - By Kaila");
    JLabel label1 = new JLabel("Start by Crystal chest or in Catherby Bank!");
    JLabel label2 = new JLabel("Only works on Coleslaw for the time Being");
    JLabel label3 = new JLabel("Utilizes the White Wolf Mountain Agility Shortcut");
    JButton startScriptButton = new JButton("Start Script");
    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          startTime = System.currentTimeMillis();
          scriptStarted = true;
        });
    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
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
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int DragonstoneSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        DragonstoneSuccessPerHr = (int) (totalDragonstones * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      c.drawString("@red@Crystal Key Chest Opener @whi@~ @mag@Kaila", 310, 48, 0xFFFFFF, 1);
      c.drawString("@whi@Dragonstones in Bank: @gre@" + DragonstonesInBank, 320, 62, 0xFFFFFF, 1);
      c.drawString("@whi@Crystal Keys in Bank: @gre@" + KeysInBank, 320, 76, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Dragonstones: @gre@"
              + totalDragonstones
              + "@yel@ (@whi@"
              + String.format("%,d", DragonstoneSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          320,
          90,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          320,
          104,
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, 320, 118, 0xFFFFFF, 1);
    }
  }
}
