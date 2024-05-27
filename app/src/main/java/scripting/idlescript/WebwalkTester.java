package scripting.idlescript;

import bot.Main;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Tests walking to specific coordinates passed as parameters
 *
 * @author
 */
public class WebwalkTester extends IdleScript {

  private Queue<Integer> coordinates = new LinkedList<>();
  private boolean scriptStarted = false;
  private Integer currentDestX = null;
  private Integer currentDestY = null;

  public int start(String[] parameters) {
    if (!scriptStarted) {
      if (!startup(parameters)) stop();
    }

    if (currentDestX == null && currentDestY == null) {
      if (coordinates.isEmpty()) {
        Main.getController().log("All destinations reached!");
        stop();
        return 0;
      } else {
        currentDestX = coordinates.poll();
        currentDestY = coordinates.poll();
        Main.getController().log("New destination: " + currentDestX + ", " + currentDestY);
      }
    }

    if (Main.getController().currentX() == currentDestX
        && Main.getController().currentY() == currentDestY) {
      Main.getController().log("Made it to " + currentDestX + ", " + currentDestY + "!");
      currentDestX = null;
      currentDestY = null;
    } else {
      Main.getController().log("Webwalking to: " + currentDestX + ", " + currentDestY);
      Main.getController().walkTowards(currentDestX, currentDestY);
    }

    return 1000;
  }

  private boolean startup(String[] parameters) {
    scriptStarted = true;
    if (parameters.length != 1) {
      Main.getController()
          .log("Invalid parameters. Please pass in coordinates as comma-separated integers.");
      return false;
    }

    String[] coords = parameters[0].split(",");
    if (coords.length % 2 != 0) {
      Main.getController()
          .log("Invalid format. Please pass in an even number of comma-separated integers.");
      return false;
    }

    try {
      for (String coord : coords) {
        coordinates.add(Integer.parseInt(coord.trim()));
      }
    } catch (NumberFormatException e) {
      Main.getController().log("Invalid format. Coordinates must be integers.");
      return false;
    }

    return true;
  }

  private void stop() {
    scriptStarted = false;
    coordinates.clear();
    Main.getController().stop();
  }

  @Override
  public void paintInterrupt() {
    Main.getController().drawString("@red@Webwalk Tester", 6, 21, 0xFFFFFF, 1);
    Main.getController()
        .drawString(
            "@red@Current location: "
                + Main.getController().currentX()
                + ", "
                + Main.getController().currentY(),
            6,
            21 + 14 * 1,
            0xFFFFFF,
            1);
    if (currentDestX != null && currentDestY != null) {
      Main.getController()
          .drawString(
              "@red@Current destination: " + currentDestX + ", " + currentDestY,
              6,
              21 + 14 * 2,
              0xFFFFFF,
              1);
    }
    Main.getController()
        .drawString(
            "@red@Remaining destinations: " + coordinates.size() / 2, 6, 21 + 14 * 3, 0xFFFFFF, 1);
  }
}
