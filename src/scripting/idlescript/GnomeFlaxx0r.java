package scripting.idlescript;

/**
 * This is a basic script that picks flax, optionally spins it, in Tree Stronghold.
 * 
 * @author Dvorak
 * 
 * bugfixes by Kaila
 */
public class GnomeFlaxx0r extends IdleScript {
	boolean spin = false;
	
    long flaxPicked = 0;
    long flaxBanked = 0;
    
    long startTimestamp = System.currentTimeMillis() / 1000L;

    public int start(String parameters[]) {
    	
    	if(parameters.length != 1) {
    		controller.displayMessage("@red@Put true or false in arguments. True to spin flax, false to just pick flax.");
    		controller.stop();
    	} else {
    		spin = Boolean.valueOf(parameters[0]);
    	}
    	
        while(controller.isRunning()) {
            if(controller.getInventoryItemCount() < 30) {
            	if(spin) {
	                controller.setStatus("@cya@Walking to the flax...");
	                controller.walkTo(693, 516);
	                
	                while(controller.getInventoryItemCount() < 30) {
	                	controller.setStatus("@cya@Picking flax!");
	                    controller.atObject(693, 517);
	                    controller.sleep(150);
						while(controller.isBatching() && controller.getInventoryItemCount() < 30)  controller.sleep(640); //added batching - kaila
	                }
	                
	                controller.setStatus("@cya@Spinnin' flax!");
	                while(controller.currentX() != 692 || controller.currentY() != 1459) {
	                	controller.atObject(691, 515);
	                    controller.sleep(2000);
						while(controller.isBatching() && controller.getInventoryItemCount() < 30)  controller.sleep(640);  //added batching (spinning untested)
	                }
	                
	                
	                while(controller.getInventoryItemCount(675) > 0) {
	                	controller.sleepHandler(98, true);
	                	
	                	//sometimes we walk away from the spinning wheel for some reason?
	                	if(controller.currentX() != 692 || controller.currentY() != 1459) {
	                		controller.walkTo(692, 1459); 
	                		controller.sleep(1000);
	                	}
	                	
	                	controller.useItemIdOnObject(693, 1459, 675);
	                	controller.sleep(500);
	                }
	                
	                while(controller.currentX() != 692 || controller.currentY() != 515) {
	                	controller.atObject(691, 1459);
	                	controller.sleep(1000);
	                }
            	} else {
	                if(controller.getInventoryItemCount() < 30) {
	                	controller.setStatus("@cya@Picking flax!");
	                    controller.atObject(712, 517);
	                    controller.sleep(2000);
						while(controller.isBatching() && controller.getInventoryItemCount() < 30)  controller.sleep(640); //added batching - kaila
	                }            		
            	}
            } else {
            	controller.setStatus("@cya@Walking to the bank.");
            	controller.walkTo(713, 516);
            	
                while(controller.currentX() != 713 || controller.currentY() != 1460) {
                	controller.atObject(714, 516);
                	controller.sleep(1000);
                }
            	
                controller.setStatus("@cya@Banking...");
                controller.openBank();
        		controller.sleep(1000);
        		
        		if(controller.isInBank()) {  
        			if(controller.getInventoryItemCount(675) > 0) {  //changed to if
        				controller.depositItem(675, 30);  //just bank all (untested)
        				controller.sleep(1280); //increased
        			}
        			if(controller.getInventoryItemCount(676) > 0) {  //changed to if
        				controller.depositItem(676, 30);  //just bank all
        				controller.sleep(1280);  //increased
        			}
        			controller.closeBank();
        			controller.sleep(640);
        		}
                if(spin) { 
                	flaxBanked = controller.getBankItemCount(676);
                } else {
                	flaxBanked = controller.getBankItemCount(675);
                }
                controller.setStatus("@cya@Going back to flax..");
                
                controller.walkTo(714, 1459);
                while(controller.currentX() != 714 || controller.currentY() != 515) {
                	controller.atObject(714, 1460);
                	controller.sleep(1000);
                }
            }
        }
        
        return 1000; //start() must return a int value now. 
    }

    public void openDoor() {
        while(controller.getObjectAtCoord(500, 454) == 64) {
        	controller.setStatus("@cya@Opening bank door...");
            controller.atObject(500, 454);
            controller.sleep(618);
        }
    }


    @Override
    public void questMessageInterrupt(String message) {
        if(message.contains("uproot a flax plant"))
            flaxPicked++;
    }

    @Override
    public void paintInterrupt() {
        if(controller != null) {
        	int flaxPerHr = 0;
        	try {
        		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        		float scale = (60 * 60) / timeRan;
        		flaxPerHr = (int)(flaxPicked * scale);
        	} catch(Exception e) {
        		//divide by zero
        	}
            controller.drawBoxAlpha(7, 7, 170, 21+14+14, 0x00FFFF, 128);
            controller.drawString("@dgr@Gnome@cya@Flaxx0r @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
            if(spin) {
	            controller.drawString("@dgr@Strings strung: @cya@" + String.format("%,d", flaxPicked) + " @gre@(@cya@" + String.format("%,d", flaxPerHr) + "@gre@/@cya@hr@gre@)", 10, 21+14, 0xFFFFFF, 1);
	            controller.drawString("@dgr@Strings in bank: @cya@" + String.format("%,d", flaxBanked), 10, 21+14+14, 0xFFFFFF, 1);
            } else {
	            controller.drawString("@dgr@Flax picked: @cya@" + String.format("%,d", flaxPicked) + " @gre@(@cya@" + String.format("%,d", flaxPerHr) + "@gre@/@cya@hr@gre@)", 10, 21+14, 0xFFFFFF, 1);
	            controller.drawString("@dgr@Flax in bank: @cya@" + String.format("%,d", flaxBanked), 10, 21+14+14, 0xFFFFFF, 1);
            }
        }
    }
}
