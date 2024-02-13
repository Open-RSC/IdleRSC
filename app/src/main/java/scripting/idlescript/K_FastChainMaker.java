package scripting.idlescript;

import bot.Main;
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
public final class K_FastChainMaker extends K_kailaScript {
  private static int totalBars = 0;
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
      c.displayMessage("@gre@Dragon scale mail maker" + '"' + " - by Kaila");
      c.displayMessage("@gre@Start in Fally east bank");
      c.quitIfAuthentic();
      if (c.currentX() < 299) bankToSpot();
      if (c.isInBank()) c.closeBank();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount() == 30) {
        spotToBank();
        bank();
        bankToSpot();
      }
      if (c.getInventoryItemCount(1366) > 0 && c.getInventoryItemCount() < 30) {
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

  private void spotToBank() {
    c.walkTo(303, 576);
    c.walkTo(294, 572);
    c.walkTo(286, 571);
  }

  private void bankToSpot() {
    c.walkTo(286, 571);
    c.walkTo(294, 572);
    c.walkTo(303, 576);
  }

  private void bank() {
    c.setStatus("@gre@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {

      totalBars = totalBars + 26;

      if (c.getInventoryItemCount() > 3) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 1366 && itemId != 1367 && itemId != 10) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
        c.sleep(100);
      }
      c.sleep(2000);
      c.closeBank();
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Platebody Smithing @whi@~ @mag@Kaila");
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
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }
}
