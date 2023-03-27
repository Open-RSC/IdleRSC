package scripting.idlescript;

import orsc.ORSCharacter;

/**
 * - Opens Holiday event Presents on an Iron, banks loot (coleslaw) only works on official irons.
 * start in any bank.
 *
 * <p>Author - Kaila
 */
public class K_IronPresentOpener extends IdleScript {
  int objectx = 0;
  int objecty = 0;

  long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String parameters[]) {
    controller.displayMessage("@ran@Iron Present Opener! Let's party like it's 2004!");

    while (controller.isRunning()) {
      if (controller.getInventoryItemCount(980) < 1) {
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
      if (controller.getInventoryItemCount(980) > 0) {
        controller.setStatus("@Gre@Opening..");

        controller.itemCommand(980);
        controller.sleep(650);
      }
    }

    return 1000; // start() must return a int value now.
  }

  public void bank() {

    controller.setStatus("@yel@Banking..");
    controller.openBank();
    controller.sleep(640);

    if (controller.isInBank()) {

      if (controller.getInventoryItemCount() > 0) {
        for (int itemId : controller.getInventoryItemIds()) {
          if (itemId != 980) {
            controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
            controller.sleep(100);
          }
        }
      }
      if (controller.getInventoryItemCount(980) < 29) {
        controller.withdrawItem(980, 29 - controller.getInventoryItemCount(980));
        controller.sleep(650);
      }
      controller.closeBank();
      controller.sleep(640);
    }
  }
}
