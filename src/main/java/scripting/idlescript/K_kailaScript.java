package scripting.idlescript;

import bot.Main;
import controller.Controller;
import javax.swing.*;

/**
 * WIP master file for common commands used in Kaila_Scripts
 *
 * <p>sans - documentation for now!
 *
 * <p>@Author - Kaila
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
  // ~~~~~~~~~~~~final~~~~~~~~~~~~~
  public static final long twoHundredFiftySecondsInMillis = 255000L;
  public static final long nineMinutesInMillis = 540000L;
  public static final long startTimestamp = System.currentTimeMillis() / 1000L;
  public static final int GAME_TICK = 640;
  public static final int eatLvl = c.getBaseStat(c.getStatId("Hits")) - 20;
  // ~~~~~~~~~~~random long/int~~~~~~~~~~~~~~~~
  public static long startTime;
  public static long next_attempt = -1;
  public static int foodInBank = -1;
  public static int usedFood = 0;
  public static int foodWithdrawAmount = -1;
  public static int foodId = -1;
  public static int fightMode = 0;
  public static int totalTrips = 0;

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
  public static int totalAirs = 0;
  public static int totalFire = 0;
  public static int totalWater = 0;
  public static int totalEarth = 0;
  public static int totalChaos = 0;

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
  public static final int[] bones = {
    20, // regular
    413, // big
    604, // bat?
    814 // dragon
  };
  public static final int[] attackPot = {
    476, // reg attack pot (1)
    475, // reg attack pot (2)
    474 // reg attack pot (3)
  };
  public static final int[] strengthPot = {
    224, // reg str pot (1)
    223, // reg str pot (2)
    222 // reg str pot (3)
  };
  public static final int[] superAttackPot = {
    488, // super  attack pot (1)
    487, // super  attack pot (2)
    486 // super attack pot (3)
  };
  public static final int[] superStrengthPot = {
    494, // super str pot (1)
    493, // super str pot (2)
    492 // super str pot (3)
  };
  public static final int[] superDefensePot = {
    497, // super defense pot (1)
    496, // super defense pot (2)
    495 // super defense pot (3)
  };
  public static final int[] antiPot = { // antidote
    571, // 1 dose
    570, // 2 dose
    569 // 3 dose
  };
  public static final int[] axeId = {
    87, // bronze axe
    12, // iron axe
    88, // steel axe
    428, // black axe
    203, // mith axe
    204, // addy axe
    405 // rune axe
  };
  public static final int[] barIds = {
    169, // bronze bar
    170, // iron bar
    171, // steel bar
    173, // mithril bar
    174, // adamantite bar
    408 // runite bar
  };
  public static final int[] logIds = {
    14, // normal logs
    632, // oak logs
    633, // willow logs
    634, // maple logs
    635, // yew logs
    636 // magic logs
  };
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
  public static final String[] foodTypes =
      new String[] {
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
  /*
   *
   *
   *
   *
   *
   *
   *          Main (useful) methods
   */
  public static void dropVial() {
    if (c.getInventoryItemCount(465) > 0 && !c.isInCombat()) {
      c.dropItem(c.getInventoryItemSlotIndex(465));
    }
  }

  public static void checkBatchBars() {
    if (!c.isAuthentic() && !orsc.Config.C_BATCH_PROGRESS_BAR) c.toggleBatchBars();
  }

  public void waitForBatching() {
    while (c.isBatching() && System.currentTimeMillis() < next_attempt) {
      c.sleep(1000);
    }
  }

  public static void endSession() {
    c.setAutoLogin(false);
    while (c.isLoggedIn()) {
      c.logout();
    }
    if (!c.isLoggedIn()) {
      c.stop();
    }
  }

  public static void depositAll() {
    for (int itemId : c.getInventoryItemIds()) {
      c.depositItem(itemId, c.getInventoryItemCount(itemId));
    }
    c.sleep(1280);
  }

  public static void buryBones() {
    for (int id : bones) {
      try {
        if (c.getInventoryItemCount(id) > 0) {
          if (c.isInCombat()) leaveCombat();
          c.setStatus("@yel@Burying bones..");
          c.itemCommand(id);
          c.sleep(640);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void leaveCombat() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.isInCombat()) {
          c.setStatus("@red@Leaving combat..");
          c.walkToAsync(c.currentX(), c.currentY(), 1);
          c.sleep(640);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void eatFoodToLoot() {
    for (int id : c.getFoodIds()) {
      try {
        if (c.getInventoryItemCount(id) > 0 && c.getInventoryItemCount() == 30) {
          if (c.isInCombat()) leaveCombat();
          c.setStatus("@red@Eating Food to Loot..");
          c.itemCommand(id);
          c.sleep(640);
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
   *      Potion Methods
   */

  /**
   * Will withdraw provided foodId, unless out, it will then attempt to withdraw any other foods
   *
   * @param foodId int
   * @param foodWithdrawAmount int
   */
  public static void withdrawFood(int foodId, int foodWithdrawAmount) {
    if (c.isInBank()) {
      if (c.getInventoryItemCount(foodId) < foodWithdrawAmount) {
        c.withdrawItem(foodId, foodWithdrawAmount - c.getInventoryItemCount(foodId));
        c.sleep(640);
      }
      if (c.getInventoryItemCount(foodId) < foodWithdrawAmount) {
        for (int foodId2 : c.getFoodIds()) {
          if (c.getInventoryItemCount() < 30) { // todo find a better way, using foodIds
            c.withdrawItem(foodId2, c.getBankItemCount(foodId2) - 1);
            c.sleep(640);
          }
        }
      }
    }
  }

  public static void withdrawAttack(int withdrawAmount) {
    int attackPotCount =
        c.getInventoryItemCount(attackPot[0])
            + c.getInventoryItemCount(attackPot[1])
            + c.getInventoryItemCount(attackPot[2]);
    if (attackPotCount < withdrawAmount) {
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

  public static void withdrawStrength(int withdrawAmount) {
    int strengthPotCount =
        c.getInventoryItemCount(strengthPot[0])
            + c.getInventoryItemCount(strengthPot[1])
            + c.getInventoryItemCount(strengthPot[2]);
    if (strengthPotCount < withdrawAmount) {
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

  public static void withdrawSuperAttack(int withdrawAmount) {
    int superAttackPotCount =
        c.getInventoryItemCount(superAttackPot[0])
            + c.getInventoryItemCount(superAttackPot[1])
            + c.getInventoryItemCount(superAttackPot[2]);
    if (superAttackPotCount < withdrawAmount) {
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

  public static void withdrawSuperStrength(int withdrawAmount) {
    int superStrengthPotCount =
        c.getInventoryItemCount(superStrengthPot[0])
            + c.getInventoryItemCount(superStrengthPot[1])
            + c.getInventoryItemCount(superStrengthPot[2]);
    if (superStrengthPotCount < withdrawAmount) {
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

  public static void withdrawSuperDefense(int withdrawAmount) {
    int superDefensePotCount =
        c.getInventoryItemCount(superDefensePot[0])
            + c.getInventoryItemCount(superDefensePot[1])
            + c.getInventoryItemCount(superDefensePot[2]);
    if (superDefensePotCount < withdrawAmount) {
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

  public static void withdrawAntidote() {
    if (c.getInventoryItemCount(antiPot[0]) < 1
        && c.getInventoryItemCount(antiPot[1]) < 1
        && c.getInventoryItemCount(antiPot[2]) < 1) {
      if (c.getBankItemCount(antiPot[0]) > 0) {
        c.withdrawItem(antiPot[0], 1);
        c.sleep(640);
      } else if (c.getBankItemCount(antiPot[1]) > 0) {
        c.withdrawItem(antiPot[1], 1);
        c.sleep(640);
      } else if (c.getBankItemCount(antiPot[2]) > 0) {
        c.withdrawItem(antiPot[2], 1);
        c.sleep(640);
      }
    }
  }

  public static void attackBoost() {
    if (c.getCurrentStat(c.getStatId("Attack")) == c.getBaseStat(c.getStatId("Attack"))) {
      int attackPotCount =
          c.getInventoryItemCount(attackPot[0])
              + c.getInventoryItemCount(attackPot[1])
              + c.getInventoryItemCount(attackPot[2]);
      if (attackPotCount > 0) {
        if (c.isInCombat()) leaveCombat();
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

  public static void strengthBoost() {
    if (c.getCurrentStat(c.getStatId("Strength")) == c.getBaseStat(c.getStatId("Strength"))) {
      int strengthPotCount =
          c.getInventoryItemCount(strengthPot[0])
              + c.getInventoryItemCount(strengthPot[1])
              + c.getInventoryItemCount(strengthPot[2]);
      if (strengthPotCount > 0) {
        if (c.isInCombat()) leaveCombat();
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

  public static void superAttackBoost() {
    if (c.getCurrentStat(c.getStatId("Attack")) == c.getBaseStat(c.getStatId("Attack"))) {
      int superAttackPotCount =
          c.getInventoryItemCount(superAttackPot[0])
              + c.getInventoryItemCount(superAttackPot[1])
              + c.getInventoryItemCount(superAttackPot[2]);
      if (superAttackPotCount > 0) {
        if (c.isInCombat()) leaveCombat();
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

  public static void superStrengthBoost() {
    if (c.getCurrentStat(c.getStatId("Strength")) == c.getBaseStat(c.getStatId("Strength"))) {
      int superStrengthPotCount =
          c.getInventoryItemCount(superStrengthPot[0])
              + c.getInventoryItemCount(superStrengthPot[1])
              + c.getInventoryItemCount(superStrengthPot[2]);
      if (superStrengthPotCount > 0) {
        if (c.isInCombat()) leaveCombat();
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

  public static void superDefenseBoost() {
    if (c.getCurrentStat(c.getStatId("Defense")) == c.getBaseStat(c.getStatId("Defense"))) {
      int superDefensePotCount =
          c.getInventoryItemCount(superDefensePot[0])
              + c.getInventoryItemCount(superDefensePot[1])
              + c.getInventoryItemCount(superDefensePot[2]);
      if (superDefensePotCount > 0) {
        if (c.isInCombat()) leaveCombat();
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

  public static void superAttackBoostCustom(int boostAt) {
    if (c.getCurrentStat(c.getStatId("Attack")) == boostAt) {
      int superAttackPotCount =
          c.getInventoryItemCount(superAttackPot[0])
              + c.getInventoryItemCount(superAttackPot[1])
              + c.getInventoryItemCount(superAttackPot[2]);
      if (superAttackPotCount > 0) {
        if (c.isInCombat()) leaveCombat();
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

  public static void superStrengthBoostCustom(int boostAt) {
    if (c.getCurrentStat(c.getStatId("Strength")) == boostAt) {
      int superStrengthPotCount =
          c.getInventoryItemCount(superStrengthPot[0])
              + c.getInventoryItemCount(superStrengthPot[1])
              + c.getInventoryItemCount(superStrengthPot[2]);
      if (superStrengthPotCount > 0) {
        if (c.isInCombat()) leaveCombat();
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

  public static void superDefenseBoostCustom(int boostAt) {
    if (c.getCurrentStat(c.getStatId("Defense")) == boostAt) {
      int superDefensePotCount =
          c.getInventoryItemCount(superDefensePot[0])
              + c.getInventoryItemCount(superDefensePot[1])
              + c.getInventoryItemCount(superDefensePot[2]);
      if (superDefensePotCount > 0) {
        if (c.isInCombat()) leaveCombat();
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

  public static void drinkPrayerPotion() {
    if (c.getCurrentStat(c.getStatId("Prayer")) < (c.getBaseStat(c.getStatId("Prayer")) - 31)) {
      if (c.getInventoryItemCount(485) > 0
          || c.getInventoryItemCount(484) > 0
          || c.getInventoryItemCount(483) > 0) {
        if (c.getInventoryItemCount(485) > 0) {
          c.itemCommand(485);
          c.sleep(320);
        } else if (c.getInventoryItemCount(484) > 0) {
          c.itemCommand(484);
          c.sleep(320);
        } else if (c.getInventoryItemCount(483) > 0) {
          c.itemCommand(483);
          c.sleep(320);
        }
      }
    }
  }

  public static void drinkAntidote() {
    if (c.isInCombat()) leaveCombat();
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

  public static void waterCheck() {
    if (c.getInventoryItemCount(32) < 6) { // 2 water
      c.openBank();
      c.sleep(1200);
      c.withdrawItem(32, 6 - c.getInventoryItemCount(32));
      c.sleep(1000);
      c.closeBank();
      c.sleep(1000);
    }
  }

  public static void lawCheck() {
    if (c.getInventoryItemCount(42) < 2) { // law
      c.openBank();
      c.sleep(1200);
      c.withdrawItem(42, 2 - c.getInventoryItemCount(42));
      c.sleep(1000);
      c.closeBank();
      c.sleep(1000);
    }
  }

  public static void earthCheck() {
    if (c.getInventoryItemCount(34) < 2) { // earth
      c.openBank();
      c.sleep(1200);
      c.withdrawItem(34, 2 - c.getInventoryItemCount(34));
      c.sleep(1000);
      c.closeBank();
      c.sleep(1000);
    }
  }

  public static void airCheck() {
    if (c.getInventoryItemCount(33) < 6) { // air
      c.openBank();
      c.sleep(1200);
      c.withdrawItem(33, 6 - c.getInventoryItemCount(33));
      c.sleep(1000);
      c.closeBank();
      c.sleep(1000);
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
