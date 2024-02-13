package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * <b>PotionMaker Script</b>
 *
 * <p>Batch bars MUST be toggles on to function properly.<br>
 * added unif only option.<br>
 * Changed method to 15/15 inventories for efficiency.<br>
 * added option to stop making unif/etc. when out of secondary.<br>
 *
 * @author Seatta, Kaila
 * @version 1.2 Updated with GUI and full logic rewrite
 */
/*
 * todo add uranium support/no batching support
 */
public final class PotionMaker extends IdleScript {
  private final Controller c = Main.getController();
  private String potion = "";
  private String primaryIngredientName = "";
  private String secondaryIngredientName = "";
  private JFrame scriptFrame = null;
  private boolean stopped = false;
  private boolean guiSetup = false;
  private boolean scriptStarted = false;
  private boolean onlyMakeUnifsCycle = false;
  // private boolean stopWithSecondary = false;
  private long startTime;
  private int levelReq = 0;
  private int combinedVialsInBank = 0;
  private int primaryIngredientTotalInBank = 0;
  private int primaryIngredientInBank = 0;
  private int secondaryIngredientTotalInBank = 0;
  private int[] startPos = {0, 0};
  private int made = 0;
  // Ingredients: Full Vial, Clean Herb, Secondary, Empty Vial, Unid Herb, Unfinished Potion
  private final int[] ingredients = {464, 0, 0, 465, 0, 0};
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    paintBuilder.start(4, 18, 220);
    c.setBatchBars(true);
    if (!guiSetup) {
      c.setStatus("@cya@Setting up script");
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      startPos[0] = c.currentX();
      startPos[1] = c.currentY();
      guiSetup = false;
      scriptStarted = false;
      if (c.isInBank()) c.closeBank();
      c.setBatchBars(true);
      bank();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    // Ingredients: Full Vial[0], Clean Herb[1], Secondary[2], Empty Vial[3], Unid Herb[4],
    // Unfinished Potion[5]
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (ingredients[5] != 0 // 1, 2, 4
          && ((c.isItemInInventory(ingredients[1]) || c.isItemInInventory(ingredients[4]))
              && (c.isItemInInventory(ingredients[0]) || c.isItemInInventory(ingredients[3])))) {
        if (c.isItemInInventory(ingredients[3])) { // typically ignored
          fillVials();
        }
        if (c.isItemInInventory( // if inventory has unid herbs, id them
            ingredients[4])) { // typically ignored if herb Identifier script is used (ideal)
          cleanHerbs();
        }
        if (c.isItemInInventory(ingredients[1])) { // mix unf potions
          makeUnf(); // skips if no herbs/vials in inventory
        }
      } else if (ingredients[1] == 468) { // no unid in index, and has pestle in [1] slot
        // grind away
        grind();
      } else if (!onlyMakeUnifsCycle) {
        // mix pots
        mix();
      } else {
        bank();
      }
    }
  }
  // Processing methods
  private void makeUnf() {
    c.setStatus("@cya@Making Unfinished Potions");
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(ingredients[1]), c.getInventoryItemSlotIndex(ingredients[0]));
    c.waitForBatching(false);
    bank();
  }

  private void mix() {
    int before = c.getInventoryItemCount(ingredients[5]);
    c.setStatus("@cya@Adding Secondary");
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(ingredients[2]), c.getInventoryItemSlotIndex(ingredients[5]));
    c.waitForBatching(false);
    made += (before - c.getInventoryItemCount(ingredients[5]));
    bank();
  }

  private void grind() {
    c.setStatus("@cya@Grinding " + c.getItemName(ingredients[2]));
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(ingredients[1]), c.getInventoryItemSlotIndex(ingredients[2]));
    c.waitForBatching(false);
    bank();
    // add to total
    made += c.getInventoryItemCount(ingredients[4]);
  }

  private void fillVials() {
    c.setStatus("@cya@Filling Vials");
    try {
      int[] fountainCoords = c.getNearestObjectById(1280);
      c.useItemIdOnObject(fountainCoords[0], fountainCoords[1], ingredients[0]);
      c.sleep(2000);
      while (c.isCurrentlyWalking()) {
        c.sleep(640);
      }
      c.waitForBatching(false);
      bank();
    } catch (Exception e) {
      // No Fountain Nearby, wasn't in Falador west bank
      quit(3);
    }
  }

  private void cleanHerbs() {
    c.setStatus("@cya@Cleaning Herbs");
    c.itemCommand(ingredients[4]);
    c.waitForBatching(false);
    bank();
  }

  // Banking and withdraw methods
  private void bank() {
    c.setStatus("@gre@Banking..");
    if (c.distance(c.currentX(), c.currentY(), startPos[0], startPos[1]) > 10) {
      c.walkTo(startPos[0], startPos[1]);
    }
    if (!c.isInBank()) {
      int[] bankerIds = {95, 224, 268, 540, 617, 792};
      ORSCharacter npc = c.getNearestNpcByIds(bankerIds, false);
      if (npc != null) {
        c.setStatus("@yel@Walking to Banker..");
        c.displayMessage("@yel@Walking to Banker..");
        c.walktoNPCAsync(npc.serverIndex);
        c.sleep(640);
      } else {
        c.log("@red@walking to Bank Error..");
        c.sleep(1000);
      }
    }
    c.openBank();
    c.sleep(2 * 640);
    if (!c.isInBank()) {
      K_kailaScript.waitForBankOpen();
    }
    if (c.isInBank()) {
      // Ingredients: Full Vial[0], Clean Herb[1], Secondary[2], Empty Vial[3], Unid Herb[4],
      // Unfinished Potion[5]
      // we need to deposit everything into bank
      // first, bot opens bank and deposits all, leaving bank open
      c.setStatus("@cya@Depositing items");
      combinedVialsInBank = c.getBankItemCount(464) + c.getBankItemCount(465);
      if (c.getInventoryItemCount() > 0) {
        for (int itemId : c.getInventoryItemIds()) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
          // }
        }
      }
      c.sleep(3 * 640);
      if (ingredients[5] != 0) {
        // now we count up unif potions + math.min((clean+ dirty herbs),(Empty + full vials)) =
        // number you can craft
        primaryIngredientTotalInBank =
            c.getBankItemCount(ingredients[5]) // unifs
                + Math.min(
                    (c.getBankItemCount(ingredients[1]) + c.getBankItemCount(ingredients[4])),
                    (c.getBankItemCount(ingredients[0]) + c.getBankItemCount(ingredients[3])));
        primaryIngredientInBank =
            c.getBankItemCount(ingredients[1])
                + c.getBankItemCount(ingredients[4])
                + c.getBankItemCount(ingredients[5]);
        secondaryIngredientTotalInBank = c.getBankItemCount(ingredients[2]);
        // Next, bot withdraws max number of unifs (up to 15 if in bank)
        if (!onlyMakeUnifsCycle) {
          withdrawUnifs();
        }
        // withdraw vials/empty vials if not 15 items in inventory (this part also withdraws herbs)
        if (c.getInventoryItemCount() == 0) {
          withdrawVials();
        }
        if (c.getInventoryItemCount(ingredients[0]) > 0
            || c.getInventoryItemCount(ingredients[3]) > 0) {
          withdrawHerb();
        }
        // Next bot withdraws 15 secondaries (only if not full inv)
        if (c.getInventoryItemCount() < 30) {
          withdrawSecondary();
        }
        //        if (stopWithSecondary) {
        //          if (c.getBankItemCount(ingredients[2]) < 15) {
        //           // c.log("error, unequal amount of secondaries. stopping. ");
        //            // endSession();
        //          } //todo re-enable this
        //        }
      } else {
        // need [2] for primary and [4] for secondary when grinding
        primaryIngredientTotalInBank = c.getBankItemCount(ingredients[2]);
        primaryIngredientInBank = c.getBankItemCount(ingredients[2]);
        secondaryIngredientTotalInBank = c.getBankItemCount(ingredients[4]);
        // withdraw pestle and mortar
        withdrawPestle();
        // withdraw pre-secondary
        withdrawPre();
      }
      c.closeBank(); // Next, close back
      c.sleep(640);
    }
  }

  private void withdrawUnifs() {
    c.setStatus("@cya@Withdrawing Unifs");
    if (c.getBankItemCount(ingredients[5]) > 1) {
      if (ingredients[2] == 1410) { // fish oil potions
        c.withdrawItem(ingredients[5], Math.min(29, c.getBankItemCount(ingredients[5]) - 1));
        c.sleep(640);
      } else {
        c.withdrawItem(ingredients[5], Math.min(15, c.getBankItemCount(ingredients[5]) - 1));
        c.sleep(640);
      }
    }
  }

  private void withdrawVials() {
    if (c.getBankItemCount(ingredients[0]) > 0) {
      c.withdrawItem(ingredients[0], Math.min(15, c.getBankItemCount(ingredients[0]) - 1));
      c.sleep(640);
      // withdraw empty vials if no filled ones are available
    } else if (c.getBankItemCount(ingredients[3]) > 0) {
      c.withdrawItem(ingredients[3], Math.min(15, c.getBankItemCount(ingredients[3]) - 1));
      c.sleep(640);
    } else {
      c.log("withdraw Vials and Herbs issue");
      quit(5);
    }
  }

  private void withdrawHerb() {
    // withdraw clean herbs
    if (c.getBankItemCount(ingredients[1]) > 0) {
      c.withdrawItem(ingredients[1], Math.min(15, c.getBankItemCount(ingredients[1]) - 1));
      c.sleep(640);
    } else {
      // withdraw unid herbs (or
      if (c.getBankItemCount(ingredients[4]) > 0) {
        c.withdrawItem(ingredients[4], Math.min(15, c.getBankItemCount(ingredients[4]) - 1));
        c.sleep(640);
      } else if (!onlyMakeUnifsCycle) {
        // no clean or unid herbs
        c.log("withdraw herb issue");
        quit(2);
      }
    }
  }

  private void withdrawSecondary() {
    if (ingredients[2] == 1410) { // fish oil
      if (c.getBankItemCount(ingredients[2]) > 0) { //
        c.withdrawItem(ingredients[2], Math.min(290, c.getBankItemCount(ingredients[2]) - 1));
        c.sleep(640);
      } else if (!onlyMakeUnifsCycle) {
        c.log("fish oil issue");
        quit(2);
      }
    } else {
      if (c.getBankItemCount(ingredients[2]) > 0) {
        c.withdrawItem(ingredients[2], Math.min(15, c.getBankItemCount(ingredients[2]) - 1));
        c.sleep(640);
      } else if (!onlyMakeUnifsCycle) {
        c.log("secondaries issue");
        quit(2);
      }
    }
  }

  private void withdrawPestle() {
    if (c.getInventoryItemCount(468) == 0) { // 468 is pestal
      c.setStatus("@cya@Withdrawing Pestle..");
      if (c.getBankItemCount(468) == 0) {
        c.log("pestle issue");
        quit(2);
      }
      c.withdrawItem(ingredients[1], 1 - c.getInventoryItemCount(468));
      c.sleep(1280);
    }
  }

  private void withdrawPre() {
    if (c.getBankItemCount(ingredients[2]) > 0) {
      if (c.getBankItemCount(ingredients[2]) > 29) {
        c.withdrawItem(ingredients[2], 29);
        c.sleep(1280);
      } else {
        c.withdrawItem(ingredients[2], c.getBankItemCount(ingredients[2]));
        c.sleep(1280);
      }
    } else {
      c.log("pre issue");
      quit(2);
    }
  }
  // Ingredients: Full Vial[0],
  // Clean Herb[1],
  // Secondary[2],
  // Empty Vial[3],
  // Unid Herb[4],
  // Unfinished Potion[5]

  private void quit(Integer i) {
    if (!stopped) {
      if (i == 1) {
        c.log("Error Code: 1");
        c.displayMessage("@red@Script stopped");
        c.setStatus("@red@Script stopped");
      } else if (i == 2) {
        c.log("Error Code: 2");
        c.displayMessage("@red@Out of ingredients");
        c.displayMessage("@red@This potion requires:");
        c.displayMessage("@red@" + c.getItemName(ingredients[1]));
        c.displayMessage("@red@" + c.getItemName(ingredients[2]));
        c.setStatus("@red@Out of ingredients");
      } else if (i == 3) {
        c.log("Error Code: 3");
        c.displayMessage("@red@Unable to fill vials, run the script in Falador West Bank");
        c.setStatus("@red@Start in Falador west bank");
      } else if (i == 4) {
        c.log("Error Code: 4");
        c.displayMessage("@red@This potion requires level " + levelReq + " Herblaw to make.");
        c.setStatus("@red@Herblaw level not high enough");
      } else if (i == 5) {
        c.log("Error Code: 5");
        c.displayMessage("@red@Out of vials.");
        c.setStatus("@red@Out of vials");
      }
    }
    // stopped = true;
    // c.stop();
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {

      // Colors are based on https://spec.draculatheme.com/#sec-Standard
      int purple = 0xBD93F9;
      int darkGray = 0x282A36;
      int white = 0xF8F8F2;
      int green = 0x50FA7B;
      int yellow = 0xF1FA8C;
      int cyan = 0x8BE9FD;

      paintBuilder.setBorderColor(purple);
      paintBuilder.setBackgroundColor(darkGray, 255);

      String[] titleStrings = {"Potion", "Maker"};
      int[] titleColors = {green, cyan};
      int[] titleXOffsets = {48, 64};

      paintBuilder.setTitleMultipleColor(titleStrings, titleColors, titleXOffsets, 4);
      paintBuilder.addRow(rowBuilder.centeredSingleStringRow("Seatta & Kaila", purple, 1));
      paintBuilder.addRow(
          rowBuilder.centeredSingleStringRow("Run Time: " + paintBuilder.stringRunTime, white, 1));
      paintBuilder.addSpacerRow(8);

      paintBuilder.addRow(
          rowBuilder.centeredSingleStringRow("Vials Remaining: " + combinedVialsInBank, cyan, 1));
      if (primaryIngredientName.length() > 0) {
        String remainingString = String.valueOf("Banked: " + primaryIngredientInBank);
        paintBuilder.addRow(
            rowBuilder.multipleStringRow(
                new String[] {primaryIngredientName, remainingString},
                new int[] {green, yellow},
                new int[] {
                  4,
                  paintBuilder.getWidth()
                      - c.getStringWidth(remainingString, 1)
                      - (remainingString.charAt(remainingString.length() - 1) == '1' ? 3 : 4)
                      - 3
                },
                1));
      }
      if (secondaryIngredientName.length() > 0) {
        String remainingString = String.valueOf("Remaining: " + secondaryIngredientTotalInBank);
        paintBuilder.addRow(
            rowBuilder.multipleStringRow(
                new String[] {secondaryIngredientName, remainingString},
                new int[] {green, yellow},
                new int[] {
                  4,
                  paintBuilder.getWidth()
                      - c.getStringWidth(remainingString, 1)
                      - (remainingString.charAt(remainingString.length() - 1) == '1' ? 3 : 4)
                      - 3
                },
                1));
      }
      paintBuilder.addRow(
          rowBuilder.centeredSingleStringRow(
              (ingredients[1] != 468
                      ? "Potions Made: "
                      : c.getItemName(ingredients[2]) + " Ground: ")
                  + String.format("%,d ", made)
                  + paintBuilder.stringAmountPerHour(made),
              green,
              1));
      paintBuilder.addSpacerRow(8);
      paintBuilder.addRow(
          rowBuilder.centeredSingleStringRow(
              "Time Remaining: "
                  + (onlyMakeUnifsCycle
                      ? c.timeToCompletion(made, primaryIngredientTotalInBank, startTime)
                      : c.timeToCompletion(
                          made,
                          Math.min(secondaryIngredientTotalInBank, primaryIngredientTotalInBank),
                          startTime)),
              white,
              1));

      paintBuilder.draw();
    }
  }

  private void parseValues() {
    startTime = System.currentTimeMillis();
    c.displayMessage("@red@REQUIRES Batch bars be toggle on in settings to work correctly");
    if (c.getBaseStat(c.getStatId("Herblaw")) < levelReq) {
      // Herblaw level too low
      quit(4);
    }
    if (ingredients[1] != 468) {
      c.displayMessage("@cya@Making: " + potion + " Potion");
    } else {
      c.displayMessage(
          "@cya@Grinding: "
              + c.getItemName(ingredients[2])
              + " into "
              + c.getItemName(ingredients[4]));
    }
    c.displayMessage("@cya@Using Main Ingredient: " + primaryIngredientName);
    c.displayMessage("@cya@Using Secondary Ingredient: " + secondaryIngredientName);
  }
  /*
   *  Ingredients: Full Vial[0], Clean Herb[1], Secondary[2], Empty Vial[3], Unid Herb[4], Unfinished Potion[5]
   *  PrimaryIngredientInBank = ingredients[1] + ingredients[4] + ingredients[5]
   *  SecondaryIngredientTotalInBank = ingredients[2]
   * ingredients[0] = 464;
   * ingredients[3] = 465;
   */
  private void setupGUI() {
    JLabel header = new JLabel("Potion Maker - Seatta & Kaila");
    JLabel batchLabel = new JLabel("Batch Bars MUST be toggled ON in settings!!!");
    JLabel batchLabel2 = new JLabel("This ensures 15 items are made each bank action.");
    JLabel potionLabel = new JLabel("Select Potion/Secondary");
    JComboBox<String> potionField =
        new JComboBox<>(
            new String[] {
              "Attack",
              "Cure Poison",
              "Strength",
              "Runecraft",
              "Stat Restoration",
              "Defense",
              "Prayer",
              "Super Attack",
              "Poison Antidote",
              "Fishing",
              "Super Strength",
              "Super Runecraft",
              "Weapon Poison",
              "Super Defense",
              "Ranging",
              "Magic",
              "Potion of Zamorak",
              "Potion of Saradomin",
              "Super Ranging",
              "Super Magic",
              "Ground Unicorn Horn",
              "Ground Blue Dragon Scale",
              "Fish Oil (Raw Turtle)",
              "Fish Oil (Raw Manta Ray)",
              "Fish Oil (Raw Shark)",
              "Fish Oil (Raw Swordfish)",
              "Fish Oil (Raw Lobster)",
              "Fish Oil (Raw Tuna)"
            });
    JCheckBox unfinishedPotionCheckbox = new JCheckBox("Only Make Unif Potions?", false);
    // JCheckBox stopCraftingSecondaryCheckbox = new JCheckBox("Stop when out of Secondary?", true);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          onlyMakeUnifsCycle = unfinishedPotionCheckbox.isSelected();
          // stopWithSecondary = stopCraftingSecondaryCheckbox.isSelected();
          parseValues();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          switch (Objects.requireNonNull(potionField.getSelectedItem()).toString()) {

              /*
               * Ingredients: Full Vial[0], Clean Herb[1], Secondary[2], Empty Vial[3], Unid
               * Herb[4], Unfinished Potion[5] PrimaryIngredientInBank = ingredients[1] +
               * ingredients[4] + ingredients[5] SecondaryIngredientTotalInBank = ingredients[2]
               */
            case "Attack":
              // Herb
              ingredients[1] = 444; // Clean
              ingredients[2] = 270; // Eye of Newt
              ingredients[4] = 165; // Grimy
              ingredients[5] = 454; // Unfinished
              primaryIngredientName = "Guam";
              secondaryIngredientName = "Eye of Newt";
              levelReq = 3;
              break;
            case "Cure Poison":
              ingredients[1] = 445; // Clean
              ingredients[2] = 473; //
              ingredients[4] = 435; // Grimy
              ingredients[5] = 455; // Unfinished
              primaryIngredientName = "Marrentill";
              secondaryIngredientName = "Ground Unicorn Horn";
              levelReq = 5;
              break;
            case "Strength":
              ingredients[1] = 446; // Clean
              ingredients[2] = 220; //
              ingredients[4] = 436; // Grimy
              ingredients[5] = 456; // Unfinished
              primaryIngredientName = "Tarromin";
              secondaryIngredientName = "Limpwurt Root";
              levelReq = 12;
              break;
            case "Runecraft":
              ingredients[1] = 445; // Clean
              ingredients[2] = 1410; //
              ingredients[4] = 435; // Grimy
              ingredients[5] = 455; // Unfinished
              primaryIngredientName = "Marrentill";
              secondaryIngredientName = "Fish Oil";
              levelReq = 12;
              break;
            case "Stat Restoration":
              // Herb
              ingredients[1] = 447; // Clean
              ingredients[2] = 219; //
              ingredients[4] = 437; // Grimy
              ingredients[5] = 457; // Unfinished
              primaryIngredientName = "Harralander";
              secondaryIngredientName = "Red Spider's Eggs";
              levelReq = 22;
              break;
            case "Defense":
              ingredients[1] = 448; // Clean
              ingredients[2] = 220; //
              ingredients[4] = 438; // Grimy
              ingredients[5] = 458; // Unfinished
              primaryIngredientName = "Ranarr";
              secondaryIngredientName = "White Berries";
              levelReq = 30;
              break;
            case "Prayer":
              ingredients[1] = 448; // clean herb
              ingredients[2] = 469; // snape grass
              ingredients[4] = 438; // unid herb
              ingredients[5] = 458; // Unfinished
              primaryIngredientName = "Ranarr";
              secondaryIngredientName = "Snape Grass";
              levelReq = 38;
              break;
            case "Super Attack":
              ingredients[1] = 449; // Clean
              ingredients[2] = 270; //
              ingredients[4] = 439; // Grimy
              ingredients[5] = 459; // Unfinished
              primaryIngredientName = "Irit";
              secondaryIngredientName = "Eye of Newt";
              levelReq = 45;
              break;
            case "Poison Antidote":
              ingredients[1] = 449; // Clean
              ingredients[2] = 473; //
              ingredients[4] = 439; // Grimy
              ingredients[5] = 459; // Unfinished
              primaryIngredientName = "Irit";
              secondaryIngredientName = "Ground Unicorn Horn";
              levelReq = 48;
              break;
            case "Fishing":
              ingredients[1] = 450; // Clean
              ingredients[2] = 469; //
              ingredients[4] = 440; // Grimy
              ingredients[5] = 460; // Unfinished
              primaryIngredientName = "Avantoe";
              secondaryIngredientName = "Snape Grass";
              levelReq = 50;
              break;
            case "Super Strength":
              ingredients[1] = 451; // Clean
              ingredients[2] = 220; //
              ingredients[4] = 441; // Grimy
              ingredients[5] = 461; // Unfinished
              primaryIngredientName = "Kwuarm";
              secondaryIngredientName = "Limpwurt Root";
              levelReq = 55;
              break;
            case "Super Runecraft":
              ingredients[1] = 450; // Clean
              ingredients[2] = 1410; //
              ingredients[4] = 440; // Grimy
              ingredients[5] = 460; // Unfinished
              primaryIngredientName = "Avantoe";
              secondaryIngredientName = "Fish Oil";
              levelReq = 57;
              break;
            case "Weapon Poison":
              ingredients[1] = 451; // Clean
              ingredients[2] = 472; //
              ingredients[4] = 441; // Grimy
              ingredients[5] = 461; // Unfinished
              primaryIngredientName = "Kwuarm";
              secondaryIngredientName = "Ground Blue Dragon Scale";
              levelReq = 60;
              break;
            case "Super Defense":
              ingredients[1] = 452; // Clean
              ingredients[2] = 471; //
              ingredients[4] = 442; // Grimy
              ingredients[5] = 462; // Unfinished
              primaryIngredientName = "Cadantine";
              secondaryIngredientName = "White Berries";
              levelReq = 66;
              break;
            case "Ranging":
              ingredients[1] = 453; // Clean
              ingredients[2] = 501; //
              ingredients[4] = 443; // Grimy
              ingredients[5] = 463; // Unfinished
              primaryIngredientName = "Dwarf Weed";
              secondaryIngredientName = "Wine of Zamorak";
              levelReq = 76;
              break;
            case "Magic": // Kaila added
              ingredients[1] = 453; // Clean
              ingredients[2] = 1467; // wine of saradomin
              ingredients[4] = 443; // Grimy
              ingredients[5] = 463; // Unfinished
              primaryIngredientName = "Dwarf Weed";
              secondaryIngredientName = "Wine of Saradomin";
              levelReq = 72;
              break;
            case "Potion of Zamorak":
              ingredients[1] = 934; // Clean
              ingredients[2] = 936; // Jangerberries
              ingredients[4] = 933; // Grimy
              ingredients[5] = 935; // Unfinished
              primaryIngredientName = "Torstol";
              secondaryIngredientName = "Jangerberries";
              levelReq = 78;
              break;

            case "Potion of Saradomin": // Kaila added
              ingredients[1] = 934; // clean
              ingredients[2] = 1458; // sliced dragonfruit
              ingredients[4] = 933; // Grimy
              ingredients[5] = 935; // unif
              primaryIngredientName = "Torstol";
              secondaryIngredientName = "Sliced Dragonfruit";
              levelReq = 81;
              break;
            case "Super Ranging": // Kaila added
              ingredients[1] = 934; // Ranging Potion
              ingredients[2] = 1361; // half coconut
              primaryIngredientName = "Ranging Potion";
              secondaryIngredientName = "Half Coconut";
              levelReq = 83;
              break;
            case "Super Magic": // Kaila added
              ingredients[1] = 934; // Magic Potion
              ingredients[2] = 1361; // half coconut
              primaryIngredientName = "Magic Potion";
              secondaryIngredientName = "Half Coconut";
              levelReq = 85;
              break;

              // grinding below
            case "Ground Unicorn Horn":
              ingredients[1] = 468; // pestle and mortar
              ingredients[2] = 466; // Unicorn Horn
              ingredients[4] = 473; // Ground Unicorn Horn
              primaryIngredientName = "Unicorn Horn";
              secondaryIngredientName = "N/A";
              levelReq = 0;
              break;
            case "Ground Blue Dragon Scale":
              ingredients[1] = 468; // pestle and mortar
              ingredients[2] = 467; // Blue Dragon Scale
              ingredients[4] = 472; // Ground Blue Dragon Scale
              primaryIngredientName = "Blue Dragon Scale";
              secondaryIngredientName = "N/A";
              levelReq = 0;
              break;
            case "Fish Oil (Raw Turtle)":
              ingredients[1] = 468; // Pestle and mortar
              ingredients[2] = 1192; // Turtle
              ingredients[4] = 1410; // Fish oil
              primaryIngredientName = "Turtle";
              secondaryIngredientName = "N/A";
              levelReq = 0;
              break;
            case "Fish Oil (Raw Manta Ray)":
              ingredients[1] = 468; // Pestle and mortar
              ingredients[2] = 1190; // Manta Ray
              ingredients[4] = 1410; // Fish oil
              primaryIngredientName = "Manta Ray";
              secondaryIngredientName = "N/A";
              levelReq = 0;
              break;
            case "Fish Oil (Raw Shark)":
              ingredients[1] = 468; // Pestle and mortar
              ingredients[2] = 545; // Shark
              ingredients[4] = 1410; // Fish oil
              primaryIngredientName = "Shark";
              secondaryIngredientName = "N/A";
              levelReq = 0;
              break;
            case "Fish Oil (Raw Swordfish)":
              ingredients[1] = 468; // Pestle and mortar
              ingredients[2] = 369; // Swordfish
              ingredients[4] = 1410; // Fish oil
              primaryIngredientName = "Swordfish";
              secondaryIngredientName = "N/A";
              levelReq = 0;
              break;
            case "Fish Oil (Raw Lobster)":
              ingredients[1] = 468; // Pestle and mortar
              ingredients[2] = 372; // Lobster
              ingredients[4] = 1410; // Fish oil
              primaryIngredientName = "Lobster";
              secondaryIngredientName = "N/A";
              levelReq = 0;
              break;
            case "Fish Oil (Raw Tuna)":
              ingredients[1] = 468; // Pestle and mortar
              ingredients[2] = 366; // Tuna
              ingredients[4] = 1410; // Fish oil
              primaryIngredientName = "Tuna";
              secondaryIngredientName = "N/A";
              levelReq = 0;
              break;
          }
          if (!Objects.equals(potionField.getSelectedItem().toString(), "")
              && !Objects.equals(potionField.getSelectedItem().toString(), "0")) {
            potion = potionField.getSelectedItem().toString();
            potion = potionField.getSelectedItem().toString();
            scriptStarted = true;
            stopped = false;
          } else {
            setupGUI();
          }

          // stopped = false;
        });

    scriptFrame = new JFrame(controller.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(batchLabel);
    scriptFrame.add(batchLabel2);
    scriptFrame.add(potionLabel);
    scriptFrame.add(potionField);
    scriptFrame.add(unfinishedPotionCheckbox);
    // scriptFrame.add(stopCraftingSecondaryCheckbox);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }
}
