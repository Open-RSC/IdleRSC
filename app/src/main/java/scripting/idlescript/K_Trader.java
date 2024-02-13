package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.*;
import javax.swing.*;

// todo add option for trading presents?
public class K_Trader extends K_kailaScript {

  private static final Controller c = Main.getController();
  private String playerName = "";
  private int[] tradeItems;
  private int indexValue;
  private int traderType;

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
      if (c.isAuthentic()) c.stop();
      // clear inventory to stop array issue
      if (c.getInventoryItemCount() > 0 && traderType != 2) {
        if (!c.isInBank()) {
          c.openBank();
          waitForBankOpen();
        }
        depositAll();
        c.closeBank();
      }

      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      c.displayMessage("@red@K_Trader by Kaila!");
      if (c.isInBank()) c.closeBank();
      scriptStart();
    }
    return 1000; // start() must return a int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (traderType == 0) { // giver
        if ((indexValue == (tradeItems.length - 1) && c.getInventoryItemCount() == 0)
            || !c.isRunning()) {
          c.stop();
        }
        if (c.getInventoryItemCount() == 0) { // do bank loop
          c.setStatus("Banking..");
          c.openBank();
          waitForBankOpen();

          if (c.isInBank()) {
            while (indexValue < (tradeItems.length - 1) && c.getInventoryItemCount() < 25) {
              if (c.isItemTradeable(tradeItems[indexValue])
                  && c.isItemNotable(tradeItems[indexValue])) {
                c.withdrawItemAsNote(
                    tradeItems[indexValue], c.getBankItemCount(tradeItems[indexValue]));
              } else if (c.isItemTradeable(tradeItems[indexValue])
                  && c.isItemStackable(tradeItems[indexValue])) {
                c.withdrawItem(tradeItems[indexValue], c.getBankItemCount(tradeItems[indexValue]));
              }
              indexValue++;
            }
            c.sleep(1280);
            c.closeBank();
          }
        }

        // do open trade and put up items loop
        if (c.getInventoryItemCount() > 0 && !c.isInTrade()) {
          c.setStatus("Trading Player " + playerName);
          int serverIndex = c.getPlayerServerIndexByName(playerName);
          if (serverIndex == -1) {
            c.log("Receiving player " + playerName + " is not present!");
          }
          int[] coords = c.getPlayerCoordsByServerIndex(serverIndex);
          c.walkTo(coords[0], coords[1]);
          c.tradePlayer(serverIndex);
          waitForTradeOpen(playerName);

          int[] tradeArray = new int[c.getInventoryItemCount()];
          int[] amountArray = new int[c.getInventoryItemCount()];

          // keep track of how many items we're trading
          int tradeSlots = 0;
          //        // create map for trade
          for (int i = 0; i < c.getInventoryItemCount(); i++) {
            int id = c.getInventorySlotItemId(i);
            if (c.isItemTradeable(id)) {
              tradeArray[tradeSlots] = id;
              amountArray[tradeSlots] = c.getInventoryItemCount(id);
              tradeSlots++;
            }
          }

          if (tradeArray.length > (tradeSlots + 1))
            System.arraycopy(tradeArray, 0, tradeArray, 0, tradeSlots);
          if (amountArray.length > (tradeSlots + 1))
            System.arraycopy(amountArray, 0, amountArray, 0, tradeSlots);

          // trade screen
          c.setTradeItems(tradeArray, amountArray, true);
          c.sleep(640);
          c.acceptTrade();
          c.sleep(800);

          // Confirmation screen
          waitForTradeConfirmation();

          if (c.isInTradeConfirmation()) {
            c.acceptTradeConfirmation();
            c.sleep(800);
          }
        }
      } else if (traderType == 1) { // receiver
        if (!c.isRunning()) {
          c.stop();
        }
        if (c.getInventoryItemCount() < 25 && !c.isInTrade()) {
          c.setStatus("Trading Player " + playerName);
          int serverIndex = c.getPlayerServerIndexByName(playerName);
          if (serverIndex == -1) {
            c.log("Receiving player " + playerName + " is not present!");
          }
          int[] coords = c.getPlayerCoordsByServerIndex(serverIndex);
          c.walkTo(coords[0], coords[1]);
          c.tradePlayer(serverIndex);

          waitForTradeOpen(playerName);
          waitForTradeRecipientAccepting();
          c.acceptTrade();
          waitForTradeConfirmation(); // Confirmation screen

          if (c.isInTradeConfirmation()) {
            c.acceptTradeConfirmation();
            waitForTradeConfirmationToClose();
          }
        } else {
          c.setStatus("Banking..");
          c.openBank();
          waitForBankOpen();

          if (c.isInBank()) {
            depositAll();
            c.sleep(1280);
            c.closeBank();
          }
        }
      } else if (traderType == 2) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < c.getInventoryItemCount(); i++) {
          result.append(c.getInventorySlotItemId(i)).append(",");
        }
        c.log(result.toString());
        c.stop();
      }
    }
  }

  public void setupGUI() {
    JLabel title = new JLabel("K_Trader ~ by Kaila");
    JLabel label1 = new JLabel("Trade items to another player (coleslaw only)");
    JLabel label2 = new JLabel("Withdraw 1 of each item, run with PrintIds");
    JLabel label3 = new JLabel("Copy Id string from console and paste to ItemIds");
    JLabel traderTypeLabel = new JLabel("Trader type Select, or PrintIds");
    JComboBox<String> traderTypeField =
        new JComboBox<>(new String[] {"Giver", "Receiver", "Print inventory itemIds in Console"});
    JLabel playerNameLabel = new JLabel("PlayerName to trade (only one name)");
    JTextField playerNameField = new JTextField(playerName);
    JLabel itemIdsLabel = new JLabel("ItemIds to trade");
    JTextField itemIdsField = new JTextField("");
    JButton startScriptButton = new JButton("Start");

    traderTypeField.addActionListener(
        e -> {
          itemIdsField.setEnabled(traderTypeField.getSelectedIndex() == 0);
        });
    startScriptButton.addActionListener(
        e -> {
          if (!itemIdsField.getText().isEmpty()) {
            String[] newResult = itemIdsField.getText().replace(" ", "").split(",");
            tradeItems = new int[newResult.length];
            for (int i = 0; i < newResult.length; i++) {
              tradeItems[i] = Integer.parseInt(newResult[i]);
            }
            indexValue = 0;
          }
          playerName = playerNameField.getText().toLowerCase();
          traderType = traderTypeField.getSelectedIndex();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          c.displayMessage("@red@Starting K_Trader Script");
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(title);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(traderTypeLabel);
    scriptFrame.add(traderTypeField);
    scriptFrame.add(playerNameLabel);
    scriptFrame.add(playerNameField);
    scriptFrame.add(itemIdsLabel);
    scriptFrame.add(itemIdsField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }
}
