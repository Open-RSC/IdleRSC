package scripting.idlescript;

import models.entities.ItemId;
import models.entities.NpcId;
import models.entities.SkillId;

public final class QH_FishingContest extends QH__QuestHandler {
  private static final int[] OUTSIDE_HUT_WAYPOINT = {433, 460};
  private static final int[] CUPBOARD_COORDS = {216, 1562};
  private static final int GIANT_CARP = ItemId.RAW_GIANT_CARP.getId();
  private static final int FISHING_ROD = ItemId.FISHING_ROD.getId();
  private static final int BAIT = ItemId.FISHING_BAIT.getId();
  private static final int GARLIC = ItemId.GARLIC.getId();
  private static final int SPADE = ItemId.SPADE.getId();
  private static final int PASS = ItemId.FISHING_COMPETITION_PASS.getId();
  private static final int RED_VINE_WORMS = ItemId.RED_VINE_WORMS.getId();
  private static final int MOUNTAIN_DWARF = NpcId.MOUNTAIN_DWARF.getId();
  private static final int HARRY = NpcId.HARRY.getId();
  private static final int BONZO = NpcId.BONZO.getId();
  private static final String[] MOUNTAIN_DWARF_START_QUEST_DIALOG = {
    "I was wondering what was down those stairs?",
    "Why not?",
    "If you were my friend I wouldn't mind it",
    "Well lets be friends",
    "And how am I meant to do that?",
    "Fortunately I'm alright at fishing"
  };
  private static final String[] BONZO_START_DIALOG = {"I'll give that a go then"};
  private static final String[] BONZO_END_DIALOG = {"I have this big fish,is it enough to win?"};

  public int start(String[] param) { // warning does not handle food conditions
    QUEST_NAME = "Fishing contest";
    START_RECTANGLE = WEST_DWARF_TUNNEL;
    QUEST_REQUIREMENTS = new String[] {};
    SKILL_REQUIREMENTS = new int[][] {{SkillId.FISHING.getId(), 10}};
    EQUIP_REQUIREMENTS = new int[][] {};
    ITEM_REQUIREMENTS = new int[][] {{ItemId.COINS.getId(), 1000}};
    INVENTORY_SPACES_NEEDED = 10;
    TOTAL_QUEST_STAGES = 3;
    doQuestChecks();
    c.log("WARNING: Entering dangerous areas, low combat not recommended", "red");
    c.log("~ by Kaila", "mag");

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      QUEST_STAGE = c.getQuestStage(QUEST_ID);
      switch (QUEST_STAGE) { // 6 stages in total
        case 0: // not started
          CURRENT_QUEST_STEP = "Getting ingredients";
          // GET FISHING ROD AND BAIT
          if (c.isRunning()
              && (c.getInventoryItemCount(FISHING_ROD) == 0
                  || c.getInventoryItemCount(BAIT) < 50)) {
            walkPath(new int[][] {OUTSIDE_HUT_WAYPOINT});
            CURRENT_QUEST_STEP = "Walking to Fish Shop";
            pathWalker(418, 486);
            CURRENT_QUEST_STEP = "Buying rod and bait";
            openShopThenBuy(
                new int[] {HARRY},
                new int[][] {
                  {FISHING_ROD, 1 - c.getInventoryItemCount(FISHING_ROD)},
                  {BAIT, 50 - c.getInventoryItemCount(BAIT)}
                });
            CURRENT_QUEST_STEP = "Walking Outside";
            c.walkTo(418, 492);
          }
          // GET SPADE
          if (c.isRunning() && c.getInventoryItemCount(SPADE) == 0) {
            CURRENT_QUEST_STEP = "Walking to Falador Spade";
            pathWalker(291, 524); // 295,525
            CURRENT_QUEST_STEP = "Getting Spade";
            // c.walkTo(291,524);
            pickupGroundItem(SPADE, 1);
            CURRENT_QUEST_STEP = "Walking Outside";
            c.walkTo(295, 524);
          }
          // GET GARLIC
          if (c.isRunning() && c.getInventoryItemCount(GARLIC) == 0) {
            CURRENT_QUEST_STEP = "Walking to Garlic";
            pathWalker(216, 619);
            climb(216, 620);
            while (c.isRunning() && c.getInventoryItemCount(GARLIC) == 0) {
              if (c.getObjectAtCoord(CUPBOARD_COORDS[0], CUPBOARD_COORDS[1]) == 140) {
                CURRENT_QUEST_STEP = "Opening Cupboard";
                c.atObject(CUPBOARD_COORDS[0], CUPBOARD_COORDS[1]);
                c.sleep(2000);
              }
              CURRENT_QUEST_STEP = "Grabbing Garlic";
              c.atObject(CUPBOARD_COORDS[0], CUPBOARD_COORDS[1]);
              c.sleep(2000);
            }
            CURRENT_QUEST_STEP = "Walking Outside";
            climb(216, 1564);
            c.walkTo(212, 605);
          }
          if (c.isRunning()
              && c.getInventoryItemCount(FISHING_ROD) > 0
              && c.getInventoryItemCount(BAIT) >= 40
              && c.getInventoryItemCount(SPADE) > 0
              && c.getInventoryItemCount(GARLIC) > 0) {

            if (c.isRunning() && !isInRectangle(START_RECTANGLE)) {
              CURRENT_QUEST_STEP = "Walking back to Dwarf";
              pathWalker(OUTSIDE_HUT_WAYPOINT);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Starting Quest";
              followNPCDialog(MOUNTAIN_DWARF, MOUNTAIN_DWARF_START_QUEST_DIALOG);
              sleepUntilQuestStageChanges();
              CURRENT_QUEST_STEP = "Walking Outside";
              walkPath(new int[][] {OUTSIDE_HUT_WAYPOINT});
            }
          }
          break;
        case 1:
          // Get red vine worms
          if (c.getInventoryItemCount(PASS) > 0 && c.getInventoryItemCount(RED_VINE_WORMS) == 0) {
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Walking to McGrubor";
              pathWalker(539, 445); // walk to mc grubbor
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Entering McGrubor";
              atWallObject(540, 445);
              CURRENT_QUEST_STEP = "Walking to Vines";
              c.walkTo(550, 453);
              c.walkTo(568, 453);
              CURRENT_QUEST_STEP = "Grabbing Red Vine Worms";
              while (c.isRunning() && c.getInventoryItemCount(RED_VINE_WORMS) == 0) {
                c.useItemIdOnObject(568, 453, SPADE);
                c.sleep(4000);
              }
              CURRENT_QUEST_STEP = "Leaving McGrubor";
              c.walkTo(550, 453);
              c.walkTo(540, 445);
              atWallObject(540, 445);
              CURRENT_QUEST_STEP = "Walking Out";
              c.walkTo(533, 451);
            }
          }
          // Start Contest
          if (c.getInventoryItemCount(PASS) > 0
              && c.getInventoryItemCount(RED_VINE_WORMS) > 0
              && c.getInventoryItemCount(FISHING_ROD) > 0
              && c.getInventoryItemCount(BAIT) >= 40) {

            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Walking to Contest";
              pathWalker(564, 492);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Entering gate";
              atObject(564, 492);
            }
            if (c.isRunning() && c.getInventoryItemCount(GARLIC) > 0) {
              CURRENT_QUEST_STEP = "Hiding Garlic";
              c.walkTo(568, 488);
              c.useItemIdOnObject(569, 488, GARLIC);
              c.sleep(2000);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Starting Contest";
              followNPCDialog(BONZO, BONZO_START_DIALOG);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Gone Fishing...";
              while (c.isRunning() && c.getInventoryItemCount(GIANT_CARP) == 0) {
                c.atObject(570, 489);
                c.sleep(2000);
              }
            }
            if (c.isRunning() && c.getInventoryItemCount(GIANT_CARP) > 0) {
              CURRENT_QUEST_STEP = "Ending Contest";
              followNPCDialog(BONZO, BONZO_END_DIALOG);
              sleepUntilQuestStageChanges();
              CURRENT_QUEST_STEP = "Leaving Contest Area";
              c.walkTo(565, 492);
              atObject(564, 492);
              c.walkTo(560, 494);
            }
          }
          break;
        case 2: // no middle case?
        case 3: // has trophy
          int FISHING_TROPHY = ItemId.HEMENSTER_FISHING_TROPHY.getId();
          if (c.getInventoryItemCount(FISHING_TROPHY) > 0) {
            if (c.isRunning() && !isInRectangle(START_RECTANGLE)) {
              CURRENT_QUEST_STEP = "Walking to Dwarf";
              pathWalker(OUTSIDE_HUT_WAYPOINT);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Ending Quest";
              talkToNpcId(MOUNTAIN_DWARF);
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
