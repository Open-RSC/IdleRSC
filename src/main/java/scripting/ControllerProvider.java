package scripting;

import controller.BotController;

public class ControllerProvider {
  private static BotController botController = null;

  public static void setBotController(BotController botController) {
    botController.debug("Adding BotController to ControllerProvider");
    ControllerProvider.botController = botController;
  }

  public static BotController getBotController() {
    if (botController == null) {
      throw new RuntimeException("BotController is not set! Please set it before using it!");
    }

    return botController;
  }
}
