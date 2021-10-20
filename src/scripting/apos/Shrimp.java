package scripting.apos;
import compatibility.apos.Script;

public class Shrimp extends Script {


// By Mofo

   int sleepAt = 90;

   public Shrimp(String e) {
      //super(e);
   }

   public void init(String params) {
      if(!params.equals(""))
         sleepAt = Integer.parseInt(params);
   }

   public int main() {

      if(getFatigue() > sleepAt) {
         useSleepingBag();
         return 1000;
      }
      if(!isAtApproxCoords(418, 500, 5) && (getInventoryCount(350) + getInventoryCount(352) + getInventoryCount(353)) == 0 && getInventoryCount() < 25) {
         walkTo(418, 499);
         return random(2500, 3000);
      }
      if(isAtApproxCoords(418,500, 5) && getInventoryCount() != 30) {
         int[] fish = getObjectById(new int[]{193});
         if( fish[0] != -1 ) {
            atObject(418, 500);
         }
         return random(800, 1500);
      }
      if(isAtApproxCoords(418,500, 5) && getInventoryCount() == 30) {
         walkTo(430,493);
         return random(1000, 1500);
      }
      if(isAtApproxCoords(430,493, 5) && getInventoryCount() == 30) {
         walkTo(433, 484);
         return random(2000, 3000);
      }
      if(isAtApproxCoords(433,484, 4) && (getInventoryCount(351) + getInventoryCount(349)) != 0) {
         int[] range = getObjectById(new int[]{11});
         if(range[0] != -1) {
            if(getInventoryCount(349) != 0) {
               useItemOnObject(349, 11);
               return random(1000, 1500);
            } else if(getInventoryCount(351) != 0) {
               useItemOnObject(351, 11);
               return random(1000, 1500);
            }
            return 1000;
         }
         return 1000;
      }
      if(isAtApproxCoords(433,484, 5) && (getInventoryCount(352) + getInventoryCount(350) + getInventoryCount(353)) != 0) {
         System.out.println("dropping / eating");
         if(getInventoryCount(352) != 0) {
            if(getInventoryIndex(352) != -1) {
               useItem(getInventoryIndex(352));
               return random(1000, 1200);
            }
            return 500;
         } else if(getInventoryCount(350) != 0) {
            if(getInventoryIndex(350) != -1) {
               useItem(getInventoryIndex(350));
               return random(1000, 1200);
            }
            return 500;
         } else if(getInventoryCount(353) != 0) {
            if(getInventoryIndex(353) != -1) {
               dropItem(getInventoryIndex(353));
               return random(1000, 1200);
            }
            return 500;
         }
         return 1000;
      }
      if(isAtApproxCoords(433,484, 5) && getInventoryCount() != 30) {
         walkTo(430, 493);
         return random(2000, 3000);
      }
      if(isAtApproxCoords(430,493, 5) && getInventoryCount() != 30) {
         walkTo(418, 500);
         return random(2500, 3000);
      }
      return 1000;
   }
}
