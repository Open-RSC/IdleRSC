package scripting.idlescript;

/**
 * MassGive by Dvorak. 
 */
public class FarmMassTake extends IdleScript {
	
	public int start(String[] parameters) {
		
		if(controller.isInTrade() == false) {
			int serverIndex = controller.getPlayerServerIndexByName(parameters[0]);
			
			if(serverIndex == -1) {
				controller.log("Giving player " + parameters[0] + " is not present!");
				return 2000;
			}
			
			controller.tradePlayer(serverIndex);
			return 3000;
		} else {
			if(controller.isInTradeConfirmation()) {
				controller.acceptTradeConfirmation();
				controller.log("Finished trading.");
				controller.sleep(3000);
				controller.stop();
				controller.logout();
				System.exit(0);
			} else {
				controller.acceptTrade();
				return 1000;
			}
			
			return 1000;
		} 
	}
}