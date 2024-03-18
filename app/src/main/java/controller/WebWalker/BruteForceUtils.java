package controller.WebWalker;

import bot.Main;
import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import utils.Extractor;

public class BruteForceUtils {
  /**
   * Utils that involve loading the whole walkable map and calculting a path through parts of it
   * Calling any function here will make your bot one-time load like another 4MB of RAM (I assume,
   * didn't actually check)
   */
  private static byte[][] walkable = null;

  private static Map<String, WebwalkNode> closestNodeCache =
      new HashMap<>(); // Cache for storing search results

  public static WebwalkNode findClosestNode(
      int x, int y, Map<WebwalkNode, List<WebwalkEdge>> adjacencyList) {
    String cacheKey = x + "," + y;
    if (closestNodeCache.containsKey(cacheKey)) {
      if (closestNodeCache.get(cacheKey) == null) {
        Main.getController().log("Could not find closest node to " + x + "," + y);
      }
      return closestNodeCache.get(cacheKey);
    }

    if (walkable == null) {
      walkable = new byte[900][4050];
      String dataPath = "/map/data";
      try (BufferedInputStream in =
          new BufferedInputStream(Extractor.extractResourceAsStream(dataPath))) {
        for (int i = 0; i < 900; ++i) {
          int read = 0;
          do {
            int r = in.read(walkable[i], read, 4050 - read);
            read += r;
          } while (read != 4050);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    // Uses simple BFS to find the closest node
    Set<WebwalkNode> visited = new HashSet<>();
    Queue<WebwalkNode> queue = new LinkedList<>();
    queue.add(new WebwalkNode(x, y));

    while (!queue.isEmpty()) {
      WebwalkNode current = queue.poll();
      if (!isWalkable(current) || visited.contains(current)) continue;
      if (adjacencyList.containsKey(current)) {
        // Main.getController().log("Closest node to " + x + "," + y + " is " +
        // current);
        closestNodeCache.put(cacheKey, current);
        return current;
      }
      visited.add(current);
      // Check adjacent positions
      for (int[] dir : new int[][] {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}) {
        WebwalkNode next = new WebwalkNode(current.getX() + dir[0], current.getY() + dir[1]);
        queue.add(next);
      }
    }

    Main.getController()
        .log(
            "Can't find the closest walkable node to "
                + x
                + ", "
                + y
                + ", using Euclidean closest.");
    closestNodeCache.put(cacheKey, findClosestNodeEuclidian(x, y, adjacencyList));
    return closestNodeCache.get(cacheKey);
  }

  private static WebwalkNode findClosestNodeEuclidian(
      int endX, int endY, Map<WebwalkNode, List<WebwalkEdge>> adjacencyList) {
    WebwalkNode closestNode = null;
    double closestDistance = Double.MAX_VALUE;

    for (WebwalkNode node : adjacencyList.keySet()) {
      double distance =
          Math.sqrt(Math.pow(node.getX() - endX, 2) + Math.pow(node.getY() - endY, 2));
      if (distance < closestDistance) {
        closestDistance = distance;
        closestNode = node;
      }
    }

    return closestNode;
  }

  public static List<int[]> findPath(int endX, int endY) {
    Map<WebwalkNode, WebwalkNode> predecessor = new HashMap<>();
    Set<WebwalkNode> visited = new HashSet<>();
    Queue<WebwalkNode> queue = new LinkedList<>();

    WebwalkNode startNode =
        new WebwalkNode(Main.getController().currentX(), Main.getController().currentY());
    WebwalkNode endNode = new WebwalkNode(endX, endY);
    queue.add(startNode);
    visited.add(startNode);
    predecessor.put(startNode, null); // Start node has no predecessor

    while (!queue.isEmpty()) {
      WebwalkNode current = queue.poll();

      if (current.equals(endNode)) {
        LinkedList<int[]> path = new LinkedList<>();
        for (WebwalkNode at = endNode; at != null; at = predecessor.get(at)) {
          path.addFirst(new int[] {at.getX(), at.getY()});
        }
        return path;
      }

      for (int[] dir : new int[][] {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}) {
        WebwalkNode next = new WebwalkNode(current.getX() + dir[0], current.getY() + dir[1]);
        if (!visited.contains(next) && isWalkable(next)) {
          queue.add(next);
          visited.add(next);
          predecessor.put(next, current);
        }
      }
    }

    return null;
  }

  private static boolean isWalkable(WebwalkNode node) {
    // Check if the node is walkable, unless we're already on it
    // The walkable datamap is outdated/not accurate. This check is needed so that
    // if we're starting on a node that is actually walkable but the map thinks
    // it's not walkable, the algorithm pretends it is walkable and continues
    return node.getX() >= 0
            && node.getX() < walkable.length
            && node.getY() >= 0
            && node.getY() < walkable[0].length
            && walkable[node.getX()][node.getY()] != 0
        || node.getX() == Main.getController().currentX()
            && node.getY() == Main.getController().currentY();
  }
}
