package scripting.idlescript.SeattaScript;

import bot.Main;
import bot.ui.components.CustomCheckBox;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.Parameter;
import bot.ui.scriptselector.models.ScriptInfo;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.Location;
import models.entities.SceneryId;
import models.entities.SkillId;

public class FishingGuild extends SeattaScript {
  public static ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.FISHING,
            Category.COOKING,
            Category.IRONMAN_SUPPORTED,
            Category.ULTIMATE_IRONMAN_SUPPORTED
          },
          "Seatta",
          "Fishes and cooks within the Fishing Guild."
              + "\n\nChat Commands:"
              + "\n   Catch sharks"
              + "\n      ::shark"
              + "\n   Catch swordfish & tuna"
              + "\n      ::swordfish ::sword ::tuna"
              + "\n   Catch lobster"
              + "\n      ::lobster ::lob"
              + "\n   Catch bass, cod, and mackerel"
              + "\n      ::net ::bass ::cod ::mackerel"
              + "\n   Catch caskets"
              + "\n      ::casket"
              + "\n   Toggle cooking"
              + "\n      ::cook"
              + "\n   Toggle certing"
              + "\n      ::cert",
          new Parameter[] {
            new Parameter(
                "Select the fish to catch and choose whether "
                    + "\n   to cook it"
                    + "\n\n   Fish Type (choose ONE):"
                    + "\n      s - Shark"
                    + "\n      h - Swordfish and Tuna"
                    + "\n      l - Lobster"
                    + "\n      n - Bass, Cod, and Mackerel"
                    + "\n      k - Caskets"
                    + "\n\n   Modifiers (optional):"
                    + "\n      c - Enable cooking"
                    + "\n      t - Enable certing"
                    + "\n\n   Examples:"
                    + "\n   -  \"h\"    - Catch swordfish"
                    + "\n   -  \"sc\"  - Catch and cook sharks"
                    + "\n   -  \"lt\"    - Catch and cert lobsters"
                    + "\n   -  \"ctn\" - Catch, cook, and cert bass")
          });

  private boolean started = false;
  private FishingTarget target, newTarget;
  private int[] banked = new int[] {0};
  boolean enableCooking, enableCerting = false;
  private int lastCookingLevel = -1;

  public int start(String[] parameters) {
    checkForSkillLevelOrQuit(SkillId.FISHING, 68);
    if (parameters.length > 0) parseFishArgs(parameters);
    if (target == null) setup();
    if (isUIM()) {
      ensureItemOrQuit(target.tool);
      if (!enableCerting) checkForEmptyInventorySpacesOrQuit(6);
    }

    paintBuilder.start(4, 18, 162);
    started = true;
    return run();
  }

  private void parseFishArgs(@Nonnull String[] params) {
    FishingTarget selected = null;
    boolean cooking = false;
    boolean certing = false;
    for (String param : params) {
      if (param == null) continue;
      for (char ch : param.toLowerCase().toCharArray()) {
        if (ch == 'c') {
          cooking = true;
          continue;
        }
        if (ch == 't') {
          certing = true;
          continue;
        }

        FishingTarget t = FishingTarget.fromFlag(ch);
        if (t != null && selected == null) selected = t;
      }
    }

    if (selected == null) return;
    this.enableCooking = cooking;
    this.enableCerting = certing;
    resetGainedXp(SkillId.COOKING, true);
    swapTarget(selected);
  }

  private int run() {
    displayFishyMessage(randomStartMessage());
    while (isScriptRunning()) {
      if (target != newTarget) swapTarget(newTarget);
      checkIfTargetIsNowCookable();
      ensureItemOrQuit(target.tool);
      if (c.getInventoryItemCount() == 30 && (!isUIM() || enableCerting)) {
        if (enableCooking && target != FishingTarget.CASKET) {
          boolean hasRawFish =
              Arrays.stream(target.getRawIds()).anyMatch(SeattaScript::hasUnnotedItem);
          boolean hasCookableFish =
              Arrays.stream(target.fish)
                  .anyMatch(
                      f ->
                          f.cooked != null
                              && f.cookingLevel <= lastCookingLevel
                              && hasUnnotedItem(f.raw));

          if (hasRawFish && hasCookableFish) cook();
        }

        if (isUIM() || enableCerting) {
          if (!enableCerting) handleDropping();
          else handleCerting();
        } else {
          bank();
        }
      }
      paintStatus = "Fishing";
      if (!Location.FISHING_GUILD_DOCKS.isAtLocation()) walkTowards(Location.FISHING_GUILD_DOCKS);
      if (!c.isBatching()) {
        if (target.usesObjectCommand2) c.atObject2(target.fishingSpot.getId());
        else c.atObject(target.fishingSpot.getId());
      }
      sleepTick();
    }

    return quit();
  }

  private void checkIfTargetIsNowCookable() {
    int currentCookingLevel = c.getBaseStat(SkillId.COOKING.getId());
    if (lastCookingLevel != -1 && enableCooking) {
      for (int i = 0; i < target.fish.length; i++) {
        FishingTarget.Fish fish = target.fish[i];
        if (fish.cooked == null) continue;
        if (lastCookingLevel < fish.cookingLevel && currentCookingLevel >= fish.cookingLevel) {
          banked[i] = 0;
        }
      }
    }
    lastCookingLevel = currentCookingLevel;
  }

  private void cook() {
    forceStopBatching();
    paintStatus = "Cooking";
    walkTowards(Location.FISHING_GUILD_RANGE);
    for (FishingTarget.Fish fish : target.fish) {
      if (fish.cooked == null || fish.cookingLevel > lastCookingLevel) continue;
      ItemId rawId = fish.raw;
      c.sleepUntil(
          () -> {
            if (!c.isBatching()) {
              c.useItemIdOnObject(583, 520, rawId.getId());
              sleepTicks(4);
            }
            return !hasItem(rawId);
          },
          120000);
    }
  }

  private void bank() {
    forceStopBatching();
    paintStatus = "Banking";
    if (openNearestBank()) {

      // Deposit and track fish
      for (int i = 0; i < target.fish.length; i++) {
        FishingTarget.Fish fish = target.fish[i];
        boolean cooked =
            enableCooking && fish.cooked != null && fish.cookingLevel <= lastCookingLevel;
        int itemId = cooked ? fish.cooked.getId() : fish.raw.getId();
        int amount = c.getUnnotedInventoryItemCount(itemId);
        banked[i] += amount;
        c.sleepUntil(
            () -> {
              c.depositItem(itemId, amount);
              return c.getUnnotedInventoryItemCount(itemId) == 0;
            });
      }

      // Cleanup pass for leftover non-tool items
      for (int item : c.getInventoryItemIds()) {
        if (item == target.tool.getId()) continue;
        int amount = c.getUnnotedInventoryItemCount(item);
        c.sleepUntil(
            () -> {
              c.depositItem(item, amount);
              return c.getUnnotedInventoryItemCount(item) == 0;
            });
      }

      c.closeBank();
    }
  }

  private void handleDropping() {
    paintStatus = "Dropping";
    if (!Location.FISHING_GUILD_DOCKS.isAtLocation()) walkTowards(Location.FISHING_GUILD_DOCKS);
    for (FishingTarget.Fish fish : target.fish) {
      boolean cooked =
          enableCooking && fish.cooked != null && fish.cookingLevel <= lastCookingLevel;
      dropAllOfItem(cooked ? fish.cooked : fish.raw);
    }
    if (target == FishingTarget.CASKET || target == FishingTarget.BASS_COD_MACKEREL) {
      for (FishingTarget.Fish fish : FishingTarget.BASS_COD_MACKEREL.fish) {
        boolean cooked =
            enableCooking && fish.cooked != null && fish.cookingLevel <= lastCookingLevel;
        dropAllOfItem(cooked ? fish.cooked : fish.raw);
      }
      for (ItemId casketJunk : FishingTarget.CASKET.getRawIds()) dropAllOfItem(casketJunk);
    }
  }

  private void handleCerting() {
    forceStopBatching();

    for (FishingTarget.Fish fish : target.fish) {
      if (fish.certerInfo != null) continue;
      boolean cooked =
          enableCooking && fish.cooked != null && fish.cookingLevel <= lastCookingLevel;
      dropAllOfItem(cooked ? fish.cooked : fish.raw);
    }
    if (target == FishingTarget.BASS_COD_MACKEREL) {
      for (FishingTarget.Fish fish : FishingTarget.CASKET.fish) {
        if (fish.certerInfo == null) dropAllOfItem(fish.raw);
      }
    }

    FishingTarget.Fish certableFish = target.fish[0];
    boolean certCooked =
        enableCooking
            && certableFish.cooked != null
            && certableFish.cookingLevel <= lastCookingLevel;
    ItemId fishId = certCooked ? certableFish.cooked : certableFish.raw;

    if (c.getUnnotedInventoryItemCount(fishId.getId()) / 5 == 0) return;

    paintStatus = "Certing";
    int chatIndex =
        certCooked ? certableFish.certerInfo.chatIndex : certableFish.certerInfo.chatIndex + 1;
    walkTowardsNearestBank();
    c.talkToNpcId(certableFish.certerInfo.certerId, true);
    c.optionAnswer(1);
    c.sleepUntil(
        () ->
            c.getOptionsMenuText(0).toLowerCase().contains("swordfish")
                || c.getOptionsMenuText(0).toLowerCase().contains("bass"));
    c.optionAnswer(chatIndex);
    c.sleepUntil(() -> c.getOptionsMenuText(0).toLowerCase().contains("five"));
    int amount = c.getUnnotedInventoryItemCount(fishId.getId());
    int option = Math.min((amount - 5) / 5, 4);
    banked[0] += (amount / 5) * 5;
    c.optionAnswer(option);
    c.sleepUntil(() -> c.getUnnotedInventoryItemCount(fishId.getId()) / 5 == 0);

    if (target == FishingTarget.BASS_COD_MACKEREL)
      for (ItemId junkItem : FishingTarget.CASKET.getRawIds()) dropAllOfItem(junkItem);
  }

  private void enqueueTargetSwap(FishingTarget target) {
    if (this.target == target) return;
    if (c.getBaseStat(SkillId.FISHING.getId()) < target.requiredLevel) {
      c.log(String.format("You don't know how to catch %s yet", target.name.toLowerCase()));
      return;
    }

    String swapMessage = String.format("Swapping to fishing %s shortly", target.name.toLowerCase());
    displayFishyMessage(swapMessage);
    Main.log(swapMessage);
    newTarget = target;
  }

  public void displayFishyMessage(String str) {
    c.displayMessage(String.format("@cya@><> @whi@%s @cya@<><", str));
  }

  private void swapTarget(FishingTarget target) {
    if (this.target == target) return;
    banked = new int[target.fish.length];
    lastCookingLevel = -1;
    resetGainedXp(SkillId.FISHING, true);
    resetGainedXp(SkillId.COOKING, true);
    paintBuilder.resetStartTime();
    this.target = target;
    newTarget = target;
    c.stopBatching();
    sleepTick();
  }

  private void setup() {
    JFrame frame = new JFrame();
    frame.setMinimumSize(new Dimension(180, 220));
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.getContentPane().setBackground(Main.primaryBG);

    AtomicBoolean started = new AtomicBoolean(false);

    FishingTarget[] targets = FishingTarget.values();
    CustomCheckBox[] fishChecks = new CustomCheckBox[targets.length];

    int fishingLevel = c.getBaseStat(SkillId.FISHING.getId());
    int highestUnlocked = -1;

    // --- Cook / Cert ---
    CustomCheckBox cookCheck = new CustomCheckBox("Cook", "Enable cooking of fish", true);
    CustomCheckBox certCheck =
        new CustomCheckBox(
            "Cert",
            "Enable certing of fish\n For UIM accounts, if unchecked catches will be dropped",
            isUIM());

    certCheck.setOpaque(false);
    cookCheck.setOpaque(false);

    for (CustomCheckBox cb : new CustomCheckBox[] {cookCheck, certCheck}) {
      cb.setForeground(Main.primaryFG);
      cb.setBackground(Main.primaryBG);
    }

    // --- Start button ---
    JButton startButton = new JButton("Start");
    startButton.setEnabled(false);
    startButton.setBackground(Main.secondaryBG);
    startButton.setForeground(Main.secondaryFG);
    startButton.addActionListener(e -> started.set(true));

    // --- Fish checkboxes ---
    JPanel fishPanel =
        new JPanel() {
          @Override
          public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
          }
        };
    fishPanel.setLayout(new BoxLayout(fishPanel, BoxLayout.Y_AXIS));
    fishPanel.setBackground(Main.primaryBG);
    fishPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // --- Cook / Cert panel ---
    JPanel cookCertRow = new JPanel(new GridLayout(1, 2));
    cookCertRow.setAlignmentX(Component.LEFT_ALIGNMENT);
    cookCertRow.setBackground(Main.primaryBG);

    JPanel cookRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    cookRow.setBackground(Main.primaryBG);
    cookRow.add(cookCheck);

    JPanel certRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    certRow.setBackground(Main.primaryBG);
    certRow.add(certCheck);

    cookCertRow.add(cookRow);
    cookCertRow.add(certRow);

    int cookingLevel = c.getBaseStat(SkillId.COOKING.getId());
    final Color disabledColor = Color.gray;

    for (int i = 0; i < targets.length; i++) {
      FishingTarget t = targets[i];
      boolean locked = fishingLevel < t.requiredLevel || (t == FishingTarget.CASKET && isUIM());

      CustomCheckBox check = new CustomCheckBox(t.name, false);
      check.setForeground(Main.primaryFG);
      check.setOpaque(false);
      if (locked) check.setEnabled(false);
      else if (highestUnlocked == -1) highestUnlocked = i;

      JPanel row = new JPanel(new BorderLayout());
      row.setBackground(locked ? disabledColor : Main.primaryBG);
      row.add(check, BorderLayout.WEST);

      fishChecks[i] = check;
      fishPanel.add(row);

      final int index = i;
      check.addActionListener(
          e -> {
            for (int j = 0; j < fishChecks.length; j++)
              if (j != index) fishChecks[j].setSelected(false);

            boolean casketSelected =
                fishChecks[index].isSelected() && targets[index] == FishingTarget.CASKET;

            if (casketSelected) {
              cookCheck.setEnabled(false);
              certCheck.setEnabled(false);
              cookCheck.setSelected(false);
              certCheck.setSelected(false);
              cookRow.setBackground(disabledColor);
              certRow.setBackground(disabledColor);
            } else {
              boolean anySelected = Arrays.stream(fishChecks).anyMatch(JCheckBox::isSelected);
              boolean selectedCanCook =
                  anySelected
                      && Arrays.stream(targets[index].fish)
                          .anyMatch(f -> f.cooked != null && f.cookingLevel <= cookingLevel);
              cookCheck.setEnabled(!anySelected || selectedCanCook);
              if (!cookCheck.isEnabled()) cookCheck.setSelected(false);
              cookRow.setBackground(cookCheck.isEnabled() ? Main.primaryBG : disabledColor);
              certCheck.setEnabled(true);
              certRow.setBackground(Main.primaryBG);
            }

            startButton.setEnabled(Arrays.stream(fishChecks).anyMatch(JCheckBox::isSelected));
          });
    }

    // --- Select highest unlocked by default ---
    if (highestUnlocked != -1) {
      fishChecks[highestUnlocked].setSelected(true);
      fishChecks[highestUnlocked].getActionListeners()[0].actionPerformed(null);
      startButton.setEnabled(true);
    } else {
      cookCheck.setEnabled(true);
      cookRow.setBackground(Main.primaryBG);
    }

    // --- Main panel ---
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Main.primaryBG);
    panel.add(fishPanel);
    panel.add(Box.createVerticalStrut(6));
    panel.add(cookCertRow);

    // --- Button panel ---
    JPanel buttonWrapper = new JPanel(new BorderLayout());
    buttonWrapper.setBackground(Main.primaryBG);
    buttonWrapper.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
    buttonWrapper.add(startButton, BorderLayout.CENTER);

    frame.add(panel, BorderLayout.CENTER);
    frame.add(buttonWrapper, BorderLayout.SOUTH);
    frame.pack();
    frame.setLocationRelativeTo(Main.getRscFrame());
    frame.setVisible(true);

    while (frame.isVisible() && !started.get()) sleepTicks(1);

    if (!started.get()) {
      frame.dispose();
      quit();
      return;
    }

    for (int i = 0; i < fishChecks.length; i++) {
      if (fishChecks[i].isSelected()) {
        swapTarget(targets[i]);
        break;
      }
    }

    enableCooking = cookCheck.isSelected();
    enableCerting = certCheck.isSelected();
    frame.dispose();
  }

  private String randomStartMessage() {
    String[] messages = {
      "Mornin', nice day for fishing, ain’t it!",
      "Gone fishin'",
      "I used to be an adventurer like you, then I took a fish to the knee",
      "Stay a while and fishin'",
      "One fish to rule them all",
      "We're whalers on the moon",
      "You can't handle the trout"
    };
    return messages[new Random().nextInt(messages.length)];
  }

  @Override
  public void chatCommandInterrupt(String commandText) {
    String command = commandText.toLowerCase();
    switch (command) {
      case "shark":
        enqueueTargetSwap(FishingTarget.SHARK);
        break;
      case "swordfish":
      case "sword":
      case "tuna":
        enqueueTargetSwap(FishingTarget.SWORDFISH_TUNA);
        break;
      case "lobster":
      case "lob":
        enqueueTargetSwap(FishingTarget.LOBSTER);
        break;
      case "net":
      case "bass":
      case "cod":
      case "mackerel":
        enqueueTargetSwap(FishingTarget.BASS_COD_MACKEREL);
        break;
      case "casket":
        enqueueTargetSwap(FishingTarget.CASKET);
        break;
      case "cert":
        if (target == FishingTarget.CASKET) break;
        enableCerting = !enableCerting;
        banked = new int[target.fish.length];
        c.log("You will " + (enableCerting ? "now cert your fish" : "no longer cert your fish"));
        break;

      case "cook":
        if (target == FishingTarget.CASKET) break;
        enableCooking = !enableCooking;
        banked = new int[target.fish.length];
        c.log(
            "You will "
                + (enableCooking
                    ? "now cook " + target.name.toLowerCase()
                    : "no longer cook " + target.name.toLowerCase()));
        break;
      default:
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (!isUIM() || enableCerting) return;
    // We only need to use message interrupts for catch tracking on UIMs with certing disabled
    String fishMessage = message.toLowerCase().replace("you catch a ", "");
    switch (fishMessage) {
      case "shark":
      case "swordfish":
      case "lobster":
      case "bass":
        banked[0] += 1;
        break;
      case "tuna":
      case "cod":
        banked[1] += 1;
        break;
      case "mackerel":
        banked[2] += 1;
        break;
      default:
    }
  }

  @Override
  public void paintInterrupt() {
    if (c == null || !started) return;

    paintBuilder.setBorderColor(colorPurple);
    paintBuilder.setBackgroundColor(colorDarkGray, 255);
    paintBuilder.setTitleMultipleColor(
        new String[] {"Fishing", "Guild"},
        new int[] {enableCerting ? colorWhite : colorBlue, enableCooking ? colorOrange : colorCyan},
        new int[] {18, 70},
        4);
    paintBuilder.addRow(rowBuilder.centeredSingleStringRow("Seatta", colorPurple, 1));
    paintBuilder.addRow(rowBuilder.centeredSingleStringRow(paintStatus, colorCyan, 1));
    paintBuilder.addRow(
        rowBuilder.centeredSingleStringRow(
            "Run Time: " + paintBuilder.stringRunTime, colorWhite, 1));

    paintBuilder.addSpacerRow(4);
    paintBuilder.addRow(
        rowBuilder.multipleStringRow(
            new String[] {
              "FXP " + paintBuilder.stringFormatInt(getGainedXp(SkillId.FISHING)),
              paintBuilder.stringAmountPerHour(getGainedXp(SkillId.FISHING))
            },
            new int[] {colorBlue, colorYellow},
            new int[] {11, 77},
            1));

    if (enableCooking) {
      paintBuilder.addRow(
          rowBuilder.multipleStringRow(
              new String[] {
                "CXP " + paintBuilder.stringFormatInt(getGainedXp(SkillId.COOKING)),
                paintBuilder.stringAmountPerHour(getGainedXp(SkillId.COOKING))
              },
              new int[] {colorOrange, colorYellow},
              new int[] {9, 79},
              1));
    }

    paintBuilder.addSpacerRow(4);
    for (int i = 0; i < target.fish.length; i++) {
      if (isUIM() && !enableCooking) break;
      FishingTarget.Fish fish = target.fish[i];
      ItemId raw = fish.raw;
      ItemId cook = fish.cooked;

      if (target == FishingTarget.CASKET && i > 2) break;
      boolean canCook =
          enableCooking && fish.cooked != null && fish.cookingLevel <= lastCookingLevel;
      ItemId displayItem = (target == FishingTarget.CASKET || !canCook) ? raw : cook;

      if (enableCerting && fish.certerInfo == null) continue;

      paintBuilder.addRow(
          rowBuilder.singleSpriteMultipleStringRow(
              displayItem.getId(),
              fish.spriteScale,
              8,
              new String[] {
                paintBuilder.stringFormatInt(banked[i]), paintBuilder.stringAmountPerHour(banked[i])
              },
              new int[] {colorWhite, colorYellow},
              new int[] {28, 52},
              14));
    }

    paintBuilder.draw();
  }

  enum FishingTarget {
    SHARK(
        76,
        "Shark",
        ItemId.HARPOON,
        SceneryId.FISH_NET_HARPOON,
        true,
        new Fish(80, 60, ItemId.RAW_SHARK, ItemId.SHARK, new CerterInfo(370, 2))),

    SWORDFISH_TUNA(
        50,
        "Swordfish and Tuna",
        ItemId.HARPOON,
        SceneryId.FISH_CAGE_HARPOON,
        true,
        new Fish(45, 60, ItemId.RAW_SWORDFISH, ItemId.SWORDFISH, new CerterInfo(369, 0)),
        new Fish(30, 60, ItemId.RAW_TUNA, ItemId.TUNA, null)),

    LOBSTER(
        40,
        "Lobster",
        ItemId.LOBSTER_POT,
        SceneryId.FISH_CAGE_HARPOON,
        false,
        new Fish(40, 65, ItemId.RAW_LOBSTER, ItemId.LOBSTER, new CerterInfo(369, 2))),

    BASS_COD_MACKEREL(
        16,
        "Bass, Cod, and Mackerel",
        ItemId.BIG_NET,
        SceneryId.FISH_NET_HARPOON,
        false,
        new Fish(43, 55, ItemId.RAW_BASS, ItemId.BASS, new CerterInfo(370, 0)),
        new Fish(18, 60, ItemId.RAW_COD, ItemId.COD, null),
        new Fish(10, 60, ItemId.RAW_MACKEREL, ItemId.MACKEREL, null)),
    CASKET(
        16,
        "Caskets",
        ItemId.BIG_NET,
        SceneryId.FISH_NET_HARPOON,
        false,
        new Fish(-1, 70, ItemId.CASKET, null, null),
        new Fish(-1, 80, ItemId.SEAWEED, null, null),
        new Fish(-1, 140, ItemId.OYSTER, null, null),
        new Fish(-1, 120, ItemId.LEATHER_GLOVES, null, null),
        new Fish(-1, 120, ItemId.BOOTS, null, null));
    final int requiredLevel;
    final String name;
    final ItemId tool;
    final SceneryId fishingSpot;
    final boolean usesObjectCommand2;
    final Fish[] fish;
    private ItemId[] rawCache;
    private ItemId[] cookedCache;

    FishingTarget(
        int fishingLevel,
        String name,
        ItemId tool,
        SceneryId fishingSpot,
        boolean atObject2,
        Fish... fish) {
      this.requiredLevel = fishingLevel;
      this.name = name;
      this.tool = tool;
      this.fishingSpot = fishingSpot;
      this.usesObjectCommand2 = atObject2;
      this.fish = fish;
    }

    private ItemId[] buildCache(Function<Fish, ItemId> mapper) {
      return Arrays.stream(fish).map(mapper).filter(Objects::nonNull).toArray(ItemId[]::new);
    }

    public ItemId[] getRawIds() {
      if (rawCache == null) rawCache = buildCache(f -> f.raw);
      return rawCache;
    }

    public ItemId[] getCookedIds() {
      if (cookedCache == null) cookedCache = buildCache(f -> f.cooked);
      return cookedCache;
    }

    public static FishingTarget fromFlag(char c) {
      switch (Character.toLowerCase(c)) {
        case 's':
          return SHARK;
        case 'h':
          return SWORDFISH_TUNA;
        case 'l':
          return LOBSTER;
        case 'n':
          return BASS_COD_MACKEREL;
        case 'k':
          return CASKET;
        default:
          return null;
      }
    }

    static class Fish {
      final int cookingLevel;
      final int spriteScale;
      final ItemId raw, cooked;
      final CerterInfo certerInfo;

      Fish(int cookingLevel, int spriteScale, ItemId raw, ItemId cooked, CerterInfo certerInfo) {
        this.cookingLevel = cookingLevel;
        this.spriteScale = spriteScale;
        this.raw = raw;
        this.cooked = cooked;
        this.certerInfo = certerInfo;
      }
    }

    static class CerterInfo {

      final int certerId, chatIndex;

      CerterInfo(int certerId, int chatIndex) {
        this.certerId = certerId;
        this.chatIndex = chatIndex;
      }
    }
  }
}
