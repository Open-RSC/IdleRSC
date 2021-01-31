package scripting.sbot;

import compatibility.sbot.Script;

public class MindPicker extends Script 
{ 
 
    public String[] getCommands() 
    { 
        return new String[]{"getminds"}; 
    } 
      
     public void start(String command, String parameter[]) 
     { 
      DisplayMessage("@ran@=@ran@=@ran@= @lre@Mind Picker Started - By Fert @ran@=@ran@=@ran@=", 3); 
      while (Running()) 
      { 
    
         int i[] = GetNearestItem(35); 
         TakeItem(i[0], i[1], 35); 
         Wait(100); 

   	  } 
      DisplayMessage("@ran@=@ran@=@ran@= @dre@Mind Picker STOPPED @ran@=@ran@=@ran@=", 3); 
    } 
}