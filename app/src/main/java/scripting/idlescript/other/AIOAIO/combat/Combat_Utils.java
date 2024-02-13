package scripting.idlescript.other.AIOAIO.combat;

import bot.Main;
import controller.Controller;
import java.util.LinkedHashMap;
import java.util.Map;
import models.entities.ItemId;
import models.entities.NpcId;
import orsc.ORSCharacter;
import scripting.idlescript.other.AIOAIO.AIOAIO;

public class Combat_Utils {
  // Melee weapons in Runescape Classic ordered from best -> worst with their
  // required attack levels
  static final Map<ItemId, Integer> meleeWeaponsWithLevels = new LinkedHashMap<>();

  static {
    meleeWeaponsWithLevels.put(ItemId.DRAGON_SWORD, 60);
    meleeWeaponsWithLevels.put(ItemId.DRAGON_AXE, 60);
    meleeWeaponsWithLevels.put(ItemId.RUNE_2_HANDED_SWORD, 40);
    meleeWeaponsWithLevels.put(ItemId.RUNE_LONG_SWORD, 40);
    meleeWeaponsWithLevels.put(ItemId.RUNE_SHORT_SWORD, 40);
    meleeWeaponsWithLevels.put(ItemId.RUNE_BATTLE_AXE, 40);
    meleeWeaponsWithLevels.put(ItemId.ADAMANTITE_2_HANDED_SWORD, 30);
    meleeWeaponsWithLevels.put(ItemId.ADAMANTITE_LONG_SWORD, 30);
    meleeWeaponsWithLevels.put(ItemId.ADAMANTITE_SHORT_SWORD, 30);
    meleeWeaponsWithLevels.put(ItemId.ADAMANTITE_BATTLE_AXE, 30);
    meleeWeaponsWithLevels.put(ItemId.MITHRIL_2_HANDED_SWORD, 20);
    meleeWeaponsWithLevels.put(ItemId.MITHRIL_LONG_SWORD, 20);
    meleeWeaponsWithLevels.put(ItemId.MITHRIL_SHORT_SWORD, 20);
    meleeWeaponsWithLevels.put(ItemId.MITHRIL_BATTLE_AXE, 20);
    meleeWeaponsWithLevels.put(ItemId.BLACK_2_HANDED_SWORD, 10);
    meleeWeaponsWithLevels.put(ItemId.BLACK_LONG_SWORD, 10);
    meleeWeaponsWithLevels.put(ItemId.BLACK_SHORT_SWORD, 10);
    meleeWeaponsWithLevels.put(ItemId.BLACK_BATTLE_AXE, 10);
    meleeWeaponsWithLevels.put(ItemId.STEEL_2_HANDED_SWORD, 5);
    meleeWeaponsWithLevels.put(ItemId.STEEL_LONG_SWORD, 5);
    meleeWeaponsWithLevels.put(ItemId.STEEL_SHORT_SWORD, 5);
    meleeWeaponsWithLevels.put(ItemId.STEEL_BATTLE_AXE, 5);
    meleeWeaponsWithLevels.put(ItemId.IRON_2_HANDED_SWORD, 1);
    meleeWeaponsWithLevels.put(ItemId.IRON_LONG_SWORD, 1);
    meleeWeaponsWithLevels.put(ItemId.IRON_SHORT_SWORD, 1);
    meleeWeaponsWithLevels.put(ItemId.IRON_BATTLE_AXE, 1);
    meleeWeaponsWithLevels.put(ItemId.BRONZE_2_HANDED_SWORD, 1);
    meleeWeaponsWithLevels.put(ItemId.BRONZE_LONG_SWORD, 1);
    meleeWeaponsWithLevels.put(ItemId.BRONZE_SHORT_SWORD, 1);
    meleeWeaponsWithLevels.put(ItemId.BRONZE_BATTLE_AXE, 1);
    meleeWeaponsWithLevels.put(ItemId.BRONZE_AXE, 1);
  }

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
    Controller c = Main.getController();
    if (c.getNearestNpcById(NpcId.BANKER.getId(), false) == null) {
      c.setStatus("Walking to Bank");
      c.walkTowards(c.getNearestBank()[0], c.getNearestBank()[1]);
      return 100;
    }
    c.setStatus("Opening bank");
    c.openBank();
    c.setStatus("Depositing Everything");
    for (int itemId : c.getInventoryItemIds()) {
      Main.getController().depositItem(itemId, Main.getController().getInventoryItemCount(itemId));
      Main.getController().sleep(100);
    }
    c.setStatus("Withdrawing best melee weapon");
    int playerAttackLevel = c.getCurrentStat(c.getStatId("Attack"));
    for (Map.Entry<ItemId, Integer> entry : meleeWeaponsWithLevels.entrySet()) {
      if (playerAttackLevel < entry.getValue() || c.getBankItemCount(entry.getKey().getId()) <= 0)
        continue;
      c.log("Found " + entry.getKey().name() + " as my best weapon Uwu");
      c.withdrawItem(entry.getKey().getId());
      c.closeBank();
      c.sleep(750);
      c.equipItemById(entry.getKey().getId());
      break;
    }
    c.closeBank();
    c.setStatus("Withdrawing food");
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
    AIOAIO.state.methodStartup = false;
    System.out.println("Combat Setup complete");
    return 680;
  }

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

  public static boolean needToEat() {
    return Main.getController().getCurrentStat(Main.getController().getStatId("Hits")) <= 6;
  }

  /**
   * Attacks an NPC. Sleeps until the NPC is in combat
   *
   * @param npcId
   */
  public static void attackNpc(NpcId npcId) {
    Main.getController().setStatus("@yel@Attacking " + npcId.name());
    ORSCharacter npc = Main.getController().getNearestNpcById(npcId.getId(), false);
    if (npc == null) return;
    Main.getController().attackNpc(npc.serverIndex);
    Main.getController().sleepUntil(() -> Main.getController().isNpcInCombat(npc.serverIndex));
  }

  public static void buryBones() {
    Main.getController().setStatus("@red@Burying bones");
    Main.getController().itemCommand(ItemId.BONES.getId());
  }

  public static void lootBones() {
    int[] lootCoord = Main.getController().getNearestItemById(ItemId.BONES.getId());
    Main.getController().setStatus("@red@Picking bones");
    Main.getController().pickupItem(lootCoord[0], lootCoord[1], ItemId.BONES.getId(), true, false);
    Main.getController()
        .sleepUntil(
            () -> Main.getController().getInventoryItemCount(ItemId.BONES.getId()) > 0, 5000);
  }

  public static void runAndEat() {
    Main.getController().setStatus("@yel@Running and eating");
    Main.getController()
        .walkToAsync(Main.getController().currentX(), Main.getController().currentY(), 5);
    Main.getController().sleep(680);
    eatFood();
  }

  public static void safelyAbortMethod() {
    if (Main.getController().getNearestNpcById(NpcId.BANKER.getId(), false) == null) {
      Main.getController().setStatus("Running to safety");
      Main.getController()
          .walkTowards(
              Main.getController().getNearestBank()[0], Main.getController().getNearestBank()[1]);
    } else {
      AIOAIO.state.endTime = System.currentTimeMillis();
      Main.getController().log("Aborted fighting method due to lack of food");
    }
  }
}
