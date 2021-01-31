package scripting.sbot;

import compatibility.sbot.Script;

public class AirPicker extends Script 
{ 
    public String[] getCommands() 
    { 
        return new String[]{"getairs"}; 
     } 
      
       public void start(String command, String parameter[]) 
    { 
       DisplayMessage("@ran@=@ran@=@ran@= @lre@Air Picker Started - By Hiyadude =) @ran@=@ran@=@ran@=", 3); 

      while (Running()) 
   { 
    
         int i[] = GetNearestItem(33); 
   TakeItem(i[0], i[1], 33); 
   Wait(100); 

} 
DisplayMessage("@ran@=@ran@=@ran@= @dre@Air Picker STOPPED @ran@=@ran@=@ran@=", 3); 
    } 
}