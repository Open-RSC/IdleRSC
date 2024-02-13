package scripting.idlescript;

import bot.Main;
import java.awt.*;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * <b>Iron Present Opener</b>
 *
 * <p>Opens Holiday event Presents on an Iron, banks loot (coleslaw) only works on official irons.
 * <br>
 * start in any bank. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_IronGiftOpener extends K_kailaScript {
  private final String[] options =
      new String[] {"Xmas Crackers", "Xmas Presents", "Halloween Crackers"};
  private final int[] itemIds = {575, 980, 1330}; // same order as options
  private final int[] bankerIds = {95, 224, 268, 540, 617, 792};
  private int itemId = -1;
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
      c.displayMessage("@ran@Iron Present Opener! Let's party like it's 2004!");
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
      if (c.getInventoryItemCount(itemId) < 1 || c.getInventoryItemCount() > 29) {
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
        bank();
      }
      if (c.getInventoryItemCount(itemId) > 0 && c.getInventoryItemCount() < 30) {
        c.setStatus("@Gre@Opening..");
        if (option == 0 || option == 2) { // open crackers or tricks
          ORSCharacter npc = c.getNearestNpcByIds(bankerIds, false);
          c.useItemOnNpc(npc.serverIndex, itemId);
          c.sleep(3000);
        } else if (option == 1) {
          c.itemCommand(itemId); // open presents
          c.sleep(650);
        }
      }
    }
  }

  public void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      for (int itemId : c.getInventoryItemIds()) {
        c.depositItem(itemId, c.getInventoryItemCount(itemId));
      }
      c.sleep(2000); // Important, leave in

      int withdrawAmount = 0;
      if (option == 0 || option == 2) { // open crackers or tricks
        withdrawAmount = 15;
      } else if (option == 1) { // open presents
        withdrawAmount = 28;
      }
      if (c.getInventoryItemCount(itemId) < withdrawAmount) {
        c.withdrawItem(itemId, withdrawAmount);
        c.sleep(640);
      }
      c.closeBank();
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Iron Gift Opener ~ Kaila");
    JLabel batchLabel = new JLabel("Opens gift items by using them on bankers");
    JLabel batchLabel2 = new JLabel("Presents open while in the inventory");
    JComboBox<String> optionField = new JComboBox<>(options);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          option = optionField.getSelectedIndex();
          itemId = itemIds[option];
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
    scriptFrame.add(optionField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }
}
