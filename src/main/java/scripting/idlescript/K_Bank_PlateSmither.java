package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;

/**
 * <b>Fast Platebody Smither</b>
 *
 * <p>Start with Hammer in Inv! Only works on Coleslaw (batch bars)<br>
 * This ensures 5 Plates are made per Menu Cycle. Supports all bar types.<br>
 * This bot supports the \"autostart\" parameter. defaults to steel.<br>
 *
 * <p>Parameters for Starting: <br>
 * <i>autostart</i> - makes steel platebodies<br>
 * <i>bronze</i> - makes bronze platebodies<br>
 * <i>iron</i> - makes iron platebodies<br>
 * <i>steel</i> - makes steel platebodies<br>
 * <i>mith, mithril</i> - makes mithril platebodies<br>
 * <i>addy, adamantite</i> - makes adamantite platebodies.<br>
 * <i>rune, runite</i> - makes runite platebodies.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_Bank_PlateSmither extends K_kailaScript {
  private static int barId = -1;
  private static final int[] barIds = {
    ItemId.BRONZE_BAR.getId(),
    ItemId.IRON_BAR.getId(),
    ItemId.STEEL_BAR.getId(),
    ItemId.MITHRIL_BAR.getId(),
    ItemId.ADAMANTITE_BAR.getId(),
    ItemId.RUNITE_BAR.getId()
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    c.quitIfAuthentic();
    c.toggleBatchBarsOn();
    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.displayMessage("Got param " + parameters[0] + ", Auto-starting Steel Plates", 0);
        System.out.println("Got param" + parameters[0] + ", Auto-starting Steel Plates");
        barId = 171;
        guiSetup = true;
        scriptStarted = true;
      }
      if (parameters[0].toLowerCase().startsWith("bronze")) {
        c.displayMessage("Got param " + parameters[0] + ". Using bronze bars!", 0);
        System.out.println("Got param" + parameters[0] + ", Using bronze bars");
        barId = 169;
        guiSetup = true;
        scriptStarted = true;
      }
      if (parameters[0].toLowerCase().startsWith("iron")) {
        c.displayMessage("Got param " + parameters[0] + ". Using iron bars!", 0);
        System.out.println("Got param" + parameters[0] + ", Using iron bars!");
        barId = 170;
        guiSetup = true;
        scriptStarted = true;
      }
      if (parameters[0].toLowerCase().startsWith("steel")) {
        c.displayMessage("Got param " + parameters[0] + ". Using steel bars!", 0);
        System.out.println("Got param" + parameters[0] + ", Using steel bars!");
        barId = 171;
        guiSetup = true;
        scriptStarted = true;
      }
      if (parameters[0].toLowerCase().startsWith("mith")
          || parameters[0].toLowerCase().startsWith("mithril")) {
        c.displayMessage("Got param " + parameters[0] + ". Using mith bars!", 0);
        System.out.println("Got param" + parameters[0] + ", Using mithril bars!");
        barId = 173;
        guiSetup = true;
        scriptStarted = true;
      }
      if (parameters[0].toLowerCase().startsWith("addy")
          || parameters[0].toLowerCase().startsWith("adamantite")) {
        c.displayMessage("Got param " + parameters[0] + ". Using addy bars!", 0);
        System.out.println("Got param" + parameters[0] + ", Using adamantite bars!");
        barId = 174;
        guiSetup = true;
        scriptStarted = true;
      }
      if (parameters[0].toLowerCase().startsWith("rune")
          || parameters[0].toLowerCase().startsWith("runite")) {
        c.displayMessage("Got param " + parameters[0] + ". Using rune bars!", 0);
        System.out.println("Got param" + parameters[0] + ", Using rune bars!");
        barId = 408;
        guiSetup = true;
        scriptStarted = true;
      }
      if (!guiSetup) {
        setupGUI();
        guiSetup = true;
      }
      if (scriptStarted) {
        guiSetup = false;
        scriptStarted = false;
        c.displayMessage("@gre@Fast Platebody Smither - by Kaila");
        c.displayMessage("@gre@Start in Varrock West bank with a HAMMER");
        c.displayMessage("@red@REQUIRES Batch bars be toggle on in settings to work correctly!");
        if (c.isInBank()) c.closeBank();
        startTime = System.currentTimeMillis();
        next_attempt = System.currentTimeMillis() + 10000L;
        scriptStart();
      }
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getInventoryItemCount(barId) < 5 && !c.isInBank()) {
        c.setStatus("@gre@Banking..");
        c.displayMessage("@gre@Banking..");
        c.walkTo(150, 507);
        bank();
        c.walkTo(148, 512);
      }
      if (c.getInventoryItemCount(barId) > 4) {
        smithingScript();
      }
    }
  }

  private void smithingScript() {
    c.sleepHandler(98, true);
    c.setStatus("@gre@Smithing..");
    c.displayMessage("@gre@Smithing..");
    c.useItemIdOnObject(148, 513, barId);
    c.sleep(1280);
    c.optionAnswer(1);
    c.sleep(640);
    c.optionAnswer(2);
    c.sleep(640);
    c.optionAnswer(2);
    c.sleep(640);
    if (!c.isAuthentic()) {
      c.optionAnswer(3);
      c.sleep(3000); // was 650
    }
    waitForBatching();
  }

  private void bank() {
    c.setStatus("@gre@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalPlates = totalPlates + 5;
      totalBars = totalBars + 25;
      if (c.getBankItemCount(barId)
          < 30) { // stops making when 30 in bank to not mess up alignments/organization of bank!!!
        c.setStatus("@red@NO Bars in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      if (c.getInventoryItemCount() > 1) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 168 && itemId != 1263 && itemId != barId) {
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
        c.sleep(100);
      }
      if (c.getInventoryItemCount(168) < 1) {
        c.withdrawItem(168, 1);
        c.sleep(100);
      }
      if (c.getInventoryItemCount(barId) < 25) {
        c.withdrawItem(barId, 25);
        c.sleep(100);
      }

      barsInBank = c.getBankItemCount(barId);
      c.closeBank();
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Platebody Smithing - Kaila");
    JLabel hammerLabel = new JLabel("Start with Hammer in Inv!");
    JLabel batchLabel = new JLabel("Batch Bars MUST be On, Bot will attempt to enable it.");
    JLabel batchLabel2 = new JLabel("This ensures 5 Plates are made per Menu Cycle.");
    JLabel barLabel = new JLabel("Bar Type:");
    JComboBox<String> barField =
        new JComboBox<>(
            new String[] {"Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite"});
    JLabel paramLabel3 = new JLabel("This bot supports the \"autostart\" parameter");
    JLabel paramLabel1 = new JLabel("Script can also be started with the Parameters:");
    JLabel paramLabel2 =
        new JLabel("\"Bronze\", \"Iron\", \"Steel\", \"Mith\", \"Addy\", \"Runite\"");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          barId = barIds[barField.getSelectedIndex()];
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
    scriptFrame.add(barLabel);
    scriptFrame.add(barField);
    scriptFrame.add(paramLabel3);
    scriptFrame.add(paramLabel1);
    scriptFrame.add(paramLabel2);
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
      int plateSuccessPerHr = 0;
      int barSuccessPerHr = 0;
      try {
        long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        plateSuccessPerHr = (int) (totalPlates * scale);
        barSuccessPerHr = (int) (totalBars * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Fast Plate Smither @whi@~ @mag@Kaila", x, 12, 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Bars In bank: @gre@" + barsInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString("@whi@Bars Used: @gre@" + totalBars, x, y + (14 * 2), 0xFFFFFF, 1);
      c.drawString(
          "@whi@Bars Per Hr: @gre@" + String.format("%,d", barSuccessPerHr) + "@yel@/@whi@hr",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString("@whi@Platebodies Made: @gre@" + totalPlates, x, y + (14 * 4), 0xFFFFFF, 1);
      c.drawString(
          "@whi@Platebodies Per Hr: @gre@"
              + String.format("%,d", plateSuccessPerHr)
              + "@yel@/@whi@hr",
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Time Remaining: " + c.timeToCompletion(totalBars, barsInBank, startTime),
          x,
          y + (14 * 6),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 7), 0xFFFFFF, 1);
      c.drawString("@whi@__________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
    }
  }
}
