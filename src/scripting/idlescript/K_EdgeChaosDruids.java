package scripting.idlescript;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * Edge Druid Killer - By Kaila.
 *
 * <p>Start in Edge bank with Gear. Sharks in bank REQUIRED.
 *
 * <p>Teleport if Pkers Attack option. 31 Magic, Laws, Airs, and Earths required for Escape Tele.
 * Unselected, bot WALKS to Edge when Attacked. Selected, bot teleports, then walks to edge.
 *
 * <p>Return to Druids after Escaping option. Unselected, bot will log out after escaping Pkers.
 * Selected, bot will grab more food and return.
 *
 * <p>Food Withdraw Amount Selection.
 *
 * <p>todo add regular attack/str pot option? - use asgarnian ice add food type selection? add
 * collect lower level herbs option.
 *
 * <p>Author - Kaila
 */
public class K_EdgeChaosDruids extends IdleScript {
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  boolean teleportOut = false;
  boolean returnEscape = true;
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

  int[] loot = {
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
    40, // nature rune
    42, // law rune
    33, // air rune
    34, // Earth rune
    36, // body runes
    1026, // unholy mould
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    1092 // rune spear
  };
  String[] foodTypes = new String[] {"Sharks", "Swordfish", "Tuna", "Lobsters"};
  long startTime;
  long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String parameters[]) {

    if (scriptStarted) {
      controller.displayMessage("@red@Edge Druid Killer - By Kaila");
      controller.displayMessage("@red@Start in Edge bank with Armor");
      controller.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
      controller.displayMessage("@red@31 Magic Required for escape tele");
      if (controller.isInBank() == true) {
        controller.closeBank();
      }
      if (controller.currentY() < 3000) {
        bank();
        BankToDruid();
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

      if (controller.getInventoryItemCount() < 30) {

        boolean lootPickedUp = false;
        for (int lootId : loot) {
          int[] coords = controller.getNearestItemById(lootId);
          if (coords != null) {
            controller.setStatus("@yel@Looting..");
            controller.walkTo(coords[0], coords[1]);
            controller.pickupItem(coords[0], coords[1], lootId, true, true);
            controller.sleep(618);
          }
        }
        if (lootPickedUp) // we don't want to start to pickup loot then immediately attack a npc
        continue;

        if (!controller.isInCombat()) {
          controller.setStatus("@yel@Attacking Druids");
          ORSCharacter npc = controller.getNearestNpcById(270, false);
          if (npc != null) {
            // controller.walktoNPC(npc.serverIndex,1);
            controller.attackNpc(npc.serverIndex);
            controller.sleep(1000);
          } else {
            controller.sleep(1000);
            if (controller.currentX() != 218 || controller.currentY() != 3245) {
              controller.walkTo(218, 3245);
              controller.sleep(1000);
            }
          }
        }
        controller.sleep(320);
      } else if (controller.getInventoryItemCount() > 29
          || controller.getInventoryItemCount(546) == 0) {
        controller.setStatus("@yel@Banking..");
        DruidToBank();
        bank();
        BankToDruid();
        controller.sleep(618);
      }
    }
  }

  public void bank() {

    controller.setStatus("@yel@Banking..");
    controller.openBank();
    controller.sleep(1200);

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

      for (int itemId : controller.getInventoryItemIds()) {
        if (itemId != 546) {
          controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
        }
      }

      controller.sleep(1400); // Important, leave in

      if (teleportOut == true) {
        if (controller.getInventoryItemCount(33) < 3) { // withdraw 3 air
          controller.withdrawItem(33, 3);
          controller.sleep(640);
        }
        if (controller.getInventoryItemCount(34) < 1) { // withdraw 1 earth
          controller.withdrawItem(34, 1);
          controller.sleep(640);
        }
        if (controller.getInventoryItemCount(42) < 1) { // withdraw 1 law
          controller.withdrawItem(42, 1);
          controller.sleep(640);
        }
      }
      if (controller.getInventoryItemCount(546) > foodWithdrawAmount) { // deposit extra shark
        controller.depositItem(546, controller.getInventoryItemCount(546) - foodWithdrawAmount);
        controller.sleep(640);
      }
      if (controller.getInventoryItemCount(546) < foodWithdrawAmount) { // withdraw 1 shark
        controller.withdrawItem(546, foodWithdrawAmount - controller.getInventoryItemCount(546));
        controller.sleep(640);
      }
      if (controller.getBankItemCount(546) == 0) {
        controller.setStatus("@red@NO Sharks in the bank, Logging Out!.");
        controller.setAutoLogin(false);
        controller.logout();
        if (!controller.isLoggedIn()) {
          controller.stop();
          return;
        }
      }
      controller.closeBank();
      controller.sleep(1000);
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
        controller.setStatus("@red@We've ran out of Food! Running Away!.");
        if (teleportOut == false
            || controller.getInventoryItemCount(42) < 1
            || controller.getInventoryItemCount(33) < 3
            || controller.getInventoryItemCount(34) < 1) { // or no earths/airs/laws
          DruidToBank();
          bank();
        }
        if (teleportOut == true) {
          controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport"));
          controller.sleep(308);
          if (controller.currentY() > 3000) {
            controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport"));
            controller.sleep(308);
          }
          if (controller.currentY() > 3000) {
            controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport(2)"));
            controller.sleep(800);
          }
          if (controller.currentY() > 3000) {
            controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport(3)"));
            controller.sleep(800);
          }
          controller.walkTo(120, 644);
          controller.atObject(119, 642);
          controller.walkTo(217, 447);
        }
        if (returnEscape == false) {
          controller.setAutoLogin(false);
          controller.logout();
          controller.sleep(1000);

          if (!controller.isLoggedIn()) {
            controller.stop();
            return;
          }
        }
        if (returnEscape == true) {
          bank();
          BankToDruid();
          controller.sleep(618);
        }
      }
    }
  }

  public void DruidToBank() {
    controller.setStatus("@gre@Walking to Bank..");
    controller.walkTo(210, 3254);
    controller.walkTo(200, 3254);
    controller.walkTo(196, 3265);
    controller.setStatus("@gre@Opening Wildy Gate North to South(1)..");
    controller.atObject(196, 3266);
    controller.sleep(640);
    openGateNorthToSouth();
    controller.walkTo(197, 3266);
    controller.walkTo(204, 3272);
    controller.walkTo(210, 3273);
    if (controller.getObjectAtCoord(211, 3272) == 57) {
      controller.setStatus("@gre@Opening Edge Gate..");
      controller.walkTo(210, 3273);
      controller.atObject(211, 3272);
      controller.sleep(340);
    }
    controller.setStatus("@gre@Walking to Bank..");
    controller.walkTo(217, 3283);
    controller.walkTo(215, 3294);
    controller.walkTo(215, 3299);
    controller.atObject(215, 3300);
    controller.sleep(640);
    controller.walkTo(217, 458);
    controller.walkTo(221, 447);
    controller.walkTo(217, 448);
    controller.sleep(640);
    totalTrips = totalTrips + 1;
    controller.setStatus("@gre@Done Walking..");
  }

  public void BankToDruid() {
    controller.setStatus("@gre@Walking to Druids..");
    controller.walkTo(221, 447);
    controller.walkTo(217, 458);
    controller.walkTo(215, 467);
    controller.atObject(215, 468);
    controller.sleep(640);
    controller.walkTo(217, 3283);
    controller.walkTo(211, 3273);
    if (controller.getObjectAtCoord(211, 3272) == 57) {
      controller.setStatus("@gre@Opening Edge Gate..");
      controller.walkTo(211, 3273);
      controller.atObject(211, 3272);
      controller.sleep(340);
    }
    controller.setStatus("@gre@Walking to Druids..");
    controller.walkTo(204, 3272);
    controller.walkTo(199, 3272);
    controller.walkTo(197, 3266);
    controller.setStatus("@gre@Opening Wildy Gate, South to North(1)..");
    controller.atObject(196, 3266);
    controller.sleep(640);
    openGateSouthToNorth();
    controller.walkTo(200, 3254);
    controller.walkTo(210, 3254);
    controller.setStatus("@gre@Done Walking..");
  }

  public void leaveCombat() {
    controller.setStatus("@red@Leaving combat..");
    controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
    controller.sleep(600);
    for (int i = 1; i <= 15; i++) {
      if (controller.isInCombat()) {
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
        controller.sleep(600);
      }
      controller.sleep(100);
    }
    controller.setStatus("@gre@Done Leaving combat..");
  }

  public void openGateNorthToSouth() {
    for (int i = 1; i <= 20; i++) {
      if (controller.currentY() == 3265) {
        controller.setStatus("@gre@Opening Wildy Gate..");
        controller.atObject(196, 3266);
        controller.sleep(640);
      }
      controller.sleep(100);
    }
    controller.setStatus("@gre@Done Opening Wildy Gate..");
  }

  public void openGateSouthToNorth() {
    for (int i = 1; i <= 20; i++) {
      if (controller.currentY() == 3266) {
        controller.setStatus("@gre@Opening Wildy Gate..");
        controller.atObject(196, 3266);
        controller.sleep(440);
      }
      controller.sleep(100);
    }
    controller.setStatus("@gre@Done Opening Wildy Gate..");
  }

  // GUI stuff below (icky)
  public void setValuesFromGUI(JCheckBox potUpCheckbox, JCheckBox escapeCheckbox) {
    if (potUpCheckbox.isSelected()) {
      teleportOut = true;
    } else {
      teleportOut = false;
    }
    if (escapeCheckbox.isSelected()) {
      returnEscape = true;
    } else {
      returnEscape = false;
    }
  }

  public void setupGUI() {
    JLabel header = new JLabel("Edge Druid Killer - By Kaila");
    JLabel label1 = new JLabel("Start in Edge bank with Gear");
    JLabel label2 = new JLabel("Sharks in bank REQUIRED");
    JCheckBox teleportCheckbox = new JCheckBox("Teleport if Pkers Attack?", false);
    JLabel label3 = new JLabel("31 Magic, Laws, Airs, and Earths required for Escape Tele");
    JLabel label4 = new JLabel("Unselected, bot WALKS to Edge when Attacked");
    JLabel label5 = new JLabel("Selected, bot teleports, then walks to edge");
    JCheckBox escapeCheckbox = new JCheckBox("Return to Druids after Escaping?", true);
    JLabel label6 = new JLabel("Unselected, bot will log out after escaping Pkers");
    JLabel label7 = new JLabel("Selected, bot will grab more food and return");
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (!foodWithdrawAmountField.getText().equals(""))
              foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
            setValuesFromGUI(teleportCheckbox, escapeCheckbox);
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
    scriptFrame.add(teleportCheckbox);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(escapeCheckbox);
    scriptFrame.add(label6);
    scriptFrame.add(label7);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(startScriptButton);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocus();
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

      controller.drawString("@red@Edgeville Druids @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
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
              + "@yel@ / @whi@Half: @gre@"
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
