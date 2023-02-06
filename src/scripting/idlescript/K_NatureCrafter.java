package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import orsc.ORSCharacter;
import scripting.idlescript.AIOCooker.FoodObject;

/**
 * Grabs Nats from edge monestary
 * 
 * 
 * 
 * 
 * Author - Kaila
 */
public class K_NatureCrafter extends IdleScript {	
	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	int NatzInBank = 0;
	int totalNatz = 0;
    int totalTrips = 0;
    
    int robeId[] = {388,389};
    
	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;
	boolean lowLevel = false;
		
		public int start(String parameters[]) {
			if (!guiSetup) {
				setupGUI();
				guiSetup = true;
			}
			if (scriptStarted) {
				controller.displayMessage("@red@Nature Rune Crafter - By Kaila");
				controller.displayMessage("@red@Start in Karamja Shop or Inside/Outside Nature Alter");
				if(controller.isInBank() == true) {
					controller.closeBank();
				}
				if(controller.isInShop() == true) {
					controller.closeShop();
				}
				if(controller.currentY() < 770 && controller.currentY() > 500) {
					bank();
					BankToNat();
					controller.sleep(100);
				}
				if(controller.currentY() > 790) {
					controller.walkTo(392,803);
					if(controller.currentX() == 392 && controller.currentY() == 803) {
						controller.atObject(392,804);
						controller.sleep(340);
					}
					controller.walkTo(787,23);
				}
				scriptStart();
			}
			return 1000; //start() must return a int value now. 
		}
		
		//if low hp log out for nature rc
		
		
		public void scriptStart() {
			while(controller.isRunning()) {
							
				if(controller.currentY() < 50) {
					controller.setStatus("@red@Crafting..");
					controller.atObject(787,21);
					if(controller.getBaseStat(18) < 91) {
						totalNatz = totalNatz + 26;
					}
					if(controller.getBaseStat(18) > 90) {
						totalNatz = totalNatz + 52;
					}
					totalTrips = totalTrips + 1;
					controller.sleep(618);
					controller.setStatus("@red@Getting more Ess..");
					NatToBank();
					bank();
					BankToNat();
					controller.sleep(618);
				}	

				int eatLvl = controller.getBaseStat(controller.getStatId("Hits")) - 20;
	
				if(controller.getCurrentStat(controller.getStatId("Hits")) < eatLvl) {
					
					while(controller.isInCombat()) {
						controller.setStatus("@red@Leaving combat..");
						controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
						controller.sleep(250);
					}
					controller.setStatus("@red@We've ran out of Food! Running Away/Logging Out.");
					controller.sleep(308);
    				controller.setAutoLogin(false);
    				controller.logout();
					BankToNat();
    				controller.setAutoLogin(false);
    				controller.logout();
    				controller.stop();
    				controller.logout();	
				}
				while(controller.isInCombat()) {
					controller.setStatus("@red@Leaving combat..");
					controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
					controller.sleep(250);
				}
			}
		}
					
	
	public void bank() {
		
		controller.setStatus("@yel@Buying Runes..");
		if(!controller.isInShop() && controller.getInventoryItemCount() != 30) {
			ORSCharacter npc = controller.getNearestNpcById(522, false);
		   	if(npc != null && controller.getInventoryItemCount() != 30 && controller.currentY() < 760) {
		   		controller.walktoNPC(npc.serverIndex,0);  //added, bot doesnt always get runes if npc moves >2 or 3 tiles away
		    	controller.npcCommand1(npc.serverIndex);
		    	controller.sleep(4000); //need LONG sleep or it breaks npccommand1
		    } else {
				controller.sleep(1000);
			}
		}
		if(controller.isInShop() && controller.getInventoryItemCount() != 30) {
			controller.shopSell(1299,27); 
			controller.sleep(800);
			controller.shopBuy(1299,27);
			controller.sleep(340);
			controller.closeShop();
			controller.sleep(340);
		}
	}
	
	public void NatToBank() {  //replace
    	controller.setStatus("@gre@Walking to Shop..");
		if(lowLevel == false) {
			controller.walkTo(787,25);
			controller.walkTo(785,26);
			if(controller.currentX() == 785 && controller.currentY() == 26) {
				controller.atObject(783,26);
				controller.sleep(340);
			}
			controller.walkTo(392,803);
			controller.walkTo(398,791);
			//controller.walkTo(555,555);
			controller.walkTo(397,782); //fix pathing ERROR here
			controller.walkTo(402,779); 
			controller.walkTo(409,779);
			controller.walkTo(414,779);
			controller.walkTo(422,782); 
			controller.walkTo(436,773);
			controller.walkTo(456,773);
			controller.walkTo(457,772);
			controller.walkTo(457,767);
			controller.walkTo(459,765);
			controller.walkTo(458,757);
			//in jungle shop
		}
		if(lowLevel == true) {
			controller.walkTo(787,25);
			controller.walkTo(785,26);
			if(controller.currentX() == 785 && controller.currentY() == 26) {
				controller.atObject(783,26);
				controller.sleep(340);
			}
			controller.walkTo(401,797);
			controller.walkTo(401,793);
			controller.walkTo(407,787);
			controller.walkTo(417,786);
			controller.walkTo(428,786);
			controller.walkTo(433,789);
			controller.walkTo(440,793);
			controller.walkTo(448,793);
			controller.walkTo(463,793);
			controller.walkTo(469,789);
			controller.walkTo(470,782);
			controller.walkTo(473,776);
			controller.walkTo(473,769);
			controller.walkTo(473,756);
			controller.walkTo(460,756);
			//in jungle shop
		}
    	controller.setStatus("@gre@Done Walking..");
	}
	
    public void BankToNat() {
		if(lowLevel == false) {
	    	controller.setStatus("@gre@Walking to Nature Alter..");
			controller.walkTo(459,757);
			controller.walkTo(459,765);
			controller.walkTo(457,767);
			controller.walkTo(457,772);
			controller.walkTo(456,773);
			controller.walkTo(436,773);
			controller.walkTo(424,783);
			controller.walkTo(422,782);
			controller.walkTo(414,779);  //pathing brokme and landed here
			controller.walkTo(408,779); //added
			controller.walkTo(403,779);
			controller.walkTo(399,780);
			controller.walkTo(397,783);
			controller.walkTo(396,786);
			controller.walkTo(396,795);
			controller.walkTo(393,800);
			controller.walkTo(392,803); 
			if(controller.currentX() < 400 && controller.currentX() > 385 && controller.currentY() > 800 && controller.currentY() < 810) {
				controller.atObject(392,804);
				controller.sleep(2000);  //was 3k
			}
			if(controller.currentY() < 50) {
			controller.walkTo(787,26);
			controller.walkTo(787,23);
	    	//next to alter now)
			}
	    	controller.setStatus("@gre@Done Walking..");
		}
		if(lowLevel == true) {
			controller.setStatus("@gre@Walking to Nature Alter..");
			controller.walkTo(460,756);
			controller.walkTo(473,756);
			controller.walkTo(473,769);
			controller.walkTo(473,776);
			controller.walkTo(470,782);
			controller.walkTo(469,789);
			controller.walkTo(463,793);
			controller.walkTo(448,793);
			controller.walkTo(440,793);
			controller.walkTo(433,789);
			controller.walkTo(428,786);
			controller.walkTo(417,786);
			controller.walkTo(407,787);
			controller.walkTo(401,793);
			controller.walkTo(401,797);
			controller.walkTo(394,803);
			if(controller.currentX() == 394 && controller.currentY() == 803) {
				controller.atObject(392,804);
				controller.sleep(340);
			}
			controller.walkTo(787,23);
		}
	}
	
	
	//GUI stuff below (icky)
	
	
	
	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	public void setValuesFromGUI(JCheckBox lowLevelCheckbox) {
			if(lowLevelCheckbox.isSelected()) {
				lowLevel = true;
			}
}
	
	public void setupGUI() {
		JLabel header = new JLabel("Nature Rune Crafter - By Kaila");
		JLabel label1 = new JLabel("Start in Karamja Shop or Inside/Outside Nature Alter");
		JLabel label2 = new JLabel("Start with Coins, Noted Ess, and Nat Talisman");
		JLabel label3 = new JLabel("Ideally 79+ combat so tribesmen dont poison you");
    	JCheckBox lowLevelCheckbox = new JCheckBox("Check This If Below 79 Combat");
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setValuesFromGUI(lowLevelCheckbox);
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				startTime = System.currentTimeMillis();
				scriptStarted = true;
			}
		});
		
		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(label1);
		scriptFrame.add(label2);
		scriptFrame.add(label3);
    	scriptFrame.add(lowLevelCheckbox);
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
		if (controller != null) {
			
			String runTime = msToString(System.currentTimeMillis() - startTime);
	    	int NatzSuccessPerHr = 0;
	    	int TripSuccessPerHr = 0;
	    	
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		NatzSuccessPerHr = (int)(totalNatz * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    		
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
			controller.drawString("@red@Nature Rune Crafter@gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Natures Crafted: @gre@" + String.valueOf(this.totalNatz) + "@yel@ (@whi@" + String.format("%,d", NatzSuccessPerHr) + "@yel@/@whi@hr@yel@)", 330, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 330, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 330, 90, 0xFFFFFF, 1);
		}
	}
}
