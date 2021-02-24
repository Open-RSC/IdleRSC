package callbacks;

import bot.Main;
import controller.Controller;
import scripting.idlescript.IdleScript;

import java.util.Locale;

public class CommandCallback {

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
