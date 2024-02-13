package scripting.idlescript;

import orsc.ORSCharacter;

/**
 * <b>Easy AF Wine drinker</b>
 *
 * <p>Drinks wine from the bank, Banks the jugs and any half wine you might get. <br>
 * About 7k+ wines processed per hour! <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_WineDrinker extends K_kailaScript {
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    // c.quitIfAuthentic();
    //    if (!guiSetup) {
    //      setupGUI();
    //      guiSetup = true;
    //    }
    //    if (scriptStarted) {
    c.displayMessage("@red@Wine Drinker!!");
    long next_attempt = System.currentTimeMillis() + 5000L;
    // guiSetup = false;
    // scriptStarted = false;
    if (c.isInBank()) c.closeBank();
    startTime = System.currentTimeMillis();
    scriptStart();
    //   }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount(142) < 1) {
        c.setStatus("@gre@Banking..");
        c.displayMessage("@gre@Banking..");
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
      if (c.getInventoryItemCount(142) > 0) {
        c.setStatus("@gre@Drinking..");
        c.itemCommand(142);
        c.sleep(100);
      }
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      K_kailaScript.waitForBankOpen();
    } else {
      if (c.getInventoryItemCount(140) > 0) {
        c.depositItem(140, c.getInventoryItemCount(140));
        c.sleep(100);
      }
      if (c.getInventoryItemCount(246) > 0) {
        c.depositItem(246, c.getInventoryItemCount(246));
        c.sleep(100);
      }
      if (c.getInventoryItemCount(142) < 30) {
        c.withdrawItem(142, 30 - c.getInventoryItemCount());
        c.sleep(650);
      }
      c.closeBank();
    }
  }
}
