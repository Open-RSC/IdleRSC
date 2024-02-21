package scripting.idlescript.other.AIOAIO;

import bot.Main;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import models.entities.ItemId;

public class AIOAIO_Script_Utils {

  /**
   * Progressively takes steps to withdrawing an item from the bank (i.e going to the bank, banking,
   * etc) Returns true if performed a step correctly. Returns false if you don't have the item in
   * the bank
   *
   * @param item
   * @param amount -1 if withdraw all but 1
   * @return
   */
  public static boolean towardsGetFromBank(ItemId item, int amount) {
    if (Main.getController().isInBank()) {
      if (amount == -1) amount = Main.getController().getBankItemCount(item.getId());
      if (Main.getController().getBankItemCount(item.getId()) < amount) {
        Main.getController()
            .log(
                "Can't withdraw "
                    + amount
                    + " "
                    + item.name()
                    + " because we don't have that many!");
        return false;
      }
      Main.getController().setStatus("Withdrawing " + amount + " " + item.name() + " from bank");
      Main.getController().withdrawItem(item.getId(), amount);
      Main.getController().sleep(680);
      return true;
    }
    if (Main.getController().getNearestNpcByIds(Main.getController().bankerIds, false) == null) {
      Main.getController().walkTowardsBank();
      return true;
    }
    Main.getController().setStatus("Opening bank");
    Main.getController().openBank();
    Main.getController().sleep(680);
    if (Main.getController().getBankItemCount(item.getId()) < amount) {
      Main.getController()
          .log(
              "Not enough "
                  + item.name()
                  + " in bank! Need "
                  + amount
                  + ", have "
                  + Main.getController().getBankItemCount(item.getId()));
      return false;
    }
    Main.getController().withdrawItem(item.getId(), amount);
    Main.getController().sleep(680);
    return true;
  }

  /**
   * Progressively takes steps towards depositing everything except the passed in Ids
   *
   * @param exceptions
   */
  public static void towardsDepositAll(int... exceptions) {
    if (Main.getController().isInBank()) {
      Main.getController().setStatus("Depositing");

      Set<Integer> excludedIds = Arrays.stream(exceptions).boxed().collect(Collectors.toSet());
      Arrays.stream(Main.getController().getInventoryItemIds())
          .filter(itemId -> itemId != 0 && !excludedIds.contains(itemId))
          .forEach(
              itemId -> {
                Main.getController()
                    .depositItem(itemId, Main.getController().getInventoryItemCount(itemId));
                Main.getController().sleep(500);
              });
      return;
    }
    if (Main.getController().getNearestNpcByIds(Main.getController().bankerIds, false) == null) {
      Main.getController().walkTowardsBank();
      return;
    }
    Main.getController().setStatus("Opening bank");
    Main.getController().openBank();
    Main.getController().sleep(680);
  }
}
