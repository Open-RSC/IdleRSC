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
 * 	Attack Cape Buyer - By Kaila.
 *
 * 		Talks to Rovin for capes and Banks.
 * 		Start by Rovin or varrock west!
 * 		Need coins in the inventory to buy.
 *
 * Author - Kaila.
 */
public class K_AttackCapeBuyer extends IdleScript {
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int GrapezInBank = 0;
	int totalTopz = 0;
	int totalBotz = 0;
    int totalTrips = 0;
    int TopzInBank = 0;
    int BotzInBank = 0;

    int robeId[] = {388,389};

	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;


		public int start(String parameters[]) {
			if (!guiSetup) {
				setupGUI();
				guiSetup = true;
			}
			if (scriptStarted) {
				controller.displayMessage("@red@Attack Cape Buyer - By Kaila");
				controller.displayMessage("@red@Start by Rovin or varrock west!");
				controller.displayMessage("@red@Need coins in the inventory to buy");
				if(controller.isInBank() == true) {
					controller.closeBank();
				}
				if(controller.currentY() < 1000) {
					bank();
					BankToGrape();
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
					GrapeToBank();
					bank();
					BankToGrape();
					controller.sleep(618);
				}
				ORSCharacter npc = controller.getNearestNpcById(18, true);

				if(npc != null) {
					controller.setStatus("@red@Getting cape from Rovin..");
					controller.talkToNpc(npc.serverIndex);
					controller.sleep(6000);

					if(controller.isInOptionMenu() == false)
						continue;

					controller.optionAnswer(2);
					controller.sleep(9000);
					controller.optionAnswer(0);
					controller.sleep(12000);
				}




			}
		}


	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(640);

		if(controller.isInBank()){

			totalTopz = totalTopz + controller.getInventoryItemCount(1374);

			if(controller.getInventoryItemCount(1374) >  0) {  //robe top
				controller.depositItem(1374,controller.getInventoryItemCount(1374));
				controller.sleep(1380);
			}

			TopzInBank = controller.getBankItemCount(1374);

			controller.closeBank();
			controller.sleep(640);
		}
	}

	public void GrapeToBank() {  //replace

    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(141,1398);
		controller.sleep(340);
		controller.atObject(142,1398); //down ladder
		controller.sleep(800);
		controller.walkTo(135,460);
		controller.walkTo(135,470);
		controller.walkTo(132,474);
		controller.walkTo(132,484);
		controller.walkTo(132,494);
		controller.walkTo(132,502);
		controller.walkTo(137,507);
		controller.walkTo(150,507);
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking..");

	}

    public void BankToGrape() {

    	controller.setStatus("@gre@Walking to Rovin..");
		controller.walkTo(150,507);
		controller.walkTo(137,507);
		controller.walkTo(132,502);
		controller.walkTo(132,494);
		controller.walkTo(132,484);
		controller.walkTo(132,474);
		controller.walkTo(135,470);
		controller.walkTo(135,460);
		controller.walkTo(141,454);
		controller.atObject(142,454); //up ladder
		controller.sleep(800);
    	//next to rovin now)
    	controller.setStatus("@gre@Done Walking..");

	}


	//GUI stuff below
	public void setupGUI() {
		JLabel header = new JLabel("Attack Cape Buyer - By Kaila");
		JLabel label1 = new JLabel("Talks to Rovin for capes & Banks");
		JLabel label2 = new JLabel("Start by Rovin or varrock west!");
		JLabel label3 = new JLabel("Need coins in the inventory to buy");
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

		scriptFrame = new JFrame(controller.getPlayerName() + " - options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(label1);
		scriptFrame.add(label2);
		scriptFrame.add(label3);
		scriptFrame.add(startScriptButton);
        scriptFrame.pack();
        scriptFrame.setLocationRelativeTo(null);
        scriptFrame.setVisible(true);
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
	    	int TopzSuccessPerHr = 0;
	    	int TripSuccessPerHr = 0;

	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		TopzSuccessPerHr = (int)(totalTopz * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);

	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Attack Cape Buyer @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Capes in Bank: @gre@" + String.valueOf(this.TopzInBank), 330, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Coins Spent: @gre@" + String.valueOf(this.totalTopz * 99) + " @whi@K", 330, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Capes Bought: @gre@" + String.valueOf(this.totalTopz) + "@yel@ (@whi@" + String.format("%,d", TopzSuccessPerHr) + "@yel@/@whi@hr@yel@)", 330, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 330, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 330, 118, 0xFFFFFF, 1);
		}
	}
}
