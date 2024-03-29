package scripting.idlescript.other.AIOAIO.woodcut;

import bot.Main;

public class Woodcutting_Utils {
  public static final int[] axes = {1263, 405, 204, 203, 88, 87, 12};

  public static boolean hasAxeInInventory() {
    // Check if any of the axes is in the inventory OR equipt
    for (int axe : axes) {
      if (Main.getController().isItemInInventory(axe)
          || Main.getController().isItemIdEquipped(axe)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasAxeInBank() {
    if (!Main.getController().isInBank()) throw new IllegalStateException("Not in bank");
    // Check if any of the axes is in the bank
    for (int axe : axes) {
      if (Main.getController().isItemInBank(axe)) {
        return true;
      }
    }
    return false;
  }

  public static void withdrawAxeFromBank() {
    for (int axe : axes) {
      if (Main.getController().isItemInBank(axe)) {
        Main.getController().withdrawItem(axe);
        return;
      }
    }
  }

  public static boolean isAxe(int itemId) {
    for (int axe : axes) {
      if (itemId == axe) {
        return true;
      }
    }
    return false;
  }
}
