package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
public final class K_MonkRobes extends K_kailaScript {
  private static int totalTopz = 0;
  private static int totalBotz = 0;
  private static int TopzInBank = 0;
  private static int BotzInBank = 0;
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
      c.displayMessage("@red@Monks Robes Picker - By Kaila");
      c.displayMessage("@red@Start in Edge Bank or upstairs Monestary");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 1000 && c.currentX() < 245) {
        bank();
        BankToGrape();
        c.sleep(1380);
      }
      if (c.currentY() < 1000 && c.currentX() > 245) {
        c.atObject(251, 468);
        c.sleep(340);
        c.walkTo(260, 1411);
        c.walkTo(260, 1411);
        c.walkTo(260, 1405);
        c.walkTo(264, 1403);
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount() == 30) {
        c.setStatus("@red@Banking..");
        GrapeToBank();
        bank();
        BankToGrape();
        c.sleep(618);
      }
      int[] coords = c.getNearestItemById(388); // always pick up tops
      if (coords != null) {
        c.setStatus("@yel@Looting..");
        c.pickupItem(coords[0], coords[1], 388, true, true);
        c.sleep(618);
      }
      if (c.getInventoryItemCount(389) < c.getInventoryItemCount(388)) {
        int[] coords2 = c.getNearestItemById(389);
        if (coords2 != null) { // pick up bottoms if you have more tops than bottoms!
          c.setStatus("@yel@Looting..");
          c.pickupItem(coords2[0], coords2[1], 389, true, true);
          c.sleep(618);
        }
        c.sleep(100);
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
      totalTopz = totalTopz + c.getInventoryItemCount(388);
      totalBotz = totalBotz + c.getInventoryItemCount(389);
      if (c.getInventoryItemCount(388) > 0) { // robe top
        c.depositItem(388, c.getInventoryItemCount(388));
        c.sleep(1380);
      }
      if (c.getInventoryItemCount(389) > 0) { // robe bot
        c.depositItem(389, c.getInventoryItemCount(389));
        c.sleep(1380);
      }
      TopzInBank = c.getBankItemCount(388);
      BotzInBank = c.getBankItemCount(389);
      c.closeBank();
    }
  }

  private void GrapeToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(260, 1405);
    c.walkTo(260, 1411);
    c.walkTo(260, 1411);
    c.walkTo(251, 1411);
    c.atObject(251, 1412);
    c.walkTo(252, 464);
    c.walkTo(254, 463);
    // next to grapes now
    c.walkTo(254, 454);
    c.walkTo(256, 451);
    c.walkTo(255, 444);
    c.walkTo(255, 433);
    c.walkTo(255, 422);
    c.walkTo(258, 422);
    c.walkTo(258, 415);
    c.walkTo(252, 421);
    c.walkTo(242, 432);
    c.walkTo(225, 432);
    c.walkTo(220, 437);
    c.walkTo(220, 445);
    c.walkTo(218, 447);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToGrape() {
    c.setStatus("@gre@Walking to Robes..");
    c.walkTo(218, 447);
    c.walkTo(220, 445);
    c.walkTo(220, 437);
    c.walkTo(225, 432);
    c.walkTo(242, 432);
    c.walkTo(252, 421);
    c.walkTo(258, 415);
    c.walkTo(258, 422);
    c.walkTo(255, 422);
    c.walkTo(255, 433);
    c.walkTo(255, 444);
    c.walkTo(256, 451);
    c.walkTo(254, 454);
    // grape pathing ends here
    c.walkTo(254, 464);
    c.walkTo(251, 464);
    c.walkTo(251, 467);
    c.sleep(340);
    while (c.currentX() == 251 && c.currentY() == 467) {
      c.atObject(251, 468);
      c.sleep(340);
    }
    c.walkTo(260, 1411);
    c.walkTo(260, 1411);
    c.walkTo(260, 1405);
    c.walkTo(264, 1403);
    // next to robes now)
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Monk Robe Picker - By Kaila");
    JLabel label1 = new JLabel("Picks up Monks Robe SETS in Edge Monastery and Banks");
    JLabel label2 = new JLabel("*Start in Edge Bank or near Robes!");
    JLabel label3 = new JLabel("*Recommend Armor against lvl 21 Scorpions");
    JLabel label4 = new JLabel("*Please Gain Permission to enter Prayer guild FIRST");
    JLabel label5 = new JLabel("*Bot will loot Equal Amounts of robe tops and bottoms");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
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
    scriptFrame.add(label4);
    scriptFrame.add(label5);
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
      int BotzSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        TopzSuccessPerHr = (int) (totalTopz * scale);
        BotzSuccessPerHr = (int) (totalTopz * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Monks Robe Picker @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Robe Tops Banked: @gre@" + TopzInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString("@whi@Robe Bots Banked:@gre@" + BotzInBank, x, y + (14 * 2), 0xFFFFFF, 1);
      c.drawString(
          "@whi@Robe Tops Picked: @gre@"
              + totalTopz
              + "@yel@ (@whi@"
              + String.format("%,d", TopzSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Robe Bots Picked: @gre@"
              + totalBotz
              + "@yel@ (@whi@"
              + String.format("%,d", BotzSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 6), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 6), 0xFFFFFF, 1);
    }
  }
}
