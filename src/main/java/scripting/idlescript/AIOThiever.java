package scripting.idlescript;

import bot.Main;
import controller.Controller;
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
  private static final Controller c = Main.getController();
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;

  final int[] lootIds = {10, 41, 333, 335, 330, 619, 38, 152, 612, 142, 161};
  int[] doorObjectIds = {60, 64};

  final long startTimestamp = System.currentTimeMillis() / 1000L;
  int success = 0;
  int failure = 0;
  private final int GAME_TICK = 640;
  private boolean goToOtherSide = false;

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
    /**
     * Determines if this object's is equal to another object.
     *
     * @param obj the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof ThievingObject) {
        return ((ThievingObject) obj).name.equals(this.name);
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
      new ArrayList<ThievingObject>() {
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

          add(new ThievingObject("All Stalls (Ardougne)", 327, false, true));
          add(new ThievingObject("Tea Stall", 1183, false, true));
          add(new ThievingObject("Bakers Stall", 322, false, true));
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
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      scriptStart();
    } else {
      if (parameters[0].equals("")) {
        if (!guiSetup) {
          setupGUI();
          guiSetup = true;
          c.setStatus("@red@Waiting for start..");
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
          c.displayMessage("@red@AIOThiever by Dvorak. Let's party like it's 2004!");

        } catch (Exception e) {
          System.out.println("Could not parse parameters!");
          c.displayMessage("@red@Could not parse parameters!");
          c.stop();
        }
      }
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (c.isRunning()) {

      eat();

      if (c.getFightMode() != this.fightMode) c.setFightMode(this.fightMode);

      // for(int doorId : doorObjectIds) {
      //	int[] doorCoords = c.getNearestObjectById(doorId);
      //
      //	if(doorCoords != null){
      //		c.setStatus("@red@AIOThiever: Opening door...");
      ///		c.atObject(doorCoords[0], doorCoords[1]);
      //		c.sleep(5000);
      ///	} else {
      //		c.sleep(200);
      //	}
      // }

      if (c.getInventoryItemCount(140) > 0) { // drop jugs from heroes
        c.setStatus("@red@Dropping empty jugs..");
        c.dropItem(c.getInventoryItemSlotIndex(140));
        c.sleep(GAME_TICK);
      }

      while (c.isBatching()) c.sleep(GAME_TICK);

      if (!c.isInCombat()) {
        if (target.isNpc) {
          c.sleepHandler(98, true);
          ORSCharacter npc = c.getNearestNpcById(target.id, false);
          if (npc != null && npc.serverIndex > 0) {
            c.setStatus("@red@Stealing..");
            c.npcCommand1(npc.serverIndex);
            c.sleep(5);
          } else {
            c.setStatus("@red@Waiting for NPC to become available..");
            c.sleep(200);
          }
        } else if (target.name.contains("Tea") && c.getInventoryItemCount() < 30) {
          if (c.getObjectAtCoord(544, 599) == 322) { // fix coords
            c.setStatus("@red@Stealing from bakers stall..");
            if (goToOtherSide && (c.currentX() != 543 && c.currentY() != 600)) {

            } else if (!goToOtherSide
                && (c.currentX() != 543 && c.currentY() != 600)) { // fix coords
              c.walkTo(543, 600); // fix coords
            }
            c.atObject(544, 599); // fix coords
            c.sleep(GAME_TICK);
          }
        } else if ((target.name.contains("Silver") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(555, 593) == 325) {
          c.setStatus("@red@Stealing from silver stall..");
          if (goToOtherSide && (c.currentX() != 554 && c.currentY() != 593)) {
            goToOtherSide = false;
            c.walkTo(554, 593);
            c.sleep(GAME_TICK);
          } else if (!goToOtherSide
              && (c.currentX() != 557 && c.currentY() != 594)) { // walk out of guard range
            c.walkTo(557, 594);
            c.sleep(GAME_TICK);
          }
          c.atObject(555, 593);
          c.sleep(GAME_TICK);
        } else if ((target.name.contains("Gems") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(551, 599) == 327) {
          c.setStatus("@red@Stealing from Gems stall..");
          if (goToOtherSide && (c.currentX() != 552 && c.currentY() != 601)) {
            goToOtherSide = false;
            c.walkTo(552, 601);
            c.sleep(GAME_TICK);
          } else if (!goToOtherSide
              && (c.currentX() != 553 && c.currentY() != 599)) { // walk out of guard range
            c.walkTo(553, 599);
            c.sleep(GAME_TICK);
          }
          c.atObject(551, 599);
          c.sleep(GAME_TICK);
        } else if ((target.name.contains("Bakers") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(544, 599) == 322) {
          c.setStatus("@red@Stealing from bakers stall..");
          if (goToOtherSide && (c.currentX() != 544 && c.currentY() != 601)) {
            goToOtherSide = false;
            c.walkTo(544, 601);
            c.sleep(GAME_TICK);
          } else if (!goToOtherSide
              && (c.currentX() != 543 && c.currentY() != 600)) { // walk out of guard range
            c.walkTo(543, 600);
          }
          c.atObject(544, 599);
          c.sleep(GAME_TICK);
        } else if ((target.name.contains("Fur") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(551, 583) == 324) {
          c.setStatus("@red@Stealing from fur stall..");
          if (goToOtherSide && (c.currentX() != 553 && c.currentY() != 584)) {
            goToOtherSide = false;
            c.walkTo(553, 584);
            c.sleep(GAME_TICK);
          } else if (!goToOtherSide
              && (c.currentX() != 552 && c.currentY() != 582)) { // walk out of guard range
            c.walkTo(552, 582);
            c.sleep(GAME_TICK);
          }
          c.atObject(551, 583);
          c.sleep(GAME_TICK);
        } else if ((target.name.contains("Spices") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(544, 590) == 326) {
          c.setStatus("@red@Stealing from spices stall..");
          if (goToOtherSide && (c.currentX() != 543 && c.currentY() != 591)) {
            goToOtherSide = false;
            c.walkTo(543, 591);
            c.sleep(GAME_TICK);
          } else if (!goToOtherSide
              && (c.currentX() != 544 && c.currentY() != 589)) { // walk out of guard range
            c.walkTo(544, 589);
            c.sleep(GAME_TICK);
          }
          c.atObject(544, 590);
          c.sleep(GAME_TICK);
        } else if ((target.name.contains("Silk") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(566, 594) == 323) {
          c.setStatus("@red@Stealing from silk stall..");
          if (goToOtherSide && (c.currentX() != 566 && c.currentY() != 593)) {
            goToOtherSide = false;
            c.walkTo(566, 593);
            c.sleep(GAME_TICK);
          } else if (!goToOtherSide
              && (c.currentX() != 567 && c.currentY() != 596)) { // walk out of guard range
            c.walkTo(567, 596);
          }
          c.atObject(566, 594);
          c.sleep(GAME_TICK);
        } else if (target.isObject && !target.name.contains("All")) { // (if stall)
          int[] coords = c.getNearestObjectById(target.id);
          if (coords != null) {
            c.setStatus("@red@Stealing..");
            if (target.name.contains("Chest")) {
              c.atObject2(coords[0], coords[1]);
            } else {
              c.atObject(coords[0], coords[1]);
              c.sleep(GAME_TICK);
            }
          } else {
            c.setStatus("@red@Waiting for respawn..");
            c.sleep(GAME_TICK);
          }
        }
        if (doBank && (c.getInventoryItemCount() == 30 || countFood() == 0)) {
          c.setStatus("@red@Banking...");
          c.walkTo(548, 589);
          c.walkTo(547, 607);
          c.openBank();

          for (int id : lootIds) {
            if (c.getInventoryItemCount(id) > 0) {
              c.depositItem(id, c.getInventoryItemCount(id));
              c.sleep(GAME_TICK);
            }
          }

          for (int id : c.getFoodIds()) {
            if (c.getInventoryItemCount(id) > 0) {
              c.depositItem(id, c.getInventoryItemCount(id));
              c.sleep(GAME_TICK);
            }
          }

          for (int id : c.getFoodIds()) {
            if (c.getBankItemCount(id) > 0) {
              c.withdrawItem(id, foodWithdrawAmount);
              c.sleep(GAME_TICK);
              break;
            }
          }

          c.walkTo(548, 605);
        }
      } else {
        c.setStatus("@red@Leaving combat..");
        leaveCombat();
        c.sleep(GAME_TICK);
      }
      c.sleep(GAME_TICK);
    }
  }

  public void eat() {
    if (c.getCurrentStat(c.getStatId("Hits")) <= eatingHealth) {

      leaveCombat();
      c.setStatus("@red@Eating..");

      boolean ate = false;

      for (int id : c.getFoodIds()) {
        if (c.getInventoryItemCount(id) > 0) {
          c.itemCommand(id);
          c.sleep(700);
          ate = true;
          break;
        }
      }

      while (!doBank && !ate) {
        c.setStatus("@red@Logging out..");
        leaveCombat();
        c.setAutoLogin(false);
        c.logout();
        c.sleep(1000);

        if (!c.isLoggedIn()) {
          c.stop();
          return;
        }
      }
    }
  }

  public static void leaveCombat() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.isInCombat()) {
          c.setStatus("@red@Leaving combat..");
          c.walkToAsync(c.currentX(), c.currentY(), 1);
          c.sleep(640);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public int countFood() {
    int result = 0;
    for (int id : c.getFoodIds()) {
      result += c.getInventoryItemCount(id);
    }

    return result;
  }

  public void setupGUI() {
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    JLabel eatAtHpLabel = new JLabel("Eat at HP: (food is automatically detected)");
    JTextField eatAtHpField =
        new JTextField(String.valueOf(c.getBaseStat(c.getStatId("Hits")) / 2));
    JComboBox<String> targetField = new JComboBox<>();
    JCheckBox doBankCheckbox = new JCheckBox("Bank? (Ardougne Square only)");
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount: (Ardougne Square only)");
    JTextField foodWithdrawAmountField = new JTextField();
    JLabel weaponWarningLabel =
        new JLabel("Never wear weapon for pickpocketing, do wear for stalls");
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

          c.displayMessage("@red@AIOThiever by Dvorak. Let's party like it's 2004!");
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

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
    scriptFrame.add(weaponWarningLabel);
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
    else if (message.contains("You fail") || message.contains("hands off there")) failure++;
    if (message.contains("Hey thats mine")) {
      failure++;
      goToOtherSide = true;
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {

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

      c.drawBoxAlpha(7, 7, 160, 21 + 14 + 14 + 14, 0xFF0000, 128);
      c.drawString("@red@AIOThiever @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
      c.drawString(
          "@red@Successes: @whi@"
              + String.format("%,d", success)
              + " @red@(@whi@"
              + String.format("%,d", successPerHr)
              + "@red@/@whi@hr@red@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@red@Failures: @whi@" + String.format("%,d", failure), 10, 21 + 14 + 14, 0xFFFFFF, 1);
      c.drawString(
          "@red@Ratio: @whi@" + String.format("%.2f", ratio), 10, 21 + 14 + 14 + 14, 0xFFFFFF, 1);
    }
  }
}
