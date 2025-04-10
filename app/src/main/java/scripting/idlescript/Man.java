package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import orsc.ORSCharacter;

public class Man extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.THIEVING, Category.IRONMAN_SUPPORTED},
          "G-unit",
          "Thieves men. Supports batching.");

  private int successfulPickpockets;
  private int unsuccessfulPickpockets;
  private long startTime = 0L;
  final int paintGapY = 13; // Gap between paint text lines

  public void thieve() {
    final ORSCharacter manToThieve = controller.getNearestNpcById(11, false);
    controller.thieveNpc(manToThieve.serverIndex);
    controller.waitForBatching(false);
  }

  public void run() {
    while (controller.isInCombat()) {
      controller.walkTo(controller.currentX(), controller.currentY());
      controller.sleep(500);
    }
  }

  public void checkSleep() {
    if (controller.getFatigue() >= 90 && controller.isRunning()) {
      while (!controller.isSleeping() && controller.isRunning()) {
        controller.itemCommand(1263);
        controller.sleep(2500);
      }
      while (controller.isSleeping() && controller.isRunning()) {
        controller.sleep(100);
      }
    }
  }

  public int start(String[] param) {
    controller.displayMessage("@red@G-unit's Man Thiever plesh", 3);

    startTime = System.currentTimeMillis();
    successfulPickpockets = 0;
    unsuccessfulPickpockets = 0;

    while (controller.isRunning()) {
      thieve();
      run();
      checkSleep();
    }
    return 1000; // start() must return a int value now.
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.equals("You pick the man's pocket")) {
      successfulPickpockets++;
    } else if (message.contains("You fail to pick the man's pocket")) {
      unsuccessfulPickpockets++;
    }

    super.serverMessageInterrupt(message);
  }

  @Override
  public void paintInterrupt() {
    if (controller == null || !controller.isRunning()) {
      return;
    }

    int x = 10;
    int y = 25;

    controller.drawString("@gre@Man Thiever @whi@by @yel@G-Unit", x, y, 0xFFFFFF, 1);

    y += paintGapY;

    controller.drawString("Successful pickpockets: " + successfulPickpockets, x, y, 0xFFFFFF, 1);

    y += paintGapY;

    controller.drawString(
        "Unsuccessful pickpockets: " + unsuccessfulPickpockets, x, y, 0xFFFFFF, 1);

    y += paintGapY;
    final int totalPickpockets = unsuccessfulPickpockets + successfulPickpockets;
    controller.drawString("Total pickpockets: " + totalPickpockets, x, y, 0xFFFFFF, 1);

    if (successfulPickpockets > 0) {
      y += paintGapY;

      final float successRate = (successfulPickpockets * 100f / (totalPickpockets));
      controller.drawString(
          "Success Rate: %" + String.format("%.2f", successRate), x, y, 0xFFFFFF, 1);
    }

    y += paintGapY;

    final long timeRunning = System.currentTimeMillis() - startTime;
    controller.drawString("Time Running: " + controller.msToString(timeRunning), x, y, 0xFFFFFF, 1);
  }
}
