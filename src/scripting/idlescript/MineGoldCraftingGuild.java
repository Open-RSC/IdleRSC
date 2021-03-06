package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * MineGoldCraftinGuild by Searos
 * @author Searos
 */
public class MineGoldCraftingGuild extends IdleScript {
	JCheckBox silver2 = new JCheckBox("Mine Silver", true);
	int a = 0;
	int[] gold = { 112, 113 };
	int[] silver = { 195, 196, 197 };
	int bankedSilver = 0;
	int bankedGold = 0;
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;

	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		while (controller.isRunning() && scriptStarted) {
			scriptStart();
		}
		return 1000; //start() must return a int value now. 
	}

	public void startWalking(int x, int y) {
		// shitty autowalk
		int newX = x;
		int newY = y;
		while (controller.currentX() != x || controller.currentY() != y) {
			if (controller.currentX() - x > 23) {
				newX = controller.currentX() - 20;
			}
			if (controller.currentY() - y > 23) {
				newY = controller.currentY() - 20;
			}
			if (controller.currentX() - x < -23) {
				newX = controller.currentX() + 20;
			}
			if (controller.currentY() - y < -23) {
				newY = controller.currentY() + 20;
			}
			if (Math.abs(controller.currentX() - x) <= 23) {
				newX = x;
			}
			if (Math.abs(controller.currentY() - y) <= 23) {
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
		
		while (controller.getNearestObjectById(112) != null && controller.getInventoryItemCount() < 30
				|| controller.getNearestObjectById(113) != null && controller.getInventoryItemCount() < 30) {
			controller.setStatus("Mining Gold");
			for (int objId : gold) {
				if (objId != 0) {
					while (controller.getInventoryItemCount() < 30 && controller.getNearestObjectById(objId) != null) {
						controller.atObject(controller.getNearestObjectById(objId)[0],
								controller.getNearestObjectById(objId)[1]);
						controller.sleep(640);
						while (controller.isBatching() && controller.getInventoryItemCount() < 30) {
							controller.sleep(10);
						}
					}
				}
			}
		}
		while (controller.getNearestObjectById(112) == null && controller.getNearestObjectById(113) == null
				&& controller.getInventoryItemCount() < 30 && silver2.isSelected()) {
			controller.setStatus("Mining Silver");
			for (int objId : silver) {
				if (objId != 0) {
					while (controller.getInventoryItemCount() < 30 && controller.getNearestObjectById(objId) != null
							&& controller.getNearestObjectById(112) == null
							&& controller.getNearestObjectById(113) == null) {
						controller.atObject(controller.getNearestObjectById(objId)[0],
								controller.getNearestObjectById(objId)[1]);
						controller.sleep(640);
						while (controller.isBatching() && controller.getInventoryItemCount() < 30
								&& controller.getNearestObjectById(112) == null
								&& controller.getNearestObjectById(113) == null) {
							controller.sleep(10);
						}
					}
				}
			}
		}
		if (!controller.isAuthentic() && controller.getInventoryItemCount() == 30) {
			controller.setStatus("Banking");
			if (controller.getInventoryItemCount() == 30 && controller.getNearestObjectById(942) != null) {
				while (!controller.isInBank()) {
					controller.atObject(controller.getNearestObjectById(942)[0],
							controller.getNearestObjectById(942)[1]);
					controller.sleep(640);
				}
				while (controller.isInBank() && controller.getInventoryItemCount() > 1) {
					for (int itemId : controller.getInventoryItemIds()) {
						if (itemId != 0) {
							controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
						}
					}
					bankedSilver = controller.getBankItemCount(383);
					bankedGold = controller.getBankItemCount(152);
				}
			}
		}

		if (controller.isAuthentic() && controller.getInventoryItemCount() == 30) {
			controller.setStatus("Leaving Guild");
			while (controller.currentY() > 600) {
				controller.openDoor(347, 601);
				controller.sleep(430);
			}
			while (controller.getNearestNpcById(95, false) == null) {
				controller.setStatus("Walking to Bank");
				startWalking(controller.getNearestBank()[0], controller.getNearestBank()[1]);
			}
			while (controller.getNearestNpcById(95, false) != null && controller.getInventoryItemCount() == 30) {
				controller.setStatus("Banking");
				while (!controller.isInBank()) {
					controller.openBank();
					controller.sleep(640);
				}
				while (controller.isInBank() && controller.getInventoryItemCount() > 1) {
					for (int itemId : controller.getInventoryItemIds()) {
						if (itemId != 0) {
							controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
						}
					}
					bankedSilver = controller.getBankItemCount(383);
					bankedGold = controller.getBankItemCount(152);
				}
			}
			while (controller.currentX() != 347 && controller.currentY() !=  600) {
				controller.setStatus("Walking to Guild");
				startWalking(347, 600);
			}
			while (controller.currentY() < 601) {
				controller.setStatus("Entering guild");
				controller.openDoor(347, 601);
				controller.sleep(430);
			}
		}
	}

	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	public void setupGUI() {
		JLabel header = new JLabel("Mine Gold at crafting guild");
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				scriptStarted = true;
				controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
				controller.displayMessage("@red@MineGold started");

			}
		});

		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(silver2);
		scriptFrame.add(startScriptButton);
		scriptFrame.setVisible(true);
		centerWindow(scriptFrame);
		scriptFrame.pack();
		scriptFrame.requestFocus();
	}

	@Override
	public void paintInterrupt() {
		if (controller != null) {
			controller.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
			controller.drawString("@red@MineGold @gre@by Searos", 10, 21, 0xFFFFFF, 1);
			controller.drawString("@red@Gold in Bank: @yel@" + String.valueOf(this.bankedGold), 10, 35, 0xFFFFFF, 1);
			controller.drawString("@red@Silver in bank: @yel@" + String.valueOf(this.bankedSilver), 10, 49, 0xFFFFFF, 1);
		}
	}
}