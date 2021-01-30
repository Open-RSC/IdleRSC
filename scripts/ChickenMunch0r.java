import compatibility.sbot.Script;

public class ChickenMunch0r extends Script
{
  public String[] getCommands()
  {
      return new String[]{"cho0k"};
  }
  public void start(String command, String parameter[])
  {  
    DisplayMessage("ChookMunch0r - pun@ran@K@whi@rocke@ran@R", 3);
    int Meat[] = GetNearestItem(133);
     
     while(Running()) {                
       
         while(Sleeping())
         Wait(1);      
   
        if(InvCount() < 30) {
         while(ObjectAt(274, 603) == 60 && GetX() > 274 && Running()) {
         AtObject(274, 603);
         Wait(1000);
         }
         while(Meat != null && Running() && !Sleeping() && InvCount() < 30) {
          TakeItem(Meat[0], Meat[1], 133);
          Wait(Rand(750,1000));
          Meat = GetNearestItem(133);    
         }
         }
         
         if(InvCount() == 30) {
         
         if(ObjectAt(274, 603) == 60) {
         AtObject(274, 603);
         Wait(1000);
         } else
         
         Walk(275, 614);
         Walk(276, 636);
         
         if(DoorAt(276, 637, 0) == 2) {
         OpenDoor(276, 637, 0);
         Wait(1000);
         }
         
         }
         
         while(InvCount() != 1) {
         
         while(Fatigue() >= 97 && Running() && !Sleeping()) {
         System.out.println("Sleeping.");
         Use(FindInv(1263));
         Wait(1500);
         }          
         
         while(Running() && FindInv(134) > 0) {
         Drop(FindInv(134));
         Wait(1000);
         }
           
         while(Running() && FindInv(132) > 0) {
         Use(FindInv(132));
         Wait(500);
         }            
         
         while(FindInv(133) > 0 && Running()) {
         UseOnObject(275, 638, FindInv(133));
         Wait(1000);                                        
         }                              
                   
         }
         if(InvCount() == 1) {          
         
         if(DoorAt(276, 637, 0) == 2) {
         OpenDoor(276, 637, 0);
         Wait(1000);
         } else
         
         Walk(271, 625);
         Walk(274, 613);
         Walk(274, 605);
           
         if(ObjectAt(274, 603) == 60) {
         AtObject(274, 603);
         Wait(1000);
         } else
         
         Walk(271, 604);        
                   
         }
         
     }
   DisplayMessage("ChookMunch0r - @red@STOPPED", 3);
}
}
