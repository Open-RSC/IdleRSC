package scripting.idlescript.other.AIOAIO.combat;

import bot.Main;
import models.entities.ItemId;
import models.entities.NpcId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;

public class Get_Helmet_Utils {
  /**
   * Buys a large helmet from Barbarian Village's shop
   *
   * @param helmet
   * @return true if bought the helmet, false if working on it
   */
  public static boolean buyHelmet(ItemId helmet) {
    AIOAIO.state.status = ("Buying " + helmet.name());
    if (Main.getController().getInventoryItemCount(ItemId.COINS.getId()) < getCost(helmet)) {
      if (!AIOAIO_Script_Utils.towardsGetFromBank(ItemId.COINS, getCost(helmet), false)) {
        Main.getController().log("Too poor to buy the helmet I want! Skipping task");
        AIOAIO.state.endTime = System.currentTimeMillis();
        return false;
      }
    }
    if (Main.getController().isInShop()) {
      Main.getController().log("Buying " + helmet.name());
      Main.getController().shopBuy(helmet.getId());
      Main.getController().closeShop();
      Main.getController().sleep(1200);
      if (Main.getController().getInventoryItemCount(helmet.getId()) >= 1) {
        Main.getController().log("Got " + helmet.name());
        Main.getController().equipItemById(helmet.getId());
        return true;
      }
      Main.getController().log("Failed to get " + helmet.name());
      return false;
    }
    if (Main.getController().currentX() != 239 || Main.getController().currentY() != 509) {
      Main.getController().walkTowards(239, 509);
      return false;
    }
    Main.getController().log("Trading Peska..");
    Main.getController().openShop(new int[] {NpcId.PEKSA.getId()});
    return false;
  }

  public static int getCost(ItemId helmet) {
    int id = helmet.getId();
    if (id == ItemId.LARGE_BRONZE_HELMET.getId()) {
      return 44;
    } else if (id == ItemId.LARGE_IRON_HELMET.getId()) {
      return 154;
    } else if (id == ItemId.LARGE_STEEL_HELMET.getId()) {
      return 550;
    } else if (id == ItemId.LARGE_MITHRIL_HELMET.getId()) {
      return 1430;
    } else if (id == ItemId.LARGE_ADAMANTITE_HELMET.getId()) {
      return 3520;
    } else {
      throw new IllegalArgumentException("Invalid helmet: " + helmet);
    }
  }

  /**
   * Gets the best helmet to use based on the player's defense level and bank content May return an
   * item that's not in the bank, in which case the bot needs to buy it
   *
   * @return The ID of the helmet deemed most appropriate for purchase or usage based on current
   *     conditions.
   * @throws IllegalStateException if the player is not currently accessing a bank, ensuring bank
   *     interaction is a prerequisite for helmet selection.
   */
  public static ItemId getBestHelmet() {
    if (!Main.getController().isInBank()) throw new IllegalStateException("Not in bank");
    int playerDefenseLevel =
        Main.getController().getCurrentStat(Main.getController().getStatId("Defense"));

    if (playerDefenseLevel >= 60
        && Main.getController().isItemInBank(ItemId.DRAGON_MEDIUM_HELMET.getId()))
      return ItemId.DRAGON_MEDIUM_HELMET;

    // Rune tier
    if (playerDefenseLevel >= 40
        && Main.getController().isItemInBank(ItemId.LARGE_RUNE_HELMET.getId()))
      return ItemId.LARGE_RUNE_HELMET;
    if (playerDefenseLevel >= 40
        && Main.getController().isItemInBank(ItemId.MEDIUM_RUNE_HELMET.getId()))
      return ItemId.MEDIUM_RUNE_HELMET;

    // Adamantite tier
    if (playerDefenseLevel >= 30)
      return ItemId.LARGE_ADAMANTITE_HELMET; // We will buy from Barbarian Village

    // Mithril tier
    if (playerDefenseLevel >= 20)
      return ItemId.LARGE_MITHRIL_HELMET; // We will buy from Barbarian Village

    // Steel tier
    if (playerDefenseLevel >= 15)
      return ItemId.LARGE_STEEL_HELMET; // We will buy from Barbarian Village

    // Iron tier
    if (playerDefenseLevel >= 10)
      return ItemId.LARGE_IRON_HELMET; // We will buy from Barbarian Village

    // Bronze tier
    return ItemId.LARGE_BRONZE_HELMET; // If needed, we will buy from Barbarian Village
  }
}
