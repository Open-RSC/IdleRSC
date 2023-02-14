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
public class K_BattlefieldTrainer extends IdleScript {	
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
	
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (scriptStarted) {
			controller.displayMessage("@red@Battlefield Trainer - By Kaila");
			controller.displayMessage("@red@Start in Edge bank with Armor");
			controller.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
			controller.displayMessage("@red@31 Magic Required for escape tele");
			if(controller.isInBank() == true) {
				controller.closeBank();
			}
			if(controller.currentX() < 600) {
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
						
				if(controller.getInventoryItemCount(546) > 0) {
				    		
				   	if(!controller.isInCombat()) {
				   		
		    			controller.setStatus("@yel@Attacking Trooper");
					   	ORSCharacter npc = controller.getNearestNpcById(407, false);
					   	if(npc != null) {
					    	controller.walktoNPC(npc.serverIndex,1);
					    	controller.attackNpc(npc.serverIndex);
					    	controller.sleep(1000);
					    } else {
							controller.sleep(1000);
						}
				    }
			    	controller.sleep(1380);
				} else if(controller.getInventoryItemCount(546) == 0) {
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
		controller.sleep(640);

		if (controller.isInBank()) {
			
			if (controller.getInventoryItemCount() > 1) {
				for (int itemId : controller.getInventoryItemIds()) {
					if (itemId != 546) {
						controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
					}
				}
				controller.sleep(1280);   // increased sleep here to prevent double banking
			}
			if(controller.getInventoryItemCount(546) < 28) {  //withdraw 1 shark
				controller.withdrawItem(546, 28);
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
				controller.setStatus("@red@We've ran out of Food! Running Away!.");
		    	DruidToBank();
		    	bank();
		    	BankToDruid();
				}
			}
		}
	
	public void DruidToBank() {
    	controller.setStatus("@gre@Walking to Bank..");
    	
		controller.walkTo(649,639);
		controller.walkTo(644,639);
		controller.walkTo(636,638);
		controller.walkTo(624,638);
		controller.walkTo(614,632);
		controller.walkTo(622,633);
		controller.walkTo(614,632);
		controller.walkTo(610,635);
		controller.walkTo(599,635);
		controller.walkTo(598,632);
		controller.walkTo(592,627);
		controller.walkTo(579,628);
		controller.walkTo(571,628);
		controller.walkTo(563,621);
		controller.walkTo(550,620);
		controller.walkTo(550,613);
    	
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking..");
	}
	
    public void BankToDruid() {	
    	controller.setStatus("@gre@Walking to Druids..");
    	
		controller.walkTo(550,613);
		controller.walkTo(550,620);
		controller.walkTo(563,621);
		controller.walkTo(571,628);
		controller.walkTo(579,628);
		controller.walkTo(592,627);
		controller.walkTo(598,632);
		controller.walkTo(599,635);
		controller.walkTo(610,635);
		controller.walkTo(614,632);
		controller.walkTo(622,633);
		controller.walkTo(624,638);
		controller.walkTo(636,638);
		controller.walkTo(644,639);
		controller.walkTo(649,639);
		controller.walkTo(653,642);
		controller.walkTo(658,642);

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
		JLabel header = new JLabel("Battlefield Trainer - By Kaila");
		JLabel label1 = new JLabel("Start in Edge bank with Gear ON");
		JLabel label2 = new JLabel("Sharks in bank REQUIRED");
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
    		int TripSuccessPerHr = 0;
    		
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    		
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
	    	
			controller.drawString("@red@Battlefield Trainer @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 76, 0xFFFFFF, 1);
		}
	}
}
