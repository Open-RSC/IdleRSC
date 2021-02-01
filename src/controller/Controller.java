package controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;
import com.openrsc.client.entityhandling.defs.SpellDef;
import com.openrsc.client.entityhandling.instances.Item;
import com.openrsc.interfaces.misc.BankInterface;

import bot.Main;
import com.openrsc.interfaces.misc.ProgressBarInterface;
import orsc.ORSCApplet;
import orsc.ORSCharacter;
import orsc.OpenRSC;
import orsc.mudclient;
import orsc.enumerations.MessageType;
import orsc.enumerations.ORSCharacterDirection;
import orsc.graphics.gui.MessageHistory;
import orsc.graphics.gui.Panel;
import orsc.graphics.three.RSModel;
import orsc.graphics.two.MudClientGraphics;
import orsc.net.Network_Socket;
import reflector.Reflector;

/**
 *
 * This is the native scripting library abstraction layer for IdleRSC.
 *
 *
 * No documentation ATM because for the most part this is self-documenting.
 * If you would like to document these, please respond to the documentation issue on Gitlab.
 *
 * @author Dvorak
 *
 */

public class Controller {
	private Reflector reflector;
	private OpenRSC client;
	private mudclient mud;

	int[] foodIds = {350, 352, 355, 357, 359, 362, 364, 367, 370, 373, 718, 551, 553, 555, 590, 546, 1193, 1191, 325, 326, 327, 328, 329, 330, 332, 333, 334, 335, 336, 750, 751, 257, 258, 259, 261, 262, 263, 210, 1102, 346, 709, 18, 228, 1269, 320, 862, 749, 337, 132, 138, 142, 179};

	final private int GAME_TICK_COUNT = 640;

	public Controller(Reflector _reflector, OpenRSC _client, mudclient _mud) {
		reflector = _reflector; client = _client; mud = _mud;
	}

	public boolean isRunning() {
		return Main.isRunning();
	}

	public void stop() {
		Main.setRunning(false);
	}

	public void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isLoaded() {
		return (int)reflector.getObjectMember(mud, "controlLoginStatus2") == 1;
	}

	public boolean isLoggedIn() {
		if(mud.getUsername().equals("")) {
			return false;
		}
		return true;
	}

	public void typeKey(char key) {
		client.keyPressed(new KeyEvent(client, 1, 20, 1, 10, key));
	}

	public void chatMessage(String text) {
		for(char c : text.toCharArray()) {
			typeKey(c);
		}
		typeKey('\n');
	}

	public int currentMouseX() {
		return mud.mouseX;
	}

	public int currentMouseY() {
		return mud.mouseY;
	}

	public void moveMouse(int x, int y) {
		if(x < 0 || y < 0)
			return;

		client.mouseMoved(new MouseEvent(client, 1, 20, 0, x, y, 0, false));
	}

	public void clickMouse(int x, int y) {
		if(x < 0 || y < 0)
			return;

		moveMouse(x, y);
		client.mousePressed(new MouseEvent(client, 1, 21, 0, x, y, 1, false));
	}

	public void setFightMode(int mode) {
		mud.setCombatStyle(mode);

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(29);
		mud.packetHandler.getClientStream().bufferBits.putByte(mode);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public int getFightMode() {
		return (int) reflector.getObjectMember(mud, "combatStyle");
	}

	public boolean isItemInInventory(int id) {
		if(id == 1263) //sleeping bag hook
			return true;

		return mud.getInventoryCount(id) > 0;
	}

	/**
	 * Returns the itemId of the item at the specified `slotIndex`.
	 *
	 * If there is no item, the return value is -1.
	 *
	 * @param slotIndex
	 * @return itemId
	 */
	public int getInventorySlotItemId(int slotIndex) {
		if(mud.getInventoryItemCount() - 1 > slotIndex)
			return -1;

		return mud.getInventory()[slotIndex].getItemDef().id;
		//return mud.getInventoryItems()[slotIndex];
	}

	public int getInventoryItemCount() {
		return mud.getInventoryItemCount();
	}

	public int getInventoryItemCount(int id) {
		if(id == 1263) //sleeping bag hook
			return 1;

		return mud.getInventoryCount(id);
	}

	public void panCamera() {
		mud.cameraRotation++;
		if(mud.cameraRotation >= 360)
			mud.cameraRotation = 0;
	}

	public ORSCharacter getPlayer() {
		return mud.getLocalPlayer();
	}

	public ORSCharacter getPlayer(int id) {
		return mud.getPlayer(id);
	}

	public ORSCharacter[] getPlayers() {
		return (ORSCharacter[]) reflector.getObjectMember(mud, "players");
	}

	public int getPlayerCount() {
		return (int) reflector.getObjectMember(mud, "playerCount");
	}

	public boolean isCurrentlyWalking() {
		int x = mud.getLocalPlayerX();
		int z = mud.getLocalPlayerZ();

		sleep(50);

		x -= mud.getLocalPlayerX();
		z -= mud.getLocalPlayerZ();

		return x != 0 || z != 0;
	}

	public int currentX() {
		return mud.getLocalPlayerX() + mud.getMidRegionBaseX();
	}

	public int currentZ() {
		return mud.getLocalPlayerZ() + mud.getMidRegionBaseZ();
	}
	
	public void walkTo(int x, int y) {
		walkTo(x, y, 0, true);
	}

	public void walkTo(int x, int z, int radius, boolean force) { //offset applied
		if(x < 0 || z < 0)
			return;

		Main.logMethod("WalkTo", x, z, radius);

		if(force) {
			walkToActionSource(mud, mud.getLocalPlayerX(), mud.getLocalPlayerZ(), x - mud.getMidRegionBaseX(), z - mud.getMidRegionBaseZ(), false);
		}

		while( (currentX() < x - radius) ||
			   (currentX() > x + radius) ||
			   (currentZ() < z - radius) ||
			   (currentZ() > z + radius) ) { //offset applied

			int fudgeFactor = ThreadLocalRandom.current().nextInt(0 - radius, radius + 1);

			//previously: currentX() != x || currentZ() != z
			walkToActionSource(mud, mud.getLocalPlayerX(), mud.getLocalPlayerZ(), x - mud.getMidRegionBaseX() + fudgeFactor, z - mud.getMidRegionBaseZ() + fudgeFactor, false);

			sleep(250);
		}

	}

	public void walkToAsync(int x, int z, int radius) { //offset applied
		if(x < 0 || z < 0)
			return;

		Main.logMethod("WalkToAsync", x, z, radius);

		int fudgeFactor = ThreadLocalRandom.current().nextInt(0 - radius, radius + 1);

		walkToActionSource(mud, mud.getLocalPlayerX(), mud.getLocalPlayerZ(), x - mud.getMidRegionBaseX() + fudgeFactor, z - mud.getMidRegionBaseZ() + fudgeFactor, false); //TODO: change to packet based.


	}

	public boolean isTileEmpty(int x, int z) {
		int count = (int)reflector.getObjectMember(mud, "gameObjectInstanceCount");
		int[] xs = (int[])reflector.getObjectMember(mud, "gameObjectInstanceX");
		int[] zs = (int[])reflector.getObjectMember(mud, "gameObjectInstanceZ");

		for(int i = 0; i < count; i++) {
			int _x = offsetX(xs[i]);
			int _z = offsetZ(zs[i]);

			if(x == _x && z == _z)
				return false;

		}

		return true;
	}

	public int[] getNearestObjectById(int id) {
		Main.logMethod("getNearestObjectById", id);
		int count = (int)reflector.getObjectMember(mud, "gameObjectInstanceCount");
		int[] xs = (int[])reflector.getObjectMember(mud, "gameObjectInstanceX");
		int[] zs = (int[])reflector.getObjectMember(mud, "gameObjectInstanceZ");
		int[] ids = (int[])reflector.getObjectMember(mud, "gameObjectInstanceID");

		int[] closestCoords = {-1, -1};
		int closestDistance = 99999;


		for(int i = 0; i < count; i++) {
			if(ids[i] == id) {
				int x = offsetX(xs[i]);
				int z = offsetZ(zs[i]);
				int dist = distance(this.currentX(), this.currentZ(), x, z);
				if(dist < closestDistance) {
					closestDistance = dist;
					closestCoords[0] = x;
					closestCoords[1] = z;
				}
			}
		}

		if(closestCoords[0] == -1)
			return null;

		return closestCoords;
	}

	public boolean atObject(int x, int z) {
		Main.logMethod("atObject", x, z);
		int count = (int)reflector.getObjectMember(mud, "gameObjectInstanceCount");
		int[] xs = (int[])reflector.getObjectMember(mud, "gameObjectInstanceX");
		int[] zs = (int[])reflector.getObjectMember(mud, "gameObjectInstanceZ");
		int[] ids = (int[])reflector.getObjectMember(mud, "gameObjectInstanceID");

		for(int i = 0; i < count; i++) {
			if(offsetX(xs[i]) == x && offsetZ(zs[i]) == z) {
				objectAt(offsetX(xs[i]), offsetZ(zs[i]), this.getDirection(offsetX(xs[i]), offsetZ(zs[i])), ids[i]);
				return true;
			}
		}

		return false;
	}

	public boolean atObject2(int x, int z) {
		Main.logMethod("atObject2", x, z);
		int count = (int)reflector.getObjectMember(mud, "gameObjectInstanceCount");
		int[] xs = (int[])reflector.getObjectMember(mud, "gameObjectInstanceX");
		int[] zs = (int[])reflector.getObjectMember(mud, "gameObjectInstanceZ");
		int[] ids = (int[])reflector.getObjectMember(mud, "gameObjectInstanceID");

		for(int i = 0; i < count; i++) {
			if(offsetX(xs[i]) == x && offsetZ(zs[i]) == z) {
				objectAt2(offsetX(xs[i]), offsetZ(zs[i]), 4, ids[i]);
				return true;
			}
		}

		return false;
	}

	public boolean isCloseToCoord(int x, int z) {
		System.out.println(currentX() + ", " + currentZ() + ", " + x + ", " + z);
		System.out.println(distance(currentX(), currentZ(), x, z));
		if(this.distance(currentX(), currentZ(), x, z) <= 1)
			return true;

		return false;
	}

	public void objectAt(int x, int z, int dir, int objectId) { //gets called with coords WITH offset.
		Main.logMethod("objectAt", x, z, dir, objectId);
		if(x < 0 || z < 0)
			return;

		//if(!isCloseToCoord(x, z))
		//	reflector.mudInvoker(mud, "walkToObject", x, z, dir, 5126, objectId);
		reflector.mudInvoker(mud, "walkToObject", x - mud.getMidRegionBaseX(), z - mud.getMidRegionBaseZ(), dir, 5126, objectId);

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(136);
		mud.packetHandler.getClientStream().bufferBits.putShort(x);
		mud.packetHandler.getClientStream().bufferBits.putShort(z);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void objectAt2(int x, int z, int dir, int objectId) {
		Main.logMethod("objectAt2", x, z, dir, objectId);
		if(x < 0 || z < 0)
			return;

		reflector.mudInvoker(mud, "walkToObject", x - mud.getMidRegionBaseX(), z - mud.getMidRegionBaseZ(), dir, 5126, objectId);

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(79);
		mud.packetHandler.getClientStream().bufferBits.putShort(x);
		mud.packetHandler.getClientStream().bufferBits.putShort(z);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public int getObjectCount() {
		return (int)reflector.getObjectMember(mud, "gameObjectInstanceCount");
	}

	public int getNpcCount() {
		return mud.getNpcCount();
	}

	public ORSCharacter getNearestNpcByIds(int[] npcIds, boolean inCombatAllowed) {
		ORSCharacter npc = null;
		ORSCharacter[] npcs = (ORSCharacter[]) reflector.getObjectMember(mud, "npcs");
		int npcCount = (int) reflector.getObjectMember(mud, "npcCount");

		int botX = mud.localPlayer.currentX;
		int botZ = mud.localPlayer.currentZ;
		int closestDistance = Integer.MAX_VALUE;
		int closestNpcIndex = -1;

		for(int i = 0; i < npcCount; i++) {

			ORSCharacter curNpc = npcs[i];
			for(int j = 0; j < npcIds.length; j++) {
				if(curNpc.npcId == npcIds[j]) {

					if(inCombatAllowed == false) {
						if(this.isNpcInCombat(curNpc.serverIndex) == true) {
							continue;
						}
					}

					int result = distance(curNpc.currentX, curNpc.currentZ, botX, botZ);
					if(result < closestDistance) {
						closestDistance = result;
						npc = curNpc;
					}
				}
			}
		}

		//npcs[i].serverIndex
		return npc;
	}

	public ORSCharacter getNearestNpcById(int npcId, boolean inCombatAllowed) {
		int[] tmp = new int[1];
		tmp[0] = npcId;

		return getNearestNpcByIds(tmp, inCombatAllowed);
	}

	public int[] getNpcCoordsByServerIndex(int serverIndex) {
		ORSCharacter[] npcs = (ORSCharacter[]) reflector.getObjectMember(mud, "npcs");

		for(ORSCharacter npc : npcs) {
			if(npc.serverIndex == serverIndex) {
				return new int[] { this.convertX(npc.currentX), this.convertZ(npc.currentZ) };
			}
		}

		return new int[] {-1, -1};
	}

	public void walktoNPCAsync(int npcServerIndex, int radius) {
		if(npcServerIndex < 0)
			return;

		ORSCharacter npc = (ORSCharacter) reflector.mudInvoker(mud, "getServerNPC", npcServerIndex);
		if(npc != null) {
			int npcX = (npc.currentX - 64) / mud.getTileSize() + mud.getMidRegionBaseX();
			int npcZ = (npc.currentZ - 64) / mud.getTileSize() + mud.getMidRegionBaseZ();

			walkToAsync(npcX, npcZ, radius);
		} else {
			return;
		}
	}

	public void walktoNPC(int npcServerIndex, int radius) {
		if(npcServerIndex < 0)
			return;

		ORSCharacter npc = (ORSCharacter) reflector.mudInvoker(mud, "getServerNPC", npcServerIndex);
		if(npc != null) {
			int npcX = (npc.currentX - 64) / mud.getTileSize() + mud.getMidRegionBaseX();
			int npcZ = (npc.currentZ - 64) / mud.getTileSize() + mud.getMidRegionBaseZ();

			walkTo(npcX, npcZ, radius, true);
		} else {
			return;
		}
	}

	public void attackNpc(int npcServerIndex) {
		Main.logMethod("attackNpc", npcServerIndex);

		if(npcServerIndex < 0)
				return;

		walktoNPCAsync(npcServerIndex, 1);


		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(190);
		mud.packetHandler.getClientStream().bufferBits.putShort(npcServerIndex);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void castSpellOnNpc(int serverIndex, int spellId) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(50);
		mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
		mud.packetHandler.getClientStream().bufferBits.putShort(serverIndex);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void useItemOnNpc(int serverIndex, int itemId) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(135);
		mud.packetHandler.getClientStream().bufferBits.putShort(serverIndex);
		mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void useItemSlotOnObject(int x, int y, int itemSlot) {
		reflector.mudInvoker(mud, "walkToObject", x - mud.getMidRegionBaseX(), y - mud.getMidRegionBaseZ(), 4, 5126, this.getObjectAtCoord(x, y));
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(115);
		mud.packetHandler.getClientStream().bufferBits.putShort(x);
		mud.packetHandler.getClientStream().bufferBits.putShort(y);
		mud.packetHandler.getClientStream().bufferBits.putShort(itemSlot);
		mud.packetHandler.getClientStream().finishPacket();
	}
	
	public void useItemIdOnObject(int x, int y, int itemId) {
		useItemSlotOnObject(x, y, this.getInventoryItemIdSlot(itemId));
	}

	public void useItemOnWall(int x, int y, int slotId) {
		//you need to be close to the object for this to work.
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(161);
		mud.packetHandler.getClientStream().bufferBits.putShort(x);
		mud.packetHandler.getClientStream().bufferBits.putShort(y);
		mud.packetHandler.getClientStream().bufferBits.putByte(0);
		mud.packetHandler.getClientStream().bufferBits.putShort(slotId);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void thieveNpc(int serverIndex) {
		npcCommand1(serverIndex);
	}

	public void npcCommand1(int serverIndex) {
		Main.logMethod("npcCommand1", serverIndex);
		walktoNPCAsync(serverIndex, 1);

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(202);
		mud.packetHandler.getClientStream().bufferBits.putShort(serverIndex);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void npcCommand2(int serverIndex) {
		Main.logMethod("npcCommand2", serverIndex);
		walktoNPCAsync(serverIndex, 1);

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(203);
		mud.packetHandler.getClientStream().bufferBits.putShort(serverIndex);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public boolean isNpcInCombat(int id) {
		ORSCharacter[] npcs = (ORSCharacter[]) reflector.getObjectMember(mud, "npcs");
		int npcCount = (int) reflector.getObjectMember(mud, "npcCount");

		for(int i = 0; i < npcCount; i++) {
			if(npcs[i].serverIndex == id)
				if(npcs[i].combatTimeout > 0)
					return true;
		}

		return false;
	}

	public boolean isDoorOpen(int x, int z) {
		int[] ids = (int[]) reflector.getObjectMember(mud, "wallObjectInstanceID");
		int[] xs = (int[]) reflector.getObjectMember(mud, "wallObjectInstanceX");
		int[] zs = (int[]) reflector.getObjectMember(mud, "wallObjectInstanceZ");
		int count = (int) reflector.getObjectMember(mud, "wallObjectInstanceCount");

		int _x = removeOffsetX(x), _z = removeOffsetZ(z);

		for(int i = 0; i < count; i++) {
			if(xs[i] == _x && zs[i] == _z)
				if(ids[i] == 2)
					return false;
		}

		return true;
	}

	public void openDoor(int x, int z) {

		if(isDoorOpen(x, z) == true) {
			System.out.println("door already open");
			return;
		}

		while(isDoorOpen(x, z) == false) {
			reflector.mudInvoker(mud, "walkToWall", this.removeOffsetX(x), this.removeOffsetZ(z), 0);
			while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
			mud.packetHandler.getClientStream().newPacket(127);
			mud.packetHandler.getClientStream().bufferBits.putShort(x);
			mud.packetHandler.getClientStream().bufferBits.putShort(z);
			mud.packetHandler.getClientStream().bufferBits.putByte(0); //direction
			mud.packetHandler.getClientStream().finishPacket();

			sleep(GAME_TICK_COUNT);
		}
	}

	public void closeDoor(int x, int z) {

		if(isDoorOpen(x, z) == false) {
			System.out.println("door already open");
			return;
		}

		while(isDoorOpen(x, z) == true) {
			reflector.mudInvoker(mud, "walkToWall", this.removeOffsetX(x), this.removeOffsetZ(z), 0);
			while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
			mud.packetHandler.getClientStream().newPacket(127);
			mud.packetHandler.getClientStream().bufferBits.putShort(x);
			mud.packetHandler.getClientStream().bufferBits.putShort(z);
			mud.packetHandler.getClientStream().bufferBits.putByte(0); //direction
			mud.packetHandler.getClientStream().finishPacket();

			sleep(GAME_TICK_COUNT);
		}
	}

	public boolean isItemAtCoord(int x, int y, int itemId) {
		int groundItemCount = (int) reflector.getObjectMember(mud, "groundItemCount");
		int[] groundItemID = (int[]) reflector.getObjectMember(mud, "groundItemID");
		int[] groundItemX = (int[]) reflector.getObjectMember(mud, "groundItemX");
		int[] groundItemZ = (int[]) reflector.getObjectMember(mud, "groundItemZ");

		for(int i = 0; i < groundItemCount; i++) {
			if(groundItemID[i] == itemId) {
				if(groundItemX[i] + mud.getMidRegionBaseX() == x
				&& groundItemZ[i] + mud.getMidRegionBaseZ() == y) {
					return true;
				}
			}
		}

		return false;
	}

	public int[] getNearestItemById(int itemId) {
		int groundItemCount = (int) reflector.getObjectMember(mud, "groundItemCount");
		int[] groundItemID = (int[]) reflector.getObjectMember(mud, "groundItemID");
		int[] groundItemX = (int[]) reflector.getObjectMember(mud, "groundItemX");
		int[] groundItemZ = (int[]) reflector.getObjectMember(mud, "groundItemZ");

		int botX = mud.getLocalPlayerX() + mud.getMidRegionBaseX();
		int botZ = mud.getLocalPlayerX() + mud.getMidRegionBaseX();
		int closestDistance = 99999;
		int closestItemIndex = -1;

		for(int i = 0; i < groundItemCount; i++) {
			if(itemId == groundItemID[i]) {
				int result = distance(groundItemX[i] + mud.getMidRegionBaseX(), groundItemZ[i] + mud.getMidRegionBaseZ(), botX, botZ);
				if(result < closestDistance) {
					//Main.logMethod("getnearestitem bleh", botX, botZ, groundItemX[i], groundItemZ[i], result, closestDistance);
					closestDistance = result;
					closestItemIndex = i;
				}
			}
		}

		if(closestItemIndex == -1) {
			return null;
		}

		return new int[] {groundItemX[closestItemIndex] + mud.getMidRegionBaseX(), groundItemZ[closestItemIndex] + mud.getMidRegionBaseZ()};
	}

	public int[] getNearestItemByIds(int[] itemIds) {
		for(int itemId : itemIds) {
			int[] result = getNearestItemById(itemId);

			if(result != null)
				return new int[] {result[0], result[1], itemId};

		}

		return null;
	}

	public void pickupItem(int x, int z, int itemId, boolean reachable, boolean async) {
		if(x < 0 || z < 0)
			return;

		Main.logMethod("pickupItem", x, z, itemId);

		Main.logMethod("pickupItem calling walkTo...", x, z);
		if(reachable) {
			if(async)
				this.walkToAsync(x, z, 0);
			else
				walkTo(x, z, 0, false);
		} else {
			if(async)
				this.walkToAsync(x, z, 1);
			else
				this.walkTo(x, z, 1, false);

		}

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(247);
		mud.packetHandler.getClientStream().bufferBits.putShort(x);
		mud.packetHandler.getClientStream().bufferBits.putShort(z);
		mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
		mud.packetHandler.getClientStream().finishPacket();
	}


	public boolean itemCommand(int itemId) {
		Main.logMethod("itemCommand", itemId);

		int inventoryIndex = getInventoryItemIdSlot(itemId);

		if(inventoryIndex == -1)
			return false;

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(90);
		mud.packetHandler.getClientStream().bufferBits.putShort(inventoryIndex);
		mud.packetHandler.getClientStream().bufferBits.putInt(1);
		mud.packetHandler.getClientStream().bufferBits.putByte(0);
		mud.packetHandler.getClientStream().finishPacket();

		return true;
	}

	public boolean itemCommandBySlot(int slotId) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(90);
		mud.packetHandler.getClientStream().bufferBits.putShort(slotId);
		mud.packetHandler.getClientStream().bufferBits.putInt(1);
		mud.packetHandler.getClientStream().bufferBits.putByte(0);
		mud.packetHandler.getClientStream().finishPacket();

		return true;
	}

	public void useItemOnItemBySlot(int slot1, int slot2) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(91);
		mud.packetHandler.getClientStream().bufferBits.putShort(slot1);
		mud.packetHandler.getClientStream().bufferBits.putShort(slot2);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public int getInventoryItemIdSlot(int itemId) {

		if(itemId == 1263) //sleeping bag hook
			return 0;

		int inventoryItemCount = (int) reflector.getObjectMember(mud, "inventoryItemCount");
		int[] inventoryItemID = this.getInventoryItemIds(); //(int[]) reflector.getObjectMember(mud, "inventoryItemID");
		int inventoryIndex = -1;

		for(int i = 0; i < inventoryItemCount; i++) {
			if(inventoryItemID[i] == itemId)
				inventoryIndex = i;
		}

		return inventoryIndex;
	}

	public void dropItem(int slot) {
		int inventoryItemID = mud.getInventoryItemID(slot);
		int inventoryItemCount = this.getInventoryItemCount(inventoryItemID);

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(246);
		mud.packetHandler.getClientStream().bufferBits.putShort(slot);
		mud.packetHandler.getClientStream().bufferBits.putInt(inventoryItemCount);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public boolean isEquipped(int slot) {
		return mud.getInventory()[slot].getEquipped();
//
//		int[] inventoryItemEquipped = (int[]) reflector.getObjectMember(mud, "inventoryItemEquipped");
//
//		if(slot != -1) {
//			return inventoryItemEquipped[slot] > 0;
//		}
//
//		return false;
	}


	public void equipItem(int slot) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(169);
		mud.packetHandler.getClientStream().bufferBits.putShort(slot);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void unequipItem(int slot) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(170);
		mud.packetHandler.getClientStream().bufferBits.putShort(slot);
		mud.packetHandler.getClientStream().finishPacket();
	}


	public void login() {
		Main.logMethod("login", "nothing");


		reflector.setObjectMember(mud, "loginScreenNumber", 2);

		Panel panelLogin = (Panel) reflector.getObjectMember(mud, "panelLogin");

		int controlLoginUser = (int) reflector.getObjectMember(mud, "controlLoginUser");
		int controlLoginPass = (int) reflector.getObjectMember(mud, "controlLoginPass");

		panelLogin.setText(controlLoginUser, Main.username);
		panelLogin.setText(controlLoginPass, Main.password);

		reflector.setObjectMember(mud, "enterPressed", true);


	}

	public int getFatigue() {
		return mud.getStatFatigue();
	}

	public int getFatigueDuringSleep() {
		return (int) reflector.getObjectMember(mud, "fatigueSleeping");
	}

	public boolean isInCombat() {
		return ((int)reflector.getObjectMember(mud, "combatTimeout")) == 499;
	}

	public boolean isPlayerInCombat(int id) {
		if(mud.getPlayer(id) == null)
			return false;

		return mud.getPlayer(id).combatTimeout == 499;
	}

	public boolean isInOptionMenu() {
		return (boolean) reflector.getObjectMember(mud, "optionsMenuShow");
	}

	public int optionMenuCount() {
		return (int) reflector.getObjectMember(mud, "optionsMenuCount");
	}

	public String optionsMenuText(int i) {
		String[] optionsMenuText = (String[]) reflector.getObjectMember(mud, "optionsMenuText");
		if(i < optionsMenuText.length)
			return optionsMenuText[i];

		return null;
	}

	public void optionAnswer(int answer) {
		if(answer >= optionMenuCount())
			return;

		Main.logMethod("optionAnswer", answer);

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(116);
		mud.packetHandler.getClientStream().bufferBits.putByte(answer);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public boolean talkToNpc(int serverIndex) {//, boolean waitForOptionsMenu) {
		if(serverIndex < 0)
			return false;

		walktoNPCAsync(serverIndex, 0);

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(153);
		mud.packetHandler.getClientStream().bufferBits.putShort(serverIndex);
		mud.packetHandler.getClientStream().finishPacket();

		return true;
	}

	public boolean isInBank() {
		return (boolean) reflector.getObjectMember(mud, "showDialogBank");
	}

	public boolean openBank() {
		final int[] bankerIds = { 95, 224, 268, 485, 540, 617 };

		if(isInBank() == true)
			return true;

		if(this.isInOptionMenu() == false) {
			for(int npcId : bankerIds) {
				ORSCharacter npc = getNearestNpcById(npcId, false);
				if(npc != null) {
					thieveNpc(npc.serverIndex);
					break;
				} else {
					return false;
				}
			}


			sleep(3000);
		}

		return false;
	}

	public void closeBank() {
		reflector.setObjectMember(mud, "showDialogBank", false);
	}

	public int getBankItemCount(int id) {
		ArrayList<Object> bankItems = (ArrayList<Object>) reflector.getObjectMemberFromSuperclass(mud.getBank(), "bankItems");

		for(Object item : bankItems) {
			int itemId = (int) reflector.getObjectMember(item, "itemID");
			int amount = (int) reflector.getObjectMember(item, "amount");

			if(itemId == id)
				return amount;

		}

		return -1;
	}

	public boolean isItemInBank(int id) {
		return getBankItemCount(id) > 0;
	}

	public boolean depositItem(int itemId) {
		return depositItem(itemId, 1);
	}

	public boolean depositItem(int itemId, int amount) {
		if(isInBank() == false)
			return false;

		if(isItemInInventory(itemId) == false)
			return false;

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(23);
		mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
		mud.packetHandler.getClientStream().bufferBits.putInt(amount);
		mud.packetHandler.getClientStream().finishPacket();

		return true;
	}

	public boolean withdrawItem(int itemId) {
		return withdrawItem(itemId, 1);
	}

	public boolean withdrawItem(int itemId, int amount) {
		if(isInBank() == false)
			return false;

		if(getInventoryItemCount(itemId) >= amount)
			return true;

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(22);
		mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
		mud.packetHandler.getClientStream().bufferBits.putInt(amount);
		mud.packetHandler.getClientStream().finishPacket();

		return false;
	}

	public void displayMessage(String msg, int type) {
		reflector.mudInvoker(mud, "showMessage", false, "", msg, MessageType.lookup(type), 0, "");
	}

	public void displayMessage(String msg) {
		reflector.mudInvoker(mud, "showMessage", false, "", msg, MessageType.GAME, 0, "");
	}



	public int distance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	public int offsetX(int x) {
		return x + mud.getMidRegionBaseX();
	}

	public int offsetZ(int z) {
		return z + mud.getMidRegionBaseZ();
	}

	public int removeOffsetX(int x) {
		return x - mud.getMidRegionBaseX();
	}

	public int removeOffsetZ(int z) {
		return z - mud.getMidRegionBaseZ();
	}

	public int convertX(int x) { //for usage with playre/npc coords only!!
		return (x - 64) / mud.getTileSize() + mud.getMidRegionBaseX();
	}

	public int convertZ(int z) {
		return (z - 64) / mud.getTileSize() + mud.getMidRegionBaseZ();
	}

	private MudClientGraphics getMudGraphics() {
		return (MudClientGraphics) reflector.getObjectMember(mud, "surface");
	}

	public void resizeWindow(int width, int height) {
		mud.resizeWidth = width;
		mud.resizeHeight = height;
	}

	public void takeScreenshot() {
		MudClientGraphics gfx = getMudGraphics();


	    final int numSnapshots = 5;
		int[][] snapshots = new int[numSnapshots][gfx.width2 * gfx.height2];
		int[] finalSnapshot = new int[gfx.width2 * gfx.height2];

		BufferedImage img = new BufferedImage(gfx.width2, gfx.height2, BufferedImage.TYPE_INT_RGB);


		//to deal with the scanline problem: make multiple copies
		//have non-black override black to "average out"
		for(int i = 0; i < numSnapshots; i++) {
			System.arraycopy(gfx.pixelData, 0, snapshots[i], 0, gfx.width2 * gfx.height2);
			this.sleep(ThreadLocalRandom.current().nextInt(1, 5+1));
		}

		for(int i = 0; i < numSnapshots; i++) {
			for(int x = 0; x < gfx.width2; x++) {
				for(int y = 0; y < gfx.height2; y++) {
					if(snapshots[i][(y * gfx.width2) + x] != 0) { //assuming it's a true black.
						finalSnapshot[(y * gfx.width2) + x] = snapshots[i][(y * gfx.width2) + x];
					}
				}
			}
		}

		for(int x = 0; x < gfx.width2; x++) {
			for(int y = 0; y < gfx.height2; y++) {
				img.setRGB(x, y, finalSnapshot[(y*gfx.width2) + x]);
			}
		}

		try {
			ImageIO.write(img, "bmp", new File("screenshot.bmp"));
			Main.log("saved");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//utilities
	public String getItemCommand(int itemId) {
		ItemDef item = EntityHandler.getItemDef(itemId);

		if(item == null)
			return null;

		String[] commands = item.getCommand();

		if(commands == null)
			return null;

		return commands[0];
	}

	public String getItemExamineText(int itemId) {
		return EntityHandler.getItemDef(itemId).getDescription();
	}

	public String getItemName(int itemId) {
		return EntityHandler.getItemDef(itemId).getName();
	}

	public int getItemId(String itemName) {
		try {
			for(int i = 0; i <= 10000; i++) {
				if(EntityHandler.getItemDef(i).getName().toLowerCase().equals(itemName.toLowerCase())) {
					return i;
				}
			}
		} catch(Exception e) {
			return -1;
		}

		return -1;
	}

	public boolean isItemWearable(int itemId) {
		return EntityHandler.getItemDef(itemId).isWieldable();
	}

	public boolean isItemStackable(int itemId) {
		return EntityHandler.getItemDef(itemId).isStackable();
	}

	public ArrayList<ORSCMessage> getMessages() {
		ArrayList<ORSCMessage> result = new ArrayList<ORSCMessage>();

		String[] colors = MessageHistory.messageHistoryColor;
		int[] crowns = MessageHistory.messageHistoryCrownID;
		String[] messages = MessageHistory.messageHistoryMessage;
		String[] senders = MessageHistory.messageHistorySender;
		int[] timeouts = MessageHistory.messageHistoryTimeout;
		MessageType[] types = MessageHistory.messageHistoryType;

		for(int i = 0; i < messages.length; i++) {
			result.add(new ORSCMessage(colors[i], crowns[i], messages[i], senders[i], timeouts[i], types[i]));
		}


		return result;
	}

	public void setServerMessage(String msg, boolean largeBox, boolean show) {
		mud.setServerMessage(msg);
		mud.setServerMessageBoxTop(largeBox);
		mud.setShowDialogServerMessage(show);
		return;
	}

	public void closeServerMessage() {
		mud.setShowDialogServerMessage(false);
	}

	public String getServerMessage() {
		return (String) reflector.getObjectMember(mud, "serverMessage");
	}

	public int getSpellIdFromName(String name) {
		for(int i = 0; i < EntityHandler.spellCount(); i++) {
			SpellDef d = EntityHandler.getSpellDef(i);
			if(EntityHandler.getSpellDef(i).getName().toLowerCase().equals(name.toLowerCase()))
				return i;
		}

		return -1;
	}

	public void castSpellOnObject(int spellId, int x, int y) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(99);
		mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
		mud.packetHandler.getClientStream().bufferBits.putShort(x);
		mud.packetHandler.getClientStream().bufferBits.putShort(y);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void castSpellOnInventoryItem(int spellId, int slotId) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(4);
		mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
		mud.packetHandler.getClientStream().bufferBits.putShort(slotId);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void castSpellOnGroundItem(int spellId, int itemId, int x, int y) {
		int a = mud.getMidRegionBaseX();
		int b = mud.getMidRegionBaseZ();

		int direction = getDirection(x, y);

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(249);
		mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
		mud.packetHandler.getClientStream().bufferBits.putShort(x);
		mud.packetHandler.getClientStream().bufferBits.putShort(y);
		mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public int getDirection(int x, int y) {
		ORSCharacterDirection direction = null;

		if(x > currentX()) {
			direction = ORSCharacterDirection.WEST;
		} else if (x < currentX()) {
			direction = ORSCharacterDirection.EAST;
		}

		if(y > currentZ()) {
			if(direction != null) {
				if(direction == ORSCharacterDirection.WEST) {
					direction = ORSCharacterDirection.SOUTH_WEST;
				} else {
					direction = ORSCharacterDirection.SOUTH_EAST;
				}
			} else {
				direction = ORSCharacterDirection.SOUTH;
			}
		} else if (y < currentZ() ) {
			if(direction != null) {
				if(direction == ORSCharacterDirection.WEST) {
					direction = ORSCharacterDirection.NORTH_WEST;
				} else {
					direction = ORSCharacterDirection.NORTH_EAST;
				}
			} else {
				direction = ORSCharacterDirection.NORTH;
			}
		}

		return direction.rsDir;
	}

	public String getNpcCommand1(int npcId) {
		return EntityHandler.getNpcDef(npcId).getCommand1();
	}
	
	public String getNpcCommand2(int npcId) {
		return EntityHandler.getNpcDef(npcId).getCommand2();
	}

	public String getNpcExamineText(int npcId) {
		return EntityHandler.getNpcDef(npcId).getDescription();
	}

	public String getNpcName(int npcId) {
		return EntityHandler.getNpcDef(npcId).getName();
	}


	public boolean isNpcAttackable(int npcId) {
		return EntityHandler.getNpcDef(npcId).isAttackable();
	}

	public String getObjectCommand1(int objId) {
		return EntityHandler.getObjectDef(objId).getCommand1();
	}

	public String getObjectCommand2(int objId) {
		return EntityHandler.getObjectDef(objId).getCommand2();
	}

	public String getObjectExamineText(int objId) {
		return EntityHandler.getObjectDef(objId).getDescription();
	}

	public String getObjectName(int objId) {
		return EntityHandler.getObjectDef(objId).getName();
	}

	public int getPrayerId(String prayerName) {
		for(int i = 0; i < EntityHandler.prayerCount(); i++) {
			if(prayerName.toLowerCase().equals(EntityHandler.getPrayerDef(i).getName().toLowerCase())) {
				return i;
			}
		}

		return -1;
	}

	public int getPrayerLevel(int prayerId) {
		return EntityHandler.getPrayerDef(prayerId).getReqLevel();
	}

	public int getPrayerDrain(int prayerId) {
		return EntityHandler.getPrayerDef(prayerId).getDrainRate();
	}

	public boolean isPrayerOn(int prayerId) {
		return mud.checkPrayerOn(prayerId);
	}

	public void enablePrayer(int prayerId) {
		//TODO: check prayer lvl and return true/false based off it
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(60);
		mud.packetHandler.getClientStream().bufferBits.putByte(prayerId);
		mud.packetHandler.getClientStream().finishPacket();
		mud.togglePrayer(prayerId, true);

	}

	public void disablePrayer(int prayerId) {
		//TODO: check prayer lvl and return true/false based off it
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(254);
		mud.packetHandler.getClientStream().bufferBits.putByte(prayerId);
		mud.packetHandler.getClientStream().finishPacket();
		mud.togglePrayer(prayerId, false);
	}

	public boolean isInShop() {
		return (boolean) reflector.getObjectMember(mud, "showDialogShop");
	}

	public void closeShop() {
		reflector.setObjectMember(mud, "showDialogShop", false);
	}

	public int shopItemCount(int itemId) {
		int[] count = (int[]) reflector.getObjectMember(mud, "shopItemCount");
		int[] ids = (int[]) reflector.getObjectMember(mud, "shopItemID");
		int[] prices = (int[]) reflector.getObjectMember(mud, "shopItemPrice");

		for(int i = 0; i < ids.length; i++) {
			if(ids[i] == itemId) {
				return count[i];
			}
		}

		return -1;
	}

	public int shopItemPrice(int itemId) {
		int[] count = (int[]) reflector.getObjectMember(mud, "shopItemCount");
		int[] ids = (int[]) reflector.getObjectMember(mud, "shopItemID");
		int[] prices = (int[]) reflector.getObjectMember(mud, "shopItemPrice");

		for(int i = 0; i < ids.length; i++) {
			if(ids[i] == itemId) {
				return prices[i];
			}
		}

		return -1;
	}

	public boolean shopBuy(int itemId) {
		//TODO: check if enough coins in inventory, return false if not enough.
		if(!isInShop() || shopItemCount(itemId) < 1)
			return false;

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(236);
		mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
		mud.packetHandler.getClientStream().bufferBits.putShort(shopItemCount(itemId));
		mud.packetHandler.getClientStream().bufferBits.putShort(1);
		mud.packetHandler.getClientStream().finishPacket();

		return true;
	}

	public boolean shopSell(int itemId) {
		//TODO: check if item in inventory
		if(!isInShop() || shopItemCount(itemId) == -1)
			return false;

		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(221);
		mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
		mud.packetHandler.getClientStream().bufferBits.putShort(shopItemCount(itemId));
		mud.packetHandler.getClientStream().bufferBits.putShort(1);
		mud.packetHandler.getClientStream().finishPacket();

		return true;
	}

	public int getStatId(String statName) {
		String[] skillNames = mud.getSkillNamesLong();

		for(int i = 0; i < skillNames.length; i++) {
			if(statName.toLowerCase().equals(skillNames[i].toLowerCase()))
				return i;
		}

		return -1;
	}

	public int getBaseStat(int id) {
		return (int) ((int[]) reflector.getObjectMember(mud, "playerStatBase"))[id];
	}

	public int getCurrentStat(int id) {
		return (int) ((int[]) reflector.getObjectMember(mud, "playerStatCurrent"))[id];
	}

	public int getStatXp(int id) {
		return (int) ((long[]) reflector.getObjectMember(mud, "playerStatXpGained"))[id];
	}


//  See issue on GitLab
//	public void keyTyped(int charCode) {
//		//overrideable
//	}
//
//	public void keyPressed(int charCode) {
//		//overrideable
//		System.out.println("key pressed = " + charCode);
//	}
//
//	public void keyReleased(int charCode) {
//		//overrideable
//	}

	public void logout() {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(102);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public int getObjectAtCoord(int x, int y) {
		int _x = x - mud.getMidRegionBaseX();
		int _y = y - mud.getMidRegionBaseZ();

		int[] ids = (int[]) reflector.getObjectMember(mud, "gameObjectInstanceID");
		int[] xs = (int[]) reflector.getObjectMember(mud, "gameObjectInstanceX");
		int[] ys = (int[]) reflector.getObjectMember(mud, "gameObjectInstanceZ");

		for(int i = 0; i < ids.length; i++) {
			if(_x == xs[i] && _y == ys[i])
				return ids[i];
		}

		return -1;
	}

	public void useItemOnGroundItem(int x, int y, int itemId, int groundItemId) {
		//TODO: check if item in inventory
		//TODO: check if item is on ground
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(53);
		mud.packetHandler.getClientStream().bufferBits.putShort(x);
		mud.packetHandler.getClientStream().bufferBits.putShort(y);
		mud.packetHandler.getClientStream().bufferBits.putShort(itemId);
		mud.packetHandler.getClientStream().bufferBits.putShort(groundItemId);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public int getPlayerAtCoord(int x, int y) {
		for(ORSCharacter player : getPlayers()) {
			if(player != null) {
				if((x == convertX(player.currentX))
				&&  y == convertZ(player.currentZ)) {
					return player.serverIndex;
				}
			}
		}

		return -1;
	}

	public int getPlayerServerIndexByName(String name) {
		for(ORSCharacter player : getPlayers()) {
			if(player != null) {
				if(player.displayName.toLowerCase().equals(name.toLowerCase())) {
					return player.serverIndex;
				}
			}
		}

		return -1;
	}

	public void duelPlayer(int playerServerIndex) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(103);
		mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void followPlayer(int playerServerIndex) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(165);
		mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void attackPlayer(int playerServerIndex) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(171);
		mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void castSpellOnPlayer(int spellId, int playerServerIndex) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(229);
		mud.packetHandler.getClientStream().bufferBits.putShort(spellId);
		mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
		mud.packetHandler.getClientStream().finishPacket();
	}




	public void tradePlayer(int playerServerIndex) {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(142);
		mud.packetHandler.getClientStream().bufferBits.putShort(playerServerIndex);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public String getTradeRecipientName() {
		return (String) reflector.getObjectMember(mud, "tradeRecipientName");
	}

	public boolean isInTrade() {
		return (boolean) reflector.getObjectMember(mud, "showDialogTrade") || isInTradeConfirmation();
	}

	public boolean isInTradeConfirmation() {
		return (boolean) reflector.getObjectMember(mud, "showDialogTradeConfirm");
	}

	public boolean isTradeRecipientAccepting() {
		return (boolean) reflector.getObjectMember(mud, "tradeRecipientAccepted");
	}

	public void declineTrade() {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(230);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void acceptTrade() {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(55);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public void acceptTradeConfirmation() {
		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(104);
		mud.packetHandler.getClientStream().finishPacket();
	}

	public int[] getTradeItems() {
		return (int[]) reflector.getObjectMember(mud, "tradeItemID");
	}

	public int[] getTradeItemsCounts() {
		return (int[]) reflector.getObjectMember(mud, "tradeItemSize");

	}

	public int[] getRecipientTradeItems() {
		return (int[]) reflector.getObjectMember(mud, "tradeRecipientItem");
	}

	public int[] getRecipientTradeItemsCounts() {
		return (int[]) reflector.getObjectMember(mud, "tradeRecipientItemCount");
	}

//	example; controller.setTradeItems(new int[] {33, 36}, new int[] {1, 1});
	public boolean setTradeItems(int[] itemIds, int[] amounts) {
		if(itemIds.length != amounts.length)
			return false;

		for(int i = 0; i < itemIds.length; i++)
			if(amounts[i] > getInventoryItemCount(itemIds[i]))
				return false;


		while(mud.packetHandler.getClientStream().hasFinishedPackets() == true) sleep(1);
		mud.packetHandler.getClientStream().newPacket(46);
		mud.packetHandler.getClientStream().bufferBits.putByte(itemIds.length);

		for(int i = 0; i < itemIds.length; i++) {
			mud.packetHandler.getClientStream().bufferBits.putShort(itemIds[i]);
			mud.packetHandler.getClientStream().bufferBits.putInt(amounts[i]);
			//mud.packetHandler.getClientStream().bufferBits.putShort(0); //maybe this is only in the next release
		}

		mud.packetHandler.getClientStream().finishPacket();

		//TODO: check if updated on client side with getTradeItems()..

		return true;

	}
//
	public void removeAllTradeItems() {
		setTradeItems(new int[] { }, new int[] { });
	}

	public void setAutoLogin(boolean value) {
		Main.setAutoLogin(value);
	}

	public void setInterlacer(boolean value) {
		mud.interlace = value;
	}

	public int[] getInventoryItemIds() {
		int[] results = new int[] {};

		int i = 0;
		for(Item d : mud.getInventory()) {
			results = Arrays.copyOf(results, results.length+1);
			if(d.getItemDef() != null)
				results[i++] = d.getItemDef().id;
		}

		return results;

		//return mud.getInventoryItems();
	}

    public void walkToActionSource(mudclient mud, int startX, int startZ, int destX, int destZ, boolean walkToEntity) {
    	reflector.mudInvoker(mud, "walkToActionSource", startX, startZ, destX, destZ, walkToEntity);
    }

    public int[] getFoodIds() {
    	return foodIds;
    }

    public boolean isBatching() {
		ProgressBarInterface progressBarInterface = (ProgressBarInterface) reflector.getObjectMember(mud, "batchProgressBar");

		if(progressBarInterface == null)
			return false;
		
		return progressBarInterface.progressBarComponent.isVisible();
	}
    
    public mudclient getMud() {
    	return this.mud;
    }
}
 