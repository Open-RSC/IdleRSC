package scripting.idlescript.other.AIOAIO.core.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.*;
import scripting.idlescript.other.AIOAIO.core.AIOAIO_Skill;
import scripting.idlescript.other.AIOAIO.core.AIOAIO_Task;

public class TasksPanel extends JPanel {
  private JList<AIOAIO_Skill> skillList;
  private DefaultListModel<AIOAIO_Skill> skillListModel;
  private JButton enableDisableSkillButton;
  private JPanel scrollablePanel; // Panel to hold tasks, making them scrollable

  public TasksPanel(JList<AIOAIO_Skill> skillList, DefaultListModel<AIOAIO_Skill> skillListModel) {
    this.skillList = skillList;
    this.skillListModel = skillListModel;
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setPreferredSize(new Dimension(200, 100));

    enableDisableSkillButton = new JButton();
    enableDisableSkillButton.setAlignmentX(LEFT_ALIGNMENT);
    add(enableDisableSkillButton);

    // Initialize scrollablePanel and wrap it in a JScrollPane
    scrollablePanel = new JPanel();
    scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(scrollablePanel);
    scrollPane.setPreferredSize(new Dimension(200, 100));
    scrollPane.setAlignmentX(LEFT_ALIGNMENT);
    add(scrollPane); // Add the JScrollPane to the TasksPanel

    updatePanel();
  }

  public void updatePanel() {
    scrollablePanel.removeAll(); // Clear previous task checkboxes

    AIOAIO_Skill selectedSkill = skillList.getSelectedValue();
    if (selectedSkill != null) {
      // Update the button text based on the selected skill's status
      enableDisableSkillButton.setText(selectedSkill.isEnabled() ? "Disable" : "Enable");

      // Remove all action listeners from the button to prevent action listener
      // stacking
      ActionListener[] listeners = enableDisableSkillButton.getActionListeners();
      for (ActionListener listener : listeners) {
        enableDisableSkillButton.removeActionListener(listener);
      }

      // Add a new action listener for the enable/disable button
      enableDisableSkillButton.addActionListener(
          e -> {
            // Toggle the enable state of the selected skill
            selectedSkill.setEnabled(!selectedSkill.isEnabled());
            // Update the model to reflect the change
            skillListModel.setElementAt(selectedSkill, skillList.getSelectedIndex());
            // Recursively update the panel to reflect changes
            updatePanel();
          });

      // Loop through tasks of the selected skill and add them to the panel
      for (AIOAIO_Task task : selectedSkill.getTasks()) {
        JCheckBox checkBox = new JCheckBox(task.getName(), task.isEnabled());
        checkBox.setAlignmentX(LEFT_ALIGNMENT);
        checkBox.addActionListener(
            e -> {
              // Toggle the enable state of the task
              task.setEnabled(checkBox.isSelected());
              // Optionally update the panel for immediate visual feedback
              updatePanel();
            });
        scrollablePanel.add(checkBox); // Add checkboxes to the scrollable panel
      }
    }

    scrollablePanel.revalidate();
    scrollablePanel.repaint();
  }
}
