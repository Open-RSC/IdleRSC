package scripting.idlescript;

import bot.Main;
import controller.BotController;
import controller.Controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.entities.ItemId;
import models.entities.MapPoint;
import models.entities.QuestId;
import models.entities.SkillId;
import orsc.ORSCharacter;

public class QH__QuestHandler extends IdleScript {
  protected static final Controller c = Main.getController();
  private static final BotController bc = new BotController(c);

  // QUEST START COORDINATES AND DESCRIPTIONS
  protected int[][] LUMBRIDGE_CASTLE_COURTYARD = {{120, 650}, {128, 665}};
  protected int[][] FALADOR_WEST_BANK = {{328, 549}, {334, 557}};

  // The indexes for QUEST_START_LOCATIONS and QUEST_START_DESCRIPTIONS must align to correctly set
  // the START_DESCRIPTION in getStartDescriptions()
  private int[][][] QUEST_START_LOCATIONS = {LUMBRIDGE_CASTLE_COURTYARD, FALADOR_WEST_BANK};
  private String[] QUEST_START_DESCRIPTIONS = {"Lumbridge Castle Courtyard", "Falador West Bank"};

  // PUBLIC VARIABLES SET BY SUBCLASS QUEST SCRIPT
  protected String QUEST_NAME, START_DESCRIPTION, CURRENT_QUEST_STEP = "";
  protected String[] QUEST_REQUIREMENTS = {};
  protected int QUEST_ID, QUEST_STAGE, INVENTORY_SPACES_NEEDED;
  protected int[][] START_RECTANGLE, SKILL_REQUIREMENTS, ITEM_REQUIREMENTS, STEP_ITEMS = {};

  // PRIVATE VARIABLES
  private List<String> MISSING_LEVELS = new ArrayList<String>();
  private List<String> MISSING_QUESTS = new ArrayList<String>();
  private List<String> MISSING_ITEMS = new ArrayList<String>();
  private boolean IS_TESTING = false;

  /** Used for testing only */
  public void doTestLoop() {
    // DO TEST STUFF HERE!!
    
  }

  public int start(String[] param) {
    QUEST_NAME = "Quest Handler";
    CURRENT_QUEST_STEP = "This is only ran for testing.";
    int loops = 0;

    while (c.isRunning() && IS_TESTING) {
      loops++;
      c.displayMessage("@yel@Starting testing loop: " + loops);
      doTestLoop();
      c.displayMessage("@yel@Loops completed: " + loops);
      c.sleep(640);
    }
    quit("Ran handler");
    return 1000;
  }

  /**
   * Checks if the player meets the quests requirements. Quits the script and specifies what
   * requirement is missing otherwise.
   */
  public void doQuestChecks() {
    CURRENT_QUEST_STEP = "Starting " + QUEST_NAME;
    QUEST_ID = QuestId.getByName(QUEST_NAME).getId();
    QUEST_STAGE = c.getQuestStage(QUEST_ID);
    START_DESCRIPTION = getStartDescription();
    if (QUEST_STAGE == -1 && !IS_TESTING) {
      quit("Quest already complete");
    } else {
      requiredQuestsCheck();
      requiredLevelsCheck();
      requiredStartItemsCheck();
      if (INVENTORY_SPACES_NEEDED > 30 - c.getInventoryItemCount()) {
        quit("Not enough empty inventory spaces");
      }
      if (!isInRectangle(START_RECTANGLE)) {
        quit("Not in start area");
      } else {
        if (c.isRunning()) {
          c.displayMessage("@gre@Start location correct");
        }
      }
    }
  }
  /* Checks if the player has the required starting items for the quest
   * CURRENTLY ONLY CHECKS INVENTORY ITEMS. WILL NEED TO BE EXPANDED LATER TO
   * CHECK BANKS FOR MORE COMPLEX QUESTS
   */
  private void requiredStartItemsCheck() {
    MISSING_ITEMS = new ArrayList<String>();
    for (int[] item : ITEM_REQUIREMENTS) {
      int itemId = item[0];
      int amount = item[1];
      c.log(String.format("NEEDED: %s HELD: %s", amount, c.getInventoryItemCount(itemId)));
      String itemName = String.valueOf(ItemId.getById(itemId)).replaceAll("_", " ").toLowerCase();
      itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);

      if (c.getInventoryItemCount(itemId) < amount) {
        String missingString = String.format("@red@- %sx %s", amount, itemName);
        MISSING_ITEMS.add(missingString);
      }
    }
    if (MISSING_ITEMS.size() > 0) {
      quit("Missing items");
    } else {
      c.displayMessage("@gre@All item requirements met");
    }
  }

  /* Checks if the player has the required levels for the quest. */
  private void requiredLevelsCheck() {
    MISSING_LEVELS = new ArrayList<String>();
    for (int[] skill : SKILL_REQUIREMENTS) {
      String skillName = String.valueOf(SkillId.getById(skill[0])).toLowerCase();
      skillName = skillName.substring(0, 1).toUpperCase() + skillName.substring(1);
      int skillLevel = skill[1];

      if (!hasSkillLevel(skillName, skillLevel)) {
        String missingString = String.format("@red@- %s %s", skillName, skillLevel);
        MISSING_LEVELS.add(missingString);
      }
    }
    if (MISSING_LEVELS.size() > 0) {
      quit("Missing levels");
    } else {
      c.displayMessage("@gre@All level requirements met");
    }
  }

  /* Checks if the player has the required quests for the quest */
  private void requiredQuestsCheck() {
    MISSING_QUESTS = new ArrayList<String>();
    for (String quest : QUEST_REQUIREMENTS) {
      int id = QuestId.getByName(quest).getId();
      if (c.getQuestStage(id) != -1) {
        String missingString = String.format("@red@- %s", quest);
        MISSING_QUESTS.add(missingString);
      }
    }
    if (MISSING_QUESTS.size() > 0) {
      quit("Missing quests");
    } else {
      c.displayMessage("@gre@All quest requirements met");
    }
  }

  /**
   * Checks if the player has at least a certain amount of an item id.
   *
   * @param id int -- The item id to check
   * @param amount int -- The amount to check for
   * @return boolean
   */
  public boolean hasAtLeastItemAmount(int id, int amount) {
    return (c.getInventoryItemCount(id) >= amount);
  }

  /**
   * Checks if the player has the required level of a specified skill.
   *
   * @param skillName String -- Name of the skill
   * @param requiredLevel int -- Level required for the skill
   * @return boolean -- Does the player have the specified skill level
   */
  public boolean hasSkillLevel(String skillName, int requiredLevel) {
    return c.getBaseStat(c.getStatId(skillName)) >= requiredLevel;
  }

  /**
   * Checks if the player is within a rectangle of two given tiles.
   *
   * @param areaRectangle int[][] -- Two corner tiles of a rectangle {TILE1[],TILE2[]} or
   *     {{X1,Y1},{X2,Y2}}
   * @return boolean
   */
  public boolean isInRectangle(int[][] areaRectangle) {
    // Sorts the array to just in case to make it work correctly with the check
    int[] toSortX = {areaRectangle[0][0], areaRectangle[1][0]};
    int[] toSortY = {areaRectangle[0][1], areaRectangle[1][1]};
    Arrays.sort(toSortX);
    Arrays.sort(toSortY);
    int[][] sortedRectangle = {{toSortX[0], toSortY[0]}, {toSortX[1], toSortY[1]}};

    if (c.currentX() >= sortedRectangle[0][0]
        && c.currentX() <= sortedRectangle[1][0]
        && c.currentY() >= sortedRectangle[0][1]
        && c.currentY() <= sortedRectangle[1][1]) {
      return true;
    }
    return false;
  }

  /**
   * Returns a start description based on what index START_RECTANGLE is in START_LOCATIONS
   *
   * @return String -- Start description
   */
  private String getStartDescription() {
    if (QUEST_START_LOCATIONS.length != QUEST_START_DESCRIPTIONS.length) {
      quit("Start location description array mismatch");
      return "Start description not found";
    }
    for (int i = 0; i < QUEST_START_LOCATIONS.length; i++) {
      if (START_RECTANGLE == QUEST_START_LOCATIONS[i]) {
        return QUEST_START_DESCRIPTIONS[i];
      }
    }
    quit("Start location not found in locations array");
    return "Start description not found";
  }

  /**
   * Drops all of a given item id except one.
   *
   * @param itemId int -- Id of the item to drop
   * @param amountToKeep int -- Amount of item to keep
   */
  public void dropAllButAmount(int itemId, int amountToKeep) {
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
  public void openShopThenSell(int[] npcIds, int[][] sellItems) {
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
  public void openShopThenBuy(int[] npcIds, int[][] buyItems) {
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
  public void openShopThenSellAndBuy(int[] npcIds, int[][] sellItems, int[][] buyItems) {
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
   * Walks to specified tile and picks up an item id from an unreachable tile. Keeps retrying every
   * 2 ticks until the specified amount is picked up.
   *
   * @param itemId int -- Id of the item to pick up
   * @param standTile int[] -- Tile to stand at to pick up item
   * @param amount int -- The amount to pick up
   */
  public void pickupUnreachableItem(int itemId, int[] standTile, int amount) {
    int newId = changingIdCheck(itemId);
    if (c.getInventoryItemCount() == 30) {
      c.displayMessage("@red@Inventory is full");
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
   * Picks up the nearest item matching the given item id. Keeps retrying every 2 ticks until the
   * specified amount is picked up.
   *
   * @param itemId int -- Id of the item to pick up
   * @param amount int -- The amount to pick up
   */
  public void pickupGroundItem(int itemId, int amount) {
    int newId = changingIdCheck(itemId);
    if (c.getInventoryItemCount() == 30) {
      c.displayMessage("@red@Inventory is full");
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
  private int changingIdCheck(int itemId) {
    System.out.println(
        "If you get stuck in the while loop here you may have to add a line similar to the line below to changingIdCheck().");
    System.out.println(
        "This happens because the item you are trying to pick up changes ids when picked up.");
    System.out.println(
        "if (newId == ItemId.ITEM_NAME.getId()) newId = ItemId.NEW_ITEM_NAME.getId();");
    // If statements to switch ids for items that change to another id on pickup
    if (itemId == ItemId.FLOUR.getId()) return ItemId.POT_OF_FLOUR.getId();

    return itemId;
  }

  /**
   * Uses an item id on an the nearest npc with matching npc id.
   *
   * @param npcId int -- Id of npc, not server index
   * @param itemId int -- Id of the item to use on the npc
   */
  public void useItemOnNearestNpcId(int npcId, int itemId) {
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
        quit("Npc not found");
      }
    }
  }

  /**
   * Talks to the nearest npc with the given id.
   *
   * @param npcId int -- Id of the npc to talk to
   */
  public void talkToNpcId(int npcId) {
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
          if (oldMessage == npc.message) break;
        }
      } else {
        c.log(String.format("Could not find NPC: %s", npcId));
        quit("Npc not found");
      }
    }
    c.sleep(2560);
  }

  /**
   * Goes through a dialog with the nearest instance of an npc id with with dialogChoices[] being
   * the responses from the character.
   *
   * @param npcId int -- Id of the npc to talk to
   * @param dialogChoices String[] -- Responses to dialogs
   */
  public void followNPCDialog(int npcId, String[] dialogChoices) {
    int[] npc_id = {npcId};
    ORSCharacter npc = c.getNearestNpcByIds(npc_id, false);
    if (npc != null) {
      c.talkToNpc(npc.serverIndex);
      c.sleep(640);
      while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
      if (dialogChoices.length > 0) {
        for (int choiceIndex = 0; choiceIndex < dialogChoices.length; choiceIndex++) {
          // Sleep until a dialog menu appears
          while (!c.isInOptionMenu() && c.isRunning()) c.sleep(640);
          // Select the menu option that cooresponds with choiceIndex
          for (int option = 0; option < c.getOptionMenuCount(); option++) {
            if (c.getOptionsMenuText(option).equals(dialogChoices[choiceIndex])) {
              c.optionAnswer(option);
              c.sleep(640);
              break;
            }
          }
        }
      }
      // After dialogChoices has been depleted sleep until npc hasn't said a new dialog for 4 ticks
      while (c.isRunning()) {
        String oldMessage = npc.message;
        c.sleep(2560);
        if (oldMessage == npc.message) break;
      }
    } else {
      c.log(String.format("Could not find NPC: %s", npcId));
      quit("Npc not found");
    }
    c.sleep(2560);
  }

  public void sleepUntilQuestStageChanges() {
    int oldStage = QUEST_STAGE;
    while (c.getQuestStage(QUEST_ID) == oldStage && c.isRunning()) c.sleep(640);
  }

  /**
   * Uses PathWalkerApi to walk to a tile
   *
   * @param mapPoint MapPoint -- Coordinates of the tile to walk to
   */
  public void pathWalker(MapPoint mapPoint) {
    while (distanceFromTile(mapPoint) > 0 && c.isRunning()) {
      c.openNearbyDoor(1);
      bc.pathWalkerApi.walkTo(mapPoint);
    }
  }
  /**
   * Uses PathWalkerApi to walk to a tile
   *
   * @param coords int[] -- Coordinates of the tile to walk to
   */
  public void pathWalker(int[] coords) {
    MapPoint mapPoint = new MapPoint(coords[0], coords[1]);
    while (distanceFromTile(mapPoint) > 0 && c.isRunning()) {
      c.openNearbyDoor(1);
      bc.pathWalkerApi.walkTo(mapPoint);
    }
  }

  /**
   * Uses PathWalkerApi to walk to a tile
   *
   * @param x int -- X coordinate of the tile to walk to
   * @param y int -- Y coordinate of the tile to walk to
   */
  public void pathWalker(int x, int y) {
    MapPoint mapPoint = new MapPoint(x, y);
    while (distanceFromTile(mapPoint) > 0 && c.isRunning()) {
      c.openNearbyDoor(1);
      bc.pathWalkerApi.walkTo(mapPoint);
    }
  }

  /**
   * Follows a given path while checking for and opening closed doors next to given tiles.
   *
   * @param path - int[][] -- Each index is a different tile. Either {{X,Y},{X2,Y2}} or
   *     {TILE1[],TILE2[]}
   */
  public void walkPath(int[][] path) {
    for (int i = 0; i < path.length; ++i) {
      while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
      if (c.isRunning()) {
        c.sleep(640);
        while (c.isNearbyDoorClosed(1) && c.isRunning()) {
          c.openNearbyDoor(1);
          c.sleep(640);
        }
        if (!c.isReachable(path[i][0], path[i][1], false)) {
          c.log("Tile unreachable: " + path[i][0] + "," + path[i][1]);
          quit("Path tile not reachable");
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
   * Follows the reverse of a given path while checking for and opening closed doors next to given
   * tiles.
   *
   * @param path - int[][] -- Each index is a different tile. Either {{X,Y},{X2,Y2}} or
   *     {TILE1[],TILE2[]}
   */
  public void walkPathReverse(int[][] path) {
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
          quit("Path tile not reachable");
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
  public int distanceFromTile(int[] coords) {
    return c.distance(c.currentX(), c.currentY(), coords[0], coords[1]);
  }

  /**
   * Returns the distance the player is from the specified Map Point
   *
   * @param mapPoint MapPoint -- MapPoint to check
   * @return int -- Returns the distance from the MapPoint
   */
  public int distanceFromTile(MapPoint mapPoint) {
    MapPoint current = new MapPoint(c.currentX(), c.currentY());
    return MapPoint.distance(current, mapPoint);
  }

  /**
   * Climbs a ladder or stairs and sleeps until you are moved to the destination
   *
   * @param coords int[] -- Coordinates of climbable object
   */
  public void climb(int[] coords) {
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
   * @param x int -- X coordinate of climbable object
   * @param y int -- Y coordinate of climbable object
   */
  public void climb(int x, int y) {
    if (c.isRunning()) {
      while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
      int startX = c.currentX();
      int startY = c.currentY();
      c.log(String.format("Attempting to climb object at (%s,%s)", x, y));
      while (c.currentX() == startX & c.currentY() == startY && c.isRunning()) {
        c.atObject(x, y);
        c.sleep(640);
      }
      c.log("Successfully climbed");
    }
  }

  /**
   * Interacts with objects in a given order based on coords. This has no pathing between objects so
   * they must be clickable.
   *
   * @param coords - int[][] -- Each index is a different Object's tile coordinates. Either
   *     {{X,Y},{X2,Y2}} or {TILE1[],TILE2[]}
   */
  public void atObjectSequence(int[][] coords) {
    int maxDistance = 30;
    for (int i = 0; i < coords.length; i++) {
      int[] coord = coords[i];
      // returns if the item is not found nearby
      if (distanceFromTile(coord) > maxDistance) quit("Object not found");
      while (c.isCurrentlyWalking() && c.isRunning()) c.sleep(640);
      // Walks to and performs atObject at the current coords
      if (c.isRunning()) {
        if (!c.atObject(coord[0], coord[1])) quit("Object not found");
        c.sleep(2560);
        while (c.isCurrentlyWalking()) c.sleep(640);
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
   * Quits the script for a specified reason.
   *
   * @param reason String -- Reason for quitting script to give user information as to why the
   *     script stopped
   */
  public void quit(String reason) {
    String quitMessage = "";
    switch (reason.toLowerCase()) {
      case "script stopped":
        quitMessage = "The script has been stopped";
        break;
      case "missing levels":
        c.displayMessage("@red@You are missing the following levels for this quest:");
        for (String item : MISSING_LEVELS) c.displayMessage(item);
        break;
      case "missing quests":
        c.displayMessage("@red@This quest requires you to complete the following quests first:");

        for (String item : MISSING_QUESTS) c.displayMessage(item);
        break;
      case "missing items":
        c.displayMessage("@red@You need the following items in your inventory:");
        for (String item : MISSING_ITEMS) c.displayMessage(item);
        break;
      case "not enough empty inventory spaces":
        quitMessage =
            String.format("You need at least %s empty inventory spaces", INVENTORY_SPACES_NEEDED);
        break;
      case "not in start area":
        quitMessage = "Start the script at: " + START_DESCRIPTION;
        break;
      case "quest already complete":
        quitMessage = "This quest has already been completed";
        break;
      case "quest completed":
        quitMessage = "@gre@Quest Completed";
        break;
      case "ran handler":
        quitMessage = "Do not run this script. Run a QH_ quest script instead";
        break;
        // Players should never see the messages after this point if quest scripts are written
        // correctly
      case "npc not found":
        quitMessage = "May be too far away or have the wrong npc id";
        break;
      case "object not found":
        quitMessage = "An object was not found. Check the coordinates";
        break;
      case "path tile not reachable":
        quitMessage = "The walk path tile was not reachable";
        break;
      case "start location description array mismatch":
        quitMessage = "Descriptions and locations arrays have mismatched lengths";
        break;
      case "start location not found in locations array":
        quitMessage = "The start location is not defined in the locations array";
        break;
      default:
        quitMessage = "The script has unexpectedly stopped!";
        break;
    }
    if ((quitMessage.length() > 0 && c.isRunning())
        || (quitMessage.length() > 0 && reason.toLowerCase() == "script stopped")) {
      c.displayMessage("@red@" + quitMessage);
    }
    c.stop();
  }

  /*
   *  Every thing past this point is paint stuff.
   */

  /**
   * Creates the text string for the items in STEP_ITEMS used in the paint.
   *
   * @param i int -- Index of the current item from STEP_ITEMS
   * @param t int -- 1 for item name, 2 for amount needed
   * @return String -- Text for drawString
   */
  public String getQuestStepItems(int i, int t) {
    String color = "";
    String text = "";
    int held = 0;
    if (c.getInventoryItemCount(STEP_ITEMS[i][0]) == 0) {
      held = 0;
      color = "@red@";
    } else if (c.getInventoryItemCount(STEP_ITEMS[i][0]) > 0
        && c.getInventoryItemCount(STEP_ITEMS[i][0]) < STEP_ITEMS[i][1]) {
      held = c.getInventoryItemCount(STEP_ITEMS[i][0]);
      color = "@yel@";
    } else {
      held = STEP_ITEMS[i][1];
      color = "@gre@";
    }
    switch (t) {
      case 1:
        String itemName = c.getItemName(STEP_ITEMS[i][0]);
        itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
        text = String.format("  %s %s", color, itemName);
        break;
      case 2:
        text = String.format("  %s %s/%s", color, held, STEP_ITEMS[i][1]);
        break;
    }
    return text;
  }

  /**
   * Draws a centered drawString.
   *
   * @param str String -- Text to be drawn
   * @param widthOfPaintArea int -- The width of the area you want your string centered in.
   * @param y int -- Distance from the top of the screen to draw the string
   * @param leftPadding int -- The padding for the left side of the paint
   * @param color int -- RGB "HTML" Color Example: 0x36E2D7
   * @param fontSize int -- Font size. 1 or greater
   */
  private void drawCenteredString(
      String str, int widthOfPaintArea, int y, int leftPadding, int color, int fontSize) {
    int adjustedFontSize = Math.max(1, fontSize);
    c.drawString(
        str,
        spacingFromLength(str, widthOfPaintArea) + (leftPadding * 2),
        y,
        color,
        adjustedFontSize);
  }

  /**
   * Gets the spacing for drawCenteredString based on the width of characters in the game font
   *
   * @param text String -- The text to calculate spacing from
   * @param width int -- The width of the area paint area
   * @return int -- x coordinate for drawCenteredString
   */
  private int spacingFromLength(String text, int width) {
    if (text.length() < 1) return width / 2;
    int spacing = 0;
    String[] twelveWide = {"W"};
    String[] tenWide = {
      "m",
    };
    String[] nineWide = {
      "M", "w",
    };
    String[] eightWide = {
      "O", "Q",
    };
    String[] sevenWide = {
      "A", "B", "C", "D", "G", "H", "K", "N", "P", "R", "S", "U", "V",
    };
    String[] sixWide = {
      "E", "J", "L", "T", "X", "Y", "Z", "a", "b", "c", "d", "e", "g", "h", "k", "n", "o", "p", "q",
      "s", "u", "x", "?"
    };
    String[] fiveWide = {
      "F", "v", "y", "z",
    };
    String[] fourWide = {
      "f", "r", " ",
    };
    String[] threeWide = {
      "t",
    };
    String[] twoWide = {"I", "i", "j", "l", "'", "!"};

    for (int i = 0; i < text.length(); i++) {
      String currentChar = String.valueOf(text.charAt(i));
      if (Arrays.asList(twelveWide).contains(currentChar)) {
        spacing += 11; // One less because there isn't a pixel gap after them
      } else if (Arrays.asList(tenWide).contains(currentChar)) {
        spacing += 9; // One less because there isn't a pixel gap after them
      } else if (Arrays.asList(nineWide).contains(currentChar)) {
        spacing += 9;
      } else if (Arrays.asList(eightWide).contains(currentChar)) {
        spacing += 8;
      } else if (Arrays.asList(sevenWide).contains(currentChar)) {
        spacing += 7;
      } else if (Arrays.asList(sixWide).contains(currentChar)) {
        spacing += 6;
      } else if (Arrays.asList(fiveWide).contains(currentChar)) {
        spacing += 5;
      } else if (Arrays.asList(fourWide).contains(currentChar)) {
        spacing += 4;
      } else if (Arrays.asList(threeWide).contains(currentChar)) {
        spacing += 3;
      } else if (Arrays.asList(twoWide).contains(currentChar)) {
        spacing += 2;
      }
    }
    spacing += (text.length() / 2);
    int newSpacing = (width / 2) - (spacing / 2);
    return newSpacing;
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      int top = 20;
      int left = 6;
      int paintWidth = 182;
      int paintHeight = 32;
      int alphaBoxHeight = 16;
      int borderColor = 0x36E2D7;
      int bgColorMain = 0x083E6B;
      int bgColorItems = 0x020C3F;
      int bgOpacity = 255;

      int nextTop = 0;

      c.drawBoxAlpha(left, top, paintWidth, alphaBoxHeight * 2, bgColorMain, bgOpacity);
      drawCenteredString("@whi@" + QUEST_NAME, paintWidth, top + 13, left, bgColorMain, 1);
      drawCenteredString(
          "@yel@" + CURRENT_QUEST_STEP,
          paintWidth,
          top + alphaBoxHeight + 13,
          left,
          bgColorMain,
          1);
      // Paint Item Requirements
      if (STEP_ITEMS.length > 0) {
        for (int i = 0; i < STEP_ITEMS.length; i++) {
          String itemCount = getQuestStepItems(i, 2);
          int itemCountSpacing = 160 - (7 * (itemCount.length() - 10));

          paintHeight += alphaBoxHeight;
          nextTop = top + (alphaBoxHeight * 2) + (i * alphaBoxHeight);
          c.drawBoxAlpha(left, nextTop, paintWidth, alphaBoxHeight, bgColorItems, bgOpacity);
          c.drawString(getQuestStepItems(i, 1), left + 2, nextTop + 12, bgColorMain, 1);
          c.drawString(itemCount, itemCountSpacing, nextTop + 12, bgColorMain, 1);
        }
      }
      c.drawBoxBorder(left, top, paintWidth, paintHeight, borderColor);
      // Used to check if the paint text is centered
      /* if (IS_TESTING) c.drawLineVert((paintWidth / 2) + left, top, paintHeight, borderColor); */
    }
  }
}
