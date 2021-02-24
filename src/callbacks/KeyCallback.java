package callbacks;


import bot.Main;
import compatibility.sbot.Script;
import controller.Controller;
import scripting.idlescript.IdleScript;

import java.awt.event.KeyEvent;

public class KeyCallback {
    public static void keyHook(KeyEvent key) {
        Controller c = Main.getController();
        char keyChar = key.getKeyChar();
        int keycode = key.getKeyCode();

        if(keycode == KeyEvent.VK_F11) {
            if(Main.isRunning())
                c.displayMessage("@red@IdleRSC@yel@: SCRIPT STOPPED");

            Main.setRunning(false);
            Main.showLoadScript();
        } else if(keycode == KeyEvent.VK_F12) {
            if(c != null) {
                c.chatMessage("::bothelp");
            }
        } else {
            if(c != null && c.getShowBotPaint() == true && c.isRunning() && Main.getCurrentRunningScript() != null) {
                if(Main.getCurrentRunningScript() instanceof IdleScript) {
                    ((IdleScript)Main.getCurrentRunningScript()).keyPressInterrupt(keycode);
                }
                if(Main.getCurrentRunningScript() instanceof Script) {
                    ((Script)Main.getCurrentRunningScript()).KeyPressed(keycode);
                }
            }
        }

    }
}
