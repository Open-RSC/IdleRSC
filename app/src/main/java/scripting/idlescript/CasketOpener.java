package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import java.util.Arrays;
import models.entities.ItemId;

public class CasketOpener extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.MISCELLANEOUS, Category.IRONMAN_SUPPORTED},
          "Acry",
          "Withdraws caskets from Catherby bank, opens them, and banks the loot.");

  private final int[][] CATHERBY_BANK = {{437, 491}, {443, 496}};
  private final int casketId = ItemId.CASKET.getId();
  private int diamondCount = 0;
  private int rubyCount = 0;
  private int emeraldCount = 0;
  private int sapphireCount = 0;
  private int toothHalfCount = 0;
  private int loopHalfCount = 0;
  private int coinsCount = 0;

  @Override
  public int start(String[] args) {
    paintBuilder.start(4, 18, 174);

    while (controller.isRunning() && controller.isLoggedIn()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);

      if (controller.getInventoryItemCount(casketId) == 0) {
        bank();
      } else {
        openCaskets();
      }
      controller.sleep(640);
    }
    return 1000;
  }

  private void openCaskets() {
    while (controller.getInventoryItemCount(casketId) > 0 && controller.isRunning()) {
      controller.setStatus("@cya@Opening Caskets");
      controller.itemCommand(casketId);
      controller.sleep(640);
      if (controller.isBatching()) {
        while (controller.isBatching()
            && controller.getInventoryItemCount(casketId) > 0
            && controller.isRunning()
            && controller.isLoggedIn()) {
          controller.sleep(640);
        }
      }
    }
  }

  private void bank() {
    controller.setStatus("@cya@Banking");
    if (!isInRectangle(CATHERBY_BANK)) {
      controller.walkTo(420, 497);
      controller.walkTo(439, 497);
    }
    openDoor();
    controller.openBank();
    while (!controller.isInBank() && controller.isRunning() && controller.isLoggedIn())
      controller.sleep(640);

    if (controller.isInBank()) {
      if (controller.getInventoryItemCount() > 0) {
        diamondCount += controller.getInventoryItemCount(ItemId.UNCUT_DIAMOND.getId());
        rubyCount += controller.getInventoryItemCount(ItemId.UNCUT_RUBY.getId());
        emeraldCount += controller.getInventoryItemCount(ItemId.UNCUT_EMERALD.getId());
        sapphireCount += controller.getInventoryItemCount(ItemId.UNCUT_SAPPHIRE.getId());
        toothHalfCount += controller.getInventoryItemCount(ItemId.TOOTH_HALF_KEY.getId());
        loopHalfCount += controller.getInventoryItemCount(ItemId.LOOP_HALF_KEY.getId());
        coinsCount += controller.getInventoryItemCount(ItemId.COINS.getId());

        for (int id : controller.getInventoryItemIds()) {
          controller.depositItem(id, controller.getInventoryItemCount(id));
        }
        controller.sleep(1280);
      }

      if (controller.getBankItemCount(casketId) > 0) {
        controller.withdrawItem(casketId, 29);
        controller.sleep(1280);
      } else {
        controller.log("No caskets found in bank.", "red");
        controller.stop();
      }
      controller.closeBank();
    }
  }

  public void openDoor() {
    while (controller.getObjectAtCoord(439, 497) == 64
        && controller.isRunning()
        && controller.isLoggedIn()) {
      controller.atObject(439, 497);
      controller.sleep(100);
    }
  }

  public boolean isInRectangle(int[][] areaRectangle) {
    int[] toSortX = {areaRectangle[0][0], areaRectangle[1][0]};
    int[] toSortY = {areaRectangle[0][1], areaRectangle[1][1]};
    Arrays.sort(toSortX);
    Arrays.sort(toSortY);
    int[][] sortedRectangle = {{toSortX[0], toSortY[0]}, {toSortX[1], toSortY[1]}};

    return controller.currentX() >= sortedRectangle[0][0]
        && controller.currentX() <= sortedRectangle[1][0]
        && controller.currentY() >= sortedRectangle[0][1]
        && controller.currentY() <= sortedRectangle[1][1];
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      int white = 0xF8F8F2;
      int yellow = 0xF1FA8C;
      int purple = 0xBD93F9;
      int darkGray = 0x282A36;

      paintBuilder.setBorderColor(purple);
      paintBuilder.setBackgroundColor(darkGray, 255);
      paintBuilder.setTitleMultipleColor(
          new String[] {"Casket", "Opener"}, new int[] {0xb74413, 0xcfdf1f}, new int[] {24, 68}, 4);
      paintBuilder.addRow(
          rowBuilder.centeredSingleStringRow("Runtime: " + paintBuilder.stringRunTime, white, 1));

      paintBuilder.addSpacerRow(4);

      paintBuilder.addRow(
          rowBuilder.multipleStringRow(
              new String[] {
                "Coins",
                paintBuilder.stringFormatInt(coinsCount),
                paintBuilder.stringAmountPerHour(coinsCount)
              },
              new int[] {white, white, yellow},
              new int[] {4, 86, 45},
              1));

      // Gems
      int[] gemIds = {
        ItemId.UNCUT_DIAMOND.getId(),
        ItemId.UNCUT_RUBY.getId(),
        ItemId.UNCUT_EMERALD.getId(),
        ItemId.UNCUT_SAPPHIRE.getId()
      };
      int[] gemCounts = {diamondCount, rubyCount, emeraldCount, sapphireCount};
      String[] gemNames = {"Diamond", "Ruby", "Emerald", "Sapphire"};

      for (int i = 0; i < gemIds.length; i++) {
        paintBuilder.addRow(
            rowBuilder.multipleStringRow(
                new String[] {
                  gemNames[i],
                  paintBuilder.stringFormatInt(gemCounts[i]),
                  paintBuilder.stringAmountPerHour(gemCounts[i])
                },
                new int[] {white, white, yellow},
                new int[] {4, 86, 45},
                1));
      }

      // Keys
      int[] keyIds = {ItemId.TOOTH_HALF_KEY.getId(), ItemId.LOOP_HALF_KEY.getId()};
      int[] keyCounts = {toothHalfCount, loopHalfCount};
      String[] keyNames = {"Tooth Half", "Loop Half"};

      for (int i = 0; i < keyIds.length; i++) {
        paintBuilder.addRow(
            rowBuilder.multipleStringRow(
                new String[] {
                  keyNames[i],
                  paintBuilder.stringFormatInt(keyCounts[i]),
                  paintBuilder.stringAmountPerHour(keyCounts[i])
                },
                new int[] {white, white, yellow},
                new int[] {4, 86, 45},
                1));
      }

      paintBuilder.draw();
    }
  }
}
