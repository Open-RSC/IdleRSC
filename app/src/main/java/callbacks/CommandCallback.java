package callbacks;

import bot.Main;
import callbacks.chatcommand.IChatCommand;
import callbacks.chatcommand.WalkToCommand;
import controller.Controller;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import scripting.idlescript.IdleScript;

public class CommandCallback {

  private static final Map<String, IChatCommand> COMMANDS = new HashMap<>();

  static {
    COMMANDS.put("walkto", new WalkToCommand());

    COMMANDS.put(
        "paint",
        IChatCommand.ofNoArgs(
            c -> {
              if (c != null) {
                // Toggle current state
                boolean newState = !c.getShowBotPaint();
                c.setBotPaint(newState);

                String msg =
                    newState
                        ? "@red@IdleRSC@yel@: Paint unhidden."
                        : "@red@IdleRSC@yel@: Paint hidden.";
                c.displayMessage(msg);
              }
            },
            " %@red@::paint - @yel@Toggles the bot paint"));

    COMMANDS.put(
        "interlace",
        IChatCommand.ofNoArgs(
            c -> {
              if (c != null) {
                c.setInterlacer(!c.isInterlacing());
                c.displayMessage("@red@Interlacer@yel@: toggled.");
              }
            },
            " %@red@::interlace - @yel@Toggles interlacing mode"));

    COMMANDS.put(
        "gfx",
        IChatCommand.of(
            (args, c) -> {
              if (c != null) c.setDrawing(!c.isDrawEnabled(), 0);
            },
            " %@red@::gfx - @yel@Toggle graphic rendering"));

    COMMANDS.put(
        "bothelp",
        IChatCommand.ofNoArgs(
            c -> {
              if (c != null) c.setServerMessage(getHelpMessageText(), true, true);
            },
            " %@red@::bothelp - @yel@Shows this help menu"));

    COMMANDS.put(
        "toggleid",
        IChatCommand.ofNoArgs(
            c -> {
              if (c != null) {
                c.toggleViewId();
              }
            },
            " %@red@::toggleid - @yel@Toggle Item/Object/NPC right click IDs"));

    COMMANDS.put(
        "screenshot",
        IChatCommand.ofNoArgs(
            c -> {
              if (c != null) c.takeScreenshot("");
            },
            " %@red@::screenshot - @yel@Take a screenshot"));
  }

  private static String getHelpMessageText() {
    return COMMANDS.values().stream()
            .map(IChatCommand::helpString)
            .reduce("@cya@IdleRSC Help Menu:", (acc, s) -> acc + s)
        + " %"
        + " %@red@F1 / F2 - @yel@Toggle Interlacing | Openrsc Left-Side Sub Menu"
        + " %@red@F3 / F4 - @yel@Camera Zoom Reset | 1st-3rd Person Perspective"
        + " %@red@F5 / F6 / F7 / F8 - @yel@Atk, Str, Def Item Swapping, Spell ID casting"
        + " %@red@F9 - @yel@Take screenshot saved to ./IdleRSC/screenshots/username/"
        + " %@red@F10 / F11 / F12 - @yel@Lock camera | Stop current script | Show this help menu";
  }

  public static void commandHook(String command) {
    Controller c = Main.getController();
    if (c == null) return;

    String[] args = command.split(" ");
    String cmd = args[0].toLowerCase();
    String[] cmdArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];

    IChatCommand chatCommand = COMMANDS.get(cmd);
    if (chatCommand != null) {
      chatCommand.execute(cmdArgs, c);
      return;
    }

    if (c.getShowBotPaint()
        && c.isRunning()
        && Main.getCurrentRunningScript() instanceof IdleScript)
      ((IdleScript) Main.getCurrentRunningScript()).chatCommandInterrupt(command);
  }
}
