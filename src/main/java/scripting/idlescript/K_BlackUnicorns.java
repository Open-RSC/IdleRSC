package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import orsc.ORSCharacter;

/**
 * Black Unicorn Killer - By Kaila.
 *
 * <p>
 *
 * <p>This bot supports the \"autostart\" parameter.
 *
 * <p>Defaults to Teleport Off, Return On.
 *
 * <p>
 *
 * <p>Start in Edge bank or Uni's with Gear.
 *
 * <p>Sharks IN BANK REQUIRED.
 *
 * <p>Teleport if Pkers Attack Option.
 *
 * <p>31 Magic, Laws, Airs, and Earths required for Escape Tele.
 *
 * <p>Unselected, bot WALKS to Edge when Attacked.
 *
 * <p>Selected, bot walks to 19 wildy and teleports.
 *
 * <p>Return to Hobs Mine after Escaping?", true.
 *
 * <p>Unselected, bot will log out after escaping Pkers.
 *
 * <p>Selected, bot will grab more food and return.
 *
 * <p>@Author - Kaila
 */
public class K_BlackUnicorns extends K_kailaScript {
  private static boolean teleportOut = false;
  private static boolean returnEscape = true;
  private static int uniInBank = 0;
  private static int totalUni = 0;

  private void startSequence() {
    c.displayMessage("@red@Black Unicorn Killer ~ By Kaila");
    c.displayMessage("@red@Start in Edge bank with Armor");
    c.displayMessage("@red@Sharks IN BANK REQUIRED");
    c.displayMessage("@red@31 Magic Required for escape tele");
    //			bank();
    if (c.isInBank()) {
      c.closeBank();
    }
    if (c.currentY() > 340) {
      bank();
      eat();
      BankToUni();
      c.sleep(1380);
    }
  }

  public int start(String[] parameters) {

    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        guiSetup = true;
        c.displayMessage("Auto-starting, teleport false, return escape true", 0);
        System.out.println("Auto-starting, teleport false, return escape true");
        teleportOut = false;
        returnEscape = true;
        scriptStarted = true;
      }
    }
    if (scriptStarted) {
      guiSetup = true;
      startTime = System.currentTimeMillis();
      startSequence();
      scriptStart();
    }
    if (!scriptStarted && !guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      eat();
      if (c.getInventoryItemCount() < 30) {
        lootScript();
        if (!c.isInCombat()) {
          c.setStatus("@yel@Attacking..");
          c.sleepHandler(296, true);
          ORSCharacter npc = c.getNearestNpcById(296, false);
          if (npc != null) {
            c.attackNpc(npc.serverIndex);
            c.sleep(3000);
          } else {
            lootBones();
            c.sleep(640);
          }
        }
      } else if (c.getInventoryItemCount() == 30) {
        buryBones();
        if (c.getInventoryItemCount() == 30) {
          c.setStatus("@yel@Banking..");
          UniToBank();
          bank();
          BankToUni();
          c.sleep(618);
        }
      }
    }
  }

  private void lootBones() {
    for (int lootId : bones) {
      try {
        int[] coords = c.getNearestItemById(lootId);
        if (coords != null && !c.isInCombat()) {
          c.setStatus("@yel@No NPCs, Picking bones");
          c.walkToAsync(coords[0], coords[1], 0);
          c.pickupItem(coords[0], coords[1], lootId, true, false);
          c.sleep(640);
          buryBones();
        } else {
          c.sleep(300);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void lootScript() {
    try {
      int[] coords = c.getNearestItemById(466);
      if (coords != null) {
        c.setStatus("@yel@Looting..");
        c.walkToAsync(coords[0], coords[1], 0);
        c.pickupItem(coords[0], coords[1], 466, true, false);
        c.sleep(640);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void bank() {

    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);

    if (c.isInBank()) {
      totalUni = totalUni + c.getInventoryItemCount(466);

      if (c.getInventoryItemCount(466) > 0) { // deposit the uni horns
        c.depositItem(466, c.getInventoryItemCount(466));
        c.sleep(340);
      }
      if (teleportOut) {
        if (c.getInventoryItemCount(33) < 3) { // withdraw 3 air
          c.withdrawItem(33, 3);
          c.sleep(340);
        }
        if (c.getInventoryItemCount(34) < 1) { // withdraw 1 earth
          c.withdrawItem(34, 1);
          c.sleep(340);
        }
        if (c.getInventoryItemCount(42) < 1) { // withdraw 1 law
          c.withdrawItem(42, 1);
          c.sleep(340);
        }
      }
      if (c.getInventoryItemCount(546) > 1) { // deposit extra shark
        c.depositItem(546, c.getInventoryItemCount(546) - 1);
        c.sleep(340);
      }
      if (c.getInventoryItemCount(546) < 1) { // withdraw 1 shark
        c.withdrawItem(546, 1);
        c.sleep(340);
      }
      if (c.getBankItemCount(546) == 0) {
        c.setStatus("@red@NO Sharks in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      uniInBank = c.getBankItemCount(466);
      c.closeBank();
      c.sleep(640);
    }
  }

  private void eat() {
    int eatLvl = c.getBaseStat(c.getStatId("Hits")) - 20;

    if (c.getCurrentStat(c.getStatId("Hits")) < eatLvl) {
      leaveCombat();
      c.setStatus("@red@Eating..");
      boolean ate = false;
      for (int id : c.getFoodIds()) {
        if (c.getInventoryItemCount(id) > 0) {
          c.itemCommand(id);
          c.sleep(700);
          ate = true;
          break;
        }
      }
      if (!ate) { // only activates if hp goes to -20 again THAT trip, will bank and get new shark
        // usually
        if (!teleportOut
            || c.getInventoryItemCount(42) < 1
            || c.getInventoryItemCount(33) < 3
            || c.getInventoryItemCount(34) < 1) { // or no earths/airs/laws
          c.setStatus("@yel@Banking..");
          UniToBank();
          bank();
          BankToUni();
          c.sleep(618);
        }
        if (teleportOut) {
          c.setStatus("@red@We've ran out of Food! Teleporting Away!.");
          goToTwenty();
          c.setStatus("@red@Teleporting Now!.");
          teleportOut();
          c.walkTo(120, 644);
          c.atObject(119, 642);
          c.walkTo(217, 447);
        }
        if (!returnEscape) {
          c.setAutoLogin(
              false); // uncomment and remove bank and banktoHobs to prevent bot going back to mine
          // after being attacked
          c.logout();
          c.sleep(1000);

          if (!c.isLoggedIn()) {
            c.stop();
            c.logout();
          }
        }
        if (returnEscape) {
          bank();
          BankToUni();
          c.sleep(618);
        }
      }
    }
  }

  private void UniToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(121, 311);
    c.walkTo(131, 321);
    c.walkTo(135, 326);
    c.walkTo(145, 336);
    c.walkTo(146, 340);
    c.walkTo(158, 352);
    c.walkTo(175, 369);
    c.walkTo(183, 372);
    c.walkTo(199, 388);
    c.walkTo(205, 393);
    c.walkTo(216, 405);
    c.walkTo(216, 426);
    c.walkTo(220, 440);
    c.walkTo(218, 447);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
    c.sleep(640);
  }

  private void BankToUni() {
    c.setStatus("@gre@Walking to Unicorns..");
    c.walkTo(220, 440);
    c.walkTo(216, 426);
    c.walkTo(216, 405);
    c.walkTo(205, 393);
    c.walkTo(199, 388);
    c.walkTo(183, 372);
    c.walkTo(175, 369);
    c.walkTo(158, 352);
    c.walkTo(146, 340);
    c.walkTo(145, 336);
    c.walkTo(135, 326);
    c.walkTo(131, 321);
    c.walkTo(121, 311);
    c.setStatus("@gre@Done Walking..");
    c.sleep(640);
  }

  private void goToTwenty() {
    c.setStatus("@red@Going to 19 Wildy (1).");
    c.walkTo(119, 314);
    c.sleep(400);
    for (int i = 1; i <= 8; i++) {
      if (c.currentY() < 314) {
        c.setStatus("@red@Going to 19 Wildy (n).");
        c.walkTo(119, 314);
        c.sleep(400);
      }
      c.sleep(10);
    }
  }

  private void teleportOut() {
    c.setStatus("@gre@Going to Bank. Casting teleport.");
    c.castSpellOnSelf(c.getSpellIdFromName("Lumbridge Teleport"));
    c.sleep(1000);
    for (int i = 1; i <= 10; i++) {
      if (c.currentY() < 420) {
        c.setStatus("@gre@Going to Bank. Casting teleport.");
        c.castSpellOnSelf(c.getSpellIdFromName("Lumbridge Teleport"));
        c.sleep(1000);
      } else {
        c.setStatus("@gre@Done teleporting..");
        break;
      }
      c.sleep(10);
    }
  }

  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("Black Unicorn Killer ~ By Kaila");
    JLabel label1 = new JLabel("Start in Edge bank or Uni's with Gear");
    JLabel label2 = new JLabel("Sharks IN BANK REQUIRED");
    JCheckBox teleportCheckbox = new JCheckBox("Teleport if Pkers Attack?", false);
    JLabel label3 = new JLabel("31 Magic, Laws, Airs, and Earths required for Escape Tele");
    JLabel label4 = new JLabel("Unselected, bot WALKS to Edge when Attacked");
    JLabel label5 = new JLabel("Selected, bot walks to 19 wildy and teleports");
    JCheckBox escapeCheckbox = new JCheckBox("Return to Hobs Mine after Escaping?", true);
    JLabel label6 = new JLabel("Unselected, bot will log out after escaping Pkers");
    JLabel label7 = new JLabel("Selected, bot will grab more food and return");
    JLabel label8 = new JLabel("This bot supports the \"autostart\" parameter");
    JLabel label9 = new JLabel("Defaults to Teleport Off, Return On.");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          teleportOut = teleportCheckbox.isSelected();
          returnEscape = escapeCheckbox.isSelected();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
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
    scriptFrame.add(label8);
    scriptFrame.add(label9);
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
      long timeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (totalUni * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);

      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Black Unicorns @mag@~ by Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Horns in Bank: @gre@" + uniInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Horns Picked: @gre@"
              + totalUni
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
