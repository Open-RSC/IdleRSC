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

public class AIOThiever extends IdleScript {
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;

	int[] lootIds = {10, 41, 333, 335, 330, 619, 38, 152, 612, 142, 161};
	int[] doorObjectIds = {60, 64};

	
    class ThievingObject {
		String name;
		int id;
		boolean isNpc;
		boolean isObject;
		
		public ThievingObject(String _name, int _id, boolean _isNpc, boolean _isObject) {
			name = _name;
			id = _id;
			isNpc = _isNpc;
			isObject = _isObject;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof ThievingObject) {
				if(((ThievingObject)o).name.equals(this.name)) {
					return true;
				}
			}
		
			return false;
		}
	}
    
    ThievingObject target = null;
    int fightMode = 0;
    int eatingHealth = 0;
    boolean doBank = false;
    int foodWithdrawAmount = 0;
    
	ArrayList<ThievingObject> objects = new ArrayList<ThievingObject>() {{
		add(new ThievingObject("Man", 11, true, false));
		add(new ThievingObject("Farmer", 63, true, false));
		add(new ThievingObject("Warrior", 86, true, false));
		add(new ThievingObject("Workman", 722, true, false));
		add(new ThievingObject("Rogue", 342, true, false));
		add(new ThievingObject("Guard", 321, true, false));
		add(new ThievingObject("Guard (Varrock)", 65, true, false));
		add(new ThievingObject("Knight", 322, true, false));
		add(new ThievingObject("Watchman", 574, true, false));
		add(new ThievingObject("Paladin", 323, true, false));
		add(new ThievingObject("Gnome", 592, true, false));
		add(new ThievingObject("Hero", 324, true, false));
		
		add(new ThievingObject("Tea Stall", 1183, false, true));
		add(new ThievingObject("Bakers Stall", 322, false, true));
		add(new ThievingObject("Bakers Stall (Banking)", 322, false, true));
		//add(new ThievingObject("Rock Cake Stall", , false, true)); //be my guest
		add(new ThievingObject("Silk Stall", 323, false, true));
		add(new ThievingObject("Fur Stall", 324, false, true));
		add(new ThievingObject("Silver Stall", 325, false, true));
		add(new ThievingObject("Spice Stall", 326, false, true));
		add(new ThievingObject("Gem Stall", 327, false, true));
		
		//add(new ThievingObject("10 Coin Chest", 327, false, true)); //who's gonna bother?
		add(new ThievingObject("Nature Rune Chest", 335, false, true));
		add(new ThievingObject("50 Coin Chest", 336, false, true));
		add(new ThievingObject("Hemenster Chest", 379, false, true));

	}};
	
	public void start(String parameters[]) {
    	if(scriptStarted) {
    		scriptStart();
    	} else {
    		if(parameters[0].equals("")) {
    			if(!guiSetup) {
    	    		setupGUI();
    	    		guiSetup = true;
    	    	}
    		} else {
    			try {
    				fightMode = Integer.parseInt(parameters[0]);
    				eatingHealth = Integer.parseInt(parameters[1]);
    				
    				for(ThievingObject obj : objects) {
    					if(obj.name.equals(parameters[2]))
    						target = obj;
    				}
    				
    				doBank = Boolean.parseBoolean(parameters[3]);
    				foodWithdrawAmount = Integer.parseInt(parameters[4]);
    				
    				if(target == null)
    					throw new Exception("Could not parse thieving target!");
    				
    				scriptStarted = true;
    				controller.displayMessage("@red@AIOThiever by Dvorak. Let's party like it's 2004!");
    				
    			} catch(Exception e) {
    				System.out.println("Could not parse parameters!");
    				controller.displayMessage("@red@Could not parse parameters!");
    			}
    		}
    	}
	}
	
	public void scriptStart() {
		while(controller.isRunning()) {
			
			if(controller.getFightMode() != this.fightMode)
				controller.setFightMode(this.fightMode);
			
			for(int doorId : doorObjectIds) {
				int[] doorCoords = controller.getNearestObjectById(doorId);
				
				if(doorCoords != null){
					controller.displayMessage("@red@AIOThiever: Opening door...");
					controller.atObject(doorCoords[0], doorCoords[1]);
					controller.sleep(5000);
				}
				
			}
			
			if(controller.getInventoryItemCount(140) > 0) { //drop jugs from heroes
				controller.dropItem(controller.getInventoryItemIdSlot(140));
				controller.sleep(500);
			}
			
			while(controller.isBatching()) controller.sleep(10);
			
			
			if(!controller.isInCombat()) { 
				if(target.isNpc == true) {
					ORSCharacter npc = controller.getNearestNpcById(target.id, false);
					if(npc != null && npc.serverIndex > 0)
						controller.npcCommand1(npc.serverIndex);
				}
				
				if(doBank) {
					if(target.name.contains("Bakers")) {
						if(controller.getInventoryItemCount() < 30) {
							if(controller.currentX() != 543 && controller.currentZ() != 600)
								controller.walkTo(543, 600);
								
							controller.objectAt(544, 599, 0, 322);
						} 
					}
					
					if(controller.getInventoryItemCount() == 30 || countFood() == 0) {
						controller.displayMessage("@red@Banking...");
						controller.walkTo(548, 589);
						controller.walkTo(547, 607);
						controller.openBank();
						
						for(int id : lootIds) {
							if(controller.getInventoryItemCount(id) > 0) {
								controller.depositItem(id, controller.getInventoryItemCount(id));
								controller.sleep(500);
							}
						}
						
						for(int id : controller.getFoodIds()) {
							if(controller.getInventoryItemCount(id) > 0) {
								controller.depositItem(id, controller.getInventoryItemCount(id));
								controller.sleep(500);
							}
						}
						
//						int withdrawn = 0;
//						while(countFood() < foodWithdrawAmount) {
//							for(int id : controller.getFoodIds()) {
//								if(controller.getBankItemCount(id) > 0) {
//									controller.withdrawItem(id, ++withdrawn);
//									controller.sleep(500);
//									break;
//								}
//							}
//						}
//						int withdrawn = 0;
//						while(countFood() < foodWithdrawAmount) {
						for(int id : controller.getFoodIds()) {
							if(controller.getBankItemCount(id) > 0) {
								controller.withdrawItem(id, foodWithdrawAmount);
								controller.sleep(500);
								break;
							}
						}
//						}					
						
						
					}
					
				} else {
				
					if(target.isObject == true) {
						int[] coords = controller.getNearestObjectById(target.id);
						if(coords != null) {
							if(target.name.contains("Chest")) {
								controller.objectAt2(coords[0], coords[1], 0, target.id);
							} else {
								controller.objectAt(coords[0], coords[1], 0, target.id);
								controller.sleep(618);
							}
						}
					}

				}
				
			} else {
				controller.walkTo(controller.currentX(), controller.currentZ(), 0, true);
				
				controller.sleep(400);
				
	    		if(controller.getCurrentStat(controller.getStatId("Hits")) <= eatingHealth) {
	    			controller.displayMessage("@red@AIOThiever: Eating food");
	    			controller.walkTo(controller.currentX(), controller.currentZ(), 0, true);
	    			
	    			boolean ate = false;
	    			
	    			for(int id : controller.getFoodIds()) {
	    				if(controller.getInventoryItemCount(id) > 0) {
	    					controller.itemCommand(id);
	    					ate = true;
	    					break;
	    				}
	    			}
	    			
	    			while(!doBank && !ate) {
	    				controller.displayMessage("@red@AIOThiever: We ran out of food! Logging out.");
	    				controller.setAutoLogin(false);
	    				controller.logout();
	    				
	    				controller.sleep(1000);
	    			}
	    			
	    			continue;
	    		}
				
			}
			
			controller.sleep(250);
			
		}
	}

	public int countFood() {
		int result = 0;
		for(int id : controller.getFoodIds()) {
			result += controller.getInventoryItemCount(id);
		}
		
		return result;
	}
	
	public static void centerWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
	
    public void setupGUI() { 	
    	JLabel fightModeLabel = new JLabel("Fight Mode:");
    	JComboBox<String> fightModeField = new JComboBox<String>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    	JLabel eatAtHpLabel = new JLabel("Eat at HP: (food is automatically detected)");
    	JTextField eatAtHpField = new JTextField(String.valueOf(controller.getBaseStat(controller.getStatId("Hits")) / 2));
    	JComboBox<String> targetField = new JComboBox<String>();  
    	JCheckBox doBankCheckbox = new JCheckBox("Bank? (Ardougne Square only)");
    	JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount: (Ardougne Square only)");
    	JTextField foodWithdrawAmountField = new JTextField();
        JButton startScriptButton = new JButton("Start");

        
        for(ThievingObject obj : objects) {
        	targetField.addItem(obj.name);
        }
        
        startScriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            		fightMode = fightModeField.getSelectedIndex();
            		eatingHealth = Integer.parseInt(eatAtHpField.getText());
            		target = objects.get(targetField.getSelectedIndex());
            		doBank = doBankCheckbox.isSelected();
            		
            		if(!foodWithdrawAmountField.getText().equals(""))
            			foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
            		
	            	scriptFrame.setVisible(false);
	            	scriptFrame.dispose();
	            	scriptStarted = true;
	            	
	            	controller.displayMessage("@red@AIOThiever by Dvorak. Let's party like it's 2004!");
            	}
        });
        
        
        
    	scriptFrame = new JFrame("Script Options");
    	
    	scriptFrame.setLayout(new GridLayout(0,1));
    	scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	scriptFrame.add(fightModeLabel);
    	scriptFrame.add(fightModeField);
    	scriptFrame.add(eatAtHpLabel);
    	scriptFrame.add(eatAtHpField);
    	scriptFrame.add(targetField);
    	scriptFrame.add(doBankCheckbox);
    	scriptFrame.add(foodWithdrawAmountLabel);
    	scriptFrame.add(foodWithdrawAmountField);
    	scriptFrame.add(startScriptButton);
    	
    	centerWindow(scriptFrame);
    	scriptFrame.setVisible(true);
    	scriptFrame.pack();
    }
    	
}
