package scripting.idlescript.other.AIOAIO.core.gui;

import bot.Main;
import java.awt.*;
import javax.swing.*;
import scripting.idlescript.other.AIOAIO.AIOAIO;
import scripting.idlescript.other.AIOAIO.core.AIOAIO_Skill;

public class Tasks_GUI {
  private JPanel tasksPanel;
  private JList<AIOAIO_Skill> skillList;
  private DefaultListModel<AIOAIO_Skill> skillListModel;
  private JButton startButton;
  private TasksPanel tasksContentPanel;

  public Tasks_GUI() {
    initializeComponents();
  }

  private void initializeComponents() {
    tasksPanel = new JPanel(new BorderLayout());

    skillListModel = new DefaultListModel<>();
    AIOAIO.state.botConfig.skills.forEach(skillListModel::addElement);
    skillList = new JList<>(skillListModel);
    skillList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    skillList.addListSelectionListener(
        e -> {
          if (!e.getValueIsAdjusting()) {
            tasksContentPanel.updatePanel();
          }
        });

    tasksContentPanel = new TasksPanel(skillList, skillListModel);
    startButton = new JButton("Start");
    startButton.addActionListener(
        e -> {
          AIOAIO_GUI.scriptFrame.dispose();
          AIOAIO.state.botConfig.saveConfig();
          AIOAIO.state.startPressed = true;
          Main.getController().log("AIO AIO starting!");
        });

    tasksPanel.add(createSectionWithTitle("Skills", new JScrollPane(skillList)), BorderLayout.WEST);
    tasksPanel.add(createSectionWithTitle("Tasks", tasksContentPanel), BorderLayout.CENTER);
    tasksPanel.add(startButton, BorderLayout.SOUTH);
  }

  public JPanel getPanel() {
    return tasksPanel;
  }

  private JPanel createSectionWithTitle(String title, Component component) {
    JPanel sectionPanel = new JPanel(new BorderLayout());
    JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
    sectionPanel.add(titleLabel, BorderLayout.NORTH);
    sectionPanel.add(component, BorderLayout.CENTER);
    return sectionPanel;
  }
}
