package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;

/**
 * <b>Berry Harvester</b>
 *
 * <p>Harvests Berries from Edgeville Monastery (Coleslaw Only).<br>
 * Start in yanille Bank with Herb Clippers or near Berries. <br>
 * Recommend level 89+ combat so warriors are non aggressive. <br>
 * This bot supports the "autostart" parameter to automatiically start the bot without gui.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_HarvestWhiteBerry extends K_kailaScript {
  private boolean agilityCapeTeleport = false;
  private boolean lowlevel = false;
  private boolean ate = true;
  private static final int WHITE_BERRIES = ItemId.WHITE_BERRIES.getId();
  private static final int AGILITY_CAPE = ItemId.AGILITY_CAPE.getId();
  private static final int LOCKPICK = ItemId.LOCKPICK.getId();
  private static final int HERB_CLIPPERS = ItemId.HERB_CLIPPERS.getId();
  private static int BerryzInBank = 0;
  private static int totalBerryz = 0;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (parameters.length > 0 && !parameters[0].isEmpty()) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.displayMessage("Auto-starting, Picking Berries", 0);
        scriptStarted = true;
        guiSetup = true;
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
      next_attempt = System.currentTimeMillis() + 5000L;
      c.displayMessage("@red@White Berry Harvester - By Kaila");
      c.displayMessage("@red@Start in yanille Bank or near Berries");
      if (c.getBaseStat(c.getStatId("Agility")) < 77) endSession();
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 3000) {
        bank();
        BankToBerry();
      }
      c.toggleBatchBarsOn();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (lowlevel) ate = eatFood();
      if (c.getInventoryItemCount() == 30 || timeToBank || (lowlevel && !ate)) {
        c.setStatus("@red@Banking..");
        if (!ate) ate = true;
        BerryToBank();
        bank();
        BankToBerry();
      }
      c.setStatus("@yel@Picking Berries..");
      int[] coords = c.getNearestObjectById(1260);
      if (coords != null) {
        c.setStatus("@yel@Harvesting...");
        c.atObject(coords[0], coords[1]);
        c.sleep(2000);
        while (c.isBatching()
            && c.getInventoryItemCount() != 30
            && (next_attempt == -1 || System.currentTimeMillis() < next_attempt)) {
          c.sleep(GAME_TICK);
        }
      } else {
        c.setStatus("@yel@Waiting for spawn..");
        c.sleep(640);
      }
      if (System.currentTimeMillis() > next_attempt) {
        c.log("@red@Walking to Avoid Logging!");
        c.walkTo(c.currentX() - 1, c.currentY(), 0, true);
        c.sleep(640);
        next_attempt = System.currentTimeMillis() + nineMinutesInMillis;
        long nextAttemptInSeconds = (next_attempt - System.currentTimeMillis()) / 1000L;
        c.log("Done Walking to not Log, Next attempt in " + nextAttemptInSeconds + " seconds!");
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
      totalBerryz = totalBerryz + c.getInventoryItemCount(WHITE_BERRIES);
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != AGILITY_CAPE && itemId != LOCKPICK && itemId != HERB_CLIPPERS) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      if (agilityCapeTeleport) withdrawItem(AGILITY_CAPE, 1);
      withdrawItem(LOCKPICK, 1);
      if (c.getInventoryItemCount(HERB_CLIPPERS) < 1) { // withdraw herb clippers
        if (c.getBankItemCount(HERB_CLIPPERS) > 0) {
          c.withdrawItem(HERB_CLIPPERS, 1);
          c.sleep(GAME_TICK);
        } else {
          c.displayMessage("@red@You need herb clippers!");
        }
      }
      if (lowlevel) withdrawFood(foodId, foodWithdrawAmount);
      BerryzInBank = c.getBankItemCount(WHITE_BERRIES);
      c.closeBank();
      if (lowlevel) eatFood();
    }
  }

  private void BerryToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    if (agilityCapeTeleport && c.getInventoryItemCount(AGILITY_CAPE) != 0) {
      c.walkTo(608, 3568); // walkTo to exit batching
      teleportAgilityCape();
    } else {
      c.walkTo(608, 3568);
      c.atObject(607, 3568); // go through pipe  (make a loop)
      c.sleep(3000);
      c.walkTo(605, 3568);
      c.walkTo(603, 3568);
      c.walkTo(597, 3574);
      c.walkTo(597, 3581);
      c.sleep(300);
      c.atObject(598, 3582); // Rope Swing  (make a loop)
      c.sleep(3000);
      if (lowlevel) eatFood();
      c.walkTo(595, 3585);
      c.walkTo(593, 3587);
      c.walkTo(593, 3589);
      c.setStatus("@gre@Picklocking Door..");
      yanilleDungeonDoorExiting(); // open door
      c.setStatus("@gre@Walking to Bank..");
      c.walkTo(594, 3593);
      c.atObject(591, 3593);
      c.sleep(2000);
      c.walkTo(591, 765);
    }
    c.walkTo(582, 767);
    c.walkTo(579, 763);
    c.walkTo(584, 754);
    c.walkTo(585, 752);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToBerry() {
    c.setStatus("@gre@Walking to Berry bush..");
    if (agilityCapeTeleport && c.getInventoryItemCount(AGILITY_CAPE) != 0) {
      teleportAgilityCape();
    } else {
      c.walkTo(584, 754);
      c.walkTo(579, 763);
      c.walkTo(582, 767);
      c.walkTo(591, 765);
    }
    c.walkTo(590, 762);
    c.atObject(591, 761);
    c.sleep(2000);
    c.walkTo(593, 3590);
    c.setStatus("@gre@Picklocking Door..");
    yanilleDungeonDoorEntering();
    c.setStatus("@gre@Walking to Druids..");
    c.walkTo(594, 3587);
    c.walkTo(596, 3585);
    c.atObject(596, 3584); // Rope Swing  (make a loop)
    c.sleep(3000);
    c.setStatus("@gre@Done Walking..");
    c.walkTo(597, 3574);
    c.walkTo(603, 3568);
    c.walkTo(605, 3568);
    c.atObject(606, 3568); // go through pipe  (make a loop)
    c.sleep(3000);
    if (lowlevel) eatFood();
    c.walkTo(611, 3569);
  }

  private void setupGUI() {
    JLabel header = new JLabel("Berry Harvester - By Kaila");
    JLabel label1 = new JLabel("Harvests Berries near yanille dungeon, with herb clippers");
    JLabel label2 = new JLabel("Requires 77 agility to not fail rope swing");
    JLabel label4 = new JLabel("Recommend level 89+ combat so warriors are non aggressive");
    JLabel label5 = new JLabel("This bot supports the \"autostart\" parameter");
    JCheckBox agilityCapeCheckBox = new JCheckBox("99 Agility Cape Teleport?", true);
    JCheckBox lowLevelCheckBox = new JCheckBox("Below 89 combat?", false);
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    foodField.setSelectedIndex(2); // sets default to sharks
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          agilityCapeTeleport = agilityCapeCheckBox.isSelected();
          lowlevel = lowLevelCheckBox.isSelected();
          if (lowlevel) {
            if (!foodWithdrawAmountField.getText().equals(""))
              foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
            foodId = foodIds[foodField.getSelectedIndex()];
            foodName = foodTypes[foodField.getSelectedIndex()];
          }
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });
    lowLevelCheckBox.addActionListener(
        e -> {
          foodField.setEnabled(lowLevelCheckBox.isSelected());
          foodWithdrawAmountField.setEnabled(lowLevelCheckBox.isSelected());
        });
    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(agilityCapeCheckBox);
    scriptFrame.add(lowLevelCheckBox);
    scriptFrame.add(foodLabel);
    scriptFrame.add(foodField);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(startScriptButton);

    foodField.setEnabled(false);
    foodWithdrawAmountField.setEnabled(false);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void chatCommandInterrupt(
      String commandText) { // ::bank ::bones ::lowlevel :potup ::prayer
    if (commandText.contains("bank")) {
      c.displayMessage("@or1@Got @red@bank@or1@ command! Going to the Bank!");
      timeToBank = true;
      c.sleep(100);
    } else if (commandText.contains("cape")) {
      if (!agilityCapeTeleport) {
        c.displayMessage("@or1@Got toggle @red@Agility Cape@or1@, turning on cape teleport!");
        agilityCapeTeleport = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@Agility Cape@or1@, turning off cape teleport!");
        agilityCapeTeleport = false;
      }
      c.sleep(100);
    }
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
        successPerHr = (int) (totalBerryz * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Berry Harvester @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Berries in Bank: @gre@" + BerryzInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Berries Picked: @gre@"
              + totalBerryz
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
      long timeRemainingTillAutoWalkAttempt = next_attempt - System.currentTimeMillis();
      c.drawString(
          "@whi@Time till AutoWalk: " + c.msToShortString(timeRemainingTillAutoWalkAttempt),
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
    }
  }
}
