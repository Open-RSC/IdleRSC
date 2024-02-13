package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Ogre Enclave Blue</b> Kills blue dragons inside ogre enclave (high reqs)
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_YanilleBlueDrag extends K_kailaScript {
  private int fightMode = 0;
  private boolean useDragonTwoHand = false;
  private boolean agilityCapeTeleport = false;
  private int totalRdagger = 0;
  private int bankNightshade = 0;
  private int bankFood = 0;
  private boolean buryBigBones = false;
  private static final int DRAGON_TWO_HAND = ItemId.DRAGON_2_HANDED_SWORD.getId();
  private static final int ANTI_DRAGON_SHIELD = ItemId.ANTI_DRAGON_BREATH_SHIELD.getId();
  private static final int ATTACK_CAPE = ItemId.ATTACK_CAPE.getId();
  private static final int AGILITY_CAPE = ItemId.AGILITY_CAPE.getId();
  private static final int NIGHTSHADE = ItemId.NIGHTSHADE.getId();
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
    if (!parameters[0].isEmpty()) {
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
      c.displayMessage("@red@Ogre Enclave Blue Dragons - By Kaila");
      c.displayMessage("@red@Start in Yanille bank with gear on, or in dragon room!");
      c.displayMessage("@red@Food and nightshade banked REQUIRED");
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
        c.setStatus("@gre@Equip Antishield.");
        c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
        c.sleep(GAME_TICK);
      } else if (useDragonTwoHand && c.isInCombat() && !c.isItemIdEquipped(DRAGON_TWO_HAND)) {
        c.setStatus("@gre@Equip d2h.");
        c.equipItem(c.getInventoryItemSlotIndex(DRAGON_TWO_HAND));
        c.sleep(GAME_TICK);
      }
      if (!eatFood() || c.getInventoryItemCount(foodId) == 0 || timeToBank || timeToBankStay) {
        c.setStatus("@red@Out of food!");
        if (useDragonTwoHand && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD))
          c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
        c.setStatus("@yel@Heading to the Bank..");
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
      lootItems(true, loot, ANTI_DRAGON_SHIELD, useDragonTwoHand);
      if (buryBigBones) {
        c.setStatus("@gre@Looting/Burying Big Bones.");
        lootItem(false, ItemId.BIG_BONES.getId());
        buryBones(false, ItemId.BIG_BONES.getId());
      }
      if (buryBones) buryBones(false);
      if (potUp) {
        superAttackBoost(2, false);
        superStrengthBoost(2, false);
      }
      checkFightMode(fightMode);
      if (!c.isInCombat()) {
        if (useDragonTwoHand && !c.isItemIdEquipped(ANTI_DRAGON_SHIELD)) {
          c.setStatus("@gre@Equip Antishield.");
          c.equipItem(c.getInventoryItemSlotIndex(ANTI_DRAGON_SHIELD));
        }
        ORSCharacter npc = c.getNearestNpcById(202, false);
        if (npc != null) {
          c.setStatus("@yel@Attacking Dragons");
          c.attackNpc(npc.serverIndex);
          c.sleep(2 * GAME_TICK);
        } else {
          c.sleep(2 * GAME_TICK);
          if (!c.isInCombat() && (c.currentX() != 644 && c.currentY() != 3611)) {
            c.setStatus("@gre@No npc going to center.");
            lootItems(false, loot, ANTI_DRAGON_SHIELD, useDragonTwoHand);
            c.walkTo(644, 3611);
            c.setStatus("@gre@at center.");
          }
        }
      } else c.sleep(GAME_TICK);
      if (c.getInventoryItemCount() == 30) {
        c.setStatus("@gre@Making inv space.");
        dropItemToLoot(false, 1, ItemId.EMPTY_VIAL.getId());
        if (buryBones) buryBonesToLoot(false);
        eatFoodToLoot(false);
      }
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
            && itemId != NIGHTSHADE
            && itemId != ATTACK_CAPE
            && itemId != AGILITY_CAPE) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      bankFood = c.getBankItemCount(foodId);
      bankBones = c.getBankItemCount(814);
      bankNightshade = c.getBankItemCount(NIGHTSHADE);
      c.sleep(3000); // Important, leave in

      int dragonTwoHand = ItemId.DRAGON_2_HANDED_SWORD.getId();
      if (useDragonTwoHand && (c.getInventoryItemCount(dragonTwoHand) < 1))
        withdrawItem(dragonTwoHand, 1);
      if (c.getInventoryItemCount(NIGHTSHADE) < 1) withdrawItem(NIGHTSHADE, 1);
      if (agilityCapeTeleport
          && (c.getInventoryItemCount(AGILITY_CAPE) < 1)
          && !c.isItemIdEquipped(AGILITY_CAPE)) {
        withdrawItem(AGILITY_CAPE, 1);
      }
      if (agilityCapeTeleport && (c.getInventoryItemCount(AGILITY_CAPE) > 1))
        c.depositItem(AGILITY_CAPE, c.getInventoryItemCount(AGILITY_CAPE) - 1);
      if (!agilityCapeTeleport) {
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
      if (!agilityCapeTeleport) {
        bankItemCheck(airId, 30);
        bankItemCheck(waterId, 10); // Falador teleport
        bankItemCheck(lawId, 10);
      }
      bankCheckAntiDragonShield();
      c.closeBank();
      if (!c.isItemIdEquipped(ATTACK_CAPE)) c.equipItem(c.getInventoryItemSlotIndex(ATTACK_CAPE));
    }
    if (!agilityCapeTeleport) {
      inventoryItemCheck(airId, 18);
      inventoryItemCheck(waterId, 6);
      inventoryItemCheck(lawId, 6);
    }
  }
  // PATHING private voids
  private void BankToDragons() {
    c.setStatus("@gre@Walking to Entrance..");
    if (agilityCapeTeleport && (c.getInventoryItemCount(AGILITY_CAPE) != 0)) {
      teleportAgilityCape(); // 591, 765
      c.walkTo(601, 765);
      c.walkTo(611, 765);
      c.walkTo(619, 765);
      c.walkTo(624, 760);
    } else {
      c.walkTo(584, 752);
      c.walkTo(584, 749);
      c.walkTo(594, 749);
      c.walkTo(604, 749);
      c.walkTo(608, 749);
      c.walkTo(613, 755);
      c.walkTo(623, 755);
      c.walkTo(613, 755);
      c.walkTo(623, 755);
    }
    c.walkTo(630, 754); // garden
    c.walkTo(638, 754);
    c.walkTo(642, 753);
    // open west yanille inner door
    if (c.getObjectAtCoord(642, 753) == 64) {
      c.atObject(642, 753);
      c.sleep(1000);
    }
    c.walkTo(646, 753);
    // open west yanille outer door
    if (c.getObjectAtCoord(647, 753) == 64) {
      c.atObject(647, 753);
      c.sleep(1000);
    }
    c.setStatus("@gre@Exiting Yanille..");
    c.walkTo(647, 756);
    c.walkTo(647, 766);
    c.walkTo(641, 771);
    c.walkTo(631, 771);
    c.walkTo(626, 774);
    c.walkTo(633, 780);
    c.walkTo(637, 782);
    c.walkTo(645, 779);
    c.walkTo(651, 771);
    c.walkTo(660, 773);
    c.walkTo(665, 773);
    // ogre door upper
    ogreGateEastToWest();
    c.setStatus("@gre@Past ogre Gate..");
    c.walkTo(668, 778);
    c.setStatus("@gre@Danger zone one..");
    if (useDragonTwoHand && !c.isItemIdEquipped(420)) {
      c.equipItem(c.getInventoryItemSlotIndex(420));
    }
    c.walkTo(661, 778);
    c.walkTo(663, 788);
    c.setStatus("@gre@Entering Enclave..");
    enterEnclave();
    // while out of combat, use item on ogre guard and wait
    // now at 647,3644
    c.setStatus("@gre@Inside enclave..");
    c.walkTo(647, 3634);
    c.walkTo(650, 3625);
    c.walkTo(654, 3617);
    c.setStatus("@gre@Done Walking..");
  }

  private void DragonsToBank() {
    c.setStatus("@gre@Walking to Bank..");
    if (agilityCapeTeleport && (c.getInventoryItemCount(AGILITY_CAPE) != 0)) {
      if (c.isInCombat()) leaveCombat();
      c.setStatus("@gre@teleporting away..");
      c.itemCommand(AGILITY_CAPE);
      c.sleep(4 * GAME_TICK);
      c.itemCommand(AGILITY_CAPE);
      c.sleep(4 * GAME_TICK);
      if (c.currentY() > 3000) {
        c.setStatus("@gre@Going to safe zone..");
        c.walkTo(645, 3615);
        c.setStatus("@gre@teleporting away..");
        teleportAway();
      }
      c.setStatus("@gre@Done Teleporting..");
      c.walkTo(582, 767);
      c.walkTo(579, 763);
      c.walkTo(584, 754);
      c.walkTo(585, 752);
    } else {
      c.log("error there is no path for this");
      c.setStatus("@gre@error there is no path for this..");
      c.displayMessage("error there is no path for this");
    }
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private static void ogreGateEastToWest() {
    for (int i = 1; i <= 10; i++) {
      if (c.currentX() == 665 && c.currentY() < 774 && c.currentY() > 771) {
        c.setStatus("@red@Crossing Gate..");
        c.atObject(666, 772); // gate won't break if someone else opens it
        c.sleep(6 * GAME_TICK);
      } else {
        break;
      }
    }
  }

  private static void teleportAway() { // either die here
    for (int i = 1; i <= 20; i++) {
      if (c.currentX() != 591 && c.currentY() != 765) {
        c.setStatus("@red@teleporting..");
        if (c.isInCombat()) {
          leaveCombat();
          c.setStatus("@gre@leaving combat to teleport..");
        }
        teleportAgilityCape();
      } else {
        break;
      }
    }
  }

  private static void enterEnclave() { // or maby here
    for (int i = 1; i <= 20; i++) {
      if (c.currentY() < 3000) {
        c.setStatus("@red@entering enclave.."); // may have died here
        if (c.isInCombat() && c.currentY() > 3000) leaveCombat();
        ORSCharacter npc3 = c.getNearestNpcById(684, true);
        c.useItemOnNpc(npc3.serverIndex, NIGHTSHADE);
        c.sleep(14000);
      } else {
        break;
      }
    }
  }

  private static void exitEnclave() {
    for (int i = 1; i <= 20; i++) {
      if (c.currentY() > 3000) {
        c.setStatus("@red@exiting enclave..");
        if (c.isInCombat()) leaveCombat();
        c.atObject(666, 772);
        c.sleep(2000);
      } else {
        break;
      }
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Ogre Enclave Blue Dragons - By Kaila");
    JLabel label1 = new JLabel("Start in Yanille bank with gear on, or in dragon room");
    JLabel label2 = new JLabel("Food and nightshade banked REQUIRED");
    JLabel label3 = new JLabel("Recommend max cmb, d2h, and agility cape");
    JLabel label4 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label5 = new JLabel("::bank ::bankstay ::burybones");
    JLabel label6 = new JLabel("Styles ::attack :strength ::defense ::controlled");
    JLabel blankLabel = new JLabel("     ");
    JCheckBox agilityCapeCheckbox = new JCheckBox("99 Agility Cape Teleport?", true);
    JCheckBox dragonTwoHandCheckbox = new JCheckBox("Swap to Dragon 2h Sword", true);
    JCheckBox buryBonesCheckbox = new JCheckBox("Bury Dragon Bones?", false);
    JCheckBox buryBigBonesCheckbox = new JCheckBox("Bury Big Bones?", false);
    JCheckBox potUpCheckbox = new JCheckBox("Use super Atk/Str Pots? (Super or reg)", false);
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

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().isEmpty()) {
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          } else {
            foodWithdrawAmount = 22;
          }
          buryBones = buryBonesCheckbox.isSelected();
          buryBigBones = buryBigBonesCheckbox.isSelected();
          fightMode = fightModeField.getSelectedIndex();
          foodId = foodIds[foodField.getSelectedIndex()];
          useDragonTwoHand = dragonTwoHandCheckbox.isSelected();
          agilityCapeTeleport = agilityCapeCheckbox.isSelected();
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
    scriptFrame.add(agilityCapeCheckbox);
    scriptFrame.add(dragonTwoHandCheckbox);
    scriptFrame.add(buryBonesCheckbox);
    scriptFrame.add(buryBigBonesCheckbox);
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
      int y2 = 220;
      c.drawString("@red@Ogre Enclave Blue Dragons @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
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
          "@whi@Nightshade in Bank: @gre@"
              + bankNightshade
              + "@yel@ / @whi@ Food in Bank: @gre@"
              + bankFood,
          x,
          y + (14 * 7),
          0xFFFFFF,
          1);
      c.drawString("@whi@____________________", x, y2, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Runtime: "
              + runTime,
          x,
          y2 + 14,
          0xFFFFFF,
          1);
    }
  }
}
