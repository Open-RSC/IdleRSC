package scripting.apos;

// Based on the Script HARRY provided on forum (fletch)
// Edited by Abyte0
// 2012-01-24 - Added WalkBack

public class Abyte0_Shaft extends Abyte0_Script {

  int x, y;

  public Abyte0_Shaft(String e) {
    super(e);
  }

  public void init(String params) {
    x = getX();
    y = getY();
    System.out.println("Shafter Edited By Abyte0 - Version 0");
    System.out.println("WalkBack at " + x + "," + y);
  }

  public int main() {
    if (getFatigue() > 90) {
      useSleepingBag();
      return random(1000, 2000);
    }

    if (getInventoryIndex(277) > 0) {
      dropItem(getInventoryIndex(277));
      return random(500, 600);
    }

    if (isQuestMenu()) {
      answer(0);
      return random(200, 400);
    }

    if (getInventoryIndex(14) > 0) {
      useItemWithItem(getInventoryIndex(13), getInventoryIndex(14));
      return random(500, 600);
    }

    int[] tree = getObjectById(0, 1);

    if (tree[0] != -1 && getInventoryCount(14) <= 0) {
      if (getX() < x - 35 || getX() > x + 35 || getY() < y - 35 || getY() > y + 35) {
        walkTo(x, y);
        return random(500, 3000);
      } else atObject(tree[1], tree[2]);
      return random(500, 600);
    }

    return random(1000, 2000);
  }
}
