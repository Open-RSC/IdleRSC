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
 * Wildy Fire Giant Killer - By Kaila");
 * Start in Edge bank with Armor");
 * Sharks/Laws/Airs/Earths IN BANK REQUIRED");
 * 31 Magic Required for escape tele");
 * 
 * Author - Kaila
 */
public class K_Paladins extends IdleScript {	
	
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	
	int totalCoins = 0;
    int totalShark = 0;  //raw sharks
    int totalChaos = 0;
    int totalAda = 0;
    int totalSap = 0;
    int totalScim = 0;
    int coinsInBank = 0;
    int chaosInBank = 0;
    int sharksInBank = 0;
    int totalTrips = 0;
    
	int[] loot = {  438, 	 //Grimy ranarr
					427,    //black scim
					439,  	 //Grimy irit
					440,  	 //Grimy ava
					441,	 //Grimy kwu
					442, 	 //Grimy cada
					443, 	 //Grimy dwu
					619, 	//blood rune
					32,   //water rune
					170,  //iron bar
					171,   //steel bar
					173,   //mithril bar
					160, 	 //saph
					159, 	 //emerald
					158, 	 //ruby
					157,	 //diamond
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
			controller.displayMessage("@ran@Paladin Tower - By Kaila");
			if(controller.isInBank() == true) {
				controller.closeBank();
			}
			if(controller.currentY() < 1500) {
				bank();
				BankToPaladins();  
				controller.sleep(1380);
			}
			if(controller.currentY() > 1542 && controller.currentY() < 1548 && controller.currentX() == 609) {
				controller.atWallObject2(609,1548);     //locked door
				controller.sleep(640);
				while(controller.isBatching()) controller.sleep(1000);
				controller.sleep(640);
				controller.walkTo(610,1549);
				controller.sleep(640);
			}
			scriptStart();
		}
		return 1000; //start() must return a int value now. 
	}

	public void scriptStart(){
			while(controller.isRunning()) {
						
				eat();
				if(controller.isInCombat()) {  
					controller.setStatus("@red@Leaving combat..");
					controller.walkTo(610, 1549, 0, true);
					controller.sleep(800);
				} //added many leaveCombat throughout to fix breaking (hopefully)

					if(controller.getInventoryItemCount(546) > 0 && controller.currentY() > 1547 && controller.currentY() < 1552 ) { 
						
						
						if(!controller.isInCombat()) {
						   	ORSCharacter npc = controller.getNearestNpcById(323, false);
						   	if(npc != null) {
				    			controller.setStatus("@yel@Thieving Paladins");
						    	controller.thieveNpc(npc.serverIndex);
						    	controller.sleep(500);
							} else {
								controller.sleep(1000);
							}
							controller.sleep(100);
					    }
						for(int lootId : loot) {
							int[] coords = controller.getNearestItemById(lootId);
							if(coords != null) {      //Loot
								controller.setStatus("@yel@Looting.."); 
					   			controller.pickupItem(coords[0], coords[1], lootId, true, true);
					   			controller.sleep(618);
							} else {
								controller.sleep(1000);
							}
							controller.sleep(100);
					   	}
				    }
					if(controller.getInventoryItemCount(546) == 0) {   //bank if no food-
						controller.setStatus("@yel@Banking..");
						PaladinsToBank();
						bank();
						BankToPaladins();
						controller.sleep(618);
					}
					if(controller.getInventoryItemCount() == 30) {
						leaveCombat();
						controller.setStatus("@red@Eating Food to Loot..");
						if(controller.getInventoryItemCount(546) > 0) {
							controller.itemCommand(546);
							controller.sleep(700);
						}
					}
					leaveCombat();
					controller.sleep(100);
				} 
			}
			
			

		
	

	
	
	
	
	
	
	
	
	
	//Important PUBLIC VOID's below
	public void leaveCombat() {

		if(controller.isInCombat()) {   
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(610, 1549, 0, true);
			controller.sleep(800);
		}
	}


	public void eat() {
		
		int eatLvl = controller.getBaseStat(controller.getStatId("Hits")) - 20;
		
		if(controller.getCurrentStat(controller.getStatId("Hits")) < eatLvl) {
			
			if(controller.isInCombat()) {  
				controller.setStatus("@red@Leaving combat..");
				controller.walkTo(610, 1549, 0, true);
				controller.sleep(800);
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
			if(!ate) {
				controller.setStatus("@red@We've ran out of Food! Banking!.");
				controller.sleep(308);
				PaladinsToBank();
				bank();
				BankToPaladins();
			}
		}
	}
	
	public void bank() {   
		
		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(2000);
		
		if(controller.isInBank()){
		
			totalCoins = totalCoins + controller.getInventoryItemCount(10);
			totalChaos = totalChaos + controller.getInventoryItemCount(41);
			totalShark = totalShark + controller.getInventoryItemCount(545);
			totalAda = totalAda + controller.getInventoryItemCount(154);
			totalSap = totalSap + controller.getInventoryItemCount(160);
			totalScim = totalScim + controller.getInventoryItemCount(427);
			coinsInBank = (controller.getBankItemCount(10)/1000000);
			chaosInBank = controller.getBankItemCount(41);
			sharksInBank = controller.getBankItemCount(546);
			
			for (int itemId : controller.getInventoryItemIds()) {                                                            //change 546(shark) to desired food id
				controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				controller.sleep(320);
			}
			if(controller.getInventoryItemCount(546) < 27) {  //withdraw 27 shark if needed        //change 546(shark) to desired food id
				controller.withdrawItem(546, 27 - controller.getInventoryItemCount(546));          //change 546(shark) to desired food id
				controller.sleep(320);
			}
			
			controller.closeBank();
			controller.sleep(320);
			
		}
	}

	
	
	
	
	
	
	
	
	
	
	//Pathing Scripts Below
	
	public void PaladinsToBank() {
		
    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(611,1550);
		controller.atObject(611,1551);
		controller.sleep(640);

		
		int[] coords = controller.getNearestItemById(427);
		if(coords != null) {      //Loot
			controller.setStatus("@yel@Grabbing Black Scimmy.."); 
			controller.walkTo(coords[0], coords[1]);
			controller.sleep(640);
   			controller.pickupItem(coords[0], coords[1], 427, true, true);

   		}
		controller.sleep(640);
		int[] coords2 = controller.getNearestObjectById(338);
		if(coords2 != null) {
			controller.setStatus("@red@Stealing From Chest..");
				controller.walkTo(610,2488);
				controller.sleep(340);
				controller.atObject2(610,2487);  
				controller.sleep(340);
				controller.atObject2(610,2487);  
				controller.sleep(340);
				if(controller.currentX() == 610 && controller.currentY() == 2488) {  //got stuck here!!!
					controller.atObject2(610,2487);
					controller.sleep(1340);
				}
				witchhavenToBank();
		}
		if(coords2 == null) {
			controller.setStatus("@red@Chest Empty, Walking...");
			controller.walkTo(611,2494);
			controller.atObject(611,2495);
   			controller.sleep(320);
			controller.walkTo(609,1548);
			controller.atWallObject(609,1548);  //added this, some chance of breaking before.....
			controller.sleep(320);
   			if(controller.currentX() == 609 && controller.currentY() == 1548) {
   				controller.atWallObject(609,1548);     //locked door
   				controller.sleep(640);
   			}
			controller.walkTo(611,1544);	
			controller.atObject(611,1545);
			controller.walkTo(608,603);
			controller.walkTo(599,603);
			//add open front gate to castle?
			controller.walkTo(577,603);
			controller.walkTo(574,606);
			controller.walkTo(564,606);
			controller.walkTo(552,606);
			controller.walkTo(550,608);
			controller.walkTo(550,612);
	    	controller.setStatus("@gre@Done Walking..");
			totalTrips = totalTrips + 1;
			controller.sleep(640);
		}
	}
	
	
	
    public void BankToPaladins() {	
    	controller.setStatus("@gre@Walking to Paladins..");
		controller.walkTo(550,612);
		controller.walkTo(550,606);
		controller.walkTo(560,606);
		controller.walkTo(570,606);	
		controller.walkTo(582,606);
		controller.walkTo(585,603);	
		controller.walkTo(598,603);
		//insert open outer castle gate?
		controller.walkTo(607,603);
		controller.walkTo(612,604);	//just below stair
		while(controller.currentX() < 615 && controller.currentX() > 609 && controller.currentY() < 610 && controller.currentY() > 600) {  //needs to be WHILE to escape paladins
				controller.walkTo(612,604);
				controller.atObject(611,601);    //sometimes get stuck here  CHANGED TO AREA INSTEAD!!!  change coords to caslte perimeter instead!!
				controller.sleep(320);
		}
		controller.walkTo(609,1547);		
		if(controller.currentX() == 609 && controller.currentY() == 1547) {
			controller.atWallObject2(609,1548);     //locked door
			controller.sleep(640);
			while(controller.isBatching()) controller.sleep(1000);
		}
    	controller.setStatus("@gre@Done Walking..");
	}
	
	
    public void witchhavenToBank() {
		if(controller.currentX() > 500 && controller.currentX() < 532) {
			controller.walkTo(528,597);
			controller.walkTo(534,597);
			controller.sleep(640);
		}
		controller.walkTo(534,597);
		controller.walkTo(543,597);
		controller.walkTo(543,605);
		controller.walkTo(550,612);
		controller.sleep(320);
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
		JLabel header = new JLabel("Paladin Thiever - By Kaila");
		JLabel label1 = new JLabel("Start in Ardy South Bank OR in Paladin Tower");
		JLabel label2 = new JLabel("Sharks in bank REQUIRED, can be changed in script");
		JLabel label3 = new JLabel("Switching to Defensive combat mode is ideal.");
		JLabel label4 = new JLabel("Low Atk/Str and Higher Def is more Efficient");
		JLabel label5 = new JLabel("Ensure to never wield weapons when Thieving.");
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
		scriptFrame.add(label4);
		scriptFrame.add(label5);
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
    		int CoinSuccessPerHr = 0;
    		int ChaosSuccessPerHr = 0;
    		int TripSuccessPerHr = 0;
    	    
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		CoinSuccessPerHr = (int)(totalCoins * scale);
	    		ChaosSuccessPerHr = (int)(totalChaos * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    		
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Paladins Thiever @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Coins: @gre@" + String.valueOf(this.totalCoins) + "@yel@ (@whi@" + String.format("%,d", CoinSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Chaos: @gre@" + String.valueOf(this.totalChaos) + "@yel@ (@whi@" + String.format("%,d", ChaosSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Raw Shark: @gre@" + String.valueOf(this.totalShark), 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Adamantite Ore: @gre@" + String.valueOf(this.totalAda), 350, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Uncut Sapphire: @gre@" + String.valueOf(this.totalSap), 350, 118, 0xFFFFFF, 1);
			controller.drawString("@whi@Black Scimitar: @gre@" + String.valueOf(this.totalScim), 350, 132, 0xFFFFFF, 1);
			controller.drawString("@whi@Items In Bank:", 330, 146, 0xFFFFFF, 1);
			controller.drawString("@whi@(1M) Coins: @yel@" + String.valueOf(this.coinsInBank), 350, 160, 0xFFFFFF, 1);
			controller.drawString("@whi@Chaos: @yel@" + String.valueOf(this.chaosInBank), 350, 174, 0xFFFFFF, 1);
			controller.drawString("@whi@Cooked Sharks: @yel@" + String.valueOf(this.sharksInBank), 350, 188, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 202, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 216, 0xFFFFFF, 1);
		}
	}
}
