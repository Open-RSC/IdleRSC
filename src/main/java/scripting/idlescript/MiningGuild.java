package scripting.idlescript;

import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

public class MiningGuild extends IdleScript {
  // Mining Guild Script by Seatta

  private static final JCheckBox runiteCheck = new JCheckBox("Mine Runite", true);
  private static final JCheckBox adamantiteCheck = new JCheckBox("Mine Adamantite", true);
  private static final JCheckBox mithrilCheck = new JCheckBox("Mine Mithril", true);
  private static final JCheckBox coalCheck = new JCheckBox("Mine Coal", true);

  private static final int[] rockIDs = {210, 109, 107, 110, 98, 111};
  private static final int[] oreIDs = {409, 154, 153, 155};
  private static final int[] gemIDs = {157, 158, 159, 160};
  private static final int[] banked = {0, 0, 0, 0};
  private static final int[] currentOre = {0, 0};
  private static final int[] ladderUp = {274, 3398};
  private static final int[] ladderDown = {274, 566};
  private static JFrame scriptFrame = null;

  private static boolean mineRunite = false;
  private static boolean mineAdamantite = false;
  private static boolean mineMithril = false;
  private static boolean mineCoal = false;
  private static boolean guiSetup = false;
  private static boolean setupCompleted = false;
  private static boolean stopped = false;
  private static boolean debug = false;
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
      String x = param[0].toLowerCase();
      if (!runiteCheck.isSelected()
          && !adamantiteCheck.isSelected()
          && !mithrilCheck.isSelected()
          && !coalCheck.isSelected()) {
        quit(3); // You can't mine nothing!
      }
      mineRunite = runiteCheck.isSelected();
      mineAdamantite = adamantiteCheck.isSelected();
      mineMithril = mithrilCheck.isSelected();
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
      miningLevel = controller.getBaseStat(controller.getStatId("Mining"));
      if (controller.getInventoryItemCount() == 30) {
        bank();
      } else {
        if (rockEmpty() || !controller.isBatching()) {
          isMining = "none";
          currentOre[0] = 0;
          currentOre[1] = 0;
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
          } else if (miningLevel >= 30 && mineCoal && coalAvailable()) {
            mine("coal");
          }
          controller.sleep(1280);
        }
      }
    }
  }

  public void mine(String i) {
    if (Objects.equals(i, "runite")) {
      int[] oreCoords = controller.getNearestObjectById(rockIDs[0]);
      if (oreCoords != null) {
        isMining = "runite";
        controller.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    } else if (Objects.equals(i, "adamantite")) {
      int[] oreCoords = controller.getNearestObjectById(rockIDs[1]);
      if (oreCoords != null && oreCoords[1] > 3383) {
        isMining = "adamantite";
        controller.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    } else if (Objects.equals(i, "mithril")) {
      int[] oreCoords = controller.getNearestObjectById(rockIDs[2]);
      if (oreCoords != null && oreCoords[1] > 3383) {
        isMining = "mithril";
        controller.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    } else if (Objects.equals(i, "coal")) {
      int[] oreCoords = controller.getNearestObjectByIds(new int[] {rockIDs[3], rockIDs[5]});
      if (oreCoords != null && oreCoords[1] > 3383) {
        isMining = "coal";
        controller.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    }
    controller.sleep(1920);
  }

  public void bank() {
    isMining = "none";
    currentOre[0] = 0;
    currentOre[1] = 0;
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
    stopped = true;
    controller.stop();
  }

  public boolean runiteAvailable() {
    return controller.getNearestObjectById(rockIDs[0]) != null;
  }

  public boolean adamantiteAvailable() {
    int[] oreCoords = controller.getNearestObjectById(rockIDs[1]);
    return controller.getNearestObjectById(rockIDs[1]) != null && oreCoords[1] > 3383;
  }

  public boolean mithrilAvailable() {
    int[] oreCoords = controller.getNearestObjectById(rockIDs[2]);
    return controller.getNearestObjectById(rockIDs[2]) != null && oreCoords[1] > 3383;
  }

  public boolean coalAvailable() {
    int[] oreCoords = controller.getNearestObjectById(rockIDs[3]);
    return controller.getNearestObjectById(rockIDs[3]) != null && oreCoords[1] > 3383;
  }

  public boolean rockEmpty() {
    if (currentOre[0] != 0) {
      return controller.getObjectAtCoord(currentOre[0], currentOre[1]) == rockIDs[4];
    } else {
      return true;
    }
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      controller.drawBoxAlpha(7, 7, 118, 21 + 78, 0xFFFFFF, 64);
      controller.drawString("@whi@________________", 10, 7, 0xFFFFFF, 1);
      controller.drawString("@gre@MiningGuild @whi@- @cya@Seatta", 10, 21, 0xFFFFFF, 1);
      controller.drawString("@whi@________________", 10, 21 + 3, 0xFFFFFF, 1);
      controller.drawString("@whi@      Ores Banked", 10, 21 + 19, 0xFFFFFF, 1);
      controller.drawString("@whi@________________", 10, 21 + 23, 0xFFFFFF, 1);
      controller.drawString("@cya@Runite ", 10, 21 + 38, 0xFFFFFF, 1);
      controller.drawString("@gre@Adamantite ", 10, 21 + 52, 0xFFFFFF, 1);
      controller.drawString("@blu@Mithril ", 10, 21 + 66, 0xFFFFFF, 1);
      controller.drawString("@bla@Coal ", 10, 21 + 80, 0xFFFFFF, 1);
      controller.drawString("@whi@|", 76, 21 + 34, 0xFFFFFF, 1);
      controller.drawString("@whi@|", 76, 21 + 38, 0xFFFFFF, 1);
      controller.drawString("@whi@|", 76, 21 + 45, 0xFFFFFF, 1);
      controller.drawString("@whi@|", 76, 21 + 52, 0xFFFFFF, 1);
      controller.drawString("@whi@|", 76, 21 + 59, 0xFFFFFF, 1);
      controller.drawString("@whi@|", 76, 21 + 66, 0xFFFFFF, 1);
      controller.drawString("@whi@|", 76, 21 + 73, 0xFFFFFF, 1);
      controller.drawString("@whi@|", 76, 21 + 80, 0xFFFFFF, 1);
      controller.drawString("@whi@" + banked[0], 81, 21 + 38, 0xFFFFFF, 1);
      controller.drawString("@whi@" + banked[1], 81, 21 + 52, 0xFFFFFF, 1);
      controller.drawString("@whi@" + banked[2], 81, 21 + 66, 0xFFFFFF, 1);
      controller.drawString("@whi@" + banked[3], 81, 21 + 80, 0xFFFFFF, 1);
    }
  }
}
