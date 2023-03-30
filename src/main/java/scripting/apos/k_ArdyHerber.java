package scripting.apos;

import compatibility.apos.Script;
import java.awt.*;
import javax.swing.*;

public class k_ArdyHerber extends Script {

  private int[] initial_xp;
  private long time;
  int loop = 0;
  int i = 0;
  int j = 0;
  final int[] herbs = {
    31, 33, 34, 40, 42, 157, 158, 159, 160, 438, 439, 440, 441, 442, 443, 448, 449, 450, 451, 452,
    453, 469, 526, 527, 1277, 458, 483
  };
  final int[] extras = {
    10, 35, 36, 454, 455, 456, 457, 459, 460, 461, 462, 463, 464, 465, 483, 981, 1026
  };
  final int[] gathered = {
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
  };
  final int[] bones = {20};
  int[] path = null;
  int fMode = 2;

  private final String[] fModeName = {"Attack", "Defence", "Strength", "Controlled"};
  private final int[] fModeIdList = {2, 3, 1, 0};

  public k_ArdyHerber(String e) {
    //		super(e);
  }

  public void init(String params) {
    initial_xp = new int[SKILL.length];
    Frame frame = new Frame("Select Fighting Mode");
    String choiceF =
        (String)
            JOptionPane.showInputDialog(
                frame,
                "Combat Style:\n",
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
  }

  public int main() {
    if (initial_xp[0] == 0) {
      for (int i = 0; i < SKILL.length; i++) {
        initial_xp[i] = getXpForLevel(i);
      }
      time = System.currentTimeMillis();
      return 500;
    }
    if (getFightMode() != fMode) setFightMode(fMode);
    if (getFatigue() > 90) {
      useSleepingBag();
      return 3000;
    }
    if (isBanking()) {
      for (int h = 0; h < herbs.length; h++) {
        if (getInventoryCount(herbs[h]) > 0) {
          if (h <= 27) {
            gathered[h] += getInventoryCount(herbs[h]);
          }
          deposit(herbs[h], getInventoryCount(herbs[h]));
        }
      }
      int[] crapHerbs = {165, 435, 436, 437, 444, 445, 446, 447};
      for (int crapHerb : crapHerbs) {
        if (getInventoryCount(crapHerb) > 0) {
          deposit(crapHerb, getInventoryCount(crapHerb));
        }
      }
      for (int extra : extras) {
        if (getInventoryCount(extra) > 0) {
          deposit(extra, getInventoryCount(extra));
        }
      }
      closeBank();
      j = 0;
      return random(1200, 1400);
    }
    if (isAtApproxCoords(581, 574, 5) && getInventoryCount() == 30) {
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
    int herblevel = getLevel(15);
    int ran = getInventoryIndex(438);
    int iri = getInventoryIndex(439);
    int ava = getInventoryIndex(440);
    int kwu = getInventoryIndex(441);
    int cad = getInventoryIndex(442);
    int dwa = getInventoryIndex(443);
    if (isAtApproxCoords(617, 558, 2) && getInventoryCount() != 30) {
      int[] door = getWallObjectById(96);
      if (door[0] != -1) atWallObject2(door[1], door[2]);
      j = 0;
      return random(1000, 1500);
    }
    if (isAtApproxCoords(617, 552, 3) && getInventoryCount() == 30) {
      int[] door = getWallObjectById(96);
      int bone = getInventoryIndex(20);
      int vial = getInventoryIndex(464);
      int rannar = getInventoryIndex(448);
      int snape = getInventoryIndex(469);
      int rannarPot = getInventoryIndex(458);
      int gua = getInventoryIndex(165);
      int limpwurt = getInventoryIndex(220);
      int mar = getInventoryIndex(435);
      int tar = getInventoryIndex(436);
      int har = getInventoryIndex(437);
      if (bone != -1 && !inCombat()) {
        useItem(bone);
        return random(800, 1000);
      }
      int haveCrapHerbID = getInventoryIndex(444, 445, 446, 447);
      if (haveCrapHerbID != -1 && !inCombat() && herblevel >= 38) {
        dropItem(haveCrapHerbID);
        return random(800, 1000);
      }
      if (vial != -1 && !inCombat() && hasInventoryItem(448) && herblevel >= 30) {
        useItemWithItem(vial, rannar);
        return random(800, 1000);
      }
      if (rannarPot != -1 && !inCombat() && hasInventoryItem(469) && herblevel >= 38) {
        useItemWithItem(rannarPot, snape);
        return random(400, 700);
      }
      if (herblevel >= 3 && !inCombat() && getInventoryCount(165) != 0) {
        useItem(gua);
        return random(800, 1000);
      }
      if (herblevel >= 5 && !inCombat() && getInventoryCount(435) != 0) {
        useItem(mar);
        return random(800, 1000);
      }
      if (herblevel >= 11 && !inCombat() && getInventoryCount(436) != 0) {
        useItem(tar);
        return random(800, 1000);
      }
      if (herblevel >= 20 && !inCombat() && getInventoryCount(437) != 0) {
        useItem(har);
        return random(800, 1000);
      }
      if (herblevel >= 25 && !inCombat() && getInventoryCount(438) != 0) {
        useItem(ran);
        return random(800, 1000);
      }
      if (herblevel >= 40 && !inCombat() && getInventoryCount(439) != 0) {
        useItem(iri);
        return random(800, 1000);
      }
      if (herblevel >= 48 && !inCombat() && getInventoryCount(440) != 0) {
        useItem(ava);
        return random(800, 1000);
      }
      if (herblevel >= 54 && !inCombat() && getInventoryCount(441) != 0) {
        useItem(kwu);
        return random(800, 1000);
      }
      if (herblevel >= 65 && !inCombat() && getInventoryCount(442) != 0) {
        useItem(cad);
        return random(800, 1000);
      }
      if (herblevel >= 70 && !inCombat() && getInventoryCount(443) != 0) {
        useItem(dwa);
        return random(800, 1000);
      }
      if (door[0] != -1) {
        atWallObject(door[1], door[2]);
        return random(800, 1200);
      }
      return 1000;
    }
    if (isAtApproxCoords(617, 552, 3) && getInventoryCount() != 30) {
      int[] druid = getNpcById(270);
      int bone = getInventoryIndex(20);
      int haveCrapHerb = getInventoryIndex(165, 435, 436, 437);
      int haveCrapHerbID = getInventoryIndex(444, 445, 446, 447);
      int vial = getInventoryIndex(464);
      int rannar = getInventoryIndex(448);
      int snape = getInventoryIndex(469);
      int rannarPot = getInventoryIndex(458);
      int gua = getInventoryIndex(165);
      int limpwurt = getInventoryIndex(220);
      int mar = getInventoryIndex(435);
      int tar = getInventoryIndex(436);
      int har = getInventoryIndex(437);
      if (druid[0] != -1 && !inCombat()) {
        attackNpc(druid[0]);
        return random(30, 100);
      }
      if (herblevel >= 3 && !inCombat() && getInventoryCount(165) != 0) {
        useItem(gua);
        return random(800, 1000);
      }
      if (herblevel >= 5 && !inCombat() && getInventoryCount(435) != 0) {
        useItem(mar);
        return random(800, 1000);
      }
      if (herblevel >= 11 && !inCombat() && getInventoryCount(436) != 0) {
        useItem(tar);
        return random(800, 1000);
      }
      if (herblevel >= 20 && !inCombat() && getInventoryCount(437) != 0) {
        useItem(har);
        return random(800, 1000);
      }
      if (herblevel >= 25 && !inCombat() && getInventoryCount(438) != 0) {
        useItem(ran);
        return random(800, 1000);
      }
      if (herblevel >= 40 && !inCombat() && getInventoryCount(439) != 0) {
        useItem(iri);
        return random(800, 1000);
      }
      if (herblevel >= 48 && !inCombat() && getInventoryCount(440) != 0) {
        useItem(ava);
        return random(800, 1000);
      }
      if (herblevel >= 54 && !inCombat() && getInventoryCount(441) != 0) {
        useItem(kwu);
        return random(800, 1000);
      }
      if (herblevel >= 65 && !inCombat() && getInventoryCount(442) != 0) {
        useItem(cad);
        return random(800, 1000);
      }
      if (herblevel >= 70 && !inCombat() && getInventoryCount(443) != 0) {
        useItem(dwa);
        return random(800, 1000);
      }
      for (int herb : herbs) {
        int[] groundHerbs = getItemById(herb);
        int[] groundBones = getItemById(bones);
        int[] crapHerbs = getItemById(165, 435, 436, 437);
        int[] groundVial = getItemById(464);
        if (getY() - groundBones[2] < 7 && groundBones[2] - getY() < 7) {
          if (groundBones[0] != -1 && !inCombat() && countPlayers() < 2) {
            pickupItem(groundBones[0], groundBones[1], groundBones[2]);
            return random(800, 900);
          }
        }
        if (groundHerbs[0] != -1 && !inCombat()) {
          pickupItem(groundHerbs[0], groundHerbs[1], groundHerbs[2]);
          return random(800, 900);
        }
        if (crapHerbs[0] != -1 && !inCombat() && countPlayers() < 2) {
          pickupItem(crapHerbs[0], crapHerbs[1], crapHerbs[2]);
          return random(800, 900);
        }
        if (groundVial[0] != -1
            && !inCombat()
            && hasInventoryItem(448)
            && countPlayers() < 2
            && (herblevel >= 38)) {
          pickupItem(groundVial[0], groundVial[1], groundVial[2]);
          return random(300, 600);
        }
      }
      if (bone != -1 && !inCombat()) {
        useItem(bone);
        return random(800, 900);
      }
      if (haveCrapHerb != -1 && !inCombat()) {
        useItem(haveCrapHerb);
        return random(400, 700);
      }
      if (haveCrapHerbID != -1 && !inCombat() && herblevel >= 38) {
        dropItem(haveCrapHerbID);
        return random(400, 700);
      }
      if (vial != -1 && !inCombat() && hasInventoryItem(448) && herblevel >= 30) {
        useItemWithItem(vial, rannar);
        return random(400, 700);
      }
      if (rannarPot != -1 && !inCombat() && hasInventoryItem(469) && herblevel >= 38) {
        useItemWithItem(rannarPot, snape);
        return random(400, 700);
      }
      return random(500, 800);
    }
    walk();
    return random(2000, 2500);
  }

  @Override
  public void paint() {
    int x = 320;
    int y = 46;
    int current_hxp = getXpForLevel(skillName("herblaw"));
    int start_hxp = initial_xp[skillName("herblaw")];
    int current_pxp = getXpForLevel(skillName("prayer"));
    int start_pxp = initial_xp[skillName("prayer")];
    int current_axp = getXpForLevel(skillName("attack"));
    int start_axp = initial_xp[skillName("attack")];
    int current_sxp = getXpForLevel(skillName("strength"));
    int start_sxp = initial_xp[skillName("strength")];
    int current_dxp = getXpForLevel(skillName("defense"));
    int start_dxp = initial_xp[skillName("defense")];
    int current_cxp = getXpForLevel(skillName("hits"));
    int start_cxp = initial_xp[skillName("hits")];
    int herblevel = getLevel(15);
    drawString("kRiStOf'S Ardy Herber", x - 1, y, 4, 0x1E90FF);
    y += 15;
    drawString(
        "Air: " + gathered[0] + " Nat: " + gathered[1] + " Law: " + gathered[2], x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        "Sa: "
            + gathered[6]
            + " Em: "
            + gathered[5]
            + " Ru: "
            + gathered[4]
            + " Di: "
            + gathered[3],
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;
    drawString("Tooth: " + gathered[20] + " Loop: " + gathered[21], x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Drag Shield: " + gathered[22], x, y, 1, 0xFFFFFF);
    y += 15;
    if (herblevel < 38) {
      drawString(
          "Snape Grass: " + gathered[19] + " (" + perHour(gathered[19]) + "/h)", x, y, 1, 0xFFFFFF);
      y += 15;
    }
    drawString(
        "Rannar: "
            + (gathered[7] + gathered[13])
            + " ("
            + perHour(gathered[7] + gathered[13])
            + "/h)",
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;
    drawString(
        "Irit: "
            + (gathered[8] + gathered[14])
            + " ("
            + perHour(gathered[8] + gathered[14])
            + "/h)",
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;
    drawString(
        "Avantoe: "
            + (gathered[9] + gathered[15])
            + " ("
            + perHour(gathered[9] + gathered[15])
            + "/h)",
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;
    drawString(
        "Kwuam: "
            + (gathered[10] + gathered[16])
            + " ("
            + perHour(gathered[10] + gathered[16])
            + "/h)",
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;
    drawString(
        "Cadantine: "
            + (gathered[11] + gathered[17])
            + " ("
            + perHour(gathered[11] + gathered[17])
            + "/h)",
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;
    drawString(
        "Dwarf Weed: "
            + (gathered[12] + gathered[18])
            + " ("
            + perHour(gathered[12] + gathered[18])
            + "/h)",
        x,
        y,
        1,
        0xFFFFFF);
    y += 15;
    if (herblevel >= 38) {
      drawString(
          "Pray Pot(U|F): "
              + (gathered[23])
              + "|"
              + (gathered[24])
              + " ("
              + perHour(gathered[23])
              + "|"
              + perHour(gathered[24])
              + "/h)",
          x,
          y,
          1,
          0xFFFFFF);
      y += 15;
    }
    if (countPlayers() < 2) {
      drawString("Alone, Get Extra's", x, y, 1, 0x1E90FF);
    }
    if (countPlayers() >= 2) {
      drawString("Not Alone, Don't Get Extra's", x, y, 1, 0xFF0000);
    }
    y += 15;
    drawString(
        "Prayer("
            + (getLevel(5))
            + ") XP: "
            + (current_pxp - start_pxp)
            + " ("
            + getXpH("prayer")
            + "/h)",
        x,
        y,
        1,
        0x1E90FF);
    y += 15;
    drawString(
        "Herblaw("
            + (getLevel(15))
            + ") XP: "
            + (current_hxp - start_hxp)
            + " ("
            + getXpH("herblaw")
            + "/h)",
        x,
        y,
        1,
        0x1E90FF);
    y += 15;
    if (getFightMode() == 0) {
      drawString(
          "Controlled XP: " + (current_cxp - start_cxp) + " (" + getXpH("hits") + "/h)",
          x,
          y,
          1,
          0x1E90FF);
    }
    if (getFightMode() == 1) {
      drawString(
          "Strength("
              + (getLevel(2))
              + ") XP: "
              + (current_sxp - start_sxp)
              + " ("
              + getXpH("strength")
              + "/h)",
          x,
          y,
          1,
          0x1E90FF);
    }
    if (getFightMode() == 2) {
      drawString(
          "Attack("
              + (getLevel(0))
              + ") XP: "
              + (current_axp - start_axp)
              + " ("
              + getXpH("attack")
              + "/h)",
          x,
          y,
          1,
          0x1E90FF);
    }
    if (getFightMode() == 3) {
      drawString(
          "Defense("
              + (getLevel(1))
              + ") XP: "
              + (current_dxp - start_dxp)
              + " ("
              + getXpH("defense")
              + "/h)",
          x,
          y,
          1,
          0x1E90FF);
    }
    y += 15;
    drawString("Runtime: " + getRunTime(), x, y, 1, 0x1E90FF);
    drawVLine(x - 7, 36, y - 32, 0x1E90FF);
    drawHLine(x - 7, y + 3, 196, 0x1E90FF);
  }

  private int perHour(int total) {
    long time = ((System.currentTimeMillis() - this.time) / 1000L);
    if (time < 1L) {
      time = 1L;
    }
    return ((int) ((total * 60L * 60L) / time));
  }

  private String getRunTime() {
    long millis = (System.currentTimeMillis() - time) / 1000;
    long second = millis % 60;
    long minute = (millis / 60) % 60;
    long hour = (millis / (60 * 60)) % 24;
    long day = (millis / (60 * 60 * 24));

    if (day > 0L) return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
    if (hour > 0L) return String.format("%02d hrs, %02d mins, %02d secs", hour, minute, second);
    if (minute > 0L) return String.format("%02d mins, %02d secs", minute, second);
    return String.format("%02d seconds", second);
  }

  private int getXpH(String skill) {
    long start_xp = initial_xp[skillName(skill)];
    long current_xp = getXpForLevel(skillName(skill));
    try {
      int xph =
          (int)
              ((((current_xp - start_xp) * 60L) * 60L)
                  / ((System.currentTimeMillis() - time) / 1000L));
      return xph;
    } catch (ArithmeticException e) {
    }
    return 0;
  }

  public int skillName(String s) {
    for (int i = 0; i <= 17; i++) if (SKILL[i].equalsIgnoreCase(s)) return i;
    return -1;
  }

  public int walk() {
    if (isAtApproxCoords(581, 574, 8) && getInventoryCount() != 30) {
      path = new int[] {581, 574, 590, 578, 588, 598, 600, 596, 603, 582, 609, 565, 617, 558};
      System.out.println("first path"); // /////////////////////////////////////////////////////////
      System.out.println("j = " + j); // /////////////////////////////////////////////////////////
    }
    if (isAtApproxCoords(617, 558, 2) && getInventoryCount() == 30) {
      path = new int[] {617, 558, 609, 565, 603, 582, 600, 596, 588, 598, 590, 578, 581, 574};
      System.out.println("second path"); // ///////////////////////////////////////////////////////
      System.out.println("j = " + j); // /////////////////////////////////////////////////////////
    }
    if ((j + 1) < path.length) {
      if (isAtApproxCoords(path[j], path[j + 1], 2)) j = j + 2;
      walkTo(path[j] + random(-1, 1), path[j + 1] + random(-1, 1));
      System.out.println(
          "j = "
              + j
              + ", walking to "
              + path[j]
              + ", "
              + path[j + 1]); // /////////////////////////////////
      return random(1200, 1500);
    }
    loop++;
    if (loop > 5) walkTo(path[j], path[j + 1]);
    System.out.println("loop");
    return 1000;
  }
}
