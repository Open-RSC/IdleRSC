package callbacks.chatcommand;

import bot.Main;
import bot.ui.scriptselector.models.PackageInfo;
import controller.Controller;
import java.util.Arrays;
import models.entities.Location;

public class WalkToCommand implements IChatCommand {

  @Override
  public void execute(String[] args, Controller c) {
    if (c == null) return;

    if (c.isRunning()) {
      c.displayMessage("@red@Unable to use the 'walkto' command with a script running");
      return;
    }

    if (args.length == 0) {
      c.displayMessage("@red@Invalid ::walkto usage");
      c.displayMessage("Expected: @yel@::walkto <x> <y> @whi@or @yel@::walkto <location>");
      return;
    }

    checkWalkToLocation(args);
  }

  @Override
  public String helpString() {
    return " %@red@::walkto <x> <y>/<location> - @yel@Walks to a specified location";
  }

  private void checkWalkToLocation(String[] args) {
    Location dest = null;
    boolean passed;

    if (args.length == 2) {
      try {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);

        passed = x > 0 && x < 1008 && y > 0 && y < 3840;

        if (!passed) {
          String message1 = String.format("@red@walkto: Invalid walk coordinates: (%s, %s)", x, y);

          String message2 = "@red@Coordinates should be between (0, 0) and (1007, 3839)";

          Main.getController().displayMessage(message1);
          Main.log(message1);
          Main.getController().displayMessage(message2);
          Main.log(message2);
        }
      } catch (Exception ignored) {
        dest = checkWalkToQuery(args);
        passed = dest != null;
      }
    } else {
      dest = checkWalkToQuery(args);
      passed = dest != null;
    }

    if (passed) {
      if (dest != null) {
        Main.config.setScriptArguments(new String[] {dest.getDescription()});
      } else {
        Main.config.setScriptArguments(args);
      }

      Main.loadAndRunScript("LocationWalker", PackageInfo.NATIVE);
      Main.setRunning(true);
    }
  }

  private Location checkWalkToQuery(String[] search) {
    boolean searchContainsBank = Arrays.stream(search).anyMatch(s -> s.equalsIgnoreCase("bank"));

    Location[] results =
        Arrays.stream(Location.values())
            .filter(
                loc -> {
                  String input = loc.getDescription().toLowerCase();
                  return Arrays.stream(search).allMatch(term -> input.contains(term.toLowerCase()));
                })
            .toArray(Location[]::new);

    String searchStr = String.join(" ", search).trim();
    Location mappedLocation = Location.getWalktoExactMatchLocation(searchStr);
    if (mappedLocation != null) return mappedLocation;

    Location destination =
        Arrays.stream(results)
            .filter(l -> l.getDescription().equalsIgnoreCase(searchStr))
            .findFirst()
            .orElse(null);

    if (destination == null) {
      if (results.length == 0) {
        noResultsFound(search);
        return null;
      } else if (results.length == 1) {
        return results[0];
      } else if (searchContainsBank || results.length > 10) {
        searchQueryNotSpecificEnough(search, results);
        return null;
      } else {
        return results[0];
      }
    }

    return destination;
  }

  private void noResultsFound(String[] query) {
    String queryStr = String.join(" ", query);
    String message = String.format("@red@walkto: No Location results found for: %s", queryStr);

    Main.getController().displayMessage(message);
    Main.log(message);
  }

  private void searchQueryNotSpecificEnough(String[] query, Location[] results) {
    String queryStr = String.join(" ", query);

    String resultsStr =
        Arrays.stream(results)
            .map(Location::toString)
            .reduce("", (acc, loc) -> acc + "   " + loc + System.lineSeparator());

    String message =
        String.format(
            "walkto: Search query was not specific enough:%nQuery: %s%nResults:%n%s",
            queryStr, resultsStr);

    Main.getController()
        .displayMessage("@red@walkto: Search query not specific enough, refer to console");

    Main.log(message);
  }
}
