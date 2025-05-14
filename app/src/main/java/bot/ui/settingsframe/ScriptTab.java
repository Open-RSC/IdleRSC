package bot.ui.settingsframe;

import bot.ui.components.CustomCheckBox;
import bot.ui.components.TextFieldPanel;
import java.awt.*;
import java.util.Properties;
import javax.swing.*;

public class ScriptTab extends JPanel implements ISettingsTab {
  final SpringLayout sl;
  final int compWidth = 180;
  final int compHeight = 41;

  public TextFieldPanel scriptName, scriptArgs, spellId, attackItems, strengthItems, defenseItems;
  public CustomCheckBox locationWalkerCheckBox;

  ScriptTab(SpringLayout springLayout) {
    super(springLayout);
    sl = springLayout;
    initializeComponents();
    setConstraints();
  }

  @Override
  public void setConstraints() {
    // Left Side
    sl.putConstraint(SpringLayout.NORTH, scriptName, 4, SpringLayout.NORTH, this);
    sl.putConstraint(SpringLayout.SOUTH, scriptName, compHeight, SpringLayout.NORTH, scriptName);
    sl.putConstraint(SpringLayout.WEST, scriptName, 4, SpringLayout.WEST, this);
    sl.putConstraint(SpringLayout.EAST, scriptName, compWidth, SpringLayout.WEST, scriptName);

    sl.putConstraint(SpringLayout.NORTH, attackItems, 4, SpringLayout.SOUTH, scriptName);
    sl.putConstraint(SpringLayout.WEST, attackItems, 4, SpringLayout.WEST, this);
    sl.putConstraint(SpringLayout.HEIGHT, attackItems, 0, SpringLayout.HEIGHT, scriptName);
    sl.putConstraint(SpringLayout.WIDTH, attackItems, 0, SpringLayout.WIDTH, scriptName);

    sl.putConstraint(SpringLayout.NORTH, defenseItems, 4, SpringLayout.SOUTH, attackItems);
    sl.putConstraint(SpringLayout.WEST, defenseItems, 4, SpringLayout.WEST, this);
    sl.putConstraint(SpringLayout.HEIGHT, defenseItems, 0, SpringLayout.HEIGHT, scriptName);
    sl.putConstraint(SpringLayout.WIDTH, defenseItems, 0, SpringLayout.WIDTH, scriptName);

    // Right Side
    sl.putConstraint(SpringLayout.NORTH, scriptArgs, 4, SpringLayout.NORTH, this);
    sl.putConstraint(SpringLayout.SOUTH, scriptArgs, compHeight, SpringLayout.NORTH, scriptArgs);
    sl.putConstraint(SpringLayout.EAST, scriptArgs, -4, SpringLayout.EAST, this);
    sl.putConstraint(SpringLayout.WEST, scriptArgs, -compWidth, SpringLayout.EAST, scriptArgs);

    sl.putConstraint(SpringLayout.NORTH, strengthItems, 4, SpringLayout.SOUTH, scriptArgs);
    sl.putConstraint(SpringLayout.EAST, strengthItems, -4, SpringLayout.EAST, this);
    sl.putConstraint(SpringLayout.HEIGHT, strengthItems, 0, SpringLayout.HEIGHT, scriptArgs);
    sl.putConstraint(SpringLayout.WIDTH, strengthItems, 0, SpringLayout.WIDTH, scriptName);

    sl.putConstraint(SpringLayout.NORTH, spellId, 4, SpringLayout.SOUTH, strengthItems);
    sl.putConstraint(SpringLayout.EAST, spellId, -4, SpringLayout.EAST, this);
    sl.putConstraint(SpringLayout.HEIGHT, spellId, 0, SpringLayout.HEIGHT, scriptArgs);
    sl.putConstraint(SpringLayout.WIDTH, spellId, 0, SpringLayout.WIDTH, scriptArgs);

    sl.putConstraint(SpringLayout.NORTH, locationWalkerCheckBox, 4, SpringLayout.SOUTH, spellId);
    sl.putConstraint(SpringLayout.WEST, locationWalkerCheckBox, 4, SpringLayout.WEST, this);
    sl.putConstraint(SpringLayout.EAST, locationWalkerCheckBox, -4, SpringLayout.EAST, this);
    sl.putConstraint(
        SpringLayout.SOUTH,
        locationWalkerCheckBox,
        compHeight,
        SpringLayout.NORTH,
        locationWalkerCheckBox);
  }

  @Override
  public void initializeComponents() {
    Component[] comps = {
      scriptName = new TextFieldPanel("Script Name:", "The script to auto-run"),
      scriptArgs = new TextFieldPanel("Script Arguments:", "Arguments for the auto-run script"),
      attackItems =
          new TextFieldPanel(
              "Attack Item Switches (F5):",
              "Comma separated list of item ids. Used for fast switching"),
      strengthItems =
          new TextFieldPanel(
              "Strength Item Switches (F6):",
              "Comma separated list of item ids. Used for fast switching"),
      defenseItems =
          new TextFieldPanel(
              "Defence Item Switches (F7):",
              "Comma separated list of item ids. Used for fast switching"),
      spellId =
          new TextFieldPanel(
              "Spell Quick Cast Id (F8):",
              "Spell id to prepare for casting when pressing the F8 key"),
      locationWalkerCheckBox =
          new CustomCheckBox(
              "Use LocationWalker For Walker Button",
              "Changes the walker button to use LocationWalker instead of PathWalker",
              false)
    };
    for (Component comp : comps) add(comp);
  }

  @Override
  public void loadSettings(Properties p) {
    scriptName.setText(p.getProperty("script-name", ""));
    scriptArgs.setText(p.getProperty("script-arguments", ""));
    spellId.setText(p.getProperty("spell-id", "-1"));
    attackItems.setText(p.getProperty("attack-items", ""));
    strengthItems.setText(p.getProperty("defence-items", ""));
    defenseItems.setText(p.getProperty("strength-items", ""));
    String locWalker = p.getProperty("use-location-walker", "false");
    locationWalkerCheckBox.setSelected(locWalker.equalsIgnoreCase("true"));
  }

  @Override
  public void setDefaultValues() {
    scriptName.setText("");
    scriptArgs.setText("");
    spellId.setText("");
    attackItems.setText("");
    strengthItems.setText("");
    defenseItems.setText("");
    locationWalkerCheckBox.setSelected(false);
  }
}
