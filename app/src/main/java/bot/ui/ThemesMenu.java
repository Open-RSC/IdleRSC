package bot.ui;

import bot.Main;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.plaf.basic.BasicMenuItemUI;

public class ThemesMenu extends JMenu {

  // Array of KeyEvents that is used for Theme accelerators.
  int[] keyEvents = {
    KeyEvent.VK_1,
    KeyEvent.VK_2,
    KeyEvent.VK_3,
    KeyEvent.VK_4,
    KeyEvent.VK_5,
    KeyEvent.VK_6,
    KeyEvent.VK_7,
    KeyEvent.VK_8,
    KeyEvent.VK_9,
    KeyEvent.VK_0,
    KeyEvent.VK_F1,
    KeyEvent.VK_F2,
    KeyEvent.VK_F3,
  };

  /**
   * Creates a JMenu specifically for Themes.
   *
   * @param s String -- The title of the ThemesMenu. This is what's displayed on the JMenuBar.
   */
  public ThemesMenu(String s) {
    super(s);
    populateThemes();
  }

  /** Populates the ThemeMenu with items from the Theme enum. */
  private void populateThemes() {

    // Should force the popup menu above the game window
    getPopupMenu().setLightWeightPopupEnabled(false);

    // This AtomicInteger is used to keep track of which KeyEvent we're on for setting theme
    // accelerators.
    AtomicInteger i = new AtomicInteger(0);

    // Loop through all themes and add them to the ThemesMenu
    for (Theme theme : Theme.values()) {
      JMenuItem menuItem = new JMenuItem(theme.getName());

      // Add an accelerator if possible
      if (i.get() < keyEvents.length)
        menuItem.setAccelerator(KeyStroke.getKeyStroke((char) keyEvents[i.getAndIncrement()]));

      // Colorize the menu item based on the theme to preview themes in the menu
      colorMenuItem(menuItem, theme);

      // Add an action listener to the menuItem that switches the theme when it's clicked.
      addMenuItemActionListener(menuItem, theme);

      // Add the theme to the ThemesMenu
      add(menuItem);
    }
  }

  /**
   * Adds an action listener to the menuItem which sets the new theme when clicked.
   *
   * @param menuItem JMenuItem -- Menu item to add the listener to.
   * @param theme Theme -- Theme to use for recoloring.
   */
  private void addMenuItemActionListener(JMenuItem menuItem, Theme theme) {
    menuItem.addActionListener(
        e -> {
          if (!Theme.colorsMatchTheme(
              Main.primaryBG, Main.primaryFG, Main.secondaryBG, Main.secondaryFG, theme))
            Main.setTheme(theme.getName());
        });
  }

  /**
   * Colors the menuItem's background and foreground based on the given theme, then draws a custom
   * color palette preview and accelerator.
   *
   * @param menuItem JMenuItem -- Menu item to recolor.
   * @param theme Theme -- Theme to use for recoloring.
   */
  private void colorMenuItem(JMenuItem menuItem, Theme theme) {
    menuItem.setBackground(
        theme.getName().equalsIgnoreCase("custom")
            ? Main.customColors[0]
            : theme.getPrimaryBackground());
    menuItem.setForeground(
        theme.getName().equalsIgnoreCase("custom")
            ? Main.customColors[1]
            : theme.getPrimaryForeground());
    drawCustomMenuItem(menuItem, theme);
  }

  /**
   * Draws a color palette preview and accelerator based on the theme color.
   *
   * @param menuItem JMenuItem -- Menu item to modify
   * @param theme Theme -- Theme to color the menuItem after.
   */
  private void drawCustomMenuItem(JMenuItem menuItem, Theme theme) {
    menuItem.setUI(
        new BasicMenuItemUI() {
          @Override
          public Dimension getPreferredSize(JComponent c) {
            Dimension d = super.getPreferredSize(c);
            d.width = Math.max(d.width, 220);
            return d;
          }

          @Override
          public void paint(Graphics g, JComponent c) {
            JMenuItem menuItem = (JMenuItem) c;
            super.paint(g, c);
            String acceleratorText =
                menuItem.getAccelerator() != null
                    ? String.valueOf(menuItem.getAccelerator().getKeyChar())
                    : "";

            g.setColor(theme.getPrimaryForeground());
            g.drawString(
                acceleratorText,
                menuItem.getWidth() - g.getFontMetrics().stringWidth(acceleratorText) - 8,
                menuItem.getHeight() / 2 + g.getFontMetrics().getAscent() / 2 - 1);

            // Set the colors
            Color[] col =
                theme.getName().equalsIgnoreCase("custom")
                    ? Main.customColors
                    : new Color[] {
                      theme.getPrimaryBackground(),
                      theme.getPrimaryForeground(),
                      theme.getSecondaryBackground(),
                      theme.getSecondaryForeground()
                    };
            // Variables for adjusting the color palette preview
            int size = 8;
            int colorGap = 2;
            int acceleratorGap = 22;
            int palletPadding = 2;
            int paletteWidth =
                (size * col.length) + (colorGap * (col.length - 1)) + (palletPadding * 2);

            int x = menuItem.getWidth() - paletteWidth - acceleratorGap;
            int y = (menuItem.getHeight() / 2) - (size / 2);

            // Draw the color palette area
            g.setColor(new Color(170, 170, 170, 170));
            g.fillRect(
                x - palletPadding, y - palletPadding, paletteWidth, size + (palletPadding * 2));
            g.setColor(Color.BLACK);
            g.drawRect(
                x - palletPadding, y - palletPadding, paletteWidth, size + (palletPadding * 2));

            // Draw all the colors as a palette
            for (Color color : col) {
              g.setColor(color);
              g.fillRect(x, y, size, size);
              g.setColor(Color.BLACK);
              g.drawRect(x, y, size, size);
              x += size + colorGap;
            }
          }

          @Override
          public void paintMenuItem(
              Graphics g,
              JComponent c,
              Icon checkIcon,
              Icon arrowIcon,
              Color background,
              Color foreground,
              int defaultTextIconGap) {
            acceleratorFont = new Font("Dialog", Font.PLAIN, 0);
            super.paintMenuItem(
                g, c, checkIcon, arrowIcon, background, foreground, defaultTextIconGap);
          }
        });
  }

  /** Draws the popup menu as a grid of themes. */
  @Override
  public JPopupMenu getPopupMenu() {
    JPopupMenu popupMenu = super.getPopupMenu();

    // Custom panel to apply grid layout for the menu items
    popupMenu.setLayout(new GridLayout(0, 3)); // Adjust rows and columns as needed

    // Ensure size constraint
    int rowCount = (int) Math.ceil(Theme.values().length / 3.0);
    int rowHeight = 21;
    popupMenu.setPreferredSize(new Dimension(550, rowCount * rowHeight));

    return popupMenu;
  }
}
