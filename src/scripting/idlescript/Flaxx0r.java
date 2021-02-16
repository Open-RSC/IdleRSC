package scripting.idlescript;

public class Flaxx0r extends IdleScript {
    long flaxPicked = 0;
    long flaxBanked = 0;
    int backgroundColor = 0x00c000;
    int modifier = 128;
    
    int[] bankPath = {501, 454,
    		501, 463,
            501, 467, 
            501, 471, 
            501, 478,
            496, 482,
            490, 486};

    public void start(String parameters[]) {
        while(controller.isRunning()) {
            if(controller.getInventoryItemCount() < 30) {
                if(controller.currentZ() < 454) {
                    //we are inside the bank
                    openDoor();
                }
                
                controller.setStatus("@cya@Walking to the field.");
                controller.walkPath(bankPath);
                
                while(controller.getInventoryItemCount() < 30) {
                	controller.setStatus("@cya@Picking flax!");
                    controller.atObject(489, 486);
                    controller.sleep(150);
                }
            } else {
            	controller.setStatus("@cya@Walking to the bank.");
            	controller.walkPathReverse(bankPath); 
            	
                openDoor();

                controller.setStatus("@cya@Banking...");
                controller.openBank();

                while(controller.getInventoryItemCount(675) > 0) {
                    controller.depositItem(675, controller.getInventoryItemCount(675));
                    controller.sleep(618);
                }
                flaxBanked = controller.getBankItemCount(675);
            }
        }
    }

    public void openDoor() {
        while(controller.getObjectAtCoord(500, 454) == 64) {
        	controller.setStatus("@cya@Opening bank door...");
            controller.objectAt(500, 454, 0, 64);
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
            controller.drawBoxAlpha(7, 7, 128, 21+14+14, 0x00FFFF, 128);
            controller.drawString("@dgr@Flax@cya@x0r @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
            controller.drawString("@dgr@Flax picked: @cya@" + String.format("%,d", flaxPicked), 10, 21+14, 0xFFFFFF, 1);
            controller.drawString("@dgr@Flax in bank: @cya@" + String.format("%,d", flaxBanked), 10, 21+14+14, 0xFFFFFF, 1);
        }
    }
}
