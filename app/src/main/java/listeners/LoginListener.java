package listeners;

import bot.Main;
import controller.Controller;

/**
 * LoginListener is a listener which will log the user back in upon logout.
 *
 * <p>LoginListener is always running, and it runs as a separate thread from the main bot.
 *
 * @author Dvorak
 */
public class LoginListener implements Runnable {
  private static double loginCount = 0;
  private static double sleepTime = 0.0;
  private final Controller controller;

  public LoginListener(Controller _controller) {
    controller = _controller;
  }

  @Override
  public void run() {
    try {
      while (true) {

        if (Main.isAutoLogin()) {
          if (!controller.isLoggedIn()) {
            if (System.currentTimeMillis() > sleepTime) {
              controller.log("Logged out! Logging back in...");
              controller.login();
              loginCount++;
              controller.sleep(640);
              // calc the next login time
              if (loginCount > 10)
                sleepTime =
                    System.currentTimeMillis() + (((loginCount * 30) / (loginCount + 60)) * 20000);
              else
                sleepTime =
                    System.currentTimeMillis() + ((Math.random() * 20000 * loginCount) + 20000);
              if (!controller.isLoggedIn()) {
                controller.log(
                    "Looks like we could not login... trying again in "
                        + (int) ((sleepTime - System.currentTimeMillis()) / 1000)
                        + " seconds...",
                    "cya");
              }
            }
          } else if (loginCount != 0) loginCount = 0;
        }
        if (!controller.isAuthentic() && controller.getMoveCharacter()) {
          moveCharacter();
          controller.charactedMoved();
        }
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void moveCharacter() {
    Controller c = Main.getController();
    int x = c.currentX();
    int y = c.currentY();

    if (c.isReachable(x + 1, y, false)) c.walkToAsync(x + 1, y, 0);
    else if (c.isReachable(x - 1, y, false)) c.walkToAsync(x - 1, y, 0);
    else if (c.isReachable(x, y + 1, false)) c.walkToAsync(x, y + 1, 0);
    else if (c.isReachable(x, y - 1, false)) c.walkToAsync(x, y - 1, 0);

    c.sleep(1280);

    c.walkTo(x, y);
  }
}
