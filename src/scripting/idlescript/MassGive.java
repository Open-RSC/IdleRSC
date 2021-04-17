package scripting.idlescript;

/**
 * MassGive by Dvorak. 
 */
public class MassGive extends IdleScript {

	private String[] names = new String[] {
			"DVORAK 00", 
			"DVORAK 01", 
			"DVORAK 02", 
			"DVORAK 03", 
			"DVORAK 04", 
			"DVORAK 05", 
			"DVORAK 06", 
			"DVORAK 07", 
			"DVORAK 08", 
			"DVORAK 09", 
			"DVORAK 10", 
			"DVORAK 11", 
			"DVORAK 12", 
			"DVORAK 13", 
			"DVORAK 14", 
			"DVORAK 15", 
			"DVORAK 16", 
			"DVORAK 17", 
			"DVORAK 18", 
			"DVORAK 19"
	};
	
	private boolean[] given = new boolean[names.length];
	
	public int start(String[] parameters) {
		
		int id = Integer.parseInt(parameters[0].split(",")[0]);
		int amount = Integer.parseInt(parameters[0].split(",")[1]);
		
		
		for(int i = 0; i < names.length; i++) {
			if(given[i] == false) {
				int serverIndex = controller.getPlayerServerIndexByName(names[i]);
				
				if(serverIndex == -1) {
					controller.log("Player " + names[i] + " is not present!");
					return 2000;
				}
				
				
				
				if(controller.isInTrade()) {
					if(controller.isInTradeConfirmation()) {
						controller.acceptTradeConfirmation();
						
						while(controller.isInTrade()) controller.sleep(100);
						given[i] = true;
						
						return 8000; //liberal timing for synchronizaiton
					} else {
						controller.setTradeItems(new int[] {id}, new int[] {amount});
						controller.sleep(3000);
						controller.acceptTrade();
						return 3000;
					}
				} else {
					controller.tradePlayer(serverIndex);
					return 2000;
				}
				
				
			}
		}
		
		controller.log("Finished trading farm.");
		controller.stop();
		return 1000; //start() must return a int value now. 
	}
}