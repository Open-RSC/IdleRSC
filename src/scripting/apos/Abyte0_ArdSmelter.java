package scripting.apos;

public class Abyte0_ArdSmelter extends Abyte0_Script {
  int[] mix;
  // int[] barMith;
  // int[] barAddy;
  // int[] barRune;

  int oreTin = 202;
  int oreCopper = 150;
  int oreIron = 151; // Iron ore
  int oreMith = 153; // Mith ore
  int oreAddy = 154; // Addy ore
  int oreRune = 409; // Rune ore
  int oreCoal = 155; // Coal ore
  int oreGold = 152;

  int gnomeBall = 981;

  // int barSteel = 171;//Steel bars

  int banker = 95; // Banker

  String barType;

  int[] doorObj;

  public Abyte0_ArdSmelter(String e) {
    super(e);
  }

  public void init(String params) {
    print("Abyte0 : Ardougne Smelter...");
    // print("No param = Steel Bars...");
    print("Version 2.1 - Added Bronze and Gold");

    if (params.equals("b")) mix = new int[] {169, 14, oreTin, 14, oreCopper, 14};
    else if (params.equals("i")) mix = new int[] {170, -1, oreIron, 29, oreCoal, 0};
    else if (params.equals("s")) mix = new int[] {171, 9, oreIron, 9, oreCoal, 18};
    else if (params.equals("g")) mix = new int[] {172, -1, oreGold, 29, oreCoal, 0};
    else if (params.equals("m")) mix = new int[] {173, 5, oreMith, 5, oreCoal, 20};
    else if (params.equals("a")) mix = new int[] {174, 4, oreAddy, 4, oreCoal, 24};
    else if (params.equals("r")) mix = new int[] {408, 3, oreRune, 3, oreCoal, 24};
    else {
      print(
          "Sorry Param B = bronze, I = Iron, S = steel, G = Gold, M = mith , A = addy, R = runite");
      stopScript();
    }

    barType = params;

    // Version 0
    // Smelt Steel bars
    // fix  0.1 : Door opening when only door 1 is close
    // fix  0.2 : When stuck in bank

    // if(params.equals(""))
    //	cptInventaireToBank = 30;
    // else
    //	cptInventaireToBank = 60;
  }

  public int main() {
    return Mine();
  }

  public int Mine() {
    if (getFightMode() != 2) {
      setFightMode(2);
    }
    if (getFatigue() > 70) {
      useSleepingBag();
      return 1000;
    }
    if (isBanking()) {
      if (getInventoryCount(gnomeBall) > 0) {
        print("We got trolled by " + getInventoryCount(gnomeBall) + "Gnome Ball! Banked!");
        deposit(gnomeBall, getInventoryCount(gnomeBall));
        return random(1111, 1500);
      } else if (getInventoryCount(mix[0]) > 0) {
        deposit(mix[0], getInventoryCount(mix[0]));
        return 1000 + random(10, 500);
      } else if (getInventoryCount(mix[2]) == 0) {
        withdraw(mix[2], mix[3]);
        return 1000 + random(10, 500);
      } else if (getInventoryCount(mix[4]) == 0 && mix[0] != 170) {
        withdraw(mix[4], mix[5]);
        // withdraw(oreCoal,8);//18 only take out 10 so i try 10 then 8 after...
        return 1000 + random(10, 500);
      } else if (getInventoryCount(mix[2]) == mix[3]
          && getInventoryCount(mix[4]) == mix[5]
          && getInventoryCount(mix[0]) == 0) closeBank();
      else {
        // Si les quantit� sont buguer on depose tout et on recommence
        deposit(mix[2], getInventoryCount(mix[2])); // ore 1
        deposit(mix[4], getInventoryCount(mix[4])); // ore 2
        deposit(mix[0], getInventoryCount(mix[0])); // bars
        return 1000 + random(5, 500);
      }
    }
    if (isQuestMenu()) {
      answer(0);
      return 1000 + random(300, 1200);
    }
    if (getX() >= 577 && getX() <= 585 && getY() >= 572 && getY() <= 576) {
      // print("In Bank");
      // Si dans la banque Lign�
      if (getInventoryCount(mix[2]) != mix[3] || getInventoryCount(mix[4]) != mix[5]) {
        print("Talking to Banker");
        if (!isBanking()) {
          int banker[] = getNpcByIdNotTalk(new int[] {95});
          if (banker[0] != -1 && !isBanking()) {
            print("Hello you Banker!");
            talkToNpc(banker[0]);
            return random(2000, 3000);
          } else print("No banker!");
        } else {
          print("I don't feel like talking to banker!");
        }
      }
    }
    if (getInventoryCount(mix[2]) == 0 || (getInventoryCount(mix[4]) == 0 && mix[0] != 170)) {
      // si a coter de la porte a l'interieur on veut sortir
      // if(getX() == 589 && getY() == 591)
      // {
      // print("Force Open");
      // doorObj = getWallObjectById(2);
      // if(doorObj[0] != -1)
      // {
      //	if (isAtApproxCoords(doorObj[1], doorObj[2], 5));
      //		atWallObject(doorObj[1], doorObj[2]);
      // }
      // atWallObject(589,591);
      // }
      // si a coter de la porte a l'interieur on veut sortir
      if (getX() == 589 && getY() == 591) {
        print("Step OUTSIDE House");
        doorObj = getWallObjectById(2);
        if (doorObj[0] != -1) {
          if (isAtApproxCoords(doorObj[1], doorObj[2], 5)) {
            atWallObject(doorObj[1], doorObj[2]);
            print("Door id 2 = position : " + doorObj[1] + "," + doorObj[2]);
            return random(6000, 12000);
          } else {
            walkTo(588, 591);
            // walk inside
            return random(500, 1000);
          }
        } else {
          walkTo(588, 591);
          // walk inside
          return random(500, 1000);
        }
        // atWallObject(589,591);
      }

      if (getX() >= 589 && getX() <= 592 && getY() >= 589 && getY() <= 594) {
        // Si dans la room
        walkTo(589, 591);
        return random(240, 2500);
      }
      if (getX() == 588 && getY() == 591) {
        walkTo(586, 581);
        return random(100, 1500);
      }
      if (getY() > 576) {
        // Si avant banque on veut entrer....

        walkTo(581, 573);
        return 1000 + random(300, 1200);
      }

      walkTo(581, 573); // Bank
      return 1000 + random(300, 1200);
    } else {
      if (getX() == 588 && getY() == 591) {
        print("Step INSIDE House");
        doorObj = getWallObjectById(2);
        if (doorObj[0] != -1) {
          if (isAtApproxCoords(doorObj[1], doorObj[2], 5)) {
            atWallObject(doorObj[1], doorObj[2]);
            print("Door id 2 = position : " + doorObj[1] + "," + doorObj[2]);
            return random(6000, 12000);
          } else {
            walkTo(589, 591);
            return random(500, 2000);
            // walk outside
          }
        } else {
          walkTo(589, 591);
          return random(500, 2000);
          // walk outside
        }
      }
      // if(getX() == 588 && getY() == 591)
      // {
      //	print("Force Open");
      // doorObj = getWallObjectById(2);
      // if(doorObj[0] != -1)
      // {
      //	if (isAtApproxCoords(doorObj[1], doorObj[2], 5));
      //		atWallObject(doorObj[1], doorObj[2]);
      // }
      //	atWallObject(589,591);
      // }
      if (getX() >= 577 && getX() <= 585 && getY() >= 572 && getY() <= 576) {
        // Si dans la banque Lign�
        walkTo(586, 581);
        return random(240, 2500);
      }
      if (getY() < 581) {
        // On marche vers la petite porte des nats
        walkTo(586, 581);
        return 1000 + random(300, 1200);
      }
      // if(getX() == 588 && getY() == 591)
      // {
      //	print("Step Inside House");
      // doorObj = getWallObjectById(2);
      // if(doorObj[0] != -1)
      // {
      //	if (isAtApproxCoords(doorObj[1], doorObj[2], 5));
      //		atWallObject(doorObj[1], doorObj[2]);
      // }
      // print("Wall door id before opening"+getWallObjectIdFromCoords(589,591));
      //	atWallObject(589,591);
      //	walkTo(589,591);
      // print("Wall door id after opening"+getWallObjectIdFromCoords(589,591));
      //	return random(2000, 3500);
      // }
      if (getX() >= 589 && getX() <= 592 && getY() >= 589 && getY() <= 594) {
        // Si dans la room
        useItemOnObject(mix[2], 118);
        return random(640, 1809);
      }
      // SINON On marche jusqua la porte du smelting room
      walkTo(588, 591);
      return random(400, 1103);
    }
  }
}
