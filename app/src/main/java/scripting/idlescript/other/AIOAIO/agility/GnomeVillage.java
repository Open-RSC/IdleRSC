package scripting.idlescript.other.AIOAIO.agility;

import bot.Main;
import controller.Controller;
import models.entities.ItemId;
import models.entities.SceneryId;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;

public class GnomeVillage {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    if (Main.getController().getCurrentStat(Main.getController().getStatId("Hits")) <= 20) {
      c.log(
          "Aborted Gnome Village because I have below 20hp and probably can't cross White Wolf alive!");
      AIOAIO.state.endTime = System.currentTimeMillis();
      return 0;
    }
    c.setBatchBars(true);
    System.out.println("Gnome Village run");

    if (c.getInventoryItemCount(ItemId.COINS.getId()) > 100) {
      AIOAIO_Script_Utils.towardsDepositAll();
    } else if (!isInsideGnomeAgilityArena()) {
      AIOAIO.state.status = ("Walking to Gnome Agility Arena");
      c.walkTowards(692, 494);
    } else if (c.distanceTo(692, 499) <= 3) {
      c.atObject(SceneryId.NET_GNOME_COURSE_START);
      c.sleepUntilGainedXp();
    } else if (c.distanceTo(692, 1448) <= 5) {
      c.atObject(SceneryId.WATCH_TOWER_GNOME_COURSE_1ST_F);
      c.sleepUntil(
          () -> Main.getController().currentX() == 693 && Main.getController().currentY() == 2394,
          2000);
    } else if (c.distanceTo(693, 2394) <= 3) {
      c.atObject(SceneryId.ROPESWING_GNOME_COURSE);
      c.sleepUntilGainedXp();
    } else if (c.distanceTo(685, 2396) <= 3) {
      c.atObject(SceneryId.WATCH_TOWER_GNOME_COURSE_2ND_F);
      c.sleepUntilGainedXp();
    } else if (c.distanceTo(683, 506) <= 3) {
      c.walkTo(683, 503);
      c.atObject(SceneryId.NET_GNOME_COURSE_END);
      c.sleepUntilGainedXp();
    } else if (c.distanceTo(683, 501) <= 5) {
      c.atObject(SceneryId.PIPE_GNOME_COURSE);
      c.sleepUntilGainedXp();
      c.sleep(620);
    } else {
      c.atObject(SceneryId.LOG_GNOME_COURSE);
      // Default - Cross the log
      c.sleepUntilGainedXp();
    }
    return 680;
  }

  private static boolean isInsideGnomeAgilityArena() {

    return c.currentX() >= 681 && c.currentY() >= 492 && c.currentX() < 695 && c.currentY() <= 510
        || c.getNearestReachableObjectById(SceneryId.WATCH_TOWER_GNOME_COURSE_1ST_F.getId(), true)
            != null
        || c.getNearestReachableObjectById(SceneryId.ROPESWING_GNOME_COURSE.getId(), true) != null
        || c.getNearestReachableObjectById(SceneryId.WATCH_TOWER_GNOME_COURSE_2ND_F.getId(), true)
            != null;
  }
}
