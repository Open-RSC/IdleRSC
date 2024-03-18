package scripting.idlescript.other.AIOAIO.thieving;

import bot.Main;
import controller.Controller;
import models.entities.NpcId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.combat.Combat_Utils;

public class AlKharidWarrior {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (Main.getController().getCurrentStat(Main.getController().getStatId("Thieving")) < 25) {
      c.log("Skipping Al Kharid Warriors until we're at least 25 thieving!");
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 50;
    }

    if (AIOAIO.state.taskStartup) {
      return Thieving_Utils.getReadyForTheiving();
    }

    c.setStatus("Thieving Al Kharid Warriors");
    if (Thieving_Utils.inCabbageField() && c.getInventoryItemCount() <= 20)
      Thieving_Utils.pickCabbage();
    else if (Thieving_Utils.goingToCabbages || Combat_Utils.needToEat() && !Combat_Utils.hasFood())
      Thieving_Utils.goToCabbages();
    else if (Combat_Utils.needToEat()) Combat_Utils.runAndEat();
    else if (c.isInCombat()) {
      Thieving_Utils.leaveCombat();
    } else if (c.getNearestNpcById(NpcId.ALKHARID_WARRIOR.getId(), false) == null)
      findAlkharidWarriors();
    else {
      Thieving_Utils.theiveNpc(NpcId.ALKHARID_WARRIOR);
      return 680;
    }
    return 50;
  }

  private static void findAlkharidWarriors() {
    AIOAIO.state.status = ("Finding the Al Kharid warriors!");
    c.walkTowards(71, 690);
  }
}
