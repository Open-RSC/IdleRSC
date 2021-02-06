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
	Random random;
	private int recentLoginAttempts;
	private long lastRandomDecrease = 0;

	public LoginListener(Controller _controller) {
		random = new Random();
		controller = _controller;
	}

	@Override
	public void run() {
		while (true) {
			if (recentLoginAttempts > 0 && System.currentTimeMillis() - lastRandomDecrease > 2500 && random.nextFloat() < 0.5) {
				// decrease randomly every ~5 seconds
				recentLoginAttempts--;
				lastRandomDecrease = System.currentTimeMillis();
			}

	    	if(!controller.isLoggedIn()) {
				Main.updateStatus("Logged out.");
			}
	    	else {
				if(Main.isRunning()) {
					Main.updateStatus("Running.");
				}
				else {
					Main.updateStatus("Logged in, idle.");
				}
			}

	    	if(Main.isAutoLogin()) {
	    		if(!controller.isLoggedIn()) {
	    			controller.login();
					// Used for mass DCs not locking your bots out when 12 bot clients spam login
	    			controller.sleep((recentLoginAttempts*2 + 1) * 1000);
	    			recentLoginAttempts ++;
	    		}
	    	}
	    	
	    	controller.sleep(40);
		}
	}	
}
