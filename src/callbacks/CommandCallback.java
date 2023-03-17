package callbacks;

import bot.Main;
import controller.Controller;
import scripting.idlescript.IdleScript;


public class CommandCallback {

	private static String helpMessageText =
                "@cya@IdleRSC Help Menu:"
			+ " %@red@::bothelp - @yel@Shows this help menu"
			+ " %@red@::show - @yel@Unhides the bot sidepane"
			+ " %@red@::gfx - @yel@toggle graphic rendering"
            + " %@red@::screenshot - @yel@Take a Screenshot"
			+ " %@red@::hidepaint or ::showpaint - @yel@Toggle Paint Left-Side Menu"
			+ " %@red@::toggleid - @yel@Toggle Item/Object/NPC right click Id's"
            + " %@red@F1  - @yel@Toggle Interlacing Mode"
            + " %@red@F2  - @yel@Toggle Openrsc Left-Side Sub Menu"
            + " %@red@F3  - @yel@Returns Camera Zoom to Default level"
			+ " %@red@F4  - @yel@Toggles 1st/3rd Person Perspective"
			+ " %@red@F5  - @yel@Attack Item Swapping. See Readme for Instructions"
			+ " %@red@F6 - @yel@Strength Item Swapping. See Readme for Instructions"
			+ " %@red@F7 - @yel@Defense Item Swapping. See Readme for Instructions"
			+ " %@red@F8 - @yel@Spell ID casting. See Readme for Instructions"
            + " %@red@F9 - @yel@Take a Screenshot, saved to ./IdleRSC/Screenshots/accountName"
            + " %@red@F10 - @yel@Lock the client's camera position till F10 or mouse click."
            + " %@red@F11 - @yel@Stop the current script and load a new one"
			+ " %@red@F12 - @yel@Show this help menu";

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
        } else if(command.equals("toggleid")) {
            if(c != null) {
                c.fakeDeveloper();
            }
        } else if(command.equals("screenshot")) {
            if(c != null) {
                c.takeScreenshot("");
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
