package scripting.idlescript;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.JCheckBox;
import orsc.ORSCharacter;

/**
 * Ice Dungeon Hob/Pirate Killer - By Kaila
 *
 * <p>Start in Fally East bank or In Ice Cave. Sharks IN BANK REQUIRED (pots optional). Use regular
 * Atk/Str Pots Option. Food Withdraw amount Selection.
 *
 * <p>Author - Kaila
 */
public class K_AsgarnianPirateHobs extends IdleScript {

  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  boolean potUp = true;

  int totalGuam = 0;
  int totalMar = 0;
  int totalTar = 0;
  int totalHar = 0;
  int totalRan = 0;
  int totalIrit = 0;
  int totalAva = 0;
  int totalKwuarm = 0;
  int totalCada = 0;
  int totalDwarf = 0;
  int totalLaw = 0;
  int totalNat = 0;
  int totalLoop = 0;
  int totalTooth = 0;
  int totalLeft = 0;
  int totalSpear = 0;
  int totalGems = 0;
  int totalTrips = 0;
  int foodWithdrawAmount = 1;

  int[] bones = {20, 413, 604, 814};
  int[] attackPot = {476, 475, 474}; // reg attack pot
  int[] strPot = {224, 223, 222}; // reg str pot

  int[] loot = {
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

  long startTime;
  long startTimestamp = System.currentTimeMillis() / 1000L;

  public boolean isWithinLootzone(int x, int y) {
    return controller.distance(282, 3522, x, y) <= 14; // center of lootzone
  }

  public int start(String parameters[]) {

    if (scriptStarted) {
      controller.displayMessage("@red@Asgarnian Pirate Hobs - By Kaila");
      controller.displayMessage("@red@Start in Fally East bank with Armor");
      controller.displayMessage("@red@Sharks IN BANK REQUIRED");
      if (controller.isInBank()) {
        controller.closeBank();
      }
      if (controller.currentY() < 3000) {
        bank();
        BankToIce();
        controller.sleep(1380);
      }
      scriptStart();
    } else {
      if (parameters[0].equals("")) {
        if (!guiSetup) {
          setupGUI();
          guiSetup = true;
        }
      } else {
        try {
          foodWithdrawAmount = Integer.parseInt(parameters[0]);
        } catch (Exception e) {
          System.out.println("Could not parse parameters!");
          controller.displayMessage("@red@Could not parse parameters!");
          controller.stop();
        }
      }
    }
    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (controller.isRunning()) {

      eat();
      buryBones();

      if (controller.getInventoryItemCount() < 30) {

        boolean lootPickedUp = false;
        for (int lootId : loot) {
          int[] coords = controller.getNearestItemById(lootId);
          if (coords != null && this.isWithinLootzone(coords[0], coords[1])) {
            controller.setStatus("@yel@Looting..");
            controller.walkTo(coords[0], coords[1]);
            controller.pickupItem(coords[0], coords[1], lootId, true, true);
            controller.sleep(618);
          }
        }
        if (lootPickedUp) // we don't want to start to pickup loot then immediately attack a npc
        continue;

        if (potUp == true) {
          if (controller.getCurrentStat(controller.getStatId("Attack"))
              == controller.getBaseStat(controller.getStatId("Attack"))) {
            if (controller.getInventoryItemCount(attackPot[0]) > 0
                || controller.getInventoryItemCount(attackPot[1]) > 0
                || controller.getInventoryItemCount(attackPot[2]) > 0) {
              attackBoost();
            }
          }
          if (controller.getCurrentStat(controller.getStatId("Strength"))
              == controller.getBaseStat(controller.getStatId("Strength"))) {
            if (controller.getInventoryItemCount(strPot[0]) > 0
                || controller.getInventoryItemCount(strPot[1]) > 0
                || controller.getInventoryItemCount(strPot[2]) > 0) {
              strengthBoost();
            }
          }
        }
        if (controller.currentX() > 295 && controller.currentY() > 3000) {
          controller.setStatus("@yel@Too far West, walking back..");
          controller.walkTo(283, 3521);
          controller.sleep(1000);
        }
        if (!controller.isInCombat()) {
          int[] npcIds = {67, 137};
          ORSCharacter npc = controller.getNearestNpcByIds(npcIds, false);
          if (npc != null) {
            controller.setStatus("@yel@Attacking..");
            // controller.walktoNPC(npc.serverIndex,1);
            controller.attackNpc(npc.serverIndex);
            controller.sleep(1000);
          } else {
            controller.sleep(1000);
            if (controller.currentX() != 283 || controller.currentY() != 3521) {
              controller.walkTo(283, 3521);
              controller.sleep(1000);
            }
          }
        }
        controller.sleep(320);
      }
      if (controller.getInventoryItemCount() > 29 || controller.getInventoryItemCount(546) == 0) {
        controller.setStatus("@yel@Banking..");
        IceToBank();
        bank();
        BankToIce();
        controller.sleep(618);
      }
    }
  }

  public void bank() {

    controller.setStatus("@yel@Banking..");
    controller.openBank();
    controller.sleep(640);

    if (controller.isInBank()) {

      totalGuam = totalGuam + controller.getInventoryItemCount(165);
      totalMar = totalMar + controller.getInventoryItemCount(435);
      totalTar = totalTar + controller.getInventoryItemCount(436);
      totalHar = totalHar + controller.getInventoryItemCount(437);
      totalRan = totalRan + controller.getInventoryItemCount(438);
      totalIrit = totalIrit + controller.getInventoryItemCount(439);
      totalAva = totalAva + controller.getInventoryItemCount(440);
      totalKwuarm = totalKwuarm + controller.getInventoryItemCount(441);
      totalCada = totalCada + controller.getInventoryItemCount(442);
      totalDwarf = totalDwarf + controller.getInventoryItemCount(443);
      totalLaw = totalLaw + controller.getInventoryItemCount(42);
      totalNat = totalNat + controller.getInventoryItemCount(40);
      totalLoop = totalLoop + controller.getInventoryItemCount(527);
      totalTooth = totalTooth + controller.getInventoryItemCount(526);
      totalLeft = totalLeft + controller.getInventoryItemCount(1277);
      totalSpear = totalSpear + controller.getInventoryItemCount(1092);
      totalGems =
          totalGems
              + controller.getInventoryItemCount(160)
              + controller.getInventoryItemCount(159)
              + controller.getInventoryItemCount(158)
              + controller.getInventoryItemCount(157);

      if (controller.getInventoryItemCount() > 2) {
        for (int itemId : controller.getInventoryItemIds()) {
          if (itemId != 476
              && itemId != 475
              && itemId != 224
              && itemId != 223) { // dont deposit partial potions!
            controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
          }
        }
      }
      controller.sleep(640);
      if (potUp == true) {
        if (controller.getInventoryItemCount(attackPot[0]) < 1
            && controller.getInventoryItemCount(attackPot[1]) < 1
            && controller.getInventoryItemCount(attackPot[2]) < 1) { // withdraw 10 shark if needed
          controller.withdrawItem(attackPot[2], 1);
          controller.sleep(340);
        }
        if (controller.getInventoryItemCount(strPot[0]) < 1
            && controller.getInventoryItemCount(strPot[1]) < 1
            && controller.getInventoryItemCount(strPot[2]) < 1) { // withdraw 10 shark if needed
          controller.withdrawItem(strPot[2], 1);
          controller.sleep(340);
        }
      }
      if (controller.getInventoryItemCount(546) < foodWithdrawAmount) { // withdraw 20 shark
        controller.withdrawItem(546, foodWithdrawAmount - controller.getInventoryItemCount(546));
        controller.sleep(340);
      }
      if (controller.getBankItemCount(546) == 0) {
        controller.setStatus("@red@NO Sharks/Laws/Airs/Earths in the bank, Logging Out!.");
        controller.setAutoLogin(false);
        controller.logout();
        if (!controller.isLoggedIn()) {
          controller.stop();
          return;
        }
      }
      controller.closeBank();
      controller.sleep(640);
    }
  }

  public void buryBones() {
    if (!controller.isInCombat()) {
      for (int id : bones) {
        if (controller.getInventoryItemCount(id) > 0) {
          controller.setStatus("@yel@Burying..");
          controller.itemCommand(id);

          controller.sleep(618);
          buryBones();
        }
      }
    }
  }

  public void eat() {
    int eatLvl = controller.getBaseStat(controller.getStatId("Hits")) - 20;

    if (controller.getCurrentStat(controller.getStatId("Hits")) < eatLvl) {

      leaveCombat();
      controller.setStatus("@red@Eating..");

      boolean ate = false;

      for (int id : controller.getFoodIds()) {
        if (controller.getInventoryItemCount(id) > 0) {
          controller.itemCommand(id);
          controller.sleep(700);
          ate = true;
          break;
        }
      }
      if (!ate) { // only activates if hp goes to -20 again THAT trip, will bank and get new shark
        // usually
        controller.setStatus("@yel@Banking..");
        IceToBank();
        bank();
        BankToIce();
        controller.sleep(618);
      }
    }
  }

  public void attackBoost() {
    leaveCombat();
    if (controller.getInventoryItemCount(attackPot[0]) > 0) {
      controller.itemCommand(attackPot[0]);
      controller.sleep(320);
      return;
    }
    if (controller.getInventoryItemCount(attackPot[1]) > 0) {
      controller.itemCommand(attackPot[1]);
      controller.sleep(320);
      return;
    }
    if (controller.getInventoryItemCount(attackPot[2]) > 0) {
      controller.itemCommand(attackPot[2]);
      controller.sleep(320);
    }
  }

  public void strengthBoost() {
    leaveCombat();
    if (controller.getInventoryItemCount(strPot[0]) > 0) {
      controller.itemCommand(strPot[0]);
      controller.sleep(320);
      return;
    }
    if (controller.getInventoryItemCount(strPot[1]) > 0) {
      controller.itemCommand(strPot[1]);
      controller.sleep(320);
      return;
    }
    if (controller.getInventoryItemCount(strPot[2]) > 0) {
      controller.itemCommand(strPot[2]);
      controller.sleep(320);
    }
  }

  public void IceToBank() {
    controller.setStatus("@gre@Walking to Bank..");
    controller.walkTo(280, 3521);
    controller.walkTo(279, 3531);
    controller.walkTo(279, 3540);
    controller.walkTo(285, 3544);
    controller.atObject(285, 3543); // go up ladder
    controller.sleep(1000);
    controller.walkTo(287, 711);
    controller.walkTo(287, 694);
    controller.walkTo(287, 680);
    controller.walkTo(287, 673);
    controller.walkTo(287, 665);
    controller.walkTo(287, 652);
    controller.walkTo(289, 650);
    controller.walkTo(288, 649);
    controller.walkTo(288, 639);
    controller.walkTo(288, 629);
    controller.walkTo(288, 619);
    controller.walkTo(288, 609);
    controller.walkTo(290, 607);
    controller.walkTo(290, 597);
    controller.walkTo(290, 587);
    controller.walkTo(290, 577);
    controller.walkTo(290, 575);
    controller.walkTo(286, 571);
    controller.sleep(640);
    totalTrips = totalTrips + 1;
    controller.setStatus("@gre@Done Walking..");
  }

  public void BankToIce() {
    controller.setStatus("@gre@Walking to Ice Dungeon..");
    controller.walkTo(287, 571);
    controller.walkTo(290, 575);
    controller.walkTo(290, 577);
    controller.walkTo(290, 587);
    controller.walkTo(290, 597);
    controller.walkTo(290, 607);
    controller.walkTo(288, 609);
    controller.walkTo(288, 619);
    controller.walkTo(288, 629);
    controller.walkTo(288, 639);
    controller.walkTo(288, 649);
    controller.walkTo(289, 650);
    controller.walkTo(287, 652);
    controller.walkTo(287, 665);
    // add pathing??
    controller.walkTo(287, 673);
    controller.walkTo(287, 680);
    controller.walkTo(287, 694);
    controller.walkTo(287, 711);
    controller.walkTo(285, 712);
    controller.atObject(285, 711); // go down ladder
    controller.sleep(1000);
    controller.walkTo(282, 3543);
    controller.walkTo(285, 3544);
    controller.walkTo(279, 3540);
    controller.walkTo(279, 3531);
    controller.walkTo(280, 3521);
    controller.setStatus("@gre@Done Walking..");
  }

  public void leaveCombat() {
    for (int i = 1; i <= 15; i++) {
      if (controller.isInCombat()) {
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
        controller.sleep(600);
      } else {
        controller.setStatus("@red@Done Leaving combat..");
        break;
      }
      controller.sleep(10);
    }
  }

  // GUI stuff below (icky)

  public void setValuesFromGUI(JCheckBox potUpCheckbox) {
    if (potUpCheckbox.isSelected()) {
      potUp = true;
    } else {
      potUp = false;
    }
  }

  public void setupGUI() {
    JLabel header = new JLabel("Ice Dungeon Hob/Pirate Killer - By Kaila");
    JLabel label1 = new JLabel("Start in Fally East bank or In Ice Cave");
    JLabel label2 = new JLabel("Sharks IN BANK REQUIRED (pots optional)");
    JCheckBox potUpCheckbox = new JCheckBox("Use regular Atk/Str Pots?", true);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    JLabel blankLabel = new JLabel("          ");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (!foodWithdrawAmountField.getText().equals(""))
              foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
            setValuesFromGUI(potUpCheckbox);
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            startTime = System.currentTimeMillis();
            scriptStarted = true;
          }
        });

    scriptFrame = new JFrame(controller.getPlayerName() + " - options");

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

  public static String msToString(long milliseconds) {
    long sec = milliseconds / 1000;
    long min = sec / 60;
    long hour = min / 60;
    sec %= 60;
    min %= 60;
    DecimalFormat twoDigits = new DecimalFormat("00");

    return new String(
        twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec));
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      String runTime = msToString(System.currentTimeMillis() - startTime);
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

      try {
        float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
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

      controller.drawString("@red@Asgarnian Pirate Hobs @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
      controller.drawString(
          "@whi@Guams: @gre@"
              + String.valueOf(this.totalGuam)
              + "@yel@ (@whi@"
              + String.format("%,d", guamSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          62,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Marrentills: @gre@"
              + String.valueOf(this.totalMar)
              + "@yel@ (@whi@"
              + String.format("%,d", marSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          76,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Tarromins: @gre@"
              + String.valueOf(this.totalTar)
              + "@yel@ (@whi@"
              + String.format("%,d", tarSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          90,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Harralanders: @gre@"
              + String.valueOf(this.totalHar)
              + "@yel@ (@whi@"
              + String.format("%,d", harSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          104,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Ranarrs: @gre@"
              + String.valueOf(this.totalRan)
              + "@yel@ (@whi@"
              + String.format("%,d", ranSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          118,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Irit Herbs: @gre@"
              + String.valueOf(this.totalIrit)
              + "@yel@ (@whi@"
              + String.format("%,d", iritSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          132,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Avantoes: @gre@"
              + String.valueOf(this.totalAva)
              + "@yel@ (@whi@"
              + String.format("%,d", avaSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          146,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Kwuarms: @gre@"
              + String.valueOf(this.totalKwuarm)
              + "@yel@ (@whi@"
              + String.format("%,d", kwuSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          160,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Cadantines: @gre@"
              + String.valueOf(this.totalCada)
              + "@yel@ (@whi@"
              + String.format("%,d", cadaSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          174,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Dwarfs: @gre@"
              + String.valueOf(this.totalDwarf)
              + "@yel@ (@whi@"
              + String.format("%,d", dwarSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          188,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Laws: @gre@"
              + String.valueOf(this.totalLaw)
              + "@yel@ (@whi@"
              + String.format("%,d", lawSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          202,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Nats: @gre@"
              + String.valueOf(this.totalNat)
              + "@yel@ (@whi@"
              + String.format("%,d", natSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          216,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Total Gems: @gre@"
              + String.valueOf(this.totalGems)
              + "@yel@ (@whi@"
              + String.format("%,d", GemsSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          230,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Tooth: @gre@"
              + String.valueOf(this.totalTooth)
              + "@yel@ / @whi@Loop: @gre@"
              + String.valueOf(this.totalLoop),
          350,
          244,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@R.Spear: @gre@"
              + String.valueOf(this.totalSpear)
              + "@yel@ / @whi@Shield Half: @gre@"
              + String.valueOf(this.totalLeft),
          350,
          258,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Total Trips: @gre@"
              + String.valueOf(this.totalTrips)
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          272,
          0xFFFFFF,
          1);
      controller.drawString("@whi@Runtime: " + runTime, 350, 286, 0xFFFFFF, 1);
    }
  }
}
