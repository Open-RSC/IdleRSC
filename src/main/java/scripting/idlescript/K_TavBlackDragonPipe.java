package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import orsc.ORSCharacter;

/**
 * Taverly Black dragon (agility Pipe Shortcut) - By Kaila.
 *
 * <p>
 *
 * <p>Start in Edge bank with Armor.
 *
 * <p>Uses Coleslaw agility pipe shortcut. (coleslaw only)
 *
 * <p>70 Agility required, for the shortcut!
 *
 * <p>Sharks/ppots/Laws/Airs/Earths IN BANK REQUIRED.
 *
 * <p>31 Magic Required for escape tele.
 *
 * <p>Bot will attempt to wield dragonfire shield when in blue dragon room.
 *
 * <p>@Author - Kaila
 */
public final class K_TavBlackDragonPipe extends K_kailaScript {
  private boolean isWithinWander(int x, int y) {
    return c.distance(408, 3337, x, y) <= 22;
  }

  private static int totalRlong = 0;
  private static int totalMed = 0;
  private static int totalDstone = 0;
  private static final int[] loot = {
    814, // D Bones
    75, // rune long
    120, // addy plate body
    619, // blood rune
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
    405, // rune axe
    81, // rune 2h
    93, // rune battle axe
    31, // fire rune
    33, // air rune
    38, // Death Rune
    619, // blood rune
    40, // nature rune
    42, // law rune
    11, // bronze arrows
    408, // rune bar
    520, // silver cert
    518, // coal cert
    159, // emerald
    158, // ruby
    157, // diamond
    523, // dragonstone!
    526, // tooth half
    527, // loop half
    1092, // rune spear
    1277, // shield (left) half
    795, // D med
  };

  public int start(String[] parameters) {
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

      ppotCheck();
      drinkPrayerPotion();
      pray();
      foodCheck();
      eat();
      superAttackBoost();
      superStrengthBoost();
      eatFoodToLoot();
      lootScript();
      dropVial();

      if (c.getInventoryItemCount() < 30) {
        if (!c.isInCombat()) {
          c.setStatus("@yel@Attacking Dragons");
          ORSCharacter npc = c.getNearestNpcById(291, false);
          if (npc != null) {
            c.attackNpc(npc.serverIndex);
            eat();
            c.sleep(100);
          } else {
            c.walkTo(408, 3336);
            c.sleep(340);
          }
        }
        c.sleep(800);
      }
      if (c.getInventoryItemCount() == 30) {
        ppotCheck();
        leaveCombat();
        if (c.getInventoryItemCount(465) > 0 && !c.isInCombat()) {
          c.setStatus("@red@Dropping Vial to Loot..");
          c.dropItem(c.getInventoryItemSlotIndex(465));
          c.sleep(340);
        }
      }
    }
  }

  private void lootScript() {
    for (int lootId : loot) {
      try {
        int[] coords = c.getNearestItemById(lootId);
        if (coords != null && isWithinWander(coords[0], coords[1])) {
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
      // ppotCount() = (c.getInventoryItemCount(483) + c.getInventoryItemCount(483)
      // +c.getInventoryItemCount(483));
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != 486
            && itemId != 487
            && itemId != 488
            && itemId != 492
            && itemId != 493
            && itemId != 494
            && itemId != 546
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
      c.sleep(1400); // Important, leave in

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
      withdrawSuperAttack(1);
      withdrawSuperStrength(1);
      withdrawAntidote();
      if (c.getInventoryItemCount(483) < 6) { // withdraw 5 ppot
        c.withdrawItem(
            483,
            6
                - (c.getInventoryItemCount(483)
                    + c.getInventoryItemCount(484)
                    + c.getInventoryItemCount(485))); // minus ppot count
        c.sleep(640);
      }
      if (c.getInventoryItemCount(546) < 4) { // withdraw 2 shark
        c.withdrawItem(546, 4 - c.getInventoryItemCount(546));
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
      bankBones = c.getBankItemCount(814);
      if (c.getBankItemCount(546) < 4
          || c.getBankItemCount(569) < 1
          || c.getBankItemCount(483) < 6
          || c.getBankItemCount(33) == 0
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
      if (!c.isItemIdEquipped(420)) {
        c.setStatus("@red@Not Wielding Dragonfire Shield!.");
        c.withdrawItem(420, 1);
        c.closeBank();
        c.equipItem(c.getInventoryItemSlotIndex(420));
        c.sleep(1320);
      }
      c.closeBank();
      c.sleep(1000);
    }
    airCheck();
    waterCheck();
    lawCheck();
  }

  private void pray() {
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
        dragonEscape();
        DragonsToBank();
        bank();
        BankToDragons();
      }
    }
  }

  private void dragonEscape() {
    c.setStatus("We've ran out of Food/PPots! @gre@Going to safe zone.");
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
    ppotCheck();
    drinkPrayerPotion();
    pray();
    c.walkTo(386, 3371);
    c.walkTo(388, 3360);
    c.walkTo(397, 3347);
    c.walkTo(397, 3343);
    c.walkTo(403, 3346);
    c.walkTo(408, 3344);
    drinkAntidote();
    c.walkTo(409, 3338);
    eat();
    ppotCheck();
    drinkPrayerPotion();
    pray();
    c.setStatus("@gre@Done Walking..");
  }

  private void ppotCheck() {
    if (c.getInventoryItemCount(483) == 0) {
      c.setStatus("@yel@No Ppots, Banking..");
      dragonEscape();
      DragonsToBank();
      bank();
      BankToDragons();
      c.sleep(618);
    }
  }

  private void foodCheck() {
    if (c.getInventoryItemCount(546) == 0) {
      c.setStatus("@yel@No food, Banking..");
      dragonEscape();
      DragonsToBank();
      bank();
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
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
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
    scriptFrame.add(startScriptButton);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocus();
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
