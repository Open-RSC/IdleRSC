package scripting.idlescript;

import orsc.ORSCharacter;

/**
 * Collects anti dragon shields from Duke of Lumbridge.
 *
 * @author Dvorak Fixed by Kaila
 */
public class AntiDragonShields extends IdleScript {
  long startTimestamp = System.currentTimeMillis() / 1000L;
  int success = 0;

  final int[] bankPath = {
    128, 659,
    122, 659,
    116, 656,
    126, 646,
    148, 646,
    173, 644,
    187, 642,
    211, 638,
    220, 633
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    controller.displayMessage("@red@AntiDragonShields by Dvorak, Fixes by Kaila");
    controller.displayMessage("@red@Start near Duke of Lumbridge");

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);

      int targetAmount = 30; // - controller.getInventoryItemCount();
      int stack = 5;

      if (controller.getInventoryItemCount() < 30 && controller.isRunning()) {
        int totalCount =
            controller.getGroundItemAmount(420, 133, 1603) + controller.getInventoryItemCount(420);

        controller.displayMessage("@red@" + totalCount + " of " + targetAmount + " collected");

        if (controller.isAuthentic()) {
          if (controller.getGroundItemAmount(420, 133, 1603) == 5
              || controller.getGroundItemAmount(420, 133, 1603) == 11
              || controller.getGroundItemAmount(420, 133, 1603) == 17
              || controller.getGroundItemAmount(420, 133, 1603) == 23
              || controller.getGroundItemAmount(420, 133, 1603) == 29) {
            controller.setStatus("@red@Picking up shields to prevent despawn..");
            while (controller.getNearestItemById(420) != null
                && controller.getInventoryItemCount() < 30) {
              controller.pickupItem(133, 1603, 420, true, false);
              controller.sleep(100);
            }
            stack += 5;
          }
          if (totalCount == targetAmount) {
            controller.setStatus("@red@Picking up shields to bank..");
            while (controller.getNearestItemById(420) != null
                && controller.getInventoryItemCount() < 30) {
              controller.pickupItem(133, 1603, 420, true, false);
              controller.sleep(100);
            }
          }
        }
        if (controller.getInventoryItemCount(420) == targetAmount) {
          continue;
        }
        if (controller.isAuthentic()) {
          while (controller.getInventoryItemCount(420) > 0) {
            controller.setStatus("@red@Dropping shield(s)..");
            controller.walkTo(133, 1603);
            controller.dropItem(controller.getInventoryItemSlotIndex(420));
            controller.sleep(300);
          }
        }
        ORSCharacter npc = controller.getNearestNpcById(198, false);

        if (npc != null) {
          controller.setStatus("@red@Getting shield from Duke..");
          controller.talkToNpc(npc.serverIndex);
          controller.sleep(4000);

          if (!controller.isInOptionMenu()) continue;

          controller.optionAnswer(0);
          controller.sleep(7000);
        }
      } else {
        toBank();
        toDuke();
      }
    }

    return 1000; // start() must return a int value now.
  }

  public void openDoor() {
    int[] coords = null;
    do {
      coords = controller.getNearestObjectById(64);
      if (coords != null) {
        controller.atObject(coords[0], coords[1]);
        controller.sleep(618);
      }
    } while (coords != null);
  }

  public void toBank() {
    controller.setStatus("@red@Going to bank..");
    controller.walkTo(138, 1610);

    if (controller.currentX() == 138 && controller.currentY() == 1610) {
      controller.atObject(139, 1610);
      controller.sleep(1000);
    }

    controller.walkTo(129, 659);
    openDoor();
    controller.walkTo(128, 659);

    controller.walkPath(bankPath);
    openDoor();
    controller.walkTo(220, 634);

    controller.openBank();

    if (controller.getInventoryItemCount(420) > 0) {
      controller.depositItem(420, controller.getInventoryItemCount(420));
      controller.sleep(300);
    }
    controller.closeBank();
  }

  public void toDuke() {
    controller.setStatus("@red@Going to the Duke..");
    openDoor();
    controller.walkPathReverse(bankPath);
    openDoor();

    controller.walkTo(138, 666);
    if (controller.currentX() == 138 && controller.currentY() == 666) {
      controller.atObject(139, 666);
      controller.sleep(1000);
    }
    controller.walkTo(134, 1603);
    openDoor();
    controller.walkTo(133, 1603);
  }
}
