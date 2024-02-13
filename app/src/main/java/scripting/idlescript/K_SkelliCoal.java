package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * <b>Skelli Coal</b>
 *
 * <p>Mines coal from the Skelli coal mine, banks in edge.<br>
 * Brings food, banks if out of food. <br>
 * Start in Edge bank with Armor and Pickaxe or near skilli mine. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_SkelliCoal extends K_kailaScript {
  private static String isMining = "none";
  private static final Integer[] currentOre = {0, 0};
  private static final int[] coalIDs = {110, 111};

  private static boolean coalAvailable() {
    return c.getNearestObjectByIds(coalIDs) != null;
  }

  private static boolean rockEmpty() {
    if (currentOre[0] != 0) {
      return c.getObjectAtCoord(currentOre[0], currentOre[1]) == 98;
    } else {
      return true;
    }
  }

  private void startSequence() {
    c.displayMessage("@red@Skeleton Coal Miner- By Kaila");
    c.displayMessage("@red@Start in Edge bank with Armor and pickaxe");
    if (c.isInBank()) c.closeBank();
    if (c.currentY() > 400) {
      bank();
      eat();
      bankToSkeli();
      eat();
      c.sleep(1380);
    }
  }
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (parameters.length > 0 && !parameters[0].isEmpty()) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.displayMessage("Auto-starting, Mining Skelli Coal", 0);
        System.out.println("Auto-starting, Mining Skelli Coal");
        startSequence();
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
      startTime = System.currentTimeMillis();
      startSequence();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount() == 30) { // c.getInventoryItemCount(546) == 0 ||
        goToBank();
      } else {
        //	eat();
        leaveCombat();
        if (rockEmpty() || !c.isBatching()) {
          isMining = "none";
          currentOre[0] = 0;
          currentOre[1] = 0;
          c.sleep(640);
        } else {
          c.waitForBatching(true);
        }
        c.setStatus("@yel@Mining..");
        if (!c.isBatching() && Objects.equals(isMining, "none") && rockEmpty()) {
          if (coalAvailable()) {
            int[] oreCoords = c.getNearestObjectByIds(coalIDs);
            if (oreCoords != null) {
              isMining = "coal";
              c.atObject(oreCoords[0], oreCoords[1]);
              currentOre[0] = oreCoords[0];
              currentOre[1] = oreCoords[1];
            }
          }
          c.sleep(1280);
        } else c.waitForBatching(true);
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
      totalCoal = totalCoal + c.getInventoryItemCount(155);
      totalSap = totalSap + c.getInventoryItemCount(160);
      totalEme = totalEme + c.getInventoryItemCount(159);
      totalRub = totalRub + c.getInventoryItemCount(158);
      totalDia = totalDia + c.getInventoryItemCount(157);

      if (c.getInventoryItemCount() > 0) {
        for (int itemId : c.getInventoryItemIds()) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
        c.sleep(1280); // increased sleep here to prevent double banking
      }
      if (c.getInventoryItemCount(546) < 1) { // withdraw 20 shark
        c.withdrawItem(546, 1);
        c.sleep(340);
      }
      coalInBank = c.getBankItemCount(155);

      c.closeBank();
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
        c.setStatus("@red@We've ran out of Food! Teleporting Away!.");
        SkeliToBank();
        c.sleep(100);
        c.walkTo(120, 644);
        c.atObject(119, 642);
        c.walkTo(217, 447);
        c.sleep(308);
        c.setAutoLogin(false);
        c.logout();
        c.sleep(1000);
        if (!c.isLoggedIn()) {
          c.stop();
          c.logout();
        }
      }
    }
  }

  private void goToBank() {
    isMining = "none";
    currentOre[0] = 0;
    currentOre[1] = 0;
    c.setStatus("@yel@Banking..");
    SkeliToBank();
    bank();
    bankToSkeli();
    c.sleep(618);
  }

  private void SkeliToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(269, 380);
    c.walkTo(265, 384);
    c.walkTo(259, 385);
    c.walkTo(249, 395);
    c.walkTo(247, 399);
    c.walkTo(234, 412);
    c.walkTo(224, 423);
    c.walkTo(220, 427);
    c.walkTo(220, 441);
    c.walkTo(220, 445);
    c.walkTo(217, 448);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking to Bank...");
  }

  private void bankToSkeli() {
    c.setStatus("@gre@Walking to Skelli " + "Mine..");
    c.walkTo(217, 448);
    c.walkTo(220, 445);
    c.walkTo(220, 441);
    c.walkTo(220, 427);
    c.walkTo(224, 423);
    c.walkTo(234, 412);
    c.walkTo(247, 399);
    c.walkTo(249, 395);
    c.walkTo(259, 385);
    c.walkTo(265, 384);
    c.walkTo(269, 380);
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Skeleton Coal Miner ~ By Kaila");
    JLabel label1 = new JLabel("Start in Edge bank with Armor and Pickaxe");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });
    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
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
      int coalSuccessPerHr = 0;
      int sapSuccessPerHr = 0;
      int emeSuccessPerHr = 0;
      int rubSuccessPerHr = 0;
      int diaSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        coalSuccessPerHr = (int) (totalCoal * scale);
        sapSuccessPerHr = (int) (totalSap * scale);
        emeSuccessPerHr = (int) (totalEme * scale);
        rubSuccessPerHr = (int) (totalRub * scale);
        diaSuccessPerHr = (int) (totalDia * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Skeli Miner  @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Coal Mined: @gre@"
              + totalCoal
              + "@yel@ (@whi@"
              + String.format("%,d", coalSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Coal in Bank: @gre@"
              + coalInBank,
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Sapphires: @gre@"
              + totalSap
              + "@yel@ (@whi@"
              + String.format("%,d", sapSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Emeralds: @gre@"
              + totalEme
              + "@yel@ (@whi@"
              + String.format("%,d", emeSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Rubys: @gre@"
              + totalRub
              + "@yel@ (@whi@"
              + String.format("%,d", rubSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Diamonds: @gre@"
              + totalDia
              + "@yel@ (@whi@"
              + String.format("%,d", diaSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 5), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
    }
  }
}
