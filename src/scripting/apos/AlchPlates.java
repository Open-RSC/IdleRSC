package scripting.apos;
import compatibility.apos.Script;
public class AlchPlates extends Script

{

// public AlchPlates(Extension paramExtension)
//
// {
//
//   super(paramExtension);
//
// }



 public void init(String paramString) {

   System.out.println("Steel Plate Alcher by XcendroX start in bank with fire staff and natures");

 }



 public int main()

 {

   if (getFatigue() > 90) {

     useSleepingBag();

     return 1000;

   }



   if (getInventoryCount(new int[] { 118 }) > 0) {

     castOnItem(28, getInventoryIndex(new int[] { 118 }));

     return 1000;

   }

   if (isBanking()) {

     if (getInventoryCount(new int[] { 118 }) == 0) {

       withdraw(118, 25);



       return random(1000, 1500);

     }

     closeBank();

     return random(500, 600);

   }

   if (isQuestMenu()) {

     answer(0);

     return random(500, 600);

   }

   int[] arrayOfInt = getNpcByIdNotTalk(this.BANKERS);

   if (arrayOfInt[0] != -1) {

     talkToNpc(arrayOfInt[0]);

     return 1000;

   }

   return random(500, 1000);

 }

}