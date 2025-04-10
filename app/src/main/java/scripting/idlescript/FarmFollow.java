package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;

/**
 * A basic script for following other players. Useful for farms.
 *
 * @author Dvorak
 */
public class FarmFollow extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.MISCELLANEOUS, Category.IRONMAN_SUPPORTED, Category.ULTIMATE_IRONMAN_SUPPORTED
          },
          "Dvorak",
          "Follows a player with a given name" + "\n\nParameter:" + "\n - Name - Player to follow");

  public int start(String[] param) {
    if (controller.isRunning()) {
      if (controller.isInCombat()) controller.walkTo(controller.currentX(), controller.currentY());

      controller.followPlayer(controller.getPlayerServerIndexByName(param[0]));
      controller.sleep(5000);
    }

    return 1000; // start() must return a int value now.
  }
}
