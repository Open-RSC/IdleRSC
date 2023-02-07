package scripting.idlescript.framework.tasks.skilling;

import models.entities.Interactable;
import models.entities.Rock;
import scripting.idlescript.framework.tasks.IdleTask;
import scripting.idlescript.framework.tasks.exception.IdleTaskStuckException;

import java.util.List;
import java.util.Optional;

public class MineOre extends IdleTask {
    private final List<Rock> rocks;

    public MineOre(List<Rock> rocks) {
        this.rocks = rocks;
    }

    @Override
    protected void executeTask() {
        Optional<Interactable> rock = botController.environmentApi.getNearestInteractable(rocks);
        if (!rock.isPresent()) {
            throw new IdleTaskStuckException("No rocks found");
        }

        mineOre(rock.get());
    }

    private void mineOre(Interactable rock) {
        botController.setStatus("@red@Mining..");
        botController.debug("Mining..");
        botController.playerApi.interact(rock);
    }

    @Override
    public int tickDelay() {
        return 3;
    }

}
