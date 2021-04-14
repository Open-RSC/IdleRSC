package scripting.apos;
import compatibility.apos.Script;
import java.io.PrintStream;

public class rena_unis
extends Script {
   int step = 0;
   int[] path = null;
   int loop = 0;
   int fightMode = 0;
   int horn = 0;
   int trips = 0;
   boolean walk_lumbydeath;
   boolean bankingnow;
   boolean walk_edgetounis;
   boolean fightingnow;
   boolean walk_unistoedge;
   boolean started;

   public rena_unis(String extension) {
//       super(extension);
   }

   public void init(String string) {
       System.out.println(" ");
       System.out.println("[================== !~ RENAFOX'S SCRIPTS ~! ===================]");
       System.out.println(" ");
       System.out.println("[==================== !~ INSCTRUCTIONS ~! ======================]");
       System.out.println("[=== !~ rena_unis <fightmode>                             ~! ===]");
       if (string.equalsIgnoreCase("0")) {
           System.out.println("[Rena] Fighting controlled style");
           this.fightMode = 1;
       } else if (string.equalsIgnoreCase("1")) {
           System.out.println("[Rena] Fighting Aggressive style");
           this.fightMode = 1;
       } else if (string.equalsIgnoreCase("2")) {
           System.out.println("[Rena] Fighting Accurate style");
           this.fightMode = 2;
       } else if (string.equalsIgnoreCase("3")) {
           System.out.println("[Rena] Fighting Defensive style");
           this.fightMode = 3;
       }
       this.walk_lumbydeath = false;
       this.bankingnow = true;
       this.walk_edgetounis = false;
       this.fightingnow = false;
       this.walk_unistoedge = false;
   }

   public int main() {
       if (this.getFightMode() != this.fightMode) {
           this.setFightMode(this.fightMode);
       }
       if (!(!this.walk_lumbydeath || this.bankingnow || this.walk_edgetounis || this.fightingnow || this.walk_unistoedge)) {
           if (this.isAtApproxCoords(120, 648, 3)) {
               this.walkTo(133, 636);
               return rena_unis.random((int)1000, (int)1500);
           }
           if (this.isAtApproxCoords(133, 636, 1)) {
               this.step = 0;
               this.path = new int[]{138, 615, 157, 613, 173, 609, 185, 605, 192, 604, 193, 586, 193, 569, 190, 548, 205, 532, 217, 509, 217, 489, 220, 475, 215, 451};
           }
           if (this.isAtApproxCoords(217, 453, 5)) {
               this.walk_lumbydeath = false;
               this.bankingnow = true;
               this.walk_edgetounis = false;
               this.fightingnow = false;
               this.walk_unistoedge = false;
               this.started = false;
               this.step = 0;
               this.loop = 0;
               this.path = null;
               System.out.println("[Rena] Switching to banking !");
               return rena_unis.random((int)1000, (int)1500);
           }
       }
       if (!(this.walk_lumbydeath || !this.bankingnow || this.walk_edgetounis || this.fightingnow || this.walk_unistoedge)) {
           if (this.isBanking()) {
               if (this.getInventoryCount(new int[]{466}) > 0) {
                   this.deposit(466, this.getInventoryCount(new int[]{466}));
                   this.horn += this.getInventoryCount(new int[]{466});
                   return rena_unis.random((int)900, (int)1000);
               }
               this.walk_lumbydeath = false;
               this.bankingnow = false;
               this.walk_edgetounis = true;
               this.fightingnow = false;
               this.walk_unistoedge = false;
               this.started = false;
               this.step = 0;
               this.loop = 0;
               this.path = null;
               this.closeBank();
               System.out.println("========= ~Status Reporting~ =========");
               System.out.println("Did " + this.trips + " trips so far!");
               System.out.println("Obtained " + this.horn + " unicorn horns so far!");
               System.out.println(" ");
               return rena_unis.random((int)800, (int)1000);
           }
           if (this.isQuestMenu()) {
               this.answer(0);
               return rena_unis.random((int)5000, (int)5200);
           }
           if (this.distanceTo(217, 453) < 6) {
               int[] arrn = this.getNpcByIdNotTalk(new int[]{95});
               if (arrn[0] != -1) {
                   this.talkToNpc(arrn[0]);
               }
               return 4500;
           }
           this.walkTo(216, 450);
           return 1000;
       }
       if (!(this.walk_lumbydeath || this.bankingnow || !this.walk_edgetounis || this.fightingnow || this.walk_unistoedge)) {
           if (this.isAtApproxCoords(120, 648, 4)) {
               this.walk_lumbydeath = true;
               this.bankingnow = false;
               this.walk_edgetounis = false;
               this.fightingnow = false;
               this.walk_unistoedge = false;
               this.started = false;
               this.step = 0;
               this.loop = 0;
               this.path = null;
               System.out.println("[Rena] Died somehow ... Walking back to edge !");
           }
           if (this.isAtApproxCoords(217, 453, 5)) {
               int[] arrn = this.getObjectById(new int[]{64});
               if (arrn[0] != -1 && arrn[2] < 451) {
                   System.out.println("[Rena] Door closed? Opening it !");
                   this.atObject(arrn[1], arrn[2]);
                   return rena_unis.random((int)1000, (int)1500);
               }
               this.walkTo(218, 446);
               return rena_unis.random((int)1000, (int)1500);
           }
           if (this.isAtApproxCoords(218, 446, 1)) {
               this.step = 0;
               this.path = new int[]{206, 444, 191, 435, 191, 425, 191, 415, 190, 405, 191, 395, 191, 385, 191, 375, 191, 361, 188, 353, 178, 344, 169, 336, 155, 326, 145, 317, 133, 307, 122, 299};
               System.out.println("[Rena] Commencing run! (Edge to magical unicorns) ");
           }
           if (this.isAtApproxCoords(122, 299, 1)) {
               this.walk_lumbydeath = false;
               this.bankingnow = false;
               this.walk_edgetounis = false;
               this.fightingnow = true;
               this.walk_unistoedge = false;
               this.started = false;
               System.out.println("[Rena] Reached Magical unicorns paradise !");
               System.out.println("[Rena] Its Kung-Fu Fighting Time !");
               this.step = 0;
               this.loop = 0;
               this.path = null;
               return rena_unis.random((int)500, (int)600);
           }
       }
       if (!(this.walk_lumbydeath || this.bankingnow || this.walk_edgetounis || !this.fightingnow || this.walk_unistoedge)) {
           if (this.isAtApproxCoords(120, 648, 4)) {
               this.walk_lumbydeath = true;
               this.bankingnow = false;
               this.walk_edgetounis = false;
               this.fightingnow = false;
               this.walk_unistoedge = false;
               this.started = false;
               this.step = 0;
               this.loop = 0;
               this.path = null;
               System.out.println("[Rena] Died somehow ... Walking back to edge !");
           }
           if (this.getHpPercent() < 50 && this.getHpPercent() < 30) {
               System.out.println("[Rena] HP at Dangerous Levels Somehow... Escaping!");
               System.out.println("[Rena] Time to go Home! (Edge)");
               System.out.println(" ");
               this.walk_lumbydeath = false;
               this.bankingnow = false;
               this.walk_edgetounis = false;
               this.fightingnow = false;
               this.walk_unistoedge = true;
               this.started = false;
               this.step = 0;
               this.loop = 0;
               this.path = null;
               return 1000;
           }
           if (this.getInventoryCount() == 30) {
               this.walk_lumbydeath = false;
               this.bankingnow = false;
               this.walk_edgetounis = false;
               this.fightingnow = false;
               this.walk_unistoedge = true;
               this.started = false;
               System.out.println("[Rena] Time to go Home! (Edge)");
               System.out.println(" ");
               return rena_unis.random((int)1600, (int)1800);
           }
           int[] arrn = this.getItemById(new int[]{466});
           if (arrn[0] != -1) {
               if (this.inCombat()) {
                   this.walkTo(this.getX(), this.getY());
               }
               this.walkTo(arrn[1], arrn[2]);
               this.pickupItem(466, arrn[1], arrn[2]);
               System.out.println("[Rena] Picking up uni horn at " + arrn[1] + "," + arrn[2] + " ...");
               System.out.println(" ");
               return rena_unis.random((int)200, (int)300);
           }
           if (this.getFatigue() > 80) {
               if (this.inCombat()) {
                   this.walkTo(this.getX(), this.getY());
               }
               this.useSleepingBag();
               System.out.println("[Rena] Fatigue %: 80 reached! Sleeping... ");
               System.out.println(" ");
               return 1000;
           }
           int[] arrn2 = this.getNpcInRadius(296, 122, 299, 15);
           if (arrn2[0] != -1 && !this.inCombat()) {
               this.attackNpc(arrn2[0]);
               return rena_unis.random((int)200, (int)300);
           }
           if (arrn2[0] == -1 && !this.inCombat()) {
               this.walkTo(122, 299);
               return rena_unis.random((int)900, (int)1100);
           }
           if (!this.isAtApproxCoords(122, 299, 15) && !this.inCombat()) {
               this.walkTo(122, 299);
               System.out.println("[Rena] Went outside Magical Unicorns Zone ... Walking back!");
               System.out.println(" ");
               return rena_unis.random((int)900, (int)1100);
           }
           return rena_unis.random((int)900, (int)1200);
       }
       if (!(this.walk_lumbydeath || this.bankingnow || this.walk_edgetounis || this.fightingnow || !this.walk_unistoedge)) {
           if (this.isAtApproxCoords(120, 648, 4)) {
               this.walk_lumbydeath = true;
               this.bankingnow = false;
               this.walk_edgetounis = false;
               this.fightingnow = false;
               this.walk_unistoedge = false;
               this.started = false;
               System.out.println("[Rena] Died somehow ... Walking back to edge !");
           }
           if (this.inCombat()) {
               this.walkTo(this.getX(), this.getY());
               System.out.println("[Rena] DaFaq? We're being attacked ! Running ...");
               return rena_unis.random((int)150, (int)250);
           }
           if (this.isAtApproxCoords(217, 453, 5)) {
               this.walk_lumbydeath = false;
               this.bankingnow = true;
               this.walk_edgetounis = false;
               this.fightingnow = false;
               this.walk_unistoedge = false;
               this.started = false;
               this.step = 0;
               this.loop = 0;
               this.path = null;
               System.out.println("[Rena] Successfully reached bank after completing a trip! ");
               System.out.println("[Rena] Banking... ");
           }
           if (this.isAtApproxCoords(122, 299, 25) && !this.isAtApproxCoords(122, 299, 1) && !this.started) {
               this.walkTo(122, 299);
               System.out.println("[Rena] Walking outta here ... ");
               return rena_unis.random((int)150, (int)250);
           }
           if (this.isAtApproxCoords(122, 299, 1)) {
               this.started = true;
               this.step = 0;
               this.path = new int[]{133, 307, 145, 317, 155, 326, 169, 336, 178, 344, 188, 353, 191, 361, 191, 375, 191, 385, 191, 395, 190, 405, 191, 415, 191, 425, 191, 435, 206, 444, 218, 446, 216, 451};
               System.out.println("[Rena] Commencing run! (Magical unicorns to edge) ");
           }
       }
       if (this.step + 1 < this.path.length) {
           int[] arrn = this.getObjectById(new int[]{64});
           if (arrn[0] != -1 && arrn[2] < 451) {
               this.atObject(arrn[1], arrn[2]);
               return rena_unis.random((int)1000, (int)1500);
           }
           if (this.isAtApproxCoords(this.path[this.step], this.path[this.step + 1], 1)) {
               this.step += 2;
           }
           this.walkTo(this.path[this.step], this.path[this.step + 1]);
           System.out.println("[Rena] Running . . . ! ");
           return rena_unis.random((int)350, (int)550);
       }
       ++this.loop;
       if (this.loop > 10) {
           this.step = 0;
           this.loop = 0;
       }
       return rena_unis.random((int)500, (int)600);
   }
}