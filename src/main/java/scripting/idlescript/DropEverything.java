package scripting.idlescript;

/**
 * This is a basic script which drops everything in your inventory.
 *
 * @author Dvorak
 */
public class DropEverything extends IdleScript {

  public int start(String parameters[]) {
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
