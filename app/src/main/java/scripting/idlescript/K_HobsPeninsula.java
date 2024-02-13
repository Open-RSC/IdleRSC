package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Asgarnian Hobs Peninsula</b>
 *
 * <p>Start in Fally East bank with Armor or Hobs Peninsula
 *
 * <p>Options: Combat Style, Loot level Herbs, Reg pots, Alter Prayer Boost, Food Type, and Food
 * Withdraw Amount Selection, Chat Command Options, Full top-left GUI, regular atk/str pot option,
 * and Autostart.
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_HobsPeninsula extends K_kailaScript {
  private static int totalLimp = 0;
  private static final int[] loot = {
    ItemId.UNID_GUAM_LEAF.getId(),
    ItemId.UNID_MARRENTILL.getId(),
    ItemId.UNID_TARROMIN.getId(),
    ItemId.UNID_HARRALANDER.getId(),
    ItemId.UNID_RANARR_WEED.getId(),
    ItemId.UNID_IRIT.getId(),
    ItemId.UNID_AVANTOE.getId(),
    ItemId.UNID_KWUARM.getId(),
    ItemId.UNID_CADANTINE.getId(),
    ItemId.UNID_DWARF_WEED.getId(),
    ItemId.AIR_RUNE.getId(),
    ItemId.EARTH_RUNE.getId(),
    ItemId.FIRE_RUNE.getId(),
    ItemId.WATER_RUNE.getId(),
    ItemId.NATURE_RUNE.getId(),
    ItemId.LAW_RUNE.getId(),
    ItemId.COSMIC_RUNE.getId(),
    ItemId.BODY_RUNE.getId(),
    ItemId.MIND_RUNE.getId(),
    ItemId.CHAOS_RUNE.getId(),
    ItemId.DEATH_RUNE.getId(),
    ItemId.BLOOD_RUNE.getId(),
    ItemId.LIMPWURT_ROOT.getId(), // limps
    ItemId.BRONZE_ARROWS.getId(), // bronze arrow
    ItemId.COINS.getId(), // , 	 //coins
    ItemId.UNCUT_SAPPHIRE.getId(),
    ItemId.UNCUT_EMERALD.getId(),
    ItemId.UNCUT_RUBY.getId(),
    ItemId.UNCUT_DIAMOND.getId(),
    ItemId.TOOTH_HALF_KEY.getId(),
    ItemId.LOOP_HALF_KEY.getId(),
    ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.getId(),
    ItemId.RUNE_SPEAR.getId(),
    ItemId.COINS.getId() //
  };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    centerX = 363;
    centerY = 610;
    centerDistance = 15;
    if (!parameters[0].equals("")) {
      try {
        foodWithdrawAmount = Integer.parseInt(parameters[0]);
      } catch (Exception e) {
        System.out.println("Could not parse parameters!");
        c.displayMessage("@red@Could not parse parameters!");
        c.stop();
      }
      if (foodWithdrawAmount != -1) {
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
      c.displayMessage("@red@Asgarnian Hobs Peninsula - By Kaila");
      c.displayMessage("@red@Start in Fally East bank with Armor or Hobs Peninsula");
      c.displayMessage("@red@Food in Bank REQUIRED");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 595) {
        bank();
        BankToPeninsula();
        c.sleep(1380);
      }
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      boolean ate = eatFood();
      if (!ate) {
        c.setStatus("@yel@Banking..");
        PeninsulaToBank();
        bank();
        BankToPeninsula();
      }
      if (c.getInventoryItemCount() < 30) {
        checkInventoryItemCounts();
        lootItems(false, loot);
        if (potUp) {
          attackBoost(0, false);
          strengthBoost(0, false);
        }
        if (!c.isInCombat()) {
          int[] npcIds = {67};
          ORSCharacter npc = c.getNearestNpcByIds(npcIds, false);
          if (npc != null) {
            c.setStatus("@yel@Attacking..");
            c.attackNpc(npc.serverIndex);
            c.sleep(GAME_TICK);
          } else {
            lootItems(false, loot);
            c.sleep(GAME_TICK);
            if (c.currentX() != 364 || c.currentY() != 607) {
              c.walkTo(364, 607);
              c.sleep(2 * GAME_TICK);
            }
          }
        }
        c.sleep(GAME_TICK);
      }
      if (c.getInventoryItemCount() == 30) {
        dropItemToLoot(false, 1, ItemId.EMPTY_VIAL.getId());
        buryBonesToLoot(false);
      }
      if (c.getInventoryItemCount() > 29 || c.getInventoryItemCount(foodId) == 0) {
        c.setStatus("@yel@Banking..");
        PeninsulaToBank();
        bank();
        BankToPeninsula();
        c.sleep(618);
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
      totalGuam = totalGuam + c.getInventoryItemCount(165);
      totalMar = totalMar + c.getInventoryItemCount(435);
      totalTar = totalTar + c.getInventoryItemCount(436);
      totalHar = totalHar + c.getInventoryItemCount(437);
      totalRan = totalRan + c.getInventoryItemCount(438);
      totalIrit = totalIrit + c.getInventoryItemCount(439);
      totalAva = totalAva + c.getInventoryItemCount(440);
      totalKwuarm = totalKwuarm + c.getInventoryItemCount(441);
      totalCada = totalCada + c.getInventoryItemCount(442);
      totalDwarf = totalDwarf + c.getInventoryItemCount(443);
      totalLaw = totalLaw + c.getInventoryItemCount(42);
      totalNat = totalNat + c.getInventoryItemCount(40);
      totalLimp = totalLimp + c.getInventoryItemCount(220);
      totalLoop = totalLoop + c.getInventoryItemCount(527);
      totalTooth = totalTooth + c.getInventoryItemCount(526);
      totalLeft = totalLeft + c.getInventoryItemCount(1277);
      totalGems =
          totalGems
              + c.getInventoryItemCount(160)
              + c.getInventoryItemCount(159)
              + c.getInventoryItemCount(158)
              + c.getInventoryItemCount(157);
      totalSpear = totalSpear + c.getInventoryItemCount(1092);

      if (c.getInventoryItemCount() > 2) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 476
              && itemId != 475
              && itemId != 224
              && itemId != 223) { // dont deposit partial potions!
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
      }
      c.sleep(640);
      if (potUp) {
        withdrawAttack(1);
        withdrawStrength(1);
      }
      withdrawFood(foodId, foodWithdrawAmount);
      bankItemCheck(foodId, 5);
      c.closeBank();
      checkInventoryItemCounts();
    }
  }

  private void PeninsulaToBank() {
    c.setStatus("@gre@Walking to Bank..");
    c.walkTo(361, 614);
    c.walkTo(356, 619);
    c.walkTo(346, 619);
    c.walkTo(336, 619);
    c.walkTo(326, 619);
    c.walkTo(319, 619);
    c.walkTo(314, 614);
    c.walkTo(309, 609);
    c.walkTo(309, 607);
    c.walkTo(299, 597);
    c.walkTo(291, 589);
    c.walkTo(291, 576);
    c.walkTo(286, 571);
    c.sleep(640);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking..");
  }

  private void BankToPeninsula() {
    c.setStatus("@gre@Walking to Penensula..");
    c.walkTo(287, 572);
    c.walkTo(291, 576);
    c.walkTo(291, 589);
    c.walkTo(299, 597);
    c.walkTo(309, 607);
    c.walkTo(309, 609);
    c.walkTo(314, 614);
    c.walkTo(319, 619);
    c.walkTo(326, 619);
    c.walkTo(336, 619);
    c.walkTo(346, 619);
    c.walkTo(356, 619);
    c.walkTo(361, 614);
    c.setStatus("@gre@Done Walking..");
  }

  private void setupGUI() {
    JLabel header = new JLabel("Asgarnian Hobs Peninsula - By Kaila");
    JLabel label1 = new JLabel("Start in Fally East bank with Armor");
    JLabel label2 = new JLabel("	or at Hobgoblin Peninsula");
    JLabel label3 = new JLabel("Food in bank REQUIRED (pots optional)");
    JCheckBox potUpCheckbox = new JCheckBox("Use regular Atk/Str Pots?", true);
    JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
    JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
    JLabel foodLabel = new JLabel("Type of Food:");
    JComboBox<String> foodField = new JComboBox<>(foodTypes);
    JLabel blankLabel = new JLabel("          ");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          if (!foodWithdrawAmountField.getText().equals(""))
            foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
          potUp = potUpCheckbox.isSelected();
          foodId = foodIds[foodField.getSelectedIndex()];
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
    scriptFrame.add(potUpCheckbox);
    scriptFrame.add(foodWithdrawAmountLabel);
    scriptFrame.add(foodWithdrawAmountField);
    scriptFrame.add(foodLabel);
    scriptFrame.add(foodField);
    scriptFrame.add(blankLabel);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int guamSuccessPerHr = 0;
      int marSuccessPerHr = 0;
      int tarSuccessPerHr = 0;
      int harSuccessPerHr = 0;
      int ranSuccessPerHr = 0;
      int iritSuccessPerHr = 0;
      int avaSuccessPerHr = 0;
      int kwuSuccessPerHr = 0;
      int cadaSuccessPerHr = 0;
      int dwarSuccessPerHr = 0;
      int limpSuccessPerHr = 0;
      int lawSuccessPerHr = 0;
      int natSuccessPerHr = 0;
      int GemsSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        guamSuccessPerHr = (int) ((totalGuam + inventGuam) * scale);
        marSuccessPerHr = (int) ((totalMar + inventMar) * scale);
        tarSuccessPerHr = (int) ((totalTar + inventTar) * scale);
        harSuccessPerHr = (int) ((totalHar + inventHar) * scale);
        ranSuccessPerHr = (int) ((totalRan + inventRan) * scale);
        iritSuccessPerHr = (int) ((totalIrit + inventIrit) * scale);
        avaSuccessPerHr = (int) ((totalAva + inventAva) * scale);
        kwuSuccessPerHr = (int) ((totalKwuarm + inventKwuarm) * scale);
        cadaSuccessPerHr = (int) ((totalCada + inventCada) * scale);
        dwarSuccessPerHr = (int) ((totalDwarf + inventDwarf) * scale);
        limpSuccessPerHr = (int) (totalLimp * scale);
        lawSuccessPerHr = (int) ((totalLaw + inventLaws) * scale);
        natSuccessPerHr = (int) ((totalNat + inventNats) * scale);
        GemsSuccessPerHr = (int) ((totalGems + inventGems) * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);
      } catch (Exception e) {
        // divide by zero
      }
      final int x = 350;
      c.drawString("@red@Hobgoblin Peninsula @whi@~ @mag@Kaila", x - 20, 48, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Guams: @gre@"
              + (totalGuam + inventGuam)
              + "@yel@ (@whi@"
              + String.format("%,d", guamSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          62,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Marrentills: @gre@"
              + (totalMar + inventMar)
              + "@yel@ (@whi@"
              + String.format("%,d", marSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          76,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tarromins: @gre@"
              + (totalTar + inventTar)
              + "@yel@ (@whi@"
              + String.format("%,d", tarSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          90,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Harralanders: @gre@"
              + (totalHar + inventHar)
              + "@yel@ (@whi@"
              + String.format("%,d", harSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          104,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Ranarrs: @gre@"
              + (totalRan + inventRan)
              + "@yel@ (@whi@"
              + String.format("%,d", ranSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          118,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Irit Herbs: @gre@"
              + (totalIrit + inventIrit)
              + "@yel@ (@whi@"
              + String.format("%,d", iritSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          132,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Avantoes: @gre@"
              + (totalAva + inventAva)
              + "@yel@ (@whi@"
              + String.format("%,d", avaSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          146,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Kwuarms: @gre@"
              + (totalKwuarm + inventKwuarm)
              + "@yel@ (@whi@"
              + String.format("%,d", kwuSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          160,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Cadantines: @gre@"
              + (totalCada + inventCada)
              + "@yel@ (@whi@"
              + String.format("%,d", cadaSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          174,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Dwarfs: @gre@"
              + (totalDwarf + inventDwarf)
              + "@yel@ (@whi@"
              + String.format("%,d", dwarSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          188,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Limpwurts: @gre@"
              + totalLimp
              + "@yel@ (@whi@"
              + String.format("%,d", limpSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          202,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Laws: @gre@"
              + (totalLaw + inventLaws)
              + "@yel@ (@whi@"
              + String.format("%,d", lawSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          216,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Nats: @gre@"
              + (totalNat + inventNats)
              + "@yel@ (@whi@"
              + String.format("%,d", natSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          230,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Gems: @gre@"
              + (totalGems + inventGems)
              + "@yel@ (@whi@"
              + String.format("%,d", GemsSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          244,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Tooth: @gre@"
              + (totalTooth + inventTooth)
              + "@yel@ / @whi@Loop: @gre@"
              + (totalLoop + inventLoop),
          x,
          258,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@R.Spear: @gre@"
              + (totalSpear + inventSpear)
              + "@yel@ / @whi@Shield Half: @gre@"
              + (totalLeft + inventLeft),
          x,
          272,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@)",
          x,
          286,
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, 300, 0xFFFFFF, 1);
    }
  }
}
