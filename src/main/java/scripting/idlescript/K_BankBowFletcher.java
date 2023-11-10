package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Fast Longbow Fletcher</b>
 *
 * <p>Start in any bank. Can make unstrung bows from logs, or string them.
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
/*
 *      todo
 *          Add autostart sequence from fastPlate and change variables
 */
public final class K_BankBowFletcher extends K_kailaScript {
  private final Controller con = Main.getController();
  private int logId = -1;
  private int logsInBank = 0;
  private int logsCut = 0;
  private int scriptOption = 0;
  private int dialogOption = 0;
  private int secondaryId = 0;
  private final int BOW_STRING = ItemId.BOW_STRING.getId();
  private final int KNIFE_ID = ItemId.KNIFE.getId();
  private final int[] unstrungIds = {
    ItemId.UNSTRUNG_LONGBOW.getId(),
    ItemId.UNSTRUNG_OAK_LONGBOW.getId(),
    ItemId.UNSTRUNG_WILLOW_LONGBOW.getId(),
    ItemId.UNSTRUNG_MAPLE_LONGBOW.getId(),
    ItemId.UNSTRUNG_YEW_LONGBOW.getId(),
    ItemId.UNSTRUNG_MAGIC_LONGBOW.getId()
  };
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
    con.quitIfAuthentic();
    con.toggleBatchBarsOn();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      con.displayMessage("@gre@Fast Longbow Fletcher ~ by Kaila");
      con.displayMessage("@gre@Start at any bank, with a KNIFE in Inv");
      con.displayMessage("@red@REQUIRES Batch bars be toggle on in settings to work correctly!");

      guiSetup = false;
      scriptStarted = false;
      if (con.isInBank()) con.closeBank();
      startTime = System.currentTimeMillis();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (con.isRunning()) {
      if (con.getInventoryItemCount(logId) == 0
          || (scriptOption == 1 && con.getInventoryItemCount(BOW_STRING) == 0)
          || con.getInventoryItemCount(KNIFE_ID) == 0) {
        if (!con.isInBank()) {
          int[] bankerIds = {95, 224, 268, 540, 617, 792};
          ORSCharacter npc = con.getNearestNpcByIds(bankerIds, false);
          if (npc != null) {
            con.setStatus("@yel@Walking to Banker..");
            con.displayMessage("@yel@Walking to Banker..");
            con.walktoNPCAsync(npc.serverIndex);
            con.sleep(200);
          } else {
            con.log("@red@walking to Bank Error..");
            con.sleep(1000);
          }
        }
        bank();
      }
      if (con.getInventoryItemCount(logId) > 0) fletchingLoop();
    }
  }

  private void fletchingLoop() {
    con.displayMessage("@gre@Fletching arrow Shafts..");
    con.setStatus("@gre@Fletching arrow Shafts..");
    con.useItemOnItemBySlot(
        con.getInventoryItemSlotIndex(secondaryId), con.getInventoryItemSlotIndex(logId));
    con.sleep(2 * GAME_TICK);
    if (scriptOption == 0 || scriptOption == 2) con.optionAnswer(dialogOption);
    while (con.isBatching()) con.sleep(GAME_TICK);
  }

  private void bank() {
    con.setStatus("@gre@Banking..");
    con.displayMessage("@gre@Banking..");
    con.openBank();
    con.sleep(GAME_TICK);
    if (!con.isInBank()) {
      waitForBankOpen();
    } else {
      if (scriptOption == 0 || scriptOption == 2) logsCut = logsCut + 29;
      else logsCut = logsCut + 15;
      if (con.getBankItemCount(logId) < 30 // out of logs or unstrung
          || (scriptOption == 0 && con.getBankItemCount(BOW_STRING) < 30) // out of strings
          || (scriptOption == 2
              && con.getBankItemCount(KNIFE_ID) == 0
              && con.getInventoryItemCount(KNIFE_ID) == 0)) {
        con.setStatus("@red@NO Logs/Strings/Knife in the bank, Logging Out!.");
        endSession();
      }
      if (con.getInventoryItemCount() > 0) {
        for (int itemId : con.getInventoryItemIds()) {
          con.depositItem(itemId, con.getInventoryItemCount(itemId));
        }
      }
      con.sleep(GAME_TICK);
      if ((scriptOption == 0 || scriptOption == 2) && con.getInventoryItemCount(KNIFE_ID) < 1) {
        con.withdrawItem(KNIFE_ID, 1);
      }
      if (con.getInventoryItemCount() < 30) {
        if (scriptOption == 0 || scriptOption == 2) con.withdrawItem(logId, 29);
        else {
          con.withdrawItem(logId, 15);
          con.sleep(GAME_TICK);
          con.withdrawItem(BOW_STRING, 15);
          con.sleep(GAME_TICK);
        }
      }
      logsInBank = con.getBankItemCount(logId);
      con.closeBank();
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Unstrung Longbow Maker ~ Kaila");
    JLabel batchLabel = new JLabel("Batch Bars MUST be On, Bot will attempt to enable it.");
    JLabel batchLabel2 = new JLabel("This ensures 29 Items are made per Menu Cycle.");
    JLabel logLabel = new JLabel("Log Type:");
    JComboBox<String> logField =
        new JComboBox<>(new String[] {"Log", "Oak", "Willow", "Maple", "Yew", "Magic"});
    JLabel optionLabel = new JLabel("Select Fletching Type:");
    JComboBox<String> optionsField =
        new JComboBox<>(new String[] {"Fletch Bows", "String Bows", "Make Arrow Shafts"});
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          scriptOption = optionsField.getSelectedIndex();
          if (scriptOption == 0) { // fletching
            logId = logIds[logField.getSelectedIndex()];
            dialogOption = 2;
            secondaryId = KNIFE_ID;
          } else if (scriptOption == 1) { // stringing
            logId = unstrungIds[logField.getSelectedIndex()];
            secondaryId = BOW_STRING;
          } else if (scriptOption == 2) { // arrow shafts
            logId = logIds[logField.getSelectedIndex()];
            dialogOption = 0;
            secondaryId = KNIFE_ID;
          } else c.log("Error in script option");
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
    scriptFrame.add(logLabel);
    scriptFrame.add(logField);
    scriptFrame.add(optionLabel);
    scriptFrame.add(optionsField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = con.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (logsCut * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      con.drawString("@red@Fast Bow Fletcher @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      con.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      if (scriptOption == 0 || scriptOption == 2)
        con.drawString("@whi@Logs in bank: @yel@" + logsInBank, x, y + 14, 0xFFFFFF, 1);
      else con.drawString("@whi@Unstrung Bows in bank: @yel@" + logsInBank, x, y + 14, 0xFFFFFF, 1);
      con.drawString("@whi@Logs Cut: @yel@" + logsCut, x, y + (14 * 2), 0xFFFFFF, 1);
      con.drawString(
          "@whi@Longbows Per Hr: @yel@" + String.format("%,d", successPerHr) + "@yel@/@whi@hr",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      con.drawString(
          "@whi@Time Remaining: " + con.timeToCompletion(logsCut, logsInBank, startTime),
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      con.drawString("@whi@Runtime: " + runTime, x, y + (14 * 5), 0xFFFFFF, 1);
      con.drawString("@whi@____________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
    }
  }
}
