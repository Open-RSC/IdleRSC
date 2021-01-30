package listeners;

import bot.Main;
import controller.Controller;
import orsc.mudclient;
import reflector.Reflector;

/**
 * CommandListener listens for commands typed by the user on the main client. 
 * 
 * CommandListener is always running, and it runs as a separate thread from the main bot.
 * 
 * @author Dvorak
 *
 */
public class CommandListener implements Runnable {

	private mudclient mud;
	private Reflector reflector;
	private Controller controller;
	
	public CommandListener(mudclient _mud, Reflector _reflector, Controller _controller) {
		mud = _mud;
		reflector = _reflector;
		controller = _controller;
	}

	@Override
	public void run() {
		String lastInput = (String) reflector.getObjectMember(mud, "chatMessageInputCommit");
		String currentInput;
		while(controller.isLoggedIn()) { 
			
			currentInput = (String) reflector.getObjectMember(mud, "chatMessageInputCommit");
			
			if(lastInput.equals(currentInput) == false) {
				//get the difference
				
				String input = currentInput.substring(lastInput.length(), currentInput.length());
				if(input.startsWith("::")) {
					if(input.equals("::showbot")) {
						Main.showBot();
					}
				}
				//System.out.println("got input: "+ input);
				lastInput = currentInput;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
}
