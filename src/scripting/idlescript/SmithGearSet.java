package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SmithGearSet extends IdleScript {
	JFrame scriptFrame = null;
	JComboBox<String> barField = new JComboBox<String>(
			new String[] { "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite" });
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int barsLeft = -1;
	int selectedBar = -1;
	boolean bankTime = true;
	int[] barType = { 169, 170, 171, 173, 174, 408 };
	int[][] itemType = { { 108, 117, 206, 124, 205, 76 }, { 6, 8, 9, 2, 89, 77 }, { 109, 118, 121, 129, 90, 78 },
			{ 110, 119, 122, 130, 91, 79 }, { 111, 120, 123, 131, 92, 80 }, { 112, 401, 402, 404, 93, 81 } };

	public void start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		while (controller.isRunning() && scriptStarted) {
			scriptStart();
		}
	}

	public void scriptStart() {
		while (controller.isRunning()) {
			while (bankTime && !controller.isInBank()) {
				controller.openBank();
				controller.sleep(1000);
				while (controller.getInventoryItemCount() > 1 && controller.isInBank()) {
					for (int itemId : controller.getInventoryItemIds()) {
						if (itemId != 168 && itemId != 1263) {
							controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
						}
					}
					controller.sleep(429);
				}
				if (controller.getInventoryItemCount(168) < 1) {
					controller.withdrawItem(168, 1);
				}
				if (controller.getInventoryItemCount(408) < 1) {
					controller.withdrawItem(selectedBar, 29);
					controller.sleep(1000);
				}
				controller.closeBank();
				controller.sleep(1280);
				bankTime = false;
			}
			
			while (controller.getInventoryItemCount(selectedBar) > 1 && !bankTime
					&& (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][0])
							+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][0])) <= (controller
									.getInventoryItemCount(itemType[barField.getSelectedIndex()][1])
									+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][1]))) {
				controller.useItemIdOnObject(controller.getNearestObjectById(50)[0],
						controller.getNearestObjectById(50)[1], selectedBar);
				controller.sleep(640);
				if (controller.isInOptionMenu()) {
					controller.optionAnswer(1);
					controller.sleep(640);
					controller.optionAnswer(0);
					controller.sleep(640);
					controller.optionAnswer(1);
					controller.sleep(640);
					controller.optionAnswer(0);
				}
				controller.sleep(640);
			}
			while (controller.getInventoryItemCount(selectedBar) > 4 && !bankTime
					&& (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][1])
							+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][1])) <= (controller
									.getInventoryItemCount(itemType[barField.getSelectedIndex()][2])
									+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][2]))) {
				controller.sleepHandler(98, true);
				controller.useItemIdOnObject(controller.getNearestObjectById(50)[0],
						controller.getNearestObjectById(50)[1], selectedBar);
				controller.sleep(640);
				if (controller.isInOptionMenu()) {
					controller.optionAnswer(1);
					controller.sleep(640);
					controller.optionAnswer(2);
					controller.sleep(640);
					controller.optionAnswer(2);
					controller.sleep(640);
					controller.optionAnswer(0);
				}
				controller.sleep(640);
			}
			while (controller.getInventoryItemCount(selectedBar) > 2 && !bankTime
					&& (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][2])
							+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][2])) <= (controller
									.getInventoryItemCount(itemType[barField.getSelectedIndex()][3])
									+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][3]))) {
				controller.sleepHandler(98, true);
				controller.useItemIdOnObject(controller.getNearestObjectById(50)[0],
						controller.getNearestObjectById(50)[1], selectedBar);
				controller.sleep(640);
				if (controller.isInOptionMenu()) {
					controller.optionAnswer(1);
					controller.sleep(640);
					controller.optionAnswer(2);
					controller.sleep(640);
					controller.optionAnswer(3);
					controller.sleep(640);
					controller.optionAnswer(0);
				}
				controller.sleep(640);
			}
			while (controller.getInventoryItemCount(selectedBar) > 2 && !bankTime
					&& (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][3])
							+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][3])) <= (controller
									.getInventoryItemCount(itemType[barField.getSelectedIndex()][4])
									+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][4]))) {
				controller.sleepHandler(98, true);
				controller.useItemIdOnObject(controller.getNearestObjectById(50)[0],
						controller.getNearestObjectById(50)[1], selectedBar);
				controller.sleep(640);
				if (controller.isInOptionMenu()) {
					controller.optionAnswer(1);
					controller.sleep(640);
					controller.optionAnswer(1);
					controller.sleep(640);
					controller.optionAnswer(1);
					controller.sleep(640);
					controller.optionAnswer(0);
				}
				controller.sleep(640);
			}
			while (controller.getInventoryItemCount(selectedBar) > 2 && !bankTime
					&& (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][4])
							+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][4])) <= (controller
									.getInventoryItemCount(itemType[barField.getSelectedIndex()][5])
									+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][5]))) {
				controller.sleepHandler(98, true);
				controller.useItemIdOnObject(controller.getNearestObjectById(50)[0],
						controller.getNearestObjectById(50)[1], selectedBar);
				controller.sleep(640);
				if (controller.isInOptionMenu()) {
					controller.optionAnswer(0);
					controller.sleep(640);
					controller.optionAnswer(3);
					controller.sleep(640);
					controller.optionAnswer(1);
					controller.sleep(640);
					controller.optionAnswer(0);
				}
				controller.sleep(640);
			}
			while (controller.getInventoryItemCount(selectedBar) > 2 && !bankTime
					&& (controller.getInventoryItemCount(itemType[barField.getSelectedIndex()][5])
							+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][5])) <= (controller
									.getInventoryItemCount(itemType[barField.getSelectedIndex()][0])
									+ controller.getBankItemCount(itemType[barField.getSelectedIndex()][0]))) {
				controller.sleepHandler(98, true);
				controller.useItemIdOnObject(controller.getNearestObjectById(50)[0],
						controller.getNearestObjectById(50)[1], selectedBar);
				controller.sleep(640);
				if (controller.isInOptionMenu()) {
					controller.optionAnswer(0);
					controller.sleep(640);
					controller.optionAnswer(2);
					controller.sleep(640);
					controller.optionAnswer(3);
					controller.sleep(640);
					controller.optionAnswer(0);
				}
				controller.sleep(640);
			}
			if (controller.getInventoryItemCount(selectedBar) < 5) {
				bankTime = true;
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
		JLabel header = new JLabel("Smithing");
		JButton startScriptButton = new JButton("Start");
		JLabel barLabel = new JLabel("Bar Type:");
		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				selectedBar = barType[barField.getSelectedIndex()];
				scriptStarted = true;
				controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
				controller.displayMessage("@red@SmithGearSet started");

			}
		});
		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(barLabel);
		scriptFrame.add(barField);
		centerWindow(scriptFrame);
		scriptFrame.add(startScriptButton);
		centerWindow(scriptFrame);
		scriptFrame.setVisible(true);
		scriptFrame.pack();
		scriptFrame.requestFocus();
	}
}