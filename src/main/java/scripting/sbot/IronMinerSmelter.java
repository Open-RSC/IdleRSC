package scripting.sbot;

import compatibility.sbot.Script;

public class IronMinerSmelter extends Script {
  public String[] getCommands() {
    return new String[] {"ironminesmelt"};
  }

  public void SmeltIron() {
    while (InvCount(151) > 0) {
      UseOnObject(306, 546, FindInv(151));
      Wait(2500);
    }
  }

  public void BankIt(int FatLVL) {
    while (!Bank() && Running()) {
      while (!QuestMenu() && Running()) {
        int BankerID = GetNearestNPC(95);
        TalkToNPC(BankerID);
        long Time = System.currentTimeMillis();
        while (System.currentTimeMillis() - Time <= 2000 && !QuestMenu() && Running()) Wait(1);
      }
      Answer(0);
      long Time = System.currentTimeMillis();
      while (System.currentTimeMillis() - Time <= 5000 && !Bank() && Running()) Wait(1);
    }
    while (InvCount(170) > 0) {
      Deposit(170, 1);
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
    if (Fatigue() > FatLVL) {
      Withdraw(1263, 1);
      Wait(100);
    }
    CloseBank();
  }

  public void BankBag() {
    if (InvCount(1263) > 0) {
      while (!Bank() && Running()) {
        while (!QuestMenu() && Running()) {
          int BankerID = GetNearestNPC(95);
          TalkToNPC(BankerID);
          long Time = System.currentTimeMillis();
          while (System.currentTimeMillis() - Time <= 2000 && !QuestMenu() && Running()) Wait(1);
        }
        Answer(0);
        long Time = System.currentTimeMillis();
        while (System.currentTimeMillis() - Time <= 5000 && !Bank() && Running()) Wait(1);
      }
      while (InvCount(1263) > 0) {
        Deposit(1263, 1);
        Wait(100);
      }
      CloseBank();
    }
  }

  public void RimmingtonToFurnace() {
    Walk(312, 628);
    Walk(311, 619);
    Walk(309, 613);
    Walk(307, 605);
    Walk(303, 598);
    Walk(298, 589);
    Walk(292, 582);
    Walk(290, 573);
    Walk(291, 564);
    Walk(293, 556);
    Walk(299, 551);
    Walk(303, 544);
    Walk(309, 542);
    while (ObjectAt(309, 543) == 2) {
      OpenDoor(309, 543, 0);
      Wait(200);
      WalkNoWait(307, 545);
    }
    Walk(307, 545);
  }

  public void FunaceToBank() {
    while (ObjectAt(309, 543) == 2) {
      OpenDoor(309, 543, 0);
      Wait(200);
    }
    Walk(307, 545);
    Walk(299, 547);
    Walk(293, 553);
    Walk(290, 561);
    Walk(287, 572);
    while (ObjectAt(287, 571) == 64) {
      AtObject(287, 571);
      Wait(300);
    }
    Walk(286, 571);
  }

  public void BankToRimmington() {
    Walk(286, 571);
    while (ObjectAt(287, 571) == 64) {
      AtObject(287, 571);
      Wait(300);
    }
    Walk(287, 572);
    Walk(291, 581);
    Walk(297, 588);
    Walk(304, 591);
    Walk(305, 599);
    Walk(307, 606);
    Walk(303, 614);
    Walk(301, 622);
    Walk(307, 628);
    Walk(309, 636);
    Walk(317, 641);
  }

  public void MineRimmingtonIron() {
    int IronLoc[] = GetNearestObject(102);
    if (IronLoc[1] < 643 && IronLoc[1] != -1 && IronLoc[0] != -1) {
      AtObject(IronLoc[0], IronLoc[1]);
      Wait(1000);
    }
  }

  public void SleepIfOver(int FatLVL) {
    if (Fatigue() > FatLVL && Running()) {
      while (!Sleeping() && Running()) {
        Use(FindInv(1263));
        Wait(2000);
      }
      while (Sleeping() && Running()) {
        System.out.print("\007");
        System.out.flush();
        Wait(700);
      }
    }
  }

  public void start(String command, String parameter[]) {
    int OreCount = 0;
    int BarCount = 0;
    int DiamondCount = 0;
    int RubyCount = 0;
    int EmeraldCount = 0;
    int SapphireCount = 0;
    int TripCount = 0;
    int SleepCount = 0;
    int FatigueSum = 0;
    int FatigueStart = 0;
    int FatigueDifference = 0;
    int FatLVL = 0;
    double MiningEXP = 0;
    int MiningDifference = 0;
    int MiningStart = 0;
    int SmithingStart = 0;
    int SmithingDifference = 0;
    double SmithingEXP = 0;
    DisplayMessage("@bla@Falador Iron Miner And Smelter", 3);
    DisplayMessage("@gre@By: Davis Zanot", 3);
    while (Running()) {
      FatigueStart = Fatigue();
      while (InvCount() < 30 && Running()) {
        MineRimmingtonIron();
      }
      OreCount = InvCount(151) + OreCount;
      MiningEXP = OreCount * 35;
      if (Running()) RimmingtonToFurnace();
      if (Running()) SmeltIron();
      BarCount = InvCount(170) + BarCount;
      SmithingEXP = BarCount * 12.5;
      if (Running()) FunaceToBank();
      FatigueDifference = Fatigue() - FatigueStart;
      FatigueSum = FatigueSum + FatigueDifference;
      TripCount = TripCount + 1;
      DiamondCount = InvCount(157) + DiamondCount;
      RubyCount = InvCount(158) + RubyCount;
      EmeraldCount = InvCount(159) + EmeraldCount;
      SapphireCount = InvCount(160) + SapphireCount;
      FatLVL = 93 - (FatigueSum / TripCount);
      BankIt(FatLVL);
      SleepIfOver(FatLVL);
      SleepCount = InvCount(1263) + SleepCount;
      BankBag();
      System.out.println("===========================================================");
      System.out.println("                    Trip #" + TripCount);
      System.out.println("===========================================================");
      System.out.println("Ores Mined: " + OreCount);
      System.out.println("Bars Smelted: " + BarCount);
      System.out.println("Diamonds Banked: " + DiamondCount);
      System.out.println("Rubys Banked: " + RubyCount);
      System.out.println("Emeralds Banked: " + EmeraldCount);
      System.out.println("Saphires Banked: " + SapphireCount);
      System.out.println("Times Slept: " + SleepCount);
      System.out.println("Mining Experience Gained " + MiningEXP);
      System.out.println("Smithing Experience Gained " + SmithingEXP);
      System.out.println("Success Rate: " + 100 * BarCount / OreCount + "%");
      System.out.println("Average Fatigue Per Trip: " + FatigueSum / TripCount);
      BankToRimmington();
    }
    DisplayMessage("@red@STOPPED", 3);
  }
}
