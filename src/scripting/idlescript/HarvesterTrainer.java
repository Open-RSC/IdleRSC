package scripting.idlescript;

/**
 * Trains Harvesting on Coleslaw in Draynor and Ardougne fields. 
 * 
 * @author Dvorak
 */
public class HarvesterTrainer extends IdleScript {
	
	int harvested = 0;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	public void start(String parameters[]) {
		controller.displayMessage("@red@HarvesterTrainer by Dvorak. Let's party like it's 2004!");
		controller.displayMessage("@red@If less than 85 harvesting, start in Draynor/Lumbridge field.");
		controller.displayMessage("@red@If >85 harvesting, start in Ardougne field.");
		controller.quitIfAuthentic();
		
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
				controller.setStatus("@yel@Harvesting...");
				controller.atObject(coords[0], coords[1]);
				controller.sleep(1000);
				
				while(controller.isBatching()) {					
					controller.sleep(10);
				}
			} else {
				controller.setStatus("@yel@Waiting for spawn..");
			}
			
			controller.sleep(100);
		}
		
	}
	
    @Override
    public void questMessageInterrupt(String message) {
        if(message.contains("You get"))
        	harvested++;
    }
	
    @Override
    public void paintInterrupt() {
        if(controller != null) {
        			
        	int harvestedPerHr = 0;
        	try {
        		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        		float scale = (60 * 60) / timeRan;
        		harvestedPerHr = (int)(harvested * scale);
        	} catch(Exception e) {
        		//divide by zero
        	}
        	
            controller.drawBoxAlpha(7, 7, 190, 21+14, 0x228B22, 128);
            controller.drawString("@yel@HarvesterTrainer @whi@by @yel@Dvorak", 10, 21, 0xFFFFFF, 1);
            controller.drawString("@yel@Stuff Harvested: @whi@" + String.format("%,d", harvested) + " @yel@(@whi@" + String.format("%,d", harvestedPerHr) + "@yel@/@whi@hr@yel@)", 10, 21+14, 0xFFFFFF, 1);
        }
    }
}
