package scripting.idlescript;

/**
 * A basic script for following other players. Useful for farms.
 * @author Dvorak
 *
 */

public class FarmFollow extends IdleScript {
	public int start(String[] param) {
		if(controller.isRunning()) {
			if(controller.isInCombat())
				controller.walkTo(controller.currentX(), controller.currentY());
			
			controller.followPlayer(controller.getPlayerServerIndexByName(param[0]));
			controller.sleep(5000);
		}
		
		return 1000; //start() must return a int value now. 
	}
}