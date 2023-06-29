package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * Edge Dungeon Chaos Druids - By Kaila.
 *
 * <p>Start in Edge bank or near Druids.
 *
 * <p>"FoodId" in bank REQUIRED.
 *
 * <p>
 *
 * <p>Teleport if Pkers Attack option.
 *
 * <p>31 Magic, Laws, Airs, and Earths required for Escape Tele.
 *
 * <p>Unselected, bot WALKS to Edge when Attacked.
 *
 * <p>Selected, bot teleports, then walks to edge.
 *
 * <p>
 *
 * <p>Return to Druids after Escaping option.
 *
 * <p>Unselected, bot will log out after escaping Pkers.
 *
 * <p>Selected, bot will grab more food and return.
 *
 * <p>
 *
 * <p>Options: Combat Style, Loot level Herbs, Loot Bones, Reg pots, Food Type, and Food Withdraw
 * Amount Selection, Chat Command Options, Full top-left GUI, regular atk/str pot option, and
 * Autostart.
 *
 * <p>@Author ~ Kaila
 */
public final class K_EdgeChaosDruids extends K_kailaScript {
  private static boolean isWithinLootzone(int x, int y) {
    return c.distance(215, 3249, x, y) <= 11;
  }

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
    40, // nature rune
    42, // law rune
    // 33,	 //air rune
    // 34, 	 //Earth rune
    // 35,	 //mind runes
    // 36,	 //body runes
    // 1026, //unholy mould
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    1092 // rune spear
  };
  private static final int[] highLevelLoot = {
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
    40, // nature rune
    42, // law rune
    // 33,	 //air rune
    // 34, 	 //Earth rune
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    1092 // rune spear
  };

  public int start(String[] parameters) {
    if (parameters[0].toLowerCase().startsWith("auto")) {
      foodId = 546;
      fightMode = 0;
      foodWithdrawAmount = 1;
      lootLowLevel = true;
      lootBones = true;
      potUp = false;
      c.displayMessage("Got Autostart Parameter");
      c.log("@cya@Auto-Starting script using 1 Shark, controlled, Loot Low Level", "cya");
      scriptStarted = true;
    }
    if (scriptStarted) {
      guiSetup = true;
      startTime = System.currentTimeMillis();
      c.displayMessage("@red@Edge Druid Killer - By Kaila");
      c.displayMessage("@red@Start in Edge bank with Armor");
      c.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
      c.displayMessage("@red@31 Magic Required for escape tele");

      if (c.isInBank()) {
        c.closeBank();
      }
      if (c.currentY() < 3000) {
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

      if (c.getCurrentStat(c.getStatId("Hits")) < eatLvl) {
        eat();
      }
      if (c.getFightMode() != fightMode) {
        c.log("@red@Changing fightmode to " + fightMode);
        c.setFightMode(fightMode);
      }
      if (potUp && !c.isInCombat()) {
        attackBoost();
        strengthBoost();
      }
      if (c.getInventoryItemCount() < 30) {
        if (!c.isInCombat()) {
          if (lootLowLevel) {
            lowLevelLooting();
          } else {
            highLevelLooting();
          }
          c.setStatus("@yel@Attacking Druids");
          ORSCharacter npc = c.getNearestNpcById(270, false);
          if (npc != null) {
            // c.walktoNPC(npc.serverIndex,1);
            c.attackNpc(npc.serverIndex);
            c.sleep(640);
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
            if (c.currentX() != 218 || c.currentY() != 3245) {
              c.walkTo(218, 3245);
              c.sleep(640);
            }
          }
        } else {
          c.sleep(640);
        }
      } else if (c.getInventoryItemCount() == 30
          || c.getInventoryItemCount(foodId) == 0
          || timeToBank) {
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        DruidToBank();
        bank();
        BankToDruid();
        c.sleep(618);
      } else {
        c.sleep(100);
      }
    }
  }

  private void lootBones() {
    for (int lootId : bones) {
      try {
        int[] coords = c.getNearestItemById(lootId);
        if (coords != null && !c.isInCombat() && isWithinLootzone(coords[0], coords[1])) {
          c.setStatus("@yel@No NPCs, Picking bones");
          c.walkToAsync(coords[0], coords[1], 0);
          c.pickupItem(coords[0], coords[1], lootId, true, false);
          c.sleep(640);
          buryBones();
        } else {
          c.sleep(300);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void highLevelLooting() {
    for (int lootId : highLevelLoot) {
      try {
        int[] coords = c.getNearestItemById(lootId);
        if (coords != null && isWithinLootzone(coords[0], coords[1])) {
          c.setStatus("@yel@Looting..");
          c.walkToAsync(coords[0], coords[1], 0);
          c.pickupItem(coords[0], coords[1], lootId, true, false);
          c.sleep(640);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void lowLevelLooting() {
    for (int lootId : lowLevelLoot) {
      try {
        int[] coords = c.getNearestItemById(lootId);
        if (coords != null && isWithinLootzone(coords[0], coords[1])) {
          c.setStatus("@yel@Looting..");
          c.walkToAsync(coords[0], coords[1], 0);
          c.pickupItem(coords[0], coords[1], lootId, true, false);
          c.sleep(640);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void bank() {

    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(1200);

    if (c.isInBank()) {

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

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != foodId) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }

      c.sleep(1400); // Important, leave in

      if (potUp) {
        withdrawAttack(1);
        withdrawStrength(1);
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
        c.setStatus("@red@NO Sharks in the bank, Logging Out!.");
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

  private void DruidToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(210, 3254);
    c.walkTo(200, 3254);
    c.walkTo(196, 3265);
    c.setStatus("@gre@Opening Wildy Gate North to South(1)..");
    c.atObject(196, 3266);
    c.sleep(1000);
    if (c.currentY() == 3265) {
      openEdgeDungGateNorthToSouth();
    }
    c.walkTo(197, 3266);
    c.walkTo(204, 3272);
    c.walkTo(210, 3273);
    if (c.getObjectAtCoord(211, 3272) == 57) {
      c.setStatus("@gre@Opening Edge Gate..");
      c.walkTo(210, 3273);
      c.atObject(211, 3272);
      c.sleep(340);
    }
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(217, 3283);
    c.walkTo(215, 3294);
    c.walkTo(215, 3299);
    c.atObject(215, 3300);
    c.sleep(640);
    c.walkTo(217, 458);
    c.walkTo(221, 447);
    c.walkTo(217, 448);
    c.sleep(640);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToDruid() {
    c.setStatus("@gre@Walking to Druids..");
    c.walkTo(221, 447);
    c.walkTo(217, 458);
    c.walkTo(215, 467);
    c.atObject(215, 468);
    c.sleep(640);
    c.walkTo(217, 3283);
    c.walkTo(211, 3273);
    if (c.getObjectAtCoord(211, 3272) == 57) {
      c.setStatus("@gre@Opening Edge Gate..");
      c.walkTo(211, 3273);
      c.atObject(211, 3272);
      c.sleep(340);
    }
    c.setStatus("@gre@Walking to Druids..");
    c.walkTo(204, 3272);
    c.walkTo(199, 3272);
    c.walkTo(197, 3266);
    c.setStatus("@gre@Opening Wildy Gate, South to North(1)..");
    c.atObject(196, 3266);
    c.sleep(1000);
    if (c.currentY() == 3266) {
      openEdgeDungSouthToNorth();
    }
    c.walkTo(200, 3254);
    c.walkTo(210, 3254);
    c.setStatus("@gre@Done Walking..");
  }

  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("Edge Druid Killer @mag@~ by Kaila");
    JLabel label1 = new JLabel("Start in Edge bank with Gear, requires food in bank!");
    JLabel label2 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label3 = new JLabel("::bank ::bones ::lowlevel :potup");
    JLabel label4 = new JLabel("Combat Styles ::attack :strength ::defense ::c");
    JLabel label5 = new JLabel("Param Format: \"auto\" for controlled, shark, 1");
    JCheckBox lootBonesCheckbox = new JCheckBox("Bury Bones? only while Npc's Null", true);
    JCheckBox lowLevelHerbCheckbox = new JCheckBox("Loot Low Level Herbs?", true);
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
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(lootBonesCheckbox);
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
      int natSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      int herbSuccessPerHr = 0;
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
        natSuccessPerHr = (int) (totalNat * scale);
        GemsSuccessPerHr = (int) (totalGems * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
        herbSuccessPerHr = (int) (totalHerbs * scale);
        foodUsedPerHr = (int) (usedFood * scale);

      } catch (Exception e) {
        // divide by zero
      }

      int x = 6;
      int y = 15;
      int y2 = 202;
      c.drawString("@red@Edge Chaos Druids @gre@by Kaila", x, y - 3, 0xFFFFFF, 1);
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
            y2 + (14 * 2),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@" + foodName + "'s in Bank: @gre@ Unknown", x, y2 + (14 * 3), 0xFFFFFF, 1);
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
            y2 + (14 * 2),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@" + foodName + "'s in Bank: @gre@" + foodInBank, x, y2 + (14 * 3), 0xFFFFFF, 1);
      }
    }
  }
}
