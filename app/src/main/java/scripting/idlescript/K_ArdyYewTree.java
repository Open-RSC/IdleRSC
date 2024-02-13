package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;

/**
 * <b>Ardy Yew Trees</b>
 *
 * <p>Cuts yew logs in NE ardy, including the far western one, banks in ardy south bank. <br>
 * Requires 53+ Combat to avoid aggressive bears! <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
/*
 * todo:
 *   logic to cut same tree as other players.
 */
public final class K_ArdyYewTree extends K_kailaScript {
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
    c.displayMessage("@red@ArdyYewTrees, start with an axe in inv/equipment");
    if (c.isInBank()) c.closeBank();
    if (c.currentY() < 620
        && c.currentY() > 600
        && c.currentX() > 543
        && c.currentX() < 555) { // inside bank
      bank();
      bankToYews();
      c.sleep(1380);
    }
    if (c.currentY() < 600 && c.currentY() > 587 && c.currentX() > 525 && c.currentX() < 543) {
      c.walkTo(533, 596);
      c.walkTo(548, 600);
      bank();
      bankToYews();
      c.sleep(1380);
    }
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
    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.displayMessage("Got Autostart, Cutting Yews", 0);
        System.out.println("Got Autostart, Cutting Yews");
        scriptStarted = true;
        guiSetup = true;
      }
    }
    if (!guiSetup) {
      guiSetup = true;
      setupGUI();
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
        c.setStatus("@gre@Cutting Yews..");
        if (c.getObjectAtCoord(509, 571) == 309) {
          cutFirstTree();
        }
        if (c.getObjectAtCoord(509, 571) == 309) {
          cutFirstTree();
        }
        if (c.getObjectAtCoord(509, 571) == 309) {
          cutFirstTree();
        }
        if (c.getObjectAtCoord(507, 567) == 309) {
          cutSecondTree();
        }
        if (c.getObjectAtCoord(507, 567) == 309) {
          cutSecondTree();
        }
        if (c.getObjectAtCoord(507, 567) == 309) {
          cutSecondTree();
        }
        mainYewToAltYew();
        if (c.getObjectAtCoord(513, 525) == 309) {
          cutThirdTree();
        }
        if (c.getObjectAtCoord(513, 525) == 309) {
          cutThirdTree();
        }
        if (c.getObjectAtCoord(513, 525) == 309) {
          cutThirdTree();
        }
        c.walkTo(505, 533);
        altYewToMainYew();
      } else {
        goToBank();
      }
    }
  }

  private void cutFirstTree() {
    c.walkTo(510, 570);
    c.atObject(509, 571);
    c.sleep(2000);
    c.waitForBatching(true);
    if (c.getInventoryItemCount() > 29) {
      goToBank();
    }
  }

  private void cutSecondTree() {
    c.walkTo(509, 568);
    c.atObject(507, 567);
    c.sleep(2000);
    c.waitForBatching(true);
    if (c.getInventoryItemCount() > 29) {
      goToBank();
    }
  }

  private void cutThirdTree() {
    c.walkTo(512, 526);
    c.atObject(513, 525);
    c.sleep(2000);
    c.waitForBatching(true);
    if (c.getInventoryItemCount() > 29) {
      altYewToMainYew();
      c.walkTo(511, 571);
      bankToYews();
    }
  }

  private void mainYewToAltYew() {
    //  c.walkTo(511,559);
    c.walkTo(507, 553);
    c.walkTo(505, 541);
  }

  private void altYewToMainYew() {
    c.walkTo(505, 541);
    c.walkTo(507, 553);
  }

  private void yewToBank() {
    c.walkTo(512, 571);
    c.walkTo(512, 577);
    c.walkTo(521, 588);
    c.walkTo(534, 595);
    c.walkTo(547, 602);
    c.walkTo(550, 612);
  }

  private void bankToYews() {
    c.walkTo(550, 612);
    c.walkTo(547, 602);
    c.walkTo(534, 595);
    c.walkTo(521, 588);
    c.walkTo(512, 577);
    c.walkTo(512, 571);
  }

  private void goToBank() {
    c.setStatus("@gre@Walking to Bank..");
    yewToBank();
    c.setStatus("@gre@Done Walking to Bank..");
    c.walkTo(551, 613);
    totalTrips = totalTrips + 1;
    bank();
    c.setStatus("@gre@Going to Yews..");
    bankToYews();
    c.setStatus("@gre@Done Walking to Yews..");
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalLog = totalLog + c.getInventoryItemCount(635);
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
      logInBank = c.getBankItemCount(635);
      c.closeBank();
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Ardy Yew Logs by Kaila");
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
      int y = 21;
      c.drawString("@red@Ardy Yew Logs @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
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
