package scripting.apos;
/** Catherby Lobster Fishing * v1.0 please report bugs if any * XcendroX` */
// Version 0 by XcendroX
// Version 1 by Abyte0
// 2  Ways doors opener
// Lobs OR Tuna/Swordy
// 2021-07-09 V1.2 Updated to OpenRSC + added Shark
// 2021-07-14 V1.3 Fixed walking bug which would happen when map is loaded from reloggin at fishing
// spot
public class Abyte0_CatherbyFisher extends Abyte0_Script {
  int sleepAt = 80;
  int FishType = 372;
  final int FishType2 = 369;
  boolean isDoingShark = false;
  int cptTry = 0;

  private Abyte0_CatherbyFisher(String e) {
    super(e);
  }

  public void init(String params) {
    print("Selected Abyte0 Catherby Fisher");
    print("Version 1.3");
    print("Param can be 'Tunas', 'Sharks' or default 'Lobs'");
    if (params.equals("Shark") || params.equals("Sharks")) {
      FishType = 545;
      isDoingShark = true;
      print("Doing Sharks!");
    } else if (params.equals("Tuna") || params.equals("Tunas")) {
      FishType = 366;
      print("Doing Tunas and Swordy!");
    } else {
      FishType = 372;
      print("Doing Lobs!");
    }
  }

  public int main() {
    if (getFatigue() > 80) {
      useSleepingBag();
      return 1000;
    }
    if (getInventoryCount() == 30) {
      if (isQuestMenu()) {
        answer(0);
        return random(1410, 1987);
      }
      if (isBanking()) {
        if (getInventoryCount(FishType) > 0 || getInventoryCount(FishType2) > 0) {
          if (getInventoryCount(FishType) > 0) deposit(FishType, getInventoryCount(FishType));
          if (getInventoryCount(FishType2) > 0) deposit(FishType2, getInventoryCount(FishType2));
        } else closeBank();
        return random(523, 603);
      }
      if (getX() < 412) {
        walkTo(412, 501);
        return random(430, 1502);
      }
      if (getX() < 423) {
        walkTo(423, 495);
        return random(430, 1502);
      }
      if (getX() < 437) {
        walkTo(439, 497);
        return random(430, 1502);
      }
      if (getX() > 443) {
        walkTo(439, 497);
        return random(430, 1502);
      }
      if (getY() < 491) {
        walkTo(439, 497);
        return random(4030, 8502);
      }
      if (getX() == 439 && getY() == 497) {
        // System.out.println("Open + Step InSide Bank");
        atObject(439, 497);
        walkTo(439, 496);
        // On remet el compteur a 0;
        cptTry = 0;
        return random(100, 1500);
      }
      if (getY() == 497) {
        // si on est perdu sur le coter...
        walkTo(439, 497);
        // On remet el compteur a 0;
        cptTry = 0;
        return random(100, 1500);
      }
      int[] banker = getNpcByIdNotTalk(BANKERS);
      if (banker[0] != -1) {
        talkToNpc(banker[0]);
        return 1000 + random(1423, 1501);
      }
      return random(400, 500);
    } else {
      if (getX() == 439 && getY() == 496) {
        // Si on ets a la porte on louvre et sort
        print("Open + Step OutSide Bank");
        atObject(439, 497);
        walkTo(439, 498);
        return random(100, 1500);
      }
      if (getY() < 497 && getX() > 436) {
        // Si on est dans la banque on va a coter de la porte
        walkTo(439, 496);
        print("Walk to Door");
        return 1000;
      }
      if (getX() == 439 && getY() == 497) {
        print(".wait.");
        // NOTHING waitting to be at 439, 498
      }

      if (isDoingShark) {
        if (getX() != 406 && getY() != 504) {
          print("Walk to Shark");
          // Si on est pas rndu au fishing on Marche
          walkTo(406, 504);
          return 1000;
        }
        if (cptTry++ >= random(80, 130)) {
          // Si on a beaucoup dessaie on bouge pas loguer out
          walkTo(406, 503);
          print("Moving because " + cptTry + " trys...");
          cptTry = 0;
          return random(1003, 4221);
        }
        if (isAtApproxCoords(406, 504, 10) && getInventoryCount() != 30) {
          int[] fish = getObjectById(261);
          if (fish[0] != -1) {
            atObject2(fish[1], fish[2]);
            return random(403, 1721);
          }
        }
      } else // Tunas && Lobs
      {
        if (getX() != 409 && getY() != 503) {
          print("Walk to Fish");
          // Si on est pas rndu au fishing on Marche
          walkTo(409, 503);
          return 1000;
        }
        if (cptTry++ >= random(80, 130)) {
          // Si on a beaucoup dessaie on bouge pas loguer out
          walkTo(409, 502);
          print("Moving because " + cptTry + " trys...");
          cptTry = 0;
          return random(1003, 4221);
        }
        if (isAtApproxCoords(409, 503, 10) && getInventoryCount() != 30) {
          int[] fish = getObjectById(194);
          if (fish[0] != -1) {
            if (FishType == 372) atObject2(fish[1], fish[2]);
            else atObject(fish[1], fish[2]);
            cptTry++;
            return random(403, 1721);
          }
        }
      }
    }
    return random(400, 500);
  }
}
