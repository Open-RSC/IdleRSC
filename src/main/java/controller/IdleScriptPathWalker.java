package controller;

import static controller.Node.WORLD_H;
import static controller.Node.WORLD_W;
import static controller.Node.getNode;
import static controller.Node.resetNodes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import models.entities.ItemId;
import utils.Extractor;

class IdleScriptPathWalker {

  private final Controller controller;
  private static final byte[][] WALKABLE = createWalkable();

  /*
   * - Features, etc.
   * Calculates a path from one point in the world to another and walks
   * there.
   * Prefers road over ground.
   * Can open many doors. Extra IDs and specifics like which key needs to
   * be used are appreciated.
   *
   * - Limitations, etc.
   * Can't change levels (with ladders, etc) or use any kind of
   * teleportation point.
   * No proper handling of direction with bounds.
   * Object information in the default loaded map may be inaccurate.
   *
   * - Credits
   * Stormy
   * Wikipedia
   * Xueqiao Xu <xueqiaoxu@gmail.com>
   *
   * Contributions are appreciated.
   */

  private static final boolean DEBUG = false;

  private Node[][] nodes;
  private Node[] path;
  private long wait_time;
  private int path_ptr;
  private static final int[] objects_1 = new int[] {64, 60, 137, 138, 93};
  private static final int[] bounds_1 = new int[] {2, 8, 55, 68, 44, 74, 117};

  public IdleScriptPathWalker(Controller controller) {
    this.controller = controller;
  }

  public IdleScriptPathWalker init() {
    if (nodes == null) {
      nodes = new Node[WORLD_W][WORLD_H];
      for (int x = 0; x < WORLD_W; ++x) {
        for (int y = 0; y < WORLD_H; ++y) {
          byte i = WALKABLE[x][y];
          if (i != 0) {
            Node n = new Node(x, y);
            n.walkable = i;
            nodes[x][y] = n;
          }
        }
      }
    }

    return this;
  }

  private static byte[][] createWalkable() {
    System.out.print("Reading map... ");

    byte[][] walkable = new byte[WORLD_W][WORLD_H];
    String dataPath = "/map/data";
    try (BufferedInputStream in =
        new BufferedInputStream(Extractor.extractResourceAsStream(dataPath))) {
      for (int i = 0; i < WORLD_W; ++i) {
        int read = 0;
        do {
          int r = in.read(walkable[i], read, WORLD_H - read);
          if (r == -1) {
            throw new IOException("Unexpected EOF");
          }
          read += r;
        } while (read != WORLD_H);
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    System.out.println("done.");
    return walkable;
  }

  public boolean walkPath() {
    if (path == null) return false;
    Node last = path[path.length - 1];
    if (controller.currentX() == last.x && controller.currentY() == last.y) {
      path = null;
      return false;
    }
    long c_time = System.currentTimeMillis();
    if (c_time >= wait_time) {
      Node n = getCurrentDest();
      if (n == null) {
        return true;
      }
      int x = n.x;
      int y = n.y;

      // TODO: Those if statements can be turned into an abstraction, e.g PathWalkerObstacle
      if (isAtApproxCoords(329, 487, 12)
          && (n.x > 341)) { // moved 2 tiles east, increase radius by 2
        atObject(341, 487);
        System.out.println("Opening Tav gate going west");
        wait_time = c_time + 8000;
      } else if (isAtApproxCoords(352, 487, 10) && (n.x <= 341)) {
        atObject(341, 487);
        System.out.println("Opening Tav gate going east");
        wait_time = c_time + 8000;
      } else if ((isAtApproxCoords(343, 593, 12) || isAtApproxCoords(356, 584, 7)) && (n.y < 581)) {
        controller.walkTo(343, 581);
        atObject(343, 581);
        System.out.println("Opening Tav gate going north");
        wait_time = c_time + 8000;
      } else if ((isAtApproxCoords(343, 570, 11) || isAtApproxCoords(342, 574, 7))
          && (n.y >= 581)) {
        controller.walkTo(343, 580);
        atObject(343, 581);
        System.out.println("Opening Tav gate going south");
        wait_time = c_time + 8000;
      } else if (isAtApproxCoords(703, 542, 10) && (n.y <= 531)) {
        atObject(703, 531);
        System.out.println("Opening Gnome Tree gate going north");
        wait_time = c_time + 8000;
      } else if (isAtApproxCoords(703, 521, 10) && (n.y > 531)) {
        atObject(703, 531);
        System.out.println("Opening Gnome Tree gate going south");
        wait_time = c_time + 8000;
      } else if (isAtApproxCoords(445, 682, 10) && (n.x < 435)) {
        atObject(434, 682);
        System.out.println("Opening Karamja gate, going East");
        wait_time = c_time + 8000;
      } else if (isAtApproxCoords(424, 521, 10) && (n.x >= 435)) {
        atObject(434, 682);
        System.out.println("Opening Karamja gate, going West");
        wait_time = c_time + 10000;
      } else if (isAtApproxCoords(111, 152, 10) && (n.y < 142)) {
        atObject(111, 142);
        System.out.println("Opening Wilderness gate, going north");
        wait_time = c_time + 1280;
      } else if (isAtApproxCoords(117, 131, 10) && (n.y >= 142)) {
        atObject(111, 142);
        System.out.println("Opening Wilderness gate, going south");
        wait_time = c_time + 1280;
      } else if (isAtApproxCoords(102, 649, 10) && (n.x < 92)) {
        // node.x represents value on the other side of the gate
        //  radius puts the circle zone so the furthest left tile on the inside of the gate
        if (controller.getInventoryItemCount(ItemId.COINS.getId()) < 10) {
          System.out.println("Not enough coins, going north");
          controller.walkTo(101, 636); // refactor this eventually, functional for now
          controller.walkTo(107, 625);
          controller.walkTo(113, 611);
          controller.walkTo(113, 599);
          controller.walkTo(104, 583);
          controller.walkTo(91, 574);
          controller.walkTo(77, 574); // at crossroads
          controller.walkTo(77, 579);
          controller.walkTo(85, 590);
          controller.walkTo(87, 615);
          controller.walkTo(87, 634);
          controller.walkTo(88, 644);
          controller.walkTo(
              88, 650); // bot needs to go back to the goal node, or you get pathing failure
        }
        int npcId = 161;
        // if prince ali resQ is completed, no response is required, this is negative
        if (controller.isInOptionMenu()
            && controller.getInventoryItemCount(ItemId.COINS.getId()) > 9) {
          controller.optionAnswer(2);
          wait_time = c_time + 4000; // wait to open gate
        }
        if (controller.getNearestNpcById(npcId, false) != null
            && !controller.isInOptionMenu()
            && controller.getInventoryItemCount(ItemId.COINS.getId()) > 9) {
          System.out.println("Entering Al-Kharid Gate");
          controller.npcCommand1(controller.getNearestNpcById(npcId, false).serverIndex);
          wait_time = c_time + 9000; // wait to open gate
        }
      } else if ((isAtApproxCoords(79, 655, 10) || isAtApproxCoords(86, 669, 10)) && (n.x >= 92)) {
        // node.x represents value on the other side of the gate
        //  radius puts the circle zone so the furthest left tile on the inside of the gate
        if (controller.getInventoryItemCount(10) < 10) { // need 10 coins to pass
          System.out.println("Not enough coins, going north");
          controller.walkTo(88, 650); // refactor this eventually, functional for now
          controller.walkTo(88, 644);
          controller.walkTo(87, 634);
          controller.walkTo(87, 615);
          controller.walkTo(85, 590);
          controller.walkTo(77, 579);
          controller.walkTo(77, 574); // at crossroads
          controller.walkTo(91, 574);
          controller.walkTo(104, 583);
          controller.walkTo(113, 599);
          controller.walkTo(113, 611);
          controller.walkTo(107, 625);
          controller.walkTo(
              101, 636); // bot needs to go back to the goal node, or you get pathing failure
        }
        int npcId = 162;
        // if prince ali resQ is completed, no response is required, this is negative
        if (controller.isInOptionMenu()
            && controller.getInventoryItemCount(ItemId.COINS.getId()) > 9) {
          controller.optionAnswer(2);
          wait_time = c_time + 4000; // wait to open gate
        }
        if (controller.getNearestNpcById(npcId, false) != null
            && !controller.isInOptionMenu()
            && controller.getInventoryItemCount(ItemId.COINS.getId()) > 9) {
          System.out.println("Entering Al-Kharid Gate");
          controller.npcCommand1(controller.getNearestNpcById(npcId, false).serverIndex);
          wait_time = c_time + 9000; // wait to open gate
        }
      } else {
        controller.walkTo(x, y);
      }

      int d = distanceTo(x, y);
      if (d != 0) {
        wait_time = c_time + 600L * d;
      } else {
        wait_time = c_time + 800;
      }
    }
    return true;
  }

  private int distanceTo(int x1, int y1, int x2, int y2) {
    return (int) Math.hypot(Math.abs(x1 - x2), Math.abs(y1 - y2));
  }

  private int distanceTo(int x1, int y1) {
    return distanceTo(x1, y1, controller.currentX(), controller.currentY());
  }

  public void resetWait() {
    wait_time = System.currentTimeMillis();
  }

  private Node getCurrentDest() {
    long c_time = System.currentTimeMillis();
    int ptr = path_ptr;
    int x, y;
    int orig = ptr;
    do {
      if (ptr >= (path.length - 1)) {
        break;
      }
      Node cur = path[ptr];
      x = cur.x;
      y = cur.y;
      Node next = path[++ptr];
      if (!controller.isReachable(next.x, next.y, false) && handleObstacles(x, y)) {
        // you may wish to modify this.
        wait_time = c_time + 3000;
        path_ptr = ptr - 1;
        return null;
      }
    } while (distanceTo(x, y) < 6);

    ptr = orig;

    int min_dist = random(7, 18);
    int max_dist = 20;
    int dist;
    int loop = 0;
    do {
      if ((++ptr) >= path.length) {
        min_dist = random(1, 18);
        ptr = orig;
      }
      Node n = path[ptr];
      x = n.x;
      y = n.y;
      dist = distanceTo(x, y);
      if (dist > max_dist) {
        min_dist = random(1, 18);
        ptr = orig;
      }
      if ((loop++) > 500) {
        System.out.println("Pathing failure");
        return null;
      }
    } while (dist < min_dist || !controller.isReachable(x, y, false));
    path_ptr = ptr;
    return path[path_ptr];
  }

  private boolean handleObstacles(int x, int y) {
    int id = controller.getWallObjectIdAtCoord(x, y);
    if (id != -1) {
      for (int i : bounds_1) {
        if (id != i) continue;
        controller.atWallObject(x, y);
        return true;
      }
    }
    // is this ridiculous or not? heh
    if (handleObject(x, y)) return true;
    if (handleObject(x + 1, y)) return true;
    if (handleObject(x - 1, y)) return true;
    if (handleObject(x, y + 1)) return true;
    if (handleObject(x, y - 1)) return true;
    if (handleObject(x - 1, y - 1)) return true;
    if (handleObject(x + 1, y + 1)) return true;
    if (handleObject(x - 1, y + 1)) return true;
    return handleObject(x + 1, y - 1);
  }

  private boolean handleObject(int x, int y) {
    int id = controller.getObjectAtCoord(x, y);
    if (id == -1) return false;
    for (int i : objects_1) {
      if (id != i) continue;
      controller.atObject(x, y);
      return true;
    }
    return false;
  }

  public Node[] setPath(Path p) {
    if (p == null) {
      path = null;
      return new Node[0];
    }
    if (p.n == path) return new Node[0];
    path = p.n;
    wait_time = 0;
    path_ptr = 0;
    return path;
  }

  public final Path calcPath(int x, int y) {
    return calcPath(controller.currentX(), controller.currentY(), x, y);
  }

  public Path calcPath(int x1, int y1, int x2, int y2) {
    Node start = getNode(nodes, x1, y1);
    if (start == null) return null;
    Node end = getNode(nodes, x2, y2);
    if (end == null) return null;
    Node[] n = astar(start, end);
    if (n == null) return null;
    Path p = new Path();
    p.n = n;
    return p;
  }

  private Node[] astar(Node start, Node goal) {
    if (DEBUG) {
      System.out.print("Calculating path from " + start + " to " + goal + "... ");
    }

    long start_ms = System.currentTimeMillis();

    start.totalCostFromStartToGoalThroughY = (short) start.estHeuristicCost(goal);

    Deque<Node> open = new ArrayDeque<>(32);
    open.add(start);
    start.isTentativeNodeToBeEvaluated = true;

    // The map of navigated nodes
    Map<Node, Node> came_from = new HashMap<>();

    Node[][] nodes = this.nodes;

    while (!open.isEmpty()) {
      Node cur = getLowestFScore(open);
      if (cur.equals(goal)) {
        Node[] n = constructPath(came_from, start, goal);
        resetNodes(nodes);
        if (DEBUG) {
          System.out.print("done. ms taken: ");
          System.out.println(System.currentTimeMillis() - start_ms);
        }
        return n;
      }
      open.remove(cur);
      cur.isTentativeNodeToBeEvaluated = false;
      cur.isEvaluated = true;
      for (Node n : cur.getNeighbors(nodes)) {
        int t_gscore = cur.costFromStartAlongBestKnownPath + n.distFrom(cur);
        int t_fscore = t_gscore + n.estHeuristicCost(goal);
        if (n.isEvaluated && t_fscore >= n.totalCostFromStartToGoalThroughY) {
          continue;
        }
        if (!n.isTentativeNodeToBeEvaluated) {
          came_from.put(n, cur);
          n.costFromStartAlongBestKnownPath = (short) t_gscore;
          n.totalCostFromStartToGoalThroughY = (short) t_fscore;
          open.add(n);
          n.isTentativeNodeToBeEvaluated = true;
        }
      }
    }

    resetNodes(nodes);
    if (DEBUG) {
      System.out.print("failed! ms taken: ");
      System.out.println(System.currentTimeMillis() - start_ms);
    }
    return null;
  }

  private static Node[] constructPath(Map<Node, Node> came_from, Node start, Node goal) {

    Deque<Node> path = new ArrayDeque<>();
    Node p = came_from.get(goal);
    while (p != start) {
      path.push(p);
      p = came_from.get(p);
    }
    path.push(p);
    path.add(goal);
    return path.toArray(new Node[path.size()]);
  }

  private static Node getLowestFScore(Deque<Node> open) {
    Node best_n = null;
    int best_f = Integer.MAX_VALUE;
    int f;
    for (Node n : open) {
      f = n.totalCostFromStartToGoalThroughY;
      if (f < best_f) {
        best_n = n;
        best_f = f;
      }
    }
    return best_n;
  }

  /**
   * Generates a psuedo-random number.
   *
   * @param min lowest possible number to generate.
   * @param max highest possible number to generate.
   * @return a psuedo-random number between min and max.
   */
  public static int random(int min, int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  public boolean isAtApproxCoords(int x, int y, int radius) {
    return distanceTo(x, y) <= radius;
  }

  public void atObject(int x, int y) {
    controller.atObject(x, y);
  }
}
