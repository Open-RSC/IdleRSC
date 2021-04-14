package scripting.apos;
import compatibility.apos.Script;

public class AlchWheatApos extends Script {
 
  int fmode = 3;
  public AlchWheatApos(String e) {
//     super(e);
  }
 
  public void init(String params) {
     if(!params.equals(""))
        fmode = Integer.parseInt(params);
  }

  public int main() {
     if(getFightMode() != fmode)
        setFightMode(fmode);
     if(getFatigue() > 90) {
        useSleepingBag();
        return 1000;
     }
     
     if(getInventoryCount(29) > 0) {
        castOnItem(28,getInventoryIndex(29));
        return 1000;
     }
     
     int[] whe = getObjectById(72);
     if(whe[0] != -1)
        atObject2(whe[1], whe[2]);
     return random(500, 1000);
  }
}