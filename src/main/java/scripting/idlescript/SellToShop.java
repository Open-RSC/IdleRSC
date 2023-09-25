package scripting.idlescript;

import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * SellToShop by Searos
 *
 * @author Searos
 */
public class SellToShop extends IdleScript {
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  int[] itemIds = {};
  int[] npcId = {};
  int bankedCash = 0;
  int shopNumber = -1;
  int startX = -1;
  int startY = -1;
  int startCash = 0;
  int cashMade = 0;
  int totalCash = 0;
  final JTextField items = new JTextField("");
  final JTextField shopCount = new JTextField("10");
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
    if (controller.getInventoryItemCount(10) > 0) {
      startCash = controller.getInventoryItemCount(10);
    }
    startX = controller.currentX();
    startY = controller.currentY();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    while (scriptStarted && controller.isRunning()) {
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void startWalking(int x, int y) {
    // shitty autowalk
    int newX = x;
    int newY = y;
    while (controller.currentX() != x || controller.currentY() != y) {
      if (controller.currentX() - x > 23) {
        newX = controller.currentX() - 20;
      }
      if (controller.currentY() - y > 23) {
        newY = controller.currentY() - 20;
      }
      if (controller.currentX() - x < -23) {
        newX = controller.currentX() + 20;
      }
      if (controller.currentY() - y < -23) {
        newY = controller.currentY() + 20;
      }
      if (Math.abs(controller.currentX() - x) <= 23) {
        newX = x;
      }
      if (Math.abs(controller.currentY() - y) <= 23) {
        newY = y;
      }
      if (!controller.isTileEmpty(newX, newY)) {
        controller.walkToAsync(newX, newY, 2);
        controller.sleep(640);
      } else {
        controller.walkToAsync(newX, newY, 0);
        controller.sleep(640);
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
    while (controller.getNearestNpcByIds(npcId, false) == null) {
      controller.setStatus("Selling");
      startWalking(startX, startY);
    }
    while (controller.getNearestNpcByIds(npcId, false) != null && !controller.isInShop()) {
      controller.setStatus("Selling");
      controller.npcCommand1(controller.getNearestNpcByIds(npcId, false).serverIndex);
      controller.sleep(640);
    }
    while (controller.isInShop() && controller.getInventoryItemCount() > 1
        || controller.isInShop()
            && controller.getInventoryItemCount(10) < 1
            && controller.getInventoryItemCount() == 1) {
      for (int itemId : itemIds) {
        if (itemId != 0 && isSellable(itemId) && controller.getShopItemCount(itemId) < shopNumber) {
          controller.shopSell(itemId, shopNumber - controller.getBankItemCount(itemId));
          controller.sleep(640);
          cashMade = controller.getInventoryItemCount(10) - startCash;
        }
      }
    }
    while (controller.getInventoryItemCount() == 1 && controller.getInventoryItemCount(10) > 0
        || controller.getInventoryItemCount() == 0) {
      startWalking(controller.getNearestBank()[0], controller.getNearestBank()[1]);
      while (controller.getNearestNpcById(95, false) == null) {
        controller.setStatus("Banking");
        startWalking(controller.getNearestBank()[0], controller.getNearestBank()[1]);
      }
      while (!controller.isInBank()) {
        controller.setStatus("Banking");
        controller.openBank();
        controller.sleep(430);
      }
      if (controller.isInBank()) {
        controller.setStatus("Banking");
        totalCash = totalCash + controller.getInventoryItemCount(10);
        controller.depositItem(10, controller.getInventoryItemCount(10));
        startCash = 0;
        for (int itemId : itemIds) {
          if (itemId != 0
              && isSellable(itemId)
              && controller.getShopItemCount(itemId) < shopNumber) {
            controller.withdrawItem(itemId, 30);
            controller.sleep(640);
          }
        }
        bankedCash = controller.getBankItemCount(10);
      }
    }
    if (!controller.isLoggedIn()) {
      controller.sleep(1000);
    }
    if (!controller.isRunning()) {
      guiSetup = false;
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
    JLabel header = new JLabel("Sell To Shop");
    JButton startScriptButton = new JButton("Start");
    JLabel itemsLabel = new JLabel("Item Ids to sell");
    JLabel shopCountLabel = new JLabel("Max number the shop can already have");
    JLabel vendorIdLabel = new JLabel("Shopkeeper ids");

    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
          completeSetup();
          controller.displayMessage("@red@SelltoShop started");
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
      controller.drawBoxAlpha(7, 7, 128, 21 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@Sell to Shop @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Profit this inventory: @yel@" + this.cashMade, 10, 35, 0xFFFFFF, 1);
      controller.drawString("@red@Gold banked: @yel@" + this.totalCash, 10, 49, 0xFFFFFF, 1);
      controller.drawString("@red@Gold in bank: @yel@" + this.bankedCash, 10, 49 + 14, 0xFFFFFF, 1);
    }
  }
}
