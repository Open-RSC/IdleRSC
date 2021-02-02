package scripting.idlescript;

public class HerbHarvester extends IdleScript {
	
	int[] herbToDoorPath = {363, 503, 
					  364, 495, 
					  364, 488,
					  355, 487, 
					  349, 487, 
					  342, 488};
	
	int[] doorToBankPath = {338, 488, 
						    335, 493,
						    329, 498, 
						    324, 501,
						    318, 507, 
						    316, 513, 
						    316, 518, 
						    320, 526, 
						    323, 535,
						    325, 544,
						    327, 552};
	
	int[] doorToHerbPath = {342, 488,
							349, 487,
							355, 487,
							364, 488,
							364, 495,
							363, 503};
	
	int[] bankToDoorPath = {327, 552,
							325, 544,
							323, 535,
							320, 526, 
							316, 518,
							316, 513,
							318, 507, 
							324, 501,
							329, 498,
							335, 493,
							338, 488};
			
	
	int[] unids = {165, 435, 436, 437, 438, 439, 440, 441, 442, 443};
	
	public void start(String[] param) {
		
		controller.displayMessage("@red@HerbHarvester by Dvorak. Let's party like it's 2004!");
		controller.displayMessage("@red@Start in Taverly with herb clippers!");
		
		while(controller.isRunning()) {
			if(controller.getInventoryItemCount() < 30) {
				int[] coords = controller.getNearestObjectById(1274);
				
				if(coords != null) {
					controller.atObject(coords[0], coords[1]);
					controller.sleep(1000);
					while(controller.getInventoryItemCount() < 30 && controller.isBatching() == true) controller.sleep(10);
				} else {
					//move so we can see all herbs
					controller.walkTo(363, 503);
				}
				
			} else {
				walkToBank();
				bank();
				walkToTaverly();
			}
			
			controller.sleep(100);
		}
	}
	
	public void walkToBank() {
		controller.displayMessage("@red@Walking to bank....");
		controller.walkPath(herbToDoorPath);
		controller.sleep(1000);
		
		//open gate
		controller.atObject(341, 487);
		controller.sleep(1000);
		
		controller.walkPath(doorToBankPath);
		
		//open bank door
		while(controller.getObjectAtCoord(327, 552) == 64) {
			controller.objectAt(327, 552, 0, 64);
			controller.sleep(100);
		}
		
		controller.walkTo(328, 552);
	}
	
	public int countHerbs() {
		int count = 0;
		for(int i = 0; i < unids.length; i++) {
			count += controller.getInventoryItemCount(unids[i]);
		}
		
		return count;
	}
	
	public void bank() {
		controller.displayMessage("@red@Banking...");
		
		controller.openBank();
		
		while(countHerbs() > 0) { 
			for(int i = 0; i < unids.length; i++) {
				if(controller.getInventoryItemCount(unids[i]) > 0) {
					//controller.depositItem(unids[i]);
					controller.depositItem(unids[i], controller.getInventoryItemCount(unids[i]));
					controller.sleep(250);
				}
			}
		}
	}
	
	public void walkToTaverly() {
		controller.displayMessage("@red@Walking back to Taverly...");
		
		while(controller.getObjectAtCoord(327, 552) == 64) {
			controller.objectAt(327, 552, 0, 64);
			controller.sleep(100);
		}
		
		controller.walkTo(327, 552);
		
		controller.walkPath(bankToDoorPath);
		controller.sleep(1000);
		
		//open door
		controller.atObject(341, 487);
		controller.sleep(1000);
		
		controller.walkPath(doorToHerbPath);
		
		
	}
	

}
