package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Math;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import orsc.ORSCharacter;

public class ShearSheep extends IdleScript {
	int startX = -1;
	int startY = -1;
	int a = 0;

	public void start(String parameters[]) {
		if (a == 0) {
			controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
			a = 1;
		}
		startX = controller.currentX();
		startY = controller.currentZ();
		while (controller.isRunning()) {
			scriptStart();
		}
	}

	public void startWalking(int x, int y) {
		// shitty autowalk
		int newX = x;
		int newY = y;
		while (controller.currentX() != x || controller.currentZ() != y) {
			if (controller.currentX() - x > 23) {
				newX = controller.currentX() - 20;
			}
			if (controller.currentZ() - y > 23) {
				newY = controller.currentZ() - 20;
			}
			if (controller.currentX() - x < -23) {
				newX = controller.currentX() + 20;
			}
			if (controller.currentZ() - y < -23) {
				newY = controller.currentZ() + 20;
			}
			if (Math.abs(controller.currentX() - x) <= 23) {
				newX = x;
			}
			if (Math.abs(controller.currentZ() - y) <= 23) {
				newY = y;
			}
			if (!controller.isTileEmpty(newX, newY)) {
				controller.walkToAsync(newX, newY, 2);
				controller.sleep(640);
			} else {
				controller.walkToAsync(newX, newY, 0);
				controller.sleep(640);
			}
		}
	}

	public void scriptStart() {
		while (controller.getInventoryItemCount() < 30 && controller.getInventoryItemCount(144) == 1) {
			controller.displayMessage("Returning to start");
			if (controller.getNearestNpcById(2, false) != null) {
				controller.displayMessage("Sheep found");
				controller.useItemOnNpc(controller.getNearestNpcById(2, false).serverIndex, 144);
				controller.displayMessage("Shearing sheep");
				controller.sleep(642);
				while (controller.isBatching() && controller.getInventoryItemCount() < 30) {
					controller.sleep(10);
				}
			} else if (controller.getNearestNpcById(2, false) == null) {
				controller.displayMessage("Finding sheep");
				startWalking(startX, startY);
			}
		}
		while (controller.getInventoryItemCount() == 30 || controller.getInventoryItemCount(144) != 1) {
			controller.displayMessage("Need to bank");
			while (controller.getNearestNpcById(95, false) == null && controller.getInventoryItemCount() == 30
					|| controller.getNearestNpcById(95, false) == null && controller.getInventoryItemCount(144) != 1) {
				controller.displayMessage("Walking to bank");
				startWalking(controller.getNearestBank()[0], controller.getNearestBank()[1]);
			}
			while (controller.getNearestNpcById(95, false) != null && !controller.isInBank()
					&& controller.getInventoryItemCount() == 30
					|| controller.getNearestNpcById(95, false) != null && controller.getInventoryItemCount(144) != 1
							&& !controller.isInBank()) {
				controller.displayMessage("Banking");
				controller.openBank();
				controller.sleep(640);
			}
			if (controller.isInBank() && controller.getInventoryItemCount() == 30
					|| controller.isInBank() && controller.getInventoryItemCount(144) != 1) {
				for (int itemId : controller.getInventoryItemIds()) {
					if (itemId != 0 && itemId != 144) {
						controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
						controller.sleep(100);
					}
				}
				if (controller.getInventoryItemCount(144) != 1) {
					controller.withdrawItem(144);
					controller.sleep(640);
				}
			}
			controller.closeBank();
			controller.displayMessage("Closing Bank");
		}
	}
}