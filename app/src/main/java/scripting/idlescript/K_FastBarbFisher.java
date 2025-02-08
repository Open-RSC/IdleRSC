package scripting.idlescript;

import bot.Main;
import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;

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
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.FISHING,
            Category.IRONMAN_SUPPORTED,
            Category.ULTIMATE_IRONMAN_SUPPORTED,
            Category.URANIUM_SUPPORTED
          },
          "Kaila",
          "Power fishes trout/salmon in Barbarian Village.");

  private static int troutSuccess = 0;
  private static int salmonSuccess = 0;
  private static int failure = 0;
  private static int invFeathers = 0;
  private long didActionTime = 0;
  private boolean doAction = true;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (!parameters[0].isEmpty()) {
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
      c.displayMessage("@gre@Power fishes trout/salmon in barb village using Batching");

      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      doAction = true;
      didActionTime = System.currentTimeMillis();

      if (!c.isAuthentic()) c.setBatchBarsOn();
      if (c.isInBank()) c.closeBank();
      if (c.currentX() < 195) {
        bank();
        c.walkTo(151, 510);
        pathWalker(213, 507);
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      // check for enough feathers
      if (c.getInventoryItemCount(381) == 0 || c.getInventoryItemCount(378) == 0) {
        // add 1 spot there
        c.setStatus("@yel@Banking to get feathers or rod");
        pathWalker(151, 507);
        bank();
        c.setStatus("@yel@Walking back to fish spot");
        c.walkTo(151, 510);
        pathWalker(213, 507);
      }
      // time to fish
      if (c.isRunning()
          && (c.getBatchBarsOn()
              || doAction
              || (didActionTime + 4000L < System.currentTimeMillis()))) {

        if (c.getNearestObjectById(192) == null) { // something went wrong
          c.setStatus("@yel@Could not find fish, waiting");
          c.sleep(1000);
          continue;
        }
        int[] spot = c.getNearestObjectById(192);
        if (c.currentX() != spot[0] + 1 || c.currentY() != spot[1]) {
          c.setStatus("@yel@Walking to fish spot");
          c.walkTo(spot[0] + 1, spot[1]);
        }
        c.setStatus("@cya@Fishing");
        c.atObject(spot[0], spot[1]);
        didActionTime = System.currentTimeMillis();
        doAction = false;
        c.waitForBatching(false);
      }
      c.sleep(640); // sleep once each loop
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      int featherId = ItemId.FEATHER.getId();
      int rodId = ItemId.FLY_FISHING_ROD.getId();
      int bagId = ItemId.SLEEPING_BAG.getId();

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != featherId && itemId != rodId && itemId != bagId) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
          c.sleep(100);
        }
      }
      c.sleep(1280); // Important, leave in
      withdrawItem(rodId, 1);
      withdrawItem(featherId, 1_000_000);

      c.sleep(1200);
      if (!inventoryItemCheck(rodId, 1)
          || !inventoryItemCheck(featherId, 1000)
          || (c.isAuthentic() && !inventoryItemCheck(bagId, 1))) { // no feathers or fly rod
        c.setStatus("@red@Out of Feathers/Fly Fishing Rod/Sleeping Bag, Stopping!");
        c.log("@red@Out of Feathers/Fly Fishing Rod/Sleeping Bag, Stopping!");
        c.stop();
      }
      c.closeBank();
      c.setStatus("@yel@Done Banking");
    }
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
      doAction = true;
      invFeathers = c.getInventoryItemCount(381);
    } else if (message.contains("You catch a salmon")) {
      salmonSuccess++;
      doAction = true;
      invFeathers = c.getInventoryItemCount(381);
    } else if (message.contains("You fail")) {
      failure++;
      doAction = true;
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
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 6), 0xFFFFFF, 1);
      c.drawString("@whi@__________________", x, y + 3 + (14 * 6), 0xFFFFFF, 1);
    }
  }
}
