package callbacks;

import bot.Main;
import controller.Controller;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import orsc.enumerations.MessageType;
import scripting.idlescript.IdleScript;

/**
 * Contains interrupts which are called every time the patched client receives a message.
 *
 * @author Dvorak
 */
public class MessageCallback {

  private static int sbotLastChatter = 0;
  private static String sbotLastChatterName = "";
  private static String sbotLastChatMessage = "";
  private static String sbotLastNPCMessage = "";
  private static String sbotLastServerMessage = "";
  private static final Pattern p =
      Pattern.compile("^(.*) (.*) level!"); // for parsing level up messages
  // message is "You just advanced 1 hitpoints level"
  // old Pattern.compile("^(.*) (.*) level!");
  // new Pattern.compile("^You just advanced \\d (.*) level!$");
  /**
   * The hook called by the patched client every time a message is printed on screen.
   *
   * @param crownEnabled for admin crowns
   * @param sender String name of sender
   * @param message String text of message
   * @param type MessageType of message (server, game, etc)
   * @param crownID Id for admin crowns
   * @param formerName String
   * @param colourOverride String color to send message in
   */
  public static void messageHook(
      boolean crownEnabled,
      String sender,
      String message,
      MessageType type,
      int crownID,
      String formerName,
      String colourOverride) {
    Controller con = Main.getController();
    con.hideRecoveryDetailsMenu();
    con.hideContactDetailsMenu();
    if (Main.isLogWindowOpen() && DrawCallback.getNextLogClear() == -1) {
      DrawCallback.setNextLogClear(System.currentTimeMillis() + 86400000L); // add 24 hrs in ms
      // cd.log(String.valueOf((timeNextLogClear - System.currentTimeMillis()) / 1000L) + " s");
    }
    if (Main.isLogWindowOpen() && (System.currentTimeMillis() > DrawCallback.getNextLogClear())) {
      Main.clearLog();
      DrawCallback.setNextLogClear(System.currentTimeMillis() + 86400000L); // add 24 hrs in ms
    }
    if ((System.currentTimeMillis() > DrawCallback.getNextRefresh())
        && DrawCallback.getNextRefresh() != -1) {
      if (!con.isDrawEnabled()) {
        con.setDrawing(true);
        DrawCallback.setNextDeRefresh(System.currentTimeMillis() + 20L); // toggle on gfx 1 frame
        DrawCallback.setNextRefresh(System.currentTimeMillis() + 30000L); // wait 1 min for refresh
        /* System.out.println("Next screen refresh in: " + ((DrawCallback.nextRefresh -
        System.currentTimeMillis()) / 1000L) + "s");*/
      } else DrawCallback.setNextRefresh(-1);
    }
    if (type == MessageType.GAME) {
      if (message.contains("You just advanced")) {
        // handleLevelUp(message);
      } else if (message.contains("You have been standing here for")) {
        Controller c = Main.getController();
        System.out.println("got standing message!");

        if (c != null) c.moveCharacter(); // this is responsible for auto-walk!
      }
    }

    if (Main.isRunning() && Main.getCurrentRunningScript() != null) {
      if (Main.getCurrentRunningScript() instanceof IdleScript) {
        if (type == MessageType.GAME) {
          ((IdleScript) Main.getCurrentRunningScript()).serverMessageInterrupt(message);
        } else if (type == MessageType.PRIVATE_RECIEVE) {
          ((IdleScript) Main.getCurrentRunningScript())
              .privateMessageReceivedInterrupt(sender, message);
        } else if (type == MessageType.CHAT) {
          ((IdleScript) Main.getCurrentRunningScript())
              .chatMessageInterrupt(sender + ": " + message);
        } else if (type == MessageType.QUEST) {
          ((IdleScript) Main.getCurrentRunningScript()).questMessageInterrupt(message);
        } else if (type == MessageType.TRADE) {
          ((IdleScript) Main.getCurrentRunningScript()).tradeMessageInterrupt(sender);
        }
      } else if (Main.getCurrentRunningScript() instanceof compatibility.sbot.Script) {
        if (type == MessageType.GAME) {
          ((compatibility.sbot.Script) Main.getCurrentRunningScript()).ServerMessage(message);
          sbotLastServerMessage = message;
        } else if (type == MessageType.CHAT) {
          ((compatibility.sbot.Script) Main.getCurrentRunningScript())
              .ChatMessage(sender + ": " + message);

          sbotLastChatter =
              (int)
                  (System.currentTimeMillis()
                      / 1000L); // 2038 problem, but that's what you get for using sbot scripts in
          // 2038.
          sbotLastChatMessage = message;
          sbotLastChatterName = sender;
        } else if (type == MessageType.QUEST) {
          ((compatibility.sbot.Script) Main.getCurrentRunningScript()).NPCMessage(message);
          sbotLastNPCMessage = message;
        }
        //                else if (type == MessageType.TRADE) { //UNTESTED
        //                    ((compatibility.sbot.Script)
        // Main.getCurrentRunningScript()).TradeRequest(message.split(" ")[0]); //needs to be
        // converted to player pid
        //                }
      } else if (Main.getCurrentRunningScript() instanceof compatibility.apos.Script) {
        //
        //	if(((compatibility.apos.Script)Main.getCurrentRunningScript()).isControllerSet()) {
        if (type == MessageType.GAME) {
          ((compatibility.apos.Script) Main.getCurrentRunningScript()).onServerMessage(message);
        } else if (type == MessageType.CHAT) {
          ((compatibility.apos.Script) Main.getCurrentRunningScript())
              .onChatMessage(message, sender, false, false);
        } else if (type == MessageType.QUEST) {
          ((compatibility.apos.Script) Main.getCurrentRunningScript()).onServerMessage(message);
        } else if (type == MessageType.TRADE) { // UNTESTED
          ((compatibility.apos.Script) Main.getCurrentRunningScript()).onTradeRequest(sender);
        } else if (type == MessageType.PRIVATE_RECIEVE) { // UNTESTED
          ((compatibility.apos.Script) Main.getCurrentRunningScript())
              .onPrivateMessage(message, sender, false, false);
        }
        //            	}
      }
    }
  }
  /**
   * Handles the level up event. to display level up text and take a screenshot. <br>
   * Currently bugged on certain level ups not returning the correct statId (invalid statId
   * generated)
   *
   * @param message the message containing the level up information
   */
  private static void handleLevelUp(String message) {
    Controller c = Main.getController();
    String skillName = null;
    int skillLevel = -1;
    // old Pattern.compile("^(.*) (.*) level!");
    // new Pattern.compile("^You just advanced \\d (.*) level!$");
    System.out.println("matching begin");
    Matcher m = p.matcher(message); // "You just advanced 1 hitpoints level"
    System.out.println("p.matcher(message) - " + p.matcher(message));
    if (m.find()) {
      skillName = m.group(2);
      int statId = c.getStatId(skillName);
      c.log("skillName - " + skillName);
      c.log("statId" + statId); // invalid skill Id is being generated...
      if (statId == -1) {
        throw new IllegalArgumentException("Invalid skill name: " + skillName);
      }
      if (skillName != null && skillName.length() > 0 && statId > 0) {
        if (skillName.equalsIgnoreCase("woodcut")) skillName = "Woodcutting"; // fix woodcut

        skillName =
            Character.toUpperCase(skillName.charAt(0)) // capitalize skill name first letter
                + skillName.substring(1); // lowercase the rest

        skillLevel = c.getBaseStat(statId); // this is returning skillLevel = -1

        DrawCallback.displayAndScreenshotLevelUp(skillName, skillLevel);
      }
    } else {
      System.out.println("no find");
    }
  }

  /**
   * Provides data for the LastChatter() function in SBot.
   *
   * @return int
   */
  public static int getSbotLastChatter() {
    return sbotLastChatter;
  }

  /**
   * Provides data for the LastChatterName() function in SBot.
   *
   * @return String -- guaranteed to not be null.
   */
  public static String getSbotLastChatterName() {
    return sbotLastChatterName;
  }

  /**
   * Provides data for the LastChatMessage() function in SBot.
   *
   * @return String -- guaranteed to not be null.
   */
  public static String getSbotLastChatMessage() {
    return sbotLastChatMessage;
  }

  /**
   * Provides data for the LastNPCMessage() function in SBot.
   *
   * @return String -- guaranteed to not be null.
   */
  public static String getSbotLastNPCMessage() {
    return sbotLastNPCMessage;
  }

  /**
   * Provides data for the LastServerMessage() function in SBot.
   *
   * @return String -- guaranteed to not be null.
   */
  public static String getSbotLastServerMessage() {
    return sbotLastServerMessage;
  }
}
