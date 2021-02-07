package scripting.idlescript;

public class HarvesterTrainer extends IdleScript {
	
	public void start(String parameters[]) {
		
		controller.displayMessage("@red@HarvesterTrainer by Dvorak. Let's party like it's 2004!");
		controller.displayMessage("@red@If less than 85 harvesting, start in Draynor/Lumbridge field.");
		controller.displayMessage("@red@If >85 harvesting, start in Ardougne field.");
		
		while(controller.isRunning()) {
			int objectId = 1265;

			if(controller.getBaseStat(19) >= 9)
				objectId = 1267;
			
			if(controller.getBaseStat(19) >= 20)
				objectId = 1269;
			
			if(controller.getBaseStat(19) >= 60)
				objectId = 1263;
			
			if(controller.getBaseStat(19) >= 85)
				objectId = 1264;
			
			int[] coords = controller.getNearestObjectById(objectId);
			if(coords != null) {
				controller.atObject(coords[0], coords[1]);
				controller.sleep(1000);
				
				while(controller.isBatching()) {					
					controller.sleep(10);
				}
			}
			
			controller.sleep(100);
		}
		
	}
}
