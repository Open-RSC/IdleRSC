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
 * Paladin Tower Thiever - By Kaila
 * Start in Ardy South Bank OR in Paladin Tower
 * Sharks in bank REQUIRED, can be changed in script
 * Switching to Defensive combat mode is ideal.
 * Low Atk/Str and Higher Def is more Efficient
 * Ensure to never wield weapons when Thieving.
 *
 * ~300k per hr+ xp per hr possible!
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
    int foodInBank = 0;
    int totalTrips = 0;
	int invCoins = 0;
	int invChaos = 0;
	int startCoins;
	int startChaos;
	int foodId = -1;

	int[] foodIds = { 546, 370, 367, 373 }; //cooked shark, swordfish, tuna, lobster
	int[] loot = {  10,		//coins
					41,		//chaos runes

					526, 	 //tooth half
					527, 	 //loop half
					1277, 	 //shield (left) half
					1092, 	 //rune spear
					160, 	 //saph
					159, 	 //emerald
					158, 	 //ruby
					157,	 //diamond

					438, 	 //Grimy ranarr
					439,  	 //Grimy irit
					440,  	 //Grimy ava
					441,	 //Grimy kwu
					442, 	 //Grimy cada
					443, 	 //Grimy dwu

					619, 	//blood rune
					32,		//water rune
					//170,    //iron bar
					171,    //steel bar
					173,    //mithril bar

					400,	//rune chain
					402,	//rune legs
					404,	//rune kite
					112,	//rune helm
					315,	//defense amulet
					};

	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;

    public void startSequence(){
        controller.displayMessage("@ran@Paladin Tower - By Kaila");
        controller.displayMessage("@gre@Beginning Startup Sequence");
        if(controller.isInBank() == true) {
            controller.closeBank();
        }
        if(controller.currentY() < 620 && controller.currentY() > 608 && controller.currentX() > 550 && controller.currentX() < 555) {  //inside bank
            bank();
            BankToPaladins();
            controller.sleep(1380);
        }
        if(controller.currentY() > 1542 && controller.currentY() < 1548 && controller.currentX() > 607 && controller.currentX() < 614) {  //inside paladin antichamber
            controller.atWallObject2(609,1548);     //locked door
            controller.sleep(640);
            while(controller.isBatching()) controller.sleep(1000);
            controller.sleep(640);
            controller.walkTo(610,1549);
            controller.sleep(640);
        }
        if(controller.currentY() < 650 && controller.currentY() > 550 && controller.currentX() < 540 && controller.currentX() > 500) {  //in witchhaven
            witchhavenToBank();
            bank();
            BankToPaladins();
            controller.sleep(1380);
        }
        if(controller.currentY() < 650 && controller.currentY() > 550 && controller.currentX() > 568 && controller.currentX() < 620) {   //on path
            pathToBank();
            bank();
            BankToPaladins();
            controller.sleep(1380);
        }
        if(controller.currentY() < 2496 && controller.currentY() > 2486 && controller.currentX() < 614 && controller.currentX() > 607) {  //Upstairs
            TreasureRoomToBank();
            bank();
            BankToPaladins();
            controller.sleep(1380);
        }
    }
	public int start(String parameters[]) {
        if (parameters.length > 0 && !parameters[0].equals("")) {
            if (parameters[0].toLowerCase().startsWith("autostart")) {
                controller.displayMessage("Got Autostart", 0);
                System.out.println("Got Autostart, Using Sharks");
                foodId = 546;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("shark")) {
                controller.displayMessage("Got param " + parameters[0] + ". Using Sharks!", 0);
                System.out.println("Got param" + parameters[0] + ", Using Sharks");
                foodId = 546;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("lobster")) {
                controller.displayMessage("Got param " + parameters[0] + ". Using Lobsters!", 0);
                System.out.println("Got param" + parameters[0] + ", Using Lobsters!");
                foodId = 373;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("swordfish")) {
                controller.displayMessage("Got param " + parameters[0] + ". Using Swordfish!", 0);
                System.out.println("Got param" + parameters[0] + ", Using Swordfish!");
                foodId = 370;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("tuna")) {
                controller.displayMessage("Got param " + parameters[0] + ". Using Tuna!", 0);
                System.out.println("Got param" + parameters[0] + ", Using Tuna!");
                foodId = 367;
                parseVariables();
                startSequence();
                scriptStart();
            }
        }
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (scriptStarted) {
            startSequence();
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
				invCoins = controller.getInventoryItemCount(10);
				invChaos = controller.getInventoryItemCount(41);
				controller.sleep(800);
			}
			if(controller.getInventoryItemCount(foodId) > 0 && controller.currentY() > 1547 && controller.currentY() < 1552 ) {

				if(!controller.isInCombat()) {
					controller.setStatus("@yel@Thieving Paladins");
					ORSCharacter npc = controller.getNearestNpcById(323, false);
					if(npc != null) {
						controller.thieveNpc(npc.serverIndex);
						controller.sleep(20); //this sleep time is important
					} else {
						invCoins = controller.getInventoryItemCount(10);
						invChaos = controller.getInventoryItemCount(41);
						controller.sleep(10); //this sleep time is important
					}
				}
				for(int lootId : loot) {
					int[] coords = controller.getNearestItemById(lootId);
					if(coords != null) {      //Loot
						controller.setStatus("@yel@Looting..");
						controller.pickupItem(coords[0], coords[1], lootId, true, true);
						controller.sleep(618); //ignore this sleep time
					} else {
						controller.sleep(5); //this sleep time is important (total of all 3 sleep times should be about 200-300ms to prevent high cpu usage)
					}
				}
			}
			if(controller.getInventoryItemCount(foodId) == 0) {   //bank if no food-
				controller.setStatus("@yel@Banking..");
                goUpPaladinsLadder();
                TreasureRoomToBank();
				bank();
				BankToPaladins();
				controller.sleep(618);
			}
			if(controller.getInventoryItemCount() == 30) {
				leaveCombat();
				controller.setStatus("@red@Eating Food to Loot..");
				if(controller.getInventoryItemCount(foodId) > 0) {
					controller.itemCommand(foodId);
					controller.sleep(700);
				} else {
                    controller.setStatus("@yel@Banking..");
                    goUpPaladinsLadder();
                    TreasureRoomToBank();
                    bank();
                    BankToPaladins();
                    controller.sleep(618);
                }
			}
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
                goUpPaladinsLadder();
                TreasureRoomToBank();
				bank();
				BankToPaladins();
			}
		}
	}

	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(1240); //lower?

		if(controller.isInBank()){

			totalCoins = totalCoins + controller.getInventoryItemCount(10);
			totalChaos = totalChaos + controller.getInventoryItemCount(41);
			totalShark = totalShark + controller.getInventoryItemCount(545);
			totalAda = totalAda + controller.getInventoryItemCount(154);
			totalSap = totalSap + controller.getInventoryItemCount(160);
			totalScim = totalScim + controller.getInventoryItemCount(427);
			coinsInBank = (controller.getBankItemCount(10)/1000000);
			chaosInBank = controller.getBankItemCount(41);
			foodInBank = controller.getBankItemCount(foodId);

			for (int itemId : controller.getInventoryItemIds()) {                                                            //change 546(shark) to desired food id
				controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				controller.sleep(100);
			}
			controller.sleep(1280);   //Important, leave in

			if(controller.getInventoryItemCount(foodId) < 27) {  //withdraw 27 shark if needed        //change 546(shark) to desired food id
				controller.withdrawItem(foodId, 27 - controller.getInventoryItemCount(foodId));          //change 546(shark) to desired food id
				controller.sleep(640);
			}
			if(controller.getBankItemCount(foodId) == 0) {
				controller.setStatus("@red@NO Food in the bank, Logging Out!.");
				controller.setAutoLogin(false);
				controller.logout();
				if(!controller.isLoggedIn()) {
					controller.stop();
					return;
				}
			}
			controller.closeBank();
			controller.sleep(1320);
			invCoins = controller.getInventoryItemCount(10);
			invChaos = controller.getInventoryItemCount(41);
		}
	}











	//Pathing Scripts Below
    public void goUpPaladinsLadder(){
        controller.setStatus("@gre@Walking to Bank..");
        controller.walkTo(611,1550);
        controller.atObject(611,1551);
        controller.sleep(640);
    }
	public void TreasureRoomToBank() {
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
    public void pathToBank() {
        controller.walkPath(pathToBank);
    }
    int[] pathToBank =  {
            604, 603,
            589, 604,
            575, 605,
            564, 606,
            554, 607
    };


	//GUI stuff below (icky)


    public void parseVariables(){
        startCoins = controller.getInventoryItemCount(10);
        startChaos = controller.getInventoryItemCount(41);
        invCoins = controller.getInventoryItemCount(10);
        invChaos = controller.getInventoryItemCount(41);
        startTime = System.currentTimeMillis();
    }
	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	public void setupGUI() {
		JLabel header = new JLabel("Paladin Thiever - By Kaila");
		JLabel label1 = new JLabel("Start in Ardy South Bank OR in Paladin Tower");
		JLabel label2 = new JLabel("Sharks/Swords/Tuna/Lobs in bank REQUIRED");
		JLabel label3 = new JLabel("Switch to Defensive combat mode.");
		JLabel label5 = new JLabel("Never wield weapons when Thieving.");
        JLabel label6 = new JLabel("This script can use param \"autostart\" (Sharks)");
        JLabel label7 = new JLabel("or param \"Sharks\", \"Swordfish\", \"Tuna\", or \"Lobsters\"");
		JLabel foodLabel = new JLabel("Type of Food:");
		JComboBox<String> foodField = new JComboBox<String>( new String[] { "Sharks", "Swordfish", "Tuna", "Lobsters" });
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                foodId = foodIds[foodField.getSelectedIndex()];
                parseVariables();
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
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
		scriptFrame.add(label5);
        scriptFrame.add(label6);
        scriptFrame.add(label7);
		scriptFrame.add(foodLabel);
		scriptFrame.add(foodField);
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
	    		CoinSuccessPerHr = (int)((totalCoins + invCoins - startCoins) * scale);
	    		ChaosSuccessPerHr = (int)((totalChaos + invChaos - startChaos) * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);

	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Paladins Thiever @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Coins: @gre@" + String.valueOf(this.totalCoins + this.invCoins - this.startCoins) + "@yel@ (@whi@" + String.format("%,d", CoinSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Chaos: @gre@" + String.valueOf(this.totalChaos + this.invChaos - this.startChaos) + "@yel@ (@whi@" + String.format("%,d", ChaosSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Raw Shark: @gre@" + String.valueOf(this.totalShark), 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Adamantite Ore: @gre@" + String.valueOf(this.totalAda), 350, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Uncut Sapphire: @gre@" + String.valueOf(this.totalSap), 350, 118, 0xFFFFFF, 1);
			controller.drawString("@whi@Black Scimitar: @gre@" + String.valueOf(this.totalScim), 350, 132, 0xFFFFFF, 1);
			controller.drawString("@whi@Items In Bank:", 330, 146, 0xFFFFFF, 1);
			controller.drawString("@whi@Coins: @gre@" + String.valueOf(this.coinsInBank) + " @gre@Million", 350, 160, 0xFFFFFF, 1);
			controller.drawString("@whi@Chaos: @gre@" + String.valueOf(this.chaosInBank), 350, 174, 0xFFFFFF, 1);
			controller.drawString("@whi@Food in Bank: @gre@" + String.valueOf(this.foodInBank), 350, 188, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 202, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 216, 0xFFFFFF, 1);
		}
	}
}
