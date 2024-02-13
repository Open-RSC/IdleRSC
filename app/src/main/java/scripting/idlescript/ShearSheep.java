package scripting.idlescript;

/**
 * ShearSheep by Searos
 *
 * @author Searos
 */
public class ShearSheep extends IdleScript {
  int startX = -1;
  int startY = -1;
  int totalWool = 0;
  int bankedWool = 0;
  int a = 0;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (a == 0) {
      controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
      a = 1;
    }
    startX = controller.currentX();
    startY = controller.currentY();
    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }
  /**
   * Starts walking to the specified coordinates.
   *
   * @param x the x-coordinate to walk to
   * @param y the y-coordinate to walk to
   */
  public void startWalking(int x, int y) {
    // shitty autowalk
    int newX = x;
    int newY = y;
    while (controller.currentX() != x || controller.currentY() != y) {
      if (controller.currentX() - x > 23) {
        newX = controller.currentX() - 20;
      }
      if (controller.currentY() - y > 23) {
        newY = controller.currentY() - 20;
      }
      if (controller.currentX() - x < -23) {
        newX = controller.currentX() + 20;
      }
      if (controller.currentY() - y < -23) {
        newY = controller.currentY() + 20;
      }
      if (Math.abs(controller.currentX() - x) <= 23) {
        newX = x;
      }
      if (Math.abs(controller.currentY() - y) <= 23) {
        newY = y;
      }
      if (!controller.isTileEmpty(newX, newY)) {
        controller.walkToAsync(newX, newY, 2);
        controller.sleep(640);
      } else {
        controller.walkToAsync(newX, newY, 0);
        controller.sleep(640);
      }
    }
  }

  public void scriptStart() {
    while (controller.getInventoryItemCount() < 30 && controller.getInventoryItemCount(144) == 1) {
      controller.setStatus("Returning to start");
      if (controller.getNearestNpcById(2, false) != null) {
        controller.setStatus("Sheep found");
        controller.useItemOnNpc(controller.getNearestNpcById(2, false).serverIndex, 144);
        controller.setStatus("Shearing sheep");
        controller.sleep(642);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30) {
          controller.sleep(640);
        }
      } else if (controller.getNearestNpcById(2, false) == null) {
        controller.setStatus("Finding sheep");
        startWalking(startX, startY);
      }
    }
    while (controller.getInventoryItemCount() == 30 || controller.getInventoryItemCount(144) != 1) {
      controller.setStatus("Need to bank");
      while (controller.getNearestNpcById(95, false) == null
              && controller.getInventoryItemCount() == 30
          || controller.getNearestNpcById(95, false) == null
              && controller.getInventoryItemCount(144) != 1) {
        controller.setStatus("Walking to bank");
        startWalking(controller.getNearestBank()[0], controller.getNearestBank()[1]);
      }
      while (controller.getNearestNpcById(95, false) != null
              && !controller.isInBank()
              && controller.getInventoryItemCount() == 30
          || controller.getNearestNpcById(95, false) != null
              && controller.getInventoryItemCount(144) != 1
              && !controller.isInBank()) {
        controller.setStatus("Banking");
        controller.openBank();
        controller.sleep(640);
      }
      if (controller.isInBank() && controller.getInventoryItemCount() == 30
          || controller.isInBank() && controller.getInventoryItemCount(144) != 1) {
        for (int itemId : controller.getInventoryItemIds()) {
          totalWool = totalWool + controller.getInventoryItemCount(145);
          if (itemId != 0 && itemId != 144) {
            controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
            controller.sleep(100);
          }
        }
        bankedWool = controller.getBankItemCount(145);
        if (controller.getInventoryItemCount(144) != 1) {
          controller.withdrawItem(144);
          controller.sleep(640);
        }
      }
      controller.closeBank();
      controller.setStatus("Closing Bank");
    }
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      controller.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@Shear Sheep @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      controller.drawString("@red@Wool collected: @yel@" + this.totalWool, 10, 35, 0xFFFFFF, 1);
      controller.drawString("@red@Wool in Bank: @yel@" + this.bankedWool, 10, 49, 0xFFFFFF, 1);
    }
  }
}
