package listeners;

import bot.Main;
import controller.Controller;

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
		while(true) {
	    	if(controller.isLoggedIn() == false)
	    		Main.updateStatus("Logged out.");
	    	else
	    		if(Main.isRunning() == true)
	    			Main.updateStatus("Running.");
	    		else
	    			Main.updateStatus("Logged in, idle.");
	    	
	    	if(Main.isAutoLogin() == true) { 
	    		if(controller.isLoggedIn() == false) {
	    			controller.login();
	    			controller.sleep(1000);
	    		}
	    	}
	    	
	    	controller.sleep(40);
		}
	}	
}
