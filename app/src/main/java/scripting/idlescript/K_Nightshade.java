package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * <b>Nightshade Picker</b>
 *
 * <p>Grabs Nightshades in Gu'Tanoth (for entrance to ogre enclave). <br>
 * Picks Nightshade in northern skavid cave. Start in Yanille bank or northern nightshade cave.<br>
 * Requires Lit Candle and Skavid Map in Invent.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_Nightshade extends K_kailaScript {
  private static int totalShade = 0;
  private static int shadeInBank = 0;
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
      c.displayMessage("@red@Nightshade Picker - By Kaila");
      c.displayMessage("@red@Start in Yanille Bank");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 800 && c.currentY() > 740 && c.currentX() < 591) {
        bank();
        BankToGrape();
        c.sleep(1380);
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
      int[] coords = c.getNearestItemById(1086); // always pick up tops
      if (coords != null) {
        c.setStatus("@yel@Looting..");
        c.walkTo(650, 3559);
        c.sleep(340);
        c.walkTo(650, 3560);
        c.pickupItem(coords[0], coords[1], 1086, true, true);
        c.sleep(340);
      } else {
        c.sleep(4000);
      }
      c.sleep(100);
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalShade = totalShade + c.getInventoryItemCount(1086);
      if (c.getInventoryItemCount(1086) > 0) { // nightshade
        c.depositItem(1086, c.getInventoryItemCount(1086));
        c.sleep(1380);
      }
      if (c.getInventoryItemCount(601) < 1) { // lit candle
        c.withdrawItem(601, 1);
        c.sleep(1380);
      }
      if (c.getInventoryItemCount(1045) < 1) { // skavid map
        c.withdrawItem(1045, 1);
        c.sleep(1380);
      }
      shadeInBank = c.getBankItemCount(1086);
      c.closeBank();
    }
  }

  private void GrapeToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(649, 3555);
    while (c.currentX() == 649 && c.currentY() == 3555) {
      c.atWallObject(649, 3554); // gate won't break if someone else opens it
      c.sleep(640);
    }
    // c.atObject(649,3554);
    c.sleep(340);
    c.walkTo(647, 767);
    c.walkTo(647, 754);
    c.walkTo(642, 754);
    c.walkTo(628, 754);
    c.walkTo(626, 755);
    c.walkTo(615, 755);
    c.walkTo(608, 749);
    c.walkTo(598, 749);
    c.walkTo(584, 749);
    c.walkTo(584, 752);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToGrape() {
    c.setStatus("@gre@Walking to Nightshade..");
    c.walkTo(584, 752);
    c.walkTo(584, 749);
    c.walkTo(598, 749);
    c.walkTo(608, 749);
    c.walkTo(615, 755);
    c.walkTo(626, 755);
    c.walkTo(628, 754);
    c.walkTo(642, 754);
    // west yanille GATE
    c.walkTo(647, 754);
    c.walkTo(647, 767);
    c.walkTo(649, 769);
    c.sleep(340);
    c.atObject(649, 770);
    c.walkTo(650, 3560);
    c.sleep(340);
    // ontop of nightshade now
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Nightshade Picker - By Kaila");
    JLabel label1 = new JLabel("Picks Nightshade in northern skavid cave");
    JLabel label2 = new JLabel("Start in Yanille bank or northern nightshade cave");
    JLabel label3 = new JLabel("Requires Lit Candle and Skavid Map in Invent");
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
      int ShadeSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        ShadeSuccessPerHr = (int) (totalShade * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Nightshade Picker @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Nightshade Banked: @gre@" + shadeInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Nightshade Picked: @gre@"
              + totalShade
              + "@yel@ (@whi@"
              + String.format("%,d", ShadeSuccessPerHr)
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
