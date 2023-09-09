package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Bank Firemaking</b>
 *
 * <p>Start in listed Bank and burn logs
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_Bank_Firemaking extends K_kailaScript {
  private static int logId = 0;
  private static int burnSuccess = 0;
  private static int logsInBank = 0;
  private static final int TINDERBOX = ItemId.TINDERBOX.getId();
  private static final String[] burnLocations = {"varrockWest", "varrockEast", "seers", "ardougne"};
  private static String burnPlace;
  private final int[] logIds = {
    ItemId.LOGS.getId(),
    ItemId.OAK_LOGS.getId(),
    ItemId.WILLOW_LOGS.getId(),
    ItemId.MAPLE_LOGS.getId(),
    ItemId.YEW_LOGS.getId(),
    ItemId.MAGIC_LOGS.getId()
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    c.quitIfAuthentic();
    c.toggleBatchBarsOn();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      c.displayMessage("@gre@Bank Firemaking ~ by Kaila");
      c.displayMessage("@gre@Do NOT use a firemaking skill cape with this trainer");
      c.displayMessage("@red@REQUIRES Batch bars On, bot will attempt to enable them!");

      guiSetup = false;
      scriptStarted = false;
      if (c.isInBank()) c.closeBank();
      startTime = System.currentTimeMillis();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getInventoryItemCount(logId) == 0 || c.getInventoryItemCount(TINDERBOX) == 0) {
        if (!c.isInBank()) {
          if (burnPlace.equals("varrockWest")) {
            if (c.currentX() > 165) {
              c.walkTo(165, 507);
              c.walkTo(151, 507);
            } else if (c.currentX() < 138) c.walkTo(148, 508);
          } else if (burnPlace.equals("varrockEast")) {
            if (c.currentX() > 115) c.walkTo(103, 509);
          } else if (burnPlace.equals("seers")) {
            if (c.currentX() > 115) c.walkTo(504, 457);
          } else if (burnPlace.equals("ardougne")) {
            if (c.currentX() > 563) c.walkTo(549, 613);
          }
          int[] bankerIds = {95, 224, 268, 540, 617, 792};
          ORSCharacter npc = c.getNearestNpcByIds(bankerIds, false);
          if (npc != null) {
            c.setStatus("@yel@Walking to Banker..");
            c.displayMessage("@yel@Walking to Banker..");
            c.walktoNPCAsync(npc.serverIndex);
            c.sleep(200);
          } else {
            c.log("@red@walking to Bank Error..");
            c.sleep(1000);
          }
        }
        bank();
      }
      if (c.getInventoryItemCount(logId) > 0) {
        burnScript();
      }
    }
  }

  private void burnScript() {
    if (burnPlace.equals("varrockWest")) {
      c.displayMessage("@gre@Got varrock west..");
      burnVarrockWest();
    } else if (burnPlace.equals("varrockEast")) {
      burnVarrockEast();
    } else if (burnPlace.equals("seers")) {
      burnSeers();
    } else if (burnPlace.equals("ardougne")) {
      burnArdougne();
    }
    while (c.isBatching()) c.sleep(GAME_TICK);
  }

  private static final int[] vWestStart = {
    152, 507, 157, 506, 157, 505, 157, 504, 134, 508, 131, 509
  }; // x then y then x then y

  private void burnVarrockWest() {
    c.displayMessage("@gre@Burning logs..");
    c.setStatus("@gre@Burning logs..");
    for (int i = 0; i < (vWestStart.length); i += 2) {
      if (c.getInventoryItemCount(logId) < 1) break;
      if (vWestStart[i] < 140) {
        c.walkTo(138, 508);
      }
      if (c.getObjectAtCoord(vWestStart[i], vWestStart[i + 1]) != 97) { // can use c.isTileEmpty
        c.walkTo(vWestStart[i], vWestStart[i + 1]);
        c.dropItem(c.getInventoryItemSlotIndex(logId), 1);
        c.sleep(GAME_TICK);
        c.useItemOnGroundItem(vWestStart[i], vWestStart[i + 1], TINDERBOX, logId);
        c.sleep(6 * GAME_TICK);
        while (c.isBatching()) c.sleep(GAME_TICK);
      }
      c.sleep(GAME_TICK);
    }
  }

  private static final int[] vEastStart = {
    87, 508, 87, 507, 90, 506, 77, 509
  }; // x then y then x then y

  private void burnVarrockEast() {
    c.displayMessage("@gre@Burning logs..");
    c.setStatus("@gre@Burning logs..");
    for (int i = 0; i < (vEastStart.length); i += 2) {
      if (c.getInventoryItemCount(logId) < 1) break;
      if (vEastStart[i] < 80) {
        c.walkTo(84, 509);
      }
      if (c.getObjectAtCoord(vEastStart[i], vEastStart[i + 1]) != 97) { // can use c.isTileEmpty
        c.walkTo(vEastStart[i], vEastStart[i + 1]);
        c.dropItem(c.getInventoryItemSlotIndex(logId), 1);
        c.sleep(GAME_TICK);
        c.useItemOnGroundItem(vEastStart[i], vEastStart[i + 1], TINDERBOX, logId);
        c.sleep(6 * GAME_TICK);
        while (c.isBatching()) c.sleep(GAME_TICK);
      }
      c.sleep(GAME_TICK);
    }
  }

  private static final int[] seersStart = {
    493, 456, 490, 458, 483, 460, 484, 463
  }; // x then y then x then y

  private void burnSeers() {
    c.displayMessage("@gre@Burning logs..");
    c.setStatus("@gre@Burning logs..");
    for (int i = 0; i < (seersStart.length); i += 2) {
      if (c.getInventoryItemCount(logId) < 1) break;
      if (seersStart[i] < 489) {
        c.walkTo(490, 460);
      }
      if (c.getObjectAtCoord(seersStart[i], seersStart[i + 1]) != 97) { // can use c.isTileEmpty
        c.walkTo(seersStart[i], seersStart[i + 1]);
        c.dropItem(c.getInventoryItemSlotIndex(logId), 1);
        c.sleep(GAME_TICK);
        c.useItemOnGroundItem(seersStart[i], seersStart[i + 1], TINDERBOX, logId);
        c.sleep(6 * GAME_TICK);
        while (c.isBatching()) c.sleep(GAME_TICK);
      }
      c.sleep(GAME_TICK);
    }
  }

  private static final int[] ardougneStart = {
    538, 607, 537, 606, 550, 620, 534, 605
  }; // x then y then x then y

  private void burnArdougne() {
    c.displayMessage("@gre@Burning logs..");
    c.setStatus("@gre@Burning logs..");
    for (int i = 0; i < (ardougneStart.length); i += 2) {
      if (c.getInventoryItemCount(logId) < 1) break;
      if (c.getObjectAtCoord(ardougneStart[i], ardougneStart[i + 1])
          != 97) { // can use c.isTileEmpty
        c.walkTo(ardougneStart[i], ardougneStart[i + 1]);
        c.dropItem(c.getInventoryItemSlotIndex(logId), 1);
        c.sleep(GAME_TICK);
        c.useItemOnGroundItem(ardougneStart[i], ardougneStart[i + 1], TINDERBOX, logId);
        c.sleep(6 * GAME_TICK);
        while (c.isBatching()) c.sleep(GAME_TICK);
      }
      c.sleep(GAME_TICK);
    }
  }

  private void bank() {
    c.setStatus("@gre@Banking..");
    c.displayMessage("@gre@Banking..");
    c.openBank();
    c.sleep(GAME_TICK);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      if (c.getBankItemCount(logId) < 30 // out of logs or unstrun
          || (c.getBankItemCount(TINDERBOX) == 0 && c.getInventoryItemCount(TINDERBOX) == 0)) {
        c.setStatus("@red@NO Logs in the bank, Logging Out!.");
        endSession();
      }
      if (c.getInventoryItemCount() > 0) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != TINDERBOX) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
      }
      c.sleep(GAME_TICK);
      if (c.getInventoryItemCount(TINDERBOX) < 1) {
        c.withdrawItem(TINDERBOX, 1);
        c.sleep(GAME_TICK);
      }
      if (c.getInventoryItemCount() < 30) c.withdrawItem(logId, 29);
      logsInBank = c.getBankItemCount(logId);
      c.closeBank();
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Bank Firemaking  ~ Kaila");
    JLabel batchLabel = new JLabel("Batch Bars MUST be toggled ON in settings!!!");
    JLabel batchLabel2 = new JLabel("This ensures 29 Items are made per Menu Cycle.");
    JLabel capeWarning = new JLabel("Do NOT use a firemaking skill cape with this trainer");
    JLabel logLabel = new JLabel("Log Type:");
    JComboBox<String> logField =
        new JComboBox<>(new String[] {"Log", "Oak", "Willow", "Maple", "Yew", "Magic"});
    logField.setSelectedIndex(4);
    JLabel burnLoc = new JLabel("Burn Location:");
    JComboBox<String> burnLocation =
        new JComboBox<>(
            new String[] {"Varrock West", "Varrock East", "Seers Village", "Ardougne South"});
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          logId = logIds[logField.getSelectedIndex()];
          burnPlace = burnLocations[burnLocation.getSelectedIndex()];
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(batchLabel);
    scriptFrame.add(batchLabel2);
    scriptFrame.add(capeWarning);
    scriptFrame.add(logLabel);
    scriptFrame.add(logField);
    scriptFrame.add(burnLoc);
    scriptFrame.add(burnLocation);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("The fire catches")) {
      burnSuccess++;
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (burnSuccess * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Bank Firemaking @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Logs in bank: @yel@" + logsInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString("@whi@Logs Burned: @yel@" + burnSuccess, x, y + (14 * 2), 0xFFFFFF, 1);
      c.drawString(
          "@whi@Logs Per Hr: @yel@" + String.format("%,d", successPerHr) + "@yel@/@whi@hr",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Time Remaining: " + c.timeToCompletion(burnSuccess, logsInBank, startTime),
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 5), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
    }
  }
}
