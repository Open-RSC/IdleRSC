package scripting.apos;

import compatibility.apos.Script;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import utils.Extractor;

/** kRiStOf's edits: added paint added runtime to paint Kaila Edits: Added ~30 new Locations */
public class PathWalker extends Script implements ActionListener, ItemListener {

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

  // class for encapsulation
  public static class Path {
    private Node[] n;
  }

  private static final class Node {
    final short x;
    final short y;
    // Cost from start along best known path.
    short g;
    // Estimated total cost from start to goal through y.
    short f;
    // Heuristic cost to goal
    short h;
    byte walkable;
    ArrayList<Node> neighbors;
    // Already evaluated.
    boolean closed;
    // Tentative node to be evaluated
    boolean open;

    Node(int x, int y) {
      this.x = (short) x;
      this.y = (short) y;
      h = -1;
    }

    void reset() {
      h = -1;
      g = 0;
      f = 0;
      closed = false;
      open = false;
    }

    int distFrom(Node n) {
      final int sx = this.x - n.x;
      if (sx == 0) return 1;
      final int sy = this.y - n.y;
      if (sy == 0) return 1;
      int dx = Math.abs(sx);
      int dy = Math.abs(sy);
      dx *= dx;
      dy *= dy;
      return (int) Math.sqrt(dx + dy);
    }

    int estHeuristicCost(Node n) {
      // manhattan
      if (h == -1) {
        h = (short) ((2 - walkable) * (Math.abs(this.x - n.x) + Math.abs(this.y - n.y)));
      }
      return h;
    }

    ArrayList<Node> getNeighbors(Node[][] nodes) {
      if (this.neighbors != null) {
        return this.neighbors;
      }

      final boolean allowDiagonal = true;
      final boolean dontCrossCorners = true;

      boolean s0 = false,
          d0 = false,
          s1 = false,
          d1 = false,
          s2 = false,
          d2 = false,
          s3 = false,
          d3 = false;

      final int x = this.x;
      final int y = this.y;
      Node n;
      final ArrayList<Node> neighbors = new ArrayList<>(0);

      n = getNode(nodes, x, y - 1);
      if (n != null) {
        neighbors.add(n);
        s0 = true;
      }
      n = getNode(nodes, x + 1, y);
      if (n != null) {
        neighbors.add(n);
        s1 = true;
      }
      n = getNode(nodes, x, y + 1);
      if (n != null) {
        neighbors.add(n);
        s2 = true;
      }
      n = getNode(nodes, x - 1, y);
      if (n != null) {
        neighbors.add(n);
        s3 = true;
      }

      if (!allowDiagonal) {
        return neighbors;
      }

      if (dontCrossCorners) {
        d0 = s3 && s0;
        d1 = s0 && s1;
        d2 = s1 && s2;
        d3 = s2 && s3;
      } else {
        d0 = s3 || s0;
        d1 = s0 || s1;
        d2 = s1 || s2;
        d3 = s2 || s3;
      }

      n = getNode(nodes, x - 1, y - 1);
      if (n != null && d0) {
        neighbors.add(n);
      }
      n = getNode(nodes, x + 1, y - 1);
      if (n != null && d1) {
        neighbors.add(n);
      }
      n = getNode(nodes, x + 1, y + 1);
      if (n != null && d2) {
        neighbors.add(n);
      }
      n = getNode(nodes, x - 1, y + 1);
      if (n != null && d3) {
        neighbors.add(n);
      }

      this.neighbors = neighbors;
      return neighbors;
    }

    @Override
    public String toString() {
      return x + "," + y;
    }
  }

  public static class Location {

    public final String name;
    public final int x;
    public final int y;
    public final boolean bank;

    public Location(String name, int x, int y, boolean b) {
      this.name = name;
      this.x = x;
      this.y = y;
      this.bank = b;
    }

    @Override
    public String toString() {
      String s = bank ? "a bank" : "not a bank";
      return String.format("%s (%d, %d), %s", name, x, y, s);
    }
  }

  public static final Location[] locations =
      new Location[] {
        // Big city Travel
        new Location("Al Kharid", 87, 695, true),
        new Location("AK Mine Crossroads", 76, 573, false),
        new Location("Ardougne North", 580, 573, true),
        new Location("Ardougne South", 550, 612, true),
        new Location("Barbarian Village", 230, 513, false),
        new Location("Battle Field", 631, 634, false),
        new Location("Baxtorian Falls", 650, 450, false),
        new Location("Black Knights' Fortress", 267, 444, false),
        new Location("Bone Yard", 700, 648, false),
        new Location("Catherby", 440, 496, true),
        new Location("Champions Guild", 151, 551, false),
        new Location("Chaos Temple (Goblin Village)", 309, 434, false),
        new Location("Coal Trucks", 614, 477, false),
        new Location("Cooking Guild", 179, 489, false),
        new Location("Crafting Guild", 347, 599, false),
        new Location("Dark Wizards' Tower", 355, 572, false),
        new Location("Dark Wizards' Circle", 119, 546, false),
        new Location("Draynor", 220, 635, true),
        new Location("Draynor Manor", 210, 557, false),
        new Location("Dwarf Mine/cannon", 280, 490, false),
        new Location("Edgeville", 215, 450, true),
        new Location("Fight Arena", 615, 683, false),
        new Location("Fishing Guild", 586, 527, false),
        new Location("Falador East", 285, 570, true),
        new Location("Falador West", 330, 555, true),
        new Location("Feldip Hills", 630, 841, false),
        new Location("Gnome Tree", 692, 494, false),
        new Location("Goblin Village", 326, 453, false),
        new Location("Hemenster", 556, 497, false),
        new Location("Heroes Guild", 372, 443, false),
        new Location("Ice Cave Ladder", 288, 711, false),
        new Location("Kandarin Monastery", 589, 653, false),
        new Location("Kandarin Monastery mine", 620, 655, false),
        new Location("Legends Guild", 512, 554, false),
        new Location("Lost City Hut", 128, 686, false),
        new Location("Lumber Yard", 82, 436, false),
        new Location("Lumbridge", 128, 640, false),
        new Location("Lumbridge Windmill", 175, 608, false),
        new Location("Monastary", 257, 474, false),
        new Location("Port Khazard", 553, 702, false),
        new Location("Port Sarim", 270, 625, false),
        new Location("Rimmington", 320, 653, false),
        new Location("Shantay pass", 62, 730, false),
        new Location("Seers Village", 500, 453, true),
        new Location("Shilo Village", 401, 849, false),
        new Location("Sorcerors' Tower", 511, 512, false),
        new Location("Taverley", 373, 495, false),
        new Location("Varrock East", 102, 511, true),
        new Location("Varrock West", 150, 505, true),
        new Location("Wizard's Tower", 217, 687, false),
        new Location("Yanille", 587, 752, true),
        // Skilling Locations
        new Location("Skilling - Al-Kharid Mine", 71, 589, false),
        new Location("Skilling - Legends' Guild Mine", 521, 573, false),
        new Location("Skilling - Seers Magic Trees", 518, 490, false),
        new Location("Skilling - Varrock East Mine", 73, 542, false),
        new Location("Skilling - Varrock West Mine", 158, 540, false),
        // Karamja
        new Location("Karamja Only - Brimhaven", 466, 661, false),
        new Location(
            "Karamja Only - Musa Point",
            338,
            712,
            false), // pathwalker cannot return from musa, but will go to musa
        new Location("Karamja Only - Tai Bwo Wannai", 461, 760, false),
        new Location(
            "Karamja Only - Shilo Bridge", 449, 843, false), // autowalk cannot cross shilo bridge
        // Wilderness
        new Location("WILD - Skeleton mine", 269, 381, false),
        new Location("WILD - Dark Warriors' Fortress", 271, 352, false),
        new Location("WILD - Black Unicorns", 120, 302, false),
        new Location("WILD - Hobgoblin Mine", 213, 268, false),
        new Location("WILD - KBD Gate", 287, 185, false),
        new Location("WILD - Mage Bank", 222, 107, false),
        new Location("WILD - Agility Course", 296, 138, false)
      };
  private static final boolean DEBUG = false;
  private static final int WORLD_W = 900;
  private static final int WORLD_H = 4050;
  private Node[][] nodes;
  private Node[] path;
  private long wait_time;
  private int path_ptr;
  private Frame frame;
  private List choice;
  private TextField field_start;
  private TextField field_end;
  private static final int[] objects_1 = new int[] {64, 60, 137, 138, 93};
  private static final int[] bounds_1 = new int[] {2, 8, 55, 68, 44, 74, 117};
  private long start_time;

  public PathWalker(String ex) {
    // super(ex);
  }

  @Override
  public void init(String params) {
    start_time = -1L;

    if (nodes == null) {
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
        System.out.println("failed: " + ex);
        return; // add recursion for 5 loops
      }

      nodes = new Node[WORLD_W][WORLD_H];

      for (int x = 0; x < WORLD_W; ++x) {
        for (int y = 0; y < WORLD_H; ++y) {
          byte i = walkable[x][y];
          if (i != 0) {
            Node n = new Node(x, y);
            n.walkable = i;
            nodes[x][y] = n;
          }
        }
      }

      walkable = null;
      System.gc();
      System.out.println("done.");
    }

    // The bot will always call init with "".
    // If init is called with null, it is being called by another script,
    // so don't create the UI.
    if (params != null) {
      createFrame();
    }
  }

  @Override
  public int main() {
    while (this.frame.isVisible()) {
      try {
        Thread.sleep(50L);
      } catch (InterruptedException ex) {
      }
    }

    try {
      Thread.sleep(1000L);
    } catch (InterruptedException ex) {
    }

    if (start_time == -1L) {
      start_time = System.currentTimeMillis();
    }
    if (inCombat()) {
      resetWait();
      walkTo(getX(), getY());
      return random(400, 600);
    }
    if (!walkPath()) {
      System.out.println("Reached destination.");
      System.out.println("Stopping.");
      stopScript();
      Toolkit.getDefaultToolkit().beep();
    }
    return 0;
  }

  @Override
  public String toString() {
    return "Path Walker";
  }

  private void createFrame() {
    if (frame == null) {
      Panel bp = new Panel();
      Button button = new Button("OK");
      button.addActionListener(this);
      bp.add(button);
      button = new Button("Cancel");
      button.addActionListener(this);
      bp.add(button);

      Panel tp = new Panel();
      tp.setLayout(new GridLayout(0, 2, 2, 2));
      tp.add(new Label("Start location"));
      tp.add(field_start = new TextField());
      tp.add(new Label("Target location"));
      tp.add(field_end = new TextField());

      choice = new List(locations.length / 2);
      for (Location l : locations) {
        choice.add(l.name);
      }
      choice.addItemListener(this);

      Panel pp = new Panel();
      pp.setLayout(new BorderLayout());
      pp.add(new Label("Preset targets", Label.CENTER), BorderLayout.NORTH);
      pp.add(choice, BorderLayout.CENTER);

      field_end.setText("0,0");

      frame = new Frame(getClass().getSimpleName());
      frame.add(tp, BorderLayout.NORTH);
      frame.add(pp, BorderLayout.CENTER);
      frame.add(bp, BorderLayout.SOUTH);
      frame.pack();
      frame.setMinimumSize(frame.getSize());
      frame.setSize(245, 280);
    }
    String str = "0,0";
    StringBuilder sb = new StringBuilder();
    try {
      sb.append(getX());
      sb.append(',');
      sb.append(getY());
      str = sb.toString();
    } catch (Throwable t) {
    }
    field_start.setText(str);
    frame.toFront();

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.requestFocusInWindow();
  }

  public boolean walkPath() {
    if (path == null) return false;
    Node last = path[path.length - 1];
    if (getX() == last.x && getY() == last.y) {
      path = null;
      return false;
    }
    long c_time = System.currentTimeMillis();
    if (c_time >= wait_time) {
      Node n = getCurrentDest();
      if (n == null) return true;
      int x = n.x;
      int y = n.y;
      // radius can cross into the other side of gate because goal node has to be on the other side
      // to call if statement
      if (isAtApproxCoords(331, 487, 10) && (n.x > 341)) { // add point here
        atObject(341, 487);
        System.out.println("Opening Tav gate going west");
        wait_time = c_time + 8000;
      } else if (isAtApproxCoords(352, 487, 10) && (n.x <= 341)) {
        atObject(341, 487);
        System.out.println("Opening Tav gate going east");
        wait_time = c_time + 8000;
      } else if ((isAtApproxCoords(343, 593, 12) || isAtApproxCoords(356, 584, 7)) && (n.y < 581)) {
        atObject(343, 581);
        System.out.println("Opening Tav gate going north");
        wait_time = c_time + 8000;
      } else if ((isAtApproxCoords(343, 570, 11) || isAtApproxCoords(342, 574, 7))
          && (n.y >= 581)) {
        atObject(343, 581);
        System.out.println("Opening Tav gate going south");
        wait_time = c_time + 8000;
      } else if (isAtApproxCoords(703, 542, 10) && (n.y <= 531)) {
        atObject(703, 531);
        System.out.println("Opening Gnome Tree gate going north"); // add point here
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
        wait_time = c_time + 8000;
      } else if (isAtApproxCoords(111, 152, 10) && (n.y < 142)) {
        atObject(111, 142);
        System.out.println("Opening Wilderness gate, going north");
        c_time = wait_time;
      } else if (isAtApproxCoords(117, 131, 10) && (n.y >= 142)) {
        atObject(111, 142);
        System.out.println("Opening Wilderness gate, going south");
        c_time = wait_time;
      } else if (isAtApproxCoords(102, 649, 10) && (n.x < 92)) { // enter alkharid
        // node.x represents value on the other side of the gate
        //  radius puts the circle zone so the furthest left tile on the inside of the gate
        if (getInventoryCount(10) < 10) { // need 10 coins to pass
          System.out.println("Not enough coins, going around to AlKharid");
          walkTo(101, 636); // refactor this eventually, functional for now
          walkTo(107, 625);
          walkTo(113, 611);
          walkTo(113, 599);
          walkTo(104, 583);
          walkTo(91, 574);
          walkTo(77, 574); // at crossroads
          walkTo(77, 579);
          walkTo(85, 590);
          walkTo(87, 615);
          walkTo(87, 634);
          walkTo(88, 644);
          walkTo(88, 650); // bot needs to go back to the goal node, or you get pathing failure
        }
        int npc_id = 161;
        int[] npc = getNpcById(npc_id);
        // if prince ali resQ is completed, no response is required, this is negative
        if (isQuestMenu() && getInventoryCount(10) >= 10) {
          answer(2);
          wait_time = c_time + 4000; // wait to open gate
        }
        if (npc[0] != -1 && getInventoryCount(10) >= 10) {
          System.out.println("Entering Al-Kharid Gate");
          talkToNpc(npc[0]);
          wait_time = c_time + 9000; // wait to talk
        }
      } else if ((isAtApproxCoords(79, 655, 13) || isAtApproxCoords(86, 669, 10)) && (n.x >= 92)) {
        // node.x represents value on the other side of the gate
        //  radius puts the circle zone so the furthest left tile on the inside of the gate
        if (getInventoryCount(10) < 10) { // need 10 coins to pass
          System.out.println("Not enough coins, going around to Lumbridge");
          walkTo(88, 650); // refactor this eventually, functional for now
          walkTo(88, 644);
          walkTo(87, 634);
          walkTo(87, 615);
          walkTo(85, 590);
          walkTo(77, 579);
          walkTo(77, 574); // at crossroads
          walkTo(91, 574);
          walkTo(104, 583);
          walkTo(113, 599);
          walkTo(113, 611);
          walkTo(107, 625);
          walkTo(101, 636); // bot needs to go back to the goal node, or you get pathing failure
        }
        int npc_id = 162;
        int[] npc = getNpcById(npc_id);
        // if prince ali resQ is completed, no response is required, this is negative
        if (isQuestMenu() && getInventoryCount(10) >= 10) {
          answer(2);
          wait_time = c_time + 4000; // wait to open gate
        }
        if (npc[0] != -1 && getInventoryCount(10) >= 10) {
          System.out.println("Exiting Al-Kharid Gate");
          talkToNpc(npc[0]);
          wait_time = c_time + 9000; // wait to talk
        }
      } else {
        walkTo(x, y);
      }
      int d = distanceTo(x, y);
      if (d != 0) {
        wait_time = c_time + random(500 * d, 600 * d);
      } else {
        wait_time = c_time + random(600, 800);
      }
    }
    return true;
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
      if (!isReachable(next.x, next.y) && handleObstacles(x, y)) {
        // you may wish to modify this.
        wait_time = c_time + random(2500, 3000);
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
    } while (dist < min_dist || !isReachable(x, y));
    path_ptr = ptr;
    return path[path_ptr];
  }

  private boolean handleObstacles(int x, int y) {
    int id = getWallObjectIdFromCoords(x, y);
    if (id != -1) {
      for (int i : bounds_1) {
        if (id != i) continue;
        atWallObject(x, y);
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
    int id = getObjectIdFromCoords(x, y);
    if (id == -1) return false;
    for (int i : objects_1) {
      if (id != i) continue;
      atObject(x, y);
      return true;
    }
    return false;
  }

  public void setPath(Path p) {
    if (p == null) {
      path = null;
      return;
    }
    if (p.n == path) return;
    path = p.n;
    wait_time = 0;
    path_ptr = 0;
  }

  public final Path calcPath(int x, int y) {
    return calcPath(getX(), getY(), x, y);
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

  public int pathLength(int x1, int y1, int x2, int y2) {
    Node start = getNode(nodes, x1, y1);
    if (start == null) return Integer.MAX_VALUE;
    Node end = getNode(nodes, x2, y2);
    if (end == null) return Integer.MAX_VALUE;
    Node[] n = astar(start, end);
    if (n == null) return Integer.MAX_VALUE;
    return n.length;
  }

  public final Location getNearestBank(final int x, final int y) {
    ArrayList<Location> ordered = new ArrayList<>();
    for (Location loc : locations) {
      if (loc.bank) {
        ordered.add(loc);
      }
    }
    ordered.sort(
        (l1, l2) -> {
          int dist1 = distanceTo(x, y, l1.x, l1.y);
          int dist2 = distanceTo(x, y, l2.x, l2.y);
          if (dist1 == dist2) {
            return 0;
          } else if (dist1 < dist2) {
            return -1;
          } else {
            return 1;
          }
        });
    int best_dist = Integer.MAX_VALUE;
    Location best_loc = null;
    for (Location loc : ordered) {
      if (distanceTo(x, y, loc.x, loc.y) > best_dist) {
        continue;
      }
      int dist = pathLength(x, y, loc.x, loc.y);
      if (dist < best_dist) {
        best_dist = dist;
        best_loc = loc;
      }
    }
    return best_loc;
  }

  private Node[] astar(Node start, Node goal) {
    if (DEBUG) {
      System.out.print("Calculating path from " + start + " to " + goal + "... ");
    }

    long start_ms = System.currentTimeMillis();

    start.f = (short) start.estHeuristicCost(goal);

    Deque<Node> open = new ArrayDeque<>(32);
    open.add(start);
    start.open = true;

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
      cur.open = false;
      cur.closed = true;
      for (Node n : cur.getNeighbors(nodes)) {
        int t_gscore = cur.g + n.distFrom(cur);
        int t_fscore = t_gscore + n.estHeuristicCost(goal);
        if (n.closed && t_fscore >= n.f) {
          continue;
        }
        if (!n.open) {
          came_from.put(n, cur);
          n.g = (short) t_gscore;
          n.f = (short) t_fscore;
          open.add(n);
          n.open = true;
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

  private static void resetNodes(Node[][] nodes) {
    for (int x = 0; x < WORLD_W; ++x) {
      for (int y = 0; y < WORLD_H; ++y) {
        Node n = nodes[x][y];
        if (n == null) continue;
        n.reset();
      }
    }
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
      f = n.f;
      if (f < best_f) {
        best_n = n;
        best_f = f;
      }
    }
    return best_n;
  }

  private static Node getNode(Node[][] nodes, int x, int y) {
    if (x < 0 || x > (WORLD_W - 1)) {
      return null;
    }
    if (y < 0 || y > (WORLD_H - 1)) {
      return null;
    }
    return nodes[x][y];
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("OK")) {
      new Thread(new FinderInit()).start();
    }
    frame.setVisible(false);
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    Location loc = locations[choice.getSelectedIndex()];
    String b = String.valueOf(loc.x) + ',' + loc.y;
    field_end.setText(b);
  }

  private class FinderInit implements Runnable {

    @Override
    public void run() {
      String[] split = field_start.getText().split(",");
      int x1 = Integer.parseInt(split[0]);
      int y1 = Integer.parseInt(split[1]);

      split = field_end.getText().split(",");
      int x2 = Integer.parseInt(split[0]);
      int y2 = Integer.parseInt(split[1]);

      Node start = getNode(nodes, x1, y1);

      if (start == null) {
        System.out.println("Failed: invalid start position.");
        frame.setVisible(false);
        return;
      }

      Node end = getNode(nodes, x2, y2);

      if (end == null) {
        System.out.println("Failed: invalid end position.");
        frame.setVisible(false);
        return;
      }

      Node[] path = astar(start, end);
      if (path == null) {
        System.out.println("Failed to calculate path. :(");
        return;
      }

      Path p = new Path();
      p.n = path;
      setPath(p);
      System.gc();
      System.out.println("Ready to run.");
    }
  }

  @Override
  public void paint() {
    int x = 320;
    int y = 46;
    drawString("Storm's Path Walker", x - 1, y, 4, 0x1E90FF);
    y += 15;
    drawString("Gate Support by kRiStOf", x - 1, y, 4, 0x1E90FF);
    y += 15;
    drawString("Runtime: " + get_time_since(start_time), x, y, 1, 0xFFFFFF);
    drawVLine(x - 7, 36, y - 32, 0x1E90FF);
    drawHLine(x - 7, y + 3, 196, 0x1E90FF);
  }

  private static String get_time_since(long t) {
    long millis = (System.currentTimeMillis() - t) / 1000;
    long second = millis % 60;
    long minute = (millis / 60) % 60;
    long hour = (millis / (60 * 60)) % 24;
    long day = (millis / (60 * 60 * 24));

    if (day > 0L) {
      return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
    }
    if (hour > 0L) {
      return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
    }
    if (minute > 0L) {
      return String.format("%02d minutes, %02d seconds", minute, second);
    }
    return String.format("%02d seconds", second);
  }
}
