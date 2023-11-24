package scripting.idlescript;

import bot.Main;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class AIOBankTrainer extends K_kailaScript {
  /**
   *
   *
   * <pre>
   *   0  list.add("Fletching");
   *   1  list.add("Gem Cutting");
   *   2  list.add("Bone Bury");
   *   3  list.add("Firemaking");
   *   4  list.add("Smithing");
   * </pre>
   */

  // todo elemental battlestaffs
  // todo misc - crystal keys, dragon scales
  // todo herb cleaner

  private int scriptSelect = 0;

  private int primaryItemId = 0;
  private String processedItemName = "";
  private int primaryItemAmount = 0;
  private int secondaryItemId = -1;
  private int secondaryItemAmount = 0;
  private int dialogOption = -1;
  private int resultItemId = -1;
  private int burnLocation = 0;
  private int processedItemInBank = 0;
  private int totalProcessedCount = 0;
  private int[] startPos = {0, 0};
  private boolean autoWalk = false;

  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (!guiSetup) {
      guiSetup = true;
      setupGUI();
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.quitIfAuthentic();
      startTime = System.currentTimeMillis();
      if (autoWalk) next_attempt = System.currentTimeMillis() + 5000L;
      c.displayMessage("@red@AIO Bank Trainer ~ By @mag@Kaila");
      startPos[0] = c.currentX();
      startPos[1] = c.currentY();
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
      // if (bringFood) ate = eatFood();
      if (c.getInventoryItemCount(primaryItemId) == 0
          || c.getInventoryItemCount(secondaryItemId) == 0) { // (bringFood && !ate)
        c.setStatus("@red@Banking..");
        bank();
      }
      switch (scriptSelect) {
        case 2: // bury bones
          buryLoop();
          break;
        case 3: // firemaking
          burnLoop(burnLocation);
          break;
        case 4: // smithing
          if (c.getInventoryItemCount(secondaryItemId) > 4) smithingLoop();
          break;
        default:
          inventoryProcessLoop();
      }
      if (autoWalk && System.currentTimeMillis() > next_attempt) {
        c.log("@red@Walking to Avoid Logging!");
        moveCharacter();
        next_attempt = System.currentTimeMillis() + nineMinutesInMillis;
        long nextAttemptInSeconds = (next_attempt - System.currentTimeMillis()) / 1000L;
        c.log("Done Walking to not Log, Next attempt in " + nextAttemptInSeconds + " seconds!");
      }
    }
  }

  private void smithingLoop() {
    c.setStatus("@gre@Smithing..");
    c.walkTo(148, 512);
    c.useItemIdOnObject(148, 513, secondaryItemId);
    c.sleep(1280);
    c.optionAnswer(1);
    c.sleep(640);
    c.optionAnswer(2);
    c.sleep(640);
    c.optionAnswer(2);
    c.sleep(640);
    if (!c.isAuthentic()) {
      c.optionAnswer(3);
      c.sleep(3000); // was 650
    }
    waitForBatching();
    c.walkTo(150, 507);
  }

  private static final int[][][] burnStartLocations = {
    // Note: turning back half-way through trail and ending at bank is ideal
    // Note 2: trails always lay west, turn-backs always turn north then east
    { // "Varrock West"
      {147, 506}, {143, 506}, {152, 507}, {134, 508}, {131, 509}, {157, 506}, {157, 505}, {157, 504}
    },
    { // "Varrock East"
      {98, 515}, {89, 505}, {89, 514}, {87, 508}, {87, 507}, {90, 506} // {77, 509} too far
    },
    { // "Falador West" (The best location probably, due to walk-back)
      {327, 554}, {324, 548}, {324, 544}, {321, 542}, {327, 551}, {334, 557}
    },
    { // "Falador East"
      {280, 568},
      {278, 563},
      {278, 561},
      {286, 573},
      {288, 566},
      {287, 569},
      {296, 566},
      {283, 575},
      {274, 574}
    },
    { // "Ardougne South"
      {538, 607}, {549, 611}, {541, 608}, {537, 606}, {550, 620}, {534, 605}
    },
    { // "Seers Village"
      {598, 453}, {493, 456}, {490, 458}, {502, 457}, {483, 460}, {484, 463}, {494, 451}
    },
    { // "Yanille"
      {585, 750}, {578, 759}, {584, 751}, {583, 748}, {583, 747}, {583, 746}, {591, 750}
    } // {585, 758}, ends with 4 left :/
  }; // x then y then x then y

  /**
   * burn loop
   *
   * @param index 0 = v west, 1 = v east, 2 = seers, 3 = ardy
   */
  private void burnLoop(int index) {
    // c.displayMessage("@gre@Burning logs..");
    c.setStatus("@gre@Burning logs..");
    for (int i = 0; i < burnStartLocations[index].length; i++) {
      if (!c.isRunning()
          || c.getInventoryItemCount(primaryItemId) < 1
          || c.getInventoryItemCount(secondaryItemId) < 1) break;
      if (burnStartLocations[index][i][1] < 140) {
        c.walkTo(138, 508);
      }
      if (c.isTileEmpty(burnStartLocations[index][i][0], burnStartLocations[index][i][1])) {
        // can use c.isTileEmpty
        c.walkTo(burnStartLocations[index][i][0], burnStartLocations[index][i][1]);
        c.dropItem(c.getInventoryItemSlotIndex(secondaryItemId), 1);
        c.sleep(GAME_TICK);
        c.useItemOnGroundItem(
            burnStartLocations[index][i][0],
            burnStartLocations[index][i][1],
            primaryItemId,
            secondaryItemId);
        c.sleep(6 * GAME_TICK);
        while (c.isBatching()) c.sleep(GAME_TICK);
      }
      c.sleep(GAME_TICK);
    }
  }

  private void inventoryProcessLoop() {
    c.setStatus("@yel@Using Items..");
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(primaryItemId), c.getInventoryItemSlotIndex(secondaryItemId));
    c.sleep(GAME_TICK);
    if (c.isInOptionMenu() && dialogOption != -1) {
      c.optionAnswer(dialogOption);
      c.sleep(2 * GAME_TICK);
    }
    while (c.isBatching() && (next_attempt == -1 || System.currentTimeMillis() < next_attempt)) {
      c.sleep(GAME_TICK);
    }
  }

  private void buryLoop() {
    while (c.getInventoryItemCount(primaryItemId) > 0 && c.isRunning()) {
      c.setStatus("@yel@Burying..");
      c.itemCommand(primaryItemId);
      c.sleep(100);
    }
  }

  private void firemakingWalkback() {
    switch (burnLocation) {
      case 0: // "Varrock West"
        if (c.currentX() > 165) {
          c.walkTo(165, 507);
          c.walkTo(151, 507);
        } else if (c.currentX() < 138) c.walkTo(148, 508);
        break;
      case 1: // "Varrock East"
        if (c.currentX() > 115) c.walkTo(103, 509);
        break;
      case 2: // "Falador West"
        if (c.currentX() < 317) c.walkTo(317, 551);
      case 3: // "Falador East"
        if (c.currentX() > 301) c.walkTo(301, 573);
        break;
      case 4: // "Ardougne South"
        if (c.currentX() > 563) c.walkTo(549, 613);
        break;
      case 5: // "Seers Village"
        // if (c.currentX() > FIX) c.walkTo(504, 457);
        break;
    }
    // "Varrock West", "Varrock East", "Falador West", "Falador East", "Ardougne South", "Seers
    // Village", "Yanille"
  }

  private void bank() { // works for all
    c.setStatus("@yel@Banking..");
    if (scriptSelect == 3) firemakingWalkback(); // walkback for fm script
    if (c.distance(c.currentX(), c.currentY(), startPos[0], startPos[1]) > 10) {
      c.walkTo(startPos[0], startPos[1]);
    }
    c.openBank();
    c.sleep(GAME_TICK);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      if (scriptSelect == 2) { // bury bones
        totalProcessedCount = totalProcessedCount + 30;
      } else if (scriptSelect == 3) {
        totalProcessedCount = totalProcessedCount + 25;
      } else {
        totalProcessedCount = totalProcessedCount + c.getInventoryItemCount(resultItemId);
      }
      for (int itemId : c.getInventoryItemIds()) {
        c.depositItem(itemId, c.getInventoryItemCount(itemId));
      }
      c.sleep(3 * GAME_TICK); // re-sync

      if (c.getInventoryItemCount(primaryItemId) < primaryItemAmount) { // withdraw harvest tool
        if (c.getBankItemCount(primaryItemId) > 0) {
          c.withdrawItem(primaryItemId, primaryItemAmount - c.getInventoryItemCount(primaryItemId));
          c.sleep(GAME_TICK);
        } else {
          c.displayMessage("@red@You are out of something!");
          c.stop();
        }
      }
      if (c.getInventoryItemCount(secondaryItemId) < secondaryItemAmount) { // withdraw harvest tool
        if (c.getBankItemCount(secondaryItemId) > 0) {
          c.withdrawItem(
              secondaryItemId, secondaryItemAmount - c.getInventoryItemCount(secondaryItemId));
          c.sleep(GAME_TICK);
        } else {
          c.displayMessage("@red@You are out of something!");
          c.stop();
        }
      }
      // if (bringFood) withdrawFood(foodId, foodWithdrawAmount);
      processedItemInBank = c.getBankItemCount(resultItemId);
      totalTrips++;
      c.closeBank();
      c.sleep(GAME_TICK);
    }
  }

  private void moveCharacter() {
    int x = c.currentX();
    int y = c.currentY();

    if (c.isReachable(x + 1, y, false)) c.walkTo(x + 1, y, 0, false);
    else if (c.isReachable(x - 1, y, false)) c.walkTo(x - 1, y, 0, false);
    else if (c.isReachable(x, y + 1, false)) c.walkTo(x, y + 1, 0, false);
    else if (c.isReachable(x, y - 1, false)) c.walkTo(x, y - 1, 0, false);

    c.sleep(1280);

    c.walkTo(x, y, 0, false);
  }

  private void setupGUI() {

    final Panel checkboxes = new Panel(new GridLayout(0, 1));
    final Panel fletchInfobox,
        gemInfobox,
        boneInfobox,
        firemakingInfobox,
        smithingInfobox,
        jangerInfobox,
        wBerriesInfobox;

    final Panel containerInfobox = new Panel(new GridLayout(0, 1));
    Font bold_title = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    Font small_info = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    scriptFrame = new JFrame(c.getPlayerName() + " - AIO Bank Trainer");
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    Label scriptOptions_label = new Label("Script Options:", Label.CENTER);

    // Add in the top infobox Stuff
    Panel[] infoBoxes = {
      fletchInfobox = new Panel(new GridLayout(0, 1)),
      gemInfobox = new Panel(new GridLayout(0, 1)),
      boneInfobox = new Panel(new GridLayout(0, 1)),
      firemakingInfobox = new Panel(new GridLayout(0, 1)),
      smithingInfobox = new Panel(new GridLayout(0, 1)),
      jangerInfobox = new Panel(new GridLayout(0, 1)),
      wBerriesInfobox = new Panel(new GridLayout(0, 1))
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
      { // firemaking
        new Label("Bank Firemaking ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Do NOT use a firemaking skill cape with this trainer")
      },
      { // smithing
        // todo : turn into aio smither
        new Label("Platebody Smithing ~ by Kaila"),
        new Label("Makes platebodies in Varrock West bank"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 5 Plates are made per Menu Cycle.")
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
      new Label("Log Type:"),
      new Label("Gem Type:"),
      new Label("Bone Type:"),
      new Label("Bar Type:")
    };
    final String[][] comboField1 = {
      { // logs
        "Normal log", "Oak log", "Willow log", "Maple log", "Yew log", "Magic log"
      },
      { // gems
        "Sapphire", "Emerald", "Ruby", "Diamond", "Dragonstone", "Opal", "Jade", "Topaz"
      },
      { // bones
        "Normal Bones", "Big Bones", "Bat Bones", "Dragon Bones"
      },
      { // bars
        "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite"
      }
    };
    final Label[] comboBoxLabel2 = {
      new Label("Select Fletching Type:"), new Label("Burn Location:")
    };
    final String[][] comboField2 = {
      { // fletching
        "Fletch Longbows",
        "String Longbows",
        "Make Arrow Shafts",
        "Fletch Shortbows(less xp)",
        "String Shortbows"
      },
      { // firemaking
        "Varrock West",
        "Varrock East",
        "Falador West",
        "Falador East",
        "Ardougne South",
        "Seers Village",
        "Yanille"
      }
    };

    // Combo box options. Change string comboLabel1, string[] comboField1, etc
    // Label comboBoxLabel1 = comboLabel1[0];
    // Label[] comboBoxLabel1 = new Label[] {new Label(), new Label(), new Label()};
    //    for (int i = 0; i < comboLabel1.length; i++) {
    //        c.log("setting");
    //        comboBoxLabel1[i].add(str);
    //    }
    Choice[] comboBoxField1 = new Choice[comboField1.length];
    for (int i = 0; i < comboBoxField1.length; i++) {
      comboBoxField1[i] = new Choice();
      for (String str : comboField1[i]) {
        comboBoxField1[i].add(str);
      }
    }
    Choice[] comboBoxField2 = new Choice[comboField2.length];
    for (int i = 0; i < comboBoxField2.length; i++) {
      comboBoxField2[i] = new Choice();
      for (String str : comboField2[i]) {
        comboBoxField2[i].add(str);
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

    // Action listeners to hide/show based on checkboxes
    //    bringFoodCheckBox.addItemListener(
    //        e -> {
    //          // comboBoxField2.setEnabled(bringFoodCheckBox.getState());
    //          // comboBoxField1[].setEnabled(bringFoodCheckBox.getState());
    //        });
    // Add left side script select options
    final java.awt.List list = new java.awt.List();
    list.add("Fletching");
    list.add("Gem Cutting");
    list.add("Bone Bury");
    list.add("Firemaking");
    list.add("Smithing");
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
            case 3: // "firemaking"
              containerInfobox.add(firemakingInfobox);
              checkboxes.add(comboBoxLabel1[0]);
              checkboxes.add(comboBoxField1[0]);
              checkboxes.add(comboBoxLabel2[1]);
              checkboxes.add(comboBoxField2[1]);
              break;
            case 4: // "smithing"
              containerInfobox.add(smithingInfobox);
              checkboxes.add(comboBoxLabel1[3]);
              checkboxes.add(comboBoxField1[3]);
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
            processedItemName = comboField1[0][comboBoxField1[0].getSelectedIndex()];
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
                primaryItemAmount = 15;
                secondaryItemId = ItemId.BOW_STRING.getId();
                secondaryItemAmount = 15;
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
                primaryItemAmount = 15;
                secondaryItemId = ItemId.BOW_STRING.getId();
                secondaryItemAmount = 15;
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
            switch (list.getSelectedIndex()) {
              case 0: // "Fletching"
                scriptSelect = 0;
                setFletchingType();
                break;
              case 1: // "Gem Cutting"
                final int[] uncutGemIds = {
                  ItemId.UNCUT_SAPPHIRE.getId(),
                  ItemId.UNCUT_EMERALD.getId(),
                  ItemId.UNCUT_RUBY.getId(),
                  ItemId.UNCUT_DIAMOND.getId(),
                  ItemId.UNCUT_DRAGONSTONE.getId(),
                  ItemId.UNCUT_OPAL.getId(),
                  ItemId.UNCUT_JADE.getId(),
                  ItemId.UNCUT_RED_TOPAZ.getId()
                };
                final int[] cutGemIds = {
                  ItemId.SAPPHIRE.getId(),
                  ItemId.EMERALD.getId(),
                  ItemId.RUBY.getId(),
                  ItemId.DIAMOND.getId(),
                  ItemId.DRAGONSTONE.getId(),
                  ItemId.OPAL.getId(),
                  ItemId.JADE.getId(),
                  ItemId.RED_TOPAZ.getId()
                };
                processedItemName = comboField1[1][comboBoxField1[1].getSelectedIndex()];
                resultItemId = cutGemIds[comboBoxField1[1].getSelectedIndex()];
                primaryItemId = ItemId.CHISEL.getId();
                secondaryItemId = uncutGemIds[comboBoxField1[1].getSelectedIndex()];
                primaryItemAmount = 1;
                secondaryItemAmount = 29;
                scriptSelect = 1;
                break;
              case 2: // "Bone Bury"
                final int[] boneIds = {
                  ItemId.BONES.getId(),
                  ItemId.BIG_BONES.getId(),
                  ItemId.BAT_BONES.getId(),
                  ItemId.DRAGON_BONES.getId()
                };
                processedItemName = comboField1[2][comboBoxField1[2].getSelectedIndex()];
                primaryItemId = boneIds[comboBoxField1[2].getSelectedIndex()];
                primaryItemAmount = 30;
                scriptSelect = 2;
                break;
              case 3: // "Firemaking"
                final int[] logIds = {
                  ItemId.LOGS.getId(),
                  ItemId.OAK_LOGS.getId(),
                  ItemId.WILLOW_LOGS.getId(),
                  ItemId.MAPLE_LOGS.getId(),
                  ItemId.YEW_LOGS.getId(),
                  ItemId.MAGIC_LOGS.getId()
                };
                processedItemName = comboField1[0][comboBoxField1[0].getSelectedIndex()];
                burnLocation = comboBoxField2[1].getSelectedIndex();
                primaryItemId = ItemId.TINDERBOX.getId();
                secondaryItemId = logIds[comboBoxField1[0].getSelectedIndex()];
                primaryItemAmount = 1;
                secondaryItemAmount = 30;
                scriptSelect = 3;
                break;
              case 4: // "Smithing"
                final int[] barIds = {
                  ItemId.BRONZE_BAR.getId(),
                  ItemId.IRON_BAR.getId(),
                  ItemId.STEEL_BAR.getId(),
                  ItemId.MITHRIL_BAR.getId(),
                  ItemId.ADAMANTITE_BAR.getId(),
                  ItemId.RUNITE_BAR.getId()
                };
                processedItemName = comboField1[3][comboBoxField1[3].getSelectedIndex()];
                resultItemId = barIds[comboBoxField1[3].getSelectedIndex()];
                primaryItemId = ItemId.HAMMER.getId();
                secondaryItemId = barIds[comboBoxField1[3].getSelectedIndex()];
                primaryItemAmount = 1;
                secondaryItemAmount = 25;
                scriptSelect = 4;
                break;
              case 5: // "Jangerberries"
                scriptSelect = 5;

                break;
              case 6: // "Jangerberries"
                scriptSelect = 6;

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

    GridBagConstraints cc = new GridBagConstraints();
    cc.fill = GridBagConstraints.HORIZONTAL;
    cc.weightx = 1.0; // request any extra horizontal space
    cc.gridwidth = 2;
    cc.gridx = 0;
    cc.gridy = 0;
    cc.ipady = 20; // make this component tall
    middle.add(containerInfobox, cc);

    cc.fill = GridBagConstraints.VERTICAL;
    cc.weightx = 0.5; // request any extra horizontal space
    cc.gridwidth = 1;
    cc.gridx = 0;
    cc.gridy = 1;
    cc.ipady = 0; // make this component tall
    middle.add(list, cc);

    cc.fill = GridBagConstraints.VERTICAL;
    cc.weightx = 0.5; // request any extra horizontal space
    cc.gridwidth = 1;
    cc.gridx = 1;
    cc.gridy = 1;
    middle.add(checkboxes, cc);

    // only add the first top infobox option (from list)
    containerInfobox.add(fletchInfobox);

    // Setup window
    scriptFrame.setSize(350, 420); // was 415, 550?
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
      c.drawString("@red@AIO Bank Trainer @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@"
              + processedItemName
              + "'s processed: @gre@"
              + totalProcessedCount
              + "@yel@ (@whi@"
              + String.format("%,d", successPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Product Bank Count: @gre@" + processedItemInBank, x, y + (14 * 2), 0xFFFFFF, 1);
      c.drawString(
          "@whi@Inventories Processed: @gre@"
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
