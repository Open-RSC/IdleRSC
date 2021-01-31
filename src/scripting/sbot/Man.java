package scripting.sbot;

import compatibility.sbot.Script;

public class Man extends Script
{

   public String[] getCommands()
   {
      return new String[]{"man"};
   }
   int ID;
   public void thieve()
   {
   ID = GetNearestNPC(11);
   ThieveNPC(ID);
   Wait(500);
   }
   public void run()
   {
   while (InCombat())
   {
      ForceWalk(GetX(), GetY());
      Wait(500);
   }
   }
   public void Checksleep()
   {
   if (Fatigue() >= 90 && Running() == true)
        {
                  while (Sleeping() == false && Running() == true)
                  {
                     Use(FindInv(1263));
                     Wait(2500);
                  }
                  while (Sleeping() == true && Running() == true)
                  {
                      Wait(100);
                  }
   }
   }   
   public void start(String command, String parameter[])
   {
      DisplayMessage("@red@G-unit's Man Thiever plesh", 3);
      while (Running())
      {
      thieve();
      run();
      Checksleep();            
      }
   }
}