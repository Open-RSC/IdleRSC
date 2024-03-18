package scripting.idlescript.other.AIOAIO.combat;

import bot.Main;
import controller.Controller;
import java.util.ArrayList;
import models.entities.ItemId;
import models.entities.NpcId;
import orsc.ORSCharacter;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;

public class Combat_Utils {
  private static ItemId swordToBuy = null; // If not null, we want to buy a sword
  private static ItemId helmetToBuy = null; // If not null, we want to buy a helmet

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
    if (helmetToBuy != null) {
      if (Get_Helmet_Utils.buyHelmet(helmetToBuy)) {
        helmetToBuy = null;
      }
      return 50;
    }

    if (!AIOAIO_Script_Utils.towardsDepositAll()) return 50;

    // We are now in bank, with no items

    int bestWeaponId = Get_Weapon_Utils.getBestWeapon().getId();
    if (!c.isItemInBank(bestWeaponId) && !c.isItemIdEquipped(bestWeaponId)) {
      swordToBuy = ItemId.getById(bestWeaponId);
      c.log("Adding 3 minutes to " + AIOAIO.state.currentTask.getName() + " to help buy a sword!");
      AIOAIO.state.endTime += 180_000;
      return 50;
    }
    ItemId bestHelmet = Get_Helmet_Utils.getBestHelmet();
    if (!c.isItemInBank(bestHelmet.getId())
        && c.getBankItemCount(ItemId.COINS.getId()) >= Get_Helmet_Utils.getCost(bestHelmet)) {
      helmetToBuy = bestHelmet;
      c.log("Adding 3 minutes to " + AIOAIO.state.currentTask.getName() + " to help buy a helmet!");
      AIOAIO.state.endTime += 180_000;
      return 50;
    }

    // We are still in bank, with no items..

    ArrayList<Integer> itemsToEquip = new ArrayList<Integer>();
    if (!c.isItemIdEquipped(bestWeaponId)) itemsToEquip.add(bestWeaponId);
    if (c.isItemInBank(bestHelmet.getId()) && !c.isItemIdEquipped(bestHelmet.getId()))
      itemsToEquip.add(bestWeaponId);

    AIOAIO.state.status = ("Withdrawing combat items");
    for (int itemId : itemsToEquip) {
      c.withdrawItem(itemId);
      c.sleep(750);
    }

    c.closeBank();

    AIOAIO.state.status = ("Equiping combat items");
    if (!c.isItemIdEquipped(bestWeaponId)) {
      c.equipItemById(bestWeaponId);
      c.sleep(750);
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
    Main.getController().log("I need to eat but have no food! Trying to safely abort task!");
    if (AIOAIO_Script_Utils.getDistanceToNearestBanker() > 5) {
      Main.getController().walkTowardsBank();
    } else {
      AIOAIO.state.endTime = System.currentTimeMillis();
      Main.getController().log("Aborted fighting task due to lack of food");
    }
  }
}
