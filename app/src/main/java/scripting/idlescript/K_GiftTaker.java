package scripting.idlescript;

/**
 * <b>Holiday Gift Taker</b>
 *
 * <p>This bot will open the bank and automatically bank when it gets close to a full inventory.
 * Tested on Christmas crackers and Christmas holiday presents! Should work in any bank, Ideal one
 * is draynor! requires 2 accounts This bot is the present "taker", it will bank when you have 29
 * items <br>
 *
 * <p>To setup start both accounts near each other with NO items in either inventory start the taker
 * bot FIRST before even starting giver bot the bots will need to be synced up similar to trader
 * bots ideally monitor them, if something goes wrong present stuff will drop to the floor and
 * despawn!!!!! you have been warned! <br>
 *
 * <p>WARNING: while within 1 tile of the giver, you will continue to recieve presents WARNING:
 * regardless of how full your inventory is. items WILL drop to the floor Recommend observing bot
 * due to high value of rares. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_GiftTaker extends K_kailaScript {
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    c.quitIfAuthentic();
    //    if (!guiSetup) {
    //      setupGUI();
    //      guiSetup = true;
    //    }
    //    if (scriptStarted) {
    c.displayMessage("@red@present TAKER! Let's party like it's 2004! ~ by Kaila");
    c.setStatus("@gre@Running..");
    // guiSetup = false;
    // scriptStarted = false;
    // if (c.isInBank()) c.closeBank();
    startTime = System.currentTimeMillis();
    scriptStart();
    //   }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.isInBank()) {
        if (c.getInventoryItemCount() < 20) {
          c.sleep(100);
        }
        if (c.getInventoryItemCount() > 19) {
          depositAll();
        }
        // c.closeBank();
      } else if (!c.isInBank()) {
        c.openBank();
        c.sleep(2 * GAME_TICK);
      }
    }
  }
}
