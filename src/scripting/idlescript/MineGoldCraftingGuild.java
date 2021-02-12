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

public class MineGoldCraftingGuild extends IdleScript {
	JCheckBox silver2 = new JCheckBox("Mine Silver", true);
	int a = 0;
	int[] gold = { 112, 113 };
	int[] silver = { 195, 196, 197 };
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
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
		while (controller.getNearestObjectById(112) != null&& controller.getInventoryItemCount() < 30
				|| controller.getNearestObjectById(113) != null && controller.getInventoryItemCount() < 30) {
			controller.displayMessage((controller.getBankItemCount(152)+controller.getInventoryItemCount(152))+" gold collected");
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
			controller.displayMessage((controller.getBankItemCount(383)+controller.getInventoryItemCount(383))+" silver in collected");
			for (int objId : silver) {
				if (objId != 0) {
					while (controller.getInventoryItemCount() < 30
							&& controller.getNearestObjectById(objId) != null && controller.getNearestObjectById(112) == null && controller.getNearestObjectById(113) == null) {
						controller.atObject(controller.getNearestObjectById(objId)[0],
								controller.getNearestObjectById(objId)[1]);
						controller.sleep(640);
						while (controller.isBatching() && controller.getInventoryItemCount() < 30 && controller.getNearestObjectById(112) == null && controller.getNearestObjectById(113) == null) {
							controller.sleep(10);
						}
					}
				}
			}
		}

		if (controller.getInventoryItemCount() == 30 && controller.getNearestObjectById(942) != null) {
			while (!controller.isInBank()) {
				controller.atObject(controller.getNearestObjectById(942)[0], controller.getNearestObjectById(942)[1]);
				controller.sleep(640);
			}
			while (controller.isInBank() && controller.getInventoryItemCount() > 1) {
				for (int itemId : controller.getInventoryItemIds()) {
					if (itemId != 0) {
						controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
					}
				}

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
}