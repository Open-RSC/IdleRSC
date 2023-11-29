package scripting.idlescript;

import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import models.entities.ItemId;
import models.entities.SkillId;

public class MiningGuild extends IdleScript {
  // Mining Guild Script by Seatta

  private static final JCheckBox runiteCheck = new JCheckBox("Mine Runite", true);
  private static final JCheckBox adamantiteCheck = new JCheckBox("Mine Adamantite", true);
  private static final JCheckBox mithrilCheck = new JCheckBox("Mine Mithril", true);
  private static final JCheckBox goldCheck = new JCheckBox("Mine Gold", true);
  private static final JCheckBox coalCheck = new JCheckBox("Mine Coal", true);

  private static final int RUNITE_ROCK = 210;
  private static final int ADAMANTITE_ROCK = 109;
  private static final int MITHRIL_ROCK = 107;
  private static final int GOLD_ROCK = 113;
  private static final int[] COAL_ROCK = {110, 111};
  private static final int EMPTY_ROCK = 98;

  private static final int[] oreIds = {
    ItemId.RUNITE_ORE.getId(),
    ItemId.ADAMANTITE_ORE.getId(),
    ItemId.MITHRIL_ORE.getId(),
    ItemId.COAL.getId(),
    ItemId.GOLD.getId()
  };
  private static final int[] gemIds = {
    ItemId.UNCUT_DIAMOND.getId(),
    ItemId.UNCUT_RUBY.getId(),
    ItemId.UNCUT_EMERALD.getId(),
    ItemId.UNCUT_SAPPHIRE.getId()
  };
  private static final int[] pickaxeIds = {
    ItemId.BRONZE_PICKAXE.getId(),
    ItemId.IRON_PICKAXE.getId(),
    ItemId.STEEL_PICKAXE.getId(),
    ItemId.MITHRIL_PICKAXE.getId(),
    ItemId.ADAMANTITE_PICKAXE.getId(),
    ItemId.RUNE_PICKAXE.getId()
  };

  private static final int[] banked = {0, 0, 0, 0, 0};
  private static int[] currentOre = {0, 0};

  private static final int[] ladderUp = {274, 3398};
  private static final int[] ladderDown = {274, 566};
  private static JFrame scriptFrame = null;

  private static boolean mineRunite, mineAdamantite, mineMithril, mineGold, mineCoal = false;
  private static boolean guiSetup, setupCompleted = false;
  private static String isMining = "none";
  private static int miningLevel;

  public int start(String[] param) {
    if (!guiSetup) {
      setup();
      guiSetup = true;
    }
    if (setupCompleted) {
      guiSetup = false;
      setupCompleted = false;
      if (!runiteCheck.isSelected()
          && !adamantiteCheck.isSelected()
          && !mithrilCheck.isSelected()
          && !goldCheck.isSelected()
          && !coalCheck.isSelected()) {
        quit(3); // You can't mine nothing!
      }
      mineRunite = runiteCheck.isSelected();
      mineAdamantite = adamantiteCheck.isSelected();
      mineMithril = mithrilCheck.isSelected();
      mineGold = goldCheck.isSelected();
      mineCoal = coalCheck.isSelected();
      run();
    }
    return 1000; // start() must return a int value now.
  }

  public void run() {
    if (controller.getBaseStat(SkillId.MINING.getId()) < 60) quit(4);

    for (int id : pickaxeIds) {
      if (controller.isItemIdEquipped(id) || controller.getInventoryItemCount(id) > 0) {
        String pickaxe = String.valueOf(ItemId.getById(id)).replace("_", " ").toLowerCase();
        pickaxe = pickaxe.substring(0, 1).toUpperCase() + pickaxe.substring(1);
        controller.displayMessage("@gre@Using: " + pickaxe);
        break;
      }
      if (id == pickaxeIds[pickaxeIds.length - 1]) {
        quit(5);
      }
    }

    while (controller.isRunning()) {
      if (controller.getObjectAtCoord(ladderUp[0], ladderUp[1]) != 5
          && controller.getObjectAtCoord(ladderDown[0], ladderDown[1]) != 223) {
        quit(2);
      } else {
        while (controller.getObjectAtCoord(ladderDown[0], ladderDown[1]) == 223
            && controller.isRunning()) {
          controller.atObject(ladderDown[0], ladderDown[1]);
          controller.sleep(640);
        }
      }
      miningLevel = controller.getBaseStat(SkillId.MINING.getId());
      if (controller.getInventoryItemCount() == 30) {
        bank();
      } else {
        if (rockEmpty() || !controller.isBatching()) {
          isMining = "none";
          currentOre = new int[] {0, 0};
        }
        if (controller.isBatching()) {
          if (Objects.equals(isMining, "runite")) {
            while (controller.isBatching() && runiteAvailable() && controller.isLoggedIn()) {
              controller.sleep(640);
            }
          }
          if (miningLevel >= 85 && mineRunite && runiteAvailable()) {
            mine("runite");
          }
          if (Objects.equals(isMining, "mithril")) {
            if (miningLevel >= 70 && mineAdamantite && adamantiteAvailable()) {
              mine("adamantite");
            }
          }
          if (Objects.equals(isMining, "coal")) {
            if (miningLevel >= 70 && mineAdamantite && adamantiteAvailable()) {
              mine("adamantite");
            } else if (mineMithril && mithrilAvailable()) {
              mine("mithril");
            } else if (mineGold && goldAvailable()) {
              mine("gold");
            }
          }
          controller.sleep(1280);
        }
        if (!controller.isBatching() && Objects.equals(isMining, "none") && rockEmpty()) {
          if (miningLevel >= 85 && mineRunite && runiteAvailable()) {
            mine("runite");
          } else if (miningLevel >= 70 && mineAdamantite && adamantiteAvailable()) {
            mine("adamantite");
          } else if (miningLevel >= 55 && mineMithril && mithrilAvailable()) {
            mine("mithril");
          } else if (miningLevel >= 40 && mineGold && goldAvailable()) {
            mine("gold");
          } else if (miningLevel >= 30 && mineCoal && coalAvailable()) {
            mine("coal");
          }
          controller.sleep(1280);
        }
      }
    }
    quit(1);
  }

  public void mine(String i) {
    int[] ore = {};
    switch (i) {
      case "runite":
        ore = controller.getNearestObjectById(RUNITE_ROCK);
        if (ore != null) {
          isMining = "runite";
          controller.setStatus("@cya@Mining @cya@Runite");
        }
        break;
      case "adamantite":
        ore = controller.getNearestObjectById(ADAMANTITE_ROCK);
        if (ore != null) {
          isMining = "adamantite";
          controller.setStatus("@cya@Mining @gre@Adamantite");
        }
        break;
      case "mithril":
        ore = controller.getNearestObjectById(MITHRIL_ROCK);
        if (ore != null) {
          isMining = "mithril";
          controller.setStatus("@cya@Mining @blu@Mithril");
        }
        break;
      case "gold":
        ore = controller.getNearestObjectById(GOLD_ROCK);
        if (ore != null) {
          isMining = "gold";
          controller.setStatus("@cya@Mining @yel@Gold");
        }
        break;
      case "coal":
        ore = controller.getNearestObjectByIds(COAL_ROCK);
        if (ore != null) {
          isMining = "coal";
          controller.setStatus("@cya@Mining @whi@Coal");
        }
        break;
      default:
        controller.sleep(640);
        controller.setStatus("@cya@Waiting");
        isMining = "none";
    }
    if (ore.length > 0) {
      controller.atObject(ore[0], ore[1]);
      currentOre = new int[] {ore[0], ore[1]};
    }
    controller.sleep(1920);
  }

  public void bank() {
    controller.setStatus("@cya@Banking");
    isMining = "none";
    currentOre = new int[] {0, 0};
    while (controller.getObjectAtCoord(ladderDown[0], ladderDown[1]) != 223
        && controller.isRunning()
        && controller.isLoggedIn()) { // sleep until the mining guild has been exited

      controller.atObject(ladderUp[0], ladderUp[1]); // attempt to ascend ladder to falador
      controller.sleep(1280);
    }
    if (!controller.isRunning()) {
      quit(1);
    }
    controller.openBank();
    while (!controller.isInBank()
        && controller.isRunning()
        && controller.isLoggedIn()) { // sleep until the bank has been opened
      controller.sleep(1280);
    }
    if (!controller.isRunning()) {
      quit(1);
    }
    if (controller.isInBank() && controller.isRunning()) {
      for (int i = 0; i < oreIds.length; i++) { // deposits all ores
        if (controller.getInventoryItemCount(oreIds[i]) > 0) {
          banked[i] +=
              controller.getInventoryItemCount(oreIds[i]); // adds ore to banked array for paint
          while (controller.getInventoryItemCount(oreIds[i]) > 0) {
            controller.depositItem(oreIds[i], controller.getInventoryItemCount(oreIds[i]));
            controller.sleep(640);
          }
        }
      }
      for (Integer gemID : gemIds) { // deposits all gems
        if (controller.getInventoryItemCount(gemID) > 0) {
          while (controller.getInventoryItemCount(gemID) > 0) {
            controller.depositItem(gemID, controller.getInventoryItemCount(gemID));
            controller.sleep(640);
          }
        }
      }
      controller.closeBank();
      controller.setStatus("@cya@Walking to Mining Guild");
      while (controller.getObjectAtCoord(ladderUp[0], ladderUp[1]) != 5
          && controller.isRunning()
          && controller.isLoggedIn()) { // sleep until the mining guild has been entered

        controller.atObject(
            ladderDown[0], ladderDown[1]); // attempt to descend ladder to mining guild
        controller.sleep(1280);
      }
      if (!controller.isRunning()) {
        quit(1);
      }
    }
  }

  public void setup() {
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          setupCompleted = true;
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(runiteCheck);
    scriptFrame.add(adamantiteCheck);
    scriptFrame.add(mithrilCheck);
    scriptFrame.add(goldCheck);
    scriptFrame.add(coalCheck);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  public void quit(Integer i) {
    if (controller.isRunning()) {
      switch (i) {
        case 1:
          controller.displayMessage("@red@Script has been stopped!");
          break;
        case 2:
          controller.displayMessage(
              "@red@Start the script inside the mining guild or the Falador east bank!");
          break;
        case 3:
          controller.displayMessage("@red@Are you planning to mine nothing?");
          break;
        case 4:
          controller.displayMessage(
              "@red@You need a mining level of 60 to enter the mining guild!");
          break;
        case 5:
          controller.displayMessage(
              "@red@You do not have a pickaxe equipped or in your inventory!");
          controller.displayMessage("@red@Get one and start the script again!");
          break;
        default:
          controller.displayMessage("@red@Quit was called but wasn't given a correct arguement");
      }
      controller.stop();
    }
  }

  public boolean runiteAvailable() {
    return controller.getNearestObjectById(RUNITE_ROCK) != null;
  }

  public boolean adamantiteAvailable() {
    int[] ore = controller.getNearestObjectById(ADAMANTITE_ROCK);
    return controller.getNearestObjectById(ADAMANTITE_ROCK) != null && ore[1] > 3383;
  }

  public boolean mithrilAvailable() {
    int[] ore = controller.getNearestObjectById(MITHRIL_ROCK);
    return controller.getNearestObjectById(MITHRIL_ROCK) != null && ore[1] > 3383;
  }

  public boolean goldAvailable() {
    int[] ore = controller.getNearestObjectById(GOLD_ROCK);
    return controller.getNearestObjectById(GOLD_ROCK) != null && ore[1] > 3383;
  }

  public boolean coalAvailable() {
    int[] ore = controller.getNearestObjectByIds(COAL_ROCK);
    return controller.getNearestObjectByIds(COAL_ROCK) != null && ore[1] > 3383;
  }

  public boolean rockEmpty() {
    return currentOre[0] != 0
        ? controller.getObjectAtCoord(currentOre[0], currentOre[1]) == EMPTY_ROCK
        : true;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      int colors[] = { // runite, adamantite, mithril, gold, coal,
        0x008C8C, 0x718161, 0x617181, 0x6C6C6C, 0xBA9537
      };
      int boxColor = 0x282A36;
      int borderColor = 0xBD93F9;

      int numberOfItems = 5;
      int paintPadding = 4;

      int boxTransparency = 255;

      int titleFontSize = 6;
      int titleWidth = 101;
      int titleYOffset = 15;
      int titleXOffset = 32;

      int itemWidth = 34;
      int itemHeight = 20;
      int itemXOffset = 14;
      int itemYOffset = titleYOffset + paintPadding;
      int itemSpacing = 24;

      int paintWidth =
          (itemWidth * numberOfItems) + (itemSpacing * numberOfItems) + (paintPadding * 2);
      int paintHeight = itemHeight + (paintPadding * 2) + titleXOffset;

      int paintX = controller.getGameWidth() - paintPadding - paintWidth;
      int paintY = controller.getGameHeight() - paintPadding * 2 - paintHeight;
      int titleX = paintX + ((paintWidth - titleWidth) / 2) - paintPadding;
      int titleY = paintY + paintPadding + titleYOffset;
      int itemX = paintX + paintPadding + itemXOffset;
      int itemY = paintY + paintPadding + itemYOffset;
      int itemAmountYOffset = itemY + itemHeight + 6;

      controller.drawBoxAlpha(paintX, paintY, paintWidth, paintHeight, boxColor, boxTransparency);
      controller.drawBoxBorder(paintX, paintY, paintWidth, paintHeight, borderColor);
      controller.drawString("Mining", titleX, titleY, colors[0], titleFontSize);
      controller.drawString("Guild", titleX + 62, titleY, colors[1], titleFontSize);

      for (int i = 0; i < oreIds.length; i++) {
        controller.drawItemSprite(
            oreIds[i],
            itemX + (itemWidth * i) + (itemSpacing * i),
            itemY,
            itemWidth,
            itemHeight,
            false);
      }
      for (int i = 0; i < banked.length; i++) {
        String str =
            banked[i] >= 1000000
                ? String.format("%.2f", (double) banked[i] / 1000000) + "M"
                : banked[i] > 1000
                    ? String.format("%.2f", (double) banked[i] / 1000) + "K"
                    : String.valueOf(banked[i]);

        controller.drawString(
            str,
            paintX + (paintPadding * 2) + (itemWidth * (i)) + (itemSpacing * (i)),
            itemAmountYOffset,
            colors[i],
            3);
      }
    }
  }
}
