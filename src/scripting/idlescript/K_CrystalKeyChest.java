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
 * Buys attack capes from rovin and banks
 *
 *
 *
 *
 * Author - Kaila
 */
public class K_CrystalKeyChest extends IdleScript {
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int totalDragonstones = 0;
    int totalTrips = 0;
    int KeysInBank = 0;
    int DragonstonesInBank = 0;

	int loot[] = {542,408,526,527};
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;


		public int start(String parameters[]) {
			if (!guiSetup) {
				setupGUI();
				guiSetup = true;
			}
			if (scriptStarted) {
				controller.displayMessage("@gre@Crystal Key Chest Opener - By Kaila");
				controller.displayMessage("@gre@Start by Crystal chest or in Catherby Bank!");
				if(controller.isInBank() == true) {
					controller.closeBank();
				}
				if(controller.currentX() > 400) {
					bank();
					BankToGrape();
					controller.sleep(1380);
				}
				scriptStart();
			}
			return 1000; //start() must return a int value now.
		}


		public void scriptStart() {
			while(controller.isRunning()) {

				for(int lootId : loot) {
					int[] coords = controller.getNearestItemById(lootId);
					if(coords != null) {
						controller.setStatus("@yel@Looting..");
						controller.pickupItem(coords[0], coords[1], lootId, true, true);
						controller.sleep(618);
					}
				}
				int[] coords = controller.getNearestItemById(542); //always pick up keys
				if(coords != null) {
					controller.setStatus("@yel@Looting Key..");
					controller.dropItem(controller.getInventoryItemSlotIndex(179));
					controller.pickupItem(coords[0], coords[1], 542, true, true);
					controller.sleep(618);
				}
				if(controller.getInventoryItemCount() == 30) {
					if(controller.getInventoryItemCount(35) > 0) {
						controller.dropItem(controller.getInventoryItemSlotIndex(35));
						controller.sleep(400);
					}
					if(controller.getInventoryItemCount(36) > 0) {
						controller.dropItem(controller.getInventoryItemSlotIndex(36));
						controller.sleep(400);
					}
					if(controller.getInventoryItemCount(33) > 0) {
						controller.dropItem(controller.getInventoryItemSlotIndex(33));
						controller.sleep(400);
					}
					if(controller.getInventoryItemCount(34) > 0) {
						controller.dropItem(controller.getInventoryItemSlotIndex(34));
						controller.sleep(400);
					}
					if(controller.getInventoryItemCount(32) > 0) {
						controller.dropItem(controller.getInventoryItemSlotIndex(32));
						controller.sleep(400);
					}
					if(controller.getInventoryItemCount(31) > 0) {
						controller.dropItem(controller.getInventoryItemSlotIndex(31));
						controller.sleep(400);
					}
					if(controller.getInventoryItemCount() == 30) {
						controller.setStatus("@gre@Banking..");
						GrapeToBank();
						bank();
						BankToGrape();
						controller.sleep(618);
					}
				}
				if(controller.getInventoryItemCount(526) > 0 && controller.getInventoryItemCount(527) > 0) {
					controller.setStatus("@gre@Combining Keys..");
					controller.useItemOnItemBySlot(controller.getInventoryItemSlotIndex(526), controller.getInventoryItemSlotIndex(527));
					controller.sleep(1000);
				}
				if(controller.getInventoryItemCount(525) == 0) {
					controller.setStatus("@gre@No Keys, Banking..");
					GrapeToBank();
					bank();
					BankToGrape();
					controller.sleep(618);
				}
				if(controller.getInventoryItemCount() < 30 && controller.getInventoryItemCount(525) > 0 ) {
					controller.setStatus("@gre@Using Key on Chest..");
					controller.useItemIdOnObject(367,497, 525);
					controller.sleep(2000);
				}

			}
		}


	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(640);

		if(controller.isInBank()){

			totalDragonstones = totalDragonstones + controller.getInventoryItemCount(542);

			for (int itemId : controller.getInventoryItemIds()) {
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
			}
			controller.sleep(1400);   // increased sleep here to prevent double banking

			if(controller.getInventoryItemCount(525) < 14) {  //crystal keys
				controller.withdrawItem(525, 14 - controller.getInventoryItemCount(525));
				controller.sleep(1000);
			}

			KeysInBank = controller.getBankItemCount(525);
			DragonstonesInBank = controller.getBankItemCount(542);

			controller.closeBank();
			controller.sleep(640);
		}
	}

	public void GrapeToBank() {  //replace

    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(371,495);
		controller.walkTo(375,498);
		controller.walkTo(385,498);
		controller.walkTo(389,502);
		controller.walkTo(394,502);
		controller.sleep(340);
		controller.atObject(395,502); //agility shortcut
		controller.sleep(4000);
		controller.walkTo(397,501);
		controller.walkTo(406,501);
		controller.walkTo(409,497);
		controller.walkTo(419,497);
		controller.walkTo(429,497);
		controller.walkTo(439,497);
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking..");

	}

    public void BankToGrape() {

    	controller.setStatus("@gre@Walking to Crystal Chest..");
		controller.walkTo(439,497);
		controller.walkTo(429,497);
		controller.walkTo(419,497);
		controller.walkTo(409,497);
		controller.walkTo(406,501);
		controller.walkTo(397,501);
		controller.sleep(340);
		controller.atObject(397,502); //agility shortcut
		controller.sleep(4000);
		controller.walkTo(389,502);
		controller.walkTo(385,498);
		controller.walkTo(375,498);
		controller.walkTo(371,495);
		controller.walkTo(367,496);
    	controller.setStatus("@gre@Done Walking..");
	}


	//GUI stuff below (icky)



	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	public void setupGUI() {
		JLabel header = new JLabel("Crystal Key Chest Opener - By Kaila");
		JLabel label1 = new JLabel("Start by Crystal chest or in Catherby Bank!");
		JLabel label2 = new JLabel("Only works on Coleslaw for the time Being");
		JLabel label3 = new JLabel("Utilizes the White Wolf Mountain Agility Shortcut");
		JButton startScriptButton = new JButton("Start Script");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
		scriptFrame.add(label3);
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
	    	int DragonstoneSuccessPerHr = 0;
	    	int TripSuccessPerHr = 0;

	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
				DragonstoneSuccessPerHr = (int)(totalDragonstones * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);

	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Crystal Key Chest Opener @gre@by Kaila", 310, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Dragonstones in Bank: @gre@" + String.valueOf(this.DragonstonesInBank), 320, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Crystal Keys in Bank: @gre@" + String.valueOf(this.KeysInBank), 320, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Dragonstones: @gre@" + String.valueOf(this.totalDragonstones) + "@yel@ (@whi@" + String.format("%,d", DragonstoneSuccessPerHr) + "@yel@/@whi@hr@yel@)", 320, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 320, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 320, 118, 0xFFFFFF, 1);
		}
	}
}
