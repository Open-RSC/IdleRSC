package scripting.sbot;

import compatibility.sbot.Script;

public class drayfish extends Script {
  public String[] getCommands() {
    return new String[] {"drayfish"};
  }

  public void start(String command, String parameter[]) {
    while (Running() == true) {

      while (Running() == true && InvCount() < 30) {
        if (Fatigue() >= 95 && Running() == true) {
          while (Sleeping() == false) {
            Use(FindInv(1263));
            Wait(2500);
          }
          while (Sleeping() == true) {
            Wait(100);
          }
        }

        AtObject(224, 661);
        Wait(1000);
      }
      Walk(221, 661);
      Wait(100);
      Walk(220, 654);
      Wait(100);
      Walk(220, 644);
      Wait(100);
      Walk(220, 633);
      Wait(100);
      Walk(215, 639);
      Wait(100);
      Walk(220, 633);

      while (QuestMenu() == false) {
        int BankerID = GetNearestNPC(95);
        TalkToNPC(BankerID);
        Wait(1300);
      }
      Answer(0);
      while (Bank() == false) Wait(2000);
      while (InvCount(351) > 1) {
        Deposit(351, 1);
        Wait(100);
      }
      while (InvCount(349) > 1) {
        Deposit(349, 1);
        Wait(100);
      }
      CloseBank();
      Wait(500);
      Walk(220, 633);
      Wait(100);
      Walk(215, 639);
      Wait(100);
      Walk(220, 633);
      Wait(100);
      Walk(220, 644);
      Wait(100);
      Walk(220, 654);
      Wait(100);
      Walk(221, 661);
    }
    DisplayMessage("@gre@Liima: @red@Terminated.", 3);
  }
}
