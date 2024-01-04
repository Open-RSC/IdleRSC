package scripting.idlescript;

/**
 * This is a basic script which drops everything in your inventory.
 *
 * @author Dvorak
 */
public class DropEverything extends IdleScript {
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    controller.displayMessage("@red@DropEverything by Dvorak. Let's party like it's 2018!");
    controller.displayMessage("Dropping everything in inventory...");

    while (controller.getInventoryItemCount() > 0) {
      controller.dropItem(0);
    }

    controller.displayMessage("Done.");
    controller.stop();

    return 1000; // start() must return a int value now.
  }
}
