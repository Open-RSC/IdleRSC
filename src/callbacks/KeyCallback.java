package callbacks;


import bot.Main;
import compatibility.sbot.Script;
import controller.Controller;
import scripting.idlescript.IdleScript;

import java.awt.event.KeyEvent;

public class KeyCallback {
	
	private static class SwitchThread implements Runnable {
		private Controller c;
		private boolean attack;
		private boolean strength;
		private boolean defence;
		
		public SwitchThread(Controller c, boolean attack, boolean strength, boolean defence) {
			this.c = c;
			this.attack = attack;
			this.strength = strength;
			this.defence = defence;
		}

		@Override
		public void run() {
			if(attack) {
				c.displayMessage("@red@IdleRSC@yel@: @gre@Attack switched!");
	        	c.equipItem(c.getInventoryItemSlotIndex(75));
	        	c.sleep(640);
	        	c.equipItem(c.getInventoryItemSlotIndex(597));
			} else if(strength) {
	        	c.displayMessage("@red@IdleRSC@yel@: @red@Strength switched!");
	        	c.equipItem(c.getInventoryItemSlotIndex(81));
	        	c.sleep(640);
	        	c.equipItem(c.getInventoryItemSlotIndex(316));
			}
		}
	}
	
    public static void keyHook(KeyEvent key) {
        Controller c = Main.getController();
        char keyChar = key.getKeyChar();
        int keycode = key.getKeyCode();

        if(keycode == KeyEvent.VK_F5) {
        	SwitchThread switchThread = new SwitchThread(c, true, false, false);
        	Thread t = new Thread(switchThread);
        	t.start();
        	
//        	c.displayMessage("@red@IdleRSC@yel@: @gre@Attack switched!");
//        	c.equipItem(c.getInventoryItemSlotIndex(75));
//        	c.sleep(640);
//        	c.equipItem(c.getInventoryItemSlotIndex(597));
        	return;
        } else if(keycode == KeyEvent.VK_F6) {
        	SwitchThread switchThread = new SwitchThread(c, false, true, false);
        	Thread t = new Thread(switchThread);
        	t.start();
//        	c.displayMessage("@red@IdleRSC@yel@: @red@Strength switched!");
//        	c.equipItem(c.getInventoryItemSlotIndex(81));
//        	c.sleep(640);
//        	c.equipItem(c.getInventoryItemSlotIndex(316));
        	return;
        } else if(keycode == KeyEvent.VK_F7) {
        	c.displayMessage("@red@IdleRSC@yel@: @cya@Defense switched!");
        } else  if(keycode == KeyEvent.VK_F11) {
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
