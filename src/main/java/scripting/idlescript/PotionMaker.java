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
  private static final Controller c = Main.getController();
  private static String potion = "";
  private static String primaryIngredientName = "";
  private static String secondaryIngredientName = "";
  private static JFrame scriptFrame = null;
  private static boolean stopped = false;
  private static boolean guiSetup = false;
  private static boolean scriptStarted = false;
  private static boolean onlyMakeUnifsCycle = false;
  private static boolean stopWithSecondary = false;
  private static final long startTimestamp = System.currentTimeMillis() / 1000L;
  private static long startTime;
  private static int levelReq = 0;
  private static int combinedVialsInBank = 0;
  private static int primaryIngredientTotalInBank = 0;
  private static int primaryIngredientInBank = 0;
  private static int secondaryIngredientTotalInBank = 0;
  private static int made = 0;
  // Ingredients: Full Vial, Clean Herb, Secondary, Empty Vial, Unid Herb, Unfinished Potion
  private static final int[] ingredients = {464, 0, 0, 465, 0, 0};
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    c.toggleBatchBarsOn();
    if (!guiSetup) {
      c.setStatus("@cya@Setting up script");
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    // Ingredients: Full Vial[0], Clean Herb[1], Secondary[2], Empty Vial[3], Unid Herb[4],
    // Unfinished Potion[5]
    while (c.isRunning()) {

      bank();
      if (!onlyMakeUnifsCycle) {
        // mix pots
        mix();
      } // have 1 or 4 AND 0 or 3
      if (ingredients[5] != 0
          && ((c.isItemInInventory(ingredients[1]) || c.isItemInInventory(ingredients[4]))
              && (c.isItemInInventory(ingredients[0]) || c.isItemInInventory(ingredients[3])))) {
        if (c.isItemInInventory(ingredients[3])) { // typically ignored
          fillVials();
        }
        // if inventory has unid herbs, id them
        if (c.isItemInInventory(
            ingredients[4])) { // typically ignored if herb Identifier script is used (ideal)
          cleanHerbs();
        }
        // mix unf potions
        if (c.isItemInInventory(ingredients[1])) {
          makeUnf(); // skips if no herbs/vials in inventory
        }
      }
      if (ingredients[5] == 0
          && ingredients[1] == 468) { // no unid in index, and has pestle in [1] slot
        // grind away
        grind();
      }
    }
  }

  private void bank() {

    c.setStatus("@gre@Banking..");
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
    c.sleep(2000);

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
        c.sleep(1000);
      }
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
        c.sleep(640);
        if (stopWithSecondary) {
          if (c.getBankItemCount(ingredients[2]) < 15) {
            c.log("error, unequal amount of secondaries. stopping. ");
            // endSession();
          }
        }
        // Next bot withdraws 15 secondaries (only if not full inv)
        if (c.getInventoryItemCount() < 30) {
          withdrawSecondary();
          c.sleep(640);
        }
        // }
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
    }
  }

  private void withdrawUnifs() {
    c.setStatus("@cya@Withdrawing Unifs");
    if (c.getBankItemCount(ingredients[5]) > 1) {
      if (ingredients[2] == 1410) { // fish oil potions
        if (c.getBankItemCount(ingredients[5]) > 29) {
          c.withdrawItem(ingredients[5], 29);
          c.sleep(640);
        } else {
          c.withdrawItem(ingredients[5], c.getBankItemCount(ingredients[5]) - 1);
          c.sleep(640);
        }
      } else {
        if (c.getBankItemCount(ingredients[5]) > 15) {
          c.withdrawItem(ingredients[5], 15);
          c.sleep(640);
        } else {
          c.withdrawItem(ingredients[5], c.getBankItemCount(ingredients[5]) - 1);
          c.sleep(640);
        }
      }
    }
  }

  private void withdrawVials() {
    if (c.getBankItemCount(ingredients[0]) > 1) {
      if (c.getBankItemCount(ingredients[0]) > 15) {
        c.withdrawItem(ingredients[0], 15);
        c.sleep(640);
      } else {
        c.withdrawItem(ingredients[0], c.getBankItemCount(ingredients[0]) - 1);
        c.sleep(640);
      }
      // withdraw empty vials if no filled ones are available
    } else if (c.getBankItemCount(ingredients[3]) > 1) {
      if (c.getBankItemCount(ingredients[3]) > 15) {
        c.withdrawItem(ingredients[3], 15);
        c.sleep(640);
      } else {
        c.withdrawItem(ingredients[3], c.getBankItemCount(ingredients[3]) - 1);
        c.sleep(640);
      }
    } else {
      c.log("withdraw Vials and Herbs issue");
      quit(5);
    }
  }

  private void withdrawHerb() {
    // withdraw clean herbs
    if (c.getBankItemCount(ingredients[1]) > 1) {
      if (c.getBankItemCount(ingredients[1]) > 15) {
        c.withdrawItem(ingredients[1], 15);
        c.sleep(300);
      } else {
        c.withdrawItem(ingredients[1], c.getBankItemCount(ingredients[1]) - 1);
        c.sleep(300);
      }
    } else {
      // withdraw unid herbs (or
      if (c.getBankItemCount(ingredients[4]) > 1) {
        if (c.getBankItemCount(ingredients[4]) > 15) {
          c.withdrawItem(ingredients[4], 15);
          c.sleep(300);
        } else {
          c.withdrawItem(ingredients[4], c.getBankItemCount(ingredients[4]) - 1);
          c.sleep(300);
        }
      } else {
        // no clean or unid herbs
        c.log("withdraw herb issue");
        quit(2);
      }
    }
  }

  private void withdrawSecondary() {
    if (ingredients[2] == 1410) { // fish oil
      if (c.getBankItemCount(ingredients[2]) > 1) { //
        if (c.getBankItemCount(ingredients[2]) > 290) {
          c.withdrawItem(ingredients[2], 290);
          c.sleep(300);
        } else {
          c.withdrawItem(ingredients[2], c.getBankItemCount(ingredients[2]) - 1);
          c.sleep(300);
        }
      } else {
        c.log("fish oil issue");
        quit(2);
      }
    } else {
      if (c.getBankItemCount(ingredients[2]) > 1) {
        if (c.getBankItemCount(ingredients[2]) > 15) {
          c.withdrawItem(ingredients[2], 15);
          c.sleep(300);
        } else {
          c.withdrawItem(ingredients[2], c.getBankItemCount(ingredients[2]) - 1);
          c.sleep(300);
        }
      } else {
        c.log("secondaries issue");
        quit(2);
      }
    }
  }

  private void makeUnf() {
    c.setStatus("@cya@Making Unfinished Potions");
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(ingredients[1]), c.getInventoryItemSlotIndex(ingredients[0]));
    c.sleep(1280);
    while (c.isBatching() && c.isRunning()) {
      c.sleep(640);
    }
  }

  private void mix() {
    int before = c.getInventoryItemCount(ingredients[5]);
    c.setStatus("@cya@Adding Secondary");
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(ingredients[2]), c.getInventoryItemSlotIndex(ingredients[5]));
    c.sleep(1280);
    while (c.isBatching() && c.isRunning()) {
      c.sleep(640);
    }
    made += (before - c.getInventoryItemCount(ingredients[5]));
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

  private void grind() {
    c.setStatus("@cya@Grinding " + c.getItemName(ingredients[2]));
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(ingredients[1]), c.getInventoryItemSlotIndex(ingredients[2]));
    c.sleep(1280);
    while (c.isBatching() && c.isRunning()) {
      c.sleep(640);
    }
    // add to total
    made += c.getInventoryItemCount(ingredients[4]);
  }

  private void fillVials() {
    c.setStatus("@cya@Filling Vials");
    try {
      int[] fountainCoords = c.getNearestObjectById(1280);
      c.useItemIdOnObject(fountainCoords[0], fountainCoords[1], ingredients[0]);
      c.sleep(1280);
      while (c.isCurrentlyWalking()) {
        c.sleep(640);
      }
      waitWhileFilling();
    } catch (Exception e) {
      // No Fountain Nearby, wasn't in Falador west bank
      quit(3);
    }
  }

  private void waitWhileFilling() {
    while (c.isBatching()) {
      c.sleep(640);
    }
  }

  private void cleanHerbs() {
    c.setStatus("@cya@Cleaning Herbs");
    c.itemCommand(ingredients[4]);
    c.sleep(1280);
    while (c.isBatching() && c.isRunning()) {
      c.sleep(640);
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
    stopped = true;
    c.stop();
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {

      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (made * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      // Ingredients: Full Vial[0], Clean Herb[1], Secondary[2], Empty Vial[3], Unid Herb[4],
      // Unfinished Potion[5]
      // c.drawBoxAlpha(7, 7, 160, 21+14, 0xFF0000, 128);
      c.drawString("@red@Coleslaw Potion Maker @cya@by Seatta and Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@__________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@" + primaryIngredientName + " Remaining: @gre@" + primaryIngredientInBank,
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@" + secondaryIngredientName + " Remaining: @gre@" + secondaryIngredientTotalInBank,
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Vials Remaining: @gre@" + combinedVialsInBank, x, y + (14 * 3), 0xFFFFFF, 1);
      if (ingredients[1] != 468) {
        c.drawString(
            "@whi@"
                + potion
                + "'s Made: @gre@"
                + String.format("%,d", made)
                + " @yel@(@gre@"
                + String.format("%,d", successPerHr)
                + "@yel@/@whi@hr@yel@)",
            x,
            y + (14 * 4),
            0xFFFFFF,
            1);
      } else {
        c.drawString(
            "@whi@"
                + c.getItemName(ingredients[2])
                + " Ground: @gre@"
                + String.format("%,d", made)
                + " @yel@(@gre@"
                + String.format("%,d", successPerHr)
                + "@yel@/@whi@hr@yel@)",
            x,
            y + (14 * 4),
            0xFFFFFF,
            1);
      }
      if (onlyMakeUnifsCycle) {
        c.drawString(
            "@whi@Time Remaining: "
                + c.timeToCompletion(made, primaryIngredientTotalInBank, startTime),
            x,
            y + (14 * 5),
            0xFFFFFF,
            1);
      } else {
        c.drawString(
            "@whi@Time Remaining: "
                + c.timeToCompletion(
                    made,
                    Math.min(secondaryIngredientTotalInBank, primaryIngredientTotalInBank),
                    startTime),
            x,
            y + (14 * 5),
            0xFFFFFF,
            1);
      }
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 6), 0xFFFFFF, 1);
      c.drawString("@whi@________________", x, y + 3 + (14 * 6), 0xFFFFFF, 1);
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
    JCheckBox stopCraftingSecondaryCheckbox = new JCheckBox("Stop when out of Secondary?", true);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          onlyMakeUnifsCycle = unfinishedPotionCheckbox.isSelected();
          stopWithSecondary = stopCraftingSecondaryCheckbox.isSelected();
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
    scriptFrame.add(stopCraftingSecondaryCheckbox);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }
}
