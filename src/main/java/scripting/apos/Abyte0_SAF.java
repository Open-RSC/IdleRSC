package scripting.apos;
// SimpleAutofighter Script Provided by RLN
// Edited by Abyte0
// 2012-01-24 - Version 0 - Added Open Doors/Gates at 10 squares from you if closed
// 2012-01-24 - Version 0 - Added Eating multiple food like Yomama Scripts
// 2012-01-24 - Version 0 - Will Add Pick && Bury BigBones

public class Abyte0_SAF extends Abyte0_Script {

  int fmode = -1;
  int npcID = -1;
  int sleepAt = 90;
  int radius = Integer.MAX_VALUE;
  int startX = -1;
  int startY = -1;
  int eatAt = -1;
  int foodID = 373;
  int bigBonesID = 413;
  int pickupID = -1;
  boolean isPickupEnabled = false;
  int[] foodIDs =
      new int[] {
        330, // cake 3/3
        333, // cake 2/3
        335, // cake 1/3
        895, // Swamp Toad
        897, // King worm
        138, // bread
        142, // wine
        373 // Lobs
      };

  boolean walkBack = false;
  boolean bury = true;

  // abyte0_saf f=3,n=321,r=25,h=20,e=373,w=1,b=1
  // f=0,n=3,w=1  for Chicken controlled

  public Abyte0_SAF(String e) {
    super(e);
  }

  public void init(String params) {

    System.out.println("Abyte0_SAF");
    System.out.println("BETA 0");
    System.out.println("USAGE = abyte0_saf f=3,n=321,r=25,h=20,e=373,w=1,b=1 ");
    String[] in;

    try {

      in = params.trim().toLowerCase().split(",");

      for (int i = 0; i < in.length; i++) {

        if (in[i].startsWith("f=")) {

          fmode = Integer.parseInt(in[i].substring(2));

          if (fmode < 0 || fmode > 3) throw new Exception("Invalid fight mode (" + fmode + ")");

          print("fmode set " + fmode);

          continue;
        }

        if (in[i].startsWith("n=")) {

          npcID = Integer.parseInt(in[i].substring(2));

          print("npcID set " + npcID);

          continue;
        }

        if (in[i].startsWith("p=")) {

          int flag = Integer.parseInt(in[i].substring(2));

          if (flag < 0 || flag > 10000) throw new Exception("Invalid pickup id ");

          pickupID = flag;
          isPickupEnabled = true;
          radius = 10;

          print("Pickup enabled for item " + pickupID);

          continue;
        }

        if (in[i].startsWith("r=")) {

          radius = Integer.parseInt(in[i].substring(2));

          print("radius set " + radius);

          continue;
        }

        if (in[i].startsWith("s=")) {

          sleepAt = Integer.parseInt(in[i].substring(2));

          if (sleepAt < 0 || sleepAt > 100)
            throw new Exception("Invalid sleep at (" + sleepAt + ")");

          print("sleepAt set " + sleepAt);

          continue;
        }

        if (in[i].startsWith("h=")) {

          eatAt = Integer.parseInt(in[i].substring(2));

          if (sleepAt < 0 || sleepAt > 100) throw new Exception("Invalid eat at (" + eatAt + ")");

          print("eatAt set " + eatAt);

          continue;
        }

        if (in[i].startsWith("e=")) {

          foodID = Integer.parseInt(in[i].substring(2));

          print("foodID set " + foodID);

          continue;
        }

        if (in[i].startsWith("b=")) {

          int flag = Integer.parseInt(in[i].substring(2));

          if (flag != 0 && flag != 1) throw new Exception("Invalid bury flag (" + flag + ")");

          bury = (flag == 0 ? false : true);

          print("bury set " + bury);

          continue;
        }

        if (in[i].startsWith("w=")) {

          int flag = Integer.parseInt(in[i].substring(2));

          if (flag != 0 && flag != 1) throw new Exception("Invalid walkback flag (" + flag + ")");

          walkBack = (flag == 0 ? false : true);

          print("walkBack set " + walkBack);

          continue;
        }

        throw new Exception("parsing fucked up");
      }

    } catch (Exception _ex) {

      System.out.println("Error while initiating simple autofighter, invalid input");

      System.out.println("Type in: \t\"n=\" for npc id");

      System.out.println("\t\t\"f=\" for fight mode");

      System.out.println("\t\t\"s=\" to sleep at specified fatigue (default is 90)");

      System.out.println("\t\t\"r=\" for radius (don't include if you don't need)");

      System.out.println(
          "\t\t\"h=\" to eat at specified hp percent, ie: 5/10 hp is 50% so the param is 50 (must be set in order to autoeat)");

      System.out.println("\t\t\"e=\" to set food ID (default is lobsters)");

      System.out.println(
          "\t\t\"w=\" for walkback (0 = off, 1 = on - will walk back to coords that the script started at)");

      System.out.println(
          "when you are typing your input, separate your variables with a comma, ie:");

      System.out.println(
          "\"simpleautofighter n=11,f=1,r=20\" \n ^^ that would fight men (11), in aggressive mode(1) (no spaces, case insensitive, in any order)");

      System.out.println("Error message: " + _ex.getMessage());

      stopScript();

      in = null;
    }

    if (fmode == -1) {

      System.out.println("No fight mode set, type f=fmode in the params");

      stopScript();
    }

    if (npcID == -1) {

      System.out.println("No npc id set, type n=npcid in the params");

      stopScript();
    }
    System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =");
    System.out.println("=Edited by Abyte0 -Open Doors Gates 10 squares from you");
    System.out.println("=Edited by Abyte0 -Multi Food");
    System.out.println("=Edited by Abyte0 -Bury BigBones");
    System.out.println("=Version 0");
    System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =");
  }

  public final void BuryBigBone() {
    int boneIndex = getInventoryIndex(bigBonesID);
    System.out.println("Bury Bones at position : " + boneIndex);
    useItem(boneIndex);
  }

  public int TryPickup() {
    if (isPickupEnabled) {
      int[] item = getItemById(pickupID);
      if (item[0] != -1) {

        int difX = item[1] - startX;
        int difY = item[2] - startY;

        if (difX <= radius && difX >= -radius && difY <= radius && difY >= -radius) {
          pickupItem(item[0], item[1], item[2]);
          return random(200, 300);
        }
      }
    }
    return -1;
  }

  public int TryBury() {
    if (bury == true) {
      if (getInventoryCount(bigBonesID) > 0) {
        if (inCombat()) {
          RunFromCombat();
          return random(100, 200);
        }
        BuryBigBone();
        return random(200, 300);
      }
      int[] bigBone = getItemById(bigBonesID);
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

  public void RunFromCombat() {
    walkTo(startX, startY);
  }

  public int getUntrapped() {
    for (int i = 1; i < 8; i++) {
      int result = getUntrappedByMaxDistance(i);
      if (result != 0) return result;
    }

    return 0;
  }

  public int getUntrappedByMaxDistance(int maxDistance) {
    // Gate
    int[] Gate = getObjectById(57);
    if (Gate[0] != -1) {
      if (isAtApproxCoords(Gate[1], Gate[2], maxDistance)) {
        atObject(Gate[1], Gate[2]);
        return random(800, 900);
      }
    }
    // Chicken gate lumb
    int[] ChickGate = getObjectById(60);
    if (ChickGate[0] != -1) {
      if (isAtApproxCoords(ChickGate[1], ChickGate[2], maxDistance)) {
        atObject(ChickGate[1], ChickGate[2]);
        return random(800, 900);
      }
    }
    // BankDoor
    int[] BankDoor = getObjectById(64);
    if (BankDoor[0] != -1) {
      if (isAtApproxCoords(BankDoor[1], BankDoor[2], maxDistance)) {
        atObject(BankDoor[1], BankDoor[2]);
        return random(800, 900);
      }
    }

    // Regular Door
    int[] Door = getWallObjectById(2);
    if (Door[0] != -1) {
      if (isAtApproxCoords(Door[1], Door[2], maxDistance)) {
        atWallObject(Door[1], Door[2]);
        return random(800, 900);
      }
    }

    // if no door found to be closed we continue
    return 0;
  }

  public int main() {

    if (startX == -1 || startY == -1) {

      startX = getX();

      startY = getY();
    }

    if (getFightMode() != fmode) {

      setFightMode(fmode);

      return random(200, 300);
    }

    if (getFatigue() >= sleepAt) {

      useSleepingBag();

      return 1;
    }

    if (!inCombat()) {

      if (getCurrentLevel(3) <= eatAt) {
        int idx = getInventoryIndex(foodIDs);
        if (idx == -1) {
          if (getHpPercent() < 10) {
            System.out.println("hp is dangerously low with no food.");
            stopScript();
            return 0;
          } else {
            return random(60000, 90000);
          }
        }
        useItem(idx);
        return random(500, 600);
      }

      // We look if trapped else we continue
      int unTrap = getUntrapped();
      if (unTrap != 0) return unTrap;

      // TODO PICK AND BURY BONES
      int nombre = TryBury();
      if (nombre != -1) return nombre;

      int pickReturn = TryPickup();
      if (pickReturn != -1) return pickReturn;

      int npc[];

      if (radius != Integer.MAX_VALUE) npc = getNpcInRadius(npcID, startX, startY, radius);
      else npc = getNpcById(npcID);

      if (npc[0] != -1) {

        attackNpc(npc[0]);

        return random(900, 1100);
      }

      if (!isAtApproxCoords(startX, startY, 0) && walkBack) {

        walkTo(startX, startY);

        return random(900, 1100);
      }
    }

    return random(500, 800);
  }
}
