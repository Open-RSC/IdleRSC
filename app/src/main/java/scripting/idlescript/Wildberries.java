package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import javax.swing.*;

/**
 * Picks whiteberries in wilderness. Needs antidragon shields.
 *
 * @author Dvorak
 */
public class Wildberries extends IdleScript {
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  int totalTrips = 0;
  final int[] gateToBerries =
      new int[] {
        140, 181,
        142, 191,
        141, 207,
        137, 213
      };

  final int[] varrockToGate =
      new int[] {
        103, 509,
        109, 498,
        110, 483,
        110, 469,
        110, 455,
        110, 441,
        110, 427,
        110, 415,
        110, 400,
        110, 385,
        104, 380,
        97, 372,
        91, 361,
        84, 352,
        76, 342,
        76, 334,
        82, 325,
        84, 313,
        84, 302,
        78, 294,
        78, 284,
        79, 267,
        80, 255,
        80, 241,
        80, 230,
        80, 220,
        80, 210,
        80, 200,
        80, 193,
        88, 183,
        101, 175,
        111, 176,
        122, 179,
        136, 179,
        140, 180
      };

  int berriesPicked = 0;
  int berriesBanked = 0;
  int sharksInBank = 0;
  int foodWithdrawAmount = 3;
  long startTime;
  final long startTimestamp = System.currentTimeMillis() / 1000L;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      controller.displayMessage("@red@Wildberries by Dvorak. Rewritten by Kaila");
      controller.displayMessage(
          "@red@Start in Varrock East bank! You need antidragon shields in the bank!");

      if (controller.isInBank()) controller.closeBank();
      if (controller.currentY() > 509) {
        bank();
        eat();
        eat();
        eat();
        controller.sleep(618);
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
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      // leaveCombat();
      eat();

      if (!controller.isItemIdEquipped(420)) {
        controller.equipItem(controller.getInventoryItemSlotIndex(420));
        controller.sleep(618);
      }

      if (controller.getInventoryItemCount(471) == 0 && controller.currentY() > 250) {

        controller.setStatus("@red@Walking to Berries..");
        controller.walkPath(varrockToGate);
        gateCheckNorthToSouth();
        controller.walkPath(gateToBerries);
      }
      if (controller.getInventoryItemCount() != 30
          && controller.currentY()
              < 250) { // whjile not full inv, grab berries //controller.getInventoryItemCount(471)
        // > 0 &&

        controller.setStatus("@red@Picking Berries..");

        if (controller.isInCombat()) {
          controller.setStatus("@red@In Combat, escaping..");
          controller.walkTo(136, 207);
          controller.sleep(600);
          controller.walkTo(137, 212);
          controller.walkTo(137, 213);
          eat();
        }
        if (controller.getGroundItemAmount(471, 137, 213) > 0) {
          controller.walkTo(134, 213);
          controller.walkTo(137, 213);
          controller.pickupItem(137, 213, 471, true, false);
          controller.sleep(618);
          eat();
        }
        if (controller.getGroundItemAmount(471, 131, 205) > 0) {
          controller.walkTo(134, 213);
          controller.walkTo(131, 210);
          controller.walkTo(131, 205);
          controller.pickupItem(131, 205, 471, true, false);
          controller.sleep(618);
          controller.walkTo(131, 210);
          eat();
        }
      }
      if (controller.getInventoryItemCount() == 30
          || controller.getInventoryItemCount(546) == 0) { // no sharks or full inv then bank
        controller.setStatus("@red@Walking to Bank..");
        controller.walkPathReverse(gateToBerries);
        gateCheckSouthToNorth();
        controller.walkPathReverse(varrockToGate);
        totalTrips = totalTrips + 1;
        bank();
        eat();
        eat();
        eat();
        controller.sleep(618);
        if (controller.getInventoryItemCount(546) < foodWithdrawAmount) {
          bank();
          eat();
          eat();
        }
      }
    }
  }

  public void gateCheckSouthToNorth() {
    for (int i = 1; i <= 15; i++) {
      if (controller.currentX() > 139
          && controller.currentX() < 142
          && controller.currentY() == 181) {
        controller.setStatus("@gre@Opening Dragon Gate South to North..");
        controller.atObject(140, 180);
        controller.sleep(900);
      } else {
        controller.setStatus("@red@Done Opening Dragon Gate South to North..");
        break;
      }
      controller.sleep(10);
    }
  }

  public void gateCheckNorthToSouth() {
    for (int i = 1; i <= 15; i++) {
      if (controller.currentX() > 139
          && controller.currentX() < 142
          && controller.currentY() == 180) {
        controller.setStatus("@gre@Opening Dragon Gate North to South..");
        controller.atObject(140, 180);
        controller.sleep(900);
      } else {
        controller.setStatus("@red@Done Opening Dragon Gate North to South..");
        break;
      }
      controller.sleep(10);
    }
  }

  public void eat() {
    if (controller.getCurrentStat(controller.getStatId("Hits"))
        <= (controller.getBaseStat(controller.getStatId("Hits")) - 20)) {
      controller.setStatus("@red@Eating..");
      leaveCombat();
      for (int id : controller.getFoodIds()) {
        if (controller.getInventoryItemCount(id) > 0) {
          controller.itemCommand(id);
          controller.sleep(700);
          break;
        }
      }
    }
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

  public void bank() {
    controller.setStatus("@red@Banking..");

    controller.openBank();
    controller.sleep(640);
    berriesPicked = berriesPicked + controller.getInventoryItemCount(471);

    if (controller.isInBank()) {

      for (int itemId : controller.getInventoryItemIds()) {
        if (itemId != 420 && itemId != 400 && itemId != 402 && itemId != 795 && itemId != 546) {
          controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
        }
      }
      controller.sleep(1000); // Important, leave in

      if (controller.getInventoryItemCount(546) > foodWithdrawAmount) {
        controller.depositItem(546, controller.getInventoryItemCount(546) - foodWithdrawAmount);
        controller.sleep(840);
      }
      if (controller.getInventoryItemCount(546) < foodWithdrawAmount) {
        controller.withdrawItem(546, foodWithdrawAmount - controller.getInventoryItemCount(546));
        controller.sleep(840);
      }
      if (!controller.isItemIdEquipped(420)) {
        controller.withdrawItem(420, 1);
        controller.sleep(840);
      }
      if (controller.getBankItemCount(546) == 0) {
        controller.setStatus("@red@NO Sharks in the bank, Logging Out!.");
        controller.setAutoLogin(false);
        controller.sleep(5000);
        controller.logout();
        if (!controller.isLoggedIn()) {
          controller.stop();
          return;
        }
      }
    }
    berriesBanked = controller.getBankItemCount(471);
    sharksInBank = controller.getBankItemCount(546);
    controller.closeBank();
  }

  public void setupGUI() {
    JLabel header = new JLabel("Wildberries by Dvorak");
    JLabel label1 = new JLabel("Rewritten and expanded by Kaila");
    JLabel label2 = new JLabel("Start in Varrock East bank or at RDI white berries");
    JLabel label3 = new JLabel("You need antidragon shields & sharks in the bank");
    JLabel label4 = new JLabel("If Red Dragons are being botted, bring less food");
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(3));
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

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  public static String msToString(long milliseconds) {
    long sec = milliseconds / 1000;
    long min = sec / 60;
    long hour = min / 60;
    sec %= 60;
    min %= 60;
    DecimalFormat twoDigits = new DecimalFormat("00");

    return twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec);
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      String runTime = msToString(System.currentTimeMillis() - startTime);
      int berriesPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        berriesPerHr = (int) (berriesPicked * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }

      // controller.drawBoxAlpha(7, 7, 160, 21+14+14, 0xFF0000, 48);
      controller.drawString("@red@Wildberries @gre@by Dvorak & Kaila", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@whi@Berries picked: @gre@"
              + String.format("%,d", berriesPicked)
              + " @yel@(@whi@"
              + String.format("%,d", berriesPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Berries in bank: @gre@" + String.format("%,d", berriesBanked),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Sharks in bank: @gre@" + String.format("%,d", sharksInBank),
          10,
          21 + 14 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Total Trips: @gre@"
              + this.totalTrips
              + "@yel@(@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14 + 14 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Runtime: " + runTime, 10, 21 + 14 + 14 + 14 + 14 + 14, 0xFFFFFF, 1);
    }
  }
}
