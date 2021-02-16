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

public class ArrowMaker extends IdleScript {
	int[] arrowHeads = { 669, 670, 671, 672, 673, 674, 381 };
	int selectedArrowHead = -1;
	boolean scriptStarted = false;
	boolean guiSetup = false;
	boolean headless = false;
	JFrame scriptFrame = null;

	public void start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}

		if (scriptStarted) {
			while (controller.isRunning()) {
				scriptStart();
			}
		}
	}

	public void scriptStart() {
		if (headless) {
			while (controller.isRunning()) {
				while (controller.getInventoryItemCount(280) > 9
						&& controller.getInventoryItemCount(selectedArrowHead) > 9) {
					controller.useItemOnItemBySlot(controller.getInventoryItemIdSlot(280),
							controller.getInventoryItemIdSlot(selectedArrowHead));
					controller.sleep(100);
					while (controller.isBatching()) {
						controller.sleep(100);
					}
				}
				if (controller.getInventoryItemCount(280) < 9
						|| controller.getInventoryItemCount(selectedArrowHead) < 9) {
					scriptStarted = false;
					guiSetup = false;
					controller.stop();
				}
			}
		}
		if (!headless) {
			while (controller.isRunning()) {
				while (controller.getInventoryItemCount(637) > 9
						&& controller.getInventoryItemCount(selectedArrowHead) > 9) {
					controller.useItemOnItemBySlot(controller.getInventoryItemIdSlot(637),
							controller.getInventoryItemIdSlot(selectedArrowHead));
					controller.sleep(100);
					while (controller.isBatching()) {
						controller.sleep(100);
					}
				}
				if (controller.getInventoryItemCount(637) < 9
						|| controller.getInventoryItemCount(selectedArrowHead) < 9) {
					scriptStarted = false;
					guiSetup = false;
					controller.stop();
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
		JLabel header = new JLabel("Select Arrowhead");
		JButton startScriptButton = new JButton("Start");
		JComboBox<String> arrowHead = new JComboBox<String>(
				new String[] { "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Headless" });
		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedArrowHead = arrowHeads[arrowHead.getSelectedIndex()];
				if (arrowHead.getSelectedIndex() == 7) {
					headless = true;
				}
				scriptStarted = true;
				controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
				controller.displayMessage("@red@ArrowMaker started");

			}
		});

		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(arrowHead);
		scriptFrame.add(startScriptButton);
		scriptFrame.setVisible(true);
		centerWindow(scriptFrame);
		scriptFrame.pack();
		scriptFrame.requestFocus();
	}
}