package scripting.sbot;

import compatibility.sbot.Script;

public class WallJumper extends Script {
  public String[] getCommands() {
    return new String[] {"jumpwall"};
  }

  public void start(String command, String parameter[]) {
    DisplayMessage(
        "@ran@=@ran@=@ran@=@ran@=@ran@= @whi@Low Wall Jumper Activated! @ran@=@ran@=@ran@=@ran@=@ran@=",
        3);
    DisplayMessage(
        "@ran@=@ran@=@ran@=@ran@=@ran@= @whi@Created by SaladFork! @ran@=@ran@=@ran@=@ran@=@ran@=",
        3);
    while (Running()) {
      if (Fatigue() >= 95 && Running() == true) {
        while (Sleeping() == false && Running() == true) {
          Use(FindInv(1263));
          Wait(2500);
        }
        while (Sleeping() == true && Running() == true) Wait(100);
      }
      OpenDoor(495, 558, 1);
      Wait(2000);
    }
    DisplayMessage("@red@STOPPED", 3);
  }
}
