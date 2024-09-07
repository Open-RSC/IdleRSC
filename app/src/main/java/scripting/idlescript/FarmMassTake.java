package scripting.idlescript;

import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;

/** MassGive by Dvorak. */
public class FarmMassTake extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(new Category[] {Category.MISCELLANEOUS}, "Dvorak", "");

  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {

    if (!controller.isInTrade()) {
      int serverIndex = controller.getPlayerServerIndexByName(parameters[0]);

      if (serverIndex == -1) {
        controller.log("Giving player " + parameters[0] + " is not present!");
        return 2000;
      }

      controller.tradePlayer(serverIndex);
      return 3000;
    } else {
      if (controller.isInTradeConfirmation()) {
        controller.acceptTradeConfirmation();
        controller.log("Finished trading.");
        controller.sleep(3000);
        controller.stop();
        controller.logout();
        System.exit(0);
      } else {
        controller.acceptTrade();
        return 1000;
      }

      return 1000;
    }
  }
}
