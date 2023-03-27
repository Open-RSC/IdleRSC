package scripting.idlescript;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * SpinStrings by Searos
 *
 * @author Searos
 */
public class SpinStrings extends IdleScript {
  JComboBox<String> item = new JComboBox<String>(new String[] {"Flax", "Wool"});
  JComboBox<String> destination = new JComboBox<String>(new String[] {"Seers", "Falador"});
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  int[] bankerIds = {95, 224, 268, 485, 540, 617};
  int[] bankX = {500, 289};
  int[] bankY = {455, 571};
  int[] inputIds = {675, 145};
  int[] outputIds = {676, 207};
  int input = -1;
  int output = -1;
  int totalString = 0;
  int bankedString = 0;
  boolean upstairs = false;
  boolean started = false;
  int bankSelX = -1;
  int bankSelY = -1;

  public int start(String parameters[]) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void startWalking(int x, int y) {
    // shitty autowalk
    int newX = x;
    int newY = y;
    while (controller.currentX() != x || controller.currentY() != y) {
      if (controller.currentX() - x > 23) {
        newX = controller.currentX() - 20;
      }
      if (controller.currentY() - y > 23) {
        newY = controller.currentY() - 20;
      }
      if (controller.currentX() - x < -23) {
        newX = controller.currentX() + 20;
      }
      if (controller.currentY() - y < -23) {
        newY = controller.currentY() + 20;
      }
      if (Math.abs(controller.currentX() - x) <= 23) {
        newX = x;
      }
      if (Math.abs(controller.currentY() - y) <= 23) {
        newY = y;
      }
      if (!controller.isTileEmpty(newX, newY)) {
        controller.walkToAsync(newX, newY, 2);
        controller.sleep(640);
      } else {
        controller.walkToAsync(newX, newY, 0);
        controller.sleep(640);
      }
    }
  }

  public void scriptStart() {
    while (controller.isRunning()) {
      // if player is near spinning wheel, not batching, and not banking, and seer's village
      // selected. spin material.
      while (controller.getNearestObjectById(121) != null
          && controller.getInventoryItemCount(input) > 0
          && destination.getSelectedIndex() == 0
          && controller.getNearestObjectById(121) != null) {
        controller.setStatus("@red@Spinning Flax");
        // if not batching use material on wheel
        while (!controller.isBatching() && controller.getInventoryItemCount(input) > 0) {
          // sleep if you have high fatigue
          controller.sleepHandler(98, true);
          controller.useItemIdOnObject(
              controller.getNearestObjectById(121)[0],
              controller.getNearestObjectById(121)[1],
              input);
          controller.sleep(640);
        }
        // if batching do nothing
        while (controller.isBatching()) {
          controller.sleep(340);
        }
      }
      // if player is near spinning wheel, not batching, and not banking, and falador selected. spin
      // flax.
      while (destination.getSelectedIndex() == 1
          && controller.getNearestObjectById(121) != null
          && controller.getInventoryItemCount(input) > 0) {
        controller.setStatus("@red@Spinning Flax");
        // if not batching use material on wheel
        while (!controller.isBatching() && controller.getInventoryItemCount(input) > 0) {
          // sleep if you have high fatigue
          controller.sleepHandler(98, true);
          controller.useItemIdOnObject(
              controller.getNearestObjectById(121)[0],
              controller.getNearestObjectById(121)[1],
              input);
          controller.sleep(640);
        }
        // if batching do nothing
        while (controller.isBatching()) {
          controller.sleep(340);
        }
      }
      // if player is not near spinning wheel and has materials, walk to spinning wheel
      // if player is in seer's village walk to ladder and go up it
      while (controller.getNearestObjectById(121) == null
          && controller.getInventoryItemCount(input) > 0
          && destination.getSelectedIndex() == 0) {
        controller.setStatus("@red@Going upstairs");
        // if the ladder at 525,462 is unloaded walk closer to it
        while (controller.isTileEmpty(525, 462)) {
          startWalking(524, 463);
        }
        // if the ladder is loaded climb up
        while (!controller.isTileEmpty(525, 462)) {
          controller.atObject(525, 462);
          controller.sleep(640);
        }
        return;
      }
      // if player is in falador, walk to the spinning wheel
      while (controller.getInventoryItemCount(input) > 0
          && destination.getSelectedIndex() == 1
          && controller.getNearestObjectById(121) == null) {
        startWalking(577, 295);
      }
      // when player has no more materials left, walk to the bank
      // go down ladder and walk to seer's village bank
      while (controller.getNearestObjectById(121) != null
          && controller.getInventoryItemCount(input) == 0
          && destination.getSelectedIndex() == 0) {
        controller.setStatus("@red@Going downstairs");
        controller.atObject(525, 1406);
        controller.sleep(100);
      }

      while (controller.getNearestNpcByIds(bankerIds, false) == null
          && destination.getSelectedIndex() == 1) {
        controller.setStatus("@red@No banker visible, walking closer...");
        controller.walkTo(291, 573);
      }

      // walk to falador bank
      while (controller.getNearestNpcByIds(bankerIds, false) == null
          && controller.getInventoryItemCount(input) == 0) {
        controller.setStatus("@red@Walking to bank");
        startWalking(bankSelX, bankSelY);
      }

      // open bank
      controller.setStatus("@red@Banking");
      while (!controller.isInBank()
          && controller.getNearestNpcByIds(bankerIds, false) != null
          && controller.getInventoryItemCount(input) == 0) {
        controller.openBank();
      }
      // deposit anything that is not a sleeping bag
      totalString = totalString + controller.getInventoryItemCount(output);
      while (controller.getInventoryItemCount(input) == 0
          && controller.getInventoryItemCount() > 1
          && controller.isInBank()) {
        for (int itemId : controller.getInventoryUniqueItemIds()) {
          if (itemId != 0 && itemId != 1263) {
            controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
            controller.sleep(618);
          }
        }
      }
      bankedString = controller.getBankItemCount(output);
      // withdraw materials
      controller.withdrawItem(input, 30 - controller.getInventoryItemCount());
      controller.sleep(618);
      controller.closeBank();
      controller.setStatus("@red@Finished Banking");
    }
  }

  public void setupGUI() {
    JLabel header = new JLabel("FlaxtoString");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            scriptStarted = true;
            controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
            bankSelX = bankX[destination.getSelectedIndex()];
            bankSelY = bankY[destination.getSelectedIndex()];
            input = inputIds[item.getSelectedIndex()];
            output = outputIds[item.getSelectedIndex()];
            controller.displayMessage("@red@FlaxtoString started");
          }
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(item);
    scriptFrame.add(destination);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      controller.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@Spin Strings @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Strings Spun: @yel@" + String.valueOf(this.totalString), 10, 35, 0xFFFFFF, 1);
      controller.drawString(
          "@red@String in Bank: @yel@" + String.valueOf(this.bankedString), 10, 49, 0xFFFFFF, 1);
    }
  }
}
