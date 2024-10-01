package scripting.idlescript;

import bot.Main;
import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;
import controller.Controller;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import models.entities.ItemId;
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
          "Dvorak, Redesigned UI by Seatta",
          "This is your standard melee/range/mage fighter script.\n\n"
              + "Chat commands can be used to direct the bot\n"
              + "    Control\n"
              + "        ::bones\n"
              + "        ::prioritize\n"
              + "        ::doors\n"
              + "    Combat Styles:\n"
              + "        ::controlled\n"
              + "        ::attack\n"
              + "        ::strength\n"
              + "        ::defense");

  private int fightMode = 2;
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
  private final int[] bones = {20, 413, 604, 814};
  private final int[] bowIds = {188, 189, 648, 649, 650, 651, 652, 653, 654, 655, 656, 657, 59, 60};
  private final int[] doorObjectIds = {60, 64};

  // do not modify these
  private int currentAttackingNpc = -1;
  private int[] lootTable = null;
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
  private final JLabel switchLabel = new JLabel("In melee range weapon switch id:");

  private final JTextField npcIdsField = new JTextField("");
  private final JTextField wanderField = new JTextField("20");
  private final JTextField eatAtHpField =
      new JTextField(String.valueOf(c.getCurrentStat(c.getStatId("Hits")) / 2));
  private final JTextField lootField = new JTextField("-1");
  private final JTextField switchIdField = new JTextField("81");

  private final JCheckBox openDoorsCheckbox =
      new JCheckBox("Open doors/gates? (if On, then set a max wander!)");
  private final JCheckBox buryBonesCheckbox = new JCheckBox("Loot and bury bones?");
  private final JCheckBox prioritizeBonesCheckbox =
      new JCheckBox("Prioritize bones over attacking?");
  private final JCheckBox magingCheckbox = new JCheckBox("Magic?");
  private final JCheckBox rangingCheckbox = new JCheckBox("Ranging?");

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

    populateNearbyNPCsTable();
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
    panel.add(fightModeLabel);
    panel.add(modeCombobBox);
    panel.add(npcIdLabel);
    panel.add(nearbyScrollPane);
    panel.add(npcIdsField);
    panel.add(refreshBtn);
    panel.add(wanderLabel);
    panel.add(wanderField);
    panel.add(eatAtHpField);
    panel.add(eatLabel);
    panel.add(lootLabel);
    panel.add(lootField);
    panel.add(openDoorsCheckbox);
    panel.add(buryBonesCheckbox);
    panel.add(prioritizeBonesCheckbox);
    panel.add(magingCheckbox);
    panel.add(spellComboBox);
    panel.add(rangingCheckbox);
    panel.add(arrowComboBox);
    panel.add(switchLabel);
    panel.add(switchIdField);
    panel.add(startBtn);
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
          if (validateFields(npcIdsField, wanderField, eatAtHpField, lootField, switchIdField)) {
            setValuesFromGUI(
                modeCombobBox,
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
            c.setStatus("@red@Started...");

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
    refreshBtn.addActionListener(e -> populateNearbyNPCsTable());

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

    sl.putConstraint(SpringLayout.NORTH, eatLabel, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, eatLabel, 22, SpringLayout.EAST, modeCombobBox);
    sl.putConstraint(SpringLayout.SOUTH, eatLabel, 18, SpringLayout.NORTH, eatLabel);

    sl.putConstraint(SpringLayout.NORTH, modeCombobBox, 4, SpringLayout.SOUTH, fightModeLabel);
    sl.putConstraint(SpringLayout.WEST, modeCombobBox, 4, SpringLayout.EAST, nearbyScrollPane);
    sl.putConstraint(SpringLayout.EAST, modeCombobBox, 100, SpringLayout.WEST, modeCombobBox);
    sl.putConstraint(SpringLayout.SOUTH, modeCombobBox, 18, SpringLayout.NORTH, modeCombobBox);

    sl.putConstraint(SpringLayout.NORTH, eatAtHpField, 4, SpringLayout.SOUTH, eatLabel);
    sl.putConstraint(SpringLayout.WEST, eatAtHpField, 24, SpringLayout.EAST, modeCombobBox);
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
    final Color backgroundColor = Main.getThemeBackColor();
    final Color foregroundColor = Main.getThemeTextColor();

    TableColumn nearbyColumn1 = nearbyTable.getColumnModel().getColumn(0);
    nearbyColumn1.setPreferredWidth(18);
    nearbyColumn1.setMinWidth(18);
    nearbyColumn1.setMaxWidth(18);
    TableColumn nearbyColumn3 = nearbyTable.getColumnModel().getColumn(2);
    nearbyColumn3.setPreferredWidth(40);
    nearbyColumn3.setMinWidth(40);
    nearbyColumn3.setMaxWidth(40);

    frame.getContentPane().setBackground(backgroundColor);
    frame.getContentPane().setForeground(foregroundColor);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setPreferredSize(new Dimension(600, 300));

    panel.setBackground(backgroundColor);
    panel.setForeground(foregroundColor);

    npcIdLabel.setForeground(foregroundColor);
    lootLabel.setForeground(foregroundColor);
    wanderLabel.setForeground(foregroundColor);
    switchLabel.setForeground(foregroundColor);
    fightModeLabel.setForeground(foregroundColor);
    eatLabel.setForeground(foregroundColor);

    buryBonesCheckbox.setForeground(foregroundColor);
    buryBonesCheckbox.setBackground(backgroundColor);
    magingCheckbox.setForeground(foregroundColor);
    magingCheckbox.setBackground(backgroundColor);
    openDoorsCheckbox.setForeground(foregroundColor);
    openDoorsCheckbox.setBackground(backgroundColor);
    prioritizeBonesCheckbox.setForeground(foregroundColor);
    prioritizeBonesCheckbox.setBackground(backgroundColor);
    rangingCheckbox.setForeground(foregroundColor);
    rangingCheckbox.setBackground(backgroundColor);

    refreshBtn.setBackground(backgroundColor.darker());
    refreshBtn.setForeground(foregroundColor);
    startBtn.setBackground(backgroundColor.darker());
    startBtn.setForeground(foregroundColor);

    nearbyTable.setRowSelectionAllowed(false);
    nearbyTable.setBorder(BorderFactory.createEmptyBorder());
    nearbyTable.setBackground(backgroundColor.brighter());
    nearbyTable.setForeground(foregroundColor);

    npcIdsField.setForeground(foregroundColor);
    npcIdsField.setBackground(backgroundColor.brighter().brighter());
    eatAtHpField.setForeground(foregroundColor);
    eatAtHpField.setBackground(backgroundColor.brighter().brighter());
    wanderField.setForeground(foregroundColor);
    wanderField.setBackground(backgroundColor.brighter().brighter());
    lootField.setForeground(foregroundColor);
    lootField.setBackground(backgroundColor.brighter().brighter());
    switchIdField.setForeground(foregroundColor);
    switchIdField.setBackground(backgroundColor.brighter().brighter());

    modeCombobBox.setForeground(foregroundColor);
    modeCombobBox.setBackground(backgroundColor.brighter().brighter());
    modeCombobBox.setBorder(BorderFactory.createEmptyBorder());
    arrowComboBox.setForeground(foregroundColor);
    arrowComboBox.setBackground(backgroundColor.brighter().brighter());
    arrowComboBox.setBorder(BorderFactory.createEmptyBorder());
    spellComboBox.setForeground(foregroundColor);
    spellComboBox.setBackground(backgroundColor.brighter().brighter());
    spellComboBox.setBorder(BorderFactory.createEmptyBorder());

    nearbyScrollPane.setBackground(backgroundColor);
    nearbyScrollPane.setForeground(foregroundColor);
    nearbyScrollPane.getViewport().setBackground(backgroundColor.brighter());
    nearbyScrollPane.setBorder(BorderFactory.createEmptyBorder());

    nearbyHeader.setReorderingAllowed(false);
    nearbyHeader.setResizingAllowed(false);
    nearbyHeader.setFont(nearbyTable.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));
    nearbyHeader.setBackground(backgroundColor);
    nearbyHeader.setForeground(foregroundColor);
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
            label.setBackground(backgroundColor);
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
    paintBuilder.start(4, 18, 180);
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

        if (!isWithinWander(c.currentX(), c.currentY())) {
          c.setStatus("@red@Out of range! Walking back.");
          c.walkTo(startTile[0], startTile[1], 0, true, true);
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
          c.walkTo(c.currentX(), c.currentY(), 0, true, true);

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
      JComboBox<String> spellNameField,
      JCheckBox rangingCheckbox,
      JComboBox<String> arrowIdField,
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
    this.spellId = c.getSpellIdFromName((String) spellNameField.getSelectedItem());
    this.ranging = rangingCheckbox.isSelected();
    this.arrowId = arrowMap.get((String) arrowIdField.getSelectedItem());
    this.switchId = Integer.parseInt(switchIdField.getText());
  }

  private void populateNearbyNPCsTable() {
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
  public void questMessageInterrupt(String message) {
    if (message.contains("bury")) bonesBuried++;
    if (message.contains("successfully")) spellsCasted++;
    else if (message.equals("I can't get a clear shot from here")) {
      // ! This lags very badly for some reason
      c.setStatus("@red@Walking to NPC to get a shot...");
      c.walktoNPCAsync(currentAttackingNpc);
      c.sleep(640);
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      paintBuilder.setBorderColor(0xBD93F9);
      paintBuilder.setBackgroundColor(0x282A36, 255);
      String bonesPerHr = paintBuilder.stringAmountPerHour(bonesBuried);
      String spellsPerHr = paintBuilder.stringAmountPerHour(spellsCasted);

      int yellow = 0xF1FA8C;
      int red = 0xFF5555;
      int white = 0xF8F8F2;
      int purple = 0xBD93F9;

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

      paintBuilder.addRow(rowBuilder.centeredSingleStringRow(paintBuilder.stringRunTime, white, 1));
      paintBuilder.addSpacerRow(8);
      if (buryBones) {
        paintBuilder.addRow(
            rowBuilder.singleSpriteMultipleStringRow(
                ItemId.BONES.getId(),
                60,
                4,
                new String[] {"Buried: " + bonesBuried, bonesPerHr},
                new int[] {white, yellow},
                new int[] {25, 64},
                12));
      }
      if (maging) {
        paintBuilder.addRow(
            rowBuilder.singleSpriteMultipleStringRow(
                ItemId.STAFF.getId(),
                60,
                4,
                new String[] {"Casts: " + spellsCasted, spellsPerHr},
                new int[] {white, yellow},
                new int[] {25, 64},
                12));
      }

      paintBuilder.draw();
    }
  }
}
