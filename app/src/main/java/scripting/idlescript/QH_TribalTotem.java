package scripting.idlescript;

import models.entities.ItemId;
import models.entities.NpcId;
import models.entities.SkillId;

public final class QH_TribalTotem extends QH__QuestHandler {
  private static final int[][] BOAT_TO_HORACIO = {{546, 609}, {548, 594}, {554, 579}, {555, 579}};
  private static final int CPT_BARNABY = NpcId.CAPTAIN_BARNABY.getId();
  private static final int CST_OFFICIAL = NpcId.CUSTOMS_OFFICIAL.getId();
  private static final int KANGAI_MAU = NpcId.KANGAI_MAU.getId();
  private static final int HORACIO = NpcId.HORACIO.getId();
  private static final int RPDT = NpcId.RPDT_EMPLOYEE.getId();
  private static final int CROMPERTY = NpcId.WIZARD_CROMPERTY.getId();
  private static final int ADDRESS_LABEL = ItemId.ADDRESS_LABEL.getId();
  private static final int TRIBAL_TOTEM = ItemId.TRIBAL_TOTEM.getId();
  private static final String[] HORACIO_DIALOG = {
    "So who are you?", "So do you garden round the back too?"
  };
  private static final String[] RPDT_DIALOG = {"So when are you going to deliver this crate?"};
  private static final String[] CROMPERTY_DIALOG = {
    "So what have you invented?", "Can I be teleported please?", "Yes, that sounds good teleport me"
  };
  private static final String[] BOAT_TO_BRIM_DIALOG = {"Yes please"};
  private static final String[] BOAT_TO_ARDY_DIALOG = {
    "Can I board this ship?", "Search away I have nothing to hide", "Ok"
  };
  private static final String[] KANGAI_MAU_START_DIALOG = {
    "I'm in search of adventure", "Ok I will get it back"
  };

  private void openBradDoor() {
    int x = c.currentX();
    int y = c.currentY();
    while (c.currentX() == x && c.currentY() == y) {
      int sleepInt = 2000;
      c.atWallObject(561, 586);
      c.sleep(sleepInt);
      c.optionAnswer(1);
      c.sleep(sleepInt);
      c.optionAnswer(0);
      c.sleep(sleepInt);
      c.optionAnswer(0);
      c.sleep(sleepInt);
      c.optionAnswer(3);
      c.sleep(sleepInt);
    }
  }

  public int start(String[] param) { // warning does not handle food conditions
    QUEST_NAME = "Tribal totem";
    if (c.currentX() < 500 && c.currentX() > 400 && c.currentY() > 645) {
      START_RECTANGLE = SHRIMP_AND_PARROT; // on brim
    } else {
      START_RECTANGLE = ARDY_SOUTH_BANK;
    }
    QUEST_REQUIREMENTS = new String[] {};
    SKILL_REQUIREMENTS = new int[][] {{SkillId.THIEVING.getId(), 21}};
    EQUIP_REQUIREMENTS = new int[][] {};
    ITEM_REQUIREMENTS = new int[][] {{ItemId.COINS.getId(), 1000}};
    INVENTORY_SPACES_NEEDED = 5;
    TOTAL_QUEST_STAGES = 2;
    doQuestChecks();
    // c.log("WARNING: Entering dangerous areas, low combat not recommended", "red");
    c.log("~ by Kaila", "mag");

    while (c.isRunning()) { // start cases with pathwalk for resume cases
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      QUEST_STAGE = c.getQuestStage(QUEST_ID);
      switch (QUEST_STAGE) { // 6 stages in total
        case 0: // not started
          if (c.isRunning() && !isInRectangle(START_RECTANGLE)) {
            CURRENT_QUEST_STEP = "Walking back to Start";
            pathWalker(546, 615);
          }
          if (c.isRunning()) {
            walkPath(new int[][] {{546, 615}});
          }
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Boat to Brimhaven";
            followNPCDialog(CPT_BARNABY, BOAT_TO_BRIM_DIALOG);
            CURRENT_QUEST_STEP = "Walking to Bar";
            c.walkTo(467, 659);
            pathWalker(451, 688);
            CURRENT_QUEST_STEP = "Starting Quest";
            followNPCDialog(KANGAI_MAU, KANGAI_MAU_START_DIALOG);
            sleepUntilQuestStageChanges();
            c.walkTo(447, 690);
          }
          break;
        case 1: // setting up break-in
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Walking to Boats";
            pathWalker(467, 652);
          }
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Going to Ardy";
            followNPCDialog(CST_OFFICIAL, BOAT_TO_ARDY_DIALOG);
            CURRENT_QUEST_STEP = "Walking to Horacio";
            walkPath(BOAT_TO_HORACIO); // gate checks
          }
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Talking to Horacio";
            followNPCDialog(HORACIO, HORACIO_DIALOG);
          }
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Talking to Cromperty";
            walkPath(
                new int[][] { // gate checks
                  {555, 579},
                  {554, 579},
                  {545, 581},
                  {545, 580}
                });
            followNPCDialog(CROMPERTY, CROMPERTY_DIALOG);
          }
          while (c.isRunning() && c.getInventoryItemCount(ADDRESS_LABEL) == 0) {
            CURRENT_QUEST_STEP = "Grabbing Address Label";
            c.atObject(559, 617);
            c.sleep(3000);
          }
          if (c.isRunning()) {
            CURRENT_QUEST_STEP = "Putting Label on other";
            c.useItemIdOnObject(558, 617, ADDRESS_LABEL);
            c.sleep(3000);
            CURRENT_QUEST_STEP = "Mailing it";
            followNPCDialog(RPDT, RPDT_DIALOG);
            sleepUntilQuestStageChanges();
            c.walkTo(559, 609);
          }
          break;
        case 2: // breaking into house
          if (c.isRunning() && c.getInventoryItemCount(706) == 0) {
            CURRENT_QUEST_STEP = "Grabbing Guide Book";
            walkPath(new int[][] {{565, 604}, {565, 603}, {564, 600}});
            pickupUnreachableItem(706, new int[] {564, 600}, 1);
            c.walkTo(561, 606);
          }
          if (c.getInventoryItemCount(706) > 0) {
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Walking to Cromperty";
              pathWalker(545, 578);
              CURRENT_QUEST_STEP = "Talking to Cromperty";
              followNPCDialog(CROMPERTY, CROMPERTY_DIALOG);
              c.sleep(2000);
            }
            if (c.isRunning()) {
              c.walkTo(561, 586);
              CURRENT_QUEST_STEP = "Picking BRAD Lock"; // brad door is wall object
              openBradDoor();
              c.walkTo(565, 585);
              CURRENT_QUEST_STEP = "Picking Door Lock";
              atWallObject2(565, 586);
              c.walkTo(564, 586);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Checking Stair Trap";
              c.atObject2(563, 587);
              c.sleep(6000);
              CURRENT_QUEST_STEP = "Climbing Stair";
              climb(563, 587);
              CURRENT_QUEST_STEP = "Searching Chest";
              c.walkTo(560, 1532);
              while (c.isRunning() && c.getInventoryItemCount(TRIBAL_TOTEM) == 0) {
                c.atObject(560, 1531);
                c.sleep(2000);
              }
            }
            if (c.isRunning() && c.getInventoryItemCount(TRIBAL_TOTEM) > 0) {
              CURRENT_QUEST_STEP = "Escaping Mansion";
              c.walkTo(563, 1534);
              c.atObject(563, 1531);
              c.sleep(2000);
            }
            if (c.isRunning() && c.getInventoryItemCount(TRIBAL_TOTEM) > 0) {
              CURRENT_QUEST_STEP = "Escaping through Sewers";
              climb(563, 587);
              c.sleep(3000);
              c.walkTo(568, 3437);
              climb(569, 3437);
              CURRENT_QUEST_STEP = "Returning the Totem";
              pathWalker(543, 615);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Boat to Brimhaven";
              followNPCDialog(CPT_BARNABY, BOAT_TO_BRIM_DIALOG);
            }
            if (c.isRunning()) {
              CURRENT_QUEST_STEP = "Walking to Bar";
              c.walkTo(467, 659);
              pathWalker(451, 688);
              CURRENT_QUEST_STEP = "Ending Quest";
              talkToNpcId(KANGAI_MAU);
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
