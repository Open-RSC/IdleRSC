package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * Taverly Druid Circle - By Kaila.
 *
 * <p>Options: Combat Style, Loot level Herbs, Loot Bones, Reg pots, Alter Prayer Boost, Food Type,
 * and Food Withdraw Amount Selection, Chat Command Options, Full top-left GUI, regular atk/str pot
 * option, and Autostart.
 *
 * <p>Author - Kaila
 */
public final class K_TavDruidCircle extends IdleScript {
  private static final Controller c = Main.getController();
  private static JFrame scriptFrame = null;
  private static String foodName = "";
  private static boolean guiSetup = false;
  private static boolean scriptStarted = false;
  private static boolean timeToBank = false;
  private static boolean timeToBankStay = false;
  private static boolean lootLowLevel = true;
  private static boolean lootBones = true;
  private static boolean potUp = false;
  private static boolean prayerBoost = true;

  private static boolean isWithinLootzone(int x, int y) {
    return c.distance(362, 462, x, y) <= 12; // center of lootzone
  }

  private static int totalGuam = 0;
  private static int totalMar = 0;
  private static int totalTar = 0;
  private static int totalHar = 0;
  private static int totalRan = 0;
  private static int totalIrit = 0;
  private static int totalAva = 0;
  private static int totalKwuarm = 0;
  private static int totalCada = 0;
  private static int totalDwarf = 0;
  private static int totalLaw = 0;
  private static int totalNat = 0;
  private static int totalFire = 0;
  private static int totalWater = 0;
  private static int totalEarth = 0;
  private static int totalChaos = 0;
  private static int totalRunes = 0;
  private static int totalHerbs = 0;
  private static int totalTrips = 0;
  private static int foodInBank = -1;
  private static int usedFood = 0;
  private static int foodWithdrawAmount = 1;
  private static int fightMode = 0;
  private static int foodId = -1;
  private static long startTime;
  private static final long startTimestamp = System.currentTimeMillis() / 1000L;
  private static final int[] bones = {
    20, // regular
    413, // big
    604, // bat?
    814 // dragon
  };
  private static final int[] attackPot = {
    476, // reg attack pot (1)
    475, // reg attack pot (2)
    474 // reg attack pot (3)
  };
  private static final int[] strPot = {
    224, // reg str pot (1)
    223, // reg str pot (2)
    222 // reg str pot (3)
  };
  private static final int[] lowLevelLoot = {
    165, // Grimy Guam
    435, // Grimy mar
    436, // Grimy tar
    437, // Grimy har
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
    42, // law rune
    32, // water rune
    34, // Earth rune
    31, // fire rune
    41 // chaos rune
  };
  private static final int[] highLevelLoot = {
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
    42, // law rune
    32, // water rune
    34, // Earth rune
    31, // fire rune
    41 // chaos rune
  };
  private static final int[] foodIds = {
    1191, // cooked Manta Ray
    1193, // cooked Sea Turtle
    546, // cooked shark
    370, // cooked swordfish
    367, // cooked tuna
    373, // cooked lobster
    555, // cooked Bass
    553, // cooked Mackerel
    551, // cooked Cod
    364, // cooked Pike
    362, // cooked Herring
    357, // cooked Salmon
    359, // cooked Trout
    352, // cooked Anchovies
    350, // cooked Shrimp
    132 // cooked Meat
  };
  private static final String[] foodTypes =
      new String[] {
        "Manta Ray",
        "Sea Turtle",
        "Shark",
        "Swordfish",
        "Tuna",
        "Lobster",
        "Bass",
        "Mackerel",
        "Cod",
        "Pike",
        "Herring",
        "Salmon",
        "Trout",
        "Anchovies",
        "Shrimp",
        "Cooked Meat"
      };

  private void whatIsFoodName() {
    if (foodId == 1191) {
      foodName = "Manta Ray";
    } else if (foodId == 1193) {
      foodName = "Sea Turtle";
    } else if (foodId == 546) {
      foodName = "Shark";
    } else if (foodId == 370) {
      foodName = "Swordfish";
    } else if (foodId == 367) {
      foodName = "Tuna";
    } else if (foodId == 373) {
      foodName = "Lobster";
    } else if (foodId == 555) {
      foodName = "Bass";
    } else if (foodId == 553) {
      foodName = "Mackerel";
    } else if (foodId == 551) {
      foodName = "Cod";
    } else if (foodId == 364) {
      foodName = "Pike";
    } else if (foodId == 362) {
      foodName = "Herring";
    } else if (foodId == 357) {
      foodName = "Salmon";
    } else if (foodId == 359) {
      foodName = "Trout";
    } else if (foodId == 352) {
      foodName = "Anchovies";
    } else if (foodId == 350) {
      foodName = "Shrimp";
    } else if (foodId == 132) {
      foodName = "Cooked Meat";
    }
  }

  public int start(String[] parameters) {
    if (parameters[0].toLowerCase().startsWith("auto")) {
      foodId = 546;
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
    }
    if (scriptStarted) {
      startTime = System.currentTimeMillis();
      c.displayMessage("@red@Tav Druid Circle - By Kaila");
      c.displayMessage("@red@Start in Fally west or druid Circle");
      c.displayMessage("@red@Food in Bank required");

      if (c.isInBank()) {
        c.closeBank();
      }
      if (c.currentY() > 515) {
        bank();
        BankToDruid();
        c.sleep(1380);
      }
      whatIsFoodName();
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
      int eatLvl = c.getBaseStat(c.getStatId("Hits")) - 20;

      buryBones();
      if (c.getCurrentStat(c.getStatId("Hits")) < eatLvl) {
        eat();
      }
      if (c.currentY() > 473) {
        c.log("currentY: " + c.currentY() + " Wandered too far, Walking Back to center", "@red@");
        c.walkTo(362, 464);
        c.sleep(640);
      }
      if (c.getFightMode() != fightMode) {
        c.log("@red@Changing fightmode to " + fightMode);
        c.setFightMode(fightMode);
      }
      if (potUp && !c.isInCombat()) {
        if (c.getCurrentStat(c.getStatId("Attack")) == c.getBaseStat(c.getStatId("Attack"))) {
          if (c.getInventoryItemCount(attackPot[0]) > 0
              || c.getInventoryItemCount(attackPot[1]) > 0
              || c.getInventoryItemCount(attackPot[2]) > 0) {
            attackBoost();
          }
        }
        if (c.getCurrentStat(c.getStatId("Strength")) == c.getBaseStat(c.getStatId("Strength"))) {
          if (c.getInventoryItemCount(strPot[0]) > 0
              || c.getInventoryItemCount(strPot[1]) > 0
              || c.getInventoryItemCount(strPot[2]) > 0) {
            strengthBoost();
          }
        }
      }
      if (c.getInventoryItemCount() < 30 && c.getInventoryItemCount(foodId) > 0 && !timeToBank) {
        if (!c.isInCombat()) {
          if (prayerBoost) {
            recharge();
            pray();
          }
          if (lootLowLevel) {
            lowLevelLooting();
          } else {
            highLevelLooting();
          }
          c.setStatus("@yel@Attacking Druids");
          ORSCharacter npc = c.getNearestNpcById(200, false);
          if (npc != null) {
            c.attackNpc(npc.serverIndex);
            c.sleep(2000);
          } else if (lootBones) {
            if (lootLowLevel) {
              lowLevelLooting();
            } else {
              highLevelLooting();
            }
            lootBones();
          } else {
            if (lootLowLevel) {
              lowLevelLooting();
            } else {
              highLevelLooting();
            }
            c.sleep(100);
          }
        } else {
          c.sleep(640);
        }
      } else if (c.getInventoryItemCount() == 30
          || c.getInventoryItemCount(foodId) == 0
          || timeToBank
          || timeToBankStay) {
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        DruidToBank();
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          c.displayMessage(
              "@red@Click on Start Button Again@or1@, to resume the script where it left off (preserving statistics)");
          c.setStatus("@red@Stopping Script.");
          c.setAutoLogin(false);
          c.stop();
        }
        BankToDruid();
        c.sleep(618);
      } else {
        c.sleep(100);
      }
    }
  }

  private void lootBones() {
    for (int lootId : bones) {
      int[] lootCoord = c.getNearestItemById(lootId);
      if (lootCoord != null && !c.isInCombat() && isWithinLootzone(lootCoord[0], lootCoord[1])) {
        c.setStatus("@red@No NPCs, Picking bones");
        c.pickupItem(lootCoord[0], lootCoord[1], lootId, true, false);
        c.sleep(618);
        buryBones();
      } else {
        buryBones();
        c.sleep(100);
      }
    }
  }

  private void highLevelLooting() {
    for (int lootId : highLevelLoot) {
      int[] coords = c.getNearestItemById(lootId);
      if (coords != null && isWithinLootzone(coords[0], coords[1])) {
        c.setStatus("@yel@Looting..");
        c.walkTo(coords[0], coords[1]);
        c.pickupItem(coords[0], coords[1], lootId, true, true);
        c.sleep(618);
      }
    }
  }

  private void lowLevelLooting() {
    for (int lootId : lowLevelLoot) {
      int[] coords = c.getNearestItemById(lootId);
      if (coords != null && isWithinLootzone(coords[0], coords[1])) {
        c.setStatus("@yel@Looting..");
        c.walkTo(coords[0], coords[1]);
        c.pickupItem(coords[0], coords[1], lootId, true, true);
        c.sleep(618);
      }
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(1200);

    if (c.isInBank()) {
      //       32,      //water rune
      // 34, 	 //Earth rune
      // 31,      //fire rune  +total runes
      // 41       //chaos rune
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
        if (c.getInventoryItemCount(attackPot[0]) < 1
            && c.getInventoryItemCount(attackPot[1]) < 1
            && c.getInventoryItemCount(attackPot[2]) < 1) { // withdraw 10 shark if needed
          c.withdrawItem(attackPot[2], 1);
          c.sleep(340);
        }
        if (c.getInventoryItemCount(strPot[0]) < 1
            && c.getInventoryItemCount(strPot[1]) < 1
            && c.getInventoryItemCount(strPot[2]) < 1) { // withdraw 10 shark if needed
          c.withdrawItem(strPot[2], 1);
          c.sleep(340);
        }
      }
      if (c.getInventoryItemCount(foodId) > foodWithdrawAmount) { // deposit extra shark
        c.depositItem(foodId, c.getInventoryItemCount(foodId) - foodWithdrawAmount);
        c.sleep(640);
      }
      if (c.getInventoryItemCount(foodId) < foodWithdrawAmount) { // withdraw 1 shark
        c.withdrawItem(foodId, foodWithdrawAmount - c.getInventoryItemCount(foodId));
        c.sleep(640);
      }
      if (c.getBankItemCount(foodId) == 0) {
        c.setStatus("@red@NO foodId in the bank, Logging Out!.");
        c.sleep(3000);
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      c.closeBank();
      c.sleep(1000);
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
    if (!c.isPrayerOn(c.getPrayerId("Ultimate Strength")) && c.currentY() < 475) {
      c.enablePrayer(c.getPrayerId("Ultimate Strength"));
    }
    // if(!c.isPrayerOn(c.getPrayerId("Incredible Reflexes")) && c.currentY() < 475) {
    //    c.enablePrayer(c.getPrayerId("Incredible Reflexes"));
    // }
  }

  private void buryBones() {
    if (!c.isInCombat()) {
      for (int id : bones) {
        if (c.getInventoryItemCount(id) > 0) {
          c.setStatus("@red@Burying bones..");
          c.itemCommand(id);

          c.sleep(618);
          buryBones();
        }
      }
    }
  }

  private void eat() {
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
      c.setStatus("@red@We've ran out of Food! Running Away!.");
      DruidToBank();
      bank();
      BankToDruid();
      c.sleep(618);
    }
  }

  private void leaveCombat() {
    c.setStatus("@red@Leaving combat..");
    c.walkTo(c.currentX(), c.currentY(), 0, true);
    c.sleep(600);
    for (int i = 1; i <= 15; i++) {
      if (c.isInCombat()) {
        c.setStatus("@red@Leaving combat..");
        c.walkTo(c.currentX(), c.currentY(), 0, true);
        c.sleep(600);
      }
      c.sleep(100);
    }
    c.setStatus("@gre@Done Leaving combat..");
  }

  private void attackBoost() {
    leaveCombat();
    if (c.getInventoryItemCount(attackPot[0]) > 0) {
      c.itemCommand(attackPot[0]);
      c.sleep(320);
    } else if (c.getInventoryItemCount(attackPot[1]) > 0) {
      c.itemCommand(attackPot[1]);
      c.sleep(320);
    } else if (c.getInventoryItemCount(attackPot[2]) > 0) {
      c.itemCommand(attackPot[2]);
      c.sleep(320);
    }
  }

  private void strengthBoost() {
    leaveCombat();
    if (c.getInventoryItemCount(strPot[0]) > 0) {
      c.itemCommand(strPot[0]);
      c.sleep(320);
    } else if (c.getInventoryItemCount(strPot[1]) > 0) {
      c.itemCommand(strPot[1]);
      c.sleep(320);
    } else if (c.getInventoryItemCount(strPot[2]) > 0) {
      c.itemCommand(strPot[2]);
      c.sleep(320);
    }
  }

  private void tavGateEastToWest() {
    for (int i = 1; i <= 15; i++) {
      if (c.currentX() == 341 && c.currentY() < 489 && c.currentY() > 486) {
        c.atObject(341, 487); // gate won't break if someone else opens it
        c.sleep(800);
      }
      c.sleep(10);
    }
  }

  private void tavGateWestToEast() {
    for (int i = 1; i <= 15; i++) {
      if (c.currentX() == 342 && c.currentY() < 489 && c.currentY() > 486) {
        c.atObject(341, 487); // gate won't break if someone else opens it
        c.sleep(800);
      }
      c.sleep(10);
    }
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
  // GUI stuff below (icky)
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
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    fightModeField.setSelectedIndex(0); // sets default to controlled
    foodField.setSelectedIndex(2); // sets default to sharks
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals(""))
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          lootLowLevel = lowLevelHerbCheckbox.isSelected();
          lootBones = lootBonesCheckbox.isSelected();
          foodId = foodIds[foodField.getSelectedIndex()];
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
      c.drawString("@red@Taverly Druid Circle @gre@by Kaila", x, y - 3, 0xFFFFFF, 1);
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
