package scripting.idlescript.framework.tasks;

import scripting.Task;

class Sleep extends Task {
    public Sleep() {
        super();
    }

    @Override
    public void execute() {
        if (botController.playerApi.isFatigueZero() && botController.playerApi.isSleeping()) {
            botController.debug("Sleeping, but interface seems to be open. Attempting to close it..");
            executeSleep();
            return;
        }

        if (botController.playerApi.isFatigueZero()) {
            botController.debug("Not tired, skipping sleep");
            return;
        }

        if (botController.playerApi.isSleeping()) {
            botController.debug("Already sleeping");
            executeSleep();
            return;
        }

        botController.debug("Sleeping..");
        executeSleep();
    }

    private void executeSleep() {
        sleep();
        execute();
    }

    private void sleep() {
        botController.playerApi.sleep();
        botController.sleepTicks(tickDelay());
    }

    @Override
    public int tickDelay() {
        return 2;
    }

}
