package scripting.idlescript;

import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import orsc.ORSCharacter;

/**
 * A basic thiever that supports most things in the game. Only supports banking in Ardougne at the
 * moment.
 *
 * @author Dvorak
 */
public class AIOThiever extends IdleScript {
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;

  final int[] lootIds = {10, 41, 333, 335, 330, 619, 38, 152, 612, 142, 161};
  int[] doorObjectIds = {60, 64};

  final long startTimestamp = System.currentTimeMillis() / 1000L;
  int success = 0;
  int failure = 0;

  static class ThievingObject {
    final String name;
    final int id;
    final boolean isNpc;
    final boolean isObject;

    public ThievingObject(String _name, int _id, boolean _isNpc, boolean _isObject) {
      name = _name;
      id = _id;
      isNpc = _isNpc;
      isObject = _isObject;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof ThievingObject) {
        return ((ThievingObject) o).name.equals(this.name);
      }

      return false;
    }
  }

  ThievingObject target = null;
  int fightMode = 0;
  int eatingHealth = 0;
  boolean doBank = false;
  int foodWithdrawAmount = 0;

  final ArrayList<ThievingObject> objects =
      new ArrayList<>() {
        {
          add(new ThievingObject("Man", 11, true, false));
          add(new ThievingObject("Farmer", 63, true, false));
          add(new ThievingObject("Warrior", 86, true, false));
          add(new ThievingObject("Workman", 722, true, false));
          add(new ThievingObject("Rogue", 342, true, false));
          add(new ThievingObject("Guard", 321, true, false));
          add(new ThievingObject("Guard (Varrock)", 65, true, false));
          add(new ThievingObject("Knight", 322, true, false));
          add(new ThievingObject("Watchman", 574, true, false));
          add(new ThievingObject("Paladin", 323, true, false));
          add(new ThievingObject("Gnome", 592, true, false));
          add(new ThievingObject("Hero", 324, true, false));

          add(new ThievingObject("Tea Stall", 1183, false, true));
          add(new ThievingObject("Bakers Stall", 322, false, true));
          add(new ThievingObject("Bakers Stall (Banking)", 322, false, true));
          // add(new ThievingObject("Rock Cake Stall", , false, true)); //be my guest
          add(new ThievingObject("Silk Stall", 323, false, true));
          add(new ThievingObject("Fur Stall", 324, false, true));
          add(new ThievingObject("Silver Stall", 325, false, true));
          add(new ThievingObject("Spice Stall", 326, false, true));
          add(new ThievingObject("Gem Stall", 327, false, true));

          // add(new ThievingObject("10 Coin Chest", 327, false, true)); //who's gonna bother?
          add(new ThievingObject("Nature Rune Chest", 335, false, true));
          add(new ThievingObject("50 Coin Chest", 336, false, true));
          add(new ThievingObject("Hemenster Chest", 379, false, true));
        }
      };

  public int start(String[] parameters) {
    if (scriptStarted) {
      scriptStart();
    } else {
      if (parameters[0].equals("")) {
        if (!guiSetup) {
          setupGUI();
          guiSetup = true;
          controller.setStatus("@red@Waiting for start..");
        }
      } else {
        try {
          fightMode = Integer.parseInt(parameters[0]);
          eatingHealth = Integer.parseInt(parameters[1]);

          for (ThievingObject obj : objects) {
            if (obj.name.equals(parameters[2])) target = obj;
          }

          doBank = Boolean.parseBoolean(parameters[3]);
          foodWithdrawAmount = Integer.parseInt(parameters[4]);

          if (target == null) throw new Exception("Could not parse thieving target!");

          scriptStarted = true;
          controller.displayMessage("@red@AIOThiever by Dvorak. Let's party like it's 2004!");

        } catch (Exception e) {
          System.out.println("Could not parse parameters!");
          controller.displayMessage("@red@Could not parse parameters!");
          controller.stop();
        }
      }
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (controller.isRunning()) {

      eat();

      if (controller.getFightMode() != this.fightMode) controller.setFightMode(this.fightMode);

      // for(int doorId : doorObjectIds) {
      //	int[] doorCoords = controller.getNearestObjectById(doorId);
      //
      //	if(doorCoords != null){
      //		controller.setStatus("@red@AIOThiever: Opening door...");
      ///		controller.atObject(doorCoords[0], doorCoords[1]);
      //		controller.sleep(5000);
      ///	} else {
      //		controller.sleep(200);
      //	}
      // }

      if (controller.getInventoryItemCount(140) > 0) { // drop jugs from heroes
        controller.setStatus("@red@Dropping empty jugs..");
        controller.dropItem(controller.getInventoryItemSlotIndex(140));
        controller.sleep(500);
      }

      while (controller.isBatching()) controller.sleep(10);

      if (!controller.isInCombat()) {
        if (target.isNpc) {
          controller.sleepHandler(98, true);
          ORSCharacter npc = controller.getNearestNpcById(target.id, false);
          if (npc != null && npc.serverIndex > 0) {
            controller.setStatus("@red@Stealing..");
            controller.npcCommand1(npc.serverIndex);
            controller.sleep(5);
          } else {
            controller.setStatus("@red@Waiting for NPC to become available..");
            controller.sleep(200);
          }
        }

        if (doBank) {
          if (target.name.contains("Bakers")) {
            if (controller.getInventoryItemCount() < 30) {
              controller.setStatus("@red@Stealing..");

              if (controller.currentX() != 543 && controller.currentY() != 600)
                controller.walkTo(543, 600);

              controller.atObject(544, 599);
            }
          }

          if (controller.getInventoryItemCount() == 30 || countFood() == 0) {
            controller.setStatus("@red@Banking...");
            controller.walkTo(548, 589);
            controller.walkTo(547, 607);
            controller.openBank();

            for (int id : lootIds) {
              if (controller.getInventoryItemCount(id) > 0) {
                controller.depositItem(id, controller.getInventoryItemCount(id));
                controller.sleep(500);
              }
            }

            for (int id : controller.getFoodIds()) {
              if (controller.getInventoryItemCount(id) > 0) {
                controller.depositItem(id, controller.getInventoryItemCount(id));
                controller.sleep(500);
              }
            }

            for (int id : controller.getFoodIds()) {
              if (controller.getBankItemCount(id) > 0) {
                controller.withdrawItem(id, foodWithdrawAmount);
                controller.sleep(500);
                break;
              }
            }

            controller.walkTo(548, 605);
          }

        } else { // we are not banking
          if (target.isObject) {
            int[] coords = controller.getNearestObjectById(target.id);
            if (coords != null) {
              controller.setStatus("@red@Stealing..");
              if (target.name.contains("Chest")) {
                controller.atObject2(coords[0], coords[1]);
              } else {
                controller.atObject(coords[0], coords[1]);
                controller.sleep(200);
              }
            } else {
              controller.setStatus("@red@Waiting for respawn..");
              controller.sleep(800);
            }
          }
        }
      } else {
        controller.setStatus("@red@Leaving combat..");
        leaveCombat();
        controller.sleep(400);
      }
      controller.sleep(250);
    }
  }

  public void eat() {
    if (controller.getCurrentStat(controller.getStatId("Hits")) <= eatingHealth) {

      leaveCombat();
      controller.setStatus("@red@Eating..");

      boolean ate = false;

      for (int id : controller.getFoodIds()) {
        if (controller.getInventoryItemCount(id) > 0) {
          controller.itemCommand(id);
          controller.sleep(700);
          ate = true;
          break;
        }
      }

      while (!doBank && !ate) {
        controller.setStatus("@red@Logging out..");
        leaveCombat();
        controller.setAutoLogin(false);
        controller.logout();
        controller.sleep(1000);

        if (!controller.isLoggedIn()) {
          controller.stop();
          return;
        }
      }
    }
  }

  public void leaveCombat() {
    for (int i = 1; i <= 15; i++) {
      if (controller.isInCombat()) {
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
        controller.sleep(400);
      }
      controller.sleep(10);
    }
  }

  public int countFood() {
    int result = 0;
    for (int id : controller.getFoodIds()) {
      result += controller.getInventoryItemCount(id);
    }

    return result;
  }

  public void setupGUI() {
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    JLabel eatAtHpLabel = new JLabel("Eat at HP: (food is automatically detected)");
    JTextField eatAtHpField =
        new JTextField(String.valueOf(controller.getBaseStat(controller.getStatId("Hits")) / 2));
    JComboBox<String> targetField = new JComboBox<>();
    JCheckBox doBankCheckbox = new JCheckBox("Bank? (Ardougne Square only)");
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount: (Ardougne Square only)");
    JTextField foodWithdrawAmountField = new JTextField();
    JButton startScriptButton = new JButton("Start");

    for (ThievingObject obj : objects) {
      targetField.addItem(obj.name);
    }

    startScriptButton.addActionListener(
        e -> {
          fightMode = fightModeField.getSelectedIndex();
          eatingHealth = Integer.parseInt(eatAtHpField.getText());
          target = objects.get(targetField.getSelectedIndex());
          doBank = doBankCheckbox.isSelected();

          if (!foodWithdrawAmountField.getText().equals(""))
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());

          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;

          controller.displayMessage("@red@AIOThiever by Dvorak. Let's party like it's 2004!");
        });

    scriptFrame = new JFrame(controller.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(fightModeLabel);
    scriptFrame.add(fightModeField);
    scriptFrame.add(eatAtHpLabel);
    scriptFrame.add(eatAtHpField);
    scriptFrame.add(targetField);
    scriptFrame.add(doBankCheckbox);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("You steal")) success++;
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You pick") || message.contains("You steal")) success++;
    else if (message.contains("You fail")
        || message.contains("Hey thats mine")
        || message.contains("hands off there")) failure++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int successPerHr = 0;
      float ratio = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (success * scale);
        ratio = (float) success / (float) failure;
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 160, 21 + 14 + 14 + 14, 0xFF0000, 128);
      controller.drawString("@red@AIOThiever @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Successes: @whi@"
              + String.format("%,d", success)
              + " @red@(@whi@"
              + String.format("%,d", successPerHr)
              + "@red@/@whi@hr@red@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@red@Failures: @whi@" + String.format("%,d", failure), 10, 21 + 14 + 14, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Ratio: @whi@" + String.format("%.2f", ratio), 10, 21 + 14 + 14 + 14, 0xFFFFFF, 1);
    }
  }
}
