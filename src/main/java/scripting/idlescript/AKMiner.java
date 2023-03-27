package scripting.idlescript;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A basic mining script with banking for Al-Kharid.
 *
 * @author Dvorak
 */
public class AKMiner extends IdleScript {
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;

  MiningObject target = null;
  int fightMode = 0;
  int eatingHealth = 0;

  int[] oreIds = {150, 202, 151, 152, 153, 154, 155, 149, 157, 158, 159, 160, 383};

  long startTimestamp = (System.currentTimeMillis() / 1000L);
  int oresMined = 0;
  int oresInBank = 0;

  class MiningObject {
    String name;
    int rockId;

    public MiningObject(String _name, int _rockId) {
      name = _name;
      rockId = _rockId;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof MiningObject) {
        if (((MiningObject) o).name.equals(this.name)) {
          return true;
        }
      }

      return false;
    }
  }

  ArrayList<MiningObject> objects =
      new ArrayList<MiningObject>() {
        {
          add(new MiningObject("Copper", 100));
          add(new MiningObject("Tin", 104));
          add(new MiningObject("Iron", 102));
          add(new MiningObject("Silver", 195));
          add(new MiningObject("Coal", 110));
          add(new MiningObject("Gold", 112));
          add(new MiningObject("Mithril", 106));
          add(new MiningObject("Adamantite", 108));
        }
      };

  public int start(String parameters[]) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
      controller.setStatus("@red@Waiting for start..");
    }

    if (scriptStarted) {
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (controller.isRunning()) {
      if (controller.getInventoryItemCount() == 30) {
        walkToBank();
        bank();
        walkToMine();
      } else {

        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(10);

        controller.sleepHandler(98, true);
        int[] objCoord = controller.getNearestObjectById(target.rockId);
        if (objCoord != null) {
          controller.setStatus("@red@Mining!");
          controller.atObject(objCoord[0], objCoord[1]);
        } else {
          controller.setStatus("@red@Waiting for spawn...");
        }

        controller.sleep(618);
      }
    }
  }

  public void openDoor() {
    controller.setStatus("@red@Opening bank door..");
    while (controller.getObjectAtCoord(86, 695) == 64) {
      controller.atObject(86, 695);
      controller.sleep(100);
    }
  }

  public void walkToBank() {
    controller.setStatus("@red@Walking to bank..");
    controller.walkTo(71, 594);
    controller.walkTo(70, 609);
    controller.walkTo(71, 629);
    controller.walkTo(75, 646);
    controller.walkTo(79, 667);
    controller.walkTo(81, 683);
    controller.walkTo(86, 695);

    openDoor();
  }

  public void walkToMine() {
    controller.setStatus("@red@Walking to mine..");

    openDoor();

    controller.walkTo(86, 695);
    controller.walkTo(81, 683);
    controller.walkTo(79, 667);
    controller.walkTo(75, 646);
    controller.walkTo(71, 629);
    controller.walkTo(70, 606);
    controller.walkTo(71, 594);
  }

  public void bank() {

    controller.setStatus("@red@Banking...");

    controller.openBank();

    for (int ore : this.oreIds) {
      if (controller.getInventoryItemCount(ore) > 0) {
        controller.depositItem(ore, controller.getInventoryItemCount(ore));
        controller.sleep(1000);
        this.oresInBank = controller.getBankItemCount(ore);
      }
    }
  }

  public void setupGUI() {
    JLabel headerLabel = new JLabel("Start in Al-Kharid mine with your pickaxe!");
    JComboBox<String> targetField = new JComboBox<String>();
    JButton startScriptButton = new JButton("Start");

    for (MiningObject obj : objects) {
      targetField.addItem(obj.name);
    }

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            target = objects.get(targetField.getSelectedIndex());
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            scriptStarted = true;

            controller.displayMessage("@red@AKMiner by Dvorak. Let's party like it's 2004!");
          }
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(headerLabel);
    scriptFrame.add(targetField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You manage to")) oresMined++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int minedPerHr = 0;
      try {
        float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        float scale = (60 * 60) / timeRan;
        minedPerHr = (int) (oresMined * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 150, 21 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@AKMiner @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Ores mined: @whi@"
              + String.format("%,d", this.oresMined)
              + " @red@(@whi@"
              + String.format("%,d", minedPerHr)
              + "@red@/@whi@hr@red@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@red@Ores in bank: @whi@" + String.format("%,d", this.oresInBank),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
}
