package scripting.idlescript;

/**
 * Withdraws unidentified herbs from the bank, identifies them, deposits clean herbs. Rinse, repeat.
 * Does the highest level herb you can identify and have in bank.
 *
 * @author Dvorak
 */
public class HerbIdentifier extends IdleScript {

  final int[] unids = {933, 443, 442, 441, 440, 439, 438, 437, 436, 435, 165};

  int identifiedCount = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;

  public int countUnids() {
    int count = 0;
    for (int unid : unids) {
      count += controller.getInventoryItemCount(unid);
    }

    return count;
  }

  public boolean canIdentifyHerb(int herbId) {
    int lvl = Integer.MAX_VALUE;
    switch (herbId) {
      case 933:
        lvl = 75;
        break;
      case 443:
        lvl = 70;
        break;
      case 442:
        lvl = 65;
        break;
      case 441:
        lvl = 54;
        break;
      case 440:
        lvl = 48;
        break;
      case 439:
        lvl = 40;
        break;
      case 438:
        lvl = 25;
        break;
      case 437:
        lvl = 20;
        break;
      case 436:
        lvl = 11;
        break;
      case 435:
        lvl = 5;
        break;
      case 165:
        lvl = 3;
        break;
    }

    return controller.getCurrentStat(controller.getStatId("Herblaw")) >= lvl;
  }

  public int start(String[] param) {

    controller.displayMessage("@red@HerbIdentifier by Dvorak. Let's party like it's 2004!");
    controller.displayMessage("@red@Start in any bank with herbs in the bank!");

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (countUnids() == 0) {
        controller.setStatus("@whi@Banking...");
        controller.openBank();
        for (int id : controller.getInventoryItemIds()) {
          if (id != 1263) // don't deposit sleeping bags.
          {
            controller.depositItem(id, controller.getInventoryItemCount(id));
            controller.sleep(618);
            break;
          }
        }
        for (int id : unids) {
          if (controller.getBankItemCount(id) > 0 && canIdentifyHerb(id)) {
            controller.withdrawItem(id, controller.getBankItemCount(id));
            controller.sleep(618);
            break;
          }
        }
        controller.closeBank();
      } else {
        controller.setStatus("@whi@Identifying...");
        for (int id : unids) {
          if (controller.getInventoryItemCount(id) > 0) {
            controller.itemCommand(id);
            controller.sleep(250);
            while (controller.isBatching()) controller.sleep(10);
            break;
          }
        }
      }
    }
    return 1000; // start() must return a int value now.
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("This herb is")) identifiedCount++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int identifiedPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        identifiedPerHr = (int) (identifiedCount * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 190, 21 + 14, 0x228B22, 128);
      controller.drawString("@yel@HerbIdentifier @whi@by @yel@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@yel@Herbs Identified: @whi@"
              + String.format("%,d", identifiedCount)
              + " @yel@(@whi@"
              + String.format("%,d", identifiedPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
    }
  }
}
