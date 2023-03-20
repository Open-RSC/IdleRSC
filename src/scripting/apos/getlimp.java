package scripting.apos;
import compatibility.apos.Script;
public class getlimp extends Script {

   int step = 0;
   int[] path = null;
   int loop = 0;

   public getlimp(String e) {
//      super(e);
   }

   public void init(String params) {
      System.out.println("Snape / Limp Collector - By mofo");
   }

   public int main() {
      if(getFightMode() != 2)
         setFightMode(2);
      if(getFatigue() > 95) {
         useSleepingBag();
         return 1000;
      }
      if(isBanking()) {
         if(getInventoryCount(220) > 0) {
            deposit(220, getInventoryCount(220));
            return random(1000, 1500);
         }
         if(getInventoryCount(442) > 0) {
            deposit(442, getInventoryCount(442));
            return random(1000, 1500);
         }
         closeBank();
      }
      if(isQuestMenu()) {
         answer(0);
         return random(6000, 6500);
      }
      if(isAtApproxCoords(364, 608, 20) && getInventoryCount() != 30) {
         step = 0;
         path = new int[] {351, 621, 336, 620, 320, 607, 307, 594, 294, 584, 282, 569};
         int[] limp = getItemById(220);
         int[] snape = getItemById(442);
         int[] goblin = getNpcById(67);
         if(limp[0] != -1) {
            pickupItem(limp[0], limp[1], limp[2]);
            return random(1000, 1500);
         }
         if(snape[0] != -1 && goblin[0] == -1 && getInventoryCount(442) < 17) {
            pickupItem(snape[0], snape[1], snape[2]);
            return random(1000, 1500);
         }
         if(getHpPercent() > 15 && getInventoryCount(220) < 24) {
            if(goblin[0] != -1 && !inCombat())
               attackNpc(goblin[0]);
            return random(800, 1300);
         }
         if(snape[0] != -1) {
            pickupItem(snape[0], snape[1], snape[2]);
            return random(1000, 1500);
         }
         return 1000;
      }
      if(isAtApproxCoords(282, 569, 4) && (getInventoryCount(442) > 0 || getInventoryCount(220) > 0)) {
         int[] bankdoor = getObjectById(64);
         if(bankdoor[0] != -1) {
            atObject(bankdoor[1], bankdoor[2]);
            return random(1000, 1500);
          }
         step = 0;
         path = new int[] {294, 584, 307, 594, 320, 607, 336, 620, 351, 621, 364, 608};
         int[] npc = getNpcById(95);
         if(npc[0] != -1) {
            talkToNpc(npc[0]);
            return random(2000, 2700);
         }
      }
      if((step + 1) < path.length) {
         int[] bankdoor = getObjectById(64);
         if(bankdoor[0] != -1) {
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
         if(isAtApproxCoords(282, 569, 10))
            path = new int[] {294, 584, 307, 594, 320, 607, 336, 620, 351, 621, 364, 608};
         step = 0;
         loop = 0;
      }
      return random(1000, 1200);
   }
}