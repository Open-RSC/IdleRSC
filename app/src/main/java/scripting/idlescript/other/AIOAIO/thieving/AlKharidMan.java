package scripting.idlescript.other.AIOAIO.thieving;

import bot.Main;
import controller.Controller;
import java.util.concurrent.ThreadLocalRandom;
import models.entities.NpcId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.combat.Combat_Utils;

public class AlKharidMan {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    c.setBatchBars(true);

    if (AIOAIO.state.taskStartup) {
      return Thieving_Utils.getReadyForTheiving();
    }

    c.setStatus("Thieving Al Kharid Men");
    if (Thieving_Utils.inCabbageField() && c.getInventoryItemCount() <= 20)
      Thieving_Utils.pickCabbage();
    else if (Thieving_Utils.goingToCabbages || Combat_Utils.needToEat() && !Combat_Utils.hasFood())
      Thieving_Utils.goToCabbages();
    else if (Combat_Utils.needToEat()) Combat_Utils.runAndEat();
    else if (c.isInCombat()) {
      Thieving_Utils.leaveCombat();
    } else if (Thieving_Utils.getNpc(NpcId.MAN_ALKHARID) == null) findAlkharidMen();
    else return Thieving_Utils.theiveNpc(NpcId.MAN_ALKHARID);
    return 50;
  }

  private static boolean eastMen = ThreadLocalRandom.current().nextBoolean();

  private static void findAlkharidMen() {
    if (ThreadLocalRandom.current().nextInt(0, 25) == 1) {
      eastMen = !eastMen;
    }
    AIOAIO.state.status = ("Finding " + (eastMen ? "east" : "west") + " Al Kharid Men");
    c.walkTowards(eastMen ? 61 : 77, 673);
  }
}
