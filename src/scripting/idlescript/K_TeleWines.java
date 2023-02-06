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
 * Grabs red spider Wines in edge dungeon, recommend very high stats ~90+
 * 
 * 
 * 
 * 
 * Author - Kaila
 */
public class K_TeleWines extends IdleScript {	
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int WinezInBank = 0;
	int totalWinez = 0;
    int totalTrips = 0;
	
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
		
		public int start(String parameters[]) {
			if (!guiSetup) {
				setupGUI();
				guiSetup = true;
			}
			if (scriptStarted) {
				controller.displayMessage("@red@Wine Picker - By Kaila");
				controller.displayMessage("@red@Start in Fally West Bank");
				controller.displayMessage("@red@Laws IN BANK REQUIRED");
				if(controller.isInBank() == true) {
					controller.closeBank();
				}
				if(controller.currentY() > 450) {
					bank();
					BankToWine();
					controller.sleep(1380);
				}
				scriptStart();
			}
			return 1000; //start() must return a int value now. 
		}
		
		
		public void scriptStart() {
			while(controller.isRunning()) {
							
				if(controller.getInventoryItemCount() == 30) {
					controller.setStatus("@red@Banking..");
					WineToBank();
					bank();
					BankToWine();
					controller.sleep(618);
				}
				
				controller.setStatus("@yel@Picking Wines..");
				int[] coords = controller.getNearestItemById(501);
				 
				if(coords != null) {
					controller.castSpellOnGroundItem(controller.getSpellIdFromName("Telekinetic grab"), 501, 333, 434);
					controller.sleep(1500);
					controller.walkTo(331,434);
					controller.sleep(100);
					controller.walkTo(332,434);
					controller.sleep(100);
				} else {
					controller.sleep(1500);
				}				
			}
		}
					
	
	public void bank() {
		
		controller.setStatus("@yel@Banking..");
		controller.openBank();
		
		while(controller.isInBank()){
			
			totalWinez = totalWinez + controller.getInventoryItemCount(501);
			
			if(controller.getInventoryItemCount(501) >  0) {  //deposit the Wines
				controller.depositItem(501,controller.getInventoryItemCount(501));
				controller.sleep(1380);
			}
			
			WinezInBank = controller.getBankItemCount(501);
			

			if(controller.getInventoryItemCount(42) < 30) {  //withdraw 30 law
				controller.withdrawItem(42, 30 - controller.getInventoryItemCount(42));
				controller.sleep(340);
			}
			if(!controller.isItemIdEquipped(101) && !controller.isItemIdEquipped(617) && !controller.isItemIdEquipped(684)) {  //check air staff!
				controller.displayMessage("@red@NO Air staff, attempting to Equip!");
				if(controller.getBankItemCount(101) > 0) {
					controller.withdrawItem(101, 1);
					controller.closeBank();
					controller.equipItem(controller.getInventoryItemSlotIndex(101));
					controller.sleep(340);
					return;
				} else if(controller.getBankItemCount(617) > 0) {
					controller.withdrawItem(617, 1);
					controller.closeBank();
					controller.equipItem(controller.getInventoryItemSlotIndex(617));
					controller.sleep(340);
					return;
				} else if(controller.getBankItemCount(684) > 0) {
					controller.withdrawItem(684, 1);
					controller.closeBank();
					controller.equipItem(controller.getInventoryItemSlotIndex(684));
					controller.sleep(340);
					return;
				} else if(controller.getBankItemCount(101) == 0 && controller.getBankItemCount(617) == 0 && controller.getBankItemCount(684) == 0)
				controller.displayMessage("@red@NO Air staff, ending script");
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
	
	public void WineToBank() {  //replace
    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(332,434);
		controller.walkTo(327,435);
		controller.walkTo(310,435);
		controller.walkTo(299,446);
		controller.walkTo(309,456);
		controller.walkTo(309,468);
		controller.walkTo(310,468);
		controller.walkTo(310,478);
		controller.walkTo(311,479);
		controller.walkTo(311,488);
		controller.walkTo(305,494);
		controller.walkTo(305,496);
		controller.walkTo(298,503);
		controller.walkTo(312,517);
		controller.walkTo(312,518);
		controller.walkTo(324,530);
		controller.walkTo(324,549);
		controller.walkTo(327,552);
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking..");
	}
	
    public void BankToWine() {	
    	controller.setStatus("@gre@Walking to Wines..");
		controller.walkTo(327,552);
		controller.walkTo(324,549);
		controller.walkTo(324,530);
		controller.walkTo(312,518);
		controller.walkTo(312,517);
		controller.walkTo(298,503);
		controller.walkTo(305,496);
		controller.walkTo(305,494);
		controller.walkTo(311,488);
		controller.walkTo(311,479);
		controller.walkTo(310,478);
		controller.walkTo(310,468);
		controller.walkTo(309,468);
		controller.walkTo(309,456);
		controller.walkTo(299,446);
		controller.walkTo(310,435);
		controller.walkTo(327,435);
		controller.walkTo(332,434);
    	//next to wine now)
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
		JLabel header = new JLabel("Zammy Wine Picker - By Kaila");
		JLabel label1 = new JLabel("Start in Fally West Bank");
		JLabel label2 = new JLabel("Air Staff/Bstaff/etc MUST be Equipped");
		JLabel label3 = new JLabel("Laws in the bank required!");
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
	    		successPerHr = (int)(totalWinez * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    		
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Zammy Winez @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Wines in Bank: @gre@" + String.valueOf(this.WinezInBank), 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Wines Picked: @gre@" + String.valueOf(this.totalWinez) + "@yel@ (@whi@" + String.format("%,d", successPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 104, 0xFFFFFF, 1);
		}
	}
}
