package scripting.apos;

import compatibility.apos.Script;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class ZanarisCooker extends Script implements ActionListener {

  private class Fish {
    String Name;
    int RawId;
    int CookedId;

    private Fish(String name, int rawId, int cookedId) {
      Name = name;
      RawId = rawId;
      CookedId = cookedId;
    }
  };

  Fish[] allFish;
  Fish fish;
  private int[] burnedIds = new int[] {353, 360, 365, 368, 371, 374, 547};
  private long startTime = -1L, waitForBank = -1L;
  private int startXp, curXp, remaining, cookedCount = 0, burnedCount = 0;
  private final DecimalFormat iformat = new DecimalFormat("#,##0");
  private final DecimalFormat twoDecimals = new DecimalFormat("##.00");
  private Frame frame;
  private Choice ch_Fish;

  public ZanarisCooker(String ex) {
    //        super(ex);
  }

  @Override
  public void init(String params) {
    allFish =
        new Fish[] {
          new Fish("Tuna", 366, 367),
          new Fish("Lobster", 372, 373),
          new Fish("Swordfish", 369, 370),
          new Fish("Shark", 545, 546)
        };
    if (frame == null) {

      Panel pInput = new Panel(new GridLayout(0, 1, 0, 2));

      ch_Fish = new Choice();
      for (Fish f : allFish) {
        ch_Fish.add(f.Name);
      }
      pInput.add(ch_Fish);

      Button button;
      Panel pButtons = new Panel();
      button = new Button("OK");
      button.addActionListener(this);
      pButtons.add(button);
      button = new Button("Cancel");
      button.addActionListener(this);
      pButtons.add(button);

      frame = new Frame(getClass().getSimpleName());
      //            frame.addWindowListener(
      //                    new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
      //            );
      //            frame.setIconImages(Constants.ICONS);
      frame.add(pInput, BorderLayout.NORTH);
      frame.add(pButtons, BorderLayout.SOUTH);
      frame.setResizable(false);
      frame.pack();
    }
    frame.setLocationRelativeTo(null);
    frame.toFront();
    frame.requestFocusInWindow();
    frame.setVisible(true);
  }

  @Override
  public int main() {

    if (startTime == -1L) {
      startTime = System.currentTimeMillis();
      startXp = getXpForLevel(7);
    }
    curXp = getXpForLevel(7);

    if (getFatigue() > 95) {
      useSleepingBag();
      return 1000;
    }

    if (waitForBank != -1L && !isBanking()) {
      if (System.currentTimeMillis() - waitForBank > 5000) {
        waitForBank = -1L;
      }
      return 50;
    }

    if (isQuestMenu()) {
      answer(0);
      waitForBank = System.currentTimeMillis();
      return 600;
    }

    if (getInventoryCount(burnedIds) > 0) {
      dropItem(getInventoryIndex(burnedIds));
      return 1000;
    }

    if (isBanking()) {
      waitForBank = -1L;
      remaining = getInventoryCount(fish.RawId) + bankCount(fish.RawId);
      if (getInventoryCount(fish.CookedId) > 0) {
        deposit(fish.CookedId, getInventoryCount(fish.CookedId));
        return 1200;
      }
      if (bankCount(fish.RawId) == 0) {
        stopScript();
        System.out.println("Out of fish to cook");
        return 0;
      }
      if (getInventoryCount(fish.RawId) > 0) {
        closeBank();
        return 600;
      }
      withdraw(fish.RawId, Math.min(MAX_INV_SIZE - getInventoryCount(), bankCount(fish.RawId)));
      return 1200;
    }

    if (getInventoryCount(fish.RawId) == 0) {
      int[] bankers = getNpcByIdNotTalk(BANKERS);
      if (bankers[0] != -1) {
        if (!isReachable(bankers[1], bankers[2])) {
          int[] door = getWallObjectById(2);
          if (door[0] != -1) {
            atWallObject(door[1], door[2]);
            return 1000;
          }
          int[] gate = getObjectById(57);
          if (gate[0] != -1) {
            atObject(gate[1], gate[2]);
            return 1000;
          }
        }
        talkToNpc(bankers[0]);
        return 2000;
      }
    }

    int[] range = getObjectById(11);
    if (range[0] != -1) {
      if (!isReachable(range[1], range[2])) {
        int[] door = getWallObjectById(2);
        if (door[0] != -1) {
          atWallObject(door[1], door[2]);
          return 1000;
        }
        int[] gate = getObjectById(57);
        if (gate[0] != -1) {
          atObject(gate[1], gate[2]);
          return 1000;
        }
      }
      useItemOnObject(fish.RawId, 11);
      return 800;
    }

    return 600;
  }

  private String per_hour(long count, long time) {
    double amount, secs;
    if (count == 0) return "0";
    amount = count * 60.0 * 60.0;
    secs = (System.currentTimeMillis() - time) / 1000.0;
    return iformat.format(amount / secs);
  }

  private int per_hour_int(long count, long time) {
    double amount, secs;
    if (count == 0) return 0;
    secs = (System.currentTimeMillis() - time) / 1000.0;
    amount = count * 60D * 60D;
    return (int) Math.round(amount / secs);
  }

  @Override
  public void paint() {
    final int white = 0xFFFFFF;
    final int cyan = 0x00FFFF;
    int y = 25;
    drawString("Zanaris Cooker", 25, y, 1, cyan);
    y += 15;
    drawString("Runtime: " + get_time_since(startTime), 25, y, 1, white);
    y += 15;
    drawString(
        "XP: " + (curXp - startXp) + " (" + per_hour(curXp - startXp, startTime) + "/h)",
        25,
        y,
        1,
        white);
    y += 15;
    drawString(
        "Cooked: " + cookedCount + " (" + per_hour(cookedCount, startTime) + "/h)",
        25,
        y,
        1,
        white);
    y += 15;
    drawString(
        "Burned: " + burnedCount + " (" + per_hour(burnedCount, startTime) + "/h)",
        25,
        y,
        1,
        white);
    y += 15;
    if (remaining > 0) {
      if (per_hour_int(cookedCount + burnedCount, startTime) == 0) {
        drawString("Remaining: " + remaining, 25, y, 1, white);
      } else {

        drawString(
            "Remaining: "
                + remaining
                + " ("
                + twoDecimals.format(
                    (double) remaining / per_hour_int(cookedCount + burnedCount, startTime))
                + " hours)",
            25,
            y,
            1,
            white);
      }
    }
  }

  // ripped from somewhere.. one of the S_ scripts
  private static String get_time_since(long t) {
    long millis = (System.currentTimeMillis() - t) / 1000;
    long second = millis % 60;
    long minute = (millis / 60) % 60;
    long hour = (millis / (60 * 60)) % 24;
    long day = (millis / (60 * 60 * 24));
    if (day > 0L) {
      return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
    }
    if (hour > 0L) {
      return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
    }
    if (minute > 0L) {
      return String.format("%02d minutes, %02d seconds", minute, second);
    }
    return String.format("%02d seconds", second);
  }

  @Override
  public void onServerMessage(String str) {
    str = str.toLowerCase();
    if (str.contains("nicely cooked")) {
      remaining--;
      cookedCount++;
    } else if (str.contains("burn")) {
      remaining--;
      burnedCount++;
    }
  }

  @Override
  public void actionPerformed(ActionEvent aE) {
    if (aE.getActionCommand().equals("OK")) {
      for (Fish f : allFish) {
        if (f.Name.equalsIgnoreCase(ch_Fish.getSelectedItem())) {
          fish = f;
          break;
        }
      }
    }
    System.out.println("Cooking " + fish.Name);
    frame.setVisible(false);
  }
}
