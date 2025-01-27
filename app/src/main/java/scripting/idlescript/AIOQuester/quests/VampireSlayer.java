package scripting.idlescript.AIOQuester.quests;

import models.entities.ItemId;
import models.entities.Location;
import models.entities.NpcId;
import orsc.ORSCharacter;
import scripting.idlescript.AIOQuester.QuestHandler;
import scripting.idlescript.AIOQuester.models.QuitReason;

public final class VampireSlayer extends QuestHandler {
  // COORDINATES FOR walkPath() PATHS AND pickupUnreachableItem()

  // OBJECT COORDINATES

  private static final int[] MORGAN_DOOR = {136, 514};
  private static final int[] JOLLY_BOAR_DOOR = {81, 441};
  private static final int[] DRAYNOR_MANOR_DOOR_ONE = {210, 556};
  private static final int[] DRAYNOR_MANOR_DOOR_TWO = {210, 553}; // wall object
  private static final int[] DRAYNOR_MANOR_STAIRS = {204, 551};
  private static final int[] COUNT_COFFIN = {204, 3380};
  private static final int[] VARROCK_HOUSE_DOOR = {108, 530};
  private static final int[] CLOTHES_SHOP_DOOR = {136, 514};
  private static final int[] GENSTORE_SHOP_DOOR = {130, 515};
  private static final int[] LADDER_UP = {216, 620};
  private static final int[] LADDER_DOWN = {216, 1564};
  private static final int CUPBOARD_CLOSED_ID = 140;
  private static final int CUPBOARD_OPEN_ID = 141;

  // ITEM IDS
  private static final int GARLIC_ID = ItemId.GARLIC.getId();
  private static final int STAKE_ID = ItemId.STAKE.getId();
  private static final int BEER_ID = ItemId.BEER.getId();
  private static final int HAMMER_ID = ItemId.HAMMER.getId();
  private static final int COIN_ID = ItemId.COINS.getId();
  private static final int LEATHER_ARMOUR_ID = ItemId.LEATHER_ARMOUR.getId();

  // NPC IDS
  private static final int MORGAN_ID = NpcId.MORGAN.getId();
  private static final int HARLOW_ID = NpcId.DR_HARLOW.getId();
  private static final int COUNT_DRAYNOR_ID = NpcId.COUNT_DRAYNOR.getId();
  private static final int BARTENDER_ID = NpcId.BARTENDER_JOLLY_BOAR.getId();
  private static final int[] THESSALIA_ARRAY_ID = {NpcId.THESSALIA.getId()};
  private static final int[] GENSTORE_ARRAY_ID = {NpcId.SHOPKEEPER_VARROCK.getId()};

  // NPC DIALOGS
  private static final String[] BUY_BEER = {"I'll have a beer please"};
  private static final String[] STEP_ONE_START_QUEST_DIALOG = {"Ok I'm up for an adventure"};
  private static final String[] STEP_TWO_QUEST_DIALOG = {"Morgan needs your help", "Ok mate"};

  private static void RaiseGP() {
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

  private static void BuyHammer() {
    walkTowards(Location.VARROCK_GENERAL_STORE);
    c.openDoor(GENSTORE_SHOP_DOOR[0], GENSTORE_SHOP_DOOR[1]);
    c.sleep(640);
    c.openShop(GENSTORE_ARRAY_ID);
    while (c.isInShop() && c.getShopItemCount(HAMMER_ID) == 0 && c.isRunning()) {
      c.sleep(640);
    }
    while (c.isRunning()
        && c.isInShop()
        && c.getShopItemCount(HAMMER_ID) > 0
        && c.getUnnotedInventoryItemCount(HAMMER_ID) < 1) {
      c.shopBuy(HAMMER_ID);
      c.sleep(1280);
    }
    c.closeShop();
  }

  public static void run() {

    // quest stages here:

    while (isQuesting()) {

      switch (QUEST_STAGE) {
        case 0: //
          // Walk to Draynor. Handled by isQuesting.
          CURRENT_QUEST_STEP = "Starting quest with Morgan.";
          c.openDoor(MORGAN_DOOR[0], MORGAN_DOOR[1]);
          c.sleep(640);
          followNPCDialog(MORGAN_ID, STEP_ONE_START_QUEST_DIALOG);
          sleepUntilQuestStageChanges();

        case 1: //
          // Checks now for inventory state and quest items in bank.

          // if the player doesn't have inventory space
          CURRENT_QUEST_STEP = "Setting up the inventory.";
          walkTowards(Location.VARROCK_EAST_BANK); // move to bank
          c.openBank();
          while (c.isInBank()
              && c.getInventoryItemCount() > 24
              && c.isRunning()) { // deposit last item in inventory
            c.depositItem(c.getInventorySlotItemId(24)); // makes room for beads and coins
            c.sleep(1000);
          }
          if (c.isInBank()
              && c.getUnnotedInventoryItemCount(COIN_ID) < 5
              && c.isRunning()) { // deposit last item in inventory
            c.withdrawItem(10, 5); // Withdraws 5 coins
            c.sleep(1000);
          }
          if (c.isInBank() && c.getUnnotedInventoryItemCount(GARLIC_ID) == 0 && c.isRunning()) {
            c.withdrawItem(GARLIC_ID);
            c.sleep(640);
          }
          if (c.isInBank() && c.getUnnotedInventoryItemCount(HAMMER_ID) == 0 && c.isRunning()) {
            c.withdrawItem(HAMMER_ID);
            c.sleep(640);
          }
          if (c.isInBank() && c.getUnnotedInventoryItemCount(BEER_ID) == 0 && c.isRunning()) {
            c.withdrawItem(BEER_ID);
            c.sleep(640);
          }

          // withdraws up to 2 beer.
          for (int beer_withdraws = 0; beer_withdraws < 2; beer_withdraws++) {
            if (c.isInBank() && c.getUnnotedInventoryItemCount(BEER_ID) < 2 && c.isRunning()) {
              c.withdrawItem(BEER_ID);
              c.sleep(640);
            }
          }
          if (c.getUnnotedInventoryItemCount(10) < 5) {
            CURRENT_QUEST_STEP = "Not enough GP to proceed. Getting some quickly.";
            RaiseGP();
          }
          if (c.getUnnotedInventoryItemCount(HAMMER_ID) == 0) {
            CURRENT_QUEST_STEP = "Need a hammer. Buying one.";
            if (c.getUnnotedInventoryItemCount(COIN_ID) < 5) {
              RaiseGP();
            }
            BuyHammer();
          }
          CURRENT_QUEST_STEP = "Walking to Jolly Boar";
          walkTowards(82, 438); // JOlly Boar Coordinates, not in LocationWalker at time of writing
          c.openDoor(JOLLY_BOAR_DOOR[0], JOLLY_BOAR_DOOR[1]);

          while (c.getUnnotedInventoryItemCount(BEER_ID) < 2 && c.isRunning()) {
            CURRENT_QUEST_STEP = "Buying beer";
            followNPCDialog(BARTENDER_ID, BUY_BEER);
            c.sleep(1280);
          }
          CURRENT_QUEST_STEP = "Liquoring up the doc";
          followNPCDialog(HARLOW_ID, STEP_TWO_QUEST_DIALOG);
          sleepUntilQuestStageChanges();

        case 2:
          // int itemcount = c.getInventoryItemCount();
          // c.equipItemById((STAKE_ID));
          // c.sleep(640);
          // int preferred_weapon = c.getInventorySlotItemId(itemcount - 1);

          // while (c.isRunning()) {
          // CURRENT_QUEST_STEP = "Staking.";
          // c.equipItemById(STAKE_ID);
          // c.sleep(1280);
          // c.equipItemById(preferred_weapon);
          // c.sleep(1280);
          // }
          CURRENT_QUEST_STEP = "Walking back to Draynor";
          walkTowards(Location.DRAYNOR_MORGANS_HOUSE);
          c.openDoor(MORGAN_DOOR[0], MORGAN_DOOR[1]);

          if (c.getUnnotedInventoryItemCount(GARLIC_ID) < 1) {
            CURRENT_QUEST_STEP = "Grabbing a garlic."; // get garlic if needed
            c.atObject(LADDER_UP[0], LADDER_UP[1]);
            c.sleep(1280);
            c.atObject(CUPBOARD_CLOSED_ID); // opens the cupboard if necessary
            c.sleep(1280);
            c.atObject(CUPBOARD_OPEN_ID);
            c.sleep(1280); // searches the open cupboard
            c.atObject(LADDER_DOWN[0], LADDER_DOWN[1]);
            c.sleep(1280);
            c.openDoor(MORGAN_DOOR[0], MORGAN_DOOR[1]);
            c.sleep(640);
          }
          CURRENT_QUEST_STEP = "Walking to the Manor";
          walkTowards(209, 561);
          c.sleep(640);
          CURRENT_QUEST_STEP = "Going inside.";
          c.walkTo(210, 553); // directly infront of Manor entrance closed door
          c.sleep(2240);
          c.atWallObject(DRAYNOR_MANOR_DOOR_TWO[0], DRAYNOR_MANOR_DOOR_TWO[1]);
          c.sleep(2000);
          c.atObject(DRAYNOR_MANOR_DOOR_TWO[0], DRAYNOR_MANOR_DOOR_TWO[1]);
          c.sleep(2000);
          c.sleep(2000);
          CURRENT_QUEST_STEP = "Going down stairs";
          while (c.currentX() != 205 && c.currentY() != 554) {
            c.walkTo(205, 554);
            c.sleep(1280);
          }
          c.atObject(DRAYNOR_MANOR_STAIRS[0], DRAYNOR_MANOR_STAIRS[1]);
          c.sleep(1280);
          // boolean goingDown = true;
          // c.sleepUntil(() -> goingDown == (c.currentY() > 3000));
          c.sleep(2280);
          ORSCharacter COUNT_DRAYNOR = c.getNearestNpcById(COUNT_DRAYNOR_ID, true);
          while (COUNT_DRAYNOR == null && c.isRunning()) {
            c.atObject(COUNT_COFFIN[0], COUNT_COFFIN[1]);
            COUNT_DRAYNOR = c.getNearestNpcById(COUNT_DRAYNOR_ID, true);
            c.sleep(1280);
          }
          CURRENT_QUEST_STEP = "Attacking the Count.";
          COUNT_DRAYNOR = c.getNearestNpcById(COUNT_DRAYNOR_ID, true);
          if (COUNT_DRAYNOR != null) {
            c.attackNpc(COUNT_DRAYNOR.serverIndex);
            c.sleep(1000);
          }
          CURRENT_QUEST_STEP = "Fighting, still high HP.";
          while (c.isRunning() && COUNT_DRAYNOR != null) {
            if (COUNT_DRAYNOR.healthCurrent / COUNT_DRAYNOR.healthMax < 0.25) {
              CURRENT_QUEST_STEP = "Staking.";
              c.equipItemById(STAKE_ID);
            }
            COUNT_DRAYNOR = c.getNearestNpcById(COUNT_DRAYNOR_ID, true);
          }

          sleepUntilQuestStageChanges();

          break;
        default:
          quit(QuitReason.QUEST_STAGE_NOT_IN_SWITCH);
          break;
      }
    }
  }
}
