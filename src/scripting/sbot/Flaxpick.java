package scripting.sbot;

import compatibility.sbot.Script;

public class Flaxpick extends Script 
{ 

    public String[] getCommands() 
    { 
        return new String[]{"flax"}; 
    } 
    public void start(String command, String parameter[]) 
    { 
      DisplayMessage("@ran@Flax Picker!",3); 
      DisplayMessage("@ran@by xex",3); 
      SetFightMode(2); 
      while (Running()) 
      { 
         Walk(500,457); 
         Walk(501,464); 
         Walk(501,472); 
         Walk(499,474); 
         Walk(493,481); 
         Walk(491,486);  
         while (InvCount() < 30 && Running() == true) 
         { 
         AtObject2(489,486); 
         Wait(100); 
         } 
         Walk(495,481); 
         Walk(498,477); 
         Walk(503,467); 
         Walk(502,458); 
         Walk(500,451);  
         int BankerID = GetNearestNPC(95); 
         while (QuestMenu() == false) 
         { 
            TalkToNPC(BankerID); 
            Wait(1000); 
         } 
         Answer(0); 
         while (Bank() == false) 
            Wait(1); 
         while (InvCount(675) > 0 && Running() == true) 
         { 
            Deposit(675,1); 
            Wait(100); 
         } 
         CloseBank(); 
      } 
      DisplayMessage("@red@STOPPED", 3); 
    } 
}