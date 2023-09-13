package scripting.idlescript;

import bot.Main;
import controller.Controller;

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

  public int start(String[] param) {

    // Quits if the required items are missing
    if ((c.isRunning() && c.getInventoryItemCount(1357) < 1)
        || c.getInventoryItemCount(621) < 1
        || getBucketCount() < 1
        || getBucketCount() > 13) {
      quit(2);
    }

    // Quits if the player does not have the required levels
    if ((c.isRunning() && c.getBaseStat(c.getStatId("Harvesting")) < 23)
        || (c.getBaseStat(c.getStatId("Crafting")) < 33 && c.isRunning())) {
      quit(3);
    }

    // Quits if the player is not on Entrana
    if ((c.isRunning() && c.currentY() > 573)
        || c.currentY() < 526
        || c.currentX() > 441
        || (c.currentX() < 396 && c.isRunning())) {
      quit(4);
    }

    while (c.isRunning()) {
      // Fill empty buckets
      while (c.getInventoryItemCount(21) > 0 && c.isRunning()) {
        fillBuckets();
      }
      // Harvest or pick up seaweed
      while (!evenBucketsAndSeaweed() && c.isRunning()) {
        harvest();
      }
      // Walk to range if on the northern side of Entrana
      if (c.currentY() < 550 && c.isRunning()) {
        c.setStatus("@Cya@Walking to range");
        walkSouth();
      }
      // Make soda ash
      while (c.getInventoryItemCount(622) > 0 && c.isRunning()) {
        makeSodaAsh();
      }
      // Make molten glass
      while (c.getInventoryItemCount(625) > 0 && c.isRunning()) {
        makeMoltenGlass();
      }
      // Make vials
      while (c.getInventoryItemCount(623) > 0 && c.isRunning()) {
        makeVials();
      }
    }
    quit(1);
    return 1000;
  }

  public void harvest() {
    // Drop edible seaweed if inventory is full
    if (c.getInventoryItemCount(1245) > 0 && c.getInventoryItemCount() == 30) {
      dropEdibleSeaweed();
    }
    // Get seaweed until amount matches number of buckets
    if (!evenBucketsAndSeaweed()) {
      getSeaweed();
    } else {
      // Forcefully stop batching once getting enough seaweed
      if (c.isBatching()) {
        c.walkTo(c.currentX(), c.currentY());
        c.sleep(640);
      }
      // Drop any remaining edible seaweed after collecting seaweed
      if (c.getInventoryItemCount(1245) > 0) {
        dropEdibleSeaweed();
      }
    }
  }

  public void getSeaweed() {
    if (!c.isBatching() || !c.isCurrentlyWalking()) {

      // Pick up ground-spawned seaweed if on the northern side of Entrana
      if (c.currentY() < 550 && c.getNearestItemById(622) != null) {
        pickupSeaweed();
      }

      int[] coords = c.getNearestObjectById(1280);
      if (coords != null) {
        c.setStatus("@Cya@Harvesting Seaweed (" + coords[0] + "," + coords[1] + ")");

        // Travel to the other side of the island if target seaweed is over there
        if (coords[1] > 550 && c.currentY() < 550) {
          walkSouth();
        } else if (coords[1] < 550 & c.currentY() > 550) {
          walkNorth();
        }

        // Harvest seaweed if not already batching or walking
        if (!c.isBatching() && !c.isCurrentlyWalking()) {
          c.atObject(coords[0], coords[1]);
          c.sleep(1280);
        }

      } else if ((c.currentX() != 423 && c.currentY() != 559) || !c.isCurrentlyWalking()) {
        // Walk to reset point to check both sides of Entrana for seaweed plants if none are found
        c.setStatus("@Cya@Walking to reset point");
        int[][] checkTile = {{423, 559}};
        walk(checkTile);
      }
    } else {
      // Sleep while batching or walking
      c.sleep(1280);
    }
  }

  public void pickupSeaweed() {
    int[] groundSeaweed = c.getNearestItemById(622);
    if (groundSeaweed != null) {
      c.setStatus("@Cya@Picking Up Seaweed (" + groundSeaweed[0] + "," + groundSeaweed[1] + ")");
      c.pickupItem(groundSeaweed[0], groundSeaweed[1], 622, true, false);
      while (c.isCurrentlyWalking()) {
        c.sleep(640);
      }
      c.sleep(640);
    }
  }

  public void dropEdibleSeaweed() {
    c.walkTo(c.currentX(), c.currentY());
    c.setStatus("@Cya@Dropping Edible Seaweed");
    c.dropItem(c.getInventoryItemSlotIndex(1245), c.getInventoryItemCount(1245));
    c.sleep(1280);
  }

  public void fillBuckets() {
    c.setStatus("@Cya@Filling Buckets");
    c.useItemIdOnObject(429, 566, 21);
    c.sleep(1280);
    while (c.isBatching() || c.isCurrentlyWalking()) {
      c.sleep(1280);
    }
  }

  public void makeSodaAsh() {
    c.setStatus("@Cya@Making Soda Ash");
    c.useItemIdOnObject(426, 560, 622);
    c.sleep(1280);
    while (c.isBatching() || c.isCurrentlyWalking()) {
      c.sleep(640);
    }
  }

  public void makeMoltenGlass() {
    c.setStatus("@Cya@Making Molten Glass");
    c.useItemIdOnObject(419, 559, 625);
    c.sleep(1280);
    while (c.isBatching() || c.isCurrentlyWalking()) {
      c.sleep(640);
    }
  }

  public void makeVials() {
    c.setStatus("@Cya@Making Vials");
    c.useItemOnItemBySlot(c.getInventoryItemSlotIndex(621), c.getInventoryItemSlotIndex(623));
    c.sleep(1280);
    c.optionAnswer(0);
    while (c.isBatching()) {
      c.sleep(640);
    }
  }

  public void walkNorth() {
    // Use the shortcut if the player has the required agility level
    if (c.getBaseStat(c.getStatId("Agility")) >= 55) {
      int[][] walkPath = {{423, 559}, {434, 549}};
      walk(walkPath);
      c.atObject(434, 550);
    } else {
      int[][] walkPath = {{423, 559}, {409, 552}, {431, 539}};
      walk(walkPath);
    }
  }

  public void walkSouth() {
    // Use the shortcut if the player has the required agility level
    if (c.getBaseStat(c.getStatId("Agility")) >= 55) {
      int[][] walkPath = {{434, 549}};
      walk(walkPath);
      c.atObject(434, 550);
      walkPath = new int[][] {{423, 559}};
      walk(walkPath);
    } else {
      int[][] walkPath = {{431, 539}, {409, 552}, {423, 559}};
      walk(walkPath);
    }
  }

  public void walk(int[][] walkPath) {
    // Follows a path along given tile coordinates
    // Pass in an int[][]:
    //      int[][] path = {{Tile1X,Tile1Y},{Tile2X,Tile2Y},{Tile3X,Tile3Y}...}
    //      walk(path);
    // Singles tile work too, but still require it to be an int[][]
    //      int[][] path = {{Tile1X,Tile1Y}}
    //      walk(path);
    for (int i = 0; i < walkPath.length; ++i) {
      c.walkTo(walkPath[i][0], walkPath[i][1]);
      c.sleep(1280);
      while (c.isCurrentlyWalking() && c.isRunning()) {
        c.sleep(640);
      }
    }
  }

  public boolean evenBucketsAndSeaweed() {
    return c.getInventoryItemCount(622) + c.getInventoryItemCount(624) == getBucketCount();
  }

  public int getBucketCount() {
    return c.getInventoryItemCount(21) + c.getInventoryItemCount(625);
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

  public void paintInterrupt() {
    if (c != null) {
      c.drawBoxAlpha(7, 7, 124, 41, 16777215, 64);
      c.drawString("@whi@_________________", 10, 7, 16777215, 1);
      c.drawString("  @gre@VialCrafter @whi@- @cya@Seatta", 10, 21, 16777215, 1);
      c.drawString("@whi@_________________", 10, 24, 16777215, 1);
      c.drawString("@cya@Vials Held", 10, 40, 16777215, 1);
      c.drawString("@whi@_________________", 10, 44, 16777215, 1);
      c.drawString("@whi@|", 68, 40, 16777215, 1);
      c.drawString("@whi@" + c.getInventoryItemCount(465), 74, 40, 16777215, 1);
    }
  }
}
