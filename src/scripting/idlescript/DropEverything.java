package scripting.idlescript;

/** 
 * This is a basic script which drops everything in your inventory.
 * 
 * @author Dvorak
 */

public class DropEverything extends IdleScript {

	public void start(String parameters[]) {
		controller.displayMessage("@red@DropEverything by Dvorak. Let's party like it's 2018!");
		controller.displayMessage("Dropping everything in inventory...");
		
		while(controller.getInventoryItemCount() > 0) {
			controller.dropItem(0);
		}
		
		controller.displayMessage("Done.");
		controller.stop();
		
	}
	
}
