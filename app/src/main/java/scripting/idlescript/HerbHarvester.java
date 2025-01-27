package scripting.idlescript;

import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;
import models.entities.ItemId;
import models.entities.Location;
import models.entities.SceneryId;

public class HerbHarvester extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.HARVESTING, Category.IRONMAN_SUPPORTED},
          "Dvorak, Auto-Pathing by Seatta",
          "Picks herbs in Taverley via harvesting. Coleslaw only.");

  int herbsPicked = 0;
  int herbsBanked = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;

  final int clippers = ItemId.HERB_CLIPPERS.getId();
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
    ItemId.UNID_DWARF_WEED.getId()
  };

  public int start(String[] param) {

    if (controller.getUnnotedInventoryItemCount(clippers) < 1) {
      controller.displayMessage("@red@You need to have herb clippers in your inventory!");
      controller.displayMessage("@red@Quitting script!");
      controller.stop();
    }
    if (controller.isRunning()) {
      controller.displayMessage("@red@HerbHarvester by Dvorak. Let's party like it's 2004!");
      controller.quitIfAuthentic();
    }

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (controller.getInventoryItemCount() < 30) {
        if (!Location.isAtLocation(Location.TAVERLEY)) Location.TAVERLEY.walkTowards();
        int[] coords = controller.getNearestObjectById(SceneryId.HERB.getId());

        if (coords != null) {
          controller.setStatus("@whi@Picking herbs!");
          controller.atObject(coords[0], coords[1]);
          controller.sleep(1000);
          while (controller.getInventoryItemCount() < 30 && controller.isBatching())
            controller.sleep(10);
        } else {
          // move so we can see all herbs
          controller.setStatus("@whi@Searching for herbs...");
          Location.walkTowards(363, 503);
        }

      } else {
        Location.FALADOR_WEST_BANK.walkTowards();
        bank();
        Location.TAVERLEY.walkTowards();
      }

      controller.sleep(100);
    }

    return 1000; // start() must return a int value now.
  }

  public int countHerbs() {
    int count = 0;
    for (int unid : herbs) {
      count += controller.getInventoryItemCount(unid);
    }

    return count;
  }

  public void bank() {
    controller.setStatus("@whi@Banking..");

    controller.openBank();

    while (countHerbs() > 0) {
      for (int unid : herbs) {
        if (controller.getInventoryItemCount(unid) > 0) {
          controller.depositItem(unid, controller.getInventoryItemCount(unid));
          controller.sleep(250);
        }
      }
    }

    herbsBanked = 0;
    for (int unid : herbs) {
      herbsBanked += controller.getBankItemCount(unid);
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("herb")) herbsPicked++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int herbsPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        herbsPerHr = (int) (herbsPicked * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 160, 21 + 14 + 14, 0xFFFFFF, 128);
      controller.drawString("@gre@HerbHarvester @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@gre@Herbs picked: @whi@"
              + String.format("%,d", herbsPicked)
              + " @gre@(@whi@"
              + String.format("%,d", herbsPerHr)
              + "@gre@/@whi@hr@gre@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@gre@Herbs in bank: @whi@" + String.format("%,d", herbsBanked),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
}
