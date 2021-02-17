package scripting.idlescript;

/**
 * Picks herbs in Taverly via harvesting. Coleslaw only.
 *  
 * @author Dvorak
 */
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
							338, 488,
							341, 488};
			
	
	int[] unids = {165, 435, 436, 437, 438, 439, 440, 441, 442, 443};
	
	int herbsPicked = 0;
	int herbsBanked = 0;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	public void start(String[] param) {
		
		controller.displayMessage("@red@HerbHarvester by Dvorak. Let's party like it's 2004!");
		controller.displayMessage("@red@Start in Taverly with herb clippers!");
		
		while(controller.isRunning()) {
			if(controller.getInventoryItemCount() < 30) {
				int[] coords = controller.getNearestObjectById(1274);
				
				if(coords != null) {
					controller.setStatus("@whi@Picking herbs!");
					controller.atObject(coords[0], coords[1]);
					controller.sleep(1000);
					while(controller.getInventoryItemCount() < 30 && controller.isBatching() == true) controller.sleep(10);
				} else {
					//move so we can see all herbs
					controller.setStatus("@whi@Searching for herbs...");
					if(controller.currentX() != 363 || controller.currentY() != 503)
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
		controller.setStatus("@whi@Walking to bank..");
		
		if(controller.currentX() < 363)
			controller.walkTo(358,  507);
		controller.walkPath(herbToDoorPath);
		controller.sleep(1000);
		
		//open gate
		controller.atObject(341, 487);
		controller.sleep(1000);
		
		controller.walkPath(doorToBankPath);
		
		//open bank door
		while(controller.getObjectAtCoord(327, 552) == 64) {
			controller.atObject(327, 552);
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
		controller.setStatus("@whi@Banking..");
		
		controller.openBank();
		
		while(countHerbs() > 0) { 
			for(int i = 0; i < unids.length; i++) {
				if(controller.getInventoryItemCount(unids[i]) > 0) {
					controller.depositItem(unids[i], controller.getInventoryItemCount(unids[i]));
					controller.sleep(250);
				}
			}
		}
		
		herbsBanked = 0;
		for(int i = 0; i < unids.length; i++) {
			herbsBanked += controller.getBankItemCount(unids[i]);
		}
		
	}
	
	public void walkToTaverly() {
		controller.setStatus("@whi@Walking back to Taverly..");
		
		while(controller.getObjectAtCoord(327, 552) == 64) {
			controller.atObject(327, 552);
			controller.sleep(100);
		}
		
		controller.walkTo(327, 552);
		
		controller.walkPath(bankToDoorPath);
		controller.sleep(1000);
		
		//open door
		while(controller.currentX() != 342 || controller.currentY() != 487) {
			controller.displayMessage("@red@Opening door..");
			if(controller.getObjectAtCoord(341, 487) == 137)
				controller.atObject(341, 487);
			controller.sleep(5000);
		}
		
		controller.walkPath(doorToHerbPath);
	}
	
	
    @Override
    public void questMessageInterrupt(String message) {
        if(message.contains("herb"))
        	herbsPicked++;
    }
	
    @Override
    public void paintInterrupt() {
        if(controller != null) {
        			
        	int herbsPerHr = 0;
        	try {
        		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        		float scale = (60 * 60) / timeRan;
        		herbsPerHr = (int)(herbsPicked * scale);
        	} catch(Exception e) {
        		//divide by zero
        	}
        	
            controller.drawBoxAlpha(7, 7, 160, 21+14+14, 0xFFFFFF, 128);
            controller.drawString("@gre@HerbHarvester @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
            controller.drawString("@gre@Herbs picked: @whi@" + String.format("%,d", herbsPicked) + " @gre@(@whi@" + String.format("%,d", herbsPerHr) + "@gre@/@whi@hr@gre@)", 10, 21+14, 0xFFFFFF, 1);
            controller.drawString("@gre@Herbs in bank: @whi@" + String.format("%,d", herbsBanked), 10, 21+14+14, 0xFFFFFF, 1);
        }
    }

}
