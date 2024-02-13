package scripting.idlescript;

import orsc.ORSCharacter;

/**
 * Uses the ledge in Yanille agility dungeon. Eats food, runs up stairs if fail. Logs out on low hp
 * + no food.
 *
 * <p>TODOS: if facing bat via character direction, attack it.
 *
 * @author Dvorak
 */
public class Ledger extends IdleScript {

  int successCount = 0;
  int failureCount = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String[] param) {

    controller.displayMessage("@red@Ledger by Dvorak. Let's party like it's 2004!");
    controller.displayMessage("@red@Start in Yanille agility dungeon with food!");
    controller.displayMessage(
        "@yel@WARNING:@red@ this script is crappy and not death safe, but it's better than the other ledger scripts.");

    int prevX = 0, prevY = 0;

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (controller.isInCombat()) controller.walkTo(controller.currentX(), controller.currentY());

      if (eatFood()) continue;

      killBlocks();

      if (countFood() <= 2) bank();

      if (controller.currentY() <= 3550) {
        controller.setStatus("@yel@Going back upstairs...");
        controller.atObject(605, 3509);
        controller.sleep(618);
      } else if (controller.currentY() <= 3557) {
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        controller.setStatus("@yel@Ledging...");
        controller.atObject(601, 3558);
        controller.sleep(618);
      } else if (controller.currentY() == 3563) {
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        controller.setStatus("@yel@Ledging...");
        controller.atObject(601, 3562);
        controller.sleep(618);
      }

      //			if(prevX == controller.currentX() && prevY == controller.currentY()) //detect and attack
      // bats which block us
      //				killBat();
      //
      //			prevX = controller.currentX();
      //			prevY = controller.currentY();

    }

    return 1000; // start() must return a int value now.
  }

  private void bank() {
    if (controller.currentY() <= 735) {
      controller.setStatus("@yel@Banking...");
      controller.walkPath(
          new int[] {
            603, 730,
            594, 734,
            589, 736,
            582, 741,
            582, 749,
            586, 753
          });

      eatFood();

      controller.openBank();

      boolean withdrew = false;
      while (controller.getInventoryItemCount() != 30) {
        for (int id : controller.getFoodIds()) {
          if (controller.getBankItemCount(id) > 0) {
            controller.withdrawItem(id, 30);
            controller.sleep(500);
            withdrew = true;
            break;
          }
        }

        if (!withdrew) {
          controller.setStatus("@red@We ran out of food! Logging out.");
          controller.setAutoLogin(false);
          controller.logout();
        }

        controller.sleep(618);

        eatFood();

        controller.walkPathReverse(
            new int[] {
              603, 725,
              603, 730,
              594, 734,
              589, 736,
              582, 741,
              582, 749,
              586, 753
            });

        controller.atObject(603, 722);
        controller.sleep(3000);
      }

    } else {
      if (!(controller.currentY() <= 3557)) { // wait until we are back upstairs
      } else {
        controller.atObject(603, 3554);
        controller.sleep(618);
      }
    }
  }

  private int countFood() {
    int totalFood = 0;
    for (int id : controller.getFoodIds()) {
      totalFood += controller.getInventoryItemCount(id);
    }
    return totalFood;
  }

  private void killBlocks() {
    if (controller.currentX() == 601 && controller.currentY() == 3557) return;

    int spiderServerIndex = controller.getBlockingNpcServerIndex(292);

    if (spiderServerIndex != -1) {
      controller.setStatus("@yel@Swatting spider away...");
      controller.attackNpc(spiderServerIndex);
      controller.sleep(618);
    }

    int batServerIndex = controller.getBlockingNpcServerIndex(43);

    if (batServerIndex != -1) {
      controller.setStatus("@yel@Swatting bat away...");
      controller.attackNpc(batServerIndex);
      controller.sleep(618);
    } else {
      if (controller.currentX() == 605 && controller.currentY() == 3553) {
        ORSCharacter npc = controller.getNpcAtCoords(604, 3553);

        if (npc == null) return;

        controller.attackNpc(npc.serverIndex);
        controller.sleep(618);
      }
    }

    //		if(controller.isInCombat())
    //			return;
    //
    //
    //
    //		ORSCharacter bat = controller.getNearestNpcById(43, false);
    //		if(bat != null) {
    //			int[] coords = controller.getNpcCoordsByServerIndex(bat.serverIndex);
    //			if(controller.distance(controller.currentX(), controller.currentY(), coords[0], coords[1])
    // <= 1) {
    //				controller.setStatus("@yel@Swatting bat away...");
    //				controller.attackNpc(bat.serverIndex);
    //				controller.sleep(618);
    //			}
    //		}
  }

  private boolean eatFood() {
    if (controller.getCurrentStat(controller.getStatId("Hits"))
        <= (controller.getBaseStat(controller.getStatId("Hits")) / 2)) {
      controller.setStatus("@red@Eating food");
      controller.walkTo(controller.currentX(), controller.currentY(), 0, true);

      boolean ate = false;

      for (int id : controller.getFoodIds()) {
        if (controller.getInventoryItemCount(id) > 0) {
          controller.itemCommand(id);
          ate = true;
          controller.sleep(250);
          break;
        }
      }

      return ate;
    }

    return false;
  }

  //	@Override
  //	public void playerDamagedInterrupt(int healthCurrent, int damageTaken) {
  //		eatFood();
  //	}

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("You skillfully balance")) successCount++;
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("you lose your footing")) failureCount++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int successesPerHr = 0;
      int failuresPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successesPerHr = (int) (successCount * scale);
        failuresPerHr = (int) (failureCount * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 150, 21 + 14 + 14, 0x228B22, 128);
      controller.drawString("@yel@Ledger @whi@by @yel@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@yel@Successes: @whi@"
              + String.format("%,d", successCount)
              + " @yel@(@whi@"
              + String.format("%,d", successesPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@yel@Failures: @whi@"
              + String.format("%,d", failureCount)
              + " @yel@(@whi@"
              + String.format("%,d", failuresPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
}
