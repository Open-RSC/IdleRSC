package scripting.idlescript.other.AIOAIO.combat;

import bot.Main;
import controller.Controller;
import models.entities.ItemId;
import models.entities.NpcId;
import scripting.idlescript.other.AIOAIO.AIOAIO;

public class AlKharidScorpions {
  private static Controller c;

  public static int strength() {
    Main.getController().setFightMode(1);
    return run();
  }

  public static int attack() {
    Main.getController().setFightMode(2);
    return run();
  }

  public static int defense() {
    Main.getController().setFightMode(3);
    return run();
  }

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (Main.getController().getBaseStat(Main.getController().getStatId("Hits")) < 15) {
      c.log("Skipping Scorpions until we're at least 15 hits!");
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 50;
    }

    if (AIOAIO.state.taskStartup) return Combat_Utils.getFightingGear();
    else if (c.getInventoryItemCount(ItemId.BONES.getId()) > 0) Combat_Utils.buryBones();
    else if (Combat_Utils.needToEat() && !Combat_Utils.hasFood()) Combat_Utils.safelyAbortTask();
    else if (Combat_Utils.needToEat()) Combat_Utils.runAndEat();
    else if (c.isInCombat()) c.sleepUntil(() -> !c.isInCombat() || Combat_Utils.needToEat());
    else if (c.getNearestNpcById(NpcId.SCORPION.getId(), false) == null) findScorpions();
    // The NPC we want to bop is found at this point
    else if (c.getInventoryItemCount() < 30
        && c.getNearestItemById(ItemId.BONES.getId(), 5) != null) Combat_Utils.lootBones();
    else Combat_Utils.attackNpc(NpcId.SCORPION);
    return 50;
  }

  private static void findScorpions() {
    AIOAIO.state.status = ("Finding scorpions to bop");
    c.walkTowards(71, 588);
  }
}
