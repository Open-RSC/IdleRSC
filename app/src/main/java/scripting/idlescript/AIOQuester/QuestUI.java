package scripting.idlescript.AIOQuester;

import static scripting.idlescript.AIOQuester.AIOQuester.c;

import bot.Main;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import scripting.idlescript.AIOQuester.models.Quest;
import scripting.idlescript.AIOQuester.models.QuestDef;

// TODO: Add amount checking for required equipped items for ammunition
public class QuestUI {
  static SpringLayout sl = new SpringLayout();
  static final JFrame frame = new JFrame("AIOQuester");
  static final JPanel panel = new JPanel();
  static final String[] columnNames = {"Quests"};
  static final DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
  static JTable table =
      new JTable(tableModel) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return false;
        }
      };
  static final JButton startBtn = new JButton("Select a quest");
  static final JTableHeader header = table.getTableHeader();
  static final JScrollPane questScroller = new JScrollPane(table);
  static final JTextPane descriptionTextPane = new JTextPane();
  static final JScrollPane descriptionScroller = new JScrollPane(descriptionTextPane);
  static final JLabel descLabel = new JLabel("Description");
  static ListSelectionModel selectionModel = table.getSelectionModel();
  static final JCheckBox showCanStartCheckbox = new JCheckBox("Show only start-able quests");

  static boolean started;
  static QuestDef selected;

  /**
   * Shows the script ui and waits for it to return a QuestDef
   *
   * @return QuestDef
   */
  public static QuestDef showFrame() {
    selected = null;
    started = false;
    table.clearSelection();

    panel.setLayout(sl);
    panel.add(startBtn);
    panel.add(questScroller);
    panel.add(descriptionScroller);
    panel.add(descLabel);
    panel.add(showCanStartCheckbox);
    setTheming();
    setConstraints();
    populateTable();

    frame.add(panel);

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    // Listeners
    selectionModel.addListSelectionListener(e -> tableSelectionChanged());
    startBtn.addActionListener(e -> buttonPressed());
    showCanStartCheckbox.addActionListener(e -> populateTable());

    while (c.isRunning() && !started && frame.isVisible()) {
      c.sleep(640);
    }
    sl.removeLayoutComponent(frame);
    if (started) return selected;
    return null;
  }

  /** Populates the table with quests */
  private static void populateTable() {
    selected = null;
    descriptionTextPane.setText("");
    startBtn.setText("Select a quest");
    startBtn.setEnabled(false);
    tableModel.setRowCount(0);

    boolean onlyStartable = showCanStartCheckbox.isSelected();
    System.out.println(
        onlyStartable ? "Showing quests with met requirements" : "Showing all available quests");
    ArrayList<QuestDef> quests = new ArrayList<>();
    for (Quest quest : Quest.values()) {
      QuestDef q = quest.getQuestDef();

      if (onlyStartable) {
        if (q.isDisplayedStartable()) quests.add(q);
      } else {
        if (q.isQuestDefined()) quests.add(q);
      }
    }
    if (!quests.isEmpty()) quests.forEach(q -> tableModel.addRow(new String[] {q.getName()}));
  }

  /** Manages the JFrame's, and its child components' theming */
  private static void setTheming() {
    frame.getContentPane().setBackground(Main.primaryBG);
    frame.getContentPane().setForeground(Main.primaryFG);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setPreferredSize(new Dimension(600, 400));

    panel.setBackground(Main.primaryBG);
    panel.setForeground(Main.primaryFG);

    startBtn.setEnabled(false);
    startBtn.setBackground(Main.secondaryBG);
    startBtn.setForeground(Main.secondaryFG);

    questScroller.setBackground(Main.primaryBG);
    questScroller.setForeground(Main.primaryFG);
    questScroller.getViewport().setBackground(Main.primaryBG.brighter());
    questScroller.setBorder(BorderFactory.createEmptyBorder());

    descLabel.setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));
    descLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descLabel.setBackground(Main.primaryBG);
    descLabel.setForeground(Main.primaryFG);

    descriptionTextPane.setEditable(false);
    descriptionTextPane.setBackground(Main.primaryBG.brighter());
    descriptionTextPane.setForeground(Main.primaryFG);

    descriptionScroller.setBackground(Main.primaryBG.brighter());
    descriptionScroller.setForeground(Main.primaryFG);
    descriptionScroller.setBorder(BorderFactory.createEmptyBorder());

    showCanStartCheckbox.setBackground(Main.primaryBG);
    showCanStartCheckbox.setForeground(Main.primaryFG);
    showCanStartCheckbox.setHorizontalAlignment(SwingConstants.CENTER);
    showCanStartCheckbox.setSelected(true);
    showCanStartCheckbox.setToolTipText(
        "<html>Shows only the quests that can be started by the player.<br>Completed quests and quests requiring missing skills or prerequisite quests are hidden.</html>");

    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setBorder(BorderFactory.createEmptyBorder());
    table.setBackground(Main.primaryBG.brighter());
    table.setForeground(Main.primaryFG);

    header.setReorderingAllowed(false);
    header.setResizingAllowed(false);
    header.setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));
    header.setBackground(Main.primaryBG);
    header.setForeground(Main.primaryFG);
    header.setBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("controlDkShadow")));
    header.setDefaultRenderer(
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
            label.setBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, UIManager.getColor("controlDkShadow")));
            label.setFont(header.getFont().deriveFont(Font.BOLD, 15f));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBackground(Main.primaryBG);
            return label;
          }
        });
  }

  /** Manages the JFrame's SpringLayout constraints */
  private static void setConstraints() {
    // Set constraints for start button
    sl.putConstraint(SpringLayout.SOUTH, startBtn, -4, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.NORTH, startBtn, -24, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.WEST, startBtn, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, startBtn, -4, SpringLayout.EAST, panel);

    // Set constraints for the quest list
    int listWidth = (int) ((frame.getPreferredSize().getWidth() * .3) + 4);
    sl.putConstraint(SpringLayout.WIDTH, questScroller, listWidth, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.NORTH, questScroller, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, questScroller, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.SOUTH, questScroller, -28, SpringLayout.SOUTH, startBtn);

    // Set constraints for start-able checkbox
    sl.putConstraint(SpringLayout.SOUTH, showCanStartCheckbox, -4, SpringLayout.NORTH, startBtn);
    sl.putConstraint(SpringLayout.WEST, showCanStartCheckbox, 4, SpringLayout.EAST, questScroller);
    sl.putConstraint(SpringLayout.EAST, showCanStartCheckbox, -4, SpringLayout.EAST, panel);

    // Set constraints for description label
    sl.putConstraint(SpringLayout.NORTH, descLabel, 6, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, descLabel, 4, SpringLayout.EAST, questScroller);
    sl.putConstraint(SpringLayout.EAST, descLabel, -4, SpringLayout.EAST, panel);
    sl.putConstraint(SpringLayout.SOUTH, descLabel, 18, SpringLayout.NORTH, panel);

    // Set constraints for description box
    sl.putConstraint(SpringLayout.NORTH, descriptionScroller, 3, SpringLayout.SOUTH, descLabel);
    sl.putConstraint(
        SpringLayout.SOUTH, descriptionScroller, -4, SpringLayout.NORTH, showCanStartCheckbox);
    sl.putConstraint(SpringLayout.EAST, descriptionScroller, -4, SpringLayout.EAST, panel);
    sl.putConstraint(SpringLayout.WEST, descriptionScroller, 4, SpringLayout.EAST, questScroller);
  }

  private static void buttonPressed() {
    started = true;
    frame.dispose();
  }

  private static void tableSelectionChanged() {
    if (table.getSelectedRow() > -1) {
      selected =
          Quest.getQuestDefFromName(String.valueOf(table.getValueAt(table.getSelectedRow(), 0)));
    }
    if (selected != null) {
      if (selected.isStartable()) {
        startBtn.setText("Start " + selected.getName());
        startBtn.setEnabled(true);
      } else {
        if (selected.getId() > -1 && c.getQuestStage(selected.getId()) == -1) {
          startBtn.setText(selected.getName() + " is already completed");

        } else {
          startBtn.setText("Unable to start " + selected.getName());
        }
        startBtn.setEnabled(false);
      }
      updateDescription();
    }
  }

  private static void updateDescription() {
    StringBuilder description = new StringBuilder(selected.getDescription());

    // ✔ Leave this as a unicode escape sequence, or it'll apparently break on Windows
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    String reqComplete = " \u2714";
    String reqUnknown = " ?";

    if (selected.getRequiredQuests() != null && selected.getRequiredQuests().length > 0) {
      description.append("\n\nQuests Required: ");
      for (Quest quest : selected.getRequiredQuests()) {
        QuestDef def = quest.getQuestDef();
        description.append("\n - ").append(def.getName());
        if (!def.isMiniquest() && c.getQuestStage(def.getId()) == -1)
          description.append(reqComplete);
        if (def.isMiniquest()) description.append(" (Unable to check mini-quests)");
      }
    }

    if (selected.getRequiredSkills() != null && selected.getRequiredSkills().length > 0) {
      description.append("\n\nLevels Required: ");
      for (int[] skill : selected.getRequiredSkills()) {
        String skillName = c.getSkillNamesLong()[skill[0]];
        String level = String.valueOf(skill[1]);
        description.append("\n - ").append(level).append(" ").append(skillName);
        if (c.getBaseStat(skill[0]) >= skill[1]) description.append(reqComplete);
      }
    }

    if (selected.getRequiredEquippedItems() != null
        && selected.getRequiredEquippedItems().length > 0) {
      description.append("\n\nEquipped Items Required: ");
      for (int[] item : selected.getRequiredEquippedItems()) {
        int id = item[0];
        int amount = item[1];
        String name = c.getItemName(item[0]);
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        description.append("\n - ");
        if (amount > 1) description.append(amount).append(" ");
        description.append(name);
        // TODO: Eventually add amount checking here for equipped ammunition amount
        if (c.isItemIdEquipped(id)) description.append(reqComplete);
      }
    }

    if (selected.getRequiredInventoryItems() != null
        && selected.getRequiredInventoryItems().length > 0) {
      description.append("\n\nInventory Items Required: ");
      for (int[] item : selected.getRequiredInventoryItems()) {
        int id = item[0];
        int amount = item[1];
        String name = c.getItemName(item[0]);
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        description.append("\n - ").append(amount).append(" ").append(name);
        if (c.getInventoryItemCount(id) >= amount) description.append(reqComplete);
      }
    }

    if (selected.getRequiredBankItems() != null && selected.getRequiredBankItems().length > 0) {
      if (c.getPlayerMode() != 2 && QuestHandler.bankItems == null) {
        description.append("\n\nOPEN A BANK THEN RESTART THE SCRIPT TO POPULATE ITEMS");
        description.append("\nBanked Items Required: ");
      } else {
        description.append("\n\nBanked Items Required: ");
      }
      for (int[] item : selected.getRequiredBankItems()) {
        int id = item[0];
        int amount = item[1];
        String name = c.getItemName(item[0]);
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        description.append("\n - ").append(amount).append(" ").append(name);

        if (c.getPlayerMode() == 2) {
          description.append(" UIM");
        } else if (QuestHandler.bankItems == null) {
          description.append(reqUnknown);
        } else if (QuestHandler.bankItems.containsKey(id)
            && QuestHandler.bankItems.get(id) >= amount) {
          description.append(reqComplete);
        }
      }
    }

    int neededQP = selected.getRequiredQuestPoints();
    if (neededQP > 0) {
      description.append("\n\nQuest Points Required: ").append(selected.getRequiredQuestPoints());
      if (neededQP > c.getQuestPoints()) {
        description.append(" (").append(c.getQuestPoints()).append(")");
      } else {
        description.append(reqComplete);
      }
    }

    int neededSlots = selected.getRequiredEmptyInventorySlots();
    if (neededSlots > 0) {
      description.append("\n\nEmpty Inventory Slots Required: ").append(neededSlots);
      if (neededSlots <= 30 - c.getInventoryItemCount()) description.append(reqComplete);
    }

    descriptionTextPane.setText(description.toString());
  }
}
