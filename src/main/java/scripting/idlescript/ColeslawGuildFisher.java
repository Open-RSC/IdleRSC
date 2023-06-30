package scripting.idlescript;

/**
 * ColeslawGuildFisher by [unknown author]. Coleslaw only.
 *
 * @author Searos
 */
public class ColeslawGuildFisher extends IdleScript {
  static final int HARPOON_ID = 379;
  static final int LOBSTER_POT_ID = 375;
  static final int SHARK_FISH_SPOT = 261;
  static final int LOBSTER_FISH_SPOT = 376;

  int equipId = HARPOON_ID;
  int spotId = SHARK_FISH_SPOT;

  public int start(String[] parameters) {
    if (!controller.isAuthentic() && !orsc.Config.C_BATCH_PROGRESS_BAR)
      controller.toggleBatchBars();
    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("lobster")) {
        controller.displayMessage("Got param " + parameters[0] + ". Fishing Lobsters!", 0);
        equipId = LOBSTER_POT_ID;
        spotId = LOBSTER_FISH_SPOT;
      } else {
        controller.displayMessage("Got param " + parameters[0] + ". Fishing Sharks!", 0);
      }
      scriptStart();
    }

    controller.displayMessage("@cya@No parameters entered", 0);

    if (controller.getInventoryItemCount(HARPOON_ID) >= 1) {
      controller.displayMessage("Because you have a harpoon, I'm assuming you want sharks.", 0);
    } else if (controller.getInventoryItemCount(LOBSTER_POT_ID) >= 1) {
      controller.displayMessage(
          "Because you have a lobster pot, I'm assuming you want lobsters.", 0);
      equipId = LOBSTER_POT_ID;
      spotId = LOBSTER_FISH_SPOT;
    } else {
      controller.displayMessage(
          "@red@Please grab either a harpoon or lobster pot, and use the parameter \"Lobster\" or \"Shark\".");
      controller.stop();
      return 1000;
    }
    controller.displayMessage(
        "If you want to override this, either put the parameter \"Lobster\" or \"Shark\"!", 0);
    controller.sleep(5000);

    scriptStart();

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (controller.isRunning()) {
      if (controller.getInventoryItemCount() >= 30) {
        handleFullInven();
      }

      if (controller.getInventoryItemCount() < 30) {
        handleFishing();
      }

      controller.sleep(600);
    }
  }

  private void handleFishing() {
    if (!controller.isBatching()) {
      int[] fishingSpot = controller.getNearestObjectById(spotId);

      try {
        if (spotId == LOBSTER_FISH_SPOT) {
          controller.atObject(fishingSpot[0], fishingSpot[1]);
        } else {
          controller.atObject2(fishingSpot[0], fishingSpot[1]);
        }
      } catch (NullPointerException ignored) {
        // Spot disappeared!
      }
    }
  }

  private void handleFullInven() {
    if (controller.isInBank()) {
      for (int itemId : controller.getInventoryItemIds()) {
        if (itemId != 0 && itemId != equipId && controller.getInventoryItemCount(itemId) > 0) {
          controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
        }
      }
    } else {
      controller.openBank();
    }
  }
}
