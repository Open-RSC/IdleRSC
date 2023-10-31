package scripting.idlescript;

import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * BuyFromShop by Searos
 *
 * @author Searos and Kaila (fixed/improved)
 */
public class BuyFromShop extends K_kailaScript {
  int[] itemIds = {};
  int[] npcId = {};
  int shopNumber = -1;
  int startX = -1;
  int startY = -1;
  int purchased = 0;
  final JTextField items = new JTextField("");
  final JTextField shopCount = new JTextField("0");
  JTextField shopBuyCount = new JTextField("0");
  final JTextField vendorId =
      new JTextField("51,55,87,105,145,168,185,222,391,82,83,88,106,146,169,186,223");
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    startX = c.currentX();
    startY = c.currentY();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      next_attempt = System.currentTimeMillis() + 5000L;
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void startWalking(int x, int y) {
    // shitty autowalk
    int newX = x;
    int newY = y;
    while (c.currentX() != x || c.currentY() != y) {
      if (c.currentX() - x > 23) {
        newX = c.currentX() - 10;
      }
      if (c.currentY() - y > 23) {
        newY = c.currentY() - 10;
      }
      if (c.currentX() - x < -23) {
        newX = c.currentX() + 10;
      }
      if (c.currentY() - y < -23) {
        newY = c.currentY() + 10;
      }
      if (Math.abs(c.currentX() - x) <= 13) {
        newX = x;
      }
      if (Math.abs(c.currentY() - y) <= 13) {
        newY = y;
      }
      if (!c.isTileEmpty(newX, newY)) {
        c.walkToAsync(newX, newY, 2);
        c.sleep(640);
      } else {
        c.walkToAsync(newX, newY, 0);
        c.sleep(640);
      }
    }
  }

  public boolean isSellable(int id) {
    for (int itemId : itemIds) {
      if (itemId == id) return true;
    }

    return false;
  }

  public void scriptStart() {
    while (c.isRunning()) {
      if (c.getInventoryItemCount() < 30) {
        if (c.getNearestNpcByIds(npcId, false) == null) {
          startWalking(startX, startY);
        }
        if (c.getNearestNpcByIds(npcId, false) != null && !c.isInShop()) {
          if (npcId[0] != 54) {
            c.npcCommand1(c.getNearestNpcByIds(npcId, false).serverIndex);
            c.sleep(640);
          } else {
            c.npcCommand2(c.getNearestNpcByIds(npcId, false).serverIndex);
            c.sleep(640);
          }
        }
        if (c.isInShop() && c.getInventoryItemCount() < 30) {
          for (int itemId : itemIds) {
            if (itemId != 0
                && isSellable(itemId)
                && c.getShopItemCount(itemId) > shopNumber
                && c.getShopItemCount(itemId) > 0) {
              c.shopBuy(itemId, shopNumber - c.getShopItemCount(itemId));
              c.sleep(640);
            }
          }
          c.sleep(640);
        }
        checkAutowalk();
        c.sleep(640);
      }
      if (c.getInventoryItemCount() == 30) {
        startWalking(c.getNearestBank()[0], c.getNearestBank()[1]);
        if (c.getNearestNpcById(95, false) == null) {
          startWalking(c.getNearestBank()[0], c.getNearestBank()[1]);
        }
        if (!c.isInBank()) {
          c.openBank();
          c.sleep(640);
        }
        if (c.isInBank() && c.getInventoryItemCount() == 30) {
          for (int itemId : c.getInventoryItemIds()) {
            if (itemId != 0 && itemId != 10) {
              c.depositItem(itemId, c.getInventoryItemCount(itemId));
              c.sleep(640);
              purchased = purchased + c.getInventoryItemCount(itemId);
            }
          }
        }
        c.sleep(640);
      }
    }
  }

  private static void checkAutowalk() {
    if (System.currentTimeMillis() > next_attempt) {
      c.log("@red@Walking to Avoid Logging!");
      int x = c.currentX();
      int y = c.currentY();

      if (c.isReachable(x + 1, y, false)) c.walkTo(x + 1, y, 0, false);
      else if (c.isReachable(x - 1, y, false)) c.walkTo(x - 1, y, 0, false);
      else if (c.isReachable(x, y + 1, false)) c.walkTo(x, y + 1, 0, false);
      else if (c.isReachable(x, y - 1, false)) c.walkTo(x, y - 1, 0, false);
      c.sleep(GAME_TICK);
      next_attempt = System.currentTimeMillis() + nineMinutesInMillis;
      long nextAttemptInSeconds = (next_attempt - System.currentTimeMillis()) / 1000L;
      c.log("Done Walking to not Log, Next attempt in " + nextAttemptInSeconds + " seconds!");
    }
  }

  public void completeSetup() {
    if (items.getText().contains(",")) {
      for (String value : items.getText().replace(" ", "").split(",")) {
        this.itemIds = Arrays.copyOf(itemIds, itemIds.length + 1);
        this.itemIds[itemIds.length - 1] = Integer.parseInt(value);
      }
    } else {
      this.itemIds = new int[] {Integer.parseInt(items.getText())};
    }
    if (vendorId.getText().contains(",")) {
      for (String value : vendorId.getText().replace(" ", "").split(",")) {
        this.npcId = Arrays.copyOf(npcId, npcId.length + 1);
        this.npcId[npcId.length - 1] = Integer.parseInt(value);
      }
    } else {
      this.npcId = new int[] {Integer.parseInt(vendorId.getText())};
    }
    shopNumber = Integer.parseInt(shopCount.getText());
  }

  public void setupGUI() {
    JLabel header = new JLabel("Buy From Shop");
    JButton startScriptButton = new JButton("Start");
    JLabel itemsLabel = new JLabel("Item Ids to buy");
    JLabel shopCountLabel = new JLabel("Buy until shop has");
    JLabel vendorIdLabel = new JLabel("Shopkeeper ids");

    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          c.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
          completeSetup();
          c.displayMessage("@red@buyFromShop started");
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(vendorIdLabel);
    scriptFrame.add(vendorId);
    scriptFrame.add(itemsLabel);
    scriptFrame.add(items);
    scriptFrame.add(shopCountLabel);
    scriptFrame.add(shopCount);
    scriptFrame.add(startScriptButton);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      c.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
      c.drawString("@red@Buy from Shop @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      c.drawString("@red@Purchased items banked: @yel@" + this.purchased, 10, 35, 0xFFFFFF, 1);
      long timeRemainingTillAutoWalkAttempt = next_attempt - System.currentTimeMillis();
      c.drawString(
          "@red@Time till AutoWalk: @yel@" + c.msToShortString(timeRemainingTillAutoWalkAttempt),
          10,
          35 + 14,
          0xFFFFFF,
          1);
    }
  }
}
