package scripting.idlescript;

import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;

public class AirPicker extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.GATHERING, Category.IRONMAN_SUPPORTED},
          "Hiyadude",
          "Air Picker - Hiyadude");

  public int start(String[] params) {
    controller.displayMessage(
        "@ran@=@ran@=@ran@= @lre@Air Picker Started - By Hiyadude =) @ran@=@ran@=@ran@=", 3);
    while (controller.isRunning()) {
      controller.pickupItem(33);
      controller.sleep(100);
    }
    controller.displayMessage("@ran@=@ran@=@ran@= @dre@Air Picker STOPPED @ran@=@ran@=@ran@=", 3);
    return 1000;
  }
}
