package scripting.idlescript;

public class Flaxx0r extends IdleScript {
    long flaxPicked = 0;
    long flaxBanked = 0;

    public void start(String parameters[]) {
        while(controller.isRunning()) {
            if(controller.getInventoryItemCount() < 30) {
                if(controller.currentZ() > 454) {
                    //we are inside the bank
                    openDoor();
                    controller.walkPath(new int[] {501, 454,
                                                   501, 468,
                                                   490, 486
                    });

                    while(controller.getInventoryItemCount() < 30) {
                        controller.atObject(489, 486);
                        controller.sleep(150);
                    }
                }
            } else {
                controller.walkPath(new int[] { 501, 468, 501, 454});
                openDoor();

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
            controller.objectAt(500, 454, 0, 64);
            controller.sleep(618);
        }
    }


    @Override
    public void serverMessageInterrupt(String message) {
        if(message.contains("uproot a flax plant"))
            flaxPicked++;
    }

    @Override
    public void paintInterrupt() {
        if(controller != null) {
            controller.drawBoxAlpha(7, 7, 128, 21+14+14, 0xFFFFFF, 64);
            controller.drawString("@dgr@Flax@cya@x0r @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
            controller.drawString("@dgr@Flax picked: @cya@" + String.valueOf(flaxPicked), 10, 21+14, 0xFFFFFF, 1);
            controller.drawString("@dgr@Flax in bank: @cya@" + String.valueOf(flaxBanked), 10, 21+14+14, 0xFFFFFF, 1);
        }
    }
}
