package scripting.idlescript.other.AIOAIO.mining;

import bot.Main;
import models.entities.ItemId;
import models.entities.QuestId;
import models.entities.SceneryId;
import scripting.idlescript.other.AIOAIO.AIOAIO;

public class Mining_Utils {
  public static int getBestPick() {
    if (Main.getController().getCurrentStat(Main.getController().getStatId("Mining")) < 6) {
      return ItemId.IRON_PICKAXE.getId();
    } else if (Main.getController().getCurrentStat(Main.getController().getStatId("Mining")) < 21) {
      return ItemId.STEEL_PICKAXE.getId();
    } else if (Main.getController().getCurrentStat(Main.getController().getStatId("Mining")) < 31) {
      return ItemId.MITHRIL_PICKAXE.getId();
    } else if (Main.getController().getCurrentStat(Main.getController().getStatId("Mining")) < 41) {
      return ItemId.ADAMANTITE_PICKAXE.getId();
    }
    return ItemId.RUNE_PICKAXE.getId();
  }

  public static boolean hasPickInInventory() {
    // Check if the best pick we can use is in the inventory
    if (Main.getController().isItemInInventory(getBestPick())
        || Main.getController().isItemIdEquipped(getBestPick())) {
      return true;
    }
    return false;
  }

  public static boolean hasPickInBank() {
    if (!Main.getController().isInBank()) throw new IllegalStateException("Not in bank");
    // Check if the best pick we can use is in the bank
    if (Main.getController().isItemInBank(getBestPick())) {
      return true;
    }
    return false;
  }

  public static void withdrawPickaxeFromBank() {
    if (Main.getController().isItemInBank(getBestPick())) {
      Main.getController().withdrawItem(getBestPick());
      return;
    }
  }

  public static int getPickCost() {
    if (getBestPick() == ItemId.IRON_PICKAXE.getId()) return 140;
    else if (getBestPick() == ItemId.STEEL_PICKAXE.getId()) return 500;
    else if (getBestPick() == ItemId.MITHRIL_PICKAXE.getId()) return 1300;
    else if (getBestPick() == ItemId.ADAMANTITE_PICKAXE.getId()) return 3200;
    return 32000;
  }

  public static boolean meetsReqs() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Clay":
      case "Copper ore":
      case "Tin ore":
        return true;
      case "Iron ore":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Mining")) >= 15;
      case "Silver ore":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Mining")) >= 20;
      case "Coal ore":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Mining")) >= 30;
      case "Gold ore":
        Main.getController().log(AIOAIO.state.currentTask.getName() + " is not yet implemented!");
        return false;
        // return
        // Main.getController().getCurrentStat(Main.getController().getStatId("Mining"))
        // >=
        // 40;
      case "Gem":
        return Main.getController().isQuestComplete(QuestId.SHILO_VILLAGE.getId())
            && Main.getController().getCurrentStat(Main.getController().getStatId("Mining")) >= 40;
      case "Mithril ore":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Mining")) >= 55;
      case "Adamantite ore":
        Main.getController().log(AIOAIO.state.currentTask.getName() + " is not yet implemented!");
        return false;
        // return
        // Main.getController().getCurrentStat(Main.getController().getStatId("Mining"))
        // >=
        // 70;
      case "Runite ore":
        Main.getController().log(AIOAIO.state.currentTask.getName() + " is not yet implemented!");
        return false;
        // return
        // Main.getController().getCurrentStat(Main.getController().getStatId("Mining"))
        // >=
        // 85;
    }
    throw new IllegalStateException("Unknown rock type: " + AIOAIO.state.currentTask.getName());
  }

  public static int[] getRockIds() {
    switch (AIOAIO.state.currentTask.getName()) {
      case "Clay":
        return new int[] {
          SceneryId.ROCK_CLAY.getId(), SceneryId.ROCK_CLAY2.getId(), SceneryId.ROCKS_CLAY3.getId()
        };
      case "Copper ore":
        return new int[] {
          SceneryId.ROCK_COPPER.getId(),
          SceneryId.ROCK_COPPER2.getId(),
          SceneryId.ROCKS_COPPER3.getId()
        };
      case "Tin ore":
        return new int[] {
          SceneryId.ROCK_TIN.getId(), SceneryId.ROCK_TIN2.getId(), SceneryId.ROCKS_TIN3.getId()
        };
      case "Iron ore":
        return new int[] {
          SceneryId.ROCK_IRON.getId(), SceneryId.ROCK_IRON2.getId(), SceneryId.ROCKS_IRON3.getId()
        };
      case "Silver ore":
        return new int[] {
          SceneryId.ROCK_SILVER.getId(),
          SceneryId.ROCK_SILVER2.getId(),
          SceneryId.ROCKS_SILVER3.getId()
        };
      case "Coal ore":
        return new int[] {
          SceneryId.ROCK_COAL.getId(), SceneryId.ROCK_COAL2.getId(), SceneryId.ROCKS_COAL3.getId()
        };
      case "Gold ore":
        return new int[] {
          SceneryId.ROCK_GOLD.getId(), SceneryId.ROCK_GOLD2.getId(), SceneryId.ROCKS_GOLD3.getId()
        };
      case "Gem":
        return new int[] {SceneryId.GEM_ROCKS.getId()};
      case "Mithril ore":
        return new int[] {
          SceneryId.ROCK_MITHRIL.getId(),
          SceneryId.ROCK_MITHRIL2.getId(),
          SceneryId.ROCKS_MITHRIL3.getId()
        };
      case "Adamantite ore":
        return new int[] {
          SceneryId.ROCK_ADAMITE.getId(),
          SceneryId.ROCK_ADAMITE2.getId(),
          SceneryId.ROCKS_ADAMITE3.getId()
        };
      case "Runite ore":
        return new int[] {
          SceneryId.ROCK_RUNITE.getId(),
          SceneryId.ROCK_RUNITE2.getId(),
          SceneryId.ROCKS_RUNITE3.getId()
        };
    }
    throw new IllegalStateException("Unknown rock type: " + AIOAIO.state.currentTask.getName());
  }

  public static boolean findRocks() {
    // If lots of people use this script, add more locs and a Random
    switch (AIOAIO.state.currentTask.getName()) {
      case "Clay":
        return Main.getController().walkTowards(160, 543); // Varrock Southwest Mine
      case "Copper ore":
        return Main.getController().walkTowards(73, 546); // Varrock Southeast Mine
      case "Tin ore":
        return Main.getController().walkTowards(160, 543); // Varrock Southwest Mine
      case "Iron ore":
        return Main.getController().walkTowards(160, 543); // Varrock Southwest Mine
      case "Silver ore":
        return Main.getController().walkTowards(160, 543); // Varrock Southwest Mine
      case "Coal ore":
        // if mining level then ...
        return Main.getController().walkTowards(228, 515); // Barbarian Village
        // Gold is skipped, we want to do Tree Gnome Stronghold mine tho
      case "Gem":
        return Main.getController().walkTowards(424, 825); // Shilo
      case "Mithril ore":
        return Main.getController().walkTowards(116, 711); // Lumbridge Swamp Mine
        // Adamantite is skipped, we want to do Tree Gnome Stronghold mine tho
        // Runite is skipped, we wanna have logic for banking our stuff first
    }
    throw new IllegalStateException("Unknown rock location: " + AIOAIO.state.currentTask.getName());
  }
}
