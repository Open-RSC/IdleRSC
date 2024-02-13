package scripting.idlescript.other.AIOAIO.agility;

import bot.Main;
import controller.Controller;
import models.entities.SceneryId;

public class GnomeVillage {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();
    System.out.println("Gnome Village run");

    if (!isInsideGnomeAgilityArena()) c.walkTowards(692, 494);
    else if (c.distanceTo(692, 499) <= 3) {
      c.atObject(SceneryId.NET_GNOME_COURSE_START);
      c.sleepUntilGainedXp();
    } else if (c.distanceTo(692, 1448) <= 3) {
      c.atObject(SceneryId.WATCH_TOWER_GNOME_COURSE_1ST_F);
      c.sleepUntilGainedXp();
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
      c.sleep(1250);
    } else {
      c.atObject(SceneryId.LOG_GNOME_COURSE);
      // Default - Cross the log
      c.sleepUntilGainedXp();
    }
    return 50;
  }

  private static boolean isInsideGnomeAgilityArena() {
    return c.getNearestReachableObjectById(SceneryId.LOG_GNOME_COURSE.getId(), true) != null
        || c.getNearestReachableObjectById(SceneryId.NET_GNOME_COURSE_START.getId(), true) != null
        || c.getNearestReachableObjectById(SceneryId.WATCH_TOWER_GNOME_COURSE_1ST_F.getId(), true)
            != null
        || c.getNearestReachableObjectById(SceneryId.ROPESWING_GNOME_COURSE.getId(), true) != null
        || c.getNearestReachableObjectById(SceneryId.WATCH_TOWER_GNOME_COURSE_2ND_F.getId(), true)
            != null
        || c.getNearestReachableObjectById(SceneryId.NET_GNOME_COURSE_END.getId(), true) != null
        || c.getNearestReachableObjectById(SceneryId.PIPE_GNOME_COURSE.getId(), true) != null;
  }
}
