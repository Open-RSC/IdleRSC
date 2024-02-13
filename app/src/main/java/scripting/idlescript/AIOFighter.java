package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import orsc.ORSCharacter;

/**
 * This is AIOFighter written for IdleRSC.
 *
 * <p>It is your standard melee/range/mage fighter script.
 *
 * <p>It has the following features:
 *
 * <p>GUI
 *
 * <p>Multiple NPCs
 *
 * <p>Food eating -- logs out when out of food
 *
 * <p>Looting
 *
 * <p>Bone burying (supports all bone types)
 *
 * <p>Maging (even when in melee combat)
 *
 * <p>Ranging (will switch to melee
 *
 * <p>weapon if in combat. Can also pick up arrows.)
 *
 * <p>Anti-wander (will walk back if out of bounds)
 *
 * @author Dvorak
 */
public class AIOFighter extends IdleScript {
  private final Controller c = Main.getController();
  private int fightMode = 2;
  private int maxWander = 3;
  private int eatingHealth = 5;
  private boolean openDoors = false;
  private boolean buryBones = true;
  private boolean prioritizeBones = false;
  private long next_attempt = -1;
  private final long nineMinutesInMillis = 540000L;
  private boolean maging = true;
  private int spellId = 0;
  private boolean ranging = true;
  private int arrowId = -1; // leave -1 to not pickup arrows.
  private int switchId = 81; // weapon to switch to when in combat if ranging.
  private int[] npcIds = {};
  private int[] loot = {}; // feathers
  private final int[] bones = {20, 413, 604, 814};
  private final int[] bowIds = {188, 189, 648, 649, 650, 651, 652, 653, 654, 655, 656, 657, 59, 60};
  private final int[] arrowIds = {
    638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 11, 574, 190, 592, 786
  };
  private final int[] doorObjectIds = {60, 64};
  // do not modify these
  private int currentAttackingNpc = -1;
  private int[] lootTable = null;
  private final int[] startTile = {-1, -1};
  private JFrame scriptFrame;
  private boolean guiSetup = false;
  private boolean scriptStarted = false;
  private final long startTimestamp = System.currentTimeMillis() / 1000L;
  private int bonesBuried = 0;
  private int spellsCasted = 0;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }

    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      next_attempt = System.currentTimeMillis() + 5000L;
      scriptStart();
    }

    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    if (c.isRunning()) {
      lootTable = Arrays.copyOf(loot, loot.length);
      if (prioritizeBones) {
        lootTable = Arrays.copyOf(lootTable, loot.length + bones.length);
        for (int i = loot.length, k = 0; i < loot.length + bones.length; i++, k++) {
          lootTable[i] = bones[k];
        }
      }

      startTile[0] = c.currentX();
      startTile[1] = c.currentY();

      while (c.isRunning()) {
        if (c.getNeedToMove()) c.moveCharacter();
        if (c.getShouldSleep()) c.sleepHandler(true);
        // 0th priority: walking back to starting zone if out of zone
        // 1st priority: setting fightmode
        // 2nd priority: eating
        // 3rd priority: bury any bones in inv
        // 4th priority: pickup loot
        // 5th priority: pickup bones
        // 6th priority: starting a fight via melee or ranging
        // 7th priority: maging

        c.sleep(618); // wait 1 tick

        if (c.isCurrentlyWalking()) {
          next_attempt = System.currentTimeMillis() + nineMinutesInMillis;
        }

        if (!isWithinWander(c.currentX(), c.currentY())) {
          c.setStatus("@red@Out of range! Walking back.");
          c.walkTo(startTile[0], startTile[1], 0, true);
        }
        if (openDoors) {
          for (int doorId : doorObjectIds) {
            int[] doorCoords = c.getNearestObjectById(doorId);

            if (doorCoords != null && this.isWithinWander(doorCoords[0], doorCoords[1])) {
              c.setStatus("@red@Opening door...");
              c.atObject(doorCoords[0], doorCoords[1]);
              c.sleep(5000);
            }
          }
        }

        if (c.getFightMode() != fightMode) {
          c.setStatus("@red@Changing fightmode");
          c.setFightMode(fightMode);
        }

        if (c.getCurrentStat(c.getStatId("Hits")) <= eatingHealth) {
          c.setStatus("@red@Eating food");
          c.walkTo(c.currentX(), c.currentY(), 0, true);

          boolean ate = false;

          for (int id : c.getFoodIds()) {
            if (c.getInventoryItemCount(id) > 0) {
              c.itemCommand(id);
              ate = true;
              break;
            }
          }

          if (!ate) {
            c.setStatus("@red@We ran out of food! Logging out.");
            c.setAutoLogin(false);
            c.logout();
          }

          continue;
        }

        for (int lootId : lootTable) {
          int[] lootCoord = c.getNearestItemById(lootId);
          if (lootCoord != null && this.isWithinWander(lootCoord[0], lootCoord[1])) {
            c.setStatus("@red@Picking up loot");
            c.pickupItem(lootCoord[0], lootCoord[1], lootId, true, false);
            c.sleep(618);
            buryBones();
          }
        }

        if (!c.isInCombat()) {
          if (c.getShouldSleep()) c.sleepHandler(true);
          ORSCharacter npc = c.getNearestNpcByIds(npcIds, false);

          if (ranging) {

            int[] arrowCoord = c.getNearestItemById(arrowId);
            if (arrowCoord != null) {
              c.setStatus("@red@Picking up arrows");
              c.pickupItem(arrowCoord[0], arrowCoord[1], arrowId, false, true);
              continue;
            }

            boolean hasArrows = false;
            for (int id : arrowIds) {
              if (c.getInventoryItemCount(id) > 0 || c.isItemIdEquipped(id)) {
                hasArrows = true;
                break;
              }
            }

            if (!hasArrows) {
              c.setStatus("@red@Out of arrows!");
              c.setAutoLogin(false);
              c.logout();
              c.stop();
            }

            for (int id : bowIds) {
              if (c.getInventoryItemCount(id) > 0) {
                if (!c.isEquipped(c.getInventoryItemSlotIndex(id))) {
                  c.setStatus("@red@Equipping bow");
                  c.equipItem(c.getInventoryItemSlotIndex(id));
                  c.sleep(1000);
                  break;
                }
              }
            }
          }

          // maybe wrap this in a 'while not in combat' loop?
          if (npc != null) {
            if (maging && !ranging) {
              currentAttackingNpc = npc.serverIndex;
              c.castSpellOnNpc(npc.serverIndex, spellId);
            } else {
              c.setStatus("@red@Attacking NPC");
              c.attackNpc(npc.serverIndex);
              c.sleep(1000);
            }

          } else {
            if (!c.isInCombat()) {
              if (buryBones) {
                for (int lootId : bones) {
                  int[] lootCoord = c.getNearestItemById(lootId);
                  if (lootCoord != null && this.isWithinWander(lootCoord[0], lootCoord[1])) {
                    c.setStatus("@red@No NPCs, Picking bones");
                    c.pickupItem(lootCoord[0], lootCoord[1], lootId, true, false);
                    c.sleep(618);
                    buryBones();
                  } else {
                    if (c.currentX() != startTile[0] && c.currentY() != startTile[1]) {
                      c.setStatus("@red@No NPCs, walking back to start...");
                      c.walkToAsync(startTile[0], startTile[1], 0);
                    }
                  }
                }
              } else {
                if (c.currentX() != startTile[0] && c.currentY() != startTile[1]) {
                  c.setStatus("@red@No NPCs found, walking back to start...");
                  c.walkToAsync(startTile[0], startTile[1], 0);
                }
              }
            }
          }
        } else {

          if (ranging) {
            if (!c.isEquipped(c.getInventoryItemSlotIndex(switchId))) {
              c.setStatus("@red@Switching to melee weapon");
              c.equipItem(c.getInventoryItemSlotIndex(switchId));
            }
          }
          if (maging) {
            c.setStatus("@red@Maging...");
            ORSCharacter victimNpc = c.getNearestNpcByIds(npcIds, true);
            if (victimNpc != null) c.castSpellOnNpc(victimNpc.serverIndex, spellId);
          }
        }
      }
    }
  }

  private void leaveCombat() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.isInCombat()) {
          c.setStatus("@red@Leaving combat..");
          c.walkToAsync(c.currentX(), c.currentY(), 1);
          c.sleep(640);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    c.setStatus("@gre@Done Leaving combat..");
  }

  private void buryBones() {
    if (c.isInCombat()) leaveCombat();
    for (int id : bones) {
      try {
        if (c.getInventoryItemCount(id) > 0) {
          c.setStatus("@red@Burying bones..");
          c.itemCommand(id);
          c.sleep(640);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private boolean isWithinWander(int x, int y) {
    if (maxWander < 0) return true;

    return c.distance(startTile[0], startTile[1], x, y) <= maxWander;
  }

  private void popup(String title, String text) {
    JFrame parent = new JFrame(title);
    JLabel textLabel = new JLabel(text);
    JButton okButton = new JButton("OK");

    parent.setLayout(new GridLayout(0, 1));

    okButton.addActionListener(
        e -> {
          parent.setVisible(false);
          parent.dispose();
        });

    parent.add(textLabel);
    parent.add(okButton);
    parent.pack();
    parent.setVisible(true);
  }

  private boolean validateFields(
      JTextField npcIds,
      JTextField maxWanderField,
      JTextField eatAtHpField,
      JTextField lootTableField,
      JTextField spellNameField,
      JTextField arrowIdField,
      JTextField switchIdField) {

    try {
      String content = npcIds.getText().replace(" ", "");
      String[] values;

      if (!content.contains(",")) {
        values = new String[] {content};
      } else {
        values = content.split(",");
      }

      for (String value : values) {
        Integer.valueOf(value);
      }

    } catch (Exception e) {
      popup("Error", "Invalid loot table value(s).");
      return false;
    }

    try {
      Integer.valueOf(maxWanderField.getText());
    } catch (Exception e) {
      popup("Error", "Invalid max wander value.");
      return false;
    }

    try {
      Integer.valueOf(eatAtHpField.getText());
    } catch (Exception e) {
      popup("Error", "Invalid eat at HP value.");
      return false;
    }

    try {
      String content = lootTableField.getText().replace(" ", "");
      String[] values;

      if (!content.contains(",")) {
        values = new String[] {content};
      } else {
        values = content.split(",");
      }

      for (String value : values) {
        Integer.valueOf(value);
      }

    } catch (Exception e) {
      popup("Error", "Invalid loot table value(s).");
      return false;
    }

    if (c.getSpellIdFromName(spellNameField.getText()) < 0) {
      popup("Error", "Spell name does not exist.");
      return false;
    }

    try {
      Integer.valueOf(arrowIdField.getText());
    } catch (Exception e) {
      popup("Error", "Invalid arrow ID value.");
      return false;
    }

    try {
      Integer.valueOf(switchIdField.getText());
    } catch (Exception e) {
      popup("Error", "Invalid switch ID value.");
      return false;
    }

    return true;
  }

  private void setValuesFromGUI(
      JComboBox<String> fightModeField,
      JTextField npcIdsField,
      JTextField maxWanderField,
      JTextField eatAtHpField,
      JTextField lootTableField,
      JCheckBox openDoorsCheckbox,
      JCheckBox buryBonesCheckbox,
      JCheckBox prioritizeBonesCheckbox,
      JCheckBox magingCheckbox,
      JTextField spellNameField,
      JCheckBox rangingCheckbox,
      JTextField arrowIdField,
      JTextField switchIdField) {
    this.fightMode = fightModeField.getSelectedIndex();

    if (npcIdsField.getText().contains(",")) {
      for (String value : npcIdsField.getText().replace(" ", "").split(",")) {
        this.npcIds = Arrays.copyOf(npcIds, npcIds.length + 1);
        this.npcIds[npcIds.length - 1] = Integer.parseInt(value);
      }
    } else {
      this.npcIds = new int[] {Integer.parseInt(npcIdsField.getText())};
    }

    this.maxWander = Integer.parseInt(maxWanderField.getText());
    this.eatingHealth = Integer.parseInt(eatAtHpField.getText());

    if (lootTableField.getText().contains(",")) {
      for (String value : lootTableField.getText().replace(" ", "").split(",")) {
        this.loot = Arrays.copyOf(loot, loot.length + 1);
        this.loot[loot.length - 1] = Integer.parseInt(value);
      }
    } else {
      this.loot = new int[] {Integer.parseInt(lootTableField.getText())};
    }

    this.openDoors = openDoorsCheckbox.isSelected();
    this.buryBones = buryBonesCheckbox.isSelected();
    this.prioritizeBones = prioritizeBonesCheckbox.isSelected();
    this.maging = magingCheckbox.isSelected();
    this.spellId = c.getSpellIdFromName(spellNameField.getText());
    this.ranging = rangingCheckbox.isSelected();
    this.arrowId = Integer.parseInt(arrowIdField.getText());
    this.switchId = Integer.parseInt(switchIdField.getText());
  }

  private void setupGUI() {

    JLabel label2 = new JLabel("Chat commands can be used to direct the bot");
    JLabel label3 = new JLabel("Control ::bones ::prioritize :doors");
    JLabel label4 = new JLabel("Combat Styles ::attack :strength ::defense ::controlled");
    JLabel fightModeLabel = new JLabel("Fight Mode:");
    JComboBox<String> fightModeField =
        new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
    fightModeField.setSelectedIndex(c.getFightMode());
    JLabel npcIdsLabel = new JLabel("NPC IDs:");
    JTextField npcIdsField = new JTextField("3");
    JLabel maxWanderLabel = new JLabel("Max Wander Distance: (-1 to disable)");
    JTextField maxWanderField = new JTextField("20");
    JLabel eatAtHpLabel = new JLabel("Eat at HP: (food is automatically detected)");
    JTextField eatAtHpField =
        new JTextField(String.valueOf(c.getCurrentStat(c.getStatId("Hits")) / 2));
    JLabel lootTableLabel = new JLabel("Loot Table: (comma separated)");
    JTextField lootTableField = new JTextField("-1");
    JCheckBox openDoorsCheckbox =
        new JCheckBox("Open doors/gates? (if On, then set a max wander!)");
    JCheckBox buryBonesCheckbox =
        new JCheckBox("Loot & Bury Bones? (only loots bones when npc is null)");
    JCheckBox prioritizeBonesCheckbox =
        new JCheckBox("Prioritize Bone looting over attacking NPCs?)");
    JCheckBox magingCheckbox = new JCheckBox("Magic?");
    JLabel spellNameLabel = new JLabel("Spell Name: (exactly as it appears in spellbook)");
    JTextField spellNameField = new JTextField("Wind Bolt");
    JCheckBox rangingCheckbox = new JCheckBox("Ranging?");
    JLabel arrowIdLabel = new JLabel("Pickup Arrow ID: (-1 to disable)");
    JTextField arrowIdField = new JTextField("-1");
    JLabel switchLabel =
        new JLabel("Switch ID (weapon to switch to if in melee combat while ranging)");
    JTextField switchIdField = new JTextField("81");
    JButton startScriptButton = new JButton("Start");

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 2));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(label2);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(fightModeLabel);
    scriptFrame.add(fightModeField);
    scriptFrame.add(npcIdsLabel);
    scriptFrame.add(npcIdsField);
    scriptFrame.add(maxWanderLabel);
    scriptFrame.add(maxWanderField);
    scriptFrame.add(eatAtHpLabel);
    scriptFrame.add(eatAtHpField);
    scriptFrame.add(lootTableLabel);
    scriptFrame.add(lootTableField);
    scriptFrame.add(openDoorsCheckbox);
    scriptFrame.add(new JLabel());
    scriptFrame.add(buryBonesCheckbox);
    scriptFrame.add(new JLabel());
    scriptFrame.add(prioritizeBonesCheckbox);
    scriptFrame.add(new JLabel());
    scriptFrame.add(magingCheckbox);
    scriptFrame.add(new JLabel());
    scriptFrame.add(spellNameLabel);
    scriptFrame.add(spellNameField);
    scriptFrame.add(rangingCheckbox);
    scriptFrame.add(new JLabel());
    scriptFrame.add(arrowIdLabel);
    scriptFrame.add(arrowIdField);
    scriptFrame.add(switchLabel);
    scriptFrame.add(switchIdField);
    scriptFrame.add(startScriptButton);

    spellNameField.setEnabled(false);
    arrowIdField.setEnabled(false);
    switchIdField.setEnabled(false);
    prioritizeBonesCheckbox.setEnabled(false);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();

    c.setStatus("@red@Waiting for start...");

    // action listeners below
    startScriptButton.addActionListener(
        e -> {
          if (validateFields(
              npcIdsField,
              maxWanderField,
              eatAtHpField,
              lootTableField,
              spellNameField,
              arrowIdField,
              switchIdField)) {
            setValuesFromGUI(
                fightModeField,
                npcIdsField,
                maxWanderField,
                eatAtHpField,
                lootTableField,
                openDoorsCheckbox,
                buryBonesCheckbox,
                prioritizeBonesCheckbox,
                magingCheckbox,
                spellNameField,
                rangingCheckbox,
                arrowIdField,
                switchIdField);

            c.displayMessage("@red@AIOFighter by Dvorak. Let's party like it's 2004!");
            c.setStatus("@red@Started...");

            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            scriptStarted = true;
          }
        });

    magingCheckbox.addActionListener(e -> spellNameField.setEnabled(magingCheckbox.isSelected()));
    buryBonesCheckbox.addActionListener(
        e -> prioritizeBonesCheckbox.setEnabled(buryBonesCheckbox.isSelected()));
    rangingCheckbox.addActionListener(
        e -> {
          arrowIdField.setEnabled(rangingCheckbox.isSelected());
          switchIdField.setEnabled(rangingCheckbox.isSelected());
        });
  }

  @Override
  public void chatCommandInterrupt(
      String commandText) { // ::bank ::bones ::lowlevel :potup ::prayer
    if (commandText.replace(" ", "").toLowerCase().contains("bones")) {
      if (!buryBones) {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning on bone looting!");
        buryBones = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@bones@or1@, turning off bone looting!");
        buryBones = false;
      }
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("prioritize")) {
      if (!prioritizeBones) {
        c.displayMessage("@or1@Got toggle @red@lowlevel@or1@, turning on low level herb looting!");
        prioritizeBones = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@lowlevel@or1@, turning off low level herb looting!");
        prioritizeBones = false;
      }
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("doors")) {
      if (!openDoors) {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning on regular atk/str pots!");
        openDoors = true;
      } else {
        c.displayMessage("@or1@Got toggle @red@potup@or1@, turning off regular atk/str pots!");
        openDoors = false;
      }
      c.sleep(100);
    } else if (commandText
        .replace(" ", "")
        .toLowerCase()
        .contains("attack")) { // field is "Controlled", "Aggressive", "Accurate", "Defensive"}
      c.displayMessage("@red@Got Combat Style Command! - Attack Xp");
      c.displayMessage("@red@Switching to \"Accurate\" combat style!");
      fightMode = 2;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("strength")) {
      c.displayMessage("@red@Got Combat Style Command! - Strength Xp");
      c.displayMessage("@red@Switching to \"Aggressive\" combat style!");
      fightMode = 1;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("defense")) {
      c.displayMessage("@red@Got Combat Style Command! - Defense Xp");
      c.displayMessage("@red@Switching to \"Defensive\" combat style!");
      fightMode = 3;
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("controlled")) {
      c.displayMessage("@red@Got Combat Style Command! - Controlled Xp");
      c.displayMessage("@red@Switching to \"Controlled\" combat style!");
      fightMode = 0;
      c.sleep(100);
    }
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("bury")) bonesBuried++;
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("successfully")) spellsCasted++;
    else if (message.equals("I can't get a clear shot from here")) {
      c.setStatus("@red@Walking to NPC to get a shot...");
      c.walktoNPCAsync(currentAttackingNpc);
      c.sleep(2400);
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      int bonesPerHr = 0;
      int spellsPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        bonesPerHr = (int) (bonesBuried * scale);
        spellsPerHr = (int) (spellsCasted * scale);
      } catch (Exception e) {
        // divide by zero
      }

      int y = 21;
      c.drawBoxAlpha(7, 7, 160, 21 + 14 + 14, 0xFF0000, 128);
      c.drawString("@red@AIOFighter @whi@by @red@Dvorak", 10, 21, 0xFFFFFF, 1);
      y += 14;

      if (buryBones) {
        c.drawString(
            "@red@Bones Buried: @whi@"
                + String.format("%,d", bonesBuried)
                + " @red@(@whi@"
                + String.format("%,d", bonesPerHr)
                + "@red@/@whi@hr@red@)",
            10,
            y,
            0xFFFFFF,
            1);
        y += 14;
      }

      if (maging) {
        c.drawString(
            "@red@Spells Casted: @whi@"
                + String.format("%,d", spellsCasted)
                + " @red@(@whi@"
                + String.format("%,d", spellsPerHr)
                + "@red@/@whi@hr@red@)",
            10,
            y,
            0xFFFFFF,
            1);
      }
    }
  }
}
