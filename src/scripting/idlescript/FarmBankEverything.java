package scripting.idlescript;

public class FarmBankEverything extends IdleScript {
	
	public int start(String[] parameters) {
		
		while(!controller.isLoggedIn()) return 100;
		
		controller.openBank();
		
		while(controller.getInventorySlotItemId(0) != -1) {
			controller.depositItem(controller.getInventorySlotItemId(0), controller.getInventoryItemCount(controller.getInventorySlotItemId(0)));
			controller.sleep(640);
		}

		controller.logout();
		controller.stop();
		System.exit(0);
		return 100;
	}
}