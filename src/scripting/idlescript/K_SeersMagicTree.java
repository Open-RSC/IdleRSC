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
 * Cuts Magic logs in seers, including the far western one, banks in Seers.
 *
 * todo:
 *   reduce walking between locations - pause at each side.
 *   logic to cut same tree as other players.
 *
 * Author - Kaila
 */
public class K_SeersMagicTree extends IdleScript {
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int logInBank = 0;
	int totalLog = 0;
	int totalTrips = 0;
    int[] axeId = {
            87,12,88,203,204,405     //bronze to rune in order
    };
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
    public void startSequence(){
        controller.displayMessage("@red@SeersMagicTree, start with an axe in inv/equipment");
        if(controller.isInBank() == true) {
            controller.closeBank();
        }
        if(controller.currentY() < 458) {
            bank();
            controller.walkTo(500,454);
            controller.walkTo(503,457);
            controller.walkTo(503,460);
            controller.walkTo(506,463);
            controller.walkTo(506,472);
            controller.walkTo(506,478);
            controller.walkTo(516,488);
            controller.sleep(1380);
        }
    }
	public int start(String parameters[]) {
        if (parameters.length > 0 && !parameters[0].equals("")) {
            if (parameters[0].toLowerCase().startsWith("auto")) {
                controller.displayMessage("Got Autostart, Cutting Magics", 0);
                System.out.println("Got Autostart, Cutting Magics");
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
			if(controller.getInventoryItemCount() < 30) {

				if(controller.getObjectAtCoord(519,494) == 310) {
					controller.walkTo(519,493);
					controller.atObject(519,494);
					controller.sleep(2000);
					while(controller.isBatching() && controller.getInventoryItemCount() < 30) {
						controller.sleep(1000);
					}
					if(controller.getInventoryItemCount() > 29) {
						goToBank();
					}
				}
				if(controller.getObjectAtCoord(521,492) == 310) {
					controller.walkTo(521,491);
					controller.atObject(521,492);
					controller.sleep(2000);
					while(controller.isBatching() && controller.getInventoryItemCount() < 30) {
						controller.sleep(1000);
					}
					if(controller.getInventoryItemCount() > 29) {
						goToBank();
					}
				}
				if(controller.getObjectAtCoord(524,489) == 310) {
					controller.walkTo(524,488);
					controller.atObject(524,489);
					controller.sleep(2000);
					while(controller.isBatching() && controller.getInventoryItemCount() < 30) {
						controller.sleep(1000);
					}
					if(controller.getInventoryItemCount() > 29) {
						goToBank();
					}
				}
				controller.walkTo(530,487);
				controller.walkTo(538,486);
				if(controller.getObjectAtCoord(548,484) == 310) {
					controller.walkTo(547,484);
					controller.atObject(548,484);
					controller.sleep(2000);
					while(controller.isBatching() && controller.getInventoryItemCount() < 30) {
						controller.sleep(1000);
					}
					if(controller.getInventoryItemCount() > 29) {
						goToBank2();
					}
					controller.walkTo(538,486);
				}
				controller.walkTo(530,487);
				controller.walkTo(524,488);
			} else {
				goToBank();
			}
		}
	//	return 1000; //start() must return a int value now.
	}


    public void goToBank() {
        controller.walkTo(516,488);
        controller.walkTo(506,478);
        controller.walkTo(506,472);
        controller.walkTo(506,463);
        controller.walkTo(503,460);
        controller.walkTo(503,457);
        controller.walkTo(500,454);
        totalTrips = totalTrips + 1;
        bank();
        controller.walkTo(500,454);
        controller.walkTo(503,457);
        controller.walkTo(503,460);
        controller.walkTo(506,463);
        controller.walkTo(506,472);
        controller.walkTo(506,478);
        controller.walkTo(516,488);
    }

    public void goToBank2() {
        controller.walkTo(547,484);
        controller.walkTo(537,474);
        controller.walkTo(531,468);
        controller.walkTo(521,468);
        controller.walkTo(510,468);
        controller.walkTo(504,462);
        controller.walkTo(504,458);
        controller.walkTo(500,454);
        totalTrips = totalTrips + 1;
        bank();
        controller.walkTo(500,454);
        controller.walkTo(503,457);
        controller.walkTo(503,460);
        controller.walkTo(506,463);
        controller.walkTo(506,472);
        controller.walkTo(506,478);
        controller.walkTo(516,488);
    }

    public void bank() {

        controller.setStatus("@yel@Banking..");
        controller.openBank();
        controller.sleep(640);

        if (controller.isInBank()) {

            totalLog = totalLog + controller.getInventoryItemCount(636);

            for (int itemId : controller.getInventoryItemIds()) {
                if (itemId != 1263 && itemId != axeId[0] && itemId != axeId[1] && itemId != axeId[2] && itemId != axeId[3] && itemId != axeId[4] && itemId != axeId[5]) {
                    controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
                }
            }

            logInBank = controller.getBankItemCount(636);
            controller.closeBank();
            controller.sleep(1000);
        }
    }
	//GUI stuff below (icky)


	public void parseVariables() {
        startTime = System.currentTimeMillis();
    }

	public void setupGUI() {
		JLabel header = new JLabel("Seers Magic Logs by Kaila");
		JLabel label1 = new JLabel("Start in Seers bank, or near trees!");
		JLabel label2 = new JLabel("Wield or have rune axe in Inv");
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
                parseVariables();
				scriptStarted = true;
			}
		});

		scriptFrame = new JFrame(controller.getPlayerName() + " - options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(label1);
		scriptFrame.add(label2);
		scriptFrame.add(startScriptButton);
        scriptFrame.pack();
        scriptFrame.setLocationRelativeTo(null);  //need this for proper windows behavior
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
	    	int successPerHr = 0;
	    	int tripSuccessPerHr = 0;
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		successPerHr = (int)(totalLog * scale);
	    		tripSuccessPerHr = (int)(totalTrips * scale);
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Seers Magic Logs @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Logs in Bank: @gre@" + String.valueOf(this.logInBank), 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Logs Cut: @gre@" + String.valueOf(this.totalLog) + "@yel@ (@whi@" + String.format("%,d", successPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", tripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 104, 0xFFFFFF, 1);

		}
	}
}
