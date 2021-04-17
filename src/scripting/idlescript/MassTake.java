package scripting.idlescript;

/**
 * MassGive by Dvorak. 
 */
public class MassTake extends IdleScript {
	
	public int start(String[] parameters) {
		
		if(controller.isInTrade() == false) {
			int serverIndex = controller.getPlayerServerIndexByName(parameters[0]);
			
			if(serverIndex == -1) {
				controller.log("Giving player " + parameters[0] + " is not present!");
				return 2000;
			}
			
			controller.tradePlayer(serverIndex);
			return 1000;
		} else {
			if(controller.isInTradeConfirmation()) {
				controller.acceptTradeConfirmation();
				controller.log("Finished trading.");
				controller.stop();
			} else {
				controller.acceptTrade();
				return 1000;
			}
			
			return 1000;
		} 
	}
}