package scripting.idlescript.other.AIOAIO.thieving;

import bot.Main;
import controller.Controller;
import models.entities.NpcId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.combat.Combat_Utils;

public class VarrockGuard {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (Main.getController().getCurrentStat(Main.getController().getStatId("Thieving")) < 40) {
      c.log("Skipping Varrock Guards until we're at least 40 thieving!");
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 50;
    }

    if (AIOAIO.state.taskStartup) {
      return Thieving_Utils.getReadyForTheiving();
    }

    c.setStatus("Thieving Varrock Guards (2nd floor)");
    if (Thieving_Utils.inCabbageField() && c.getInventoryItemCount() <= 20)
      Thieving_Utils.pickCabbage();
    else if (Thieving_Utils.goingToCabbages || Combat_Utils.needToEat() && !Combat_Utils.hasFood())
      Thieving_Utils.goToCabbages();
    else if (Combat_Utils.needToEat()) Combat_Utils.runAndEat();
    else if (c.isInCombat()) {
      Thieving_Utils.leaveCombat();
    } else if (c.getNearestNpcById(NpcId.GUARD.getId(), false) == null) findVarrockGuards();
    else {
      Thieving_Utils.theiveNpc(NpcId.GUARD);
      return 680;
    }
    return 50;
  }

  private static void findVarrockGuards() {
    AIOAIO.state.status = ("Finding Varrock Guards!");
    c.walkTowards(139, 1400);
  }
}
