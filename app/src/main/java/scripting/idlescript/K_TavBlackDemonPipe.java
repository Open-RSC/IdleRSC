package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.PrayerId;
import orsc.ORSCharacter;

/**
 * <b>Tav Black Demons</b>
 *
 * <p>Start in Fally west bank with gear. Uses Coleslaw agility pipe shortcut. <br>
 * Sharks/ppots/Laws/Airs/Earths IN BANK REQUIRED. super atk, super str pots suggested.<br>
 * 37 Magic Required for tele, 37 prayer for paralize monster, 70 agility for shortcut.<br>
 * anti dragon shield required. D2h recommended to demon kill rates don't suck.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_TavBlackDemonPipe extends K_kailaScript {
  private boolean d2hWield = false;
  private boolean craftCapeTeleport = false;
  private int totalMed = 0;
  private int totalDstone = 0;
  private int totalRbar = 0;
  private int totalRunestuff = 0;
  private int totalRchain = 0;
  private int totalRmed = 0;
  private int fightMode = 0;
  private static final int DRAGON_TWO_HAND = ItemId.DRAGON_2_HANDED_SWORD.getId();
  private static final int ANTI_DRAGON_SHIELD = ItemId.ANTI_DRAGON_BREATH_SHIELD.getId();
  private static final int ATTACK_CAPE = ItemId.ATTACK_CAPE.getId();
  private static final int CRAFT_CAPE = ItemId.CRAFTING_CAPE.getId();
  private static final int PARALYZE_MONSTER = PrayerId.PARALYZE_MONSTER.getId();
  private static final int[] loot = {
    ItemId.UNID_RANARR_WEED.getId(),
    ItemId.UNID_IRIT.getId(),
    ItemId.UNID_AVANTOE.getId(),
    ItemId.UNID_KWUARM.getId(),
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
    ItemId.FIRE_RUNE.getId(),
    ItemId.NATURE_RUNE.getId(),
    ItemId.LAW_RUNE.getId(),
    ItemId.COSMIC_RUNE.getId(),
    ItemId.CHAOS_RUNE.getId(),
    ItemId.DEATH_RUNE.getId(),
    ItemId.BLOOD_RUNE.getId(),
    ItemId.AIR_RUNE.getId(),
    ItemId.RUNE_CHAIN_MAIL_BODY.getId(),
    ItemId.MEDIUM_RUNE_HELMET.getId(),
    ItemId.ADAMANTITE_BAR.getId(),
    ItemId.RUNE_KITE_SHIELD.getId(),
    ItemId.RUNE_SQUARE_SHIELD.getId(),
    ItemId.UNCUT_DRAGONSTONE.getId(),
    ItemId.DRAGONSTONE.getId(),
    ItemId.DRAGON_MEDIUM_HELMET.getId(),
    ItemId.RUNE_AXE.getId(),
    ItemId.RUNITE_BAR.getId(),
    ItemId.RUNE_2_HANDED_SWORD.getId(),
    ItemId.RUNE_BATTLE_AXE.getId(),
    ItemId.SILVER_CERTIFICATE.getId(),
    ItemId.COAL_CERTIFICATE.getId(),
    ItemId.UNCUT_SAPPHIRE.getId(),
    ItemId.UNCUT_EMERALD.getId(),
    ItemId.UNCUT_RUBY.getId(),
    ItemId.UNCUT_DIAMOND.getId(),
    ItemId.TOOTH_HALF_KEY.getId(),
    ItemId.LOOP_HALF_KEY.getId(),
    ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.getId(),
    ItemId.RUNE_SPEAR.getId(),
    ItemId.DRAGON_MEDIUM_HELMET.getId()
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    centerX = 390;
    centerY = 3371;
    centerDistance = 10;
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.displayMessage("@red@Taverley Black Demons - By Kaila");
      c.displayMessage("@red@Start in Fally west with gear on, or in demon room!");
      c.displayMessage("@red@Sharks, Law, Water, Air IN BANK REQUIRED");
      c.displayMessage("@red@70 Agility required, for the shortcut!");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 2800) {
        bank();
        BankToDemons();
        c.sleep(1380);
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }
  // Main Script section
  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      drinkPrayerPotion(31, true);
      prayParalyze();
      if (!eatFood()
          || c.getInventoryItemCount(prayerPot[2]) == 0
          || c.getInventoryItemCount(foodId) == 0
          || c.getInventoryItemCount() == 30
          || timeToBank
          || timeToBankStay) {
        c.setStatus("@red@Full Inv / Out of Food or pots / time to bank");
        timeToBank = false;
        demonEscape();
        DemonsToBank();
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          endSession();
        }
        BankToDemons();
      }
      checkFightMode(fightMode);
      eatFoodToLoot(true);
      dropItemToLoot(true, 1, ItemId.EMPTY_VIAL.getId());
      if (potUp) {
        superAttackBoost(0, true);
        superStrengthBoost(0, true);
      }
      lootItems(true, loot);
      if (!c.isInCombat()) {
        ORSCharacter npc = c.getNearestNpcById(290, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking Demons");
          c.attackNpc(npc.serverIndex);
          c.sleep(2 * GAME_TICK);
        } else c.sleep(GAME_TICK);
      } else c.sleep(GAME_TICK);
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalRunestuff =
          totalRunestuff
              + c.getInventoryItemCount(404) // kite
              + c.getInventoryItemCount(403) // sq
              + c.getInventoryItemCount(405) // axe
              + c.getInventoryItemCount(81) // 2h
              + c.getInventoryItemCount(93); // bAxe
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
      totalChaos = totalChaos + c.getInventoryItemCount(41);
      totalDeath = totalDeath + c.getInventoryItemCount(38);
      totalBlood = totalBlood + c.getInventoryItemCount(619);
      totalRbar = totalRbar + c.getInventoryItemCount(408);
      totalLoop = totalLoop + c.getInventoryItemCount(527);
      totalTooth = totalTooth + c.getInventoryItemCount(526);
      totalDstone = totalDstone + c.getInventoryItemCount(523);
      totalLeft = totalLeft + c.getInventoryItemCount(1277);
      totalSpear = totalSpear + c.getInventoryItemCount(1092);
      totalRchain = totalMed + c.getInventoryItemCount(400);
      totalRmed = totalMed + c.getInventoryItemCount(399);
      totalMed = totalMed + c.getInventoryItemCount(795);
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != ItemId.SUPER_ATTACK_POTION_1DOSE.getId()
            && itemId != ItemId.SUPER_ATTACK_POTION_2DOSE.getId()
            && itemId != ItemId.SUPER_STRENGTH_POTION_1DOSE.getId()
            && itemId != ItemId.SUPER_STRENGTH_POTION_2DOSE.getId()
            && itemId != ItemId.RESTORE_PRAYER_POTION_1DOSE.getId()
            && itemId != ItemId.RESTORE_PRAYER_POTION_2DOSE.getId()
            && itemId != ATTACK_CAPE
            && itemId != CRAFT_CAPE
            && itemId != ANTI_DRAGON_SHIELD
            && itemId != DRAGON_TWO_HAND) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(2000); // Important, leave in

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
      if (potUp) {
        withdrawSuperAttack(1);
        withdrawSuperStrength(1);
      }
      withdrawPrayer(prayPotWithdrawAmount);
      withdrawFood(foodId, foodWithdrawAmount);
      if (!craftCapeTeleport) {
        bankItemCheck(airId, 30);
        bankItemCheck(waterId, 10); // Falador teleport
        bankItemCheck(lawId, 10);
      }
      bankItemCheck(prayerPot[2], prayPotWithdrawAmount);
      bankItemCheck(foodId, foodWithdrawAmount);
      bankCheckAntiDragonShield();
      c.closeBank();
      if (!c.isItemIdEquipped(ATTACK_CAPE) && craftCapeTeleport)
        c.equipItem(c.getInventoryItemSlotIndex(ATTACK_CAPE));
    }
    if (!craftCapeTeleport) {
      inventoryItemCheck(airId, 18);
      inventoryItemCheck(waterId, 6);
      inventoryItemCheck(lawId, 6);
    }
  }

  private static void prayParalyze() {
    if (!c.isPrayerOn(PARALYZE_MONSTER) && c.currentY() > 3000) {
      c.enablePrayer(PARALYZE_MONSTER);
    }
  }
  // PATHING private voids
  private void demonEscape() {
    c.setStatus("We've ran out of Stuff! @gre@Going to safe zone.");
    c.walkTo(382, 3372);
    c.walkTo(372, 3372);
    c.sleep(640);
    if (c.currentX() > 374) {
      c.walkTo(372, 3372);
      c.sleep(640);
    }
  }

  private void DemonsToBank() {
    c.setStatus("@gre@Going to Bank");
    if (craftCapeTeleport && (c.getInventoryItemCount(CRAFT_CAPE) != 0)) {
      c.setStatus("@gre@Going to Bank. Casting craft cape teleport.");
      teleportCraftCape();
      if (c.isPrayerOn(PARALYZE_MONSTER)) c.disablePrayer(PARALYZE_MONSTER);
      c.walkTo(347, 600);
      craftGuildDoorEntering(ATTACK_CAPE);
      if (d2hWield && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
        c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
        c.sleep(4 * GAME_TICK);
      }
      c.walkTo(347, 607);
      c.walkTo(346, 608);
    } else {
      teleportFalador();
      if (c.isPrayerOn(PARALYZE_MONSTER)) c.disablePrayer(PARALYZE_MONSTER);
      c.walkTo(327, 552);
    }
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToDemons() {
    c.setStatus("@gre@Walking to Black Demons..");
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
    if (d2hWield) {
      c.equipItem(c.getInventoryItemSlotIndex(1346));
    }
    c.enablePrayer(PARALYZE_MONSTER);
    c.sleep(320);
    c.walkTo(380, 3372);
    c.setStatus("@gre@Done Walking..");
    drinkPrayerPotion(31, true);
    prayParalyze();
  }

  private void setupGUI() {
    JLabel header = new JLabel("Taverley Black Demon (Pipe Shortcut) ~ Kaila");
    JLabel label1 = new JLabel("Start in Fally west with gear on, or in Demon room!");
    JLabel label2 = new JLabel("Food, P.pots, and teleport method is required");
    JLabel label3 = new JLabel("70 Agility required, for the pipe shortcut");
    JLabel label4 = new JLabel("Bot will attempt to wield dragonfire shield");
    JLabel label5 = new JLabel("When walking through Blue Dragon Room");
    JLabel blankLabel = new JLabel("     ");
    JCheckBox d2hCheckbox = new JCheckBox("Swap to Dragon 2h Sword", true);
    JCheckBox craftCapeCheckbox = new JCheckBox("99 Crafting Cape Teleport Method?", false);
    JCheckBox potUpCheckbox = new JCheckBox("Use super Atk/Str Pots?", true);
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    foodField.setSelectedIndex(2); // sets default to sharks
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(2));
    JLabel prayPotWithdrawAmountLabel = new JLabel("Prayer Pot Withdraw amount:");
    JTextField prayPotWithdrawAmountField = new JTextField(String.valueOf(15));
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
          d2hWield = d2hCheckbox.isSelected();
          craftCapeTeleport = craftCapeCheckbox.isSelected();
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
      int RuneSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int FireSuccessPerHr = 0;
      int LawSuccessPerHr = 0;
      int NatSuccessPerHr = 0;
      int ChaosSuccessPerHr = 0;
      int DeathSuccessPerHr = 0;
      int BloodSuccessPerHr = 0;
      int RbarSuccessPerHr = 0;
      int RchainSuccessPerHr = 0;
      int RmedSuccessPerHr = 0;
      int HerbSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        RuneSuccessPerHr = (int) (totalRunestuff * scale);
        GemsSuccessPerHr = (int) (totalGems * scale);
        FireSuccessPerHr = (int) (totalFire * scale);
        LawSuccessPerHr = (int) (totalLaw * scale);
        NatSuccessPerHr = (int) (totalNat * scale);
        ChaosSuccessPerHr = (int) (totalChaos * scale);
        DeathSuccessPerHr = (int) (totalDeath * scale);
        BloodSuccessPerHr = (int) (totalBlood * scale);
        RbarSuccessPerHr = (int) (totalRbar * scale);
        RchainSuccessPerHr = (int) (totalRchain * scale);
        RmedSuccessPerHr = (int) (totalRmed * scale);
        HerbSuccessPerHr = (int) (totalHerb * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Tavelry Black Demons @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Laws: @gre@"
              + totalLaw
              + "@yel@ (@whi@"
              + String.format("%,d", LawSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Natures: @gre@"
              + totalNat
              + "@yel@ (@whi@"
              + String.format("%,d", NatSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Fires: @gre@"
              + totalFire
              + "@yel@ (@whi@"
              + String.format("%,d", FireSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Chaos: @gre@"
              + totalChaos
              + "@yel@ (@whi@"
              + String.format("%,d", ChaosSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Deaths: @gre@"
              + totalDeath
              + "@yel@ (@whi@"
              + String.format("%,d", DeathSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Bloods: @gre@"
              + totalBlood
              + "@yel@ (@whi@"
              + String.format("%,d", BloodSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@R. Chain: @gre@"
              + totalRchain
              + "@yel@ (@whi@"
              + String.format("%,d", RchainSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@R. Med: @gre@"
              + totalRmed
              + "@yel@ (@whi@"
              + String.format("%,d", RmedSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@R. Bars: @gre@"
              + totalRbar
              + "@yel@ (@whi@"
              + String.format("%,d", RbarSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
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
              + "@yel@/@whi@hr@yel@) ",
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
              + "@yel@ / @whi@R.Items: @gre@"
              + totalRunestuff
              + "@yel@ (@whi@"
              + String.format("%,d", RuneSuccessPerHr)
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
      c.drawString("@whi@________________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
    }
  }
}
