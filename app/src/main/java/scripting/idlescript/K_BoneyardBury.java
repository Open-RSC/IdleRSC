package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;

/**
 * <b>Boneyard Big Bone Bury (in wilderness) </b>
 *
 * <p>Options: Food Type, and Food Withdraw Amount Selection, Chat Command Options, Full top-left
 * GUI.
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_BoneyardBury extends K_kailaScript {
  private int fightMode = 0;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (parameters[0].toLowerCase().startsWith("auto")) {
      foodId = 546;
      foodName = "Shark";
      fightMode = 0;
      foodWithdrawAmount = 1;
      c.displayMessage("Got Autostart Parameter");
      c.log("Auto-Starting using 1 Shark, controlled, Loot Low Level, no pot up", "cya");
      c.log("Looting Bones, Banking bones", "cya");
      guiSetup = true;
      scriptStarted = true;
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      c.displayMessage("@red@Bone Yard Skeletons ~ Kaila");
      c.displayMessage("@red@Start in Edge bank or Bone Yard with Armor");

      if (c.isInBank()) c.closeBank();
      if (c.currentY() > 440) {
        bank();
        bankToHouse();
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
      boolean ate = eatFood();
      if (!ate) {
        c.setStatus("@red@We've ran out of Food! Running Away!.");
        houseToBank();
        bank();
        bankToHouse();
      }
      checkFightMode(fightMode);
      if (c.getInventoryItemCount(foodId) > 0 && !timeToBank) {
        lootBones();
        buryBones(true);
      }
      if (c.getInventoryItemCount(foodId) == 0 || timeToBank || timeToBankStay) {
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        houseToBank();
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          c.displayMessage(
              "@red@Click on Start Button Again@or1@, to resume the script where it left off (preserving statistics)");
          c.setStatus("@red@Stopping Script.");
          c.setAutoLogin(false);
          c.stop();
        }
        bankToHouse();
        c.sleep(618);
      } else {
        c.sleep(100);
      }
    }
  }

  private void lootBones() {
    final int boneId = 413;
    int[] coords = c.getNearestItemById(boneId);
    if (coords != null) {
      c.setStatus("@yel@Picking bones");
      c.walkToAsync(coords[0], coords[1], 0);
      c.pickupItem(coords[0], coords[1], boneId, true, false);
      c.sleep(640);
      if (buryBones) buryBones(true);
    } else {
      if (buryBones) buryBones(true);
      c.sleep(100);
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      for (int itemId : c.getInventoryItemIds()) {
        c.depositItem(itemId, c.getInventoryItemCount(itemId));
      }
      c.sleep(1240); // Important, leave in
      withdrawFood(foodId, foodWithdrawAmount);
      bankItemCheck(foodId, 5);
      c.closeBank();
    }
  }

  private void bankToHouse() {
    c.setStatus("@gre@Walking to Edge Skeletons..");
    c.walkTo(217, 447);
    c.walkTo(210, 447);
    c.walkTo(200, 435);
    c.walkTo(192, 435);
    c.walkTo(185, 427);
    c.walkTo(185, 419);
    c.walkTo(178, 411); // near muggers
    c.walkTo(165, 410);
    c.walkTo(158, 403);
    c.walkTo(150, 396);
    c.walkTo(150, 384);
    c.walkTo(150, 374);
    c.walkTo(150, 364);
    c.walkTo(144, 357);
    c.walkTo(142, 341);
    c.walkTo(142, 320);
    c.walkTo(140, 317);
    c.walkTo(140, 300);
    c.walkTo(128, 287); // leave from here
    c.walkTo(128, 275);
    c.setStatus("@gre@Done Walking..");
  }

  private void houseToBank() {
    c.setStatus("@gre@Walking to Edge Bank..");
    c.walkTo(128, 287); // leave from here
    c.walkTo(140, 300);
    c.walkTo(140, 317);
    c.walkTo(142, 320);
    c.walkTo(142, 341);
    c.walkTo(144, 357);
    c.walkTo(150, 364);
    c.walkTo(150, 374);
    c.walkTo(150, 384);
    c.walkTo(150, 396);
    c.walkTo(158, 403);
    c.walkTo(165, 410);
    c.walkTo(178, 411); // near muggers
    c.walkTo(185, 419);
    c.walkTo(185, 427);
    c.walkTo(192, 435);
    c.walkTo(200, 435);
    c.walkTo(210, 447);
    c.walkTo(217, 447);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Boneyard Bury ~ by Kaila");
    JLabel label1 = new JLabel("Start in Boneyard or Edge Bank");
    JLabel label5 = new JLabel("Param Format: \"auto\"");
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(28));
    foodField.setSelectedIndex(5); // sets default to lobs
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals(""))
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          foodId = foodIds[foodField.getSelectedIndex()];
          foodName = foodTypes[foodField.getSelectedIndex()];
          fightMode = fightModeField.getSelectedIndex();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label5);
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
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You eat the")) {
      usedFood++;
    }
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("You dig a hole")) {
      usedBones++;
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int TripSuccessPerHr = 0;
      int foodUsedPerHr = 0;
      int boneSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        TripSuccessPerHr = (int) (totalTrips * scale);
        boneSuccessPerHr = (int) ((bankBones + usedBones) * scale);
        foodUsedPerHr = (int) (usedFood * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      int y2 = 220;
      c.drawString("@red@Boneyard Big Bone Bury @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Total Bones: @gre@"
              + (bankBones + usedBones)
              + "@yel@ (@whi@"
              + String.format("%,d", boneSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y2, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 3), 0xFFFFFF, 1);
      if (foodInBank == -1) {
        c.drawString(
            "@whi@"
                + foodName
                + "'s Used: @gre@"
                + usedFood
                + "@yel@ (@whi@"
                + String.format("%,d", foodUsedPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 4),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@" + foodName + "'s in Bank: @gre@ Unknown", x, y + (14 * 5), 0xFFFFFF, 1);
      } else {
        c.drawString(
            "@whi@"
                + foodName
                + "'s Used: @gre@"
                + usedFood
                + "@yel@ (@whi@"
                + String.format("%,d", foodUsedPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 4),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@" + foodName + "'s in Bank: @gre@" + foodInBank, x, y + (14 * 5), 0xFFFFFF, 1);
      }
    }
  }
}
