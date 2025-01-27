package scripting.idlescript.AIOQuester.quests;

import models.entities.ItemId;
import models.entities.Location;
import models.entities.NpcId;
import scripting.idlescript.AIOQuester.QuestHandler;
import scripting.idlescript.AIOQuester.models.QuitReason;

public final class TheRestlessGhost extends QuestHandler {
  // COORDINATES FOR walkPath() PATHS AND pickupUnreachableItem()
  private static final int[] SKULL = {218, 3521};
  private static final int[] LADDER_UP = {218, 3526};
  private static final int[] COFFIN = {103, 675};
  // OBJECT COORDINATES

  private static final int[] CHURCH_DOOR = {115, 664};
  private static final int[] GRAVEYARD_DOOR = {106, 675};
  private static final int[] SWAMP_DOOR = {116, 709};

  private static final int[] APOTHECARY_DOOR = {216, 2580};

  // ITEM IDS
  private static final int GHOSTSPEAK_ID = ItemId.AMULET_OF_GHOSTSPEAK.getId();
  private static final int SKULL_ID = ItemId.QUEST_SKULL.getId();

  // NPC IDS
  private static final int PRIEST_ID = NpcId.PRIEST.getId();
  private static final int URHNEY_ID = NpcId.URHNEY.getId();
  private static final int GHOST_ID = NpcId.GHOST_RESTLESS.getId();
  private static final int APOTHECARY_ID = NpcId.APOTHECARY.getId();

  // NPC DIALOGS
  private static final String[] STEP_ONE_START_QUEST_DIALOG = {"I'm looking for a quest"};
  private static final String[] STEP_TWO_QUEST_DIALOG = {
    "Father Aereck sent me to talk to you", "He's got a ghost haunting his graveyard"
  };
  private static final String[] STEP_THREE_QUEST_DIALOG = {"Yep, now tell me what the problem is"};

  public static void run() {

    // quest stages here:

    while (isQuesting()) {

      switch (QUEST_STAGE) {
        case 0:
          // walk to the Church in Lumbridge
          CURRENT_QUEST_STEP = "Meeting the priest";
          c.openDoor(CHURCH_DOOR[0], CHURCH_DOOR[1]);
          followNPCDialog(PRIEST_ID, STEP_ONE_START_QUEST_DIALOG);
          sleepUntilQuestStageChanges();
        case 1:
          // check if room for amulet and skull

          if (c.getInventoryItemCount() > 28) { // if the player doesn't have 2 inventory spaces
            CURRENT_QUEST_STEP = "Need 2 inventory slots";
            walkTowards(Location.DRAYNOR_BANK); // move to bank
            c.openBank();
            while (c.getInventoryItemCount() > 28
                && c.isRunning()) { // deposit last item in inventory
              c.depositItem(c.getInventorySlotItemId(28)); // makes room for beads and coins
              c.sleep(1000);
            }
          }
          CURRENT_QUEST_STEP = "Walking to the father";
          walkTowards(Location.LUMBRIDGE_SWAMP_URHNEYS_HOUSE);
          c.openDoor(SWAMP_DOOR[0], SWAMP_DOOR[1]);
          followNPCDialog(URHNEY_ID, STEP_TWO_QUEST_DIALOG);
          sleepUntilQuestStageChanges();
        case 2:
          // equip amulet
          CURRENT_QUEST_STEP = "Wearing amulet";
          c.equipItemById(GHOSTSPEAK_ID);
          walkTowards(
              Location.LUMBRIDGE_GRAVEYARD); // gets stuck walking back directly from the swamp
          c.openDoor(GRAVEYARD_DOOR[0], GRAVEYARD_DOOR[1]);
          CURRENT_QUEST_STEP = "Talking to ghost";
          followNPCDialog(GHOST_ID, STEP_THREE_QUEST_DIALOG);
          sleepUntilQuestStageChanges();
        case 3:
          // off to Wizard's tower
          walkTowards(Location.WIZARDS_TOWER_BASEMENT);
          c.pickupItem(SKULL[0], SKULL[1], SKULL_ID, true, true);
          c.sleep(10000);

          while (!c.atObject(LADDER_UP[0], LADDER_UP[1]) & c.isRunning()) {
            while (c.isInCombat() && c.isRunning()) {
              c.walkTo(c.currentX(), c.currentY(), 0, true, true);
            }
          }
          ;

          c.sleep(1500);
          walkTowards(Location.LUMBRIDGE_GRAVEYARD);
          c.openDoor(GRAVEYARD_DOOR[0], GRAVEYARD_DOOR[1]);
          c.sleep(1200);
          while ((c.getUnnotedInventoryItemCount(SKULL_ID) >= 1)) {
            c.useItemIdOnObject(COFFIN[0], COFFIN[1], SKULL_ID);
            c.sleep(2000);
          }
          sleepUntilQuestStageChanges();

          // case 4:
          break;
        default:
          quit(QuitReason.QUEST_STAGE_NOT_IN_SWITCH);
          break;
      }
    }
  }
}
