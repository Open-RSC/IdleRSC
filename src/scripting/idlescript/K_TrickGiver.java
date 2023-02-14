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
 * 
 * Should work in any bank, tested in varrock west 
*requires 2 accounts
*This bot is the present "taker", it will bank when you have 29 items
*
*To setup start both accounts near each other with NO items in either inventory
*start the taker bot FIRST before even starting giver bot
*the bots will need to be synced up similar to trader bots
*ideally monitor them, if something goes wrong present stuff will drop to the floor and despawn!!!!! you have been warned!
*
*
*WARNING: while within 1 tile of the giver, you will continue to recieve presents
*WARNING: regardless of how full your inventory is. items WILL drop to the floor
*Walk 1 tile away to stop the trades temporarily, and/or bank items
 * 
 * Heavily edited by Kailash
 */
public class K_TrickGiver extends IdleScript {	
	
	long startTimestamp = System.currentTimeMillis() / 1000L;
	
	public int start(String parameters[]) {
		controller.displayMessage("@red@present GIVER! Let's party like it's 2004!");
		
		
		while(controller.isRunning()) {
			if(controller.getInventoryItemCount() > 29) {
				controller.setStatus("@gre@Banking.");
				bank();
			}
			if(controller.getInventoryItemCount(1330) < 2) {
				controller.setStatus("@gre@Banking.");
				bank();
			}
			if(controller.getInventoryItemCount() < 30 && controller.getInventoryItemCount(1330) > 1) {
				controller.setStatus("@gre@Opening.");
				controller.useItemOnPlayer(1,controller.getPlayerServerIndexByName("kailashu"));  //replace the player name
				controller.sleep(640);
			} 
		}
		
		return 1000; //start() must return a int value now. 
	}
	
	
	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(640);

		if (controller.isInBank()) {
			if(controller.getInventoryItemCount() >  0) {
				for (int itemId : controller.getInventoryItemIds()) {
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				}
				controller.sleep(1280);
			}
			if (controller.getInventoryItemCount(1330) < 23) {
				controller.withdrawItem(1330, 23 - controller.getInventoryItemCount(1330));
				controller.sleep(1280);
			}
			controller.closeBank();
			controller.sleep(640);
		}
	}
}
