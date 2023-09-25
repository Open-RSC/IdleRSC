package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * <b>Red Spider Eggs</b>
 *
 * <p>Grabs red spider eggs in edge dungeon, recommend very high stats ~90+ and good defensive
 * armor.<br>
 * Start in Edge bank with Armor. Sharks in bank REQUIRED.<br>
 * Escape Teleport<br>
 * 31 Magic, Laws, Airs, and Earths required for Escape Tele.<br>
 * Unselected, bot WALKS to Edge when Attacked. Selected, bot teleports, then walks to edge.<br>
 * Return to eggs<br>
 * Unselected, bot will log out after escaping Pkers.<br>
 * Selected, bot will grab more food and return.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_RedSpiderEggz extends K_kailaScript {
  private static boolean teleportOut = false;
  private static boolean returnEscape = true;
  private static int eggzInBank = 0;
  private static int totalEggz = 0;
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
      c.displayMessage("@red@Red Spider Egg Picker - By Kaila");
      c.displayMessage("@red@Start in Edge bank with Armor");
      c.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
      c.displayMessage("@red@31 Magic Required for escape tele");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() > 340 && c.currentY() < 500) { // fixed start area bug
        bank();
        BankToEgg();
        c.sleep(100);
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      boolean ate = eatFood();
      if (!ate) {
        escapeRoute();
      }
      leaveCombat();
      if (c.getInventoryItemCount() > 29 || c.getInventoryItemCount(546) == 0) {
        c.setStatus("@red@Banking..");
        EggToBank();
        bank();
        BankToEgg();
        c.sleep(618);
      }
      if (c.getNearestItemById(219) != null) {
        c.setStatus("@yel@Picking Eggs..");
        int[] coords = c.getNearestItemById(219);
        c.walkToAsync(coords[0], coords[1], 0);
        c.pickupItem(coords[0], coords[1], 219, true, false);
        c.sleep(1280);
      } else {
        c.sleep(1280);
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
      totalEggz = totalEggz + c.getInventoryItemCount(219);
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != 546) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1280);
      eggzInBank = c.getBankItemCount(219);

      if (c.getInventoryItemCount(546) > 1) { // deposit extra shark
        c.depositItem(546, c.getInventoryItemCount(546) - 1);
        c.sleep(340);
      }
      if (c.getInventoryItemCount(546) < 1) { // withdraw 1 shark
        c.withdrawItem(546, 1);
        c.sleep(340);
      }
      if (teleportOut) {
        if (c.getInventoryItemCount(33) < 3) { // withdraw 3 air
          c.withdrawItem(33, 3);
          c.sleep(640);
        }
        if (c.getInventoryItemCount(34) < 1) { // withdraw 1 earth
          c.withdrawItem(34, 1);
          c.sleep(640);
        }
        if (c.getInventoryItemCount(42) < 1) { // withdraw 1 law
          c.withdrawItem(42, 1);
          c.sleep(640);
        }
      }
      if (c.getBankItemCount(546) == 0) {
        c.setStatus("@red@NO Sharks in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      c.closeBank();
    }
  }

  private void escapeRoute() {
    c.setStatus("@red@We've ran out of Food! Running Away!.");
    if (!teleportOut
        || c.getInventoryItemCount(42) < 1
        || c.getInventoryItemCount(33) < 3
        || c.getInventoryItemCount(34) < 1) { // or no earths/airs/laws
      EggToBank();
      bank();
    }
    if (teleportOut) {
      teleportLumbridge();
      c.walkTo(120, 644);
      c.atObject(119, 642);
      c.walkTo(217, 447);
    }
    if (!returnEscape) {
      c.setAutoLogin(false);
      c.logout();
      c.sleep(1000);

      if (!c.isLoggedIn()) {
        c.stop();
        c.logout();
      }
    }
    if (returnEscape) {
      bank();
      BankToEgg();
      c.sleep(618);
    }
  }

  private void EggToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(197, 3244);
    c.walkTo(197, 3255);
    c.walkTo(196, 3265);
    c.setStatus("@gre@Opening Wildy Gate North to South(1)..");
    c.atObject(196, 3266);
    c.sleep(640);
    openEdgeDungGateNorthToSouth();
    c.walkTo(197, 3266);
    c.walkTo(204, 3272);
    c.walkTo(210, 3273);
    if (c.getObjectAtCoord(211, 3272) == 57) {
      c.setStatus("@gre@Opening Edge Gate..");
      c.walkTo(210, 3273);
      c.atObject(211, 3272);
      c.sleep(340);
    }
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(217, 3283);
    c.walkTo(215, 3294);
    c.walkTo(215, 3299);
    c.atObject(215, 3300);
    c.sleep(640);
    c.walkTo(217, 458);
    c.walkTo(221, 447);
    c.walkTo(217, 447); // outside bank door
    openDoorObjects(64, 217, 447); // open bank door
    c.sleep(640);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToEgg() {
    c.setStatus("@gre@Walking to Eggs..");
    c.walkTo(217, 448); // inside bank door
    openDoorObjects(64, 217, 447); // open bank door
    c.walkTo(221, 447);
    c.walkTo(217, 458);
    c.walkTo(215, 467);
    c.atObject(215, 468);
    c.sleep(640);
    c.walkTo(217, 3283);
    c.walkTo(211, 3273);
    if (c.getObjectAtCoord(211, 3272) == 57) {
      c.setStatus("@gre@Opening Edge Gate..");
      c.walkTo(211, 3273);
      c.atObject(211, 3272);
      c.sleep(340);
    }
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(204, 3272);
    c.walkTo(199, 3272);
    c.walkTo(197, 3266);
    c.setStatus("@gre@Opening Wildy Gate, South to North(1)..");
    c.atObject(196, 3266);
    c.sleep(640);
    openEdgeDungSouthToNorth();
    c.walkTo(197, 3244);
    c.walkTo(208, 3240);
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Red Spider Egg Picker @whi@~ @mag@Kaila");
    JLabel label1 = new JLabel("Start in Edge bank with Armor");
    JLabel label2 = new JLabel("Sharks in bank REQUIRED");
    JCheckBox teleportCheckbox = new JCheckBox("Teleport if Pkers Attack?", false);
    JLabel label3 = new JLabel("31 Magic, Laws, Airs, and Earths required for Escape Tele");
    JLabel label4 = new JLabel("Unselected, bot WALKS to Edge when Attacked");
    JLabel label5 = new JLabel("Selected, bot teleports, then walks to edge");
    JCheckBox escapeCheckbox = new JCheckBox("Return to Eggz after Escaping?", true);
    JLabel label6 = new JLabel("Unselected, bot will log out after escaping Pkers");
    JLabel label7 = new JLabel("Selected, bot will grab more food and return");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          teleportOut = teleportCheckbox.isSelected();
          returnEscape = escapeCheckbox.isSelected();
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
    scriptFrame.add(teleportCheckbox);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(escapeCheckbox);
    scriptFrame.add(label6);
    scriptFrame.add(label7);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {

      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (totalEggz * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);

      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@RedSpiderEggz @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Eggs in Bank: @gre@" + eggzInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Eggs Picked: @gre@"
              + totalEggz
              + "@yel@ (@whi@"
              + String.format("%,d", successPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 4), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 4), 0xFFFFFF, 1);
    }
  }
}
