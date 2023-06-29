package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * Asgarnian Hobs Peninsula - By Kaila
 *
 * <p>Start in Fally East bank with Armor or Hobs Peninsula Food IN BANK REQUIRED (reg atk str
 * optional) Can Use regular Atk/Str Pots. Food Withdraw amount selection. Type of Food selection.
 * "Sharks", "Swordfish", "Tuna", "Lobsters"
 *
 * <p>Author - Kaila
 */
public class K_HobsPeninsula extends K_kailaScript {
  private static boolean isWithinLootzone(int x, int y) {
    return c.distance(363, 610, x, y) <= 15; // center of lootzone
  }

  private static int totalLimp = 0;
  private static final int[] loot = {
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    1092, // rune spear
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond
    33, // air rune
    34, // Earth rune
    31, // fire rune
    32, // water rune
    36, // body runes
    46, // cosmic
    40, // nature rune
    42, // law rune
    35, // mind rune
    41, // chaos rune
    38, // death rune
    619, // blood rune
    220, // limps
    165, // Grimy Guam
    435, // Grimy mar
    436, // Grimy tar
    437, // Grimy har
    438, // Grimy ranarr
    439, // Grimy irit
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
    11, // bronze arrow
    1026, // unholy mould
    10 // , 	 //coins
    // 20       //bones

  };

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
        scriptStarted = true;
      }
    }
    if (scriptStarted) {
      guiSetup = true;
      c.displayMessage("@red@Asgarnian Hobs Peninsula - By Kaila");
      c.displayMessage("@red@Start in Fally East bank with Armor or Hobs Peninsula");
      c.displayMessage("@red@Food in Bank REQUIRED");
      if (c.isInBank()) {
        c.closeBank();
      }
      if (c.currentY() < 595) {
        bank();
        BankToPeninsula();
        c.sleep(1380);
      }
      scriptStart();
    }
    if (!scriptStarted && !guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {

      eat();
      // buryBones();

      if (c.getInventoryItemCount() < 30) {
        lootScript();
        if (potUp) {
          attackBoost();
          strengthBoost();
        }
        if (!c.isInCombat()) {
          int[] npcIds = {67};
          ORSCharacter npc = c.getNearestNpcByIds(npcIds, false);
          if (npc != null) {
            c.setStatus("@yel@Attacking..");
            c.attackNpc(npc.serverIndex);
            c.sleep(1000);
          } else {
            c.sleep(1000);
            if (c.currentX() != 364 || c.currentY() != 607) {
              c.walkTo(364, 607);
              c.sleep(1000);
            }
          }
        }
        c.sleep(320);
      }
      if (c.getInventoryItemCount() > 29 || c.getInventoryItemCount(foodId) == 0) {
        c.setStatus("@yel@Banking..");
        PeninsulaToBank();
        bank();
        BankToPeninsula();
        c.sleep(618);
      }
    }
  }

  private void lootScript() {
    for (int lootId : loot) {
      int[] coords = c.getNearestItemById(lootId);
      if (coords != null && isWithinLootzone(coords[0], coords[1])) {
        c.setStatus("@yel@Looting..");
        c.walkTo(coords[0], coords[1]);
        c.pickupItem(coords[0], coords[1], lootId, true, true);
        c.sleep(618);
      }
    }
  }

  private void bank() {

    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);

    if (c.isInBank()) {

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
      totalLimp = totalLimp + c.getInventoryItemCount(220);
      totalLoop = totalLoop + c.getInventoryItemCount(527);
      totalTooth = totalTooth + c.getInventoryItemCount(526);
      totalLeft = totalLeft + c.getInventoryItemCount(1277);
      totalGems =
          totalGems
              + c.getInventoryItemCount(160)
              + c.getInventoryItemCount(159)
              + c.getInventoryItemCount(158)
              + c.getInventoryItemCount(157);
      totalSpear = totalSpear + c.getInventoryItemCount(1092);

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
      if (c.getInventoryItemCount(foodId) < foodWithdrawAmount) { // withdraw foods
        c.withdrawItem(foodId, foodWithdrawAmount);
        c.sleep(340);
      }
      if (c.getBankItemCount(foodId) == 0) {
        c.setStatus("@red@NO Food in the bank, Logging Out!.");
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
        PeninsulaToBank();
        bank();
        BankToPeninsula();
        c.sleep(618);
      }
    }
  }

  private void PeninsulaToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(361, 614);
    c.walkTo(356, 619);
    c.walkTo(346, 619);
    c.walkTo(336, 619);
    c.walkTo(326, 619);
    c.walkTo(319, 619);
    c.walkTo(314, 614);
    c.walkTo(309, 609);
    c.walkTo(309, 607);
    c.walkTo(299, 597);
    c.walkTo(291, 589);
    c.walkTo(291, 576);
    c.walkTo(286, 571);
    c.sleep(640);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToPeninsula() {
    c.setStatus("@gre@Walking to Penensula..");
    c.walkTo(287, 572);
    c.walkTo(291, 576);
    c.walkTo(291, 589);
    c.walkTo(299, 597);
    c.walkTo(309, 607);
    c.walkTo(309, 609);
    c.walkTo(314, 614);
    c.walkTo(319, 619);
    c.walkTo(326, 619);
    c.walkTo(336, 619);
    c.walkTo(346, 619);
    c.walkTo(356, 619);
    c.walkTo(361, 614);
    c.setStatus("@gre@Done Walking..");
  }

  // GUI stuff below (icky)

  private void setupGUI() {
    JLabel header = new JLabel("Asgarnian Hobs Peninsula - By Kaila");
    JLabel label1 = new JLabel("Start in Fally East bank with Armor");
    JLabel label2 = new JLabel("	or at Hobgoblin Peninsula");
    JLabel label3 = new JLabel("Food in bank REQUIRED (pots optional)");
    JCheckBox potUpCheckbox = new JCheckBox("Use regular Atk/Str Pots?", true);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel blankLabel = new JLabel("          ");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals(""))
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          potUp = potUpCheckbox.isSelected();
          foodId = foodIds[foodField.getSelectedIndex()];
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
    scriptFrame.add(potUpCheckbox);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(foodLabel);
    scriptFrame.add(foodField);
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
      int limpSuccessPerHr = 0;
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
        limpSuccessPerHr = (int) (totalLimp * scale);
        lawSuccessPerHr = (int) (totalLaw * scale);
        natSuccessPerHr = (int) (totalNat * scale);
        GemsSuccessPerHr = (int) (totalGems * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);

      } catch (Exception e) {
        // divide by zero
      }
      final int x = 350;
      c.drawString("@red@Hobgoblin Peninsula @gre@by Kaila", x - 20, 48, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Guams: @gre@"
              + totalGuam
              + "@yel@ (@whi@"
              + String.format("%,d", guamSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          62,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Marrentills: @gre@"
              + totalMar
              + "@yel@ (@whi@"
              + String.format("%,d", marSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          76,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tarromins: @gre@"
              + totalTar
              + "@yel@ (@whi@"
              + String.format("%,d", tarSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          90,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Harralanders: @gre@"
              + totalHar
              + "@yel@ (@whi@"
              + String.format("%,d", harSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          104,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Ranarrs: @gre@"
              + totalRan
              + "@yel@ (@whi@"
              + String.format("%,d", ranSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          118,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Irit Herbs: @gre@"
              + totalIrit
              + "@yel@ (@whi@"
              + String.format("%,d", iritSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          132,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Avantoes: @gre@"
              + totalAva
              + "@yel@ (@whi@"
              + String.format("%,d", avaSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          146,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Kwuarms: @gre@"
              + totalKwuarm
              + "@yel@ (@whi@"
              + String.format("%,d", kwuSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          160,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Cadantines: @gre@"
              + totalCada
              + "@yel@ (@whi@"
              + String.format("%,d", cadaSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          174,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Dwarfs: @gre@"
              + totalDwarf
              + "@yel@ (@whi@"
              + String.format("%,d", dwarSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          188,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Limpwurts: @gre@"
              + totalLimp
              + "@yel@ (@whi@"
              + String.format("%,d", limpSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          202,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Laws: @gre@"
              + totalLaw
              + "@yel@ (@whi@"
              + String.format("%,d", lawSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          216,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Nats: @gre@"
              + totalNat
              + "@yel@ (@whi@"
              + String.format("%,d", natSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          230,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Gems: @gre@"
              + totalGems
              + "@yel@ (@whi@"
              + String.format("%,d", GemsSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          244,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tooth: @gre@" + totalTooth + "@yel@ / @whi@Loop: @gre@" + totalLoop,
          x,
          258,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@R.Spear: @gre@" + totalSpear + "@yel@ / @whi@Shield Half: @gre@" + totalLeft,
          x,
          272,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          286,
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, 300, 0xFFFFFF, 1);
    }
  }
}
