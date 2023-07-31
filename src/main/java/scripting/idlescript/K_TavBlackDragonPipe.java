package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * <b>Taverly Black dragon (agility Pipe Shortcut)</b>.
 *
 * <p>Start in Edge bank with Armor. <br>
 * Uses Coleslaw agility pipe shortcut. (coleslaw only) <br>
 * 70 Agility required, for the shortcut! <br>
 * Sharks/prayPots/Laws/Airs/Earths IN BANK REQUIRED.<br>
 * 31 Magic Required for escape tele.<br>
 * Bot will attempt to wield dragonfire shield when in blue dragon room.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_TavBlackDragonPipe extends K_kailaScript {
  private static int totalRlong = 0;
  private static int totalMed = 0;
  private static int totalDstone = 0;
  private static final int[] loot = {
    UNID_RANARR, // Grimy Ranarr Weed
    UNID_IRIT, // Grimy Irit
    UNID_AVANTOE, // Grimy Avantoe
    UNID_KWUARM, // Grimy Kwuarm
    UNID_CADA, // Grimy Cadantine
    UNID_DWARF, // Grimy Dwarf Weed
    CHAOS_RUNE, // chaos rune
    DEATH_RUNE, // Death Rune
    BLOOD_RUNE, // blood rune
    NATURE_RUNE, // nature rune
    LAW_RUNE, // law rune
    AIR_RUNE, // air rune
    FIRE_RUNE,
    814, // D Bones
    75, // rune long
    120, // addy plate body
    405, // rune axe
    81, // rune 2h
    93, // rune battle axe
    11, // bronze arrows
    408, // rune bar
    520, // silver cert
    518, // coal cert
    795, // D med
    UNCUT_SAPP, // saph
    UNCUT_EMER, // emerald
    UNCUT_RUBY, // ruby
    UNCUT_DIA, // diamond
    TOOTH_HALF, // tooth half
    LOOP_HALF, // loop half
    LEFT_HALF, // shield (left) half
    RUNE_SPEAR // rune spear
  };

  public int start(String[] parameters) {
    centerX = 408;
    centerY = 3337;
    centerDistance = 22;
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.displayMessage("@red@Tavelry Black Dragons - By Kaila");
      c.displayMessage("@red@Start in Fally west with gear on, or in demon room!");
      c.displayMessage("@red@Sharks, Law, Water, Air IN BANK REQUIRED");
      c.displayMessage("@red@70 Agility required, for the shortcut!");
      if (c.isInBank()) {
        c.closeBank();
      }
      if (c.currentY() < 2800) {
        bank();
        BankToDragons();
        c.sleep(1380);
      }
      c.quitIfAuthentic();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      eat();
      prayPotCheck();
      drinkPrayerPotion(31, true);
      pray();
      foodCheck();
      checkFightMode();
      lootItems(true, loot);
      if (buryBones) buryBones(false);
      if (potUp) {
        superAttackBoost(5, false);
        superStrengthBoost(5, false);
      }
      if (c.getInventoryItemCount() < 30) {
        if (!c.isInCombat()) {
          ORSCharacter npc = c.getNearestNpcById(291, false);
          if (npc != null) {
            c.setStatus("@yel@Attacking Dragons");
            c.attackNpc(npc.serverIndex);
            c.sleep(2 * GAME_TICK);
          } else {
            c.sleep(GAME_TICK);
            lootItems(true, loot);
          }
        } else c.sleep(GAME_TICK);
      }
      if (c.getInventoryItemCount() == 30) {
        prayPotCheck();
        dropItemToLoot(false, 1, EMPTY_VIAL);
        if (buryBones) buryBonesToLoot(false);
        eatFoodToLoot(false);
      }
      if (c.getInventoryItemCount() == 30 || c.getInventoryItemCount(546) == 0) {
        c.setStatus("@red@Full Inv / Out of Food");
        dragonEscape();
        DragonsToBank();
        bank();
        BankToDragons();
      }
    }
  }

  private void eat() {
    boolean ate = eatFood();
    if (!ate) {
      c.setStatus("@red@We've ran out of Food! Running Away!.");
      c.sleep(308);
      dragonEscape();
      DragonsToBank();
      bank();
      BankToDragons();
    }
  }

  private void bank() {
    totalTrips = totalTrips + 1;
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalBones = totalBones + c.getInventoryItemCount(814);
      totalGems =
          totalGems
              + c.getInventoryItemCount(160)
              + c.getInventoryItemCount(159)
              + c.getInventoryItemCount(158)
              + c.getInventoryItemCount(157);
      totalHerb =
          totalHerb
              + c.getInventoryItemCount(438)
              + c.getInventoryItemCount(439)
              + c.getInventoryItemCount(440)
              + c.getInventoryItemCount(441)
              + c.getInventoryItemCount(442)
              + c.getInventoryItemCount(443);
      totalFire = totalFire + c.getInventoryItemCount(31);
      totalLaw = totalLaw + c.getInventoryItemCount(42);
      totalNat = totalNat + c.getInventoryItemCount(40);
      totalDeath = totalDeath + c.getInventoryItemCount(38);
      totalBlood = totalBlood + c.getInventoryItemCount(619);
      totalLoop = totalLoop + c.getInventoryItemCount(527);
      totalTooth = totalTooth + c.getInventoryItemCount(526);
      totalDstone = totalDstone + c.getInventoryItemCount(523);
      totalLeft = totalLeft + c.getInventoryItemCount(1277);
      totalMed = totalMed + c.getInventoryItemCount(795);
      totalSpear = totalSpear + c.getInventoryItemCount(1092);
      totalAddy = totalAddy + c.getInventoryItemCount(120);
      totalRlong = totalRlong + c.getInventoryItemCount(75);
      // prayPotCount() = (c.getInventoryItemCount(483) + c.getInventoryItemCount(483)
      // +c.getInventoryItemCount(483));
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != 486 // dont bank partial potions
            && itemId != 487
            && itemId != 488
            && itemId != 492
            && itemId != 493
            && itemId != 494
            && itemId != 420
            && itemId != 485
            && itemId != 484
            && itemId != 483
            && itemId != 571
            && itemId != 570
            && itemId != 569) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1280); // Important, leave in
      if (potUp) {
        withdrawSuperAttack(1);
        withdrawSuperStrength(1);
      }
      withdrawAntidote(1);
      withdrawPrayer(prayPotWithdrawAmount);
      withdrawFood(foodId, foodWithdrawAmount);
      withdrawItem(airId, 18);
      withdrawItem(lawId, 6);
      withdrawItem(waterId, 6);
      bankBones = c.getBankItemCount(814);
      bankItemCheck(prayerPot[2], 6);
      bankItemCheck(antiPot[2], 1);
      bankItemCheck(foodId, 30);
      bankItemCheck(airId, 30);
      bankItemCheck(waterId, 10); // Falador teleport
      bankItemCheck(lawId, 10);
      bankCheckAntiDragonShield();
      c.closeBank();
      c.sleep(1000);
    }
    inventoryItemCheck(airId, 18);
    inventoryItemCheck(waterId, 6);
    inventoryItemCheck(lawId, 6);
  }

  private void pray() {
    if (!c.isPrayerOn(c.getPrayerId("Paralyze Monster")) && c.currentY() > 3000) {
      c.enablePrayer(c.getPrayerId("Paralyze Monster"));
    }
  }

  private void dragonEscape() {
    c.setStatus("We've ran out of Food/prayPots! @gre@Going to safe zone.");
    c.walkTo(408, 3340);
    c.walkTo(408, 3348);
    c.sleep(1000);
  }

  private void BankToDragons() {
    c.setStatus("@gre@Walking to Black Dragons..");
    c.walkTo(327, 552);
    c.walkTo(324, 549);
    c.walkTo(324, 539);
    c.walkTo(324, 530);
    c.walkTo(317, 523);
    c.walkTo(317, 516);
    c.walkTo(327, 506);
    c.walkTo(327, 506);
    c.walkTo(337, 496);
    c.walkTo(337, 492);
    c.walkTo(341, 488);
    tavGateEastToWest();
    c.setStatus("@gre@Walking to Tav Dungeon Ladder..");
    c.walkTo(342, 493);
    c.walkTo(352, 503);
    c.walkTo(362, 513);
    c.walkTo(367, 514);
    c.walkTo(374, 521);
    c.walkTo(376, 521);
    c.equipItem(c.getInventoryItemSlotIndex(420));
    c.sleep(320);
    c.atObject(376, 520);
    c.sleep(640);
    c.walkTo(375, 3352);
    if (!c.isItemIdEquipped(420)) {
      c.setStatus("@red@Not Wielding Dragonfire Shield!.");
      c.setAutoLogin(false);
      c.logout();
      if (!c.isLoggedIn()) {
        c.stop();
      }
    }
    c.atObject(374, 3352);
    c.sleep(640);
    c.walkTo(372, 3364);
    c.walkTo(377, 3369);
    c.equipItem(c.getInventoryItemSlotIndex(404));
    c.enablePrayer(c.getPrayerId("Paralyze Monster"));
    c.sleep(320);
    c.walkTo(380, 3372);
    eat();
    prayPotCheck();
    drinkPrayerPotion(31, true);
    pray();
    c.walkTo(386, 3371);
    c.walkTo(388, 3360);
    c.walkTo(397, 3347);
    c.walkTo(397, 3343);
    c.walkTo(403, 3346);
    c.walkTo(408, 3344);
    c.walkTo(408, 3340);
    drinkAntidote(true);
    eat();
    prayPotCheck();
    drinkPrayerPotion(31, true);
    pray();
    c.setStatus("@gre@Done Walking..");
  }

  private void prayPotCheck() {
    int prayerPotCount =
        c.getInventoryItemCount(prayerPot[0])
            + c.getInventoryItemCount(prayerPot[1])
            + c.getInventoryItemCount(prayerPot[2]);
    if (prayerPotCount == 0) {
      c.setStatus("@yel@No prayPots, Banking..");
      dragonEscape();
      DragonsToBank();
      bank();
      BankToDragons();
      c.sleep(618);
    }
  }

  private void foodCheck() {
    if (c.getInventoryItemCount(foodId) < 1 || timeToBank || timeToBankStay) {
      c.setStatus("@yel@No food, Banking..");
      dragonEscape();
      DragonsToBank();
      timeToBank = false;
      bank();
      if (timeToBankStay) {
        timeToBankStay = false;
        c.displayMessage(
            "@red@Click on Start Button Again@or1@, to resume the script where it left off (preserving statistics)");
        c.setStatus("@red@Stopping Script.");
        endSession();
      }
      BankToDragons();
      c.sleep(618);
    }
  }

  private void DragonsToBank() {
    c.setStatus("@gre@Going to Bank. Casting 1st teleport.");
    c.castSpellOnSelf(c.getSpellIdFromName("Falador Teleport"));
    c.sleep(1000);
    for (int i = 1; i <= 15; i++) {
      if (c.currentY() > 3000) {
        c.setStatus("@gre@Teleport unsuccessful, Casting teleports.");
        c.castSpellOnSelf(c.getSpellIdFromName("Falador Teleport"));
        c.sleep(1000);
      }
      c.sleep(10);
    }
    c.sleep(308);
    c.walkTo(327, 552);
    c.sleep(308);
    c.setStatus("@gre@Done Walking..");
  }
  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("Tavelry Black Dragons (Pipe) - By Kaila");
    JLabel label1 = new JLabel("Start in Fally west with gear on, or in Demon room!");
    JLabel label2 = new JLabel("Sharks, Law, Water, Air IN BANK required");
    JLabel label3 = new JLabel("70 Agility required, for the shortcut!");
    JLabel label4 = new JLabel("Bot will attempt to wield dragonfire shield");
    JLabel label5 = new JLabel("When walking through Blue Dragon Room");
    JLabel label6 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label7 = new JLabel("::bank ::bankstay ::burybones");
    JLabel label8 = new JLabel("Styles ::attack :strength ::defense ::controlled");
    JCheckBox buryBonesCheckbox = new JCheckBox("Bury Dragon Bones?", false);
    JCheckBox potUpCheckbox = new JCheckBox("Use super Atk/Str Pots?", true);
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(0); // sets default to controlled
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(3));
    JLabel prayPotWithdrawAmountLabel = new JLabel("Prayer Pot Withdraw amount:");
    JTextField prayPotWithdrawAmountField = new JTextField(String.valueOf(6));

    foodField.setSelectedIndex(2); // sets default to sharks
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals("")) {
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          } else {
            foodWithdrawAmount = 3;
          }
          if (!prayPotWithdrawAmountField.getText().equals("")) {
            prayPotWithdrawAmount = Integer.parseInt(prayPotWithdrawAmountField.getText());
          } else {
            prayPotWithdrawAmount = 6;
          }
          buryBones = buryBonesCheckbox.isSelected();
          fightMode = fightModeField.getSelectedIndex();
          foodId = foodIds[foodField.getSelectedIndex()];
          potUp = potUpCheckbox.isSelected();
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
    scriptFrame.add(label6);
    scriptFrame.add(label7);
    scriptFrame.add(label8);
    scriptFrame.add(buryBonesCheckbox);
    scriptFrame.add(potUpCheckbox);
    scriptFrame.add(fightModeLabel);
    scriptFrame.add(fightModeField);
    scriptFrame.add(foodLabel);
    scriptFrame.add(foodField);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(prayPotWithdrawAmountLabel);
    scriptFrame.add(prayPotWithdrawAmountField);
    scriptFrame.add(startScriptButton);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocus();
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
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int DbonesSuccessPerHr = 0;
      int RdaggerSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int FireSuccessPerHr = 0;
      int LawSuccessPerHr = 0;
      int AddySuccessPerHr = 0;
      int HerbSuccessPerHr = 0;
      int DeathSuccessPerHr = 0;
      int BloodSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long timeRanInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = (timeRanInSeconds) - startTimestamp;
        float scale = (60 * 60) / timeRan;
        DbonesSuccessPerHr = (int) (totalBones * scale);
        RdaggerSuccessPerHr = (int) (totalRlong * scale);
        GemsSuccessPerHr = (int) (totalGems * scale);
        FireSuccessPerHr = (int) (totalFire * scale);
        LawSuccessPerHr = (int) (totalLaw * scale);
        AddySuccessPerHr = (int) (totalAddy * scale);
        HerbSuccessPerHr = (int) (totalHerb * scale);
        DeathSuccessPerHr = (int) (totalDeath * scale);
        BloodSuccessPerHr = (int) (totalBlood * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;

      c.drawString("@red@Tav Black Dragons @mag@~ by Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@______________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Gathered D.Bones: @gre@"
              + totalBones
              + "@yel@ (@whi@"
              + String.format("%,d", DbonesSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@D.Bones in Bank: @gre@"
              + bankBones,
          x,
          y + 14,
          0xFFFFFF,
          1); // fix y cords
      c.drawString(
          "@whi@Rune Longs: @gre@"
              + totalRlong
              + "@yel@ (@whi@"
              + String.format("%,d", RdaggerSuccessPerHr)
              + "@yel@/@whi@hr@yel@)"
              + "@whi@Addy Plate Body: @gre@"
              + totalAddy
              + "@yel@ (@whi@"
              + String.format("%,d", AddySuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Laws: @gre@"
              + totalLaw
              + "@yel@ (@whi@"
              + String.format("%,d", LawSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Bloods: @gre@ "
              + totalBlood
              + "@yel@ (@whi@"
              + String.format("%,d", BloodSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Deaths: @gre@"
              + totalDeath
              + "@yel@ (@whi@"
              + String.format("%,d", DeathSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Herbs: @gre@"
              + totalHerb
              + "@yel@ (@whi@"
              + String.format("%,d", HerbSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Total Gems: @gre@"
              + totalGems
              + "@yel@ (@whi@"
              + String.format("%,d", GemsSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tooth: @gre@"
              + totalTooth
              + "@yel@ / @whi@Loop: @gre@"
              + totalLoop
              + "@yel@ / @whi@Dstone: @gre@"
              + totalDstone
              + "@yel@ / @whi@Fires: @gre@"
              + totalFire
              + "@yel@ (@whi@"
              + String.format("%,d", FireSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);

      c.drawString(
          "@whi@D Med: @gre@"
              + totalMed
              + "@yel@ / @whi@Left Half: @gre@"
              + totalLeft
              + "@yel@ / @whi@Rune Spear: @gre@"
              + totalSpear,
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
      c.drawString("@whi@______________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
    }
  }
}
