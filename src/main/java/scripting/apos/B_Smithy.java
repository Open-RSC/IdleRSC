package scripting.apos;

import compatibility.apos.Script;
import java.util.Date;
import javax.swing.*;

public class B_Smithy extends Script {

  private final int VROOOOM =
      10; // Might need increasing to ~200. 10 is stupid fast.. But menus don't appear, and that's
  // fun.
  private final int HAMMER = 168;
  private final int SLEEPING_BAG = 1263;
  private final int ANVIL = 50;
  private final int[] REQUIRED_ITEMS = {HAMMER, SLEEPING_BAG};
  private final int[] SMITHY = {147, 511};
  private final int[] BANK = {150, 505};
  private final int[] BANK_DOOR = {64, 150, 507};
  private final int[] SMITHY_DOOR = {2, 146, 510};
  private int items;
  private int current_key;
  private int[] inventory;
  private int[] initial_xp;
  private long time;
  private long smithing;
  private long banking;
  private long[] screenshot;
  private String filename;
  private Item item;

  public B_Smithy(String e) {
    // super(e);
  }

  @Override
  public void init(String s) {
    items = 0;
    smithing = -1L;
    banking = -1L;
    inventory = new int[MAX_INV_SIZE];
    initial_xp = new int[SKILL.length];
    screenshot = new long[4];

    int[] bar_ids = {169, 170, 171, 173, 174, 408};
    String[] bar_names = {"Bronze", "Iron", "Steel", "Mithril", "Adamant", "Runite"};
    int option =
        JOptionPane.showOptionDialog(
            null,
            "What type of bars would you like to use?",
            "Blood's Smithy",
            JOptionPane.OK_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            bar_names,
            bar_names[5]);
    Item[] items = new Item[19];
    if (option < 0 || option > items.length - 1) {
      stopScript();
    }
    items[0] = new Item(bar_ids[option], bar_names[option] + " Dagger", 1, 1, 0, 0);
    items[1] = new Item(bar_ids[option], bar_names[option] + " Axe", 1, 1, 0, 3, 0);
    items[2] = new Item(bar_ids[option], bar_names[option] + " Mace", 1, 1, 0, 4);
    items[3] = new Item(bar_ids[option], bar_names[option] + " Medium Helmet", 1, 1, 1, 0, 0);
    items[4] = new Item(bar_ids[option], bar_names[option] + " Short Sword", 1, 1, 0, 2, 0);
    items[5] = new Item(bar_ids[option], bar_names[option] + " Dart Tips", 1, 7, 2, 1);
    items[6] = new Item(bar_ids[option], bar_names[option] + " Arrow Heads", 1, 10, 2, 0);
    items[7] = new Item(bar_ids[option], bar_names[option] + " Scimitar", 2, 1, 0, 2, 2);
    items[8] = new Item(bar_ids[option], bar_names[option] + " Long Sword", 2, 1, 0, 2, 1);
    items[9] = new Item(bar_ids[option], bar_names[option] + " Large Helmet", 2, 1, 1, 0, 1);
    items[10] = new Item(bar_ids[option], bar_names[option] + " Throwing Knife", 1, 2, 0, 1);
    items[11] = new Item(bar_ids[option], bar_names[option] + " Square Shield", 2, 1, 1, 1, 0);
    items[12] = new Item(bar_ids[option], bar_names[option] + " Battle Axe", 3, 1, 0, 3, 1);
    items[13] = new Item(bar_ids[option], bar_names[option] + " Chain Body", 3, 1, 1, 2, 0);
    items[14] = new Item(bar_ids[option], bar_names[option] + " Kite Shield", 3, 1, 1, 1, 1);
    items[15] = new Item(bar_ids[option], bar_names[option] + " 2h Sword", 3, 1, 0, 2, 3);
    items[16] = new Item(bar_ids[option], bar_names[option] + " Plate Skirt", 3, 1, 1, 2, 3);
    items[17] = new Item(bar_ids[option], bar_names[option] + " Plate Legs", 3, 1, 1, 2, 2);
    items[18] = new Item(bar_ids[option], bar_names[option] + " Plate Body", 5, 1, 1, 2, 1);
    JPanel panel = new JPanel();
    panel.add(new JLabel("What item would you like to make?"));
    DefaultComboBoxModel<String> cb = new DefaultComboBoxModel<>();
    for (Item item : items) {
      cb.addElement(item.getItemName());
    }
    JComboBox<String> comboBox = new JComboBox<>(cb);
    panel.add(comboBox);
    option =
        JOptionPane.showConfirmDialog(
            null,
            panel,
            "Item selection",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.OK_OPTION) {
      item = items[comboBox.getSelectedIndex()];
    } else {
      stopScript();
    }
  }

  @Override
  public int main() {
    if (initial_xp[3] == 0) {
      for (int i = 0; i < SKILL.length; i++) {
        initial_xp[i] = getXpForLevel(i);
      }
      time = System.currentTimeMillis();
      return 500;
    }
    if (screenshot[0] != -1L) {
      return screenshot(System.currentTimeMillis());
    }
    if (getFatigue() > 95) {
      System.out.println("Sleeping... " + new Date());
      useSleepingBag();
      return random(800, 1200);
    }
    if (isWalking()) {
      return VROOOOM;
    }
    if (banking != -1L) {
      if (System.currentTimeMillis() > banking + 15000L) {
        banking = -1L;
        return 0;
      }
      if (isQuestMenu()) {
        answer(0);
      }
      if (isBanking()) {
        banking = -1L;
      }
      if (banking != -1L) {
        return VROOOOM;
      }
    }
    if (getInventoryCount(item.getBarID()) >= item.getBarsRequired()) {
      if (isReachable(SMITHY[0], SMITHY[1])) {
        if (isQuestMenu()) {
          answer(item.getKeys()[current_key]);
          current_key++;
          smithing = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() > smithing + 5000L) {
          smithing = -1L;
        }
        if (smithing != -1L) {
          return VROOOOM;
        }
        useItemOnObject(item.getBarID(), ANVIL);
        smithing = System.currentTimeMillis();
        return VROOOOM;
      } else {
        return openDoors();
      }
    } else {
      if (isReachable(BANK[0], BANK[1])) {
        if (isBanking()) {
          if (bankCount(item.getBarID()) < 1) {
            System.out.println("Out of supplies");
            stop();
          }
          if (!hasInventoryItem(1263)) {
            if (hasBankItem(1263)) {
              withdraw(1263, 1);
              return 2000;
            }
            System.out.println("No sleeping bag in inventory or bank, stopping");
            stop();
          }
          if (!hasInventoryItem(168)) {
            if (hasBankItem(168)) {
              withdraw(168, 1);
              return 2000;
            }
            System.out.println("No hammer in inventory or bank, stopping");
            stop();
          }
          pollInventory();
          if (getEmptySlots() != MAX_INV_SIZE - REQUIRED_ITEMS.length) {
            for (int item : inventory) {
              if (item != 0) {
                deposit(item, getInventoryCount(item));
              }
            }
            return 1000;
          }
          withdraw(item.getBarID(), getEmptySlots());
          return 1000;
        }
        int[] banker = getNpcByIdNotTalk(BANKERS);
        if (banker[0] != -1) {
          talkToNpc(banker[0]);
          banking = System.currentTimeMillis();
          return random(800, 1200);
        }
      } else {
        return openDoors();
      }
    }
    return 5000;
  }

  public void pollInventory() {
    for (int i = 0, item; i < getInventoryCount(); i++) {
      item = getInventoryId(i);
      if (isRequiredItem(item)) {
        inventory[i] = 0;
        continue;
      }
      inventory[i] = item;
    }
  }

  public boolean isRequiredItem(int item) {
    for (int i : REQUIRED_ITEMS) {
      if (i == item) {
        return true;
      }
    }
    return false;
  }

  public int openDoors() {
    int bank_door = getObjectIdFromCoords(BANK_DOOR[1], BANK_DOOR[2]);
    boolean special;
    special =
        getY() < 507
            ? isReachable(BANK_DOOR[1], BANK_DOOR[2] - 1)
            : isReachable(BANK_DOOR[1], BANK_DOOR[2]);
    if (bank_door == BANK_DOOR[0] && special) {
      atObject(BANK_DOOR[1], BANK_DOOR[2]);
      return random(800, 1200);
    }
    special =
        getY() < 510
            ? isReachable(SMITHY_DOOR[1], SMITHY_DOOR[2] - 1)
            : isReachable(SMITHY_DOOR[1], SMITHY_DOOR[2]);
    int smithy_door = getWallObjectIdFromCoords(SMITHY_DOOR[1], SMITHY_DOOR[2]);
    if (smithy_door == SMITHY_DOOR[0] && special) {
      atWallObject(SMITHY_DOOR[1], SMITHY_DOOR[2]);
    }
    return random(800, 1200);
  }

  @Override
  public void paint() {
    int x = 12;
    int y = 50;
    int[] xp = getXpStatistics(13);
    drawString("Blood's Smithy", x - 4, y - 17, 4, 0x00b500);
    drawString("Smithing XP Gained: " + xp[2] + " (" + xp[3] + " XP/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString(
        item.getItemName() + " made: " + items + " (" + perHour(items) + "/h)", x, y, 1, 0xFFFFFF);
    y += 15;
    drawString("Runtime: " + getRunTime(), x, y, 1, 0xFFFFFF);
    drawVLine(8, 37, y + 3 - 37, 0xFFFFFF);
    drawHLine(8, y + 3, 183, 0xFFFFFF);
  }

  @Override
  public void onServerMessage(String s) {
    if (s.contains("You hammer the metal")) {
      items += item.getItemsMade();
      smithing = -1L;
      return;
    }
    if (s.equals("What would you like to make?")) {
      current_key = 0;
      return;
    }
    if (s.contains("Banker is busy at the moment")) {
      banking = -1L;
      return;
    }
    if (s.contains("just advanced")) {
      screenshot[0] = System.currentTimeMillis();
      filename = new Date().getTime() + " - " + s;
    }
  }

  @Override
  public void onChatMessage(String msg, String name, boolean pmod, boolean jmod) {
    System.out.println(name + ": " + msg);
    if (jmod) {
      stop();
    }
  }

  @Override
  public void onPrivateMessage(String msg, String name, boolean pmod, boolean jmod) {
    System.out.println(name + ": " + msg + " (PM)");
    if (jmod) {
      stop();
    }
  }

  private int perHour(int total) {
    long time = ((System.currentTimeMillis() - this.time) / 1000L);
    if (time < 1L) {
      time = 1L;
    }
    return ((int) ((total * 60L * 60L) / time));
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

  private void stop() {
    setAutoLogin(false);
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

  private int screenshot(long now) {
    if (now > screenshot[0] + 3000L) {
      screenshot[0] = -1L;
      return 50;
    }
    if (screenshot[1] == 0) {
      if (isPaintOverlay()) { // Get state of Paint
        screenshot[2] = 1L; // If it's on, remember this
        setPaintOverlay(false); // If it's on, turn it off for the screenshot
      } else {
        screenshot[2] = -1L; // If it's off, remember this
      }
      if (isRendering()) { // Get state of Graphics
        screenshot[3] = 1L; // If it's on, remember this
      } else {
        screenshot[3] = -1L; // If it's off, remember this
        setRendering(true); // If it's off, turn it on for the screenshot
      }
      screenshot[1] = 1;
      return 50;
    }
    if (isSkipLines()) { // to be uncommented 2.4
      setSkipLines(false);
    }
    if (now < screenshot[0] + 1000L) { // time for paint() to redraw...
      return 50;
    }
    takeScreenshot(filename); // Take screenshot
    screenshot[0] = -1L;
    screenshot[1] = 0;
    if (screenshot[2] == 1L) { // If Paint was enabled before the screenshot, turn it back on
      // PaintListener.toggle();
    }
    if (screenshot[2] == 1L) { // If Paint was enabled before the screenshot, turn it back on
      setPaintOverlay(true);
    }
    if (screenshot[3] != 1L) { // If the Graphics were off before the screenshot, turn them back off
      setRendering(false);
    }
    return 50;
  }

  private static class Item {
    private final int bar_id;
    private final int bars_required;
    private final int items_made;
    private final int[] keys;
    private final String name;

    public Item(int bar_id, String name, int bars_required, int items_made, int... keys) {
      this.bar_id = bar_id;
      this.name = name;
      this.bars_required = bars_required;
      this.items_made = items_made;
      this.keys = keys;
    }

    public int getBarID() {
      return this.bar_id;
    }

    public int getBarsRequired() {
      return this.bars_required;
    }

    public int getItemsMade() {
      return this.items_made;
    }

    public String getItemName() {
      return this.name;
    }

    public int[] getKeys() {
      return this.keys;
    }
  }
}
