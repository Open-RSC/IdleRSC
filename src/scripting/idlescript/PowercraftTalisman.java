package scripting.idlescript;

public class PowercraftTalisman extends IdleScript {
	int a = 0;

	public void start(String parameters[]) {
		if (a == 0) {
			controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
			a = 1;
		}
		scriptStart();
	}

	public void scriptStart() {
		while (controller.isRunning()) {
			if (controller.getInventoryItemCount(1299) < 1 && controller.getInventoryItemCount(1385) < 1) {
				if (!controller.isBatching()) {
					controller.walkTo(controller.currentX() + 1, controller.currentZ());
					controller.walkTo(controller.currentX() - 1, controller.currentZ());
				}
				while(controller.getInventoryItemCount()<30 && !controller.isBatching()) {
				controller.atObject(691, 2);
				controller.sleep(1000);
				}
				while (controller.isBatching()) {
					controller.sleep(100);
				}
			}
			while (controller.getInventoryItemCount(1299) >= 1 && !controller.isBatching()
					|| controller.getInventoryItemCount(1385) >= 1 && !controller.isBatching()) {
				controller.useItemOnItemBySlot(controller.getInventoryItemIdSlot(167),
						controller.getInventoryItemIdSlot(1299));
				controller.sleep(15);
				if (controller.getInventoryItemCount(1385) > 0 && controller.getInventoryItemCount(1299) < 1) {
					controller.dropItem(controller.getInventoryItemIdSlot(1385));
				}
			}
		}
		a = 0;
	}

}