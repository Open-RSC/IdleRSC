package scripting.idlescript.other.AIOAIO.fishing;

import bot.Main;
import models.entities.ItemId;

public class Fishing_Utils {
  public static void depositAllExceptNet() {
    for (int itemId : Main.getController().getInventoryItemIds()) {
      if (itemId != 0 && itemId != ItemId.NET.getId())
        Main.getController()
            .depositItem(itemId, Main.getController().getInventoryItemCount(itemId));
      Main.getController().sleep(500);
    }
  }
}
