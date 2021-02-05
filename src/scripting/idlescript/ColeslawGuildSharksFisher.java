package scripting.idlescript;

public class ColeslawGuildSharksFisher extends IdleScript {
	int harpoonId = 379;
	int sharkSpotId = 261;

	public void start(String[] parameters) {
		scriptStart();
	}

	public void scriptStart() {
		while (controller.isRunning()) {

			if (controller.getInventoryItemCount(harpoonId) < 1) {
				controller.displayMessage("Give me a harpoon!", 0);
			}

			// If not in fishing guild
			// TODO

			// If inventory is full
			if (controller.getInventoryItemCount() >= 30) {
				handleFullInven();
			}

			// If inventory is not full
			if (controller.getInventoryItemCount() <= 30) {
				handleFishing();
			}

			controller.sleep(600);
		}
	}

	private void handleFishing() {
		if (!controller.isBatching()) {
			int[] fishingSpot = controller.getNearestObjectById(sharkSpotId);
			controller.atObject2(fishingSpot[0], fishingSpot[1]);
		}
	}

	private void handleFullInven() {
		if (controller.isInBank()) {
			for (int itemId : controller.getInventoryItemIds()) {
				if (controller.getInventoryItemCount(itemId) > 0 && itemId != harpoonId) {
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				}
			}
		} else {
			controller.openBank();
		}
	}

}