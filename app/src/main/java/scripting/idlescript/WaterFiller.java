package scripting.idlescript;

import bot.Main;
import java.awt.*;
import javax.swing.*;
import models.entities.ItemId;

/**
 * Fills vials/jugs in Falador
 *
 * @author Dvorak and Kaila
 */
public class WaterFiller extends K_kailaScript {
  int itemsFilled = 0;
  int fullItems = 0;
  int emptyItems = 0;
  private int emptyId = 0;
  private int fullId = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    c.setBatchBarsOn();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      c.displayMessage("@red@VialFiller by Dvorak + Kaila. Let's party like it's 2004!");
      c.displayMessage("@red@Start in Falador West bank!");

      guiSetup = false;
      scriptStarted = false;
      if (c.isInBank()) c.closeBank();
      startTime = System.currentTimeMillis();
      scriptStart();
    }
    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount(emptyId) > 0) {
        c.useItemIdOnObject(327, 545, emptyId);
        c.sleep(618);
        c.waitForBatching(false);
      } else {
        openDoor();
        bank();
        openDoor();
      }
    }
  }

  public void openDoor() {
    int[] coords = null;
    do {
      coords = c.getNearestObjectById(64);
      if (coords != null) {
        c.atObject(coords[0], coords[1]);
        c.sleep(618);
      }
    } while (coords != null);
  }

  public void bank() {
    c.setStatus("@blu@Banking..");

    c.openBank();

    itemsFilled += c.getInventoryItemCount(fullId);

    for (int itemId : c.getInventoryItemIds()) {
      c.depositItem(itemId, c.getInventoryItemCount(itemId));
    }

    while (c.isInBank() && c.getInventoryItemCount(emptyId) < 1) {
      c.withdrawItem(emptyId, 30 - c.getInventoryItemCount());
      c.sleep(100);
    }

    fullItems = c.getBankItemCount(fullId);
    emptyItems = c.getBankItemCount(emptyId);
  }

  private void setupGUI() {
    JLabel header = new JLabel("Water Filler - Dvorak plus Kaila");
    JLabel optionsLabel = new JLabel("Handles vials, jugs, buckets, and bowls");
    JLabel startLabel = new JLabel("Start in Fally west Bank!");
    JLabel batchLabel = new JLabel("Bot will toggle on batch bars");
    JLabel itemLabel = new JLabel("Item Type:");
    JComboBox<String> itemField =
        new JComboBox<>(new String[] {"Vials", "Jugs", "Buckets", "Bowls"});
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (itemField.getSelectedIndex() == 0) {
            c.log("The varrock apothecary can fill all vials for a small fee.");
            emptyId = ItemId.EMPTY_VIAL.getId();
            fullId = ItemId.VIAL.getId();
          } else if (itemField.getSelectedIndex() == 1) {
            emptyId = ItemId.JUG.getId();
            fullId = ItemId.JUG_OF_WATER.getId();
          } else if (itemField.getSelectedIndex() == 2) {
            emptyId = ItemId.BUCKET.getId();
            fullId = ItemId.BUCKET_OF_WATER.getId();
          } else if (itemField.getSelectedIndex() == 3) {
            emptyId = ItemId.BOWL.getId();
            fullId = ItemId.BOWL_OF_WATER.getId();
          }
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });
    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(optionsLabel);
    scriptFrame.add(startLabel);
    scriptFrame.add(batchLabel);
    scriptFrame.add(itemLabel);
    scriptFrame.add(itemField);
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

      int itemsPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        itemsPerHr = (int) (itemsFilled * scale);
      } catch (Exception e) {
        // divide by zero
      }

      c.drawBoxAlpha(7, 7, 160, 21 + 14 + 14 + 14, 0x0000FF, 48);
      c.drawString("@blu@WaterFiller", 10, 21, 0xFFFFFF, 1);
      c.drawString(
          "@blu@Items filled: @whi@"
              + String.format("%,d", itemsFilled)
              + " @blu@(@whi@"
              + String.format("%,d", itemsPerHr)
              + "@blu@/@whi@hr@blu@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@blu@Full items in bank: @whi@" + String.format("%,d", fullItems),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@blu@Empty items in bank: @whi@" + String.format("%,d", emptyItems),
          10,
          21 + 14 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
}
