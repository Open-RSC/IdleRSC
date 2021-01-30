import compatibility.sbot.Script;

public class dummy extends Script 
{ 

    public String[] getCommands() 
    { 
        return new String[]{"dummy"}; 
     } 
      
       public void start(String command, String parameter[]) 
    { 
       DisplayMessage("@dre@=@whi@=@dre@= @lre@Dummy Script Started - By Fert @dre@=@whi@=@dre@=", 3); 

      
      while (Running()) 
   {
   if (Fatigue() > 95 && Sleeping() == false)
					{
				DisplayMessage("@Dre@SBoT: @whi@Sleeping", 3);
						Use(FindInv(1263));
						Wait(5000);
					}
   if (GetCurrentStat(0) >= (8))
   {
   Use(FindInv(269));
   }
   
   int Dummy[] = GetNearestObject(49); 
            if (Dummy[0] > -1 & Running() == true) 
               AtObject2(Dummy[0],Dummy[1]); 
            Wait(100);
   
   
   }
   
  
   
   
   
  

  
DisplayMessage("@dre@=@whi@=@dre@= @dre@Dummy Script STOPPED @dre@=@whi@=@dre@=", 3); 
    } 
}

