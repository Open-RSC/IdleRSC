package scripting.idlescript;

public class VialCrafter extends IdleScript {	
	//VialCrafter Script by Seatta
	Integer vials = 0;
	Integer buckets = 0;
	Boolean stopped = false;
	Integer coords[] = {0,0};
	
	public void start(String param[]) {
		vials = controller.getInventoryItemCount(465);
		//Check for required levels
		if (controller.getBaseStat(controller.getStatId("Harvesting")) < 23 || controller.getBaseStat(controller.getStatId("Crafting")) < 33 && controller.isRunning()) {
			quit(3);
		}
		//Check if on Entrana
		if (controller.currentY() > 573 || controller.currentY() < 526 || controller.currentX() > 441 || controller.currentX() < 396 && controller.isRunning()) {
			quit(4);
		}
		
		while (controller.isRunning()) {
			controller.setStatus("@Cya@Getting Ready");
			//Checks inventory for herb clippers and glassblowing pipe
			if (controller.getInventoryItemCount(1357) == 1 && controller.getInventoryItemCount(621) == 1) {
				buckets = (controller.getInventoryItemCount(21) + controller.getInventoryItemCount(625));
				//Checks inventory for buckets
				if (buckets > 0 && controller.isRunning()) {
					//drop extra items just to make sure everything works
					dropExtra();					
					//Fill Buckets
					while (controller.getInventoryItemCount(21) > 0 && controller.isRunning()) {
						fillBuckets();
						controller.setStatus("@Cya@Filling Buckets");
					}					
					//Harvest Seaweed
					while (controller.getInventoryItemCount(622) + controller.getInventoryItemCount(624) < controller.getInventoryItemCount(625) && controller.isRunning()) {
						controller.setStatus("@Cya@Harvesting Seaweed");
						harvest();
					}					
					//walk to range if on Northern side of Entrana
					if (controller.currentY() < 550) {
						controller.setStatus("@Cya@Walking to range");
						walkSouth();
					}
					//Cook Seaweed
					while (controller.getInventoryItemCount(622) > 0 && controller.isRunning()) {
						controller.setStatus("@Cya@Making Soda Ash");
						makeSodaAsh();
					}
					//Make Molten Glass
					while (controller.getInventoryItemCount(625) > 0 && controller.isRunning()) {
						controller.setStatus("@Cya@Making Molten Glass");
						makeMoltenGlass();
					}
					
					//Blow Molten Glass Into Vials
					while (controller.getInventoryItemCount(623) > 0 && controller.isRunning()) {
						controller.setStatus("Making Vials");
						makeVials();
					}
					vials = controller.getInventoryItemCount(465);
				} else {
					quit(2);
				}
			}
		}
		if (buckets > 0) {
			quit(1);
		}
	}	
	
	public void fillBuckets() {
		if (controller.currentY() < 550) {
			walkSouth();
		}
		controller.useItemIdOnObject(429, 566, 21);
		controller.sleep(1280);
		while (controller.isBatching()) {
			controller.sleep(1280);
		}			
	}
	public void harvest() { //Could be way better
		if (!controller.isBatching() && controller.isRunning()) {
			//Drop Edible Seaweed
			while (controller.getInventoryItemCount(1245) > 0) {
				controller.dropItem(controller.getInventoryItemSlotIndex(1245), controller.getInventoryItemCount(1245));
				controller.sleep(640);
			}
			//Pick up seaweed spawns if on northern side of the island
			if (controller.currentY() < 550 && controller.getInventoryItemCount() < 30) {
				pickupSeaweed();
			}
			//Finds and harvests nearest seaweed plant
			if (controller.getInventoryItemCount(622)< controller.getInventoryItemCount(625)) {
				try {
					int plantCoords[] = controller.getNearestObjectById(1280);
					coords[0] = plantCoords[0];
					coords[1] = plantCoords[1];
				} catch (Exception e) {
					controller.walkTo(423, 555);
					while (controller.isCurrentlyWalking()) {
						controller.sleep(640);
					}								
				}
				if (coords != null && coords[1] > 550) {
					if (controller.currentY() < 550) {
						walkSouth();
					}
					controller.atObject(coords[0], coords[1]);
				}							
				if (coords != null && coords[1] < 550) {
					if (controller.currentY() > 550) {
						walkNorth();
					}
					controller.atObject(coords[0], coords[1]);
				}
			}
			controller.sleep(1280);
		} else if (controller.isBatching() && controller.getInventoryItemCount(622) < controller.getInventoryItemCount(625)){
			controller.sleep(640);
		}
		if (controller.isBatching() && controller.getInventoryItemCount(622) == controller.getInventoryItemCount(625)) {
			controller.walkTo(controller.currentX(), controller.currentY());
			controller.sleep(640);
		}
	}
	public void pickupSeaweed() {
		for (int i=0;i<4;i++) {
			int[] groundSeaweed = controller.getNearestItemById(622);
			if (groundSeaweed != null) {
				controller.pickupItem(groundSeaweed[0], groundSeaweed[1], 622, true, false);
				while (controller.isCurrentlyWalking()) {
					controller.sleep(640);
				}
				controller.sleep(640);
			}			
		}
	}
	public void walkNorth() {
		int[] pointFurnace = {423, 559};
		int[] pointSS = {434, 551};
		int[] shortcut = {434, 550};
		int[] pointChicken = {409, 552};
		int[] pointBeach = {431, 539};
		if (controller.getBaseStat(controller.getStatId("Agility")) >= 55) {
			controller.walkTo(pointFurnace[0], pointFurnace[1]);
			while (controller.isCurrentlyWalking() && controller.isRunning()) {
				controller.sleep(1280);
			}
			controller.walkTo(pointSS[0], pointSS[1]);
			while (controller.isCurrentlyWalking() && controller.isRunning()) {
				controller.sleep(1280);
			}
			controller.atObject(shortcut[0], shortcut[1]);
			controller.sleep(2560);
		} else {
			controller.walkTo(pointFurnace[0], pointFurnace[1]);
			while (controller.isCurrentlyWalking() && controller.isRunning()) {
				controller.sleep(1280);
			}
			controller.walkTo(pointChicken[0], pointChicken[1]);
			while (controller.isCurrentlyWalking() && controller.isRunning()) {
				controller.sleep(1280);
			}
			controller.walkTo(pointBeach[0], pointBeach[1]);
			while (controller.isCurrentlyWalking() && controller.isRunning()) {
				controller.sleep(1280);
			}
			
		}
	}
	public void walkSouth() {
		int[] pointFurnace = {423, 559};
		int[] pointSN = {434, 549};
		int[] shortcut = {434, 550};
		int[] pointChicken = {409, 552};
		int[] pointBeach = {431, 539};
		if (controller.getBaseStat(controller.getStatId("Agility")) >= 55) {
			controller.walkTo(pointSN[0], pointSN[1]);
			while (controller.isCurrentlyWalking() && controller.isRunning()) {
				controller.sleep(1280);
			}
			controller.atObject(shortcut[0], shortcut[1]);
			controller.sleep(2560);
			controller.walkTo(pointFurnace[0], pointFurnace[1]);
			while (controller.isCurrentlyWalking() && controller.isRunning()) {
				controller.sleep(1280);
			}
		} else {
			controller.walkTo(pointBeach[0], pointBeach[1]);
			while (controller.isCurrentlyWalking() && controller.isRunning()) {
				controller.sleep(1280);
			}
			controller.walkTo(pointChicken[0], pointChicken[1]);
			while (controller.isCurrentlyWalking() && controller.isRunning()) {
				controller.sleep(1280);
			}
			controller.walkTo(pointFurnace[0], pointFurnace[1]);
			while (controller.isCurrentlyWalking() && controller.isRunning()) {
				controller.sleep(1280);
			}
		}		
	}
	public void makeSodaAsh() {
		controller.useItemIdOnObject(426, 560, 622);
		controller.sleep(1280);
		while (controller.isBatching()){
			controller.sleep(640);
		}
	}
	public void makeMoltenGlass() {
		controller.useItemIdOnObject(419, 559, 625);	
		controller.sleep(1280);
		while (controller.isBatching()){
			controller.sleep(640);
		}
	}
	public void makeVials() {
		controller.useItemOnItemBySlot(controller.getInventoryItemSlotIndex(621), controller.getInventoryItemSlotIndex(623));
		controller.sleep(1280);
		controller.optionAnswer(0);
		while (controller.isBatching()) {
			controller.sleep(640);							
		}		
	}
	public void dropExtra() {		
		//drop extra materials (just to start fresh)
		//seaweed
		while (controller.getInventoryItemCount(622) > 0 && controller.isRunning()) {
			controller.dropItem(controller.getInventoryItemSlotIndex(622), controller.getInventoryItemCount(622));
			controller.sleep(640);
		}
		//edible seaweed
		while (controller.getInventoryItemCount(1245) > 0 && controller.isRunning()) {
			controller.dropItem(controller.getInventoryItemSlotIndex(1245), controller.getInventoryItemCount(1245));
			controller.sleep(640);
		}
		//soda ash
		while (controller.getInventoryItemCount(624) > 0 && controller.isRunning()) {
			controller.dropItem(controller.getInventoryItemSlotIndex(624), controller.getInventoryItemCount(624));
			controller.sleep(640);
		}
		//molten glass
		while (controller.getInventoryItemCount(623) > 0 && controller.isRunning()) {
			controller.dropItem(controller.getInventoryItemSlotIndex(623), controller.getInventoryItemCount(623));
			controller.sleep(640);
		}
	}
	public void quit(int i) {
		if (!stopped) {
			if (i == 1) {
				controller.displayMessage("@red@Script has been stopped!");
			} else if (i == 2) {
				controller.displayMessage("@red@Please start the script with the following items in your inventory:");
				controller.displayMessage("@red@Herb Clippers, Glass Blowing Pipe, and");
				controller.displayMessage("@red@up to 13 buckets");
			} else if (i == 3) {
				controller.displayMessage("@red@You need level 23 harvesting and 33 crafting to use this script.");
			} else if (i == 4) {
				controller.displayMessage("@red@Start the script on Entrana.");
			}
		}
		controller.stop();
		stopped = true;
	}
	 @Override
	public void paintInterrupt() {
		 if(controller != null) {      	
            controller.drawBoxAlpha(7, 7, 124, 21+20, 0xFFFFFF, 64);
            controller.drawString("@whi@_________________", 10, 7, 0xFFFFFF, 1);
            controller.drawString("  @gre@VialCrafter @whi@- @cya@Seatta", 10, 21, 0xFFFFFF, 1);
            controller.drawString("@whi@_________________", 10, 21+3, 0xFFFFFF, 1);
            controller.drawString("@cya@Vials Held", 10, 21+19, 0xFFFFFF, 1);
            controller.drawString("@whi@_________________", 10, 21+23, 0xFFFFFF, 1);	
            controller.drawString("@whi@|", 68, 21+19, 0xFFFFFF, 1);	
            controller.drawString("@whi@" + vials, 74, 21+19, 0xFFFFFF, 1);
		 }
	 }
}	