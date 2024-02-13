package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Wildy Fire Giant Killer</b>
 *
 * <p>Start in mage bank with Armor <br>
 * Sharks/Laws/Airs/Earths IN BANK REQUIRED <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_WildyFireGiants extends K_kailaScript {
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
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    centerX = 269;
    centerY = 2949;
    centerDistance = 10;
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.displayMessage("@red@Wildy Fire Giant Killer - By Kaila");
      c.displayMessage("@red@Start in Mage bank OR in Giants room");
      c.displayMessage("@red@Sharks IN BANK REQUIRED");
      if (c.isInBank()) c.closeBank();
      if (c.currentX() > 260 && c.currentX() < 275 && c.currentY() < 132 && c.currentY() > 125) {
        stairToGiants();
        c.sleep(1380);
      }
      if (c.currentY() > 3364) {
        bank();
        BankToStair();
        stairToGiants();
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
          || timeToBank
          || c.getInventoryItemCount(546) == 0
          || c.getInventoryItemCount(795) > 0
          || c.getInventoryItemCount(1277) > 0) { // bank if d med, or left half in inv
        c.setStatus("@yel@Banking..");
        GiantsToBank();
        bank();
        BankToStair();
        stairToGiants();
      }
      buryBones(true);
      lootItems(false, loot);
      superAttackBoost(0, true);
      superStrengthBoost(0, true);
      if (!c.isInCombat()) {
        ORSCharacter npc = c.getNearestNpcById(344, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking Giants");
          c.walktoNPC(npc.serverIndex, 1);
          c.attackNpc(npc.serverIndex);
          c.sleep(2 * GAME_TICK);
        } else c.sleep(GAME_TICK);
      } else c.sleep(GAME_TICK);
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
        if (itemId != 486
            && itemId != 487
            && itemId != 488
            && itemId != 492
            && itemId != 493
            && itemId != 494) { // keep partial pots
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1280); // keep, important
      withdrawSuperAttack(1);
      withdrawSuperStrength(1);
      withdrawFood(546, 27);
      bankItemCheck(546, 27);
      c.closeBank();
    }
  }

  private void GiantsToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(273, 2953);
    if (c.getObjectAtCoord(274, 2952) == 57) {
      c.setStatus("@gre@Opening Fire Giant Gate..");
      c.walkTo(273, 2953);
      c.atObject(274, 2952);
      c.sleep(340);
      c.setStatus("@gre@Walkin..");
    }
    c.walkTo(275, 2953);
    c.walkTo(282, 2969); // broke hgere
    if (c.getObjectAtCoord(281, 2969) == 57) {
      c.setStatus("@gre@Opening Chaos Dwarf Gate..");
      c.walkTo(282, 2969);
      c.atObject(281, 2969);
      c.sleep(340);
      c.setStatus("@gre@Walkin..");
    }
    c.walkTo(277, 2971);
    c.walkTo(273, 2972);
    if (c.getObjectAtCoord(272, 2972) == 57) {
      c.setStatus("@gre@Opening Giants Gate..");
      c.walkTo(273, 2972);
      c.atObject(272, 2972);
      c.sleep(340);
      c.setStatus("@gre@Walkin..");
    }
    c.walkTo(269, 2972);
    c.walkTo(269, 2963);
    c.setStatus("@gre@Trying Stairs (1)..");
    c.atObject(268, 2960); // try stairs once
    c.sleep(1280);
    goUpStairs();
    if (c.currentY() > 1000) {
      goUpStairs();
    }
    if (c.currentY() > 1000) {
      goUpStairs();
    }
    c.setStatus("@gre@Walkin..");
    c.walkTo(268, 126);
    c.walkTo(254, 126);
    c.walkTo(232, 104);
    c.walkTo(227, 105);
    c.sleep(340);
    if (!c.isDoorOpen(227, 106)) {
      c.setStatus("@gre@Opening Mage Bank Outer Door..");
      c.walkTo(227, 105);
      c.openDoor(227, 106);
      c.sleep(340);
      c.setStatus("@gre@Walkin..");
    }
    c.walkTo(227, 106);
    c.sleep(1000);
    c.setStatus("@gre@Cutting Outer Web..");
    outerWebIn();
    if (c.getWallObjectIdAtCoord(227, 107) == 24) {
      outerWebIn();
    }
    c.setStatus("@gre@Walkin..");
    c.walkTo(227, 108);
    c.sleep(340);
    c.setStatus("@gre@Cutting Inner Web..");
    innerWebIn();
    if (c.getWallObjectIdAtCoord(227, 109) == 24) {
      innerWebIn();
    }
    c.setStatus("@gre@Walkin..");
    c.walkTo(227, 110);
    c.walkTo(226, 110);
    c.sleep(340);
    if (!c.isDoorOpen(226, 110)) {
      c.setStatus("@gre@Opening Mage Bank Inner Door..");
      c.walkTo(226, 110);
      c.openDoor(226, 110);
      c.sleep(340);
    }
    c.walkTo(224, 110);
    c.sleep(320);
    c.atObject(223, 110);
    c.sleep(320);
    totalTrips = totalTrips + 1;
    c.walkTo(451, 3371);
    c.walkTo(453, 3376);
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToStair() {
    c.setStatus("@gre@Walking to Fire Giants..");
    c.walkTo(453, 3374);
    c.walkTo(450, 3370);
    c.walkTo(446, 3368);
    c.sleep(340);
    c.atObject(446, 3367);
    c.sleep(640);
    if (c.currentX() == 446 && c.currentY() == 3368) {
      c.atObject(446, 3367);
      c.sleep(640);
    }
    c.walkTo(225, 110);
    c.sleep(340);
    if (!c.isDoorOpen(226, 110)) {
      c.setStatus("@gre@Opening Mage Bank Inner Door..");
      c.walkTo(225, 110);
      c.atWallObject(226, 110);
      c.sleep(340);
      c.setStatus("@gre@Walkin..");
    }
    c.walkTo(227, 109);
    c.sleep(340);
    c.setStatus("@gre@Cutting Inner Web..");
    innerWebOut();
    if (c.getWallObjectIdAtCoord(227, 109) == 24) {
      innerWebOut();
    }
    c.setStatus("@gre@Walkin..");
    c.walkTo(227, 107);
    c.sleep(340);
    c.setStatus("@gre@Cutting Outer Web..");
    outerWebOut();
    if (c.getWallObjectIdAtCoord(227, 107) == 24) {
      outerWebOut();
    }
    if (c.getWallObjectIdAtCoord(227, 107) == 24) {
      outerWebOut();
    }
    c.setStatus("@gre@Walkin..");
    c.walkTo(227, 106);
    c.sleep(340);
    if (!c.isDoorOpen(227, 106)) {
      c.setStatus("@gre@Opening Mage Bank Outer Door..");
      c.walkTo(227, 106);
      c.openDoor(227, 106);
      c.sleep(340);
      c.setStatus("@gre@Walkin..");
    }
    c.walkTo(227, 105);
    c.walkTo(232, 104);
    c.walkTo(254, 126);
    c.walkTo(268, 127);
    c.sleep(340);
  }

  private void stairToGiants() {
    c.walkTo(268, 127);
    c.setStatus("@gre@Going down stairs..");
    c.walkTo(268, 127);
    c.atObject(268, 128);
    c.sleep(600);
    c.setStatus("@gre@Walkin..");
    c.walkTo(272, 2972);
    c.sleep(340);
    if (c.getObjectAtCoord(272, 2972) == 57) {
      c.setStatus("@gre@Opening Giants Gate..");
      c.walkTo(272, 2972);
      c.atObject(272, 2972);
      c.sleep(340);
      c.setStatus("@gre@Walkin..");
    }
    c.walkTo(278, 2970);
    c.walkTo(281, 2970);
    c.sleep(340);
    if (c.getObjectAtCoord(281, 2969) == 57) {
      c.setStatus("@gre@Opening Chaos Dwarf Gate..");
      c.walkTo(281, 2970);
      c.atObject(281, 2969);
      c.sleep(340);
      c.setStatus("@gre@Walkin..");
    }
    c.walkTo(283, 2969);
    c.walkTo(281, 2962);
    c.walkTo(274, 2953);
    c.sleep(340);
    if (c.getObjectAtCoord(274, 2952) == 57) {
      c.setStatus("@gre@Opening Fire Giant Gate..");
      c.walkTo(274, 2953);
      c.atObject(274, 2952);
      c.sleep(340);
      c.setStatus("@gre@Walkin..");
    }
    c.walkTo(272, 2953);
    c.setStatus("@gre@Done Walking..");
  }

  private void goUpStairs() {
    for (int i = 1; i <= 15; i++) {
      if (c.currentY() > 1000) {
        c.setStatus("@gre@Going up Stairs..");
        c.walkTo(269, 2963);
        c.atObject(268, 2960);
        c.sleep(800);
      } else {
        c.setStatus("@gre@Done Going up Stairs..");
        c.walkTo(268, 126);
        c.sleep(300);
        break;
      }
      c.sleep(500);
    }
  }

  private void innerWebIn() {
    for (int i = 1; i <= 40; i++) {
      if (c.getWallObjectIdAtCoord(227, 109) == 24) {
        c.setStatus("@gre@Cutting Inner Web..");
        c.atWallObject(227, 109);
        c.sleep(1000);
      } else {
        c.setStatus("@gre@Done Cutting Inner Web..");
        c.walkTo(227, 109);
        c.sleep(340);
        break;
      }
      c.sleep(500);
    }
  }

  private void outerWebIn() {
    for (int i = 1; i <= 40; i++) {
      if (c.getWallObjectIdAtCoord(227, 107) == 24) {
        c.setStatus("@gre@Cutting Outer Web..");
        c.atWallObject(227, 107);
        c.sleep(1000);
      } else {
        c.setStatus("@gre@Done Cutting Outer Web..");
        c.walkTo(227, 107);
        c.sleep(340);
        break;
      }
      c.sleep(500);
    }
  }

  private void innerWebOut() {
    for (int i = 1; i <= 40; i++) {
      if (c.getWallObjectIdAtCoord(227, 109) == 24) {
        c.setStatus("@gre@Cutting Inner Web..");
        c.atWallObject(227, 109);
        c.sleep(1000);
      } else {
        c.setStatus("@gre@Done Cutting Inner Web..");
        c.walkTo(227, 107);
        c.sleep(340);
        break;
      }
      c.sleep(500);
    }
  }

  private void outerWebOut() {
    for (int i = 1; i <= 30; i++) {
      if (c.getWallObjectIdAtCoord(227, 107) == 24) {
        c.setStatus("@gre@Cutting Outer Web..");
        c.atWallObject(227, 107);
        c.sleep(1000);
      } else {
        c.setStatus("@gre@Done Cutting Outer Web..");
        try {
          c.walkTo(227, 106);
          c.sleep(340);
        } catch (Exception e) {
          c.setStatus("@Red@Something went wrong..");
          System.out.println("Something went wrong.");
        }
        break;
      }
      c.sleep(10);
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Wildy Fire Giant Killer ~ By Kaila");
    JLabel label1 = new JLabel("Start in Mage bank OR in Giants room");
    JLabel label2 = new JLabel("Sharks IN BANK REQUIRED");
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
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
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
      c.drawString("@red@Wilderness Fire Giants @whi@~ @mag@Kaila", 330, 48, 0xFFFFFF, 1);
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
