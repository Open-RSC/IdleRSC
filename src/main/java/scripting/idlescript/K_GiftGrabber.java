package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.*;

/**
 * <b>Monk Robe Picker</b>
 *
 * <p>Grabs Monks Robes from edge monestary <br>
 * Picks up Monks Robe SETS in Edge Monastery and Banks (equal amount of tops and bottoms) <br>
 * Start in Edge Bank or near Robes Recommend Armor against lvl 21 Scorpions <br>
 * Please Gain Permission to enter Prayer guild FIRST <br>
 * Bot will loot Equal Amounts of robe tops and bottoms<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_GiftGrabber extends K_kailaScript {
  private static int totalCrackers = 0;
  private static int CrackersInBank = 0;
  private int location = 0; // 0 is draynor
  private final int H_CRACKER = 1330;
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
      c.displayMessage("@red@Cracker Picker - By Kaila");
      if (c.isInBank()) c.closeBank();
      //      if (c.currentY() < 1000 && c.currentX() < 245) {
      //        bank();
      //        bankToDray();
      //        c.sleep(1380);
      //      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getInventoryItemCount() == 30) {
        if (location == 0) { // dray
          c.setStatus("@red@Banking..");
          drayToBank();
          bank();
          bankToDray();
        } else if (location == 1) { // var west
          c.setStatus("@red@Banking..");
          varWestToBank();
          bank();
          bankToVarWest();
        } else if (location == 2) { // var east
          c.setStatus("@red@Banking..");
          varEastToBank();
          bank();
          bankToVarEast();
        } else if (location == 3) { // cath
          c.setStatus("@red@Banking..");
          cathToBank();
          bank();
          bankToCath();
        }
      }
      int[] coords = c.getNearestItemById(H_CRACKER); // always pick up tops
      if (coords != null) {
        c.setStatus("@yel@Looting..");
        c.pickupItem(coords[0], coords[1], H_CRACKER, true, true);
        c.sleep(2 * GAME_TICK);
      } else if (location == 0 && c.currentX() != 210 && c.currentY() != 653) {
        c.walkTo(210, 653);
        c.sleep(GAME_TICK);
      } else if (location == 1 && c.currentX() != 185 && c.currentY() != 502) {
        c.walkTo(185, 502);
        c.sleep(GAME_TICK);
      } else if (location == 2 && c.currentX() != 88 && c.currentY() != 549) {
        c.walkTo(88, 549);
        c.sleep(GAME_TICK);
      } else if (location == 3 && c.currentX() != 444 && c.currentY() != 477) {
        c.walkTo(444, 477);
        c.sleep(GAME_TICK);
      } else {
        c.sleep(5 * GAME_TICK);
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
      totalCrackers = totalCrackers + c.getInventoryItemCount(H_CRACKER);
      if (c.getInventoryItemCount(H_CRACKER) > 0) {
        c.depositItem(H_CRACKER, c.getInventoryItemCount(H_CRACKER));
        c.sleep(1380);
      }
      CrackersInBank = c.getBankItemCount(H_CRACKER);
      c.closeBank();
    }
  }

  private void drayToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(214, 644);
    c.walkTo(215, 633);
    c.walkTo(220, 633);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void bankToDray() {
    c.setStatus("@gre@Walking to Robes..");
    c.walkTo(220, 633);
    c.walkTo(215, 633);
    c.walkTo(214, 644);
    // next to robes now)
    c.setStatus("@gre@Done Walking..");
  }

  private void varWestToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(185, 502);
    c.walkTo(181, 507);
    c.walkTo(171, 507);
    c.walkTo(161, 507);
    c.walkTo(151, 507);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void bankToVarWest() {
    c.setStatus("@gre@Walking to Robes..");
    c.walkTo(151, 507);
    c.walkTo(161, 507);
    c.walkTo(171, 507);
    c.walkTo(181, 507);
    c.walkTo(185, 502);
    // next to robes now)
    c.setStatus("@gre@Done Walking..");
  }

  private void varEastToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(88, 549);
    c.walkTo(85, 543);
    c.walkTo(74, 532);
    c.walkTo(79, 524);
    c.walkTo(83, 510);
    c.walkTo(92, 509);
    c.walkTo(102, 509);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void bankToVarEast() {
    c.setStatus("@gre@Walking to Robes..");
    c.walkTo(102, 509);
    c.walkTo(92, 509);
    c.walkTo(83, 510);
    c.walkTo(79, 524);
    c.walkTo(74, 532);
    c.walkTo(85, 543);
    c.walkTo(88, 549);
    // next to robes now)
    c.setStatus("@gre@Done Walking..");
  }

  private void cathToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(444, 477);
    c.walkTo(444, 487);
    c.walkTo(444, 497);
    c.walkTo(440, 497);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void bankToCath() {
    c.setStatus("@gre@Walking to Robes..");
    c.walkTo(440, 497);
    c.walkTo(444, 497);
    c.walkTo(444, 487);
    c.walkTo(444, 477);
    // next to robes now)
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Cracker Grabber - By Kaila");
    JLabel label1 = new JLabel("Start at the associated bank or near loot spot");
    JComboBox<String> locationField =
        new JComboBox<>(new String[] {"Draynor", "Varrock West", "Varrock East", "Catherby"});
    locationField.setSelectedIndex(0); // sets default to controlled
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          location = locationField.getSelectedIndex();
          startTime = System.currentTimeMillis();
          scriptStarted = true;
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(locationField);
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
      int TopzSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        TopzSuccessPerHr = (int) (totalCrackers * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Gift grabber @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Crackers Banked: @gre@" + CrackersInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Crackers Picked: @gre@"
              + totalCrackers
              + "@yel@ (@whi@"
              + String.format("%,d", TopzSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
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
