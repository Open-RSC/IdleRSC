package scripting.sbot;

import compatibility.sbot.Script;

public class AgilityNet extends Script {

  public String[] getCommands() {
    return new String[] {"agilitynet"};
  }

  public void start(String command, String[] parameter) {
    DisplayMessage("Gnome Agility Net - Flop", 3);
    while (Running()) {
      AtObject(683, 502);
      Wait(618);
      {
        if (Fatigue() >= 95 && Running()) {
          while (!Sleeping() && Running()) {
            Use(FindInv(1263));
            Wait(2500);
          }
          while (Sleeping() && Running()) Wait(100);
        }
      }
    }
    DisplayMessage("@red@STOPPED", 3);
  }
}
