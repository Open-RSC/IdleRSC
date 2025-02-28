package scripting.idlescript;

import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;

public class AgilityNet extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.AGILITY, Category.IRONMAN_SUPPORTED},
          "Flop",
          "Gnome Agility Net - Flop");

  private long startTime;
  private int jumpsMade;
  final int paintGapY = 13; // Gap between paint text lines

  public void interactObstacle() {
    controller.atObject(683, 502);
    controller.sleep(618);
  }

  public void checkSleep() {
    if (controller.getFatigue() >= 95 && controller.isRunning()) {
      while (!controller.isSleeping() && controller.isRunning()) {
        controller.itemCommand(1263);
        controller.sleep(2500);
      }
      while (controller.isSleeping() && controller.isRunning()) {
        controller.sleep(100);
      }
    }
  }

  public int start(String[] params) {
    controller.displayMessage("Gnome Agility Net - Flop", 3);

    startTime = System.currentTimeMillis();
    jumpsMade = 0;

    while (controller.isRunning()) {
      interactObstacle();
      checkSleep();
    }

    controller.displayMessage("@red@STOPPED", 3);
    return 1000;
  }

  @Override
  public void paintInterrupt() {
    if (controller == null || !controller.isRunning()) {
      return;
    }

    int x = 10;
    int y = 25;

    controller.drawString("@gre@Gnome Agility Net @whi@by @yel@Flop", x, y, 0xFFFFFF, 1);

    y += paintGapY;
    controller.drawString(
        "Jumps Made: @gre@" + jumpsMade + " @whi@(@gre@" + jumpsPerHour() + " @whi@jumps/hr)",
        x,
        y,
        0xFFFFFF,
        1);

    y += paintGapY;
    controller.drawString(
        "Time Running: @gre@" + controller.msToString(timeRunningMs()), x, y, 0xFFFFFF, 1);
  }

  public long timeRunningMs() {
    return System.currentTimeMillis() - startTime;
  }

  public long jumpsPerHour() {
    long jumpsPerHour = 0L;

    try {
      float timeRan = timeRunningMs() / 1000f;
      float scale = (60 * 60) / timeRan;
      jumpsPerHour = (int) (jumpsMade * scale);
    } catch (Exception e) {
    }

    return jumpsPerHour;
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.equals("and run towards the net")) {
      jumpsMade++;
    }
  }
}
