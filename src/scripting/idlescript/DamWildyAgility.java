package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class DamWildyAgility extends IdleScript {

	public boolean started = false, debug = false;
	public String status;

	int[] pipeNW = { 305, 120 }, pipeSE = { 292, 123 }, swingNW = { 294, 110 }, swingSE = { 291, 114 },
			rockNW = { 294, 98 }, rockSE = { 288, 108 }, logNW = { 298, 105 }, logSE = { 295, 112 },
			vineNW = { 310, 111 }, vineSE = { 301, 117 }, downNW = {303, 2931}, downSE = {288, 2947};
	int objectId;

	int bonesInBankNew = 0, bonesInBankOld = 0, bonesPicked;
	int itemID;
	int[] toBank;
	int[] toSpot;

	long startTime;

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
		while (controller.isInCombat()) {
			controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
			controller.sleep(250);
		}
		for (int id : controller.getFoodIds()) {
			if (controller.getInventoryItemCount(id) > 0) {
				controller.itemCommand(id);
				controller.sleep(700);
				break;
			}
		}
	}

	public void scriptStart() {
		while (controller.isRunning()) {
			if (inArea(pipeNW, pipeSE)) {
				objectId = 705;
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

	public void avoidCombat() {
		controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
		controller.sleep(400);
	}

	class FishObject {
		String name;

		public FishObject(String _name) {
			name = _name;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof FishObject) {
				if (((FishObject) o).name.equals(this.name)) {
					return true;
				}
			}

			return false;
		}
	}

	int target = 0;

	ArrayList<FishObject> objects = new ArrayList<FishObject>() {
		{
			add(new FishObject("Wild"));
		}
	};

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
		JLabel headerLabel = new JLabel("Start in varrock east bank");
		JComboBox<String> FishField = new JComboBox<String>();

		JButton startScriptButton = new JButton("Start");

		for (FishObject obj : objects) {
			FishField.addItem(obj.name);
		}

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.displayMessage("@cya@" + '"' + "Ty for using DamScripts <3" + '"' + " - Damrau");
				setValuesFromGUI(FishField.getSelectedIndex());
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
		scriptFrame.add(FishField);
		scriptFrame.add(startScriptButton);

		centerWindow(scriptFrame);
		scriptFrame.setVisible(true);
		scriptFrame.pack();
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
			String runTime = msToString(System.currentTimeMillis() - startTime);
			if (controller != null) {
				///controller.setShowCoords(false);
				//controller.setShowStatus(false);
				//controller.setShowXp(false);
				controller.drawString("@cya@DamWildAgility v1.0 - By Damrau", 7, 25, 0xFFFFFF, 1);
				controller.drawString("@cya@Runtime: " + runTime, 7, 25 + 14, 0xFFFFFF, 1);

			}
		}
	}
}
