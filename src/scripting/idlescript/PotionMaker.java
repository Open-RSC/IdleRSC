package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * PotionMaker Script by Seatta, Updated by Kaila.
 *
 * <p>Batch bars MUST be toggles on to function properly.
 *
 * <p>Kaila - added unif only option. Changed method to 15/15 inventories for efficiency. added
 * option to stop making unif/etc when out of secondary.
 *
 * <p>* todo * add uranium support/no batching support
 */
public class PotionMaker extends IdleScript {
  //
  String potion = "";
  String primaryIngredientName = "";
  String secondaryIngredientName = "";
  Integer levelReq = 0;
  int combinedVialsInBank = 0;
  int primaryIngredientTotalInBank = 0;
  int primaryIngredientInBank = 0;
  int secondaryIngredientTotalInBank = 0;
  long startTimestamp = System.currentTimeMillis() / 1000L;
  long startTime;
  JFrame scriptFrame = null;
  Boolean stopped = false;
  Boolean guiSetup = false;
  boolean scriptStarted = false;
  boolean onlyMakeUnifsCycle = false;
  boolean stopWithSecondary = false;
  Integer made = 0;
  // Ingredients: Full Vial, Clean Herb, Secondary, Empty Vial, Unid Herb, Unfinished Potion
  Integer ingredients[] = {464, 0, 0, 465, 0, 0};

  public int start(String[] parameters) {
    if (!guiSetup) {
      controller.setStatus("@cya@Setting up script");
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted == true) {
      controller.quitIfAuthentic();
      scriptStart();
    }
    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    // Ingredients: Full Vial[0], Clean Herb[1], Secondary[2], Empty Vial[3], Unid Herb[4],
    // Unfinished Potion[5]
    while (controller.isRunning()) {

      bank();
      if (onlyMakeUnifsCycle == false) {
        // mix pots
        mix();
      } // have 1 or 4 AND 0 or 3
      if (ingredients[5] != 0
          && ((controller.isItemInInventory(ingredients[1])
                  || controller.isItemInInventory(ingredients[4]))
              && (controller.isItemInInventory(ingredients[0])
                  || controller.isItemInInventory(ingredients[3])))) {
        if (controller.isItemInInventory(ingredients[3])) { // typically ignored
          fillVials();
        }
        // if inventory has unid herbs, id them
        if (controller.isItemInInventory(
            ingredients[4])) { // typically ignored if herb Identifier script is used (ideal)
          cleanHerbs();
        }
        // mix unf potions
        if (controller.isItemInInventory(ingredients[1])) {
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

  public void bank() {

    controller.setStatus("@gre@Banking..");
    if (!controller.isInBank()) {
      int[] bankerIds = {95, 224, 268, 540, 617, 792};
      ORSCharacter npc = controller.getNearestNpcByIds(bankerIds, false);
      if (npc != null) {
        controller.setStatus("@yel@Walking to Banker..");
        controller.displayMessage("@yel@Walking to Banker..");
        controller.walktoNPCAsync(npc.serverIndex);
        controller.sleep(200);
      } else {
        controller.log("@red@walking to Bank Error..");
        controller.sleep(1000);
      }
    }
    controller.openBank();
    controller.sleep(640);

    if (controller.isInBank()) {
      // Ingredients: Full Vial[0], Clean Herb[1], Secondary[2], Empty Vial[3], Unid Herb[4],
      // Unfinished Potion[5]
      // we need to deposit everything into bank
      // first, bot opens bank and deposits all, leaving bank open
      controller.setStatus("@cya@Depositing items");

      combinedVialsInBank = controller.getBankItemCount(464) + controller.getBankItemCount(465);

      if (controller.getInventoryItemCount() > 0) {
        for (int itemId : controller.getInventoryItemIds()) {
          // if (itemId != 168 && itemId != 1263 && itemId != barId) {
          controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
          // }
        }
        controller.sleep(640);
      }
      if (ingredients[5] != 0) {
        // now we count up unif potions + math.min((clean+ dirty herbs),(Empty + full vials)) = #
        // you can craft
        primaryIngredientTotalInBank =
            controller.getBankItemCount(ingredients[5]) // unifs
                + Math.min(
                    (controller.getBankItemCount(ingredients[1])
                        + controller.getBankItemCount(ingredients[4])),
                    (controller.getBankItemCount(ingredients[0])
                        + controller.getBankItemCount(ingredients[3])));
        primaryIngredientInBank =
            controller.getBankItemCount(ingredients[1])
                + controller.getBankItemCount(ingredients[4])
                + controller.getBankItemCount(ingredients[5]);
        secondaryIngredientTotalInBank = controller.getBankItemCount(ingredients[2]);
        // Next, bot withdraws max number of unifs (up to 15 if in bank)
        if (onlyMakeUnifsCycle == false) {
          withdrawUnifs();
        }
        // withdraw vials/empty vials if not 15 items in inventory (this part also withdraws herbs)
        if (controller.getInventoryItemCount() == 0) {
          withdrawVialsAndHerbs();
        }
        if (stopWithSecondary == true) {
          if (controller.getBankItemCount(ingredients[2]) < 15) {
            controller.log("error, unequal amount of secondaries. stopping. ");
            // endSession();
          }
        }
        // Next bot withdraws 15 secondaries (only if not full inv)
        if (controller.getInventoryItemCount() < 30) {
          withdrawSecondary();
          controller.sleep(300);
        }
        // }
      } else {
        // need [2] for primary and [4] for secondary when grinding
        primaryIngredientTotalInBank = controller.getBankItemCount(ingredients[2]);
        primaryIngredientInBank = controller.getBankItemCount(ingredients[2]);
        secondaryIngredientTotalInBank = controller.getBankItemCount(ingredients[4]);

        // withdraw pestle & mortar
        withdrawPestle();
        // withdraw pre-secondary
        withdrawPre();
      }
      controller.closeBank(); // Next, close back
      controller.sleep(640);
    }
  }

  public void withdrawUnifs() {
    controller.setStatus("@cya@Withdrawing Unifs");
    if (controller.getBankItemCount(ingredients[5]) > 1) {
      if (controller.getBankItemCount(ingredients[5]) > 15) {
        controller.withdrawItem(ingredients[5], 15);
        controller.sleep(640);
      } else {
        controller.withdrawItem(ingredients[5], controller.getBankItemCount(ingredients[5]) - 1);
        controller.sleep(640);
      }
    }
  }

  public void withdrawVialsAndHerbs() {
    if (controller.getBankItemCount(ingredients[0]) > 1) {
      if (controller.getBankItemCount(ingredients[0]) > 15) {
        controller.withdrawItem(ingredients[0], 15);
        controller.sleep(640);
      } else {
        controller.withdrawItem(ingredients[0], controller.getBankItemCount(ingredients[0]) - 1);
        controller.sleep(640);
      }
      // withdraw empty vials if no filled ones are available
    } else if (controller.getBankItemCount(ingredients[3]) > 1) {
      if (controller.getBankItemCount(ingredients[3]) > 15) {
        controller.withdrawItem(ingredients[3], 15);
        controller.sleep(640);
      } else {
        controller.withdrawItem(ingredients[3], controller.getBankItemCount(ingredients[3]) - 1);
        controller.sleep(640);
      }
    } else {
      controller.log("withdraw Vials and Herbs issue");
      quit(5);
    }
    withdrawHerb();
  }

  public void withdrawHerb() {
    // withdraw clean herbs
    if (controller.getBankItemCount(ingredients[1]) > 1) {
      if (controller.getBankItemCount(ingredients[1]) > 15) {
        controller.withdrawItem(ingredients[1], 15);
        controller.sleep(300);
      } else {
        controller.withdrawItem(ingredients[1], controller.getBankItemCount(ingredients[5]) - 1);
        controller.sleep(300);
      }
    } else {
      // withdraw unid herbs (or
      if (controller.getBankItemCount(ingredients[4]) > 1) {
        if (controller.getBankItemCount(ingredients[4]) > 15) {
          controller.withdrawItem(ingredients[4], 15);
          controller.sleep(300);
        } else {
          controller.withdrawItem(ingredients[4], controller.getBankItemCount(ingredients[4]) - 1);
          controller.sleep(300);
        }
      } else {
        // no clean or unid herbs
        controller.log("withdraw herb issue");
        quit(2);
      }
    }
  }

  public void withdrawSecondary() {
    if (ingredients[2] == 1410) { // fish oil
      if (controller.getBankItemCount(ingredients[2]) > 1) {
        if (controller.getBankItemCount(ingredients[2]) > 100) {
          controller.withdrawItem(ingredients[2], 100);
          controller.sleep(300);
        } else {
          controller.withdrawItem(ingredients[2], controller.getBankItemCount(ingredients[2]) - 1);
          controller.sleep(300);
        }
      } else {
        controller.log("fish oil issue");
        quit(2);
      }
    } else {
      if (controller.getBankItemCount(ingredients[2]) > 1) {
        if (controller.getBankItemCount(ingredients[2]) > 15) {
          controller.withdrawItem(ingredients[2], 15);
          controller.sleep(300);
        } else {
          controller.withdrawItem(ingredients[2], controller.getBankItemCount(ingredients[2]) - 1);
          controller.sleep(300);
        }
      } else {
        controller.log("secondaries issue");
        quit(2);
      }
    }
  }

  public void makeUnf() {
    controller.setStatus("@cya@Making Unfinished Potions");
    controller.useItemOnItemBySlot(
        controller.getInventoryItemSlotIndex(ingredients[1]),
        controller.getInventoryItemSlotIndex(ingredients[0]));
    controller.sleep(1280);
    while (controller.isBatching() && controller.isRunning()) {
      controller.sleep(640);
    }
  }

  public void mix() {
    int before = controller.getInventoryItemCount(ingredients[5]);
    controller.setStatus("@cya@Adding Secondary");
    controller.useItemOnItemBySlot(
        controller.getInventoryItemSlotIndex(ingredients[2]),
        controller.getInventoryItemSlotIndex(ingredients[5]));
    controller.sleep(1280);
    while (controller.isBatching() && controller.isRunning()) {
      controller.sleep(640);
    }
    made += (before - controller.getInventoryItemCount(ingredients[5]));
  }

  public void withdrawPestle() {
    if (controller.getInventoryItemCount(468) == 0) { // 468 is pestal
      controller.setStatus("@cya@Withdrawing Pestle..");
      if (controller.getBankItemCount(468) == 0) {
        controller.log("pestle issue");
        quit(2);
      }
      controller.withdrawItem(ingredients[1], 1 - controller.getInventoryItemCount(468));
      controller.sleep(1280);
    }
  }

  public void withdrawPre() {
    if (controller.getBankItemCount(ingredients[2]) > 0) {
      if (controller.getBankItemCount(ingredients[2]) > 29) {
        controller.withdrawItem(ingredients[2], 29);
        controller.sleep(1280);
      } else {
        controller.withdrawItem(ingredients[2], controller.getBankItemCount(ingredients[2]));
        controller.sleep(1280);
      }
    } else {
      controller.log("pre issue");
      quit(2);
    }
  }

  public void grind() {
    controller.setStatus("@cya@Grinding " + controller.getItemName(ingredients[2]));
    controller.useItemOnItemBySlot(
        controller.getInventoryItemSlotIndex(ingredients[1]),
        controller.getInventoryItemSlotIndex(ingredients[2]));
    controller.sleep(1280);
    while (controller.isBatching() && controller.isRunning()) {
      controller.sleep(640);
    }
    // add to total
    made += controller.getInventoryItemCount(ingredients[4]);
  }

  public void fillVials() {
    controller.setStatus("@cya@Filling Vials");
    try {
      int fountainCoords[] = controller.getNearestObjectById(1280);
      controller.useItemIdOnObject(fountainCoords[0], fountainCoords[1], ingredients[0]);
      controller.sleep(1280);
      while (controller.isCurrentlyWalking()) {
        controller.sleep(640);
      }
      while (controller.isBatching()) {
        controller.sleep(640);
      }
    } catch (Exception e) {
      // No Fountain Nearby, wasn't in Falador west bank
      quit(3);
    }
  }

  public void cleanHerbs() {
    controller.setStatus("@cya@Cleaning Herbs");
    controller.itemCommand(ingredients[4]);
    controller.sleep(1280);
    while (controller.isBatching() && controller.isRunning()) {
      controller.sleep(640);
    }
  }
  // Ingredients: Full Vial[0],
  // Clean Herb[1],
  // Secondary[2],
  // Empty Vial[3],
  // Unid Herb[4],
  // Unfinished Potion[5]

  public void quit(Integer i) {
    if (!stopped) {
      if (i == 1) {
        controller.log("Error Code: 1");
        controller.displayMessage("@red@Script stopped!");
        controller.setStatus("@red@Script stopped!");
      } else if (i == 2) {
        controller.log("Error Code: 2");
        controller.displayMessage("@red@Out of ingredients!");
        controller.displayMessage("@red@This potion requires:");
        controller.displayMessage("@red@" + controller.getItemName(ingredients[1]));
        controller.displayMessage("@red@" + controller.getItemName(ingredients[2]));
        controller.setStatus("@red@Out of ingredients!");
      } else if (i == 3) {
        controller.log("Error Code: 3");
        controller.displayMessage(
            "@red@Unable to fill vials, run the script in Falador West Bank!");
        controller.setStatus("@red@Start in Falador west bank!");
      } else if (i == 4) {
        controller.log("Error Code: 4");
        controller.displayMessage(
            "@red@This potion requires level " + levelReq + " Herblaw to make.");
        controller.setStatus("@red@Herblaw level not high enough!");
      } else if (i == 5) {
        controller.log("Error Code: 5");
        controller.displayMessage("@red@Out of vials.");
        controller.setStatus("@red@Out of vials!");
      }
    }
    stopped = true;
    // scriptStarted = false;
    // guiSetup = false;
    controller.stop();
  }

  public static void centerWindow(Window frame) {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
    frame.setLocation(x, y);
  }

  public void setValuesFromGUI(
      JCheckBox unfinishedPotionCheckbox, JCheckBox stopCraftingSecondaryCheckbox) {
    if (!unfinishedPotionCheckbox.isSelected()) {
      onlyMakeUnifsCycle = false;
      controller.log("@cya@Making Potions & Unifs");
    } else {
      onlyMakeUnifsCycle = true;
      controller.log("@cya@Only Making Unifs");
    }
    if (!stopCraftingSecondaryCheckbox.isSelected()) {
      stopWithSecondary = false;
      controller.log("@cya@Will keep making unifs when out of secondaries");
    } else {
      stopWithSecondary = true;
      controller.log("@cya@Will stop crafting when out of secondaries");
    }
  }

  public static String msToString(long milliseconds) {
    long sec = milliseconds / 1000;
    long min = sec / 60;
    long hour = min / 60;
    sec %= 60;
    min %= 60;
    DecimalFormat twoDigits = new DecimalFormat("00");

    return new String(
        twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec));
  }
  /** credit to chomp for toTimeToCompletion (from AA_Script) (totalBars, barsInBank, startTime) */
  public static String toTimeToCompletion(
      final int processed, final int remaining, final long time) {
    if (processed == 0) {
      return "0:00:00";
    }

    final double seconds = (System.currentTimeMillis() - time) / 1000.0;
    final double secondsPerItem = seconds / processed;
    final long ttl = (long) (secondsPerItem * remaining);
    return String.format("%d:%02d:%02d", ttl / 3600, (ttl % 3600) / 60, (ttl % 60));
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      String runTime = msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      try {
        float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (made * scale);
      } catch (Exception e) {
        // divide by zero
      }
      // Ingredients: Full Vial[0], Clean Herb[1], Secondary[2], Empty Vial[3], Unid Herb[4],
      // Unfinished Potion[5]
      // controller.drawBoxAlpha(7, 7, 160, 21+14, 0xFF0000, 128);
      controller.drawString(
          "@red@Coleslaw Potion Maker @cya@by Seatta and Kaila", 6, 21, 0xFFFFFF, 1);
      controller.drawString("@whi@__________________", 6, 21 + 3, 0xFFFFFF, 1);
      controller.drawString(
          "@whi@" + primaryIngredientName + " Remaining: @gre@" + this.primaryIngredientInBank,
          6,
          21 + 17,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@"
              + secondaryIngredientName
              + " Remaining: @gre@"
              + this.secondaryIngredientTotalInBank,
          6,
          21 + 17 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Total Vials Remaining: @gre@" + this.combinedVialsInBank,
          6,
          21 + 17 + 14 + 14,
          0xFFFFFF,
          1);
      if (ingredients[1] != 468) {
        controller.drawString(
            "@whi@"
                + potion
                + "'s Made: @gre@"
                + String.format("%,d", made)
                + " @yel@(@gre@"
                + String.format("%,d", successPerHr)
                + "@yel@/@whi@hr@yel@)",
            6,
            21 + 17 + 28 + 14,
            0xFFFFFF,
            1);
      } else {
        controller.drawString(
            "@whi@"
                + controller.getItemName(ingredients[2])
                + " Ground: @gre@"
                + String.format("%,d", made)
                + " @yel@(@gre@"
                + String.format("%,d", successPerHr)
                + "@yel@/@whi@hr@yel@)",
            6,
            21 + 17 + 28 + 14,
            0xFFFFFF,
            1);
      }
      if (onlyMakeUnifsCycle == true) {
        controller.drawString(
            "@whi@Time Remaining: "
                + toTimeToCompletion(made, this.primaryIngredientTotalInBank, startTime),
            6,
            21 + 17 + 42 + 14,
            0xFFFFFF,
            1);
      } else {
        controller.drawString(
            "@whi@Time Remaining: "
                + toTimeToCompletion(
                    made,
                    Math.min(
                        this.secondaryIngredientTotalInBank, this.primaryIngredientTotalInBank),
                    startTime),
            6,
            21 + 17 + 42 + 14,
            0xFFFFFF,
            1);
      }
      controller.drawString("@whi@Runtime: " + runTime, 6, 21 + 17 + 56 + 14, 0xFFFFFF, 1);
      controller.drawString("@whi@________________", 6, 21 + 17 + 56 + 14 + 3, 0xFFFFFF, 1);
    }
  }

  public void parseValues() {
    startTime = System.currentTimeMillis();
    controller.displayMessage(
        "@red@REQUIRES Batch bars be toggle on in settings to work correctly!");
    if (controller.getBaseStat(controller.getStatId("Herblaw")) < levelReq) {
      // Herblaw level too low
      quit(4);
    }
    if (ingredients[1] != 468) {
      controller.displayMessage("@cya@Making: " + potion + " Potion");
    } else {
      controller.displayMessage(
          "@cya@Grinding: "
              + controller.getItemName(ingredients[2])
              + " into "
              + controller.getItemName(ingredients[4]));
    }
    controller.displayMessage("@cya@Using Main Ingredient: " + primaryIngredientName);
    controller.displayMessage("@cya@Using Secondary Ingredient: " + secondaryIngredientName);
  }

  public void setupGUI() {
    JLabel header = new JLabel("Potion Maker - Searos & Kaila");
    JLabel batchLabel = new JLabel("Batch Bars MUST be toggled ON in settings!!!");
    JLabel batchLabel2 = new JLabel("This ensures 15 items are made each bank action.");
    JLabel potionLabel = new JLabel("Select Potion/Secondary");
    JComboBox<String> potionField =
        new JComboBox<String>(
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
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setValuesFromGUI(unfinishedPotionCheckbox, stopCraftingSecondaryCheckbox);
            parseValues();
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            switch (potionField.getSelectedItem().toString()) {

                /**
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
            if (potionField.getSelectedItem().toString() != ""
                && potionField.getSelectedItem().toString() != "0") {
              potion = potionField.getSelectedItem().toString();
              scriptStarted = true;
              stopped = false;
            } else {
              setupGUI();
            }

            // stopped = false;
          }
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

    centerWindow(scriptFrame);
    scriptFrame.setVisible(true);
    scriptFrame.pack();
    scriptFrame.requestFocusInWindow();
  }
}
