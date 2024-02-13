package scripting.idlescript;

import models.entities.ItemId;
import models.entities.NpcId;

public final class QH_MonksFriend extends QH__QuestHandler {
  private static final int[] OUTSIDE_WAYPOINT = {589, 647};
  private static final int[] INSIDE_MONESTARY = {589, 664};
  private static final int BUCKET_OF_WATER = ItemId.BUCKET_OF_WATER.getId();
  private static final int BLANKET = ItemId.BLANKET.getId();
  private static final int LOGS = ItemId.LOGS.getId();
  private static final int BUCKET = ItemId.BUCKET.getId();
  private static final int BROTHER_OMAD = NpcId.BROTHER_OMAD.getId();
  private static final int BROTHER_CEDRIC = NpcId.BROTHER_CEDRIC.getId();
  private static final String[] BROTHER_OMAD_START_QUEST_DIALOG = {
    "Why can't you sleep, what's wrong?", "Can I help at all?"
  };
  private static final String[] BROTHER_OMAD_PART_2 = {
    "who's brother Cedric?", "where should I look?"
  };
  private static final String[] BROTHER_CEDRIC_PART_2 = {
    "Yes i'd be happy to",
  };

  public int start(String[] param) { // warning does not handle food conditions
    QUEST_NAME = "Monk's friend";
    START_RECTANGLE = ARDY_MONASTERY;
    QUEST_REQUIREMENTS = new String[] {};
    SKILL_REQUIREMENTS = new int[][] {};
    EQUIP_REQUIREMENTS = new int[][] {};
    ITEM_REQUIREMENTS = new int[][] {{ItemId.BRONZE_AXE.getId(), 1}};
    INVENTORY_SPACES_NEEDED = 5;
    TOTAL_QUEST_STAGES = 6;
    doQuestChecks();
    c.log("~ by Kaila", "mag");

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      QUEST_STAGE = c.getQuestStage(QUEST_ID);
      switch (QUEST_STAGE) { // 6 stages in total
        case 0: // not started
          if (c.isRunning() && c.getInventoryItemCount(LOGS) == 0) {
            CURRENT_QUEST_STEP = "Getting logs";
            while (c.isRunning() && c.getInventoryItemCount(LOGS) == 0) {
              if (c.getObjectAtCoord(596, 655) == 1) { // chop tree
                c.walkTo(597, 655);
                c.atObject(596, 655);
              } else if (c.getObjectAtCoord(599, 655) == 1) { // chop tree2
                c.walkTo(598, 655);
                c.atObject(599, 655);
              }
              c.sleep(3000);
            }
          }
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Walking to Brother Omad";
            pathWalker(OUTSIDE_WAYPOINT);
            walkPath(new int[][] {INSIDE_MONESTARY}); // walk inside
          }
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Talking to Brother Omad";
            followNPCDialog(BROTHER_OMAD, BROTHER_OMAD_START_QUEST_DIALOG);
            sleepUntilQuestStageChanges();
            CURRENT_QUEST_STEP = "Walking Outside";
            walkPath(new int[][] {OUTSIDE_WAYPOINT});
          }
          break;
        case 1: // get the blanket and return it
          if (c.isRunning() && c.getInventoryItemCount(BLANKET) == 0) {
            CURRENT_QUEST_STEP = "Walking to Kandarin Dungeon";
            if (isInRectangle(START_RECTANGLE))
              walkPath(new int[][] {OUTSIDE_WAYPOINT}); // walk outside
            pathWalker(618, 657); // walk to kandarin dungeon
          }
          // underground section
          if (c.isRunning() && c.getInventoryItemCount(BLANKET) == 0) {
            CURRENT_QUEST_STEP = "Grabbing Blanket";
            climb(617, 657); // enter dungeon ladder
            walkPath(new int[][] {{619, 3500}});
            pickupGroundItem(716, 1);
            walkPath(new int[][] {{618, 3489}});
            climb(617, 3489); // enter dungeon ladder
          }
          if (c.isRunning() && c.getInventoryItemCount(BLANKET) == 1) {
            CURRENT_QUEST_STEP = "Walking to Brother Omad";
            pathWalker(OUTSIDE_WAYPOINT); // walk back to omad
            walkPath(new int[][] {INSIDE_MONESTARY}); // walk inside
          }
          if (c.isRunning() && c.getInventoryItemCount(BLANKET) == 1) {
            CURRENT_QUEST_STEP = "Giving the Blanket";
            talkToNpcId(BROTHER_OMAD);
            sleepUntilQuestStageChanges();
          }
          break;
        case 2: // start part 3
          CURRENT_QUEST_STEP = "Talking to Omad again";
          if (c.isRunning()) {
            followNPCDialog(BROTHER_OMAD, BROTHER_OMAD_PART_2);
            sleepUntilQuestStageChanges();
            walkPath(new int[][] {OUTSIDE_WAYPOINT}); // walk outside
          }
          break;
        case 3: // Talk to cedric
          if (c.isRunning() && c.currentX() < 608) {
            CURRENT_QUEST_STEP = "Walking to Cedric";
            if (isInRectangle(START_RECTANGLE))
              walkPath(new int[][] {OUTSIDE_WAYPOINT}); // walk outside
            pathWalker(616, 640);
            // walkPath(new int[][]{{589,653},{602,645},{615,636}}); //walk outside
          }
          if (c.isRunning() // get bucket
              && c.getInventoryItemCount(BUCKET) == 0
              && c.getInventoryItemCount(ItemId.BUCKET_OF_WATER.getId()) == 0) {
            CURRENT_QUEST_STEP = "Getting Bucket";
            pickupGroundItem(BUCKET, 1);
          }
          if (c.isRunning() // get water
              && c.getInventoryItemCount(BUCKET) == 1
              && c.getInventoryItemCount(BUCKET_OF_WATER) == 0) {
            CURRENT_QUEST_STEP = "Filling Bucket";
            walkPath(new int[][] {{602, 633}});
            c.useItemIdOnObject(600, 633, BUCKET);
            CURRENT_QUEST_STEP = "Walking Back";
            walkPath(new int[][] {{612, 637}});
          }
          if (c.isRunning() // walk to cedric
              && c.getInventoryItemCount(BUCKET_OF_WATER) == 1) {
            CURRENT_QUEST_STEP = "Talking to Cedric";
            talkToNpcId(BROTHER_CEDRIC);
            sleepUntilQuestStageChanges();
          }
          break;
        case 4: // Sober them up
          if (c.isRunning() && c.currentX() < 608) {
            CURRENT_QUEST_STEP = "Walking to Cedric";
            if (isInRectangle(START_RECTANGLE))
              walkPath(new int[][] {OUTSIDE_WAYPOINT}); // walk outside
            pathWalker(616, 640);
          }
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Sobering up Cedric";
            useItemOnNearestNpcId(BROTHER_CEDRIC, BUCKET_OF_WATER);
            sleepUntilQuestStageChanges();
          }
          break;
        case 5: // talk to them
          if (c.isRunning() && c.getInventoryItemCount(LOGS) == 0) {
            CURRENT_QUEST_STEP = "Getting logs";
            while (c.isRunning() && c.getInventoryItemCount(LOGS) == 0) {
              if (c.getObjectAtCoord(596, 655) == 1) { // chop tree
                c.walkTo(611, 638);
                c.atObject(611, 639);
                c.sleep(3000);
              }
            }
          }
          if (c.isRunning() && c.getInventoryItemCount(LOGS) > 0) {
            CURRENT_QUEST_STEP = "Giving Cedric Logs";
            followNPCDialog(BROTHER_CEDRIC, BROTHER_CEDRIC_PART_2);
            sleepUntilQuestStageChanges();
            c.walkTo(606, 636);
          }
          break;
        case 6: // returning to omad
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Walking to Brother Omad";
            pathWalker(OUTSIDE_WAYPOINT);
            walkPath(new int[][] {INSIDE_MONESTARY}); // walk inside
          }
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Talking to Brother Omad";
            talkToNpcId(BROTHER_OMAD);
            sleepUntilQuestStageChanges();
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
