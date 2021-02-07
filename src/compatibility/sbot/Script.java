package compatibility.sbot;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import bot.Main;
import controller.Controller;
import controller.ORSCMessage;
import orsc.ORSCharacter;
import orsc.enumerations.MessageType;

/**
 * 
 * This is the compatibility abstraction layer for SBot. 
 * 
 * All functions are here, but some may not work until we have full covereage.
 * 
 * No documentation ATM because for the most part this is self-documenting.
 * If you would like to document these, please respond to the documentation issue on Gitlab.
 * 
 * @author Dvorak
 *
 */
public abstract class Script
{
	Controller controller = null;
	
	String lastChatMessage = "";
	String lastNPCMessage = "";
	String lastServerMessage = "";
	boolean resetLastChatMessage = true;
	boolean resetLastNPCMessage = true;
	boolean resetLastServerMessage = true;
	
	public void setController(Controller _controller) {
		controller = _controller;
	}
	
    public void start(String command, String parameters[])
    {
		System.out.println("If you see this, your script did not call the start function.");
    }
    public String[] getCommands()
    {
        return new String[0];
    }
    public void init()
    {
    }
	public void ServerMessage(String message)
	{
		Main.logMethod("ServerMessage", message);
		//do nothing. this is an interrupt.
	}
	public void ChatMessage(String message)
	{
		Main.logMethod("ChatMessage", message);
		//do nothing. this is an interrupt.
	}
	public void NPCMessage(String message)
	{
		Main.logMethod("NPCMessage", message);
		//do nothing. this is an interrupt.
	}
	public void TradeRequest(int PlayerID)
	{
		Main.logMethod("TradeRequest", PlayerID);
		//do nothing. this is an interrupt.
		//THIS IS NOT IMPLEMENTED.
	}
	public void Accepted()
	{
		Main.logMethod("Accepted");
		//do nothing. this is an interrupt.
		//THIS IS NOT IMPLEMENTED.
	}
	public void TradeOver()
	{
		Main.logMethod("TradeOver");
		//do nothing. this is an interrupt.
		//THIS IS NOT IMPLEMENTED.
	}
	public void KeyPressed(int key)
	{
		Main.logMethod("KeyPressed");
		//THIS IS NOT IMPLEMENTED.
	}
	public void KeyReleased(int key)
	{
		Main.logMethod("KeyReleased");
		//THIS IS NOT IMPLEMENTED.
	}

	public int LastChatter()
	{
		Main.logMethod("LastChatter");
		return 0;
	}
	public String LastChatterName()
	{
		Main.logMethod("LastChatterName");
		return controller.getMessages().get(0).getSender();
	}
	public int BankCount(int item)
	{
		Main.logMethod("BankCount", item);
		if (!Running())
			return -1;
		
		return controller.getBankItemCount(item);
	}
	public int StrToInt(String st)
	{
		return Integer.parseInt(st);
	}
	public String IntToStr(int num)
	{
		return String.valueOf(num);
	}
	public int NPCX(int id)
	{
		Main.logMethod("NPCX", id);
		return controller.getNpcCoordsByServerIndex(id)[0];
	}
	public int NPCY(int id)
	{
		Main.logMethod("NPCY", id);
		return controller.getNpcCoordsByServerIndex(id)[1];
	}
	public boolean IsAccepted()
	{
		Main.logMethod("IsAccepted");
		return controller.isTradeRecipientAccepting();
	}

	public void Quit()
	{
		Main.logMethod("Quit");
		System.exit(1);
	}

	public int Distance(int x, int y)
	{
		return GetDistance(GetX(), GetY(), x, y);
	}

	public int TradeStatus()
	{
		Main.logMethod("TradeStatus");
		if(controller.isInTrade() == true) {
			if(controller.isInTradeConfirmation())
				return 2;
			return 1;
		} else {
			return 0;
		}
	}
	public void AcceptTrade()
	{
		Main.logMethod("AcceptTrade");
		controller.acceptTrade();
	}
	public void AcceptTrade2()
	{
		Main.logMethod("AcceptTrade2");
		controller.acceptTradeConfirmation();
	}
	public void DeclineTrade()
	{
		Main.logMethod("DeclineTrade");
		controller.declineTrade();
	}
	
	public void TradeArray(int item, int amount)
	{
		Main.logMethod("TradeArray", item, amount);
		TradeArray(new int[] {item}, new int[] {amount});
	}
//
	public void TradeArray(int item[], int amount[])
	{
		Main.logMethod("TradeArray", item, amount);
		controller.setTradeItems(item, amount);
	}
//
	public void UpdateTrade()
	{
		Main.logMethod("UpdateTrade");
		//THIS IS NOT IMPLEMENTED.
	}
//
	public void ResetTrade()
	{
		Main.logMethod("ResetTrade");
		controller.removeAllTradeItems();
	}

	public boolean CanReach(int x, int y)
	{
		//THIS IS NOT IMPLEMENTED.
		//I have no clue how to implement this beyond implementing a pathfinding algorithm
		//If anyone has a better plan please respond to the issue on GitLab.
		return true;
	}
	public void Beep()
	{
		Main.logMethod("Beep");
		Toolkit.getDefaultToolkit().beep();
	}
	public void Wait(int ticks)
	{
		Main.logMethod("Wait", ticks);
		try
		{
			Thread.sleep(ticks);
		}
		catch (InterruptedException e) { e.toString(); }
	}
	public void DisplayMessage(String message, int type)
	{
		Main.logMethod("DisplayMessage", message, type);
		controller.displayMessage(message, type);
	}
	public void SetFightMode(int style)
	{
		Main.logMethod("SetFightMode", style);
		controller.setFightMode(style);
	}
	public int GetFightMode()
	{
		Main.logMethod("GetFightMode");
		WaitForLoad();
		return controller.getFightMode();
	}
	public void Println(String message)
	{
		Main.logMethod("Println", message);
		System.out.println(message);
	}
	public void Print(String message)
	{
		Main.logMethod("Print", message);
		System.out.print(message);
	}
	public void SexyPrint(String message)
	{
		Main.logMethod("SexyPrint", message);
		for (int i = 0; i < message.length() - 1; i++)
		{
			System.out.print(message.substring(i,i+1));
			Wait(1);
		}
		System.out.println(message.substring(message.length()-1));
	}
	public void ForceWalk(int x, int y)
	{
		Main.logMethod("ForceWalk", x, y);
		controller.walkTo(x, y, 0, true);
	}
	public void ForceWalkNoWait(int x, int y)
	{
		Main.logMethod("ForceWalkNoWait", x, y);
		controller.walkTo(x, y, 0, true);
	}
	public void AtObject(int x, int y)
	{
		Main.logMethod("AtObject", x, y);
		boolean result = controller.atObject(x, y);

		if(!result)
			controller.displayMessage("@red@ERROR: @whi@No object found at: @yel@" + x + ", " + y, 3);
	}
	public void AtObject(int coords[])
	{
		Main.logMethod("AtObject", coords);
		AtObject(coords[0], coords[1]);
	}
	public void AtObject2(int x, int y)
	{
		Main.logMethod("AtObject2", x, y);
		boolean result = controller.atObject2(x, y);

		if(!result)
			controller.displayMessage("@red@ERROR: @whi@No object found at: @yel@" + x + ", " + y, 3);
	}
	public void AtObject2(int coords[])
	{
		Main.logMethod("AtObject2", coords);
		AtObject2(coords[0], coords[1]);
	}
	public void Walk(int x, int y)
	{
		Main.logMethod("Walk", x, y);
		controller.walkTo(x, y, 0, false);
	}
	public void Walk(int x, int y, int step)
	{
		Main.logMethod("Walk", x, y, step);
		//WaitForLoad();
		while (GetX() != x && GetY() != y)
		{
			Walk(x,y);
			long T = TickCount();
			while (TickCount() - T < step && GetX() != x && GetY() != y)
				Wait(1);
		}
	}
	public void WalkNoWait(int x, int y)
	{
		Main.logMethod("WalkNoWait", x, y);
		controller.walkTo(x, y, 0, false);
	}
	public long TickCount()
	{
		return System.currentTimeMillis();
	}
	public void WalkEmpty(int x1, int y1, int x2, int y2)
	{
		Main.logMethod("WalkEmpty", x1, y1, x2, y2);
		boolean FoundEmpty = false;
		for (int horizontal = x1; horizontal <= x2; horizontal++)
		{
			for (int vertical = y1; vertical <= y2; vertical++)
			{
				if (EmptyTile(horizontal, vertical) == true)
				{
					FoundEmpty = true;
				}
			}
		}
		if (FoundEmpty == true)
		{
			boolean GoneTo = false;
			while (GoneTo == false)
			{
				//WaitForLoad();
				int TempX = Rand(x1, x2);
				int TempY = Rand(y1, y2);
				if (EmptyTile(TempX, TempY) == true)
				{
					Walk(TempX, TempY);
					GoneTo = true;
				}
			}
		} else {
			System.out.println("Warning: No Empty Tiles Found");
			Walk(Rand(x1, x2), Rand(y1, y2));
		}
	}
	public int PlayerAt(int x, int y)
	{
		Main.logMethod("PlayerAt", x, y);
		return controller.getPlayerAtCoord(x, y);
	}
	public boolean Obstructed(int x, int y)
	{
		if (ObjectAt(x,y) != -1)
			return true;
		if (PlayerAt(x,y) != -1)
			return true;
		if (!CanReach(x,y))
			return true;
		return false;
		
		//NOTE: Until CanReach is successfully implemented this function call is BROKEN.

	}
	public void WalkEmptyNoWait(int x1, int y1, int x2, int y2)
	{
		Main.logMethod("WalkEmptyNoWait", x1, y1, x2, y2);
		WaitForLoad();
		boolean FoundEmpty = false;
		for (int horizontal = x1; horizontal <= x2; horizontal++)
		{
			for (int vertical = y1; vertical <= y2; vertical++)
			{
				if (EmptyTile(horizontal, vertical) == true)
				{
					FoundEmpty = true;
				}
			}
		}
		if (FoundEmpty == true)
		{
			boolean GoneTo = false;
			while (GoneTo == false)
			{
				WaitForLoad();
				int TempX = Rand(x1, x2);
				int TempY = Rand(y1, y2);
				if (EmptyTile(TempX, TempY) == true)
				{
					Walk(TempX, TempY);
					GoneTo = true;
				}
			}
		} else {
			System.out.println("Warning: No Empty Tiles Found");
			Walk(Rand(x1, x2), Rand(y1, y2));
		}
	}
	public int GetX()
	{
		Main.logMethod("GetX");
		return controller.currentX();
	}
	public int GetY()
	{
		Main.logMethod("GetY");
		return controller.currentZ();
	}
	public int Rand(int lower, int higher)
	{
		return ThreadLocalRandom.current().nextInt(lower, higher);
	}
	public void Say(String message)
	{
		Main.logMethod("Say", message);
		controller.chatMessage(message);
	}
	public boolean EmptyTile(int x, int y)
	{
		Main.logMethod("EmptyTile", x, y);
		return controller.isTileEmpty(x, y);
	}
	public void MagicPlayer(int player, int spell)
	{
		Main.logMethod("MagicPlayer", player, spell);
		controller.castSpellOnPlayer(spell, player);
	}
	public void UseOnPlayer(int player, int slot)
	{
		Main.logMethod("UseOnPlayer", player, slot);
		//THIS IS NOT IMPLEMENTED.
	}
	public void AttackPlayer(int player)
	{
		Main.logMethod("AttackPlayer", player);
		controller.attackPlayer(player);
	}
	public void DuelPlayer(int player)
	{
		Main.logMethod("DuelPlayer", player);
		controller.duelPlayer(player);
	}
	public void TradePlayer(int player)
	{
		Main.logMethod("TradePlayer", player);
		controller.tradePlayer(player);
	}
	public void FollowPlayer(int player)
	{
		Main.logMethod("FollowPlayer", player);
		controller.followPlayer(player);
	}
	public void MagicItem(int x, int y, int item, int spell)
	{
		Main.logMethod("MagicItem", x, y, item, spell);
		WaitForLoad();
		controller.castSpellOnGroundItem(spell, item, x, y);
	}
	public void UseOnItem(int x, int y, int type, int item)
	{
		Main.logMethod("UseOnItem", x, y, type, item);
		WaitForLoad();
		controller.useItemOnGroundItem(x, y, item, type); 
	}
	public boolean TakeItem(int x, int y, int type)
	{
		Main.logMethod("TakeItem", x, y, type);
		int beforeCount, afterCount;
		
		beforeCount = controller.getInventoryItemCount(type);
		controller.pickupItem(x, y, type, false, false);
		
		Wait(618); //wait 1 tick
		
		afterCount = controller.getInventoryItemCount(type);
		return afterCount > beforeCount;
	}
	public void MagicNPC(int id, int spell)
	{
		Main.logMethod("MagicNPC", id, spell);
		controller.castSpellOnNpc(id, spell);
	}
	public void UseOnNPC(int id, int item)
	{
		Main.logMethod("UseOnNPC", id, item);
		controller.useItemOnNpc(id, item);
	}
	public void TalkToNPC(int serverIndex)
	{
		Main.logMethod("TalkToNPC", serverIndex);
		controller.talkToNpc(serverIndex);
	}
	public void AttackNPC(int serverIndex)
	{
		Main.logMethod("AttackNPC", serverIndex);
		controller.attackNpc(serverIndex);
	}
	public void ThieveNPC(int serverIndex)
	{
		Main.logMethod("ThieveNPC", serverIndex);
		controller.thieveNpc(serverIndex);
	}
	public void MagicDoor(int x, int y, int dir, int spell)
	{
		Main.logMethod("MagicDoor", x, y, dir, spell);
		//THIS IS NOT IMPLEMENTED.
	}
	public void UseOnDoor(int x, int y, int dir, int item)
	{
		Main.logMethod("UseOnDoor", x, y, dir, item);
		WaitForLoad();
		controller.useItemOnWall(x, y, controller.getInventoryItemIdSlot(item));
	}
	public void OpenDoor(int x, int y, int dir)
	{
		Main.logMethod("OpenDoor", x, y, dir);
		controller.openDoor(x, y);
	}
	public void CloseDoor(int x, int y, int dir)
	{
		Main.logMethod("CloseDoor", x, y, dir);
		controller.closeDoor(x, y);
	}
	public void MagicObject(int x, int y, int spell)
	{
		Main.logMethod("MagicObject", x, y, spell);
		WaitForLoad();
		controller.castSpellOnObject(spell, x, y);
	}
	public void UseOnObject(int x, int y, int slotId)
	{
		Main.logMethod("UseOnObject", x, y, slotId);
		WaitForLoad();
		controller.useItemSlotOnObject(x, y, slotId);
	}
	public void Magic(int spell)
	{
		//THIS IS NOT IMPLEMENTED.
	}
	public boolean QuestMenu()
	{
		Main.logMethod("QuestMenu");
		return controller.isInOptionMenu();
	}
	public void Answer(int answer)
	{
		Main.logMethod("Answer", answer);
		Wait(3000);
		controller.optionAnswer(answer);
	}
	public void MagicInventory(int slot, int spell)
	{
		Main.logMethod("MagicInventory", slot, spell);
		WaitForLoad();
		controller.castSpellOnInventoryItem(spell, slot);
	}
	public void UseWithInventory(int slot1, int slot2)
	{
		Main.logMethod("UseWithInventory", slot1, slot2);
		controller.useItemOnItemBySlot(slot1, slot2);
	}
	public void Remove(int slot)
	{
		Main.logMethod("Remove", slot);
		WaitForLoad();
		controller.unequipItem(slot);
	}
	public void Wield(int slot)
	{
		Main.logMethod("Wield", slot);
		WaitForLoad();
		controller.equipItem(slot);
	}
	public void Use(int slot)
	{
		Main.logMethod("Use", slot);
		WaitForLoad();
		controller.itemCommandBySlot(slot);
	}
	public void Drop(int slot)
	{
		Main.logMethod("Drop", slot);
		controller.dropItem(slot);
	}
	public void Deposit(int type, int amount)
	{
		Main.logMethod("Deposit", type, amount);
		controller.depositItem(type, amount);
	}
	public void Withdraw(int type, int amount)
	{
		Main.logMethod("Withdraw", type, amount);
		controller.withdrawItem(type, amount);
	}
	public int InvCount(int type)
	{
		Main.logMethod("InvCount", type);
		return controller.getInventoryItemCount(type);
	}
	public int InvCount()
	{
		Main.logMethod("InvCount");
		return controller.getInventoryItemCount();
	}
	public void Logout()
	{
		Main.logMethod("Logout");
		WaitForLoad();
		controller.logout();
	}
	public int[] GetNearestObject(int type[])
	{
		Main.logMethod("GetNearestObject", type);
		WaitForLoad();
		
		int closestDistance = 999999;
		int[] result = new int[] {-1, -1};
		for(int itemId : type) {
			int[] tmp = GetNearestObject(itemId);
			int distance = GetDistance(this.GetX(), this.GetY(), tmp[0], tmp[1]);
			
			if(distance < closestDistance) {
				closestDistance = distance;
				result = tmp;
			}
					
		}
		
		return result;
	}
	public int[] GetNearestObject(int type)
	{
		Main.logMethod("GetNearestObject", type);
		int[] result = controller.getNearestObjectById(type);
		if(result == null) {
			int[] badCoord = {-1, -1};
			return badCoord;
		}
			
		return result;
	}

	public int[] GetNearestObject(int type, int x1, int y1, int x2, int y2)
	{
		//THIS IS NOT IMPLEMENTED.
		//This would be really easy to implement but I'm lazy and someone else can do it :)
		return new int[] {-1, -1};
	}
	public int[] GetNearestObject(int type[], int x1, int y1, int x2, int y2)
	{
		//THIS IS NOT IMPLEMENTED.
		//This would be really easy to implement but I'm lazy and someone else can do it :)
		return new int[] {-1, -1};
	}
	public int GetNearestNPC(int type)
	{
		Main.logMethod("GetNearestNpc", type);
		ORSCharacter npc = controller.getNearestNpcById(type, false);
		
		if(npc == null)
			return -1;
		
		return npc.serverIndex;
	}
	public int GetNearestNPC(int type[])
	{
		Main.logMethod("GetNearestNPC", type);
		ORSCharacter npc = controller.getNearestNpcByIds(type, false);
		
		if(npc == null)
			return -1;
		
		return npc.serverIndex;
	}
	public int GetNearestNPC(int type, int x1, int y1, int x2, int y2)
	{
		//THIS IS NOT IMPLEMENTED.
		//This would be really easy to implement but I'm lazy and someone else can do it :)
		return -1;
	}
	public int GetNearestNPC(int type[], int x1, int y1, int x2, int y2)
	{
		//THIS IS NOT IMPLEMENTED.
		//This would be really easy to implement but I'm lazy and someone else can do it :)
		return -1;
	}
	public int[] GetNearestItem(int type)
	{
		Main.logMethod("GetNearestItem", type);
		int[] result = controller.getNearestItemById(type);
		
		if(result == null)
			return new int[] {-1, -1};
		
		return result;
	}
	public int[] GetNearestItem(int type[])
	{
		Main.logMethod("GetNearestItem", type);
		return controller.getNearestItemById(type[0]);
	}
	public void CloseShop()
	{
		Main.logMethod("CloseShop");
		controller.closeShop();
	}
	public void CloseBank()
	{
		Main.logMethod("CloseBank");
		controller.closeBank();
	}
	public void Buy(int item)
	{
		Main.logMethod("Buy", item);
		controller.shopBuy(item);
	}
	public void Sell(int item)
	{
		Main.logMethod("Sell", item);
		controller.shopSell(item);
	}
	public boolean Bank()
	{
		Main.logMethod("Bank");
		return controller.isInBank();
	}
	public boolean Shop()
	{
		Main.logMethod("Shop");
		return controller.isInShop();
	}
	public int DoorAt(int x, int y, int dir)
	{
		Main.logMethod("DoorAt", x, y, dir);
		WaitForLoad();
		
		if(controller.isDoorOpen(x, y)) {
			return 1; //1 = open
		} else {
			return 2; //2 = closed. dumb i know. 
		}
	}
	public boolean ItemAt(int x, int y, int type)
	{
		Main.logMethod("ItemAt", x, y, type);
		WaitForLoad();
		return controller.isItemAtCoord(x, y, type);
	}
	public int ObjectAt(int x, int y)
	{
		Main.logMethod("ObjectAt", x, y);
		WaitForLoad();
		return controller.getObjectAtCoord(x, y);
	}
	public int PlayerByName(String name)
	{
		Main.logMethod("PlayerByName", name);
		return controller.getPlayerServerIndexByName(name);
	}
	public int GetExperience(int statno)
	{
		Main.logMethod("GetExperience", statno);
		return controller.getStatXp(statno);
	}
	public int GetCurrentStat(int statno)
	{
		Main.logMethod("GetCurrentStat", statno);
		return controller.getCurrentStat(statno);
	}
	public int GetStat(int statno)
	{
		Main.logMethod("GetStat", statno);
		return controller.getBaseStat(statno);
	}
	public int PlayerHP(int id)
	{
		Main.logMethod("PlayerHP", id);
		if(controller.getPlayer(id) == null)
			return -1;
		
		return controller.getPlayer(id).healthCurrent;
	}
	public boolean InCombat()
	{
		Main.logMethod("InCombat");
		return controller.isInCombat();
	}
	public boolean PlayerInCombat(int id)
	{
		Main.logMethod("PlayerInCombat", id);
		return controller.isPlayerInCombat(id);
	}
	public int PlayerX(int id)
	{
		Main.logMethod("PlayerX", id);
		if(controller.getPlayer(id) == null)
			return -1;
		
		return controller.convertX(controller.getPlayer(id).currentX);
	}
	public int PlayerY(int id)
	{
		Main.logMethod("PlayerY", id);
		if(controller.getPlayer(id) == null)
			return -1;
		
		return controller.convertZ(controller.getPlayer(id).currentZ);
	}
	public int PlayerDestX(int id)
	{
		Main.logMethod("PlayerDestX", id);
		if(controller.getPlayer(id) == null)
			return -1;
		
		int[] xs = controller.getPlayer(id).waypointsX;
		int length = controller.getPlayer(id).waypointsX.length;
		int index = length - 1 > 0 ? length - 1 : 0;
		
		return controller.convertX(xs[index]);
	}
	public int PlayerDestY(int id)
	{
		Main.logMethod("PlayerDestY", id);
		if(controller.getPlayer(id) == null)
			return -1;
		
		int[] ys = controller.getPlayer(id).waypointsZ;
		int length = controller.getPlayer(id).waypointsZ.length;
		int index = length - 1 > 0 ? length - 1 : 0;
		
		return controller.convertZ(ys[index]);
	}
	public String LastChatMessage()
	{
		Main.logMethod("LastChatMessage");
		String messageText = "";
		
		for(ORSCMessage message : controller.getMessages()) {
			if(message.getType() == MessageType.CHAT) {
				messageText = message.getMessage();
			}
		}
		
		if(resetLastChatMessage == true) {
			if(lastChatMessage.equals(messageText)) {
				return "";
			} else {
				resetLastChatMessage = false;
			}
		}
		
		lastChatMessage = messageText;
		return messageText;
	}
	public void ResetLastChatMessage()
	{
		Main.logMethod("ResetLastChatMessage");
		resetLastChatMessage = true;
	}
	public String LastNPCMessage()
	{
		Main.logMethod("LastNPCMessage");
		String messageText = "";
		
		for(ORSCMessage message : controller.getMessages()) {
			if(message.getType() == MessageType.QUEST) {
				messageText = message.getMessage();
			}
		}
		
		if(resetLastNPCMessage == true) {
			if(lastNPCMessage.equals(messageText)) {
				return "";
			} else {
				resetLastNPCMessage = false;
			}
		}
		
		lastNPCMessage = messageText;
		return messageText;
	}
	public void ResetLastNPCMessage()
	{
		Main.logMethod("ResetLastNPCMessage");
		resetLastNPCMessage = true;
	}

	public String LastServerMessage()
	{
		Main.logMethod("LastServerMessage");
		String messageText = "";
		
		for(ORSCMessage message : controller.getMessages()) {
			if(message.getType() == MessageType.GAME) {
				messageText = message.getMessage();
			}
		}
		
		if(resetLastServerMessage == true) {
			if(lastServerMessage.equals(messageText)) {
				return "";
			} else {
				resetLastServerMessage = false;
			}
		}
		
		lastServerMessage = messageText;
		return messageText;
	}
	public void ResetLastServerMessage()
	{
		Main.logMethod("ResetLastServerMessage");
		resetLastServerMessage = true;
	}
	public void WaitForServerMessage(int timeout)
	{
		Main.logMethod("WaitForServerMessage", timeout);
		boolean newMessage = false;
		String currentMessage = LastServerMessage();
		long T = System.currentTimeMillis();
		while (newMessage == false && System.currentTimeMillis() - T <= timeout)
		{
			if(currentMessage.equals(LastServerMessage())) {
				Wait(10);
			} else {
				newMessage = true;
			}
		}
	}
	public boolean InLastServerMessage(String st)
	{
		Main.logMethod("InLastServerMessage", st);
		if(LastServerMessage() == null)
			return false;
		
		if (LastServerMessage().indexOf(st) >= 0)
		{
			return true;
		}
		return false;

	}
	public int FindInv(int type)
	{
		Main.logMethod("FindInv", type);
		return controller.getInventoryItemIdSlot(type);
	}
	public boolean Running()
	{
		Main.logMethod("Running");
		return Main.isRunning();
	}
	public void CheckFighters(boolean check)
	{
		Main.logMethod("CheckFighters", check);
		//THIS IS NOT IMPLEMENTED.
	}
	public void SleepWord()
	{
		Main.logMethod("SleepWord");
		WaitForLoad();
		//THIS IS NOT IMPLEMENTED.
	}
	public boolean Sleeping()
	{
		Main.logMethod("Sleeping");
		return false;
	}
	public int Fatigue()
	{
		Main.logMethod("Fatigue");
		return controller.getFatigue();
	}
	public boolean Loading()
	{
		Main.logMethod("Loading");
		return !controller.isLoaded();
	}
	public void WaitForLoad()
	{
		while (Loading())
			Wait(100);
	}
	public void PrayerOn(int prayer)
	{
		Main.logMethod("PrayerOn", prayer);
		controller.enablePrayer(prayer);
	}
	public void PrayerOff(int prayer)
	{
		Main.logMethod("PrayerOff", prayer);
		controller.disablePrayer(prayer);
	}
	public boolean Prayer(int prayer)
	{
		Main.logMethod("Prayer", prayer);
		return controller.isPrayerOn(prayer);
	}
	public int ShopCount(int item)
	{
		Main.logMethod("ShopCount", item);
		return controller.shopItemCount(item);
	}
	public void SetWorld(int world)
	{
		Main.logMethod("SetWorld", world);
		Main.log("Script attempted a world hop. No world hop functionality.");
	}
	public int LastPlayerAttacked()
	{
		Main.logMethod("LastPlayerAttacked");
		//THIS IS NOT IMPLEMENTED.
		return -1;
	}
	public void ResetLastPlayerAttacked()
	{
		Main.logMethod("ResetLastPlayerAttacked");
		//THIS IS NOT IMPLEMENTED.
	}
	public void Login(String username, String password)
	{
		Main.logMethod("Login", username, password);
		Main.username = username;
		Main.password = password;
		controller.login();
	}
	public boolean LoggedIn()
	{
		Main.logMethod("LoggedIn");
		return controller.isLoggedIn();
	}
	public void OpenUnbusyBank()
	{
		Main.logMethod("OpenUnbusyBank");
		OpenBank();
	}
	public void OpenBank()
	{
		Main.logMethod("OpenBank");
		controller.openBank();
	}
	public boolean InStr(String str, String locate)
	{
		if (str.indexOf(locate) >= 0)
			return true;
		return false;
	}
	public String GetAnswer(int pos)
	{
		Main.logMethod("GetAnswer", pos);
		WaitForLoad();
		return controller.optionsMenuText(pos);
	}
	public int GetDistance(int x1, int y1, int x2, int y2)
	{
		return controller.distance(x1, y1, x2, y2);
	}
	public int GetHP()
	{
		Main.logMethod("GetHP");
		return controller.getPlayer().healthCurrent;
	}
	public int GetMaxHP()
	{
		Main.logMethod("GetMaxHP");
		return controller.getPlayer().healthMax;
	}
	public int GetHPPercent()
	{
		Main.logMethod("GetHPPercent");
		return (GetHP() / GetMaxHP()) * 100;
	}
	public int Inv(int slot)
	{
		Main.logMethod("Inv", slot);
		return controller.getInventorySlotItemId(slot);
	}
	public String GetItemCommand(int type)
	{
		return controller.getItemCommand(type);
	}
	public String GetItemDesc(int type)
	{
		return controller.getItemExamineText(type);
	}
	public String GetItemName(int type)
	{
		return controller.getItemName(type);
	}
	public String Username()
	{
		return controller.getPlayer().accountName;
	}
	public int GetPrayerLevel(int prayer)
	{
		Main.logMethod("GetPrayerLevel", prayer);
		WaitForLoad();
		return controller.getPrayerLevel(prayer);
	}
	
	public int GetPrayerDrain(int prayer)
	{
		Main.logMethod("GetPrayerDrain", prayer);
		WaitForLoad();
		return controller.getPrayerDrain(prayer);
	}
	public String GetNPCCommand(int type)
	{
		WaitForLoad();
		return controller.getNpcCommand(type);
	}
	public String GetNPCDesc(int type)
	{
		WaitForLoad();
		return controller.getNpcExamineText(type);
	}
	public int GetNPCLevel(int type)
	{
		Main.logMethod("GetNPCLevel", type);
		ORSCharacter npc = controller.getNearestNpcById(type, true);
		
		if(npc != null)
			return npc.level;
		
		return -1;
	}
	public int GetNPCType(int id)
	{
		//THIS IS NOT IMPLEMENTED.
		//This would be really easy to implement but I'm lazy and someone else can do it :)
		return -1;
	}
	public int GetNPCMaxHP(int type)
	{
		Main.logMethod("GetNPCMaxHP", type);
		ORSCharacter npc = controller.getNearestNpcById(type, true);
		
		if(npc != null)
			return npc.healthMax;
		
		return -1;
	}
	public String GetNPCName(int type)
	{
		return controller.getNpcName(type);
	}
	public int GetAnswerCount()
	{
		Main.logMethod("GetAnswerCount");
		return controller.optionMenuCount();
	}
	public int GetItemCount()
	{
		Main.logMethod("GetItemCount");
		return controller.getInventoryItemCount();
	}
	public int GetNPCCount()
	{
		Main.logMethod("GetNPCCount");
		return controller.getNpcCount();
	}
	public int GetPlayerCount()
	{
		Main.logMethod("GetPlayerCount");
		return controller.getPlayerCount();
	}
	public int GetObjectCount()
	{
		Main.logMethod("GetObjectCount");
		WaitForLoad();
		return controller.getObjectsCount();
	}
	public String GetObjectCommand1(int type)
	{
		return controller.getObjectCommand1(type);
	}
	public String GetObjectCommand2(int type)
	{
		return controller.getObjectCommand2(type);
	}
	public String GetObjectDesc(int type)
	{
		return controller.getObjectExamineText(type);
	}
	public String GetObjectName(int type)
	{
		return controller.getObjectName(type);
	}
	public boolean InArea(int x1, int y1, int x2, int y2)
	{
		int x = GetX();
		int y = GetY();
		if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
			return true;
		}
		return false;
	}
	public boolean Wearable(int type)
	{
		WaitForLoad();
		return controller.isItemWearable(type);
	}
	public boolean IsWorn(int slot)
	{
		Main.logMethod("IsWorn", slot);
		WaitForLoad();
		return controller.isEquipped(slot);
	}
	public boolean IsNPCAttackable(int type)
	{
		WaitForLoad();
		return controller.isNpcAttackable(type);
	}
	public void WalkPath(int pathx[], int pathy[])
	{
		if (pathx.length != pathy.length)
		{
			Println("### WalkPath - COORDINATES NOT THE SAME LENGTH");
			//return;
		}
		int startPoint = 0;
		int startDistance = 8000;
		for (int i = 0; i < pathx.length; i++)
			if (startDistance == 8000)
				startDistance = Distance(pathx[i],pathy[i]);
			else if (Distance(pathx[i],pathy[i]) < startDistance)
			{
				startPoint = i;
				startDistance = Distance(pathx[i],pathy[i]);
			}
		for (int i = startPoint; i < pathx.length; i++)
			Walk(pathx[i],pathy[i],Rand(8000,12000));
	}
	public void WalkPath(int pathx[], int pathy[], int ticks)
	{
		if (pathx.length != pathy.length)
		{
			Println("### WalkPath - COORDINATES NOT THE SAME LENGTH");
			//return;
		}
		int startPoint = 0;
		int startDistance = 8000;
		for (int i = 0; i < pathx.length; i++)
			if (startDistance == 8000)
				startDistance = Distance(pathx[i],pathy[i]);
			else if (Distance(pathx[i],pathy[i]) < startDistance)
			{
				startPoint = i;
				startDistance = Distance(pathx[i],pathy[i]);
			}
		for (int i = startPoint; i < pathx.length; i++)
			Walk(pathx[i],pathy[i],ticks);
	}
	public void WalkPathReverse(int pathx[], int pathy[])
	{
		if (pathx.length != pathy.length)
		{
			Println("### WalkPath - COORDINATES NOT THE SAME LENGTH");
			//return;
		}
		int startPoint = 0;
		int startDistance = 8000;
		for (int i = 0; i < pathx.length; i++)
			if (startDistance == 8000)
				startDistance = Distance(pathx[pathx.length-1-i],pathy[pathx.length-1-i]);
			else if (Distance(pathx[pathx.length-1-i],pathy[pathx.length-1-i]) < startDistance)
			{
				startPoint = i;
				startDistance = Distance(pathx[pathx.length-1-i],pathy[pathx.length-1-i]);
			}
		for (int i = startPoint; i < pathx.length; i++)
			Walk(pathx[pathx.length-1-i],pathy[pathx.length-1-i],Rand(8000,12000));
	}
	public void WalkPathReverse(int pathx[], int pathy[], int ticks)
	{
		if (pathx.length != pathy.length)
		{
			Println("### WalkPath - COORDINATES NOT THE SAME LENGTH");
			//return;
		}
		int startPoint = 0;
		int startDistance = 8000;
		for (int i = 0; i < pathx.length; i++)
			if (startDistance == 8000)
				startDistance = Distance(pathx[pathx.length-1-i],pathy[pathx.length-1-i]);
			else if (Distance(pathx[pathx.length-1-i],pathy[pathx.length-1-i]) < startDistance)
			{
				startPoint = i;
				startDistance = Distance(pathx[pathx.length-1-i],pathy[pathx.length-1-i]);
			}
		for (int i = startPoint; i < pathx.length; i++)
			Walk(pathx[pathx.length-1-i],pathy[pathx.length-1-i],ticks);
	}
	public boolean CoordsAt(int pos[])
	{
		if (GetX() == pos[0] && GetY() == pos[1])
			return true;
		return false;
	}
	public boolean CoordsAt(int x, int y)
	{
		if (GetX() == x && GetY() == y)
			return true;
		return false;
	}
	public boolean IsWalking()
	{
		Main.logMethod("IsWalking");
		return controller.isCurrentlyWalking();
	}
	public void EndScript()
	{
		Main.logMethod("EndScript");
		Main.setRunning(false);
	}
	public boolean AutoLogin()
	{
		Main.logMethod("AutoLogin");
		return Main.isAutoLogin();
	}
	public void EnableAutoLogin()
	{
		Main.logMethod("EnableAutoLogin");
		Main.setAutoLogin(true);
	}
	public void DisableAutoLogin()
	{
		Main.logMethod("DisableAutoLogin");
		Main.setAutoLogin(false);
	}
	public String Version()
	{
		Main.logMethod("Version");
		return "";
	}
	public boolean NPCExists(int id)
	{
		Main.logMethod("NPCExists", id);
		return this.GetNearestNPC(id) != -1;
	}
	public boolean NPCInCombat(int id)
	{
		Main.logMethod("NPCInCombat", id);
		return controller.isNpcInCombat(id);
	}
	public int InvCountByName(String name)
	{
		Main.logMethod("InvCountByName", name);
		return controller.getInventoryItemCount(controller.getItemId(name));
	}
	public int InvByName(String name)
	{
		Main.logMethod("InvByName", name);
		return controller.getInventoryItemIdSlot(controller.getItemId(name));
	}
	public boolean IsStackable(int id)
	{
		return controller.isItemStackable(id);
	}
	public int GetRandomNPC(int id)
	{
		Main.logMethod("GetRandomNPC", id);
		return this.GetNearestNPC(id);
	}
	public int GetRandomNPC(int id[])
	{
		Main.logMethod("GetRandomNPC", id);
		for(int i : id) {
			int tmp = GetRandomNPC(i);
			
			if(tmp != -1)
				return tmp;
		}
		
		return -1;
	}
	public int PID() {
		Main.logMethod("PID");
		return 0; //PID isn't a "real thing" anymore :( 
	}
	
	public void WaitForBatchFinish() {
		while(controller.isBatching()) controller.sleep(10);
	}
}