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

/**- 
*Opens Holiday event Presents on an Iron, banks loot (coleslaw)
*only works on irons. start in any bank.
*Author Kaila
 */
public class K_IronPresentOpener extends IdleScript {	
	int objectx = 0;
	int objecty = 0;
	
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	public int start(String parameters[]) {
		controller.displayMessage("@ran@Iron Present Opener! Let's party like it's 2004!");

		
		while(controller.isRunning()) {
			if(controller.getInventoryItemCount(980) < 1) {
				bank();
			}
			if(controller.getInventoryItemCount(980) > 0) {
						controller.setStatus("@Gre@Opening..");
						
						controller.itemCommand(980);
						controller.sleep(650);
	
			}
		}
		
		return 1000; //start() must return a int value now. 
	}
	
	
	public void bank() {
		controller.setStatus("@gre@Banking..");
		
		controller.openBank();
		
		while(controller.isInBank() && controller.getInventoryItemCount() >  0) {
			for (int itemId : controller.getInventoryItemIds()) {
			if (itemId != 980) {
			controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
			controller.sleep(100);
				}
			}
		}
		while(controller.isInBank() && controller.getInventoryItemCount(980) < 29) {
			controller.withdrawItem(980, 29 - controller.getInventoryItemCount(980));
			controller.sleep(650);
		}
		controller.closeBank();
		controller.sleep(650);
	}
}
