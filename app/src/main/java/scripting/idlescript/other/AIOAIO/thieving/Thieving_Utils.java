package scripting.idlescript.other.AIOAIO.thieving;

import bot.Main;
import models.entities.ItemId;
import models.entities.NpcId;
import orsc.ORSCharacter;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.AIOAIO_Script_Utils;
import scripting.idlescript.other.AIOAIO.combat.Combat_Utils;

public class Thieving_Utils {
  static boolean goingToCabbages = false;

  public static void leaveCombat() {
    AIOAIO.state.status = ("Getting out of combat");
    Main.getController()
        .walkToAsync(Main.getController().currentX(), Main.getController().currentY(), 0);
    Main.getController().sleep(600);
  }

  public static int theiveNpc(NpcId npcId) {
    AIOAIO.state.status = ("@yel@Thieving " + npcId.name());
    ORSCharacter npc = Main.getController().getNearestNpcById(npcId.getId(), false);
    if (npc == null) {
      AIOAIO.state.status = ("@yel@Can't find a " + npcId.name() + " :c");
      return 50;
    }
    Main.getController().thieveNpc(npc.serverIndex);
    return 680;
  }

  public static int getReadyForTheiving() {
    goingToCabbages = false;
    AIOAIO.state.status = ("@yel@Depositing all my stuff before thieving");
    if (!AIOAIO_Script_Utils.towardsDepositAll()) return 50; // Still working on reaching the bank
    AIOAIO.state.status = ("@yel@Depositing complete, withdrawing up to 20 noms!");
    int inventoryItemCount =
        Main.getController()
            .getInventoryItemCount(); // Keep track of how many items we have because we're gonna
    // withdraw food super fast (faster than the
    // controller.getInventoryItemCount will be able to update
    // on!)
    for (ItemId foodId : Combat_Utils.food) {
      int withdrawAmount =
          Math.min(Main.getController().getBankItemCount(foodId.getId()), 20 - inventoryItemCount);
      Main.getController().withdrawItem(foodId.getId(), withdrawAmount);
      inventoryItemCount += withdrawAmount;
    }
    AIOAIO.state.taskStartup = false;
    AIOAIO.state.status = ("@yel@Finished withdrawing my noms, done Theiving startup!");
    return 50;
  }

  public static void goToCabbages() {
    AIOAIO.state.status = ("@yel@Going to cabbage field");
    Main.getController().log("Going to cabbage");
    goingToCabbages = true;
    Main.getController().walkTowards(142, 608);
  }

  public static boolean inCabbageField() {
    return Main.getController().currentX() >= 137
        && Main.getController().currentY() <= 614
        && Main.getController().currentX() <= 154
        && Main.getController().currentY() >= 597;
  }

  public static void pickCabbage() {
    AIOAIO.state.status = ("Picking cabbage");
    goingToCabbages = false;
    Main.getController().pickupItem(ItemId.CABBAGE.getId());
  }
}
