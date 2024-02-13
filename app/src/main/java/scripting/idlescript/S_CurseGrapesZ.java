package scripting.idlescript;

/**
 * Zammy Curse Grapes by spilk
 *
 * @author spilk
 */
public class S_CurseGrapesZ extends IdleScript {
  boolean guiSetup = false;
  boolean scriptStarted = true;
  int[] bankLoc = {588, 754}; // Yanille

  int grapesBank = 0;
  int cursedBank = 0;
  int grapesCursed = 0;
  int zamGrapeID = 1466;
  int agilityCapeID = 1518;
  int grapeID = 143;
  int unholyID = 1029;
  final long startTimestamp = System.currentTimeMillis() / 1000L;

  public int start(String parameters[]) {
    if (!guiSetup) {
      guiSetup = true;
    }
    if (scriptStarted) {
      startSequence();
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void startSequence() {
    controller.displayMessage("@red@Grape Curser by spilk");
    controller.displayMessage("@red@Start Anywhere Bank with Agil cape (:D),");
    controller.displayMessage("@red@blessed zammy symbol, zammy robe, robe skirt");
    controller.displayMessage("@red@and completed observatory quest + 28+ Prayer");
    controller.sleep(710);
  }

  public void scriptStart() {

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      teleToBank();
      bank();
      walkToGrave();
      curseGrapes();
    }
  }

  public void teleToBank() {
    controller.setStatus("@gre@Tele then walk to bank in Yanille...");
    controller.itemCommand(agilityCapeID);
    controller.sleep(710);
    controller.walkTo(588, 754); // Yanille Bank
  }

  public void walkToGrave() {
    controller.setStatus("@gre@Walking to grave..");
    controller.walkTo(587, 732);
    controller.walkTo(587, 716);
    controller.walkTo(598, 676);
    controller.walkTo(613, 648); // Check this one
    controller.walkTo(626, 636);
    controller.walkTo(649, 642);
    controller.walkTo(660, 642);
    controller.walkTo(675, 645);
    controller.walkTo(684, 636);
    controller.walkTo(695, 637);
    controller.walkTo(700, 649);
    controller.setStatus("@red@Recharging Prayer");
    controller.sleep(100);
    controller.atObject(699, 650); // Recharge Prayer
    controller.sleep(700);
  }

  public int countLoot() {
    int count = 0;
    count += controller.getInventoryItemCount(zamGrapeID);
    return count;
  }

  public void bank() {

    controller.setStatus("@yel@Banking..");
    controller.openBank();
    controller.sleep(640);

    grapesCursed += controller.getInventoryItemCount(zamGrapeID);

    if (controller.isInBank()) {

      while (countLoot() > 0) {
        if (controller.getInventoryItemCount(zamGrapeID) > 0) {
          controller.depositItem(
              zamGrapeID,
              controller.getInventoryItemCount(zamGrapeID)); // /////////////////////////////
          controller.sleep(250);
        }
      }
      grapesBank = controller.getBankItemCount(grapeID);
      cursedBank = controller.getBankItemCount(zamGrapeID);
      controller.sleep(100);
      controller.withdrawItem(grapeID, 28);
      controller.sleep(100);
      controller.closeBank();
    }
  }

  public void curseGrapes() {
    controller.setStatus("@gre@Cursing Grapes..");
    controller.useItemOnItemBySlot(
        controller.getInventoryItemSlotIndex(grapeID),
        controller.getInventoryItemSlotIndex(unholyID));
    controller.sleep(4500);
    while (controller.isBatching()) {
      controller.sleep(100);
    }
  }
}
