package scripting.idlescript.AIOQuester;

import static scripting.idlescript.AIOQuester.AIOQuester.TESTING_QUESTS;
import static scripting.idlescript.AIOQuester.AIOQuester.TESTING_STAGE;

import bot.Main;
import controller.BotController;
import controller.Controller;
import controller.PaintBuilder.PaintBuilder;
import controller.PaintBuilder.RowBuilder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import models.entities.ItemId;
import models.entities.Location;
import models.entities.MapPoint;
import models.entities.SkillId;
import orsc.ORSCharacter;
import scripting.idlescript.AIOQuester.models.QuestDef;
import scripting.idlescript.AIOQuester.models.QuitReason;

// TODO: Add a method for atObjectUntilItemAmount(int[][] objectCoords, int itemId, int amount)
// TODO: Add a method for atObjectUntilItemAmount(int objectX, int objectY, int itemId, int amount)
// TODO: Add support for eating and handling combat in quest scripts

/* USE THESE FOR HELP WITH QUEST STAGES/DIALOGUES/ETC...
Authentic Quests: https://gitlab.com/open-runescape-classic/core/-/tree/develop/server/plugins/com/openrsc/server/plugins/authentic/quests
Custom Quests: https://gitlab.com/open-runescape-classic/core/-/tree/develop/server/plugins/com/openrsc/server/plugins/custom/quests
*/

/**
 * The superclass that AIOQuester quest scripts extend. Contains useful methods for quest script
 * writing. Quest scripts MUST extend this class, or they will not be found by AIOQuester!
 */
public abstract class QuestHandler {
  protected static final Controller c = Main.getController();
  protected static final BotController bc = new BotController(c);
  public static PaintBuilder paintBuilder;
  public static RowBuilder rowBuilder;
  public static QuestDef quest;
  public static Map<Integer, Integer> bankItems = null;

  protected static boolean timeToDrinkAntidote = false;
  protected static boolean hasWalkedToStart = false;

  protected static String CURRENT_QUEST_STEP;
  protected static int QUEST_STAGE;
  protected static int[][] STEP_ITEMS;
  protected static Location START_LOCATION;

  /**
   * Checks for and runs the given class' run method.
   *
   * @param cl Class - Class of the quest script that will be run.
   */
  static void start(Class<? extends QuestHandler> cl) {
    paintBuilder.start(4, 18, 182);
    CURRENT_QUEST_STEP = "Starting " + quest.getName();
    START_LOCATION = quest.getStartLocation();
    hasWalkedToStart = false;
    if (!quest.isMiniquest()) QUEST_STAGE = c.getQuestStage(quest.getId());

    if (c.isRunning()) {
      c.setBatchBars(true);
      try {
        Method run = cl.getDeclaredMethod("run");
        if (!run.isAccessible()) run.setAccessible(true);
        run.invoke(null);
        quit(QuitReason.SCRIPT_STOPPED);
      } catch (NoSuchMethodException e) {
        System.out.println("\nError: 'run' method not available for the class: " + cl.getName());
      } catch (NullPointerException e) {
        System.out.println(
            "\nError: 'run' method may not be static for the class: " + cl.getName());
      } catch (IllegalAccessException | InvocationTargetException e) {
        System.out.println();
        //noinspection CallToPrintStackTrace
        e.printStackTrace();
      }
    }
  }

  /** Used for testing only */
  private static void doTestLoop() {
    // DO TEST STUFF HERE!!
  }

  /** Used for testing only */
  public static void startTestingLoop() {
    QuestHandler.CURRENT_QUEST_STEP = "This is only ran for testing";
    paintBuilder.start(4, 18, 182);
    int loops = 0;

    while (c.isRunning()) {
      c.displayMessage("@yel@Starting testing loop: " + ++loops);
      doTestLoop();
      c.displayMessage("@yel@Loops completed: " + loops);
      c.sleep(640);
    }
  }

  /** Uses WebWalker to walk to the start location. */
  public static void walkToStartLocation() {
    if (!isAtLocation(START_LOCATION)) {
      CURRENT_QUEST_STEP = "Walking to start location";
      walkTowards(START_LOCATION);
      hasWalkedToStart = true;
    }
  }

  /**
   * Uses WebWalker to walk to a location's standable tile or if null, to the first corner in its
   * bounds.
   *
   * @param location - Location to walk to
   */
  public static void walkTowards(Location location) {
    walkTowards(location.getX(), location.getY());
  }

  /**
   * Uses WebWalker to walk to a set of coordinates.
   *
   * @param x - X coordinate to walk to
   * @param y - y coordinate to walk to
   */
  public static void walkTowards(int x, int y) {
    if (c.isRunning()) {
      if (!isAtCoords(x, y)) {
        c.displayMessage(
            "@yel@Attempting to walk to: @cya@" + Location.getDescriptionFromStandableTile(x, y));
        System.out.println(
            "\nAttempting to walk to: " + Location.getDescriptionFromStandableTile(x, y));
        System.out.println(
            "If this fails, WebWalker might need to be updated to include the area.");
        int failedAttempts = 0;
        while (!isAtCoords(x, y) && c.isRunning()) {
          failedAttempts = !c.walkTowards(x, y) ? ++failedAttempts : 0;
          if (failedAttempts >= 5) quit(QuitReason.UNABLE_TO_WALK_TO_LOCATION);
          c.sleep(100);
        }
      }
    }
  }

  /**
   * Checks if the player has at least a certain amount of an item id.
   *
   * @param id int -- The item id to check
   * @param amount int -- The amount to check for
   * @return boolean
   */
  public static boolean hasAtLeastItemAmount(int id, int amount) {
    return (c.getInventoryItemCount(id) >= amount);
  }

  /**
   * Checks if the player has at least a certain amount of an ItemId.
   *
   * @param item ItemId -- The ItemId to check
   * @param amount int -- The amount to check for
   * @return boolean
   */
  public static boolean hasAtLeastItemAmount(ItemId item, int amount) {
    return (c.getInventoryItemCount(item.getId()) >= amount);
  }

  /**
   * Checks if the player has the required level of a specified skill.
   *
   * @param skill SkillId -- SkillId enum value
   * @param requiredLevel int -- Level required for the skill
   * @return boolean -- Does the player have the specified skill level
   */
  public static boolean hasSkillLevel(SkillId skill, int requiredLevel) {
    return c.getBaseStat(skill.getId()) >= requiredLevel;
  }

  /**
   * Checks if the player is within the boundary of a Location
   *
   * @param location Location to check
   * @return boolean
   */
  public static boolean isAtLocation(Location location) {
    return Location.isAtLocation(location);
  }

  /**
   * Returns whether the player is at a specific coordinate.
   *
   * @param x int - X coordinate to check
   * @param y int - Y coordinate to check
   * @return boolean
   */
  public static boolean isAtCoords(int x, int y) {
    return c.currentX() == x && c.currentY() == y;
  }

  /**
   * Returns whether the controller is still running; while handling sleep, afk anti-logout
   * movement, setting QUEST_STAGE, and doing the initial walk to START_LOCATION. Use this for
   * looping the stage switch in scripts.
   *
   * @return boolean
   */
  public static boolean isQuesting() {
    if (!c.isRunning()) return false;
    if (c.getNeedToMove()) c.moveCharacter();
    if (c.getShouldSleep()) c.sleepHandler(true);
    if (!hasWalkedToStart) walkToStartLocation();
    if (!quest.isMiniquest()) {
      QUEST_STAGE = getQuestStage();
      if (QUEST_STAGE == -1) {
        quit(QuitReason.QUEST_COMPLETED);
        return false;
      }
    }

    return true;
  }

  /**
   * Drops all of a given item id except one.
   *
   * @param itemId int -- Id of the item to drop
   * @param amountToKeep int -- Amount of item to keep
   */
  public static void dropAllButAmount(int itemId, int amountToKeep) {
    while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
    c.sleep(1280);
    if (c.getInventoryItemCount(itemId) > amountToKeep && c.isRunning()) {
      c.dropItem(
          c.getInventoryItemSlotIndex(itemId), c.getInventoryItemCount(itemId) - amountToKeep);
      c.sleep(1280);
      while (c.getInventoryItemCount(itemId) > amountToKeep && c.isRunning()) {
        c.sleep(640);
      }
    }
  }

  /**
   * Opens the shop for the closest NPC with a given id and sells items to them.
   *
   * @param npcIds int[] -- Ids of npcs to look for
   * @param sellItems int[][] -- ItemId/amount pairs. Example: {{SILK_ID, 1}, {EGG_ID, 12}}
   */
  public static void openShopThenSell(int[] npcIds, int[][] sellItems) {
    if (c.isRunning()) {
      c.npcCommand1(c.getNearestNpcByIds(npcIds, false).serverIndex);
      c.sleep(1280);
      while (!c.isInShop() && c.isRunning()) c.sleep(640);

      for (int[] item : sellItems) {
        int itemId = item[0];
        int sellAmount = item[1];
        int startAmount = c.getInventoryItemCount(itemId);

        String itemName = String.valueOf(ItemId.getById(itemId)).replaceAll("_", " ").toLowerCase();
        itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);

        if (startAmount < sellAmount) {
          c.log(String.format("Only had %sx %s to sell", startAmount, itemName));
        }
        c.shopSell(itemId, sellAmount);
        c.sleep(1280);
      }
      c.closeShop();
      while (c.isInShop() && c.isRunning()) c.sleep(640);
    }
  }

  /**
   * Opens the shop for the closest NPC with a given id and buys items from them.
   *
   * @param npcIds int[] -- Ids of npcs to look for
   * @param buyItems int[][] -- ItemId/amount pairs. Example: {{SILK_ID, 1}, {EGG_ID, 12}}
   */
  public static void openShopThenBuy(int[] npcIds, int[][] buyItems) {
    if (c.isRunning()) {
      c.npcCommand1(c.getNearestNpcByIds(npcIds, false).serverIndex);
      c.sleep(1280);
      while (!c.isInShop() && c.isRunning()) c.sleep(640);

      for (int[] item : buyItems) {
        int itemId = item[0];
        int buyAmount = item[1];
        int startAmount = c.getInventoryItemCount(itemId);
        int emptySpaces = 30 - c.getInventoryItemCount();
        int price = c.getShopItemPrice(itemId);
        int heldGold = c.getInventoryItemCount(ItemId.COINS.getId());

        String itemName = String.valueOf(ItemId.getById(itemId)).replaceAll("_", " ").toLowerCase();
        itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);

        // Skip this loop if the player doesn't have enough gold
        if (heldGold < (price * buyAmount)) {
          c.log(String.format("Not enough coins to buy %sx %s", buyAmount, itemName));
          continue;
        }
        // Skip this loop if the player doesn't have enough empty inventory spaces
        if (emptySpaces == 0) {
          if (c.isItemStackable(itemId) && c.getInventoryItemCount(itemId) == 0
              || !c.isItemStackable(itemId)) {
            c.log(String.format("Not enough space for %sx %s", buyAmount, itemName));
            continue;
          }
        }
        while (c.getInventoryItemCount(itemId) < startAmount + buyAmount && c.isRunning()) {
          while (c.getShopItemCount(itemId) < 1 && c.isRunning()) c.sleep(640);
          c.shopBuy(itemId, buyAmount - (c.getInventoryItemCount(itemId) - startAmount));
          c.sleep(1280);
        }
      }
      c.closeShop();
      while (c.isInShop() && c.isRunning()) c.sleep(640);
    }
  }

  /**
   * Opens the shop for the closest NPC with a given id and sells then buys items.
   *
   * @param npcIds int[] -- Ids of npcs to look for
   * @param sellItems int[][] -- ItemId/amount pairs. Example: {{SILK_ID, 1}, {EGG_ID, 12}}
   * @param buyItems int[][] -- ItemId/amount pairs. Example: {{SILK_ID, 1}, {EGG_ID, 12}}
   */
  public static void openShopThenSellAndBuy(int[] npcIds, int[][] sellItems, int[][] buyItems) {
    if (c.isRunning()) {
      c.npcCommand1(c.getNearestNpcByIds(npcIds, false).serverIndex);
      c.sleep(1280);
      while (!c.isInShop() && c.isRunning()) c.sleep(640);

      // Sell Items
      for (int[] item : sellItems) {
        int itemId = item[0];
        int sellAmount = item[1];
        int startAmount = c.getInventoryItemCount(itemId);

        String itemName = String.valueOf(ItemId.getById(itemId)).replaceAll("_", " ").toLowerCase();
        itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);

        if (startAmount < sellAmount) {
          c.log(String.format("Only had %sx %s to sell", startAmount, itemName));
        }
        c.shopSell(itemId, sellAmount);
        c.sleep(1280);
      }

      // Buy items
      for (int[] item : buyItems) {
        int itemId = item[0];
        int buyAmount = item[1];
        int startAmount = c.getInventoryItemCount(itemId);
        int emptySpaces = 30 - c.getInventoryItemCount();
        int price = c.getShopItemPrice(itemId);
        int heldGold = c.getInventoryItemCount(ItemId.COINS.getId());

        String itemName = String.valueOf(ItemId.getById(itemId)).replaceAll("_", " ").toLowerCase();
        itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);

        // Skip this loop if the player doesn't have enough gold
        if (heldGold < (price * buyAmount)) {
          c.log(String.format("Not enough coins to buy %sx %s", buyAmount, itemName));
          continue;
        }
        // Skip this loop if the player doesn't have enough empty inventory spaces
        if (emptySpaces == 0) {
          if (c.isItemStackable(itemId) && c.getInventoryItemCount(itemId) == 0
              || !c.isItemStackable(itemId)) {
            c.log(String.format("Not enough space for %sx %s", buyAmount, itemName));
            continue;
          }
        }
        while (c.getInventoryItemCount(itemId) < startAmount + buyAmount && c.isRunning()) {
          while (c.getShopItemCount(itemId) < 1 && c.isRunning()) c.sleep(640);
          c.shopBuy(itemId, buyAmount - (c.getInventoryItemCount(itemId) - startAmount));
          c.sleep(1280);
        }
      }
      c.closeShop();
      while (c.isInShop() && c.isRunning()) c.sleep(640);
    }
  }

  /**
   * Walks to specified tile and picks up an item id from an unreachable tile. Keeps retrying ever
   * two ticks until the specified amount is picked up.
   *
   * @param itemId int -- Id of the item to pick up
   * @param standTile int[] -- Tile to stand at to pick up item
   * @param amount int -- The amount to pick up
   */
  public static void pickupUnreachableItem(int itemId, int[] standTile, int amount) {
    int newId = changingIdCheck(itemId);
    if (c.getInventoryItemCount() == 30) {
      if (!c.isItemStackable(itemId)
          || ((c.isItemStackable(itemId) && c.getInventoryItemCount(itemId) < 1))) {
        quit(QuitReason.INVENTORY_FULL);
      }
    } else {
      int itemStartCount = c.getInventoryItemCount(newId);
      while (c.getInventoryItemCount(newId) < itemStartCount + amount && c.isRunning()) {
        int[] item = c.getNearestItemById(itemId);
        if (item != null) {
          if (distanceFromTile(standTile) > 1) {
            int[][] path = {standTile};
            walkPath(path);
          }
          c.pickupItem(item[0], item[1], itemId, false, true);
        }
        c.sleep(1280);
      }
    }
  }

  /**
   * Picks up the nearest item matching the given item id. Keeps retrying every two ticks until the
   * specified amount is picked up.
   *
   * @param itemId int -- Id of the item to pick up
   * @param amount int -- The amount to pick up
   */
  public static void pickupGroundItem(int itemId, int amount) {
    int newId = changingIdCheck(itemId);
    if (c.getInventoryItemCount() == 30) {
      if (!c.isItemStackable(itemId)
          || ((c.isItemStackable(itemId) && c.getInventoryItemCount(itemId) < 1))) {
        quit(QuitReason.INVENTORY_FULL);
      }
    } else {
      int itemStartCount = c.getInventoryItemCount(newId);
      while (c.getInventoryItemCount(newId) < itemStartCount + amount && c.isRunning()) {
        int[] item = c.getNearestItemById(itemId);
        if (item != null) {
          if (distanceFromTile(item) > 1) {
            int[][] path = {item};
            walkPath(path);
          }
          c.pickupItem(item[0], item[1], itemId, true, true);
        }
        c.sleep(1280);
      }
    }
  }

  /**
   * Checks if the given itemId matches any conditions to change it and if not return the original
   *
   * @param itemId Int -- Id of item to check
   * @return Int -- Item id after being checked
   */
  private static int changingIdCheck(int itemId) {
    System.out.println(
        "If you get stuck in the while loop here you may have to add a line similar to the line below to changingIdCheck().");
    System.out.println(
        "This happens because the item you are trying to pick up changes ids when picked up.");
    System.out.println(
        "\"if (newId == ItemId.ITEM_NAME.getId()) newId = ItemId.NEW_ITEM_NAME.getId();\"");
    // If statements to switch ids for items that change to another id on pickup
    if (itemId == ItemId.FLOUR.getId()) return ItemId.POT_OF_FLOUR.getId();

    return itemId;
  }

  /**
   * Uses an item id on the nearest npc with matching npc id.
   *
   * @param npcId int -- Id of npc, not server index
   * @param itemId int -- Id of the item to use on the npc
   */
  public static void useItemOnNearestNpcId(int npcId, int itemId) {
    if (c.isRunning()) {
      int[] npc_id = {npcId};
      final ORSCharacter npc = c.getNearestNpcByIds(npc_id, false);
      if (npc != null) {
        c.useItemOnNpc(npc.serverIndex, itemId);
        c.sleep(640);
        while (c.isCurrentlyWalking()) {
          c.sleep(640);
        }
      } else {
        c.log(String.format("Could not find NPC: %s", npcId));
        quit(QuitReason.NPC_NOT_FOUND);
      }
    }
  }

  /**
   * Talks to the nearest npc with the given id.
   *
   * @param npcId int -- Id of the npc to talk to
   */
  public static void talkToNpcId(int npcId) {
    if (c.isRunning()) {
      int[] npc_id = {npcId};
      ORSCharacter npc = c.getNearestNpcByIds(npc_id, false);
      if (npc != null) {
        c.talkToNpc(npc.serverIndex);
        c.sleep(640);
        while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
        while (c.isRunning()) {
          String oldMessage = npc.message;
          c.sleep(2560);
          if (oldMessage.equals(npc.message)) break;
        }
      } else {
        c.log(String.format("Could not find NPC: %s", npcId));
        quit(QuitReason.NPC_NOT_FOUND);
      }
    }
    c.sleep(2560);
  }

  /**
   * Goes through a dialog with the nearest instance of a npc id with dialogChoices[] being the
   * responses from the character.
   *
   * <p>This method is for pop up quest dialog after interacting with an object
   *
   * @param npcId int -- Id of the npc to talk to
   * @param dialogChoices String[] -- Responses to dialogs
   */
  public static void followNPCDialogPopUps(int npcId, String[] dialogChoices) {
    int[] npc_id = {npcId};
    ORSCharacter npc = c.getNearestNpcByIds(npc_id, false);
    if (npc != null) {
      while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
      c.sleep(6000);
      for (String dialogChoice : dialogChoices) {
        // Sleep until a dialog menu appears
        while (!c.isInOptionMenu() && c.isRunning()) c.sleep(640);
        // Select the menu option that corresponds with choiceIndex
        for (int option = 0; option < c.getOptionMenuCount(); option++) {
          if (c.getOptionsMenuText(option).equals(dialogChoice)) {
            c.optionAnswer(option);
            c.sleep(640);
            break;
          }
        }
      }
      // After dialogChoices has been depleted sleep until npc hasn't said a new dialog for 4 ticks
      while (c.isRunning()) {
        String oldMessage = npc.message;
        c.sleep(2560);
        if (oldMessage.equals(npc.message)) break;
      }
    } else {
      c.log(String.format("Could not find NPC: %s", npcId));
      quit(QuitReason.NPC_NOT_FOUND);
    }
    c.sleep(2560);
  }

  /**
   * Goes through a dialog with the nearest instance of a npc id with dialogChoices[] being the
   * responses from the character.
   *
   * @param npcId int -- Id of the npc to talk to
   * @param dialogChoices String[] -- Responses to dialogs
   */
  public static void followNPCDialog(int npcId, String[] dialogChoices) {
    int[] npc_id = {npcId};
    ORSCharacter npc = c.getNearestNpcByIds(npc_id, false);
    if (npc != null) {
      c.talkToNpc(npc.serverIndex);
      c.sleep(640);
      while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
      for (String dialogChoice : dialogChoices) {
        // Sleep until a dialog menu appears
        while (!c.isInOptionMenu() && c.isRunning()) c.sleep(640);
        // Select the menu option that corresponds with choiceIndex
        for (int option = 0; option < c.getOptionMenuCount(); option++) {
          if (c.getOptionsMenuText(option).equals(dialogChoice)) {
            c.optionAnswer(option);
            c.sleep(640);
            break;
          }
        }
      }
      // After dialogChoices has been depleted sleep until npc hasn't said a new dialog for 4 ticks
      while (c.isRunning()) {
        String oldMessage = npc.message;
        c.sleep(2560);
        if (oldMessage.equals(npc.message)) break;
      }
    } else {
      c.log(String.format("Could not find NPC: %s", npcId));
      quit(QuitReason.NPC_NOT_FOUND);
    }
    c.sleep(2560);
  }

  public static void sleepUntilQuestStageChanges() {
    int oldStage = QUEST_STAGE;
    while (c.getQuestStage(quest.getId()) == oldStage && c.isRunning()) c.sleep(640);
  }

  /**
   * Ideally, this will eventually be removed in place of walkToward(). Uses PathWalkerApi to walk
   * to a tile.
   *
   * @param mapPoint MapPoint -- Coordinates of the tile to walk to
   */
  public static void pathWalker(MapPoint mapPoint) {
    while (distanceFromTile(mapPoint) > 0 && c.isRunning()) {
      c.openNearbyDoor(1);
      bc.pathWalkerApi.walkTo(mapPoint);
    }
  }

  /**
   * Ideally, this will eventually be removed in place of walkToward(). Uses PathWalkerApi to walk
   * to a tile.
   *
   * @param coords int[] -- Coordinates of the tile to walk to
   */
  public static void pathWalker(int[] coords) {
    pathWalker(new MapPoint(coords[0], coords[1]));
  }

  /**
   * Ideally, this will eventually be removed in place of walkToward(). Uses PathWalkerApi to walk
   * to a tile.
   *
   * @param x int -- X coordinate of the tile to walk to
   * @param y int -- Y coordinate of the tile to walk to
   */
  public static void pathWalker(int x, int y) {
    pathWalker(new MapPoint(x, y));
  }

  /**
   * Ideally, this will eventually be removed in place of walkToward(). Follows a given path while
   * checking for and opening closed doors next to given tiles.
   *
   * @param path - Int[][] -- Each index is a different tile. Either {{X, Y}, {X2, Y2}} or
   *     {TILE1[],TILE2[]}
   */
  public static void walkPath(int[][] path) {
    for (int[] tile : path) {
      while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
      if (c.isRunning()) {
        c.sleep(640);
        while (c.isNearbyDoorClosed(1) && c.isRunning()) {
          c.openNearbyDoor(1);
          c.sleep(640);
        }
        if (!c.isReachable(tile[0], tile[1], false)) {
          c.log("Tile unreachable: " + tile[0] + "," + tile[1]);
          quit(QuitReason.PATH_TILE_NOT_REACHABLE);
        } else {
          c.walkTo(tile[0], tile[1]);
          c.sleep(640);
        }
      }
      while (c.isCurrentlyWalking() && c.isRunning()) {
        c.sleep(640);
      }
    }
  }

  /**
   * Ideally, this will eventually be removed in place of walkToward(). Follows the reverse of a
   * given path while checking for and opening closed doors next to given tiles.
   *
   * @param path - Int[][] -- Each index is a different tile. Either {{X, Y}, {X2, Y2}} or {TILE1[],
   *     TILE2[]}
   */
  public static void walkPathReverse(int[][] path) {
    for (int i = path.length - 1; i >= 0; i--) {
      while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
      if (c.isRunning()) {
        c.sleep(640);
        while (c.isNearbyDoorClosed(1) && c.isRunning()) {
          c.openNearbyDoor(1);
          c.sleep(640);
        }
        if (!c.isReachable(path[i][0], path[i][1], false)) {
          c.log("Tile unreachable: " + path[i][0] + "," + path[i][1]);
          quit(QuitReason.PATH_TILE_NOT_REACHABLE);
        } else {
          c.walkTo(path[i][0], path[i][1]);
          c.sleep(640);
        }
      }
      while (c.isCurrentlyWalking() && c.isRunning()) {
        c.sleep(640);
      }
    }
  }

  /**
   * Returns the distance the player is from the specified tile
   *
   * @param coords int[] -- Coordinates to specified tile
   * @return boolean -- Returns the distance from the tile
   */
  public static int distanceFromTile(int[] coords) {
    return c.distance(c.currentX(), c.currentY(), coords[0], coords[1]);
  }

  /**
   * Returns the distance the player is from the specified Map Point
   *
   * @param mapPoint MapPoint -- MapPoint to check
   * @return int -- Returns the distance from the MapPoint
   */
  public static int distanceFromTile(MapPoint mapPoint) {
    MapPoint current = new MapPoint(c.currentX(), c.currentY());
    return MapPoint.distance(current, mapPoint);
  }

  /**
   * Does c.atObject command and sleeps until you are moved to the destination, Climbs a ladder or
   * stairs and sleeps until you are moved to the destination Also Supports going through fixed
   * (does not stay open) doors
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param coords int[] -- Coordinates of an object
   */
  public static void atObject(int[] coords) {
    c.log("Entered Object");
    while (c.isRunning() && c.isCurrentlyWalking()) c.sleep(640);
    int startX = c.currentX();
    int startY = c.currentY();
    c.log(String.format("Attempting to interact with object at (%s,%s)", coords[0], coords[1]));
    while (c.currentX() == startX & c.currentY() == startY && c.isRunning()) {
      c.atObject(coords[0], coords[1]);
      c.sleep(1280);
    }
    c.log("Successfully entered Object");
  }

  /**
   * Does c.atObject2 command and sleeps until you are moved to the destination, Climbs a ladder or
   * stairs and sleeps until you are moved to the destination Also Supports going through a fixed
   * (does not stay open) doors
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param coords int[] -- Coordinates of an object
   */
  public static void atObject2(int[] coords) {
    c.log("Entered Object2");
    while (c.isRunning() && c.isCurrentlyWalking()) c.sleep(640);
    int startX = c.currentX();
    int startY = c.currentY();
    c.log(String.format("Attempting to interact with object2 at (%s,%s)", coords[0], coords[1]));
    while (c.currentX() == startX & c.currentY() == startY && c.isRunning()) {
      c.atObject2(coords[0], coords[1]);
      c.sleep(1280);
    }
    c.log("Successfully entered Object2");
  }

  /**
   * Does c.atObject command and sleeps until you are moved to the destination. Climbs a ladder or
   * stairs and sleeps until you are moved to the destination. Also Supports going through a fixed
   * (does not stay open) doors
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param x int x coordinate of the object
   * @param y int y coordinate of the object
   */
  public static void atObject(int x, int y) {
    atObject(new int[] {x, y});
  }

  /**
   * Does c.atObject2 command and sleeps until you are moved to the destination, Climbs a ladder or
   * stairs and sleeps until you are moved to the destination Also Supports going through fixed
   * (does not stay open) doors
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param x int x coordinate of the object
   * @param y int y coordinate of the object
   */
  public static void atObject2(int x, int y) {
    atObject2(new int[] {x, y});
  }

  /**
   * Opens a fixed (does not stay open) wall object (c.atWallObject()) and sleeps until you are
   * moved to the destination
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param coords int[] -- Coordinates of a wall object
   */
  public static void atWallObject(int[] coords) {
    c.log("Entered Wall Object");
    while (c.isRunning() && c.isCurrentlyWalking()) c.sleep(640);
    int startX = c.currentX();
    int startY = c.currentY();
    c.log(
        String.format("Attempting to interact with wall object at (%s,%s)", coords[0], coords[1]));
    while (c.currentX() == startX & c.currentY() == startY && c.isRunning()) {
      c.atWallObject(coords[0], coords[1]);
      c.sleep(1280);
    }
    c.log("Successfully entered door");
  }

  /**
   * Opens a fixed (does not stay open) wall object (c.atWallObject2())and sleeps until you are
   * moved to the destination
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param coords int[] -- Coordinates of a wall object
   */
  public static void atWallObject2(int[] coords) {
    c.log("Entered Wall Object2");
    while (c.isRunning() && c.isCurrentlyWalking()) c.sleep(640);
    int startX = c.currentX();
    int startY = c.currentY();
    c.log(
        String.format("Attempting to interact with wall object2 at (%s,%s)", coords[0], coords[1]));
    while (c.currentX() == startX & c.currentY() == startY && c.isRunning()) {
      c.atWallObject2(coords[0], coords[1]);
      c.sleep(1280);
    }
    c.log("Successfully entered wallObject2");
  }

  /**
   * Opens a fixed (does not stay open) wall object (c.atWallObject()) and sleeps until you are
   * moved to the destination
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param x int x coordinate of the object
   * @param y int y coordinate of the object
   */
  public static void atWallObject(int x, int y) {
    atWallObject(new int[] {x, y});
  }

  /**
   * Opens a fixed (does not stay open) wall object (c.atWallObject2())and sleeps until you are
   * moved to the destination
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param x int x coordinate of the object
   * @param y int y coordinate of the object
   */
  public static void atWallObject2(int x, int y) {
    atWallObject2(new int[] {x, y});
  }

  /**
   * Climbs a ladder or stairs and sleeps until you are moved to the destination Also Supports going
   * through fixed (does not stay open) doors
   *
   * @param coords int[] -- Coordinates of a climbable object
   */
  public static void climb(int[] coords) {
    c.log("Entered climb");
    while (c.isCurrentlyWalking()) c.sleep(640);
    int startX = c.currentX();
    int startY = c.currentY();
    c.log(String.format("Attempting to climb ladder or stairs at (%s,%s)", coords[0], coords[1]));
    while (c.currentX() == startX & c.currentY() == startY && c.isRunning()) {
      c.atObject(coords[0], coords[1]);
      c.sleep(640);
    }
    c.log("Successfully climbed");
  }

  /**
   * Climbs a ladder or stairs and sleeps until you are moved to the destination
   *
   * @param x int -- X coordinate of a climbable object
   * @param y int -- Y coordinate of a climbable object
   */
  public static void climb(int x, int y) {
    climb(new int[] {x, y});
  }

  /**
   * Interacts with objects in a given order based on coords. This has no pathing between objects,
   * so they must be clickable.
   *
   * @param coords - Int[][] -- Each index is a different Object's tile coordinates. Either {{X, Y},
   *     {X2, Y2}} or {TILE1[], TILE2[]}
   */
  public static void atObjectSequence(int[][] coords) {
    int maxDistance = 30;
    for (int i = 0; i < coords.length; i++) {
      int[] coord = coords[i];
      // Quit if the item is not found nearby
      if (distanceFromTile(coord) > maxDistance) quit(QuitReason.OBJECT_NOT_FOUND);
      while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
      // Walks to and performs atObject at the current coords
      if (c.isRunning()) {
        if (!c.atObject(coord[0], coord[1])) quit(QuitReason.OBJECT_NOT_FOUND);
        c.sleep(2560);
        while (c.isRunning() && c.isCurrentlyWalking()) c.sleep(640);
        if (i + 1 < coords.length) {
          // If the player is not within maxDistance of the next object sleep for up to 20 ticks
          for (int j = 0; j < 5; j++) {
            if (distanceFromTile(coords[i + 1]) > maxDistance && c.isRunning()) {
              c.atObject(coord[0], coord[1]);
              c.sleep(2560);
            } else {
              break;
            }
          }
        }
      }
    }
    c.sleep(1280);
  }

  /**
   * If TESTING_QUESTS is false, returns the quest's stage; if not, then it returns TESTING_STAGE
   *
   * @return int -- Quest stage
   */
  public static int getQuestStage() {
    if (TESTING_QUESTS) {
      @SuppressWarnings("ConstantValue")
      int stage = Math.max(TESTING_STAGE, 0);
      c.log("Testing '" + quest.getName() + "' at stage " + stage);
      return stage;
    }
    return c.getQuestStage(quest.getId());
  }

  /**
   * Update the bankItems Map from controller.getDebuggerBank(). This should always be run after
   * opening a bank interface.
   */
  public static void updateBankItems() {
    bankItems = c.getDebuggerBank();
  }

  /**
   * Quits the script and prints out a corresponding message.
   *
   * @param reason QuestReason - Reason to quit
   */
  public static void quit(QuitReason reason) {
    String message = reason.getMessage();
    if (c.isRunning() || message.equals(QuitReason.SCRIPT_STOPPED.getMessage())) {
      c.log(message, "red");
      c.stop();
    }
  }

  /**
   * Check if the client receives a server message and do something with it. This method is called
   * in AIOQuester where it overrides IdleScripts serverMessageInterrupt()
   *
   * @param message String - Message to check for
   */
  public static void serverMessageInterrupt(String message) {
    if (message.contains("@gr3@You @gr2@are @gr1@poisioned")) { // don't change spelling
      timeToDrinkAntidote = true;
    }
  }

  /** Draws the quest paint using PaintBuilder. */
  public static void drawPaint() {
    if (quest != null) {
      // Colors are based on https://spec.draculatheme.com/#sec-Standard
      int purple = 0xBD93F9;
      int darkGray = 0x282A36;
      int darkerGray = 0x1d1f27;
      int white = 0xF8F8F2;
      int green = 0x50FA7B;
      int yellow = 0xF1FA8C;
      int red = 0xFF5555;

      paintBuilder.setBackgroundColor(darkGray, 255);
      paintBuilder.setBorderColor(purple);

      paintBuilder.addRow(rowBuilder.centeredSingleStringRow(quest.getName(), purple, 4));
      paintBuilder.addRow(rowBuilder.centeredSingleStringRow(CURRENT_QUEST_STEP, purple, 3));
      if (quest.getStagesTotal() > 0) {
        paintBuilder.addSpacerRow(4);
        paintBuilder.addRow(
            rowBuilder.progressBarRow(
                QUEST_STAGE == -1 ? quest.getStagesTotal() + 1 : QUEST_STAGE,
                quest.getStagesTotal() + 1,
                darkerGray,
                green,
                darkGray,
                20,
                paintBuilder.getWidth() - 40,
                18,
                true,
                false,
                "Run Time: " + paintBuilder.stringRunTime,
                white));
        if (STEP_ITEMS != null && STEP_ITEMS.length > 0) {
          paintBuilder.addSpacerRow(8);
        }
      } else {
        paintBuilder.addSpacerRow(4);
        paintBuilder.addRow(
            rowBuilder.centeredSingleStringRow(
                "Run Time: " + paintBuilder.stringRunTime, white, 1));
      }

      if (STEP_ITEMS != null && STEP_ITEMS.length > 0) {
        paintBuilder.addSpacerRow(4);

        for (int[] stepItem : STEP_ITEMS) {

          String itemName = c.getItemName(stepItem[0]);
          itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
          String amount = c.getInventoryItemCount(stepItem[0]) + "/" + stepItem[1];

          String[] strings = {itemName, amount};
          int stringColor =
              c.getInventoryItemCount(stepItem[0]) >= stepItem[1]
                  ? green
                  : c.getInventoryItemCount(stepItem[0]) == 0 ? red : yellow;
          int[] colors = {stringColor, stringColor};
          int[] spacing = {
            4,
            paintBuilder.getWidth()
                - c.getStringWidth(amount, 1)
                - (amount.charAt(amount.length() - 1) == '1' ? 3 : 4)
                - 4
          };
          paintBuilder.addRow(rowBuilder.multipleStringRow(strings, colors, spacing, 1));
        }
      }
      paintBuilder.draw();
    }
  }
}
