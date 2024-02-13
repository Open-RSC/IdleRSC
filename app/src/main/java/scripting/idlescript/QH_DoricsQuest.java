package scripting.idlescript;

import models.entities.ItemId;
import models.entities.NpcId;
import models.entities.SkillId;

public final class QH_DoricsQuest extends QH__QuestHandler {

  // COORDINATES FOR walkPath() PATHS AND pickupUnreachableItem()
  private static final int[] GENERAL_STORE = {316, 533};
  private static final int[] BANK_INNER = {328, 552};
  private static final int[] BANK_OUTER = {327, 552};
  private static final int[] FALADOR_NORTH_GATE = {315, 521};
  private static final int[] CROSSROAD = {312, 512};
  private static final int[] NORTHEAST_ROAD_ONE = {300, 509};
  private static final int[] NORTHEAST_ROAD_TWO = {281, 503};
  private static final int[] NORTHEAST_ROAD_THREE = {263, 488};
  private static final int[] TO_MINE_ONE = {263, 488};
  private static final int[] TO_MINE_TWO = {271, 480};
  private static final int[] MINE_BUILDING_OUTER = {280, 490};
  private static final int[] MINE_BUILDING_INNER = {280, 491};
  private static final int[] MINE_LADDER_OUTER = {280, 494};
  private static final int[] MINE_LADDER_INNER = {280, 3326};
  private static final int[] CLAY_TILE = {277, 3347};
  private static final int[] MINE_GENERAL_STORE = {291, 3339};
  private static final int[] NURMOFS_SHOP = {291, 3331};
  private static final int[] NORTHWEST_ROAD = {318, 500};
  private static final int[] DORICS_HOUSE_OUTER = {325, 493};
  private static final int[] DORICS_HOUSE_INNER = {325, 492};

  // PATHS FOR walkPath()
  private static final int[][] BANK_TO_CROSSROAD = {
    BANK_INNER, BANK_OUTER, GENERAL_STORE, FALADOR_NORTH_GATE, CROSSROAD
  };
  private static final int[][] CROSSROAD_TO_MINE = {
    NORTHEAST_ROAD_ONE,
    NORTHEAST_ROAD_TWO,
    NORTHEAST_ROAD_THREE,
    TO_MINE_ONE,
    TO_MINE_TWO,
    MINE_BUILDING_OUTER,
    MINE_BUILDING_INNER,
    MINE_LADDER_OUTER
  };
  private static final int[][] MINE_ENTRANCE_TO_MINE_SHOP = {MINE_LADDER_INNER, NURMOFS_SHOP};
  private static final int[][] MINE_SHOP_TO_ROCKS = {NURMOFS_SHOP, CLAY_TILE};
  private static final int[][] CROSSROAD_TO_DORICS_HOUSE = {
    CROSSROAD, NORTHWEST_ROAD, DORICS_HOUSE_OUTER, DORICS_HOUSE_INNER
  };

  // OBJECT COORDINATES
  private static final int[] CLAY_ROCK = {277, 3348};
  private static final int[] COPPER_ROCK = {276, 3341};
  private static final int[] IRON_ROCK = {274, 3340};
  private static final int[] MINE_LADDER_DOWN = {279, 494};
  private static final int[] MINE_LADDER_UP = {279, 3326};

  // ITEM IDS
  private static final int CLAY_ID = ItemId.CLAY.getId();
  private static final int COPPER_ID = ItemId.COPPER_ORE.getId();
  private static final int IRON_ID = ItemId.IRON_ORE.getId();
  private static final int PICKAXE_ID = ItemId.BRONZE_PICKAXE.getId();
  private static final int PIE_DISH_ID = ItemId.PIE_DISH.getId();

  // NPC IDS
  private static final int DORIC_ID = NpcId.DORIC.getId();
  private static final int NURMOF_ID = NpcId.NURMOF.getId();
  private static final int SHOPKEEPER_ID = NpcId.DWARVEN_SHOPKEEPER.getId();

  // NPC DIALOGS
  private static final String[] DORIC_START_QUEST_DIALOG = {
    "I wanted to use your anvils", "Yes I will get you materials"
  };

  public int start(String[] param) {
    QUEST_NAME = "Doric's Quest";
    START_RECTANGLE = FALADOR_WEST_BANK;
    QUEST_REQUIREMENTS = new String[] {};
    SKILL_REQUIREMENTS = new int[][] {{SkillId.MINING.getId(), 15}};
    ITEM_REQUIREMENTS = new int[][] {};
    EQUIP_REQUIREMENTS = new int[][] {};
    INVENTORY_SPACES_NEEDED = 13;
    TOTAL_QUEST_STAGES = 1;
    doQuestChecks();

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      QUEST_STAGE = c.getQuestStage(QUEST_ID);
      switch (QUEST_STAGE) {
        case 0:
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Walking to the Dwarven Mine";
            walkPath(BANK_TO_CROSSROAD);
            walkPath(CROSSROAD_TO_MINE);
            c.atObject(MINE_LADDER_DOWN[0], MINE_LADDER_DOWN[1]);
            while (c.currentY() < 3000 && c.isRunning()) c.sleep(640);
            c.sleep(1280);
          }

          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Buying a bronze pickaxe";
            walkPath(MINE_ENTRANCE_TO_MINE_SHOP);
            pickupGroundItem(PIE_DISH_ID, 1);
            walkPath(new int[][] {MINE_GENERAL_STORE});
            openShopThenSell(new int[] {SHOPKEEPER_ID}, new int[][] {{PIE_DISH_ID, 1}});
            walkPath(new int[][] {NURMOFS_SHOP});
            openShopThenBuy(new int[] {NURMOF_ID}, new int[][] {{PICKAXE_ID, 1}});
          }

          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Mining needed ores";
            STEP_ITEMS =
                new int[][] {
                  {CLAY_ID, 6},
                  {COPPER_ID, 4},
                  {IRON_ID, 2}
                };
            walkPath(MINE_SHOP_TO_ROCKS);
            while (!hasAtLeastItemAmount(CLAY_ID, 6) && c.isRunning()) {
              c.atObject(CLAY_ROCK[0], CLAY_ROCK[1]);
              c.sleep(640);
              c.waitForBatching(false);
            }
            dropAllButAmount(CLAY_ID, 6);

            while (!hasAtLeastItemAmount(COPPER_ID, 4) && c.isRunning()) {
              c.atObject(COPPER_ROCK[0], COPPER_ROCK[1]);
              c.sleep(640);
              c.waitForBatching(false);
            }
            dropAllButAmount(COPPER_ID, 4);

            while (!hasAtLeastItemAmount(IRON_ID, 2) && c.isRunning()) {
              c.atObject(IRON_ROCK[0], IRON_ROCK[1]);
              c.sleep(640);
              c.waitForBatching(false);
            }
            dropAllButAmount(IRON_ID, 2);
          }

          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Walking to Doric";
            c.atObject(MINE_LADDER_UP[0], MINE_LADDER_UP[1]);
            while (c.currentY() > 3000 && c.isRunning()) c.sleep(640);
            walkPathReverse(CROSSROAD_TO_MINE);
            walkPath(CROSSROAD_TO_DORICS_HOUSE);
          }

          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Talking to Doric";
            followNPCDialog(DORIC_ID, DORIC_START_QUEST_DIALOG);
            sleepUntilQuestStageChanges();
          }
          break;
        case 1:
          if (c.isRunning()) talkToNpcId(DORIC_ID);
          sleepUntilQuestStageChanges();
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
