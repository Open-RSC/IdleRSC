package scripting.apos;

import compatibility.apos.Script;

public final class Abyte0_SwampMiner extends Script {
  final int oreMith = 153; // Mith ore
  final int oreAddy = 154; // Addy ore
  final int oreCoal = 155; // Coal ore

  final int RockCoal = 110; // Coal rock
  final int RockMith = 106; // Mith rock 	//est id=107 au guild
  final int RockAddy = 108; // Addy rock 	//pas 109 a la swamp entk

  final int gem1 = 160; // sapph
  final int gem2 = 159; // emerald
  final int gem3 = 158; // ruby
  final int gem4 = 157; // diamond

  int cptMithBanked;
  int cptAddyBanked;
  int cptCoalDrop;

  int banker = 95; // Banker

  boolean bankCoal;

  final int cptInventaireToBank = 30;

  public Abyte0_SwampMiner(String e) {
    // super(e);
  }

  public void init(String params) {
    cptMithBanked = 0;
    cptAddyBanked = 0;
    cptCoalDrop = 0;

    print("Abyte0 : Swamp Miner...");
    print(
        "No param = Bank Ruby, Diamon, Mith ores, Addy Ores and power mine the coal when waiting...");
    print("Version 0 r7");

    // print("addy rock id = "+getObjectIdFromCoords(111,699));
    // print("mith rock1 id = "+getObjectIdFromCoords(110,697));
    // print("mith rock2 id = "+getObjectIdFromCoords(110,698));
    // print("mith rock3 id = "+getObjectIdFromCoords(110,704));
    // print("mith rock4 id = "+getObjectIdFromCoords(110,705));

    if (params.equals("")) {
      bankCoal = false;
      print("Bank Ruby, Diamon, Mith ores, Addy Ores + POWER MINE COAL");
    } else {
      bankCoal = true;
      print("Bank Ruby, Diamon, Coal, Mith ores, Addy Ores");
    }
  }

  public int main() {
    return Mine();
  }

  private int Mine() {
    if (getFightMode() != 2) {
      setFightMode(2);
    }
    if (getFatigue() > 65) {
      useSleepingBag();
      return 1000;
    }
    if (isBanking()) {
      // Deposit gems and ores
      if (getInventoryCount(gem4) > 0) {
        deposit(gem4, 1);
        return 1000;
      }
      if (getInventoryCount(gem3) > 0) {
        deposit(gem3, 1);
        return 1000;
      }
      if (getInventoryCount(gem2) > 0) {
        deposit(gem2, 1);
        return 1000;
      }
      if (getInventoryCount(gem1) > 0) {
        deposit(gem1, 1);
        return 1000;
      }
      if (getInventoryCount(oreCoal) > 0) {
        cptCoalDrop += getInventoryCount(oreCoal);
        deposit(oreCoal, getInventoryCount(oreCoal));
        // print("Banked "+cptCoalDrop+" coal so far");
        return 1000;
      }
      if (getInventoryCount(oreMith) > 0) {
        cptMithBanked += getInventoryCount(oreMith);
        deposit(oreMith, getInventoryCount(oreMith));
        print("Banked " + cptMithBanked + " mith ore so far");
        return 1000;
      }
      if (getInventoryCount(oreAddy) > 0) {
        cptAddyBanked += getInventoryCount(oreAddy);
        deposit(oreAddy, getInventoryCount(oreAddy));
        print("Banked " + cptAddyBanked + " addy ore so far");
        return 1000;
      }
      print("Banked or dropped " + cptCoalDrop + " coal ore");
      closeBank();
      return 1000;
    }
    if (isQuestMenu()) {
      answer(0);
      return random(900, 1600);
    }
    // Open door
    // if(distanceTo(getX(),getY(),287,571) < 5)
    //	if(getObjectById(64).length > 0)
    //		atObject(getObjectById(64)[1],getObjectById(64)[2]);
    // Talk to banker
    // if(distanceTo(286,571) < 6 && getInventoryCount() > 2)
    // {
    //	talkToNpc(getNpcById(banker)[0]);
    //	return 2002 + random(100, 100);
    // }
    if (getInventoryCount() == cptInventaireToBank) {
      if (getInventoryCount(gem1) > 0) // Drop Saphire
      {
        dropItem(getInventoryIndex(gem1));
        return random(1500, 3000);
      }
      if (getInventoryCount(gem2) > 0) // Drop Emeral
      {
        dropItem(getInventoryIndex(gem2));
        return random(1500, 3000);
      }
      if (!bankCoal && getInventoryCount(oreCoal) > 0) {
        dropItem(getInventoryIndex(oreCoal));

        return random(1500, 3000);
      }
      if (getX() < 123) {
        // Walk outside swamp
        walkTo(123, 695);
        return random(789, 1800);
      }
      if (getX() < 130) {
        // Walk near Lake
        walkTo(130, 686);
        return random(789, 1800);
      }
      if (getX() < 141) {
        // Walk Weast Lake
        walkTo(141, 684);
        return random(789, 1800);
      }
      if (getX() < 155) {
        // Walk near Swamp tars
        walkTo(155, 677);
        return random(789, 1800);
      }
      if (getX() < 168) {
        // Walk near wood
        walkTo(168, 675);
        return random(789, 1800);
      }
      if (getX() < 178) {
        // Walk near fire
        walkTo(178, 667);
        return random(789, 1800);
      }
      if (getX() < 191) {
        // Walk near firemaking
        walkTo(191, 661);
        return random(789, 1800);
      }
      if (getX() < 199) {
        // Walk chemin boue
        walkTo(199, 654);
        return random(789, 1800);
      }
      if (getX() < 207) {
        // Walk proche rue
        walkTo(207, 645);
        return random(789, 1800);
      }
      if (getX() < 215) {
        // Walk coin banque
        walkTo(215, 633);
        return random(789, 1833);
      }
      if (getX() >= 216 && getX() <= 223 && getY() >= 634 && getY() <= 638) {
        // Si dans la banque
        print("Talking to Banker");
        if (!isBanking()) {
          int[] banker = getNpcByIdNotTalk(95);
          if (banker[0] != -1 && !isBanking()) {
            talkToNpc(banker[0]);
            return random(2000, 3000);
          }
        }
        return random(231, 1500);
      }
      if (getX() == 220 && getY() == 633) {
        // Si devant la bank door on ouvre et on entre
        atObject(219, 633);
        walkTo(220, 634);
        return random(1100, 1733);
      }

      print("Walking to Bank Door");
      walkTo(220, 633);

      return random(1100, 1733);
    } else {
      if (getX() == 220 && getY() == 634) {
        // Si a coter de la porte a linterieur
        print("Step Outside Bank");
        atObject(219, 633);
        walkTo(220, 633);
        return random(121, 3500);
      }
      if (getX() >= 216 && getX() <= 223 && getY() >= 634 && getY() <= 638) {
        // Si dans la banque on va a la porte
        walkTo(220, 634);
        return random(240, 2500);
      }
      if (getX() > 215) {
        // Walk coin banque
        walkTo(215, 633);
        return random(1100, 1733);
      }
      if (getX() > 207) {
        // Walk proche rue
        walkTo(207, 645);
        return random(1100, 1733);
      }
      if (getX() > 199) {
        // Walk chemin boue
        walkTo(199, 654);
        return random(1100, 1733);
      }
      if (getX() > 191) {
        // Walk near firemaking
        walkTo(191, 661);
        return random(1100, 1733);
      }
      if (getX() > 178) {
        // Walk near fire
        walkTo(178, 667);
        return random(1100, 1733);
      }
      if (getX() > 168) {
        // Walk near wood
        walkTo(168, 675);
        return random(900, 1600);
      }
      if (getX() > 155) {
        // Walk near Swamp tars
        walkTo(155, 677);
        return random(900, 1600);
      }
      if (getX() > 141) {
        // Walk Weast Lake
        walkTo(141, 684);
        return random(900, 1600);
      }
      if (getX() > 130) {
        // Walk near Lake
        walkTo(130, 686);
        return random(900, 1600);
      }
      if (getX() > 123) {
        // Walk outside swamp
        walkTo(123, 695);
        return random(900, 1600);
      }
      if (getX() > 122) {
        // Walk to addy si on est rendu a 123,695
        walkTo(111, 700);
        return random(900, 1600);
      }
      if (distanceTo(111, 700) < 20) {

        int nombreAddy = mineOre(RockAddy);
        if (nombreAddy > 0) return random(1500, 3601);

        // SINON si aucun addy proche on regarde mith
        int nombreMith = mineOre(RockMith);
        if (nombreMith > 0) return random(1300, 2601);

        // SINON si aucun mith proche on regarde coal
        int nombreCoal = mineOre(RockCoal);
        if (nombreCoal > 0) return random(1301, 1605);

        return random(800, 900);
      }
      print("ELSE");
      return random(400, 1103);
    }
  }

  private void print(String gameText) {
    System.out.println(gameText);
  }

  private int mineOre(int id) {
    int[] rock = getObjectById(id);
    if (rock[0] != -1) {
      if (rock[1] >= 108 && rock[1] <= 118 && rock[2] >= 695 && rock[2] <= 706) {
        // Si on est a la swamp mine
        atObject(rock[1], rock[2]);
        // print("Mining " + getObjectIdFromCoords(rock[1], rock[2]));
        return 1;
      }
    }
    return 0;
  }
}
