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
  public static boolean toggleOnViewId = false;
  public static long nextRefresh = -1;
  public static long nextDeRefresh = -1;
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

  private static void drawBotStatus(Controller c) {
    int y = 130;
    String localStatusText = statusText;

    if (toggleOnViewId) {
      c.getPlayer().groupID = 9;
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
    if (System.currentTimeMillis() > nextDeRefresh && nextDeRefresh != -1) {
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
