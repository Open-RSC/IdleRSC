package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Math;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SpinStrings extends IdleScript {
	JComboBox<String> item = new JComboBox<String>(new String[] { "Flax", "Wool" });
	JComboBox<String> destination = new JComboBox<String>(new String[] { "Seers", "Falador" });
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int[] bankerIds = { 95, 224, 268, 485, 540, 617 };
	int[] bankX = { 500, 289 };
	int[] bankY = { 455, 571 };
	int[] inputIds = { 675, 145 };
	int[] outputIds = { 676, 207 };
	int input = -1;
	int output = -1;
	int totalString = 0;
	boolean upstairs = false;
	boolean started = false;
	int bankSelX = -1;
	int bankSelY = -1;

	public void start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (scriptStarted) {
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
		while (controller.isRunning()) {
			// if player is upstairs, not batching, and not banking, spin flax.
			while (controller.getNearestObjectById(121) != null && controller.getInventoryItemCount(input) > 0
					&& destination.getSelectedIndex() == 0 && controller.getNearestObjectById(121) != null) {
				controller.displayMessage("@red@Spinning Flax");
				while (!controller.isBatching()) {
					controller.sleepHandler(98, true);
					controller.useItemIdOnObject(controller.getNearestObjectById(121)[0],
							controller.getNearestObjectById(121)[1], input);
					controller.sleep(640);
				}
				while (controller.isBatching()) {
					controller.sleep(100);
				}
			}
			while (destination.getSelectedIndex() == 1 && controller.getNearestObjectById(121) != null
					&& controller.getInventoryItemCount(input) > 0) {
				controller.displayMessage("@red@Spinning Flax");
				while (!controller.isBatching()) {
					controller.sleepHandler(98, true);
					controller.useItemIdOnObject(controller.getNearestObjectById(121)[0],
							controller.getNearestObjectById(121)[1], input);
					controller.sleep(640);
				}
				while (controller.isBatching()) {
					controller.sleep(100);
				}
			}
			while (controller.getNearestObjectById(121) == null && controller.getInventoryItemCount(input) > 0
					&& destination.getSelectedIndex() == 0) {
				controller.displayMessage("@red@Going upstairs");
				while (controller.isTileEmpty(525, 462)) {
					startWalking(524, 463);
				}
				while (!controller.isTileEmpty(525, 462)) {
					controller.atObject(525, 462);
					controller.sleep(640);
				}
				return;
			}
			while (controller.getInventoryItemCount(input) > 0 && destination.getSelectedIndex() == 1
					&& controller.getNearestObjectById(121) == null) {
				startWalking(577, 295);
			}
			while (controller.getNearestObjectById(121) != null && controller.getInventoryItemCount(input) == 0
					&& destination.getSelectedIndex() == 0) {
				controller.displayMessage("@red@Going downstairs");
				controller.atObject(525, 1406);
				controller.sleep(100);
			}
			while (controller.getNearestNpcByIds(bankerIds, false) == null
					&& controller.getInventoryItemCount(input) == 0) {
				controller.displayMessage("@red@Walking to bank");
				startWalking(bankSelX, bankSelY);
			}
			controller.displayMessage("@red@Banking");
			while (!controller.isInBank() && controller.getNearestNpcByIds(bankerIds, false) != null
					&& controller.getInventoryItemCount(input) == 0) {
				controller.openBank();
			}
			totalString = totalString + controller.getInventoryItemCount(output);
			while (controller.getInventoryItemCount(input) == 0 && controller.getInventoryItemCount() > 0
					&& controller.isInBank()) {
				for (int itemId : controller.getInventoryItemIds()) {
					if (itemId != 0) {
						controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
						controller.sleep(10);
					}
				}
			}
			controller.withdrawItem(input, 30);
			controller.sleep(618);
			controller.closeBank();
			controller.displayMessage("@red@Finished Banking");
			controller.displayMessage("@yel@ Banked " + totalString + " bowstrings");
		}
	}

	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	public void setupGUI() {
		JLabel header = new JLabel("FlaxtoString");
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				scriptStarted = true;
				controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
				bankSelX = bankX[destination.getSelectedIndex()];
				bankSelY = bankY[destination.getSelectedIndex()];
				input = inputIds[item.getSelectedIndex()];
				output = outputIds[item.getSelectedIndex()];
				controller.displayMessage("@red@FlaxtoString started");
			}
		});

		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(item);
		scriptFrame.add(destination);
		scriptFrame.add(startScriptButton);
		centerWindow(scriptFrame);
		scriptFrame.setVisible(true);
		scriptFrame.pack();
		scriptFrame.requestFocus();
	}
}