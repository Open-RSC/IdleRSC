package scripting.idlescript;

import bot.Main;
import controller.Controller;
import javax.swing.*;
import models.entities.EquipSlotIndex;
import models.entities.ItemId;
import models.entities.NpcId;
import models.entities.SpellId;
import orsc.ORSCharacter;

/**
 * WIP master file for common commands used in Kaila_Scripts
 *
 * <p>int trout = ItemId.RAW_TROUT.getId();
 *
 * @author Kaila
 */
/*
 *       todo
 *           int trout = ItemId.RAW_TROUT.getId();
 *           replace eat food to loot with clearInventorySlot
 *          make master list of all methods and variables,
 *              - full list near top,sub list each section!
 *          Javadoc for each method
 *          change prayer potions to prayer potion int[] method
 *          waitForBankOpen(); //temporary fix for npc desync issues,
 *              redo into better bank wait, using less sleep
 *
 * todo add int param to select how far above base to use boost potion
 *
 */
public class K_kailaScript extends IdleScript {
  protected static final Controller c = Main.getController();
  protected static JFrame scriptFrame = null;
  protected static String foodName = "";
  // ~~~~~~~~~~~Boolean~~~~~~~~~~~~
  protected static boolean guiSetup = false;
  protected static boolean scriptStarted = false;
  protected static boolean timeToBank = false;
  protected static boolean timeToBankStay = false;
  protected static boolean lootLowLevel = false;
  protected static boolean lootBones = false;
  protected static boolean buryBones = false;
  protected static boolean lootLimp = false;
  protected static boolean potUp = false;
  // ~~~~~~~~~~~~OTHER CONSTANTS~~~~~~~~~~~~~
  protected static final long twoHundredFiftySecondsInMillis = 255000L;
  protected static final long nineMinutesInMillis = 540000L;
  protected static final long startTimestamp = System.currentTimeMillis() / 1000L;
  protected static final int GAME_TICK = 640;

  // ~~~~~~~~~~~random long/int~~~~~~~~~~~~~~~~

  protected static long startTime;
  protected static int foodInBank = -1;
  protected static int usedFood = 0;
  protected static int foodWithdrawAmount = -1;
  protected static int prayPotWithdrawAmount = -1;
  protected static int foodId = -1;
  protected static int totalTrips = 0;
  protected static int centerX = -1;
  protected static int centerY = -1;
  protected static int centerDistance = -1;
  protected static int fightMode = 0;
  // Inventory Item Counts
  // Herbs
  protected static int inventGuam = 0;
  protected static int inventMar = 0;
  protected static int inventTar = 0;
  protected static int inventHar = 0;
  protected static int inventRan = 0;
  protected static int inventIrit = 0;
  protected static int inventAva = 0;
  protected static int inventKwuarm = 0;
  protected static int inventCada = 0;
  protected static int inventDwarf = 0;
  protected static int inventHerbs = 0;
  // Runes
  protected static int inventMind = 0;
  protected static int inventChaos = 0;
  protected static int inventDeath = 0;
  protected static int inventBlood = 0;
  protected static int inventLaws = 0;
  protected static int inventNats = 0;
  protected static int inventCosmic = 0;
  protected static int inventAir = 0;
  protected static int inventFire = 0;
  protected static int inventWater = 0;
  protected static int inventEarth = 0;
  protected static int inventRunes = 0;
  // inventory Gems
  protected static int inventSapphire = 0;
  protected static int inventEmerald = 0;
  protected static int inventRuby = 0;
  protected static int inventDiamond = 0;
  protected static int inventGems = 0;
  // inventory Rares
  protected static int inventTooth = 0;
  protected static int inventLoop = 0;
  protected static int inventSpear = 0;
  protected static int inventLeft = 0;

  // UNID HERBS
  protected static int totalGuam = 0;
  protected static int totalMar = 0;
  protected static int totalTar = 0;
  protected static int totalHerbs = 0;
  protected static int totalHar = 0;
  protected static int totalRan = 0;
  protected static int totalIrit = 0;
  protected static int totalAva = 0;
  protected static int totalKwuarm = 0;
  protected static int totalCada = 0;
  protected static int totalDwarf = 0;

  // OTHER
  protected static int usedBones = 0; // the ones you have buried in the ground
  protected static int bankBones = 0; // the # you have IN the bank
  protected static int totalBones = 0; // the ## you have deposited in the bank
  protected static int totalPlates = 0;
  protected static int totalBars = 0;

  // RUNES
  protected static final int airId = 33;
  protected static final int waterId = 32;
  protected static final int earthId = 34;
  protected static final int lawId = 42;
  protected static int totalAir = 0;
  protected static int totalFire = 0;
  protected static int totalWater = 0;
  protected static int totalEarth = 0;
  protected static int totalChaos = 0;
  protected static int totalCosmic = 0;
  protected static int totalLaw = 0;
  protected static int totalNat = 0;
  protected static int totalBlood = 0;
  protected static int totalDeath = 0;

  // ORE
  protected static int coalInBank = 0;
  protected static int mithInBank = 0;
  protected static int addyInBank = 0;
  protected static int totalCoal = 0;
  protected static int totalMith = 0;
  protected static int totalAddy = 0; // defined in different ways, usually ore/bar depo script
  protected static int barsInBank = 0;
  protected static int logInBank = 0;
  // RDT
  protected static int totalSap = 0;
  protected static int totalEme = 0;
  protected static int totalRub = 0;
  protected static int totalDia = 0;
  protected static int totalLoop = 0;
  protected static int totalTooth = 0;
  protected static int totalLeft = 0;
  protected static int totalSpear = 0;

  // "totals"
  protected static int totalHerb = 0;
  protected static int totalGems = 0;
  protected static int totalLog = 0;
  protected static int totalRunes = 0;
  /*
   *
   *      int[]  Arrays
   */
  /**
   * int[] array of <b>bone id</b>'s <br>
   * [regular,big,bat,dragon]
   */
  protected static final int[] bones = {
    ItemId.BONES.getId(),
    ItemId.BIG_BONES.getId(),
    ItemId.BAT_BONES.getId(),
    ItemId.DRAGON_BONES.getId()
  };
  /**
   * int[] array of <b>regular attack potions</b><br>
   * [1,2,3] doses
   */
  protected static final int[] attackPot = {
    ItemId.ATTACK_POTION_1DOSE.getId(),
    ItemId.ATTACK_POTION_2DOSE.getId(),
    ItemId.ATTACK_POTION_3DOSE.getId()
  };
  /**
   * int[] array of <b>regular strength potions</b><br>
   * [1,2,3] doses
   */
  protected static final int[] strengthPot = {
    ItemId.STRENGTH_POTION_1DOSE.getId(),
    ItemId.STRENGTH_POTION_2DOSE.getId(),
    ItemId.STRENGTH_POTION_3DOSE.getId(),
    ItemId.STRENGTH_POTION_4DOSE.getId()
  };
  /**
   * int[] array of <b>regular defense potions</b><br>
   * [1,2,3] doses
   */
  protected static final int[] defensePot = {
    ItemId.DEFENSE_POTION_1DOSE.getId(),
    ItemId.DEFENSE_POTION_2DOSE.getId(),
    ItemId.DEFENSE_POTION_3DOSE.getId()
  };
  /**
   * int[] array of <b>super attack potion</b><br>
   * [1,2,3] doses
   */
  protected static final int[] superAttackPot = {
    ItemId.SUPER_ATTACK_POTION_1DOSE.getId(),
    ItemId.SUPER_ATTACK_POTION_2DOSE.getId(),
    ItemId.SUPER_ATTACK_POTION_3DOSE.getId()
  };
  /**
   * int[] array of <b>super strength potion</b><br>
   * [1,2,3] doses
   */
  protected static final int[] superStrengthPot = {
    ItemId.SUPER_STRENGTH_POTION_1DOSE.getId(),
    ItemId.SUPER_STRENGTH_POTION_2DOSE.getId(),
    ItemId.SUPER_STRENGTH_POTION_3DOSE.getId()
  };
  /**
   * int[] array of <b>super defense potion</b><br>
   * [1,2,3] doses
   */
  protected static final int[] superDefensePot = {
    ItemId.SUPER_DEFENSE_POTION_1DOSE.getId(),
    ItemId.SUPER_DEFENSE_POTION_2DOSE.getId(),
    ItemId.SUPER_DEFENSE_POTION_3DOSE.getId()
  };
  /**
   * int[] array of <b>prayer potion</b><br>
   * [1,2,3] doses
   */
  protected static final int[] prayerPot = {
    ItemId.RESTORE_PRAYER_POTION_1DOSE.getId(),
    ItemId.RESTORE_PRAYER_POTION_2DOSE.getId(),
    ItemId.RESTORE_PRAYER_POTION_3DOSE.getId()
  };
  /**
   * int[] array of <b>antidote</b><br>
   * (the red one) [1,2,3] doses
   */
  protected static final int[] antiPot = {
    ItemId.POISON_ANTIDOTE_1DOSE.getId(),
    ItemId.POISON_ANTIDOTE_2DOSE.getId(),
    ItemId.POISON_ANTIDOTE_3DOSE.getId()
  };
  /**
   * int[] array of cooked <b>food Ids</b><br>
   * [Manta,turtle,shark,swordfish,tuna,lobster,bass,mackerel,<br>
   * cod,pike,herring,salmon,trout,anchovies,shrimp,meat]
   */
  protected static final int[] foodIds = {
    ItemId.MANTA_RAY.getId(),
    ItemId.SEA_TURTLE.getId(),
    ItemId.SHARK.getId(),
    ItemId.SWORDFISH.getId(),
    ItemId.TUNA.getId(),
    ItemId.LOBSTER.getId(),
    ItemId.BASS.getId(),
    ItemId.MACKEREL.getId(),
    ItemId.COD.getId(),
    ItemId.PIKE.getId(),
    ItemId.HERRING.getId(),
    ItemId.SALMON.getId(),
    ItemId.TROUT.getId(),
    ItemId.ANCHOVIES.getId(),
    ItemId.SHRIMP.getId(),
    ItemId.COOKEDMEAT.getId()
  };
  /**
   * String[] array of <b>cooked food NAMES</b><br>
   * [Manta,turtle,shark,swordfish,tuna,lobster,bass,mackerel,<br>
   * cod,pike,herring,salmon,trout,anchovies,shrimp,meat]<br>
   * Warning: foodIds and foodTypes need the same order for scripts to use
   */
  // todo: convert to hashmap
  protected static final String[] foodTypes = {
    "Manta Ray",
    "Sea Turtle",
    "Shark",
    "Swordfish",
    "Tuna",
    "Lobster",
    "Bass",
    "Mackerel",
    "Cod",
    "Pike",
    "Herring",
    "Salmon",
    "Trout",
    "Anchovies",
    "Shrimp",
    "Cooked Meat"
  };
  /**
   * Checks the amount of items in the inventory and stores the data in the inventItem static
   * variables. For use with GUI to display the total amount of items gathered between the bank and
   * inventory.
   */
  protected static void checkInventoryItemCounts() {
    // Herbs
    inventGuam = c.getInventoryItemCount(ItemId.GUAM_LEAF.getId());
    inventMar = c.getInventoryItemCount(ItemId.UNID_MARRENTILL.getId());
    inventTar = c.getInventoryItemCount(ItemId.UNID_TARROMIN.getId());
    inventHar = c.getInventoryItemCount(ItemId.UNID_HARRALANDER.getId());
    inventRan = c.getInventoryItemCount(ItemId.UNID_RANARR_WEED.getId());
    inventIrit = c.getInventoryItemCount(ItemId.UNID_IRIT.getId());
    inventAva = c.getInventoryItemCount(ItemId.UNID_AVANTOE.getId());
    inventKwuarm = c.getInventoryItemCount(ItemId.UNID_KWUARM.getId());
    inventCada = c.getInventoryItemCount(ItemId.UNID_CADANTINE.getId());
    inventDwarf = c.getInventoryItemCount(ItemId.UNID_DWARF_WEED.getId());
    // Runes
    inventLaws = c.getInventoryItemCount(ItemId.LAW_RUNE.getId());
    inventNats = c.getInventoryItemCount(ItemId.NATURE_RUNE.getId());
    inventMind = c.getInventoryItemCount(ItemId.MIND_RUNE.getId());
    inventChaos = c.getInventoryItemCount(ItemId.CHAOS_RUNE.getId());
    inventDeath = c.getInventoryItemCount(ItemId.DEATH_RUNE.getId());
    inventBlood = c.getInventoryItemCount(ItemId.BLOOD_RUNE.getId());
    inventCosmic = c.getInventoryItemCount(ItemId.COSMIC_RUNE.getId());
    inventAir = c.getInventoryItemCount(ItemId.AIR_RUNE.getId());
    inventFire = c.getInventoryItemCount(ItemId.FIRE_RUNE.getId());
    inventWater = c.getInventoryItemCount(ItemId.WATER_RUNE.getId());
    inventEarth = c.getInventoryItemCount(ItemId.EARTH_RUNE.getId());
    // Gems
    inventSapphire = c.getInventoryItemCount(ItemId.UNCUT_SAPPHIRE.getId());
    inventEmerald = c.getInventoryItemCount(ItemId.UNCUT_EMERALD.getId());
    inventRuby = c.getInventoryItemCount(ItemId.UNCUT_RUBY.getId());
    inventDiamond = c.getInventoryItemCount(ItemId.UNCUT_DIAMOND.getId());
    inventGems = inventSapphire + inventEmerald + inventRuby + inventDiamond;
    // rares
    inventTooth = c.getInventoryItemCount(ItemId.TOOTH_HALF_KEY.getId());
    inventLoop = c.getInventoryItemCount(ItemId.LOOP_HALF_KEY.getId());
    inventLeft = c.getInventoryItemCount(ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.getId());
    inventSpear = c.getInventoryItemCount(ItemId.RUNE_SPEAR.getId());

    // Totals
    inventRunes =
        inventMind
            + inventChaos
            + inventDeath
            + inventBlood
            + inventLaws
            + inventNats
            + inventCosmic
            + inventAir
            + inventFire
            + inventWater
            + inventEarth;
    inventHerbs =
        inventGuam
            + inventMar
            + inventTar
            + inventHar
            + inventRan
            + inventIrit
            + inventAva
            + inventKwuarm
            + inventCada
            + inventDwarf;
  }
  /* protected static int whatIsMaxBoostLevel(String statName) { //super pots boost effect
    int potionEffect = 6;
    if (statName != null && !statName.equals("")) {
        if (c.getBaseStat(c.getStatId("")) < 14) {
            return potionEffect;
        } else if (c.getBaseStat(c.getStatId("")) < 20) {
            potionEffect = 7;
        } else if (c.getBaseStat(c.getStatId("")) < 27) {
            potionEffect = 8;
        } else if (c.getBaseStat(c.getStatId("")) < 34) {
            potionEffect = 9;
        } else if (c.getBaseStat(c.getStatId("")) < 40) {
            potionEffect = 10;
        } else if (c.getBaseStat(c.getStatId("")) < 47) {
            potionEffect = 11;
        } else if (c.getBaseStat(c.getStatId("")) < 54) {
            potionEffect = 12;
        } else if (c.getBaseStat(c.getStatId("")) < 60) {
            potionEffect = 13;
        } else if (c.getBaseStat(c.getStatId("")) < 67) {
            potionEffect = 14;
        } else if (c.getBaseStat(c.getStatId("")) < 74) {
            potionEffect = 15;
        } else if (c.getBaseStat(c.getStatId("")) < 80) {
            potionEffect = 16;
        } else if (c.getBaseStat(c.getStatId("")) < 87) {
            potionEffect = 17;
        } else if (c.getBaseStat(c.getStatId("")) < 94) {
            potionEffect = 18;
        } else if (c.getBaseStat(c.getStatId("")) > 93) {
            potionEffect = 19;
        }
    }
    return potionEffect;
  }
  */
  /*
   *
   *
   *
   *
   *
   *
   *          Main (useful) methods
   */

  protected static void combatLoop(int npcId, int[] lootId) {
    lootItems(false, lootId);
    if (lootBones) lootItem(false, ItemId.BONES.getId());
    buryBones(false);
    checkFightMode(fightMode);
    checkInventoryItemCounts();
    if (!c.isInCombat()) {
      ORSCharacter npc = c.getNearestNpcById(npcId, false);
      if (npc != null) {
        c.setStatus("@yel@Attacking " + NpcId.getById(npcId).name());
        c.attackNpc(npc.serverIndex);
        c.sleep(3 * GAME_TICK);
      } else c.sleep(GAME_TICK);
    } else c.sleep(640);
    if (c.getInventoryItemCount() == 30) {
      dropItemToLoot(false, 1, ItemId.EMPTY_VIAL.getId());
      buryBonesToLoot(false);
    }
  }
  /**
   * Checks distance from provided x, y coords to the predefined center position and radius with int
   * values centerX, centerY, and centerDistance (set values in main method) If not predefined, then
   * method will return true as a default fallback
   *
   * @param x coordinate of point you are checking
   * @param y coordinate of point you are checking
   * @return true if point is within radius of center, false if outside radius distance from center.
   */
  protected static boolean isWithinLootzone(int x, int y) {
    if (centerX == -1 || centerY == -1 || centerDistance == -1) {
      // c.log("ERROR: please set values for centerX, centerY, and centerDistance.");
      return true;
    }
    return c.distance(centerX, centerY, x, y) <= centerDistance; // center of lootzone
  }

  /**
   * eatFood method will eat any food ids in the inventory with a for loop. Always exiting combat.
   *
   * @return ate - true if no need to eat, or if successful. False if out of food items.
   */
  protected static boolean eatFood() {
    boolean ate = false;
    if (c.getCurrentStat(c.getStatId("Hits")) < (c.getBaseStat(c.getStatId("Hits")) - 20)) {
      for (int id : c.getFoodIds()) {
        if (c.getInventoryItemCount(id) > 0) {
          leaveCombat();
          c.setStatus("@red@Eating..");
          c.itemCommand(id);
          c.sleep(GAME_TICK);
          ate = true;
          break;
        }
      }
    } else return true; // not necessary to eat
    return ate; // return false if not eaten, return true if has eaten.
  }
  /**
   * eatFood method will eat any food ids in the inventory with a for loop. Always exiting combat.
   * Use this method to wear an item before eating (like anti-dragon shield)
   *
   * @param wearId int item Id to equip
   * @param swapState boolean whether to use swap or not (pass a boolean from gui)
   * @return ate - true if no need to eat, or if successful. False if out of food items.
   */
  protected static boolean eatFood(int wearId, boolean swapState) {
    boolean ate = false;
    if (c.getCurrentStat(c.getStatId("Hits")) < (c.getBaseStat(c.getStatId("Hits")) - 20)) {
      for (int id : c.getFoodIds()) {
        if (c.getInventoryItemCount(id) > 0) {
          leaveCombat();
          if (swapState && !c.isItemIdEquipped(wearId)) {
            c.equipItem(c.getInventoryItemSlotIndex(wearId));
            c.sleep(GAME_TICK);
          }
          c.setStatus("@red@Eating..");
          c.itemCommand(id);
          c.sleep(GAME_TICK);
          ate = true;
          break;
        }
      }
    } else return true; // not necessary to eat
    return ate; // return false if not eaten, return true if has eaten.
  }
  /**
   * Checks for supplied itemId within lootzone (using isWithinLootzone method) and loots 1 of item.
   *
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   * @param itemId int of itemId to loot. For multiple items use lootItems(boolean, int[]);
   */
  protected static void lootItem(boolean leaveCombat, int itemId) {
    int[] coords = c.getNearestItemById(itemId);
    if (coords != null && isWithinLootzone(coords[0], coords[1])) {
      if (!leaveCombat && c.isInCombat()) return; // blocked by combat
      else if (leaveCombat && c.isInCombat()) leaveCombat();
      c.setStatus("@yel@Picking Loot...");
      c.pickupItem(coords[0], coords[1], itemId, true, false); // always leaves combat to loot
      c.sleep(GAME_TICK);
      return;
    }
  }
  /**
   * Checks for supplied itemId within lootzone (using isWithinLootzone method) and loots list of
   * items
   *
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   * @param itemIds int[] array listing itemIds to loot using for loop. Can institize item arrays in
   *     the method parameters with "new int[]{data}" as the value for this param.
   */
  protected static void lootItems(boolean leaveCombat, int[] itemIds) {
    for (int itemId : itemIds) {
      int[] coords = c.getNearestItemById(itemId);
      if (coords != null && isWithinLootzone(coords[0], coords[1])) {
        if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        else if (leaveCombat && c.isInCombat()) leaveCombat();
        c.setStatus("@yel@Picking loot...");
        c.pickupItem(coords[0], coords[1], itemId, true, false);
        c.sleep(GAME_TICK);
        return;
      }
    }
  }
  /**
   * Checks for supplied itemId within lootzone (using isWithinLootzone method) and loots list of
   * items Use this method to wear an item before eating (like anti-dragon shield)
   *
   * @param wearId int item Id to equip
   * @param swapState boolean whether to use swap or not (pass a boolean from gui)
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   * @param itemIds int[] array listing itemIds to loot using for loop. Can institize item arrays in
   *     the method parameters with "new int[]{data}" as the value for this param.
   */
  protected static void lootItems(
      boolean leaveCombat, int[] itemIds, int wearId, boolean swapState) {
    for (int itemId : itemIds) {
      int[] coords = c.getNearestItemById(itemId);
      if (coords != null && isWithinLootzone(coords[0], coords[1])) {
        if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        else if (leaveCombat && c.isInCombat()) leaveCombat();
        if (swapState && !c.isItemIdEquipped(wearId)) {
          c.equipItem(c.getInventoryItemSlotIndex(wearId));
        }
        c.setStatus("@yel@Picking loot...");
        c.pickupItem(coords[0], coords[1], itemId, true, false);
        c.sleep(GAME_TICK);
        return;
      }
    }
  }
  /**
   * Checks fight mode against selected fightMode int, if no fightMode selector is provided, this
   * method would force controlled fight mode. fightMode 0 = controlled
   */
  protected static void checkFightMode(int fightMode) {
    if (c.getFightMode() != fightMode) {
      c.log("@red@Changing fightmode to " + fightMode, "@yel@");
      c.setFightMode(fightMode);
      c.sleep(GAME_TICK);
    }
  }
  /**
   * Drops all of specified itemId and waits for batch dropping to complete.
   *
   * @param itemId int of item to drop
   * @param amount int number of item to drop (negative value to drop all)
   * @param leaveCombat boolean - true will exit combat in order to drop. False will return, if in
   *     combat.
   */
  protected static void dropItemAmount(int itemId, int amount, boolean leaveCombat) {
    if (c.getInventoryItemCount(itemId) > 0) {
      if (leaveCombat && c.isInCombat()) leaveCombat(); // this may be breaking things ?
      else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
      c.dropItem(c.getInventoryItemSlotIndex(itemId), amount);
      // c.sleep(GAME_TICK);
    }
  }
  /**
   * Set autologin false, <br>
   * WHILE logged in attempt to log out,<br>
   * when not logged in then stop script <br>
   */
  protected static void endSession() {
    while (c.isLoggedIn()) {
      c.logout();
    }
    c.stop();
  }
  /** for each itemId in the inventory, deposit all the items. */
  protected static void depositAll() {
    for (int itemId : c.getInventoryItemIds()) {
      c.depositItem(itemId, c.getInventoryItemCount(itemId));
    }
    c.sleep(2 * GAME_TICK);
  }
  /**
   * for input boneIds, attempt to bury bones<br>
   * will leave combat to bury bones <br>
   *
   * @param leaveCombat
   * @param boneId
   */
  protected static void buryBones(boolean leaveCombat, int boneId) {
    if (c.getInventoryItemCount(boneId) > 0) {
      if (!c.isRunning() || (!leaveCombat && c.isInCombat())) return; // blocked by combat
      else if (leaveCombat && c.isInCombat()) leaveCombat();
      c.setStatus("@yel@Burying bones..");
      c.itemCommand(boneId);
      c.sleep(GAME_TICK);
    }
  }
  /**
   * for all boneIds, attempt to bury bones<br>
   * will leave combat to bury bones <br>
   */
  protected static void buryBones(boolean leaveCombat) {
    for (int id : bones) {
      if (c.getInventoryItemCount(id) > 0) {
        if (!c.isRunning() || (!leaveCombat && c.isInCombat())) return; // blocked by combat
        else if (leaveCombat && c.isInCombat()) leaveCombat();
        c.setStatus("@yel@Burying bones..");
        c.itemCommand(id);
        c.sleep(GAME_TICK);
        return;
      }
    }
  }
  /**
   * attempt to leave combat once per tick for 15 ticks<br>
   * walks to current tile (async non-blocking) radius 0. <br>
   */
  protected static void leaveCombat() {
    for (int i = 0; i <= 15; i++) {
      if (c.isInCombat()) {
        c.setStatus("@red@Leaving combat..");
        c.walkTo(c.currentX(), c.currentY());
        c.sleep(GAME_TICK);
      } else return;
    }
  }
  /**
   * Checks for full inventory, if true eat 1 food to make inventory space. <br>
   * Depending on leaveCombat.<br>
   *
   * @param leaveCombat boolean - true exits combat in order to drop. False will return, if in
   *     combat.
   */
  protected static void eatFoodToLoot(boolean leaveCombat) {
    for (int id : c.getFoodIds()) {
      if (!c.isRunning() || c.getInventoryItemCount() != 30) return;
      if (c.getInventoryItemCount(id) > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        c.setStatus("@red@Eating Food to Loot..");
        c.itemCommand(id);
        c.sleep(GAME_TICK);
        return;
      }
    }
  }
  /**
   * Checks for full inventory, if true bury bones to make inventory space.<br>
   * Depending on leaveCombat.
   *
   * @param leaveCombat boolean - true exits combat in order to drop. False will return, if in
   *     combat.
   */
  protected static void buryBonesToLoot(boolean leaveCombat) {
    for (int id : bones) {
      if (c.getInventoryItemCount() != 30) return;
      if (c.getInventoryItemCount(id) > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        c.setStatus("@yel@Burying bones..");
        c.itemCommand(id);
        c.sleep(GAME_TICK);
        return;
      }
    }
  }
  /**
   * Checks for full inventory, if true drops items to make inventory space.<br>
   * Depending on leaveCombat.
   *
   * @param leaveCombat boolean - true exits combat in order to drop. False will return, if in
   *     combat.
   */
  protected static void dropItemToLoot(boolean leaveCombat, int amount, int itemId) {
    if (c.getInventoryItemCount() != 30) return;
    int startCount = c.getInventoryItemCount(itemId);
    if (startCount > 0) {
      if (amount < 1) amount = 1;
      if (leaveCombat && c.isInCombat()) leaveCombat();
      else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
      c.dropItem(c.getInventoryItemSlotIndex(itemId), amount);
      c.sleep(GAME_TICK);
      for (int i = 0; i < 8; i++) {
        if (c.getInventoryItemCount(itemId) <= (startCount - amount)) break;
        c.sleep(GAME_TICK);
      }
    }
  }
  // TODO check for item (if equipped, else if in invent return true, else withdraw?)

  /*
   *      Potion Methods
   */
  /**
   * Boost with reg attack potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param boostAboveBase int - levels above base stat to boost at. Typically, 0 to 5 above base.
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   */
  /*
   * todo add int param to select how far above base to use boost potion
   */
  protected static void attackBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl = c.getBaseStat(c.getStatId("Attack")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Attack")) <= boostAtLvl) {
      final int oneDose = ItemId.ATTACK_POTION_1DOSE.getId();
      final int twoDose = ItemId.ATTACK_POTION_2DOSE.getId();
      final int threeDose = ItemId.ATTACK_POTION_3DOSE.getId();
      int attackPotCount =
          c.getInventoryItemCount(oneDose)
              + c.getInventoryItemCount(twoDose)
              + c.getInventoryItemCount(threeDose);
      if (attackPotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(oneDose) > 0) {
          c.itemCommand(oneDose);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(twoDose) > 0) {
          c.itemCommand(twoDose);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(threeDose) > 0) {
          c.itemCommand(threeDose);
          c.sleep(GAME_TICK);
        }
      }
    }
  }
  /**
   * Boost with reg strength potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param boostAboveBase int - levels above base stat to boost at. Typically, 0 to 5 above base.
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   */
  protected static void strengthBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl = c.getBaseStat(c.getStatId("Strength")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Strength")) <= boostAtLvl) {
      int strengthPotCount =
          c.getInventoryItemCount(strengthPot[0])
              + c.getInventoryItemCount(strengthPot[1])
              + c.getInventoryItemCount(strengthPot[2]);
      if (strengthPotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(strengthPot[0]) > 0) {
          c.itemCommand(strengthPot[0]);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(strengthPot[1]) > 0) {
          c.itemCommand(strengthPot[1]);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(strengthPot[2]) > 0) {
          c.itemCommand(strengthPot[2]);
          c.sleep(GAME_TICK);
        }
      }
    }
  }
  /**
   * Boost with reg defense potions. (checks for and uses 1 or 2 dose potions first)
   *
   * @param boostAboveBase int - levels above base stat to boost at. Typically, 0 to 5 above base.
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   */
  protected static void defenseBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl = c.getBaseStat(c.getStatId("Defense")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Defense")) <= boostAtLvl) {
      int defensePotCount =
          c.getInventoryItemCount(defensePot[0])
              + c.getInventoryItemCount(defensePot[1])
              + c.getInventoryItemCount(defensePot[2]);
      if (defensePotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(defensePot[0]) > 0) {
          c.itemCommand(defensePot[0]);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(defensePot[1]) > 0) {
          c.itemCommand(defensePot[1]);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(defensePot[2]) > 0) {
          c.itemCommand(defensePot[2]);
          c.sleep(GAME_TICK);
        }
      }
    }
  }
  /**
   * Boost with super attack potions. (checks for and uses 1 or 2 dose potions first)
   *
   * @param boostAboveBase int - levels above base stat to boost at. Typically, 0 to 10 above base.
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   */
  protected static void superAttackBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl = c.getBaseStat(c.getStatId("Attack")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Attack")) <= boostAtLvl) {
      int superAttackPotCount =
          c.getInventoryItemCount(superAttackPot[0])
              + c.getInventoryItemCount(superAttackPot[1])
              + c.getInventoryItemCount(superAttackPot[2]);
      if (superAttackPotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(superAttackPot[0]) > 0) {
          c.itemCommand(superAttackPot[0]);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(superAttackPot[1]) > 0) {
          c.itemCommand(superAttackPot[1]);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(superAttackPot[2]) > 0) {
          c.itemCommand(superAttackPot[2]);
          c.sleep(GAME_TICK);
        }
      } else {
        attackBoost(boostAboveBase, leaveCombat);
      }
    }
  }
  /**
   * Boost with super strength potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param boostAboveBase int - levels above base stat to boost at.
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   */
  protected static void superStrengthBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl = c.getBaseStat(c.getStatId("Strength")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Strength")) <= boostAtLvl) {
      int superStrengthPotCount =
          c.getInventoryItemCount(superStrengthPot[0])
              + c.getInventoryItemCount(superStrengthPot[1])
              + c.getInventoryItemCount(superStrengthPot[2]);
      if (superStrengthPotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(superStrengthPot[0]) > 0) {
          c.itemCommand(superStrengthPot[0]);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(superStrengthPot[1]) > 0) {
          c.itemCommand(superStrengthPot[1]);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(superStrengthPot[2]) > 0) {
          c.itemCommand(superStrengthPot[2]);
          c.sleep(GAME_TICK);
        }
      } else {
        strengthBoost(boostAboveBase, leaveCombat);
      }
    }
  }
  /**
   * Boost with super defense potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param boostAboveBase int - levels above base stat to boost at.
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   */
  protected static void superDefenseBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl = c.getBaseStat(c.getStatId("Defense")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Defense")) <= boostAtLvl) {
      int superDefensePotCount =
          c.getInventoryItemCount(superDefensePot[0])
              + c.getInventoryItemCount(superDefensePot[1])
              + c.getInventoryItemCount(superDefensePot[2]);
      if (superDefensePotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(superDefensePot[0]) > 0) {
          c.itemCommand(superDefensePot[0]);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(superDefensePot[1]) > 0) {
          c.itemCommand(superDefensePot[1]);
          c.sleep(GAME_TICK);
        } else if (c.getInventoryItemCount(superDefensePot[2]) > 0) {
          c.itemCommand(superDefensePot[2]);
          c.sleep(GAME_TICK);
        }
      } else {
        defenseBoost(boostAboveBase, leaveCombat);
      }
    }
  }
  /**
   * drinks prayer potions when 31 points below base stat level, leaving combat. Checks for and uses
   * 1 and 2 dose potions first. Recommend this boolean is ALWAYS true, or bot could die.
   *
   * @param boostBelowBase int - levels below base stat to boost at. Recommend 31 to not waste
   *     doses.
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   */
  protected static void drinkPrayerPotion(int boostBelowBase, boolean leaveCombat) {
    int boostAtLvl = c.getBaseStat(c.getStatId("Prayer")) - boostBelowBase;
    if (c.getCurrentStat(c.getStatId("Prayer")) <= boostAtLvl) {
      int prayerPotCount =
          c.getInventoryItemCount(prayerPot[0])
              + c.getInventoryItemCount(prayerPot[1])
              + c.getInventoryItemCount(prayerPot[2]);
      if (prayerPotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat

        if (c.getInventoryItemCount(prayerPot[0]) > 0) {
          c.itemCommand(prayerPot[0]);
          c.sleep(320);
        } else if (c.getInventoryItemCount(prayerPot[1]) > 0) {
          c.itemCommand(prayerPot[1]);
          c.sleep(320);
        } else if (c.getInventoryItemCount(prayerPot[2]) > 0) {
          c.itemCommand(prayerPot[2]);
          c.sleep(320);
        }
      }
    }
  }
  /**
   * drinks prayer potions when 31 points below base stat level, leaving combat. Checks for and uses
   * 1 and 2 dose potions first. Recommend this boolean is ALWAYS true, or bot could die. <br>
   * Use this method to wear an item before eating (like anti-dragon shield)
   *
   * @param wearId int item Id to equip
   * @param swapState boolean whether to use swap or not (pass a boolean from gui)
   * @param boostBelowBase int - levels below base stat to boost at. Recommend 31 to not waste
   *     doses.
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   */
  protected static void drinkPrayerPotion(
      int boostBelowBase, boolean leaveCombat, int wearId, boolean swapState) {
    int boostAtLvl = c.getBaseStat(c.getStatId("Prayer")) - boostBelowBase;
    if (c.getCurrentStat(c.getStatId("Prayer")) <= boostAtLvl) {
      int prayerPotCount =
          c.getInventoryItemCount(prayerPot[0])
              + c.getInventoryItemCount(prayerPot[1])
              + c.getInventoryItemCount(prayerPot[2]);
      if (prayerPotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (swapState && !c.isItemIdEquipped(wearId)) {
          c.equipItem(c.getInventoryItemSlotIndex(wearId));
          c.sleep(GAME_TICK);
        }
        if (c.getInventoryItemCount(prayerPot[0]) > 0) {
          c.itemCommand(prayerPot[0]);
          c.sleep(320);
        } else if (c.getInventoryItemCount(prayerPot[1]) > 0) {
          c.itemCommand(prayerPot[1]);
          c.sleep(320);
        } else if (c.getInventoryItemCount(prayerPot[2]) > 0) {
          c.itemCommand(prayerPot[2]);
          c.sleep(320);
        }
      }
    }
  }
  /**
   * drinks antidote potion, leaving combat. (checks for and uses 1 and 2 dose potions first)
   * Recommend this boolean is ALWAYS true, or bot could die.
   *
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   * @return boolean -- true if was able to drink, false if not
   */
  protected static boolean drinkAnti(boolean leaveCombat) {
    int[] curepoison = {
      ItemId.CURE_POISON_POTION_1DOSE.getId(),
      ItemId.CURE_POISON_POTION_2DOSE.getId(),
      ItemId.CURE_POISON_POTION_3DOSE.getId()
    };
    int[] antidote = {
      ItemId.POISON_ANTIDOTE_1DOSE.getId(),
      ItemId.POISON_ANTIDOTE_2DOSE.getId(),
      ItemId.POISON_ANTIDOTE_3DOSE.getId()
    };
    int curepoisonCount =
        c.getInventoryItemCount(curepoison[0])
            + c.getInventoryItemCount(curepoison[1])
            + c.getInventoryItemCount(curepoison[2]);
    int antidoteCount =
        c.getInventoryItemCount(antidote[0])
            + c.getInventoryItemCount(antidote[1])
            + c.getInventoryItemCount(antidote[2]);
    int[] pot;
    if (curepoisonCount > 0) pot = curepoison;
    else if (antidoteCount > 0) pot = antidote;
    else return false;
    if (leaveCombat && c.isInCombat()) leaveCombat();
    else if (!leaveCombat && c.isInCombat()) return false; // blocked by combat
    if (c.getInventoryItemCount(pot[0]) > 0) {
      c.itemCommand(pot[0]);
      c.sleep(GAME_TICK);
    } else if (c.getInventoryItemCount(pot[1]) > 0) {
      c.itemCommand(pot[1]);
      c.sleep(GAME_TICK);
    } else if (c.getInventoryItemCount(pot[2]) > 0) {
      c.itemCommand(pot[2]);
      c.sleep(GAME_TICK);
    }
    return true;
  }
  /*
   * BANKING METHODS
   *
   *
   *
   *
   *
   *
   *
   *
   *
   *
   *
   */
  /** if bank is not open, wait 2 ticks, repeat check. repeats 20 times. */
  protected static void waitForBankOpen() {
    c.sleep(640);
    for (int i = 0; i <= 200; i++) {
      if (!c.isRunning()) break;
      if (!c.isInBank()) {
        c.sleep(2 * GAME_TICK);
      } else {
        break;
      }
    }
  }
  /** if trade screen is not open, wait 3 ticks, trade again, repeat check. repeats 200 times. */
  protected static void waitForTradeOpen(String playerName) {
    for (int i = 0; i <= 200; i++) {
      if (!c.isRunning()) break;
      if (!c.isInTrade()) {
        c.tradePlayer(c.getPlayerServerIndexByName(playerName));
        c.sleep(3 * GAME_TICK);
      } else {
        break;
      }
    }
  }

  /** if trade confirmation screen is not open, wait 2 ticks, repeat check. repeats 200 times. */
  protected static void waitForTradeConfirmation() {
    for (int i = 0; i <= 200; i++) {
      if (!c.isRunning()) break;
      if (!c.isInTradeConfirmation()) {
        c.sleep(2 * GAME_TICK);
      } else {
        break;
      }
    }
  }
  /** if trade confirmation screen is not open, wait 2 ticks, repeat check. repeats 200 times. */
  protected static void waitForTradeConfirmationToClose() {
    for (int i = 0; i <= 200; i++) {
      if (!c.isRunning()) break;
      if (c.isInTradeConfirmation()) {
        c.sleep(2 * GAME_TICK);
      } else {
        break;
      }
    }
  }
  /**
   * if trade is not confirmed by the other player, wait 2 ticks, repeat check. repeats 200 times.
   */
  protected static void waitForTradeRecipientAccepting() {
    for (int i = 0; i <= 200; i++) {
      if (!c.isRunning()) break;
      if (!c.isTradeRecipientAccepting()) {
        c.sleep(2 * GAME_TICK);
      } else {
        break;
      }
    }
  }
  /**
   * Withdraw amount of item from the bank, accepts any itemId or withdraw Amount. For potions or
   * food use withdrawFood() or other methods below this. Withdraws none if inventory count is the
   * same or greater than withdraw amount.
   *
   * @param itemId int - accepts int variables such as "airId, lawId, earthId, waterId, fireId, etc"
   * @param withdrawAmount int - number of item to withdraw.
   */
  protected static void withdrawItem(int itemId, int withdrawAmount) {
    if (c.getInventoryItemCount(itemId) < withdrawAmount) {
      c.withdrawItem(itemId, withdrawAmount - c.getInventoryItemCount(itemId));
    }
  }

  /**
   * Checks bank for specified itemId, if none, sends error log message and ends session.
   *
   * @param itemId int - accepts int variables such as "airId, lawId, earthId, waterId, fireId, etc"
   * @param bankAmount int - minimum number of item that should be in the bank, before ending
   *     session.
   */
  protected static void bankItemCheck(int itemId, int bankAmount) {
    if (itemId == -1) itemId = ItemId.SHARK.getId();
    ItemId.getById(546);
    if (c.getBankItemCount(itemId) < bankAmount) {
      if (c.isInBank()) c.closeBank();
      c.log(
          "Warning: Item "
              + ItemId.getById(itemId)
              + " with Id ("
              + itemId
              + ") not detected in the Bank, in amount ("
              + bankAmount
              + ")",
          "@red@");
      c.setStatus("Item " + ItemId.getById(itemId) + " not detected in the Bank");
      c.sleep(30 * GAME_TICK);
      endSession();
    }
  }
  /**
   * checks inventory for specific item and amount, if too few opens banks and withdraws more
   *
   * @param itemId int - accepts int variables such as "airId, lawId, earthId, waterId, fireId, etc"
   * @param itemAmount int - number of item that should be in the inventory.
   */
  protected static void inventoryItemCheck(int itemId, int itemAmount) {
    if (c.getInventoryItemCount(itemId) < itemAmount) {
      c.withdrawItem(itemId, itemAmount - c.getInventoryItemCount(itemId));
    }
  }

  /**
   * Deposit itemId amounts that are greater than itemAmount provided. i.e shark, 5 deposits any
   * sharks over 5
   *
   * @param itemId int itemId to check
   * @param keepAmount int itemAmount to keep
   */
  protected static void depositExtra(int itemId, int keepAmount) {
    if (c.getInventoryItemCount(itemId) > keepAmount) {
      c.depositItem(itemId, c.getInventoryItemCount(itemId) - keepAmount);
    }
  }
  /**
   * Withdraw provided foodId, unless out, then attempt to withdraw any other foods
   *
   * @param foodId int
   * @param foodWithdrawAmount int
   */
  protected static void withdrawFood(int foodId, int foodWithdrawAmount) {
    if (c.getInventoryItemCount(foodId) < foodWithdrawAmount) {
      c.withdrawItem(foodId, foodWithdrawAmount - c.getInventoryItemCount(foodId));
      c.sleep(GAME_TICK);
    }
    if (c.getInventoryItemCount(foodId) < (foodWithdrawAmount - 2)) {
      for (int foodId2 : c.getFoodIds()) {
        c.withdrawItem(foodId2, foodWithdrawAmount - c.getInventoryItemCount(foodId) - 2);
      }
    }
  }
  /**
   * Withdraw reg attack potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  /*
   * todo make it withdraw one 2 dose and one 1 dose instead of 3 dose?
   */
  protected static void withdrawAttack(int withdrawAmount) {
    int attackPotCount =
        c.getInventoryItemCount(attackPot[0])
            + c.getInventoryItemCount(attackPot[1])
            + c.getInventoryItemCount(attackPot[2]);
    int bankPotCount = attackPot[0] + attackPot[1] + attackPot[2];
    if ((attackPotCount < withdrawAmount) && (bankPotCount > 0)) {
      if (c.getBankItemCount(attackPot[0]) > 0) {
        c.withdrawItem(attackPot[0], withdrawAmount - attackPotCount);
        c.sleep(2 * GAME_TICK); // 2 ticks
      } else if (c.getBankItemCount(attackPot[1]) > 0) {
        c.withdrawItem(attackPot[1], withdrawAmount - attackPotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(attackPot[2]) > 0) {
        c.withdrawItem(attackPot[2], withdrawAmount - attackPotCount);
        c.sleep(2 * GAME_TICK);
      }
      c.sleep(GAME_TICK); // necessary for resync
      int attackPotRecheck =
          (c.getInventoryItemCount(attackPot[0])
              + c.getInventoryItemCount(attackPot[1])
              + c.getInventoryItemCount(attackPot[2]));
      if (attackPotRecheck < withdrawAmount) {
        if (c.getBankItemCount(attackPot[2]) > 0) {
          c.withdrawItem(attackPot[2], withdrawAmount - attackPotRecheck);
          c.sleep(2 * GAME_TICK);
        }
      }
    }
  }

  /**
   * Withdraw reg strength potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  protected static void withdrawStrength(int withdrawAmount) {
    int strengthPotCount =
        c.getInventoryItemCount(strengthPot[0])
            + c.getInventoryItemCount(strengthPot[1])
            + c.getInventoryItemCount(strengthPot[2]);
    int bankPotCount =
        c.getBankItemCount(strengthPot[0])
            + c.getBankItemCount(strengthPot[1])
            + c.getBankItemCount(strengthPot[2]);
    if ((strengthPotCount < withdrawAmount) && (bankPotCount > 0)) {
      if (c.getBankItemCount(strengthPot[0]) > 0) {
        c.withdrawItem(strengthPot[0], withdrawAmount - strengthPotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(strengthPot[1]) > 0) {
        c.withdrawItem(strengthPot[1], withdrawAmount - strengthPotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(strengthPot[2]) > 0) {
        c.withdrawItem(strengthPot[2], withdrawAmount - strengthPotCount);
        c.sleep(2 * GAME_TICK);
      }
      c.sleep(GAME_TICK); // necessary for resync
      int strengthPotRecheck =
          (c.getInventoryItemCount(strengthPot[0])
              + c.getInventoryItemCount(strengthPot[1])
              + c.getInventoryItemCount(strengthPot[2]));
      if (strengthPotRecheck < withdrawAmount) {
        if (c.getBankItemCount(strengthPot[2]) > 0) {
          c.withdrawItem(strengthPot[2], withdrawAmount - strengthPotRecheck);
          c.sleep(2 * GAME_TICK);
        }
      }
    }
  }
  /**
   * Withdraw reg defense potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  protected static void withdrawDefense(int withdrawAmount) {
    int defensePotCount =
        c.getInventoryItemCount(defensePot[0])
            + c.getInventoryItemCount(defensePot[1])
            + c.getInventoryItemCount(defensePot[2]);
    int bankPotCount =
        c.getBankItemCount(defensePot[0])
            + c.getBankItemCount(defensePot[1])
            + c.getBankItemCount(defensePot[2]);
    if ((defensePotCount < withdrawAmount) && (bankPotCount > 0)) {
      if (c.getBankItemCount(defensePot[0]) > 0) {
        c.withdrawItem(defensePot[0], withdrawAmount - defensePotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(defensePot[1]) > 0) {
        c.withdrawItem(defensePot[1], withdrawAmount - defensePotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(defensePot[2]) > 0) {
        c.withdrawItem(defensePot[2], withdrawAmount - defensePotCount);
        c.sleep(2 * GAME_TICK);
      }
      c.sleep(GAME_TICK); // necessary for resync
      int defensePotRecheck =
          (c.getInventoryItemCount(defensePot[0])
              + c.getInventoryItemCount(defensePot[1])
              + c.getInventoryItemCount(defensePot[2]));
      if (defensePotRecheck < withdrawAmount) {
        if (c.getBankItemCount(defensePot[2]) > 0) {
          c.withdrawItem(defensePot[2], withdrawAmount - defensePotRecheck);
          c.sleep(2 * GAME_TICK);
        }
      }
    }
  }
  /**
   * Withdraw super attack potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  protected static void withdrawSuperAttack(int withdrawAmount) {
    int superAttackPotCount =
        c.getInventoryItemCount(superAttackPot[0])
            + c.getInventoryItemCount(superAttackPot[1])
            + c.getInventoryItemCount(superAttackPot[2]);
    int bankPotCount =
        c.getBankItemCount(superAttackPot[0])
            + c.getBankItemCount(superAttackPot[1])
            + c.getBankItemCount(superAttackPot[2]);
    if ((superAttackPotCount < withdrawAmount) && (bankPotCount > 0)) {
      if (c.getBankItemCount(superAttackPot[0]) > 0) {
        c.withdrawItem(superAttackPot[0], withdrawAmount - superAttackPotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(superAttackPot[1]) > 0) {
        c.withdrawItem(superAttackPot[1], withdrawAmount - superAttackPotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(superAttackPot[2]) > 0) {
        c.withdrawItem(superAttackPot[2], withdrawAmount - superAttackPotCount);
        c.sleep(2 * GAME_TICK);
      }
      c.sleep(GAME_TICK); // necessary for resync
      int superAttackPotRecheck =
          (c.getInventoryItemCount(superAttackPot[0])
              + c.getInventoryItemCount(superAttackPot[1])
              + c.getInventoryItemCount(superAttackPot[2]));
      if (superAttackPotRecheck < withdrawAmount) {
        if (c.getBankItemCount(superAttackPot[2]) > 0) {
          c.withdrawItem(superAttackPot[2], withdrawAmount - superAttackPotRecheck);
          c.sleep(2 * GAME_TICK);
        }
      }
    } else if (bankPotCount == 0) {
      withdrawAttack(withdrawAmount);
    }
  }
  /**
   * Withdraw super strength potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  protected static void withdrawSuperStrength(int withdrawAmount) {
    int superStrengthPotCount =
        c.getInventoryItemCount(superStrengthPot[0])
            + c.getInventoryItemCount(superStrengthPot[1])
            + c.getInventoryItemCount(superStrengthPot[2]);
    int bankPotCount =
        c.getBankItemCount(superStrengthPot[0])
            + c.getBankItemCount(superStrengthPot[1])
            + c.getBankItemCount(superStrengthPot[2]);
    if ((superStrengthPotCount < withdrawAmount) && (bankPotCount > 0)) {
      if (c.getBankItemCount(superStrengthPot[0]) > 0) {
        c.withdrawItem(superStrengthPot[0], withdrawAmount - superStrengthPotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(superStrengthPot[1]) > 0) {
        c.withdrawItem(superStrengthPot[1], withdrawAmount - superStrengthPotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(superStrengthPot[2]) > 0) {
        c.withdrawItem(superStrengthPot[2], withdrawAmount - superStrengthPotCount);
        c.sleep(2 * GAME_TICK);
      }
      c.sleep(GAME_TICK); // necessary for resync
      int superStrengthPotRecheck =
          (c.getInventoryItemCount(superStrengthPot[0])
              + c.getInventoryItemCount(superStrengthPot[1])
              + c.getInventoryItemCount(superStrengthPot[2]));
      if (superStrengthPotRecheck < withdrawAmount) {
        if (c.getBankItemCount(superStrengthPot[2]) > 0) {
          c.withdrawItem(superStrengthPot[2], withdrawAmount - superStrengthPotRecheck);
          c.sleep(2 * GAME_TICK);
        }
      }
    } else if (bankPotCount == 0) {
      withdrawStrength(withdrawAmount);
    }
  }
  /**
   * Withdraw super defense potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  protected static void withdrawSuperDefense(int withdrawAmount) {
    int superDefensePotCount =
        c.getInventoryItemCount(superDefensePot[0])
            + c.getInventoryItemCount(superDefensePot[1])
            + c.getInventoryItemCount(superDefensePot[2]);
    int bankPotCount =
        c.getBankItemCount(superDefensePot[0])
            + c.getBankItemCount(superDefensePot[1])
            + c.getBankItemCount(superDefensePot[2]);
    if ((superDefensePotCount < withdrawAmount) && (bankPotCount > 0)) {
      if (c.getBankItemCount(superDefensePot[0]) > 0) {
        c.withdrawItem(superDefensePot[0], withdrawAmount - superDefensePotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(superDefensePot[1]) > 0) {
        c.withdrawItem(superDefensePot[1], withdrawAmount - superDefensePotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(superDefensePot[2]) > 0) {
        c.withdrawItem(superDefensePot[2], withdrawAmount - superDefensePotCount);
        c.sleep(2 * GAME_TICK);
      }
      c.sleep(GAME_TICK); // necessary for resync
      int superDefensePotRecheck =
          (c.getInventoryItemCount(superDefensePot[0])
              + c.getInventoryItemCount(superDefensePot[1])
              + c.getInventoryItemCount(superDefensePot[2]));
      if (superDefensePotRecheck < withdrawAmount) {
        if (c.getBankItemCount(superDefensePot[2]) > 0) {
          c.withdrawItem(superDefensePot[2], withdrawAmount - superDefensePotRecheck);
          c.sleep(2 * GAME_TICK);
        }
      }
    } else if (bankPotCount == 0) {
      withdrawDefense(withdrawAmount);
    }
  }
  /** Withdraw antidote potions (checks for and uses 1 and 2 dose potions first) */
  // todo fix partial plus full potion withdraw
  protected static void withdrawAntidote(int withdrawAmount) {
    int antidotePotCount =
        c.getInventoryItemCount(antiPot[0])
            + c.getInventoryItemCount(antiPot[1])
            + c.getInventoryItemCount(antiPot[2]);
    int bankPotCount =
        c.getBankItemCount(antiPot[0])
            + c.getBankItemCount(antiPot[1])
            + c.getBankItemCount(antiPot[2]);
    if ((antidotePotCount < withdrawAmount) && (bankPotCount > 0)) {
      if (c.getBankItemCount(antiPot[0]) > 0) {
        c.withdrawItem(antiPot[0], withdrawAmount - antidotePotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(antiPot[1]) > 0) {
        c.withdrawItem(antiPot[1], withdrawAmount - antidotePotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(antiPot[2]) > 0) {
        c.withdrawItem(antiPot[2], withdrawAmount - antidotePotCount);
        c.sleep(2 * GAME_TICK);
      }
      c.sleep(GAME_TICK); // necessary for resync
      int antiPotRecheck =
          (c.getInventoryItemCount(antiPot[0])
              + c.getInventoryItemCount(antiPot[1])
              + c.getInventoryItemCount(antiPot[2]));
      if (antiPotRecheck < withdrawAmount) {
        if (c.getBankItemCount(antiPot[2]) > 0) {
          c.withdrawItem(antiPot[2], withdrawAmount - antiPotRecheck);
          c.sleep(2 * GAME_TICK);
        }
      }
    }
  }
  /** Withdraw prayer potions (checks for and uses 1 and 2 dose potions first) */
  protected static void withdrawPrayer(int withdrawAmount) {
    int prayerPotCount =
        (c.getInventoryItemCount(prayerPot[0])
            + c.getInventoryItemCount(prayerPot[1])
            + c.getInventoryItemCount(prayerPot[2]));
    int bankPotCount = prayerPot[0] + prayerPot[1] + prayerPot[2];
    if ((prayerPotCount < withdrawAmount) && (bankPotCount > 0)) {
      if (c.getBankItemCount(prayerPot[0]) > 0) {
        c.withdrawItem(prayerPot[0], withdrawAmount - prayerPotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(prayerPot[1]) > 0) {
        c.withdrawItem(prayerPot[1], withdrawAmount - prayerPotCount);
        c.sleep(2 * GAME_TICK);
      } else if (c.getBankItemCount(prayerPot[2]) > 0) {
        c.withdrawItem(prayerPot[2], withdrawAmount - prayerPotCount);
        c.sleep(2 * GAME_TICK);
      }
      c.sleep(GAME_TICK); // necessary for resync
      int prayerPotRecheck = // todo refactor this
          (c.getInventoryItemCount(prayerPot[0])
              + c.getInventoryItemCount(prayerPot[1])
              + c.getInventoryItemCount(prayerPot[2]));
      if (prayerPotRecheck < withdrawAmount) {
        if (c.getBankItemCount(prayerPot[2]) > 0) {
          c.withdrawItem(prayerPot[2], withdrawAmount - prayerPotRecheck);
          c.sleep(2 * GAME_TICK);
        }
      }
    }
  }
  /** Checks Inventory for empty slots. If detected, opens bank and withdraws more foodId. */
  protected static void reBankForFullFoodCheck() {
    if (c.getInventoryItemCount() < 30) {
      c.openBank();
      if (!c.isInBank()) {
        waitForBankOpen();
      } else {
        c.withdrawItem(foodId, 30);
      }
      c.closeBank();
    }
  }
  /**
   * Checks equipment for anti dragon shield, if none, checks bank. If none in bank, end session. If
   * anti dragon shield is in bank, withdraw, exit bank, and wield it.
   */
  protected static void bankCheckAntiDragonShield() {
    final int ANTI_SHIELD = ItemId.ANTI_DRAGON_BREATH_SHIELD.getId();
    if (!c.isItemIdEquipped(ANTI_SHIELD)) {
      if (c.getInventoryItemCount(ANTI_SHIELD) > 0) return;
      if (c.getBankItemCount(420) > 0) {
        c.withdrawItem(420, 1);
        c.closeBank();
        c.equipItem(c.getInventoryItemSlotIndex(420));
        c.sleep(GAME_TICK);
      } else {
        if (c.isInBank()) c.closeBank();
        c.setStatus("@red@ Warning: Cannot find anti dragon shield in bank, logging OUT");
        c.log("Warning: Cannot find anti dragon shield in bank, logging OUT", "@red@");
        c.sleep(30 * GAME_TICK);
        endSession();
      }
    }
  }

  /*
   *
   *
   *
   *
   *
   *
   *
   *
   *
   *
   *
   *      Magic/Other Methods
   */
  /** checks if brass key is in inventory, if not, sets warning message, and shuts down bot. */
  protected static void brassKeyCheck() {
    if (c.getInventoryItemCount(99) == 0) {
      if (c.isInBank()) c.closeBank();
      c.log("ERROR - No brass Key, shutting down bot in 30 Seconds", "@red@");
      c.sleep(10000);
      c.log("ERROR - No brass Key, shutting down bot in 20 Seconds", "@red@");
      c.sleep(10000);
      c.log("ERROR - No brass Key, shutting down bot in 10 Seconds", "@red@");
      c.sleep(5000);
      c.log("ERROR - No brass Key, shutting down bot", "@red@");
      c.sleep(1000);
      endSession();
    }
  }
  /** Loop to make sure to equip an item */
  protected static void forceEquipItem(int itemToEquip) {
    for (int i = 1; i <= 15; i++) {
      if (c.isRunning()
          && !c.isItemIdEquipped(itemToEquip)
          && c.getInventoryItemCount(itemToEquip) > 0) {
        c.equipItem(c.getInventoryItemSlotIndex(itemToEquip));
        c.sleep(4 * GAME_TICK);
      } else {
        c.sleep(4 * GAME_TICK);
        break;
      }
    }
  }
  /**
   * Loop Use agility cape to teleport Note that cape teleports do not work while in combat (unlike
   * normal teleports) Please ensure bot is in a safe zone before calling method, method will also
   * attempt to leaveCombat.
   */
  protected static void teleportAgilityCape() {
    int AGILITY_CAPE = ItemId.AGILITY_CAPE.getId();
    if (c.isItemIdEquipped(AGILITY_CAPE) && c.getInventoryItemCount(AGILITY_CAPE) < 1) {
      c.unequipItem(EquipSlotIndex.CAPE.getId());
    }
    if (c.isInCombat()) leaveCombat();
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() != 591 && c.currentY() != 765) {
        c.setStatus("@gre@Teleporting..");
        if (c.isInCombat()) leaveCombat();
        c.itemCommand(AGILITY_CAPE);
        c.sleep(4 * GAME_TICK);
      } else {
        c.sleep(4 * GAME_TICK);
        break;
      }
    }
  }
  /**
   * Loop Use craft cape to teleport Note that cape teleports do not work while in combat (unlike
   * normal teleports) Please ensure bot is in a safe zone before calling method, method will also
   * attempt to leaveCombat.
   */
  protected static void teleportCraftCape() {
    int CRAFTING_CAPE = ItemId.CRAFTING_CAPE.getId();
    if (c.isItemIdEquipped(CRAFTING_CAPE) && c.getInventoryItemCount(CRAFTING_CAPE) < 1) {
      c.unequipItem(EquipSlotIndex.CAPE.getId());
      c.sleep(640);
    }
    if (c.isInCombat()) leaveCombat();
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() != 347 && c.currentY() != 599) {
        c.setStatus("@gre@Teleporting..");
        if (c.isInCombat()) leaveCombat();
        c.itemCommand(CRAFTING_CAPE);
        c.sleep(4 * GAME_TICK);
      } else {
        c.sleep(4 * GAME_TICK);
        break;
      }
    }
  }
  /** Loop teleport to Lumbridge center */
  protected static void teleportLumbridge() {
    c.castSpellOnSelf(SpellId.LUMBRIDGE_TELEPORT.getId());
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() != 120 && c.currentY() != 648) {
        c.setStatus("@gre@Teleporting..");
        c.castSpellOnSelf(SpellId.LUMBRIDGE_TELEPORT.getId());
        c.sleep(4 * GAME_TICK);
      } else {
        c.sleep(4 * GAME_TICK);
        break;
      }
    }
  }
  /** Loop teleport to Falador center */
  protected static void teleportFalador() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() != 312 && c.currentY() != 552) {
        c.setStatus("@gre@Teleporting..");
        c.castSpellOnSelf(SpellId.FALADOR_TELEPORT.getId());
        c.sleep(4 * GAME_TICK);
      } else {
        c.sleep(4 * GAME_TICK);
        break;
      }
    }
  }
  /** Loop teleport to Varrock center */
  protected static void teleportVarrock() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() != 120 && c.currentY() != 504) {
        c.setStatus("@gre@Teleporting..");
        c.castSpellOnSelf(SpellId.VARROCK_TELEPORT.getId());
        c.sleep(4 * GAME_TICK);
      } else {
        c.sleep(4 * GAME_TICK);
        break;
      }
    }
  }
  /** Loop teleport to Camelot Castle */
  protected static void teleportCamelot() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() != 456 && c.currentY() != 456) {
        c.setStatus("@gre@Teleporting..");
        c.castSpellOnSelf(SpellId.CAMELOT_TELEPORT.getId());
        c.sleep(4 * GAME_TICK);
      } else {
        c.sleep(4 * GAME_TICK);
        break;
      }
    }
  }
  /** Loop teleport to Ardougne */
  protected static void teleportArdy() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() != 588 && c.currentY() != 621) {
        c.setStatus("@gre@Teleporting..");
        c.castSpellOnSelf(SpellId.ARDOUGNE_TELEPORT.getId());
        c.sleep(4 * GAME_TICK);
      } else {
        c.sleep(4 * GAME_TICK);
        break;
      }
    }
  }
  /** Loop teleport to Watchtower */
  protected static void teleportWatchtower() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.isRunning() && c.currentX() != 493 && c.currentY() != 3525) {
        c.setStatus("@gre@Teleporting..");
        c.castSpellOnSelf(SpellId.WATCHTOWER_TELEPORT.getId());
        c.sleep(4 * GAME_TICK);
      } else {
        c.sleep(4 * GAME_TICK);
        break;
      }
    }
  }

  /**
   * Method to open NON-FIXED doors/gates existing as objects (atObject command is used)
   * openWallObjectDoors() will handle WALL door/gate objects (atWallObject command is used
   *
   * @param objectId int of the door object when it is CLOSED
   * @param gateX int x coordinate of the gate
   * @param gateY int y coordinate of the gate
   */
  protected static void openDoorObjects(int objectId, int gateX, int gateY) {
    for (int i = 0; i < 100; i++) {
      int[] gateLocation = c.getNearestObjectById(objectId);
      if (gateLocation == null) return;
      if (c.isRunning() && gateLocation[0] == gateX && gateLocation[1] == gateY) {
        // Arrays.equals(gateLocation, new int[] {gateX, gateY})
        if (c.getNearestObjectById(objectId) != null) c.atObject(gateX, gateY);
        c.sleep(1280);
      } else {
        return;
      }
    }
  }
  /**
   * Method to open NON-FIXED doors/gates existing as WALL objects (atWallObject command is used)
   * openObjectDoors() will handle door/gate objects (atObject command is used
   *
   * @param objectId int of the door object when it is CLOSED
   * @param gateX int x coordinate of the gate
   * @param gateY int y coordinate of the gate
   */
  protected static void openWallDoorObjects(int objectId, int gateX, int gateY) {
    for (int i = 0; i < 100; i++) {
      int[] gateLocation = c.getNearestObjectById(objectId);
      if (gateLocation == null) return;
      if (c.isRunning() && gateLocation[0] == gateX && gateLocation[1] == gateY) {
        if (c.getNearestObjectById(objectId) != null)
          c.atWallObject(gateLocation[0], gateLocation[1]);
        c.sleep(2000);
      }
    }
  }
  /*      FIXED Gate Methods - i.e. gates that don't "open" and you instead teleport to other side  */
  /** opens wall door in edgeville dungeon that goes to the wilderness tunnel shortcut */
  protected static void edgeWallGate() {
    for (int i = 1; i <= 100; i++) {
      if (c.isRunning() && c.currentX() == 218 && c.currentY() == 3282) {
        c.setStatus("@gre@Opening Edge Wall Gate..");
        c.atWallObject(219, 3282);
        c.sleep(4 * GAME_TICK);
      } else {
        break;
      }
    }
  }
  /** goes through the wilderness tunnel shortcut */
  protected static void edgeShortcut() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() > 218 && c.currentY() > 3000) {
        c.setStatus("@gre@ Going through Edge Shortcut..");
        c.atObject(223, 3281);
        c.sleep(4 * GAME_TICK);
      } else {
        c.setStatus("@red@Done Opening Edge wall..");
        break;
      }
    }
  }
  /** Goes through the fixed gate leading to Tav from the south (craft guild). (south to north) */
  protected static void tavGateSouthToNorth() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() > 342 && c.currentX() < 345 && c.currentY() == 581) {
        c.setStatus("@red@Crossing Tav Gate..");
        c.atObject(343, 581); // gate won't break if someone else opens it
        c.sleep(4 * GAME_TICK);
      } else {
        break;
      }
    }
  }
  /** Goes through the fixed gate leading to Tav from the south (craft guild). (north to south) */
  protected static void tavGateNorthToSouth() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() > 342 && c.currentX() < 345 && c.currentY() == 580) {
        c.setStatus("@red@Crossing Tav Gate..");
        c.atObject(343, 581); // gate won't break if someone else opens it
        c.sleep(4 * GAME_TICK);
      } else {
        break;
      }
    }
  }

  /** Goes DOWN the ladder into edge dungeon, that is south of the bank. */
  protected void edgeLadderDown() {
    c.setStatus("@gre@Going down ladder..");
    for (int i = 0; i < 100; i++) {
      if (c.isRunning() && c.currentY() < 3000 && c.getNearestObjectById(6) != null) {
        c.walkTo(215, 467);
        c.atObject(215, 468);
        c.sleep(4 * GAME_TICK);
      } else break;
    }
    c.setStatus("@gre@Done Going down ladder..");
  }
  /** Goes UP the ladder exiting edge dungeon, that is south of the bank. */
  protected void edgeLadderUp() {
    c.setStatus("@gre@Going up ladder..");
    for (int i = 0; i < 100; i++) {
      if (c.isRunning() && c.currentY() > 3000 && c.getNearestObjectById(5) != null) {
        c.walkTo(215, 3299);
        c.atObject(215, 3300);
        c.sleep(4 * GAME_TICK);
      } else break;
    }
    c.setStatus("@gre@Done Going up ladder..");
  }
  /**
   * Enters through the fixed door leading to craft guild. (north to south) Bot will auto-detect
   * craft cape/brown apron and use them.
   */
  protected static void craftGuildDoorEntering(int reEquipItemId) {
    final int CRAFT_CAPE = ItemId.CRAFTING_CAPE.getId();
    final int BROWN_APRON = ItemId.BROWN_APRON.getId();
    if (c.getInventoryItemCount(CRAFT_CAPE) > 0 || c.isItemIdEquipped(CRAFT_CAPE)) {
      if (!c.isItemIdEquipped(CRAFT_CAPE)) {
        c.equipItem(c.getInventoryItemSlotIndex(CRAFT_CAPE));
        c.sleep(3 * GAME_TICK);
      }
    } else if (c.getInventoryItemCount(BROWN_APRON) > 0 || c.isItemIdEquipped(BROWN_APRON)) {
      if (!c.isItemIdEquipped(BROWN_APRON)) {
        c.equipItem(c.getInventoryItemSlotIndex(BROWN_APRON));
        c.sleep(3 * GAME_TICK);
      }
    } else {
      c.log(
          "No entrance item exits, you need a Crafting Cape or Brown Apron, EXITING BOT", "@red@");
      c.sleep(10000);
      endSession();
      return;
    }
    for (int i = 1; i <= 30; i++) {
      if (c.isRunning() && c.currentX() == 347 && c.currentY() == 600) {
        c.setStatus("@red@Entering Crafting Guild..");
        c.atWallObject(347, 601);
        c.sleep(6 * GAME_TICK);
      } else {
        c.sleep(6 * GAME_TICK);
        break;
      }
    }
    if (reEquipItemId > 0) {
      c.equipItem(c.getInventoryItemSlotIndex(reEquipItemId));
      c.sleep(GAME_TICK);
    }
  }
  /**
   * Exits through the fixed door leading to craft guild. (north to south) Bot will auto-detect
   * craft cape/brown apron and use them.
   */
  protected static void craftGuildDoorExiting(int reEquipItemId) {
    final int CRAFT_CAPE = ItemId.CRAFTING_CAPE.getId();
    final int BROWN_APRON = ItemId.BROWN_APRON.getId();
    if (c.getInventoryItemCount(CRAFT_CAPE) > 0 || c.isItemIdEquipped(CRAFT_CAPE)) {
      if (!c.isItemIdEquipped(CRAFT_CAPE)) {
        c.equipItem(c.getInventoryItemSlotIndex(CRAFT_CAPE));
        c.sleep(3 * GAME_TICK);
      }
    } else if (c.getInventoryItemCount(BROWN_APRON) > 0 || c.isItemIdEquipped(BROWN_APRON)) {
      if (!c.isItemIdEquipped(BROWN_APRON)) {
        c.equipItem(c.getInventoryItemSlotIndex(BROWN_APRON));
        c.sleep(3 * GAME_TICK);
      }
    } else {
      c.log(
          "No entrance item exits, you need a Crafting Cape or Brown Apron, EXITING BOT", "@red@");
      c.sleep(10000);
      endSession();
      return;
    }
    for (int i = 1; i <= 30; i++) {
      if (c.isRunning() && c.currentX() == 347 && c.currentY() == 601) {
        c.setStatus("@red@Exiting Crafting Guild..");
        c.atWallObject(347, 601);
        c.sleep(6 * GAME_TICK);
      } else {
        c.sleep(6 * GAME_TICK);
        break;
      }
    }
    if (reEquipItemId > 0) {
      c.equipItem(c.getInventoryItemSlotIndex(reEquipItemId));
      c.sleep(GAME_TICK);
    }
  }
  /** Goes through the fixed gate leading to Tav. (going from east to west) */
  protected static void tavGateEastToWest() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() != 342 && c.currentY() < 489 && c.currentY() > 486) {
        c.setStatus("@red@Crossing Tav Gate..");
        c.atObject(341, 487); // gate won't break if someone else opens it
        c.sleep(4 * GAME_TICK);
      } else {
        break;
      }
    }
  }

  /** Goes through the fixed gate leading to Tav. (going from west to east) */
  protected static void tavGateWestToEast() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() != 341 && c.currentY() < 489 && c.currentY() > 486) {
        c.setStatus("@red@Crossing Tav Gate..");
        c.atObject(341, 487); // gate won't break if someone else opens it
        c.sleep(4 * GAME_TICK);
      } else {
        break;
      }
    }
  }
  /** Goes through the fixed gate in Yanille dungeon (north to south) */
  protected static void yanilleDungeonDoorExiting() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() == 593 && c.currentY() == 3589) {
        c.atWallObject2(593, 3590); // locked door
        c.sleep(4 * GAME_TICK);
        if (c.isBatching()) c.sleep(2 * GAME_TICK);
      } else {
        break;
      }
    }
  }
  /** Goes through the fixed gate in Yanille dungeon (south to north) */
  protected static void yanilleDungeonDoorEntering() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentX() == 593 && c.currentY() == 3590) {
        c.atWallObject2(593, 3590); // locked door
        c.sleep(4 * GAME_TICK);
        if (c.isBatching()) c.sleep(2 * GAME_TICK);
      } else {
        break;
      }
    }
  }
  /** Goes through the fixed gate in Edge dungeon leading to wilderness area (north to south) */
  protected static void openEdgeDungGateNorthToSouth() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentY() == 3265) {
        c.setStatus("@gre@Opening Wildy Gate..");
        c.atObject(196, 3266);
        c.sleep(4 * GAME_TICK);
      } else {
        c.setStatus("@gre@Done Opening Wildy Gate..");
        break;
      }
    }
  }
  /** * Goes through the fixed gate in Edge dungeon leading to wilderness area (south to north) */
  protected void openEdgeDungSouthToNorth() {
    for (int i = 1; i <= 200; i++) {
      if (c.isRunning() && c.currentY() == 3266) {
        c.setStatus("@gre@Opening Wildy Gate..");
        c.atObject(196, 3266);
        c.sleep(4 * GAME_TICK);
      } else {
        c.setStatus("@gre@Done Opening Wildy Gate..");
        break;
      }
    }
  }
  /**
   * Goes through the fixed door west of varrock (brass key) leading to edge dungeon. Exiting (north
   * to south)
   */
  protected void brassDoorNorthToSouth() {
    int dustyKey = 99;
    for (int i = 1; i <= 20; i++) {
      if (c.isRunning() && c.currentX() == 202 && c.currentY() == 484) {
        c.useItemOnWall(202, 485, c.getInventoryItemSlotIndex(dustyKey));
        c.sleep(800);
      } else {
        break;
      }
    }
  }
  /**
   * Goes through the fixed door west of varrock (brass key) leading to edge dungeon. Entering
   * (south to north)
   */
  protected void brassDoorSouthToNorth() {
    int dustyKey = 99;
    for (int i = 1; i <= 20; i++) {
      if (c.isRunning() && c.currentX() == 202 && c.currentY() == 485) {
        c.useItemOnWall(202, 485, c.getInventoryItemSlotIndex(dustyKey));
        c.sleep(800);
      } else {
        break;
      }
    }
  }
  /** Goes through the fixed door leading into the druid tower. Exiting (north to south) */
  protected static void openDruidTowerNorthToSouth() {
    for (int i = 1; i <= 20; i++) {
      if (c.isRunning() && c.currentY() == 555) {
        c.setStatus("@gre@Opening Druid Gate..");
        c.atWallObject(617, 556);
        c.sleep(GAME_TICK);
      } else {
        c.setStatus("@gre@Done Opening Druid Gate..");
        break;
      }
    }
  }
  /** Goes through the fixed door leading into the druid tower. Entering (south to north) */
  protected static void openDruidTowerSouthToNorth() {
    for (int i = 1; i <= 20; i++) {
      if (c.isRunning() && c.currentY() == 556) {
        c.setStatus("@gre@Opening Druid Gate..");
        c.atWallObject2(617, 556);
        c.sleep(GAME_TICK);
      } else {
        c.setStatus("@gre@Done Opening Druid Gate..");
        break;
      }
    }
  }
  /** Goes through the fixed gate leading into red dragon isle. Existing (South to North) */
  protected static void redDragGateSouthToNorth() {
    for (int i = 1; i <= 20; i++) {
      if (c.isRunning() && c.currentX() > 139 && c.currentX() < 142 && c.currentY() == 181) {
        if (c.isInCombat()) leaveCombat();
        c.setStatus("@gre@Opening Dragon Gate South to North..");
        c.atObject(140, 180);
        c.sleep(2 * GAME_TICK);
      } else {
        break;
      }
    }
  }
  /** Goes through the fixed gate leading into red dragon isle. Entering (North to South) */
  protected static void redDragGateNorthToSouth() {
    for (int i = 1; i <= 20; i++) {
      if (c.isRunning() && c.currentX() > 139 && c.currentX() < 142 && c.currentY() == 180) {
        if (c.isInCombat()) leaveCombat();
        c.setStatus("@gre@Opening Dragon Gate North to South..");
        c.atObject(140, 180);
        c.sleep(2 * GAME_TICK);
      } else {
        break;
      }
    }
  }
}
  /*protected void combineDef() {
  	if(!c.isInCombat()) {  //not working
  		if(c.getInventoryItemCount(497) > 1) {
  			c.useItemOnItemBySlot(c.getInventoryItemSlotIndex(497), c.getInventoryItemSlotIndex(497));   //just need to fix this somehow, maby list lost index of item id kinda thing!
  			c.sleep(340);
  		}
  		if(c.getInventoryItemCount(497) > 0 && c.getInventoryItemCount(496) > 0 ) {  //this part works!!!
  			c.useItemOnItemBySlot(c.getInventoryItemSlotIndex(497), c.getInventoryItemSlotIndex(496));
  			c.sleep(340);
  		}
  	}
  }*/
