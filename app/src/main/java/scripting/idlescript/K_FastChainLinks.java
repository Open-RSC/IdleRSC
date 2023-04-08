package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Fast Platebody Smither - by Kaila. ~ Start with Hammer in Inv! Batch Bars MUST be toggled ON in
 * settings!!! This ensures 5 Plates are made per Menu Cycle. Supports all bar types. ~ This bot
 * supports the \"autostart\" parameter. defaults to steel plates. ~ Parameters for Starting: auto,
 * autostart - makes steel platebodies. bronze - makes bronze platebodies. iron - makes iron
 * platebodies. steel - makes steel platebodies. mith, mithril - makes mithril platebodies. addy,
 * adamantite - makes adamantite platebodies. rune, runite - makes runite platebodies. ~ Author -
 * Kaila
 */
public class K_FastChainLinks extends IdleScript {
  private static final Controller c = Main.getController();
  private static JFrame scriptFrame = null;
  private static boolean guiSetup = false;
  private static boolean scriptStarted = false;
  private static int barsInBank = 0;
  private static int totalBars = 0;
  private static long startTime;
  private static final long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String[] parameters) {
    if (scriptStarted) {
      c.displayMessage("@gre@Chain Link Crafter" + '"' + " - by Kaila");
      c.displayMessage("@gre@Start in Fally East");

      c.quitIfAuthentic();
      if (c.isInBank()) c.closeBank();
      startTime = System.currentTimeMillis();
      scriptStart();
    }
    if (c.currentY() < 3000) {
      bank();
      bankToAnvil();
    }
    if (!scriptStarted && !guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getInventoryItemCount(593) > 0) {
        c.setStatus("@gre@Smithing..");
        c.useItemIdOnObject(320, 3424, 593);
        c.sleep(4000);
      } else if (c.getInventoryItemCount(1365) > 0) {
        c.setStatus("@gre@Smithing..");
        c.useItemIdOnObject(322, 3422, 1365);
        c.sleep(1240);
      } else if (c.getInventoryItemCount(593) == 0 && c.getInventoryItemCount(1365) == 0) {
        anvilToBank();
        bank();
        bankToAnvil();
      }
    }
  }

  private void bankToAnvil() {
    c.setStatus("@gre@Walking to Anvil!");
    c.walkTo(287, 571);
    c.walkTo(287, 562);
    c.walkTo(282, 556);
    c.walkTo(275, 556);
    c.walkTo(270, 551);
    c.walkTo(269, 546);
    c.walkTo(258, 546);
    c.walkTo(252, 541);
    c.walkTo(249, 540);
    c.walkTo(250, 538);
    c.atObject(251, 537);
    c.sleep(3000);
    c.walkTo(259, 3369);
    c.walkTo(263, 3369);
    c.walkTo(267, 3365);
    c.walkTo(267, 3355);
    c.walkTo(267, 3345);
    c.walkTo(267, 3339);
    c.walkTo(271, 3339);
    c.atObject(271, 3340);
    c.sleep(3000);
    c.walkTo(326, 3422);
    c.walkTo(322, 3425);
    c.sleep(100);
    c.setStatus("@gre@Done Walking!");
  }

  private void anvilToBank() {
    c.setStatus("@gre@Walking to Fally, Teleporting!");
    controller.castSpellOnSelf(controller.getSpellIdFromName("Falador Teleport"));
    controller.sleep(1000);
    // walk to east bank here
    c.walkTo(303, 552);
    c.walkTo(298, 552);
    c.walkTo(290, 560);
    c.walkTo(290, 568);
    c.walkTo(287, 571);
  }

  private void bank() {

    c.setStatus("@gre@Banking..");
    c.openBank();
    c.sleep(2000);
    if (c.isInBank()) {

      totalBars = totalBars + 26;

      if (c.getBankItemCount(593)
          < 2) { // stops making when 30 in bank to not mess up alignments/organization of bank!!!
        c.setStatus("@red@NO D Longs in the bank, Stopping!.");
        c.stop();
      }
      if (c.getInventoryItemCount() > 1) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 168 && itemId != 33 && itemId != 42 && itemId != 32) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
        c.sleep(100);
      }
      c.sleep(1240);
      if (controller.getInventoryItemCount(168) < 1) { // hammer
        controller.withdrawItem(168, 1 - controller.getInventoryItemCount(168));
        controller.sleep(1000);
      }
      if (controller.getInventoryItemCount(33) < 300) { // air
        controller.withdrawItem(33, 300 - controller.getInventoryItemCount(33));
        controller.sleep(1000);
      }
      if (controller.getInventoryItemCount(42) < 100) { // law
        controller.withdrawItem(42, 100 - controller.getInventoryItemCount(42));
        controller.sleep(1000);
      }
      if (controller.getInventoryItemCount(32) < 100) { // water
        controller.withdrawItem(32, 100 - controller.getInventoryItemCount(32));
        controller.sleep(1000);
      }
      if (c.getInventoryItemCount(593) < 25) { // dlong
        c.withdrawItem(593, 25);
        c.sleep(100);
      }
      barsInBank = c.getBankItemCount(593);
      c.closeBank();
      c.sleep(200);
    }
  }
  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("Platebody Smithing - Kaila");
    JLabel hammerLabel = new JLabel("Start with Hammer in Inv!");
    JLabel batchLabel = new JLabel("Batch Bars MUST be toggled ON in settings!!!");
    JLabel batchLabel2 = new JLabel("This ensures 5 Plates are made per Menu Cycle.");
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
    scriptFrame.add(hammerLabel);
    scriptFrame.add(batchLabel);
    scriptFrame.add(batchLabel2);
    scriptFrame.add(startScriptButton);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocus();
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int barSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        barSuccessPerHr = (int) (totalBars * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Fast Chain Link Smither @mag@~ by Kaila", x, 12, 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@D Longs In bank: @gre@" + barsInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString("@whi@D Longs Used: @gre@" + totalBars, x, y + (14 * 2), 0xFFFFFF, 1);
      c.drawString(
          "@whi@D Longs Per Hr: @gre@" + String.format("%,d", barSuccessPerHr) + "@yel@/@whi@hr",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Time Remaining: " + c.timeToCompletion(totalBars, barsInBank, startTime),
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 5), 0xFFFFFF, 1);
      c.drawString("@whi@__________________", x, y + 3 + (14 * 5), 0xFFFFFF, 1);
    }
  }
}
