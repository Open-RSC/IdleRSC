package scripting.apos;
// Abyte0
// 2012-04-14
// 2021-07-19 Fixed normal tree id

/*
To Cut tree
abyte0_powerwc normal
abyte0_powerwc oak
abyte0_powerwc willow
abyte0_powerwc yew
abyte0_powerwc magic
*/

public class Abyte0_PowerWc extends Abyte0_Script {
  final int[] normalTree = {0, 1};
  final int[] oakTree = {306};
  final int[] willowTree = {307};
  final int[] yewTree = {309};
  final int[] magicTree = {310};

  int xSpot, ySpot;
  int[] treeId;
  final int fmode = 1;

  public Abyte0_PowerWc(String e) {
    super(e);
  }

  public void init(String param) {
    print("Power Woodcutter by Abyte0");
    print("Commands to cut: oak willow yew magic");
    print("Version 1");

    xSpot = getX();
    ySpot = getY();
    treeId = yewTree;

    switch (param) {
      case "yew":
        print("Doing Yew");
        break;
      case "normal":
        print("Doing Normal");

        treeId = normalTree;
        break;
      case "oak":
        print("Doing Oak");

        treeId = oakTree;
        break;
      case "willow":
        print("Doing Willow");

        treeId = willowTree;
        break;
      case "magic":
        print("Doing Magic");

        treeId = magicTree;
        break;
      default:
        print("Default = Yew LongBow");
        break;
    }
  }

  public int main() {
    if (getFightMode() != fmode) {
      setFightMode(fmode);
      return 500;
    }
    if (getFatigue() > 85) {
      useSleepingBag();
      return random(800, 1000);
    } else {
      if (isAtApproxCoords(xSpot, ySpot, 25)) {
        // si on est au spot
        int[] tree = getObjectById(treeId);
        if (tree[0] != -1) {
          atObject(tree[1], tree[2]);
          return random(500, 600);
        }
      } else {
        walkTo(xSpot, ySpot);
        return random(1000, 3000);
      }
    }
    return random(800, 1000);
  }
}
