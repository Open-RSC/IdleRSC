package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Ardy Chaos Druid Tower</b>
 *
 * <p>Options: Combat Style, Loot level Herbs, Loot Bones, Reg pots, Food Type, and Food Withdraw
 * Amount Selection.
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_ArdyChaosDruids extends K_kailaCombatScript {
  private int fightMode = 0;
  private boolean bloodChest = true;
  private boolean thieveChest1 = false;
  private boolean thieveChest2 = false;
  private int openChest = 0;
  private long nextAttemptChest1 = -1;
  private long nextAttemptChest2 = -1;
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
    ItemId.NATURE_RUNE.getId(),
    ItemId.LAW_RUNE.getId(),
    ItemId.COSMIC_RUNE.getId(),
    ItemId.BODY_RUNE.getId(),
    ItemId.MIND_RUNE.getId(),
    ItemId.CHAOS_RUNE.getId(),
    ItemId.DEATH_RUNE.getId(),
    ItemId.BLOOD_RUNE.getId(),
    ItemId.BRONZE_ARROWS.getId(),
    ItemId.COINS.getId(),
    ItemId.UNCUT_SAPPHIRE.getId(),
    ItemId.UNCUT_EMERALD.getId(),
    ItemId.UNCUT_RUBY.getId(),
    ItemId.UNCUT_DIAMOND.getId(),
    ItemId.TOOTH_HALF_KEY.getId(),
    ItemId.LOOP_HALF_KEY.getId(),
    ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.getId(),
    ItemId.RUNE_SPEAR.getId()
  };
  private static final int[] highLevelLoot = {
    ItemId.UNID_RANARR_WEED.getId(),
    ItemId.UNID_IRIT.getId(),
    ItemId.UNID_AVANTOE.getId(),
    ItemId.UNID_KWUARM.getId(),
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
    ItemId.NATURE_RUNE.getId(),
    ItemId.LAW_RUNE.getId(),
    ItemId.AIR_RUNE.getId(),
    ItemId.EARTH_RUNE.getId(),
    ItemId.UNCUT_SAPPHIRE.getId(),
    ItemId.UNCUT_EMERALD.getId(),
    ItemId.UNCUT_RUBY.getId(),
    ItemId.UNCUT_DIAMOND.getId(),
    ItemId.TOOTH_HALF_KEY.getId(),
    ItemId.LOOP_HALF_KEY.getId(),
    ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.getId(),
    ItemId.RUNE_SPEAR.getId()
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    centerX = 618;
    centerY = 553;
    centerDistance = 7;
    if (parameters[0].toLowerCase().startsWith("auto")) {
      foodId = ItemId.SHARK.getId();
      foodName = "Shark";
      fightMode = 0;
      foodWithdrawAmount = 1;
      lootLowLevel = true;
      lootBones = true;
      potUp = false;
      c.displayMessage("Got Autostart Parameter");
      c.log(
          "@cya@Auto-Starting using 1 Shark, controlled, Loot Low Level, Loot Bones, no pot up",
          "cya");
      scriptStarted = true;
      guiSetup = true;
    }
    if (!guiSetup && !scriptStarted) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      c.displayMessage("@red@Edge Druid Killer - By Kaila");
      c.displayMessage("@red@Start in Edge bank with Armor");
      c.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
      c.displayMessage("@red@31 Magic Required for escape tele");
      if (c.isInBank()) c.closeBank();
      if (c.currentX() < 600) {
        bank();
        BankToDruid();
        c.sleep(1380);
      }
      if (c.currentY() > 3000) {
        c.walkTo(618, 3391);
        c.walkTo(618, 3384);
        c.atObject(618, 3383);
        c.sleep(1000);
      }
      nextAttemptChest1 =
          System.currentTimeMillis() + 5000L; // staggered 50% start to reduce impact on druid KPH
      nextAttemptChest2 = System.currentTimeMillis() + (twoHundredFiftySecondsInMillis / 2) + 5000L;
      c.setBatchBarsOn();
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
          || timeToBank) {
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        DruidToBank();
        bank();
        BankToDruid();
        c.sleep(618);
      }
      checkFightMode(fightMode);
      checkInventoryItemCounts();
      buryBones(false);
      if (potUp) {
        attackBoost(0, false);
        strengthBoost(0, false);
      }
      if (lootLowLevel) lootItems(false, lowLevelLoot);
      else lootItems(false, highLevelLoot);
      if (thieveChest1 || System.currentTimeMillis() > nextAttemptChest1 && c.currentY() < 556) {
        c.displayMessage("@or1@Looting Blood Chest (1)!");
        thieveChest1 = false;
        lootBloodChest1();
        c.setStatus("@or1@Killing Druids...");
      }
      if (thieveChest2 || System.currentTimeMillis() > nextAttemptChest2 && c.currentY() < 556) {
        c.displayMessage("@or1@Looting Blood Chest (2)!");
        thieveChest2 = false;
        lootBloodChest2();
        c.setStatus("@or1@Killing Druids...");
      }

      if (c.getInventoryItemCount() < 30 && c.getInventoryItemCount(foodId) > 0) {
        if (!c.isInCombat()) {
          c.setStatus("@yel@Attacking Druids");
          ORSCharacter npc = c.getNearestNpcById(270, false);
          if (npc != null) {
            c.attackNpc(npc.serverIndex);
            c.sleep(3 * GAME_TICK);
          } else {
            if (lootLowLevel) lootItems(false, lowLevelLoot);
            else lootItems(false, highLevelLoot);
            if (lootBones) lootItem(false, ItemId.BONES.getId());
            c.sleep(300);
          }
        } else {
          c.sleep(2 * GAME_TICK);
        }
      }
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(false, 1, ItemId.EMPTY_VIAL.getId());
        buryBonesToLoot(false);
      }
    }
  }

  private void lootBloodChest1() {
    if (lootLowLevel) lootItems(false, lowLevelLoot);
    else lootItems(false, highLevelLoot);
    c.walkTo(618, 552);
    c.atObject(618, 551);
    c.sleep(3000);
    if (c.getObjectAtCoord(614, 3399) == 339) {
      c.setStatus("@or1@Chest Empty, returning...");
      nextAttemptChest1 = System.currentTimeMillis() + twoHundredFiftySecondsInMillis;
      long nextAttemptInSeconds = (nextAttemptChest1 - System.currentTimeMillis()) / 1000L;
      c.displayMessage(
          "@or1@Done looting blood chest (1), Next attempt in "
              + nextAttemptInSeconds
              + " seconds!");
      c.walkTo(618, 3384);
      c.sleep(640);
      c.atObject(618, 3383);
      c.sleep(2000);
    } else if (c.getObjectAtCoord(614, 3399) == 337) {
      c.setStatus("@or1@Stealing From Blood Chest (1)..");
      c.walkTo(618, 3396);
      c.walkTo(615, 3399);
      c.sleep(340);
      c.atObject2(614, 3399);
      nextAttemptChest1 = System.currentTimeMillis() + twoHundredFiftySecondsInMillis;
      long nextAttemptInSeconds = (nextAttemptChest1 - System.currentTimeMillis()) / 1000L;
      c.sleep(2000);
      c.displayMessage(
          "@or1@Done looting blood chest (1), Next attempt in "
              + nextAttemptInSeconds
              + " seconds!");
      if (c.currentY() > 1000) {
        c.sleep(3000);
      }
      if (c.currentY() > 1000) {
        c.sleep(3000);
      }
      if (c.currentY() > 1000) {
        c.sleep(3000);
      }
      if (c.currentY() > 1000) {
        c.sleep(3000);
      }
      if (c.currentY() > 1000) {
        c.sleep(3000);
      }
      if (c.currentY() > 1000) {
        c.displayMessage("@or1@Something went wrong, walking back up Ladder!");
        c.walkTo(615, 3399);
        c.walkTo(618, 3396);
        c.walkTo(618, 3384);
        c.atObject(618, 3383);
        c.sleep(1280);
      } else {
        townToTower();
      }
      c.setStatus("@or1@Done Thieving..");
    }
  }

  private void lootBloodChest2() {
    if (lootLowLevel) lootItems(false, lowLevelLoot);
    else lootItems(false, highLevelLoot);
    c.walkTo(618, 552);
    c.atObject(618, 551);
    c.sleep(3000);
    if (c.getObjectAtCoord(614, 3401) == 339) {
      c.setStatus("@or1@Chest Empty, returning...");
      nextAttemptChest2 = System.currentTimeMillis() + twoHundredFiftySecondsInMillis;
      long nextAttemptInSeconds = (nextAttemptChest2 - System.currentTimeMillis()) / 1000L;
      c.displayMessage(
          "@or1@Done looting blood chest (2), Next attempt in "
              + nextAttemptInSeconds
              + " seconds!");
      c.walkTo(618, 3384);
      c.sleep(640);
      c.atObject(618, 3383);
      c.sleep(2000);
    } else if (c.getObjectAtCoord(614, 3401) == 337) {
      c.setStatus("@or1@Stealing From Blood Chest (2)..");
      c.walkTo(618, 3396);
      c.walkTo(615, 3399);
      c.walkTo(615, 3401);
      c.sleep(340);
      c.atObject2(614, 3401);
      nextAttemptChest2 = System.currentTimeMillis() + twoHundredFiftySecondsInMillis;
      long nextAttemptInSeconds = (nextAttemptChest2 - System.currentTimeMillis()) / 1000L;
      c.sleep(2000);
      c.displayMessage(
          "@or1@Done looting blood chest (2), Next attempt in "
              + nextAttemptInSeconds
              + " seconds!");
      if (c.currentY() > 1000) {
        c.sleep(3000);
      }
      if (c.currentY() > 1000) {
        c.sleep(3000);
      }
      if (c.currentY() > 1000) {
        c.sleep(3000);
      }
      if (c.currentY() > 1000) {
        c.sleep(3000);
      }
      if (c.currentY() > 1000) {
        c.sleep(3000);
      }
      if (c.currentY() > 1000) {
        c.displayMessage("@or1@Something went wrong, walking back up Ladder!");
        c.walkToAsync(615, 3399, 0);
        c.walkToAsync(618, 3396, 0);
        c.walkToAsync(618, 3384, 0);
        c.atObject(618, 3383);
        c.sleep(1280);
      } else {
        townToTower();
      }
      c.setStatus("@or1@Done Thieving..");
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      if (lootLowLevel) {
        totalGuam = totalGuam + c.getInventoryItemCount(165);
        totalMar = totalMar + c.getInventoryItemCount(435);
        totalTar = totalTar + c.getInventoryItemCount(436);
        totalHar = totalHar + c.getInventoryItemCount(437);
      }
      totalRan = totalRan + c.getInventoryItemCount(438);
      totalIrit = totalIrit + c.getInventoryItemCount(439);
      totalAva = totalAva + c.getInventoryItemCount(440);
      totalKwuarm = totalKwuarm + c.getInventoryItemCount(441);
      totalCada = totalCada + c.getInventoryItemCount(442);
      totalDwarf = totalDwarf + c.getInventoryItemCount(443);
      totalLaw = totalLaw + c.getInventoryItemCount(42);
      totalNat = totalNat + c.getInventoryItemCount(40);
      totalLoop = totalLoop + c.getInventoryItemCount(527);
      totalTooth = totalTooth + c.getInventoryItemCount(526);
      totalLeft = totalLeft + c.getInventoryItemCount(1277);
      totalSpear = totalSpear + c.getInventoryItemCount(1092);
      foodInBank = c.getBankItemCount(foodId);
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

      depositAll();
      if (potUp) {
        withdrawAttack(1);
        withdrawStrength(1);
      }
      withdrawFood(foodId, foodWithdrawAmount);
      if (c.getBankItemCount(foodId) == 0) {
        c.setStatus("@red@NO Sharks in the bank, Logging Out!.");
        c.sleep(10000);
        endSession();
      }
      c.closeBank();
      checkInventoryItemCounts();
    }
  }

  private void townToTower() {
    c.setStatus("@gre@Walking back..");
    c.walkTo(609, 573);
    c.walkTo(609, 568);
    c.walkTo(609, 566);
    c.walkTo(613, 562);
    c.walkTo(617, 558);
    c.walkTo(617, 556);
    openDruidTowerSouthToNorth();
    c.setStatus("@gre@Done Walking..");
  }

  private void DruidToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(617, 555);
    openDruidTowerNorthToSouth();
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(617, 558);
    c.walkTo(613, 562);
    c.walkTo(609, 566);
    c.walkTo(609, 577);
    c.walkTo(606, 577);
    c.walkTo(601, 582);
    c.walkTo(601, 596);
    c.walkTo(597, 600);
    c.walkTo(596, 603);
    c.walkTo(591, 603);
    c.walkTo(587, 599);
    c.walkTo(587, 591);
    c.walkTo(587, 581);
    c.walkTo(587, 571);
    c.walkTo(581, 571);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToDruid() {
    c.setStatus("@gre@Walking to Druids..");
    c.walkTo(581, 571);
    c.walkTo(587, 571);
    c.walkTo(587, 581);
    c.walkTo(587, 591);
    c.walkTo(587, 599);
    c.walkTo(591, 603);
    c.walkTo(596, 603);
    c.walkTo(597, 600);
    c.walkTo(601, 596);
    c.walkTo(601, 582);
    c.walkTo(606, 577);
    c.walkTo(609, 577);
    c.walkTo(609, 566);
    c.walkTo(613, 562);
    c.walkTo(617, 558);
    c.walkTo(617, 556);
    c.setStatus("@gre@Opening Druid Gate, South to North(1)..");
    openDruidTowerSouthToNorth();
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Ardy Chaos Druids - By Kaila");
    JLabel label1 = new JLabel("Start in Ardy North Bank or in Chaos Tower!");
    JLabel label2 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label3 = new JLabel("::bank ::bones ::lowlevel :potup ::bloods ::chest1 ::chest2");
    JLabel label4 = new JLabel("Combat Styles ::attack :strength ::defense ::controlled");
    JLabel label5 = new JLabel("Param Format: \"auto\" for ");
    JCheckBox lootBonesCheckbox = new JCheckBox("Bury Bones? only while Npc's Null", true);
    JCheckBox lowLevelHerbCheckbox = new JCheckBox("Loot Low Level Herbs?", true);
    JCheckBox bloodChestCheckbox = new JCheckBox("Loot Blood Chest?", true);
    JCheckBox potUpCheckbox = new JCheckBox("Use regular Atk/Str Pots?", false);
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    foodField.setSelectedIndex(2); // sets default to sharks
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().isEmpty())
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          lootLowLevel = lowLevelHerbCheckbox.isSelected();
          lootBones = lootBonesCheckbox.isSelected();
          foodId = foodIds[foodField.getSelectedIndex()];
          foodName = foodTypes[foodField.getSelectedIndex()];
          fightMode = fightModeField.getSelectedIndex();
          potUp = potUpCheckbox.isSelected();
          bloodChest = bloodChestCheckbox.isSelected();
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
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(lootBonesCheckbox);
    scriptFrame.add(lowLevelHerbCheckbox);
    scriptFrame.add(bloodChestCheckbox);
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
    if (commandText.replace(" ", "").toLowerCase().contains("bank")) {
      c.displayMessage("@or1@Got @red@bank@or1@ command! Going to the Bank!");
      timeToBank = true;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("chest1")) {
      c.displayMessage("@or1@Got thieve @red@blood chest 1@or1@!");
      thieveChest1 = true;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("chest2")) {
      c.displayMessage("@or1@Got thieve @red@blood chest 2@or1@!");
      thieveChest2 = true;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("bones")) {
      if (!lootBones) {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning on bone looting!");
        lootBones = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning off bone looting!");
        lootBones = false;
      }
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("lowlevel")) {
      if (!lootLowLevel) {
        c.displayMessage("@or1@Got toggle @red@lowlevel@or1@, turning on low level herb looting!");
        lootLowLevel = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@lowlevel@or1@, turning off low level herb looting!");
        lootLowLevel = false;
      }
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("bloods")) {
      if (!bloodChest) {
        c.displayMessage("@or1@Got toggle @red@bloods@or1@, turning on blood thieving!");
        bloodChest = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@bloods@or1@, turning off blood thieving!");
        bloodChest = false;
      }
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("potup")) {
      if (!potUp) {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning on regular atk/str pots!");
        potUp = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning off regular atk/str pots!");
        potUp = false;
      }
      c.sleep(100);
    } else if (commandText
        .replace(" ", "")
        .toLowerCase()
        .contains("attack")) { // field is "Controlled", "Aggressive", "Accurate", "Defensive"}
      c.displayMessage("@red@Got Combat Style Command! - Attack Xp");
      c.displayMessage("@red@Switching to \"Accurate\" combat style!");
      fightMode = 2;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("strength")) {
      c.displayMessage("@red@Got Combat Style Command! - Strength Xp");
      c.displayMessage("@red@Switching to \"Aggressive\" combat style!");
      fightMode = 1;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("defense")) {
      c.displayMessage("@red@Got Combat Style Command! - Defense Xp");
      c.displayMessage("@red@Switching to \"Defensive\" combat style!");
      fightMode = 3;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("controlled")) {
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
    } else if (message.contains("You open the chest")) {
      openChest++;
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
      int natSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      int herbSuccessPerHr = 0;
      int foodUsedPerHr = 0;
      int chestSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        guamSuccessPerHr = (int) ((totalGuam + inventGuam) * scale);
        marSuccessPerHr = (int) ((totalMar + inventMar) * scale);
        tarSuccessPerHr = (int) ((totalTar + inventTar) * scale);
        harSuccessPerHr = (int) ((totalHar + inventHar) * scale);
        ranSuccessPerHr = (int) ((totalRan + inventRan) * scale);
        iritSuccessPerHr = (int) ((totalIrit + inventIrit) * scale);
        avaSuccessPerHr = (int) ((totalAva + inventAva) * scale);
        kwuSuccessPerHr = (int) ((totalKwuarm + inventKwuarm) * scale);
        cadaSuccessPerHr = (int) ((totalCada + inventCada) * scale);
        dwarSuccessPerHr = (int) ((totalDwarf + inventDwarf) * scale);
        lawSuccessPerHr = (int) ((totalLaw + inventLaws) * scale);
        natSuccessPerHr = (int) ((totalNat + inventNats) * scale);
        GemsSuccessPerHr = (int) ((totalGems + inventGems) * scale);
        herbSuccessPerHr = (int) ((totalHerbs + inventHerbs) * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
        foodUsedPerHr = (int) (usedFood * scale);
        chestSuccessPerHr = (int) (openChest * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      int y2 = 220;
      c.drawString("@red@Ardy Chaos Druid Tower @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      if (lootLowLevel) {
        c.drawString(
            "@whi@Guam: @gre@"
                + (totalGuam + inventGuam)
                + "@yel@ (@whi@"
                + String.format("%,d", guamSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Mar: @gre@"
                + (totalMar + inventMar)
                + "@yel@ (@whi@"
                + String.format("%,d", marSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Tar: @gre@"
                + (totalTar + inventTar)
                + "@yel@ (@whi@"
                + String.format("%,d", tarSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + 14,
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Har: @gre@"
                + (totalHar + inventHar)
                + "@yel@ (@whi@"
                + String.format("%,d", harSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Rana: @gre@"
                + (totalRan + inventRan)
                + "@yel@ (@whi@"
                + String.format("%,d", ranSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Irit: @gre@"
                + (totalIrit + inventIrit)
                + "@yel@ (@whi@"
                + String.format("%,d", iritSuccessPerHr)
                + "@yel@/@whi@hr@yel@)",
            x,
            y + (14 * 2),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Ava: @gre@"
                + (totalAva + inventAva)
                + "@yel@ (@whi@"
                + String.format("%,d", avaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Kwu: @gre@"
                + (totalKwuarm + inventKwuarm)
                + "@yel@ (@whi@"
                + String.format("%,d", kwuSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Cada: @gre@"
                + (totalCada + inventCada)
                + "@yel@ (@whi@"
                + String.format("%,d", cadaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 3),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Dwar: @gre@"
                + (totalDwarf + inventDwarf)
                + "@yel@ (@whi@"
                + String.format("%,d", dwarSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Laws: @gre@"
                + (totalLaw + inventLaws)
                + "@yel@ (@whi@"
                + String.format("%,d", lawSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Nats: @gre@"
                + (totalNat + inventNats)
                + "@yel@ (@whi@"
                + String.format("%,d", natSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 4),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Total Gems: @gre@"
                + (totalGems + inventGems) // remove for regular druids!!!
                + "@yel@ (@whi@"
                + String.format("%,d", GemsSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Total Herbs: @gre@"
                + (totalHerbs + inventHerbs)
                + "@yel@ (@whi@"
                + String.format("%,d", herbSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 5),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Tooth: @gre@"
                + (totalTooth + inventTooth) // remove for regular druids!!!
                + "@yel@ / @whi@Loop: @gre@"
                + (totalLoop + inventLoop)
                + "@yel@ / @whi@R.Spear: @gre@"
                + (totalSpear + inventSpear)
                + "@yel@ / @whi@Half: @gre@"
                + (totalLeft + inventLeft),
            x,
            y + (14 * 6),
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
            y + (14 * 7),
            0xFFFFFF,
            1);
        c.drawString("@whi@____________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
      } else {
        c.drawString(
            "@whi@Rana: @gre@"
                + (totalRan + inventRan)
                + "@yel@ (@whi@"
                + String.format("%,d", ranSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Irit: @gre@"
                + (totalIrit + inventIrit)
                + "@yel@ (@whi@"
                + String.format("%,d", iritSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Avan: @gre@"
                + (totalAva + inventAva)
                + "@yel@ (@whi@"
                + String.format("%,d", avaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + 14,
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Kwua: @gre@"
                + (totalKwuarm + inventKwuarm)
                + "@yel@ (@whi@"
                + String.format("%,d", kwuSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Cada: @gre@"
                + (totalCada + inventCada)
                + "@yel@ (@whi@"
                + String.format("%,d", cadaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Dwar: @gre@"
                + (totalDwarf + inventDwarf)
                + "@yel@ (@whi@"
                + String.format("%,d", dwarSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 2),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Total Gems: @gre@"
                + (totalGems + inventGems) // remove for regular druids!!!
                + "@yel@ (@whi@"
                + String.format("%,d", GemsSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Total Herbs: @gre@"
                + (totalHerbs + inventHerbs)
                + "@yel@ (@whi@"
                + String.format("%,d", herbSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 3),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Tooth: @gre@"
                + (totalTooth + inventTooth) // remove for regular druids!!!
                + "@yel@ / @whi@Loop: @gre@"
                + (totalLoop + inventLoop)
                + "@yel@ / @whi@R.Spear: @gre@"
                + (totalSpear + inventSpear)
                + "@yel@ / @whi@Half: @gre@"
                + (totalLeft + inventLeft),
            x,
            y + (14 * 4),
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
            y + (14 * 5),
            0xFFFFFF,
            1);
        c.drawString("@whi@____________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
      }
      c.drawString("@whi@____________________", x, y2, 0xFFFFFF, 1);
      if (bloodChest) {
        c.drawString(
            "@whi@Chest's Opened: @gre@"
                + openChest
                + "@yel@ (@whi@"
                + String.format("%,d", chestSuccessPerHr)
                + "@yel@/@whi@hr@yel@)",
            x,
            y2 + 14,
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Bloods: @gre@"
                + (openChest * 2)
                + "@yel@ (@whi@"
                + String.format("%,d", (chestSuccessPerHr * 2))
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Coins: @gre@"
                + ((openChest * 500) / 1000)
                + "@or1@k@yel@ (@whi@"
                + String.format("%,d", ((chestSuccessPerHr * 500) / 1000))
                + "@or1@k@yel@/@whi@hr@yel@) ",
            x,
            y2 + (14 * 2),
            0xFFFFFF,
            1);
        long timeRemainingTillChest1 = nextAttemptChest1 - System.currentTimeMillis();
        long timeRemainingTillChest2 = nextAttemptChest2 - System.currentTimeMillis();
        c.drawString(
            "@whi@Chest 1: "
                + c.msToShortString(timeRemainingTillChest1)
                + " @whi@Chest 2: "
                + c.msToShortString(timeRemainingTillChest2),
            x,
            y2 + (14 * 3),
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
              y2 + (14 * 4),
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
              y2 + (14 * 4),
              0xFFFFFF,
              1);
        }
      } else {
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
              y2 + 14,
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
              y2 + 14,
              0xFFFFFF,
              1);
        }
      }
    }
  }
}
      /*if(timeRemainingTillChest1 == (timeRemainingTillChest2 + 30000L)) { //couldnt get this function working but looks unnecessary with starting timer offset! However, player commands could mess up timing again!
          c.displayMessage("@or1@De-Syncing blood chests, adding 100s to chest 1");
          nextAttemptChest1 = nextAttemptChest1 + 100000L;
      }
      if(timeRemainingTillChest2 == (timeRemainingTillChest1 + 30000L)) {
          c.displayMessage("@or1@De-Syncing blood chests, adding 100s to chest 2");
          nextAttemptChest2 = nextAttemptChest2 + 100000L;
      }*/
