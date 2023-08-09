package scripting.idlescript;

import bot.Main;
import controller.Controller;
import javax.swing.*;

/**
 * WIP master file for common commands used in Kaila combat Scripts
 *
 * <p>sans - documentation for now!
 *
 * @see scripting.idlescript.K_kailaScript
 * @author - Kaila
 */
/*
 *       todo
 *           fix teleport spot bounding
 *           replace eat food to loot with clearInventorySlot
 *          eat any 1 food script, return true/false to bank?
 *          make master list of all methods and variables,
 *              - full list near top,sub list each section!
 *          Javadoc for each method
 *          change prayer potions to prayer potion int[] method
 *          waitForBankOpen(); //temporary fix for npc desync issues,
 *              redo into better bank wait, using less sleep
 *
 * todo add int param to select how far above base to use boost potion
 *
 */
public class K_kailaCombatScript extends K_kailaScript {
  public static final Controller c = Main.getController();
}
  /*public void combineDef() {
  	if(!c.isInCombat()) {  //not working
  		if(c.getInventoryItemCount(497) > 1) {
  			c.useItemOnItemBySlot(c.getInventoryItemSlotIndex(497), c.getInventoryItemSlotIndex(497));   //just need to fix this somehow, maby list lost index of item id kinda thing!
  			c.sleep(340);
  		}
  		if(c.getInventoryItemCount(497) > 0 && c.getInventoryItemCount(496) > 0 ) {  //this part works!!!
  			c.useItemOnItemBySlot(c.getInventoryItemSlotIndex(497), c.getInventoryItemSlotIndex(496));
  			c.sleep(340);
  		}
  	}
  }*/
