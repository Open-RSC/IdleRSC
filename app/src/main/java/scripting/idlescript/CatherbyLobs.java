package scripting.idlescript;

import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;
import orsc.ORSCharacter;

public class CatherbyLobs extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.FISHING, Category.IRONMAN_SUPPORTED},
          "SaladFork",
          "Catherby Lobs - SaladFork");

  public int start(String[] params) {
    controller.displayMessage("@cya@Andrew has been added to your friend list.", 3);
    controller.sleep(1000);
    controller.displayMessage("@cya@Andrew: tells you Catherby Lob Script has been activated!");
    controller.sleep(1000);
    controller.displayMessage("@cya@Andrew: tells you Script created by SaladFork!", 3);
    controller.sleep(1000);
    controller.displayMessage("@cya@Andrew has been removed from your friend list.", 3);
    controller.sleep(1000);
    while (controller.isRunning()) {
      while (controller.getInventoryItemCount() < 30 && controller.isRunning()) {
        if (controller.getFatigue() >= 82) {
          while (!controller.isSleeping()) {
            controller.itemCommand(1263);
            controller.sleep(100);
          }
          while (controller.isSleeping()) {
            controller.sleep(100);
          }
        }
        controller.atObject2(409, 504);
        controller.sleep(2000);
      }

      controller.walkTo(413, 501);
      controller.walkTo(416, 499);
      controller.walkTo(421, 497);
      controller.walkTo(426, 495);
      controller.walkTo(430, 494);
      controller.walkTo(431, 489);
      controller.walkTo(435, 485);
      controller.walkTo(432, 482);

      while (controller.getInventoryItemCount(372) > 0 && controller.isRunning()) {
        if (controller.getFatigue() >= 95) {
          while (!controller.isSleeping()) {
            controller.itemCommand(1263);
            controller.sleep(100);
          }
          while (controller.isSleeping()) {
            controller.sleep(100);
          }
        }
        controller.useItemIdOnObject(432, 480, 372);
        controller.sleep(2000);
      }
      while (controller.getInventoryItemCount(374) > 0 && controller.isRunning()) {
        controller.dropItem(374);
        controller.sleep(1000);
      }
      controller.walkTo(435, 484);
      controller.walkTo(435, 487);
      controller.walkTo(430, 489);
      controller.walkTo(431, 492);
      controller.walkTo(434, 494);
      controller.walkTo(436, 497);
      controller.walkTo(439, 497);
      controller.walkTo(440, 495);

      while (!controller.isInOptionMenu()) {
        ORSCharacter BankerID = controller.getNearestNpcById(95, false);
        controller.talkToNpc(BankerID.serverIndex);
        controller.sleep(1000);
      }
      controller.optionAnswer(0);
      while (!controller.isInBank()) controller.sleep(1);
      while (controller.getInventoryItemCount(373) > 0) {
        controller.depositItem(373, 1);
        controller.sleep(100);
      }
      controller.walkTo(439, 497);
      controller.walkTo(436, 498);
      controller.walkTo(432, 498);
      controller.walkTo(429, 498);
      controller.walkTo(426, 498);
      controller.walkTo(423, 498);
      controller.walkTo(420, 499);
      controller.walkTo(418, 499);
      controller.walkTo(415, 500);
      controller.walkTo(412, 502);
      controller.walkTo(410, 503);
    }
    controller.displayMessage("@red@STOPPED", 3);
    return 1000;
  }
}
