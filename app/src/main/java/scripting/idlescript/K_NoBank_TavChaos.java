package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.NpcId;
import models.entities.SkillId;
import orsc.ORSCharacter;

/**
 * <b>Taverly Dungeon Chaos Druid Killer</b>
 *
 * <p>Options: Combat Style, Loot level Herbs, Loot Bones, Reg pots, Food Type, and Food Withdraw
 * Amount Selection, Chat Command Options, Full top-left GUI, regular atk/str pot option, and
 * Autostart. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_NoBank_TavChaos extends K_kailaScript {
  private boolean startUp = false;
  private int fightMode = 0;
  private int capeSwapId = 0;
  private static final int VIAL = ItemId.VIAL.getId();
  private static final int EMPTY_VIAL = ItemId.EMPTY_VIAL.getId();
  private static final int SNAPE_GRASS = ItemId.SNAPE_GRASS.getId();
  // todo build out loot based on herb level (use put)
  private final int[] DRUID_LOOT = { // length 14
    ItemId.LAW_RUNE.getId(),
    ItemId.AIR_RUNE.getId(),
    ItemId.UNID_GUAM_LEAF.getId(),
    ItemId.UNID_MARRENTILL.getId(),
    ItemId.UNID_TARROMIN.getId(), // with limp
    ItemId.UNID_HARRALANDER.getId(),
    ItemId.UNID_RANARR_WEED.getId(), // with snape
    ItemId.SNAPE_GRASS.getId(),
    ItemId.UNID_IRIT.getId(), // eye of newt
    ItemId.UNID_AVANTOE.getId(), // with snape
    ItemId.UNID_KWUARM.getId(), // with limp
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId()
  };
  private final int[] HOBS_LOOT = { // length 14
    ItemId.LAW_RUNE.getId(),
    ItemId.AIR_RUNE.getId(),
    ItemId.UNID_GUAM_LEAF.getId(),
    ItemId.UNID_MARRENTILL.getId(),
    ItemId.UNID_TARROMIN.getId(), // with limp
    ItemId.LIMPWURT_ROOT.getId(),
    ItemId.UNID_HARRALANDER.getId(),
    ItemId.UNID_RANARR_WEED.getId(), // with snape
    ItemId.UNID_IRIT.getId(), // eye of newt
    ItemId.UNID_AVANTOE.getId(), // with snape
    ItemId.UNID_KWUARM.getId(), // with limp
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
  };
  private int[] cleanedDruidLoot;
  private int[] cleanedHobsLoot;

  private final int[] UNIF_HERBS = {
    ItemId.UNID_GUAM_LEAF.getId(),
    ItemId.UNID_MARRENTILL.getId(),
    ItemId.UNID_TARROMIN.getId(), // with limp
    ItemId.UNID_HARRALANDER.getId(),
    ItemId.UNID_RANARR_WEED.getId(), // with snape
    ItemId.UNID_IRIT.getId(), // eye of newt
    ItemId.UNID_AVANTOE.getId(), // with snape
    ItemId.UNID_KWUARM.getId(), // with limp,
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
  };
  private final int[] UNIF_HERBS_TO_MAKE = {
    ItemId.UNID_RANARR_WEED.getId(), // with snape
    ItemId.UNID_AVANTOE.getId(), // with snape
    ItemId.UNID_GUAM_LEAF.getId(), // order matters here
    ItemId.UNID_IRIT.getId(), // eye of newt
    // ItemId.UNID_TARROMIN.getId(), // with limp
    ItemId.UNID_KWUARM.getId(), // with limp,
  };
  private final int[] CLEAN_HERBS = {
    ItemId.RANARR_WEED.getId(),
    ItemId.AVANTOE.getId(),
    ItemId.GUAM_LEAF.getId(), // order matters here
    ItemId.IRIT_LEAF.getId(),
    // ItemId.TARROMIN.getId(), // with limp
    ItemId.KWUARM.getId(),
  };
  private final int[] UNIF_POTS = {
    ItemId.UNF_GUAM_POTION.getId(),
    ItemId.UNF_RANARR_POTION.getId(),
    ItemId.UNF_IRIT_POTION.getId(),
    ItemId.UNF_AVANTOE_POTION.getId(),
    // ItemId.UNF_TARROMIN_POTION.getId(), // with limp
    ItemId.UNF_KWUARM_POTION.getId()
  };
  private final int[] UNNEEDED_CLEAN_HERBS = {
    ItemId.MARRENTILL.getId(),
    ItemId.TARROMIN.getId(), // with limp
    ItemId.HARRALANDER.getId(),
    ItemId.CADANTINE.getId(),
    ItemId.DWARF_WEED.getId(),
  };

  // counting methods
  private int getSnapeSecCount() {
    int count = 0;
    count += c.getInventoryItemCount(ItemId.UNF_RANARR_POTION.getId());
    count += c.getInventoryItemCount(ItemId.UNID_RANARR_WEED.getId());
    count += c.getInventoryItemCount(ItemId.RANARR_WEED.getId());
    count += c.getInventoryItemCount(ItemId.UNF_AVANTOE_POTION.getId());
    count += c.getInventoryItemCount(ItemId.UNID_AVANTOE.getId());
    count += c.getInventoryItemCount(ItemId.AVANTOE.getId());
    return count;
  }

  private int getNewtSecCount() {
    int count = 0;
    count += c.getInventoryItemCount(ItemId.UNF_GUAM_POTION.getId());
    count += c.getInventoryItemCount(ItemId.UNID_GUAM_LEAF.getId());
    count += c.getInventoryItemCount(ItemId.GUAM_LEAF.getId());
    count += c.getInventoryItemCount(ItemId.UNF_IRIT_POTION.getId());
    count += c.getInventoryItemCount(ItemId.UNID_IRIT.getId());
    count += c.getInventoryItemCount(ItemId.IRIT_LEAF.getId());
    return count;
  }

  private int getLimpSecCount() {
    int count = 0;
    count += c.getInventoryItemCount(ItemId.UNF_KWUARM_POTION.getId());
    count += c.getInventoryItemCount(ItemId.UNID_KWUARM.getId());
    count += c.getInventoryItemCount(ItemId.KWUARM.getId());
    return count;
  }

  private int getHerbCount() {
    int count = 0;
    for (int id : UNIF_HERBS_TO_MAKE) {
      count += c.getInventoryItemCount(id);
    }
    for (int id : CLEAN_HERBS) {
      count += c.getInventoryItemCount(id);
    }
    for (int id : UNIF_POTS) {
      count += c.getInventoryItemCount(id);
    }
    return count;
  }

  private int getHerbCountForVial() {
    int count = 0;
    for (int id : UNIF_HERBS_TO_MAKE) {
      count += c.getInventoryItemCount(id);
    }
    for (int id : CLEAN_HERBS) {
      count += c.getInventoryItemCount(id);
    }
    return count;
  }

  private int getUnifCount() {
    int count = 0;
    for (int id : UNIF_POTS) {
      count += c.getInventoryItemCount(id);
    }
    return count;
  }

  public int start(String[] parameters) {
    centerX = 344;
    centerY = 3318;
    centerDistance = 12;
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      c.displayMessage("@red@NO BANK Tav Druid Killer - By Kaila");
      c.displayMessage("@red@Start in tav, near hobs, or near druids");
      if (c.isInBank()) c.closeBank();
      calculateLoot();
      if (c.currentY() < 550) {
        startUp = true;
        shopToChaos();
      } else if (c.currentY() > 550 && c.currentY() < 3000) {
        startUp = true;
        hobsToChaos();
      }
      startUp = false;
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (getHerbCount() > 9 || c.getInventoryItemCount() == 30 || timeToBank) {
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        totalTrips = totalTrips + 1;
        calculateLoot();
        cleanHerbs();
        dropUnneededHerbs();
        druidToShop();
        cleanHerbs();
        dropUnneededHerbs();
        doShop(0, false);
        cleanHerbs();
        dropUnneededHerbs(); // redundant but having extra unneeded breaks something
        if ((c.getInventoryItemCount(EMPTY_VIAL) + getLimpSecCount() + getSnapeSecCount()) > 0) {
          shopToHobs();
        }
        doHerblawLoop(); // stuck here
        if ((getLimpSecCount() + getSnapeSecCount()) > 0) {
          while (c.isRunning()
              && c.getInventoryItemCount(ItemId.LIMPWURT_ROOT.getId()) < getLimpSecCount()) {
            _combatLoop(NpcId.HOBGOBLIN_LVL32.getId(), cleanedHobsLoot);
            if (c.getInventoryItemCount(ItemId.LIMPWURT_ROOT.getId()) < getLimpSecCount()) {
              int[] limp = c.getNearestItemById(ItemId.LIMPWURT_ROOT.getId());
              if (limp != null) {
                c.walkTo(limp[0], limp[1]);
                c.pickupItem(limp[0], limp[1], ItemId.LIMPWURT_ROOT.getId(), true, false);
                c.sleep(GAME_TICK);
              } else c.sleep(2 * GAME_TICK);
            }
            if (c.getInventoryItemCount(SNAPE_GRASS) < getSnapeSecCount()) {
              int[] grass = c.getNearestItemById(SNAPE_GRASS);
              if (grass != null) {
                c.walkTo(grass[0], grass[1]);
                c.pickupItem(grass[0], grass[1], SNAPE_GRASS, true, false);
                c.sleep(GAME_TICK);
              } else c.sleep(2 * GAME_TICK);
            }
          }
          while (c.isRunning() && c.getInventoryItemCount(SNAPE_GRASS) < getSnapeSecCount()) {
            int[] grass = c.getNearestItemById(SNAPE_GRASS);
            if (grass != null) {
              c.walkTo(grass[0], grass[1]);
              c.pickupItem(grass[0], grass[1], SNAPE_GRASS, true, false);
              c.sleep(GAME_TICK);
            } else c.sleep(2 * GAME_TICK);
          }
          // we have our limps and snapes now
          doHerblawLoop();
        }
        if (c.currentY() > 550 && c.currentY() < 3000) {
          doHerblawLoop();
          hobsToChaos();
          if (getNewtSecCount() > 0) { // || c.getInventoryItemCount(ItemId.EYE_OF_NEWT.getId()) < 5
            doHerblawLoop();
            buyMoreNewts();
            doHerblawLoop();
          }
        } else if (c.currentY() > 450 && c.currentY() < 550) shopToChaos();
      }
      _combatLoop(270, cleanedDruidLoot);
    }
  }

  private void buyMoreNewts() {
    c.walkTo(376, 515);
    c.walkTo(374, 509);
    c.walkTo(370, 506);
    doShop(getNewtSecCount(), true);
    if (c.isInShop()) c.closeShop();
    doHerblawLoop();
    shopToChaos();
  }

  private void calculateLoot() {
    int herblawLevel = c.getBaseStat(SkillId.HERBLAW.getId());
    // cutoffs 5,11,20,25,40,48,54,65,70
    int[] cleanDruid = Arrays.copyOf(DRUID_LOOT, DRUID_LOOT.length - 9);
    int[] cleanHobs = Arrays.copyOf(HOBS_LOOT, HOBS_LOOT.length - 9);
    if (herblawLevel >= 70) { // dwarf
      cleanDruid = DRUID_LOOT;
      cleanHobs = HOBS_LOOT;
    } else if (herblawLevel >= 65) { // cad
      cleanDruid = Arrays.copyOf(DRUID_LOOT, DRUID_LOOT.length - 1);
      cleanHobs = Arrays.copyOf(HOBS_LOOT, HOBS_LOOT.length - 1);
    } else if (herblawLevel >= 55) { // kwu
      cleanDruid = Arrays.copyOf(DRUID_LOOT, DRUID_LOOT.length - 2);
      cleanHobs = Arrays.copyOf(HOBS_LOOT, HOBS_LOOT.length - 2);
    } else if (herblawLevel >= 50) { // ava
      cleanDruid = Arrays.copyOf(DRUID_LOOT, DRUID_LOOT.length - 3);
      cleanHobs = Arrays.copyOf(HOBS_LOOT, HOBS_LOOT.length - 3);
    } else if (herblawLevel >= 45) { // irit
      cleanDruid = Arrays.copyOf(DRUID_LOOT, DRUID_LOOT.length - 4);
      cleanHobs = Arrays.copyOf(HOBS_LOOT, HOBS_LOOT.length - 4);
    } else if (herblawLevel >= 38) { // ranarr
      cleanDruid = Arrays.copyOf(DRUID_LOOT, DRUID_LOOT.length - 5); // skip 2 for limp
      cleanHobs = Arrays.copyOf(HOBS_LOOT, HOBS_LOOT.length - 5);
    } else if (herblawLevel >= 20) { // harr
      cleanDruid = Arrays.copyOf(DRUID_LOOT, DRUID_LOOT.length - 7);
      cleanHobs = Arrays.copyOf(HOBS_LOOT, HOBS_LOOT.length - 6);
    } else if (herblawLevel >= 11) { // tar
      cleanDruid = Arrays.copyOf(DRUID_LOOT, DRUID_LOOT.length - 8);
      cleanHobs = Arrays.copyOf(HOBS_LOOT, HOBS_LOOT.length - 7); // skip 2 for limp
    } else if (herblawLevel >= 5) { // mar
      cleanDruid = Arrays.copyOf(DRUID_LOOT, DRUID_LOOT.length - 9);
      cleanHobs = Arrays.copyOf(HOBS_LOOT, HOBS_LOOT.length - 9);
    }
    cleanedHobsLoot = cleanHobs;
    cleanedDruidLoot = cleanDruid;
    c.log("Herblaw level is " + herblawLevel + ", looting the following...");
    StringBuilder resultStr = new StringBuilder();
    for (int herb : cleanedDruidLoot) {
      resultStr.append(ItemId.getById(herb).name()).append(",");
    }
    c.log(resultStr.toString());
  }
  // herblaw action methods
  private void cleanHerbs() {
    for (int id : UNIF_HERBS) {
      if (c.getInventoryItemCount(id) > 0) {
        c.itemCommand(id);
        c.sleep(640);
        c.waitForBatching(false);
      }
    }
  }

  private void makeUnifPots() {
    // make unid pots
    for (int id : CLEAN_HERBS) {
      if (c.getInventoryItemCount(id) > 0 && (c.getInventoryItemCount(VIAL) > 0)) {
        c.setStatus("@cya@Making Unid pots");
        c.useItemOnItemBySlot(c.getInventoryItemSlotIndex(id), c.getInventoryItemSlotIndex(VIAL));
        c.sleep(2 * GAME_TICK);
        c.waitForBatching(false);
      }
    }
  }

  private void makePots() {
    if (c.getInventoryItemCount(ItemId.UNF_KWUARM_POTION.getId()) > 0
        && c.getInventoryItemCount(ItemId.LIMPWURT_ROOT.getId()) > 0) {
      c.setStatus("@cya@Making kwuarm pots");
      c.useItemOnItemBySlot(
          c.getInventoryItemSlotIndex(ItemId.LIMPWURT_ROOT.getId()),
          c.getInventoryItemSlotIndex(ItemId.UNF_KWUARM_POTION.getId()));
      c.sleep(640);
      c.waitForBatching(false);
    }
    if (c.getInventoryItemCount(SNAPE_GRASS) > 0) {
      if (c.getInventoryItemCount(ItemId.UNF_RANARR_POTION.getId()) > 0) {
        c.setStatus("@cya@Making ranarr pots");
        c.useItemOnItemBySlot(
            c.getInventoryItemSlotIndex(SNAPE_GRASS),
            c.getInventoryItemSlotIndex(ItemId.UNF_RANARR_POTION.getId()));
        c.sleep(640);
        c.waitForBatching(false);
      }
      if (c.getInventoryItemCount(ItemId.UNF_AVANTOE_POTION.getId()) > 0) {
        c.setStatus("@cya@Making avantoe pots");
        c.useItemOnItemBySlot(
            c.getInventoryItemSlotIndex(SNAPE_GRASS),
            c.getInventoryItemSlotIndex(ItemId.UNF_AVANTOE_POTION.getId()));
        c.sleep(640);
        c.waitForBatching(false);
      }
    }
    if (c.getInventoryItemCount(ItemId.EYE_OF_NEWT.getId()) > 0) {
      if (c.getInventoryItemCount(ItemId.UNF_GUAM_POTION.getId()) > 0) {
        c.setStatus("@cya@Making guam pots");
        c.sleep(640);
        c.useItemOnItemBySlot(
            c.getInventoryItemSlotIndex(ItemId.UNF_GUAM_POTION.getId()),
            c.getInventoryItemSlotIndex(ItemId.EYE_OF_NEWT.getId()));
        c.sleep(1280);
        c.waitForBatching(false);
      }
      if (c.getInventoryItemCount(ItemId.UNF_IRIT_POTION.getId()) > 0) {
        c.setStatus("@cya@Making irit pots");
        c.useItemOnItemBySlot(
            c.getInventoryItemSlotIndex(ItemId.EYE_OF_NEWT.getId()),
            c.getInventoryItemSlotIndex(ItemId.UNF_IRIT_POTION.getId()));
        c.sleep(640);
        c.waitForBatching(false);
      }
    }
  }

  private void dropFinPots() {
    int[] finishedPots = {
      ItemId.SUPER_STRENGTH_POTION_3DOSE.getId(),
      ItemId.RESTORE_PRAYER_POTION_3DOSE.getId(),
      ItemId.FISHING_POTION_3DOSE.getId(),
      ItemId.ATTACK_POTION_3DOSE.getId(),
      ItemId.SUPER_ATTACK_POTION_3DOSE.getId(),
    };
    for (int id : finishedPots) {
      if (c.getInventoryItemCount(id) > 0) {
        c.setStatus("@cya@Dropping finished pot");
        c.dropItem(c.getInventoryItemSlotIndex(id), c.getInventoryItemCount(id));
        c.sleep(2 * GAME_TICK);
      }
    }
  }

  private void dropUnneededHerbs() {
    for (int id : UNNEEDED_CLEAN_HERBS) {
      if (c.getInventoryItemCount(id) > 0) {
        c.setStatus("@cya@Dropping unneeded herbs");
        c.dropItem(c.getInventoryItemSlotIndex(id), c.getInventoryItemCount(id));
        c.sleep(2 * GAME_TICK);
      }
    }
  }

  private void doHerblawLoop() {
    cleanHerbs();
    makeUnifPots();
    makePots();
    dropFinPots();
    c.sleep(640);
    dropUnneededHerbs();
  }
  // Shop and combat actions
  private void _combatLoop(int npcId, int[] lootId) {
    lootItems(true, lootId);
    if (lootBones) lootItem(true, ItemId.BONES.getId());
    buryBones(true);
    checkFightMode(fightMode);
    checkInventoryItemCounts();
    doHerblawLoop();
    // todo drop extra snapes here?
    pickUpVials();
    lootItems(true, lootId);
    if (!c.isInCombat()) {
      ORSCharacter npc = c.getNearestNpcById(npcId, false);
      if (npc != null) {
        c.setStatus("@yel@Attacking " + NpcId.getById(npcId).name());
        c.attackNpc(npc.serverIndex);
        c.sleep(GAME_TICK);
        while (c.isInCombat()) c.sleep(GAME_TICK);
      } else c.sleep(GAME_TICK);
    } else c.sleep(640);
    if (c.getInventoryItemCount() == 30) {
      dropItemToLoot(false, 1, EMPTY_VIAL);
      dropItemToLoot(false, 1, VIAL);
      buryBonesToLoot(false);
    }
  }

  private void pickUpVials() {
    if ((c.getInventoryItemCount(VIAL) + c.getInventoryItemCount(EMPTY_VIAL))
        > (getHerbCountForVial() + 3)) {

      c.dropItem(
          c.getInventoryItemSlotIndex(EMPTY_VIAL),
          (c.getInventoryItemCount(VIAL) + c.getInventoryItemCount(EMPTY_VIAL))
              - (getHerbCountForVial() + 3));

      if ((c.getInventoryItemCount(VIAL) + c.getInventoryItemCount(EMPTY_VIAL))
          > (getHerbCountForVial() + 3)) {

        c.dropItem(
            c.getInventoryItemSlotIndex(VIAL),
            (c.getInventoryItemCount(VIAL) + c.getInventoryItemCount(EMPTY_VIAL))
                - (getHerbCountForVial() + 3));
      }
    } else if ((c.getInventoryItemCount(VIAL) + c.getInventoryItemCount(EMPTY_VIAL))
        < (getHerbCountForVial() + 3)) {
      lootItem(true, VIAL);
    }
  }

  private void doShop(int extraNewtsCount, boolean returnTrip) {
    c.setStatus("@yel@Buying Items..");
    if (!c.isInShop()) {
      ORSCharacter npc = c.getNearestNpcById(230, false);
      if (npc != null && c.currentY() < 3000) {
        c.walktoNPC(
            npc.serverIndex,
            0); // added, bot doesn't always get runes if npc moves >2 or 3 tiles away
        c.npcCommand1(npc.serverIndex);
        c.sleep(3000); // need LONG sleep or it breaks npccommand1
      } else {
        c.sleep(1000);
      }
    }
    int numberToBuy =
        (getHerbCountForVial())
            - (c.getInventoryItemCount(EMPTY_VIAL)
                + c.getInventoryItemCount(ItemId.VIAL.getId() + getUnifCount()));
    int newtCount =
        extraNewtsCount + getNewtSecCount() - c.getInventoryItemCount(ItemId.EYE_OF_NEWT.getId());
    while (c.isInShop()
        && c.getInventoryItemCount() < 30
        && ((c.getInventoryItemCount(EMPTY_VIAL) < numberToBuy && !returnTrip)
            || c.getInventoryItemCount(ItemId.EYE_OF_NEWT.getId()) < newtCount)) {
      if (!returnTrip && c.getInventoryItemCount(EMPTY_VIAL) < numberToBuy) {
        c.shopBuy(EMPTY_VIAL, numberToBuy);
        c.sleep(320);
      }
      if (c.getInventoryItemCount(ItemId.EYE_OF_NEWT.getId()) < newtCount) {
        c.shopBuy(ItemId.EYE_OF_NEWT.getId(), newtCount);
        c.sleep(320);
      }
      c.sleep(2 * GAME_TICK);
    }
    c.closeShop();
    c.sleep(640);
    c.setStatus("@yel@Done Buying..");
  }

  // navigation methods
  private void druidToShop() {
    c.walkTo(352, 3320);
    c.walkTo(362, 3320);
    c.walkTo(372, 3320);
    c.walkTo(376, 3327);
    c.walkTo(376, 3337);
    c.walkTo(376, 3347);
    c.walkTo(377, 3352);
    c.atObject(376, 3352);
    c.sleep(640);
    c.walkTo(377, 514);
    c.walkTo(371, 506);
    c.setStatus("@gre@Done Walking..");
  }

  private void shopToChaos() {
    c.walkTo(371, 506);
    c.walkTo(377, 514);
    c.walkTo(377, 520);
    c.atObject(376, 520);
    c.sleep(640);
    c.walkTo(376, 3347);
    c.walkTo(376, 3337);
    c.walkTo(376, 3327);
    c.walkTo(372, 3320);
    c.walkTo(362, 3320);
    c.walkTo(352, 3320);
  }

  private void hobsToChaos() {
    teleportCraftCape();
    c.walkTo(346, 595);
    c.walkTo(346, 589);
    c.walkTo(343, 581);
    tavGateSouthToNorth();
    c.walkTo(343, 570);
    c.walkTo(343, 560);
    c.walkTo(352, 549);
    c.walkTo(360, 539);
    c.walkTo(370, 534);
    c.walkTo(377, 525);
    c.walkTo(377, 520);
    // check for needing more newts here
    doHerblawLoop();
    if (startUp || (getNewtSecCount() == 0)) {
      c.atObject(376, 520);
      c.sleep(1280);
      c.walkTo(376, 3347);
      c.walkTo(376, 3337);
      c.walkTo(376, 3327);
      c.walkTo(372, 3320);
      c.walkTo(362, 3320);
      c.walkTo(352, 3320);
    }
  }

  private void shopToHobs() {
    c.setStatus("@gre@going to craft guild..Casting craft cape teleport");
    teleportCraftCape();
    // dont fill vials if you do not need to
    if (c.getInventoryItemCount(EMPTY_VIAL) > 0) {
      c.walkTo(347, 600);
      craftGuildDoorEntering(capeSwapId);
      c.sleep(640);
      c.walkTo(349, 611);
      c.setStatus("@gre@Going up Ladder");
      c.atObject(349, 612);
      c.sleep(2000);
      c.walkTo(344, 1547);
      c.useItemIdOnObject(343, 1547, EMPTY_VIAL);
      c.sleep(5000);
      c.waitForBatching(false);
      c.walkTo(347, 1547);
      c.sleep(640);
      c.itemCommand(ItemId.CRAFTING_CAPE.getId());
      c.sleep(2000);
    }
    if ((getLimpSecCount() + getSnapeSecCount()) > 0) {
      c.walkTo(340, 598);
      c.walkTo(335, 601);
      c.walkTo(335, 611);
      c.walkTo(335, 616);
      c.walkTo(344, 619);
      c.walkTo(355, 618);
      c.walkTo(362, 609);
    }
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("NOBANK Tav Chaos Druids - By Kaila");
    JLabel label1a = new JLabel("Start near tav chaos, tav shop, or hobs!");
    JLabel label1b = new JLabel("Req: crafting cape, gp");
    JLabel label2 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label3 = new JLabel("::bank ::bones ::lowlevel :potup");
    JLabel label4 = new JLabel("Combat Styles ::attack :strength ::defense ::controlled");
    JLabel label5 = new JLabel("Param Format: \"auto\"");
    JLabel blankLabel = new JLabel("     ");
    // JCheckBox craftCapeCheckbox = new JCheckBox("99 Crafting Cape Teleport?", true);
    JLabel capeItemIdLabel = new JLabel("Cape ItemId to switch back too (req)");
    JTextField capeItemIdField = new JTextField(String.valueOf(ItemId.ATTACK_CAPE.getId()));
    JCheckBox lootBonesCheckbox = new JCheckBox("Bury Bones? only while Npc's Null", true);
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    //    JLabel foodLabel = new JLabel("Type of Food:");
    //    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    //    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    //    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    //    foodField.setSelectedIndex(5); // sets default to lobs
    JButton startScriptButton = new JButton("Start");

    //    craftCapeCheckbox.addActionListener(
    //        e -> {
    //          capeItemIdLabel.setEnabled(craftCapeCheckbox.isSelected());
    //          capeItemIdField.setEnabled(craftCapeCheckbox.isSelected());
    //        });

    startScriptButton.addActionListener(
        e -> {
          //          if (!foodWithdrawAmountField.getText().isEmpty())
          //            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          // craftCapeTeleport = craftCapeCheckbox.isSelected();
          if (!capeItemIdField.getText().isEmpty()) {
            capeSwapId = Integer.parseInt(capeItemIdField.getText());
          }
          lootBones = lootBonesCheckbox.isSelected();
          //          foodId = foodIds[foodField.getSelectedIndex()];
          //          foodName = foodTypes[foodField.getSelectedIndex()];
          fightMode = fightModeField.getSelectedIndex();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1a);
    scriptFrame.add(label1b);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(blankLabel);
    // scriptFrame.add(craftCapeCheckbox);
    scriptFrame.add(capeItemIdLabel);
    scriptFrame.add(capeItemIdField);
    scriptFrame.add(lootBonesCheckbox);
    scriptFrame.add(fightModeLabel);
    scriptFrame.add(fightModeField);
    //    scriptFrame.add(foodLabel);
    //    scriptFrame.add(foodField);
    //    scriptFrame.add(foodWithdrawAmountLabel);
    //    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void chatCommandInterrupt(
      String commandText) { // ::bank ::bones ::lowlevel :potup ::prayer
    if (commandText.replace(" ", "").toLowerCase().contains("bank")) {
      c.displayMessage("@or1@Got @red@bank@or1@ command! Going to the Bank!");
      timeToBank = true;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("bones")) {
      if (!lootBones) {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning on bone looting!");
        lootBones = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning off bone looting!");
        lootBones = false;
      }
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("lowlevel")) {
      if (!lootLowLevel) {
        c.displayMessage("@or1@Got toggle @red@lowlevel@or1@, turning on low level herb looting!");
        lootLowLevel = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@lowlevel@or1@, turning off low level herb looting!");
        lootLowLevel = false;
      }
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("potup")) {
      if (!potUp) {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning on regular atk/str pots!");
        potUp = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning off regular atk/str pots!");
        potUp = false;
      }
      c.sleep(100);
    } else if (commandText
        .replace(" ", "")
        .toLowerCase()
        .contains("attack")) { // field is "Controlled", "Aggressive", "Accurate", "Defensive"}
      c.displayMessage("@red@Got Combat Style Command! - Attack Xp");
      c.displayMessage("@red@Switching to \"Accurate\" combat style!");
      fightMode = 2;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("strength")) {
      c.displayMessage("@red@Got Combat Style Command! - Strength Xp");
      c.displayMessage("@red@Switching to \"Aggressive\" combat style!");
      fightMode = 1;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("defense")) {
      c.displayMessage("@red@Got Combat Style Command! - Defense Xp");
      c.displayMessage("@red@Switching to \"Defensive\" combat style!");
      fightMode = 3;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("controlled")) {
      c.displayMessage("@red@Got Combat Style Command! - Controlled Xp");
      c.displayMessage("@red@Switching to \"Controlled\" combat style!");
      fightMode = 0;
      c.sleep(100);
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You eat the")) {
      usedFood++;
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {

      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int guamSuccessPerHr = 0;
      int marSuccessPerHr = 0;
      int tarSuccessPerHr = 0;
      int harSuccessPerHr = 0;
      int ranSuccessPerHr = 0;
      int iritSuccessPerHr = 0;
      int avaSuccessPerHr = 0;
      int kwuSuccessPerHr = 0;
      int cadaSuccessPerHr = 0;
      int dwarSuccessPerHr = 0;
      int lawSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      int herbSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        guamSuccessPerHr = (int) ((totalGuam + inventGuam) * scale);
        marSuccessPerHr = (int) ((totalMar + inventMar) * scale);
        tarSuccessPerHr = (int) ((totalTar + inventTar) * scale);
        harSuccessPerHr = (int) ((totalHar + inventHar) * scale);
        ranSuccessPerHr = (int) ((totalRan + inventRan) * scale);
        iritSuccessPerHr = (int) ((totalIrit + inventIrit) * scale);
        avaSuccessPerHr = (int) ((totalAva + inventAva) * scale);
        kwuSuccessPerHr = (int) ((totalKwuarm + inventKwuarm) * scale);
        cadaSuccessPerHr = (int) ((totalCada + inventCada) * scale);
        dwarSuccessPerHr = (int) ((totalDwarf + inventDwarf) * scale);
        lawSuccessPerHr = (int) ((totalLaw + inventLaws) * scale);
        GemsSuccessPerHr = (int) ((totalGems + inventGems) * scale);
        herbSuccessPerHr = (int) ((totalHerbs + inventHerbs) * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);

      } catch (Exception e) {
        // divide by zero
      }

      int x = 6;
      int y = 15;
      c.drawString("@red@NOBANK Tav Chaos Druids @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      if (lootLowLevel) {
        c.drawString(
            "@whi@Guam: @gre@"
                + (totalGuam + inventGuam)
                + "@yel@ (@whi@"
                + String.format("%,d", guamSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Mar: @gre@"
                + (totalMar + inventMar)
                + "@yel@ (@whi@"
                + String.format("%,d", marSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Tar: @gre@"
                + (totalTar + inventTar)
                + "@yel@ (@whi@"
                + String.format("%,d", tarSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + 14,
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Har: @gre@"
                + (totalHar + inventHar)
                + "@yel@ (@whi@"
                + String.format("%,d", harSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Rana: @gre@"
                + (totalRan + inventRan)
                + "@yel@ (@whi@"
                + String.format("%,d", ranSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Irit: @gre@"
                + (totalIrit + inventIrit)
                + "@yel@ (@whi@"
                + String.format("%,d", iritSuccessPerHr)
                + "@yel@/@whi@hr@yel@)",
            x,
            y + (14 * 2),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Ava: @gre@"
                + (totalAva + inventAva)
                + "@yel@ (@whi@"
                + String.format("%,d", avaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Kwu: @gre@"
                + (totalKwuarm + inventKwuarm)
                + "@yel@ (@whi@"
                + String.format("%,d", kwuSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Cada: @gre@"
                + (totalCada + inventCada)
                + "@yel@ (@whi@"
                + String.format("%,d", cadaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 3),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Dwar: @gre@"
                + (totalDwarf + inventDwarf)
                + "@yel@ (@whi@"
                + String.format("%,d", dwarSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Laws: @gre@"
                + (totalLaw + inventLaws)
                + "@yel@ (@whi@"
                + String.format("%,d", lawSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 4),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Total Herbs: @gre@"
                + (totalHerbs + inventHerbs)
                + "@yel@ (@whi@"
                + String.format("%,d", herbSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 5),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Total Trips: @gre@"
                + totalTrips
                + "@yel@ (@whi@"
                + String.format("%,d", TripSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Runtime: "
                + runTime,
            x,
            y + (14 * 6),
            0xFFFFFF,
            1);
        c.drawString("@whi@____________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
      } else {
        c.drawString(
            "@whi@Rana: @gre@"
                + (totalRan + inventRan)
                + "@yel@ (@whi@"
                + String.format("%,d", ranSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Irit: @gre@"
                + (totalIrit + inventIrit)
                + "@yel@ (@whi@"
                + String.format("%,d", iritSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Avan: @gre@"
                + (totalAva + inventAva)
                + "@yel@ (@whi@"
                + String.format("%,d", avaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + 14,
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Kwua: @gre@"
                + (totalKwuarm + inventKwuarm)
                + "@yel@ (@whi@"
                + String.format("%,d", kwuSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Cada: @gre@"
                + (totalCada + inventCada)
                + "@yel@ (@whi@"
                + String.format("%,d", cadaSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Dwar: @gre@"
                + (totalDwarf + inventDwarf)
                + "@yel@ (@whi@"
                + String.format("%,d", dwarSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 2),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Total Gems: @gre@"
                + (totalGems + inventGems) // remove for regular druids!!!
                + "@yel@ (@whi@"
                + String.format("%,d", GemsSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Total Herbs: @gre@"
                + (totalHerbs + inventHerbs)
                + "@yel@ (@whi@"
                + String.format("%,d", herbSuccessPerHr)
                + "@yel@/@whi@hr@yel@) ",
            x,
            y + (14 * 3),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Tooth: @gre@"
                + (totalTooth + inventTooth) // remove for regular druids!!!
                + "@yel@ / @whi@Loop: @gre@"
                + (totalLoop + inventLoop)
                + "@yel@ / @whi@R.Spear: @gre@"
                + (totalSpear + inventSpear)
                + "@yel@ / @whi@Half: @gre@"
                + (totalLeft + inventLeft),
            x,
            y + (14 * 4),
            0xFFFFFF,
            1);
        c.drawString(
            "@whi@Total Trips: @gre@"
                + totalTrips
                + "@yel@ (@whi@"
                + String.format("%,d", TripSuccessPerHr)
                + "@yel@/@whi@hr@yel@) "
                + "@whi@Runtime: "
                + runTime,
            x,
            y + (14 * 5),
            0xFFFFFF,
            1);
        c.drawString("@whi@____________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
      }
    }
  }
}
