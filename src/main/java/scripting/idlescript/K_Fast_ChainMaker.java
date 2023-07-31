package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import orsc.ORSCharacter;

/**
 * Fast Platebody Smither - by Kaila. ~ Start with Hammer in Inv! Batch Bars MUST be toggled ON in
 * settings!!! This ensures 5 Plates are made per Menu Cycle. Supports all bar types. ~ This bot
 * supports the \"autostart\" parameter. defaults to steel plates. ~ Parameters for Starting: auto,
 * autostart - makes steel platebodies. bronze - makes bronze platebodies. iron - makes iron
 * platebodies. steel - makes steel platebodies. mith, mithril - makes mithril platebodies. addy,
 * adamantite - makes adamantite platebodies. rune, runite - makes runite platebodies. ~
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_Fast_ChainMaker extends K_kailaScript {
  private static int totalBars = 0;

  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.displayMessage("@gre@Chain Link Crafter" + '"' + " - by Kaila");
      c.displayMessage("@gre@Start in Fally East");

      c.quitIfAuthentic();
      if (c.isInBank()){
        c.closeBank();
        c.sleep(2*GAME_TICK);
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getInventoryItemCount(1366) > 0) {
        c.setStatus("@gre@Talking to Wayne..");
        ORSCharacter npc = c.getNearestNpcById(141, false);
        c.talkToNpc(npc.serverIndex);
        while (!c.isInOptionMenu()) c.sleep(640);
        if (c.isInOptionMenu()) {
          c.optionAnswer(2);
          c.sleep(1240);
        }
        while (!c.isInOptionMenu()) c.sleep(640);
        if (c.isInOptionMenu()) {
          c.optionAnswer(0);
          c.sleep(25000); // 30k worked
        }
      }
    }
  }

  private void bank() {
    c.setStatus("@gre@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {

      totalBars = totalBars + 26;

      if (c.getBankItemCount(593)
          < 2) { // stops making when 30 in bank to not mess up alignments/organization of bank!!!
        c.setStatus("@red@NO D Longs in the bank, Stopping!.");
        c.stop();
      }
      if (c.getInventoryItemCount() > 1) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 168 && itemId != 33 && itemId != 42 && itemId != 32) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
        c.sleep(100);
      }
      c.sleep(1240);
      if (c.getInventoryItemCount(168) < 1) { // hammer
        c.withdrawItem(168, 1 - c.getInventoryItemCount(168));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(33) < 300) { // air
        c.withdrawItem(33, 300 - c.getInventoryItemCount(33));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(42) < 100) { // law
        c.withdrawItem(42, 100 - c.getInventoryItemCount(42));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(32) < 100) { // water
        c.withdrawItem(32, 100 - c.getInventoryItemCount(32));
        c.sleep(1000);
      }
      if (c.getInventoryItemCount(593) < 25) { // dlong
        c.withdrawItem(593, 25);
        c.sleep(100);
      }
      c.closeBank();
      c.sleep(2*GAME_TICK);
    }
  }
  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("Platebody Smithing - Kaila");
    JLabel hammerLabel = new JLabel("Start with Hammer in Inv!");
    JLabel batchLabel = new JLabel("Batch Bars MUST be toggled ON in settings!!!");
    JLabel batchLabel2 = new JLabel("This ensures 5 Plates are made per Menu Cycle.");
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
    scriptFrame.add(hammerLabel);
    scriptFrame.add(batchLabel);
    scriptFrame.add(batchLabel2);
    scriptFrame.add(startScriptButton);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }
}
