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
import models.entities.EquipSlotIndex;
import models.entities.SkillId;
import orsc.ORSCharacter;

/**
 * A basic thiever that supports most things in the game. Only supports banking in Ardougne at the
 * moment.
 *
 * @author Dvorak
 */
public class AIOThiever extends IdleScript {
  private final int[][][]
      tiles = { // {objectX, objectY, object Id}, {walkTo}, {walkTo}, {walkTo}, {walkTo}
    { // tea
      {1183, 91, 518}, // missing side data
      {93, 518},
      {90, 519}
    },
    { // gems  //best stalls first!
      {551, 599, 327}, // Gem Stall
      {552, 601}, // South
      {551, 601}, // South
      {550, 600}, // East
      {550, 599}, // East
      {551, 598}, // North
      {552, 598}, // North
      {553, 599}, // West
      {553, 600}, // West
    },
    { // silver
      {555, 593, 325},
      {556, 595}, // South
      {555, 595}, // South
      {554, 594}, // East
      {554, 593}, // East
      {555, 592}, // North
      {556, 592}, // North
      {557, 593}, // West
      {557, 594} // West
    },
    { // Bakers
      {544, 599, 322},
      {545, 601}, // South
      {544, 601}, // South
      {543, 600}, // East
      {543, 599}, // East
      {544, 598}, // North
      {545, 598}, // North
      {546, 599}, // West
      {546, 600} // West
    },
    { // Spices
      {544, 590, 326},
      {545, 592}, // South
      {544, 592}, // South
      {543, 591}, // East
      {543, 590}, // East
      {544, 589}, // North
      {545, 589}, // North
      {546, 590}, // West
      {546, 591} // West
    },
    { // Fur
      {551, 583, 324},
      {552, 585}, // South
      {551, 585}, // South
      {550, 584}, // East
      {550, 583}, // East
      {551, 582}, // North
      {552, 582}, // North
      {553, 583}, // West
      {553, 584}, // West
    },
    { // Silk
      {566, 594, 323},
      {567, 596}, // South
      {566, 596}, // South
      {565, 595}, // East
      {565, 594}, // East
      {566, 593}, // North
      {567, 593}, // North
      {568, 594}, // West
      {568, 595}, // West
    }
  };
  private final ArrayList<ThievingObject> objects =
      new ArrayList<ThievingObject>() {
        {
          add(new ThievingObject("Man", 11, true, false));
          add(new ThievingObject("Farmer", 63, true, false));
          add(new ThievingObject("Warrior", 86, true, false));
          add(new ThievingObject("Workman", 722, true, false));
          add(new ThievingObject("Rogue", 342, true, false));
          add(new ThievingObject("Guard (Ardy)", 321, true, false));
          add(new ThievingObject("Guard (Varrock)", 65, true, false));
          add(new ThievingObject("Knight", 322, true, false));
          add(new ThievingObject("Watchman", 574, true, false));
          add(new ThievingObject("Paladin", 323, true, false));
          add(new ThievingObject("Gnome", 592, true, false));
          add(new ThievingObject("Hero", 324, true, false));

          // index 12
          add(new ThievingObject("All Stalls (Ardougne)", 327, false, true));
          add(new ThievingObject("Tea Stall", 1183, false, true));
          add(new ThievingObject("Bakers Stall", 322, false, true));
          // add(new ThievingObject("Rock Cake Stall", , false, true)); //be my guest
          add(new ThievingObject("Silk Stall", 323, false, true));
          add(new ThievingObject("Fur Stall", 324, false, true));
          add(new ThievingObject("Silver Stall", 325, false, true));
          add(new ThievingObject("Spice Stall", 326, false, true));
          add(new ThievingObject("Gem Stall", 327, false, true));

          // index 20
          // add(new ThievingObject("10 Coin Chest", 327, false, true)); //who's gonna bother?
          add(new ThievingObject("Nature Rune Chest", 335, false, true));
          add(new ThievingObject("50 Coin Chest", 336, false, true));
          add(new ThievingObject("Hemenster Chest", 379, false, true));
        }
      };
  private final int[] lootIds = {10, 41, 333, 335, 330, 619, 38, 152, 612, 142, 161};
  private final int[] doorObjectIds = {60, 64};
  private final String[] bankSpots =
      new String[] {"None", "Ardougne Square", "Varrock West", "Varrock East"};
  private static final Controller c = Main.getController();
  private JFrame scriptFrame = null;
  private String bankSpot;
  private ThievingObject target = null;
  private int success = 0;
  private int failure = 0;
  private int fightMode = 0;
  private int eatingHealth = 0;
  private int foodWithdrawAmount = 0;
  private int locIndex = 1;
  private int randomSide = 1; // random number between 1 and 8 (inclusive)
  private int targetIndex;
  private final int GAME_TICK = 640;
  private final long startTimestamp = System.currentTimeMillis() / 1000L;
  private boolean guiSetup = false;
  private boolean scriptStarted = false;
  // private boolean goToOtherSide = false;

  public int start(String[] parameters) {
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      scriptStart();
    } else {
      if (parameters[0].isEmpty()) {
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

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      eat();
      if (c.getFightMode() != this.fightMode) c.setFightMode(this.fightMode);
      if (c.getInventoryItemCount(140) > 0) { // drop jugs from heroes
        c.setStatus("@red@Dropping empty jugs..");
        c.dropItem(c.getInventoryItemSlotIndex(140));
        c.sleep(GAME_TICK);
      }
      c.waitForBatching(false);
      if (!c.isInCombat()) {
        eat();
        if (target.isNpc) {
          // c.sleepHandler(98, true);
          ORSCharacter npc = c.getNearestNpcById(target.id, false);
          if (npc != null && npc.serverIndex > 0) {
            if (c.isEquipped(EquipSlotIndex.WEAPON.getId())) {
              c.log("Silly goose, looks like you have a weapon equipped, You should not wear");
              c.log(
                  "weapons when thieving, it severely drops xp rates for everyone, including you!");
              c.chatMessage("I did something bad and tried to wield a weapon while thieving");
              if (c.getInventoryItemCount() < 30) c.unequipItem(EquipSlotIndex.WEAPON.getId());
            }
            c.setStatus("@red@Stealing..");
            c.npcCommand1(npc.serverIndex);
            c.sleep(320);
          } else {
            c.setStatus("@red@Waiting for NPC to become available..");
            c.sleep(100);
          }
          // Stall Thieving
          // todo investiagte searching npc positions and selecting opposite them
        } else if (targetIndex > 11 && targetIndex < 20) {
          if (targetIndex == 12 || target.name.contains("All")) {
            for (int i = 1; i < tiles.length; i++) {
              if (c.getObjectAtCoord(tiles[i][0][0], tiles[i][0][1]) == tiles[i][0][2]) {
                locIndex = i;
                break;
              }
            }
          }
          if (c.getInventoryItemCount() < 30
              && c.getObjectAtCoord(tiles[locIndex][0][0], tiles[locIndex][0][1])
                  == tiles[locIndex][0][2]) { // Stall is "stocked"
            if ((c.currentX() != tiles[locIndex][randomSide][0] // not standing on stall tile
                && c.currentY() != tiles[locIndex][randomSide][1])) {
              c.walkTo(tiles[locIndex][randomSide][0], tiles[locIndex][randomSide][1]);
              c.sleep(320);
            }
            c.atObject(tiles[locIndex][0][0], tiles[locIndex][0][1]);
            int oldSide = randomSide;
            randomSide = (int) ((Math.random() * 8) + 1); // random number between 1 and 8
            if (randomSide == oldSide) randomSide = (int) ((Math.random() * 8) + 1);
            c.sleep(4 * GAME_TICK);
          }

          // Chest Thieving
        } else if (target.isObject) { // (if obj or chest thieving)
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

        // Banking
        if (!bankSpot.equals(bankSpots[0]) // not the "None" option
            && (c.getInventoryItemCount() == 30 || countFood() == 0)) {
          bankingLoop();
        }
      } else {
        c.setStatus("@red@Leaving combat..");
        leaveCombat();
      }
      c.sleep(100);
    }
  }

  private void bankingLoop() {
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

  private void eat() {
    if (c.getCurrentStat(c.getStatId("Hits")) <= eatingHealth
        || c.getCurrentStat(c.getStatId("Hits"))
            <= Math.min(c.getBaseStat(SkillId.HITS.getId()), 20)) {
      if (c.isInCombat()) leaveCombat();
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
      while (eatingHealth > 0 && bankSpot.equals(bankSpots[0]) && !ate) {
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

  private static void leaveCombat() {
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

  private int countFood() {
    int result = 0;
    for (int id : c.getFoodIds()) {
      result += c.getInventoryItemCount(id);
    }
    return result;
  }

  private void setupGUI() {
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    JLabel eatAtHpLabel = new JLabel("Eat at HP: (food is automatically detected)");
    JLabel eatAtHpLabel2 = new JLabel("Setting to 0 will do su***de thieving method.");
    JLabel thieveLabel = new JLabel("Select thieving option:");
    JTextField eatAtHpField =
        new JTextField(String.valueOf(Math.max(c.getBaseStat(c.getStatId("Hits")) - 20, 35)));
    JComboBox<String> targetField = new JComboBox<>();
    JLabel bankLabel = new JLabel("Select banking option:");
    JComboBox<String> doBankCombobox = new JComboBox<>(bankSpots);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount: (banking only)");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(0));
    JLabel weaponWarningLabel =
        new JLabel("NEVER WEAR WEAPONS for pickpocketing, only wear for stalls");
    JButton startScriptButton = new JButton("Start");

    for (ThievingObject obj : objects) {
      targetField.addItem(obj.name);
    }
    targetField.addActionListener( // set suggested values for option
        e -> {
          int index = targetField.getSelectedIndex();
          if (index == 13) doBankCombobox.setSelectedIndex(3); // set to var east
          if (index > 19) doBankCombobox.setSelectedIndex(0); // no banking for chests
          if (index == 5
              || index == 7
              || index == 9
              || (index != 13 && index >= 11 && index <= 19)) {
            doBankCombobox.setSelectedIndex(1); // set to ardy
            foodWithdrawAmountField.setText(String.valueOf(1));
          }
          if (index == 6) {
            doBankCombobox.setSelectedIndex(2); // set to var west
            foodWithdrawAmountField.setText(String.valueOf(1));
          }
        });

    startScriptButton.addActionListener(
        e -> {
          fightMode = fightModeField.getSelectedIndex();
          eatingHealth = Integer.parseInt(eatAtHpField.getText());
          target = objects.get(targetField.getSelectedIndex());
          targetIndex = targetField.getSelectedIndex();
          locIndex = targetIndex - 12;
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
    scriptFrame.add(eatAtHpLabel2);
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
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("You steal")) success++;
  }

  //  @Override
  //  public void questMessageInterrupt(String message) {
  //    if (message.contains("You pick") || message.contains("You steal")) success++;
  //    else if (message.contains("You fail")) failure++;
  //    else if (message.contains("Hey thats mine") || (message.contains("hands off there"))) {
  //      failure++;
  //      goToOtherSide = true;
  //    }
  //  }

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

  // Inner class for Thieving Object
  private static class ThievingObject {
    private final String name;
    private final int id;
    private final boolean isNpc;
    private final boolean isObject;

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
}
