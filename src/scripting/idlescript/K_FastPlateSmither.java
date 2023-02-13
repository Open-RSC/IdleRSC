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
 * Start in bank with hammer 
 * Batch bars MUST be toggled on with in-game settings!
 * by Kaila
 */
public class K_FastPlateSmither extends IdleScript {	
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int barId = -1;
	int[] barIds = { 169, 170, 171, 173, 174, 408 };
	int barsInBank = 0;
	int totalPlates = 0;
	int totalBars = 0;
	
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
		if(controller.getInventoryItemCount(barId) < 5 && !controller.isInBank()) {
			controller.setStatus("@gre@Banking..");
			controller.walkTo(150,507);
			bank();
			controller.walkTo(148,512);
		}
		if(controller.getInventoryItemCount(barId) > 4) {
			controller.setStatus("@gre@Smithing..");
			controller.useItemIdOnObject(148, 513, barId);
			controller.sleep(1000);
			controller.optionAnswer(1);
			controller.sleep(650);
			controller.optionAnswer(2);
			controller.sleep(650);
			controller.optionAnswer(2);
			controller.sleep(650);
			controller.optionAnswer(3);
			controller.sleep(650);
			while(controller.isBatching()) controller.sleep(1000);
		}
		controller.sleep(320);
	}
}
	















	public void bank() {

		controller.setStatus("@gre@Banking..");
		controller.openBank();
		
		while(controller.isInBank() == true) {
			
			totalPlates = totalPlates + 5;
			totalBars = totalBars + 25;
			
			if(controller.getBankItemCount(barId) < 30) {    //stops making when 30 in bank to not mess up alignments/organization of bank!!!
				controller.setStatus("@red@NO Bars in the bank, Logging Out!.");
				controller.setAutoLogin(false);
				controller.logout();
				if(!controller.isLoggedIn()) {
					controller.stop();
					return;
				}
			}
			if(controller.getInventoryItemCount() >  0) {
				for (int itemId : controller.getInventoryItemIds()) {
					if (itemId != 168 && itemId != barId) {
						controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
					}
				}
				controller.sleep(100);
			}
			if (controller.getInventoryItemCount(168) < 1) {
				controller.withdrawItem(168, 1);
				controller.sleep(320);
			}
			if (controller.getInventoryItemCount(barId) < 25) {
				controller.withdrawItem(barId, 25);
				controller.sleep(320);
			}
			
			barsInBank = controller.getBankItemCount(barId);
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
		JLabel header = new JLabel("Platebody Smithing - Kaila");
		JLabel hammerLabel = new JLabel("Start with Hammer in Inv!");
		JLabel batchLabel = new JLabel("Batch Bars MUST be toggled ON in settings!!!");
		JLabel batchLabel2 = new JLabel("This ensures 5 Plates are made per Menu Cycle.");
		JLabel barLabel = new JLabel("Bar Type:");
		JComboBox<String> barField = new JComboBox<String>(
				new String[] { "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite" });
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				barId = barIds[barField.getSelectedIndex()];
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				startTime = System.currentTimeMillis();
				scriptStarted = true;
				controller.displayMessage("@gre@" + '"' + "Fast Platebody Smither" + '"' + " - by Kaila");
				controller.displayMessage("@gre@Start in Varrock West bank with a HAMMER");
			}
		});
		
		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(hammerLabel);
		scriptFrame.add(batchLabel);
		scriptFrame.add(batchLabel2);
		scriptFrame.add(barLabel);
		scriptFrame.add(barField);
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
        		successPerHr = (int)(totalPlates * scale);
        	} catch(Exception e) {
        		//divide by zero
        	}
			controller.drawString("@red@Fast Plate Smither @gre@by Kaila", 350, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Bars In bank: @yel@" + String.valueOf(this.barsInBank), 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Bars Used: @yel@" + String.valueOf(this.totalBars), 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Platebodies Made: @yel@" + String.valueOf(this.totalPlates), 350, 90, 0xFFFFFF, 1);
            controller.drawString("@whi@Platebodies Per Hr: @yel@" + String.format("%,d", successPerHr) + "@red@/@whi@hr@red@)", 350, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 118, 0xFFFFFF, 1);
		}
	}
}
