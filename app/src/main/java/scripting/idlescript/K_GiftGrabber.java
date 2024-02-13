package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import java.util.Calendar;
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
/*
input current minute on starting script. (for banking)
bank between waves
random offset to not focus on the SAME ones
bots walk to the same ones
bot gets stuck too far north in draynor (add exception)


 */

public final class K_GiftGrabber extends K_kailaScript {
  private final int[] pos = {
    211, 611, 610, 653, 185, 502, 88, 549, 444, 477
  }; // remove pos 0 and pos 1
  private int totalCrackers = 0;
  private int CrackersInBank = 0;
  private int location = 0; // 0 is draynor
  private int waveTime = 0;
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
      //      Random rand = new Random();
      //      int offsetX = rand.nextInt(9) - 4; // offset by +/- 4
      //      int offsetY = rand.nextInt(9) - 4;
      //      for (int i = 0; i < pos.length / 2; i = i + 2) {
      //        if ((i + 1) < pos.length) { // null safer
      //          pos[i] = pos[i] + offsetX;
      //          pos[i + 1] = pos[i + 1] + offsetY;
      //        }
      //      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount() == 30) goToBank();
      int[] coords = c.getNearestItemById(H_CRACKER); // always pick up tops
      if (coords != null) {
        c.setStatus("@yel@Looting..");
        c.pickupItem(coords[0], coords[1], H_CRACKER, true, true);
        c.sleep(2 * GAME_TICK);
      } else { // change to a for:each loop or something.

        int timeInMins = Calendar.getInstance().get(Calendar.MINUTE);
        // c.log(String.valueOf(timeInMins));
        if (c.getInventoryItemCount(H_CRACKER) > 0
            && (timeInMins > waveTime + 10 || timeInMins < waveTime - 10)) {

          goToBank();
        }
        // check if > 40 tiles away for all?
        if (location == 0 && c.currentX() != pos[2] && c.currentY() != pos[3]) {
          if (c.currentY() < 610) { // walk south if too far north
            if (c.currentX() < 180) { // northeast near cow field
              c.walkTo(179, 593);
              c.walkTo(193, 593);
            }
            c.walkTo(211, 611); // if near mansion
          } else if (c.currentX() < 177 && c.currentY() > 668) { // in dray swamp
            c.walkTo(178, 667);
            // north of swamp but far east
          } else if (c.currentX() < 177 && c.currentY() < 668 && c.currentY() > 610) {
            if (c.currentX() < 145) { // near castle, walk west
              c.walkTo(157, 650);
            }
            c.walkTo(178, 647);
          }
          c.walkTo(pos[2], pos[3]);
          c.sleep(GAME_TICK);
        } else if (location == 1 && c.currentX() != pos[4] && c.currentY() != pos[5]) { // var west
          if (c.currentX()
              > 207) { // west of spot in barb village (seems to be the only stuck spot)
            c.walkTo(207, 512);
          }
          c.walkTo(pos[4], pos[5]);
          c.sleep(GAME_TICK);
        } else if (location == 2 && c.currentX() != pos[6] && c.currentY() != pos[7]) {
          c.walkTo(pos[6], pos[7]);
          c.sleep(GAME_TICK);
        } else if (location == 3 && c.currentX() != pos[8] && c.currentY() != pos[9]) {
          c.walkTo(pos[8], pos[9]);
          c.sleep(GAME_TICK);
        } else {
          c.sleep(5 * GAME_TICK);
        }
      }
    }
  }

  private void goToBank() {
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
    JLabel waveMinuteLabel = new JLabel("What minute of the hour are waves?");
    JTextField waveMinuteField = new JTextField(String.valueOf(30));
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!waveMinuteField.getText().isEmpty()) {
            waveTime = Integer.parseInt(waveMinuteField.getText());
          } else {
            waveTime = 30;
          }
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
    scriptFrame.add(waveMinuteLabel);
    scriptFrame.add(waveMinuteField);
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
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 4), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 4), 0xFFFFFF, 1);
    }
  }
}
