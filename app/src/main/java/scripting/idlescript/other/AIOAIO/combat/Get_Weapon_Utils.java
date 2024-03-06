package scripting.idlescript.other.AIOAIO.combat;

import bot.Main;
import models.entities.ItemId;
import models.entities.NpcId;
import models.entities.QuestId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;

public class Get_Weapon_Utils {
  /**
   * Buys a 2h from Tavlery's shop
   *
   * @param sword
   * @return true if bought the 2h, false if working on it
   */
  static boolean buySword(ItemId sword) {
    AIOAIO.state.status = ("Buying " + sword.name());
    if (Main.getController().getInventoryItemCount(ItemId.COINS.getId()) < getCost(sword)) {
      if (!AIOAIO_Script_Utils.towardsGetFromBank(ItemId.COINS, getCost(sword), false)) {
        Main.getController().log("Too poor to buy the sword I want! Skipping task");
        AIOAIO.state.endTime = System.currentTimeMillis();
        return false;
      }
    }
    if (Main.getController().isInShop()) {
      Main.getController().log("Buying " + sword.name());
      Main.getController().shopBuy(sword.getId());
      Main.getController().closeShop();
      Main.getController().sleep(1200);
      if (Main.getController().getInventoryItemCount(sword.getId()) >= 1) {
        Main.getController().log("Got " + sword.name());
        return true;
      }
      Main.getController().log("Failed to get " + sword.name());
      return false;
    }
    if (Main.getController().currentX() != 379 || Main.getController().currentY() != 502) {
      Main.getController().walkTowards(379, 502);
      return false;
    }
    Main.getController().log("Trading Gaius..");
    Main.getController().openShop(new int[] {NpcId.GAIUS.getId()});
    return false;
  }

  private static int getCost(ItemId sword) {
    int id = sword.getId();
    if (id == ItemId.BRONZE_2_HANDED_SWORD.getId()) {
      return 80;
    } else if (id == ItemId.IRON_2_HANDED_SWORD.getId()) {
      return 280;
    } else if (id == ItemId.STEEL_2_HANDED_SWORD.getId()) {
      return 1000;
    } else if (id == ItemId.BLACK_2_HANDED_SWORD.getId()) {
      return 1920;
    } else if (id == ItemId.MITHRIL_2_HANDED_SWORD.getId()) {
      return 2600;
    } else if (id == ItemId.ADAMANTITE_2_HANDED_SWORD.getId()) {
      return 6400;
    }
    throw new IllegalArgumentException("Invalid sword: " + sword);
  }

  /**
   * Gets the best weapon to use based on the player's attack level and bank content May return an
   * item that's not in the bank, in which case the bot needs to buy it
   */
  static ItemId getBestWeapon() {
    if (!Main.getController().isInBank()) throw new IllegalStateException("Not in bank");
    int playerAttackLevel =
        Main.getController().getCurrentStat(Main.getController().getStatId("Attack"));

    // Dragon tier
    if (playerAttackLevel >= 60
        && Main.getController().isQuestComplete(QuestId.LEGENDS_QUEST.getId())
        && Main.getController().isItemInBank(ItemId.DRAGON_AXE.getId())) return ItemId.DRAGON_AXE;
    if (playerAttackLevel >= 60
        && Main.getController().isQuestComplete(QuestId.LOST_CITY.getId())
        && Main.getController().isItemInBank(ItemId.DRAGON_SWORD.getId()))
      return ItemId.DRAGON_SWORD;

    // Rune tier
    if (playerAttackLevel >= 40
        && Main.getController().isItemInBank(ItemId.RUNE_2_HANDED_SWORD.getId()))
      return ItemId.RUNE_2_HANDED_SWORD;
    if (playerAttackLevel >= 40
        && Main.getController().isItemInBank(ItemId.RUNE_LONG_SWORD.getId()))
      return ItemId.RUNE_LONG_SWORD;
    if (playerAttackLevel >= 40
        && Main.getController().isItemInBank(ItemId.RUNE_SHORT_SWORD.getId()))
      return ItemId.RUNE_SHORT_SWORD;
    if (playerAttackLevel >= 40
        && Main.getController().isItemInBank(ItemId.RUNE_BATTLE_AXE.getId()))
      return ItemId.RUNE_BATTLE_AXE;

    // Adamantite tier
    if (playerAttackLevel >= 30
        && Main.getController().isItemInBank(ItemId.ADAMANTITE_2_HANDED_SWORD.getId()))
      return ItemId.ADAMANTITE_2_HANDED_SWORD;
    if (playerAttackLevel >= 30
        && Main.getController().isItemInBank(ItemId.ADAMANTITE_LONG_SWORD.getId()))
      return ItemId.ADAMANTITE_LONG_SWORD;
    if (playerAttackLevel >= 30
        && Main.getController().isItemInBank(ItemId.ADAMANTITE_SHORT_SWORD.getId()))
      return ItemId.ADAMANTITE_SHORT_SWORD;
    if (playerAttackLevel >= 30
        && Main.getController().isItemInBank(ItemId.ADAMANTITE_BATTLE_AXE.getId()))
      return ItemId.ADAMANTITE_BATTLE_AXE;
    if (playerAttackLevel >= 30)
      return ItemId.ADAMANTITE_2_HANDED_SWORD; // We will buy from Taverley

    // Mithril tier
    if (playerAttackLevel >= 20
        && Main.getController().isItemInBank(ItemId.MITHRIL_2_HANDED_SWORD.getId()))
      return ItemId.MITHRIL_2_HANDED_SWORD;
    if (playerAttackLevel >= 20
        && Main.getController().isItemInBank(ItemId.MITHRIL_LONG_SWORD.getId()))
      return ItemId.MITHRIL_LONG_SWORD;
    if (playerAttackLevel >= 20
        && Main.getController().isItemInBank(ItemId.MITHRIL_SHORT_SWORD.getId()))
      return ItemId.MITHRIL_SHORT_SWORD;
    if (playerAttackLevel >= 20
        && Main.getController().isItemInBank(ItemId.MITHRIL_BATTLE_AXE.getId()))
      return ItemId.MITHRIL_BATTLE_AXE;
    if (playerAttackLevel >= 20) return ItemId.MITHRIL_2_HANDED_SWORD; // We will buy from Taverley

    // Black tier
    if (playerAttackLevel >= 10
        && Main.getController().isItemInBank(ItemId.BLACK_2_HANDED_SWORD.getId()))
      return ItemId.BLACK_2_HANDED_SWORD;
    if (playerAttackLevel >= 10
        && Main.getController().isItemInBank(ItemId.BLACK_LONG_SWORD.getId()))
      return ItemId.BLACK_LONG_SWORD;
    if (playerAttackLevel >= 10
        && Main.getController().isItemInBank(ItemId.BLACK_SHORT_SWORD.getId()))
      return ItemId.BLACK_SHORT_SWORD;
    if (playerAttackLevel >= 10
        && Main.getController().isItemInBank(ItemId.BLACK_BATTLE_AXE.getId()))
      return ItemId.BLACK_BATTLE_AXE;
    if (playerAttackLevel >= 10) return ItemId.BLACK_2_HANDED_SWORD; // We will buy from Taverley

    // Steel tier
    if (playerAttackLevel >= 5
        && Main.getController().isItemInBank(ItemId.STEEL_2_HANDED_SWORD.getId()))
      return ItemId.STEEL_2_HANDED_SWORD;
    if (playerAttackLevel >= 5
        && Main.getController().isItemInBank(ItemId.STEEL_LONG_SWORD.getId()))
      return ItemId.STEEL_LONG_SWORD;
    if (playerAttackLevel >= 5
        && Main.getController().isItemInBank(ItemId.STEEL_SHORT_SWORD.getId()))
      return ItemId.STEEL_SHORT_SWORD;
    if (playerAttackLevel >= 5
        && Main.getController().isItemInBank(ItemId.STEEL_BATTLE_AXE.getId()))
      return ItemId.STEEL_BATTLE_AXE;
    if (playerAttackLevel >= 5) return ItemId.STEEL_2_HANDED_SWORD; // We will buy from Taverley

    // Iron tier
    if (playerAttackLevel >= 1
        && Main.getController().isItemInBank(ItemId.IRON_2_HANDED_SWORD.getId()))
      return ItemId.IRON_2_HANDED_SWORD;
    if (playerAttackLevel >= 1 && Main.getController().isItemInBank(ItemId.IRON_LONG_SWORD.getId()))
      return ItemId.IRON_LONG_SWORD;
    if (playerAttackLevel >= 1
        && Main.getController().isItemInBank(ItemId.IRON_SHORT_SWORD.getId()))
      return ItemId.IRON_SHORT_SWORD;
    if (playerAttackLevel >= 1 && Main.getController().isItemInBank(ItemId.IRON_BATTLE_AXE.getId()))
      return ItemId.IRON_BATTLE_AXE;
    if (playerAttackLevel >= 1) return ItemId.IRON_2_HANDED_SWORD; // We will buy from Taverley

    // Bronze tier (default tier if none of the above conditions are met)
    return ItemId.BRONZE_2_HANDED_SWORD; // Default weapon if no other conditions are met
  }
}
