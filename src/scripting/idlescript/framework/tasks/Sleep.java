package scripting.idlescript.framework.tasks;

import scripting.Task;

class Sleep extends Task {
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

    private void sleep() {
        botController.playerApi.sleep();
        botController.sleepTicks(tickDelay());
    }

    @Override
    public int tickDelay() {
        return 40;
    }

}
