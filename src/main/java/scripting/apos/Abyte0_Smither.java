package scripting.apos;

/** Based on old AnySmither Seer Smither by Abyte0 */
public final class Abyte0_Smither extends Abyte0_Script {
  int barID = 171;
  int nbBars = 25;
  int nbBarsParItems = 5;

  final int[] itemToDeposit =
      new int[] {
        63, 67, 72, 78, 84, 88, 90, 95, 105, 109, 114, 118, 121, 125, 129, 225, 309, 1024, 1064,
        1077
      };

  int reponse1 = 1, reponse2 = 2, reponse3 = 1, nbReponses = 3;

  final int fmode = 2;

  int cptReponseActuel = 0;

  private Abyte0_Smither(String e) {
    super(e);
  }

  public void init(String params) {
    if (!params.equals("")) {
      String[] p = params.trim().split(",");

      barID = Integer.parseInt(p[0]);
      nbBars = Integer.parseInt(p[1]);
      nbBarsParItems = Integer.parseInt(p[2]);
      reponse1 = Integer.parseInt(p[3]);
      reponse2 = Integer.parseInt(p[4]);
      reponse3 = Integer.parseInt(p[5]);
      nbReponses = Integer.parseInt(p[6]);
    }

    print("Seer Village Smither by Abyte0");
    print("Version 0 r5 less wait");
    print(
        "Default = Steel Pl8s || abyte0_smither barID,nbBars,nbBarsParItems,reponse1,reponse2,reponse3,nbReponses");
    print("Param = " + params);
  }

  public int main() {
    if (getFightMode() != fmode) {
      setFightMode(fmode);
      return 500;
    }
    if (getFatigue() > 85) {
      useSleepingBag();
      return random(800, 1000);
    }
    if (isBanking()) {
      for (int i = 0; i < 20; i++) {
        if (getInventoryCount(itemToDeposit[i]) > 0) {
          deposit(itemToDeposit[i], getInventoryCount(itemToDeposit[i]));
          return random(1000, 1500);
        }
      }
      if (getInventoryCount(barID) < nbBars) {
        withdraw(barID, nbBars - getInventoryCount(barID));
        return random(1000, 1500);
      }
      closeBank();
      return random(1000, 1500);
    }
    if (isQuestMenu()) {
      if (getX() >= 498 && getX() <= 504 && getY() >= 447 && getY() <= 453) {
        // si dans la banque
        answer(0);
        return random(1000, 1500);
      } else {
        switch (cptReponseActuel) {
          case 0:
            answer(reponse1);
            break;
          case 1:
            answer(reponse2);
            break;
          case 2:
            answer(reponse3);
            break;
        }
        cptReponseActuel++;
        return random(1900, 2200);
      }
    }
    if (getX() == 514 && getY() == 455 && getInventoryCount(barID) >= nbBars) {
      // si devant porte on ouvre et on entre dans la house
      int[] doorObj = getWallObjectById(2);
      if (doorObj[0] != -1) {
        if (isAtApproxCoords(doorObj[1], doorObj[2], 5)) ;
        atWallObject(doorObj[1], doorObj[2]);
      }
      // atWallObject(514,454);
      walkTo(514, 454);
      return random(400, 2500);
    }
    if (getX() == 514 && getY() == 454 && getInventoryCount(barID) < nbBars) {
      // si devant porte on ouvre et on sort de la house
      int[] doorObj = getWallObjectById(2);
      if (doorObj[0] != -1) {
        if (isAtApproxCoords(doorObj[1], doorObj[2], 5)) ;
        atWallObject(doorObj[1], doorObj[2]);
      }
      // atWallObject(514,454);
      walkTo(514, 455);
      return random(400, 2500);
    }
    if (getX() >= 510 && getX() <= 515 && getY() >= 451 && getY() <= 454) {
      // Si dans la house
      // print("Walking to Door");
      if (getInventoryCount(barID) >= nbBarsParItems && !isQuestMenu()) {
        useItemOnObject(barID, 50);
        cptReponseActuel = 0;
        return random(1000, 1500);
      } else {
        // si pu asser de bars on walk a la porte
        walkTo(514, 454);
        return random(240, 2500);
      }
    }
    if (getInventoryCount(barID) >= nbBars) {
      if (getX() == 500 && getY() == 453) {
        // si devant porte on ouvre et on sort de la banque
        atObject(500, 454);
        walkTo(500, 454);
        return random(400, 2500);
      }
      if (getX() >= 498 && getX() <= 504 && getY() >= 447 && getY() <= 453) {
        // Si dans la banque
        // print("Walking to Door");
        walkTo(500, 453);
        return random(240, 2500);
      }

      walkTo(514, 455);
    } else {
      // sinon si pas beaucoup de bars
      if (getX() == 500 && getY() == 454) {
        // si devant porte on ouvre et on entre dans la banque
        atObject(500, 454);
        walkTo(500, 453);
        return random(400, 2500);
      }
      if (getX() >= 498 && getX() <= 504 && getY() >= 447 && getY() <= 453) {
        // Si dans la banque
        int[] banker = getNpcByIdNotTalk(95);
        if (banker[0] != -1 && !isBanking()) talkToNpc(banker[0]);
        else print("No banker!");
        return random(240, 2500);
      }

      walkTo(500, 454);
    }
    return random(800, 1000);
  }
}
