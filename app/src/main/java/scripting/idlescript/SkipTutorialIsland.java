package scripting.idlescript;

public class SkipTutorialIsland extends IdleScript {
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    controller.skipTutorialIsland();
    controller.sleep(5000);
    controller.stop();
    controller.logout();
    System.exit(0);
    return 1000; // start() must return a int value now.
  }
}
