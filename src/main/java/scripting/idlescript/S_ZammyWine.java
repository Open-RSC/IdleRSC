package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import javax.swing.*;
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
public final class S_ZammyWine extends K_kailaScript {
  private static final Controller con = Main.getController();
  private static int logId = -1;
  private static int logsInBank = 0;
  private static int totalBows = 0;
  private static boolean stringBows = false;
  private static final int BOW_STRING = 141; // S Water Jug
  private static final int KNIFE_ID = 13;
  private static final int zammyGrapeID = 1466; // S
  private static final int[] unstrungIds = {276, 658, 660, 662, 664, 666, zammyGrapeID};

  public int start(String[] parameters) {
    con.quitIfAuthentic();
    con.toggleBatchBarsOn();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      con.displayMessage("@gre@" + '"' + "Fast Longbow Fletcher" + '"' + " ~ by Kaila");
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
          || (stringBows && con.getInventoryItemCount(BOW_STRING) == 0)
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
      if (con.getInventoryItemCount(logId) > 0) {
        if (!stringBows) fletchingScript();
        else stringScript();
        con.sleep(100);
      }
      // con.sleep(320);
    }
  }

  private void stringScript() {
    con.displayMessage("@gre@Stringing..");
    con.setStatus("@gre@Stringing.");
    con.useItemOnItemBySlot(
        con.getInventoryItemSlotIndex(BOW_STRING), con.getInventoryItemSlotIndex(logId));
    con.sleep(2 * GAME_TICK);
    while (con.isBatching()) con.sleep(GAME_TICK);
  }

  private void fletchingScript() {
    con.displayMessage("@gre@Fletching..");
    con.setStatus("@gre@Fletching..");
    con.useItemOnItemBySlot(
        con.getInventoryItemSlotIndex(KNIFE_ID), con.getInventoryItemSlotIndex(logId));
    con.sleep(2 * GAME_TICK);
    con.optionAnswer(2);
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
      if (!stringBows) totalBows = totalBows + 29;
      else totalBows = totalBows + 15;
      if (con.getBankItemCount(logId) < 30 // out of logs or unstrung
          || (stringBows && con.getBankItemCount(BOW_STRING) < 30) // out of strings
          || (!stringBows
              && con.getBankItemCount(KNIFE_ID) == 0
              && con.getInventoryItemCount(KNIFE_ID) == 0)) {
        con.setStatus("@red@NO Logs in the bank, Logging Out!.");
        endSession();
      }
      if (con.getInventoryItemCount() > 0) {
        for (int itemId : con.getInventoryItemIds()) {
          if (itemId != KNIFE_ID) {
            con.depositItem(itemId, con.getInventoryItemCount(itemId));
          }
        }
      }
      con.sleep(GAME_TICK);
      if (!stringBows && con.getInventoryItemCount(KNIFE_ID) < 1) {
        con.withdrawItem(KNIFE_ID, 1);
        con.sleep(GAME_TICK);
      }
      if (con.getInventoryItemCount() < 30) {
        if (!stringBows) con.withdrawItem(logId, 29);
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
  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("Unstrung Longbow Maker ~ Kaila");
    JLabel knifeLabel = new JLabel("Start with Knife in Inv!");
    JLabel batchLabel = new JLabel("Batch Bars MUST be toggled ON in settings!!!");
    JLabel batchLabel2 = new JLabel("This ensures 29 Items are made per Menu Cycle.");
    JLabel logLabel = new JLabel("Log Type:");
    JComboBox<String> logField =
        new JComboBox<>(
            new String[] {"Log", "Oak", "Willow", "Maple", "Yew", "Magic", "ZammyWine"});
    JCheckBox stringBowsCheckbox = new JCheckBox("String Bows?", true);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          stringBows = stringBowsCheckbox.isSelected();
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
    scriptFrame.add(logLabel);
    scriptFrame.add(logField);
    scriptFrame.add(stringBowsCheckbox);
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
        successPerHr = (int) (totalBows * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      con.drawString("@red@Fast Bow Fletcher @mag@~ by Kaila", x, y - 3, 0xFFFFFF, 1);
      con.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      if (!stringBows)
        con.drawString("@whi@Logs in bank: @yel@" + logsInBank, x, y + 14, 0xFFFFFF, 1);
      else con.drawString("@whi@Unstrung Bows in bank: @yel@" + logsInBank, x, y + 14, 0xFFFFFF, 1);
      con.drawString("@whi@Longbows Made: @yel@" + totalBows, x, y + (14 * 2), 0xFFFFFF, 1);
      con.drawString(
          "@whi@Longbows Per Hr: @yel@" + String.format("%,d", successPerHr) + "@yel@/@whi@hr",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      con.drawString(
          "@whi@Time Remaining: " + con.timeToCompletion(totalBows, logsInBank, startTime),
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      con.drawString("@whi@Runtime: " + runTime, x, y + (14 * 5), 0xFFFFFF, 1);
      con.drawString("@whi@____________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
    }
  }
}
