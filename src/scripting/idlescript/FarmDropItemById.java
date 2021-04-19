package scripting.idlescript;

public class FarmDropItemById extends IdleScript {
	
	public int start(String[] parameters) {
		
		while(!controller.isLoggedIn()) return 100;
		
		int id = Integer.parseInt(parameters[0]);
		
		while(controller.getInventoryItemCount(id) > 0) {
			controller.dropItem(controller.getInventoryItemSlotIndex(id));
			controller.sleep(618);
		}
		
		controller.logout();
		controller.stop();
		System.exit(0);
		return 100;
	}
}