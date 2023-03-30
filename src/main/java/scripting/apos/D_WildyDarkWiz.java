package scripting.apos;

import compatibility.apos.Script;
import java.util.Locale;
import javax.swing.JOptionPane;

public class D_WildyDarkWiz extends Script {

  public D_WildyDarkWiz(String arg0) {
    // super(arg0);]]]==///[[[sss---
    // TODO Auto-generated constructor stub
  }

  final int banker = 95;

  String fightModeSelected;
  int fightMode;
  final int[] spotToBankX = {
    311, 308, 304, 302, 305, 311, 312, 311, 308, 299, 311, 314, 313, 303, 294, 290, 287
  };
  final int[] spotToBankY = {
    414, 423, 435, 447, 459, 467, 478, 488, 496, 506, 514, 526, 537, 543, 552, 566, 571
  };
  final int[] bankToLumX = {
    287, 290, 290, 290, 279, 265, 251, 236, 222, 209, 194, 180, 166, 154, 139, 135, 134, 123
  };
  final int[] bankToLumY = {
    571, 576, 589, 599, 609, 610, 610, 610, 607, 607, 604, 606, 611, 614, 616, 626, 636, 645
  };
  boolean inBankingSequence = false;
  final int[] lootIds = {
    464, 220, 438, 439, 440, 441, 442, 443, 526, 527, 157, 158, 159, 160, 1277, 815, 817, 819, 821,
    823, 933, 40, 41, 42, 33, 31, 32, 34, 619, 38, 46
  };
  private double startxp = 0;
  private long start_time;
  int start_xp = 0;
  int autoEatHP = 0;
  long time = -1L;
  int withdrawAmount = 0;
  public final int areaX2 = 303;
  public final int areaY2 = 419;
  public final int areaX = 323;
  public final int areaY = 403;
  public final int bankX = 286;
  public final int bankY = 564;
  public final int bankX2 = 280;
  public final int bankY2 = 573;
  public final int bankFrontX = 288;
  public final int bankFrontY = 568;
  public final int bankFrontX2 = 287;
  public final int bankFrontY2 = 574;
  public int airs = 0;
  public int earths = 0;
  public int waters = 0;
  public int fires = 0;
  public int chaos = 0;
  public int bloods = 0;
  public int cosmics = 0;
  public int laws = 0;
  public int nats = 0;

  int foodid = 373;
  int bankEveryMinutes = 60;
  long lastBankTimeMark;

  public void init(String params) {

    Object[] fightModeOptions = {"Attack", "Strength", "Defense", "Controlled"};
    fightModeSelected =
        (String)
            JOptionPane.showInputDialog(
                null,
                "Fight mode",
                "Wiz",
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
    bankEveryMinutes =
        Integer.parseInt(
            JOptionPane.showInputDialog("Always bank every minutes: (set -1 to disable)"));
    start_time = System.currentTimeMillis();
    startxp += getAccurateXpForLevel(0);
    startxp += getAccurateXpForLevel(1);
    startxp += getAccurateXpForLevel(2);
    startxp += getAccurateXpForLevel(3);
    System.out.println("Must use food, at least 1 food per trip, will eat food for space");
    System.out.println("Start with 3 items & sleeping bag");
    System.out.println("Will deathwalk & withdraw new sleeping bag if you get pked");
    lastBankTimeMark = System.currentTimeMillis();
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

    if (getObjectIdFromCoords(287, 571) != 63) {

      atObject(287, 571);
      return true;
    }
    return false;
  }

  public void updateLootCount(int itemID) {
    if (itemID == 41) {
      chaos += getInventoryCount(itemID);
    } else if (itemID == 32) {
      waters += getInventoryCount(itemID);
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
    } else if (itemID == 46) {
      cosmics += getInventoryCount(itemID);
    } else if (itemID == 619) {
      bloods += getInventoryCount(itemID);
    }
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
    if (inArea(getX(), getY(), areaX, areaY, areaX2, areaY2)
        && getInventoryCount() != 30
        && hasInventoryItem(foodid)
        && hasInventoryItem(1263)
        && !bankingTime()) {

      if (!inCombat()) {
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
    } else if ((
        /* getInventoryCount() == 30 && */ !hasInventoryItem(1263) || !hasInventoryItem(foodid))
        || (inBankingSequence)
        || getY() >= 574
        || bankingTime()) {

      if (inArea(getX(), getY(), bankX, bankY, bankX2, bankY2)) {
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
        boolean YN = openBankDoors();
        if (YN) {

          return random(800, 1200);
        } else {

          walkTo(283, 567);
          return random(800, 1200);
        }
      }
      if (!inArea(getX(), getY(), bankFrontX, bankFrontY, bankFrontX2, bankFrontY2)
          && getY() <= 574) {

        return walkArrayPath(spotToBankX, spotToBankY);
      }

      if (!inArea(getX(), getY(), bankFrontX, bankFrontY, bankFrontX2, bankFrontY2)
          && getY() >= 574) {
        return walkArrayPathReverse(bankToLumX, bankToLumY);
      }

      // this handles walking to the wiz
    } else if (!inArea(getX(), getY(), areaX, areaY, areaX2, areaY2)
        && getInventoryCount() != 30
        && !bankingTime()) {

      if (inArea(getX(), getY(), bankX, bankY, bankX2, bankY2)) {
        boolean YN = openBankDoors();
        if (YN) {
          return random(800, 1200);
        } else {

          walkTo(288, 570);
          return random(800, 1200);
        }
      }

      if (!inArea(getX(), getY(), areaX, areaY, areaX2, areaY2) && getY() <= 574) {

        return walkArrayPathReverse(spotToBankX, spotToBankY);
      }
    }

    return 1000;
  }

  public boolean attack() {
    int[] p = _getReachableNpc(57, 60);
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

  public void paint() {
    long time = ((System.currentTimeMillis() - start_time) / 1000L);
    if (time < 1L) {
      time = 1L;
    }
    int x = 10;
    int y = 222;

    drawString("D_WildyDarkWiz", x, y, 1, 0xFFFFFF);
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
    if (bankEveryMinutes != -1) {
      y += 15;
      drawString("Minutes to next Bank: " + minutesToNextBank(), x, y, 1, 0xFFFFFF);
    }
    x = 315;
    y = 55;
    drawString("Banked runes", x, y, 1, 0xFFFFFF);
    y += 25;
    drawString("Airs: " + airs, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Waters: " + waters, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Earths: " + earths, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Fires: " + fires, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Chaos: " + chaos, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Laws: " + laws, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Nats: " + nats, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Cosmics: " + cosmics, x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Bloods: " + bloods, x, y, 1, 0xFFFFFF);
  }

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
    return 500;
  }

  // walks to furthest tile considering the max distance to the tile in
  // reverse order, put tiles x & y in the matching
  // indexes
  private int walkArrayPathReverse(int[] x, int[] y) {
    for (int i = 0; i < x.length; i++) {
      if (isReachable(x[i], y[i]) && distanceTo(x[i], y[i], getX(), getY()) < 25) {
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
        if (!inArea(x, y, areaX, areaY, areaX2, areaY2)) continue;
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
