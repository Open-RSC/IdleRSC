package scripting.idlescript.other.AIOAIO.mining;

import bot.Main;
import controller.Controller;
import models.entities.ItemId;
import models.entities.NpcId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;

public class Mine {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (!Mining_Utils.meetsReqs()) {
      Main.getController()
          .log(
              "Aborted " + AIOAIO.state.currentTask.getName() + " task because we don't meet reqs");
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 0;
    }
    if (!Mining_Utils.hasPickInInventory()) return getPick();
    else if (c.getInventoryItemCount() >= 30)
      AIOAIO_Script_Utils.towardsDepositAll(Mining_Utils.getBestPick());
    else if (c.isBatching()) return 250; // Wait to finish mining
    else if (c.getNearestReachableObjectByIds(Mining_Utils.getRockIds(), true) != null)
      return mineRock();
    else Mining_Utils.findRocks();
    return 50;
  }

  private static int mineRock() {
    c.setStatus("Mining rock");
    int[] rockCoords = c.getNearestReachableObjectByIds(Mining_Utils.getRockIds(), true);
    c.atObject(rockCoords[0], rockCoords[1]);
    c.sleepUntilMoving(1200);
    c.sleepUntilNotMoving(15000);
    return 1200;
  }

  private static int getPick() {
    if (!AIOAIO.state.hasPickInBank) return buyPickFromDwarvenMines();
    return getPickFromBank();
  }

  private static int getPickFromBank() {
    if (c.getNearestNpcById(95, false) == null) {
      c.walkTowardsBank();
      return 100;
    }
    c.setStatus("Opening bank");
    c.openBank();
    if (!Mining_Utils.hasPickInBank()) {
      System.out.println("No pick in bank, gotta get one..");
      // Next loop will grab it from Dwarven Mines
      AIOAIO.state.hasPickInBank = false;
      c.closeBank();
      return 100;
    }
    c.setStatus("Withdrawing axe");
    Mining_Utils.withdrawPickaxeFromBank();
    c.closeBank();
    return 680;
  }

  private static int buyPickFromDwarvenMines() {
    if (c.getInventoryItemCount(ItemId.COINS.getId()) < Mining_Utils.getPickCost()) {
      if (!AIOAIO_Script_Utils.towardsGetFromBank(ItemId.COINS, Mining_Utils.getPickCost())) {
        Main.getController()
            .log(
                "Too poor to buy "
                    + ItemId.getById(Mining_Utils.getBestPick()).name()
                    + ".. Skipping task");
        AIOAIO.state.endTime = System.currentTimeMillis();
      }
      return 50;
    }
    if (c.isInBank()) c.closeBank();
    if (c.isInShop()) {
      c.setStatus("Buying " + ItemId.getById(Mining_Utils.getBestPick()).name());
      c.shopBuy(Mining_Utils.getBestPick());
      c.closeShop();
      c.sleep(1200);
      if (c.getInventoryItemCount(Mining_Utils.getBestPick()) >= 1) {
        c.log("Got " + ItemId.getById(Mining_Utils.getBestPick()).name());
      } else {
        c.log("Failed to get " + ItemId.getById(Mining_Utils.getBestPick()).name());
      }
      return 50;
    } else if (c.distanceTo(293, 3329) < 5) {
      c.setStatus("Opening Nurmof shop");
      c.openShop(new int[] {NpcId.NURMOF.getId()});
    } else {
      c.walkTowards(293, 3329);
    }
    return 0;
  }
}
