package scripting.idlescript;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import controller.Controller;
import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import models.entities.SkillId;
import orsc.ORSCharacter;

public class MonkOfZamorak extends IdleScript {
  private final Controller c = Main.getController();
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.COMBAT, Category.MELEE, Category.PRAYER},
          "Nugs",
          "Fights Monks of Zamorak using TALK instead of ATTACK to avoid stat curse.\n\n"
              + "Banks at Falador East or Edgeville for food resupply.\n"
              + "Supports configurable food, eat HP threshold, and bone burying.\n\n"
              + "    Chat commands:\n"
              + "        ::bones   - Toggle bone burying\n"
              + "        ::attack  - Switch to Accurate\n"
              + "        ::strength - Switch to Aggressive\n"
              + "        ::defense  - Switch to Defensive\n"
              + "        ::controlled - Switch to Controlled");

  // --- NPC IDs ---
  private static final int MONK_LVL19_ID = 140;
  private static final int MONK_LVL29_ID = 139;

  // --- Common Food Item IDs (OpenRSC) ---
  private static final int LOBSTER_ID = 373;
  private static final int SWORDFISH_ID = 370;
  private static final int SHARK_ID = 546;
  private static final int TROUT_ID = 335;
  private static final int SALMON_ID = 357;
  private static final int TUNA_ID = 367;

  // --- Bone IDs ---
  private final int[] bones = {20, 413, 604, 814};

  // --- Chaos Temple area (north of Falador / Goblin Village) ---
  // NOTE: Verify coordinates in-game with F5 debug overlay.
  private static final int TEMPLE_X = 324;
  private static final int TEMPLE_Y = 437;
  private static final int TEMPLE_RADIUS = 15;

  // --- Walking waypoints: Falador Bank -> Chaos Temple ---
  // NOTE: Verify these coords in-game. Adjust if character gets stuck.
  private static final int[][] FALADOR_TO_TEMPLE = {
    {328, 552}, // Falador East Bank
    {326, 540},
    {324, 528},
    {320, 516},
    {316, 504},
    {316, 492},
    {318, 480},
    {320, 468},
    {322, 456},
    {324, 444},
    {324, 437}, // Chaos Temple
  };

  // --- Walking waypoints: Edgeville Bank -> Chaos Temple ---
  private static final int[][] EDGE_TO_TEMPLE = {
    {215, 450}, // Edgeville Bank
    {220, 450},
    {232, 450},
    {244, 448},
    {256, 448},
    {268, 446},
    {280, 444},
    {292, 442},
    {304, 440},
    {316, 438},
    {324, 437}, // Chaos Temple
  };

  // --- Script state ---
  private String status = "Setting up...";
  private boolean guiSetup = false;
  private boolean scriptStarted = false;
  private boolean bankAtFalador = true;
  private int foodId = LOBSTER_ID;
  private int eatAtHp = 15;
  private boolean fightLvl19 = true;
  private boolean fightLvl29 = true;
  private int foodToWithdraw = 25;
  private boolean buryBones = true;
  private int fightMode = 0; // 0=Controlled, 1=Aggressive, 2=Accurate, 3=Defensive
  private int[] npcIds = {};

  // --- Tracking ---
  private int killCount = 0;
  private int bankTrips = 0;
  private int bonesBuried = 0;

  // --- GUI ---
  private final JFrame frame = new JFrame("Monk of Zamorak: " + c.getPlayerName());

  // =====================================================
  // ENTRY POINT - matches AIOFighter pattern
  // =====================================================

  public int start(String[] parameters) {
    if (!guiSetup) {
      showGUI();
      guiSetup = true;
    }

    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      scriptStart();
    }
    c.stop();
    return 1000;
  }

  // =====================================================
  // MAIN SCRIPT LOOP
  // =====================================================

  private void scriptStart() {
    paintBuilder.start(4, 18, 182);

    // Build NPC ID array from checkboxes
    if (!(!fightLvl19 || !fightLvl29)) {
      npcIds = new int[] {MONK_LVL19_ID, MONK_LVL29_ID};
    } else if (fightLvl19) {
      npcIds = new int[] {MONK_LVL19_ID};
    } else if (fightLvl29) {
      npcIds = new int[] {MONK_LVL29_ID};
    }

    c.displayMessage(
        "@gre@Bank: "
            + (bankAtFalador ? "Falador" : "Edgeville")
            + " | Food: "
            + c.getItemName(foodId)
            + " | Eat at: "
            + eatAtHp
            + " HP");

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      while (!c.isLoggedIn() && c.isAutoLogin()) c.sleep(1280);
      c.sleep(618); // wait 1 tick

      // Ensure correct fight mode
      if (c.getFightMode() != fightMode) {
        status = "Changing fight mode...";
        c.setFightMode(fightMode);
      }

      // 1. Check if we need to eat
      if (eatIfNeeded()) continue;

      // 2. Bury bones if we have them and not in combat
      if (buryBones && !c.isInCombat()) {
        buryBones();
      }

      // 3. Check if we need to bank (out of food and not in combat)
      if (needsBank() && !c.isInCombat()) {
        bankForFood();
        continue;
      }

      // 4. If in combat, wait it out (eat if needed)
      if (c.isInCombat()) {
        status = "Fighting Monk of Zamorak...";
        continue;
      }

      // 5. Loot bones on the ground
      if (buryBones && !c.isInCombat()) {
        if (lootBones()) continue;
      }

      // 6. Make sure we're at the Chaos Temple
      if (!isNearTemple()) {
        walkToTemple();
        continue;
      }

      // 7. Find and TALK TO a monk (not attack!)
      talkToMonk();
    }
  }

  // =====================================================
  // COMBAT - Talk to Monk (avoids curse)
  // =====================================================

  private void talkToMonk() {
    if (c.isInCombat()) return;

    ORSCharacter npc = c.getNearestNpcByIds(npcIds, false);

    if (npc != null) {
      status = "Talking to Monk (avoids curse)...";
      c.talkToNpc(npc.serverIndex);
      c.sleep(1500);

      // Wait for dialogue -> combat initiation
      // The monk says something threatening then attacks
      c.sleep(2000);

      if (c.isInCombat()) {
        status = "Fighting Monk of Zamorak...";
      }
    } else {
      status = "Waiting for monks to respawn...";
      c.sleep(2000);
    }
  }

  // =====================================================
  // EATING - matches AIOFighter pattern using getFoodIds()
  // =====================================================

  private boolean eatIfNeeded() {
    if (c.getCurrentStat(c.getStatId("Hits")) <= eatAtHp) {
      status = "Eating food...";
      c.walkTo(c.currentX(), c.currentY(), 0, true, true);

      boolean ate =
          Arrays.stream(c.getFoodIds())
              .anyMatch(
                  f -> {
                    if (c.getUnnotedInventoryItemCount(f) < 1) return false;
                    c.itemCommand(f);
                    c.sleep(640);
                    return true;
                  });

      if (!ate) {
        // Try our specific food ID as fallback
        if (c.getInventoryItemCount(foodId) > 0) {
          c.itemCommand(foodId);
          c.sleep(640);
          return true;
        }
        // If not in combat, let bank logic handle it
        if (!c.isInCombat()) {
          status = "Out of food, heading to bank...";
          return false;
        }
        c.log("We ran out of food in combat! Logging out.", "red");
        c.setAutoLogin(false);
        c.logout();
      }
      return true;
    }
    return false;
  }

  // =====================================================
  // BONE HANDLING - matches AIOFighter pattern
  // =====================================================

  private boolean lootBones() {
    for (int boneId : bones) {
      int[] lootCoord = c.getNearestItemById(boneId);
      if (lootCoord != null && isNearTemple()) {
        status = "Picking up bones...";
        c.pickupItem(lootCoord[0], lootCoord[1], boneId, true, false);
        c.sleep(618);
        return true;
      }
    }
    return false;
  }

  private void buryBones() {
    for (int boneId : bones) {
      while (c.getInventoryItemCount(boneId) > 0 && !c.isInCombat()) {
        status = "Burying bones...";
        c.itemCommand(boneId);
        c.sleep(640);
      }
    }
  }

  // =====================================================
  // BANKING
  // =====================================================

  private boolean needsBank() {
    // Check all food IDs the client knows about
    boolean hasAnyFood =
        Arrays.stream(c.getFoodIds()).anyMatch(f -> c.getUnnotedInventoryItemCount(f) > 0);
    if (hasAnyFood) return false;
    // Also check our specific configured food
    return c.getInventoryItemCount(foodId) == 0;
  }

  private void bankForFood() {
    status = "Out of food! Walking to bank...";

    // Leave combat first if needed
    if (c.isInCombat()) {
      leaveCombat();
    }

    // Walk to bank
    walkToBank();

    // Open bank
    status = "Opening bank...";
    c.openBank();
    c.sleep(2000);

    if (!c.isInBank()) {
      c.openBank();
      c.sleep(2000);
    }

    if (c.isInBank()) {
      // Deposit bones and other junk
      for (int boneId : bones) {
        if (c.getInventoryItemCount(boneId) > 0) {
          c.depositItem(boneId, c.getInventoryItemCount(boneId));
          c.sleep(400);
        }
      }

      // Withdraw food
      int foodInBank = c.getBankItemCount(foodId);
      if (foodInBank <= 0) {
        status = "OUT OF FOOD IN BANK! Stopping.";
        c.log("No more food in bank. Script stopped.", "red");
        c.displayMessage("@red@No more food in bank! Script stopping.");
        c.closeBank();
        c.setAutoLogin(false);
        c.logout();
        c.stop();
        return;
      }

      int toWithdraw = Math.min(foodToWithdraw, foodInBank);
      c.withdrawItem(foodId, toWithdraw);
      c.sleep(600);

      c.closeBank();
      c.sleep(400);

      bankTrips++;
      status = "Bank trip #" + bankTrips + " complete!";
      c.displayMessage("@gre@Bank trip #" + bankTrips + " complete. Walking to temple...");

      // Walk back to temple
      walkToTemple();
    } else {
      status = "Failed to open bank. Retrying...";
    }
  }

  private void leaveCombat() {
    for (int i = 0; i < 20; i++) {
      if (c.isInCombat()) {
        status = "Leaving combat...";
        c.walkToAsync(c.currentX(), c.currentY(), 1);
        c.sleep(640);
      } else {
        break;
      }
    }
  }

  // =====================================================
  // WALKING / PATHING
  // =====================================================

  private boolean isNearTemple() {
    return c.distance(c.currentX(), c.currentY(), TEMPLE_X, TEMPLE_Y) <= TEMPLE_RADIUS;
  }

  private void walkToTemple() {
    status = "Walking to Chaos Temple...";
    int[][] path = bankAtFalador ? FALADOR_TO_TEMPLE : EDGE_TO_TEMPLE;
    walkPath(path);
  }

  private void walkToBank() {
    status = "Walking to bank...";
    int[][] path = bankAtFalador ? FALADOR_TO_TEMPLE : EDGE_TO_TEMPLE;
    walkPathReverse(path);
  }

  private void walkPath(int[][] path) {
    for (int[] waypoint : path) {
      if (!c.isRunning()) return;
      c.walkTo(waypoint[0], waypoint[1], 0, true, true);
      c.sleep(800);
      int timeout = 0;
      while (c.distance(c.currentX(), c.currentY(), waypoint[0], waypoint[1]) > 3 && timeout < 20) {
        c.sleep(500);
        timeout++;
      }
    }
  }

  private void walkPathReverse(int[][] path) {
    for (int i = path.length - 1; i >= 0; i--) {
      if (!c.isRunning()) return;
      c.walkTo(path[i][0], path[i][1], 0, true, true);
      c.sleep(800);
      int timeout = 0;
      while (c.distance(c.currentX(), c.currentY(), path[i][0], path[i][1]) > 3 && timeout < 20) {
        c.sleep(500);
        timeout++;
      }
    }
  }

  // =====================================================
  // MESSAGE HANDLING
  // =====================================================

  @Override
  public void questMessageInterrupt(String message) {
    if (message != null) {
      if (message.contains("defeated") || message.contains("killed")) {
        killCount++;
      }
      if (message.contains("bury")) {
        bonesBuried++;
      }
    }
  }

  @Override
  public void chatCommandInterrupt(String commandText) {
    String str = commandText.replace(" ", "").toLowerCase();

    if (str.contains("bones")) {
      buryBones = !buryBones;
      c.displayMessage(
          String.format(
              "@or1@Toggled @red@bones@or1@, turned %s bone burying!", buryBones ? "on" : "off"));
    } else if (str.contains("attack")) {
      c.displayMessage("@red@Switching to Accurate (Attack XP)!");
      fightMode = 2;
    } else if (str.contains("strength")) {
      c.displayMessage("@red@Switching to Aggressive (Strength XP)!");
      fightMode = 1;
    } else if (str.contains("defense")) {
      c.displayMessage("@red@Switching to Defensive (Defense XP)!");
      fightMode = 3;
    } else if (str.contains("controlled")) {
      c.displayMessage("@red@Switching to Controlled!");
      fightMode = 0;
    }
  }

  // =====================================================
  // GUI
  // =====================================================

  private void showGUI() {
    scriptStarted = false;

    JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Fight Mode
    panel.add(new JLabel("Fight Mode:"));
    JComboBox<String> modeCombo =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    panel.add(modeCombo);

    // Bank selection
    panel.add(new JLabel("Bank Location:"));
    JComboBox<String> bankCombo = new JComboBox<>(new String[] {"Falador East", "Edgeville"});
    panel.add(bankCombo);

    // Food selection
    panel.add(new JLabel("Food Type:"));
    JComboBox<String> foodCombo =
        new JComboBox<>(new String[] {"Lobster", "Swordfish", "Shark", "Trout", "Salmon", "Tuna"});
    panel.add(foodCombo);

    // Custom food ID
    panel.add(new JLabel("Custom Food ID (0 = use above):"));
    JTextField customFoodField = new JTextField("0");
    panel.add(customFoodField);

    // Eat at HP
    panel.add(new JLabel("Eat at HP:"));
    JTextField eatAtField =
        new JTextField(String.valueOf(c.getCurrentStat(c.getStatId("Hits")) / 2));
    panel.add(eatAtField);

    // Food per trip
    panel.add(new JLabel("Food per trip (max 30):"));
    JTextField foodAmountField = new JTextField("25");
    panel.add(foodAmountField);

    // Monk levels
    panel.add(new JLabel("Fight Level 19 Monks:"));
    JCheckBox lvl19Check = new JCheckBox("", true);
    panel.add(lvl19Check);

    panel.add(new JLabel("Fight Level 29 Monks:"));
    JCheckBox lvl29Check = new JCheckBox("", true);
    panel.add(lvl29Check);

    // Bury bones
    panel.add(new JLabel("Loot and bury bones?"));
    JCheckBox buryBonesCheck = new JCheckBox("", true);
    panel.add(buryBonesCheck);

    // Warning
    JLabel infoLabel =
        new JLabel("<html><b>NOTE:</b> Uses TALK not ATTACK to avoid stat curse!</html>");
    infoLabel.setForeground(Color.RED);
    panel.add(infoLabel);

    // Start button
    JButton startBtn = new JButton("Start Script");
    panel.add(startBtn);

    // Theming to match IdleRSC dark theme
    frame.getContentPane().setBackground(Main.primaryBG);
    frame.getContentPane().setForeground(Main.primaryFG);
    panel.setBackground(Main.primaryBG);

    for (Component comp : panel.getComponents()) {
      if (comp instanceof JLabel) {
        comp.setForeground(Main.primaryFG);
      } else if (comp instanceof JCheckBox) {
        comp.setForeground(Main.primaryFG);
        comp.setBackground(Main.primaryBG);
      } else if (comp instanceof JTextField) {
        comp.setForeground(Main.primaryFG);
        comp.setBackground(Main.primaryBG.brighter().brighter());
      } else if (comp instanceof JComboBox) {
        comp.setForeground(Main.primaryFG);
        comp.setBackground(Main.primaryBG.brighter().brighter());
      }
    }
    startBtn.setForeground(Main.secondaryFG);
    startBtn.setBackground(Main.secondaryBG);

    startBtn.addActionListener(
        e -> {
          // Fight mode
          fightMode = modeCombo.getSelectedIndex();

          // Bank
          bankAtFalador = bankCombo.getSelectedIndex() == 0;

          // Food
          int customFood;
          try {
            customFood = Integer.parseInt(customFoodField.getText().trim());
          } catch (NumberFormatException ex) {
            throw new RuntimeException(ex);
          }

          if (customFood > 0) foodId = customFood;
          else {
            switch (foodCombo.getSelectedIndex()) {
              case 1:
                foodId = SWORDFISH_ID;
                break;
              case 2:
                foodId = SHARK_ID;
                break;
              case 3:
                foodId = TROUT_ID;
                break;
              case 4:
                foodId = SALMON_ID;
                break;
              case 5:
                foodId = TUNA_ID;
                break;
              default:
                foodId = LOBSTER_ID;
                break;
            }
          }

          // Eat at HP
          try {
            eatAtHp = Integer.parseInt(eatAtField.getText().trim());
            eatAtHp = Math.max(1, eatAtHp);
          } catch (NumberFormatException ex) {
            eatAtHp = c.getCurrentStat(c.getStatId("Hits")) / 2;
          }

          // Food per trip
          try {
            foodToWithdraw = Integer.parseInt(foodAmountField.getText().trim());
            foodToWithdraw = Math.max(1, Math.min(30, foodToWithdraw));
          } catch (NumberFormatException ex) {
            foodToWithdraw = 25;
          }

          // Monk levels
          fightLvl19 = lvl19Check.isSelected();
          fightLvl29 = lvl29Check.isSelected();
          buryBones = buryBonesCheck.isSelected();

          if (!fightLvl19 && !fightLvl29) {
            JOptionPane.showMessageDialog(frame, "Select at least one monk level to fight!");
            return;
          }

          c.displayMessage("@red@Monk of Zamorak by Nugs. Let's train!");
          frame.setVisible(false);
          frame.dispose();
          scriptStarted = true;
        });

    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(Main.rscFrame);
    frame.setVisible(true);
    frame.requestFocus();

    // Wait for GUI - matches AIOFighter pattern
    while (!scriptStarted && c.isRunning() && frame.isVisible()) c.sleep(640);
  }

  // =====================================================
  // PAINT - uses paintBuilder from IdleScript
  // =====================================================

  @Override
  public void paintInterrupt() {
    if (c != null) {
      int yellow = 0xF1FA8C;
      int red = 0xFF5555;
      int white = 0xF8F8F2;
      int purple = 0xBD93F9;
      int green = 0x50FA7B;

      paintBuilder.setBorderColor(purple);
      paintBuilder.setBackgroundColor(0x282A36, 255);

      paintBuilder.setTitleMultipleColor(
          new String[] {"Monk", "of", "Zamorak"},
          new int[] {red, white, yellow},
          new int[] {
            9, c.getStringWidth("Monk", 6) + 6, c.getStringWidth("of", 6) + 6,
          },
          4);

      paintBuilder.addRow(rowBuilder.centeredSingleStringRow("by Nugs", white, 1));

      paintBuilder.addRow(rowBuilder.centeredSingleStringRow(status, yellow, 1));

      paintBuilder.addRow(rowBuilder.centeredSingleStringRow(paintBuilder.stringRunTime, white, 1));

      // HP bar
      int currHp = c.getCurrentStat(SkillId.HITS.getId());
      int maxHp = c.getBaseStat(SkillId.HITS.getId());

      paintBuilder.addRow(
          rowBuilder.progressBarRow(
              currHp,
              maxHp,
              0x282A36,
              (currHp > maxHp / 2 ? 0x85d589 : currHp > maxHp / 3 ? 0xc9d389 : 0xbc7181),
              purple,
              8,
              paintBuilder.getWidth() - 16,
              14,
              true,
              true,
              "Hits",
              red));

      paintBuilder.addSpacerRow(4);

      // Kill count
      paintBuilder.addRow(
          rowBuilder.multipleStringRow(
              new String[] {
                "Kills:", String.valueOf(killCount), paintBuilder.stringAmountPerHour(killCount)
              },
              new int[] {white, green, yellow},
              new int[] {4, 68, 52},
              1));

      // Bank trips
      paintBuilder.addRow(
          rowBuilder.multipleStringRow(
              new String[] {"Bank Trips:", String.valueOf(bankTrips)},
              new int[] {white, green},
              new int[] {4, 68},
              1));

      // Bones buried
      if (buryBones) {
        paintBuilder.addRow(
            rowBuilder.multipleStringRow(
                new String[] {
                  "Bones:",
                  String.valueOf(bonesBuried),
                  paintBuilder.stringAmountPerHour(bonesBuried)
                },
                new int[] {white, green, yellow},
                new int[] {4, 68, 52},
                1));
      }

      // Food remaining
      int foodLeft = c.getInventoryItemCount(foodId);
      paintBuilder.addRow(
          rowBuilder.multipleStringRow(
              new String[] {"Food:", String.valueOf(foodLeft)},
              new int[] {white, foodLeft > 5 ? green : red},
              new int[] {4, 68},
              1));

      paintBuilder.draw();
    }
  }
}
