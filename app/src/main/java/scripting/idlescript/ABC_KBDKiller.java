package scripting.idlescript;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.NpcId;
import orsc.ORSCharacter;

// made by abcde updated by dahun

public class ABC_KBDKiller extends IdleScript {
  public static ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.COMBAT, Category.IRONMAN_SUPPORTED},
          "abcde",
          "A King Black Dragon fighting script");

  // ================================================================================
  // ********************************************************************************
  private static class Coordinate {
    public int x;
    public int y;

    public Coordinate(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  // ================================================================================
  public enum EState {
    Bank,
    WalkToEdgevilleDungeon,
    TeleportToWilderness,
    WalkToKBD,
    PrepareAtKBD,
    FightKBD,
    ReturnToBank
  }

  public enum EOutcome {
    SUCCESS,
    FAILURE, // Note that for actions with multiple attempts (via RunAction), failure only means
    // failure of the current attempt
    STOP
  }

  // --------------------------------------------------------------------------------
  private static final int DRAGON_ID = NpcId.KING_BLACK_DRAGON.getId();
  private static final int ACTION_ATTEMPTS = 5;

  private static final int[] ANTI_POISON_POTIONS = {
    ItemId.POISON_ANTIDOTE_1DOSE.getId(),
    ItemId.POISON_ANTIDOTE_2DOSE.getId(),
    ItemId.POISON_ANTIDOTE_3DOSE.getId(),
    ItemId.CURE_POISON_POTION_1DOSE.getId(),
    ItemId.CURE_POISON_POTION_2DOSE.getId(),
    ItemId.CURE_POISON_POTION_3DOSE.getId()
  };

  private static final int[] ATTACK_POTIONS = {
    ItemId.SUPER_ATTACK_POTION_1DOSE.getId(),
    ItemId.SUPER_ATTACK_POTION_2DOSE.getId(),
    ItemId.SUPER_ATTACK_POTION_3DOSE.getId()
  };

  private static final int[] STRENGTH_POTIONS = {
    ItemId.SUPER_STRENGTH_POTION_1DOSE.getId(),
    ItemId.SUPER_STRENGTH_POTION_2DOSE.getId(),
    ItemId.SUPER_STRENGTH_POTION_3DOSE.getId()
  };

  private static final int[] DEFENCE_POTIONS = {
    ItemId.SUPER_DEFENSE_POTION_1DOSE.getId(),
    ItemId.SUPER_DEFENSE_POTION_2DOSE.getId(),
    ItemId.SUPER_DEFENSE_POTION_3DOSE.getId()
  };

  private static final int[] POISON_ANTIDOTES = {
    ItemId.POISON_ANTIDOTE_1DOSE.getId(),
    ItemId.POISON_ANTIDOTE_2DOSE.getId(),
    ItemId.POISON_ANTIDOTE_3DOSE.getId()
  };

  private static final int[] CURE_POISON_POTIONS = {
    ItemId.CURE_POISON_POTION_1DOSE.getId(),
    ItemId.CURE_POISON_POTION_2DOSE.getId(),
    ItemId.CURE_POISON_POTION_3DOSE.getId()
  };

  private static final Coordinate[] KBD_PATH = {
    new Coordinate(325, 212),
    // new Coordinate(320, 208),
    // new Coordinate(315, 205),
    new Coordinate(310, 200),
    // new Coordinate(305, 195),
    // new Coordinate(300, 195),
    new Coordinate(294, 192),
    // new Coordinate(289, 187),
    new Coordinate(285, 185)
  };

  private static final int[] LOOT = {
    ItemId.DRAGON_2_HANDED_SWORD.getId(),
    ItemId.KING_BLACK_DRAGON_SCALE.getId(),
    ItemId.DRAGON_BAR.getId(),
    ItemId.CHIPPED_DRAGON_SCALE.getId(),
    ItemId.DRAGON_METAL_CHAIN.getId(),
    ItemId.DRAGON_SCALE_MAIL.getId(),
    ItemId.RING_OF_WEALTH.getId(),
    ItemId.ATTACK_CAPE.getId(),
    ItemId.STRENGTH_CAPE.getId(),
    ItemId.DRAGON_MEDIUM_HELMET.getId(),
    ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.getId(),
    ItemId.DRAGONSTONE_AMULET.getId(),
    ItemId.DRAGONSTONE.getId(),
    ItemId.UNCUT_DRAGONSTONE.getId(),
    ItemId.DRAGON_BONES.getId(),
    ItemId.TOOTH_HALF_KEY.getId(),
    ItemId.LOOP_HALF_KEY.getId(),
    ItemId.RUNE_2_HANDED_SWORD.getId(),
    ItemId.RUNE_BATTLE_AXE.getId(),
    ItemId.RUNE_KITE_SHIELD.getId(),
    ItemId.RUNE_SQUARE_SHIELD.getId(),
    ItemId.RUNE_PLATE_MAIL_LEGS.getId(),
    ItemId.RUNE_CHAIN_MAIL_BODY.getId(),
    ItemId.LARGE_RUNE_HELMET.getId(),
    ItemId.RUNE_LONG_SWORD.getId(),
    ItemId.RUNE_SCIMITAR.getId(),
    ItemId.RUNE_AXE.getId(),
    ItemId.MEDIUM_RUNE_HELMET.getId(),
    ItemId.DIAMOND.getId(),
    ItemId.UNCUT_DIAMOND.getId(),
    ItemId.SHARK.getId(),
    ItemId.ADAMANTITE_PLATE_MAIL_BODY.getId(),
    ItemId.ADAMANTITE_AXE.getId(),
    ItemId.RUBY_AMULET_OF_STRENGTH.getId(),
    ItemId.RUBY.getId(),
    ItemId.UNCUT_RUBY.getId(),
    ItemId.MITHRIL_BATTLE_AXE.getId(),
    ItemId.EMERALD.getId(),
    ItemId.UNCUT_EMERALD.getId(),
    ItemId.BLOOD_RUNE.getId(),
    ItemId.DEATH_RUNE.getId(),
    ItemId.LIFE_RUNE.getId(),
    ItemId.LAW_RUNE.getId(),
    ItemId.NATURE_RUNE.getId(),
    ItemId.CHAOS_RUNE.getId(),
    ItemId.BODY_RUNE.getId(),
    ItemId.MIND_RUNE.getId(),
    ItemId.EARTH_RUNE.getId(),
    ItemId.AIR_RUNE.getId(),
    ItemId.WATER_RUNE.getId(),
    ItemId.FIRE_RUNE.getId(),
    ItemId.RUNITE_BAR.getId(),
    ItemId.RUNITE_ORE.getId(),
    ItemId.ADAMANTITE_BAR.getId(),
    ItemId.COAL_CERTIFICATE.getId(),
    ItemId.IRON_ORE_CERTIFICATE.getId(),
    ItemId.YEW_LOGS_CERTIFICATE.getId(),
    ItemId.IRON_ARROWS.getId(),
    ItemId.BRONZE_ARROWS.getId()
  };

  // ================================================================================
  private JFrame mFrame = null;

  private boolean mInitialised = false;
  private boolean mSwapWeapons = true;
  private boolean mDrinkDuringWait = false;
  private boolean mLogAttacks = false;
  private int mHealHits = 40;
  private int mMinimumHitsToHeal = 15;

  private EState mState = EState.Bank;

  private int mHitCount = 0;
  private boolean mCuredPoison = false;

  private int mSurprisesCount = 0;
  private int mDragon2HCount = 0;
  private int mDragonScaleCount = 0;
  private int mDragonMediumCount = 0;
  private int mDragonLeftShieldCount = 0;
  private int mDragonstoneAmuletCount = 0;
  private int mDragonstonesCount = 0;
  private int mDragonBonesCount = 0;
  private int mKeyToothCount = 0;
  private int mKeyLoopCount = 0;
  private int mRuniteCount = 0;

  // INITIALISATION ================================================================================
  // --------------------------------------------------------------------------------
  private void ShowInitialisationUI() {
    // Widgets
    JCheckBox checkSwapWeapons = new JCheckBox("Swap weapons", true);
    JCheckBox checkDrinkDuringWait = new JCheckBox("Drink during wait", false);
    JCheckBox checkLogAttacks = new JCheckBox("Log attacks received", true);
    JLabel labelHealHits = new JLabel("Remaining hits to heal at");
    JTextField textHealHits = new JTextField("30");
    JLabel labelMinimumHitsToHeal = new JLabel("Minimum hits to heal");
    JTextField textMinimumHitsToHeal = new JTextField("15");
    JButton buttonStart = new JButton("Start");

    // Listeners
    buttonStart.addActionListener(
        (e) -> {
          // Settings
          mSwapWeapons = checkSwapWeapons.isSelected();
          mDrinkDuringWait = checkDrinkDuringWait.isSelected();
          mLogAttacks = checkLogAttacks.isSelected();
          try {
            mHealHits = Integer.parseInt(textHealHits.getText().replace(" ", ""));
          } catch (Exception ex) {
            controller.log("Remaining hits to heal at must be an integer.");
          }
          if (mHealHits < 30) mHealHits = 30;
          if (mHealHits >= 99) mHealHits = 98;
          try {
            mMinimumHitsToHeal = Integer.parseInt(textMinimumHitsToHeal.getText().replace(" ", ""));
          } catch (Exception ex) {
            controller.log("Minimum hits to heal must be an integer.");
          }
          if (mMinimumHitsToHeal < 1) mMinimumHitsToHeal = 1;
          if (mMinimumHitsToHeal >= 30) mMinimumHitsToHeal = 30;
          mInitialised = true;

          // Close
          mFrame.setVisible(false);
          mFrame.dispose();
        });

    // Frame
    mFrame = new JFrame("KBD Killer Options (" + controller.getPlayerName() + ")");
    mFrame.setLayout(new GridLayout(0, 1));
    mFrame.add(checkSwapWeapons);
    mFrame.add(checkDrinkDuringWait);
    mFrame.add(checkLogAttacks);
    mFrame.add(labelHealHits);
    mFrame.add(textHealHits);
    mFrame.add(labelMinimumHitsToHeal);
    mFrame.add(textMinimumHitsToHeal);
    mFrame.add(buttonStart);
    mFrame.pack();
    mFrame.setLocationRelativeTo(null);
    mFrame.setVisible(true);
    mFrame.requestFocusInWindow();
  }

  // EVENTS ================================================================================
  // --------------------------------------------------------------------------------
  @Override
  public int start(String[] parameters) {
    // Initialise
    if (mFrame == null) ShowInitialisationUI();
    if (!mInitialised) return 1000;

    // Heading
    controller.log("@or3@KBD Killer 1.2");

    // Checks
    if (controller.getBaseStat(controller.getStatId("Magic")) < 25) {
      controller.displayMessage("@red@Magic must be 25+ for Varrock teleportation.");
      return Stop();
    }
    if (!controller.isItemIdEquipped(ItemId.ANTI_DRAGON_BREATH_SHIELD.getId())) {
      controller.displayMessage("@red@Please equip an anti dragon breath shield.");
      return Stop();
    }
    if (!AtVarrockWestBank() && !AtKBDLair()) {
      controller.displayMessage("@red@Please start at the Varrock west bank or the KBD lair.");
      return Stop();
    }

    // Information text
    controller.log("@or2@Requirements:");
    controller.log(
        "@or2@1. Must have paid 200k to Scot Ruth in Edgeville dungeon to unlock wilderness teleport.");
    controller.log("@or2@2. Have cooked Sharks and anti poisons or poison antidotes.");
    controller.log("@or2@3. Have runes for Varrock teleport.");

    // Run
    return Run();
  }

  // --------------------------------------------------------------------------------
  @Override
  public void paintInterrupt() {
    // Checks
    if (controller == null) return;

    // HUD
    int x = 350 + controller.getMud().getGameWidth() - 512;
    controller.drawBoxAlpha(x - 4, 35, 163, 160, 0xFF0000, 48);
    controller.drawString("@dre@KBD Killer", x, 48, 0xFFFFFF, 1);
    controller.drawString("@whi@Surprises: @yel@" + mSurprisesCount, x, 62, 0xFFFFFF, 1);
    controller.drawString("@whi@Dragon 2H: @yel@" + mDragon2HCount, x, 76, 0xFFFFFF, 1);
    controller.drawString("@whi@Dragon Scale: @yel@" + mDragonScaleCount, x, 90, 0xFFFFFF, 1);
    controller.drawString("@whi@Dragon Medium: @yel@" + mDragonMediumCount, x, 104, 0xFFFFFF, 1);
    controller.drawString(
        "@whi@Dragon Left Shield: @yel@" + mDragonLeftShieldCount, x, 118, 0xFFFFFF, 1);
    //   controller.drawString(
    //       "@whi@Dragonstone Amulet: @yel@" + mDragonstoneAmuletCount, x, 132, 0xFFFFFF, 1);
    controller.drawString("@whi@Dragonstones: @yel@" + mDragonstonesCount, x, 132, 0xFFFFFF, 1);
    controller.drawString("@whi@Dragon Bones: @yel@" + mDragonBonesCount, x, 146, 0xFFFFFF, 1);
    controller.drawString("@whi@Runite: @yel@" + mRuniteCount, x, 160, 0xFFFFFF, 1);
    controller.drawString("@whi@Key Tooth: @yel@" + mKeyToothCount, x, 174, 0xFFFFFF, 1);
    controller.drawString("@whi@Key Loop: @yel@" + mKeyLoopCount, x, 188, 0xFFFFFF, 1);
  }

  // --------------------------------------------------------------------------------

  // Reset any changing static variables here so they're correct for the next run
  @Override
  public void cleanup() {}

  @Override
  public void playerDamagedInterrupt(int currentHealth, int damageAmount) {
    // Hit count
    ++mHitCount;

    // Logging
    if (mLogAttacks) {
      // Output
      String output = "Hit for " + damageAmount + " ";
      boolean first = true;

      // Nearby players
      List<ORSCharacter> nearbyPlayers = controller.getPlayers();
      for (ORSCharacter p : nearbyPlayers) {
        if (p != controller.getPlayer()) {
          int distance =
              controller.distance(
                  controller.getMud().localPlayer.currentX,
                  controller.getMud().localPlayer.currentZ,
                  p.currentX,
                  p.currentZ);
          if (distance <= 512) {
            output += (first ? "" : " ") + "[p:" + p.accountName + ":" + (distance / 128) + "]";
            if (first) first = false;
          }
        }
      }

      // Nearby npcs
      List<ORSCharacter> nearbyNPCs = controller.getNpcs();
      for (ORSCharacter n : nearbyNPCs) {
        int distance =
            controller.distance(
                controller.getMud().localPlayer.currentX,
                controller.getMud().localPlayer.currentZ,
                n.currentX,
                n.currentZ);
        if (distance <= 512) {
          output +=
              (first ? "" : " ")
                  + "[n:"
                  + controller.getNpcName(n.npcId)
                  + ":"
                  + (distance / 128)
                  + "]";
          if (first) first = false;
        }
      }

      // Log
      Main.log(output);
    }
  }

  // MAIN ================================================================================
  // --------------------------------------------------------------------------------
  private int Run() {
    // Initial state
    mState = EState.Bank;
    if (AtKBDLair()) mState = EState.FightKBD;

    // Run
    while (controller.isRunning()) {
      // States
      EOutcome outcome = EOutcome.FAILURE;
      switch (mState) {
        case Bank:
          outcome = Bank();
          break;
        case WalkToEdgevilleDungeon:
          outcome = WalkToEdgevilleDungeon();
          break;
        case TeleportToWilderness:
          outcome = TeleportToWilderness();
          break;
        case WalkToKBD:
          outcome = WalkToKBD();
          break;
        case PrepareAtKBD:
          outcome = PrepareAtKBD();
          break;
        case FightKBD:
          outcome = FightKBD();
          break;
        case ReturnToBank:
          outcome = ReturnToBank();
          break;
      }

      // Transitions
      if (outcome == EOutcome.SUCCESS) {
        switch (mState) {
          case Bank:
            mState = EState.WalkToEdgevilleDungeon;
            break;
          case WalkToEdgevilleDungeon:
            mState = EState.TeleportToWilderness;
            break;
          case TeleportToWilderness:
            mState = EState.WalkToKBD;
            break;
          case WalkToKBD:
            mState = EState.PrepareAtKBD;
            break;
          case PrepareAtKBD:
            mState = EState.FightKBD;
            break;
          case FightKBD:
            mState = EState.ReturnToBank;
            break;
          case ReturnToBank:
            mState = EState.Bank;
            break;
        }
      } else if (outcome == EOutcome.FAILURE || outcome == EOutcome.STOP) {
        if (mState == EState.PrepareAtKBD || mState == EState.FightKBD) TeleportToVarrock();
        return Stop();
      }
    }

    // Stop
    return Stop();
  }

  // STATE ================================================================================
  // --------------------------------------------------------------------------------
  private int Stop(boolean logout) {
    controller.log("@dre@KBD Killer Stopped.");
    if (logout) {
      controller.setAutoLogin(false);
      controller.logout();
    }
    controller.stop();
    return 0;
  }

  // --------------------------------------------------------------------------------
  private int Stop() {
    return Stop(false);
  }

  // SEQUENCES ================================================================================
  // --------------------------------------------------------------------------------
  private EOutcome Bank() {
    // Checks
    if (!AtVarrockWestBank()) return EOutcome.FAILURE;

    // Equip anti dragon breath shield (to avoid banking it)
    if (mSwapWeapons && !controller.isItemIdEquipped(ItemId.ANTI_DRAGON_BREATH_SHIELD.getId())) {
      controller.equipItem(
          controller.getInventoryItemSlotIndex(ItemId.ANTI_DRAGON_BREATH_SHIELD.getId()));
      controller.sleep(1000);
    }

    // Open
    controller.openBank();
    // if (!controller.isInBank()) return EOutcome.FAILURE;

    // Deposit rune kite shield (to stop it from falsifying the tallies)
    if (mSwapWeapons && controller.getInventoryItemCount(ItemId.RUNE_KITE_SHIELD.getId()) > 0) {
      controller.depositItem(ItemId.RUNE_KITE_SHIELD.getId(), 1);
      controller.sleep(1000);
    }

    // Tallies
    mSurprisesCount += InventoryCount(1365, 1366, 1367, 1368, 1318, 1374, 1381);
    mDragon2HCount += InventoryCount(1346);
    mDragonScaleCount += InventoryCount(1347);
    mDragonMediumCount += InventoryCount(ItemId.DRAGON_MEDIUM_HELMET.getId());
    mDragonLeftShieldCount += InventoryCount(ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.getId());
    mDragonstoneAmuletCount += InventoryCount(ItemId.DRAGONSTONE_AMULET.getId());
    mDragonstonesCount +=
        InventoryCount(ItemId.DRAGONSTONE.getId(), ItemId.UNCUT_DRAGONSTONE.getId());
    mDragonBonesCount += InventoryCount(ItemId.DRAGON_BONES.getId());
    mKeyToothCount += InventoryCount(ItemId.TOOTH_HALF_KEY.getId());
    mKeyLoopCount += InventoryCount(ItemId.LOOP_HALF_KEY.getId());
    mRuniteCount +=
        InventoryCount(
            ItemId.RUNE_2_HANDED_SWORD.getId(),
            ItemId.RUNE_BATTLE_AXE.getId(),
            ItemId.RUNE_KITE_SHIELD.getId(),
            ItemId.RUNE_SQUARE_SHIELD.getId(),
            ItemId.RUNE_PLATE_MAIL_LEGS.getId(),
            ItemId.RUNE_CHAIN_MAIL_BODY.getId(),
            ItemId.LARGE_RUNE_HELMET.getId(),
            ItemId.RUNE_LONG_SWORD.getId(),
            ItemId.RUNE_SCIMITAR.getId(),
            ItemId.RUNE_AXE.getId(),
            ItemId.MEDIUM_RUNE_HELMET.getId());

    // Deposit all
    DepositAll();

    // Withdraw equipment
    if (mSwapWeapons) {
      if (controller.getBankItemCount(ItemId.RUNE_KITE_SHIELD.getId()) > 0)
        controller.withdrawItem(ItemId.RUNE_KITE_SHIELD.getId());
    }
    controller.sleep(640);
    // Withdraw runes
    if (controller.getBankItemCount(ItemId.FIRE_RUNE.getId()) < 1
        || controller.getBankItemCount(ItemId.AIR_RUNE.getId()) < 3
        || controller.getBankItemCount(ItemId.LAW_RUNE.getId()) < 1) {
      controller.closeBank();
      controller.log("@or3@Stopping: out of runes for Varrock teleport.");
      return EOutcome.STOP;
    }
    controller.withdrawItem(ItemId.FIRE_RUNE.getId(), 1);
    controller.sleep(640);
    controller.withdrawItem(ItemId.AIR_RUNE.getId(), 3);
    controller.sleep(640);
    controller.withdrawItem(ItemId.LAW_RUNE.getId(), 1);
    controller.sleep(640);
    //   Withdraw potions
    if (!WithdrawFirstOf(ANTI_POISON_POTIONS)) {
      controller.closeBank();
      controller.log("@or3@Stopping: out of anti-poison potions.");
      return EOutcome.STOP;
    }
    controller.sleep(640);
    if (!WithdrawFirstOf(ATTACK_POTIONS)) controller.log("@or3@Warning: out of attack potions.");
    controller.sleep(640);
    if (!WithdrawFirstOf(STRENGTH_POTIONS))
      controller.log("@or3@Warning: out of strength potions.");
    controller.sleep(640);
    if (!WithdrawFirstOf(DEFENCE_POTIONS)) controller.log("@or3@Warning: out of defence potions.");

    // potions.");
    controller.sleep(640);
    // Withdraw food
    //    if (controller.getBankItemCount(ItemId.SHARK.getId()) < 25) {
    //      controller.closeBank();
    //      controller.log("@or3@Stopping: out of SHARKs.");
    //     return EOutcome.STOP;
    //    }
    //    int SHARKCount = 30 - controller.getInventoryItemCount();
    inventoryItemCheck(ItemId.FIRE_RUNE.getId(), 1);
    inventoryItemCheck(ItemId.AIR_RUNE.getId(), 3);
    inventoryItemCheck(ItemId.LAW_RUNE.getId(), 1);
    controller.withdrawItem(ItemId.SHARK.getId(), 30);
    controller.sleep(1000);
    controller.closeBank();

    // Eat
    Eat();

    // Open
    controller.openBank();
    controller.withdrawItem(
        ItemId.SHARK.getId(),
        30); // The amount argument is treated as the desired amount in the inventory, not
    // the amount tt withdraw!
    controller.closeBank();

    // Success
    return EOutcome.SUCCESS;
  }

  // --------------------------------------------------------------------------------
  private EOutcome WalkToEdgevilleDungeon() {
    // Checks
    if (!controller.isRunning()) return EOutcome.FAILURE;

    // Walk
    controller.setStatus("@yel@Walking to Edgeville dungeon");
    controller.walkTo(157, 510);
    controller.walkTo(167, 510);
    controller.walkTo(177, 510);
    controller.walkTo(186, 510);
    controller.walkTo(197, 511);
    controller.walkTo(207, 512);
    controller.walkTo(215, 512);
    controller.walkTo(215, 502);
    controller.walkTo(215, 492);
    controller.walkTo(215, 482);
    controller.walkTo(221, 471);
    controller.walkTo(213, 470);
    controller.walkTo(215, 467);
    controller.atObject(215, 468);
    controller.sleep(1000);

    // Success
    return EOutcome.SUCCESS;
  }

  // --------------------------------------------------------------------------------
  private EOutcome TeleportToWilderness() {
    // Checks
    if (!controller.isRunning()) return EOutcome.FAILURE;

    // Walk to wilderness door
    controller.setStatus("@yel@Walking to wilderness door");
    controller.walkTo(218, 3282);

    // Wilderness push door
    EOutcome outcome =
        RunAction(
            "Using wilderness door",
            () -> {
              if (X() == 218 && Y() == 3282 && controller.getWallObjectIdAtCoord(219, 3282) == 22) {
                controller.atWallObject(219, 3282);
                controller.sleep(2000);
              } else controller.sleep(5000); // Wait for the push wall to close
              return X() == 219 && Y() == 3282 ? EOutcome.SUCCESS : EOutcome.FAILURE;
            });
    if (outcome == EOutcome.FAILURE) return outcome;

    // Teleport to wilderness
    if (!controller.isRunning()) return EOutcome.FAILURE;
    controller.setStatus("@yel@Teleporting to wilderness");
    controller.atObject(223, 3281);
    controller.sleep(3000);

    // Success
    return EOutcome.SUCCESS;
  }

  // --------------------------------------------------------------------------------
  private EOutcome WalkToKBD() {
    // Checks
    if (!controller.isRunning()) return EOutcome.FAILURE;

    // Walk to gate
    controller.setStatus("@yel@Walking to KBD gate");
    for (Coordinate c : KBD_PATH) {
      while (X() != c.x && Y() != c.y) {
        if (!controller.isRunning()) return EOutcome.FAILURE;
        Eat();
        controller.walkToAsync(c.x, c.y, 0);
        controller.sleep(250);
      }
    }

    // Drink antidote
    controller.setStatus("@yel@Using poison antidote");
    mCuredPoison = UseFirstOf(POISON_ANTIDOTES);

    // Open gate
    EOutcome outcome =
        RunAction(
            "Opening KBD gate",
            () -> {
              if (X() == 285 && Y() == 185 && controller.getObjectAtCoord(285, 185) == 508) {
                controller.atObject(285, 185);
                controller.sleep(2000);
              } else controller.sleep(2500); // Wait for the gate to close
              return X() == 284 && Y() == 185 ? EOutcome.SUCCESS : EOutcome.FAILURE;
            });
    if (outcome == EOutcome.FAILURE) return outcome;

    // Down the ladder
    outcome =
        RunAction(
            "Climbing ladder",
            30,
            () -> {
              controller.walkTo(283, 185);
              Eat();
              controller.atObject(282, 185);
              controller.sleep(1000);
              return X() >= 279 && X() <= 283 && Y() >= 3014 && Y() <= 3020
                  ? EOutcome.SUCCESS
                  : EOutcome.FAILURE;
            });
    if (outcome == EOutcome.FAILURE) return outcome;

    // Start counting poison spider hits
    mHitCount = 0;

    // Pull the lever
    outcome =
        RunAction(
            "Pulling lever",
            30,
            () -> {
              controller.walkTo(282, 3020);
              controller.atObject(282, 3020);
              controller.sleep(1000);
              return X() == 567 && Y() == 3330 ? EOutcome.SUCCESS : EOutcome.FAILURE;
            });
    if (outcome == EOutcome.FAILURE) return outcome;

    // Walk down
    controller.setStatus("@yel@Walking down");
    controller.walkTo(567, 3331);
    controller.sleep(500);

    // Success
    return EOutcome.SUCCESS;
  }

  // --------------------------------------------------------------------------------
  private EOutcome PrepareAtKBD() {
    // Poison spider hit count
    if (mHitCount == 0) mCuredPoison = true;

    // Drink poison cure (leave combat in case KBD ambushes us)
    if (!mCuredPoison && LeaveCombat(5)) {
      controller.setStatus("@yel@Using cure poison");
      mCuredPoison = UseFirstOf(CURE_POISON_POTIONS);
      Eat();
    }

    // Check poison
    if (!mCuredPoison) {
      controller.log("@or3@Stopping: failed to cure poison.");
      return EOutcome.FAILURE;
    }

    // Drink potion
    if (LeaveCombat()) {
      controller.setStatus("@yel@Drinking");
      DrinkPotions(20);
      Eat();
    }

    // Success
    return EOutcome.SUCCESS;
  }

  // --------------------------------------------------------------------------------
  private EOutcome FightKBD() {
    // Variables
    boolean wasInCombat = false;
    boolean kbdWasAlive = false;

    // Fight
    while (controller.isRunning()) {
      // Flee
      if (Hits() <= mHealHits) {
        // Status
        controller.setStatus("@yel@Healing");

        // Out of food
        if (FoodCount() == 0 /*Hits() + TotalFoodHits() < BaseHits()*/) {
          boolean foundLoot = Loot(LOOT);
          controller.log("@or3@Returning to Varrock: out of food.");
          return EOutcome.SUCCESS;
        }

        // Equip anti dragon breath shield (done before fleeing as otherwise it leaves less time for
        // eating)
        if (mSwapWeapons && controller.isInCombat())
          controller.equipItem(
              controller.getInventoryItemSlotIndex(ItemId.ANTI_DRAGON_BREATH_SHIELD.getId()));

        // Leave combat
        if (!LeaveCombat()) {
          controller.log("@or3@Returning to Varrock: failed to escape when in need of healing.");
          return EOutcome.SUCCESS;
        }
      }

      // Fight / loot
      if (!controller.isInCombat()) {
        // Top up
        Eat();
        if (Hits() <= mHealHits) {
          // Status
          controller.setStatus("@yel@Healing");

          // Out of food
          if (FoodCount() == 0 /*Hits() + TotalFoodHits() < BaseHits()*/) {
            boolean foundLoot = Loot(LOOT);
            controller.log("@or3@Returning to Varrock: out of food.");
            return EOutcome.SUCCESS;
          }
        }
        // KBD
        ORSCharacter kbd = controller.getNearestNpcById(DRAGON_ID, true);
        if (kbd != null) {
          // Equip anti dragon breath shield
          if (mSwapWeapons
              && !controller.isItemIdEquipped(ItemId.ANTI_DRAGON_BREATH_SHIELD.getId()))
            controller.equipItem(
                controller.getInventoryItemSlotIndex(ItemId.ANTI_DRAGON_BREATH_SHIELD.getId()));

          // Attack
          controller.setStatus("@ora@Attacking");
          controller.attackNpc(kbd.serverIndex);
          controller.sleep(
              2000); // Important that this is long enough to let an equip item complete, otherwise
          // it might get stuck swapping back and forth
        } else {
          // Equip anti dragon breath shield
          if (mSwapWeapons
              && kbdWasAlive
              && !controller.isItemIdEquipped(ItemId.ANTI_DRAGON_BREATH_SHIELD.getId()))
            controller.equipItem(
                controller.getInventoryItemSlotIndex(ItemId.ANTI_DRAGON_BREATH_SHIELD.getId()));

          // Loot
          boolean foundLoot = Loot(LOOT);

          // Drink
          if (mDrinkDuringWait && kbdWasAlive) {
            controller.setStatus("@yel@Drinking");
            DrinkPotions(8);
          }

          // Wait
          controller.setStatus("@yel@Waiting");
          if (X() != 567 || Y() != 3318) controller.walkTo(567, 3318);
        }

        // Tracking
        wasInCombat = false;
        kbdWasAlive = kbd != null;
      } else {
        // Fighting
        controller.setStatus("@ora@Fighting");
        controller.sleep(500);

        // Unequip anti dragon breath shield
        if (mSwapWeapons
            && !wasInCombat
            && controller.isItemIdEquipped(ItemId.ANTI_DRAGON_BREATH_SHIELD.getId())) {
          if (controller.getInventoryItemCount(ItemId.RUNE_KITE_SHIELD.getId()) > 0)
            controller.equipItem(
                controller.getInventoryItemSlotIndex(ItemId.RUNE_KITE_SHIELD.getId()));
        }

        // Tracking
        wasInCombat = true;
        kbdWasAlive = true;
      }
    }

    // Success
    return EOutcome.SUCCESS;
  }

  // --------------------------------------------------------------------------------
  private EOutcome ReturnToBank() {
    // Checks
    if (!controller.isRunning()) return EOutcome.FAILURE;

    // Teleport to Varrock
    TeleportToVarrock();

    // Walk to bank
    controller.setStatus("@yel@Walking to bank");
    controller.walkTo(130, 504);
    controller.walkTo(140, 507);
    controller.walkTo(150, 507);
    controller.openNearbyDoor(
        2); // In case some nasty person closes the door - who would do such a thing?
    controller.walkTo(150, 504);
    controller.sleep(1000);

    // Success
    return EOutcome.SUCCESS;
  }

  // --------------------------------------------------------------------------------
  private void TeleportToVarrock() {
    // Teleport
    controller.setStatus("@yel@Teleporting to Varrock");
    controller.castSpellOnSelf(controller.getSpellIdFromName("Varrock Teleport"));
    controller.sleep(1000);
  }

  // ACTIONS ================================================================================
  // --------------------------------------------------------------------------------
  private EOutcome RunAction(String name, int attempts, IAction action) {
    // Status
    controller.setStatus("@yel@" + name);

    // Attempts
    EOutcome outcome = EOutcome.STOP;
    for (int i = 0; i < attempts; ++i) {
      if (!controller.isRunning()) return EOutcome.FAILURE;
      outcome = action.Action();
      if (outcome == EOutcome.SUCCESS) return outcome;
      else if (outcome == EOutcome.STOP) break;
    }

    // Failure
    controller.log("@red@Action failed: '" + name + "'.");
    return EOutcome.FAILURE;
  }

  // --------------------------------------------------------------------------------
  private EOutcome RunAction(String name, IAction action) {
    return RunAction(name, ACTION_ATTEMPTS, action);
  }

  // LOCATIONS ================================================================================
  // --------------------------------------------------------------------------------
  private boolean AtVarrockWestBank() {
    return X() >= 145 && X() <= 155 && Y() >= 496 && Y() <= 508;
  }

  // --------------------------------------------------------------------------------
  private boolean AtKBDLair() {
    return X() >= 563 && X() <= 571 && Y() >= 3315 && Y() <= 3331;
  }

  // BANKING ================================================================================
  // --------------------------------------------------------------------------------
  private void DepositAll() {
    for (int i : controller.getInventoryItemIds()) {
      controller.depositItem(i, controller.getInventoryItemCount(i));
    }
    controller.sleep(1500);
  }

  // --------------------------------------------------------------------------------
  private boolean WithdrawFirstOf(int... itemIDs) {
    for (int i : itemIDs) {
      if (controller.getBankItemCount(i) > 0) {
        controller.withdrawItem(i);
        controller.sleep(400);
        return true;
      }
    }
    return false;
  }

  // PLAYER ================================================================================
  // --------------------------------------------------------------------------------
  private int X() {
    return controller.currentX();
  }

  private int Y() {
    return controller.currentY();
  }

  // --------------------------------------------------------------------------------
  private int Hits() {
    return controller.getCurrentStat(controller.getStatId("Hits"));
  }

  private int BaseHits() {
    return controller.getBaseStat(controller.getStatId("Hits"));
  }

  // --------------------------------------------------------------------------------
  private int HitsDeficit() {
    return controller.getBaseStat(controller.getStatId("Hits"))
        - controller.getCurrentStat(controller.getStatId("Hits"));
  }

  // COMBAT ================================================================================
  // --------------------------------------------------------------------------------
  private boolean LeaveCombat(int attempts) {
    // Leave combat
    for (int i = 0; i < attempts; ++i) {
      if (controller.isInCombat()) {
        controller.setStatus("@ora@Leaving combat");
        controller.walkTo(controller.currentX(), controller.currentY(), 0, true, true);
        controller.sleep(750);
      }
    }

    // Outcome
    return !controller.isInCombat();
  }

  // --------------------------------------------------------------------------------
  private boolean LeaveCombat() {
    return LeaveCombat(6);
  }

  // ITEM USAGE ================================================================================
  // --------------------------------------------------------------------------------
  private void Eat() {
    int difference = HitsDeficit();
    while (difference >= mMinimumHitsToHeal) {
      if (!controller.itemCommand(ItemId.SHARK.getId())) break;
      controller.sleep(700);
      difference -= 20;
    }
  }

  // --------------------------------------------------------------------------------
  private int FoodCount() {
    return controller.getInventoryItemCount(ItemId.SHARK.getId());
  }

  private int TotalFoodHits() {
    return FoodCount() * 20;
  }

  // --------------------------------------------------------------------------------
  private void DrinkPotions(int threshold) {
    if (controller.getCurrentStat(controller.getStatId("Attack"))
        <= controller.getBaseStat(controller.getStatId("Attack")) + threshold) {
      controller.log("@yel@Drinking attack potion.");
      UseFirstOf(ATTACK_POTIONS);
    }
    if (controller.getCurrentStat(controller.getStatId("Strength"))
        <= controller.getBaseStat(controller.getStatId("Strength")) + threshold) {
      controller.log("@yel@Drinking strength potion.");
      UseFirstOf(STRENGTH_POTIONS);
    }
    if (controller.getCurrentStat(controller.getStatId("Defense"))
        <= controller.getBaseStat(controller.getStatId("Defense")) + threshold) {
      controller.log("@yel@Drinking defence potion.");
      UseFirstOf(DEFENCE_POTIONS);
    }
  }

  // --------------------------------------------------------------------------------
  private boolean UseFirstOf(int... itemIDs) {
    for (int i : itemIDs) {
      if (controller.getInventoryItemCount(i) > 0) {
        controller.itemCommand(i);
        controller.sleep(2000);
        return true;
      }
    }
    return false;
  }

  // LOOTING ================================================================================
  // --------------------------------------------------------------------------------
  private boolean Loot(int... itemIDs) {
    // Loot
    boolean foundLoot = false;
    for (int i : itemIDs) {
      int[] coords = controller.getNearestItemById(i);
      if (coords != null && controller.distance(567, 3319, coords[0], coords[1]) <= 20) {
        controller.setStatus("@gre@Looting");
        foundLoot = true;
        if (controller.getInventoryItemCount() == 30) {
          controller.itemCommand(ItemId.SHARK.getId()); // Eat a SHARK to make space
          controller.sleep(700);
        }
        controller.pickupItem(coords[0], coords[1], i, true, false);
        controller.sleep(700);
      }
    }

    // Return
    return foundLoot;
  }

  // --------------------------------------------------------------------------------
  private int InventoryCount(int... itemIDs) {
    int count = 0;
    for (int i : itemIDs) {
      count += controller.getInventoryItemCount(i);
    }
    return count;
  }

  // ================================================================================
  // ********************************************************************************
  private interface IAction {
    public EOutcome Action();
  }

  private boolean inventoryItemCheck(int itemId, int itemAmount) {
    if (controller.getInventoryItemCount(itemId) < itemAmount) {
      controller.withdrawItem(itemId, itemAmount - controller.getInventoryItemCount(itemId));
    }
    controller.sleep(640);
    return controller.getInventoryItemCount(itemId) >= itemAmount;
  }
}
