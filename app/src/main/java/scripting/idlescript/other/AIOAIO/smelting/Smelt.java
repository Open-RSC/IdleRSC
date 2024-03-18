package scripting.idlescript.other.AIOAIO.smelting;

import bot.Main;
import controller.Controller;
import java.util.Arrays;
import models.entities.ItemId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;

public class Smelt {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (!meetsReqs()) {
      c.log("Missing required level to smelt " + AIOAIO.state.currentTask.getName());
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 50;
    } else if (c.isBatching()) return 680; // Wait to finish fishing
    else if (!hasSmeltingItemsInInventory()) withdrawSmeltingItems();
    else startSmelting();
    return 250;
  }

  private static void withdrawSmeltingItems() {
    AIOAIO.state.status = ("Withdrawing smelting items");
    if (!c.isInBank()) {
      AIOAIO_Script_Utils.towardsOpenBank();
    }
    int[][] requiredItems = getSmeltingItemRequirements(); // [itemId, amount]
    // Deposit any excessive items I have in inven
    c.getInventoryItems().stream()
        .forEach(
            item -> {
              int requiredAmount =
                  Arrays.stream(requiredItems)
                      .filter(r -> r[0] == item.getItemDef().id)
                      .findFirst()
                      .map(r -> r[1])
                      .orElse(0);
              int excessAmount = item.getAmount() - requiredAmount;
              if (excessAmount > 0) c.depositItem(item.getItemDef().id, excessAmount);
            });
    // Withdraw any needed items
    Arrays.stream(requiredItems)
        .forEach(
            item -> {
              if (c.getInventoryItemCount(item[0]) >= item[1]) return;
              if (!AIOAIO_Script_Utils.towardsGetFromBank(
                  ItemId.getById(item[0]), item[1], false)) {
                c.log(
                    "Missing required items to smelt "
                        + AIOAIO.state.currentTask.getName()
                        + "; Need "
                        + item[1]
                        + " "
                        + ItemId.getById(item[0]).name()
                        + ", only have "
                        + c.getBankItemCount(item[0]));
                AIOAIO.state.endTime = System.currentTimeMillis();
                return;
              }
            });
  }

  private static boolean hasSmeltingItemsInInventory() {
    for (int[] item : getSmeltingItemRequirements()) {
      if (c.getInventoryItemCount(item[0]) < item[1]) {
        return false;
      }
    }
    return true;
  }

  /*
   * Returns a pair of itemId : amount
   */
  private static int[][] getSmeltingItemRequirements() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Bronze bar":
        return new int[][] {{ItemId.COPPER_ORE.getId(), 15}, {ItemId.TIN_ORE.getId(), 15}};
      case "Iron bar":
        return new int[][] {{ItemId.IRON_ORE.getId(), 30}};
      case "Silver bar":
        return new int[][] {{ItemId.SILVER.getId(), 30}};
      case "Steel bar":
        return new int[][] {{ItemId.COAL.getId(), 20}, {ItemId.IRON_ORE.getId(), 10}};
      case "Gold bar":
        return new int[][] {{ItemId.GOLD.getId(), 30}};
      case "Mithril bar":
        return new int[][] {{ItemId.COAL.getId(), 24}, {ItemId.MITHRIL_ORE.getId(), 6}};
      case "Adamantite bar":
        return new int[][] {{ItemId.COAL.getId(), 26}, {ItemId.ADAMANTITE_ORE.getId(), 4}};
      case "Runite bar":
        return new int[][] {{ItemId.COAL.getId(), 27}, {ItemId.RUNITE_ORE.getId(), 3}};
      default:
        throw new IllegalStateException("Unknown bar type: " + AIOAIO.state.currentTask.getName());
    }
  }

  private static boolean meetsReqs() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Bronze bar":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Smithing")) >= 1;
      case "Iron bar":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Smithing"))
            >= 15;
      case "Silver bar":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Smithing"))
            >= 20;
      case "Steel bar":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Smithing"))
            >= 30;
      case "Gold bar":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Smithing"))
            >= 40;
      case "Mithril bar":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Smithing"))
            >= 50;
      case "Adamantite bar":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Smithing"))
            >= 70;
      case "Runite bar":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Smithing"))
            >= 85;
      default:
        throw new IllegalStateException(
            "Unknown smithing task: " + AIOAIO.state.currentTask.getName());
    }
  }

  private static void startSmelting() {
    AIOAIO.state.status = ("Starting to smelt " + AIOAIO.state.currentTask.getName());
    if (c.getNearestObjectById(118) == null) {
      c.walkTowards(311, 545);
      return;
    }
    int oreId = -1;
    switch (AIOAIO.state.currentTask.getName()) {
      case "Bronze bar":
        oreId = ItemId.COPPER_ORE.getId();
        break;
      case "Iron bar":
        oreId = ItemId.IRON_ORE.getId();
        break;
      case "Silver bar":
        oreId = ItemId.SILVER.getId();
        break;
      default:
        oreId = ItemId.COAL.getId();
    }
    c.useItemIdOnObject(c.getNearestObjectById(118)[0], c.getNearestObjectById(118)[1], oreId);
    c.sleepUntilGainedXp();
    AIOAIO.state.status = ("Smelting " + AIOAIO.state.currentTask.getName() + "...");
  }
}
