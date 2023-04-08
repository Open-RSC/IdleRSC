package scripting.idlescript;

public class SkipTutorialIsland extends IdleScript {

  public int start(String[] parameters) {
    controller.skipTutorialIsland();
    controller.sleep(5000);
    controller.stop();
    controller.logout();
    System.exit(0);
    return 1000; // start() must return a int value now.
  }
}
