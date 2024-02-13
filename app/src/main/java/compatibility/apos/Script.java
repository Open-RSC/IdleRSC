package compatibility.apos;

import bot.Main;
import com.openrsc.client.entityhandling.instances.Item;
import controller.Controller;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import orsc.ORSCharacter;

/**
 * This is the APOS API, implemented as a compatibility abstraction layer.
 *
 * @author Dvorak
 */
public abstract class Script implements IScript {

  private static HashMap<Image, BufferedImage> imageCache;

  private String toType = "";
  private int typeOffset;

  // private final IClient client;
  // private LocalRouteCalc locRouteCalc;

  private boolean trick;

  /** An array of all the Banker NPC IDs. */
  public static final int[] BANKERS = {95, 224, 268, 485, 540, 617};

  /** An array of all the bone item IDs. */
  public static final int[] BONES = {20, 413, 604, 814};

  /** An array of all the skill names. */
  public static String[] SKILL = new String[] {""};

  /** An array of all the spell names. */
  public static String[] SPELL = new String[] {""};

  /** An array containing: "Controlled", "Strength", "Attack", "Defence" */
  public static final String[] FIGHTMODES = {"Controlled", "Strength", "Attack", "Defence"};

  /** The maximum number of items the client's inventory can hold. */
  public static final int MAX_INV_SIZE = 30;

  public static final int DIR_NORTH = 0;

  public static final int DIR_NORTHWEST = 1;

  public static final int DIR_WEST = 2;

  public static final int DIR_SOUTHWEST = 3;

  public static final int DIR_SOUTH = 4;

  public static final int DIR_SOUTHEAST = 5;

  public static final int DIR_EAST = 6;

  public static final int DIR_NORTHEAST = 7;

  //	/**
  //	 * Creates the Script object. Called by the bot on start-up.
  //	 *
  //	 * @param ex
  //	 *			the this.
  //	 */
  //	public Script(Extension ex) {
  //		this.client = ex;
  //	}

  static Controller controller = null;

  /**
   * Used internally by the bot to set {@link controller.Controller}, as that is what adapts APOS to
   * IdleRSC.
   *
   * @param _controller controller
   */
  public static void setController(Controller _controller) {
    controller = _controller;
    SKILL = controller.getSkillNamesLong();
    SPELL = controller.getSpellNames();
  }

  /**
   * Called when the Script is selected by the user - override it and use it to process the
   * parameters.
   *
   * @param params the parameters entered by the user when the script is selected.
   */
  @Override
  public void init(String params) {}

  /**
   * The bot will wait for the milliseconds main() returns before calling it again. It is only
   * called when logged in and not sleeping. Override it and use it for your Script's logic
   * processing.
   */
  @Override
  public int main() {
    return 0;
  }

  /**
   * Called when the game is re-drawn. Override it and use it for your Script's drawing functions.
   */
  @Override
  public void paint() {}

  /**
   * Called when a server message is sent, i.e "Welcome to RuneScape!".
   *
   * @param str the message.
   */
  @Override
  public void onServerMessage(String str) {}

  /**
   * Called when a trade request is recieved by your player.
   *
   * @param name the display name of the player who sent the request.
   */
  @Override
  public void onTradeRequest(String name) {
    System.out.println(name + " wishes to trade with you.");
  }

  /**
   * Called when a player speaks in public chat.
   *
   * @param msg the message.
   * @param name the display name of the player who sent the message.
   * @param mod true if the sender is a player moderator (silver crown).
   * @param admin true if the sender is a Jagex moderator (gold crown).
   */
  @Override
  public void onChatMessage(String msg, String name, boolean mod, boolean admin) {
    System.out.println(name + ": " + msg);
  }

  /**
   * Called when a player speaks in private chat.
   *
   * @param msg the message.
   * @param name the display name of the player who sent the message.
   * @param mod true if the sender is a player moderator (silver crown).
   * @param admin true if the sender is a jagex moderator (gold crown).
   */
  @Override
  public void onPrivateMessage(String msg, String name, boolean mod, boolean admin) {
    System.out.println("(PRIV) " + name + ": " + msg);
  }

  /**
   * Called when the user presses a key on the keyboard and the client is the active window. Do not
   * use this to send key events, it does not work that way. Instead use void setTypeLine(String s)
   * and boolean next().
   *
   * @see Script#disableKeys()
   * @param keycode the key pressed expressed as an integer
   */
  @Override
  public void onKeyPress(int keycode) {}

  /**
   * Disables keys used by the bot so they can be used by the script. They are enabled again when
   * the user stops the script.
   */
  public void disableKeys() {
    // this.setKeysDisabled(true);
  }

  /**
   * Hops to the specified world.
   *
   * @param world the world.
   */
  public void hop(int world) {
    Main.log("apos.hop() unimplemented");
    //		if (world < 0 || world > 5) {
    //			System.out.println("Invalid world number: " + world);
    //			logout();
    //			return;
    //		}
    //		this.setServer(world);
    //		logout();
  }

  /**
   * Hops to the next world in a predefined sequence.
   *
   * @param veteran true to enable hopping to world one
   */
  public void autohop(boolean veteran) {
    switch (getWorld()) {
      case 1:
        hop(2);
        break;
      case 2:
        hop(3);
        break;
      case 3:
        hop(4);
        break;
      case 4:
        hop(5);
        break;
      case 5:
        hop(veteran ? 1 : 2);
        break;
    }
  }

  /**
   * Returns the world the client is logged in to.
   *
   * @return the world the client is logged in to.
   */
  public int getWorld() {
    // return this.getServer();
    return 1;
  }

  /**
   * Stops the script.
   *
   * @see Script#setAutoLogin(boolean)
   */
  public void stopScript() {
    controller.stop();
  }

  /** Attempts to log the client out. */
  public void logout() {
    controller.logout();
  }

  /**
   * Returns true if the sleeping/fatigue screen is visible.
   *
   * @return true if the sleeping/fatigue screen is visible.
   */
  @Override
  public boolean isSleeping() {
    return controller.isSleeping();
  }

  /**
   * Returns the client's fatigue percentage.
   *
   * @return the client's fatigue percentage.
   */
  @Override
  public int getFatigue() {
    if (isSleeping()) {
      return controller.getFatigueDuringSleep();
    }
    return controller.getFatigue();
  }

  public double getAccurateFatigue() {
    controller.log("WARNING: Scripts which do fatiguing on IdleRSC may be inaccurate.");

    if (controller.isSleeping()) {
      return controller.getFatigueDuringSleep();
    }

    return controller.getFatigue();
  }

  /**
   * Returns the X position of the tile the client is standing on.
   *
   * @return the X position of the tile the client is standing on.
   */
  public int getX() {
    return controller.currentX();
  }

  /**
   * Returns the Y position of the tile the client is standing on.
   *
   * @return the Y position of the tile the client is standing on.
   */
  public int getY() {
    return controller.currentY();
  }

  /**
   * Returns true if the client can log out. This is determined by the time since the client was
   * last in combat.
   *
   * @return true if the client can log out.
   */
  public boolean canLogout() {
    return !controller.isInCombat();
  }

  public boolean isTalking() {
    return controller.getPlayer().messageTimeout > 0;
  }

  public boolean isHpBarShowing() {
    return controller.isInCombat();
  }

  /**
   * Returns true if the client is in combat.
   *
   * @return true if the client is in combat.
   */
  public boolean inCombat() {
    return controller.isInCombat();
  }
  /**
   * Returns true if we are within the given NE/SW coordinates.
   *
   * @param northEast The North East coordinate.
   * @param southWest The South West coordinate.
   * @return true if we are within the NE/SW coordinates.
   */
  public boolean isWithinArea(Point northEast, Point southWest) {
    int x = getX();
    int y = getY();

    return (x >= northEast.x && x <= southWest.x) && (y >= northEast.y && y <= southWest.y);
  }

  /**
   * Returns true if the coordinate is within a basic square area, using North East and South West
   * coordinates.
   *
   * @param searchCoord The coordinate to check
   * @param northEast The North East coordinate.
   * @param southWest The South West coordinate.
   * @return true if the searchCoord is within the NE/SW area.
   */
  public boolean isWithinArea(Point searchCoord, Point northEast, Point southWest) {
    int x = searchCoord.x;
    int y = searchCoord.y;

    return (x >= northEast.x && x <= southWest.x) && (y >= northEast.y && y <= southWest.y);
  }

  /**
   * Returns true if the coordinate is within an area of points. Can be an irregular shape to
   * support rooms which aren't square. Points provided should be generated in either a clockwise,
   * or anti-clockwise list, with the first and last entry of the Point[] array being the same
   * coordinate to "join up" the bounds of the shape.
   *
   * @param coord The coordinate to check
   * @param points The area of points to search within
   * @return True if coordinate inside the points area array
   */
  public boolean isWithinArea(final Point coord, final Point[] points) {

    if (points.length < 3) {
      System.out.println("isWithinArea() requires a minimum of 3 points to triangulate an area.");
      throw new UnsupportedOperationException();
    }

    Polygon polygon = new Polygon();

    for (Point point : points) {
      polygon.addPoint(point.x, point.y);
    }

    if (polygon.contains(coord)) { // If we're inside the polygon return true.
      return true;
    }

    // Next check if it's on the boundary, or the outside edge, of the polygon.
    for (int i = 0; i < points.length; i++) {
      int nextIndex = (i + 1) % points.length;
      Line2D edge =
          new Line2D.Double(points[i].x, points[i].y, points[nextIndex].x, points[nextIndex].y);
      if (edge.ptSegDist(coord) == 0) {
        return true; // Point is on the boundary
      }
    }
    return false; // Point is not on the boundary
  }

  /**
   * Returns the distance between the client and the tile at x, y.
   *
   * @param coordinate The X/Y coordinate to check the distance.
   * @return the distance between the client and the given tile.
   */
  public int distanceTo(Point coordinate) {
    return distanceTo(coordinate.x, coordinate.y, getX(), getY());
  }

  /**
   * Returns true if an icon is visible over the client's head. An icon is drawn when certain
   * actions such as mining, cutting down a tree, or cooking food are performed. Note that this will
   * not return true if the client only has a skull icon.
   *
   * @return true if the player has a head icon visible.
   */
  public boolean isSkilling() {
    return controller.getPlayer().bubbleTimeout > 0;
  }

  /**
   * Returns true if the client is walking.
   *
   * @return true if the client is walking.
   */
  public boolean isWalking() {
    return controller.isCurrentlyWalking();
  }

  /**
   * @deprecated For internal use. Legacy.
   * @return not what you think it returns!
   */
  @Override
  @Deprecated
  public boolean isTricking() {
    if (trick && getSleepingFatigue() < 88) {
      useSleepingBag();
      return false;
    }
    return trick && getSleepingFatigue() < 100;
  }

  /**
   * Enables or disables fatigue tricking, which is disabled by default. If the player kills certain
   * NPCs with 99% fatigue in melee combat, they can gain combat XP without hitpoints XP. If the bot
   * is instructed to use the fatigue trick, it will attempt to keep the fatigue at 99% when it
   * sleeps. The script should take care of the rest.
   *
   * @param flag on/off
   */
  public void setTrickMode(boolean flag) {
    this.trick = flag;
  }

  /**
   * @deprecated Use {@link Script#getFatigue()}.
   * @return the client's current fatigue percentage.
   */
  @Deprecated
  public int getSleepingFatigue() {
    return getFatigue();
  }

  /**
   * Sets the line to be typed by next().
   *
   * @param str the line.
   */
  public void setTypeLine(String str) {
    toType = str;
  }

  String getTypeLine() {
    return toType;
  }

  /**
   * Types the next character of the setTypeLine or newline if it has reached the end of the String.
   *
   * @return false until it has finished typing the set line.
   */
  public boolean next() {
    if (typeOffset >= toType.length()) {
      controller.typeKey('\n');
      typeOffset = 0;
      return true;
    }
    final char c = toType.charAt(typeOffset++);
    controller.typeKey(c);
    return false;
  }

  /**
   * Returns the client's combat style.
   *
   * @return the client's selected combat style (position in the list starting at 0).
   */
  public int getFightMode() {
    return controller.getFightMode();
  }

  /**
   * Sets the client's combat style.
   *
   * @param style the combat style's position in the list starting at 0.
   */
  public void setFightMode(int style) {
    controller.setFightMode(style);
  }

  /**
   * Returns the client's level in the given skill.
   *
   * @see Script#getCurrentLevel(int)
   * @param skill the skill's identifer (position in the list starting at 0).
   * @return the player's level in specified skill.
   */
  public int getLevel(int skill) {
    return controller.getBaseStat(skill);
  }

  /**
   * Returns the client's current level in the given skill. The returned integer, unlike getLevel,
   * will be different if the skill has been drained or raised.
   *
   * @see Script#getLevel(int)
   * @param skill the skill's identifer (position in the list starting at 0).
   * @return the player's current level in specified skill.
   */
  public int getCurrentLevel(int skill) {
    return controller.getCurrentStat(skill);
  }

  /**
   * Returns the client's experience in the given skill.
   *
   * @param skill the skill's identifer (position in the list starting at 0).
   * @return the client's experience in specified skill.
   */
  public int getXpForLevel(int skill) {
    return controller.getPlayerExperience(skill);
  }

  /**
   * Returns the client's experience in the given skill.
   *
   * @param skill the skill's identifer (position in the list starting at 0).
   * @return the client's experience in specified skill.
   */
  public double getAccurateXpForLevel(int skill) {
    // controller.log("WARNING: Scripts which do fatiguing on IdleRSC may be inaccurate.");
    return controller.getPlayerExperience(skill);
  }

  /**
   * Returns the percentage of hitpoints the client has remaining.
   *
   * @return the percentage of hitpoints the client has remaining.
   */
  public int getHpPercent() {
    final double d = (double) getCurrentLevel(3) / (double) getLevel(3);
    return (int) (d * 100.0D);
  }

  /**
   * Casts a spell with the given id on the local player. For teleport/charge spells.
   *
   * @param spell the spell's identifier (position in the list starting at 0).
   */
  public void castOnSelf(int spell) {
    controller.castSpellOnSelf(spell);
  }

  /**
   * Attempts to walk to the given x, y coordinates.
   *
   * @param x the x position of the target tile.
   * @param y the y position of the target tile.
   */
  public void walkTo(int x, int y) {
    controller.walkTo(x, y);
  }

  /**
   * Returns the number of individual items in the client's inventory.
   *
   * @return the number of individual items in the client's inventory. Between 0 and MAX_INV_SIZE.
   */
  public int getInventoryCount() {
    return controller.getInventoryItemCount();
  }

  /**
   * Returns the number of unoccupied slots in the client's inventory.
   *
   * @return MAX_INV_SIZE - getInventoryCount()
   */
  public int getEmptySlots() {
    return MAX_INV_SIZE - getInventoryCount();
  }

  /**
   * Returns the total number of items in the client's inventory with the specified IDs. Stack sizes
   * and individual items are counted.
   *
   * @param ids the IDs of the items to search for.
   * @return the total number of items in the client's inventory with the specified ids. Stack sizes
   *     and individual items are counted.
   */
  public int getInventoryCount(int... ids) {
    int count = 0;
    for (final int id : ids) {
      count += controller.getInventoryItemCount(id);
    }
    return count;
  }

  //	private int countItem(int id) {
  //		int count = 0;
  //		for (int i = 0; i < controller.getInventoryItemCount(); i++) {
  //			if (controller.getInventoryItemIds()[i] == id) {
  //				if (!controller.isItemStackable(id)) {
  //					count++;
  //				} else {
  //					count += controller.getInventoryItemCount(id);
  //				}
  //			}
  //		}
  //		return count;
  //	}

  /**
   * Returns true if the client's inventory contains at least one item with the given ID.
   *
   * @param id the ID of the item to search for.
   * @return true if the client's inventory contains at least one item with the given id.
   */
  public boolean hasInventoryItem(int id) {
    return controller.getInventoryItemCount(id) > 0;
  }

  /**
   * Returns the position of the item with the given ID in the client's inventory.
   *
   * @param ids the identifiers of the items to search for.
   * @return the position of the first item with the given id(s). May range from 0 to MAX_INV_SIZE.
   */
  public int getInventoryIndex(int... ids) {
    for (int i = 0; i < getInventoryCount(); i++) {
      if (inArray(ids, controller.getInventorySlotItemId(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Uses the client's sleeping bag. If the client's inventory does not contain a sleeping bag the
   * script will be stopped.
   */
  public void useSleepingBag() {
    final int i = getInventoryIndex(1263);
    if (i == -1) {
      System.out.println("No sleeping bag found.");
      stopScript();
    } else {
      useItem(i);
    }
  }

  public int getGroundItemCount() {
    return controller.getGroundItemsCount();
  }

  public int getGroundItemId(int index) {
    if (index >= controller.getGroundItemsCount()) return -1;

    return controller.getGroundItems()[index];
  }

  public int getItemX(int index) {
    if (index >= controller.getGroundItemsCount()) return -1;

    return controller.getGroundItemsX()[index];
  }

  public int getItemY(int index) {
    if (index >= controller.getGroundItemsCount()) return -1;

    return controller.getGroundItemsY()[index];
  }

  /**
   * Locates the nearest ground item with the given id(s).
   *
   * @param ids the item id(s) to search for.
   * @return always an integer array of size 3. If no item can be found, the array will contain -1,
   *     -1, -1. If an item was found, the array will contain the item's ID, X, Y.
   */
  public int[] getItemById(int... ids) {
    int[] result = controller.getNearestItemByIds(ids);
    if (result == null) return new int[] {-1, -1, -1};

    return new int[] {result[2], result[0], result[1]};
  }

  /**
   * Returns true if there is a ground item with the specified id on the specified tile.
   *
   * @param id the item to search for.
   * @param x the x position of the tile to examine.
   * @param y the y position of the tile to examine.
   * @return true if the item is on the tile.
   */
  public boolean isItemAt(int id, int x, int y) {
    return controller.getGroundItemAmount(id, x, y) > 0;
  }

  /**
   * Whether or not the server is configured to be authentic. This returns true for Uranium, false
   * for Coleslaw.
   *
   * @return boolean
   */
  public boolean isAuthentic() {
    return controller.isAuthentic();
  }
  /**
   * Returns true if the specified SLOT is equipped.
   *
   * @see Script#getInventoryIndex(int...)
   * @param slot the position of the item in the client's inventory, starting at 0.
   * @return true if the specified item is equipped.
   */
  public boolean isItemEquipped(int slot) {
    return controller.isEquipped(slot);
  }
  /*

  */
  public int[] getEquippedItemIds() {
    return controller.getEquippedItemIds();
  }
  /**
   * Returns true if the specified itemId is equipped. (coleslaw)
   *
   * @see Script#getInventoryIndex(int...)
   * @param itemId the position of the item in the client's inventory, starting at 0.
   * @return true if the specified item is equipped.
   */
  public boolean isItemIdEquipped(int itemId) {
    return controller.isItemIdEquipped(itemId);
  }
  /**
   * Attempts to perform the primary action on an item in the client's inventory. This can be "eat",
   * "bury", etc.
   *
   * @see Script#getInventoryIndex(int...)
   * @param slot the position of the item in the client's inventory, starting at 0.
   */
  public void useItem(int slot) {
    controller.itemCommandBySlot(slot);
  }

  /**
   * Attempts to drop an item in the client's inventory.
   *
   * @param slot the position of the item in the client's inventory, starting at 0.
   */
  public void dropItem(int slot) {
    controller.dropItem(slot);
  }

  /**
   * Attempts to equip an item in the client's inventory.
   *
   * @see Script#getInventoryIndex(int...)
   * @param slot the position of the item in the client's inventory, starting at 0.
   */
  public void wearItem(int slot) {
    controller.equipItem(slot);
  }

  /**
   * Attemps to unequip an item in the client's inventory.
   *
   * @see Script#getInventoryIndex(int...)
   * @param slot the position of the item in the client's inventory, starting at 0.
   */
  public void removeItem(int slot) {
    controller.unequipItem(slot);
  }

  /**
   * Uses one item with another based on their positions in the player's inventory. Useful for
   * cutting gems, fletching, etc.
   *
   * @see Script#getInventoryIndex(int...)
   * @param slot_1 the position of the item in the client's inventory.
   * @param slot_2 the position of the item in the client's inventory.
   */
  public void useItemWithItem(int slot_1, int slot_2) {
    controller.useItemOnItemBySlot(slot_1, slot_2);
  }

  /**
   * Casts the specified spell on the specified slot in the player's inventory. Useful for high
   * alchemy, superheat, etc.
   *
   * @see Script#getInventoryIndex(int...)
   * @param spell the spell's ID (position in the list starting at 0).
   * @param slot the position of the item in the client's inventory, starting at 0.
   */
  public void castOnItem(int spell, int slot) {
    controller.castSpellOnInventoryItem(spell, slot);
  }

  /**
   * Takes an item from the ground by the item's id, x, y
   *
   * @param id the id of the ground item.
   * @param x the x position of the ground item.
   * @param y the y position of the ground item.
   */
  public void pickupItem(int id, int x, int y) {
    controller.pickupItem(x, y, id, false, true);
  }

  /**
   * Uses an item based on its position in the players inventory with an item on the ground based on
   * its id, x position, y position.
   *
   * @see Script#getItemById(int...)
   * @see Script#getInventoryIndex(int...)
   * @param item_slot the position of the item in the players inventory, starting at 0.
   * @param ground_id the id of the ground item.
   * @param ground_x the x position of the ground item.
   * @param ground_y the y position of the ground item.
   */
  public void useItemOnGroundItem(int item_slot, int ground_id, int ground_x, int ground_y) {
    controller.useSlotIndexOnGroundItem(ground_x, ground_y, item_slot, ground_id);
  }

  /**
   * Casts a spell on a ground item.
   *
   * @see Script#getItemById(int...)
   * @param spell the spell's position in the list starting at 0.
   * @param item_id the item's id.
   * @param item_x the item's x position.
   * @param item_y the item's y position.
   */
  public void castOnGroundItem(int spell, int item_id, int item_x, int item_y) {
    controller.castSpellOnGroundItem(spell, item_id, item_x, item_y);
  }

  /**
   * Uses an item by id with an object by id - only works if the player has the item, and the object
   * is within distance.
   *
   * @param item_id the ID of the item to use.
   * @param object_id the ID of the object to use the item on.
   */
  public void useItemOnObject(int item_id, int object_id) {
    final int[] object = getObjectById(object_id);
    if (object[0] != -1) {
      useItemOnObject(item_id, object[1], object[2]);
    }
  }

  //	private int getObjectIndex(int x, int y) {
  //		final int lx = x - this.getAreaX();
  //		final int ly = y - this.getAreaY();
  //		for (int i = 0; i < this.getObjectCount(); i++) {
  //			if (lx == this.getObjectLocalX(i) && ly == this.getObjectLocalY(i)) {
  //				return i;
  //			}
  //		}
  //		return -1;
  //	}

  /**
   * Uses an item by id with the object at the specified coordinates. This only works if the player
   * has the item, and the object is within distance.
   *
   * @param item_id the id of the item to use.
   * @param object_x x tile of the object.
   * @param object_y y tile of the object.
   */
  public void useItemOnObject(int item_id, int object_x, int object_y) {
    useSlotOnObject(getInventoryIndex(item_id), object_x, object_y);
  }

  public void useSlotOnObject(int slot, int object_x, int object_y) {
    controller.useItemSlotOnObject(object_x, object_y, slot);
  }

  /**
   * Locates the nearest NPC with the given id(s). This will search all NPCs in the area regardless
   * of whether they are in combat or talking.
   *
   * @see Script#getNpcById(int...)
   * @see Script#getNpcByIdNotTalk(int...)
   * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
   * @see Script#getNpcInRadius(int, int, int, int)
   * @param ids the NPC id(s) to search for.
   * @return always an integer array of size 3. If no NPC can be found, the array will contain -1,
   *     -1, -1. If an NPC was found, the array will contain the NPC's local index, X, Y.
   */
  public int[] getAllNpcById(int... ids) {

    for (int id : ids) {
      ORSCharacter npc = controller.getNearestNpcById(id, true);
      if (npc != null) {
        int[] coords = controller.getNpcCoordsByServerIndex(npc.serverIndex);
        int localIndex = this.getNpcLocalIndexFromServerIndex(npc.serverIndex);
        return new int[] {localIndex, coords[0], coords[1]};
      }
    }

    return new int[] {-1, -1, -1};
  }

  /**
   * Locates the nearest NPC with the given id(s). This will skip NPCs which are in combat.
   *
   * @see Script#getAllNpcById(int...)
   * @see Script#getNpcByIdNotTalk(int...)
   * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
   * @see Script#getNpcInRadius(int, int, int, int)
   * @param ids the NPC id(s) to search for.
   * @return always an integer array of size 3. If no NPC can be found, the array will contain -1,
   *     -1, -1. If an NPC was found, the array will contain the NPC's local index, X, Y.
   */
  public int[] getNpcById(int... ids) {
    for (int id : ids) {
      ORSCharacter npc = controller.getNearestNpcById(id, false);
      if (npc != null) {
        int[] coords = controller.getNpcCoordsByServerIndex(npc.serverIndex);
        int localIndex = this.getNpcLocalIndexFromServerIndex(npc.serverIndex);
        return new int[] {localIndex, coords[0], coords[1]};
      }
    }

    return new int[] {-1, -1, -1};
  }

  /**
   * Locates the nearest NPC with the given id(s). This will skip NPCs which are talking.
   *
   * @see Script#getAllNpcById(int...)
   * @see Script#getNpcById(int...)
   * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
   * @see Script#getNpcInRadius(int, int, int, int)
   * @param ids the NPC id(s) to search for.
   * @return always an integer array of size 3. If no NPC can be found, the array will contain -1,
   *     -1, -1. If an NPC was found, the array will contain the NPC's local index, X, Y.
   */
  public int[] getNpcByIdNotTalk(int... ids) {
    final int[] finalNpc = new int[] {-1, -1, -1};
    int max_dist = Integer.MAX_VALUE;
    for (int id : ids) {
      for (ORSCharacter npc : controller.getNpcs()) {
        if (npc.npcId == id) {
          if (!controller.isNpcTalking(npc.serverIndex)) {
            final int[] coords = controller.getNpcCoordsByServerIndex(npc.serverIndex);
            final int dist = distanceTo(coords[0], coords[1], getX(), getY());

            if (dist < max_dist) {
              max_dist = dist;
              finalNpc[0] = this.getNpcLocalIndexFromServerIndex(npc.serverIndex);
              finalNpc[1] = coords[0];
              finalNpc[2] = coords[1];
            }
          }
        }
      }
    }

    return finalNpc;
  }

  /**
   * Locates the nearest NPC with the given id within a radius. This is equivalent to a call to
   * getNpcInExtendedRadius(id, start_x, start_y, radius, radius).
   *
   * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
   * @see Script#getAllNpcById(int...)
   * @see Script#getNpcById(int...)
   * @see Script#getNpcByIdNotTalk(int...)
   * @param id the ID of the NPC to search for.
   * @param start_x the center X of the circle area.
   * @param start_y the center Y of the circle area.
   * @param radius the radius of the circle area.
   * @return always an integer array of size 3. If no NPC can be found, the array will contain -1,
   *     -1, -1. If an NPC was found, the array will contain the NPC's local index, X, Y.
   */
  public final int[] getNpcInRadius(int id, int start_x, int start_y, int radius) {
    return getNpcInExtendedRadius(id, start_x, start_y, radius, radius);
  }

  /**
   * Locates the nearest NPC with the given id(s) within a radius. This will skip NPCs which are in
   * combat.
   *
   * @see Script#getNpcInRadius(int, int, int, int)
   * @see Script#getAllNpcById(int...)
   * @see Script#getNpcById(int...)
   * @see Script#getNpcByIdNotTalk(int...)
   * @param id the ID of the NPC to search for.
   * @param start_x the center X of the circle area.
   * @param start_y the center Y of the circle area.
   * @param latitude the distance east and west of center X's area.
   * @param longitude the distance north and south of center Y's area.
   * @return always an integer array of size 3. If no NPC can be found, the array will contain -1,
   *     -1, -1. If an NPC was found, the array will contain the NPC's local index, X, Y.
   */
  public int[] getNpcInExtendedRadius(
      int id, int start_x, int start_y, int latitude, int longitude) {

    final int[] finalNpc = new int[] {-1, -1, -1};
    int max_dist = Integer.MAX_VALUE;
    for (ORSCharacter npc : controller.getNpcs()) {
      if (npc.npcId == id) {
        if (!controller.isNpcInCombat(npc.serverIndex)) {
          final int[] coords = controller.getNpcCoordsByServerIndex(npc.serverIndex);

          if (Math.abs(coords[0] - start_x) <= latitude
              && Math.abs(coords[1] - start_y) <= longitude) {
            final int dist = distanceTo(coords[0], coords[1], getX(), getY());
            if (dist < max_dist) {
              max_dist = dist;
              finalNpc[0] = this.getNpcLocalIndexFromServerIndex(npc.serverIndex);
              finalNpc[1] = coords[0];
              finalNpc[2] = coords[1];
            }
          }
        }
      }
    }

    return finalNpc;
  }

  /**
   * Attempts to start melee combat with the NPC with the given local index.
   *
   * @see Script#getNpcById(int...)
   * @see Script#getAllNpcById(int...)
   * @see Script#getNpcInRadius(int, int, int, int)
   * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
   * @param local_index the NPC's local index.
   */
  public void attackNpc(int local_index) {
    controller.attackNpc(this.getNpcServerIndex(local_index));
  }

  /**
   * Attempts to start talking to the NPC with the given local index.
   *
   * @see Script#getNpcByIdNotTalk(int...)
   * @param local_index the NPC's local index.
   */
  public void talkToNpc(int local_index) {
    controller.talkToNpc(this.getNpcServerIndex(local_index));
  }

  /**
   * Attempts to pickpocket the NPC with the given local index.
   *
   * @see Script#getNpcById(int...)
   * @see Script#getNpcInRadius(int, int, int, int)
   * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
   * @param local_index the NPC's local index.
   */
  public void thieveNpc(int local_index) {
    controller.thieveNpc(this.getNpcServerIndex(local_index));
  }

  /**
   * Attempts to cast a spell on the NPC with the given local index.
   *
   * @see Script#getAllNpcById(int...)
   * @param local_index the NPC's local index.
   * @param spell the spell's ID (position in the list starting at 0).
   */
  public void mageNpc(int local_index, int spell) {
    controller.castSpellOnNpc(this.getNpcServerIndex(local_index), spell);
  }
  /**
   * Retrieves the quest stage for the specified quest ID.
   *
   * @param questId the ID of the quest
   * @return the quest stage for the specified quest ID
   */
  public int getQuestStage(int questId) {
    return controller.getQuestStage(questId);
  }
  /**
   * Attempts to use an item in the client's inventory with a NPC.
   *
   * @see Script#getAllNpcById(int...)
   * @see Script#getNpcByIdNotTalk(int...)
   * @param local_index the NPC's local index.
   * @param slot the item's ID.
   */
  public void useOnNpc(int local_index, int slot) {
    controller.useItemOnNpc(
        this.getNpcServerIndex(local_index), controller.getInventorySlotItemId(slot));
  }

  /**
   * Returns true if a quest menu is visible.
   *
   * @return true if a quest menu is visible.
   */
  public boolean isQuestMenu() {
    return controller.isInOptionMenu();
  }

  /**
   * Returns an array of the visible quest menu options.
   *
   * @return an array of the visible quest menu options.
   */
  public String[] questMenuOptions() {
    return controller.getOptionsMenuText();
  }

  /**
   * Returns the number of visible quest menu options.
   *
   * @return the number of visible quest menu options.
   */
  public int questMenuCount() {
    return controller.getOptionMenuCount();
  }

  public String getQuestMenuOption(int i) {
    String returnValue = controller.getOptionsMenuText(i);

    if (returnValue == null) return "";

    return returnValue;
  }

  /**
   * Returns the position of the specified quest menu option, or -1 if the option could not be
   * found. <b>NOT CASE SENSITIVE</b>.
   *
   * @param str the string to compare to the visible options.
   * @return the specified string's position in the array, or -1.
   */
  public int getMenuIndex(String str) {
    if (isQuestMenu()) {
      for (int i = 0; i < questMenuCount(); i++) {
        if (questMenuOptions()[i].equalsIgnoreCase(str)) {
          return i;
        }
      }
    }
    return -1;
  }

  /**
   * Selects the quest menu option with the given position.
   *
   * @see Script#getMenuIndex(String)
   * @see Script#questMenuOptions()
   * @param i the option's position in the list, starting at 0.
   */
  public void answer(int i) {
    controller.optionAnswer(i);
  }

  /**
   * Locates the player with the given display name. <b>CASE SENSITIVE</b>.
   *
   * @param name the display name of the player to search for.
   * @return always an integer array of size 3. If no player can be found, the array will contain
   *     -1, -1, -1. If a player was found, the array will contain the player's local index, X, Y.
   */
  public int[] getPlayerByName(String name) {
    int serverIndex = controller.getPlayerServerIndexByName(name);

    if (serverIndex == -1) {
      return new int[] {-1, -1, -1};
    }

    int localIndex = this.getPlayerLocalIndexFromServerIndex(serverIndex);
    int[] coords = controller.getPlayerCoordsByServerIndex(serverIndex);

    return new int[] {localIndex, coords[0], coords[1]};
  }

  /**
   * @deprecated Unreliable in comparison to {@link Script#getPlayerByName(String)}.
   * @param server_index the server index of the player to search for.
   * @return always an integer array of size 3. If no player can be found, the array will contain
   *     -1, -1, -1. If a player was found, the array will contain the player's local index, X, Y.
   */
  @Deprecated
  public int[] getPlayerByPid(int server_index) {
    int[] coords = controller.getPlayerCoordsByServerIndex(server_index);

    if (coords[0] == -1) return new int[] {-1, -1, -1};

    int localIndex = this.getPlayerLocalIndexFromServerIndex(server_index);

    return new int[] {localIndex, coords[0], coords[1]};
  }

  /**
   * Returns the display name of the given player.
   *
   * @see Script#countPlayers()
   * @param local_index the player's local index.
   * @return the player's display name.
   */
  public String getPlayerName(int local_index) {
    ORSCharacter npc = controller.getPlayer(this.getPlayerServerIndexFromLocalIndex(local_index));

    if (npc == null) {
      return "NO_PLAYER";
    }

    return npc.displayName;
  }

  /**
   * Returns the server index of the given player.
   *
   * @see Script#countPlayers()
   * @see Script#getPlayerByName(String)
   * @param local_index the player's local index.
   * @return the player's server index.
   */
  public int getPlayerPID(int local_index) {
    ORSCharacter npc = controller.getPlayer(this.getPlayerServerIndexFromLocalIndex(local_index));

    if (npc == null) {
      return -1;
    }

    return npc.serverIndex;
  }

  public int getNpcServerIndex(int local_index) {
    return this.getNpcServerIndexFromLocalIndex(local_index);
  }

  public int getPlayerX(int index) {
    int serverIndex = this.getPlayerServerIndexFromLocalIndex(index);
    if (serverIndex == -1) return -1;

    return controller.getPlayerCoordsByServerIndex(serverIndex)[0];
  }

  public int getPlayerY(int index) {
    int serverIndex = this.getPlayerServerIndexFromLocalIndex(index);
    if (serverIndex == -1) return -1;

    return controller.getPlayerCoordsByServerIndex(serverIndex)[1];
  }

  /**
   * Returns the direction the given player is facing.
   *
   * @see Script#countPlayers()
   * @see Script#getPlayerByName(String)
   * @param local_index the player's local index.
   * @return the direction the player is facing: DIR_NORTH, DIR_NORTHWEST, DIR_WEST, DIR_SOUTHWEST,
   *     DIR_SOUTH, DIR_SOUTHEAST, DIR_EAST, DIR_NORTHEAST
   */
  public int getPlayerDirection(int local_index) {
    int serverIndex = this.getPlayerServerIndexFromLocalIndex(local_index);
    if (serverIndex == -1) return -1;

    return controller.getCharacterDirection(controller.getPlayer(serverIndex)).rsDir;
  }

  /**
   * Returns the combat level of the given player.
   *
   * @see Script#countPlayers()
   * @see Script#getPlayerByName(String)
   * @param local_index the player's local index.
   * @return the player's combat level.
   */
  public int getPlayerCombatLevel(int local_index) {
    int serverIndex = this.getPlayerServerIndexFromLocalIndex(local_index);

    if (serverIndex == -1) return -1;

    ORSCharacter player = controller.getPlayer(serverIndex);
    return player.level;
  }

  /**
   * Returns true if the given player is in combat.
   *
   * @see Script#countPlayers()
   * @see Script#getPlayerByName(String)
   * @param local_index the player's local index.
   * @return true if the player is in combat.
   */
  public boolean isPlayerInCombat(int local_index) {
    int serverIndex = this.getPlayerServerIndexFromLocalIndex(local_index);
    if (serverIndex == -1) return false;

    return controller.isPlayerInCombat(serverIndex);
  }

  public boolean isPlayerHpBarVisible(int index) {
    int serverIndex = this.getPlayerServerIndexFromLocalIndex(index);

    if (serverIndex == -1) return false;

    ORSCharacter player = controller.getPlayer(serverIndex);
    return player.combatTimeout > 0;
  }

  /**
   * Returns true if the given player is talking.
   *
   * @see Script#countPlayers()
   * @see Script#getPlayerByName(String)
   * @param local_index the player's local index.
   * @return true if the player is in talking.
   */
  public boolean isPlayerTalking(int local_index) {
    int serverIndex = this.getPlayerServerIndexFromLocalIndex(local_index);

    if (serverIndex == -1) return false;

    ORSCharacter player = controller.getPlayer(serverIndex);
    return player.messageTimeout > 0;
  }

  /**
   * Returns true if the given player is walking.
   *
   * @see Script#countPlayers()
   * @see Script#getPlayerByName(String)
   * @param local_index the player's local index.
   * @return true if the player is in walking.
   */
  public boolean isPlayerWalking(int local_index) {
    return controller.isPlayerCurrentlyWalking(getPlayerServerIndexFromLocalIndex(local_index));
  }

  /**
   * Returns the number of players visible to the this.
   *
   * @return the number of players visible to the this.
   */
  public int countPlayers() {
    return controller.getPlayerCount();
  }

  /**
   * Attempts to start melee combat with the given player.
   *
   * @see Script#countPlayers()
   * @see Script#getPlayerByName(String)
   * @param local_index the player's local index.
   */
  public void attackPlayer(int local_index) {
    int serverIndex = this.getPlayerServerIndexFromLocalIndex(local_index);
    if (serverIndex == -1) return;

    controller.attackPlayer(serverIndex);
  }

  /**
   * Attempts to cast a spell on the given player.
   *
   * @see Script#countPlayers()
   * @see Script#getPlayerByName(String)
   * @param local_index the player's local index.
   * @param spell the spell's identifier (position in the list starting at 0).
   */
  public void magePlayer(int local_index, int spell) {
    int serverIndex = this.getPlayerServerIndexFromLocalIndex(local_index);
    if (serverIndex == -1) return;

    controller.castSpellOnPlayer(spell, serverIndex);
  }

  /**
   * Locates the nearest object with the given id(s).
   *
   * @param ids the object id(s) to search for.
   * @return always an integer array of size 3. If no object can be found, the array will contain
   *     -1, -1, -1. If an object was found, the array will contain the object's id, X, Y.
   */
  public int[] getObjectById(int... ids) {
    int[] closest = new int[] {-1, -1, -1};
    int distance = Integer.MAX_VALUE;

    for (int id : ids) {
      int[] coords = controller.getNearestObjectById(id);

      if (coords != null) {

        if (controller.getDistanceFromLocalPlayer(coords[0], coords[1]) < distance) {
          distance = controller.getDistanceFromLocalPlayer(coords[0], coords[1]);
          closest = new int[] {id, coords[0], coords[1]};
        }
      }
    }

    return closest;
  }

  public int getObjectCount() {
    return controller.getObjectsCount();
  }

  public int getObjectId(int index) {
    if (index > controller.getObjectsCount()) return -1;

    return controller.getObjectsIds()[index];
    //		return this.getObjectId(index);
  }

  public int getObjectX(int index) {
    if (index >= controller.getObjectsCount()) return -1;

    return controller.offsetX(controller.getObjectsX()[index]);
  }

  public int getObjectY(int index) {
    if (index >= controller.getObjectsCount()) return -1;

    return controller.offsetZ(controller.getObjectsZ()[index]);
  }

  public static String getObjectName(int id) {
    return controller.getObjectName(id);
  }

  public static String getObjectDesc(int id) {
    return controller.getObjectExamineText(id);
  }

  /**
   * Returns the id of the object at the given coordinates, or -1 if no object could be found.
   *
   * @param x the x position of the tile to examine.
   * @param y the y position of the tile to examine.
   * @return the id of the object at the given coordinates, or -1 if no object could be found.
   */
  public int getObjectIdFromCoords(int x, int y) {
    return controller.getObjectAtCoord(x, y);
  }

  /**
   * Returns true if the tile at the given coordinates contains an object.
   *
   * @param x the x position of the tile to examine.
   * @param y the y position of the tile to examine.
   * @return true if the tile at the given coordinates contains an object.
   */
  public boolean isObjectAt(int x, int y) {
    return controller.getObjectAtCoord(x, y) != -1;
  }

  /**
   * Attempts to perform the primary action on the object at the given coordinates.
   *
   * @see Script#getObjectById(int...)
   * @see Script#atObject2(int, int)
   * @param x the x position of the object to interact with.
   * @param y the y position of the object to interact with.
   */
  public void atObject(int x, int y) {
    controller.atObject(x, y);
  }

  /**
   * Attempts to perform the secondary action on the object at the given coordinates.
   *
   * @see Script#getObjectById(int...)
   * @see Script#atObject(int, int)
   * @param x the x position of the object to interact with.
   * @param y the y position of the object to interact with.
   */
  public void atObject2(int x, int y) {
    controller.atObject2(x, y);
  }

  /**
   * Locates the nearest boundary with the given id(s).
   *
   * @param ids the boundary id(s) to search for.
   * @return always an integer array of size 3. If no boundary can be found, the array will contain
   *     -1, -1, -1. If a boundary was found, the array will contain the boundary's id, X, Y.
   */
  public int[] getWallObjectById(int... ids) {
    final int[] bound = new int[] {-1, -1, -1};
    int max_dist = Integer.MAX_VALUE;

    for (int i = 0; i < controller.getWallObjectsCount(); i++) {
      final int id = controller.getWallObjectIds()[i];
      if (inArray(ids, id)) {
        final int x = controller.offsetX(controller.getWallObjectsX()[i]);
        final int y = controller.offsetZ(controller.getWallObjectsZ()[i]);

        final int dist = distanceTo(x, y, getX(), getY());
        if (dist < max_dist) {
          bound[0] = id;
          bound[1] = x;
          bound[2] = y;
          max_dist = dist;
        }
      }
    }

    return bound;
  }

  /**
   * Returns the id of the boundary at the given coordinates, or -1.
   *
   * @param x the x position of the tile to examine.
   * @param y the y position of the tile to examine.
   * @return the id of the boundary at the given coordinates, or -1.
   */
  public int getWallObjectIdFromCoords(int x, int y) {
    return controller.getWallObjectIdAtCoord(x, y);
  }

  public int getWallObjectCount() {
    return controller.getWallObjectsCount();
  }

  public int getWallObjectId(int index) {
    if (controller.getWallObjectsCount() >= index) return -1;

    return controller.getWallObjectIds()[index];
  }

  public int getWallObjectX(int index) {
    if (controller.getWallObjectsCount() >= index) return -1;

    return controller.offsetX(controller.getWallObjectsX()[index]);
  }

  public int getWallObjectY(int index) {
    if (controller.getWallObjectsCount() >= index) return -1;

    return controller.offsetZ(controller.getWallObjectsZ()[index]);
  }

  public static String getWallObjectName(int id) {
    return controller.getWallObjectExamineText(id);
  }

  public static String getWallObjectDesc(int id) {
    return controller.getWallObjectExamineText(id);
  }

  //	private int getBoundIndex(int x, int y) {
  //		final int lx = x - this.getAreaX();
  //		final int ly = y - this.getAreaY();
  //		for (int i = 0; i < this.getBoundCount(); i++) {
  //			if (this.getBoundLocalX(i) == lx && this.getBoundLocalY(i) == ly) {
  //				return i;
  //			}
  //		}
  //		return -1;
  //	}

  /**
   * Attempts to perform the primary action on the boundary at the given coordinates.
   *
   * @see Script#getWallObjectById(int...)
   * @see Script#atWallObject2(int, int)
   * @param x the x position of the boundary to interact with.
   * @param y the y position of the boundary to interact with.
   */
  public void atWallObject(int x, int y) {
    // controller.openDoor(x, y);
    controller.atWallObject(x, y);
    controller.sleep(2000);
  }

  /**
   * Attempts to perform the secondary action on the boundary at the given coordinates.
   *
   * @see Script#getWallObjectById(int...)
   * @see Script#atWallObject(int, int)
   * @param x the x position of the boundary to interact with.
   * @param y the y position of the boundary to interact with.
   */
  public void atWallObject2(int x, int y) {
    controller.atWallObject2(x, y);
  }

  /**
   * Attempts to use an item in the client's inventory with the boundary at the given coordinates.
   *
   * @param slot the position of the item in the client's inventory, starting at 0.
   * @param x the x position of the boundary to interact with.
   * @param y the y position of the boundary to interact with.
   */
  public void useItemOnWallObject(int slot, int x, int y) {
    controller.useItemOnWall(x, y, slot);
  }

  /**
   * Attempts to deposit the given amount of the given item in the client's bank.
   *
   * @param id the id of the item to deposit.
   * @param amount the number of the given item to deposit.
   */
  public void deposit(int id, int amount) {
    controller.depositItem(id, amount);
  }

  /**
   * Attempts to withdraw the given amount of the given item from the client's bank.
   *
   * @param id the id of the item to withdraw.
   * @param amount the number of the given item to withdraw.
   */
  public void withdraw(int id, int amount) {
    controller.withdrawItem_apos(id, amount);
  }

  /**
   * Returns the number of items with the given ids(s) in the client's bank if the bank is visible.
   *
   * @param ids the item id(s) to search for.
   * @return the number of items with the given id(s) in the client's bank.
   */
  public int bankCount(int... ids) {
    int count = 0;
    if (isBanking()) {
      for (int i = 0; i < this.getBankSize(); i++) {
        if (inArray(ids, this.getBankId(i))) {
          count += this.getBankStack(i);
        }
      }
    }
    return count;
  }

  public int getBankSize() {
    return controller.getBankItemsCount();
    //		return this.getBankSize();
  }

  public int getBankId(int i) {
    // if(i >= controller.getBankItemsCount())
    if (i >= controller.getBankItems().size()) return -1;

    return controller.getBankItems().get(i).getCatalogID();
    //		return this.getBankId(i);
  }

  public int getBankStack(int i) {
    int bankCount = controller.getBankItemsCount();

    if (i >= bankCount) return -1;

    return controller.getBankItems().get(i).getAmount();
    //		return this.getBankStack(i);
  }

  /**
   * Returns true if the client's bank contains at least one item with the given id if the bank is
   * visible.
   *
   * @param id the item to search for.
   * @return true if the client's bank contains at least one item with the given id.
   */
  public boolean hasBankItem(int id) {
    return controller.isItemInBank(id);
  }

  /**
   * Returns true if the bank screen is visible.
   *
   * @return true if the bank screen is visible.
   */
  public boolean isBanking() {
    return controller.isInBank();
  }

  /** Closes the client's bank. */
  public void closeBank() {
    controller.closeBank();
  }

  /**
   * Returns the distance between the client and the tile at x, y.
   *
   * @param x the x position of the tile to compare to the client's position.
   * @param y the y position of the tile to compare to the client's position.
   * @return the distance between the client and the given tile.
   */
  public int distanceTo(int x, int y) {
    return distanceTo(x, y, getX(), getY());
  }

  /**
   * Returns the distance between x1, y1 and x2, y2.
   *
   * @param x1 the first x coordinate.
   * @param y1 the first y coordinate.
   * @param x2 the second x coordinate.
   * @param y2 the second y coordinate.
   * @return the distance between x1, y1 and x2, y2.
   */
  public static int distanceTo(int x1, int y1, int x2, int y2) {
    return (int) Math.hypot(Math.abs(x1 - x2), Math.abs(y1 - y2));
  }

  /**
   * Returns true if the tile the client is standing on is within radius distance of x, y.
   *
   * @param x the x position of the comparison tile.
   * @param y the y position of the comparison tile.
   * @param radius the maximum distance between the client and x, y for the method to return true.
   * @return true if the tile the client is standing on is within radius distance of x, y.
   */
  public boolean isAtApproxCoords(int x, int y, int radius) {
    return distanceTo(x, y) <= radius;
  }

  /**
   * Returns true if it is possible for the client to walk to x, y.
   *
   * @param x the x position of the comparison tile.
   * @param y the y position of the comparison tile.
   * @return true if it is possible for the client to walk to x, y.
   */
  public boolean isReachable(int x, int y) {
    return controller.isReachable(x, y, false);
    //		final int dx = x - this.getAreaX();
    //		final int dy = y - this.getAreaY();
    //		if (locRouteCalc == null) {
    //			locRouteCalc = new LocalRouteCalc();
    //		}
    //		return locRouteCalc.calculate(this.getAdjacency(), this.getLocalX(), this.getLocalY(), dx,
    // dy, dx, dy, false) != -1;
  }

  /**
   * Attempts to enable a prayer.
   *
   * @param prayer the position of the prayer in the list, starting at 0.
   */
  public void enablePrayer(int prayer) {
    controller.enablePrayer(prayer);
  }

  /**
   * Attempts to disable a prayer.
   *
   * @param prayer the position of the prayer in the list, starting at 0.
   */
  public void disablePrayer(int prayer) {
    controller.disablePrayer(prayer);
  }

  /**
   * Returns true if the specified prayer is enabled.
   *
   * @param prayer the position of the prayer in the list, starting at 0.
   * @return true if the specified prayer is enabled.
   */
  public boolean isPrayerEnabled(int prayer) {
    return controller.isPrayerOn(prayer);
  }

  /**
   * Returns true if the shop screen is visible.
   *
   * @return true if the shop screen is visible.
   */
  public boolean isShopOpen() {
    return controller.isInShop();
  }

  /**
   * Returns the position of the item with the given id in the shop screen.
   *
   * @param id the item id to search for.
   * @return the position of the item with the given id in the shop screen.
   */
  public int getShopItemById(int id) {

    int i = 0;
    for (Item item : controller.getShopItems()) {
      if (item.getCatalogID() == id) {
        return i;
      }

      i++;
    }

    return -1;
  }

  public void print(String toPrint) {
    System.out.println(toPrint);
  }
  /**
   * Returns the ID of the item at the given shop position, starting at 0.
   *
   * @param i the position.
   * @return the id of the item at i position.
   */
  public int getShopItemId(int i) {
    return controller.getShopItems().get(i).getCatalogID();
  }

  /**
   * Returns the size of the stack the shop has for the item at the given shop position, starting at
   * 0.
   *
   * @see Script#getShopItemById(int)
   * @param i the position of the item in the shop screen.
   * @return the size of the stack the shop has for the item at the given shop position.
   */
  public int getShopItemAmount(int i) {
    return controller.getShopItemsCount();
  }

  /**
   * Attempts to buy the given number of the item at the given position from the visible shop.
   *
   * @param i the position of the item in the shop screen.
   * @param amount the number to buy.
   */
  public void buyShopItem(int i, int amount) {
    if (i >= controller.getShopItems().size()) return;

    int id = controller.getShopItems().get(i).getCatalogID();
    controller.shopBuy(id, amount);
  }

  /**
   * Attempts to sell the given number of the item at the given position from the visible shop.
   *
   * @param i the position of the item in the shop screen.
   * @param amount the number to sell.
   */
  public void sellShopItem(int i, int amount) {
    if (i >= controller.getShopItems().size()) return;

    int id = controller.getShopItems().get(i).getCatalogID();
    controller.shopSell(id, amount);
  }

  /** Attempts to close the shop screen. */
  public void closeShop() {
    controller.closeShop();
  }

  /**
   * Attempts to send a trade request to the player with the given server index.
   *
   * @see Script#getPlayerPID(int)
   * @param server_index the server index of the player to send the request to.
   */
  public void sendTradeRequest(int server_index) {
    controller.tradePlayer(server_index);
  }

  /**
   * Attempts to start following the player with the given server index.
   *
   * @see Script#getPlayerPID(int)
   * @param server_index the server index of the player to start following.
   */
  public void followPlayer(int server_index) {
    controller.followPlayer(server_index);
  }

  /**
   * Attempts to use an item in the client's inventory with a player.
   *
   * @see Script#getAllNpcById(int...)
   * @see Script#getNpcByIdNotTalk(int...)
   * @param local_index the NPC's local index.
   * @param slot the item's ID.
   */
  public void useItemWithPlayer(int local_index, int slot) {
    controller.useItemOnPlayer(slot, this.getPlayerLocalIndexFromServerIndex(local_index));
  }

  /**
   * Returns true if the trade offer screen is visible.
   *
   * @see Script#isInTradeConfirm()
   * @return true if the trade offer screen is visible.
   */
  public boolean isInTradeOffer() {
    return controller.isInTrade();
  }

  /**
   * Returns true if the trade confirm screen is visible.
   *
   * @see Script#isInTradeOffer()
   * @return true if the trade confirm screen is visible.
   */
  public boolean isInTradeConfirm() {
    return controller.isInTradeConfirmation();
  }

  /**
   * Attempts to add items to the current trade offer.
   *
   * @param slot the position of the item to offer in the client's inventory, starting at 0.
   * @param amount the number of the given item to offer.
   */
  public void offerItemTrade(int slot, int amount) {
    this.offerItemTrade(slot, amount);
  }

  /**
   * Returns true if the other player's trade offer contains at least the given number of the given
   * item.
   *
   * @param id the id of the item to search for.
   * @param amount the number of the item to ensure is there.
   * @return true if the other player has traded at least the given number of the given item.
   */
  public boolean hasOtherTraded(int id, int amount) {
    List<Item> items = controller.getRecipientTradeItems();

    for (Item item : items) {
      if (item.getItemDef().id == id) {
        if (item.getAmount() >= amount) return true;
      }
    }

    return false;
  }

  /**
   * Returns the number of items in the client's trade offer.
   *
   * @deprecated use Script#getLocalTradeItemCount()
   * @return the number of items in the client's trade offer.
   */
  @Deprecated
  public int getOurTradedItemCount() {
    return this.getLocalTradeItemCount();
  }

  public int getLocalTradeItemCount() {
    return controller.getLocalTradeItemsCount();
  }

  public int getLocalTradeItemId(int i) {
    if (i >= controller.getLocalTradeItemsCount()) return -1;

    return controller.getLocalTradeItems().get(i).getCatalogID();
  }

  public int getLocalTradeItemStack(int i) {
    if (i >= controller.getLocalTradeItemsCount()) return -1;

    return controller.getLocalTradeItems().get(i).getAmount();
  }

  /**
   * Returns the number of items in the other player's trade offer.
   *
   * @deprecated use Script#getRemoteTradeItemCount()
   * @return the number of items in the other player's trade offer.
   */
  @Deprecated
  public int getTheirTradedItemCount() {
    return this.getRemoteTradeItemCount();
  }

  public int getRemoteTradeItemCount() {
    return controller.getRecipientTradeItemsCount();
  }

  public int getRemoteTradeItemId(int i) {
    if (i >= controller.getRecipientTradeItemsCount()) return -1;

    return controller.getRecipientTradeItems().get(i).getCatalogID();
  }

  public int getRemoteTradeItemStack(int i) {
    if (i >= controller.getRecipientTradeItemsCount()) return -1;

    return controller.getRecipientTradeItems().get(i).getAmount();
  }

  //	public boolean hasLocalAcceptedTrade() {
  //	}

  public boolean hasRemoteAcceptedTrade() {
    return controller.isTradeRecipientAccepting();
  }

  /**
   * Attempts to accept the trade offer.
   *
   * @see Script#confirmTrade()
   * @see Script#declineTrade()
   */
  public void acceptTrade() {
    controller.acceptTrade();
  }

  /**
   * Attempts to confirm the trade offer.
   *
   * @see Script#acceptTrade()
   * @see Script#declineTrade()
   */
  public void confirmTrade() {
    controller.acceptTradeConfirmation();
  }

  /**
   * Attempts to decline the trade.
   *
   * @see Script#acceptTrade()
   * @see Script#confirmTrade()
   */
  public void declineTrade() {
    controller.declineTrade();
  }

  /**
   * Draws a string on the game's canvas. Only use in paint().
   *
   * @param str the string to draw.
   * @param x the x position to start at.
   * @param y the y position to start at.
   * @param size the size of the text.
   * @param colour the hex colour (24-bit RGB) of the text.
   */
  public void drawString(String str, int x, int y, int size, int colour) {
    controller.drawString(str, x, y, colour, 2);
  }

  /**
   * Draws a horizontal 1px line on the game. Only use in paint().
   *
   * @param x the x position to start at.
   * @param y the y position to start at.
   * @param length the length of the line in pixels.
   * @param colour the hex colour (24-bit RGB) of the line.
   */
  public void drawHLine(int x, int y, int length, int colour) {
    controller.drawLineHoriz(x, y, length, colour);
  }

  /**
   * Draws a vertical 1px line on the game. Only use in paint().
   *
   * @param x the x position to start at.
   * @param y the y position to start at.
   * @param length the length of the line in pixels.
   * @param colour the hex colour (24-bit RGB) of the line.
   */
  public void drawVLine(int x, int y, int length, int colour) {
    controller.drawLineVert(x, y, length, colour);
  }

  /**
   * Draws a 1px outline of a equiangular quadrilateral on the game. Only use in paint().
   *
   * @param x the x position to start at.
   * @param y the y position to start at.
   * @param width the width of the shape.
   * @param height the height of the shape.
   * @param colour the hex colour (24-bit RGB) of the shape.
   */
  public void drawBoxOutline(int x, int y, int width, int height, int colour) {
    drawHLine(x, y, width, colour);
    drawHLine(x, (y + height) - 1, width, colour);
    drawVLine(x, y, height, colour);
    drawVLine((x + width) - 1, y, height, colour);
  }

  /**
   * Draws a filled equiangular quadrilateral on the game. Only use in paint().
   *
   * @param x the x position to start at.
   * @param y the y position to start at.
   * @param width the width of the shape.
   * @param height the height of the shape.
   * @param colour the colour (24-bit RGB) of the shape.
   */
  public void drawBoxFill(int x, int y, int width, int height, int colour) {
    // only 1 script used this
    Main.log("apos.drawBoxFill() unimplemented");
  }

  /**
   * Draws a filled equiangular quadrilateral with transparency on the game. Only use in paint().
   *
   * @param x the x position to start at.
   * @param y the y position to start at.
   * @param width the width of the shape.
   * @param height the height of the shape.
   * @param trans transparency of the shape (255-0).
   * @param colour the colour (24-bit RGB) of the shape.
   */
  public void drawBoxAlphaFill(int x, int y, int width, int height, int trans, int colour) {
    controller.drawBoxAlpha(x, y, width, height, colour, trans);
  }

  /**
   * Draws a filled circle with optional transparency (use 255 for solid) on the game.
   *
   * @param x the x position of the centre of the circle.
   * @param y the y position of the centre of the circle.
   * @param radius the radius of the circle.
   * @param colour the colour (24-bit RGB) of the shape.
   * @param trans transparency of the shape (255-0).
   */
  public void drawCircleFill(int x, int y, int radius, int colour, int trans) {
    controller.drawCircle(x, y, radius, colour, trans, 0);
  }

  /**
   * Sets a pixel on the game.
   *
   * @param x the x position of the pixel.
   * @param y the y position of the pixel.
   * @param colour the colour (24-bit RGB) to set.
   */
  public void setPixel(int x, int y, int colour) {
    //		RasterOps.setPixel(this.getPixels(),
    //		this.getGameWidth(), this.getGameHeight(),
    //		x, y, colour);
    // no scripts use this.
    Main.log("apos.setPixel() unimplemented");
  }

  /**
   * Draws an image on the game.
   *
   * @param image the image to draw.
   * @param start_x the x position to start drawing.
   * @param start_y the y position to start drawing.
   */
  public void drawImage(Image image, int start_x, int start_y) {
    // no scripts use this.
    Main.log("apos.drawImage() unimplemented");
  }
  //		if (image instanceof BufferedImage) {
  //			drawBuf((BufferedImage) image,
  //		start_x, start_y, this.getPixels(),
  //		this.getGameWidth(), this.getGameHeight());
  //			return;
  //		}
  //		if (imageCache == null) {
  //			imageCache = new HashMap<>();
  //		}
  //		int width;
  //		int height;
  //		BufferedImage buf;
  //		if ((buf = imageCache.get(image)) != null) {
  //			width = buf.getWidth();
  //			height = buf.getHeight();
  //		} else {
  //			width = image.getWidth(null);
  //			height = image.getHeight(null);
  //			buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  //			final Graphics g = buf.createGraphics();
  //			g.drawImage(image, 0, 0, null);
  //			g.dispose();
  //			imageCache.put(image, buf);
  //		}
  //		drawBuf(buf, start_x, start_y, this.getPixels(),
  //		this.getGameWidth(), this.getGameHeight());
  //	}
  //
  //	private static void drawBuf(BufferedImage image,
  //	int start_x, int start_y,
  //		int[] pixels, int rw, int rh) {
  //
  //		final int width = image.getWidth();
  //		final int height = image.getHeight();
  //		for (int x = 0; x < width; x++) {
  //			for (int y = 0; y < height; y++) {
  //				final int d_x = (start_x + x);
  //				final int d_y = (start_y + y);
  //				if (d_x > rw || d_y > rh) {
  //					continue;
  //				}
  //				pixels[d_x + d_y * 512] = 0xff000000 | image.getRGB(x, y);
  //			}
  //		}
  //	}

  /**
   * Prints a line of text in the in-game chat box and console.
   *
   * @param str the text to print.
   */
  public void writeLine(String str) {
    controller.displayMessage(str);
  }

  /**
   * Returns the number of NPCs visible to the this.
   *
   * @return the number of NPCs visible to the this.
   */
  public int countNpcs() {
    return controller.getNpcCount();
  }

  public int getNpcId(int index) {
    return controller.getNpc(getNpcServerIndexFromLocalIndex(index)).npcId;
  }

  public int getNpcX(int index) {
    return controller.getNpcCoordsByServerIndex(getNpcServerIndexFromLocalIndex(index))[0];
  }

  public int getNpcY(int index) {
    return controller.getNpcCoordsByServerIndex(getNpcServerIndexFromLocalIndex(index))[1];
  }

  public boolean isNpcInCombat(int index) {
    return controller.isNpcInCombat(getNpcServerIndexFromLocalIndex(index));
  }

  public boolean isNpcTalking(int index) {
    return controller.isNpcTalking(this.getNpcServerIndexFromLocalIndex(index));
  }

  public boolean isNpcHpBarVisible(int index) {
    return controller.getNpc(this.getNpcServerIndexFromLocalIndex(index)).combatTimeout > 0;
  }

  /**
   * Returns the name of the given NPC.
   *
   * @param index the NPC's local index.
   * @return the NPC's name.
   */
  public String getNpcName(int index) {
    ORSCharacter npc = controller.getNpc(getNpcServerIndexFromLocalIndex(index));

    if (npc == null) return "NO_NPC";

    return npc.displayName;
  }

  public static String getNpcNameId(int id) {
    return controller.getNpcName(id);
  }

  /**
   * Returns the description (examine text) of the given NPC.
   *
   * @param index the NPC's local index.
   * @return the NPC's description (examine text).
   */
  public String getNpcDescription(int index) {
    ORSCharacter npc = controller.getNpc(getNpcServerIndexFromLocalIndex(index));

    if (npc == null) return "NO_NPC";

    return controller.getNpcExamineText(npc.npcId);
  }

  public static String getNpcDescriptionId(int id) {
    return controller.getNpcExamineText(id);
  }

  /**
   * Returns the combat level of the given NPC.
   *
   * @param index the NPC's local index.
   * @return the NPC's combat level.
   */
  public int getNpcCombatLevel(int index) {
    return controller.getNpc(this.getNpcServerIndexFromLocalIndex(index)).level;
  }

  public static int getNpcCombatLevelId(int id) {
    Main.log("apos.getNpcCombatLevelId() unimplemented");
    return 0;
    // return StaticAccess.get().getNpcLevel(id);
  }

  public int getInventoryId(int slot) {
    return controller.getInventorySlotItemId(slot);
  }

  public int getInventoryStack(int slot) {
    int id = controller.getInventorySlotItemId(slot);
    return controller.getInventoryItemCount(id);
  }

  /**
   * Returns the name of an item in the client's inventory.
   *
   * @param slot the position of the item in the client's inventory, starting at 0.
   * @return the item's name.
   */
  public String getItemName(int slot) {
    return getItemNameId(this.getInventoryId(slot));
  }

  public static String getItemNameId(int id) {
    return controller.getItemName(id);
  }

  /**
   * Returns the description (examine text) of an item in the client's inventory.
   *
   * @param slot the position of the item in the client's inventory, starting at 0.
   * @return the item's description (examine text).
   */
  public String getItemDescription(int slot) {
    return getItemDescriptionId(this.getInventoryId(slot));
  }

  public static String getItemDescriptionId(int id) {
    return controller.getItemExamineText(id);
  }

  /**
   * Returns the primary action ("eat", "bury") of an item in the client's inventory.
   *
   * @param slot the position of the item in the client's inventory, starting at 0.
   * @return the item's primary action ("eat", "bury").
   */
  public String getItemCommand(int slot) {
    return getItemCommandId(this.getInventoryId(slot));
  }

  public static String getItemCommandId(int id) {
    return controller.getItemCommand(id);
  }

  /**
   * Returns true if an item in the client's inventory is tradeable.
   *
   * @param slot the position of the item in the client's inventory, starting at 0.
   * @return true if the item is tradeable.
   */
  public boolean isItemTradable(int slot) {
    return controller.isItemTradeable(controller.getInventorySlotItemId(slot));
  }

  public static boolean isItemTradableId(int id) {
    return controller.isItemTradeable(id);
  }

  public boolean isItemStackable(int slot) {
    return isItemStackableId(controller.getInventorySlotItemId(slot));
  }

  public static boolean isItemStackableId(int id) {
    return controller.isItemStackable(id);
  }

  /**
   * Returns the approximate value of an item in the client's inventory. Should generally be used
   * just for comparison.
   *
   * @param slot the position of the item in the client's inventory, starting at 0.
   * @return the approximate value of the item.
   */
  public int getItemBasePrice(int slot) {
    return getItemBasePriceId(controller.getInventorySlotItemId(slot));
  }

  public static int getItemBasePriceId(int id) {
    return controller.getShopItemPrice(id);
  }

  /**
   * Returns true if the client has the required magic level and reagents to cast the given spell.
   *
   * @param spell the spell's position in the list starting at 0.
   * @return true if the client has the required magic level and reagents to cast the given spell.
   */
  public boolean canCastSpell(int spell) {
    return controller.canCastSpell(spell);
  }

  /**
   * Enables or disables autologin.
   *
   * @param flag on/off
   * @see Script#stopScript()
   */
  public void setAutoLogin(boolean flag) {
    controller.setAutoLogin(flag);
  }

  /**
   * See {@link Script#setAutoLogin(boolean)}. This checks the set flag.
   *
   * @return true if autologin is enabled.
   */
  public boolean isAutoLogin() {
    return controller.isAutoLogin();
  }

  /**
   * When disabled (false), may cause the game to enter a non-standard (implemented by the bot) low
   * graphics mode. The graphics buffer may no longer be updated. When re-enabled, there may be a
   * minor delay before the buffer is updated again.
   *
   * @param flag the rendering flag.
   */
  public void setRendering(boolean flag) {
    controller.setDrawing(flag, 0);
  }

  /**
   * See {@link Script#setRendering(boolean)}. This checks the set flag.
   *
   * @return true if rendering is enabled.
   */
  public boolean isRendering() {
    return controller.isDrawEnabled();
  }

  /**
   * Some implementations support a low graphics mode that skips lines when rendering (usually
   * toggled by pressing F1). This enables or disables that mode.
   *
   * @param flag the skip_lines flag.
   */
  public void setSkipLines(boolean flag) {
    controller.setInterlacer(flag);
  }

  /**
   * See {@link Script#setSkipLines(boolean)}. This checks the set flag.
   *
   * @return true if skip_lines is enabled.
   */
  public boolean isSkipLines() {
    return controller.isInterlacing();
  }

  /**
   * If paint_overlay is disabled, {@link Script#paint()} will not be called and the built-in bot
   * display (if any) will be hidden. May be effected by the rendering flag {@link
   * Script#setRendering(boolean)}.
   *
   * @param flag the paint_overlay flag.
   */
  public void setPaintOverlay(boolean flag) {
    controller.setBotPaint(flag);
  }

  /**
   * See {@link Script#setPaintOverlay(boolean)}. This checks the set flag.
   *
   * @return true if paint_overlay is enabled.
   */
  public boolean isPaintOverlay() {
    return controller.getShowBotPaint();
  }

  /**
   * Attempts to save a screenshot with the specified file name in the Screenshots directory. The
   * file type will always be .png.
   *
   * @param file the file name (excluding extension) to save the screenshot as.
   */
  public void takeScreenshot(String file) {
    controller.takeScreenshot(file);
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

  /**
   * Returns true if the int[] contains the int.
   *
   * @param haystack int[] to search.
   * @param needle int to search for.
   * @return true if the int[] contains the int.
   */
  public static boolean inArray(int[] haystack, int needle) {
    if (haystack == null) return false;

    for (final int element : haystack) {
      if (element == needle) {
        return true;
      }
    }
    return false;
  }

  /**
   * @deprecated Will cause the entire game to freeze when called in main()'s thread. The RSC
   *     servers expect a ping every 5 seconds among other things, so use of this method is
   *     definitely not recommended. If you find yourself using this, consider updating your code to
   *     make use of System.currentTimeMillis() or System.nanoTime().
   * @param ms time in milliseconds to sleep for.
   */
  @Deprecated
  public static void sleep(int ms) {
    controller.sleep(ms);
  }

  /**
   * Attempts to send a private message to the player with the given display name.
   *
   * @param msg the message to send.
   * @param name the display name to send the private message to.
   */
  public void sendPrivateMessage(String msg, String name) {
    controller.sendPrivateMessage(name, msg);
  }

  public void addFriend(String name) {
    controller.addFriend(name);
  }

  public void removeFriend(String name) {
    controller.removeFriend(name);
  }

  public void addIgnore(String name) {
    controller.addIgnore(name);
  }

  public void removeIgnore(String name) {
    controller.removeIgnore(name);
  }

  public int getFriendCount() {
    return controller.getFriendList().size();
  }

  public String getFriendName(int i) {
    return controller.getFriendList().get(i);
  }

  public int getIgnoredCount() {
    return controller.getIgnoreList().size();
  }

  public String getIgnoredName(int i) {
    return controller.getIgnoreList().get(i);
  }

  public boolean isFriend(String name) {
    return controller.getFriendList().contains(name);
  }

  public boolean isIgnored(String name) {
    return controller.getIgnoreList().contains(name);
  }

  public boolean isLoggedIn() {
    return controller.isLoggedIn();
  }

  public static boolean isCombatSpell(int spell) {
    return controller.getSpellType(spell) == 2;
  }

  public static boolean isCastableOnInv(int spell) {
    return controller.getSpellType(spell) == 3;
  }

  public static boolean isCastableOnGroundItem(int spell) {
    return controller.getSpellType(spell) == 3;
  }

  public static boolean isCastableOnSelf(int spell) {
    return controller.getSpellType(spell) == 0;
  }

  public static int getPrayerCount() {
    return controller.getPrayersCount();
  }

  public static String getPrayerName(int i) {
    return controller.getPrayerName(i);
  }

  public static int getPrayerLevel(int i) {
    return controller.getPrayerLevel(i);
  }

  public int getQuestCount() {
    return controller.getQuestsCount();
  }

  public String getQuestName(int i) {
    if (i > this.getQuestCount()) return "NO_QUEST";

    return controller.getQuestNames()[i];
  }

  public boolean isQuestComplete(int i) {
    return controller.isQuestComplete(i);
  }

  public int getGameWidth() {
    return controller.getGameWidth();
  }

  public int getGameHeight() {
    return controller.getGameHeight();
  }

  /**
   * <b>New APOS function.</b> On Coleslaw, returns whether or not we are currently batching.
   *
   * @return true if batching and false if not
   */
  public boolean isBatching() {
    return controller.isBatching();
  }

  // adapter functions for IdleRSC:
  private int getNpcLocalIndexFromServerIndex(int serverIndex) {
    int i = 0;
    for (ORSCharacter npc : controller.getNpcs()) {
      if (npc.serverIndex == serverIndex) return i;

      i++;
    }

    return -1;
  }

  private int getPlayerLocalIndexFromServerIndex(int serverIndex) {
    int i = 0;
    for (ORSCharacter player : controller.getPlayers()) {
      if (player.serverIndex == serverIndex) return i;

      i++;
    }

    return -1;
  }

  private int getNpcServerIndexFromLocalIndex(int local_index) {
    if (local_index >= controller.getNpcCount()) return -1;

    return controller.getNpcs().get(local_index).serverIndex;
  }

  private int getPlayerServerIndexFromLocalIndex(int local_index) {
    try {
      return controller.getPlayers().get(local_index).serverIndex;
    } catch (Exception e) {
      return -1;
    }
  }

  public boolean isControllerSet() {
    return controller != null;
  }
}

/*
 * unimplemented functions:
 *
 * <p>x hop (currently there aren't multiple worlds on openrsc) x getAccurateFatigue (inauthentic,
 * no decimal precision) x getAccurateXpForLevel (inauthentic, no decimal precision)
 *
 * <p>x drawBoxFill (no scripts use this.) x drawImage (no scripts use this.) x setPixel (no scripts
 * use this.) x getNpcCombatLevelId (no scripts use this. additionally, this informaiton is not
 * provided by entityhandler.) x hasLocalAcceptedTrade (no scripts use this.)
 */
