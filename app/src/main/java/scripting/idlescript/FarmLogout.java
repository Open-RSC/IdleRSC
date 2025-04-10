package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;

public class FarmLogout extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.MISCELLANEOUS, Category.IRONMAN_SUPPORTED, Category.ULTIMATE_IRONMAN_SUPPORTED
          },
          "Dvorak",
          "Logs out and closes the client.");

  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    while (!controller.isLoggedIn()) return 100;

    controller.logout();
    controller.stop();
    System.exit(0);
    return 100;
  }
}
