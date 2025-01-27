package scripting.idlescript;

import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;
import models.entities.ItemId;
import models.entities.Location;
import models.entities.SceneryId;

/**
 * Harvests limpwurt roots and snape grass in Taverly. Coleslaw only.
 *
 * @author Dvorak
 */
public class LimpySnapez extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.HARVESTING, Category.IRONMAN_SUPPORTED},
          "Dvorak, Auto-Pathing by Seatta",
          "Harvests Limpwurt Roots and Snape Grass in Taverley. Coleslaw only.");

  final int clippers = ItemId.HERB_CLIPPERS.getId();
  final int[] plants = {SceneryId.SNAPE_GRASS.getId(), SceneryId.LIMPWURT_ROOT.getId()};
  final int[] loot = {ItemId.SNAPE_GRASS.getId(), ItemId.LIMPWURT_ROOT.getId()};

  boolean tileFlick = false;

  int snapezPicked = 0;
  int snapezInBank = 0;
  int limpzPicked = 0;
  int limpzInBank = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String[] param) {

    if (controller.getUnnotedInventoryItemCount(clippers) < 1) {
      controller.displayMessage("@red@You need to have herb clippers in your inventory!");
      controller.displayMessage("@red@Quitting script!");
      controller.stop();
    }
    if (controller.isRunning()) {
      controller.displayMessage("@red@LimpySnapez by Dvorak. Let's party like it's 2004!");
      controller.quitIfAuthentic();
    }

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (controller.getInventoryItemCount() < 30) {
        if (!Location.isAtLocation(Location.TAVERLEY)) Location.TAVERLEY.walkTowards();

        boolean foundPlants = false;
        for (int plantId : plants) {
          int[] coords = controller.getNearestObjectById(plantId);

          if (coords != null) {
            controller.setStatus("@whi@Picking plant.");
            foundPlants = true;
            controller.atObject(coords[0], coords[1]);
            controller.sleep(1000);
            while (controller.getInventoryItemCount() < 30 && controller.isBatching())
              controller.sleep(10);
            break;
          }
        }

        if (!foundPlants) {
          controller.setStatus("@whi@Searching for plants...");
          if (!tileFlick) {
            controller.walkTo(364, 472);
          } else {
            controller.walkTo(364, 471);
          }
          tileFlick = !tileFlick;
          controller.sleep(700);
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

  public int countPlants() {
    int count = 0;
    for (int j : loot) {
      count += controller.getInventoryItemCount(j);
    }

    return count;
  }

  public void bank() {
    controller.setStatus("@whi@Banking...");

    controller.openBank();

    while (countPlants() > 0) {
      for (int j : loot) {
        if (controller.getInventoryItemCount(j) > 0) {
          controller.depositItem(j, controller.getInventoryItemCount(j));
          controller.sleep(250);
        }
      }
    }

    snapezInBank = controller.getBankItemCount(220);
    limpzInBank = controller.getBankItemCount(469);
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("some grass")) snapezPicked++;
    else if (message.contains("some root")) limpzPicked++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int snapezPerHr = 0;
      int limpzPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        snapezPerHr = (int) (snapezPicked * scale);
        limpzPerHr = (int) (limpzPicked * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 160, 21 + 14 + 14 + 14 + 14, 0xFFFFFF, 128);
      controller.drawString("@lre@Limpy@gre@Snapez @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@gre@Snapez picked: @whi@"
              + String.format("%,d", snapezPicked)
              + " @gre@(@whi@"
              + String.format("%,d", snapezPerHr)
              + "@gre@/@whi@hr@gre@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@gre@Snapez in bank: @whi@" + String.format("%,d", snapezInBank),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@lre@Limpz picked: @whi@"
              + String.format("%,d", limpzPicked)
              + " @lre@(@whi@"
              + String.format("%,d", limpzPerHr)
              + "@lre@/@whi@hr@lre@)",
          10,
          21 + 14 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@lre@Limpz in bank: @whi@" + String.format("%,d", limpzInBank),
          10,
          21 + 14 + 14 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
}
