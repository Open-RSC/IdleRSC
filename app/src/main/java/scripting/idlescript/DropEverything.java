package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;

/**
 * This is a basic script that drops everything in your inventory.
 *
 * @author Dvorak
 */
public class DropEverything extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.MISCELLANEOUS, Category.IRONMAN_SUPPORTED, Category.ULTIMATE_IRONMAN_SUPPORTED
          },
          "Dvorak",
          "This is a basic script that drops everything in your inventory.");

  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    controller.displayMessage("@red@DropEverything by Dvorak. Let's party like it's 2018!");
    controller.displayMessage("Dropping everything in inventory...");

    while (controller.getInventoryItemCount() > 0) {
      controller.dropItem(0);
    }

    controller.displayMessage("Done.");
    controller.stop();

    return 1000; // start() must return a int value now.
  }
}
