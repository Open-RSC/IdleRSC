package scripting.idlescript;

public class HerbIdentifier extends IdleScript {
	
		int[] unids = {933, 443, 442, 441, 440, 439, 438, 437, 436, 435, 165};
		
		public int countUnids() {
			int count = 0;
			for(int i = 0; i < unids.length; i++) {
				count += controller.getInventoryItemCount(unids[i]);
			}
			
			return count;
		}
		
		public boolean canIdentifyHerb(int herbId) {
			int lvl = Integer.MAX_VALUE;
			switch(herbId) {
			case 933:
				lvl = 75;
				break;
			case 443:
				lvl = 70;
				break;
			case 442:
				lvl = 65;
				break;
			case 441:
				lvl = 54;
				break;
			case 440:
				lvl = 48;
				break;
			case 439:
				lvl = 40;
				break;
			case 438:
				lvl = 25;
				break;
			case 437:
				lvl = 20;
				break;
			case 436:
				lvl = 11;
				break;
			case 435:
				lvl = 5;
				break;
			case 165:
				lvl = 3;
				break;
			}
			
			return controller.getCurrentStat(controller.getStatId("Herblaw")) >= lvl;
		}
		
		public void start(String[] param) {
			
			controller.displayMessage("@red@HerbIdentifier by Dvorak. Let's party like it's 2004!");
			controller.displayMessage("@red@Start in any bank with herbs in the bank!");
			
			while(controller.isRunning()) {
				
				controller.sleepHandler(98, true);
				
				if(countUnids() == 0) {
					controller.openBank();
					
					for(int id : controller.getInventoryItemIds()) {
						if(id != 1263) //don't deposit sleeping bags.
						{
							controller.depositItem(id, controller.getInventoryItemCount(id));
							controller.sleep(618);
							break;
						}
					}
					
					for(int id : unids) {
						if(controller.getBankItemCount(id) > 0 && canIdentifyHerb(id)) {
							controller.withdrawItem(id, controller.getBankItemCount(id));
							controller.sleep(618);
							break;
						}
					}
					
					controller.closeBank();
					
				} else {
					for(int id : unids) {
						if(controller.getInventoryItemCount(id) > 0) {
							controller.itemCommand(id);
							controller.sleep(250);
							while(controller.isBatching()) controller.sleep(10);
							break;
						}
					}
				}
			}
			
		}
	
}
