package scripting.idlescript;

import java.util.List;

import com.openrsc.client.entityhandling.instances.Item;

/**
 * MassGive by Dvorak. 
 */
public class FarmTradeEverything extends IdleScript {
	int itemId, amount;
	boolean stackable;
	
    private void trade_sleep(int maxTicks) {
    	int ticks = 0;
    	
    	while(ticks < maxTicks) {
    		if(controller.isInTrade())
    			return;
    		
    		controller.sleep(10);
    		ticks++;
    	}
    	
    }
	
	public int start(String[] parameters) {
		
		
		controller.log("cycle");
		if(controller.getInventoryItemCount() == 0) {
			controller.log("opening...");
			controller.openBank();
			controller.log("open");
			
			
			if(controller.isInBank()) {
				if(controller.getBankItemsCount() > 0) {
					
					while(controller.getInventoryItemCount() < 24) {
						List<Item> items = controller.getBankItems();
						
						if(items.size() == 0) {
							if(controller.getInventoryItemCount() == 0) {
								System.exit(0);
							}
							break;
						}
						
						
						itemId = items.get(0).getCatalogID();
						amount = items.get(0).getAmount();
						stackable = items.get(0).getItemDef().stackable;
						
						
						if(!stackable && amount > 24)
							amount = 24;
						
						
						controller.withdrawItem(itemId, amount);
						controller.sleep(640);
					}
					
					controller.closeBank();
					controller.sleep(1200);
				} else {
					System.exit(0);
				}
			}
		}
		
		if(controller.getInventoryItemCount() > 0 && controller.isInTrade() == false) {
			int serverIndex = controller.getPlayerServerIndexByName(parameters[0]);
			
			if(serverIndex == -1) {
				controller.log("Receiving player " + parameters[0] + " is not present!");
				return 2000;
			}
			
			int[] coords = controller.getPlayerCoordsByServerIndex(serverIndex);
			controller.walkTo(coords[0], coords[1]);
			controller.tradePlayer(serverIndex);
			trade_sleep(700);
			return 0;
		} else {
			if(controller.isInTradeConfirmation()) {
				controller.acceptTradeConfirmation();
				controller.log("Finished trading.");
				controller.sleep(800);
			} else {
				
				itemId = controller.getInventorySlotItemId(0);
				amount = controller.getInventoryItemCount(itemId);
				stackable = controller.isItemStackable(itemId);
				if(stackable) {
					controller.setTradeItems(new int[] {itemId}, new int[] {amount});
					controller.sleep(1000);
				} else {
					itemId = controller.getInventorySlotItemId(0);
					amount = controller.getInventoryItemCount(itemId);
					
					int[] items = new int[amount];
					int[] amounts = new int[amount];
					
					for(int i = 0; i < amount; i++) {
						items[i] = itemId;
						amounts[i] = 1;
					}
					
					controller.setTradeItems(items, amounts);
					controller.sleep(640); //changed -- good
				}
				controller.acceptTrade();
				return 800; //changed
			}
			
			return 1000;
		} 
	}
}