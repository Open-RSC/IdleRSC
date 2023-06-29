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
 * Wildy Fire Giant Killer - By Kaila");
 * Start in Edge bank with Armor");
 * Sharks/Laws/Airs/Earths IN BANK REQUIRED");
 * 31 Magic Required for escape tele");
 * 
 * Author - Kaila
 */
public class K_WaterfallFireGiants extends IdleScript {	
	
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	
	int totalBstaff = 0;
    int totalRscim = 0;
    int totalRunestuff = 0;
    int totalGems = 0;
    int totalFire = 0;
    int totalLaw = 0;
    int totalNat = 0;
    int totalChaos = 0;
    int totalDeath = 0;
    int totalBlood = 0;
    int totalRbar = 0;
    int totalLoop = 0;
    int totalTooth = 0;
    int totalDstone = 0;
    int totalLeft = 0;
    int totalSpear = 0;  
    int totalMed = 0; 
    int totalHerb = 0;
    int totalTrips = 0;

	int[] bones = {20, 413, 604, 814};
   	int[] attackPot = {488,487,486};
   	int[] strPot = {494,493,492};
	int[] loot = {  413,		 // big bones
					438, 	 //Grimy ranarr
					439,  	 //Grimy irit
					440,  	 //Grimy ava
					441,	 //Grimy kwu
					442, 	 //Grimy cada
					443, 	 //Grimy dwu
					40,	 	 // nature rune
					42, 	 // law rune
					160, 	 //saph
					159, 	 //emerald
					158, 	 //ruby
					157,	 //diamond
					526, 	 //tooth half
					527, 	 //loop half
					1277, 	 //shield (left) half
					1092, 	 //rune spear
					404, 	 //rune kite
					403,	 //rune square
					542,	 //uncut dstone
					523,  	//cut dstone
					795,  	//D med
					405,  	//rune axe
					408,  	//rune bar
					81, 	//rune 2h
					93, 	//rune battle axe
					38, 	//death rune
					520, 	//silver cert
					518, 	//coal cert
					398, 	//rune scimmy
					615, 	//fire bstaff
					619, 	//blood rune
					41, 	//chaos rune
					31, 		//fire rune
					373 	//lobster (will get eaten)
					};

	
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	
	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (scriptStarted) {
			controller.displayMessage("@red@Waterfall Fire Giant Killer - By Kaila");
			controller.displayMessage("@red@Start in Seers bank with gear on, or in fire giant room!");
			controller.displayMessage("@red@Sharks IN BANK REQUIRED");
			if(controller.isInBank() == true) {
				controller.closeBank();
			}
			if(controller.currentY() < 500) {
				bank();
				BankToGiants();  
				controller.sleep(1380);
			}
			scriptStart();
		}
		return 1000; //start() must return a int value now. 
	}













	public void scriptStart() {
			while(controller.isRunning()) {
				
				buryBones();
				eat();

				if(controller.getInventoryItemCount(546) > 0) {

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

						if(controller.getCurrentStat(controller.getStatId("Attack")) == controller.getBaseStat(controller.getStatId("Attack"))) {
							if(controller.getInventoryItemCount(attackPot[0]) > 0 || controller.getInventoryItemCount(attackPot[1]) > 0 || controller.getInventoryItemCount(attackPot[2]) > 0 ) {
							attackBoost();
							}
						}
						if(controller.getCurrentStat(controller.getStatId("Strength")) == controller.getBaseStat(controller.getStatId("Strength"))) {
							if(controller.getInventoryItemCount(strPot[0]) > 0 || controller.getInventoryItemCount(strPot[1]) > 0 || controller.getInventoryItemCount(strPot[2]) > 0 ) {
							strengthBoost();
							}
						}

						if(!controller.isInCombat()) {
							controller.setStatus("@yel@Attacking Giants");
							controller.sleepHandler(98, true);
							ORSCharacter npc = controller.getNearestNpcById(344, false);
							if(npc != null) {
								controller.attackNpc(npc.serverIndex);
								controller.sleep(1000);
							}
						}
						controller.sleep(1380);
					}
					if(controller.getInventoryItemCount() == 30) {
						while(controller.isInCombat()) {
							controller.setStatus("@red@Leaving combat..");
							controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
							controller.sleep(250);
						}
						buryBones();
						if(controller.getInventoryItemCount(465) > 0 && !controller.isInCombat()) {
							controller.setStatus("@red@Dropping Vial to Loot..");
							controller.dropItem(controller.getInventoryItemSlotIndex(465));
							controller.sleep(250);
						}
						controller.setStatus("@red@Eating Food to Loot..");
						for(int id : controller.getFoodIds()) {
							if(controller.getInventoryItemCount(id) > 0 && !controller.isInCombat()) {
								controller.itemCommand(id);
								controller.sleep(700);
							}
						}
					}
				}
				if(controller.getInventoryItemCount(546) == 0) {
					controller.setStatus("@yel@Banking, escaping south..");
					giantEscape();
					GiantsToBank();
					bank();
					BankToGiants();
					controller.sleep(618);
					}
				}
			}
	
					
		
	

	
	
	
	//PUBLIC VOIDS
	
	
	
	
	

	
    public void buryBones() {
    	if(!controller.isInCombat()) {
			for(int id : bones) {
				if(controller.getInventoryItemCount(id) > 0) {
					controller.setStatus("@red@Burying bones..");
					controller.itemCommand(id);
					
					controller.sleep(618);
					//buryBones();
				}
			}
    	}
    }
	
	public void bank() {   
		
		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(1200);
		
		while(controller.isInBank()){

			totalBstaff = totalBstaff + controller.getInventoryItemCount(615);
			totalRscim = totalRscim + controller.getInventoryItemCount(398);
			totalRunestuff = totalRunestuff 
					+ controller.getInventoryItemCount(404) //kite
					+ controller.getInventoryItemCount(403) //sq
					+ controller.getInventoryItemCount(405) //axe
					+ controller.getInventoryItemCount(81) //2h
					+ controller.getInventoryItemCount(93) //bAxe
					+ controller.getInventoryItemCount(408); //r bar
			totalGems = totalGems 
					+ controller.getInventoryItemCount(160) 
					+ controller.getInventoryItemCount(159)
					+ controller.getInventoryItemCount(158)
					+ controller.getInventoryItemCount(157);
			totalHerb = totalHerb
					+ controller.getInventoryItemCount(438) 
					+ controller.getInventoryItemCount(439) 
					+ controller.getInventoryItemCount(440) 
					+ controller.getInventoryItemCount(441) 
					+ controller.getInventoryItemCount(442) 
					+ controller.getInventoryItemCount(443);
			totalFire = totalFire + controller.getInventoryItemCount(31);
			totalLaw = totalLaw + controller.getInventoryItemCount(42);
			totalNat = totalNat + controller.getInventoryItemCount(40);
			totalChaos = totalChaos + controller.getInventoryItemCount(41);
			totalBlood = totalBlood + controller.getInventoryItemCount(619);
			totalLoop = totalLoop + controller.getInventoryItemCount(527);
			totalTooth = totalTooth + controller.getInventoryItemCount(526);
			totalDstone = totalDstone + controller.getInventoryItemCount(523);
			totalLeft = totalLeft + controller.getInventoryItemCount(1277);
			totalSpear = totalSpear + controller.getInventoryItemCount(1092);
			totalMed = totalMed + controller.getInventoryItemCount(795);
			
		
			for (int itemId : controller.getInventoryItemIds()) {
				if (itemId != 782 && itemId != 237 && itemId != 486 && itemId != 487 && itemId != 488 && itemId != 492 && itemId != 493 && itemId != 494 && itemId != 546 ) {
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				}
			}
			controller.sleep(1400);   // increased sleep here to prevent double banking

			if(controller.getInventoryItemCount(782) < 1) {  //glariels amulet
				controller.withdrawItem(782, 1);
				controller.sleep(640);
			}
			if(controller.getInventoryItemCount(237) < 1) {  //rope
				controller.withdrawItem(237, 1);
				controller.sleep(640);
			}
			if(controller.getInventoryItemCount(33) < 30) {  //air
				controller.withdrawItem(33, 30 - controller.getInventoryItemCount(33));
				controller.sleep(1000);
			}
			if(controller.getInventoryItemCount(42) < 6) {  //law
				controller.withdrawItem(42, 6 - controller.getInventoryItemCount(42));
				controller.sleep(1000);
			}
			controller.sleep(640);  //leave in
			if(controller.getInventoryItemCount(attackPot[0]) < 1 && controller.getInventoryItemCount(attackPot[1]) < 1 && controller.getInventoryItemCount(attackPot[2]) < 1 ) {  //withdraw 10 shark if needed
				controller.withdrawItem(attackPot[2], 1);
				controller.sleep(640);
			}
			if(controller.getInventoryItemCount(strPot[0]) < 1 && controller.getInventoryItemCount(strPot[1]) < 1 && controller.getInventoryItemCount(strPot[2]) < 1 ) {  //withdraw 10 shark if needed
				controller.withdrawItem(strPot[2], 1);
				controller.sleep(640);
			}
			if(controller.getInventoryItemCount(33) < 30) {  //air
				controller.withdrawItem(33, 30 - controller.getInventoryItemCount(33));
				controller.sleep(1000);
			}
			if(controller.getInventoryItemCount(42) < 6) {  //law
				controller.withdrawItem(42, 6 - controller.getInventoryItemCount(42));
				controller.sleep(1000);
			}
			if(controller.getInventoryItemCount(546) < 23) {  //withdraw 23 shark
				controller.withdrawItem(546, 23 - controller.getInventoryItemCount(546));
				controller.sleep(640);
			}

			if(controller.getBankItemCount(546) == 0 || controller.getBankItemCount(33) == 0 || controller.getBankItemCount(42) == 0) {
				controller.setStatus("@red@NO Sharks/Laws/Airs in the bank, Logging Out!.");
				controller.setAutoLogin(false);
				controller.logout();
				if(!controller.isLoggedIn()) {
					controller.stop();
					return;
				}
			}
			controller.closeBank();
			controller.sleep(1000);
		}
		lawCheck();
		airCheck();
	}
	public void lawCheck() {
		if(controller.getInventoryItemCount(42) < 5) {  //law
			controller.openBank();
			controller.sleep(1200);
			controller.withdrawItem(42, 5 - controller.getInventoryItemCount(42));
			controller.sleep(1000);
			controller.closeBank();
			controller.sleep(1000);
		}
	}
	public void airCheck() {
		if(controller.getInventoryItemCount(33) < 15) {  //air
			controller.openBank();
			controller.sleep(1200);
			controller.withdrawItem(33, 15 - controller.getInventoryItemCount(33));
			controller.sleep(1000);
			controller.closeBank();
			controller.sleep(1000);
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
				controller.setStatus("@red@We've ran out of Food! Running Away!.");
				controller.sleep(308);
				giantEscape();
				GiantsToBank();
				bank();
				BankToGiants();
			}
		}
	}





















	public void giantEscape() {
		controller.setStatus("We've ran out of Food! @gre@Going to safe zone.");
		controller.walkTo(660,3295);
		controller.sleep(1000);
		if (controller.currentX() > 661 || controller.currentY() > 3000 && controller.currentY() < 3295) {
			controller.walkTo(660,3295);
			controller.sleep(1000);
		}
	}
	public void GiantsToBank() {
		controller.setStatus("@gre@Walking to Bank. Casting 2nd teleport.");
		controller.castSpellOnSelf(controller.getSpellIdFromName("Camelot Teleport"));
		controller.sleep(400);
		if(controller.currentY() > 3000) {
			controller.setStatus("@gre@Teleport unsuccessful, Casting 3rd teleport.");
			controller.castSpellOnSelf(controller.getSpellIdFromName("Camelot Teleport"));
			controller.sleep(600);
		}
		controller.sleep(300);
		while(controller.currentY() > 3000) {
			controller.setStatus("@gre@Teleport unsuccessful, Casting nth teleport.");
			controller.castSpellOnSelf(controller.getSpellIdFromName("Camelot Teleport"));
			controller.sleep(640);
		}
		controller.setStatus("@gre@Going to Bank. Casting 1st teleport.");
		controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
		controller.sleep(1000);
		if(controller.currentY() > 3000) {
			controller.setStatus("@gre@Teleport unsuccessful, Casting 2rd teleport.");
			controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
			controller.sleep(1000);
		}
		controller.sleep(300);
		if(controller.currentY() > 3000) {
			controller.setStatus("@gre@Teleport unsuccessful, Casting 3rd teleport.");
			controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
			controller.sleep(1000);
		}
		controller.sleep(300);
		if(controller.currentY() > 3000) {
			controller.setStatus("@gre@Teleport unsuccessful, Casting 4th teleport.");
			controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
			controller.sleep(1000);
		}
		controller.sleep(300);
		if(controller.currentY() > 3000) {
			controller.setStatus("@gre@Teleport unsuccessful, Casting 5th teleport.");
			controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
			controller.sleep(1000);
		}
		controller.sleep(300);
		if(controller.currentY() > 3000) {
			controller.setStatus("@gre@Teleport unsuccessful, Casting 6th teleport.");
			controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
			controller.sleep(1000);
		}
		totalTrips = totalTrips + 1;
		controller.walkTo(468,462);
		if(controller.getObjectAtCoord(467, 463) == 57) {
			controller.setStatus("@gre@Opening Castle Gate..");
			controller.walkTo(468,462);
			controller.sleep(100);
			controller.atObject(467, 463);
			controller.sleep(1000);
		}
		controller.walkTo(468,464);
		controller.walkTo(478,464);
		controller.walkTo(488,464);
		controller.walkTo(496,455);    	
		controller.walkTo(501,454);
		controller.sleep(308);
    	controller.setStatus("@gre@Done Walking..");
	}  
	
    public void BankToGiants() {	
    	controller.setStatus("@gre@Walking to Fire Giants..");
		controller.walkTo(501,456);
		controller.walkTo(522,456);
		controller.walkTo(543,477);
		controller.walkTo(560,477);
		controller.walkTo(576,477);
		controller.walkTo(581,472);
		controller.walkTo(587,465);
		controller.walkTo(590,461);
		controller.walkTo(592,458);
		while(controller.currentX() == 592 && controller.currentY() == 458) {
			controller.atObject(593, 458);    //log
			controller.sleep(640);
		}
		controller.walkTo(602,458);
		controller.walkTo(607,463);
		controller.walkTo(612,468);
		controller.walkTo(617,473);
		if(controller.getObjectAtCoord(617, 474) == 57) {
			controller.setStatus("@gre@Opening Coal Mine Gate..");
			controller.walkTo(617,473);
			controller.sleep(100);
			controller.atObject(617, 474);
			controller.sleep(1000);
		}
		controller.walkTo(618,474);
		controller.walkTo(618,480);
		controller.walkTo(623,480);
		controller.walkTo(637,466);
		controller.walkTo(637,458);
		controller.walkTo(648,448);
		controller.walkTo(659,449);
		controller.sleep(1000);
		entranceScript();
	}

	public void entranceScript() {
		if(controller.isCloseToCoord(659 , 449)) {
			controller.atObject(660,449);  //boat
			controller.sleep(10000);
		}
		if(controller.isCloseToCoord(662 , 463)) {
			controller.equipItem(controller.getInventoryItemSlotIndex(782));
			controller.useItemIdOnObject(662,463,237); //1st tree
			controller.sleep(10000);
		}
		if(controller.isCloseToCoord(662, 467)) {
			controller.equipItem(controller.getInventoryItemSlotIndex(782));
			controller.useItemIdOnObject(662,467,237); //2nd tree
			controller.sleep(10000);
		}
		if(controller.isCloseToCoord(659 , 471)) {
			controller.equipItem(controller.getInventoryItemSlotIndex(782));
			controller.useItemIdOnObject(659,471,237); //3rd tree
			controller.sleep(12000);
		}
		if(controller.isCloseToCoord(659, 3305)) {
			controller.walkTo(659,3303);
			controller.sleep(1000);  //broke outside door
			if(!controller.isItemIdEquipped(782)) {
				controller.setAutoLogin(false);
				controller.logout();
				if(!controller.isLoggedIn()) {
					controller.stop();
					return;
				}
			}
			while(controller.currentX() == 659 && controller.currentY() == 3303) { //wont break when others open same tick
				controller.atObject(659, 3303);   //gate
				controller.sleep(1000);
			}
			controller.walkTo(659,3289);
			controller.equipItem(controller.getInventoryItemSlotIndex(522));
		}
		controller.sleep(640);
		controller.setStatus("@gre@Done Walking..");
	}


















	public void attackBoost() {
		while(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
			controller.sleep(250);
		}
		if(controller.getInventoryItemCount(attackPot[0]) > 0) {
			controller.itemCommand(attackPot[0]);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(attackPot[1]) > 0) {
			controller.itemCommand(attackPot[1]);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(attackPot[2]) > 0) {
			controller.itemCommand(attackPot[2]);
			controller.sleep(320);
			return;
		}
		return;
	}

	public void strengthBoost() {
		while(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
			controller.sleep(250);
		}
		if(controller.getInventoryItemCount(strPot[0]) > 0) {
			controller.itemCommand(strPot[0]);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(strPot[1]) > 0) {
			controller.itemCommand(strPot[1]);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(strPot[2]) > 0) {
			controller.itemCommand(strPot[2]);
			controller.sleep(320);
			return;
		}
		return;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//GUI stuff below (icky)
	
	
	
	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	public void setupGUI() {
		JLabel header = new JLabel("Waterfall Fire Giant Killer - By Kaila");
		JLabel label1 = new JLabel("Start in Seers bank OR in Giants room");
		JLabel label2 = new JLabel("Sharks, Law, Air required");
		JLabel label3 = new JLabel("Must have glariels amulet and rope");
		JLabel label4 = new JLabel("Bot will attempt to wield dragonstone amulet");
		JLabel label5 = new JLabel("can be changed");
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
    		int BstaffSuccessPerHr = 0;
    		int RscimSuccessPerHr = 0;
    		int RuneSuccessPerHr = 0;
    		int GemsSuccessPerHr = 0;
    		int FireSuccessPerHr = 0;
    		int LawSuccessPerHr = 0;
    		int NatSuccessPerHr = 0;
    		int ChaosSuccessPerHr = 0;
    		int BloodSuccessPerHr = 0;
    	    int HerbSuccessPerHr = 0;
    		int TripSuccessPerHr = 0;
    	    
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		BstaffSuccessPerHr = (int)(totalBstaff * scale);
	    		RscimSuccessPerHr = (int)(totalRscim * scale);
	    		RuneSuccessPerHr = (int)(totalRunestuff * scale);
	    		GemsSuccessPerHr = (int)(totalGems * scale);
	    		FireSuccessPerHr = (int)(totalFire * scale);
	    		LawSuccessPerHr = (int)(totalLaw * scale);
	    		NatSuccessPerHr = (int)(totalNat * scale);
	    		ChaosSuccessPerHr = (int)(totalChaos * scale);
	    		BloodSuccessPerHr = (int)(totalBlood * scale);
	    		HerbSuccessPerHr = (int)(totalHerb * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Waterfall Fire Giants @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Laws: @gre@" + String.valueOf(this.totalLaw) + "@yel@ (@whi@" + String.format("%,d", LawSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Natures: @gre@" + String.valueOf(this.totalNat) + "@yel@ (@whi@" + String.format("%,d", NatSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Fires: @gre@" + String.valueOf(this.totalFire) + "@yel@ (@whi@" + String.format("%,d", FireSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Chaos: @gre@" + String.valueOf(this.totalChaos) + "@yel@ (@whi@" + String.format("%,d", ChaosSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Bloods: @gre@" + String.valueOf(this.totalBlood) + "@yel@ (@whi@" + String.format("%,d", BloodSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 118, 0xFFFFFF, 1);
			controller.drawString("@whi@Fire Bstaff: @gre@" + String.valueOf(this.totalBstaff) + "@yel@ (@whi@" + String.format("%,d", BstaffSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 132, 0xFFFFFF, 1);  //fix y cords
			controller.drawString("@whi@Rune Scim: @gre@" + String.valueOf(this.totalRscim) + "@yel@ (@whi@" + String.format("%,d", RscimSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 146, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Herbs: @gre@" + String.valueOf(this.totalHerb) + "@yel@ (@whi@" + String.format("%,d", HerbSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 160, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Gems: @gre@" + String.valueOf(this.totalGems) + "@yel@ (@whi@" + String.format("%,d", GemsSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 174, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Rune Items: @gre@" + String.valueOf(this.totalRunestuff) + "@yel@ (@whi@" + String.format("%,d", RuneSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 188, 0xFFFFFF, 1);
			controller.drawString("@whi@Tooth: @gre@" + String.valueOf(this.totalTooth) + "@yel@ / @whi@Loop: @gre@" + String.valueOf(this.totalLoop), 350, 202, 0xFFFFFF, 1);
			controller.drawString("@whi@Dstone: @gre@" + String.valueOf(this.totalDstone) + "@yel@ / @whi@Rune Spear: @gre@" + String.valueOf(this.totalSpear), 350, 216, 0xFFFFFF, 1);
			controller.drawString("@whi@D Med: @gre@" + String.valueOf(this.totalMed) + "@yel@ / @whi@Left Half: @gre@" + String.valueOf(this.totalLeft), 350, 230, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 244, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 258, 0xFFFFFF, 1);
		}
	}
}
