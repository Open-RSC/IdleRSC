package scripting.sbot;

import compatibility.sbot.Script;
//Have only sleeping bag, harpoon inventory.
//Start near the fishing spots, drops fish
public class PowerTuna extends Script

{

    public String[] getCommands()
    {
        return new String[]{"powertuna"};
       
    }
    public void start(String command, String parameter[])

    {
    	DisplayMessage("@red@IdleRSC NOTE: This script is made to be used in Karamja.", 3);
         DisplayMessage("@org2@SBoT: @ran@Power@bla@T@gre@u@blu@n@red@a - @gre@By Th0ng @red@Girls", 3);
   for(; Running();
   )
{

{
   Walk(369, 685);
   AtObject(368, 684);
   Wait(50);
if (Fatigue() > 85)
{
while (Sleeping() == false)
{
Use(FindInv(1263));
Wait(2000);
}
while (Sleeping() == true && Running() == true)
{
Wait(100);
}
}

}

}

   }
  }

