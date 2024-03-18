package scripting.idlescript.other.AIOAIO.fishing;

import bot.Main;
import controller.Controller;
import models.entities.ItemId;
import models.entities.NpcId;
import models.entities.SceneryId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;

public class Fish {
  private static Controller c;
  private static boolean needBuyTool = false;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();
    if (AIOAIO.state.taskStartup) {
      needBuyTool =
          false; // reset this, since it may have previously wanted to buy a different tool
      AIOAIO.state.taskStartup = false;
    }

    if (!meetsReqs()) {
      c.log("Missing required level to fish " + AIOAIO.state.currentTask.getName());
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 50;
    }
    if (needBuyTool && c.getInventoryItemCount(ItemId.COINS.getId()) < getNeededCoins())
      return getCoinsFromBank();
    else if (needBuyTool) return buyTool();
    else if (!hasFishingTool()) return getToolFromBank();
    else if (c.getInventoryItemCount() >= 30)
      AIOAIO_Script_Utils.towardsDepositAll(fishTool().getId(), ItemId.FEATHER.getId());
    else if (c.isBatching()) return 250; // Wait to finish fishing
    else if (c.getNearestReachableObjectById(getFishingSpotId(), true) != null) return fish();
    else return findFishingSpot();
    return 50;
  }

  private static int getNeededCoins() {
    return AIOAIO.state.currentTask.getName().equals("Salmon") ? 1405 : 30;
  }

  private static boolean hasFishingTool() {
    if (AIOAIO.state.currentTask.getName().equals("Salmon")
        && c.getInventoryItemCount(ItemId.FEATHER.getId()) <= 0) return false;
    return c.getInventoryItemCount(fishTool().getId()) >= 1;
  }

  private static ItemId fishTool() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Shrimp":
        return ItemId.NET;
      case "Salmon":
        return ItemId.FLY_FISHING_ROD;
      case "Lobster":
        return ItemId.LOBSTER_POT;
      case "Shark":
        return ItemId.HARPOON;
    }
    throw new IllegalStateException("Unknown tree type: " + AIOAIO.state.currentTask.getName());
  }

  private static boolean meetsReqs() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Shrimp":
        return true;
      case "Salmon":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fishing")) >= 20;
      case "Lobster":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fishing")) >= 40;
      case "Shark":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fishing")) >= 76;
    }
    throw new IllegalStateException("Unknown tree type: " + AIOAIO.state.currentTask.getName());
  }

  private static int getFishingSpotId() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Shrimp":
        return SceneryId.FISH_NET_BAIT.getId();
      case "Salmon":
        return SceneryId.FISH_LURE_BAIT.getId();
      case "Lobster":
        return SceneryId.FISH_HARPOON_CAGE.getId();
      case "Shark":
        return SceneryId.FISH_HARPOON_CAGE.getId();
    }
    throw new IllegalStateException("Unknown tree type: " + AIOAIO.state.currentTask.getName());
  }

  private static int findFishingSpot() {
    if (AIOAIO.state.currentTask.getName().equals("Salmon")) {
      AIOAIO.state.status = ("Walking to Barbarian Villiage");
      c.walkTowards(211, 500);
    } else {
      AIOAIO.state.status = ("Walking to Catherby");
      c.walkTowards(410, 502);
    }
    return 50;
  }

  private static int fish() {
    AIOAIO.state.status = ("Fishing " + AIOAIO.state.currentTask.getName());
    int[] fishCoords = c.getNearestReachableObjectById(getFishingSpotId(), true);
    if (AIOAIO.state.currentTask.getName() != "Shark") c.atObject(fishCoords[0], fishCoords[1]);
    else c.atObject2(fishCoords[0], fishCoords[1]);
    return 1200;
  }

  private static int getToolFromBank() {
    if (!AIOAIO_Script_Utils.towardsGetFromBank(fishTool(), 1, true)) {
      needBuyTool = true;
    }

    if (AIOAIO.state.currentTask.getName().equals("Salmon")) {
      if (!AIOAIO_Script_Utils.towardsGetFromBank(ItemId.FEATHER, -1, false)) {
        needBuyTool = true;
      }
    }
    return 50;
  }

  private static int getCoinsFromBank() {
    // +40 incase bot decides to take Al Kharid Gate, a boat or something
    if (!AIOAIO_Script_Utils.towardsGetFromBank(ItemId.COINS, getNeededCoins() + 40, true)) {
      Main.getController().log("Legit too poor to fish.. Skipping task");
      AIOAIO.state.endTime = System.currentTimeMillis();
    }
    return 50;
  }

  private static int buyTool() {
    AIOAIO.state.status = ("Buying " + fishTool().name());
    if (c.isInShop()) {
      c.log("Buying " + fishTool().name());
      c.shopBuy(fishTool().getId());
      if (AIOAIO.state.currentTask.getName().equals("Salmon")) {
        c.log("Buying feathers");
        c.sleep(600);
        c.shopBuy(ItemId.FEATHER.getId(), 200);
      }
      c.closeShop();
      c.sleep(1200);
      if (c.getInventoryItemCount(fishTool().getId()) >= 1) {
        c.log("Got " + fishTool().name());
        needBuyTool = false;
      } else {
        c.log("Failed to get " + fishTool().name());
      }
      return 50;
    }
    if (c.currentX() != 278 || c.currentY() != 649) {
      c.walkTowards(278, 649);
      return 50;
    }
    c.log("Trading Gerrant..");
    c.openShop(new int[] {NpcId.GERRANT.getId()});
    return 3000;
  }
}
