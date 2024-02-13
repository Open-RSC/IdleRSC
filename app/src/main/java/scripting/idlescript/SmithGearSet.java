package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * SmithGearSet by Searos
 *
 * @author Searos
 */
public class SmithGearSet extends IdleScript {
  JFrame scriptFrame = null;
  final JComboBox<String> barField =
      new JComboBox<>(new String[] {"Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite"});
  boolean guiSetup = false;
  boolean scriptStarted = false;
  int barsLeft = -1;
  int selectedBar = -1;
  int totalHelms = 0;
  int totalPlates = 0;
  int totalLegs = 0;
  int totalKites = 0;
  int totalBaxes = 0;
  int total2Hs = 0;
  boolean bankTime = true;
  long totalSetCount = 0;
  final int[] barType = {169, 170, 171, 173, 174, 408};
  final int[][] itemType = {
    {108, 117, 206, 124, 205, 76},
    {6, 8, 9, 2, 89, 77},
    {109, 118, 121, 129, 90, 78},
    {110, 119, 122, 130, 91, 79},
    {111, 120, 123, 131, 92, 80},
    {112, 401, 402, 404, 93, 81}
  };
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
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      while (bankTime && !controller.isInBank()) {
        totalSetCount =
            (controller.getBankItemCount(itemType[barField.getSelectedIndex()][0])
                + controller.getBankItemCount(itemType[barField.getSelectedIndex()][1])
                + controller.getBankItemCount(itemType[barField.getSelectedIndex()][2])
                + controller.getBankItemCount(itemType[barField.getSelectedIndex()][3])
                + controller.getBankItemCount(itemType[barField.getSelectedIndex()][4])
                + controller.getBankItemCount(itemType[barField.getSelectedIndex()][5]));
        controller.setStatus("Banking");
        controller.openBank();
        controller.sleep(1000);
        while (controller.getInventoryItemCount() > 1 && controller.isInBank()) {
          totalHelms =
              totalHelms
                  + controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][0]);
          totalPlates =
              totalPlates
                  + controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][1]);
          totalLegs =
              totalLegs
                  + controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][2]);
          totalKites =
              totalKites
                  + controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][3]);
          totalBaxes =
              totalBaxes
                  + controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][4]);
          total2Hs =
              total2Hs + controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][5]);
          for (int itemId : controller.getInventoryItemIds()) {
            if (itemId != 168 && itemId != 1263) {
              controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
            }
          }

          controller.sleep(429);
        }
        if (controller.getInventoryItemCount(168) < 1) {
          controller.withdrawItem(168, 1);
        }
        if (controller.getInventoryItemCount(408) < 1) {
          controller.withdrawItem(selectedBar, 29);
          controller.sleep(1000);
        }
        controller.closeBank();
        bankTime = false;
      }

      while (controller.getInventoryItemCount(selectedBar) > 1
          && !bankTime
          && (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][0])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][0]))
              <= (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][1])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][1]))) {
        controller.setStatus("Crafting Helm");
        controller.useItemIdOnObject(
            controller.getNearestObjectById(50)[0],
            controller.getNearestObjectById(50)[1],
            selectedBar);
        controller.sleep(640);
        if (controller.isInOptionMenu()) {
          controller.optionAnswer(1);
          controller.sleep(640);
          controller.optionAnswer(0);
          controller.sleep(640);
          controller.optionAnswer(1);
          controller.sleep(640);
          controller.optionAnswer(0);
        }
        controller.sleep(640);
      }
      while (controller.getInventoryItemCount(selectedBar) > 4
          && !bankTime
          && (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][1])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][1]))
              <= (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][2])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][2]))) {
        controller.setStatus("Crafting Plate");
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        controller.useItemIdOnObject(
            controller.getNearestObjectById(50)[0],
            controller.getNearestObjectById(50)[1],
            selectedBar);
        controller.sleep(640);
        if (controller.isInOptionMenu()) {
          controller.optionAnswer(1);
          controller.sleep(640);
          controller.optionAnswer(2);
          controller.sleep(640);
          controller.optionAnswer(2);
          controller.sleep(640);
          controller.optionAnswer(0);
        }
        controller.sleep(640);
      }
      while (controller.getInventoryItemCount(selectedBar) > 2
          && !bankTime
          && (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][2])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][2]))
              <= (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][3])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][3]))) {
        controller.setStatus("Crafting Legs");
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        controller.useItemIdOnObject(
            controller.getNearestObjectById(50)[0],
            controller.getNearestObjectById(50)[1],
            selectedBar);
        controller.sleep(640);
        if (controller.isInOptionMenu()) {
          controller.optionAnswer(1);
          controller.sleep(640);
          controller.optionAnswer(2);
          controller.sleep(640);
          controller.optionAnswer(3);
          controller.sleep(640);
          controller.optionAnswer(0);
        }
        controller.sleep(640);
      }
      while (controller.getInventoryItemCount(selectedBar) > 2
          && !bankTime
          && (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][3])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][3]))
              <= (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][4])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][4]))) {
        controller.setStatus("Crafting Kite");
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        controller.useItemIdOnObject(
            controller.getNearestObjectById(50)[0],
            controller.getNearestObjectById(50)[1],
            selectedBar);
        controller.sleep(640);
        if (controller.isInOptionMenu()) {
          controller.optionAnswer(1);
          controller.sleep(640);
          controller.optionAnswer(1);
          controller.sleep(640);
          controller.optionAnswer(1);
          controller.sleep(640);
          controller.optionAnswer(0);
        }
        controller.sleep(640);
      }
      while (controller.getInventoryItemCount(selectedBar) > 2
          && !bankTime
          && (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][4])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][4]))
              <= (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][5])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][5]))) {
        controller.setStatus("Crafting Baxe");
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        controller.useItemIdOnObject(
            controller.getNearestObjectById(50)[0],
            controller.getNearestObjectById(50)[1],
            selectedBar);
        controller.sleep(640);
        if (controller.isInOptionMenu()) {
          controller.optionAnswer(0);
          controller.sleep(640);
          controller.optionAnswer(3);
          controller.sleep(640);
          controller.optionAnswer(1);
          controller.sleep(640);
          controller.optionAnswer(0);
        }
        controller.sleep(640);
      }
      while (controller.getInventoryItemCount(selectedBar) > 2
          && !bankTime
          && (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][5])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][5]))
              <= (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][0])
                  + controller.getBankItemCount(itemType[barField.getSelectedIndex()][0]))) {
        controller.setStatus("Crafting 2H");
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        controller.useItemIdOnObject(
            controller.getNearestObjectById(50)[0],
            controller.getNearestObjectById(50)[1],
            selectedBar);
        controller.sleep(640);
        if (controller.isInOptionMenu()) {
          controller.optionAnswer(0);
          controller.sleep(640);
          controller.optionAnswer(2);
          controller.sleep(640);
          controller.optionAnswer(3);
          controller.sleep(640);
          controller.optionAnswer(0);
        }
        controller.sleep(640);
      }
      if (controller.getInventoryItemCount(selectedBar) < 5) {
        bankTime = true;
      }
    }
  }

  public void setupGUI() {
    JLabel header = new JLabel("Smithing");
    JButton startScriptButton = new JButton("Start");
    JLabel barLabel = new JLabel("Bar Type:");
    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          selectedBar = barType[barField.getSelectedIndex()];
          scriptStarted = true;
          controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
          controller.displayMessage("@red@SmithGearSet started");
        });
    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(barLabel);
    scriptFrame.add(barField);
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
      controller.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@Smith Gear Set @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      controller.drawString("@red@Helms Smithed: @yel@" + totalHelms, 10, 35, 0xFFFFFF, 1);
      controller.drawString("@red@Plates Smithed: @yel@" + totalPlates, 10, 49, 0xFFFFFF, 1);
      controller.drawString("@red@Legs Smithed: @yel@" + totalLegs, 10, 63, 0xFFFFFF, 1);
      controller.drawString("@red@Kites Smithed: @yel@" + totalKites, 10, 77, 0xFFFFFF, 1);
      controller.drawString("@red@Baxes Smithed: @yel@" + totalBaxes, 10, 91, 0xFFFFFF, 1);
      controller.drawString("@red@2Hs Smithed: @yel@" + total2Hs, 10, 105, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Sets in Bank: @yel@" + (totalSetCount / 6), 10, 105 + 14, 0xFFFFFF, 1);
    }
  }
}
