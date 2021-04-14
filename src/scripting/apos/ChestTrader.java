package scripting.apos;
import compatibility.apos.Script;
import java.awt.*;
import java.util.Arrays;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import com.aposbot.Constants;
//import com.aposbot.StandardCloseHandler;

public final class ChestTrader extends Script
implements ActionListener {

   private String tradingPartner;
   private long startTime = -1L;
   private long tradeTimer = -1L;
   private long bankTimer = -1L;
   private long tradeSentTimer = -1L;
   private boolean needToMove = false;
   private final DecimalFormat iformat = new DecimalFormat("#,##0");
   private static final int CHEST_ID = 942;

   private Frame frame;
   private TextField tf_tradingPartner;
   private Choice ch_GiveTake;
   private TextField tf_Items;

   private int[] tradeItems;
   private int itemIndex;
   private boolean giving;
   private boolean waitForBank, didWithdraw = false;
   private int tradeCount = 0;
   private int itemsRemaining = 0;

   public ChestTrader(String ex) {

   }

   public static void main(String[] argv) {
       ChestTrader t = new ChestTrader(null);
       t.init(null);
   }

   @Override
   public void init(String params) {

       if (frame == null) {

           Panel pInput = new Panel(new GridLayout(0, 2, 0, 2));

           ch_GiveTake = new Choice();
           ch_GiveTake.add("Give");
           ch_GiveTake.add("Take");
           pInput.add(ch_GiveTake);
           pInput.add(new Label());

           pInput.add(new Label("Name to trade:"));
           pInput.add(tf_tradingPartner = new TextField());

           pInput.add(new Label("Item ids (id1, id2, ...)"));
           pInput.add(tf_Items = new TextField());

           Button button;
           Panel pButtons = new Panel();
           button = new Button("OK");
           button.addActionListener(this);
           pButtons.add(button);
           button = new Button("Cancel");
           button.addActionListener(this);
           pButtons.add(button);

           frame = new Frame(getClass().getSimpleName());
//           frame.addWindowListener(
//               new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
//           );
//           frame.setIconImages(Constants.ICONS);
           frame.add(pInput, BorderLayout.NORTH);
           frame.add(pButtons, BorderLayout.SOUTH);
           frame.setResizable(false);
           frame.pack();
       }
       frame.setLocationRelativeTo(null);
       frame.toFront();
       frame.requestFocus();
       frame.setVisible(true);

   }

   @Override
   public int main() {
       if (startTime == -1L) startTime = System.currentTimeMillis();

       // resets the trade timer when the other player declines the trade
       if (!isInTradeConfirm() && !isInTradeOffer()) {
           tradeTimer = -1L;
       }

       if (waitForBank && !isBanking()) {
           if (System.currentTimeMillis() - bankTimer > 10000) {
               System.out.println("Triggered check for failing to open bank");
               waitForBank = false;
               bankTimer = -1L;
           }
           return 50;
       }
       if (isBanking()) {
           waitForBank = false;
           bankTimer = -1L;
           tradeTimer = -1L;
           tradeSentTimer = -1L;
           if (giving) itemsRemaining = bankCount(tradeItems);
           // this bit is to account for a little lag in withdrawing
           if (didWithdraw)  {
               didWithdraw = false;
               closeBank();
               return 600;
           }
           if (giving) {
               if (bankCount(tradeItems[itemIndex]) < 24) itemIndex++;
               if (getInventoryCount(tradeItems[itemIndex]) < 24) {
                   int need = 24 - getInventoryCount(tradeItems[itemIndex]);
                   withdraw(tradeItems[itemIndex], need);
                   didWithdraw = true;
                   return 800;
               } else {
                   closeBank();
                   return 600;
               }
           } else {
               if (getInventoryCount() > 0) {
                   deposit(getInventoryId(0), getInventoryCount(getInventoryId(0)));
                   return 600;
               } else {
                   closeBank();
                   return 600;
               }
           }
       }

       if (isInTradeConfirm()) {
           tradeSentTimer = -1L;
           bankTimer = -1L;
           if (tradeTimer == -1L) {
               tradeTimer = System.currentTimeMillis();
           } else {
               if (System.currentTimeMillis() - tradeTimer > 10000) {
                   System.out.println("Triggered check for trade taking too long");
                   declineTrade();
                   return 600;
               }
           }
           confirmTrade();
           return 600;
       }

       if (isInTradeOffer()) {
           tradeSentTimer = -1L;
           if (tradeTimer == -1L) {
               tradeTimer = System.currentTimeMillis();
           } else {
               if (System.currentTimeMillis() - tradeTimer > 10000) {
                   System.out.println("Triggered check for trade taking too long");
                   declineTrade();
                   return 600;
               }
           }
           if (giving) {
               if (getLocalTradeItemCount() <= 0) {
                   int  slot = getInventoryIndex(tradeItems[itemIndex]);
                   // should always be 12 if our banking was good
                   offerItemTrade(slot, Math.max(12, getInventoryCount(tradeItems[itemIndex])));
                   return 600;
               }
           }
           acceptTrade();
           return 600;
       }

       tradeTimer = -1L;

       if (needToMove) {
           needToMove = false;
           if (isReachable(getX() + 1, getY())) {
               walkTo(getX()+1,getY());
               return 1500;
           }
           if (isReachable(getX(), getY()+1)) {
               walkTo(getX(),getY()+1);
               return 1500;
           }
           if (isReachable(getX(), getY()-1)) {
               walkTo(getX(),getY()-1);
               return 1500;
           }
           if (isReachable(getX()-1, getY())) {
               walkTo(getX()-1,getY());
               return 1500;
           }
       }

       if (!giving) { // taking
           if (getInventoryCount() > 12) {
               int[] chest = getObjectById(CHEST_ID);
               if (chest[0] != -1) {
                   atObject(chest[1], chest[2]);
                   return 600;
               }
               return 300;
           } else {
               int[] player = getPlayerByName(tradingPartner);
               if (player[0] != -1) {
                   if (tradeSentTimer == -1L) {
                       tradeSentTimer = System.currentTimeMillis();
                       sendTradeRequest(getPlayerPID(player[0]));
                       return 600;
                   } else {
                       if (System.currentTimeMillis() - tradeSentTimer > 3000) {
                           tradeSentTimer = System.currentTimeMillis();
                           sendTradeRequest(getPlayerPID(player[0]));
                           return 600;
                       } else {
                           return 50;
                       }
                   }
               }
           }
       } else { // giving
           if (getInventoryCount() < 12) {
               int[] chest = getObjectById(CHEST_ID);
               if (chest[0] != -1) {
                   atObject(chest[1], chest[2]);
                   return 500;
               }
               return 300;
           } else {
               int[] player = getPlayerByName(tradingPartner);
               if (player[0] != -1) {
                   if (tradeSentTimer == -1L) {
                       tradeSentTimer = System.currentTimeMillis();
                       sendTradeRequest(getPlayerPID(player[0]));
                       return 600;
                   } else {
                       if (System.currentTimeMillis() - tradeSentTimer > 3000) {
                           System.out.println("Triggered check for not in trade");
                           sendTradeRequest(getPlayerPID(player[0]));
                           return 600;
                       } else {
                           return 50;
                       }
                   }
               }
           }
       }
       return 1000;
   }

   @Override
   public void onServerMessage(String str) {
       str = str.toLowerCase();
       if (str.contains("this chest")) {
           waitForBank = true;
           bankTimer = System.currentTimeMillis();
           tradeTimer = -1L;
           tradeSentTimer = -1L;
       } else if (str.contains("have been standing")) {
           needToMove = true;
       } else if (str.contains("trade completed")) {
           tradeCount += 12;
           tradeTimer = -1L;
           tradeSentTimer = -1L;
       } else if (str.contains("declined")) {
           tradeTimer = -1L;
           tradeSentTimer = -1L;
       }
   }

   // ripped from Shantay_Trader
   private String per_hour(long count, long time) {
       double amount, secs;

       if (count == 0) return "0";
       amount = count * 60.0 * 60.0;
       secs = (System.currentTimeMillis() - time) / 1000.0;
       return iformat.format(amount / secs);
   }

   @Override
   public void paint() {
       final int white = 0xFFFFFF;
       final int cyan = 0x00FFFF;
       int y = 25;
        int num = 0;
       drawString("Shantay Chest Trader", 25, y, 1, white);
       y += 15;
       drawString("Runtime: " + get_time_since(startTime), 25, y, 1, white);
       y += 15;
       if (giving) {
           drawString("Remaining Items: " + itemsRemaining + "/h", 25, y, 1, white);
           y += 15;
       }
       drawString("Transfer rate: " + per_hour(tradeCount, startTime) + "/h", 25, y, 1, white);
       y += 15;
   }

   // ripped from somewhere.. one of the S_ scripts
    private static String get_time_since(long t) {
        long millis = (System.currentTimeMillis() - t) / 1000;
        long second = millis % 60;
        long minute = (millis / 60) % 60;
        long hour = (millis / (60 * 60)) % 24;
        long day = (millis / (60 * 60 * 24));

        if (day > 0L) {
            return String.format("%02d days, %02d hrs, %02d mins",
                    day, hour, minute);
        }
        if (hour > 0L) {
            return String.format("%02d hours, %02d mins, %02d secs",
                    hour, minute, second);
        }
        if (minute > 0L) {
            return String.format("%02d minutes, %02d seconds",
                    minute, second);
        }
        return String.format("%02d seconds", second);
    }

   @Override
    public void actionPerformed(ActionEvent aE) {
        if (aE.getActionCommand().equals("OK")) {
            String[] items;
            tradingPartner = tf_tradingPartner.getText().trim();
            System.out.println("Trading partner is " + tradingPartner);
            if (ch_GiveTake.getSelectedItem() == "Give") {
                System.out.println("Option selected: GIVING");
                giving = true;
            } else {
                System.out.println("Option selected: TAKING");
                giving  = false;
            }
            if (giving) {
                items = tf_Items.getText().trim().split(",");
                tradeItems = new int[items.length];
                System.out.println("Giving the following items: ");
                for (int i = 0; i < items.length; i++) {
                    System.out.println(getItemNameId(Integer.parseInt(items[i])));
                    tradeItems[i] = Integer.parseInt(items[i]);
                }
            }
            tradingPartner = tf_tradingPartner.getText().trim();
        }
        frame.setVisible(false);
    }
}
