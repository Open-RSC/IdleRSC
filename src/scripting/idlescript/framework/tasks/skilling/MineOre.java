package scripting.idlescript.framework.tasks.skilling;

import java.util.List;
import java.util.Optional;
import models.entities.Interactable;
import models.entities.Rock;
import scripting.idlescript.framework.tasks.IdleTask;
import scripting.idlescript.framework.tasks.exception.IdleTaskStuckException;

import static java.util.stream.Collectors.toList;

public class MineOre extends IdleTask {
    private final List<Rock> rocks;

    public MineOre(List<Rock> rocks) {
        this.rocks = getSortedRocks(rocks);
    }

    @Override
    public int tickDelay() {
        return 3;
    }

    @Override
    protected void executeTask() {
        Optional<Interactable> rock = findRock(rocks);
        if (!rock.isPresent()) {
            throw new IdleTaskStuckException("No rocks found");
        }

        mineOre(rock.get());
    }

    private Optional<Interactable> findRock(List<Rock> rocks) {
        Optional<Rock> rock = rocks.stream().findFirst();
        if (!rock.isPresent()) {
            return Optional.empty();
        }

        Optional<Interactable> mineableRock = botController.environmentApi.getNearestInteractable(rock.get());
        return mineableRock.isPresent() ? mineableRock : findRock(rocks.subList(1, rocks.size()));
    }

    private static List<Rock> getSortedRocks(List<Rock> rocks) {
        return rocks.stream().sorted((o1, o2) -> o2.ordinal() - o1.ordinal()).collect(toList());
    }

    private void mineOre(Interactable rock) {
        botController.setStatus("@red@Mining..");
        botController.debug("Mining..");
        botController.playerApi.interact(rock);
    }

}
