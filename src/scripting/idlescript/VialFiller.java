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
public class VialFiller extends IdleScript {	
	int vialsFilled = 0;
	int fullVials = 0;
	int emptyVials = 0;
	
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	public void start(String parameters[]) {
		controller.displayMessage("@red@VialFiller by Dvorak. Let's party like it's 2004!");
		controller.displayMessage("@red@Start in Falador West bank!");
		
		while(controller.isRunning()) {
			if(controller.getInventoryItemCount(465) > 0) {
				controller.useItemIdOnObject(327, 545, 465);
				controller.sleep(618);
				
				while(controller.isBatching()) controller.sleep(10);
			} else {
				openDoor();
				bank();
				openDoor();
			}
		}
	}
	
	public void openDoor() {
		int[] coords = null;
		do {
			coords = controller.getNearestObjectById(64);
			if(coords != null) {
				controller.atObject(coords[0], coords[1]);
				controller.sleep(618);
			}
		} while(coords != null);
	}
	
	public void bank() {
		controller.setStatus("@blu@Banking..");
		
		controller.openBank();
		
		vialsFilled += controller.getInventoryItemCount(464);
		
		while(controller.isInBank() && controller.getInventoryItemCount(464) >  0) {
			controller.depositItem(464, controller.getInventoryItemCount(464));
			controller.sleep(100);
		}
		
		while(controller.isInBank() && controller.getInventoryItemCount(465) < 1) {
			controller.withdrawItem(465, 30 - controller.getInventoryItemCount());
			controller.sleep(100);
		}
		
		
		fullVials = controller.getBankItemCount(464);
		emptyVials = controller.getBankItemCount(465);
	}
	
    @Override
    public void paintInterrupt() {
        if(controller != null) {
        			
        	int vialsPerHr = 0;
        	try {
        		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        		float scale = (60 * 60) / timeRan;
        		vialsPerHr = (int)(vialsFilled * scale);
        	} catch(Exception e) {
        		//divide by zero
        	}
        	
            controller.drawBoxAlpha(7, 7, 160, 21+14+14+14, 0x0000FF, 48);
            controller.drawString("@blu@VialFiller @whi@by @blu@Dvorak", 10, 21, 0xFFFFFF, 1);
            controller.drawString("@blu@Vials filled: @whi@" + String.format("%,d", vialsFilled) + " @blu@(@whi@" + String.format("%,d", vialsPerHr) + "@blu@/@whi@hr@blu@)", 10, 21+14, 0xFFFFFF, 1);
            controller.drawString("@blu@Full vials in bank: @whi@" + String.format("%,d", fullVials), 10, 21+14+14, 0xFFFFFF, 1);
            controller.drawString("@blu@Empty vials in bank: @whi@" + String.format("%,d", emptyVials), 10, 21+14+14+14, 0xFFFFFF, 1);
        }
    }

}
