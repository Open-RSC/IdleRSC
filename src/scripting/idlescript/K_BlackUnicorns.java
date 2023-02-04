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
 * Black Unicorn Killer - By Kaila
 * Start in Edge bank with Armor
 * Sharks/Laws/Airs/Earths IN BANK REQUIRED
 * 31 Magic Required for escape tele
 * 
 * Author - Kaila
 */
public class K_BlackUnicorns extends IdleScript {	
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int uniInBank = 0;
	int totalUni = 0;
    int totalTrips = 0;
    
	int[] loot = { 466, 381 };
	
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (scriptStarted) {
			controller.displayMessage("@red@Black Unicorn Killer - By Kaila");
			controller.displayMessage("@red@Start in Edge bank with Armor");
			controller.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
			controller.displayMessage("@red@31 Magic Required for escape tele");
//			bank();
			if(controller.isInBank() == true) {
				controller.closeBank();
			}
			if(controller.currentY() > 340) {
				bank();
				eat();
				BankToUni();
				controller.sleep(1380);
			}
			scriptStart();
		}
		return 1000; //start() must return a int value now. 
	}

	public void scriptStart() {
		while(controller.isRunning()) {
						
				eat();
							
				if(controller.getInventoryItemCount() < 30) {
					
			   		boolean lootPickedUp = false;
		    		for(int lootId : loot) {
		    			int[] coords = controller.getNearestItemById(lootId);
		    			if(coords != null) {
							controller.setStatus("@yel@Looting..");
							controller.walkTo(coords[0], coords[1]);
		    				controller.pickupItem(coords[0], coords[1], lootId, true, true);
		    				controller.sleep(618);
		    			}
		    		}
		    		if(lootPickedUp) //we don't want to start to pickup loot then immediately attack a npc
		    			continue;
		    		
		    		if(!controller.isInCombat()) {
    					controller.setStatus("@yel@Attacking..");
		    			controller.sleepHandler(296, true);
			    		ORSCharacter npc = controller.getNearestNpcById(296, false);
			    		if(npc != null) {

			    			//controller.walktoNPC(npc.serverIndex,1);
			    			controller.attackNpc(npc.serverIndex);
			    			controller.sleep(1000);
			    		}
		    		}
	    			controller.sleep(1380);
	    			

			
		} else if(controller.getInventoryItemCount() > 29) {
				controller.setStatus("@yel@Banking..");
				UniToBank();
				bank();
				BankToUni();
				controller.sleep(618);
								
			}
		}
		
	}
	

	public void bank() {
		
		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(1380);
		
		if(controller.isInBank()){
			
			totalUni = totalUni + controller.getInventoryItemCount(466);
			
			if(controller.getInventoryItemCount(466) >  0) {  //deposit the uni horns
				controller.depositItem(466, controller.getInventoryItemCount(466));
				controller.sleep(340);
			}
			
			uniInBank = controller.getBankItemCount(466);
			
			if(controller.getInventoryItemCount(33) < 3) {  //withdraw 3 air
				controller.withdrawItem(33, 3);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(34) < 1) {  //withdraw 1 earth
				controller.withdrawItem(34, 1);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(42) < 1) {  //withdraw 1 law
				controller.withdrawItem(42, 1);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(546) > 1) {  //deposit extra shark
				controller.depositItem(546, controller.getInventoryItemCount(546) - 1);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(546) < 1) {  //withdraw 1 shark
				controller.withdrawItem(546, 1);
				controller.sleep(340);
			}
			if(controller.getBankItemCount(546) == 0) {
				controller.setStatus("@red@NO Sharks/Laws/Airs/Earths in the bank, Logging Out!.");
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
			if(!ate) { //only activates if hp goes to -20 again THAT trip, will bank and get new shark usually
				controller.setStatus("@red@We've ran out of Food! Teleporting Away!.");
    				while(controller.currentY() < 314) {
	    				controller.setStatus("@red@Going to 19 Wildy.");
    					controller.walkTo(119, 314);
    				}
    				controller.setStatus("@red@Teleporting Now!.");
    		    	while(controller.currentY() < 427) {
    		    		controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport"));
    		    		controller.sleep(308);
    				}
					controller.walkTo(120,644);
					controller.atObject(119,642);
					controller.walkTo(217,447);
					controller.setAutoLogin(false);
					controller.logout();
					controller.sleep(1000);
				
					if(!controller.isLoggedIn()) {
						controller.stop();
						return;
					}
	    		} else if(!ate) {
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
	
	public void UniToBank() {
    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(121,311);
		controller.walkTo(131,321);
		controller.walkTo(135,326);
		controller.walkTo(145,336);
		controller.walkTo(146,340);
		controller.walkTo(158,352);
		controller.walkTo(175,369);
		controller.walkTo(183,372);
		controller.walkTo(199,388);
		controller.walkTo(205,393);
		controller.walkTo(216,405);
		controller.walkTo(216,426);
		controller.walkTo(220,440);
		controller.walkTo(218,447);
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking..");
		controller.sleep(640);

	}
	
    public void BankToUni() {
    	controller.setStatus("@gre@Walking to Unicorns..");
		controller.walkTo(220,440);
		controller.walkTo(216,426);
		controller.walkTo(216,405);
		controller.walkTo(205,393);
		controller.walkTo(199,388);
		controller.walkTo(183,372);
		controller.walkTo(175,369);
		controller.walkTo(158,352);
		controller.walkTo(146,340);
		controller.walkTo(145,336);
		controller.walkTo(135,326);
		controller.walkTo(131,321);
		controller.walkTo(121,311);
    	controller.setStatus("@gre@Done Walking..");
		controller.sleep(640);
	}
	
	//GUI stuff below (icky)
	
	
	
	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	public void setupGUI() {
		JLabel header = new JLabel("Black Unicorn Killer - By Kaila");
		JLabel label1 = new JLabel("Start in Edge bank or Uni's with Gear");
		JLabel label2 = new JLabel("Sharks/Laws/Airs/Earths IN BANK REQUIRED");
		JLabel label3 = new JLabel("31 Magic Required for Escape Tele");
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
	    		successPerHr = (int)(totalUni * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    		
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Black Unicorns @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Horns in Bank: @gre@" + String.valueOf(this.uniInBank), 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Horns Picked: @gre@" + String.valueOf(this.totalUni) + "@yel@ (@whi@" + String.format("%,d", successPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 104, 0xFFFFFF, 1);
		}
	}
}

