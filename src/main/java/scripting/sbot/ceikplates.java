package scripting.sbot;

import compatibility.sbot.Script;

public class ceikplates extends Script {

  public String[] getCommands() {
    return new String[] {"ceikplate"};
  }

  public void start(String command, String parameter[]) {
    while (Running()) {

      DisplayMessage("Steel Platebody Smither by Ceikry", 3);

      // Walk to starting point
      if (!Bank() && !QuestMenu() && Running()) {
        Walk(150, 503);
        Wait(100);
      }

      // Talk to npc and wait for 450ms until the dialogue options appear
      TalkToNPC(GetNearestNPC(95));
      Wait(450);

      // Access bank dialogue option
      if (QuestMenu()) {
        Answer(0);
      } else {
        TalkToNPC(GetNearestNPC(95));
        Wait(450);
        Answer(0);
      }

      // Wait for bank to open
      while (!Bank() && Running()) {
        Wait(450);
      }

      // Deposit items
      while (Bank() && Running()) {

        if (InvCount(118) > 0) {
          Deposit(118, 5);
          Wait(1000);
        }

        Withdraw(171, 25);
        Wait(1200);

        CloseBank();
      }

      // Walk to anvil
      Walk(150, 503);
      Wait(100);
      Walk(150, 507);
      Wait(100);
      Walk(148, 512);
      Wait(100);

      // Use bar on anvil
      UseOnObject(148, 513, 5);
      Wait(1000);

      // Go through dialogue to smith plates
      Answer(1);
      Wait(25);
      Answer(2);
      Wait(25);
      Answer(2);
      Wait(25);
      Answer(3);

      // Wait until plates are done smithing (6000-7000ms)
      Wait(7000);
    }
    DisplayMessage("@red@SCRIPT STOPPED", 3);
  }
}
