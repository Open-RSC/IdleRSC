package compatibility.sbot;

import bot.Main;
import callbacks.MessageCallback;
import controller.Controller;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import orsc.ORSCharacter;

/**
 * This is the SBot API, implemented as a compatibility abstraction layer.
 *
 * @author Dvorak
 */
public abstract class Script {
  Controller controller = null;
  private Thread scriptThread;

  /**
   * Used internally by the bot to set {@link controller.Controller}, as that is what adapts SBot to
   * IdleRSC.
   *
   * @param _controller
   */
  public void setController(Controller _controller) {
    controller = _controller;
  }

  /**
   * The start function for the script.
   *
   * @param command -- usually the name of the script.
   * @param parameters -- script parameters.
   */
  public void start(String command, String[] parameters) {
    System.out.println("If you see this, your script did not call the start function.");
  }
  /**
   * Not used, but remains for compatability.
   *
   * @return
   */
  public String[] getCommands() {
    return new String[0];
  }
  /** The init function for the script -- almost never used. */
  public void init() {}
  /**
   * Interrupt which is called when a server message is sent to the client.
   *
   * @param message
   */
  public void ServerMessage(String message) {
    Main.logMethod("ServerMessage", message);
    // do nothing. this is an interrupt.
  }
  /**
   * Interrupt which is called when a chat message is sent to the client.
   *
   * @param message
   */
  public void ChatMessage(String message) {
    Main.logMethod("ChatMessage", message);
    // do nothing. this is an interrupt.
  }
  /**
   * Interrupt which is called when a quest message is sent to the cilent.
   *
   * @param message
   */
  public void NPCMessage(String message) {
    Main.logMethod("NPCMessage", message);
    // do nothing. this is an interrupt.
  }
  /**
   * <b> NOT IMPLEMENTED YET </b>
   *
   * @param PlayerID
   */
  public void TradeRequest(int PlayerID) {
    Main.logMethod("TradeRequest", PlayerID);
    // do nothing. this is an interrupt.
    // THIS IS NOT IMPLEMENTED.
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public void Accepted() {
    Main.logMethod("Accepted");
    // do nothing. this is an interrupt.
    // THIS IS NOT IMPLEMENTED.
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public void TradeOver() {
    Main.logMethod("TradeOver");
    // do nothing. this is an interrupt.
    // THIS IS NOT IMPLEMENTED.
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public void KeyPressed(int key) {
    Main.logMethod("KeyPressed");
    // THIS IS NOT IMPLEMENTED.
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public void KeyReleased(int key) {
    Main.logMethod("KeyReleased");
    // THIS IS NOT IMPLEMENTED.
  }

  /**
   * Retrieves the timestamp of the last chat message to the client.
   *
   * <p><b>Not 2038 safe</b>.
   *
   * @return int -- timestamp in seconds
   */
  public int LastChatter() {
    Main.logMethod("LastChatter");
    return MessageCallback.getSbotLastChatter();
  }
  /**
   * Retrieves the username of the individual who sent the last message to the client.
   *
   * @return String -- username
   */
  public String LastChatterName() {
    Main.logMethod("LastChatterName");
    return MessageCallback.getSbotLastChatterName();
  }

  /**
   * Retrieves how many of `item` is inside the bank.
   *
   * @param item -- item id
   * @return int
   */
  public int BankCount(int item) {
    Main.logMethod("BankCount", item);
    if (!Running()) return -1;

    return controller.getBankItemCount(item);
  }
  /**
   * Converts a string to an integer.
   *
   * @param st
   * @return int
   */
  public int StrToInt(String st) {
    return Integer.parseInt(st);
  }

  /**
   * Converts an integer to a string.
   *
   * @param num
   * @return String
   */
  public String IntToStr(int num) {
    return String.valueOf(num);
  }

  /**
   * Retrieves the X coordinates of a npc id.
   *
   * @param id -- npc id, not a server index.
   * @return int
   */
  public int NPCX(int id) {
    Main.logMethod("NPCX", id);
    return controller.getNpcCoordsByServerIndex(id)[0];
  }
  /**
   * Retrieves the Y coordinates of a npc id.
   *
   * @param id -- npc id, not a server index.
   * @return int
   */
  public int NPCY(int id) {
    Main.logMethod("NPCY", id);
    return controller.getNpcCoordsByServerIndex(id)[1];
  }
  /**
   * Whether or not the trade recipient is accepting the current trade offer.
   *
   * @return
   */
  public boolean IsAccepted() {
    Main.logMethod("IsAccepted");
    return controller.isTradeRecipientAccepting();
  }

  /** Exits the bot. */
  public void Quit() {
    Main.logMethod("Quit");
    System.exit(1);
  }

  /**
   * Retrieves the distance between the player and the respective coordinates.
   *
   * @param x
   * @param y
   * @return
   */
  public int Distance(int x, int y) {
    return GetDistance(GetX(), GetY(), x, y);
  }

  /**
   * The current status index of the trade.
   *
   * <p>0: no trade 1: 1st trade window 2: 2nd trade window
   *
   * @return
   */
  public int TradeStatus() {
    Main.logMethod("TradeStatus");
    if (controller.isInTrade()) {
      if (controller.isInTradeConfirmation()) return 2;
      return 1;
    } else {
      return 0;
    }
  }

  /** Accepts the trade, as presented. */
  public void AcceptTrade() {
    Main.logMethod("AcceptTrade");
    controller.acceptTrade();
  }
  /** Accepts the 2nd trade window, as presented. */
  public void AcceptTrade2() {
    Main.logMethod("AcceptTrade2");
    controller.acceptTradeConfirmation();
  }
  /** Declines the trade. */
  public void DeclineTrade() {
    Main.logMethod("DeclineTrade");
    controller.declineTrade();
  }

  /**
   * Puts up the respective item id, of the respective amount.
   *
   * @param item
   * @param amount
   */
  public void TradeArray(int item, int amount) {
    Main.logMethod("TradeArray", item, amount);
    TradeArray(new int[] {item}, new int[] {amount});
  }
  /**
   * Puts up the respective item ids, of the respective amounts.
   *
   * @param item -- multiple ids
   * @param amount -- multiple amounts
   */
  public void TradeArray(int[] item, int[] amount) {
    Main.logMethod("TradeArray", item, amount);
    controller.setTradeItems(item, amount, false);
  }
  /** Not used, but remains for compatability; trades will automatically update. */
  void UpdateTrade() {
    Main.logMethod("UpdateTrade");
    // THIS IS NOT IMPLEMENTED.
  }
  /** Removes all items from the trade window. */
  public void ResetTrade() {
    Main.logMethod("ResetTrade");
    controller.removeAllTradeItems();
  }
  /**
   * Retrieves whether or not the specified tile is currently reachable by the player.
   *
   * @param x
   * @param y
   * @return
   */
  public boolean CanReach(int x, int y) {
    return controller.isReachable(x, y, false);
  }
  /** Plays a "beep" noise via AWT. */
  public void Beep() {
    Main.logMethod("Beep");
    Toolkit.getDefaultToolkit().beep();
  }
  /**
   * Sleeps for the specified number of milliseconds.
   *
   * @param ticks
   */
  public void Wait(int ticks) {
    Main.logMethod("Wait", ticks);
    try {
      Thread.sleep(ticks);
    } catch (InterruptedException e) {
      e.toString();
    }
  }
  /**
   * Displays a message to the user via the client chat window.
   *
   * @param message -- text (may contain @col@ etc)
   * @param type -- message type (index specified in orsc.enumerations.MessageType)
   */
  public void DisplayMessage(String message, int type) {
    Main.logMethod("DisplayMessage", message, type);
    controller.displayMessage(message, type);
  }
  /**
   * Sets the fight mode to the specified style.
   *
   * @param style
   */
  public void SetFightMode(int style) {
    Main.logMethod("SetFightMode", style);
    controller.setFightMode(style);
  }
  /**
   * Retrieves the current fight mode.
   *
   * @return
   */
  public int GetFightMode() {
    Main.logMethod("GetFightMode");
    WaitForLoad();
    return controller.getFightMode();
  }
  /**
   * Prints a message, followed by newline, to the system console.
   *
   * @param message
   */
  public void Println(String message) {
    Main.logMethod("Println", message);
    System.out.println(message);
  }
  /**
   * Prints a message, with no newline, to the system console.
   *
   * @param message
   */
  public void Print(String message) {
    Main.logMethod("Print", message);
    System.out.print(message);
  }
  /**
   * Prints a nicely formatted message to the system console.
   *
   * @param message
   */
  public void SexyPrint(String message) {
    Main.logMethod("SexyPrint", message);
    for (int i = 0; i < message.length() - 1; i++) {
      System.out.print(message.charAt(i));
      Wait(1);
    }
    System.out.println(message.substring(message.length() - 1));
  }
  /**
   * Walks to the specified tile, and does not give up until it is there.
   *
   * @param x
   * @param y
   */
  public void ForceWalk(int x, int y) {
    Main.logMethod("ForceWalk", x, y);
    controller.walkTo(x, y, 0, true, true);
  }
  /**
   * Walks to the specified tile, and does not give up until it is there -- asynchronous.
   *
   * @param x
   * @param y
   */
  public void ForceWalkNoWait(int x, int y) {
    Main.logMethod("ForceWalkNoWait", x, y);
    controller.walkToAsync(x, y, 0);
  }
  /**
   * Performs the 1st command action available on the object at the specified tile.
   *
   * @param x
   * @param y
   */
  public void AtObject(int x, int y) {
    Main.logMethod("AtObject", x, y);
    boolean result = controller.atObject(x, y);

    if (!result)
      controller.displayMessage("@red@ERROR: @whi@No object found at: @yel@" + x + ", " + y, 3);
  }
  /**
   * Performs the 1st command action available on the object at the specified tile.
   *
   * @param coords
   */
  public void AtObject(int[] coords) {
    Main.logMethod("AtObject", coords);
    AtObject(coords[0], coords[1]);
  }
  /**
   * Performs the 2nd command action available on the object at the specified tile.
   *
   * @param x
   * @param y
   */
  public void AtObject2(int x, int y) {
    Main.logMethod("AtObject2", x, y);
    boolean result = controller.atObject2(x, y);

    if (!result)
      controller.displayMessage("@red@ERROR: @whi@No object found at: @yel@" + x + ", " + y, 3);
  }
  /**
   * Performs the 2nd command action available on the object at the specified tile.
   *
   * @param coords
   */
  public void AtObject2(int[] coords) {
    Main.logMethod("AtObject2", coords);
    AtObject2(coords[0], coords[1]);
  }
  /**
   * Walks to the specified tile.
   *
   * @param x
   * @param y
   */
  public void Walk(int x, int y) {
    Main.logMethod("Walk", x, y);
    controller.walkTo(x, y, 0, false, false);
  }
  /**
   * Some hacky SBot method for walking to a tile involving wait -- not recommended for use.
   *
   * @param x
   * @param y
   * @param step
   */
  public void Walk(int x, int y, int step) {
    Main.logMethod("Walk", x, y, step);
    WaitForLoad();
    while (GetX() != x && GetY() != y) {
      Walk(x, y);
      long T = TickCount();
      while (TickCount() - T < step && GetX() != x && GetY() != y) Wait(1);
    }
  }
  /**
   * Walks to the specified tile -- asynchronous.
   *
   * @param x
   * @param y
   */
  public void WalkNoWait(int x, int y) {
    Main.logMethod("WalkNoWait", x, y);
    controller.walkToAsync(x, y, 0);
  }
  /**
   * Retrieves the current system time in milliseconds.
   *
   * @return
   */
  public long TickCount() {
    return System.currentTimeMillis();
  }
  /**
   * Within the specified 2 points, walks to an empty tile within that rectangle.
   *
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   */
  public void WalkEmpty(int x1, int y1, int x2, int y2) {
    Main.logMethod("WalkEmpty", x1, y1, x2, y2);
    boolean FoundEmpty = false;
    for (int horizontal = x1; horizontal <= x2; horizontal++) {
      for (int vertical = y1; vertical <= y2; vertical++) {
        if (EmptyTile(horizontal, vertical)) {
          FoundEmpty = true;
        }
      }
    }
    if (FoundEmpty) {
      boolean GoneTo = false;
      while (!GoneTo) {
        WaitForLoad();
        int TempX = Rand(x1, x2);
        int TempY = Rand(y1, y2);
        if (EmptyTile(TempX, TempY)) {
          Walk(TempX, TempY);
          GoneTo = true;
        }
      }
    } else {
      System.out.println("Warning: No Empty Tiles Found");
      Walk(Rand(x1, x2), Rand(y1, y2));
    }
  }
  /**
   * Retrieves the server index of the player at the specified coordinates.
   *
   * @param x
   * @param y
   * @return
   */
  public int PlayerAt(int x, int y) {
    Main.logMethod("PlayerAt", x, y);
    return controller.getPlayerAtCoord(x, y);
  }
  /**
   * Whether or not the specified tile is reachable or obstructed.
   *
   * @param x
   * @param y
   * @return
   */
  public boolean Obstructed(int x, int y) {
    if (ObjectAt(x, y) != -1) return true;
    if (PlayerAt(x, y) != -1) return true;
    return !CanReach(x, y);
  }
  /**
   * Within the specified 2 points, walks to an empty tile within that rectangle -- asynchronous.
   *
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   */
  public void WalkEmptyNoWait(int x1, int y1, int x2, int y2) {
    Main.logMethod("WalkEmptyNoWait", x1, y1, x2, y2);
    WaitForLoad();
    boolean FoundEmpty = false;
    for (int horizontal = x1; horizontal <= x2; horizontal++) {
      for (int vertical = y1; vertical <= y2; vertical++) {
        if (EmptyTile(horizontal, vertical)) {
          FoundEmpty = true;
        }
      }
    }
    if (FoundEmpty) {
      boolean GoneTo = false;
      while (!GoneTo) {
        WaitForLoad();
        int TempX = Rand(x1, x2);
        int TempY = Rand(y1, y2);
        if (EmptyTile(TempX, TempY)) {
          Walk(TempX, TempY);
          GoneTo = true;
        }
      }
    } else {
      System.out.println("Warning: No Empty Tiles Found");
      Walk(Rand(x1, x2), Rand(y1, y2));
    }
  }
  /**
   * Retrieves the current X coordinate of the player.
   *
   * @return
   */
  public int GetX() {
    Main.logMethod("GetX");
    return controller.currentX();
  }
  /**
   * Retrieves the current Y coordinate of the player.
   *
   * @return
   */
  public int GetY() {
    Main.logMethod("GetY");
    return controller.currentY();
  }
  /**
   * Returns a number between `low` and `higher`.
   *
   * @param lower
   * @param higher
   * @return
   */
  public int Rand(int lower, int higher) {
    return ThreadLocalRandom.current().nextInt(lower, higher);
  }
  /**
   * Sends the specified message to the chat.
   *
   * @param message
   */
  public void Say(String message) {
    Main.logMethod("Say", message);
    controller.chatMessage(message);
  }
  /**
   * Whether or not the specified tile is empty.
   *
   * @param x
   * @param y
   * @return
   */
  public boolean EmptyTile(int x, int y) {
    Main.logMethod("EmptyTile", x, y);
    return controller.isTileEmpty(x, y);
  }
  /**
   * Casts the specified spell index against the specified player index.
   *
   * @param player
   * @param spell
   */
  public void MagicPlayer(int player, int spell) {
    Main.logMethod("MagicPlayer", player, spell);
    controller.castSpellOnPlayer(spell, player);
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public void UseOnPlayer(int player, int slot) {
    Main.logMethod("UseOnPlayer", player, slot);
    // THIS IS NOT IMPLEMENTED.
  }
  /**
   * Attacks the specified player index.
   *
   * @param player
   */
  public void AttackPlayer(int player) {
    Main.logMethod("AttackPlayer", player);
    controller.attackPlayer(player);
  }
  /**
   * Sends a duel request to the specified player.
   *
   * @param player
   */
  public void DuelPlayer(int player) {
    Main.logMethod("DuelPlayer", player);
    controller.duelPlayer(player);
  }
  /**
   * Trades the specified player.
   *
   * @param player
   */
  public void TradePlayer(int player) {
    Main.logMethod("TradePlayer", player);
    controller.tradePlayer(player);
  }
  /**
   * Follows the specified player.
   *
   * @param player
   */
  public void FollowPlayer(int player) {
    Main.logMethod("FollowPlayer", player);
    controller.followPlayer(player);
  }
  /**
   * Casts the specified magic index against the specified item id on the specified tile.
   *
   * @param x
   * @param y
   * @param item
   * @param spell
   */
  public void MagicItem(int x, int y, int item, int spell) {
    Main.logMethod("MagicItem", x, y, item, spell);
    WaitForLoad();
    controller.castSpellOnGroundItem(spell, item, x, y);
  }
  /**
   * Uses the specified inventory item on the specified ground item on the specified tile.
   *
   * @param x
   * @param y
   * @param type -- inventory item id
   * @param item -- ground item id
   */
  public void UseOnItem(int x, int y, int type, int item) {
    Main.logMethod("UseOnItem", x, y, type, item);
    WaitForLoad();
    controller.useItemOnGroundItem(x, y, item, type);
  }
  /**
   * Picks up the specified item on the ground.
   *
   * @param x
   * @param y
   * @param type
   * @return
   */
  public boolean TakeItem(int x, int y, int type) {
    Main.logMethod("TakeItem", x, y, type);
    int beforeCount, afterCount;

    beforeCount = controller.getInventoryItemCount(type);
    controller.pickupItem(x, y, type, false, false);

    Wait(618); // wait 1 tick

    afterCount = controller.getInventoryItemCount(type);
    return afterCount > beforeCount;
  }
  /**
   * Casts the specified spell index against the specified npc server index.
   *
   * @param id -- server index
   * @param spell
   */
  public void MagicNPC(int id, int spell) {
    Main.logMethod("MagicNPC", id, spell);
    controller.castSpellOnNpc(id, spell);
  }
  /**
   * Uses the specified item against the specified npc server index.
   *
   * @param id -- server index
   * @param item
   */
  public void UseOnNPC(int id, int item) {
    Main.logMethod("UseOnNPC", id, item);
    controller.useItemOnNpc(id, item);
  }
  /**
   * Talks to the specified npc server index.
   *
   * @param serverIndex
   */
  public void TalkToNPC(int serverIndex) {
    Main.logMethod("TalkToNPC", serverIndex);
    controller.talkToNpc(serverIndex);
  }
  /**
   * Attacks the specified npc server index.
   *
   * @param serverIndex
   */
  public void AttackNPC(int serverIndex) {
    Main.logMethod("AttackNPC", serverIndex);
    controller.attackNpc(serverIndex);
  }
  /**
   * Thieves the specified npc server index.
   *
   * @param serverIndex
   */
  public void ThieveNPC(int serverIndex) {
    Main.logMethod("ThieveNPC", serverIndex);
    controller.thieveNpc(serverIndex);
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public void MagicDoor(int x, int y, int dir, int spell) {
    Main.logMethod("MagicDoor", x, y, dir, spell);
    // THIS IS NOT IMPLEMENTED.
  }
  /**
   * Uses the specified item on the specified door tile.
   *
   * @param x
   * @param y
   * @param dir -- not used, put anything here.
   * @param item -- item id
   */
  public void UseOnDoor(int x, int y, int dir, int item) {
    Main.logMethod("UseOnDoor", x, y, dir, item);
    WaitForLoad();
    controller.useItemOnWall(x, y, controller.getInventoryItemSlotIndex(item));
  }
  /**
   * Opens the door at the specified tile.
   *
   * @param x
   * @param y
   * @param dir -- not used, put anything here.
   */
  public void OpenDoor(int x, int y, int dir) {
    Main.logMethod("OpenDoor", x, y, dir);
    controller.openDoor(x, y);
  }
  /**
   * Closes the door at the specified tile.
   *
   * @param x
   * @param y
   * @param dir -- not used, put anything here.
   */
  public void CloseDoor(int x, int y, int dir) {
    Main.logMethod("CloseDoor", x, y, dir);
    controller.closeDoor(x, y);
  }
  /**
   * Casts the specified spell index against the object at the specified tile.
   *
   * @param x
   * @param y
   * @param spell
   */
  public void MagicObject(int x, int y, int spell) {
    Main.logMethod("MagicObject", x, y, spell);
    WaitForLoad();
    controller.castSpellOnObject(spell, x, y);
  }
  /**
   * Uses the specified item from the specified slot on the specified object tile.
   *
   * @param x
   * @param y
   * @param slotId
   */
  public void UseOnObject(int x, int y, int slotId) {
    Main.logMethod("UseOnObject", x, y, slotId);
    WaitForLoad();
    controller.useItemSlotOnObject(x, y, slotId);
  }
  /** Casts the specified spell id on the player. */
  public void Magic(int spell) {
    Main.logMethod("Magic", spell);
    WaitForLoad();
    controller.castSpellOnSelf(spell);
  }
  /**
   * Whether or not an NPC option menu is present.
   *
   * @return
   */
  public boolean QuestMenu() {
    Main.logMethod("QuestMenu");
    return controller.isInOptionMenu();
  }
  /**
   * Answers with the specified option answer when in a NPC dialogue.
   *
   * @param answer
   */
  public void Answer(int answer) {
    Main.logMethod("Answer", answer);
    Wait(3000);
    controller.optionAnswer(answer);
  }
  /**
   * Casts the specified spell on the specified slot.
   *
   * @param slot
   * @param spell
   */
  public void MagicInventory(int slot, int spell) {
    Main.logMethod("MagicInventory", slot, spell);
    WaitForLoad();
    controller.castSpellOnInventoryItem(spell, slot);
  }
  /**
   * Uses the item at `slot1` on `slot2`.
   *
   * @param slot1
   * @param slot2
   */
  public void UseWithInventory(int slot1, int slot2) {
    Main.logMethod("UseWithInventory", slot1, slot2);
    controller.useItemOnItemBySlot(slot1, slot2);
  }
  /**
   * Removes the specified item at the specified slot, if equipped.
   *
   * @param slot
   */
  public void Remove(int slot) {
    Main.logMethod("Remove", slot);
    WaitForLoad();
    controller.unequipItem(slot);
  }
  /**
   * Wields the specified item at the specified slot, if equipped.
   *
   * @param slot
   */
  public void Wield(int slot) {
    Main.logMethod("Wield", slot);
    WaitForLoad();
    controller.equipItem(slot);
  }
  /**
   * Uses the primary command on the item at the specified slot.
   *
   * @param slot
   */
  public void Use(int slot) {
    Main.logMethod("Use", slot);
    WaitForLoad();
    controller.itemCommandBySlot(slot);
  }
  /**
   * Drops one of the item at the specified slot.
   *
   * @param slot
   */
  public void Drop(int slot) {
    Main.logMethod("Drop", slot);
    controller.dropItem(slot);
  }
  /**
   * Deposits the specified amount of the specified item.
   *
   * @param type
   * @param amount
   */
  public void Deposit(int type, int amount) {
    Main.logMethod("Deposit", type, amount);
    controller.depositItem(type, amount);
  }
  /**
   * Withdraws the specified amount of the specified item.
   *
   * @param type
   * @param amount
   */
  public void Withdraw(int type, int amount) {
    Main.logMethod("Withdraw", type, amount);
    controller.withdrawItem(type, amount);
  }
  /**
   * Retrieves how many of the specified item id you have in your inventory.
   *
   * @param type
   * @return
   */
  public int InvCount(int type) {
    Main.logMethod("InvCount", type);
    return controller.getInventoryItemCount(type);
  }
  /**
   * Retrieves how many items are in your inventory.
   *
   * @return
   */
  public int InvCount() {
    Main.logMethod("InvCount");
    return controller.getInventoryItemCount();
  }
  /** Attempts to logout. */
  public void Logout() {
    Main.logMethod("Logout");
    WaitForLoad();
    controller.logout();
  }
  /**
   * Retrieves the coordinates of the closest object in the specified array of object ids.
   *
   * @param type
   * @return
   */
  public int[] GetNearestObject(int[] type) {
    Main.logMethod("GetNearestObject", type);
    WaitForLoad();

    int closestDistance = 999999;
    int[] result = new int[] {-1, -1};
    for (int itemId : type) {
      int[] tmp = GetNearestObject(itemId);
      int distance = GetDistance(this.GetX(), this.GetY(), tmp[0], tmp[1]);

      if (distance < closestDistance) {
        closestDistance = distance;
        result = tmp;
      }
    }

    return result;
  }
  /**
   * Retrieves the coordinates of the closest object id.
   *
   * @param type
   * @return
   */
  public int[] GetNearestObject(int type) {
    Main.logMethod("GetNearestObject", type);
    int[] result = controller.getNearestObjectById(type);
    if (result == null) {
      int[] badCoord = {-1, -1};
      return badCoord;
    }

    return result;
  }

  /** <b> NOT IMPLEMENTED YET </b> */
  public int[] GetNearestObject(int type, int x1, int y1, int x2, int y2) {
    // THIS IS NOT IMPLEMENTED.
    // This would be really easy to implement but I'm lazy and someone else can do it :)
    return new int[] {-1, -1};
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public int[] GetNearestObject(int[] type, int x1, int y1, int x2, int y2) {
    // THIS IS NOT IMPLEMENTED.
    // This would be really easy to implement but I'm lazy and someone else can do it :)
    return new int[] {-1, -1};
  }
  /**
   * Retrieves the server index of the nearest npc of the specified npc id.
   *
   * @param type
   * @return
   */
  public int GetNearestNPC(int type) {
    Main.logMethod("GetNearestNpc", type);
    ORSCharacter npc = controller.getNearestNpcById(type, false);

    if (npc == null) return -1;

    return npc.serverIndex;
  }
  /**
   * Retrieves the server index of the nearest npc from the specified npc ids.
   *
   * @param type
   * @return
   */
  public int GetNearestNPC(int[] type) {
    Main.logMethod("GetNearestNPC", type);
    ORSCharacter npc = controller.getNearestNpcByIds(type, false);

    if (npc == null) return -1;

    return npc.serverIndex;
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public int GetNearestNPC(int type, int x1, int y1, int x2, int y2) {
    // THIS IS NOT IMPLEMENTED.
    // This would be really easy to implement but I'm lazy and someone else can do it :)
    return -1;
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public int GetNearestNPC(int[] type, int x1, int y1, int x2, int y2) {
    // THIS IS NOT IMPLEMENTED.
    // This would be really easy to implement but I'm lazy and someone else can do it :)
    return -1;
  }
  /**
   * Retrieves the coordinates of the nearest item id.
   *
   * @param type
   * @return
   */
  public int[] GetNearestItem(int type) {
    Main.logMethod("GetNearestItem", type);
    int[] result = controller.getNearestItemById(type);

    if (result == null) return new int[] {-1, -1};

    return result;
  }
  /**
   * Retrieves the coordinates of the nearest item from the list of ids.
   *
   * @param type
   * @return
   */
  public int[] GetNearestItem(int[] type) {
    Main.logMethod("GetNearestItem", type);
    return controller.getNearestItemById(type[0]);
  }
  /** Closes the current shop window. */
  public void CloseShop() {
    Main.logMethod("CloseShop");
    controller.closeShop();
  }
  /** Closes the current bank window. */
  public void CloseBank() {
    Main.logMethod("CloseBank");
    controller.closeBank();
  }
  /**
   * Buys 1 of the specified item id from the shop window.
   *
   * @param item
   */
  public void Buy(int item) {
    Main.logMethod("Buy", item);
    controller.shopBuy(item);
  }
  /**
   * Sells 1 of the specified item id from the shop window.
   *
   * @param item
   */
  public void Sell(int item) {
    Main.logMethod("Sell", item);
    controller.shopSell(item);
  }
  /**
   * Whether or not the bank window is currently open.
   *
   * @return
   */
  public boolean Bank() {
    Main.logMethod("Bank");
    return controller.isInBank();
  }
  /**
   * whether or not a shop window is currently open.
   *
   * @return
   */
  public boolean Shop() {
    Main.logMethod("Shop");
    return controller.isInShop();
  }
  /**
   * Whether or not the door at the specified coordinates is open or closed.
   *
   * <p>1 = open 2 = closed
   *
   * @param x
   * @param y
   * @param dir -- not used, put anything in here.
   * @return
   */
  public int DoorAt(int x, int y, int dir) {
    Main.logMethod("DoorAt", x, y, dir);
    WaitForLoad();

    if (controller.isDoorOpen(x, y)) {
      return 1; // 1 = open
    } else {
      return 2; // 2 = closed. dumb i know.
    }
  }
  /**
   * Whether or not an item of the specified type is at the specified coordinates.
   *
   * @param x
   * @param y
   * @param type
   * @return
   */
  public boolean ItemAt(int x, int y, int type) {
    Main.logMethod("ItemAt", x, y, type);
    WaitForLoad();
    return controller.isItemAtCoord(x, y, type);
  }
  /**
   * Retrieves the object id at the specified coordinates.
   *
   * @param x
   * @param y
   * @return
   */
  public int ObjectAt(int x, int y) {
    Main.logMethod("ObjectAt", x, y);
    WaitForLoad();
    return controller.getObjectAtCoord(x, y);
  }
  /**
   * Retrieves the server index of the specified player name.
   *
   * @param name
   * @return
   */
  public int PlayerByName(String name) {
    Main.logMethod("PlayerByName", name);
    return controller.getPlayerServerIndexByName(name);
  }
  /**
   * Retrieves the experience in the specified skill.
   *
   * @param statno
   * @return
   */
  public int GetExperience(int statno) {
    Main.logMethod("GetExperience", statno);
    return controller.getStatXp(statno);
  }
  /**
   * Retrieves the current boosted/degraded stat of the specified skill.
   *
   * @param statno
   * @return
   */
  public int GetCurrentStat(int statno) {
    Main.logMethod("GetCurrentStat", statno);
    return controller.getCurrentStat(statno);
  }
  /**
   * Retrieves the skill level of the specified skill.
   *
   * @param statno
   * @return
   */
  public int GetStat(int statno) {
    Main.logMethod("GetStat", statno);
    return controller.getBaseStat(statno);
  }
  /**
   * Retrieves the HP level of the specified player index.
   *
   * @param id
   * @return
   */
  public int PlayerHP(int id) {
    Main.logMethod("PlayerHP", id);
    if (controller.getPlayer(id) == null) return -1;

    return controller.getPlayer(id).healthCurrent;
  }
  /**
   * Whether or not we are currently in combat.
   *
   * @return
   */
  public boolean InCombat() {
    Main.logMethod("InCombat");
    return controller.isInCombat();
  }
  /**
   * Whether or not the specified player is in combat.
   *
   * @param id
   * @return
   */
  public boolean PlayerInCombat(int id) {
    Main.logMethod("PlayerInCombat", id);
    return controller.isPlayerInCombat(id);
  }
  /**
   * Retrieves the X coordinate of the specified player index.
   *
   * @param id
   * @return
   */
  public int PlayerX(int id) {
    Main.logMethod("PlayerX", id);
    if (controller.getPlayer(id) == null) return -1;

    return controller.convertX(controller.getPlayer(id).currentX);
  }
  /**
   * Retrieves the Y coordinate of the specified player index.
   *
   * @param id
   * @return
   */
  public int PlayerY(int id) {
    Main.logMethod("PlayerY", id);
    if (controller.getPlayer(id) == null) return -1;

    return controller.convertZ(controller.getPlayer(id).currentZ);
  }
  /**
   * Retrieves the X coordinate of where the player index is currently headed.
   *
   * @param id
   * @return
   */
  public int PlayerDestX(int id) {
    Main.logMethod("PlayerDestX", id);
    if (controller.getPlayer(id) == null) return -1;

    int[] xs = controller.getPlayer(id).waypointsX;
    int length = controller.getPlayer(id).waypointsX.length;
    int index = length - 1 > 0 ? length - 1 : 0;

    return controller.convertX(xs[index]);
  }
  /**
   * Retrieves the Y coordinate of where the player index is currently headed.
   *
   * @param id
   * @return
   */
  public int PlayerDestY(int id) {
    Main.logMethod("PlayerDestY", id);
    if (controller.getPlayer(id) == null) return -1;

    int[] ys = controller.getPlayer(id).waypointsZ;
    int length = controller.getPlayer(id).waypointsZ.length;
    int index = length - 1 > 0 ? length - 1 : 0;

    return controller.convertZ(ys[index]);
  }
  /**
   * Retrieves the last spoken chat message.
   *
   * @return
   */
  public String LastChatMessage() {
    Main.logMethod("LastChatMessage");
    return MessageCallback.getSbotLastChatMessage();
  }
  /** Not used, but remains for compatability. */
  public void ResetLastChatMessage() {
    Main.logMethod("ResetLastChatMessage");
  }
  /**
   * Retrieves the last quest message.
   *
   * @return
   */
  public String LastNPCMessage() {
    Main.logMethod("LastNPCMessage");
    return MessageCallback.getSbotLastNPCMessage();
  }
  /** Not used, but remains for compatability. */
  public void ResetLastNPCMessage() {
    Main.logMethod("ResetLastNPCMessage");
  }
  /**
   * Retrieves the last server message.
   *
   * @return
   */
  public String LastServerMessage() {
    Main.logMethod("LastServerMessage");
    return MessageCallback.getSbotLastServerMessage();
  }
  /** Not used, but remains for compatability. */
  public void ResetLastServerMessage() {
    Main.logMethod("ResetLastServerMessage");
  }
  /**
   * Waits until a server message appears.
   *
   * @param timeout
   */
  public void WaitForServerMessage(int timeout) {
    Main.logMethod("WaitForServerMessage", timeout);
    boolean newMessage = false;
    String currentMessage = LastServerMessage();
    long T = System.currentTimeMillis();
    while (!newMessage && System.currentTimeMillis() - T <= timeout) {
      if (currentMessage.equals(LastServerMessage())) {
        Wait(10);
      } else {
        newMessage = true;
      }
    }
  }
  /**
   * Whether or not the specified substring is inside the last server message.
   *
   * @param st
   * @return
   */
  public boolean InLastServerMessage(String st) {
    Main.logMethod("InLastServerMessage", st);
    if (LastServerMessage() == null) return false;

    return LastServerMessage().contains(st);
  }
  /**
   * Retrieves the slot index of the specified item id.
   *
   * @param type
   * @return
   */
  public int FindInv(int type) {
    Main.logMethod("FindInv", type);
    return controller.getInventoryItemSlotIndex(type);
  }
  /**
   * Whether or not the script is currenty running.
   *
   * @return
   */
  public boolean Running() {
    Main.logMethod("Running");
    return Main.isRunning();
  }
  /** Not used, but remains for compatability. */
  public void CheckFighters(boolean check) {
    Main.logMethod("CheckFighters", check);
    // THIS IS NOT IMPLEMENTED.
  }
  /** Not used, but remains for compatability. */
  public void SleepWord() {
    Main.logMethod("SleepWord");
    WaitForLoad();
    // THIS IS NOT IMPLEMENTED.
  }
  /**
   * Whether or not we are currently sleeping in a bag/bed.
   *
   * @return
   */
  public boolean Sleeping() {
    Main.logMethod("Sleeping");
    return controller.isSleeping();
  }
  /**
   * Retrieves the current amount of fatigue.
   *
   * @return
   */
  public int Fatigue() {
    Main.logMethod("Fatigue");
    return controller.getFatigue();
  }
  /**
   * Whether or not the client has loaded.
   *
   * @return
   */
  public boolean Loading() {
    Main.logMethod("Loading");
    return !controller.isLoaded();
  }
  /** Waits for the client to finish loading. */
  public void WaitForLoad() {
    while (Loading()) Wait(100);
  }
  /**
   * Turns on the specified prayer index.
   *
   * @param prayer
   */
  public void PrayerOn(int prayer) {
    Main.logMethod("PrayerOn", prayer);
    controller.enablePrayer(prayer);
  }
  /**
   * Turns off the specified prayer index.
   *
   * @param prayer
   */
  public void PrayerOff(int prayer) {
    Main.logMethod("PrayerOff", prayer);
    controller.disablePrayer(prayer);
  }
  /**
   * Whether or not the specified prayer index is currently on.
   *
   * @param prayer
   * @return
   */
  public boolean Prayer(int prayer) {
    Main.logMethod("Prayer", prayer);
    return controller.isPrayerOn(prayer);
  }
  /**
   * Retrieves how many of the specifed item id the shop has in stock.
   *
   * @param item
   * @return
   */
  public int ShopCount(int item) {
    Main.logMethod("ShopCount", item);
    return controller.getShopItemCount(item);
  }
  /** Not used, but remains for compatability. */
  public void SetWorld(int world) {
    Main.logMethod("SetWorld", world);
    Main.log("Script attempted a world hop. No world hop functionality.");
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public int LastPlayerAttacked() {
    Main.logMethod("LastPlayerAttacked");
    // THIS IS NOT IMPLEMENTED.
    return -1;
  }
  /** <b> NOT IMPLEMENTED YET </b> */
  public void ResetLastPlayerAttacked() {
    Main.logMethod("ResetLastPlayerAttacked");
    // THIS IS NOT IMPLEMENTED.
  }
  /**
   * Attempts to login with the specified user and password.
   *
   * @param username
   * @param password
   */
  public void Login(String username, String password) {
    Main.logMethod("Login", username, password);
    Main.config.setUsername(username);
    Main.config.setPassword(password);
    controller.login();
  }
  /**
   * Whether or not the player is currently logged in.
   *
   * @return
   */
  public boolean LoggedIn() {
    Main.logMethod("LoggedIn");
    return controller.isLoggedIn();
  }
  /** Opens a nearby bank (banker or chest.) */
  public void OpenUnbusyBank() {
    Main.logMethod("OpenUnbusyBank");
    OpenBank();
  }
  /** Opens a nearby bank (banker or chest.) */
  public void OpenBank() {
    Main.logMethod("OpenBank");
    controller.openBank();
  }
  /**
   * Whether or not the specified substring is inside the string.
   *
   * @param str -- string
   * @param locate -- substring
   * @return
   */
  public boolean InStr(String str, String locate) {
    return str.contains(locate);
  }
  /**
   * Retrieves the text of the specified option answer in NPC dialogue.
   *
   * @param pos
   * @return
   */
  public String GetAnswer(int pos) {
    Main.logMethod("GetAnswer", pos);
    WaitForLoad();
    return controller.getOptionsMenuText(pos);
  }
  /**
   * Retrieves the distance between two points.
   *
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @return
   */
  public int GetDistance(int x1, int y1, int x2, int y2) {
    return controller.distance(x1, y1, x2, y2);
  }
  /**
   * Retrieves the players current amount of health.
   *
   * @return
   */
  public int GetHP() {
    Main.logMethod("GetHP");
    return controller.getPlayer().healthCurrent;
  }
  /**
   * Retrieves the maximum amount of HP available to the player.
   *
   * @return
   */
  public int GetMaxHP() {
    Main.logMethod("GetMaxHP");
    return controller.getPlayer().healthMax;
  }
  /**
   * Retrieves the current percentage of player hp.
   *
   * @return
   */
  public int GetHPPercent() {
    Main.logMethod("GetHPPercent");
    return (GetHP() / GetMaxHP()) * 100;
  }
  /**
   * Retrieves the item id of the specified slot.
   *
   * @param slot
   * @return
   */
  public int Inv(int slot) {
    Main.logMethod("Inv", slot);
    return controller.getInventorySlotItemId(slot);
  }
  /**
   * Retrieves the command of the specified item id.
   *
   * @param type
   * @return
   */
  public String GetItemCommand(int type) {
    return controller.getItemCommand(type);
  }
  /**
   * Retrieves the examine text of the specified item id.
   *
   * @param type
   * @return
   */
  public String GetItemDesc(int type) {
    return controller.getItemExamineText(type);
  }
  /**
   * Retrieves the name of the specified item id.
   *
   * @param type
   * @return
   */
  public String GetItemName(int type) {
    return controller.getItemName(type);
  }
  /**
   * Retrieves the name of the logged in user.
   *
   * @return
   */
  public String Username() {
    return controller.getPlayer().accountName;
  }

  public int GetPrayerLevel(int prayer) {
    Main.logMethod("GetPrayerLevel", prayer);
    WaitForLoad();
    return controller.getPrayerLevel(prayer);
  }

  public int GetPrayerDrain(int prayer) {
    Main.logMethod("GetPrayerDrain", prayer);
    WaitForLoad();
    return controller.getPrayerDrain(prayer);
  }

  public String GetNPCCommand(int type) {
    WaitForLoad();
    return controller.getNpcCommand1(type);
  }

  public String GetNPCDesc(int type) {
    WaitForLoad();
    return controller.getNpcExamineText(type);
  }

  public int GetNPCLevel(int type) {
    Main.logMethod("GetNPCLevel", type);
    ORSCharacter npc = controller.getNearestNpcById(type, true);

    if (npc != null) return npc.level;

    return -1;
  }

  /** <b> NOT IMPLEMENTED YET </b> */
  public int GetNPCType(int id) {
    // THIS IS NOT IMPLEMENTED.
    // This would be really easy to implement but I'm lazy and someone else can do it :)
    return -1;
  }

  public int GetNPCMaxHP(int type) {
    Main.logMethod("GetNPCMaxHP", type);
    ORSCharacter npc = controller.getNearestNpcById(type, true);

    if (npc != null) return npc.healthMax;

    return -1;
  }

  public String GetNPCName(int type) {
    return controller.getNpcName(type);
  }

  public int GetAnswerCount() {
    Main.logMethod("GetAnswerCount");
    return controller.getOptionMenuCount();
  }

  public int GetItemCount() {
    Main.logMethod("GetItemCount");
    return controller.getInventoryItemCount();
  }

  public int GetNPCCount() {
    Main.logMethod("GetNPCCount");
    return controller.getNpcCount();
  }

  public int GetPlayerCount() {
    Main.logMethod("GetPlayerCount");
    return controller.getPlayerCount();
  }

  public int GetObjectCount() {
    Main.logMethod("GetObjectCount");
    WaitForLoad();
    return controller.getObjectsCount();
  }

  public String GetObjectCommand1(int type) {
    return controller.getObjectCommand1(type);
  }

  public String GetObjectCommand2(int type) {
    return controller.getObjectCommand2(type);
  }

  public String GetObjectDesc(int type) {
    return controller.getObjectExamineText(type);
  }

  public String GetObjectName(int type) {
    return controller.getObjectName(type);
  }

  public boolean InArea(int x1, int y1, int x2, int y2) {
    int x = GetX();
    int y = GetY();
    return x >= x1 && x <= x2 && y >= y1 && y <= y2;
  }

  public boolean Wearable(int type) {
    WaitForLoad();
    return controller.isItemWearable(type);
  }

  /**
   * Since this function uses an item slot, on Coleslaw, it is permanently broken. Scripts will have
   * to be rewritten which use this function on Coleslaw.
   *
   * @param slot
   * @return
   */
  public boolean IsWorn(int slot) {
    Main.logMethod("IsWorn", slot);
    WaitForLoad();
    return controller.isEquipped(slot);
  }

  public boolean IsNPCAttackable(int type) {
    WaitForLoad();
    return controller.isNpcAttackable(type);
  }

  public void WalkPath(int[] pathx, int[] pathy) {
    if (pathx.length != pathy.length) {
      Println("### WalkPath - COORDINATES NOT THE SAME LENGTH");
      return;
    }
    int startPoint = 0;
    int startDistance = 8000;
    for (int i = 0; i < pathx.length; i++)
      if (startDistance == 8000) startDistance = Distance(pathx[i], pathy[i]);
      else if (Distance(pathx[i], pathy[i]) < startDistance) {
        startPoint = i;
        startDistance = Distance(pathx[i], pathy[i]);
      }
    for (int i = startPoint; i < pathx.length; i++) Walk(pathx[i], pathy[i], Rand(8000, 12000));
  }

  public void WalkPath(int[] pathx, int[] pathy, int ticks) {
    if (pathx.length != pathy.length) {
      Println("### WalkPath - COORDINATES NOT THE SAME LENGTH");
      return;
    }
    int startPoint = 0;
    int startDistance = 8000;
    for (int i = 0; i < pathx.length; i++)
      if (startDistance == 8000) startDistance = Distance(pathx[i], pathy[i]);
      else if (Distance(pathx[i], pathy[i]) < startDistance) {
        startPoint = i;
        startDistance = Distance(pathx[i], pathy[i]);
      }
    for (int i = startPoint; i < pathx.length; i++) Walk(pathx[i], pathy[i], ticks);
  }

  public void WalkPathReverse(int[] pathx, int[] pathy) {
    if (pathx.length != pathy.length) {
      Println("### WalkPath - COORDINATES NOT THE SAME LENGTH");
      return;
    }
    int startPoint = 0;
    int startDistance = 8000;
    for (int i = 0; i < pathx.length; i++)
      if (startDistance == 8000)
        startDistance = Distance(pathx[pathx.length - 1 - i], pathy[pathx.length - 1 - i]);
      else if (Distance(pathx[pathx.length - 1 - i], pathy[pathx.length - 1 - i]) < startDistance) {
        startPoint = i;
        startDistance = Distance(pathx[pathx.length - 1 - i], pathy[pathx.length - 1 - i]);
      }
    for (int i = startPoint; i < pathx.length; i++)
      Walk(pathx[pathx.length - 1 - i], pathy[pathx.length - 1 - i], Rand(8000, 12000));
  }

  public void WalkPathReverse(int[] pathx, int[] pathy, int ticks) {
    if (pathx.length != pathy.length) {
      Println("### WalkPath - COORDINATES NOT THE SAME LENGTH");
      return;
    }
    int startPoint = 0;
    int startDistance = 8000;
    for (int i = 0; i < pathx.length; i++)
      if (startDistance == 8000)
        startDistance = Distance(pathx[pathx.length - 1 - i], pathy[pathx.length - 1 - i]);
      else if (Distance(pathx[pathx.length - 1 - i], pathy[pathx.length - 1 - i]) < startDistance) {
        startPoint = i;
        startDistance = Distance(pathx[pathx.length - 1 - i], pathy[pathx.length - 1 - i]);
      }
    for (int i = startPoint; i < pathx.length; i++)
      Walk(pathx[pathx.length - 1 - i], pathy[pathx.length - 1 - i], ticks);
  }

  public boolean CoordsAt(int[] pos) {
    return GetX() == pos[0] && GetY() == pos[1];
  }

  public boolean CoordsAt(int x, int y) {
    return GetX() == x && GetY() == y;
  }

  public boolean IsWalking() {
    Main.logMethod("IsWalking");
    return controller.isCurrentlyWalking();
  }

  public void EndScript() {
    Main.logMethod("EndScript");
    Main.setRunning(false);
  }

  public boolean AutoLogin() {
    Main.logMethod("AutoLogin");
    return Main.isAutoLogin();
  }

  public void EnableAutoLogin() {
    Main.logMethod("EnableAutoLogin");
    Main.setAutoLogin(true);
  }

  public void DisableAutoLogin() {
    Main.logMethod("DisableAutoLogin");
    Main.setAutoLogin(false);
  }

  public String Version() {
    Main.logMethod("Version");
    return "";
  }

  public boolean NPCExists(int id) {
    Main.logMethod("NPCExists", id);
    return this.GetNearestNPC(id) != -1;
  }

  public boolean NPCInCombat(int id) {
    Main.logMethod("NPCInCombat", id);
    return controller.isNpcInCombat(id);
  }

  public int InvCountByName(String name) {
    Main.logMethod("InvCountByName", name);
    return controller.getInventoryItemCount(controller.getItemId(name));
  }

  public int InvByName(String name) {
    Main.logMethod("InvByName", name);
    return controller.getInventoryItemSlotIndex(controller.getItemId(name));
  }

  public boolean IsStackable(int id) {
    return controller.isItemStackable(id);
  }

  public int GetRandomNPC(int id) {
    Main.logMethod("GetRandomNPC", id);
    return this.GetNearestNPC(id);
  }

  public int GetRandomNPC(int[] id) {
    Main.logMethod("GetRandomNPC", id);
    for (int i : id) {
      int tmp = GetRandomNPC(i);

      if (tmp != -1) return tmp;
    }

    return -1;
  }
  /**
   * Not used, but remains for compatibility. "PID" is not a real thing on OpenRSC. Always returns
   * 0.
   *
   * @return
   */
  public int PID() {
    Main.logMethod("PID");
    return 0; // PID isn't a "real thing" anymore :(
  }

  /** <b>New SBot function.</b> On Coleslaw, if batching, sleeps until batching is finished. */
  public void WaitForBatchFinish() {
    while (controller.isBatching()) controller.sleep(10);
  }

  /**
   * <b>New SBot function.</b> On Coleslaw, returns whether or not we are currently batching.
   *
   * @return
   */
  public boolean IsBatching() {
    return controller.isBatching();
  }
}
