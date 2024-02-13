package scripting.idlescript;

import models.entities.*;

public final class QH_BarCrawl extends QH__QuestHandler {
  private static final int[] BARB_OUTPOST_DOOR = {494, 543};
  // ITEM IDS
  private static final int BAR_CRAWL_CARD = ItemId.BARCRAWL_CARD.getId();
  // NPC IDS
  private static final int CPT_BARNABY = NpcId.CAPTAIN_BARNABY.getId();
  private static final int CST_OFFICIAL = NpcId.CUSTOMS_OFFICIAL.getId();
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
  private static final String[] BARB_GUARD = {
    "I want to come through this gate", "Looks can be deceiving, I am in fact a barbarian"
  };

  public int start(String[] param) { // warning does not handle food conditions
    QUEST_NAME = "Miniquest";
    START_RECTANGLE = BARB_OUTPOST;
    QUEST_REQUIREMENTS = new String[] {};
    SKILL_REQUIREMENTS = new int[][] {};
    EQUIP_REQUIREMENTS = new int[][] {};
    ITEM_REQUIREMENTS = new int[][] {{ItemId.COINS.getId(), 1000}};
    INVENTORY_SPACES_NEEDED = 5;
    doQuestChecks();
    c.log("~ by Kaila", "mag");

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.isRunning()) {
        CURRENT_QUEST_STEP = "Starting Barcrawl";
        c.walkTo(494, 544);
        c.atObject(BARB_OUTPOST_DOOR[0], BARB_OUTPOST_DOOR[1]);
        c.sleep(1000);
        followNPCDialogPopUps(305, BARB_GUARD);
      }
      doBarCrawl();
      CURRENT_QUEST_STEP = "Quest Complete";
      quit("Quest completed");
    }
    quit("Script stopped");
    return 1000;
  }

  public void doBarCrawl() {
    if (c.getInventoryItemCount(BAR_CRAWL_CARD) > 0) { // do bar crawl
      if (c.isRunning()) {
        CURRENT_QUEST_STEP = "Going to Brimhaven Pub";
        c.walkTo(500, 547);
        pathWalker(527, 617);
        CURRENT_QUEST_STEP = "Boat to Brimhaven";
        followNPCDialog(CPT_BARNABY, BOAT_TO_BRIM_DIALOG);
        CURRENT_QUEST_STEP = "Walking to Bar";
        c.walkTo(467, 659);
        pathWalker(446, 699);
        walkPath(
            new int[][] {
              {448, 699}, // bypass closed bar door
              {449, 699},
              {450, 702}
            });
        CURRENT_QUEST_STEP = "Getting Drink";
        followNPCDialog(BRIM_TENDER, GENERIC_TENDER_DIALOG);
      }
      if (c.isRunning()) {
        CURRENT_QUEST_STEP = "Walking to Boats";
        walkPath(
            new int[][] {
              {449, 699}, // bypass closed bar door
              {448, 699},
              {445, 694},
              {445, 673},
              {458, 662},
              {467, 654} // walk to boats
            });
        CURRENT_QUEST_STEP = "Going to Ardy";
        followNPCDialog(CST_OFFICIAL, BOAT_TO_ARDY_DIALOG);
      }
      if (c.isRunning()) {
        CURRENT_QUEST_STEP = "Going to Seers Pub";
        c.walkTo(545, 611);
        pathWalker(522, 455);
        walkPath(new int[][] {{522, 454}}); // bypass closed doors
        CURRENT_QUEST_STEP = "Getting Drink";
        followNPCDialog(SEERS_TENDER, GENERIC_TENDER_DIALOG);
        CURRENT_QUEST_STEP = "Going to Falador Pub";
        walkPath(new int[][] {{522, 454}, {522, 455}, {528, 458}}); // bypass closed doors
      }
      if (c.isRunning()) {
        pathWalker(319, 540);
        walkPath(new int[][] {{320, 542}, {320, 543}, {319, 546}}); // bypass closed doors
        CURRENT_QUEST_STEP = "Getting Drink";
        followNPCDialog(FALADOR_TENDER, GENERIC_TENDER_DIALOG);
        CURRENT_QUEST_STEP = "Going to Port Sarim";
        walkPath(new int[][] {{320, 543}, {320, 542}, {319, 540}}); // bypass closed doors
        pathWalker(252, 623);
        walkPath(new int[][] {{252, 624}, {257, 624}}); // bypass closed doors
        CURRENT_QUEST_STEP = "Getting Drink";
        followNPCDialog(SARIM_TENDER, GENERIC_TENDER_DIALOG);
      }
      if (c.isRunning()) {
        CURRENT_QUEST_STEP = "Going to Varrock";
        walkPath(new int[][] {{252, 624}, {252, 623}, {252, 621}}); // bypass closed doors
        pathWalker(127, 524);
        walkPath(new int[][] {{126, 524}, {120, 524}}); // bypass closed doors
        CURRENT_QUEST_STEP = "Getting Drink";
        followNPCDialog(VARROCK_TENDER, GENERIC_TENDER_DIALOG);
        CURRENT_QUEST_STEP = "Going to Blue Moon Inn";
        walkPath(
            new int[][] {
              {126, 524}, // bypass closed doors
              {127, 524},
              {131, 521},
              {133, 512}
            });
        pathWalker(82, 441);
        walkPath(
            new int[][] {
              {82, 442}, // bypass closed doors
              {81, 452}
            });
      }
      if (c.isRunning()) {
        CURRENT_QUEST_STEP = "Getting Drink";
        followNPCDialog(BOAR_TENDER, GENERIC_TENDER_DIALOG);
        CURRENT_QUEST_STEP = "Going back to Barbarians";
        walkPath(
            new int[][] {
              {82, 442}, // bypass closed doors
              {82, 441},
              {82, 435}
            });
        // todo check card completion here
        pathWalker(133, 511); // Varrock center
        pathWalker(512, 554); // legends guild
        c.walkTo(501, 551);
        CURRENT_QUEST_STEP = "Turning in Card";
        talkToNpcId(305);
      }
    }
  }
}
