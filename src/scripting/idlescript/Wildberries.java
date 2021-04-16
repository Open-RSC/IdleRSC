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
 * Picks whiteberries in wilderness. Needs antidragon shields.
 *  
 * @author Dvorak
 */
public class Wildberries extends IdleScript {	
	int[] gateToBerries = new int[] {
			140, 181, 
			142, 191, 
			141, 207, 
			137, 213
	};
	
	int[] varrockToGate = new int[] {
			103, 509,
			109, 498,
			110, 483,
			110, 469,
			110, 455,
			110, 441,
			110, 427,
			110, 415, 
			110, 400, 
			110, 385,
			104, 380, 
			97, 372,
			91, 361, 
			84, 352,
			76, 342,
			76, 334, 
			82, 325,
			84, 313, 
			84, 302, 
			78, 294, 
			78, 284, 
			79, 267, 
			80, 255, 
			80, 241, 
			80, 230,
			80, 220, 
			80, 210, 
			80, 200, 
			80, 193, 
			88, 183, 
			101, 175, 
			111, 176, 
			122, 179, 
			136, 179, 
			140, 180
	};
	
	int berriesPicked = 0;
	int berriesBanked = 0;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	public int start(String parameters[]) {
		controller.displayMessage("@red@Wildberries by Dvorak. Let's party like it's 2004!");
		controller.displayMessage("@red@Start in Varrock East bank! You need antidragon shields in the bank!");
		
		while(controller.isRunning()) {
			
			if(!controller.isEquipped(controller.getInventoryItemSlotIndex(420))) {
				controller.equipItem(controller.getInventoryItemSlotIndex(420));
				controller.sleep(618);
			}
			
			if(controller.getInventoryItemCount(471) == 0) {
				
				bank();
				
				controller.walkPath(varrockToGate);
				while(controller.currentX() != 140 || controller.currentY() != 181) {
					controller.atObject(140, 180);
					controller.sleep(618);
				}
				controller.walkPath(gateToBerries);
				
				if(controller.getGroundItemAmount(471, 137, 213) > 0) {
					controller.pickupItem(137, 213, 471, true, false);
					controller.sleep(618);
				}
				
				while(controller.getInventoryItemCount(471) == 0) {
					controller.pickupItem(137, 213, 471, true, false);
					controller.sleep(618);
				}
				
			} else if(controller.getInventoryItemCount(471) > 0 && controller.getInventoryItemCount() != 30) {
				if(controller.getGroundItemAmount(471, 137, 213) > 0) {
					controller.pickupItem(137, 213, 471, true, false);
					controller.sleep(618);
				}
				
				if(controller.isInCombat()) {
					controller.walkTo(137, 213);
					controller.sleep(333);
				}
				
				eat();
				
			} else {
				controller.walkPathReverse(gateToBerries);
				while(controller.currentX() != 140 || controller.currentY() != 180) {
					controller.atObject(140, 180);
					controller.sleep(618);
				}
				controller.walkPathReverse(varrockToGate);
				bank();
			}
		}
		
		return 1000; //start() must return a int value now. 
	}
	
	public void eat() {
		if(controller.getCurrentStat(controller.getStatId("Hits")) <= (controller.getBaseStat(controller.getStatId("Hits")) - 20) ) {
			controller.setStatus("@red@Eating..");
			
			for(int id : controller.getFoodIds()) {
				if(controller.getInventoryItemCount(id) > 0) {
					controller.itemCommand(id);
					controller.sleep(700);
					break;
				}
			}
		}
	}
	
	public void bank() {
		controller.setStatus("@red@Banking..");
		
		controller.openBank();
		
		berriesPicked += controller.getInventoryItemCount(471);
		
		while(controller.isInBank() && controller.getInventoryItemCount(471) >  0) {
			controller.depositItem(471, controller.getInventoryItemCount(471));
			controller.sleep(100);
		}
		
		while(controller.isInBank() && controller.getInventoryItemCount(546) < 5) {
			controller.withdrawItem(546, 5);
			controller.sleep(2000);
		}
		
		while(controller.isInBank() && controller.getInventoryItemCount(420) < 1) {
			controller.withdrawItem(420, 1);
			controller.sleep(2000);
		}
		
		
		berriesBanked = controller.getBankItemCount(471);
	}
	
    @Override
    public void paintInterrupt() {
        if(controller != null) {
        			
        	int berriesPerHr = 0;
        	try {
        		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        		float scale = (60 * 60) / timeRan;
        		berriesPerHr = (int)(berriesPicked * scale);
        	} catch(Exception e) {
        		//divide by zero
        	}
        	
            controller.drawBoxAlpha(7, 7, 160, 21+14+14, 0xFF0000, 48);
            controller.drawString("@red@Wildberries @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
            controller.drawString("@red@Berries picked: @whi@" + String.format("%,d", berriesPicked) + " @red@(@whi@" + String.format("%,d", berriesPerHr) + "@red@/@whi@hr@red@)", 10, 21+14, 0xFFFFFF, 1);
            controller.drawString("@red@Berries in bank: @whi@" + String.format("%,d", berriesBanked), 10, 21+14+14, 0xFFFFFF, 1);
        }
    }

}
