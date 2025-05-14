package scripting.idlescript;

import bot.Main;
import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import controller.Controller;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import models.entities.ItemId;
import models.entities.SkillId;
import orsc.ORSCharacter;

public class AIOFighter extends IdleScript {
  private final Controller c = Main.getController();
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {
            Category.COMBAT,
            Category.MELEE,
            Category.RANGED,
            Category.MAGIC,
            Category.PRAYER,
            Category.IRONMAN_SUPPORTED
          },
          "Dvorak, Redesigned UI and paint by Seatta",
          "This is your standard melee/ranged/mage fighter script.\n\n"
              + "Chat commands can be used to direct the bot\n"
              + "    Controls:\n"
              + "        ::bones\n"
              + "        ::prioritize\n"
              + "        ::doors\n"
              + "    Combat Styles:\n"
              + "        ::controlled\n"
              + "        ::attack\n"
              + "        ::strength\n"
              + "        ::defense");

  private String status = "Setting up script...";
  private int fightMode = 2;
  private int targetLevel = -1;
  private int maxWander = 3;
  private int eatingHealth = 5;
  private boolean openDoors = false;
  private boolean buryBones = true;
  private boolean prioritizeBones = false;
  private boolean maging = true;
  private int spellId = 0;
  private boolean ranging = true;
  private int arrowId = -1; // leave -1 to not pickup arrows.
  private int switchId = 81; // weapon to switch to when in combat if ranging.
  private int[] npcIds = {};
  private int[] loot = {}; // feathers
  private int[] lootTable = {};
  private final int[] bones = {20, 413, 604, 814};
  private final Map<ItemId, Integer> bowLevelMap =
      new LinkedHashMap<ItemId, Integer>() {
        {
          put(ItemId.MAGIC_LONGBOW, 50);
          put(ItemId.MAGIC_SHORTBOW, 45);
          put(ItemId.YEW_LONGBOW, 40);
          put(ItemId.YEW_SHORTBOW, 35);
          put(ItemId.MAPLE_LONGBOW, 30);
          put(ItemId.MAPLE_SHORTBOW, 25);
          put(ItemId.WILLOW_LONGBOW, 20);
          put(ItemId.WILLOW_SHORTBOW, 15);
          put(ItemId.OAK_LONGBOW, 10);
          put(ItemId.OAK_SHORTBOW, 5);
          put(ItemId.LONGBOW, 1);
          put(ItemId.SHORTBOW, 1);
          put(ItemId.PHOENIX_CROSSBOW, 1);
          put(ItemId.CROSSBOW, 1);
        }
      };

  private final Map<SkillId, Integer> startingXP =
      new LinkedHashMap<SkillId, Integer>() {
        {
          put(SkillId.HITS, c.getStatXp(SkillId.HITS.getId()));
          put(SkillId.ATTACK, c.getStatXp(SkillId.ATTACK.getId()));
          put(SkillId.STRENGTH, c.getStatXp(SkillId.STRENGTH.getId()));
          put(SkillId.DEFENSE, c.getStatXp(SkillId.DEFENSE.getId()));
          put(SkillId.RANGED, c.getStatXp(SkillId.RANGED.getId()));
          put(SkillId.MAGIC, c.getStatXp(SkillId.MAGIC.getId()));
          put(SkillId.PRAYER, c.getStatXp(SkillId.PRAYER.getId()));
        }
      };

  private final int[] doorObjectIds = {60, 64};

  // do not modify these
  private int currentAttackingNpc = -1;
  private final int[] startTile = {-1, -1};
  private boolean guiSetup = false;
  private boolean scriptStarted = false;
  private int bonesBuried = 0;
  private int spellsCasted = 0;

  private final JFrame frame = new JFrame("AIOFighter: " + c.getPlayerName());
  private final JPanel panel = new JPanel();

  // UI Components
  private final SpringLayout sl = new SpringLayout();
  private final String[] nearbyTableColumns = {"", "Name", "Id"};
  private final DefaultTableModel tableModel =
      new DefaultTableModel(new Object[][] {}, nearbyTableColumns) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
          if (columnIndex == 0) {
            return Boolean.class;
          }
          return super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
          return false;
        }
      };

  private final JTable nearbyTable = new JTable(tableModel) {};
  private final JScrollPane nearbyScrollPane = new JScrollPane(nearbyTable);
  private final JTableHeader nearbyHeader = nearbyTable.getTableHeader();

  private final JLabel npcIdLabel = new JLabel("NPC IDs: (comma separated)");
  private final JLabel lootLabel = new JLabel("Loot Table: (comma separated)");
  private final JLabel wanderLabel = new JLabel("Wander Distance: (-1 to disable)");
  private final JLabel eatLabel = new JLabel("Eat at HP:");
  private final JLabel fightModeLabel = new JLabel("Fight Mode:");
  private final JLabel targetLevelLabel = new JLabel("Target Level:");
  private final JLabel switchLabel = new JLabel("In melee range weapon switch id:");

  private final JTextField npcIdsField = new JTextField("");
  private final JTextField wanderField = new JTextField("20");
  private final JTextField eatAtHpField =
      new JTextField(String.valueOf(c.getCurrentStat(c.getStatId("Hits")) / 2));
  private final JTextField lootField = new JTextField("-1");
  private final JTextField targetLevelField = new JTextField("-1");
  private final JTextField switchIdField = new JTextField("81");

  private final JCheckBox openDoorsCheckbox =
      new JCheckBox("Open doors/gates? (if On, then set a max wander!)");
  private final JCheckBox buryBonesCheckbox = new JCheckBox("Loot and bury bones?");
  private final JCheckBox prioritizeBonesCheckbox =
      new JCheckBox("Prioritize bones over attacking?");
  private final JCheckBox magingCheckbox = new JCheckBox("Magic?");
  private final JCheckBox rangingCheckbox = new JCheckBox("Ranging?");

  @SuppressWarnings("UnnecessaryUnicodeEscape")
  private final JButton refreshBtn = new JButton("\u21BB");

  private final JButton startBtn = new JButton("Run Script");

  private final JComboBox<String> modeCombobBox =
      new JComboBox<>(new String[] {"Controlled", "Aggressive", "Accurate", "Defensive"});
  private final JComboBox<String> spellComboBox = new JComboBox<>(c.getSpellNames());
  private final Map<String, Integer> arrowMap =
      new LinkedHashMap<String, Integer>() {
        {
          put("Pick up no arrows", -1);
          put("Pick up Bronze Arrows", ItemId.BRONZE_ARROWS.getId());
          put("Pick up Iron Arrows", ItemId.IRON_ARROWS.getId());
          put("Pick up Steel Arrows", ItemId.STEEL_ARROWS.getId());
          put("Pick up Mithril Arrows", ItemId.MITHRIL_ARROWS.getId());
          put("Pick up Adamantite Arrows", ItemId.ADAMANTITE_ARROWS.getId());
          put("Pick up Rune Arrows", ItemId.RUNE_ARROWS.getId());
          put("Pick up Poison Bronze Arrows", ItemId.POISON_BRONZE_ARROWS.getId());
          put("Pick up Poison Iron Arrows", ItemId.POISON_IRON_ARROWS.getId());
          put("Pick up Poison Steel Arrows", ItemId.POISON_STEEL_ARROWS.getId());
          put("Pick up Poison Mithril Arrows", ItemId.POISON_MITHRIL_ARROWS.getId());
          put("Pick up Poison Adamantite Arrows", ItemId.POISON_ADAMANTITE_ARROWS.getId());
          put("Pick up Poison Rune Arrows", ItemId.POISON_RUNE_ARROWS.getId());
          put("Pick up Ice Arrows", ItemId.ICE_ARROWS.getId());
        }
      };
  private final List<Integer> arrowIds =
      arrowMap.values().stream().filter(id -> id != -1).collect(Collectors.toList());
  private final JComboBox<String> arrowComboBox =
      new JComboBox<>(arrowMap.keySet().toArray(new String[0]));

  private Map<String, Integer> nearbyNPCs = getNearbyNpcs();

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

    populateNearbyNpcTable();
    while (!scriptStarted && c.isRunning() && frame.isVisible()) c.sleep(640);
  }

  private void setDefaultValues() {
    scriptStarted = false;
    nearbyTable.clearSelection();
    arrowComboBox.setEnabled(false);
    spellComboBox.setEnabled(false);
    startBtn.setEnabled(false);
    magingCheckbox.setSelected(false);
    rangingCheckbox.setSelected(false);
    prioritizeBonesCheckbox.setSelected(false);
    openDoorsCheckbox.setSelected(false);
    buryBonesCheckbox.setSelected(false);
    switchIdField.setEnabled(false);
    prioritizeBonesCheckbox.setEnabled(false);
  }

  private void addComponentsToPanel() {
    panel.setLayout(sl);
    for (Component comp :
        new Component[] {
          fightModeLabel,
          modeCombobBox,
          targetLevelLabel,
          targetLevelField,
          npcIdLabel,
          nearbyScrollPane,
          npcIdsField,
          refreshBtn,
          wanderLabel,
          wanderField,
          eatAtHpField,
          eatLabel,
          lootField,
          lootLabel,
          openDoorsCheckbox,
          buryBonesCheckbox,
          prioritizeBonesCheckbox,
          magingCheckbox,
          spellComboBox,
          rangingCheckbox,
          arrowComboBox,
          switchLabel,
          switchIdField,
          startBtn
        }) panel.add(comp);
  }

  public void setListeners() {
    npcIdsField
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              public void changedUpdate(DocumentEvent e) {
                update();
              }

              public void removeUpdate(DocumentEvent e) {
                update();
              }

              public void insertUpdate(DocumentEvent e) {
                update();
              }

              public void update() {
                startBtn.setEnabled(!npcIdsField.getText().isEmpty());
              }
            });
    startBtn.addActionListener(
        e -> {
          if (validateFields(
              npcIdsField, wanderField, eatAtHpField, lootField, switchIdField, targetLevelField)) {
            setValuesFromGUI(
                modeCombobBox,
                targetLevelField,
                npcIdsField,
                wanderField,
                eatAtHpField,
                lootField,
                openDoorsCheckbox,
                buryBonesCheckbox,
                prioritizeBonesCheckbox,
                magingCheckbox,
                spellComboBox,
                rangingCheckbox,
                arrowComboBox,
                switchIdField);

            c.displayMessage("@red@AIOFighter by Dvorak. Let's party like it's 2004!");

            frame.setVisible(false);
            frame.dispose();
            scriptStarted = true;
          }
        });

    magingCheckbox.addActionListener(
        e -> {
          spellComboBox.setEnabled(magingCheckbox.isSelected());
          if (magingCheckbox.isSelected()) {
            rangingCheckbox.setSelected(false);
            arrowComboBox.setEnabled(false);
            switchIdField.setEnabled(false);
          }
        });
    rangingCheckbox.addActionListener(
        e -> {
          arrowComboBox.setEnabled(rangingCheckbox.isSelected());
          switchIdField.setEnabled(rangingCheckbox.isSelected());
          if (rangingCheckbox.isSelected()) {
            magingCheckbox.setSelected(false);
            spellComboBox.setEnabled(false);
          }
        });

    buryBonesCheckbox.addActionListener(
        e -> {
          if (!buryBonesCheckbox.isSelected()) prioritizeBonesCheckbox.setSelected(false);
          prioritizeBonesCheckbox.setEnabled(buryBonesCheckbox.isSelected());
        });

    // Button listener for refreshing nearby npcs
    refreshBtn.addActionListener(e -> populateNearbyNpcTable());

    // Mouse click listener to nearby enemies table
    nearbyTable.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            int row = nearbyTable.rowAtPoint(e.getPoint()); // Get the row clicked
            if (row != -1) {
              // Toggle the boolean value in the first column
              boolean currentValue = (Boolean) nearbyTable.getValueAt(row, 0);
              nearbyTable.setValueAt(!currentValue, row, 0);
            }
            setNearbyNpcsFromTable();
          }
        });
  }

  public void setNearbyNpcsFromTable() {
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < nearbyTable.getRowCount(); i++) {
      if ((boolean) nearbyTable.getValueAt(i, 0)) {
        if (text.length() > 0) text.append(",");
        text.append((String) nearbyTable.getValueAt(i, 2));
      }
    }
    npcIdsField.setText(text.toString());
  }

  public void setConstraints() {
    // LEFT SIDE
    sl.putConstraint(SpringLayout.NORTH, npcIdLabel, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, npcIdLabel, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, npcIdLabel, 4, SpringLayout.EAST, refreshBtn);
    sl.putConstraint(SpringLayout.SOUTH, npcIdLabel, 18, SpringLayout.NORTH, npcIdLabel);

    sl.putConstraint(SpringLayout.NORTH, npcIdsField, 4, SpringLayout.SOUTH, npcIdLabel);
    sl.putConstraint(SpringLayout.SOUTH, npcIdsField, 18, SpringLayout.NORTH, npcIdsField);
    sl.putConstraint(SpringLayout.WEST, npcIdsField, 0, SpringLayout.WEST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.EAST, npcIdsField, -4, SpringLayout.WEST, refreshBtn);

    sl.putConstraint(SpringLayout.NORTH, nearbyScrollPane, 4, SpringLayout.SOUTH, npcIdsField);
    sl.putConstraint(SpringLayout.WEST, nearbyScrollPane, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, nearbyScrollPane, 240, SpringLayout.WEST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.SOUTH, nearbyScrollPane, -4, SpringLayout.NORTH, lootLabel);

    sl.putConstraint(SpringLayout.SOUTH, refreshBtn, 0, SpringLayout.SOUTH, npcIdsField);
    sl.putConstraint(SpringLayout.NORTH, refreshBtn, 0, SpringLayout.NORTH, npcIdsField);
    sl.putConstraint(SpringLayout.WEST, refreshBtn, -46, SpringLayout.EAST, refreshBtn);
    sl.putConstraint(SpringLayout.EAST, refreshBtn, 0, SpringLayout.EAST, nearbyScrollPane);

    sl.putConstraint(SpringLayout.NORTH, lootLabel, -18, SpringLayout.SOUTH, lootLabel);
    sl.putConstraint(SpringLayout.WEST, lootLabel, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, lootLabel, 0, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.SOUTH, lootLabel, -4, SpringLayout.NORTH, lootField);

    sl.putConstraint(SpringLayout.NORTH, lootField, -18, SpringLayout.SOUTH, lootField);
    sl.putConstraint(SpringLayout.WEST, lootField, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, lootField, 0, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.SOUTH, lootField, -4, SpringLayout.NORTH, startBtn);

    // RIGHT SIDE
    sl.putConstraint(SpringLayout.NORTH, fightModeLabel, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, fightModeLabel, 4, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.SOUTH, fightModeLabel, 18, SpringLayout.NORTH, fightModeLabel);

    sl.putConstraint(SpringLayout.NORTH, modeCombobBox, 4, SpringLayout.SOUTH, fightModeLabel);
    sl.putConstraint(SpringLayout.WEST, modeCombobBox, 4, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.EAST, modeCombobBox, 100, SpringLayout.WEST, modeCombobBox);
    sl.putConstraint(SpringLayout.SOUTH, modeCombobBox, 18, SpringLayout.NORTH, modeCombobBox);

    sl.putConstraint(SpringLayout.NORTH, targetLevelLabel, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, targetLevelLabel, 4, SpringLayout.EAST, modeCombobBox);
    sl.putConstraint(SpringLayout.EAST, targetLevelLabel, 82, SpringLayout.WEST, targetLevelLabel);
    sl.putConstraint(
        SpringLayout.SOUTH, targetLevelLabel, 18, SpringLayout.NORTH, targetLevelLabel);

    sl.putConstraint(SpringLayout.NORTH, targetLevelField, 4, SpringLayout.SOUTH, targetLevelLabel);
    sl.putConstraint(SpringLayout.WEST, targetLevelField, 4, SpringLayout.EAST, modeCombobBox);
    sl.putConstraint(SpringLayout.EAST, targetLevelField, 82, SpringLayout.WEST, targetLevelField);
    sl.putConstraint(SpringLayout.SOUTH, targetLevelField, 18, SpringLayout.NORTH, modeCombobBox);

    sl.putConstraint(SpringLayout.NORTH, eatLabel, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, eatLabel, 16, SpringLayout.EAST, targetLevelLabel);
    sl.putConstraint(SpringLayout.SOUTH, eatLabel, 18, SpringLayout.NORTH, eatLabel);

    sl.putConstraint(SpringLayout.NORTH, eatAtHpField, 4, SpringLayout.SOUTH, eatLabel);
    sl.putConstraint(SpringLayout.WEST, eatAtHpField, 0, SpringLayout.WEST, eatLabel);
    sl.putConstraint(SpringLayout.EAST, eatAtHpField, 48, SpringLayout.WEST, eatAtHpField);
    sl.putConstraint(SpringLayout.SOUTH, eatAtHpField, 18, SpringLayout.NORTH, eatAtHpField);

    sl.putConstraint(SpringLayout.NORTH, wanderLabel, 4, SpringLayout.SOUTH, modeCombobBox);
    sl.putConstraint(SpringLayout.WEST, wanderLabel, 8, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.SOUTH, wanderLabel, 18, SpringLayout.NORTH, wanderLabel);

    sl.putConstraint(SpringLayout.NORTH, wanderField, 0, SpringLayout.NORTH, wanderLabel);
    sl.putConstraint(SpringLayout.SOUTH, wanderField, 0, SpringLayout.SOUTH, wanderLabel);
    sl.putConstraint(SpringLayout.WEST, wanderField, 4, SpringLayout.EAST, wanderLabel);
    sl.putConstraint(SpringLayout.EAST, wanderField, -4, SpringLayout.EAST, panel);

    sl.putConstraint(SpringLayout.NORTH, openDoorsCheckbox, 4, SpringLayout.SOUTH, wanderField);
    sl.putConstraint(SpringLayout.WEST, openDoorsCheckbox, 4, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.EAST, openDoorsCheckbox, -4, SpringLayout.EAST, panel);
    sl.putConstraint(
        SpringLayout.SOUTH, openDoorsCheckbox, 18, SpringLayout.NORTH, openDoorsCheckbox);

    sl.putConstraint(
        SpringLayout.NORTH, buryBonesCheckbox, 14, SpringLayout.SOUTH, openDoorsCheckbox);
    sl.putConstraint(SpringLayout.WEST, buryBonesCheckbox, 4, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.EAST, buryBonesCheckbox, -4, SpringLayout.EAST, panel);
    sl.putConstraint(
        SpringLayout.SOUTH, buryBonesCheckbox, 18, SpringLayout.NORTH, buryBonesCheckbox);

    sl.putConstraint(
        SpringLayout.NORTH, prioritizeBonesCheckbox, 4, SpringLayout.SOUTH, buryBonesCheckbox);
    sl.putConstraint(
        SpringLayout.WEST, prioritizeBonesCheckbox, 4, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.EAST, prioritizeBonesCheckbox, -4, SpringLayout.EAST, panel);
    sl.putConstraint(
        SpringLayout.SOUTH,
        prioritizeBonesCheckbox,
        18,
        SpringLayout.NORTH,
        prioritizeBonesCheckbox);

    sl.putConstraint(SpringLayout.NORTH, magingCheckbox, -18, SpringLayout.SOUTH, magingCheckbox);
    sl.putConstraint(SpringLayout.SOUTH, magingCheckbox, -4, SpringLayout.NORTH, rangingCheckbox);
    sl.putConstraint(SpringLayout.WEST, magingCheckbox, 4, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.EAST, magingCheckbox, 78, SpringLayout.WEST, magingCheckbox);

    sl.putConstraint(SpringLayout.NORTH, spellComboBox, 0, SpringLayout.NORTH, magingCheckbox);
    sl.putConstraint(SpringLayout.SOUTH, spellComboBox, 0, SpringLayout.SOUTH, magingCheckbox);
    sl.putConstraint(SpringLayout.WEST, spellComboBox, 4, SpringLayout.EAST, magingCheckbox);
    sl.putConstraint(SpringLayout.EAST, spellComboBox, -4, SpringLayout.EAST, panel);

    sl.putConstraint(SpringLayout.NORTH, rangingCheckbox, -18, SpringLayout.SOUTH, rangingCheckbox);
    sl.putConstraint(SpringLayout.SOUTH, rangingCheckbox, -4, SpringLayout.NORTH, switchLabel);
    sl.putConstraint(SpringLayout.WEST, rangingCheckbox, 4, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.EAST, rangingCheckbox, 78, SpringLayout.WEST, rangingCheckbox);

    sl.putConstraint(SpringLayout.NORTH, arrowComboBox, 0, SpringLayout.NORTH, rangingCheckbox);
    sl.putConstraint(SpringLayout.SOUTH, arrowComboBox, 0, SpringLayout.SOUTH, rangingCheckbox);
    sl.putConstraint(SpringLayout.WEST, arrowComboBox, 4, SpringLayout.EAST, rangingCheckbox);
    sl.putConstraint(SpringLayout.EAST, arrowComboBox, -4, SpringLayout.EAST, panel);

    sl.putConstraint(SpringLayout.NORTH, switchLabel, -18, SpringLayout.SOUTH, switchLabel);
    sl.putConstraint(SpringLayout.SOUTH, switchLabel, -18, SpringLayout.NORTH, startBtn);
    sl.putConstraint(SpringLayout.WEST, switchLabel, 20, SpringLayout.WEST, modeCombobBox);

    sl.putConstraint(SpringLayout.NORTH, switchIdField, 0, SpringLayout.NORTH, switchLabel);
    sl.putConstraint(SpringLayout.SOUTH, switchIdField, 0, SpringLayout.SOUTH, switchLabel);
    sl.putConstraint(SpringLayout.WEST, switchIdField, 4, SpringLayout.EAST, switchLabel);
    sl.putConstraint(SpringLayout.EAST, switchIdField, -4, SpringLayout.EAST, panel);

    // BOTTOM
    sl.putConstraint(SpringLayout.NORTH, startBtn, -24, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.SOUTH, startBtn, -4, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.WEST, startBtn, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, startBtn, -4, SpringLayout.EAST, panel);
  }

  public void setTheming() {
    TableColumn nearbyColumn1 = nearbyTable.getColumnModel().getColumn(0);
    nearbyColumn1.setPreferredWidth(18);
    nearbyColumn1.setMinWidth(18);
    nearbyColumn1.setMaxWidth(18);
    TableColumn nearbyColumn3 = nearbyTable.getColumnModel().getColumn(2);
    nearbyColumn3.setPreferredWidth(40);
    nearbyColumn3.setMinWidth(40);
    nearbyColumn3.setMaxWidth(40);

    frame.getContentPane().setBackground(Main.primaryBG);
    frame.getContentPane().setForeground(Main.primaryFG);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setPreferredSize(new Dimension(600, 300));

    panel.setBackground(Main.primaryBG);
    panel.setForeground(Main.primaryFG);

    for (JLabel label :
        new JLabel[] {
          npcIdLabel,
          lootLabel,
          wanderLabel,
          switchLabel,
          fightModeLabel,
          eatLabel,
          targetLevelLabel
        }) label.setForeground(Main.primaryFG);

    for (JCheckBox check :
        new JCheckBox[] {
          buryBonesCheckbox,
          magingCheckbox,
          openDoorsCheckbox,
          prioritizeBonesCheckbox,
          rangingCheckbox
        }) {
      check.setForeground(Main.primaryFG);
      check.setBackground(Main.primaryBG);
    }

    for (JButton button : new JButton[] {refreshBtn, startBtn}) {
      button.setForeground(Main.secondaryFG);
      button.setBackground(Main.secondaryBG);
    }

    nearbyTable.setRowSelectionAllowed(false);
    nearbyTable.setBorder(BorderFactory.createEmptyBorder());
    nearbyTable.setBackground(Main.primaryBG.brighter());
    nearbyTable.setForeground(Main.primaryFG);

    for (JTextField field :
        new JTextField[] {
          npcIdsField, eatAtHpField, wanderField, lootField, switchIdField, targetLevelField
        }) {
      field.setForeground(Main.primaryFG);
      field.setBackground(Main.primaryBG.brighter().brighter());
    }

    for (JComboBox<?> combo : new JComboBox[] {arrowComboBox, modeCombobBox, spellComboBox}) {
      combo.setForeground(Main.primaryFG);
      combo.setBackground(Main.primaryBG.brighter().brighter());
      combo.setBorder(BorderFactory.createEmptyBorder());
    }

    nearbyScrollPane.setBackground(Main.primaryBG);
    nearbyScrollPane.setForeground(Main.primaryFG);
    nearbyScrollPane.getViewport().setBackground(Main.primaryBG.brighter());
    nearbyScrollPane.setBorder(BorderFactory.createEmptyBorder());

    nearbyHeader.setReorderingAllowed(false);
    nearbyHeader.setResizingAllowed(false);
    nearbyHeader.setFont(nearbyTable.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));
    nearbyHeader.setBackground(Main.primaryBG);
    nearbyHeader.setForeground(Main.primaryFG);
    nearbyHeader.setBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("controlDkShadow")));
    nearbyHeader.setDefaultRenderer(
        new DefaultTableCellRenderer() {
          @Override
          public Component getTableCellRendererComponent(
              JTable table,
              Object value,
              boolean isSelected,
              boolean hasFocus,
              int row,
              int column) {

            JLabel label =
                (JLabel)
                    super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            label.setBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, UIManager.getColor("controlDkShadow")));
            label.setFont(nearbyHeader.getFont().deriveFont(Font.BOLD, 15f));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBackground(Main.primaryBG);
            return label;
          }
        });
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
    paintBuilder.start(4, 18, 182);
    if (c.isRunning()) {
      lootTable = Arrays.copyOf(loot, loot.length);
      if (prioritizeBones) {
        lootTable = new int[loot.length + bones.length];
        System.arraycopy(loot, 0, lootTable, 0, loot.length);
        System.arraycopy(bones, 0, lootTable, loot.length, bones.length);
      }

      startTile[0] = c.currentX();
      startTile[1] = c.currentY();

      while (c.isRunning()) {
        if (c.getNeedToMove()) c.moveCharacter();
        if (c.getShouldSleep()) c.sleepHandler(true);
        while (!c.isLoggedIn() && c.isAutoLogin()) c.sleep(1280);
        if (hasReachedTargetLevel()) {
          c.log("Level target has been reached! Stopping script.");
          c.stop();
          break;
        }
        c.sleep(618); // wait 1 tick

        // Return to wander radius
        if (!isWithinWander(c.currentX(), c.currentY())) {
          status = "Walking back...";
          c.walkTo(startTile[0], startTile[1], 0, true, true);
        }

        // Open Doors
        if (openDoors) {
          for (int doorId : doorObjectIds) {
            int[] doorCoords = c.getNearestObjectById(doorId);

            if (doorCoords != null && this.isWithinWander(doorCoords[0], doorCoords[1])) {
              status = "Opening door...";
              c.atObject(doorCoords[0], doorCoords[1]);
              c.sleep(5000);
            }
          }
        }

        // Ensure that the correct fight mode is set only if a level target is not set
        if (c.getFightMode() != fightMode && targetLevel == -1) {
          status = "Changing fight mode...";
          c.setFightMode(fightMode);
        }

        // Eat food if needed
        if (eatIfNeeded()) continue;

        // Loot desired loot and bones
        for (int lootId : lootTable) {
          int[] lootCoord = c.getNearestItemById(lootId);
          if (lootCoord != null && this.isWithinWander(lootCoord[0], lootCoord[1])) {
            status = "Picking up loot...";
            c.pickupItem(lootCoord[0], lootCoord[1], lootId, true, false);
            c.sleep(618);
            buryBones();
          }
        }

        if (!c.isInCombat()) {
          if (c.getShouldSleep()) c.sleepHandler(true);
          if (ranging) {
            if (lootArrows()) continue;
            if (!checkBowAndArrows()) break;
            if (c.getUnnotedInventoryItemCount(switchId) < 1 && !c.isItemIdEquipped(switchId)) {
              c.log("We don't have our specified melee switch item! Stopping script.", "red");
              break;
            }
          }

          ORSCharacter npc = c.getNearestNpcByIds(npcIds, false);

          // maybe wrap this in a 'while not in combat' loop?
          if (npc != null) {
            if (maging && !ranging) {
              currentAttackingNpc = npc.serverIndex;
              c.castSpellOnNpc(npc.serverIndex, spellId);
            } else {
              status = "Attacking...";
              c.attackNpc(npc.serverIndex);
              c.sleep(1000);
            }

          } else {
            if (!c.isInCombat()) {
              if (buryBones) {
                for (int lootId : bones) {
                  int[] lootCoord = c.getNearestItemById(lootId);
                  if (lootCoord != null && this.isWithinWander(lootCoord[0], lootCoord[1])) {
                    status = "Picking bones...";
                    c.pickupItem(lootCoord[0], lootCoord[1], lootId, true, false);
                    c.sleep(618);
                    buryBones();
                  } else {
                    if (c.currentX() != startTile[0] && c.currentY() != startTile[1]) {
                      status = "Walking to start...";
                      c.walkToAsync(startTile[0], startTile[1], 0);
                    }
                  }
                }
              } else {
                if (c.currentX() != startTile[0] && c.currentY() != startTile[1]) {
                  status = "Walking to start...";
                  c.walkToAsync(startTile[0], startTile[1], 0);
                }
              }
            }
          }
        } else {

          if (ranging) {
            if (!c.isItemIdEquipped(switchId)) {
              status = "Switching to melee...";
              c.equipItem(c.getInventoryItemSlotIndex(switchId));
              c.sleep(100);
            }
          }
          if (maging) {
            status = "Maging...";
            ORSCharacter victimNpc = c.getNearestNpcByIds(npcIds, true);
            if (victimNpc != null) c.castSpellOnNpc(victimNpc.serverIndex, spellId);
          }
        }
      }
    }
  }

  private boolean eatIfNeeded() {
    if (c.getCurrentStat(c.getStatId("Hits")) <= eatingHealth) {
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
        c.log("We ran out of food! Logging out.", "red");
        c.setAutoLogin(false);
        c.logout();
      }
      return true;
    }
    return false;
  }

  private boolean lootArrows() {
    if (arrowId == -1) return false;
    int[] arrowCoord = c.getNearestItemById(arrowId);
    if (arrowCoord != null
        && c.isReachable(arrowCoord[0], arrowCoord[1], false)
        && isWithinWander(arrowCoord[0], arrowCoord[1])) {
      status = "Picking up arrows...";
      c.pickupItem(arrowCoord[0], arrowCoord[1], arrowId, false, true);
      c.sleep(100);
      // c.itemCommand(arrowId);
      return true;
    }
    c.sleep(100);
    return false;
  }

  private boolean checkBowAndArrows() {
    boolean hasArrows =
        arrowIds.stream()
            .anyMatch(
                a -> {
                  if (c.isItemIdEquipped(a)) return true;
                  if (c.getInventoryItemCount(a) > 0 && !c.isItemIdEquipped(a)) {
                    c.sleep(100);
                    c.equipItemById(a);
                    status = "@red@Equipping: " + c.getItemName(a) + "...";
                    return true;
                  }
                  return false;
                });

    boolean hasUsableBow =
        bowLevelMap.entrySet().stream()
            .anyMatch(
                e -> {
                  int id = e.getKey().getId();
                  int level = e.getValue();
                  if (c.isItemIdEquipped(id)) return true;
                  if (c.getUnnotedInventoryItemCount(id) > 0
                      && c.getBaseStat(SkillId.RANGED.getId()) >= level) {
                    c.sleep(100);
                    status = "@red@Equipping: " + c.getItemName(id) + "...";
                    c.equipItemById(id);
                    return true;
                  }
                  return false;
                });

    if (!hasArrows || !hasUsableBow) {
      if (!hasArrows) c.log("We ran out of arrows! Logging out.", "red");
      if (!hasUsableBow) c.log("We don't have a usable bow! Logging out.", "red");
      c.setAutoLogin(false);
      c.logout();
      c.stop();
      return false;
    }
    return true;
  }

  private void leaveCombat() {
    for (int i = 1; i <= 20; i++) {
      try {
        if (c.isInCombat()) {
          status = "Leaving combat..";
          c.walkToAsync(c.currentX(), c.currentY(), 1);
          c.sleep(640);
        } else {
          break;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    status = "Done leaving combat..";
  }

  private void buryBones() {
    if (c.isInCombat()) leaveCombat();
    for (int id : bones) {
      try {
        if (c.getInventoryItemCount(id) > 0) {
          status = "Burying bones...";
          c.itemCommand(id);
          c.sleep(640);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private boolean isWithinWander(int x, int y) {
    return maxWander < 0 || c.distance(startTile[0], startTile[1], x, y) <= maxWander;
  }

  private void popup(String text) {
    JFrame parent = new JFrame("Error");
    JLabel textLabel = new JLabel(text);
    JButton okButton = new JButton("OK");

    parent.setLayout(new GridLayout(0, 1));

    okButton.addActionListener(
        e -> {
          parent.setVisible(false);
          parent.dispose();
        });

    parent.setPreferredSize(new Dimension(220, 100));
    parent.add(textLabel);
    parent.add(okButton);
    parent.pack();
    parent.setResizable(false);
    parent.setLocationRelativeTo(frame);
    parent.setVisible(true);
  }

  /** Checks if all gui fields are valid or displays an error */
  private boolean validateFields(
      JTextField npcIds,
      JTextField maxWanderField,
      JTextField eatAtHpField,
      JTextField lootTableField,
      JTextField switchIdField,
      JTextField targetLevelField) {

    Map<JTextField, String> integerFieldMap =
        new HashMap<JTextField, String>() {
          {
            put(eatAtHpField, "Invalid eat at HP value.");
            put(switchIdField, "Invalid switch ID value.");
            put(maxWanderField, "Invalid max wander value.");
            put(lootTableField, "Invalid loot table value(s).");
            put(npcIds, "Invalid npc value(s).");
            put(targetLevelField, "Invalid target level.");
          }
        };

    for (Map.Entry<JTextField, String> entry : integerFieldMap.entrySet()) {
      String value = entry.getKey().getText().replace(" ", "");
      if (value.isEmpty()) continue;
      for (String part : value.split(",")) {
        if (part.isEmpty()) continue;
        try {
          int intValue = Integer.parseInt(part);

          // Throw any logic-based exceptions here
          if (entry.getKey().equals(targetLevelField) && (intValue == 0 || intValue > 99))
            throw new NumberFormatException();

        } catch (NumberFormatException e) {
          popup(entry.getValue());
          return false;
        }
      }
    }
    return true;
  }

  /** Sets field values to their respective gui value */
  private void setValuesFromGUI(
      JComboBox<String> fightModeField,
      JTextField targetLevelField,
      JTextField npcIdsField,
      JTextField maxWanderField,
      JTextField eatAtHpField,
      JTextField lootTableField,
      JCheckBox openDoorsCheckbox,
      JCheckBox buryBonesCheckbox,
      JCheckBox prioritizeBonesCheckbox,
      JCheckBox magingCheckbox,
      JComboBox<String> spellNameField,
      JCheckBox rangingCheckbox,
      JComboBox<String> arrowIdField,
      JTextField switchIdField) {

    String[] npcValues = npcIdsField.getText().replace(" ", "").split(",");
    npcIds = new int[npcValues.length];
    for (int i = 0; i < npcValues.length; i++) npcIds[i] = Integer.parseInt(npcValues[i]);

    String[] lootValues = lootTableField.getText().replace(" ", "").split(",");
    loot = new int[lootValues.length];
    for (int i = 0; i < lootValues.length; i++) loot[i] = Integer.parseInt(lootValues[i]);

    maxWander = Integer.parseInt(maxWanderField.getText());
    eatingHealth = Integer.parseInt(eatAtHpField.getText());
    targetLevel = Integer.parseInt(targetLevelField.getText());
    switchId = Integer.parseInt(switchIdField.getText());
    fightMode = fightModeField.getSelectedIndex();
    openDoors = openDoorsCheckbox.isSelected();
    buryBones = buryBonesCheckbox.isSelected();
    prioritizeBones = prioritizeBonesCheckbox.isSelected();
    maging = magingCheckbox.isSelected();
    ranging = rangingCheckbox.isSelected();
    spellId = c.getSpellIdFromName((String) spellNameField.getSelectedItem());
    arrowId = arrowMap.get((String) arrowIdField.getSelectedItem());
  }

  /** Populates the NPC table with nearby NPCs */
  private void populateNearbyNpcTable() {
    Map<String, Integer> newMap = getNearbyNpcs();
    if (newMap != null && !newMap.isEmpty()) {
      tableModel.setRowCount(0);

      if (nearbyNPCs == null) {
        nearbyNPCs = newMap;
      } else {
        nearbyNPCs.putAll(newMap);
      }

      if (nearbyNPCs != null && !nearbyNPCs.isEmpty()) {
        List<String> sortedKeys =
            nearbyNPCs.keySet().stream()
                .sorted(
                    Comparator.comparing(this::extractName).thenComparingInt(this::extractNumber))
                .collect(Collectors.toList());
        sortedKeys.forEach(
            name -> {
              Integer id = nearbyNPCs.get(name);
              tableModel.addRow(new Object[] {false, name, String.valueOf(id)});
            });
      }
    }
  }

  /**
   * Used for sorting npc names alphabetically then by level
   *
   * @param str String -- Name (level)
   * @return String -- Name
   */
  private String extractName(String str) {
    int index = str.indexOf('(');
    if (index != -1) {
      return str.substring(0, index).trim();
    }
    return str;
  }

  /**
   * Used for sorting npc names alphabetically then by level
   *
   * @param str String -- Name (level)
   * @return int -- level as int
   */
  private int extractNumber(String str) {
    int start = str.indexOf('(');
    int end = str.indexOf(')');
    if (start != -1 && end != -1) {
      return Integer.parseInt(str.substring(start + 1, end));
    }
    return 0; // Default if no number found
  }

  /**
   * Returns a map of nearby NPC names and ids to show in the UI
   *
   * @return List
   */
  private Map<String, Integer> getNearbyNpcs() {
    try {
      return Arrays.stream(controller.getNpcsAsArray())
          .filter(
              npc ->
                  controller.isNpcAttackable(npc.npcId)
                      && controller.getNearestNpcById(npc.npcId, true) != null)
          .distinct()
          .collect(
              Collectors.toMap(
                  npc -> {
                    String name = c.getNpcName(npc.npcId);
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    name += String.format(" (%s)", c.calculateNpcLevel(npc.npcId));
                    return name;
                  },
                  npc -> npc.npcId,
                  (oldValue, newValue) -> oldValue));
    } catch (Exception e) {
      return null;
    }
  }

  public boolean hasReachedTargetLevel() {
    if (targetLevel < 0) return false;
    HashMap<Integer, Integer> modeMap =
        new HashMap<Integer, Integer>() {
          {
            put(1, SkillId.STRENGTH.getId());
            put(2, SkillId.ATTACK.getId());
            put(3, SkillId.DEFENSE.getId());
          }
        };
    int currentFightMode = c.getFightMode();

    switch (fightMode) {
      case 0:
        // Check if any melee style has not hit the targeted level
        boolean hasAnySkillHitTarget =
            modeMap.entrySet().stream().anyMatch(e -> c.getBaseStat(e.getValue()) >= targetLevel);

        // If no style has hit the target, enforce controlled style and break since we don't want to
        // perform the style switching logic
        if (!hasAnySkillHitTarget) {
          if (currentFightMode != 0) c.setFightMode(0);
          break;
        }

        // If any style has reached the target, get a list of all non-finished styles
        List<Integer> skillsNotAtTarget =
            modeMap.entrySet().stream()
                .filter(e -> c.getBaseStat(e.getValue()) < targetLevel)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // If the list is empty, it means we're finished
        if (skillsNotAtTarget.isEmpty()) return true;

        // Otherwise, get the lowest leveled style of the unfinished ones
        int newFightMode =
            skillsNotAtTarget.stream()
                .min(Comparator.comparingInt(mode -> c.getBaseStat(modeMap.get(mode))))
                .orElse(skillsNotAtTarget.get(0));
        // int newFightMode = skillsNotAtTarget.get(0);

        // Change to the next style with the lowest level
        if (currentFightMode != newFightMode) c.setFightMode(newFightMode);
        break;
      case 1:
      case 2:
      case 3:
        if (currentFightMode != fightMode) c.setFightMode(fightMode);
        if (c.getBaseStat(modeMap.get(fightMode)) >= targetLevel) return true;
        break;
    }
    return false;
  }

  @Override
  public void chatCommandInterrupt(
      String commandText) { // ::bank ::bones ::lowlevel :potup ::prayer
    String str = commandText.replace(" ", "").toLowerCase();

    if (str.contains("bones")) {
      buryBones = !buryBones;
      c.displayMessage(
          String.format(
              "@or1@Got toggle @red@bones@or1@, turned %s bone looting!",
              buryBones ? "on" : "off"));
      c.sleep(100);

    } else if (str.contains("prioritize")) {
      prioritizeBones = !prioritizeBones;
      c.displayMessage(
          String.format(
              "@or1@Got toggle @red@prioritize@or1@, turned %s bone prioritization!",
              prioritizeBones ? "on" : "off"));

      if (prioritizeBones) {
        lootTable = new int[loot.length + bones.length];
        System.arraycopy(loot, 0, lootTable, 0, loot.length);
        System.arraycopy(bones, 0, lootTable, loot.length, bones.length);
      } else {
        lootTable = new int[loot.length];
        System.arraycopy(loot, 0, lootTable, 0, loot.length);
      }
      c.sleep(100);
    } else if (commandText.replace(" ", "").toLowerCase().contains("doors")) {
      openDoors = !openDoors;
      c.displayMessage(
          String.format(
              "@or1@Got toggle @red@doors@or1@, turned %s door opening!",
              openDoors ? "on" : "off"));
      c.sleep(100);
      // field is "Controlled", "Aggressive", "Accurate", "Defensive"}
    } else if (str.contains("attack")) {
      c.displayMessage("@red@Got Combat Style Command! - Attack Xp");
      c.displayMessage("@red@Switching to \"Accurate\" combat style!");
      fightMode = 2;
      c.sleep(100);
    } else if (str.contains("strength")) {
      c.displayMessage("@red@Got Combat Style Command! - Strength Xp");
      c.displayMessage("@red@Switching to \"Aggressive\" combat style!");
      fightMode = 1;
      c.sleep(100);
    } else if (str.contains("defense")) {
      c.displayMessage("@red@Got Combat Style Command! - Defense Xp");
      c.displayMessage("@red@Switching to \"Defensive\" combat style!");
      fightMode = 3;
      c.sleep(100);
    } else if (str.contains("controlled")) {
      c.displayMessage("@red@Got Combat Style Command! - Controlled Xp");
      c.displayMessage("@red@Switching to \"Controlled\" combat style!");
      fightMode = 0;
      c.sleep(100);
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("bury")) bonesBuried++;
    if (message.contains("successfully")) spellsCasted++;
    else if (message.equals("I can't get a clear shot from here")) {
      // ! This lags very badly for some reason
      status = "Walking to NPC to get a shot...";
      c.walktoNPCAsync(currentAttackingNpc);
      c.sleep(640);
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      int yellow = 0xF1FA8C;
      int red = 0xFF5555;
      int white = 0xF8F8F2;
      int purple = 0xBD93F9;

      paintBuilder.setBorderColor(purple);
      paintBuilder.setBackgroundColor(0x282A36, 255);

      int[] skillNameColors = {
        0xd41300, 0x821508, 0x0b840b, 0x5b95f2, 0x5aa60e, 0x3169d8, 0x8ae8fc
      };

      paintBuilder.setTitleMultipleColor(
          new String[] {"AIO", "Fighter"},
          new int[] {yellow, red},
          new int[] {(paintBuilder.getWidth() - c.getStringWidth("AIOFighter", 6)) / 2, 34},
          4);

      paintBuilder.addRow(
          rowBuilder.multipleStringRow(
              new String[] {"Dvorak", "&", "Seatta"},
              new int[] {red, white, purple},
              new int[] {
                (paintBuilder.getWidth() - c.getStringWidth("Dvorak & Seatta", 1)) / 2, 42, 11
              },
              1));

      paintBuilder.addRow(rowBuilder.centeredSingleStringRow(status, yellow, 1));

      paintBuilder.addRow(rowBuilder.centeredSingleStringRow(paintBuilder.stringRunTime, white, 1));

      int currHp = c.getCurrentStat(SkillId.HITS.getId());
      int maxHp = c.getBaseStat(SkillId.HITS.getId());
      int currPrayer = c.getCurrentStat(SkillId.PRAYER.getId());
      int maxPrayer = c.getBaseStat(SkillId.PRAYER.getId());

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
              skillNameColors[0]));

      paintBuilder.addRow(
          rowBuilder.progressBarRow(
              currPrayer,
              maxPrayer,
              0x282A36,
              skillNameColors[6],
              purple,
              8,
              paintBuilder.getWidth() - 16,
              14,
              true,
              true,
              "Prayer",
              skillNameColors[6]));

      paintBuilder.addSpacerRow(4);
      AtomicInteger index = new AtomicInteger();
      startingXP.forEach(
          (skill, startXP) -> {
            int currentXp = c.getStatXp(skill.getId());
            int gainedXP = currentXp - startXP;
            if (gainedXP > 0) {

              paintBuilder.addRow(
                  rowBuilder.multipleStringRow(
                      new String[] {
                        c.getSkillNamesLong()[skill.getId()],
                        paintBuilder.stringFormatInt(gainedXP),
                        paintBuilder.stringAmountPerHour(gainedXP)
                      },
                      new int[] {skillNameColors[index.get()], white, yellow},
                      new int[] {4, 52, 52},
                      1));
            }
            index.getAndIncrement();
          });

      if (buryBones) {
        paintBuilder.addSpacerRow(4);
        paintBuilder.addRow(
            rowBuilder.singleSpriteMultipleStringRow(
                ItemId.BONES.getId(),
                60,
                4,
                new String[] {
                  String.valueOf(bonesBuried), paintBuilder.stringAmountPerHour(bonesBuried)
                },
                new int[] {white, yellow},
                new int[] {52, 52},
                12));
      }
      if (maging) {
        paintBuilder.addSpacerRow(4);
        paintBuilder.addRow(
            rowBuilder.singleSpriteMultipleStringRow(
                ItemId.STAFF.getId(),
                60,
                4,
                new String[] {
                  String.valueOf(spellsCasted), paintBuilder.stringAmountPerHour(spellsCasted)
                },
                new int[] {white, yellow},
                new int[] {52, 52},
                12));
      }
      paintBuilder.draw();
    }
  }
}
