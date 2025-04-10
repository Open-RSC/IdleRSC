package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;

public class ObtainSleepingBag extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.MISCELLANEOUS},
          "",
          "Gets a sleeping bag from Bilbo Baggin. Probably no longer useful.");

  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {

    if (controller.isLoggedIn() && controller.getInventoryItemCount(1263) > 0) System.exit(0);

    if (!controller.isInTrade()) {
      int serverIndex = controller.getPlayerServerIndexByName("bilbo baggin");

      if (serverIndex == -1) {
        controller.log("Giving player " + parameters[0] + " is not present!");
        return 2000;
      }

      controller.tradePlayer(serverIndex);
      return 1000;
    } else {
      if (controller.isInTradeConfirmation()) {
        controller.acceptTradeConfirmation();
        controller.log("Finished trading.");
        controller.sleep(5000);
      } else {
        controller.acceptTrade();
        return 1000;
      }

      return 1000;
    }
  }
}
