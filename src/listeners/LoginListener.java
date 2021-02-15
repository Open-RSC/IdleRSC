package listeners;

import bot.Main;
import controller.Controller;

import java.util.Random;

/**
 * LoginListener is a listener which will log the user back in upon logout.
 * 
 * LoginListener is always running, and it runs as a separate thread from the main bot.
 * 
 * @author Dvorak
 *
 */
public class LoginListener implements Runnable {

	private Controller controller;

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
						Thread.sleep(5000);

						if (controller.isLoggedIn() == false) {
							int sleepTime = (int) (Math.random() * (30000)) + 30000;
							controller.log("Looks like we could not login... trying again in " + String.valueOf(sleepTime) + " ms...");
							Thread.sleep(sleepTime);
						}

					}
				}

				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}	
}
