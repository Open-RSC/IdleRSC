package scripting.idlescript;

import bot.Main;
import bot.ui.components.CustomCheckBox;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.Parameter;
import bot.ui.scriptselector.models.ScriptInfo;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.Location;
import models.entities.SceneryId;
import models.entities.SkillId;

public class MiningGuild extends SeattaScript {
  public static ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.MINING, Category.IRONMAN_SUPPORTED},
          "Seatta",
          "Mines ores in the Mining Guild.",
          new Parameter[] {
            new Parameter(
                "Ores",
                "The ores to mine"
                    + "\n   r - Runite"
                    + "\n   a - Adamantite"
                    + "\n   m - Mithril"
                    + "\n   c - Coal"
                    + "\n   g - Gold"
                    + "\n\nExample for coal and gold: \"c g\"")
          });

  private boolean started = false;
  private Boolean[] mineOres = {false, false, false, false, false};

  private final int[] banked = {0, 0, 0, 0, 0, 0, 0, 0, 0};
  private Ore current = Ore.NONE;

  private final int[] lootIds = {
    ItemId.RUNITE_ORE.getId(),
    ItemId.ADAMANTITE_ORE.getId(),
    ItemId.MITHRIL_ORE.getId(),
    ItemId.COAL.getId(),
    ItemId.GOLD.getId(),
    ItemId.UNCUT_DIAMOND.getId(),
    ItemId.UNCUT_RUBY.getId(),
    ItemId.UNCUT_EMERALD.getId(),
    ItemId.UNCUT_SAPPHIRE.getId()
  };

  /**
   * The entry point of the script where we'll check for requirements.
   *
   * @param param String[] -- Args passed into the script
   * @return int -- Sleep int, not used since this script loops internally
   */
  public int start(String[] param) {
    checkForSkillLevelOrQuit(SkillId.MINING, 60);
    checkForUsablePickaxeOrQuit();

    // Convert args into indices for mineOres
    Map<Character, Integer> oreMap =
        new HashMap<Character, Integer>() {
          {
            put('r', 0);
            put('a', 1);
            put('m', 2);
            put('c', 3);
            put('g', 4);
          }
        };

    // Set mineOres based on the input args and their corresponding indices
    for (String str : param) {
      String s = str.trim().toLowerCase();
      if (s.isEmpty()) continue;

      Integer index = oreMap.get(s.charAt(0));
      if (index != null) mineOres[index] = true;
    }

    // If no args were sent, instead run setup
    if (Arrays.stream(mineOres).noneMatch(Boolean::booleanValue)) setup();

    paintBuilder.start(4, 18, 162);
    started = true;
    return run();
  }

  /**
   * The main loop of the script
   *
   * @return int
   */
  private int run() {
    while (isScriptRunning()) {
      if (!c.isBatching()) current = Ore.NONE;
      if (c.getInventoryItemCount() == 30) bank();
      if (!Location.FALADOR_MINING_GUILD.isAtLocation()) {
        paintStatus = "Walking to Mining Guild";
        Location.FALADOR_MINING_GUILD.walkTowards();
        continue;
      }
      if (current == Ore.NONE) {
        paintStatus = "Waiting for ore...";
        Ore best = getBestAvailableOre();
        if (best != null) mine(best);
      } else {
        while (c.isBatching() && isRunningAndLoggedIn()) {
          if (c.getInventoryItemCount() == 30) {
            c.stopBatching();
            break;
          }
          Ore best = getBestAvailableOre();
          if (best != null && best.ordinal() < current.ordinal()) mine(best);
          sleepTicks(1);
        }
      }
      sleepTicks(1);
    }
    return quit();
  }

  /**
   * Gets the current best available ore that can be mined
   *
   * @return Ore -- The best available ore
   */
  private Ore getBestAvailableOre() {
    for (Ore ore : Ore.values()) if (shouldMine(ore)) return ore;
    return null;
  }

  /**
   * Checks whether the specified ore meets requirements to be mined
   *
   * @param ore Ore -- The ore type to check
   * @return boolean -- Whether it can be mined
   */
  private boolean shouldMine(Ore ore) {
    int miningLevel = c.getBaseStat(SkillId.MINING.getId());
    switch (ore) {
      case RUNITE:
        return mineOres[0] && miningLevel >= 85 && isOreAvailable(Ore.RUNITE);
      case ADAMANTITE:
        return mineOres[1] && miningLevel >= 70 && isOreAvailable(Ore.ADAMANTITE);
      case MITHRIL:
        return mineOres[2] && miningLevel >= 55 && isOreAvailable(Ore.MITHRIL);
      case COAL:
        return mineOres[3] && miningLevel >= 30 && isOreAvailable(Ore.COAL);
      case GOLD:
        return mineOres[4] && miningLevel >= 40 && isOreAvailable(Ore.GOLD);
      default:
        return false;
    }
  }

  /**
   * Set an oreType as the current target to mine
   *
   * @param oreType Ore -- Ore type to mine
   */
  private void mine(Ore oreType) {
    int[] ore = getNearestAvailableOreOfType(oreType);
    if (ore != null) {
      current = oreType;
      paintStatus = String.format("Mining %s", oreType.getName());
      c.atObject(ore[0], ore[1]);
      sleepTicks(3);
    }
  }

  /** Bank our loot */
  private void bank() {
    current = Ore.NONE;
    paintStatus = "Banking";
    Location.FALADOR_EAST_BANK.walkTowards();

    boolean hasBankables = Arrays.stream(lootIds).anyMatch(SeattaScript::hasUnnotedItem);
    while (hasBankables && c.isRunning()) {
      while (!c.isLoggedIn()) sleepTicks(1);

      // Wait until the bank is opened
      while (isRunningAndLoggedIn() && !c.isInBank()) {
        if (!c.isCurrentlyWalking()) c.openBank();
        sleepTicks(1);
      }

      // Add all items to our banked counter and deposit them
      for (int i = 0; i < lootIds.length; i++) {
        int item = lootIds[i];
        if (!isRunningAndLoggedIn()) break;
        if (c.getInventoryItemCount(item) < 1) continue;

        banked[i] += c.getInventoryItemCount(item);
        c.depositItem(lootIds[i], c.getInventoryItemCount(lootIds[i]));
        while (c.getInventoryItemCount(lootIds[i]) > 0 && isRunningAndLoggedIn()) sleepTicks(1);
      }
      if (c.isLoggedIn())
        hasBankables = Arrays.stream(lootIds).anyMatch(SeattaScript::hasUnnotedItem);
    }
    c.closeBank();
    if (!c.isRunning()) quit();
  }

  /**
   * Check for the nearest ore of an oreType
   *
   * @param oreType Ore -- Type to check for
   * @return int[] -- Coordinates of the ore
   */
  private int[] getNearestAvailableOreOfType(Ore oreType) {
    return c.getNearestObjectByIds(oreType.getRocks());
  }

  /**
   * Checks if an ore of a specific type is available
   *
   * @param oreType Ore -- Type to check for
   * @return boolean -- Whether is it available
   */
  private boolean isOreAvailable(Ore oreType) {
    int[] ore = getNearestAvailableOreOfType(oreType);
    return ore != null && ore[1] > 3383;
  }

  private void setup() {
    JFrame scriptFrame = new JFrame();
    scriptFrame.setMinimumSize(new Dimension(180, 180));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.getContentPane().setBackground(Main.primaryBG);
    scriptFrame.setForeground(Main.primaryFG);
    AtomicBoolean buttonPressed = new AtomicBoolean(false);
    String[] oreNames = {"Runite", "Adamantite", "Mithril", "Coal", "Gold"};

    CustomCheckBox[] checks = new CustomCheckBox[oreNames.length];
    for (int i = 0; i < oreNames.length; i++)
      checks[i] = new CustomCheckBox("Mine " + oreNames[i], true);

    JButton startButton = new JButton("Start");
    startButton.setBackground(Main.secondaryBG);
    startButton.setForeground(Main.secondaryFG);
    startButton.setPreferredSize(new Dimension(160, 28));
    startButton.addActionListener(e -> buttonPressed.set(true));

    JPanel checkPanel = new JPanel();
    checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
    checkPanel.setBackground(Main.primaryBG);

    for (CustomCheckBox check : checks) {
      check.setAlignmentX(Component.LEFT_ALIGNMENT);
      check.setForeground(Main.primaryFG);
      check.setBackground(Main.primaryBG);
      check.addActionListener(
          e -> startButton.setEnabled(Arrays.stream(checks).anyMatch(JCheckBox::isSelected)));
      checkPanel.add(check);
    }

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Main.primaryBG);
    buttonPanel.add(startButton);

    scriptFrame.add(checkPanel, BorderLayout.CENTER);
    scriptFrame.add(buttonPanel, BorderLayout.SOUTH);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(Main.getRscFrame());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
    scriptFrame.setResizable(false);
    scriptFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    while (scriptFrame.isVisible() && !buttonPressed.get()) sleepTicks(1);
    scriptFrame.dispose();
    if (!buttonPressed.get()) quit();
    mineOres = Arrays.stream(checks).map(CustomCheckBox::isSelected).toArray(Boolean[]::new);
  }

  @Override
  public void paintInterrupt() {
    if (c != null && started) {
      int[] stringOffsets = {28, 52};
      int[] oreScales = {80, 80, 80, 80, 100, 100, 100, 100, 100};
      int[] oreColors = {
        0x008C8C, 0x718161, 0x617181, 0x6C6C6C, 0xBA9537, 0xd6d6d6, 0xd62a00, 0x249324, 0x0036b8
      };

      paintBuilder.setBorderColor(colorPurple);
      paintBuilder.setBackgroundColor(colorDarkGray, 255);
      paintBuilder.setTitleMultipleColor(
          new String[] {"Mining", "Guild"},
          new int[] {oreColors[0], oreColors[1]},
          new int[] {25, 62},
          4);
      paintBuilder.addRow(rowBuilder.centeredSingleStringRow("Seatta", colorPurple, 1));
      paintBuilder.addRow(rowBuilder.centeredSingleStringRow(paintStatus, colorCyan, 1));
      paintBuilder.addRow(
          rowBuilder.centeredSingleStringRow(
              "Run Time: " + paintBuilder.stringRunTime, colorWhite, 1));
      for (int i = 0; i < lootIds.length; i++) {
        if (i >= mineOres.length || mineOres[i]) {
          paintBuilder.addRow(
              rowBuilder.singleSpriteMultipleStringRow(
                  lootIds[i],
                  oreScales[i],
                  i >= 4 ? 8 : 4,
                  new String[] {
                    paintBuilder.stringFormatInt(banked[i]),
                    paintBuilder.stringAmountPerHour(banked[i])
                  },
                  new int[] {oreColors[i], colorGreen},
                  i >= 4 ? new int[] {24, 52} : stringOffsets,
                  14));
        }
      }
      paintBuilder.draw();
    }
  }
}

enum Ore {
  NONE("None", null),
  RUNITE(
      "Runite",
      new SceneryId[] {SceneryId.ROCK_RUNITE, SceneryId.ROCK_RUNITE2, SceneryId.ROCKS_RUNITE3}),
  ADAMANTITE(
      "Adamantite",
      new SceneryId[] {
        SceneryId.ROCK_ADAMITE, SceneryId.ROCK_ADAMITE2, SceneryId.ROCKS_ADAMITE3,
      }),
  MITHRIL(
      "Mithril",
      new SceneryId[] {
        SceneryId.ROCK_MITHRIL, SceneryId.ROCK_MITHRIL2, SceneryId.ROCKS_MITHRIL3,
      }),
  GOLD(
      "Gold",
      new SceneryId[] {
        SceneryId.ROCK_GOLD, SceneryId.ROCK_GOLD2, SceneryId.ROCKS_GOLD3,
      }),
  COAL(
      "Coal",
      new SceneryId[] {
        SceneryId.ROCK_COAL, SceneryId.ROCK_COAL2, SceneryId.ROCKS_COAL3,
      });

  private final String name;
  private final SceneryId[] rocks;

  Ore(String name, SceneryId[] rockIds) {
    this.name = name;
    rocks = rockIds;
  }

  public int[] getRocks() {
    return Arrays.stream(this.rocks).mapToInt(SceneryId::getId).toArray();
  }

  public String getName() {
    return name;
  }
}
