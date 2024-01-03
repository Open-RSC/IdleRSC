package scripting.apos;

import compatibility.apos.Script;

public class Rope extends Script {

  private final int[][] ROPES = {{598, 3582}, {596, 3584}};
  private int swung;
  private int sleep_at = 100;
  private int[] initial_xp;
  private long time;
  private String filename;
  private final long[] screenshot = new long[4];

  public Rope(String e) {
    //       super(e);
  }

  @Override
  public void init(String s) {
    System.out.println("Let's get swingin'");
    swung = 0;
    initial_xp = new int[SKILL.length];
  }

  @Override
  public int main() {
    if (initial_xp[0] == 0) {
      for (int i = 0; i < SKILL.length; i++) {
        initial_xp[i] = getXpForLevel(i);
      }
      time = System.currentTimeMillis();
    }
    if (getFatigue() >= sleep_at) {
      useSleepingBag();
      return 2000;
    }
    if (screenshot[0] != -1L) {
      return screenshot(System.currentTimeMillis());
    }
    int rope;
    if (getY() < 3583) {
      rope = 0;
    } else {
      rope = 1;
    }
    if (!isWalking()) {
      atObject(ROPES[rope][0], ROPES[rope][1]);
      return 500;
    }
    return 10;
  }

  @Override
  public void paint() {
    int x = 10;
    int y = 30;
    drawString("Blood's Swinger", x, y, 4, 0x00b500);
    y += 12;
    drawString("Swung " + swung + " times.", x, y, 1, 0xFFFFFF);
    y += 12;
    for (int i = 0; i < SKILL.length; ++i) {
      int[] xp = getXpStatistics(i);
      if (xp[2] > 100) {
        drawString(SKILL[i] + " XP Gained: " + xp[2] + " (" + xp[3] + " XP/h)", x, y, 1, 0xFFFFFF);
        y += 12;
      }
    }
    drawString("Runtime: " + getRunTime(), x, y, 1, 0xFFFFFF);
  }

  private int[] getXpStatistics(int skill) {
    long time = ((System.currentTimeMillis() - this.time) / 1000L);
    if (time < 1L) {
      time = 1L;
    }
    int start_xp = initial_xp[skill];
    int current_xp = getXpForLevel(skill);
    int[] intArray = new int[4];
    intArray[0] = current_xp;
    intArray[1] = start_xp;
    intArray[2] = intArray[0] - intArray[1];
    intArray[3] = (int) ((((current_xp - start_xp) * 60L) * 60L) / time);
    return intArray;
  }

  public void stop() {
    //       AutoLogin.setAutoLogin(false);
    logout();
    stopScript();
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

  @Override
  public void onChatMessage(String msg, String name, boolean pmod, boolean jmod) {
    super.onChatMessage(msg, name, pmod, jmod);
    if (jmod) {
      stop();
    }
  }

  @Override
  public void onPrivateMessage(String msg, String name, boolean pmod, boolean jmod) {
    super.onPrivateMessage(msg, name, pmod, jmod);
    if (jmod) {
      stop();
    }
  }

  @Override
  public void onServerMessage(String s) {
    if (s.contains("skillfully swing")) {
      swung++;
      return;
    }
    if (s.contains("just advanced")) {
      screenshot[0] = System.currentTimeMillis();
      filename = screenshot[0] + " - " + s;
      return;
    }
    if (s.contains("too tired")) {
      sleep_at = getFatigue() - 1;
    }
  }

  private int screenshot(long now) {
    if (now > screenshot[0] + 3000L) {
      screenshot[0] = -1L;
      return 50;
    }
    if (screenshot[1] == 0) {
      if (isPaintOverlay()) {
        screenshot[2] = 1L;
        setPaintOverlay(false);
      } else {
        screenshot[2] = -1L;
      }
      if (isRendering()) {
        screenshot[3] = 1L;
      } else {
        screenshot[3] = -1L;
        setRendering(true);
      }
      screenshot[1] = 1;
      return 50;
    }
    if (isSkipLines()) {
      setSkipLines(false);
    }
    if (now < screenshot[0] + 1000L) {
      return 50;
    }
    takeScreenshot(filename);
    screenshot[0] = -1L;
    screenshot[1] = 0;
    if (screenshot[2] == 1L) {
      setPaintOverlay(true);
    }
    if (screenshot[3] != 1L) {
      setRendering(false);
    }
    return 50;
  }
}
