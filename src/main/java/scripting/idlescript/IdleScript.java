package scripting.idlescript;

import controller.Controller;

/**
 * This is the base class from which all IdleScript scripts are derived; this class also contains
 * interrupts called by patched client hooks.
 *
 * @author Dvorak
 */
public abstract class IdleScript {
  Controller controller = null;

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
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    System.out.println("If you see this, your script did not come with the start function.");
    return 0;
  }

  /**
   * This will be called by {@link callbacks.DrawCallback} every game frame. <b>Override this in
   * your script to paint on the screen.</b>
   */
  public void paintInterrupt() {}

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
