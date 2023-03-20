package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.JTextField;

public class DamWildyAgility extends IdleScript {

	public boolean started = false , debug = false;
	public String status;

	int[] pipeNW = { 305, 120 }, pipeSE = { 292, 123 }, swingNW = { 294, 110 }, swingSE = { 291, 114 },
			rockNW = { 294, 98 }, rockSE = { 288, 108 }, logNW = { 298, 105 }, logSE = { 295, 112 },
			vineNW = { 310, 111 }, vineSE = { 301, 117 }, downNW = {303, 2931}, downSE = {288, 2947};
	int objectId;
	int totalLaps = 0;
	int totalFood = 0;
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	int foodId = -1;
	int foodInBank = 0;
	int[] foodIds = { 546, 370, 367, 373 }; //cooked shark, swordfish, tuna, lobster
	JFrame scriptFrame = null;
	boolean guiSetup = false;

	public boolean inArea(int[] nwTile, int[] seTile) {
		if (controller.currentX() <= nwTile[0] && controller.currentX() >= seTile[0]
				&& controller.currentY() >= nwTile[1] && controller.currentY() <= seTile[1]) {
			return true;
		}
		return false;
	}
	

	public int start(String parameters[]) {
		if (!guiSetup) {
			setupGUI();
			guiSetup = true;
		}
		if (started) {
			scriptStart();
		}
		return 1000;
	}

	public void useObject(int i) {
		int[] objID = controller.getNearestObjectById(i);
		try {
			if (objID.length > 0) {
				status = "Interacting with object id: " + i;
				if (debug) {
					controller.displayMessage("@cya@" + "Interacting with object id:" + i);
				}
				controller.atObject(objID[0], objID[1]);
			}
		} catch (NullPointerException ignored) {

		}

	}
	
	public boolean needToEat() {
		return controller.getCurrentStat(
				controller.getStatId("Hits")) <= controller.getBaseStat(controller.getStatId("Hits")) - 30;
	}
	
	public void eat() {
		leaveCombat();
		for (int id : controller.getFoodIds()) {
			if (controller.getInventoryItemCount(id) > 0) {
				totalFood = totalFood + 1;
				controller.itemCommand(id);
				controller.sleep(700);
				break;
			}
		}
	}

	public void scriptStart() {
		while (controller.isRunning()) {
			if (inArea(pipeNW, pipeSE)) {
				if (controller.getInventoryItemCount(foodId) == 0) {
					controller.setStatus("@red@NO Food in Inventory, Logging Out!.");
					controller.sleep(2000);
					controller.setAutoLogin(false);
					controller.logout();
					if(!controller.isLoggedIn()) {
						controller.stop();
						return;
					}
					//walkToBank();  //add banking soon
					//bank();
					//bankToCourse();
				}
				if (!controller.isCurrentlyWalking() && !controller.isInCombat()) {
					controller.walkTo(294,120);
					totalLaps = totalLaps + 1;
					useObject(705);
					controller.sleep(1000);
				}
			}
			if (inArea(swingNW, swingSE)) {
				objectId = 706;
			}
			if (inArea(rockNW, rockSE)) {
				objectId = 707;
			}
			if (inArea(logNW, logSE)) {
				objectId = 708;
			}
			if (inArea(vineNW, vineSE)) {
				objectId = 709;
			}
			if (inArea(downNW, downSE)) {
				objectId = 5;
			}
			if (needToEat()) {
				eat();
			}
			if (controller.isInCombat()) {
				avoidCombat();
			}
			if (!controller.isCurrentlyWalking() && !controller.isInCombat()) {
				useObject(objectId);
			}

			controller.sleep(600);
		}
	}

	public void walkToBank() {

	}
	public void bankToCourse() {

	}




	public void avoidCombat() {
		controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
		controller.sleep(400);
	}



	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(1240); //lower?

		if(controller.isInBank()){

			foodInBank = controller.getBankItemCount(foodId);

			for (int itemId : controller.getInventoryItemIds()) {                                                            //change 546(shark) to desired food id
				controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
				controller.sleep(320);
			}
			if(controller.getInventoryItemCount(foodId) < 27) {  //withdraw 27 shark if needed        //change 546(shark) to desired food id
				controller.withdrawItem(foodId, 27 - controller.getInventoryItemCount(foodId));          //change 546(shark) to desired food id
				controller.sleep(320);
			}
			if(controller.getBankItemCount(foodId) == 0) {
				controller.setStatus("@red@NO Food in the bank, Logging Out!.");
				controller.setAutoLogin(false);
				controller.logout();
				if(!controller.isLoggedIn()) {
					controller.stop();
					return;
				}
			}
			controller.closeBank();
			controller.sleep(320);
		}
	}
	public void leaveCombat() {
		if(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(),controller.currentY(), 0, true);
			controller.sleep(800);
		}
		if(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(),controller.currentY(), 0, true);
			controller.sleep(800);
		}
		if(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(),controller.currentY(), 0, true);
			controller.sleep(800);
		}
		if(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(),controller.currentY(), 0, true);
			controller.sleep(800);
		}
		if(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(),controller.currentY(), 0, true);
			controller.sleep(800);
		}
		if(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(),controller.currentY(), 0, true);
			controller.sleep(800);
		}
		if(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(),controller.currentY(), 0, true);
			controller.sleep(800);
		}
		if(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(),controller.currentY(), 0, true);
			controller.sleep(800);
		}
	}



















	//class FishObject {
	//	String name;
	//	public FishObject(String _name) {
	//		name = _name;
	//	}
	//	@Override
	//	public boolean equals(Object o) {
	//		if (o instanceof FishObject) {
	//			if (((FishObject) o).name.equals(this.name)) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}
	//}

//	int target = 0;

	//ArrayList<FishObject> objects = new ArrayList<FishObject>() {
	//	{
	//		add(new FishObject("Wild"));
	//	}
	//};

	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	public void setValuesFromGUI(int i) {
		if (i == 0) {

		}

	}

	public void setupGUI() {
		JLabel headerLabel = new JLabel("DamWildAgility v1.2 - By Damrau & Kaila");
		JLabel Label1 = new JLabel("Start at Wilderness Agility Course!");
		JLabel Label2 = new JLabel("Bring armor, and cooked fish for food!");
		JLabel foodLabel = new JLabel("Type of Food to Withdraw:");
		JComboBox<String> FishField = new JComboBox<String>( new String[] { "Sharks", "Swordfish", "Tuna", "Lobsters" });
		JButton startScriptButton = new JButton("Start");

		//for (FishObject obj : objects) {
		//	FishField.addItem(obj.name);
		//}

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.displayMessage("@cya@" + '"' + "Ty for using DamScripts <3" + '"' + " - Damrau & Kaila");
				foodId = foodIds[FishField.getSelectedIndex()];
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				startTime = System.currentTimeMillis();
				started = true;
			}
		});

		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(headerLabel);
		scriptFrame.add(Label1);
		scriptFrame.add(Label2);
		scriptFrame.add(foodLabel);
		scriptFrame.add(FishField);
		scriptFrame.add(startScriptButton);

		centerWindow(scriptFrame);
		scriptFrame.setVisible(true);
		scriptFrame.pack();
		scriptFrame.requestFocus();
	}

	public static String msToString(long milliseconds) {
		long sec = milliseconds / 1000;
		long min = sec / 60;
		long hour = min / 60;
		sec %= 60;
		min %= 60;
		DecimalFormat twoDigits = new DecimalFormat("00");

		return new String(twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec));
	}

	@Override
	public void paintInterrupt() {
		if (started) {
			if (controller != null) {

				String runTime = msToString(System.currentTimeMillis() - startTime);
				int LapSuccessPerHr = 0;
				int FoodSuccessPerHr = 0;

				try {
					float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
					float scale = (60 * 60) / timeRan;
					LapSuccessPerHr = (int)(totalLaps * scale);
					FoodSuccessPerHr = (int)(totalFood * scale);

				} catch(Exception e) {
					//divide by zero
				}



				controller.drawString("@cya@DamWildAgility v1.2 - By Damrau & Kaila", 7, 25, 0xFFFFFF, 1);
				controller.drawString("@whi@Food in Bank: @gre@" + String.valueOf(this.foodInBank), 7, 39, 0xFFFFFF, 1);
				controller.drawString("@whi@Food Used: @gre@" + String.valueOf(this.totalFood) + "@yel@ (@whi@" + String.format("%,d", FoodSuccessPerHr) + "@yel@/@whi@hr@yel@)", 7, 53, 0xFFFFFF, 1);
				controller.drawString("@whi@Total Laps: @gre@" + String.valueOf(this.totalLaps) + "@yel@ (@whi@" + String.format("%,d", LapSuccessPerHr) + "@yel@/@whi@hr@yel@)", 7, 67, 0xFFFFFF, 1);
				controller.drawString("@whi@Runtime: " + runTime, 7, 81, 0xFFFFFF, 1);

			}
		}
	}
}
