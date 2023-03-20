package scripting.apos;

import java.awt.Point;
import java.util.ArrayList;

public class Abyte0_Firemaking extends Abyte0_Script {
  private int fmode = 3;
  private int _lastActionReturn = 0;
  private ArrayList<Point> _lastTree = null;
  private ArrayList<ArrayList<Point>> _paths = new ArrayList<ArrayList<Point>>();

  public Abyte0_Firemaking(String e) {
    super(e);
  }

  // public void print(String gameText)
  // {
  //	System.out.println(gameText);
  //	//printBot(gameText);
  // }

  public void init(String params) {
    print("Started Abyte0 Draynor Firemaking");
    print("Version 0.7");
    print("Start Behind Bank with Box + Axe");

    loadPaths();
  }

  public int main() {
    if (getFightMode() != fmode) {
      setFightMode(fmode);
      return random(600, 1000);
    }
    if (getFatigue() > 96) {
      useSleepingBag();
      return random(921, 1000);
    }

    if (_lastActionReturn == 0) {
      // Near bank
      _lastActionReturn = isNearBank();
      if (_lastActionReturn != -1) return _lastActionReturn;
    }

    int myX = getX();
    int myY = getY();

    if (_lastTree == null) _lastTree = findNextTree(myX, myY);

    // if near tree cut it
    if (_lastTree.get(1).x == myX && _lastTree.get(1).y == myY) {
      // Cut
      int id = getObjectIdFromCoords(_lastTree.get(2).x, _lastTree.get(2).y);
      // printBot("id = "+id);
      if (id == 1 || id == 0) {
        atObject(_lastTree.get(2).x, _lastTree.get(2).y);
        return random(1100, 1200);
      }
      // Light - thanks rln for premade code Wrong PARAM
      int[] log = getItemById(14);
      if (log[0] != -1 && !isObjectAt(log[1], log[2]) && log[1] == getX() && log[2] == getY()) {
        int tinder = getInventoryIndex(166);
        useItemOnGroundItem(tinder, log[0], log[1], log[2]);

        return random(1100, 1200);
      }

      // lest get next tree by setting it null
      // printBot("Reset Tree");
      _lastTree = null;
    } else {
      // looking if npc in my way
      int[] idNpcs = {0, 4, 8};
      for (int i = 0; i < 3; i++) {
        int[] npc = getNpcInRadius(idNpcs[i], _lastTree.get(1).x, _lastTree.get(1).y, 1);
        if (npc[0] != -1 && !inCombat()) {
          attackNpc(npc[0]);
          return random(600, 1200);
        }
      }

      walkTo(_lastTree.get(1).x, _lastTree.get(1).y);
      return random(600, 1200);
    }
    return random(600, 650);
  }

  public int isNearBank() {
    if (isAtApproxCoords(213, 648, 2)) {
      walkTo(200, 653);
      return random(1000, 1100);
    }
    if (isAtApproxCoords(200, 653, 5)) {
      walkTo(213, 648);
      return random(1000, 1100);
    }
    return -1;
  }

  private final ArrayList<Point> findNextTree(int pX, int pY) {
    for (int i = 0; i < _paths.size(); i++) {
      if (_paths.get(i).get(0).x == pX && _paths.get(i).get(0).y == pY) {
        return _paths.get(i);
      }
    }

    // Lost lets try to get to first tree
    ArrayList<Point> objTemp = new ArrayList<Point>();
    objTemp.add(new Point(pX, pY));
    objTemp.add(_paths.get(0).get(1));
    objTemp.add(_paths.get(0).get(2));

    return objTemp;
  }

  private final void loadPaths() {
    // Tree 1
    addPath(new Point(198, 653), new Point(195, 653), new Point(195, 652));
    addPath(new Point(197, 653), new Point(195, 653), new Point(195, 652));
    addPath(new Point(196, 653), new Point(195, 653), new Point(195, 652));

    // Tree 2
    addPath(new Point(195, 653), new Point(195, 654), new Point(194, 654));

    // Tree 3
    addPath(new Point(195, 654), new Point(196, 655), new Point(195, 655));
    addPath(new Point(196, 654), new Point(196, 655), new Point(195, 655));

    // Tree 4
    addPath(new Point(196, 655), new Point(196, 656), new Point(196, 657));

    // Tree 5
    addPath(new Point(196, 656), new Point(194, 657), new Point(193, 657));
    addPath(new Point(195, 656), new Point(194, 657), new Point(193, 657));
    addPath(new Point(194, 656), new Point(194, 657), new Point(193, 657));
    addPath(new Point(195, 657), new Point(194, 657), new Point(193, 657));

    // Tree 6
    addPath(new Point(194, 657), new Point(194, 659), new Point(195, 659));
    addPath(new Point(194, 658), new Point(194, 659), new Point(195, 659));

    // Tree 7
    addPath(new Point(194, 659), new Point(193, 660), new Point(194, 660));
    addPath(new Point(193, 659), new Point(193, 660), new Point(194, 660));

    // Tree 8
    addPath(new Point(193, 660), new Point(193, 661), new Point(193, 662));

    // Tree 9
    addPath(new Point(193, 661), new Point(192, 664), new Point(192, 665));
    addPath(new Point(192, 661), new Point(192, 664), new Point(192, 665));
    addPath(new Point(192, 662), new Point(192, 664), new Point(192, 665));
    addPath(new Point(192, 663), new Point(192, 664), new Point(192, 665));

    // Tree 10
    addPath(new Point(192, 664), new Point(193, 664), new Point(194, 664));

    // Tree 11
    addPath(new Point(193, 664), new Point(195, 665), new Point(196, 665));
    addPath(new Point(193, 665), new Point(195, 665), new Point(196, 665));
    addPath(new Point(194, 665), new Point(195, 665), new Point(196, 665));

    // Tree 1112A
    addPath(new Point(195, 665), new Point(196, 667), new Point(196, 668));
    addPath(new Point(195, 666), new Point(196, 667), new Point(196, 668));
    addPath(new Point(195, 667), new Point(196, 667), new Point(196, 668));
    addPath(new Point(196, 666), new Point(196, 667), new Point(196, 668));
    // Tree 1112B
    addPath(new Point(196, 667), new Point(198, 667), new Point(198, 668));
    addPath(new Point(197, 667), new Point(198, 667), new Point(198, 668));
    // Tree 1112C
    addPath(new Point(198, 667), new Point(198, 666), new Point(199, 666));

    // Tree 12
    addPath(new Point(198, 666), new Point(197, 665), new Point(198, 665));
    addPath(new Point(197, 666), new Point(197, 665), new Point(198, 665));

    // Tree 13
    addPath(new Point(197, 665), new Point(198, 664), new Point(198, 663));
    addPath(new Point(197, 664), new Point(198, 664), new Point(198, 663));

    // Tree 15
    addPath(new Point(198, 664), new Point(199, 662), new Point(200, 662));
    addPath(new Point(199, 664), new Point(199, 662), new Point(200, 662));
    addPath(new Point(199, 663), new Point(199, 662), new Point(200, 662));

    // Tree 16
    addPath(new Point(199, 662), new Point(198, 661), new Point(197, 661));
    addPath(new Point(198, 662), new Point(198, 661), new Point(197, 661));
    addPath(new Point(199, 661), new Point(198, 661), new Point(197, 661));

    // Tree 17
    addPath(new Point(198, 661), new Point(198, 659), new Point(198, 658));
    addPath(new Point(198, 660), new Point(198, 659), new Point(198, 658));

    // Tree 18
    addPath(new Point(198, 659), new Point(200, 657), new Point(201, 657));
    addPath(new Point(197, 659), new Point(200, 657), new Point(201, 657));
    addPath(new Point(197, 658), new Point(200, 657), new Point(201, 657));
    addPath(new Point(197, 657), new Point(200, 657), new Point(201, 657));
    addPath(new Point(198, 657), new Point(200, 657), new Point(201, 657));
    addPath(new Point(199, 657), new Point(200, 657), new Point(201, 657));

    // Tree 19
    addPath(new Point(200, 657), new Point(200, 655), new Point(201, 655));
    addPath(new Point(200, 656), new Point(200, 655), new Point(201, 655));

    // Tree 20
    addPath(new Point(200, 655), new Point(200, 653), new Point(200, 652));
    addPath(new Point(200, 654), new Point(200, 653), new Point(200, 652));

    // Tree 21
    addPath(new Point(200, 653), new Point(198, 653), new Point(198, 652));
    addPath(new Point(199, 653), new Point(198, 653), new Point(198, 652));
  }

  // addPath( PossibleXY , XYToWalkToBeReadyToCutTree , TreeXY )
  private final void addPath(Point pFrom, Point pTo, Point pTree) {
    ArrayList<Point> objListFromTo = new ArrayList<Point>();
    objListFromTo.add(pFrom);
    objListFromTo.add(pTo);
    objListFromTo.add(pTree);

    _paths.add(objListFromTo);
  }
}
