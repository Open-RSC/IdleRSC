package scripting.idlescript;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import controller.Controller;

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
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.HIDDEN_FROM_SELECTOR},
          "Kaila",
          "WIP master file for common commands used in Kaila combat scripts");
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

// shitty autowalk
//  public void startWalking(int x, int y) {
//    // shitty autowalk
//    int newX = x;
//    int newY = y;
//    while (controller.currentX() != x || controller.currentY() != y) {
//      if (controller.currentX() - x > 23) {
//        newX = controller.currentX() - 20;
//      }
//      if (controller.currentY() - y > 23) {
//        newY = controller.currentY() - 20;
//      }
//      if (controller.currentX() - x < -23) {
//        newX = controller.currentX() + 20;
//      }
//      if (controller.currentY() - y < -23) {
//        newY = controller.currentY() + 20;
//      }
//      if (Math.abs(controller.currentX() - x) <= 23) {
//        newX = x;
//      }
//      if (Math.abs(controller.currentY() - y) <= 23) {
//        newY = y;
//      }
//      if (!controller.isTileEmpty(newX, newY)) {
//        controller.walkToAsync(newX, newY, 2);
//        controller.sleep(640);
//      } else {
//        controller.walkToAsync(newX, newY, 0);
//        controller.sleep(640);
//      }
//    }
//  }
