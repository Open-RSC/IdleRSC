package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Cuts yew logs in NE ardy, including the far western one, banks in seers.
 *
 * <p>todo: reduce walking between locations - pause at each side logic to cut same tree as other
 * players
 *
 * <p>Author - Kaila
 */
public final class K_GnomeMagicTree extends K_kailaScript {

  public void startSequence() {
    c.displayMessage("@red@GnomeMagicTree,  start with an axe in inv/equipment");
    if (c.isInBank()) {
      c.closeBank();
    }
    if (c.currentY() > 1000) {
      bank();
      c.walkTo(714, 1459);
      c.atObject(714, 1460);
      c.walkTo(722, 507);
      c.sleep(1380);
    }
    if (!c.isAuthentic() && !orsc.Config.C_BATCH_PROGRESS_BAR) c.toggleBatchBars();
  }

  public int start(String[] parameters) {
    checkBatchBars();
    if (parameters.length > 0 && !parameters[0].equals("")) {
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
      if (c.getInventoryItemCount() < 30) {

        if (c.getObjectAtCoord(718, 520) == 310) {
          cutFirstTree();
        }
        if (c.getObjectAtCoord(718, 520) == 310) {
          cutFirstTree();
        }
        if (c.getObjectAtCoord(718, 520) == 310) {
          cutFirstTree();
        }
        c.walkTo(722, 507);
        if (c.getObjectAtCoord(734, 506) == 310) {
          cutSecondTree();
        }
        if (c.getObjectAtCoord(734, 506) == 310) {
          cutSecondTree();
        }
        if (c.getObjectAtCoord(734, 506) == 310) {
          cutSecondTree();
        }
        c.walkTo(722, 507);
        if (c.getObjectAtCoord(718, 493) == 310) {
          cutThirdTree();
        }
        if (c.getObjectAtCoord(718, 493) == 310) {
          cutThirdTree();
        }
        if (c.getObjectAtCoord(718, 493) == 310) {
          cutThirdTree();
        }
        c.walkTo(722, 507);
        if (c.getObjectAtCoord(718, 493) == 310) {
          cutFourthTree();
        }
        if (c.getObjectAtCoord(718, 493) == 310) {
          cutFourthTree();
        }
        if (c.getObjectAtCoord(718, 493) == 310) {
          cutFourthTree();
        }
        c.walkTo(695, 521); // error walking here
        if (c.getObjectAtCoord(678, 518) == 310) {
          cutFifthTree();
        }
        if (c.getObjectAtCoord(678, 518) == 310) {
          cutFifthTree();
        }
        if (c.getObjectAtCoord(678, 518) == 310) {
          cutFifthTree();
        }
        c.walkTo(696, 521);
        c.walkTo(710, 519);
        c.walkTo(722, 507);

      } else {
        goToBank();
      }
    }
  }

  public void cutFirstTree() {
    c.walkTo(718, 519);
    c.atObject(718, 520);
    c.sleep(2000);
    while (c.isBatching() && c.getInventoryItemCount() < 30) {
      c.sleep(1000);
    }
    if (c.getInventoryItemCount() > 29) {
      goToBank();
    }
  }

  public void cutSecondTree() {
    c.walkTo(733, 506);
    c.atObject(734, 506);
    c.sleep(2000);
    while (c.isBatching() && c.getInventoryItemCount() < 30) {
      c.sleep(1000);
    }
    if (c.getInventoryItemCount() > 29) {
      goToBank();
    }
  }

  public void cutThirdTree() {
    c.walkTo(718, 494);
    c.atObject(718, 493);
    c.sleep(2000);
    while (c.isBatching() && c.getInventoryItemCount() < 30) {
      c.sleep(1000);
    }
    if (c.getInventoryItemCount() > 29) {
      goToBank();
    }
  }

  public void cutFourthTree() {
    c.walkTo(718, 494);
    c.atObject(718, 493);
    c.sleep(2000);
    while (c.isBatching() && c.getInventoryItemCount() < 30) {
      c.sleep(1000);
    }
    if (c.getInventoryItemCount() > 29) {
      goToBank();
    }
  }

  public void cutFifthTree() {
    c.walkTo(679, 518);
    c.atObject(678, 518);
    c.sleep(2000);
    while (c.isBatching() && c.getInventoryItemCount() < 30) {
      c.sleep(1000);
    }
    if (c.getInventoryItemCount() > 29) {
      c.walkTo(696, 521);
      c.walkTo(710, 519);
      goToBank();
    }
  }

  public void goToBank() {
    c.walkTo(715, 516);
    if (c.currentY() < 1000) { // added to fix index out of bounds
      c.atObject(714, 516); // 714 out of bounds
      c.sleep(1000);
    }
    c.sleep(100);
    c.walkTo(714, 1454);
    totalTrips = totalTrips + 1;
    bank();
    c.walkTo(714, 1459);
    c.atObject(714, 1460);
    c.walkTo(722, 507);
  }

  public void bank() {

    c.setStatus("@blu@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {

      totalLog = totalLog + c.getInventoryItemCount(636);

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != 1263
            && itemId != axeId[0]
            && itemId != axeId[1]
            && itemId != axeId[2]
            && itemId != axeId[3]
            && itemId != axeId[4]
            && itemId != axeId[5]) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }

      logInBank = c.getBankItemCount(636);
      c.closeBank();
      c.sleep(1000);
    }
  }
  // GUI stuff below (icky)
  public void setupGUI() {
    JLabel header = new JLabel("Gnome Magic Logs by Kaila");
    JLabel label1 = new JLabel("Start in Gnome Stronghold west of bank, near trees!");
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
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      int tripSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (totalLog * scale);
        tripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 24;
      c.drawString("@red@Gnome Magic Logs @gre@by Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@__________________", x, y, 0xFFFFFF, 1);
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
      c.drawString("@whi@__________________", x, y + 3 + (14 * 4), 0xFFFFFF, 1);
    }
  }
}
