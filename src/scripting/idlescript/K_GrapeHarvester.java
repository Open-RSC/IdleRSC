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
import scripting.idlescript.AIOCooker.FoodObject;

/**
 * Grabs Grapes from edge monestary
 * 
 * 
 * 
 * 
 * Author - Kaila
 */
public class K_GrapeHarvester extends IdleScript {	
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int GrapezInBank = 0;
	int totalGrapez = 0;
    int totalTrips = 0;
	
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
		
		public int start(String parameters[]) {
			if (!guiSetup) {
				setupGUI();
				guiSetup = true;
			}
			if (scriptStarted) {
				controller.displayMessage("@red@Grape Harvester - By Kaila");
				controller.displayMessage("@red@Start in Edge Bank or near Grapes");
				if(controller.isInBank() == true) {
					controller.closeBank();
				}
				if(controller.currentX() < 240) {
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
				
				controller.setStatus("@yel@Picking Grapes..");
				int[] coords = controller.getNearestObjectById(1283);
				if(coords != null) {
					controller.setStatus("@yel@Harvesting...");
					controller.atObject(coords[0], coords[1]);
					controller.sleep(1000);
					
					while(controller.isBatching() && controller.getInventoryItemCount() < 30) {
						controller.sleep(1000);
					}
				} else {
					controller.setStatus("@yel@Waiting for spawn..");
					controller.sleep(1000);
				}
				controller.sleep(100);
			}
		}
					
	
	public void bank() {
		
		controller.setStatus("@yel@Banking..");
		controller.openBank();
		
		while(controller.isInBank()){
			
			totalGrapez = totalGrapez + controller.getInventoryItemCount(143);
			
			if(controller.getInventoryItemCount(143) >  0) {  //deposit the Grapes
				controller.depositItem(143,controller.getInventoryItemCount(143));
				controller.sleep(1380);
			}
			if(controller.getInventoryItemCount(1357) < 1) {  //withdraw herb clippers
				if(controller.getBankItemCount(1357) > 0) {
					controller.withdrawItem(1357,1);
					controller.sleep(1380);
				} else {
					controller.displayMessage("@red@You need herb clippers!");
				}


			}

			
			GrapezInBank = controller.getBankItemCount(143);
			controller.closeBank();
			controller.sleep(640);
		}
	}
	
	public void GrapeToBank() {  //replace
		
    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(251,454);
		controller.walkTo(254,454);
		controller.walkTo(256,451);
		controller.walkTo(255,444);
		controller.walkTo(255,433);
		controller.walkTo(255,422);
		controller.walkTo(258,422);
		controller.walkTo(258,415);
		controller.walkTo(252,421);
		controller.walkTo(242,432);
		controller.walkTo(225,432);
		controller.walkTo(220,437);
		controller.walkTo(220,445);
		controller.walkTo(218,447);
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking..");
    	
	}
	
    public void BankToGrape() {
    	
    	controller.setStatus("@gre@Walking to Grapes..");
		controller.walkTo(218,447);
		controller.walkTo(220,445);
		controller.walkTo(220,437);
		controller.walkTo(225,432);
		controller.walkTo(242,432);
		controller.walkTo(252,421);
		controller.walkTo(258,415);
		controller.walkTo(258,422);
		controller.walkTo(255,422);
		controller.walkTo(255,433);
		controller.walkTo(255,444);
		controller.walkTo(256,451);
		controller.walkTo(254,454);
		controller.walkTo(251,454);
    	//next to Grape now)
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
		JLabel header = new JLabel("Grape Harvester - By Kaila");
		JLabel label1 = new JLabel("Harvests Grapes near Edge Monastery");
		JLabel label2 = new JLabel("*Start in Edge Bank with Herb Clippers");
		JLabel label3 = new JLabel("*Recommend Armor against lvl 21 Scorpions");
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
	    	int successPerHr = 0;
	    	int TripSuccessPerHr = 0;
	    	
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		successPerHr = (int)(totalGrapez * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    		
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Grape Harvester @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Grapes in Bank: @gre@" + String.valueOf(this.GrapezInBank), 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Grapes Picked: @gre@" + String.valueOf(this.totalGrapez) + "@yel@ (@whi@" + String.format("%,d", successPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 104, 0xFFFFFF, 1);
		}
	}
}
