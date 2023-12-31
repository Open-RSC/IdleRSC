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
public class K_LumbPresentGrabber extends IdleScript {

  public int start(String parameters[]) {
    controller.displayMessage("@red@present grabber! Let's party like it's 2004!");

    while (controller.isRunning()) {
      if (controller.getInventoryItemCount(980) < 26) {
        controller.walkTo(127, 663);
        controller.atObject(127, 664);
        controller.sleep(1000);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(1000);
        if (controller.getInventoryItemCount() > 29) {
          goToBank();
        }
        controller.walkTo(122, 663);
        controller.atObject(122, 664);
        controller.sleep(1000);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(1000);
        if (controller.getInventoryItemCount() > 29) {
          goToBank();
        }
        controller.walkTo(123, 661);
        controller.atObject(123, 660);
        controller.sleep(1000);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(1000);
        if (controller.getInventoryItemCount() > 29) {
          goToBank();
        }
        controller.walkTo(125, 660);
        controller.atObject(126, 660);
        controller.sleep(1000);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(1000);
        if (controller.getInventoryItemCount() > 29) {
          goToBank();
        }
        controller.walkTo(126, 657);
        controller.atObject(126, 656);
        controller.sleep(1000);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(1000);
        if (controller.getInventoryItemCount() > 29) {
          goToBank();
        }

        controller.walkTo(123, 657);
        controller.atObject(123, 656);
        controller.sleep(1000);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(1000);
        controller.walkTo(125, 661);
        if (controller.getInventoryItemCount() > 29) {
          goToBank();
        }
      }
    }
    return 1000; // start() must return a int value now.
  }

  public void goToBank() {
    controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
    controller.walkTo(322, 552);
    controller.walkTo(330, 552);
    bank();
    controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport"));
    controller.walkTo(126, 648);
    controller.atObject(127, 648);
    controller.sleep(1000);
    while (controller.isBatching()) controller.sleep(1000);
    controller.walkTo(115, 651);
    controller.walkTo(116, 659);
    controller.walkTo(124, 663);
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
