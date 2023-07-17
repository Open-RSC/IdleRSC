package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import orsc.ORSCharacter;

/**
 * Wildy Fire Giant Killer - By Kaila
 *
 * <p>Start in Edge bank with Armor
 *
 * <p>Sharks/Laws/Airs/Earths IN BANK REQUIRED
 *
 * <p>31 Magic Required for escape tele
 *
 * <p>@Author - Kaila
 */
public final class K_WaterfallFireGiants extends K_kailaScript {
  private static int totalBstaff = 0;
  private static int totalRscim = 0;
  private static int totalRunestuff = 0;
  private static int totalDstone = 0;
  private static int totalMed = 0;
  private static final int[] loot = {
    413, // big bones
    1346, // d2h
    795, // D med
    522, // dragonstone ammy
    1318, // ring of wealth
    402, // rune leg
    1374, // atk cape
    1318, // ring of wealth
    402, // rune leg
    400, // rune chain
    399, // rune med
    403, // rune sq
    404, // rune kite
    112, // rune full helm
    522, // dragonstone ammy
    542, // uncut dstone
    523, // cut dstone
    795, // D med
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    1092, // rune spear
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
    40, // nature rune
    42, // law rune
    38, // death rune
    619, // blood rune
    41, // chaos rune
    31, // fire rune
    404, // rune kite
    403, // rune square
    126, // mithril sq
    405, // rune axe
    408, // rune bar
    81, // rune 2h
    93, // rune battle axe
    398, // rune scimmy
    615, // fire bstaff
    520, // silver cert
    518, // coal cert
    373 // lobster (will get eaten)
  };

  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.displayMessage("@red@Waterfall Fire Giant Killer - By Kaila");
      c.displayMessage("@red@Start in Seers bank with gear on, or in fire giant room!");
      c.displayMessage("@red@Sharks IN BANK REQUIRED");
      if (c.isInBank()) {
        c.closeBank();
      }
      if (c.currentY() < 500) {
        bank();
        BankToGiants();
        c.sleep(1380);
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {

      buryBones();
      eat();
      lootScript();
      superAttackBoost(0, false);
      superStrengthBoost(0, false);

      if (c.getInventoryItemCount(546) > 0) {
        if (c.getInventoryItemCount() < 30) {
          if (!c.isInCombat()) {

            // c.sleepHandler(98, true);
            ORSCharacter npc = c.getNearestNpcById(344, false);
            if (npc != null) {
              c.setStatus("@yel@Attacking Giants");
              c.attackNpc(npc.serverIndex);
              c.sleep(1280);
            } else {
              c.sleep(640);
            }
          } else {
            c.sleep(640);
          }
        }
        if (c.getInventoryItemCount() == 30) {
          leaveCombat();
          buryBones();
          if (c.getInventoryItemCount(465) > 0 && !c.isInCombat()) {
            c.setStatus("@red@Dropping Vial to Loot..");
            c.dropItem(c.getInventoryItemSlotIndex(465));
            c.sleep(250);
          }
          c.setStatus("@red@Eating Food to Loot..");
          eatFoodToLoot();
        }
      }
      if (c.getInventoryItemCount(546) == 0) {
        c.setStatus("@yel@Banking, escaping south..");
        giantEscape();
        GiantsToBank();
        bank();
        BankToGiants();
        c.sleep(618);
      }
    }
  }

  private void lootScript() {
    for (int lootId : loot) {
      try {
        int[] coords = c.getNearestItemById(lootId);
        if (coords != null) {
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

    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {

      totalBstaff = totalBstaff + c.getInventoryItemCount(615);
      totalRscim = totalRscim + c.getInventoryItemCount(398);
      totalRunestuff =
          totalRunestuff
              + c.getInventoryItemCount(404) // kite
              + c.getInventoryItemCount(403) // sq
              + c.getInventoryItemCount(405) // axe
              + c.getInventoryItemCount(81) // 2h
              + c.getInventoryItemCount(93) // bAxe
              + c.getInventoryItemCount(408); // r bar
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
      totalBlood = totalBlood + c.getInventoryItemCount(619);
      totalLoop = totalLoop + c.getInventoryItemCount(527);
      totalTooth = totalTooth + c.getInventoryItemCount(526);
      totalDstone = totalDstone + c.getInventoryItemCount(523);
      totalLeft = totalLeft + c.getInventoryItemCount(1277);
      totalSpear = totalSpear + c.getInventoryItemCount(1092);
      totalMed = totalMed + c.getInventoryItemCount(795);

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != 782
            && itemId != 237
            && itemId != 486
            && itemId != 487
            && itemId != 488
            && itemId != 492
            && itemId != 493
            && itemId != 494
            && itemId != 546) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1280); // increased sleep here to prevent double banking

      withdrawItem(782, 1); // glariels amulet
      withdrawItem(237, 1); // rope
      withdrawItem(airId, 30);
      withdrawItem(lawId, 6);
      withdrawSuperAttack(1);
      withdrawSuperStrength(1);
      bankItemCheck(foodId, 30);
      bankItemCheck(airId, 50);
      bankItemCheck(lawId, 10);
      c.closeBank();
      c.sleep(1280);
    }
    inventoryItemCheck(airId, 30);
    inventoryItemCheck(lawId, 6);
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
        giantEscape();
        GiantsToBank();
        bank();
        BankToGiants();
      }
    }
  }

  private void giantEscape() {
    c.setStatus("We've ran out of Food! @gre@Going to safe zone.");
    c.walkTo(660, 3295);
    c.sleep(1000);
    if (c.currentX() > 661 || c.currentY() > 3000 && c.currentY() < 3295) {
      c.walkTo(660, 3295);
      c.sleep(1000);
    }
  }

  private void GiantsToBank() {
    c.setStatus("@gre@Walking to Bank. Casting 1st teleport.");
    c.castSpellOnSelf(c.getSpellIdFromName("Camelot Teleport"));
    c.sleep(800);
    teleport();
    totalTrips = totalTrips + 1;
    c.walkTo(468, 462);
    if (c.getObjectAtCoord(467, 463) == 57) {
      c.setStatus("@gre@Opening Castle Gate..");
      c.walkTo(468, 462);
      c.sleep(100);
      c.atObject(467, 463);
      c.sleep(1000);
    }
    c.walkTo(468, 464);
    c.walkTo(478, 464);
    c.walkTo(488, 464);
    c.walkTo(496, 455);
    c.walkTo(501, 454);
    c.sleep(308);
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToGiants() {
    c.setStatus("@gre@Walking to Fire Giants..");
    c.walkTo(501, 456);
    c.walkTo(522, 456);
    c.walkTo(543, 477);
    c.walkTo(560, 477);
    c.walkTo(576, 477);
    c.walkTo(581, 472);
    c.walkTo(587, 465);
    c.walkTo(590, 461);
    c.walkTo(592, 458);
    c.setStatus("@gre@Crossing log..");
    c.atObject(593, 458); // log
    c.sleep(640);
    logScript();
    c.setStatus("@gre@Walkin..");
    c.walkTo(602, 458);
    c.walkTo(607, 463);
    c.walkTo(612, 468);
    c.walkTo(617, 473);
    if (c.getObjectAtCoord(617, 474) == 57) {
      c.setStatus("@gre@Opening Coal Mine Gate..");
      c.walkTo(617, 473);
      c.sleep(100);
      c.atObject(617, 474);
      c.sleep(1000);
    }
    c.walkTo(618, 474);
    c.walkTo(618, 480);
    c.walkTo(623, 480);
    c.walkTo(637, 466);
    c.walkTo(637, 458);
    c.walkTo(648, 448);
    c.walkTo(659, 449);
    c.sleep(1000);
    entranceScript();
  }

  private void entranceScript() {
    boatScript();
    firstTree();
    secondTree();
    thirdTree();
    if (!c.isItemIdEquipped(782)) {
      c.setAutoLogin(false);
      c.logout();
      if (!c.isLoggedIn()) {
        c.stop();
      }
    }
    c.walkTo(659, 3303);
    waterfallGateScript();
    c.walkTo(659, 3289);
    c.equipItem(c.getInventoryItemSlotIndex(522));
    c.sleep(640);
    c.setStatus("@gre@Done Walking..");
  }

  private void boatScript() {
    for (int i = 1; i <= 5; i++) {
      if (c.isCloseToCoord(659, 449)) {
        c.setStatus("@red@Going down Boat..");
        c.atObject(660, 449); // boat
        c.sleep(5000);
        c.sleep(5000);
      }
    }
  }

  private void firstTree() {
    for (int i = 1; i <= 5; i++) {
      if (c.isCloseToCoord(662, 463)) {
        c.setStatus("@red@Using rope on 1st Tree..");
        c.equipItem(c.getInventoryItemSlotIndex(782));
        c.useItemIdOnObject(662, 463, 237); // 1st tree
        c.sleep(5000);
        c.sleep(5000);
      }
    }
  }

  private void secondTree() {
    for (int i = 1; i <= 5; i++) {
      if (c.isCloseToCoord(662, 467)) {
        c.setStatus("@red@Using rope on 2nd Tree..");
        c.equipItem(c.getInventoryItemSlotIndex(782));
        c.useItemIdOnObject(662, 467, 237); // 2nd tree
        c.sleep(5000);
        c.sleep(5000);
      }
    }
  }

  private void thirdTree() {
    for (int i = 1; i <= 5; i++) {
      if (c.isCloseToCoord(659, 471)) {
        c.setStatus("@red@Using rope on 3rd Tree..");
        c.equipItem(c.getInventoryItemSlotIndex(782));
        c.useItemIdOnObject(659, 471, 237); // 3rd tree
        c.sleep(5000);
        c.sleep(5000);
      }
    }
  }

  private void logScript() {
    for (int i = 1; i <= 4; i++) {
      if (c.currentX() == 592 && c.currentY() == 458) {
        c.setStatus("@gre@Crossing log..");
        c.atObject(593, 458); // log
        c.sleep(640);
      } else {
        c.setStatus("@red@Done Crossing log..");
        break;
      }
      c.sleep(10);
    }
  }

  private void waterfallGateScript() {
    for (int i = 1; i <= 9; i++) {
      if (c.currentY() > 3302 && c.currentY() < 3306) { // won't break when others open same tick
        c.setStatus("@gre@opening gate");
        c.atObject(659, 3303); // gate
        c.sleep(800);
      } else {
        c.setStatus("@red@Done opening gate..");
        break;
      }
      c.sleep(10);
    }
  }

  private void teleport() {
    for (int i = 1; i <= 20; i++) {
      if (c.currentY() > 3000) {
        c.setStatus("@gre@1st Teleport unsuccessful, Casting teleports.");
        c.castSpellOnSelf(c.getSpellIdFromName("Camelot Teleport"));
        c.sleep(800);
      } else {
        c.setStatus("@red@Done Teleporting..");
        break;
      }
      c.sleep(10);
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Waterfall Fire Giant Killer - By Kaila");
    JLabel label1 = new JLabel("Start in Seers bank OR in Giants room");
    JLabel label2 = new JLabel("Sharks, Law, Air required");
    JLabel label3 = new JLabel("Must have glariels amulet and rope");
    JLabel label4 = new JLabel("Bot will attempt to wield dragonstone amulet");
    JLabel label5 = new JLabel("can be changed");
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
      int BstaffSuccessPerHr = 0;
      int RscimSuccessPerHr = 0;
      int RuneSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int FireSuccessPerHr = 0;
      int LawSuccessPerHr = 0;
      int NatSuccessPerHr = 0;
      int ChaosSuccessPerHr = 0;
      int BloodSuccessPerHr = 0;
      int HerbSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        BstaffSuccessPerHr = (int) (totalBstaff * scale);
        RscimSuccessPerHr = (int) (totalRscim * scale);
        RuneSuccessPerHr = (int) (totalRunestuff * scale);
        GemsSuccessPerHr = (int) (totalGems * scale);
        FireSuccessPerHr = (int) (totalFire * scale);
        LawSuccessPerHr = (int) (totalLaw * scale);
        NatSuccessPerHr = (int) (totalNat * scale);
        ChaosSuccessPerHr = (int) (totalChaos * scale);
        BloodSuccessPerHr = (int) (totalBlood * scale);
        HerbSuccessPerHr = (int) (totalHerb * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      c.drawString("@red@Waterfall Fire Giants @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Laws: @gre@"
              + totalLaw
              + "@yel@ (@whi@"
              + String.format("%,d", LawSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          62,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Natures: @gre@"
              + totalNat
              + "@yel@ (@whi@"
              + String.format("%,d", NatSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          76,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Fires: @gre@"
              + totalFire
              + "@yel@ (@whi@"
              + String.format("%,d", FireSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          90,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Chaos: @gre@"
              + totalChaos
              + "@yel@ (@whi@"
              + String.format("%,d", ChaosSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          104,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Bloods: @gre@"
              + totalBlood
              + "@yel@ (@whi@"
              + String.format("%,d", BloodSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          118,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Fire Bstaff: @gre@"
              + totalBstaff
              + "@yel@ (@whi@"
              + String.format("%,d", BstaffSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          132,
          0xFFFFFF,
          1); // fix y cords
      c.drawString(
          "@whi@Rune Scim: @gre@"
              + totalRscim
              + "@yel@ (@whi@"
              + String.format("%,d", RscimSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          146,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Herbs: @gre@"
              + totalHerb
              + "@yel@ (@whi@"
              + String.format("%,d", HerbSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          160,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Gems: @gre@"
              + totalGems
              + "@yel@ (@whi@"
              + String.format("%,d", GemsSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          174,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Rune Items: @gre@"
              + totalRunestuff
              + "@yel@ (@whi@"
              + String.format("%,d", RuneSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          188,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tooth: @gre@" + totalTooth + "@yel@ / @whi@Loop: @gre@" + totalLoop,
          350,
          202,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Dstone: @gre@" + totalDstone + "@yel@ / @whi@Rune Spear: @gre@" + totalSpear,
          350,
          216,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@D Med: @gre@" + totalMed + "@yel@ / @whi@Left Half: @gre@" + totalLeft,
          350,
          230,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          244,
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, 350, 258, 0xFFFFFF, 1);
    }
  }
}
