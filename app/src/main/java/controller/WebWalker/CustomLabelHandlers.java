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
    if (Main.getController().getObjectAtCoord(105, 619) == 59) return true;
    Main.getController().log("Opening Lummy East Cow Gate");
    Main.getController().atObject(105, 619);
    return Main.getController()
        .sleepUntil(() -> Main.getController().getObjectAtCoord(105, 619) == 59, 6000);
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
    if (goingNorth) {
      Main.getController().walkTo(703, 532);
    }
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
    Main.getController().walkTo(693, 1451);
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

  public static boolean skipTutorial() {
    Main.getController().skipTutorialIsland();
    Main.getController().sleepUntil(() -> Main.getController().distanceTo(207, 750) > 50);
    return Main.getController().distanceTo(207, 750) > 50;
  }

  public static boolean lummyNorthChickensGate() {
    if (Main.getController().getObjectAtCoord(158, 614) == 59) return true; // Gate is already open
    Main.log("Opening North Chickens Gate...");
    Main.getController().atObject(158, 614);
    return Main.getController()
        .sleepUntil(() -> Main.getController().getObjectAtCoord(158, 614) == 59, 6000);
  }

  public static boolean lummyNorthGarlicGate() {
    int xCoord = 184, yCoord = 604;
    if (Main.getController().getObjectAtCoord(xCoord, yCoord) == 59)
      return true; // Gate is already open
    Main.log("Opening Lummy North Garlic Gate...");
    Main.getController().atObject(xCoord, yCoord);
    return Main.getController()
        .sleepUntil(() -> Main.getController().getObjectAtCoord(xCoord, yCoord) == 59, 6000);
  }

  public static boolean lummyNorthPotatoGate() {
    int xCoord = 184, yCoord = 606;
    if (Main.getController().getObjectAtCoord(xCoord, yCoord) == 59)
      return true; // Gate is already open
    Main.log("Opening Lummy North Garlic Gate...");
    Main.getController().atObject(xCoord, yCoord);
    return Main.getController()
        .sleepUntil(() -> Main.getController().getObjectAtCoord(xCoord, yCoord) == 59, 6000);
  }

  public static boolean varrockPalaceNorthwestLadder() {
    boolean goingDown = Main.getController().currentY() >= 1000;
    int ladderId = goingDown ? 6 : 5;
    Main.log("Climbing " + (goingDown ? "down" : "up") + " Varrock Palace Ladder...");
    Main.getController().atObject(ladderId);
    return Main.getController()
        .sleepUntil(
            () ->
                goingDown
                    ? Main.getController().currentY() < 1000
                    : Main.getController().currentY() >= 1000);
  }

  public static boolean varrockPalaceFence() {
    int xCoord = 131, yCoord = 501;
    if (Main.getController().getObjectAtCoord(xCoord, yCoord) == 58)
      return true; // Gate is already open
    Main.log("Opening Varrock Palace Fence...");
    Main.getController().atObject(xCoord, yCoord);
    return Main.getController()
        .sleepUntil(() -> Main.getController().getObjectAtCoord(xCoord, yCoord) == 58, 6000);
  }

  public static boolean varrockEastDigsiteGate() {
    int xCoord = 79, yCoord = 505;
    if (Main.getController().getObjectAtCoord(xCoord, yCoord) == 59)
      return true; // Gate is already open
    Main.log("Opening Varrock East Digsite Gate...");
    Main.getController().atObject(xCoord, yCoord);
    return Main.getController()
        .sleepUntil(() -> Main.getController().getObjectAtCoord(xCoord, yCoord) == 59, 6000);
  }

  public static boolean brimhavenArdyBoat() {
    // Only gets considered if we have 60gp, to prevent getting stuck.
    boolean comingFromArdy =
        Main.getController().getNearestNpcById(NpcId.CAPTAIN_BARNABY.getId(), false) != null;
    if (comingFromArdy) {
      // Talk to Barnaby
      Main.getController().talkToNpcId(NpcId.CAPTAIN_BARNABY.getId(), true);
      Main.getController().optionAnswer(1);
    } else {
      // Talk to Customs Official
      Main.getController().talkToNpcId(NpcId.CUSTOMS_OFFICIAL.getId(), true);
      Main.log("Answering..");
      Main.getController().optionAnswer(0); // Can I board this ship?
      Main.getController().sleepUntil(() -> !Main.getController().isInOptionMenu());
      Main.getController().sleepUntil(() -> Main.getController().isInOptionMenu());
      Main.getController().optionAnswer(1); // Search away, I have nothing to hide
      Main.getController().sleepUntil(() -> !Main.getController().isInOptionMenu());
      Main.getController().sleepUntil(() -> Main.getController().isInOptionMenu());
      Main.getController().optionAnswer(0); // Ok
    }
    return Main.getController()
        .sleepUntil(
            () ->
                comingFromArdy
                    ? Main.getController().getNearestNpcById(NpcId.CAPTAIN_BARNABY.getId(), false)
                        == null
                    : Main.getController().getNearestNpcById(NpcId.CUSTOMS_OFFICIAL.getId(), false)
                        == null);
  }

  public static boolean portSarimKaramjaBoat() {
    // Only gets considered if we have 60gp, to prevent getting stuck.
    boolean comingFromKaramja =
        Main.getController().getNearestNpcById(NpcId.CUSTOMS_OFFICER.getId(), false) != null;
    if (comingFromKaramja) {
      // Talk to Customs Officer
      Main.getController().talkToNpcId(NpcId.CUSTOMS_OFFICER.getId(), true);
      Main.log("Answering..");
      Main.getController().optionAnswer(0); // Can I board this ship?
      Main.getController().sleepUntil(() -> !Main.getController().isInOptionMenu());
      Main.getController().sleepUntil(() -> Main.getController().isInOptionMenu());
      Main.getController().optionAnswer(1); // Search away, I have nothing to hide
      Main.getController().sleepUntil(() -> !Main.getController().isInOptionMenu());
      Main.getController().sleepUntil(() -> Main.getController().isInOptionMenu());
      Main.getController().optionAnswer(0); // Ok
    } else {
      Main.log("Talking to Npc..");
      // Talk to Seaman Lorris
      Main.getController().talkToNpcId(NpcId.SEAMAN_LORRIS.getId(), true);
      Main.getController().sleepUntil(() -> !Main.getController().isInOptionMenu());
      Main.getController().sleepUntil(() -> Main.getController().isInOptionMenu());
      Main.getController().optionAnswer(1);
    }
    return Main.getController()
        .sleepUntil(
            () ->
                comingFromKaramja
                    ? Main.getController().getNearestNpcById(NpcId.CUSTOMS_OFFICER.getId(), false)
                        == null
                    : Main.getController().getNearestNpcById(NpcId.SEAMAN_LORRIS.getId(), false)
                        == null);
  }

  public static boolean brimhavenKaramjaGate() {
    boolean goingEast = Main.getController().currentX() >= 435;
    // Interact with the gate
    Main.getController().atObject(434, 682);
    // Sleep until we're on the other side of the gate
    return Main.getController()
        .sleepUntil(
            () ->
                goingEast
                    ? Main.getController().currentX() < 435
                    : Main.getController().currentX() >= 435);
  }

  public static boolean lummyEastChickenGate() {
    // Check if the gate is open; if object at 114, 608 == 59, gate is open and we
    // just walk through
    if (Main.getController().getObjectAtCoord(114, 608) == 59) return true;
    // Interact with the gate
    Main.getController().atObject(114, 608);
    return true;
  }

  public static boolean wizardTowerBasement() {
    boolean goingDown = Main.getController().currentY() <= 1000;
    int ladderId = goingDown ? 6 : 5;

    openWallObject(217, 690, 2, null);

    Main.log(
        String.format(
            "Going %s the ladder %s Wizards' Tower Basement...",
            goingDown ? "down" : "up", goingDown ? "to" : "from"));
    Main.getController().atObject(ladderId);
    return Main.getController()
        .sleepUntil(() -> goingDown == (Main.getController().currentY() < 1000));
  }

  public static boolean wizardTowerDoor() {
    if (isWithinArea(216, 690, 219, 694)) openWallObject(217, 690, 2, null);
    return openWallObject(215, 689, 8, "Opening door to Wizards' Tower...");
  }

  /**
   * Returns whether the player is within a rectangle of coordinates.
   *
   * @param x1 int -- X1 coordinate to check for
   * @param y1 int -- Y1 coordinate to check for
   * @param x2 int -- X2 coordinate to check for
   * @param y2 int -- Y2 coordinate to check for
   * @return boolean
   */
  private static boolean isWithinArea(int x1, int y1, int x2, int y2) {
    int x = Main.getController().currentX();
    int y = Main.getController().currentY();
    return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
  }

  /**
   * Handles interacting with an object id at given coordinates if it matches objectId. Then wait
   * for that id to change before continuing.
   *
   * @implNote Only use on objects that change ids after interacting with them. Do not use this for
   *     objects that move the player without changing ids (teleports, crawlspaces, etc.)
   * @param x int -- X coordinate of the object
   * @param y int -- Y coordinate of the object
   * @param objectId int -- Id of an object to interact with (a closed gate, for example). Skips
   *     check if id is not at coordinates
   * @param logMessage String -- Message to log when doing the interaction. Leave null or empty if
   *     not wanted
   * @return boolean
   */
  private static boolean openObject(int x, int y, int objectId, String logMessage) {
    if (Main.getController().getObjectAtCoord(x, y) == objectId) {
      if (logMessage != null && !logMessage.isEmpty()) Main.log(logMessage);
      while (Main.getController().getObjectAtCoord(x, y) == objectId
          && Main.getController().isRunning()) {
        Main.getController().atObject(x, y);
        Main.getController().sleep(1280);
      }
    }
    return true;
  }

  /**
   * Handles interacting with a wall object at given coordinates that matches the given id, while
   * waiting for that wall object's id to change.
   *
   * @implNote Only use on objects that change ids after interacting with them. Do not use this for
   *     objects that move the player without changing ids (teleports, crawlspaces, etc.)
   * @param x int -- X coordinate of a wall object
   * @param y int -- Y coordinate of a wall object
   * @param wallObjectId int -- Id of an object to interact with (a closed door, for example). Skips
   *     check if id is not at coordinates
   * @param logMessage String -- Message to print to log
   * @return boolean
   */
  private static boolean openWallObject(int x, int y, int wallObjectId, String logMessage) {
    if (Main.getController().getWallObjectIdAtCoord(x, y) == wallObjectId) {
      if (logMessage != null && !logMessage.isEmpty()) Main.log(logMessage);
      while (Main.getController().getWallObjectIdAtCoord(x, y) == wallObjectId
          && Main.getController().isRunning()) {
        Main.getController().atWallObject(x, y);
        Main.getController().sleep(1280);
      }
    }
    return true;
  }

  /**
   * Handles opening a door at given coordinates. Some doors may not be detected with this method.
   * Those require using handleWallObject() instead.
   *
   * @param x int -- X Coordinate of a wall object
   * @param y int -- Y Coordinate of a wall object
   * @param logMessage String -- Message to print to log
   * @return boolean
   */
  private static boolean openDoor(int x, int y, String logMessage) {
    if (!Main.getController().isDoorOpen(x, y)) {
      if (logMessage != null && !logMessage.isEmpty()) Main.log(logMessage);
      while (!Main.getController().isDoorOpen(x, y) && Main.getController().isRunning()) {
        Main.getController().openDoor(x, y);
        Main.getController().sleep(1280);
      }
    }
    return true;
  }
}
