package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Dragon Sword Buyer - By Kaila.</b>
 *
 * <p>Talks to Jakut for swords and Banks. <br>
 * Start by Jakut or zanaris bank! <br>
 * Need coins in the inventory to buy and diamonds to enter. <br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila.
 */
public final class K_BuyDragonSwords extends K_kailaScript {
  private static int totalTopz = 0;
  private static int totalTrips = 0;
  private static int TopzInBank = 0;
  private static int stopAmount = 100;
  private final int DIAMOND = ItemId.DIAMOND.getId();
  private final int DRAGON_SWORD = ItemId.DRAGON_SWORD.getId();
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
      c.displayMessage("@red@Dragon Sword Buyer - By Kaila");
      c.displayMessage("@red@Start by seller or at zanaris bank!");
      c.displayMessage("@red@Need coins in the inventory to buy");
      if (c.isInBank()) c.closeBank();
      if (c.currentX() > 118) {
        bank();
        BankToGrape();
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
        GrapeToBank();
        bank();
        BankToGrape();
      }
      if (!c.isInShop() && c.getInventoryItemCount() != 30) {
        ORSCharacter npc = c.getNearestNpcById(220, true);
        if (npc != null) {
          c.setStatus("@red@Getting Dragon Swords from Jakut..");
          c.npcCommand1(npc.serverIndex);
          c.sleep(4000);
        }
      } else if (c.isInShop() && c.getInventoryItemCount() != 30) {
        if (c.getShopItemCount(DRAGON_SWORD) > 0) {
          c.shopBuy(DRAGON_SWORD, c.getShopItemCount(DRAGON_SWORD));
        } else {
          c.sleep(GAME_TICK);
        }
      }
      c.sleep(GAME_TICK);
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalTopz = totalTopz + c.getInventoryItemCount(DRAGON_SWORD);
      if (c.getInventoryItemCount(DRAGON_SWORD) > 0) {
        c.depositItem(DRAGON_SWORD, c.getInventoryItemCount(DRAGON_SWORD));
        c.sleep(1380);
      }
      if (c.getInventoryItemCount(DIAMOND) < 2) {
        withdrawItem(DIAMOND, 2);
      }
      TopzInBank = c.getBankItemCount(DRAGON_SWORD);
      c.closeBank();
      if (TopzInBank >= stopAmount) endSession();
    }
  }

  private void doorLoop() {
    // for (int id = 0; id < 100; id++) {
    c.atWallObject(116, 3537); // interact with door
    c.sleep(9000);
    c.optionAnswer(0);
    c.sleep(2000);
    // }
  }

  private void GrapeToBank() { // replace
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(115, 3537);
    doorLoop();
    c.walkTo(117, 3537);
    c.walkTo(126, 3528);
    c.walkTo(133, 3528);
    c.walkTo(142, 3527);
    c.walkTo(152, 3527);
    c.walkTo(162, 3527);
    c.walkTo(172, 3527);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToGrape() {
    c.setStatus("@gre@Walking to Zanaris Market..");
    c.walkTo(172, 3527);
    c.walkTo(162, 3527);
    c.walkTo(152, 3527);
    c.walkTo(142, 3527);
    c.walkTo(133, 3528);
    c.walkTo(126, 3528);
    c.walkTo(118, 3536);
    // near door
    doorLoop();
    c.walkTo(114, 3542);
    // next to jakut now
    c.setStatus("@gre@Done Walking..");
  }
  // GUI stuff below
  private void setupGUI() {
    JLabel header = new JLabel("Dragon Sword Buyer ~ By Kaila");
    JLabel label1 = new JLabel("Enters zanaris market to buy D Swords");
    JLabel label2 = new JLabel("Start by Jakut or at zanaris bank!");
    JLabel label3 = new JLabel("Need coins and cut diamonds");
    JLabel stopAmountLabel = new JLabel("What bank amount should we stop at?");
    JTextField stopAmountField = new JTextField(String.valueOf(100));
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!stopAmountField.getText().isEmpty()) {
            stopAmount = Integer.parseInt(stopAmountField.getText());
          }
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
    scriptFrame.add(stopAmountLabel);
    scriptFrame.add(stopAmountField);
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
      int TopzSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        TopzSuccessPerHr = (int) (totalTopz * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@Dragon Sword Buyer @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@D.swords in Bank: @gre@" + TopzInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString("@whi@Stop at bank amount: @gre@" + stopAmount, x, y + (14 * 2), 0xFFFFFF, 1);
      c.drawString(
          "@whi@Coins Spent: @gre@" + (totalTopz * 100) + " @whi@K", x, y + (14 * 3), 0xFFFFFF, 1);
      c.drawString(
          "@whi@D.swords Bought: @gre@"
              + totalTopz
              + "@yel@ (@whi@"
              + String.format("%,d", TopzSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 6), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 6), 0xFFFFFF, 1);
    }
  }
}
