package scripting.idlescript;

/** MassGive by Dvorak. */
public class FarmMassAcceptShantay extends IdleScript {
  int itemId, amount;
  boolean stackable;
  boolean canTrade = true;

  @Override
  public void tradeMessageInterrupt(String player) {
    if (!controller.isInBank()
        && !controller.isInTrade()
        && controller.getInventoryItemCount() <= 18) {
      System.out.println("trading!");
      controller.tradePlayer(controller.getPlayerServerIndexByName(player));
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

    while (true) {
      if (controller.isInTrade()) {
        if (controller.isInTradeConfirmation()) {
          controller.acceptTradeConfirmation();
          controller.log("Finished trading.");
          controller.sleep(800);
        } else {
          controller.acceptTrade();
          controller.sleep(800);
        }
      }

      if (controller.getInventoryItemCount() > 12) {

        controller.openBank();

        while (controller.isInBank() && controller.getInventoryItemCount() > 0) {
          controller.depositItem(
              controller.getInventorySlotItemId(0),
              controller.getInventoryItemCount(controller.getInventorySlotItemId(0)));
          controller.sleep(250);
        }

        controller.closeBank();
      }
    }
  }
}
