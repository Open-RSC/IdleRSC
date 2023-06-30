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
 * @author Searos
 */
public class BuyFromShop extends IdleScript {
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  int[] itemIds = {};
  int[] npcId = {};
  int shopNumber = -1;
  int startX = -1;
  int startY = -1;
  int purchased = 0;
  final JTextField items = new JTextField("");
  final JTextField shopCount = new JTextField("10");
  JTextField shopBuyCount = new JTextField("10");
  final JTextField vendorId =
      new JTextField("51,55,87,105,145,168,185,222,391,82,83,88,106,146,169,186,223");

  public int start(String[] parameters) {
    startX = controller.currentX();
    startY = controller.currentY();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
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
    while (controller.isRunning()) {
      while (controller.getInventoryItemCount() < 30) {
        while (controller.getNearestNpcByIds(npcId, false) == null) {
          startWalking(startX, startY);
        }
        while (controller.getNearestNpcByIds(npcId, false) != null && !controller.isInShop()) {
          if (npcId[0] != 54) {
            controller.npcCommand1(controller.getNearestNpcByIds(npcId, false).serverIndex);
            controller.sleep(640);
          } else {
            controller.npcCommand2(controller.getNearestNpcByIds(npcId, false).serverIndex);
            controller.sleep(640);
          }
        }
        while (controller.isInShop() && controller.getInventoryItemCount() < 30) {
          for (int itemId : itemIds) {
            while (itemId != 0
                && isSellable(itemId)
                && controller.getShopItemCount(itemId) > shopNumber
                && controller.getShopItemCount(itemId) > 0) {
              controller.shopBuy(itemId, shopNumber - controller.getShopItemCount(itemId));
              controller.sleep(430);
            }
          }
          controller.sleep(420);
        }
      }
      while (controller.getInventoryItemCount() == 30) {
        startWalking(controller.getNearestBank()[0], controller.getNearestBank()[1]);
        while (controller.getNearestNpcById(95, false) == null) {
          startWalking(controller.getNearestBank()[0], controller.getNearestBank()[1]);
        }
        while (!controller.isInBank()) {
          controller.openBank();
          controller.sleep(430);
        }
        while (controller.isInBank() && controller.getInventoryItemCount() == 30) {
          for (int itemId : controller.getInventoryItemIds()) {
            purchased = purchased + controller.getInventoryItemCount(itemId);
            if (itemId != 0 && itemId != 10) {
              controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
              controller.sleep(10);
            }
          }
        }
      }
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
    JLabel itemsLabel = new JLabel("Item Ids to buy");
    JLabel shopCountLabel = new JLabel("Buy until shop has");
    JLabel vendorIdLabel = new JLabel("Shopkeeper ids");

    startScriptButton.addActionListener(
        e -> {
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          controller.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
          completeSetup();
          controller.displayMessage("@red@buyFromShop started");
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
      controller.drawString("@red@Buy from Shop @gre@by Searos", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Purchased items banked: @yel@" + this.purchased, 10, 35, 0xFFFFFF, 1);
    }
  }
}
