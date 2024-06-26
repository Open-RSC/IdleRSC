package scripting.idlescript.other.AIOAIO;

import bot.Main;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import models.entities.ItemId;
import orsc.ORSCharacter;

public class AIOAIO_Script_Utils {

  /**
   * Progressively takes steps to withdrawing an item from the bank (i.e going to the bank, banking,
   * etc) Returns true if performed a step correctly. Returns false if you don't have the item in
   * the bank
   *
   * @param item
   * @param amount -1 if withdraw all
   * @return
   */
  public static boolean towardsGetFromBank(ItemId item, int amount, boolean depositEverythingElse) {
    if (Main.getController().isInBank()) {
      AIOAIO.state.status = "Withdrawing " + item.name();
      if (depositEverythingElse) {
        Main.getController().depositAll();
        Main.getController().sleepUntil(() -> Main.getController().getInventoryItemCount() == 0);
      }
      if (amount == -1) amount = Main.getController().getBankItemCount(item.getId());
      if (Main.getController().getBankItemCount(item.getId()) < amount || amount == 0) {
        Main.getController()
            .log(
                "Can't withdraw "
                    + amount
                    + " "
                    + item.name()
                    + " because we don't have that many!");
        return false;
      }
      AIOAIO.state.status = "Withdrawing " + amount + " " + item.name() + " from bank";
      Main.getController().withdrawItem(item.getId(), amount);
      Main.getController().sleep(680);
      return true;
    }
    if (getDistanceToNearestBanker() > 5) {
      AIOAIO.state.status = "Walking towards bank to get " + item.name();
      Main.getController().walkTowardsBank();
      return true;
    }
    AIOAIO.state.status = "Opening bank to get " + item.name();
    Main.getController().openBank();
    return true;
  }

  /**
   * Progressively takes steps towards depositing everything except the passed in Ids Returns true
   * if it's done (bank will be open)
   *
   * @param exceptions Item Ids to not deposit
   * @return true if the deposit is completed (+ bank is open), false if we only progressed towards
   *     making that happen
   */
  public static boolean towardsDepositAll(int... exceptions) {
    if (Main.getController().isInBank()) {
      AIOAIO.state.status = ("Depositing");

      Set<Integer> excludedIds = Arrays.stream(exceptions).boxed().collect(Collectors.toSet());
      Arrays.stream(Main.getController().getInventoryItemIds())
          .filter(itemId -> itemId != 0 && !excludedIds.contains(itemId))
          .forEach(
              itemId -> {
                Main.getController()
                    .depositItem(itemId, Main.getController().getInventoryItemCount(itemId));
                Main.getController().sleep(50);
              });
      Main.getController()
          .sleepUntil(
              () ->
                  Arrays.stream(Main.getController().getInventoryItemIds())
                      .allMatch(itemId -> itemId == 0 || excludedIds.contains(itemId)),
              3000);
      return true;
    }
    towardsOpenBank();
    return false;
  }

  /**
   * Progressively takes steps towards opening a bank
   *
   * @return true if bank is open, false if we're working on it
   */
  public static boolean towardsOpenBank() {
    if (getDistanceToNearestBanker() > 5) {
      AIOAIO.state.status = "Going towards bank to open it";
      Main.getController().walkTowardsBank();
      return false;
    }
    AIOAIO.state.status = "Opening bank";
    Main.getController().openBank();
    Main.getController().sleep(680);
    return Main.getController().isInBank();
  }

  /**
   * Gets the distance to the nearest banker NPC Useful because sometimes the banker NPC isn't null,
   * but it's also not nearby
   *
   * @return distance to nearest banker NPC, or Integer.MAX_VALUE if no banker NPCs are found
   */
  public static int getDistanceToNearestBanker() {
    ORSCharacter banker =
        Main.getController().getNearestNpcByIds(Main.getController().bankerIds, false);
    if (banker == null) return Integer.MAX_VALUE;
    int dist =
        Main.getController()
            .distanceTo(
                Main.getController().convertX(banker.currentX),
                Main.getController().convertZ(banker.currentZ));
    // Main.getController().log("Dist to bank: " + dist);
    return dist;
  }

  static long lastBankCheckTime = 0;

  public static void checkAccountValue() {
    // Kinda sorta not really expensive, so only do it every 10s
    if (System.currentTimeMillis() - lastBankCheckTime < 10000) return;
    AIOAIO.state.lastCheckedBankValue = Main.getController().getAccountValue();
    if (AIOAIO.state.initBankValue == -1)
      AIOAIO.state.initBankValue = AIOAIO.state.lastCheckedBankValue;
  }
}
