package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * <b>Battlefield Trainer</b>
 *
 * <p>Start in Ardy or at Battlefield. <br>
 * Food in Bank required. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
/*
 *   todo add food type selection add maging option
 */
public final class K_BattlefieldTrainer extends K_kailaScript {
  private int fightMode = 0;
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
      c.displayMessage("@red@Battlefield Trainer - By Kaila");
      c.displayMessage("@red@Start in Ardy or at Battlefield");
      c.displayMessage("@red@Sharks in Bank REQUIRED");
      if (c.isInBank()) c.closeBank();
      if (c.currentX() < 600) {
        bank();
        BankToDruid();
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
      if (potUp) {
        attackBoost(0, false);
        strengthBoost(0, false);
      }
      checkFightMode(fightMode);
      if (!c.isInCombat()) {
        ORSCharacter npc = c.getNearestNpcById(407, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking Trooper");
          // c.walktoNPC(npc.serverIndex,1);
          c.attackNpc(npc.serverIndex);
          c.sleep(GAME_TICK);
        } else c.sleep(GAME_TICK);
      } else c.sleep(GAME_TICK);
      timeToBank = !eatFood(); // does the eating checks
      if (c.getInventoryItemCount(foodId) == 0 || timeToBank || timeToBankStay) {
        c.setStatus("@yel@Banking..");
        DruidToBank();
        timeToBank = false;
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          c.displayMessage("@red@Click on Start Button Again@or1@, to resume");
          endSession();
        }
        BankToDruid();
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
      if (c.getInventoryItemCount() > 0) {
        for (int itemId : c.getInventoryItemIds()) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
        c.sleep(1280); // increased sleep here to prevent double banking
      }
      if (potUp) {
        withdrawAttack(1);
        withdrawStrength(1);
      }
      withdrawItem(foodId, foodWithdrawAmount);
      bankItemCheck(foodId, foodWithdrawAmount);
      c.closeBank();
    }
  }

  private void DruidToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(649, 639);
    c.walkTo(644, 639);
    c.walkTo(636, 638);
    c.walkTo(624, 638);
    c.walkTo(614, 632);
    c.walkTo(622, 633);
    c.walkTo(614, 632);
    c.walkTo(610, 635);
    c.walkTo(599, 635);
    c.walkTo(598, 632);
    c.walkTo(592, 627);
    c.walkTo(579, 628);
    c.walkTo(571, 628);
    c.walkTo(563, 621);
    c.walkTo(550, 620);
    c.walkTo(550, 613);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToDruid() {
    c.setStatus("@gre@Walking to Druids..");
    c.walkTo(550, 613);
    c.walkTo(550, 620);
    c.walkTo(563, 621);
    c.walkTo(571, 628);
    c.walkTo(579, 628);
    c.walkTo(592, 627);
    c.walkTo(598, 632);
    c.walkTo(599, 635);
    c.walkTo(610, 635);
    c.walkTo(614, 632);
    c.walkTo(622, 633);
    c.walkTo(624, 638);
    c.walkTo(636, 638);
    c.walkTo(644, 639);
    c.walkTo(649, 639);
    c.walkTo(653, 642);
    c.walkTo(658, 642);
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Battlefield Trainer - By Kaila");
    JLabel label1 = new JLabel("Start in Ardy or at Battlefield");
    JLabel label2 = new JLabel("Sharks in Bank REQUIRED");
    JLabel label4 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label5 = new JLabel("::bank ::bankstay");
    JLabel label6 = new JLabel("Styles ::attack :strength ::defense ::controlled");
    JCheckBox potUpCheckbox = new JCheckBox("Use regular Atk/Str Pots?", true);
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    foodField.setSelectedIndex(5); // sets default to lobs
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(30));
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals("")) {
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          } else {
            foodWithdrawAmount = 30;
          }
          fightMode = fightModeField.getSelectedIndex();
          foodId = foodIds[foodField.getSelectedIndex()];
          potUp = potUpCheckbox.isSelected();
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
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(label6);
    scriptFrame.add(potUpCheckbox);
    scriptFrame.add(fightModeLabel);
    scriptFrame.add(fightModeField);
    scriptFrame.add(foodLabel);
    scriptFrame.add(foodField);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void chatCommandInterrupt(String commandText) { // ::bank ::lowlevel :potup ::prayer
    if (commandText.contains("bank")) {
      c.displayMessage("@or1@Got @red@bank@or1@ command! Going to the Bank!");
      timeToBank = true;
      c.sleep(100);
    } else if (commandText.contains("bankstay")) {
      c.displayMessage("@or1@Got @red@bankstay@or1@ command! Going to the Bank and Staying!");
      timeToBankStay = true;
      c.sleep(100);
    } else if (commandText.contains("potup")) {
      if (!potUp) {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning on regular atk/str pots!");
        potUp = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning off regular atk/str pots!");
        potUp = false;
      }
      c.sleep(100);
    } else if (commandText.contains(
        "attack")) { // field is "Controlled", "Aggressive", "Accurate", "Defensive"}
      c.displayMessage("@red@Got Combat Style Command! - Attack Xp");
      c.displayMessage("@red@Switching to \"Accurate\" combat style!");
      fightMode = 2;
      c.sleep(100);
    } else if (commandText.contains("strength")) {
      c.displayMessage("@red@Got Combat Style Command! - Strength Xp");
      c.displayMessage("@red@Switching to \"Aggressive\" combat style!");
      fightMode = 1;
      c.sleep(100);
    } else if (commandText.contains("defense")) {
      c.displayMessage("@red@Got Combat Style Command! - Defense Xp");
      c.displayMessage("@red@Switching to \"Defensive\" combat style!");
      fightMode = 3;
      c.sleep(100);
    } else if (commandText.contains("controlled")) {
      c.displayMessage("@red@Got Combat Style Command! - Controlled Xp");
      c.displayMessage("@red@Switching to \"Controlled\" combat style!");
      fightMode = 0;
      c.sleep(100);
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;

        TripSuccessPerHr = (int) (totalTrips * scale);

      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Battlefield Trainer @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 2), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 2), 0xFFFFFF, 1);
    }
  }
}
