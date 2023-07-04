package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * Ice Dungeon Hob/Pirate Killer
 *
 * <p>
 *
 * <p>Start in Fally East bank or in Ice Cave.
 *
 * <p>Food in bank required. (pots optional).
 *
 * <p>Use regular Atk/Str Pots Option.
 *
 * <p>Food Withdraw amount Selection.
 *
 * @author Kaila
 */
public final class K_AsgarnianPirateHobs extends K_kailaScript {

  private static boolean isWithinLootzone(int x, int y) {
    return c.distance(282, 3522, x, y) <= 14; // center of lootzone
  }

  private static final int[] loot = {
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    1092, // rune spear
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond
    40, // nature rune
    42, // law rune
    33, // air rune
    34, // Earth rune
    36, // body runes
    31, // fire rune
    32, // water rune
    41, // chaos rune
    38, // death rune
    619, // blood rune
    46, // cosmic
    11, // bronze arrow
    1026, // unholy mould
    10, // coins
    20, // bones
    165, // Grimy Guam
    435, // Grimy mar
    436, // Grimy tar
    437, // Grimy har
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443 // Grimy dwu
  };

  public int start(String[] parameters) {
    if (parameters[0].toLowerCase().startsWith("auto")) {
      c.displayMessage("Got Autostart, using 1 Shark, yes pots", 0);
      System.out.println("Got Autostart, using 1 Shark, yes pots");
      foodWithdrawAmount = 1;
      potUp = true;
      guiSetup = true;
      scriptStarted = true;
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.displayMessage("@red@Asgarnian Pirate Hobs - By Kaila");
      c.displayMessage("@red@Start in Fally East bank with Armor");
      c.displayMessage("@red@Sharks IN BANK REQUIRED");
      if (c.isInBank()) {
        c.closeBank();
      }
      if (c.currentY() < 3000) {
        bank();
        BankToIce();
        c.sleep(1380);
      }
      scriptStart();
    }

    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {

      eat();
      buryBones();

      if (c.getInventoryItemCount() < 30) {
        lootScript();
        if (potUp) {
          attackBoost(false);
          strengthBoost(false);
        }
        if (c.currentX() > 295 && c.currentY() > 3000) {
          c.setStatus("@yel@Too far West, walking back..");
          c.walkTo(283, 3521);
          c.sleep(1000);
        }
        if (!c.isInCombat()) {
          int[] npcIds = {67, 137};
          ORSCharacter npc = c.getNearestNpcByIds(npcIds, false);
          if (npc != null) {
            c.setStatus("@yel@Attacking..");
            // c.walktoNPC(npc.serverIndex,1);
            c.attackNpc(npc.serverIndex);
            c.sleep(1000);
          } else {
            c.sleep(1000);
            if (c.currentX() != 283 || c.currentY() != 3521) {
              c.walkTo(283, 3521);
              c.sleep(1000);
            }
          }
        }
        c.sleep(320);
      }
      if (c.getInventoryItemCount() > 29 || c.getInventoryItemCount(546) == 0) {
        c.setStatus("@yel@Banking..");
        IceToBank();
        bank();
        BankToIce();
        c.sleep(618);
      }
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
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {

      totalGuam = totalGuam + c.getInventoryItemCount(165);
      totalMar = totalMar + c.getInventoryItemCount(435);
      totalTar = totalTar + c.getInventoryItemCount(436);
      totalHar = totalHar + c.getInventoryItemCount(437);
      totalRan = totalRan + c.getInventoryItemCount(438);
      totalIrit = totalIrit + c.getInventoryItemCount(439);
      totalAva = totalAva + c.getInventoryItemCount(440);
      totalKwuarm = totalKwuarm + c.getInventoryItemCount(441);
      totalCada = totalCada + c.getInventoryItemCount(442);
      totalDwarf = totalDwarf + c.getInventoryItemCount(443);
      totalLaw = totalLaw + c.getInventoryItemCount(42);
      totalNat = totalNat + c.getInventoryItemCount(40);
      totalLoop = totalLoop + c.getInventoryItemCount(527);
      totalTooth = totalTooth + c.getInventoryItemCount(526);
      totalLeft = totalLeft + c.getInventoryItemCount(1277);
      totalSpear = totalSpear + c.getInventoryItemCount(1092);
      totalGems =
          totalGems
              + c.getInventoryItemCount(160)
              + c.getInventoryItemCount(159)
              + c.getInventoryItemCount(158)
              + c.getInventoryItemCount(157);

      if (c.getInventoryItemCount() > 2) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 476
              && itemId != 475
              && itemId != 224
              && itemId != 223) { // dont deposit partial potions!
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
      }
      c.sleep(640);
      if (potUp) {
        withdrawAttack(1);
        withdrawStrength(1);
      }
      if (c.getInventoryItemCount(546) < foodWithdrawAmount) { // withdraw 20 shark
        c.withdrawItem(546, foodWithdrawAmount - c.getInventoryItemCount(546));
        c.sleep(340);
      }
      if (c.getBankItemCount(546) == 0) {
        c.setStatus("@red@NO Sharks/Laws/Airs/Earths in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      c.closeBank();
      c.sleep(640);
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
        c.setStatus("@yel@Banking..");
        IceToBank();
        bank();
        BankToIce();
        c.sleep(618);
      }
    }
  }

  private void IceToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(280, 3521);
    c.walkTo(279, 3531);
    c.walkTo(279, 3540);
    c.walkTo(285, 3544);
    c.atObject(285, 3543); // go up ladder
    c.sleep(1000);
    c.walkTo(287, 711);
    c.walkTo(287, 694);
    c.walkTo(287, 680);
    c.walkTo(287, 673);
    c.walkTo(287, 665);
    c.walkTo(287, 652);
    c.walkTo(289, 650);
    c.walkTo(288, 649);
    c.walkTo(288, 639);
    c.walkTo(288, 629);
    c.walkTo(288, 619);
    c.walkTo(288, 609);
    c.walkTo(290, 607);
    c.walkTo(290, 597);
    c.walkTo(290, 587);
    c.walkTo(290, 577);
    c.walkTo(290, 575);
    c.walkTo(286, 571);
    c.sleep(640);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToIce() {
    c.setStatus("@gre@Walking to Ice Dungeon..");
    c.walkTo(287, 571);
    c.walkTo(290, 575);
    c.walkTo(290, 577);
    c.walkTo(290, 587);
    c.walkTo(290, 597);
    c.walkTo(290, 607);
    c.walkTo(288, 609);
    c.walkTo(288, 619);
    c.walkTo(288, 629);
    c.walkTo(288, 639);
    c.walkTo(288, 649);
    c.walkTo(289, 650);
    c.walkTo(287, 652);
    c.walkTo(287, 665);
    c.walkTo(287, 673);
    c.walkTo(287, 680);
    c.walkTo(287, 694);
    c.walkTo(287, 711);
    c.walkTo(285, 712);
    c.atObject(285, 711); // go down ladder
    c.sleep(1000);
    c.walkTo(282, 3543);
    c.walkTo(285, 3544);
    c.walkTo(279, 3540);
    c.walkTo(279, 3531);
    c.walkTo(280, 3521);
    c.setStatus("@gre@Done Walking..");
  }

  // GUI stuff below (icky)

  private void setupGUI() {
    JLabel header = new JLabel("Ice Dungeon Hob/Pirate Killer ~ By Kaila");
    JLabel label1 = new JLabel("Start in Fally East bank or In Ice Cave");
    JLabel label2 = new JLabel("Sharks IN BANK REQUIRED (pots optional)");
    JCheckBox potUpCheckbox = new JCheckBox("Use regular Atk/Str Pots?", true);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    JLabel blankLabel = new JLabel("          ");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals(""))
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
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
    scriptFrame.add(potUpCheckbox);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(blankLabel);
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
      int guamSuccessPerHr = 0;
      int marSuccessPerHr = 0;
      int tarSuccessPerHr = 0;
      int harSuccessPerHr = 0;
      int ranSuccessPerHr = 0;
      int iritSuccessPerHr = 0;
      int avaSuccessPerHr = 0;
      int kwuSuccessPerHr = 0;
      int cadaSuccessPerHr = 0;
      int dwarSuccessPerHr = 0;
      int lawSuccessPerHr = 0;
      int natSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        guamSuccessPerHr = (int) (totalGuam * scale);
        marSuccessPerHr = (int) (totalMar * scale);
        tarSuccessPerHr = (int) (totalTar * scale);
        harSuccessPerHr = (int) (totalHar * scale);
        ranSuccessPerHr = (int) (totalRan * scale);
        iritSuccessPerHr = (int) (totalIrit * scale);
        avaSuccessPerHr = (int) (totalAva * scale);
        kwuSuccessPerHr = (int) (totalKwuarm * scale);
        cadaSuccessPerHr = (int) (totalCada * scale);
        dwarSuccessPerHr = (int) (totalDwarf * scale);
        lawSuccessPerHr = (int) (totalLaw * scale);
        natSuccessPerHr = (int) (totalNat * scale);
        GemsSuccessPerHr = (int) (totalGems * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);

      } catch (Exception e) {
        // divide by zero
      }

      c.drawString("@red@Asgarnian Pirate Hobs @mag@~ by Kaila", 330, 48, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Guams: @gre@"
              + totalGuam
              + "@yel@ (@whi@"
              + String.format("%,d", guamSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          62,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Marrentills: @gre@"
              + totalMar
              + "@yel@ (@whi@"
              + String.format("%,d", marSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          76,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tarromins: @gre@"
              + totalTar
              + "@yel@ (@whi@"
              + String.format("%,d", tarSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          90,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Harralanders: @gre@"
              + totalHar
              + "@yel@ (@whi@"
              + String.format("%,d", harSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          104,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Ranarrs: @gre@"
              + totalRan
              + "@yel@ (@whi@"
              + String.format("%,d", ranSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          118,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Irit Herbs: @gre@"
              + totalIrit
              + "@yel@ (@whi@"
              + String.format("%,d", iritSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          132,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Avantoes: @gre@"
              + totalAva
              + "@yel@ (@whi@"
              + String.format("%,d", avaSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          146,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Kwuarms: @gre@"
              + totalKwuarm
              + "@yel@ (@whi@"
              + String.format("%,d", kwuSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          160,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Cadantines: @gre@"
              + totalCada
              + "@yel@ (@whi@"
              + String.format("%,d", cadaSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          174,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Dwarfs: @gre@"
              + totalDwarf
              + "@yel@ (@whi@"
              + String.format("%,d", dwarSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          188,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Laws: @gre@"
              + totalLaw
              + "@yel@ (@whi@"
              + String.format("%,d", lawSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          202,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Nats: @gre@"
              + totalNat
              + "@yel@ (@whi@"
              + String.format("%,d", natSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          216,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Gems: @gre@"
              + totalGems
              + "@yel@ (@whi@"
              + String.format("%,d", GemsSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          230,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tooth: @gre@" + totalTooth + "@yel@ / @whi@Loop: @gre@" + totalLoop,
          350,
          244,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@R.Spear: @gre@" + totalSpear + "@yel@ / @whi@Shield Half: @gre@" + totalLeft,
          350,
          258,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          272,
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, 350, 286, 0xFFFFFF, 1);
    }
  }
}
