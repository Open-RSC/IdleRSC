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
  private static long loginCount = 0;
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
            controller.log("Logged out! Logging back in...");
            controller.login();
            controller.sleep(640);
            /*
             * Math.random returns value between 0.0 and 1.0
             *
             * <p>for the following: (Math.random() * (30000)) + 30000; thus Max Sleep was 60
             * seconds and Min Sleep was 30 seconds
             *
             * <p>This MIGHT be causing connection issues with extended downtimes and large bot
             * counts on one computer.
             *
             * <p>Suggested Values calculated with Excel Spreadsheet, Available from Kaila at
             * request. Suggested change: Math.random() * 30000) + (((i * 15)/(i + 60)) * 30000) +
             * 30000
             *
             * <p>bot will slowly ramp up to 5 min delays after several hrs of no reconnect, anding
             * the cycle after 12-14 hrs and repeating
             *
             * <p>First 5 tries - Original Behavior, Min Sleep is 30 seconds and Max Sleep is 60
             * seconds After 6 tries (After 2.5 to 5 mins) - Min Sleep is 57 seconds and Max Sleep
             * is 87 seconds (0.95 to 1.45 mins each cycle) After 15 tries (After 11 to 18 mins) -
             * Min Sleep is 90 seconds and Max Sleep is 120 seconds (1.5 to 2 mins each cycle) After
             * 30 tries (After 33.5 to 48 mins) - Min Sleep is 130 seconds and Max Sleep is 160
             * seconds (2.1 to 2.7 mins each cycle) After 60 tries (After 1.5 hrs to 2 hrs) - Min
             * Sleep is 180 seconds and Max Sleep is 210 seconds (3 to 3.5 mins each cycle) After
             * 120 tries (After 4.6 hrs to 5.5 hrs) - Min Sleep is 230 seconds and Max Sleep is 260
             * seconds (3.8 to 4.3 mins each cycle) After 240 tries (After 12.2 hrs to 14 hrs) - Min
             * Sleep is 270 seconds and Max Sleep is 300 seconds (4.5 to 5 mins each cycle)
             *
             * <p>then it resets timer and repeats ramping up cycle...
             *
             * <p>~ Kaila ~
             */
            if (!controller.isLoggedIn()) {
              int i = (int) loginCount;
              int sleepTime = (int) ((Math.random() * 20000) + 60000);
              if (loginCount > 10) {
                sleepTime = sleepTime + (((i * 30) / (i + 60)) * 30000);
              }
              int sleepTimeInSeconds = sleepTime / 1000;
              controller.log(
                  "Looks like we could not login... trying again in "
                      + sleepTimeInSeconds
                      + " seconds...",
                  "cya");
              controller.sleep(sleepTime);
              loginCount++;
            } else loginCount = 0;
          }
        }

        if (controller.getMoveCharacter()) {
          moveCharacter();
          controller.charactedMoved();
        }
        if (controller.isLoggedIn() && loginCount > 0) {
          loginCount = 0;
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

    if (c.isReachable(x + 1, y, false)) c.walkTo(x + 1, y, 0, false);
    else if (c.isReachable(x - 1, y, false)) c.walkTo(x - 1, y, 0, false);
    else if (c.isReachable(x, y + 1, false)) c.walkTo(x, y + 1, 0, false);
    else if (c.isReachable(x, y - 1, false)) c.walkTo(x, y - 1, 0, false);

    c.sleep(1280);

    c.walkTo(x, y, 0, false);
  }
}
