package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import java.util.Arrays;
import models.entities.Location;
import models.entities.SceneryId;
import models.entities.SkillId;

/**
 * Trains Harvesting on Coleslaw in Lumbridge and Ardougne fields.
 *
 * @author Dvorak
 */
public class HarvesterTrainer extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.HARVESTING, Category.IRONMAN_SUPPORTED, Category.ULTIMATE_IRONMAN_SUPPORTED
          },
          "Dvorak, Auto-Pathing by Seatta",
          "Trains Harvesting on Coleslaw in Lumbridge and Ardougne fields.");

  int harvested = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    controller.displayMessage("@red@HarvesterTrainer by Dvorak. Let's party like it's 2004!");
    controller.quitIfAuthentic();

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);

      // Gets the highest level crop training method that the player can do based on the Crop enum
      CropInfo crop =
          Arrays.stream(CropInfo.values())
              .filter(cr -> controller.getBaseStat(SkillId.HARVESTING.getId()) >= cr.getLevel())
              .reduce((crop1, crop2) -> crop1.getLevel() > crop2.getLevel() ? crop1 : crop2)
              .orElse(CropInfo.POTATO);

      // Walk to the closest crop field if not already there
      boolean isAtPlot =
          Arrays.stream(crop.getFields()).anyMatch(location -> Location.isAtLocation(location));
      if (!isAtPlot) Location.walkTowardsClosest(crop.getFields());

      // Harvest crop or wait for the plant to spawn
      int[] coords = controller.getNearestObjectById(crop.id);
      if (coords != null) {
        controller.setStatus("@yel@Harvesting...");
        controller.atObject(coords[0], coords[1]);
        controller.sleep(1280);

        while (controller.isBatching()) {
          controller.sleep(640);
        }
      } else {
        controller.setStatus("@yel@Waiting for spawn..");
      }

      controller.sleep(100);
    }

    return 1000; // start() must return a int value now.
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You get")) harvested++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int harvestedPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        harvestedPerHr = (int) (harvested * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 190, 21 + 14, 0x228B22, 128);
      controller.drawString("@yel@HarvesterTrainer @whi@by @yel@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@yel@Stuff Harvested: @whi@"
              + String.format("%,d", harvested)
              + " @yel@(@whi@"
              + String.format("%,d", harvestedPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
    }
  }
}

enum CropInfo {
  POTATO(
      0,
      SceneryId.POTATO_PLANT,
      new Location[] {Location.LUMBRIDGE_CROP_FIELD, Location.ARDOUGNE_CROP_FIELD}),
  GARLIC(
      9,
      SceneryId.GARLIC_PLANT,
      new Location[] {Location.LUMBRIDGE_CROP_FIELD, Location.ARDOUGNE_CROP_FIELD}),
  CORN(
      20,
      SceneryId.CORN_PLANT,
      new Location[] {Location.LUMBRIDGE_CORN_FIELD, Location.ARDOUGNE_CROP_FIELD}),
  RED_CABBAGE(60, SceneryId.RED_CABBAGE, new Location[] {Location.LUMBRIDGE_CABBAGE_FIELD}),
  WHITE_PUMPKIN(90, SceneryId.WHITE_PUMPKIN, new Location[] {Location.ARDOUGNE_CROP_FIELD});

  final int level;
  final int id;
  final Location[] locations;

  CropInfo(int level, SceneryId plant, Location[] locations) {
    this.level = level;
    this.id = plant.getId();
    this.locations = locations;
  }

  public int getLevel() {
    return level;
  }

  public int getId() {
    return id;
  }

  public Location[] getFields() {
    return locations;
  }
}
