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
 * Tav black demons - By Kaila
 * Start in Fally west bank with gear
 * Sharks/ppots/Laws/Airs/Earths IN BANK REQUIRED. super atk, super str pots suggested.
 * 37 Magic Required for  tele, 37 prayer for paralize monster, 70 agility for shortcut.
 * anti dragon shield required. rune squareshield recomended.
 *
 * Author - Kaila
 */
public class K_TavBlackDemonPipe extends IdleScript {	
	
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	boolean d2hWield = false;
	
	int totalDbones = 0;
    int totalRdagger = 0;
    int totalGems = 0;
    int totalLaw = 0;
    int totalNat = 0;
    int totalFire = 0;
    int totalWater = 0;
    int totalAddy = 0;
    int totalLoop = 0;
    int totalTooth = 0;
    int totalLeft = 0;
    int totalSpear = 0;  
    int totalHerb = 0;
    int bankDbones = 0;
    int totalBlood = 0;
    int totalChaos = 0;
    int totalMed = 0;
    int totalDstone = 0;
    int totalRbar = 0;
    int totalRunestuff = 0;
    int totalTrips = 0;
    int totalDeath = 0;
    int totalRchain = 0;
    int totalRmed = 0;
    
    
   // DRAGON_BONES, 396, 93, LAW_RUNE, 40, WATER_RUNE, 31, 526, 527, 1277

   	int[] attackPot = {488,487,486};
   	int[] strPot = {494,493,492};
	int[] loot = { 400,   //rune chain
				   399,   // rune med
					31, 	 //fire rune
					42, 	 // law rune
					41, //chaos rune
					619, //blood rune
					33, //air rune
					40,	 	 // nature rune
					38, //Death Rune
					438, 	 //Grimy ranarr
					439,  	 //Grimy irit
					440,  	 //Grimy ava
					441,	 //Grimy kwu
					442, 	 //Grimy cada
					443, 	 //Grimy dwu
					174,     //Addy bar
					160, 	 //saph
					159, 	 //emerald
					158, 	 //ruby
					157,	 //diamond
					404, 	 //rune kite
					403,	 //rune square
					542,	 //uncut dstone
					523,  	//cut dstone
					795,  	//D med
					405,  	//rune axe
					408,  	//rune bar
					81, 	//rune 2h
					93, 	//rune battle axe
					520, 	//silver cert
					518, 	//coal cert
					526, 	 //tooth half
					527, 	 //loop half
					1277, 	 //shield (left) half
					1092, 	 //rune spear
					795  	//D med
					};

    public boolean isWithinWander(int x, int y) { 
    	return controller.distance(390, 3371, x, y) <= 8;
    }
	
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	
	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (scriptStarted) {
			controller.displayMessage("@red@Taverley Black Demons - By Kaila");
			controller.displayMessage("@red@Start in Fally west with gear on, or in demon room!");
			controller.displayMessage("@red@Sharks, Law, Water, Air IN BANK REQUIRED");
			controller.displayMessage("@red@70 Agility required, for the shortcut!");
			if(controller.isInBank() == true) {
				controller.closeBank();
			}
			if(controller.currentY() < 2800) {
				bank();
				BankToDemons();  
				controller.sleep(1380);
			}
			scriptStart();
		}
		return 1000; //start() must return a int value now. 
	}

	public void scriptStart() {
			while(controller.isRunning()) {

				eat();
				ppotCheck();
				drink();
				pray();

				
				if(controller.getInventoryItemCount(465) > 0 && !controller.isInCombat()) {
					controller.dropItem(controller.getInventoryItemSlotIndex(465));
				}		

				if(controller.getInventoryItemCount() < 30) {
					ppotCheck();
					foodCheck();
			 		boolean lootPickedUp = false; 
				   	for(int lootId : loot) {
				   		int[] coords = controller.getNearestItemById(lootId);
		        		if(coords != null && this.isWithinWander(coords[0], coords[1])) {
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
		    			controller.setStatus("@yel@Attacking Demons");
				    	controller.sleepHandler(98, true);
					   	ORSCharacter npc = controller.getNearestNpcById(290, false);
					   	if(npc != null) {
					    	controller.attackNpc(npc.serverIndex);
					    	controller.sleep(1000);
					    } else {
							controller.sleep(1000);
					    }
				    }
			    	controller.sleep(1380);
			    	
			    	
			} else if(controller.getInventoryItemCount() == 30) {
				ppotCheck();
				foodCheck();
				while(controller.isInCombat()) {
					controller.setStatus("@red@Leaving combat..");
					controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
					controller.sleep(250);
				}
				
				controller.setStatus("@red@Eating Food to Loot..");
				
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
					controller.setStatus("@yel@No food, Banking..");
					DemonsToBank();
					bank();
					BankToDemons();
					controller.sleep(618);
					}	
				}
			}
		}
	

	
	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(640);

		if (controller.isInBank()) {
			
			totalRunestuff = totalRunestuff 
					+ controller.getInventoryItemCount(404) //kite
					+ controller.getInventoryItemCount(403) //sq
					+ controller.getInventoryItemCount(405) //axe
					+ controller.getInventoryItemCount(81) //2h
					+ controller.getInventoryItemCount(93); //bAxe
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
			totalDeath = totalDeath + controller.getInventoryItemCount(38);
			totalBlood = totalBlood + controller.getInventoryItemCount(619);
			totalRbar = totalRbar + controller.getInventoryItemCount(408);
			totalLoop = totalLoop + controller.getInventoryItemCount(527);
			totalTooth = totalTooth + controller.getInventoryItemCount(526);
			totalDstone = totalDstone + controller.getInventoryItemCount(523);
			totalLeft = totalLeft + controller.getInventoryItemCount(1277);
			totalSpear = totalSpear + controller.getInventoryItemCount(1092);
			totalRchain = totalMed + controller.getInventoryItemCount(400);
			totalRmed = totalMed + controller.getInventoryItemCount(399);
			totalMed = totalMed + controller.getInventoryItemCount(795);
			
			//ppotCount() = (controller.getInventoryItemCount(483) + controller.getInventoryItemCount(483) +controller.getInventoryItemCount(483));
			for (int itemId : controller.getInventoryItemIds()) {
				if (itemId != 486 && itemId != 487 && itemId != 488 && itemId != 492 && itemId != 493 && itemId != 494 && itemId != 546 && itemId != 420 && itemId != 485 && itemId != 484 && itemId != 483 && itemId != 1346 ) {
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				}
			}
			controller.sleep(1280);   // increased sleep here to prevent double banking
			
			if(controller.getInventoryItemCount(420) < 1) {  //antidragon shield
				controller.withdrawItem(420, 1);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(33) < 3) {  //3 air
				controller.withdrawItem(33, 3 - controller.getInventoryItemCount(33));
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(42) < 1) {  //1 law
				controller.withdrawItem(42, 1);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(32) < 1) {  //1 water
				controller.withdrawItem(32, 1);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(attackPot[0]) < 1 && controller.getInventoryItemCount(attackPot[1]) < 1 && controller.getInventoryItemCount(attackPot[2]) < 1 ) {  //withdraw 10 shark if needed
				controller.withdrawItem(attackPot[2], 1);
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(strPot[0]) < 1 && controller.getInventoryItemCount(strPot[1]) < 1 && controller.getInventoryItemCount(strPot[2]) < 1 ) {  //withdraw 10 shark if needed
				controller.withdrawItem(strPot[2], 1);
				controller.sleep(340);
			}

			if(controller.getInventoryItemCount(483) < 17) {  //withdraw 17 ppot
				controller.withdrawItem(483, 17 - (controller.getInventoryItemCount(483) + controller.getInventoryItemCount(484) + controller.getInventoryItemCount(485)));  //minus ppot count
				controller.sleep(340);
			}
			if(controller.getInventoryItemCount(546) < 2) {  //withdraw 2 shark
				controller.withdrawItem(546, 2 - controller.getInventoryItemCount(546));
				controller.sleep(340);
			}
			bankDbones = controller.getBankItemCount(814);
			if(controller.getBankItemCount(546) == 0 || controller.getBankItemCount(33) == 0 || controller.getBankItemCount(42) == 0 || controller.getBankItemCount(32) == 0) {
				controller.setStatus("@red@NO Sharks/Laws/Airs in the bank, Logging Out!.");
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
	
	
	
	
	
	
	
	public void pray() {
		if(!controller.isPrayerOn(controller.getPrayerId("Paralyze Monster")) && controller.currentY() > 3000);
			controller.enablePrayer(controller.getPrayerId("Paralyze Monster"));
	}
	public void drink() {
	   	if(controller.getCurrentStat(controller.getStatId("Prayer")) < (controller.getBaseStat(controller.getStatId("Prayer")) - 31)) {
	   		if(controller.getInventoryItemCount(485) > 0 || controller.getInventoryItemCount(484) > 0 || controller.getInventoryItemCount(483) > 0 ) {
	   			drinkPot();
			} else {
				controller.sleep(308);
				DemonsToBank();
				bank();
				BankToDemons();
			}
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
				DemonsToBank();
				bank();
				BankToDemons();
			}
		}
	}
	public void ppotCheck() {
		if(controller.getInventoryItemCount(483) == 0) {
			controller.setStatus("@yel@No Ppots, Banking..");
			DemonsToBank();
			bank();
			BankToDemons();
			controller.sleep(618);
		}
	}
	public void foodCheck() {
		if(controller.getInventoryItemCount(546) == 0) {
			controller.setStatus("@yel@No food, Banking..");
			DemonsToBank();
			bank();
			BankToDemons();
			controller.sleep(618);
		}
	}
	public void DemonsToBank() {
    	controller.setStatus("@gre@Walking to Bank..");
    	while(controller.currentY() > 3000) {
		controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
		controller.sleep(308);
    	}
		totalTrips = totalTrips + 1;
		if(controller.isPrayerOn(controller.getPrayerId("Paralyze Monster"))); {  // && controller.currentY() < 3000
			controller.disablePrayer(controller.getPrayerId("Paralyze Monster"));
		}
		controller.sleep(308);
		controller.walkTo(327,552);
		controller.sleep(308);
    	controller.setStatus("@gre@Done Walking..");
	}  
	
    public void BankToDemons() {	
    	controller.setStatus("@gre@Walking to Black Demons..");
		controller.walkTo(327, 552);
		controller.walkTo(324, 549);
		controller.walkTo(324, 539);
		controller.walkTo(324, 530);
		controller.walkTo(317, 523);
		controller.walkTo(317, 516);
		controller.walkTo(327, 506);
		controller.walkTo(337, 496);
		controller.walkTo(337, 492);
		controller.walkTo(341, 488);
		while(controller.currentX() == 341 && controller.currentY() < 489 && controller.currentY() > 486) {
			controller.atObject(341,487);   //gate wont break if someone else opens it
			controller.sleep(640);
		}
		controller.walkTo(342,493);
		controller.walkTo(352,503);
		controller.walkTo(362,513);
		controller.walkTo(367,514);
		controller.walkTo(374,521);
		controller.walkTo(376,521);
		controller.equipItem(controller.getInventoryItemSlotIndex(420));
		controller.sleep(320);
		controller.atObject(376,520);
		controller.sleep(640);
		controller.walkTo(375,3352);
		if(!controller.isItemIdEquipped(420)) {
			controller.setStatus("@red@Not Wielding Dragonfire Shield!.");
			controller.setAutoLogin(false);
			controller.logout();
			if(!controller.isLoggedIn()) {
				controller.stop();
				return;
			}
		}
		controller.atObject(374,3352);
		controller.sleep(640);
		controller.walkTo(372,3364);
		controller.walkTo(377,3369);
		if (d2hWield == true) {
			controller.equipItem(controller.getInventoryItemSlotIndex(1346));
		}
		controller.enablePrayer(controller.getPrayerId("Paralyze Monster"));
		controller.sleep(320);
		controller.walkTo(380,3372);
    	controller.setStatus("@gre@Done Walking..");
		eat();
		ppotCheck();
		drink();
		pray();
    	
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
	
	public void drinkPot() {
		ppotCheck();
		while(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
			controller.sleep(250);
		}
		if(controller.getInventoryItemCount(485) > 0) {
			controller.itemCommand(485);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(484) > 0) {
			controller.itemCommand(484);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(483) > 0) {
			controller.itemCommand(483);
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
	public void setValuesFromGUI(JCheckBox d2hCheckbox) {
		if(d2hCheckbox.isSelected()) {
			d2hWield = true;
		}
	}
	public void setupGUI() {
		JLabel header = new JLabel("Taverley Black Demon (Pipe) - By Kaila");
		JLabel label1 = new JLabel("Start in Fally west with gear on, or in Demon room!");
		JLabel label2 = new JLabel("Sharks, Law, Water, Air IN BANK required");
		JLabel label3 = new JLabel("70 Agility required, for the shortcut!");
		JLabel label4 = new JLabel("Bot will attempt to wield dragonfire shield");
		JLabel label5 = new JLabel("When walking through Blue Dragon Room");
		JCheckBox d2hCheckbox = new JCheckBox("Check This if using D2H");
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setValuesFromGUI(d2hCheckbox);
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
		scriptFrame.add(d2hCheckbox);
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
    		int RuneSuccessPerHr = 0;
    		int GemsSuccessPerHr = 0;
    		int FireSuccessPerHr = 0;
    		int LawSuccessPerHr = 0;
    		int NatSuccessPerHr = 0;
    		int ChaosSuccessPerHr = 0;
    		int DeathSuccessPerHr = 0;
    		int BloodSuccessPerHr = 0;
    		int RbarSuccessPerHr = 0;
    		int RchainSuccessPerHr = 0;
    		int RmedSuccessPerHr = 0;
    	    int HerbSuccessPerHr = 0;
    	    int TripSuccessPerHr = 0;
    	    
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		RuneSuccessPerHr = (int)(totalRunestuff * scale);
	    		GemsSuccessPerHr = (int)(totalGems * scale);
	    		FireSuccessPerHr = (int)(totalFire * scale);
	    		LawSuccessPerHr = (int)(totalLaw * scale);
	    		NatSuccessPerHr = (int)(totalNat * scale);
	    		ChaosSuccessPerHr = (int)(totalChaos * scale);
	    		DeathSuccessPerHr = (int)(totalDeath * scale);
	    		BloodSuccessPerHr = (int)(totalBlood * scale);
	    		RbarSuccessPerHr = (int)(totalRbar * scale);
	    		RchainSuccessPerHr = (int)(totalRchain * scale);
	    		RmedSuccessPerHr = (int)(totalRmed * scale);
	    		HerbSuccessPerHr = (int)(totalHerb * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    		
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Tavelry Black Demons @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Laws: @gre@" + String.valueOf(this.totalLaw) + "@yel@ (@whi@" + String.format("%,d", LawSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Natures: @gre@" + String.valueOf(this.totalNat) + "@yel@ (@whi@" + String.format("%,d", NatSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Fires: @gre@" + String.valueOf(this.totalFire) + "@yel@ (@whi@" + String.format("%,d", FireSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Chaos: @gre@" + String.valueOf(this.totalChaos) + "@yel@ (@whi@" + String.format("%,d", ChaosSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Deaths: @gre@" + String.valueOf(this.totalDeath) + "@yel@ (@whi@" + String.format("%,d", DeathSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 118, 0xFFFFFF, 1);
			controller.drawString("@whi@Bloods: @gre@" + String.valueOf(this.totalBlood) + "@yel@ (@whi@" + String.format("%,d", BloodSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 132, 0xFFFFFF, 1);
			controller.drawString("@whi@Rune Chain: @gre@" + String.valueOf(this.totalRchain) + "@yel@ (@whi@" + String.format("%,d", RchainSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 146, 0xFFFFFF, 1);
			controller.drawString("@whi@Rune Med: @gre@" + String.valueOf(this.totalRmed) + "@yel@ (@whi@" + String.format("%,d", RmedSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 160, 0xFFFFFF, 1);
			controller.drawString("@whi@Rune Bars: @gre@" + String.valueOf(this.totalRbar) + "@yel@ (@whi@" + String.format("%,d", RbarSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 174, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Herbs: @gre@" + String.valueOf(this.totalHerb) + "@yel@ (@whi@" + String.format("%,d", HerbSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 188, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Gems: @gre@" + String.valueOf(this.totalGems) + "@yel@ (@whi@" + String.format("%,d", GemsSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 202, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Rune Items: @gre@" + String.valueOf(this.totalRunestuff) + "@yel@ (@whi@" + String.format("%,d", RuneSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 216, 0xFFFFFF, 1);
			controller.drawString("@whi@Tooth: @gre@" + String.valueOf(this.totalTooth) + "@yel@ / @whi@Loop: @gre@" + String.valueOf(this.totalLoop), 350, 230, 0xFFFFFF, 1);
			controller.drawString("@whi@Dstone: @gre@" + String.valueOf(this.totalDstone) + "@yel@ / @whi@Rune Spear: @gre@" + String.valueOf(this.totalSpear), 350, 244, 0xFFFFFF, 1);
			controller.drawString("@whi@D Med: @gre@" + String.valueOf(this.totalMed) + "@yel@ / @whi@Left Half: @gre@" + String.valueOf(this.totalLeft), 350, 258, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 272, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 286, 0xFFFFFF, 1);
		}  //add air runes, 
	}
}
