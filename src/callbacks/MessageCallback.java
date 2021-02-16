package callbacks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bot.Main;
import compatibility.sbot.Script;
import controller.Controller;
import orsc.enumerations.MessageType;
import scripting.idlescript.IdleScript;

public class MessageCallback {

    private static int sbotLastChatter = 0;
    private static String sbotLastChatterName = "";
    private static String sbotLastChatMessage = "";
    private static String sbotLastNPCMessage = "";
    private static String sbotLastServerMessage = "";
    
    private static final Pattern p = Pattern.compile("^(.*) (.*) level!"); //for parsing level up messages

    public static void messageHook(boolean crownEnabled, String sender, String message, MessageType type, int crownID,
                                   String formerName, String colourOverride) {
    	
        if (type == MessageType.GAME) {
        	if(message.contains("You just advanced")) {
        		handleLevelUp(message);
        	}
        }
        
        if (Main.isRunning() && Main.getCurrentRunningScript() != null) {
            if (Main.getCurrentRunningScript() instanceof IdleScript) {
            	if (type == MessageType.GAME) {
                    ((IdleScript) Main.getCurrentRunningScript()).serverMessageInterrupt(message);
                } else if (type == MessageType.CHAT) {
                    ((IdleScript) Main.getCurrentRunningScript()).chatMessageInterrupt(sender + ": " + message);
                } else if (type == MessageType.QUEST) {
                    ((IdleScript) Main.getCurrentRunningScript()).questMessageInterrupt(message);
                } else if (type == MessageType.TRADE) {
                    ((IdleScript) Main.getCurrentRunningScript()).tradeMessageInterrupt(message);
                }
            } else if (Main.getCurrentRunningScript() instanceof Script) {
                if (type == MessageType.GAME) {
                    ((Script) Main.getCurrentRunningScript()).ServerMessage(message);
                    sbotLastServerMessage = message;
                } else if (type == MessageType.CHAT) {
                    ((Script) Main.getCurrentRunningScript()).ChatMessage(sender + ": " + message);

                    sbotLastChatter = (int)(System.currentTimeMillis() / 1000L); //2038 problem, but that's what you get for using sbot scripts in 2038.
                    sbotLastChatMessage = message;
                    sbotLastChatterName = sender;
                } else if (type == MessageType.QUEST) {
                    ((Script) Main.getCurrentRunningScript()).NPCMessage(message);
                    sbotLastNPCMessage = message;
                }
                //TODO: Implement trade for SBot per SBot's contract functions.
            }
        }
    }
	
	private static void handleLevelUp(String message) {
		Controller c = Main.getController();
		String skillName = null;
		int skillLevel = -1;
		
		Matcher m = p.matcher(message);
		
		if(m.find()) {
			skillName = m.group(2);
			if(skillName != null && skillName.length() > 1) {
				if(skillName.toLowerCase().equals("woodcut"))
					skillName = "Woodcutting";
				
				skillName = Character.toUpperCase(skillName.charAt(0)) + skillName.substring(1);
				
				skillLevel = c.getBaseStat(c.getStatId(skillName));
				
				DrawCallback.displayAndScreenshotLevelUp(skillName, skillLevel); 
			}
		}
		
	}

	public static int getSbotLastChatter() {
        return sbotLastChatter;
    }

    public static String getSbotLastChatterName() {
        return sbotLastChatterName;
    }

    public static String getSbotLastChatMessage() {
        return sbotLastChatMessage;
    }

    public static String getSbotLastNPCMessage() {
        return sbotLastNPCMessage;
    }

    public static String getSbotLastServerMessage() {
        return sbotLastServerMessage;
    }
}