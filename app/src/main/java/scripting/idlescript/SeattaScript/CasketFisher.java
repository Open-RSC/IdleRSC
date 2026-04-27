package scripting.idlescript.SeattaScript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import models.entities.ItemId;
import models.entities.Location;
import models.entities.SkillId;

public class CasketFisher extends SeattaScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.FISHING, Category.IRONMAN_SUPPORTED},
          "Seatta",
          "Fishes for caskets in Catherby. Also chisels oyster pearls into bolt tips.");

  int[] paintColors = {
    0xb74413, 0xcfdf1f, 0xffffff
  }; // wooden-ish, golden....-ish, casket amount, trash amounts

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

  public int start(String[] param) {
    paintBuilder.start(4, 18, 174);
    int maxItemCount = 26;
    int casketsPerTrip = 10;

    if (!isAtLocation(Location.CATHERBY_BANK) && !isAtLocation(Location.CATHERBY_FISHING_SPOT)) {
      c.log("Start this script in either Catherby Bank or the Catherby fishing area.", "red");
      c.stop();
    }
    if (c.getBaseStat(SkillId.FISHING.getId()) < 16 && c.isRunning()) {
      c.log("You need at least 16 fishing to use a big net!", "red");
      c.stop();
    }

    handleItems();
    if (c.getInventoryItemCount(casketId) > 0
        || c.getInventoryItemCount(pearlTipsId) > 0
        || c.getInventoryItemCount(netId) == 0
        || c.getInventoryItemCount(chiselId) == 0) bank();

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount(casketId) < casketsPerTrip) {
        while (c.currentX() != 406 && c.currentY() != 504 && c.isRunning() && c.isLoggedIn()) {
          c.setStatus("@cya@Walking to fishing spot");
          c.walkTo(406, 504);
          c.sleep(640);
        }

        if (!c.isBatching() && c.getInventoryItemCount() < maxItemCount) {
          c.setStatus("@cya@Fishing");
          c.atObject(406, 505);
          c.sleep(640);
        }

        int x = c.currentX();
        int y = c.currentY();
        while (c.isBatching() && c.isRunning() && c.isLoggedIn()) {
          if (c.getInventoryItemCount() >= maxItemCount) {
            c.stopBatching();
            while (c.currentY() != y - 1 && c.isRunning() && c.isLoggedIn()) {
              c.walkTo(x, y - 1);
              c.sleep(640);
            }
          }
        }
        if (!c.isBatching() && c.getInventoryItemCount() >= maxItemCount) handleItems();
      } else {
        handleItems();
        bank();
      }
    }
    c.stop();
    casketAmount = 0;
    bankedCaskets = 0;
    tipAmount = 0;
    bankedBoltTips = 0;
    return 1000; // start() must return a int value now.
  }

  public void handleItems() {
    if (c.isRunning()) {

      // Open oysters
      while (c.getInventoryItemCount(oysterId) > 0 && c.isRunning()) {
        c.setStatus("@cya@Opening oysters");
        c.itemCommand(oysterId);
        while (c.isBatching() && c.isRunning()) {
          if (c.getInventoryItemCount(oysterId) == 0) c.stopBatching();
          c.sleep(640);
        }
        c.sleep(640);
      }
      // Chisel pearls into bolt tips
      while (c.getInventoryItemCount(pearlsId) > 0 && c.isRunning()) {
        c.setStatus("@cya@Chiseling oyster pearls");
        c.useItemOnItemBySlot(
            c.getInventoryItemSlotIndex(chiselId), c.getInventoryItemSlotIndex(pearlsId));
        while (c.isBatching() && c.isRunning() && c.isLoggedIn()) {
          if (c.getInventoryItemCount(pearlsId) == 0) c.stopBatching();
          c.sleep(640);
        }
        c.sleep(640);
      }

      // Drop trash items
      for (int trashId : trashIds) {
        while (c.getInventoryItemCount(trashId) > 0 && c.isRunning() && c.isLoggedIn()) {
          String itemName = c.getItemName(trashId).toLowerCase();
          itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
          c.setStatus("@cya@Dropping Item: " + itemName);
          c.dropItem(c.getInventoryItemSlotIndex(trashId), c.getInventoryItemCount(trashId));
          c.sleep(1280);
        }
      }
      c.sleep(640);
    }
  }

  public void openDoor() {
    while (c.getObjectAtCoord(439, 497) == 64 && c.isRunning() && c.isLoggedIn()) {
      c.atObject(439, 497);
      c.sleep(100);
    }
  }

  public void bank() {
    c.setStatus("@cya@Banking");
    if (!isAtLocation(Location.CATHERBY_BANK)) {
      c.walkTo(420, 497);
      c.walkTo(439, 497);
    }
    openDoor();
    openNearestBank();

    // Deposit all items but net
    bankedCaskets += c.getInventoryItemCount(casketId);
    bankedBoltTips += c.getInventoryItemCount(pearlTipsId);
    for (int i : c.getInventoryItemIds()) {
      if (i != netId && i != chiselId) {
        c.depositItem(i, c.getInventoryItemCount(i));
        while (c.getInventoryItemCount(i) > 0 && c.isRunning() && c.isLoggedIn()) c.sleep(640);
      }
    }
    // Withdraw a net and chisel if not already held or give an error if they're not found.
    if (c.getInventoryItemCount(netId) < 1 || c.getInventoryItemCount(chiselId) < 1) {
      if (c.getBankItemCount(netId) < 1) {
        c.log("You are missing a big net.", "red");
        c.stop();
      } else {
        c.withdrawItem(netId, 1);
        c.sleep(640);
      }
      if (c.getBankItemCount(chiselId) < 1) {
        c.log("You are missing a chisel.", "red");
        c.stop();
      } else {
        c.withdrawItem(chiselId, 1);
        c.sleep(640);
      }
    }

    c.walkTo(439, 496);
    openDoor();

    c.setStatus("@cya@Walking to fishing spot");
    c.walkTo(436, 497);
    c.walkTo(430, 497);
    c.walkTo(423, 496);
    c.walkTo(419, 499);
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {

      casketAmount = bankedCaskets + c.getInventoryItemCount(casketId);
      tipAmount = bankedBoltTips + c.getInventoryItemCount(pearlTipsId);

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
