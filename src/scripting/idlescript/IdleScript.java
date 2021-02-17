package scripting.idlescript;

import controller.Controller;

/**
 * This is the base class from which all IdleScript scripts are derived; this class also contains interrupts called by patched client hooks.
 * 
 * 
 * @author Dvorak
 *
 */

public abstract class IdleScript {
	Controller controller = null;
	
	/**
	 * Called by {@link callbacks.MessageCallback} every time a new server message is drawn on the screen. <b>Override this in your script to process these messages.</b>
	 * @param message -- variable containing the message. 
	 */
	public void serverMessageInterrupt(String message) {
		
	}
	
	/**
	 * Called by {@link callbacks.MessageCallback} every time a new chat message is drawn on the screen. <b>Override this in your script to process these messages.</b>
	 * @param message -- variable containing the message. 
	 */
	public void chatMessageInterrupt(String message) { 
		
	}
	
	/**
	 * Called by {@link callbacks.MessageCallback} every time a new quest message is drawn on the screen. <b>Override this in your script to process these messages.</b>
	 * @param message -- variable containing the message. 
	 */
	public void questMessageInterrupt(String message) {
		
	}
	
	/**
	 * Called by {@link callbacks.MessageCallback} every time a new trade message is drawn on the screen. <b>Override this in your script to process these messages.</b>
	 * @param message -- variable containing the message. 
	 */
	public void tradeMessageInterrupt(String message) {
		
	}
	
	public void setController(Controller _controller) {
		controller = _controller;
	}
	
    public void start(String parameters[])
    {
    	System.out.println("If you see this, your script did not come with the start function.");
    }

	/**
	 * This will be called by {@link callbacks.DrawCallback} every game frame. <b>Override this in your script to paint on the screen.</b>
	 */
	public void paintInterrupt() {

	}
}
