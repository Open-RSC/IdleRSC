package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * Edge Dungeon Hobs (and Skeleton/Zombie) - by Kaila
 *
 * <p>
 *
 * <p>Options: Combat Style, Loot level Herbs, Reg pots, Alter Prayer Boost, Food Type, and Food
 * Withdraw Amount Selection, Chat Command Options, Full top-left GUI, regular atk/str pot option,
 * and Autostart.
 *
 * <p>- cannot support bone looting with this bot due to the shape of the dungeon
 *
 * <p>@Author - Kaila
 */
public final class K_Edge_DungeonThugs extends K_kailaScript {

  private static final int[] loot = {
    UNID_GUAM, // Grimy Guam
    UNID_MAR, // Grimy mar
    436, // Grimy tar
    437, // Grimy har
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
    10, // coins
    40, // nature rune
    41, // chaos rune
    38, // death rune
    46, // cosmic rune
    42, // law rune
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
    centerX = 199;
    centerY = 3254;
    centerDistance = 7;
    if (parameters[0].toLowerCase().startsWith("auto")) {
      foodId = 546;
      fightMode = 0;
      foodWithdrawAmount = 1;
      potUp = false;
      lootBones = true;
      buryBones = true;
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
      c.displayMessage("@red@Edge Dungeon Thugs ~ Kaila");
      c.displayMessage("@red@Start in Edge bank with Armor");

      if (c.isInBank()) {
        c.closeBank();
      }
      if (c.currentY() < 3000) {
        bank();
        bankToHouse();
        c.sleep(1380);
      }
      whatIsFoodName();
      scriptStart();
    }

    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      boolean ate = eatFood();
      if (!ate) {
        c.setStatus("@red@We've ran out of Food! Running Away!.");
        houseToBank();
        bank();
        bankToHouse();
      }
      checkFightMode();
      if (potUp) {
        attackBoost(0, false);
        strengthBoost(0, false);
      }
      checkInventoryItemCounts();
      if (c.getInventoryItemCount() < 30 && c.getInventoryItemCount(foodId) > 0 && !timeToBank) {
        if (!c.isInCombat()) {
          lootItems(false, loot);
          if (buryBones) buryBones(false);
          ORSCharacter npc = c.getNearestNpcById(251, false);
          if (npc != null) {
            c.setStatus("@yel@Attacking..");
            c.attackNpc(npc.serverIndex);
            c.sleep(2000);
          } else {
            lootItems(false, loot);
            if (lootBones) lootItem(false, BONES);
            c.sleep(100);
          }
        } else {
          c.sleep(640);
        }
      }
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(false, 1, EMPTY_VIAL);
        buryBonesToLoot(false);
      }
      if (c.getInventoryItemCount() == 30
          || c.getInventoryItemCount(foodId) == 0
          || timeToBank
          || timeToBankStay) {
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
      totalDeath = totalDeath + c.getInventoryItemCount(38);
      totalCosmic = totalCosmic + c.getInventoryItemCount(46);
      totalNat = totalNat + c.getInventoryItemCount(40);
      totalChaos = totalChaos + c.getInventoryItemCount(41);

      totalBones = totalBones + c.getInventoryItemCount(20);
      foodInBank = c.getBankItemCount(foodId);
      totalRunes = totalLaw + totalDeath + totalCosmic + totalNat + totalChaos;
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
      c.sleep(1000);
      checkInventoryItemCounts();
    }
  }
  // GUI stuff below (icky)
  private void houseToBank() {
    c.setStatus("@gre@Walking to Bank..");
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

  private void bankToHouse() {
    c.setStatus("@gre@Walking to Thugs..");
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
    c.walkTo(199, 3255);
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Edge Dungeon Thugs ~ by Kaila");
    JLabel label1 = new JLabel("Start by Edge Thugs or Edge Bank");
    JLabel label2 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label3 = new JLabel("::bank ::potup ::lootbones ::burybones");
    JLabel label4 = new JLabel("Styles ::attack :strength ::defense ::controlled");
    JLabel label5 = new JLabel("Param Format: \"auto\"");
    JCheckBox lootBonesCheckbox = new JCheckBox("Pickup Bones?", true);
    JCheckBox buryBonesCheckbox = new JCheckBox("Bury Bones?", true);
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
          lootBones = lootBonesCheckbox.isSelected();
          buryBones = buryBonesCheckbox.isSelected();
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
    scriptFrame.add(buryBonesCheckbox);
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
  public void chatCommandInterrupt(String commandText) { // ::bank ::lowlevel :potup ::prayer
    if (commandText.contains("bank")) {
      c.displayMessage("@or1@Got @red@bank@or1@ command! Going to the Bank!");
      timeToBank = true;
      c.sleep(100);
    } else if (commandText.contains("bankstay")) {
      c.displayMessage("@or1@Got @red@bankstay@or1@ command! Going to the Bank and Staying!");
      timeToBankStay = true;
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
  public void serverMessageInterrupt(String message) {
    if (message.contains("You dig a hole")) {
      usedBones++;
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
      int runeSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      int herbSuccessPerHr = 0;
      int foodUsedPerHr = 0;
      int boneSuccessPerHr = 0;
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
        herbSuccessPerHr = (int) ((totalHerbs + inventHerbs) * scale);
        runeSuccessPerHr = (int) ((totalRunes + inventRunes) * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
        boneSuccessPerHr = (int) ((bankBones + usedBones) * scale);
        foodUsedPerHr = (int) (usedFood * scale);

      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      int y2 = 202;
      c.drawString("@red@Edge Dungeon Thugs @mag@~ by Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
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
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Herbs: @gre@"
              + (totalHerbs + inventHerbs)
              + "@yel@ (@whi@"
              + String.format("%,d", herbSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Runes: @gre@"
              + totalRunes
              + "@yel@ (@whi@"
              + String.format("%,d", runeSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 6),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Bones: @gre@"
              + (bankBones + usedBones)
              + "@yel@ (@whi@"
              + String.format("%,d", boneSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 7),
          0xFFFFFF,
          1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
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
