package scripting.idlescript;

import bot.Main;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.SkillId;

/**
 * <b>Berry Harvester</b>
 *
 * <p>Harvests Berries from Edgeville Monastery (Coleslaw Only).<br>
 * Start in yanille Bank with Herb Clippers or near Berries. <br>
 * Recommend level 89+ combat so warriors are non aggressive. <br>
 * This bot supports the "autostart" parameter to automatiically start the bot without gui.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 * @author Kaila
 */
public class K_AIOHarvester extends K_kailaScript {
  private boolean capeTeleport = false;
  private boolean bringFood = false;
  private boolean ate = true;
  private int harvestedItemInBank = 0;
  private int totalHarvestedCount = 0;
  /** 0 = grapes 1 = whiteberries */
  private int scriptSelect = -1;

  private int harvestItemId = -1;
  private int teleportItemId = -1;
  private int accessItemId = -1;
  private int harvestToolId = -1;
  private int harvestObjectId = -1;
  private boolean autoWalk = false;

  public K_AIOHarvester() {}

  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (parameters.length > 0 && !parameters[0].isEmpty()) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.displayMessage("Auto-starting, Picking Berries", 0);
        Main.setScriptStarted(true);
        Main.setGuiSetup(false);
      }
    }
    if (!Main.getGuiSetup()) {
      Main.setGuiSetup(true);
      setupGUI();
    }
    if (Main.getScriptStarted()) {
      Main.setGuiSetup(false);
      Main.setScriptStarted(false);
      startTime = System.currentTimeMillis();
      if (autoWalk) next_attempt = System.currentTimeMillis() + 5000L;
      if (scriptSelect == 0 && c.getBaseStat(c.getStatId("Agility")) < 77) endSession();
      c.displayMessage("@red@AIOHarvester ~ By @mag@Kaila");
      if (c.getBaseStat(SkillId.HARVESTING.getId()) == 99
          && !c.isItemIdEquipped(ItemId.HARVESTING_CAPE.getId())) {
        c.displayMessage("@red@Looks like you have 99 Harvesting");
        c.displayMessage("@red@Talk to Lily in Lumbridge to get a harvesting skill cape");
        c.displayMessage("@red@It will increase your harvesting yeild to wear it.");
      }
      if (c.isInBank()) c.closeBank();
      for (int bankerId : c.bankerIds) {
        if (c.getNearestNpcById(bankerId, false) != null) {
          bank();
          bankToSpot();
          break;
        }
      }
      c.toggleBatchBarsOn();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (bringFood) ate = eatFood();
      if (c.getInventoryItemCount() == 30 || timeToBank || (bringFood && !ate)) {
        c.setStatus("@red@Banking..");
        timeToBank = false;
        if (!ate) ate = true;
        spotToBank();
        bank();
        bankToSpot();
      }
      c.setStatus("@yel@Harvesting Items..");
      int[] coords = c.getNearestObjectById(harvestObjectId);
      if (coords != null) {
        c.setStatus("@yel@Harvesting...");
        c.atObject(coords[0], coords[1]);
        c.sleep(2000);
        while (c.isBatching()
            && c.getInventoryItemCount() != 30
            && (next_attempt == -1 || System.currentTimeMillis() < next_attempt)) {
          c.sleep(GAME_TICK);
        }
      } else {
        c.setStatus("@yel@Waiting for spawn..");
        c.sleep(GAME_TICK);
      }
      if (autoWalk && System.currentTimeMillis() > next_attempt) {
        c.log("@red@Walking to Avoid Logging!");
        c.walkTo(c.currentX() - 1, c.currentY(), 0, true);
        c.sleep(GAME_TICK);
        next_attempt = System.currentTimeMillis() + nineMinutesInMillis;
        long nextAttemptInSeconds = (next_attempt - System.currentTimeMillis()) / 1000L;
        c.log("Done Walking to not Log, Next attempt in " + nextAttemptInSeconds + " seconds!");
      }
    }
  }

  private void bank() { // works for all
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(GAME_TICK);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalHarvestedCount = totalHarvestedCount + c.getInventoryItemCount(harvestItemId);
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != teleportItemId && itemId != accessItemId && itemId != harvestToolId) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      if (capeTeleport) withdrawItem(teleportItemId, 1);
      if (accessItemId != -1 && c.getInventoryItemCount(accessItemId) < 1)
        withdrawItem(accessItemId, 1);
      if (c.getInventoryItemCount(harvestToolId) < 1) { // withdraw harvest tool
        if (c.getBankItemCount(harvestToolId) > 0) {
          c.withdrawItem(harvestToolId, 1);
          c.sleep(GAME_TICK);
        } else {
          c.displayMessage("@red@You need a harvesting tool for this!");
        }
      }
      if (bringFood) withdrawFood(foodId, foodWithdrawAmount);
      harvestedItemInBank = c.getBankItemCount(harvestItemId);
      c.closeBank();
      c.sleep(GAME_TICK);
      if (bringFood) eatFood();
    }
  }

  private void bankToSpot() {
    switch (scriptSelect) {
      case 0: // grapes
        bankToGrape();
        break;
      case 1: // whiteberries
        bankToBerry();
        break;
      default:
        throw new Error("unknown banking location");
    }
  }

  private void spotToBank() {
    switch (scriptSelect) {
      case 0: // grapes
        grapeToBank();
        break;
      case 1: // whiteberries
        berryToBank();
        break;
      default:
        throw new Error("unknown banking location");
    }
  }

  private void berryToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    if (capeTeleport && c.getInventoryItemCount(teleportItemId) != 0) {
      c.walkTo(608, 3568); // walkTo to exit batching
      teleportAgilityCape();
    } else {
      c.walkTo(608, 3568);
      c.atObject(607, 3568); // go through pipe  (make a loop)
      c.sleep(3000);
      c.walkTo(605, 3568);
      c.walkTo(603, 3568);
      c.walkTo(597, 3574);
      c.walkTo(597, 3581);
      c.sleep(300);
      c.atObject(598, 3582); // Rope Swing  (make a loop)
      c.sleep(3000);
      if (bringFood) eatFood();
      c.walkTo(595, 3585);
      c.walkTo(593, 3587);
      c.walkTo(593, 3589);
      c.setStatus("@gre@Picklocking Door..");
      yanilleDungeonDoorExiting(); // open door
      c.setStatus("@gre@Walking to Bank..");
      c.walkTo(594, 3593);
      c.atObject(591, 3593);
      c.sleep(2000);
      c.walkTo(591, 765);
    }
    c.walkTo(582, 767);
    c.walkTo(579, 763);
    c.walkTo(584, 754);
    c.walkTo(585, 752);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void bankToBerry() {
    c.setStatus("@gre@Walking to Berry bush..");
    if (capeTeleport && c.getInventoryItemCount(teleportItemId) != 0) {
      teleportAgilityCape();
    } else {
      c.walkTo(584, 754);
      c.walkTo(579, 763);
      c.walkTo(582, 767);
      c.walkTo(591, 765);
    }
    c.walkTo(590, 762);
    if (!Main.isRunning()) return;
    c.atObject(591, 761);
    c.sleep(2000);
    c.walkTo(593, 3590);
    c.setStatus("@gre@Picklocking Door..");
    yanilleDungeonDoorEntering();
    c.setStatus("@gre@Walking to Druids..");
    c.walkTo(594, 3587);
    c.walkTo(596, 3585);
    c.atObject(596, 3584); // Rope Swing  (make a loop)
    c.sleep(3000);
    c.setStatus("@gre@Done Walking..");
    c.walkTo(597, 3574);
    c.walkTo(603, 3568);
    c.walkTo(605, 3568);
    c.atObject(606, 3568); // go through pipe  (make a loop)
    c.sleep(3000);
    if (bringFood) eatFood();
    c.walkTo(611, 3569);
  }

  private void grapeToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(251, 454);
    c.walkTo(254, 454);
    c.walkTo(256, 451);
    c.walkTo(255, 444);
    c.walkTo(255, 433);
    c.walkTo(255, 422);
    c.walkTo(258, 422);
    if (bringFood) eatFood();
    c.walkTo(258, 415);
    c.walkTo(252, 421);
    c.walkTo(242, 432);
    c.walkTo(225, 432);
    c.walkTo(220, 437);
    c.walkTo(220, 445);
    c.walkTo(218, 447);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void bankToGrape() {
    c.setStatus("@gre@Walking to Grapes..");
    c.walkTo(218, 447);
    c.walkTo(220, 445);
    c.walkTo(220, 437);
    c.walkTo(225, 432);
    c.walkTo(242, 432);
    c.walkTo(252, 421);
    c.walkTo(258, 415);
    if (bringFood) eatFood();
    c.walkTo(258, 422);
    c.walkTo(255, 422);
    c.walkTo(255, 433);
    c.walkTo(255, 444);
    c.walkTo(256, 451);
    c.walkTo(254, 454);
    c.walkTo(251, 454);
    // (next to Grape now)
    c.setStatus("@gre@Done Walking..");
  }


  private void setupGUI() {

    final Panel checkboxes = new Panel(new GridLayout(0, 1));
    final Panel grapeInfobox = new Panel(new GridLayout(0, 1));
    final Panel wBerriesInfobox = new Panel(new GridLayout(0, 1));
    final Panel containerInfobox = new Panel(new GridLayout(0, 1));
    Font bold_title = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    Font small_info = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    scriptFrame = new JFrame(c.getPlayerName() + " - AIO Harvester");
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Add in the top infobox Stuff
    Label grapeLabel1 = new Label("Harvests Grapes near Edge Monastery");
    Label grapeLabel2 = new Label("*Start in Edge Bank with Herb Clippers");
    Label grapeLabel3 = new Label("*Recommend Armor against lvl 21 Scorpions");

    Label wBerriesLabel1 = new Label("Harvests Whiteberries in Yanille dungeon");
    Label wBerriesLabel2 = new Label("*Start in Yanille Bank with Herb Clippers and Lockpick");
    Label wBerriesLabel3 = new Label("*Recommend level 109+ combat so Skellies are non aggressive");
    Label wBerriesLabel4 = new Label("*Requires 77 agility to not fail rope swing");

    // Add top right choices
    Label scriptOptions_label = new Label("Script Options:", Label.CENTER);
    grapeInfobox.add(grapeLabel1);
    grapeInfobox.add(grapeLabel2);
    grapeInfobox.add(grapeLabel3);
    grapeLabel1.setFont(bold_title);
    grapeLabel2.setFont(small_info);
    grapeLabel3.setFont(small_info);
    wBerriesInfobox.add(wBerriesLabel1);
    wBerriesInfobox.add(wBerriesLabel2);
    wBerriesInfobox.add(wBerriesLabel3);
    wBerriesInfobox.add(wBerriesLabel4);
    wBerriesLabel1.setFont(bold_title);
    wBerriesLabel2.setFont(small_info);
    wBerriesLabel3.setFont(small_info);
    wBerriesLabel4.setFont(small_info);

    // add in the checkbox options on the right
    final Checkbox agilityCapeCheckBox = new Checkbox("99 Agility Cape Teleport?", true);
    final Checkbox bringFoodCheckBox = new Checkbox("Bring food?", false);
    final Checkbox doStuff = new Checkbox("doStuff?", false);
    final Label space_saver_a = new Label();
    final Label space_saver_b = new Label();
    final Label space_saver_c = new Label();
    final Label space_saver_d = new Label();

    // Introduce right side options panel fields
    Choice foodType = new Choice();
    for (String str : foodTypes) {
      foodType.add(str);
    }
    foodType.select(2);
    TextField foodAmountsField = new TextField(String.valueOf(1));
    foodType.setEnabled(false);
    foodAmountsField.setEnabled(false);

    // Add bottom right side options panel
    // Panel optionsPanel = new Panel(new GridLayout(2, 0));
    Label foodAmountsLabel = new Label("Food Amount:");
    Label foodTypeLabel = new Label("Food Type:");

    // constraints.fill = GridBagConstraints.HORIZONTAL;
    // constraints.anchor = GridBagConstraints.EAST; // bottom of space
    // constraints.weightx = 1.0; // request any extra horizontal space
    checkboxes.add(scriptOptions_label);
    scriptOptions_label.setFont(bold_title);
    checkboxes.add(agilityCapeCheckBox);
    checkboxes.add(bringFoodCheckBox);
    // checkboxes.add(doStuff);
    checkboxes.add(foodAmountsLabel);
    checkboxes.add(foodAmountsField);
    checkboxes.add(foodTypeLabel);
    checkboxes.add(foodType);
    checkboxes.add(space_saver_a);
    checkboxes.add(space_saver_b);
    checkboxes.add(space_saver_c);
    checkboxes.add(space_saver_d);
    // checkboxes.add(optionsPanel); // add the options panel section of checkboxes last

    // Action listeners to hide/show based on checkboxes
    bringFoodCheckBox.addItemListener(
        e -> {
          foodType.setEnabled(bringFoodCheckBox.getState());
          foodAmountsField.setEnabled(bringFoodCheckBox.getState());
        });
    // Add left side script select options
    final java.awt.List list = new java.awt.List();
    list.add("Grapes");
    list.add("Whiteberries");
    list.select(0);
    list.addItemListener(
        e -> {
          containerInfobox.invalidate();
          containerInfobox.remove(grapeInfobox);
          containerInfobox.remove(wBerriesInfobox);
          checkboxes.invalidate();
          checkboxes.remove(bringFoodCheckBox);
          checkboxes.remove(doStuff);
          checkboxes.remove(agilityCapeCheckBox);
          checkboxes.remove(foodAmountsLabel);
          checkboxes.remove(foodAmountsField);
          checkboxes.remove(foodTypeLabel);
          checkboxes.remove(foodType);
          checkboxes.remove(space_saver_a);
          checkboxes.remove(space_saver_b);
          checkboxes.remove(space_saver_c);
          checkboxes.remove(space_saver_d);

          switch (list.getSelectedItem()) {
            case "Grapes":
              containerInfobox.add(grapeInfobox);
              containerInfobox.remove(wBerriesInfobox);
              checkboxes.add(bringFoodCheckBox);
              checkboxes.add(doStuff);
              checkboxes.add(foodAmountsLabel);
              checkboxes.add(foodAmountsField);
              checkboxes.add(foodTypeLabel);
              checkboxes.add(foodType);
              checkboxes.add(space_saver_a);
              checkboxes.add(space_saver_b);
              checkboxes.add(space_saver_c);
              checkboxes.add(space_saver_d);
              containerInfobox.validate();
              checkboxes.validate();
              break;
            case "Whiteberries":
              containerInfobox.remove(grapeInfobox);
              containerInfobox.add(wBerriesInfobox);
              checkboxes.add(agilityCapeCheckBox);
              checkboxes.add(bringFoodCheckBox);
              checkboxes.add(foodAmountsLabel);
              checkboxes.add(foodAmountsField);
              checkboxes.add(foodTypeLabel);
              checkboxes.add(foodType);
              checkboxes.add(space_saver_a);
              checkboxes.add(space_saver_b);
              checkboxes.add(space_saver_c);
              checkboxes.add(space_saver_d);
              containerInfobox.validate();
              checkboxes.validate();
              break;
          }
          checkboxes.add(space_saver_a);
          checkboxes.add(space_saver_b);
          checkboxes.add(space_saver_c);
          checkboxes.add(space_saver_d);
          grapeInfobox.validate();
          checkboxes.validate();
        });

    // Add run button
    Button startButton = new Button("Start Script");
    startButton.addActionListener( // parse results and run
        new ActionListener() {
          private void set_ids() {
            bringFood = bringFoodCheckBox.getState();
            if (bringFood) {
              c.log("bringing food");
              if (!foodAmountsField.getText().isEmpty()) {
                foodWithdrawAmount = Integer.parseInt(foodAmountsField.getText());
              } else foodWithdrawAmount = 1;
              foodId = foodIds[foodType.getSelectedIndex()];
              foodName = foodTypes[foodType.getSelectedIndex()];
            }
            // assign item ids to harvest, paths, etc
            switch (list.getSelectedItem()) {
              case "Grapes":
                scriptSelect = 0;
                harvestItemId = ItemId.GRAPES.getId();
                harvestToolId = ItemId.HERB_CLIPPERS.getId();
                harvestObjectId = 1283;

                break;
              case "Whiteberries":
                scriptSelect = 1;
                harvestToolId = ItemId.HERB_CLIPPERS.getId();
                harvestItemId = ItemId.WHITE_BERRIES.getId();
                teleportItemId = ItemId.AGILITY_CAPE.getId();
                accessItemId = ItemId.LOCKPICK.getId();
                harvestObjectId = 1260;
                autoWalk = true;
                capeTeleport = true;

                break;
              default:
                throw new Error("unknown option");
            }
          }
          // @Override
          public void actionPerformed(ActionEvent e) {
            set_ids();
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            Main.setScriptStarted(true);
          }
        });

    // Add Cancel Button
    Button cancel = new Button("Cancel");
    cancel.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          Main.setScriptStarted(false);
          Main.setGuiSetup(false);
          Main.setRunning(false);
        });

    // Implement Run and Cancel Buttons
    Panel buttons = new Panel();
    buttons.add(startButton);
    buttons.add(cancel);

    // Arrange the Full Layout
    Panel middle = new Panel(new GridBagLayout());

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.weightx = 1.0; // request any extra horizontal space
    constraints.gridwidth = 2;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.ipady = 20; // make this component tall
    middle.add(containerInfobox, constraints);

    constraints.fill = GridBagConstraints.VERTICAL;
    constraints.weightx = 0.5; // request any extra horizontal space
    constraints.gridwidth = 1;
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.ipady = 0; // make this component tall
    middle.add(list, constraints);

    constraints.fill = GridBagConstraints.VERTICAL;
    constraints.weightx = 0.5; // request any extra horizontal space
    constraints.gridwidth = 1;
    constraints.gridx = 1;
    constraints.gridy = 1;
    middle.add(checkboxes, constraints);

    // only add the first top infobox option (from list)
    containerInfobox.add(grapeInfobox);

    // Setup window
    scriptFrame.setSize(370, 460); // was 415, 550?
    scriptFrame.setMinimumSize(scriptFrame.getSize());

    scriptFrame.add(middle, BorderLayout.CENTER);
    scriptFrame.add(buttons, BorderLayout.SOUTH);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void chatCommandInterrupt(
      String commandText) { // ::bank ::bones ::lowlevel :potup ::prayer
    if (commandText.contains("bank")) {
      c.displayMessage("@or1@Got @red@bank@or1@ command! Going to the Bank!");
      timeToBank = true;
      c.sleep(100);
    }
  }
  //todo add custom labels to on screen gui
  //todo locations[scriptSelect]
  private String[] locations = {"Grapes", "Whiteberries"};
  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      int TripSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (totalHarvestedCount * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@AIO Harvester @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Item Bank Count: @gre@" + harvestedItemInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Harvested Count: @gre@"
              + totalHarvestedCount
              + "@yel@ (@whi@"
              + String.format("%,d", successPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 4), 0xFFFFFF, 1);
      if (autoWalk) {
        long timeRemainingTillAutoWalkAttempt = next_attempt - System.currentTimeMillis();
        c.drawString(
            "@whi@Time till AutoWalk: " + c.msToShortString(timeRemainingTillAutoWalkAttempt),
            x,
            y + (14 * 5),
            0xFFFFFF,
            1);
        c.drawString("@whi@____________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
      } else {
        c.drawString("@whi@____________________", x, y + 3 + (14 * 4), 0xFFFFFF, 1);
      }
    }
  }
}
