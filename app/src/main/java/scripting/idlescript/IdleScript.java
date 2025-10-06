package scripting.idlescript;

import bot.Main;
import bot.ui.scriptselector.models.ScriptInfo;
import controller.Controller;
import controller.PaintBuilder.PaintBuilder;
import controller.PaintBuilder.RowBuilder;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This is the base class from which all IdleScript scripts are derived; this class also contains
 * interrupts called by patched client hooks.
 *
 * @author Dvorak
 */
public abstract class IdleScript {
  Controller controller = null;
  protected PaintBuilder paintBuilder = new PaintBuilder(4, 18, 214);
  protected RowBuilder rowBuilder = new RowBuilder();

  // These classes are omitted from the cleanup override check
  protected final Class<?>[] excludedClasses = {Controller.class, ScriptInfo.class};

  /**
   * Called by {@link callbacks.MessageCallback} every time a new server message is drawn on the
   * screen. <b>Override this in your script to process these messages.</b>
   *
   * @param message -- variable containing the message.
   */
  public void serverMessageInterrupt(String message) {}

  /**
   * Called by {@link callbacks.MessageCallback} every time a new private message is received on the
   * screen. <b>Override this in your script to process these messages.</b>
   *
   * @param sender -- variable containing the sender.
   * @param message -- variable containing the message.
   */
  public void privateMessageReceivedInterrupt(String sender, String message) {}

  /**
   * Called by {@link callbacks.MessageCallback} every time a new chat message is drawn on the
   * screen. <b>Override this in your script to process these messages.</b>
   *
   * @param message -- variable containing the message.
   */
  public void chatMessageInterrupt(String message) {}

  /**
   * Called by {@link callbacks.MessageCallback} every time a new quest message is drawn on the
   * screen. <b>Override this in your script to process these messages.</b>
   *
   * @param message -- variable containing the message.
   */
  public void questMessageInterrupt(String message) {}

  /**
   * Called by {@link callbacks.MessageCallback} every time a new trade message is drawn on the
   * screen. <b>Override this in your script to process these messages.</b>
   *
   * @param message -- variable containing the message.
   */
  public void tradeMessageInterrupt(String message) {}

  public void setController(Controller _controller) {
    controller = _controller;
  }
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * the script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    System.out.println("If you see this, your script did not come with the start function.");
    return 0;
  }

  /**
   * This method is executed during the script shutdown process. <br>
   * <br>
   * It should be used to reset modified static fields to ensure that the next instance of this
   * script—or, if this is a superclass, any child class—starts with correctly reset values. Only
   * static fields that are changed during script runtime need to be reset in the override.<br>
   * <br>
   * If implementing this in a superclass, consider delegating reset logic for child classes to a
   * secondary method (e.g., cleanupScript()) and calling it from the super class's overridden
   * cleanup method. This ensures that child classes can override the secondary method without
   * accidentally bypassing the original cleanup logic. <br>
   * <br>
   * For an example implementation for superclasses look at {@link SeattaScript}<br>
   * <br>
   * Note: Classes can be added to excludedStaticClassTypes to ignore them when checking for static
   * fields. <br>
   * <br>
   */
  public void cleanup() {
    Class<?> current = this.getClass();
    List<String> classList = new ArrayList<>();

    // Loops through the running script and its superclasses until reaching IdleScript.
    // Adds any classes with non-excluded static fields to a list, which is printed as a warning
    // stating that cleanup overriding may be necessary.
    while (current != null && current != IdleScript.class) {
      Class<?> checkClass = current;
      boolean hasStaticFields =
          Arrays.stream(checkClass.getDeclaredFields())
              .anyMatch(
                  f ->
                      Modifier.isStatic(f.getModifiers())
                          && Arrays.stream(excludedClasses)
                              .noneMatch(c -> c.isAssignableFrom(f.getType())));
      if (hasStaticFields) classList.add(checkClass.getSimpleName());
      current = current.getSuperclass();
    }

    // Reverse the list to show them in hierarchical order
    Collections.reverse(classList);

    // Print a warning if the list isn't empty.
    if (!classList.isEmpty()) {

      String staticStringNotice =
          String.format(
              "The following class%s not override cleanup. This could cause variables to not be reset upon stopping the script: %n"
                  + "   %s%n",
              classList.size() > 1
                  ? "es declare static fields, but do"
                  : " declares static fields, but does",
              classList);
      Main.logError(staticStringNotice);
    }
  }

  /**
   * This will be called by {@link callbacks.DrawCallback} every game frame. <b>Override this in
   * your script to paint on the screen.</b>
   */
  public void paintInterrupt() {
    // Draws a placeholder paint on scripts that haven't overridden paintInterrupt.
    if (controller != null) {
      paintBuilder.drawPlaceholderPaint();
    }
  }

  /**
   * This is called by {@link callbacks.DamageCallback} every time the NPC we are currently fighting
   * is damaged. <b>Override this in your script to intercept hitsplats.</b>
   */
  public void npcDamagedInterrupt(int currentHealth, int damageAmount) {}

  /**
   * This is called by {@link callbacks.DamageCallback} every time the player is damaged.
   * <b>Override this in your script to intercept hitsplats.</b>
   */
  public void playerDamagedInterrupt(int currentHealth, int damageAmount) {}

  /**
   * This is called by {@link callbacks.CommandCallback} if a player types a "::" command into the
   * chatbox. <b>Override this in your script to intercept and read these.</b>
   */
  public void chatCommandInterrupt(String commandText) {}

  /**
   * This is called by {@link callbacks.KeyCallback} every time the user presses a key. <b>Override
   * this in your script to implement actions on key presses.</b>
   */
  public void keyPressInterrupt(int keyCode) {}
}
