package scripting.idlescript;

import orsc.ORSCharacter;

/**
 * Collects anti dragon shields from Duke of Lumbridge.
 * 
 * @author Dvorak
 *
 */
public class AntiDragonShields extends IdleScript {
	long startTimestamp = System.currentTimeMillis() / 1000L;
	int success = 0;
	
	int[] bankPath = {
		128, 659, 
		122, 659,
		116, 656, 
		126, 646, 
		148, 646, 
		173, 644, 
		187, 642, 
		211, 638, 
		220, 633
	};
	
	public int start(String parameters[]) {
		controller.displayMessage("@red@AntiDragonShields by Dvorak");
		controller.displayMessage("@red@Start near Duke of Lumbridge");
		
		while(controller.isRunning()) {
			
			int targetAmount = 30 - controller.getInventoryItemCount();
			int stack = 5;
			
			while(controller.getInventoryItemCount() < 30 && controller.isRunning()) {
				int totalCount = controller.getGroundItemAmount(420, 132, 1603) + controller.getInventoryItemCount(420);
				
				controller.displayMessage("@red@" + String.valueOf(totalCount) + " of " + String.valueOf(targetAmount) + " collected");
				if(controller.getGroundItemAmount(420, 132, 1603) >= stack || totalCount == targetAmount) {
					controller.setStatus("@red@Picking up shields..");
					while(controller.getNearestItemById(420) != null && controller.getInventoryItemCount() < 30) {
						controller.pickupItem(133, 1603, 420, true, false);
						controller.sleep(100);
					}
					
					stack += 5;
				}
				
				if(controller.getInventoryItemCount(420) == targetAmount) {
					continue;
				}
				
				while(controller.getInventoryItemCount(420) > 0) {
					controller.setStatus("@red@Dropping shield(s)..");
					controller.walkTo(133, 1603);
					controller.dropItem(controller.getInventoryItemSlotIndex(420));
					controller.sleep(618);
				}
				
				ORSCharacter npc = controller.getNearestNpcById(198, false);
				
				if(npc != null) {
					controller.setStatus("@red@Getting shield from Duke..");
					controller.talkToNpc(npc.serverIndex);
					controller.sleep(4000);
					
					if(controller.isInOptionMenu() == false)
						continue;
					
					controller.optionAnswer(0);
					controller.sleep(7000);
				}
			}
			
			toBank();
			toDuke();
		}
		
		return 1000; //start() must return a int value now. 
	}
	
	public void openDoor() {
		int[] coords = null;
		do {
			coords = controller.getNearestObjectById(64);
			if(coords != null) {
				controller.atObject(coords[0], coords[1]);
				controller.sleep(618);
			}
		} while(coords != null);
	}
	
	public void toBank() {
		while(controller.currentX() != 138 || controller.currentY() != 666) {
			controller.atObject(139, 1610);
			controller.sleep(1000);
		}
		
		controller.walkTo(129, 659);
		openDoor();
		controller.walkTo(128, 659);
		
		controller.walkPath(bankPath);
		openDoor();
		controller.walkTo(220, 634);
		
		controller.openBank();
		
		while(controller.getInventoryItemCount(420) > 0) {
			controller.depositItem(420, controller.getInventoryItemCount(420) );
			controller.sleep(100);
		}
	}
	
	public void toDuke() {
		openDoor();
		controller.walkPathReverse(bankPath);
		openDoor();
		
		while(controller.currentX() != 138 || controller.currentY() != 1610) {
			controller.atObject(139, 666);
			controller.sleep(1000);
		}
	}
}
