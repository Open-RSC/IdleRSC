package scripting.idlescript;

import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;
import java.util.Arrays;
import models.entities.ItemId;
import models.entities.SkillId;

public class CasketFisher extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.FISHING, Category.IRONMAN_SUPPORTED},
          "Seatta",
          "Fishes for caskets in Catherby. Also chisels oyster pearls into bolt tips.");

  int[] paintColors = {
    0xb74413, 0xcfdf1f, 0xffffff
  }; // wooden-ish, golden....-ish, casket amount, trash amounts

  private final int[][] CATHERBY_BANK = {{437, 491}, {443, 496}};
  private final int[][] CATHERBY_FISHING_AREA = {{398, 495}, {420, 506}};
  private final int[][] WATER_OBELISK_ISLAND_OVERLAP = {{410, 505}, {417, 505}};

  final int casketId = ItemId.CASKET.getId();
  final int netId = ItemId.BIG_NET.getId();
  final int oysterId = ItemId.OYSTER.getId();
  final int pearlsId = ItemId.OYSTER_PEARLS.getId();
  final int pearlTipsId = ItemId.OYSTER_PEARL_BOLT_TIPS.getId();
  final int chiselId = ItemId.CHISEL.getId();
  final int[] trashIds = {
    ItemId.RAW_MACKEREL.getId(),
    ItemId.RAW_COD.getId(),
    ItemId.RAW_BASS.getId(),
    ItemId.EMPTY_OYSTER.getId(),
    ItemId.SEAWEED.getId(),
    ItemId.LEATHER_GLOVES.getId(),
    ItemId.BOOTS.getId()
  };
  private int casketAmount = 0;
  private int bankedCaskets = 0;
  private int tipAmount = 0;
  private int bankedBoltTips = 0;

  private final int casketsPerTrip = 10;
  private final int maxItemCount = 26;

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

    handleItems();
    if (controller.getInventoryItemCount(casketId) > 0
        || controller.getInventoryItemCount(pearlTipsId) > 0
        || controller.getInventoryItemCount(netId) == 0
        || controller.getInventoryItemCount(chiselId) == 0) bank();

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (controller.getInventoryItemCount(casketId) < casketsPerTrip) {
        while (controller.currentX() != 406
            && controller.currentY() != 504
            && controller.isRunning()
            && controller.isLoggedIn()) {
          controller.setStatus("@cya@Walking to fishing spot");
          controller.walkTo(406, 504);
          controller.sleep(640);
        }

        if (!controller.isBatching() && controller.getInventoryItemCount() < maxItemCount) {
          controller.setStatus("@cya@Fishing");
          controller.atObject(406, 505);
          controller.sleep(640);
        }

        int x = controller.currentX();
        int y = controller.currentY();
        while (controller.isBatching() && controller.isRunning() && controller.isLoggedIn()) {
          if (controller.getInventoryItemCount() >= maxItemCount) {
            controller.stopBatching();
            while (controller.currentY() != y - 1
                && controller.isRunning()
                && controller.isLoggedIn()) {
              controller.walkTo(x, y - 1);
              controller.sleep(640);
            }
          }
        }
        if (!controller.isBatching() && controller.getInventoryItemCount() >= maxItemCount)
          handleItems();
      } else {
        handleItems();
        bank();
      }
    }
    controller.stop();
    casketAmount = 0;
    bankedCaskets = 0;
    tipAmount = 0;
    bankedBoltTips = 0;
    return 1000; // start() must return a int value now.
  }

  public void handleItems() {
    if (controller.isRunning()) {

      // Open oysters
      while (controller.getInventoryItemCount(oysterId) > 0 && controller.isRunning()) {
        controller.setStatus("@cya@Opening oysters");
        controller.itemCommand(oysterId);
        while (controller.isBatching() && controller.isRunning()) {
          if (controller.getInventoryItemCount(oysterId) == 0) controller.stopBatching();
          controller.sleep(640);
        }
        controller.sleep(640);
      }
      // Chisel pearls into bolt tips
      while (controller.getInventoryItemCount(pearlsId) > 0 && controller.isRunning()) {
        controller.setStatus("@cya@Chiseling oyster pearls");
        controller.useItemOnItemBySlot(
            controller.getInventoryItemSlotIndex(chiselId),
            controller.getInventoryItemSlotIndex(pearlsId));
        while (controller.isBatching() && controller.isRunning() && controller.isLoggedIn()) {
          if (controller.getInventoryItemCount(pearlsId) == 0) controller.stopBatching();
          controller.sleep(640);
        }
        controller.sleep(640);
      }

      // Drop trash items
      for (int trashId : trashIds) {
        while (controller.getInventoryItemCount(trashId) > 0
            && controller.isRunning()
            && controller.isLoggedIn()) {
          String itemName = controller.getItemName(trashId).toLowerCase();
          itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
          controller.setStatus("@cya@Dropping Item: " + itemName);
          controller.dropItem(
              controller.getInventoryItemSlotIndex(trashId),
              controller.getInventoryItemCount(trashId));
          controller.sleep(1280);
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
    bankedBoltTips += controller.getInventoryItemCount(pearlTipsId);
    for (int i : controller.getInventoryItemIds()) {
      if (i != netId && i != chiselId) {
        controller.depositItem(i, controller.getInventoryItemCount(i));
        while (controller.getInventoryItemCount(i) > 0
            && controller.isRunning()
            && controller.isLoggedIn()) controller.sleep(640);
      }
    }
    // Withdraw a net and chisel if not already held or give an error if they're not found.
    if (controller.getInventoryItemCount(netId) < 1
        || controller.getInventoryItemCount(chiselId) < 1) {
      if (controller.getBankItemCount(netId) < 1) {
        controller.log("You are missing a big net.", "red");
        controller.stop();
      } else {
        controller.withdrawItem(netId, 1);
        controller.sleep(640);
      }
      if (controller.getBankItemCount(chiselId) < 1) {
        controller.log("You are missing a chisel.", "red");
        controller.stop();
      } else {
        controller.withdrawItem(chiselId, 1);
        controller.sleep(640);
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

    return controller.currentX() >= sortedRectangle[0][0]
        && controller.currentX() <= sortedRectangle[1][0]
        && controller.currentY() >= sortedRectangle[0][1]
        && controller.currentY() <= sortedRectangle[1][1];
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      casketAmount = bankedCaskets + controller.getInventoryItemCount(casketId);
      tipAmount = bankedBoltTips + controller.getInventoryItemCount(pearlTipsId);

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
      paintBuilder.addRow(
          rowBuilder.singleSpriteMultipleStringRow(
              pearlTipsId,
              80,
              25,
              new String[] {
                paintBuilder.stringFormatInt(tipAmount), paintBuilder.stringAmountPerHour(tipAmount)
              },
              new int[] {paintColors[2], 0x00ff00},
              new int[] {31, 52},
              12));
      paintBuilder.draw();
    }
  }
}
