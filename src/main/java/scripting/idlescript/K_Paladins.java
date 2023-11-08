package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import orsc.ORSCharacter;

/**
 * <b>Paladin Tower Thiever</b>
 *
 * <p>Start in Ardy South Bank OR in Paladin Tower <br>
 * Switching to Defensive combat mode is ideal. <br>
 * Low Atk/Str and Higher Def is more Efficient <br>
 * Ensure to never wield weapons when Thieving.<br>
 *
 * <p>~300k per hr+ xp per hr possible! (at 99 with thieving cape)<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_Paladins extends K_kailaScript {
  private int totalCoins = 0;
  private int totalShark = 0; // raw sharks that we pick up
  private int totalAda = 0;
  private int totalScim = 0;
  private int coinsInBank = -1;
  private int chaosInBank = -1;
  private int invCoins = 0;
  private int invChaos = 0;
  private int thieveSuccess = 0;
  private int openChest = 0;
  private int thieveFailure = 0;
  private int thieveCapture = 0;
  private int fightMode = 0;
  private int startCoins;
  private int startChaos;
  private static final int[] pathToBank = {
    604, 603,
    589, 604,
    575, 605,
    564, 606,
    554, 607
  };
  private static final int[] loot = {
    10, // coins
    41, // chaos runes
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    1092, // rune spear
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
    619, // blood rune
    32, // water rune
    // 170,    //iron bar
    // 171,    //steel bar
    173, // mithril bar
    400, // rune chain
    402, // rune legs
    404, // rune kite
    112, // rune helm
    315, // defense amulet
  };

  private void startSequence() {
    c.displayMessage("@ran@Paladin Tower - By Kaila.");
    c.displayMessage("@gre@Beginning Startup Sequence.");
    if (c.isInBank()) c.closeBank();
    if (c.currentY() < 621
        && c.currentY() > 600
        && c.currentX() > 539
        && c.currentX() < 565) { // NEAR bank
      bank();
      BankToPaladins();
    }
    if (c.currentY() > 1542
        && c.currentY() < 1548
        && c.currentX() > 607
        && c.currentX() < 614) { // inside paladin antichamber
      c.walkTo(609, 1547);
      paladinDoorEntering();
      c.sleep(640);
      c.walkTo(610, 1549);
    }
    if (c.currentY() < 650
        && c.currentY() > 550
        && c.currentX() < 540
        && c.currentX() > 500) { // in witchhaven
      witchhavenToBank();
      bank();
      BankToPaladins();
    }
    if (c.currentY() < 650
        && c.currentY() > 550
        && c.currentX() > 564
        && c.currentX() < 620) { // on path
      c.walkPath(pathToBank);
      bank();
      BankToPaladins();
    }
    if (c.currentY() < 2496
        && c.currentY() > 2486
        && c.currentX() < 614
        && c.currentX() > 607) { // Upstairs
      paladinsToBank(false);
      bank();
      BankToPaladins();
    }
    c.displayMessage("@gre@Finished Startup Sequence.");
  }
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
      fightMode = 3;
      c.displayMessage("Got Autostart Parameter");
      c.log("@cya@Auto-Starting script using Sharks with a foodID of " + foodId, "cya");
      scriptFrame = null;
      guiSetup = true;
      scriptStarted = true;
    } else if (!parameters[0].equals("")) {
      fightMode = 3;
      try {
        c.displayMessage("@cya@Got parameter: " + parameters[0]);
        for (int i = 0; i < foodTypes.length; i++) {
          String option = foodTypes[i];

          if (option.equalsIgnoreCase(parameters[0])) {
            foodId = foodIds[i];
            break;
          }
        }
        if (foodId == -1) {
          throw new Exception("Food Type not selected! Format is \"Lobster\" ");
        }
        c.log("@cya@Starting script using " + parameters[0] + " with a foodID of " + foodId, "cya");
        guiSetup = true;
        scriptStarted = true;
      } catch (Exception e) {
        c.log("@red@Could not parse parameters! Could not parse ", "red");
        c.stop();
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startCoins = c.getInventoryItemCount(10);
      startChaos = c.getInventoryItemCount(41);
      invCoins = c.getInventoryItemCount(10);
      invChaos = c.getInventoryItemCount(41);
      startTime = System.currentTimeMillis();
      c.toggleBatchBarsOff();
      startSequence();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.isInCombat()) {
        c.setStatus("@red@Leaving combat..");
        c.walkTo(610, 1549, 0, true);
        c.sleep(640);
      }
      if (!c.isInCombat() && c.getInventoryItemCount(foodId) > 0) {
        c.setStatus("@yel@Thieving Paladins");
        ORSCharacter npc = c.getNearestNpcById(323, false);
        if (npc != null) {
          c.thieveNpc(npc.serverIndex);
          c.sleep(640); // this sleep time is important //was 300
        } else {
          c.sleep(100); // this sleep time is important
        }
      }
      int eatLvl = c.getBaseStat(c.getStatId("Hits")) - 20;
      if (c.getCurrentStat(c.getStatId("Hits")) < eatLvl) {
        eat();
      }
      checkFightMode(fightMode);
      lootItems(true, loot);
      if (c.getInventoryItemCount(foodId) == 0 || timeToBank) { // bank if no food-
        c.setStatus("@yel@Banking..");
        paladinsToBank(true);
        bank();
        timeToBank = false;
        BankToPaladins();
      }
      if (c.getInventoryItemCount() == 30) {
        leaveCombat();
        c.setStatus("@red@Eating Food to Loot..");
        if (c.getInventoryItemCount(foodId) > 0) {
          c.itemCommand(foodId);
          c.sleep(700);
        } else {
          c.setStatus("@yel@Banking..");
          paladinsToBank(true);
          bank();
          BankToPaladins();
        }
      }
    }
  }
  // Important private VOID's below
  private void eat() {
    if (c.isInCombat()) {
      c.setStatus("@red@Leaving combat..");
      c.walkTo(610, 1549, 0, true);
      c.sleep(640);
    }
    c.setStatus("@red@Eating..");
    boolean ate = false;
    for (int id : c.getFoodIds()) {
      if (c.getInventoryItemCount(id) > 0) {
        c.itemCommand(id);
        c.sleep(640);
        ate = true;
        break;
      }
    }
    if (!ate) {
      c.setStatus("@red@We've ran out of Food! Banking!.");
      c.sleep(308);
      paladinsToBank(true);
      bank();
      BankToPaladins();
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalCoins = totalCoins + c.getInventoryItemCount(10);
      totalChaos = totalChaos + c.getInventoryItemCount(41);
      totalShark = totalShark + c.getInventoryItemCount(545);
      totalAda = totalAda + c.getInventoryItemCount(154);
      totalSap = totalSap + c.getInventoryItemCount(160);
      totalScim = totalScim + c.getInventoryItemCount(427);
      coinsInBank = (c.getBankItemCount(10) / 1000000);
      chaosInBank = c.getBankItemCount(41);
      foodInBank = c.getBankItemCount(foodId);

      for (int itemId : c.getInventoryItemIds()) { // change 546(shark) to desired food id
        c.depositItem(itemId, c.getInventoryItemCount(itemId));
        c.sleep(100);
      }
      c.sleep(1280); // Important, leave in
      if (c.getInventoryItemCount(foodId)
          < 27) { // withdraw 27 shark if needed        //change 546(shark) to desired food id
        c.withdrawItem(
            foodId, 27 - c.getInventoryItemCount(foodId)); // change 546(shark) to desired food id
        c.sleep(640);
      }
      if (c.getBankItemCount(foodId) == 0) {
        c.setStatus("@red@NO Food in the bank, Logging Out!.");
        endSession();
      }
      c.closeBank();
      invCoins = c.getInventoryItemCount(10);
      invChaos = c.getInventoryItemCount(41);
    }
  }
  // Pathing Scripts Below
  private void paladinsToBank(boolean goUpLadder) {
    if (goUpLadder) {
      c.setStatus("@gre@Walking to Bank..");
      c.walkTo(611, 1550);
      for (int i = 0; i < 10; i++) {
        if (c.currentY() < 2000 && c.getNearestObjectById(5) != null) {
          c.atObject(611, 1551); // ladder in paladin room
          c.sleep(3 * GAME_TICK);
        }
      }
    }
    int[] scimCoords = c.getNearestItemById(427);
    if (scimCoords != null) { // Loot
      c.setStatus("@yel@Grabbing Black Scimmy..");
      c.walkTo(scimCoords[0], scimCoords[1]);
      c.pickupItem(scimCoords[0], scimCoords[1], 427, true, true);
      c.sleep(640);
    }
    int[] chestCoords = c.getNearestObjectById(338);
    boolean walkToBank = false;
    if (chestCoords != null) {
      c.setStatus("@red@Stealing From Chest..");
      c.walkTo(610, 2488);
      if (c.getInventoryItemCount() > 27 && c.getInventoryItemCount(foodId) > 0) {
        dropItemAmount(foodId, c.getInventoryItemCount(foodId), true);
      }
      c.atObject2(chestCoords[0], chestCoords[1]);
      c.sleep(8 * GAME_TICK);
      if (c.currentX() == 610 && c.currentY() == 2488) {
        walkToBank = true;
      } else witchhavenToBank();
    } else walkToBank = true;
    if (walkToBank) {
      c.setStatus("@red@Chest Empty, Walking...");
      c.walkTo(611, 2494);
      for (int i = 0; i < 10; i++) {
        if (c.currentY() > 2000 && c.getNearestObjectById(6) != null) {
          c.atObject(611, 2495); // go down ladder
          c.sleep(3 * GAME_TICK);
        }
      }
      c.atObject(611, 2495); // go down ladder
      c.sleep(10 * GAME_TICK);
      c.walkTo(609, 1548); // walk to door
      paladinDoorExiting(); // go through door
      c.walkTo(611, 1544);
      c.atObject(611, 1545); // go downstairs
      c.walkTo(608, 603);
      openDoorObjects(64, 607, 603); // open inner gate
      c.walkTo(599, 603);
      openDoorObjects(57, 598, 603); // open outer gate
      c.walkTo(577, 603);
      c.walkTo(574, 606);
      c.walkTo(564, 606);
      c.walkTo(552, 606);
      c.walkTo(550, 608);
      c.walkTo(550, 612);
      c.setStatus("@gre@Done Walking..");
      totalTrips = totalTrips + 1;
      c.sleep(640);
    }
  }

  private void paladinStair() {
    for (int i = 1; i <= 50; i++) {
      if (c.currentX() < 615 && c.currentX() > 609 && c.currentY() < 610 && c.currentY() > 600) {
        c.walkTo(612, 604);
        c.atObject(611, 601);
        c.sleep(320);
        c.atObject(611, 601);
        c.sleep(640);
      } else return;
    }
  }

  private void paladinDoorExiting() { // gate upstairs in paladins
    for (int i = 1; i <= 20; i++) {
      if (c.currentX() == 609 && c.currentY() == 1548) {
        c.atWallObject(609, 1548); // locked door
        c.sleep(4 * GAME_TICK);
      }
    }
  }

  private void paladinDoorEntering() { // gate upstairs in paladins
    c.toggleBatchBarsOn();
    for (int i = 1; i <= 20; i++) {
      if (c.currentX() == 609 && c.currentY() == 1547) {
        c.atWallObject2(609, 1548); // locked door
        c.sleep(4 * GAME_TICK);
        while (c.isBatching()) c.sleep(GAME_TICK);
      }
    }
    c.toggleBatchBarsOff();
  }

  private void BankToPaladins() {
    c.setStatus("@gre@Walking to Paladins..");
    c.walkTo(550, 612);
    c.walkTo(550, 606);
    c.walkTo(560, 606);
    c.walkTo(570, 606);
    c.walkTo(582, 606);
    c.walkTo(585, 603);
    c.walkTo(598, 603);
    openDoorObjects(57, 598, 603); // open outer gate
    c.walkTo(607, 603);
    openDoorObjects(64, 607, 603); // open inner gate
    c.walkTo(612, 604); // just below stair
    paladinStair();
    c.walkTo(609, 1547);
    paladinDoorEntering();
    c.setStatus("@gre@Done Walking..");
  }

  private void witchhavenToBank() {
    if (c.currentX() > 500 && c.currentX() < 532) {
      c.walkTo(528, 597);
      c.walkTo(534, 597);
      c.sleep(640);
    }
    c.walkTo(534, 597);
    c.walkTo(543, 597);
    c.walkTo(543, 605);
    c.walkTo(550, 612);
    c.sleep(320);
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Paladin Thiever - By Kaila.");
    JLabel label1 = new JLabel("Start in Ardy South Bank OR in Paladin Tower.");
    JLabel label2 = new JLabel("Please don't wield weapons while Thieving.");
    JLabel label3 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label4 = new JLabel("Example ::bank ");
    JLabel label5 = new JLabel("Combat Styles ::attack :strength ::defense ::controller");
    JLabel label7 = new JLabel("Param Format: \"foodType\", \"Sharks\", \"etc\".");
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    fightModeField.setSelectedIndex(3); // sets default to defensive
    foodField.setSelectedIndex(2); // sets default to sharks
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          foodId = foodIds[foodField.getSelectedIndex()];
          fightMode = fightModeField.getSelectedIndex();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          c.log(
              "@cya@Starting script using "
                  + foodField.getSelectedIndex()
                  + " with a foodID of "
                  + foodId,
              "cya");
          scriptStarted = true;
        });
    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(label7);
    scriptFrame.add(fightModeLabel);
    scriptFrame.add(fightModeField);
    scriptFrame.add(foodLabel);
    scriptFrame.add(foodField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void chatCommandInterrupt(String commandText) {
    if (commandText.contains("bank")) {
      c.displayMessage("@red@Got banking command! Going to the Bank!");
      timeToBank = true;
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
  public void serverMessageInterrupt(String message) {
    if (message.contains("are under attack")) {
      thieveCapture++;
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You pick the paladin's pocket")) {
      thieveSuccess++;
      invCoins = c.getInventoryItemCount(10);
      invChaos = c.getInventoryItemCount(41);
    } else if (message.contains("You fail to pick the paladin's pocket")) {
      thieveFailure++;
      invCoins = c.getInventoryItemCount(10);
      invChaos = c.getInventoryItemCount(41);
    } else if (message.contains("You eat the")) {
      usedFood++;
    } else if (message.contains("You open the chest")) {
      openChest++;
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int coinSuccessPerHr = 0;
      int chaosSuccessPerHr = 0;
      int tripSuccessPerHr = 0;
      int thieveSuccessPerHr = 0;
      int thieveFailurePerHr = 0;
      int thieveCapturePerHr = 0;
      int foodUsedPerHr = 0;
      long runTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = runTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        coinSuccessPerHr = (int) ((totalCoins + invCoins - startCoins) * scale);
        chaosSuccessPerHr = (int) ((totalChaos + invChaos - startChaos) * scale);
        tripSuccessPerHr = (int) (totalTrips * scale);
        thieveSuccessPerHr = (int) (thieveSuccess * scale);
        thieveFailurePerHr = (int) (thieveFailure * scale);
        thieveCapturePerHr = (int) (thieveCapture * scale);
        foodUsedPerHr = (int) (usedFood * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Paladins Thiever @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y, 0xFFFFFF, 1);
      if (coinsInBank == -1) {
        c.drawString(
            "@whi@Coins: @gre@"
                + (totalCoins + invCoins - startCoins)
                + "@yel@ (@whi@"
                + String.format("%,d", coinSuccessPerHr)
                + "@yel@/@whi@hr@yel@)"
                + "@yel@ (@whi@Coins in Bank: @gre@ Unknown @yel@)",
            x,
            y + 14,
            0xFFFFFF,
            1);
      } else {
        c.drawString(
            "@whi@Coins: @gre@"
                + (totalCoins + invCoins - startCoins)
                + "@yel@ (@whi@"
                + String.format("%,d", coinSuccessPerHr)
                + "@yel@/@whi@hr@yel@)"
                + "@whi@ Coins in Bank: @gre@"
                + coinsInBank
                + " @gre@million",
            x,
            y + 14,
            0xFFFFFF,
            1);
      }
      if (chaosInBank == -1) {
        c.drawString(
            "@whi@Chaos: @gre@"
                + (totalChaos + invChaos - startChaos)
                + "@yel@ (@whi@"
                + String.format("%,d", chaosSuccessPerHr)
                + "@yel@/@whi@hr@yel@)"
                + "@whi@ Chaos in Bank: @gre@ Unknown",
            x,
            y + (14 * 2),
            0xFFFFFF,
            1);
      } else {
        c.drawString(
            "@whi@Chaos: @gre@"
                + (totalChaos + invChaos - startChaos)
                + "@yel@ (@whi@"
                + String.format("%,d", chaosSuccessPerHr)
                + "@yel@/@whi@hr@yel@)"
                + "@whi@ Chaos in Bank: @gre@"
                + chaosInBank,
            x,
            y + (14 * 2),
            0xFFFFFF,
            1);
      }
      if (foodInBank == -1) {
        c.drawString(
            "@whi@Food Used: @gre@"
                + usedFood // working off eat()
                + "@yel@ (@whi@"
                + String.format("%,d", foodUsedPerHr)
                + "@yel@/@whi@hr@yel@)"
                + "@whi@ Food in Bank: @gre@ Unknown",
            x,
            y + (14 * 3),
            0xFFFFFF,
            1);
      } else {
        c.drawString(
            "@whi@Food Used: @gre@"
                + usedFood // working off eat()
                + "@yel@ (@whi@"
                + String.format("%,d", foodUsedPerHr)
                + "@yel@/@whi@hr@yel@)"
                + "@whi@ Food in Bank: @gre@"
                + foodInBank
                + "@yel@)",
            x,
            y + (14 * 3),
            0xFFFFFF,
            1);
      }
      c.drawString(
          "@whi@Success: @gre@"
              + thieveSuccess
              + "@yel@ (@whi@"
              + String.format("%,d", thieveSuccessPerHr)
              + "@yel@/@whi@hr@yel@)"
              + "@whi@ Fail: @gre@"
              + thieveFailure
              + "@yel@ (@whi@"
              + String.format("%,d", thieveFailurePerHr)
              + "@yel@/@whi@hr@yel@)"
              + "@whi@ Capture: @gre@"
              + thieveCapture
              + "@yel@ (@whi@"
              + String.format("%,d", thieveCapturePerHr)
              + "@yel@/@whi@hr@yel@)" // works off interrupt
          ,
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", tripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)"
              + "@yel@ @whi@Chest's Opened: @gre@"
              + openChest,
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      if (usedFood == 0 || foodInBank == -1) {
        c.drawString(
            "@whi@Time till out of Food: Unable to Parse, Please wait!",
            x,
            y + (14 * 6),
            0xFFFFFF,
            1);
      } else {
        c.drawString(
            "@whi@Time till out of Food: " + c.timeToCompletion(usedFood, foodInBank, startTime),
            x,
            y + (14 * 6),
            0xFFFFFF,
            1);
      }
      // long timeRemainingTillAutoWalkAttempt = next_attempt - System.currentTimeMillis();
      c.drawString(
          "@whi@Runtime: " + runTime,
          x,
          y + (14 * 7),
          0xFFFFFF,
          1); // + " (Time till AutoWalk: " + c.msToShortString(timeRemainingTillAutoWalkAttempt) +
      // ")"
      c.drawString("@whi@__________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
    }
  }
}
