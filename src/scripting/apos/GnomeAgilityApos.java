package scripting.apos;

import compatibility.apos.Script;

/**
*  START AT THE END OF AN OBSTACLE!
*/
public class GnomeAgilityApos extends Script {
 
  int fmode = 1;
 
//  public GnomeAgility(Extension e)  {
//  super(e);
//  }
 
  public void init(String params){
     if(!params.equals(""))
     fmode = Integer.parseInt(params);
  }
 
  public int main(){
     int[] obj = new int[]{-1,-1,-1};
     
     if(getFightMode() != fmode)
        setFightMode(fmode);
     if(getFatigue() > 90) {
        useSleepingBag();
        return 1000;
     }
     if (getX() == 683 && getY() == 494) {
         obj = getObjectById(655); // log
         atObject(obj[1], 495);
         return 2000;
     }
      if (getX() == 692 && getY() == 499) {
          obj = getObjectById(647); // log
          atObject(obj[1], obj[2]);
          return 2000;
      }
      if (getX() == 692 && getY() == 1448) {
          obj = getObjectById(648); // log
          atObject(obj[1], obj[2]);
          return 2000;
      }
      if (getX() == 693 && getY() == 2394) {
          obj = getObjectById(650); // log
          atObject(obj[1], obj[2]);
          return 2000;
      }
      if (getX() == 685 && getY() == 2396) {
          obj = getObjectById(649); // log
          atObject(obj[1], obj[2]);
          return 2000;
      }
      if (getX() == 683 && getY() == 506) {
          obj = getObjectById(653); // log
          atObject(obj[1], obj[2]);
          return 2000;
      }
      if (getX() == 683 && getY() == 501) {
          obj = getObjectById(654); // log
          atObject(obj[1], obj[2]);
          return 2000;
      }
     return 2000;
  }
}
