package controller;

import bot.Main;
import callbacks.DrawCallback;
import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.DoorDef;
import com.openrsc.client.entityhandling.defs.GameObjectDef;
import com.openrsc.client.entityhandling.defs.ItemDef;
import com.openrsc.client.entityhandling.defs.SpellDef;
import com.openrsc.client.entityhandling.instances.Item;
import com.openrsc.client.model.Sprite;
import com.openrsc.interfaces.misc.AuctionHouse;
import com.openrsc.interfaces.misc.ProgressBarInterface;
import controller.WebWalker.WebWalker;
import controller.WebWalker.WebwalkGraph;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import models.entities.*;
import orsc.Config;
import orsc.ORSCharacter;
import orsc.OpenRSC;
import orsc.buffers.RSBufferUtils;
import orsc.enumerations.GameMode;
import orsc.enumerations.MessageType;
import orsc.enumerations.ORSCharacterDirection;
import orsc.graphics.gui.Panel;
import orsc.graphics.gui.SocialLists;
import orsc.graphics.two.MudClientGraphics;
import orsc.mudclient;
import reflector.Reflector;

/**
 * This is the native scripting API for IdleRSC.
 *
 * @author Dvorak
 */
public class Controller {
  private final Reflector reflector;
  private final OpenRSC client;
  private final mudclient mud;

  final int[] foodIds = {
    335, 333, 350, 352, 355, 357, 359, 362, 364, 367, 370, 373, 718, 551, 553, 555, 590, 546, 1193,
    1191, 325, 326, 327, 328, 329, 330, 332, 334, 336, 750, 751, 257, 258, 259, 261, 262, 263, 210,
    1102, 346, 709, 18, 228, 1269, 320, 862, 749, 337, 132, 138, 142, 179, 1352, 1245, 1348, 1349,
    1350, 1353, 1354, 1359, 1360, 1459, 1460, 210, 1417, 1463
  };
  public final int[] bankerIds = {95, 224, 268, 540, 617, 792};

  final int[] closedObjectDoorIds = {
    57, 60, 64, 93, 94, 137, 138, 142, 180, 252, 253, 254, 256, 305, 311, 319, 346, 347, 356, 358,
    371, 392, 443, 457, 480, 504, 508, 513, 563, 577, 611, 624, 626, 660, 702, 703, 704, 712, 722,
    723, 916, 926, 932, 988, 989, 1019, 1020, 1068, 1079, 1089, 1140, 1165, 465, 471, 472, 486, 583,
    869, 958, 1080, 1160
  };
  final int[] closedWallDoorIds = {
    2, 8, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 35, 36, 37, 38, 39, 40, 43,
    44, 45, 47, 48, 49, 50, 51, 52, 53, 54, 55, 57, 58, 59, 60, 61, 63, 64, 65, 66, 67, 68, 69, 70,
    71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 87, 88, 89, 90, 91, 92, 93, 94, 95,
    96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 112, 113, 114, 115, 116,
    117, 120, 121, 122, 123, 124, 125, 128, 129, 130, 131, 132, 133, 134, 135, 136, 138, 139, 140,
    141, 142, 143, 145, 146, 147, 148, 149, 150, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161,
    162, 165, 173, 176, 177, 179, 180, 187, 188, 189, 190, 191, 192, 194, 196, 197, 198, 204, 209,
    210, 211, 212, 213
  };

  private final int GAME_TICK = 640;

  private boolean showStatus = true;
  private boolean showCoords = true;
  private boolean showXp = true;
  private boolean showBotPaint = true;
  private boolean drawing = true;
  private boolean needToMove = false;
  private boolean shouldSleep = false;
  private long auctionTimeout = -1;
  public static boolean temporaryToggleSideMenu = false;
  public static final int CTRL_DOWN_MASK = 1 << 7;
  public static final int SHIFT_DOWN_MASK = 1 << 6;
  static final int JDK_1_3_MODIFIERS = SHIFT_DOWN_MASK - 1;

  public Controller(Reflector _reflector, OpenRSC _client, mudclient _mud) {
    reflector = _reflector;
    client = _client;
    mud = _mud;
  }

  /** @return Whether or not a script is currently running. */
  public boolean isRunning() {
    return Main.isRunning();
  }

  /** Stops the currently running script. */
  public void stop() {
    Main.setRunning(false);
    this.stopBatching();
  }

  /** @param ms Sleeps for the specified amount of milliseconds. */
  public void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Suspends the current thread's execution until the provided condition evaluates to true or a 20
   * second timeout is reached
   *
   * @param condition A Supplier condition that must be met to resume execution.
   * @return true if the condition was met before the timeout, false if the timeout was reached.
   */
  public boolean sleepUntil(java.util.function.Supplier<Boolean> condition) {
    return sleepUntil(condition, 20_000);
  }

  /**
   * Suspends the current thread's execution until the provided condition evaluates to true.
   *
   * @param condition A Supplier condition that must be met to resume execution.
   * @param timeout milliseconds before just returning, even if condition is not yet true
   * @return true if the condition was met before the timeout, false if the timeout was reached.
   */
  public boolean sleepUntil(java.util.function.Supplier<Boolean> condition, int timeout) {
    final long pollInterval = 250; // Check condition every 250 milliseconds

    long startTime = System.currentTimeMillis();
    while (!condition.get() && isRunning()) {
      if (System.currentTimeMillis() - startTime > timeout) {
        return false; // Timeout reached, condition not met
      }
      try {
        Thread.sleep(pollInterval); // Wait a bit before checking the condition again
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // Restore the interrupted status
        return false; // Interrupted during sleep
      }
    }
    return true; // Condition met
  }

  /**
   * Suspends the current thread's execution until the player gains exp
   *
   * @return true if the condition was met before the timeout, false if the timeout was reached.
   */
  public boolean sleepUntilGainedXp() {
    final long timeout = 10000; // 10 seconds timeout for the condition to become true
    final long pollInterval = 250; // Check condition every 250 milliseconds

    long startTime = System.currentTimeMillis();
    long startXp = getTotalXp();
    setStatus("Sleeping until xp drop (cur xp: " + getTotalXp() + ")");
    while (startXp == getTotalXp()) {
      if (System.currentTimeMillis() - startTime > timeout) {
        return false; // Timeout reached, condition not met
      }
      try {
        Thread.sleep(pollInterval); // Wait a bit before checking the condition again
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // Restore the interrupted status
        return false; // Interrupted during sleep
      }
    }
    return true; // We gained xp
  }

  /**
   * Returns the total XP by summing up the player experience for each stat.
   *
   * @return the total XP as a long value
   */
  public long getTotalXp() {
    long result = 0;

    for (int statIndex = 0; statIndex < getStatCount(); statIndex++) {
      result += getPlayerExperience(statIndex);
    }

    return result;
  }

  /**
   * Whether or not the client is loaded.
   *
   * @return boolean
   */
  public boolean isLoaded() {
    return (int) reflector.getObjectMember(mud, "controlLoginStatus2") == 1;
  }

  /**
   * Whether or not the player is currently logged in.
   *
   * @return boolean
   */
  public boolean isLoggedIn() {
    GameMode currentViewMode = (GameMode) reflector.getObjectMember(mud, "currentViewMode");
    return currentViewMode == GameMode.GAME;
  }

  /**
   * Types a single key of the specified char.
   *
   * @param key character
   */
  public void typeKey(char key) {
    client.keyPressed(new KeyEvent(client, 1, 20, 1, 10, key));
  }

  /** @param rstext Sends the specified message via chat. You may use @col@ colors here. */
  public void chatMessage(String rstext) {
    for (char c : rstext.toCharArray()) {
      typeKey(c);
    }
    typeKey('\n');
  }

  /** Pastes the contents of the clipboard to the dialog box. You may use @col@ colors here. */
  public void paste() {
    if (!isLoggedIn()) {
      return;
    }

    final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    final Transferable t = clipboard.getContents(null);
    final DataFlavor f = DataFlavor.stringFlavor;

    if (!t.isDataFlavorSupported(f)) {
      return;
    }

    try {
      for (final char textData : ((String) t.getTransferData(f)).toCharArray()) {
        String rstext = String.valueOf(textData);
        for (char c : rstext.toCharArray()) {
          typeKey(c);
        }
      }
    } catch (final UnsupportedFlavorException | IOException e) {
      e.printStackTrace();
    }
  }

  /** @param mode Sets the fight mode. */
  public void setFightMode(int mode) {
    mud.setCombatStyle(mode);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(29);
    mud.packetHandler.getClientStream().bufferBits.putByte(mode);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Retrieves the current fight mode set.
   *
   * @return int -- [0, 3]
   */
  public int getFightMode() {
    return (int) reflector.getObjectMember(mud, "combatStyle");
  }

  /**
   * Whether or not the specified item id is in the inventory.
   *
   * @param itemId int
   * @return boolean
   */
  public boolean isItemInInventory(int itemId) {
    return mud.getInventoryCount(itemId) > 0;
  }

  /**
   * Retrieves the itemId of the item at the specified `slotIndex`.
   *
   * @param slotIndex int
   * @return itemId -- returns -1 if no item in slot.
   */
  public int getInventorySlotItemId(int slotIndex) {
    Item slot = mud.getInventory()[slotIndex];

    if (slot == null) return -1;

    if (slot.getItemDef() == null) return -1;

    return slot.getItemDef().id;
  }

  /**
   * Retrieves the number of items currently in the inventory.
   *
   * @return int
   */
  public int getInventoryItemCount() {
    return mud.getInventoryItemCount();
  }

  /**
   * Retrieves the count of the specified item in the inventory.
   *
   * @param itemId int
   * @return int
   */
  public int getInventoryItemCount(int itemId) {
    return mud.getInventoryCount(itemId);
  }

  /**
   * Retrieve a list of items on the ground near the player. Duplicate items on the same tile will
   * be added to the list once, with the actual amount available from the getAmount() method.
   *
   * @return guaranteed to not be null.
   */
  public List<GroundItemDef> getGroundItemsStacked() {
    int[] groundItemIDs = this.getGroundItems();
    int[] groundItemsX = this.getGroundItemsX();
    int[] groundItemsZ = this.getGroundItemsY();
    boolean[] groundItemsNoted = this.getGroundItemsNoted();

    int groundItemsCount = this.getGroundItemsCount();

    List<GroundItemDef> _list = new ArrayList<>();

    for (int i = 0; i < groundItemsCount; i++) {
      int groundItemID = groundItemIDs[i];
      int groundItemXI = groundItemsX[i];
      int groundItemZI = groundItemsZ[i];
      boolean groundItemNoted = groundItemsNoted[i];

      ItemDef itemDef = EntityHandler.getItemDef(groundItemID, groundItemNoted);
      GroundItemDef groundItemDef = new GroundItemDef(itemDef);
      groundItemDef.setX(groundItemXI);
      groundItemDef.setZ(groundItemZI);
      groundItemDef.setAmount(
          this.getGroundItemAmount(
              groundItemDef.getID(), groundItemDef.getX(), groundItemDef.getZ()));
      groundItemDef.setDistance(
          this.getDistanceFromLocalPlayer(groundItemDef.getX(), groundItemDef.getZ()));

      boolean found = false;

      for (GroundItemDef _groundItemDef : _list) {
        if (_groundItemDef.getID() == groundItemDef.getID()
            && _groundItemDef.getX() == groundItemDef.getX()
            && _groundItemDef.getZ() == groundItemDef.getZ()) {
          found = true;
          break;
        }
      }

      if (!found) {
        _list.add(groundItemDef);
      }
    }

    return _list;
  }

  /**
   * Retrieves all itemIds of items on the ground.
   *
   * @return int[] -- no guarantee on nullability.
   */
  public int[] getGroundItems() {
    return (int[]) reflector.getObjectMember(mud, "groundItemID");
  }

  /**
   * Retrieves the count of different item ids on the ground.
   *
   * @return int
   */
  public int getGroundItemsCount() {
    return (int) reflector.getObjectMember(mud, "groundItemCount");
  }

  /**
   * Retrieves an array of X coordinates of ground items.
   *
   * @return int[] -- no guarantee on nullability.
   */
  public int[] getGroundItemsX() {
    int[] xs = (int[]) reflector.getObjectMember(mud, "groundItemX");
    int[] tmp = new int[xs.length];

    for (int i = 0; i < xs.length; i++) tmp[i] = this.offsetX(xs[i]);

    return tmp;
  }

  /**
   * Retrieves an array of Y coordinates of ground items.
   *
   * @return int[] -- no guarantee on nullability.
   */
  public int[] getGroundItemsY() {
    int[] ys = (int[]) reflector.getObjectMember(mud, "groundItemZ");
    int[] tmp = new int[ys.length];

    for (int i = 0; i < ys.length; i++) tmp[i] = this.offsetZ(ys[i]);

    return tmp;
  }

  /**
   * Retrieves an array of whether or not the ground item is noted.
   *
   * @return boolean[] -- no guarantee on nullability.
   */
  public boolean[] getGroundItemsNoted() {
    return (boolean[]) reflector.getObjectMember(mud, "groundItemNoted");
  }

  /**
   * Retrieves the amount of the item id on the ground at the specified coordinates.
   *
   * @param itemId int
   * @param x int
   * @param y int
   * @return int -- always returns 0 or greater.
   */
  public int getGroundItemAmount(int itemId, int x, int y) {
    int groundItemCount = this.getGroundItemsCount();
    int[] groundItemIds = this.getGroundItems();
    int[] groundItemsX = this.getGroundItemsX();
    int[] groundItemsZ = this.getGroundItemsY();

    int groundItemAmount = 0;

    for (int i = 0; i < groundItemCount; i++) {
      int groundItemId = groundItemIds[i];
      int groundItemX = groundItemsX[i];
      int groundItemZ = groundItemsZ[i];

      if (groundItemId == itemId && groundItemX == x && groundItemZ == y) {
        groundItemAmount++;
      }
    }

    return groundItemAmount;
  }

  /**
   * Retrieves the ORSCharacter of the local player.
   *
   * @return ORSCharacter -- no guarantee on nullability.
   */
  public ORSCharacter getPlayer() {
    return mud.getLocalPlayer();
  }

  /**
   * Retrieves the ORSCharacter of the specified player server index.
   *
   * @param serverIndex int
   * @return ORSCharacter -- no guarantee on nullability.
   */
  public ORSCharacter getPlayer(int serverIndex) {
    if (serverIndex < 0) return null;

    return mud.getPlayer(serverIndex);
  }

  /**
   * Retrieves a list of nearby players.
   *
   * @return guaranteed to not be null.
   */
  public List<ORSCharacter> getPlayers() {

    List<ORSCharacter> _list = new ArrayList<>();

    ORSCharacter[] players = (ORSCharacter[]) this.getMudClientValue("players");
    int playerCount = this.getPlayerCount();

    _list.addAll(Arrays.asList(players).subList(0, playerCount));

    return _list;
  }

  /**
   * Retrieves the number of nearby players.
   *
   * @return int
   */
  public int getPlayerCount() {
    return (int) reflector.getObjectMember(mud, "playerCount");
  }

  /**
   * Whether or not the player is currently walking.
   *
   * @return boolean
   */
  public boolean isCurrentlyWalking() {
    int x = mud.getLocalPlayer().currentX;
    int z = mud.getLocalPlayer().currentZ;

    sleep(50);

    x -= mud.getLocalPlayer().currentX;
    z -= mud.getLocalPlayer().currentZ;

    return x != 0 || z != 0;
  }

  /**
   * Sleeps until the player starts walking
   *
   * <p>Good to combine with sleepUntilNotMoving, so you can wait for the player to start responding
   * to your action, then wait for them to finish
   *
   * @param timeout Max ms to wait until moving
   */
  public void sleepUntilMoving(long timeout) {
    long start = System.currentTimeMillis();
    while (Main.isRunning()
        && !isCurrentlyWalking()
        && start + timeout > System.currentTimeMillis()) {}
  }

  /**
   * Sleeps until the player is no longer walking
   *
   * <p>Good to combine with sleepUntilMoving, so you can wait for the player to start responding to
   * your action, then wait for them to finish
   *
   * @param timeout Max ms to wait to stop moving
   */
  public void sleepUntilNotMoving(long timeout) {
    long start = System.currentTimeMillis();
    while (Main.isRunning()
        && isCurrentlyWalking()
        && start + timeout > System.currentTimeMillis()) {}
  }

  /**
   * Determines if the player is currently walking.
   *
   * @param serverIndex the index of the player on the server
   * @return true if the player is currently walking, false otherwise
   */
  public boolean isPlayerCurrentlyWalking(int serverIndex) {
    int x = this.getPlayer(serverIndex).currentX;
    int z = this.getPlayer(serverIndex).currentZ;

    sleep(50);

    x -= this.getPlayer(serverIndex).currentX;
    z -= this.getPlayer(serverIndex).currentZ;

    return x != 0 || z != 0;
  }

  /**
   * Checks if an NPC is currently walking.
   *
   * @param serverIndex the server index of the NPC
   * @return true if the NPC is currently walking, false otherwise
   */
  public boolean isNpcCurrentlyWalking(int serverIndex) {
    int x = this.getNpc(serverIndex).currentX;
    int z = this.getNpc(serverIndex).currentZ;

    sleep(50);

    x -= this.getNpc(serverIndex).currentX;
    z -= this.getNpc(serverIndex).currentZ;

    return x != 0 || z != 0;
  }

  /**
   * Retrieves the distance of the coordinates from the player.
   *
   * @param coordX int
   * @param coordY int
   * @return int
   */
  public int getDistanceFromLocalPlayer(int coordX, int coordY) {
    int localCoordX = this.currentX();
    int localCoordY = this.currentY();

    return this.distance(localCoordX, localCoordY, coordX, coordY);
  }

  /**
   * Retrieves the current X coordinates of the player. <br>
   * This occasionally returns incorrect values while Underground
   *
   * @return int
   */
  public int currentX() {
    int getLocalPlayerX = mud.getLocalPlayerX();
    int getMidRegionBaseX = mud.getMidRegionBaseX();

    return getLocalPlayerX + getMidRegionBaseX;
  }

  /**
   * Retrieves the current Y coordinates of the player. <br>
   * This occasionally returns incorrect values while Underground
   *
   * @return int
   */
  public int currentY() {
    int getLocalPlayerZ = mud.getLocalPlayerZ();
    int getMidRegionBaseZ = mud.getMidRegionBaseZ();

    return getLocalPlayerZ + getMidRegionBaseZ;
  }

  /**
   * Walks to the specified tile, does not return until at tile, in combat, or a long timeout is
   * reached
   *
   * @param x int
   * @param y int
   */
  public void walkTo(int x, int y) {
    walkTo(x, y, 0, true, true);
  }

  /**
   * Walks to the specified tile, does not return until at tile or within tile radius, in combat, or
   * a long timeout is reached. Note: when walking to the same tile as current tile the walkTo will
   * not be called. If walking to same tile to leaving combat, ensure leaveCombat boolean is true.
   *
   * @param x int
   * @param y int
   * @param radius int
   * @param leaveCombat boolean - true if bot should keep trying walkTo while in combat
   * @param forced boolean - true if attempt to walkTo should be made reguardless of conditions
   */
  public void walkTo(int x, int y, int radius, boolean forced, boolean leaveCombat) {
    if (x < 0 || y < 0) return;
    if (currentX() == 0 && currentY() == 0) return; // Not logged in yet

    Main.logMethod("WalkTo", x, y, radius);

    if (forced) {
      walkToActionSource(
          mud,
          mud.getLocalPlayerX(),
          mud.getLocalPlayerZ(),
          x - mud.getMidRegionBaseX(),
          y - mud.getMidRegionBaseZ(),
          false);
      sleep(640);
    }

    int timeout = 60_000;
    long starttime = System.currentTimeMillis();
    while ((((currentX() < x - radius)
                || (currentX() > x + radius)
                || (currentY() < y - radius)
                || (currentY() > y + radius))
            || leaveCombat && isInCombat())
        && Main.isRunning()
        && System.currentTimeMillis() < starttime + timeout) {

      int fudgeFactor = ThreadLocalRandom.current().nextInt(-radius, radius + 1);

      // System.out.println(
      // "Tryng to walk, time remaining: "
      // + (timeout - (System.currentTimeMillis() - starttime))
      // + " ms");
      walkToActionSource(
          mud,
          mud.getLocalPlayerX(),
          mud.getLocalPlayerZ(),
          x - mud.getMidRegionBaseX() + fudgeFactor,
          y - mud.getMidRegionBaseZ() + fudgeFactor,
          false);

      // Smart sleeping before clicking again
      for (int i = 0; i < 20_000; i += 1000) {
        if (distanceTo(x, y) <= 5 || isInCombat()) break;
        sleep(1000);
      }
      sleep(1280);
    }

    if (System.currentTimeMillis() >= starttime + timeout) {
      Main.logMethod(
          "WalkTo",
          "FAILED to walk from ",
          currentX(),
          currentY(),
          " to ",
          x,
          y,
          " with a radius of ",
          radius,
          ". We timed out waiting ",
          timeout,
          " ms!");
    }
  }

  /**
   * Walks to the specified tile, non-blocking.
   *
   * @param x int
   * @param y int
   * @param radius int
   */
  public void walkToAsync(int x, int y, int radius) { // offset applied
    if (x < 0 || y < 0) return;

    Main.logMethod("WalkToAsync", x, y, radius);

    int fudgeFactor = ThreadLocalRandom.current().nextInt(-radius, radius + 1);

    walkToActionSource(
        mud,
        mud.getLocalPlayerX(),
        mud.getLocalPlayerZ(),
        x - mud.getMidRegionBaseX() + fudgeFactor,
        y - mud.getMidRegionBaseZ() + fudgeFactor,
        false);
  }

  WebWalker webWalker = null;

  /**
   * Smartly takes the next step towards any location. Handles obstacles, ladders, gates, doors,
   * etc.
   *
   * <p>It is a _bug_ if it cannot walk from any location to any location!
   *
   * <p>If it's not getting to your desired location, please update the graph.txt using the GUI
   * program here - https://github.com/dginovker/Runescape-Classic-Webwalker
   *
   * <p>If this function cannot walk to a certain location, please use the above link to add
   * support!
   *
   * <p>Note - If you have enough coins in your inventory, this function may use them for boat/gate
   * fees! Note - This might not take the most efficient path, but it will be reasonably efficient
   *
   * @param x Destination x
   * @param y Desination y
   * @return Whether or not we successfully walked towards the destination
   */
  public boolean walkTowards(int x, int y) {
    setStatus("Walking to " + x + "," + y);
    if (webWalker == null) webWalker = new WebWalker(new WebwalkGraph("assets/map/graph.txt"));
    boolean success = webWalker.webwalkTowards(currentX(), currentY(), new int[][] {{x, y}});
    setStatus("Done taking step towards " + x + "," + y);
    return success;
  }

  /**
   * Smartly takes the next step towards the nearest bank. It considers obstacles, ladders, gates,
   * doors, etc., and aims to find a path using the web walking algorithm.
   *
   * <p>If unable to reach any bank, it suggests updating the graph with the GUI tool available at
   * https://github.com/dginovker/Runescape-Classic-Webwalker.
   *
   * <p>Note: If sufficient coins are available, it may use them for boat/gate fees. While not
   * guaranteed to take the most efficient path, it seeks to be reasonably efficient.
   *
   * @return Whether or not the step towards the nearest bank was successfully taken.
   */
  public boolean walkTowardsBank() {
    int[][] bankLocations = {
      {220, 635}, {150, 504}, {103, 511}, {220, 365}, {216, 450}, {283, 569},
      {503, 452}, {582, 576}, {566, 605}, {588, 754}, {129, 3543}, {440, 495},
      {327, 552}, {89, 694}
    };

    setStatus("Walking to the nearest bank");
    if (webWalker == null) webWalker = new WebWalker(new WebwalkGraph("assets/map/graph.txt"));
    boolean success = webWalker.webwalkTowards(currentX(), currentY(), bankLocations);
    setStatus(
        success ? "Walking towards the nearest bank" : "Failed to find a path to the nearest bank");
    return success;
  }

  /**
   * Whether or not the specified tile has an object at it.
   *
   * @param x int
   * @param y int
   * @return boolean
   */
  public boolean isTileEmpty(int x, int y) {
    int count = getObjectsCount();
    int[] xs = getObjectsX();
    int[] zs = getObjectsZ();

    for (int i = 0; i < count; i++) {
      int _x = offsetX(xs[i]);
      int _z = offsetZ(zs[i]);

      if (x == _x && y == _z) return false;
    }

    return true;
  }

  /**
   * Retrieves the coordinates of the nearest specified object ID, if nearby.
   *
   * @param objectId The ID of the object to find.
   * @return int[] The coordinates [x, y] of the nearest object, or null if no such object is
   *     nearby.
   */
  public int[] getNearestObjectById(int objectId) {
    Main.logMethod("getNearestObjectById", objectId);
    int[] closestCoords = null;
    int closestDistance = 99999;

    for (int[] objCoords : getObjectsById(objectId)) {
      int dist = distance(this.currentX(), this.currentY(), objCoords[0], objCoords[1]);
      if (dist < closestDistance) {
        closestDistance = dist;
        closestCoords = objCoords;
      }
    }

    return closestCoords; // Returns null if no object is nearby
  }

  /**
   * Retrieves the coordinates of the nearest specified object ID, if nearby and reachable.
   *
   * @param objectId The ID of the object to find.
   * @param includeTileEdges -- whether or not the edges of the tile are permitted. Such as picking
   *     up an item on a table -- you can't walk on top of the table, but you can reach the edges.
   * @return int[] The coordinates [x, y] of the nearest reachable object, or null if no such object
   *     is nearby and reachable.
   */
  public int[] getNearestReachableObjectById(int objectId, boolean includeTileEdges) {
    Main.logMethod("getNearestReachableObjectById", objectId);
    int[] closestCoords = null;
    int closestDistance = 99999;

    for (int[] objCoords : getObjectsById(objectId)) {
      if (isReachable(objCoords[0], objCoords[1], includeTileEdges)) {
        int dist = distance(this.currentX(), this.currentY(), objCoords[0], objCoords[1]);
        if (dist < closestDistance) {
          closestDistance = dist;
          closestCoords = objCoords;
        }
      }
    }

    return closestCoords; // Returns null if no reachable object is nearby
  }

  /**
   * Retrieves the coordinates of the nearest specified objects by their IDs, if any are nearby and
   * reachable.
   *
   * @param objectIds The array of IDs of the objects to find.
   * @param includeTileEdges -- whether or not the edges of the tile are permitted.
   * @return int[] The coordinates [x, y] of the nearest reachable object among the given IDs, or
   *     null if no such objects are nearby and reachable.
   */
  public int[] getNearestReachableObjectByIds(int[] objectIds, boolean includeTileEdges) {
    Main.logMethod("getNearestReachableObjectByIds", objectIds);
    int[] closestCoords = null;
    int closestDistance = 99999;

    for (int objectId : objectIds) {
      for (int[] objCoords : getObjectsById(objectId)) {
        if (isReachable(objCoords[0], objCoords[1], includeTileEdges)) {
          int dist = distance(this.currentX(), this.currentY(), objCoords[0], objCoords[1]);
          if (dist < closestDistance) {
            closestDistance = dist;
            closestCoords = objCoords;
          }
        }
      }
    }

    return closestCoords; // Returns null if no reachable objects are nearby among the given IDs
  }

  /**
   * Retrieves all the coordinates of the nearby object ID
   *
   * <p>This method returns an array of coordinate pairs for each instance of the object with the
   * given ID that is nearby.
   *
   * @param objectId The ID of the objects to retrieve.
   * @return int[][] An array of [x, y] coordinates for each object found. Returns an empty array if
   *     no objects are found.
   */
  public int[][] getObjectsById(int objectId) {
    Main.logMethod("getObjectsById", objectId);
    int count = getObjectsCount();
    int[] xs = getObjectsX();
    int[] zs = getObjectsZ();
    int[] ids = getObjectsIds();
    List<int[]> pointsList = new ArrayList<>(); // Use dynamic list to collect valid coordinates

    for (int i = 0; i < count; i++) {
      if (ids[i] == objectId) {
        int x = offsetX(xs[i]);
        int z = offsetZ(zs[i]);
        pointsList.add(new int[] {x, z});
      }
    }

    // Convert list back to array
    int[][] points = new int[pointsList.size()][2];
    points = pointsList.toArray(points);

    return points;
  }

  /**
   * Finds the nearest object coordinates based on the given object IDs.
   *
   * @param objectIds an array of object IDs
   * @return an array containing the coordinates of the nearest object
   */
  public int[] getNearestObjectByIds(int[] objectIds) {
    int distance = Integer.MAX_VALUE;
    int[] result = null;

    for (int id : objectIds) {
      int[] coord = getNearestObjectById(id);

      if (coord != null) {
        int tmpDistance = this.distance(currentX(), currentY(), coord[0], coord[1]);

        if (tmpDistance < distance) {
          distance = tmpDistance;
          result = coord;
        }
      }
    }

    return result;
  }

  /**
   * Performs the primary command option on the nearest reachable scenery ID
   *
   * @param id scenery ID
   * @return boolean -- returns true if we interacted successfully
   */
  public boolean atObject(SceneryId id) {
    setStatus("Interacting " + id.name() + " " + id.getId());
    Main.logMethod("atObject", id);
    return atObject(id.getId());
  }

  /**
   * Performs the primary command option on the nearest reachable objectId
   *
   * @param id int
   * @return boolean -- returns true if we interacted successfully
   */
  public boolean atObject(int id) {
    Main.logMethod("atObject", id);
    int[] coords = getNearestObjectById(id);
    if (coords == null) return false;
    return atObject(coords[0], coords[1]);
  }

  /**
   * Performs the second command option on the nearest reachable objectId
   *
   * @param id int
   * @return boolean -- returns true if we interacted successfully
   */
  public boolean atObject2(int id) {
    Main.logMethod("atObject", id);
    int[] coords = getNearestObjectById(id);
    if (coords == null) return false;
    return atObject2(coords[0], coords[1]);
  }

  /**
   * Performs the primary command option on the specified object id at the specified coordinates.
   *
   * @param x int
   * @param y int
   * @return boolean -- returns false if no object at those coordinates.
   */
  public boolean atObject(int x, int y) {
    Main.logMethod("atObject", x, y);
    int count = getObjectsCount();
    int[] xs = getObjectsX();
    int[] zs = getObjectsZ();
    int[] ids = getObjectsIds();

    for (int i = 0; i < count; i++) {
      if (offsetX(xs[i]) == x && offsetZ(zs[i]) == y) {
        objectAt(
            offsetX(xs[i]),
            offsetZ(zs[i]),
            this.getDirection(offsetX(xs[i]), offsetZ(zs[i])),
            ids[i]);
        return true;
      }
    }

    return false;
  }

  /**
   * Performs the 2nd command option on the specified object id at the specified coordinates.
   *
   * @param x int
   * @param y int
   * @return boolean -- returns false if no object at those coordinates.
   */
  public boolean atObject2(int x, int y) {
    Main.logMethod("atObject2", x, y);
    int count = getObjectsCount();
    int[] xs = getObjectsX();
    int[] zs = getObjectsZ();
    int[] ids = getObjectsIds();

    for (int i = 0; i < count; i++) {
      if (offsetX(xs[i]) == x && offsetZ(zs[i]) == y) {
        objectAt2(
            offsetX(xs[i]),
            offsetZ(zs[i]),
            this.getDirection(offsetX(xs[i]), offsetZ(zs[i])),
            ids[i]);
        return true;
      }
    }

    return false;
  }

  /**
   * Whether or not you are within 1 tile of the specified coordinates.
   *
   * @param x int
   * @param y int
   * @return boolean
   */
  public boolean isCloseToCoord(int x, int y) {
    System.out.println(currentX() + ", " + currentY() + ", " + x + ", " + y);
    System.out.println(distance(currentX(), currentY(), x, y));
    return this.distance(currentX(), currentY(), x, y) <= 1;
  }

  /**
   * Interacts with the object (first option) at the given coordinates with the given id and
   * direction Private method called by atObject()
   *
   * @param x x coordinate of the object
   * @param z z (y) coordinate of the object
   * @param dir direction of the object
   * @param objectId id of the object
   */
  private void objectAt(int x, int z, int dir, int objectId) {
    if (x < 0 || z < 0) return;

    reflector.mudInvoker(
        mud,
        "walkToObject",
        x - mud.getMidRegionBaseX(),
        z - mud.getMidRegionBaseZ(),
        dir,
        5126,
        objectId);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(136);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(z);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Interacts with the object (2nd option) at the given coordinates with the given id and direction
   * Private method called by atObject()
   *
   * @param x x coordinate of the object
   * @param z z (y) coordinate of the object
   * @param dir direction of the object
   * @param objectId id of the object
   */
  private void objectAt2(int x, int z, int dir, int objectId) {
    if (x < 0 || z < 0) return;

    reflector.mudInvoker(
        mud,
        "walkToObject",
        x - mud.getMidRegionBaseX(),
        z - mud.getMidRegionBaseZ(),
        dir,
        5126,
        objectId);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(79);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(z);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Retrieves a list of nearby objects.
   *
   * @return guaranteed to not be null.
   */
  public List<GameObjectDef> getObjects() {
    int[] gameObjectInstanceIDs = getObjectsIds();

    List<Integer> _list = new ArrayList<>();
    int gameObjectInstanceCount = this.getObjectsCount();

    for (int i = 0; i < gameObjectInstanceCount; i++) {
      int gameObjectInstanceID = gameObjectInstanceIDs[i];
      _list.add(gameObjectInstanceID);
    }

    return _list.stream().map(EntityHandler::getObjectDef).collect(Collectors.toList());
  }

  /**
   * Retrieves the count of objects nearby.
   *
   * @return int
   */
  public int getObjectsCount() {
    return (int) reflector.getObjectMember(mud, "gameObjectInstanceCount");
  }

  /**
   * Retrieves the IDs of objects nearby.
   *
   * @return int[] -- no guarantee on nullability.
   */
  public int[] getObjectsIds() {
    return (int[]) reflector.getObjectMember(mud, "gameObjectInstanceID");
  }

  /**
   * Retrieves an array of all of the X coordinates of nearby objects.
   *
   * @return int[] -- no guarantee on nullability.
   */
  public int[] getObjectsX() {
    return (int[]) reflector.getObjectMember(mud, "gameObjectInstanceX");
  }

  /**
   * Retrieves an array of all of the Z coordinates of nearby objects.
   *
   * @return int[] -- no guarantee on nullability.
   */
  public int[] getObjectsZ() {
    return (int[]) reflector.getObjectMember(mud, "gameObjectInstanceZ");
  }

  /**
   * Retrieves a list of NPCs nearby.
   *
   * @return guaranteed to not be null.
   */
  public List<ORSCharacter> getNpcs() {
    List<ORSCharacter> _list = new ArrayList<>();

    ORSCharacter[] npcs = (ORSCharacter[]) this.getMudClientValue("npcs");
    int npcCount = this.getNpcCount();

    _list.addAll(Arrays.asList(npcs).subList(0, npcCount));

    return _list;
  }

  /**
   * Retrieves ORSCharacter[] array of NPCs nearby.
   *
   * @return guaranteed to not be null.
   */
  public ORSCharacter[] getNpcsAsArray() { // tested working
    ORSCharacter[] npcs = (ORSCharacter[]) this.getMudClientValue("npcs");
    int npcCount = this.getNpcCount();

    ORSCharacter[] result = new ORSCharacter[npcCount];
    System.arraycopy(npcs, 0, result, 0, npcCount);
    return result;
  }

  /**
   * Retrieves int[] array of NPCs nearby.
   *
   * @return guaranteed to not be null.
   */
  public int[] getNpcsAsIntArray() { // tested working
    ORSCharacter[] orscNpcs = getNpcsAsArray();
    int[] npcIds = new int[orscNpcs.length];

    for (int i = 0; i < npcIds.length; i++) {
      npcIds[i] = orscNpcs[i].npcId;
    }
    return npcIds;
  }

  /**
   * Retrieves the count of NPCs nearby.
   *
   * @return int
   */
  public int getNpcCount() {
    return mud.getNpcCount();
  }

  /**
   * Retrieves a list of wall objects nearby.
   *
   * @return guaranteed to not be null.
   */
  public List<DoorDef> getWallObjects() {
    List<Integer> _list = new ArrayList<>();

    int[] wallObjectInstanceIDs = getWallObjectIds();
    int wallObjectInstanceCount = this.getWallObjectsCount();

    for (int i = 0; i < wallObjectInstanceCount; i++) {
      int wallObjectInstanceID = wallObjectInstanceIDs[i];
      _list.add(wallObjectInstanceID);
    }

    return _list.stream().map(EntityHandler::getDoorDef).collect(Collectors.toList());
  }

  /**
   * Retrieves an array of all wall object IDs nearby.
   *
   * @return int[] -- no guarantee on nullability
   */
  public int[] getWallObjectIds() {
    return (int[]) this.getMudClientValue("wallObjectInstanceID");
  }

  /**
   * Retrieves the count of wall objects nearby.
   *
   * @return int
   */
  public int getWallObjectsCount() {
    return (int) reflector.getObjectMember(mud, "wallObjectInstanceCount");
  }

  /**
   * Retrieves an array of all of the X coordinates of nearby wall objects.
   *
   * @return int[] -- no guarantee on nullability.
   */
  public int[] getWallObjectsX() {
    return (int[]) reflector.getObjectMember(mud, "wallObjectInstanceX");
  }

  /**
   * Retrieves an array of all of the Z coordinates of nearby wall objects.
   *
   * @return int[] -- no guarantee on nullability.
   */
  public int[] getWallObjectsZ() {
    return (int[]) reflector.getObjectMember(mud, "wallObjectInstanceZ");
  }

  public int[] getWallObjectsDirections() {
    return (int[]) reflector.getObjectMember(mud, "wallObjectInstanceDir");
  }

  /**
   * Retrieves the coordinates of the specified wall object id, if nearby.
   *
   * @param wallObjectId int
   * @return int[] -- [x, y]. returns null if no wall object nearby.
   */
  public int[] getNearestWallObjectById(int wallObjectId) {
    Main.logMethod("getNearestWallObjectById", wallObjectId);
    int count = this.getWallObjectsCount();
    int[] xs = this.getWallObjectsX();
    int[] zs = this.getWallObjectsZ();
    int[] ids = this.getWallObjectIds();

    int[] closestCoords = {-1, -1};
    int closestDistance = 99999;

    for (int i = 0; i < count; i++) {
      if (ids[i] == wallObjectId) {
        int x = offsetX(xs[i]);
        int z = offsetZ(zs[i]);
        int dist = distance(this.currentX(), this.currentY(), x, z);
        if (dist < closestDistance) {
          closestDistance = dist;
          closestCoords[0] = x;
          closestCoords[1] = z;
        }
      }
    }

    if (closestCoords[0] == -1) return null;

    return closestCoords;
  }

  /**
   * Retrieves the character object of the nearest NPC specified in the list of ids.
   *
   * @param npcIds int
   * @param inCombatAllowed -- whether or not to return NPCs which are currently engaged in combat.
   * @return orsc.ORSCharacter -- returns null if npc not present.
   */
  public ORSCharacter getNearestNpcByIds(int[] npcIds, boolean inCombatAllowed) {
    ORSCharacter npc = null;
    ORSCharacter[] npcs = (ORSCharacter[]) reflector.getObjectMember(mud, "npcs");
    int npcCount = (int) reflector.getObjectMember(mud, "npcCount");

    int botX = mud.localPlayer.currentX;
    int botZ = mud.localPlayer.currentZ;
    int closestDistance = Integer.MAX_VALUE;

    for (int i = 0; i < npcCount; i++) {

      ORSCharacter curNpc = npcs[i];
      for (int npcId : npcIds) {
        if (curNpc.npcId == npcId) {

          if (!inCombatAllowed) {
            if (this.isNpcInCombat(curNpc.serverIndex)) {
              continue;
            }
          }

          int result = distance(curNpc.currentX, curNpc.currentZ, botX, botZ);
          if (result < closestDistance) {
            closestDistance = result;
            npc = curNpc;
          }
        }
      }
    }

    return npc;
  }

  /**
   * Retrieves the character object of the nearest NPC that satisfies the condition
   *
   * @param condition - Condition the NPC must satisfy
   * @return orsc.ORSCharacter -- returns null if npc not present.
   */
  public ORSCharacter getNearestNPCByLambda(java.util.function.Predicate<ORSCharacter> condition) {
    ORSCharacter closestNpc = null;
    int closestDistance = Integer.MAX_VALUE;

    ORSCharacter[] npcs = (ORSCharacter[]) reflector.getObjectMember(mud, "npcs");
    int npcCount = (int) reflector.getObjectMember(mud, "npcCount");
    int botX = mud.localPlayer.currentX;
    int botZ = mud.localPlayer.currentZ;

    for (int i = 0; i < npcCount; i++) {
      ORSCharacter currentNpc = npcs[i];
      if (!condition.test(currentNpc)) continue;
      int distance = distance(currentNpc.currentX, currentNpc.currentZ, botX, botZ);
      if (distance > closestDistance) continue;
      closestDistance = distance;
      closestNpc = currentNpc;
    }

    return closestNpc;
  }

  /**
   * Method to make a 2D array with all [x,y,npcId] positions of the supplied npcIds[] array<br>
   * Other methods only return the closest npc, this will return all of them in a nx3 matrix
   *
   * @param npcIds int[n] of npcIds you would like to search for
   * @param inCombatAllowed boolean if in-combat npcs should be recorded
   * @return int[n][3] 2D array of x,y,npcId values for the supplied npc Ids
   */
  public int[][] getAllNpcsById(int[] npcIds, boolean inCombatAllowed) {
    ORSCharacter[] npcs = (ORSCharacter[]) reflector.getObjectMember(mud, "npcs");
    int[][] result = new int[npcs.length][3]; // length of all (n x 2 matrix)
    int npcCount = (int) reflector.getObjectMember(mud, "npcCount");
    int resultActiveSlot = 0; // the slot of our result array we are iterating through

    for (int i = 0; i < npcCount; i++) {

      ORSCharacter curNpc = npcs[i];
      for (int npcId : npcIds) {
        if (curNpc.npcId == npcId) {

          if (!inCombatAllowed) {
            if (this.isNpcInCombat(curNpc.serverIndex)) {
              continue;
            }
          }
          result[resultActiveSlot][0] = curNpc.currentX;
          result[resultActiveSlot][1] = curNpc.currentZ;
          result[resultActiveSlot][2] = curNpc.npcId;
          resultActiveSlot++;
        }
      }
      int resultLength =
          (resultActiveSlot + 1); // length is 1 more than the index value, so 8 index is 9 values.
      if (result.length > resultLength) {
        // now we need to shrink our array and remove null cells, then return shortened
        // string
        int[][] newResult = new int[resultLength][3];
        for (int j = 0; j < resultLength; j++) {
          newResult[j][0] = result[j][0];
          newResult[j][1] = result[j][1];
          newResult[j][2] = result[j][2];
        }
        return newResult;
      }
    }
    return result;
  }

  /**
   * Retrieves the character object of the nearest npc.
   *
   * @param npcId int
   * @param inCombatAllowed -- whether or not to return NPCs which are currently engaged in combat.
   * @return orsc.ORSCharacter -- returns null if NPC not present.
   */
  public ORSCharacter getNearestNpcById(int npcId, boolean inCombatAllowed) {
    int[] tmp = new int[1];
    tmp[0] = npcId;

    return getNearestNpcByIds(tmp, inCombatAllowed);
  }

  /**
   * Retrieves the coordinates of the specified NPC.
   *
   * @param serverIndex int
   * @return int[] -- [x, y]. Returns [-1, -1] on no NPC present.
   */
  public int[] getNpcCoordsByServerIndex(int serverIndex) {
    ORSCharacter[] npcs = (ORSCharacter[]) reflector.getObjectMember(mud, "npcs");

    for (ORSCharacter npc : npcs) {
      if (npc != null && npc.serverIndex == serverIndex) {
        return new int[] {this.convertX(npc.currentX), this.convertZ(npc.currentZ)};
      }
    }

    // TODO: return null for consistency and update scripts.
    return new int[] {-1, -1};
  }

  /**
   * Creates an NPC character object based on the server index provided.
   *
   * @param serverIndex the server index of the NPC (see get
   * @return the NPC character with the specified server index, or null if not found
   */
  public ORSCharacter getNpc(int serverIndex) {
    ORSCharacter[] npcs = (ORSCharacter[]) reflector.getObjectMember(mud, "npcs");

    for (ORSCharacter npc : npcs) {
      if (npc != null && npc.serverIndex == serverIndex) {
        return npc;
      }
    }

    return null;
  }

  /**
   * Walks to the specified NPC. This function is non-blocking.
   *
   * @param npcServerIndex int
   */
  public void walktoNPCAsync(int npcServerIndex) {
    if (npcServerIndex < 0) return;

    ORSCharacter npc = (ORSCharacter) reflector.mudInvoker(mud, "getServerNPC", npcServerIndex);
    if (npc != null) {
      int npcX = (npc.currentX - 64) / mud.getTileSize();
      int npcZ = (npc.currentZ - 64) / mud.getTileSize();

      walkToActionSource(mud, mud.getLocalPlayerX(), mud.getLocalPlayerZ(), npcX, npcZ, true);
    }
  }

  /**
   * Walks to the specified NPC.
   *
   * @param npcServerIndex int
   * @param radius -- must be 0 or greater.
   */
  public void walktoNPC(int npcServerIndex, int radius) {
    if (npcServerIndex < 0) return;

    ORSCharacter npc = (ORSCharacter) reflector.mudInvoker(mud, "getServerNPC", npcServerIndex);
    if (npc != null) {
      int npcX = (npc.currentX - 64) / mud.getTileSize() + mud.getMidRegionBaseX();
      int npcZ = (npc.currentZ - 64) / mud.getTileSize() + mud.getMidRegionBaseZ();

      walkTo(npcX, npcZ, radius, true, true);
    }
  }

  /**
   * Attacks the specified NPC.
   *
   * @param npcServerIndex int
   */
  public void attackNpc(int npcServerIndex) {
    Main.logMethod("attackNpc", npcServerIndex);

    if (npcServerIndex < 0) return;

    walktoNPCAsync(npcServerIndex);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(190);
    mud.packetHandler.getClientStream().bufferBits.putShort(npcServerIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Casts the specified spell on the specified npc.
   *
   * @param serverIndex int
   * @param spellId int
   */
  public void castSpellOnNpc(int serverIndex, int spellId) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(50);
    mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
    mud.packetHandler.getClientStream().bufferBits.putShort(serverIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Uses the specified item on the specified npc.
   *
   * @param serverIndex int
   * @param itemId int
   */
  public void useItemOnNpc(int serverIndex, int itemId) {
    walktoNPCAsync(serverIndex);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(135);
    mud.packetHandler.getClientStream().bufferBits.putShort(serverIndex);
    mud.packetHandler.getClientStream().bufferBits.putShort(this.getInventoryItemSlotIndex(itemId));
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Uses the specified item slot on the object at the specified coordinates. Note that this uses a
   * slot id, not an item id.
   *
   * <p>This is primarily used to interact with an object, such as using an axe with a tree. For
   * tasks like opening locked doors try using "c.useItemOnWall(int x, int y, int slotIndex)"
   * instead
   *
   * @param x int
   * @param y int
   * @param slotIndex int
   */
  public void useItemSlotOnObject(int x, int y, int slotIndex) {
    reflector.mudInvoker(
        mud,
        "walkToObject",
        x - mud.getMidRegionBaseX(),
        y - mud.getMidRegionBaseZ(),
        4,
        5126,
        this.getObjectAtCoord(x, y));
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(115);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(y);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Uses the specified item id on the object at the specified coordinates.
   *
   * <p>This is primarily used to interact with an object, such as using an axe with a tree. For
   * tasks like opening locked doors try using "c.useItemOnWall(int x, int y, int slotIndex)"
   * instead
   *
   * @param x int
   * @param y int
   * @param itemId int
   */
  public void useItemIdOnObject(int x, int y, int itemId) {
    useItemSlotOnObject(x, y, this.getInventoryItemSlotIndex(itemId));
  }

  /**
   * Uses the item at the specified slot on the wall object at the specified coordinates. Note that
   * this uses a slot id, not item id.
   *
   * @param x int
   * @param y int
   * @param slotIndex int
   */
  public void useItemOnWall(int x, int y, int slotIndex) {
    int direction = getWallObjectDirectionAtCoord(x, y);

    reflector.mudInvoker(
        mud, "walkToWall", this.removeOffsetX(x), this.removeOffsetZ(y), direction);
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(161);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(y);
    mud.packetHandler.getClientStream().bufferBits.putByte(direction);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Thieves the NPC.
   *
   * @param serverIndex int
   */
  public void thieveNpc(int serverIndex) {
    npcCommand1(serverIndex);
  }

  /**
   * Walks to the NPC and select the 1st command option.
   *
   * @param serverIndex int
   */
  public void npcCommand1(int serverIndex) {
    Main.logMethod("npcCommand1", serverIndex);
    walktoNPCAsync(serverIndex);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(202);
    mud.packetHandler.getClientStream().bufferBits.putShort(serverIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Walks to the NPC and select the 2nd command option.
   *
   * @param serverIndex int
   */
  public void npcCommand2(int serverIndex) {
    Main.logMethod("npcCommand2", serverIndex);
    walktoNPCAsync(serverIndex);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(203);
    mud.packetHandler.getClientStream().bufferBits.putShort(serverIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Whether or not the specified npc is in combat.
   *
   * @param serverIndex int
   * @return boolean -- returns true if in combat. Returns false if not in combat, or if server
   *     index not found.
   */
  public boolean isNpcInCombat(int serverIndex) {
    ORSCharacter[] npcs = (ORSCharacter[]) reflector.getObjectMember(mud, "npcs");
    int npcCount = (int) reflector.getObjectMember(mud, "npcCount");

    for (int i = 0; i < npcCount; i++) {
      if (npcs[i].serverIndex == serverIndex) {
        ORSCharacterDirection dir = this.getCharacterDirection(npcs[i]);
        if (dir == ORSCharacterDirection.COMBAT_A || dir == ORSCharacterDirection.COMBAT_B)
          return true;
      }
    }

    return false;
  }

  /**
   * Whether or not the door at the specified coordinates is open.
   *
   * @param x int
   * @param y int
   * @return boolean
   */
  public boolean isDoorOpen(int x, int y) {
    int[] naughtyDoors = new int[] {163, 164, 68, 97, 96, 43, 162, 94};

    int[] ids = getWallObjectIds();
    int[] xs = getWallObjectsX();
    int[] zs = getWallObjectsZ();
    int count = getWallObjectsCount();

    int _x = removeOffsetX(x), _z = removeOffsetZ(y);

    for (int id : naughtyDoors) {
      if (this.getWallObjectIdAtCoord(x, y) == id) return false;
    }

    for (int i = 0; i < count; i++) {
      if (xs[i] == _x && zs[i] == _z) if (ids[i] == 2) return false;
    }

    return true;
  }

  /**
   * Returns an int[][] of closed door coordinates
   *
   * @param radius int -- Radius to check for doors
   * @return int[][] -- Door coordinates
   */
  public int[][] getNearbyClosedDoors(int radius) {
    List<int[]> doors = new ArrayList<int[]>();

    for (int id : this.closedObjectDoorIds) {
      int[] coords = this.getNearestObjectById(id);
      if (coords != null) {
        if (this.distance(currentX(), currentY(), coords[0], coords[1]) <= radius) {
          doors.add(coords);
        }
      }
    }

    for (int id : this.closedWallDoorIds) {
      int[] coords = this.getNearestWallObjectById(id);
      if (coords != null) {
        if (this.distance(currentX(), currentY(), coords[0], coords[1]) <= radius) {
          doors.add(coords);
        }
      }
    }

    int[][] doorArray = new int[doors.size()][2];
    doors.toArray(doorArray);
    return doorArray;
  }

  /**
   * Checks in the radius for a closed door
   *
   * @param radius int -- Radius to check
   * @return boolean -- If a closed door was found
   */
  public boolean isNearbyDoorClosed(int radius) {
    int x = this.currentX();
    int y = this.currentY();

    int objectId = -1;
    int wallObjectId = -1;

    for (int id : this.closedObjectDoorIds) {
      int[] coords = this.getNearestObjectById(id);
      if (coords != null) {
        if (this.distance(x, y, coords[0], coords[1]) <= radius) {
          objectId = id;
          break;
        }
      }
    }

    if (objectId != -1) {
      int[] coords = this.getNearestObjectById(objectId);
      if (coords != null && this.distance(x, y, coords[0], coords[1]) <= radius) {
        return true;
      }
    }

    for (int id : this.closedWallDoorIds) {
      int[] coords = this.getNearestWallObjectById(id);
      if (coords != null) {
        if (this.distance(x, y, coords[0], coords[1]) <= radius) {
          wallObjectId = id;
          break;
        }
      }
    }

    if (wallObjectId != -1) {
      int[] coords = this.getNearestWallObjectById(wallObjectId);
      if (coords != null && this.distance(x, y, coords[0], coords[1]) <= radius) {
        return true;
      }
    }
    return false;
  }

  /**
   * Retrieves the id of the wall object at the specified coordinates.
   *
   * @param x int
   * @param y int
   * @return int -- returns -1 if no wall object present.
   */
  public int getWallObjectIdAtCoord(int x, int y) {
    int _x = removeOffsetX(x);
    int _y = removeOffsetZ(y);

    int[] xs = this.getWallObjectsX();
    int[] ys = this.getWallObjectsZ();
    List<DoorDef> objs = this.getWallObjects();

    for (int i = 0; i < objs.size(); i++) {
      if (xs[i] == _x && ys[i] == _y) return objs.get(i).id;
    }

    return -1;
  }

  /**
   * Returns the direction of the wall object.
   *
   * @param x int
   * @param y int
   * @return int
   */
  public int getWallObjectDirectionAtCoord(int x, int y) {
    int _x = removeOffsetX(x);
    int _y = removeOffsetZ(y);

    int[] xs = this.getWallObjectsX();
    int[] ys = this.getWallObjectsZ();

    for (int i = 0; i < xs.length; i++) {
      if (xs[i] == _x && ys[i] == _y) return this.getWallObjectsDirections()[i];
    }

    return -1;
  }

  /**
   * Opens the door at the specified coordinates. Does nothing if the door is already open.
   *
   * @param x int
   * @param y int
   */
  public void openDoor(int x, int y) {

    if (isDoorOpen(x, y)) {
      System.out.println("door already open");
      return;
    }

    int opcode = 127;
    int direction = getWallObjectDirectionAtCoord(x, y);

    if (this.getWallObjectIdAtCoord(x, y) == 163
        || this.getWallObjectIdAtCoord(x, y) == 164
        || this.getWallObjectIdAtCoord(x, y) == 43
        || ((this.currentX() == 609) && this.currentY() == 1548)) {
      opcode = 14; // we want WALL_COMMAND1 for these IDs
      // height = 1;
    }

    // while(isDoorOpen(x, y) == false && Main.isRunning()) {
    reflector.mudInvoker(
        mud, "walkToWall", this.removeOffsetX(x), this.removeOffsetZ(y), direction);
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(opcode);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(y);
    mud.packetHandler.getClientStream().bufferBits.putByte(direction);
    mud.packetHandler.getClientStream().finishPacket();

    // sleep(GAME_TICK_COUNT);
    // }
  }

  /**
   * Closes the door at the specified coordinates. Does nothing if the door is already closed.
   *
   * @param x int
   * @param y int
   */
  public void closeDoor(int x, int y) {

    if (!isDoorOpen(x, y)) {
      System.out.println("door already closed");
      return;
    }

    while (isDoorOpen(x, y) && Main.isRunning()) {
      reflector.mudInvoker(mud, "walkToWall", this.removeOffsetX(x), this.removeOffsetZ(y), 0);
      while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
      mud.packetHandler.getClientStream().newPacket(127);
      mud.packetHandler.getClientStream().bufferBits.putShort(x);
      mud.packetHandler.getClientStream().bufferBits.putShort(y);
      mud.packetHandler.getClientStream().bufferBits.putByte(0); // direction
      mud.packetHandler.getClientStream().finishPacket();

      sleep(GAME_TICK);
    }
  }

  /**
   * Whether or not the specified item is present at the specified coordinates.
   *
   * @param x int
   * @param y int
   * @param itemId int
   * @return boolean
   */
  public boolean isItemAtCoord(int x, int y, int itemId) {
    int groundItemCount = getGroundItemsCount();
    int[] groundItemID = getGroundItems();
    int[] groundItemX = getGroundItemsX();
    int[] groundItemZ = getGroundItemsY();

    for (int i = 0; i < groundItemCount; i++) {
      if (groundItemID[i] == itemId) {
        if (groundItemX[i] == x && groundItemZ[i] == y) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Retrieves the coordinates of the nearest specified item within the given distance, if on the
   * ground
   *
   * @param itemId int
   * @return int[] -- [x, y]. Returns null if item not found.
   */
  public int[] getNearestItemById(int itemId) {
    return getNearestItemById(itemId, 20000);
  }

  /**
   * Retrieves the coordinates of the specified item, if on the ground.
   *
   * @param itemId int
   * @param maxDistance Maximum distance to bother checking
   * @return int[] -- [x, y]. Returns null if item not found.
   */
  public int[] getNearestItemById(int itemId, int maxDistance) {
    int groundItemCount = getGroundItemsCount();
    int[] groundItemID = getGroundItems();
    int[] groundItemX = getGroundItemsX();
    int[] groundItemZ = getGroundItemsY();

    int botX = currentX();
    int botZ = currentY();
    int closestItemIndex = -1;

    for (int i = 0; i < groundItemCount; i++) {
      if (itemId == groundItemID[i]) {
        int result = distance(groundItemX[i], groundItemZ[i], botX, botZ);
        if (result <= maxDistance) {
          // Main.logMethod("getnearestitem bleh", botX, botZ, groundItemX[i],
          // groundItemZ[i],
          // result, closestDistance);
          maxDistance = result;
          closestItemIndex = i;
        }
      }
    }

    if (closestItemIndex == -1) {
      return null;
    }

    return new int[] {groundItemX[closestItemIndex], groundItemZ[closestItemIndex]};
  }

  /**
   * Retrieves the coordinates of the specified items, if on the ground.
   *
   * @param itemIds int
   * @return int[] -- [x, y, itemId]. Returns null if no items found.
   */
  public int[] getNearestItemByIds(int[] itemIds) {
    for (int itemId : itemIds) {
      int[] result = getNearestItemById(itemId);

      if (result != null) return new int[] {result[0], result[1], itemId};
    }

    return null;
  }

  /**
   * Picks up the item at the specified coordinates.
   *
   * @param x int
   * @param y int
   * @param itemId int
   * @param unused
   * @param async -- whether or not to block when walking to the item. If set to true, it will keep
   *     attempting to walk to the item, until it is close enough to pick it up.
   */
  public void pickupItem(int x, int y, int itemId, boolean unused, boolean async) {
    if (x < 0 || y < 0) return;

    Main.logMethod("pickupItem", x, y, itemId);

    Main.logMethod("pickupItem calling walkTo...", x, y);
    if (async) this.walkToAsync(x, y, 0);
    else this.walkTo(x, y, 0, false, true);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(247);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(y);
    mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Picks up the nearest reachable item with id itemId
   *
   * @param itemId int
   * @return true if the item was found
   */
  public boolean pickupItem(int itemId) {
    int[] item = getNearestItemById(itemId);
    if (item == null) return false;
    pickupItem(item[0], item[1], itemId, false, false);
    return true;
  }

  /**
   * Uses the command option on the specified item id.
   *
   * @param itemId int
   * @return boolean -- returns true on success. returns false if the item is not in the inventory.
   */
  public boolean itemCommand(int itemId) {
    Main.logMethod("itemCommand", itemId);

    int inventoryIndex = getInventoryItemSlotIndex(itemId);

    if (inventoryIndex == -1) return false;

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(90);
    mud.packetHandler.getClientStream().bufferBits.putShort(inventoryIndex);
    mud.packetHandler.getClientStream().bufferBits.putInt(1);
    mud.packetHandler.getClientStream().bufferBits.putByte(0);
    mud.packetHandler.getClientStream().finishPacket();

    return true;
  }

  /**
   * Uses the command option on the item at the specified slot id. Note that this does not use item
   * ids, but slot ids.
   *
   * @param slotIndex int
   */
  public void itemCommandBySlot(int slotIndex) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(90);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex);
    mud.packetHandler.getClientStream().bufferBits.putInt(1);
    mud.packetHandler.getClientStream().bufferBits.putByte(0);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Uses the item at `slot1` on `slot2`. Note that this does not use item ids, but slot ids.
   *
   * @param slotIndex1 int
   * @param slotIndex2 int
   */
  public void useItemOnItemBySlot(int slotIndex1, int slotIndex2) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(91);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex1);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex2);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Retrieves the slot id of the specified item id.
   *
   * @param itemId -- returns -1 if item not in inventory.
   * @return int
   */
  public int getInventoryItemSlotIndex(int itemId) {
    int inventoryItemCount = (int) reflector.getObjectMember(mud, "inventoryItemCount");
    int[] inventoryItemID = this.getInventoryItemIds();
    int slotIndex = -1;

    for (int i = 0; i < inventoryItemCount; i++) {
      if (inventoryItemID[i] == itemId) slotIndex = i;
    }

    return slotIndex;
  }

  /**
   * Drops one the specified item at the specified item slot. Note that this does not use an item
   * id, but a slot index.
   *
   * @param slotIndex int
   */
  public void dropItem(int slotIndex) {
    int inventoryItemID = mud.getInventoryItemID(slotIndex);
    int inventoryItemCount = this.getInventoryItemCount(inventoryItemID);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(246);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex);
    mud.packetHandler.getClientStream().bufferBits.putInt(inventoryItemCount);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Drops the specified item at the specified item slot, of specified amount. Note that this does
   * not use an item id, but a slot index.
   *
   * @param slotIndex int
   * @param amount int
   */
  public void dropItem(int slotIndex, int amount) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(246);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex);
    mud.packetHandler.getClientStream().bufferBits.putInt(amount);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * return a int item id that is equipped in the provided slot index
   *
   * @return int of Item Id in provided slot Index
   */
  public int getEquippedItemId(int slotIndex) {
    int[] result = new int[10];
    int index = 0;
    ItemDef[] equippedItems = this.getMud().equippedItems;
    for (ItemDef item : equippedItems) {
      if (item != null) {
        result[index] = item.id;
        index++;
      }
    }
    return result[slotIndex];
  }

  /**
   * return a list of all item ids that are equipped
   *
   * @return int[] of Item Ids (trimmed)
   */
  public int[] getEquippedItemIds() {
    int[] result = new int[10];
    int index = 0;
    ItemDef[] equippedItems = this.getMud().equippedItems;
    for (ItemDef item : equippedItems) {
      if (item != null) {
        result[index] = item.id;
        index++;
      }
    }
    int[] cleanResult = new int[index];
    System.arraycopy(result, 0, cleanResult, 0, index);
    return cleanResult;
  }

  /**
   * Whether or not the specified item slot is equipped. Note that this does not use an item id, but
   * a slot index.
   *
   * @param slotIndex int
   * @return boolean
   */
  public boolean isEquipped(int slotIndex) {
    if (slotIndex < 0) return false;

    return mud.getInventory()[slotIndex].getEquipped();
    //
    // int[] inventoryItemEquipped = (int[]) reflector.getObjectMember(mud,
    // "inventoryItemEquipped");
    //
    // if(slot != -1) {
    // return inventoryItemEquipped[slot] > 0;
    // }
    //
    // return false;
  }

  /**
   * Whether or not the specified item ID is equipped. This is different from Controller.isEquipped
   * due to Coleslaw allowing for you to wield items outside the inventory. It functions as expected
   * on Uranium.
   *
   * @param itemId int
   * @return boolean
   */
  public boolean isItemIdEquipped(int itemId) {
    if (this.isAuthentic()) return this.isEquipped(this.getInventoryItemSlotIndex(itemId));

    ItemDef[] equippedItems = this.getMud().equippedItems;
    for (ItemDef item : equippedItems) {
      if (item != null) {
        if (item.getName().equalsIgnoreCase(this.getItemName(itemId))) return true;
      }
    }
    return false;
  }

  /**
   * Equips the item in the specified slot. Note that this does not use an item id, but a slot
   * index.
   *
   * @param slotIndex int
   */
  public void equipItem(int slotIndex) {
    if (slotIndex < 0 || slotIndex > 30)
      System.out.println("Warning: Trying to equip an invalid slot");
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(169);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Equipts an item by ID. The item must be in your inventory
   *
   * @param id the ID of the item to equip
   */
  public void equipItemById(int id) {
    equipItem(getInventoryItemSlotIndex(id));
  }

  /**
   * Unequips the item in the specified slot. Note that this does not use an item id, but a slot
   * index.
   *
   * @param slotIndex int
   */
  public void unequipItem(int slotIndex) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(170);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Creates the specified account provided in the command line with the specified email.
   *
   * @param email String
   */
  public void createAccount(String email) {
    createAccount(email, Main.config.getUsername(), Main.config.getPassword());
  }

  /**
   * Creates the specified account on the server.
   *
   * @param email String
   * @param username String
   * @param password String
   */
  public void createAccount(String email, String username, String password) {
    // TODO: return true/false based on success
    boolean autoLogin = Main.isAutoLogin();
    Main.setAutoLogin(false);

    Main.logMethod("createAccount", "nothing");

    reflector.setObjectMember(mud, "loginScreenNumber", 1);

    Panel panelLogin = (Panel) reflector.getObjectMember(mud, "menuNewUser");

    int menuNewUserUsername = (int) reflector.getObjectMember(mud, "menuNewUserUsername");
    int menuNewUserPassword = (int) reflector.getObjectMember(mud, "menuNewUserPassword");
    int menuNewUserEmail = (int) reflector.getObjectMember(mud, "menuNewUserEmail");

    panelLogin.setText(menuNewUserUsername, Main.config.getUsername());
    panelLogin.setText(menuNewUserPassword, Main.config.getPassword());
    panelLogin.setText(menuNewUserEmail, email);

    reflector.setObjectMember(mud, "enterPressed", true);

    Main.setAutoLogin(autoLogin);
  }

  /** Attempts in using the credentials specified in the command line. */
  public void login() {
    // TODO: return true/false based on success

    Main.logMethod("login", "nothing");

    reflector.setObjectMember(mud, "loginScreenNumber", 2);

    Panel panelLogin = (Panel) reflector.getObjectMember(mud, "panelLogin");

    int controlLoginUser = (int) reflector.getObjectMember(mud, "controlLoginUser");
    int controlLoginPass = (int) reflector.getObjectMember(mud, "controlLoginPass");

    panelLogin.setText(controlLoginUser, Main.config.getUsername());
    panelLogin.setText(controlLoginPass, Main.config.getPassword());

    reflector.setObjectMember(mud, "enterPressed", true);
  }

  /**
   * Retrieves the current fatigue. Returns 0 on Coleslaw.
   *
   * @return int -- as a percentage [0, 100].
   */
  public int getFatigue() {
    if (!isAuthentic()) return 0;
    return mud.getStatFatigue();
  }

  /**
   * Retrieves the current fatigue status while sleeping.
   *
   * @return int -- as a percentage [0, 100]
   */
  public int getFatigueDuringSleep() {
    return (int) reflector.getObjectMember(mud, "fatigueSleeping");
  }

  /**
   * Whether or not the player is currently in combat.
   *
   * @return boolean
   */
  public boolean isInCombat() {
    ORSCharacterDirection dir = this.getCharacterDirection(this.getPlayer());
    return dir == ORSCharacterDirection.COMBAT_A || dir == ORSCharacterDirection.COMBAT_B;
  }

  /**
   * Whether or not the specified player index is in combat.
   *
   * @param playerIndex int
   * @return boolean -- returns true if in combat. returns false if not in combat, or if player
   *     index is non-existent.
   */
  public boolean isPlayerInCombat(int playerIndex) {
    if (mud.getPlayer(playerIndex) == null) return false;

    return mud.getPlayer(playerIndex).combatTimeout == 499;
  }

  /**
   * Whether or not an NPC/action option menu is currently presented to the player.
   *
   * @return boolean
   */
  public boolean isInOptionMenu() {
    return (boolean) reflector.getObjectMember(mud, "optionsMenuShow");
  }

  /**
   * Retrieves the amount of options currently presented to the user.
   *
   * @return int
   */
  public int getOptionMenuCount() {
    return (int) reflector.getObjectMember(mud, "optionsMenuCount");
  }

  /**
   * Retrieves the text of the specified option index when talking to an NPC or performing an
   * action.
   *
   * @param i zero-indexed option (i.e first option is 0, second option is 1, etc..)
   * @return String -- null if option does not exist, or if quest menu is not up.
   */
  public String getOptionsMenuText(int i) {
    String[] optionsMenuText = (String[]) reflector.getObjectMember(mud, "optionsMenuText");
    if (i < optionsMenuText.length) return optionsMenuText[i];

    return null;
  }

  /**
   * Retrieves the array of options inside of an option menu.
   *
   * @return String[]
   */
  public String[] getOptionsMenuText() {
    return (String[]) reflector.getObjectMember(mud, "optionsMenuText");
  }

  /**
   * Selects an option menu when talking to an NPC or performing an action.
   *
   * @param answerIndex -- the index of the answer, starting at 0.
   */
  public void optionAnswer(int answerIndex) {
    if (answerIndex >= getOptionMenuCount()) return;

    Main.logMethod("optionAnswer", answerIndex);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(116);
    mud.packetHandler.getClientStream().bufferBits.putByte(answerIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Talks to the specified npc server index.
   *
   * @param serverIndex int
   * @return true -- true if request to talk sent, false if server index is invalid.
   */
  public boolean talkToNpc(int serverIndex) {
    // TODO: add boolean parameter for waitForOptionsMenu
    if (serverIndex < 0) return false;

    walktoNPCAsync(serverIndex);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(153);
    mud.packetHandler.getClientStream().bufferBits.putShort(serverIndex);
    mud.packetHandler.getClientStream().finishPacket();

    return true;
  }

  /**
   * Whether or not the bank window is currently open.
   *
   * @return boolean
   */
  public boolean isInBank() {
    return (boolean) reflector.getObjectMember(mud, "showDialogBank");
  }

  /** Closes the bank window. No effect if window is not currently open. */
  public void closeBank() {
    reflector.setObjectMember(mud, "showDialogBank", false);
    sleep(GAME_TICK);
  }

  /**
   * Retrieves a list of all the items inside the bank.
   *
   * @return guaranteed to not be null.
   */
  public List<Item> getBankItems() {
    List<Item> bankItems = new ArrayList<>();

    if (!isInBank()) {
      return bankItems;
    }

    ArrayList<Object> _bankItems =
        (ArrayList<Object>) reflector.getObjectMemberFromSuperclass(mud.getBank(), "bankItems");

    if (_bankItems != null) {
      for (Object _bankItem : _bankItems) {
        Item bankItem = (Item) reflector.getObjectMember(_bankItem, "item");

        if (bankItem != null) {
          bankItems.add(bankItem);
        }
      }
    }

    return bankItems;
  }

  /**
   * Retrieves the total count of all items in the bank.
   *
   * @return int
   */
  public int getBankItemsCount() {
    return (int) this.getMudClientValue("newBankItemCount");
  }

  /**
   * Retrieves the amount of the item in the bank.
   *
   * @param itemId int
   * @return int -- returns -1 if bank not open.
   */
  public int getBankItemCount(int itemId) {
    if (!this.isInBank()) return -1;

    List<Item> bankItems = this.getBankItems();

    for (Item bankItem : bankItems) {
      int bankItemId = bankItem.getItemDef().id;

      if (bankItemId == itemId) {
        return bankItem.getAmount();
      }
    }

    return 0;
  }

  /**
   * Whether or not the specified item ID is in the bank.
   *
   * @param itemId int
   * @return boolean -- true if item is in the bank. Returns false if item not present or bank is
   *     not open.
   */
  public boolean isItemInBank(int itemId) {
    return getBankItemCount(itemId) > 0;
  }

  /**
   * Deposits all yo stuff in the bank
   *
   * @return true if we were in the bank
   */
  public boolean depositAll() {
    if (!isInBank()) return false;
    for (int itemId : getInventoryItemIds()) {
      depositItem(itemId, getInventoryItemCount(itemId));
      sleep(320);
    }
    return true;
  }

  /**
   * Deposits one of specified item into the bank.
   *
   * @param itemId int
   * @return boolean -- returns true on success. Returns false if you do not have that item in your
   *     inventory, or if the bank is not open.
   */
  public boolean depositItem(int itemId) {
    return depositItem(itemId, 1);
  }

  /**
   * Deposits the specified item, of specified amount, into the bank.
   *
   * @param itemId int
   * @param amount int
   * @return boolean -- returns true on success. Returns false if you do not have that item in your
   *     inventory, or if the bank is not open.
   */
  public boolean depositItem(int itemId, int amount) {
    if (!isInBank()) {
      return false;
    }

    if (!isItemInInventory(itemId)) {
      return false;
    }

    if (amount <= 0) return true;

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(23);
    mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
    mud.packetHandler.getClientStream().bufferBits.putInt(amount);
    mud.packetHandler.getClientStream().finishPacket();

    return true;
  }

  /**
   * Withdraws one of the specified item from the bank.
   *
   * @param itemId int
   * @return boolean -- returns true if you already have one or more of those items in your
   *     inventory. Returns false if you currently do not have that amount, or if you do not have
   *     the bank open.
   */
  public boolean withdrawItem(int itemId) {
    return withdrawItem(itemId, 1);
  }

  /**
   * Withdraws the specified item, of specified amount, from the bank.
   *
   * @param itemId int
   * @param amount int
   * @return boolean -- returns true if you already have that amount in your inventory. Returns
   *     false if you do not currently have that amount, or if you do not have the bank open.
   */
  public boolean withdrawItem(int itemId, int amount) {
    if (!isInBank()) return false;

    if (getInventoryItemCount(itemId) >= amount) return true;

    if (amount <= 0) return true;

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(22);
    mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
    mud.packetHandler.getClientStream().bufferBits.putInt(amount);

    if (Config.S_WANT_BANK_NOTES) mud.packetHandler.getClientStream().bufferBits.putByte(0);

    mud.packetHandler.getClientStream().finishPacket();

    return false;
  }

  /**
   * Withdraws a specified amount of an item from the bank. (APOS compatibility method)
   *
   * @param itemId the ID of the item to be withdrawn
   * @param amount the amount of the item to be withdrawn
   * @return true if the withdrawal was successful, false otherwise
   */
  public boolean withdrawItem_apos(int itemId, int amount) {
    if (!isInBank()) return false;

    if (amount <= 0) return true;

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(22);
    mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
    mud.packetHandler.getClientStream().bufferBits.putInt(amount);

    if (Config.S_WANT_BANK_NOTES) mud.packetHandler.getClientStream().bufferBits.putByte(0);

    mud.packetHandler.getClientStream().finishPacket();

    return false;
  }

  /**
   * Withdraws the specified item, as a note, of specified amount, from the bank. Only works on
   * Coleslaw.
   *
   * @param itemId int
   * @param amount int
   * @return boolean -- returns true if you already have that amount in your inventory. Returns
   *     false if you do not currently have that amount, or if you do not have the bank open.
   */
  public boolean withdrawItemAsNote(int itemId, int amount) {
    if (!Config.S_WANT_BANK_NOTES) {
      this.displayMessage("@whi@ERROR: @red@Server is not configured to use bank notes");
      return false;
    }

    if (!isInBank()) return false;

    if (getInventoryItemCount(itemId) >= amount) return true;

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(22);
    mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
    mud.packetHandler.getClientStream().bufferBits.putInt(amount);
    mud.packetHandler.getClientStream().bufferBits.putByte(1);

    mud.packetHandler.getClientStream().finishPacket();

    return false;
  }

  /**
   * Displays a message in the client chat window, of the specified MessageType.
   *
   * <p>EXAMPLE(int type, "default @color code@")
   *
   * <p>GAME(0, "@whi@")
   *
   * <p>PRIVATE_RECEIVE(1, "@cya@")
   *
   * <p>PRIVATE_SEND(2, "@cya@")
   *
   * <p>QUEST(3, "@whi@")
   *
   * <p>CHAT(4, "@yel@")
   *
   * <p>FRIEND_STATUS(5, "@cya@")
   *
   * <p>TRADE(6, "@whi@")
   *
   * <p>INVENTORY(7, "@whi@")
   *
   * <p>GLOBAL_CHAT(8, "@yel@")
   *
   * <p>CLAN_CHAT(9, "@yel@")
   *
   * @param rstext -- you may use @col@ colors here.
   */
  public void displayMessage(String rstext, int type) {
    reflector.mudInvoker(mud, "showMessage", false, "", rstext, MessageType.lookup(type), 0, "");
  }

  /**
   * Displays a message in the client chat window.
   *
   * @param rstext -- you may use @col@ colors here.
   */
  public void displayMessage(String rstext) {
    reflector.mudInvoker(mud, "showMessage", false, "", rstext, MessageType.GAME, 0, "");
  }

  /**
   * Retrieves the distance between two tiles.
   *
   * @param x1 int
   * @param y1 int
   * @param x2 int
   * @param y2 int
   * @return int
   */
  public int distance(int x1, int y1, int x2, int y2) {
    return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  /*
   * +
   * Retrieves the distance to a tile
   *
   * @param x1 int
   *
   * @param x2 int
   *
   * @return int
   */
  public int distanceTo(int x, int y) {
    return distance(currentX(), currentY(), x, y);
  }

  /**
   * Converts local region coordinates to global coordinates.
   *
   * @param x int
   * @return int
   */
  public int offsetX(int x) {
    return x + mud.getMidRegionBaseX();
  }

  /**
   * Converts local region coordinates to global coordinates.
   *
   * @param z int
   * @return int
   */
  public int offsetZ(int z) {
    return z + mud.getMidRegionBaseZ();
  }

  /**
   * Converts global coordinates to local region coordinates.
   *
   * @param x int
   * @return int
   */
  public int removeOffsetX(int x) {
    return x - mud.getMidRegionBaseX();
  }

  /**
   * Converts global coordinates to local region coordinates.
   *
   * @param z int
   * @return int
   */
  public int removeOffsetZ(int z) {
    return z - mud.getMidRegionBaseZ();
  }

  /**
   * Converts player/NPC coordinates to local region coordinates to global coordinates.
   *
   * @param x int
   * @return int
   */
  public int convertX(int x) {
    return (x - 64) / mud.getTileSize() + mud.getMidRegionBaseX();
  }

  /**
   * Converts player/NPC coordinates to local region coordinates to global coordinates.
   *
   * @param z int
   * @return int
   */
  public int convertZ(int z) {
    return (z - 64) / mud.getTileSize() + mud.getMidRegionBaseZ();
  }

  private MudClientGraphics getMudGraphics() {
    return (MudClientGraphics) reflector.getObjectMember(mud, "surface");
  }

  /**
   * Resizes the client applet window.
   *
   * @param width int
   * @param height int
   */
  public void resizeWindow(int width, int height) {
    mud.resizeWidth = width;
    mud.resizeHeight = height;
  }

  /**
   * Makes the "unique" file name for each screenshot to prevent screenshot file over writing. could
   * be shortened? Theoretically allows 1 screenshot to be saved per second forever, also
   * screenshots will autosort by date/time when sorted by filename. This uses an almost identical
   * save file structure and (I assume) similar method as to Runelite.
   */
  private static final SimpleDateFormat screenshotNameFormat =
      new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

  /**
   * Takes a screenshot of the client applet and saves a bitmap as the specified filename.
   *
   * @param fileName String
   * @return boolean -- returns true on success. Returns false if image could not be saved.
   */
  public boolean takeScreenshot(String fileName) {
    boolean temporaryToggledGFX = false;
    boolean temporaryToggle3D = false;
    boolean temporaryToggledInterlacing = false;
    String directory = "";
    String path = "";
    String savePath = "";

    if (isInterlacing()) {
      setInterlacer(false);
      temporaryToggledInterlacing = true;
      sleep(100);
    }
    if (!isDrawEnabled()) {
      setDrawing(true, 0);
      temporaryToggledGFX = true;
    }
    if (!isRender3DEnabled()) {
      setRender3D(true);
      temporaryToggle3D = true;
    }
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String playerTime = screenshotNameFormat.format(timestamp);
    String playerName = getPlayerName();
    MudClientGraphics gfx = getMudGraphics();

    final int numSnapshots = 5;
    int[][] snapshots = new int[numSnapshots][gfx.width2 * gfx.height2];
    int[] finalSnapshot = new int[gfx.width2 * gfx.height2];

    BufferedImage img = new BufferedImage(gfx.width2, gfx.height2, BufferedImage.TYPE_INT_RGB);

    // to deal with the scanline problem: make multiple copies
    // have non-black override black to "average out"
    for (int i = 0; i < numSnapshots; i++) {
      System.arraycopy(gfx.pixelData, 0, snapshots[i], 0, gfx.width2 * gfx.height2);
      this.sleep(ThreadLocalRandom.current().nextInt(1, 5 + 1));
    }

    for (int i = 0; i < numSnapshots; i++) {
      for (int x = 0; x < gfx.width2; x++) {
        for (int y = 0; y < gfx.height2; y++) {
          if (snapshots[i][(y * gfx.width2) + x] != 0) { // assuming it's a true black.
            finalSnapshot[(y * gfx.width2) + x] = snapshots[i][(y * gfx.width2) + x];
          }
        }
      }
    }

    for (int x = 0; x < gfx.width2; x++) {
      for (int y = 0; y < gfx.height2; y++) {
        img.setRGB(x, y, finalSnapshot[(y * gfx.width2) + x]);
      }
    }

    try {
      if (playerName != null && !playerName.isEmpty()) {
        directory = "Screenshots/" + playerName + "/";
        path = playerName + "_" + playerTime + ".png";
      } else {
        directory = "Screenshots/";
        path = playerTime + ".png";
      }
      if (fileName != null && !fileName.isEmpty()) {
        savePath = directory + fileName + "_" + path;
      } else {
        savePath = directory + path;
      }

      Files.createDirectories(Paths.get(directory));
      ImageIO.write(img, "png", new File(savePath));

      if (Files.exists(Paths.get(savePath))) {
        log("@cya@Screenshot successfully saved to ./IdleRSC/" + savePath);
      } else {
        log("@red@Error: @cya@Screenshot not detected at ./IdleRSC/" + savePath);
      }
    } catch (IOException e) {
      System.err.println("Failed to create directory and/or take screenshot!" + e.getMessage());
      e.printStackTrace();
      return false;
    }
    if (temporaryToggle3D && isRender3DEnabled()) {
      setRender3D(false);
    }
    if (temporaryToggledGFX && isDrawEnabled()) {
      setDrawing(false, 0);
    }
    if (temporaryToggledInterlacing) {
      setInterlacer(true);
    }
    return true;
  }

  /**
   * Retrieves the command of the specified item.
   *
   * @param itemId int
   * @return String -- guaranteed to not be null.
   */
  public String getItemCommand(int itemId) {
    try {
      ItemDef item = EntityHandler.getItemDef(itemId);

      if (item == null) return null;

      String[] commands = item.getCommand();

      if (commands == null) return "";

      return commands[0];
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves the examine text of the specified item.
   *
   * @param itemId int
   * @return String -- guaranteed to not be null.
   */
  public String getItemExamineText(int itemId) {
    try {
      return EntityHandler.getItemDef(itemId).getDescription();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves whether or not the item is tradeable.
   *
   * @param itemId int
   */
  public boolean isItemTradeable(int itemId) {
    try {
      return !EntityHandler.getItemDef(itemId).untradeable;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Retrieves whether or not the item is notable.
   *
   * @param itemId int
   */
  public boolean isItemNotable(int itemId) {
    try {
      return EntityHandler.getItemDef(itemId).noteable;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Retrieves the name of the specified item.
   *
   * @param itemId int
   * @return String -- guaranteed to not be null.
   */
  public String getItemName(int itemId) {
    try {
      return EntityHandler.getItemDef(itemId).getName();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves the item id of the specified item name.
   *
   * @param itemName String
   * @return int -- returns -1 if item does not exist
   */
  public int getItemId(String itemName) {
    try {
      for (int i = 0; i <= 10000; i++) {
        if (EntityHandler.getItemDef(i).getName().equalsIgnoreCase(itemName)) {
          return i;
        }
      }
    } catch (Exception e) {
      return -1;
    }

    return -1;
  }

  /**
   * Whether or not the specified item is a wearable item.
   *
   * @param itemId int
   * @return boolean
   */
  public boolean isItemWearable(int itemId) {
    try {
      return EntityHandler.getItemDef(itemId).isWieldable();
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Whether or not the specified item is a stackable item.
   *
   * @param itemId int
   * @return boolean
   */
  public boolean isItemStackable(int itemId) {
    try {
      return EntityHandler.getItemDef(itemId).isStackable();
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Sets the current server message window text.
   *
   * @param msg String
   * @param largeBox boolean
   * @param show boolean
   */
  public void setServerMessage(String msg, boolean largeBox, boolean show) {
    mud.setServerMessage(msg);
    mud.setServerMessageBoxTop(largeBox);
    mud.setShowDialogServerMessage(show);
  }

  /** Closes the current server message popup window. */
  public void closeServerMessage() {
    mud.setShowDialogServerMessage(false);
  }

  /**
   * Retrieves the current server message popup window text.
   *
   * @return String -- no guarantee on nullability.
   */
  public String getServerMessage() {
    return (String) reflector.getObjectMember(mud, "serverMessage");
  }

  /**
   * Retrieves the spellId of the spell name. (0,1,2,3,4,etc)
   *
   * @param name -- must match spelling of the spell book. case insensitive.
   * @return int -- -1 if spell not found.
   */
  public int getSpellIdFromName(String name) {
    try {
      for (int i = 0; i < EntityHandler.spellCount(); i++) {
        SpellDef d = EntityHandler.getSpellDef(i);
        if (EntityHandler.getSpellDef(i).getName().equalsIgnoreCase(name)) return i;
      }
    } catch (Exception e) {
      return -1;
    }

    return -1;
  }

  /**
   * Casts the specified spell on the object at the specified coordinates.
   *
   * @param spellId int
   * @param x int
   * @param y int
   */
  public void castSpellOnObject(int spellId, int x, int y) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(99);
    mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(y);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /** Sends a packet to the server to stop batching and sleeps until it has stopped. */
  public void stopBatching() {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(199);
    mud.packetHandler.getClientStream().bufferBits.putByte(6);
    mud.packetHandler.getClientStream().finishPacket();
    while (isBatching()) sleep(640);
  }

  // * The Auction House only allows one interaction every 5 seconds. When making
  // new methods
  // * for auctions make sure it uses the following methods to guarantee it
  // complies with this
  // * limitation.
  // * - waitForAuctionTimer() -- Put this before any part of a method that
  // interacts with the
  // * Auction House
  // * - beginAuctionTimeout() -- Put this after intacting. If you're calling
  // getAuctions() or
  // * refreshAuctions() those methods will automatically do this.

  /**
   * Check if the player is in an auction house and not at the disallowed one on Karamja
   *
   * @return boolean -- If the player is in an allowed auction house
   */
  public boolean isInAuctionHouse() {
    if (!mud.auctionHouse.isVisible()) {
      log("You are not in an Auction House", "red");
      return false;
    }

    // Disallow auction house on Karamja so creating/cancelling auctions can't be
    // used to bypass the
    // lack of a bank in the area
    if (currentX() > 320 && currentX() < 400 && currentY() > 679 && currentY() < 730) {
      closeAuctionHouse();
      log(
          "This auction house clerk has been disallowed from creating auctions in scripts to prevent",
          "red");
      log("the potentially unintended noting of items in an area without a bank", "red");
      return false;
    }
    return true;
  }

  /** Closes the Auction House window */
  public int closeAuctionHouse() {
    if (!mud.auctionHouse.isVisible()) return -1;
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(199);
    mud.packetHandler.getClientStream().bufferBits.putByte(10);
    mud.packetHandler.getClientStream().bufferBits.putByte(4);
    mud.packetHandler.getClientStream().finishPacket();
    mud.auctionHouse.setVisible(false);
    sleep(640);
    return 1;
  }

  /** Uses npcCommand1 to open the auction house on the nearest clerk */
  public void openAuctionHouse() {
    // Open an auction house if near an auction clerk that isn't the disallowed
    // Karamja one
    if (currentX() < 320 && currentX() > 400 && currentY() < 679 && currentY() > 730) {
      ORSCharacter npc = getNearestNpcById(NpcId.AUCTION_CLERK.getId(), false);
      if (npc != null) {
        npcCommand1(npc.serverIndex);
        sleep(1280);
        while (!isInAuctionHouse() && isRunning()) sleep(640);
      } else {
        log("Auction house clerk not found", "red");
      }
    }
  }

  /**
   * Buys an amount of items from a given auction id.
   *
   * @param auction AuctionItem -- Auction to buy from
   * @param itemAmount int -- Amount of items to buy
   */
  public int auctionBuy(AuctionItem auction, int itemAmount) {
    if (!isInAuctionHouse()) return -1;
    if (auction == null) {
      log("AuctionItem may not be null", "red");
      return -1;
    }
    if (auction.getSeller().equals(getPlayerName())) {
      log("Skipping auction " + auction.getAuctionId() + " since you are the seller", "red");
      return -1;
    }
    if (itemAmount < 1) {
      log("The amount to sell must be higher than 0", "red");
      return -1;
    }
    if (getInventoryItemCount(ItemId.COINS.getId()) < auction.getPricePerItem()) {
      log("You do not have enough coins to buy anything from this auction", "red");
      return -1;
    }

    waitForAuctionTimer();
    int heldCoins = getInventoryItemCount(ItemId.COINS.getId());
    int pricePer = auction.getPricePerItem();
    int buyAmount =
        heldCoins >= pricePer * itemAmount ? itemAmount : Math.floorDiv(heldCoins, pricePer);

    log(
        "Bought "
            + buyAmount
            + " of item "
            + auction.getItemId()
            + " on the Auction House for "
            + pricePer
            + " coins each",
        "gre");
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(199);
    mud.packetHandler.getClientStream().bufferBits.putByte(10);
    mud.packetHandler.getClientStream().bufferBits.putByte(0);
    mud.packetHandler.getClientStream().bufferBits.putInt(auction.getAuctionId());
    mud.packetHandler.getClientStream().bufferBits.putInt(buyAmount);
    mud.packetHandler.getClientStream().finishPacket();
    sleep(2560);
    mud.setShowDialogServerMessage(false); // Close the dialog
    beginAuctionTimeout();
    return 1;
  }

  /**
   * This method can potentially use all of your carried coins so be cautious when using it.<br>
   * <br>
   * Attempts to buy all items from the given AuctionItem.
   *
   * @param auction AuctionItem -- The auction to buy from.
   */
  public void auctionBuyAll(AuctionItem auction) {
    auctionBuy(auction, auction.getAmount());
  }

  /**
   * Lists an amount of itemId for a specified price in the auction house. If the amount is greater
   * than the amount in the inventory it will list all in inventory.
   *
   * @param itemId int -- Item id
   * @param itemAmount int -- Amount of item
   * @param pricePerItem int -- Price to sell each item at
   */
  public int auctionCreate(int itemId, int itemAmount, int pricePerItem) {
    if (!isInAuctionHouse()) return -1;
    if (itemId < 0) {
      log(itemId + " is not a valid item id", "red");
      return -1;
    }
    if (getInventoryItemCount(itemId) == 0) {
      log("Item " + itemId + " is not in inventory!", "red");
      return -1;
    }

    int sellAmount =
        getInventoryItemCount(itemId) >= itemAmount ? itemAmount : getInventoryItemCount(itemId);

    waitForAuctionTimer();
    log(
        "Listed "
            + sellAmount
            + " of item "
            + itemId
            + " on the Auction House for "
            + pricePerItem
            + " coins each",
        "gre");
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(199);
    mud.packetHandler.getClientStream().bufferBits.putByte(10);
    mud.packetHandler.getClientStream().bufferBits.putByte(1);
    mud.packetHandler.getClientStream().bufferBits.putInt(itemId);
    mud.packetHandler.getClientStream().bufferBits.putInt(sellAmount);
    mud.packetHandler.getClientStream().bufferBits.putInt(pricePerItem * sellAmount);
    mud.packetHandler.getClientStream().finishPacket();
    sleep(2560);
    mud.setShowDialogServerMessage(false);
    beginAuctionTimeout();
    return 1;
  }

  /**
   * Cancels an auction. This only works if the auction is owned by the current player.
   *
   * @param auction AuctionItem -- Auction Id
   */
  public int auctionCancel(AuctionItem auction) {
    if (!isInAuctionHouse() || auction == null) return -1;
    if (this.getPlayerName().equals(auction.getSeller())) {
      /*
       * Checks if the player is the owner of the auction.If you remove this check and
       * try to
       * cancel someone else's auction you will be logged by the server for potential
       * packet
       * manipulation.
       */
      log("You are not the owner of this auction", "red");
      return -1;
    }

    waitForAuctionTimer();
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(199);
    mud.packetHandler.getClientStream().bufferBits.putByte(10);
    mud.packetHandler.getClientStream().bufferBits.putByte(2);
    mud.packetHandler.getClientStream().bufferBits.putInt(auction.getAuctionId());
    mud.packetHandler.getClientStream().finishPacket();
    sleep(2560);
    mud.setShowDialogServerMessage(false);
    beginAuctionTimeout();
    return 1;
  }

  /** Refreshes the Auction House listings. */
  public int auctionRefresh() {
    if (!isInAuctionHouse()) return -1;

    waitForAuctionTimer();
    log("Refreshed Auction House", "gre");
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(199);
    mud.packetHandler.getClientStream().bufferBits.putByte(10);
    mud.packetHandler.getClientStream().bufferBits.putByte(3);
    mud.packetHandler.getClientStream().finishPacket();
    beginAuctionTimeout();
    return 1;
  }

  /**
   * Returns an ArrayList of AuctionItems for a given item id.
   *
   * @param itemId int -- Item id to search for.
   * @return ArrayList -- Items that match the item id
   */
  public ArrayList<AuctionItem> auctionSearchForItemId(int itemId) {
    if (!isInAuctionHouse()) return null;
    if (itemId < 0) {
      log(itemId + " is not a valid item id", "red");
      return null;
    }

    ArrayList<AuctionItem> searchedArray = new ArrayList<>();

    waitForAuctionTimer();
    for (AuctionItem item : getAuctions()) {
      if (item.getItemId() == itemId) searchedArray.add(item);
    }
    if (searchedArray.size() > 0) return searchedArray;
    log("There are no auctions for item " + itemId, "red");
    return null;
  }

  /**
   * Returns an AuctionItem with the cheapest price per item for an item id.
   *
   * @param itemId int -- Item id to search for
   * @return AuctionItem -- Cheapest AuctionItem of the requested itemId
   */
  public AuctionItem auctionSearchCheapestOfItemId(int itemId) {
    if (!isInAuctionHouse()) return null;
    if (itemId < 0) {
      log(itemId + " is not a valid item id", "red");
      return null;
    }

    waitForAuctionTimer();
    ArrayList<AuctionItem> searchedArray = auctionSearchForItemId(itemId);

    if (searchedArray == null) return null;

    Collections.sort(searchedArray, Comparator.comparing(AuctionItem::getPricePerItem));
    if (searchedArray.size() > 0) return searchedArray.get(0);
    log("There are no auctions for item " + itemId, "red");
    return null;
  }

  /**
   * Returns an ArrayList of auctions for sale by the current player
   *
   * @return ArrayList
   */
  public ArrayList<AuctionItem> getMyAuctions() {
    return getPlayerAuctions(this.getPlayerName());
  }

  /**
   * Returns an ArrayList of auctions for sale by a specified player
   *
   * @param playerName String -- Player name to search for
   * @return
   */
  public ArrayList<AuctionItem> getPlayerAuctions(String playerName) {
    if (!isInAuctionHouse()) return null;
    if (playerName.length() < 1) return null;

    waitForAuctionTimer();
    ArrayList<AuctionItem> auctions = getAuctions();
    ArrayList<AuctionItem> myAuctions = new ArrayList<>();

    for (AuctionItem auction : auctions) {
      if (auction.getSeller().equals(playerName)) myAuctions.add(auction);
    }
    if (myAuctions.size() > 0) return myAuctions;
    log(playerName + " has no auctions", "red");
    return null;
  }

  /**
   * Does the same as getAuctions except it refreshes the auctions before returning the list.<br>
   * <br>
   * Gets an ArrayList of com.openrsc.interfaces.misc.AuctionItems from AuctionHouse via the
   * reflectAuctionList method and converts it to an ArrayList of models.entities.AuctionItem then
   * returns it
   *
   * @return ArrayList -- List of auctions
   */
  public ArrayList<AuctionItem> getAuctionsWithRefresh() {
    auctionRefresh();
    return getAuctions();
  }

  /**
   * Gets an ArrayList of com.openrsc.interfaces.misc.AuctionItems from AuctionHouse via the
   * reflectAuctionList method and converts it to an ArrayList of models.entities.AuctionItem then
   * returns it
   *
   * @return ArrayList -- List of auctions
   */
  public ArrayList<AuctionItem> getAuctions() {
    if (!isInAuctionHouse()) return null;

    ArrayList<AuctionItem> auctionItems = new ArrayList<>();
    ArrayList<?> reflectedCopy = new ArrayList<>(reflectAuctionList());

    for (Object a : reflectedCopy) {
      Integer auctionID = -1;
      Integer itemID = -1;
      Integer amount = -1;
      Integer price = -1;
      String seller2 = "";

      Method[] methods = a.getClass().getDeclaredMethods();
      for (Method m : methods) {
        try {
          switch (m.getName()) {
            case "getAuctionID":
              m.setAccessible(true);
              auctionID = (int) m.invoke(a);
              break;
            case "getItemID":
              m.setAccessible(true);
              itemID = (int) m.invoke(a);
              break;
            case "getAmount":
              m.setAccessible(true);
              amount = (int) m.invoke(a);
              break;
            case "getPrice":
              m.setAccessible(true);
              price = (int) m.invoke(a);
              break;
            case "getSeller":
              m.setAccessible(true);
              seller2 = (String) m.invoke(a);
              break;
            default:
          }
        } catch (Exception e) {
          System.out.println(e);
        }
      }
      auctionItems.add(new AuctionItem(auctionID, itemID, amount, price, seller2));
    }
    sleep(640);
    if (auctionItems.size() > 0) return auctionItems;
    return null;
  }

  /**
   * Returns a generic ArrayList of AuctionItems that need to be converted to
   * models.entities.AuctionItem This is done in getAuctions so call that method instead.
   *
   * @return ArrayList -- Generic ArrayList containing AuctionItems from
   *     com.openrsc.interfaces.misc.AuctionHouse
   */
  private ArrayList<?> reflectAuctionList() {
    AuctionHouse AH = this.getMud().getAuctionHouse();
    if (AH != null) {
      try {
        Field auctionField = AH.getClass().getDeclaredField("auctionItems");
        auctionField.setAccessible(true);
        Object obj = auctionField.get(AH);
        if (obj instanceof ArrayList) {
          ArrayList<?> reflectedList = (ArrayList<?>) obj;
          return reflectedList;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * The auction house requires a minimum of 5 seconds between each interaction. This method sets a
   * timeout to 5.5 seconds to make sure nothing strange happens.
   */
  public void beginAuctionTimeout() {
    auctionTimeout = (System.currentTimeMillis() + 6000);
    System.out.println("\nAuction timeout: " + auctionTimeout);
    log("Setting an auction timeout for 5.5 seconds", "yel");
  }

  /**
   * Checks if the auction timeout from beginAuctionTimeout is over, and sleeps if it is not over.
   */
  public void waitForAuctionTimer() {
    if (System.currentTimeMillis() <= auctionTimeout && auctionTimeout != -1) {
      log("Sleeping until the auction timeout is over", "yel");
      sleep((int) (auctionTimeout - System.currentTimeMillis()));
    }
    auctionTimeout = -1;
  }

  /**
   * Prints an AuctionItem to console
   *
   * @param item AuctionItem -- Auction item to print
   */
  public void printAuctionItem(AuctionItem item) {
    System.out.println(
        "Auction: "
            + item.getAuctionId()
            + (String.join("", Collections.nCopies(7 - getIntLength(item.getAuctionId()), " ")))
            + "Seller: "
            + item.getSeller()
            + (String.join("", Collections.nCopies(15 - item.getSeller().length(), " ")))
            + "Item: "
            + item.getItemId()
            + (String.join("", Collections.nCopies(7 - getIntLength(item.getItemId()), " ")))
            + ItemId.getById(item.getItemId())
            + (String.join(
                "",
                Collections.nCopies(
                    33 - String.valueOf(ItemId.getById(item.getItemId())).length(), " ")))
            + "Amount: "
            + item.getAmount()
            + (String.join("", Collections.nCopies(13 - getIntLength(item.getAmount()), " ")))
            + "Price: "
            + (item.getPricePerItem())
            + (String.join("", Collections.nCopies(13 - getIntLength(item.getPricePerItem()), " ")))
            + "Stack Price: "
            + item.getPriceTotal()
            + (String.join("", Collections.nCopies(13 - getIntLength(item.getPriceTotal()), " "))));
  }

  /**
   * Returns the the amount of digits in an int
   *
   * @param numberToMeasure int -- Number to measure length of
   * @return int -- length
   */
  private int getIntLength(int numberToMeasure) {
    if (numberToMeasure == 0) return 1;
    return (int) (Math.log10(numberToMeasure) + 1);
  }

  /**
   * Casts the specified spell on the specified inventory item. Based on item slot, not item id.
   *
   * @param spellId int
   * @param slotIndex int
   */
  public void castSpellOnInventoryItem(int spellId, int slotIndex) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(4);
    mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Casts the specified spell on the specified ground item on the specified tile.
   *
   * @param spellId int
   * @param itemId int
   * @param x int
   * @param y int
   */
  public void castSpellOnGroundItem(int spellId, int itemId, int x, int y) {
    int a = mud.getMidRegionBaseX();
    int b = mud.getMidRegionBaseZ();

    int direction = getDirection(x, y);

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(249);
    mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(y);
    mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Retrieves the direction of the specified coordinate, relative to the player.
   *
   * @param x int
   * @param y int
   * @return int -- returns NORTH if standing on specified tile.
   */
  public int getDirection(int x, int y) {
    ORSCharacterDirection direction = ORSCharacterDirection.NORTH;

    if (x > currentX()) {
      direction = ORSCharacterDirection.WEST;
    } else if (x < currentX()) {
      direction = ORSCharacterDirection.EAST;
    }

    if (y > currentY()) {
      if (direction == ORSCharacterDirection.WEST) {
        direction = ORSCharacterDirection.SOUTH_WEST;
      } else {
        direction = ORSCharacterDirection.SOUTH_EAST;
      }
    } else if (y < currentY()) {
      if (direction == ORSCharacterDirection.WEST) {
        direction = ORSCharacterDirection.NORTH_WEST;
      } else {
        direction = ORSCharacterDirection.NORTH_EAST;
      }
    }

    return direction.rsDir;
  }

  /**
   * Retrieves the 1st command of the specified NPC.
   *
   * @param npcId int
   * @return String -- guaranteed to not be null
   */
  public String getNpcCommand1(int npcId) {
    try {
      return EntityHandler.getNpcDef(npcId).getCommand1();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves the 2nd command of the specified NPC.
   *
   * @param npcId int
   * @return String -- guaranteed to not be null
   */
  public String getNpcCommand2(int npcId) {
    try {
      return EntityHandler.getNpcDef(npcId).getCommand2();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves the examine text of the specified NPC.
   *
   * @param npcId int
   * @return String -- guaranteed to not be null
   */
  public String getNpcExamineText(int npcId) {
    try {
      return EntityHandler.getNpcDef(npcId).getDescription();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves the name of the specified NPC.
   *
   * @param npcId int
   * @return String -- guaranteed to not be null
   */
  public String getNpcName(int npcId) {
    try {
      return EntityHandler.getNpcDef(npcId).getName();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Whether or not the specified npcId is attackable. This does not reflect whether or not the
   * specified NPC is in combat.
   *
   * @param npcId int -- the id of the npc. This is NOT a server index.
   * @return boolean
   */
  public boolean isNpcAttackable(int npcId) {
    try {
      return EntityHandler.getNpcDef(npcId).isAttackable();
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Retrieves the 1st command of the specified object id.
   *
   * @param objId int
   * @return String -- guaranteed to not be null
   */
  public String getObjectCommand1(int objId) {
    try {
      return EntityHandler.getObjectDef(objId).getCommand1();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves the 2nd command of the specified object id.
   *
   * @param objId int
   * @return String -- guaranteed to not be null
   */
  public String getObjectCommand2(int objId) {
    try {
      return EntityHandler.getObjectDef(objId).getCommand2();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves the examine text of the specified object id.
   *
   * @param objId int
   * @return String -- guaranteed to not be null
   */
  public String getObjectExamineText(int objId) {
    try {
      return EntityHandler.getObjectDef(objId).getDescription();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves the name of the specified object id.
   *
   * @param objId int
   * @return String -- guaranteed to not be null
   */
  public String getObjectName(int objId) {
    try {
      return EntityHandler.getObjectDef(objId).getName();
    } catch (Exception e) {
      return "";
    }
  }

  /** Retrieves the number of prayers in the game. */
  public int getPrayersCount() {
    return EntityHandler.prayerCount();
  }

  /**
   * Retrieves the id of the specified prayerName. (0,1,2,3,4,etc)
   *
   * @param prayerName -- must match spelling of what is inside prayer book. Case insensitive.
   * @return int -- -1 if the prayer does not exist.
   */
  public int getPrayerId(String prayerName) {
    try {
      for (int i = 0; i < EntityHandler.prayerCount(); i++) {
        if (prayerName.equalsIgnoreCase(EntityHandler.getPrayerDef(i).getName())) {
          return i;
        }
      }
    } catch (Exception e) {
      return -1;
    }

    return -1;
  }

  /**
   * Retrieves the name of the specified prayer id.
   *
   * @param prayerId -- the id of the prayer
   * @return String -- null if no prayer found.
   */
  public String getPrayerName(int prayerId) {
    try {
      return EntityHandler.getPrayerDef(prayerId).getName();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Retrieves the level required to use the specified prayer.
   *
   * @param prayerId int
   * @return int -- -1 if the prayer does not exist
   */
  public int getPrayerLevel(int prayerId) {
    try {
      return EntityHandler.getPrayerDef(prayerId).getReqLevel();
    } catch (Exception e) {
      return -1;
    }
  }

  /**
   * Retrieves the drain rate of the specified prayer.
   *
   * @param prayerId int
   * @return int -- -1 if the prayer does not exist
   */
  public int getPrayerDrain(int prayerId) {
    try {
      return EntityHandler.getPrayerDef(prayerId).getDrainRate();
    } catch (Exception e) {
      return -1;
    }
  }

  /**
   * Whether or not the specified prayer is currently on.
   *
   * @param prayerId int
   * @return boolean
   */
  public boolean isPrayerOn(int prayerId) {
    return mud.checkPrayerOn(prayerId);
  }

  /**
   * Enables the prayer.
   *
   * @param prayerId int
   */
  public void enablePrayer(int prayerId) {
    // TODO: check prayer lvl and return true/false based off it
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(60);
    mud.packetHandler.getClientStream().bufferBits.putByte(prayerId);
    mud.packetHandler.getClientStream().finishPacket();
    mud.togglePrayer(prayerId, true);
  }

  /**
   * Disables the prayer.
   *
   * @param prayerId int
   */
  public void disablePrayer(int prayerId) {
    // TODO: check prayer lvl and return true/false based off it
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(254);
    mud.packetHandler.getClientStream().bufferBits.putByte(prayerId);
    mud.packetHandler.getClientStream().finishPacket();
    mud.togglePrayer(prayerId, false);
  }

  /**
   * Whether or not a shop window is currently open.
   *
   * @return boolean
   */
  public boolean isInShop() {
    return (boolean) reflector.getObjectMember(mud, "showDialogShop");
  }

  /** Uses npcCommand1 to open the shop on the nearest npc id given */
  public void openShop(int[] npcIds) {
    // I made this a String array just in case some npcs have a different command to
    // open their
    // shop.
    String[] shopCommandStrings = {"Trade"};
    ORSCharacter npc = getNearestNpcByIds(npcIds, false);
    if (npc != null) {
      for (int i = 0; i < shopCommandStrings.length; i++) {
        if (getNpcCommand1(npc.npcId).equals(shopCommandStrings[i])) {
          npcCommand1(npc.serverIndex);
          sleep(1280);
          while (!isInShop() && isRunning()) sleep(640);
        } else if (i == shopCommandStrings.length - 1) log("NPC does not have a shop", "red");
      }
    } else {
      log("Npc not found", "red");
    }
  }

  /** Closes the currently open shop window. Does nothing if no shop window is open. */
  public void closeShop() {
    reflector.setObjectMember(mud, "showDialogShop", false);
  }

  /**
   * Retrieves the number of different items which the shop sells.
   *
   * @return int -- amount of different items. Returns -1 if shop is not open.
   */
  public int getShopItemsCount() {
    if (!this.isInShop()) {
      return -1;
    }

    int[] shopItemIds = (int[]) this.getMudClientValue("shopCategoryID");

    int count = 0;
    for (int shopItemId : shopItemIds) {
      if (shopItemId > -1) {
        count++;
      } else {
        return count;
      }
    }

    return count;
  }

  /**
   * Retrieves a list of all the shop items in the shop.
   *
   * @return shopItems guaranteed to not be null.
   */
  public List<Item> getShopItems() {
    List<Item> shopItems = new ArrayList<>();

    if (!this.isInShop()) {
      return shopItems;
    }

    int[] shopItemIds = (int[]) this.getMudClientValue("shopCategoryID");
    int shopItemsCount = this.getShopItemsCount();

    for (int i = 0; i < shopItemsCount; i++) {
      int shopItemId = shopItemIds[i];

      if (shopItemId > -1) {
        int shopItemAmount = this.getShopItemCount(shopItemId);
        ItemDef shopItemDef = EntityHandler.getItemDef(shopItemId);

        Item shopItem = new Item(shopItemDef);
        shopItem.setAmount(shopItemAmount);

        shopItems.add(shopItem);
      }
    }

    return shopItems;
  }

  /**
   * Retrieves how many of the specified item is in stock.
   *
   * @param itemId int
   * @return int -- stock amount. If item is not sold at shop, it returns -1.
   */
  public int getShopItemCount(int itemId) {
    int[] count = (int[]) reflector.getObjectMember(mud, "shopItemCount");
    int[] ids = (int[]) reflector.getObjectMember(mud, "shopCategoryID");
    int[] prices = (int[]) reflector.getObjectMember(mud, "shopItemPrice");

    for (int i = 0; i < ids.length; i++) {
      if (ids[i] == itemId) {
        return count[i];
      }
    }

    return -1;
  }

  /**
   * Retrieves the price of the stack of items in the shop.
   *
   * @param itemId int
   * @return int -- price. -1 if item is not in the shop at all.
   */
  public int getShopItemStackPrice(int itemId) {
    int[] count = (int[]) reflector.getObjectMember(mud, "shopItemCount");
    int[] ids = (int[]) reflector.getObjectMember(mud, "shopCategoryID");
    int[] prices = (int[]) reflector.getObjectMember(mud, "shopItemPrice");

    for (int i = 0; i < ids.length; i++) {
      if (ids[i] == itemId) {
        return prices[i];
      }
    }

    return -1;
  }

  /**
   * Retrieves the price of a single item in the shop.
   *
   * @param itemId int
   * @return int -- price. -1 if item is not in the shop at all.
   */
  public int getShopItemPrice(int itemId) {
    int[] count = (int[]) reflector.getObjectMember(mud, "shopItemCount");
    int[] ids = (int[]) reflector.getObjectMember(mud, "shopCategoryID");
    int[] prices = (int[]) reflector.getObjectMember(mud, "shopItemPrice");

    for (int i = 0; i < ids.length; i++) {
      if (ids[i] == itemId) {
        int shopStock = getShopItemCount(itemId);
        int stackPrice = prices[i];
        return stackPrice / shopStock;
      }
    }

    return -1;
  }

  /**
   * Buys the specified item from the currently open shop.
   *
   * @param itemId int
   * @return boolean -- true on success. false if the shop is not open or shop does not have enough
   *     stock.
   */
  public boolean shopBuy(int itemId) {
    // TODO: check if enough coins in inventory, return false if not enough.
    if (!isInShop() || getShopItemCount(itemId) < 1) return false;

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(236);
    mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
    mud.packetHandler.getClientStream().bufferBits.putShort(getShopItemCount(itemId));
    mud.packetHandler.getClientStream().bufferBits.putShort(1);
    mud.packetHandler.getClientStream().finishPacket();

    return true;
  }

  /**
   * Sells the specified item to the currently open shop.
   *
   * @param itemId int
   * @return boolean -- true on success. false if shop is not open, shop does not accept the item,
   *     or not enough in inventory.
   */
  public boolean shopSell(int itemId) {
    if (!isInShop() || getShopItemCount(itemId) == -1 || this.getInventoryItemCount(itemId) < 1)
      return false;

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(221);
    mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
    mud.packetHandler.getClientStream().bufferBits.putShort(getShopItemCount(itemId));
    mud.packetHandler.getClientStream().bufferBits.putShort(1);
    mud.packetHandler.getClientStream().finishPacket();

    return true;
  }

  /**
   * Retrieves a list of all the skills.
   *
   * @return _list guaranteed to not be null.
   */
  public List<SkillDef> getSkills() {
    String[] skillNames = this.getSkillNamesLong();

    List<SkillDef> _list = new ArrayList<>();

    if (skillNames != null) {
      for (String skillName : skillNames) {
        SkillDef skillDef = new SkillDef();

        String name = skillName;
        int id = this.getStatId(name);
        int base = this.getBaseStat(id);
        int current = this.getCurrentStat(id);
        int xp = this.getPlayerExperience(id);
        int gainedXp = this.getStatXp(id);

        skillDef.setName(name);
        skillDef.setId(id);
        skillDef.setBase(base);
        skillDef.setCurrent(current);
        skillDef.setXp(xp);
        skillDef.setGainedXp(gainedXp);

        _list.add(skillDef);
      }
    }

    return _list;
  }

  /**
   * Retrieves the id of the specified skill name. Skill name is case insensitive and must match
   * what is spelled inside the stat tab.
   *
   * @param statName int
   * @return int -- -1 if the skill does not exist.
   */
  public int getStatId(String statName) {
    String[] skillNames = mud.getSkillNamesLong();

    for (int i = 0; i < skillNames.length; i++) {
      if (statName.equalsIgnoreCase(skillNames[i])) return i;
    }

    log(
        "Error - Unknown stat name: "
            + statName
            + ". Available names: "
            + Arrays.toString(skillNames));
    return -1;
  }

  /**
   * Retrieves the base level (excluding boosted/degraded stats) of the specified skill. `id` must
   * be within [0, getStatCount()].
   *
   * @param statId int
   * @return int
   */
  public int getBaseStat(int statId) {
    return ((int[]) reflector.getObjectMember(mud, "playerStatBase"))[statId];
  }

  /**
   * Retrieves the current level (including boosted/degraded stats) of the specified skill. `id`
   * must be within [0, getStatCount()].
   *
   * @param statId int
   * @return int
   */
  public int getCurrentStat(int statId) {
    return ((int[]) reflector.getObjectMember(mud, "playerStatCurrent"))[statId];
  }

  /**
   * Retrieves the current XP in the specified skill. `id` must be within [0, getStatCount()].
   *
   * @param statId int
   * @return int
   */
  public int getStatXp(int statId) {
    return (int) ((long[]) reflector.getObjectMember(mud, "playerStatXpGained"))[statId];
  }

  /**
   * Retrieves the number of skills in the game.
   *
   * @return int
   */
  public int getStatCount() {
    return ((long[]) reflector.getObjectMember(mud, "playerStatXpGained")).length;
  }

  /**
   * Retrieves the amount of XP gained in the skill since last login.
   *
   * @param statId int
   * @return int
   */
  public int getPlayerExperience(int statId) {
    return mud.getPlayerExperience(statId);
  }

  /**
   * Retrieves an array of all the skill names.
   *
   * @return String[] -- no guarantee on size or nullability.
   */
  public String[] getSkillNamesLong() {
    return (String[]) this.getMudClientValue("skillNameLong");
  }

  // See issue on GitLab
  // public void keyTyped(int charCode) {
  // //overrideable
  // }
  //
  // public void keyPressed(int charCode) {
  // //overrideable
  // System.out.println("key pressed = " + charCode);
  // }
  //
  // public void keyReleased(int charCode) {
  // //overrideable
  // }

  /** Disables autologin and attempts to logout. No guarantee on success. */
  public void logout() {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(102);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Retrieves the id of the object at the specified coordinates.
   *
   * @param x int
   * @param y int
   * @return int -- -1 if no object at the coordinates.
   */
  public int getObjectAtCoord(int x, int y) {
    int _x = x - mud.getMidRegionBaseX();
    int _y = y - mud.getMidRegionBaseZ();

    int[] ids = getObjectsIds();
    int[] xs = getObjectsX();
    int[] ys = getObjectsZ();

    for (int i = 0; i < ids.length; i++) {
      if (_x == xs[i] && _y == ys[i]) return ids[i];
    }

    return -1;
  }

  /**
   * Uses the specified item in the inventory on the specified ground item.
   *
   * @param x int
   * @param y int
   * @param slotIndex int (use getInventoryItemSlotIndex)
   * @param groundItemId int
   */
  public void useSlotIndexOnGroundItem(int x, int y, int slotIndex, int groundItemId) {
    // if (getInventoryItemCount(itemId) < 1) return;
    // TODO: check if item is on ground
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(53);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(y);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex);
    mud.packetHandler.getClientStream().bufferBits.putShort(groundItemId);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Uses the specified item in the inventory on the specified ground item.
   *
   * @param x int
   * @param y int
   * @param itemId int
   * @param groundItemId int
   */
  public void useItemOnGroundItem(int x, int y, int itemId, int groundItemId) {
    // if (getInventoryItemCount(itemId) < 1) return;
    // TODO: check if item is on ground
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(53);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(y);
    mud.packetHandler.getClientStream().bufferBits.putShort(getInventoryItemSlotIndex(itemId));
    mud.packetHandler.getClientStream().bufferBits.putShort(groundItemId);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Retrieves the server index of the player at the specified coordinates.
   *
   * @param x int
   * @param y int
   * @return int -- returns -1 if no player at specified tile.
   */
  public int getPlayerAtCoord(int x, int y) {
    for (ORSCharacter player : getPlayers()) {
      if (player != null) {

        if (player.serverIndex == getPlayer().serverIndex) continue;

        if ((x == convertX(player.currentX)) && y == convertZ(player.currentZ)) {
          return player.serverIndex;
        }
      }
    }

    return -1;
  }

  /**
   * Retrieves the server index of the specified player name.
   *
   * @param name -- must not be null.
   * @return int -- returns -1 if no player with that name nearby.
   */
  public int getPlayerServerIndexByName(String name) {
    for (ORSCharacter player : getPlayers()) {
      if (player != null) {
        if (player.displayName.equalsIgnoreCase(name)) {
          return player.serverIndex;
        }
      }
    }

    return -1;
  }

  /**
   * Duels the specified player.
   *
   * @param playerServerIndex int
   */
  public void duelPlayer(int playerServerIndex) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(103);
    mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Follows the specified player.
   *
   * @param playerServerIndex int
   */
  public void followPlayer(int playerServerIndex) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(165);
    mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Attacks the specified player.
   *
   * @param playerServerIndex int
   */
  public void attackPlayer(int playerServerIndex) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(171);
    mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Uses the specified item on the player.
   *
   * @param slotIndex int
   * @param playerServerIndex int
   */
  public void useItemOnPlayer(int slotIndex, int playerServerIndex) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(113);
    mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
    mud.packetHandler.getClientStream().bufferBits.putShort(slotIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Casts the specified spell on the specified player.
   *
   * @param spellId int
   * @param playerServerIndex int
   */
  public void castSpellOnPlayer(int spellId, int playerServerIndex) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(229);
    mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
    mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Casts the specified spell on the player.
   *
   * @param spellId int
   */
  public void castSpellOnSelf(int spellId) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(137);
    mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Trades the specified player.
   *
   * @param playerServerIndex -- player index, retrievable with getPlayerServerIndexByName("name").
   */
  public void tradePlayer(int playerServerIndex) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(142);
    mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Retrieves the name of the trade recipient, if we are in a trade.
   *
   * @return String -- no guarantee on nullability.
   */
  public String getTradeRecipientName() {
    return (String) reflector.getObjectMember(mud, "tradeRecipientName");
  }

  /**
   * Whether or not we are currently engaged in a trade.
   *
   * @return boolean
   */
  public boolean isInTrade() {
    return (boolean) reflector.getObjectMember(mud, "showDialogTrade") || isInTradeConfirmation();
  }

  /**
   * Whether or not we are currently in the trade confirmation window.
   *
   * @return boolean
   */
  public boolean isInTradeConfirmation() {
    return (boolean) reflector.getObjectMember(mud, "showDialogTradeConfirm");
  }

  /**
   * Whether or not the recipient is currently accepting the trade.
   *
   * @return boolean
   */
  public boolean isTradeRecipientAccepting() {
    return (boolean) reflector.getObjectMember(mud, "tradeRecipientAccepted");
  }

  /** Declines the trade. */
  public void declineTrade() {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(230);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /** Accepts the trade on the first trade window. */
  public void acceptTrade() {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(55);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /** Accepts the current trade on the final trade window. */
  public void acceptTradeConfirmation() {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(104);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Retrieves items which the player is offering in the trade.
   *
   * @return localTradeItems guaranteed to not be null.
   */
  public List<Item> getLocalTradeItems() {
    List<Item> localTradeItems = new ArrayList<>();

    Item[] _localTradeItems = (Item[]) this.getMudClientValue("trade");
    int localTradeItemsCount = this.getLocalTradeItemsCount();

    localTradeItems.addAll(Arrays.asList(_localTradeItems).subList(0, localTradeItemsCount));

    return localTradeItems;
  }

  /**
   * Retrieves how many items the player is offering in the trade.
   *
   * @return int
   */
  public int getLocalTradeItemsCount() {
    return (int) this.getMudClientValue("tradeItemCount");
  }

  /**
   * Retrieves an array of item counts inside of the current trade window.
   *
   * @return int[] -- no guarantee on size or nullability.
   */
  public int[] getTradeItemsCounts() {
    return (int[]) reflector.getObjectMember(mud, "tradeItemSize");
  }

  /**
   * Retrieves a list of items which your trade recipient is offering.
   *
   * @return recipientTradeItems guaranteed to not be null.
   */
  public List<Item> getRecipientTradeItems() {
    List<Item> recipientTradeItems = new ArrayList<>();

    Item[] _recipientTradeItems = (Item[]) this.getMudClientValue("tradeRecipient");
    int recipientItemsCount = this.getRecipientTradeItemsCount();

    recipientTradeItems.addAll(Arrays.asList(_recipientTradeItems).subList(0, recipientItemsCount));

    return recipientTradeItems;
  }

  /**
   * Retrieves the number of items presented by the trade recipient
   *
   * @return int
   */
  public int getRecipientTradeItemsCount() {
    return (int) reflector.getObjectMember(mud, "tradeRecipientItemsCount");
  }

  /**
   * Will put up the specified items and amounts on the trade window.
   *
   * @param itemIds -- int[]
   * @param amounts -- int[]
   * @param notableItems -- boolean if trade will contain noted items
   * @return boolean -- returns true on success. false on mismatched array lengths or if you do not
   *     have enough of an item.
   */
  public boolean setTradeItems(int[] itemIds, int[] amounts, boolean notableItems) {
    if (itemIds.length != amounts.length) return false;

    for (int i = 0; i < itemIds.length; i++)
      if (amounts[i] > getInventoryItemCount(itemIds[i])) return false;

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(46);
    mud.packetHandler.getClientStream().bufferBits.putByte(itemIds.length);

    for (int i = 0; i < itemIds.length; i++) {
      mud.packetHandler.getClientStream().bufferBits.putShort(itemIds[i]);
      mud.packetHandler.getClientStream().bufferBits.putInt(amounts[i]);
      if (isItemStackable(itemIds[i]) || !notableItems) {
        mud.packetHandler.getClientStream().bufferBits.putShort(0);
      } else {
        // TODO: fix for noted,0 = unnoted 1 = noted
        mud.packetHandler.getClientStream().bufferBits.putShort(1);
      }
    }

    mud.packetHandler.getClientStream().finishPacket();

    // TODO: check if updated on client side with getTradeItems()..

    return true;
  }

  /** Removes all trade items from the current trade window. */
  public void removeAllTradeItems() {
    setTradeItems(new int[] {}, new int[] {}, true);
  }

  /**
   * Toggles auto-login.
   *
   * @param value boolean
   */
  public void setAutoLogin(boolean value) {
    Main.setAutoLogin(value);
  }

  /** Retrieves whether or not auto-login is set. */
  public boolean isAutoLogin() {
    return Main.isAutoLogin();
  }

  /**
   * Toggles the client interlacer, which is for saving CPU cycles.
   *
   * @param interlace boolean
   */
  public void setInterlacer(boolean interlace) {
    if (interlace) log("IdleRSC: Turning On Interlacer", "gre");
    else log("IdleRSC: Turning Off Interlacer", "gre");
    mud.interlace = interlace;
  }

  /**
   * Toggles Custom UI mode, this gives a redesigned in-game UI. The inventory and other tabs stay
   * open after interaction.
   *
   * @param mode boolean
   */
  public void setCustomUiMode(boolean mode) {
    if (mode) log("IdleRSC: Turning On Custom UI mode", "gre");
    else log("IdleRSC: Turning Off Custom UI mode", "gre");
    mud.setCustomUI(mode);
  }

  /** Checks for the Custom UI mode config in mudclient. */
  public boolean getCustomUiMode() {
    return mud.getCustomUI();
  }

  /**
   * Set the in-game scroll level of the client.
   *
   * @param level int - level to set zoom to. in range ~ 50 - 300
   */
  public void setScrollLevel(int level) {
    mud.setLastZoom(level);
  }

  /**
   *
   *
   * <pre>
   * Toggle which types of ground items show up on the screen. In-game setting
   * * Level 0 - Show ALL items
   * * Level 1 - Hide ALL items
   * * Level 3 - Hide Bones only
   * * Level 3 - No Bones or Ashes
   * </pre>
   *
   * @param level int - level to set (0-3)
   */
  public void setGroundItemsToggle(int level) {
    mud.setGroundItemsToggle(level);
  }

  /**
   *
   *
   * <pre>
   * Set Inventory Toggle Mode. If false, the inventory will not close between actions.
   * This was the old android inventory option, but was removed from legitimate game modes.
   * BOTTING ONLY
   * </pre>
   *
   * @param setting boolean - true to keep invent open, false to let it close.
   */
  public void setKeepInventoryOpenMode(boolean setting) {
    if (setting) log("IdleRSC: Turning On Custom UI mode", "gre");
    else log("IdleRSC: Turning Off Custom UI mode", "gre");
    mud.setAndroidInvToggle(!setting);
  }

  /** Retrieves whether or not the interlacer is set. */
  public boolean isInterlacing() {
    return mud.interlace;
  }

  /**
   * Retrieves a list of all items in the inventory.
   *
   * @return _list guaranteed to not be null.
   */
  public List<Item> getInventoryItems() {
    List<Item> _list = new ArrayList<>();

    Item[] inventoryItems = mud.getInventory();
    int inventoryItemCount = this.getInventoryItemCount();

    _list.addAll(Arrays.asList(inventoryItems).subList(0, inventoryItemCount));

    return _list;
  }

  /**
   * Retrieves an array of all the ids of all items in the inventory.
   *
   * @return int[] -- no guarantee on size or nullability.
   */
  public int[] getInventoryItemIds() {
    int[] results = new int[] {};

    int i = 0;
    for (Item d : mud.getInventory()) {
      results = Arrays.copyOf(results, results.length + 1);
      if (d.getItemDef() != null) results[i++] = d.getItemDef().id;
    }

    return results;
  }

  public int[] getInventoryUniqueItemIds() {
    return IntStream.of(this.getInventoryItemIds()).distinct().toArray();
  }

  private void walkToActionSource(
      mudclient mud, int startX, int startZ, int destX, int destZ, boolean walkToEntity) {
    System.out.println(
        "Controller walkToActionSource with "
            + startX
            + ", "
            + startZ
            + ", "
            + destX
            + ", "
            + destZ
            + ", "
            + walkToEntity);

    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    for (StackTraceElement element : stackTraceElements) {
      // System.out.println(element.toString());
    }

    reflector.mudInvoker(mud, "walkToActionSource", startX, startZ, destX, destZ, walkToEntity);
  }

  /**
   * Retrieves the list of all food items in the game.
   *
   * @return int[] -- will never be null.
   */
  public int[] getFoodIds() {
    return foodIds;
  }

  /**
   * Retrieves a list of users on the friends list.
   *
   * @return friendsList guaranteed to never be null.
   */
  public List<String> getFriendList() {
    List<String> friendList = new ArrayList<>();

    int friendListCount = SocialLists.friendListCount;
    String[] _friendList = SocialLists.friendList;

    friendList.addAll(Arrays.asList(_friendList).subList(0, friendListCount));

    return friendList;
  }

  /**
   * Retrieves a list of users on the ignore list.
   *
   * @return ignoreList guaranteed to never be null.
   */
  public List<String> getIgnoreList() {
    List<String> ignoreList = new ArrayList<>();

    int ignoreListCount = SocialLists.ignoreListCount;
    String[] _ignoreList = SocialLists.ignoreList;

    ignoreList.addAll(Arrays.asList(_ignoreList).subList(0, ignoreListCount));

    return ignoreList;
  }

  /**
   * Whether or not the batch progress bar is currently shown on screen.
   *
   * @return boolean
   */
  public boolean isBatching() {
    ProgressBarInterface progressBarInterface =
        (ProgressBarInterface) reflector.getObjectMember(mud, "batchProgressBar");

    if (progressBarInterface == null) return false;

    return progressBarInterface.progressBarComponent.isVisible();
  }

  /**
   * while batching, sleep 1 Game tick. Cancel loop if controller stops or needToMove is true
   * Warning - sleeps 2 ticks before checking if batching.
   *
   * @param checkInventory boolean - true to check for full inventory, false to ignore full invent.
   */
  public void waitForBatching(boolean checkInventory) {
    if (isAuthentic()) return;
    sleep(2 * GAME_TICK);
    while (isRunning()
        && isBatching()
        && !needToMove
        && !shouldSleep
        && (!checkInventory || getInventoryItemCount() < 30)) {
      sleep(GAME_TICK);
    }
  }

  /**
   * Whether or not the server is configured to be authentic. This returns true for Uranium, false
   * for Coleslaw.
   *
   * @return boolean
   */
  public boolean isAuthentic() {
    return !Config.S_WANT_CUSTOM_SPRITES;
  }

  /**
   * Retrieves the specified field object value of the `mudclient`.
   *
   * @param propertyName -- field name
   * @return Object -- null if field does not exist.
   */
  public Object getMudClientValue(String propertyName) {
    return this.reflector.getObjectMember(mud, propertyName);
  }

  /**
   * Retrieves the `mudclient`.
   *
   * @return mudclient
   */
  public mudclient getMud() {
    return this.mud;
  }

  /**
   * Opens the bank and sleeps until the maximum number of ticks is reached or the bank interface is
   * open.
   *
   * @param maxTicks the maximum number of ticks to sleep for
   */
  private void openBank_sleep(int maxTicks) {
    int ticks = 0;

    while (ticks < maxTicks) {
      if (this.isInBank()) return;

      this.sleep(10);
      ticks++;
    }
  }

  /**
   * Opens the bank option menu and sleeps until options menu dialog is visible or the maximum
   * number of ticks is reached. private method called by openBank()
   *
   * @param maxTicks the maximum number of ticks to wait for the option menu to open
   */
  private void openBank_optionMenu_sleep(int maxTicks) {
    int ticks = 0;

    while (ticks < maxTicks) {
      if (this.isInOptionMenu()) return;

      this.sleep(10);
      ticks++;
    }
  }

  /**
   * Will open bank near any bank NPC or bank chest. Uses right click option if possible. Does not
   * return until the bank screen is open. Hence, if no banker/chest is present, this function will
   * block and not return until one is found.
   */
  public void openBank() {

    while (!isInBank() && Main.isRunning()) {

      boolean usedBankerNpc = false;

      if (getObjectAtCoord(58, 731) == 942) { // handle shantay chest
        atObject(58, 731);
        sleep(5000);
        return;
      }

      for (int bankerId : bankerIds) {
        ORSCharacter bankerNpc = getNearestNpcById(bankerId, false);

        if (bankerNpc != null) {
          usedBankerNpc = true;
          int[] coords = getNpcCoordsByServerIndex(bankerNpc.serverIndex);

          walkToAsync(coords[0], coords[1], 0);

          while (!isInBank() && Main.isRunning()) {
            if (getNpcCommand1(95).equals("Bank")) { // Can we right click bank? If so, do that.
              npcCommand1(bankerNpc.serverIndex);
              openBank_sleep(200);
            } else {
              talkToNpc(bankerNpc.serverIndex);
              openBank_optionMenu_sleep(500);
              optionAnswer(0);
              openBank_sleep(200);
            }
          }
        }
      }

      if (!usedBankerNpc) {
        // Use a bank chest
        int[] bankChestId = getNearestObjectById(942);
        if (bankChestId != null) {

          while (!isInBank() && Main.isRunning()) {
            if (currentX() != 59 && currentY() != 731)
              walkToAsync(bankChestId[0], bankChestId[1], 1);

            atObject(bankChestId[0], bankChestId[1]);
            openBank_sleep(600);
            this.sleep(1000);
          }

          // this.sleep(2000);
        }
      }
    }

    this.sleep(640); // to avoid crashing caused by concurrency
  }

  /**
   * Walks the specified coordinates path. Will be blocked by objects such as doors or gates.
   *
   * @param path -- length must be divisible by 2.
   */
  public void walkPath(int[] path) {
    for (int i = 0; i < path.length; i += 2) {
      while ((currentX() != path[i] || currentY() != path[i + 1]) && Main.isRunning()) {
        walkTo(path[i], path[i + 1]);
        sleep(618);
      }
    }
  }

  /**
   * Walks the specified coordinates path, but in reverse. Will be blocked by objects such as doors
   * or gates.
   *
   * @param path -- length must be divisible by 2.
   */
  public void walkPathReverse(int[] path) {
    for (int i = path.length - 2; i >= 0; i -= 2) {
      while ((currentX() != path[i] || currentY() != path[i + 1]) && Main.isRunning()) {
        walkTo(path[i], path[i + 1]);
        sleep(618);
      }
    }
  }

  /**
   * Logs text to the console, bot log window, and OpenRSC applet.
   *
   * @param text String
   */
  public void log(String text) {
    log(text, "gre");
  }

  /**
   * Logs text to the console, bot log window, and OpenRSC applet with the specified @col@.
   *
   * @param text String
   * @param rsTextColor -- the color of the text, such as "red" or "cya". Do not wrap in @'s.
   */
  public void log(String text, String rsTextColor) {
    Main.log(text); // main log does sysout
    displayMessage("@" + rsTextColor + "@" + text);
  }

  /**
   * Whether or not the player is currently sleeping.
   *
   * @return boolean
   */
  public boolean isSleeping() {
    return mud.getIsSleeping();
  }

  public boolean getShouldSleep() {
    return shouldSleep;
  }

  public void setShouldSleep(boolean shouldSleep) {
    this.shouldSleep = shouldSleep;
  }

  /**
   * If fatigue is greater or equal to `fatigueToSleepAt`, this will commence the sleep process and
   * IdleRSC will fill in the answer from the OCR. Has no effect on Coleslaw.
   *
   * @param quitOnNoSleepingBag -- whether or not to logout and stop the script if no sleeping bag
   *     is present. If no sleeping bag is present and this is false, this function has no effect.
   */
  public void sleepHandler(boolean quitOnNoSleepingBag) {
    if (!isLoggedIn()) return;
    if (getInventoryItemCount(1263) < 1 && quitOnNoSleepingBag) {
      log(getPlayerName() + " has no sleeping bag! Logging out...");
      setAutoLogin(false);
      logout();
      stop();
    } else {
      itemCommand(1263);
      sleep(1280);
      while (isSleeping()) sleep(100);
    }
    shouldSleep = false;
  }

  /**
   * Retrieves the coordinates of the nearest bank, based on your current position. WARNING: Do not
   * use this with the webwalker (walkTowards). Use getNearestWebwalkBank() instead
   *
   * @return int[] -- [x, y] with the coordinates of the bank. Never returns null.
   */
  public int[] getNearestBank() {
    int[] bankX = {220, 150, 103, 220, 216, 283, 503, 582, 566, 588, 129, 440, 327, 89};
    int[] bankY = {635, 504, 511, 365, 450, 569, 452, 576, 600, 754, 3543, 495, 552, 694};
    int prevX = 10000;
    int prevY = 10000;
    int index = 0;
    for (int i = 0; i < bankX.length; i++) {
      if (Math.abs((bankX[i] - currentX())) < prevX && Math.abs((bankY[i] - currentY())) < prevY) {
        prevX = Math.abs(bankX[i] - currentX());
        prevY = Math.abs(bankY[i] - currentY());
        index = i;
      }
    }
    int[] bankCoords = {bankX[index], bankY[index]};
    return bankCoords;
  }

  /**
   * Buys the specified itemId from the shop.
   *
   * @param itemId int
   * @param amount int
   * @return boolean -- returns true on success. False if not in shop, or if shop has no stock.
   */
  public boolean shopBuy(int itemId, int amount) {
    // TODO: check if enough coins in inventory, return false if not enough.
    if (!isInShop() || getShopItemCount(itemId) < 1) return false;

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(236);
    mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
    mud.packetHandler.getClientStream().bufferBits.putShort(getShopItemCount(itemId));
    mud.packetHandler.getClientStream().bufferBits.putShort(amount);
    mud.packetHandler.getClientStream().finishPacket();

    return true;
  }

  /**
   * Sells the specified itemId to the shop.
   *
   * @param itemId int
   * @param amount int
   * @return boolean -- returns true on success. False if not in shop, if shop does not accept item,
   *     or not enough items in inventory.
   */
  public boolean shopSell(int itemId, int amount) {
    // TODO: check if item in inventory
    if (!isInShop() || getShopItemCount(itemId) == -1 || getInventoryItemCount(itemId) < amount)
      return false;

    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(221);
    mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
    mud.packetHandler.getClientStream().bufferBits.putShort(getShopItemCount(itemId));
    mud.packetHandler.getClientStream().bufferBits.putShort(amount);
    mud.packetHandler.getClientStream().finishPacket();

    return true;
  }

  /**
   * Draws a gradient box at the specified coordinates.
   *
   * @param x int
   * @param y int
   * @param width int
   * @param height int
   * @param topColor -- RGB "HTML" Color Example: 0x36E2D7
   * @param bottomColor -- RGB "HTML" Color Example: 0x36E2D7
   */
  public void drawVerticalGradient(
      int x, int y, int width, int height, int topColor, int bottomColor) {
    mud.getSurface().drawVerticalGradient(x, y, width, height, topColor, bottomColor);
  }

  /**
   * Draws a box at the specified coordinates with the specified color and transparency. Must be
   * used inside paintInterrupt().
   *
   * @param x int
   * @param y int
   * @param width int
   * @param height int
   * @param color -- RGB "HTML" Color Example: 0x36E2D7
   * @param transparency -- must be between 0 and 255
   */
  public void drawBoxAlpha(int x, int y, int width, int height, int color, int transparency) {
    int clampedTransparency = Math.min(255, Math.max(0, transparency));
    mud.getSurface().drawBoxAlpha(x, y, width, height, color, clampedTransparency);
  }

  /**
   * Draws a hollow rectangle at the specified coordinates. Must be used inside paintInterrupt().
   *
   * @param x int
   * @param y int
   * @param width int
   * @param height int
   * @param color -- RGB "HTML" Color Example: 0x36E2D7
   */
  public void drawBoxBorder(int x, int y, int width, int height, int color) {
    mud.getSurface().drawBoxBorder(x, width, y, height, color); // rearranged per source!
  }

  /**
   * Draws a circle at the specified coordinates with specified radius, color, and transparency.
   * Must be used inside paintInterrupt().
   *
   * @param x int
   * @param y int
   * @param radius int
   * @param color -- RGB "HTML" Color Example: 0x36E2D7
   * @param transparency -- must be between 0 and 255
   * @param dummy int
   */
  public void drawCircle(int x, int y, int radius, int color, int transparency, int dummy) {
    int clampedTransparency = Math.min(255, Math.max(0, transparency));
    mud.getSurface().drawCircle(x, y, radius, color, clampedTransparency, dummy);
  }

  /**
   * Draws a horizontal line at the specified coordinates with the specified width. Must be used
   * inside paintInterrupt().
   *
   * @param x int
   * @param y int
   * @param width int
   * @param color -- RGB "HTML" Color Example: 0x36E2D7
   */
  public void drawLineHoriz(int x, int y, int width, int color) {
    mud.getSurface().drawLineHoriz(x, y, width, color);
  }

  /**
   * Draws a vertical line at the specified coordinates with the specified height.
   *
   * @param x int
   * @param y int
   * @param height int
   * @param color -- RGB "HTML" Color Example: 0x36E2D7
   */
  public void drawLineVert(int x, int y, int height, int color) {
    mud.getSurface().drawLineVert(x, y, color, height); // rearranged per source!
  }

  /**
   * Draws a progress bar. Must be used inside paintInterrupt()
   *
   * @param current int -- Current value to calculate the progress bar from
   * @param maximum int -- Maximum value to calculate the progress bar from
   * @param bgColor int -- Color of the progress bar's background. RGB "HTML" Color Example:
   *     0x36E2D7
   * @param fgColor int -- Color of the progress bar's foreground. RGB "HTML" Color Example:
   *     0x36E2D7
   * @param borderColor int -- Color of the progress bar's border. RGB "HTML" Color Example:
   *     0x36E2D7
   * @param x int -- X coordinate of the top left of the progress bar
   * @param y int -- Y coordinate of the top left of the progress bar
   * @param width int -- Width of the progress bar
   * @param height int -- Height of the progress bar
   * @param showPercentage boolean -- Show the percentage on the bar
   * @param showValues boolean -- Show the current and maximum values on the bar
   */
  public void drawProgressBar(
      int current,
      int maximum,
      int bgColor,
      int fgColor,
      int borderColor,
      int x,
      int y,
      int width,
      int height,
      boolean showPercentage,
      boolean showValues) {
    int currentPercent = (current * 100) / maximum > 100 ? 100 : (current * 100) / maximum;
    int currentBarWidth = currentPercent >= 100 ? width : (width * currentPercent) / 100;
    drawBoxAlpha(x, y, width, height, bgColor, 255);
    drawBoxAlpha(x, y, currentBarWidth, height, fgColor, 255);
    drawBoxBorder(x, y, width, height, borderColor);
    if (showPercentage) {
      drawShadowText(
          String.valueOf(currentPercent + "%"),
          (width / 2) + x,
          y + (height / 2) - 3,
          0xffffff,
          1,
          true);
    }
    if (showValues) {
      drawShadowText(
          String.valueOf(current),
          x + 4 + (this.getStringWidth(String.valueOf(current), 1) / 2),
          y + (height / 2) - 3,
          0xffffff,
          1,
          true);
      drawShadowText(
          String.valueOf(maximum),
          x + width - (5 * String.valueOf(maximum).length()),
          y + (height / 2) - 3,
          0xffffff,
          1,
          true);
    }
  }

  /**
   * Draws text at the specified coordinates. Must be used inside paintInterrupt().
   *
   * @param str -- you may use @col@ colors here.
   * @param x int
   * @param y int
   * @param color -- RGB "HTML" Color Example: 0x36E2D7
   * @param fontSize -- 1 or greater
   */
  public void drawString(String str, int x, int y, int color, int fontSize) {
    mud.getSurface().drawString(str, x, y, color, Math.max(1, fontSize));
  }

  /**
   * Draws text that is centered at the specified coordinates. Must be used inside paintInterrupt().
   *
   * @param str String -- String to draw
   * @param x int -- X coordinate
   * @param y int -- Y coordinate
   * @param color int -- String color. RGB "HTML" Color Example: 0x36E2D7
   * @param fontSize int -- 1 or greater
   */
  public void drawCenteredString(String str, int x, int y, int color, int fontSize) {
    int textWidth = mud.getSurface().stringWidth(fontSize, str);
    int textHeight = mud.getSurface().fontHeight(fontSize);
    x = x - textWidth / 2;
    y = y + textHeight / 2;
    this.drawString(str, x, y, color, fontSize);
  }

  /**
   * Draws shadow text at the specified coordinates. Must be used inside paintInterrupt().
   *
   * @param text -- you may use @col@ colors here.
   * @param x int
   * @param y int
   * @param textColor int -- RGB "HTML" color Example: 0x36E2D7
   * @param fontSize int -- 1 or greater
   * @param center boolean
   */
  public void drawShadowText(
      String text, int x, int y, int textColor, int fontSize, boolean center) {
    mud.getSurface().drawShadowText(text, x, y, textColor, Math.max(1, fontSize), center);
  }

  /**
   * Draws the sprite of an item at specified coordinates. Must be used inside paintInterrupt().
   *
   * @param itemId int --
   * @param x int -- X coordinate
   * @param y int -- Y coordinate
   * @param scalePercent int -- Percent to scale the sprite. 100 for normal, less for smaller, and
   *     greater for larger.
   * @param mirrorX boolean -- Mirror the sprite across it's X axis.
   */
  public void drawItemSprite(int itemId, int x, int y, int scalePercent, boolean mirrorX) {
    ItemDef def = EntityHandler.getItemDef(itemId);
    Sprite sprite = mud.spriteSelect(def);
    mud.getSurface()
        .drawSpriteClipping(
            sprite,
            x,
            y,
            getItemSpriteScaledWidth(itemId, scalePercent),
            getItemSpriteScaledHeight(itemId, scalePercent),
            def.getPictureMask(),
            0,
            def.getBlueMask(),
            mirrorX,
            0,
            1);
  }

  /**
   * Returns the width of the sprite for an item id
   *
   * @param itemId int -- Item id
   * @return int -- Width
   */
  public int getItemSpriteWidth(int itemId) {
    if (itemId > -1) {
      ItemDef def = EntityHandler.getItemDef(itemId);
      Sprite sprite = mud.spriteSelect(def);
      return sprite.getWidth();
    }
    return 0;
  }

  /**
   * Returns the width of the sprite for an item id
   *
   * @param itemId int -- Item id
   * @param scalePercent int -- Percent to scale the sprite up or down
   * @return int -- Scaled width
   */
  public int getItemSpriteScaledWidth(int itemId, int scalePercent) {
    if (itemId > -1) {
      ItemDef def = EntityHandler.getItemDef(itemId);
      Sprite sprite = mud.spriteSelect(def);
      return (int)
          ((sprite.getWidth() < 16 ? sprite.getWidth() * 22 : sprite.getWidth())
              * (double) scalePercent
              / 100.0);
    }
    return 0;
  }

  /**
   * Returns the height of the sprite for an item id
   *
   * @param itemId int -- Item id
   * @return int -- Height
   */
  public int getItemSpriteHeight(int itemId) {
    if (itemId > -1) {
      ItemDef def = EntityHandler.getItemDef(itemId);
      Sprite sprite = mud.spriteSelect(def);
      return sprite.getHeight();
    }
    return 0;
  }

  /**
   * Returns the height of the sprite for an item id
   *
   * @param itemId int -- Item id
   * @param scalePercent int -- Percent to scale the sprite up or down
   * @return int -- Scaled height
   */
  public int getItemSpriteScaledHeight(int itemId, int scalePercent) {
    if (itemId > -1) {
      ItemDef def = EntityHandler.getItemDef(itemId);
      Sprite sprite = mud.spriteSelect(def);
      return (int)
          ((sprite.getHeight() < 16 ? sprite.getHeight() * 22 : sprite.getHeight())
              * (double) scalePercent
              / 100.0);
    }
    return 0;
  }

  /**
   * Returns the width of a string of a specified font size.
   *
   * @param string String -- Text to get the width from
   * @param fontSize int -- Font size
   * @return int
   */
  public int getStringWidth(String string, int fontSize) {
    return mud.getSurface().stringWidth(fontSize, string);
  }

  /**
   * Returns the height of a specified font size.
   *
   * @param fontSize int -- Font size to get height from
   * @return int
   */
  public int getStringHeight(int fontSize) {
    if (fontSize < 1 || fontSize > 7) return -1;
    int[] fontHeights = {12, 12, 14, 14, 16, 20, 23};
    return fontHeights[fontSize - 1];
  }

  /**
   * Sets the left-hand status indicator text value.
   *
   * @param rstext -- You may use @col@ colors here.
   */
  public void setStatus(String rstext) {
    DrawCallback.setStatusText(rstext);
  }

  /**
   * Retrieves the name of the currently logged in player.
   *
   * @return String
   */
  public String getPlayerName() {
    if (this.getPlayer() != null) return this.getPlayer().accountName;

    return "";
  }

  /**
   * Retrieves the direction of the ORSCharacter (NPC or player.)
   *
   * @param c -- character
   * @return ORSCharacterDirection
   */
  public ORSCharacterDirection getCharacterDirection(ORSCharacter c) {
    return (ORSCharacterDirection) reflector.getObjectMember(c, "direction");
  }

  /**
   * Whether or not the left-hand status indicator is enabled.
   *
   * @return boolean
   */
  public boolean getShowStatus() {
    return showStatus;
  }

  /**
   * Toggles the left-hand status indicator.
   *
   * @param b int
   */
  public void setShowStatus(boolean b) {
    showStatus = b;
  }

  /**
   * Whether or not the the left-hand coordinates indicator is enabled.
   *
   * @return boolean
   */
  public boolean getShowCoords() {
    return showCoords;
  }

  /**
   * Toggles the left-hand coordinates indicator.
   *
   * @param b int
   */
  public void setShowCoords(boolean b) {
    showCoords = b;
  }

  /**
   * Whether or not the left-hand XP counter is enabled.
   *
   * @return boolean
   */
  public boolean getShowXp() {
    return showXp;
  }

  /**
   * Toggles the left-hand XP counter.
   *
   * @param b int
   */
  public void setShowXp(boolean b) {
    showXp = b;
  }

  /**
   * Whether or not bot painting is enabled.
   *
   * @return boolean
   */
  public boolean getShowBotPaint() {
    return showBotPaint;
  }

  /**
   * Toggle bot painting (such as progress reports.) This does not disable client graphics.
   *
   * @param _showBotPaint int
   */
  public void setBotPaint(boolean _showBotPaint) {
    if (_showBotPaint) log("IdleRSC: Turning On Bot Paint", "gre");
    else log("IdleRSC: Turning Off Bot Paint", "gre");
    showBotPaint = _showBotPaint;
  }

  /**
   * Whether or not the tile is reachable in the current map segment.
   *
   * @param x int
   * @param y int
   * @param includeTileEdges -- whether or not the edges of the tile are permitted. Such as picking
   *     up an item on a table -- you can't walk on top of the table, but you can reach the edges.
   * @return true if the tile is reachable, false if blocked.
   */
  public boolean isReachable(int x, int y, boolean includeTileEdges) {
    int[] pathX = new int[8000];
    int[] pathZ = new int[8000];
    int startX = removeOffsetX(currentX());
    int startZ = removeOffsetZ(currentY());

    int _x = removeOffsetX(x);
    int _y = removeOffsetZ(y);

    try {
      return mud.getWorld().findPath(pathX, pathZ, startX, startZ, _x, _x, _y, _y, includeTileEdges)
          >= 1;
    } catch (Exception e) {
      this.sleep(1000); // in case we are loading a new segment
      return false;
    }
  }

  /**
   * If running on an authentic server, this stops the script and outputs a message about
   * compatibility.
   */
  public void quitIfAuthentic() {
    if (this.isAuthentic()) {
      this.log(
          "This script is not designed to run on authentic servers (\"Uranium\".) This is only supported on Coleslaw.");
      this.stop();
    }
  }

  /**
   * Retrieves the coordinates of the specified NPC.
   *
   * @param serverIndex int
   * @return int[] -- [x, y]. Returns [-1, -1] on no NPC present.
   */
  public int[] getPlayerCoordsByServerIndex(int serverIndex) {
    ORSCharacter[] players = (ORSCharacter[]) reflector.getObjectMember(mud, "players");

    for (ORSCharacter player : players) {
      if (player != null && player.serverIndex == serverIndex) {
        return new int[] {this.convertX(player.currentX), this.convertZ(player.currentZ)};
      }
    }

    // TODO: return null for consistency and update scripts.
    return new int[] {-1, -1};
  }

  /**
   * Whether or not draw/graphics is currently enabled.
   *
   * @return boolean
   */
  public boolean isDrawEnabled() {
    return drawing;
  }

  /**
   * Toggle draw/graphics.
   *
   * @param drawing_ boolean - what draw state to set it
   * @param pauseTicks int - how long to wait before reverting graphics selection. 0 to not
   *     derefresh.
   */
  public synchronized void setDrawing(boolean drawing_, int pauseTicks) {
    drawing = drawing_;
    if (pauseTicks > 0) {
      sleep(pauseTicks);
      drawing = !drawing_;
    }
  }

  /**
   * Whether or not render 3D is currently enabled.
   *
   * @return boolean
   */
  public boolean isRender3DEnabled() {
    return mud.isRender3DEnabled();
  }

  /**
   * Toggle render 3D.
   *
   * @param render3D boolean - what render state to set.
   */
  public void setRender3D(boolean render3D) {
    mud.setRender3D(render3D);
  }

  /**
   * Call this method to trigger a move within scripts. Sets the need to move flag.
   *
   * @param needToMove_ boolean - true if need to move, false otherwise.
   */
  public void setNeedToMove(Boolean needToMove_) {
    needToMove = needToMove_;
  }

  /**
   * Call this method to check the state of needToMove. Script should call moveCharacter() to
   * prevent logout, if this is true.
   *
   * @return boolean - true if needToMove, false otherwise.
   */
  public boolean getNeedToMove() {
    return needToMove;
  }

  /** Call this method from within a script to move the character one tile away to a random tile. */
  public void moveCharacter() {
    int x = currentX();
    int y = currentY();

    if (isReachable(x + 1, y, false)) walkToAsync(x + 1, y, 0);
    else if (isReachable(x - 1, y, false)) walkToAsync(x - 1, y, 0);
    else if (isReachable(x, y + 1, false)) walkToAsync(x, y + 1, 0);
    else if (isReachable(x, y - 1, false)) walkToAsync(x, y - 1, 0);
    sleep(640); // was 1280

    walkTo(x, y);
    setNeedToMove(false);
  }

  /**
   * If standing next to a closed door or gate within the specified radius, open it.
   *
   * @param radius -- within how many tiles to find said door
   */
  public void openNearbyDoor(int radius) {
    int x = this.currentX();
    int y = this.currentY();

    int objectId = -1;
    int wallObjectId = -1;

    for (int id : this.closedObjectDoorIds) {
      int[] coords = this.getNearestObjectById(id);
      if (coords != null) {
        if (this.distance(x, y, coords[0], coords[1]) <= radius) {
          objectId = id;
          break;
        }
      }
    }

    if (objectId != -1) {
      int[] coords = this.getNearestObjectById(objectId);
      if (coords != null && this.distance(x, y, coords[0], coords[1]) <= radius) {
        this.atObject(coords[0], coords[1]);
        this.sleep(250);
        return;
      }
    }

    for (int id : this.closedWallDoorIds) {
      int[] coords = this.getNearestWallObjectById(id);
      if (coords != null) {
        if (this.distance(x, y, coords[0], coords[1]) <= radius) {
          wallObjectId = id;
          break;
        }
      }
    }

    if (wallObjectId != -1) {
      int[] coords = this.getNearestWallObjectById(wallObjectId);
      if (coords != null && this.distance(x, y, coords[0], coords[1]) <= radius) {
        this.openDoor(coords[0], coords[1]);
        this.sleep(250);
        return;
      }
    }
  }

  /**
   * Opens all reachable doors with the same id that match the ids in the radius.
   *
   * @param radius -- within how many tiles to find said door
   */
  public void openNearbyDoors(int radius) {
    int x = this.currentX();
    int y = this.currentY();

    int objectId = -1;
    int wallObjectId = -1;

    for (int id : this.closedObjectDoorIds) {
      int[] coords = this.getNearestObjectById(id);
      if (coords != null) {
        if (this.distance(currentX(), currentY(), coords[0], coords[1]) <= radius) {
          objectId = id;
          break;
        }
      }
    }

    if (objectId != -1) {
      int[] coords = this.getNearestObjectById(objectId);

      if (coords != null && Main.isRunning()) {
        this.atObject(coords[0], coords[1]);
        this.sleep(250);
        coords = this.getNearestObjectById(objectId);
      }
      return;
    }

    for (int id : this.closedWallDoorIds) {
      int[] coords = this.getNearestWallObjectById(id);
      if (coords != null) {
        if (this.distance(currentX(), currentY(), coords[0], coords[1]) <= radius) {
          wallObjectId = id;
          break;
        }
      }
    }

    if (wallObjectId != -1) {
      int[] coords = this.getNearestWallObjectById(wallObjectId);

      while (coords != null && Main.isRunning()) {
        this.openDoor(coords[0], coords[1]);
        this.sleep(250);
        coords = this.getNearestWallObjectById(wallObjectId);
      }
    }
  }

  public int[] getMudMouseCoords() {
    return new int[] {mud.getMouseX(), mud.getMouseY()};
  }

  /**
   * Returns the height, in pixels, of the game window.
   *
   * @return int
   */
  public int getGameHeight() {
    return mud.getGameHeight();
  }

  /**
   * Returns the width, in pixels, of the game window.
   *
   * @return int
   */
  public int getGameWidth() {
    return mud.getGameWidth();
  }

  /**
   * Checks if the NPC is currently talking.
   *
   * @param serverIndex the index of the NPC on the server
   * @return true if the NPC is talking, false otherwise
   */
  public boolean isNpcTalking(int serverIndex) {
    ORSCharacter npc = this.getNpc(serverIndex);

    if (npc == null) return false;

    return npc.messageTimeout > 0;
  }

  /**
   * Retrieves the examine text of the specified wall object id.
   *
   * @param objId int
   * @return String -- guaranteed to not be null
   */
  public String getWallObjectExamineText(int objId) {
    try {
      return EntityHandler.getDoorDef(objId).getDescription();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves the examine text of the specified wall object id.
   *
   * @param objId int
   * @return String -- guaranteed to not be null
   */
  public String getWallObjectName(int objId) {
    try {
      return EntityHandler.getDoorDef(objId).getName();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Retrieves the number of spells (total)
   *
   * @return the number of spells
   */
  public int getSpellsCount() {
    int result = 0;
    try {
      for (; result < 999; result++) EntityHandler.getSpellDef(result).getName();
      return result;
    } catch (Exception e) {
      return result;
    }
  }

  /**
   * Retrieves the names of all spells.
   *
   * @return an array of strings containing the names of all spells
   */
  public String[] getSpellNames() {
    int spellsCount = getSpellsCount();
    String[] result = new String[spellsCount];

    for (int i = 0; i < spellsCount; i++) result[i] = EntityHandler.getSpellDef(i).getName();

    return result;
  }

  /**
   * Retrieves the spell level for the given spell ID.
   *
   * @param spellId the ID of the spell
   * @return the required level for the spell, or -1 if the spell ID is invalid
   */
  public int getSpellLevel(int spellId) {
    try {
      return EntityHandler.getSpellDef(spellId).getReqLevel();
    } catch (Exception e) {
      return -1;
    }
  }

  /**
   * Gets the spell type for the given spell ID.
   *
   * @param spellId the ID of the spell
   * @return the spell type, or -1 if an exception occurs
   */
  public int getSpellType(int spellId) {
    try {
      return EntityHandler.getSpellDef(spellId).getSpellType();
    } catch (Exception e) {
      return -1;
    }
  }

  /**
   * Retrieves the set of spell runes required for a given spell ID.
   *
   * @param spellId the ID of the spell
   * @return the set of spell runes required, or null if an exception occurs
   */
  public Set<Entry<Integer, Integer>> getSpellRunes(int spellId) {
    try {
      return EntityHandler.getSpellDef(spellId).getRunesRequired();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Determines if the player can cast a specific spell (high enough stat level and has runes)
   *
   * @param spellId the ID of the spell
   * @return true if the player can cast the spell, false otherwise
   */
  public boolean canCastSpell(int spellId) {
    if (this.getCurrentStat(6) < this.getSpellLevel(spellId)) return false;

    Set<Entry<Integer, Integer>> ingredients = this.getSpellRunes(spellId);
    for (Entry<Integer, Integer> entry : ingredients) {
      int runeId = entry.getKey();
      int runeAmount = entry.getValue();

      if (this.getInventoryItemCount(runeId) < runeAmount) return false;
    }

    return true;
  }

  /**
   * Returns an array of quest names.
   *
   * @return an array of quest names
   */
  public String[] getQuestNames() {
    return ((String[]) reflector.getObjectMember(mud, "questNames"));
  }

  /**
   * Returns the number of quests (total)
   *
   * @return the number of quests
   */
  public int getQuestsCount() {
    return this.getQuestNames().length;
  }

  /**
   * Retrieves the quest stage for the specified quest ID.
   *
   * @param questId the ID of the quest
   * @return the quest stage for the specified quest ID
   */
  public int getQuestStage(int questId) {
    if (questId >= this.getQuestsCount()) return 0;

    return ((int[]) reflector.getObjectMember(mud, "questStages"))[questId];
  }

  /**
   * Determines if a quest is complete.
   *
   * @param questId the ID of the quest
   * @return true if the quest is complete, false otherwise
   */
  public boolean isQuestComplete(int questId) {
    return this.getQuestStage(questId) == -1;
  }

  /**
   * Returns the amount of quest points the player has.
   *
   * @return int
   */
  public int getQuestPoints() {
    try {
      Field f = mud.getClass().getDeclaredField("questPoints");
      f.setAccessible(true);
      return (int) f.get(mud);
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * Adds a friend to the user's friend list.
   *
   * @param username the username of the friend to be added
   */
  public void addFriend(String username) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(195);
    mud.packetHandler.getClientStream().bufferBits.putString(username);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Adds the specified username to the ignore list
   *
   * @param username the username to add to the ignore list
   */
  public void addIgnore(String username) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(132);
    mud.packetHandler.getClientStream().bufferBits.putString(username);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Removes a friend from the user's friend list.<br>
   * Does not update on client side
   *
   * @param username the username of the friend to be removed
   */
  public void removeFriend(String username) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(167);
    mud.packetHandler.getClientStream().bufferBits.putNullThenString(username, 110);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Removes the specified player from the ignore list. <br>
   * Does not update on client side
   *
   * @param username the username of the user to remove from the ignore list
   */
  public void removeIgnore(String username) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(241);
    mud.packetHandler.getClientStream().bufferBits.putNullThenString(username, -78);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Sends a private message to a specified user.
   *
   * @param username the username of the recipient
   * @param message the message to be sent
   */
  public void sendPrivateMessage(String username, String message) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(218);
    mud.packetHandler.getClientStream().bufferBits.putString(username);
    RSBufferUtils.putEncryptedString(mud.packetHandler.getClientStream().bufferBits, message);
    mud.packetHandler.getClientStream().finishPacket();
  }

  public ORSCharacter getTargetedNpc() {
    ORSCharacter player = this.getPlayer();
    ORSCharacter npc = null;

    // Set npc to ranged/mage target
    if (player.attackingNpcServerIndex > 0) {
      npc = this.getNpc(player.attackingNpcServerIndex);

      // Set npc to melee target
    } /*
       * else if (MELEE STUFF) {
       * npc = YEP;
       * }
       */
    if (npc != null) return npc;
    return null;
  }

  /**
   * Returns the id of the last killed npc
   *
   * @return int
   */
  public int getLastNpcKilledId() {
    return mud.getLastNpcKilledId();
  }

  /**
   * Returns the kill count of the last killed npc
   *
   * @return int
   */
  public int getLastNpcKilledCount() {
    return mud.getStatKills3();
  }

  /**
   * Returns the total kill count of all npcs
   *
   * @return int
   */
  public int getTotalKillCount() {
    return mud.getStatKills2();
  }

  /**
   * Returns the server index of the NPC which is currently blocking you. Useful for scripts where
   * an NPC blocking you is bad. Only works if your character is facing the NPC directly.
   *
   * @param npcId -- necessary because you don't want to accidentally attack the wrong npc.
   * @return the NPC's server index. returns -1 on no NPC blocking.
   */
  public int getBlockingNpcServerIndex(int npcId) {
    ORSCharacterDirection dir = this.getCharacterDirection(this.getPlayer());
    int x = 0, y = 0;

    if (this.isInCombat()) return -1;

    switch (dir) {
      case NORTH:
        y = -1;
        break;
      case NORTH_EAST:
        x = -1;
        y = -1;
        break;
      case EAST:
        x = -1;
        break;
      case SOUTH_EAST:
        x = -1;
        y = 1;
        break;
      case SOUTH:
        y = 1;
        break;
      case SOUTH_WEST:
        x = 1;
        y = 1;
        break;
      case WEST:
        x = 1;
        break;
      case NORTH_WEST:
        x = 1;
        y = -1;
        break;
    }

    for (ORSCharacter npc : this.getNpcs()) {
      if (npc.npcId == npcId) {
        if (this.getNpcCoordsByServerIndex(npc.serverIndex)[0] == (this.currentX() + x)
            && this.getNpcCoordsByServerIndex(npc.serverIndex)[1] == (this.currentY() + y))
          return npc.serverIndex;
        // if(npc.currentX == (mud.getLocalPlayerX() + x)
        // && npc.currentZ == (mud.getLocalPlayerZ() + y))
        // return npc.serverIndex;
      }
    }

    return -1;
  }

  /**
   * Returns the NPC object of the NPC at the specified coordinates. If there is no NPC at those
   * coordinates, it returns nothing.
   *
   * @param x int
   * @param y int
   * @return int
   */
  public ORSCharacter getNpcAtCoords(int x, int y) {
    int[] coords = new int[] {x, y};
    for (ORSCharacter npc : this.getNpcs()) {
      if (this.getNpcCoordsByServerIndex(npc.serverIndex)[0] == x
          && this.getNpcCoordsByServerIndex(npc.serverIndex)[1] == y) {
        return npc;
      }
    }

    return null;
  }

  /** If on tutorial island, skips tutorial island. */
  public void skipTutorialIsland() {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(84);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Internal function used for sleeping.
   *
   * @param word String
   */
  public void sendSleepWord(String word) {
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(45);
    mud.packetHandler.getClientStream().bufferBits.putByte(1);
    mud.packetHandler.getClientStream().bufferBits.putNullThenString(word, 116);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Walks to the wall object and then interacts with it.
   *
   * @param x int
   * @param y int
   */
  public void atWallObject(int x, int y) {
    int opcode = 14; // opcode was switched
    int direction = getWallObjectDirectionAtCoord(x, y);

    reflector.mudInvoker(
        mud, "walkToWall", this.removeOffsetX(x), this.removeOffsetZ(y), direction);
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(opcode);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(y);
    mud.packetHandler.getClientStream().bufferBits.putByte(direction);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Walks to the wall object and then interacts with it (secondary interaction.)
   *
   * @param x int
   * @param y int
   */
  public void atWallObject2(int x, int y) {
    int opcode = 127; // opcode was switched
    int direction = getWallObjectDirectionAtCoord(x, y);

    reflector.mudInvoker(
        mud, "walkToWall", this.removeOffsetX(x), this.removeOffsetZ(y), direction);
    while (mud.packetHandler.getClientStream().hasFinishedPackets()) sleep(1);
    mud.packetHandler.getClientStream().newPacket(opcode);
    mud.packetHandler.getClientStream().bufferBits.putShort(x);
    mud.packetHandler.getClientStream().bufferBits.putShort(y);
    mud.packetHandler.getClientStream().bufferBits.putByte(direction);
    mud.packetHandler.getClientStream().finishPacket();
  }

  /**
   * Internal function used to grant the ability for normal accounts to access the developer ID
   * menus.
   */
  public void toggleViewId() {
    if (isLoggedIn()) {
      int groupId = getPlayer().groupID;

      if (groupId == 10) { // if viewId off, change to on
        if (Config.C_SIDE_MENU_OVERLAY) {
          temporaryToggleSideMenu = true;
          orsc.Config.C_SIDE_MENU_OVERLAY = false; // bugfix for coleslaw flickering
        }
        DrawCallback.setToggleOnViewId(true);
      } else if (groupId == 9) { // if viewId on, change to off
        DrawCallback.setToggleOnViewId(false);
        if (temporaryToggleSideMenu) {
          temporaryToggleSideMenu = false;
          orsc.Config.C_SIDE_MENU_OVERLAY = true; // bugfix for coleslaw flickering
        }
      }
    }
  }

  /**
   * (setBatchBars(boolean)) Method can be called to toggle ON Batch Bars in the openrsc client
   * config. <br>
   * This is necessary for scripts utilizing batch bars.
   */
  public void setBatchBarsOn() {
    setBatchBars(true);
  }

  /**
   * (use setBatchBars(boolean)) Method can be called to toggle OFF Batch Bars in the openrsc client
   * config for native scripts utilizing batch bars.
   */
  public void setBatchBarsOff() {
    setBatchBars(false);
  }

  /**
   * Method can be called to toggle Batch Bars in the openrsc client config for native scripts
   * utilizing batch bars.
   */
  public void setBatchBars(boolean value) {
    if (isLoggedIn() && !isAuthentic()) {
      Config.C_BATCH_PROGRESS_BAR = value;
    }
  }

  /**
   * Check if batch bars are enabled
   *
   * @return boolean - true if batch bars are enabled, false otherwise or if on uranium.
   */
  public boolean getBatchBarsOn() {
    if (isAuthentic()) return false;
    return Config.C_BATCH_PROGRESS_BAR;
  }

  /**
   * Display String "Hr:Min:Sec" version of milliseconds long int.
   *
   * @param milliseconds long timeInMilliseconds
   * @return String Hr:Min:Sec
   */
  public String msToString(long milliseconds) {
    long sec = milliseconds / 1000;
    long min = sec / 60;
    long hour = min / 60;
    sec %= 60;
    min %= 60;
    DecimalFormat twoDigits = new DecimalFormat("00");

    return twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec);
  }

  /**
   * Display String "Hr:Min" version of milliseconds long int.
   *
   * @param milliseconds long timeInMilliseconds
   * @return String Hr:Min
   */
  public String msToShortString(long milliseconds) {
    long sec = milliseconds / 1000;
    long min = sec / 60;
    sec %= 60;
    min %= 60;
    DecimalFormat twoDigits = new DecimalFormat("00");

    return twoDigits.format(min) + ":" + twoDigits.format(sec);
  }

  /**
   * Shows Time to Completions in hours, minutes, and seconds. ("01:23:45")
   *
   * <p>credit to chomp for toTimeToCompletion (from AA_Script) (totalBars, barsInBank, startTime)
   *
   * @param processed int totalItemsUsed
   * @param remaining int totalItemsUnused
   * @param time startTime System.currentTimeMillis when bot started
   * @return String Hr:Min:Sec
   */
  public String timeToCompletion(final int processed, final int remaining, final long time) {
    if (processed == 0) {
      return "0:00:00";
    }

    final double seconds = (System.currentTimeMillis() - time) / 1000.0;
    final double secondsPerItem = seconds / processed;
    final long ttl = (long) (secondsPerItem * remaining);
    return String.format("%d:%02d:%02d", ttl / 3600, (ttl % 3600) / 60, (ttl % 60));
  }

  /**
   * Shows short Time to Completions in hours only. ("1234")
   *
   * <p>credit to chomp for toTimeToCompletion (from AA_Script) (totalBars, barsInBank, startTime)
   *
   * @param processed int totalItemsUsed
   * @param remaining int totalItemsUnused
   * @param time startTime System.currentTimeMillis when bot started
   * @return String Hr
   */
  public String shortTimeToCompletion(final int processed, final int remaining, final long time) {
    if (processed == 0) {
      return "0";
    }

    final double seconds = (System.currentTimeMillis() - time) / 1000.0;
    final double secondsPerItem = seconds / processed;
    final long ttl = (long) (secondsPerItem * remaining);
    return String.format("%d", ttl / 3600);
  }

  /** Show Recovery Question Menu */
  public void showRecoveryDetailsMenu() {
    mud.setShowRecoveryDialogue(true);
  }

  /**
   * Hide Recovery Question Menu (Run this every message hook callback to prevent corrupt packets
   * showing this menu)
   */
  public void hideRecoveryDetailsMenu() {
    mud.setShowRecoveryDialogue(false);
  }

  /** Show Details Menu */
  public void showContactDetailsMenu() {
    mud.setShowContactDialogue(true);
  }

  /**
   * Hides Details Menu (Run this every message hook callback to prevent corrupt packets showing
   * this menu)
   */
  public void hideContactDetailsMenu() { // int opcode = 253;
    mud.setShowContactDialogue(false);
  }
}
