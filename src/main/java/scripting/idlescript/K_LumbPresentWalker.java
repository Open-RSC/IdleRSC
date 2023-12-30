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
 * Grabs presents from under varrock trees -
 *  based off vial crafter by dvorak
 * automatically banks at full inv, at the end of a batch cycle
 * Start near the trees
 * will keep going in circles if trees are empty, not worth fixing it doesnt break bot
 * Also, when stopping the bot, click stop, optionally stop current batch. Then, wait for the bot to stop
 * it will walk to 133,506 then fully stop. again, not worth fixing
 *  @author Dvorak
 * Heavily edited by Kailash
 */
public class K_LumbPresentWalker extends IdleScript {
	int vialsFilled = 0;
	int fullVials = 0;
	int emptyVials = 0;
	int objectx = 0;
	int objecty = 0;

	long startTimestamp = System.currentTimeMillis() / 1000L;

	public int start(String parameters[]) {
		controller.displayMessage("@red@present grabber! Let's party like it's 2004!");


		while(controller.isRunning()) {
			if(controller.getInventoryItemCount(980) < 30) {
				controller.walkTo(127,663);
				controller.atObject(127,664);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					goToBank();
				}
				controller.walkTo(122,663);
				controller.atObject(122,664);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					goToBank();
				}
				controller.walkTo(123,661);
				controller.atObject(123,660);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					goToBank();
				}
				controller.walkTo(125,660);
				controller.atObject(126,660);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					goToBank();
				}
				controller.walkTo(126,657);
				controller.atObject(126,656);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					goToBank();
				}

				controller.walkTo(123,657);
				controller.atObject(123,656);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				controller.walkTo(125,661);
				if(controller.getInventoryItemCount() > 29) {
					goToBank();
				}



			}
		}
		return 1000; //start() must return a int value now.
	}

	public void goToBank() {
		controller.walkTo(114,656);
		controller.walkTo(130,647);
		controller.walkTo(147,645);
		controller.walkTo(167,633);
		controller.walkTo(189,630);
		controller.walkTo(210,629);
		controller.walkTo(219,633);
		bank();
		controller.walkTo(219,633);
		controller.walkTo(210,629);
		controller.walkTo(189,630);
		controller.walkTo(167,633);
		controller.walkTo(147,645);
		controller.walkTo(130,647);
		controller.walkTo(126,648);   //harvest tree by teleport
		controller.atObject(127,648);
		controller.sleep(1000);
		while(controller.isBatching()) controller.sleep(1000);
		controller.walkTo(115,651);  //walk in castle
		controller.walkTo(116,659);
		controller.walkTo(124,663);
	}

	public void bank() {
		controller.setStatus("@blu@Banking..");

		controller.openBank();

		while(controller.isInBank() && controller.getInventoryItemCount(980) >  0) {
			controller.depositItem(980, controller.getInventoryItemCount(980));
			controller.sleep(100);
		}
	}

}
