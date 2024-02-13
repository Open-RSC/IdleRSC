package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Cuts, fletches and drops bows of any log type.
 *
 * @author Dvorak
 */
public class PowerFletcha extends IdleScript {
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;

  FletchObject target = null;
  int fightMode = 0;
  int eatingHealth = 0;

  final int[] bowIds = {276, 277, 658, 659, 660, 661, 662, 663, 664, 665, 666, 667};

  int itemsFletched = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;

  static class FletchObject {
    final String name;
    final int treeId;
    final int logId;
    final int optionId;

    public FletchObject(String _name, int _treeId, int _logId, int _optionId) {
      name = _name;
      treeId = _treeId;
      logId = _logId;
      optionId = _optionId;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof FletchObject) {
        return ((FletchObject) o).name.equals(this.name);
      }

      return false;
    }
  }

  final ArrayList<FletchObject> objects =
      new ArrayList<FletchObject>() {
        {
          add(new FletchObject("Arrow Shafts", 1, 14, 0));
          add(new FletchObject("Shortbow", 1, 14, 1));
          add(new FletchObject("Longbow", 1, 14, 2));
          add(new FletchObject("Oak Shortbow", 306, 632, 0));
          add(new FletchObject("Oak Longbow", 306, 632, 1));
          add(new FletchObject("Willow Shortbow", 307, 633, 0));
          add(new FletchObject("Willow Longbow", 307, 633, 1));
          add(new FletchObject("Maple Shortbow", 308, 634, 0));
          add(new FletchObject("Maple Longbow", 308, 634, 1));
          add(new FletchObject("Yew Shortbow", 309, 635, 0));
          add(new FletchObject("Yew Longbow", 309, 635, 1));
          add(new FletchObject("Magic Shortbow", 310, 636, 0));
          add(new FletchObject("Magic Longbow", 310, 636, 1));
        }
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
      controller.setStatus("@yel@Waiting for start...");
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);

      for (int id : bowIds) {
        if (controller.getInventoryItemCount(id) > 0) {
          controller.setStatus("@yel@Dropping bows...");
          controller.dropItem(controller.getInventoryItemSlotIndex(id));
          controller.sleep(250);
        }
      }

      if (controller.getInventoryItemCount(target.logId) > 0) {
        controller.setStatus("@yel@Fletching...");
        controller.useItemOnItemBySlot(
            controller.getInventoryItemSlotIndex(13),
            controller.getInventoryItemSlotIndex(target.logId));
        controller.sleep(700);

        if (controller.getOptionMenuCount() > 2 && target.logId != 14)
          controller.optionAnswer(target.optionId + 1); // fix for RSC Coleslaw
        else controller.optionAnswer(target.optionId);

        controller.sleep(618);

        while (controller.isBatching()) controller.sleep(10);
      }

      int[] objCoords = controller.getNearestObjectById(target.treeId);
      if (objCoords != null) {
        controller.setStatus("@yel@Cutting wood..");
        controller.atObject(objCoords[0], objCoords[1]);
        controller.sleep(618);
        while (controller.isBatching()) controller.sleep(10);
      } else {
        controller.setStatus("@yel@Waiting for tree spawn..");
      }
    }
  }

  public void setupGUI() {
    JLabel headerLabel = new JLabel("Start with a knife, axe, and next to trees!");
    JComboBox<String> targetField = new JComboBox<>();
    JButton startScriptButton = new JButton("Start");

    for (FletchObject obj : objects) {
      targetField.addItem(obj.name);
    }

    startScriptButton.addActionListener(
        e -> {
          target = objects.get(targetField.getSelectedIndex());
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;

          controller.displayMessage("@red@PowerFletcha by Dvorak. Let's party like it's 2004!");
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(headerLabel);
    scriptFrame.add(targetField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("You carefully")) itemsFletched++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int fletchedPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        fletchedPerHr = (int) (itemsFletched * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 160, 21 + 14, 0x228B22, 128);
      controller.drawString("@yel@PowerFletcha @whi@by @yel@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@yel@Items fletched: @whi@"
              + String.format("%,d", itemsFletched)
              + " @yel@(@whi@"
              + String.format("%,d", fletchedPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
    }
  }
}
