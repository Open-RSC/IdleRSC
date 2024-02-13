package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Ardy Moss Giants</b>
 *
 * <p>Options: Combat Style, Loot level Herbs, Reg pots, Alter Prayer Boost, Food Type, and Food
 * Withdraw Amount Selection, Chat Command Options, Full top-left GUI, regular atk/str pot option,
 * and Autostart.
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_ArdyMossGiants extends K_kailaScript {
  private boolean lootSpinachRoll = false;
  private int fightMode = 0;
  private static final int[] lowLevelLoot = {
    ItemId.UNID_GUAM_LEAF.getId(),
    ItemId.UNID_MARRENTILL.getId(),
    ItemId.UNID_TARROMIN.getId(),
    ItemId.UNID_HARRALANDER.getId(),
    ItemId.UNID_RANARR_WEED.getId(),
    ItemId.UNID_IRIT.getId(),
    ItemId.UNID_AVANTOE.getId(),
    ItemId.UNID_KWUARM.getId(),
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
    ItemId.AIR_RUNE.getId(),
    ItemId.EARTH_RUNE.getId(),
    ItemId.FIRE_RUNE.getId(),
    ItemId.WATER_RUNE.getId(),
    ItemId.CHAOS_RUNE.getId(),
    ItemId.DEATH_RUNE.getId(),
    ItemId.BLOOD_RUNE.getId(),
    ItemId.NATURE_RUNE.getId(),
    ItemId.LAW_RUNE.getId(),
    ItemId.COSMIC_RUNE.getId(),
    ItemId.BLACK_SQUARE_SHIELD.getId(), // black sq shield
    ItemId.COINS.getId(),
    ItemId.BRONZE_ARROWS.getId(),
    ItemId.UNCUT_SAPPHIRE.getId(),
    ItemId.UNCUT_EMERALD.getId(),
    ItemId.UNCUT_RUBY.getId(),
    ItemId.UNCUT_DIAMOND.getId(),
    ItemId.TOOTH_HALF_KEY.getId(),
    ItemId.LOOP_HALF_KEY.getId(),
    ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.getId(),
    ItemId.RUNE_SPEAR.getId(),
  };
  private static final int[] highLevelLoot = {
    ItemId.UNID_RANARR_WEED.getId(),
    ItemId.UNID_IRIT.getId(),
    ItemId.UNID_AVANTOE.getId(),
    ItemId.UNID_KWUARM.getId(),
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
    ItemId.AIR_RUNE.getId(),
    ItemId.EARTH_RUNE.getId(),
    ItemId.FIRE_RUNE.getId(),
    ItemId.WATER_RUNE.getId(),
    ItemId.CHAOS_RUNE.getId(),
    ItemId.DEATH_RUNE.getId(),
    ItemId.BLOOD_RUNE.getId(),
    ItemId.NATURE_RUNE.getId(),
    ItemId.LAW_RUNE.getId(),
    ItemId.COSMIC_RUNE.getId(),
    ItemId.BLACK_SQUARE_SHIELD.getId(), // black sq shield
    ItemId.COINS.getId(),
    ItemId.BRONZE_ARROWS.getId(),
    ItemId.UNCUT_SAPPHIRE.getId(),
    ItemId.UNCUT_EMERALD.getId(),
    ItemId.UNCUT_RUBY.getId(),
    ItemId.UNCUT_DIAMOND.getId(),
    ItemId.TOOTH_HALF_KEY.getId(),
    ItemId.LOOP_HALF_KEY.getId(),
    ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.getId(),
    ItemId.RUNE_SPEAR.getId(),
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * the corresponding logic based on the values of the parameters.
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    centerX = 637;
    centerY = 504;
    centerDistance = 20;
    if (parameters[0].toLowerCase().startsWith("auto")) {
      foodId = ItemId.SHARK.getId();
      foodName = "Shark";
      fightMode = 0;
      foodWithdrawAmount = 1;
      lootLowLevel = true;
      potUp = false;
      lootBones = true;
      buryBones = false;
      c.displayMessage("Got Autostart Parameter");
      c.log("Auto-Starting using 1 Shark, controlled, Loot Low Level, no pot up", "cya");
      c.log("Looting Bones, Banking bones", "cya");
      guiSetup = true;
      scriptStarted = true;
    }
    if (!guiSetup) {
      guiSetup = true;
      setupGUI();
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      c.displayMessage("@red@Ardy Moss Giants ~ Kaila");
      c.displayMessage("@red@Start in Ardy north bank or near moss giants.");

      if (c.isInBank()) c.closeBank();
      if (c.currentY() > 565) {
        bank();
        bankToDungeon();
        c.sleep(1380);
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }
  /**
   * Executes the script by continuously performing a series of actions until the script is stopped.
   */
  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (!eatFood()
          || c.getInventoryItemCount() == 30
          || c.getInventoryItemCount(foodId) == 0
          || timeToBank
          || timeToBankStay) {
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        dungeonToBank();
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          c.displayMessage(
              "@red@Click on Start Button Again@or1@, to resume the script where it left off (preserving statistics)");
          endSession();
        }
        bankToDungeon();
      }
      if (potUp) {
        attackBoost(0, false);
        strengthBoost(0, false);
      }
      if (lootLowLevel) lootItems(false, lowLevelLoot);
      else lootItems(false, highLevelLoot);
      if (lootSpinachRoll) lootItem(false, ItemId.SPINACH_ROLL.getId());
      if (lootBones) lootItem(false, ItemId.BIG_BONES.getId());
      if (buryBones) buryBones(false);
      checkFightMode(fightMode);
      if (!c.isInCombat()) {
        ORSCharacter npc = c.getNearestNpcById(104, false);
        c.setStatus("@yel@Attacking..");
        if (npc != null) {
          c.attackNpc(npc.serverIndex);
          c.sleep(GAME_TICK);
        } else c.sleep(GAME_TICK);
      } else c.sleep(640);
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(false, 1, ItemId.EMPTY_VIAL.getId());
        buryBonesToLoot(false);
      }
    }
  }
  /** Executes the banking functionality. */
  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalGuam = totalGuam + c.getInventoryItemCount(165);
      totalMar = totalMar + c.getInventoryItemCount(435);
      totalTar = totalTar + c.getInventoryItemCount(436);
      totalHar = totalHar + c.getInventoryItemCount(437);
      totalRan = totalRan + c.getInventoryItemCount(438);
      totalIrit = totalIrit + c.getInventoryItemCount(439);
      totalAva = totalAva + c.getInventoryItemCount(440);
      totalKwuarm = totalKwuarm + c.getInventoryItemCount(441);
      totalCada = totalCada + c.getInventoryItemCount(442);
      totalDwarf = totalDwarf + c.getInventoryItemCount(443);
      totalLaw = totalLaw + c.getInventoryItemCount(42);
      totalNat = totalNat + c.getInventoryItemCount(40);
      totalChaos = totalChaos + c.getInventoryItemCount(41);
      totalDeath = totalDeath + c.getInventoryItemCount(38);
      totalBlood = totalBlood + c.getInventoryItemCount(619);
      totalEarth = totalEarth + c.getInventoryItemCount(34);
      totalAir = totalAir + c.getInventoryItemCount(33);
      totalCosmic = totalCosmic + c.getInventoryItemCount(46);
      totalLoop = totalLoop + c.getInventoryItemCount(527);
      totalTooth = totalTooth + c.getInventoryItemCount(526);
      totalLeft = totalLeft + c.getInventoryItemCount(1277);
      totalSpear = totalSpear + c.getInventoryItemCount(1092);
      totalBones = totalBones + c.getInventoryItemCount(413);
      foodInBank = c.getBankItemCount(foodId);
      totalRunes =
          totalLaw
              + totalNat
              + totalEarth
              + totalChaos
              + totalDeath
              + totalBlood
              + totalAir
              + totalCosmic;
      totalGems =
          totalGems
              + c.getInventoryItemCount(160)
              + c.getInventoryItemCount(159)
              + c.getInventoryItemCount(158)
              + c.getInventoryItemCount(157);
      totalHerbs =
          totalGuam
              + totalMar
              + totalTar
              + totalHar
              + totalRan
              + totalIrit
              + totalAva
              + totalKwuarm
              + totalCada
              + totalDwarf;
      for (int itemId : c.getInventoryItemIds()) {
        c.depositItem(itemId, c.getInventoryItemCount(itemId));
      }
      c.sleep(1240); // Important, leave in
      if (potUp) {
        withdrawAttack(1);
        withdrawStrength(1);
      }
      withdrawFood(foodId, foodWithdrawAmount);
      bankItemCheck(foodId, 5);
      c.closeBank();
    }
  }
  /** Walks the character from the bank to the dungeon. */
  private void bankToDungeon() {
    c.setStatus("@gre@Walking to Moss Giants..");
    c.walkTo(581, 571);
    c.walkTo(588, 568);
    c.walkTo(588, 562);
    c.walkTo(593, 558);
    c.walkTo(600, 551);
    c.walkTo(602, 544);
    c.walkTo(611, 536);
    c.walkTo(623, 526);
    c.walkTo(633, 516);
    c.setStatus("@gre@Done Walking..");
  }
  /** Dungeon to bank function that walks the character from the dungeon to the Ardy North Bank. */
  private void dungeonToBank() {
    c.setStatus("@gre@Walking to Ardy North Bank..");
    c.walkTo(633, 516);
    c.walkTo(623, 526);
    c.walkTo(611, 536);
    c.walkTo(602, 544);
    c.walkTo(600, 551);
    c.walkTo(593, 558);
    c.walkTo(588, 562);
    c.walkTo(588, 568);
    c.walkTo(581, 571);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }
  /** Sets up the graphical user interface for the program. */
  private void setupGUI() {
    JLabel header = new JLabel("Ardy Moss Giants ~ by Kaila");
    JLabel label1 = new JLabel("Start in Varrock West or in Edge Dungeon");
    JLabel label6 = new JLabel("Dusty Key Required + Food in Bank");
    JLabel label2 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label3 = new JLabel("::bank ::lowlevel :potup ::lootbones ::burybones");
    JLabel label4 = new JLabel("Styles ::attack :strength ::defense ::controlled");
    JLabel label5 = new JLabel("Param Format: \"auto\"");
    JCheckBox lootBonesCheckbox = new JCheckBox("Pickup Big Bones?", true);
    JCheckBox buryBonesCheckbox = new JCheckBox("Bury Big Bones?", true);
    JCheckBox lootSpinachRollCheckbox = new JCheckBox("Loot Spinach Rolls?", true);
    JCheckBox lowLevelHerbCheckbox = new JCheckBox("Loot Low Level Herbs?", true);
    JCheckBox potUpCheckbox = new JCheckBox("Use regular Atk/Str Pots?", false);
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    foodField.setSelectedIndex(5); // sets default to lobs
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals(""))
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          lootLowLevel = lowLevelHerbCheckbox.isSelected();
          lootBones = lootBonesCheckbox.isSelected();
          lootSpinachRoll = lootSpinachRollCheckbox.isSelected();
          buryBones = buryBonesCheckbox.isSelected();
          foodId = foodIds[foodField.getSelectedIndex()];
          foodName = foodTypes[foodField.getSelectedIndex()];
          fightMode = fightModeField.getSelectedIndex();
          potUp = potUpCheckbox.isSelected();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });
    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label6);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(lootBonesCheckbox);
    scriptFrame.add(buryBonesCheckbox);
    scriptFrame.add(lootSpinachRollCheckbox);
    scriptFrame.add(lowLevelHerbCheckbox);
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
  /**
   * Handles chat commands input by the user and changes values in the program
   *
   * @param commandText the text of the command
   */
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
    } else if (commandText.contains("lootspin")) {
      if (!lootSpinachRoll) {
        c.displayMessage(
            "@or1@Got toggle @red@lootSpinachRoll@or1@, turning on SpinachRoll looting!");
        lootSpinachRoll = true;
      } else {
        c.displayMessage(
            "@or1@Got toggle @red@lootSpinachRoll@or1@, turning off SpinachRoll looting!");
        lootSpinachRoll = false;
      }
      c.sleep(100);
    } else if (commandText.contains("lootbones")) {
      if (!lootBones) {
        c.displayMessage("@or1@Got toggle @red@lootbones@or1@, turning on bone looting!");
        lootBones = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning off bone looting!");
        lootBones = false;
      }
      c.sleep(100);
    } else if (commandText.contains("burybones")) {
      if (!buryBones) {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning on bone bury!");
        buryBones = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@buryBones@or1@, turning off bone bury!");
        buryBones = false;
      }
      c.sleep(100);
    } else if (commandText.contains("lowlevel")) {
      if (!lootLowLevel) {
        c.displayMessage("@or1@Got toggle @red@lowlevel@or1@, turning on low level herb looting!");
        lootLowLevel = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@lowlevel@or1@, turning off low level herb looting!");
        lootLowLevel = false;
      }
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
  /**
   * Handles quest messages appearing in chat. interrupt to process messages
   *
   * @param message the text of the message
   */
  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You eat the")) {
      usedFood++;
    }
  }
  /**
   * Handles server messages appearing in chat. interrupt to process messages
   *
   * @param message the text of the message
   */
  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("You dig a hole")) {
      usedBones++;
    }
  }
  /** Overrides paintInterrupt. Displays various statistics */
  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int guamSuccessPerHr = 0;
      int marSuccessPerHr = 0;
      int tarSuccessPerHr = 0;
      int harSuccessPerHr = 0;
      int ranSuccessPerHr = 0;
      int iritSuccessPerHr = 0;
      int avaSuccessPerHr = 0;
      int kwuSuccessPerHr = 0;
      int cadaSuccessPerHr = 0;
      int dwarSuccessPerHr = 0;
      int lawSuccessPerHr = 0;
      int runeSuccessPerHr = 0;
      int natSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      int herbSuccessPerHr = 0;
      int foodUsedPerHr = 0;
      int boneSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        guamSuccessPerHr = (int) (totalGuam * scale);
        marSuccessPerHr = (int) (totalMar * scale);
        tarSuccessPerHr = (int) (totalTar * scale);
        harSuccessPerHr = (int) (totalHar * scale);
        ranSuccessPerHr = (int) (totalRan * scale);
        iritSuccessPerHr = (int) (totalIrit * scale);
        avaSuccessPerHr = (int) (totalAva * scale);
        kwuSuccessPerHr = (int) (totalKwuarm * scale);
        cadaSuccessPerHr = (int) (totalCada * scale);
        dwarSuccessPerHr = (int) (totalDwarf * scale);
        lawSuccessPerHr = (int) (totalLaw * scale);
        natSuccessPerHr = (int) (totalNat * scale);
        GemsSuccessPerHr = (int) (totalGems * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
        herbSuccessPerHr = (int) (totalHerbs * scale);
        runeSuccessPerHr = (int) (totalRunes * scale);
        boneSuccessPerHr = (int) ((bankBones + usedBones) * scale);
        foodUsedPerHr = (int) (usedFood * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      int y2 = 220;
      c.drawString("@red@Ardy Moss Giants @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      if (lootLowLevel) {
        c.drawString(
            "@whi@Guam: @gre@"
                + totalGuam
                + "@yel@ (@whi@"
                + String.format("%,d", guamSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Mar: @gre@"
                + totalMar
                + "@yel@ (@whi@"
                + String.format("%,d", marSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Tar: @gre@"
                + totalTar
                + "@yel@ (@whi@"
                + String.format("%,d", tarSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + 14,
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Har: @gre@"
                + totalHar
                + "@yel@ (@whi@"
                + String.format("%,d", harSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Rana: @gre@"
                + totalRan
                + "@yel@ (@whi@"
                + String.format("%,d", ranSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Irit: @gre@"
                + totalIrit
                + "@yel@ (@whi@"
                + String.format("%,d", iritSuccessPerHr)
                + "@yel@/@whi@hr@yel@)",
            x,
            y + (14 * 2),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Ava: @gre@"
                + totalAva
                + "@yel@ (@whi@"
                + String.format("%,d", avaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Kwu: @gre@"
                + totalKwuarm
                + "@yel@ (@whi@"
                + String.format("%,d", kwuSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Cada: @gre@"
                + totalCada
                + "@yel@ (@whi@"
                + String.format("%,d", cadaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 3),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Dwar: @gre@"
                + totalDwarf
                + "@yel@ (@whi@"
                + String.format("%,d", dwarSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Laws: @gre@"
                + totalLaw
                + "@yel@ (@whi@"
                + String.format("%,d", lawSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Nats: @gre@"
                + totalNat
                + "@yel@ (@whi@"
                + String.format("%,d", natSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 4),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Total Gems: @gre@"
                + totalGems // remove for regular druids!!!
                + "@yel@ (@whi@"
                + String.format("%,d", GemsSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Total Herbs: @gre@"
                + totalHerbs
                + "@yel@ (@whi@"
                + String.format("%,d", herbSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 5),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Tooth: @gre@"
                + totalTooth // remove for regular druids!!!
                + "@yel@ / @whi@Loop: @gre@"
                + totalLoop
                + "@yel@ / @whi@R.Spear: @gre@"
                + totalSpear
                + "@yel@ / @whi@Half: @gre@"
                + totalLeft,
            x,
            y + (14 * 6),
            0xFFFFFF,
            1);

        c.drawString(
            "@whi@Total Runes: @gre@"
                + totalRunes
                + "@yel@ (@whi@"
                + String.format("%,d", runeSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Total Bones: @gre@"
                + (bankBones + usedBones)
                + "@yel@ (@whi@"
                + String.format("%,d", boneSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 7),
            0xFFFFFF,
            1);
        c.drawString("@whi@____________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
      } else {
        c.drawString(
            "@whi@Rana: @gre@"
                + totalRan
                + "@yel@ (@whi@"
                + String.format("%,d", ranSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Irit: @gre@"
                + totalIrit
                + "@yel@ (@whi@"
                + String.format("%,d", iritSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Avan: @gre@"
                + totalAva
                + "@yel@ (@whi@"
                + String.format("%,d", avaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + 14,
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Kwua: @gre@"
                + totalKwuarm
                + "@yel@ (@whi@"
                + String.format("%,d", kwuSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Cada: @gre@"
                + totalCada
                + "@yel@ (@whi@"
                + String.format("%,d", cadaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Dwar: @gre@"
                + totalDwarf
                + "@yel@ (@whi@"
                + String.format("%,d", dwarSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 2),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Total Gems: @gre@"
                + totalGems // remove for regular druids!!!
                + "@yel@ (@whi@"
                + String.format("%,d", GemsSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Total Herbs: @gre@"
                + totalHerbs
                + "@yel@ (@whi@"
                + String.format("%,d", herbSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 3),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Tooth: @gre@"
                + totalTooth // remove for regular druids!!!
                + "@yel@ / @whi@Loop: @gre@"
                + totalLoop
                + "@yel@ / @whi@R.Spear: @gre@"
                + totalSpear
                + "@yel@ / @whi@Half: @gre@"
                + totalLeft,
            x,
            y + (14 * 4),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Total Runes: @gre@"
                + totalRunes
                + "@yel@ (@whi@"
                + String.format("%,d", runeSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Total Bones: @gre@"
                + (bankBones + usedBones)
                + "@yel@ (@whi@"
                + String.format("%,d", boneSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 5),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Total Trips: @gre@"
                + totalTrips
                + "@yel@ (@whi@"
                + String.format("%,d", TripSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Runtime: "
                + runTime,
            x,
            y + (14 * 6),
            0xFFFFFF,
            1);
        c.drawString("@whi@____________________", x, y + 3 + (14 * 6), 0xFFFFFF, 1);
      }
      c.drawString("@whi@____________________", x, y2, 0xFFFFFF, 1);
      c.drawString("@whi@Runtime: " + runTime, x, y2 + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y2 + (14 * 2),
          0xFFFFFF,
          1);
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
            y2 + (14 * 3),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@" + foodName + "'s in Bank: @gre@ Unknown", x, y2 + (14 * 4), 0xFFFFFF, 1);
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
            y2 + (14 * 3),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@" + foodName + "'s in Bank: @gre@" + foodInBank, x, y2 + (14 * 4), 0xFFFFFF, 1);
      }
    }
  }
}
