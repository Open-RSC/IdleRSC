package scripting.sbot;

import compatibility.sbot.Script;

public class Draypowerfishnosleep extends Script {
  public String[] getCommands() {
    return new String[] {"fish"};
  }

  public void start(String command, String parameter[]) {
    DisplayMessage("@cya@we all live in a yellow submarine", 3);
    while (Running()) {
      int pid = PID();
      System.out.println(pid);
      {
        if (Fatigue() >= 99 && Running() == true) {

          Quit();
        }
        AtObject(224, 661);
        Wait(3000);
      }
    }
    DisplayMessage("@red@stoooop", 3);
  }
}
