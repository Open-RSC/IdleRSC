package scripting.idlescript;

/**
 * Trains mining and crafting at the essence rocks. 
 * 
 * @author Searos
 */
public class PowercraftTalisman extends IdleScript {
	int a = 0;
	int totalTalismans = 0;
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
					controller.setStatus("Preventing Logout");
					controller.walkTo(controller.currentX() + 1, controller.currentY());
					controller.walkTo(controller.currentX() - 1, controller.currentY());
				}
				while(controller.getInventoryItemCount()<30 && !controller.isBatching()) {
				controller.setStatus("Mining");
				controller.atObject(691, 2);
				controller.sleep(1000);
				}
				while (controller.isBatching()) {
					controller.sleep(100);
				}
			}
			while (controller.getInventoryItemCount(1299) >= 1 && !controller.isBatching()
					|| controller.getInventoryItemCount(1385) >= 1 && !controller.isBatching()) {
				controller.setStatus("Crafting");
				controller.useItemOnItemBySlot(controller.getInventoryItemIdSlot(167),
						controller.getInventoryItemIdSlot(1299));
				controller.sleep(15);
				if (controller.getInventoryItemCount(1385) > 0 && controller.getInventoryItemCount(1299) < 1) {
					controller.setStatus("Dropping");
					totalTalismans = totalTalismans + controller.getInventoryItemCount(1385);
					controller.dropItem(controller.getInventoryItemIdSlot(1385));
				}
			}
		}
		a = 0;
	}
	@Override
	public void paintInterrupt() {
		if (controller != null) {
			controller.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
			controller.drawString("@red@Powercraft Talismans @gre@by Searos", 10, 21, 0xFFFFFF, 1);
			controller.drawString("@red@Talismans crafted: @yel@" + String.valueOf(this.totalTalismans), 10, 35, 0xFFFFFF, 1);
		}
	}
}