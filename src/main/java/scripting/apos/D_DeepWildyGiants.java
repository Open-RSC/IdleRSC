package scripting.apos;

import compatibility.apos.Script;
import java.util.Locale;
import javax.swing.JOptionPane;

public class D_DeepWildyGiants extends Script {

  public D_DeepWildyGiants(String arg0) {
    // super(arg0);
    // TODO Auto-generated constructor stub
  }

  final int banker = 792;
  final int hob = 61;
  int bbones = 0;
  int limps = 0;
  String fightModeSelected;
  int fightMode;
  int limpsBanked = 0;
  final int[] lumbToP2PGateX = {
    131, 128, 117, 110, 113, 113, 110, 109, 107, 103, 95, 92, 84, 73, 66, 64, 64, 68, 70, 72, 73,
    79, 88, 97, 106, 110, 110, 110, 109, 109, 110, 109, 110, 109, 111, 113, 117, 120, 119, 128, 130,
    132, 130, 124, 122, 119, 121, 119, 118, 118, 118, 118, 117, 114, 111, 111, 111, 112, 108, 105,
    106, 108, 105, 102, 111,
  };
  final int[] lumbToP2PGateY = {
    637, 626, 625, 618, 610, 602, 595, 592, 587, 582, 579, 574, 571, 569, 564, 558, 551, 545, 539,
    531, 521, 512, 508, 507, 507, 499, 490, 481, 475, 466, 456, 446, 436, 426, 419, 411, 405, 394,
    384, 376, 369, 363, 355, 346, 339, 328, 321, 312, 304, 297, 288, 281, 273, 262, 254, 243, 233,
    222, 210, 199, 190, 178, 167, 151, 143,
  };
  final int[] gateToMBx = {
    113, 118, 129, 140, 152, 162, 175, 185, 193, 205, 214, 220, 227,
  };
  final int[] gateToMBy = {
    132, 126, 121, 118, 115, 110, 102, 100, 99, 98, 98, 98, 105,
  };
  final int[] mbtoSpotX = {
    227, 239, 247, 253, 262, 269,
  };
  final int[] mbtoSpotY = {
    105, 110, 118, 126, 125, 127,
  };
  boolean inBankingSequence = false;
  int[] lootIds = {
    220, 413, 526, 527, 1277, 1276, 31, 32, 33, 34, 35, 36, 38, 40, 41, 46, 173, 73, 126, 448, 449,
    450, 451, 452, 453, 157, 158, 159, 160, 10, 193, 411, 438, 439, 440, 441, 442, 443, 466, 40, 42,
  };
  private double startxp = 0;
  private long start_time;
  int start_xp = 0;
  int autoEatHP = 0;
  long time = -1L;
  int withdrawAmount = 0;
  public final int areaX2 = 263;
  public final int areaY2 = 110;
  public final int areaX = 269;
  public final int areaY = 100;
  public final int bankFrontX = 225;
  public final int bankFrontY = 108;
  public final int bankFrontX2 = 220;
  public final int bankFrontY2 = 111;
  public int weaponID = 594;
  int foodid = 373;
  int bankEveryMinutes = 60;
  long lastBankTimeMark;
  int bronzeArrows = 0;
  int airs = 0;
  int earths = 0;
  int laws = 0;
  int nats = 0;
  int fires = 0;
  int chaos = 0;
  int keyhalfs = 0;
  int shieldhalfs = 0;
  int startxpPray = 0;
  boolean izok = false;
  boolean lootLimps = false;
  boolean bankBones = false;

  public void init(String params) {

    Object[] fightModeOptions = {"Attack", "Strength", "Defense", "Controlled"};
    fightModeSelected =
        (String)
            JOptionPane.showInputDialog(
                null,
                "Fight mode",
                "Thugs",
                JOptionPane.PLAIN_MESSAGE,
                null,
                fightModeOptions,
                fightModeOptions[0]);
    switch (fightModeSelected) {
      case "Strength":
        fightMode = 1;
        break;
      case "Attack":
        fightMode = 2;
        break;
      case "Defense":
        fightMode = 3;
        break;
      case "Controlled":
        fightMode = 0;
        break;
    }

    autoEatHP = Integer.parseInt(JOptionPane.showInputDialog("Eat HP: "));
    foodid = Integer.parseInt(JOptionPane.showInputDialog("Food ID: "));
    withdrawAmount = Integer.parseInt(JOptionPane.showInputDialog("Food amount to withdraw: "));
    weaponID = Integer.parseInt(JOptionPane.showInputDialog("Weapon ID: "));
    int answer =
        JOptionPane.showConfirmDialog(null, null, "Loot limps?", JOptionPane.YES_NO_OPTION);
    if (answer == JOptionPane.YES_OPTION) {
      lootLimps = true;
      lootIds =
          new int[] {
            220, 413, 526, 527, 1277, 1276, 31, 32, 33, 34, 35, 36, 38, 40, 41, 46, 173, 73, 414,
            126, 448, 449, 450, 451, 452, 453, 157, 158, 159, 160, 10, 193, 411, 438, 439, 440, 441,
            442, 443, 466, 40, 42
          };
    } else {
      lootLimps = false;
    }

    answer = JOptionPane.showConfirmDialog(null, null, "Bank bones?", JOptionPane.YES_NO_OPTION);
    bankBones = answer == JOptionPane.YES_OPTION;

    start_time = System.currentTimeMillis();
    startxp += getAccurateXpForLevel(0);
    startxp += getAccurateXpForLevel(1);
    startxp += getAccurateXpForLevel(2);
    startxp += getAccurateXpForLevel(3);
    startxpPray += getAccurateXpForLevel(5);
    System.out.println("Must use food, at least 1 food per trip, will eat food for space");
    bankEveryMinutes =
        Integer.parseInt(
            JOptionPane.showInputDialog("Always bank every minutes: (set -1 to disable)"));
    lastBankTimeMark = System.currentTimeMillis();
  }

  public boolean bankingTime() {
    if (bankEveryMinutes == -1) {
      return false;
    }
    return (System.currentTimeMillis() - lastBankTimeMark) >= (bankEveryMinutes * 60000L);
  }

  public int minutesToNextBank() {
    return (int)
        (((bankEveryMinutes * 60000) - (System.currentTimeMillis() - lastBankTimeMark)) / 60000);
  }

  public int useGate() {
    int[] objects = getObjectById(137);

    if (objects[0] != -1) {

      atObject(objects[1], objects[2]);
      return random(800, 1200);
    }
    return random(800, 1200);
  }

  public boolean openBankDoors() {
    int[] objects = getObjectById(64);

    if (objects[0] != -1) {

      atObject(objects[1], objects[2]);
      return true;
    }
    return false;
  }

  public int main() {

    if (time == -1L) {
      time = System.currentTimeMillis();
    }

    if (getFightMode() != fightMode) {

      setFightMode(fightMode);
      return random(100, 200);
    }

    if (getFatigue() >= 96
        && hasInventoryItem(1263)
        && !inCombat()
        && getCurrentLevel(3) >= autoEatHP) {

      useSleepingBag();
      return random(1000, 1500);
    }

    if (getCurrentLevel(3) < autoEatHP && hasInventoryItem(foodid) && !isBanking()) {
      if (!inCombat()) {
        int count = getInventoryCount();
        for (int i = 0; i < count; i++) {
          if (getItemCommand(i).toLowerCase(Locale.ENGLISH).equals("eat")) {
            useItem(i);
            return random(800, 1000);
          }
        }
        System.out.println("No food!");
        return random(500, 1000);
      } else {
        walkTo(getX(), getY());
        return random(300, 500);
      }
    }
    // this handles fighting
    if (isAtApproxCoords(268, 2963, 40)
        && getInventoryCount() != 30
        && hasInventoryItem(foodid)
        && hasInventoryItem(1263)
        && !bankingTime()) {

      if (!inCombat()) {
        if (!bankBones) {
          if (getInventoryCount(413) > 0) {
            useItem(getInventoryIndex(413));
            return random(800, 1200);
          }
        }
        if (lootAll()) return random(800, 1200);
        if (attack()) return random(800, 1200);
      }

    } else if (getInventoryCount() == 30 && hasInventoryItem(foodid) && !inCombat()) {
      System.out.println("Eating food for space");
      int count = getInventoryCount();
      for (int i = 0; i < count; i++) {
        if (getItemCommand(i).toLowerCase(Locale.ENGLISH).equals("eat")) {
          useItem(i);
          return random(800, 1000);
        }
      }
      return random(800, 1000);
      // this handles walking to bank & banking & deathwalk.
    } else if (((!hasInventoryItem(1263) || !hasInventoryItem(foodid))
            || (inBankingSequence)
            || bankingTime())
        && getX() > 219) {

      if (isAtApproxCoords(446, 3368, 25)) {
        if (!isBanking()) {
          System.out.println("Opening bank");
          if (isQuestMenu()) {
            answer(0);
            return random(2000, 3000);
          } else {
            int[] bankers = getNpcById(banker);
            if (bankers[0] != -1) {
              talkToNpc(bankers[0]);
              return random(3000, 4000);
            }
          }
        } else {
          System.out.println("Banking");
          for (int lootId : lootIds) {
            if (getInventoryCount(lootId) > 0) {
              updateLootCount(lootId);
              deposit(lootId, getInventoryCount(lootId));
              inBankingSequence = true;
              return random(1000, 1500);
            }
          }
          if (getInventoryCount(foodid) < withdrawAmount) {
            if (bankCount(foodid) > withdrawAmount) {
              withdraw(foodid, withdrawAmount - getInventoryCount(foodid));
            } else {
              System.out.println("No more food in bank, stopping");
              setAutoLogin(false);
              stopScript();
            }

            inBankingSequence = true;
            return random(1000, 1500);
          }
          if (!hasInventoryItem(1263)) {
            if (bankCount(1263) > 0) {
              withdraw(1263, 1);
            } else {
              System.out.println("Out of sleeping bags, stopping");
              setAutoLogin(false);
              stopScript();
            }
            inBankingSequence = true;
            return random(1000, 1500);
          }

          closeBank();
          inBankingSequence = false;
          lastBankTimeMark = System.currentTimeMillis();
          return random(1000, 1500);
        }
      }

      if (inArea(getX(), getY(), bankFrontX, bankFrontY, bankFrontX2, bankFrontY2)) {
        atObject(223, 110);
        return random(800, 1200);
      }

      if (isAtApproxCoords(227, 105, 0)) {
        if (getWallObjectIdFromCoords(227, 106) == 2) {
          atWallObject(227, 106);
          return random(800, 1000);
        } else {
          walkTo(227, 106);
          return random(800, 1000);
        }
      }
      if (isAtApproxCoords(227, 106, 0)) {
        if (getWallObjectIdFromCoords(227, 107) == 24) {
          System.out.println("Outter web");
          useItemOnWallObject(getInventoryIndex(weaponID), 227, 107);
          return random(800, 1000);
        } else {
          walkTo(227, 107);
          return random(800, 1000);
        }
      }
      if (isAtApproxCoords(227, 107, 0) || isAtApproxCoords(227, 108, 0)) {
        if (getWallObjectIdFromCoords(227, 109) == 24) {
          System.out.println("Inner web");
          useItemOnWallObject(getInventoryIndex(weaponID), 227, 109);
          return random(800, 1000);
        } else {
          walkTo(226, 110);
          return random(800, 1000);
        }
      }

      if (isAtApproxCoords(227, 109, 0)
          || isAtApproxCoords(227, 110, 0)
          || isAtApproxCoords(226, 110, 0)) {
        if (getWallObjectIdFromCoords(226, 110) == 2) {
          atWallObject(226, 110);
          return random(800, 1000);
        } else {
          walkTo(225, 110);
          return random(800, 1000);
        }
      }

      if (isAtApproxCoords(268, 2963, 40)) {
        if (inCombat()) {
          walkTo(getX(), getY());
          return random(250, 350);
        }
        atObject(268, 2960);
        return random(800, 1200);
      }

      return walkArrayPathReverse(mbtoSpotX, mbtoSpotY);

    } else if (!inArea(getX(), getY(), areaX, areaY, areaX2, areaY2)
        && getInventoryCount() != 30
        && getX() > 219) {

      if (isAtApproxCoords(446, 3368, 25)) {
        atObject(446, 3367);
        return random(800, 1200);
      }
      if (inArea(getX(), getY(), bankFrontX, bankFrontY, bankFrontX2, bankFrontY2)) {
        if (getWallObjectIdFromCoords(226, 110) == 2) {
          atWallObject(226, 110);
          return random(800, 1000);
        } else {
          walkTo(226, 110);
          return random(800, 1000);
        }
      }

      if (isAtApproxCoords(227, 109, 0)
          || isAtApproxCoords(227, 110, 0)
          || isAtApproxCoords(226, 110, 0)) {
        if (getWallObjectIdFromCoords(227, 109) == 24) {
          System.out.println("Cutting inner web");
          useItemOnWallObject(getInventoryIndex(weaponID), 227, 109);
          return random(800, 1000);
        } else {
          walkTo(227, 107);
          return random(800, 1000);
        }
      }

      if (isAtApproxCoords(227, 107, 0) || isAtApproxCoords(227, 108, 0)) {
        System.out.println("Outter Web");
        if (getWallObjectIdFromCoords(227, 107) == 24) {
          useItemOnWallObject(getInventoryIndex(weaponID), 227, 107);
          return random(800, 1000);
        } else {
          walkTo(227, 106);
          return random(800, 1000);
        }
      }

      if (isAtApproxCoords(227, 106, 0)) {
        if (getWallObjectIdFromCoords(227, 106) == 2) {
          atWallObject(227, 106);
          return random(800, 1200);
        } else {
          walkTo(227, 105);
          return random(800, 1200);
        }
      }

      if (isAtApproxCoords(268, 127, 4)) {
        atObject(268, 128);
        return random(800, 1200);
      }

      return walkArrayPath(mbtoSpotX, mbtoSpotY);

    } else {
      if (getY() > 141) {
        if (inArea(getX(), getY(), 114, 142, 109, 143)) {
          atObject(111, 142);
          return random(800, 1200);
        } else {
          return walkArrayPath(lumbToP2PGateX, lumbToP2PGateY);
        }
      } else {
        return walkArrayPath(gateToMBx, gateToMBy);
      }
    }

    return 100;
  }

  public boolean attack() {
    int[] p = _getReachableNpc(hob);
    if (p[0] != -1) {
      attackNpc(p[0]);
      return true;
    }
    return false;
  }

  public boolean lootAll() {

    int[] item = getItemById(lootIds);

    if (item[0] != -1 && (distanceTo(item[1], item[2]) < 5) && isReachable(item[1], item[2])) {
      pickupItem(item[0], item[1], item[2]);
      return true;
    }
    return false;
  }

  public void updateLootCount(int itemID) {
    if (itemID == 41) {
      chaos += getInventoryCount(itemID);
    } else if (itemID == 34) {
      earths += getInventoryCount(itemID);
    } else if (itemID == 33) {
      airs += getInventoryCount(itemID);
    } else if (itemID == 40) {
      nats += getInventoryCount(itemID);
    } else if (itemID == 42) {
      laws += getInventoryCount(itemID);
    } else if (itemID == 31) {
      fires += getInventoryCount(itemID);
    } else if (itemID == 11) {
      bronzeArrows += getInventoryCount(itemID);
    } else if (itemID == 526) {
      keyhalfs += getInventoryCount(itemID);
    } else if (itemID == 527) {
      keyhalfs += getInventoryCount(itemID);
    } else if (itemID == 1277) {
      shieldhalfs += getInventoryCount(itemID);
    } else if (itemID == 220) {
      limps += getInventoryCount(itemID);
    } else if (itemID == 413) {
      bbones += getInventoryCount(itemID);
    }
  }

  public void paint() {
    long time = ((System.currentTimeMillis() - start_time) / 1000L);
    if (time < 1L) {
      time = 1L;
    }
    int x = 10;
    int y = 222;

    drawString("D_DeepWildyGiants", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Runtime: " + _getRuntime(), x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        "Combat XP gained: "
            + (((getAccurateXpForLevel(2)
                        + getAccurateXpForLevel(1)
                        + getAccurateXpForLevel(0)
                        + getAccurateXpForLevel(3))
                    - startxp)
                + ", per hour: "
                + (int)
                    (((((getAccurateXpForLevel(2)
                                        + getAccurateXpForLevel(1)
                                        + getAccurateXpForLevel(0)
                                        + getAccurateXpForLevel(3))
                                    - startxp)
                                * 60L)
                            * 60L)
                        / time)),
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;
    drawString(
        "Prayer XP gained: "
            + (((getAccurateXpForLevel(5)) - startxpPray)
                + ", per hour: "
                + (int) (((((getAccurateXpForLevel(5)) - startxpPray) * 60L) * 60L) / time)),
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;
    if (bankEveryMinutes != -1) {
      y += 15;
      drawString("Minutes to next Bank: " + minutesToNextBank(), x, y, 1, 0xFFFFFF);
    }
    x = 315;
    y = 55;
    drawString("Banked items:", x, y, 1, 0xFFFFFF);
    y += 25;
    drawString("Key half: " + keyhalfs, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Left Half: " + shieldhalfs, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Big bones: " + bbones, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Limps: " + limps, x, y, 1, 0xFFFFFF);
  }

  // checks if given X,Y is inside rectangluar area areaX,areaY - >
  // areaXx,areaYy where first pair of coordinates represents the top left (north-western) corner &
  // second set represents the bottom right (south-eastern) corner
  private boolean inArea(int entityX, int entityY, int areaX, int areaY, int areaXx, int areaYy) {
    return entityX <= areaX && entityY >= areaY && entityX >= areaXx && entityY <= areaYy;
  }

  // walks to furthest tile in the given array, put tiles x & y in the
  // matching indexes
  private int walkArrayPath(int[] x, int[] y) {
    for (int i = x.length - 1; i >= 0; i--) {
      if (isReachable(x[i], y[i]) && distanceTo(x[i], y[i], getX(), getY()) < 25) {
        walkTo(x[i], y[i]);
        return random(700, 1200);
      }
    }
    // no tile within 25 dist, what the hell, lets walk if its even reachable.
    for (int i = x.length - 1; i >= 0; i--) {
      if (isReachable(x[i], y[i])) {
        walkTo(x[i], y[i]);
        return random(700, 1200);
      }
    }
    return 500;
  }

  // walks to furthest tile considering the max distance to the tile in reverse order, put tiles x &
  // y in the matching
  // indexes
  private int walkArrayPathReverse(int[] x, int[] y) {
    for (int i = 0; i < x.length; i++) {
      if (isReachable(x[i], y[i]) && distanceTo(x[i], y[i], getX(), getY()) < 25) {
        walkTo(x[i], y[i]);
        return random(700, 1200);
      }
    }
    // no tile within 25 dist, what the hell, lets walk if its even reachable.
    for (int i = 0; i < x.length; i++) {
      if (isReachable(x[i], y[i])) {
        walkTo(x[i], y[i]);
        return random(700, 1200);
      }
    }
    return 500;
  }

  // storms stuff
  private int[] _getReachableNpc(int... ids) {
    int[] npc = new int[] {-1, -1, -1};
    int max_dist = Integer.MAX_VALUE;
    int count = countNpcs();
    for (int i = 0; i < count; i++) {
      if (isNpcInCombat(i)) continue;
      if (inArray(ids, getNpcId(i))) {
        int x = getNpcX(i);
        int y = getNpcY(i);
        if (!isReachable(x, y)) continue;
        int dist = distanceTo(x, y, getX(), getY());
        if (dist < max_dist) {
          npc[0] = i;
          npc[1] = x;
          npc[2] = y;
          max_dist = dist;
        }
      }
    }
    return npc;
  }

  private String _getRuntime() {
    long secs = ((System.currentTimeMillis() - start_time) / 1000);
    if (secs >= 3600) {
      return (secs / 3600) + " hours, " + ((secs % 3600) / 60) + " mins, " + (secs % 60) + " secs.";
    }
    if (secs >= 60) {
      return secs / 60 + " mins, " + (secs % 60) + " secs.";
    }
    return secs + " secs.";
  }
}
