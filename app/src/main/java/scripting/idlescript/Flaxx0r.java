package scripting.idlescript;

/**
 * This is a basic script that picks flax and banks it in Seers Village.
 *
 * @author Dvorak
 */
public class Flaxx0r extends IdleScript {
  long flaxPicked = 0;
  long flaxBanked = 0;
  int modifier = 128;

  final int[] bankPath = {501, 454, 501, 463, 501, 467, 501, 471, 501, 478, 496, 482, 490, 486};

  final long startTimestamp = System.currentTimeMillis() / 1000L;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    controller.log("This script picks flax in seers", "red");
    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (controller.getInventoryItemCount() < 30) {
        if (controller.currentY() < 454) {
          // we are inside the bank
          openDoor();
        }

        controller.setStatus("@cya@Walking to the field.");
        controller.walkPath(bankPath);

        while (controller.getInventoryItemCount() < 30) {
          controller.setStatus("@cya@Picking flax!");
          controller.atObject(489, 486);
          controller.sleep(150);
        }
      } else {
        controller.setStatus("@cya@Walking to the bank.");
        controller.walkPathReverse(bankPath);

        openDoor();

        controller.setStatus("@cya@Banking...");
        controller.openBank();

        while (controller.getInventoryItemCount(675) > 0) {
          controller.depositItem(675, controller.getInventoryItemCount(675));
          controller.sleep(618);
        }
        flaxBanked = controller.getBankItemCount(675);
      }
    }

    return 1000; // start() must return a int value now.
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
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        flaxPerHr = (int) (flaxPicked * scale);
      } catch (Exception e) {
        // divide by zero
      }
      controller.drawBoxAlpha(7, 7, 155, 21 + 14 + 14, 0x00FFFF, 128);
      controller.drawString("@dgr@Flax@cya@x0r @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
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
