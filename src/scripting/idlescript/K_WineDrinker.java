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
*Easy AF Wine drinker
*Start in VARROCK EAST BANK ONLY
*Drinks wines from the bank, stores the jugs and any half wine you might get
*About 7k+ wines processed per hour!
*Author Kaila
 */
public class K_WineDrinker extends IdleScript {
	int vialsFilled = 0;
	int fullVials = 0;
	int emptyVials = 0;
	int objectx = 0;
	int objecty = 0;

	long startTimestamp = System.currentTimeMillis() / 1000L;

	public int start(String parameters[]) {
		controller.displayMessage("@red@Wine Drinker, Start in Varrock East!!!");


		while(controller.isRunning()) {
			if(controller.getInventoryItemCount(142) < 1) {
				controller.setStatus("@gre@Banking..");
				controller.walkTo(98,510);
				controller.sleep(640);
				controller.walktoNPCAsync(95);
				bank();
			}
			if(controller.getInventoryItemCount(142) > 0) {
						controller.setStatus("@gre@Drinking..");
						controller.itemCommand(142);
						controller.sleep(100);

			}
		}

		return 1000; //start() must return a int value now.
	}


	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(640);

		if (controller.isInBank()) {


			if (controller.getInventoryItemCount(140) > 0) {
				controller.depositItem(140, controller.getInventoryItemCount(140));
				controller.sleep(100);
			}

			if (controller.getInventoryItemCount(246) > 0) {
				controller.depositItem(246, controller.getInventoryItemCount(246));
				controller.sleep(100);
			}

			if (controller.getInventoryItemCount(142) < 30) {
				controller.withdrawItem(142, 30 - controller.getInventoryItemCount());
				controller.sleep(650);
			}
			controller.closeBank();
			controller.sleep(650);
		}
	}
}
