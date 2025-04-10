package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;

public class BuryBone extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.PRAYER, Category.IRONMAN_SUPPORTED}, "Lucid", "Bury Bones");

  int level;

  public int start(String[] params) {
    try {
      level = Integer.parseInt(params[0]);
    } catch (Exception e) {
      level = 99;
    }
    while (controller.isRunning()) {
      if (controller.getFatigue() >= 90 && controller.isRunning()) {
        while (!controller.isSleeping() && controller.isRunning()) {
          controller.itemCommand(1263);
          controller.sleep(2500);
        }
        while (controller.isSleeping() && controller.isRunning()) {
          controller.sleep(100);
        }
      }
      while (controller.getInventoryItemCount() < 30) {
        controller.pickupItem(20);
        controller.sleep(1000);
      }

      if (controller.getInventoryItemCount() == 30) {
        while (controller.getInventoryItemCount(20) > 0) {
          if (controller.getFatigue() >= 90 && controller.isRunning()) {
            while (!controller.isSleeping() && controller.isRunning()) {
              controller.itemCommand(1263);
              controller.sleep(2500);
            }
            while (controller.isSleeping() && controller.isRunning()) {
              controller.sleep(100);
            }
          }

          controller.itemCommand(20);
          controller.sleep(500);
        }
      }

      if (controller.getCurrentStat(5) == level) {
        controller.logout();
      }
    }
    controller.displayMessage("@red@Bury Bones STOPPED", 3);
    return 1000;
  }
}
