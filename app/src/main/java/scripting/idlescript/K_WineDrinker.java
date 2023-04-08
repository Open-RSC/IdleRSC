package scripting.idlescript;

import bot.Main;
import controller.Controller;
import orsc.ORSCharacter;

/**
 * - Easy AF Wine drinker
 *
 * <p>Drinks wine from the bank, Banks the jugs and any half wine you might get. About 7k+ wines
 * processed per hour!
 *
 * <p>Author - Kaila
 */
public class K_WineDrinker extends IdleScript {
  private static final Controller c = Main.getController();
  private static final long nineMinutesInMillis = 540000L;

  public int start(String[] parameters) {
    c.displayMessage("@red@Wine Drinker!!");
    long next_attempt = System.currentTimeMillis() + 5000L;

    while (c.isRunning()) {
      if (System.currentTimeMillis() > next_attempt) {
        c.log("@red@Walking to Avoid Logging!");
        int x = c.currentX();
        int y = c.currentY();

        if (c.isReachable(x + 1, y, true)) c.walkTo(x + 1, y, 0, false);
        else if (c.isReachable(x - 1, y, true)) c.walkTo(x - 1, y, 0, false);
        else if (c.isReachable(x, y + 1, true)) c.walkTo(x, y + 1, 0, false);
        else if (c.isReachable(x, y - 1, true)) c.walkTo(x, y - 1, 0, false);
        c.sleep(640);
        next_attempt = System.currentTimeMillis() + nineMinutesInMillis;
        long nextAttemptInSeconds = (next_attempt - System.currentTimeMillis()) / 1000L;
        c.log("Done Walking to not Log, Next attempt in " + nextAttemptInSeconds + " seconds!");
      }
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

    return 1000; // start() must return an int value now.
  }

  private void bank() {

    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);

    if (c.isInBank()) {

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
      c.sleep(650);
    }
  }
}
