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
 * Edge Druid Killer - By Kaila");
 * Start in Edge bank with Armor");
 * Sharks/Laws/Airs/Earths IN BANK REQUIRED");
 * 31 Magic Required for escape tele");
 * 
 * Author - Kaila
 */
public class K_EdgeChaosDruids extends IdleScript {	
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int totalGuam = 0;
	int totalMar = 0;
	int totalTar = 0;
	int totalHar = 0;
	int totalRan = 0;
	int totalIrit = 0;
	int totalAva = 0;
	int totalKwuarm = 0;
	int totalCada = 0;
	int totalDwarf = 0;
    int totalLaw = 0;
    int totalNat = 0;
    int totalLoop = 0;
    int totalTooth = 0;
    int totalLeft = 0;
    int totalSpear = 0;
    int totalTrips = 0;
    
	int[] loot = { 
			165,     //Grimy Guam
			435,     //Grimy mar
			436,     //Grimy tar
			437,     //Grimy har
			438, 	 //Grimy ranarr
			439,  	 //Grimy irit
			440,  	 //Grimy ava
			441,	 //Grimy kwu
			442, 	 //Grimy cada
			443, 	 //Grimy dwu
			40,	 	 // nature rune
			42, 	 // law rune
			33,		//air rune
			34, 	//Earth rune
			36,		//body runes
			1026,     //unholy mould
			160, 	 //saph
			159, 	 //emerald
			158, 	 //ruby
			157,	  	 //diamond
			526, 	 //tooth half
			527, 	 //loop half
			1277, 	 //shield (left) half
			1092 	 //rune spear
			};
	
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	
	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (scriptStarted) {
			controller.displayMessage("@red@Edge Druid Killer - By Kaila");
			controller.displayMessage("@red@Start in Edge bank with Armor");
			controller.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
			controller.displayMessage("@red@31 Magic Required for escape tele");
			if(controller.isInBank() == true) {
				controller.closeBank();
			}
			if(controller.currentY() < 3000) {
				bank();
				BankToDruid();  
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
		    			controller.setStatus("@yel@Attacking Druids");
					   	ORSCharacter npc = controller.getNearestNpcById(270, false);
					   	if(npc != null) {
					    	//controller.walktoNPC(npc.serverIndex,1);
					    	controller.attackNpc(npc.serverIndex);
					    	controller.sleep(1000);
					    } else {
							controller.sleep(1000);
							if (controller.currentX() != 218 || controller.currentY() != 3245){
								controller.walkTo(218,3245);
								controller.sleep(1000);
							}
						}
				    }
			    	controller.sleep(1380);
				} else if(controller.getInventoryItemCount() > 29 || controller.getInventoryItemCount(546) == 0) {
					controller.setStatus("@yel@Banking..");
					DruidToBank();
					bank();
					BankToDruid();
					controller.sleep(618);
				}
			}
	}
					
		

	


	
	
	public void bank() {
		
		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(1280);
		
		while(controller.isInBank()){
			
			totalGuam = totalGuam + controller.getInventoryItemCount(165);
			totalMar = totalMar + controller.getInventoryItemCount(435);
			totalTar = totalTar + controller.getInventoryItemCount(436);
			totalHar = totalHar + controller.getInventoryItemCount(437);
			totalRan = totalRan + controller.getInventoryItemCount(438);
			totalIrit = totalIrit + controller.getInventoryItemCount(439);
			totalAva = totalAva + controller.getInventoryItemCount(440);
			totalKwuarm = totalKwuarm + controller.getInventoryItemCount(441);
			totalCada = totalCada + controller.getInventoryItemCount(442);
			totalDwarf = totalDwarf + controller.getInventoryItemCount(443);
			totalLaw = totalLaw + controller.getInventoryItemCount(42);
			totalNat = totalNat + controller.getInventoryItemCount(40);
			totalLoop = totalLoop + controller.getInventoryItemCount(527);
			totalTooth = totalTooth + controller.getInventoryItemCount(526);
			totalLeft = totalLeft + controller.getInventoryItemCount(1277);
			totalSpear = totalSpear + controller.getInventoryItemCount(1092);
			
			if (controller.getInventoryItemCount() > 1) {
				for (int itemId : controller.getInventoryItemIds()) {
					if (itemId != 546) {
						controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
					}
				}
				controller.sleep(1280);   // increased sleep here to prevent double banking
			}
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
	    		controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport"));
	    		controller.sleep(308);
		    		if(controller.currentY() > 3000) {
		    			controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport"));
		    			controller.sleep(308);
		    		}
		    		controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport"));
		    		controller.sleep(308);
		    		controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport"));
		    		controller.sleep(308);
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
				}
			}
		}
	
	public void DruidToBank() {
    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(210,3254);
		controller.walkTo(200,3254);
		controller.walkTo(196,3265);
		while(controller.currentX() > 195 && controller.currentX() < 198 && controller.currentY() == 3265) {
			controller.atObject(196,3266);   //gate wont break if someone else opens it
			controller.sleep(640);
		}
		controller.walkTo(197,3266);
		controller.walkTo(204,3272);
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
	
    public void BankToDruid() {	
    	controller.setStatus("@gre@Walking to Druids..");
		controller.walkTo(221,447);
		controller.walkTo(217,458);
		controller.walkTo(215,467);
		controller.atObject(215,468);
		controller.sleep(640);
		controller.walkTo(217,3283);
		controller.walkTo(211,3273);
		if(controller.getObjectAtCoord(211,3272) == 57) {
			controller.setStatus("@gre@Opening Giants Gate..");
			controller.walkTo(211,3273);
			controller.atObject(211,3272);
			controller.sleep(340);
		}
		controller.walkTo(204,3272);
		controller.walkTo(197,3266);
		while(controller.currentX() > 195 && controller.currentX() < 198 && controller.currentY() == 3266) {
			controller.atObject(196,3266);  //"while" for gate wont break if someone else opens it
			controller.sleep(640);
		}
		controller.walkTo(200,3254);
		controller.walkTo(210,3254);
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
		JLabel header = new JLabel("Edge Druid Killer - By Kaila");
		JLabel label1 = new JLabel("Start in Edge bank with Gear");
		JLabel label2 = new JLabel("Sharks/Laws/Airs/Earths IN BANK REQUIRED");
		JLabel label3 = new JLabel("31 Magic Required for escape tele");
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
	    	int guamSuccessPerHr = 0;
    		int marSuccessPerHr = 0;
    		int tarSuccessPerHr = 0;
    		int harSuccessPerHr = 0;
    		int ranSuccessPerHr = 0;
    		int iritSuccessPerHr = 0;
    		int avaSuccessPerHr = 0;
    		int kwuSuccessPerHr = 0;
    		int cadaSuccessPerHr = 0;
    		int dwarSuccessPerHr = 0;
    		int lawSuccessPerHr = 0;
    		int natSuccessPerHr = 0;
    		int loopSuccessPerHr = 0;
    		int toothSuccessPerHr = 0;
    		int leftSuccessPerHr = 0;
    		int spearSuccessPerHr = 0;
    		int TripSuccessPerHr = 0;
    		
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		guamSuccessPerHr = (int)(totalGuam * scale);
	    		marSuccessPerHr = (int)(totalMar * scale);
	    		tarSuccessPerHr = (int)(totalTar * scale);
	    		harSuccessPerHr = (int)(totalHar * scale);
	    		ranSuccessPerHr = (int)(totalRan * scale);
	    		iritSuccessPerHr = (int)(totalIrit * scale);
	    		avaSuccessPerHr = (int)(totalAva * scale);
	    		kwuSuccessPerHr = (int)(totalKwuarm * scale);
	    		cadaSuccessPerHr = (int)(totalCada * scale);
	    		dwarSuccessPerHr = (int)(totalDwarf * scale);
	    		lawSuccessPerHr = (int)(totalLaw * scale);
	    		natSuccessPerHr = (int)(totalNat * scale);
	    		loopSuccessPerHr = (int)(totalLoop * scale);
	    		toothSuccessPerHr = (int)(totalTooth * scale);
	    		leftSuccessPerHr = (int)(totalLeft * scale);
	    		spearSuccessPerHr = (int)(totalSpear * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    		
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
	    	
			controller.drawString("@red@Edgeville Druids @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Guams: @gre@" + String.valueOf(this.totalGuam) + "@yel@ (@whi@" + String.format("%,d", guamSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Marrentills: @gre@" + String.valueOf(this.totalMar) + "@yel@ (@whi@" + String.format("%,d", marSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Tarromins: @gre@" + String.valueOf(this.totalTar) + "@yel@ (@whi@" + String.format("%,d", tarSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Harralanders: @gre@" + String.valueOf(this.totalHar) + "@yel@ (@whi@" + String.format("%,d", harSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Ranarrs: @gre@" + String.valueOf(this.totalRan) + "@yel@ (@whi@" + String.format("%,d", ranSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 118, 0xFFFFFF, 1);
			controller.drawString("@whi@Irit Herbs: @gre@" + String.valueOf(this.totalIrit) + "@yel@ (@whi@" + String.format("%,d", iritSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 132, 0xFFFFFF, 1);
			controller.drawString("@whi@Avantoes: @gre@" + String.valueOf(this.totalAva) + "@yel@ (@whi@" + String.format("%,d", avaSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 146, 0xFFFFFF, 1);
			controller.drawString("@whi@Kwuarms: @gre@" + String.valueOf(this.totalKwuarm) + "@yel@ (@whi@" + String.format("%,d", kwuSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 160, 0xFFFFFF, 1);
			controller.drawString("@whi@Cadantines: @gre@" + String.valueOf(this.totalCada) + "@yel@ (@whi@" + String.format("%,d", cadaSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 174, 0xFFFFFF, 1);
			controller.drawString("@whi@Dwarfs: @gre@" + String.valueOf(this.totalDwarf) + "@yel@ (@whi@" + String.format("%,d", dwarSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 188, 0xFFFFFF, 1);
			controller.drawString("@whi@Laws: @gre@" + String.valueOf(this.totalLaw) + "@yel@ (@whi@" + String.format("%,d", lawSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 202, 0xFFFFFF, 1);
			controller.drawString("@whi@Nats: @gre@" + String.valueOf(this.totalNat) + "@yel@ (@whi@" + String.format("%,d", natSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 216, 0xFFFFFF, 1);
			controller.drawString("@whi@Loop Half: @gre@" + String.valueOf(this.totalLoop) + "@yel@ (@whi@" + String.format("%,d", loopSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 230, 0xFFFFFF, 1);
			controller.drawString("@whi@Tooth Half: @gre@" + String.valueOf(this.totalTooth) + "@yel@ (@whi@" + String.format("%,d", toothSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 244, 0xFFFFFF, 1);
			controller.drawString("@whi@Shield Half: @gre@" + String.valueOf(this.totalLeft) + "@yel@ (@whi@" + String.format("%,d", leftSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 258, 0xFFFFFF, 1);
			controller.drawString("@whi@Rune Spear: @gre@" + String.valueOf(this.totalSpear) + "@yel@ (@whi@" + String.format("%,d", spearSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 272, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 286, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 300, 0xFFFFFF, 1);
		}
	}
}
