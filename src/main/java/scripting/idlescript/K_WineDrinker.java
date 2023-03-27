package scripting.idlescript;

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
  int vialsFilled = 0;
  int fullVials = 0;
  int emptyVials = 0;
  int objectx = 0;
  int objecty = 0;

  long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String parameters[]) {
    controller.displayMessage("@red@Wine Drinker!!");

    while (controller.isRunning()) {
      if (controller.getInventoryItemCount(142) < 1) {
        controller.setStatus("@gre@Banking..");
        controller.displayMessage("@gre@Banking..");
        if (!controller.isInBank()) {
          int[] bankerIds = {95, 224, 268, 540, 617, 792};
          ORSCharacter npc = controller.getNearestNpcByIds(bankerIds, false);
          if (npc != null) {
            controller.setStatus("@yel@Walking to Banker..");
            controller.displayMessage("@yel@Walking to Banker..");
            controller.walktoNPCAsync(npc.serverIndex);
            controller.sleep(200);
          } else {
            controller.log("@red@Error..");
            controller.sleep(1000);
          }
        }
        bank();
      }
      if (controller.getInventoryItemCount(142) > 0) {
        controller.setStatus("@gre@Drinking..");
        controller.itemCommand(142);
        controller.sleep(100);
      }
    }

    return 1000; // start() must return a int value now.
  }

  public void bank() {

    controller.setStatus("@yel@Banking..");
    controller.openBank();
    controller.sleep(640);

    if (controller.isInBank()) {

      if (controller.getInventoryItemCount(140) > 0) {
        controller.depositItem(140, controller.getInventoryItemCount(140));
        controller.sleep(100);
      }

      if (controller.getInventoryItemCount(246) > 0) {
        controller.depositItem(246, controller.getInventoryItemCount(246));
        controller.sleep(100);
      }

      if (controller.getInventoryItemCount(142) < 30) {
        controller.withdrawItem(142, 30 - controller.getInventoryItemCount());
        controller.sleep(650);
      }
      controller.closeBank();
      controller.sleep(650);
    }
  }
}