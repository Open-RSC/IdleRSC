package scripting.idlescript.framework.tasks;

import scripting.Task;

class Sleep extends Task {
  private static final int MAGIC_NUMBER_THAT_TRIES_TO_AVOID_CLIENT_WHITESCREEN_CRASH = 35;

  public Sleep() {
    super();
  }

  @Override
  public void execute() {
    if (botController.playerApi.isFatigueZero()) {
      botController.debug("Not tired, skipping sleep");
      return;
    }

    botController.debug("Sleeping..");
    sleep();
  }

  @Override
  public int tickDelay() {
    return MAGIC_NUMBER_THAT_TRIES_TO_AVOID_CLIENT_WHITESCREEN_CRASH;
  }

  private void sleep() {
    botController.playerApi.sleep();
    botController.sleepTicks(tickDelay());
  }
}
