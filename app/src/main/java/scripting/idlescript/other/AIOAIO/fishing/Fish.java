package scripting.idlescript.other.AIOAIO.fishing;

import bot.Main;
import controller.Controller;
import models.entities.ItemId;
import scripting.idlescript.other.AIOAIO.AIOAIO;

public class Fish {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (c.getInventoryItemCount(ItemId.NET.getId()) <= 0) return getNetFromBank();
    if (c.getInventoryItemCount() >= 30) return bankFish();
    if (c.isBatching()) return 250; // Wait to finish fishing
    if (c.getNearestReachableObjectById(getFishingSpotId(), true) != null) return fish();
    else return findFishingSpot();
  }

  private static int getFishingSpotId() {
    return 193;
  }

  private static int findFishingSpot() {
    c.setStatus("Walking to Catherby");
    c.walkTowards(410, 502);
    return 50;
  }

  private static int fish() {
    c.setStatus("Fishing shrimp");
    int[] fishCoords = c.getNearestReachableObjectById(getFishingSpotId(), true);
    c.atObject(fishCoords[0], fishCoords[1]);
    return 1200;
  }

  private static int bankFish() {
    if (c.getNearestNpcById(95, false) == null) {
      c.setStatus("Walking to Bank");
      c.walkTowards(c.getNearestBank()[0], c.getNearestBank()[1]);
      return 50;
    }
    c.setStatus("Opening bank");
    c.openBank();
    Fishing_Utils.depositAllExceptNet();
    c.closeBank();
    return 680;
  }

  private static int getNetFromBank() {
    if (c.getNearestNpcById(95, false) == null) {
      c.setStatus("Walking to Bank");
      c.walkTowards(c.getNearestBank()[0], c.getNearestBank()[1]);
      return 50;
    }
    c.setStatus("Opening bank");
    c.openBank();
    c.sleep(680);
    if (c.getBankItemCount(ItemId.NET.getId()) <= 0) {
      c.log("No net! Skipping task..");
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 50;
    }
    c.setStatus("Withdrawing net");
    c.sleep(680);
    c.withdrawItem(ItemId.NET.getId());
    c.sleep(680);
    c.closeBank();
    return 680;
  }
}
