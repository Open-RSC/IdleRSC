package scripting.idlescript;

import bot.Main;
import java.awt.*;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.SceneryId;

/**
 * <b>Seers Magic Tree</b>
 *
 * <p>Cuts Magic logs in seers, including the far western one, banks in Seers. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
/*
 * todo:
 *   reduce walking between locations - pause at each side.
 *   logic to cut same tree as other players.
 */
// 1238/39
public final class K_ChristmasPresents extends K_kailaScript {
  private final int FULL_TREE = SceneryId.CHRISTMAS_TREE.getId();
  private final int EMPTY_TREE = SceneryId.DECORATED_TREE.getId();
  private int totalPresents = 0;
  private int presentsInBank = 0;
  //        "Lumbridge Walking",
  //          "Lumbridge Teleport",
  //          "Varrock",
  //          "Falador",
  //          "Seers Village"
  private int location = 0;

  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      c.displayMessage("@mag@Present Picker");
      if (c.isInBank()) c.closeBank();
      c.setBatchBarsOn();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount() < 30) { // do harvesting
        int[] closestTree = c.getNearestObjectById(FULL_TREE);
        if (closestTree != null) { // harvest closest
          c.atObject(closestTree[0], closestTree[1]);
          c.sleep(2000);
          c.waitForBatching(true);
        } else {
          switch (location) { // walk to center
            case 0: // "Lumbridge Walking"
            case 1: // "Lumbridge Teleport"
              if (c.currentX() != 124 && c.currentY() != 659) c.walkTo(124, 659);
              break;
            case 2: // "Varrock"
              if (c.currentX() != 131 && c.currentY() != 501) c.walkTo(131, 501);
              break;
            case 3: // "Falador"
              if (c.currentX() != 315 && c.currentY() != 538) c.walkTo(315, 538);
              break;
            case 4: // "Seers Village"
              if (c.currentX() != 498 && c.currentY() != 456) c.walkTo(498, 456);
              break;
          }
        }
        c.sleep(1280);
      } else { // do banking
        goToBank();
        bank();
        totalTrips++;
        goToSpot();
      }
    }
  }

  private void goToBank() {
    switch (location) { // walk to center
      case 0: // "Lumbridge Walking"
        c.walkTo(114, 656);
        c.walkTo(130, 647);
        c.walkTo(147, 645);
        c.walkTo(167, 633);
        c.walkTo(189, 630);
        c.walkTo(210, 629);
        c.walkTo(219, 633);
        break;
      case 1: // "Lumbridge Teleport"
        teleportFalador();
        c.walkTo(322, 552);
        c.walkTo(330, 552);
        break;
      case 2: // "Varrock"
        c.walkTo(134, 508);
        c.walkTo(138, 509);
        c.walkTo(151, 507);
        break;
      case 3: // "Falador"
        c.walkTo(318, 539);
        c.walkTo(325, 546);
        c.walkTo(326, 552);
        c.walkTo(330, 553);
        break;
      case 4: // "Seers Village"
        c.walkTo(501, 454);
        break;
    }
  }

  private void goToSpot() {
    switch (location) { // walk to center
      case 0: // "Lumbridge Walking"
        c.walkTo(219, 633);
        c.walkTo(210, 629);
        c.walkTo(189, 630);
        c.walkTo(167, 633);
        c.walkTo(147, 645);
        c.walkTo(130, 647);
        c.walkTo(126, 648);
        c.walkTo(115, 651);
        c.walkTo(116, 659);
        c.walkTo(124, 663);
        break;
      case 1: // "Lumbridge Teleport"
        teleportLumbridge();
        c.walkTo(126, 648);
        c.walkTo(115, 651);
        c.walkTo(116, 659);
        c.walkTo(124, 663);
        break;
      case 2: // "Varrock"
        c.walkTo(151, 507);
        c.walkTo(138, 509);
        c.walkTo(134, 508);
        break;
      case 3: // "Falador"
        c.walkTo(326, 552);
        c.walkTo(325, 541);
        c.walkTo(315, 539);
        break;
      case 4: // "Seers Village"
        c.walkTo(498, 456);
        break;
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(GAME_TICK);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalPresents = totalPresents + c.getInventoryItemCount(ItemId.PRESENT.getId());
      for (int itemId : c.getInventoryItemIds()) {
        c.depositItem(itemId, c.getInventoryItemCount(itemId));
      }
      c.sleep(2000); // Important, leave in

      presentsInBank = c.getBankItemCount(ItemId.PRESENT.getId());
      c.closeBank();
    }
  }

  private void setupGUI() {

    String[] locationBoxLabels = {
      "Lumbridge Walking", "Lumbridge Teleport", "Varrock", "Falador", "Seers Village"
    };

    JLabel header = new JLabel("Present picker by Kaila");
    JLabel label1 = new JLabel("Start near christmas trees, during xmas event");
    JLabel locationLabel = new JLabel("Pick harvesting location:\t");
    JComboBox<String> locationBox = new JComboBox<>(locationBoxLabels);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          location = locationBox.getSelectedIndex();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });
    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(locationLabel);
    scriptFrame.add(locationBox);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int prezSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        prezSuccessPerHr = (int) ((totalPresents) * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;

      c.drawString("@gre@Xmas Presents @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Total Presents: @gre@"
              + totalPresents
              + "@yel@ (@whi@"
              + String.format("%,d", prezSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString("@whi@Presents in Bank: @gre@" + presentsInBank, x, y + (14 * 2), 0xFFFFFF, 1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 4), 0xFFFFFF, 1);
    }
  }
}
