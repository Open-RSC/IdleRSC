package scripting.idlescript;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Grabs Monks Robes from edge monestary
 *
 * <p>Picks up Monks Robe SETS in Edge Monastery and Banks (equal amount of tops and bottoms) Start
 * in Edge Bank or near Robes Recommend Armor against lvl 21 Scorpions Please Gain Permission to
 * enter Prayer guild FIRST Bot will loot Equal Amounts of robe tops and bottoms
 *
 * <p>Author - Kaila
 */
public class K_MonkRobes extends IdleScript {
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  int GrapezInBank = 0;
  int totalTopz = 0;
  int totalBotz = 0;
  int totalTrips = 0;
  int TopzInBank = 0;
  int BotzInBank = 0;

  int robeId[] = {388, 389};

  long startTime;
  long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String parameters[]) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      controller.displayMessage("@red@Monks Robes Picker - By Kaila");
      controller.displayMessage("@red@Start in Edge Bank or upstairs Monestary");
      if (controller.isInBank() == true) {
        controller.closeBank();
      }
      if (controller.currentY() < 1000 && controller.currentX() < 245) {
        bank();
        BankToGrape();
        controller.sleep(1380);
      }
      if (controller.currentY() < 1000 && controller.currentX() > 245) {
        controller.atObject(251, 468);
        controller.sleep(340);
        controller.walkTo(260, 1411);
        controller.walkTo(260, 1411);
        controller.walkTo(260, 1405);
        controller.walkTo(264, 1403);
      }
      scriptStart();
    }
    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (controller.isRunning()) {

      if (controller.getInventoryItemCount() == 30) {
        controller.setStatus("@red@Banking..");
        GrapeToBank();
        bank();
        BankToGrape();
        controller.sleep(618);
      }

      int[] coords = controller.getNearestItemById(388); // always pick up tops
      if (coords != null) {
        controller.setStatus("@yel@Looting..");
        controller.pickupItem(coords[0], coords[1], 388, true, true);
        controller.sleep(618);
      }
      if (controller.getInventoryItemCount(389) < controller.getInventoryItemCount(388)) {
        int[] coords2 = controller.getNearestItemById(389);
        if (coords2 != null) { // pick up bottoms if you have more tops then bottoms!
          controller.setStatus("@yel@Looting..");
          controller.pickupItem(coords2[0], coords2[1], 389, true, true);
          controller.sleep(618);
        }
        controller.sleep(100);
      }
    }
  }

  public void bank() {

    controller.setStatus("@yel@Banking..");
    controller.openBank();
    controller.sleep(640);

    if (controller.isInBank()) {

      totalTopz = totalTopz + controller.getInventoryItemCount(388);
      totalBotz = totalBotz + controller.getInventoryItemCount(389);

      if (controller.getInventoryItemCount(388) > 0) { // robe top
        controller.depositItem(388, controller.getInventoryItemCount(388));
        controller.sleep(1380);
      }
      if (controller.getInventoryItemCount(389) > 0) { // robe bot
        controller.depositItem(389, controller.getInventoryItemCount(389));
        controller.sleep(1380);
      }

      TopzInBank = controller.getBankItemCount(388);
      BotzInBank = controller.getBankItemCount(389);

      controller.closeBank();
      controller.sleep(640);
    }
  }

  public void GrapeToBank() { // replace

    controller.setStatus("@gre@Walking to Bank..");
    controller.walkTo(260, 1405);
    controller.walkTo(260, 1411);
    controller.walkTo(260, 1411);
    controller.walkTo(251, 1411);
    controller.atObject(251, 1412);
    controller.walkTo(252, 464);
    controller.walkTo(254, 463);
    // next to grapes now
    controller.walkTo(254, 454);
    controller.walkTo(256, 451);
    controller.walkTo(255, 444);
    controller.walkTo(255, 433);
    controller.walkTo(255, 422);
    controller.walkTo(258, 422);
    controller.walkTo(258, 415);
    controller.walkTo(252, 421);
    controller.walkTo(242, 432);
    controller.walkTo(225, 432);
    controller.walkTo(220, 437);
    controller.walkTo(220, 445);
    controller.walkTo(218, 447);
    totalTrips = totalTrips + 1;
    controller.setStatus("@gre@Done Walking..");
  }

  public void BankToGrape() {

    controller.setStatus("@gre@Walking to Robes..");
    controller.walkTo(218, 447);
    controller.walkTo(220, 445);
    controller.walkTo(220, 437);
    controller.walkTo(225, 432);
    controller.walkTo(242, 432);
    controller.walkTo(252, 421);
    controller.walkTo(258, 415);
    controller.walkTo(258, 422);
    controller.walkTo(255, 422);
    controller.walkTo(255, 433);
    controller.walkTo(255, 444);
    controller.walkTo(256, 451);
    controller.walkTo(254, 454);
    // grape pathing ends here
    controller.walkTo(254, 464);
    controller.walkTo(251, 464);
    controller.walkTo(251, 467);
    controller.sleep(340);
    while (controller.currentX() == 251 && controller.currentY() == 467) {
      controller.atObject(251, 468);
      controller.sleep(340);
    }
    controller.walkTo(260, 1411);
    controller.walkTo(260, 1411);
    controller.walkTo(260, 1405);
    controller.walkTo(264, 1403);
    // next to robes now)
    controller.setStatus("@gre@Done Walking..");
  }

  // GUI stuff below (icky)
  public void setupGUI() {
    JLabel header = new JLabel("Monk Robe Picker - By Kaila");
    JLabel label1 = new JLabel("Picks up Monks Robe SETS in Edge Monastery and Banks");
    JLabel label2 = new JLabel("*Start in Edge Bank or near Robes!");
    JLabel label3 = new JLabel("*Recommend Armor against lvl 21 Scorpions");
    JLabel label4 = new JLabel("*Please Gain Permission to enter Prayer guild FIRST");
    JLabel label5 = new JLabel("*Bot will loot Equal Amounts of robe tops and bottoms");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            startTime = System.currentTimeMillis();
            scriptStarted = true;
          }
        });

    scriptFrame = new JFrame(controller.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(startScriptButton);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  public static String msToString(long milliseconds) {
    long sec = milliseconds / 1000;
    long min = sec / 60;
    long hour = min / 60;
    sec %= 60;
    min %= 60;
    DecimalFormat twoDigits = new DecimalFormat("00");

    return new String(
        twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec));
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      String runTime = msToString(System.currentTimeMillis() - startTime);
      int TopzSuccessPerHr = 0;
      int BotzSuccessPerHr = 0;
      int TripSuccessPerHr = 0;

      try {
        float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        float scale = (60 * 60) / timeRan;
        TopzSuccessPerHr = (int) (totalTopz * scale);
        BotzSuccessPerHr = (int) (totalTopz * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);

      } catch (Exception e) {
        // divide by zero
      }
      controller.drawString("@red@Monks Robe Picker @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
      controller.drawString(
          "@whi@Robe Tops Banked: @gre@" + String.valueOf(this.TopzInBank), 330, 62, 0xFFFFFF, 1);
      controller.drawString(
          "@whi@Robe Bots Banked:@gre@" + String.valueOf(this.BotzInBank), 330, 76, 0xFFFFFF, 1);
      controller.drawString(
          "@whi@Robe Tops Picked: @gre@"
              + String.valueOf(this.totalTopz)
              + "@yel@ (@whi@"
              + String.format("%,d", TopzSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          330,
          90,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Robe Bots Picked: @gre@"
              + String.valueOf(this.totalBotz)
              + "@yel@ (@whi@"
              + String.format("%,d", BotzSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          330,
          104,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Total Trips: @gre@"
              + String.valueOf(this.totalTrips)
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          330,
          118,
          0xFFFFFF,
          1);
      controller.drawString("@whi@Runtime: " + runTime, 330, 132, 0xFFFFFF, 1);
    }
  }
}
