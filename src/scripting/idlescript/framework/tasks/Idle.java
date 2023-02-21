package scripting.idlescript.framework.tasks;

import models.entities.MapPoint;

import java.util.Random;

public class Idle extends IdleTask {
    private static MapPoint lastLocation = null;
    private static int identicalLocationCount = 0;
    private static final int TRACKED_LOCATIONS_LIMIT = 200;

    @Override
    public int tickDelay() {
        return 2;
    }

    @Override
    protected void executeTask() {
        MapPoint currentLocation = botController.playerApi.getCurrentLocation();
        if (!currentLocation.equals(lastLocation)) {
            lastLocation = currentLocation;
            identicalLocationCount = 0;
        } else {
            identicalLocationCount++;
        }

        botController.debug("Identical location count: " + identicalLocationCount);

        if (identicalLocationCount > TRACKED_LOCATIONS_LIMIT - 1) {
            handleAssumedStuckPosition(currentLocation);
            return;
        }

        botController.setStatus("@red@Idling..");
        botController.debug("Idling..");
    }

    private void handleAssumedStuckPosition(MapPoint currentLocation) {
        botController.debug("Idling so long that it seems we are stuck. Trying to move..");

        botController.pathWalkerApi
                .walkTo(new MapPoint(
                        currentLocation.getX() + getRandomDeviation(),
                        currentLocation.getY() + getRandomDeviation()));

        identicalLocationCount = 0;
    }

    private static int getRandomDeviation() {
        return getFromMinusOneToOne();
    }

    private static int getFromMinusOneToOne() {
        return new Random().nextInt(3) - 1;
    }

}
