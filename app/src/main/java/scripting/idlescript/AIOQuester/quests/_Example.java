package scripting.idlescript.AIOQuester.quests;

import scripting.idlescript.AIOQuester.QuestHandler;
import scripting.idlescript.AIOQuester.models.Location;
import scripting.idlescript.AIOQuester.models.QuitReason;

// ! Quest scripts must extend QuestHandler and have a static void method named run.

// * Check out QuestHandler for useful methods for writing scripts

// * Please strive to make quest scripts viable to UIMs.

/* Any info you need from the QuestDef for your quest you can get from 'quest,'
For example, to get the array of required equipped items you would use:
quest.getRequiredEquippedItems(); */

public final class _Example extends QuestHandler {

  // ! Quest scripts must have a static run method. This is what QuestHandler runs
  public static void run() {
    /* OPTIONAL: Reassigning START_LOCATION must happen before looping isQuesting.
    This should only be done if you want a START_LOCATION different from
    quest.getStartLocation(), which is automatically loaded from the Quest enum by the Handler

    Here's an example of changing the start location based on inventory items */
    if (c.getInventoryItemCount(10) > 2) {
      START_LOCATION = Location.ARDOUGNE_MONASTERY;
    } else if (c.getInventoryItemCount(129) < 1) {
      START_LOCATION = Location.BARBARIAN_OUTPOST_ENTRANCE;
    }

    /* REQUIRED: isQuesting will check if the controller is still running every loop,
    as well as update QUEST_STAGE, and check if the quest is complete.
    It will also walk to START_LOCATION during the first loop

    This will keep looping until either the controller is stopped,
    the quit method is called, or the quest is completed */
    while (isQuesting()) {

      /* REQUIRED FOR NON-MINIQUESTS: Start stage checking loop here.
      Quest complete stage (-1) is already handled in isQuesting().
      If you get the QUEST_STAGE_NOT_IN_SWITCH error, you haven't defined your current quest
      stage in the switch

      Miniquests don't have stages, so you'll have to be a bit more creative with them */
      switch (QUEST_STAGE) {
          /* Add a case for each quest stage */

          /* At the end of every stage case, you should add a call to
          sleepUntilQuestStageChanges() to make sure nothing unexpected happens.
          Make sure everything else in the stage is finished or the script will get
          stuck. */
        case 0:
          // You can set CURRENT_QUEST_STEP to update the paint's second string.
          CURRENT_QUEST_STEP = "Doing stage 0 stuff";
          // Do stage 0 stuff here
          sleepUntilQuestStageChanges();
        case 1:
          CURRENT_QUEST_STEP = "Doing stage 1 stuff";
          // Do stage 1 stuff here
          sleepUntilQuestStageChanges();
          break;
        default:
          // REQUIRED: Quest stage isn't defined in switch
          quit(QuitReason.QUEST_STAGE_NOT_IN_SWITCH);
      }
    }
  }
}
