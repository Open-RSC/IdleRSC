package scripting.sbot;

import compatibility.sbot.Script;

public class YanilleIron extends Script {
  public final long StartTime = System.currentTimeMillis();
  public int Trips = 0;
  public int RockCount = 0;

  public String[] getCommands() {
    return new String[] {"yaniron"};
  }

  public void start(String command, String[] parameter) {
    DisplayMessage(
        "@ran@-@ran@=@ran@=@ran@:@ora@Yanille Iron Miner By Unknown Idiot started@ran@:@ran@=@ran@=@ran@-",
        3);
    while (Running()) {
      DoMining();
      Wait(500);
      GoToBank();
      Wait(500);
      DoBanking();
      Wait(500);
      GoToRocks();
      Wait(500);
      Trips++;
      if (Trips == 2) {
        ProgressRepert();
        Trips = 0;
      }
    }
    DisplayMessage(
        "@ran@-@ran@=@ran@=@ran@:@ora@Yanille Iron Miner By Unknown Idiot stopped@ran@:@ran@=@ran@=@ran@-",
        3);
  }

  // MINING PROCEDURE
  public void DoMining() {
    DisplayMessage("@ran@-@ran@=@ran@=@ran@:@ora@UI Yanille Miner: @gre@Mining iron...", 3);
    while (InvCount() < 30) {
      if (Fatigue() >= 95 && Running()) {
        while (!Sleeping()) {
          Use(FindInv(1263));
          Wait(2500);
        }
        while (Sleeping()) {
          Wait(500);
          Beep();
        }
      }
      if (ObjectAt(567, 716) == 102 && Running()) AtObject(567, 716);
      if (ObjectAt(569, 715) == 102 && ObjectAt(567, 716) == 98 && Running()) AtObject(569, 715);
      Wait(2000);
    }
  }
  // WALK TO BANK
  public void GoToBank() {
    DisplayMessage("@ran@-@ran@=@ran@=@ran@:@ora@UI Yanille Miner: @gre@Walking to Bank...", 3);
    while (GetX() != 573 && GetY() != 723) Walk(573, 723);
    Wait(100);
    while (GetX() != 577 && GetY() != 731) Walk(577, 731);
    Wait(100);
    while (GetX() != 579 && GetY() != 738) Walk(579, 738);
    Wait(100);
    while (GetX() != 581 && GetY() != 745) Walk(581, 745);
    Wait(100);
    while (GetX() != 584 && GetY() != 752) Walk(584, 752);
    Wait(100);
    while (GetX() != 586 && GetY() != 752) Walk(586, 752);
    Wait(1000);
  }
  // BANKING PROCEDURE
  public void DoBanking() {
    DisplayMessage("@ran@-@ran@=@ran@=@ran@:@ora@UI Yanille Miner: @gre@Banking...", 3);
    while (!QuestMenu()) {
      int BankerID = GetNearestNPC(95);
      TalkToNPC(BankerID);
      Wait(2000);
    }
    Answer(0);
    while (!Bank()) Wait(10);
    while (InvCount(151) > 0) {
      Deposit(151, 1);
      Wait(100);
    }
    while (InvCount(157) > 0) {
      Deposit(157, 1);
      Wait(100);
    }
    while (InvCount(158) > 0) {
      Deposit(158, 1);
      Wait(100);
    }
    while (InvCount(159) > 0) {
      Deposit(159, 1);
      Wait(100);
    }
    while (InvCount(160) > 0) {
      Deposit(160, 1);
      Wait(100);
    }
  }
  // WALK TO IRON ROCKS
  public void GoToRocks() {
    DisplayMessage("@ran@-@ran@=@ran@=@ran@:@ora@UI Yanille Miner: @gre@Walking to rocks...", 3);
    while (GetX() != 586 && GetY() != 752) Walk(586, 752);
    Wait(100);
    while (GetX() != 584 && GetY() != 752) Walk(584, 752);
    Wait(100);
    while (GetX() != 581 && GetY() != 744) Walk(581, 744);
    Wait(100);
    while (GetX() != 577 && GetY() != 738) Walk(577, 738);
    Wait(100);
    while (GetX() != 575 && GetY() != 731) Walk(575, 731);
    Wait(100);
    while (GetX() != 574 && GetY() != 725) Walk(574, 725);
    Wait(100);
    while (GetX() != 568 && GetY() != 720) Walk(568, 720);
    Wait(100);
    while (GetX() != 568 && GetY() != 716) Walk(568, 716);
    Wait(1000);
  }

  public void ServerMessage(String message) {
    if (message.equals("@gam@You manage to obtain some iron ore")) RockCount++;
  }

  public void ProgressRepert() {
    long TotalTime = System.currentTimeMillis() - StartTime;
    long MinsWorked = TotalTime / 60000L;
    int XPGain = RockCount * 35;
    Println("PROGRESS REPORT:");
    Println("Working for " + MinsWorked + " minutes");
    Println("Mined a total of " + RockCount + " iron ores");
    Println("Gained " + XPGain + " mining experience");
  }
}
