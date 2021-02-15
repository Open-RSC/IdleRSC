package listeners;

import bot.Main;
import compatibility.sbot.Script;
import controller.Controller;
import controller.ORSCMessage;
import orsc.enumerations.MessageType;
import scripting.idlescript.IdleScript;

/**
 * MessageListener listens for messages coming in to the client and then sends it to the respective function interrupt inside the current running script.
 * 
 * MessageListener is always running, and it runs as a separate thread from the main bot.
 * 
 * This code is bad but it works. 
 * The problem is, it plays a game of chance whether or not it is actually grabbing the latest message.
 * All it does right now is a loop where it grab the newest message presented to the user and then compares it to the previous one grabbed.
 * If 2 messages arrive at the exact same time (I'm not sure if this is possible on OpenRSC), it will only see one of them.
 * If you would like to revise the below code, please respond to the issue on Gitlab. 
 * 
 * @author Dvorak
 *
 */
public class MessageListener implements Runnable {

	private Controller controller = null;
	
	private ORSCMessage previousMessage = new ORSCMessage();
	
	public MessageListener(Controller _controller) {
		controller = _controller;
	}
	
	@Override
	public void run() {
		while(true) {
			
			if(Main.isRunning() && Main.getCurrentRunningScript() != null) {
				if(controller.getMessages() != null && controller.getMessages().size() > 0) {
					ORSCMessage message = controller.getMessages().get(0);
					
					if(!message.equals(previousMessage)) {
						if(Main.getCurrentRunningScript() instanceof IdleScript) {
							if(message.getType() == MessageType.GAME) {
								((IdleScript)Main.getCurrentRunningScript()).serverMessageInterrupt(message.getMessage());
							} else if(message.getType() == MessageType.CHAT) {
								((IdleScript)Main.getCurrentRunningScript()).chatMessageInterrupt(message.getSender() + ": " + message.getMessage());
							} else if(message.getType() == MessageType.QUEST) {
								((IdleScript)Main.getCurrentRunningScript()).npcMessageInterrupt(message.getMessage());
							} else if(message.getType() == MessageType.TRADE) {
								((IdleScript)Main.getCurrentRunningScript()).tradeMessageInterrupt(message.getMessage());
							}
						} else if(Main.getCurrentRunningScript() instanceof Script) {
							if(message.getType() == MessageType.GAME) {
								System.out.println(message.getMessage());
								((Script)Main.getCurrentRunningScript()).ServerMessage(message.getMessage());
							} else if(message.getType() == MessageType.CHAT) {
								((Script)Main.getCurrentRunningScript()).ChatMessage(message.getSender() + ": " + message.getMessage());
							} else if(message.getType() == MessageType.QUEST) {
								((Script)Main.getCurrentRunningScript()).NPCMessage(message.getMessage());
							} 
							//TODO: Implement trade for SBot per SBot's contract functions.
						}
						previousMessage = message;
					}
				}		
			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}	
}
