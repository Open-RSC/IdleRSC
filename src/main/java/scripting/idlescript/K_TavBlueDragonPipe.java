package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import orsc.ORSCharacter;

/**
 * Wildy Fire Giant Killer - By Kaila.
 *
 * <p>
 *
 * <p>Start in Fally west with gear on, or in Dragon room!
 *
 * <p>Uses Coleslaw agility pipe shortcut.
 *
 * <p>70 Agility required, for the shortcut.
 *
 * <p>Sharks/Laws/Airs/Earths IN BANK REQUIRED.
 *
 * <p>31 Magic Required for escape tele.
 *
 * <p>Adjustable Food Withdraw amount.
 *
 * <p>@Author - Kaila
 */
public final class K_TavBlueDragonPipe extends K_kailaScript {
  private boolean isWithinLootzone(
      int x, int y) { // FURTHEST LOOT is 376, 3368, go 361, 3353  (15 tiles)
    return c.distance(361, 3353, x, y) <= 15; // center of lootzone
  }

  private static int totalRdagger = 0;
  private static int foodWithdrawAmount = 16;
  private static final int[] loot = {
    814, // D Bones
    396, // rune dagger
    40, // nature rune
    42, // law rune
    32, // water rune
    31, // fire rune
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
    154, // Addy Ore
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond
    1092, // rune spear
    795 // D med
  };
  // STARTing script
  public int start(String[] parameters) {
    if (!parameters[0].equals("")) {
      try {
        foodWithdrawAmount = Integer.parseInt(parameters[0]);
      } catch (Exception e) {
        System.out.println("Could not parse parameters!");
        c.displayMessage("@red@Could not parse parameters!");
        c.stop();
      }
      if (foodWithdrawAmount != -1) {
        guiSetup = true;
        scriptStarted = true;
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.displayMessage("@red@Tavelry Blue Dragons (Pipe) - By Kaila");
      c.displayMessage("@red@Start in Fally west with gear on, or in dragon room!");
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

      scriptStart();
    }

    return 1000; // start() must return an int value now.
  }

  // Main Script section
  private void scriptStart() {
    while (c.isRunning()) {

      eat();
      superAttackBoost(2, false);
      superStrengthBoost(2, false);
      dropVial();
      lootScript();

      if (c.getInventoryItemCount(546) > 0) {
        if (c.getInventoryItemCount() < 30) {
          if (!c.isInCombat()) {
            c.setStatus("@yel@Attacking Dragons");
            c.sleepHandler(98, true);
            ORSCharacter npc = c.getNearestNpcById(202, false);
            if (npc != null) {
              c.attackNpc(npc.serverIndex);
              c.sleep(1000);
            } else {
              lootScript();
              c.sleep(640);
              walkToCenter();
            }
          }
          c.sleep(800);
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
      if (c.getInventoryItemCount(546) < 1) {
        pipeEscape();
        DragonsToBank();
        bank();
        BankToDragons();
        c.sleep(618);
      }
    }
  }

  private void walkToCenter() {
    if (c.currentX() != 370 || c.currentY() != 3353) {
      c.walkTo(370, 3353);
      c.sleep(1000);
    }
  }

  private void lootScript() {
    for (int lootId : loot) {
      try {
        int[] coords = c.getNearestItemById(lootId);
        if (coords != null && isWithinLootzone(coords[0], coords[1])) {
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
    c.sleep(1200);

    if (c.isInBank()) {

      totalBones = totalBones + c.getInventoryItemCount(814);
      totalRdagger = totalRdagger + c.getInventoryItemCount(396);
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
      totalWater = totalWater + c.getInventoryItemCount(32);
      totalAddy = totalAddy + c.getInventoryItemCount(154);
      totalLoop = totalLoop + c.getInventoryItemCount(527);
      totalTooth = totalTooth + c.getInventoryItemCount(526);
      totalLeft = totalLeft + c.getInventoryItemCount(1277);
      totalSpear = totalSpear + c.getInventoryItemCount(1092);

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != 486
            && itemId != 487
            && itemId != 488
            && itemId != 492
            && itemId != 493
            && itemId != 494) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1400); // Important, leave in

      if (c.getInventoryItemCount(33) < 18) { // air
        c.withdrawItem(33, 18 - c.getInventoryItemCount(33));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(42) < 6) { // law
        c.withdrawItem(42, 6 - c.getInventoryItemCount(42));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(32) < 6) { // water
        c.withdrawItem(32, 6 - c.getInventoryItemCount(32));
        c.sleep(1000);
      }
      c.sleep(640); // leave in
      withdrawSuperAttack(1);
      withdrawSuperStrength(1);
      if (c.getInventoryItemCount(33) < 18) { // air
        c.withdrawItem(33, 18 - c.getInventoryItemCount(33));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(42) < 6) { // law
        c.withdrawItem(42, 6 - c.getInventoryItemCount(42));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(32) < 6) { // water
        c.withdrawItem(32, 6 - c.getInventoryItemCount(32));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(546) < foodWithdrawAmount) { // withdraw 20 shark
        c.withdrawItem(546, foodWithdrawAmount - c.getInventoryItemCount(546));
        c.sleep(640);
      }
      bankBones = c.getBankItemCount(814);
      if (c.getBankItemCount(546) == 0
          || c.getBankItemCount(33) == 0
          || c.getBankItemCount(42) == 0
          || c.getBankItemCount(32) == 0) {
        c.setStatus("@red@NO Sharks/Laws/Airs/DF shield in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.sleep(5000);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      if (!c.isItemIdEquipped(420)) {
        c.setStatus("@red@Not Wielding Dragonfire Shield!.");
        if (c.getBankItemCount(420) == 0) {
          c.setAutoLogin(false);
          c.logout();
          if (!c.isLoggedIn()) {
            c.stop();
          }
        }
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
        pipeEscape();
        c.sleep(600);
        DragonsToBank();
        bank();
        BankToDragons();
      }
    }
  }

  // PATHING private voids

  private void BankToDragons() {
    c.setStatus("@gre@Walking to Tav Gate..");
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
    c.walkTo(350, 501);
    c.walkTo(355, 506);
    c.walkTo(360, 511);
    c.walkTo(362, 513);
    c.walkTo(367, 514);
    c.walkTo(374, 521);
    c.walkTo(376, 521);
    c.atObject(376, 520);
    c.sleep(640);
    c.walkTo(375, 3352);
    c.atObject(374, 3352);
    c.sleep(640);
    c.walkTo(372, 3352);
    c.sleep(320);
    c.setStatus("@gre@Done Walking..");
  }

  private void pipeEscape() {
    c.setStatus("We've ran out of Food! @gre@Going through Pipe.");
    c.walkTo(372, 3352);
    c.atObject(373, 3352);
    c.sleep(1000);
  }

  private void DragonsToBank() {
    teleportOutFalador();
    totalTrips = totalTrips + 1;
    c.sleep(308);
    c.walkTo(327, 552);
    c.sleep(308);
    c.setStatus("@gre@Done Walking..");
  }
  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("Tavelry Blue Dragons (Pipe) - By Kaila");
    JLabel label1 = new JLabel("Start in Fally west with gear on, or in Dragon room!");
    JLabel label2 = new JLabel("Sharks, Law, Water, Air IN BANK required");
    JLabel label3 = new JLabel("70 Agility required, for the shortcut!");
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(20));
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals(""))
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());

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
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
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
      int DbonesSuccessPerHr = 0;
      int RdaggerSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int FireSuccessPerHr = 0;
      int LawSuccessPerHr = 0;
      int NatSuccessPerHr = 0;
      int WaterSuccessPerHr = 0;
      int AddySuccessPerHr = 0;
      int HerbSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        DbonesSuccessPerHr = (int) (totalBones * scale);
        RdaggerSuccessPerHr = (int) (totalRdagger * scale);
        GemsSuccessPerHr = (int) (totalGems * scale);
        FireSuccessPerHr = (int) (totalFire * scale);
        LawSuccessPerHr = (int) (totalLaw * scale);
        NatSuccessPerHr = (int) (totalNat * scale);
        WaterSuccessPerHr = (int) (totalWater * scale);
        AddySuccessPerHr = (int) (totalAddy * scale);
        HerbSuccessPerHr = (int) (totalHerb * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Tavelry Blue Dragons @gre@by Kaila", x, y - 3, 0xFFFFFF, 1);
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
          1);
      c.drawString(
          "@whi@R. Dagger: @gre@"
              + totalRdagger
              + "@yel@ (@whi@"
              + String.format("%,d", RdaggerSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Addy Plate: @gre@"
              + totalAddy
              + "@yel@ (@whi@"
              + String.format("%,d", AddySuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Addy Ore: @gre@"
              + totalAddy
              + "@yel@ (@whi@"
              + String.format("%,d", AddySuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
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
              + "@whi@Natures: @gre@"
              + totalNat
              + "@yel@ (@whi@"
              + String.format("%,d", NatSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Waters: @gre@"
              + totalWater
              + "@yel@ (@whi@"
              + String.format("%,d", WaterSuccessPerHr)
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
          "@whi@Left Half: @gre@" + totalLeft + "@yel@ / @whi@Rune Spear: @gre@" + totalSpear,
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
