package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
  private static boolean d2hWield = false;
  private static boolean craftCapeTeleport = false;
  private static int totalMed = 0;
  private static int totalDstone = 0;
  private static int totalRbar = 0;
  private static int totalRunestuff = 0;
  private static int totalRchain = 0;
  private static int totalRmed = 0;
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
  // STARTing script
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
      boolean ate = eatFood();
      if (!ate) {
        c.setStatus("@red@We've ran out of Food! Running Away!.");
        demonEscape();
        DemonsToBank();
        bank();
        BankToDemons();
      }
      foodPotCheck();
      drinkPrayerPotion(31, true);
      prayParalyze();
      superAttackBoost(0, false);
      superStrengthBoost(0, false);
      eatFoodToLoot(true);
      if (c.getInventoryItemCount() == 30) dropItemToLoot(true, 1, ItemId.EMPTY_VIAL.getId());
      if (c.getInventoryItemCount() < 30) {
        lootItems(true, loot);
        if (!c.isInCombat()) {
          ORSCharacter npc = c.getNearestNpcById(290, false);
          if (npc != null) {
            c.setStatus("@yel@Attacking Demons");
            c.attackNpc(npc.serverIndex);
            c.sleep(GAME_TICK);
          } else {
            c.sleep(GAME_TICK);
            lootItems(true, loot);
          }
        } else c.sleep(GAME_TICK);
      }

      if (c.getInventoryItemCount() == 30 || c.getInventoryItemCount(546) == 0) {
        c.setStatus("@red@Full Inv / Out of Food");
        c.sleep(308);
        demonEscape();
        DemonsToBank();
        bank();
        BankToDemons();
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
        if (itemId != 486
            && itemId != 487
            && itemId != 488
            && itemId != 492
            && itemId != 493
            && itemId != 494
            && itemId != ANTI_DRAGON_SHIELD
            && itemId != 485
            && itemId != 484
            && itemId != 483
            && itemId != DRAGON_TWO_HAND) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1400); // Important, leave in

      if (d2hWield && (c.getInventoryItemCount(DRAGON_TWO_HAND) < 1))
        withdrawItem(DRAGON_TWO_HAND, 1);
      if (craftCapeTeleport && (c.getInventoryItemCount(CRAFT_CAPE) < 1))
        withdrawItem(CRAFT_CAPE, 1);
      if (craftCapeTeleport && (c.getInventoryItemCount(CRAFT_CAPE) > 1))
        c.depositItem(CRAFT_CAPE, c.getInventoryItemCount(CRAFT_CAPE) - 1);
      if (!craftCapeTeleport) {
        withdrawItem(airId, 18);
        withdrawItem(lawId, 6);
        withdrawItem(waterId, 6);
      }
      withdrawSuperAttack(1);
      withdrawSuperStrength(1);
      withdrawPrayer(16);
      withdrawFood(546, 2);

      if (!craftCapeTeleport) {
        bankItemCheck(airId, 30);
        bankItemCheck(waterId, 10); // Falador teleport
        bankItemCheck(lawId, 10);
      }
      bankItemCheck(prayerPot[2], 17);
      bankItemCheck(546, 10);
      bankCheckAntiDragonShield();
      c.closeBank();
      if (!c.isItemIdEquipped(ATTACK_CAPE)) c.equipItem(c.getInventoryItemSlotIndex(ATTACK_CAPE));
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
    c.setStatus("We've ran out of Food! @gre@Going to safe zone.");
    c.walkTo(382, 3372);
    c.walkTo(375, 3372);
    c.sleep(640);
    if (c.currentX() > 376) {
      c.walkTo(375, 3372);
      c.sleep(640);
    }
  }

  private void DemonsToBank() {
    c.setStatus("@gre@Going to Bank");
    if (craftCapeTeleport) {
      c.setStatus("@gre@Going to Bank. Casting craft cape teleport.");
      teleportCraftCape();
      c.sleep(4 * GAME_TICK); // cannot do things after teleport
      if (c.isPrayerOn(PARALYZE_MONSTER)) c.disablePrayer(PARALYZE_MONSTER);
      c.walkTo(347, 600);
      forceEquipItem(CRAFT_CAPE);
      craftCapeDoorEntering();
      forceEquipItem(ATTACK_CAPE);
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
    if (craftCapeTeleport) {
      teleportCraftCape();
      c.sleep(4 * GAME_TICK); // cannot do things after teleport
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
  // BOOST private voids
  private void foodPotCheck() {
    if (c.getInventoryItemCount(483) == 0 || c.getInventoryItemCount(546) == 0) {
      c.setStatus("@yel@No Ppots/food, Banking..");
      demonEscape();
      DemonsToBank();
      bank();
      BankToDemons();
      c.sleep(618);
    }
  }
  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("Taverley Black Demon (Pipe) - By Kaila");
    JLabel label1 = new JLabel("Start in Fally west with gear on, or in Demon room!");
    JLabel label2 = new JLabel("Sharks, Law, Water, Air IN BANK required");
    JLabel label3 = new JLabel("70 Agility required, for the shortcut!");
    JLabel label4 = new JLabel("Bot will attempt to wield dragonfire shield");
    JLabel label5 = new JLabel("When walking through Blue Dragon Room");
    JLabel blankLabel = new JLabel("     ");
    JCheckBox d2hCheckbox = new JCheckBox("Swap to Dragon 2h Sword", true);
    JCheckBox craftCapeCheckbox = new JCheckBox("99 Crafting Cape Teleport?", false);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          d2hWield = d2hCheckbox.isSelected();
          craftCapeTeleport = craftCapeCheckbox.isSelected();
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
    scriptFrame.add(startScriptButton);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
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
      c.drawString("@red@Tavelry Black Demons @mag@~ by Kaila", x, y - 3, 0xFFFFFF, 1);
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
