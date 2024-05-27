package scripting.idlescript.AIOQuester.models;

// TODO: Add more locations for the over-world

import bot.Main;
import controller.Controller;
import controller.WebWalker.WebWalker;
import controller.WebWalker.WebwalkGraph;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enum of locations useful for checking if the player is in a Location's boundary with
 * QuestHandler.isAtLocation()or Location.isAtLocation(), or walking to a location's standable tile
 * with QuestHandler.walkTowards() or Location.walkTowards().
 */
public enum Location {
  AL_KHARID_BORDER_GATE(
      new Boundary(88, 647, 91, 652), new Tile(80, 649), "Al-Kharid Gate to Lumbridge"),
  ARDOUGNE_MONASTERY(new Boundary(575, 651, 603, 669), new Tile(589, 653), "Ardougne Monastery"),
  ARDOUGNE_SOUTH_BANK(new Boundary(534, 605, 557, 619), new Tile(552, 613), "Ardougne South Bank"),
  BARBARIAN_OUTPOST_ENTRANCE(
      new Boundary(493, 538, 506, 555), new Tile(495, 544), "Barbarian Outpost Gate"),
  BARBARIAN_OUTPOST_INNER(
      new Boundary(485, 541, 493, 555), new Tile(491, 546), "Barbarian Outpost Inner"),
  BRIMHAVEN_SHRIMP_AND_PARROT(
      new Boundary(443, 679, 459, 692), new Tile(443, 679), "Shrimp and Parrot Pub"),
  CHAMPIONS_GUILD(new Boundary(148, 554, 152, 562), new Tile(150, 556), "Champion's Guild "),
  DWARF_TUNNEL_WEST_ENTRANCE(
      new Boundary(420, 450, 432, 464), new Tile(427, 457), "Dwarf Tunnel Western Entrance"),
  EDGEVILLE_MONASTERY(new Boundary(249, 456, 265, 472), new Tile(255, 464), "Edgeville Monastery"),
  FALADOR_WEST_BANK(new Boundary(328, 549, 334, 557), new Tile(328, 552), "Falador West Bank"),
  LUMBRIDGE_BOBS_AXES(new Boundary(122, 666, 124, 670), new Tile(122, 668), "Bob's Axe Shop"),
  LUMBRIDGE_BORDER_GATE(
      new Boundary(92, 647, 95, 652), new Tile(93, 649), "Lumbridge Gate to Al-Kharid"),
  LUMBRIDGE_CABBAGE_FIELD(
      new Boundary(137, 597, 153, 612), new Tile(145, 606), "Lumbridge Cabbage Field"),
  LUMBRIDGE_CASTLE_COURTYARD(
      new Boundary(120, 652, 128, 665), new Tile(126, 658), "Lumbridge Castle Courtyard"),
  LUMBRIDGE_CASTLE_DUKES_ROOM(
      new Boundary(129, 1601, 133, 1606), new Tile(132, 1603), "Lumbridge Duke's Room"),
  LUMBRIDGE_CASTLE_KITCHEN(
      new Boundary(131, 659, 137, 662), new Tile(135, 660), "Lumbridge Castle Kitchen"),
  LUMBRIDGE_CASTLE_SPINNING_WHEEL(
      new Boundary(136, 2552, 141, 2557), new Tile(138, 2554), "Lumbridge Spinning Wheel"),
  LUMBRIDGE_CHICKEN_PEN(
      new Boundary(115, 603, 122, 612), new Tile(117, 609), "Lumbridge Chicken Pen"),
  LUMBRIDGE_CHURCH(new Boundary(109, 660, 113, 669), new Tile(113, 664), "Lumbridge Church"),
  LUMBRIDGE_CORN_FIELD(
      new Boundary(157, 600, 163, 609), new Tile(160, 607), "Lumbridge Corn Field"),
  LUMBRIDGE_COW_PEN(new Boundary(96, 605, 104, 628), new Tile(103, 619), "Lumbridge Cow Pen"),
  LUMBRIDGE_FARMER_FREDS_HOUSE(
      new Boundary(157, 617, 161, 620), new Tile(159, 618), "Farmer Fred's House"),
  LUMBRIDGE_FURNACE(new Boundary(130, 626, 133, 630), new Tile(130, 628), "Lumbridge Furnace"),
  LUMBRIDGE_GENERAL_STORE(
      new Boundary(132, 642, 134, 643), new Tile(133, 642), "Lumbridge General Store"),
  LUMBRIDGE_GOBLIN_HUT(
      new Boundary(115, 628, 119, 632), new Tile(117, 630), "Lumbridge Goblin Hut"),
  LUMBRIDGE_GRAVEYARD(new Boundary(106, 666, 110, 676), new Tile(107, 675), "Lumbridge Graveyard"),
  LUMBRIDGE_MILL_F1(
      new Boundary(163, 597, 169, 603), new Tile(166, 601), "Lumbridge Mill Ground Floor"),
  LUMBRIDGE_MILL_F2(
      new Boundary(163, 1541, 169, 1547), new Tile(164, 1544), "Lumbridge Mill Second Floor"),
  LUMBRIDGE_MILL_F3(
      new Boundary(163, 2485, 169, 2491), new Tile(167, 2489), "Lumbridge Mill Third Floor"),
  LUMBRIDGE_MUMS_HOUSE(new Boundary(119, 664, 119, 667), new Tile(119, 666), "Mum's House"),
  LUMBRIDGE_ONION_FIELD(
      new Boundary(157, 621, 160, 623), new Tile(160, 621), "Lumbridge Onion Field"),
  LUMBRIDGE_POTATO_FIELD(
      new Boundary(95, 588, 103, 603), new Tile(102, 597), "Lumbridge Potato Field"),
  LUMBRIDGE_SHEEP_PEN(new Boundary(136, 618, 156, 633), new Tile(144, 630), "Lumbridge Sheep Pen"),
  LUMBRIDGE_SWAMP_KRESHS_HUT(
      new Boundary(156, 694, 159, 697), new Tile(157, 695), "Lumbridge Swamp Kresh's Hut"),
  LUMBRIDGE_SWAMP_LEPRECHAUN_TREE(
      new Boundary(171, 660, 173, 663), new Tile(173, 662), "Lumbridge Swamp Leprechaun Tree"),
  LUMBRIDGE_SWAMP_URHNEYS_HOUSE(
      new Boundary(115, 709, 118, 712),
      new Tile(116, 710),
      "Lumbridge Swamp Father Urhney's House"),
  LUMBRIDGE_SWAMP_ZANARIS_SHED(
      new Boundary(126, 685, 127, 687), new Tile(126, 686), "Lumbridge Swamp Zanaris Shed"),
  LUMBRIDGE_WHEAT_FIELD(
      new Boundary(170, 596, 178, 604), new Tile(172, 604), "Lumbridge Wheat Field"),
  MYSTERIOUS_RUINS_AIR(
      new Boundary(305, 592, 309, 596), new Tile(307, 592), "Air Altar Mysterious Ruins"),
  MYSTERIOUS_RUINS_BODY(
      new Boundary(258, 502, 262, 506), new Tile(261, 506), "Body Altar Mysterious Ruins"),
  MYSTERIOUS_RUINS_CHAOS(
      new Boundary(231, 374, 235, 378), new Tile(233, 378), "Chaos Altar Mysterious Ruins"),
  MYSTERIOUS_RUINS_EARTH(
      new Boundary(61, 463, 65, 467), new Tile(64, 467), "Earth Altar Mysterious Ruins"),
  MYSTERIOUS_RUINS_FIRE(
      new Boundary(49, 632, 53, 636), new Tile(53, 634), "Fire Altar Mysterious Ruins"),
  MYSTERIOUS_RUINS_MIND(
      new Boundary(296, 437, 300, 441), new Tile(298, 441), "Mind Altar Mysterious Ruins"),
  MYSTERIOUS_RUINS_NATURE(
      new Boundary(391, 803, 395, 807), new Tile(394, 803), "Nature Altar Mysterious Ruins"),
  MYSTERIOUS_RUINS_WATER(
      new Boundary(145, 682, 150, 687), new Tile(150, 684), "Water Altar Mysterious Ruins"),
  SORCERERS_TOWER(
      new Boundary(507, 505, 514, 511), new Tile(511, 508), "Sorcerers' Tower Ground Floor"),
  SORCERERS_TOWER_ABOVE(
      new Boundary(507, 1448, 514, 1458), new Tile(511, 1452), "Sorcerers' Tower 1st Floor "),
  TAVERLEY_DUNGEON_ENTRANCE_OUTER(
      new Boundary(371, 514, 384, 525), new Tile(377, 520), "Taverly Dungeon Entrance Outer"),
  VARROCK_ANVILS(new Boundary(145, 510, 148, 516), new Tile(148, 512), "Varrock Anvils"),
  VARROCK_APOTHECARY(new Boundary(141, 518, 145, 521), new Tile(143, 520), "Varrock Apothecary"),
  VARROCK_AUBURYS_SHOP(
      new Boundary(101, 522, 103, 525), new Tile(101, 525), "Varrock Aubury's Rune Shop"),
  VARROCK_BERRY_BUSHES_EAST(
      new Boundary(75, 530, 78, 536), new Tile(76, 533), "Varrock Berry Bushes East"),
  VARROCK_BERRY_BUSHES_SOUTH(
      new Boundary(81, 536, 87, 538), new Tile(84, 537), "Varrock Berry Bushes South"),
  VARROCK_BERRY_BUSHES_WEST(
      new Boundary(96, 536, 103, 538), new Tile(99, 537), "Varrock Berry Bushes West"),
  VARROCK_BLACK_ARM_GANG_ENTRANCE(
      new Boundary(145, 533, 151, 535), new Tile(148, 534), "Varrock Black Arm Gang"),
  VARROCK_BLUE_MOON_INN(
      new Boundary(120, 520, 126, 526), new Tile(122, 523), "Varrock Blue Moon Inn"),
  VARROCK_BLUE_MOON_INN_KITCHEN(
      new Boundary(116, 520, 119, 526), new Tile(117, 525), "Varrock Blue Moon Inn Kitchen"),
  VARROCK_BRASS_KEY_HUT(
      new Boundary(201, 481, 204, 484), new Tile(202, 482), "Varrock Brass Key Hut"),
  VARROCK_CASTLE_ALTAR(
      new Boundary(130, 1399, 137, 1403), new Tile(136, 1401), "Varrock Castle Altar"),
  VARROCK_CASTLE_COURTYARD(
      new Boundary(123, 478, 139, 486), new Tile(131, 483), "Varrock Castle Courtyard"),
  VARROCK_CASTLE_GARDEN(
      new Boundary(123, 487, 139, 499), new Tile(130, 493), "Varrock Castle Garden"),
  VARROCK_CASTLE_GUARD_BARRACKS(
      new Boundary(138, 1395, 142, 1408), new Tile(140, 1398), "Varrock Castle Guard Barracks"),
  VARROCK_CASTLE_KITCHEN(
      new Boundary(118, 461, 123, 468), new Tile(119, 465), "Varrock Castle Kitchen"),
  VARROCK_CASTLE_LIBRARY(
      new Boundary(126, 455, 134, 459), new Tile(128, 457), "Varrock Castle Library"),
  VARROCK_CASTLE_SIR_PRYSINS_ROOM(
      new Boundary(135, 472, 140, 477), new Tile(137, 474), "Varrock Castle Sir Prysin's Room"),
  VARROCK_CASTLE_THRONE_ROOM(
      new Boundary(124, 472, 128, 477), new Tile(126, 474), "Varrock Castle Throne Room"),
  VARROCK_CHURCH(
      new Boundary(98, 473, 106, 482), new Tile(100, 475), "Varrock Church Ground Floor"),
  VARROCK_CHURCH_SECOND_FLOOR(
      new Boundary(103, 1417, 105, 1420), new Tile(104, 1418), "Varrock Church Second Floor"),
  VARROCK_COOKS_GUILD(new Boundary(176, 480, 181, 487), new Tile(179, 483), "Cook's Guild"),
  VARROCK_DANCING_DONKEY_INN(
      new Boundary(89, 528, 94, 539), new Tile(91, 529), "Varrock Dancing Donkey Inn"),
  VARROCK_DIMINTHEIS_HOUSE(
      new Boundary(84, 521, 85, 523), new Tile(84, 522), "Varrock Dimintheis' House"),
  VARROCK_EAST_BANK(new Boundary(98, 510, 106, 515), new Tile(102, 512), "Varrock East Bank"),
  VARROCK_EAST_MINE(new Boundary(68, 542, 80, 550), new Tile(73, 545), "Varrock East Mine"),
  VARROCK_GENERAL_STORE(
      new Boundary(124, 513, 129, 517), new Tile(126, 515), "Varrock General Store"),
  VARROCK_GERTRUDES_HOUSE(
      new Boundary(163, 511, 168, 517), new Tile(165, 512), "Varrock Gertrude's House"),
  VARROCK_GUIDORS_HOUSE(
      new Boundary(79, 530, 86, 535), new Tile(85, 533), "Varrock Guidor's House"),
  VARROCK_HORVIKS_SHOP(
      new Boundary(113, 498, 119, 505), new Tile(117, 501), "Varrock Horvik's Shop"),
  VARROCK_JULIETS_HOUSE(
      new Boundary(165, 494, 174, 501), new Tile(168, 496), "Varrock Juliet's House"),
  VARROCK_LOWES_SHOP(
      new Boundary(113, 511, 116, 516), new Tile(114, 513), "Varrock Lowe's Archery Shop"),
  VARROCK_MARKET_SQUARE(
      new Boundary(126, 505, 137, 511), new Tile(134, 506), "Varrock Market Square"),
  VARROCK_MUSEUM(
      new Boundary(97, 484, 106, 496), new Tile(100, 494), "Varrock Museum Ground Floor"),
  VARROCK_MUSEUM_SECOND_FLOOR(
      new Boundary(97, 1428, 106, 1440), new Tile(100, 1433), "Varrock Museum Second Floor"),
  VARROCK_PHOENIX_GANG_ENTRANCE(
      new Boundary(106, 534, 107, 536), new Tile(106, 535), "Varrock Phoenix Gang Entrance"),
  VARROCK_SEWER_ENTRANCE(
      new Boundary(110, 473, 112, 475), new Tile(111, 475), "Varrock Sewer Entrance"),
  VARROCK_SHEEP_PEN(new Boundary(96, 560, 109, 570), new Tile(104, 566), "Varrock Sheep Pen"),
  VARROCK_SOUTH_ALTAR(new Boundary(96, 528, 101, 535), new Tile(98, 531), "Varrock South Altar"),
  VARROCK_SPIRIT_TREE(new Boundary(159, 452, 161, 454), new Tile(161, 453), "Varrock Spirit Tree"),
  VARROCK_STONE_CIRCLE(
      new Boundary(104, 545, 117, 558), new Tile(111, 553), "Varrock Stone Circle"),
  VARROCK_SWORD_SHOP(new Boundary(133, 522, 138, 527), new Tile(134, 525), "Varrock Sword Shop"),
  VARROCK_TAILORS_HOUSE(
      new Boundary(82, 524, 85, 526), new Tile(84, 525), "Varrock Tailor's House"),
  VARROCK_THESSALIAS_SHOP(
      new Boundary(136, 515, 139, 516), new Tile(137, 516), "Varrock Thessalia's Clothes Shop"),
  VARROCK_TRAINING_ROOM(
      new Boundary(101, 499, 107, 505), new Tile(104, 502), "Varrock Training Room"),
  VARROCK_WEST_BANK(new Boundary(147, 499, 153, 506), new Tile(150, 502), "Varrock West Bank"),
  VARROCK_WEST_BANK_BASEMENT(
      new Boundary(147, 3330, 152, 3332), new Tile(149, 3332), "Varrock West Bank Basement"),
  VARROCK_WEST_MINE(new Boundary(160, 531, 169, 548), new Tile(161, 541), "Varrock West Mine"),
  VARROCK_WHEAT_FIELD(new Boundary(129, 558, 136, 569), new Tile(131, 559), "Varrock Wheat Field"),
  VARROCK_ZAFFS_SHOP(
      new Boundary(138, 503, 142, 506), new Tile(140, 505), "Varrock Zaff's Staff Shop"),
  WIZARDS_TOWER_BASEMENT(
      new Boundary(213, 3514, 225, 3526), new Tile(220, 3519), "Wizards' Tower Basement"),
  WIZARDS_TOWER_ENTRANCE(
      new Boundary(214, 684, 220, 688), new Tile(217, 687), "Wizards' Tower Entrance");

  // MYSTERIOUS_RUINS_COSMIC(new Boundary(), new Tile(), "Cosmic Altar Mysterious Ruins"),
  // MYSTERIOUS_RUINS_LAW(new Boundary(), new Tile(), "Law Altar Mysterious Ruins"),     NOT ADDED
  // MYSTERIOUS_RUINS_DEATH(new Boundary(), new Tile(), "Death Altar Mysterious Ruins"), NOT ADDED
  // MYSTERIOUS_RUINS_BLOOD(new Boundary(), new Tile(), "Blood Altar Mysterious Ruins"), NOT ADDED

  // TREE_GNOME_STRONGHOLD_SPIRIT_TREE(new Boundary(), new Tile(), "Tree Gnome Stronghold Spirit
  // Tree"),
  // TREE_GNOME_VILLAGE_SPIRIT_TREE(new Boundary(), new Tile(), "Tree Gnome Village Spirit Tree"),
  // BATTLEFIELD_SPIRIT_TREE(new Boundary(), new Tile(), "Battlefield Spirit Tree"),

  private static final Controller c = Main.getController();
  private static final WebWalker w = new WebWalker(new WebwalkGraph("assets/map/graph.txt"));
  private final Boundary boundary;
  private final Tile standableTile;
  private final String description;

  /**
   * @param boundary Boundary - Rectangular area that makes up the location
   * @param standableTile Tile - Standable tile within the boundary
   * @param description String - Description of the location
   */
  Location(Boundary boundary, Tile standableTile, String description) {
    this.boundary = sortBoundary(boundary);
    // If a location's standable tile is null, it will default to the first corner of its boundary
    this.standableTile =
        standableTile != null
            ? new Tile(standableTile.getX(), standableTile.getY())
            : new Tile(this.boundary.getX1(), this.boundary.getY1());
    this.description = description;
  }

  /**
   * Attempts to walk towards the specified location's standable tile. If this fails, WebWalker may
   * need to be updated to support the location you're attempting to navigate to.
   */
  public void walkTowards() {
    if (c.isRunning()) {
      int x = getX();
      int y = getY();
      if (notAtCoords(x, y)) {
        c.displayMessage(
            "@yel@Attempting to walk to: @cya@" + Location.getDescriptionFromStandableTile(x, y));
        System.out.println(
            "\nAttempting to walk to: " + Location.getDescriptionFromStandableTile(x, y));
        System.out.println(
            "If this fails, WebWalker might need to be updated to include the area.");
        int failedAttempts = 0;
        while (notAtCoords(x, y) && c.isRunning()) {
          failedAttempts = !c.walkTowards(x, y) ? ++failedAttempts : 0;
          if (failedAttempts >= 5) {
            c.log("Failed to walk to specified location. WebWalker may need to be updated.", "red");
            c.stop();
          }
          c.sleep(100);
        }
      }
    }
  }

  /**
   * Attempts to walk to the closest Location in an array of Locations.
   *
   * @param locations Location[] -- Array of locations
   */
  public static void walkTowardsClosest(Location[] locations) {
    if (locations == null) return;
    getClosest(locations).walkTowards();
  }

  /**
   * Returns the closest Location from a given array of Locations.
   *
   * @param locations Location[] -- Array of locations to check
   * @return Location
   */
  private static Location getClosest(Location[] locations) {
    if (locations == null) return null;
    Map<Location, Integer> locationMap =
        Arrays.stream(locations).collect(Collectors.toMap(l -> l, Location::distanceTo));
    return Collections.min(locationMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue))
        .getKey();
  }

  /**
   * Returns a rough Geodesic distance from the player's current position to the standable tile of
   * the Location. Geodesic distance is the distance between two points while considering terrain
   * and navigability.
   *
   * @return int
   */
  public int distanceTo() {
    return w.distanceBetween(c.currentX(), c.currentY(), getX(), getY());
  }

  /**
   * Returns a rough Geodesic distance from this Location to a second Location. Geodesic distance is
   * the distance between two points while considering terrain and navigability.
   *
   * @param loc Location -- Location to check
   * @return int
   */
  public int distanceBetween(Location loc) {
    return w.distanceBetween(loc.getX(), loc.getY(), getX(), getY());
  }

  /**
   * Returns a rough Geodesic distance between two locations. Geodesic distance is the distance
   * between two points while considering terrain and navigability.
   *
   * @param loc1 Location -- Location to check
   * @param loc2 Location -- Location to check
   * @return int
   */
  public static int distanceBetween(Location loc1, Location loc2) {
    return w.distanceBetween(loc1.getX(), loc1.getY(), loc2.getX(), loc2.getY());
  }

  /**
   * Returns whether the player is not at the specified coordinates.
   *
   * @param x int -- X Coordinate
   * @param y int -- Y Coordinate
   * @return boolean
   */
  private static boolean notAtCoords(int x, int y) {
    return c.currentX() != x || c.currentY() != y;
  }

  /**
   * Returns the Location's Boundary
   *
   * @return Boundary
   */
  private Boundary getBoundary() {
    return boundary;
  }

  /**
   * Returns the X coordinate of the Location's standable tile.
   *
   * @return int
   */
  public int getX() {
    return standableTile.getX();
  }

  /**
   * Returns the Y coordinate of the Location's standable tile.
   *
   * @return int
   */
  public int getY() {
    return standableTile.getY();
  }

  /**
   * Returns the description of a start location
   *
   * @return String
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sorts the coordinates of the boundary so that the first coordinate pair is always lower than
   * the second.
   *
   * @param boundary Boundary -- Boundary to sort
   * @return Boundary
   */
  private static Boundary sortBoundary(Boundary boundary) {
    int x1 = Math.min(boundary.getX1(), boundary.getX2());
    int y1 = Math.min(boundary.getY1(), boundary.getY2());
    int x2 = Math.max(boundary.getX1(), boundary.getX2());
    int y2 = Math.max(boundary.getY1(), boundary.getY2());
    return new Boundary(x1, y1, x2, y2);
  }

  /**
   * Returns whether the player is within the Location's boundary rectangle.
   *
   * @param loc Location -- Location to check
   * @return boolean
   */
  public static boolean isAtLocation(Location loc) {
    int cX = c.currentX();
    int cY = c.currentY();
    Boundary b = loc.getBoundary();

    return cX >= b.getX1() && cX <= b.getX2() && cY >= b.getY1() && cY <= b.getY2();
  }

  /**
   * Returns whether the player is within the Location's boundary rectangle.
   *
   * @return boolean
   */
  public boolean isAtLocation() {
    int cX = c.currentX();
    int cY = c.currentY();
    Boundary b = getBoundary();

    return cX >= b.getX1() && cX <= b.getX2() && cY >= b.getY1() && cY <= b.getY2();
  }

  /**
   * Returns either the description of a Location with a matching standable tile, or (X, Y)
   * coordinates as a string.
   *
   * @param x int - X coordinate to check for description
   * @param y int - Y coordinate to check for description
   * @return String - Location description
   */
  public static String getDescriptionFromStandableTile(int x, int y) {
    return Arrays.stream(Location.values())
        .filter(loc -> loc.getX() == x && loc.getY() == y)
        .findFirst()
        .map(Location::getDescription)
        .orElse(String.format("(%s, %s)", x, y));
  }
}

class Boundary {
  private final int x1;
  private final int y1;
  private final int x2;
  private final int y2;

  Boundary(int x1, int y1, int x2, int y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  public int getX1() {
    return x1;
  }

  public int getY1() {
    return y1;
  }

  public int getX2() {
    return x2;
  }

  public int getY2() {
    return y2;
  }

  public String toString() {
    return String.format("(%s, %s), (%s, %s)", x1, y1, x2, y2);
  }
}

class Tile {
  private final int x;
  private final int y;

  Tile(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public String toString() {
    return String.format("(%s, %s)", x, y);
  }
}
