package scripting.apos;
import compatibility.apos.Script;
import java.util.HashMap;

public class k_ShopBuyer extends Script {

   private HashMap<Integer, Integer> start_inventory;
   private boolean veteran;
   private int start_gp;
   private int[] item_ids;
   private int menu;
   private int npc_id;
   private int started;
   private long time;
   private long answering;

//   public k_ShopBuyer(Extension e) {
//       super(e);
//   }

   public void init(String s) {
       System.out.println();

       //If you want to use this script on only one npc to buy only x, y, z, fill this out and leave parameters blank at runtime.
       //THINGS TO CHANGE
       veteran = false;
       npc_id = 149;
       item_ids = new int[]{33};
       //END OF THINGS TO CHANGE

       if (!s.isEmpty()) {
           System.out.println("Parameter string supplied");
           String[] params = s.split(",");
           veteran = params[0].equalsIgnoreCase("yes");
           npc_id = Integer.parseInt(params[1]);
           item_ids = new int[params.length - 2];
           for (int i = 0; i < params.length - 2; ++i) {
               item_ids[i] = Integer.parseInt(params[i + 2]);
           }
       }

       System.out.println("Veteran: " + (veteran ? "Yes" : "No"));
       System.out.println("NPC: " + getNpcNameId(npc_id));
       for (int i = 0; i < item_ids.length; ++i) {
           System.out.println("Item: " + getItemNameId(item_ids[i]));
       }

       start_inventory = new HashMap<>();
       start_gp = -1;
       menu = -1;
       started = 0;
       time = -1L;
       answering = -1L;
       System.out.println();
   }

   public int main() {
       if (started == 0) {
           for (int i = 0; i < MAX_INV_SIZE; ++i) {
               int id = getInventoryId(i);
               if (getInventoryIndex(id) == -1) {
                   continue;
               }
               int stack = getInventoryStack(i);
               start_inventory.put(id, stack);
           }
           started = getXpForLevel(3);
           start_gp = getInventoryCount(10);
           time = System.currentTimeMillis();
           return 100;
       }
       if (isWalking()) {
           return 10;
       }
       if (answering != -1L) {
           if (System.currentTimeMillis() > answering + 15000L) {
               answering = -1L;
               return 10;
           }
           if (isQuestMenu()) {
               if (menu == -1) {
                   String[] options = questMenuOptions();
                   for (int i = 0; i < questMenuCount(); ++i) {
                       String option = options[i].toLowerCase();
                       if (option.contains("sell") || option.contains("yes please") || option.contains("yes ok") || option.contains("see your wares") || option.contains("see what you've got") || option.contains("oh that sounds intersting")) {
                           menu = i;
                           return 10;
                       }
                   }
                   System.out.println("Menu option not supported. Please report the following to the script developer:");
                   for (String option : options) {
                       System.out.println(option);
                   }
                   stop();
               }
               answer(menu);
               return 10;
           }
           if (isShopOpen()) {
               answering = -1L;
               return 10;
           }
           return 10;
       }
       if (isShopOpen()) {
           for (int item_id : item_ids) {
               int slot = getShopItemById(item_id);
               if (slot == -1) {
                   continue;
               }
               int amount = getShopItemAmount(slot);
               if (amount > 1) {
                   buyShopItem(slot, amount);
                   return random(200, 400);
               }
           }
           //autohop(veteran);
           return random(400, 800);
       }
       int[] npc = getNpcById(npc_id);
       if (npc[0] != -1) {
           talkToNpc(npc[0]);
           return random(400, 800);
       }
		//if isBanking() {
		//	deposit( ///////////////////////
		//}
		
       return 500;
   }

   private String getRunTime() {
       long millis = (System.currentTimeMillis() - time) / 1000;
       long second = millis % 60;
       long minute = (millis / 60) % 60;
       long hour = (millis / (60 * 60)) % 24;
       long day = (millis / (60 * 60 * 24));

       if (day > 0L) return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
       if (hour > 0L) return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
       if (minute > 0L) return String.format("%02d minutes, %02d seconds", minute, second);
       return String.format("%02d seconds", second);
   }

   private int perHour(int total) {
       long time = ((System.currentTimeMillis() - this.time) / 1000L);
       if (time < 1L) {
           time = 1L;
       }
       return ((int) ((total * 60L * 60L) / time));
   }

   private void stop() {
       setAutoLogin(false);
       logout();
       stopScript();
   }

   @Override
   public void paint() {
       int x = 110;
       int y = 10;
       drawString("kRiStOf's Shop Buyer", x, y, 2, 0xFFFFFF);
       y += 12;
       drawString("Runtime: " + getRunTime(), x, y, 2, 0xFFFFFF);
       y += 12;
       int spent = getInventoryCount(10) - start_gp;
       if (spent > 0) {
           drawString("Coins spent: " + spent, x, y, 2, 0xFFFFFF);
           y += 12;
       }
       for (int item_id : item_ids) {
           int initial;
           if (start_inventory.containsKey(item_id)) {
               initial = start_inventory.get(item_id);
           } else {
               initial = 0;
           }
           int purchased = getInventoryCount(item_id) - initial;
           if (purchased > 0) {
               drawString(getItemNameId(item_id) + ": " + purchased + " (" + perHour(purchased) + "/h)", x, y, 2, 0xFFFFFF);
               y += 12;
           }
       }
   }

   @Override
   public void onServerMessage(String s) {
       s = s.toLowerCase();
       if (s.contains("store") || s.contains("hello") || s.contains("welcome") || s.contains("buy") || s.contains("how can i help") || s.contains("would you")) {
           answering = System.currentTimeMillis();
           return;
       }
       if (s.contains("you don't have enough money")) {
           takeScreenshot(System.currentTimeMillis() + "k_ShopBuyer");
           System.out.println("Out of money");
           System.out.println("Runtime: " + getRunTime());
           System.out.println("Coins spent: " + (getInventoryCount(10) - start_gp));
           for (int item_id : item_ids) {
               int purchased = getInventoryCount(item_id) - start_inventory.get(item_id);
               if (purchased > 0) {
                   System.out.println(getItemNameId(item_id) + ": " + purchased + " (" + perHour(purchased) + "/h)");
               }
           }
		   System.out.println("last stop");
           stop();
       }
   }
}