package controller.WebWalker;

import bot.Main;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import models.entities.ItemId;

public class WebWalker {
  // See https://github.com/dginovker/Runescape-Classic-Webwalker for generating
  // the graph.txt

  private final WebwalkGraph graph;

  public WebWalker(WebwalkGraph graph) {
    this.graph = graph;
  }

  /**
   * Takes the first step towards the closest coordinate pair from the given array. Returns after
   * the walk is completed.
   *
   * @param currentX the current X coordinate
   * @param currentY the current Y coordinate
   * @param endCoords the array of end coordinate pairs, where each pair is an array of two integers
   *     [endX, endY]
   * @return true if successfully took a step towards the closest end coordinate pair
   */
  public boolean webwalkTowards(int currentX, int currentY, int[][] endCoords) {
    long startTime = System.currentTimeMillis();
    Map<WebwalkNode, int[]> closestNodesToEndCoords = new HashMap<>();
    for (int[] coords : endCoords) {
      WebwalkNode node = graph.findClosestNode(coords[0], coords[1]);
      closestNodesToEndCoords.put(node, coords);
    }

    if (closestNodesToEndCoords.isEmpty()) {
      Main.getController()
          .log("No walkable end nodes found, yeeting off in a random direction to get unstuck..");
      Main.getController()
          .walkTo(
              currentX + ThreadLocalRandom.current().nextInt(-5, 6),
              currentY + ThreadLocalRandom.current().nextInt(-5, 6));
      return true;
    }

    WebwalkNode startNode = graph.findClosestNode(currentX, currentY);

    // Use modified Dijkstra's algorithm to find the path to the closest of the end
    // nodes
    Set<WebwalkNode> endNodes = closestNodesToEndCoords.keySet();
    List<WebwalkNode> path = dijkstraPathfinding(startNode, endNodes);

    if (path.isEmpty()) {
      Main.getController().log("No valid path found.");
      return false;
    }

    WebwalkNode finalGraphNode = path.get(path.size() - 1);
    int[] finalCoords = closestNodesToEndCoords.get(finalGraphNode);
    if (finalCoords != null
        && !(finalGraphNode.getX() == finalCoords[0] && finalGraphNode.getY() == finalCoords[1])) {
      // If final graph node's coordinates don't match the final coordinates, add a
      // step towards the actual final coordinates
      path.add(new WebwalkNode(finalCoords[0], finalCoords[1]));
    }

    // Logging the path
    StringBuilder pathLog = new StringBuilder("Path: ");
    for (WebwalkNode node : path) {
      pathLog.append("(").append(node.getX()).append(", ").append(node.getY()).append(") -> ");
    }
    Main.getController()
        .log(
            "Path calculated in "
                + (System.currentTimeMillis() - startTime)
                + "ms: "
                + pathLog.substring(0, pathLog.length() - 4));

    // Walk the first edge of the path
    if (path.size() < 2) return false; // Check if a valid path was found
    WebwalkEdge edge = graph.getEdge(path.get(0), path.get(1));
    if (edge != null && edge.getLabel().isPresent()) {
      Main.getController().log("Walking along " + edge + " with label: " + edge.getLabel().get());
      if (executeCustomLabelFunction(edge.getLabel().get())) {
        Main.getController().walkTo(path.get(1).getX(), path.get(1).getY());
        return true;
      } else {
        Main.getController()
            .log("Failed to execute custom function for label: " + edge.getLabel().get());
        return false;
      }
    } else {
      Main.getController().walkTo(path.get(1).getX(), path.get(1).getY());
      return true;
    }
  }

  private List<WebwalkNode> dijkstraPathfinding(WebwalkNode start, Set<WebwalkNode> goals) {
    Map<WebwalkNode, Double> costFromStart = new HashMap<>();
    costFromStart.put(start, 0.0);
    PriorityQueue<WebwalkNode> openSet =
        new PriorityQueue<>(Comparator.comparingDouble(costFromStart::get));
    Set<WebwalkNode> visited = new HashSet<>();
    Map<WebwalkNode, WebwalkNode> cameFrom = new HashMap<>();
    openSet.add(start);

    while (!openSet.isEmpty()) {
      WebwalkNode current = openSet.poll();

      if (visited.contains(current)) {
        continue; // Skip if this node has already been visited
      }

      if (goals.contains(current)) {
        return reconstructPath(cameFrom, current);
      }

      visited.add(current); // Mark the current node as visited

      for (WebwalkNode neighbor : graph.getNeighbors(current)) {
        if (visited.contains(neighbor)) {
          continue; // Skip if the neighbor has already been visited
        }
        WebwalkEdge edge = graph.getEdge(current, neighbor);
        if (!canWalkEdge(edge)) continue;
        double newCost =
            costFromStart.getOrDefault(current, Double.POSITIVE_INFINITY) + edge.getDist();
        if (newCost < costFromStart.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
          cameFrom.put(neighbor, current);
          costFromStart.put(neighbor, newCost);
          openSet.add(neighbor); // Re-add the neighbor with its updated cost
        }
      }
    }

    Main.getController()
        .log(
            "No path found from "
                + start
                + " to goals: "
                + goals.stream()
                    .map(node -> "(" + node.getX() + ", " + node.getY() + ")")
                    .collect(Collectors.joining(", ")));
    return new ArrayList<>();
  }

  private List<WebwalkNode> reconstructPath(
      Map<WebwalkNode, WebwalkNode> cameFrom, WebwalkNode current) {
    List<WebwalkNode> path = new ArrayList<>();
    while (current != null) {
      path.add(0, current); // Prepend to path
      current = cameFrom.get(current);
    }
    return path;
  }

  private boolean canWalkEdge(WebwalkEdge edge) {
    if (!edge.getLabel().isPresent()) return true;
    switch (edge.getLabel().get()) {
      case "alkharidGate":
        return Main.getController().isQuestComplete(9)
            || Main.getController().getInventoryItemCount(ItemId.COINS.getId()) >= 10;
      case "miningGuildDoor":
        return Main.getController().getCurrentStat(Main.getController().getStatId("Mining")) >= 60;
      default:
        return true;
    }
  }

  private boolean executeCustomLabelFunction(String label) {
    switch (label) {
      case "alkharidGate":
        return CustomLabelHandlers.alkharidGate();
      case "southFallyTavGate":
        return CustomLabelHandlers.southFallyTavGate();
      case "lummyNorthCowGate":
        return CustomLabelHandlers.lummyNorthCowGate();
      case "lummyNorthCabbageGate":
        return CustomLabelHandlers.lummyNorthCabbageGate();
      case "lummyEastCowGate":
        return CustomLabelHandlers.lummyEastCowGate();
      case "northFallyTavGate":
        return CustomLabelHandlers.northFallyTavGate();
      case "lummySheepEastGate":
        return CustomLabelHandlers.lummySheepEastGate();
      case "lummyNorthSheepGate":
        return CustomLabelHandlers.lummyNorthSheepGate();
      case "gerrantHouseDoor":
        return CustomLabelHandlers.gerrantHouseDoor();
      case "catherbyChefDoor":
        return CustomLabelHandlers.catherbyChefDoor();
      case "dwarvenMineFaladorEntrance":
        return CustomLabelHandlers.dwarvenMineFaladorEntrance();
      case "dwarvenMineCannonEntrance":
        return CustomLabelHandlers.dwarvenMineCannonEntrance();
      case "digsiteGate":
        return CustomLabelHandlers.digsiteGate();
      case "gnomeTreeGate":
        return CustomLabelHandlers.gnomeTreeGate();
      case "gnomeAgilityClimbFirstNet":
        return CustomLabelHandlers.gnomeAgilityClimbFirstNet();
      case "gnomeAgilityClimbTower":
        return CustomLabelHandlers.gnomeAgilityClimbTower();
      case "gnomeAgilityRopeSwing":
        return CustomLabelHandlers.gnomeAgilityRopeSwing();
      case "gnomeAgilityClimbDownTower":
        return CustomLabelHandlers.gnomeAgilityClimbDownTower();
      case "skipTutorial":
        return CustomLabelHandlers.skipTutorial();
      case "lummyNorthChickensGate":
        return CustomLabelHandlers.lummyNorthChickensGate();
      default:
        Main.getController().log("Missing function for label: " + label);
        return false;
    }
  }
}
