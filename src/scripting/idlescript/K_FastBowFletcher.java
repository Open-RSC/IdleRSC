package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * start in bank with knife in inventory
 * by Kaila
 */
public class K_FastBowFletcher extends IdleScript {	
	JFrame scriptFrame = null;
	int objectx = 0;
	int objecty = 0;
	int logId = -1;
	int[] logIds = { 14, 632, 633, 634, 635, 636 };
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int logsInBank = 0;
	int totalBows = 0;
	
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (scriptStarted) {
			if(controller.isInBank() == true) {
				controller.closeBank();
			}
			scriptStart();
		}
		return 1000; //start() must return a int value now. 
	}
	
	public void scriptStart() {
		while(controller.isRunning()) {
			if(controller.getInventoryItemCount(logId) < 1) {
				bank();
			}
			if(controller.getInventoryItemCount(logId) > 0) {
				controller.setStatus("@gre@Fletching..");
				controller.useItemOnItemBySlot(controller.getInventoryItemSlotIndex(13), controller.getInventoryItemSlotIndex(logId));
				controller.sleep(1200);
				controller.optionAnswer(2);
				while(controller.isBatching()) controller.sleep(1000);
			}
			controller.sleep(320);
		}	
	}
	

	public void bank() {
		
		controller.setStatus("@gre@Banking..");
		controller.openBank();
		
		while(controller.isInBank()) {
			
			totalBows = totalBows + 29;
			
			if(controller.getBankItemCount(logId) < 30) {    //stops making when 30 in bank to not mess up alignments/organization of bank!!!
				controller.setStatus("@red@NO Logs in the bank, Logging Out!.");
				controller.setAutoLogin(false);
				controller.logout();
				if(!controller.isLoggedIn()) {
					controller.stop();
					return;
				}
			}
			if(controller.getInventoryItemCount() >  0) {
				for (int itemId : controller.getInventoryItemIds()) {
				if (itemId != 13 && itemId != logId) {
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
					}
				}
				controller.sleep(100);
			}
			if(controller.getInventoryItemCount() < 30) {
				controller.withdrawItem(logId, 29);
				controller.sleep(650);
			}
		
			logsInBank = controller.getBankItemCount(logId);
			controller.closeBank();
			
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
		JLabel header = new JLabel("Unstrung Longbow Maker - Kaila");
		JLabel knifeLabel = new JLabel("Start with Knife in Inv!");
		JLabel logLabel = new JLabel("Log Type:");
		JComboBox<String> logField = new JComboBox<String>(
				new String[] { "Log", "Oak", "Willow", "Maple", "Yew", "Magic" });
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logId = logIds[logField.getSelectedIndex()];
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				startTime = System.currentTimeMillis();
				scriptStarted = true;
				controller.displayMessage("@gre@" + '"' + "Fast Longbow Fletcher" + '"' + " - by Kaila");
				controller.displayMessage("@gre@Start at any bank, with a KNIFE in Inv");
			}
		});
		
		scriptFrame = new JFrame("Script Options");
		
		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(knifeLabel);
		scriptFrame.add(logLabel);
		scriptFrame.add(logField);
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
        	try {
        		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        		float scale = (60 * 60) / timeRan;
        		successPerHr = (int)(totalBows * scale);
        	} catch(Exception e) {
        		//divide by zero
        	}
			controller.drawString("@red@Fast Bow Fletcher @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Logs In bank: @yel@" + String.valueOf(this.logsInBank), 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Longbows Made: @yel@" + String.valueOf(this.totalBows), 350, 76, 0xFFFFFF, 1);
            controller.drawString("@whi@Longbows Per Hr: @yel@" + String.format("%,d", successPerHr) + "@red@/@whi@hr@red@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 104, 0xFFFFFF, 1);
		}
	}
}

