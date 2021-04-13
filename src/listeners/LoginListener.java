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
						
						int ticks = 0;
						while(ticks < 50) {
							if(controller.isLoggedIn()) {
								controller.hideWelcomeMessage();
								break;
							}
							controller.sleep(100);
							ticks++;
						}
						
						
						if (controller.isLoggedIn() == false) {
							int sleepTime = (int) (Math.random() * (30000)) + 30000;
							controller.log("Looks like we could not login... trying again in " + String.valueOf(sleepTime) + " ms...");
							Thread.sleep(sleepTime);
						}

					}
				}

				if(controller.getMoveCharacter()) {
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

		if(c.isReachable(x + 1, y, true))
			c.walkTo(x+1, y, 0, false);
		else if(c.isReachable(x - 1, y, true))
			c.walkTo(x - 1, y, 0, false);
		else if(c.isReachable(x, y+1, true))
			c.walkTo(x, y+1,0 , false);
		else if(c.isReachable(x, y-1, true))
			c.walkTo(x, y-1,0, false);

		c.sleep(1000);

		c.walkTo(x, y, 0, false);
	}
}
