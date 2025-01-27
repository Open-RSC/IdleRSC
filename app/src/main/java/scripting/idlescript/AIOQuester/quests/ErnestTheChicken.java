package scripting.idlescript.AIOQuester.quests;

import models.entities.ItemId;
import models.entities.Location;
import models.entities.NpcId;
import scripting.idlescript.AIOQuester.QuestHandler;
import scripting.idlescript.AIOQuester.models.QuitReason;

public final class ErnestTheChicken extends QuestHandler {
  // COORDINATES FOR walkPath() PATHS AND pickupUnreachableItem()

  // OBJECT COORDINATES

  private static final int[] DRAYNOR_MANOR_DOOR_ONE = {210, 556};
  private static final int[] DRAYNOR_MANOR_DOOR_TWO = {210, 553}; // wall object door
  private static final int[] DRAYNOR_MANOR_DOOR_THREE = {213, 545};
  private static final int[] DRAYNOR_MANOR_DOOR_FOUR = {199, 551};
  private static final int[] DRAYNOR_MANOR_DOOR_FIVE = {217, 543};
  private static final int[] DRAYNOR_MANOR_DOOR_SIX = {220, 547};
  private static final int[] DRAYNOR_MANOR_DOOR_SEVEN = {215, 550};
  private static final int[] DRAYNOR_MANOR_CLOSET = {212, 545};
  private static final int[] DRAYNOR_MANOR_F2_DOOR_ONE = {207, 1495};
  private static final int[] DRAYNOR_MANOR_F2_DOOR_TWO = {209, 1497};
  private static final int[] DRAYNOR_MANOR_F3_DOOR = {211, 2438};
  private static final int[] DRAYNOR_MANOR_STAIRS_UP = {210, 547};
  private static final int[] DRAYNOR_MANOR_STAIRS_DOWN = {210, 1491};
  private static final int[] DRAYNOR_MANOR_LADDER_UP = {215, 1492};
  private static final int[] DRAYNOR_MANOR_LADDER_DOWN = {215, 2436};
  private static final int[] DRAYNOR_MANOR_LADDER_DOWN_BASEMENT = {223, 553};
  private static final int[] DRAYNOR_MANOR_LADDER_UP_BASEMENT = {223, 3385};
  private static final int[] COMPOST_HEAP = {230, 552};
  private static final int[] FOUNTAIN = {226, 565};

  private static final int[] DRAYNOR_MANOR_LEVER_A = {225, 3386};
  private static final int[] DRAYNOR_MANOR_LEVER_B = {222, 3382};
  private static final int[] DRAYNOR_MANOR_LEVER_C = {222, 3378};
  private static final int[] DRAYNOR_MANOR_LEVER_D = {223, 3375};
  private static final int[] DRAYNOR_MANOR_LEVER_E = {229, 3375};
  private static final int[] DRAYNOR_MANOR_LEVER_F = {230, 3376};
  private static final int[] DRAYNOR_MANOR_DOOR_A = {223, 3381};
  private static final int[] DRAYNOR_MANOR_DOOR_B = {225, 3379};
  private static final int[] DRAYNOR_MANOR_DOOR_C = {225, 3376};
  private static final int[] DRAYNOR_MANOR_DOOR_D = {226, 3378};
  private static final int[] DRAYNOR_MANOR_DOOR_E = {226, 3381};
  private static final int[] DRAYNOR_MANOR_DOOR_F = {228, 3379};
  private static final int[] DRAYNOR_MANOR_DOOR_G = {228, 3376};
  private static final int[] DRAYNOR_MANOR_DOOR_H = {229, 3378};
  private static final int[] DRAYNOR_MANOR_DOOR_I = {228, 3382};

  // ITEM IDS
  private static final int FISH_FOOD_ID = ItemId.FISH_FOOD.getId();
  private static final int PRESSURE_GAUGE_ID = ItemId.PRESSURE_GAUGE.getId();
  private static final int RUBBER_TUBE_ID = ItemId.RUBBER_TUBE.getId();
  private static final int SPADE_ID = ItemId.SPADE.getId();
  private static final int CLOSET_KEY_ID = ItemId.CLOSET_KEY.getId();
  private static final int OIL_CAN_ID = ItemId.OIL_CAN.getId();
  private static final int POISON_ID = ItemId.POISON.getId();
  private static final int POISON_FOOD_ID = ItemId.POISONED_FISH_FOOD.getId();

  // NPC IDS
  private static final int VERONICA_ID = NpcId.VERONICA.getId();
  private static final int ODDENSTEIN_ID = NpcId.PROFESSOR_ODDENSTEIN.getId();

  // NPC DIALOGS
  private static final String[] STEP_ONE_START_QUEST_DIALOG = {
    "Aha, sounds like a quest. I'll help"
  };
  private static final String[] STEP_TWO_QUEST_DIALOG = {
    "I'm looking for a guy called Ernest", "Change him back this instant"
  };

  public static void puzzle() {
    // step one
    CURRENT_QUEST_STEP = "Doing this damn puzzle";
    while (c.isRunning() && c.getInventoryItemCount(OIL_CAN_ID) == 0) {
      c.walkTo(DRAYNOR_MANOR_LEVER_A[0], DRAYNOR_MANOR_LEVER_A[1]);
      c.sleep(640);
      c.atObject(DRAYNOR_MANOR_LEVER_A[0], DRAYNOR_MANOR_LEVER_A[1]);
      c.sleep(640);
      c.walkTo(DRAYNOR_MANOR_LEVER_B[0], DRAYNOR_MANOR_LEVER_B[1]);
      c.sleep(640);
      c.atObject(DRAYNOR_MANOR_LEVER_B[0], DRAYNOR_MANOR_LEVER_B[1]);
      c.sleep(640);
      c.walkTo(DRAYNOR_MANOR_DOOR_A[0], DRAYNOR_MANOR_DOOR_A[1]);
      c.sleep(1280);
      c.atWallObject(DRAYNOR_MANOR_DOOR_A[0], DRAYNOR_MANOR_DOOR_A[1]);
      c.sleep(2800);

      // step two
      c.sleep(1280);
      c.walkTo(DRAYNOR_MANOR_LEVER_D[0], DRAYNOR_MANOR_LEVER_D[1]);
      c.sleep(1280);
      c.atObject(DRAYNOR_MANOR_LEVER_D[0], DRAYNOR_MANOR_LEVER_D[1]);
      c.sleep(1280);
      c.walkTo(224, 3379);
      c.sleep(1280);
      c.atWallObject(DRAYNOR_MANOR_DOOR_B[0], DRAYNOR_MANOR_DOOR_B[1]);
      c.sleep(2800);
      c.walkTo(226, 3380);
      c.sleep(2800);
      c.atWallObject(DRAYNOR_MANOR_DOOR_E[0], DRAYNOR_MANOR_DOOR_E[1]);
      c.sleep(2800);

      // step three
      c.walkTo(DRAYNOR_MANOR_LEVER_A[0], DRAYNOR_MANOR_LEVER_A[1]);
      c.sleep(1280);
      c.atObject(DRAYNOR_MANOR_LEVER_A[0], DRAYNOR_MANOR_LEVER_A[1]);
      c.sleep(1280);
      c.walkTo(DRAYNOR_MANOR_LEVER_B[0], DRAYNOR_MANOR_LEVER_B[1]);
      c.sleep(1280);
      c.atObject(DRAYNOR_MANOR_LEVER_B[0], DRAYNOR_MANOR_LEVER_B[1]);
      c.sleep(1280);
      c.walkTo(DRAYNOR_MANOR_DOOR_E[0], DRAYNOR_MANOR_DOOR_E[1]);
      c.sleep(2580);
      c.atWallObject(DRAYNOR_MANOR_DOOR_E[0], DRAYNOR_MANOR_DOOR_E[1]);
      c.sleep(2800);
      c.walkTo(DRAYNOR_MANOR_DOOR_F[0] - 1, DRAYNOR_MANOR_DOOR_F[1]);
      c.sleep(2800);
      c.atWallObject(DRAYNOR_MANOR_DOOR_F[0], DRAYNOR_MANOR_DOOR_F[1]);
      c.sleep(2800);
      c.walkTo(DRAYNOR_MANOR_DOOR_H[0], DRAYNOR_MANOR_DOOR_H[1]);
      c.sleep(2800);
      c.atWallObject(DRAYNOR_MANOR_DOOR_H[0], DRAYNOR_MANOR_DOOR_H[1]);
      c.sleep(2800);

      // step four
      c.walkTo(DRAYNOR_MANOR_LEVER_E[0], DRAYNOR_MANOR_LEVER_E[1]);
      c.sleep(1280);
      c.atObject(DRAYNOR_MANOR_LEVER_E[0], DRAYNOR_MANOR_LEVER_E[1]);
      c.sleep(1280);
      c.walkTo(DRAYNOR_MANOR_LEVER_F[0], DRAYNOR_MANOR_LEVER_F[1]);
      c.sleep(1280);
      c.atObject(DRAYNOR_MANOR_LEVER_F[0], DRAYNOR_MANOR_LEVER_F[1]);
      c.sleep(1280);
      c.walkTo(DRAYNOR_MANOR_DOOR_G[0], DRAYNOR_MANOR_DOOR_G[1]);
      c.sleep(2800);
      c.atWallObject(DRAYNOR_MANOR_DOOR_G[0], DRAYNOR_MANOR_DOOR_G[1]);
      c.sleep(2800);
      c.walkTo(DRAYNOR_MANOR_DOOR_C[0], DRAYNOR_MANOR_DOOR_C[1]);
      c.sleep(2800);
      c.atWallObject(DRAYNOR_MANOR_DOOR_C[0], DRAYNOR_MANOR_DOOR_C[1]);
      c.sleep(2800);

      // step five
      c.walkTo(DRAYNOR_MANOR_LEVER_C[0], DRAYNOR_MANOR_LEVER_C[1]);
      c.sleep(1280);
      c.atObject(DRAYNOR_MANOR_LEVER_C[0], DRAYNOR_MANOR_LEVER_C[1]);
      c.sleep(1280);
      c.walkTo(DRAYNOR_MANOR_DOOR_C[0] - 1, DRAYNOR_MANOR_DOOR_C[1]);
      c.sleep(2800);
      c.atWallObject(DRAYNOR_MANOR_DOOR_C[0], DRAYNOR_MANOR_DOOR_C[1]);
      c.sleep(2800);
      c.walkTo(DRAYNOR_MANOR_DOOR_G[0] - 1, DRAYNOR_MANOR_DOOR_G[1]);
      c.sleep(2800);
      c.atWallObject(DRAYNOR_MANOR_DOOR_G[0], DRAYNOR_MANOR_DOOR_G[1]);
      c.sleep(2800);

      // step six
      c.walkTo(DRAYNOR_MANOR_LEVER_E[0], DRAYNOR_MANOR_LEVER_E[1]);
      c.sleep(1280);
      c.atObject(DRAYNOR_MANOR_LEVER_E[0], DRAYNOR_MANOR_LEVER_E[1]);
      c.sleep(1280);
      c.walkTo(DRAYNOR_MANOR_DOOR_G[0], DRAYNOR_MANOR_DOOR_G[1]);
      c.sleep(2800);
      c.atWallObject(DRAYNOR_MANOR_DOOR_G[0], DRAYNOR_MANOR_DOOR_G[1]);
      c.sleep(2800);
      c.walkTo(DRAYNOR_MANOR_DOOR_D[0], DRAYNOR_MANOR_DOOR_D[1] - 1);
      c.sleep(2800);
      c.atWallObject(DRAYNOR_MANOR_DOOR_D[0], DRAYNOR_MANOR_DOOR_D[1]);
      c.sleep(2800);
      c.walkTo(DRAYNOR_MANOR_DOOR_E[0], DRAYNOR_MANOR_DOOR_E[1] - 1);
      c.sleep(2800);
      c.atWallObject(DRAYNOR_MANOR_DOOR_E[0], DRAYNOR_MANOR_DOOR_E[1]);
      c.sleep(2800);

      // grab can
      c.walkTo(227, 3382);
      c.sleep(640);
      c.atWallObject(DRAYNOR_MANOR_DOOR_I[0], DRAYNOR_MANOR_DOOR_I[1]);
      c.sleep(2580);
      while (c.isRunning() && c.getUnnotedInventoryItemCount(OIL_CAN_ID) < 1) {
        CURRENT_QUEST_STEP = "Retrieving can";
        int[] OIL_CAN_LOCATION = c.getNearestItemById(OIL_CAN_ID);
        if (OIL_CAN_LOCATION != null && c.getUnnotedInventoryItemCount(OIL_CAN_ID) == 0) {
          c.pickupItem(OIL_CAN_LOCATION[0], OIL_CAN_LOCATION[1], OIL_CAN_ID, true, true);
          c.sleep(640);
        }
      }
      c.walkTo(228, 3382);
      c.sleep(640);
      c.atWallObject(DRAYNOR_MANOR_DOOR_I[0], DRAYNOR_MANOR_DOOR_I[1]);
      c.sleep(2560);
      c.walkTo(223, 3384);
      c.sleep(640);
      c.atObject(DRAYNOR_MANOR_LADDER_UP_BASEMENT[0], DRAYNOR_MANOR_LADDER_UP_BASEMENT[1]);
      c.sleep(1280);
    }
  }

  public static void run() {

    // quest stages here:
    while (isQuesting()) {

      switch (QUEST_STAGE) {
        case 0:
          // Walk to Draynor is handled by isQuesting.
          // Before beginning we need to have 5 empty inventory slots
          // Poison -> Pressure Gauge (1)
          // Spade + Key + Rubber Tube (3)
          // Oil Can (1)
          CURRENT_QUEST_STEP = "Setting up the inventory."; // move to bank
          if (c.getInventoryItemCount() > 24) {
            c.openBank();
            while (c.isInBank()
                && c.getInventoryItemCount() > 24
                && c.isRunning()) { // deposit last item in inventory
              c.depositItem(c.getInventorySlotItemId(24)); // makes room for beads and coins
              c.sleep(1000);
            }
          }
          CURRENT_QUEST_STEP = "Moving to Draynor Manor."; // move to bank
          walkTowards(209, 561);
          c.sleep(640);
          followNPCDialog(VERONICA_ID, STEP_ONE_START_QUEST_DIALOG);
          sleepUntilQuestStageChanges();
          c.sleep(4000);

        case 1: //
          STEP_ITEMS =
              new int[][] {
                {ItemId.PRESSURE_GAUGE.getId(), 1},
                {ItemId.OIL_CAN.getId(), 1},
                {ItemId.RUBBER_TUBE.getId(), 1},
              };
          CURRENT_QUEST_STEP = "Finding Oddenstein.";
          // Find Oddenstein
          walkTowards(Location.DRAYNOR_MANOR_ENTRANCE);
          c.openDoor(DRAYNOR_MANOR_DOOR_ONE[0], DRAYNOR_MANOR_DOOR_ONE[1]);

          c.walkTo(210, 553); // directly infront of Manor entrance closed door
          c.sleep(2000);
          c.atWallObject(DRAYNOR_MANOR_DOOR_TWO[0], DRAYNOR_MANOR_DOOR_TWO[1]);
          c.sleep(3000);
          c.walkTo(210, 550); // directly infront of stairs up
          c.atObject(DRAYNOR_MANOR_STAIRS_UP[0], DRAYNOR_MANOR_STAIRS_UP[1]);
          c.sleep(1280);
          c.openDoor(DRAYNOR_MANOR_F2_DOOR_ONE[0], DRAYNOR_MANOR_F2_DOOR_ONE[1]);
          c.sleep(640);
          c.openDoor(DRAYNOR_MANOR_F2_DOOR_TWO[0], DRAYNOR_MANOR_F2_DOOR_TWO[1]);
          c.sleep(640);
          c.walkTo(212, 1497);
          c.sleep(640);
          while (c.isRunning() && c.getUnnotedInventoryItemCount(FISH_FOOD_ID) < 1) {
            int[] FISH_FOOD_LOCATION = c.getNearestItemById(FISH_FOOD_ID);
            if (FISH_FOOD_LOCATION != null && c.getUnnotedInventoryItemCount(FISH_FOOD_ID) == 0) {
              c.pickupItem(FISH_FOOD_LOCATION[0], FISH_FOOD_LOCATION[1], FISH_FOOD_ID, true, true);
              c.sleep(640);
            }
          }

          c.walkTo(215, 1491);
          c.sleep(640); // directly infront of ladder up
          c.atObject(DRAYNOR_MANOR_LADDER_UP[0], DRAYNOR_MANOR_LADDER_UP[1]);
          c.sleep(1280);
          c.openDoor(DRAYNOR_MANOR_F3_DOOR[0], DRAYNOR_MANOR_F3_DOOR[1]);
          c.sleep(640);
          followNPCDialog(ODDENSTEIN_ID, STEP_TWO_QUEST_DIALOG);
          sleepUntilQuestStageChanges();
          c.sleep(5000);

        case 2: //
          STEP_ITEMS =
              new int[][] {
                {ItemId.PRESSURE_GAUGE.getId(), 1},
                {ItemId.OIL_CAN.getId(), 1},
                {ItemId.RUBBER_TUBE.getId(), 1},
              };
          CURRENT_QUEST_STEP = "Finding quest items";
          c.walkTo(215, 2435); // directly beside ladder
          c.atObject(DRAYNOR_MANOR_LADDER_DOWN[0], DRAYNOR_MANOR_LADDER_DOWN[1]);
          c.sleep(1280);
          c.walkTo(210, 1490); // directly beside staircase entrance
          c.atObject(DRAYNOR_MANOR_STAIRS_DOWN[0], DRAYNOR_MANOR_STAIRS_DOWN[1]);
          c.sleep(1280);
          c.openDoor(DRAYNOR_MANOR_DOOR_THREE[0], DRAYNOR_MANOR_DOOR_THREE[1]);
          c.sleep(640);
          c.openDoor(DRAYNOR_MANOR_DOOR_FIVE[0], DRAYNOR_MANOR_DOOR_FIVE[1]);
          c.sleep(640);
          c.openDoor(DRAYNOR_MANOR_DOOR_SIX[0], DRAYNOR_MANOR_DOOR_SIX[1]);
          c.sleep(640);
          c.walkTo(221, 546); // poison

          while (c.isRunning() && c.getUnnotedInventoryItemCount(POISON_ID) < 1) {
            int[] FISH_FOOD_LOCATION = c.getNearestItemById(POISON_ID);
            if (FISH_FOOD_LOCATION != null && c.getUnnotedInventoryItemCount(POISON_ID) == 0) {
              c.pickupItem(FISH_FOOD_LOCATION[0], FISH_FOOD_LOCATION[1], POISON_ID, true, true);
              c.sleep(640);
            }
          }
          c.useItemOnItemBySlot(
              c.getInventoryItemSlotIndex(POISON_ID), c.getInventoryItemSlotIndex(FISH_FOOD_ID));
          c.sleep(640);
          c.walkTo(208, 544); // spade spawn
          c.walkTo(197, 554); // spade spawn

          while (c.isRunning() && c.getUnnotedInventoryItemCount(SPADE_ID) < 1) {
            int[] SPADE_LOCATION = c.getNearestItemById(SPADE_ID);
            if (SPADE_LOCATION != null && c.getUnnotedInventoryItemCount(SPADE_ID) == 0) {
              c.pickupItem(SPADE_LOCATION[0], SPADE_LOCATION[1], SPADE_ID, true, true);
              c.sleep(1280);
            }
          }

          c.walkTo(DRAYNOR_MANOR_DOOR_FOUR[0], DRAYNOR_MANOR_DOOR_FOUR[1]); // wall object door
          c.sleep(1280);
          c.atWallObject(
              DRAYNOR_MANOR_DOOR_FOUR[0], DRAYNOR_MANOR_DOOR_FOUR[1]); // should be outside now
          c.sleep(2400);
          c.walkTo(204, 539); // around back 1
          c.sleep(640);
          c.walkTo(215, 538); // around back 2
          c.sleep(640);
          c.walkTo(226, 546); // around back 3
          c.sleep(640);
          c.walkTo(229, 552); // beside compost
          c.sleep(640);
          c.useUnnotedItemIdOnObject(COMPOST_HEAP[0], COMPOST_HEAP[1], SPADE_ID);
          c.sleep(5000);
          c.dropItem(c.getInventoryItemSlotIndex(SPADE_ID));
          c.sleep(640);
          c.walkTo(227, 564); // beside fountain
          c.sleep(640);
          c.useUnnotedItemIdOnObject(FOUNTAIN[0], FOUNTAIN[1], POISON_FOOD_ID); // kill fish
          c.sleep(6000);
          while (c.isRunning() && c.getInventoryItemCount(PRESSURE_GAUGE_ID) == 0) {
            c.atObject(FOUNTAIN[0], FOUNTAIN[1]); // search fountain
            c.sleep(640);
          }
          c.sleep(4000);
          c.walkTo(222, 562);
          c.sleep(640);
          c.walkTo(216, 562);
          c.sleep(640);
          c.walkTo(210, 553); // directly infront of Manor entrance closed door
          c.sleep(2000);
          c.atWallObject(DRAYNOR_MANOR_DOOR_TWO[0], DRAYNOR_MANOR_DOOR_TWO[1]);
          c.sleep(3000);
          c.walkTo(DRAYNOR_MANOR_CLOSET[0], DRAYNOR_MANOR_CLOSET[1]);
          c.sleep(640);
          c.useItemOnWall(
              DRAYNOR_MANOR_CLOSET[0],
              DRAYNOR_MANOR_CLOSET[1],
              c.getInventoryItemSlotIndex(CLOSET_KEY_ID));
          c.sleep(2560);

          while (c.isRunning() && c.getUnnotedInventoryItemCount(RUBBER_TUBE_ID) < 1) {
            int[] RUBBER_TUBE_LOCATION = c.getNearestItemById(RUBBER_TUBE_ID);
            if (RUBBER_TUBE_LOCATION != null
                && c.getUnnotedInventoryItemCount(RUBBER_TUBE_ID) == 0) {
              c.pickupItem(
                  RUBBER_TUBE_LOCATION[0], RUBBER_TUBE_LOCATION[1], RUBBER_TUBE_ID, true, true);
              c.sleep(1280);
            }
          }
          c.walkTo(DRAYNOR_MANOR_CLOSET[0] - 1, DRAYNOR_MANOR_CLOSET[1]);
          c.sleep(640);
          c.useItemOnWall(
              DRAYNOR_MANOR_CLOSET[0],
              DRAYNOR_MANOR_CLOSET[1],
              c.getInventoryItemSlotIndex(CLOSET_KEY_ID));
          c.sleep(2560);
          c.dropItem(c.getInventoryItemSlotIndex(CLOSET_KEY_ID));
          c.sleep(640);
          c.openDoor(DRAYNOR_MANOR_DOOR_SEVEN[0], DRAYNOR_MANOR_DOOR_SEVEN[1]);
          c.sleep(640);
          c.walkTo(222, 553); // beside ladder
          c.atObject(DRAYNOR_MANOR_LADDER_DOWN_BASEMENT[0], DRAYNOR_MANOR_LADDER_DOWN_BASEMENT[1]);
          c.sleep(1280);

          puzzle(); // does the puzzle
          c.sleep(640);
          c.walkTo(212, 550);
          c.sleep(640);
          c.openDoor(DRAYNOR_MANOR_DOOR_SEVEN[0], DRAYNOR_MANOR_DOOR_SEVEN[1]);
          c.sleep(640);
          c.openDoor(DRAYNOR_MANOR_DOOR_THREE[0], DRAYNOR_MANOR_DOOR_THREE[1]);
          c.sleep(640);
          c.walkTo(210, 550); // directly infront of stairs up
          c.atObject(DRAYNOR_MANOR_STAIRS_UP[0], DRAYNOR_MANOR_STAIRS_UP[1]);
          c.sleep(1280);
          c.walkTo(215, 1491);
          c.sleep(640); // directly infront of ladder up
          c.atObject(DRAYNOR_MANOR_LADDER_UP[0], DRAYNOR_MANOR_LADDER_UP[1]);
          c.sleep(1280);
          c.openDoor(DRAYNOR_MANOR_F3_DOOR[0], DRAYNOR_MANOR_F3_DOOR[1]);
          c.sleep(640);
          talkToNpcId(ODDENSTEIN_ID);
          sleepUntilQuestStageChanges();
          break;
        default:
          quit(QuitReason.QUEST_STAGE_NOT_IN_SWITCH);
          break;
      }
    }
  }
}
