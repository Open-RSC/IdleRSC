package scripting.apos;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class Abyte0_TraverlyShop extends Abyte0_Script {
  int fMode = 2;

  int idVials = 465;
  int idEyes = 270;

  int vialsToBuy = 0;
  int eyesToBuy = 0;

  int idVialsWater = 464;

  String[] fModeName = {"Attack", "Defence", "Strength", "Controlled"};
  int[] fModeIdList = {2, 3, 1, 0};

  String[] cptVialsName = {"0", "5", "10", "14", "15", "19", "24", "29"};
  int[] cptVialsList = {0, 5, 10, 14, 15, 19, 24, 29};

  String[] cptEyesName = {"0", "5", "10", "14", "15", "19", "24", "29"};
  int[] cptEyesList = {0, 5, 10, 14, 15, 19, 24, 29};

  public Abyte0_TraverlyShop(String e) {
    super(e);
  }

  public void init(String params) {
    print("--");
    print("--");
    print("Abyte0 Traverly Shopper");
    print("Version 0");
    print("--");
    print("--");

    Frame frame = new Frame("Select Fighting Mode");
    String choiceF =
        (String)
            JOptionPane.showInputDialog(
                frame,
                "Fighting Mode:\n",
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
    print("fMode = " + fMode);
    print("--");

    Frame frameVials = new Frame("Select Cpt Vials");
    String choiceVials =
        (String)
            JOptionPane.showInputDialog(
                frameVials,
                "How many Vials:\n",
                "Vials Cpt Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                cptVialsName,
                null);
    for (int i = 0; i < cptVialsName.length; i++) {
      if (cptVialsName[i].equals(choiceVials)) {
        vialsToBuy = cptVialsList[i];
        break;
      }
    }
    print("Cpt Vials = " + vialsToBuy);
    print("--");

    Frame frameEyes = new Frame("Select Cpt Eyes");
    String choiceEyes =
        (String)
            JOptionPane.showInputDialog(
                frameEyes,
                "How many Eyes:\n",
                "Eyes Cpt Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                cptEyesName,
                null);
    for (int i = 0; i < cptEyesName.length; i++) {
      if (cptEyesName[i].equals(choiceEyes)) {
        eyesToBuy = cptEyesList[i];
        break;
      }
    }
    print("Cpt Eyes = " + eyesToBuy);
    print("--");
  }

  public int main() {
    if (getFightMode() != fMode) setFightMode(fMode);
    if (isQuestMenu()) {
      answer(0);
      return random(2200, 2400);
    }
    if (shopWindowOpen()) {
      // if we in shop we should buy what we need
      if (getInventoryCount(idVials) < vialsToBuy) {
        // we buy the vial missing
        buyItemIdFromShop(idVials, vialsToBuy - getInventoryCount(idVials));
        return random(1000, 1400);
      }
      if (getInventoryCount(idEyes) < eyesToBuy) {

        // we buy the vial missing
        // if we got room for all
        if (30 - getInventoryCount() >= eyesToBuy - getInventoryCount(eyesToBuy)) {
          buyItemIdFromShop(idEyes, eyesToBuy - getInventoryCount(idEyes));
          return random(1000, 1400);
        }
        // ELSE
        buyItemIdFromShop(idEyes, 30 - getInventoryCount());
      }

      // then we close the shop
      closeShop();
      return random(2000, 2500);
    }

    if (isBanking()) {
      // Deposit Eyes
      if (getInventoryCount(idEyes) > 0) {
        deposit(idEyes, getInventoryCount(idEyes));
        return random(1000, 1500);
      }

      // Deposit Water Vials
      if (getInventoryCount(idVialsWater) > 0) {
        deposit(idVialsWater, getInventoryCount(idVialsWater));
        return random(1000, 1500);
      }

      // if poor get money
      if (getInventoryCount(10) < 1000) {
        withdraw(10, 50000);
        return random(1000, 1500);
      }

      // Deposit Vials THIS SHOULD NOT HAPPEN
      if (getInventoryCount(idVials) > 0) {
        deposit(idVials, getInventoryCount(idVials));
        return random(1000, 1500);
      }

      closeBank();
      return random(1000, 1500);
    }

    // if we in shop we must full inventory
    if (getX() >= 366 && getX() <= 370 && getY() >= 502 && getY() <= 510) {
      // if we got money and room
      if (getInventoryCount() < 30 && getInventoryCount(10) > 10) {
        // if we not in shop we should talk jatix
        if (!shopWindowOpen()) {

          int shopNpc[] = getNpcByIdNotTalk(new int[] {230});
          if (shopNpc[0] != -1) {
            talkToNpc(shopNpc[0]);

            // long wait because jatix is outside
            if (shopNpc[1] >= 366 && shopNpc[1] <= 370 && shopNpc[2] >= 502 && shopNpc[2] <= 510)
              return random(3000, 4000);
            else return random(12000, 15000);
          }
        }
        return random(600, 1000);
      } else {
        // we need to open door and get outside

        // if we next to door
        if (getX() == 370 && getY() == 506) {
          int[] doors = getWallObjectById(2);
          if (isAtApproxCoords(doors[1], doors[2], 3)) {
            // on ouvre la porte
            atWallObject(doors[1], doors[2]);
            return random(1000, 2000);
          }

          // lets go out
          walkTo(371, 506);

          return random(800, 1400);
        }

        // else we have to walk to door
        walkTo(370, 506);
        return random(800, 1400);
      }
    }
    // If have less than 25 items and have money we want to shop
    if (getInventoryCount() <= 5 && getInventoryCount(10) >= 100) {
      // if we on f2p side
      if (getX() <= 341) {
        // if in front of gate we pass it
        if (getX() == 341 && getY() == 487) {
          atObject(341, 487);
          return random(1502, 2000);
        }

        // if in bank
        if (getX() >= 328 && getX() <= 334 && getY() >= 549 && getY() <= 557) {
          // if in front of bank door (inside)
          if (getX() == 328 && getY() == 552) {
            int[] doors = getObjectById(64);
            if (isAtApproxCoords(doors[1], doors[2], 3)) {
              atObject(doors[1], doors[2]);
              return random(1000, 2000);
            }
            walkTo(327, 552);
            return random(800, 1400);
          }
          // lets walk to door if not already
          walkTo(328, 552);
          return random(800, 1400);
        }

        // walking to fountain
        if (getY() > 546) {
          walkTo(326, 546);
          return random(800, 1400);
        }

        // walking behind shop
        if (getY() > 533) {
          walkTo(323, 533);
          return random(800, 1400);
        }

        // walking Border City
        if (getY() > 520) {
          walkTo(317, 520);
          return random(800, 1400);
        }

        // walking Out Of City
        if (getY() > 507) {
          walkTo(324, 507);
          return random(800, 1400);
        }

        // walking Almost At Gate
        if (getY() > 495) {
          walkTo(332, 495);
          return random(800, 1400);
        }

        // ELSE walking to Gate
        walkTo(341, 487);
        return random(800, 1400);
      }
      // else we on p2p side
      else {
        // if front of jatix door lets open it
        if (getX() >= 371) {
          // if we next to door
          if (getX() == 371 && getY() == 506) {
            int[] doors = getWallObjectById(2);
            if (isAtApproxCoords(doors[1], doors[2], 3)) {
              // on ouvre la porte
              atWallObject(doors[1], doors[2]);
              return random(1000, 2000);
            }

            // lets go out
            walkTo(370, 506);

            return random(800, 1400);
          }

          // else we have to walk to door
          walkTo(371, 506);
          return random(800, 1400);
        }

        // walking near druid
        if (getX() < 353) {
          walkTo(353, 497);
          return random(800, 1400);
        }

        // walking side shop
        if (getX() < 365) {
          walkTo(365, 499);
          return random(800, 1400);
        }

        // walking side shop
        if (getX() < 371) {
          walkTo(371, 506);
          return random(800, 1400);
        }
      }
    }
    // else we have to go near fountain first then look if we fill vial or bank
    else {
      // if we on f2p side
      if (getX() <= 341) {
        // if in front of bank we open and enter (we outside)
        if (getX() == 327 && getY() == 552) {
          int[] doors = getObjectById(64);
          if (isAtApproxCoords(doors[1], doors[2], 3)) {
            atObject(doors[1], doors[2]);
            return random(1000, 2000);
          }
          walkTo(328, 552);
          return random(800, 1400);
        }

        // if in bank
        if (getX() >= 328 && getX() <= 334 && getY() >= 549 && getY() <= 557) {
          if (isQuestMenu()) {
            System.out.println("Bank Answer");
            answer(0);
            return random(2000, 2100);
          }

          if (!isBanking()) {
            int banker[] = getNpcByIdNotTalk(new int[] {95});
            if (banker[0] != -1 && !isBanking()) {
              talkToNpc(banker[0]);
              return random(2000, 3000);
            }
          }
          return 500;
        }

        // walking Almost At Gate
        if (getY() < 495) {
          walkTo(332, 495);
          return random(800, 1400);
        }

        // walking Out Of City
        if (getY() < 507) {
          walkTo(324, 507);
          return random(800, 1400);
        }

        // walking Border City
        if (getY() < 520) {
          walkTo(317, 520);
          return random(800, 1400);
        }

        // walking behind shop
        if (getY() < 533) {
          walkTo(323, 533);
          return random(800, 1400);
        }

        // walking to fountain
        if (getY() < 546) {
          walkTo(326, 546);
          return random(800, 1400);
        }

        // if we have vial we fill them
        if (getInventoryCount(idVials) > 0) {
          // credit to OHDFallyVials for those 4 lines
          int[] Fountain = getObjectById(26);
          if (isAtApproxCoords(327, 546, 5)) {
            useItemOnObject(idVials, Fountain[0]);
            return random(600, 700);
          }
        }

        // else we walk front of bank
        walkTo(327, 552);
        return random(800, 1400);
      }

      // else we on p2p side
      else {
        // if in front of gate we pass it
        if (getX() == 342 && getY() == 488) {
          atObject(341, 487);
          return random(1502, 2000);
        }

        // if front of jatix door lest walk to SIDE SHOP
        if (getX() > 371) {
          walkTo(371, 506);
          return random(800, 1400);
        }

        // walking side shop
        if (getX() > 365) {
          walkTo(365, 499);
          return random(800, 1400);
        }

        // walking near druid
        if (getX() > 353) {
          walkTo(353, 497);
          return random(800, 1400);
        }

        // walking to gate
        walkTo(342, 488);
        return random(800, 1400);
      }
    }
    print("Unreachable TODO");
    return 500;
  }
}
