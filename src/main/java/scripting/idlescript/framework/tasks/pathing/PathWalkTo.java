package scripting.idlescript.framework.tasks.pathing;

import models.entities.MapPoint;
import scripting.idlescript.framework.tasks.IdleTask;
import scripting.idlescript.framework.util.LimitedQueue;

public class PathWalkTo extends IdleTask {
  private static final int TRACKED_LOCATIONS_LIMIT = 5;
  private static final LimitedQueue<MapPoint> TRACKED_LAST_LOCATIONS =
      new LimitedQueue<>(TRACKED_LOCATIONS_LIMIT);
  private static MapPoint lastLocation = null;
  private static int identicalLocationCount = 0;
  private final MapPoint destination;

  public PathWalkTo(MapPoint destination) {
    this.destination = destination;
  }

  @Override
  protected void executeTask() {
    walkTo();
  }

  @Override
  public int tickDelay() {
    return 1;
  }

  private void walkTo() {
    MapPoint currentLocation = botController.playerApi.getCurrentLocation();

    if (!TRACKED_LAST_LOCATIONS.contains(currentLocation)) {
      TRACKED_LAST_LOCATIONS.add(currentLocation);
    }

    botController.debug("Track last locations size: " + TRACKED_LAST_LOCATIONS.size());
    if (!currentLocation.equals(lastLocation)) {
      lastLocation = currentLocation;
      identicalLocationCount = 0;
    } else {
      identicalLocationCount++;
    }

    botController.debug("Identical location count: " + identicalLocationCount);
    if (identicalLocationCount > TRACKED_LOCATIONS_LIMIT - 1) {
      moveBackwards();
      identicalLocationCount = 0;
      return;
    }

    botController.debug("Walking to " + destination);
    botController.setStatus("@red@Walking to next destination..");
    botController.pathWalkerApi.walkTo(destination);
  }

  private void moveBackwards() {
    botController.debug("Stuck, trying to walk back to get unstuck..");
    TRACKED_LAST_LOCATIONS
        .getAllElements()
        .forEach(
            location -> {
              botController.playerApi.walkTo(location);
              botController.sleepTicks(2);
            });
    botController.debug("Stuck, trying to walk back to get unstuck..");
  }
}
