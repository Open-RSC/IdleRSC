package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import java.util.concurrent.ThreadLocalRandom;

public class ChickenMunch0r extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.FISHING, Category.IRONMAN_SUPPORTED},
          "ChookMunch0r",
          "Cooks chicken");

  public int start(String[] params) {
    controller.displayMessage("ChookMunch0r - pun@ran@K@whi@rocke@ran@R", 3);
    int[] Meat = controller.getNearestItemById(133);
    while (controller.isRunning()) {
      while (controller.isSleeping()) controller.sleep(1);

      if (controller.getInventoryItemCount() < 30) {
        while (controller.getObjectAtCoord(274, 603) == 60
            && controller.getPlayer().currentX > 274
            && controller.isRunning()) {
          controller.atObject(274, 603);
          controller.sleep(1000);
        }

        while (Meat != null
            && controller.isRunning()
            && !controller.isSleeping()
            && controller.getInventoryItemCount() < 30) {
          controller.pickupItem(133);
          controller.sleep(random(750, 1000));
          Meat = controller.getNearestItemById(133);
        }
      }

      if (controller.getInventoryItemCount() == 30) {

        if (controller.getObjectAtCoord(274, 603) == 60) {
          controller.atObject(274, 603);
          controller.sleep(1000);
        } else controller.walkTo(275, 614);
        controller.walkTo(276, 636);

        if (controller.isDoorOpen(276, 637)) {
          controller.openDoor(276, 637);
          controller.sleep(1000);
        }
      }

      while (controller.getInventoryItemCount() != 1) {

        while (controller.getFatigue() >= 97
            && controller.isRunning()
            && !controller.isSleeping()) {
          System.out.println("Sleeping.");
          controller.itemCommand(1263);
          controller.sleep(1500);
        }

        while (controller.isRunning() && controller.isItemInInventory(134)) {
          controller.dropItem(controller.getInventoryItemSlotIndex(134));
          controller.sleep(1000);
        }

        while (controller.isRunning() && controller.isItemInInventory(132)) {
          controller.itemCommand(132);
          controller.sleep(500);
        }

        while (controller.isItemInInventory(133) && controller.isRunning()) {
          controller.useItemIdOnObject(275, 638, 133);
          controller.sleep(1000);
        }
      }

      if (controller.getInventoryItemCount() == 1) {

        if (controller.isDoorOpen(276, 637)) {
          controller.openDoor(276, 637);
          controller.sleep(1000);
        } else controller.walkTo(271, 625);
        controller.walkTo(274, 613);
        controller.walkTo(274, 605);

        if (controller.getObjectAtCoord(274, 603) == 60) {
          controller.atObject(274, 603);
          controller.sleep(1000);
        } else controller.walkTo(271, 604);
      }
    }
    controller.displayMessage("ChookMunch0r - @red@STOPPED", 3);
    return 1000;
  }

  private int random(int min, int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }
}
