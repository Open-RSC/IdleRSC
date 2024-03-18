package controller.WebWalker;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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

  public WebwalkNode findClosestNode(int x, int y) {
    return BruteForceUtils.findClosestNode(x, y, adjacencyList);
  }
}
