package scripting.idlescript.AIOQuester;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import controller.Controller;
import scripting.idlescript.AIOQuester.models.*;
import scripting.idlescript.IdleScript;

/**
 * This is the main entry point of AIOQuester. This class starts the setup ui which returns a
 * QuestDef. The script class for that QuestDef is then passed off to QuestHandler.start(class) to
 * actually run the script.
 */
public class AIOQuester extends IdleScript {
  protected static final Controller c = Main.getController();
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.QUESTING, Category.IRONMAN_SUPPORTED, Category.ULTIMATE_IRONMAN_SUPPORTED
          },
          "Seatta",
          "An all-in-one questing script."
              + "\n\nNOT ALL QUESTS ARE AVAILABLE YET!"
              + "\n\nSpecial thanks to:"
              // Feel free to add your name here if you've helped write quest scripts.
              + "\n - Kaila");

  /* TODO: Don't forget to set these to false before committing
  These are for testing quest stages and methods. There's obviously no way to reinitialize a
  certain dialog state with an npc apart from having server permissions to reset stages, so stage
  testing is only used for pathing/interacting with objects(unless they're stage-locked/etc...*/
  protected static final boolean TESTING_LOOP = false;
  public static final boolean SKIP_QUEST_DEFINED_CHECK = false;
  public static final boolean TESTING_QUESTS = false;
  public static final int TESTING_STAGE = 0;

  public int start(String[] parameters) {
    QuestHandler.updateBankItems();
    setupAndRun();
    c.stop();
    return 1000;
  }

  /** Starts the script ui; then passes the selected quest's script class to the QuestHandler. */
  private void setupAndRun() {
    QuestHandler.paintBuilder = null;
    QuestHandler.rowBuilder = null;
    QuestHandler.quest = null;

    QuestDef quest = TESTING_LOOP ? QuestDef.loopTesting : QuestUI.showFrame();
    if (quest != null) {
      QuestHandler.paintBuilder = paintBuilder;
      QuestHandler.rowBuilder = rowBuilder;
      QuestHandler.quest = quest;
      if (TESTING_LOOP) {
        QuestHandler.startTestingLoop();
      } else {
        if (quest.getScript() == null) {
          System.out.println(
              "The quest: '" + quest.getName() + "' does not have a script class assigned to it");
          c.stop();
        }
        if (c.isRunning()) QuestHandler.start(quest.getScript());
      }
    }
  }

  @Override
  public void serverMessageInterrupt(String message) {
    // Do server message checks in the QuestHandler method instead of in here.
    QuestHandler.serverMessageInterrupt(message);
  }

  @Override
  public void paintInterrupt() {
    if (c != null) QuestHandler.drawPaint();
  }
}
