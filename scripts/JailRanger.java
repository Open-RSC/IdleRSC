import compatibility.sbot.Script;

public class JailRanger extends Script
{

    public String[] getCommands()
    {
        return new String[]{ "jailranger" };
    }
   public void ServerMessage(String message)
   {

   }
    public void start(String command, String parameter[])
    {
while(Running())
{
   int ID[] = new int[] { 21, 137, 192 } ;
   int Mob = GetNearestNPC(ID);
   while(Mob != -1)
   {
      AttackNPC(Mob);
      Wait(100);
      Mob = GetNearestNPC(ID);
   }
   if (GetX() != 285 && GetY() != 659)
   {
      Walk(285, 659);
   }
   if (Fatigue() >= 95 && Running() == true)
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
    }
} 