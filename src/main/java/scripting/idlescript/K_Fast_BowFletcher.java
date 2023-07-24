package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import orsc.ORSCharacter;

/**
 * Fast Longbow Fletcher
 *
 * <p>Start in any bank with knife in inventory
 *
 * <p>@Author ~ Kaila
 */
/*
 *      todo
 *          Add autostart sequence from fastPlate and change variables
 */
public final class K_Fast_BowFletcher extends K_kailaScript {
  private static int logId = -1;
  private static int logsInBank = 0;
  private static int totalBows = 0;

  public int start(String[] parameters) {
    c.quitIfAuthentic();
    checkBatchBars();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      c.displayMessage("@gre@" + '"' + "Fast Longbow Fletcher" + '"' + " ~ by Kaila");
      c.displayMessage("@gre@Start at any bank, with a KNIFE in Inv");
      c.displayMessage("@red@REQUIRES Batch bars be toggle on in settings to work correctly!");

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
      if (c.getInventoryItemCount(logId) < 1) {
        if (!c.isInBank()) {
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
        fletchingScript();
      }
      c.sleep(320);
    }
  }

  private void fletchingScript() {
    c.displayMessage("@gre@Fletching..");
    c.setStatus("@gre@Fletching..");
    c.useItemOnItemBySlot(c.getInventoryItemSlotIndex(13), c.getInventoryItemSlotIndex(logId));
    c.sleep(1200);
    c.optionAnswer(2);
    while (c.isBatching()) c.sleep(1000);
  }

  private void bank() {
    c.setStatus("@gre@Banking..");
    c.displayMessage("@gre@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalBows = totalBows + 29;
      if (c.getBankItemCount(logId)
          < 30) { // stops making when 30 in bank to not mess up alignments/organization of bank!!!
        c.setStatus("@red@NO Logs in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      if (c.getInventoryItemCount() > 0) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 13 && itemId != logId) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
        c.sleep(100);
      }
      if (c.getInventoryItemCount(13) < 1) {
        c.withdrawItem(13, 1);
        c.sleep(320);
      }
      if (c.getInventoryItemCount() < 30) {
        c.withdrawItem(logId, 29);
        c.sleep(650);
      }

      logsInBank = c.getBankItemCount(logId);
      c.closeBank();
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
        new JComboBox<>(new String[] {"Log", "Oak", "Willow", "Maple", "Yew", "Magic"});
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          logId = logIds[logField.getSelectedIndex()];
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
      c.drawString("@red@Fast Bow Fletcher @mag@~ by Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Logs In bank: @yel@" + logsInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString("@whi@Longbows Made: @yel@" + totalBows, x, y + (14 * 2), 0xFFFFFF, 1);
      c.drawString(
          "@whi@Longbows Per Hr: @yel@" + String.format("%,d", successPerHr) + "@yel@/@whi@hr",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Time Remaining: " + c.timeToCompletion(totalBows, logsInBank, startTime),
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 5), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
    }
  }
}
