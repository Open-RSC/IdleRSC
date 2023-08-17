package scripting.apos;

import java.awt.*;
import javax.swing.*;

/** Gather sinister keys by killing salarin */
public final class Abyte0_Sinister extends Abyte0_Script {
  // -- Default Settings --
  int fMode = 3;
  int foodId = 373;
  int spellId = 8;
  int runesCount = 2500;
  int inventoryCount = 18;
  // -----------------------

  final int cptRunesToBankAt =
      100; // zero is too dangerous as you cant hit with 0 str and 0 attack, so you fight to death
  // if
  // stuck

  final int chaosDruids = 270;
  final int salarinId = 567;
  final int[] allNpcIds = {chaosDruids, salarinId};

  final int mindRunes = 35;
  final int chaosRunes = 41;

  final int sinisterKey = 932;
  final int halfKey1 = 526;
  final int halfKey2 = 527;

  final int WhiteBerries = 471;

  final int HalfDragonSquareShield1 = 1276;
  final int HalfDragonSquareShield2 = 1277;

  final int windBolt = 8;
  final int waterBolt = 11;
  final int earthBolt = 14;
  final int fireBolt = 17;

  final int FireRune = 31;
  final int WaterRune = 32;
  final int AirRune = 33;
  final int EarthRune = 34;
  final int deathRunes = 38;
  int nature = 40;
  final int law = 42;
  final int CosmicRune = 46;

  final int superDef1 = 497;

  final int irit = 439;
  final int avantoe = 440;
  final int kwuarm = 441;
  final int dwarfWeed = 443;

  final int vial = 465;

  final String[] itemsName = {"Shield + Keys + Law + Pot", "All Good Items"};
  final int[] items0 = {
    HalfDragonSquareShield1,
    HalfDragonSquareShield2,
    sinisterKey,
    halfKey1,
    halfKey2,
    law,
    superDef1
  };
  final int[] items1 = {
    HalfDragonSquareShield1,
    HalfDragonSquareShield2,
    sinisterKey,
    halfKey1,
    halfKey2,
    law,
    deathRunes,
    CosmicRune,
    FireRune,
    AirRune,
    WhiteBerries,
    EarthRune,
    WaterRune,
    superDef1,
    438,
    irit,
    avantoe,
    kwuarm,
    442,
    dwarfWeed,
    815,
    817,
    819,
    821,
    823,
    933
  };
  final Object[] itemsList = {items0, items1};

  int[] items = items1; // Default Setting

  final String[] fModeName = {"Attack", "Defence", "Strength", "Controlled"};
  final int[] fModeIdList = {2, 3, 1, 0};

  final String[] spellName = {"Wind strike", "Wind bolt"};
  final int[] spellIdList = {0, 8};
  final int camelotTeleport = 22;

  final String[] runesCountName = {"500", "1000", "2500", "5000"};
  final int[] runesCountList = {500, 1000, 2500, 5000};

  final String[] foodName = {"Tunas", "Lobs", "Swordfish", "Sharks"};
  final int[] foodIdList = {367, 373, 370, 546};

  final String[] foodCountName = {"0", "5", "10", "12"};
  final int[] foodCountList = {30, 25, 20, 18};

  final String[] drinkOrNotName = {"Drink", "No Drink"};
  int drinkOrNot = 0; // Default Setting

  public Abyte0_Sinister(String e) {
    super(e);
  }

  public void init(String params) {
    print("--");
    print("Hosted By: Harry");
    print("Hosted By: Harry");
    print("Hosted By: Harry");
    print("Hosted By: Harry");
    print("--");

    if (!params.equals("default")) {

      Frame frame = new Frame("Select Fighting Mode");
      String choiceF =
          (String)
              JOptionPane.showInputDialog(
                  frame,
                  "Items:\n",
                  "Fighting Mode Selection",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  fModeName,
                  null);
      for (int i = 0; i < fModeName.length; i++) {
        if (fModeName[i].equals(choiceF)) {
          fMode = fModeIdList[i];
          break;
        }
      }
      print("fMode = " + choiceF);
      print("--");

      Frame frameS = new Frame("Select Spell");
      String choiceS =
          (String)
              JOptionPane.showInputDialog(
                  frameS,
                  "Items:\n",
                  "Spell Selection",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  spellName,
                  null);
      for (int i = 0; i < spellName.length; i++) {
        if (spellName[i].equals(choiceS)) {
          spellId = spellIdList[i];
          break;
        }
      }
      print("Spell = " + choiceS);
      print("--");

      Frame frameSpellCount = new Frame("Select how many runes to withdraw from bank");
      String choiceRunesCount =
          (String)
              JOptionPane.showInputDialog(
                  frameSpellCount,
                  "Items:\n",
                  "Runes Count Selection",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  runesCountName,
                  null);
      for (int i = 0; i < runesCountName.length; i++) {
        if (runesCountName[i].equals(choiceRunesCount)) {
          runesCount = runesCountList[i];
          break;
        }
      }
      print("Total Runes to withdraw when banking = " + choiceRunesCount);
      print("--");

      Frame frameFood = new Frame("Select Food");
      String choiceFood =
          (String)
              JOptionPane.showInputDialog(
                  frameFood,
                  "Items:\n",
                  "Food Selection",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  foodName,
                  null);
      for (int i = 0; i < foodName.length; i++) {
        if (foodName[i].equals(choiceFood)) {
          foodId = foodIdList[i];
          break;
        }
      }
      print("Food = " + choiceFood);
      print("--");

      Frame frameFoodCount = new Frame("Select how many rooms to keep in inventory");
      String choiceFoodCount =
          (String)
              JOptionPane.showInputDialog(
                  frameFoodCount,
                  "Items:\n",
                  "Food Count Selection",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  foodCountName,
                  null);
      for (int i = 0; i < foodCountName.length; i++) {
        if (foodCountName[i].equals(choiceFoodCount)) {
          inventoryCount = foodCountList[i];
          break;
        }
      }
      print("Total Inventory empty rooms after banking = " + choiceFoodCount);
      print("--");

      Frame framePickUp = new Frame("Select PickUp");
      String choicePickUp =
          (String)
              JOptionPane.showInputDialog(
                  framePickUp,
                  "Items:\n",
                  "Pick Up Selection",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  itemsName,
                  null);
      for (int i = 0; i < itemsName.length; i++) {
        if (itemsName[i].equals(choicePickUp)) {
          items = (int[]) itemsList[i];
          break;
        }
      }
      print("PickUp = " + choicePickUp);
      print("--");

      Frame frameDrink = new Frame("Select To Drink Super Def or Not");
      String choiceDrink =
          (String)
              JOptionPane.showInputDialog(
                  frameDrink,
                  "Items:\n",
                  "Drink Selection",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  drinkOrNotName,
                  null);
      for (int i = 0; i < drinkOrNotName.length; i++) {
        if (drinkOrNotName[i].equals(choiceDrink)) {
          drinkOrNot = i;
          break;
        }
      }
      print("Drink = " + choiceDrink);
      print("--");
    } else
      print(
          "Default settings: Defence, 2500 chaos, Lobsters, 5 empty spaces, drink, pick all items");

    print("Sinister Key Banker by: Abyte0");
    print("Version Version 1 - RunOrAttack + teleport safety added");
    print("--");
    print("Hosted By: Harry");
    print("--");
  }

  public int main() {
    if (getFightMode() != fMode) setFightMode(fMode);
    // if we want to drink + we have a pot + we are to regular level def or lower we want to drink
    if (drinkOrNot == 0
        && getInventoryCount(superDef1) > 0
        && getLevel(1) + 5 >= getCurrentLevel(1)) {
      if (inCombat()) {
        RunFromCombat();
        return random(200, 400);
      }
      useItem(getInventoryIndex(superDef1));
      return random(1100, 1300);
    }
    // if we have a empty vial we drop it
    if (getInventoryCount(vial) > 0) {
      if (inCombat()) {
        RunFromCombat();
        return random(200, 400);
      }
      dropItem(getInventoryIndex(vial));
      return random(1000, 1200);
    }

    // if we in the room
    if (getX() >= 581 && getX() <= 585 && getY() >= 3551 && getY() <= 3576) {
      if (getHpPercent() < 20 && getInventoryCount(foodId) == 0) {
        castOnSelf(camelotTeleport);
        print("Emergency teleport exit and stop");
        setAutoLogin(false);
        logout();
        stopScript();
        return random(5400, 5000);
      }
      // if we are low life
      if (getHpPercent() < 60) {
        if (inCombat()) {
          RunFromCombat();
          return random(200, 400);
        }
        if (getInventoryCount(foodId) == 0) {
          // get the fuck out and hurry
          if (getY() < 3564) {
            RunSouthOrAttackSouth(allNpcIds);
            return 200;
          }
          int[] pileOfMud = getObjectById(636);
          if (pileOfMud[0] != -1) {
            atObject(pileOfMud[1], pileOfMud[2]);
            return random(200, 600);
          }
        }
        EatFood();
        return random(1600, 2000);
      }
      if (getInventoryCount(foodId) <= 1
          || (getInventoryCount(mindRunes) <= cptRunesToBankAt
              && getInventoryCount(chaosRunes) <= cptRunesToBankAt)) {
        // get the fuck out before its too late
        if (getY() >= 3570) {
          int[] pileOfMud = getObjectById(636);
          if (pileOfMud[0] != -1) {
            atObject(pileOfMud[1], pileOfMud[2]);
            return random(200, 600);
          }
        } else RunSouthOrAttackSouth(allNpcIds);
        return random(500, 700);
      }
      // We need to Pick Up the Item if any on floor
      for (int item : items) {
        int[] groundItems = getItemById(item);
        if (groundItems[0] != -1) {
          if (isAtApproxCoords(groundItems[1], groundItems[2], 8)) {
            // if in combat we run else we pickup
            if (inCombat()) {
              RunFromCombat();
              return random(300, 500);
            }
            // if we dont have room to pickup the item we eat a food
            if (getInventoryCount() == 30) {
              EatFood();
              return random(3300, 3500);
            }
            // we finaly pick the item :D

            pickupItem(groundItems[0], groundItems[1], groundItems[2]);
            return random(1000, 1500);
          }
        }
      }
      // we need ot fight the npc
      int[] salarin = getAllNpcById(salarinId);
      if (salarin[0] != -1) {
        if (!isAtApproxCoords(salarin[1], salarin[2], 5)) {
          walkTo(salarin[1], salarin[2]);
          return random(1300, 1400);
        }
        if (spellId == windBolt && getInventoryCount(WaterRune) >= 2)
          mageNpc(salarin[0], waterBolt);
        else if (spellId == windBolt && getInventoryCount(EarthRune) >= 3)
          mageNpc(salarin[0], earthBolt);
        else if (spellId == windBolt && getInventoryCount(FireRune) >= 4)
          mageNpc(salarin[0], fireBolt);
        else mageNpc(salarin[0], spellId);

        return random(1300, 1400);
      }

      if (getFatigue() > 20) {
        if (inCombat()) {
          RunFromCombat();
          return random(200, 400);
        }
        useSleepingBag();
        return 3000;
      }

      int[] druids = getAllNpcById(chaosDruids);
      if (druids[0] != -1 && druids[2] <= 3560) {
        mageNpc(druids[0], spellId);
        return random(1300, 1400);
      }

      if (getX() != 583 && getY() != 3556) {
        // if no monster then we may want to wait away
        walkTo(583, 3556); // Center room
        print("Nothing to do, walkback");
        return random(700, 950);
      }

      return random(1200, 1400);
    }
    if (isBanking()) {
      for (int item : items) {
        if (item == law) continue;

        if (getInventoryCount(item) > 0) {
          deposit(item, getInventoryCount(item));
          return random(1000, 1500);
        }
      }
      if (spellId == 0 && getInventoryCount(mindRunes) < runesCount) {
        withdraw(mindRunes, runesCount - getInventoryCount(mindRunes));
        return random(1000, 1500);
      }
      if (getInventoryCount(law) > 60) {
        deposit(law, 50);
        return random(2000, 3500);
      }
      if (getInventoryCount(law) < 1) {
        withdraw(law, 1);
        return random(1000, 1500);
      }
      if (spellId == 8 && getInventoryCount(chaosRunes) < runesCount) {
        withdraw(chaosRunes, runesCount - getInventoryCount(chaosRunes));
        return random(1000, 1500);
      }
      if (getInventoryCount() < inventoryCount) {
        withdraw(foodId, inventoryCount - getInventoryCount());
        return random(1000, 1500);
      }
      closeBank();
    }
    // If we need to go bank
    if (getInventoryCount(foodId) <= 1
        || (getInventoryCount(mindRunes) <= cptRunesToBankAt
            && getInventoryCount(chaosRunes) <= cptRunesToBankAt)) {
      print("We must go Bank");
      // if we in small house near bank we mmust go outside
      if (getX() >= 589 && getX() <= 593 && getY() >= 761 && getY() <= 764) {
        // If not next to door we walk to it
        if (getX() != 591 && getY() != 764) {
          walkTo(591, 764);
          return random(100, 400);
        }
        // Else we open the door if open
        int[] doorObj = getWallObjectById(2);
        if (doorObj[0] != -1) {
          if (isAtApproxCoords(doorObj[1], doorObj[2], 2)) {
            atWallObject(doorObj[1], doorObj[2]);
            return random(200, 500);
          }
        }
        // Else we walk outside
        walkTo(591, 765);
        return random(100, 1500);
      }
      // IF WE NEAR BANK BUT OUTSIDE HOUSE
      if (isAtApproxCoords(585, 753, 30)) {
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

      // downstair near bank we want to go upstair
      if (getX() >= 591 && getX() <= 596 && getY() >= 3590 && getY() <= 3597) {
        int[] stair = getObjectById(43);
        if (stair[0] != -1) atObject(stair[1], stair[2]);
        return random(300, 400);
      }

      // Chaos Druids rooom
      if (getX() >= 576 && getX() <= 598 && getY() >= 3580 && getY() <= 3589) {
        if (getX() >= 585) {
          // door side lest pick lock door
          int[] door = getWallObjectById(162);
          if (door[0] != -1) atWallObject2(door[1], door[2]);
          return random(300, 400);
        } else {
          // Stair Side lest walk to center room
          walkTo(585, 3585);
          return random(300, 400);
        }
      }
      // Chaos Druids JUNK SPOT
      if (getX() >= 582 && getX() <= 583 && getY() == 3589) {
        // walk to center room
        walkTo(585, 3585);
        return random(300, 400);
      }

      // If we down the Pile of Mud
      if (getX() >= 570 && getX() <= 585 && getY() >= 3520 && getY() <= 3535) {
        int[] stairObj = getObjectById(629);
        if (stairObj[0] != -1) {
          atObject(stairObj[1], stairObj[2]);
          return random(200, 500);
        }
      }
    } else {
      // we can go to war

      // if we in front of the room door we want to open and enter
      if (getX() == 591 && getY() == 765) {
        // we open the door
        int[] doorObj = getWallObjectById(2);
        if (doorObj[0] != -1) {
          if (isAtApproxCoords(doorObj[1], doorObj[2], 2)) {
            atWallObject(doorObj[1], doorObj[2]);
            return random(200, 500);
          }
        }
        // Else we walk inside
        walkTo(591, 764);
        return random(100, 1500);
      }

      // if we in small house near bank we mmust go outside
      if (getX() >= 589 && getX() <= 593 && getY() >= 761 && getY() <= 764) {
        // we in the hosue so we need ot get downstairs
        int[] stairObj = getObjectById(42);
        if (stairObj[0] != -1) {
          atObject(stairObj[1], stairObj[2]);
          return random(200, 500);
        }
      }
      // IF WE NEAR BANK BUT OUTSIDE HOUSE
      if (isAtApproxCoords(585, 753, 30)) {
        // we want to walk to the house
        walkTo(591, 765);
        return random(400, 500);
      }

      // downstair near bank we want to get to chaos
      if (getX() >= 591 && getX() <= 596 && getY() >= 3590 && getY() <= 3597) {
        int[] door = getWallObjectById(162);
        if (door[0] != -1) atWallObject2(door[1], door[2]);
        return random(300, 400);
      }

      // Chaos Druids rooom
      if (getX() >= 576 && getX() <= 598 && getY() >= 3580 && getY() <= 3589) {
        if (getX() > 585) {
          // door side we wnt to walk to center
          walkTo(585, 3585);
          return random(300, 400);
        } else {
          // Stair Side we want to use the stairs
          int[] stairObj = getObjectById(630);
          if (stairObj[0] != -1) {
            atObject(stairObj[1], stairObj[2]);
            return random(200, 500);
          }
        }
      }
      // Chaos Druids JUNK SPOT
      if (getX() >= 582 && getX() <= 583 && getY() == 3589) {
        // walk to center room
        walkTo(585, 3585);
        return random(300, 400);
      }

      // If we down the Pile of Mud
      if (getX() >= 570 && getX() <= 585 && getY() >= 3520 && getY() <= 3535) {
        int[] stairObj = getObjectById(633);
        if (stairObj[0] != -1) {
          atObject(stairObj[1], stairObj[2]);
          return random(200, 500);
        }
      }
    }
    return random(2000, 2500);
  }

  private final void EatFood() {
    int foodIndex = getInventoryIndex(foodId);
    useItem(foodIndex);
  }

  private void RunFromCombat() {
    walkTo(getX(), getY());
  }

  private void RunSouthOrAttackSouth(int[] npcIds) {
    boolean npcFound = false;

    print("Run or attack V2");

    for (int npcId : npcIds) {
      int[] npcs = getNpcInRadius(npcId, getX(), getY() + 1, 0);
      if (npcs[0] == -1) continue;

      print("attacking" + npcs[0]);
      attackNpc(npcs[0]);
      npcFound = true;
      break;
    }

    if (!npcFound) {
      if (random(1, 8) == 5) // Try to Bypass invisible block
      walkTo(getX() + random(-1, 1), getY() + random(-1, 2));
      else if (isReachable(getX(), getY() + 1)) walkTo(getX(), getY() + 1);
      else if (isReachable(getX() + 1, getY() + 1)) walkTo(getX() + 1, getY() + 1);
      else if (isReachable(getX() - 1, getY() + 1)) walkTo(getX() - 1, getY() + 1);
      else walkTo(getX() + random(-3, 3), getY() + random(0, 3));

      print("walking to escape south");
    }
  }
}
