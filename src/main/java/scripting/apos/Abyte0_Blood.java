package scripting.apos;

/** opens blood chest below ardy chaos druid tower */
public final class Abyte0_Blood extends Abyte0_Script {
  // Version 3.1 2021-08-17 Support Cake as 330

  int fmode = 2;
  final int coinsId = 10;
  final int bloodId = 619;
  int foodId = 373;
  int minFoodToBankAt = 3;
  int SafeOrNot = 0; // 0 oui 1 non
  int BuryOrNot = 0; // 0 oui 1 non

  int minWait = 30000;
  int maxWait = 90000;

  private Abyte0_Blood(String e) {
    super(e);
  }

  public void init(String params) {
    String[] str = params.split(",");

    if (str.length >= 5) {
      fmode = Integer.parseInt(str[0]);
      foodId = Integer.parseInt(str[1]);
      minFoodToBankAt = Integer.parseInt(str[2]);
      SafeOrNot = Integer.parseInt(str[3]);
      BuryOrNot = Integer.parseInt(str[4]);
      if (str.length == 7) {
        minWait = Integer.parseInt(str[5]);
        maxWait = Integer.parseInt(str[6]);
      }
    } else {
      print(
          "You should use: Abyte0_Blood Fmode,FoodId,minFoodToBankAt,SafeOrNot(0= save food 1 = not),BuryOrNot(0= yes 1 = not) (optional ,minWait,maxWait");
      print(
          "You are Using Default: Attack Mode, Lobs, Run To Bank At 2 Lobs Left, Walk to center even if no chest ready");
    }

    // if(str[1].equals("2")

    print("Blood Runes Banker ~By Abyte0");
    print("Version 3.1 Now support cake");
    print("-");
    print("Fmode = " + fmode);
    print("foodId = " + foodId);
    print("minFoodToBankAt = " + minFoodToBankAt);
    if (SafeOrNot == 0) print("Saving Food By Using Wait Wall");
    else print("Just use more food but faster maybe");
    if (BuryOrNot == 0) print("Bury Big Bones");
    else print("Fk Prayer");
    print("minWait = " + minWait);
    print("maxWait = " + maxWait);
  }

  public int main() {

    if (getFightMode() != fmode) setFightMode(fmode);

    if (isAtApproxCoords(615, 3396, 25)) {
      if (getInventoryCount(foodId) <= minFoodToBankAt || getFatigue() > 70) {
        print("Running");
        if (inCombat()) {
          RunFromCombat();
          return random(200, 400);
        }
        if (getHpPercent() < 60 && IsStillHavingFood(foodId)) {
          EatFood(foodId);
          return random(2400, 3000);
        }
        if (getY() > 3388) {
          RunNorthOrAttackNorth();
          return random(500, 1104);
        }
        if (getX() >= 617 && getX() <= 619 && getY() >= 3382 && getY() <= 3388) {
          if (getFatigue() > 60) {
            useSleepingBag();
            return 3000;
          } else {
            // on monte lechelle
            int[] stairs = getObjectById(5);
            if (stairs[0] != -1) {
              atObject(stairs[1], stairs[2]);
              return random(800, 900);
            }
          }
          return random(800, 1300);
        }
        return random(100, 212);
      } else {
        // si asser de food on thieve le chest :D
        // si blblabla  614 3398      614 3402
        int[] bloodChest = getObjectById(337);
        if (getHpPercent() < 60) {
          if (inCombat()) {
            RunFromCombat();
            return random(99, 222);
          }
          EatFood(foodId);
          return random(2400, 3000);
        } else if (bloodChest[0] != -1) {
          if (inCombat()) {
            RunFromCombat();
            random(250, 300);
          }
          atObject2(bloodChest[1], bloodChest[2]);
          return random(250, 589);
        } else if (getObjectIdFromCoords(614, 3399) == 340
            && getObjectIdFromCoords(614, 3401) == 340) {
          // si position 1 = 340 = lotted et si position 2 = 340 = lotted
          // Run Safe or Run Center
          if (SafeOrNot == 0) {
            if (!(getX() == 618 && getY() == 3398)) walkTo(618, 3398); // wall
            else {
              int nombre = TryBury();
              if (nombre != -1) return nombre;
            }
          } else {
            if (!(getX() == 614 && getY() == 3400)) walkTo(614, 3400); // centre chest
            else {
              int nombre = TryBury();
              if (nombre != -1) return nombre;
            }
          }
          return random(300, 654);
        } else {
          if (inCombat()) {
            RunFromCombat();
            return 200;
          }
          if (random(1, 2) == 1) atObject2(614, 3399);
          else atObject2(614, 3401);
          return random(300, 650);
        }
      }
    }
    if (isBanking()) {
      if (getInventoryCount(coinsId) > 1) {
        deposit(coinsId, getInventoryCount(coinsId) - 1);
        return random(1000, 1500);
      }
      if (getInventoryCount(coinsId) < 1) {
        withdraw(coinsId, 1);
        return random(1000, 1500);
      }
      if (getInventoryCount(bloodId) > 1) {
        deposit(bloodId, getInventoryCount(bloodId) - 1);
        return random(1000, 1500);
      }
      if (getInventoryCount(bloodId) < 1) {
        withdraw(bloodId, 1);
      }
      if (getInventoryCount() < 28) {
        withdraw(foodId, 30 - getInventoryCount());
        return random(1000, 1500);
      }
      if (getInventoryCount() > 28
          && getInventoryCount(bloodId) < 5
          && getInventoryCount(coinsId) < 1000) closeBank();
    }
    if (getX() == 612 && getY() == 572) {
      // Si a  coter d ela porte de la teleported house
      print("Step Outside House");
      int[] doorObj = getWallObjectById(2);
      if (doorObj[0] != -1) {
        if (isAtApproxCoords(doorObj[1], doorObj[2], 5)) ;
        atWallObject(doorObj[1], doorObj[2]);
      }
      walkTo(612, 573);

      if (getHpPercent() < 70) {
        EatFood(foodId);
        return random(2400, 3000);
      }

      return random(100, 1500);
    }
    if (getX() >= 611 && getX() <= 617 && getY() >= 568 && getY() <= 572) {
      // Si dans la teleported room
      walkTo(612, 572);
      if (getFatigue() > 6) {
        useSleepingBag();
        print("Waitting....");
        return random(minWait, maxWait);
      }
      return random(14000, 30003); // 618,551
    }
    if (isAtApproxCoords(581, 574, 5)) {
      if (getInventoryCount(335)
          > 0) // We do not want any residue of cake in inventory for next batch
      {
        EatFood(335);
        return 2000;
      }
      if (getInventoryCount(333)
          > 0) // We do not want any residue of cake in inventory for next batch
      {
        EatFood(333);
        return 2000;
      }
      if (getInventoryCount() < 28
          || getInventoryCount(bloodId) > 5
          || getInventoryCount(coinsId) > 1000) {
        if (!isQuestMenu()) {
          int[] banker = getNpcByIdNotTalk(95);
          if (banker[0] != -1) {
            talkToNpc(banker[0]);
            return random(2000, 2700);
          }
          return 1000;
        } else {
          answer(0);
          return 6000;
        }
      }
    }
    if (isAtApproxCoords(617, 558, 2) && getInventoryCount(foodId) > minFoodToBankAt) {
      int[] door = getWallObjectById(96);
      if (door[0] != -1) atWallObject2(door[1], door[2]);
      return random(1000, 1500);
    }
    if (isAtApproxCoords(617, 552, 3) && getInventoryCount(foodId) <= minFoodToBankAt) {
      int[] door = getWallObjectById(96);
      if (door[0] != -1) {
        atWallObject(door[1], door[2]);
        return random(800, 1200);
      }
      return 1000;
    }
    if (isAtApproxCoords(617, 552, 3) && getInventoryCount(foodId) > minFoodToBankAt) {
      int[] stairs = getObjectById(6);
      if (stairs[0] != -1) {
        atObject(stairs[1], stairs[2]);
        return random(800, 900);
      }
      return random(500, 800);
    }
    if (getX() == 618 && getY() == 3382) {
      // on monte lechelle car on est bloquer du mauvais coter
      int[] stairs = getObjectById(5);
      if (stairs[0] != -1) {
        atObject(stairs[1], stairs[2]);
        return random(800, 900);
      }
      return random(500, 800);
    }
    walk();
    return random(2000, 2500);
  }

  private int walk() {
    if (getInventoryCount(foodId) <= minFoodToBankAt) {
      // we need to bank
      // 617, 558, 609, 565, 603, 582, 600, 595, 588, 598, 590, 578, 581, 574
      if (getX() > 600) {
        if (getY() < 558) walkTo(617, 558);
        else if (getY() < 565) walkTo(609, 565);
        else if (getY() < 582) walkTo(603, 582);
        else if (getY() < 595) walkTo(600, 595);
        else walkTo(597, 602);
      } else {
        if (getX() > 588 && getY() > 580) walkTo(588, 598);
        else if (getY() > 578) walkTo(590, 578);
        else if (getY() > 574) walkTo(581, 574);
      }
    } else {
      // enoug food we go war
      // 581, 574, 590, 578, 588, 598, 600, 595, 603, 582, 609, 565, 617, 558
      if (getX() < 600) {
        // if(getY() < 574)
        //	walkTo(581,574);
        if (getY() < 578) walkTo(590, 578);
        else if (getY() < 598) walkTo(588, 598);
        // else if(getY() < 602)
        // walkTo(597,602);
        else walkTo(602, 594);

      } else {
        if (getY() > 594) walkTo(602, 594);
        else if (getY() > 582) walkTo(603, 582);
        else if (getY() > 565) walkTo(609, 565);
        else if (getY() > 558) walkTo(617, 558);
      }
    }
    return 1000;
  }

  private void BuryBigBone() {
    int boneIndex = getInventoryIndex(413);
    print("Bury Bones at position : " + boneIndex);
    useItem(boneIndex);
  }

  private int TryBury() {
    if (BuryOrNot == 0) {
      if (getInventoryCount(413) > 0) {
        if (inCombat()) {
          RunFromCombat();
          return random(100, 200);
        }
        BuryBigBone();
        return random(200, 300);
      }
      int[] bigBone = getItemById(413);
      if (bigBone[0] != -1) {
        if (bigBone[1] == getX() && bigBone[2] == getY()) {
          if (inCombat()) {
            RunFromCombat();
            return random(100, 200);
          }

          pickupItem(bigBone[0], bigBone[1], bigBone[2]);
          return random(200, 300);
        }
      }
    }
    return -1;
  }

  private void RunFromCombat() {
    int x = getX();
    int y = getY();
    walkTo(x, y);
  }

  private void RunNorthOrAttackNorth() {
    int[] npcs = getNpcInRadius(312, getX(), getY() - 1, 0); // ,525,706);
    if (npcs[0] != -1) attackNpc(npcs[0]);
    else {
      if (getObjectIdFromCoords(getX(), getY() - 1) == -1) walkTo(617, getY() - 1);
      else walkTo(618, getY() - 1);
    }
  }
}
