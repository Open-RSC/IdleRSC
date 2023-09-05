package scripting.idlescript;

import com.openrsc.client.entityhandling.instances.Item;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** MassGive by Dvorak. */
public class FarmTradeEverything extends IdleScript {

  private void trade_sleep(int maxTicks) {
    int ticks = 0;

    while (ticks < maxTicks) {
      if (controller.isInTrade()) return;

      controller.sleep(10);
      ticks++;
    }
  }
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {

    controller.log(controller.getMudMouseCoords()[0] + "," + controller.getMudMouseCoords()[1]);

    if (controller.getInventoryItemCount() == 0) {
      controller.log("opening...");
      controller.openBank();
      controller.log("open");

      if (controller.isInBank()) {

        if (controller.getBankItemsCount() > 0) {

          while (controller.getInventoryItemCount() < 24) {
            List<Item> items = controller.getBankItems();

            if (items.size() == 0) {
              if (controller.getInventoryItemCount() == 0) {
                System.exit(0);
              }
              break;
            }

            int itemId = items.get(0).getCatalogID();
            int amount = items.get(0).getAmount();
            boolean stackable = items.get(0).getItemDef().stackable;

            if (!stackable && amount > 24) amount = 24;

            controller.withdrawItem(itemId, amount);
            controller.sleep(1000);
          }

          controller.closeBank();
        } else {
          System.exit(0);
        }
      }
    }

    if (controller.getInventoryItemCount() > 0 && !controller.isInTrade()) {
      int serverIndex = controller.getPlayerServerIndexByName(parameters[0]);

      if (serverIndex == -1) {
        controller.log("Receiving player " + parameters[0] + " is not present!");
        return 2000;
      }

      int[] coords = controller.getPlayerCoordsByServerIndex(serverIndex);
      controller.walkTo(coords[0], coords[1]);
      controller.tradePlayer(serverIndex);
      trade_sleep(700);
      return 0;
    } else {
      if (controller.isInTradeConfirmation()) {
        controller.acceptTradeConfirmation();
        controller.log("Finished trading.");
        controller.sleep(800);
      } else {

        int[] _itemIds = controller.getInventoryItemIds();
        HashSet<Integer> itemIds = new HashSet<>();
        ArrayList<Integer> tradeIds = new ArrayList<>();
        ArrayList<Integer> tradeAmounts = new ArrayList<>();

        // grab unique inventory ids
        for (int id : itemIds) {
          itemIds.add(id);
        }

        // keep track of how many items we're trading
        int totalSlots = 0;

        // create map for trade
        // for(int id : itemIds) {
        for (int i = 0; i < controller.getInventoryItemCount(); i++) {
          int id = controller.getInventorySlotItemId(i);
          int amount = controller.getInventoryItemCount(id);

          if (totalSlots >= 12) {
            break;
          }

          if (controller.isItemStackable(id)) {
            tradeIds.add(id);
            tradeAmounts.add(amount);
            totalSlots++;
          } else {
            tradeIds.add(id);
            tradeAmounts.add(1);
            totalSlots++;
          }
        }

        // convert map to idlersc api
        int[] finalTradeIds = new int[totalSlots];
        int[] finalTradeAmounts = new int[totalSlots];

        for (int i = 0; i < totalSlots; i++) {
          finalTradeIds[i] = tradeIds.get(i);
          finalTradeAmounts[i] = tradeAmounts.get(i);
        }

        controller.setTradeItems(finalTradeIds, finalTradeAmounts);
        controller.sleep(640);

        //				int itemId = controller.getInventorySlotItemId(0);
        //				int amount = controller.getInventoryItemCount(itemId);
        //				boolean stackable = controller.isItemStackable(itemId);
        //				if(stackable) {
        //					controller.setTradeItems(new int[] {itemId}, new int[] {amount});
        //					controller.sleep(1000);
        //				} else {
        //					itemId = controller.getInventorySlotItemId(0);
        //					amount = controller.getInventoryItemCount(itemId);
        //
        //					int[] items = new int[amount];
        //					int[] amounts = new int[amount];
        //
        //					for(int i = 0; i < amount; i++) {
        //						items[i] = itemId;
        //						amounts[i] = 1;
        //					}
        //
        //					controller.setTradeItems(items, amounts);
        //					controller.sleep(640); //changed -- good
        //				}
        controller.acceptTrade();
        return 800; // changed
      }

      return 1000;
    }
  }
}
