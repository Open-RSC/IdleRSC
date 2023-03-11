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
 *
 *
 * This bot supports the "autostart" parameter to automatiically start the bot without gui
 *
 *
 * Author - Kaila
 */
public class K_HobsMiner extends IdleScript {
	JFrame scriptFrame = null;

	int coalInBank = 0;
	int mithInBank = 0;
	int addyInBank = 0;
	int totalCoal = 0;
	int totalMith = 0;
	int totalAddy = 0;
	int totalSap = 0;
	int totalEme = 0;
	int totalRub = 0;
	int totalDia = 0;
    int totalTrips = 0;

	int currentOre[] = {0,0};
	int addyIDs[] = {108,231,109}; //108,231,109 (addy) 106,107 (mith) 110,111 (coal)  98 empty
	int mithIDs[] = {106,107};
	int coalIDs[] = {110,111};
	int oreIDs[] = {409,154,153,155};
	int gemIDs[] = {157,158,159,160};
	int[] loot = {
			//loot RDT hob drops
			526, 	 //tooth half
			527, 	 //loop half
			1277, 	 //shield (left) half
			1092, 	 //rune spear
			160, 	 //saph
			159, 	 //emerald
			158, 	 //ruby
			157,	 //diamond

			//loot common armor if another bot dies
			1318,	//ring of wealth
			402,	//rune leg
			400,	//rune chain
			399,	//rune med
			403,	//rune sq
			404,	//rune kite
			112,	//rune full helm
			1262,	//rune pic
			315,	//Emerald Amulet of protection
			317,	//Diamond Amulet of power
			522,	//dragonstone ammy

			//loot "some" hobs drops
			38, 	 //death rune
			619, 	 //blood rune
			42,		 //laws
			40,		 //nats
			440,  	 //Grimy ava
			441,	 //Grimy kwu
			442, 	 //Grimy cada
			443, 	 //Grimy dwu

	};
	String isMining = "none";

	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;

	boolean guiSetup = false;
	boolean scriptStarted = false;
	boolean teleportOut = false;
	boolean returnEscape = true;
	public boolean isWithinLootzone(int x, int y) {
		return controller.distance(225, 251, x, y) <= 30; //center of hobs mine lootzone
	}
    public void startSequence() {
        controller.displayMessage("@red@Hobs Miner- By Kaila");
        controller.displayMessage("@red@Start in Edge bank with Armor and pickaxe");
        controller.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
        controller.displayMessage("@red@31 Magic Required for escape tele");
        if(controller.isInBank() == true) {
            controller.closeBank();
        }
        if(controller.currentY() > 340) {
            bank();
            eat();
            bankToHobs();
            eat();
            controller.sleep(1380);
        }
        if(controller.currentY() > 270 && controller.currentY() < 341) {
            bankToHobs();
            eat();
            controller.sleep(1380);
        }
    }
		public int start(String parameters[]) {
            if (parameters.length > 0 && !parameters[0].equals("")) {
                if (parameters[0].toLowerCase().startsWith("auto")) {
                    controller.displayMessage("Auto-starting, teleport false, return escape true", 0);
                    System.out.println("Auto-starting, teleport false, return escape true");
                    teleportOut = false;
                    returnEscape = true;
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


		public void scriptStart() {
			while(controller.isRunning()) {

				eat();
				leaveCombat();

				if(controller.getInventoryItemCount(546) == 0) {
					controller.setStatus("@red@We've ran out of Food! Teleporting Away!.");
					hobsToTwenty();
					controller.sleep(100);
					controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport"));
					controller.sleep(308);
					controller.walkTo(120,644);
					controller.atObject(119,642);
					controller.walkTo(217,447);
					controller.sleep(618);
					bank();
					bankToHobs();
				}
				if (controller.getInventoryItemCount() == 30) {

					goToBank();

				}
				if (controller.getInventoryItemCount() < 30) {

					eat();
					leaveCombat();

					boolean lootPickedUp = false;
					for (int lootId : loot) {
						int[] coords = controller.getNearestItemById(lootId);
						if (coords != null && this.isWithinLootzone(coords[0], coords[1])) {
							controller.setStatus("@yel@Looting..");
							controller.walkTo(coords[0], coords[1]);
							controller.pickupItem(coords[0], coords[1], lootId, true, true);
							controller.sleep(618);
						}
					}
					if (lootPickedUp) //we don't want to start to pickup loot then immediately attack a npc
						continue;

					if (rockEmpty() || !controller.isBatching()) {
						isMining = "none";
						currentOre[0] = 0;
						currentOre[1] = 0;
					}
					if (controller.isBatching()) {
						if (isMining == "mithril") {
							if (adamantiteAvailable()) {
								mine("adamantite");
							}
						}
						if (isMining == "coal") {
							if (adamantiteAvailable()) {
								mine("adamantite");
							} else if (mithrilAvailable()) {
								mine("mithril");
							}
						}
						controller.sleep(1280);
					}
					leaveCombat();
					controller.setStatus("@yel@Mining..");

					if (!controller.isBatching() && isMining == "none" && rockEmpty()) {
						if (adamantiteAvailable()) {
							mine("adamantite");
						} else if (mithrilAvailable()) {
							mine("mithril");
						} else if (coalAvailable()) {
							mine("coal");
						}
						controller.sleep(1280);
					}
				}
			}
		}


		public void mine(String i) {
			if (i == "adamantite") {
				int oreCoords[] = controller.getNearestObjectByIds(addyIDs);
				if (oreCoords != null) {
					isMining = "adamantite";
					controller.atObject(oreCoords[0], oreCoords[1]);
					currentOre[0] = oreCoords[0];
					currentOre[1] = oreCoords[1];
				}
			} else if (i == "mithril") {
				int oreCoords[] = controller.getNearestObjectByIds(mithIDs);
				if (oreCoords != null) {
					isMining = "mithril";
					controller.atObject(oreCoords[0], oreCoords[1]);
					currentOre[0] = oreCoords[0];
					currentOre[1] = oreCoords[1];
				}
			} else if (i == "coal") {
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
		public boolean adamantiteAvailable() {
		    return controller.getNearestObjectByIds(addyIDs) != null;
		}
		public boolean mithrilAvailable() {
		    return controller.getNearestObjectByIds(mithIDs) != null;
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
		controller.sleep(1200);

		if (controller.isInBank()) {

			totalCoal = totalCoal + controller.getInventoryItemCount(155);
			totalMith = totalMith + controller.getInventoryItemCount(153);
			totalAddy = totalAddy + controller.getInventoryItemCount(154);
			totalSap = totalSap + controller.getInventoryItemCount(160);
			totalEme = totalEme + controller.getInventoryItemCount(159);
			totalRub = totalRub + controller.getInventoryItemCount(158);
			totalDia = totalDia + controller.getInventoryItemCount(157);

			for (int itemId : controller.getInventoryItemIds()) {
				if (itemId != 546 && itemId != 156 && itemId != 1263 && itemId != 1262) { //wont banks sharks, rune/bronze pick, or sleeping bags
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				}
			}
			controller.sleep(1280);   // increased sleep here to prevent double banking


			coalInBank = controller.getBankItemCount(155);
			mithInBank = controller.getBankItemCount(153);
			addyInBank = controller.getBankItemCount(154);

			if (teleportOut == true) {
				if (controller.getInventoryItemCount(33) < 3) {  //withdraw 3 air
					controller.withdrawItem(33, 3);
					controller.sleep(640);
				}
				if (controller.getInventoryItemCount(34) < 1) {  //withdraw 1 earth
					controller.withdrawItem(34, 1);
					controller.sleep(640);
				}
				if (controller.getInventoryItemCount(42) < 1) {  //withdraw 1 law
					controller.withdrawItem(42, 1);
					controller.sleep(640);
				}
			}
			if(controller.getInventoryItemCount(546) > 1) {
				controller.depositItem(546, controller.getInventoryItemCount(546) - 1);
				controller.sleep(640);
			}
			if(controller.getInventoryItemCount(546) < 1) {  //withdraw 1 shark
				controller.withdrawItem(546, 1);
				controller.sleep(640);
			}
			if(controller.getBankItemCount(546) == 0) {
				controller.setStatus("@red@NO Sharks in the bank, Logging Out!.");
				controller.setAutoLogin(false);
				controller.sleep(5000);
				controller.logout();
				if(!controller.isLoggedIn()) {
					controller.stop();
					return;
				}
			}
			controller.closeBank();
			controller.sleep(640);
		}
		if (teleportOut == true) {
			airCheck();
			earthCheck();
			lawCheck();
		}
	}

	public void eat() {

		int eatLvl = controller.getBaseStat(controller.getStatId("Hits")) - 20;

		if(controller.getCurrentStat(controller.getStatId("Hits")) < eatLvl) {

			leaveCombat();
			controller.sleep(200);

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

				controller.setStatus("@red@We've ran out of Food at Hobs! Running Away!.");
				isMining = "none";
				currentOre[0] = 0;
				currentOre[1] = 0;
				controller.setStatus("@yel@Banking..");
				hobsToTwenty();

				if (teleportOut == false
						|| controller.getInventoryItemCount(42) < 1
						|| controller.getInventoryItemCount(33) < 3
						|| controller.getInventoryItemCount(34) < 1) {  //or no earths/airs/laws
					twentyToBank();
				}
				if (teleportOut == true) {
					controller.sleep(100);
					controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport (1)"));
					controller.sleep(800);
					if (controller.currentY() < 425) {
						controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport (2)"));
						controller.sleep(800);
					}
					if (controller.currentY() < 425) {
						controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport (3)"));
						controller.sleep(1000);
					}
					while (controller.currentY() < 425) {
						controller.castSpellOnSelf(controller.getSpellIdFromName("Lumbridge Teleport (n)"));
						controller.sleep(800);
					}
					controller.walkTo(120, 644);
					controller.atObject(119, 642);
					controller.walkTo(217, 447);
					controller.sleep(308);
				}
				if (returnEscape == false) {
					controller.setAutoLogin(false);  //uncomment and remove bank and banktoHobs to prevent bot going back to mine after being attacked
					controller.logout();
					controller.sleep(1000);

					if(!controller.isLoggedIn()) {
						controller.stop();
						controller.logout();
						return;
					}
				}
				if (returnEscape == true) {
					bank();
					bankToHobs();
					controller.sleep(618);
				}
			}
		}
	}

	public void goToBank() {
		isMining = "none";
		currentOre[0] = 0;
		currentOre[1] = 0;
		controller.setStatus("@yel@Banking..");
		hobsToTwenty();
		twentyToBank();
		bank();
		bankToHobs();
		controller.sleep(618);
	}

	public void hobsToTwenty() {
    	controller.setStatus("@gre@Walking to 19 wildy..");
		controller.walkTo(221,262);
		controller.walkTo(221,283);
		controller.walkTo(221,301);
		controller.walkTo(221,314);
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking to 19..");
	}

    public void twentyToBank() {
    	controller.setStatus("@gre@Walking to Bank..");
		eat();
		controller.walkTo(221,321);
		controller.walkTo(222,341);
		controller.walkTo(222,361);
		controller.walkTo(222,381);
		controller.walkTo(222,401);
		controller.walkTo(215,410);
		controller.walkTo(215,420);
		controller.walkTo(220,425);
		controller.walkTo(220,445);
		controller.walkTo(217,448);


    	controller.setStatus("@gre@Done Walking..");
	}
    public void bankToHobs() {
    	controller.setStatus("@gre@Walking to Hobs Mine..");
		controller.walkTo(218,447);
		controller.walkTo(220,443);
		controller.walkTo(220,433);
		controller.walkTo(220,422);
		controller.walkTo(215,417);
		controller.walkTo(215,410);
		controller.walkTo(215,401);
		controller.walkTo(215,395);
		eat();
		controller.walkTo(222,388);
		controller.walkTo(222,381);
		controller.walkTo(222,361);
		controller.walkTo(222,341);
		controller.walkTo(221,321);
		controller.walkTo(221,314);
		controller.walkTo(221,301);
		controller.walkTo(221,283);
		controller.walkTo(221,262);

    	controller.setStatus("@gre@Done Walking..");
	}
	public void lawCheck() {
		if(controller.getInventoryItemCount(42) < 1) {  //law
			controller.openBank();
			controller.sleep(1200);
			controller.withdrawItem(42, 1);
			controller.sleep(1000);
			controller.closeBank();
			controller.sleep(1000);
		}
	}
	public void earthCheck() {
		if(controller.getInventoryItemCount(34) < 1) {  //earth
			controller.openBank();
			controller.sleep(1200);
			controller.withdrawItem(34, 1);
			controller.sleep(1000);
			controller.closeBank();
			controller.sleep(1000);
		}
	}
	public void airCheck() {
		if(controller.getInventoryItemCount(33) < 3) {  //air
			controller.openBank();
			controller.sleep(1200);
			controller.withdrawItem(33, 3 - controller.getInventoryItemCount(33));
			controller.sleep(1000);
			controller.closeBank();
			controller.sleep(1000);
		}
	}
	public void leaveCombat() {
		for (int i = 1; i <= 15; i++) {
			if (controller.isInCombat()) {
				controller.setStatus("@red@Leaving combat..");
				controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
				controller.sleep(400);
			}
			controller.sleep(10);
		}
	}





	//GUI stuff below (icky)

    public void parseVariables() {
        startTime = System.currentTimeMillis();
    }
	public void setValuesFromGUI(JCheckBox potUpCheckbox, JCheckBox escapeCheckbox) {
		if (potUpCheckbox.isSelected()) {
			teleportOut = true;
		} else {
			teleportOut = false;
		}
		if (escapeCheckbox.isSelected()) {
			returnEscape = true;
		} else {
			returnEscape = false;
		}
	}
	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	public void setupGUI() {
		JLabel header = new JLabel("Hobs Miner - By Kaila");
		JLabel label1 = new JLabel("Start in Edge bank with Armor and Pickaxe");
		JLabel label2 = new JLabel("Sharks in bank REQUIRED");
		JCheckBox teleportCheckbox = new JCheckBox("Teleport if Pkers Attack?", false);
		JLabel label3 = new JLabel("31 Magic, Laws, Airs, and Earths required for Escape Tele");
		JLabel label4 = new JLabel("Unselected, bot WALKS to Edge when Attacked");
		JLabel label5 = new JLabel("Selected, bot walks to 19 wildy & teleports");
		JCheckBox escapeCheckbox = new JCheckBox("Return to Hobs Mine after Escaping?", true);
		JLabel label6 = new JLabel("Unselected, bot will log out after escaping Pkers");
		JLabel label7 = new JLabel("Selected, bot will grab more food and return");
        JLabel label8 = new JLabel("This bot supports the \"autostart\" parameter");
        JLabel label9 = new JLabel("Defaults to Teleport Off, Return On.");
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setValuesFromGUI(teleportCheckbox, escapeCheckbox);
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
                parseVariables();
				scriptStarted = true;
			}
		});

		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(label1);
		scriptFrame.add(label2);
		scriptFrame.add(teleportCheckbox);
		scriptFrame.add(label3);
		scriptFrame.add(label4);
		scriptFrame.add(label5);
		scriptFrame.add(escapeCheckbox);
		scriptFrame.add(label6);
		scriptFrame.add(label7);
        scriptFrame.add(label8);
        scriptFrame.add(label9);
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
	    	int mithSuccessPerHr = 0;
	    	int addySuccessPerHr = 0;
	    	int sapSuccessPerHr = 0;
	    	int emeSuccessPerHr = 0;
	    	int rubSuccessPerHr = 0;
	    	int diaSuccessPerHr = 0;
    		int TripSuccessPerHr = 0;

	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		coalSuccessPerHr = (int)(totalCoal * scale);
	    		mithSuccessPerHr = (int)(totalMith * scale);
	    		addySuccessPerHr = (int)(totalAddy * scale);
	    		sapSuccessPerHr = (int)(totalSap * scale);
	    		emeSuccessPerHr = (int)(totalEme * scale);
	    		rubSuccessPerHr = (int)(totalRub * scale);
	    		diaSuccessPerHr = (int)(totalDia * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);

	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Hobs Miner @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Coal Mined: @gre@" + String.valueOf(this.totalCoal) + "@yel@ (@whi@" + String.format("%,d", coalSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Mith Mined: @gre@" + String.valueOf(this.totalMith) + "@yel@ (@whi@" + String.format("%,d", mithSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Addy Mined: @gre@" + String.valueOf(this.totalAddy) + "@yel@ (@whi@" + String.format("%,d", addySuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Sapphires: @gre@" + String.valueOf(this.totalSap) + "@yel@ (@whi@" + String.format("%,d", sapSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Emeralds: @gre@" + String.valueOf(this.totalEme) + "@yel@ (@whi@" + String.format("%,d", emeSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 118, 0xFFFFFF, 1);
			controller.drawString("@whi@Rubys: @gre@" + String.valueOf(this.totalRub) + "@yel@ (@whi@" + String.format("%,d", rubSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 132, 0xFFFFFF, 1);
			controller.drawString("@whi@Diamonds: @gre@" + String.valueOf(this.totalDia) + "@yel@ (@whi@" + String.format("%,d", diaSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 146, 0xFFFFFF, 1);
			controller.drawString("@whi@Coal in Bank: @gre@" + String.valueOf(this.coalInBank), 370, 160, 0xFFFFFF, 1);
			controller.drawString("@whi@Mith in Bank: @gre@" + String.valueOf(this.mithInBank), 370, 174, 0xFFFFFF, 1);
			controller.drawString("@whi@Addy in Bank: @gre@" + String.valueOf(this.addyInBank), 370, 188, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 370, 202, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 370, 216, 0xFFFFFF, 1);
		}
	}
}
