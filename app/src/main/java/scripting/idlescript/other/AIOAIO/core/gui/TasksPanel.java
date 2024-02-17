package scripting.idlescript.other.AIOAIO.core.gui;

import java.awt.Dimension;
import javax.swing.*;
import scripting.idlescript.other.AIOAIO.core.AIOAIO_Skill;
import scripting.idlescript.other.AIOAIO.core.AIOAIO_Task;

public class TasksPanel extends JPanel {
  private JList<AIOAIO_Skill> skillList;
  private DefaultListModel<AIOAIO_Skill> skillListModel;
  private JButton enableDisableSkillButton;

  public TasksPanel(JList<AIOAIO_Skill> skillList, DefaultListModel<AIOAIO_Skill> skillListModel) {
    this.skillList = skillList;
    this.skillListModel = skillListModel;
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setPreferredSize(new Dimension(200, 100));
    // Initialize and add the enable/disable button here
    enableDisableSkillButton = new JButton();
    enableDisableSkillButton.setAlignmentX(LEFT_ALIGNMENT); // Align button to the left
    add(enableDisableSkillButton); // Add the button to ensure it's always at the top left
    updatePanel();
  }

  public void updatePanel() {
    removeAll(); // Remove all components including the enable/disable button

    // Re-add the enable/disable button to ensure it's always at the top left
    enableDisableSkillButton = new JButton();
    enableDisableSkillButton.setAlignmentX(LEFT_ALIGNMENT);
    add(enableDisableSkillButton);

    AIOAIO_Skill selectedSkill = skillList.getSelectedValue();
    if (selectedSkill == null) {
      revalidate();
      repaint();
      return;
    }

    // Update the button text and action listener
    enableDisableSkillButton.setText(selectedSkill.isEnabled() ? "Disable" : "Enable");
    enableDisableSkillButton.addActionListener(
        e -> {
          if (selectedSkill != null) {
            selectedSkill.setEnabled(!selectedSkill.isEnabled());
            skillListModel.setElementAt(selectedSkill, skillList.getSelectedIndex());
            updatePanel(); // Recursively update the panel to reflect changes
          }
        });

    for (AIOAIO_Task task : selectedSkill.getTasks()) {
      JCheckBox checkBox = new JCheckBox(task.getName(), task.isEnabled());
      checkBox.setAlignmentX(LEFT_ALIGNMENT); // Align checkboxes to the left
      checkBox.addActionListener(
          e -> {
            task.setEnabled(checkBox.isSelected());
            updatePanel(); // Optionally update the panel if you want immediate visual feedback
          });
      add(checkBox);
    }

    revalidate();
    repaint();
  }
}
