package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import java.awt.*;
import models.entities.ItemId;

public class CoconutCutter extends IdleScript {

  public static final ScriptInfo info =
      new ScriptInfo(new Category[] {Category.CRAFTING}, "Toast", "Cuts coconuts for a living.");

  private final int FULL_COCONUT = ItemId.COCONUT.getId();
  private final int HALF_COCONUT = ItemId.HALF_COCONUT.getId();
  private final int MACHETE = ItemId.MACHETTE.getId();
  private int totalCut = 0;
  private int leftInBank = 0;

  @Override
  public int start(String[] parameters) {
    paintBuilder.start(4, 4, 160);
    scriptStart();
    return 1000;
  }

  public void scriptStart() {
    while (controller.isRunning()) {

      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);

      if ((controller.getInventoryItemCount(FULL_COCONUT) < 1
              || controller.getInventoryItemCount(MACHETE) < 1)
          && !controller.isInBank()) {
        controller.setStatus("Banking coconuts");
        controller.openBank();
        controller.sleep(300);

        for (int itemId : controller.getInventoryItemIds()) {
          if (itemId != MACHETE) {
            controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
          }
        }
        controller.sleep(200);

        if (controller.getInventoryItemCount(MACHETE) < 1
            && controller.getBankItemCount(MACHETE) < 1) {
          controller.closeBank();
          controller.log("@red@No machete was found. Stopping...");
          controller.stop();
        }

        if (controller.getInventoryItemCount(MACHETE) < 1) controller.withdrawItem(MACHETE, 1);

        leftInBank = controller.getBankItemCount(FULL_COCONUT);

        if (leftInBank < 1) {
          controller.log("@red@No more coconuts left. Stopping script.");
          controller.setStatus("@red@No coconuts left in bank. Logging out!");
          controller.closeBank();

          controller.setAutoLogin(false);
          controller.logout();

          if (!controller.isLoggedIn()) {
            controller.stop();
          }
          return;
        }

        while (controller.getInventoryItemCount() < 28
            && controller.getBankItemCount(FULL_COCONUT) > 0) {
          int freeSlots = 28 - controller.getInventoryItemCount();
          controller.withdrawItem(FULL_COCONUT, freeSlots);
          controller.sleep(200);
        }

        controller.closeBank();
      }

      while (controller.getInventoryItemCount(FULL_COCONUT) > 0) {
        controller.setStatus("Cutting coconuts");

        int before = controller.getInventoryItemCount(FULL_COCONUT);

        controller.useItemOnItemBySlot(
            controller.getInventoryItemSlotIndex(MACHETE),
            controller.getInventoryItemSlotIndex(FULL_COCONUT));

        controller.sleep(500);

        while (controller.isBatching()) {
          controller.sleep(100);
        }

        int after = controller.getInventoryItemCount(FULL_COCONUT);
        int cutThisBatch = before - after;
        if (cutThisBatch > 0) {
          totalCut += cutThisBatch;
        }
      }
    }
  }

  @Override
  public void paintInterrupt() {

    if (controller != null) {
      paintBuilder.setBackgroundColor(0xfffeee, 128);
      paintBuilder.setBorderColor(0x5e330b);
      paintBuilder.setTitleMultipleColor(
          new String[] {"Coconut", "Cutter"}, new int[] {0x5e330b, 0xffeeee}, new int[] {9, 81}, 4);
      paintBuilder.addRow(rowBuilder.centeredSingleStringRow("Toast", 0x69d69d, 1));
      paintBuilder.addSpacerRow(4);
      paintBuilder.addRow(
          rowBuilder.singleSpriteMultipleStringRow(
              HALF_COCONUT,
              80,
              20,
              new String[] {
                paintBuilder.stringFormatInt(totalCut), paintBuilder.stringAmountPerHour(totalCut)
              },
              new int[] {0xffeeee, 0x69d69d},
              new int[] {36, 52},
              16));
      paintBuilder.addRow(
          rowBuilder.centeredSingleStringRow(
              String.format("Coconuts in bank: %s", paintBuilder.stringFormatInt(leftInBank)),
              0xffffff,
              1));

      paintBuilder.draw();
    }
  }
}
