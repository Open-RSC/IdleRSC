package scripting.apos;

import java.awt.*;
import javax.swing.*;

/**
 * * This script is used to cook your food in catherby. * start at catherby bank, change the rawID
 * and cookedID and burntID varables to cook whatever you want. * * v 1.5 * - yomama` edited by
 * XcendroX Edited by Abyte0
 */
// Version 2.0 Updated to OpenRSC 2012-07-02
// Version 2.1 Integrate Shark 2012-07-02
// Version 2.3 Integrate Menu 2012-07-03
// Version 2.4 Drop differently to avoid drop cooked fish
public class Abyte0_CatherbyCooker extends Abyte0_Script {
  int rawID = 366;
  // enter raw fishies ID
  int cookedID = 367;
  // enter cooked fishies ID
  int burntID = 368;
  // enter burnt fishies ID

  final String tunasParam = "T";
  final String lobstersParam = "L";
  final String swordfishsParam = "S";
  final String sharksParam = "Shark";

  final int[] Tunas = new int[] {366, 367, 368};
  // 366: Raw Tuna
  // 367: Tuna
  // 368: Burnt fish

  final int[] Swordfishs = new int[] {369, 370, 371};
  // 369: Raw Swordfish
  // 370: Swordfish
  // 371: Burnt Swordfish

  final int[] Lobsters = new int[] {372, 373, 374};
  // 372: Raw Lobster
  // 373: Lobster
  // 374: Burnt Lobster

  final int[] Sharks = new int[] {545, 546, 547};
  // 545: Raw Shark
  // 546: Shark
  // 547: Burnt Shark

  final int[] cookArea = new int[] {435, 485};
  final int[] bankArea = new int[] {439, 495};

  public Abyte0_CatherbyCooker(String e) {
    super(e);
  }

  public void init(String params) {

    print(
        "Default Selector, for quick setup set parameter: T = Tunas L = Lobs, S = Swordys, Shark = Sharks");
    if (!params.equals(tunasParam)
        && !params.equals(lobstersParam)
        && !params.equals("S")
        && !params.equals("Shark")) {
      Frame frame = new Frame("Fish selector");
      Object[] fishes = {"Tunas", "Lobsters", "Swordfishs", "Sharks"};
      String S_FightMode =
          (String)
              JOptionPane.showInputDialog(
                  frame,
                  "Fish selector:\n",
                  "Types",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  fishes,
                  null);

      if (S_FightMode.equals("Tunas")) params = tunasParam;
      if (S_FightMode.equals("Lobsters")) params = lobstersParam;
      if (S_FightMode.equals("Swordfishs")) params = swordfishsParam;
      if (S_FightMode.equals("Sharks")) params = sharksParam;
    }

    if (params.equals(tunasParam)) {
      rawID = Tunas[0];
      cookedID = Tunas[1];
      burntID = Tunas[2];
      print("Doing Tunas");
    } else if (params.equals(lobstersParam)) {
      rawID = Lobsters[0];
      cookedID = Lobsters[1];
      burntID = Lobsters[2];
      print("Doing Lobsters");
    } else if (params.equals(swordfishsParam)) {
      rawID = Swordfishs[0];
      cookedID = Swordfishs[1];
      burntID = Swordfishs[2];
      print("Doing Swordfishs");
    } else if (params.equals(sharksParam)) {
      rawID = Sharks[0];
      cookedID = Sharks[1];
      burntID = Sharks[2];
      print("Doing Sharks");
    }

    print("Version 2.4 Open RSC + Integrate Shark + Menu + New drop order");
  }

  public int main() {
    if (getFatigue() > 80) {
      useSleepingBag();
      return 1000;
    }
    if (isBanking()) {
      if (getInventoryCount(cookedID) != 0) {
        deposit(cookedID, getInventoryCount(cookedID));
        return random(800, 1000);
      }
      if (getInventoryCount() == 30) {
        closeBank();
        return random(500, 600);
      }
      withdraw(rawID, 30 - getInventoryCount());
      return random(600, 800);
    }
    if (isQuestMenu()) {
      answer(0);
      return random(1000, 1500);
    }
    if (getInventoryCount(rawID) != 0) {
      if (distanceTo(cookArea[0], cookArea[1]) < 10) {
        // if we burnt fishies, they will be dropped
        if (getInventoryCount(burntID) > 0) {
          return dropItemIdOrWait(burntID);
        }

        useItemOnObject(rawID, 11);
        return random(1500, 2000);
      }
      walkTo(cookArea[0], cookArea[1]);
      return random(11000, 13000);
    }

    if (distanceTo(bankArea[0], bankArea[1]) < 10) {
      int[] banker = getNpcByIdNotTalk(BANKERS);
      if (banker[0] != -1) talkToNpc(banker[0]);
      return 2500;
    } else {
      walkTo(bankArea[0], bankArea[1]);
      if (getFatigue() > 10) {
        useSleepingBag();
        return 1000;
      }
    }
    return random(800, 1500);
  }

  public final void print(String gameText) {
    System.out.println(gameText);
  }
}
