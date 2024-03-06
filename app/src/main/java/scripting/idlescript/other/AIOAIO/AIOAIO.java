package scripting.idlescript.other.AIOAIO;

import bot.Main;
import scripting.idlescript.IdleScript;
import scripting.idlescript.other.AIOAIO.core.AIOAIO_State;
import scripting.idlescript.other.AIOAIO.core.gui.AIOAIO_GUI;

public class AIOAIO extends IdleScript {
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
  public static AIOAIO_State state = new AIOAIO_State();

  public static final String VERSION = "1.11.2";

  public int start(String[] parameters) {
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
        Main.log("A straight JVM error occured! " + t.getMessage());
        Main.log("I don't know _why_ this happens! Just gonna sleep 20s and ignore it lol");
        return 20000;
      }
    }
    return 50;
  }

  private int loop() {
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
    return state.currentTask.getAction().get();
  }

  @Override
  public void paintInterrupt() {
    Main.getController().drawString("@red@AIOAIO v" + AIOAIO.VERSION, 6, 21, 0xFFFFFF, 1);
    String currentSkillText =
        "Current Skill: " + (state.currentSkill != null ? state.currentSkill.getName() : "None");
    String currentTaskText =
        "Current Task: " + (state.currentTask != null ? state.currentTask.getName() : "None");
    Main.getController().drawString("@red@" + currentSkillText, 6, 35, 0xFFFFFF, 1);
    Main.getController().drawString("@red@AIOAIO status: " + state.status, 6, 49, 0xFFFFFF, 1);
    Main.getController().drawString("@red@" + currentTaskText, 6, 63, 0xFFFFFF, 1);
    long timeRemaining = state.endTime - System.currentTimeMillis();
    String timeRemainingText =
        "Time remaining: " + (timeRemaining > 0 ? timeRemaining / 1000 + " seconds" : "None");
    Main.getController().drawString("@red@" + timeRemainingText, 6, 77, 0xFFFFFF, 1);
  }
}
