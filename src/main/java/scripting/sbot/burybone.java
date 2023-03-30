package scripting.sbot;

import compatibility.sbot.Script;

public class burybone extends Script {

  int level;

  public String[] getCommands() {
    return new String[] {"burybone"};
  }

  public void start(String command, String[] parameter) {
    level = Integer.parseInt(parameter[0]);
    while (Running()) {
      if (Fatigue() >= 90 && Running()) {
        while (!Sleeping() && Running()) {
          Use(FindInv(1263));
          Wait(2500);
        }
        while (Sleeping() && Running()) {
          Wait(100);
        }
      }
      while (InvCount() < 30) {
        int[] i = GetNearestItem(20);
        TakeItem(i[0], i[1], 20);
        Wait(1000);
      }
      if (InvCount() == 30) {
        while (InvCount(20) > 0) {
          if (Fatigue() >= 90 && Running()) {
            while (!Sleeping() && Running()) {
              Use(FindInv(1263));
              Wait(2500);
            }
            while (Sleeping() && Running()) {
              Wait(100);
            }
          }
          int bone = FindInv(20);
          Use(bone);
          Wait(500);
        }
      }
      if (GetCurrentStat(5) == level) {
        Logout();
      }
      if (!LoggedIn() && GetCurrentStat(5) != level) {
        Login("funman", "hehehe");
      }
    }
    DisplayMessage("@red@Bury Bones STOPPED", 3);
  }
}
