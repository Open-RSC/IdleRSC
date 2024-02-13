package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;

/**
 * <b>Hobgoblin Miner</b>
 *
 * <p>Mines Addy/Mith/Coal in Hobgoblin Mine and banks in Edge! (some pk/death protection). <br>
 * This bot supports the "autostart" parameter to automatiically start the bot without gui.<br>
 *
 * <p>Start in Edge bank with Armor and Pickaxe. <br>
 * Sharks in bank REQUIRED.<br>
 * Teleport if Pkers Attack Option.<br>
 * 31 Magic, Laws, Airs, and Earths required for Escape Tele.<br>
 * Unselected, bot WALKS to Edge when Attacked.<br>
 * Selected, bot walks to 19 wildy and teleports.<br>
 * Return to Hobs Mine after Escaping Option.<br>
 * Unselected, bot will log out after escaping Pkers.<br>
 * Selected, bot will grab more food and return.<br>
 * This bot supports the \"autostart\" parameter.<br>
 * Defaults to Teleport Off, Return On.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_HobsMiner extends K_kailaScript {
  private static String isMining = "none";
  private static boolean teleportOut = false;
  private static boolean returnEscape = true;
  private static final int[] currentOre = {0, 0};
  private static final int[] addyIDs = {
    108, 231, 109
  }; // 108,231,109 (addy) 106,107 (mith) 110,111 (coal)  98 empty
  private static final int[] mithIDs = {106, 107};
  private static final int[] coalIDs = {110, 111};
  private static final int[] loot = {
    // loot RDT hob drops
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    1092, // rune spear
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond
    // loot common armor if another bot dies
    1318, // ring of wealth
    402, // rune leg
    400, // rune chain
    399, // rune med
    403, // rune sq
    404, // rune kite
    112, // rune full helm
    1262, // rune pic
    315, // Emerald Amulet of protection
    317, // Diamond Amulet of power
    522, // dragonstone ammy
    // loot "some" hobs drops
    38, // death rune
    619, // blood rune
    42, // laws
    40, // nats
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
  };

  private boolean adamantiteAvailable() {
    return c.getNearestObjectByIds(addyIDs) != null;
  }

  private boolean mithrilAvailable() {
    return c.getNearestObjectByIds(mithIDs) != null;
  }

  private boolean coalAvailable() {
    return c.getNearestObjectByIds(coalIDs) != null;
  }

  private boolean rockEmpty() {
    if (currentOre[0] != 0) {
      return c.getObjectAtCoord(currentOre[0], currentOre[1]) == 98;
    } else {
      return true;
    }
  }

  private void startSequence() {
    c.displayMessage("@red@Hobs Miner- By Kaila");
    c.displayMessage("@red@Start in Edge bank with Armor and pickaxe");
    c.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
    c.displayMessage("@red@31 Magic Required for escape tele");
    if (c.isInBank()) c.closeBank();
    if (c.currentY() > 340) {
      bank();
      eat();
      bankToHobs();
      eat();
      c.sleep(1380);
    }
    if (c.currentY() > 270 && c.currentY() < 341) {
      bankToHobs();
      eat();
      c.sleep(1380);
    }
    c.setBatchBarsOn();
  }
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    centerX = 225;
    centerY = 251;
    centerDistance = 30;
    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.displayMessage("Auto-starting, teleport false, return escape true", 0);
        System.out.println("Auto-starting, teleport false, return escape true");
        teleportOut = false;
        returnEscape = true;
        guiSetup = true;
        scriptStarted = true;
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      startSequence();
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      eat();
      leaveCombat();
      if (c.getInventoryItemCount(546) == 0) {
        c.setStatus("@red@We've ran out of Food! Teleporting Away!.");
        hobsToTwenty();
        c.sleep(100);
        teleportLumbridge();
        c.sleep(308);
        c.walkTo(120, 644);
        c.atObject(119, 642);
        c.walkTo(217, 447);
        c.sleep(618);
        bank();
        bankToHobs();
      }
      if (c.getInventoryItemCount() == 30) {
        goToBank();
      }
      if (c.getInventoryItemCount() < 30) {
        eat();
        leaveCombat();
        lootItems(true, loot);
        if (rockEmpty() || !c.isBatching()) {
          isMining = "none";
          currentOre[0] = 0;
          currentOre[1] = 0;
        }
        if (c.isBatching() && !c.getNeedToMove()) {
          if (Objects.equals(isMining, "mithril")) {
            if (adamantiteAvailable()) {
              mine("adamantite");
            }
          }
          if (Objects.equals(isMining, "coal")) {
            if (adamantiteAvailable()) {
              mine("adamantite");
            } else if (mithrilAvailable()) {
              mine("mithril");
            }
          }
          c.sleep(1280);
        }
        leaveCombat();
        c.setStatus("@yel@Mining..");
        if (!c.isBatching() && Objects.equals(isMining, "none") && rockEmpty()) {
          if (adamantiteAvailable()) {
            mine("adamantite");
          } else if (mithrilAvailable()) {
            mine("mithril");
          } else if (coalAvailable()) {
            mine("coal");
          }
          c.sleep(1280);
        }
      }
    }
  }
  /**
   * Mines a specific type of ore based on the provided input.
   *
   * @param i The type of ore to mine (adamantite, mithril, or coal).
   */
  private void mine(String i) {
    if (Objects.equals(i, "adamantite")) {
      int[] oreCoords = c.getNearestObjectByIds(addyIDs);
      if (oreCoords != null) {
        isMining = "adamantite";
        c.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    } else if (Objects.equals(i, "mithril")) {
      int[] oreCoords = c.getNearestObjectByIds(mithIDs);
      if (oreCoords != null) {
        isMining = "mithril";
        c.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    } else if (Objects.equals(i, "coal")) {
      int[] oreCoords = c.getNearestObjectByIds(coalIDs);
      if (oreCoords != null) {
        isMining = "coal";
        c.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    }
    c.sleep(1920);
  }
  /** The primary banking loop */
  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalCoal = totalCoal + c.getInventoryItemCount(155);
      totalMith = totalMith + c.getInventoryItemCount(153);
      totalAddy = totalAddy + c.getInventoryItemCount(154);
      totalSap = totalSap + c.getInventoryItemCount(160);
      totalEme = totalEme + c.getInventoryItemCount(159);
      totalRub = totalRub + c.getInventoryItemCount(158);
      totalDia = totalDia + c.getInventoryItemCount(157);
      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != 546
            && itemId != 156
            && itemId != 1263
            && itemId != 1262) { // won't bank sharks, rune/bronze pick, or sleeping bags
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1280); // increased sleep here to prevent double banking
      coalInBank = c.getBankItemCount(155);
      mithInBank = c.getBankItemCount(153);
      addyInBank = c.getBankItemCount(154);
      if (teleportOut) {
        withdrawItem(ItemId.AIR_RUNE.getId(), 3);
        withdrawItem(ItemId.EARTH_RUNE.getId(), 1);
        withdrawItem(ItemId.LAW_RUNE.getId(), 1);
      }
      withdrawItem(ItemId.SHARK.getId(), 1);
      bankItemCheck(ItemId.SHARK.getId(), 10);
      c.closeBank();
    }
    if (teleportOut) {
      inventoryItemCheck(airId, 3);
      inventoryItemCheck(earthId, 1);
      inventoryItemCheck(lawId, 1);
    }
  }
  /** Executes the eat logic. */
  private void eat() {
    int eatLvl = c.getBaseStat(c.getStatId("Hits")) - 20;
    if (c.getCurrentStat(c.getStatId("Hits")) < eatLvl) {
      leaveCombat();
      c.sleep(200);
      c.setStatus("@red@Eating..");
      boolean ate = false;
      for (int id : c.getFoodIds()) {
        if (c.getInventoryItemCount(id) > 0) {
          c.itemCommand(id);
          c.sleep(700);
          ate = true;
        }
      }
      if (!ate) { // only activates if hp goes to -20 again THAT trip, will bank and get new shark
        // usually
        c.setStatus("@red@We've ran out of Food at Hobs! Running Away!.");
        isMining = "none";
        currentOre[0] = 0;
        currentOre[1] = 0;
        c.setStatus("@yel@Banking..");
        hobsToTwenty();
        if (!teleportOut
            || c.getInventoryItemCount(42) < 1
            || c.getInventoryItemCount(33) < 3
            || c.getInventoryItemCount(34) < 1) { // or no earths/airs/laws
          twentyToBank();
        }
        if (teleportOut) {
          teleportLumbridge();
          c.walkTo(120, 644);
          c.atObject(119, 642);
          c.walkTo(217, 447);
          c.sleep(308);
        }
        if (!returnEscape) {
          endSession();
        }
        if (returnEscape) {
          bank();
          bankToHobs();
          c.sleep(618);
        }
      }
    }
  }
  /** Logic forgoing to the bank and perform banking operations. */
  private void goToBank() {
    isMining = "none";
    currentOre[0] = 0;
    currentOre[1] = 0;
    c.setStatus("@yel@Banking..");
    hobsToTwenty();
    twentyToBank();
    bank();
    bankToHobs();
    c.sleep(618);
  }
  /**
   * Sets the status to indicate that the character is walking to 19 wildy. Then walks the character
   * to the specified coordinates: (221, 314). Increments the totalTrips variable by 1. Sets the
   * status to indicate that the character is done walking to 19.
   */
  private void hobsToTwenty() {
    c.setStatus("@gre@Walking to 19 wildy..");
    c.walkTo(221, 262);
    c.walkTo(221, 283);
    c.walkTo(221, 301);
    c.walkTo(221, 314);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking to 19..");
  }
  /** The function that walks the character to the bank. */
  private void twentyToBank() {
    c.setStatus("@gre@Walking to Bank..");
    eat();
    c.walkTo(221, 321);
    c.walkTo(222, 341);
    c.walkTo(222, 361);
    c.walkTo(222, 381);
    c.walkTo(222, 401);
    c.walkTo(215, 410);
    c.walkTo(215, 420);
    c.walkTo(220, 425);
    c.walkTo(220, 445);
    c.walkTo(217, 448);
    c.setStatus("@gre@Done Walking..");
  }
  /** Walks from the bank to the Hobs Mine. */
  private void bankToHobs() {
    c.setStatus("@gre@Walking to Hobs Mine..");
    c.walkTo(218, 447);
    c.walkTo(220, 443);
    c.walkTo(220, 433);
    c.walkTo(220, 422);
    c.walkTo(215, 417);
    c.walkTo(215, 410);
    c.walkTo(215, 401);
    c.walkTo(215, 395);
    eat();
    c.walkTo(222, 388);
    c.walkTo(222, 381);
    c.walkTo(222, 361);
    c.walkTo(222, 341);
    c.walkTo(221, 321);
    c.walkTo(221, 314);
    c.walkTo(221, 301);
    c.walkTo(221, 283);
    c.walkTo(221, 262);
    c.setStatus("@gre@Done Walking..");
  }
  /** Sets up the graphical user interface (GUI) for the application. */
  private void setupGUI() {
    JLabel header = new JLabel("Hobs Miner - By Kaila");
    JLabel label1 = new JLabel("Start in Edge bank with Armor and Pickaxe");
    JLabel label2 = new JLabel("Sharks in bank REQUIRED");
    JLabel label3 = new JLabel("31 Magic, Laws, Airs, and Earths required for Escape Tele");
    JLabel label8 = new JLabel("This bot supports the \"autostart\" parameter");
    JCheckBox teleportCheckbox = new JCheckBox("Teleport if Pkers Attack?", false);
    JCheckBox escapeCheckbox = new JCheckBox("Return to Hobs Mine after Escaping?", true);
    JButton startScriptButton = new JButton("Start");
    startScriptButton.addActionListener(
        e -> {
          teleportOut = teleportCheckbox.isSelected();
          returnEscape = escapeCheckbox.isSelected();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });
    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(teleportCheckbox);
    scriptFrame.add(label3);
    scriptFrame.add(escapeCheckbox);
    scriptFrame.add(label8);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }
  /**
   * Overrides paintInterrupt. Displays various mining statistics such as coal mined, mithril mined,
   * addy mined, sapphires, emeralds, rubies, diamonds, total trips, and runtime.
   */
  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int coalSuccessPerHr = 0;
      int mithSuccessPerHr = 0;
      int addySuccessPerHr = 0;
      int sapSuccessPerHr = 0;
      int emeSuccessPerHr = 0;
      int rubSuccessPerHr = 0;
      int diaSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        coalSuccessPerHr = (int) (totalCoal * scale);
        mithSuccessPerHr = (int) (totalMith * scale);
        addySuccessPerHr = (int) (totalAddy * scale);
        sapSuccessPerHr = (int) (totalSap * scale);
        emeSuccessPerHr = (int) (totalEme * scale);
        rubSuccessPerHr = (int) (totalRub * scale);
        diaSuccessPerHr = (int) (totalDia * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Hobs Miner @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Coal Mined: @gre@"
              + totalCoal
              + "@yel@ (@whi@"
              + String.format("%,d", coalSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Coal in Bank: @gre@"
              + coalInBank,
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Mith Mined: @gre@"
              + totalMith
              + "@yel@ (@whi@"
              + String.format("%,d", mithSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Mith in Bank: @gre@"
              + mithInBank,
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Addy Mined: @gre@"
              + totalAddy
              + "@yel@ (@whi@"
              + String.format("%,d", addySuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Addy in Bank: @gre@"
              + addyInBank,
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Sapphires: @gre@"
              + totalSap
              + "@yel@ (@whi@"
              + String.format("%,d", sapSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Emeralds: @gre@"
              + totalEme
              + "@yel@ (@whi@"
              + String.format("%,d", emeSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Rubys: @gre@"
              + totalRub
              + "@yel@ (@whi@"
              + String.format("%,d", rubSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Diamonds: @gre@"
              + totalDia
              + "@yel@ (@whi@"
              + String.format("%,d", diaSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Runtime: "
              + runTime,
          x,
          y + (14 * 6),
          0xFFFFFF,
          1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 6), 0xFFFFFF, 1);
    }
  }
}
