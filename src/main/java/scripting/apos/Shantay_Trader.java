package scripting.apos;

import compatibility.apos.Script;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;

// import com.aposbot.Constants;
// import com.aposbot.StandardCloseHandler;

public final class Shantay_Trader extends Script implements ActionListener {

  private int BANK_CHEST_X = 58;
  private int BANK_CHEST_Y = 731;
  private boolean[] itm_banked;
  private String name;
  private long bank_time;
  private long menu_time;
  private long start_time;
  private long items_traded;
  private int check_x;
  private int check_y;
  private boolean move_to;
  private boolean is_idle;
  private boolean stop_now;
  private final DecimalFormat iformat = new DecimalFormat("#,##0");
  private static final int GNOMEBALL_ID = 981;
  private static final int CHEST_ID = 942;

  private Frame frame;
  private TextField tf_name;
  private TextField tf_give;
  private TextField tf_take;
  private Checkbox cb_loc;

  private int[] give_ids;
  private int[] give_count;
  private int[] take_ids;
  private int[] take_count;

  private boolean[] itm_offered;
  private int ptr;
  private int last_item_id;

  private boolean giving;
  private boolean taking;
  private boolean shantay;
  private boolean loc;

  public Shantay_Trader(String ex) {
    // super(ex);
  }

  //   public static void main(String[] argv) {
  //       Shantay_Trader t = new Shantay_Trader(null);
  //       t.init(null);
  //   }

  @Override
  public void init(String params) {

    if (frame == null) {

      Panel pInput = new Panel(new GridLayout(0, 2, 0, 2));
      pInput.add(new Label("Name to trade:"));
      pInput.add(tf_name = new TextField());

      pInput.add(new Label("Take Item ids (1,2,3...):"));
      pInput.add(tf_take = new TextField());

      pInput.add(new Label("Give Item ids (1,2,3...):"));
      pInput.add(tf_give = new TextField());

      Panel cbPanel = new Panel();
      cbPanel.setLayout(new GridLayout(0, 1));
      cbPanel.add(cb_loc = new Checkbox("Trade in bank instead of Shantay"));

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
      frame.add(cbPanel, BorderLayout.CENTER);
      frame.add(pButtons, BorderLayout.SOUTH);
      frame.setResizable(false);
      frame.pack();
    }
    frame.setLocationRelativeTo(null);
    frame.toFront();
    frame.requestFocusInWindow();
    frame.setVisible(true);

    start_time = System.currentTimeMillis();
    menu_time = -1L;
    bank_time = -1L;
    ptr = 0;
    items_traded = 0;
    last_item_id = -1;
    check_x = -1;
    check_y = -1;
    move_to = false;
    is_idle = false;
    stop_now = false;
    shantay = false;
  }

  @Override
  public int main() {
    while (this.frame.isVisible()) {
      try {
        Thread.sleep(1L);
      } catch (InterruptedException e) {
      }
    }
    if (stop_now && (ptr >= (give_ids.length))) {
      stopScript();
      return 0;
    }

    if (!isFriend(name)) {
      if (getFriendCount()
          < 400) { // If not friends script will stop when 1 account is out of items not both.
        addFriend(name);
        return random(600, 600);
      }
    }

    if (loc) {
      if (isQuestMenu()) {
        menu_time = -1L;
        answer(0);
        bank_time = System.currentTimeMillis();
        return random(600, 600);
      } else if (menu_time != -1L) {
        if (System.currentTimeMillis() >= (menu_time + 8000L)) {
          menu_time = -1L;
        }
        return random(300, 400);
      }
    }

    if (isBanking()) {
      bank_time = -1L;
      int itm_sz = take_ids.length;
      for (int i = 0; i < itm_sz; ++i) {
        int id = take_ids[i];
        int count = getInventoryCount(id);
        if (count > 0) {
          if (!itm_banked[i]) {
            take_count[i] += count;
            itm_banked[i] = true;
            if (!giving) {
              items_traded += count;
            }
          }
          deposit(id, count);
          return random(600, 600);
        }
      }
      if (giving) {
        if (getInventoryCount(give_ids[ptr]) != 0 || last_item_id == give_ids[ptr]) {
          last_item_id =
              -1; // Prevent multiple withrawl attempts of the same item id due to missing server
          // ticks
          closeBank();
          return random(600, 600);
        }
        last_item_id = give_ids[ptr];
        int w = getEmptySlots();
        if (w > 24) w = 24;
        int bankc = bankCount(give_ids[ptr]);
        while (bankc <= 23) {
          if (ptr >= (give_ids.length - 1)) {
            System.out.println("ERROR: Out of items.");
            if (isFriend(name)) {
              sendPrivateMessage("Stop", name);
            }
            stopScript();
            return 0;
          }
          bankc = bankCount(give_ids[++ptr]);
          System.out.println("Next item");
        }
        if (w > bankc) w = bankc;
        withdraw(give_ids[ptr], w);
      } else {
        closeBank();
      }
      return random(600, 600);
    } else if (bank_time != -1L) {
      if (System.currentTimeMillis() >= (bank_time + 8000L)) {
        bank_time = -1L;
      }
      return random(300, 400);
    }
    if (!giving) {
      if (loc) {
        if (getInventoryCount() > 18) {
          int[] banker = getNpcByIdNotTalk(BANKERS);
          if (banker[0] != -1) {
            menu_time = System.currentTimeMillis();
            talkToNpc(banker[0]);
          }
          return random(600, 600);
        }
      } else {
        if (getInventoryCount() > 18) {
          int[] chest_object = getObjectById(CHEST_ID);
          if (chest_object[0] != -1) {
            atObject(chest_object[1], chest_object[2]);
            return random(1000, 1500);
          }
          return random(600, 600);
        }
      }
    } else {
      if (loc) {
        if (getInventoryCount(give_ids[ptr]) <= 0) {
          int[] banker = getNpcByIdNotTalk(BANKERS);
          if (banker[0] != -1) {
            menu_time = System.currentTimeMillis();
            talkToNpc(banker[0]);
          }
          return random(600, 600);
        }
      } else {
        if (getInventoryCount(give_ids[ptr]) <= 0) {
          int[] chest_object = getObjectById(CHEST_ID);
          if (chest_object[0] != -1) {
            atObject(chest_object[1], chest_object[2]);
            return random(1000, 1500);
          }
          return random(600, 600);
        }
      }
    }
    if (isInTradeConfirm()) {
      confirmTrade();
      return random(600, 600);
    }
    if (isInTradeOffer()) {

      if (giving) {
        if (getLocalTradeItemCount() <= 0) {
          int index = getInventoryIndex(give_ids[ptr]);
          if (index != -1) {
            int count = getInventoryCount(give_ids[ptr]);
            if (count > 12) count = 12;
            offerItemTrade(index, count);
            if (!itm_offered[ptr]) {
              give_count[ptr] += count;
              items_traded += count;
              itm_offered[ptr] = true;
            }
            return random(1000, 1000);
          }
        }
      }
      acceptTrade();
      return random(1000, 100);
    }
    int[] player = getPlayerByName(name);
    if (player[0] == -1) {
      System.out.println("Couldn't find player: " + name);
      System.out.println("Make sure you entered their name properly.");
      return random(1000, 1000);
    }
    if ((is_idle) && (!loc)) { // ANTI LOGOUT
      int x = getX();
      int y = getY();
      if (x == 59 && y == 731) {
        walk_for_idle(x, y);
        return random(1000, 1000);
      } else {
        is_idle = false;
      }
    }
    int ball = getInventoryIndex(GNOMEBALL_ID);
    if (ball != -1) {
      dropItem(ball);
      return random(1000, 1200);
    }
    if (!isWalking()) {
      if (move_to) {
        walkTo(player[1], player[2]);
        move_to = false;
      } else {
        if (taking) {
          Arrays.fill(itm_banked, false);
        }
        if (giving) {
          Arrays.fill(itm_offered, false);
        }
        sendTradeRequest(getPlayerPID(player[0]));
        return random(2000, 3000);
      }
    }
    return random(600, 800);
  }

  @Override
  public void onServerMessage(String str) {
    str = str.toLowerCase(Locale.ENGLISH);
    if (str.contains("This chest")) {
      bank_time = System.currentTimeMillis();
    } else if (str.contains("have been standing")) {
      is_idle = true;
    } else if (str.contains("not near")) {
      move_to = true;
    } else if (str.contains("busy")) {
      menu_time = -1L;
    }
  }

  private String per_hour(long count, long time) {
    double amount, secs;

    if (count == 0) return "0";
    amount = count * 60.0 * 60.0;
    secs = (System.currentTimeMillis() - time) / 1000.0;
    return iformat.format(amount / secs);
  }

  private long getTime() {
    long secondsSinceStarted = ((System.currentTimeMillis() - start_time) / 1000);
    if (secondsSinceStarted <= 0) {
      return 1L;
    }
    return secondsSinceStarted;
  }

  private String getRunTime() {
    long millis = getTime();
    long second = millis % 60;
    long minute = (millis / 60) % 60;
    long hour = (millis / (60 * 60)) % 24;
    long day = (millis / (60 * 60 * 24));

    if (day > 0L) return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
    if (hour > 0L) return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
    if (minute > 0L) return String.format("%02d minutes, %02d seconds", minute, second);
    return String.format("%02d seconds", second);
  }

  private void walk_for_idle(int x, int y) {
    if (isReachable(x + 1, y) && !is_player_on_pos(x + 1, y)) {
      walkTo(x + 1, y);
    } else if (isReachable(x, y + 1) && !is_player_on_pos(x, y + 1)) {
      walkTo(x, y + 1);
    } else if (isReachable(x + 1, y + 1) && !is_player_on_pos(x + 1, y + 1)) {
      walkTo(x + 1, y + 1);
    }
  }

  private boolean is_player_on_pos(int x, int y) {
    int count = countPlayers();
    for (int i = 1; i < count; ++i) {
      if (getPlayerX(i) == x && getPlayerY(i) == y) return true;
    }
    return false;
  }

  @Override
  public void paint() {
    final int white = 0xFFFFFF;
    final int cyan = 0x00FFFF;
    int y = 25;
    int num = 0;
    drawString("Shantay Chest Trader", 25, y, 1, cyan);
    y += 15;
    drawString("Runtime: " + getRunTime(), 25, y, 1, white);
    y += 15;

    if (taking) {
      num = take_ids.length;
      for (int i = 0; i < num; ++i) {
        if (take_count[i] <= 0) {
          continue;
        }
        drawString("Banked " + getItemNameId(take_ids[i]) + ": " + take_count[i], 25, y, 1, white);
        y += 15;
      }
    }
    if (giving) {
      num = give_ids.length;
      for (int i = 0; i < num; ++i) {
        if (give_count[i] <= 0) {
          continue;
        }
        drawString("Given " + getItemNameId(give_ids[i]) + ": " + give_count[i], 25, y, 1, white);
        y += 15;
      }
    }
    drawString("Transfer rate: " + per_hour(items_traded, start_time) + "/h", 25, y, 1, white);
    y += 15;
  }

  @Override
  public void onPrivateMessage(String msg, String name, boolean pmod, boolean jmod) {
    super.onPrivateMessage(msg, name, pmod, jmod);
    if (msg.equals("Stop")) {
      if (this.name.equals(name)) {
        stop_now = true;
        System.out.println("Got stop signal from: " + this.name);
      } else {
        System.out.println("Warning - Ignored stop signal from wrong person: " + name);
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("OK")) {
      try {
        String[] array = tf_take.getText().trim().split(",");
        int array_sz = array.length;
        take_ids = new int[array_sz];
        for (int i = 0; i < array_sz; i++) {
          take_ids[i] = Integer.parseInt(array[i]);
        }
        take_count = new int[array_sz];
        itm_banked = new boolean[array_sz];
        taking = true;
        System.out.println("Taking: " + Arrays.toString(take_ids));
      } catch (Throwable t) {
        System.out.println("We are not taking anything.");
        take_ids = new int[0];
        taking = false;
      }
      try {
        String[] array = tf_give.getText().trim().split(",");
        int array_sz = array.length;
        give_ids = new int[array_sz];
        for (int i = 0; i < array_sz; i++) {
          give_ids[i] = Integer.parseInt(array[i]);
        }
        give_count = new int[array_sz];
        itm_offered = new boolean[array_sz];
        giving = true;
        System.out.println("Giving: " + Arrays.toString(give_ids));
      } catch (Throwable t) {
        System.out.println("We are not giving anything.");
        give_ids = new int[0];
        giving = false;
      }
      name = tf_name.getText().trim();
      System.out.println("Trading: " + name);

      if (cb_loc.getState()) {
        loc = true;
        System.out.println("Using Bank instead of Shantay Chest");
      }
    }
    frame.setVisible(false);
  }
}
