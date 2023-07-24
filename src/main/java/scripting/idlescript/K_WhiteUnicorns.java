package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.*;
import orsc.ORSCharacter;

/**
 * Edge Dungeon Hobs (and Skeleton/Zombie) - by Kaila
 *
 * <p>
 *
 * <p>Options: Combat Style, Loot level Herbs, Reg pots, Alter Prayer Boost, Food Type, and Food
 * Withdraw Amount Selection, Chat Command Options, Full top-left GUI, regular atk/str pot option,
 * and Autostart.
 *
 * <p>- cannot support bone looting with this bot due to the shape of the dungeon
 *
 * <p>@Author - Kaila
 */
public final class K_WhiteUnicorns extends K_kailaScript {
  private static int totalUni = 0, inventUni = 0, uniInBank = 0;
  private static final int UNI_HORN = 466;

  public int start(String[] parameters) {
    centerX = 451;
    centerY = 470;
    centerDistance = 30;
    if (parameters[0].toLowerCase().startsWith("auto")) {
      foodId = 546;
      fightMode = 0;
      foodWithdrawAmount = 1;
      potUp = false;
      lootBones = true;
      buryBones = true;
      c.displayMessage("Got Autostart Parameter");
      c.log("Auto-Starting using 1 Shark, controlled, Loot Low Level, no pot up", "cya");
      c.log("Looting Bones, Banking bones", "cya");
      guiSetup = true;
      scriptStarted = true;
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      c.displayMessage("@red@Edge Skeletons ~ Kaila");
      c.displayMessage("@red@Start in Edge bank with Armor");

      if (c.isInBank()) {
        c.closeBank();
      }
      if (c.currentX() < 450) {
        bank();
        bankToHouse();
        c.sleep(1380);
      }
      whatIsFoodName();
      scriptStart();
    }

    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      boolean ate = eatFood();
      if (!ate) {
        c.setStatus("@red@We've ran out of Food! Running Away!.");
        houseToBank();
        bank();
        bankToHouse();
      }
      checkFightMode();
      if (potUp) {
        attackBoost(0, false);
        strengthBoost(0, false);
      }
      inventUni = c.getInventoryItemCount(466);
      if (c.getInventoryItemCount() < 30 && c.getInventoryItemCount(foodId) > 0 && !timeToBank) {
        if (!c.isInCombat()) {
          lootItem(false, UNI_HORN);
          if (buryBones) buryBones(false);
          ORSCharacter npc = c.getNearestNpcById(0, true);
          if (npc != null) {
            c.setStatus("@yel@Attacking..");
            c.attackNpc(npc.serverIndex);
            c.sleep(2000);
          } else {
            lootItem(false, UNI_HORN);
            if (lootBones) lootItem(false, BONES);
            c.sleep(100);
          }
        } else {
          c.sleep(640);
        }
      }
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(false, 1, EMPTY_VIAL);
        buryBonesToLoot(false);
      }
      if (c.getInventoryItemCount() == 30
          || c.getInventoryItemCount(foodId) == 0
          || timeToBank
          || timeToBankStay) {
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        houseToBank();
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          c.displayMessage(
              "@red@Click on Start Button Again@or1@, to resume the script where it left off (preserving statistics)");
          c.setStatus("@red@Stopping Script.");
          c.setAutoLogin(false);
          c.stop();
        }
        bankToHouse();
        c.sleep(618);
      } else {
        c.sleep(100);
      }
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      totalUni = totalUni + c.getInventoryItemCount(466);
      uniInBank = c.getBankItemCount(466);
      for (int itemId : c.getInventoryItemIds()) {
        c.depositItem(itemId, c.getInventoryItemCount(itemId));
      }
      c.sleep(1240); // Important, leave in
      if (potUp) {
        withdrawAttack(1);
        withdrawStrength(1);
      }
      withdrawFood(foodId, foodWithdrawAmount);
      bankItemCheck(foodId, 5);
      c.closeBank();
      c.sleep(1000);
    }
  }

  private void bankToHouse() {
    c.setStatus("@gre@Walking to Unicorns..");
    c.walkTo(440, 497);
    c.walkTo(444, 498);
    c.walkTo(445, 487);
    c.walkTo(445, 477);
    c.setStatus("@gre@Done Walking..");
  }

  private void houseToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(445, 477);
    c.walkTo(445, 487);
    c.walkTo(444, 498);
    c.walkTo(440, 497);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }
  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("White Unicorns ~ by Kaila");
    JLabel warning = new JLabel("This is inefficient and meant for HCIM");
    JLabel label1 = new JLabel("Start by Catherby bank or by Unicorns");
    JLabel label2 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label3 = new JLabel("::bank ::potup ::lootbones ::burybones");
    JLabel label4 = new JLabel("Styles ::attack :strength ::defense ::controlled");
    JLabel label5 = new JLabel("Param Format: \"auto\"");
    JCheckBox lootBonesCheckbox = new JCheckBox("Pickup Bones?", true);
    JCheckBox buryBonesCheckbox = new JCheckBox("Bury Bones?", true);
    JCheckBox potUpCheckbox = new JCheckBox("Use regular Atk/Str Pots?", false);
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    fightModeField.setSelectedIndex(0); // sets default to controlled
    foodField.setSelectedIndex(2); // sets default to sharks
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals(""))
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          lootBones = lootBonesCheckbox.isSelected();
          buryBones = buryBonesCheckbox.isSelected();
          foodId = foodIds[foodField.getSelectedIndex()];
          fightMode = fightModeField.getSelectedIndex();
          potUp = potUpCheckbox.isSelected();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(warning);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(lootBonesCheckbox);
    scriptFrame.add(buryBonesCheckbox);
    scriptFrame.add(potUpCheckbox);
    scriptFrame.add(fightModeLabel);
    scriptFrame.add(fightModeField);
    scriptFrame.add(foodLabel);
    scriptFrame.add(foodField);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(startScriptButton);
    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void chatCommandInterrupt(String commandText) { // ::bank ::lowlevel :potup ::prayer
    if (commandText.contains("bank")) {
      c.displayMessage("@or1@Got @red@bank@or1@ command! Going to the Bank!");
      timeToBank = true;
      c.sleep(100);
    } else if (commandText.contains("bankstay")) {
      c.displayMessage("@or1@Got @red@bankstay@or1@ command! Going to the Bank and Staying!");
      timeToBankStay = true;
      c.sleep(100);
    } else if (commandText.contains("lootbones")) {
      if (!lootBones) {
        c.displayMessage("@or1@Got toggle @red@lootbones@or1@, turning on bone looting!");
        lootBones = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning off bone looting!");
        lootBones = false;
      }
      c.sleep(100);
    } else if (commandText.contains("burybones")) {
      if (!buryBones) {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning on bone bury!");
        buryBones = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@buryBones@or1@, turning off bone bury!");
        buryBones = false;
      }
      c.sleep(100);
    } else if (commandText.contains("potup")) {
      if (!potUp) {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning on regular atk/str pots!");
        potUp = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning off regular atk/str pots!");
        potUp = false;
      }
      c.sleep(100);
    } else if (commandText.contains(
        "attack")) { // field is "Controlled", "Aggressive", "Accurate", "Defensive"}
      c.displayMessage("@red@Got Combat Style Command! - Attack Xp");
      c.displayMessage("@red@Switching to \"Accurate\" combat style!");
      fightMode = 2;
      c.sleep(100);
    } else if (commandText.contains("strength")) {
      c.displayMessage("@red@Got Combat Style Command! - Strength Xp");
      c.displayMessage("@red@Switching to \"Aggressive\" combat style!");
      fightMode = 1;
      c.sleep(100);
    } else if (commandText.contains("defense")) {
      c.displayMessage("@red@Got Combat Style Command! - Defense Xp");
      c.displayMessage("@red@Switching to \"Defensive\" combat style!");
      fightMode = 3;
      c.sleep(100);
    } else if (commandText.contains("controlled")) {
      c.displayMessage("@red@Got Combat Style Command! - Controlled Xp");
      c.displayMessage("@red@Switching to \"Controlled\" combat style!");
      fightMode = 0;
      c.sleep(100);
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You eat the")) {
      usedFood++;
    }
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("You dig a hole")) {
      usedBones++;
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {

      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      int TripSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) ((totalUni + inventUni) * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);

      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 21;
      c.drawString("@red@White Unicorns @mag@~ by Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString("@whi@Horns in Bank: @gre@" + uniInBank, x, y + 14, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Horns Picked: @gre@"
              + (totalUni + inventUni)
              + "@yel@ (@whi@"
              + String.format("%,d", successPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 4), 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 4), 0xFFFFFF, 1);
    }
  }
}
