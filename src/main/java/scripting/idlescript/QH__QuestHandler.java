package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.util.*;
import models.entities.QuestId;
import orsc.ORSCharacter;

public class QH__QuestHandler extends IdleScript {
  protected static final Controller c = Main.getController();

  // QUEST START COORDINATES
  protected Integer[][] LUMBRIDGE_CASTLE_COURTYARD = {{120, 650}, {128, 665}};

  // PUBLIC VARIABLES SET BY SUBCLASS QUEST SCRIPT
  protected String QUEST_NAME, START_DESCRIPTION, CURRENT_QUEST_STEP = "";
  protected Integer QUEST_ID, QUEST_STAGE;
  protected Integer[][] START_RECTANGLE; // {{CORNER1X, CORNER1Y}, {CORNER2X,CORNER2Y}}
  protected String[][] SKILL_REQUIREMENTS; // {{"Cooking", "99"}, {"Coolness", "99"}}
  protected String[] QUEST_REQUIREMENTS; // {"Legend's Quest", "Black Knight's Fortress"}
  protected Integer[][] STEP_ITEMS; // {{ItemId.BANANA.getId(), 1},{ItemId.MILK.getId(), 900}}
  protected String[] DIALOG_CHOICES;

  // PRIVATE VARIABLES
  private List<String> MISSING_LEVELS = new ArrayList<String>();
  private List<String> MISSING_QUESTS = new ArrayList<String>();
  private boolean IS_TESTING = false;

  public int start(String[] param) {
    QUEST_NAME = "Quest Handler";
    CURRENT_QUEST_STEP = "This is only ran for testing.";
    while (c.isRunning() && IS_TESTING) {}

    quit("Ran handler");
    return 1000;
  }

  /**
   * Checks if the player meets the quests requirements. Quits the script and specifies what
   * requirement is missing otherwise.
   */
  public void doQuestChecks() {
    if (QUEST_STAGE == -1) {
      quit("Quest already complete");
    } else {
      requiredQuestsCheck();
      requiredLevelsCheck();
      if (!isInRectangle(START_RECTANGLE)) {
        quit("Not in start area");
      } else {
        c.displayMessage("@gre@Start location correct");
      }
    }
  }

  // Checks if the player has the required levels for the quest
  private void requiredLevelsCheck() {
    MISSING_LEVELS = new ArrayList<String>();
    for (int i = 0; i < SKILL_REQUIREMENTS.length; i++) {
      String skillName = SKILL_REQUIREMENTS[i][0];
      Integer skillLevel = Integer.parseInt(SKILL_REQUIREMENTS[i][1]);
      if (!hasSkillLevel(skillName, skillLevel)) {
        String missingString = String.format("@red@- %s %s", skillName, String.valueOf(skillLevel));
        MISSING_LEVELS.add(missingString);
      }
    }
    if (MISSING_LEVELS.size() > 0) {
      quit("Missing levels");
    } else {
      c.displayMessage("@gre@All level requirements met");
    }
  }

  // Checks if the player has the required quests for the quest
  private void requiredQuestsCheck() {
    MISSING_QUESTS = new ArrayList<String>();
    if (QUEST_REQUIREMENTS.length > 0) {
      for (int i = 0; i < QUEST_REQUIREMENTS.length; i++) {
        String questName = QUEST_REQUIREMENTS[i];
        int id = QuestId.getByName(QUEST_REQUIREMENTS[i]).getId();
        if (c.getQuestStage(id) != -1) {
          String missingString = String.format("@red@- %s", questName);
          MISSING_QUESTS.add(missingString);
        }
      }
    }
    if (MISSING_QUESTS.size() > 0) {
      quit("Missing quests");
    } else {
      c.displayMessage("@gre@All quest requirements met");
    }
  }

  /**
   * Checks if the player has at least a certain amount of an item id
   *
   * @param id Integer -- The item id to check
   * @param amount Integer -- The amount to check for
   * @return boolean
   */
  public boolean hasAtLeastItemAmount(Integer id, Integer amount) {
    return (c.getInventoryItemCount(id) >= amount);
  }

  /**
   * Checks if the player has the required level of a specified skill
   *
   * @param skillName String -- Name of the skill
   * @param requiredLevel Integer -- Level required for the skill
   * @return boolean
   */
  public boolean hasSkillLevel(String skillName, Integer requiredLevel) {
    return c.getBaseStat(c.getStatId(skillName)) >= requiredLevel;
  }

  /**
   * Checks if the player is within a rectangle of two given tiles
   *
   * @param areaRectangle Integer[][] -- Two corner tiles of a rectangle {TILE1[],TILE2[]} or
   *     {{X1,Y1},{X2,Y2}}
   * @return boolean
   */
  public boolean isInRectangle(Integer[][] areaRectangle) {
    // Sorts the array to just in case to make it work correctly with the check
    int[] toSortX = {areaRectangle[0][0], areaRectangle[1][0]};
    int[] toSortY = {areaRectangle[0][1], areaRectangle[1][1]};
    Arrays.sort(toSortX);
    Arrays.sort(toSortY);
    int[][] sortedRectangle = {{toSortX[0], toSortY[0]}, {toSortX[1], toSortY[1]}};

    if (c.currentX() >= sortedRectangle[0][0]
        && c.currentX() <= sortedRectangle[1][0]
        && c.currentY() >= sortedRectangle[0][1]
        && c.currentY() <= sortedRectangle[1][1]) {
      return true;
    }
    return false;
  }

  /**
   * Drops all of a given item id except one
   *
   * @param itemId Integer -- Id of the item to drop
   */
  public void dropAllButOne(Integer itemId) {
    if (c.getInventoryItemCount(itemId) > 1) {
      c.dropItem(c.getInventoryItemSlotIndex(itemId), c.getInventoryItemCount(itemId) - 1);
      while (c.getInventoryItemCount(itemId) > 1) {
        c.sleep(640);
      }
    }
  }

  /**
   * Walks to specified tile and picks up an item id from an unreachable tile
   *
   * @param itemId Integer -- Id of the item to pick up
   * @param standTile Integer[] -- Tile to stand at to pick up item
   */
  public void pickupUnreachableItem(Integer itemId, Integer[] standTile) {
    int[] item = c.getNearestItemById(itemId);
    if (item != null) {
      Integer[][] path = {{standTile[0], standTile[1]}};
      walkPath(path);
      c.pickupItem(item[0], item[1], itemId, false, true);
      c.sleep(1280);
    }
  }

  /**
   * Picks up the nearest item matching the given item id
   *
   * @param itemId Integer -- Id of the item to pick up
   */
  public void pickupGroundItem(Integer itemId) {
    int[] item = c.getNearestItemById(itemId);
    if (item != null) {
      Integer[][] path = {{item[0], item[1]}};
      walkPath(path);
      c.pickupItem(item[0], item[1], itemId, true, true);
      c.sleep(1280);
    }
  }

  /**
   * Uses an item id on an the nearest npc with matching npc id
   *
   * @param npcId Integer -- Id of npc, not server index
   * @param itemId Integer -- Id of the item to use on the npc
   */
  public void useItemOnNearestNpcId(Integer npcId, Integer itemId) {
    int[] npc_id = {npcId};
    final ORSCharacter npc = c.getNearestNpcByIds(npc_id, false);
    if (npc != null) {
      c.useItemOnNpc(npc.serverIndex, itemId);
      c.sleep(640);
      while (c.isCurrentlyWalking()) {
        c.sleep(640);
      }
    } else {
      c.log("NPC: %s was not found", String.valueOf(npcId));
    }
  }

  /**
   * Goes through a dialog with the nearest instance of an npc id with with dialogChoices[] being
   * the responses from the character
   *
   * @param npcId Integer -- Id of the npc to talk to
   * @param dialogChoices String[] -- Responses to dialogs
   */
  public void followNPCDialog(Integer npcId, String[] dialogChoices) {
    int[] npc_id = {npcId};
    ORSCharacter npc = c.getNearestNpcByIds(npc_id, false);
    if (npc != null) {
      c.talkToNpc(npc.serverIndex);
      if (dialogChoices.length > 0) {
        for (int choiceIndex = 0; choiceIndex < dialogChoices.length; choiceIndex++) {
          // Sleep until a dialog menu shows
          while (!c.isInOptionMenu()) {
            c.sleep(640);
          }
          String[] menuOptionText = c.getOptionsMenuText();
          // Select the menu option that cooresponds with choiceIndex
          for (int option = 0; option < c.getOptionMenuCount(); option++) {
            if (c.getOptionsMenuText(option) == dialogChoices[choiceIndex]) {
              c.optionAnswer(option);
              break;
            }
          }
          // Sleep until a new options menu shows
          while (menuOptionText[0] == c.getOptionsMenuText(0)) {
            c.sleep(640);
          }
        }
      } else {
        while (c.isNpcTalking(npc.serverIndex)) {
          c.sleep(640);
        }
      }
    } else {
      c.log(
          String.format(
              "Could not find NPC: %s near the player at: (%s,%s)",
              String.valueOf(npcId), String.valueOf(c.currentX()), String.valueOf(c.currentY())));
    }
  }

  /**
   * Follows a given path while checking for and opening closed doors next to given tiles
   *
   * @param path - Integer[][] -- Each index is a different tile. Either {{X,Y},{X2,Y2}} or
   *     {TILE1[],TILE2[]}
   */
  public void walkPath(Integer[][] path) {
    for (int i = 0; i < path.length; ++i) {
      if (c.isCurrentlyWalking()) {
        while (c.isCurrentlyWalking() && c.isRunning()) {
          c.sleep(640);
        }
      }
      if (c.isRunning()) {
        c.openNearbyDoor(1);
        c.sleep(640);
        c.walkTo(path[i][0], path[i][1]);
        c.sleep(640);
      }
      while (c.isCurrentlyWalking() && c.isRunning()) {
        c.sleep(640);
      }
    }
  }

  /**
   * Interacts with objects in a given order based on coords
   *
   * @param coords - Integer[][] -- Each index is a different Object's tile coordinates. Either
   *     {{X,Y},{X2,Y2}} or {TILE1[],TILE2[]}
   */
  public void atObjectSequence(Integer[][] coords) {
    for (int i = 0; i < coords.length; i++) {
      if (c.isCurrentlyWalking()) {
        while (c.isCurrentlyWalking() && c.isRunning()) {
          c.sleep(640);
        }
      }
      if (c.isRunning()) {
        c.atObject(coords[i][0], coords[i][1]);
        c.sleep(640);
      }
      while (c.isCurrentlyWalking() && c.isRunning()) {
        c.sleep(640);
      }
    }
  }

  /**
   * Quits the script for a specified reason
   *
   * @param reason String -- Reason for quitting script to give user information as to why the
   *     script stopped
   */
  public void quit(String reason) {
    if (c.isRunning()) {
      switch (reason) {
        case "Script stopped":
          // Quit if script is stopped
          c.displayMessage("@red@The script has been stopped!");
          break;
        case "Missing levels":
          // Quit if the required levels are missing
          c.displayMessage("@red@You are missing the following levels for this quest:");
          for (int i = 0; i < MISSING_LEVELS.size(); i++) {
            c.displayMessage(MISSING_LEVELS.get(i));
          }
          break;
        case "Missing quests":
          c.displayMessage("@red@This quest requires you to complete the follwing quests first:");
          for (int i = 0; i < MISSING_QUESTS.size(); i++) {
            c.displayMessage(MISSING_QUESTS.get(i));
          }
          break;
        case "Not in start area":
          // Quit if the player is not in the starting area
          if (START_RECTANGLE == LUMBRIDGE_CASTLE_COURTYARD) {
            START_DESCRIPTION = "Lumbridge Castle Courtyard";
            /*
             * } else if (START_RECTANGLE == ){ START_DESCRIPTION = "";
             */
          } else {
            START_DESCRIPTION = "Unspecifed start area";
          }
          c.displayMessage("@red@Start the script at:");
          c.displayMessage("@red@" + START_DESCRIPTION);
          break;
        case "Quest already complete":
          c.displayMessage("@red@This quest has already been completed");
          break;
        case "Quest completed":
          c.displayMessage("@gre@Quest Completed");
          c.setStatus("@gre@Quest Completed");
          break;
        case "Ran handler":
          c.displayMessage("@red@Do not run this script");
          c.displayMessage("@red@Run a QH_ quest script instead");
          break;
        default:
          c.displayMessage("@red@The script has unexpectedly stopped!");
          break;
      }
      c.stop();
    }
  }

  // AFTER THIS POINT IS ALL PAINT STUFF

  public String getQuestStepItems(int i, int t) {
    String color = "";
    String text = "";
    int held = 0;
    if (c.getInventoryItemCount(STEP_ITEMS[i][0]) == 0) {
      held = 0;
      color = "@red@";
    } else if (c.getInventoryItemCount(STEP_ITEMS[i][0]) > 0
        && c.getInventoryItemCount(STEP_ITEMS[i][0]) < STEP_ITEMS[i][1]) {
      held = c.getInventoryItemCount(STEP_ITEMS[i][0]);
      color = "@yel@";
    } else {
      held = STEP_ITEMS[i][1];
      color = "@gre@";
    }
    switch (t) {
      case 1:
        String itemName = c.getItemName(STEP_ITEMS[i][0]);
        itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
        text = String.format("  %s %s", color, itemName);
        break;
      case 2:
        text = String.format("  %s %s/%s", color, held, STEP_ITEMS[i][1]);
        break;
    }
    return text;
  }

  private void drawCenteredString(String str, int widthOfPaintArea, int y, int color, int font) {
    c.drawString(str, spacingFromLength(str, widthOfPaintArea), y, color, font);
  }

  private int spacingFromLength(String text, Integer width) {
    if (text.length() < 1) return width / 2;
    Integer spacing = 0;
    String[] twelveWide = {"W"};
    String[] tenWide = {
      "m",
    };
    String[] nineWide = {
      "M", "w",
    };
    String[] eightWide = {
      "O", "Q",
    };
    String[] sevenWide = {
      "A", "B", "C", "D", "G", "H", "K", "N", "P", "R", "S", "U", "V",
    };
    String[] sixWide = {
      "E", "J", "L", "T", "X", "Y", "Z", "a", "b", "c", "d", "e", "g", "h", "k", "n", "o", "p", "q",
      "s", "u", "x", "?"
    };
    String[] fiveWide = {
      "F", "v", "y", "z",
    };
    String[] fourWide = {
      "f", "r", " ",
    };
    String[] threeWide = {
      "t",
    };
    String[] twoWide = {"I", "i", "j", "l", "'", "!"};

    for (int i = 0; i < text.length(); i++) {
      String currentChar = String.valueOf(text.charAt(i));
      if (Arrays.asList(twelveWide).contains(currentChar)) {
        spacing += 11; // One less because there isn't a pixel gap after them
      } else if (Arrays.asList(tenWide).contains(currentChar)) {
        spacing += 9; // One less because there isn't a pixel gap after them
      } else if (Arrays.asList(nineWide).contains(currentChar)) {
        spacing += 9;
      } else if (Arrays.asList(eightWide).contains(currentChar)) {
        spacing += 8;
      } else if (Arrays.asList(sevenWide).contains(currentChar)) {
        spacing += 7;
      } else if (Arrays.asList(sixWide).contains(currentChar)) {
        spacing += 6;
      } else if (Arrays.asList(fiveWide).contains(currentChar)) {
        spacing += 5;
      } else if (Arrays.asList(fourWide).contains(currentChar)) {
        spacing += 4;
      } else if (Arrays.asList(threeWide).contains(currentChar)) {
        spacing += 3;
      } else if (Arrays.asList(twoWide).contains(currentChar)) {
        spacing += 2;
      }
    }
    spacing += (text.length() / 2);
    Integer newSpacing = (width / 2) - (spacing / 2);
    return newSpacing;
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      int top = 20;
      int left = 6;
      int paintWidth = 182;
      int paintHeight = 32;
      int alphaBoxHeight = 16;
      int borderColor = 255255255;
      int bgColor1 = 606060;
      int bgColor2 = 200000;
      int bgOpacity = 255;

      int nextTop = 0;

      c.drawBoxAlpha(left, top, paintWidth, alphaBoxHeight * 2, bgColor1, bgOpacity);
      drawCenteredString("@whi@" + QUEST_NAME, paintWidth + (left * 4), top + 13, bgColor1, 1);
      drawCenteredString(
          "@yel@" + CURRENT_QUEST_STEP,
          paintWidth + (left * 4),
          top + alphaBoxHeight + 13,
          bgColor1,
          1);
      // Paint Item Requirements
      if (STEP_ITEMS.length > 0 && STEP_ITEMS[0][0] != null) {
        for (int i = 0; i < STEP_ITEMS.length; i++) {
          String itemCount = getQuestStepItems(i, 2);
          int itemCountSpacing = 160 - (7 * (itemCount.length() - 10));

          paintHeight += alphaBoxHeight;
          nextTop = top + (alphaBoxHeight * 2) + (i * alphaBoxHeight);
          c.drawBoxAlpha(left, nextTop, paintWidth, alphaBoxHeight, bgColor2, bgOpacity);
          c.drawString(getQuestStepItems(i, 1), left + 2, nextTop + 12, bgColor1, 1);
          c.drawString(itemCount, itemCountSpacing, nextTop + 12, bgColor1, 1);
        }
      }
      c.drawBoxBorder(left, top, paintWidth, paintHeight, borderColor);
    }
  }
}
