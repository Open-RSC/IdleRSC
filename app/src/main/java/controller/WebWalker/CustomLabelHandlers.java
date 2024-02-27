package controller.WebWalker;

import bot.Main;
import models.entities.NpcId;
import models.entities.SceneryId;
import orsc.ORSCharacter;

public class CustomLabelHandlers {
  public static boolean alkharidGate() {
    // Only even gets considered if we have 10gp or quest is complete, so we don't
    // have to handle walking around case manually
    boolean entering = Main.getController().currentX() >= 92;
    ORSCharacter npc =
        Main.getController()
            .getNearestNPCByLambda(
                n ->
                    n.npcId
                        == (entering
                            ? NpcId.BORDER_GUARD_LUMBRIDGE.getId()
                            : NpcId.BORDER_GUARD_ALKHARID.getId()));

    Main.getController().talkToNpc(npc.serverIndex);
    Main.getController().sleep(6000);
    if (!Main.getController().isQuestComplete(9)) {
      Main.getController().optionAnswer(2);
    }
    Main.getController()
        .sleepUntil(
            () ->
                entering
                    ? Main.getController().currentX() < 92
                    : Main.getController().currentX() >= 92,
            15000);

    return entering ? Main.getController().currentX() < 92 : Main.getController().currentX() >= 92;
  }

  public static boolean lummyNorthCowGate() {
    boolean goingNorth = Main.getController().currentY() >= 593;
    if (Main.getController().getObjectAtCoord(154, 593) != -1) {
      Main.getController().log("Opening Lummy North Cow Gate");
      Main.getController().atObject(154, 593);
      Main.getController().sleep(1000);
    }
    Main.getController().walkTo(155, goingNorth ? 592 : 593);
    return goingNorth
        ? Main.getController().currentY() <= 593
        : Main.getController().currentY() >= 593;
  }

  public static boolean lummyNorthCabbageGate() {
    boolean goingNorth = Main.getController().currentY() >= 597;
    if (Main.getController().getObjectAtCoord(148, 596) != -1) {
      Main.getController().log("Opening Lummy North Cabbage Fence Gate");
      Main.getController().atObject(148, 596);
      Main.getController().sleep(1000);
    }
    Main.getController().walkTo(148, goingNorth ? 596 : 597);
    return goingNorth
        ? Main.getController().currentY() <= 596
        : Main.getController().currentY() >= 597;
  }

  public static boolean lummyEastCowGate() {
    boolean goingEast = Main.getController().currentX() >= 105;
    if (Main.getController().getObjectAtCoord(105, 619) == 60) {
      Main.getController().log("Opening Lummy East Cow Gate");
      Main.getController().atObject(105, 619);
      Main.getController().sleep(1000);
    }
    if (Main.getController().getObjectAtCoord(105, 619) == 60) {
      Main.getController().log("Failed to open :C");
      return false;
    }
    Main.getController().walkTo(goingEast ? 104 : 105, 619);
    return goingEast
        ? Main.getController().currentX() <= 104
        : Main.getController().currentX() >= 105;
  }

  public static boolean southFallyTavGate() {
    boolean goingNorth = Main.getController().currentY() >= 581;
    if (!goingNorth) {
      // idk, it's finnicky
      Main.getController().walkTo(343, 580);
      Main.getController()
          .sleepUntil(
              () ->
                  Main.getController().currentX() == 343 && Main.getController().currentY() == 580,
              10000);
    }
    Main.getController().log("Handling South Fally Gate.. Going North? " + goingNorth);
    Main.getController().atObject(343, 581);
    Main.getController().log("Finished clicking object at 343, 581");
    Main.getController()
        .sleepUntil(
            () ->
                goingNorth
                    ? Main.getController().currentY() < 580
                    : Main.getController().currentY() >= 581,
            10000);
    Main.getController().sleep(680);
    return goingNorth
        ? Main.getController().currentY() < 580
        : Main.getController().currentY() >= 581;
  }

  public static boolean northFallyTavGate() {
    boolean goingEast = Main.getController().currentX() >= 342;
    Main.getController().atObject(137);
    Main.getController()
        .sleepUntil(
            () ->
                goingEast
                    ? Main.getController().currentX() < 342
                    : Main.getController().currentX() >= 342,
            10000);
    Main.getController().sleep(680);
    return goingEast
        ? Main.getController().currentX() < 342
        : Main.getController().currentX() >= 342;
  }

  public static boolean lummySheepEastGate() {
    boolean goingEast = Main.getController().currentX() >= 136;
    if (Main.getController().getObjectAtCoord(135, 630) == 60) {
      Main.getController().log("Opening Lummy Sheep East Gate");
      Main.getController().atObject(135, 630);
      Main.getController().sleep(1000);
    }
    if (Main.getController().getObjectAtCoord(135, 630) == 60) {
      Main.getController().log("Failed to open :C");
      return false;
    }
    Main.getController().walkTo(goingEast ? 135 : 136, 630);
    return goingEast
        ? Main.getController().currentX() < 135
        : Main.getController().currentX() >= 136;
  }

  public static boolean lummyNorthSheepGate() {
    boolean goingNorth = Main.getController().currentY() >= 616;
    if (Main.getController().getObjectAtCoord(152, 615) == 60) {
      Main.getController().log("Opening Lummy North Sheep Gate");
      Main.getController().atObject(152, 615);
      Main.getController().sleep(1000);
    }
    if (Main.getController().getObjectAtCoord(152, 615) == 60) {
      Main.getController().log("Failed to open :C");
      return false;
    }
    Main.getController().walkTo(152, goingNorth ? 615 : 616);
    return goingNorth
        ? Main.getController().currentY() <= 615
        : Main.getController().currentY() >= 616;
  }

  public static boolean gerrantHouseDoor() {
    boolean goingNorth = Main.getController().currentY() >= 651;
    if (Main.getController().getWallObjectIdAtCoord(277, 651) == 2) {
      Main.getController().log("Opening Gerrant House Door");
      Main.getController().atWallObject(277, 651);
      Main.getController().sleep(1000);
    }
    if (Main.getController().getWallObjectIdAtCoord(277, 651) == 2) {
      Main.getController().log("Failed to open :C");
      return false;
    }
    Main.getController().walkTo(277, goingNorth ? 650 : 651);
    return goingNorth
        ? Main.getController().currentY() <= 650
        : Main.getController().currentY() >= 651;
  }

  public static boolean catherbyChefDoor() {
    boolean goingNorth = Main.getController().currentY() >= 486;
    if (Main.getController().getWallObjectIdAtCoord(435, 486) == 2) {
      Main.getController().log("Opening Catherby Chef Door");
      Main.getController().atWallObject(435, 486);
      Main.getController().sleep(1000);
    }
    if (Main.getController().getWallObjectIdAtCoord(435, 486) == 2) {
      Main.getController().log("Failed to open :C");
      return false;
    }
    Main.getController().walkTo(435, goingNorth ? 485 : 486);
    return goingNorth
        ? Main.getController().currentY() <= 485
        : Main.getController().currentY() >= 486;
  }

  public static boolean dwarvenMineFaladorEntrance() {
    boolean goingIntoMines = Main.getController().currentY() <= 600;
    if (goingIntoMines) {
      Main.getController().log("Going into Dwarven Mines");
      Main.getController().atObject(251, 537);
    } else {
      Main.getController().log("Going out of Dwarven Mines");
      Main.getController().atObject(251, 3369);
    }
    Main.getController().sleep(1000);
    return goingIntoMines
        ? Main.getController().currentY() >= 600
        : Main.getController().currentY() <= 600;
  }

  public static boolean dwarvenMineCannonEntrance() {
    boolean goingIntoMines = Main.getController().currentY() <= 600;
    if (goingIntoMines) {
      Main.getController().log("Going into Dwarven Mines");
      Main.getController().atObject(279, 494);
    } else {
      Main.getController().log("Going out of Dwarven Mines");
      Main.getController().atObject(279, 3326);
    }
    Main.getController().sleep(1000);
    return goingIntoMines
        ? Main.getController().currentY() >= 600
        : Main.getController().currentY() <= 600;
  }

  public static boolean digsiteGate() {
    boolean goingEast = Main.getController().currentX() >= 59;
    Main.getController().atObject(59, 573);
    Main.getController()
        .sleepUntil(
            () ->
                goingEast
                    ? Main.getController().currentX() < 59
                    : Main.getController().currentX() >= 59,
            8000);
    return goingEast ? Main.getController().currentX() < 59 : Main.getController().currentX() >= 59;
  }

  public static boolean gnomeTreeGate() {
    boolean goingNorth = Main.getController().currentY() >= 532;
    Main.getController().atObject(703, 531);
    Main.getController()
        .sleepUntil(
            () ->
                goingNorth
                    ? Main.getController().currentY() <= 531
                    : Main.getController().currentY() >= 532
                        || Main.getController().isInOptionMenu(),
            12000);
    if (Main.getController().isInOptionMenu()) {
      Main.getController().log("Handling first time entrance to Gnome Tree...");
      Main.getController()
          .sleepUntil(() -> Main.getController().getOptionsMenuText(1).contains("ok then"), 12000);
      Main.getController().optionAnswer(1);
      Main.getController().atObject(703, 531);
      Main.getController().sleepUntil(() -> Main.getController().currentY() <= 531, 12000);
    }
    return goingNorth
        ? Main.getController().currentY() <= 531
        : Main.getController().currentY() >= 532;
  }

  public static boolean gnomeAgilityClimbFirstNet() {
    // Only have to worry about one-way because the path weights are setup such that
    // it's always easier to continue down the course than to try and go back
    Main.getController().atObject(SceneryId.NET_GNOME_COURSE_START);
    Main.getController().sleepUntilGainedXp();
    Main.getController().sleep(680);
    return true;
  }

  public static boolean gnomeAgilityClimbTower() {
    Main.getController().atObject(SceneryId.WATCH_TOWER_GNOME_COURSE_1ST_F);
    Main.getController()
        .sleepUntil(
            () -> Main.getController().currentX() == 693 && Main.getController().currentY() == 2394,
            2000);
    Main.getController().sleep(680);
    return true;
  }

  public static boolean gnomeAgilityRopeSwing() {
    Main.getController().atObject(SceneryId.ROPESWING_GNOME_COURSE);
    Main.getController().sleepUntilGainedXp();
    Main.getController().sleep(680);
    return true;
  }

  public static boolean gnomeAgilityClimbDownTower() {
    Main.getController().atObject(SceneryId.WATCH_TOWER_GNOME_COURSE_2ND_F);
    Main.getController().sleepUntilGainedXp();
    Main.getController().sleep(680);
    return true;
  }
}
