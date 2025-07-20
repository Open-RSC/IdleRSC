package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import models.entities.ItemId;
import models.entities.SceneryId;
import orsc.ORSCharacter;

/**
 * @author Dahun for use with nature runes inspiration and code taken from damwildyagility by
 *     Dam(?), herbharvester by Dvorak and Seatta and AirRunes by Potato
 */
public class NatureClipper extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.RUNECRAFTING, Category.HARVESTING, Category.IRONMAN_SUPPORTED},
          "Dahun",
          "Crafts Nature runes with cursed talismans, clips herbs and buys vials/bronze using the Shilo bank.\n\n"
              + "Start in the bank with combat gear equipped and agility cape on.\n\n"
              + "Requires 95+ combat, coins, 99 agility with cape on and the Shilo Village quest. "
              + "Uses tuna to heal if needed. Will use the harvesting cape if available.");

  int runeId = 40; // rune to craft
  int talismanId = 1308; // Talisman currently in use
  int chiselId = 167; // The chisel
  int[] talismanIds = {1308, 1394, 1385}; // talismans we can use, normal, cursed, blank
  int runestoneId = 1299; // blank runestone id
  int reqMagicLevel = 19; // Level for casting curse
  int[] reqCurseRunes = {32, 34, 36, 40}; // runes needed for curse
  int[] reqCurseCount = {2, 3, 1, 10}; // rune count for curse
  int[] reqRuneLevel = {44, 44, 51}; // Levels for crafting, making talisman, using cursed
  int[] ruins = {392, 804}; // ruins entrance
  int[] alter = {787, 21}; // crafting alter x
  int[] portal = {783, 26}; // exit portal x
  int[] bankDoor = {398, 851}; // bank door location
  int[] gate = {394, 851};
  int[] stone = {368, 830};
  int bankDoorValue = 2; // bank door closed object id
  int food = 367; // food to eat
  int Clippers = 1357; // herb clipper id
  int AgilityCape = 1518;
  int HarvestingCape = 1526;
  int herbsPicked = 0;
  private int vialsBought = 0;
  private int bronzeBought = 0;
  private final int VIAL_ID = ItemId.EMPTY_VIAL.getId();
  private final int BRONZE_BAR_ID = ItemId.BRONZE_BAR.getId();
  public String status;
  public final boolean debug = false;

  final int[] cartNW = {384, 850};
  final int[] cartSE = {381, 853};
  final int[] gateNW = {387, 850};
  final int[] gateSE = {385, 853};
  final int[] villageNW = {397, 848};
  final int[] villageSE = {394, 854};
  int objectId;

  public boolean inArea(int[] nwTile, int[] seTile) {
    return controller.currentX() <= nwTile[0]
        && controller.currentX() >= seTile[0]
        && controller.currentY() >= nwTile[1]
        && controller.currentY() <= seTile[1];
  }

  public void useObject(int i) {
    int[] objID = controller.getNearestObjectById(i);
    try {
      if (objID.length > 0) {
        status = "Interacting with object id: " + i;
        if (debug) {
          controller.displayMessage("@cya@" + "Interacting with object id:" + i);
        }
        controller.atObject(objID[0], objID[1]);
      }
    } catch (NullPointerException ignored) {

    }
  }

  final int[] herbs = {
    ItemId.UNID_GUAM_LEAF.getId(),
    ItemId.UNID_MARRENTILL.getId(),
    ItemId.UNID_TARROMIN.getId(),
    ItemId.UNID_HARRALANDER.getId(),
    ItemId.UNID_RANARR_WEED.getId(),
    ItemId.UNID_IRIT.getId(),
    ItemId.UNID_AVANTOE.getId(),
    ItemId.UNID_KWUARM.getId(),
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
    ItemId.COINS.getId(),
    465,
    169,
    ItemId.BRONZE_BAR.getId(),
    1308, // might need to remove this if not cursing ??
    ItemId.VIAL.getId()
  };

  long startTimestamp = (System.currentTimeMillis() / 1000L); // timestamp for perhr calcs
  int runesCrafted = 0; // total crafted
  int runesInBank = 0; // total banked
  int runestoneInBank = 0; // runestones left in bank

  boolean curse = false; // Curse talismans
  boolean outOfRunes = false; // do we have runes?

  public int start(String parameters[]) {
    controller.setStatus("@red@Nature Crafter by Dahun..."); // status update
    while (!controller.isInBank()) { // open the bank
      controller.openBank(); // bank
    }
    depositAll(); // clear out inventory
    if ((controller.getBaseStat(controller.getStatId("Magic")) >= reqMagicLevel)
        && (controller.getBaseStat(controller.getStatId("Runecraft"))
            >= reqRuneLevel[2])) { // if we can, curse the talisman
      curse = true; // we are cursing
      talismanId = talismanIds[1]; // update the item id to use
      curseBank(); // do the thing
    }
    bank(); // grab some blank stones
    scriptStart(); // ready to run
    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (controller.isRunning()) {
      walkToRuins(); // Walk to the alter
      clipHerbs(); // clip herbs
      walkToBank(); // Walk to the bank
      //     talkToMosol(); // talk to mosol
      hopWall();
      bankForCoins(); // coins to buy vials
      buyVials(); // buy vials and bronze
      if (curse) { // If we are cursing
        curseBank(); // do it first
      }
      bank(); // Bank stuff
    }
  }

  public void openBankDoor() {
    while (controller.getObjectAtCoord(bankDoor[0], bankDoor[1])
        == bankDoorValue) { // if the bank door is closed
      controller.setStatus("@red@Opening bank door..."); // Status update
      controller.atObject(bankDoor[0], bankDoor[1]); // Open it
      controller.sleep(640); // Crashes if someone else opens the door while enroute
    }
  }

  public void avoidCombat() {
    controller.walkTo(controller.currentX(), controller.currentY(), 0, true, true);
    controller.sleep(400);
  }

  public void walkToBank() {
    if (controller.getBaseStat(controller.getStatId("Runecraft")) >= reqRuneLevel[2]
        && !outOfRunes) { // Can we curse talismans now?
      curse = true; // we are cursing
      talismanId = talismanIds[1]; // update the item id to use
    }
    if (controller.getInventoryItemCount(AgilityCape) >= 1) {
      controller.equipItemById(AgilityCape); // equip Agility cape
      controller.sleep(600);
    }
    controller.setStatus("@red@Walking to bank..."); // status update
    controller.walkTo(391, 806);
    if (controller.getInventoryItemCount(AgilityCape) >= 1) {
      controller.equipItemById(AgilityCape); // equip Agility cape
      controller.sleep(600);
    }
    controller.walkTo(388, 807); // towards the bank
    controller.walkTo(383, 812);
    controller.walkTo(378, 817);
    controller.walkTo(373, 823);
    controller.walkTo(369, 829);
    if (controller.isInCombat()) {
      avoidCombat();
    }
    controller.sleep(200);
    controller.atObject(stone[0], stone[1]);
    controller.sleep(1000);
    controller.walkTo(367, 831);
    controller.walkTo(370, 836);
    controller.walkTo(372, 842);
    controller.walkTo(377, 848);
    controller.walkTo(381, 850);
    controller.walkTo(383, 851);
    controller.setStatus("@end of walk to bank");
    controller.sleep(100);
  }

  public void hopWall() {
    boolean leavecombat = false;
    while (controller.isRunning()) {
      controller.setStatus("@red@Hopping wall"); // status update
      if (inArea(cartNW, cartSE)) {
        objectId = 613;
        leavecombat =
            false; // we dont wanna leavecombat if at the cart step can get over while in combat
      }
      if (inArea(gateNW, gateSE)) {
        objectId = 611;
        leavecombat = true;
      }
      if (inArea(villageNW, villageSE)) {
        break;
      }
      if (controller.isInOptionMenu()) { // cart option
        controller.setStatus("@red@Option yes");
        if (controller.getOptionsMenuText(0).contains("Yes")) controller.optionAnswer(0);
        controller.sleep(6000);
      }
      if (controller.isInCombat() && leavecombat == true) {
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true, true);
        controller.sleep(800);
      }
      if (controller.isInCombat() && leavecombat == true) {
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true, true);
        controller.sleep(800);
      }
      if (controller.isInCombat() && leavecombat == true) { // this is necassary i think.....
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true, true);
        controller.sleep(800);
      }
      if (controller.isInCombat() && leavecombat == true) { // this is necassary i think.....
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true, true);
        controller.sleep(800);
      }
      if (controller.isInCombat() && leavecombat == true) { // this is necassary i think.....
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true, true);
        controller.sleep(800);
      }
      if (controller.isInCombat() && leavecombat == true) { // this is necassary i think.....
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true, true);
        controller.sleep(800);
      }
      if (controller.isInCombat() && leavecombat == true) { // this is necassary i think.....
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true, true);
        controller.sleep(800);
      }
      if (controller.isInCombat() && leavecombat == true) { // could of made a while loop i guess
        controller.setStatus("@red@Leaving combat..");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true, true);
        controller.sleep(800);
      }
      if (!controller.isInCombat()) {
        if (controller.isInOptionMenu()) { // cart option
          controller.setStatus("@red@Option yes");
          if (controller.getOptionsMenuText(0).contains("Yes")) controller.optionAnswer(0);
          controller.sleep(6000);
        }
        controller.setStatus("@red@Using object");
        if (controller.isInOptionMenu()) { // cart option
          controller.setStatus("@red@Option yes");
          if (controller.getOptionsMenuText(0).contains("Yes")) controller.optionAnswer(0);
          controller.sleep(6000);
        }
        useObject(objectId); // click on either the cart or the gate
        if (controller.isInOptionMenu()) {
          controller.setStatus("@red@Option yes");
          if (controller.getOptionsMenuText(0).contains("Yes")) controller.optionAnswer(0);
          controller.sleep(6000);
        }
        controller.sleep(3000);
      }
      if (controller.isInOptionMenu()) { // cart option
        controller.setStatus("@red@Option yes");
        if (controller.getOptionsMenuText(0).contains("Yes")) controller.optionAnswer(0);
        controller.sleep(6000);
      }
    }
  }

  public void talkToMosol() { // fuck this guy

    controller.setStatus("@talk to mosol step1");
    ORSCharacter npc;
    while (controller.currentX() < 393) {
      while (!controller.isInOptionMenu()) {
        controller.setStatus("@talk to mosol");
        npc = controller.getNearestNpcById(539, false);
        if (npc != null && controller.currentX() < 390) {
          controller.talkToNpc(npc.serverIndex);
          controller.sleep(5000);
        }

        if (controller.isInOptionMenu()) {

          if (controller.getOptionsMenuText(0).contains("Yes")) controller.optionAnswer(0);
          controller.sleep(1000);
        }
        if (controller.currentX() == 395 && controller.currentY() == 851) break;
      }
      if (controller.currentX() == 395 && controller.currentY() == 851) break;
    }
  }

  public void walkToRuins() {
    controller.setStatus("@red@Walking to ruins..."); // status update
    controller.walkTo(399, 851); // walk to bank door
    openBankDoor(); // check the bank door
    controller.walkTo(394, 851); // towards ruins
    controller.atObject(gate[0], gate[1]);
    controller.sleep(1000);
    controller.walkTo(377, 848); // towards ruins
    controller.walkTo(372, 842); // walk to the ruins
    controller.walkTo(370, 836); // walk to the ruins
    controller.walkTo(367, 831); // walk to the ruins
    controller.atObject(stone[0], stone[1]);
    controller.sleep(1000);
    controller.walkTo(369, 829);
    controller.walkTo(373, 823); // walk to the ruins
    controller.walkTo(378, 817); // walk to the ruins
    controller.walkTo(383, 812); // walk to the ruins
    controller.walkTo(388, 807); // walk to the ruins
    controller.walkTo(391, 806); // walk to the ruins
    controller.atObject(ruins[0], ruins[1]); // enter the ruins
    controller.walkTo(787, 26);
    controller.walkTo(787, 23); // walk to the alter
    controller.setStatus("@red@Crafting runes..."); // status update
    controller.atObject(alter[0], alter[1]); // Craft runes
    controller.sleep(2000); // delay
    controller.walkTo(785, 26); // walk to the portal
    controller.atObject(portal[0], portal[1]); // go through portal
    controller.sleep(1000);
  }

  public void clipHerbs() {
    int[] coords = controller.getNearestObjectById(SceneryId.HERB.getId());
    if (controller.getInventoryItemCount(HarvestingCape) >= 1) {
      controller.equipItemById(HarvestingCape); // equip harvesting cape
      controller.sleep(600);
    }

    while (controller.getInventoryItemCount() < 30
        && coords != null) { // check if inventory full and if there are herbs to clip
      coords = controller.getNearestObjectById(SceneryId.HERB.getId());
      if (coords != null) {
        controller.setStatus("@whi@Picking herbs!");
        controller.atObject(coords[0], coords[1]);
        controller.sleep(1000);
        while (controller.getInventoryItemCount() < 30 && controller.isBatching())
          controller.sleep(10);
      }
    }
    if (controller.getInventoryItemCount(AgilityCape) >= 1) {
      controller.equipItemById(AgilityCape); // equip Agility cape
      controller.sleep(600);
    }
  }

  public void curseBank() {

    controller.setStatus("@red@CurseBanking..."); // status update
    while (!controller.isInBank()) { // open the bank
      controller.openBank(); // bank
    }
    while (countHerbs() > 0) {
      for (int unid : herbs) {
        if (controller.getInventoryItemCount(unid) > 0) {
          controller.depositItem(unid, controller.getInventoryItemCount(unid));
          controller.sleep(250);
        }
      }
    }
    if (controller.getBankItemCount(talismanId) > 0) { // If we already have a talisman
      controller.withdrawItem(talismanId); // get it
      controller.sleep(640);
    } else { // else we make it
      for (int i = 0; i < reqCurseRunes.length; i++) { // Loop for curse runes
        if (controller.getBankItemCount(reqCurseRunes[i]) >= reqCurseCount[i]) { // Check for runes
          controller.withdrawItem(reqCurseRunes[i], reqCurseCount[i]); // withdraw if available
          controller.sleep(640);
        } else { // Otherwise...
          curse = false; // We can't curse
          outOfRunes = true; // Don't check again.
          talismanId = talismanIds[0]; // Change to standard talisman
          depositAll(); // dump inventory
          return; // we done here...
        }
      }
      if (curse) { // If still cursing
        controller.withdrawItem(runestoneId, 1); // Blank runestone
        controller.sleep(640);
        controller.withdrawItem(chiselId, 1); // Chisel
        controller.sleep(1000);
        controller.useItemOnItemBySlot(
            controller.getInventoryItemSlotIndex(chiselId),
            controller.getInventoryItemSlotIndex(runestoneId)); // cut a blank
        controller.sleep(1000);
        controller.useItemOnItemBySlot(
            controller.getInventoryItemSlotIndex(reqCurseRunes[3]),
            controller.getInventoryItemSlotIndex(talismanIds[2])); // make it
        controller.sleep(1000);
        controller.castSpellOnInventoryItem(
            9, controller.getInventoryItemSlotIndex(talismanIds[0])); // curse it
        controller.sleep(640);
      }
    }
  }

  public void depositAll() { // Seems to work???
    while (controller.getInventoryItemCount() > 0) {
      controller.depositItem(
          controller.getInventorySlotItemId(0),
          controller.getInventoryItemCount(controller.getInventorySlotItemId(0)));
      controller.sleep(640);
    }
  }

  public int countHerbs() {
    int count = 0;
    for (int unid : herbs) {
      count += controller.getInventoryItemCount(unid);
    }

    return count;
  }

  public void buyVials() {
    controller.setStatus("@gre@Buying stuff..");
    if (controller.getInventoryItemCount() < 30) {
      controller.walkTo(408, 851);
      controller.walkTo(413, 849);
      controller.walkTo(417, 847);
      controller.sleep(300);
      ORSCharacter npc = controller.getNearestNpcById(620, false);
      while (npc != null) {

        if (!controller.isInShop()) {
          controller.npcCommand1(npc.serverIndex);
          controller.sleep(2000);
        }
        while (controller.isInShop() && controller.getShopItemCount(VIAL_ID) > 0) {

          controller.shopBuy(VIAL_ID, controller.getShopItemCount(VIAL_ID)); // buy vials
          controller.shopBuy(
              BRONZE_BAR_ID,
              controller.getShopItemCount(
                  BRONZE_BAR_ID)); // buy bronze probably not worth for main accounts
          controller.sleep(100);
        }
        if (controller.getInventoryItemCount(VIAL_ID) > 0) break;
      }
      controller.walkTo(413, 851);
      controller.sleep(640);
    }
    vialsBought += controller.getInventoryItemCount(VIAL_ID);
    bronzeBought += controller.getInventoryItemCount(BRONZE_BAR_ID);
  }

  public void bankForCoins() {
    //    if (controller.isInOptionMenu()) {

    //      if (controller.getOptionsMenuText(0).contains("Yes")) controller.optionAnswer(0);
    //      controller.sleep(1000);
    //    }
    //    if (controller.currentX() != 395 && controller.currentY() != 851) {
    //      talkToMosol(); // failsafe incase we got here without finishing mosol dialog
    //    }

    controller.setStatus("@red@Banking..."); // status update
    while (!controller.isInBank()) { // open the bank
      controller.openBank(); // bank
    }
    if (controller.getInventoryItemCount(chiselId) > 0) { // if we have a chisel still,
      controller.depositItem(chiselId); // dump it
      controller.sleep(640); // wait a sec
    }
    if (controller.getInventoryItemCount(runeId) > 0) { // if we have runes,
      this.runesCrafted += (controller.getInventoryItemCount(runeId)); // update the crafted count
      controller.depositItem(
          runeId, controller.getInventoryItemCount(runeId)); // deposit crafted runes
      controller.sleep(640); // wait a sec
      this.runesInBank = controller.getBankItemCount(runeId); // update runes count
    }
    while (countHerbs() > 0) {
      for (int unid : herbs) {
        if (controller.getInventoryItemCount(unid) > 0) {
          controller.depositItem(unid, controller.getInventoryItemCount(unid));
          controller.sleep(250);
        }
      }
    }
    if (controller.getInventoryItemCount(talismanId) < 1) { // If no talisman
      controller.withdrawItem(talismanId); // get it
      controller.sleep(640); // wait a sec
    }
    if (controller.getInventoryItemCount(Clippers) < 1) { // If no clippers
      controller.withdrawItem(Clippers); // get it
      controller.sleep(640); // wait a sec
    }
    if (controller.getCurrentStat(controller.getStatId("Hits")) <= 50) {
      controller.setStatus("@red@Eating food");
      controller.withdrawItem(food, 29);
      controller.closeBank();
      controller.itemCommand(food);
      controller.sleep(1000);
    }
    while (!controller.isInBank()) { // open the bank
      controller.openBank(); // bank
    }
    if (controller.getInventoryItemCount(10) < 500) {
      controller.withdrawItem(10, 500);
      controller.sleep(640);
      this.runestoneInBank = controller.getBankItemCount(runestoneId);
    }
  }

  public void bank() {

    controller.setStatus("@red@Banking..."); // status update
    if (controller.isInOptionMenu()) {

      if (controller.getOptionsMenuText(0).contains("Yes")) controller.optionAnswer(0);
      controller.sleep(1000);
    }

    while (!controller.isInBank()) { // open the bank
      controller.openBank(); // bank
    }
    if (controller.getInventoryItemCount(chiselId) > 0) { // if we have a chisel still,
      controller.depositItem(chiselId); // dump it
      controller.sleep(640); // wait a sec
    }
    if (controller.getInventoryItemCount(HarvestingCape) < 1
        && controller.getCurrentStat(controller.getStatId("Harvesting"))
            >= 99) { // if have 99 harvesting and no cape withdraw it
      controller.withdrawItem(HarvestingCape); // withdraw it
      controller.sleep(640); // wait a sec
    }
    if (controller.getInventoryItemCount(runeId) > 0) { // if we have runes,
      this.runesCrafted += (controller.getInventoryItemCount(runeId)); // update the crafted count
      controller.depositItem(
          runeId, controller.getInventoryItemCount(runeId)); // deposit crafted runes
      controller.sleep(640); // wait a sec
      this.runesInBank = controller.getBankItemCount(runeId); // update runes count
    }
    while (countHerbs() > 0) {
      for (int unid : herbs) {
        if (controller.getInventoryItemCount(unid) > 0) {
          controller.depositItem(unid, controller.getInventoryItemCount(unid));
          controller.sleep(250);
        }
      }
    }
    if (controller.getInventoryItemCount(talismanId) < 1) { // If no talisman
      controller.withdrawItem(1308); // get it
      controller.sleep(640); // wait a sec
    }
    if (controller.getInventoryItemCount(Clippers) < 1) { // If no clippers
      controller.withdrawItem(Clippers); // get it
      controller.sleep(640); // wait a sec
    }
    if (controller.getInventoryItemCount(465) > 0) { // if vials
      controller.depositItem(465); // deposit it
      controller.sleep(640); // wait a sec
    }
    if (controller.getCurrentStat(controller.getStatId("Hits")) <= 50) {
      controller.setStatus("@red@Eating food");
      controller.withdrawItem(food, 1);
      controller.closeBank();
      controller.itemCommand(food);
      controller.sleep(1000);
    }
    while (!controller.isInBank()) { // open the bank
      controller.openBank(); // bank
    }
    if (controller.getInventoryItemCount(runestoneId) < 28) {
      controller.withdrawItem(runestoneId, 29);
      controller.sleep(640);
      this.runestoneInBank = controller.getBankItemCount(runestoneId); // update runestone count
    }
  }

  public static void centerWindow(Window frame) {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
    frame.setLocation(x, y);
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("herb")) herbsPicked++;
    if (message.contains("double")) herbsPicked++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int craftedPerHr = 0;
      int herbsPerHr = 0;
      try {
        float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        float scale = (60 * 60) / timeRan;
        craftedPerHr = (int) (runesCrafted * scale);
        herbsPerHr = (int) (herbsPicked * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 180, 21 + 14 + 14 + 14 + 14 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@NatureClipper @whi@by @red@Dahun", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Runes crafted: @whi@"
              + String.format("%,d", this.runesCrafted)
              + " @red@(@whi@"
              + String.format("%,d", craftedPerHr)
              + "@red@/@whi@hr@red@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@red@Runes in bank: @whi@" + String.format("%,d", this.runesInBank),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@red@Runestone in bank: @whi@" + String.format("%,d", this.runestoneInBank),
          10,
          21 + 14 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@red@Herbs Picked @whi@" + String.format("%,d", this.herbsPicked),
          10,
          21 + 14 + 14 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@red@Vials Bought @whi@" + String.format("%,d", this.vialsBought),
          10,
          21 + 14 + 14 + 14 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@red@Bronze Bought @whi@" + String.format("%,d", this.bronzeBought),
          10,
          21 + 14 + 14 + 14 + 14 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
}
