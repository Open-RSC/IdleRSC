package scripting.sbot;

import compatibility.sbot.Script;

public class CoalCerter extends Script {
  public String[] getCommands() {
    return new String[] {"certcoal"};
  }

  public void start(String command, String[] parameter) {

    DisplayMessage("@blu@SBoT: @red@Certing Coal - Vegunks", 3);
    while (Running()) {
      while (!QuestMenu()) {
        int CerterID = GetNearestNPC(225);
        TalkToNPC(CerterID);
        Wait(2000);
      }
      for (; !QuestMenu(); Wait(25)) ;
      Answer(1);
      for (; !QuestMenu(); Wait(25)) ;
      Answer(1);
      for (; !QuestMenu(); Wait(25)) ;
      Answer(4);
      Wait(1000);

      Walk(224, 632);

      while (!QuestMenu()) {
        int BankerID = GetNearestNPC(95);
        TalkToNPC(BankerID);
        while (QuestMenu()) ;
        Wait(1500);
      }
      Answer(0);
      while (!Bank()) Wait(1500);
      while (InvCount(155) < 25) {
        Withdraw(155, 1);
        Wait(200);
      }
      CloseBank();

      Walk(226, 631);
    }

    DisplayMessage("@red@STOPPED", 3);
  }
}
