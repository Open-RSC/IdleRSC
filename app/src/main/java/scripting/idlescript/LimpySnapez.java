package scripting.idlescript;

/**
 * Harvests limpwurt roots and snape grass in Taverly. Coleslaw only.
 *
 * @author Dvorak
 */
public class LimpySnapez extends IdleScript {

  final int[] herbToDoorPath = {366, 472, 360, 478, 353, 484, 347, 487, 342, 487, 342, 488};

  final int[] doorToBankPath = {
    338, 488, 335, 493, 329, 498, 324, 501, 318, 507, 316, 513, 316, 518, 320, 526, 323, 535, 325,
    544, 327, 552
  };

  final int[] doorToHerbPath = {347, 487, 353, 484, 360, 478, 366, 472};

  final int[] bankToDoorPath = {
    327, 552, 325, 544, 323, 535, 320, 526, 316, 518, 316, 513, 318, 507, 324, 501, 329, 498, 335,
    493, 338, 488, 341, 488
  };

  final int[] plants = {1273, 1281};
  final int[] loot = {220, 469};

  boolean tileFlick = false;

  int snapezPicked = 0;
  int snapezInBank = 0;
  int limpzPicked = 0;
  int limpzInBank = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String[] param) {

    controller.displayMessage("@red@LimpySnapez by Dvorak. Let's party like it's 2004!");
    controller.displayMessage("@red@Start in Taverly with herb clippers!");
    controller.quitIfAuthentic();

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (controller.getInventoryItemCount() < 30) {

        boolean foundPlants = false;
        for (int plantId : plants) {
          int[] coords = controller.getNearestObjectById(plantId);

          if (coords != null) {
            controller.setStatus("@whi@Picking plant.");
            foundPlants = true;
            controller.atObject(coords[0], coords[1]);
            controller.sleep(1000);
            while (controller.getInventoryItemCount() < 30 && controller.isBatching())
              controller.sleep(10);
            break;
          }
        }

        if (!foundPlants) {
          controller.setStatus("@whi@Searching for plants...");
          if (!tileFlick) {
            controller.walkTo(364, 472);
          } else {
            controller.walkTo(364, 471);
          }
          tileFlick = !tileFlick;
          controller.sleep(700);
        }

      } else {
        walkToBank();
        bank();
        walkToTaverly();
      }

      controller.sleep(100);
    }

    return 1000; // start() must return a int value now.
  }

  public void walkToBank() {
    controller.setStatus("@whi@Walking to bank....");
    controller.walkPath(herbToDoorPath);
    controller.sleep(1000);

    // open gate
    while (controller.currentX() != 341 || controller.currentY() != 487) {
      controller.displayMessage("@red@Opening door..");
      if (controller.getObjectAtCoord(341, 487) == 137) controller.atObject(341, 487);
      controller.sleep(5000);
    }

    controller.walkPath(doorToBankPath);

    // open bank door
    while (controller.getObjectAtCoord(327, 552) == 64) {
      controller.atObject(327, 552);
      controller.sleep(100);
    }

    controller.walkTo(328, 552);
  }

  public int countPlants() {
    int count = 0;
    for (int j : loot) {
      count += controller.getInventoryItemCount(j);
    }

    return count;
  }

  public void bank() {
    controller.setStatus("@whi@Banking...");

    controller.openBank();

    while (countPlants() > 0) {
      for (int j : loot) {
        if (controller.getInventoryItemCount(j) > 0) {
          controller.depositItem(j, controller.getInventoryItemCount(j));
          controller.sleep(250);
        }
      }
    }

    snapezInBank = controller.getBankItemCount(220);
    limpzInBank = controller.getBankItemCount(469);
  }

  public void walkToTaverly() {
    controller.setStatus("@whi@Walking back to Taverly...");

    while (controller.getObjectAtCoord(327, 552) == 64) {
      controller.atObject(327, 552);
      controller.sleep(100);
    }

    controller.walkTo(327, 552);

    controller.walkPath(bankToDoorPath);
    controller.sleep(1000);

    // open door
    while (controller.currentX() != 342 || controller.currentY() != 487) {
      controller.displayMessage("@red@Opening door...");
      if (controller.getObjectAtCoord(341, 487) == 137) controller.atObject(341, 487);
      controller.sleep(5000);
    }

    controller.walkPath(doorToHerbPath);
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("some grass")) snapezPicked++;
    else if (message.contains("some root")) limpzPicked++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int snapezPerHr = 0;
      int limpzPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        snapezPerHr = (int) (snapezPicked * scale);
        limpzPerHr = (int) (limpzPicked * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 160, 21 + 14 + 14 + 14 + 14, 0xFFFFFF, 128);
      controller.drawString("@lre@Limpy@gre@Snapez @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@gre@Snapez picked: @whi@"
              + String.format("%,d", snapezPicked)
              + " @gre@(@whi@"
              + String.format("%,d", snapezPerHr)
              + "@gre@/@whi@hr@gre@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@gre@Snapez in bank: @whi@" + String.format("%,d", snapezInBank),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@lre@Limpz picked: @whi@"
              + String.format("%,d", limpzPicked)
              + " @lre@(@whi@"
              + String.format("%,d", limpzPerHr)
              + "@lre@/@whi@hr@lre@)",
          10,
          21 + 14 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@lre@Limpz in bank: @whi@" + String.format("%,d", limpzInBank),
          10,
          21 + 14 + 14 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
}
