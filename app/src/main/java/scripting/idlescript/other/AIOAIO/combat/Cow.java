package scripting.idlescript.other.AIOAIO.combat;

import bot.Main;
import controller.Controller;
import models.entities.ItemId;
import models.entities.NpcId;
import scripting.idlescript.other.AIOAIO.AIOAIO;

public class Cow {
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

    if (AIOAIO.state.methodStartup) return Combat_Utils.getFightingGear();
    else if (c.getInventoryItemCount(ItemId.BONES.getId()) > 0) Combat_Utils.buryBones();
    else if (inCabbageField() && c.getInventoryItemCount() <= 20) pickCabbage();
    else if (Combat_Utils.needToEat() && !Combat_Utils.hasFood()) goToCabbages();
    else if (Combat_Utils.needToEat()) Combat_Utils.runAndEat();
    else if (c.isInCombat()) c.sleepUntilGainedXp();
    else if (c.getNearestNpcById(NpcId.COW_ATTACKABLE.getId(), false) == null) findCows();
    // The NPC we want to bop is found at this point
    else if (c.getInventoryItemCount() < 30
        && c.getNearestItemById(ItemId.BONES.getId(), 5) != null) Combat_Utils.lootBones();
    else Combat_Utils.attackNpc(NpcId.COW_ATTACKABLE);
    return 50;
  }

  private static void goToCabbages() {
    c.log("Going to cabbage");
    c.walkTowards(142, 608);
  }

  private static boolean inCabbageField() {
    return c.currentX() >= 137 && c.currentY() <= 614 && c.currentX() <= 154 && c.currentY() >= 597;
  }

  private static void pickCabbage() {
    c.setStatus("Picking cabbage");
    c.pickupItem(ItemId.CABBAGE.getId());
  }

  private static void findCows() {
    c.setStatus("Finding cows");
    c.walkTowards(99, 617);
  }
}
