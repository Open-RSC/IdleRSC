package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;

/**
 * SpinStrings by Searos
 *
 * @author Searos and Kaila
 */
public class SpinStrings extends IdleScript {
  private final Controller c = Main.getController();
  private final JComboBox<String> item = new JComboBox<>(new String[] {"Flax", "Wool"});
  private final JComboBox<String> destination =
      new JComboBox<>(new String[] {"Falador", "Seers", "Crafting Guild"});
  private JFrame scriptFrame = null;
  private boolean guiSetup = false;
  private boolean scriptStarted = false;
  private final int[] bankerIds = {95, 224, 268, 485, 540, 617};
  private final int[] bankX = {289, 500, 346};
  private final int[] bankY = {571, 455, 608};
  private final int[] inputIds = {ItemId.FLAX.getId(), ItemId.WOOL.getId()};
  private final int[] outputIds = {ItemId.BOW_STRING.getId(), ItemId.BALL_OF_WOOL.getId()};
  private int input = -1;
  private int output = -1;
  private int totalString = 0;
  private int bankedString = 0;
  private int bankSelX = -1;
  private int bankSelY = -1;
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
      if (c.getNearestNpcByIds(bankerIds, false) != null) {
        if (c.getInventoryItemCount(input) == 0) {
          bankingLoop();
        }
        walkToSpot();
      }
      scriptStart();
    }
    return 1000; // start() must return a int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getNearestObjectById(121) != null && c.getInventoryItemCount(input) > 0) {
        c.setStatus("@gre@Spinning Flax");
        int[] spinningWheel = c.getNearestObjectById(121);
        if (destination.getSelectedIndex() == 0) {
          c.walkTo(spinningWheel[0] + 1, spinningWheel[1]);
        } else c.walkTo(spinningWheel[0], spinningWheel[1] - 1);
        c.useItemIdOnObject(spinningWheel[0], spinningWheel[1], input);
        c.waitForBatching(false);
      }
      // Checks for these actions inside the methods
      walkToBank();
      bankingLoop();
      walkToSpot();
    }
  }

  private void bankingLoop() {
    if (c.getInventoryItemCount(input) == 0) {
      c.setStatus("@gre@Out of Input items");
      if (!c.isInBank()) {
        c.setStatus("@gre@Opening Bank");
        c.openBank();
        K_kailaScript.waitForBankOpen();
      }
      // deposit all
      totalString = totalString + c.getInventoryItemCount(output);
      if (c.getInventoryItemCount(input) == 0 && c.isInBank()) {
        for (int itemId : c.getInventoryUniqueItemIds()) {
          if (itemId != 1263) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
        c.sleep(2000);
        bankedString = c.getBankItemCount(output);
        c.withdrawItem(input, 30);
        c.closeBank();
        c.sleep(640);
        c.setStatus("@gre@Finished Banking");
      }
    }
  }

  private void walkToSpot() {
    // Handle seers
    if (c.getInventoryItemCount(input) > 0) {
      c.setStatus("@gre@Walking to Spot");
      if (destination.getSelectedIndex() == 0) { // fally
        c.walkTo(297, 576);
      }
      if (destination.getSelectedIndex() > 0 // seers or craft guild
          && c.getNearestObjectById(121) == null) {
        int[][] walkToCoords = {{524, 462}, {349, 611}};
        int[][] ladderCoords = {{525, 462}, {349, 612}};
        c.setStatus("@gre@Walking to Ladder");
        c.walkTo(
            walkToCoords[destination.getSelectedIndex() - 1][0],
            walkToCoords[destination.getSelectedIndex() - 1][1]);
        c.setStatus("@gre@Going up the ladder");
        c.atObject(
            ladderCoords[destination.getSelectedIndex() - 1][0],
            ladderCoords[destination.getSelectedIndex() - 1][1]);
        while (c.isRunning() && c.currentY() < 1000) c.sleep(640);
      }
    }
  }

  private void walkToBank() {
    if (c.getInventoryItemCount(input) == 0) {
      c.setStatus("@gre@Walking to Bank");
      if (destination.getSelectedIndex() > 0
          && c.getNearestObjectById(121) != null) { // seers or craft
        int[][] walkToCoords = {{524, 1406}, {349, 1555}};
        int[][] ladderCoords = {{525, 1406}, {349, 1556}};
        c.setStatus("@gre@Walking to Ladder");
        c.walkTo(
            walkToCoords[destination.getSelectedIndex() - 1][0],
            walkToCoords[destination.getSelectedIndex() - 1][1]);
        c.setStatus("@gre@Going downstairs");
        c.atObject(
            ladderCoords[destination.getSelectedIndex() - 1][0],
            ladderCoords[destination.getSelectedIndex() - 1][1]);
        while (c.isRunning() && c.currentY() > 1000) c.sleep(640);
      }
      c.walkTo(bankSelX, bankSelY);
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("FlaxtoString");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          c.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos and Kaila");
          bankSelX = bankX[destination.getSelectedIndex()];
          bankSelY = bankY[destination.getSelectedIndex()];
          input = inputIds[item.getSelectedIndex()];
          output = outputIds[item.getSelectedIndex()];
          c.displayMessage("@red@FlaxtoString started");
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(item);
    scriptFrame.add(destination);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      c.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
      c.drawString("@red@Spin Strings @gre@by Searos and Kaila", 10, 21, 0xFFFFFF, 1);
      c.drawString("@red@Strings Spun: @yel@" + totalString, 10, 35, 0xFFFFFF, 1);
      c.drawString("@red@String in Bank: @yel@" + bankedString, 10, 49, 0xFFFFFF, 1);
    }
  }
}
