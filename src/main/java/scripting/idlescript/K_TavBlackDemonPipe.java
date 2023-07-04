package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import orsc.ORSCharacter;

/**
 * Tav black demons - By Kaila.
 *
 * <p>
 *
 * <p>Start in Fally west bank with gear. Uses Coleslaw agility pipe shortcut.
 *
 * <p>Sharks/ppots/Laws/Airs/Earths IN BANK REQUIRED. super atk, super str pots suggested.
 *
 * <p>37 Magic Required for tele, 37 prayer for paralize monster, 70 agility for shortcut.
 *
 * <p>anti dragon shield required. D2h recommended to demon kill rates don't suck.
 *
 * <p>@Author - Kaila
 */
public final class K_TavBlackDemonPipe extends K_kailaScript {
  private static boolean d2hWield = false;

  private static boolean isWithinWander(int x, int y) {
    return c.distance(390, 3371, x, y) <= 10;
  }

  private static int totalMed = 0;
  private static int totalDstone = 0;
  private static int totalRbar = 0;
  private static int totalRunestuff = 0;
  private static int totalRchain = 0;
  private static int totalRmed = 0;
  private static final int[] loot = {
    400, // rune chain
    399, // rune med
    31, // fire rune
    42, // law rune
    41, // chaos rune
    619, // blood rune
    33, // air rune
    40, // nature rune
    38, // Death Rune
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
    174, // Addy bar
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond
    404, // rune kite
    403, // rune square
    542, // uncut dstone
    523, // cut dstone
    795, // D med
    405, // rune axe
    408, // rune bar
    81, // rune 2h
    93, // rune battle axe
    520, // silver cert
    518, // coal cert
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    1092, // rune spear
    795 // D med
  };

  // STARTing script
  public int start(String[] parameters) {
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
      if (c.isInBank()) {
        c.closeBank();
      }
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

      foodPotCheck();
      eat();
      drinkPrayerPotion(true);
      prayParalyze();
      superAttackBoost(false);
      superStrengthBoost(false);

      if (c.getInventoryItemCount() < 30) {
        lootScript();

        if (!c.isInCombat()) {
          c.setStatus("@yel@Attacking Demons");
          c.sleepHandler(98, true);
          ORSCharacter npc = c.getNearestNpcById(290, false);
          if (npc != null) {
            c.attackNpc(npc.serverIndex);
            c.sleep(1000);
          } else {
            c.sleep(1000);
          }
        }
        c.sleep(1380);
      }
      if (c.getInventoryItemCount() == 30) {
        leaveCombat();
        if (c.getInventoryItemCount(465) > 0 && !c.isInCombat()) {
          c.setStatus("@red@Dropping Vial to Loot..");
          c.dropItem(c.getInventoryItemSlotIndex(465));
          c.sleep(340);
        }
        eatFoodToLoot();
      }
    }
  }

  public void lootScript() {
    for (int lootId : loot) {
      int[] coords = c.getNearestItemById(lootId);
      if (coords != null && isWithinWander(coords[0], coords[1])) {
        c.setStatus("@yel@Looting..");
        c.walkTo(coords[0], coords[1]);
        c.pickupItem(coords[0], coords[1], lootId, true, true);
        c.sleep(618);
      }
    }
  }
  // actionable private voids (eat, bank, etc)

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
            && itemId != 420
            && itemId != 485
            && itemId != 484
            && itemId != 483
            && itemId != 1346) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1400); // Important, leave in

      if (c.getInventoryItemCount(420) < 1) { // antidragon shield
        c.withdrawItem(420, 1);
        c.sleep(640);
      }
      if (c.getInventoryItemCount(33) < 18) { // 6 air
        c.withdrawItem(33, 18 - c.getInventoryItemCount(33));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(42) < 6) { // 2 law
        c.withdrawItem(42, 6 - c.getInventoryItemCount(42));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(32) < 6) { // 2 water
        c.withdrawItem(32, 6 - c.getInventoryItemCount(32));
        c.sleep(1000);
      }
      c.sleep(640); // leave in
      if (c.getInventoryItemCount(superAttackPot[0]) < 1
          && c.getInventoryItemCount(superAttackPot[1]) < 1
          && c.getInventoryItemCount(superAttackPot[2]) < 1) { // withdraw 10 shark if needed
        c.withdrawItem(superAttackPot[2], 1);
        c.sleep(640);
      }
      if (c.getInventoryItemCount(superStrengthPot[0]) < 1
          && c.getInventoryItemCount(superStrengthPot[1]) < 1
          && c.getInventoryItemCount(superStrengthPot[2]) < 1) { // withdraw 10 shark if needed
        c.withdrawItem(superStrengthPot[2], 1);
        c.sleep(640);
      }

      if (c.getInventoryItemCount(483) < 17) { // withdraw 17 ppot
        c.withdrawItem(
            483,
            17
                - (c.getInventoryItemCount(483)
                    + c.getInventoryItemCount(484)
                    + c.getInventoryItemCount(485))); // minus ppot count
        c.sleep(640);
      }
      if (c.getInventoryItemCount(546) < 2) { // withdraw 2 shark
        c.withdrawItem(546, 2 - c.getInventoryItemCount(546));
        c.sleep(640);
      }
      if (c.getInventoryItemCount(33) < 18) { // 6 air
        c.withdrawItem(33, 18 - c.getInventoryItemCount(33));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(42) < 6) { // 2 law
        c.withdrawItem(42, 6 - c.getInventoryItemCount(42));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(32) < 6) { // 2 water
        c.withdrawItem(32, 6 - c.getInventoryItemCount(32));
        c.sleep(1000);
      }
      if (c.getBankItemCount(546) == 0
          || c.getBankItemCount(33) == 0
          || c.getBankItemCount(42) == 0
          || c.getBankItemCount(32) == 0) {
        c.setStatus("@red@NO Sharks/Laws/Airs in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      c.closeBank();
      c.sleep(1000);
    }
    airCheck();
    waterCheck();
    lawCheck();
  }

  private static void prayParalyze() {
    if (!c.isPrayerOn(c.getPrayerId("Paralyze Monster")) && c.currentY() > 3000) {
      c.enablePrayer(c.getPrayerId("Paralyze Monster"));
    }
  }

  private void eat() {

    int eatLvl = c.getBaseStat(c.getStatId("Hits")) - 20;

    if (c.getCurrentStat(c.getStatId("Hits")) < eatLvl) {
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
        c.sleep(308);
        demonEscape();
        DemonsToBank();
        bank();
        BankToDemons();
      }
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
    c.setStatus("@gre@Going to Bank. Casting 1st teleport.");
    c.castSpellOnSelf(c.getSpellIdFromName("Falador Teleport"));
    c.sleep(1000);
    for (int i = 1; i <= 20; i++) {
      if (c.currentY() > 3000) {
        c.setStatus("@gre@Teleport unsuccessful, Casting nth teleport.");
        c.castSpellOnSelf(c.getSpellIdFromName("Falador Teleport"));
        c.sleep(1000);
      }
      c.sleep(10);
    }
    totalTrips = totalTrips + 1;
    if (c.isPrayerOn(c.getPrayerId("Paralyze Monster"))) {
      c.disablePrayer(c.getPrayerId("Paralyze Monster"));
    }
    c.sleep(308);
    c.walkTo(327, 552);
    c.sleep(308);
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToDemons() {
    c.setStatus("@gre@Walking to Black Demons..");
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
    if (d2hWield) {
      c.equipItem(c.getInventoryItemSlotIndex(1346));
    }
    c.enablePrayer(c.getPrayerId("Paralyze Monster"));
    c.sleep(320);
    c.walkTo(380, 3372);
    c.setStatus("@gre@Done Walking..");
    eat();
    drinkPrayerPotion(true);
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
    JCheckBox d2hCheckbox = new JCheckBox("Check This if using D2H");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          d2hWield = d2hCheckbox.isSelected();
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
    scriptFrame.add(d2hCheckbox);
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
