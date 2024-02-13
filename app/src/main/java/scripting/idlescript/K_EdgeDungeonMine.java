package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * <b>Edge Dungeon Mine</b>
 *
 * <p>Mines Addy/Mith/Coal in Hobgoblin Mine and banks in Edge! (some pk/death protection). <br>
 * This bot supports the "autostart" parameter to automatiically start the bot without gui.<br>
 * Start in Varrock East bank or near Mine, with a pickaxe and bass key.<br>
 * Sharks in bank REQUIRED.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_EdgeDungeonMine extends K_kailaScript {
  private static String isMining = "none";
  private static final int[] currentOre = {0, 0};
  private static final int[] addyIDs = {108, 231, 109};
  private static final int[] mithIDs = {106, 107};
  private static final int[] coalIDs = {110, 111};

  private boolean adamantiteAvailable() {
    return c.getNearestObjectByIds(addyIDs) != null;
  }

  private boolean mithrilAvailable() {
    return c.getNearestObjectByIds(mithIDs) != null;
  }

  private boolean coalAvailable() {
    return c.getNearestObjectByIds(coalIDs) != null;
  }

  private boolean rockEmpty() {
    if (currentOre[0] != 0) {
      return c.getObjectAtCoord(currentOre[0], currentOre[1]) == 98;
    } else {
      return true;
    }
  }

  private void startSequence() {
    c.displayMessage("@red@Edge Dungeon Miner- By Kaila");
    c.displayMessage("@red@Start in Edge bank with Armor and pickaxe");
    c.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
    c.displayMessage("@red@31 Magic Required for escape tele");

    if (c.isInBank()) c.closeBank();
    if (c.currentY() < 3000) {
      bank();
      bankToDungeon();
      c.sleep(1380);
    }
    c.setBatchBarsOn();
  }
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
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
      if (c.getInventoryItemCount() == 30) {
        goToBank();
      }
      if (c.getInventoryItemCount() < 30) {
        if (rockEmpty() || !c.isBatching()) {
          isMining = "none";
          currentOre[0] = 0;
          currentOre[1] = 0;
        }
        if (c.isBatching() && !c.getNeedToMove()) {
          if (Objects.equals(isMining, "mithril")) {
            if (adamantiteAvailable()) {
              mine("adamantite");
            }
          }
          if (Objects.equals(isMining, "coal")) {
            if (adamantiteAvailable()) {
              mine("adamantite");
            } else if (mithrilAvailable()) {
              mine("mithril");
            }
          }
          c.sleep(1280);
        }
        if (!c.isBatching() && Objects.equals(isMining, "none") && rockEmpty()) {
          if (adamantiteAvailable()) {
            mine("adamantite");
          } else if (mithrilAvailable()) {
            mine("mithril");
          } else if (coalAvailable()) {
            mine("coal");
          }
          c.sleep(1280);
        }
      }
    }
  }

  private void mine(String i) {
    if (Objects.equals(i, "adamantite")) {
      int[] oreCoords = c.getNearestObjectByIds(addyIDs);
      if (oreCoords != null) {
        isMining = "adamantite";
        c.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    } else if (Objects.equals(i, "mithril")) {
      int[] oreCoords = c.getNearestObjectByIds(mithIDs);
      if (oreCoords != null) {
        isMining = "mithril";
        c.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    } else if (Objects.equals(i, "coal")) {
      int[] oreCoords = c.getNearestObjectByIds(coalIDs);
      if (oreCoords != null) {
        isMining = "coal";
        c.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    }
    c.sleep(1920);
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalCoal = totalCoal + c.getInventoryItemCount(155);
      totalMith = totalMith + c.getInventoryItemCount(153);
      totalAddy = totalAddy + c.getInventoryItemCount(154);
      totalSap = totalSap + c.getInventoryItemCount(160);
      totalEme = totalEme + c.getInventoryItemCount(159);
      totalRub = totalRub + c.getInventoryItemCount(158);
      totalDia = totalDia + c.getInventoryItemCount(157);

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != 99 && itemId != 1262) { // won't bank brass key or sleeping bags
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1240);
      if (c.getInventoryItemCount(99) < 1) { // withdraw brass key
        c.withdrawItem(99, 1);
        c.sleep(640);
      }
      coalInBank = c.getBankItemCount(155);
      mithInBank = c.getBankItemCount(153);
      addyInBank = c.getBankItemCount(154);
      c.closeBank();
    }
    brassKeyCheck();
  }

  private void goToBank() {
    isMining = "none";
    currentOre[0] = 0;
    currentOre[1] = 0;
    c.setStatus("@yel@Banking..");
    dungeonToBank();
    bank();
    bankToDungeon();
    c.sleep(618);
  }

  private void bankToDungeon() {
    c.setStatus("@gre@Walking to Edge Dungeon..");
    c.walkTo(151, 507);
    c.walkTo(162, 507);
    c.walkTo(172, 507);
    c.walkTo(182, 507);
    c.walkTo(192, 497);
    c.walkTo(202, 487);
    c.walkTo(202, 485);
    brassKeyCheck();
    c.setStatus("@gre@Crossing Dusty Gate..");
    brassDoorSouthToNorth();
    c.setStatus("@gre@Walking to Edge Dungeon..");
    c.walkTo(203, 483);
    c.atObject(203, 482);
    c.sleep(2000);
    c.walkTo(207, 3315);
    c.walkTo(207, 3300);
    c.walkTo(205, 3299);
    c.walkTo(193, 3299);
    c.setStatus("@gre@Done Walking..");
  }

  private void dungeonToBank() {
    c.setStatus("@gre@Walking to Varrock West..");
    c.walkTo(207, 3315);
    c.walkTo(203, 3315);
    c.atObject(203, 3314);
    c.sleep(2000);
    c.walkTo(202, 484);
    brassKeyCheck();
    c.setStatus("@gre@Crossing Dusty Gate..");
    brassDoorNorthToSouth();
    c.setStatus("@gre@Walking to Varrock West..");
    c.walkTo(202, 487);
    c.walkTo(192, 497);
    c.walkTo(182, 507);
    c.walkTo(172, 507);
    c.walkTo(162, 507);
    c.walkTo(151, 507);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Edge Dungeon Miner - By Kaila");
    JLabel label1 = new JLabel("Start in Varrock West with Pickaxe and Brass key");
    JLabel label2 = new JLabel("This bot supports the \"autostart\" parameter");
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
      int coalSuccessPerHr = 0;
      int mithSuccessPerHr = 0;
      int addySuccessPerHr = 0;
      int sapSuccessPerHr = 0;
      int emeSuccessPerHr = 0;
      int rubSuccessPerHr = 0;
      int diaSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        coalSuccessPerHr = (int) (totalCoal * scale);
        mithSuccessPerHr = (int) (totalMith * scale);
        addySuccessPerHr = (int) (totalAddy * scale);
        sapSuccessPerHr = (int) (totalSap * scale);
        emeSuccessPerHr = (int) (totalEme * scale);
        rubSuccessPerHr = (int) (totalRub * scale);
        diaSuccessPerHr = (int) (totalDia * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) { // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Edge Dungeon Miner @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
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
          "@whi@Mith Mined: @gre@"
              + totalMith
              + "@yel@ (@whi@"
              + String.format("%,d", mithSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Mith in Bank: @gre@"
              + mithInBank,
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Addy Mined: @gre@"
              + totalAddy
              + "@yel@ (@whi@"
              + String.format("%,d", addySuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Addy in Bank: @gre@"
              + addyInBank,
          x,
          y + (14 * 3),
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
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 4),
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
          y + (14 * 5),
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
          y + (14 * 6),
          0xFFFFFF,
          1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 6), 0xFFFFFF, 1);
    }
  }
}
