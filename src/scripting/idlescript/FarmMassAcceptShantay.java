package scripting.idlescript;

import java.util.List;

import com.openrsc.client.entityhandling.instances.Item;

/**
 * MassGive by Dvorak. 
 */
public class FarmMassAcceptShantay extends IdleScript {
	int itemId, amount;
	boolean stackable;
	boolean canTrade = true;
	
	@Override
	public void tradeMessageInterrupt(String player) {
		if(controller.isInBank() == false && controller.isInTrade() == false && controller.getInventoryItemCount() <= 18) {
			System.out.println("trading!");
			controller.tradePlayer(controller.getPlayerServerIndexByName(player));
		}
	}
	
	public int start(String[] parameters) {
		
		while(true) {
			if(controller.isInTrade()) {
				if(controller.isInTradeConfirmation()) {
					controller.acceptTradeConfirmation();
					controller.log("Finished trading.");
					controller.sleep(800);
				} else {
					controller.acceptTrade();
					controller.sleep(800);
				}
			} 
			
			if(controller.getInventoryItemCount() > 12) {
				
				controller.openBank();
			
				while(controller.isInBank() && controller.getInventoryItemCount() > 0) {
					controller.depositItem(controller.getInventorySlotItemId(0), controller.getInventoryItemCount(controller.getInventorySlotItemId(0)));
					controller.sleep(250);
				}
				
				
				controller.closeBank();
				//controller.sleep(640);
			}
		}
	}
}