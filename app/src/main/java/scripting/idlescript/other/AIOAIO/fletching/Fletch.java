package scripting.idlescript.other.AIOAIO.fletching;

import bot.Main;
import controller.Controller;
import java.util.Arrays;
import models.entities.ItemId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;

public class Fletch {
  private static Controller c;
  private static boolean needsToGetKnife = false;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (needsToGetKnife) getKnife();
    else if (!meetsReqs()) {
      c.log("Missing required level to fletch " + getFletchedProductId().name());
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 50;
    } else if (c.isBatching()) return 680; // Wait to finish fishing
    else if (!hasFletchingItemsInInventory()) withdrawFletchingItems();
    else fletch();
    return 250;
  }

  private static void getKnife() {
    AIOAIO.state.status = ("Getting knife");
    if (c.currentX() == 130 && c.currentY() == 667) {
      c.pickupItem(ItemId.KNIFE.getId());
      c.sleep(1200);
      if (c.getInventoryItemCount(ItemId.KNIFE.getId()) > 0) {
        c.log("Got knife!");
        needsToGetKnife = false;
      }
      return;
    }
    c.walkTowards(130, 667);
  }

  private static void withdrawFletchingItems() {
    AIOAIO.state.status = ("Withdrawing fletching items");
    int[][] requiredItems = getFletchingItemRequirements();
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
    Arrays.stream(requiredItems)
        .forEach(
            item -> {
              if (c.getInventoryItemCount(item[0]) >= item[1]) return;
              if (!AIOAIO_Script_Utils.towardsGetFromBank(
                  ItemId.getById(item[0]), item[1], false)) {
                if (item[0] == ItemId.KNIFE.getId()) {
                  needsToGetKnife = true;
                  return;
                }
                c.log(
                    "Missing required items to fletch "
                        + getFletchedProductId().name()
                        + "; Need "
                        + item[1]
                        + " "
                        + ItemId.getById(item[0]).name());
                AIOAIO.state.endTime = System.currentTimeMillis();
                return;
              }
            });
  }

  private static boolean hasFletchingItemsInInventory() {
    for (int[] item : getFletchingItemRequirements()) {
      if (c.getInventoryItemCount(item[0]) < item[1]) {
        return false;
      }
    }
    return true;
  }

  /*
   * Returns a pair of itemId : amount
   */
  private static int[][] getFletchingItemRequirements() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Arrow shafts":
        return new int[][] {{ItemId.LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung shortbow":
        return new int[][] {{ItemId.LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Longbow":
        return new int[][] {{ItemId.LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Oak shortbow":
        return new int[][] {{ItemId.OAK_LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Oak Longbow":
        return new int[][] {{ItemId.OAK_LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Willow shortbow":
        return new int[][] {{ItemId.WILLOW_LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Willow Longbow":
        return new int[][] {{ItemId.WILLOW_LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Maple shortbow":
        return new int[][] {{ItemId.MAPLE_LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Maple Longbow":
        return new int[][] {{ItemId.MAPLE_LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Yew shortbow":
        return new int[][] {{ItemId.YEW_LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Yew Longbow":
        return new int[][] {{ItemId.YEW_LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Magic shortbow":
        return new int[][] {{ItemId.MAGIC_LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      case "Unstrung Magic Longbow":
        return new int[][] {{ItemId.MAGIC_LOGS.getId(), 29}, {ItemId.KNIFE.getId(), 1}};
      default:
        throw new IllegalStateException("Unknown fish type: " + AIOAIO.state.currentTask.getName());
    }
  }

  private static ItemId getFletchedProductId() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Arrow shafts":
        return ItemId.ARROW_SHAFTS;
      case "Unstrung shortbow":
        return ItemId.UNSTRUNG_SHORTBOW;
      case "Unstrung Longbow":
        return ItemId.UNSTRUNG_LONGBOW;
      case "Unstrung Oak shortbow":
        return ItemId.UNSTRUNG_OAK_SHORTBOW;
      case "Unstrung Oak Longbow":
        return ItemId.UNSTRUNG_OAK_LONGBOW;
      case "Unstrung Willow shortbow":
        return ItemId.UNSTRUNG_WILLOW_SHORTBOW;
      case "Unstrung Willow Longbow":
        return ItemId.UNSTRUNG_WILLOW_LONGBOW;
      case "Unstrung Maple shortbow":
        return ItemId.UNSTRUNG_MAPLE_SHORTBOW;
      case "Unstrung Maple Longbow":
        return ItemId.UNSTRUNG_MAPLE_LONGBOW;
      case "Unstrung Yew shortbow":
        return ItemId.UNSTRUNG_YEW_SHORTBOW;
      case "Unstrung Yew Longbow":
        return ItemId.UNSTRUNG_YEW_LONGBOW;
      case "Unstrung Magic shortbow":
        return ItemId.UNSTRUNG_MAGIC_SHORTBOW;
      case "Unstrung Magic Longbow":
        return ItemId.UNSTRUNG_MAGIC_LONGBOW;
      default:
        throw new IllegalStateException(
            "Unknown fletching item type: " + AIOAIO.state.currentTask.getName());
    }
  }

  private static boolean meetsReqs() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Arrow shafts":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 1;
      case "Unstrung shortbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 5;
      case "Unstrung Longbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 10;
      case "Unstrung Oak shortbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 20;
      case "Unstrung Oak Longbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 25;
      case "Unstrung Willow shortbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 35;
      case "Unstrung Willow Longbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 40;
      case "Unstrung Maple shortbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 50;
      case "Unstrung Maple Longbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 55;
      case "Unstrung Yew shortbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 65;
      case "Unstrung Yew Longbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 70;
      case "Unstrung Magic shortbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 80;
      case "Unstrung Magic Longbow":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Fletching"))
            >= 85;
      default:
        throw new IllegalStateException(
            "Unknown fletching task: " + AIOAIO.state.currentTask.getName());
    }
  }

  private static void fletch() {
    AIOAIO.state.status = ("Fletching " + AIOAIO.state.currentTask.getName());
    c.useItemOnItemBySlot(
        c.getInventoryItemSlotIndex(getFletchingItemRequirements()[0][0]),
        c.getInventoryItemSlotIndex(getFletchingItemRequirements()[1][0]));
    c.sleep(700);
    if (AIOAIO.state.currentTask.getName().contains("shortbow")) c.optionAnswer(1);
    else if (AIOAIO.state.currentTask.getName().contains("Longbow")) c.optionAnswer(2);
    else c.optionAnswer(0);
    c.sleep(600);
  }
}
