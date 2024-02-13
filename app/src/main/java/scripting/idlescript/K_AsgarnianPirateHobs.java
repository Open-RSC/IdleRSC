package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Ice Dungeon Hob/Pirate Killer</b>
 *
 * <p>Start in Fally East bank or in Ice Cave. <br>
 * Food in bank required. (pots optional).<br>
 * Use regular Atk/Str Pots Option.<br>
 * Food Withdraw amount Selection.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_AsgarnianPirateHobs extends K_kailaScript {
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
    ItemId.RUNE_SPEAR.getId(),
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    centerX = 282;
    centerY = 3522;
    centerDistance = 14;
    if (parameters[0].toLowerCase().startsWith("auto")) {
      c.displayMessage("Got Autostart, using 1 Shark, yes pots", 0);
      System.out.println("Got Autostart, using 1 Shark, yes pots");
      lootBones = true;
      foodWithdrawAmount = 1;
      potUp = true;
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
      c.displayMessage("@red@Asgarnian Pirate Hobs - By Kaila");
      c.displayMessage("@red@Start in Fally East bank with Armor");
      c.displayMessage("@red@Sharks IN BANK REQUIRED");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 3000) {
        bank();
        BankToIce();
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
        IceToBank();
        timeToBank = false;
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          c.displayMessage("@red@Click on Start Button Again@or1@, to resume");
          endSession();
        }
        BankToIce();
      }
      if (potUp) {
        attackBoost(0, false);
        strengthBoost(0, false);
      }
      lootItems(false, loot);
      if (lootBones) lootItem(false, ItemId.BONES.getId());
      buryBones(false);
      checkFightMode(fightMode);
      checkInventoryItemCounts();
      if (!c.isInCombat()) {
        int[] npcIds = {67, 137};
        ORSCharacter npc = c.getNearestNpcByIds(npcIds, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking..");
          c.attackNpc(npc.serverIndex);
          c.sleep(2 * GAME_TICK);
        } else c.sleep(GAME_TICK);
      } else c.sleep(GAME_TICK);
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(false, 1, ItemId.EMPTY_VIAL.getId());
        buryBonesToLoot(false);
      }
      if (c.currentX() > 295 && c.currentY() > 3000) {
        c.setStatus("@yel@Too far West, walking back..");
        c.walkTo(283, 3521);
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
      totalGems =
          totalGems
              + c.getInventoryItemCount(160)
              + c.getInventoryItemCount(159)
              + c.getInventoryItemCount(158)
              + c.getInventoryItemCount(157);

      if (c.getInventoryItemCount() > 2) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 476
              && itemId != 475
              && itemId != 224
              && itemId != 223) { // dont deposit partial potions!
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
      }
      c.sleep(640);
      if (potUp) {
        withdrawAttack(1);
        withdrawStrength(1);
      }
      withdrawItem(foodId, foodWithdrawAmount);
      bankItemCheck(foodId, 5);
      c.closeBank();
      checkInventoryItemCounts();
    }
  }

  private void IceToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(280, 3521);
    c.walkTo(279, 3531);
    c.walkTo(279, 3540);
    c.walkTo(285, 3544);
    c.atObject(285, 3543); // go up ladder
    c.sleep(1000);
    c.walkTo(287, 711);
    c.walkTo(287, 694);
    c.walkTo(287, 680);
    c.walkTo(287, 673);
    c.walkTo(287, 665);
    c.walkTo(287, 652);
    c.walkTo(289, 650);
    c.walkTo(288, 649);
    c.walkTo(288, 639);
    c.walkTo(288, 629);
    c.walkTo(288, 619);
    c.walkTo(288, 609);
    c.walkTo(290, 607);
    c.walkTo(290, 597);
    c.walkTo(290, 587);
    c.walkTo(290, 577);
    c.walkTo(290, 575);
    c.walkTo(286, 571);
    c.sleep(640);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToIce() {
    c.setStatus("@gre@Walking to Ice Dungeon..");
    c.walkTo(287, 571);
    c.walkTo(290, 575);
    c.walkTo(290, 577);
    c.walkTo(290, 587);
    c.walkTo(290, 597);
    c.walkTo(290, 607);
    c.walkTo(288, 609);
    c.walkTo(288, 619);
    c.walkTo(288, 629);
    c.walkTo(288, 639);
    c.walkTo(288, 649);
    c.walkTo(289, 650);
    c.walkTo(287, 652);
    c.walkTo(287, 665);
    c.walkTo(287, 673);
    c.walkTo(287, 680);
    c.walkTo(287, 694);
    c.walkTo(287, 711);
    c.walkTo(285, 712);
    c.atObject(285, 711); // go down ladder
    c.sleep(1000);
    c.walkTo(282, 3543);
    c.walkTo(285, 3544);
    c.walkTo(279, 3540);
    c.walkTo(279, 3531);
    c.walkTo(280, 3521);
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Ice Dungeon Hob/Pirate Killer ~ By Kaila");
    JLabel label1 = new JLabel("Start in Fally East bank or In Ice Cave");
    JLabel label2 = new JLabel("Food in bank REQUIRED (pots optional)");
    JLabel label3 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label4 = new JLabel("::bank ::bankstay");
    JLabel label5 = new JLabel("Styles ::attack :strength ::defense ::controlled");
    JCheckBox lootBonesCheckbox = new JCheckBox("Bury Bones? only while Npc's Null", true);
    JCheckBox potUpCheckbox = new JCheckBox("Use regular Atk/Str Pots?", true);
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    foodField.setSelectedIndex(5); // sets default to lobs
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    JLabel blankLabel = new JLabel("          ");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals(""))
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());

          potUp = potUpCheckbox.isSelected();
          lootBones = lootBonesCheckbox.isSelected();
          fightMode = fightModeField.getSelectedIndex();
          foodId = foodIds[foodField.getSelectedIndex()];
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          startTime = System.currentTimeMillis();
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
    scriptFrame.add(potUpCheckbox);
    scriptFrame.add(fightModeLabel);
    scriptFrame.add(fightModeField);
    scriptFrame.add(foodLabel);
    scriptFrame.add(foodField);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(blankLabel);
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
    } else if (commandText.contains("potup")) {
      if (!potUp) {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning on regular atk/str pots!");
        potUp = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning off regular atk/str pots!");
        potUp = false;
      }
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
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
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
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      c.drawString("@red@Asgarnian Pirate Hobs @whi@~ @mag@Kaila", 330, 48, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Guams: @gre@"
              + (totalGuam + inventGuam)
              + "@yel@ (@whi@"
              + String.format("%,d", guamSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          62,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Marrentills: @gre@"
              + (totalMar + inventMar)
              + "@yel@ (@whi@"
              + String.format("%,d", marSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          76,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tarromins: @gre@"
              + (totalTar + inventTar)
              + "@yel@ (@whi@"
              + String.format("%,d", tarSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          90,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Harralanders: @gre@"
              + (totalHar + inventHar)
              + "@yel@ (@whi@"
              + String.format("%,d", harSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          104,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Ranarrs: @gre@"
              + (totalRan + inventRan)
              + "@yel@ (@whi@"
              + String.format("%,d", ranSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          118,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Irit Herbs: @gre@"
              + (totalIrit + inventIrit)
              + "@yel@ (@whi@"
              + String.format("%,d", iritSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          132,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Avantoes: @gre@"
              + (totalAva + inventAva)
              + "@yel@ (@whi@"
              + String.format("%,d", avaSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          146,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Kwuarms: @gre@"
              + (totalKwuarm + inventKwuarm)
              + "@yel@ (@whi@"
              + String.format("%,d", kwuSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          160,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Cadantines: @gre@"
              + (totalCada + inventCada)
              + "@yel@ (@whi@"
              + String.format("%,d", cadaSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          174,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Dwarfs: @gre@"
              + (totalDwarf + inventDwarf)
              + "@yel@ (@whi@"
              + String.format("%,d", dwarSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          188,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Laws: @gre@"
              + (totalLaw + inventLaws)
              + "@yel@ (@whi@"
              + String.format("%,d", lawSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          202,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Nats: @gre@"
              + (totalNat + inventNats)
              + "@yel@ (@whi@"
              + String.format("%,d", natSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          216,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Gems: @gre@"
              + (totalGems + inventGems)
              + "@yel@ (@whi@"
              + String.format("%,d", GemsSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          230,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tooth: @gre@"
              + (totalTooth + inventTooth)
              + "@yel@ / @whi@Loop: @gre@"
              + (totalLoop + inventLoop),
          350,
          244,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@R.Spear: @gre@"
              + (totalSpear + inventSpear)
              + "@yel@ / @whi@Shield Half: @gre@"
              + (totalLeft + inventLeft),
          350,
          258,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          272,
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, 350, 286, 0xFFFFFF, 1);
    }
  }
}
