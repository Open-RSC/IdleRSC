package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
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
  int randomSide = (int) (Math.random() * 10 + 1); // random number between 1 and 10
  final long startTimestamp = System.currentTimeMillis() / 1000L;
  int success = 0;
  int failure = 0;
  private final int GAME_TICK = 640;
  private boolean goToOtherSide = false;
  private String bankSpot;
  private String[] bankSpots =
      new String[] {"None", "Ardougne Square", "Varrock West", "Varrock East"};

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

      if (goToOtherSide) {
        int oldSide = randomSide;
        randomSide = (int) (Math.random() * 10 + 1); // random number between 1 and 10
        if (randomSide == oldSide) {
          randomSide = (int) (Math.random() * 10 + 1); // random number between 1 and 10
        }
        if (randomSide == oldSide) {
          randomSide = (int) (Math.random() * 10 + 1); // random number between 1 and 10
        }
        goToOtherSide = false;
        c.sleep(100);
      }

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
          if (c.getObjectAtCoord(91, 518) == 1183) {
            c.setStatus("@red@Stealing from tea stall..");
            if (randomSide < 5
                && (c.currentX() != 93 && c.currentY() != 518)) { // needs random sides
              c.walkTo(93, 518);
              c.sleep(GAME_TICK);
            } else if (randomSide >= 5 && (c.currentX() != 90 && c.currentY() != 519)) {
              c.walkTo(90, 519);
            }
            c.atObject(91, 518);
            c.sleep(4 * GAME_TICK);
          }
        } else if ((target.name.contains("Gems") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(551, 599) == 327) {
          c.setStatus("@red@Stealing from Gems stall..");
          if (randomSide <= 3
              && c.getObjectAtCoord(551, 599) == 327
              && (c.currentX() != 552 && c.currentY() != 601)) { // south
            c.walkTo(552, 601);
            c.sleep(GAME_TICK);
          } else if (randomSide >= 4
              && randomSide <= 6
              && c.getObjectAtCoord(551, 599) == 327
              && (c.currentX() != 553 && c.currentY() != 599)) { // west
            c.walkTo(553, 599);
            c.sleep(GAME_TICK);
          } else if (randomSide >= 7
              && randomSide <= 8
              && c.getObjectAtCoord(551, 599) == 327
              && (c.currentX() != 552 && c.currentY() != 598)) { // north
            c.walkTo(552, 598);
            c.sleep(GAME_TICK);
          } else if (randomSide > 8
              && c.getObjectAtCoord(551, 599) == 327
              && (c.currentX() != 550 && c.currentY() != 600)) { // east
            c.walkTo(552, 598);
            c.sleep(GAME_TICK);
          }
          c.atObject(551, 599);
          c.sleep(4 * GAME_TICK);
        } else if ((target.name.contains("Silver") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(555, 593) == 325) {
          c.setStatus("@red@Stealing from silver stall..");

          if (randomSide <= 3 // more weight for better sides
              && c.getObjectAtCoord(555, 593) == 325
              && (c.currentX() != 555 && c.currentY() != 592)) { // north
            c.walkTo(555, 592);
            c.sleep(GAME_TICK);
          } else if (randomSide >= 4
              && randomSide <= 6 // more weight for better sides
              && c.getObjectAtCoord(555, 593) == 325
              && (c.currentX() != 557 && c.currentY() != 594)) { // west
            c.walkTo(557, 594);
            c.sleep(GAME_TICK);
          } else if (randomSide >= 7
              && randomSide <= 8 // less weight
              && c.getObjectAtCoord(555, 593) == 325
              && (c.currentX() != 556 && c.currentY() != 596)) { // south
            c.walkTo(556, 595);
            c.sleep(GAME_TICK);
          } else if (randomSide > 8 // less weight
              && c.getObjectAtCoord(555, 593) == 325
              && (c.currentX() != 554 && c.currentY() != 593)) { // east
            c.walkTo(554, 593);
            c.sleep(GAME_TICK);
          }
          c.atObject(555, 593);
          c.sleep(4 * GAME_TICK);
        } else if ((target.name.contains("Bakers") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(544, 599) == 322) {
          c.setStatus("@red@Stealing from bakers stall..");
          if (randomSide <= 3
              && c.getObjectAtCoord(544, 599) == 322
              && (c.currentX() != 544 && c.currentY() != 601)) { // south
            c.walkTo(544, 601);
            c.sleep(GAME_TICK);
          } else if (randomSide >= 4
              && randomSide <= 6
              && c.getObjectAtCoord(544, 599) == 322
              && (c.currentX() != 543 && c.currentY() != 600)) { // east
            c.walkTo(543, 600);
          } else if (randomSide >= 7
              && randomSide <= 8
              && c.getObjectAtCoord(544, 599) == 322
              && (c.currentX() != 546 && c.currentY() != 599)) { // west
            c.walkTo(546, 599);
          } else if (randomSide > 8
              && c.getObjectAtCoord(544, 599) == 322
              && (c.currentX() != 544 && c.currentY() != 598)) { // north
            c.walkTo(543, 600);
          }
          c.atObject(544, 599);
          c.sleep(4 * GAME_TICK);
        } else if ((target.name.contains("Spices") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(544, 590) == 326) {
          c.setStatus("@red@Stealing from spices stall..");
          if (randomSide <= 3
              && c.getObjectAtCoord(544, 590) == 326
              && (c.currentX() != 543 && c.currentY() != 591)) { // east
            c.walkTo(543, 591);
            c.sleep(GAME_TICK);
          } else if (randomSide >= 4
              && randomSide <= 6
              && c.getObjectAtCoord(544, 590) == 326
              && (c.currentX() != 544 && c.currentY() != 589)) { // north
            c.walkTo(544, 589);
            c.sleep(GAME_TICK);
          } else if (randomSide >= 7
              && randomSide <= 8
              && c.getObjectAtCoord(544, 590) == 326
              && (c.currentX() != 544 && c.currentY() != 592)) { // south
            c.walkTo(544, 592);
            c.sleep(GAME_TICK);
          } else if (randomSide > 8
              && c.getObjectAtCoord(544, 590) == 326
              && (c.currentX() != 546 && c.currentY() != 590)) { // north
            c.walkTo(544, 592);
            c.sleep(GAME_TICK);
          }
          c.atObject(544, 590);
          c.sleep(4 * GAME_TICK);
        } else if ((target.name.contains("Fur") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(551, 583) == 324) {
          c.setStatus("@red@Stealing from fur stall..");
          if (randomSide <= 3
              && c.getObjectAtCoord(551, 583) == 324
              && (c.currentX() != 553 && c.currentY() != 584)) { // west
            c.walkTo(553, 584);
            c.sleep(GAME_TICK);
          } else if (randomSide >= 4
              && randomSide <= 6
              && c.getObjectAtCoord(551, 583) == 324
              && (c.currentX() != 552 && c.currentY() != 582)) { // north
            c.walkTo(552, 582);
            c.sleep(GAME_TICK);
          } else if (randomSide >= 7
              && randomSide <= 8
              && c.getObjectAtCoord(551, 583) == 324
              && (c.currentX() != 550 && c.currentY() != 583)) { // east
            c.walkTo(550, 583);
            c.sleep(GAME_TICK);
          } else if (randomSide > 8
              && c.getObjectAtCoord(551, 583) == 324
              && (c.currentX() != 552 && c.currentY() != 585)) { // south
            c.walkTo(552, 585);
            c.sleep(GAME_TICK);
          }
          c.atObject(551, 583);
          c.sleep(4 * GAME_TICK);
        } else if ((target.name.contains("Silk") || target.name.contains("All"))
            && c.getInventoryItemCount() < 30
            && c.getObjectAtCoord(566, 594) == 323) {
          c.setStatus("@red@Stealing from silk stall..");
          if (randomSide <= 3
              && c.getObjectAtCoord(566, 594) == 323
              && (c.currentX() != 566 && c.currentY() != 593)) { // north
            c.walkTo(566, 593);
            c.sleep(GAME_TICK);
          } else if (randomSide >= 4
              && randomSide <= 6
              && c.getObjectAtCoord(566, 594) == 323
              && (c.currentX() != 565 && c.currentY() != 594)) { // east
            c.walkTo(565, 594);
          } else if (randomSide >= 7
              && randomSide <= 8
              && c.getObjectAtCoord(566, 594) == 323
              && (c.currentX() != 568 && c.currentY() != 595)) { // west
            c.walkTo(567, 596);
          } else if (randomSide > 8
              && c.getObjectAtCoord(566, 594) == 323
              && (c.currentX() != 567 && c.currentY() != 596)) { // south
            c.walkTo(567, 596);
          }
          c.atObject(566, 594);
          c.sleep(4 * GAME_TICK);
        } else if (target.isObject || !target.name.contains("All")) { // (if obj or chest)
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
        if (!bankSpot.equals(bankSpots[0]) // not the "None" option
            && (c.getInventoryItemCount() == 30 || countFood() == 0)) {
          c.setStatus("@red@Banking...");
          // walk near to bank
          if (bankSpot.equals(bankSpots[1])) { // ardy
            if (c.currentY() < 590) c.walkTo(548, 589);
            c.walkTo(547, 607);
          } else if (bankSpot.equals(bankSpots[2])) { // var west
            c.walkTo(151, 507);
          } else if (bankSpot.equals(bankSpots[3])) { // var east
            c.walkTo(96, 509);
            c.walkTo(102, 509);
          }
          c.openBank();

          for (int itemId : c.getInventoryItemIds()) {
            if (c.getInventoryItemCount() > 0) {
              c.depositItem(itemId, c.getInventoryItemCount(itemId));
              c.sleep(GAME_TICK);
            }
          }
          c.sleep(2000); // Important, leave in

          for (int id : c.getFoodIds()) {
            if (c.getBankItemCount(id) > 0) {
              c.withdrawItem(id, foodWithdrawAmount);
              c.sleep(GAME_TICK);
              break;
            }
          }
          // walk back to thieve spots
          if (bankSpot.equals(bankSpots[1])) { // ardy
            c.walkTo(548, 605);
          } else if (bankSpot.equals(bankSpots[2])) { // var west
            c.walkTo(151, 507);
          } else if (bankSpot.equals(bankSpots[3])) { // var east
            c.walkTo(102, 509);
            c.walkTo(93, 511);
          }
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

      while (bankSpot.equals(bankSpots[0]) && !ate) {
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
    JLabel thieveLabel = new JLabel("Select thieving option:");
    JTextField eatAtHpField =
        new JTextField(String.valueOf(c.getBaseStat(c.getStatId("Hits")) / 2));
    JComboBox<String> targetField = new JComboBox<>();
    JLabel bankLabel = new JLabel("Select banking option:");
    JComboBox<String> doBankCombobox = new JComboBox<>(bankSpots);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount: (banking only)");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(0));
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
          bankSpot = bankSpots[doBankCombobox.getSelectedIndex()];

          if (!foodWithdrawAmountField.getText().isEmpty())
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
    scriptFrame.add(thieveLabel);
    scriptFrame.add(targetField);
    scriptFrame.add(bankLabel);
    scriptFrame.add(doBankCombobox);
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
    else if (message.contains("You fail")) failure++;
    else if (message.contains("Hey thats mine") || (message.contains("hands off there"))) {
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
