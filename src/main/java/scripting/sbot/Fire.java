package scripting.sbot;

import compatibility.sbot.Script;

public class Fire extends Script {
  public String[] getCommands() {
    return new String[] {"fire"};
  }

  int TotalStartXP = 0;
  int StartLevel = 0;
  int BagCount = 0;
  long StartTime = 0;

  public void start(String command, String[] parameter) {
    StartSleeper();
    TotalStartXP = GetExperience(11);
    StartLevel = GetStat(11);
    StartTime = (int) (TickCount() / 1000);
    Println("##### Start Firemaking Experience: " + TotalStartXP + " (" + StartLevel + ")");
    while (Running()) {
      if (!Sleeping() && Fatigue() < 80) {
        Println("#### Fire Making - RichyT");
        Println(
            "### " + ((long) ((int) (TickCount() / 1000)) - StartTime) + " seconds have passed");
        Println("### Experience Gained So Far: " + (GetExperience(11) - TotalStartXP));
        Println("### Levels Gained So Far: " + (GetStat(11) - StartLevel));
        Println("### Number of times used the sleeping bag so far: " + BagCount);

        Println("## Cutting Trees...");
        Tree();

        long T = TickCount();
        while (TickCount() - T < 500 && !ItemAt(GetX(), GetY(), 14)) Wait(1);

        Println("## Lighting Fire...");
        firemake();
      }

      Wait(1);
    }
    Println("#### Script Ended ####");
  }

  public void firemake() {
    int[] TreeTypes = new int[] {0, 1};
    int[] TreeCoords = GetNearestObject(TreeTypes);
    if (TreeCoords[0] != -1) {
      while (ObjectAt(GetX(), GetY()) == -1
          && Running()
          && !Sleeping()
          && ItemAt(GetX(), GetY(), 14)
          && Fatigue() < 80) {
        Println("# Lighting Fire");
        UseOnItem(GetX(), GetY(), 14, FindInv(166));
        long T = TickCount();
        while (ObjectAt(GetX(), GetY()) == -1
            && Running()
            && TickCount() - T < Rand(2500, 3000)
            && !Sleeping()
            && ItemAt(GetX(), GetY(), 14)
            && Fatigue() < 80) Wait(10);
      }
    }
  }

  public void Tree() {
    int[] TreeTypes = new int[] {0, 1};
    int[] TreeCoords = GetNearestObject(TreeTypes);
    if (TreeCoords[0] != -1) {
      while (ObjectAt(TreeCoords[0], TreeCoords[1]) < 2
          && Running()
          && !Sleeping()
          && Fatigue() < 80) {
        Println("# Cutting Tree");
        AtObject(TreeCoords[0], TreeCoords[1]);
        long T = TickCount();
        while (ObjectAt(TreeCoords[0], TreeCoords[1]) < 2
            && Running()
            && TickCount() - T < Rand(2500, 3000)
            && !Sleeping()
            && Fatigue() < 80) Wait(10);
      }
    }
  }

  public void StartSleeper() {
    new Thread(
            () -> {
              while (Running()) {
                if (Fatigue() >= 80 && !Sleeping()) {
                  Println("Fatigue is " + Fatigue() + ", using sleeping bag");
                  Use(FindInv(1263));
                  BagCount++;
                  Wait(5000);
                }
                Wait(250);
              }
            })
        .start();
  }
}
