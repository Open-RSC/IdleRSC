package scripting;

import controller.BotController;

import static scripting.ControllerProvider.getBotController;

public abstract class Task {
    protected BotController botController = getBotController();

    public Task() {
    }

    protected abstract void execute();

    public abstract int tickDelay();

}
