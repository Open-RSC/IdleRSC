package scripting.idlescript.other.AIOAIO.woodcutting;

import bot.Main;
import controller.Controller;
import models.entities.SceneryId;
import scripting.idlescript.other.AIOAIO.AIOAIO;

public class Woodcut {
  private static Controller c;

  public static int run() {
    c = Main.getController();
    c.setBatchBarsOn();

    if (!Woodcutting_Utils.hasAxeInInventory()) return getAxe();
    if (c.getInventoryItemCount() >= 30) return bankLogs();
    if (c.isBatching()) return 250; // Wait to finish chopping
    if (c.getNearestReachableObjectById(getTreeId(), true) != null) return chopTree();
    else return findTrees();
  }

  private static int getTreeId() {
    switch (AIOAIO.state.currentMethod.getName()) {
      case "normal":
        return SceneryId.LEAFY_TREE.getId();
      case "oak":
        return SceneryId.TREE_OAK.getId();
      case "willow":
        return SceneryId.TREE_WILLOW.getId();
    }
    throw new IllegalStateException("Unknown tree type: " + AIOAIO.state.currentMethod.getName());
  }

  private static int findTrees() {
    switch (AIOAIO.state.currentMethod.getName()) {
      case "normal":
      case "oak":
        c.setStatus("Walking to Seers");
        c.walkTowards(500, 453);
      case "willow":
        c.setStatus("Walking to Seers");
        c.walkTowards(509, 442);
    }
    throw new IllegalStateException("Unknown tree type: " + AIOAIO.state.currentMethod.getName());
  }

  private static int chopTree() {
    c.setStatus("Chopping tree");
    int[] treeCoords = c.getNearestReachableObjectById(getTreeId(), true);
    c.atObject(treeCoords[0], treeCoords[1]);
    return 1200;
  }

  private static int bankLogs() {
    if (c.getNearestNpcById(95, false) == null) {
      c.setStatus("Walking to Bank");
      c.walkTowards(c.getNearestBank()[0], c.getNearestBank()[1]);
      return 50;
    }
    c.setStatus("Opening bank");
    c.openBank();
    Woodcutting_Utils.depositAllExceptAxe();
    c.closeBank();
    return 680;
  }

  private static int getAxe() {
    if (!AIOAIO.state.hasAxeInBank) return getAxeFromFaladorSpawn();
    return getAxeFromBank();
  }

  private static int getAxeFromBank() {
    if (c.getNearestNpcById(95, false) == null) {
      c.setStatus("Walking to Bank");
      c.walkTowards(c.getNearestBank()[0], c.getNearestBank()[1]);
      return 100;
    }
    c.setStatus("Opening bank");
    c.openBank();
    if (!Woodcutting_Utils.hasAxeInBank()) {
      System.out.println("No axe in bank, gotta get one..");
      // Next loop will grab it from Falador Spawn
      AIOAIO.state.hasAxeInBank = false;
      c.closeBank();
      return 100;
    }
    c.setStatus("Withdrawing axe");
    Woodcutting_Utils.withdrawAxeFromBank();
    c.closeBank();
    return 680;
  }

  private static int getAxeFromFaladorSpawn() {
    if (c.isInBank()) c.closeBank();
    if (c.pickupItem(87)) {
      c.setStatus("Picking up axe");
      for (int i = 0; i < 10000; i += 1000) {
        if (Woodcutting_Utils.hasAxeInInventory()) break;
        c.sleep(1000);
      }
      c.setStatus("Going down ladder");
      c.atObject(308, 1466);
      return 5000;
    }
    if (c.distanceTo(308, 522) < 5) {
      c.setStatus("Going up ladder");
      c.atObject(308, 522); // Climb ladder
      return 1000;
    }
    c.setStatus("Walking to axe");
    c.walkTowards(308, 523);
    return 50;
  }
}
