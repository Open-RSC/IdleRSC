package scripting.idlescript.SeattaScript.AIOQuester.quests;

import models.entities.ItemId;
import models.entities.Location;
import scripting.idlescript.SeattaScript.AIOQuester.QuestHandler;
import scripting.idlescript.SeattaScript.AIOQuester.models.QuitReason;

/* AIOQuester script args:
  debug - Logs all messages received by the client by their exact string:
    This includes server, quest, chat, private, and trade messages
    Examples:
      Debug: Quest: [Wizard Sedridor: stone. I know not where the altars are]
      Debug: Server: [@gre@You haved gained 1 quest point!]
    Format:
      Debug: MESSAGE_TYPE: [EXACT MESSAGE STRING]
*/

// ! Quest scripts must extend QuestHandler

// * Check out QuestHandler for useful methods for writing scripts

// * Please strive to make quest scripts viable to UIMs

/* Any info you need from the QuestDef for your quest you can get from the 'QUEST' object.
For example, to get the array of required equipped items you would use:
QUEST.getRequiredEquippedItems(); */

public final class _Example extends QuestHandler {

  // ! Quest scripts must have override the run method
  @Override
  public void run() {
    /* OPTIONAL: Reassigning START_LOCATION must happen before looping isQuesting.
    This should only be done if you want a START_LOCATION different from
    QUEST.getStartLocation(), which is automatically loaded from the Quest enum by the Handler

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

          // Get quest stages from their server plugins
          /*
          Authentic Quests:
          https://gitlab.com/openrsc/openrsc/-/tree/develop/server/plugins/com/openrsc/server/plugins/authentic/quests
          Custom Quests:
          https://gitlab.com/openrsc/openrsc/-/tree/develop/server/plugins/com/openrsc/server/plugins/custom/quests
          */

        case 0:
          // You can set CURRENT_QUEST_STEP to update the paint's second string.
          CURRENT_QUEST_STEP = "Doing stage 0 stuff";

          // You can set STEP_ITEMS to an int[][] where each nested array is an {item id, amount}
          // pair.
          // If set the items will be displayed in the paint as either:
          //   GREEN - If the player has the item with the correct amount
          //   YELLOW - If the player has the item, but not the entire amount
          //   RED - If the player does not have the item
          STEP_ITEMS =
              new int[][] {
                {ItemId.POT_OF_FLOUR.getId(), 1},
                {ItemId.BUCKET.getId(), 1}
              };

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

  /*
  The following methods can be overridden for handling various messages from the client:
    serverMessageInterrupt -- Called when a "server" message is received
    questMessageInterrupt -- Called when a "quest" message is received
    privateMessageInterrupt -- Called when a "private" message is received
    chatMessageInterrupt -- Called when a "chat" message is received
    tradeMessageInterrupt -- Called when a "trade" message is received

    Below is an example of overriding questMessageInterrupt();
   */

  @Override
  public void questMessageInterrupt(String message) {
    if (message.toLowerCase().contains("cheese")) c.chatMessage("I LIKE CHEESE!");
  }
}
