package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.SpellId;

/**
 * <b>Tele Wines</b>
 *
 * <p>Tele Grabs Wines in chaos temple. <br>
 * NOT recommended to use on coleslaw, wines can be obtained with harvesting. <br>
 * Not tested on uranium, but should function if sleep added. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_TeleWines extends K_kailaScript {
  private static int WinezInBank = 0;
  private static int totalWinez = 0;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.displayMessage("@cya@Wine Telegrab @whi@~ @mag@Kaila");
      c.displayMessage("@cya@Start in Edge Bank");
      c.displayMessage("@cya@Laws, Air staff required");
      c.displayMessage("@red@Recommend using grape harvester for coleslaw wines!!!!");

      if (c.isInBank()) c.closeBank();
      if (c.currentY() > 450) {
        bank();
        BankToWine();
        c.sleep(1380);
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount() == 30) {
        c.setStatus("@red@Banking..");
        WineToBank();
        bank();
        BankToWine();
        c.sleep(618);
      }
      c.setStatus("@yel@Picking Wines..");
      int[] coords = c.getNearestItemById(501);
      if (coords != null) {
        c.castSpellOnGroundItem(SpellId.TELEKINETIC_GRAB.getId(), 501, 333, 434);
        c.sleep(1500);
        c.walkTo(331, 434);
        c.sleep(100);
        c.walkTo(332, 434);
        c.sleep(100);
      } else {
        c.sleep(1500);
      }
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalWinez = totalWinez + c.getInventoryItemCount(501);
      if (c.getInventoryItemCount(501) > 0) { // deposit the Wines
        c.depositItem(501, c.getInventoryItemCount(501));
        c.sleep(1380);
      }
      WinezInBank = c.getBankItemCount(501);
      if (c.getInventoryItemCount(42) < 30) { // withdraw 30 law
        c.withdrawItem(42, 30 - c.getInventoryItemCount(42));
        c.sleep(340);
      }
      if (!c.isItemIdEquipped(101)
          && !c.isItemIdEquipped(617)
          && !c.isItemIdEquipped(684)) { // check air staff!
        c.displayMessage("@red@NO Air staff, attempting to Equip!");
        if (c.getBankItemCount(101) > 0) {
          c.withdrawItem(101, 1);
          c.closeBank();
          c.equipItem(c.getInventoryItemSlotIndex(101));
        } else if (c.getBankItemCount(617) > 0) {
          c.withdrawItem(617, 1);
          c.closeBank();
          c.equipItem(c.getInventoryItemSlotIndex(617));
        } else if (c.getBankItemCount(684) > 0) {
          c.withdrawItem(684, 1);
          c.closeBank();
          c.equipItem(c.getInventoryItemSlotIndex(684));
        } else if (c.getBankItemCount(101) == 0
            && c.getBankItemCount(617) == 0
            && c.getBankItemCount(684) == 0) c.displayMessage("@red@NO Air staff, ending script");
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      if (c.isInBank()) c.closeBank();
    }
  }

  private void WineToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(332, 434);
    c.walkTo(327, 435);
    c.walkTo(310, 435);
    c.walkTo(299, 446);
    c.walkTo(309, 456);
    c.walkTo(309, 468);
    c.walkTo(310, 468);
    c.walkTo(310, 478);
    c.walkTo(311, 479);
    c.walkTo(311, 488);
    c.walkTo(305, 494);
    c.walkTo(305, 496);
    c.walkTo(298, 503);
    c.walkTo(312, 517);
    c.walkTo(312, 518);
    c.walkTo(324, 530);
    c.walkTo(324, 549);
    c.walkTo(327, 552);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToWine() {
    c.setStatus("@gre@Walking to Wines..");
    c.walkTo(327, 552);
    c.walkTo(324, 549);
    c.walkTo(324, 530);
    c.walkTo(312, 518);
    c.walkTo(312, 517);
    c.walkTo(298, 503);
    c.walkTo(305, 496);
    c.walkTo(305, 494);
    c.walkTo(311, 488);
    c.walkTo(311, 479);
    c.walkTo(310, 478);
    c.walkTo(310, 468);
    c.walkTo(309, 468);
    c.walkTo(309, 456);
    c.walkTo(299, 446);
    c.walkTo(310, 435);
    c.walkTo(327, 435);
    c.walkTo(332, 434);
    // next to wine now)
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Zammy Wine Tele Grabber ~ Kaila");
    JLabel label1 = new JLabel("Start in Edge Bank");
    JLabel label2 = new JLabel("Air Staff/Bstaff/etc MUST be Equipped");
    JLabel label3 = new JLabel("Laws in the inv required!");
    JLabel label4 = new JLabel("Recommend using grape harvester for coleslaw wines!!!!");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          startTime = System.currentTimeMillis();
          scriptStarted = true;
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
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
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (totalWinez * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Zammy Winez @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Wines in Bank: @gre@" + WinezInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Wines Picked: @gre@"
              + totalWinez
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
              + String.format("%,d", TripSuccessPerHr)
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
