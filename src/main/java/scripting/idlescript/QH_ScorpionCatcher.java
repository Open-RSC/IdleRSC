package scripting.idlescript;

import models.entities.ItemId;
import models.entities.MapPoint;
import models.entities.NpcId;
import models.entities.SkillId;

public final class QH_ScorpionCatcher extends QH__QuestHandler {

  // MAP POINTS377,520
  private static final MapPoint TOWER_ENTRANCE = new MapPoint(511, 507);
  private static final MapPoint SEER_HOUSE = new MapPoint(521, 465);
  private static final MapPoint BARB_OUTPOST = new MapPoint(494, 543);
  private static final MapPoint MONESTARY = new MapPoint(256, 469);
  private static final MapPoint MONESTARY_LADDER = new MapPoint(251, 467);
  private static final MapPoint PRAYER_ALTER = new MapPoint(262, 460);
  private static final MapPoint TAV_LADDER_ABOVE = new MapPoint(377, 520);
  private static final MapPoint TAV_LADDER_UNDER = new MapPoint(376, 3351);
  private static final MapPoint TAV_DUSTY_WAYPOINT = new MapPoint(376, 3351);
  // OBJECT COORDINATES
  private static final int[] TOWER_LADDER_UP = {510, 507};
  private static final int[] TOWER_LADDER_DOWN = {510, 1451};
  private static final int[] BARB_OUTPOST_DOOR = {494, 543};
  private static final int[] PRAY_ALTER = {261, 459};
  private static final int[] MONESTARY_LADDER_UP = {251, 468};
  private static final int[] MONESTARY_LADDER_DOWN = {251, 1412};
  private static final int[] TAV_LADDER_UP = {376, 3352};
  private static final int[] TAV_LADDER_DOWN = {376, 520};
  // ITEM IDS

  /*
   *  DO IN THIS ORDER
   *   second scorp is barb outpost
   *   third scorp is monestary
   *   first scorp is tavelry
   *
   *
   */
  private static final int SCORP_CAGE_STAGE_1 = ItemId.SCORPION_CAGE_NONE.getId();
  private static final int SCORP_CAGE_STAGE_2 = ItemId.SCORPION_CAGE_TWO.getId();
  private static final int SCORP_CAGE_STAGE_3 = ItemId.SCORPION_CAGE_TWO_THREE.getId();
  private static final int SCORP_CAGE_STAGE_4 = ItemId.SCORPION_CAGE_ONE_TWO_THREE.getId();

  // NPC IDS
  private static final int THORMAC_ID = NpcId.THORMAC_THE_SORCEROR.getId();
  private static final int SEER_ID = NpcId.SEER.getId();
  private static final int MONK_ID = NpcId.ABBOT_LANGLEY.getId();
  // NPC DIALOGS
  private static final String[] THORMAC_START_QUEST_DIALOG = {
    "What do you need assistance with?",
    "So how would I go about catching them then?",
    "Ok I will do it then"
  };
  private static final String[] THORMAC_END_QUEST_DIALOG = {""};
  private static final String[] SEER_QUEST_DIALOG = {
    "I need to locate some scorpions",
  };
  private static final String[] MONK_DIALOG = {"Well can i join your order?"};
  private static final int[][] BARB_OUTPOST_REC = {{485, 541}, {493, 555}};

  private void drinkAnti() {
    timeToDrinkAntidote = false;
    if (K_kailaScript.drinkAnti(true)) {
      c.stop();
      c.logout();
    }
  }
  /*
   * Case 0 - Not started (no cage)
   * Case 1 - Talk to seer
   * Case 2 - Gathering scorpions and completing
   *
   */
  // todo check for barcrawl completion!!!
  public int start(String[] param) { // warning does not handle food conditions
    QUEST_NAME = "Scorpion catcher";
    if (c.getInventoryItemCount(SCORP_CAGE_STAGE_2) == 1) {
      START_RECTANGLE = EDGE_MONESTARY;
    } else if (c.getInventoryItemCount(SCORP_CAGE_STAGE_3) == 1) {
      START_RECTANGLE = TAV_DUNGEON_LADDER;
    } else {
      START_RECTANGLE = SORCERORS_TOWER;
    }
    QUEST_REQUIREMENTS = new String[] {};
    SKILL_REQUIREMENTS = new int[][] {{SkillId.PRAYER.getId(), 31}, {SkillId.AGILITY.getId(), 70}};
    EQUIP_REQUIREMENTS = new int[][] {{ItemId.ANTI_DRAGON_BREATH_SHIELD.getId(), 1}};
    ITEM_REQUIREMENTS = new int[][] {};
    INVENTORY_SPACES_NEEDED = 5;
    doQuestChecks();
    c.log("~ by Kaila", "mag");
    c.log("This quest requires Barcrawl Completion beforehand", "red");
    c.log("STOP NOW IF YOU HAVE NOW COMPLETED IT", "red");

    while (c.isRunning()) {
      QUEST_STAGE = c.getQuestStage(QUEST_ID);
      switch (QUEST_STAGE) {
        case 0: // not started
          // get anti
          if (c.isRunning()
              && c.getInventoryItemCount(ItemId.CURE_POISON_POTION_1DOSE.getId()) == 0) {
            CURRENT_QUEST_STEP = "Getting cure poison potion";
            c.walkTo(511, 513); // outside tower
            pathWalker(new MapPoint(631, 634)); // battlefield
            pathWalker(new MapPoint(691, 678)); // goblins
            c.walkTo(698, 687); // at anti-poison
            pickupGroundItem(ItemId.CURE_POISON_POTION_1DOSE.getId(), 1);
            c.walkTo(691, 678); // outside gobs
            pathWalker(new MapPoint(631, 634)); // battlefield
          }
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Walking to Thormac";
            pathWalker(TOWER_ENTRANCE);
            climb(TOWER_LADDER_UP);
          }
          //          if (c.isRunning()) {
          //            CURRENT_QUEST_STEP = "Buying a bronze pickaxe";
          //            // walkPath(new int[][] {MINE_LADDER_INNER});
          //            pathWalker(MINES_NURMOFS_SHOP);
          //            pickupGroundItem(PIE_DISH_ID, 1);
          //            pathWalker(MINES_GENERAL_STORE);
          //            openShopThenSell(new int[] {SHOPKEEPER_ID}, new int[][] {{PIE_DISH_ID, 1}});
          //            pathWalker(MINES_NURMOFS_SHOP);
          //            openShopThenBuy(new int[] {NURMOF_ID}, new int[][] {{PICKAXE_ID, 1}});
          //          }

          //          if (c.isRunning()) {
          //            CURRENT_QUEST_STEP = "Mining needed ores";
          //            STEP_ITEMS =
          //                new int[][] {
          //                  {CLAY_ID, 6},
          //                  {COPPER_ID, 4},
          //                  {IRON_ID, 2}
          //                };
          //            pathWalker(DWARVEN_MINE_ORE);
          //            while (!hasAtLeastItemAmount(CLAY_ID, 6) && c.isRunning()) {
          //              c.atObject(CLAY_ROCK[0], CLAY_ROCK[1]);
          //              c.sleep(640);
          //              while (c.isBatching() && c.isRunning()) c.sleep(640);
          //            }
          //            dropAllButAmount(CLAY_ID, 6);
          //          }
          //          if (c.isRunning()) {
          //            CURRENT_QUEST_STEP = "Walking to Doric";
          //            pathWalker(DWARVEN_MINE_EXIT);
          //            climb(MINE_LADDER_UP);
          //            pathWalker(DORICS_HOUSE);
          //
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Talking to Thormac";
            followNPCDialog(THORMAC_ID, THORMAC_START_QUEST_DIALOG);
            sleepUntilQuestStageChanges();
          }
          break;
        case 1: // talking to seer
          if (c.getInventoryItemCount(SCORP_CAGE_STAGE_1) == 1) {
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Walking to Seer";
              climb(TOWER_LADDER_DOWN);
              c.walkTo(511, 513); // prevent pathwalker breaking inside houses?
              pathWalker(SEER_HOUSE);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Talking to Seer";
              followNPCDialog(SEER_ID, SEER_QUEST_DIALOG);
              sleepUntilQuestStageChanges();
              c.walkTo(521, 458);
            }
          }
          break;
        case 2: // scorpion time
          if (c.getInventoryItemCount(SCORP_CAGE_STAGE_1) == 1) {
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Walking to Barb Outpost Scorpion";
              pathWalker(BARB_OUTPOST);
              atObject(BARB_OUTPOST_DOOR, 1);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Getting Barb Outpost Scorpion";
              c.walkTo(489, 547);
              c.sleep(640);
              useItemOnNearestNpcId(303, 678);
            }
            //   DO IN THIS ORDER barb outpost, monestary, tavelry
          } else if (c.getInventoryItemCount(SCORP_CAGE_STAGE_2) == 1) {
            if (c.isRunning()) {
              if (isInRectangle(BARB_OUTPOST_REC)) {
                atObject(BARB_OUTPOST_DOOR, 1);
              }
              CURRENT_QUEST_STEP = "Walking to Monestery Scorpion";
              pathWalker(MONESTARY);
            }
            if (c.isRunning() && c.getCurrentStat(SkillId.PRAYER.getId()) < 31) {
              CURRENT_QUEST_STEP = "Refilling Prayer Points";
              pathWalker(PRAYER_ALTER);
              while (c.isCurrentlyWalking()) c.sleep(640);
              c.atObject(PRAY_ALTER[0], PRAY_ALTER[1]);
              c.sleep(1280);
            }
            if (c.isRunning() && c.getCurrentStat(SkillId.PRAYER.getId()) >= 31) {
              CURRENT_QUEST_STEP = "Going up ladder, joining guild";
              pathWalker(MONESTARY_LADDER);
              c.atObject(MONESTARY_LADDER_UP[0], MONESTARY_LADDER_UP[1]);
              c.sleep(6000);
              if (c.isInOptionMenu()) {
                followNPCDialogPopUps(MONK_ID, MONK_DIALOG);
              } else {
                c.walkTo(251, 1411);
                climb(MONESTARY_LADDER_UP);
              }
            }
            if (c.isRunning()
                && c.currentY() > 1000
                && c.getCurrentStat(SkillId.PRAYER.getId()) >= 31) {
              CURRENT_QUEST_STEP = "Catching second scorpion";
              useItemOnNearestNpcId(304, SCORP_CAGE_STAGE_2);
              climb(MONESTARY_LADDER_DOWN);
            }
          } else if (c.getInventoryItemCount(SCORP_CAGE_STAGE_3) == 1) {
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Going Down Ladder";
              pathWalker(TAV_LADDER_ABOVE);
              climb(TAV_LADDER_DOWN);
            }
            if (c.isRunning() && c.currentY() > 1000) { // tav dungeon
              // c.walkTo(375,3352);
              CURRENT_QUEST_STEP = "Going through Pipe, walking to Scorpion";
              atObject(new int[] {374, 3352}, 1);
              c.walkTo(376, 3368);
              pathWalker(new MapPoint(383, 3352));
              atWallObject(new int[] {383, 3353}, 1);
              useItemOnNearestNpcId(302, SCORP_CAGE_STAGE_3);
            }
            if (c.isRunning() && c.currentY() > 1000) { // tav dungeon
              CURRENT_QUEST_STEP = "Returning to Surface";
              c.walkTo(383, 3353);
              atWallObject(new int[] {383, 3353}, 1);
              c.walkTo(375, 3365);
              c.walkTo(372, 3352);
              atObject(new int[] {373, 3352}, 1);
              climb(TAV_LADDER_UP);
              c.walkTo(374, 513);
            }
          } else if (c.getInventoryItemCount(SCORP_CAGE_STAGE_4) == 1) {
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Walking to Tower";
              if (timeToDrinkAntidote) drinkAnti();
              pathWalker(TOWER_ENTRANCE);
              if (timeToDrinkAntidote) drinkAnti();
              climb(TOWER_LADDER_UP);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Talking to Thormac, Finishing Quest";
              talkToNpcId(THORMAC_ID);
              sleepUntilQuestStageChanges();
            }
          }
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
