package scripting.idlescript;

import orsc.ORSCharacter;

public class GetMeToArdougne extends IdleScript {
	
	int[] lumbToShip = {
		120, 648, 
		130, 647,
		142, 644, 
		154, 642,
		171, 643,
		179, 640,
		190, 638, 
		202, 633,
		213, 632, 
		227, 631, 
		237, 620, 
		254, 619, 
		259, 631, 
		267, 641
	};
	
	int[] shipToGate = {
		334, 713, 
		355, 709, 
		370, 706,
		386, 699, 
		402, 691, 
		416, 686, 
		426, 682, 
		434, 682
	};
	
	int[] gateToShip = {
		439, 675,
		446, 665, 
		459, 661, 
		467, 654
	};
	
	boolean walkedToShip = false;
	boolean inKaramja = false;
	public void start(String parameters[]) {
		controller.displayMessage("@red@GetMeToArdougne by Dvorak. Start in Lumbridge!");
		controller.displayMessage("@yel@WARNING: @red@Script is not death safe.");
		
		while(controller.isRunning()) {
			
			while(controller.getInventoryItemCount(10) < 60)
			{
				if(controller.isInCombat()) {
					controller.walkTo(controller.currentX(), controller.currentZ());
					controller.sleep(250);
				}
				
				//thieve a man
				ORSCharacter npc = controller.getNearestNpcById(11, false);
				if(npc != null) 
					controller.thieveNpc(npc.serverIndex);
				
				controller.sleep(700);
			}
			
			if(walkedToShip == false) {
				controller.walkPath(lumbToShip);
				walkedToShip = true;
			}
			
			if(inKaramja == false) {
				ORSCharacter npc;
				while(controller.isInOptionMenu() == false) {
					npc = controller.getNearestNpcById(166, false);
					if(npc != null) { 
						controller.talkToNpc(npc.serverIndex);
						controller.sleep(5000);
					}
					
					if(controller.isInOptionMenu()) {
						if(controller.optionsMenuText(0).contains("Yes"))
							controller.optionAnswer(0);
						else
							controller.optionAnswer(1); //user has not completed dragon slayer
						
						controller.sleep(6000);
					
						if(controller.currentX() == 324 && controller.currentZ() == 713) {
							inKaramja = true;
							break;
						}
					}
				}
			}
			
			if(inKaramja) {
				controller.walkPath(shipToGate);
				
				while(controller.currentX() != 435 || controller.currentZ() != 682) {
					controller.displayMessage("@red@Opening door...");
					
					while(controller.isInCombat()) {
						controller.walkTo(controller.currentX(), controller.currentZ());
						controller.sleep(250);
					}
					
					if(controller.getObjectAtCoord(434, 682) == 254)
						controller.atObject(434, 682);
					
					controller.sleep(5000);
				}
				
				controller.walkPath(gateToShip);
				
				ORSCharacter npc;
				while(controller.isInOptionMenu() == false) {
					npc = controller.getNearestNpcById(317, false);
					if(npc != null) { 
						controller.talkToNpc(npc.serverIndex);
						controller.sleep(5000);
					}
					
					//controller.sleep(3000);
					
					if(controller.isInOptionMenu()) {
						controller.optionAnswer(0);
						controller.sleep(5000);
						controller.optionAnswer(1);
						controller.sleep(5000);
						controller.optionAnswer(0);
						controller.sleep(5000);
						break;
					}
				}
				
				controller.displayMessage("@red@GetMeToArdougne finished!");
				controller.stop();
				
			}
			
		}
	}
}
