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

/**-
 *
 * K_GiftTaker - by Kaila
 *
 *      This bot will open the bank and automatically bank when it gets close to a full inventory.
 *
 *
 *      Tested on christmas crackers and christmas holiday presents!
 *      Should work in any bank, Ideal one is draynor!
 *      requires 2 accounts
 *      This bot is the present "taker", it will bank when you have 29 items
 *
 *      To setup start both accounts near each other with NO items in either inventory
 *      start the taker bot FIRST before even starting giver bot
 *      the bots will need to be synced up similar to trader bots
 *      ideally monitor them, if something goes wrong present stuff will drop to the floor and despawn!!!!! you have been warned!
 *
 *
 *      WARNING: while within 1 tile of the giver, you will continue to recieve presents
 *      WARNING: regardless of how full your inventory is. items WILL drop to the floor
 *      Recommend observing bot due to high value of rares.
 *
 * Author -  Kaila
 */
public class K_GiftTaker extends IdleScript {
	int objectx = 0;
	int objecty = 0;

	long startTimestamp = System.currentTimeMillis() / 1000L;

	public int start(String parameters[]) {
		controller.displayMessage("@red@present TAKER! Let's party like it's 2004!");
		controller.setStatus("@gre@Running..");
		controller.openBank();

		while(controller.isRunning()) {
				if(!controller.isInBank()) {
					controller.openBank();
				}
				if(controller.getInventoryItemCount() <  20) {
					controller.sleep(100);
				}
				if(controller.getInventoryItemCount() >  19) {
					controller.sleep(1280);
					for (int itemId : controller.getInventoryItemIds()) {
					controller.depositItem(itemId, controller.getInventoryItemCount(itemId));

					}
					controller.sleep(1280);
				}
			}
		return 1000; //start() must return a int value now.
	}
}
