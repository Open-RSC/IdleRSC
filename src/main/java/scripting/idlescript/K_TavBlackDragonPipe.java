package scripting.idlescript;

import bot.Main;
import controller.Controller;
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
public class K_TavBlackDragonPipe extends IdleScript {

  private static final Controller c = Main.getController();
  private static JFrame scriptFrame = null;
  private static boolean guiSetup = false;
  private static boolean scriptStarted = false;

  private boolean isWithinWander(int x, int y) {
    return c.distance(408, 3337, x, y) <= 22;
  }

  private static long startTime;
  private static final long startTimestamp = System.currentTimeMillis() / 1000L;
  private static int totalDbones = 0;
  private static int totalGems = 0;
  private static int totalLaw = 0;
  private static int totalNat = 0;
  private static int totalFire = 0;
  private static int totalAddy = 0;
  private static int totalLoop = 0;
  private static int totalTooth = 0;
  private static int totalLeft = 0;
  private static int totalRlong = 0;
  private static int totalSpear = 0;
  private static int totalHerb = 0;
  private static int bankDbones = 0;
  private static int totalBlood = 0;
  private static int totalMed = 0;
  private static int totalDstone = 0;
  private static int totalTrips = 0;
  private static int totalDeath = 0;
  private static final int[] superAttackPot = {
    488, // super  attack pot (1)
    487, // super  attack pot (2)
    486 // super attack pot (3)
  };
  private static final int[] superStrengthPot = {
    494, // super str pot (1)
    493, // super str pot (2)
    492 // super str pot (3)
  };
  private static final int[] antiPot = {
    571, // 1 dose
    570, // 2 dose
    569 // 3 dose
  };
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
    if (scriptStarted) {
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
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {

      ppotCheck();
      drink();
      pray();
      foodCheck();
      eat();

      if (c.getInventoryItemCount(465) > 0 && !c.isInCombat()) {
        c.dropItem(c.getInventoryItemSlotIndex(465));
      }
      dropToLootScript();
      lootScript();
      if (c.getInventoryItemCount() < 30) {

        if (c.getCurrentStat(c.getStatId("Attack")) == c.getBaseStat(c.getStatId("Attack"))) {
          if (c.getInventoryItemCount(superAttackPot[0]) > 0
              || c.getInventoryItemCount(superAttackPot[1]) > 0
              || c.getInventoryItemCount(superAttackPot[2]) > 0) {
            attackBoost();
          }
        }
        if (c.getCurrentStat(c.getStatId("Strength")) == c.getBaseStat(c.getStatId("Strength"))) {
          if (c.getInventoryItemCount(superStrengthPot[0]) > 0
              || c.getInventoryItemCount(superStrengthPot[1]) > 0
              || c.getInventoryItemCount(superStrengthPot[2]) > 0) {
            strengthBoost();
          }
        }
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

  private void dropToLootScript() {
    for (int id : c.getFoodIds()) {
      if (c.getInventoryItemCount(id) > 0 && c.getInventoryItemCount() == 30) {
        c.setStatus("@red@Eating Food to Loot..");
        c.itemCommand(id);
        c.sleep(700);
      }
    }
  }

  private void lootScript() {
    for (int lootId : loot) {
      int[] coords = c.getNearestItemById(lootId);
      if (coords != null && this.isWithinWander(coords[0], coords[1])) {
        c.setStatus("@yel@Looting..");
        c.walkTo(coords[0], coords[1]);
        c.pickupItem(coords[0], coords[1], lootId, true, true);
        c.sleep(618);
      }
    }
  }

  private void bank() {
    totalTrips = totalTrips + 1;
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(1200);

    if (c.isInBank()) {

      totalDbones = totalDbones + c.getInventoryItemCount(814);
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
      if (c.getInventoryItemCount(superAttackPot[0]) < 1
          && c.getInventoryItemCount(superAttackPot[1]) < 1
          && c.getInventoryItemCount(superAttackPot[2]) < 1) { // withdraw 10 shark if needed
        c.withdrawItem(superAttackPot[2], 1);
        c.sleep(640);
      }
      if (c.getInventoryItemCount(antiPot[0]) < 1
          && c.getInventoryItemCount(antiPot[1]) < 1
          && c.getInventoryItemCount(antiPot[2]) < 1) { // withdraw 10 shark if needed
        if (c.getBankItemCount(antiPot[0]) > 0) {
          c.withdrawItem(antiPot[0], 1);
          c.sleep(640);
        }
      }
      if (c.getInventoryItemCount(superStrengthPot[0]) < 1
          && c.getInventoryItemCount(superStrengthPot[1]) < 1
          && c.getInventoryItemCount(superStrengthPot[2]) < 1) { // withdraw 10 shark if needed
        c.withdrawItem(superStrengthPot[2], 1);
        c.sleep(640);
      }
      if (c.getInventoryItemCount(antiPot[0]) < 1
          && c.getInventoryItemCount(antiPot[1]) < 1
          && c.getInventoryItemCount(antiPot[2]) < 1) {
        if (c.getBankItemCount(antiPot[1]) > 0) {
          c.withdrawItem(antiPot[1], 1);
          c.sleep(640);
        }
      }
      if (c.getInventoryItemCount(483) < 6) { // withdraw 5 ppot
        c.withdrawItem(
            483,
            6
                - (c.getInventoryItemCount(483)
                    + c.getInventoryItemCount(484)
                    + c.getInventoryItemCount(485))); // minus ppot count
        c.sleep(640);
      }
      if (c.getInventoryItemCount(antiPot[0]) < 1
          && c.getInventoryItemCount(antiPot[1]) < 1
          && c.getInventoryItemCount(antiPot[2]) < 1) {
        if (c.getBankItemCount(antiPot[2]) > 0) {
          c.withdrawItem(antiPot[2], 1);
          c.sleep(640);
        }
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
      bankDbones = c.getBankItemCount(814);
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

  private void lawCheck() {
    if (c.getInventoryItemCount(42) < 6) { // law
      c.openBank();
      c.sleep(1200);
      c.withdrawItem(42, 6 - c.getInventoryItemCount(42));
      c.sleep(1000);
      c.closeBank();
      c.sleep(1000);
    }
  }

  private void waterCheck() {
    if (c.getInventoryItemCount(32) < 6) { // 2 water
      c.openBank();
      c.sleep(1200);
      c.withdrawItem(32, 6 - c.getInventoryItemCount(32));
      c.sleep(1000);
      c.closeBank();
      c.sleep(1000);
    }
  }

  private void airCheck() {
    if (c.getInventoryItemCount(33) < 18) { // 6 air
      c.openBank();
      c.sleep(1200);
      c.withdrawItem(33, 18 - c.getInventoryItemCount(33));
      c.sleep(1000);
      c.closeBank();
      c.sleep(1000);
    }
  }

  private void pray() {
    if (!c.isPrayerOn(c.getPrayerId("Paralyze Monster")) && c.currentY() > 3000) {
      c.enablePrayer(c.getPrayerId("Paralyze Monster"));
    }
  }

  private void drink() {
    if (c.getCurrentStat(c.getStatId("Prayer")) < (c.getBaseStat(c.getStatId("Prayer")) - 31)) {
      if (c.getInventoryItemCount(485) > 0
          || c.getInventoryItemCount(484) > 0
          || c.getInventoryItemCount(483) > 0) {
        drinkPot();
      } else {
        c.sleep(308);
        dragonEscape();
        DragonsToBank();
        bank();
        BankToDragons();
      }
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
    drink();
    pray();
    c.walkTo(386, 3371);
    c.walkTo(388, 3360);
    c.walkTo(397, 3347);
    c.walkTo(397, 3343);
    c.walkTo(403, 3346);
    c.walkTo(408, 3344);
    antiBoost();
    c.walkTo(409, 3338);
    eat();
    ppotCheck();
    drink();
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

  private void attackBoost() {
    leaveCombat();
    if (c.getInventoryItemCount(superAttackPot[0]) > 0) {
      c.itemCommand(superAttackPot[0]);
      c.sleep(320);
    } else if (c.getInventoryItemCount(superAttackPot[1]) > 0) {
      c.itemCommand(superAttackPot[1]);
      c.sleep(320);
    } else if (c.getInventoryItemCount(superAttackPot[2]) > 0) {
      c.itemCommand(superAttackPot[2]);
      c.sleep(320);
    }
  }

  private void strengthBoost() {
    leaveCombat();
    if (c.getInventoryItemCount(superStrengthPot[0]) > 0) {
      c.itemCommand(superStrengthPot[0]);
      c.sleep(320);
    } else if (c.getInventoryItemCount(superStrengthPot[1]) > 0) {
      c.itemCommand(superStrengthPot[1]);
      c.sleep(320);
    } else if (c.getInventoryItemCount(superStrengthPot[2]) > 0) {
      c.itemCommand(superStrengthPot[2]);
      c.sleep(320);
    }
  }

  private void drinkPot() {
    ppotCheck();
    leaveCombat();
    if (c.getInventoryItemCount(485) > 0) {
      c.itemCommand(485);
      c.sleep(320);
    } else if (c.getInventoryItemCount(484) > 0) {
      c.itemCommand(484);
      c.sleep(320);
    } else if (c.getInventoryItemCount(483) > 0) {
      c.itemCommand(483);
      c.sleep(320);
    }
  }

  private void antiBoost() {
    leaveCombat();
    if (c.getInventoryItemCount(antiPot[0]) > 0) {
      c.itemCommand(antiPot[0]);
      c.sleep(320);
    } else if (c.getInventoryItemCount(antiPot[1]) > 0) {
      c.itemCommand(antiPot[1]);
      c.sleep(320);
    } else if (c.getInventoryItemCount(antiPot[2]) > 0) {
      c.itemCommand(antiPot[2]);
      c.sleep(320);
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

  private void leaveCombat() {
    for (int i = 1; i <= 15; i++) {
      if (c.isInCombat()) {
        c.setStatus("@red@Leaving combat..");
        c.walkTo(c.currentX(), c.currentY(), 0, true);
        c.sleep(600);
      }
      c.sleep(10);
    }
  }

  private void tavGateEastToWest() {
    for (int i = 1; i <= 15; i++) {
      if (c.currentX() == 341 && c.currentY() < 489 && c.currentY() > 486) {
        c.setStatus("@red@Crossing Tav Gate..");
        c.atObject(341, 487); // gate won't break if someone else opens it
        c.sleep(800);
      }
      c.sleep(10);
    }
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
        DbonesSuccessPerHr = (int) (totalDbones * scale);
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
              + totalDbones
              + "@yel@ (@whi@"
              + String.format("%,d", DbonesSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@D.Bones in Bank: @gre@"
              + bankDbones,
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
