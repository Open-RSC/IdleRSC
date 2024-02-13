package scripting.idlescript;

/**
 * Trains Harvesting on Coleslaw in Draynor and Ardougne fields.
 *
 * @author Dvorak
 */
public class HarvesterTrainer extends IdleScript {

  int harvested = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    controller.displayMessage("@red@HarvesterTrainer by Dvorak. Let's party like it's 2004!");
    controller.displayMessage("@red@If less than 85 harvesting, start in Draynor/Lumbridge field.");
    controller.displayMessage("@red@If >85 harvesting, start in Ardougne field.");
    controller.quitIfAuthentic();

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      // pots and garlic are in the same western patch
      int objectId = 1265; // potatoes

      if (controller.getBaseStat(19) >= 9) objectId = 1267; // garlic

      if (controller.getBaseStat(19) >= 20) objectId = 1269; // corn

      if (controller.getBaseStat(19) >= 60) objectId = 1263; // red cabbage

      if (controller.currentX() > 450 && controller.getBaseStat(19) >= 85) objectId = 1264;

      if ((objectId == 1269 || objectId == 1263) && controller.currentX() > 179) {
        controller.walkTo(178, 607);
        controller.walkTo(170, 609);
      }
      int[] coords = controller.getNearestObjectById(objectId);
      if (coords != null) {
        controller.setStatus("@yel@Harvesting...");
        controller.atObject(coords[0], coords[1]);
        controller.sleep(1280);

        while (controller.isBatching()) {
          controller.sleep(640);
        }
      } else {
        controller.setStatus("@yel@Waiting for spawn..");
      }

      controller.sleep(100);
    }

    return 1000; // start() must return a int value now.
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You get")) harvested++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int harvestedPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        harvestedPerHr = (int) (harvested * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 190, 21 + 14, 0x228B22, 128);
      controller.drawString("@yel@HarvesterTrainer @whi@by @yel@Dvorak", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@yel@Stuff Harvested: @whi@"
              + String.format("%,d", harvested)
              + " @yel@(@whi@"
              + String.format("%,d", harvestedPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
    }
  }
}
