package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import orsc.ORSCharacter;

/**
 * Grabs red spider eggs in edge dungeon, recommend very high stats ~90+
 *
 *
 *
 *
 * Author - Kaila
 */
public class K_RedSpiderEggz extends IdleScript {
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	boolean teleportOut = false;
	boolean returnEscape = true;
	int eggzInBank = 0;
	int totalEggz = 0;
    int totalTrips = 0;

	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;


		public int start(String parameters[]) {
			if (!guiSetup) {
				setupGUI();
				guiSetup = true;
			}
			if (scriptStarted) {
				controller.displayMessage("@red@Red Spider Egg Picker - By Kaila");
				controller.displayMessage("@red@Start in Edge bank with Armor");
				controller.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
				controller.displayMessage("@red@31 Magic Required for escape tele");
				if(controller.isInBank() == true) {
					controller.closeBank();
				}
				if(controller.currentY() > 340 && controller.currentY() < 500 ) { //fixed start area bug
					bank();
					eat();
					BankToEgg();
					controller.sleep(100);
				}
				scriptStart();
			}
			return 1000; //start() must return a int value now.
		}


		public void scriptStart() {
			while(controller.isRunning()) {

				eat();
				leaveCombat();
				controller.setStatus("@yel@Picking Eggs..");

				if(controller.getInventoryItemCount() > 29 || controller.getInventoryItemCount(546) == 0) {
					controller.setStatus("@red@Banking..");
					EggToBank();
					bank();
					BankToEgg();
					controller.sleep(618);
				}
				if(controller.getNearestItemById(219) != null) {
					int[] coords = controller.getNearestItemById(219);
					controller.pickupItem(coords[0], coords[1], 219, true, true);
					controller.sleep(1000);
				} else {  //fixed cpu overrun issue
					controller.sleep(1000); //fixed cpu overrun issue
				}
			}
		}


	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(1200);

		if (controller.isInBank()) {

			totalEggz = totalEggz + controller.getInventoryItemCount(219);

			for (int itemId : controller.getInventoryItemIds()) {
				if (itemId != 546) {
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				}
			}
			controller.sleep(1280);
			eggzInBank = controller.getBankItemCount(219);

			if(controller.getInventoryItemCount(546) > 1) {  //deposit extra shark
				controller.depositItem(546, controller.getInventoryItemCount(546) - 1);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(546) < 1) {  //withdraw 1 shark
				controller.withdrawItem(546, 1);
				controller.sleep(340);
			}
			if (teleportOut == true) {
				if (controller.getInventoryItemCount(33) < 3) {  //withdraw 3 air
					controller.withdrawItem(33, 3);
					controller.sleep(640);
				}
				if (controller.getInventoryItemCount(34) < 1) {  //withdraw 1 earth
					controller.withdrawItem(34, 1);
					controller.sleep(640);
				}
				if (controller.getInventoryItemCount(42) < 1) {  //withdraw 1 law
					controller.withdrawItem(42, 1);
					controller.sleep(640);
				}
			}
			if(controller.getBankItemCount(546) == 0) {
				controller.setStatus("@red@NO Sharks in the bank, Logging Out!.");
				controller.setAutoLogin(false);
				controller.logout();
				if(!controller.isLoggedIn()) {
					controller.stop();
					return;
				}
			}
			controller.closeBank();
			controller.sleep(640);
		}
	}

	public void eat() {

		int eatLvl = controller.getBaseStat(controller.getStatId("Hits")) - 20;


		if(controller.getCurrentStat(controller.getStatId("Hits")) < eatLvl) {

			leaveCombat();
			controller.setStatus("@red@Eating..");

			boolean ate = false;

			for(int id : controller.getFoodIds()) {
				if(controller.getInventoryItemCount(id) > 0) {
					controller.itemCommand(id);
					controller.sleep(700);
					ate = true;
					break;
				}
			}
			if(!ate) {  //only activates if hp goes to -20 again THAT trip, will bank and get new shark usually
				controller.setStatus("@red@We've ran out of Food! Running Away!.");
				if (teleportOut == false
						|| controller.getInventoryItemCount(42) < 1
						|| controller.getInventoryItemCount(33) < 3
						|| controller.getInventoryItemCount(34) < 1) { //or no earths/airs/laws
					EggToBank();
					bank();
				}
				if (teleportOut == true) {
					controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport(1)"));
					controller.sleep(800);
					if (controller.currentY() > 3000) {
						controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport(2)"));
						controller.sleep(800);
					}
					if (controller.currentY() > 3000) {
						controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport(3)"));
						controller.sleep(800);
					}
					controller.walkTo(120, 644);
					controller.atObject(119, 642);
					controller.walkTo(217, 447);
				}
				if (returnEscape == false) {
					controller.setAutoLogin(false);
					controller.logout();
					controller.sleep(1000);

					if (!controller.isLoggedIn()) {
						controller.stop();
						controller.logout();
						return;
					}
				}
				if (returnEscape == true) {
					bank();
					BankToEgg();
					controller.sleep(618);
				}
			}
		}
	}

	public void EggToBank() {
    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(197,3244);
		controller.walkTo(197,3255);
		controller.walkTo(196,3265);
		controller.setStatus("@gre@Opening Wildy Gate North to South(1)..");
		controller.atObject(196,3266);
		controller.sleep(640);
		openGateNorthToSouth();
		controller.walkTo(197,3266);
		controller.walkTo(204,3272);
		controller.walkTo(210,3273);
		if(controller.getObjectAtCoord(211,3272) == 57) {
			controller.setStatus("@gre@Opening Edge Gate..");
			controller.walkTo(210,3273);
			controller.atObject(211,3272);
			controller.sleep(340);
		}
		controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(217,3283);
		controller.walkTo(215,3294);
		controller.walkTo(215,3299);
		controller.atObject(215,3300);
		controller.sleep(640);
		controller.walkTo(217,458);
		controller.walkTo(221,447);
		controller.walkTo(217,448);
		controller.sleep(640);
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking..");
	}

    public void BankToEgg() {
    	controller.setStatus("@gre@Walking to Eggs..");
		controller.walkTo(221,447);
		controller.walkTo(217,458);
		controller.walkTo(215,467);
		controller.atObject(215,468);
		controller.sleep(640);
		controller.walkTo(217,3283);
		controller.walkTo(211,3273);
		if(controller.getObjectAtCoord(211,3272) == 57) {
			controller.setStatus("@gre@Opening Edge Gate..");
			controller.walkTo(211,3273);
			controller.atObject(211,3272);
			controller.sleep(340);
		}
		controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(204,3272);
		controller.walkTo(199,3272);
		controller.walkTo(197,3266);
		controller.setStatus("@gre@Opening Wildy Gate, South to North(1)..");
		controller.atObject(196,3266);
		controller.sleep(640);
		openGateSouthToNorth();
		controller.walkTo(197,3244);
		controller.walkTo(208,3240);
    	controller.setStatus("@gre@Done Walking..");
	}
	public void leaveCombat() {
		for (int i = 1; i <= 15; i++) {
			if (controller.isInCombat()) {
				controller.setStatus("@red@Leaving combat (n)..");
				controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
				controller.sleep(600);
			} else {
				controller.setStatus("@red@Done Leaving combat..");
				break;
			}
			controller.sleep(10);
		}
	}
	public void openGateNorthToSouth() {
		for (int i = 1; i <= 25; i++) {
			if(controller.currentY() == 3265) {
				controller.setStatus("@gre@Opening Wildy Gate..");
				controller.atObject(196,3266);
				controller.sleep(640);
			} else {
				controller.setStatus("@red@Done Opening Wildy Gate..");
				break;
			}
			controller.sleep(10);
		}
	}
	public void openGateSouthToNorth() {
		for (int i = 1; i <= 25; i++) {
			if(controller.currentY() == 3266) {
				controller.setStatus("@gre@Opening Wildy Gate..");
				controller.atObject(196,3266);
				controller.sleep(640);
			} else {
				controller.setStatus("@red@Done Opening Wildy Gate..");
				break;
			}
			controller.sleep(10);
		}
	}























	//GUI stuff below (icky)


	public void setValuesFromGUI(JCheckBox potUpCheckbox, JCheckBox escapeCheckbox) {
		if (potUpCheckbox.isSelected()) {
			teleportOut = true;
		} else {
			teleportOut = false;
		}
		if (escapeCheckbox.isSelected()) {
			returnEscape = true;
		} else {
			returnEscape = false;
		}
	}
	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	public void setupGUI() {
		JLabel header = new JLabel("Red Spider Egg Picker - By Kaila");
		JLabel label1 = new JLabel("Start in Edge bank with Armor");
		JLabel label2 = new JLabel("Sharks in bank REQUIRED");
		JCheckBox teleportCheckbox = new JCheckBox("Teleport if Pkers Attack?", false);
		JLabel label3 = new JLabel("31 Magic, Laws, Airs, and Earths required for Escape Tele");
		JLabel label4 = new JLabel("Unselected, bot WALKS to Edge when Attacked");
		JLabel label5 = new JLabel("Selected, bot teleports, then walks to edge");
		JCheckBox escapeCheckbox = new JCheckBox("Return to Eggz after Escaping?", true);
		JLabel label6 = new JLabel("Unselected, bot will log out after escaping Pkers");
		JLabel label7 = new JLabel("Selected, bot will grab more food and return");
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setValuesFromGUI(teleportCheckbox, escapeCheckbox);
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				startTime = System.currentTimeMillis();
				scriptStarted = true;
			}
		});

		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(label1);
		scriptFrame.add(label2);
		scriptFrame.add(teleportCheckbox);
		scriptFrame.add(label3);
		scriptFrame.add(label4);
		scriptFrame.add(label5);
		scriptFrame.add(escapeCheckbox);
		scriptFrame.add(label6);
		scriptFrame.add(label7);
		scriptFrame.add(startScriptButton);
		centerWindow(scriptFrame);
		scriptFrame.setVisible(true);
		scriptFrame.pack();
		scriptFrame.requestFocus();

	}
	public static String msToString(long milliseconds) {
		long sec = milliseconds / 1000;
		long min = sec / 60;
		long hour = min / 60;
		sec %= 60;
		min %= 60;
		DecimalFormat twoDigits = new DecimalFormat("00");

		return new String(twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec));
	}
	@Override
	public void paintInterrupt() {
		if (controller != null) {

			String runTime = msToString(System.currentTimeMillis() - startTime);
	    	int successPerHr = 0;
	    	int TripSuccessPerHr = 0;

	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		successPerHr = (int)(totalEggz * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);

	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@RedSpiderEggz @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Eggs in Bank: @gre@" + String.valueOf(this.eggzInBank), 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Eggs Picked: @gre@" + String.valueOf(this.totalEggz) + "@yel@ (@whi@" + String.format("%,d", successPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 104, 0xFFFFFF, 1);
		}
	}
}
