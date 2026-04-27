package scripting.idlescript.SeattaScript.AIOQuester;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.Parameter;
import bot.ui.scriptselector.models.ScriptInfo;
import controller.Controller;
import scripting.idlescript.IdleScript;
import scripting.idlescript.SeattaScript.AIOQuester.models.*;

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
              + "\n - Kaila, Xatain, and Acry",
          new Parameter[] {
            new Parameter(
                "debug",
                "Enables logging of various debug messages\n"
                    + "Useful for getting exact strings for overriding messageInterrupt methods during quest script development\n"),
            new Parameter(
                "test",
                "Runs the internal testing loop\n"
                    + "Useful for testing and developing various methods for QuestHandler\n"
                    + "Not useful for AIOQuester users")
          });

  /* TODO: Don't forget to set these to false before committing
  These are for testing quest stages and methods. There's obviously no way to reinitialize a
  certain dialog state with an npc apart from having server permissions to reset stages, so stage
  testing is only used for pathing/interacting with objects(unless they're stage-locked/etc...*/

  private QuestHandler qh;
  protected boolean TESTING_LOOP = false;
  public static boolean DEBUGGING = false;
  public static boolean SKIP_QUEST_DEFINED_CHECK = false;
  public static final boolean TESTING_QUESTS = false;
  public static final int TESTING_STAGE = 0;

  public int start(String[] parameters) {
    for (String parameter : parameters) {
      if (parameter.toLowerCase().contains("debug")) {
        DEBUGGING = true;
        Main.log("AIOQuester debugging enabled");
      }
      if (parameter.toLowerCase().contains("test")) {
        TESTING_LOOP = true;
        Main.log("AIOQuester testing loop started");
      }
    }

    qh = new QuestHandler();
    qh.updateBankItems();
    setupAndRun();
    c.stop();
    return 1000;
  }

  /** Starts the script ui; then passes the selected quest's script class to the QuestHandler. */
  private void setupAndRun() {
    QuestHandler.paintBuilder = null;
    QuestHandler.rowBuilder = null;

    QuestDef quest = TESTING_LOOP ? QuestDef.loopTesting : QuestUI.showFrame();
    if (quest != null) {
      QuestHandler.paintBuilder = paintBuilder;
      QuestHandler.rowBuilder = rowBuilder;
      QuestHandler.QUEST = quest;
      if (TESTING_LOOP) {
        qh.startTestingLoop();
      } else {
        if (quest.getScript() == null) {
          System.out.println(
              "The quest: '" + quest.getName() + "' does not have a script class assigned to it");
          c.stop();
        }
        if (c.isRunning()) qh.start(quest.getScript());
      }
    }
  }

  @Override
  public void cleanup() {
    qh.cleanup();
  }

  @Override
  public void serverMessageInterrupt(String message) {
    // Do server message checks in the QuestHandler method below instead here.
    qh._serverMessageInterrupt(message);
  }

  @Override
  public void questMessageInterrupt(String message) {
    // Do server message checks in the QuestHandler method below instead here.
    qh._questMessageInterrupt(message);
  }

  @Override
  public void tradeMessageInterrupt(String message) {
    // Do trade message checks in the QuestHandler method below instead here.
    qh._tradeMessageInterrupt(message);
  }

  @Override
  public void chatMessageInterrupt(String message) {
    // Do chat message checks in the QuestHandler method below instead here.
    qh._chatMessageInterrupt(message);
  }

  @Override
  public void privateMessageReceivedInterrupt(String sender, String message) {
    // Do private message checks in the QuestHandler method below instead here.
    qh._privateMessageInterrupt(sender, message);
  }

  @Override
  public void paintInterrupt() {
    if (c != null && qh != null) qh.drawPaint();
  }
}
