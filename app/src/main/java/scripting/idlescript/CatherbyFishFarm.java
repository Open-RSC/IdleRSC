package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import com.openrsc.client.entityhandling.instances.Item;
import models.entities.ItemId;
import models.entities.SkillId;

public class CatherbyFishFarm extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.FISHING, Category.COOKING, Category.IRONMAN_SUPPORTED},
          "Author Unknown, Paint and Fixed by Seatta",
          "Fishes and cooks fish in Catherby.\n\n"
              + "Fishes shrimp and anchovies to 35 fishing and 30 cooking\n"
              + "Then swaps over to catching tuna and swordfish");

  final int[] rawIds = {349, 351, 366, 369};
  final int[] cookedIds = {350, 352, 353, 367, 368, 370, 371};

  int startFishingXp;
  int startCookingXp;
  int fished = 0;
  int cooked = 0;

  public int start(String[] param) {

    startFishingXp = controller.getStatXp(SkillId.FISHING.getId());
    startCookingXp = controller.getStatXp(SkillId.COOKING.getId());
    paintBuilder.start(4, 18, 220);
    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);

      int toolId =
          controller.getBaseStat(SkillId.FISHING.getId()) < 35
                  || controller.getBaseStat(SkillId.COOKING.getId()) < 30
              ? ItemId.NET.getId()
              : ItemId.HARPOON.getId();
      if (controller.getInventoryItemCount(toolId) < 1) {
        controller.setStatus(String.format("Withdrawing %s", controller.getItemName(toolId)));
        bank();
      }

      if (controller.getInventoryItemCount() < 30) {
        int fishLvl = controller.getBaseStat(controller.getStatId("Fishing"));
        int cookLvl = controller.getBaseStat(controller.getStatId("Cooking"));

        if (fishLvl < 35 || cookLvl < 30) {
          int x = controller.currentX();
          int y = controller.currentY();

          if (controller.currentX() != 418 || controller.currentY() != 499)
            controller.walkTo(418, 499);
          // .npcCommand1(controller.getNearestNpcById(193, false).serverIndex);

          controller.atObject(418, 500);
        } else {
          if (controller.currentX() != 409 && controller.currentY() != 503)
            controller.walkTo(409, 503);

          // controller.npcCommand1(controller.getNearestNpcById(194, false).serverIndex);
          controller.atObject(409, 504);
        }

        controller.sleep(500);
        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(10);
      } else {
        cook();

        bank();

        walkBack();
      }
    }

    return 1000; // start() must return a int value now.
  }

  public void cook() {

    controller.walkTo(419, 499);
    controller.walkTo(428, 498);
    controller.walkTo(436, 493);
    controller.walkTo(435, 486);

    openCookDoor();

    for (int rawId : rawIds) {
      while (controller.getInventoryItemCount(rawId) > 0 && controller.isRunning()) {
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        if (!controller.isBatching()) controller.useItemIdOnObject(432, 480, rawId);
        controller.sleep(250);
      }
    }

    controller.walkTo(435, 485);
    openCookDoor();
  }

  public void openDoor() {
    while (controller.getObjectAtCoord(439, 497) == 64) {
      controller.atObject(439, 497);
      controller.sleep(100);
    }
  }

  public void openCookDoor() {
    while (!controller.isDoorOpen(435, 486)) {
      controller.openDoor(435, 486);
    }
  }

  public void bank() {

    controller.walkTo(439, 497);
    openDoor();

    controller.openBank();

    int toolId =
        controller.getBaseStat(SkillId.FISHING.getId()) < 35
                || controller.getBaseStat(SkillId.COOKING.getId()) < 30
            ? ItemId.NET.getId()
            : ItemId.HARPOON.getId();

    for (Item item : controller.getInventoryItems()) {
      int id = item.getItemDef().id;
      if (id == toolId) continue;

      controller.depositItem(id, controller.getInventoryItemCount(id));
      controller.sleep(180);
    }

    if (controller.getInventoryItemCount(toolId) < 1) {
      if (controller.getBankItemCount(toolId) < 1) {
        controller.log(String.format("Missing %s", controller.getItemName(toolId)), "red");
        controller.log("Stopping script", "red");
        controller.stop();
      } else {
        controller.withdrawItem(toolId, 1);
      }
    }

    controller.walkTo(439, 496);
    openDoor();
  }

  public void walkBack() {
    controller.walkTo(436, 497);
    controller.walkTo(430, 497);
    controller.walkTo(423, 496);
    controller.walkTo(419, 499);
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("nicely cooked")) cooked++;
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You catch")) fished++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      int currentFishingXp = controller.getStatXp(SkillId.FISHING.getId());
      int currentCookingXp = controller.getStatXp(SkillId.COOKING.getId());

      int fishingGains = currentFishingXp - startFishingXp;
      int cookingGains = currentCookingXp - startCookingXp;

      paintBuilder.setTitleCenteredSingleColor("CatherbyFishFarm", 0x00ffff, 4);
      paintBuilder.addSpacerRow(4);
      paintBuilder.setBackgroundColor(0x656565, 180);
      paintBuilder.setBorderColor(0x00ffff);

      paintBuilder.addRow(
          rowBuilder.multipleStringRow(
              new String[] {
                "Caught:",
                paintBuilder.stringFormatInt(fished),
                paintBuilder.stringAmountPerHour(fished)
              },
              new int[] {0x00ffff, 0x00ffff, 0xffff00},
              new int[] {4, 82, 58},
              1));

      paintBuilder.addRow(
          rowBuilder.multipleStringRow(
              new String[] {
                "Fishing XP:",
                paintBuilder.stringFormatInt(fishingGains),
                paintBuilder.stringAmountPerHour(fishingGains)
              },
              new int[] {0x00ffff, 0x00ffff, 0xffff00},
              new int[] {4, 82, 58},
              1));

      paintBuilder.addSpacerRow(4);

      paintBuilder.addRow(
          rowBuilder.multipleStringRow(
              new String[] {
                "Cooked:",
                paintBuilder.stringFormatInt(cooked),
                paintBuilder.stringAmountPerHour(cooked)
              },
              new int[] {0xff8000, 0xff8000, 0xffff00},
              new int[] {4, 82, 58},
              1));

      paintBuilder.addRow(
          rowBuilder.multipleStringRow(
              new String[] {
                "Cooking XP:",
                paintBuilder.stringFormatInt(cookingGains),
                paintBuilder.stringAmountPerHour(cookingGains)
              },
              new int[] {0xff8000, 0xff8000, 0xffff00},
              new int[] {4, 82, 58},
              1));
    }
    paintBuilder.draw();
  }
}
