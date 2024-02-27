package controller.WebWalker;

import bot.Main;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import utils.Extractor;

public class WebwalkGraph {
  private final Map<WebwalkNode, List<WebwalkEdge>> adjacencyList = new HashMap<>();

  public WebwalkGraph(String filePath) {
    try (Scanner scanner = new Scanner(new File(filePath))) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        // Split the line into parts: node1x, node1y, node2x, node2y, dist, label
        String[] parts = line.split(",");

        // Create nodes and edge with distance and label
        WebwalkNode from = new WebwalkNode(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        WebwalkNode to = new WebwalkNode(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        int cost = Integer.parseInt(parts[4]);
        String label = parts.length > 5 ? parts[5] : null; // Label is optional

        adjacencyList
            .computeIfAbsent(from, k -> new ArrayList<>())
            .add(new WebwalkEdge(from, to, cost, label));
        adjacencyList
            .computeIfAbsent(to, k -> new ArrayList<>())
            .add(new WebwalkEdge(to, from, cost, label));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public List<WebwalkNode> getNeighbors(WebwalkNode node) {
    return adjacencyList.getOrDefault(node, new ArrayList<>()).stream()
        .map(WebwalkEdge::getTo)
        .collect(Collectors.toList());
  }

  public List<WebwalkEdge> getEdgesFrom(WebwalkNode node) {
    return adjacencyList.getOrDefault(node, new ArrayList<>());
  }

  private byte[][] walkable = null;

  private Map<String, WebwalkNode> cache = new HashMap<>(); // Cache for storing search results

  public WebwalkNode findClosestNode(int x, int y) {
    String cacheKey = x + "," + y;
    if (cache.containsKey(cacheKey)) {
      if (cache.get(cacheKey) == null) {
        Main.getController().log("Could not find closest node to " + x + "," + y);
      }
      return cache.get(cacheKey);
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
      if (current.getX() < 0
          || current.getX() >= 900
          || current.getY() < 0
          || current.getY() >= 4050
          // Check if the node is walkable, unless we're already on it
          // The walkable datamap is outdated/not accurate. This check is needed so that
          // if we're starting on a node that is actually walkable but the map thinks
          // it's not walkable, the algorithm pretends it is walkable and continues
          || walkable[current.getX()][current.getY()] == 0
              && (Main.getController().currentX() != current.getX()
                  || Main.getController().currentY() != current.getY())
          || visited.contains(current)) continue;
      if (adjacencyList.containsKey(current)) {
        // Main.getController().log("Closest node to " + x + "," + y + " is " +
        // current);
        cache.put(cacheKey, current);
        return current;
      }
      visited.add(current);
      // Check adjacent positions
      for (int[] dir : new int[][] {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}) {
        WebwalkNode next = new WebwalkNode(current.getX() + dir[0], current.getY() + dir[1]);
        queue.add(next);
      }
    }

    Main.getController().log("Could not find closest node to " + x + "," + y);
    cache.put(cacheKey, null);
    return null;
  }

  // Method to get an edge between two nodes, if it exists
  public WebwalkEdge getEdge(WebwalkNode from, WebwalkNode to) {
    List<WebwalkEdge> edges = adjacencyList.get(from);
    if (edges != null) {
      for (WebwalkEdge edge : edges) {
        if (edge.getTo().equals(to)) {
          return edge;
        }
      }
    }
    return null;
  }

  public WebwalkNode findClosestNodeEuclidian(int endX, int endY) {
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
}
