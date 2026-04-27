package controller.WebWalker;

import bot.Main;
import models.entities.ItemId;
import models.entities.NpcId;
import models.entities.QuestId;
import models.entities.SceneryId;
import orsc.ORSCharacter;

public class CustomLabelHandlers {

  public static boolean wildTunnelAllowed = true;

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
        .sleepUntil(() -> entering == (Main.getController().currentX() < 92), 15000);

    return entering == (Main.getController().currentX() < 92);
  }

  public static boolean lummyNorthCowGate() {
    boolean goingNorth = Main.getController().currentY() >= 593;
    if (Main.getController().getObjectAtCoord(154, 593) != -1) {
      Main.log("Opening Lummy North Cow Gate");
      Main.getController().atObject(154, 593);
      Main.getController().sleep(1000);
    }
    Main.getController().walkTo(155, goingNorth ? 592 : 593);
    return goingNorth
        ? Main.getController().currentY() <= 593
        : Main.getController().currentY() >= 593;
  }

  public static boolean lummyCabbageGate() {
    return open(148, 596, 60, "Opening Lumbridge Cabbage Gate...", null);
  }

  public static boolean lummyEastCowGate() {
    return open(105, 619, 60, "Opening Lumbridge East Cow Gate...", null);
  }

  public static boolean southFallyTavGate() {
    return enterStatic(
        343,
        581,
        181,
        343,
        580,
        343,
        581,
        Main.getController().currentY() < 581,
        "Passing the Taverley south gate...");
  }

  public static boolean northFallyTavGate() {
    return enterStatic(
        341,
        487,
        181,
        341,
        487,
        342,
        487,
        Main.getController().currentX() <= 341,
        "Passing the Taverley north gate...");
  }

  public static boolean miningGuildLadder() {
    boolean goingDown = Main.getController().currentY() <= 1000;
    Main.log("Climbing " + (goingDown ? "down" : "up") + " the Mining Guild ladder...");

    Main.getController().climb(274, goingDown ? 566 : 3398);
    Main.getController().sleep(200);
    return goingDown == (Main.getController().currentY() >= 1000);
  }

  public static boolean miningGuildDoor() {
    return enterStatic(
        268,
        3381,
        11,
        268,
        3380,
        268,
        3381,
        Main.getController().currentY() < 3381,
        "Opening Mining Guild door...");
  }

  public static boolean lummySheepEastGate() {
    boolean goingEast = Main.getController().currentX() >= 136;
    if (Main.getController().getObjectAtCoord(135, 630) == 60) {
      Main.log("Opening Lummy Sheep East Gate");
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
      Main.log("Opening Lummy North Sheep Gate");
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
      Main.log("Opening Gerrant House Door");
      Main.getController().atWallObject(277, 651);
      Main.getController().sleep(1000);
    }
    if (Main.getController().getWallObjectIdAtCoord(277, 651) == 2) {
      Main.log("Failed to open :C");
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
      Main.log("Opening Catherby Chef Door");
      Main.getController().atWallObject(435, 486);
      Main.getController().sleep(1000);
    }
    if (Main.getController().getWallObjectIdAtCoord(435, 486) == 2) {
      Main.log("Failed to open :C");
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
      Main.log("Going into Dwarven Mines");
      Main.getController().atObject(251, 537);
    } else {
      Main.log("Going out of Dwarven Mines");
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
      Main.log("Going into Dwarven Mines");
      Main.getController().atObject(279, 494);
    } else {
      Main.log("Going out of Dwarven Mines");
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
        .sleepUntil(() -> goingEast == (Main.getController().currentX() < 59), 8000);
    return goingEast == (Main.getController().currentX() < 59);
  }

  public static boolean gnomeTreeGate() {
    boolean goingNorth = Main.getController().currentY() >= 532;
    Main.getController().walkTo(703, goingNorth ? 532 : 531);
    Main.getController().sleepUntilNotMoving(10000);
    Main.getController().atObject(703, 531);
    Main.getController().sleep(1280);
    Main.getController()
        .sleepUntil(
            () ->
                goingNorth
                    ? Main.getController().currentY() <= 531
                    : Main.getController().currentY() >= 532
                        || Main.getController().isInOptionMenu(),
            12000);
    if (Main.getController().isInOptionMenu()) {
      Main.log("Handling first time entrance to Gnome Tree...");
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
    return open(158, 614, 60, "Opening North Chickens Gate...", null);
  }

  public static boolean lummyNorthGarlicGate() {
    return open(184, 604, 60, "Opening Lumbridge North Garlic Gate...", null);
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
        .sleepUntil(() -> goingDown == (Main.getController().currentY() < 1000));
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
      boolean dragonSlain = Main.getController().isQuestComplete(QuestId.DRAGON_SLAYER.getId());
      // Talk to Barnaby
      Main.getController().talkToNpcId(NpcId.CAPTAIN_BARNABY.getId(), true);
      Main.getController().optionAnswer(dragonSlain ? 0 : 1);
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
      boolean dragonSlain = Main.getController().isQuestComplete(QuestId.DRAGON_SLAYER.getId());
      Main.log("Talking to Npc..");
      // Talk to Seaman Lorris
      Main.getController().talkToNpcId(NpcId.SEAMAN_LORRIS.getId(), true);
      Main.getController().sleepUntil(() -> Main.getController().isInOptionMenu());
      Main.getController().optionAnswer(dragonSlain ? 0 : 1);
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
        .sleepUntil(() -> goingEast == (Main.getController().currentX() < 435));
  }

  public static boolean lummyEastChickenGate() {
    return open(114, 608, 60, "Opening Lumbridge East Chicken Gate...", null);
  }

  public static boolean lummyNorthWheatSouthGate() {
    return open(172, 607, 60, "Opening Lumbridge Wheat South Gate...", null);
  }

  public static boolean lummyNorthWheatNorthGate() {
    return open(177, 595, 60, "Opening Lumbridge Wheat North Gate...", null);
  }

  public static boolean wizardTowerBasement() {
    boolean goingDown = Main.getController().currentY() <= 1000;
    int ladderId = goingDown ? 6 : 5;

    open(217, 690, 2, null, null);

    Main.log(
        String.format(
            "Going %s the ladder %s Wizards' Tower Basement...",
            goingDown ? "down" : "up", goingDown ? "to" : "from"));
    Main.getController().atObject(ladderId);
    return Main.getController()
        .sleepUntil(() -> goingDown == (Main.getController().currentY() < 1000));
  }

  public static boolean wizardTowerDoor() {
    if (isWithinArea(216, 690, 219, 694) && (!isOnTile(219, 690))) open(217, 690, 2, null, null);
    return open(215, 689, 8, "Opening door to Wizards' Tower...", null);
  }

  public static boolean mcgroubersGate() {
    boolean entering = Main.getController().currentX() < 540;
    Main.getController().atWallObject(540, 445);
    Main.getController()
        .sleepUntil(() -> entering == (Main.getController().currentX() >= 540), 10000);
    Main.getController().sleep(680);
    return entering == (Main.getController().currentX() >= 540);
  }

  public static boolean taverleySteppingStones() {
    boolean goingWest = Main.getController().currentX() < 396;
    String logString =
        String.format(
            "Crossing the stepping stones from %s...",
            goingWest ? "Taverley to Catherby" : "Catherby to Taverley");

    Main.log(logString);
    Main.getController().atObject(goingWest ? 395 : 397, 502);
    Main.getController()
        .sleepUntil(() -> Main.getController().currentX() == (goingWest ? 397 : 395), 10000);
    return Main.getController().currentX() == (goingWest ? 397 : 395);
  }

  public static boolean dwarfTunnel() {
    int cY = Main.getController().currentY();
    int cX = Main.getController().currentX();
    boolean descending = cY < 1000;
    int ladderId = descending ? 359 : 43;
    boolean west = cX > 400;

    String dir = descending ? "down" : "up";
    String start = descending ? west ? "from Catherby" : "from Taverley" : "from Dwarf Tunnel";
    String destination = descending ? "to Dwarf Tunnel" : west ? "to Catherby" : "to Taverley";

    String logString = String.format("Going %s the stairs %s %s...", dir, start, destination);

    Main.log(logString);

    Main.getController().atObject(ladderId);
    return Main.getController()
        .sleepUntil(() -> descending == (Main.getController().currentY() < 1000));
  }

  public static boolean witchsHouseDoor() {
    boolean entering = !isWithinArea(358, 491, 362, 496);
    int doorKey = ItemId.FRONT_DOOR_KEY.getId();

    // Get key if needed (returns false if inventory is full)
    if (Main.getController().getInventoryItemCount(doorKey) == 0) {
      if (Main.getController().getInventoryItemCount() == 30) return false;
      Main.log("Getting a front door key...");
      if (entering) {
        Main.getController().atObject2(SceneryId.DOOR_MAT.getId());
        Main.getController()
            .sleepUntil(() -> Main.getController().getInventoryItemCount(doorKey) == 1, 10000);
      } else {
        Main.getController().walkTo(359, 491);
        Main.getController().atObject(358, 492);
        Main.getController().sleepUntil(() -> Main.getController().currentY() > 1000, 10000);
        Main.getController()
            .sleepUntil(
                () -> Main.getController().getGroundItemAmount(doorKey, 362, 1439) > 0, 10000);
        Main.getController().pickupItem(doorKey);
        Main.getController()
            .sleepUntil(() -> Main.getController().getInventoryItemCount(doorKey) > 0, 10000);
        Main.getController().walkTo(359, 1439);
        Main.getController().atObject(358, 1436);
        Main.getController().sleepUntil(() -> Main.getController().currentY() < 1000, 10000);
      }
    }
    if (Main.getController().getInventoryItemCount(doorKey) < 1) return false;
    Main.log(String.format("%s the Witch's House...", entering ? "Entering" : "Leaving"));

    // These loops may not be necessary. I added them just in case.
    int attempts = 0;
    while (++attempts < 5
        && Main.getController().isRunning()
        && Main.getController().isLoggedIn()) {

      // Wait for the door to close
      Main.getController()
          .sleepUntil(() -> Main.getController().getWallObjectIdAtCoord(363, 494) == 69);

      // Use key on the door
      Main.getController()
          .useItemOnWall(363, 494, Main.getController().getInventoryItemSlotIndex(doorKey));
      Main.getController().sleepUntil(() -> isWithinArea(358, 491, 362, 496) == entering, 5000);
      if (isWithinArea(358, 491, 362, 496) == entering) return true;
    }
    return false;
  }

  public static boolean faladorWestBankDoor() {
    return open(327, 552, 64, "Opening Falador west bank's door...", null);
  }

  public static boolean lummyLadderTo2FS() {
    int yThreshold = 1000;

    boolean atLowerLadder = Main.getController().currentY() < yThreshold;
    String climbMessage =
        String.format(
            "Climbing %s floor Lumbridge Castle ladder",
            atLowerLadder ? "up the first" : "down the second");
    return climb(139, 666, 139, 1610, climbMessage);
  }

  public static boolean lummyLadderTo2FN() {
    int yThreshold = 1000;

    boolean atLowerLadder = Main.getController().currentY() < yThreshold;
    String climbMessage =
        String.format(
            "Climbing %s floor Lumbridge Castle ladder",
            atLowerLadder ? "up the first" : "down the second");
    return climb(139, 648, 139, 1592, climbMessage);
  }

  public static boolean lummyLadderTo3FS() {
    int yThreshold = 2000;

    boolean atLowerLadder = Main.getController().currentY() < yThreshold;
    String climbMessage =
        String.format(
            "Climbing %s floor Lumbridge Castle ladder",
            atLowerLadder ? "up the second" : "down the third");
    return climb(138, 1612, 138, 2556, climbMessage);
  }

  public static boolean lummyLadderTo3FN() {
    int yThreshold = 2000;

    boolean atLowerLadder = Main.getController().currentY() < yThreshold;

    String climbMessage =
        String.format(
            "Climbing %s floor Lumbridge Castle ladder",
            atLowerLadder ? "up the second" : "down the third");
    return climb(138, 1593, 138, 2537, climbMessage);
  }

  public static boolean fishingGuildEntrance() {
    return enterStatic(
        586,
        524,
        11,
        586,
        523,
        586,
        524,
        Main.getController().currentY() < 524,
        (Main.getController().currentY() < 524 ? "Exiting" : "Entering") + " the Fishing Guild");
  }

  public static boolean zanarisShed() {
    boolean startInSwamp = Main.getController().currentY() < 3000;

    if (startInSwamp) {
      open(126, 686, 66, "Entering Zanaris Shed", null);
    } else {
      Main.log("@gre@Climbing the Zanaris ladder to Lumbridge Swamp");
      Main.getController().atObject(5);
    }

    return Main.getController()
        .sleepUntil(
            () ->
                startInSwamp
                    ? Main.getController().currentY() > 3000
                    : Main.getController().currentY() < 3000);
  }

  public static boolean yanilleWestGate() {
    if (Main.getController().getObjectAtCoord(647, 753) == 63
        && Main.getController().getObjectAtCoord(642, 753) == 63) return true;

    int currentX = Main.getController().currentX();

    // East of Yanille west gate
    if (currentX <= 642) {
      open(642, 753, 64, null, "Failed opening inner Yanille gate");
      open(647, 753, 64, "Opened Yanille gates", "Failed opening outer Yanille gate");

      // West of Yanille west gate or between gates
    } else {
      open(647, 753, 64, null, "Failed opening outer Yanille gate");
      open(642, 753, 64, "Opened Yanille gates", "Failed opening inner Yanille gate");
    }

    return (Main.getController().getObjectAtCoord(647, 753) == 63
        && Main.getController().getObjectAtCoord(642, 753) == 63);
  }

  public static boolean gnomeStrongholdBankNorthLadder() {
    String climbMessage =
        String.format(
            "Climbing ladder %s the Gnome Stronghold bank",
            Main.getController().currentY() < 1000 ? "up to" : "down from");
    return climb(714, 500, 714, 1444, climbMessage);
  }

  public static boolean gnomeStrongholdBankSouthLadder() {
    String climbMessage =
        String.format(
            "Climbing ladder %s the Gnome Stronghold bank",
            Main.getController().currentY() < 1000 ? "up to" : "down from");
    return climb(714, 516, 714, 1460, climbMessage);
  }

  public static boolean gnomeStrongholdSpinningWheelLadder() {
    String climbMessage =
        String.format(
            "Climbing ladder %s the Gnome Stronghold spinning wheel",
            Main.getController().currentY() < 1000 ? "up to" : "down from");
    return climb(691, 515, 691, 1459, climbMessage);
  }

  public static boolean asgarniaLadder() {
    String climbMessage =
        String.format(
            "Climbing ladder %s the Asgarnia ice dungeon",
            Main.getController().currentY() < 1000 ? "down to" : "up from");
    return climb(285, 711, 285, 3543, climbMessage);
  }

  public static boolean craftingGuild() {
    boolean entering = Main.getController().currentY() <= 600;
    Main.log((entering ? "Entering" : "Exiting") + " the Crafting Guild...");

    // Equip access item if needed
    if (!Main.getController().isItemIdEquipped(ItemId.BROWN_APRON.getId())
        && !Main.getController().isItemIdEquipped(ItemId.CRAFTING_CAPE.getId())) {
      if (Main.getController().isItemInInventory(ItemId.BROWN_APRON.getId())) {
        Main.getController().equipItemById(ItemId.BROWN_APRON.getId());
        Main.getController().sleep(640);
      } else if (Main.getController().isItemInInventory(ItemId.CRAFTING_CAPE.getId())) {
        Main.getController().equipItemById(ItemId.CRAFTING_CAPE.getId());
        Main.getController().sleep(640);
      }
    }

    return open(347, 601, 68, null, null);
  }

  public static boolean edgeRowBoat() {
    Main.log("Using Edge rowboat...");
    Main.getController().atObject(1242);
    Main.getController().sleepUntilNotMoving(5000);
    return Main.getController().sleepUntil(() -> Main.getController().currentY() < 550);
  }

  public static boolean edgeDungeonDoor() {
    return open(218, 646, 2, "Opening Edgeville Dungeon door...", null);
  }

  public static boolean edgeDungeonLadder() {
    return climb(215, 468, 215, 3300, "Climbing Edgeville Dungeon ladder...");
  }

  public static boolean oddWall() {
    boolean inside = Main.getController().currentX() >= 219;
    Main.log("Squeezing through odd wall...");
    Main.getController().atWallObject(219, 3282);
    return Main.getController().sleepUntil(() -> inside == (Main.getController().currentX() < 219));
  }

  public static boolean wildTunnelSkip() {
    Main.log("Entering Wilderness Tunnel (assuming 200k paid)...");
    int startY = Main.getController().currentY();
    Main.getController().atObject(1241);
    boolean moved =
        Main.getController().sleepUntil(() -> Main.getController().currentY() != startY, 3000);
    if (!moved) wildTunnelAllowed = false;
    return moved;
  }

  public static boolean wildyMageBankDoor() {
    return open(226, 110, 2, "Opening Mage Bank door...", null);
  }

  public static boolean wildyMageBankWebs() {
    boolean goingNorth = Main.getController().currentY() >= 108;
    if (goingNorth) {
      for (int i = 0; i < 40 && Main.getController().isRunning(); i++) {
        if (Main.getController().getWallObjectIdAtCoord(227, 109) != 24) break;
        Main.log("Cutting inner web...");
        Main.getController().atWallObject(227, 109);
        Main.getController().sleep(1000);
        Main.getController().sleep(500);
      }
      Main.getController().walkTo(227, 108);
      Main.getController().sleep(340);
      for (int i = 0; i < 40 && Main.getController().isRunning(); i++) {
        if (Main.getController().getWallObjectIdAtCoord(227, 107) != 24) break;
        Main.log("Cutting outer web...");
        Main.getController().atWallObject(227, 107);
        Main.getController().sleep(1000);
        Main.getController().sleep(500);
      }
      if (!Main.getController().isDoorOpen(227, 106)) {
        Main.getController().openDoor(227, 106);
        Main.getController().sleep(640);
      }
      return Main.getController().getWallObjectIdAtCoord(227, 107) != 24;
    } else {
      if (!Main.getController().isDoorOpen(227, 106)) {
        Main.getController().openDoor(227, 106);
        Main.getController().sleep(640);
      }
      for (int i = 0; i < 40 && Main.getController().isRunning(); i++) {
        if (Main.getController().getWallObjectIdAtCoord(227, 107) != 24) break;
        Main.log("Cutting outer web...");
        Main.getController().atWallObject(227, 107);
        Main.getController().sleep(1000);
        Main.getController().sleep(500);
      }
      Main.getController().walkTo(227, 108);
      Main.getController().sleep(340);
      for (int i = 0; i < 40 && Main.getController().isRunning(); i++) {
        if (Main.getController().getWallObjectIdAtCoord(227, 109) != 24) break;
        Main.log("Cutting inner web...");
        Main.getController().atWallObject(227, 109);
        Main.getController().sleep(1000);
        Main.getController().sleep(500);
      }
      return Main.getController().getWallObjectIdAtCoord(227, 109) != 24;
    }
  }

  public static boolean wildyMageBankLadder() {
    return climb(223, 110, 446, 3367, "Climbing Mage Bank ladder...");
  }

  public static boolean icePlateauGate() {
    boolean goingNorth = Main.getController().currentY() >= 142;
    Main.log((goingNorth ? "Exiting" : "Entering") + " Ice Plateau gate...");
    Main.getController().atObject(331, 142);
    return Main.getController()
        .sleepUntil(() -> goingNorth == (Main.getController().currentY() < 142), 10000);
  }

  public static boolean deepWildDungeonStairs() {
    boolean goingDown = Main.getController().currentY() < 1000;
    Main.log("Going " + (goingDown ? "down" : "up") + " Deep Wilderness dungeon stairs...");
    if (goingDown) {
      Main.getController().atObject(268, 128);
    } else {
      Main.getController().atObject(268, 2960);
    }
    return Main.getController()
        .sleepUntil(() -> goingDown == (Main.getController().currentY() > 1000));
  }

  public static boolean deepWildDungeonGate1() {
    if (Main.getController().getObjectAtCoord(272, 2972) == 57) {
      Main.getController().atObject(272, 2972);
      Main.getController().sleepUntilNotMoving(5000);
      Main.getController().sleep(340);
    }
    return (Main.getController().getObjectAtCoord(272, 2972) != 57);
  }

  public static boolean deepWildDungeonGate2() {
    if (Main.getController().getObjectAtCoord(281, 2969) == 57) {
      Main.getController().atObject(281, 2969);
      Main.getController().sleepUntilNotMoving(5000);
      Main.getController().sleep(340);
    }
    return (Main.getController().getObjectAtCoord(281, 2969) != 57);
  }

  public static boolean deepWildDungeonGate3() {
    if (Main.getController().getObjectAtCoord(274, 2952) == 57) {
      Main.getController().atObject(274, 2952);
      Main.getController().sleepUntilNotMoving(5000);
      Main.getController().sleep(340);
    }
    return (Main.getController().getObjectAtCoord(274, 2952) != 57);
  }

  public static boolean deepWildyGate() {
    return enterStatic(
        111,
        142,
        -1,
        111,
        141,
        111,
        142,
        Main.getController().currentY() < 142,
        "Passing through Deep Wilderness gate...");
  }

  public static boolean brimMossGiantSwing() {
    boolean goingToIsland = Main.getController().currentX() <= 509;
    int treeId = goingToIsland ? 694 : 695;
    Main.log((goingToIsland ? "Swinging to Moss Giant island..." : "Swinging back from island..."));
    Main.getController().atObject(treeId);
    return Main.getController().sleepUntilGainedXp();
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
  public static boolean isWithinArea(int x1, int y1, int x2, int y2) {
    int pX = Main.getController().currentX();
    int pY = Main.getController().currentY();

    int smallestX = Math.min(x1, x2);
    int biggestX = Math.max(x1, x2);
    int smallestY = Math.min(y1, y2);
    int biggestY = Math.max(y1, y2);

    return (pX >= smallestX && pX <= biggestX && pY >= smallestY && pY <= biggestY);
  }

  /**
   * Returns whether the player is within a rectangle of coordinates, regardless of which floor they
   * are on.
   *
   * @param groundX1 int -- Ground floor X1 coordinate of the rectangle's top-left corner
   * @param groundY1 int -- Ground floor Y1 coordinate to check for
   * @param groundX2 int -- Ground floor X2 coordinate to check for
   * @param groundY2 int -- Ground floor Y2 coordinate to check for
   * @return boolean -- Whether the player is within the rectangle on any floor
   */
  public static boolean isWithinAreaFloorAgnostic(
      int groundX1, int groundY1, int groundX2, int groundY2) {
    if (groundY1 >= 945 || groundY2 >= 945) {
      throw new IllegalArgumentException(
          "Y coordinates must be ground floor values (< 945). Got y1="
              + groundY1
              + ", y2="
              + groundY2);
    }

    final int FLOOR_HEIGHT = 944;
    final int FLOOR_COUNT = 4;

    for (int i = 0; i < FLOOR_COUNT; i++)
      if (isWithinArea(
          groundX1, groundY1 + (i * FLOOR_HEIGHT), groundX2, groundY2 + (i * FLOOR_HEIGHT)))
        return true;

    return false;
  }

  private static boolean isOnTile(int x, int y) {
    int pX = Main.getController().currentX();
    int pY = Main.getController().currentY();
    return (pX != x || pY != y);
  }

  /**
   * Returns whether the player is standing on any tile from an array of tiles. This is similar to
   * isWithinArea, except isOnTiles doesn't require the tiles to be contiguous. Useful for checking
   * areas of irregular shapes.
   *
   * @param tiles int[][] -- Array of tiles
   * @return boolean -- Whether the player on one of the tiles in the array
   */
  private static boolean isOnTiles(int[][] tiles) {
    if (tiles == null || tiles.length < 1) return false;
    int pX = Main.getController().currentX();
    int pY = Main.getController().currentY();
    for (int[] tile : tiles) {
      int tileX = tile[0];
      int tileY = tile[1];
      if (pX == tileX && pY == tileY) return true;
    }
    return false;
  }

  /**
   * Handles climbing ladders/stairs. This returns whether checks if the player has been moved
   * across the threshold coordinate.
   *
   * @param ladder1X int -- The X coordinate of the lower ladder/stair object
   * @param ladder1Y int -- The Y coordinate of the lower ladder/stair object
   * @param ladder2X int -- The X coordinate of the upper ladder/stair object
   * @param ladder2Y int -- The Y coordinate of the upper ladder/stair object
   * @param logMessage String -- The message to print after interacting with the object
   * @return boolean -- Whether the action was a success
   */
  private static boolean climb(
      int ladder1X, int ladder1Y, int ladder2X, int ladder2Y, String logMessage) {
    int startCoord = Main.getController().currentY();

    int lowerLadderX, lowerLadderY, higherLadderX, higherLadderY;
    if (ladder1Y < ladder2Y) {
      lowerLadderX = ladder1X;
      lowerLadderY = ladder1Y;
      higherLadderX = ladder2X;
      higherLadderY = ladder2Y;
    } else {
      lowerLadderX = ladder2X;
      lowerLadderY = ladder2Y;
      higherLadderX = ladder1X;
      higherLadderY = ladder1Y;
    }

    int threshold = (lowerLadderY + higherLadderY) / 2;
    boolean startedBelowThreshold = startCoord < threshold;

    if (startedBelowThreshold) {
      Main.getController().atObject(lowerLadderX, lowerLadderY);
    } else {
      Main.getController().atObject(higherLadderX, higherLadderY);
    }

    Main.log("@gre@" + logMessage);
    return Main.getController()
        .sleepUntil(
            () -> {
              int currentCoord = Main.getController().currentY();

              return startedBelowThreshold ? currentCoord > threshold : currentCoord < threshold;
            });
  }

  /**
   * Handles interacting with an object or wall object at given coordinates that matches the given
   * id, while waiting for that wall object's id to change.
   *
   * @implNote Only use on objects that change ids after interacting with them. Do not use this for
   *     objects that move the player without changing ids (teleports, crawlspaces, etc.)
   * @param x int -- X coordinate of a wall object
   * @param y int -- Y coordinate of a wall object
   * @param closedObjectId int -- Id of an object to interact with (a closed door, for example).
   *     Skips check if id is not at coordinates
   * @param logSuccessMessage String -- Message to print to log on successfully opening the object.
   *     Leave null to print nothing.
   * @param logFailMessage String -- Message to print to log on failure to open the object. Leave
   *     null to print nothing
   * @return boolean
   */
  private static boolean open(
      int x, int y, int closedObjectId, String logSuccessMessage, String logFailMessage) {
    if (logSuccessMessage != null && !logSuccessMessage.isEmpty())
      Main.log("@gre@" + logSuccessMessage);

    boolean isClosed =
        Main.getController().getWallObjectIdAtCoord(x, y) == closedObjectId
            || Main.getController().getObjectAtCoord(x, y) == closedObjectId;

    int openAttempts = 0;
    while (isClosed && Main.getController().isRunning() && Main.getController().isLoggedIn()) {
      openAttempts++;
      if (openAttempts > 10) {
        if (logFailMessage != null && !logFailMessage.isEmpty()) Main.log(logFailMessage);

        return false;
      }

      // * This hasn't been tested extensively.
      // * It might need a boolean instead to choose between Object and WallObject.
      // * I'm not sure what would happen if there are an Object and WallObject on the same tile (if
      // * that's even possible)

      if (Main.getController().getWallObjectIdAtCoord(x, y) != -1)
        Main.getController().atWallObject(x, y);
      if (Main.getController().getObjectAtCoord(x, y) != -1) Main.getController().atObject(x, y);

      Main.getController().sleep(1280);

      isClosed =
          Main.getController().getWallObjectIdAtCoord(x, y) == closedObjectId
              || Main.getController().getObjectAtCoord(x, y) == closedObjectId;
    }
    return true;
  }

  /**
   * Handles interacting with a "static" object or wall object at given coordinates.
   *
   * @implNote Use this for objects that either do not change IDs after interacting with them, or
   *     temporarily change IDs before reverting (such as a door that opens then closes).
   * @param objectX int -- X coordinate of the object.
   * @param objectY int -- Y coordinate of the object.
   * @param openedObjectId int -- ID for the opened instance of the door. Set to -1 if object
   *     doesn't change IDs after entering
   * @param northEastTileX int -- X coordinate of the tile to stand on when approaching from the
   *     north or east.
   * @param northEastTileY int -- Y coordinate of the tile to stand on when approaching from the
   *     north or east.
   * @param southWestTileX int -- X coordinate of the tile to stand on when approaching from the
   *     south or west.
   * @param southWestTileY int -- Y coordinate of the tile to stand on when approaching from the
   *     south or west.
   * @param isNorthOrEastOf boolean -- Whether the player is approaching from the north or east side
   *     of the object.
   * @param logMessage String -- Message to log.
   * @return boolean -- Whether the threshold was successfully passed.
   */
  private static boolean enterStatic(
      int objectX,
      int objectY,
      int openedObjectId,
      int northEastTileX,
      int northEastTileY,
      int southWestTileX,
      int southWestTileY,
      boolean isNorthOrEastOf,
      String logMessage) {

    int walkX = isNorthOrEastOf ? northEastTileX : southWestTileX;
    int walkY = isNorthOrEastOf ? northEastTileY : southWestTileY;
    int destX = isNorthOrEastOf ? southWestTileX : northEastTileX;
    int destY = isNorthOrEastOf ? southWestTileY : northEastTileY;

    if (Main.getController().currentX() != walkX || Main.getController().currentY() != walkY) {
      Main.getController().walkTo(walkX, walkY);
      Main.getController()
          .sleepUntil(
              () ->
                  Main.getController().currentX() == walkX
                      && Main.getController().currentY() == walkY,
              10000);
    }

    if (openedObjectId != -1) {
      boolean doorClosed =
          Main.getController()
              .sleepUntil(
                  () ->
                      Main.getController().getWallObjectIdAtCoord(objectX, objectY)
                              != openedObjectId
                          && Main.getController().getObjectAtCoord(objectX, objectY)
                              != openedObjectId,
                  10000);
      if (!doorClosed) return false;
    }

    if (logMessage != null && !logMessage.isEmpty()) Main.log(logMessage);

    Main.getController().atWallObject(objectX, objectY);
    Main.getController().atObject(objectX, objectY);
    int dist = Main.getController().distanceTo(destX, destY);

    Main.getController()
        .sleepUntil(
            () -> {
              Main.getController().sleep(640);
              if (Main.getController().distanceTo(destX, destY) > dist) {
                Main.log("Waiting for the object to become available...");
                if (Main.getController().isCurrentlyWalking())
                  Main.getController().walkTo(walkX, walkY);
                return false;
              }
              return Main.getController().currentX() == destX
                  && Main.getController().currentY() == destY;
            },
            10000);
    Main.getController().sleep(640);

    return Main.getController().currentX() == destX && Main.getController().currentY() == destY;
  }
}
