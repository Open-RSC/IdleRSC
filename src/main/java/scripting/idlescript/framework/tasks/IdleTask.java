package scripting.idlescript.framework.tasks;

import scripting.Task;

public abstract class IdleTask extends Task {
  private final Sleep sleep = new Sleep();
  private static final int FATIGUE_THRESHOLD = 95;

  public IdleTask() {
    super();
  }

  @Override
  protected final void execute() {
    if (botController.playerApi.isSleeping()) {
      botController.debug("Player is sleeping, skipping task");
      return;
    }

    if (botController.playerApi.getFatigue() > FATIGUE_THRESHOLD) {
      sleep.execute();
    }

    executeTask();
  }

  protected abstract void executeTask();
}
