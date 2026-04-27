package scripting.idlescript.SeattaScript.AIOQuester.quests;

import models.entities.ItemId;
import models.entities.NpcId;
import scripting.idlescript.SeattaScript.AIOQuester.QuestHandler;
import scripting.idlescript.SeattaScript.AIOQuester.models.QuitReason;

public final class CooksAssistant extends QuestHandler {
  // COORDINATES FOR walkPath() PATHS AND pickupUnreachableItem()
  private final int[] WHEAT_FIELD_OUTER = {172, 607};
  private final int[] WHEAT_FIELD_INNER = {172, 606};
  private final int[] COURTYARD_OUTER = {128, 658};
  private final int[] COURTYARD_INNER = {129, 658};
  private final int[] GENERAL_STORE = {130, 641};
  private final int[] CHICKEN_OUTER = {114, 609};
  private final int[] CHICKEN_INNER = {115, 609};
  private final int[] CABBAGE_FIELD = {151, 614};
  private final int[] BRIDGE_NORTH = {125, 625};
  private final int[] BRIDGE_SOUTH = {106, 656};
  private final int[] GOBLIN_ROAD = {106, 625};
  private final int[] MILL_FLOUR = {166, 598};
  private final int[] MILL_INNER = {166, 603};
  private final int[] MILL_OUTER = {166, 604};
  private final int[] COW_OUTER = {105, 619};
  private final int[] COW_INNER = {104, 619};
  private final int[] KITCHEN = {133, 660};
  private final int[] SHEEP = {135, 625};

  // PATHS FOR walkPath()
  private final int[][] MILL_TO_COURTYARD = {
    MILL_OUTER, CABBAGE_FIELD, SHEEP, GENERAL_STORE, COURTYARD_OUTER
  };
  private final int[][] COWS_TO_MILL = {COW_INNER, BRIDGE_NORTH, SHEEP, CABBAGE_FIELD, MILL_OUTER};
  private final int[][] MILL_TO_WHEAT_FIELD = {WHEAT_FIELD_OUTER, WHEAT_FIELD_INNER};
  private final int[][] KITCHEN_TO_COURTYARD = {COURTYARD_INNER, COURTYARD_OUTER};
  private final int[][] CHICKENS_TO_COWS = {CHICKEN_INNER, COW_OUTER, COW_INNER};
  private final int[][] PENS_TO_CHICKENS = {CHICKEN_OUTER, CHICKEN_INNER};
  private final int[][] COURTYARD_TO_KITCHEN = {COURTYARD_OUTER, KITCHEN};
  private final int[][] COURTYARD_TO_PENS = {BRIDGE_SOUTH, GOBLIN_ROAD};
  private final int[][] PENS_TO_COWS = {COW_OUTER, COW_INNER};
  private final int[][] INTO_MILL = {MILL_OUTER, MILL_INNER};

  // OBJECT COORDINATES
  private final int[] MILL_LADDER_DOWN_SECOND_FLOOR = {165, 1542};
  private final int[] MILL_LADDER_DOWN_THIRD_FLOOR = {166, 2490};
  private final int[] MILL_LADDER_UP_SECOND_FLOOR = {166, 1546};
  private final int[] MILL_LADDER_UP_GROUND_FLOOR = {165, 598};
  private final int[] MILL_HOPPER = {166, 2487};
  private final int[] WHEAT_PLANT = {172, 605};

  // ITEM IDS
  private final int POT_OF_FLOUR_ID = ItemId.POT_OF_FLOUR.getId();
  private final int BUCKET_ID = ItemId.BUCKET.getId();
  private final int GRAIN_ID = ItemId.GRAIN.getId();
  private final int FLOUR_ID = ItemId.FLOUR.getId();
  private final int MILK_ID = ItemId.MILK.getId();
  private final int EGG_ID = ItemId.EGG.getId();
  private final int POT_ID = ItemId.POT.getId();

  // NPC IDS
  private final int COW_ID = NpcId.COW_ATTACKABLE.getId();
  private final int COOK_ID = NpcId.COOK.getId();

  // NPC DIALOGS
  private final String[] COOK_START_QUEST_DIALOG = {"What's wrong?", "Yes, I'll help you"};

  public void run() {
    while (isQuesting()) {
      switch (QUEST_STAGE) {
        case 0:
          // Gather pot and bucket if needed
          while (((!hasAtLeastItemAmount(POT_ID, 1) && !hasAtLeastItemAmount(POT_OF_FLOUR_ID, 1))
                  || (!hasAtLeastItemAmount(BUCKET_ID, 1) && !hasAtLeastItemAmount(MILK_ID, 1)))
              && c.isRunning()) {
            STEP_ITEMS =
                new int[][] {
                  {POT_ID, 1},
                  {BUCKET_ID, 1}
                };
            if (!hasAtLeastItemAmount(POT_ID, 1) && !hasAtLeastItemAmount(POT_OF_FLOUR_ID, 1)) {
              CURRENT_QUEST_STEP = "Getting a pot";
              walkPath(COURTYARD_TO_KITCHEN);
              pickupUnreachableItem(POT_ID, KITCHEN, 1);
              walkPath(KITCHEN_TO_COURTYARD);
            }
            if (!hasAtLeastItemAmount(BUCKET_ID, 1) && !hasAtLeastItemAmount(MILK_ID, 1)) {
              CURRENT_QUEST_STEP = "Getting a bucket";
              walkPath(COURTYARD_TO_PENS);
              walkPath(PENS_TO_CHICKENS);
              pickupGroundItem(BUCKET_ID, 1);
            }
          }
          // Gather egg, milk, and flour
          while (!hasAtLeastItemAmount(EGG_ID, 1)
              || !hasAtLeastItemAmount(MILK_ID, 1)
              || !hasAtLeastItemAmount(POT_OF_FLOUR_ID, 1) && c.isRunning()) {
            STEP_ITEMS =
                new int[][] {
                  {EGG_ID, 1},
                  {MILK_ID, 1},
                  {POT_OF_FLOUR_ID, 1}
                };
            CURRENT_QUEST_STEP = "Getting ingredients";
            while (!hasAtLeastItemAmount(EGG_ID, 1) && c.isRunning()) {
              CURRENT_QUEST_STEP = "Getting an egg";
              if (isAtLocation(START_LOCATION)) {
                walkPath(COURTYARD_TO_PENS);
                walkPath(PENS_TO_CHICKENS);
              }
              pickupGroundItem(EGG_ID, 1);
            }
            while (!hasAtLeastItemAmount(MILK_ID, 1) && c.isRunning()) {
              CURRENT_QUEST_STEP = "Getting milk";
              if (isAtLocation(START_LOCATION)) {
                walkPath(COURTYARD_TO_PENS);
                walkPath(PENS_TO_COWS);
              } else {
                walkPath(CHICKENS_TO_COWS);
              }
              useItemOnNearestNpcId(COW_ID, BUCKET_ID);
            }
            while (!hasAtLeastItemAmount(POT_OF_FLOUR_ID, 1) && c.isRunning()) {
              CURRENT_QUEST_STEP = "Getting flour";
              if (isAtLocation(START_LOCATION)) {
                walkPathReverse(MILL_TO_COURTYARD);
              } else {
                walkPath(COWS_TO_MILL);
              }

              if (!hasAtLeastItemAmount(GRAIN_ID, 1)) {
                walkPath(MILL_TO_WHEAT_FIELD);
                c.atObject2(WHEAT_PLANT[0], WHEAT_PLANT[1]);
                while (!hasAtLeastItemAmount(GRAIN_ID, 1)) c.sleep(640);
                c.walkTo(c.currentX(), c.currentY());
                dropAllButAmount(GRAIN_ID, 1);
                walkPathReverse(MILL_TO_WHEAT_FIELD);
              }
              walkPath(INTO_MILL);
              int[][] objectSeq = {
                MILL_LADDER_UP_GROUND_FLOOR, MILL_LADDER_UP_SECOND_FLOOR,
              };
              atObjectSequence(objectSeq);
              c.useItemIdOnObject(MILL_HOPPER[0], MILL_HOPPER[1], GRAIN_ID);
              c.sleep(2560);
              objectSeq =
                  new int[][] {
                    MILL_HOPPER, MILL_LADDER_DOWN_THIRD_FLOOR, MILL_LADDER_DOWN_SECOND_FLOOR
                  };
              atObjectSequence(objectSeq);
              pickupUnreachableItem(FLOUR_ID, MILL_FLOUR, 1);
              walkPathReverse(INTO_MILL);
            }
          }
          CURRENT_QUEST_STEP = "Returning to the Cook";
          walkPath(MILL_TO_COURTYARD);
          walkPath(COURTYARD_TO_KITCHEN);
          followNPCDialog(COOK_ID, COOK_START_QUEST_DIALOG);
          sleepUntilQuestStageChanges();
          break;
        case 1:
          CURRENT_QUEST_STEP = "Handing in items to the Cook";
          talkToNpcId(COOK_ID);
          break;
        default:
          quit(QuitReason.QUEST_STAGE_NOT_IN_SWITCH);
          break;
      }
    }
  }
}
