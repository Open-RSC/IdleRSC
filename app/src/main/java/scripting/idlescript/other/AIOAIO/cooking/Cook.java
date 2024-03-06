package scripting.idlescript.other.AIOAIO.cooking;

import bot.Main;
import controller.Controller;
import models.entities.ItemId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;

public class Cook {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (!meetsReqs()) {
      c.log("Missing required level to cook " + getRawFishId().name());
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 50;
    }
    if (c.isBatching()) return 680; // Wait to finish fishing
    else if (c.getInventoryItemCount(getRawFishId().getId()) <= 0) {
      if (!AIOAIO_Script_Utils.towardsGetFromBank(getRawFishId(), 30, true)) {
        c.log("Not enough " + getRawFishId().name() + " to cook! Skipping task");
        AIOAIO.state.endTime = System.currentTimeMillis();
      }
    } else if (c.currentX() == 432 && c.currentY() == 482) cookFish();
    else c.walkTowards(432, 482);
    return 250;
  }

  private static ItemId getRawFishId() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Shrimp":
        return ItemId.RAW_SHRIMP;
      case "Anchovies":
        return ItemId.RAW_ANCHOVIES;
      case "Trout":
        return ItemId.RAW_TROUT;
      case "Salmon":
        return ItemId.RAW_SALMON;
      case "Lobster":
        return ItemId.RAW_LOBSTER;
      case "Shark":
        return ItemId.RAW_SHARK;
      default:
        throw new IllegalStateException("Unknown fish type: " + AIOAIO.state.currentTask.getName());
    }
  }

  private static boolean meetsReqs() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Shrimp":
        return true;
      case "Anchovies":
        return true;
      case "Trout":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Cooking")) >= 15;
      case "Salmon":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Cooking")) >= 25;
      case "Lobster":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Cooking")) >= 40;
      case "Shark":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Cooking")) >= 80;
    }
    throw new IllegalStateException("Unknown tree type: " + AIOAIO.state.currentTask.getName());
  }

  private static void cookFish() {
    c.useItemIdOnObject(432, 480, getRawFishId().getId());
    c.waitForBatching(false);
  }
}
