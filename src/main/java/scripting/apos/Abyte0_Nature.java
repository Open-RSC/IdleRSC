package scripting.apos;

import compatibility.apos.Script;

/**
 * a simple nature rune chest thiever. picks the chest and... that's about it. works at the chest
 * near banker stall, dunno if it works at the other one.
 *
 * <p>- yomama`
 */

// Edited by Abyte0

public final class Abyte0_Nature extends Script {
  int cpt = 0;

  public Abyte0_Nature(String e) {
    //        super(e);
  }

  public void init(String params) {
    print("Started Abyte0 Nat Thiever");
    print("Version 1.1");
    // Version 0 by yomama
    // Version 1 by Abyte0
    // Walk to avoid log out (work with the 2 houses)
    // fix 1.1 Lower click time
    if (isAtApproxCoords(539, 1545, 15)) print("Cake Nat");
    else print("Line Nat");
  }

  public int main() {
    if (getFightMode() != 2) {
      setFightMode(2);
      return random(300, 2500);
    }

    if (getFatigue() > 90) {
      useSleepingBag();
      return random(921, 1000);
    }
    if (cpt > random(50, 100)) {
      print("Walk");
      if (isAtApproxCoords(539, 1545, 15)) walkTo(539, 1545);
      else walkTo(582, 1525);
      cpt = 0;
      return 2000;
    }

    int[] nats = getObjectById(335, 334, 340); // 332,333,334,335,336,337,338,339,340
    if (nats[0] != -1) {
      atObject2(nats[1], nats[2]);
      print("thieve: " + nats[0]);
    }

    cpt++;
    return random(random(126, 567), 1142);
  }

  public void print(String gameText) {
    System.out.println(gameText);
  }
}
