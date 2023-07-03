package scripting.idlescript;

import orsc.ORSCharacter;

/**
 * Opens Holiday event Presents on an Iron, banks loot (coleslaw) only works on official irons.
 *
 * <p>start in any bank. @Author - Kaila
 */
public final class K_IronPresentOpener extends K_kailaScript {

  public int start(String[] parameters) {
    c.displayMessage("@ran@Iron Present Opener! Let's party like it's 2004!");

    while (c.isRunning()) {
      if (c.getInventoryItemCount(980) < 1) {
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
      if (c.getInventoryItemCount(980) > 0) {
        c.setStatus("@Gre@Opening..");

        c.itemCommand(980);
        c.sleep(650);
      }
    }

    return 1000; // start() must return an int value now.
  }

  public void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {

      if (c.getInventoryItemCount() > 0) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 980) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
            c.sleep(100);
          }
        }
      }
      if (c.getInventoryItemCount(980) < 29) {
        c.withdrawItem(980, 29 - c.getInventoryItemCount(980));
        c.sleep(650);
      }
      c.closeBank();
      c.sleep(640);
    }
  }
}
