package scripting.idlescript;

import bot.Main;
import controller.Controller;
import orsc.ORSCharacter;

import java.awt.GridLayout;
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
    Controller c = Main.getController();
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
        if(c.isInBank() == true) {
            c.closeBank();
        }
    }
	public int start(String parameters[]) {
        if (parameters.length > 0 && !parameters[0].equals("")) {
            if (parameters[0].toLowerCase().startsWith("auto")) {
                c.displayMessage("Got param " + parameters[0] + ", Auto-starting Steel Plates", 0);
                System.out.println("Got param" + parameters[0] + ", Auto-starting Steel Plates");
                barId = 171;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("bronze")) {
                c.displayMessage("Got param " + parameters[0] + ". Using bronze bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using bronze bars");
                barId = 169;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("iron")) {
                c.displayMessage("Got param " + parameters[0] + ". Using iron bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using iron bars!");
                barId = 170;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("steel")) {
                c.displayMessage("Got param " + parameters[0] + ". Using steel bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using steel bars!");
                barId = 171;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("mith")
                    || parameters[0].toLowerCase().startsWith("mithril")) {
                c.displayMessage("Got param " + parameters[0] + ". Using mith bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using mithril bars!");
                barId = 173;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("addy")
                    || parameters[0].toLowerCase().startsWith("adamantite")) {
                c.displayMessage("Got param " + parameters[0] + ". Using addy bars!", 0);
                System.out.println("Got param" + parameters[0] + ", Using adamantite bars!");
                barId = 174;
                parseVariables();
                startSequence();
                scriptStart();
            }
            if (parameters[0].toLowerCase().startsWith("rune")
                    || parameters[0].toLowerCase().startsWith("runite")) {
                c.displayMessage("Got param " + parameters[0] + ". Using rune bars!", 0);
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
            c.quitIfAuthentic();
            startSequence();
			scriptStart();
		}
		return 1000; //start() must return a int value now.
	}
public void scriptStart() {
	while(c.isRunning()) {
		if(c.getInventoryItemCount(barId) < 5 && !c.isInBank()) {
			c.setStatus("@gre@Banking..");
            c.displayMessage("@gre@Banking..");
			c.walkTo(150,507);
			bank();
			c.walkTo(148,512);
		}
		if(c.getInventoryItemCount(barId) > 4) {
			c.sleepHandler(98, true);
			c.setStatus("@gre@Smithing..");
            c.displayMessage("@gre@Smithing..");
			c.useItemIdOnObject(148, 513, barId);
			c.sleep(1000);
			c.optionAnswer(1);
			c.sleep(500);
			c.optionAnswer(2);
			c.sleep(500);
			c.optionAnswer(2);
			c.sleep(500);
			if (!c.isAuthentic()) {
				c.optionAnswer(3);
				c.sleep(1000); //was 650
				while (c.isBatching()) c.sleep(200);
			}
		}
		//c.sleep(320);
	}
}
	public void bank() {

		c.setStatus("@gre@Banking..");
		c.openBank();
		c.sleep(640);
        if(!c.isInBank()) { //attempt to fix desync issue, remove later
            c.walkTo(150,502);
            c.openBank();
            c.sleep(1280);
        }
		if(c.isInBank()) {

			totalPlates = totalPlates + 5;
			totalBars = totalBars + 25;

			if(c.getBankItemCount(barId) < 30) {    //stops making when 30 in bank to not mess up alignments/organization of bank!!!
				c.setStatus("@red@NO Bars in the bank, Logging Out!.");
				c.setAutoLogin(false);
				c.logout();
				if(!c.isLoggedIn()) {
					c.stop();
					return;
				}
			}
			if(c.getInventoryItemCount() >  1) {
				for (int itemId : c.getInventoryItemIds()) {
					if (itemId != 168 && itemId != 1263 && itemId != barId) {
						c.depositItem(itemId, c.getInventoryItemCount(itemId));
					}
				}
				c.sleep(100);
			}
			if (c.getInventoryItemCount(168) < 1) {
				c.withdrawItem(168, 1);
				c.sleep(100);
			}
			if (c.getInventoryItemCount(barId) < 25) {
				c.withdrawItem(barId, 25);
				c.sleep(100);
			}

			barsInBank = c.getBankItemCount(barId);
			c.closeBank();
			c.sleep(200);
		}
	}



	//GUI stuff below (icky)
    public void parseVariables() {
        startTime = System.currentTimeMillis();
        c.displayMessage("@gre@" + '"' + "Fast Platebody Smither" + '"' + " - by Kaila");
        c.displayMessage("@gre@Start in Varrock West bank with a HAMMER");
        c.displayMessage("@red@REQUIRES Batch bars be toggle on in settings to work correctly!");
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

		scriptFrame = new JFrame(c.getPlayerName() + " - options");

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
		if (c != null) {
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
            int x = 6;
            int y = 15;
			c.drawString("@red@Fast Plate Smither @gre@by Kaila", x, 12, 0xFFFFFF, 1);
            c.drawString("@whi@________________________", x, y, 0xFFFFFF, 1);
			c.drawString("@whi@Bars In bank: @gre@" + this.barsInBank, x, y+14, 0xFFFFFF, 1);
			c.drawString("@whi@Bars Used: @gre@" + this.totalBars, x, y+(14*2), 0xFFFFFF, 1);
            c.drawString("@whi@Bars Per Hr: @gre@" + String.format("%,d", barSuccessPerHr) + "@yel@/@whi@hr", x, y+(14*3), 0xFFFFFF, 1);
			c.drawString("@whi@Platebodies Made: @gre@" + this.totalPlates, x, y+(14*4), 0xFFFFFF, 1);
            c.drawString("@whi@Platebodies Per Hr: @gre@" + String.format("%,d", plateSuccessPerHr) + "@yel@/@whi@hr", x, y+(14*5), 0xFFFFFF, 1);
            c.drawString("@whi@Time Remaining: " + toTimeToCompletion(totalBars, barsInBank, startTime), x, y+(14*6), 0xFFFFFF, 1);
            c.drawString("@whi@Runtime: " + runTime, x, y+(14*7), 0xFFFFFF, 1);
            c.drawString("@whi@__________________", x, y+3+(14*7), 0xFFFFFF, 1);
		}
	}
}
