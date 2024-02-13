package scripting.idlescript;

import orsc.ORSCharacter;

/**
 * This is a basic script that attacks monks and heals using monks.
 *
 * @author Dvorak
 */
public class Monkz extends IdleScript {
  int fightMode = -1;
  boolean doBuryBones = false;
  int prayerId = -1;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {

    if (parameters.length != 3) {
      controller.displayMessage("@red@Script args: fightmode doburybones prayerid");
      controller.displayMessage("@red@example: 0 true 10");
      controller.stop();
    } else {
      controller.displayMessage("@dor@Monkz by Dvorak");
      fightMode = Integer.parseInt(parameters[0]);
      doBuryBones = Boolean.parseBoolean(parameters[1]);
      prayerId = Integer.parseInt(parameters[2]);
    }

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      controller.setFightMode(Integer.parseInt(parameters[0]));
      openDoor();
      pray();
      heal();
      if (doBuryBones) {
        buryBones();
      }
      attack();
      controller.sleep(1000);
    }

    return 1000; // start() must return a int value now.
  }

  public void pray() {
    if (controller.getCurrentStat(controller.getStatId("Prayer")) == 0) {
      int[] coords = controller.getNearestObjectById(19);
      if (coords != null) {
        controller.atObject(coords[0], coords[1]);
        controller.sleep(1000);
      }
    } else {
      if (controller.isInCombat() && !controller.isPrayerOn(prayerId)) {
        controller.enablePrayer(prayerId);
        controller.sleep(618);
      } else if (!controller.isInCombat() && controller.isPrayerOn(prayerId)) {
        controller.disablePrayer(prayerId);
        controller.sleep(618);
      }
    }
  }

  public void buryBones() {
    if (!controller.isInCombat()) {
      int[] coords = controller.getNearestItemById(20);
      if (coords != null) {
        controller.pickupItem(coords[0], coords[1], 20, true, false);
        controller.sleep(618);
      }
      if (controller.getInventoryItemCount(20) > 0) {
        controller.setStatus("@red@Burying bones..");
        controller.itemCommand(20);

        controller.sleep(618);
        buryBones();
      }
    }
  }

  public void heal() {
    while (controller.getCurrentStat(controller.getStatId("Hits")) < 10) {
      controller.setStatus("@dor@Healing");
      while (controller.isInCombat()) {
        controller.walkTo(controller.currentX(), controller.currentY());
        controller.sleep(618);
      }

      ORSCharacter npc = controller.getNearestNpcById(93, false);
      if (npc != null) {
        controller.talkToNpc(npc.serverIndex);
        controller.sleep(3000);

        if (controller.isInOptionMenu()) {
          controller.optionAnswer(0);
          controller.sleep(5000);
        }
      }
      controller.sleep(1000);
    }
  }

  public void attack() {
    if (controller.getCurrentStat(controller.getStatId("Prayer")) != 0) {
      if (!controller.isInCombat()) {
        ORSCharacter npc = controller.getNearestNpcById(93, false);
        if (npc != null) {
          controller.setStatus("@dor@Attacking");
          controller.attackNpc(npc.serverIndex);
        }
      }
    }
  }

  public void openDoor() {
    int[] coords = null;
    do {

      coords = controller.getNearestObjectById(64);
      if (coords != null) {
        controller.setStatus("@dor@Opening door");
        controller.atObject(coords[0], coords[1]);
        controller.sleep(618);
      }
    } while (coords != null);
  }

  //    @Override
  //    public void paintInterrupt() {
  //        if(controller != null) {
  //        	int flaxPerHr = 0;
  //        	try {
  //        		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
  //        		float scale = (60 * 60) / timeRan;
  //        		flaxPerHr = (int)(flaxPicked * scale);
  //        	} catch(Exception e) {
  //        		//divide by zero
  //        	}
  //            controller.drawBoxAlpha(7, 7, 155, 21+14+14, 0x00FFFF, 128);
  //            controller.drawString("@dgr@Flax@cya@x0r @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
  //            controller.drawString("@dgr@Flax picked: @cya@" + String.format("%,d", flaxPicked) +
  // " @gre@(@cya@" + String.format("%,d", flaxPerHr) + "@gre@/@cya@hr@gre@)", 10, 21+14, 0xFFFFFF,
  // 1);
  //            controller.drawString("@dgr@Flax in bank: @cya@" + String.format("%,d", flaxBanked),
  // 10, 21+14+14, 0xFFFFFF, 1);
  //        }
  //    }
}
