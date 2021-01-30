package listeners;

import bot.Main;
import controller.Controller;

/**
 * PositionListener constantly updates the current mouse and character position on the side panel. 
 * 
 * PositionListener is always running, and it runs as a separate thread from the main bot.
 * 
 * @author Dvorak
 *
 */
public class PositionListener implements Runnable {
	private Controller controller;
	
	public PositionListener(Controller _controller) {
		controller = _controller;
	}
	
	@Override
	public void run() {
		while(true) {
			Main.updatePosnStatus(controller.currentX(), controller.currentZ());
			Main.updateMouseStatus(controller.currentMouseX(), controller.currentMouseY());
	    	controller.sleep(40);
		}
	}	

}
