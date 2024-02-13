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
  // todo wine drinker

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
  private int destination = 0;
  private boolean autoWalk = false;
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
      {286, 573},
      {288, 566},
      {287, 569},
      {278, 563},
      {278, 561},
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
      c.displayMessage("@red@AIO Bank Trainer ~ By @mag@Kaila");
      startPos[0] = c.currentX();
      startPos[1] = c.currentY();
      if (c.isInBank()) c.closeBank();
      //      for (int bankerId : c.bankerIds) {
      //        if (c.getNearestNpcById(bankerId, false) != null || c.getNearestObjectById(942) !=
      // null) {
      //          bank();
      //          break;
      //        }
      //      }
      c.setBatchBarsOn();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      // if (bringFood) ate = eatFood();
      if ((primaryItemId != -1 && c.getInventoryItemCount(primaryItemId) == 0)
          || (secondaryItemId != -1
              && c.getInventoryItemCount(secondaryItemId) == 0)) { // (bringFood && !ate)
        c.setStatus("@red@Banking..");
        bank();
      }
      switch (scriptSelect) {
        case 1: // crafting (spinning)
          spinningLoop();
          break;
        case 2: // bury bones
          buryLoop();
          break;
        case 3: // firemaking
          burnLoop(burnLocation);
          break;
        case 4: // smithing
          if (c.getInventoryItemCount(secondaryItemId) > 4) smithingLoop();
          break;
        case 5: // bstaffs and kbd scales
          manualProcessingLoop();
          break;
        default:
          batchProcessLoop();
      }
    }
  }

  private void manualProcessingLoop() {
    c.setStatus("@yel@Using Items..");
    for (int i = 0; i < 200; i++) {
      if (!c.isRunning() || c.getInventoryItemCount(secondaryItemId) == 0) break;
      c.useItemOnItemBySlot(
          c.getInventoryItemSlotIndex(primaryItemId), c.getInventoryItemSlotIndex(secondaryItemId));
      c.sleep(50); // 320 worked
    }
  }

  private void batchProcessLoop() {
    c.setStatus("@yel@Using Items..");
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(primaryItemId), c.getInventoryItemSlotIndex(secondaryItemId));
    c.sleep(GAME_TICK);
    if (c.isInOptionMenu() && dialogOption != -1) {
      c.optionAnswer(dialogOption);
      c.sleep(2 * GAME_TICK);
    }
    c.waitForBatching(false);
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
    }
    if (c.isInBank()) {
      if (scriptSelect == 2) { // bury bones
        totalProcessedCount = totalProcessedCount + 30;
      } else if (scriptSelect == 3) { // fm
        totalProcessedCount = totalProcessedCount + 25;
      } else {
        totalProcessedCount = totalProcessedCount + c.getInventoryItemCount(resultItemId);
      }
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != primaryItemId && itemId != secondaryItemId) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(3 * GAME_TICK); // re-sync

      if (c.getInventoryItemCount(primaryItemId) < primaryItemAmount) { // withdraw harvest tool
        if (c.getBankItemCount(primaryItemId) > primaryItemAmount) {
          c.withdrawItem(primaryItemId, primaryItemAmount - c.getInventoryItemCount(primaryItemId));
          c.sleep(GAME_TICK);
        } else {
          c.displayMessage("@red@You are out of something!");
          c.stop();
        }
      }
      if (c.getInventoryItemCount(secondaryItemId) < secondaryItemAmount) { // withdraw harvest tool
        if (c.getBankItemCount(secondaryItemId) > secondaryItemAmount) {
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

  private void spinningLoop() {
    if (c.getNearestObjectById(121) != null && c.getInventoryItemCount(primaryItemId) > 0) {
      c.setStatus("@gre@Spinning Flax");
      int[] spinningWheel = c.getNearestObjectById(121);
      if (destination == 0) {
        c.walkTo(spinningWheel[0] + 1, spinningWheel[1]);
      } else c.walkTo(spinningWheel[0], spinningWheel[1] - 1);
      c.useItemIdOnObject(spinningWheel[0], spinningWheel[1], primaryItemId);
      c.sleep(3000);
      c.waitForBatching(false);
    }
    // Checks for these actions inside the methods
    walkToSpinBank();
    bank();
    walkToSpinSpot();
  }

  private void walkToSpinSpot() {
    // Handle seers
    if (c.getInventoryItemCount(primaryItemId) > 0) {
      c.setStatus("@gre@Walking to Spot");
      if (destination == 0) { // fally
        c.walkTo(297, 576);
      }
      if (destination > 0 && c.getNearestObjectById(121) == null) { // seers or craft guild
        int[][] walkToCoords = {{524, 462}, {349, 611}};
        int[][] ladderCoords = {{525, 462}, {349, 612}};
        c.setStatus("@gre@Walking to Ladder");
        c.walkTo(walkToCoords[destination - 1][0], walkToCoords[destination - 1][1]);
        c.setStatus("@gre@Going up the ladder");
        c.atObject(ladderCoords[destination - 1][0], ladderCoords[destination - 1][1]);
        while (c.isRunning() && c.currentY() < 1000) c.sleep(640);
      }
    }
  }

  private void walkToSpinBank() {
    int[] bankX = new int[] {289, 500, 346};
    int[] bankY = new int[] {571, 455, 608};
    int bankSelX = bankX[destination];
    int bankSelY = bankY[destination];
    if (c.getInventoryItemCount(primaryItemId) == 0) {
      c.setStatus("@gre@Walking to Bank");
      if (destination > 0 && c.getNearestObjectById(121) != null) { // seers or craft
        int[][] walkToCoords = {{524, 1406}, {349, 1555}};
        int[][] ladderCoords = {{525, 1406}, {349, 1556}};
        c.setStatus("@gre@Walking to Ladder");
        c.walkTo(walkToCoords[destination - 1][0], walkToCoords[destination - 1][1]);
        c.setStatus("@gre@Going downstairs");
        c.atObject(ladderCoords[destination - 1][0], ladderCoords[destination - 1][1]);
        while (c.isRunning() && c.currentY() > 1000) c.sleep(640);
      }
      c.walkTo(bankSelX, bankSelY);
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
    c.waitForBatching(false);
    c.walkTo(150, 507);
  }
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
        c.waitForBatching(false);
      }
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
        break;
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

  private void setupGUI() {
    final JPanel checkboxes = new JPanel(new GridLayout(0, 1));
    final JPanel fletchInfobox,
        craftingInfobox,
        boneInfobox,
        firemakingInfobox,
        smithingInfobox,
        jangerInfobox,
        wBerriesInfobox;
    final JPanel containerInfobox = new JPanel(new GridLayout(0, 1));
    Font bold_title = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    Font small_info = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    scriptFrame = new JFrame(c.getPlayerName() + " - AIO Bank Trainer");
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    Label scriptOptions_label = new Label("Script Options:", Label.CENTER);

    // Add in the top infobox Stuff
    JPanel[] infoBoxes = {
      fletchInfobox = new JPanel(new GridLayout(0, 1)),
      craftingInfobox = new JPanel(new GridLayout(0, 1)),
      boneInfobox = new JPanel(new GridLayout(0, 1)),
      firemakingInfobox = new JPanel(new GridLayout(0, 1)),
      smithingInfobox = new JPanel(new GridLayout(0, 1)),
      jangerInfobox = new JPanel(new GridLayout(0, 1)),
      wBerriesInfobox = new JPanel(new GridLayout(0, 1))
    };
    Label[][] allLabels = {
      { // fletching
        new Label("Fletching Scripts ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Start in any bank with an empty inventory")
      },
      { // gem cutting
        new Label("Crafting Scripts ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Start in any bank with an empty inventory")
      },
      { // bone bury
        new Label("Prayer Scripts ~ Fast Bone Bury ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Start in any bank with an empty inventory")
      },
      { // firemaking
        new Label("Firemaking Scripts ~ by Kaila"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 29 Items are made per Menu Cycle."),
        new Label("Do NOT use a firemaking skill cape with this trainer")
      },
      { // smithing
        // todo : turn into aio smither
        new Label("Smithing Scripts ~ by Kaila"),
        new Label("Makes platebodies in Varrock West bank"),
        new Label("Batch Bars MUST be On, Bot will attempt to enable it."),
        new Label("This ensures 5 Plates are made per Menu Cycle.")
      }
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
    final JCheckBox agilityCapeCheckBox = new JCheckBox("99 Agility Cape Teleport?", true),
        ardyTeleCheckBox = new JCheckBox("Teleport to Ardy?", false),
        lumbTeleCheckBox = new JCheckBox("Teleport to Lumbridge?", false),
        bringFoodCheckBox = new JCheckBox("Bring food?", false),
        doStuff = new JCheckBox("doStuff?", false);

    final JLabel[] comboBoxLabels = {
      new JLabel("Log Type:"),
      new JLabel("Select Crafting Method:"),
      new JLabel("Bone Type:"),
      new JLabel("Bar Type:"),
      new JLabel("Select Fletching Method:"),
      new JLabel("Burn Location:"),
      new JLabel("Gem Type:"),
      new JLabel("Select Spin Type:"),
      new JLabel("Select Battlestaff Type:"),
      new JLabel("Select Spinning Location:")
    };
    final String[][] comboFields = {
      { // logs (0)
        "Normal log", "Oak log", "Willow log", "Maple log", "Yew log", "Magic log"
      },
      { // crafting (1)
        "Gem Cutting", "Spinning Wheel", "Battlestaffs", "KBD Scales"
      },
      { // bones (2)
        "Normal Bones", "Big Bones", "Bat Bones", "Dragon Bones"
      },
      { // bars (3)
        "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite"
      },
      { // fletching (4)
        "Fletch Longbows",
        "String Longbows",
        "Make Arrow Shafts",
        "Fletch Shortbows(less xp)",
        "String Shortbows"
      },
      { // firemaking (5)
        "Varrock West",
        "Varrock East",
        "Falador West",
        "Falador East",
        "Ardougne South",
        "Seers Village",
        "Yanille"
      },
      { // gems (6)
        "Sapphire", "Emerald", "Ruby", "Diamond", "Dragonstone", "Opal", "Jade", "Topaz"
      },
      { // spin type (7)
        "Flax", "Wool"
      },
      { // battlestaff type (8)
        "Battlestaff of Fire", "Battlestaff of Air", "Battlestaff of Earth", "Battlestaff of Water"
      },
      { // spinning (9)
        "Falador", "Seers", "Crafting Guild"
      }
    };
    Choice[] comboBoxFields = new Choice[comboFields.length];
    for (int i = 0; i < comboBoxFields.length; i++) {
      comboBoxFields[i] = new Choice();
      for (String str : comboFields[i]) {
        comboBoxFields[i].add(str);
      }
    }
    //    final Label foodTypeLabel = new Label("Food Type:");
    //    Choice foodType = new Choice();
    //    for (String str : foodTypes) {
    //      foodType.add(str);
    //    }
    final Label space_saver_a = new Label();
    final Label space_saver_b = new Label();
    final Label space_saver_c = new Label();
    final Label space_saver_d = new Label();
    final Label space_saver_e = new Label();
    final Label space_saver_f = new Label();

    JCheckBox[] checkboxList =
        new JCheckBox[] {
          agilityCapeCheckBox, ardyTeleCheckBox, lumbTeleCheckBox, bringFoodCheckBox, doStuff
        };

    // set up initial/default panel (fletching)
    checkboxes.add(scriptOptions_label);
    scriptOptions_label.setFont(bold_title);
    checkboxes.add(comboBoxLabels[0]);
    checkboxes.add(comboBoxFields[0]);
    checkboxes.add(comboBoxLabels[4]);
    checkboxes.add(comboBoxFields[4]);
    checkboxes.add(space_saver_a);
    checkboxes.add(space_saver_b);
    checkboxes.add(space_saver_c);
    checkboxes.add(space_saver_d);
    checkboxes.add(space_saver_e);
    checkboxes.add(space_saver_f);

    // Add left side script select options
    final java.awt.List list = new java.awt.List();
    list.add("Fletching");
    list.add("Crafting");
    list.add("Prayer");
    list.add("Firemaking");
    list.add("Smithing");
    list.select(0);
    list.addItemListener(
        e -> {
          for (JPanel infobox : infoBoxes) {
            containerInfobox.remove(infobox);
          }
          for (JCheckBox checkbox : checkboxList) {
            checkboxes.remove(checkbox);
          }
          for (JLabel label : comboBoxLabels) {
            checkboxes.remove(label);
          }
          for (Choice comboSet : comboBoxFields) {
            checkboxes.remove(comboSet);
          }
          checkboxes.remove(space_saver_a);
          checkboxes.remove(space_saver_b);
          checkboxes.remove(space_saver_c);
          checkboxes.remove(space_saver_d);
          checkboxes.remove(space_saver_e);
          checkboxes.remove(space_saver_f);

          switch (list.getSelectedIndex()) {
              // use space savers to keep ~ 6 rows each
            case 0: // "Fletching"
              containerInfobox.add(fletchInfobox); // i = 4 is start of old new
              checkboxes.add(comboBoxLabels[0]); // 8 is end
              checkboxes.add(comboBoxFields[0]);
              checkboxes.add(comboBoxLabels[4]);
              checkboxes.add(comboBoxFields[4]);
              checkboxes.add(space_saver_a);
              checkboxes.add(space_saver_b);
              checkboxes.add(space_saver_c);
              checkboxes.add(space_saver_d);
              break;
            case 1: // "Crafting"
              containerInfobox.add(craftingInfobox);
              checkboxes.add(comboBoxLabels[1]);
              checkboxes.add(comboBoxFields[1]);
              if (comboBoxFields[1].getSelectedIndex() < 3) {
                checkboxes.add(comboBoxLabels[6 + comboBoxFields[1].getSelectedIndex()]);
                checkboxes.add(comboBoxFields[6 + comboBoxFields[1].getSelectedIndex()]);
              }
              if (comboBoxFields[1].getSelectedIndex() == 1) { // spin
                checkboxes.add(comboBoxLabels[9]);
                checkboxes.add(comboBoxFields[9]);
              } else {
                checkboxes.add(space_saver_a);
                checkboxes.add(space_saver_b);
              }
              checkboxes.add(space_saver_c);
              checkboxes.add(space_saver_d);
              comboBoxFields[1].addItemListener(
                  e2 -> {
                    checkboxes.remove(comboBoxLabels[6]);
                    checkboxes.remove(comboBoxFields[6]);
                    checkboxes.remove(comboBoxLabels[7]);
                    checkboxes.remove(comboBoxFields[7]);
                    checkboxes.remove(comboBoxLabels[8]);
                    checkboxes.remove(comboBoxFields[8]);
                    checkboxes.remove(comboBoxLabels[9]);
                    checkboxes.remove(comboBoxFields[9]);
                    checkboxes.remove(space_saver_a);
                    checkboxes.remove(space_saver_b);
                    checkboxes.remove(space_saver_c);
                    checkboxes.remove(space_saver_d);

                    if (comboBoxFields[1].getSelectedIndex() < 3) {
                      checkboxes.add(comboBoxLabels[6 + comboBoxFields[1].getSelectedIndex()]);
                      checkboxes.add(comboBoxFields[6 + comboBoxFields[1].getSelectedIndex()]);
                    }
                    if (comboBoxFields[1].getSelectedIndex() == 1) {
                      checkboxes.add(comboBoxLabels[9]);
                      checkboxes.add(comboBoxFields[9]);
                    } else {
                      checkboxes.add(space_saver_a);
                      checkboxes.add(space_saver_b);
                    }
                    checkboxes.add(space_saver_c);
                    checkboxes.add(space_saver_d);
                    checkboxes.revalidate();
                  });

              break;
            case 2: // "Bone Bury"
              containerInfobox.add(boneInfobox);
              checkboxes.add(comboBoxLabels[2]);
              checkboxes.add(comboBoxFields[2]);
              checkboxes.add(space_saver_a);
              checkboxes.add(space_saver_b);
              checkboxes.add(space_saver_c);
              checkboxes.add(space_saver_d);
              checkboxes.add(space_saver_e);
              checkboxes.add(space_saver_f);
              break;
            case 3: // "firemaking"
              containerInfobox.add(firemakingInfobox);
              checkboxes.add(comboBoxLabels[0]);
              checkboxes.add(comboBoxFields[0]);
              checkboxes.add(comboBoxLabels[5]);
              checkboxes.add(comboBoxFields[5]);
              checkboxes.add(space_saver_a);
              checkboxes.add(space_saver_b);
              checkboxes.add(space_saver_c);
              checkboxes.add(space_saver_d);
              break;
            case 4: // "smithing"
              containerInfobox.add(smithingInfobox);
              checkboxes.add(comboBoxLabels[3]);
              checkboxes.add(comboBoxFields[3]);
              checkboxes.add(space_saver_a);
              checkboxes.add(space_saver_b);
              checkboxes.add(space_saver_c);
              checkboxes.add(space_saver_d);
              checkboxes.add(space_saver_e);
              checkboxes.add(space_saver_f);
              break;
          }
          containerInfobox.revalidate();
          checkboxes.revalidate();
        });

    // Add run button
    Button startButton = new Button("Start Script");
    startButton.addActionListener( // parse results and run
        new ActionListener() {
          private void setCraftingType() { // scriptSelect = 0 is deafult processing
            switch (comboBoxFields[1].getSelectedIndex()) {
              case 0: // gem cutting
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
                processedItemName =
                    comboFields[6 + comboBoxFields[1].getSelectedIndex()][
                        comboBoxFields[6].getSelectedIndex()];
                resultItemId = cutGemIds[comboBoxFields[6].getSelectedIndex()];
                primaryItemId = ItemId.CHISEL.getId();
                secondaryItemId = uncutGemIds[comboBoxFields[6].getSelectedIndex()];
                primaryItemAmount = 1;
                secondaryItemAmount = 29;
                scriptSelect = 0;
                break;
              case 1: // spinning
                final int[] inputIds = {ItemId.FLAX.getId(), ItemId.WOOL.getId()};
                final int[] outputIds = {ItemId.BOW_STRING.getId(), ItemId.BALL_OF_WOOL.getId()};
                processedItemName =
                    comboFields[6 + comboBoxFields[1].getSelectedIndex()][
                        comboBoxFields[6].getSelectedIndex()];
                resultItemId = outputIds[comboBoxFields[7].getSelectedIndex()];
                primaryItemId = inputIds[comboBoxFields[7].getSelectedIndex()]; // flax or wool
                primaryItemAmount = 30;
                scriptSelect = 1;
                destination = comboBoxFields[9].getSelectedIndex();
                break;
              case 2: // battlestaffs
                final int[] orbIds = {
                  ItemId.FIRE_ORB.getId(),
                  ItemId.AIR_ORB.getId(),
                  ItemId.EARTH_ORB.getId(),
                  ItemId.WATER_ORB.getId()
                };
                final int[] staffIds = {
                  ItemId.BATTLESTAFF_OF_FIRE.getId(),
                  ItemId.BATTLESTAFF_OF_AIR.getId(),
                  ItemId.BATTLESTAFF_OF_EARTH.getId(),
                  ItemId.BATTLESTAFF_OF_WATER.getId()
                };
                processedItemName =
                    comboFields[6 + comboBoxFields[8].getSelectedIndex()][
                        comboBoxFields[8].getSelectedIndex()];
                resultItemId = staffIds[comboBoxFields[8].getSelectedIndex()];
                primaryItemId = ItemId.BATTLESTAFF.getId();
                secondaryItemId = orbIds[comboBoxFields[8].getSelectedIndex()];
                primaryItemAmount = 15;
                secondaryItemAmount = 15;
                scriptSelect = 5; // manual
                break;
              case 3: // KBD scales
                processedItemName = "KBD Scales";
                resultItemId = ItemId.CHIPPED_DRAGON_SCALE.getId();
                primaryItemId = ItemId.CHISEL.getId();
                secondaryItemId = ItemId.KING_BLACK_DRAGON_SCALE.getId();
                primaryItemAmount = 1;
                secondaryItemAmount = 29;
                scriptSelect = 5; // manual
                break;
            }
          }

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
            processedItemName = comboFields[0][comboBoxFields[0].getSelectedIndex()];
            switch (comboBoxFields[4].getSelectedIndex()) {
              case 0: // fletch longbow
                primaryItemId = ItemId.KNIFE.getId();
                primaryItemAmount = 1;
                secondaryItemId = logIds[comboBoxFields[0].getSelectedIndex()];
                secondaryItemAmount = 29;
                resultItemId = unstrungLongIds[comboBoxFields[0].getSelectedIndex()];
                dialogOption = 2;
                break;
              case 1: // string longbow
                primaryItemId = unstrungLongIds[comboBoxFields[0].getSelectedIndex()];
                primaryItemAmount = 15;
                secondaryItemId = ItemId.BOW_STRING.getId();
                secondaryItemAmount = 15;
                resultItemId = strungLongIds[comboBoxFields[0].getSelectedIndex()];
                break;
              case 2: // arrow shafts
                primaryItemId = ItemId.KNIFE.getId();
                primaryItemAmount = 1;
                secondaryItemId = logIds[comboBoxFields[0].getSelectedIndex()];
                secondaryItemAmount = 29;
                resultItemId = ItemId.ARROW_SHAFTS.getId();
                dialogOption = 0;
                break;
              case 3: // fletch shortbow
                primaryItemId = ItemId.KNIFE.getId();
                primaryItemAmount = 1;
                secondaryItemId = logIds[comboBoxFields[0].getSelectedIndex()];
                secondaryItemAmount = 29;
                resultItemId = unstrungShortIds[comboBoxFields[0].getSelectedIndex()];
                dialogOption = 1;
                break;
              case 4: // string shortbow
                primaryItemId = unstrungShortIds[comboBoxFields[0].getSelectedIndex()];
                primaryItemAmount = 15;
                secondaryItemId = ItemId.BOW_STRING.getId();
                secondaryItemAmount = 15;
                resultItemId = strungShortIds[comboBoxFields[0].getSelectedIndex()];
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
            switch (list.getSelectedIndex()) { // no scriptSelct = 1 is used
              case 0: // "Fletching"
                scriptSelect = 0;
                setFletchingType();
                break;
              case 1: // "Crafting"
                setCraftingType();
                break;
              case 2: // "Bone Bury"
                final int[] boneIds = {
                  ItemId.BONES.getId(),
                  ItemId.BIG_BONES.getId(),
                  ItemId.BAT_BONES.getId(),
                  ItemId.DRAGON_BONES.getId()
                };
                processedItemName = comboFields[2][comboBoxFields[2].getSelectedIndex()];
                primaryItemId = boneIds[comboBoxFields[2].getSelectedIndex()];
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
                processedItemName = comboFields[0][comboBoxFields[0].getSelectedIndex()];
                burnLocation = comboBoxFields[5].getSelectedIndex();
                primaryItemId = ItemId.TINDERBOX.getId();
                secondaryItemId = logIds[comboBoxFields[0].getSelectedIndex()];
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
                processedItemName = comboFields[3][comboBoxFields[3].getSelectedIndex()];
                resultItemId = barIds[comboBoxFields[3].getSelectedIndex()];
                primaryItemId = ItemId.HAMMER.getId();
                secondaryItemId = barIds[comboBoxFields[3].getSelectedIndex()];
                primaryItemAmount = 1;
                secondaryItemAmount = 25;
                scriptSelect = 4;
                break;
              case 5: // "Jangerberries"
                scriptSelect = 0;

                break;
              case 6: // "Jangerberries"
                scriptSelect = 0;

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
    scriptFrame.setSize(320, 420); // was 415, 550?
    scriptFrame.setMinimumSize(scriptFrame.getSize());

    scriptFrame.add(middle, BorderLayout.CENTER);
    scriptFrame.add(buttons, BorderLayout.SOUTH);

    scriptFrame.pack();
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
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
              + " processed: @gre@"
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
      c.drawString("@whi@____________________", x, y + 3 + (14 * 4), 0xFFFFFF, 1);
    }
  }
}
