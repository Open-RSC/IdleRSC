package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import orsc.ORSCharacter;

public class AKMiner extends IdleScript {
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
    
    MiningObject target = null;
    int fightMode = 0;
    int eatingHealth = 0;
    
	int[] oreIds = {150, 202, 151, 152, 153, 154, 155, 149, 157, 158, 159, 160};
    
    class MiningObject {
		String name;
		int rockId;
		
		public MiningObject(String _name, int _rockId) {
			name = _name;
			rockId = _rockId;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof MiningObject) {
				if(((MiningObject)o).name.equals(this.name)) {
					return true;
				}
			}
		
			return false;
		}
	}
	
	ArrayList<MiningObject> objects = new ArrayList<MiningObject>() {{
		add(new MiningObject("Copper", 100));
		add(new MiningObject("Tin", 104));
		add(new MiningObject("Iron", 102));
		add(new MiningObject("Silver", 195));
		add(new MiningObject("Coal", 110));
		add(new MiningObject("Gold", 112));
		add(new MiningObject("Mithril", 106));
		add(new MiningObject("Adamantite", 108));
	}};
	
	
	public void start(String parameters[]) {
		if(!guiSetup) {
    		setupGUI();
    		guiSetup = true;
    	}
    	
    	if(scriptStarted) {
    		scriptStart();
    	}
	}
	
	public void scriptStart() {
		while(controller.isRunning()) {
			if(controller.getInventoryItemCount() == 30) {
				walkToBank();
				bank();
				walkToMine();
			} else {
				
				while(controller.isBatching()) controller.sleep(10);
				
				int[] objCoord = controller.getNearestObjectById(target.rockId);
				if(objCoord != null) {
					controller.objectAt(objCoord[0], objCoord[1], 0, target.rockId);
				}
				
				controller.sleep(618);
			}
		}
	}
	
	
	public void openDoor() {
		while(controller.getObjectAtCoord(86, 695) == 64) {
			controller.objectAt(86, 695, 0, 64);
			controller.sleep(100);
		}
	}
	
	public void walkToBank() {		
		controller.walkTo(71, 594);
		controller.walkTo(70, 609);
		controller.walkTo(71, 629);
		controller.walkTo(75, 646);
		controller.walkTo(79, 667);
		controller.walkTo(81, 683);
		controller.walkTo(86, 695);
		
		openDoor();
	}
	
	public void walkToMine() {
		
		openDoor();
		

		
		controller.walkTo(86, 695);
		controller.walkTo(81, 683);
		controller.walkTo(79, 667);
		controller.walkTo(75, 646);
		controller.walkTo(71, 629);
		controller.walkTo(70, 606);
		controller.walkTo(71, 594);
	}
	
	public void bank() {
		while(controller.isInBank() == false) {
			ORSCharacter npc = controller.getNearestNpcById(268, false);
			
			while(controller.isInOptionMenu() == false) {
				controller.talkToNpc(npc.serverIndex);
				controller.sleep(3000);
			}
			
			controller.optionAnswer(0);
			
			controller.sleep(5000);
		}
		
		for(int ore : this.oreIds) {
			if(controller.getInventoryItemCount(ore) > 0) {
				controller.depositItem(ore, controller.getInventoryItemCount(ore));
				controller.sleep(250);
			}
		}
		
	}

	
	public static void centerWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
	
    public void setupGUI() { 	
    	JLabel headerLabel = new JLabel("Start in Al-Kharid mine with your pickaxe!");
    	JComboBox<String> targetField = new JComboBox<String>();  
        JButton startScriptButton = new JButton("Start");
        
        for(MiningObject obj : objects) {
        	targetField.addItem(obj.name);
        }
        
        startScriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            		target = objects.get(targetField.getSelectedIndex());
	            	scriptFrame.setVisible(false);
	            	scriptFrame.dispose();
	            	scriptStarted = true;
	            	
	            	controller.displayMessage("@red@AKMiner by Dvorak. Let's party like it's 2004!");
            	}
        });
        
        
        
    	scriptFrame = new JFrame("Script Options");
    	
    	scriptFrame.setLayout(new GridLayout(0,1));
    	scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	scriptFrame.add(headerLabel);
    	scriptFrame.add(targetField);
    	scriptFrame.add(startScriptButton);
    		
    	centerWindow(scriptFrame);
    	scriptFrame.setVisible(true);
    	scriptFrame.pack();
    }
    	
}
