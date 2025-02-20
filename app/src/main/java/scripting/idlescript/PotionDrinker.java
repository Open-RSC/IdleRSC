package scripting.idlescript;

import bot.Main;
import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;
import controller.Controller;
import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.entities.ItemId;

public class PotionDrinker extends IdleScript {
  private final Controller c = Main.getController();
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.HERBLAW,
          },
          "xatain, Dvorak/Seatta code refactored",
          "Drinks potions from your bank");

  private final int[] StrengthPotion = {222, 223, 224};
  private final int[] AttackPotion = {474, 475, 476};
  private final int[] StatRestorePotion = {477, 478, 479};
  private final int[] DefensePotion = {480, 481, 482};
  private final int[] RestorePrayerPotion = {483, 484, 485};
  private final int[] SuperAttackPotion = {486, 487, 488};
  private final int[] FishingPotion = {489, 490, 491};
  private final int[] SuperStrengthPotion = {492, 493, 494};
  private final int[] SuperDefensePotion = {495, 496, 497};
  private final int[] RangingPotion = {498, 499, 500};
  private final int[] CurePoisonPotion = {566, 567, 568};
  private final int[] ZamorakPotion = {963, 964, 965};
  private final int[] SuperRunecraftPotion = {1414, 1415, 1416};
  private final int[] RunecraftPotion = {1411, 1412, 1413};
  private final int[] MagicPotion = {1468, 1469, 1470};
  private final int[] SaradominPotion = {1471, 1472, 1473};
  private final int[] SuperRangingPotion = {1474, 1475, 1476};
  private final int[] SuperMagicPotion = {1477, 1478, 1479};
  private int[][] potions;
  private final JCheckBox strengthPotionCheckbox = new JCheckBox("Strength");
  private final JCheckBox attackPotionCheckbox = new JCheckBox("Attack");
  private final JCheckBox statRestorePotionCheckbox = new JCheckBox("Stat Restore");
  private final JCheckBox DefensePotionCheckbox = new JCheckBox("Defense");
  private final JCheckBox RestorePrayerPotionCheckbox = new JCheckBox("Restore Prayer");
  private final JCheckBox SuperAttackPotionCheckbox = new JCheckBox("Super Att");
  private final JCheckBox FishingPotionCheckbox = new JCheckBox("Fishing");
  private final JCheckBox SuperStrengthPotionCheckbox = new JCheckBox("Super Str");
  private final JCheckBox SuperDefensePotionCheckbox = new JCheckBox("Super Def");
  private final JCheckBox RangingPotionCheckbox = new JCheckBox("Ranging");
  private final JCheckBox CurePoisonPotionCheckbox = new JCheckBox("Cure Poison");
  private final JCheckBox ZamorakPotionCheckbox = new JCheckBox("Zamorak");
  private final JCheckBox SuperRunecraftPotionCheckbox = new JCheckBox("Super RC");
  private final JCheckBox RunecraftPotionCheckbox = new JCheckBox("Runecraft");
  private final JCheckBox MagicPotionCheckbox = new JCheckBox("Magic");
  private final JCheckBox SaradominPotionCheckbox = new JCheckBox("Saradomin");
  private final JCheckBox SuperRangingPotionCheckbox = new JCheckBox("Super Range");
  private final JCheckBox SuperMagicPotionCheckbox = new JCheckBox("Super Mage");

  // do not modify these
  private boolean guiSetup = false;
  private boolean scriptStarted = false;

  private final JFrame frame = new JFrame("PotionDrinker: " + c.getPlayerName());
  private final JPanel panel = new JPanel();

  // UI Components
  private final SpringLayout sl = new SpringLayout();
  private final JButton startBtn = new JButton("Check the potions to drink, then click here");

  public void showGUI() {
    setDefaultValues();
    addComponentsToPanel();
    setConstraints();
    setTheming();
    setListeners();
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(Main.rscFrame);
    frame.setVisible(true);
    frame.requestFocus();

    while (!scriptStarted && c.isRunning() && frame.isVisible()) c.sleep(640);
  }

  private void setDefaultValues() {
    scriptStarted = false;
    startBtn.setEnabled(true);

    strengthPotionCheckbox.setEnabled(true);
    attackPotionCheckbox.setEnabled(true);
    statRestorePotionCheckbox.setEnabled(true);
    DefensePotionCheckbox.setEnabled(true);
    RestorePrayerPotionCheckbox.setEnabled(true);
    SuperAttackPotionCheckbox.setEnabled(true);
    FishingPotionCheckbox.setEnabled(true);
    SuperStrengthPotionCheckbox.setEnabled(true);
    SuperDefensePotionCheckbox.setEnabled(true);
    RangingPotionCheckbox.setEnabled(true);
    CurePoisonPotionCheckbox.setEnabled(true);
    ZamorakPotionCheckbox.setEnabled(true);
    SuperRunecraftPotionCheckbox.setEnabled(true);
    RunecraftPotionCheckbox.setEnabled(true);
    MagicPotionCheckbox.setEnabled(true);
    SaradominPotionCheckbox.setEnabled(true);
    SuperRangingPotionCheckbox.setEnabled(true);
    SuperMagicPotionCheckbox.setEnabled(true);
  }

  private void addComponentsToPanel() {
    panel.setLayout(sl);
    panel.add(strengthPotionCheckbox);
    panel.add(attackPotionCheckbox);
    panel.add(statRestorePotionCheckbox);
    panel.add(DefensePotionCheckbox);
    panel.add(RestorePrayerPotionCheckbox);
    panel.add(SuperAttackPotionCheckbox);
    panel.add(FishingPotionCheckbox);
    panel.add(SuperStrengthPotionCheckbox);
    panel.add(SuperDefensePotionCheckbox);
    panel.add(RangingPotionCheckbox);
    panel.add(CurePoisonPotionCheckbox);
    panel.add(ZamorakPotionCheckbox);
    panel.add(SuperRunecraftPotionCheckbox);
    panel.add(RunecraftPotionCheckbox);
    panel.add(MagicPotionCheckbox);
    panel.add(SaradominPotionCheckbox);
    panel.add(SuperRangingPotionCheckbox);
    panel.add(SuperMagicPotionCheckbox);
    panel.add(startBtn);
  }

  public void setListeners() {

    startBtn.addActionListener(
        e -> {
          setValuesFromGUI(
              strengthPotionCheckbox,
              attackPotionCheckbox,
              statRestorePotionCheckbox,
              DefensePotionCheckbox,
              RestorePrayerPotionCheckbox,
              SuperAttackPotionCheckbox,
              FishingPotionCheckbox,
              SuperStrengthPotionCheckbox,
              SuperDefensePotionCheckbox,
              RangingPotionCheckbox,
              CurePoisonPotionCheckbox,
              ZamorakPotionCheckbox,
              SuperRunecraftPotionCheckbox,
              RunecraftPotionCheckbox,
              MagicPotionCheckbox,
              SaradominPotionCheckbox,
              SuperRangingPotionCheckbox,
              SuperMagicPotionCheckbox);
          if (potions.length < 1) return;
          c.displayMessage("@red@PotionDrinker by xatain, with borrowings from Dvorak. Drink up!");
          c.setStatus("@red@Started...");

          frame.setVisible(false);
          frame.dispose();
          scriptStarted = true;
        });
  }

  public void setConstraints() {
    // FIRST COLUMN OF CHECKBOXES
    sl.putConstraint(SpringLayout.NORTH, strengthPotionCheckbox, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, strengthPotionCheckbox, 4, SpringLayout.WEST, panel);

    sl.putConstraint(
        SpringLayout.NORTH, attackPotionCheckbox, 4, SpringLayout.SOUTH, strengthPotionCheckbox);
    sl.putConstraint(SpringLayout.WEST, attackPotionCheckbox, 4, SpringLayout.WEST, panel);

    sl.putConstraint(
        SpringLayout.NORTH, statRestorePotionCheckbox, 4, SpringLayout.SOUTH, attackPotionCheckbox);
    sl.putConstraint(SpringLayout.WEST, statRestorePotionCheckbox, 4, SpringLayout.WEST, panel);

    sl.putConstraint(
        SpringLayout.NORTH,
        DefensePotionCheckbox,
        4,
        SpringLayout.SOUTH,
        statRestorePotionCheckbox);
    sl.putConstraint(SpringLayout.WEST, DefensePotionCheckbox, 4, SpringLayout.WEST, panel);

    sl.putConstraint(
        SpringLayout.NORTH,
        RestorePrayerPotionCheckbox,
        4,
        SpringLayout.SOUTH,
        DefensePotionCheckbox);
    sl.putConstraint(SpringLayout.WEST, RestorePrayerPotionCheckbox, 4, SpringLayout.WEST, panel);

    sl.putConstraint(
        SpringLayout.NORTH,
        SuperAttackPotionCheckbox,
        4,
        SpringLayout.SOUTH,
        RestorePrayerPotionCheckbox);
    sl.putConstraint(SpringLayout.WEST, SuperAttackPotionCheckbox, 4, SpringLayout.WEST, panel);

    // SECOND COLUMN OF CHECKBOXES
    sl.putConstraint(SpringLayout.NORTH, FishingPotionCheckbox, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(
        SpringLayout.WEST,
        FishingPotionCheckbox,
        50,
        SpringLayout.EAST,
        SuperAttackPotionCheckbox); // Increased space

    sl.putConstraint(
        SpringLayout.NORTH,
        SuperStrengthPotionCheckbox,
        4,
        SpringLayout.SOUTH,
        FishingPotionCheckbox);
    sl.putConstraint(
        SpringLayout.WEST,
        SuperStrengthPotionCheckbox,
        50,
        SpringLayout.EAST,
        SuperAttackPotionCheckbox); // Increased space

    sl.putConstraint(
        SpringLayout.NORTH,
        SuperDefensePotionCheckbox,
        4,
        SpringLayout.SOUTH,
        SuperStrengthPotionCheckbox);
    sl.putConstraint(
        SpringLayout.WEST,
        SuperDefensePotionCheckbox,
        50,
        SpringLayout.EAST,
        SuperAttackPotionCheckbox); // Increased space

    sl.putConstraint(
        SpringLayout.NORTH,
        RangingPotionCheckbox,
        4,
        SpringLayout.SOUTH,
        SuperDefensePotionCheckbox);
    sl.putConstraint(
        SpringLayout.WEST,
        RangingPotionCheckbox,
        50,
        SpringLayout.EAST,
        SuperAttackPotionCheckbox); // Increased space

    sl.putConstraint(
        SpringLayout.NORTH, CurePoisonPotionCheckbox, 4, SpringLayout.SOUTH, RangingPotionCheckbox);
    sl.putConstraint(
        SpringLayout.WEST,
        CurePoisonPotionCheckbox,
        50,
        SpringLayout.EAST,
        SuperAttackPotionCheckbox); // Increased space

    sl.putConstraint(
        SpringLayout.NORTH, ZamorakPotionCheckbox, 4, SpringLayout.SOUTH, CurePoisonPotionCheckbox);
    sl.putConstraint(
        SpringLayout.WEST,
        ZamorakPotionCheckbox,
        50,
        SpringLayout.EAST,
        SuperAttackPotionCheckbox); // Increased space

    // THIRD COLUMN OF CHECKBOXES
    sl.putConstraint(
        SpringLayout.NORTH, SuperRunecraftPotionCheckbox, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(
        SpringLayout.WEST,
        SuperRunecraftPotionCheckbox,
        50,
        SpringLayout.EAST,
        ZamorakPotionCheckbox); // Increased space

    sl.putConstraint(
        SpringLayout.NORTH,
        RunecraftPotionCheckbox,
        4,
        SpringLayout.SOUTH,
        SuperRunecraftPotionCheckbox);
    sl.putConstraint(
        SpringLayout.WEST,
        RunecraftPotionCheckbox,
        50,
        SpringLayout.EAST,
        ZamorakPotionCheckbox); // Increased space

    sl.putConstraint(
        SpringLayout.NORTH, MagicPotionCheckbox, 4, SpringLayout.SOUTH, RunecraftPotionCheckbox);
    sl.putConstraint(
        SpringLayout.WEST,
        MagicPotionCheckbox,
        50,
        SpringLayout.EAST,
        ZamorakPotionCheckbox); // Increased space

    sl.putConstraint(
        SpringLayout.NORTH, SaradominPotionCheckbox, 4, SpringLayout.SOUTH, MagicPotionCheckbox);
    sl.putConstraint(
        SpringLayout.WEST,
        SaradominPotionCheckbox,
        50,
        SpringLayout.EAST,
        ZamorakPotionCheckbox); // Increased space

    sl.putConstraint(
        SpringLayout.NORTH,
        SuperRangingPotionCheckbox,
        4,
        SpringLayout.SOUTH,
        SaradominPotionCheckbox);
    sl.putConstraint(
        SpringLayout.WEST,
        SuperRangingPotionCheckbox,
        50,
        SpringLayout.EAST,
        ZamorakPotionCheckbox); // Increased space

    sl.putConstraint(
        SpringLayout.NORTH,
        SuperMagicPotionCheckbox,
        4,
        SpringLayout.SOUTH,
        SuperRangingPotionCheckbox);
    sl.putConstraint(
        SpringLayout.WEST,
        SuperMagicPotionCheckbox,
        50,
        SpringLayout.EAST,
        ZamorakPotionCheckbox); // Increased space

    // BOTTOM - START BUTTON

    sl.putConstraint(SpringLayout.NORTH, startBtn, -24, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.SOUTH, startBtn, -4, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.WEST, startBtn, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, startBtn, -4, SpringLayout.EAST, panel);
  }

  public void setTheming() {
    frame.getContentPane().setBackground(Main.primaryBG);
    frame.getContentPane().setForeground(Main.primaryFG);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setPreferredSize(new Dimension(380, 234));
    panel.setBackground(Main.primaryBG);
    panel.setForeground(Main.primaryFG);
    strengthPotionCheckbox.setForeground(Main.primaryFG);
    strengthPotionCheckbox.setBackground(Main.primaryBG);
    attackPotionCheckbox.setForeground(Main.primaryFG);
    attackPotionCheckbox.setBackground(Main.primaryBG);
    statRestorePotionCheckbox.setForeground(Main.primaryFG);
    statRestorePotionCheckbox.setBackground(Main.primaryBG);
    DefensePotionCheckbox.setForeground(Main.primaryFG);
    DefensePotionCheckbox.setBackground(Main.primaryBG);
    RestorePrayerPotionCheckbox.setForeground(Main.primaryFG);
    RestorePrayerPotionCheckbox.setBackground(Main.primaryBG);
    SuperAttackPotionCheckbox.setForeground(Main.primaryFG);
    SuperAttackPotionCheckbox.setBackground(Main.primaryBG);
    FishingPotionCheckbox.setForeground(Main.primaryFG);
    FishingPotionCheckbox.setBackground(Main.primaryBG);
    SuperStrengthPotionCheckbox.setForeground(Main.primaryFG);
    SuperStrengthPotionCheckbox.setBackground(Main.primaryBG);
    SuperDefensePotionCheckbox.setForeground(Main.primaryFG);
    SuperDefensePotionCheckbox.setBackground(Main.primaryBG);
    RangingPotionCheckbox.setForeground(Main.primaryFG);
    RangingPotionCheckbox.setBackground(Main.primaryBG);
    CurePoisonPotionCheckbox.setForeground(Main.primaryFG);
    CurePoisonPotionCheckbox.setBackground(Main.primaryBG);
    ZamorakPotionCheckbox.setForeground(Main.primaryFG);
    ZamorakPotionCheckbox.setBackground(Main.primaryBG);
    SuperRunecraftPotionCheckbox.setForeground(Main.primaryFG);
    SuperRunecraftPotionCheckbox.setBackground(Main.primaryBG);
    RunecraftPotionCheckbox.setForeground(Main.primaryFG);
    RunecraftPotionCheckbox.setBackground(Main.primaryBG);
    MagicPotionCheckbox.setForeground(Main.primaryFG);
    MagicPotionCheckbox.setBackground(Main.primaryBG);
    SaradominPotionCheckbox.setForeground(Main.primaryFG);
    SaradominPotionCheckbox.setBackground(Main.primaryBG);
    SuperRangingPotionCheckbox.setForeground(Main.primaryFG);
    SuperRangingPotionCheckbox.setBackground(Main.primaryBG);
    SuperMagicPotionCheckbox.setForeground(Main.primaryFG);
    SuperMagicPotionCheckbox.setBackground(Main.primaryBG);
    startBtn.setBackground(Main.secondaryBG);
    startBtn.setForeground(Main.secondaryFG);
  }

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
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    // c.log(Integer.toString((potions.length)));
    // for (int i = 0; i < potions.length; i++) {
    // for (int j = 0; j < potions[i].length; j++) {
    // c.log("array[" + i + "][" + j + "] = " + potions[i][j]);
    // }
    // }

    while (c.isRunning()) {
      c.openBank();
      c.sleep(640);

      if (c.getInventoryItemCount() > 0) {
        c.log("Depositing");
        c.depositAll();
      }
      c.sleep(640);
      for (int[] potion : potions) {
        for (int i : potion) {
          if (c.getBankItemCount(i) > 0) {
            c.withdrawItem(i, c.getBankItemCount(i));
            c.sleep(640);
          }
          if (c.getInventoryItemCount() == 30) {
            break;
          }
        }
      }
      c.closeBank();
      if (c.getInventoryItemCount() == 0) {
        return;
      }
      int inventoryPotions = c.getInventoryItemCount();
      while (c.isRunning()
          && c.getInventoryItemCount(ItemId.EMPTY_VIAL.getId()) < inventoryPotions) {
        c.itemCommand(c.getInventorySlotItemId(0));
        c.sleep(640);
      }
    }
  }

  private void setValuesFromGUI(
      JCheckBox strengthPotionCheckbox,
      JCheckBox attackPotionCheckbox,
      JCheckBox statRestorePotionCheckbox,
      JCheckBox DefensePotionCheckbox,
      JCheckBox RestorePrayerPotionCheckbox,
      JCheckBox SuperAttackPotionCheckbox,
      JCheckBox FishingPotionCheckbox,
      JCheckBox SuperStrengthPotionCheckbox,
      JCheckBox SuperDefensePotionCheckbox,
      JCheckBox RangingPotionCheckbox,
      JCheckBox CurePoisonPotionCheckbox,
      JCheckBox ZamorakPotionCheckbox,
      JCheckBox SuperRunecraftPotionCheckbox,
      JCheckBox RunecraftPotionCheckbox,
      JCheckBox MagicPotionCheckbox,
      JCheckBox SaradominPotionCheckbox,
      JCheckBox SuperRangingPotionCheckbox,
      JCheckBox SuperMagicPotionCheckbox) {

    List<int[]> potionList = new ArrayList<>();

    if (strengthPotionCheckbox.isSelected()) {
      potionList.add(StrengthPotion);
    }
    if (attackPotionCheckbox.isSelected()) {
      potionList.add(AttackPotion);
    }
    if (statRestorePotionCheckbox.isSelected()) {
      potionList.add(StatRestorePotion);
    }
    if (DefensePotionCheckbox.isSelected()) {
      potionList.add(DefensePotion);
    }
    if (RestorePrayerPotionCheckbox.isSelected()) {
      potionList.add(RestorePrayerPotion);
    }
    if (SuperAttackPotionCheckbox.isSelected()) {
      potionList.add(SuperAttackPotion);
    }
    if (FishingPotionCheckbox.isSelected()) {
      potionList.add(FishingPotion);
    }
    if (SuperStrengthPotionCheckbox.isSelected()) {
      potionList.add(SuperStrengthPotion);
    }
    if (SuperDefensePotionCheckbox.isSelected()) {
      potionList.add(SuperDefensePotion);
    }
    if (RangingPotionCheckbox.isSelected()) {
      potionList.add(RangingPotion);
    }
    if (CurePoisonPotionCheckbox.isSelected()) {
      potionList.add(CurePoisonPotion);
    }
    if (ZamorakPotionCheckbox.isSelected()) {
      potionList.add(ZamorakPotion);
    }
    if (SuperRunecraftPotionCheckbox.isSelected()) {
      potionList.add(SuperRunecraftPotion);
    }
    if (RunecraftPotionCheckbox.isSelected()) {
      potionList.add(RunecraftPotion);
    }
    if (MagicPotionCheckbox.isSelected()) {
      potionList.add(MagicPotion);
    }
    if (SaradominPotionCheckbox.isSelected()) {
      potionList.add(SaradominPotion);
    }
    if (SuperRangingPotionCheckbox.isSelected()) {
      potionList.add(SuperRangingPotion);
    }
    if (SuperMagicPotionCheckbox.isSelected()) {
      potionList.add(SuperMagicPotion);
    }
    potions = potionList.toArray(new int[0][0]);
  }
}
