package scripting.apos;

import java.awt.*;
import javax.swing.*;

/**
 * Mine Mithril and Coal in mining guild <br>
 * Coleslaw has Runite and Addy so this script is not recommended<br>
 */
public final class Abyte0_GuildMiner extends Abyte0_Script {
  int oreIron = 151; // Iron ore
  int oreCoal = 155; // Coal ore
  int oreMith = 153; // Mith ore

  int RockCoal2 = 110; // Coal rock/ EAST
  int RockCoal = 111; // Coal rock WEST
  final int RockMith = 107; // Mith rock

  boolean mith = false;
  // Extension myReference = null;
  public Abyte0_GuildMiner(String e) {
    super(e);
    // myReference = e;
  }

  public void init(String params) {
    print("Started Abyte0 Guild Miner");
    print("Version 1.1");
    // Version 0
    // 4  Ways doors opener
    // Version 1
    // Coal only mining OR Mith then Coal mining
    // based on old free guild mining script made in 2009

    Frame frame = new Frame("Ores Selection");
    Object[] fightModes = {"Coal", "Mith"};
    String S_FightMode =
        (String)
            JOptionPane.showInputDialog(
                frame,
                "Rock/Ore:\n",
                "Ore Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                fightModes,
                null);
    if (S_FightMode.equals("Mith")) {
      mith = true;
      print("Mining Mith then Coal...");
    } else {
      mith = false;
      print("Mining Only Coal...");
    }

    Frame frameS = new Frame("Spot Selection");
    Object[] spotName = {"Ladder Side First", "Far From Ladder First"};
    String choiceSpot =
        (String)
            JOptionPane.showInputDialog(
                frameS,
                "Spot:\n",
                "Spot Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                spotName,
                null);
    if (choiceSpot.equals(spotName[1])) {
      RockCoal2 = 111; // Coal rock/ EAST
      RockCoal = 110; // Coal rock WEST
      print("Spot: " + spotName[1]);
    } else {
      RockCoal2 = 110; // Coal rock/ EAST
      RockCoal = 111; // Coal rock WEST
      print("Spot: " + spotName[0]);
    }

    print("Script loaded!");
  }

  public int main() {
    int[] doorObj;
    int[] stairs;
    if (getFightMode() != 2) {
      setFightMode(2);
    }
    if (getFatigue() > 80) {
      useSleepingBag();
      return 1000;
    }
    if (isQuestMenu()) {
      System.out.println("Bank Answer");
      answer(0);
      return random(2000, 2100);
    }
    if (isBanking()) {
      if (!depositGems()) // lol don't judge me
      {
        return 300;
      }
    }
    if (getInventoryCount() < 30) {
      if (!isAtApproxCoords(274, 3396, 30)) {
        if (getX() == 286 && getY() == 571) {
          // Si a coter de la porte a linterieur
          print("Step Outside Bank");
          atObject(287, 571);
          walkTo(287, 571);
          return random(121, 3500);
        }
        if (getX() >= 280 && getX() <= 286 && getY() >= 564 && getY() <= 573) {
          // Si dans la banque
          // print("Walking to Door");
          walkTo(286, 571);
          return random(240, 2500);
        }
        if (getX() == 274 && getY() == 562) {
          // Si a lexterieur de la House
          doorObj = getWallObjectById(2);
          if (doorObj[0] != -1) {
            if (isAtApproxCoords(doorObj[1], doorObj[2], 5)) ;
            atWallObject(doorObj[1], doorObj[2]);
          }
          print("Step Inside House");
          walkTo(274, 563);
          return random(100, 1500);
        }
        if (getX() >= 272 && getX() <= 277 && getY() >= 563 && getY() <= 567) {
          // print("Step to Ladder");
          stairs = getObjectById(223);
          if (stairs[0] != -1) {
            atObject(stairs[1], stairs[2]);
            return random(800, 900);
          }
        }
        walkTo(274, 562);
        return random(100, 1500);
      } else {
        if (mith) {
          int nombre = mineOre(RockMith);
          if (nombre > 0) return random(300, 601);
        }
        // SINON si aucun mith proche
        int nombre = mineOre(RockCoal);
        if (nombre > 0) return random(301, 605);
        nombre = mineOre(RockCoal2);
        if (nombre > 0) return random(301, 605);
        return random(800, 900);
      }
    } else {
      if (isAtApproxCoords(267, 3394, 9)) {
        // Si downstair
        stairs = getObjectById(5);
        if (stairs[0] != -1) {
          if (isAtApproxCoords(stairs[1], stairs[2], 16)) {
            atObject(stairs[1], stairs[2]);
            return random(800, 900);
          }
        } else {
          print("Where the fuck is the ladder lol?");
          walkTo(272, 3396);
          return random(800, 900);
        }
      }
      if (getX() == 274 && getY() == 563) {
        // Si a linterieur de la House
        print("Step Outside House");
        doorObj = getWallObjectById(2);
        if (doorObj[0] != -1) {
          if (isAtApproxCoords(doorObj[1], doorObj[2], 5)) ;
          atWallObject(doorObj[1], doorObj[2]);
        }
        walkTo(274, 562);
        return random(100, 1500);
      }
      if (getX() >= 272 && getX() <= 277 && getY() >= 563 && getY() <= 567) {
        // Si dans la House
        // print("Walk to House Door");
        walkTo(274, 563);
        return random(102, 1500);
      }
      if (getX() >= 280 && getX() <= 286 && getY() >= 564 && getY() <= 573) {
        // Si dans la banque
        print("Talking to Banker");
        if (!isBanking()) {
          int[] banker = getNpcByIdNotTalk(95);
          if (banker[0] != -1 && !isBanking()) {
            talkToNpc(banker[0]);
            return random(2000, 3000);
          }
        }
        return random(631, 1500);
      }
      if (getX() == 287 && getY() == 571) {
        // Si a coter de la porte a exterieur
        print("Step Inside Bank");
        atObject(287, 571);
        walkTo(286, 571);
        return random(100, 1500);
      }
      walkTo(287, 571);
    }
    return random(302, 400);
  }

  private int mineOre(int id) {
    int[] rock = getObjectById(id);
    if (rock[0] != -1) {
      if (rock[1] >= 263 && rock[1] <= 277 && rock[2] >= 3387 && rock[2] <= 3400) {
        // Si le rock est dans la sale...
        // print("Mining : " + id);
        atObject(rock[1], rock[2]);
        return 1;
      }
    }
    return 0;
  }

  private boolean depositGems() {
    if (getInventoryCount(155) > 0) {
      deposit(155, getInventoryCount(155));
      return false;
    }
    if (getInventoryCount(153) > 0) {
      deposit(153, getInventoryCount(153));
      return false;
    }
    // Saphire
    if (getInventoryCount(160) > 0) {
      deposit(160, getInventoryCount(160));
      return false;
    }
    // Ruby
    if (getInventoryCount(158) > 0) {
      deposit(158, getInventoryCount(158));
      return false;
    }
    // Emerald
    if (getInventoryCount(159) > 0) {
      deposit(159, getInventoryCount(159));
      return false;
    }
    // Diamond
    if (getInventoryCount(157) > 0) {
      deposit(157, getInventoryCount(157));
      return false;
    } else {
      walkTo(274, 562);
      return true;
    }
  }

  // public final void print(String gameText)
  // {
  //	System.out.println(gameText);
  // }
}
