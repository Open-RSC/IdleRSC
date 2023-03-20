package scripting.idlescript;

public class ObtainSleepingBag extends IdleScript {
  public int start(String[] parameters) {

    if (controller.isLoggedIn() && controller.getInventoryItemCount(1263) > 0) System.exit(0);

    if (controller.isInTrade() == false) {
      int serverIndex = controller.getPlayerServerIndexByName("bilbo baggin");

      if (serverIndex == -1) {
        controller.log("Giving player " + parameters[0] + " is not present!");
        return 2000;
      }

      controller.tradePlayer(serverIndex);
      return 1000;
    } else {
      if (controller.isInTradeConfirmation()) {
        controller.acceptTradeConfirmation();
        controller.log("Finished trading.");
        controller.sleep(5000);
      } else {
        controller.acceptTrade();
        return 1000;
      }

      return 1000;
    }
  }
}
