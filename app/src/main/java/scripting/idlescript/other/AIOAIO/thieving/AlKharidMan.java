package scripting.idlescript.other.AIOAIO.thieving;

import bot.Main;
import controller.Controller;
import java.util.concurrent.ThreadLocalRandom;
import models.entities.ItemId;
import models.entities.NpcId;
import orsc.ORSCharacter;
import scripting.idlescript.other.AIOAIO.combat.Combat_Utils;

public class AlKharidMan {
  private static Controller c;
  private static boolean goingToCabbages = false;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (inCabbageField() && c.getInventoryItemCount() <= 20) pickCabbage();
    else if (goingToCabbages || Combat_Utils.needToEat() && !Combat_Utils.hasFood()) goToCabbages();
    else if (Combat_Utils.needToEat()) Combat_Utils.runAndEat();
    else if (c.isInCombat()) {
      c.setStatus("Getting out of combat");
      c.walkToAsync(c.currentX(), c.currentY(), 0);
      c.sleep(600);
    } else if (c.getNearestNpcById(NpcId.MAN_ALKHARID.getId(), false) == null) findAlkharidMen();
    else {
      Main.getController().setStatus("@yel@Thieving Man");
      ORSCharacter npc = Main.getController().getNearestNpcById(NpcId.MAN_ALKHARID.getId(), false);
      if (npc == null) {

        Main.getController().setStatus("@yel@Can't find nobody :c");
        return 50;
      }
      Main.getController().thieveNpc(npc.serverIndex);
      return 680;
    }
    return 50;
  }

  private static boolean eastMen = ThreadLocalRandom.current().nextBoolean();

  private static void findAlkharidMen() {
    if (ThreadLocalRandom.current().nextInt(0, 25) == 1) {
      eastMen = !eastMen;
    }
    c.setStatus("Finding " + (eastMen ? "east" : "west") + " Al Kharid Men");
    c.walkTowards(eastMen ? 61 : 77, 673);
  }

  private static void goToCabbages() {
    c.log("Going to cabbage");
    goingToCabbages = true;
    c.walkTowards(142, 608);
  }

  private static boolean inCabbageField() {
    return c.currentX() >= 137 && c.currentY() <= 614 && c.currentX() <= 154 && c.currentY() >= 597;
  }

  private static void pickCabbage() {
    c.setStatus("Picking cabbage");
    goingToCabbages = false;
    c.pickupItem(ItemId.CABBAGE.getId());
  }
}
