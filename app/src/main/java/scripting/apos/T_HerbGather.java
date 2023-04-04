package scripting.apos;

import compatibility.apos.Script;
import javax.swing.*;

// import java.util.Date;

public class T_HerbGather extends Script {

  private int[] initial_xp;
  private long time;
  // private Weps weapid2;
  int loop = 0;
  int i = 0;
  final int[] herbs = {
    438, 439, 440, 441, 442, 443, 526, 527, 157, 158, 159, 160, 1277, 815, 817, 819, 821, 823, 933,
    40, 20, 33, 42
  };
  final int[] gathered = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
  int[] path = null;
  int staffid;
  int weapid; // 75,593, 594
  int wepid;

  public T_HerbGather(String e) {
    //		super(e);
  }

  public void init(String params) {
    // String[] p = params.trim().split(",");
    // this.weapid = Integer.parseInt(p[0]);
    initial_xp = new int[SKILL.length];
    System.out.println("Herb Collector - By Toichi");

    Weps Rune_Longsword = new Weps("Rune Longsword", 75);
    Weps Rune_Axe = new Weps("Rune Axe", 405);
    Weps Rune_two_h = new Weps("Rune 2-Handed Sword", 81);
    Weps Dragon_Longsword = new Weps("Dragon Longsword", 593);
    Weps Dragon_Axe = new Weps("Dragon Axe", 594);
    Weps[] wepopts = {Rune_Longsword, Rune_Axe, Rune_two_h, Dragon_Longsword, Dragon_Axe};
    String[] options = new String[wepopts.length];

    for (int mu = 0; mu < options.length; mu++) {
      options[mu] = wepopts[mu].name;
    }

    int option =
        JOptionPane.showOptionDialog(
            null,
            "Choose your weapon",
            "Toichi's Herb Gatherer",
            JOptionPane.OK_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[3]);
    this.weapid = wepopts[option].wep_id;
    // weapid = weapid2.wep_id;
  }

  public int main() {

    if (initial_xp[0] == 0) {
      for (int i = 0; i < SKILL.length; i++) {
        initial_xp[i] = getXpForLevel(i);
      }
      time = System.currentTimeMillis();
      return 500;
    }

    if (getFightMode() != 3) setFightMode(3);

    if (getFatigue() > 90) {
      useSleepingBag();
      return 3000;
    }

    int bone = getInventoryIndex(20);
    if (bone != -1 && !inCombat()) {
      useItem(bone);
      return random(600, 800);
    }

    if (getHpPercent() < 10) {
      stopScript();
    }

    // Depositing Herbs
    if (isBanking()) {
      i = 0;
      for (int h = 0; h < herbs.length - 3; h++) {
        if (getInventoryCount(herbs[h]) > 0) {
          if (h <= 12) {
            gathered[h] += getInventoryCount(herbs[h]);
          }
          deposit(herbs[h], getInventoryCount(herbs[h]));
          return random(1000, 1500);
        }
      }
      closeBank();
    }

    // Talk to Banker
    if (isAtApproxCoords(330, 552, 5) && getInventoryCount() == 30) {
      i = 0;
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

    // At Druids
    if (isAtApproxCoords(344, 3318, 5) && getInventoryCount() != 30) {
      // System.out.println("problem!");
      int[] druid = getNpcById(270);
      for (int herb : herbs) {
        int[] groundHerbs = getItemById(herb);
        if (groundHerbs[0] != -1
            && groundHerbs[1] >= 344 - 5
            && groundHerbs[1] <= 344 + 5
            && groundHerbs[2] >= 3318 - 5
            && groundHerbs[2] <= 3318 + 5) {
          pickupItem(groundHerbs[0], groundHerbs[1], groundHerbs[2]);
          return random(1000, 1500);
        }
      }

      if (druid[0] != -1 && !inCombat()) {
        attackNpc(druid[0]);
        return random(800, 1300);
      }
      return random(500, 800);
    }

    walk();
    return random(2000, 2500);
  }

  public int walk() {
    if (getInventoryCount() == 30) {
      if (getY() > 3000) {
        staffid = getInventoryIndex(102);
        wearItem(staffid);

        if (isItemEquipped(staffid)) {
          castOnSelf(18);
          System.out.println("Teleporting to Falador to bank");
          return random(400, 500);
        }
      }

      if (getY() < 549) {
        walkTo(327, 552);
        return random(400, 500);
      }

      if (getY() >= 548 && getX() < 328) {
        walkTo(327, 552);
        int[] bank_doors = getObjectById(64);
        if (bank_doors[0] != -1) {
          atObject(bank_doors[1], bank_doors[2]);
        }
        walkTo(329, 552);
        return random(400, 500);
      }

      walkTo(327, 552);
      int[] bank_doors = getObjectById(64);
      if (bank_doors[0] != -1) {
        atObject(bank_doors[1], bank_doors[2]);
      }
      walkTo(329, 552);
    } else {
      // inside fally
      if (isAtApproxCoords(334, 554, 5)) {
        walkTo(328, 552);
        return random(1000, 1500);
      }

      // inside fally
      if (getX() == 328 && getY() == 552) {
        walkTo(326, 552);
        return random(1000, 2000);
      }

      // inside fally
      if (getY() > 538 && getY() <= 558) {
        walkTo(316, 538);
        return random(1000, 1500);
      }

      // inside fally -> fally north gate
      if (getY() > 520 && getY() <= 538 && getX() < 350) {
        walkTo(315, 520);
        return random(1000, 1500);
      }

      // fally north gate -> halfway between tav gate
      if (getY() > 505 && getY() <= 520 && getX() <= 341) {
        walkTo(330, 505);
        return random(1000, 1500);
      }

      // halfway between tav gate -> tav gate
      if (getY() > 487 && getY() <= 505 && getX() < 341) {
        walkTo(341, 487);
        return random(1000, 1500);
      }

      // opens gate
      if (getX() == 341 && getY() == 487) {
        wepid = getInventoryIndex(weapid);
        wearItem(wepid);
        if (isItemEquipped(wepid)) {
          int[] gate = getObjectById(137);

          if (gate[0] != -1) {
            atObject(gate[1], gate[2]);
            return random(2500, 2600);
          }
        }
      }

      // Gate -> halfway to dungeon
      if (getY() >= 488 && getY() < 502 && getX() >= 342) {
        walkTo(356, 502);
        return random(1000, 1500);
      }

      // halfway to dungeon -> dungeon ladder
      if (getY() >= 502 && getX() >= 356 && getX() < 376 && getY() < 3000) {
        walkTo(376, 521);
        return random(1000, 1500);
      }

      // goes down dungeon ladder
      if (getX() == 376 && getY() == 521) {
        int[] dung = getObjectById(6);

        if (dung[0] != -1) {
          atObject(dung[1], dung[2]);
          return random(200, 300);
        }
      }

      // inside dungeon @ladder -> halfway point
      if (getY() > 3323 && getY() <= 3353 && getX() > 370) // 376, 3353
      {
        walkTo(376, 3323);
        return random(1000, 1500);
      }

      // halfway point -> druids
      if (getX() > 344 && getX() <= 376 && getY() <= 3330 && getY() > 3310) // 376, 3353
      {
        walkTo(344, 3318);
        return random(2000, 2500);
      }
    }
    return 1000;
  }

  @Override
  public void paint() {
    int x = 315;
    int y = 100;
    drawString("Toichi's Herb Gatherer", x - 4, y - 17, 4, 0xB20000);
    int[] xp = getXpStatistics(14);
    drawString("Ranarr: " + gathered[0] + " (" + perHour(gathered[0]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Irit: " + gathered[1] + " (" + perHour(gathered[1]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Avantoe: " + gathered[2] + " (" + perHour(gathered[2]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Kwuarm: " + gathered[3] + " (" + perHour(gathered[3]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        "Cadantine: " + gathered[4] + " (" + perHour(gathered[4]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        "Dwarf Weed: " + gathered[5] + " (" + perHour(gathered[5]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        "Key Half 1: " + gathered[6] + " (" + perHour(gathered[6]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        "Key Half 2: " + gathered[7] + " (" + perHour(gathered[7]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        "Uncut Diamond: " + gathered[8] + " (" + perHour(gathered[8]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        "Uncut Ruby: " + gathered[9] + " (" + perHour(gathered[9]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        "Uncut Emerald: " + gathered[10] + " (" + perHour(gathered[10]) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        "Uncut Sapphire: " + gathered[11] + " (" + perHour(gathered[11]) + "/h)",
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;
    drawString(
        "Left Shield Half: " + gathered[12] + " (" + perHour(gathered[12]) + "/h)",
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;

    drawString("Runtime: " + getRunTime(), x, y, 1, 0xFFFFFF);
    drawVLine(x - 7, 74, y + 3 - 74, 0xFFFFFF);
    drawHLine(x - 7, y + 3, 183, 0xFFFFFF);
  }

  private int perHour(int total) {
    long time = ((System.currentTimeMillis() - this.time) / 1000L);
    if (time < 1L) {
      time = 1L;
    }
    return ((int) ((total * 60L * 60L) / time));
  }

  private int[] getXpStatistics(int skill) {
    long time = ((System.currentTimeMillis() - this.time) / 1000L);
    if (time < 1L) {
      time = 1L;
    }
    int start_xp = initial_xp[skill];
    int current_xp = getXpForLevel(skill);
    int[] intArray = new int[4];
    intArray[0] = current_xp;
    intArray[1] = start_xp;
    intArray[2] = intArray[0] - intArray[1];
    intArray[3] = (int) ((((current_xp - start_xp) * 60L) * 60L) / time);
    return intArray;
  }

  private String getRunTime() {
    long millis = (System.currentTimeMillis() - time) / 1000;
    long second = millis % 60;
    long minute = (millis / 60) % 60;
    long hour = (millis / (60 * 60)) % 24;
    long day = (millis / (60 * 60 * 24));

    if (day > 0L) return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
    if (hour > 0L) return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
    if (minute > 0L) return String.format("%02d minutes, %02d seconds", minute, second);
    return String.format("%02d seconds", second);
  }

  public static class Weps {
    final String name;
    final int wep_id;

    public Weps(String name, int wep_id) {
      this.name = name;
      this.wep_id = wep_id;
    }
  }
}
