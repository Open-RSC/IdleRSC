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
 * Fast Platebody Smither - by Kaila.
 *
 * 		Start with Hammer in Inv!
 * 		Batch Bars MUST be toggled ON in settings!!!
 * 		This ensures 5 Plates are made per Menu Cycle.
 * 		Supports all bar types.
 *
 *        This bot supports the \"autostart\" parameter.
 *                  defaults to steel plates.
 *
 * Parameters for Starting:
 *              auto, autostart  - makes steel platebodies.
 *              bronze  - makes bronze platebodies.
 *              iron -   makes iron platebodies.
 *              steel -  makes steel platebodies.
 *              mith, mithril -   makes mithril platebodies.
 *              addy, adamantite -  makes adamantite platebodies.
 *              rune, runite -  makes runite platebodies.
 *
 *
 *
 * Author - Kaila
 *
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
    long craftTime;
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;

    public void startSequence() {
        if(controller.isInBank() == true) {
            controller.closeBank();
        }
    }
	public int start(String parameters[]) {
        if (parameters.length > 0 && !parameters[0].equals("")) {
            if (parameters[0].toLowerCase().startsWith("auto")) {
                controller.displayMessage("Got param " + parameters[0] + ", Auto-starting Steel Plates", 0);
                System.out.println("Got param" + parameters[0] + ", Auto-starting Steel Plates");
                barId = 171;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("bronze")) {
                controller.displayMessage("Got param " + parameters[0] + ". Using bronze bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using bronze bars");
                barId = 169;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("iron")) {
                controller.displayMessage("Got param " + parameters[0] + ". Using iron bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using iron bars!");
                barId = 170;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("steel")) {
                controller.displayMessage("Got param " + parameters[0] + ". Using steel bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using steel bars!");
                barId = 171;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("mith")
                    || parameters[0].toLowerCase().startsWith("mithril")) {
                controller.displayMessage("Got param " + parameters[0] + ". Using mith bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using mithril bars!");
                barId = 173;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("addy")
                    || parameters[0].toLowerCase().startsWith("adamantite")) {
                controller.displayMessage("Got param " + parameters[0] + ". Using addy bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using adamantite bars!");
                barId = 174;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("rune")
                    || parameters[0].toLowerCase().startsWith("runite")) {
                controller.displayMessage("Got param " + parameters[0] + ". Using rune bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using rune bars!");
                barId = 408;
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
		if(controller.getInventoryItemCount(barId) < 5 && !controller.isInBank()) {
			controller.setStatus("@gre@Banking..");
            controller.displayMessage("@gre@Banking..");
			controller.walkTo(150,507);
			bank();
			controller.walkTo(148,512);
		}
		if(controller.getInventoryItemCount(barId) > 4) {
			controller.sleepHandler(98, true);
			controller.setStatus("@gre@Smithing..");
            controller.displayMessage("@gre@Smithing..");
			controller.useItemIdOnObject(148, 513, barId);
			controller.sleep(1000);
			controller.optionAnswer(1);
			controller.sleep(500);
			controller.optionAnswer(2);
			controller.sleep(500);
			controller.optionAnswer(2);
			controller.sleep(500);
			if (!controller.isAuthentic()) {
				controller.optionAnswer(3);
				controller.sleep(1000); //was 650
				while (controller.isBatching()) controller.sleep(200);
			}
		}
		//controller.sleep(320);
	}
}
















	public void bank() {

		controller.setStatus("@gre@Banking..");
		controller.openBank();
		controller.sleep(600);

		if(controller.isInBank()) {

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
			if(controller.getInventoryItemCount() >  1) {
				for (int itemId : controller.getInventoryItemIds()) {
					if (itemId != 168 && itemId != 1263 && itemId != barId) {
						controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
					}
				}
				controller.sleep(100);
			}
			if (controller.getInventoryItemCount(168) < 1) {
				controller.withdrawItem(168, 1);
				controller.sleep(100);
			}
			if (controller.getInventoryItemCount(barId) < 25) {
				controller.withdrawItem(barId, 25);
				controller.sleep(100);
			}

			barsInBank = controller.getBankItemCount(barId);
			controller.closeBank();
			controller.sleep(200);
		}
	}



	//GUI stuff below (icky)



	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
    public void parseVariables() {
        startTime = System.currentTimeMillis();
        controller.displayMessage("@gre@" + '"' + "Fast Platebody Smither" + '"' + " - by Kaila");
        controller.displayMessage("@gre@Start in Varrock West bank with a HAMMER");
    }
	public void setupGUI() {
		JLabel header = new JLabel("Platebody Smithing - Kaila");
		JLabel hammerLabel = new JLabel("Start with Hammer in Inv!");
		JLabel batchLabel = new JLabel("Batch Bars MUST be toggled ON in settings!!!");
		JLabel batchLabel2 = new JLabel("This ensures 5 Plates are made per Menu Cycle.");
		JLabel barLabel = new JLabel("Bar Type:");
		JComboBox<String> barField = new JComboBox<String>(
				new String[] { "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite" });
        JLabel paramLabel3 = new JLabel("This bot supports the \"autostart\" parameter");
        JLabel paramLabel1 = new JLabel("Script can also be started with the Parameters:");
        JLabel paramLabel2 = new JLabel("\"Bronze\", \"Iron\", \"Steel\", \"Mith\", \"Addy\", \"Runite\"");
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				barId = barIds[barField.getSelectedIndex()];
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
		scriptFrame.add(hammerLabel);
		scriptFrame.add(batchLabel);
		scriptFrame.add(batchLabel2);
		scriptFrame.add(barLabel);
		scriptFrame.add(barField);
        scriptFrame.add(paramLabel3);
        scriptFrame.add(paramLabel1);
        scriptFrame.add(paramLabel2);
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
    /**
     * credit to chomp for toTimeToCompletion (from AA_Script) (totalBars, barsInBank, startTime)
     */
    public static String toTimeToCompletion(final int processed, final int remaining, final long time) {
        if (processed == 0) {
            return "0:00:00";
        }

        final double seconds = (System.currentTimeMillis() - time) / 1000.0;
        final double secondsPerItem = seconds / processed;
        final long ttl = (long) (secondsPerItem * remaining);
        return String.format("%d:%02d:%02d", ttl / 3600, (ttl % 3600) / 60, (ttl % 60));
    }
	@Override
	public void paintInterrupt() {
		if (controller != null) {
            String runTime = msToString(System.currentTimeMillis() - startTime);
        	int plateSuccessPerHr = 0;
            int barSuccessPerHr = 0;
        	try {
        		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        		float scale = (60 * 60) / timeRan;
        		plateSuccessPerHr = (int)(totalPlates * scale);
                barSuccessPerHr = (int)(totalBars * scale);
        	} catch(Exception e) {
        		//divide by zero
        	}
			controller.drawString("@red@Fast Plate Smither @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Bars In bank: @gre@" + this.barsInBank, 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Bars Used: @gre@" + this.totalBars, 350, 76, 0xFFFFFF, 1);
            controller.drawString("@whi@Bars Per Hr: @gre@" + String.format("%,d", barSuccessPerHr) + "@yel@/@whi@hr", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Platebodies Made: @gre@" + this.totalPlates, 350, 104, 0xFFFFFF, 1);
            controller.drawString("@whi@Platebodies Per Hr: @gre@" + String.format("%,d", plateSuccessPerHr) + "@yel@/@whi@hr", 350, 118, 0xFFFFFF, 1);
            controller.drawString("@whi@Time Remaining: " + toTimeToCompletion(totalBars, barsInBank, startTime), 350, 118+14, 0xFFFFFF, 1);
            controller.drawString("@whi@Runtime: " + runTime, 350, 118+28, 0xFFFFFF, 1);
		}
	}
}
