package scripting.idlescript;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.PrayerId;
import orsc.ORSCharacter;

/**
 * <b>Taverley Hellhounds (agility Pipe Shortcut)</b>.
 *
 * <p>Start in Fally west bank with gear, or in Hellhound room. <br>
 * Uses Coleslaw agility pipe shortcut. <br>
 * 70 Agility required for the shortcut. <br>
 * Sharks/pray pots/Laws/Airs/Earths IN BANK REQUIRED. Super atk/str pots suggested. <br>
 * 37 Magic required for escape tele, 37 Prayer for Paralyze Monster. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Nugs
 */
public final class N_TavHellhoundPipe extends K_kailaScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.COMBAT, Category.MELEE, Category.PRAYER, Category.IRONMAN_SUPPORTED
          },
          "Nugs",
          "Fights Hellhounds in the Taverley Dungeon.");

  private boolean d2hWield = false;
  private boolean craftCapeTeleport = false;
  private boolean timeToDrinkAntidote = false;
  private int fightMode = 0;

  private static final int DRAGON_TWO_HAND = ItemId.DRAGON_2_HANDED_SWORD.getId();
  private static final int ANTI_DRAGON_SHIELD = ItemId.ANTI_DRAGON_BREATH_SHIELD.getId();
  private static final int CRAFT_CAPE = ItemId.CRAFTING_CAPE.getId();
  private static final int PARALYZE_MONSTER = PrayerId.PARALYZE_MONSTER.getId();
  private static final int CURE_POISON_3 = ItemId.CURE_POISON_POTION_3DOSE.getId();
  private static final int CURE_POISON_2 = ItemId.CURE_POISON_POTION_2DOSE.getId();
  private static final int CURE_POISON_1 = ItemId.CURE_POISON_POTION_1DOSE.getId();

  // Hellhounds only drop bones; no loot table needed.
  private static final int[] loot = {};

  /**
   * Entry point for the script. Parameters can be provided via CLI or the script options UI.
   *
   * @param parameters script parameters
   */
  public int start(String[] parameters) {
    // Hellhound camp reached via poison spiders side near (387, 3333).
    centerX = 387;
    centerY = 3333;
    centerDistance = 8;

    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.displayMessage("@red@Taverley Hellhounds - By Nugs");
      c.displayMessage("@red@Start in Fally west with gear on, or in Hellhound room!");
      c.displayMessage("@red@Sharks, Law, Water, Air IN BANK REQUIRED");
      c.displayMessage("@red@70 Agility required, for the shortcut!");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 2800) {
        bank();
        BankToHellhounds();
        c.sleep(1380);
      }
      scriptStart();
    }
    return 1000;
  }

  // Main combat loop.
  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);

      // Swap between shield and D2H for better DPS while staying safe during travel.
      if (d2hWield && !c.isInCombat() && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
        int shieldSlot = c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD);
        if (shieldSlot != -1) {
          c.equipItem(shieldSlot);
          c.sleep(GAME_TICK);
        }
      } else if (d2hWield && c.isInCombat() && !c.isItemIdEquipped(DRAGON_TWO_HAND)) {
        int d2hSlot = c.getInventoryItemSlotIndex(DRAGON_TWO_HAND);
        if (d2hSlot != -1) {
          c.equipItem(d2hSlot);
          c.sleep(GAME_TICK);
        }
      }

      // Handle poison from spiders on the way in.
      if (timeToDrinkAntidote) {
        timeToDrinkAntidote = false;
        if (d2hWield && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
          c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
          c.sleep(340);
        }
        K_kailaScript.drinkAnti(true);
      }

      // Keep prayer up for Paralyze Monster while underground.
      drinkPrayerPotion(31, true);
      prayParalyze();

      // Bank when out of food, out of prayer pots, full inventory, or told to bank.
      if (!eatFood()
          || c.getInventoryItemCount(prayerPot[2]) == 0
          || c.getInventoryItemCount(foodId) == 0
          || c.getInventoryItemCount() == 30
          || timeToBank
          || timeToBankStay) {
        c.setStatus("@red@Full Inv / Out of Food or pots / time to bank");
        timeToBank = false;
        HellhoundsToBank();
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          endSession();
        }
        BankToHellhounds();
      }

      checkFightMode(fightMode);
      if (potUp) {
        superAttackBoost(0, true);
        superStrengthBoost(0, true);
      }
      // Bury bones when enabled; leave combat if needed, then bury (only when actually out of combat).
      if (buryBones) {
        buryBones(true); // one attempt (may leave combat first)
        // When already out of combat, bury a few more to avoid pile-up.
        for (int i = 0; i < 2 && !c.isInCombat(); i++) {
          K_kailaScript.buryBones(false, ItemId.BONES.getId());
          c.sleep(GAME_TICK);
        }
      }

      // Acquire and attack nearest Hellhound.
      if (!c.isInCombat()) {
        ORSCharacter npc = c.getNearestNpcById(294, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking Hellhounds");
          c.attackNpc(npc.serverIndex);
          c.sleep(2 * GAME_TICK);
        } else c.sleep(GAME_TICK);
      } else c.sleep(GAME_TICK);
    }
  }

  private static void prayParalyze() {
    if (!c.isPrayerOn(PARALYZE_MONSTER) && c.currentY() > 3000) {
      c.enablePrayer(PARALYZE_MONSTER);
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      // Track bones so paint can show bones/hour and bones in bank.
      // #region agent log
      try {
        java.io.FileWriter fw = new java.io.FileWriter("debug-be19d2.log", true);
        java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
        bw.write(
            "{\"sessionId\":\"be19d2\","
                + "\"runId\":\"pre-fix\","
                + "\"hypothesisId\":\"bones-tracking\","
                + "\"location\":\"N_TavHellhoundPipe.bank\","
                + "\"message\":\"Banking bones snapshot\","
                + "\"data\":{"
                + "\"invBones\":"
                + c.getInventoryItemCount(models.entities.ItemId.BONES.getId())
                + ",\"invBigBones\":"
                + c.getInventoryItemCount(models.entities.ItemId.BIG_BONES.getId())
                + ",\"bankBones\":"
                + c.getBankItemCount(models.entities.ItemId.BONES.getId())
                + ",\"bankBigBones\":"
                + c.getBankItemCount(models.entities.ItemId.BIG_BONES.getId())
                + "},"
                + "\"timestamp\":"
                + System.currentTimeMillis()
                + "}\n");
        bw.close();
        fw.close();
      } catch (java.io.IOException ignored) {
      }
      // #endregion agent log

      totalBones = totalBones + c.getInventoryItemCount(ItemId.BONES.getId());
      bankBones = c.getBankItemCount(ItemId.BONES.getId());

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != ItemId.SUPER_ATTACK_POTION_1DOSE.getId()
            && itemId != ItemId.SUPER_ATTACK_POTION_2DOSE.getId()
            && itemId != ItemId.SUPER_STRENGTH_POTION_1DOSE.getId()
            && itemId != ItemId.SUPER_STRENGTH_POTION_2DOSE.getId()
            && itemId != ItemId.RESTORE_PRAYER_POTION_1DOSE.getId()
            && itemId != ItemId.RESTORE_PRAYER_POTION_2DOSE.getId()
            && itemId != CRAFT_CAPE
            && itemId != ANTI_DRAGON_SHIELD
            && itemId != DRAGON_TWO_HAND) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(2000);

      if (d2hWield && (c.getInventoryItemCount(DRAGON_TWO_HAND) < 1))
        withdrawItem(DRAGON_TWO_HAND, 1);
      if (craftCapeTeleport
          && (c.getInventoryItemCount(CRAFT_CAPE) < 1)
          && !c.isItemIdEquipped(CRAFT_CAPE)) {
        withdrawItem(CRAFT_CAPE, 1);
      }
      if (craftCapeTeleport && (c.getInventoryItemCount(CRAFT_CAPE) > 1))
        c.depositItem(CRAFT_CAPE, c.getInventoryItemCount(CRAFT_CAPE) - 1);
      if (!craftCapeTeleport) {
        withdrawItem(airId, 18);
        withdrawItem(lawId, 6);
        withdrawItem(waterId, 6);
      }
      withdrawPrayer(prayPotWithdrawAmount);
      withdrawFood(foodId, foodWithdrawAmount);
      if (!craftCapeTeleport) {
        bankItemCheck(airId, 30);
        bankItemCheck(waterId, 10); // Falador teleport
        bankItemCheck(lawId, 10);
      }
      if (potUp) {
        withdrawSuperAttack(1);
        withdrawSuperStrength(1);
      }
      // Prefer Cure Poison (instant) for spiders on the path; fallback to Antidote.
      withdrawCurePoisonOrAntidote(1);
      bankItemCheck(prayerPot[2], prayPotWithdrawAmount);
      bankItemCheck(antiPot[2], 1);
      bankItemCheck(foodId, foodWithdrawAmount);
      bankCheckAntiDragonShield();
      c.closeBank();
    }
    if (!craftCapeTeleport) {
      inventoryItemCheck(airId, 18);
      inventoryItemCheck(waterId, 6);
      inventoryItemCheck(lawId, 6);
    }
  }

  /** Withdraw up to n Cure Poison potions (instant cure) if in bank; else withdraw Antidote. */
  private void withdrawCurePoisonOrAntidote(int n) {
    int have =
        c.getInventoryItemCount(CURE_POISON_3)
            + c.getInventoryItemCount(CURE_POISON_2)
            + c.getInventoryItemCount(CURE_POISON_1);
    if (have >= n) return;
    int need = n - have;
    if (c.getBankItemCount(CURE_POISON_3) > 0) {
      c.withdrawItem(CURE_POISON_3, need);
      c.sleep(2 * GAME_TICK);
    } else if (c.getBankItemCount(CURE_POISON_2) > 0) {
      c.withdrawItem(CURE_POISON_2, need);
      c.sleep(2 * GAME_TICK);
    } else if (c.getBankItemCount(CURE_POISON_1) > 0) {
      c.withdrawItem(CURE_POISON_1, need);
      c.sleep(2 * GAME_TICK);
    } else {
      withdrawAntidote(n);
    }
  }

  // Escape from Hellhound area to a safe tile before teleporting.
  private void HellhoundsToBank() {
    c.setStatus("@gre@Going to Bank from Hellhounds.");
    if (d2hWield && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
      c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
    }

    // Walk back up into the main corridor away from the dragons.
    c.walkTo(392, 3335);
    c.walkTo(392, 3347);
    c.walkTo(388, 3360);
    c.walkTo(386, 3371);
    c.walkTo(380, 3372);
    c.sleep(640);

    if (craftCapeTeleport && (c.getInventoryItemCount(CRAFT_CAPE) != 0)) {
      c.setStatus("@gre@Casting craft cape teleport.");
      teleportCraftCape();
      if (c.isPrayerOn(PARALYZE_MONSTER)) c.disablePrayer(PARALYZE_MONSTER);
      c.walkTo(347, 600);
      craftGuildDoorEntering(ItemId.ATTACK_CAPE.getId());
      if (d2hWield && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
        c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
        c.sleep(4 * GAME_TICK);
      }
      c.walkTo(347, 607);
      c.walkTo(346, 608);
    } else {
      c.setStatus("@gre@Teleporting to Falador.");
      teleportFalador();
      if (c.isPrayerOn(PARALYZE_MONSTER)) c.disablePrayer(PARALYZE_MONSTER);
      c.walkTo(327, 552);
      if (d2hWield && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
        c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
      }
      c.sleep(GAME_TICK);
    }
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToHellhounds() {
    c.setStatus("@gre@Walking to Hellhounds..");
    if (craftCapeTeleport && (c.getInventoryItemCount(CRAFT_CAPE) != 0)) {
      teleportCraftCape();
      c.walkTo(347, 588);
      c.walkTo(347, 586);
      c.walkTo(343, 581);
      tavGateSouthToNorth();
      c.walkTo(343, 570);
      c.walkTo(343, 560);
      c.walkTo(343, 550);
      c.walkTo(350, 542);
      c.walkTo(356, 536);
      c.walkTo(363, 536);
      c.walkTo(368, 531);
      c.walkTo(375, 524);
      c.walkTo(375, 521);
      c.walkTo(376, 521);
    } else {
      c.walkTo(328, 553);
      // open bank door
      if (c.getObjectAtCoord(327, 552) == 64) {
        c.atObject(327, 552);
        c.sleep(1000);
      }
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
      tavGateEastToWest();
      c.setStatus("@gre@Walking to Tav Dungeon Ladder..");
      c.walkTo(342, 493);
      c.walkTo(352, 503);
      c.walkTo(362, 513);
      c.walkTo(367, 514);
      c.walkTo(374, 521);
      c.walkTo(376, 521);
    }

    if (d2hWield && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
      c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
    }
    c.sleep(GAME_TICK);
    c.atObject(376, 520);
    c.sleep(640);
    c.walkTo(375, 3352);
    if (!c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
      c.setStatus("@red@Not wielding dragonfire shield!.");
      c.setAutoLogin(false);
      c.logout();
      if (!c.isLoggedIn()) {
        c.stop();
      }
    }
    c.atObject(374, 3352);
    c.sleep(640);

    // After pipe, walk corridor then take poison spider side path into Hellhound camp.
    c.walkTo(372, 3364);
    c.walkTo(377, 3369);
    c.walkTo(380, 3372);
    c.enablePrayer(PARALYZE_MONSTER);
    c.sleep(320);
    c.walkTo(386, 3371);
    c.walkTo(388, 3360);
    // Recorded path from ActionRecorder:
    c.walkTo(392, 3347);
    c.walkTo(392, 3343);
    c.walkTo(391, 3339);
    c.walkTo(387, 3340);
    c.walkTo(386, 3336);
    c.walkTo(387, 3332);
    c.walkTo(387, 3333);
    c.setStatus("@gre@Done Walking..");
    drinkPrayerPotion(31, true);
    prayParalyze();
  }

  @Override
  public void cleanup() {
    // No additional static state here; delegate to shared reset logic.
    super.cleanup();
  }

  private void setupGUI() {
    JLabel header = new JLabel("Taverley Hellhounds (Pipe Shortcut) ~ Nugs");
    JLabel label1 = new JLabel("Start in Fally west with gear on, or in Hellhound room!");
    JLabel label2 = new JLabel("Food, P.pots, and teleport method is required");
    JLabel label3 = new JLabel("70 Agility required, for the pipe shortcut");
    JLabel label4 = new JLabel("Bot will attempt to wield dragonfire shield");
    JLabel label5 = new JLabel("When walking through Blue Dragon Room");
    JLabel blankLabel = new JLabel("     ");
    JCheckBox d2hCheckbox = new JCheckBox("Swap to Dragon 2h Sword", true);
    JCheckBox craftCapeCheckbox = new JCheckBox("99 Crafting Cape Teleport Method?", false);
    JCheckBox buryBonesCheckbox = new JCheckBox("Bury Bones?", true);
    JCheckBox potUpCheckbox = new JCheckBox("Use super Atk/Str Pots?", true);
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    foodField.setSelectedIndex(2); // default to sharks
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(4));
    JLabel prayPotWithdrawAmountLabel = new JLabel("Prayer Pot Withdraw amount:");
    JTextField prayPotWithdrawAmountField = new JTextField(String.valueOf(8));
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals("")) {
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          } else {
            foodWithdrawAmount = 4;
          }
          if (!prayPotWithdrawAmountField.getText().equals("")) {
            prayPotWithdrawAmount = Integer.parseInt(prayPotWithdrawAmountField.getText());
          } else {
            prayPotWithdrawAmount = 8;
          }
          d2hWield = d2hCheckbox.isSelected();
          craftCapeTeleport = craftCapeCheckbox.isSelected();
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
    scriptFrame.add(blankLabel);
    scriptFrame.add(d2hCheckbox);
    scriptFrame.add(craftCapeCheckbox);
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
    scriptFrame.setLocationRelativeTo(Main.getRscFrame());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void serverMessageInterrupt(String message) {
    // Match poisoned message (game uses "poisioned"; also accept "poisoned").
    if (message.contains("@gr3@You @gr2@are @gr1@poisioned")
        || (message.toLowerCase().contains("you ") && message.toLowerCase().contains("poison"))) {
      timeToDrinkAntidote = true;
    }
  }

  @Override
  public void chatCommandInterrupt(String commandText) {
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
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning on atk/str pots!");
        potUp = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning off atk/str pots!");
        potUp = false;
      }
      c.sleep(100);
    } else if (commandText.contains("attack")) {
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
      int BonesSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        BonesSuccessPerHr = (int) (totalBones * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      // #region agent log
      try {
        java.io.FileWriter fw = new java.io.FileWriter("debug-be19d2.log", true);
        java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
        bw.write(
            "{\"sessionId\":\"be19d2\","
                + "\"runId\":\"pre-fix\","
                + "\"hypothesisId\":\"bones-paint\","
                + "\"location\":\"N_TavHellhoundPipe.paintInterrupt\","
                + "\"message\":\"Paint stats snapshot\","
                + "\"data\":{"
                + "\"totalBones\":"
                + totalBones
                + ",\"bankBones\":"
                + bankBones
                + ",\"bonesPerHr\":"
                + BonesSuccessPerHr
                + ",\"buryBonesFlag\":"
                + buryBones
                + "},"
                + "\"timestamp\":"
                + System.currentTimeMillis()
                + "}\n");
        bw.close();
        fw.close();
      } catch (java.io.IOException ignored) {
      }
      // #endregion agent log

      int x = 6;
      int y = 15;
      c.drawString("@red@Taverley Hellhounds @whi@~ @mag@Nugs", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Bones: @gre@"
              + totalBones
              + "@yel@ (@whi@"
              + String.format("%,d", BonesSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Bones in Bank: @gre@"
              + bankBones,
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 3), 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y + 3 + (14 * 3), 0xFFFFFF, 1);
    }
  }
}
