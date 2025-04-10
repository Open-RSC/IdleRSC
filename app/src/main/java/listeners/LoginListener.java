package listeners;

import controller.Controller;

/**
 * LoginListener is a listener which will log the user back in upon logout.
 *
 * <p>LoginListener is always running, and it runs as a separate thread from the main bot.
 *
 * @author Dvorak and Kaila
 */
/*
 * TODO: Only spawn this thread when autologin is turned on, otherwise kill thread!
 */
public class LoginListener implements Runnable {
  private double loginCount;
  private double sleepTime;
  private final Controller controller;

  public LoginListener(Controller controller) {
    this.controller = controller;
    sleepTime = System.currentTimeMillis();
  }

  @Override
  public void run() {
    try {
      while (true) {

        Thread.sleep(5000);

        // Thread is not needed, long sleep
        if (!controller.isAutoLogin() || controller.isLoggedIn()) {
          loginCount = 0;
          Thread.sleep(15000);
          continue;
        }
        if (System.currentTimeMillis() <= sleepTime) { // waiting to login again, short sleep
          continue;
        }

        controller.log("Logged out! Logging back in...");
        controller.login();
        loginCount++;

        // short wait, 30s to 60s in all cases
        if (loginCount < 3) sleepTime = System.currentTimeMillis() + 30000 + Math.random() * 30000;
        // long wait, 5 mins to 15 mins, scaling up with more attempts
        else
          sleepTime =
              System.currentTimeMillis() + 240_000 + loginCount * 20000 + Math.random() * 60000;
        // reset counter if sleep time gets longer than 15 mins (starts again at ~5 mins)
        if (sleepTime > 900_000) loginCount = 3;

        if (!controller.isLoggedIn()) {
          controller.log(
              "Looks like we could not log in... trying again in "
                  + (int) ((sleepTime - System.currentTimeMillis()) / 1000)
                  + " seconds...",
              "cya");
        }
      }
    } catch (InterruptedException e) {
      // Restore interrupted status
      Thread.currentThread().interrupt();
    }
  }
}
