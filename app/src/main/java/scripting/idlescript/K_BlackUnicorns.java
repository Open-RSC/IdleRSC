package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Black Unicorn Killer - By Kaila.</b>
 *
 * <p>This bot supports the \"autostart\" parameter.<br>
 * Defaults to Teleport Off, Return On. <br>
 * Start in Edge bank or Uni's with Gear. <br>
 * Sharks IN BANK REQUIRED.<br>
 * Teleport if Pkers Attack Option.<br>
 * 31 Magic, Laws, Airs, and Earths required for Escape Tele.<br>
 * Unselected, bot WALKS to Edge when Attacked.<br>
 * Selected, bot walks to 19 wildy and teleports.<br>
 * Return to Hobs Mine after Escaping?", true.<br>
 * Unselected, bot will log out after escaping Pkers.<br>
 * Selected, bot will grab more food and return.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_BlackUnicorns extends K_kailaScript {
  private static final int UNI_HORN = ItemId.UNICORN_HORN.getId();
  private static final int SHARK = ItemId.SHARK.getId();
  private static boolean bankTeleport = false;
  private static boolean teleportOut = false;
  private static boolean returnEscape = false;
  private static int uniInBank = 0;
  private static int totalUni = 0;
  private static int inventUni = 0;

  private void startSequence() {
    c.displayMessage("@red@Black Unicorn Killer ~ By Kaila");
    c.displayMessage("@red@Start in Edge bank with Armor");
    c.displayMessage("@red@Sharks IN BANK REQUIRED");
    c.displayMessage("@red@31 Magic Required for escape tele");
    if (c.isInBank()) c.closeBank();
    if (c.currentY() > 340) {
      bank();
      BankToUni();
      c.sleep(1380);
    }
  }
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        guiSetup = true;
        c.displayMessage("Auto-starting, teleport false, return escape true", 0);
        System.out.println("Auto-starting, teleport false, return escape true");
        teleportOut = false;
        returnEscape = true;
        guiSetup = true;
        scriptStarted = true;
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      startSequence();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (!eatFood()
          || c.getInventoryItemCount() == 30
          || c.getInventoryItemCount(SHARK) == 0
          || timeToBank) {
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        UniToBank();
        bank();
        BankToUni();
        c.sleep(618);
      }
      lootItem(false, UNI_HORN);
      if (lootBones) lootItem(false, ItemId.BONES.getId());
      if (!c.isInCombat()) {
        ORSCharacter npc = c.getNearestNpcById(296, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking..");
          c.attackNpc(npc.serverIndex);
          inventUni = c.getInventoryItemCount(UNI_HORN);
          c.sleep(2 * GAME_TICK);
        } else c.sleep(GAME_TICK);
      } else c.sleep(GAME_TICK);
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(false, 1, ItemId.EMPTY_VIAL.getId());
        buryBonesToLoot(false);
      }
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(GAME_TICK);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalUni = totalUni + c.getInventoryItemCount(UNI_HORN);
      if (c.getInventoryItemCount(UNI_HORN) > 0) { // deposit the uni horns
        c.depositItem(UNI_HORN, c.getInventoryItemCount(UNI_HORN));
        c.sleep(340);
      }
      if (teleportOut || bankTeleport) {
        withdrawItem(airId, 3);
        withdrawItem(lawId, 1);
        withdrawItem(earthId, 1);
      }
      withdrawFood(SHARK, 1);
      bankItemCheck(SHARK, 5);
      inventoryItemCheck(airId, 3);
      inventoryItemCheck(lawId, 1);
      inventoryItemCheck(earthId, 1);
      uniInBank = c.getBankItemCount(UNI_HORN);
      c.closeBank();
      inventUni = c.getBankItemCount(UNI_HORN);
    }
  }

  private void escapePath() {
    if (!teleportOut
        || c.getInventoryItemCount(42) < 1
        || c.getInventoryItemCount(33) < 3
        || c.getInventoryItemCount(34) < 1) { // or no earths/airs/laws
      c.setStatus("@yel@Banking..");
      UniToBank();
      bank();
      BankToUni();
    }
    if (teleportOut || bankTeleport) {
      c.setStatus("@red@We've ran out of Food! Teleporting Away!.");
      goToTwenty();
      c.setStatus("@red@Teleporting Now!.");
      teleportLumbridge();
      c.walkTo(120, 644);
      c.atObject(119, 642);
      c.walkTo(217, 447);
    }
    if (!returnEscape) {
      endSession();
    }
    if (returnEscape) {
      bank();
      BankToUni();
      c.sleep(618);
    }
  }

  private void UniToBank() {
    c.setStatus("@gre@Walking to Bank..");
    boolean hasTeleportRunes =
        c.getInventoryItemCount(airId) >= 3
            && c.getInventoryItemCount(lawId) > 0
            && c.getInventoryItemCount(earthId) > 0;
    if (bankTeleport && hasTeleportRunes) {
      goToTwenty();
      c.setStatus("@red@Teleporting Now!.");
      teleportLumbridge();
      c.walkTo(120, 644);
      c.atObject(119, 642);
      c.walkTo(217, 447);
    } else {
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
    }
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
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

  private void setupGUI() {
    JLabel header = new JLabel("Black Unicorn Killer ~ By Kaila");
    JLabel label1 = new JLabel("Start in Edge bank or at Uni's with Gear");
    JLabel label2 = new JLabel("Sharks IN BANK REQUIRED");
    JLabel label3 = new JLabel("31 Magic, Laws, Airs, and Earths required for Escape Tele");
    JLabel label8 = new JLabel("This bot supports the \"autostart\" parameter");
    JCheckBox teleportCheckbox = new JCheckBox("Teleport if Pkers Attack?", true);
    JCheckBox escapeCheckbox = new JCheckBox("Return to Uni after Escaping?", true);
    JCheckBox bankTeleCheckbox = new JCheckBox("Use teleport to Bank?", true);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          teleportOut = teleportCheckbox.isSelected();
          bankTeleport = bankTeleCheckbox.isSelected();
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
    scriptFrame.add(label3);
    scriptFrame.add(label8);
    scriptFrame.add(teleportCheckbox);
    scriptFrame.add(escapeCheckbox);
    scriptFrame.add(bankTeleCheckbox);
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
      int successPerHr = 0;
      int TripSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) ((totalUni + inventUni) * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Black Unicorns @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Horns in Bank: @gre@" + uniInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Horns Picked: @gre@"
              + (totalUni + inventUni)
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
