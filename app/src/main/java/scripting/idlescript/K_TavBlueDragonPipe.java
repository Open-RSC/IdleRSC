package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.EquipSlotIndex;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Wildy Fire Giant Killer</b>
 *
 * <p>Start in Fally west with gear on, or in Dragon room! <br>
 * Uses Coleslaw agility pipe shortcut.<br>
 * 70 Agility required, for the shortcut.<br>
 * Sharks/Laws/Airs/Earths IN BANK REQUIRED.<br>
 * 31 Magic Required for escape tele.<br>
 * Adjustable Food Withdraw amount.<br>
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_TavBlueDragonPipe extends K_kailaScript {
  private int fightMode = 0;
  private int capeSwapId = 0;
  private boolean useDragonTwoHand = false;
  private boolean craftCapeTeleport = false;
  private int totalRdagger = 0;
  private static final int DRAGON_TWO_HAND = ItemId.DRAGON_2_HANDED_SWORD.getId();
  private static final int ANTI_DRAGON_SHIELD = ItemId.ANTI_DRAGON_BREATH_SHIELD.getId();
  private static final int CRAFT_CAPE = ItemId.CRAFTING_CAPE.getId();
  private static final int[] loot = {
    ItemId.UNID_RANARR_WEED.getId(),
    ItemId.UNID_IRIT.getId(),
    ItemId.UNID_AVANTOE.getId(),
    ItemId.UNID_KWUARM.getId(),
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
    ItemId.NATURE_RUNE.getId(),
    ItemId.LAW_RUNE.getId(),
    ItemId.FIRE_RUNE.getId(),
    ItemId.WATER_RUNE.getId(),
    ItemId.DRAGON_BONES.getId(),
    ItemId.RUNE_DAGGER.getId(),
    ItemId.ADAMANTITE_ORE.getId(),
    ItemId.UNCUT_SAPPHIRE.getId(),
    ItemId.UNCUT_EMERALD.getId(),
    ItemId.UNCUT_RUBY.getId(),
    ItemId.UNCUT_DIAMOND.getId(),
    ItemId.TOOTH_HALF_KEY.getId(),
    ItemId.LOOP_HALF_KEY.getId(),
    ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.getId(),
    ItemId.RUNE_SPEAR.getId(),
    ItemId.DRAGON_MEDIUM_HELMET.getId()
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    centerX = 361;
    centerY = 3353; // FURTHEST LOOT is 376, 3368, go 361, 3353  (15 tiles)
    centerDistance = 15;
    if (!parameters[0].equals("")) {
      try {
        foodWithdrawAmount = Integer.parseInt(parameters[0]);
        foodId = Integer.parseInt(parameters[1]);
        fightMode = Integer.parseInt(parameters[2]);
        potUp = Boolean.parseBoolean(parameters[3]);
      } catch (Exception e) {
        System.out.println("Could not parse parameters!");
        c.displayMessage("@red@Could not parse parameters!");
        c.stop();
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      c.displayMessage("@red@Tavelry Blue Dragons (Pipe) - By Kaila");
      c.displayMessage("@red@Start in Fally west with gear on, or in dragon room!");
      c.displayMessage("@red@Sharks, Law, Water, Air IN BANK REQUIRED");
      c.displayMessage("@red@70 Agility required, for the shortcut!");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 2800) {
        bank();
        BankToDragons();
        c.sleep(1380);
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }
  // Main Script section
  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (useDragonTwoHand && !c.isInCombat() && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
        c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
        c.sleep(GAME_TICK);
      } else if (useDragonTwoHand && c.isInCombat() && !c.isItemIdEquipped(DRAGON_TWO_HAND)) {
        c.equipItem(c.getInventoryItemSlotIndex(DRAGON_TWO_HAND));
        c.sleep(GAME_TICK);
      }
      if (!eatFood() || c.getInventoryItemCount(foodId) == 0 || timeToBank || timeToBankStay) {
        if (useDragonTwoHand && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD))
          c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
        c.setStatus("@yel@Banking..");
        timeToBank = false;
        DragonsToBank();
        bank();
        if (timeToBankStay) {
          timeToBankStay = false;
          c.displayMessage(
              "@red@Click on Start Button Again@or1@, to resume the script where it left off (preserving statistics)");
          c.setStatus("@red@Stopping Script.");
          endSession();
        }
        BankToDragons();
      }
      if (buryBones) buryBones(false);
      if (potUp) {
        superAttackBoost(2, false);
        superStrengthBoost(2, false);
      }
      checkFightMode(fightMode);
      lootItems(true, loot, ANTI_DRAGON_SHIELD, useDragonTwoHand);
      if (!c.isInCombat()) {
        ORSCharacter npc = c.getNearestNpcById(202, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking Dragons");
          c.attackNpc(npc.serverIndex);
          c.sleep(2 * GAME_TICK);
        } else {
          c.sleep(3 * GAME_TICK);
          if (!c.isInCombat() && (c.currentX() != 370 && c.currentY() != 3355)) {
            c.setStatus("@gre@No npc going to center.");
            lootItems(false, loot, ANTI_DRAGON_SHIELD, useDragonTwoHand);
            c.walkTo(370, 3355);
            c.setStatus("@gre@at center.");
          }
        }
      } else c.sleep(GAME_TICK);
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(false, 1, ItemId.EMPTY_VIAL.getId());
        if (buryBones) buryBonesToLoot(false);
        eatFoodToLoot(false);
      }
    }
  }

  private void walkToCenter() {
    if (!c.isInCombat() && (c.currentX() != 370 || c.currentY() != 3353)) {
      c.walkTo(370, 3353);
      c.sleep(1000);
    }
  }

  private void bank() {
    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(1200);
    if (c.isInBank()) {
      totalBones = totalBones + c.getInventoryItemCount(814);
      totalRdagger = totalRdagger + c.getInventoryItemCount(396);
      totalGems =
          totalGems
              + c.getInventoryItemCount(160)
              + c.getInventoryItemCount(159)
              + c.getInventoryItemCount(158)
              + c.getInventoryItemCount(157);
      totalHerb =
          totalHerb
              + c.getInventoryItemCount(438)
              + c.getInventoryItemCount(439)
              + c.getInventoryItemCount(440)
              + c.getInventoryItemCount(441)
              + c.getInventoryItemCount(442)
              + c.getInventoryItemCount(443);
      totalFire = totalFire + c.getInventoryItemCount(31);
      totalLaw = totalLaw + c.getInventoryItemCount(42);
      totalNat = totalNat + c.getInventoryItemCount(40);
      totalWater = totalWater + c.getInventoryItemCount(32);
      totalAddy = totalAddy + c.getInventoryItemCount(154);
      totalLoop = totalLoop + c.getInventoryItemCount(527);
      totalTooth = totalTooth + c.getInventoryItemCount(526);
      totalLeft = totalLeft + c.getInventoryItemCount(1277);
      totalSpear = totalSpear + c.getInventoryItemCount(1092);

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != ItemId.SUPER_ATTACK_POTION_1DOSE.getId()
            && itemId != ItemId.SUPER_ATTACK_POTION_2DOSE.getId()
            && itemId != ItemId.SUPER_STRENGTH_POTION_1DOSE.getId()
            && itemId != ItemId.SUPER_STRENGTH_POTION_2DOSE.getId()
            && itemId != ItemId.DRAGON_2_HANDED_SWORD.getId()
            && itemId != capeSwapId
            && itemId != CRAFT_CAPE) { // craft cape
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      bankBones = c.getBankItemCount(814);
      c.sleep(1240); // Important, leave in

      int dragonTwoHand = ItemId.DRAGON_2_HANDED_SWORD.getId();
      if (useDragonTwoHand && (c.getInventoryItemCount(dragonTwoHand) < 1))
        withdrawItem(dragonTwoHand, 1);
      if (craftCapeTeleport
          && (c.getInventoryItemCount(CRAFT_CAPE) < 1)
          && !c.isItemIdEquipped(CRAFT_CAPE)) {
        withdrawItem(CRAFT_CAPE, 1);
      }
      if (craftCapeTeleport && (c.getInventoryItemCount(CRAFT_CAPE) > 1))
        c.depositItem(CRAFT_CAPE, c.getInventoryItemCount(CRAFT_CAPE) - 1);
      if (!craftCapeTeleport) {
        withdrawItem(airId, 18);
        withdrawItem(lawId, 6);
        withdrawItem(waterId, 6);
      }
      if (potUp) {
        withdrawSuperAttack(1);
        withdrawSuperStrength(1);
      }
      withdrawFood(foodId, foodWithdrawAmount);
      bankItemCheck(foodId, 30);
      if (!craftCapeTeleport) {
        bankItemCheck(airId, 30);
        bankItemCheck(waterId, 10); // Falador teleport
        bankItemCheck(lawId, 10);
      }
      bankCheckAntiDragonShield();
      c.closeBank();
      if (!c.isItemIdEquipped(capeSwapId)) c.equipItem(c.getInventoryItemSlotIndex(capeSwapId));
    }
    if (!craftCapeTeleport) {
      inventoryItemCheck(airId, 18);
      inventoryItemCheck(waterId, 6);
      inventoryItemCheck(lawId, 6);
    }
  }
  // PATHING private voids
  private void BankToDragons() {
    c.setStatus("@gre@Walking to Tav Gate..");
    if (craftCapeTeleport && (c.getInventoryItemCount(CRAFT_CAPE) != 0)) {
      teleportCraftCape();
      c.walkTo(347, 588);
      c.walkTo(347, 586);
      c.walkTo(343, 581);
      tavGateSouthToNorth();
      c.walkTo(343, 570);
      c.walkTo(343, 560);
      c.walkTo(343, 550);
      c.walkTo(350, 542);
      c.walkTo(356, 536);
      c.walkTo(363, 536);
      c.walkTo(368, 531);
      c.walkTo(375, 524);
      c.walkTo(375, 521);
      c.walkTo(376, 521);
    } else {
      controller.walkTo(328, 553);
      // open bank door
      openDoorObjects(64, 327, 552);

      c.walkTo(327, 552);
      c.walkTo(324, 549);
      c.walkTo(324, 539);
      c.walkTo(324, 530);
      c.walkTo(317, 523);
      c.walkTo(317, 516);
      c.walkTo(327, 506);
      c.walkTo(337, 496);
      c.walkTo(337, 492);
      c.walkTo(341, 488);
      tavGateEastToWest();
      c.setStatus("@gre@Walking to Tav Dungeon Ladder..");
      c.walkTo(342, 493);
      c.walkTo(350, 501);
      c.walkTo(355, 506);
      c.walkTo(360, 511);
      c.walkTo(362, 513);
      c.walkTo(367, 514);
      c.walkTo(374, 521);
      c.walkTo(376, 521);
    }
    c.equipItem(c.getInventoryItemSlotIndex(420));
    c.sleep(320);
    c.atObject(376, 520);
    c.sleep(640);
    c.walkTo(375, 3352);
    if (useDragonTwoHand && !c.isItemIdEquipped(420)) {
      c.equipItem(c.getInventoryItemSlotIndex(420));
    }
    c.atObject(374, 3352);
    c.sleep(640);
    c.walkTo(372, 3352);
    c.sleep(320);
    c.setStatus("@gre@Done Walking..");
  }

  private void exitDragonPipe() {
    for (int i = 0; i < 200; i++) {
      if (c.currentX() < 374) {
        c.walkTo(372, 3352);
        c.atObject(373, 3352);
        c.sleep(1000);
      } else break;
    }
  }

  private void DragonsToBank() {
    c.setStatus("@gre@Walking to Bank.. going through pipe first");
    if (useDragonTwoHand && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
      c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
      c.sleep(2 * GAME_TICK);
    }
    exitDragonPipe();
    int CRAFTING_CAPE = ItemId.CRAFTING_CAPE.getId();
    if (craftCapeTeleport
        && c.isItemIdEquipped(CRAFTING_CAPE)
        && c.getInventoryItemCount(CRAFTING_CAPE) < 1) {
      c.unequipItem(EquipSlotIndex.CAPE.getId());
    }
    if (craftCapeTeleport && (c.getInventoryItemCount(CRAFT_CAPE) != 0)) {
      c.setStatus("@gre@Going to Bank. Casting craft cape teleport.");
      teleportCraftCape();
      c.walkTo(347, 600);
      craftGuildDoorEntering(capeSwapId);
      c.walkTo(347, 607);
      c.walkTo(346, 608);
      totalTrips = totalTrips + 1;
      c.sleep(GAME_TICK);
    } else {
      exitDragonPipe();
      teleportFalador();
      c.sleep(308);
      c.walkTo(327, 552);
      c.sleep(308);
    }
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Tavelry Blue Dragons (Pipe) - By Kaila");
    JLabel label1 = new JLabel("Start in Fally west with gear on, or in Dragon room!");
    JLabel label2 = new JLabel("Sharks, Law, Water, Air IN BANK required");
    JLabel label3 = new JLabel("70 Agility required, for the shortcut!");
    JLabel label4 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label5 = new JLabel("::bank ::bankstay ::burybones");
    JLabel label6 = new JLabel("Styles ::attack :strength ::defense ::controlled");
    JLabel blankLabel = new JLabel("     ");
    JCheckBox craftCapeCheckbox = new JCheckBox("99 Crafting Cape Teleport?", true);
    JLabel capeItemIdLabel = new JLabel("Cape ItemId to switch back too");
    JTextField capeItemIdField = new JTextField(String.valueOf(ItemId.ATTACK_CAPE.getId()));
    JCheckBox dragonTwoHandCheckbox = new JCheckBox("Swap to Dragon 2h Sword", true);
    JCheckBox buryBonesCheckbox = new JCheckBox("Bury Dragon Bones?", false);
    JCheckBox potUpCheckbox = new JCheckBox("Use Atk/Str Pots? (Super or reg)", false);
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    foodField.setSelectedIndex(2); // sets default to sharks
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(10));
    JButton startScriptButton = new JButton("Start");

    craftCapeCheckbox.addActionListener(
        e -> {
          capeItemIdLabel.setEnabled(craftCapeCheckbox.isSelected());
          capeItemIdField.setEnabled(craftCapeCheckbox.isSelected());
        });

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().isEmpty()) {
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          } else {
            foodWithdrawAmount = 22;
          }
          if (!capeItemIdField.getText().isEmpty()) {
            capeSwapId = Integer.parseInt(capeItemIdField.getText());
          }
          buryBones = buryBonesCheckbox.isSelected();
          fightMode = fightModeField.getSelectedIndex();
          foodId = foodIds[foodField.getSelectedIndex()];
          useDragonTwoHand = dragonTwoHandCheckbox.isSelected();
          craftCapeTeleport = craftCapeCheckbox.isSelected();
          potUp = potUpCheckbox.isSelected();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          startTime = System.currentTimeMillis();
          scriptStarted = true;
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(label6);
    scriptFrame.add(blankLabel);
    scriptFrame.add(craftCapeCheckbox);
    scriptFrame.add(capeItemIdLabel);
    scriptFrame.add(capeItemIdField);
    scriptFrame.add(dragonTwoHandCheckbox);
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
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
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
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int DbonesSuccessPerHr = 0;
      int RdaggerSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int FireSuccessPerHr = 0;
      int LawSuccessPerHr = 0;
      int NatSuccessPerHr = 0;
      int WaterSuccessPerHr = 0;
      int AddySuccessPerHr = 0;
      int HerbSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        DbonesSuccessPerHr = (int) (totalBones * scale);
        RdaggerSuccessPerHr = (int) (totalRdagger * scale);
        GemsSuccessPerHr = (int) (totalGems * scale);
        FireSuccessPerHr = (int) (totalFire * scale);
        LawSuccessPerHr = (int) (totalLaw * scale);
        NatSuccessPerHr = (int) (totalNat * scale);
        WaterSuccessPerHr = (int) (totalWater * scale);
        AddySuccessPerHr = (int) (totalAddy * scale);
        HerbSuccessPerHr = (int) (totalHerb * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Tavelry Blue Dragons @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@______________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Gathered D.Bones: @gre@"
              + totalBones
              + "@yel@ (@whi@"
              + String.format("%,d", DbonesSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@D.Bones in Bank: @gre@"
              + bankBones,
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@R. Dagger: @gre@"
              + totalRdagger
              + "@yel@ (@whi@"
              + String.format("%,d", RdaggerSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Addy Plate: @gre@"
              + totalAddy
              + "@yel@ (@whi@"
              + String.format("%,d", AddySuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Addy Ore: @gre@"
              + totalAddy
              + "@yel@ (@whi@"
              + String.format("%,d", AddySuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Laws: @gre@"
              + totalLaw
              + "@yel@ (@whi@"
              + String.format("%,d", LawSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Natures: @gre@"
              + totalNat
              + "@yel@ (@whi@"
              + String.format("%,d", NatSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Waters: @gre@"
              + totalWater
              + "@yel@ (@whi@"
              + String.format("%,d", WaterSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Herbs: @gre@"
              + totalHerb
              + "@yel@ (@whi@"
              + String.format("%,d", HerbSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Total Gems: @gre@"
              + totalGems
              + "@yel@ (@whi@"
              + String.format("%,d", GemsSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tooth: @gre@"
              + totalTooth
              + "@yel@ / @whi@Loop: @gre@"
              + totalLoop
              + "@yel@ / @whi@Fires: @gre@"
              + totalFire
              + "@yel@ (@whi@"
              + String.format("%,d", FireSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);

      c.drawString(
          "@whi@Left Half: @gre@" + totalLeft + "@yel@ / @whi@Rune Spear: @gre@" + totalSpear,
          x,
          y + (14 * 6),
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
          y + (14 * 7),
          0xFFFFFF,
          1);
      c.drawString("@whi@______________________", x, y + 3 + (14 * 7), 0xFFFFFF, 1);
    }
  }
}
