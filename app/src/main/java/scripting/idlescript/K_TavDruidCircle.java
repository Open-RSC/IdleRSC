package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.PrayerId;
import orsc.ORSCharacter;

/**
 * <b>Taverly Druid Circle</b>
 *
 * <p>Options: Combat Style, Loot level Herbs, Loot Bones, Reg pots, Alter Prayer Boost, Food Type,
 * and Food Withdraw Amount Selection, Chat Command Options, Full top-left GUI, regular atk/str pot
 * option, and Autostart. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_TavDruidCircle extends K_kailaScript {
  private boolean prayerBoost = true;
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
    ItemId.LAW_RUNE.getId(),
    ItemId.EARTH_RUNE.getId(),
    ItemId.FIRE_RUNE.getId(),
    ItemId.WATER_RUNE.getId(),
    ItemId.CHAOS_RUNE.getId()
  };
  private static final int[] highLevelLoot = {
    ItemId.UNID_RANARR_WEED.getId(),
    ItemId.UNID_IRIT.getId(),
    ItemId.UNID_AVANTOE.getId(),
    ItemId.UNID_KWUARM.getId(),
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
    ItemId.LAW_RUNE.getId(),
    ItemId.EARTH_RUNE.getId(),
    ItemId.FIRE_RUNE.getId(),
    ItemId.WATER_RUNE.getId(),
    ItemId.CHAOS_RUNE.getId()
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    centerX = 362;
    centerY = 462;
    centerDistance = 12;
    if (parameters[0].toLowerCase().startsWith("auto")) {
      foodId = ItemId.SHARK.getId();
      foodName = "Shark";
      fightMode = 0;
      foodWithdrawAmount = 1;
      lootLowLevel = true;
      lootBones = true;
      potUp = false;
      prayerBoost = true;
      c.displayMessage("Got Autostart Parameter");
      c.log(
          "@cya@Auto-Starting using 1 Shark, controlled, Loot Low Level, Loot Bones, no pot up, yes prayer boosting",
          "cya");
      scriptStarted = true;
      guiSetup = true;
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      c.displayMessage("@red@Tav Druid Circle - By Kaila");
      c.displayMessage("@red@Start in Fally west or druid Circle");
      c.displayMessage("@red@Food in Bank required");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() > 515) {
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
      if (!eatFood()
          || c.getInventoryItemCount() == 30
          || c.getInventoryItemCount(foodId) == 0
          || timeToBank
          || timeToBankStay) {
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        DruidToBank();
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          c.displayMessage("@red@Click on Start Button Again@or1@, to resume");
          endSession();
        }
        BankToDruid();
      }
      if (potUp) {
        attackBoost(0, false);
        strengthBoost(0, false);
      }
      if (lootLowLevel) lootItems(false, lowLevelLoot);
      else lootItems(false, highLevelLoot);
      if (lootBones) lootItem(false, ItemId.BONES.getId());
      buryBones(false);
      if (prayerBoost) {
        recharge();
        pray();
      }
      checkFightMode(fightMode);
      if (!c.isInCombat()) {
        ORSCharacter npc = c.getNearestNpcById(200, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking Druids");
          c.attackNpc(npc.serverIndex);
          c.sleep(GAME_TICK);
        } else c.sleep(GAME_TICK);
      } else c.sleep(GAME_TICK);
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(false, 1, ItemId.EMPTY_VIAL.getId());
        buryBonesToLoot(false);
      }
      if (c.currentY() > 473) {
        c.log("currentY: " + c.currentY() + " Wandered too far, Walking Back to center", "@red@");
        c.walkTo(362, 464);
        c.sleep(640);
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
      totalFire = totalFire + c.getInventoryItemCount(31);
      totalEarth = totalEarth + c.getInventoryItemCount(34);
      totalChaos = totalChaos + c.getInventoryItemCount(41);
      totalWater = totalWater + c.getInventoryItemCount(32);
      foodInBank = c.getBankItemCount(foodId);
      totalRunes = totalFire + totalNat + totalEarth + totalChaos + totalWater + totalLaw;
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

  private void recharge() {
    if (c.getCurrentStat(c.getStatId("Prayer")) < 5) {
      c.setStatus("@yel@Recharging Prayer");
      c.walkTo(363, 461);
      c.atObject(362, 462);
      c.sleep(640);
    }
  }

  private void pray() {
    if (!c.isPrayerOn(PrayerId.ULTIMATE_STRENGTH.getId()) && c.currentY() < 475) {
      c.enablePrayer(PrayerId.ULTIMATE_STRENGTH.getId());
    }
    // if(!c.isPrayerOn(c.getPrayerId("Incredible Reflexes")) && c.currentY() < 475) {
    //    c.enablePrayer(c.getPrayerId("Incredible Reflexes"));
    // }
  }

  private void BankToDruid() {
    c.setStatus("@gre@Walking to Tav Gate..");
    c.walkTo(327, 552);
    c.walkTo(324, 549);
    c.walkTo(324, 539);
    c.walkTo(324, 530);
    c.walkTo(317, 523);
    c.walkTo(317, 516);
    c.walkTo(327, 506);
    c.walkTo(337, 496);
    c.walkTo(337, 492);
    c.walkTo(341, 488);
    c.setStatus("@red@Crossing Tav Gate..");
    tavGateEastToWest();
    c.setStatus("@gre@Walking to Druid Circle..");
    c.walkTo(346, 487);
    c.walkTo(351, 482);
    c.walkTo(356, 477);
    c.walkTo(361, 472);
    c.walkTo(361, 466);
    c.setStatus("@gre@Done Walking..");
  }

  private void DruidToBank() {
    c.setStatus("@gre@Walking to Tav Gate..");
    c.walkTo(361, 466);
    c.walkTo(361, 472);
    c.walkTo(356, 477);
    c.walkTo(351, 482);
    c.walkTo(346, 487);
    c.walkTo(342, 487);
    c.setStatus("@red@Crossing Tav Gate..");
    tavGateWestToEast();
    c.setStatus("@gre@Walking to Fally West..");
    c.walkTo(337, 492);
    c.walkTo(337, 496);
    c.walkTo(327, 506);
    c.walkTo(317, 516);
    c.walkTo(317, 523);
    c.walkTo(324, 530);
    c.walkTo(324, 539);
    c.walkTo(324, 549);
    c.walkTo(327, 552);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Tav Druid Circle - By Kaila");
    JLabel label1 = new JLabel("Start in Fally west or druid Circle");
    JLabel label6 = new JLabel("Food in Bank required!");
    JLabel label2 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label3 = new JLabel("::bank ::bones ::lowlevel :potup ::prayer");
    JLabel label4 = new JLabel("Styles ::attack :strength ::defense ::controlled");
    JLabel label5 = new JLabel("Param Format: \"auto\"");
    JCheckBox lootBonesCheckbox = new JCheckBox("Bury Bones? only while Npc's Null", true);
    JCheckBox lowLevelHerbCheckbox = new JCheckBox("Loot Low Level Herbs?", true);
    JCheckBox prayerBoostCheckbox = new JCheckBox("Use Alter for Boost Prayers?", true);
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
          foodId = foodIds[foodField.getSelectedIndex()];
          foodName = foodTypes[foodField.getSelectedIndex()];
          fightMode = fightModeField.getSelectedIndex();
          prayerBoost = prayerBoostCheckbox.isSelected();
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
    scriptFrame.add(lowLevelHerbCheckbox);
    scriptFrame.add(prayerBoostCheckbox);
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
  public void chatCommandInterrupt(
      String commandText) { // ::bank ::bones ::lowlevel :potup ::prayer
    if (commandText.contains("bank")) {
      c.displayMessage("@or1@Got @red@bank@or1@ command! Going to the Bank!");
      timeToBank = true;
      c.sleep(100);
    } else if (commandText.contains("bankstay")) {
      c.displayMessage("@or1@Got @red@bankstay@or1@ command! Going to the Bank and Staying!");
      timeToBankStay = true;
      c.sleep(100);
    } else if (commandText.contains("bones")) {
      if (!lootBones) {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning on bone looting!");
        lootBones = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning off bone looting!");
        lootBones = false;
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
    } else if (commandText.contains("prayer")) {
      if (!prayerBoost) {
        c.displayMessage("@or1@Got toggle @red@prayer@or1@, now using boost prayers!");
        prayerBoost = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@prayer@or1@, no longer using boost prayers!");
        prayerBoost = false;
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
  public void questMessageInterrupt(String message) {
    if (message.contains("You eat the")) {
      usedFood++;
    }
  }

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
      int TripSuccessPerHr = 0;
      int herbSuccessPerHr = 0;
      int fireSuccessPerHr = 0;
      int waterSuccessPerHr = 0;
      int earthSuccessPerHr = 0;
      int chaosSuccessPerHr = 0;
      int runeSuccessPerHr = 0;
      int foodUsedPerHr = 0;
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
        TripSuccessPerHr = (int) (totalTrips * scale);
        herbSuccessPerHr = (int) (totalHerbs * scale);
        fireSuccessPerHr = (int) (totalHerbs * scale);
        waterSuccessPerHr = (int) (totalHerbs * scale);
        earthSuccessPerHr = (int) (totalHerbs * scale);
        chaosSuccessPerHr = (int) (totalHerbs * scale);
        runeSuccessPerHr = (int) (totalRunes * scale);
        foodUsedPerHr = (int) (usedFood * scale);

      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Taverly Druid Circle @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
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
                + "@whi@Chaos: @gre@"
                + totalChaos
                + "@yel@ (@whi@"
                + String.format("%,d", chaosSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
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
                + "@whi@Total Herbs: @gre@"
                + totalHerbs
                + "@yel@ (@whi@"
                + String.format("%,d", herbSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 5),
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
                  + "@yel@/@whi@hr@yel@) "
                  + "@whi@"
                  + foodName
                  + "'s in Bank: @gre@ Unknown",
              x,
              y + (14 * 6),
              0xFFFFFF,
              1);
        } else {
          c.drawString(
              "@whi@"
                  + foodName
                  + "'s Used: @gre@"
                  + usedFood
                  + "@yel@ (@whi@"
                  + String.format("%,d", foodUsedPerHr)
                  + "@yel@/@whi@hr@yel@) "
                  + "@whi@"
                  + foodName
                  + "'s in Bank: @gre@"
                  + foodInBank,
              x,
              y + (14 * 6),
              0xFFFFFF,
              1);
        }
        c.drawString(
            "@whi@Total Trips: @gre@"
                + totalTrips
                + "@yel@ (@whi@"
                + String.format("%,d", TripSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Runtime: "
                + runTime,
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
            "@whi@Fire: @gre@"
                + totalFire
                + "@yel@ (@whi@"
                + String.format("%,d", fireSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Water: @gre@"
                + totalWater
                + "@yel@ (@whi@"
                + String.format("%,d", waterSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Earth: @gre@"
                + totalEarth
                + "@yel@ (@whi@"
                + String.format("%,d", earthSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 3),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Chaos: @gre@"
                + totalChaos
                + "@yel@ (@whi@"
                + String.format("%,d", chaosSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Total Runes: @gre@"
                + totalRunes
                + "@yel@ (@whi@"
                + String.format("%,d", runeSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Total Herbs: @gre@"
                + totalHerbs
                + "@yel@ (@whi@"
                + String.format("%,d", herbSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 4),
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
                  + "@yel@/@whi@hr@yel@) "
                  + "@whi@"
                  + foodName
                  + "'s in Bank: @gre@ Unknown",
              x,
              y + (14 * 5),
              0xFFFFFF,
              1);
        } else {
          c.drawString(
              "@whi@"
                  + foodName
                  + "'s Used: @gre@"
                  + usedFood
                  + "@yel@ (@whi@"
                  + String.format("%,d", foodUsedPerHr)
                  + "@yel@/@whi@hr@yel@) "
                  + "@whi@"
                  + foodName
                  + "'s in Bank: @gre@"
                  + foodInBank,
              x,
              y + (14 * 5),
              0xFFFFFF,
              1);
        }
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
    }
  }
}
