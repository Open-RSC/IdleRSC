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
 * Grabs Grapes from edge monestary
 *
 *
 *
 *
 * Author - Kaila
 */
public class K_Nightshade extends IdleScript {
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int GrapezInBank = 0;
	int totalShade = 0;
    int totalTrips = 0;
    int shadeInBank = 0;


	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;


		public int start(String parameters[]) {
			if (!guiSetup) {
				setupGUI();
				guiSetup = true;
			}
			if (scriptStarted) {
				controller.displayMessage("@red@Nightshade Picker - By Kaila");
				controller.displayMessage("@red@Start in Yanille Bank");
				if(controller.isInBank() == true) {
					controller.closeBank();
				}
				if(controller.currentY() < 800 && controller.currentY() > 740 && controller.currentX() < 591) {
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

				if(controller.getInventoryItemCount() == 30) {
					controller.setStatus("@red@Banking..");
					GrapeToBank();
					bank();
					BankToGrape();
					controller.sleep(618);
				}


			   		int[] coords = controller.getNearestItemById(1086);  //always pick up tops
	        		if(coords != null) {
						controller.setStatus("@yel@Looting..");
						controller.walkTo(650,3559);
			   			controller.sleep(340);
						controller.walkTo(650,3560);
			   			controller.pickupItem(coords[0], coords[1], 1086, true, true);
			   			controller.sleep(340);
			   		} else {
		        		controller.sleep(4000);
			   		}
	        		controller.sleep(100);
	        		}
			}



	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(640);

		if(controller.isInBank()){

			totalShade = totalShade + controller.getInventoryItemCount(1086);

			if(controller.getInventoryItemCount(1086) >  0) {  //nightshade
				controller.depositItem(1086,controller.getInventoryItemCount(1086));
				controller.sleep(1380);
			}
			if(controller.getInventoryItemCount(601) < 1) {  //lit candle
				controller.withdrawItem(601,1);
				controller.sleep(1380);
			}
			if(controller.getInventoryItemCount(1045) < 1) {  //skavid map
				controller.withdrawItem(1045,1);
				controller.sleep(1380);
			}
			shadeInBank = controller.getBankItemCount(1086);

			controller.closeBank();
			controller.sleep(640);
		}
	}

	public void GrapeToBank() {  //replace

    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(649,3555);
		while(controller.currentX() == 649 && controller.currentY() == 3555) {
			controller.atWallObject(649,3554);   //gate wont break if someone else opens it
			controller.sleep(640);
		}
		//controller.atObject(649,3554);
		controller.sleep(340);
		controller.walkTo(647,767);
		controller.walkTo(647,754);
		controller.walkTo(642,754);
		controller.walkTo(628,754);
		controller.walkTo(626,755);
		controller.walkTo(615,755);
		controller.walkTo(608,749);
		controller.walkTo(598,749);
		controller.walkTo(584,749);
		controller.walkTo(584,752);
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking..");

	}

    public void BankToGrape() {

    	controller.setStatus("@gre@Walking to Nightshade..");
		controller.walkTo(584,752);
		controller.walkTo(584,749);
		controller.walkTo(598,749);
		controller.walkTo(608,749);
		controller.walkTo(615,755);
		controller.walkTo(626,755);
		controller.walkTo(628,754);
		controller.walkTo(642,754);
		//west yanille GATE
		controller.walkTo(647,754);
		controller.walkTo(647,767);
		controller.walkTo(649,769);
		controller.sleep(340);
		controller.atObject(649,770);
		controller.walkTo(650,3560);
		controller.sleep(340);
    	//ontop of nightshade now
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
		JLabel header = new JLabel("Nightshade Picker - By Kaila");
		JLabel label1 = new JLabel("Picks Nightshade in northern skavid cave");
		JLabel label2 = new JLabel("Start in Yanille bank or northern nightshade cave");
		JLabel label3 = new JLabel("Requires Lit Candle and Skavid Map in Invent");
		JButton startScriptButton = new JButton("Start");

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
	    	int ShadeSuccessPerHr = 0;
	    	int TripSuccessPerHr = 0;

	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		ShadeSuccessPerHr = (int)(totalShade * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);

	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Nightshade Picker @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Nightshade Banked: @gre@" + String.valueOf(this.shadeInBank), 330, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Nightshade Picked: @gre@" + String.valueOf(this.totalShade) + "@yel@ (@whi@" + String.format("%,d", ShadeSuccessPerHr) + "@yel@/@whi@hr@yel@)", 330, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 330, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 330, 104, 0xFFFFFF, 1);
		}
	}
}
