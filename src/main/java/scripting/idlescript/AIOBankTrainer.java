package scripting.idlescript;

import bot.Main;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.SceneryId;
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
public class AIOBankTrainer extends K_kailaScript {
  final String[] locations = {"Fletching", "Gems", "Bones"};
  private boolean teleportBanking = false;
  private boolean bringFood = false;
  private boolean ate = true;
  private int processedItemInBank = 0;
  private int totalProcessedCount = 0;
  /**
   *
   *
   * <pre>
   * 0 = grapes
   * 1 = whiteberries
   * </pre>
   */
  private int scriptSelect = 0;

  private int[] accessItemId;
  private int[] accessItemAmount;
  private int primaryItemId = 0;
  private int primaryItemAmount = -1;
  private int secondaryItemId = -1;
  private int secondaryItemAmount = -1;
  private int dialogOption = -1;
  private int resultItemId = -1;
  private int harvestToolId = -1;
  private int harvestObjectId = -1;
  private boolean autoWalk = false;

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
        scriptStarted = true;
        guiSetup = false;
      }
    }
    if (!guiSetup) {
      guiSetup = true;
      setupGUI();
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      if (autoWalk) next_attempt = System.currentTimeMillis() + 5000L;
      if (scriptSelect == 6 && c.getBaseStat(c.getStatId("Agility")) < 77) endSession();
      c.displayMessage("@red@AIOHarvester ~ By @mag@Kaila");
      if (c.getBaseStat(SkillId.HARVESTING.getId()) == 99
          && !c.isItemIdEquipped(ItemId.HARVESTING_CAPE.getId())) {
        c.displayMessage("@red@Looks like you have 99 Harvesting");
        c.displayMessage("@red@Talk to Lily in Lumbridge to get a harvesting skill cape");
        c.displayMessage("@red@It will increase your harvesting yeild to wear it.");
      }
      if (c.isInBank()) c.closeBank();
      for (int bankerId : c.bankerIds) {
        if (c.getNearestNpcById(bankerId, false) != null || c.getNearestObjectById(942) != null) {
          bank();
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
        bank();
      }
      // drop empty water skins
      if (c.getInventoryItemCount(1085) > 0) c.dropItem(c.getInventoryItemSlotIndex(1085));
      if (!harvestLoop()) { // if nothing to harvest
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

  private boolean harvestLoop() {
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
      return true;
    }
    return false;
  }

  private void bank() { // works for all
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(GAME_TICK);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalProcessedCount = totalProcessedCount + c.getInventoryItemCount(resultItemId);
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != harvestToolId) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      for (int i = 0; i < accessItemId.length; i++) {
        if (accessItemId[i] != 0
            && c.getInventoryItemCount(accessItemId[i]) < accessItemAmount[i]) {
          withdrawItem(accessItemId[i], accessItemAmount[i]);
        }
      }
      if (c.getInventoryItemCount(harvestToolId) < 1) { // withdraw harvest tool
        if (c.getBankItemCount(harvestToolId) > 0) {
          c.withdrawItem(harvestToolId, 1);
          c.sleep(GAME_TICK);
        } else {
          c.displayMessage("@red@You need a harvesting tool for this!");
        }
      }
      if (bringFood) withdrawFood(foodId, foodWithdrawAmount);
      processedItemInBank = c.getBankItemCount(resultItemId);
      c.closeBank();
      c.sleep(GAME_TICK);
      if (bringFood) eatFood();
    }
  }

  private void setupGUI() {

    final Panel checkboxes = new Panel(new GridLayout(0, 1));
    final Panel fletchInfobox = new Panel(new GridLayout(0, 1));
    final Panel gemInfobox = new Panel(new GridLayout(0, 1));
    final Panel boneInfobox = new Panel(new GridLayout(0, 1));

    final Panel cocoInfobox = new Panel(new GridLayout(0, 1));
    final Panel dfInfobox = new Panel(new GridLayout(0, 1));
    final Panel jangerInfobox = new Panel(new GridLayout(0, 1));
    final Panel wBerriesInfobox = new Panel(new GridLayout(0, 1));

    final Panel containerInfobox = new Panel(new GridLayout(0, 1));
    Font bold_title = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    Font small_info = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    scriptFrame = new JFrame(c.getPlayerName() + " - AIO Bank Trainer");
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    Label scriptOptions_label = new Label("Script Options:", Label.CENTER);

    // Add in the top infobox Stuff
    Panel[] infoBoxes = {
      fletchInfobox, gemInfobox, boneInfobox, cocoInfobox, dfInfobox, jangerInfobox, wBerriesInfobox
    };
    Label[][] allLabels = {
      { // fletching
        new Label("Fletching Script ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Start in any bank with an empty inventory")
      },
      { // gem cutting
        new Label("Gem Cutting Script ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Start in any bank with an empty inventory")
      },
      { // bone bury
        new Label("Fast Bone Bury ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Start in any bank with an empty inventory")
      },
      { // coco
        new Label("Gem Cutting Script ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Start in any bank with an empty inventory")
      },
      { // df
        new Label("Gem Cutting Script ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Start in any bank with an empty inventory")
      },
      { // janger
        new Label("Gem Cutting Script ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Start in any bank with an empty inventory")
      },
      { // wber
        new Label("Gem Cutting Script ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Start in any bank with an empty inventory")
      },
    };
    // Set up our infoboxes and format
    for (int i = 0; i < allLabels.length; i++) {
      for (int j = 0; j < allLabels[i].length; j++) {
        infoBoxes[i].add(allLabels[i][j]);
        if (j == 0) {
          allLabels[i][j].setFont(bold_title);
        } else allLabels[i][j].setFont(small_info);
      }
    }

    // add in the checkbox options on the right
    final Checkbox agilityCapeCheckBox = new Checkbox("99 Agility Cape Teleport?", true);
    final Checkbox ardyTeleCheckBox = new Checkbox("Teleport to Ardy?", false);
    final Checkbox lumbTeleCheckBox = new Checkbox("Teleport to Lumbridge?", false);
    final Checkbox bringFoodCheckBox = new Checkbox("Bring food?", false);
    final Checkbox doStuff = new Checkbox("doStuff?", false);

    final Label[] comboBoxLabel1 = {
      new Label("Log Type:"), new Label("Gem Type:"), new Label("Bone Type:")
    };
    final String[][] comboField1 = {
      { // logs
        "Log", "Oak", "Willow", "Maple", "Yew", "Magic"
      },
      { // gems
        "Sapphire", "Emerald", "Ruby", "Diamond", "Dragonstone", "Opal", "Jade", "Topaz"
      },
      { // bones
        "Normal Bones", "Big Bones", "Bat Bones", "Dragon Bones"
      }
    };
    final Label[] comboBoxLabel2 = {new Label("Select Fletching Type:")};
    final String[][] comboField2 = {
      { // fletching
        "Fletch Longbows",
        "String Longbows",
        "Make Arrow Shafts",
        "Fletch Shortbows(less xp)",
        "String Shortbows"
      },
    };

    // Combo box options. Change string comboLabel1, string[] comboField1, etc
    // Label comboBoxLabel1 = comboLabel1[0];
    // Label[] comboBoxLabel1 = new Label[] {new Label(), new Label(), new Label()};
    //    for (int i = 0; i < comboLabel1.length; i++) {
    //        c.log("setting");
    //        comboBoxLabel1[i].add(str);
    //    }
    Choice[] comboBoxField1 = new Choice[] {new Choice(), new Choice(), new Choice()};
    for (int i = 0; i < comboBoxField1.length; i++) {
      for (String str : comboField1[i]) {
        c.log("setting");
        comboBoxField1[i].add(str);
      }
    }
    Choice[] comboBoxField2 = new Choice[] {new Choice()};
    for (int i = 0; i < comboBoxField2.length; i++) {
      for (String str : comboField2[i]) {
        c.log("setting");
        comboBoxField1[i].add(str);
      }
    }
    // initialzite for fletching
    //    Label comboBoxLabel2 = new Label(comboLabel2[0]);
    //    Choice comboBoxField2 = new Choice();
    //    for (String str : comboField2[0]) {
    //      comboBoxField2.add(str);
    //    }

    //    final Label foodTypeLabel = new Label("Food Type:");
    //    Choice foodType = new Choice();
    //    for (String str : foodTypes) {
    //      foodType.add(str);
    //    }
    final Label space_saver_a = new Label();
    final Label space_saver_b = new Label();
    final Label space_saver_c = new Label();
    final Label space_saver_d = new Label();

    Checkbox[] checkboxList =
        new Checkbox[] {
          agilityCapeCheckBox, ardyTeleCheckBox, lumbTeleCheckBox, bringFoodCheckBox, doStuff
        };

    // set up initial/default panel (fletching)
    checkboxes.add(scriptOptions_label);
    scriptOptions_label.setFont(bold_title);
    checkboxes.add(comboBoxLabel1[0]);
    checkboxes.add(comboBoxField1[0]);
    checkboxes.add(comboBoxLabel2[0]);
    checkboxes.add(comboBoxField2[0]);
    checkboxes.add(space_saver_a);
    checkboxes.add(space_saver_b);
    checkboxes.add(space_saver_c);
    checkboxes.add(space_saver_d);
    // checkboxes.add(optionsPanel); // add the options panel section of checkboxes last

    // Action listeners to hide/show based on checkboxes
    bringFoodCheckBox.addItemListener(
        e -> {
          // comboBoxField2.setEnabled(bringFoodCheckBox.getState());
          // comboBoxField1[].setEnabled(bringFoodCheckBox.getState());
        });
    // Add left side script select options
    final java.awt.List list = new java.awt.List();
    list.add("Fletching");
    list.add("Gem Cutting");
    list.add("Bone Bury");
    // list.add("Coconuts");
    // list.add("Dragonfruit");
    // list.add("Jangerberries");
    // list.add("Whiteberries");
    // list.add("Tav Limps/Snapes");
    // list.add("Ardy Limps/Snapes");
    // list.add("Tav Herbs");
    // list.add("Corn");
    // list.add("Red Cabbage");
    // list.add("White Pumpkin");
    list.select(0);
    list.addItemListener(
        e -> {
          containerInfobox.invalidate();
          checkboxes.invalidate();

          for (Panel infobox : infoBoxes) {
            containerInfobox.remove(infobox);
          }
          for (Checkbox checkbox : checkboxList) {
            checkboxes.remove(checkbox);
          }
          for (Label label : comboBoxLabel1) {
            checkboxes.remove(label);
          }
          for (Choice comboSet : comboBoxField1) {
            checkboxes.remove(comboSet);
          }
          for (Label label : comboBoxLabel2) {
            checkboxes.remove(label);
          }
          for (Choice comboSet : comboBoxField2) {
            checkboxes.remove(comboSet);
          }
          checkboxes.remove(space_saver_a);
          checkboxes.remove(space_saver_b);
          checkboxes.remove(space_saver_c);
          checkboxes.remove(space_saver_d);

          switch (list.getSelectedIndex()) {
            case 0: // "Fletching"
              containerInfobox.add(fletchInfobox);
              checkboxes.add(comboBoxLabel1[0]);
              checkboxes.add(comboBoxField1[0]);
              checkboxes.add(comboBoxLabel2[0]);
              checkboxes.add(comboBoxField2[0]);
              break;
            case 1: // "Gem Cutting"
              containerInfobox.add(gemInfobox);

              checkboxes.add(comboBoxLabel1[1]);
              checkboxes.add(comboBoxField1[1]);
              break;
            case 2: // "Bone Bury"
              containerInfobox.add(boneInfobox);

              checkboxes.add(comboBoxLabel1[2]);
              checkboxes.add(comboBoxField1[2]);
              break;

            case 3: // "Coconuts"
              containerInfobox.add(cocoInfobox);
              checkboxes.add(ardyTeleCheckBox);
              break;
            case 4: // "Dragonfruit"
              containerInfobox.add(dfInfobox);
              // checkboxes.add(lumbTeleCheckBox);
              checkboxes.add(bringFoodCheckBox);
              checkboxes.add(comboBoxLabel1[1]);
              checkboxes.add(comboBoxField1[1]);
              break;
            case 5: // "Jangerberries"
              containerInfobox.add(jangerInfobox);
              break;
            case 6: // "Whiteberries"
              containerInfobox.add(wBerriesInfobox);
              checkboxes.add(agilityCapeCheckBox);
              checkboxes.add(bringFoodCheckBox);
              break;
          }
          checkboxes.add(space_saver_a);
          checkboxes.add(space_saver_b);
          checkboxes.add(space_saver_c);
          checkboxes.add(space_saver_d);
          containerInfobox.validate();
          checkboxes.validate();
        });

    // Add run button
    Button startButton = new Button("Start Script");
    startButton.addActionListener( // parse results and run
        new ActionListener() {
          private void setFletchingType() {
            final int[] unstrungLongIds = {
              ItemId.UNSTRUNG_LONGBOW.getId(),
              ItemId.UNSTRUNG_OAK_LONGBOW.getId(),
              ItemId.UNSTRUNG_WILLOW_LONGBOW.getId(),
              ItemId.UNSTRUNG_MAPLE_LONGBOW.getId(),
              ItemId.UNSTRUNG_YEW_LONGBOW.getId(),
              ItemId.UNSTRUNG_MAGIC_LONGBOW.getId()
            };
            final int[] unstrungShortIds = {
              ItemId.UNSTRUNG_SHORTBOW.getId(),
              ItemId.UNSTRUNG_OAK_SHORTBOW.getId(),
              ItemId.UNSTRUNG_WILLOW_SHORTBOW.getId(),
              ItemId.UNSTRUNG_MAPLE_SHORTBOW.getId(),
              ItemId.UNSTRUNG_YEW_SHORTBOW.getId(),
              ItemId.UNSTRUNG_MAGIC_SHORTBOW.getId()
            };
            final int[] strungLongIds = {
              ItemId.LONGBOW.getId(),
              ItemId.OAK_LONGBOW.getId(),
              ItemId.WILLOW_LONGBOW.getId(),
              ItemId.MAPLE_LONGBOW.getId(),
              ItemId.YEW_LONGBOW.getId(),
              ItemId.MAGIC_LONGBOW.getId()
            };
            final int[] strungShortIds = {
              ItemId.SHORTBOW.getId(),
              ItemId.OAK_SHORTBOW.getId(),
              ItemId.WILLOW_SHORTBOW.getId(),
              ItemId.MAPLE_SHORTBOW.getId(),
              ItemId.YEW_SHORTBOW.getId(),
              ItemId.MAGIC_SHORTBOW.getId()
            };
            final int[] logIds = {
              ItemId.LOGS.getId(),
              ItemId.OAK_LOGS.getId(),
              ItemId.WILLOW_LOGS.getId(),
              ItemId.MAPLE_LOGS.getId(),
              ItemId.YEW_LOGS.getId(),
              ItemId.MAGIC_LOGS.getId()
            };
            switch (comboBoxField2[0].getSelectedIndex()) {
              case 0: // fletch longbow
                primaryItemId = ItemId.KNIFE.getId();
                primaryItemAmount = 1;
                secondaryItemId = logIds[comboBoxField1[0].getSelectedIndex()];
                secondaryItemAmount = 29;
                resultItemId = unstrungLongIds[comboBoxField1[0].getSelectedIndex()];
                dialogOption = 2;
                break;
              case 1: // string longbow
                primaryItemId = unstrungLongIds[comboBoxField1[0].getSelectedIndex()];
                primaryItemAmount = 14;
                secondaryItemId = ItemId.BOW_STRING.getId();
                secondaryItemAmount = 14;
                resultItemId = strungLongIds[comboBoxField1[0].getSelectedIndex()];
                break;
              case 2: // arrow shafts
                primaryItemId = ItemId.KNIFE.getId();
                primaryItemAmount = 1;
                secondaryItemId = logIds[comboBoxField1[0].getSelectedIndex()];
                secondaryItemAmount = 29;
                resultItemId = ItemId.ARROW_SHAFTS.getId();
                dialogOption = 0;
                break;
              case 3: // fletch shortbow
                primaryItemId = ItemId.KNIFE.getId();
                primaryItemAmount = 1;
                secondaryItemId = logIds[comboBoxField1[0].getSelectedIndex()];
                secondaryItemAmount = 29;
                resultItemId = unstrungShortIds[comboBoxField1[0].getSelectedIndex()];
                dialogOption = 1;
                break;
              case 4: // string shortbow
                primaryItemId = unstrungShortIds[comboBoxField1[0].getSelectedIndex()];
                primaryItemAmount = 14;
                secondaryItemId = ItemId.BOW_STRING.getId();
                secondaryItemAmount = 14;
                resultItemId = strungShortIds[comboBoxField1[0].getSelectedIndex()];
                break;
              default:
                c.log("Error in script option");
            }
          }

          private void set_ids() {
            //           bringFood = bringFoodCheckBox.getState();
            //            if (bringFood) {
            //              c.log("bringing food");
            //              if (!foodAmountsField.getText().isEmpty()) {
            //                foodWithdrawAmount = Integer.parseInt(foodAmountsField.getText());
            //              } else foodWithdrawAmount = 1;
            //              foodId = foodIds[foodType.getSelectedIndex()];
            //              foodName = foodTypes[foodType.getSelectedIndex()];
            //            }
            // assign item ids to harvest, paths, etc
            switch (list.getSelectedItem()) {
              case "Fletching":
                scriptSelect = 0;
                setFletchingType();
                break;
              case "Gem Cutting":
                final int[] gemIds = {
                  ItemId.SAPPHIRE.getId(),
                  ItemId.EMERALD.getId(),
                  ItemId.RUBY.getId(),
                  ItemId.DIAMOND.getId(),
                  ItemId.DRAGONSTONE.getId(),
                  ItemId.OPAL.getId(),
                  ItemId.JADE.getId(),
                  ItemId.RED_TOPAZ.getId()
                };
                scriptSelect = 1;
                primaryItemId = ItemId.CHISEL.getId();
                primaryItemAmount = 1;
                secondaryItemId = gemIds[comboBoxField1[1].getSelectedIndex()];
                secondaryItemAmount = 29;
                break;
              case "Bone Bury":
                final int[] boneIds = {
                  ItemId.BONES.getId(),
                  ItemId.BIG_BONES.getId(),
                  ItemId.BAT_BONES.getId(),
                  ItemId.DRAGON_BONES.getId()
                };
                scriptSelect = 2;
                primaryItemId = boneIds[comboBoxField1[1].getSelectedIndex()];
                primaryItemAmount = 30;
                break;

              case "Coconuts":
                scriptSelect = 3;
                harvestObjectId = SceneryId.COCONUT_PALM.getId();
                harvestToolId = ItemId.FRUIT_PICKER.getId();
                accessItemId = new int[] {ItemId.COINS.getId()};
                accessItemAmount = new int[] {1000};
                break;
              case "Dragonfruit":
                scriptSelect = 4;
                harvestToolId = ItemId.FRUIT_PICKER.getId();
                accessItemAmount = new int[] {1}; // 1
                accessItemId =
                    new int[] {
                      ItemId.SHANTAY_DESERT_PASS.getId()
                    }; // , ItemId.FULL_WATER_SKIN.getId()
                // }
                harvestObjectId = SceneryId.DRAGONFRUIT_TREE.getId();
                break;
              case "Jangerberries":
                scriptSelect = 5;
                harvestObjectId = SceneryId.JANGERBERRY_BUSH.getId();
                harvestToolId = ItemId.FRUIT_PICKER.getId();
                // accessItemId = ItemId.COINS.getId();
                // accessItemAmount = new int[] {1000};
                break;
              case "Whiteberries":
                scriptSelect = 6;
                harvestObjectId = SceneryId.WHITEBERRY_BUSH.getId();
                harvestToolId = ItemId.HERB_CLIPPERS.getId();
                accessItemId = new int[] {ItemId.LOCKPICK.getId()};
                teleportBanking = agilityCapeCheckBox.getState();
                accessItemAmount = new int[] {1};
                autoWalk = true;

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
            scriptStarted = true;
          }
        });

    // Add Cancel Button
    Button cancel = new Button("Cancel");
    cancel.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = false;
          guiSetup = false;
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
    containerInfobox.add(fletchInfobox);

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
        successPerHr = (int) (totalProcessedCount * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@AIO Harvester @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@" + locations[scriptSelect] + " Bank Count: @gre@" + processedItemInBank,
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@"
              + locations[scriptSelect]
              + " Harvest Count: @gre@"
              + totalProcessedCount
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
