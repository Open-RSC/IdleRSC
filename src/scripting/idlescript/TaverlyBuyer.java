package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import orsc.ORSCharacter;
import scripting.idlescript.AIOCooker.FoodObject;

/**
 * Buys vials or newts in Taverly, banks in Falador
 *  
 * @author Dvorak
 */
public class TaverlyBuyer extends IdleScript {	
	String[] options = new String[] { "Vials", "Newts", "Vials then newts"};
	
	int[] doorToHerbPath = {342, 488,
							349, 487,
							355, 487,
							364, 488, 
							364, 496, 
							371, 506,
							370, 506};
	
	int[] bankToDoorPath = {327, 552,
							325, 544,
							323, 535,
							320, 526, 
							316, 518,
							316, 513,
							318, 507, 
							324, 501,
							329, 498,
							335, 493,
							338, 488,
							341, 488};
			
	
	int[] loot = {465, 270};
	
	int option = -1;
	boolean scriptStarted = false;
	boolean guiSetup = false;
	
	int vialsBought = 0;
	int vialsBanked = 0;
	int newtsBought = 0;
	int newtsBanked = 0;
	
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
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
		controller.displayMessage("@red@TaverlyBuyer by Dvorak. Let's party like it's 2004!");
		controller.displayMessage("@red@Start in Taverly with herb clippers!");
		controller.quitIfAuthentic();
		
		while(controller.isRunning()) {
			if(controller.getInventoryItemCount() < 30) {
				controller.setStatus("@gre@Buying stuff..");
				ORSCharacter npc = controller.getNearestNpcById(230, false);
				
				if(npc != null) {
					
					while(!controller.isInShop()) {
						if(controller.isAuthentic()) {
							controller.talkToNpc(npc.serverIndex);
							controller.sleep(2000);
							controller.optionAnswer(0);
							controller.sleep(1000);
						} else {
							controller.npcCommand1(npc.serverIndex);
							controller.sleep(1000);
						}
					}
					
					while(controller.getInventoryItemCount() < 30) {
						if(option == 0) {
							if(controller.getShopItemCount(465) > 0) { 
								controller.shopBuy(465, controller.getShopItemCount(465));
							}
						} else if(option == 1) {
							if(controller.getShopItemCount(270) > 0) { 
								controller.shopBuy(270, controller.getShopItemCount(270));
							}
						} else {
							if(controller.getShopItemCount(465) > 0) { 
								controller.shopBuy(465, controller.getShopItemCount(465));
								controller.sleep(250);
							}
							if(controller.getShopItemCount(270) > 0) { 
								controller.shopBuy(270, controller.getShopItemCount(270));
							}
						}
					}
					
				}
				
			} else {
				walkToBank();
				bank();
				walkToTaverly();
			}
			
			controller.sleep(100);
		}
	}
	
	public void walkToBank() {
		controller.setStatus("@gre@Walking to bank..");
		
		if(controller.currentX() < 363)
			controller.walkTo(358,  507);
		controller.walkPathReverse(doorToHerbPath);
		controller.sleep(1000);
		
		//open gate
		while(controller.currentX() != 341 || controller.currentY() != 487) {
			controller.displayMessage("@red@Opening door..");
			if(controller.getObjectAtCoord(341, 487) == 137)
				controller.atObject(341, 487);
			controller.sleep(5000);
		}
		
		controller.walkPathReverse(bankToDoorPath);
		
		//open bank door
		while(controller.getObjectAtCoord(327, 552) == 64) {
			controller.atObject(327, 552);
			controller.sleep(100);
		}
		
		controller.walkTo(328, 552);
	}
	
	public int countLoot() {
		int count = 0;
		for(int i = 0; i < loot.length; i++) {
			count += controller.getInventoryItemCount(loot[i]);
		}
		
		return count;
	}
	
	public void bank() {
		controller.setStatus("@gre@Banking..");
		
		controller.openBank();
		
		vialsBought += controller.getInventoryItemCount(465);
		newtsBought += controller.getInventoryItemCount(270);
		
		while(countLoot() > 0) { 
			for(int i = 0; i < loot.length; i++) {
				if(controller.getInventoryItemCount(loot[i]) > 0) {
					controller.depositItem(loot[i], controller.getInventoryItemCount(loot[i])); ///////////////////////////////
					controller.sleep(250);
				}
			}
		}
		
		vialsBanked = controller.getBankItemCount(465);
		newtsBanked = controller.getBankItemCount(270);
		
		
	}
	
	public void walkToTaverly() {
		controller.setStatus("@gre@Walking back to Taverly..");
		
		while(controller.getObjectAtCoord(327, 552) == 64) {
			controller.atObject(327, 552);
			controller.sleep(100);
		}
		
		controller.walkTo(327, 552);
		
		controller.walkPath(bankToDoorPath);
		controller.sleep(1000);
		
		//open door
		while(controller.currentX() != 342 || controller.currentY() != 487) {
			controller.displayMessage("@red@Opening door..");
			if(controller.getObjectAtCoord(341, 487) == 137)
				controller.atObject(341, 487);
			controller.sleep(5000);
		}
		
		controller.walkPath(doorToHerbPath);
	}
    
	public static void centerWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
	
    public void setupGUI() { 	
    	final JFrame scriptFrame = new JFrame("Script Options");
    	JLabel headerLabel = new JLabel("Start in Taverly with GP!");
    	JComboBox<String> optionField = new JComboBox<String>(options);
        JButton startScriptButton = new JButton("Start");
        
        startScriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            		option = optionField.getSelectedIndex();
            		
	            	scriptFrame.setVisible(false);
	            	scriptFrame.dispose();
	            	scriptStarted = true;
	            	
	            	controller.displayMessage("@red@AIOCooker by Dvorak. Let's party like it's 2004!");
            	}
        });
        
        
        
    	
    	
    	scriptFrame.setLayout(new GridLayout(0,1));
    	scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	scriptFrame.add(headerLabel);
    	scriptFrame.add(optionField);
    	scriptFrame.add(startScriptButton);
    		
    	centerWindow(scriptFrame);
    	scriptFrame.setVisible(true);
    	scriptFrame.pack();
    }
    
    @Override
    public void paintInterrupt() {
        if(controller != null) {
        			
        	int vialsPerHr = 0;
        	int newtsPerHr = 0;
        	try {
        		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        		float scale = (60 * 60) / timeRan;
        		vialsPerHr = (int)(vialsBought * scale);
        		newtsPerHr = (int)(newtsBought * scale);
        	} catch(Exception e) {
        		//divide by zero
        	}
        	
        	int height = 21 + 14 + 14;
        	if(option == 2) {
        		height += 14 + 14;
        	}
        	
            controller.drawBoxAlpha(7, 7, 160, height, 0xFFFFFF, 128);
            controller.drawString("@gre@TaverlyBuyer @whi@by @gre@Dvorak", 10, 21, 0xFFFFFF, 1);
            
            if(option == 0) {
	            controller.drawString("@gre@Vials bought: @whi@" + String.format("%,d", vialsBought) + " @gre@(@whi@" + String.format("%,d", vialsPerHr) + "@gre@/@whi@hr@gre@)", 10, 21+14, 0xFFFFFF, 1);
	            controller.drawString("@gre@Vials in bank: @whi@" + String.format("%,d", vialsBanked), 10, 21+14+14, 0xFFFFFF, 1);
            } else if(option == 1) {
	            controller.drawString("@gre@Newts bought: @whi@" + String.format("%,d", newtsBought) + " @gre@(@whi@" + String.format("%,d", newtsPerHr) + "@gre@/@whi@hr@gre@)", 10, 21+14, 0xFFFFFF, 1);
	            controller.drawString("@gre@Newts in bank: @whi@" + String.format("%,d", newtsBanked), 10, 21+14+14, 0xFFFFFF, 1);
            } else {
	            controller.drawString("@gre@Vials bought: @whi@" + String.format("%,d", vialsBought) + " @gre@(@whi@" + String.format("%,d", vialsPerHr) + "@gre@/@whi@hr@gre@)", 10, 21+14, 0xFFFFFF, 1);
	            controller.drawString("@gre@Vials in bank: @whi@" + String.format("%,d", vialsBanked), 10, 21+14+14, 0xFFFFFF, 1);
	            controller.drawString("@gre@Newts bought: @whi@" + String.format("%,d", newtsBought) + " @gre@(@whi@" + String.format("%,d", newtsPerHr) + "@gre@/@whi@hr@gre@)", 10, 21+14+14+14, 0xFFFFFF, 1);
	            controller.drawString("@gre@Newts in bank: @whi@" + String.format("%,d", newtsBanked), 10, 21+14+14+14+14, 0xFFFFFF, 1);
            }
        }
    }

}
