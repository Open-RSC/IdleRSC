package bot.ui.settingsframe;

import bot.ui.Theme;
import bot.ui.components.ColorPickerPanel;
import bot.ui.components.ComboBoxPanel;
import bot.ui.components.CustomCheckBox;
import bot.ui.components.TextFieldPanel;
import java.awt.*;
import java.util.Properties;
import javax.swing.*;

public class DisplayTab extends JPanel implements ISettingsTab {
  final SpringLayout sl;
  final int compWidth = 180;
  final int compHeight = 41;

  public ComboBoxPanel themeChoice;
  public ColorPickerPanel primaryBGPanel, primaryFGPanel, secondaryBGPanel, secondaryFGPanel;
  public CustomCheckBox botPaint,
      disableGraphics,
      screenRefresh,
      interlace,
      newIcons,
      newUi,
      keepOpen;
  public TextFieldPanel startPosX, startPosY;

  DisplayTab(SpringLayout springLayout) {
    super(springLayout);
    sl = springLayout;
    initializeComponents();
    setConstraints();
  }

  @Override
  public void setConstraints() {
    // Left Side
    sl.putConstraint(SpringLayout.NORTH, themeChoice, 4, SpringLayout.NORTH, this);
    sl.putConstraint(SpringLayout.SOUTH, themeChoice, compHeight, SpringLayout.NORTH, themeChoice);
    sl.putConstraint(SpringLayout.WEST, themeChoice, 4, SpringLayout.WEST, this);
    sl.putConstraint(SpringLayout.EAST, themeChoice, compWidth, SpringLayout.WEST, themeChoice);

    sl.putConstraint(SpringLayout.NORTH, primaryBGPanel, 4, SpringLayout.SOUTH, themeChoice);
    sl.putConstraint(SpringLayout.HEIGHT, primaryBGPanel, 0, SpringLayout.HEIGHT, themeChoice);
    sl.putConstraint(SpringLayout.WEST, primaryBGPanel, 0, SpringLayout.WEST, themeChoice);
    sl.putConstraint(SpringLayout.EAST, primaryBGPanel, 0, SpringLayout.EAST, themeChoice);

    sl.putConstraint(SpringLayout.NORTH, primaryFGPanel, 4, SpringLayout.SOUTH, primaryBGPanel);
    sl.putConstraint(SpringLayout.HEIGHT, primaryFGPanel, 0, SpringLayout.HEIGHT, themeChoice);
    sl.putConstraint(SpringLayout.WEST, primaryFGPanel, 0, SpringLayout.WEST, themeChoice);
    sl.putConstraint(SpringLayout.EAST, primaryFGPanel, 0, SpringLayout.EAST, themeChoice);

    sl.putConstraint(SpringLayout.NORTH, secondaryBGPanel, 4, SpringLayout.SOUTH, primaryFGPanel);
    sl.putConstraint(SpringLayout.HEIGHT, secondaryBGPanel, 0, SpringLayout.HEIGHT, themeChoice);
    sl.putConstraint(SpringLayout.WEST, secondaryBGPanel, 0, SpringLayout.WEST, themeChoice);
    sl.putConstraint(SpringLayout.EAST, secondaryBGPanel, 0, SpringLayout.EAST, themeChoice);

    sl.putConstraint(SpringLayout.NORTH, secondaryFGPanel, 4, SpringLayout.SOUTH, secondaryBGPanel);
    sl.putConstraint(SpringLayout.HEIGHT, secondaryFGPanel, 0, SpringLayout.HEIGHT, themeChoice);
    sl.putConstraint(SpringLayout.WEST, secondaryFGPanel, 0, SpringLayout.WEST, themeChoice);
    sl.putConstraint(SpringLayout.EAST, secondaryFGPanel, 0, SpringLayout.EAST, themeChoice);

    // Right Side
    sl.putConstraint(SpringLayout.NORTH, startPosX, 4, SpringLayout.NORTH, this);
    sl.putConstraint(SpringLayout.EAST, startPosX, -4, SpringLayout.EAST, this);
    sl.putConstraint(SpringLayout.HEIGHT, startPosX, 0, SpringLayout.HEIGHT, themeChoice);
    sl.putConstraint(SpringLayout.WIDTH, startPosX, 0, SpringLayout.WIDTH, themeChoice);

    sl.putConstraint(SpringLayout.NORTH, startPosY, 4, SpringLayout.SOUTH, startPosX);
    sl.putConstraint(SpringLayout.WEST, startPosY, 0, SpringLayout.WEST, startPosX);
    sl.putConstraint(SpringLayout.HEIGHT, startPosY, 0, SpringLayout.HEIGHT, startPosX);
    sl.putConstraint(SpringLayout.WIDTH, startPosY, 0, SpringLayout.WIDTH, startPosX);

    sl.putConstraint(SpringLayout.NORTH, disableGraphics, 4, SpringLayout.SOUTH, startPosY);
    sl.putConstraint(SpringLayout.SOUTH, disableGraphics, 20, SpringLayout.NORTH, disableGraphics);
    sl.putConstraint(SpringLayout.EAST, disableGraphics, -4, SpringLayout.EAST, this);
    sl.putConstraint(
        SpringLayout.WEST, disableGraphics, -compWidth + 24, SpringLayout.EAST, disableGraphics);

    sl.putConstraint(SpringLayout.NORTH, interlace, 4, SpringLayout.SOUTH, disableGraphics);
    sl.putConstraint(SpringLayout.SOUTH, interlace, 20, SpringLayout.NORTH, interlace);
    sl.putConstraint(SpringLayout.EAST, interlace, 0, SpringLayout.EAST, disableGraphics);
    sl.putConstraint(SpringLayout.WEST, interlace, 0, SpringLayout.WEST, disableGraphics);

    sl.putConstraint(SpringLayout.NORTH, botPaint, 4, SpringLayout.SOUTH, interlace);
    sl.putConstraint(SpringLayout.SOUTH, botPaint, 20, SpringLayout.NORTH, botPaint);
    sl.putConstraint(SpringLayout.EAST, botPaint, 0, SpringLayout.EAST, disableGraphics);
    sl.putConstraint(SpringLayout.WEST, botPaint, 0, SpringLayout.WEST, disableGraphics);

    sl.putConstraint(SpringLayout.NORTH, screenRefresh, 4, SpringLayout.SOUTH, botPaint);
    sl.putConstraint(SpringLayout.SOUTH, screenRefresh, 20, SpringLayout.NORTH, screenRefresh);
    sl.putConstraint(SpringLayout.EAST, screenRefresh, 0, SpringLayout.EAST, disableGraphics);
    sl.putConstraint(SpringLayout.WEST, screenRefresh, 0, SpringLayout.WEST, disableGraphics);

    sl.putConstraint(SpringLayout.NORTH, newUi, 4, SpringLayout.SOUTH, screenRefresh);
    sl.putConstraint(SpringLayout.SOUTH, newUi, 20, SpringLayout.NORTH, newUi);
    sl.putConstraint(SpringLayout.EAST, newUi, 0, SpringLayout.EAST, disableGraphics);
    sl.putConstraint(SpringLayout.WEST, newUi, 0, SpringLayout.WEST, disableGraphics);

    sl.putConstraint(SpringLayout.NORTH, keepOpen, 4, SpringLayout.SOUTH, newUi);
    sl.putConstraint(SpringLayout.SOUTH, keepOpen, 20, SpringLayout.NORTH, keepOpen);
    sl.putConstraint(SpringLayout.EAST, keepOpen, 0, SpringLayout.EAST, disableGraphics);
    sl.putConstraint(SpringLayout.WEST, keepOpen, 0, SpringLayout.WEST, disableGraphics);

    sl.putConstraint(SpringLayout.NORTH, newIcons, 4, SpringLayout.SOUTH, keepOpen);
    sl.putConstraint(SpringLayout.SOUTH, newIcons, 20, SpringLayout.NORTH, newIcons);
    sl.putConstraint(SpringLayout.EAST, newIcons, 0, SpringLayout.EAST, disableGraphics);
    sl.putConstraint(SpringLayout.WEST, newIcons, 0, SpringLayout.WEST, disableGraphics);
  }

  @Override
  public void loadSettings(Properties p) {
    startPosX.setText(p.getProperty("x-position", "-1"));
    startPosY.setText(p.getProperty("y-position", "-1"));
    botPaint.setSelected(Boolean.parseBoolean(p.getProperty("bot-paint", "true")));
    disableGraphics.setSelected(Boolean.parseBoolean(p.getProperty("disable-gfx", "false")));
    interlace.setSelected(Boolean.parseBoolean(p.getProperty("interlace", "false")));
    themeChoice.setSelectedItem(p.getProperty("theme", Theme.RUNEDARK.getName()));
    newIcons.setSelected(Boolean.parseBoolean(p.getProperty("new-icons", "false")));
    newUi.setSelected(Boolean.parseBoolean(p.getProperty("new-ui", "false")));
    keepOpen.setSelected(Boolean.parseBoolean(p.getProperty("keep-open", "false")));
    screenRefresh.setSelected(Boolean.parseBoolean(p.getProperty("screen-refresh", "true")));
  }

  @Override
  public void setDefaultValues() {
    startPosX.setText("-1");
    startPosY.setText("-1");
    themeChoice.setSelectedIndex(0);
    botPaint.setSelected(true);
    disableGraphics.setSelected(false);
    screenRefresh.setSelected(true);
    interlace.setSelected(false);
    newIcons.setSelected(false);
    newUi.setSelected(false);
    keepOpen.setSelected(true);
    primaryBGPanel.setHexColor(ColorPickerPanel.colorToHex(Theme.RUNEDARK.getPrimaryBackground()));
    primaryFGPanel.setHexColor(ColorPickerPanel.colorToHex(Theme.RUNEDARK.getPrimaryForeground()));
    secondaryBGPanel.setHexColor(
        ColorPickerPanel.colorToHex(Theme.RUNEDARK.getSecondaryBackground()));
    secondaryFGPanel.setHexColor(
        ColorPickerPanel.colorToHex(Theme.RUNEDARK.getSecondaryForeground()));
  }

  @Override
  public void initializeComponents() {
    Component[] comps = {
      primaryBGPanel =
          new ColorPickerPanel(
              "Custom Primary BG:",
              "The main background color for the 'Custom' theme. Used for most component backgrounds",
              Theme.RUNEDARK.getPrimaryBackground()),
      primaryFGPanel =
          new ColorPickerPanel(
              "Custom Primary FG:",
              "The main foreground color for the 'Custom' theme. Used for most component foregrounds",
              Theme.RUNEDARK.getPrimaryForeground()),
      secondaryBGPanel =
          new ColorPickerPanel(
              "Custom Secondary BG:",
              "The secondary background color for the 'Custom' theme. Used as an accent background for some components",
              Theme.RUNEDARK.getSecondaryBackground()),
      secondaryFGPanel =
          new ColorPickerPanel(
              "Custom Secondary FG:",
              "The secondary foreground color for the 'Custom' theme. Used as an accent foreground for some components",
              Theme.RUNEDARK.getSecondaryForeground()),
      themeChoice = new ComboBoxPanel("Theme:", "Select a client-wide theme"),
      startPosX =
          new TextFieldPanel("Window X Position:", "The IdleRSC window's start up X position"),
      startPosY =
          new TextFieldPanel("Window Y Position:", "The IdleRSC window's start up Y position"),
      botPaint = new CustomCheckBox("Show Bot Paint", "Draw bot paints"),
      disableGraphics = new CustomCheckBox("Disable Graphics", "Disable all client graphics"),
      screenRefresh =
          new CustomCheckBox(
              "60s Screen Refresh",
              "Refresh the graphics every 60 seconds. Recommended when graphics are disabled"),
      interlace = new CustomCheckBox("Enable Interlace Mode", "Interlace the client graphics"),
      newUi = new CustomCheckBox("Custom Game UI", "Use the new UI instead of the default"),
      newIcons =
          new CustomCheckBox("New Menu Icons", "Switch the game menu icons to the new icons"),
      keepOpen =
          new CustomCheckBox(
              "Keep Inventory Open", "Keeps inventory open when using the custom UI"),
    };

    for (Theme t : Theme.values()) themeChoice.addItem(t.getName());
    for (Component comp : comps) add(comp);
  }
}
