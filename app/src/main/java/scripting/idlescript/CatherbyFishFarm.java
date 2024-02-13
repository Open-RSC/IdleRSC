package scripting.idlescript;

public class CatherbyFishFarm extends IdleScript {

  final int[] rawIds = {349, 351, 366, 369};
  final int[] cookedIds = {350, 352, 353, 367, 368, 370, 371};

  public int start(String[] param) {
    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (controller.getInventoryItemCount() < 30) {
        int fishLvl = controller.getBaseStat(controller.getStatId("Fishing"));
        int cookLvl = controller.getBaseStat(controller.getStatId("Cooking"));

        if (fishLvl < 35 || cookLvl < 30) {
          int x = controller.currentX();
          int y = controller.currentY();

          if (controller.currentX() != 418 || controller.currentY() != 499)
            controller.walkTo(418, 499);
          // .npcCommand1(controller.getNearestNpcById(193, false).serverIndex);

          controller.atObject(418, 500);
        } else {
          if (controller.currentX() != 409 && controller.currentY() != 503)
            controller.walkTo(409, 503);

          // controller.npcCommand1(controller.getNearestNpcById(194, false).serverIndex);
          controller.atObject(409, 504);
        }

        controller.sleep(500);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(10);
      } else {
        cook();

        bank();

        walkBack();
      }
    }

    return 1000; // start() must return a int value now.
  }

  public void cook() {

    controller.walkTo(419, 499);
    controller.walkTo(428, 498);
    controller.walkTo(436, 493);
    controller.walkTo(435, 486);

    openCookDoor();

    for (int rawId : rawIds) {
      while (controller.getInventoryItemCount(rawId) > 0) {

        if (controller.getShouldSleep()) controller.sleepHandler(true);

        if (!controller.isBatching()) controller.useItemIdOnObject(432, 480, rawId);

        controller.sleep(250);
      }
    }

    controller.walkTo(435, 485);
    openCookDoor();
  }

  public void openDoor() {
    while (controller.getObjectAtCoord(439, 497) == 64) {
      controller.atObject(439, 497);
      controller.sleep(100);
    }
  }

  public void openCookDoor() {
    while (!controller.isDoorOpen(435, 486)) {
      controller.openDoor(435, 486);
    }
  }

  public void bank() {

    controller.walkTo(439, 497);
    openDoor();

    controller.openBank();

    for (int cookedId : cookedIds) {
      if (controller.getInventoryItemCount(cookedId) > 0) {
        controller.depositItem(cookedId, controller.getInventoryItemCount(cookedId));
        controller.sleep(250);
      }
    }

    for (int rawId : rawIds) {
      if (controller.getInventoryItemCount(rawId) > 0) {
        controller.depositItem(rawId, controller.getInventoryItemCount(rawId));
        controller.sleep(250);
      }
    }

    controller.walkTo(439, 496);
    openDoor();
  }

  public void walkBack() {
    controller.walkTo(436, 497);
    controller.walkTo(430, 497);
    controller.walkTo(423, 496);
    controller.walkTo(419, 499);
  }
}
