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
public class K_VarPresentGrabber extends IdleScript {
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
				controller.walkTo(135,506);
				controller.atObject(135, 505);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					goToBank();
				}
				controller.walkTo(133,511);
				controller.atObject(133, 512);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					goToBank();
				}
				controller.walkTo(128,510);
				controller.atObject(128, 511);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					goToBank();
				}
				controller.walkTo(123,507);
				controller.walkTo(122,503);
				controller.atObject(122, 502);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					goToBank();
				}
				controller.walkTo(132,501);
				controller.walkTo(131,485);
				controller.atObject(131, 484);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					controller.walkTo(131,488);
					controller.walkTo(132,501);
					goToBank();
				}
				controller.walkTo(126,483);
				controller.atObject(126,482);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					controller.walkTo(131,488);
					controller.walkTo(132,501);
					goToBank();
				}
				controller.walkTo(135,482);
				controller.atObject(136,482);
				controller.sleep(1000);
				while(controller.isBatching() && controller.getInventoryItemCount() < 30) controller.sleep(1000);
				if(controller.getInventoryItemCount() > 29) {
					controller.walkTo(131,488);
					controller.walkTo(132,501);
					goToBank();
				}


			}
		}
		return 1000; //start() must return a int value now.
	}

public void goToBank() {
	controller.walkTo(134,508);
	controller.walkTo(138,509);
	controller.walkTo(151,507);
	bank();
	controller.walkTo(151,507);
	controller.walkTo(138,509);
	controller.walkTo(134,508);
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
