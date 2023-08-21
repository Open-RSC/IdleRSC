package scripting.idlescript;

/**
 * Fills vials in Falador
 *
 * @author Dvorak
 */
public class S_JugFiller extends IdleScript {
  int vialsFilled = 0;
  int fullVials = 0;
  int emptyVials = 0;

  final long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String[] parameters) {
    controller.displayMessage("@red@VialFiller by Dvorak. Let's party like it's 2004!");
    controller.displayMessage("@red@Start in Falador West bank!");

    while (controller.isRunning()) {
      if (controller.getInventoryItemCount(140) > 0) {
        controller.useItemIdOnObject(327, 545, 140);
        controller.sleep(618);

        while (controller.isBatching()) controller.sleep(10);
      } else {
        openDoor();
        bank();
        openDoor();
      }
    }

    return 1000; // start() must return a int value now.
  }

  public void openDoor() {
    int[] coords = null;
    do {
      coords = controller.getNearestObjectById(64);
      if (coords != null) {
        controller.atObject(coords[0], coords[1]);
        controller.sleep(618);
      }
    } while (coords != null);
  }

  public void bank() {
    controller.setStatus("@blu@Banking..");

    controller.openBank();

    vialsFilled += controller.getInventoryItemCount(141);

    while (controller.isInBank() && controller.getInventoryItemCount(141) > 0) {
      controller.depositItem(141, controller.getInventoryItemCount(141));
      controller.sleep(100);
    }

    while (controller.isInBank() && controller.getInventoryItemCount(140) < 1) {
      controller.withdrawItem(140, 30 - controller.getInventoryItemCount());
      controller.sleep(100);
    }

    fullVials = controller.getBankItemCount(141);
    emptyVials = controller.getBankItemCount(140);
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int vialsPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        vialsPerHr = (int) (vialsFilled * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 160, 21 + 14 + 14 + 14, 0x0000FF, 48);
      controller.drawString("@blu@VialFiller @whi@by @blu@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@blu@Vials filled: @whi@"
              + String.format("%,d", vialsFilled)
              + " @blu@(@whi@"
              + String.format("%,d", vialsPerHr)
              + "@blu@/@whi@hr@blu@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@blu@Full vials in bank: @whi@" + String.format("%,d", fullVials),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@blu@Empty vials in bank: @whi@" + String.format("%,d", emptyVials),
          10,
          21 + 14 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
}
