package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
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
 * <p>todo Add autostart sequence from fastPlate and change variables
 *
 * <p>Author - Kaila
 */
public class K_FastBowFletcher extends IdleScript {
  Controller c = Main.getController();
  JFrame scriptFrame = null;
  int logId = -1;
  int[] logIds = {14, 632, 633, 634, 635, 636};
  boolean guiSetup = false;
  boolean scriptStarted = false;
  int logsInBank = 0;
  int totalBows = 0;

  long startTime;
  long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String parameters[]) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      if (c.isInBank() == true) {
        c.closeBank();
      }
      c.quitIfAuthentic();
      scriptStart();
    }
    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
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
        c.displayMessage("@gre@Fletching..");
        c.setStatus("@gre@Fletching..");
        c.useItemOnItemBySlot(c.getInventoryItemSlotIndex(13), c.getInventoryItemSlotIndex(logId));
        c.sleep(1200);
        c.optionAnswer(2);
        while (c.isBatching()) c.sleep(1000);
      }
      c.sleep(320);
    }
  }

  public void bank() {

    c.setStatus("@gre@Banking..");
    c.displayMessage("@gre@Banking..");
    c.openBank();
    c.sleep(640);

    if (c.isInBank()) {

      totalBows = totalBows + 29;

      if (c.getBankItemCount(logId)
          < 30) { // stops making when 30 in bank to not mess up alignments/organization of bank!!!
        c.setStatus("@red@NO Logs in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
          return;
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
  public void setupGUI() {
    JLabel header = new JLabel("Unstrung Longbow Maker - Kaila");
    JLabel knifeLabel = new JLabel("Start with Knife in Inv!");
    JLabel batchLabel = new JLabel("Batch Bars MUST be toggled ON in settings!!!");
    JLabel batchLabel2 = new JLabel("This ensures 29 Items are made per Menu Cycle.");
    JLabel logLabel = new JLabel("Log Type:");
    JComboBox<String> logField =
        new JComboBox<String>(new String[] {"Log", "Oak", "Willow", "Maple", "Yew", "Magic"});
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            logId = logIds[logField.getSelectedIndex()];
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            startTime = System.currentTimeMillis();
            scriptStarted = true;
            c.displayMessage("@gre@" + '"' + "Fast Longbow Fletcher" + '"' + " - by Kaila");
            c.displayMessage("@gre@Start at any bank, with a KNIFE in Inv");
            c.displayMessage(
                "@red@REQUIRES Batch bars be toggle on in settings to work correctly!");
          }
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

  public static String msToString(long milliseconds) {
    long sec = milliseconds / 1000;
    long min = sec / 60;
    long hour = min / 60;
    sec %= 60;
    min %= 60;
    DecimalFormat twoDigits = new DecimalFormat("00");

    return new String(
        twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec));
  }
  /** credit to chomp for toTimeToCompletion (from AA_Script) (totalBars, barsInBank, startTime) */
  public static String toTimeToCompletion(
      final int processed, final int remaining, final long time) {
    if (processed == 0) {
      return "0:00:00";
    }

    final double seconds = (System.currentTimeMillis() - time) / 1000.0;
    final double secondsPerItem = seconds / processed;
    final long ttl = (long) (secondsPerItem * remaining);
    return String.format("%d:%02d:%02d", ttl / 3600, (ttl % 3600) / 60, (ttl % 60));
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      try {
        float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (totalBows * scale);
      } catch (Exception e) {
        // divide by zero
      }
      c.drawString("@red@Fast Bow Fletcher @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Logs In bank: @yel@" + String.valueOf(this.logsInBank), 350, 62, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Longbows Made: @yel@" + String.valueOf(this.totalBows), 350, 76, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Longbows Per Hr: @yel@" + String.format("%,d", successPerHr) + "@yel@/@whi@hr",
          350,
          90,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Time Remaining: " + toTimeToCompletion(totalBows, logsInBank, startTime),
          350,
          104,
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, 350, 104 + 14, 0xFFFFFF, 1);
    }
  }
}
