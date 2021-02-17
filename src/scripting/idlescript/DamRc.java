package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import orsc.ORSCharacter;

public class DamRc extends IdleScript {

	public boolean started = false, useObj2 = false, debug, mineEss = false;
	public String status, method;

	int[] bankNW, bankSE, spotNW, spotSE, alterNW, alterSE, mineNW, mineSE;
	int auburyId = 54;
	int taliId;
	int runeId;
	int alterId;
	int alterZ;
	int essId = 1299;
	int portalId;
	int ruinsId;
	int essRockId = 1227;
	int[] toBank;
	int[] toSpot;

	JFrame scriptFrame = null;
	boolean guiSetup = false;

	public boolean inArea(int[] nwTile, int[] seTile) {
		if (controller.currentX() <= nwTile[0] && controller.currentX() >= seTile[0]
				&& controller.currentY() >= nwTile[1] && controller.currentY() <= seTile[1]) {
			return true;
		}
		return false;
	}

	public void start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (started) {
			scriptStart();
		}
	}

	public void scriptStart() {
		while (controller.isRunning()) {
			if (debug) {
				controller.displayMessage("@cya@" + "Current Z: " + controller.currentY());
			}
			if (mineEss) {
				if (inArea(mineNW, mineSE)) {
					if (controller.getInventoryItemCount() >= 30) {
						useObject(portalId);
					} else {
						if (!controller.isBatching()) {
							useObject(essRockId);
						}
					}
				}
				if (!inArea(mineNW, mineSE) && controller.currentY() > 30 && controller.currentY() <= 600) {
					if (controller.getInventoryItemCount() >= 30) {
						if (!inArea(bankNW, bankSE)) {
							walkToBank();
						} else {
							bank();
						}
					} else {
						if (!inArea(spotNW, spotSE)) {
							walkToSpot();
						} else {
							teleport();
						}
					}
				}
			}
			if (!mineEss) {
				if (inArea(alterNW, alterSE)) {
					if (controller.getInventoryItemCount() >= 30) {
						useObject(alterId);
					} else {
						useObject(portalId);
					}
				}

				if (!inArea(alterNW, alterSE) && controller.currentY() > alterZ && controller.currentY() <= 600) {
					if (controller.getInventoryItemCount() >= 30) {
						if (!inArea(spotNW, spotSE)) {
							walkToSpot();
						} else {
							useObject(ruinsId);
						}
					} else {
						if (!inArea(bankNW, bankSE)) {
							walkToBank();
						} else {
							bank();
						}
					}
				}
			}
		}
		controller.sleep(640);

	}

	public void teleport() {
		ORSCharacter aubury = controller.getNearestNpcById(auburyId, false);
		status = "Teleporting to mine";
		if (debug) {	
			controller.displayMessage("@cya@" + "Teleporting to ess");
		}
		if (aubury != null && aubury.serverIndex > 0) {
			controller.npcCommand1(aubury.serverIndex);
			controller.sleep(600);
		}
	}

	public void useObject(int i) {
		int[] objID = controller.getNearestObjectById(i);
		try {
			if (objID.length > 0) {
				status = "Interacting with object id: " + i;
				if (debug) {
					controller.displayMessage("@cya@" + "Interacting with object id:" + i);
				}
				controller.atObject(objID[0], objID[1]);
				controller.sleep(640);
			}
		} catch (NullPointerException ignored) {

		}

	}

	public void bank() {
		if (controller.isInBank()) {

			if (controller.getInventoryItemCount(runeId) > 0) {
				status = "Deposit runes";
				if (debug) {	
					controller.displayMessage("@cya@" + "Deposit runes");
				}
				controller.depositItem(runeId, controller.getInventoryItemCount(runeId));
			} else {
				status = "Withdraw ess";
				if (debug) {	
					controller.displayMessage("@cya@" + "Withdraw ess");
				}
				controller.withdrawItem(essId, 29);
				controller.sleep(640);
			}

		} else {
			status = "Open bank";
			if (debug) {
				controller.displayMessage("@cya@" + "Open bank");
			}
			if (!controller.isCurrentlyWalking()) {
				controller.openBank();
				controller.sleep(640);
			}
		}
	}

	public void walkToBank() {
		status = "Walk to bank";
		if (debug) {
			controller.displayMessage("@cya@" + "Walk to bank");
		}
		controller.walkPath(toBank);
		controller.sleep(640);
	}

	public void walkToSpot() {
		status = "Walk to tele spot";
		if (debug) {
			controller.displayMessage("@cya@" + "Walk to tele spot");
		}
		controller.walkPath(toSpot);
		controller.sleep(640);
	}

	class guiObject {
		String name;

		public guiObject(String _name) {
			name = _name;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof guiObject) {
				if (((guiObject) o).name.equals(this.name)) {
					return true;
				}
			}

			return false;
		}
	}

	ArrayList<guiObject> objects = new ArrayList<guiObject>() {
		{
			add(new guiObject("Air - Fally south"));
			add(new guiObject("Mind - Fally north"));
			add(new guiObject("Earth - Varrock east"));
			add(new guiObject("Water - Draynor"));
			add(new guiObject("Fire -  Al Kharid"));
			add(new guiObject("Body - Edge"));
			add(new guiObject("Mine ess - Varrock east"));

		}
	};

	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	public void setValuesFromGUI(int i) {
		if (i == 0) {
			taliId = 1300;
			alterId = 1191;
			ruinsId = 1190;
			portalId = 1214;
			runeId = 33;
			alterZ = 25;
			toBank = new int[] { 303, 588, 296, 584, 290, 578, 284, 570 };
			toSpot = new int[] { 290, 578, 296, 584, 303, 588, 307, 592 };
			bankNW = new int[] { 286, 564 };
			bankSE = new int[] { 280, 573 };
			spotNW = new int[] { 313, 587 };
			spotSE = new int[] { 301, 600 };
			alterNW = new int[] { 986, 17 };
			alterSE = new int[] { 980, 22 };
			method = "Air rune crafting";
			controller.displayMessage("@cya@" + "We're crafting airs");
		}
		if (i == 1) {
			taliId = 1301;
			alterId = 1193;
			ruinsId = 1192;
			portalId = 1215;
			runeId = 35;
			alterZ = 25;
			toBank = new int[] { 299, 445, 304, 455, 309, 465, 312, 474, 313, 484, 310, 492, 304, 499, 302, 510, 313,
					516, 315, 525, 314, 534, 321, 541, 326, 547, 330, 553 };
			toSpot = new int[] { 326, 547, 321, 541, 314, 534, 315, 525, 313, 516, 302, 510, 304, 499, 310, 492, 313,
					484, 312, 475, 309, 465, 304, 455, 299, 445, 298, 441 };
			bankNW = new int[] { 334, 549 };
			bankSE = new int[] { 328, 557 };
			spotNW = new int[] { 302, 435 };
			spotSE = new int[] { 295, 442 };
			alterNW = new int[] { 942, 13 };
			alterSE = new int[] { 928, 29 };
			method = "Mind rune crafting";
			controller.displayMessage("@cya@" + "We're crafting minds");
		}
		if (i == 2) {
			taliId = 1303;
			alterId = 1197;
			ruinsId = 1196;
			portalId = 1217;
			runeId = 34;
			alterZ = 75;
			toBank = new int[] { 65, 472, 65, 481, 64, 493, 72, 502, 82, 506, 90, 508, 97, 509, 102, 511 };
			toSpot = new int[] { 97, 509, 90, 508, 82, 506, 72, 502, 64, 493, 65, 481, 65, 472, 63, 467 };
			bankNW = new int[] { 106, 510 };
			bankSE = new int[] { 98, 515 };
			spotNW = new int[] { 67, 461 };
			spotSE = new int[] { 60, 469 };
			alterNW = new int[] { 939, 63 };
			alterSE = new int[] { 929, 77 };
			method = "Earth rune crafting";
			controller.displayMessage("@cya@" + "We're crafting earths");
		}
		if (i == 3) {
			taliId = 1302;
			alterId = 1195;
			ruinsId = 1194;
			portalId = 1216;
			runeId = 32;
			alterZ = 70;
			toBank = new int[] { 155, 676, 162, 673, 172, 668, 182, 662, 181, 659, 201, 654, 210, 650, 214, 641, 219,
					635 };
			toSpot = new int[] { 214, 641, 210, 650, 201, 654, 181, 659, 182, 662, 172, 668, 162, 673, 155, 676, 150,
					684 };
			bankNW = new int[] { 223, 634 };
			bankSE = new int[] { 216, 638 };
			spotNW = new int[] { 152, 681 };
			spotSE = new int[] { 145, 689 };
			alterNW = new int[] { 991, 60 };
			alterSE = new int[] { 980, 75 };
			method = "Water rune crafting";
			controller.displayMessage("@cya@" + "We're crafting waters");
		}
		if (i == 4) {
			taliId = 1304;
			alterId = 1199;
			ruinsId = 1198;
			portalId = 1218;
			runeId = 31;
			alterZ = 30;
			toBank = new int[] { 58, 641, 67, 647, 74, 656, 83, 662, 81, 671, 80, 680, 90, 694 };//
			toSpot = new int[] { 80, 680, 81, 671, 83, 662, 74, 656, 67, 647, 58, 641, 51, 636 };//
			bankNW = new int[] { 93, 689 };//
			bankSE = new int[] { 87, 700 };//
			spotNW = new int[] { 54, 631 };
			spotSE = new int[] { 48, 638 };
			alterNW = new int[] { 894, 15 };
			alterSE = new int[] { 882, 28 };
			method = "Fire rune crafting";
			controller.displayMessage("@cya@" + "We're crafting fires");
		}
		if (i == 5) {
			taliId = 1305;
			alterId = 1201;
			ruinsId = 1200;
			portalId = 1219;
			runeId = 36;
			alterZ = 77;
			toBank = new int[] { 253, 509, 248, 514, 242, 505, 239, 494, 234, 484, 227, 477, 221, 472, 212, 462, 216,
					450 };
			toSpot = new int[] { 212, 462, 221, 472, 227, 477, 236, 484, 239, 494, 242, 505, 248, 514, 253, 509, 260,
					506 };
			bankNW = new int[] { 220, 448 };
			bankSE = new int[] { 212, 453 };
			spotNW = new int[] { 263, 500 };
			spotSE = new int[] { 257, 509 };
			alterNW = new int[] { 895, 64 };
			alterSE = new int[] { 882, 77 };
			method = "Body rune crafting";
			controller.displayMessage("@cya@" + "We're crafting bodies");
		}
		if (i == 6) {
			mineEss = true;
			portalId = 1226;
			runeId = essId;
			toBank = new int[] { 107, 522, 107, 514, 102, 511 };
			toSpot = new int[] { 107, 514, 107, 522, 102, 525 };
			bankNW = new int[] { 106, 510 };
			bankSE = new int[] { 98, 515 };
			spotNW = new int[] { 104, 522 };
			spotSE = new int[] { 100, 525 };
			mineNW = new int[] { 705, 5 };
			mineSE = new int[] { 685, 27 };
			method = "Mining rune essence";
			controller.displayMessage("@cya@" + "We're mining essence");
		}
	}

	public void setupGUI() {
		JLabel headerLabel = new JLabel("If rcing start near the alter/bank with your tali please.");
		JLabel headerLabel2 = new JLabel("If mining start at the bank or in the mine please.");
		JComboBox<String> guiField = new JComboBox<String>();
		JCheckBox debugCheckbox = new JCheckBox("Debug", false);
		JButton startScriptButton = new JButton("Start");

		for (guiObject obj : objects) {
			guiField.addItem(obj.name);
		}

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.displayMessage("@cya@" + "Ty for using DamScripts <3" + " - Damrau");
				setValuesFromGUI(guiField.getSelectedIndex());
				debug = debugCheckbox.isSelected();
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				started = true;
			}
		});

		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(headerLabel);
		scriptFrame.add(headerLabel2);
		scriptFrame.add(guiField);
		scriptFrame.add(debugCheckbox);
		scriptFrame.add(startScriptButton);

		centerWindow(scriptFrame);
		scriptFrame.setVisible(true);
		scriptFrame.pack();
	}
	 @Override
    public void paintInterrupt() {
        if(controller != null) {
            controller.drawString("@cya@DamRc - By Damrau", 10, 21, 0xFFFFFF, 1);
            controller.drawString("@cya@Script Status: " + status, 10, 21+14, 0xFFFFFF, 1);
            controller.drawString("@cya@Method: " + method, 10, 21+28, 0xFFFFFF, 1);
        }
    }
}