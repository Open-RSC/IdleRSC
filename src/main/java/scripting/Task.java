package scripting;

import static scripting.ControllerProvider.getBotController;

import controller.BotController;

public abstract class Task {
  protected BotController botController = getBotController();

  public Task() {}

  protected abstract void execute();

  public abstract int tickDelay();
}
