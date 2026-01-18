package scripting.idlescript;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import controller.Controller;
import java.awt.*;
import models.entities.ItemId;
import models.entities.SceneryId;
import models.entities.SkillId;

public class VialCrafter extends SeattaScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.CRAFTING, Category.IRONMAN_SUPPORTED, Category.ULTIMATE_IRONMAN_SUPPORTED
          },
          "Seatta & Kaila",
          "Crafts vials on Entrana.");

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
  private static final ItemId EDIBLE_SEAWEED = ItemId.EDIBLE_SEAWEED;
  private static final ItemId MOLTEN_GLASS = ItemId.MOLTEN_GLASS;
  private static final ItemId EMPTY_VIAL = ItemId.EMPTY_VIAL;
  private static final ItemId SODA_ASH = ItemId.SODA_ASH;
  private static final ItemId SEAWEED = ItemId.SEAWEED;
  private static final ItemId BUCKET = ItemId.BUCKET;
  private static final ItemId SAND = ItemId.SAND;

  // MADE
  private static Integer VIALS_MADE = 0;

  public int start(String[] param) {
    paintBuilder.start(4, 18, 160);

    // Quits if the required items are missing
    if (getBucketCount() < 1 || getBucketCount() > 13)
      quit(
          QuitReason.MISSING_UNNOTED_INVENTORY_ITEM,
          String.format(" - 1 to 13 %s", "Buckets (or Buckets of Sand)"));

    checkForUnnotedInventoryAmountOrQuit(ItemId.HERB_CLIPPERS, 1);
    checkForUnnotedInventoryAmountOrQuit(ItemId.GLASSBLOWING_PIPE, 1);
    checkForSkillLevelOrQuit(SkillId.HARVESTING, 23);
    checkForSkillLevelOrQuit(SkillId.CRAFTING, 33);
    checkIfPlayerIsInAreaOrQuit(396, 526, 441, 573, "Entrana");

    while (isScriptRunning()) {

      // Fill empty buckets
      while (hasItem(BUCKET) && c.isRunning()) fillBuckets();

      // Harvest or pick up seaweed
      while (!evenBucketsAndSeaweed() && c.isRunning()) harvest();

      // Drop any remaining edible seaweed after collecting seaweed
      dropEdibleSeaweed();

      // Walk to range if on the northern side of Entrana
      if (c.currentY() < 550) {
        c.setStatus("@Cya@Walking to range");
        walkSouth();
      }

      // Make soda ash
      while (hasItem(SEAWEED) && c.isRunning()) makeSodaAsh();

      // Make molten glass
      while (getInventoryItemCount(SODA_ASH) >= getBucketCount() && c.isRunning())
        makeMoltenGlass();

      // Make vials
      while (getInventoryItemCount(MOLTEN_GLASS) > 0 && c.isRunning()) makeVials();
    }
    return quit();
  }

  public void harvest() {
    // Drop edible seaweed if inventory is full
    if (isInventoryFull()) dropEdibleSeaweed();

    if (!evenBucketsAndSeaweed()) {
      // Get seaweed until amount matches number of buckets
      getSeaweed();
    } else {
      // Forcefully stop batching once getting enough seaweed
      forceStopBatching();
    }
  }

  public void getSeaweed() {
    if (!c.isBatching() || !c.isCurrentlyWalking()) {
      int[] PLANT_COORDINATES = c.getNearestObjectById(SceneryId.SEA_WEED.getId());
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
        if (c.getNearestItemById(SEAWEED.getId()) != null && !c.isBatching()) {
          if (c.currentY() > 550) walkNorth();
          pickupSeaweed();
        } else {
          if (!c.isCurrentlyWalking() && c.currentX() != RESET_TILE[0]
              || c.currentY() != RESET_TILE[1]) {
            c.setStatus("@Cya@Walking to reset point");
            if (c.currentY() < 550) walkSouth();

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
    Point groundSeaweed = getNearestGroundItemOf(SEAWEED);
    if (groundSeaweed != null) {
      c.setStatus("@Cya@Picking Up Seaweed (" + groundSeaweed.x + "," + groundSeaweed.y + ")");
      c.pickupItem(groundSeaweed.x, groundSeaweed.y, SEAWEED.getId(), true, false);
      while (c.isCurrentlyWalking()) c.sleep(640);

      c.sleep(640);
    }
  }

  public void dropEdibleSeaweed() {
    if (hasUnnotedInventoryAmount(ItemId.EDIBLE_SEAWEED, 1)) {
      c.walkTo(c.currentX(), c.currentY());
      c.waitForBatching(false);
      c.setStatus("@Cya@Dropping Edible Seaweed");
      dropAllOfItem(ItemId.EDIBLE_SEAWEED);
      c.sleep(1280);
      while (hasUnnotedItem(ItemId.EDIBLE_SEAWEED) && c.isRunning()) c.sleep(640);
    }
  }

  public void fillBuckets() {
    c.setStatus("@Cya@Filling Buckets");
    c.useItemIdOnObject(429, 566, BUCKET.getId());
    do c.sleep(1280);
    while ((c.isRunning() && c.isBatching() && !c.getNeedToMove()) || c.isCurrentlyWalking());
  }

  public void makeSodaAsh() {
    c.setStatus("@Cya@Making Soda Ash");
    c.useItemIdOnObject(426, 560, SEAWEED.getId());
    c.sleep(1280);
    while ((c.isRunning() && c.isBatching() && !c.getNeedToMove()) || c.isCurrentlyWalking())
      c.sleep(640);
  }

  public void makeMoltenGlass() {
    c.setStatus("@Cya@Making Molten Glass");
    c.useItemIdOnObject(419, 559, SAND.getId());
    c.sleep(1280);
    while ((c.isRunning() && c.isBatching() && !c.getNeedToMove()) || c.isCurrentlyWalking())
      c.sleep(640);
  }

  public void makeVials() {
    int startVials = getInventoryItemCount(EMPTY_VIAL);
    c.setStatus("@Cya@Making Vials");
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(ItemId.GLASSBLOWING_PIPE.getId()),
        c.getInventoryItemSlotIndex(MOLTEN_GLASS.getId()));
    c.sleep(1280);
    c.optionAnswer(0);
    c.waitForBatching(false);
    VIALS_MADE += (getInventoryItemCount(EMPTY_VIAL) - startVials);
  }

  public void walkNorth() {
    // Use the shortcut if the player has the required agility level
    if (hasSkillLevel(SkillId.AGILITY, 55)) {
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
    if (hasSkillLevel(SkillId.AGILITY, 55)) {
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
   * @param walkPath Integer[][] -- Integer Tile[] or separate tile X and Y values per index
   */
  public void walk(Integer[][] walkPath) {
    for (Integer[] integers : walkPath) {
      while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
      c.walkTo(integers[0], integers[1]);
      do c.sleep(1280);
      while (c.isCurrentlyWalking() && c.isRunning());
    }
  }

  public boolean evenBucketsAndSeaweed() {
    return (getInventoryItemCount(SEAWEED) + getInventoryItemCount(SODA_ASH)) >= getBucketCount();
  }

  public int getBucketCount() {
    return getInventoryItemCount(BUCKET) + getInventoryItemCount(SAND);
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      paintBuilder.setBorderColor(colorPurple);
      paintBuilder.setBackgroundColor(colorDarkGray, 255);

      paintBuilder.setTitleCenteredSingleColor("VialCrafter", colorPurple, 4);
      paintBuilder.addRow(rowBuilder.centeredSingleStringRow("Seatta", colorPurple, 1));
      String[] strings = {
        String.format("Made: %s", VIALS_MADE), paintBuilder.stringAmountPerHour(VIALS_MADE)
      };
      int[] colors = {colorWhite, colorYellow};
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
