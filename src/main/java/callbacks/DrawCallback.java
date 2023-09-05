package callbacks;

import bot.Main;
import controller.Controller;
import scripting.idlescript.IdleScript;

/**
 * Contains logic for interrupts caused by each drawing of the game frame.
 *
 * @author Dvorak
 */
public class DrawCallback {

  private static long startTimestamp = System.currentTimeMillis() / 1000L;
  private static long startingXp = Long.MAX_VALUE;
  private static String statusText = "@red@Botting!";
  private static String levelUpSkill = "", levelUpLevel = "";
  private static String levelUpText = "";
  private static long levelUpTextTimeout = 0;
  private static boolean screenshotTaken = true;
  private static boolean toggleOnViewId = false;
  private static long nextRefresh = -1;
  private static long nextDeRefresh = -1;
  private static long timeNextLogClear = -1;

  /**
   * Returns the value of the next refresh.
   *
   * @return the value of the next refresh
   */
  public static long getNextRefresh() {
    return nextRefresh;
  }
  /**
   * A description of the entire Java function.
   *
   * @return description of return value
   */
  public static long getNextDeRefresh() {
    return nextDeRefresh;
  }
  /**
   * Retrieves the next time the log will be cleared.
   *
   * @return the next time the log will be cleared
   */
  public static long getNextLogClear() {
    return timeNextLogClear;
  }
  /**
   * Retrieves the value of the toggleOnViewId variable.
   *
   * @return the value of the toggleOnViewId variable
   */
  public static boolean getToggleOnViewId() {
    return toggleOnViewId;
  }
  /**
   * Sets the value of the nextRefresh variable.
   *
   * @param nextRefreshValue the value to set for the nextRefresh variable
   */
  public static void setNextRefresh(long nextRefreshValue) {
    nextRefresh = nextRefreshValue;
  }
  /**
   * Sets the value of the nextDeRefresh variable.
   *
   * @param nextDeRefreshValue the new value for nextDeRefresh
   */
  public static void setNextDeRefresh(long nextDeRefreshValue) {
    nextDeRefresh = nextDeRefreshValue;
  }
  /**
   * Sets the value of the next log clear time.
   *
   * @param nextLogClear the next log clear time to be set
   */
  public static void setNextLogClear(long nextLogClear) {
    timeNextLogClear = nextLogClear;
  }
  /**
   * Sets the value of the toggleOnViewId variable.
   *
   * @param toggleOnViewIdValue the new value for toggleOnViewId
   */
  public static void setToggleOnViewId(boolean toggleOnViewIdValue) {
    toggleOnViewId = toggleOnViewIdValue;
  }

  /** The hook called each frame by the patched client. */
  public static void drawHook() {
    Controller c = Main.getController();

    drawBotStatus(c);
    drawScript(c);
  }
  /**
   * Sets the left hand pane bot status text.
   *
   * @param str "@col@string status text"
   */
  public static void setStatusText(String str) {
    statusText = str;
  }
  /** Draws the status menu of the bot (the onscreen GUI showing hp/prayer/position/etc) */
  private static void drawBotStatus(Controller c) {
    int y = 130;
    String localStatusText = statusText;

    if (toggleOnViewId) {
      c.getPlayer().groupID = 9; // set developer group id to show itemIds
      orsc.Config.C_SIDE_MENU_OVERLAY = false; // turn off side menu to fix flickering bug
    } else {
      c.getPlayer().groupID = 10;
    }

    if (c.getShowBotPaint()) { // when true, this shows the left hand paint section

      int currentHits = c.getCurrentStat(c.getStatId("Hits"));
      int currentPrayer = c.getCurrentStat(c.getStatId("Prayer"));
      int maxHits = c.getBaseStat(c.getStatId("Hits"));
      int maxPrayer = c.getBaseStat(c.getStatId("Prayer"));
      int fatigue = c.getFatigue();

      c.drawString("Hits: " + currentHits + "@red@/@whi@" + maxHits, 7, y, 0xFFFFFF, 1);
      y += 14;
      c.drawString("Prayer: " + currentPrayer + "@red@/@whi@" + maxPrayer, 7, y, 0xFFFFFF, 1);
      y += 14;
      if (c.isAuthentic()) { // hidden on coleslaw so that submenu ON, Npc Kill counts ON can show
        // Kill counter at that screen location! Uranium will still get fatigue.
        c.drawString("Fatigue: " + fatigue + "@red@%", 7, y, 0xFFFFFF, 1);
      }
      y += 14;

      if (!Main.isRunning()) {
        localStatusText = "@red@Idle.";
      }

      if (c.getShowStatus()) c.drawString("Status: " + localStatusText, 7, y, 0xFFFFFF, 1);

      y += 14;

      if (c.getShowCoords())
        c.drawString(
            "Coords: @red@(@whi@" + c.currentX() + "@red@,@whi@" + c.currentY() + "@red@)",
            7,
            y,
            0xFFFFFF,
            1);

      y += 14;
      long totalXp = getTotalXp();
      startingXp = Math.min(totalXp, startingXp);
      long xpGained = totalXp - startingXp;
      long xpPerHr;
      long runTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = runTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        xpPerHr = (int) (xpGained * scale);
      } catch (Exception e) {
        xpPerHr = 0;
      }

      if (c.getShowXp()) {
        c.drawString(
            "XP Gained: @red@"
                + String.format("%,d", xpGained)
                + " @whi@(@red@"
                + String.format("%,d", xpPerHr)
                + " @whi@xp/hr)",
            7,
            y,
            0xFFFFFF,
            1);
      }
    }
    if ((System.currentTimeMillis() > nextDeRefresh) && nextDeRefresh != -1) {
      c.setDrawing(false);
      nextDeRefresh = -1;
    }
    if (System.currentTimeMillis() / 1000L < levelUpTextTimeout) {
      y += 14;
      c.drawString(levelUpText, 7, y, 0xFFFFFF, 1);
      if (!screenshotTaken) {
        c.takeScreenshot(levelUpLevel + "_" + levelUpSkill + "_");
        screenshotTaken = true;
      }
    }
  }
  /** Draws the script on the controller. */
  private static void drawScript(Controller c) {

    if (c != null
        && c.getShowBotPaint()
        && c.isRunning()
        && Main.getCurrentRunningScript() != null) {
      if (Main.getCurrentRunningScript() instanceof IdleScript) {
        ((IdleScript) Main.getCurrentRunningScript()).paintInterrupt();
      } else if (Main.getCurrentRunningScript() instanceof compatibility.apos.Script) {
        //
        //	if(((compatibility.apos.Script)Main.getCurrentRunningScript()).isControllerSet()) {
        try {
          ((compatibility.apos.Script) Main.getCurrentRunningScript()).paint();
        } catch (Exception e) {
          // catch paint exceptions caused by a lot of apos scripts
        }
        //            	}
      }
    }
  }
  /**
   * Returns the total XP by summing up the player experience for each stat.
   *
   * @return the total XP as a long value
   */
  private static long getTotalXp() {
    Controller c = Main.getController();

    long result = 0;

    for (int statIndex = 0; statIndex < c.getStatCount(); statIndex++) {
      result += c.getPlayerExperience(statIndex); // c.getStatXp(statIndex);
    }

    return result;
  }

  /**
   * Displays level up messages and takes screenshot the next frame.
   *
   * @param statName string
   * @param level integer
   */
  public static void displayAndScreenshotLevelUp(String statName, int level) {
    screenshotTaken = false;
    levelUpSkill = statName;
    levelUpLevel = String.valueOf(level);

    levelUpText = "@red@" + level + " @whi@" + statName + "@red@!";
    levelUpTextTimeout = System.currentTimeMillis() / 1000L + 15; // display for 15seconds
  }
  /** Resets the XP counter and re-initializes the necessary variables. */
  public static void resetXpCounter() {
    Controller c = Main.getController();
    boolean temporaryToggledGFX = false;
    startTimestamp = System.currentTimeMillis() / 1000L;
    startingXp = Long.MAX_VALUE;

    if (c != null) {
      c.displayMessage("@red@IdleRSC@yel@: XP counter reset!");
      if (!c
          .isDrawEnabled()) { // if you reset xp, it will toggle on graphics briefly to reset, then
        // toggle off again. Otherwise, counter will not begin counting xp. It
        // has to reload counter into memory?  ~ Kaila ~
        c.setDrawing(true);
        temporaryToggledGFX = true;
        c.sleep(
            100); // sleep here will not affect scripts running, tested with 10s delay no effect on
        // scripts, it might delay drawcallback, but as graphics were toggled off before
        // reset xp, shouldn't be an issue.
      }
      if (temporaryToggledGFX) {
        c.setDrawing(false);
      }
    }
  }
}
