package scripting.idlescript;

import static bot.Main.log;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A basic cooking script to use in Catherby.
 *
 * @author Dvorak - original script, Kaila - rewrite
 * @version 1.1 - Batch bar autotoggle bugfix still preserving uranium support
 */
public class AIOCooker extends IdleScript {
  final Controller c = Main.getController();
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  final long startTimestamp = System.currentTimeMillis() / 1000L;
  int success = 0;
  int failure = 0;
  FoodObject target = null;
  boolean dropBurnt = true;
  boolean gauntlets = true;

  final ArrayList<FoodObject> objects =
      new ArrayList<FoodObject>() {
        {
          add(new FoodObject("Chicken", 133, 132, 134)); // raw, cooked, burnt
          add(new FoodObject("Shrimp", 349, 350, 353));
          add(new FoodObject("Anchovies", 351, 352, 353));
          add(new FoodObject("Sardine", 351, 355, 360));
          add(new FoodObject("Salmon", 356, 357, 360));
          add(new FoodObject("Trout", 358, 359, 360));
          add(new FoodObject("Herring", 361, 362, 365));
          add(new FoodObject("Pike", 363, 364, 365));
          add(new FoodObject("Cod", 550, 551, 360)); // pointed
          add(new FoodObject("Mackerel", 552, 553, 365)); // not pointed
          add(new FoodObject("Tuna", 366, 367, 368));
          add(new FoodObject("Lobster", 372, 373, 374));
          add(new FoodObject("Swordfish", 369, 370, 371));
          add(new FoodObject("Bass", 554, 555, 368));
          add(new FoodObject("Shark", 545, 546, 547));
          add(new FoodObject("Sea Turtle", 1192, 1193, 1248));
          add(new FoodObject("Manta Ray", 1190, 1191, 1247));
        }
      };

  class FoodObject {
    String name;
    int rawId;
    int cookedId;
    int burntId;

    public FoodObject(String _name, int _rawId, int _cookedId, int _burntId) {
      name = _name;
      rawId = _rawId;
      cookedId = _cookedId;
      burntId = _burntId;
    }

    public FoodObject(String name) {
      for (FoodObject food : objects) {
        if (food.name.equalsIgnoreCase(name)) {
          name = food.name;
          rawId = food.rawId;
          cookedId = food.cookedId;
          burntId = food.burntId;
        }
      }
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof FoodObject) {
        return ((FoodObject) o).name.equals(this.name);
      }
      return false;
    }
  }
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    c.setBatchBarsOn();
    String[] splitParams = null;
    if (parameters != null && parameters[0].contains(" ")) {
      splitParams = parameters[0].split(" ");
    }
    if (splitParams == null || splitParams.length < 3) {
      if (!guiSetup) {
        setupGUI();
        guiSetup = true;
      }
      if (scriptStarted) {
        guiSetup = false;
        scriptStarted = false;
        log("Equivalent parameters: ");
        log(target.name + " " + dropBurnt + " " + gauntlets);
        scriptStart();
      }
    } else {
      try {
        target = new FoodObject(splitParams[0]);
        dropBurnt = Boolean.parseBoolean(splitParams[1]);
        gauntlets = Boolean.parseBoolean(splitParams[2]);
        scriptStart();
      } catch (Exception e) {
        log("Invalid parameters! Usage: ");
        log("foodname true true");
        c.stop();
      }
    }
    return 1000; // start() must return an int value now.
  }

  public void scriptStart() {

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount(target.rawId) == 0) {
        goToBank();
        bank();
        goToCook();
      } else {
        cook();
      }
      c.sleep(250);
    }
  }

  public void bank() {
    c.walkTo(439, 497);
    openDoor();
    c.openBank();
    c.sleep(600);

    if (c.isInBank()) {
      if (c.getInventoryItemCount(target.cookedId) > 0) {
        c.depositItem(target.cookedId, c.getInventoryItemCount(target.cookedId));
        c.sleep(250);
      }
      if (!this.dropBurnt) {
        if (c.getInventoryItemCount(target.burntId) > 0) {
          c.depositItem(target.burntId, c.getInventoryItemCount(target.burntId));
          c.sleep(250);
        }
      }
      if (this.gauntlets && c.getInventoryItemCount(700) == 0) {
        c.withdrawItem(700);
        c.sleep(250);
      }
      if (c.getInventoryItemCount(target.rawId) == 0) {
        c.withdrawItem(target.rawId, 30);
        c.sleep(250);
      }
      c.closeBank();
    }
    c.walkTo(439, 496);
    openDoor();
  }

  public void goToCook() {
    c.walkTo(435, 486);
    openCookDoor();
    if (gauntlets) {
      if (c.getInventoryItemCount(700) < 1) {
        c.displayMessage("@red@Please withdraw gauntlets. Stopping script.");
        c.stop();
      }
      if (!c.isEquipped(c.getInventoryItemSlotIndex(700))) {
        c.equipItem(c.getInventoryItemSlotIndex(700));
        c.sleep(618);
      }
    }
  }

  public void goToBank() {
    if (this.dropBurnt) {
      while (c.getInventoryItemCount(target.burntId) > 0) {
        c.dropItem(c.getInventoryItemSlotIndex(target.burntId));
        c.sleep(250);
      }
    }
    c.walkTo(435, 485);
    openCookDoor();
  }

  public void cook() {
    if (c.getInventoryItemCount(target.rawId) > 0) {
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (!c.isBatching()) {
        c.useItemIdOnObject(432, 480, target.rawId);
      }
      c.sleep(640);
      c.waitForBatching(false);
    }
  }

  public void openDoor() {
    while (c.getObjectAtCoord(439, 497) == 64) {
      c.atObject(439, 497);
      c.sleep(100);
    }
  }

  public void openCookDoor() {
    while (!c.isDoorOpen(435, 486)) {
      c.openDoor(435, 486);
    }
  }

  public void setupGUI() {
    JLabel headerLabel = new JLabel("Start in Catherby!");
    JComboBox<String> foodField = new JComboBox<>();
    JCheckBox dropBurntCheckbox = new JCheckBox("Drop Burnt?", false);
    JCheckBox gauntletsCheckbox = new JCheckBox("Cooking Gauntlets?", false);

    JButton startScriptButton = new JButton("Start");

    for (FoodObject obj : objects) {
      foodField.addItem(obj.name);
    }

    startScriptButton.addActionListener(
        e -> {
          dropBurnt = dropBurntCheckbox.isSelected();
          gauntlets = gauntletsCheckbox.isSelected();
          target = objects.get(foodField.getSelectedIndex());

          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;

          c.displayMessage("@red@AIOCooker by Dvorak. Let's party like it's 2004!");
          c.displayMessage("@red@Coleslaw: Recommend Batch bars be toggle on in settings!");
          c.displayMessage("@red@Coleslaw: This will slightly increase efficiency!");
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(headerLabel);
    scriptFrame.add(foodField);
    scriptFrame.add(dropBurntCheckbox);
    scriptFrame.add(gauntletsCheckbox);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("nicely cooked")) success++;
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("burn the")) failure++;
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
      c.drawString("@red@AIOCooker @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
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
