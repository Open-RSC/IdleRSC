package scripting.idlescript;

import java.util.Arrays;
import models.entities.ItemId;
import models.entities.SkillId;

public class CasketFisher extends IdleScript {
  int paintColors[] = {
    0xb74413, 0xcfdf1f, 0xffffff, 0xff0000
  }; // wooden-ish, golden....-ish, casket amount, trash amounts

  private final int[][] CATHERBY_BANK = {{437, 491}, {443, 496}};
  private final int[][] CATHERBY_FISHING_AREA = {{398, 495}, {420, 506}};
  private final int[][] WATER_OBELISK_ISLAND_OVERLAP = {{410, 505}, {417, 505}};

  final int casketId = ItemId.CASKET.getId();
  final int netId = ItemId.BIG_NET.getId();
  final int[] trashIds = {
    ItemId.RAW_MACKEREL.getId(),
    ItemId.RAW_COD.getId(),
    ItemId.RAW_BASS.getId(),
    ItemId.SEAWEED.getId(),
    ItemId.OYSTER.getId(),
    ItemId.LEATHER_GLOVES.getId(),
    ItemId.BOOTS.getId()
  };
  private int casketAmount = 0; // Caskets then trash items
  private int bankedCaskets = 0;

  public int start(String[] param) {
    paintBuilder.start(4, 18, 174);

    if (!isInRectangle(CATHERBY_BANK) && !isInRectangle(CATHERBY_FISHING_AREA)
        || isInRectangle(WATER_OBELISK_ISLAND_OVERLAP)) {
      controller.log(
          "Start this script in either Catherby Bank or the Catherby fishing area.", "red");
      controller.stop();
    }
    if (controller.getBaseStat(SkillId.FISHING.getId()) < 16 && controller.isRunning()) {
      controller.log("You need at least 16 fishing to use a big net!", "red");
      controller.stop();
    }

    dropJunk();
    if (controller.getInventoryItemCount(casketId) > 0
        || controller.getInventoryItemCount(netId) == 0) bank();

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (controller.getInventoryItemCount(casketId) < 20) {
        while (controller.currentX() != 406
            && controller.currentY() != 504
            && controller.isRunning()
            && controller.isLoggedIn()) {
          controller.setStatus("@cya@Walking to fishing spot");
          controller.walkTo(406, 504);
          controller.sleep(640);
        }

        if (!controller.isBatching() && controller.getInventoryItemCount() < 26) {
          controller.setStatus("@cya@Fishing");
          controller.atObject(406, 505);
          controller.sleep(640);
        }

        int x = controller.currentX();
        int y = controller.currentY();
        while (controller.isBatching() && controller.isRunning() && controller.isLoggedIn()) {
          if (controller.getInventoryItemCount() >= 26) {
            controller.stopBatching();
            while (controller.currentY() != y - 1
                && controller.isRunning()
                && controller.isLoggedIn()) {
              controller.walkTo(x, y - 1);
              controller.sleep(640);
            }
          }
        }
        if (!controller.isBatching() && controller.getInventoryItemCount() >= 26) dropJunk();
      } else {
        dropJunk();
        bank();
      }
    }
    controller.stop();
    casketAmount = 0;
    bankedCaskets = 0;
    return 1000; // start() must return a int value now.
  }

  public void dropJunk() {
    if (controller.isRunning()) {
      for (int i = 0; i < trashIds.length; i++) {
        if (controller.getInventoryItemCount(trashIds[i]) > 0
            && controller.isRunning()
            && controller.isLoggedIn()) {
          controller.setStatus("@cya@Dropping Junk");
          controller.dropItem(
              controller.getInventoryItemSlotIndex(trashIds[i]),
              controller.getInventoryItemCount(trashIds[i]));
          controller.sleep(1280);
          while (controller.getInventoryItemCount(trashIds[i]) > 0
              && controller.isRunning()
              && controller.isLoggedIn()) {
            controller.sleep(640);
          }
        }
      }
      controller.sleep(640);
    }
  }

  public void openDoor() {
    while (controller.getObjectAtCoord(439, 497) == 64
        && controller.isRunning()
        && controller.isLoggedIn()) {
      controller.atObject(439, 497);
      controller.sleep(100);
    }
  }

  public void bank() {
    controller.setStatus("@cya@Banking");
    if (!isInRectangle(CATHERBY_BANK)) {
      controller.walkTo(420, 497);
      controller.walkTo(439, 497);
    }
    openDoor();
    controller.openBank();
    while (!controller.isInBank() && controller.isRunning() && controller.isLoggedIn())
      controller.sleep(640);
    // Deposit all items but net
    bankedCaskets += controller.getInventoryItemCount(casketId);
    for (int i : controller.getInventoryItemIds()) {
      if (i != netId) {
        controller.depositItem(i, controller.getInventoryItemCount(i));
        while (controller.getInventoryItemCount(i) > 0
            && controller.isRunning()
            && controller.isLoggedIn()) controller.sleep(640);
      }
    }
    // Withdraw a net if not already held or give an error if no net is found.
    if (controller.getInventoryItemCount(netId) < 1) {
      if (controller.getBankItemCount(netId) < 1) {
        controller.log("You are missing a big net.", "red");
        controller.stop();
      } else {
        controller.withdrawItem(netId, 1);
      }
    }

    controller.walkTo(439, 496);
    openDoor();

    controller.setStatus("@cya@Walking to fishing spot");
    controller.walkTo(436, 497);
    controller.walkTo(430, 497);
    controller.walkTo(423, 496);
    controller.walkTo(419, 499);
  }

  public boolean isInRectangle(int[][] areaRectangle) {
    // Sorts the array to just in case to make it work correctly with the check
    int[] toSortX = {areaRectangle[0][0], areaRectangle[1][0]};
    int[] toSortY = {areaRectangle[0][1], areaRectangle[1][1]};
    Arrays.sort(toSortX);
    Arrays.sort(toSortY);
    int[][] sortedRectangle = {{toSortX[0], toSortY[0]}, {toSortX[1], toSortY[1]}};

    if (controller.currentX() >= sortedRectangle[0][0]
        && controller.currentX() <= sortedRectangle[1][0]
        && controller.currentY() >= sortedRectangle[0][1]
        && controller.currentY() <= sortedRectangle[1][1]) {
      return true;
    }
    return false;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      casketAmount = bankedCaskets + controller.getInventoryItemCount(casketId);

      paintBuilder.setBorderColor(0xBD93F9);
      paintBuilder.setBackgroundColor(0x282A36, 255);
      paintBuilder.setTitleMultipleColor(
          new String[] {"Casket", "Fisher"},
          new int[] {paintColors[0], paintColors[1]},
          new int[] {24, 68},
          4);
      paintBuilder.addRow(rowBuilder.centeredSingleStringRow("Seatta", 0xBD93F9, 1));
      paintBuilder.addRow(
          rowBuilder.centeredSingleStringRow(
              "Run Time: " + paintBuilder.stringRunTime, 0xffffff, 1));
      paintBuilder.addRow(
          rowBuilder.singleSpriteMultipleStringRow(
              casketId,
              80,
              20,
              new String[] {
                paintBuilder.stringFormatInt(casketAmount),
                paintBuilder.stringAmountPerHour(casketAmount)
              },
              new int[] {paintColors[2], 0x00ff00},
              new int[] {36, 52},
              16));
      paintBuilder.draw();
    }
  }
}
