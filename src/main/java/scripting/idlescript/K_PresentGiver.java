package scripting.idlescript;

import orsc.ORSCharacter;

/**
 * - Coleslaw Present Opener.
 *
 * <p>Opens coleslaw presents by using them on a 2nd account. Used in conjunction with K_GiftTaker
 * Script!
 *
 * <p>Should work in any bank, Ideal location is Draynor Bank! Requires 2 accounts. This bot is the
 * present "taker", it will bank when you have 29 items.
 *
 * <p>To setup start both accounts near each other with NO items in either inventory. start the
 * taker bot FIRST before even starting giver bot. the bots will need to be synced up similar to
 * trader bots. ideally monitor them, if something goes wrong present stuff will drop to the floor
 * and despawn!!!!! you have been warned!
 *
 * <p>WARNING: while within 1 tile of the giver, you will continue to recieve presents. WARNING:
 * regardless of how full your inventory is. items WILL drop to the floor.
 *
 * <p>Author - Kaila.
 */
public final class K_PresentGiver extends K_kailaScript {

  public int start(String[] parameters) {
    c.displayMessage("@red@present GIVER! Let's party like it's 2004! ~ Kaila");
    c.displayMessage("@red@Directions inside K_PresentGiver.java file");
    c.displayMessage("@red@Ideal location is Draynor Bank!");

    while (c.isRunning()) {
      if (c.getInventoryItemCount(980) < 2) {
        c.setStatus("@gre@Banking.");
        if (!c.isInBank()) {
          int[] bankerIds = {95, 224, 268, 540, 617, 792};
          ORSCharacter npc = c.getNearestNpcByIds(bankerIds, false);
          if (npc != null) {
            c.setStatus("@yel@Walking to Banker..");
            c.displayMessage("@yel@Walking to Banker..");
            c.walktoNPCAsync(npc.serverIndex);
            c.sleep(200);
          } else {
            c.log("@red@Error..");
            c.sleep(1000);
          }
        }
        bank();
      }
      if (c.getInventoryItemCount(980) > 1) {
        c.useItemOnPlayer(
            1, c.getPlayerServerIndexByName("ExampleAccountName")); // replace the player name
        c.sleep(640);
      }
    }

    return 1000; // start() must return an int value now.
  }

  private void bank() {

    c.setStatus("@yel@Banking..");
      c.openBank();
      c.sleep(640);
      if (!c.isInBank()) {
          waitForBankOpen();
      } else {
      if (c.getInventoryItemCount(980) < 2) {
        c.withdrawItem(980, 30);
        c.sleep(1280);
      }
      c.closeBank();
      c.setStatus("@gre@Opening.");
      c.sleep(1000);
    }
  }
}
