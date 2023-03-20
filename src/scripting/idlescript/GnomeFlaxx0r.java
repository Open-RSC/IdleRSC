package scripting.idlescript;

/**
 * This is a basic script that picks flax, optionally spins it, in Tree Stronghold.
 *
 * @author Dvorak
 *     <p>bugfixes and rewrite by Kaila
 */
public class GnomeFlaxx0r extends IdleScript {
  boolean spin = false;

  long flaxPicked = 0;
  long flaxBanked = 0;

  long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String parameters[]) {

    controller.displayMessage(
        "@red@Put true or false in Params. True to spin & bank flax, false to pick & bank flax.");
    controller.displayMessage("@red@Bot will Pick & Bank by Default!");

    if (controller.isInBank() == true) {
      controller.closeBank();
    }
    if (controller.currentY() > 1000) {
      bank();
      goToFlax();
      controller.sleep(1380);
    }
    if (parameters.length != 1) {
      controller.displayMessage(
          "@red@Put true or false in Params. True to spin & bank flax, false to pick & bank flax.");
      controller.displayMessage("@red@Bot will Pick & Bank by Default!");
      controller.stop();
    } else {
      spin = Boolean.valueOf(parameters[0]);
    }

    while (controller.isRunning()) {

      if (spin) {
        if (controller.getInventoryItemCount() < 30) {
          controller.setStatus("@cya@Walking to the flax...");
          goToWheelFlax();

          // if (controller.getInventoryItemCount() < 30) {
          controller.setStatus("@cya@Picking flax!");
          controller.atObject(693, 517);
          if (!controller.isAuthentic()) {
            controller.sleep(2000);
            while (controller.isBatching() && controller.getInventoryItemCount() < 30) {
              controller.sleep(640); // added batching - kaila
            }
          } else {
            controller.sleep(150);
          }
          // }
        }

        if (controller.getInventoryItemCount(675) > 0) {

          controller.setStatus("@cya@Spinnin' flax!");
          controller.sleepHandler(98, true);
          goToWheel();

          controller.useItemIdOnObject(693, 1459, 675);
          if (!controller.isAuthentic()) {
            controller.sleep(2000);
            while (controller.isBatching()) {
              controller.sleep(640); // added batching - kaila
            }
          } else {
            controller.sleep(500);
          }
        }
        if (controller.getInventoryItemCount(676) > 0 && controller.currentY() > 1000) {

          controller.atObject(691, 1459);
          controller.sleep(1240);
          goToBankFlax();
          goToBank();
          bank();
          goToFlax();
        }
      } else { // else if !spin just pick

        if (controller.getInventoryItemCount() < 30) {
          controller.setStatus("@cya@Picking flax!");
          controller.atObject(712, 517);
          if (!controller.isAuthentic()) {
            controller.sleep(2000);
            while (controller.isBatching() && controller.getInventoryItemCount() < 30) {
              controller.sleep(640); // added batching - kaila
            }
          } else {
            controller.sleep(150);
          }
        } else {
          goToBank();
          bank();
          goToFlax();
        }
      }
    }
    return 1000; // start() must return a int value now.
  }

  public void goToWheel() {

    controller.setStatus("@cya@Going back to wheel..");
    controller.walkTo(692, 515);
    controller.atObject(691, 515);
    controller.sleep(1000);
    controller.walkTo(692, 1459);
  }

  public void goToWheelFlax() {

    controller.setStatus("@cya@Going back to flax..");
    controller.walkTo(703, 516);
    controller.walkTo(693, 516);
    controller.sleep(340);
  }

  public void goToBankFlax() {

    controller.setStatus("@cya@Walking to the bank.");
    controller.walkTo(693, 516);
    controller.walkTo(703, 516);
    controller.walkTo(709, 518);
    controller.sleep(340);
  }

  public void goToBank() {

    controller.setStatus("@cya@Walking to the bank.");
    controller.walkTo(713, 516);
    controller.sleep(100);
    controller.atObject(714, 516); // go up ladder
    controller.sleep(1000);
    controller.walkTo(714, 1458);
    controller.walkTo(714, 1454);
    controller.setStatus("@gre@Done Walking..");
  }

  public void goToFlax() {

    controller.setStatus("@cya@Going back to flax..");
    controller.walkTo(714, 1454);
    controller.walkTo(714, 1459);
    controller.sleep(100);
    controller.atObject(714, 1460);
    controller.sleep(1000);
    controller.walkTo(712, 516);
    controller.setStatus("@gre@Done Walking..");
  }

  public void bank() {

    controller.setStatus("@cya@Banking...");
    controller.openBank();
    controller.sleep(640);

    if (controller.isInBank()) {
      if (controller.getInventoryItemCount(675) > 0) { // changed to if
        controller.depositItem(675, 30);
        controller.sleep(340);
      }
      if (controller.getInventoryItemCount(676) > 0) { // changed to if
        controller.depositItem(676, 30);
        controller.sleep(340);
      }
      if (spin) {
        flaxBanked = controller.getBankItemCount(676);
      } else {
        flaxBanked = controller.getBankItemCount(675);
      }
      controller.closeBank();
      controller.sleep(640);
    }
  }

  public void openDoor() {
    while (controller.getObjectAtCoord(500, 454) == 64) {
      controller.setStatus("@cya@Opening bank door...");
      controller.atObject(500, 454);
      controller.sleep(618);
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("uproot a flax plant")) flaxPicked++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      int flaxPerHr = 0;
      try {
        float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        float scale = (60 * 60) / timeRan;
        flaxPerHr = (int) (flaxPicked * scale);
      } catch (Exception e) {
        // divide by zero
      }
      controller.drawBoxAlpha(7, 7, 185, 21 + 14 + 14, 0x00FFFF, 128);
      controller.drawString(
          "@dgr@Gnome@cya@Flaxx0r @whi@by @red@Dvorak @whi@& @red@Kaila", 10, 21, 0xFFFFFF, 1);
      if (spin) {
        controller.drawString(
            "@dgr@Strings strung: @cya@"
                + String.format("%,d", flaxPicked)
                + " @gre@(@cya@"
                + String.format("%,d", flaxPerHr)
                + "@gre@/@cya@hr@gre@)",
            10,
            21 + 14,
            0xFFFFFF,
            1);
        controller.drawString(
            "@dgr@Strings in bank: @cya@" + String.format("%,d", flaxBanked),
            10,
            21 + 14 + 14,
            0xFFFFFF,
            1);
      } else {
        controller.drawString(
            "@dgr@Flax picked: @cya@"
                + String.format("%,d", flaxPicked)
                + " @gre@(@cya@"
                + String.format("%,d", flaxPerHr)
                + "@gre@/@cya@hr@gre@)",
            10,
            21 + 14,
            0xFFFFFF,
            1);
        controller.drawString(
            "@dgr@Flax in bank: @cya@" + String.format("%,d", flaxBanked),
            10,
            21 + 14 + 14,
            0xFFFFFF,
            1);
      }
    }
  }
}
