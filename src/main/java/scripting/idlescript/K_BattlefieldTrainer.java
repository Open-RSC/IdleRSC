package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import orsc.ORSCharacter;

/**
 * Battlefield Trainer - By Kaila.
 *
 * <p>Start in Ardy or at Battlefield. Sharks in Bank REQUIRED. @Author ~ Kaila
 */
/*
 *   todo add food type selection add maging option
 */
public class K_BattlefieldTrainer extends K_kailaScript {
  public int start(String[] parameters) {

    if (scriptStarted) {
      guiSetup = true;
      c.displayMessage("@red@Battlefield Trainer - By Kaila");
      c.displayMessage("@red@Start in Ardy or at Battlefield");
      c.displayMessage("@red@Sharks in Bank REQUIRED");
      if (c.isInBank()) {
        c.closeBank();
      }
      if (c.currentX() < 600) {
        bank();
        BankToDruid();
        c.sleep(1380);
      }
      scriptStart();
    }
    if (!scriptStarted && !guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {

      eat();

      if (c.getInventoryItemCount(546) > 0) {

        if (!c.isInCombat()) {

          c.setStatus("@yel@Attacking Trooper");
          ORSCharacter npc = c.getNearestNpcById(407, false);
          if (npc != null) {
            // c.walktoNPC(npc.serverIndex,1);
            c.attackNpc(npc.serverIndex);
            c.sleep(600);
          } else {
            c.sleep(600);
          }
        }
        c.sleep(380);
      } else if (c.getInventoryItemCount(546) == 0) {
        c.setStatus("@yel@Banking..");
        DruidToBank();
        bank();
        BankToDruid();
        c.sleep(618);
      }
    }
  }

  private void bank() {

    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);

    if (c.isInBank()) {

      if (c.getInventoryItemCount() > 1) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 546) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
        c.sleep(1280); // increased sleep here to prevent double banking
      }
      if (c.getInventoryItemCount(546) < 28) { // withdraw 1 shark
        c.withdrawItem(546, 28);
        c.sleep(340);
      }
      if (c.getBankItemCount(546) == 0) {
        c.setStatus("@red@NO Sharks/Laws/Airs/Earths in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      c.closeBank();
      c.sleep(640);
    }
  }

  private void eat() {
    int eatLvl = c.getBaseStat(c.getStatId("Hits")) - 20;

    if (c.getCurrentStat(c.getStatId("Hits")) < eatLvl) {

      leaveCombat();
      c.setStatus("@red@Eating..");

      boolean ate = false;

      for (int id : c.getFoodIds()) {
        if (c.getInventoryItemCount(id) > 0) {
          c.itemCommand(id);
          c.sleep(700);
          ate = true;
          break;
        }
      }
      if (!ate) { // only activates if hp goes to -20 again THAT trip, will bank and get new shark
        // usually
        c.setStatus("@red@We've ran out of Food! Running Away!.");
        DruidToBank();
        bank();
        BankToDruid();
      }
    }
  }

  private void DruidToBank() {
    c.setStatus("@gre@Walking to Bank..");

    c.walkTo(649, 639);
    c.walkTo(644, 639);
    c.walkTo(636, 638);
    c.walkTo(624, 638);
    c.walkTo(614, 632);
    c.walkTo(622, 633);
    c.walkTo(614, 632);
    c.walkTo(610, 635);
    c.walkTo(599, 635);
    c.walkTo(598, 632);
    c.walkTo(592, 627);
    c.walkTo(579, 628);
    c.walkTo(571, 628);
    c.walkTo(563, 621);
    c.walkTo(550, 620);
    c.walkTo(550, 613);

    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToDruid() {
    c.setStatus("@gre@Walking to Druids..");

    c.walkTo(550, 613);
    c.walkTo(550, 620);
    c.walkTo(563, 621);
    c.walkTo(571, 628);
    c.walkTo(579, 628);
    c.walkTo(592, 627);
    c.walkTo(598, 632);
    c.walkTo(599, 635);
    c.walkTo(610, 635);
    c.walkTo(614, 632);
    c.walkTo(622, 633);
    c.walkTo(624, 638);
    c.walkTo(636, 638);
    c.walkTo(644, 639);
    c.walkTo(649, 639);
    c.walkTo(653, 642);
    c.walkTo(658, 642);

    c.setStatus("@gre@Done Walking..");
  }

  // GUI stuff below (icky)
  private void setupGUI() {

    JLabel header = new JLabel("Battlefield Trainer - By Kaila");
    JLabel label1 = new JLabel("Start in Ardy or at Battlefield");
    JLabel label2 = new JLabel("Sharks in Bank REQUIRED");
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
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;

        TripSuccessPerHr = (int) (totalTrips * scale);

      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Battlefield Trainer @mag@~ by Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 2), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 2), 0xFFFFFF, 1);
    }
  }
}
