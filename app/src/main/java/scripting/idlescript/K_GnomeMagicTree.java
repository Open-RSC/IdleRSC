package scripting.idlescript;

import bot.Main;
import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;

/**
 * <b>Gnome Magic Trees</b>
 *
 * <p>Cuts Magic Trees in Gnome Tree, including the far eastern one. <br>
 * Banks the logs at upstairs flax bank<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_GnomeMagicTree extends K_kailaScript {

  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.WOODCUTTING, Category.IRONMAN_SUPPORTED, Category.URANIUM_SUPPORTED
          },
          "Kaila",
          "Cuts Magic trees in the Tree Gnome Stronghold.");

  private static final int[] axeId = {
    ItemId.BRONZE_AXE.getId(),
    ItemId.IRON_AXE.getId(),
    ItemId.STEEL_AXE.getId(),
    ItemId.BLACK_AXE.getId(),
    ItemId.MITHRIL_AXE.getId(),
    ItemId.ADAMANTITE_AXE.getId(),
    ItemId.RUNE_AXE.getId()
  };

  private long fail = 0;
  private long success = 0;
  private boolean doAction = true;
  private long didActionTime = 0;

  public void startSequence() {
    boolean noAxe = true;
    boolean noBag = true;
    c.displayMessage("@red@GnomeMagicTree,  start with an axe in inv/equipment");
    if (c.isInBank()) c.closeBank();
    for (int axe : axeId) {
      if (c.getInventoryItemCount(axe) > 0) noAxe = false;
    }
    if (!c.isAuthentic() || c.getInventoryItemCount(ItemId.SLEEPING_BAG.getId()) > 0) noBag = false;
    if (noAxe || noBag) {
      c.log(
          "START ITEMS MISSING: start with an axe in inventory/equip and sleeping bag if uranium",
          "red");
      c.stop();
    }
    if (c.currentY() > 1000) {
      bank();
      c.walkTo(714, 1459);
      c.atObject(714, 1460);
      c.walkTo(722, 507);
      c.sleep(1380);
    }
    c.setBatchBarsOn();
  }
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    c.setBatchBarsOn();
    if (parameters.length > 0 && !parameters[0].isEmpty()) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.displayMessage("Got Autostart, Cutting Magics", 0);
        System.out.println("Got Autostart, Cutting Magics");
        scriptStarted = true;
        guiSetup = true;
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      startSequence();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  public void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount() < 30
          && c.isRunning()
          && (c.getBatchBarsOn()
              || doAction
              || (didActionTime + 4000L < System.currentTimeMillis()))) {
        // if there is an active tree, cut it
        if (c.getObjectAtCoord(718, 520) == 310) cutTree(718, 519, 718, 520);
        else if (c.getObjectAtCoord(734, 506) == 310) cutTree(733, 506, 734, 506);
        else if (c.getObjectAtCoord(718, 493) == 310) cutTree(718, 494, 718, 493);
        else if (c.getObjectAtCoord(678, 518) == 310) cutTree(679, 518, 678, 518);
        // We dont see valid trees, check from where you can see first 3 trees
        else if (c.currentX() != 722 || c.currentY() != 507) {
          c.setStatus("@yel@Checking first 3 trees");
          c.walkTo(722, 507);
          if (c.getObjectAtCoord(718, 520) != 310
              && c.getObjectAtCoord(734, 506) != 310
              && c.getObjectAtCoord(718, 493) != 310) {
            // first 3 are down, check the east tree
            if (c.currentX() != 695 || c.currentY() != 516) {
              // check 4th tree spot
              c.setStatus("@yel@Checking the 4th tree");
              c.walkTo(712, 512);
              c.walkTo(704, 515);
              c.walkTo(694, 515);
            }
            // east tree is up, cut it

            if (c.getObjectAtCoord(678, 518) == 310) cutTree(679, 518, 678, 518);
            else { // east tree is down, walk back
              c.setStatus("@yel@Tree 4 is down, walking back");
              if (c.currentX() < 694) c.walkTo(691, 518);
              c.walkTo(694, 515);
              c.walkTo(704, 515);
              c.walkTo(712, 512);
            }
          }
        }
        // check for bank
      } else if (c.getInventoryItemCount() == 30) {
        if (c.currentX() < 706) {
          c.setStatus("@yel@Walking to bank from the east");
          c.walkTo(695, 516);
          c.walkTo(706, 516);
        }
        c.setStatus("@yel@Walking to bank");
        goToBank();
      }
      c.sleep(640); // sleep once each loop
    }
  }

  private void cutTree(int walkX, int walkY, int objX, int objY) {
    if (c.currentX() != walkX || c.currentY() != walkY) c.walkTo(walkX, walkY);
    c.atObject(objX, objY);
    didActionTime = System.currentTimeMillis();
    doAction = false;
    c.waitForBatching(true);
  }

  public void goToBank() {
    c.walkTo(715, 516);
    if (c.currentY() < 1000) { // Go up the stairs
      c.atObject(714, 516);
      c.sleep(1000);
    }
    c.walkTo(714, 1454);
    totalTrips = totalTrips + 1;
    bank();
    c.walkTo(714, 1459);
    c.atObject(714, 1460);
    c.sleep(1000);
    c.walkTo(722, 507);
  }

  public void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalLog = totalLog + c.getInventoryItemCount(636);
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != ItemId.SLEEPING_BAG.getId()
            && itemId != axeId[0]
            && itemId != axeId[1]
            && itemId != axeId[2]
            && itemId != axeId[3]
            && itemId != axeId[4]
            && itemId != axeId[5]
            && itemId != axeId[6]) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1280);
      logInBank = c.getBankItemCount(636);
      for (int axe : axeId) {
        withdrawItem(axe, 1);
        if (c.getInventoryItemCount(axe) > 0) break;
      }
      if (c.isAuthentic()) withdrawItem(ItemId.SLEEPING_BAG.getId(), 1);
      c.closeBank();
    }
  }

  public void setupGUI() {
    JLabel header = new JLabel("Gnome Magic Logs by Kaila");
    JLabel label1 = new JLabel("Start in Gnome Stronghold west of bank, near trees!");
    JLabel label2 = new JLabel("Start with an axe, and sleeping bag if uranium");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.toLowerCase().contains("you slip and fail")) {
      fail++;
      doAction = true;
    } else if (message.toLowerCase().contains("you get some wood")) {
      success++;
      doAction = true;
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      int tripsPerHr = 0;
      int failurePerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (success * scale);
        failurePerHr = (int) (fail * scale);
        tripsPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 24;
      c.drawString("@red@Gnome Magic Logs @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@__________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Logs in Bank: @gre@" + logInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Logs Cut: @gre@"
              + success
              + "@yel@ (@whi@"
              + String.format("%,d", successPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Failure to Cut: @gre@"
              + fail
              + "@yel@ (@whi@"
              + failurePerHr
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", tripsPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 5), 0xFFFFFF, 1);
      c.drawString("@whi@__________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
    }
  }
}
