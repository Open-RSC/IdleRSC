package scripting.idlescript.other.AIOAIO;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import scripting.idlescript.IdleScript;
import scripting.idlescript.other.AIOAIO.core.AIOAIO_State;
import scripting.idlescript.other.AIOAIO.core.gui.AIOAIO_GUI;

public class AIOAIO extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.AGILITY,
            Category.COMBAT,
            Category.COOKING,
            Category.FISHING,
            Category.FLETCHING,
            Category.MINING,
            Category.SMITHING,
            Category.THIEVING,
            Category.WOODCUTTING,
            Category.IRONMAN_SUPPORTED,
            Category.BROKEN
          },
          "Red Bracket",
          "One script to rule them all.");

  // * Apparently this is broken, so I gave it the BROKEN category until it's working again -Seatta

  /**
   * Welcome to AIO AIO! The goal of this script is to train your account in all aspects.
   *
   * <p>Feel free to expand it! The priorities are _reliability_ and _maintainability_. Individual
   * tasks should be very robust and not affect anything out of their file (otherwise this AIO AIO
   * script would get very messy with a million variables).
   *
   * <p>The goal of AIO AIO is to eventually have a "PKing" task that makes every bot using this
   * script do PK events at certain times of the day! (It's also just fun to watch your account
   * progress on its own, with no user input)
   */
  public static AIOAIO_State state;

  public static final String VERSION = "1.18.1";

  public int start(String[] parameters) {
    paintBuilder.start(4, 4, 240);
    if (state == null) state = new AIOAIO_State();
    if (!state.guiSetup) {
      state.guiSetup = true;
      if (parameters.length >= 1 && parameters[0].equals("nogui")) {
        state.startPressed = true;
      } else {
        AIOAIO_GUI.setupGUI();
      }
    }
    if (state.startPressed) {
      try {
        return Math.max(loop(), 50);
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Exception occured! Sleeping 1.5s...");
        return 1500;
      } catch (Throwable t) {
        t.printStackTrace();
        Main.log("A straight JVM error occured! Check stdout for details.");
        Main.log("I don't know _why_ this happens! Just gonna sleep 20s and ignore it lol");
        return 20000;
      }
    }
    return 50;
  }

  private int loop() {
    if (!state.postLoginSetup && Main.getController().isLoggedIn()) {
      loginSetup();
    }
    if (System.currentTimeMillis() >= state.endTime) {
      state.currentSkill = state.botConfig.getRandomEnabledSkill();
      state.currentTask = state.currentSkill.getRandomEnabledTask();
      Main.getController()
          .log(
              "Picked new task: "
                  + state.currentTask.getName()
                  + " ("
                  + state.currentSkill.getName()
                  + ")");
      state.endTime = System.currentTimeMillis() + 600_000;
      state.taskStartup = true;
    }
    AIOAIO_Script_Utils.checkAccountValue();
    return state.currentTask.getAction().get();
  }

  private void loginSetup() {
    Main.getController().hideWelcomeScreen();
    state.initLevel = Main.getController().getTotalLevel();
    state.postLoginSetup = true;
  }

  @Override
  public void paintInterrupt() {
    AIOAIO_Paint.paint(paintBuilder, rowBuilder);
  }
}
