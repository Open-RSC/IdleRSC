package scripting.idlescript;

import bot.Main;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.*;
import models.entities.ItemId;

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
public class K_HarvestClass extends K_kailaScript {
  private boolean capeTeleport = false;
  private boolean lowlevel = false;
  private boolean ate = true;
  private final int WHITE_BERRIES = ItemId.WHITE_BERRIES.getId();
  private final int AGILITY_CAPE = ItemId.AGILITY_CAPE.getId();
  private final int LOCKPICK = ItemId.LOCKPICK.getId();
  private final int HERB_CLIPPERS = ItemId.HERB_CLIPPERS.getId();
  private int BerryzInBank = 0;
  private int totalBerryz = 0;
  private ItemListener acquire_box_listener;
  private ItemListener dispose_box_listener;
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
      next_attempt = System.currentTimeMillis() + 5000L;
      c.displayMessage("@red@White Berry Harvester - By Kaila");
      c.displayMessage("@red@Start in yanille Bank or near Berries");
      if (c.getBaseStat(c.getStatId("Agility")) < 77) endSession();
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 3000) {
        bank();
        BankToBerry();
      }
      c.toggleBatchBarsOn();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (lowlevel) ate = eatFood();
      if (c.getInventoryItemCount() == 30 || timeToBank || (lowlevel && !ate)) {
        c.setStatus("@red@Banking..");
        if (!ate) ate = true;
        BerryToBank();
        bank();
        BankToBerry();
      }
      c.setStatus("@yel@Picking Berries..");
      int[] coords = c.getNearestObjectById(1260);
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
        c.sleep(640);
      }
      if (System.currentTimeMillis() > next_attempt) {
        c.log("@red@Walking to Avoid Logging!");
        c.walkTo(c.currentX() - 1, c.currentY(), 0, true);
        c.sleep(640);
        next_attempt = System.currentTimeMillis() + nineMinutesInMillis;
        long nextAttemptInSeconds = (next_attempt - System.currentTimeMillis()) / 1000L;
        c.log("Done Walking to not Log, Next attempt in " + nextAttemptInSeconds + " seconds!");
      }
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(GAME_TICK);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalBerryz = totalBerryz + c.getInventoryItemCount(WHITE_BERRIES);
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != AGILITY_CAPE && itemId != LOCKPICK && itemId != HERB_CLIPPERS) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      if (capeTeleport) withdrawItem(AGILITY_CAPE, 1);
      withdrawItem(LOCKPICK, 1);
      if (c.getInventoryItemCount(HERB_CLIPPERS) < 1) { // withdraw herb clippers
        if (c.getBankItemCount(HERB_CLIPPERS) > 0) {
          c.withdrawItem(HERB_CLIPPERS, 1);
          c.sleep(GAME_TICK);
        } else {
          c.displayMessage("@red@You need herb clippers!");
        }
      }
      if (lowlevel) withdrawFood(foodId, foodWithdrawAmount);
      BerryzInBank = c.getBankItemCount(WHITE_BERRIES);
      c.closeBank();
      if (lowlevel) eatFood();
    }
  }

  private void BerryToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    if (capeTeleport && c.getInventoryItemCount(AGILITY_CAPE) != 0) {
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
      if (lowlevel) eatFood();
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

  private void BankToBerry() {
    c.setStatus("@gre@Walking to Berry bush..");
    if (capeTeleport && c.getInventoryItemCount(AGILITY_CAPE) != 0) {
      teleportAgilityCape();
    } else {
      c.walkTo(584, 754);
      c.walkTo(579, 763);
      c.walkTo(582, 767);
      c.walkTo(591, 765);
    }
    c.walkTo(590, 762);
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
    if (lowlevel) eatFood();
    c.walkTo(611, 3569);
  }

  private void setupGUI() {

    final Panel checkboxes = new Panel(new GridLayout(0, 1));
    final Panel grapeInfobox = new Panel(new GridLayout(0, 1));
    final Panel wBerriesInfobox = new Panel(new GridLayout(0, 1));
    final Panel containerInfobox = new Panel(new GridLayout(0, 1));
    Font bold_title = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    Font small_info = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    // final java.util.List<Checkbox> acquire_boxes = new ArrayList<>();
    // final CheckboxGroup acquire_group = new CheckboxGroup();
    scriptFrame = new JFrame(c.getPlayerName() + " - AIO_Harvester");
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
    checkboxes.add(doStuff);
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
    Button ok = new Button("Start Script");
    ok.addActionListener( // parse results and run
        new ActionListener() {
          private void set_ids() {
            lowlevel = bringFoodCheckBox.getState();
            if (lowlevel) {
              if (!foodAmountsField.getText().isEmpty()) {
                foodWithdrawAmount = Integer.parseInt(foodAmountsField.getText());
              } else {
                foodWithdrawAmount = 1;
              }
              foodId = foodIds[foodType.getSelectedIndex()];
              foodName = foodTypes[foodType.getSelectedIndex()];
            }
            switch (list.getSelectedItem()) {
              case "Grapes":

                // assign item ids to harvest, paths, etc
              case "Whiteberries":
                // do stuff
              default:
                throw new Error("unknown option");
            }
          }

          @Override
          public void actionPerformed(ActionEvent e) {
            set_ids();

            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            Main.setGuiSetup(false);
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
    buttons.add(ok);
    buttons.add(cancel);
    buttons.add(new Label("\t"));
    buttons.add(new Label("by Kaila"));

    // Arrange the Full Layout
    Panel middle = new Panel(new GridBagLayout());

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    // constraints.anchor = GridBagConstraints.PAGE_START; // bottom of space
    constraints.weightx = 1.0; // request any extra horizontal space
    constraints.gridwidth = 2;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.ipady = 20; // make this component tall
    middle.add(containerInfobox, constraints);

    constraints.fill = GridBagConstraints.VERTICAL;
    // constraints.anchor = GridBagConstraints.WEST; // bottom of space
    // constraints.gridheight = 3;
    constraints.weightx = 0.5; // request any extra horizontal space
    constraints.gridwidth = 1;
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.ipady = 0; // make this component tall
    middle.add(list, constraints);

    constraints.fill = GridBagConstraints.VERTICAL;
    // constraints.anchor = GridBagConstraints.EAST; // bottom of space
    constraints.weightx = 0.5; // request any extra horizontal space
    constraints.gridwidth = 1;
    constraints.gridx = 1;
    constraints.gridy = 1;
    middle.add(checkboxes, constraints);

    containerInfobox.add(grapeInfobox); // only add the first list option

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
    } else if (commandText.contains("cape")) {
      if (!capeTeleport) {
        c.displayMessage("@or1@Got toggle @red@Agility Cape@or1@, turning on cape teleport!");
        capeTeleport = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@Agility Cape@or1@, turning off cape teleport!");
        capeTeleport = false;
      }
      c.sleep(100);
    }
  }

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
        successPerHr = (int) (totalBerryz * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Berry Harvester @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Berries in Bank: @gre@" + BerryzInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Berries Picked: @gre@"
              + totalBerryz
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
      long timeRemainingTillAutoWalkAttempt = next_attempt - System.currentTimeMillis();
      c.drawString(
          "@whi@Time till AutoWalk: " + c.msToShortString(timeRemainingTillAutoWalkAttempt),
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
    }
  }
}
