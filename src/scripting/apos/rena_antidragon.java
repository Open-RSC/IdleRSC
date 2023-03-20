package scripting.apos;
import compatibility.apos.Script;
public class rena_antidragon extends Script
 
{
    // General walk
    int step = 0;
    int[] path = null;
    int loop = 0;

    // Booleans
    boolean draytoduke = true;
    boolean demandshields = false;
    boolean jugglingshields = false;
    boolean duketodray = false;
    boolean banking = false;
    
    boolean picked = false;
    
    // Areas
    int[] bedroomArea = new int[]{133, 1602};
    
    // Shield Count
    int shieldcount = 0;
    int shieldobtained = 0;
    
    
    int[] intervals = new int[]
    {        
        5,11,17,23,29
    };
    
    public rena_antidragon (String e)
 
    {
//        super(e);
    }
 
     public void init(String params) 
    {
        System.out.println("[===================================================================]");
        System.out.println("[Rena] Started Antidragonbreath Shield Collector by Renafox! ");
        System.out.println("[======================= !~ INSCTRUCTIONS ~! =======================]");
        System.out.println("[======== !~ rena_aiosmelter <starting area>              ~! =======]");
        System.out.println("[======== !~ Starting area available : bank   (draynor)   ~! =======]");
        System.out.println("[======== !~                           castle (2nd floor) ~! =======]");
        System.out.println("[===================================================================]");
        if(params.equalsIgnoreCase("bank")) 
        {
              System.out.println("[Rena] We are starting at Draynor Bank!");
                    draytoduke = true;
                    demandshields = false;
                    jugglingshields = false;
                    duketodray = false;
                    banking = false;
        } 
        else if(params.equalsIgnoreCase("castle"))
        {
              System.out.println("[Rena] We are starting at Duke's room (Lumbridge 2nd Floor)!");
                    draytoduke = false;
                    demandshields = true;
                    jugglingshields = false;
                    duketodray = false;
                    banking = false;
        }
        else
        {
              System.out.println("[Rena] Invalid Param ... Stopping script");
              stopScript();
        }
    }    

    public int main()
 
    {
        //Draynor to Duke
        if(draytoduke && !demandshields && !jugglingshields && !duketodray && !banking)
        {
            if(isAtApproxCoords(138,1610, 1))
            {
                draytoduke = false;
                demandshields = true;
                jugglingshields = false;
                duketodray = false;
                banking = false;
                step = 0;
                loop = 0;
                path = null;
                System.out.println("[Rena] Reached duke's room safely !");
                System.out.println("[Rena] Switching to demanding shields from old man !");
                return random(1000, 1500);
            }
            if(isAtApproxCoords(138,666, 1))
            {
                int[] ladderup = getObjectById(5);
                if(ladderup[0] != -1) 
                {
                    atObject(ladderup[1], ladderup[2]);
                    return random(400, 500);
                }
                System.out.println("[Rena] Climbing up ladder! ");
                return random(1000, 1500);
            }
            if(isAtApproxCoords(219,638,3))
            {
                step = 0;
                path = new int[] {219,633,214,633,206,644,195,643,184,643,174,643,160,643,144,643,128,645,116,657,129,659,138,666};
            }
            /*
            else
            {
                System.out.println("[Rena] Error! You are not in draynor bank !");
                System.out.println("[Rena] Stopping Script ... !");
                stopScript();
            }
            */
        }
        
        // Demanding shields from old man
        if(!draytoduke && demandshields && !jugglingshields && !duketodray && !banking)
        {
            if(getInventoryCount(420) == 30) // Got all the shields we need
            {   
                draytoduke = false;
                demandshields = false;
                jugglingshields = false;
                duketodray = true;
                banking = false;
                System.out.println("[Rena] Successfully conned 30 shields from old bearded man!");
                System.out.println("[Rena] Time to bank ( at draynor ) !");
            }
            if(shieldcount == 35) // 30 Shields on the floor, lets pick them
            {
                int[] demshields = getItemById(420);
                if(demshields[0]!=-1)
                {
                    pickupItem(420, demshields[1], demshields[2]);
                    System.out.println("[Rena] Picking up dem shields at "+demshields[1]+","+demshields[2]+" ..."); 
                    System.out.println(" "); 
                    return random(200,300);
                }
            }
            if(shieldcount < 35) // Gather shields
            {
                if((shieldcount == 5) || (shieldcount == 11) || (shieldcount == 17) || (shieldcount == 23) || (shieldcount == 29)) // Juggling time
                {
                    draytoduke = false;
                    demandshields = false;
                    jugglingshields = true;
                    duketodray = false;
                    banking = false;
                    picked = false;
                    System.out.println("[Rena] Time to Juggle the shields so they dont disappear !");
                }
                if(getInventoryCount(420) > 0) // Got a shield, dropping em
                {              
                    dropItem(getInventoryIndex(420));  
                    shieldcount++;
                    shieldobtained++;
                    System.out.println("[Rena] Demanded "+shieldobtained+" shields so far !");
                    return 2000;
                }
                if(getInventoryCount(420) == 0) // No shields
                {  
                    if(isQuestMenu())
                    {
                        answer(0);
                        return random(1000, 1500);
                    }
                    if(distanceTo(bedroomArea[0], bedroomArea[1]) < 6) 
                    {
                        int oldman[] = getNpcByIdNotTalk(198);
                        if(oldman[0] != -1)
                            talkToNpc(oldman[0]);
                        return 2000;
         
                    } 
                    else
                    {
                        walkTo(bedroomArea[0], bedroomArea[1]);
                        return 200;
                    }
                }
            }
        }
        
        // Juggling shields!
        if(!draytoduke && !demandshields && jugglingshields && !duketodray && !banking)
        {
            if(!picked)
            {
                int[] demshields = getItemById(420);
                if(demshields[0]!=-1)
                {
                    pickupItem(420, demshields[1], demshields[2]);
                    System.out.println("[Rena] Picking up dem shields at "+demshields[1]+","+demshields[2]+" ..."); 
                    System.out.println(" "); 
                    return random(200,300);
                }
                else
                {
                    System.out.println("[Rena] Finished picking ...");
                    System.out.println("[Rena] Now dropping !");
                    System.out.println(" "); 
                    picked = true;
                }
            }
            if(picked)
            { 
                if(getInventoryCount(420) > 0) // Dropping em
                {              
                    dropItem(getInventoryIndex(420));  
                    return 1000;
                }
                else // Finished Juggling Process
                {              
                    shieldcount++; // To stop the loop
                    draytoduke = false;
                    demandshields = true;
                    jugglingshields = false;
                    duketodray = false;
                    banking = false;
                    System.out.println("[Rena] Finished Juggling Process!");
                    System.out.println("[Rena] Time to demand more shields from old man !");
                }
            }
        }    
        //Duke to Draynor
        if(!draytoduke && !demandshields && !jugglingshields && duketodray && !banking)
        {
            if(!isAtApproxCoords(138,1610, 1) && isAtApproxCoords(138,1610, 10))
            {
                walkTo(138,1610);
                System.out.println("[Rena] Waltzing to the ladder! ");
                return random(1000, 1500);
            }
            if(isAtApproxCoords(138,666, 1))
            {
                int[] ladderdown = getObjectById(6);
                if(ladderdown[0] != -1) 
                {
                    atObject(ladderdown[1], ladderdown[2]);
                    return random(400, 500);
                }
                System.out.println("[Rena] Climbing down ladder! ");
                return random(1000, 1500);
            }
            if(isAtApproxCoords(138,666,1))
            {
                step = 0;
                path = new int[] {129,659,116,657,128,645,144,643,160,643,174,643,184,643,195,643,206,644,214,633,219,633,219,636};
            }
            if(isAtApproxCoords(219, 638,3))
            {
                draytoduke = false;
                demandshields = false;
                jugglingshields = false;
                duketodray = false;
                banking = true;
                step = 0;
                loop = 0;
                path = null;
                System.out.println("[Rena] Reached draynor bank safely !");
                System.out.println("[Rena] Switching to banking !");
                return random(1000, 1500);
            }
        }
        
        // Banking
        if(!draytoduke && !demandshields && !jugglingshields && !duketodray && banking)
        {

            if(isBanking())
              {
                // Deposit loot
                if(getInventoryCount(420) > 0)
                {            
                    deposit(420, getInventoryCount(420));
                    return random(900,1000);
                }
                else
                {
                    System.out.println("[Rena] Done Banking Shields!");
                    System.out.println("[Rena] Mission Accomplished ... Stopping Script");
                    closeBank();
                    stopScript();
                }
                return random(1000,1500);
 
            }
            // menu open
            if(isQuestMenu())
            {
                answer(0);
                return random(5000, 5200);
            }
            if(distanceTo(217,453) < 6) {
 
                int banker[] = getNpcByIdNotTalk(95);
 
                if(banker[0] != -1)
 
                    talkToNpc(banker[0]);
 
                return 4500;
 
            }
            walkTo(216,450);
 
            return 1000;
        }
    
        // general walk
        if((step + 1) < path.length) {
          int[] bankdoor = getObjectById(64);
          if(bankdoor[0] != -1 && bankdoor[2] < 451) {
             atObject(bankdoor[1], bankdoor[2]);
             return random(1000, 1500);
           }
          if(isAtApproxCoords(path[step], path[step + 1], 0))
             step = step + 2;
          walkTo(path[step] , path[step + 1] );
          System.out.println("[Rena] Running . . . ! ");
          return random(350, 550);
       }
       loop++;
       if(loop > 10) 
       {
          step = 0;
          loop = 0;
       }
       return random(500, 600);
    }
    
} 