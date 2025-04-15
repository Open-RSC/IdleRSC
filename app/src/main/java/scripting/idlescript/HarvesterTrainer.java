package scripting.idlescript;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.Parameter;
import bot.ui.scriptselector.models.ScriptInfo;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.*;
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
          "Dvorak, Auto-Pathing and startup GUI by Seatta",
          "Trains Harvesting on Coleslaw in Lumbridge and Ardougne fields.",
          new Parameter[] {
            new Parameter(
                "Crop Type",
                "What crop to harvest: \n"
                    + "   Auto: Harvests the best crop for your level\n"
                    + "   Potato: Harvest only potatoes\n"
                    + "   Garlic: Harvest only garlic\n"
                    + "   Corn: Harvest only corn\n"
                    + "   Cabbage: Harvest only red cabbage\n"
                    + "   Pumpkin: Harvest only white pumpkin")
          });

  int harvested = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;
  private CropInfo crop;
  private boolean autoCrops = false;
  private boolean started = false;

  public int start(String[] parameters) {
    controller.displayMessage("@red@HarvesterTrainer by Dvorak. Let's party like it's 2004!");
    controller.quitIfAuthentic();

    Map<String, CropInfo> cropMap = new HashMap<>();
    cropMap.put("potato", CropInfo.POTATO);
    cropMap.put("garlic", CropInfo.GARLIC);
    cropMap.put("corn", CropInfo.CORN);
    cropMap.put("cabbage", CropInfo.RED_CABBAGE);
    cropMap.put("pumpkin", CropInfo.WHITE_PUMPKIN);

    for (String p : parameters) {
      String lowerP = p.toLowerCase();

      if (lowerP.contains("auto")) {
        autoCrops = true;
        break;
      }

      for (Map.Entry<String, CropInfo> entry : cropMap.entrySet()) {
        if (lowerP.contains(entry.getKey())) {
          crop = entry.getValue();
          break;
        }
      }
    }

    if (!autoCrops) scriptSetup();
    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);

      // Gets the highest level crop training method that the player can do based on the Crop enum
      if (autoCrops)
        crop =
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

  private void scriptSetup() {
    JFrame frame = new JFrame("HarvesterTrainer");
    frame.setLocationRelativeTo(Main.rscFrame);
    frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
    frame.setResizable(false);
    frame.setPreferredSize(new Dimension(272, 128));
    frame.getContentPane().setBackground(Main.primaryBG.brighter());
    frame.getContentPane().setForeground(Main.primaryFG);
    frame.setBackground(Main.primaryBG.brighter());
    frame.setForeground(Main.primaryFG);

    JLabel label = new JLabel("<html><center>Select which crop to harvest.</center></html>");
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
    label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    label.setBackground(Main.primaryBG.brighter());
    label.setForeground(Main.primaryFG);

    JComboBox<String> comboBox = new JComboBox<>();
    comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
    comboBox.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    comboBox.setForeground(Main.primaryFG);
    comboBox.setBackground(Main.primaryBG.brighter());
    comboBox.setBorder(BorderFactory.createEmptyBorder());

    JButton startButton = getJButton(comboBox, frame);

    // Shows the GUI instead if the player has 0 or more than 1 use-able talismans
    if (crop == null) {
      // Maps craft-able runeTypes to an array of Strings for the UIs combo box.
      CropInfo[] harvestableCrops =
          Arrays.stream(CropInfo.values())
              .filter(t -> controller.getCurrentStat(SkillId.HARVESTING.getId()) >= t.getMinLevel())
              .toArray(CropInfo[]::new);

      comboBox.addItem("Auto");
      for (CropInfo harvestableCrop : harvestableCrops) {
        String cropName = harvestableCrop.name().toLowerCase();
        cropName =
            cropName.substring(0, 1).toUpperCase() + cropName.substring(1).replaceAll("_", " ");
        comboBox.addItem(cropName);
      }
      comboBox.setSelectedIndex(0);

      frame.getContentPane().add(label);
      frame.getContentPane().add(comboBox);
      frame.getContentPane().add(startButton);

      frame.pack();
      frame.setVisible(true);
    }
    while (!started && frame.isVisible()) controller.sleep(640);
  }

  private JButton getJButton(JComboBox<String> comboBox, JFrame frame) {
    JButton startButton = new JButton("Start");
    startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    startButton.setBackground(Main.secondaryBG);
    startButton.setForeground(Main.secondaryFG);
    startButton.addActionListener(
        e -> {
          String cropName = ((String) Objects.requireNonNull(comboBox.getSelectedItem()));
          autoCrops = cropName.equalsIgnoreCase("Auto");
          if (!autoCrops) crop = CropInfo.getCropFromName(cropName);
          started = true;
          frame.dispose();
        });
    return startButton;
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

      controller.drawBoxAlpha(7, 7, 214, 21 + 14, 0x228B22, 128);
      controller.drawString(
          "@yel@HarvesterTrainer @whi@by @yel@Dvorak & Seatta", 10, 21, 0xFFFFFF, 1);
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
      0,
      SceneryId.POTATO_PLANT,
      new Location[] {Location.LUMBRIDGE_CROP_FIELD, Location.ARDOUGNE_CROP_FIELD}),
  GARLIC(
      9,
      9,
      SceneryId.GARLIC_PLANT,
      new Location[] {Location.LUMBRIDGE_CROP_FIELD, Location.ARDOUGNE_CROP_FIELD}),
  CORN(
      20,
      20,
      SceneryId.CORN_PLANT,
      new Location[] {Location.LUMBRIDGE_CORN_FIELD, Location.ARDOUGNE_CROP_FIELD}),
  RED_CABBAGE(60, 30, SceneryId.RED_CABBAGE, new Location[] {Location.LUMBRIDGE_CABBAGE_FIELD}),
  WHITE_PUMPKIN(90, 47, SceneryId.WHITE_PUMPKIN, new Location[] {Location.ARDOUGNE_CROP_FIELD});

  final int level;
  final int minLevel;
  final int id;
  final Location[] locations;

  CropInfo(int level, int minLevel, SceneryId plant, Location[] locations) {
    this.level = level;
    this.minLevel = minLevel;
    this.id = plant.getId();
    this.locations = locations;
  }

  public int getLevel() {
    return level;
  }

  public int getMinLevel() {
    return minLevel;
  }

  public int getId() {
    return id;
  }

  public Location[] getFields() {
    return locations;
  }

  public static CropInfo getCropFromName(String name) {
    String convertedName = name.toUpperCase().replaceAll(" ", "_");
    return Arrays.stream(CropInfo.values())
        .filter(ci -> ci.name().equalsIgnoreCase(convertedName))
        .findFirst()
        .orElse(POTATO);
  }
}
