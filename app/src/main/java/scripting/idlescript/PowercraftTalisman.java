package scripting.idlescript;

/**
 * Trains mining and crafting at the essence rocks.
 *
 * @author Searos
 */
public class PowercraftTalisman extends IdleScript {
  int a = 0;
  int totalTalismans = 0;
  boolean dropping = false;
  long dropTimer;
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
    scriptStart();

    return 1000; // start() must return a int value now.
  }

  public static long getRemaining(long time) {
    if (isRunning(time)) {
      return (time - System.currentTimeMillis());
    }
    return 0;
  }

  public static boolean isRunning(long time) {
    return (System.currentTimeMillis() < time);
  }

  public static String toRemainingString(long time) {
    return format(getRemaining(time));
  }

  public static String format(long time) {
    StringBuilder t = new StringBuilder();
    long total_secs = time / 1000;
    long total_mins = total_secs / 60;
    long total_hrs = total_mins / 60;
    int secs = (int) total_secs % 60;
    int mins = (int) total_mins % 60;
    int hrs = (int) total_hrs % 60;
    if (hrs < 10) {
      t.append("0");
    }
    t.append(hrs);
    t.append(":");
    if (mins < 10) {
      t.append("0");
    }
    t.append(mins);
    t.append(":");
    if (secs < 10) {
      t.append("0");
    }
    t.append(secs);
    return t.toString();
  }

  public void scriptStart() {
    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (controller.getInventoryItemCount(1385) < 1 || getRemaining(dropTimer) <= 0) {
        dropping = false;
      }
      if (controller.getInventoryItemCount(1299) < 1
          && controller.getInventoryItemCount(1385) < 1) {
        if (!controller.isBatching()) {
          controller.setStatus("Preventing Logout");
          dropping = false;
          controller.walkTo(controller.currentX() + 1, controller.currentY());
          controller.walkTo(controller.currentX() - 1, controller.currentY());
        }
        while (controller.getInventoryItemCount() < 30 && !controller.isBatching()) {
          controller.setStatus("Mining");
          controller.atObject(691, 2);
          controller.sleep(1000);
        }
        while (controller.isBatching()) {
          controller.sleep(600);
        }
      }
      while (controller.getInventoryItemCount(1299) >= 1 && !controller.isBatching()
          || controller.getInventoryItemCount(1385) >= 1 && !controller.isBatching() && !dropping) {
        controller.setStatus("Crafting");
        controller.useItemOnItemBySlot(
            controller.getInventoryItemSlotIndex(167), controller.getInventoryItemSlotIndex(1299));
        totalTalismans = totalTalismans + controller.getInventoryItemCount(1299);
        controller.sleep(1200);
        if (controller.getInventoryItemCount(1385) > 0
            && controller.getInventoryItemCount(1299) < 1
            && !dropping) {
          controller.setStatus("Dropping");
          controller.dropItem(controller.getInventoryItemSlotIndex(1385));
          dropTimer = System.currentTimeMillis() + 15000;
          dropping = true;
          controller.sleep(1200);
        }
      }
    }
    a = 0;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      controller.drawBoxAlpha(7, 7, 173, 21 + 58, 0xFF0000, 64);
      controller.drawString("@red@Powercraft Talismans", 10, 21, 0xFFFFFF, 1);
      controller.drawString("@gre@by Searos & fixed by Damrau", 10, 35, 0xFFFFFF, 1);
      controller.drawString("@red@Talismans crafted: @yel@" + totalTalismans, 10, 49, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Drop timer: @yel@" + toRemainingString(dropTimer), 10, 64, 0xFFFFFF, 1);
      controller.drawString("@red@Dropping: @yel@" + dropping, 10, 79, 0xFFFFFF, 1);
    }
  }
}
