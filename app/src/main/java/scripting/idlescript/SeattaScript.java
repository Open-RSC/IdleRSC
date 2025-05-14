package scripting.idlescript;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import controller.Controller;
import java.awt.*;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Arrays;
import models.entities.*;

/** Super class for Seatta's scripts */
public class SeattaScript extends IdleScript {
  public static final Controller c = Main.getController();
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.HIDDEN_FROM_SELECTOR},
          "Seatta",
          "Super class for Seatta's scripts.");

  // --------------- CONSTANTS ---------------
  public static final int TICK = 640;

  //    PaintBuilder Colors - Based on https://draculatheme.com/contribute
  public static final int colorGray = 0x44475A;
  public static final int colorDarkGray = 0x282A36;
  public static final int colorWhite = 0xF8F8F2;
  public static final int colorBlue = 0x6272A4;
  public static final int colorCyan = 0x8BE9FD;
  public static final int colorGreen = 0x50FA7B;
  public static final int colorOrange = 0xFFB86C;
  public static final int colorPink = 0xFF79C6;
  public static final int colorPurple = 0xBD93F9;
  public static final int colorRed = 0xFF5555;
  public static final int colorYellow = 0xF1FA8C;

  //    PaintBuilder Defaults and Variables
  public static final int borderColor = colorPurple;
  public static final int bgColor = colorDarkGray;
  public static final int bgTransparency = 255;
  public static final int paintX = 4;
  public static final int paintY = 18;
  public static final int paintW = 182;
  public static String paintStatus = "Starting Script";

  // -------------- COLLECTIONS --------------
  public static final ItemId[] bones = {
    ItemId.DRAGON_BONES, ItemId.BIG_BONES, ItemId.BAT_BONES, ItemId.BONES,
  };

  // ------------ CLEANUP METHODS ------------

  /**
   * This method is run during the script shutdown process. <br>
   * <br>
   * It should be used for resetting any modified static fields so they can be reused correctly in
   * another script.
   */
  @Override
  public void cleanup() {
    // Reset SeattaScript static fields here
    paintStatus = "Starting Script";

    // Override this if needed in child scripts, and reset the static fields for the script in
    // there.
    cleanupScript();
  }

  /**
   * Prints a message if the child script contains non-excluded static fields. This method is
   * intended to be overridden by child scripts to reset modified static field values without
   * overriding the main cleanup method. This is to keep the superclass cleanup intact.
   */
  public void cleanupScript() {
    Class<?> script = this.getClass();

    boolean hasStaticFields =
        Arrays.stream(script.getDeclaredFields())
            .anyMatch(
                f ->
                    Modifier.isStatic(f.getModifiers())
                        && Arrays.stream(excludedClasses)
                            .noneMatch(c -> c.isAssignableFrom(f.getType())));
    if (hasStaticFields)
      System.out.printf(
          "SeattaScript: The following script declares static fields, but does not override cleanupScript. This could cause variables to not be reset upon stopping the script: %n"
              + "   %s%n",
          script.getSimpleName());
  }

  // ---------------- METHODS ----------------

  /**
   * Uses WebWalker to walk to a location's stand-able tile or if null, to the first corner in its
   * bounds.
   *
   * @param loc Location -- Location to walk to
   */
  public static void walkTowards(Location loc) {
    loc.walkTowards();
  }

  /**
   * Uses WebWalker to walk to a set of coordinates.
   *
   * @param x int -- X coordinate to walk to.
   * @param y int -- Y coordinate to walk to.
   */
  public static void walkTowards(int x, int y) {
    Location.walkTowards(x, y);
  }

  /**
   * Uses WebWalker to walk to a set of coordinates.
   *
   * @param point Point -- The coordinate to walk to.
   */
  public static void walkTowards(Point point) {
    Location.walkTowards(point.x, point.y);
  }

  /** Uses WebWalker to walk to the nearest bank. */
  public static void walkTowardsNearestBank() {
    Location.walkTowardsNearestBank();
  }

  /**
   * Checks whether the player is in a Location's boundary rectangle.
   *
   * @param loc Location -- The Location to check
   * @return boolean -- Whether the player is with the Location's boundary
   */
  public static boolean isAtLocation(Location loc) {
    return Location.isAtLocation(loc);
  }

  /**
   * Returns whether the player is at a specific set of coordinates.
   *
   * @param x int -- X coordinate to check
   * @param y int -- Y coordinate to check
   * @return boolean
   */
  public static boolean isAtCoords(int x, int y) {
    return (c.currentX() == x && c.currentY() == y);
  }

  /**
   * Returns whether the player is at a specific coordinate.
   *
   * @param point Point -- Coordinates to check
   * @return boolean
   */
  public static boolean isAtCoords(Point point) {
    return (c.currentX() == point.x && c.currentY() == point.y);
  }

  /**
   * Returns whether a specified SceneryId is at a given coordinate
   *
   * @param scenery SceneryId -- The Object we're looking for
   * @param point Point -- The coordinates we're checking
   * @return boolean -- Whether the SceneryId is at the coordinates
   */
  public static boolean isSceneryAtCoords(SceneryId scenery, Point point) {
    return c.getObjectAtCoord(point.x, point.y) == scenery.getId();
  }

  /**
   * Returns whether a specified SceneryId is at a given coordinate
   *
   * @param scenery SceneryId -- The Object we're looking for
   * @param x int -- The x coordinate we're checking
   * @param y int -- The y coordinate we're checking
   * @return boolean -- Whether the SceneryId is at the coordinates
   */
  public static boolean isSceneryAtCoords(SceneryId scenery, int x, int y) {
    return c.getObjectAtCoord(x, y) == scenery.getId();
  }

  /**
   * Return whether the player has the specified level for a skill.
   *
   * @param skill SkillId -- SkillId to check
   * @param level int -- Level to check for
   * @return boolean
   */
  public static boolean hasSkillLevel(SkillId skill, int level) {
    return c.getBaseStat(skill.getId()) >= level;
  }
  /**
   * Return whether the player has the specified level for a skill.
   *
   * @param skill id -- Id of the skill to check
   * @param level int -- Level to check for
   * @return boolean
   */
  public static boolean hasSkillLevel(int skill, int level) {
    return c.getBaseStat(skill) >= level;
  }

  /**
   * Returns whether the player has completed the specified quest
   *
   * @param quest int -- Quest id to check
   * @return boolean
   */
  public static boolean hasCompletedQuest(int quest) {
    return c.isQuestComplete(quest);
  }

  /**
   * Returns whether the player has completed the specified quest
   *
   * @param quest QuestId -- QuestId to check
   * @return boolean
   */
  public static boolean hasCompletedQuest(QuestId quest) {
    return hasCompletedQuest(quest.getId());
  }

  /**
   * Checks if the player has a usable pickaxe with them.
   *
   * @return boolean
   */
  public static boolean hasUsablePickaxe() {
    final Map<Integer, Integer> pickaxeLevelMap =
        new HashMap<Integer, Integer>() {
          {
            put(ItemId.BRONZE_PICKAXE.getId(), 1);
            put(ItemId.IRON_PICKAXE.getId(), 1);
            put(ItemId.STEEL_PICKAXE.getId(), 6);
            put(ItemId.MITHRIL_PICKAXE.getId(), 21);
            put(ItemId.ADAMANTITE_PICKAXE.getId(), 31);
            put(ItemId.RUNE_PICKAXE.getId(), 41);
          }
        };
    return pickaxeLevelMap.entrySet().stream()
        .anyMatch(
            entry ->
                (hasUnnotedItem(entry.getKey()))
                    && hasSkillLevel(SkillId.MINING, entry.getValue()));
  }
  /**
   * Checks if the player has a usable axe with them.
   *
   * @return boolean
   */
  public static boolean hasUsableAxe() {
    final ItemId[] axes = {
      ItemId.RUNE_AXE,
      ItemId.ADAMANTITE_AXE,
      ItemId.MITHRIL_AXE,
      ItemId.BLACK_AXE,
      ItemId.STEEL_AXE,
      ItemId.IRON_AXE,
      ItemId.BRONZE_AXE,
    };

    return Arrays.stream(axes).anyMatch(SeattaScript::hasUnnotedItem);
  }

  /**
   * Returns whether the player has at least the specified amount of an item in their inventory.<br>
   * <br>
   * If you need to differentiate between unnoted and noted items, use hasUnnotedInventoryAmount()
   * and hasNotedInventoryAmount().
   *
   * @param item itemId -- Item to check for
   * @param amount int -- Amount of itemId to check for
   * @return boolean
   */
  public static boolean hasInventoryAmount(ItemId item, int amount) {
    return hasInventoryAmount(item.getId(), amount);
  }
  /**
   * Returns whether the player has at least the specified amount of an item in their inventory.<br>
   * <br>
   * If you need to differentiate between unnoted and noted items, use hasUnnotedInventoryAmount()
   * and hasNotedInventoryAmount().
   *
   * @param item int -- Item to check for
   * @param amount int -- Amount of itemId to check for
   * @return boolean
   */
  public static boolean hasInventoryAmount(int item, int amount) {
    return c.getInventoryItemCount(item) >= amount;
  }

  /**
   * Returns whether the player has a specified noted amount of an item id in their inventory.
   *
   * @param item ItemId -- Item to check for
   * @param amount int -- Amount to check for
   * @return boolean
   */
  public static boolean hasNotedInventoryAmount(ItemId item, int amount) {
    return hasUnnotedInventoryAmount(item.getId(), amount);
  }

  /**
   * Returns whether the player has a specified noted amount of an item id in their inventory.
   *
   * @param item int -- Item to check for
   * @param amount int -- Amount to check for
   * @return boolean
   */
  public static boolean hasNotedInventoryAmount(int item, int amount) {
    return c.getUnnotedInventoryItemCount(item) >= amount;
  }

  /**
   * Returns whether the player has a specified unnoted amount of an item id in their inventory.
   *
   * @param item int -- Item to check for
   * @param amount int -- Amount to check for
   * @return boolean
   */
  public static boolean hasUnnotedInventoryAmount(ItemId item, int amount) {
    return hasNotedInventoryAmount(item.getId(), amount);
  }

  /**
   * Returns whether the player has a specified unnoted amount of an item id in their inventory.
   *
   * @param item int -- Item to check for
   * @param amount int -- Amount to check for
   * @return boolean
   */
  public static boolean hasUnnotedInventoryAmount(int item, int amount) {
    return c.getNotedInventoryItemCount(item) >= amount;
  }

  /**
   * Returns whether the player has the specified item equipped
   *
   * @param item itemId -- Item to check for
   * @return boolean
   */
  public static boolean hasEquippedItem(ItemId item) {
    return hasEquippedItem(item.getId());
  }

  /**
   * Returns whether the player has the specified item equipped
   *
   * @param item int -- Item to check for
   * @return boolean
   */
  public static boolean hasEquippedItem(int item) {
    return c.isItemIdEquipped(item);
  }

  /**
   * Returns whether the player has the specified item in their inventory or equipped. <br>
   * <br>
   * If you need to differentiate between unnoted and noted, use hasUnnotedItem() or hasNotedItem()
   * instead.
   *
   * @param item ItemId -- ItemId to check for
   * @return boolean
   */
  public static boolean hasItem(ItemId item) {
    return hasItem(item.getId());
  }

  /**
   * Returns whether the player has the specified item in their inventory or equipped. <br>
   * <br>
   * If you need to differentiate between unnoted and noted, use hasUnnotedItem() or hasNotedItem()
   * instead.
   *
   * @param item int -- Item to check for
   * @return boolean
   */
  public static boolean hasItem(int item) {
    return c.isItemIdEquipped(item) || c.getInventoryItemCount(item) > 0;
  }

  /**
   * Returns whether the player has the specified item unnoted in their inventory or equipped.
   *
   * @param item int -- Item id
   * @return boolean -- Whether noted item is in inventory
   */
  public static boolean hasUnnotedItem(int item) {
    return c.isItemIdEquipped(item) || c.getUnnotedInventoryItemCount(item) > 0;
  }

  /**
   * Returns whether the player has the specified item unnoted in their inventory or equipped.
   *
   * @param item ItemId -- ItemId
   * @return boolean -- Whether noted item is in inventory
   */
  public static boolean hasUnnotedItem(ItemId item) {
    return hasUnnotedItem(item.getId());
  }

  /**
   * Returns whether the player has the specified item noted in their inventory.
   *
   * @param item ItemId -- ItemId
   * @return boolean -- Whether noted item is in inventory
   */
  public static boolean hasNotedItem(int item) {
    return c.getNotedInventoryItemCount(item) > 0;
  }

  /**
   * Returns whether the player has the specified item noted in their inventory.
   *
   * @param item int -- Item id
   * @return boolean -- Whether noted item is in inventory
   */
  public static boolean hasNotedItem(ItemId item) {
    return hasNotedItem(item.getId());
  }

  /**
   * Returns whether the player has a specified number of empty inventory spaces.
   *
   * @param amount int -- Number of empty spaces to check for
   * @return boolean
   */
  public static boolean hasEmptyInventorySpaces(int amount) {
    return (30 - c.getInventoryItemCount()) >= amount;
  }

  /**
   * Check if the player has a specified quest completed. Quits the script if the player does not.
   *
   * @param quest QuestId -- QuestId of the quest to check
   */
  public static void checkForQuestCompletionOrQuit(QuestId quest) {
    if (!hasCompletedQuest(quest)) {
      String name = c.getQuestNames()[quest.getId()];
      quit(QuitReason.MISSING_QUEST_REQUIREMENT, new String[] {name});
    }
  }

  /**
   * Check if the player has a specified level for SkillId. Quits the script if the player does not.
   *
   * @param skill SkillId -- SkillId of the skill to check
   * @param level int -- Level to check for
   */
  public static void checkForSkillLevelOrQuit(SkillId skill, int level) {
    if (!hasSkillLevel(skill, level)) {
      String name = skill.name().toLowerCase();
      name = name.substring(0, 1).toUpperCase() + name.substring(1);

      quit(
          QuitReason.MISSING_SKILL_REQUIREMENT,
          new String[] {String.format(" - %s %s", level, name)});
    }
  }

  /**
   * Checks if the player has a usable pickaxe in their inventory. Quits the script if the player
   * does not.
   */
  public static void checkForUsablePickaxeOrQuit() {
    if (!hasUsablePickaxe()) {
      quit(QuitReason.MISSING_INVENTORY_ITEM, new String[] {" -A usable pickaxe"});
    }
  }

  /**
   * Checks if the player has a usable axe in their inventory. Quits the script if the player does
   * not.
   */
  public static void checkForUsableAxeOrQuit() {
    if (!hasUsableAxe()) {
      quit(QuitReason.MISSING_INVENTORY_ITEM, new String[] {" -A usable axe"});
    }
  }

  /**
   * Check if the player has a specified amount of an item in their inventory. Quits the script if
   * the player does not.<br>
   * <br>
   * If you need to differentiate between noted and unnoted items, use
   * checkForUnnotedInventoryAmountOrQuit() or checkForNotedInventoryAmountOrQuit instead.
   *
   * @param item ItemId -- ItemId to check for
   * @param amount int -- Amount of item to check for
   */
  public static void checkForInventoryAmountOrQuit(ItemId item, int amount) {
    if (!hasInventoryAmount(item, amount)) {
      String name = c.getItemName(item.getId()).toLowerCase();
      name = name.substring(0, 1).toUpperCase() + name.substring(1);

      quit(
          QuitReason.MISSING_INVENTORY_ITEM,
          new String[] {String.format(" - %s %s", amount, name)});
    }
  }

  /**
   * Check if the player has a specified unnoted amount of an item in their inventory. Quits the
   * script if the player does not.
   *
   * @param item ItemId -- ItemId to check for
   * @param amount int -- Amount of item to check for
   */
  public static void checkForUnnotedInventoryAmountOrQuit(ItemId item, int amount) {
    if (!hasUnnotedInventoryAmount(item, amount)) {
      String name = c.getItemName(item.getId()).toLowerCase();
      name = name.substring(0, 1).toUpperCase() + name.substring(1);

      quit(
          QuitReason.MISSING_NOTED_INVENTORY_ITEM,
          new String[] {String.format(" - %s %s", amount, name)});
    }
  }

  /**
   * Check if the player has a specified noted amount of an item in their inventory. Quits the
   * script if the player does not.
   *
   * @param item ItemId -- ItemId to check for
   * @param amount int -- Amount of item to check for
   */
  public static void checkForNotedInventoryAmountOrQuit(ItemId item, int amount) {
    if (!hasNotedInventoryAmount(item, amount)) {
      String name = c.getItemName(item.getId()).toLowerCase();
      name = name.substring(0, 1).toUpperCase() + name.substring(1);

      quit(
          QuitReason.MISSING_UNNOTED_INVENTORY_ITEM,
          new String[] {String.format(" - %s %s", amount, name)});
    }
  }

  /**
   * Check if the player has a specified item equipped. Quits the script if the player does not.
   *
   * @param item ItemId -- ItemId to check for
   */
  public static void checkForEquippedItemOrQuit(ItemId item) {
    if (!c.isItemIdEquipped(item.getId())) {
      String name = c.getItemName(item.getId()).toLowerCase();
      name = name.substring(0, 1).toUpperCase() + name.substring(1);

      quit(QuitReason.MISSING_EQUIPPED_ITEM, new String[] {String.format(" - %s", name)});
    }
  }

  /**
   * Check if the player has a specified item either equipped or in their inventory. Quits the
   * script if the player does not.
   *
   * @param item ItemId -- ItemId to check for
   */
  public static void checkForEquippedOrUnnotedInventoryItemOrQuit(ItemId item) {
    if (!hasUnnotedItem(item)) {
      String name = c.getItemName(item.getId()).toLowerCase();
      name = name.substring(0, 1).toUpperCase() + name.substring(1);

      quit(
          QuitReason.MISSING_EQUIPPED_OR_INVENTORY_ITEM,
          new String[] {String.format(" - %s", name)});
    }
  }

  /**
   * Check if the player has a specified number of empty inventory spaces. Quits the script if the
   * player does not.
   *
   * @param amount int -- Number of empty spaces to check for
   */
  public static void checkForEmptyInventorySpacesOrQuit(int amount) {
    if (!hasEmptyInventorySpaces(amount)) {
      quit(
          QuitReason.MISSING_INVENTORY_ITEM,
          new String[] {String.format(" - You need at least %s empty inventory spaces", amount)});
    }
  }

  /**
   * Returns whether the player is an Ultimate Iron Man.
   *
   * @return boolean
   */
  public static boolean isUIM() {
    return c.getPlayerMode() == 2;
  }

  /**
   * Returns whether the player is a Hardcore Iron Man.
   *
   * @return boolean
   */
  public static boolean isHCIM() {
    return c.getPlayerMode() == 3;
  }

  /**
   * Returns whether the script is running and the player is logged in.
   *
   * @return boolean
   */
  public static boolean isRunningAndLoggedIn() {
    return c.isRunning() && c.isLoggedIn();
  }

  /**
   * Returns whether the script is running. If true, it also handles sleep and idle movement. Used
   * for the main script loop.
   *
   * @return boolean
   */
  public static boolean isScriptRunning() {
    if (!c.isRunning()) return false;
    while (c.isRunning() && !c.isLoggedIn()) {
      c.login();
      c.sleep(10000);
    }
    handleSleepAndIdleMovement();
    return true;
  }

  /** Sleeps if the player needs to sleep. Moves if the player has been idle for too long. */
  public static void handleSleepAndIdleMovement() {
    if (c.getNeedToMove()) c.moveCharacter();
    if (c.getShouldSleep()) c.sleepHandler(true);
  }

  /**
   * Sleep for a specified number of game ticks. (640ms each)
   *
   * @param amountOfTicks int -- The number of ticks to sleep for.
   */
  public static void sleepTicks(int amountOfTicks) {
    c.sleep(amountOfTicks * TICK);
  }

  /** Sleeps while the player is doing a batched action. */
  public static void sleepWhileBatching() {
    do sleepTicks(1);
    while (c.isBatching() && isRunningAndLoggedIn());
  }

  /**
   * Quits the script.
   *
   * @return int -- Returned int so scripts can call this for their run method return
   */
  public static int quit() {
    return quit(QuitReason.SCRIPT_STOPPED);
  }

  /**
   * Quits the script and prints out a message.
   *
   * @param message String -- Message to print out
   * @return int -- Returned int so scripts can call this for their run method return
   */
  public static int quit(String message) {
    return quit(QuitReason.SCRIPT_STOPPED, new String[] {message});
  }

  /**
   * Quits the script and prints the message corresponding to the QuitReason.
   *
   * @param reason QuitReason -- Reason printed when quitting.
   * @return int -- Returned int so scripts can call this for their run method return
   */
  public static int quit(QuitReason reason) {
    return quit(reason, null);
  }

  /**
   * Quits the script while logging the reason and all Strings from messageArray
   *
   * @param reason QuitReason -- Reason printed when quitting.
   * @param messageArray String[] -- Array of message strings to print after the QuitReason.
   * @return int -- Returned int so scripts can call this for their run method return
   */
  public static int quit(QuitReason reason, String[] messageArray) {
    if (c.isRunning()) {
      if (!reason.equals(QuitReason.SCRIPT_STOPPED)) c.log(reason.getMessage(), "red");
      if (messageArray != null && messageArray.length > 0) {
        Arrays.stream(messageArray).forEach(m -> c.log(m, "red"));
      }
      c.stop();
    }
    return 1000;
  }

  /**
   * An enum of reasons to use in for displaying messages when stopping a script with the quit
   * methods
   */
  public enum QuitReason {
    SCRIPT_STOPPED(null),
    MISSING_EQUIPPED_ITEM("You are missing a required equipped item:"),
    MISSING_EQUIPPED_OR_INVENTORY_ITEM("You are missing a required item:"),
    MISSING_INVENTORY_ITEM("You are missing a required inventory item:"),
    MISSING_UNNOTED_INVENTORY_ITEM("You are missing a required unnoted inventory item:"),
    MISSING_NOTED_INVENTORY_ITEM("You are missing a required noted inventory item:"),
    MISSING_SKILL_REQUIREMENT("You are missing a required skill level:"),
    MISSING_QUEST_REQUIREMENT("You are missing a required quest:"),
    UNABLE_TO_FIND_NPC("The following npc was not able to be found:"),
    NOT_ENOUGH_EMPTY_INVENTORY_SPACES("You do not have enough empty inventory spaces.");

    private final String reason;

    QuitReason(String reason) {
      this.reason = reason;
    }

    public String getMessage() {
      return reason;
    }
  }
}
