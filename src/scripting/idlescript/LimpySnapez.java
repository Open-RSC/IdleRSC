package scripting.idlescript;

public class LimpySnapez extends IdleScript {

	int[] herbToDoorPath = {366, 472,
			360, 478, 
			353, 484,
			347, 487,
			342, 487,
			342, 488
	};

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
	
	int[] doorToHerbPath = {347, 487,
			353, 484, 
			360, 478,
			366, 472};
	
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
						338, 488,
						341, 488};
	
	int[] plants = {1273, 1281};
	int[] loot = {220, 469};
	
	boolean tileFlick = false;
	
	public void start(String[] param) {
	
		controller.displayMessage("@red@LimpySnapez by Dvorak. Let's party like it's 2004!");
		controller.displayMessage("@red@Start in Taverly with herb clippers!");
		
		while(controller.isRunning()) {
			if(controller.getInventoryItemCount() < 30) {
				
				boolean foundPlants = false;
				for(int plantId : plants) {
					int[] coords = controller.getNearestObjectById(plantId);
					
					if(coords != null) {
						foundPlants = true;
						controller.atObject(coords[0], coords[1]);
						controller.sleep(1000);
						while(controller.getInventoryItemCount() < 30 && controller.isBatching() == true) controller.sleep(10);
						break;
					}
				}
				
				if(foundPlants == false) {
					if(!tileFlick) {
						controller.walkTo(364, 472);
					} else {
						controller.walkTo(364, 471);
					}
					tileFlick = !tileFlick;
					controller.sleep(700);
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
	
	public int countPlants() {
		int count = 0;
		for(int i = 0; i < loot.length; i++) {
			count += controller.getInventoryItemCount(loot[i]);
		}
		
		return count;
	}
	
	public void bank() {
		controller.displayMessage("@red@Banking...");
		
		controller.openBank();
		
		while(countPlants() > 0) { 
			for(int i = 0; i < loot.length; i++) {
				if(controller.getInventoryItemCount(loot[i]) > 0) {
					controller.depositItem(loot[i], controller.getInventoryItemCount(loot[i]));
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
		while(controller.currentX() != 342 || controller.currentZ() != 487) {
			controller.displayMessage("@red@Opening door...");
			if(controller.getObjectAtCoord(341, 487) == 137)
				controller.atObject(341, 487);
			controller.sleep(5000);
		}
		
		controller.walkPath(doorToHerbPath);	
	}
		
}
