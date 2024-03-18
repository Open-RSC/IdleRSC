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

  /**
   * Returns the value of the next refresh.
   *
   * @return the value of the next refresh
   */
  public static long getNextRefresh() {
    return nextRefresh;
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
    int y = 116;
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

      int hitsPercentage =
          (currentHits * 100) / maxHits > 100 ? 100 : (currentHits * 100) / maxHits;
      int hitsColor = hitsPercentage <= 25 ? 0xff0000 : hitsPercentage <= 50 ? 0xffff00 : 0x00ff00;

      c.drawProgressBar(
          currentHits, maxHits, 0x000000, hitsColor, 0xffffff, 7, y + 2, 80, 14, false, true);
      // c.drawString("Hits: " + currentHits + "@red@/@whi@" + maxHits, 7, y,
      // 0xFFFFFF, 1);
      y += 16;
      c.drawProgressBar(
          currentPrayer, maxPrayer, 0x000000, 0x8BE9FD, 0xffffff, 7, y + 2, 80, 14, false, true);
      // c.drawString("Prayer: " + currentPrayer + "@red@/@whi@" + maxPrayer, 7, y,
      // 0xFFFFFF, 1);
      y += 14;
      if (c.isAuthentic()) { // hidden on coleslaw so that submenu ON, Npc Kill counts ON can show
        // Kill counter at that screen location! Uranium will still get fatigue.
        c.drawString("Fatigue: " + fatigue + "@red@%", 7, 202, 0xFFFFFF, 1);
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
      long totalXp = c.getTotalXp();
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
      c.setDrawing(false, 0);
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
        // if(((compatibility.apos.Script)Main.getCurrentRunningScript()).isControllerSet())
        // {
        try {
          ((compatibility.apos.Script) Main.getCurrentRunningScript()).paint();
        } catch (Exception e) {
          // catch paint exceptions caused by a lot of apos scripts
        }
        // }
      }
    }
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
    startTimestamp = System.currentTimeMillis() / 1000L;
    startingXp = Long.MAX_VALUE;

    if (c != null) {
      c.log("IdleRSC: XP counter reset!", "gre");
      if (!c.isDrawEnabled()) {
        c.setDrawing(true, 100);
      }
    }
  }
}
