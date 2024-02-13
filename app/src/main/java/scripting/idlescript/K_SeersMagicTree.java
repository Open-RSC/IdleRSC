package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;

/**
 * <b>Seers Magic Tree</b>
 *
 * <p>Cuts Magic logs in seers, including the far western one, banks in Seers. <br>
 * Apos compatability fixed
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
/*
 * todo:
 *   reduce walking between locations - pause at each side.
 *   logic to cut same tree as other players.
 */
public final class K_SeersMagicTree extends K_kailaScript {
  private static int logInBank = 0;
  private static int totalLog = 0;
  private static final int[] axeId = {
    ItemId.BRONZE_AXE.getId(),
    ItemId.IRON_AXE.getId(),
    ItemId.STEEL_AXE.getId(),
    ItemId.BLACK_AXE.getId(),
    ItemId.MITHRIL_AXE.getId(),
    ItemId.ADAMANTITE_AXE.getId(),
    ItemId.RUNE_AXE.getId()
  };

  private void startSequence() {
    c.displayMessage("@red@SeersMagicTree, start with an axe in inv/equipment");
    boolean noAxe = true;
    boolean noBag = true;
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
    if (c.isInBank()) c.closeBank();
    if (c.currentY() < 458) {
      bank();
      c.walkTo(500, 454);
      c.walkTo(503, 457);
      c.walkTo(503, 460);
      c.walkTo(506, 463);
      c.walkTo(506, 472);
      c.walkTo(506, 478);
      c.walkTo(516, 488);
      c.sleep(1380);
    }
    if (!c.isAuthentic()) c.setBatchBarsOn();
  }
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
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

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount() < 30) {
        if (c.getObjectAtCoord(519, 494) == 310) cutFirstTree();
        else if (c.getObjectAtCoord(521, 492) == 310) cutSecondTree();
        else if (c.getObjectAtCoord(524, 489) == 310) cutThirdTree();
        else if (c.getObjectAtCoord(548, 484) == 310) cutFourthTree();
        else if (c.currentX() != 531 || c.currentY() != 487)
          c.walkTo(531, 487); // go to center position to check
        c.sleep(GAME_TICK);
      } else {
        if (c.currentX() < 533) goToBank();
        else goToBank2();
      }
    }
  }

  private void cutFirstTree() {
    c.walkTo(519, 493);
    while (c.isRunning()
        && !c.getShouldSleep()
        && c.getObjectAtCoord(519, 494) == 310
        && c.getInventoryItemCount() < 30) {
      c.atObject(519, 494);
      c.sleep(GAME_TICK);
      c.waitForBatching(true);
    }
  }

  private void cutSecondTree() {
    c.walkTo(521, 491);
    while (c.isRunning()
        && !c.getShouldSleep()
        && c.getObjectAtCoord(521, 492) == 310
        && c.getInventoryItemCount() < 30) {
      c.atObject(521, 492);
      c.sleep(GAME_TICK);
      c.waitForBatching(true);
    }
  }

  private void cutThirdTree() {
    c.walkTo(524, 488);
    while (c.isRunning()
        && !c.getShouldSleep()
        && c.getObjectAtCoord(524, 489) == 310
        && c.getInventoryItemCount() < 30) {
      c.atObject(524, 489);
      c.sleep(GAME_TICK);
      c.waitForBatching(true);
    }
  }

  private void cutFourthTree() {
    c.walkTo(538, 486);
    c.walkTo(547, 484);
    while (c.isRunning()
        && !c.getShouldSleep()
        && c.getObjectAtCoord(548, 484) == 310
        && c.getInventoryItemCount() < 30) {
      c.atObject(548, 484);
      c.sleep(GAME_TICK);
      c.waitForBatching(true);
    }
    if (c.getObjectAtCoord(548, 484) != 310) {
      c.walkTo(538, 486);
      c.walkTo(531, 487);
    }
  }

  private void goToBank() {
    c.walkTo(516, 488);
    c.walkTo(506, 478);
    c.walkTo(506, 472);
    c.walkTo(506, 463);
    c.walkTo(503, 460);
    c.walkTo(503, 457);
    c.walkTo(500, 454);
    totalTrips = totalTrips + 1;
    bank();
    c.walkTo(500, 454);
    c.walkTo(503, 457);
    c.walkTo(503, 460);
    c.walkTo(506, 463);
    c.walkTo(506, 472);
    c.walkTo(506, 478);
    c.walkTo(516, 488);
  }

  private void goToBank2() {
    c.walkTo(547, 484);
    c.walkTo(537, 474);
    c.walkTo(531, 468);
    c.walkTo(521, 468);
    c.walkTo(510, 468);
    c.walkTo(504, 462);
    c.walkTo(504, 458);
    c.walkTo(500, 454);
    totalTrips = totalTrips + 1;
    bank();
    c.walkTo(500, 454);
    c.walkTo(503, 457);
    c.walkTo(503, 460);
    c.walkTo(506, 463);
    c.walkTo(506, 472);
    c.walkTo(506, 478);
    c.walkTo(516, 488);
  }

  private void bank() {
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

      logInBank = c.getBankItemCount(636);
      c.closeBank();
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Seers Magic Logs by Kaila");
    JLabel label1 = new JLabel("Start in Seers bank, or near trees!");
    JLabel label2 = new JLabel("Wield or have rune axe in Inv");
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
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      int tripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (totalLog * scale);
        tripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Seers Magic Logs @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Logs in Bank: @gre@" + logInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Logs Cut: @gre@"
              + totalLog
              + "@yel@ (@whi@"
              + String.format("%,d", successPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", tripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 4), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 4), 0xFFFFFF, 1);
    }
  }
}
