package scripting.idlescript;

import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * <b>No Bank Superheat</b>
 *
 * <p>Mines Iron in Hobgoblin Mine and banks in Edge! (some pk/death protection). <br>
 * This bot supports the "autostart" parameter to automatiically start the bot without gui.<br>
 * Start in Varrock East bank or near Mine, with a pickaxe and bass key.<br>
 * Sharks in bank REQUIRED.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_NoBank_Superheat extends K_kailaScript {
  private static String isMining = "none";
  private static int castsRemaining = 0;
  private static int spellsCasted = 0;
  private static final int IRON_ORE = 151, IRON_BAR = 170, SUPERHEAT_ID = 21, IRON_PLATE = 8;
  private static final int[] currentOre = {0, 0};
  private static final int[] ironIDs = {102, 103};

  private boolean rockEmpty() {
    if (currentOre[0] != 0) {
      return c.getObjectAtCoord(currentOre[0], currentOre[1]) == 98;
    } else {
      return true;
    }
  }

  private void startSequence() {
    c.displayMessage("@red@No Bank Iron Superheat- By Kaila");
    c.displayMessage("@red@Start in Khazard Mine with nats,hammer,fire staff,pickaxe");
    if (c.isInBank()) c.closeBank();
    if (!orsc.Config.C_BATCH_PROGRESS_BAR) c.toggleBatchBars();
  }

  public int start(String[] parameters) {
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
      dropGems();
      if (c.getInventoryItemCount(IRON_ORE) > 0) {
        c.castSpellOnInventoryItem(
            SUPERHEAT_ID,
            c.getInventoryItemSlotIndex(IRON_ORE)); // c.getSpellIdFromName("Superheat Item") ]21]
        c.sleep(2 * GAME_TICK);
      }
      if (c.getInventoryItemCount() < 30) {
        if (rockEmpty() || !c.isBatching()) {
          isMining = "none";
          currentOre[0] = 0;
          currentOre[1] = 0;
        }
      }
      if (c.getInventoryItemCount() == 30) {
        goToAnvil();
        smithing();
        anvilToOre();
      }
    }
  }

  private void smithing() {
    // c.sleepHandler(98, true);
    c.setStatus("@gre@Casting Superheat");
    while (c.getInventoryItemCount(IRON_ORE) > 0) {
      c.castSpellOnInventoryItem(
          SUPERHEAT_ID,
          c.getInventoryItemSlotIndex(IRON_ORE)); // c.getSpellIdFromName("Superheat Item") ]21]
      c.sleep(2 * GAME_TICK);
    }
    castsRemaining = c.getInventoryItemCount(NATURE_RUNE);
    totalBars = totalBars + c.getInventoryItemCount(IRON_BAR);
    c.setStatus("@gre@Smithing");
    if (c.getInventoryItemCount(IRON_BAR) > 0) {
      c.useItemIdOnObject(568, 699, IRON_BAR);
      c.sleep(2 * GAME_TICK);
      c.optionAnswer(1);
      c.sleep(GAME_TICK);
      c.optionAnswer(2);
      c.sleep(GAME_TICK);
      c.optionAnswer(2);
      c.sleep(GAME_TICK);
      if (!c.isAuthentic()) {
        c.optionAnswer(3);
        c.sleep(3000); // was 650
      }
      waitForBatching();
    }
    totalPlates = totalPlates + c.getInventoryItemCount(IRON_PLATE);
    if (c.getInventoryItemCount(IRON_BAR) > 0) { // make daggers if bars left over
      c.useItemIdOnObject(568, 699, IRON_BAR);
      c.sleep(2 * GAME_TICK);
      c.optionAnswer(0);
      c.sleep(GAME_TICK);
      c.optionAnswer(0);
      c.sleep(GAME_TICK);
      c.optionAnswer(3);
      c.sleep(GAME_TICK);
      waitForBatching();
    }
    if (c.getInventoryItemCount(IRON_PLATE) > 0) {
      c.dropItem(c.getInventoryItemSlotIndex(IRON_PLATE), c.getInventoryItemCount(IRON_PLATE));
      c.sleep(GAME_TICK);
      waitForBatching();
    }
  }

  private void dropGems() {
    dropItemAmount(UNCUT_SAPP, -1, true);
    dropItemAmount(UNCUT_EMER, -1, true);
    dropItemAmount(UNCUT_RUBY, -1, true);
    dropItemAmount(UNCUT_DIA, -1, true);
  }

  private void mine(String i) {
    if (Objects.equals(i, "iron")) {
      int[] oreCoords = c.getNearestObjectByIds(ironIDs);
      if (oreCoords != null) {
        isMining = "iron";
        c.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    }
    c.sleep(1920);
  }

  private void goToAnvil() {
    isMining = "none";
    currentOre[0] = 0;
    currentOre[1] = 0;
    c.setStatus("@yel@Smelting..");
    oreToAnvil();
  }

  private void anvilToOre() {
    c.setStatus("@gre@Walking to Ore Rocks..");
    c.walkTo(569, 697);
    c.walkTo(573, 697);
    c.walkTo(572, 706);
    c.setStatus("@gre@Done Walking..");
  }

  private void oreToAnvil() {
    c.setStatus("@gre@Walking to Anvil..");
    c.walkTo(569, 697);
    c.walkTo(569, 699);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }
  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("NoBank Iron Superheat - By Kaila");
    JLabel label1 = new JLabel("Start in Port Khazard Mine");
    JLabel label2 = new JLabel("Required: Nats, hammer, fire staff, pickaxe");
    JLabel label3 = new JLabel("This bot supports the \"autostart\" parameter");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
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
    scriptFrame.add(label3);
    scriptFrame.add(startScriptButton);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("successfully")) spellsCasted++;
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int plateSuccessPerHr = 0;
      int barSuccessPerHr = 0;
      int spellsPerHr = 0;
      try {
        long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        plateSuccessPerHr = (int) (totalPlates * scale);
        barSuccessPerHr = (int) (totalBars * scale);
        spellsPerHr = (int) (spellsCasted * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@NoBank Superheat@mag@~ by Kaila", x, 12, 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Bars Used: @gre@" + totalBars, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Bars Per Hr: @gre@" + String.format("%,d", barSuccessPerHr) + "@yel@/@whi@hr",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString("@whi@Platebodies Made: @gre@" + totalPlates, x, y + (14 * 3), 0xFFFFFF, 1);
      c.drawString(
          "@whi@Platebodies Per Hr: @gre@"
              + String.format("%,d", plateSuccessPerHr)
              + "@yel@/@whi@hr",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Spells Cast: @gre@"
              + String.format("%,d", spellsCasted)
              + " @yel@(@whi@"
              + String.format("%,d", spellsPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Time Remaining: " + c.timeToCompletion(totalBars, castsRemaining, startTime),
          x,
          y + (14 * 6),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 7), 0xFFFFFF, 1);
      c.drawString("@whi@__________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
    }
  }
}
