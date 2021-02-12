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
		while (true) {
			
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
	    			controller.log("Logged out! Logging back in...");
	    			controller.login();
	    			controller.sleep(5000);
	    			
	    			if(controller.isLoggedIn() == false) {
	    				int sleepTime = (int) (Math.random() * (30000)) + 30000;
	    				controller.log("Looks like we could not login... trying again in " + String.valueOf(sleepTime) + " ms...");
	    				controller.sleep(sleepTime);
	    			}
					
	    		}
	    	}
	    	
	    	controller.sleep(1000);
		}
	}	
}
