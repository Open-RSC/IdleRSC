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
 * Grabs red spider eggs in edge dungeon, recommend very high stats ~90+
 * 
 * 
 * 
 * 
 * Author - Kaila
 */
public class K_SkelliCoal extends IdleScript {	
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int coalInBank = 0;
	int totalCoal = 0;
	int totalSap = 0;
	int totalEme = 0;
	int totalRub = 0;
	int totalDia = 0;
    int totalTrips = 0;

	Integer currentOre[] = {0,0};
	int coalIDs[] = {110,111};
	int oreIDs[] = {155};
	int gemIDs[] = {157,158,159,160}; 
	String isMining = "none";
	
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
		
		public int start(String parameters[]) {
			if (!guiSetup) {
				setupGUI();
				guiSetup = true;
			}
			if (scriptStarted) {
				controller.displayMessage("@red@Skeleton Coal Miner- By Kaila");
				controller.displayMessage("@red@Start in Edge bank with Armor and pickaxe");
				controller.displayMessage("@red@Enable eat(); if low level and at 546 to bank withdraw");
				if(controller.isInBank() == true) {
					controller.closeBank();
				}
				if(controller.currentY() > 400) {
					bank();
					eat();
					bankToSkeli();
					eat();
					controller.sleep(1380);
				}
				scriptStart();
			}
			return 1000; //start() must return a int value now. 
		}
		
		
		public void scriptStart() {
			while(controller.isRunning()) {
				if (controller.getInventoryItemCount() == 30) {    //controller.getInventoryItemCount(546) == 0 || 
					
					goToBank();
					
				} else {	
				
			//	eat();
				
				while(controller.isInCombat()) {
					controller.setStatus("@red@Leaving combat..");
					controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
					controller.sleep(250);
				}
					
				if (rockEmpty() || !controller.isBatching()) {
					isMining = "none";
					currentOre[0] = 0;
					currentOre[1] = 0;
				}
				
				controller.setStatus("@yel@Mining..");
				
				if (!controller.isBatching() && isMining == "none" && rockEmpty()) {
					if (coalAvailable()) {
						mine("coal");
					}
					controller.sleep(1280);
				}
			}


		}
	}

		
		public void mine(String i) {
		 if (i == "coal") {
				int oreCoords[] = controller.getNearestObjectByIds(coalIDs);
				if (oreCoords != null) {
					isMining = "coal";	
					controller.atObject(oreCoords[0], oreCoords[1]);
					currentOre[0] = oreCoords[0];
					currentOre[1] = oreCoords[1];
				}
			}
			controller.sleep(1920);
		}
		public boolean coalAvailable() {
		    return controller.getNearestObjectByIds(coalIDs) != null;
		}
		public boolean rockEmpty() {
			if (currentOre[0] != 0) {
				return controller.getObjectAtCoord(currentOre[0], currentOre[1]) == 98;
			} else {
				return true;
			}
		}
		
	
	public void bank() {
		
		controller.setStatus("@yel@Banking..");
		controller.openBank();
		
		while(controller.isInBank()){
			
			totalCoal = totalCoal + controller.getInventoryItemCount(155);
			totalSap = totalSap + controller.getInventoryItemCount(160);
			totalEme = totalEme + controller.getInventoryItemCount(159);
			totalRub = totalRub + controller.getInventoryItemCount(158);
			totalDia = totalDia + controller.getInventoryItemCount(157);
			
			if (controller.getInventoryItemCount() > 0) {
				for (int itemId : controller.getInventoryItemIds()) {
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				}
				controller.sleep(1280);   // increased sleep here to prevent double banking
			}
			if(controller.getInventoryItemCount(546) < 1) {  //withdraw 20 shark
				controller.withdrawItem(546, 1);
				controller.sleep(340);
			}
			coalInBank = controller.getBankItemCount(155);
			
			controller.closeBank();
			controller.sleep(640);
		}
	}
	
	public void eat() {
		
		int eatLvl = controller.getBaseStat(controller.getStatId("Hits")) - 20;
		
		if(controller.getCurrentStat(controller.getStatId("Hits")) < eatLvl) {
			
			while(controller.isInCombat()) {
				controller.setStatus("@red@Leaving combat..");
				controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
				controller.sleep(250);
			}
			
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
			controller.setStatus("@red@We've ran out of Food! Teleporting Away!.");
			SkeliToBank();
			controller.sleep(100);

			controller.walkTo(120,644);
			controller.atObject(119,642);
			controller.walkTo(217,447);
			controller.sleep(308);
			controller.setAutoLogin(false);
			controller.logout();
			controller.sleep(1000);
		
			if(!controller.isLoggedIn()) {
				controller.stop();
				controller.logout();
				return;
			}
		}
	}
}
	
	public void goToBank() {
		isMining = "none";
		currentOre[0] = 0;
		currentOre[1] = 0;	
		controller.setStatus("@yel@Banking..");
		SkeliToBank();
		bank();
		bankToSkeli();
		controller.sleep(618);
	}
	
	public void SkeliToBank() {
    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(269,380);
		controller.walkTo(265,384);
		controller.walkTo(259,385);
		controller.walkTo(249,395);
		controller.walkTo(247,399);
		controller.walkTo(234,412);
		controller.walkTo(224,423);
		controller.walkTo(220,427);
		controller.walkTo(220,441);
		controller.walkTo(220,445);
		controller.walkTo(217,448);
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking to Bank...");
	}
	
    public void bankToSkeli() {	
    	controller.setStatus("@gre@Walking to Skelli "
    			+ "Mine..");
		controller.walkTo(217,448);
		controller.walkTo(220,445);
		controller.walkTo(220,441);
		controller.walkTo(220,427);
		controller.walkTo(224,423);
		controller.walkTo(234,412);
		controller.walkTo(247,399);
		controller.walkTo(249,395);
		controller.walkTo(259,385);
		controller.walkTo(265,384);
		controller.walkTo(269,380);
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
		JLabel header = new JLabel("Skeleton Coal Miner - By Kaila");
		JLabel label1 = new JLabel("Start in Edge bank with Armor and Pickaxe");
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
	    	int coalSuccessPerHr = 0;
	    	int sapSuccessPerHr = 0;
	    	int emeSuccessPerHr = 0;
	    	int rubSuccessPerHr = 0;
	    	int diaSuccessPerHr = 0;
    		int TripSuccessPerHr = 0;
    		
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		coalSuccessPerHr = (int)(totalCoal * scale);
	    		sapSuccessPerHr = (int)(totalSap * scale);
	    		emeSuccessPerHr = (int)(totalEme * scale);
	    		rubSuccessPerHr = (int)(totalRub * scale);
	    		diaSuccessPerHr = (int)(totalDia * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    		
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Skeli Miner @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Coal Mined: @gre@" + String.valueOf(this.totalCoal) + "@yel@ (@whi@" + String.format("%,d", coalSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Sapphires: @gre@" + String.valueOf(this.totalSap) + "@yel@ (@whi@" + String.format("%,d", sapSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Emeralds: @gre@" + String.valueOf(this.totalEme) + "@yel@ (@whi@" + String.format("%,d", emeSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Rubys: @gre@" + String.valueOf(this.totalRub) + "@yel@ (@whi@" + String.format("%,d", rubSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Diamonds: @gre@" + String.valueOf(this.totalDia) + "@yel@ (@whi@" + String.format("%,d", diaSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 118, 0xFFFFFF, 1);
			controller.drawString("@whi@Coal in Bank: @gre@" + String.valueOf(this.coalInBank), 370, 132, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 146, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 370, 160, 0xFFFFFF, 1);
		}
	}
}
