package scripting.idlescript;

import models.entities.ItemId;
import models.entities.NpcId;

public final class QH_SheepShearer extends QH__QuestHandler {

  // COORDINATES FOR walkPath() PATHS AND pickupUnreachableItem()
  private static final int[] GENERAL_STORE_OUTER = {131, 641};
  private static final int[] GENERAL_STORE_INNER = {133, 641};
  private static final int[] FRED_HOUSE_OUTER = {159, 616};
  private static final int[] FRED_HOUSE_INNER = {159, 617};
  private static final int[] FRED_GATE_OUTER = {159, 614};
  private static final int[] FRED_GATE_INNER = {159, 615};
  private static final int[] COURTYARD_OUTER = {128, 658};
  private static final int[] COURTYARD_INNER = {129, 658};
  private static final int[] SHEEP_PEN_OUTER = {135, 630};
  private static final int[] SHEEP_PEN_INNER = {136, 630};
  private static final int[] ROAD_CABBAGES = {144, 616};
  private static final int[] ROAD_FURNACE = {134, 626};
  private static final int[] ROAD_CORNER = {135, 617};
  private static final int[] LADDER = {139, 667};

  // PATHS FOR walkPath()
  final int[][] COURTYARD_TO_FRED = {
    COURTYARD_OUTER,
    GENERAL_STORE_OUTER,
    ROAD_FURNACE,
    ROAD_CORNER,
    ROAD_CABBAGES,
    FRED_GATE_OUTER,
    FRED_GATE_INNER,
    FRED_HOUSE_OUTER,
    FRED_HOUSE_INNER
  };
  private static final int[][] GENERAL_STORE_TO_SHEEP = {
    GENERAL_STORE_INNER, GENERAL_STORE_OUTER, SHEEP_PEN_OUTER, SHEEP_PEN_INNER
  };
  private static final int[][] COURTYARD_TO_SHEEP = {
    COURTYARD_OUTER, GENERAL_STORE_OUTER, SHEEP_PEN_OUTER, SHEEP_PEN_INNER
  };
  private static final int[][] COURTYARD_TO_GENERAL_STORE = {
    COURTYARD_OUTER, GENERAL_STORE_OUTER, GENERAL_STORE_INNER
  };
  private static final int[][] COURTYARD_TO_LADDER = {COURTYARD_OUTER, COURTYARD_INNER, LADDER};

  // OBJECT COORDINATES
  private static final int[] LADDER_UP_GROUND_FLOOR = {139, 666};
  private static final int[] LADDER_UP_SECOND_FLOOR = {138, 1612};
  private static final int[] LADDER_DOWN_THIRD_FLOOR = {138, 2556};
  private static final int[] LADDER_DOWN_SECOND_FLOOR = {139, 1610};
  private static final int[] SPINNING_WHEEL = {139, 2554};

  // OBJECT SEQUENCES
  private static final int[][] UP_LADDER = {LADDER_UP_GROUND_FLOOR, LADDER_UP_SECOND_FLOOR};
  private static final int[][] DOWN_LADDER = {LADDER_DOWN_THIRD_FLOOR, LADDER_DOWN_SECOND_FLOOR};

  // ITEM IDS
  private static final int WOOL_ID = ItemId.WOOL.getId();
  private static final int BALL_OF_WOOL_ID = ItemId.BALL_OF_WOOL.getId();
  private static final int SHEARS_ID = ItemId.SHEARS.getId();

  // NPC IDS
  private static final int SHEEP_ID = NpcId.SHEEP.getId();
  private static final int FRED_ID = NpcId.FRED_THE_FARMER.getId();

  // NPC DIALOGS
  private static final String[] FRED_START_QUEST_DIALOG = {
    "I'm looking for a quest", "Yes okay. I can do that"
  };

  public int start(String[] param) {
    QUEST_NAME = "Sheep Shearer";
    START_RECTANGLE = LUMBRIDGE_CASTLE_COURTYARD;
    QUEST_REQUIREMENTS = new String[] {};
    SKILL_REQUIREMENTS = new int[][] {};
    ITEM_REQUIREMENTS = new int[][] {};
    EQUIP_REQUIREMENTS = new int[][] {};
    INVENTORY_SPACES_NEEDED = 21;
    TOTAL_QUEST_STAGES = 1;
    doQuestChecks();

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      QUEST_STAGE = c.getQuestStage(QUEST_ID);
      switch (QUEST_STAGE) {
        case 0:
          STEP_ITEMS = new int[][] {{SHEARS_ID, 1}};
          // Get shears
          if (!hasAtLeastItemAmount(SHEARS_ID, 1) && c.isRunning()) {
            CURRENT_QUEST_STEP = "Getting shears";

            int MIND_RUNE_ID = ItemId.MIND_RUNE.getId();
            int[] MIND_RUNE_TILE = {138, 667};

            walkPath(COURTYARD_TO_LADDER);
            pickupUnreachableItem(MIND_RUNE_ID, MIND_RUNE_TILE, 1);
            walkPathReverse(COURTYARD_TO_LADDER);
            walkPath(COURTYARD_TO_GENERAL_STORE);
            openShopThenSellAndBuy(
                new int[] {55, 83}, new int[][] {{MIND_RUNE_ID, 1}}, new int[][] {{SHEARS_ID, 1}});
            walkPath(GENERAL_STORE_TO_SHEEP);
          } else {
            CURRENT_QUEST_STEP = "Walking to sheep";
            walkPath(COURTYARD_TO_SHEEP);
          }

          // Get wool
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Shearing sheep for wool";
            STEP_ITEMS = new int[][] {{WOOL_ID, 20}};
            while (!hasAtLeastItemAmount(WOOL_ID, 20) && c.isRunning()) {
              useItemOnNearestNpcId(SHEEP_ID, SHEARS_ID);
              c.sleep(640);
              c.waitForBatching(false);
            }
            dropAllButAmount(WOOL_ID, 20);
          }

          // Spin wool
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Spinning wool";
            STEP_ITEMS = new int[][] {{BALL_OF_WOOL_ID, 20}};
            walkPath(COURTYARD_TO_LADDER);
            atObjectSequence(UP_LADDER);
            while (hasAtLeastItemAmount(WOOL_ID, 1) && c.isRunning()) {
              c.useItemIdOnObject(SPINNING_WHEEL[0], SPINNING_WHEEL[1], WOOL_ID);
              c.sleep(1280);
              c.waitForBatching(false);
            }
            CURRENT_QUEST_STEP = "Taking balls of wool to Fred";
            atObjectSequence(DOWN_LADDER);
            walkPathReverse(COURTYARD_TO_LADDER);
          }

          // Turn in balls of wool
          if (c.isRunning()) {
            walkPath(COURTYARD_TO_FRED);
            followNPCDialog(FRED_ID, FRED_START_QUEST_DIALOG);
            sleepUntilQuestStageChanges();
          }
          break;
        case 1:
          if (c.isRunning()) talkToNpcId(FRED_ID);
          c.sleep(640);
          break;
        case -1:
          quit("quest completed");
          break;
        default:
          quit("");
          break;
      }
    }
    quit("script stopped");
    return 1000;
  }
}
