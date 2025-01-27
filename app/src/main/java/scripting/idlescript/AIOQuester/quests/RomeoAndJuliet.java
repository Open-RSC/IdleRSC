package scripting.idlescript.AIOQuester.quests;

import models.entities.ItemId;
import models.entities.Location;
import models.entities.NpcId;
import scripting.idlescript.AIOQuester.QuestHandler;
import scripting.idlescript.AIOQuester.models.QuitReason;

public final class RomeoAndJuliet extends QuestHandler {
  // COORDINATES FOR walkPath() PATHS AND pickupUnreachableItem()

  // OBJECT COORDINATES

  private static final int[] JULIET_DOOR_ONE = {166, 495};
  private static final int[] JULIET_DOOR_TWO = {170, 498};
  private static final int[] JULIET_DOOR_THREE = {170, 496};
  private static final int[] JULIET_DOOR_FOUR = {168, 499};
  private static final int[] CHURCH_DOOR_ONE = {107, 474};
  private static final int[] CHURCH_DOOR_TWO = {107, 480};
  private static final int[] CHURCH_DOOR_THREE = {104, 478};
  private static final int[] CHURCH_DOOR_FOUR = {103, 474};

  private static final int[] APOTHECARY_DOOR = {216, 2580};

  // ITEM IDS
  private static final int CADAVABERRIES_ID = ItemId.CADAVABERRIES.getId();

  // NPC IDS
  private static final int ROMEO_ID = NpcId.ROMEO.getId();
  private static final int JULIET_ID = NpcId.JULIET.getId();
  private static final int FATHER_LAWRENCE_ID = NpcId.FATHER_LAWRENCE.getId();
  private static final int APOTHECARY_ID = NpcId.APOTHECARY.getId();

  // NPC DIALOGS
  private static final String[] STEP_ONE_START_QUEST_DIALOG = {"Can I help find her for you?"};

  private static void checkCadavaberries() {
    if (!(c.getUnnotedInventoryItemCount(CADAVABERRIES_ID)
        >= 1)) { // if the player doesn't have the berry
      if (c.getInventoryItemCount() > 28) { // if the player doesn't have inventory space
        walkTowards(Location.VARROCK_EAST_BANK); // move to bank
        c.openBank();
        while (c.getInventoryItemCount() > 28 && c.isRunning()) { // deposit last item in inventory
          c.depositItem(c.getInventorySlotItemId(29)); // makes room for beads and coins
          c.sleep(1000);
        }
      }
      walkTowards(Location.VARROCK_EAST_MINE); // great location for poison berries

      while (!(c.getUnnotedInventoryItemCount(CADAVABERRIES_ID) >= 1)
          && c.isRunning()) { // we gonna be a gatherer
        int[] CADAVABERRIES_LOCATION = c.getNearestItemById(CADAVABERRIES_ID);
        if (CADAVABERRIES_LOCATION != null
            && c.getUnnotedInventoryItemCount(CADAVABERRIES_ID) == 0) {
          c.pickupItem(
              CADAVABERRIES_LOCATION[0], CADAVABERRIES_LOCATION[1], CADAVABERRIES_ID, true, true);
          c.sleep(618);
        }
      }
    }
  }

  public static void run() {

    // quest stages here:
    // https://gitlab.com/openrsc/openrsc/-/blob/develop/server/plugins/com/openrsc/server/plugins/authentic/quests/free/RomeoAndJuliet.java
    // and here:
    // https://gitlab.com/openrsc/openrsc/-/blob/develop/server/plugins/com/openrsc/server/plugins/authentic/npcs/varrock/Apothecary.java
    checkCadavaberries();

    while (isQuesting()) {

      switch (QUEST_STAGE) {
        case 0:
          checkCadavaberries();
          CURRENT_QUEST_STEP = "Talking to Romeo";
          followNPCDialog(ROMEO_ID, STEP_ONE_START_QUEST_DIALOG);
          sleepUntilQuestStageChanges();

        case 1:
          checkCadavaberries();
          CURRENT_QUEST_STEP = "Walking to Juliet's";
          walkTowards(Location.VARROCK_JULIETS_HOUSE);
          // make sure Juliet isn't stuck behind any doors
          c.openDoor(JULIET_DOOR_ONE[0], JULIET_DOOR_ONE[1]);
          c.sleep(618);
          c.openDoor(JULIET_DOOR_TWO[0], JULIET_DOOR_TWO[1]);
          c.sleep(618);
          c.openDoor(JULIET_DOOR_THREE[0], JULIET_DOOR_THREE[1]);
          c.sleep(618);
          c.openDoor(JULIET_DOOR_FOUR[0], JULIET_DOOR_FOUR[1]);
          c.sleep(618);
          CURRENT_QUEST_STEP = "Talking to Juliet";
          c.talkToNpcId(JULIET_ID, true); // get message
          sleepUntilQuestStageChanges();
        case 2:
          checkCadavaberries();
          walkTowards(Location.VARROCK_MARKET_SQUARE);
          c.talkToNpcId(ROMEO_ID, true); // deliver message
          sleepUntilQuestStageChanges();
        case 3:
          checkCadavaberries();
          CURRENT_QUEST_STEP = "Going to Church";
          walkTowards(Location.VARROCK_CHURCH);

          c.openDoor(CHURCH_DOOR_ONE[0], CHURCH_DOOR_ONE[1]);
          c.sleep(618);
          c.openDoor(CHURCH_DOOR_TWO[0], CHURCH_DOOR_TWO[1]);
          c.sleep(618);
          c.openDoor(CHURCH_DOOR_THREE[0], CHURCH_DOOR_THREE[1]);
          c.sleep(618);
          c.openDoor(CHURCH_DOOR_FOUR[0], CHURCH_DOOR_FOUR[1]);
          c.sleep(618);
          CURRENT_QUEST_STEP = "Talk to the Pastor guy";
          c.talkToNpcId(FATHER_LAWRENCE_ID, true); // ask for advice
          sleepUntilQuestStageChanges();
        case 4:
          checkCadavaberries();
          CURRENT_QUEST_STEP = "Talk to Potion guy";
          walkTowards(Location.VARROCK_APOTHECARY);
          c.openDoor(APOTHECARY_DOOR[0], APOTHECARY_DOOR[1]);
          c.talkToNpcId(APOTHECARY_ID, true); // get request
          sleepUntilQuestStageChanges();
        case 5:
          checkCadavaberries();
          c.sleep(2000);
          c.talkToNpcId(APOTHECARY_ID, true); // give request
          sleepUntilQuestStageChanges();
        case 6:
          walkTowards(Location.VARROCK_JULIETS_HOUSE);
          c.openDoor(JULIET_DOOR_ONE[0], JULIET_DOOR_ONE[1]);
          c.sleep(618);
          c.openDoor(JULIET_DOOR_TWO[0], JULIET_DOOR_TWO[1]);
          c.sleep(618);
          c.openDoor(JULIET_DOOR_THREE[0], JULIET_DOOR_THREE[1]);
          c.sleep(618);
          c.openDoor(JULIET_DOOR_FOUR[0], JULIET_DOOR_FOUR[1]);
          c.sleep(618);
          CURRENT_QUEST_STEP = "Giving death potion";
          c.talkToNpcId(JULIET_ID, true);
          sleepUntilQuestStageChanges();
        case 7:
          CURRENT_QUEST_STEP = "Returning to lover boy";
          walkTowards(Location.VARROCK_MARKET_SQUARE);
          CURRENT_QUEST_STEP = "Completing quest";
          c.talkToNpcId(ROMEO_ID, true); // finish quest
          sleepUntilQuestStageChanges();
          break;
        default:
          quit(QuitReason.QUEST_STAGE_NOT_IN_SWITCH);
          break;
      }
    }
  }
}
