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
 * Wildy Fire Giant Killer - By Kaila.
 *
 *      Start in Edge bank with Armor.
 *      Uses Coleslaw agility pipe shortcut.
 *      70 Agility required, for the shortcut!
 *      Sharks/ppots/Laws/Airs/Earths IN BANK REQUIRED.
 *      31 Magic Required for escape tele.
 *      Bot will attempt to wield dragonfire shield when in blue dragon room.
 *
 * Author - Kaila
 */
public class K_TavBlackDragonPipe extends IdleScript {

	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;

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
    int totalRlong = 0;
    int totalSpear = 0;
    int totalHerb = 0;
    int bankDbones = 0;
    int totalBlood = 0;
    int totalChaos = 0;
    int totalMed = 0;
    int totalDstone = 0;
    int totalRbar = 0;
    int totalTrips = 0;
    int totalDeath = 0;
    int totalRchain = 0;
    int totalRmed = 0;


   // DRAGON_BONES, 396, 93, LAW_RUNE, 40, WATER_RUNE, 31, 526, 527, 1277

   	int[] attackPot = {488,487,486};
   	int[] strPot = {494,493,492};
   	int[] antiPot = {571,570,569};
	int[] loot = {
			814,     //D Bones
			75,     //rune long
			120, 	//addy plate body

			619,			//blood rune

			438, 	 //Grimy ranarr
			439,  	 //Grimy irit
			440,  	 //Grimy ava
			441,	 //Grimy kwu
			442, 	 //Grimy cada
			443, 	 //Grimy dwu

			405,  	//rune axe
			81, 	//rune 2h
			93, 	//rune battle axe

			31, 	 //fire rune
			33,      // air rune
			38,      //Death Rune
			619,     //blood rune
			40,	 	 // nature rune
			42, 	 // law rune

			11,      //bronze arrows

			408,  	//rune bar
			520, 	//silver cert
			518, 	//coal cert

			159, 	 //emerald
			158, 	 //ruby
			157,	 //diamond
			523,     //dragonstone!

			526, 	 //tooth half
			527, 	 //loop half
			1092, 	 //rune spear
			1277, 	 //shield (left) half
			795,  	//D med
			};


	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;

    public boolean isWithinWander(int x, int y) {
    	return controller.distance(408,3337, x, y) <= 22;
    }

	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (scriptStarted) {
			controller.displayMessage("@red@Tavelry Black Dragons - By Kaila");
			controller.displayMessage("@red@Start in Fally west with gear on, or in demon room!");
			controller.displayMessage("@red@Sharks, Law, Water, Air IN BANK REQUIRED");
			controller.displayMessage("@red@70 Agility required, for the shortcut!");
			if(controller.isInBank() == true) {
				controller.closeBank();
			}
			if(controller.currentY() < 2800) {
				bank();
				BankToDragons();
				controller.sleep(1380);
			}
			scriptStart();
		}
		return 1000; //start() must return a int value now.
	}
















	public void scriptStart() {
		while(controller.isRunning()) {

			ppotCheck();
			drink();
			pray();
			foodCheck();
			eat();

			if(controller.getInventoryItemCount(465) > 0 && !controller.isInCombat()) {
				controller.dropItem(controller.getInventoryItemSlotIndex(465));
			}

			if(controller.getInventoryItemCount() < 30) {

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
					controller.setStatus("@yel@Attacking Dragons");
					ORSCharacter npc = controller.getNearestNpcById(291, false);
					if(npc != null) {
						controller.attackNpc(npc.serverIndex);
						eat();
						controller.sleep(100);
						eat();
					} else {
						controller.walkTo(408,3336);
						controller.sleep(340);
					}
				}
				controller.sleep(800);
			}
			if(controller.getInventoryItemCount() == 30) {
				ppotCheck();
				leaveCombat();
				if(controller.getInventoryItemCount(465) > 0 && !controller.isInCombat()) {
					controller.setStatus("@red@Dropping Vial to Loot..");
					controller.dropItem(controller.getInventoryItemSlotIndex(465));
					controller.sleep(340);
				}
				for(int id : controller.getFoodIds()) {
					if(controller.getInventoryItemCount(id) > 0 && controller.getInventoryItemCount() == 30) {
						controller.setStatus("@red@Eating Food to Loot..");
						controller.itemCommand(id);
						controller.sleep(700);
					}
				}
			}
		}
	}



















	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(1200);

		if(controller.isInBank()){

			totalDbones = totalDbones + controller.getInventoryItemCount(814);
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
			totalDeath = totalDeath + controller.getInventoryItemCount(38);
			totalBlood = totalBlood + controller.getInventoryItemCount(619);
			totalLoop = totalLoop + controller.getInventoryItemCount(527);
			totalTooth = totalTooth + controller.getInventoryItemCount(526);
			totalDstone = totalDstone + controller.getInventoryItemCount(523);
			totalLeft = totalLeft + controller.getInventoryItemCount(1277);
			totalMed = totalMed + controller.getInventoryItemCount(795);
			totalSpear = totalSpear + controller.getInventoryItemCount(1092);
			totalAddy = totalAddy + controller.getInventoryItemCount(120);
			totalRlong = totalRlong + controller.getInventoryItemCount(75);
			//ppotCount() = (controller.getInventoryItemCount(483) + controller.getInventoryItemCount(483) +controller.getInventoryItemCount(483));
			for (int itemId : controller.getInventoryItemIds()) {
				if (itemId != 486 && itemId != 487 && itemId != 488 && itemId != 492 && itemId != 493 && itemId != 494 && itemId != 546 && itemId != 420 && itemId != 485 && itemId != 484 && itemId != 483 && itemId != 571 && itemId != 570 && itemId != 569 ) {
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				}
			}
			controller.sleep(1400);      //Important, leave in

			if(controller.getInventoryItemCount(33) < 18) {  //6 air
				controller.withdrawItem(33, 18 - controller.getInventoryItemCount(33));
				controller.sleep(1000);
			}
			if(controller.getInventoryItemCount(42) < 6) {  //2 law
				controller.withdrawItem(42, 6 - controller.getInventoryItemCount(42));
				controller.sleep(1000);
			}
			if(controller.getInventoryItemCount(32) < 6) {  //2 water
				controller.withdrawItem(32, 6 - controller.getInventoryItemCount(32));
				controller.sleep(1000);
			}
			controller.sleep(640);  //leave in
			if(controller.getInventoryItemCount(attackPot[0]) < 1 && controller.getInventoryItemCount(attackPot[1]) < 1 && controller.getInventoryItemCount(attackPot[2]) < 1 ) {  //withdraw 10 shark if needed
				controller.withdrawItem(attackPot[2], 1);
				controller.sleep(640);
			}
			if(controller.getInventoryItemCount(antiPot[0]) < 1 && controller.getInventoryItemCount(antiPot[1]) < 1 && controller.getInventoryItemCount(antiPot[2]) < 1 ) {  //withdraw 10 shark if needed
				if(controller.getBankItemCount(antiPot[0]) > 0) {
					controller.withdrawItem(antiPot[0], 1);
					controller.sleep(640);
				}
			}
			if(controller.getInventoryItemCount(strPot[0]) < 1 && controller.getInventoryItemCount(strPot[1]) < 1 && controller.getInventoryItemCount(strPot[2]) < 1 ) {  //withdraw 10 shark if needed
				controller.withdrawItem(strPot[2], 1);
				controller.sleep(640);
			}
			if(controller.getInventoryItemCount(antiPot[0]) < 1 && controller.getInventoryItemCount(antiPot[1]) < 1 && controller.getInventoryItemCount(antiPot[2]) < 1 ) {
				if(controller.getBankItemCount(antiPot[1]) > 0) {
					controller.withdrawItem(antiPot[1], 1);
					controller.sleep(640);
				}
			}
			if(controller.getInventoryItemCount(483) < 6) {  //withdraw 5 ppot
				controller.withdrawItem(483, 6 - (controller.getInventoryItemCount(483) + controller.getInventoryItemCount(484) + controller.getInventoryItemCount(485)));  //minus ppot count
				controller.sleep(640);
			}
			if(controller.getInventoryItemCount(antiPot[0]) < 1 && controller.getInventoryItemCount(antiPot[1]) < 1 && controller.getInventoryItemCount(antiPot[2]) < 1 ) {
				if(controller.getBankItemCount(antiPot[2]) > 0) {
					controller.withdrawItem(antiPot[2], 1);
					controller.sleep(640);
				}
			}
			if(controller.getInventoryItemCount(546) < 4) {  //withdraw 2 shark
				controller.withdrawItem(546, 4 - controller.getInventoryItemCount(546));
				controller.sleep(640);
			}
			if(controller.getInventoryItemCount(33) < 18) {  //6 air
				controller.withdrawItem(33, 18 - controller.getInventoryItemCount(33));
				controller.sleep(1000);
			}
			if(controller.getInventoryItemCount(42) < 6) {  //2 law
				controller.withdrawItem(42, 6 - controller.getInventoryItemCount(42));
				controller.sleep(1000);
			}
			if(controller.getInventoryItemCount(32) < 6) {  //2 water
				controller.withdrawItem(32, 6 - controller.getInventoryItemCount(32));
				controller.sleep(1000);
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
			if(!controller.isItemIdEquipped(420)) {
				controller.setStatus("@red@Not Wielding Dragonfire Shield!.");
				controller.withdrawItem(420, 1);
				controller.closeBank();
				controller.equipItem(controller.getInventoryItemSlotIndex(420));
				controller.sleep(1320);
			}
			controller.closeBank();
			controller.sleep(1000);
		}
		airCheck();
		waterCheck();
		lawCheck();
	}
	public void lawCheck() {
		if(controller.getInventoryItemCount(42) < 6) {  //law
			controller.openBank();
			controller.sleep(1200);
			controller.withdrawItem(42, 6 - controller.getInventoryItemCount(42));
			controller.sleep(1000);
			controller.closeBank();
			controller.sleep(1000);
		}
	}
	public void waterCheck() {
		if(controller.getInventoryItemCount(32) < 6) {  //2 water
			controller.openBank();
			controller.sleep(1200);
			controller.withdrawItem(32, 6 - controller.getInventoryItemCount(32));
			controller.sleep(1000);
			controller.closeBank();
			controller.sleep(1000);
		}
	}
	public void airCheck() {
		if(controller.getInventoryItemCount(33) < 18) {  //6 air
			controller.openBank();
			controller.sleep(1200);
			controller.withdrawItem(33, 18 - controller.getInventoryItemCount(33));
			controller.sleep(1000);
			controller.closeBank();
			controller.sleep(1000);
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
				dragonEscape();
				DragonsToBank();
				bank();
				BankToDragons();
			}
	   	}
	}
	public void eat() {

		int eatLvl = controller.getBaseStat(controller.getStatId("Hits")) - 20;


		if(controller.getCurrentStat(controller.getStatId("Hits")) < eatLvl) {

			leaveCombat();
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
				dragonEscape();
				DragonsToBank();
				bank();
				BankToDragons();
			}
		}
	}





	public void dragonEscape() {
		controller.setStatus("We've ran out of Food/PPots! @gre@Going to safe zone.");
		controller.walkTo(408, 3340);
		controller.walkTo(408, 3348);
		controller.sleep(1000);
	}
	public void BankToDragons() {
		controller.setStatus("@gre@Walking to Black Dragons..");
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
		tavGateEastToWest();
		controller.setStatus("@gre@Walking to Tav Dungeon Ladder..");
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
		controller.equipItem(controller.getInventoryItemSlotIndex(404));
		controller.enablePrayer(controller.getPrayerId("Paralyze Monster"));
		controller.sleep(320);
		controller.walkTo(380,3372);
		eat();
		ppotCheck();
		drink();
		pray();
		controller.walkTo(386,3371);
		controller.walkTo(388,3360);
		controller.walkTo(397,3347);
		controller.walkTo(397,3343);
		controller.walkTo(403,3346);
		controller.walkTo(408,3344);
		antiBoost();
		controller.walkTo(409,3338);
		eat();
		ppotCheck();
		drink();
		pray();
		controller.setStatus("@gre@Done Walking..");

	}
	public void ppotCheck() {
		if(controller.getInventoryItemCount(483) == 0) {
			controller.setStatus("@yel@No Ppots, Banking..");
			dragonEscape();
			DragonsToBank();
			bank();
			BankToDragons();
			controller.sleep(618);
		}
	}
	public void foodCheck() {
		if(controller.getInventoryItemCount(546) == 0) {
			controller.setStatus("@yel@No food, Banking..");
			dragonEscape();
			DragonsToBank();
			bank();
			BankToDragons();
			controller.sleep(618);
		}
	}
	public void attackBoost() {
		leaveCombat();
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
		}
	}

	public void strengthBoost() {
		leaveCombat();
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
		}
	}

	public void drinkPot() {
		ppotCheck();
		leaveCombat();
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
		}
	}

	public void antiBoost() {
		leaveCombat();
		if(controller.getInventoryItemCount(antiPot[0]) > 0) {
			controller.itemCommand(antiPot[0]);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(antiPot[1]) > 0) {
			controller.itemCommand(antiPot[1]);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(antiPot[2]) > 0) {
			controller.itemCommand(antiPot[2]);
			controller.sleep(320);
		}
	}
	public void DragonsToBank() {
		controller.setStatus("@gre@Going to Bank. Casting 1st teleport.");
		controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
		controller.sleep(1000);
		for (int i = 1; i <= 15; i++) {
			if (controller.currentY() > 3000) {
				controller.setStatus("@gre@Teleport unsuccessful, Casting teleports.");
				controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
				controller.sleep(1000);
			} else {
				controller.setStatus("@red@Done Teleporting..");
				break;
			}
			controller.sleep(10);
		}
		controller.sleep(308);
		controller.walkTo(327,552);
		controller.sleep(308);
		controller.setStatus("@gre@Done Walking..");
	}
	public void leaveCombat() {
		for (int i = 1; i <= 15; i++) {
			if (controller.isInCombat()) {
				controller.setStatus("@red@Leaving combat..");
				controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
				controller.sleep(600);
			} else {
				controller.setStatus("@red@Done Leaving combat..");
				break;
			}
			controller.sleep(10);
		}
	}
	public void tavGateEastToWest() {
		for (int i = 1; i <= 15; i++) {
			if (controller.currentX() == 341 && controller.currentY() < 489 && controller.currentY() > 486) {
				controller.setStatus("@red@Crossing Tav Gate..");
				controller.atObject(341, 487);   //gate wont break if someone else opens it
				controller.sleep(800);
			} else {
				controller.setStatus("@red@Done Crossing Tav Gate..");
				break;
			}
			controller.sleep(10);
		}
	}












	//GUI stuff below (icky)



	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	public void setupGUI() {
		JLabel header = new JLabel("Tavelry Black Dragons (Pipe) - By Kaila");
		JLabel label1 = new JLabel("Start in Fally west with gear on, or in Demon room!");
		JLabel label2 = new JLabel("Sharks, Law, Water, Air IN BANK required");
		JLabel label3 = new JLabel("70 Agility required, for the shortcut!");
		JLabel label4 = new JLabel("Bot will attempt to wield dragonfire shield");
		JLabel label5 = new JLabel("When walking through Blue Dragon Room");
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
    		int DbonesSuccessPerHr = 0;
    		int RdaggerSuccessPerHr = 0;
    		int GemsSuccessPerHr = 0;
    		int FireSuccessPerHr = 0;
    		int LawSuccessPerHr = 0;
    		int AddySuccessPerHr = 0;
    	    int HerbSuccessPerHr = 0;
    	    int DeathSuccessPerHr = 0;
    	    int BloodSuccessPerHr = 0;
    		int TripSuccessPerHr = 0;
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		DbonesSuccessPerHr = (int)(totalDbones * scale);
	    		RdaggerSuccessPerHr = (int)(totalRlong * scale);
	    		GemsSuccessPerHr = (int)(totalGems * scale);
	    		FireSuccessPerHr = (int)(totalFire * scale);
	    		LawSuccessPerHr = (int)(totalLaw * scale);
	    		AddySuccessPerHr = (int)(totalAddy * scale);
	    		HerbSuccessPerHr = (int)(totalHerb * scale);
	    		DeathSuccessPerHr = (int)(totalDeath * scale);
	    		BloodSuccessPerHr = (int)(totalBlood * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Tav Black Dragons @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Total D.Bones: @gre@" + String.valueOf(this.bankDbones), 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Banked D.Bones: @gre@" + String.valueOf(this.totalDbones) + "@yel@ (@whi@" + String.format("%,d", DbonesSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);  //fix y cords
			controller.drawString("@whi@Rune Longs: @gre@" + String.valueOf(this.totalRlong) + "@yel@ (@whi@" + String.format("%,d", RdaggerSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Addy Plate Body: @gre@" + String.valueOf(this.totalAddy) + "@yel@ (@whi@" + String.format("%,d", AddySuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Fires: @gre@" + String.valueOf(this.totalFire) + "@yel@ (@whi@" + String.format("%,d", FireSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 118, 0xFFFFFF, 1);
			controller.drawString("@whi@Laws: @gre@" + String.valueOf(this.totalLaw) + "@yel@ (@whi@" + String.format("%,d", LawSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 132, 0xFFFFFF, 1);
			controller.drawString("@whi@Deaths: @gre@" + String.valueOf(this.totalDeath) + "@yel@ (@whi@" + String.format("%,d", DeathSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 146, 0xFFFFFF, 1);
			controller.drawString("@whi@Bloods: @gre@" + String.valueOf(this.totalBlood) + "@yel@ (@whi@" + String.format("%,d", BloodSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 160, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Herbs: @gre@" + String.valueOf(this.totalHerb) + "@yel@ (@whi@" + String.format("%,d", HerbSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 174, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Gems: @gre@" + String.valueOf(this.totalGems) + "@yel@ (@whi@" + String.format("%,d", GemsSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 188, 0xFFFFFF, 1);
			controller.drawString("@whi@Tooth: @gre@" + String.valueOf(this.totalTooth) + "@yel@ / @whi@Loop: @gre@" + String.valueOf(this.totalLoop), 350, 202, 0xFFFFFF, 1);
			controller.drawString("@whi@Dstone: @gre@" + String.valueOf(this.totalDstone) + "@yel@ / @whi@Rune Spear: @gre@" + String.valueOf(this.totalSpear), 350, 216, 0xFFFFFF, 1);
			controller.drawString("@whi@D Med: @gre@" + String.valueOf(this.totalMed) + "@yel@ / @whi@Left Half: @gre@" + String.valueOf(this.totalLeft), 350, 230, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 244, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 258, 0xFFFFFF, 1);
		}
	}
}

