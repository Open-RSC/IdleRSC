package scripting.idlescript;

import bot.Main;
import controller.BotController;
import controller.Controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import models.entities.ItemId;
import models.entities.MapPoint;
import models.entities.QuestId;
import models.entities.SkillId;
import orsc.ORSCharacter;

/*
TODO: Add a method for atObjectUntilItemAmount(int[][] objectCoords, int itemId, int amount) for picking berries until getting amount
TODO: Add a method for atObjectUntilItemAmount(int objectX, int objectY, int itemId, int amount) for picking berries until getting amount
*/

/*
Authentic Quests:
$ https://gitlab.com/open-runescape-classic/core/-/tree/develop/server/plugins/com/openrsc/server/plugins/authentic/quests

Custom Quests:
$ https://gitlab.com/open-runescape-classic/core/-/tree/develop/server/plugins/com/openrsc/server/plugins/custom/quests
*/

public class QH__QuestHandler extends IdleScript {
  protected static final Controller c = Main.getController();
  private static final BotController bc = new BotController(c);

  // TODO: Don't forget to set this to false before committing */
  protected boolean IS_TESTING = false;

  // QUEST START COORDINATES AND DESCRIPTIONS
  protected int[][] LUMBRIDGE_CASTLE_COURTYARD = {{120, 650}, {128, 665}};
  protected int[][] FALADOR_WEST_BANK = {{328, 549}, {334, 557}};
  protected int[][] VARROCK_SQUARE = {{126, 505}, {137, 511}};
  protected int[][] SORCERERS_TOWER = {{507, 505}, {514, 511}};
  protected int[][] EDGE_MONASTERY = {{249, 456}, {265, 472}};
  protected int[][] TAV_DUNGEON_LADDER = {{371, 514}, {384, 525}};
  protected int[][] ARDY_MONASTERY = {{575, 651}, {603, 669}};
  protected int[][] WEST_DWARF_TUNNEL = {{420, 450}, {432, 464}};
  protected int[][] ARDY_SOUTH_BANK = {{534, 605}, {557, 619}};
  protected int[][] SHRIMP_AND_PARROT = {{443, 679}, {459, 692}};
  protected int[][] BARB_OUTPOST = {{493, 538}, {506, 555}};
  protected int[][] SORCERERS_TOWER_ABOVE = {{507, 1448}, {514, 1458}};

  private final HashMap<int[][], String> START_DESCRIPTIONS =
      new HashMap<int[][], String>() {
        {
          put(LUMBRIDGE_CASTLE_COURTYARD, "Lumbridge Castle Courtyard");
          put(FALADOR_WEST_BANK, "Falador West Bank");
          put(VARROCK_SQUARE, "Varrock Square");
          put(SORCERERS_TOWER, "Ground Floor Sorcerers' Tower");
          put(EDGE_MONASTERY, "Edgeville Monastery");
          put(TAV_DUNGEON_LADDER, "Near Taverly dungeon entrance ladder");
          put(ARDY_MONASTERY, "Near Ardougne Monastery");
          put(WEST_DWARF_TUNNEL, "Western Entrance of Dwarf Tunnel");
          put(ARDY_SOUTH_BANK, "Ardougne south bank");
          put(SHRIMP_AND_PARROT, "Shrimp and Parrot Pub (Brimhaven)");
          put(BARB_OUTPOST, "Barbarian Outpost");
          put(SORCERERS_TOWER_ABOVE, "1st Floor Sorcerers' Tower");
        }
      };

  // PUBLIC VARIABLES SET BY SUBCLASS QUEST SCRIPT
  protected String QUEST_NAME, START_DESCRIPTION, CURRENT_QUEST_STEP = "";
  protected String[] QUEST_REQUIREMENTS = {};
  protected int QUEST_ID, QUEST_STAGE, TOTAL_QUEST_STAGES, INVENTORY_SPACES_NEEDED;
  protected int[][] START_RECTANGLE,
      SKILL_REQUIREMENTS,
      EQUIP_REQUIREMENTS,
      ITEM_REQUIREMENTS,
      STEP_ITEMS = {};
  protected boolean timeToDrinkAntidote = false;
  // PRIVATE VARIABLES
  private List<String> MISSING_LEVELS = new ArrayList<String>();
  private List<String> MISSING_QUESTS = new ArrayList<String>();
  private List<String> MISSING_ITEMS = new ArrayList<String>();
  private List<String> MISSING_EQUIP = new ArrayList<String>();

  /** Used for testing only */
  public void doTestLoop() {
    // DO TEST STUFF HERE!!

  }

  public int start(String[] param) {
    // Start needed here for handler testing
    paintBuilder.start(4, 18, 182);
    QUEST_NAME = "Quest Handler";
    CURRENT_QUEST_STEP = "This is only ran for testing";
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
    // Start needed here for quests
    paintBuilder.start(4, 18, 182);

    if (!QUEST_NAME.equals("Miniquest")) {
      QUEST_ID = QuestId.getByName(QUEST_NAME).getId();
      QUEST_STAGE = c.getQuestStage(QUEST_ID);
      if (QUEST_STAGE == -1 && !IS_TESTING) {
        quit("Quest already complete");
        return;
      }
    }
    if (START_DESCRIPTIONS.get(START_RECTANGLE) == null)
      quit("Start location not found in locations array");
    START_DESCRIPTION = START_DESCRIPTIONS.get(START_RECTANGLE);
    CURRENT_QUEST_STEP = "Starting " + QUEST_NAME;
    c.setBatchBars(true);
    requiredQuestsCheck();
    requiredLevelsCheck();
    requiredStartItemsCheck();
    requiredStartEquipCheck();
    if (INVENTORY_SPACES_NEEDED > 30 - c.getInventoryItemCount()) {
      quit("Not enough empty inventory spaces");
    }
    if (START_RECTANGLE.length < 2) {
      quit("no start area");
    } else if (!isInRectangle(START_RECTANGLE)) {
      quit("Not in start area");
    } else {
      if (c.isRunning()) {
        c.displayMessage("@gre@Start location correct");
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
      String itemName = String.valueOf(ItemId.getById(itemId)).replaceAll("_", " ").toLowerCase();
      itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);

      if (c.getInventoryItemCount(itemId) < amount) {
        String missingString = String.format("@red@- %sx %s", amount, itemName);
        MISSING_ITEMS.add(missingString);
      }
    }
    if (!MISSING_ITEMS.isEmpty()) {
      quit("Missing items");
    } else {
      c.displayMessage("@gre@All inventory item requirements met");
    }
  }
  /* Checks if the player has the required starting items EQUIPPED for the quest
   * CURRENTLY ONLY CHECKS EQUIP ITEMS ARE THERE AND NOT HOW MANY
   */
  private void requiredStartEquipCheck() {
    MISSING_EQUIP = new ArrayList<String>();
    for (int[] item : EQUIP_REQUIREMENTS) {
      int itemId = item[0];
      int amount =
          1; // item[1]; //unimplemented, always put 1. todo add support to check amounts to
      // controller (arrows)
      String itemName = String.valueOf(ItemId.getById(itemId)).replaceAll("_", " ").toLowerCase();
      itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);

      if (!c.isItemIdEquipped(itemId)) {
        String missingString = String.format("@red@- %sx %s", amount, itemName);
        MISSING_EQUIP.add(missingString);
      }
    }
    if (!MISSING_EQUIP.isEmpty()) {
      quit("Missing equip");
    } else {
      c.displayMessage("@gre@All equip item requirements met");
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
    if (!MISSING_LEVELS.isEmpty()) {
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
    if (!MISSING_QUESTS.isEmpty()) {
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
          if (oldMessage.equals(npc.message)) break;
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
   * <p>This method is for pop up quest dialog after interacting with an object
   *
   * @param npcId int -- Id of the npc to talk to
   * @param dialogChoices String[] -- Responses to dialogs
   */
  public void followNPCDialogPopUps(int npcId, String[] dialogChoices) {
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
      quit("Npc not found");
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
   * Climbs a ladder or stairs and sleeps until you are moved to the destination Also Supports going
   * through Opens a fixed(does not stay open) doors
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
   * Does c.atObject command and sleeps until you are moved to the destination Climbs a ladder or
   * stairs and sleeps until you are moved to the destination Also Supports going through Opens a
   * fixed(does not stay open) doors
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param coords int[] -- Coordinates of object
   */
  public void atObject(int[] coords) {
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
   * Does c.atObject2 command and sleeps until you are moved to the destination Climbs a ladder or
   * stairs and sleeps until you are moved to the destination Also Supports going through Opens a
   * fixed(does not stay open) doors
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param coords int[] -- Coordinates of object
   */
  public void atObject2(int[] coords) {
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
   * Does c.atObject command and sleeps until you are moved to the destination Climbs a ladder or
   * stairs and sleeps until you are moved to the destination Also Supports going through Opens a
   * fixed(does not stay open) doors
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param x int x coordinate of the object
   * @param y int y coordinate of the object
   */
  public void atObject(int x, int y) {
    c.log("Entered Object");
    while (c.isRunning() && c.isCurrentlyWalking()) c.sleep(640);
    int startX = c.currentX();
    int startY = c.currentY();
    c.log(String.format("Attempting to interact with object at (%s,%s)", x, y));
    while (c.currentX() == startX & c.currentY() == startY && c.isRunning()) {
      c.atObject(x, y);
      c.sleep(1280);
    }
    c.log("Successfully entered Object");
  }

  /**
   * Does c.atObject2 command and sleeps until you are moved to the destination Climbs a ladder or
   * stairs and sleeps until you are moved to the destination Also Supports going through Opens a
   * fixed(does not stay open) doors
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param x int x coordinate of the object
   * @param y int y coordinate of the object
   */
  public void atObject2(int x, int y) {
    c.log("Entered Object2");
    while (c.isRunning() && c.isCurrentlyWalking()) c.sleep(640);
    int startX = c.currentX();
    int startY = c.currentY();
    c.log(String.format("Attempting to interact with object2 at (%s,%s)", x, y));
    while (c.currentX() == startX & c.currentY() == startY && c.isRunning()) {
      c.atObject2(x, y);
      c.sleep(1280);
    }
    c.log("Successfully entered Object2");
  }
  /**
   * Opens a fixed(does not stay open) wall object (c.atWallObject()) and sleeps until you are moved
   * to the destination
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param coords int[] -- Coordinates of wall object
   */
  public void atWallObject(int[] coords) {
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
   * Opens a fixed(does not stay open) wall object (c.atWallObject2())and sleeps until you are moved
   * to the destination
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param coords int[] -- Coordinates of wall object
   */
  public void atWallObject2(int[] coords) {
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
   * Opens a fixed(does not stay open) wall object (c.atWallObject()) and sleeps until you are moved
   * to the destination
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param x int x coordinate of the object
   * @param y int y coordinate of the object
   */
  public void atWallObject(int x, int y) {
    c.log("Entered Wall Object");
    while (c.isRunning() && c.isCurrentlyWalking()) c.sleep(640);
    int startX = c.currentX();
    int startY = c.currentY();
    c.log(String.format("Attempting to interact with wall object at (%s,%s)", x, y));
    while (c.currentX() == startX & c.currentY() == startY && c.isRunning()) {
      c.atWallObject(x, y);
      c.sleep(1280);
    }
    c.log("Successfully entered door");
  }
  /**
   * Opens a fixed(does not stay open) wall object (c.atWallObject2())and sleeps until you are moved
   * to the destination
   *
   * <p>*Requires the object moving your coordinates*
   *
   * @param x int x coordinate of the object
   * @param y int y coordinate of the object
   */
  public void atWallObject2(int x, int y) {
    c.log("Entered Wall Object2");
    while (c.isRunning() && c.isCurrentlyWalking()) c.sleep(640);
    int startX = c.currentX();
    int startY = c.currentY();
    c.log(String.format("Attempting to interact with wall object2 at (%s,%s)", x, y));
    while (c.currentX() == startX & c.currentY() == startY && c.isRunning()) {
      c.atWallObject2(x, y);
      c.sleep(1280);
    }
    c.log("Successfully entered wallObject2");
  }
  /**
   * Climbs a ladder or stairs and sleeps until you are moved to the destination
   *
   * @param x int -- X coordinate of climbable object
   * @param y int -- Y coordinate of climbable object
   */
  public void climb(int x, int y) {
    if (c.isRunning()) {
      while (c.isRunning() && c.isCurrentlyWalking()) c.sleep(640);
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
      case "missing equip":
        c.displayMessage("@red@You need the following items equipped:");
        for (String item : MISSING_EQUIP) c.displayMessage(item);
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
      case "no start area":
        quitMessage = "Start area was not defined";
        break;
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
      c.log(quitMessage, "red");
    }
    c.stop();
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("@gr3@You @gr2@are @gr1@poisioned")) { // don't change spelling
      timeToDrinkAntidote = true;
    }
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

      paintBuilder.setBackgroundColor(darkGray, 255);
      paintBuilder.setBorderColor(purple);

      paintBuilder.addRow(rowBuilder.centeredSingleStringRow(QUEST_NAME, purple, 4));
      paintBuilder.addRow(rowBuilder.centeredSingleStringRow(CURRENT_QUEST_STEP, purple, 3));
      if (TOTAL_QUEST_STAGES > 0) {
        paintBuilder.addSpacerRow(4);
        paintBuilder.addRow(
            rowBuilder.progressBarRow(
                QUEST_STAGE == -1 ? TOTAL_QUEST_STAGES + 1 : QUEST_STAGE,
                TOTAL_QUEST_STAGES + 1,
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
        if (STEP_ITEMS.length > 0) {
          paintBuilder.addSpacerRow(8);
        }
      } else {
        paintBuilder.addSpacerRow(4);
        paintBuilder.addRow(
            rowBuilder.centeredSingleStringRow(
                "Run Time: " + paintBuilder.stringRunTime, white, 1));
      }

      if (STEP_ITEMS.length > 0) {
        paintBuilder.addSpacerRow(4);
        for (int i = 0; i < STEP_ITEMS.length; i++) {

          String itemName = c.getItemName(STEP_ITEMS[i][0]);
          itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
          String amount =
              String.valueOf(c.getInventoryItemCount(STEP_ITEMS[i][0]) + "/" + STEP_ITEMS[i][1]);

          String[] strings = {itemName, amount};
          int stringColor =
              c.getInventoryItemCount(STEP_ITEMS[i][0]) >= STEP_ITEMS[i][1]
                  ? green
                  : c.getInventoryItemCount(STEP_ITEMS[i][0]) == 0 ? red : yellow;
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
