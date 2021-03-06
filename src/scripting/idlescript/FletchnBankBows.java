package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * FletchnBankBows by Searos
 * @author Searos
 */
public class FletchnBankBows extends IdleScript {
	JCheckBox string = new JCheckBox("String", true);
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int bowId = -1;
	int logId = -1;
	int bowComplete = -1;
	int ans = -1;
	int[] bowIdsShort = { 277, 659, 661, 663, 665, 667 };
	int[] bowIdsLong = { 276, 658, 660, 662, 664, 666 };
	int[] bowCompleteShort = { 189, 649, 651, 653, 655, 657 };
	int[] bowCompleteLong = { 188, 648, 650, 652, 654, 656 };
	int[] logIds = { 14, 632, 633, 634, 635, 636 };
	int totalBows = 0;
	int totalFletched = 0;
	boolean bankTime = false;
	boolean stringTime = false;
	int[] bankerIds = { 95, 224, 268, 485, 540, 617 };

	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}

		if (scriptStarted) {
			scriptStart();
		}
		
		return 1000; //start() must return a int value now. 
	}

	public void scriptStart() {
		while (controller.isRunning()) {
			if (controller.getInventoryItemCount(logId) < 1 && !stringTime && !controller.isInBank()) {
				controller.setStatus("Banking");
				controller.openBank();
				controller.sleep(431);
				if (controller.getInventoryItemCount() > 1) {
					for (int itemId : controller.getInventoryItemIds()) {
						if (itemId != 13 && itemId != 1263) {
							if(controller.getInventoryItemCount(itemId) == bowId) {
								totalFletched = totalFletched + controller.getInventoryItemCount(itemId);
							}
							if(controller.getInventoryItemCount(itemId) == bowComplete) {
								totalBows = totalBows + controller.getInventoryItemCount(itemId);
							}
							controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
						}
					}
					controller.sleep(429);
				}
				if (controller.getInventoryItemCount(13) < 1) {
					controller.withdrawItem(13, 1);
				}
				if (controller.getBankItemCount(logId) < 1) {
					if (string.isSelected()) {
						stringTime = true;
						return;
					} else {
						stringTime = false;
						scriptStarted = false;
						guiSetup = false;
						return;
					}
				}
				if (!stringTime) {
					controller.withdrawItem(logId, 29);
					controller.sleep(100);
				}
				controller.closeBank();
				controller.sleep(1000);
			}
			if (controller.getInventoryItemCount(bowId) < 1 && stringTime && !controller.isInBank()
					|| controller.getInventoryItemCount(676) < 1 && stringTime && !controller.isInBank()) {
				controller.setStatus("Banking");
				controller.openBank();
				controller.sleep(431);
				{
					if (controller.getInventoryItemCount() > 1) {
						for (int itemId : controller.getInventoryItemIds()) {
							if (itemId != 0 && itemId != 1263) {
								if(controller.getInventoryItemCount(itemId) == bowId) {
									totalFletched = totalFletched + controller.getInventoryItemCount(itemId);
								}
								if(controller.getInventoryItemCount(itemId) == bowComplete) {
									totalBows = totalBows + controller.getInventoryItemCount(itemId);
								}
								controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
							}
						}
						controller.sleep(429);
					}
					if (controller.getInventoryItemCount(13) > 0) {
						controller.depositItem(13, 1);
					}
					if (controller.getBankItemCount(bowId) < 1 || controller.getBankItemCount(676) < 1 ) {
						stringTime = false;
						scriptStarted = false;
						guiSetup = false;
						return;
					}
					controller.withdrawItem(bowId, 15);
					controller.withdrawItem(676, 15);
					controller.sleep(1500);
					controller.closeBank();
				}

			}
			while (controller.getInventoryItemCount(logId) > 0 && !stringTime) {
				controller.setStatus("Fletching Bow");
				controller.sleepHandler(98, true);
				controller.useItemOnItemBySlot(0, 1);
				controller.sleep(500);
				if (controller.isInOptionMenu()) {
					controller.optionAnswer(ans);
				}
				controller.sleep(100);
				while (controller.isBatching()) {
					controller.sleep(100);
				}
			}
			while (controller.getInventoryItemCount(bowId) > 0 && stringTime
					&& controller.getInventoryItemCount(676) > 0) {
				controller.setStatus("Stringing");
				controller.useItemOnItemBySlot(controller.getInventoryItemSlotIndex(bowId),
						controller.getInventoryItemSlotIndex(676));
				controller.sleep(15);
			}
		}
		stringTime = false;
		scriptStarted = false;
		guiSetup = false;
	}

	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	public void setupGUI() {
		JLabel header = new JLabel("Woodcutting");
		JLabel logLabel = new JLabel("Log Type:");
		JComboBox<String> logField = new JComboBox<String>(
				new String[] { "Normal", "Oak", "Willow", "Maple", "Yew", "Magic" });
		JLabel bowLabel = new JLabel("Bow Type:");
		JComboBox<String> bowField = new JComboBox<String>(new String[] { "Short", "Long" });
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (bowField.getSelectedIndex() == 1) {
					bowId = bowIdsLong[logField.getSelectedIndex()];
					bowComplete = bowCompleteLong[logField.getSelectedIndex()];
				} else {
					bowId = bowIdsShort[logField.getSelectedIndex()];
					bowComplete = bowCompleteShort[logField.getSelectedIndex()];
				}
				ans = bowField.getSelectedIndex() + 1;
				logId = logIds[logField.getSelectedIndex()];
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				scriptStarted = true;
				controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
				controller.displayMessage("@red@Fletcher started");
			}
		});

		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(logLabel);
		scriptFrame.add(logField);
		scriptFrame.add(bowLabel);
		scriptFrame.add(bowField);
		scriptFrame.add(string);
		scriptFrame.add(startScriptButton);
		centerWindow(scriptFrame);
		scriptFrame.setVisible(true);
		scriptFrame.pack();
		scriptFrame.requestFocus();
	}
	@Override
	public void paintInterrupt() {
		if (controller != null) {
			controller.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
			controller.drawString("@red@Fletch Bows @gre@by Searos", 10, 21, 0xFFFFFF, 1);
			controller.drawString("@red@Unstrung Bows Crafted: @yel@" + String.valueOf(this.totalFletched), 10, 35, 0xFFFFFF, 1);
			controller.drawString("@red@Bows Completed: @yel@" + String.valueOf(this.totalBows), 10, 49, 0xFFFFFF, 1);
		}
	}
}