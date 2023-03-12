package scripting.idlescript;

import orsc.ORSCharacter;

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

/**
 * Fast Bank Bury.
 *
 *      Selectable Bone Id.
 *      start in bank with unnoted bones in bank.
 *      Will withdraw and bury bones.
 *
 *  todo
 *      add gui and statistics.
 *
 *  Author - Kaila
 */
public class K_FastBankBury extends IdleScript {
	JFrame scriptFrame = null;
	int boneId = -1;
	int[] boneIds = { 20, 413, 604, 814};
	boolean guiSetup = false;
	boolean scriptStarted = false;

	long startTimestamp = System.currentTimeMillis() / 1000L;

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
		while(controller.isRunning()) {
			if(controller.getInventoryItemCount(boneId) < 1) {
                if (!controller.isInBank()) {
                    int[] bankerIds = {95, 224, 268, 540, 617, 792};
                    ORSCharacter npc = controller.getNearestNpcByIds(bankerIds, false);
                    if (npc != null) {
                        controller.setStatus("@yel@Walking to Banker..");
                        controller.displayMessage("@yel@Walking to Banker..");
                        controller.walktoNPCAsync(npc.serverIndex);
                        controller.sleep(200);
                    } else {
                        controller.log("@red@Error..");
                        controller.sleep(1000);
                    }
                }
                controller.setStatus("@yel@Banking..");
                controller.displayMessage("@gre@Banking..");
				bank();
				controller.sleep(1200);
			}
			if(controller.getInventoryItemCount(boneId) > 0) {
				controller.setStatus("@yel@Burying..");
				controller.itemCommand(boneId);
				controller.sleep(100);
			}
		}

	//	return 1000; //start() must return a int value now.
	}



public void bank() {

	controller.setStatus("@yel@Banking..");
	controller.openBank();
	controller.sleep(640);

	if (controller.isInBank()) {
		if(controller.getInventoryItemCount(boneId) < 30) {
			controller.withdrawItem(boneId, 30);
		}
		controller.closeBank();
	}
}

	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	public void setupGUI() {
		JLabel header = new JLabel("Fast Bone Bury");
		JLabel boneLabel = new JLabel("bone Type:");
		JComboBox<String> boneField = new JComboBox<String>(
				new String[] { "Normal Bones", "Big Bones", "Bat Bones", "Dragon Bones" });
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boneId = boneIds[boneField.getSelectedIndex()];
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				scriptStarted = true;
				controller.displayMessage("@gre@" + '"' + "Fast Bone Bury" + '"' + " - by Kaila");
				controller.displayMessage("@gre@Start in any bank");
			}
		});

		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(boneLabel);
		scriptFrame.add(boneField);
		scriptFrame.add(startScriptButton);
		centerWindow(scriptFrame);
		scriptFrame.setVisible(true);
		scriptFrame.pack();
		scriptFrame.requestFocus();

	}

}

