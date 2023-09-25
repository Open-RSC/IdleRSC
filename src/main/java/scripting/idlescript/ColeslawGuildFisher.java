package scripting.idlescript;

import java.awt.*;
import javax.swing.*;
import models.entities.ItemId;

/**
 * ColeslawGuildFisher by [unknown author]. Coleslaw only.
 *
 * @author Searos and Kaila
 */
public class ColeslawGuildFisher extends K_kailaScript {
  private static final int HARPOON_ID = ItemId.HARPOON.getId();
  private static final int LOBSTER_POT_ID = ItemId.LOBSTER_POT.getId();
  private static final int SHARK_FISH_SPOT = 261;
  private static final int LOBSTER_FISH_SPOT = 376;
  private static boolean swordFish = false;
  private static boolean bigNetFishing = false;
  private static boolean dropJunk = true;
  private static int equipId = HARPOON_ID;
  private static int spotId = SHARK_FISH_SPOT;
  private static int sharkSuccess = 0;
  private static int swordSuccess = 0;
  private static int lobsterSuccess = 0;
  private static int tunaSuccess = 0;
  private static int success = 0;
  private static int failure = 0;
  private static int fishInBank = 0;
  private static int fishIdOne = 0;
  private static int fishIdTwo = 0;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    c.toggleBatchBarsOn();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      c.displayMessage("@gre@Coleslaw Guild Fisher");
      c.displayMessage("@gre@Start in fishing guild with Lobster pot or Harpoon");
      c.displayMessage("@red@Recommend Batch bars be toggle on in settings to work correctly!");

      guiSetup = false;
      scriptStarted = false;
      if (c.isInBank()) c.closeBank();
      startTime = System.currentTimeMillis();
      scriptStart();
    }
    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("lobster")) {
        c.displayMessage("Got param " + parameters[0] + ". Fishing Lobsters!", 0);
        equipId = LOBSTER_POT_ID;
        spotId = LOBSTER_FISH_SPOT;
        fishIdOne = 372;
        guiSetup = true;
        scriptStarted = true;
      } else if (parameters[0].toLowerCase().startsWith("sword")) {
        c.displayMessage("Got param " + parameters[0] + ". Fishing Swordfish!", 0);
        spotId = LOBSTER_FISH_SPOT;
        fishIdOne = ItemId.RAW_TUNA.getId();
        fishIdTwo = ItemId.RAW_SWORDFISH.getId();
        swordFish = true;
        guiSetup = true;
        scriptStarted = true;
      } else if (parameters[0].toLowerCase().startsWith("big")) {
        c.displayMessage("Got param " + parameters[0] + ". Fishing Big Net!", 0);
        equipId = ItemId.BIG_NET.getId();
        bigNetFishing = true;
        guiSetup = true;
        scriptStarted = true;
      } else {
        c.displayMessage("Got unknown param. Fishing Sharks!", 0);
        fishIdOne = ItemId.RAW_SHARK.getId();
        guiSetup = true;
        scriptStarted = true;
      }
      scriptStart();
    }

    /*c.displayMessage("@cya@No parameters entered", 0);

       if (c.getInventoryItemCount(HARPOON_ID) >= 1) {
         c.displayMessage("Because you have a harpoon, I'm assuming you want sharks.", 0);
       } else if (c.getInventoryItemCount(LOBSTER_POT_ID) >= 1) {
         c.displayMessage(
             "Because you have a lobster pot, I'm assuming you want lobsters.", 0);
         equipId = LOBSTER_POT_ID;
         spotId = LOBSTER_FISH_SPOT;
       } else {
         c.displayMessage(
             "@red@Please grab either a harpoon or lobster pot, and use the parameter \"Lobster\" or \"Shark\".");
         c.stop();
         return 1000;
       }
       c.displayMessage(
           "If you want to override this, either put the parameter \"Lobster\" or \"Shark\"!", 0);
       c.sleep(5000);

       scriptStart();
    */
    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (c.isRunning()) {
      if (c.getInventoryItemCount() == 30) {
        handleFullInven();
      }

      if (c.getInventoryItemCount() < 30) {
        handleFishing();
      }

      c.sleep(600);
    }
  }

  private void dropJunk() {
    for (int itemId : c.getInventoryItemIds()) {
      if (itemId == 622) dropItemAmount(622, 30, true); // seaweed
      if (itemId == 793) dropItemAmount(793, 30, true); // oyster
      if (itemId == 16) dropItemAmount(16, 30, true); // gloves
      if (itemId == 17) dropItemAmount(17, 30, true); // boots
    }
  }

  private void handleFishing() {
    if (!c.isBatching()) {
      if (dropJunk && bigNetFishing) dropJunk();
      int[] fishingSpot = c.getNearestObjectById(spotId);
      try {
        if (spotId == LOBSTER_FISH_SPOT) {
          if (!swordFish) c.atObject(fishingSpot[0], fishingSpot[1]);
          else c.atObject2(fishingSpot[0], fishingSpot[1]);
        } else { // if spot Id is shark Id
          if (!bigNetFishing) c.atObject2(fishingSpot[0], fishingSpot[1]);
          else c.atObject(fishingSpot[0], fishingSpot[1]);
        }
        c.sleep(GAME_TICK);
      } catch (NullPointerException ignored) {
        // Spot disappeared!
      }
    }
  }

  private void handleFullInven() {
    if (c.isInBank()) {
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != 0 && itemId != equipId && c.getInventoryItemCount(itemId) > 0) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      if (fishIdOne != 0) {
        fishInBank = c.getBankItemCount(fishIdOne);
        if (fishIdTwo != 0) fishInBank = fishInBank + c.getBankItemCount(fishIdTwo);
      }
    } else {
      c.openBank();
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Coleslaw Guild Fisher");
    JLabel knifeLabel = new JLabel("Start with Lobster pot or Harpoon!");
    JLabel batchLabel = new JLabel("Can also use params to start");
    JLabel batchLabel2 = new JLabel("\"shark\", \"swordfish\", or \"lobster\"");
    JLabel fishLabel = new JLabel("Fish Type:");
    JComboBox<String> fishField =
        new JComboBox<>(new String[] {"Sharks", "Swordfish", "Lobster", "Big Net"});
    JCheckBox dropJunkCheckbox = new JCheckBox("Drop Junk", true);
    JButton startScriptButton = new JButton("Start");

    fishField.addActionListener(
        e -> {
          dropJunkCheckbox.setEnabled(fishField.getSelectedIndex() == 3);
        });
    startScriptButton.addActionListener(
        e -> {
          if (fishField.getSelectedIndex() == 1) {
            spotId = LOBSTER_FISH_SPOT;
            swordFish = true;
          } else if (fishField.getSelectedIndex() == 2) {
            equipId = LOBSTER_POT_ID;
            spotId = LOBSTER_FISH_SPOT;
          } else if (fishField.getSelectedIndex() == 3) {
            equipId = 548;
            bigNetFishing = true;
          }
          dropJunk = dropJunkCheckbox.isSelected();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(knifeLabel);
    scriptFrame.add(batchLabel);
    scriptFrame.add(batchLabel2);
    scriptFrame.add(fishLabel);
    scriptFrame.add(fishField);
    scriptFrame.add(dropJunkCheckbox);
    scriptFrame.add(startScriptButton);

    dropJunkCheckbox.setEnabled(false);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You catch a shark")) {
      sharkSuccess++;
    } else if (message.contains("You catch a swordfish")) {
      swordSuccess++;
    } else if (message.contains("You catch a tuna")) {
      tunaSuccess++;
    } else if (message.contains("You catch a lobster")) {
      lobsterSuccess++;
    } else if (message.contains("You catch a")) {
      success++;
    } else if (message.contains("You fail")) {
      failure++;
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int sharkSuccessPerHr = 0;
      int swordSuccessPerHr = 0;
      int lobsterSuccessPerHr = 0;
      int tunaSuccessPerHr = 0;
      int successPerHr = 0;
      int failurePerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        sharkSuccessPerHr = (int) (sharkSuccess * scale);
        swordSuccessPerHr = (int) (swordSuccess * scale);
        lobsterSuccessPerHr = (int) (lobsterSuccess * scale);
        tunaSuccessPerHr = (int) (tunaSuccess * scale);
        successPerHr = (int) (success * scale);
        failurePerHr = (int) (failure * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Coleslaw Guild Fisher @cya@~ Searos and Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y, 0xFFFFFF, 1);
      if (spotId == LOBSTER_FISH_SPOT) {
        if (swordFish) {
          c.drawString(
              "@whi@Swordfish Caught: @gre@"
                  + (swordSuccess + success)
                  + "@yel@ (@whi@"
                  + String.format("%,d", (swordSuccessPerHr + successPerHr))
                  + "@yel@/@whi@hr@yel@)",
              x,
              y + 14,
              0xFFFFFF,
              1);
          c.drawString(
              "@whi@Tuna Caught: @gre@"
                  + (tunaSuccess)
                  + "@yel@ (@whi@"
                  + String.format("%,d", tunaSuccessPerHr)
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
          c.drawString("@whi@Total fish in Bank: @gre@" + fishInBank, x, y + (14 * 4), 0xFFFFFF, 1);
          c.drawString("@whi@Fishing: @gre@Swordfish/Tuna", x, y + (14 * 5), 0xFFFFFF, 1);
          c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 6), 0xFFFFFF, 1);
        } else {
          c.drawString(
              "@whi@Lobsters Caught: @gre@"
                  + (lobsterSuccess + success)
                  + "@yel@ (@whi@"
                  + String.format("%,d", lobsterSuccessPerHr + successPerHr)
                  + "@yel@/@whi@hr@yel@)",
              x,
              y + 14,
              0xFFFFFF,
              1);
          c.drawString(
              "@whi@Failure to Catch: @gre@"
                  + failure
                  + "@yel@ (@whi@"
                  + failurePerHr
                  + "@yel@/@whi@hr@yel@)",
              x,
              y + (14 * 2),
              0xFFFFFF,
              1);
          c.drawString("@whi@Total fish in Bank: @gre@" + fishInBank, x, y + (14 * 3), 0xFFFFFF, 1);
          c.drawString("@whi@Fishing: @gre@Lobster", x, y + (14 * 4), 0xFFFFFF, 1);
          c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 5), 0xFFFFFF, 1);
        }
      } else if (spotId == SHARK_FISH_SPOT) {
        if (bigNetFishing) {
          c.drawString(
              "@whi@Big Net Fish Caught: @gre@"
                  + (success)
                  + "@yel@ (@whi@"
                  + String.format("%,d", successPerHr)
                  + "@yel@/@whi@hr@yel@)",
              x,
              y + 14,
              0xFFFFFF,
              1);
          c.drawString(
              "@whi@Failure to Catch: @gre@"
                  + failure
                  + "@yel@ (@whi@"
                  + failurePerHr
                  + "@yel@/@whi@hr@yel@)",
              x,
              y + (14 * 2),
              0xFFFFFF,
              1);
          c.drawString("@whi@Fishing: @gre@Big Net", x, y + (14 * 3), 0xFFFFFF, 1);
          c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 4), 0xFFFFFF, 1);
        } else {
          c.drawString(
              "@whi@Sharks Caught: @gre@"
                  + (sharkSuccess + success)
                  + "@yel@ (@whi@"
                  + String.format("%,d", sharkSuccessPerHr + successPerHr)
                  + "@yel@/@whi@hr@yel@)",
              x,
              y + 14,
              0xFFFFFF,
              1);
          c.drawString(
              "@whi@Failure to Catch: @gre@"
                  + failure
                  + "@yel@ (@whi@"
                  + failurePerHr
                  + "@yel@/@whi@hr@yel@)",
              x,
              y + (14 * 2),
              0xFFFFFF,
              1);
          c.drawString("@whi@Fishing: @gre@Sharks", x, y + (14 * 3), 0xFFFFFF, 1);
          c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 4), 0xFFFFFF, 1);
        }
      }
    }
  }
}
