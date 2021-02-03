package scripting.idlescript;

public class PowercraftTalisman extends IdleScript {
	int a = 0;
    public void start(String parameters[]) {
    	if(a == 0) {
    	controller.displayMessage("@gre@"+'"'+"heh"+'"'+" - Searos");
    	a = 1;
    	}
    		scriptStart();
	}
    
    public void scriptStart() {
    	if(controller.getInventoryItemCount(1299) < 1 && controller.getInventoryItemCount(1385) < 1 && !controller.isBatching()) {
    		if(!controller.isBatching()) {
       		controller.walkTo(controller.currentX()+1, controller.currentZ());
       		controller.walkTo(controller.currentX()-1, controller.currentZ());
    		}
    		controller.atObject(691,2);
    		controller.sleep(100);
    		while(controller.isBatching()) {controller.sleep(100);}
    	}
    	if(controller.getInventoryItemCount(1299) >= 1 || controller.getInventoryItemCount(1385) >= 1 && !controller.isBatching()) {
    		controller.useItemOnItemBySlot(controller.getInventoryItemIdSlot(167), controller.getInventoryItemIdSlot(1299));
    		controller.sleep(500);
    		while(controller.isBatching()) {
    			if(controller.getInventoryItemCount(1385) >= 1) {
    				controller.dropItem(controller.getInventoryItemIdSlot(1385));
    			}
    			 {controller.sleep(100);}
    		}
    		controller.sleep(10);
    	}
    }
}