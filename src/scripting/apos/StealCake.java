package scripting.apos;

import compatibility.apos.Script;

public class StealCake extends Script {
  int fmode = 3;
  int stallID = 322;
  int[] bakeStart1 = new int[] {544, 601};
  int[] bakeStart2 = new int[] {543, 600};
  int[] bankArea = new int[] {551, 612};
  boolean power = true;

  public StealCake(String e) {
    //        super(e);
  }

  public void init(String params) {
    if (params.equals("bank")) power = false;
  }

  public int main() {
    if (getFightMode() != fmode) setFightMode(fmode);

    if (getFatigue() > 90) {
      useSleepingBag();
      return 1000;
    }

    if (inCombat()) {
      if (getY() == bakeStart2[1]) walkTo(bakeStart1[0], bakeStart1[1]);
      else walkTo(bakeStart2[0], bakeStart2[1]);
      return random(500, 600);
    }

    if (getHpPercent() < 80) {
      int idx = getInventoryIndex(335);
      if (idx == -1) idx = getInventoryIndex(333);
      if (idx == -1) idx = getInventoryIndex(330);
      if (idx == -1) {
        System.out.println("hp is dangerously low with no food.");
        stopScript();
        return 0;
      }
      useItem(idx);
      return random(500, 600);
    }

    if (isBanking()) {
      if (getInventoryCount() != 30) {
        closeBank();
        return random(500, 600);
      }
      deposit(330, getInventoryCount(330) - 2);
      return random(500, 600);
    }

    if (isQuestMenu()) {
      answer(0);
      return random(500, 600);
    }

    if (power || (getInventoryCount() < 30 && !power)) {
      if (getX() != 543 && getX() != 600) {
        walkTo(543, 600);
        return 2000;
      }
      int baker[] = getNpcByIdNotTalk(325);
      if (baker[0] != -1) {
        int[] stall = getObjectById(stallID);
        if (stall[0] != -1) {
          atObject2(stall[1], stall[2]);
          return 7000 + random(500, 1000);
        }
      } else {
        return 3000 + random(500, 750);
      }
    }
    if (!power) {
      if (distanceTo(bankArea[0], bankArea[1]) < 10) {
        int banker[] = getNpcByIdNotTalk(95);
        if (banker[0] != -1) talkToNpc(banker[0]);
        return random(1222, 3000);
      } else walkTo(bankArea[0], bankArea[1]);
    }
    return random(500, 1000);
  }
}
