package scripting.idlescript;

/**
 * -
 *
 * <p>K_GiftTaker - by Kaila
 *
 * <p>This bot will open the bank and automatically bank when it gets close to a full inventory.
 *
 * <p>Tested on Christmas crackers and Christmas holiday presents! Should work in any bank, Ideal
 * one is draynor! requires 2 accounts This bot is the present "taker", it will bank when you have
 * 29 items
 *
 * <p>To setup start both accounts near each other with NO items in either inventory start the taker
 * bot FIRST before even starting giver bot the bots will need to be synced up similar to trader
 * bots ideally monitor them, if something goes wrong present stuff will drop to the floor and
 * despawn!!!!! you have been warned!
 *
 * <p>WARNING: while within 1 tile of the giver, you will continue to recieve presents WARNING:
 * regardless of how full your inventory is. items WILL drop to the floor Recommend observing bot
 * due to high value of rares.
 *
 * <p>Author - Kaila
 */
public final class K_GiftTaker extends K_kailaScript {

  public int start(String[] parameters) {
    c.displayMessage("@red@present TAKER! Let's party like it's 2004! ~ by Kaila");
    c.setStatus("@gre@Running..");
    c.openBank();
    c.sleep(1240);

    if (!c.isInBank()) {
      c.openBank();
      c.sleep(1240);
    }
    if (c.isInBank()) {
      if (c.getInventoryItemCount() < 20) {
        c.sleep(100);
      }
      if (c.getInventoryItemCount() > 19) {
        depositAll();
      }
      c.closeBank();
      c.sleep(1240);
    }
    return 1000; // start() must return an int value now.
  }
}
