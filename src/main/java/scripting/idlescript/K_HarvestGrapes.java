package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.*;

/**
 * <b>Grape Harvester</b>
 *
 * <p>Harvests Grapes from Edgeville Monastery (Coleslaw Only).<br>
 * Start in Edge Bank with Herb Clippers or near grapes. <br>
 * Recommend Armor against lvl 21 Scorpions. <br>
 * This bot supports the "autostart" parameter to automatiically start the bot without gui.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_HarvestGrapes extends K_kailaScript {
  private boolean lowlevel = false;
  private boolean ate = true;
  private static int GrapezInBank = 0;
  private static int totalGrapez = 0;

  private void startSequence() {
    c.displayMessage("@red@Grape Harvester - By Kaila");
    c.displayMessage("@red@Start in Edge Bank or near Grapes");
    if (c.isInBank()) c.closeBank();
    if (c.currentX() < 240) {
      bank();
      BankToGrape();
    }
    c.toggleBatchBarsOn();
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
        c.displayMessage("Auto-starting, Picking Grapes", 0);
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
      startSequence();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (lowlevel) ate = eatFood();
      if (c.getInventoryItemCount() == 30 || (lowlevel && !ate)) {
        c.setStatus("@red@Banking..");
        GrapeToBank();
        bank();
        BankToGrape();
      }
      c.setStatus("@yel@Picking Grapes..");
      int[] coords = c.getNearestObjectById(1283);
      if (coords != null) {
        c.setStatus("@yel@Harvesting...");
        c.atObject(coords[0], coords[1]);
        c.sleep(2000);
        while (c.isBatching() && c.getInventoryItemCount() != 30) {
          c.sleep(GAME_TICK);
        }
      } else {
        c.setStatus("@yel@Waiting for spawn..");
        c.sleep(1000);
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
      totalGrapez = totalGrapez + c.getInventoryItemCount(143);
      if (c.getInventoryItemCount(143) > 0) { // deposit the Grapes
        c.depositItem(143, c.getInventoryItemCount(143));
        c.sleep(1380);
      }
      if (c.getInventoryItemCount(1357) < 1) { // withdraw herb clippers
        if (c.getBankItemCount(1357) > 0) {
          c.withdrawItem(1357, 1);
          c.sleep(1380);
        } else {
          c.displayMessage("@red@You need herb clippers!");
        }
      }
      if (lowlevel) withdrawFood(foodId, foodWithdrawAmount);
      GrapezInBank = c.getBankItemCount(143);
      c.closeBank();
      if (lowlevel) eatFood();
    }
  }

  private void GrapeToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(251, 454);
    c.walkTo(254, 454);
    c.walkTo(256, 451);
    c.walkTo(255, 444);
    c.walkTo(255, 433);
    c.walkTo(255, 422);
    c.walkTo(258, 422);
    if (lowlevel) eatFood();
    c.walkTo(258, 415);
    c.walkTo(252, 421);
    c.walkTo(242, 432);
    c.walkTo(225, 432);
    c.walkTo(220, 437);
    c.walkTo(220, 445);
    c.walkTo(218, 447);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToGrape() {
    c.setStatus("@gre@Walking to Grapes..");
    c.walkTo(218, 447);
    c.walkTo(220, 445);
    c.walkTo(220, 437);
    c.walkTo(225, 432);
    c.walkTo(242, 432);
    c.walkTo(252, 421);
    c.walkTo(258, 415);
    if (lowlevel) eatFood();
    c.walkTo(258, 422);
    c.walkTo(255, 422);
    c.walkTo(255, 433);
    c.walkTo(255, 444);
    c.walkTo(256, 451);
    c.walkTo(254, 454);
    c.walkTo(251, 454);
    // (next to Grape now)
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Grape Harvester - By Kaila");
    JLabel label1 = new JLabel("Harvests Grapes near Edge Monastery");
    JLabel label2 = new JLabel("*Start in Edge Bank with Herb Clippers");
    JLabel label3 = new JLabel("*Recommend Armor against lvl 21 Scorpions");
    JLabel label4 = new JLabel("This bot supports the \"autostart\" parameter");
    JCheckBox lowLevelCheckBox = new JCheckBox("Below 89 combat?", false);
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    foodField.setSelectedIndex(2); // sets default to sharks
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
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
    scriptFrame.add(label3);
    scriptFrame.add(label4);
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
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      int TripSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (totalGrapez * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Grape Harvester @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Grapes in Bank: @gre@" + GrapezInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Grapes Picked: @gre@"
              + totalGrapez
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
