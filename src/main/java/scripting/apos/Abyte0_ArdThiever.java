package scripting.apos;

/** * Based on Any Thiever from yomama` */
/** pickpockets anything, eats/banks (cakes) * - yomama` */
// Version 5.0 Updated to OpenRSC 2021-06-29
// Version 5.1 Support Hero
public class Abyte0_ArdThiever extends Abyte0_Script {
  int MAX_INVENTORY_SIZE = 30;
  int freeInventrySlotsRequired = 21;
  int fightMode = 3;
  int hero = 324;
  boolean isDoingHero = false;

  int goldId = 152;
  int chaosId = 41;
  int deathId = 38;
  int bloodId = 619;
  int wineId = 142;
  int diamonId = 161;

  int[] npcID =
      new int[] {
        // all types of MAN npc's by default
        11, 24, 72, 307, 318, 750, 323 // Paladin
      };
  int[] dropIDs =
      new int[] {
        140, // jug
        612, // fire orb
        714, // lockpick
        559 // Poisoned Iron dagger
      };
  int[] foodIDs =
      new int[] {
        330, // cake 3/3
        333, // cake 2/3
        335, // cake 1/3
        895, // Swamp Toad
        897, // King worm
        138, // bread
        wineId // wine
      };
  int[] bankIDs =
      new int[] {
        diamonId, // diamond
        goldId, // gold
        wineId // wine
      };

  public Abyte0_ArdThiever(String e) {
    super(e);
  }

  public void init(String params) {
    System.out.println("ArdThiever fmode,npc,npc,npc...  paladin is 323, hero 324");
    System.out.println("ArdThiever V5.1");
    String[] in = params.split(",");
    fightMode = Integer.parseInt(in[0]);
    npcID = new int[in.length - 1];
    for (int i = 0; i < npcID.length; i++) {
      npcID[i] = Integer.parseInt(in[i + 1]);
      if (npcID[i] == hero) isDoingHero = true;
    }
  }

  public int main() {
    if (getFightMode() != fightMode) setFightMode(fightMode);
    if (inCombat()) {
      walkTo(getX(), getY());
      return random(800, 1111);
    }
    if (getFatigue() > 90) {
      useSleepingBag();
      return 1000;
    }
    if (getInventoryCount(dropIDs) > 0) {
      dropItem(getInventoryIndex(dropIDs));
    }
    if (isBanking()) {
      // deposit money and keep 1 gp
      if (getInventoryCount(10) > 1) {
        deposit(10, getInventoryCount(10) - 1);
        return random(500, 600);
      } else if (getInventoryCount(10) < 1) {
        withdraw(10, 1);
        return random(500, 600);
      }
      // deposit chaos and keep 1 chaos
      if (getInventoryCount(chaosId) > 1) {
        deposit(chaosId, getInventoryCount(chaosId) - 1);
        return random(500, 600);
      }
      if (getInventoryCount(deathId) > 1) {
        deposit(deathId, getInventoryCount(deathId) - 1);
        return random(500, 600);
      }
      if (getInventoryCount(bloodId) > 1) {
        deposit(bloodId, getInventoryCount(bloodId) - 1);
        return random(500, 600);
      }
      if (getInventoryCount(goldId) > 0) {
        deposit(goldId, getInventoryCount(goldId));
        return random(500, 600);
      }
      if (getInventoryCount(wineId) > 0) {
        deposit(wineId, getInventoryCount(wineId));
        return random(500, 600);
      }
      if (getInventoryCount(diamonId) > 0) {
        deposit(diamonId, getInventoryCount(diamonId));
        return random(500, 600);
      }
      // else if(getInventoryCount(41) < 1)
      // {
      //	withdraw(41,1);
      //	return random(500, 600);
      // }

      if ((getInventoryCount() == MAX_INVENTORY_SIZE && !isDoingHero)
          || getInventoryCount() >= MAX_INVENTORY_SIZE - freeInventrySlotsRequired && isDoingHero) {
        closeBank();
        return random(500, 600);
      }

      if (isDoingHero) {
        withdraw(330, MAX_INVENTORY_SIZE - freeInventrySlotsRequired - getInventoryCount());
        return random(700, 800);
      } else {
        withdraw(330, MAX_INVENTORY_SIZE - getInventoryCount() - 1);
        return random(700, 800);
      }
    }
    if (isQuestMenu()) {
      answer(0);
      return random(5500, 5600);
    }

    // verrification si on est trapper
    if (getX() >= 555 && getX() <= 558 && getY() >= 587 && getY() <= 590) {
      // si la la clauture dans la garnde cloture
      int[] Door = getObjectById(57);
      if (Door[0] != -1) {
        atObject(Door[1], Door[2]);
        return random(800, 900);
      }
    } else if (getX() >= 555 && getX() <= 560 && getY() >= 577 && getY() <= 586) {
      // si  la garnde cloture
      int[] Door = getObjectById(57);
      if (Door[0] != -1) {
        atObject(Door[1], Door[2]);
        return random(800, 900);
      }
    } else if (getX() >= 542 && getX() <= 548 && getY() >= 576 && getY() <= 580) {
      // si  la teleport house
      int[] Door = getWallObjectById(2);
      if (Door[0] != -1) {
        atWallObject(Door[1], Door[2]);
        return random(800, 900);
      }
    } else if (getX() >= 552 && getX() <= 556 && getY() >= 568 && getY() <= 572) {
      // si  la bed house
      int[] Door = getWallObjectById(2);
      if (Door[0] != -1) {
        atWallObject(Door[1], Door[2]);
        return random(800, 900);
      }
    } else if (getY() >= 608) {
      // si proche banque on ouvre la porte de la banque
      int[] Door = getObjectById(64); // BankDoor
      if (Door[0] != -1) {
        atObject(Door[1], Door[2]);
        return random(800, 900);
      }
    } else if (getX() >= 553 && getX() <= 558 && getY() >= 599 && getY() <= 606) {
      // si proche banque on ouvre la porte de la house
      int[] Door = getWallObjectById(2);
      if (Door[0] != -1) {
        atWallObject(Door[1], Door[2]);
        return random(800, 900);
      }
    }

    if (getInventoryCount(foodIDs) == 0
        || (getInventoryCount() == MAX_INVENTORY_SIZE && isDoingHero)) {
      int banker[] = getNpcByIdNotTalk(BANKERS);
      if (banker[0] != -1) {
        talkToNpc(banker[0]);
        return 3000;
      } else if (getX() == 549 && getY() == 596) {
        // si au "WalkingCenter" on walk "WalkingNearBank"
        walkTo(548, 608); // WalkingNearBank
      } else {
        walkTo(549, 596); // WalkingCenter
      }
    }
    if (getHpPercent() < 70) {
      int idx = getInventoryIndex(foodIDs);
      if (idx == -1) {
        if (getHpPercent() < 30) {
          System.out.println("hp is dangerously low with no food.");
          walkTo(549, 596);
          return 1000;
        } else {
          return random(60000, 90000);
        }
      }
      useItem(idx);
      return random(1500, 1600);
    }
    int[] npc = getAllNpcById(npcID);
    if (npc[0] != -1) {
      thieveNpc(npc[0]);
      return random(500, 1000);
    } else {
      walkTo(549, 596); // WalkingCenter
    }
    return 500;
  }
}
