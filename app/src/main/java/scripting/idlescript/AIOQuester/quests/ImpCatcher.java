package scripting.idlescript.AIOQuester.quests;

import java.util.Arrays;
import models.entities.ItemId;
import models.entities.Location;
import models.entities.NpcId;
import orsc.ORSCharacter;
import scripting.idlescript.AIOQuester.QuestHandler;
import scripting.idlescript.AIOQuester.models.QuitReason;

public final class ImpCatcher extends QuestHandler {
  // COORDINATES FOR walkPath() PATHS AND pickupUnreachableItem()
  private static final int[] WIZARD_TOWER_OUTER = {217, 687};
  private static final int[] FALADOR_WEST_SOUTH = {314, 558};
  private static final int[] FALADOR_WEST_NORTH = {314, 550};
  private static final int[] VOLCANO_SOUTH = {418, 702};
  private static final int[] VOLCANO_NORTH = {417, 681};
  private static final int[] KARAMJA_DOCKS = {335, 713};
  static int[] FARM_LOCATION = null;
  static int[] FARM_LOCATION_ONE = null;
  static int[] FARM_LOCATION_TWO = null;

  // OBJECT COORDINATES

  private static final int[] WIZARD_TOWER_LADDER_UP_GROUND_FLOOR = {218, 692};
  private static final int[] WIZARD_TOWER_LADDER_UP_SECOND_FLOOR = {216, 1636};
  private static final int[] WIZARD_TOWER_LADDER_DOWN_THIRD_FLOOR = {216, 2580};
  private static final int[] WIZARD_TOWER_LADDER_DOWN_SECOND_FLOOR = {218, 1636};

  // ITEM IDS
  private static final int RED_BEAD_ID = ItemId.RED_BEAD.getId();
  private static final int YELLOW_BEAD_ID = ItemId.YELLOW_BEAD.getId();
  private static final int BLACK_BEAD_ID = ItemId.BLACK_BEAD.getId();
  private static final int WHITE_BEAD_ID = ItemId.WHITE_BEAD.getId();
  private static final int[] BEADS = {RED_BEAD_ID, YELLOW_BEAD_ID, BLACK_BEAD_ID, WHITE_BEAD_ID};
  private static final int[] BEADS_TABLE = Arrays.copyOf(BEADS, BEADS.length);

  // NPC IDS
  private static final int MIZGOG_ID = NpcId.WIZARD_MIZGOG.getId();
  private static final int IMP_ID = NpcId.IMP.getId();
  private static final int DARK_WIZARD_ID = NpcId.DARKWIZARD_LVL25.getId();
  private static final int SCORPION_ID = NpcId.SCORPION.getId();
  private static final int CAPTAIN_TOBIAS_ID = NpcId.CAPTAIN_TOBIAS.getId();
  private static final int CUSTOM_OFFICER_ID = NpcId.CUSTOMS_OFFICER.getId();
  // NPC DIALOGS
  private static final String[] MIZGOG_START_QUEST_DIALOG = {
    "Give me a quest!", "Give me a quest please"
  };
  private static final String[] VOLCANO_RETURN_DIALOG = {
    "Can I board this ship?", "Search away I have nothing to hide", "Ok"
  };
  private static final String[] VOLCANO_DEPART_DIALOG = {"Yes please"};

  private static boolean checkForBeads() { // Check if beads are in inventory
    return (c.getUnnotedInventoryItemCount(RED_BEAD_ID) >= 1
        && c.getUnnotedInventoryItemCount(YELLOW_BEAD_ID) >= 1
        && c.getUnnotedInventoryItemCount(BLACK_BEAD_ID) >= 1
        && c.getUnnotedInventoryItemCount(WHITE_BEAD_ID) >= 1);
  }

  private static boolean onKaramja;
  /**
   * Uses the combat level formula to determine if an NPC may be aggressive. Aggressive monster
   * behaviour is stored server-sided, not within the IdleRSC files.
   */
  private static boolean checkLevelAggression(
      int MONSTER_ID) { // Determines if the NPC may be aggressive towards the player
    int MONSTER_LEVEL = c.calculateNpcLevel(MONSTER_ID);
    ORSCharacter quester = c.getPlayer();
    int PLAYER_LEVEL = quester.level;
    return PLAYER_LEVEL
        <= MONSTER_LEVEL * 2; // Player needs to be 2x + 1 higher combat level than the NPC
  }

  public static void run() {
    boolean DARK_WIZARD_AGGRESSIVE = checkLevelAggression(DARK_WIZARD_ID);
    boolean SCORPION_AGGRESSIVE = checkLevelAggression(SCORPION_ID);

    boolean HAS_BEADS = checkForBeads();

    if (!HAS_BEADS) {
      Location.walkTowardsNearestBank();
      c.openBank();

      while (c.getInventoryItemCount() > 26 && c.isRunning()) {
        c.depositItem(c.getInventorySlotItemId(26)); // makes room for beads and coins
        c.sleep(1000);
      }

      c.withdrawItem(RED_BEAD_ID); // attempts to withdraw any banked beads
      c.sleep(1000);
      c.withdrawItem(YELLOW_BEAD_ID);
      c.sleep(1000);
      c.withdrawItem(BLACK_BEAD_ID);
      c.sleep(1000);
      c.withdrawItem(WHITE_BEAD_ID);
      c.sleep(1000);
      c.withdrawItem(10, 60); // attempts to withdraw round trip fare to the Island
      c.sleep(1000);
      c.closeBank();
      c.sleep(1000);

      HAS_BEADS = checkForBeads();
    }
    if (HAS_BEADS) {
      START_LOCATION = Location.WIZARDS_TOWER_ENTRANCE;
    }
    if (!SCORPION_AGGRESSIVE && c.getInventoryItemCount(10) >= 60) {
      START_LOCATION = Location.PORT_SARIM_DOCKS;
    }

    while (isQuesting()) {

      switch (QUEST_STAGE) {
        case 0:
          if (!HAS_BEADS
              && !SCORPION_AGGRESSIVE
              && c.getInventoryItemCount(10)
                  >= 60) { // farm on Karamja if the player can get there and do it safely
            CURRENT_QUEST_STEP = "Walking to imps on Karamja...";
            // Location.walkTowards(PORT_SARIM_DOCKS[0], PORT_SARIM_DOCKS[1]);
            followNPCDialog(CAPTAIN_TOBIAS_ID, VOLCANO_DEPART_DIALOG);
            c.sleep(10000);
            Location.walkTowards(VOLCANO_NORTH[0], VOLCANO_NORTH[1]);
            FARM_LOCATION_ONE = VOLCANO_NORTH;
            FARM_LOCATION_TWO = VOLCANO_SOUTH;
            FARM_LOCATION = FARM_LOCATION_ONE;
            onKaramja = true;
          }
          if (!HAS_BEADS
              && (SCORPION_AGGRESSIVE
                  || c.getInventoryItemCount(10)
                      < 60)) { // else walks towards Falador West for two imp spawns
            CURRENT_QUEST_STEP = "Walking to imps in Falador...";
            Location.walkTowards(FALADOR_WEST_SOUTH[0], FALADOR_WEST_SOUTH[1]);
            FARM_LOCATION_ONE = FALADOR_WEST_SOUTH;
            FARM_LOCATION_TWO = FALADOR_WEST_NORTH; //
            FARM_LOCATION = FALADOR_WEST_SOUTH;
          }
          while (!HAS_BEADS && c.isRunning()) {

            STEP_ITEMS =
                new int[][] {
                  {ItemId.WHITE_BEAD.getId(), 1},
                  {ItemId.RED_BEAD.getId(), 1},
                  {ItemId.BLACK_BEAD.getId(), 1},
                  {ItemId.YELLOW_BEAD.getId(), 1}
                };
            CURRENT_QUEST_STEP = "Farming the imps...";
            ORSCharacter imp = c.getNearestNpcById(IMP_ID, true);

            if (imp != null) {
              c.attackNpc(imp.serverIndex);
              c.sleep(1000);
            }

            for (int beadId :
                BEADS_TABLE) { // if any item has dropped after we kill the NPC, check if it is
              // a
              // bead and if we need it
              int[] beadCoord = c.getNearestItemById(beadId);

              if (beadCoord != null && c.getUnnotedInventoryItemCount(beadId) == 0) {
                c.pickupItem(beadCoord[0], beadCoord[1], beadId, true, true);
                c.sleep(618);
              }
            }

            HAS_BEADS = checkForBeads();
            imp = c.getNearestNpcById(IMP_ID, true);
            if (!c.isCurrentlyWalking() && !c.isInCombat() && imp == null) {
              Location.walkTowards(FARM_LOCATION[0], FARM_LOCATION[1]);
              if (FARM_LOCATION == FARM_LOCATION_ONE) {
                FARM_LOCATION =
                    FARM_LOCATION_TWO; // imps are notoriously wander-happy. get your butt back
                // to falador
              } else {
                FARM_LOCATION = FARM_LOCATION_ONE;
              }
            }
          }
          CURRENT_QUEST_STEP = "Returning to the Wizard's Tower...";
          if (onKaramja) { // if on the island,
            Location.walkTowards(KARAMJA_DOCKS[0], KARAMJA_DOCKS[1]);
            followNPCDialog(CUSTOM_OFFICER_ID, VOLCANO_RETURN_DIALOG);
            c.sleep(10000);
          }
          Location.walkTowards(WIZARD_TOWER_OUTER[0], WIZARD_TOWER_OUTER[1]);
          int[][] enterSeq = {
            WIZARD_TOWER_LADDER_UP_GROUND_FLOOR,
            WIZARD_TOWER_LADDER_UP_SECOND_FLOOR, // climb up the wizard's tower
          };
          atObjectSequence(enterSeq);
          c.sleep(2000);
          c.openDoor(216, 2579);
          c.sleep(1000);
          c.openDoor(216, 2581);
          c.sleep(1000);
          c.openDoor(217, 2582);
          c.sleep(1300);
          if (DARK_WIZARD_AGGRESSIVE) {

            Location.walkTowards(215, 2579);

            boolean MIZGOG_SAFE = false;

            while (!MIZGOG_SAFE && c.isRunning()) {
              CURRENT_QUEST_STEP = "Waiting to talk to Mizgog";
              ORSCharacter MIZGOG = c.getNearestNpcById(MIZGOG_ID, true);
              if (MIZGOG != null) {
                int[] MIZGOG_LOCATION = c.getNpcCoordsByServerIndex(MIZGOG.serverIndex);
                if (MIZGOG_LOCATION[1] <= 2579) {
                  MIZGOG_SAFE = true;
                }
              }
            }
          }
          followNPCDialog(MIZGOG_ID, MIZGOG_START_QUEST_DIALOG); // start the quest
          sleepUntilQuestStageChanges();
          break;

        case 1:
          CURRENT_QUEST_STEP = "Handing in beads to the Wizard";
          talkToNpcId(MIZGOG_ID);
          int[][] leaveSeq = {
            WIZARD_TOWER_LADDER_DOWN_THIRD_FLOOR,
            WIZARD_TOWER_LADDER_DOWN_SECOND_FLOOR, // climb up the wizard's tower
          };
          atObjectSequence(leaveSeq);
          break;
        default:
          quit(QuitReason.QUEST_STAGE_NOT_IN_SWITCH);
          break;
      }
    }
  }
}
