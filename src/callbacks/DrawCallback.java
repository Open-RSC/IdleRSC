package callbacks;

import bot.Main;
import controller.Controller;
import scripting.idlescript.IdleScript;

public class DrawCallback {

    private static long startTimestamp = System.currentTimeMillis() / 1000L;
    private static long startingXp = Long.MAX_VALUE;




    public static void drawHook() {
        Controller c = Main.getController();

        drawBotStatus(c);
        drawScript(c);

    }

    private static void drawBotStatus(Controller c) {
        int y = 130 + 14 + 14 + 14;
        String statusText = "Idle.";
        if(Main.isRunning())
            statusText = "Botting!";
        c.drawString("Status: @red@" + statusText, 7, y, 0xFFFFFF, 1);

        y+= 14;
        c.drawString("Coords: @red@(@whi@" + String.valueOf(c.currentX()) + "@red@,@whi@" + String.valueOf(c.currentZ()) + "@red@)", 7, y, 0xFFFFFF, 1);

        y += 14;
        long totalXp = getTotalXp();
        startingXp = totalXp < startingXp ? totalXp : startingXp;
        long xpGained = totalXp - startingXp;
        long xpPerHr;
        try {
            xpPerHr = (xpGained / (((System.currentTimeMillis() / 1000L) - startTimestamp))) * (60*60);
        }
        catch(Exception e) {
            xpPerHr = 0;
        }
        c.drawString("XP Gained: @red@" + String.format("%,d", xpGained)
                       + " @whi@(@red@" + String.format("%,d", xpPerHr) + " @whi@xp/hr)", 7, y, 0xFFFFFF, 1);
    }

    private static void drawScript(Controller c) {

        if(Main.isRunning() && Main.getCurrentRunningScript() != null) {
            if(Main.getCurrentRunningScript() instanceof IdleScript) {
                ((IdleScript)Main.getCurrentRunningScript()).paintInterrupt();
            }
        }
    }

    private static long getTotalXp() {
        Controller c = Main.getController();

        long result = 0;

        for(int statIndex = 0; statIndex < c.getStatCount(); statIndex++) {
            result += c.getStatXp(statIndex);
        }

        return result;

    }

/**
 * 			if(Main.isRunning() && Main.getCurrentRunningScript() != null) {
 * 				if(controller.getMessages() != null && controller.getMessages().size() > 0) {
 * 					ORSCMessage message = controller.getMessages().get(0);
 *
 * 					if(!message.equals(previousMessage)) {
 * 						if(Main.getCurrentRunningScript() instanceof IdleScript) {
 * 							if(message.getType() == MessageType.GAME) {
 * 								((IdleScript)Main.getCurrentRunningScript()).serverMessageInterrupt(message.getMessage());
 *                                                        } else if(message.getType() == MessageType.CHAT) {
 * 								((IdleScript)Main.getCurrentRunningScript()).chatMessageInterrupt(message.getSender() + ": " + message.getMessage());
 *                            } else if(message.getType() == MessageType.QUEST) {
 * 								((IdleScript)Main.getCurrentRunningScript()).npcMessageInterrupt(message.getMessage());
 *                            } else if(message.getType() == MessageType.TRADE) {
 * 								((IdleScript)Main.getCurrentRunningScript()).tradeMessageInterrupt(message.getMessage());
 *                            }* 						} else if(Main.getCurrentRunningScript() instanceof Script) {
 * 							if(message.getType() == MessageType.GAME) {
 * 								System.out.println(message.getMessage());
 * 								((Script)Main.getCurrentRunningScript()).ServerMessage(message.getM                            );
 * 							} else if(message.getType() == MessageType.CHAT) {
 * 								((Script)Main.getCurrentRunningScript()).ChatMessage(message.getSender() + ": " + message.getM                            );
 * 							} else if(message.getType() == MessageType.QUEST) {
 * 								((Script)Main.getCurrentRunningScript()).NPCMessage(message.getM                            );
 * 							}
 * 							//TODO: Implement trade for SBot per SBot's contract                         ns.
 * 						}
 * 						previousMessage                     age;
 *                }
 *            }
 * 			}
 */
}
