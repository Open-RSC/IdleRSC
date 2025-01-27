package scripting.idlescript.AIOQuester.quests;

import models.entities.ItemId;
import models.entities.Location;
import models.entities.NpcId;
import orsc.ORSCharacter;
import scripting.idlescript.AIOQuester.QuestHandler;
import scripting.idlescript.AIOQuester.models.QuitReason;

public final class WitchsPotion extends QuestHandler {
  // COORDINATES FOR walkPath() PATHS AND pickupUnreachableItem()
  private static final int GAME_TICK = 640;
  // OBJECT COORDINATES
  private static final int[] CAULDRON = {316, 666};

  private static final int[][] RIMMINGTON_DOORS = {
    {319, 667}, // door one
    {324, 667}, // door two
    {329, 661}, // door three
    {320, 662} // door four
  };

  // ITEM IDS
  private static final int COOKED_MEAT_ID = ItemId.COOKEDMEAT.getId();
  private static final int BURNT_MEAT_ID = ItemId.BURNTMEAT.getId(); // 2gp at The Clothes Shop
  private static final int EYE_OF_NEWT_ID = ItemId.EYE_OF_NEWT.getId();
  private static final int ONION_ID = ItemId.ONION.getId(); //
  private static final int RATS_TAIL_ID = ItemId.RATS_TAIL.getId(); //
  private static final int COIN_ID = ItemId.COINS.getId(); // // 1 gp Lumbridge General Store
  private static final int[] REQUIRED_ITEMS = {
    COOKED_MEAT_ID, EYE_OF_NEWT_ID, ONION_ID, RATS_TAIL_ID, COIN_ID
  };
  private static final int[][] drops = {{RATS_TAIL_ID, 1}};

  // NPC IDS
  private static final int HETTY_ID = NpcId.HETTY.getId();
  private static final int RAT_ID = NpcId.RAT_WITCHES_POTION.getId();

  // NPC DIALOGS
  private static final String[] STEP_ONE_START_QUEST_DIALOG = {
    "I am in search of a quest", "Yes help me become one with my darker side"
  };

  private static void depositNonQuestItems() {
    depositNonQuestItems(0);
  }

  private static void depositNonQuestItems(int gold_amount_needed) {
    c.walkTowardsBank();
    c.openBank();
    c.sleep(GAME_TICK * 4);
    c.depositAll();
    for (int required_item : REQUIRED_ITEMS) {
      if (required_item == COIN_ID && gold_amount_needed > 0) {
        c.withdrawItem(required_item, gold_amount_needed);
      } else c.withdrawItem(required_item);
      c.sleep(GAME_TICK);
    }
  }

  private static void RaiseGP_VARROCK() {
    CURRENT_QUEST_STEP = "Getting paid";
    int[] VARROCK_HOUSE_DOOR = {108, 530};
    int LEATHER_ARMOUR_ID = ItemId.LEATHER_ARMOUR.getId();
    int[] CLOTHES_SHOP_DOOR = {136, 514};
    int[] THESSALIA_ARRAY_ID = {NpcId.THESSALIA.getId()};
    walkTowards(Location.VARROCK_RUNE_SHOP);
    c.openDoor(VARROCK_HOUSE_DOOR[0], VARROCK_HOUSE_DOOR[1]);
    c.sleep(640);
    while (c.isRunning()
        && !(c.getUnnotedInventoryItemCount(LEATHER_ARMOUR_ID) >= 1)
        && c.isRunning()) { // we gonna be a gatherer
      int[] LEATHER_ARMOUR_LOCATION = c.getNearestItemById(LEATHER_ARMOUR_ID);
      if (LEATHER_ARMOUR_LOCATION != null
          && c.getUnnotedInventoryItemCount(LEATHER_ARMOUR_ID) == 0) {
        c.pickupItem(
            LEATHER_ARMOUR_LOCATION[0], LEATHER_ARMOUR_LOCATION[1], LEATHER_ARMOUR_ID, true, true);
        c.sleep(640);
      }
    }
    walkTowards(Location.VARROCK_CLOTHES_SHOP);
    c.openDoor(CLOTHES_SHOP_DOOR[0], CLOTHES_SHOP_DOOR[1]);
    c.sleep(640);
    c.openShop(THESSALIA_ARRAY_ID);
    c.sleep(640);
    c.shopSell(LEATHER_ARMOUR_ID);
    c.sleep(640); // should now have enough GP
  }

  private static void pickOnions(int amount) {
    CURRENT_QUEST_STEP = "Picking onion";
    if (c.getUnnotedInventoryItemCount(ONION_ID) < amount) {
      walkTowards(Location.LUMBRIDGE_ONION_FIELD);
      c.sleep(GAME_TICK * 2);
      while (c.isRunning() && c.getUnnotedInventoryItemCount(ONION_ID) < amount) {
        CURRENT_QUEST_STEP = "Retrieving Onion";
        int[] OIL_CAN_LOCATION = c.getNearestItemById(ONION_ID);
        if (OIL_CAN_LOCATION != null && c.getUnnotedInventoryItemCount(ONION_ID) < amount) {
          c.pickupItem(OIL_CAN_LOCATION[0], OIL_CAN_LOCATION[1], ONION_ID, true, true);
          c.sleep(640);
        }
      }
    }
  }

  private static void RaiseGP_Lumbridge() {
    CURRENT_QUEST_STEP = "Getting monies";
    int IRON_DAGGER_ID = ItemId.IRON_DAGGER.getId();
    int[] SHOPKEEPERS = {55, 83};
    int[][] SELL_ARRAY = {{IRON_DAGGER_ID, 1}};
    walkTowards(Location.LUMBRIDGE_GOBLIN_HUT);
    c.sleep(GAME_TICK * 2);
    while (c.getUnnotedInventoryItemCount(IRON_DAGGER_ID) < 1) {
      c.pickupItem(IRON_DAGGER_ID);
      c.sleep(GAME_TICK * 2);
    }
    walkTowards(Location.LUMBRIDGE_GENERAL_STORE);
    openShopThenSell(SHOPKEEPERS, SELL_ARRAY);
  }

  private static boolean getUsablePickaxe() {
    boolean usablePickaxeFound = false;
    int[][] all_pickaxes = {
      {41, 1262}, {31, 1261}, {21, 1260}, {6, 1259}, {1, 1258}, {1, 156}
    }; // {levelReq, itemId}
    int[] worn_equipment = c.getEquippedItemIds();
    int mining_level = c.getCurrentStat(14);
    for (int[] pickaxe : all_pickaxes) {

      c.log("Checking for item:" + " " + c.getItemName(pickaxe[1]));

      if (mining_level >= pickaxe[0] && c.getBankItemCount(pickaxe[1]) >= 1 && c.isInBank()) {
        c.log("Found usable Pickaxe in bank:");
        c.log(c.getItemName(pickaxe[1]));
        usablePickaxeFound = true;
        c.withdrawItem(pickaxe[1]);
        c.sleep(GAME_TICK * 4);
        c.equipItemById(pickaxe[1]);
        break;
      }
      if (mining_level >= pickaxe[0] && c.getUnnotedInventoryItemCount(pickaxe[1]) >= 1) {
        c.log("Found usable Pickaxe:");
        c.log(c.getItemName(pickaxe[1]));
        usablePickaxeFound = true;
        c.equipItemById(pickaxe[1]);
        break;
      }
      for (int equipment : worn_equipment) {
        if (mining_level >= pickaxe[0] && equipment == pickaxe[1]) {
          c.log("Found usable Pickaxe already worn:");
          c.log(c.getItemName(equipment));
          usablePickaxeFound = true;
          break;
        }
      }
      if (usablePickaxeFound) {
        return true;
      }
    }
    if (!usablePickaxeFound) {
      c.log("No usable pickaxe found");
      return false;
    }
    return false;
  }

  private static boolean checkUsablePickaxe() {
    boolean usablePickaxeFound = false;
    int[][] all_pickaxes = {
      {41, 1262}, {31, 1261}, {21, 1260}, {6, 1259}, {1, 1258}, {1, 156}
    }; // {levelReq, itemId}
    int[] worn_equipment = c.getEquippedItemIds();
    int mining_level = c.getCurrentStat(14);
    for (int[] pickaxe : all_pickaxes) {

      c.log("Checking for item:" + " " + c.getItemName(pickaxe[1]));

      if (mining_level >= pickaxe[0] && c.getUnnotedInventoryItemCount(pickaxe[1]) >= 1) {
        c.log("Found usable Pickaxe:");
        c.log(c.getItemName(pickaxe[1]));
        usablePickaxeFound = true;
        c.equipItemById(pickaxe[1]);
        break;
      }
      for (int equipment : worn_equipment) {
        if (mining_level >= pickaxe[0] && equipment == pickaxe[1]) {
          c.log("Found usable Pickaxe already worn:");
          c.log(c.getItemName(equipment));
          usablePickaxeFound = true;
          break;
        }
      }
      if (usablePickaxeFound) {
        return true;
      }
    }
    if (!usablePickaxeFound) {
      c.log("No usable pickaxe found");
      return false;
    }
    return false;
  }
  // int[] equipped_items = c.getEquippedItemIds();
  // for (int equippedItem : equipped_items) {
  // c.log(Integer.toString(equippedItem));
  // }
  // c.log(Integer.toString(c.getCurrentStat(14)));

  public static void getBurntMeat() {
    walkTowards(Location.BARBARIAN_VILLAGE_MESS_HALL);
    c.sleep(GAME_TICK);
    while (c.isRunning() && c.getUnnotedInventoryItemCount(COOKED_MEAT_ID) < 1) {
      CURRENT_QUEST_STEP = "Getting meat";
      c.walkTo(234, 501);
      c.sleep(GAME_TICK * 2);
      int[] OIL_CAN_LOCATION = c.getNearestItemById(COOKED_MEAT_ID);
      if (OIL_CAN_LOCATION != null && c.getUnnotedInventoryItemCount(COOKED_MEAT_ID) < 2) {
        c.pickupItem(OIL_CAN_LOCATION[0], OIL_CAN_LOCATION[1], COOKED_MEAT_ID, true, true);
        c.sleep(640);
      }
    }
    while (c.isRunning() && c.getUnnotedInventoryItemCount(COOKED_MEAT_ID) > 0) {
      c.walkTo(235, 495);
      c.useItemIdOnObject(234, 495, COOKED_MEAT_ID);
      c.sleep(GAME_TICK * 3);
    }
  }

  private static void farmNpcForDrops(int npcID, int[][] drops, int startingX, int startingY) {
    boolean hasDrops = false;

    walkTowards(startingX, startingY);
    while (!hasDrops && c.isRunning()) {

      for (int[] door : RIMMINGTON_DOORS) {
        if (c.isDoorOpen(door[0], door[1])) {
          c.openDoor(door[0], door[1]);
          c.sleep(GAME_TICK);
        }
      }
      ORSCharacter npc = c.getNearestNpcById(npcID, true);

      if (npc != null) {
        c.attackNpc(npc.serverIndex);
        c.sleep(GAME_TICK * 2);
      }

      for (int[] drop : drops) { //
        int[] dropCoord = c.getNearestItemById(drop[0]);

        if (dropCoord != null && c.getUnnotedInventoryItemCount(drop[0]) < drop[1]) {
          c.pickupItem(dropCoord[0], dropCoord[1], drop[0], true, true);
          c.sleep(GAME_TICK);
        }
      }
      for (int[] drop : drops) {
        if (c.getUnnotedInventoryItemCount(drop[0]) >= drop[1]) {
          hasDrops = true;
        } else {
          hasDrops = false;
          break;
        }
      }
      if (!c.isCurrentlyWalking() && !c.isInCombat() && npc == null) {
        Location.walkTowards(startingX, startingY);
      }
    }
  }

  private static void getEyeOfNewt() {
    int[] shopIds = {NpcId.BETTY.getId()};
    int[][] buyItems = {{EYE_OF_NEWT_ID, 1}};

    walkTowards(Location.PORT_SARIM_RUNE_SHOP);
    openShopThenBuy(shopIds, buyItems);
  }

  public static void run() {

    while (isQuesting()) {

      switch (QUEST_STAGE) {
        case 0:
          for (int[] door : RIMMINGTON_DOORS) {
            c.openDoor(door[0], door[1]);
            c.sleep(GAME_TICK);
          }
          followNPCDialog(HETTY_ID, STEP_ONE_START_QUEST_DIALOG);

        case 1: //
          CURRENT_QUEST_STEP = "Finding quest items";
          STEP_ITEMS =
              new int[][] {
                {BURNT_MEAT_ID, 1},
                {ONION_ID, 1},
                {EYE_OF_NEWT_ID, 1},
                {RATS_TAIL_ID, 1}
              };
          walkTowards(Location.DRAYNOR_BANK);
          depositNonQuestItems(3);
          if (c.getUnnotedInventoryItemCount(BURNT_MEAT_ID) == 0) {
            getBurntMeat();
          }
          if (c.getUnnotedInventoryItemCount(ONION_ID) == 0) {
            pickOnions(1);
          }
          if (c.getUnnotedInventoryItemCount(EYE_OF_NEWT_ID) == 0) {
            if (c.getUnnotedInventoryItemCount(COIN_ID) < 3) {
              RaiseGP_Lumbridge();
            }
            getEyeOfNewt();
          }
          if (c.getUnnotedInventoryItemCount(RATS_TAIL_ID) == 0) {
            farmNpcForDrops(RAT_ID, drops, 323, 664);
          }
          STEP_ITEMS = new int[][] {};
          walkTowards(Location.RIMMINGTON_HETTYS_HOUSE);
          c.talkToNpcId(HETTY_ID, false);
          sleepUntilQuestStageChanges();
        case 2: //
          c.sleep(GAME_TICK * 10);
          c.walkTo(317, 666);
          c.atObject2(CAULDRON[0], CAULDRON[1]);
          sleepUntilQuestStageChanges();
          break;

        case 3:

        default:
          quit(QuitReason.QUEST_STAGE_NOT_IN_SWITCH);
          break;
      }
    }
  }
}
