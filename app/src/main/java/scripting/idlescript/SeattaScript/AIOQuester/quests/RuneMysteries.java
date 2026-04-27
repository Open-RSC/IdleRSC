package scripting.idlescript.SeattaScript.AIOQuester.quests;

import models.entities.ItemId;
import models.entities.Location;
import models.entities.NpcId;
import models.entities.QuestId;
import orsc.ORSCharacter;
import scripting.idlescript.SeattaScript.AIOQuester.QuestHandler;
import scripting.idlescript.SeattaScript.AIOQuester.models.QuitReason;

public final class RuneMysteries extends QuestHandler {

  private final int DUKE_ID = NpcId.DUKE_OF_LUMBRIDGE.getId();
  private final int SEDRIDOR_ID = NpcId.SEDRIDOR.getId();
  private final int AUBURY_ID = NpcId.AUBURY.getId();

  private final int AIR_TALISMAN = ItemId.AIR_TALISMAN.getId();
  private final int RESEARCH_PACKAGE = ItemId.RESEARCH_PACKAGE.getId();
  private final int RESEARCH_NOTES = ItemId.RESEARCH_NOTES.getId();

  private final String[] DUKE_DIALOG = {
    "Have you any quests for me?", "Sure, I have some spare time"
  };
  private final String[] SEDRIDOR_DIALOG_1 = {"I'm looking for the head wizard", "Yes, certainly"};
  private final String[] AUBURY_DIALOG = {
    "I've been sent here with a package for you", "Yes, please."
  };
  private final String[] SEDRIDOR_DIALOG_2 = {"Rune Mysteries"};
  private boolean breakFinalDialog = false;

  public void run() {
    hasWalkedToStart = true;
    while (isQuesting()) {

      // The quest only has one actual quest stage, however this script was written as 3
      // pseudo-stages. One pseudo-stage is used per npc task

      if (breakFinalDialog) break;
      if (c.getInventoryItemCount(RESEARCH_NOTES) > 0) QUEST_STAGE = 3;
      else if (c.getInventoryItemCount(RESEARCH_PACKAGE) > 0) QUEST_STAGE = 2;
      else if (c.getInventoryItemCount(AIR_TALISMAN) > 0) QUEST_STAGE = 1;

      switch (QUEST_STAGE) {
        case 0:
          CURRENT_QUEST_STEP = "Talking to Duke Horacio";
          STEP_ITEMS = new int[][] {{AIR_TALISMAN, 1}};
          if (c.getNearestNpcById(DUKE_ID, false) != null) {
            talkToNpcAndSelectOptions(DUKE_ID, DUKE_DIALOG);
            waitForItem(AIR_TALISMAN);
          } else if (!Location.LUMBRIDGE_CASTLE_DUKES_ROOM.isAtLocation()) {
            Location.LUMBRIDGE_CASTLE_DUKES_ROOM.walkTowards();
          } else {
            c.sleep(640);
          }
          break;

        case 1:
          CURRENT_QUEST_STEP = "Taking Talisman to Sedridor";
          STEP_ITEMS = new int[][] {{AIR_TALISMAN, 1}};
          if (c.getInventoryItemCount(RESEARCH_PACKAGE) > 0) {
            CURRENT_QUEST_STEP = "Taking Package to Aubury";
            STEP_ITEMS = new int[][] {{RESEARCH_PACKAGE, 1}};
            if (c.getNearestNpcById(AUBURY_ID, false) != null) {
              talkToNpcAndSelectOptions(AUBURY_ID, AUBURY_DIALOG);
              waitForItem(RESEARCH_NOTES);
            } else if (Location.WIZARDS_TOWER_BASEMENT.isAtLocation()) {
              int[] ladder = c.getNearestObjectById(5);
              if (ladder != null) {
                c.atObject(ladder[0], ladder[1]);
                c.sleep(2000);
              } else {
                Location.WIZARDS_TOWER_ENTRANCE.walkTowards();
              }
            } else if (!Location.VARROCK_RUNE_SHOP.isAtLocation()) {
              Location.VARROCK_RUNE_SHOP.walkTowards();
            } else {
              c.sleep(640);
            }
            break;
          }
          if (c.getNearestNpcById(SEDRIDOR_ID, false) != null) {
            talkToNpcAndSelectOptions(SEDRIDOR_ID, SEDRIDOR_DIALOG_1);
            waitForItem(RESEARCH_PACKAGE);
          } else if (!Location.WIZARDS_TOWER_BASEMENT.isAtLocation()) {
            Location.WIZARDS_TOWER_BASEMENT.walkTowards();
          } else {
            c.sleep(640);
          }
          break;

        case 2:
          CURRENT_QUEST_STEP = "Taking Package to Aubury";
          STEP_ITEMS = new int[][] {{RESEARCH_PACKAGE, 1}};
          if (c.getNearestNpcById(AUBURY_ID, false) != null) {
            talkToNpcAndSelectOptions(AUBURY_ID, AUBURY_DIALOG);
            waitForItem(RESEARCH_NOTES);
          } else if (Location.WIZARDS_TOWER_BASEMENT.isAtLocation()) {
            int[] ladder = c.getNearestObjectById(5);
            if (ladder != null) {
              c.atObject(ladder[0], ladder[1]);
              c.sleep(2000);
            } else {
              Location.WIZARDS_TOWER_ENTRANCE.walkTowards();
            }
          } else if (!Location.VARROCK_RUNE_SHOP.isAtLocation()) {
            Location.VARROCK_RUNE_SHOP.walkTowards();
          } else {
            c.sleep(640);
          }
          break;

        case 3:
          CURRENT_QUEST_STEP = "Returning Notes to Sedridor";
          STEP_ITEMS = new int[][] {{RESEARCH_NOTES, 1}};
          if (c.getNearestNpcById(SEDRIDOR_ID, false) != null) {
            talkToNpcAndSelectOptions(SEDRIDOR_ID, SEDRIDOR_DIALOG_2);
            waitForStageChange();
          } else if (!Location.WIZARDS_TOWER_BASEMENT.isAtLocation()) {
            Location.WIZARDS_TOWER_BASEMENT.walkTowards();
          } else {
            c.sleep(640);
          }
          break;

        default:
          quit(QuitReason.QUEST_STAGE_NOT_IN_SWITCH);
      }
    }
  }

  private void waitForItem(int itemId) {
    long timeout = System.currentTimeMillis() + 15000;
    while (c.isRunning()
        && c.getInventoryItemCount(itemId) < 1
        && System.currentTimeMillis() < timeout) {
      c.sleep(100);
    }
  }

  private void waitForStageChange() {
    int currentStage = c.getQuestStage(QuestId.RUNE_MYSTERIES.getId());
    long timeout = System.currentTimeMillis() + 15000;
    while (c.getQuestStage(QuestId.RUNE_MYSTERIES.getId()) != currentStage
        && c.isRunning()
        && System.currentTimeMillis() < timeout) {
      c.sleep(100);
    }
  }

  private void talkToNpcAndSelectOptions(int npcId, String[] options) {
    ORSCharacter npc = c.getNearestNpcById(npcId, false);
    if (npc == null) return;

    String lastDialogueHeard;

    long busyTimeout = System.currentTimeMillis() + 60000;
    while (c.isNpcTalking(npc.serverIndex)
        && c.isRunning()
        && System.currentTimeMillis() < busyTimeout) {
      c.sleep(100);
    }

    c.talkToNpc(npc.serverIndex);
    c.sleep(1200);
    while (c.isCurrentlyWalking()) c.sleep(600);
    c.sleep(1000);

    for (String option : options) {
      long timeout = System.currentTimeMillis() + 120000;
      while (!c.isInOptionMenu()
          && c.isRunning()
          && System.currentTimeMillis() < timeout
          && !breakFinalDialog) {
        c.sleep(640);
      }

      if (c.isInOptionMenu()) {
        boolean found = false;
        for (int i = 0; i < c.getOptionMenuCount(); i++) {
          if (c.getOptionsMenuText(i).toLowerCase().contains(option.toLowerCase())) {
            c.optionAnswer(i);
            c.sleep(1000);
            found = true;
            break;
          }
        }
        if (!found) return;
      } else {
        return;
      }
    }
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("Well done you have completed the rune mysteries quest"))
      breakFinalDialog = true;
  }
}
