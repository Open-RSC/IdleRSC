package scripting.idlescript;

/**
 * A basic script for following other players. Useful for farms.
 * @author Dvorak
 *
 */

public class Follow extends IdleScript {
	public void start(String[] param) {
		if(controller.isRunning()) {
			if(controller.isInCombat())
				controller.walkTo(controller.currentX(), controller.currentY());
			
			controller.followPlayer(controller.getPlayerServerIndexByName(param[0]));
			controller.sleep(5000);
		}
	}
}