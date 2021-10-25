package callbacks;

import bot.Main;
import controller.Controller;
import orsc.ORSCharacter;
import orsc.enumerations.ORSCharacterDirection;

public class DrawPlayerCallback {
	
	public static void drawPlayerCallback(int index, int x, int y, int topPixelSkew) {
		Controller c = Main.getController();
		
		int healthCurrent = c.getPlayer(index).healthCurrent;
		int healthMax = c.getPlayer(index).healthMax;
		int skew = 0;
		ORSCharacter player = c.getPlayer(index);
		
		if(c.getCharacterDirection(player) == ORSCharacterDirection.COMBAT_A) {
			skew = -25;
		} else if(c.getCharacterDirection(player) == ORSCharacterDirection.COMBAT_B) {
			skew = 25;
		}
		
		if(c.getShowBotPaint()) {
			if(healthMax != 0) {
				c.drawString("@red@" + Integer.toString(c.getPlayer(index).healthCurrent) + "/" + Integer.toString(c.getPlayer(index).healthMax), x + 16 + skew, y - 5, 0xFFFFFF, 1);
			} else {
				c.drawString("@red@ ?/?", x + 16, y, 0xFFFFFF, 1);
			}
		}
	}

}
