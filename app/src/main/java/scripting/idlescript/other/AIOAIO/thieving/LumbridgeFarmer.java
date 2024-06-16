package scripting.idlescript.other.AIOAIO.thieving;

import bot.Main;
import controller.Controller;
import java.util.concurrent.ThreadLocalRandom;
import models.entities.NpcId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.combat.Combat_Utils;

public class LumbridgeFarmer {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (Main.getController().getCurrentStat(Main.getController().getStatId("Thieving")) < 10) {
      c.log("Skipping Lummy farmers until we're at least 15 thieving!");
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 50;
    }

    if (AIOAIO.state.taskStartup) {
      return Thieving_Utils.getReadyForTheiving();
    }

    c.setStatus("Thieving Lumbridge Farmers");
    if (Thieving_Utils.inCabbageField() && c.getInventoryItemCount() <= 20)
      Thieving_Utils.pickCabbage();
    else if (Thieving_Utils.goingToCabbages || Combat_Utils.needToEat() && !Combat_Utils.hasFood())
      Thieving_Utils.goToCabbages();
    else if (Combat_Utils.needToEat()) Combat_Utils.runAndEat();
    else if (c.isInCombat()) {
      Thieving_Utils.leaveCombat();
    } else if (Thieving_Utils.getNpc(NpcId.FARMER) == null) findLummyFarmer();
    else return Thieving_Utils.theiveNpc(NpcId.FARMER);
    return 50;
  }

  private static boolean northLummyFarmer = ThreadLocalRandom.current().nextBoolean();

  private static void findLummyFarmer() {
    if (ThreadLocalRandom.current().nextInt(0, 25) == 1) {
      northLummyFarmer = !northLummyFarmer;
    }
    AIOAIO.state.status =
        ("Finding the " + (northLummyFarmer ? "Northern" : "Southern") + " Lummy farmer");
    if (northLummyFarmer) {
      c.walkTowards(114, 595);
    } else {
      c.walkTowards(116, 607);
    }
  }
}
