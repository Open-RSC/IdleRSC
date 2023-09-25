package scripting.idlescript;

import com.openrsc.client.entityhandling.instances.Item;
import java.util.*;

/** MassGive by Dvorak. */
public class FarmGetInventoryItems extends IdleScript {
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {

    if (controller.isLoggedIn()) {
      controller.sleep(1000);
      boolean atLeastOne = false;
      List<Item> _items = controller.getInventoryItems();
      HashSet<Item> items = new HashSet<>();
      String playerName = controller.getPlayerName();

      // create a unique list of items.
      for (Item item : _items) {
        items.add(item);
      }

      for (Item item : items) {
        String itemId = Integer.toString(item.getItemDef().id);
        String itemName = item.getItemDef().name;
        String amount = Integer.toString(item.getAmount());

        controller.log(playerName + " - " + itemName + " (ID: " + itemId + ")" + " - " + amount);

        atLeastOne = true;
      }

      if (!atLeastOne) {
        controller.log(playerName + " - inventory empty!");
      }

      System.exit(0);
    }

    return 1000;
  }
}
