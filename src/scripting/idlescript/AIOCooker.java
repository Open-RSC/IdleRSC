package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import orsc.ORSCharacter;

import static bot.Main.log;

/**
 * A basic cooking script to use in Catherby.
 *
 * @author Dvorak
 */
public class AIOCooker extends IdleScript {
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	int success = 0;
	int failure = 0;
    FoodObject target = null;
    boolean dropBurnt = true;
    boolean gauntlets = true;

	ArrayList<FoodObject> objects = new ArrayList<FoodObject>() {{
		add(new FoodObject("Chicken", 133, 132, 134)); //raw, cooked, burnt
		add(new FoodObject("Shrimp", 349, 350, 353));
		add(new FoodObject("Anchovies", 351, 352, 353));
		add(new FoodObject("Sardine", 351, 355, 360));
		add(new FoodObject("Salmon", 356, 357, 360));
		add(new FoodObject("Trout", 358, 359, 360));
		add(new FoodObject("Herring", 361, 362, 365));
		add(new FoodObject("Pike", 363, 364, 365));
		add(new FoodObject("Cod", 550, 551, 360)); //pointed
		add(new FoodObject("Mackerel", 552, 553, 365)); //not pointed
		add(new FoodObject("Tuna", 366, 367, 368));
		add(new FoodObject("Lobster", 372, 373, 374));
		add(new FoodObject("Swordfish", 369, 370, 371));
		add(new FoodObject("Bass", 554, 555, 368));
		add(new FoodObject("Shark", 545, 546, 547));
		add(new FoodObject("Sea Turtle", 1192, 1193, 1248));
		add(new FoodObject("Manta Ray", 1190, 1191, 1247));
	}};
    class FoodObject {
        String name;
        int rawId;
        int cookedId;
        int burntId;

        public FoodObject(String _name, int _rawId, int _cookedId, int _burntId) {
            name = _name;
            rawId = _rawId;
            cookedId = _cookedId;
            burntId = _burntId;
        }

        public FoodObject (String name) {
            for (FoodObject food : objects) {
                if (food.name.equalsIgnoreCase(name)) {
                    name = food.name;
                    rawId = food.rawId;
                    cookedId = food.cookedId;
                    burntId = food.burntId;
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof FoodObject) {
                if(((FoodObject)o).name.equals(this.name)) {
                    return true;
                }
            }

            return false;
        }
    }
	public int start(String parameters[]) {
		String[] splitParams = null;
		if(parameters != null && parameters[0].contains(" ")) {
            splitParams = parameters[0].split(" ");
        }
		if (splitParams == null || splitParams.length < 3) {
			if(!guiSetup) {
				setupGUI();
				guiSetup = true;
			}

			if(scriptStarted) {
				log("Equivalent parameters: ");
				log(target.name + " " + dropBurnt + " " + gauntlets);
				scriptStart();
			}
		} else {
			try {
				target = new FoodObject(splitParams[0]);
				dropBurnt = Boolean.parseBoolean(splitParams[1]);
				gauntlets = Boolean.parseBoolean(splitParams[2]);

				scriptStart();
			} catch (Exception e) {
				log("Invalid parameters! Usage: ");
				log("foodname true true");
				controller.stop();
			}
		}

		return 1000; //start() must return a int value now.
	}

	public void scriptStart() {
		while(controller.isRunning()) {

			if(controller.getInventoryItemCount(target.rawId) == 0) {
				bank();
			} else {
				cook();
			}

			controller.sleep(250);

		}
	}

	public void bank() {

		controller.walkTo(439, 497);
		openDoor();

		controller.openBank();


		if(controller.getInventoryItemCount(target.cookedId) > 0) {
			controller.depositItem(target.cookedId, controller.getInventoryItemCount(target.cookedId));
			controller.sleep(250);
		}
		if(!this.dropBurnt) {
			if(controller.getInventoryItemCount(target.burntId) > 0) {
				controller.depositItem(target.burntId, controller.getInventoryItemCount(target.burntId));
				controller.sleep(250);
			}
		}

		if(this.gauntlets && controller.getInventoryItemCount(700) == 0) {
			controller.withdrawItem(700);
			controller.sleep(250);
		}

		if(controller.getInventoryItemCount(target.rawId) == 0) {
			controller.withdrawItem(target.rawId, 30);
			controller.sleep(250);
		}


		controller.walkTo(439, 496);
		openDoor();
	}

	public void cook() {

		controller.walkTo(435, 486);
		openCookDoor();

		if(this.gauntlets == true) {

			if(controller.getInventoryItemCount(700) < 1) {
				controller.displayMessage("@red@Please withdraw gauntlets. Stopping script.");
				controller.stop();
				return;
			}

			if(!controller.isEquipped(controller.getInventoryItemSlotIndex(700))) {
				controller.equipItem(controller.getInventoryItemSlotIndex(700));
				controller.sleep(618);
			}
		}

		while(controller.getInventoryItemCount(target.rawId) > 0) {

			if(controller.isBatching() == false)
				controller.useItemIdOnObject(432, 480, target.rawId);

			controller.sleepHandler(98, true);

			controller.sleep(250);
		}

		if(this.dropBurnt) {
			while(controller.getInventoryItemCount(target.burntId) > 0) {
				controller.dropItem(controller.getInventoryItemSlotIndex(target.burntId));
				controller.sleep(250);
			}
		}

		controller.walkTo(435, 485);
		openCookDoor();
	}

	public void openDoor() {
		while(controller.getObjectAtCoord(439, 497) == 64) {
			controller.atObject(439, 497);
			controller.sleep(100);
		}
	}

	public void openCookDoor() {
		while(!controller.isDoorOpen(435, 486)) {
			controller.openDoor(435, 486);
		}
	}

    public static void centerWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
    public void setupGUI() {
    	JLabel headerLabel = new JLabel("Start in Catherby!");
    	JComboBox<String> foodField = new JComboBox<String>();
    	JCheckBox dropBurntCheckbox = new JCheckBox("Drop Burnt?", true);
    	JCheckBox gauntletsCheckbox = new JCheckBox("Cooking Gauntlets?", true);


        JButton startScriptButton = new JButton("Start");

        for(FoodObject obj : objects) {
        	foodField.addItem(obj.name);
        }

        startScriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            		dropBurnt = dropBurntCheckbox.isSelected();
            		gauntlets = gauntletsCheckbox.isSelected();
            		target = objects.get(foodField.getSelectedIndex());

	            	scriptFrame.setVisible(false);
	            	scriptFrame.dispose();
	            	scriptStarted = true;

	            	controller.displayMessage("@red@AIOCooker by Dvorak. Let's party like it's 2004!");
            	}
        });



    	scriptFrame = new JFrame("Script Options");

    	scriptFrame.setLayout(new GridLayout(0,1));
    	scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	scriptFrame.add(headerLabel);
    	scriptFrame.add(foodField);
    	scriptFrame.add(dropBurntCheckbox);
    	scriptFrame.add(gauntletsCheckbox);
    	scriptFrame.add(startScriptButton);

    	centerWindow(scriptFrame);
    	scriptFrame.setVisible(true);
    	scriptFrame.pack();
    }

    @Override
    public void serverMessageInterrupt(String message) {
    	if(message.contains("nicely cooked"))
    		success++;
    }

    @Override
    public void questMessageInterrupt(String message) {
    	if(message.contains("burn the"))
        	failure++;
    }

    @Override
    public void paintInterrupt() {
        if(controller != null) {

        	int successPerHr = 0;
        	float ratio = 0;
        	try {
        		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        		float scale = (60 * 60) / timeRan;
        		successPerHr = (int)(success * scale);
        		ratio = (float)success / (float)failure;
        	} catch(Exception e) {
        		//divide by zero
        	}

            controller.drawBoxAlpha(7, 7, 160, 21+14+14+14, 0xFF0000, 128);
            controller.drawString("@red@AIOCooker @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
            controller.drawString("@red@Successes: @whi@" + String.format("%,d", success) + " @red@(@whi@" + String.format("%,d", successPerHr) + "@red@/@whi@hr@red@)", 10, 21+14, 0xFFFFFF, 1);
            controller.drawString("@red@Failures: @whi@" + String.format("%,d", failure), 10, 21+14+14, 0xFFFFFF, 1);
            controller.drawString("@red@Ratio: @whi@" + String.format("%.2f", ratio), 10, 21+14+14+14, 0xFFFFFF, 1);
        }
    }

}
