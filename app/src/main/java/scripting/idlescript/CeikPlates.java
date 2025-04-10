package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;

public class CeikPlates extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.SMITHING, Category.IRONMAN_SUPPORTED},
          "Ceikry",
          "Steel Platebody Smither by Ceikry");

  public int start(String[] params) {
    while (controller.isRunning()) {
      controller.displayMessage("Steel Platebody Smither by Ceikry", 3);

      // Walk to starting point
      if (!controller.isInBank() && !controller.isInOptionMenu() && controller.isRunning()) {
        controller.walkTo(150, 503);
        controller.sleep(100);
      }

      // Talk to npc and wait for 450ms until the dialogue options appear
      controller.talkToNpc(controller.getNearestNpcById(95, false).serverIndex);
      controller.sleep(450);

      // Access bank dialogue option
      if (controller.isInOptionMenu()) {
        controller.optionAnswer(0);
      } else {
        controller.talkToNpc(controller.getNearestNpcById(95, false).serverIndex);
        controller.sleep(450);
        controller.optionAnswer(0);
      }

      // Wait for bank to open
      while (!controller.isInBank() && controller.isRunning()) {
        controller.sleep(450);
      }

      // Deposit items
      while (controller.isInBank() && controller.isRunning()) {

        if (controller.getInventoryItemCount(118) > 0) {
          controller.depositItem(118, 5);
          controller.sleep(1000);
        }

        controller.withdrawItem(171, 25);
        controller.sleep(1200);

        controller.closeBank();
      }

      // Walk to anvil
      controller.walkTo(150, 503);
      controller.sleep(100);
      controller.walkTo(150, 507);
      controller.sleep(100);
      controller.walkTo(148, 512);
      controller.sleep(100);

      // Use bar on anvil
      controller.useItemIdOnObject(148, 513, 5);
      controller.sleep(1000);

      // Go through dialogue to smith plates
      controller.optionAnswer(1);
      controller.sleep(25);
      controller.optionAnswer(2);
      controller.sleep(25);
      controller.optionAnswer(2);
      controller.sleep(25);
      controller.optionAnswer(3);

      // Wait until plates are done smithing (6000-7000ms)
      controller.sleep(7000);
    }
    controller.displayMessage("@red@SCRIPT STOPPED", 3);
    return 1000;
  }
}
