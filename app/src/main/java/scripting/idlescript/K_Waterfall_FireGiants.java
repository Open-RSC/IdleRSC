package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Waterfall Fire Giants</b>
 *
 * <p>Start in Seers bank with Armor <br>
 * Sharks/Laws/Airs IN BANK REQUIRED<br>
 * 31 Magic Required for escape tele<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_Waterfall_FireGiants extends K_kailaScript {
  private int amuletId = 0;
  private static int totalBstaff = 0;
  private static int totalRscim = 0;
  private static int totalRunestuff = 0;
  private static int totalDstone = 0;
  private static int totalMed = 0;
  private static final int[] loot = {
    ItemId.UNID_RANARR_WEED.getId(),
    ItemId.UNID_IRIT.getId(),
    ItemId.UNID_AVANTOE.getId(),
    ItemId.UNID_KWUARM.getId(),
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
    ItemId.BIG_BONES.getId(),
    ItemId.DRAGONSTONE_AMULET.getId(),
    ItemId.RING_OF_WEALTH.getId(),
    ItemId.UNCUT_DRAGONSTONE.getId(),
    ItemId.DRAGONSTONE.getId(),
    ItemId.NATURE_RUNE.getId(),
    ItemId.LAW_RUNE.getId(),
    ItemId.CHAOS_RUNE.getId(),
    ItemId.DEATH_RUNE.getId(),
    ItemId.BLOOD_RUNE.getId(),
    ItemId.FIRE_RUNE.getId(),
    ItemId.RUNE_KITE_SHIELD.getId(),
    ItemId.RUNE_SQUARE_SHIELD.getId(),
    ItemId.MITHRIL_SQUARE_SHIELD.getId(),
    ItemId.RUNE_AXE.getId(),
    ItemId.RUNITE_BAR.getId(),
    ItemId.RUNE_2_HANDED_SWORD.getId(),
    ItemId.RUNE_BATTLE_AXE.getId(),
    ItemId.RUNE_SCIMITAR.getId(),
    ItemId.BATTLESTAFF_OF_FIRE.getId(),
    ItemId.SILVER_CERTIFICATE.getId(),
    ItemId.COAL_CERTIFICATE.getId(),
    ItemId.LOBSTER.getId(),
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
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (!eatFood() || c.getInventoryItemCount(foodId) == 0 || timeToBank) {
        c.setStatus("@yel@Banking, escaping south..");
        timeToBank = false;
        giantEscape();
        GiantsToBank();
        bank();
        BankToGiants();
      }
      lootItems(true, loot);
      buryBones(false);
      if (potUp) {
        superAttackBoost(0, false);
        superStrengthBoost(0, false);
      }
      if (!c.isInCombat()) {
        ORSCharacter npc = c.getNearestNpcById(344, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking Giants");
          c.attackNpc(npc.serverIndex);
          c.sleep(2 * GAME_TICK);
        } else c.sleep(GAME_TICK);
      } else c.sleep(640);
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(true, 1, ItemId.EMPTY_VIAL.getId());
        eatFoodToLoot(true);
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
        if (itemId != ItemId.GLARIALS_AMULET.getId()
            && itemId != 237
            && itemId != 486
            && itemId != 487
            && itemId != 488
            && itemId != 492
            && itemId != 493
            && itemId != 494) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1280); // increased sleep here to prevent double banking
      withdrawItem(ItemId.GLARIALS_AMULET.getId(), 1); // glariels amulet
      withdrawItem(237, 1); // rope
      withdrawItem(airId, 30);
      withdrawItem(lawId, 6);
      if (potUp) {
        withdrawSuperAttack(1);
        withdrawSuperStrength(1);
      }
      withdrawItem(foodId, foodWithdrawAmount);
      bankItemCheck(foodId, 30);
      bankItemCheck(airId, 50);
      bankItemCheck(lawId, 10);
      c.closeBank();
    }
    inventoryItemCheck(airId, 30);
    inventoryItemCheck(lawId, 6);
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
    c.setStatus("@gre@Teleporting to Bank.");
    teleportCamelot();
    totalTrips = totalTrips + 1;
    c.walkTo(468, 462);
    if (c.getObjectAtCoord(467, 463) == 57) {
      c.setStatus("@gre@Opening Castle Gate..");
      c.walkTo(468, 462);
      c.atObject(467, 463);
      c.sleep(1000);
    }
    if (c.getInventoryItemCount(amuletId) > 0) c.equipItem(c.getInventoryItemSlotIndex(amuletId));
    c.walkTo(468, 464);
    c.walkTo(478, 464);
    c.walkTo(488, 464);
    c.walkTo(496, 455);
    c.walkTo(501, 454);
    if (c.getInventoryItemCount(amuletId) > 0) c.equipItem(c.getInventoryItemSlotIndex(amuletId));
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
    c.sleep(2 * 640);
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
    c.equipItem(c.getInventoryItemSlotIndex(amuletId));
  }

  private void entranceScript() {
    boatScript();
    firstTree();
    secondTree();
    thirdTree();
    c.sleep(3000); // nice long sleep before wielding amulet
    c.equipItem(c.getInventoryItemSlotIndex(ItemId.GLARIALS_AMULET.getId()));
    c.sleep(1280);
    if (!c.isItemIdEquipped(ItemId.GLARIALS_AMULET.getId())) {
      c.setAutoLogin(false);
      c.logout();
      if (!c.isLoggedIn()) {
        c.stop();
      }
    }
    c.walkTo(659, 3303);
    waterfallGateScript();
    c.walkTo(659, 3289);
    c.sleep(1280);
    c.equipItem(c.getInventoryItemSlotIndex(amuletId));
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

  private void setupGUI() {
    JLabel header = new JLabel("Waterfall Fire Giant Killer - By Kaila");
    JLabel label1 = new JLabel("Start in Seers bank OR in Giants room");
    JLabel label2 = new JLabel("Bank: Sharks, Law, Air");
    JLabel label3 = new JLabel("Inventory: glariels amulet and rope");
    JLabel label3b = new JLabel("Equipped: your chosen AmuletId");
    JLabel label4 = new JLabel("Bot will attempt to wield swap amulets");
    JLabel label5 = new JLabel("Amulet ItemId to switch back too");
    JTextField amuletIdField = new JTextField("");
    JCheckBox potUpCheckbox = new JCheckBox("Use super Atk/Str Pots?", false);
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!amuletIdField.getText().toLowerCase().replace(" ", "").isEmpty()) {
            amuletId = Integer.parseInt(amuletIdField.getText().toLowerCase().replace(" ", ""));
          }
          if (!foodWithdrawAmountField.getText().toLowerCase().replace(" ", "").isEmpty())
            foodWithdrawAmount =
                Integer.parseInt(foodWithdrawAmountField.getText().toLowerCase().replace(" ", ""));
          potUp = potUpCheckbox.isSelected();
          foodId = foodIds[foodField.getSelectedIndex()];
          foodName = foodTypes[foodField.getSelectedIndex()];
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
    scriptFrame.add(label3b);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(amuletIdField);
    scriptFrame.add(potUpCheckbox);
    scriptFrame.add(foodLabel);
    scriptFrame.add(foodField);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
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
      c.drawString("@red@Waterfall Fire Giants @whi@~ @mag@Kaila", 330, 48, 0xFFFFFF, 1);
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
