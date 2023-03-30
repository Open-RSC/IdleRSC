package scripting.sbot;

import compatibility.sbot.Script;

public class CoalUnCerter extends Script {
  public String[] getCommands() {
    return new String[] {"uncertcoal"};
  }

  public void start(String command, String[] parameter) {

    DisplayMessage("@blu@SBoT: @red@Uncerting Coal - Vegunks", 3);
    while (Running()) {
      while (!QuestMenu()) {
        int CerterID = GetNearestNPC(225);
        TalkToNPC(CerterID);
        Wait(3000);
      }
      for (; !QuestMenu(); Wait(3000)) ;
      Answer(0);
      Wait(1000);
      for (; !QuestMenu(); Wait(3000)) ;
      Answer(1);
      Wait(1000);
      for (; !QuestMenu(); Wait(3000)) ;
      Answer(4);
      Wait(1000);
      Wait(3000);

      Walk(224, 632);

      while (!QuestMenu()) {
        int BankerID = GetNearestNPC(95);
        TalkToNPC(BankerID);
        while (QuestMenu()) ;
        Wait(1000);
      }
      Answer(0);
      while (!Bank()) Wait(1000);
      while (InvCount(155) > 0) {
        Deposit(155, 25);
        Wait(100);
      }
      CloseBank();

      Walk(226, 631);
    }

    DisplayMessage("@red@STOPPED", 3);
  }
}
