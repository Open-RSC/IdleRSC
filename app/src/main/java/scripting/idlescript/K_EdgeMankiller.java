package scripting.idlescript;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Edge Man Killer</b>
 *
 * <p>Options: Combat Style, Loot level Herbs, Reg pots, Alter Prayer Boost, Food Type, and Food
 * Withdraw Amount Selection, Chat Command Options, Full top-left GUI, regular atk/str pot option,
 * and Autostart.
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_EdgeMankiller extends K_kailaScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.COMBAT, Category.MELEE, Category.PRAYER, Category.IRONMAN_SUPPORTED
          },
          "Kaila",
          "Fights Men in Edgeville.");
  private int fightMode = 0;
  private static final int[] loot = {
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
    ItemId.MIND_RUNE.getId(), // mind rune
    ItemId.CHAOS_RUNE.getId(), // chaos rune
    ItemId.EARTH_RUNE.getId(), // Earth rune
    ItemId.FIRE_RUNE.getId(), // fire rune
    ItemId.COINS.getId(), // coins
    ItemId.BRONZE_ARROWS.getId() // Bronze arrow
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    centerX = 213;
    centerY = 442;
    centerDistance = 9;
    if (parameters[0].toLowerCase().startsWith("auto")) {
      foodId = ItemId.SHARK.getId();
      foodName = "Shark";
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
      c.displayMessage("@red@Edge Man Killer ~ Kaila");
      c.displayMessage("@red@Start in Edge bank with Armor");

      if (c.isInBank()) c.closeBank();
      if (c.currentY() > 445) {
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
      if (!eatFood()
          || c.getInventoryItemCount() == 30
          || c.getInventoryItemCount(foodId) == 0
          || timeToBank
          || timeToBankStay) {
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        houseToBank();
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          c.displayMessage("@red@Click on Start Button Again@or1@, to resume");
          endSession();
        }
        bankToHouse();
      }
      if (potUp) {
        attackBoost(0, false);
        strengthBoost(0, false);
      }
      lootItems(false, loot);
      if (lootBones) lootItem(false, ItemId.BONES.getId());
      if (buryBones) buryBones(false);
      checkFightMode(fightMode);
      if (!c.isInCombat()) {
        ORSCharacter npc = c.getNearestNpcById(11, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking..");
          c.attackNpc(npc.serverIndex);
          checkInventoryItemCounts();
          c.sleep(2 * GAME_TICK);
        } else c.sleep(GAME_TICK);
      } else c.sleep(GAME_TICK);
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(false, 1, ItemId.EMPTY_VIAL.getId());
        buryBonesToLoot(false);
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
      totalFire = totalFire + c.getInventoryItemCount(31);
      totalEarth = totalEarth + c.getInventoryItemCount(34);
      totalChaos = totalChaos + c.getInventoryItemCount(41);
      totalBones = totalBones + c.getInventoryItemCount(413);
      foodInBank = c.getBankItemCount(foodId);
      totalRunes = totalFire + totalEarth + totalChaos;
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
      checkInventoryItemCounts();
    }
  }

  private void bankToHouse() {
    c.setStatus("@gre@Walking to Edge House..");
    c.walkTo(217, 447);
    c.walkTo(209, 446);
    c.walkTo(209, 443);
    c.setStatus("@gre@Done Walking..");
  }

  private void houseToBank() {
    c.setStatus("@gre@Walking to Edge Bank..");
    c.walkTo(209, 443);
    c.walkTo(209, 446);
    c.walkTo(217, 447);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Edge Mankiller ~ by Kaila");
    JLabel label1 = new JLabel("Start in Edge House or Edge Bank");
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
          lootBones = lootBonesCheckbox.isSelected();
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
    scriptFrame.setLocationRelativeTo(Main.getRscFrame());
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
        TripSuccessPerHr = (int) (totalTrips * scale);
        herbSuccessPerHr = (int) ((totalHerbs + inventHerbs) * scale);
        runeSuccessPerHr = (int) (totalRunes * scale);
        boneSuccessPerHr = (int) ((bankBones + usedBones) * scale);
        foodUsedPerHr = (int) (usedFood * scale);

      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      int y2 = 220;
      c.drawString("@red@Edge Mankiller @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
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
          "@whi@Total herbs: @gre@"
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
              + (totalRunes + inventRunes)
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
