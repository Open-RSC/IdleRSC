package scripting.idlescript.framework.tasks.banking;

import models.entities.ItemId;
import scripting.idlescript.framework.tasks.IdleTask;
import scripting.idlescript.framework.tasks.exception.IdleTaskStuckException;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class BankItems extends IdleTask {
    private final List<ItemId> itemIds;

    public BankItems(List<ItemId> itemIds) {
        this.itemIds = itemIds;
    }

    @Override
    protected void executeTask() {
        botController.setStatus("@red@Banking items..");
        botController.debug("Banking items..");
        if (!botController.playerApi.hasItemsInInventory(itemIds)) {
            return;
        }

        if (!botController.bankApi.areBankersVisible()) {
            throw new IdleTaskStuckException("No bankers visible");
        }

        if (!isInterfaceOpen()) {
            openBank();
        }

        if (!isInterfaceOpen()) {
            throw new IdleTaskStuckException("Failed to open bank");
        }

        depositOres();

        if (isInterfaceOpen()) {
            botController.bankApi.close();
        }

        if (!botController.playerApi.hasItemsInInventory(itemIds)) {
            throw new IdleTaskStuckException("Failed to bank items");
        }
    }

    private boolean isInterfaceOpen() {
        return botController.bankApi.isInterfaceOpen();
    }

    @Override
    public int tickDelay() {
        return 3;
    }

    private void openBank() {
        botController.debug("Opening bank..");
        botController.setStatus("@red@Opening bank..");
        botController.bankApi.open();
    }

    private void depositOres() {
        botController.debug("Depositing items..");
        botController.setStatus("@red@Depositing items..");
        botController.bankApi.deposit(itemIds.stream().map(ItemId::getId).collect(toList()));
    }

}
