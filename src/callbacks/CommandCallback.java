package callbacks;

import bot.Main;
import controller.Controller;
import scripting.idlescript.IdleScript;

import java.util.Locale;

public class CommandCallback {
	
	private static String helpMessageText = "@red@IdleRSC @yel@Help:"
			+ "% @red@::bothelp -- @yel@Shows this message"
			+ "% @red@::show -- @yel@Unhides the bot sidepane"
			+ "% @red@::gfx -- @yel@toggle graphics"
			+ "% @red@::hidepaint -- @yel@turn off bot painting (progress reports, xp counter, etc)"
			+ "% @red@::showpaint -- @yel@turn on bot painting (progress reports, xp counter, etc)"
			+ "%"
			+ " %"
			+ " %"
			+ " %@red@F11 -- @yel@Stop the current script and load a new one"
			+ " %@red@F12 -- @yel@Show help menu"
			+ " %"
            + " %"
            + " %"
            + " %"
			+ "@red@IdleRSC @yel@by @red@Dvorak @yel@2021";

    public static void commandHook(String command) {
        Controller c = Main.getController();
        command = command.toLowerCase();

        if(command.equals("show")) {
            Main.showBot();
        } else if(command.equals("hidepaint")) {
            if(c != null) {
                c.setBotPaint(false);
                c.displayMessage("@red@IdleRSC@yel@: Paint hidden.");
            }
        } else if(command.equals("showpaint")) {
            if(c != null) {
                c.setBotPaint(true);
                c.displayMessage("@red@IdleRSC@yel@: Paint unhidden.");
            }
        } else if(command.equals("gfx")) {
            if(c != null) {
                c.setDrawing(!c.isDrawEnabled());
            }
        } else if(command.equals("bothelp")) {
        	if(c != null) {
        		c.setServerMessage(helpMessageText, true, true);
        	}
        } else {
            //pass to script
            if(c != null && c.getShowBotPaint() == true && c.isRunning() && Main.getCurrentRunningScript() != null) {
                if(Main.getCurrentRunningScript() instanceof IdleScript) {
                    ((IdleScript)Main.getCurrentRunningScript()).chatCommandInterrupt(command);
                }
            }
        }
    }
}
