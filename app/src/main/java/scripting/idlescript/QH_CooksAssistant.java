package scripting.idlescript;

import models.entities.ItemId;
import models.entities.NpcId;

public final class QH_CooksAssistant extends QH__QuestHandler {

  // COORDINATES FOR walkPath() PATHS AND pickupUnreachableItem()
  private static final int[] WHEAT_FIELD_OUTER = {172, 607};
  private static final int[] WHEAT_FIELD_INNER = {172, 606};
  private static final int[] COURTYARD_OUTER = {128, 658};
  private static final int[] COURTYARD_INNER = {129, 658};
  private static final int[] GENERAL_STORE = {130, 641};
  private static final int[] CHICKEN_OUTER = {114, 609};
  private static final int[] CHICKEN_INNER = {115, 609};
  private static final int[] CABBAGE_FIELD = {151, 614};
  private static final int[] BRIDGE_NORTH = {125, 625};
  private static final int[] BRIDGE_SOUTH = {106, 656};
  private static final int[] GOBLIN_ROAD = {106, 625};
  private static final int[] MILL_FLOUR = {166, 598};
  private static final int[] MILL_INNER = {166, 603};
  private static final int[] MILL_OUTER = {166, 604};
  private static final int[] COW_OUTER = {105, 619};
  private static final int[] COW_INNER = {104, 619};
  private static final int[] KITCHEN = {133, 660};
  private static final int[] SHEEP = {135, 625};

  // PATHS FOR walkPath()
  private static final int[][] MILL_TO_COURTYARD = {
    MILL_OUTER, CABBAGE_FIELD, SHEEP, GENERAL_STORE, COURTYARD_OUTER
  };
  private static final int[][] COWS_TO_MILL = {
    COW_INNER, BRIDGE_NORTH, SHEEP, CABBAGE_FIELD, MILL_OUTER
  };
  private static final int[][] MILL_TO_WHEAT_FIELD = {WHEAT_FIELD_OUTER, WHEAT_FIELD_INNER};
  private static final int[][] KITCHEN_TO_COURTYARD = {COURTYARD_INNER, COURTYARD_OUTER};
  private static final int[][] CHICKENS_TO_COWS = {CHICKEN_INNER, COW_OUTER, COW_INNER};
  private static final int[][] PENS_TO_CHICKENS = {CHICKEN_OUTER, CHICKEN_INNER};
  private static final int[][] COURTYARD_TO_KITCHEN = {COURTYARD_OUTER, KITCHEN};
  private static final int[][] COURTYARD_TO_PENS = {BRIDGE_SOUTH, GOBLIN_ROAD};
  private static final int[][] PENS_TO_COWS = {COW_OUTER, COW_INNER};
  private static final int[][] INTO_MILL = {MILL_OUTER, MILL_INNER};

  // OBJECT COORDINATES
  private static final int[] MILL_LADDER_DOWN_SECOND_FLOOR = {165, 1542};
  private static final int[] MILL_LADDER_DOWN_THIRD_FLOOR = {166, 2490};
  private static final int[] MILL_LADDER_UP_SECOND_FLOOR = {166, 1546};
  private static final int[] MILL_LADDER_UP_GROUND_FLOOR = {165, 598};
  private static final int[] MILL_HOPPER = {166, 2487};
  private static final int[] WHEAT_PLANT = {172, 605};

  // ITEM IDS
  private static final int POT_OF_FLOUR_ID = ItemId.POT_OF_FLOUR.getId();
  private static final int BUCKET_ID = ItemId.BUCKET.getId();
  private static final int GRAIN_ID = ItemId.GRAIN.getId();
  private static final int FLOUR_ID = ItemId.FLOUR.getId();
  private static final int MILK_ID = ItemId.MILK.getId();
  private static final int EGG_ID = ItemId.EGG.getId();
  private static final int POT_ID = ItemId.POT.getId();

  // NPC IDS
  private static final int COW_ID = NpcId.COW_ATTACKABLE.getId();
  private static final int COOK_ID = NpcId.COOK.getId();

  // NPC DIALOGS
  private static final String[] COOK_START_QUEST_DIALOG = {"What's wrong?", "Yes, I'll help you"};

  public int start(String[] param) {
    QUEST_NAME = "Cook's Assistant";
    START_RECTANGLE = LUMBRIDGE_CASTLE_COURTYARD;
    TOTAL_QUEST_STAGES = 1;
    QUEST_REQUIREMENTS = new String[] {};
    SKILL_REQUIREMENTS = new int[][] {};
    ITEM_REQUIREMENTS = new int[][] {};
    EQUIP_REQUIREMENTS = new int[][] {};
    INVENTORY_SPACES_NEEDED = 4;
    doQuestChecks();

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      QUEST_STAGE = c.getQuestStage(QUEST_ID);
      switch (QUEST_STAGE) {
        case 0:
          // Gather pot and bucket if needed
          while ((!hasAtLeastItemAmount(POT_ID, 1) && !hasAtLeastItemAmount(POT_OF_FLOUR_ID, 1))
              || (!hasAtLeastItemAmount(BUCKET_ID, 1) && !hasAtLeastItemAmount(MILK_ID, 1))
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
              if (isInRectangle(START_RECTANGLE)) {
                walkPath(COURTYARD_TO_PENS);
                walkPath(PENS_TO_CHICKENS);
              }
              pickupGroundItem(EGG_ID, 1);
            }
            while (!hasAtLeastItemAmount(MILK_ID, 1) && c.isRunning()) {
              CURRENT_QUEST_STEP = "Getting milk";
              if (isInRectangle(START_RECTANGLE)) {
                walkPath(COURTYARD_TO_PENS);
                walkPath(PENS_TO_COWS);
              } else {
                walkPath(CHICKENS_TO_COWS);
              }
              useItemOnNearestNpcId(COW_ID, BUCKET_ID);
            }
            while (!hasAtLeastItemAmount(POT_OF_FLOUR_ID, 1) && c.isRunning()) {
              CURRENT_QUEST_STEP = "Getting flour";
              if (isInRectangle(START_RECTANGLE)) {
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
        case -1:
          quit("Quest completed");
          break;
        default:
          quit("");
          break;
      }
    }
    quit("Script stopped");
    return 1000;
  }
}
