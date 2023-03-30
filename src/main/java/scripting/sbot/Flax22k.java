package scripting.sbot;

import compatibility.sbot.Script;

public class Flax22k extends Script {
  public String[] getCommands() {
    return new String[] {"flax22k"};
  }

  public final long StartS = (int) (System.currentTimeMillis() / 1000);
  public int Trips = 0;
  public int EXP = 0;
  public int LVL = 0;
  public int FlaxCount = 0;
  public int SpinCount = 0;
  public int SleepCount = 0;
  public boolean Sleep = false;
  public boolean UpX = false;
  public boolean DownX = false;

  public void NPCMessage(String message) {
    if (message.startsWith("@whi@You make the flax into a bow string")) {
      SpinCount++;
      EXP += 15;
    }
  }

  public void ServerMessage(String message) {
    if (message.startsWith("You uproot a flax plant")) {
      FlaxCount++;
    }
    if (message.equals("@gre@You just advanced 1 crafting level!")) {
      LVL++;
    }
    if (message.equals("You climb up the ladder")) UpX = true;
    if (message.equals("You climb down the ladder")) DownX = true;
  }

  public void KeyPressed(int id) {
    if (id == 1012) {
      id = 0;
      float Minutes = (((float) System.currentTimeMillis() / 1000) - (float) StartS) / 60;
      float EXPHour = (float) EXP / (Minutes / 60);
      for (int i = 0; i < 25; i++) System.out.println();
      Println("------------------------");
      Println("Status Report: Flax Crafter");
      Println("------------------------");
      Println("Levels Acheived: " + LVL);
      Println("Crafting XP Gained: " + EXP);
      Println("Time Running: " + Minutes);
      Println("Exp Per Hour: " + EXPHour);
      Println("Flax Picked: " + FlaxCount);
    }
  }

  public void start(String command, String[] parameter) {
    DisplayMessage("@gre@Jake: @whi@Flax Crafter", 3);
    while (Running()) {

      while (InvCount() < 30 && Running()) {
        AtObject2(693, 524);
        Wait(750);
      }
      while (!UpX && Running()) {
        Wait(100);
        AtObject(692, 525);
        Wait(1000);
      }
      UpX = false;
      while (InvCount(675) > 0 && Running()) {
        if (Fatigue() > 99 && Running()) {
          while (!Sleeping()) {
            Use(FindInv(1263));
            Wait(3000);
          }
          while (Sleeping()) {
            Wait(200);
          }
          SleepCount++;
        }
        UseOnObject(694, 1469, FindInv(675));
        Wait(750);
      }
      while (InvCount(676) > 0 && Running()) {
        Drop(FindInv(676));
        Wait(750);
      }
      while (!DownX && Running()) {
        DisplayMessage("going down", 3);
        Wait(100);
        AtObject(692, 1469);
        Wait(1000);
      }
      DownX = false;
    }
    float Minutes = (((float) System.currentTimeMillis() / 1000) - (float) StartS) / 60;
    float EXPHour = (float) EXP / (Minutes / 60);
    for (int i = 0; i < 25; i++) System.out.println();
    Println("------------------------");
    Println("Status Report: Flax Crafter");
    Println("------------------------");
    Println("Levels Acheived: " + LVL);
    Println("Crafting XP Gained: " + EXP);
    Println("Time Running: " + Minutes);
    Println("Exp Per Hour: " + EXPHour);
    Println("Flax Picked: " + FlaxCount);
    DisplayMessage("@gre@Jake: @red@Script Stopped", 3);
  }
}
