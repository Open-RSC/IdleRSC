package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Cuts yew logs in NE ardy, including the far western one, banks in seers.
 *
 * <p>todo: logic to cut same tree as other players.
 *
 * <p>Author - Kaila.
 */
public class K_ArdyYewTree extends IdleScript {
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  int logInBank = 0;
  int totalLog = 0;
  int totalTrips = 0;
  int[] axeId = {
    87, 12, 88, 203, 204, 405 // bronze to rune in order
  };

  long startTime;
  long startTimestamp = System.currentTimeMillis() / 1000L;

  public void startSequence() {
    controller.displayMessage("@red@ArdyYewTrees, start with an axe in inv/equipment");
    if (controller.isInBank() == true) {
      controller.closeBank();
    }
    if (controller.currentY() < 620
        && controller.currentY() > 600
        && controller.currentX() > 543
        && controller.currentX() < 555) { // inside bank
      bank();
      bankToYews();
      controller.sleep(1380);
    }
    if (controller.currentY() < 600
        && controller.currentY() > 587
        && controller.currentX() > 525
        && controller.currentX() < 543) {
      controller.walkTo(533, 596);
      controller.walkTo(548, 600);
      bank();
      bankToYews();
      controller.sleep(1380);
    }
  }

  public int start(String parameters[]) {
    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        controller.displayMessage("Got Autostart, Cutting Yews", 0);
        System.out.println("Got Autostart, Cutting Yews");
        parseVariables();
        startSequence();
        scriptStart();
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      startSequence();
      scriptStart();
    }
    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (controller.isRunning()) {
      if (controller.getInventoryItemCount() < 30) {
        controller.setStatus("@gre@Cutting Yews..");
        if (controller.getObjectAtCoord(509, 571) == 309) {
          controller.walkTo(510, 570);
          controller.atObject(509, 571);
          controller.sleep(2000);
          while (controller.isBatching() && controller.getInventoryItemCount() < 30) {
            controller.sleep(1000);
          }
          if (controller.getInventoryItemCount() > 29) {
            goToBank();
          }
        }
        if (controller.getObjectAtCoord(507, 567) == 309) {
          controller.walkTo(509, 568);
          controller.atObject(507, 567);
          controller.sleep(2000);
          while (controller.isBatching() && controller.getInventoryItemCount() < 30) {
            controller.sleep(1000);
          }
          if (controller.getInventoryItemCount() > 29) {
            goToBank();
          }
        }
        mainYewToAltYew();
        if (controller.getObjectAtCoord(513, 525) == 309) {
          controller.walkTo(512, 526);
          controller.atObject(513, 525);
          controller.sleep(2000);
          while (controller.isBatching() && controller.getInventoryItemCount() < 30) {
            controller.sleep(1000);
          }
          if (controller.getInventoryItemCount() > 29) {
            altYewToMainYew();
            controller.walkTo(511, 571);
            bankToYews();
          }
          controller.walkTo(505, 533);
        }
        altYewToMainYew();
      } else {
        goToBank();
      }
    }
    //	return 1000; //start() must return a int value now.
  }

  public void mainYewToAltYew() {
    //  controller.walkTo(511,559);
    controller.walkTo(507, 553);
    controller.walkTo(505, 541);
  }

  public void altYewToMainYew() {
    controller.walkTo(505, 541);
    controller.walkTo(507, 553);
  }

  public void yewToBank() {
    controller.walkTo(512, 571);
    controller.walkTo(512, 577);
    controller.walkTo(521, 588);
    controller.walkTo(534, 595);
    controller.walkTo(547, 602);
    controller.walkTo(550, 612);
  }

  public void bankToYews() {
    controller.walkTo(550, 612);
    controller.walkTo(547, 602);
    controller.walkTo(534, 595);
    controller.walkTo(521, 588);
    controller.walkTo(512, 577);
    controller.walkTo(512, 571);
  }

  public void goToBank() {
    controller.setStatus("@gre@Walking to Bank..");
    yewToBank();
    controller.setStatus("@gre@Done Walking to Bank..");
    controller.walkTo(551, 613);
    totalTrips = totalTrips + 1;
    bank();
    controller.setStatus("@gre@Going to Yews..");
    bankToYews();
    controller.setStatus("@gre@Done Walking to Yews..");
  }

  public void bank() {

    controller.setStatus("@yel@Banking..");
    controller.openBank();
    controller.sleep(640);

    if (controller.isInBank()) {

      totalLog = totalLog + controller.getInventoryItemCount(635);

      for (int itemId : controller.getInventoryItemIds()) {
        if (itemId != 1263
            && itemId != axeId[0]
            && itemId != axeId[1]
            && itemId != axeId[2]
            && itemId != axeId[3]
            && itemId != axeId[4]
            && itemId != axeId[5]) {
          controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
        }
      }

      logInBank = controller.getBankItemCount(635);
      controller.closeBank();
      controller.sleep(1000);
    }
  }
  // GUI stuff below (icky)
  public String getOperatingSystem() {
    String os = System.getProperty("os.name");
    System.out.println("Using System Property: " + os);
    return os;
  }

  public static void centerWindow(Window frame) {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
    frame.setLocation(x, y);
  }

  public void parseVariables() {
    startTime = System.currentTimeMillis();
  }

  public void setupGUI() {
    JLabel header = new JLabel("Ardy Yew Logs by Kaila");
    JLabel label1 = new JLabel("Start in Seers bank, or near trees!");
    JLabel label2 = new JLabel("Wield or have rune axe in Inv");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            parseVariables();
            scriptStarted = true;
          }
        });

    scriptFrame = new JFrame(controller.getPlayerName() + " - options");

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
      int successPerHr = 0;
      int tripSuccessPerHr = 0;
      try {
        float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (totalLog * scale);
        tripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      controller.drawString("@red@Ardy Yew Logs @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
      controller.drawString(
          "@whi@Logs in Bank: @gre@" + String.valueOf(this.logInBank), 350, 62, 0xFFFFFF, 1);
      controller.drawString(
          "@whi@Logs Cut: @gre@"
              + String.valueOf(this.totalLog)
              + "@yel@ (@whi@"
              + String.format("%,d", successPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          76,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Total Trips: @gre@"
              + String.valueOf(this.totalTrips)
              + "@yel@ (@whi@"
              + String.format("%,d", tripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          350,
          90,
          0xFFFFFF,
          1);
      controller.drawString("@whi@Runtime: " + runTime, 350, 104, 0xFFFFFF, 1);
    }
  }
}
