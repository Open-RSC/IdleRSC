package scripting.idlescript;

/**
 * Grabs presents from under varrock trees - based off vial crafter by dvorak automatically banks at
 * full inv, at the end of a batch cycle Start near the trees will keep going in circles if trees
 * are empty, not worth fixing it doesnt break bot Also, when stopping the bot, click stop,
 * optionally stop current batch. Then, wait for the bot to stop it will walk to 133,506 then fully
 * stop. again, not worth fixing
 *
 * @author Dvorak Heavily edited by Kailash
 */
public class K_FallyPresentGrabber extends IdleScript {
  int vialsFilled = 0;
  int fullVials = 0;
  int emptyVials = 0;
  int objectx = 0;
  int objecty = 0;

  long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String parameters[]) {
    controller.displayMessage("@red@present grabber! Let's party like it's 2004!");

    while (controller.isRunning()) {
      if (controller.getInventoryItemCount(980) < 30) {
        controller.walkTo(316, 538);
        controller.atObject(317, 538);
        controller.sleep(1000);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(1000);
        if (controller.getInventoryItemCount() > 29) {
          goToBank();
        }
        controller.walkTo(311, 538);
        controller.atObject(310, 538);
        controller.sleep(1000);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(1000);
        if (controller.getInventoryItemCount() > 29) {
          goToBank();
        }
        controller.walkTo(311, 541);
        controller.atObject(310, 541);
        controller.sleep(1000);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(1000);
        if (controller.getInventoryItemCount() > 29) {
          goToBank();
        }
        controller.walkTo(316, 541);
        controller.atObject(317, 541);
        controller.sleep(1000);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(1000);
        if (controller.getInventoryItemCount() > 29) {
          goToBank();
        }
      }
    }
    return 1000; // start() must return a int value now.
  }

  public void goToBank() {
    controller.walkTo(318, 539);
    controller.walkTo(325, 546);
    controller.walkTo(326, 552);
    controller.walkTo(330, 553);
    bank();
    controller.walkTo(326, 552);
    controller.walkTo(325, 541);
    controller.walkTo(315, 539);
  }

  public void bank() {
    controller.setStatus("@blu@Banking..");

    controller.openBank();

    while (controller.isInBank() && controller.getInventoryItemCount(980) > 0) {
      controller.depositItem(980, controller.getInventoryItemCount(980));
      controller.sleep(100);
    }
  }
}
