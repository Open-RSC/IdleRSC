package scripting.idlescript.other.AIOAIO.combat;

import bot.Main;
import controller.Controller;
import models.entities.ItemId;
import models.entities.NpcId;
import orsc.ORSCharacter;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;

public class Combat_Utils {
  private static ItemId swordToBuy = null; // If not null, we're out buying a sword

  // Food to use for training
  public static final ItemId[] food = {
    ItemId.CABBAGE,
    ItemId.SHRIMP,
    ItemId.MACKEREL,
    ItemId.TROUT,
    ItemId.SALMON,
    ItemId.LOBSTER,
    ItemId.SWORDFISH,
    ItemId.SHARK
  };

  public static int getFightingGear() {
    AIOAIO.state.status = "Getting fighting gear";
    Controller c = Main.getController();
    if (swordToBuy != null) {
      if (Get_Weapon_Utils.buySword(swordToBuy)) {
        swordToBuy = null;
      }
      return 50;
    }

    if (!AIOAIO_Script_Utils.towardsDepositAll()) return 50;

    // We are now in bank, with no items

    int bestWeaponId = Get_Weapon_Utils.getBestWeapon().getId();
    if (!Main.getController().isItemInBank(bestWeaponId)
        && !Main.getController().isItemIdEquipped(bestWeaponId)) {
      swordToBuy = ItemId.getById(bestWeaponId);
      return 50;
    }

    if (!Main.getController().isItemIdEquipped(bestWeaponId)) {
      c.withdrawItem(bestWeaponId);
      c.closeBank();
      c.sleep(750);
      c.equipItemById(bestWeaponId);
    }

    AIOAIO.state.status = ("Withdrawing food");
    c.openBank();
    for (ItemId foodId : food) {
      if (c.getInventoryItemCount() >= 30) break;
      c.withdrawItem(foodId.getId(), c.getBankItemCount(foodId.getId()));
    }
    c.sleep(2000);
    if (c.getInventoryItemCount() >= 30) {
      // deposit an item so we have room to loot bones
      c.depositItem(c.getInventoryItemIds()[0], 1);
    }
    c.closeBank();
    AIOAIO.state.taskStartup = false;
    System.out.println("Combat Setup complete");
    return 680;
  }

  /** Eats once piece of food from food[] array (assuming you have at least one piece) */
  public static void eatFood() {
    for (ItemId foodId : food) {
      if (Main.getController().getInventoryItemCount(foodId.getId()) <= 0) continue;
      Main.getController().itemCommand(foodId.getId());
      break;
    }
  }

  public static boolean hasFood() {
    for (ItemId foodId : food) {
      if (Main.getController().getInventoryItemCount(foodId.getId()) > 0) return true;
    }
    return false;
  }

  /** @return true if we're below 60% health, false otherwise */
  public static boolean needToEat() {
    return Main.getController().getCurrentStat(Main.getController().getStatId("Hits"))
        <= 0.6 * Main.getController().getBaseStat(Main.getController().getStatId("Hits"));
  }

  /**
   * Attacks an NPC. Sleeps until the NPC is in combat
   *
   * @param npcId
   */
  public static void attackNpc(NpcId npcId) {
    AIOAIO.state.status = ("@yel@Bopping " + npcId.name());
    ORSCharacter npc = Main.getController().getNearestNpcById(npcId.getId(), false);
    if (npc == null) return;
    Main.getController().attackNpc(npc.serverIndex);
    Main.getController().sleepUntil(() -> Main.getController().isInCombat(), 5000);
  }

  public static void buryBones() {
    AIOAIO.state.status = ("@red@Burying bones");
    Main.getController().itemCommand(ItemId.BONES.getId());
  }

  public static void lootBones() {
    int[] lootCoord = Main.getController().getNearestItemById(ItemId.BONES.getId());
    AIOAIO.state.status = ("@red@Picking bones");
    Main.getController().pickupItem(lootCoord[0], lootCoord[1], ItemId.BONES.getId(), true, false);
    Main.getController()
        .sleepUntil(
            () -> Main.getController().getInventoryItemCount(ItemId.BONES.getId()) > 0, 5000);
  }

  public static void runAndEat() {
    AIOAIO.state.status = ("@yel@Running and eating");
    Main.getController()
        .walkToAsync(Main.getController().currentX(), Main.getController().currentY(), 5);
    Main.getController().sleep(680);
    eatFood();
  }

  public static void safelyAbortTask() {
    Main.getController().log("Trying to safely abort task!");
    if (AIOAIO_Script_Utils.getDistanceToNearestBanker() > 5) {
      Main.getController().walkTowardsBank();
    } else {
      AIOAIO.state.endTime = System.currentTimeMillis();
      Main.getController().log("Aborted fighting task due to lack of food");
    }
  }
}
