package scripting.idlescript;

import bot.Main;
import java.awt.*;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * <b>Coleslaw Present/Cracker/Trick Giver</b>
 *
 * <p>Opens gift item by using them on a 2nd account. Used in conjunction with K_GiftTaker Script!
 * <br>
 *
 * <p>Should work in any bank, Ideal location is Draynor Bank! Requires 2 accounts. This bot is the
 * present "taker", it will bank when you have 29 items.<br>
 *
 * <p>To setup start both accounts near each other with NO items in either inventory. start the
 * taker bot FIRST before even starting giver bot. the bots will need to be synced up similar to
 * trader bots. ideally monitor them, if something goes wrong present stuff will drop to the floor
 * and despawn!!!!! you have been warned!<br>
 *
 * <p>WARNING: while within 1 tile of the giver, you will continue to recieve presents. WARNING:
 * regardless of how full your inventory is. items WILL drop to the floor. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_GiftGiver extends K_kailaScript {
  private final String[] options =
      new String[] {"Xmas Crackers", "Xmas Presents", "Halloween Crackers"};
  private final int[] bankerIds = {95, 224, 268, 540, 617, 792};
  private static String playerName;
  private int option = -1;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    c.quitIfAuthentic();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      c.displayMessage("@red@present GIVER! Let's party like it's 2004! ~ Kaila");
      c.displayMessage("@red@Ideal location is Draynor Bank!");
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
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (option == 0) { // Xmas Crackers
        crackerLoop();
      } else if (option == 1) { // Xmas Presents
        presentLoop();
      } else if (option == 2) { // Halloween Crackers
        trickLoop();
      }
    }
  }

  private void presentLoop() {
    if (c.getInventoryItemCount(980) < 2) {
      c.setStatus("@gre@Banking.");
      if (!c.isInBank()) {
        ORSCharacter npc = c.getNearestNpcByIds(bankerIds, false);
        if (npc != null) {
          c.setStatus("@yel@Walking to Banker..");
          c.displayMessage("@yel@Walking to Banker..");
          c.walktoNPCAsync(npc.serverIndex);
          c.sleep(200);
        } else {
          c.log("@red@Error..");
          c.sleep(1000);
        }
      }
      presentBank();
    }
    if (c.getInventoryItemCount(980) > 1) {
      c.useItemOnPlayer(c.getInventoryItemSlotIndex(980), c.getPlayerServerIndexByName(playerName));
      c.sleep(640);
    }
  }

  private void presentBank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      if (c.getInventoryItemCount(980) < 2) {
        c.withdrawItem(980, 30);
        c.sleep(1280);
      }
      c.closeBank();
      c.setStatus("@gre@Opening.");
    }
  }

  private void trickLoop() {
    if (c.getInventoryItemCount() > 29) {
      c.setStatus("@gre@Banking.");
      if (!c.isInBank()) {
        ORSCharacter npc = c.getNearestNpcByIds(bankerIds, false);
        if (npc != null) {
          c.setStatus("@yel@Walking to Banker..");
          c.displayMessage("@yel@Walking to Banker..");
          c.walktoNPCAsync(npc.serverIndex);
          c.sleep(200);
        } else {
          c.log("@red@Error..");
          c.sleep(1000);
        }
      }
      trickBank();
    }
    if (c.getInventoryItemCount(1330) < 2) {
      c.setStatus("@gre@Banking.");
      if (!c.isInBank()) {
        ORSCharacter npc = c.getNearestNpcByIds(bankerIds, false);
        if (npc != null) {
          c.setStatus("@yel@Walking to Banker..");
          c.displayMessage("@yel@Walking to Banker..");
          c.walktoNPCAsync(npc.serverIndex);
          c.sleep(200);
        } else {
          c.log("@red@Error..");
          c.sleep(1000);
        }
      }
      trickBank();
    }
    if (c.getInventoryItemCount() < 30 && c.getInventoryItemCount(1330) > 1) {
      c.setStatus("@gre@Opening.");
      c.useItemOnPlayer(
          c.getInventoryItemSlotIndex(1330), c.getPlayerServerIndexByName(playerName));
      c.sleep(640);
    }
  }

  private void trickBank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      K_kailaScript.waitForBankOpen();
    } else {
      if (c.getInventoryItemCount() > 0) {
        for (int itemId : c.getInventoryItemIds()) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
        c.sleep(1280);
      }
      if (c.getInventoryItemCount(1330) < 23) {
        c.withdrawItem(1330, 23 - c.getInventoryItemCount(1330));
        c.sleep(1280);
      }
      c.closeBank();
    }
  }

  private void crackerLoop() {
    if (c.getInventoryItemCount() > 29) {
      c.setStatus("@gre@Banking.");
      if (!c.isInBank()) {
        int[] bankerIds = {95, 224, 268, 540, 617, 792};
        ORSCharacter npc = c.getNearestNpcByIds(bankerIds, false);
        if (npc != null) {
          c.setStatus("@yel@Walking to Banker..");
          c.displayMessage("@yel@Walking to Banker..");
          c.walktoNPCAsync(npc.serverIndex);
          c.sleep(200);
        } else {
          c.log("@red@Error..");
          c.sleep(1000);
        }
      }
      crackerBank();
    }
    if (c.getInventoryItemCount(575) < 2) {
      c.setStatus("@gre@Banking.");
      if (!c.isInBank()) {
        int[] bankerIds = {95, 224, 268, 540, 617, 792};
        ORSCharacter npc = c.getNearestNpcByIds(bankerIds, false);
        if (npc != null) {
          c.setStatus("@yel@Walking to Banker..");
          c.displayMessage("@yel@Walking to Banker..");
          c.walktoNPCAsync(npc.serverIndex);
          c.sleep(200);
        } else {
          c.log("@red@Error..");
          c.sleep(1000);
        }
      }
      crackerBank();
    }
    if (c.getInventoryItemCount() < 30 && c.getInventoryItemCount(575) > 1) {
      c.setStatus("@gre@Opening.");
      c.useItemOnPlayer(
          c.getInventoryItemSlotIndex(575),
          c.getPlayerServerIndexByName(playerName)); // replace the player name
      c.sleep(640);
    }
  }

  private void crackerBank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      K_kailaScript.waitForBankOpen();
    } else {
      if (c.getInventoryItemCount() > 0) {
        for (int itemId : c.getInventoryItemIds()) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
        c.sleep(1280);
      }
      if (c.getInventoryItemCount(575) < 23) {
        c.withdrawItem(575, 23 - c.getInventoryItemCount(575));
        c.sleep(1280);
        c.closeBank();
      }
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Gift giver ~ Kaila");
    JLabel batchLabel = new JLabel("Opens gift item by using them on a 2nd account");
    JLabel batchLabel2 = new JLabel("Used in conjunction with K_GiftTaker");
    JLabel logLabel = new JLabel("WARNING: the taker account will continue to receive items");
    JLabel logLabel2 = new JLabel("If a glitch happens, Items WILL go to the ground");
    JComboBox<String> optionField = new JComboBox<>(options);
    JTextField playerNameField = new JTextField("exampleAccountName");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          option = optionField.getSelectedIndex();
          if (!playerNameField.getText().isEmpty()) playerName = playerNameField.getText();
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
    scriptFrame.add(logLabel2);
    scriptFrame.add(optionField);
    scriptFrame.add(playerNameField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }
}
