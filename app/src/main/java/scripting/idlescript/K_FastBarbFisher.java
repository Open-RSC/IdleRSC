package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * <b>Fast Barb Fisher</b>
 *
 * <p>Power fishes trout/salmon in barb village using Batching. <br>
 * Batch bars MUST be toggles on to function properly. Bot will Autotoggle them On. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila.
 */
public final class K_FastBarbFisher extends K_kailaScript {
  private static int troutSuccess = 0;
  private static int salmonSuccess = 0;
  private static int failure = 0;
  private static int invFeathers = 0;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (!parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.log("Got Autostart, Fishing", "@red@");
        scriptStarted = true;
        guiSetup = true;
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
      c.displayMessage("@red@Power fishes trout/salmon in barb village using Batching");

      c.quitIfAuthentic();
      c.setBatchBarsOn();
      if (c.isInBank()) c.closeBank();
      if (c.currentX() < 195) {
        bank();
        bankToFish();
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      int[] spot = c.getNearestObjectById(192);
      c.walkTo(spot[0] + 1, spot[1]);
      c.sleep(1240);
      c.atObject(spot[0], spot[1]);
      c.sleep(2000);
      c.waitForBatching(true);
      if (c.getInventoryItemCount(381) == 0 || c.getInventoryItemCount(378) == 0) {
        fishToBank();
        bank();
        bankToFish();
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
      int featherId = 381;
      int rodId = 378;
      for (int itemId : c.getInventoryItemIds()) {
        c.depositItem(itemId, c.getInventoryItemCount(itemId));
        c.sleep(100);
      }
      c.sleep(1280); // Important, leave in
      if (c.getInventoryItemCount(rodId) < 1) { // withdraw 1 fly fish rod if needed
        c.withdrawItem(rodId, 1);
        c.sleep(640);
      }
      if (c.getInventoryItemCount(featherId) < 1000000) { // withdraw 1m feathers if needed
        if (c.getBankItemCount(featherId) > 1000000) {
          c.withdrawItem(featherId, 1000000);
          c.sleep(640);
        } else if (c.getBankItemCount(featherId) < 1000000) {
          c.withdrawItem(featherId, c.getBankItemCount(featherId) - 1);
          c.sleep(640);
        }
      }
      if (c.getBankItemCount(featherId) == 0
          || c.getBankItemCount(rodId) == 0) { // no feathers or fly rod
        c.setStatus("@red@NO Feathers/Fly Fishing Rod in the bank, Logging Out!.");
        c.log("@red@NO Feathers/Fly Fishing Rod in the bank, Logging Out!.");
        endSession();
      }
      c.closeBank();
    }
  }

  private void bankToFish() {
    c.walkTo(151, 507);
    c.walkTo(161, 507);
    c.walkTo(171, 507);
    c.walkTo(179, 515);
    c.walkTo(189, 515);
    c.walkTo(199, 515);
    c.walkTo(202, 512);
    c.walkTo(212, 512);
    c.walkTo(216, 512);
    c.walkTo(216, 510);
    c.walkTo(213, 507);
  }

  private void fishToBank() {
    c.walkTo(216, 510);
    c.walkTo(216, 512);
    c.walkTo(212, 512);
    c.walkTo(202, 512);
    c.walkTo(199, 515);
    c.walkTo(189, 515);
    c.walkTo(179, 515);
    c.walkTo(171, 507);
    c.walkTo(161, 507);
    c.walkTo(151, 507);
  }

  private void setupGUI() {
    JLabel header = new JLabel("Fast Barb Fisher - Kaila");
    JLabel label5 = new JLabel("* REQUIRES Batch Bars, bot will autotoggle them!");
    JLabel label1 = new JLabel("* Start near Barb Fish spots or Var West!");
    JLabel label2 = new JLabel("* Have Feathers and Fly Fishing Rod");
    JLabel label3 = new JLabel("* Uses Batching Bars to get better xp Rates");
    JLabel label4 = new JLabel("* Only works on Coleslaw currently!");
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
    scriptFrame.add(label5);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label4);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You catch a trout")) {
      troutSuccess++;
      invFeathers = c.getInventoryItemCount(381);
    } else if (message.contains("You catch a salmon")) {
      salmonSuccess++;
      invFeathers = c.getInventoryItemCount(381);
    } else if (message.contains("You fail")) {
      failure++;
      invFeathers = c.getInventoryItemCount(381);
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int troutSuccessPerHr = 0;
      int salmonSuccessPerHr = 0;
      int failurePerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        troutSuccessPerHr = (int) (troutSuccess * scale);
        salmonSuccessPerHr = (int) (salmonSuccess * scale);
        failurePerHr = (int) (failure * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      int totalFish = troutSuccess + salmonSuccess;
      c.drawString("@red@Fast Barb Fisher @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Trout Caught: @gre@"
              + troutSuccess
              + "@yel@ (@whi@"
              + String.format("%,d", troutSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Salmon Caught: @gre@"
              + salmonSuccess
              + "@yel@ (@whi@"
              + salmonSuccessPerHr
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Failure to Catch: @gre@"
              + failure
              + "@yel@ (@whi@"
              + failurePerHr
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Feathers Used: @gre@"
              + totalFish
              + "@yel@ (@whi@"
              + String.format("%,d", (troutSuccessPerHr + salmonSuccessPerHr))
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Time Remaining: "
              + c.shortTimeToCompletion(totalFish, invFeathers, startTime)
              + " hours",
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 7), 0xFFFFFF, 1);
      c.drawString("@whi@__________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
    }
  }
}
