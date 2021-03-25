package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class PotionMaker extends IdleScript{
	//PotionMaker Script by Seatta
	String potion = "";
	Integer levelReq = 0;
	JFrame scriptFrame = null;
	Boolean stopped = false;
	Boolean guiSetup = false;
	Boolean setupCompleted = false;
	Integer made = 0;
	//Ingredients: Full Vial, Clean Herb, Secondary, Empty Vial, Unid Herb, Unfinished Potion
	Integer ingredients[] = {464,0,0,465,0,0};
	
	public void start(String param[]) {
		if (!guiSetup) {
			controller.setStatus("@cya@Setting up script");
			setup();
			guiSetup = true;
		}
		while (controller.isRunning() && setupCompleted) {
			run();
		}		
	}
	public void setup() {
		JLabel potionLabel = new JLabel("Select Potion/Secondary");
		JComboBox<String> potionField = new JComboBox<String>(new String[] {"Attack", "Cure Poison", "Strength", "Runecraft",
				"Stat Restoration", "Defense", "Prayer", "Super Attack", "Poison Antidote", "Fishing", "Super Strength", 
				"Super Runecraft", "Weapon Poison", "Super Defense", "Ranging", "Zamorak", "", "Ground Unicorn Horn", "Ground Blue Dragon Scale",
				"Fish Oil (Raw Turtle)","Fish Oil (Raw Manta Ray)","Fish Oil (Raw Shark)","Fish Oil (Raw Swordfish)","Fish Oil (Raw Lobster)","Fish Oil (Raw Tuna)"});
	
		JButton startScriptButton = new JButton("Start");
	
		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				
				switch(potionField.getSelectedItem().toString()) {
				case "Attack":
					//Herb
					ingredients[1] = 444;
					ingredients[2] = 270;
					ingredients[4] = 165;
					ingredients[5] = 454;
					levelReq = 3;
					break;
				case "Cure Poison":
					ingredients[1] = 445;
					ingredients[2] = 473;
					ingredients[4] = 435;
					ingredients[5] = 455;
					levelReq = 5;	
					break;
				case "Strength":
					ingredients[1] = 446;
					ingredients[2] = 220;
					ingredients[4] = 436;
					ingredients[5] = 456;
					levelReq = 12;	
					break;
				case "Runecraft":
					ingredients[1] = 445;
					ingredients[2] = 1410;
					ingredients[4] = 435;
					ingredients[5] = 455;
					levelReq = 12;	
					break;
				case "Stat Restoration":
					//Herb
					ingredients[1] = 447;
					ingredients[2] = 219;
					ingredients[4] = 437;
					ingredients[5] = 457;
					levelReq = 22;
					break;
				case "Defense":
					ingredients[1] = 448;
					ingredients[2] = 220;
					ingredients[4] = 438;
					ingredients[5] = 458;	
					levelReq = 30;		
					break;
				case "Prayer":
					ingredients[1] = 448;
					ingredients[2] = 469;
					ingredients[4] = 438;
					ingredients[5] = 458;
					levelReq = 38;
					break;
				case "Super Attack":
					ingredients[1] = 449;
					ingredients[2] = 270;
					ingredients[4] = 439;
					ingredients[5] = 459;	
					levelReq = 45;
					break;
				case "Poison Antidote":
					ingredients[1] = 449;
					ingredients[2] = 473;
					ingredients[4] = 439;
					ingredients[5] = 459;	
					levelReq = 48;				
					break;
				case "Fishing":
					ingredients[1] = 450;
					ingredients[2] = 469;
					ingredients[4] = 440;
					ingredients[5] = 460;	
					levelReq = 50;	
					break;
				case "Super Strength":
					ingredients[1] = 451;
					ingredients[2] = 220;
					ingredients[4] = 441;
					ingredients[5] = 461;	
					levelReq = 55;				
					break;
				case "Super Runecraft":
					ingredients[1] = 450;
					ingredients[2] = 1410;
					ingredients[4] = 440;
					ingredients[5] = 460;
					levelReq = 57;	
					break;
				case "Weapon Poison":
					ingredients[1] = 451;
					ingredients[2] = 472;
					ingredients[4] = 441;
					ingredients[5] = 461;	
					levelReq = 60;				
					break;
				case "Super Defense":
					ingredients[1] = 452;
					ingredients[2] = 471;
					ingredients[4] = 442;
					ingredients[5] = 462;	
					levelReq = 66;	
					break;
				case "Ranging":
					ingredients[1] = 453;
					ingredients[2] = 501;
					ingredients[4] = 443;
					ingredients[5] = 463;	
					levelReq = 72;	
					break;
				case "Zamorak":
					ingredients[1] = 934;
					ingredients[2] = 936;
					ingredients[4] = 933;
					ingredients[5] = 935;	
					levelReq = 78;
					break;
				case "Ground Unicorn Horn":
					ingredients[1] = 468; //pestle and mortar
					ingredients[2] = 466; //Unicorn Horn
					ingredients[4] = 473; //Ground Unicorn Horn
					levelReq = 0;
					break;
				case "Ground Blue Dragon Scale":
					ingredients[1] = 468; //pestle and mortar
					ingredients[2] = 467; //Blue Dragon Scale
					ingredients[4] = 472; //Ground Blue Dragon Scale
					levelReq = 0;
					break;
				case "Fish Oil (Raw Turtle)":
					ingredients[1] = 468; //Pestle and mortar
					ingredients[2] = 1192; //Turtle
					ingredients[4] = 1410; //Fish oil
					levelReq = 0;
					break;
				case "Fish Oil (Raw Manta Ray)":
					ingredients[1] = 468; //Pestle and mortar
					ingredients[2] = 1190; //Manta Ray
					ingredients[4] = 1410; //Fish oil
					levelReq = 0;
					break;
				case "Fish Oil (Raw Shark)":
					ingredients[1] = 468; //Pestle and mortar
					ingredients[2] = 545; //Shark
					ingredients[4] = 1410; //Fish oil
					levelReq = 0;
					break;
				case "Fish Oil (Raw Swordfish)":
					ingredients[1] = 468; //Pestle and mortar
					ingredients[2] = 369; //Swordfish
					ingredients[4] = 1410; //Fish oil
					levelReq = 0;
					break;
				case "Fish Oil (Raw Lobster)":
					ingredients[1] = 468; //Pestle and mortar
					ingredients[2] = 372; //Lobster
					ingredients[4] = 1410; //Fish oil
					levelReq = 0;
					break;
				case "Fish Oil (Raw Tuna)":
					ingredients[1] = 468; //Pestle and mortar
					ingredients[2] = 366; //Tuna
					ingredients[4] = 1410; //Fish oil
					levelReq = 0;
					break;
				}
				if (potionField.getSelectedItem().toString() != "") {
					potion = potionField.getSelectedItem().toString();
					setupCompleted = true;
					stopped = false;
				} else {
					setup();
				}
			}
		});
		
		scriptFrame = new JFrame("Script Options");
		
		scriptFrame.setLayout(new GridLayout(0,1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(potionLabel);
		scriptFrame.add(potionField);
		scriptFrame.add(startScriptButton);
		
		centerWindow(scriptFrame);
		scriptFrame.setVisible(true);
		scriptFrame.pack();
	}
	
	public static void centerWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
	

	public void run() {
		if (controller.getBaseStat(controller.getStatId("Herblaw")) < levelReq) {
			//Herblaw level too low
			quit(4);
		}		
		if (ingredients[1] != 468) {
			controller.displayMessage("@cya@Making: " + potion + " Potion");
		} else {
			controller.displayMessage("@cya@Grinding: " + controller.getItemName(ingredients[2]) + " into " + controller.getItemName(ingredients[4]));			
		}
	
		while (controller.isRunning()) {
			//deposit everything into bank
			depositAll();
			controller.setStatus("@cya@Withdrawing items");
			if (ingredients[5] != 0) {
				//withdraw unf potions/filled vials/vials
				if (controller.isRunning()) {
					withdrawVial();
				}
				//withdraw herb/unid herb
				if (controller.isRunning()) {
					withdrawHerb();
				}
				//withdraw secondary
				if (controller.isRunning()) {
					withdrawSecondary();
				}
				controller.closeBank();
				controller.sleep(1280);
		
				//if inventory has empty vials fill them
				if (controller.isItemInInventory(ingredients[3]) && controller.isRunning()) {
					fillVials();
				}
				//if inventory has unid herbs, id them
				if (controller.isItemInInventory(ingredients[4]) && controller.isRunning()) {
					cleanHerbs();
				}				
				//mix unf potions
				if (controller.isRunning()) {
					makeUnf();
				}
				//mix finished potions
				if (controller.isRunning()) {
					mix();
				}
			} else {
				//withdraw pestle & mortar
				if (controller.isRunning()) {
					withdrawPestle();
				}
				//withdraw pre-secondary
				if (controller.isRunning()) {
					withdrawPre();
				}
				//grind away
				if (controller.isRunning()) {
					grind();
				}				
			}
		}
		if (!stopped) {
			quit(1);
		}
	}
	public void depositAll() {
		controller.setStatus("@cya@Waiting for bank interface to open");
		controller.openBank();
		while (!controller.isInBank() && controller.isRunning()) {
			controller.sleep(1280);
		}
		controller.setStatus("@cya@Depositing items");
		while(controller.getInventoryItemCount() > 0 && controller.isRunning()) {
			int id = controller.getInventoryItems().get(0).getItemDef().id;
			controller.depositItem(id, controller.getInventoryItemCount(id));		
			controller.sleep(1280);
		}	
	}
	public void withdrawVial() {
		//withdraw unfinished potions if they exist
		if (controller.getBankItemCount(ingredients[5]) > 0) {
			if (controller.getBankItemCount(ingredients[5]) > 10) {
				controller.withdrawItem(ingredients[5], 10);
				controller.sleep(1280);
			} else {
				controller.withdrawItem(ingredients[5], controller.getBankItemCount(ingredients[5]));
				controller.sleep(1280);
			}
		} else {
			//withdraw vial
			if (controller.getBankItemCount(ingredients[0]) > 0) {
				if (controller.getBankItemCount(ingredients[0]) > 10) {
					controller.withdrawItem(ingredients[0], 10);
					controller.sleep(1280);
				} else {
					controller.withdrawItem(ingredients[0], controller.getBankItemCount(ingredients[0]));
					controller.sleep(1280);
				}
				//withdraw empty vials if no filled ones are available
			} else if (controller.getBankItemCount(ingredients[3]) > 0){
				if (controller.getBankItemCount(ingredients[3]) > 10) {
					controller.withdrawItem(ingredients[3], 10);
					controller.sleep(1280);
				} else {
					controller.withdrawItem(ingredients[3], controller.getBankItemCount(ingredients[3]));
					controller.sleep(1280);
				}
			
			} else {
				quit(5);
			}
		}
	}	
	public void withdrawHerb() {
		//withdraw clean herbs
		if (controller.getBankItemCount(ingredients[1]) > 0) {
			if (controller.getBankItemCount(ingredients[1]) > 10) {
				controller.withdrawItem(ingredients[1], 10);
				controller.sleep(1280);
			} else {
				controller.withdrawItem(ingredients[1], controller.getBankItemCount(ingredients[5]));
				controller.sleep(1280);
			}
		} else {
			//withdraw unid herbs
			if (controller.getBankItemCount(ingredients[4]) > 0) {
				if (controller.getBankItemCount(ingredients[4]) > 10) {
					controller.withdrawItem(ingredients[4], 10);
					controller.sleep(1280);
				} else {
					controller.withdrawItem(ingredients[4], controller.getBankItemCount(ingredients[4]));
					controller.sleep(1280);
				}
			} else {
				//no clean or unid herbs
				quit(5);
			}
		}		
	}
	public void withdrawSecondary() {
		if (ingredients[2] == 1410) {
			if (controller.getBankItemCount(ingredients[2]) > 0) {
				if (controller.getBankItemCount(ingredients[2]) > 100) {
					controller.withdrawItem(ingredients[2], 100);
					controller.sleep(1280);
				} else {
					controller.withdrawItem(ingredients[2], controller.getBankItemCount(ingredients[2]));
					controller.sleep(1280);
				}
			} else {
				quit(2);
			}
		} else {
			if (controller.getBankItemCount(ingredients[2]) > 0) {
				if (controller.getBankItemCount(ingredients[2]) > 10) {
					controller.withdrawItem(ingredients[2], 10);
					controller.sleep(1280);
				} else {
					controller.withdrawItem(ingredients[2], controller.getBankItemCount(ingredients[2]));
					controller.sleep(1280);
				}
			} else {
				quit(2);
			}
		}
	}
	public void makeUnf() {
		controller.setStatus("@cya@Making Unfinished Potions");
		controller.useItemOnItemBySlot(controller.getInventoryItemSlotIndex(ingredients[1]), controller.getInventoryItemSlotIndex(ingredients[0]));
		controller.sleep(1280);
		while (controller.isBatching() && controller.isRunning()) {
			controller.sleep(640);
		}
	}
	public void mix() {
		int before = controller.getInventoryItemCount(ingredients[5]);
		controller.setStatus("@cya@Adding Secondary");
		controller.useItemOnItemBySlot(controller.getInventoryItemSlotIndex(ingredients[2]), controller.getInventoryItemSlotIndex(ingredients[5]));
		controller.sleep(1280);
		while (controller.isBatching() && controller.isRunning()) {
			controller.sleep(640);				
		}
		made += (before - controller.getInventoryItemCount(ingredients[5]));
	}
	
	public void withdrawPestle() {
		controller.setStatus("@cya@Withdrawing Items");
		if (controller.getBankItemCount(ingredients[1]) > 0 && controller.isRunning()) {
			controller.withdrawItem(ingredients[1], 1);
			controller.sleep(1280);
		} else {
			quit(2);
		}		
	}
	public void withdrawPre() {
		if (controller.getBankItemCount(ingredients[2]) > 0) {
			if (controller.getBankItemCount(ingredients[2]) > 29) {					
				controller.withdrawItem(ingredients[2], 29);
				controller.sleep(1280);
			} else {				
				controller.withdrawItem(ingredients[2], controller.getBankItemCount(ingredients[2]));
				controller.sleep(1280);						
			}
		} else {
			quit(2);
		}		
	}
	public void grind() {
		controller.setStatus("@cya@Grinding " + controller.getItemName(ingredients[2]));
		controller.useItemOnItemBySlot(controller.getInventoryItemSlotIndex(ingredients[1]), controller.getInventoryItemSlotIndex(ingredients[2]));
		controller.sleep(1280);
		while (controller.isBatching() && controller.isRunning()) {
			controller.sleep(640);
		}
		//add to total
		made += controller.getInventoryItemCount(ingredients[4]);
	}
	
	public void fillVials() {
		controller.setStatus("@cya@Filling Vials");
		try {
			int fountainCoords[] = controller.getNearestObjectById(1280);
			controller.useItemIdOnObject(fountainCoords[0], fountainCoords[1], ingredients[0]);
			controller.sleep(1280);
			while (controller.isCurrentlyWalking()) {
				controller.sleep(640);
			}
			while (controller.isBatching() && controller.isRunning()) {
				controller.sleep(640);
			}
		} catch (Exception e) {
			//No Fountain Nearby, wasn't in Falador west bank
			quit(3);
		}
	}
	public void cleanHerbs() {
		controller.setStatus("@cya@Cleaning Herbs");
		controller.itemCommand(ingredients[4]);
		controller.sleep(1280);
		while (controller.isBatching() && controller.isRunning()){
			controller.sleep(640);
		}
	}
	
	public void quit(Integer i) {
		if (!stopped) {
			if (i == 1) {
				controller.displayMessage("@red@Script stopped!");	
				controller.setStatus("@red@Script stopped!");			
			} else if (i == 2) {
				controller.displayMessage("@red@Out of ingredients!");
				controller.displayMessage("@red@This potion requires:");
				controller.displayMessage("@red@" + controller.getItemName(ingredients[1]));
				controller.displayMessage("@red@" + controller.getItemName(ingredients[2]));
				controller.setStatus("@red@Out of ingredients!");
			} else if (i == 3) {
				controller.displayMessage("@red@Unable to fill vials, run the script in Falador West Bank!");
				controller.setStatus("@red@Start in Falador west bank!");
			} else if (i == 4) {
				controller.displayMessage("@red@This potion requires level " + levelReq + " Herblaw to make.");
				controller.setStatus("@red@Herblaw level not high enough!");
			} else if (i == 5) {
				controller.displayMessage("@red@Out of vials.");
				controller.setStatus("@red@Out of vials!");
			}
		}
		stopped = true;
		setupCompleted = false;
		guiSetup = false;
		controller.stop();
	}
	
	@Override
	public void paintInterrupt() {
		 if(controller != null) {      	
            controller.drawBoxAlpha(7, 7, 124, 21+20, 0xFFFFFF, 64);
            controller.drawString("@whi@_________________", 10, 7, 0xFFFFFF, 1);
            controller.drawString("@gre@PotionMaker @whi@- @cya@Seatta", 10, 21, 0xFFFFFF, 1);
            controller.drawString("@whi@_________________", 10, 21+3, 0xFFFFFF, 1);
            if (ingredients[5] != 0) {
            	controller.drawString("@cya@    Potions", 10, 21+19, 0xFFFFFF, 1);
            } else {
            	controller.drawString("@cya@    Made", 10, 21+19, 0xFFFFFF, 1);            	
            }
            controller.drawString("@whi@_________________", 10, 21+23, 0xFFFFFF, 1);	
            controller.drawString("@whi@|", 68, 21+19, 0xFFFFFF, 1);	
            controller.drawString("@whi@" + made, 74, 21+19, 0xFFFFFF, 1);
		 }
	 }
}