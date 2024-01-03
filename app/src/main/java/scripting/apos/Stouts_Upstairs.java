package scripting.apos;

import compatibility.apos.Script;

/**
 * This script is used to help you gain attack levels with out
 *
 * <p>gaining any hitpoints exp.
 *
 * <p>if you run the script with the parameter "buy"
 *
 * <p>make sure that you are near falador west bank
 *
 * <p>have money in your inventory and thats it
 *
 * <p>if you run the script with the parameter "train"
 *
 * <p>make sure that you are near varrock east bank
 *
 * <p>if you want to use wine instead of beer to train
 *
 * <p>change the variables below.
 *
 * <p>v 1.5
 *
 * <p>- yomama`
 */
public class Stouts_Upstairs extends Script {

  ///// change these if you want to TRAIN/BUY with different beer/wine

  /*

  267: Asgarnian Ale

  268: Wizard's Mind Bomb

  269: Dwarven Stout

  142: wine

  */

  final int drinkID = 267;

  final int glassID = 620;

  /////

  boolean train = true;

  final boolean upstairs = true;

  int[] bankArea = new int[2];

  int[] actArea = new int[2];

  long talk = System.currentTimeMillis();

  public Stouts_Upstairs(String e) {

    // super(e);

  }

  public void init(String params) {

    params = params.trim().toLowerCase();

    if (params.equals("buy")) {

      train = false;

      bankArea = new int[] {329, 552};

      actArea = new int[] {322, 547};

    } else if (params.equals("train")) {

      train = true;

      bankArea = new int[] {103, 511};

      actArea = new int[] {105, 501};

    } else {

      System.out.println("Invalid input, script ended (params are \"buy\" and \"train\")");

      stopScript();
    }
  }

  public int main() {

    if (train) return doTraining();

    return doBuying();
  }

  public int doBuying() {

    if (getInventoryCount(10) < 3) {

      System.out.println("out of gold. script ending...");

      stopScript();
    }

    if (!isQuestMenu() && System.currentTimeMillis() - talk < 3000) {

      return 100;
    }

    if (isQuestMenu()) {

      String[] options = questMenuOptions();

      for (int i = 0; i < options.length; i++) {

        if (options[i].indexOf("Asgarn") > 0) {

          answer(drinkID - 267);

          return 500;

        } else if (options[i].indexOf("bank") > 0) {

          answer(i);

          return 500;
        }

        talk = System.currentTimeMillis();
      }

      return 1000;
    }

    if (isBanking()) {

      if (getInventoryCount(drinkID) == 0) {

        closeBank();

        return 1000;
      }

      deposit(drinkID, getInventoryCount(drinkID));

      return 1000;
    }

    if (getInventoryCount() == 30) {

      int[] closedDoor = getWallObjectById(2);

      if (closedDoor[0] != -1
          && closedDoor[1] > 322
          && distanceTo(closedDoor[1], closedDoor[2]) < 10) {

        atWallObject(closedDoor[1], closedDoor[2]);

        return random(400, 500);
      }

      if (getY() > 1000) {

        int[] stairs = getObjectById(42);

        if (stairs[0] != -1) atObject(stairs[1], stairs[2]);

        return 1000;
      }

      if (distanceTo(bankArea[0], bankArea[1]) < 10) {

        int[] banker = getNpcByIdNotTalk(95);

        if (banker[0] != -1) {

          talkToNpc(banker[0]);

          talk = System.currentTimeMillis();
        }

        return 1000;
      }

      walkTo(bankArea[0], bankArea[1]);

      return 1000;
    }

    if (upstairs) {

      actArea = new int[] {320, 1490};

      if (getY() < 1000) {

        int[] stairs = getObjectById(41);

        if (stairs[0] != -1) atObject(stairs[1], stairs[2]);

        return 1000;
      }
    }

    if (distanceTo(actArea[0], actArea[1]) < 10) {

      int[] barmaid = getNpcByIdNotTalk(142);

      if (barmaid[0] != -1) {

        talkToNpc(barmaid[0]);

        talk = System.currentTimeMillis();
      } // else

      // upstairs = true;

      return 1000;
    }

    walkTo(actArea[0], actArea[1]);

    return 1000;
  }

  public int doTraining() {

    if (getFatigue() > 90) {

      useSleepingBag();

      return 1000;
    }

    if (getY() >= 509) {

      int[] bankDoors = getObjectById(64);

      if (bankDoors[0] != -1 && bankDoors[1] == 102 && bankDoors[2] == 509) {

        atObject(bankDoors[1], bankDoors[2]);

        return random(500, 600);
      }
    }

    if (getY() >= 506) {

      int[] closedDoor = getWallObjectById(2);

      if (closedDoor[0] != -1 && closedDoor[1] == 104 && closedDoor[2] == 506) {

        atWallObject(closedDoor[1], closedDoor[2]);

        return random(500, 600);
      }
    }

    if (getInventoryCount(glassID) > 0) {

      int glassIDx = getInventoryIndex(glassID);

      if (glassIDx != -1) dropItem(glassIDx);

      return 1000;
    }

    if (isBanking()) {

      if (getInventoryCount() == 30) {

        closeBank();

        return 1000;
      }

      withdraw(drinkID, 30 - getInventoryCount());

      return 1000;
    }

    if (isQuestMenu()) {

      answer(0);

      return 1000;
    }

    if (getInventoryCount(drinkID) == 0) {

      int[] closedDoor = getWallObjectById(2);

      if (closedDoor[0] != -1 && closedDoor[1] == 104 && closedDoor[2] == 506) {

        atWallObject(closedDoor[1], closedDoor[2]);

        return random(500, 600);
      }

      int[] bankDoors = getObjectById(64);

      if (bankDoors[0] != -1 && bankDoors[1] == 102 && bankDoors[2] == 509) {

        atObject(bankDoors[1], bankDoors[2]);

        return random(500, 600);
      }

      if (distanceTo(bankArea[0], bankArea[1]) < 10) {

        int[] banker = getNpcByIdNotTalk(95);

        if (banker[0] != -1) talkToNpc(banker[0]);

        return 1000;
      }

      walkTo(bankArea[0], bankArea[1]);

      return 1000;
    }

    if (getCurrentLevel(0) > 7) {

      int drinkIDx = getInventoryIndex(drinkID);

      if (drinkIDx == -1) {

        return 1000;
      }

      useItem(drinkIDx);

      return 1000;
    }

    if (distanceTo(actArea[0], actArea[1]) < 10) {

      int[] dummy = getObjectById(49);

      if (dummy[0] != -1) {

        atObject(dummy[1], dummy[2]);
      }

      return 180;
    }

    walkTo(actArea[0], actArea[1]);

    return random(500, 1000);
  }
}
