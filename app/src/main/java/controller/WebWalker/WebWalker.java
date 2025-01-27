package controller.WebWalker;

import bot.Main;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import models.entities.ItemId;
import models.entities.QuestId;
import models.entities.SkillId;

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
   * @return if successfully took a step towards the closest end coordinate pair
   */
  public boolean webwalkTowards(int currentX, int currentY, int[][] endCoords) {
    long startTime = System.currentTimeMillis();
    Map<WebwalkNode, int[]> closestNodesToEndCoords = new HashMap<>();
    for (int[] coords : endCoords) {
      WebwalkNode node = graph.findClosestNode(coords[0], coords[1]);
      closestNodesToEndCoords.put(node, coords);
    }

    if (closestNodesToEndCoords.isEmpty()) {
      System.out.println(
          "No walkable end nodes found, yeeting off in a random direction to get unstuck..");
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
    System.out.println(
        "Path calculated in "
            + (System.currentTimeMillis() - startTime)
            + "ms: "
            + pathLog.substring(0, pathLog.length() - 4));

    if (path.size() == 1) {
      Main.getController().log("Walking to the last node..");
      Main.getController().walkDamnit(path.get(0).getX(), path.get(0).getY());
      return true;
    }

    WebwalkEdge edge = graph.getEdge(path.get(0), path.get(1));
    if (edge != null && edge.getLabel().isPresent()) {
      System.out.println("Walking along " + edge + " with label: " + edge.getLabel().get());
      if (executeCustomLabelFunction(edge.getLabel().get())) {
        Main.getController().walkDamnit(path.get(1).getX(), path.get(1).getY());
        return true;
      } else {
        System.out.println("Failed to execute custom function for label: " + edge.getLabel().get());
        return false;
      }
    } else {
      Main.getController().walkDamnit(path.get(1).getX(), path.get(1).getY());
      return true;
    }
  }

  /**
   * Returns a rough estimate of the distance of a webwalker path.
   *
   * @param x1 int -- X1
   * @param y1 int -- Y1
   * @param x2 int -- X2
   * @param y2 int -- Y2
   * @return int
   */
  public int distanceBetween(int x1, int y1, int x2, int y2) {
    Map<WebwalkNode, int[]> closestNodesToEndCoords = new HashMap<>();
    int[][] endCoords = new int[][] {{x2, y2}};
    for (int[] coords : endCoords) {
      WebwalkNode node = graph.findClosestNode(coords[0], coords[1]);
      closestNodesToEndCoords.put(node, coords);
    }
    WebwalkNode startNode = graph.findClosestNode(x1, y1);

    Set<WebwalkNode> endNodes = closestNodesToEndCoords.keySet();
    List<WebwalkNode> path = dijkstraPathfinding(startNode, endNodes);

    int distance = 0;
    WebwalkNode previousNode = path.get(0);

    for (WebwalkNode current : path) {
      if (current.equals(previousNode)) continue;
      int stepDistance =
          Main.getController()
              .distance(previousNode.getX(), previousNode.getY(), current.getX(), current.getY());

      distance += stepDistance;
      previousNode = current;
    }
    System.out.printf(
        "The distance between (%s, %s) and (%s, %s) is roughly: %s%n", x1, y1, x2, y2, distance);
    return distance;
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
          openSet.remove(
              neighbor); // Remove the neighbor so we can update it's priority queue placement
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
        return Main.getController().isQuestComplete(QuestId.PRINCE_ALI_RESCUE.getId())
            || Main.getController().getInventoryItemCount(ItemId.COINS.getId()) >= 10;
      case "miningGuildDoor":
      case "miningGuildLadder":
        return Main.getController().getCurrentStat(SkillId.MINING.getId()) >= 60;
      case "taverleySteppingStones":
        return Main.getController().getCurrentStat(SkillId.AGILITY.getId()) >= 50
            && Main.getController().getCurrentStat(SkillId.HITS.getId()) > 20;
      case "dwarfTunnel":
        return Main.getController().isQuestComplete(QuestId.FISHING_CONTEST.getId());
      case "witchsHouseDoor":
        return Main.getController().isQuestComplete(QuestId.WITCHS_HOUSE.getId())
            || Main.getController().getInventoryItemCount(ItemId.FRONT_DOOR_KEY.getId()) > 0;
      case "portSarimKaramjaBoat":
      case "brimhavenKaramjaGate":
        return Main.getController().getInventoryItemCount(ItemId.COINS.getId()) >= 60;
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
      case "lummyCabbageGate":
        return CustomLabelHandlers.lummyCabbageGate();
      case "mcgroubersGate":
        return CustomLabelHandlers.mcgroubersGate();
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
      case "lummyNorthGarlicGate":
        return CustomLabelHandlers.lummyNorthGarlicGate();
      case "lummyNorthPotatoGate":
        return CustomLabelHandlers.lummyNorthPotatoGate();
      case "varrockPalaceNorthwestLadder":
        return CustomLabelHandlers.varrockPalaceNorthwestLadder();
      case "varrockPalaceFence":
        return CustomLabelHandlers.varrockPalaceFence();
      case "varrockEastDigsiteGate":
        return CustomLabelHandlers.varrockEastDigsiteGate();
      case "brimhavenArdyBoat":
        return CustomLabelHandlers.brimhavenArdyBoat();
      case "portSarimKaramjaBoat":
        return CustomLabelHandlers.portSarimKaramjaBoat();
      case "brimhavenKaramjaGate":
        return CustomLabelHandlers.brimhavenKaramjaGate();
      case "lummyEastChickenGate":
        return CustomLabelHandlers.lummyEastChickenGate();
      case "wizardTowerDoor":
        return CustomLabelHandlers.wizardTowerDoor();
      case "wizardTowerBasement":
        return CustomLabelHandlers.wizardTowerBasement();
      case "lummyNorthWheatSouthGate":
        return CustomLabelHandlers.lummyNorthWheatSouthGate();
      case "lummyNorthWheatNorthGate":
        return CustomLabelHandlers.lummyNorthWheatNorthGate();
      case "witchsHouseDoor":
        return CustomLabelHandlers.witchsHouseDoor();
      case "taverleySteppingStones":
        return CustomLabelHandlers.taverleySteppingStones();
      case "dwarfTunnel":
        return CustomLabelHandlers.dwarfTunnel();
      case "faladorWestBankDoor":
        return CustomLabelHandlers.faladorWestBankDoor();
      case "miningGuildDoor":
        return CustomLabelHandlers.miningGuildDoor();
      case "miningGuildLadder":
        return CustomLabelHandlers.miningGuildLadder();
      default:
        Main.getController().log("Missing function for label: " + label);
        return false;
    }
  }
}
