package scripting.idlescript.framework.tasks.inventory;

import java.util.List;
import java.util.Optional;
import models.entities.ItemId;
import scripting.idlescript.framework.tasks.IdleTask;
import scripting.idlescript.framework.tasks.exception.IdleTaskStuckException;

public class DropItems extends IdleTask {
  private final List<ItemId> itemIds;

  public DropItems(List<ItemId> itemIds) {
    this.itemIds = itemIds;
  }

  @Override
  public int tickDelay() {
    return 1;
  }

  @Override
  protected void executeTask() {
    if (!botController.playerApi.hasItemsInInventory(itemIds)) {
      throw new IdleTaskStuckException("No items to drop");
    }

    while (botController.playerApi.hasItemsInInventory(itemIds) && botController.sleepTicks(1)) {
      dropItems();
    }
  }

  private void dropItems() {
    botController.setStatus("Dropping items..");
    botController.debug("Dropping items..");
    itemIds.stream()
        .map(id -> botController.playerApi.getInventorySlotIndex(id))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .ifPresent(index -> botController.playerApi.dropInventoryItem(index));
  }
}
