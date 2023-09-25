package scripting.idlescript;

public class FarmDropItemById extends IdleScript {
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {

    while (!controller.isLoggedIn()) return 100;

    int id = Integer.parseInt(parameters[0]);

    while (controller.getInventoryItemCount(id) > 0) {
      controller.dropItem(controller.getInventoryItemSlotIndex(id));
      controller.sleep(618);
    }

    controller.logout();
    controller.stop();
    System.exit(0);
    return 100;
  }
}
