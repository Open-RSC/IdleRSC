package scripting.idlescript;

import models.entities.*;

public final class QH_ScorpionCatcher extends QH__QuestHandler {

  // MAP POINTS377,520
  private static final MapPoint TOWER_ENTRANCE = new MapPoint(511, 507);
  private static final MapPoint SEER_HOUSE = new MapPoint(521, 465);
  private static final MapPoint BARB_OUTPOST_POINT = new MapPoint(500, 547);
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
  private static final int SCORP_CAGE_STAGE_1 = ItemId.SCORPION_CAGE_NONE.getId();
  private static final int SCORP_CAGE_STAGE_2 = ItemId.SCORPION_CAGE_TWO.getId();
  private static final int SCORP_CAGE_STAGE_3 = ItemId.SCORPION_CAGE_TWO_THREE.getId();
  private static final int SCORP_CAGE_STAGE_4 = ItemId.SCORPION_CAGE_ONE_TWO_THREE.getId();
  private static final int BAR_CRAWL_CARD = ItemId.BARCRAWL_CARD.getId();
  // NPC IDS
  private static final int CPT_BARNABY = NpcId.CAPTAIN_BARNABY.getId();
  private static final int CST_OFFICIAL = NpcId.CUSTOMS_OFFICIAL.getId();
  private static final int THORMAC_ID = NpcId.THORMAC_THE_SORCEROR.getId();
  private static final int SEER_ID = NpcId.SEER.getId();
  private static final int MONK_ID = NpcId.ABBOT_LANGLEY.getId();
  private static final int BRIM_TENDER = NpcId.BARTENDER_BRIMHAVEN.getId();
  private static final int SEERS_TENDER = NpcId.BARTENDER_SEERS.getId();
  private static final int FALADOR_TENDER = NpcId.BARMAID.getId();
  private static final int SARIM_TENDER = NpcId.BARTENDER_PORTSARIM.getId();
  private static final int VARROCK_TENDER = NpcId.BARTENDER_VARROCK.getId();
  private static final int BOAR_TENDER = NpcId.BARTENDER_JOLLY_BOAR.getId();
  // NPC DIALOGS
  private static final String[] BOAT_TO_BRIM_DIALOG = {"Yes please"};
  private static final String[] GENERIC_TENDER_DIALOG = {"I'm doing Alfred Grimhand's barcrawl"};
  private static final String[] BOAT_TO_ARDY_DIALOG = {
    "Can I board this ship?", "Search away I have nothing to hide", "Ok"
  };
  private static final String[] THORMAC_START_QUEST_DIALOG = {
    "What do you need assistance with?",
    "So how would I go about catching them then?",
    "Ok I will do it then"
  };
  private static final String[] BARB_GUARD = {
    "I want to come through this gate", "Looks can be deceiving, I am in fact a barbarian"
  };
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

  public int start(String[] param) { // warning does not handle food conditions
    QUEST_NAME = "Scorpion catcher";
    if (c.getInventoryItemCount(SCORP_CAGE_STAGE_1) == 1) {
      START_RECTANGLE = BARB_OUTPOST;
    } else if (c.getInventoryItemCount(SCORP_CAGE_STAGE_2) == 1) {
      START_RECTANGLE = EDGE_MONASTERY;
    } else if (c.getInventoryItemCount(SCORP_CAGE_STAGE_3) == 1) {
      START_RECTANGLE = TAV_DUNGEON_LADDER;
    } else if (c.currentY() > 1200) {
      START_RECTANGLE = SORCERERS_TOWER_ABOVE;
    } else {
      START_RECTANGLE = SORCERERS_TOWER;
    }
    QUEST_REQUIREMENTS = new String[] {};
    SKILL_REQUIREMENTS = new int[][] {{SkillId.PRAYER.getId(), 31}, {SkillId.AGILITY.getId(), 70}};
    EQUIP_REQUIREMENTS = new int[][] {{ItemId.ANTI_DRAGON_BREATH_SHIELD.getId(), 1}};
    ITEM_REQUIREMENTS = new int[][] {{ItemId.COINS.getId(), 1000}};
    INVENTORY_SPACES_NEEDED = 5;
    TOTAL_QUEST_STAGES = 2;
    doQuestChecks();
    c.log("~ by Kaila", "mag");

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      QUEST_STAGE = c.getQuestStage(QUEST_ID);
      switch (QUEST_STAGE) {
        case 0: // not started
          // get anti
          if (c.isRunning()
              && c.getInventoryItemCount(ItemId.CURE_POISON_POTION_1DOSE.getId()) == 0) {
            CURRENT_QUEST_STEP = "Getting cure poison potion";
            if (isInRectangle(SORCERERS_TOWER_ABOVE)) climb(510, 1451);
            c.walkTo(511, 513); // outside tower
            pathWalker(new MapPoint(631, 634)); // battlefield
            pathWalker(new MapPoint(691, 678)); // goblins
            c.walkTo(698, 687); // at anti-poison
            pickupGroundItem(ItemId.CURE_POISON_POTION_1DOSE.getId(), 1);
            c.walkTo(691, 678); // outside gobs
            pathWalker(new MapPoint(631, 634)); // battlefield
          }
          if (c.isRunning() && !isInRectangle(SORCERERS_TOWER_ABOVE)) {
            CURRENT_QUEST_STEP = "Walking to Thormac";
            pathWalker(TOWER_ENTRANCE);
            climb(TOWER_LADDER_UP);
          }
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
              pathWalker(BARB_OUTPOST_POINT);
              //              c.walkTo(494, 543); //?
              c.walkTo(494, 544);
              CURRENT_QUEST_STEP = "Entering Barb Gate";
              c.atObject(BARB_OUTPOST_DOOR[0], BARB_OUTPOST_DOOR[1]);
              c.sleep(3000);
              if (c.currentX() > 493) {
                CURRENT_QUEST_STEP = "Doing Barcrawl";
                followNPCDialogPopUps(305, BARB_GUARD);
                c.sleep(3000);
              }
            }
            QH_BarCrawl barCrawl = new QH_BarCrawl();
            barCrawl.doBarCrawl();

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
                atObject(BARB_OUTPOST_DOOR);
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
              CURRENT_QUEST_STEP = "Going up ladder";
              pathWalker(MONESTARY_LADDER);
              c.atObject(MONESTARY_LADDER_UP[0], MONESTARY_LADDER_UP[1]);
              c.sleep(6000);
              if (c.isInOptionMenu()) {
                CURRENT_QUEST_STEP = "Joining guild";
                followNPCDialogPopUps(MONK_ID, MONK_DIALOG);
                //              } else {
                //                //c.walkTo(251, 1411);
                //                climb(MONESTARY_LADDER_UP);
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
              atObject(374, 3352);
              c.walkTo(376, 3368);
              pathWalker(383, 3352);
              atWallObject(383, 3353);
              useItemOnNearestNpcId(302, SCORP_CAGE_STAGE_3);
            }
            if (c.isRunning() && c.currentY() > 1000) { // tav dungeon
              CURRENT_QUEST_STEP = "Returning to Surface";
              c.walkTo(383, 3353);
              atWallObject(383, 3353);
              c.walkTo(375, 3365);
              c.walkTo(372, 3352);
              atObject(373, 3352);
              climb(TAV_LADDER_UP);
              c.walkTo(374, 513);
            }
          } else if (c.getInventoryItemCount(SCORP_CAGE_STAGE_4) == 1) {
            if (c.isRunning() && !isInRectangle(SORCERERS_TOWER_ABOVE)) {
              CURRENT_QUEST_STEP = "Walking to Tower";
              if (timeToDrinkAntidote) drinkAnti();
              pathWalker(TOWER_ENTRANCE);
              if (timeToDrinkAntidote) drinkAnti();
              climb(TOWER_LADDER_UP);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Talking to Thormac, Finishing Quest";
              if (timeToDrinkAntidote) drinkAnti();
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
