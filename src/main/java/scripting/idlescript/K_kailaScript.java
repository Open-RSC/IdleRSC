package scripting.idlescript;

import bot.Main;
import controller.Controller;
import javax.swing.*;

/**
 * WIP master file for common commands used in Kaila_Scripts
 *
 * @author Kaila
 */
/*
 *       todo
 *           fix teleport spot bounding
 *           replace eat food to loot with clearInventorySlot
 *          eat any 1 food script, return true/false to bank?
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
  public static final Controller c = Main.getController();
  public static JFrame scriptFrame = null;
  public static String foodName = "";
  // ~~~~~~~~~~~Boolean~~~~~~~~~~~~
  public static boolean guiSetup = false;
  public static boolean scriptStarted = false;
  public static boolean timeToBank = false;
  public static boolean timeToBankStay = false;
  public static boolean lootLowLevel = false;
  public static boolean lootBones = false;
  public static boolean buryBones = false;
  public static boolean lootLimp = false;
  public static boolean potUp = false;
  // ~~~~~~~~~~~~OTHER CONSTANTS~~~~~~~~~~~~~
  public static final long twoHundredFiftySecondsInMillis = 255000L;
  public static final long nineMinutesInMillis = 540000L;
  public static final long startTimestamp = System.currentTimeMillis() / 1000L;
  public static final int GAME_TICK = 640;
  public static final int EAT_LEVEL = c.getBaseStat(c.getStatId("Hits")) - 20;

  // ~~~~~~~~~~~~~ITEM CONSTANTS~~~~~~~~~~~
  public static final int UNCUT_SAPP = 160, UNCUT_EMER = 159, UNCUT_RUBY = 158, UNCUT_DIA = 157;
  public static final int CUT_SAPP = 164, CUT_EMER = 163, CUT_RUBY = 162, CUT_DIA = 161;
  public static final int UNID_GUAM = 165, UNID_MAR = 435, UNID_TAR = 436, UNID_HAR = 437;
  public static final int UNID_RANARR = 438, UNID_IRIT = 439, UNID_AVANTOE = 440, UNID_KWUARM = 441;
  public static final int UNID_CADA = 442, UNID_DWARF = 443, UNID_TORSTOL = 933;
  public static final int FIRE_RUNE = 31, WATER_RUNE = 32, AIR_RUNE = 33, EARTH_RUNE = 34;
  public static final int MIND_RUNE = 35, CHAOS_RUNE = 41, DEATH_RUNE = 38, BLOOD_RUNE = 619;
  public static final int LAW_RUNE = 42, NATURE_RUNE = 40, COSMIC_RUNE = 46, BODY_RUNE = 36;
  public static final int TOOTH_HALF = 526, LOOP_HALF = 527, RUNE_SPEAR = 1092, LEFT_HALF = 1277;
  public static final int COINS = 10, EMPTY_VIAL = 465, BRONZE_ARROW = 11, SPIN_ROLL = 179;
  public static final int LIMP_ROOT = 220;
  public static final int BONES = 20, BAT_BONES = 604, BIG_BONES = 413, DRAGON_BONES = 814;

  // ~~~~~~~~~~~random long/int~~~~~~~~~~~~~~~~

  public static long startTime;
  public static long next_attempt = -1;
  public static int foodInBank = -1;
  public static int usedFood = 0;
  public static int foodWithdrawAmount = -1;
  public static int prayPotWithdrawAmount = -1;
  public static int foodId = -1;
  public static int fightMode = 0;
  public static int totalTrips = 0;
  public static int centerX = -1;
  public static int centerY = -1;
  public static int centerDistance = -1;
  // Inventory Item Counts
  // Herbs
  public static int inventGuam = 0;
  public static int inventMar = 0;
  public static int inventTar = 0;
  public static int inventHar = 0;
  public static int inventRan = 0;
  public static int inventIrit = 0;
  public static int inventAva = 0;
  public static int inventKwuarm = 0;
  public static int inventCada = 0;
  public static int inventDwarf = 0;
  public static int inventHerbs = 0;
  // Runes
  public static int inventMind = 0;
  public static int inventChaos = 0;
  public static int inventDeath = 0;
  public static int inventBlood = 0;
  public static int inventLaws = 0;
  public static int inventNats = 0;
  public static int inventCosmic = 0;
  public static int inventAir = 0;
  public static int inventFire = 0;
  public static int inventWater = 0;
  public static int inventEarth = 0;
  public static int inventRunes = 0;
  // inventory Gems
  public static int inventSapphire = 0;
  public static int inventEmerald = 0;
  public static int inventRuby = 0;
  public static int inventDiamond = 0;
  public static int inventGems = 0;
  // inventory Rares
  public static int inventTooth = 0;
  public static int inventLoop = 0;
  public static int inventSpear = 0;
  public static int inventLeft = 0;

  // UNID HERBS
  public static int totalGuam = 0;
  public static int totalMar = 0;
  public static int totalTar = 0;
  public static int totalHerbs = 0;
  public static int totalHar = 0;
  public static int totalRan = 0;
  public static int totalIrit = 0;
  public static int totalAva = 0;
  public static int totalKwuarm = 0;
  public static int totalCada = 0;
  public static int totalDwarf = 0;

  // OTHER
  public static int usedBones = 0; // the ones you have buried in the ground
  public static int bankBones = 0; // the # you have IN the bank
  public static int totalBones = 0; // the ## you have deposited in the bank
  public static int totalPlates = 0;
  public static int totalBars = 0;

  // RUNES
  public static final int airId = 33;
  public static final int fireId = 31;
  public static final int waterId = 32;
  public static final int earthId = 34;
  public static final int lawId = 42;
  public static int totalAir = 0;
  public static int totalFire = 0;
  public static int totalWater = 0;
  public static int totalEarth = 0;
  public static int totalChaos = 0;
  public static int totalCosmic = 0;
  public static int totalLaw = 0;
  public static int totalNat = 0;
  public static int totalBlood = 0;
  public static int totalDeath = 0;

  // ORE
  public static int coalInBank = 0;
  public static int mithInBank = 0;
  public static int addyInBank = 0;
  public static int totalCoal = 0;
  public static int totalMith = 0;
  public static int totalAddy = 0; // defined in different ways, usually ore/bar depo script
  public static int barsInBank = 0;
  public static int logInBank = 0;
  // RDT
  public static int totalSap = 0;
  public static int totalEme = 0;
  public static int totalRub = 0;
  public static int totalDia = 0;
  public static int totalLoop = 0;
  public static int totalTooth = 0;
  public static int totalLeft = 0;
  public static int totalSpear = 0;

  // "totals"
  public static int totalHerb = 0;
  public static int totalGems = 0;
  public static int totalLog = 0;
  public static int totalRunes = 0;
  /*
   *
   *
   *
   *
   *
   *
   *      int[]
   */
  /**
   *
   *
   * <pre>
   * int[] array of bone id's
   *
   * [regular,big,bat,dragon] </pre>
   */
  public static final int[] bones = {
    20, // regular
    413, // big
    604, // bat?
    814 // dragon
  };
  /**
   *
   *
   * <pre>
   * int[] array of regular attack potions
   *
   * [1,2,3] doses </pre>
   */
  public static final int[] attackPot = {
    476, // reg attack pot (1)
    475, // reg attack pot (2)
    474 // reg attack pot (3)
  };
  /**
   *
   *
   * <pre>
   * int[] array of regular strength potions
   *
   * [1,2,3] doses </pre>
   */
  public static final int[] strengthPot = {
    224, // reg str pot (1)
    223, // reg str pot (2)
    222, // reg str pot (3)
    221 // reg str pot (4)
  };
  /**
   *
   *
   * <pre>
   * int[] array of regular defense potions
   *
   * [1,2,3] doses </pre>
   */
  public static final int[] defensePot = {
    482, // reg def pot (1)
    481, // reg def pot (2)
    480 // reg def pot (3)
  };
  /**
   *
   *
   * <pre>
   * int[] array of super attack potion
   *
   * [1,2,3] doses </pre>
   */
  public static final int[] superAttackPot = {
    488, // super  attack pot (1)
    487, // super  attack pot (2)
    486 // super attack pot (3)
  };
  /**
   *
   *
   * <pre>
   * int[] array of super strength potion
   *
   * [1,2,3] doses </pre>
   */
  public static final int[] superStrengthPot = {
    494, // super str pot (1)
    493, // super str pot (2)
    492 // super str pot (3)
  };
  /**
   *
   *
   * <pre>
   * int[] array of super defense potion
   *
   * [1,2,3] doses </pre>
   */
  public static final int[] superDefensePot = {
    497, // super defense pot (1)
    496, // super defense pot (2)
    495 // super defense pot (3)
  };
  /**
   *
   *
   * <pre>
   * int[] array of prayer potion
   *
   * [1,2,3] doses </pre>
   */
  public static final int[] prayerPot = {
    485, // prayer potion (1)
    484, // prayer potion (2)
    483 // prayer potion (3)
  };
  /**
   *
   *
   * <pre>
   * int[] array of antidote (the red one)
   *
   * [1,2,3] doses </pre>
   */
  public static final int[] antiPot = {
    571, // 1 dose
    570, // 2 dose
    569 // 3 dose
  };
  /**
   *
   *
   * <pre>
   *  int[] array of axe Id's
   *
   *  [bronze,iron,steel,black,mith,addy,rune] </pre>
   */
  public static final int[] axeId = {
    87, // bronze axe
    12, // iron axe
    88, // steel axe
    428, // black axe
    203, // mith axe
    204, // addy axe
    405 // rune axe
  };
  /**
   *
   *
   * <pre>
   * int[] array of bar Ids
   *
   * [1,2,3] doses </pre>
   */
  public static final int[] barIds = {
    169, // bronze bar
    170, // iron bar
    171, // steel bar
    173, // mithril bar
    174, // adamantite bar
    408 // runite bar
  };
  /**
   *
   *
   * <pre>
   * int[] array of log Ids
   *
   * [normal,oak,willow,maple,yew,magic] </pre>
   */
  public static final int[] logIds = {
    14, // normal logs
    632, // oak logs
    633, // willow logs
    634, // maple logs
    635, // yew logs
    636 // magic logs
  };
  /**
   *
   *
   * <pre>
   * int[] array of cooked food IDS
   *
   * [Manta,turtle,shark,swordfish,tuna,lobster,bass,mackerel,
   *      cod,pike,herring,salmon,trout,anchovies,shrimp,meat] </pre>
   */
  public static final int[] foodIds = {
    1191, // cooked Manta Ray
    1193, // cooked Sea Turtle
    546, // cooked shark
    370, // cooked swordfish
    367, // cooked tuna
    373, // cooked lobster
    555, // cooked Bass
    553, // cooked Mackerel
    551, // cooked Cod
    364, // cooked Pike
    362, // cooked Herring
    357, // cooked Salmon
    359, // cooked Trout
    352, // cooked Anchovies
    350, // cooked Shrimp
    132 // cooked Meat
  };
  /**
   *
   *
   * <pre>
   * String[] array of cooked food NAMES
   *
   * [Manta,turtle,shark,swordfish,tuna,lobster,bass,mackerel,
   *      cod,pike,herring,salmon,trout,anchovies,shrimp,meat] </pre>
   */
  public static final String[] foodTypes = {
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
  public static void checkInventoryItemCounts() {
    // Herbs
    inventGuam = c.getInventoryItemCount(UNID_GUAM);
    inventMar = c.getInventoryItemCount(UNID_MAR);
    inventTar = c.getInventoryItemCount(UNID_TAR);
    inventHar = c.getInventoryItemCount(UNID_HAR);
    inventRan = c.getInventoryItemCount(UNID_RANARR);
    inventIrit = c.getInventoryItemCount(UNID_IRIT);
    inventAva = c.getInventoryItemCount(UNID_AVANTOE);
    inventKwuarm = c.getInventoryItemCount(UNID_KWUARM);
    inventCada = c.getInventoryItemCount(UNID_CADA);
    inventDwarf = c.getInventoryItemCount(UNID_DWARF);
    // Runes
    inventLaws = c.getInventoryItemCount(LAW_RUNE);
    inventNats = c.getInventoryItemCount(NATURE_RUNE);
    inventMind = c.getInventoryItemCount(MIND_RUNE);
    inventChaos = c.getInventoryItemCount(CHAOS_RUNE);
    inventDeath = c.getInventoryItemCount(DEATH_RUNE);
    inventBlood = c.getInventoryItemCount(BLOOD_RUNE);
    inventCosmic = c.getInventoryItemCount(COSMIC_RUNE);
    inventAir = c.getInventoryItemCount(AIR_RUNE);
    inventFire = c.getInventoryItemCount(FIRE_RUNE);
    inventWater = c.getInventoryItemCount(WATER_RUNE);
    inventEarth = c.getInventoryItemCount(EARTH_RUNE);
    // Gems
    inventSapphire = c.getInventoryItemCount(UNCUT_SAPP);
    inventEmerald = c.getInventoryItemCount(UNCUT_EMER);
    inventRuby = c.getInventoryItemCount(UNCUT_RUBY);
    inventDiamond = c.getInventoryItemCount(UNCUT_DIA);
    inventGems = inventSapphire + inventEmerald + inventRuby + inventDiamond;
    // rares
    inventTooth = c.getInventoryItemCount(TOOTH_HALF);
    inventLoop = c.getInventoryItemCount(LOOP_HALF);
    inventLeft = c.getInventoryItemCount(LEFT_HALF);
    inventSpear = c.getInventoryItemCount(RUNE_SPEAR);

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
  /**
   *
   *
   * <pre>
   * sets foodName string to the name for the current foodId </pre>
   */
  /*
   *  todo refactor to input foodId, output foodName
   */
  public static void whatIsFoodName() {
    if (foodId == 1191) {
      foodName = "Manta Ray";
    } else if (foodId == 1193) {
      foodName = "Sea Turtle";
    } else if (foodId == 546) {
      foodName = "Shark";
    } else if (foodId == 370) {
      foodName = "Swordfish";
    } else if (foodId == 367) {
      foodName = "Tuna";
    } else if (foodId == 373) {
      foodName = "Lobster";
    } else if (foodId == 555) {
      foodName = "Bass";
    } else if (foodId == 553) {
      foodName = "Mackerel";
    } else if (foodId == 551) {
      foodName = "Cod";
    } else if (foodId == 364) {
      foodName = "Pike";
    } else if (foodId == 362) {
      foodName = "Herring";
    } else if (foodId == 357) {
      foodName = "Salmon";
    } else if (foodId == 359) {
      foodName = "Trout";
    } else if (foodId == 352) {
      foodName = "Anchovies";
    } else if (foodId == 350) {
      foodName = "Shrimp";
    } else if (foodId == 132) {
      foodName = "Cooked Meat";
    }
  }

  /* public static int whatIsMaxBoostLevel(String statName) { //super pots boost effect
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

  /**
   * Checks distance from provided x, y coords to the predefined center position and radius with int
   * values centerX, centerY, and centerDistance (set values in main method) If not predefined, then
   * method will return true as a default fallback
   *
   * @param x coordinate of point you are checking
   * @param y coordinate of point you are checking
   * @return true if point is within radius of center, false if outside radius distance from center.
   */
  public static boolean isWithinLootzone(int x, int y) {
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
  public static boolean eatFood() {
    boolean ate = false;
    if (c.getCurrentStat(c.getStatId("Hits")) < EAT_LEVEL) {
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
   * Checks for supplied itemId within lootzone (using isWithinLootzone method) and loots 1 of item.
   *
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   * @param itemId int of itemId to loot. For multiple items use lootItems(boolean, int[]);
   */
  public static void lootItem(boolean leaveCombat, int itemId) {
    try {
      int[] coords = c.getNearestItemById(itemId);
      if (coords != null && isWithinLootzone(coords[0], coords[1])) {
        c.setStatus("@yel@Picking Loot...");
        if (!leaveCombat && c.isInCombat()) return; // method blocked by combat
        c.walkToAsync(coords[0], coords[1], 0);
        c.pickupItem(coords[0], coords[1], itemId, true, false);
        c.sleep(GAME_TICK);
        if (buryBones) buryBones(false);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
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
  public static void lootItems(boolean leaveCombat, int[] itemIds) {
    for (int itemId : itemIds) {
      try {
        int[] coords = c.getNearestItemById(itemId);
        if (coords != null && isWithinLootzone(coords[0], coords[1])) {
          if (!leaveCombat && c.isInCombat()) return; // method blocked by combat
          c.setStatus("@yel@Picking loot...");
          c.walkToAsync(coords[0], coords[1], 0);
          c.pickupItem(coords[0], coords[1], itemId, true, false);
          c.sleep(GAME_TICK);
          if (buryBones) buryBones(false);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /**
   * Checks fight mode against selected fightMode int, if no fightMode selector is provided, this
   * method would force controlled fight mode. fightMode 0 = controlled
   */
  public static void checkFightMode() {
    if (c.getFightMode() != fightMode) {
      c.log("@red@Changing fightmode to " + fightMode, "@yel@");
      c.setFightMode(fightMode);
      c.sleep(640);
    }
  }
  /**
   * Drops all of specified itemId and waits for batch dropping to complete.
   *
   * @param itemId int of item to drop
   * @param amount int number of item to drop (negative value to drop all)
   * @param leaveCombat boolean - true exits combat in order to drop. False will return, if in
   *     combat.
   */
  public static void dropItemAmount(int itemId, int amount, boolean leaveCombat) {
    if (c.getInventoryItemCount(itemId) > 0) {
      if (leaveCombat && c.isInCombat()) leaveCombat();
      else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
      if (amount < 1) amount = 1;
      c.dropItem(c.getInventoryItemSlotIndex(itemId), amount);
      c.sleep(GAME_TICK);
      waitForBatching();
    }
  }
  /** If on coleslaw and batch bars are off, it will toggle ON batch bars */
  public static void checkBatchBars() {
    if (!c.isAuthentic() && !orsc.Config.C_BATCH_PROGRESS_BAR) c.toggleBatchBars();
  }
  /** while batching, sleep 1 Game tick. Unless next_attempt timestamp (triggers autowalk) */
  public static void waitForBatching() {
    while (c.isBatching() && System.currentTimeMillis() < next_attempt && next_attempt != -1) {
      c.sleep(GAME_TICK);
    }
  }
  /**
   *
   *
   * <pre>
   * Set autologin false,
   * WHILE logged in attempt to log out,
   * when not logged in then stop script </pre>
   */
  public static void endSession() {
    c.setAutoLogin(false);
    while (c.isLoggedIn()) {
      c.logout();
    }
    if (!c.isLoggedIn()) {
      c.stop();
    }
  }
  /** for each itemId in the inventory, deposit all the items. */
  public static void depositAll() {
    for (int itemId : c.getInventoryItemIds()) {
      c.depositItem(itemId, c.getInventoryItemCount(itemId));
    }
    c.sleep(650);
  }
  /**
   *
   *
   * <pre>
   * for all boneIds, attempt to bury bones
   *
   * will leave combat to bury bones </pre>
   */
  public static void buryBones(boolean leaveCombat) {
    for (int id : bones) {
      try {
        if (c.getInventoryItemCount(id) > 0) {
          if (leaveCombat && c.isInCombat()) leaveCombat();
          else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
          c.setStatus("@yel@Burying bones..");
          c.itemCommand(id);
          c.sleep(640);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /**
   *
   *
   * <pre>
   * attempt to leave combat once per tick for 30 ticks
   * walks to current tile (async non-blocking) radius 1. </pre>
   */
  public static void leaveCombat() {
    for (int i = 0; i <= 30; i++) {
      try {
        if (c.isInCombat()) {
          c.setStatus("@red@Leaving combat..");
          c.walkToAsync(c.currentX(), c.currentY(), 1);
          c.sleep(640);
        } else {
          return;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /**
   * Checks for full inventory, if true eat 1 food to make inventory space. Depending on
   * leaveCombat.
   *
   * @param leaveCombat boolean - true exits combat in order to drop. False will return, if in
   *     combat.
   */
  public static void eatFoodToLoot(boolean leaveCombat) {
    for (int id : c.getFoodIds()) {
      try {
        if (c.getInventoryItemCount() != 30) return;
        if (c.getInventoryItemCount(id) > 0) {
          if (leaveCombat && c.isInCombat()) leaveCombat();
          else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
          c.setStatus("@red@Eating Food to Loot..");
          c.itemCommand(id);
          c.sleep(GAME_TICK);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /**
   * Checks for full inventory, if true bury bones to make inventory space. Depending on
   * leaveCombat.
   *
   * @param leaveCombat boolean - true exits combat in order to drop. False will return, if in
   *     combat.
   */
  public static void buryBonesToLoot(boolean leaveCombat) {
    for (int id : bones) {
      try {
        if (c.getInventoryItemCount() != 30) return;
        if (c.getInventoryItemCount(id) > 0) {
          if (leaveCombat && c.isInCombat()) leaveCombat();
          else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
          c.setStatus("@yel@Burying bones..");
          c.itemCommand(id);
          c.sleep(GAME_TICK);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /**
   * Checks for full inventory, if true drops items to make inventory space. Depending on
   * leaveCombat.
   *
   * @param leaveCombat boolean - true exits combat in order to drop. False will return, if in
   *     combat.
   */
  public static void dropItemToLoot(boolean leaveCombat, int amount, int itemId) {
    try {
      if (c.getInventoryItemCount() != 30) return;
      if (c.getInventoryItemCount(itemId) > 0) {
        if (amount < 1) amount = 1;
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        c.dropItem(c.getInventoryItemSlotIndex(itemId), amount);
        c.sleep(GAME_TICK);
        waitForBatching();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
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
  public static void attackBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl;
    boostAtLvl = c.getBaseStat(c.getStatId("Attack")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Attack")) == boostAtLvl) {
      int attackPotCount =
          c.getInventoryItemCount(attackPot[0])
              + c.getInventoryItemCount(attackPot[1])
              + c.getInventoryItemCount(attackPot[2]);
      if (attackPotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(attackPot[0]) > 0) {
          c.itemCommand(attackPot[0]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(attackPot[1]) > 0) {
          c.itemCommand(attackPot[1]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(attackPot[2]) > 0) {
          c.itemCommand(attackPot[2]);
          c.sleep(640);
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
  public static void strengthBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl;
    boostAtLvl = c.getBaseStat(c.getStatId("Strength")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Strength")) == boostAtLvl) {
      int strengthPotCount =
          c.getInventoryItemCount(strengthPot[0])
              + c.getInventoryItemCount(strengthPot[1])
              + c.getInventoryItemCount(strengthPot[2]);
      if (strengthPotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(strengthPot[0]) > 0) {
          c.itemCommand(strengthPot[0]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(strengthPot[1]) > 0) {
          c.itemCommand(strengthPot[1]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(strengthPot[2]) > 0) {
          c.itemCommand(strengthPot[2]);
          c.sleep(640);
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
  public static void defenseBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl;
    boostAtLvl = c.getBaseStat(c.getStatId("Defense")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Defense")) == boostAtLvl) {
      int defensePotCount =
          c.getInventoryItemCount(defensePot[0])
              + c.getInventoryItemCount(defensePot[1])
              + c.getInventoryItemCount(defensePot[2]);
      if (defensePotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(defensePot[0]) > 0) {
          c.itemCommand(defensePot[0]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(defensePot[1]) > 0) {
          c.itemCommand(defensePot[1]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(defensePot[2]) > 0) {
          c.itemCommand(defensePot[2]);
          c.sleep(640);
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
  public static void superAttackBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl;
    boostAtLvl = c.getBaseStat(c.getStatId("Attack")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Attack")) == boostAtLvl) {
      int superAttackPotCount =
          c.getInventoryItemCount(superAttackPot[0])
              + c.getInventoryItemCount(superAttackPot[1])
              + c.getInventoryItemCount(superAttackPot[2]);
      if (superAttackPotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(superAttackPot[0]) > 0) {
          c.itemCommand(superAttackPot[0]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(superAttackPot[1]) > 0) {
          c.itemCommand(superAttackPot[1]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(superAttackPot[2]) > 0) {
          c.itemCommand(superAttackPot[2]);
          c.sleep(640);
        }
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
  public static void superStrengthBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl;
    boostAtLvl = c.getBaseStat(c.getStatId("Strength")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Strength")) == boostAtLvl) {
      int superStrengthPotCount =
          c.getInventoryItemCount(superStrengthPot[0])
              + c.getInventoryItemCount(superStrengthPot[1])
              + c.getInventoryItemCount(superStrengthPot[2]);
      if (superStrengthPotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(superStrengthPot[0]) > 0) {
          c.itemCommand(superStrengthPot[0]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(superStrengthPot[1]) > 0) {
          c.itemCommand(superStrengthPot[1]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(superStrengthPot[2]) > 0) {
          c.itemCommand(superStrengthPot[2]);
          c.sleep(640);
        }
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
  public static void superDefenseBoost(int boostAboveBase, boolean leaveCombat) {
    int boostAtLvl;
    boostAtLvl = c.getBaseStat(c.getStatId("Defense")) + boostAboveBase;
    if (c.getCurrentStat(c.getStatId("Defense")) == boostAtLvl) {
      int superDefensePotCount =
          c.getInventoryItemCount(superDefensePot[0])
              + c.getInventoryItemCount(superDefensePot[1])
              + c.getInventoryItemCount(superDefensePot[2]);
      if (superDefensePotCount > 0) {
        if (leaveCombat && c.isInCombat()) leaveCombat();
        else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
        if (c.getInventoryItemCount(superDefensePot[0]) > 0) {
          c.itemCommand(superDefensePot[0]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(superDefensePot[1]) > 0) {
          c.itemCommand(superDefensePot[1]);
          c.sleep(640);
        } else if (c.getInventoryItemCount(superDefensePot[2]) > 0) {
          c.itemCommand(superDefensePot[2]);
          c.sleep(640);
        }
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
  public static void drinkPrayerPotion(int boostBelowBase, boolean leaveCombat) {
    int boostAtLvl;
    boostAtLvl = c.getBaseStat(c.getStatId("Prayer")) - boostBelowBase;
    if (c.getCurrentStat(c.getStatId("Prayer")) < boostAtLvl) {
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
   * drinks antidote potion, leaving combat. (checks for and uses 1 and 2 dose potions first)
   * Recommend this boolean is ALWAYS true, or bot could die.
   *
   * @param leaveCombat boolean - true will exit combat in order to boost. False will return; if in
   *     combat.
   */
  public static void drinkAntidote(boolean leaveCombat) {
    int antiPotCount =
        c.getInventoryItemCount(antiPot[0])
            + c.getInventoryItemCount(antiPot[1])
            + c.getInventoryItemCount(antiPot[2]);
    if (antiPotCount > 0) {
      if (leaveCombat && c.isInCombat()) leaveCombat();
      else if (!leaveCombat && c.isInCombat()) return; // blocked by combat
      if (c.getInventoryItemCount(antiPot[0]) > 0) {
        c.itemCommand(antiPot[0]);
        c.sleep(640);
      } else if (c.getInventoryItemCount(antiPot[1]) > 0) {
        c.itemCommand(antiPot[1]);
        c.sleep(640);
      } else if (c.getInventoryItemCount(antiPot[2]) > 0) {
        c.itemCommand(antiPot[2]);
        c.sleep(640);
      }
    }
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
  /** if bank is not open, wait 2 ticks, repeat check. repeats 16 times. */
  public static void waitForBankOpen() {
    for (int i = 0; i <= 15; i++) {
      try {
        if (!c.isInBank()) {
          // c.log("waiting for bank");
          c.sleep(1280);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Withdraw amount of item from the bank, accepts any itemId or withdraw Amount. For potions or
   * food use withdrawFood() or other methods below this.
   *
   * @param itemId int - accepts int variables such as "airId, lawId, earthId, waterId, fireId, etc"
   * @param withdrawAmount int - number of item to withdraw.
   */
  public static void withdrawItem(int itemId, int withdrawAmount) {
    if (c.getInventoryItemCount(itemId) < withdrawAmount) {
      c.withdrawItem(itemId, withdrawAmount - c.getInventoryItemCount(itemId));
      c.sleep(640);
    }
  }

  /**
   * Checks bank for specified itemId, if none, sends error log message and ends session.
   *
   * @param itemId int - accepts int variables such as "airId, lawId, earthId, waterId, fireId, etc"
   * @param bankAmount int - minimum number of item that should be in the bank, before ending
   *     session.
   */
  public static void bankItemCheck(int itemId, int bankAmount) {
    if (c.getBankItemCount(itemId) < bankAmount) {
      c.log(
          "Warning: Item (" + itemId + ") not detected in the Bank, in amount (" + bankAmount + ")",
          "@red@");
      endSession();
    }
  }
  /**
   * checks inventory for specific item and amount, if too few opens banks and withdraws more
   *
   * @param itemId int - accepts int variables such as "airId, lawId, earthId, waterId, fireId, etc"
   * @param itemAmount int - number of item that should be in the inventory.
   */
  public static void inventoryItemCheck(int itemId, int itemAmount) {
    if (c.getInventoryItemCount(itemId) < itemAmount) {
      c.openBank();
      c.sleep(640);
      if (!c.isInBank()) {
        waitForBankOpen();
      } else {
        c.withdrawItem(itemId, itemAmount - c.getInventoryItemCount(itemId));
        c.sleep(640);
        c.closeBank();
        c.sleep(2*GAME_TICK);
      }
    }
  }
  /**
   * Withdraw provided foodId, unless out, then attempt to withdraw any other foods
   *
   * @param foodId int
   * @param foodWithdrawAmount int
   */
  public static void withdrawFood(int foodId, int foodWithdrawAmount) {
    if (c.getInventoryItemCount(foodId) < foodWithdrawAmount) {
      c.withdrawItem(foodId, foodWithdrawAmount - c.getInventoryItemCount(foodId));
      c.sleep(1280);
    }
    if (c.getInventoryItemCount(foodId) < (foodWithdrawAmount - 2)) {
      for (int foodId2 : c.getFoodIds()) {
        c.withdrawItem(foodId2, foodWithdrawAmount - c.getInventoryItemCount(foodId) - 2);
        c.sleep(640);
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
  public static void withdrawAttack(int withdrawAmount) {
    int attackPotCount =
        c.getInventoryItemCount(attackPot[0])
            + c.getInventoryItemCount(attackPot[1])
            + c.getInventoryItemCount(attackPot[2]);
    int bankPotCount = attackPot[0] + attackPot[1] + attackPot[2];
    if ((attackPotCount < withdrawAmount) && (bankPotCount > 0)) {
      if (c.getBankItemCount(attackPot[0]) > 0) {
        c.withdrawItem(attackPot[0], withdrawAmount - attackPotCount);
        c.sleep(640);
      } else if (c.getBankItemCount(attackPot[1]) > 0) {
        c.withdrawItem(attackPot[1], withdrawAmount - attackPotCount);
        c.sleep(640);
      } else if (c.getBankItemCount(attackPot[2]) > 0) {
        c.withdrawItem(attackPot[2], withdrawAmount - attackPotCount);
        c.sleep(640);
      }
    }
  }

  /**
   * Withdraw reg strength potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  public static void withdrawStrength(int withdrawAmount) {
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
        c.sleep(640);
      } else if (c.getBankItemCount(strengthPot[1]) > 0) {
        c.withdrawItem(strengthPot[1], withdrawAmount - strengthPotCount);
        c.sleep(640);
      } else if (c.getBankItemCount(strengthPot[2]) > 0) {
        c.withdrawItem(strengthPot[2], withdrawAmount - strengthPotCount);
        c.sleep(640);
      }
    }
  }
  /**
   * Withdraw reg defense potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  public static void withdrawDefense(int withdrawAmount) {
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
        c.sleep(640);
      } else if (c.getBankItemCount(defensePot[1]) > 0) {
        c.withdrawItem(defensePot[1], withdrawAmount - defensePotCount);
        c.sleep(640);
      } else if (c.getBankItemCount(defensePot[2]) > 0) {
        c.withdrawItem(defensePot[2], withdrawAmount - defensePotCount);
        c.sleep(640);
      }
    }
  }
  /**
   * Withdraw super attack potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  public static void withdrawSuperAttack(int withdrawAmount) {
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
        c.sleep(640);
      } else if (c.getBankItemCount(superAttackPot[1]) > 0) {
        c.withdrawItem(superAttackPot[1], withdrawAmount - superAttackPotCount);
        c.sleep(640);
      } else if (c.getBankItemCount(superAttackPot[2]) > 0) {
        c.withdrawItem(superAttackPot[2], withdrawAmount - superAttackPotCount);
        c.sleep(640);
      }
    }
  }
  /**
   * Withdraw super strength potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  public static void withdrawSuperStrength(int withdrawAmount) {
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
        c.sleep(640);
      } else if (c.getBankItemCount(superStrengthPot[1]) > 0) {
        c.withdrawItem(superStrengthPot[1], withdrawAmount - superStrengthPotCount);
        c.sleep(640);
      } else if (c.getBankItemCount(superStrengthPot[2]) > 0) {
        c.withdrawItem(superStrengthPot[2], withdrawAmount - superStrengthPotCount);
        c.sleep(640);
      }
    }
  }
  /**
   * Withdraw super defense potions (checks for and uses 1 or 2 dose potions first)
   *
   * @param withdrawAmount int
   */
  public static void withdrawSuperDefense(int withdrawAmount) {
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
        c.sleep(640);
      } else if (c.getBankItemCount(superDefensePot[1]) > 0) {
        c.withdrawItem(superDefensePot[1], withdrawAmount - superDefensePotCount);
        c.sleep(640);
      } else if (c.getBankItemCount(superDefensePot[2]) > 0) {
        c.withdrawItem(superDefensePot[2], withdrawAmount - superDefensePotCount);
        c.sleep(640);
      }
    }
  }
  /** Withdraw antidote potions (checks for and uses 1 and 2 dose potions first) */
  public static void withdrawAntidote(int withdrawAmount) {
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
        c.sleep(640);
      } else if (c.getBankItemCount(antiPot[1]) > 0) {
        c.withdrawItem(antiPot[1], withdrawAmount - antidotePotCount);
        c.sleep(640);
      } else if (c.getBankItemCount(antiPot[2]) > 0) {
        c.withdrawItem(antiPot[2], withdrawAmount - antidotePotCount);
        c.sleep(640);
      }
    }
  }
  /** Withdraw prayer potions (checks for and uses 1 and 2 dose potions first) */
  public static void withdrawPrayer(int withdrawAmount) {
    int prayerPotCount =
        c.getInventoryItemCount(prayerPot[0])
            + c.getInventoryItemCount(prayerPot[1])
            + c.getInventoryItemCount(prayerPot[2]);
    int bankPotCount = prayerPot[0] + prayerPot[1] + prayerPot[2];
    if ((prayerPotCount) < withdrawAmount && (bankPotCount > 0)) {
      if (c.getBankItemCount(prayerPot[0]) > 0) {
        c.withdrawItem(prayerPot[0], withdrawAmount - prayerPotCount);
        c.sleep(640);
      } else if (c.getBankItemCount(prayerPot[1]) > 0) {
        c.withdrawItem(prayerPot[1], withdrawAmount - prayerPotCount);
        c.sleep(640);
      } else if (c.getBankItemCount(prayerPot[2]) > 0) {
        c.withdrawItem(prayerPot[2], withdrawAmount - prayerPotCount);
        c.sleep(640);
      }
    }
  }
  /** Checks Inventory for empty slots. If detected, opens bank and withdraws more foodId. */
  public static void reBankForFullFoodCheck() {
    if (c.getInventoryItemCount() < 30) {
      c.openBank();
      c.sleep(1280);
      if (!c.isInBank()) {
        waitForBankOpen();
      } else {
        c.withdrawItem(foodId, 30 - c.getInventoryItemCount());
        c.sleep(640);
      }
      c.closeBank();
      c.sleep(2*GAME_TICK);
    }
  }
  /**
   * Checks equipment for anti dragon shield, if none, checks bank. If none in bank, end session. If
   * anti dragon shield is in bank, withdraw, exit bank, and wield it.
   */
  public static void bankCheckAntiDragonShield() {
    if (!c.isItemIdEquipped(420)) {
      c.log("@red@Not Wielding Dragonfire Shield!.", "@red@");
      if (c.getBankItemCount(420) == 0) {
        c.log("Warning: Cannot find anti dragon shield, logging OUT", "@red@");
        endSession();
      }
      c.withdrawItem(420, 1);
      c.closeBank();
      c.sleep(2 * GAME_TICK);
      c.equipItem(c.getInventoryItemSlotIndex(420));
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
  public static void brassKeyCheck() {
    if (c.getInventoryItemCount(99) == 0) {
      c.displayMessage("@red@ERROR - No brass Key, shutting down bot in 30 Seconds");
      c.sleep(10000);
      c.displayMessage("@red@ERROR - No brass Key, shutting down bot in 20 Seconds");
      c.sleep(10000);
      c.displayMessage("@red@ERROR - No brass Key, shutting down bot in 10 Seconds");
      c.sleep(5000);
      c.displayMessage("@red@ERROR - No brass Key, shutting down bot");
      c.sleep(1000);
      endSession();
    }
  }
  /**
   *
   *
   * <pre>
   * (depreciated) while in KBD lair attempt to tele to lumbridge </pre>
   *
   * todo change to "while NOT in lumbridge, attempt to teleport"
   */
  public static void teleportOutLumbridge() {
    for (int i = 1; i <= 8; i++) {
      try {
        if (c.currentY() < 400) { // change to lumb tele location
          c.setStatus("@gre@Done Walking..Teleporting(n)..");
          c.castSpellOnSelf(c.getSpellIdFromName("Lumbridge Teleport"));
          c.sleep(1280);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /**
   *
   *
   * <pre>
   * (depreciated) while in tav dungeon attempt to tele to falador </pre>
   *
   * todo change to "while NOT in location, then attempt to teleport"
   */
  public static void teleportOutFalador() {
    for (int i = 1; i <= 8; i++) {
      try {
        if (c.currentY() > 2000) { // change to fally tele location
          c.setStatus("@gre@Done Walking..Teleporting(n)..");
          c.castSpellOnSelf(c.getSpellIdFromName("Falador Teleport"));
          c.sleep(1280);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
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
   *
   *
   *
   *      Gate Methods
   */
  /** opens wall door in edgeville dungeon that goes to the wilderness tunnel shortcut */
  public static void edgeWallGate() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentX() == 218 && c.currentY() == 3282) {
          c.setStatus("@gre@Opening Edge Wall Gate..");
          c.atWallObject(219, 3282);
          c.sleep(1280);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** goes through the wilderness tunnel shortcut */
  public static void edgeShortcut() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentX() > 218 && c.currentY() > 3000) {
          c.setStatus("@gre@ Going through Edge Shortcut..");
          c.atObject(223, 3281);
          c.sleep(1280);
        } else {
          c.setStatus("@red@Done Opening Edge wall..");
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** Goes through the fixed gate leading to Tav. (going from east to west) */
  public static void tavGateEastToWest() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentX() == 341 && c.currentY() < 489 && c.currentY() > 486) {
          c.setStatus("@red@Crossing Tav Gate..");
          c.atObject(341, 487); // gate won't break if someone else opens it
          c.sleep(800);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** Goes through the fixed gate leading to Tav. (going from west to east) */
  public static void tavGateWestToEast() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentX() == 342 && c.currentY() < 489 && c.currentY() > 486) {
          c.atObject(341, 487); // gate won't break if someone else opens it
          c.sleep(800);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** Goes through the fixed gate in Yanille dungeon (north to south) */
  public static void yanilleDungeonDoorExiting() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentX() == 593 && c.currentY() == 3589) {
          c.atWallObject2(593, 3590); // locked door
          c.sleep(1000);
          if (c.isBatching()) c.sleep(5000);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** Goes through the fixed gate in Yanille dungeon (south to north) */
  public static void yanilleDungeonDoorEntering() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentX() == 593 && c.currentY() == 3590) {
          c.atWallObject2(593, 3590); // locked door
          c.sleep(640);
          if (c.isBatching()) c.sleep(5000);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** Goes through the fixed gate in Edge dungeon leading to wilderness area (north to south) */
  public static void openEdgeDungGateNorthToSouth() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentY() == 3265) {
          c.setStatus("@gre@Opening Wildy Gate..");
          c.atObject(196, 3266);
          c.sleep(640);
        } else {
          c.setStatus("@gre@Done Opening Wildy Gate..");
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** * Goes through the fixed gate in Edge dungeon leading to wilderness area (south to north) */
  public void openEdgeDungSouthToNorth() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentY() == 3266) {
          c.setStatus("@gre@Opening Wildy Gate..");
          c.atObject(196, 3266);
          c.sleep(440);
        } else {
          c.setStatus("@gre@Done Opening Wildy Gate..");
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /**
   * Goes through the fixed door west of varrock (brass key) leading to edge dungeon. Exiting (north
   * to south)
   */
  public void brassDoorNorthToSouth() {
    int dustyKey = 99;
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentX() == 202 && c.currentY() == 484) {
          c.useItemOnWall(202, 485, c.getInventoryItemSlotIndex(dustyKey));
          c.sleep(800);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /**
   * Goes through the fixed door west of varrock (brass key) leading to edge dungeon. Entering
   * (south to north)
   */
  public void brassDoorSouthToNorth() {
    int dustyKey = 99;
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentX() == 202 && c.currentY() == 485) {
          c.useItemOnWall(202, 485, c.getInventoryItemSlotIndex(dustyKey));
          c.sleep(800);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** Goes through the fixed door leading into the druid tower. Exiting (north to south) */
  public static void openDruidTowerNorthToSouth() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentY() == 555) {
          c.setStatus("@gre@Opening Druid Gate..");
          c.atWallObject(617, 556);
          c.sleep(640);
        } else {
          c.setStatus("@gre@Done Opening Druid Gate..");
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** Goes through the fixed door leading into the druid tower. Entering (south to north) */
  public static void openDruidTowerSouthToNorth() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentY() == 556) {
          c.setStatus("@gre@Opening Druid Gate..");
          c.atWallObject2(617, 556);
          c.sleep(640);
        } else {
          c.setStatus("@gre@Done Opening Druid Gate..");
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** Goes through the fixed gate leading into red dragon isle. Existing (South to North) */
  public static void redDragGateSouthToNorth() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentX() > 139 && c.currentX() < 142 && c.currentY() == 181) {
          if (c.isInCombat()) leaveCombat();
          c.setStatus("@gre@Opening Dragon Gate South to North..");
          c.atObject(140, 180);
          c.sleep(1280);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  /** Goes through the fixed gate leading into red dragon isle. Entering (North to South) */
  public static void redDragGateNorthToSouth() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.currentX() > 139 && c.currentX() < 142 && c.currentY() == 180) {
          if (c.isInCombat()) leaveCombat();
          c.setStatus("@gre@Opening Dragon Gate North to South..");
          c.atObject(140, 180);
          c.sleep(1280);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
  /*public void combineDef() {
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
