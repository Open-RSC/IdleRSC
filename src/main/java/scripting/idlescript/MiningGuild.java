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

  private static final int RUNITE_ORE = ItemId.RUNITE_ORE.getId();
  private static final int ADAMANTITE_ORE = ItemId.ADAMANTITE_ORE.getId();
  private static final int MITHRIL_ORE = ItemId.MITHRIL_ORE.getId();
  private static final int GOLD_ORE = ItemId.GOLD.getId();
  private static final int COAL_ORE = ItemId.COAL.getId();

  private static final int[] gemIDs = {157, 158, 159, 160};
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
    while (controller.isRunning()) {
      if (controller.getObjectAtCoord(ladderUp[0], ladderUp[1]) != 5
          && controller.getObjectAtCoord(ladderDown[0], ladderDown[1]) != 223) {
        quit(2);
      } else {
        while (controller.getObjectAtCoord(ladderDown[0], ladderDown[1]) == 223) {
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
        if (ore != null) isMining = "runite";
        break;
      case "adamantite":
        ore = controller.getNearestObjectById(ADAMANTITE_ROCK);
        if (ore != null && ore[1] > 3383) isMining = "adamantite";
        break;
      case "mithril":
        ore = controller.getNearestObjectById(MITHRIL_ROCK);
        if (ore != null && ore[1] > 3383) isMining = "mithril";
        break;
      case "gold":
        ore = controller.getNearestObjectById(GOLD_ROCK);
        if (ore != null && ore[1] > 3383) isMining = "gold";
        break;
      case "coal":
        ore = controller.getNearestObjectByIds(COAL_ROCK);
        if (ore != null && ore[1] > 3383) {
          isMining = "coal";
        }
        break;
      default:
        controller.sleep(640);
        isMining = "none";
        break;
    }
    if (ore.length > 0) {
      controller.atObject(ore[0], ore[1]);
      currentOre = new int[] {0, 0};
    }
    controller.sleep(1920);
  }

  public void bank() {
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
      int[] oreIDs = {RUNITE_ORE, ADAMANTITE_ORE, MITHRIL_ORE, GOLD_ORE, COAL_ORE};
      for (int i = 0; i < oreIDs.length; i++) { // deposits all ores
        if (controller.getInventoryItemCount(oreIDs[i]) > 0) {
          banked[i] += controller.getInventoryItemCount(oreIDs[i]); // adds ore to array for paint
          while (controller.getInventoryItemCount(oreIDs[i]) > 0) {
            controller.depositItem(oreIDs[i], controller.getInventoryItemCount(oreIDs[i]));
            controller.sleep(640);
          }
        }
      }
      for (Integer gemID : gemIDs) { // deposits all gems
        if (controller.getInventoryItemCount(gemID) > 0) {
          while (controller.getInventoryItemCount(gemID) > 0) {
            controller.depositItem(gemID, controller.getInventoryItemCount(gemID));
            controller.sleep(640);
          }
        }
      }
      controller.closeBank();
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
    if (i == 1) {
      controller.displayMessage("@red@Script has been stopped!");
    } else if (i == 2) {
      controller.displayMessage(
          "@red@Start the script inside the mining guild or the Falador east bank!");
    } else if (i == 3) {
      controller.displayMessage("@red@Are you planning to mine nothing?");
    }
    if (controller.isRunning()) controller.stop();
  }

  public boolean runiteAvailable() {
    return controller.getNearestObjectById(RUNITE_ROCK) != null;
  }

  public boolean adamantiteAvailable() {
    int[] ore = controller.getNearestObjectById(ADAMANTITE_ROCK);
    return ore != null && ore[1] > 3383;
  }

  public boolean mithrilAvailable() {
    int[] ore = controller.getNearestObjectById(MITHRIL_ROCK);
    return ore != null && ore[1] > 3383;
  }

  public boolean goldAvailable() {
    int[] ore = controller.getNearestObjectById(GOLD_ROCK);
    return ore != null && ore[1] > 3383;
  }

  public boolean coalAvailable() {
    int[] ore = controller.getNearestObjectByIds(COAL_ROCK);
    return ore != null && ore[1] > 3383;
  }

  public boolean rockEmpty() {
    return currentOre[0] != 0
        ? controller.getObjectAtCoord(currentOre[0], currentOre[1]) == EMPTY_ROCK
        : false;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      controller.drawBoxAlpha(7, 7, 118, 21 + 90, 0xFFFFFF, 64);
      controller.drawString("@whi@________________", 10, 7, 0xFFFFFF, 1);
      controller.drawString("@gre@MiningGuild @whi@- @cya@Seatta", 10, 21, 0xFFFFFF, 1);
      controller.drawString("@whi@________________", 10, 21 + 3, 0xFFFFFF, 1);
      controller.drawString("@whi@      Ores Banked", 10, 21 + 19, 0xFFFFFF, 1);
      controller.drawString("@whi@________________", 10, 21 + 23, 0xFFFFFF, 1);
      controller.drawString("@cya@Runite ", 10, 21 + 38, 0xFFFFFF, 1);
      controller.drawString("@gre@Adamantite ", 10, 21 + 52, 0xFFFFFF, 1);
      controller.drawString("@blu@Mithril ", 10, 21 + 66, 0xFFFFFF, 1);
      controller.drawString("@yel@Gold ", 10, 21 + 80, 0xFFFFFF, 1);
      controller.drawString("@bla@Coal ", 10, 21 + 94, 0xFFFFFF, 1);
      controller.drawLineVert(77, 21 + 24, 72, 0xFFFFFF);
      controller.drawString("@whi@" + banked[0], 81, 21 + 38, 0xFFFFFF, 1);
      controller.drawString("@whi@" + banked[1], 81, 21 + 52, 0xFFFFFF, 1);
      controller.drawString("@whi@" + banked[2], 81, 21 + 66, 0xFFFFFF, 1);
      controller.drawString("@whi@" + banked[3], 81, 21 + 80, 0xFFFFFF, 1);
      controller.drawString("@whi@" + banked[4], 81, 21 + 94, 0xFFFFFF, 1);
    }
  }
}
