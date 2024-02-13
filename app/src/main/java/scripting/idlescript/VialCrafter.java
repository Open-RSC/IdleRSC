package scripting.idlescript;

import bot.Main;
import controller.Controller;
import models.entities.ItemId;

/**
 * VialCrafter
 *
 * <p>Craft Vials on Entrana
 *
 * <p>Items Required: 1 Glassblowing Pipe 1 Herb Clippers Up to 13 Buckets
 *
 * <p>Levels Required: 23 Harvesting 33 Crafting
 *
 * @author Seatta with fixes by Kaila
 */
public class VialCrafter extends IdleScript {
  protected static final Controller c = Main.getController();

  // TILE COORDINATES
  private static final Integer[] NORTH_OF_SHORTCUT = {434, 549};
  private static final Integer[] SOUTH_OF_SHORTCUT = {434, 551};
  private static final Integer[] CHICKEN_PEN = {409, 552};
  private static final Integer[] NORTH_WEST = {431, 539};
  private static final Integer[] RESET_TILE = {431, 554};
  private static final Integer[] SHORTCUT = {434, 550};
  private static final Integer[] FURNACE = {423, 555};

  // ITEM IDS
  private static final Integer GLASSBLOWING_PIPE = ItemId.GLASSBLOWING_PIPE.getId();
  private static final Integer EDIBLE_SEAWEED = ItemId.EDIBLE_SEAWEED.getId();
  private static final Integer HERB_CLIPPERS = ItemId.HERB_CLIPPERS.getId();
  private static final Integer MOLTEN_GLASS = ItemId.MOLTEN_GLASS.getId();
  private static final Integer EMPTY_VIAL = ItemId.EMPTY_VIAL.getId();
  private static final Integer SODA_ASH = ItemId.SODA_ASH.getId();
  private static final Integer SEAWEED = ItemId.SEAWEED.getId();
  private static final Integer BUCKET = ItemId.BUCKET.getId();
  private static final Integer SAND = ItemId.SAND.getId();

  // OBJECT IDS
  private static final Integer SEAWEED_PLANT = 1280;

  // LEVEL REQUIREMENTS
  private static final Integer HARVESTING_LEVEL = 23;
  private static final Integer CRAFTING_LEVEL = 33;
  private static final Integer AGILITY_LEVEL = 55; // OPTIONAL FOR SHORTCUT

  // MADE
  private static Integer VIALS_MADE = 0;

  public int start(String[] param) {
    paintBuilder.start(4, 18, 160);

    // Quits if the required items are missing
    if (c.isRunning()
        && (c.getInventoryItemCount(HERB_CLIPPERS) < 1
            || c.getInventoryItemCount(GLASSBLOWING_PIPE) < 1
            || getBucketCount() < 1
            || getBucketCount() > 13)) {
      quit(2);
    }

    // Quits if the player does not have the required levels
    if (c.isRunning()
        && (!hasSkillLevel("Harvesting", HARVESTING_LEVEL)
            || !hasSkillLevel("Crafting", CRAFTING_LEVEL))) {
      quit(3);
    }

    // Quits if the player is not on Entrana
    if (c.isRunning()
        && (c.currentY() > 573 || c.currentY() < 526 || c.currentX() > 441 || c.currentX() < 396)) {
      quit(4);
    }

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      // Fill empty buckets
      while (c.getInventoryItemCount(BUCKET) > 0 && c.isRunning()) {
        fillBuckets();
      }
      // Harvest or pick up seaweed
      while (!evenBucketsAndSeaweed() && c.isRunning()) {
        harvest();
      }
      // Drop any remaining edible seaweed after collecting seaweed
      dropEdibleSeaweed();
      // Walk to range if on the northern side of Entrana
      if (c.currentY() < 550) {
        c.setStatus("@Cya@Walking to range");
        walkSouth();
      }
      // Make soda ash
      while (c.getInventoryItemCount(SEAWEED) > 0 && c.isRunning()) {
        makeSodaAsh();
      }
      // Make molten glass
      while (c.getInventoryItemCount(SODA_ASH) >= getBucketCount() && c.isRunning()) {
        makeMoltenGlass();
      }
      // Make vials
      while (c.getInventoryItemCount(MOLTEN_GLASS) > 0 && c.isRunning()) {
        makeVials();
      }
    }
    // Quits if the controller is stopped.
    quit(1);
    return 1000;
  }

  public void harvest() {
    // Drop edible seaweed if inventory is full
    if (c.getInventoryItemCount() == 30) {
      dropEdibleSeaweed();
    }
    // Get seaweed until amount matches number of buckets
    if (!evenBucketsAndSeaweed()) {
      getSeaweed();
    } else {
      // Forcefully stop batching once getting enough seaweed
      if (c.isBatching()) c.stopBatching();
    }
  }

  public void getSeaweed() {
    if (!c.isBatching() || !c.isCurrentlyWalking()) {
      int[] PLANT_COORDINATES = c.getNearestObjectById(SEAWEED_PLANT);
      if (PLANT_COORDINATES != null) {
        c.setStatus(
            "@Cya@Harvesting Seaweed (" + PLANT_COORDINATES[0] + "," + PLANT_COORDINATES[1] + ")");
        // Travel to the other side of the island if target seaweed is over there
        if (PLANT_COORDINATES[1] > 550 && c.currentY() < 550) {
          walkSouth();
        } else if (PLANT_COORDINATES[1] < 550 & c.currentY() > 550) {
          walkNorth();
        }
        // Harvest seaweed if not already batching or walking
        if (!c.isBatching() && !c.isCurrentlyWalking()) {
          c.atObject(PLANT_COORDINATES[0], PLANT_COORDINATES[1]);
          c.sleep(1280);
        }
      } else {
        if (c.getNearestItemById(SEAWEED) != null && !c.isBatching()) {
          if (c.currentY() > 550) {
            walkNorth();
          }
          pickupSeaweed();
        } else {
          if (!c.isCurrentlyWalking() && c.currentX() != RESET_TILE[0]
              || c.currentY() != RESET_TILE[1]) {
            c.setStatus("@Cya@Walking to reset point");
            if (c.currentY() < 550) {
              walkSouth();
            }
            Integer[][] path = {RESET_TILE};
            walk(path);
            c.setStatus("@Cya@Waiting for seaweed");
          }
        }
      }
    } else {
      // Sleep while batching or walking
      c.sleep(1280);
    }
  }

  public void pickupSeaweed() {
    int[] groundSeaweed = c.getNearestItemById(SEAWEED);
    if (groundSeaweed != null) {
      c.setStatus("@Cya@Picking Up Seaweed (" + groundSeaweed[0] + "," + groundSeaweed[1] + ")");
      c.pickupItem(groundSeaweed[0], groundSeaweed[1], SEAWEED, true, false);
      while (c.isCurrentlyWalking()) {
        c.sleep(640);
      }
      c.sleep(640);
    }
  }

  public void dropEdibleSeaweed() {
    if (c.getInventoryItemCount(EDIBLE_SEAWEED) > 0) {
      c.walkTo(c.currentX(), c.currentY());
      c.waitForBatching(false);
      c.setStatus("@Cya@Dropping Edible Seaweed");
      c.dropItem(
          c.getInventoryItemSlotIndex(EDIBLE_SEAWEED), c.getInventoryItemCount(EDIBLE_SEAWEED));
      c.sleep(1280);
      while (c.getInventoryItemCount(EDIBLE_SEAWEED) > 0 && c.isRunning()) {
        c.sleep(640);
      }
    }
  }

  public void fillBuckets() {
    c.setStatus("@Cya@Filling Buckets");
    c.useItemIdOnObject(429, 566, BUCKET);
    c.sleep(1280);
    while ((c.isRunning() && c.isBatching() && !c.getNeedToMove()) || c.isCurrentlyWalking()) {
      c.sleep(1280);
    }
  }

  public void makeSodaAsh() {
    c.setStatus("@Cya@Making Soda Ash");
    c.useItemIdOnObject(426, 560, SEAWEED);
    c.sleep(1280);
    while ((c.isRunning() && c.isBatching() && !c.getNeedToMove()) || c.isCurrentlyWalking()) {
      c.sleep(640);
    }
  }

  public void makeMoltenGlass() {
    c.setStatus("@Cya@Making Molten Glass");
    c.useItemIdOnObject(419, 559, SAND);
    c.sleep(1280);
    while ((c.isRunning() && c.isBatching() && !c.getNeedToMove()) || c.isCurrentlyWalking()) {
      c.sleep(640);
    }
  }

  public void makeVials() {
    int startVials = controller.getInventoryItemCount(EMPTY_VIAL);
    c.setStatus("@Cya@Making Vials");
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(GLASSBLOWING_PIPE), c.getInventoryItemSlotIndex(MOLTEN_GLASS));
    c.sleep(1280);
    c.optionAnswer(0);
    c.waitForBatching(false);
    VIALS_MADE += (controller.getInventoryItemCount(EMPTY_VIAL) - startVials);
  }

  public void walkNorth() {
    // Use the shortcut if the player has the required agility level
    if (hasSkillLevel("Agility", AGILITY_LEVEL)) {
      Integer[][] walkPath = {SOUTH_OF_SHORTCUT};
      walk(walkPath);
      c.atObject(SHORTCUT[0], SHORTCUT[1]);
      c.sleep(1280);
    } else {
      Integer[][] walkPath = {FURNACE, CHICKEN_PEN, NORTH_WEST};
      walk(walkPath);
    }
  }

  public void walkSouth() {
    // Use the shortcut if the player has the required agility level
    if (hasSkillLevel("Agility", AGILITY_LEVEL)) {
      Integer[][] walkPath = {NORTH_OF_SHORTCUT};
      walk(walkPath);
      c.atObject(SHORTCUT[0], SHORTCUT[1]);
      c.sleep(1280);
      walkPath = new Integer[][] {RESET_TILE};
      walk(walkPath);
    } else {
      Integer[][] walkPath = {NORTH_WEST, CHICKEN_PEN, FURNACE};
      walk(walkPath);
    }
  }

  /**
   * Follows a given path of tile coordinates
   *
   * @param walkPath Integer[][] -- Integer Tile[] or seperate tile X and Y values per index
   */
  public void walk(Integer[][] walkPath) {
    for (int i = 0; i < walkPath.length; ++i) {
      if (c.isCurrentlyWalking()) {
        while (c.isCurrentlyWalking() && c.isRunning()) {
          c.sleep(640);
        }
      }
      c.walkTo(walkPath[i][0], walkPath[i][1]);
      c.sleep(1280);
      while (c.isCurrentlyWalking() && c.isRunning()) {
        c.sleep(1280);
      }
    }
  }

  public boolean evenBucketsAndSeaweed() {
    return (c.getInventoryItemCount(SEAWEED) + c.getInventoryItemCount(SODA_ASH))
        >= getBucketCount();
  }

  public int getBucketCount() {
    return c.getInventoryItemCount(BUCKET) + c.getInventoryItemCount(SAND);
  }

  public boolean hasSkillLevel(String skillName, Integer requiredLevel) {
    return c.getBaseStat(c.getStatId(skillName)) >= requiredLevel;
  }

  public void quit(int i) {
    switch (i) {
      case 1:
        c.displayMessage("@red@Script has been stopped!");
        break;
      case 2:
        c.displayMessage(
            "@red@Please start the script with the following items in your inventory:");
        c.displayMessage("@red@Herb Clippers, Glass Blowing Pipe, and");
        c.displayMessage("@red@up to 13 buckets");
        break;
      case 3:
        c.displayMessage("@red@You need level 23 harvesting and 33 crafting to use this script.");
        break;
      case 4:
        c.displayMessage("@red@Start the script on Entrana.");
        break;
      default:
        c.displayMessage("@red@The script has unexpectedly stopped!");
        break;
    }
    c.stop();
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      // Colors are based on https://spec.draculatheme.com/#sec-Standard
      int purple = 0xBD93F9;
      int darkGray = 0x282A36;
      int darkerGray = 0x1d1f27;
      int white = 0xF8F8F2;
      int green = 0x50FA7B;
      int yellow = 0xF1FA8C;
      int red = 0xFF5555;

      paintBuilder.setBorderColor(purple);
      paintBuilder.setBackgroundColor(darkGray, 255);

      paintBuilder.setTitleCenteredSingleColor("VialCrafter", purple, 4);
      paintBuilder.addRow(rowBuilder.centeredSingleStringRow("Seatta", purple, 1));
      String[] strings = {
        "Made: " + String.valueOf(VIALS_MADE), paintBuilder.stringAmountPerHour(VIALS_MADE)
      };
      int[] colors = {white, yellow};
      int[] xOffsets = {
        4,
        paintBuilder.getWidth()
            - c.getStringWidth(strings[1], 1)
            - (strings[1].charAt(strings[1].length() - 1) == '1' ? 3 : 4)
            - 3
      };
      paintBuilder.addRow(rowBuilder.multipleStringRow(strings, colors, xOffsets, 1));

      paintBuilder.draw();
    }
  }
}
