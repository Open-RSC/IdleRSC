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
public class K_WildyFireGiants extends IdleScript {	
	
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
	int[] loot = {  //413,   // big bones //un-comment this to loot and bury dbones, it will reduce Kills per Hr significantly b/c of Shadow Spiders
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
					31, 	//fire rune
					373 	//lobster (will get eaten)
				};
	
    public boolean isWithinLootzone(int x, int y) { 
    	return controller.distance(269, 2949, x, y) <= 10;
    }
	
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	
	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (scriptStarted) {
			controller.displayMessage("@red@Wildy Fire Giant Killer - By Kaila");
			controller.displayMessage("@red@Start in Mage bank OR in Giants room");
			controller.displayMessage("@red@Sharks IN BANK REQUIRED");
			if(controller.isInBank() == true) {
				controller.closeBank();
			}
			if(controller.currentX() > 260 && controller.currentX() < 275 && controller.currentY() < 132 && controller.currentY() > 125) {
				stairToGiants();
				controller.sleep(1380);
			}
			if(controller.currentY() > 3364) {
				bank();
				BankToStair();
				stairToGiants();
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

			if(controller.getInventoryItemCount(465) > 0 && !controller.isInCombat()) {
				controller.dropItem(controller.getInventoryItemSlotIndex(465));
			}
			if(controller.getInventoryItemCount(546) > 0) {
				if(controller.getInventoryItemCount() < 30) {
					boolean lootPickedUp = false;
					for(int lootId : loot) {
						int[] coords = controller.getNearestItemById(lootId);
						if(coords != null && this.isWithinLootzone(coords[0], coords[1])) {
							controller.setStatus("@yel@Looting..");
							controller.walkTo(coords[0], coords[1]);
							controller.pickupItem(coords[0], coords[1], lootId, true, true);
							controller.sleep(618);
						}
					}
					if(lootPickedUp) //we don't want to start to pickup loot then immediately attack a npc
						continue;

					if(controller.getInventoryItemCount(795) > 0 || controller.getInventoryItemCount(1277) > 0) {  //bank if d med, or left half in inv
						controller.setStatus("@yel@DANK drop acquired, Banking..");
						GiantsToBank();
						bank();
						BankToStair();
						stairToGiants();
						controller.sleep(618);
					}
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
							controller.walktoNPC(npc.serverIndex, 1);
							controller.attackNpc(npc.serverIndex);
							controller.sleep(1000);
						} else {
							controller.sleep(1000);
						}
					}
					controller.sleep(340);
				}
				if(controller.getInventoryItemCount() == 30) {
					while(controller.isInCombat()) {
						controller.setStatus("@red@Leaving combat..");
						controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
						controller.sleep(340);
					}
					controller.setStatus("@red@Eating Food to Loot..");
					buryBones();
					for(int id : controller.getFoodIds()) {
						if(controller.getInventoryItemCount(id) > 0 && controller.getInventoryItemCount() == 30) {
							controller.itemCommand(id);
							controller.sleep(700);
						}
					}
				}
			}
			if(controller.getInventoryItemCount(546) == 0 || controller.getInventoryItemCount() == 30) {
				controller.setStatus("@yel@Banking..");
				GiantsToBank();
				bank();
				BankToStair();
				stairToGiants();
				controller.sleep(618);
			}
		}
	}





					
    public void buryBones() {
    	if(!controller.isInCombat()) {
			for(int id : bones) {
				if(controller.getInventoryItemCount(id) > 0) {
					controller.setStatus("@red@Burying bones..");
					controller.itemCommand(id);
					
					controller.sleep(618);
					buryBones();
				}
			}
    	}
    }

	
	public void attackBoost() {
		while(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
			controller.sleep(340);
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
			controller.sleep(340);
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
	
	public void bank() {   
		
		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(640);

		if(controller.isInBank()){

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
			
			
		//	if (controller.getInventoryItemCount() > 1) {
				for (int itemId : controller.getInventoryItemIds()) {
					if (itemId != 546 && itemId != 486 && itemId != 487 && itemId != 488 && itemId != 492 && itemId != 493 && itemId != 494) {
						controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
					}
				}
				controller.sleep(1280);   // increased sleep here to prevent double banking
			//}
			if(controller.getInventoryItemCount(attackPot[0]) < 1 && controller.getInventoryItemCount(attackPot[1]) < 1 && controller.getInventoryItemCount(attackPot[2]) < 1 ) {  //withdraw 10 shark if needed
				controller.withdrawItem(attackPot[2], 1);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(strPot[0]) < 1 && controller.getInventoryItemCount(strPot[1]) < 1 && controller.getInventoryItemCount(strPot[2]) < 1 ) {  //withdraw 10 shark if needed
				controller.withdrawItem(strPot[2], 1);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(546) < 27) {  //withdraw 10 shark if needed
				controller.withdrawItem(546, 27 - controller.getInventoryItemCount(546));
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
			eat();
		}

	}
	
	public void eat() {
		
		int eatLvl = controller.getBaseStat(controller.getStatId("Hits")) - 20;
		int panicLvl = controller.getBaseStat(controller.getStatId("Hits")) - 50;

		if(controller.getCurrentStat(controller.getStatId("Hits")) < panicLvl) {
			controller.setStatus("@red@We've taken massive damage! Running Away!.");  //Tested and when panic hp goToBank then Logout is working
			controller.sleep(308);
			GiantsToBank();
			bank();
			controller.setAutoLogin(false);
			controller.logout();
			controller.sleep(1000);

			if(!controller.isLoggedIn()) {
				controller.stop();
				controller.logout();
				return;
			}
		}
		if(controller.getCurrentStat(controller.getStatId("Hits")) < eatLvl) {
			
			while(controller.isInCombat()) {
				controller.setStatus("@red@Leaving combat..");
				controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
				controller.sleep(340);
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
				GiantsToBank();
				bank();
				BankToStair();
				stairToGiants();
			}
		}
	}
	public void GiantsToBank() {
    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(273,2953);
		if(controller.getObjectAtCoord(274,2952) == 57) {
					//&& controller.currentX() < 275 && controller.currentX() > 271 && controller.currentY() < 2958 && controller.currentY() > 2950) {
				controller.setStatus("@gre@Opening Fire Giant Gate..");
				controller.walkTo(273,2953);
				controller.atObject(274,2952);
				controller.sleep(340);
		}
		controller.walkTo(275,2953);
		controller.walkTo(282,2969);  //broke hgere
		if(controller.getObjectAtCoord(281,2969) == 57) {
					//&& controller.currentX() < 285 && controller.currentX() > 280 && controller.currentY() < 2974 && controller.currentY() > 2965) {
				controller.setStatus("@gre@Opening Chaos Dwarf Gate..");
				controller.walkTo(282,2969);
				controller.atObject(281,2969);
				controller.sleep(340);
		}
		controller.walkTo(277,2971);
		controller.walkTo(273,2972);
		if(controller.getObjectAtCoord(272,2972) == 57) {
					//&& controller.currentX() < 275 && controller.currentX() > 269 && controller.currentY() < 2975 && controller.currentY() > 2969) {
				controller.setStatus("@gre@Opening Giants Gate..");
				controller.walkTo(273,2972);
				controller.atObject(272,2972);
				controller.sleep(340);
		}
		controller.walkTo(269,2972);
		controller.walkTo(269,2963);
		controller.setStatus("@gre@Trying Stairs.."); 
		controller.atObject(268,2960);   //try stairs once
		controller.sleep(1280);
		if(controller.currentX() == 269 && controller.currentY() == 2963) {
			controller.setStatus("@gre@Going up Stairs..");   //bot breaks here sometimes????

			while(controller.isInCombat()) {  //stuck in while loop!!!
				controller.setStatus("@red@Leaving combat..");
				controller.walkTo(269,2963);
				controller.sleep(100);
			}
			controller.atObject(268,2960);
			controller.sleep(1000);
		}
		controller.walkTo(268,126);
		controller.walkTo(254,126);
		controller.walkTo(232,104);
		controller.walkTo(227,105);
		controller.sleep(340);
		if(!controller.isDoorOpen(227,106)) {
					//&& controller.currentX() < 230 && controller.currentX() > 225 && controller.currentY() < 106 && controller.currentY() > 103) {
				controller.setStatus("@gre@Opening Mage Bank Outer Door..");
				controller.walkTo(227,105);
				controller.openDoor(227,106);
				controller.sleep(340);
		}
		controller.walkTo(227,106);
		controller.sleep(1000);
		//if(controller.currentX() > 224 && controller.currentX() < 231 && controller.currentY() < 107) {
			while(controller.getWallObjectIdAtCoord(227, 107) == 24) {
				controller.setStatus("@gre@Cutting Outer Web..");
				controller.walkTo(227,106);
				controller.atWallObject(227,107);
				controller.sleep(300);
			}
		//}
		controller.walkTo(227,108);
		controller.sleep(340);
		if(controller.currentX() == 227 && controller.currentY() > 106 && controller.currentY() < 112){
			while(controller.getWallObjectIdAtCoord(227, 109) == 24) {
				controller.setStatus("@gre@Cutting Inner Web..");
				controller.walkTo(227, 108);
				controller.atWallObject(227, 109);
				controller.sleep(300);
			}
		}
		controller.walkTo(226,110);
		controller.sleep(340);
		if(!controller.isDoorOpen(226,110)) {
				//&& controller.currentX() < 229 && controller.currentX() > 224 && controller.currentY() < 112 && controller.currentY() > 109) {
				controller.setStatus("@gre@Opening Mage Bank Inner Door..");
				controller.walkTo(226,110);
				controller.openDoor(226,110);
				controller.sleep(340);
		}
		controller.walkTo(227,107);
		controller.sleep(320);
		controller.atObject(223,110);
		controller.sleep(320);
		totalTrips = totalTrips + 1;
		controller.walkTo(451,3371);
		controller.walkTo(453,3376);
    	controller.setStatus("@gre@Done Walking..");
	}  //107 and 109 y cord of web=
	
    public void BankToStair() {
		controller.setStatus("@gre@Walking to Fire Giants..");
		controller.walkTo(453, 3374);
		controller.walkTo(450, 3370);
		controller.walkTo(446, 3368);
		controller.sleep(340);
		controller.atObject(446, 3367);
		controller.sleep(640);
		if (controller.currentX() == 446 && controller.currentY() == 3368) {
			controller.atObject(446, 3367);
			controller.sleep(640);
		}
		controller.walkTo(225, 110);  //error code for this line, need more sleep?
		controller.sleep(340);
		if (!controller.isDoorOpen(226, 110)) {
					//&& controller.currentX() < 227 && controller.currentX() > 224 && controller.currentY() == 110) {
				controller.setStatus("@gre@Opening Mage Bank Inner Door..");
				controller.walkTo(225, 110);
				controller.atWallObject(226, 110);
				controller.sleep(340);
		}
		controller.walkTo(227, 109);
		controller.sleep(340);
		if (controller.currentY() > 108 && controller.currentY() < 112) {
			while (controller.getWallObjectIdAtCoord(227, 109) == 24) {
				controller.setStatus("@gre@Cutting Inner Web..");
				controller.walkTo(227, 109);
				controller.atWallObject(227, 109);
				controller.sleep(300);
			}
		}
		controller.walkTo(227, 107);
		controller.sleep(340);
		controller.atWallObject(227, 107);
		controller.sleep(300);
		while (controller.getWallObjectIdAtCoord(227, 107) == 24) { //still breaking here???
			controller.setStatus("@gre@Cutting Outer Web..");
			controller.walkTo(227, 107);
			controller.atWallObject(227, 107);
			controller.sleep(300);
		}
		if (controller.getWallObjectIdAtCoord(227, 107) == -1) {  //this should fix the web break!!
			controller.setStatus("@gre@Walkin..");
			controller.walkTo(227, 106);
			controller.sleep(300);
		}
		controller.walkTo(227, 106);
		controller.sleep(340);
		if (!controller.isDoorOpen(227, 106)) { 
				//&& controller.currentX() < 228 && controller.currentX() > 225 && controller.currentY() == 106) {
				controller.setStatus("@gre@Opening Mage Bank Outer Door..");
				controller.walkTo(227, 106);
				controller.openDoor(227, 106);
				controller.sleep(340);
		}
		controller.walkTo(227, 105);
		controller.walkTo(232, 104);
		controller.walkTo(254, 126);
		controller.walkTo(268, 127);
		controller.sleep(340);
	}
	public void stairToGiants() {

		controller.walkTo(268,127);
	    controller.atObject(268,128);
		controller.sleep(340);
		if(controller.currentX() < 270 && controller.currentX() > 265 && controller.currentY() < 130 && controller.currentY() > 125) {
				controller.setStatus("@gre@Going down stairs..");
				controller.walkTo(268,127);
			    controller.atObject(268,128);
				controller.sleep(600);
		}
		controller.walkTo(272,2972);   //error code this line
		controller.sleep(340);
		if(controller.currentX() > 265 && controller.currentX() < 270 && controller.currentY() > 2958 && controller.currentY() < 2967) {
			controller.setStatus("@gre@Walking..");
			controller.walkTo(272,2972);   //error code this line
			controller.sleep(340);
		}
		//if(controller.currentX() == 272 && controller.currentY() < 2975 && controller.currentY() > 2970) {
			if(controller.getObjectAtCoord(272,2972) == 57) {
				controller.setStatus("@gre@Opening Giants Gate..");
				controller.walkTo(272,2972);
				controller.atObject(272,2972);
				controller.sleep(340);
			}
		//}
		controller.walkTo(278,2970);
		controller.walkTo(281,2970);
		controller.sleep(340);
		//if(controller.currentX() < 285 && controller.currentX() > 279 && controller.currentY() < 2974 && controller.currentY() > 2965) {
			if(controller.getObjectAtCoord(281,2969) == 57) {
				controller.setStatus("@gre@Opening Chaos Dwarf Gate..");
				controller.walkTo(281,2970);
				controller.atObject(281,2969);
				controller.sleep(340);
			}
		//}
		controller.walkTo(283,2969);
		controller.walkTo(281,2962);
		controller.walkTo(274,2953);
		controller.sleep(340);
		//if(controller.currentX() < 277 && controller.currentX() > 271 && controller.currentY() < 2958 && controller.currentY() > 2950) {
			if(controller.getObjectAtCoord(274,2952) == 57) {
				controller.setStatus("@gre@Opening Fire Giant Gate..");
				controller.walkTo(274,2953);
				controller.atObject(274,2952);
				controller.sleep(340);
			}
		//}
		controller.walkTo(272,2953);
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
		JLabel header = new JLabel("Wildy Fire Giant Killer - By Kaila");
		JLabel label1 = new JLabel("Start in Mage bank OR in Giants room");
		JLabel label2 = new JLabel("Sharks IN BANK REQUIRED");
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
			controller.drawString("@red@Wilderness Fire Giants @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
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