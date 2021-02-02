package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This is AIOSmelter written for IdleRSC.
 * 
 * It is your standard Falador smelter script.
 * 
 * It has the following features:
 * 
 * 	* GUI
 *  * All bars smeltable 
 *  * Goldsmithing gauntlets support
 *  * Cannonball support
 *  
 * 
 * @author Dvorak
 */

public class AIOSmelter extends IdleScript {

	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	
	int barId = -1;
	int[] barIds = {169, 170, 384, 171, 1041, 172, 173, 174, 408};
	
	Map<Integer, Map<Integer, Integer>> ingredientsMapping = new HashMap<Integer, Map<Integer, Integer>>()
			{{
				put(169, new HashMap<Integer, Integer>() {{put(150, 15); put(202, 15); }}); //bronze needs 1 copper and 1 tin
				put(170, new HashMap<Integer, Integer>() {{put(151, 30); }}); //iron needs 1 iron ore
				put(384, new HashMap<Integer, Integer>() {{put(383, 30); }}); //silver needs 1 silver ore
				put(171, new HashMap<Integer, Integer>() {{put(151, 10); put(155, 20); }}); //steel needs 1 iron 2 coal
				put(1041, new HashMap<Integer, Integer>() {{put(1057, 1); put(171, 29);  }});
				put(172, new HashMap<Integer, Integer>() {{put(152, 29); put(699, 1); }}); //gold needs 1 gold ore
				put(173, new HashMap<Integer, Integer>() {{put(153, 6); put(155, 24); }});
				put(174, new HashMap<Integer, Integer>() {{put(154, 4); put(155, 24); }});
				put(408, new HashMap<Integer, Integer>() {{put(409, 3); put(155, 24); }});
			}};			
			
	Map<Integer, Integer> ingredients = null;
	
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
		if(isEnoughOre()) {
			controller.walkTo(318, 551, 0, true);
			controller.walkTo(311, 545, 0, true);
			
			int oreId = ingredients.entrySet().iterator().next().getKey();
			
			if(oreId == 1057) { //do not use the cannonball mold on the furnace!
				oreId = 171;
			}
			
			while(controller.getInventoryItemCount(oreId) > 0) {
				
				while(controller.isBatching()) controller.sleep(10);
				
				if(controller.getInventoryItemCount(699) > 0) { //wield gauntlets
					controller.equipItem(controller.getInventoryItemIdSlot(699));
					controller.sleep(618);
				}
				

				
				controller.useItemIdOnObject(310, 546, oreId);
				if(oreId == 171)
					controller.sleep(3000); //cannonballs take way longer and can be interrupted by starting another one.
				else
					controller.sleep(618);
				
				while(controller.isBatching()) controller.sleep(10);
			}
			
		} else {
			controller.walkTo(318, 551, 0, true);
			controller.walkTo(329, 553, 0, true);
			
			controller.openBank();
			
			while(controller.getInventoryItemCount() > 0) {
				for(int itemId : controller.getInventoryItemIds()) {
					if(itemId != 0) {
						controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
					}
				}
				
				controller.sleep(618);
			}
			
			for(Map.Entry<Integer, Integer> entry : ingredients.entrySet()) {
				controller.withdrawItem(entry.getKey(), entry.getValue());
				controller.sleep(618);
			}
			
			//while(controller.isInBank()) controller.closeBank();
			
		}
		
	}
	
	public boolean isEnoughOre() {
		for(Map.Entry<Integer, Integer> entry : ingredients.entrySet()) {
			if(controller.getInventoryItemCount(entry.getKey()) < entry.getValue())
				return false;
		}
		
		return true;
	}
	
	public static void centerWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
	
    public void setupGUI() { 	
    	JLabel header = new JLabel("Start in Falador bank!");
    	JLabel barLabel = new JLabel("Bar Type:");
    	JComboBox<String> barField = new JComboBox<String>(new String[] {"Bronze Bar", "Iron Bar", "Silver Bar", "Steel Bar", "Cannonball", "Gold Bar", "Mithril Bar", "Adamantite Bar", "Runite Bar"});
        JButton startScriptButton = new JButton("Start");
        
        startScriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            		barId = barIds[barField.getSelectedIndex()];
            		ingredients = ingredientsMapping.get(barId);
	            	scriptFrame.setVisible(false);
	            	scriptFrame.dispose();
	            	scriptStarted = true;
	            	
	            	controller.displayMessage("@red@AIOSmelter by Dvorak. Let's party like it's 2004!");
            	}
        });
        
        
        
    	scriptFrame = new JFrame("Script Options");
    	
    	scriptFrame.setLayout(new GridLayout(0,1));
    	scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	scriptFrame.add(header);
    	scriptFrame.add(barLabel);
    	scriptFrame.add(barField);
    	scriptFrame.add(startScriptButton);
    		
    	centerWindow(scriptFrame);
    	scriptFrame.setVisible(true);
    	scriptFrame.pack();
    }
    
}