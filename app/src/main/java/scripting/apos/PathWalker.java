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
import javax.swing.SwingUtilities;
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
  private static Node[][] nodes;
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

  public PathWalker() {}

  public PathWalker(String ex) {}

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

      boolean completedQuest = isQuestComplete(9);

      if (!completedQuest)
        System.out.printf(
            "[%s] Setting Al Kharid Gate to Impassible (Prince Ali Rescue quest is not complete)%n",
            this);

      for (int x = 0; x < WORLD_W; ++x) {
        for (int y = 0; y < WORLD_H; ++y) {
          byte i = walkable[x][y];
          if (i != 0) {
            if (completedQuest
                || (x != 92
                    || (y != 649
                        && y
                            != 650))) { // Coords for al kharid gate, so we dont try to walk through
              // if haven't done quest.
              final Node n = new Node(x, y);
              n.walkable = i;
              nodes[x][y] = n;
            }
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
      if (DEBUG) System.out.println("Opening PathWalker GUI...");
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
      for (int i = 0; i < 5000; i += 100) {
        if (path != null) break;
        Thread.sleep(100L);
      }
    } catch (InterruptedException ex) {
    }
    if (path == null)
      System.out.println(
          "Could not calculate path in 5 seconds! Path is still null, so script will fail to run..");

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

  TextField searchField;
  Button okButton, cancelButton; // References for button actions

  private void createFrame() {
    frame = new Frame(getClass().getSimpleName());

    frame.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            frame.dispose(); // Ensure the frame closes properly
          }

          public void windowOpened(WindowEvent e) {
            searchField.requestFocusInWindow();
            searchField.selectAll();
          }
        });

    searchField = new TextField(20);
    searchField.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if ((e.getKeyCode() == KeyEvent.VK_A)
                && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK)) {
              searchField.selectAll();
              e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              actionPerformed(new ActionEvent(okButton, ActionEvent.ACTION_PERFORMED, "OK"));
              e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
              frame.dispose(); // Closes the frame
            } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
              int currentIndex = choice.getSelectedIndex();
              int newIndex = currentIndex + (e.getKeyCode() == KeyEvent.VK_UP ? -1 : 1);
              if (newIndex >= 0 && newIndex < choice.getItemCount()) {
                choice.select(newIndex);
                choice.makeVisible(newIndex);
                updateFieldEndWithSelectedLocation();
                e.consume();
              }
            }
          }

          public void keyReleased(KeyEvent e) {
            if (!e.isActionKey()) {
              updateSearchResults();
            }
          }
        });

    Panel searchPanel = new Panel();
    searchPanel.add(new Label("Search:"));
    searchPanel.add(searchField);

    okButton = new Button("OK");
    okButton.addActionListener(this);
    Button cancelButton = new Button("Cancel");
    cancelButton.addActionListener(this);
    Panel bp = new Panel(); // Button panel
    bp.add(okButton);
    bp.add(cancelButton);

    Panel formPanel = new Panel(new GridLayout(2, 2, 5, 5));
    field_start = new TextField(getX() + "," + getY(), 20);
    field_end = new TextField("0,0", 20);
    formPanel.add(new Label("Start location:"));
    formPanel.add(field_start);
    formPanel.add(new Label("Target location:"));
    formPanel.add(field_end);

    choice = new List(4, false);
    for (Location l : locations) {
      choice.add(l.name);
    }
    choice.addItemListener(this);
    choice.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
              e.consume(); // Already handled in searchField's listener
            }
          }
        });

    Panel listPanel = new Panel(new BorderLayout());
    listPanel.add(new Label("Preset targets", Label.CENTER), BorderLayout.NORTH);
    listPanel.add(choice, BorderLayout.CENTER);

    Panel centerPanel = new Panel(new BorderLayout());
    centerPanel.add(formPanel, BorderLayout.NORTH);
    centerPanel.add(listPanel, BorderLayout.CENTER);

    frame.add(searchPanel, BorderLayout.NORTH);
    frame.add(centerPanel, BorderLayout.CENTER);
    frame.add(bp, BorderLayout.SOUTH);

    frame.pack(); // Adjusts frame to fit preferred sizes
    frame.setMinimumSize(new Dimension(300, 400)); // Ensures a minimum size
    // frame.setSize(300, 400); // Not necessary due to pack() and setMinimumSize()

    // This ensures UI components are correctly redrawn, addressing the grey-out
    // issue
    SwingUtilities.invokeLater(
        () -> {
          frame.validate();
          frame.repaint();
        });

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private Map<Integer, Integer> choiceToLocationsMap = new HashMap<>();

  private void updateSearchResults() {
    String searchText = searchField.getText().toLowerCase();
    choice.removeAll();
    choiceToLocationsMap.clear();
    boolean itemAdded = false;
    for (int i = 0; i < locations.length; i++) {
      Location l = locations[i];
      if (l.name.toLowerCase().contains(searchText)) {
        choice.add(l.name);
        choiceToLocationsMap.put(
            choice.getItemCount() - 1, i); // Map the choice index to the original array index
        if (!itemAdded) {
          choice.select(0);
          updateFieldEndWithSelectedLocation();
          itemAdded = true;
        }
      }
    }
  }

  private void updateFieldEndWithSelectedLocation() {
    int selectedIndex = choice.getSelectedIndex();
    if (selectedIndex >= 0) {
      // Use the mapping to get the correct index in locations array
      Integer originalIndex = choiceToLocationsMap.get(selectedIndex);
      if (originalIndex != null) {
        Location loc = locations[originalIndex];
        String b = loc.x + "," + loc.y;
        field_end.setText(b);
      }
    }
  }

  public boolean walkPath() {
    if (path == null) {
      if (DEBUG) System.out.println("Path is null");
      return false;
    }
    Node last = path[path.length - 1];
    if (getX() == last.x && getY() == last.y) {
      if (DEBUG) System.out.println("Reached destination");
      path = null;
      return false;
    }
    long c_time = System.currentTimeMillis();
    if (c_time >= wait_time) {
      Node n = getCurrentDest();
      if (n == null) return true;
      int x = n.x;
      int y = n.y;
      // radius can cross into the other side of gate because goal node has to be on
      // the other side
      // to call if statement
      if (isAtApproxCoords(331, 487, 10) && (n.x > 341)) { // add point here
        atObject(341, 487);
        System.out.println("Opening Tav gate going west");
        wait_time = c_time + 8000;
      } else if ((isAtApproxCoords(342, 487, 10) || isAtApproxCoords(342, 500, 10)) && n.x <= 341) {
        System.out.println("Opening Tav gate going east");
        walkTo(342, 487);
        atObject(341, 487);
        wait_time = c_time + 2000;
      } else if ((isAtApproxCoords(343, 593, 12) || isAtApproxCoords(356, 584, 7)) && (n.y < 581)) {
        atObject(343, 581);
        System.out.println("Opening Tav gate going north");
        wait_time = c_time + 8000;
      } else if ((isAtApproxCoords(343, 570, 11) || isAtApproxCoords(342, 574, 7))
          && n.y >= 581
          && getY() <= 580) {
        walkTo(343, 580);
        atObject(343, 581);
        System.out.println("Opening Tav gate going south");
        wait_time = c_time + 2000;
      } else if (isAtApproxCoords(703, 542, 10) && (n.y <= 531)) {
        walkTo(703, 532);
        atObject(703, 531);
        System.out.println("Opening Gnome Tree gate going north");
        wait_time = c_time + 2000;
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
      } else if (isQuestComplete(9)
          && !isInAlKharid()
          && isAtApproxCoords(96, 650, 20)
          && (n.x < 92)) { // enter alkharid
        // Coordinate X >= 92 means we're on the west side. n.x < 92 means the next node we're
        // moving to is on the east side.
        int[] npc = getNpcById(161); // Border Guard (West)
        if (npc[0] != -1) {
          System.out.println("Entering Al-Kharid Gate");
          talkToNpc(npc[0]);
          wait_time = c_time + 9000; // wait to talk
        }
      } else if (isQuestComplete(9) && isInAlKharid() && (n.x >= 92)) {
        // Coordinate X < 92 means we're on the east side. n.x >= 92 means the next node we're
        // moving to is on the west side.
        int[] npc = getNpcById(162); // Border Guard (East)
        if (npc[0] != -1) {
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

  private boolean isInAlKharid() {
    int x = getX();
    int y = getY();

    final Point myLocation = new Point(x, y);

    // Points relate to vertex's running along the line of the fence on the al-kharid side to
    // create a polygon that we can check to see if we're within.
    final Point[] alkharidPoints =
        new Point[] {
          new Point(93, 669),
          new Point(93, 656),
          new Point(92, 655),
          new Point(91, 654),
          new Point(91, 645),
          new Point(92, 644),
          new Point(92, 638),
          new Point(80, 638),
          new Point(80, 669),
          new Point(93, 669), // first and last points match to create the shape
        };

    return isWithinArea(myLocation, alkharidPoints);
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

  /**
   * Caclulate path to provided destination, defaults to radius 5 and start at current tile
   *
   * @param x int - destination x coordinate
   * @param y int - destination y coordinate
   * @return Path object representing the path to the destination
   */
  public final Path calcPath(final int x, final int y) {
    return calcPath(
        getX(), getY(), x, y,
        5); // Default to use varying starting points to assist in successful path generations.
  }
  /**
   * Calculate a path from current location to target destination.
   *
   * @param x1 Current X coord
   * @param y1 Current Y coord
   * @param x2 Target X coord
   * @param y2 Target Y coord
   * @return Path from current to target location.
   */
  public Path calcPath(final int x1, final int y1, final int x2, final int y2) {
    return calcPath(x1, y1, x2, y2, 5);
  }

  /**
   * Calculates a path from current location (or a point within the defined radius) to the fixed
   * target location.
   *
   * @param x1 Starting X coord
   * @param y1 Starting Y coord
   * @param x2 Target X coord
   * @param y2 Target Y coord
   * @param radius Radius around us to set as starting point
   * @return Path of nodes from current location to target destination.
   */
  public Path calcPath(int x1, int y1, final int x2, final int y2, final int radius) {
    Path path = calculatePath(x1, y1, x2, y2);

    if (path != null) return path;

    // https://stackoverflow.com/a/398302
    int dx = 0;
    int _dx;
    int dy = -1;

    int i = 0;

    do {
      if (isReachable(x1, y1)
          && !isObjectAt(x1, y1)
          && (path = calculatePath(x1, y1, x2, y2)) != null) {
        break;
      }

      if ((x1 == y1) || (x1 < 0 && x1 == -y1) || (x1 > 0 && x1 == 1 - y1)) {
        _dx = dx;
        dx = -dy;
        dy = _dx;
      }

      x1 += dx;
      y1 += dy;

      i++;
    } while (i < Math.pow(radius * 2 + 1, 2));

    return path;
  }
  /**
   * Calculate a path from current location to target destination. Returns the previously generated
   * path if requesting the same coordinates.
   *
   * @param x1 Current X coord
   * @param y1 Current Y coord
   * @param x2 Target X coord
   * @param y2 Target Y coord
   * @return Path from current to target location.
   */
  private Path calculatePath(final int x1, final int y1, final int x2, final int y2) {
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
      String[] split = field_start.getText().replace(" ", "").split(",");
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
    drawString("Gate Support by kRiStOf", x - 1, y, 2, 0x1E90FF);
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
