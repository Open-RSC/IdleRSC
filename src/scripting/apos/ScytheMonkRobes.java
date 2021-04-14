package scripting.apos;
import compatibility.apos.Script;
//Put together and modified by Scytheman/Pandy/Yellowcard 2015
//Credit goes to Mofo, Blood, and Shauder
//Source of Material from: Mofo's getBerries, Blood's Barbarian Low Wall, Shauder's SAF_Moss Fatigue
//Start in West Falador Bank, roughly 40 Robe Tops per hour, 80 Robe Bottoms per hour

public final class ScytheMonkRobes extends Script{
   
   int step = 0;
   int[] path = null;
   int loop = 0;
   
   private long time;
   private int[] initial_xp = new int[SKILL.length];
   
   private final int
   MONK_TOP = 388,
   MONK_BOTTOM = 389;
   
   private final int[]
   DROP_IDS = {MONK_TOP, MONK_BOTTOM};
   
   private int //for paint count
   TOP = 0,
   BOTTOM = 0;
   
//   public ScytheMonkRobes(Extension e) {
//       super(e);
//   }
   
   public void init(String params) {
   System.out.println("Monk Robe Collector - Check code for sources - Start in West Falador Bank");
   }
   
   //Pathing below obtained from Mofo's getBerries
   //Banking below obtained from Shauder's SAF_Moss Fatigue
   
   @Override
   public int main() {
       if(initial_xp[0] == 0) //Statement obtained from Blood's Barbarian Low Wall
   {
           for(int i = 0; i < SKILL.length; i++) {
              initial_xp[i] = getXpForLevel(i); //replace 'xp' with skill name
           }
           time = System.currentTimeMillis();
     }
       
       if(isBanking()) {
           for(int drop : DROP_IDS) {
               int drop_count = getInventoryCount(drop);
               if (drop_count > 0) {
                   switch(drop) {
                       case MONK_TOP:
                       TOP = TOP + drop_count;
                       break;
                       case MONK_BOTTOM:
                       BOTTOM = BOTTOM + drop_count;
                       break;
                   }
                   deposit(drop, drop_count);
               }
           }
       
       if(getInventoryCount(388) > 0) {
          deposit(388, getInventoryCount(388));
          return random(1000, 1500);
       }
       if(getInventoryCount(389) > 0) {
          deposit(389, getInventoryCount(389));
          return random(1000, 1500);
       }

       closeBank();
       }
       
       if(isQuestMenu()) {
       answer(0);
       return random(6000, 6500);
       }
   
   //Walking from Falador bank to outside ladder
       
   if(isAtApproxCoords(330, 553, 6)) { //Falador inside of bank
       int[] bankdoor = getObjectById(64);
       if(bankdoor[0] != -1 && bankdoor[2] < 553) {
          atObject(bankdoor[1], bankdoor[2]);
          return random(1000, 1500);
       }
       step = 0;
       path = new int[] {325, 546, 316, 538, 314, 518, 298, 505, 286, 505, 274, 505, 264, 497,
       259, 488, 259, 478, 254, 469, 253, 464, 252, 464, 251, 467}; //From Falador bank outside to downstairs ladder
       
       int[] npc = getNpcById(95); //banker ID
       if(npc[0] != -1 && (getInventoryCount(388) > 0) || (getInventoryCount(389) > 0)) { //if banker exists, talk to banker
          talkToNpc(npc[0]);
          return random(2700, 3000);
       }
   }
   
   //Walking from upstairs ladder to spawn
   //If you want to go from outside ladder to upper floor, you need to climb up ladder
   if(isAtApproxCoords(251, 467, 6) && getInventoryCount() != 30) { //downstairs ladder coordinates
       path = new int[] {251, 1411, 264, 1403};  // upstairs ladder coordinate to spawn
       step = 0;
       int[] upladder = getObjectById(198);
       if(upladder[0] != -1)
          atObject(upladder[1], upladder[2]);
       return random(2500, 3000);
   }
   
   //Walking from robe spawn to ladder
   
   if(isAtApproxCoords(264, 1403, 6) && getInventoryCount() != 30) { // monk robe spawn
       step = 0;
       path = new int[] {264, 1403, 251, 1411}; // from spawn to upstairs ladder
       
       //pick up items
       int[] monktop = getItemById(388);
       if(monktop[0] != -1) {
          pickupItem(monktop[0], monktop[1], monktop[2]);
          return random(1000, 1500);
       }
       
       int[] monkbottom = getItemById(389);
       if(monkbottom[0] != -1) {
          pickupItem(monkbottom[0], monkbottom[1], monkbottom[2]);
          return random(1000, 1500);
       }
       return 1000;
   }
   
   //Walking from outside ladder to bank
   //If you want to get from top floor to bottom to bank, you need to climb down the ladder  
   if(isAtApproxCoords(251, 1411, 6) && getInventoryCount() == 30) {  //upstairs ladder
       step = 0;
       path = new int[] {251, 467, 252, 464, 254, 464, 254, 472, 257, 479, 263, 493, 269, 504, 283, 504, 292, 504,
       300, 507, 313, 513, 313, 519, 315, 534, 316, 539, 324, 541, 325, 547, 326, 552, 330, 553}; //from outside ladder to Falador inside bank
       int[] downladder = getObjectById(6); //Downstairs ladder
       if(downladder[0] != -1)
          atObject(downladder[1], downladder[2]);
       return random(2500, 3000);
   }
   
   //Statements below taken from Mofo's getBerries
   if((step + 1) < path.length) {
       int[] bankdoor = getObjectById(64); //open bank door if command is available
       if(bankdoor[0] != -1 && bankdoor[2] < 553) { //y coordinate of bank (inside bank)
          atObject(bankdoor[1], bankdoor[2]);
          return random(1000, 1500);
        }
       if(isAtApproxCoords(path[step], path[step + 1], 2))
          step = step + 2;
       walkTo(path[step] + random(-2, 2), path[step + 1] + random(-2, 2));
       return random(2000, 2500);
   }
   
   loop++;
   if(loop > 10) {
       step = 0;
       loop = 0;
   }
   return random(1000, 1200);
   }
   
   @Override //Paint obtained from Blood's Barbarian Low Wall and Shauder's SAF_MossFatigue
   public void paint() {
       int x = 8;
       int y = 33;
       drawString("@gr3@Monk Robe Collector", x, y, 4, 0xFFFFFF);
       y += 15;
       if (TOP < 1 || BOTTOM < 1)
       {
           drawString("No Robes Banked Yet", x + 5, y, 1, 0xFFFFFF);
           y += 15;
       }
       else
       {
           if (TOP > 0) {
               drawString("Monk Tops Banked: @gre@" + TOP, x + 5, y, 1, 0xFFFFFF);
               y += 15;
           }
           if (BOTTOM > 0) {
               drawString("Monk Bottoms Banked: @gre@" + BOTTOM, x + 5, y, 1, 0xFFFFFF);
               y += 15;
           }
       }
           
       drawString("Runtime: @gre@" + getRunTime(), x + 5, y, 1, 0xFFFFFF); //Runtime
       drawVLine(8,37,y+3-37,0xFFFFFF);
       drawHLine(8,y+3,183,0xFFFFFF);
   }
   
   private String getRunTime() { //RunTime obtained from Blood's Low Wall
       long ttime = ((System.currentTimeMillis() - time) / 1000);
       if (ttime >= 7200)
           return new String((ttime / 3600) + " hours, " + ((ttime % 3600) / 60) + " minutes, " + (ttime % 60) + " seconds.");
       if (ttime >= 3600 && ttime < 7200)
           return new String((ttime / 3600) + " hour, " + ((ttime % 3600) / 60) + " minutes, " + (ttime % 60) + " seconds.");
       if (ttime >= 60)
           return new String(ttime / 60 + " minutes, " + (ttime % 60) + " seconds.");
       return new String(ttime + " seconds.");
   }
}