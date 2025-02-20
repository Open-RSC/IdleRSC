package bot.ui;

import java.awt.*;
import java.util.Arrays;

public enum Theme {
  RUNEDARK(
      new UITheme(
          "RuneDark",
          new Color(40, 40, 40, 255),
          new Color(219, 219, 219, 255),
          new Color(40, 40, 40, 255).brighter(),
          new Color(219, 219, 219, 255).brighter())),

  OLDSCAPE(
      new UITheme(
          "2007Scape",
          new Color(194, 177, 144, 255),
          new Color(10, 10, 8, 255),
          new Color(194, 177, 144, 255).brighter(),
          new Color(10, 10, 8, 255).brighter())),
  CLASSIC(
      new UITheme(
          "Classic",
          new Color(91, 100, 128, 255),
          new Color(0, 0, 0, 255),
          new Color(91, 100, 128, 255).brighter(),
          new Color(0, 0, 0, 255).brighter())),
  PURPLE(
      new UITheme(
          "Purple",
          new Color(41, 21, 72, 255),
          new Color(209, 186, 255, 255),
          new Color(41, 21, 72, 255).brighter(),
          new Color(209, 186, 255, 255).brighter())),
  MAGENTA(
      new UITheme(
          "Magenta",
          new Color(141, 22, 129, 255),
          new Color(255, 217, 255, 255),
          new Color(141, 22, 129, 255).brighter(),
          new Color(255, 217, 255, 255).brighter())),
  RED(
      new UITheme(
          "Red",
          new Color(110, 0, 16, 255),
          new Color(255, 183, 195, 255),
          new Color(110, 0, 16, 255).brighter(),
          new Color(255, 183, 195, 255).brighter())),
  AQUAMARINE(
      new UITheme(
          "Aquamarine",
          new Color(11, 143, 137, 255),
          new Color(210, 255, 255, 255),
          new Color(11, 143, 137, 255).brighter(),
          new Color(210, 255, 255, 255).brighter())),
  BLUE(
      new UITheme(
          "Blue",
          new Color(22, 65, 182, 255),
          new Color(191, 208, 255, 255),
          new Color(22, 65, 182, 255).brighter(),
          new Color(191, 208, 255, 255).brighter())),
  GREEN(
      new UITheme(
          "Green",
          new Color(9, 94, 0, 255),
          new Color(195, 255, 187, 255),
          new Color(9, 94, 0, 255).brighter(),
          new Color(195, 255, 187, 255).brighter())),
  BROWN(
      new UITheme(
          "Brown",
          new Color(73, 48, 48, 255),
          new Color(234, 202, 202, 255),
          new Color(73, 48, 48, 255).brighter(),
          new Color(234, 202, 202, 255).brighter())),
  ORANGE(
      new UITheme(
          "Orange",
          new Color(200, 112, 58, 255),
          new Color(61, 61, 61),
          new Color(200, 120, 58, 255).brighter(),
          new Color(61, 61, 61).brighter())),
  GOLD(
      new UITheme(
          "Gold",
          new Color(141, 113, 22, 255),
          new Color(255, 254, 200, 255),
          new Color(141, 113, 22, 255).brighter(),
          new Color(255, 254, 200, 255).brighter())),
  DRACULA(
      new UITheme(
          "Dracula",
          new Color(40, 42, 54),
          new Color(248, 248, 242),
          new Color(68, 71, 90),
          new Color(189, 147, 249))),

  CATPPUCCIN_DARK(
      new UITheme(
          "Catppuccin - Dark",
          new Color(36, 39, 58),
          new Color(198, 208, 245),
          new Color(140, 170, 238),
          new Color(41, 44, 60))),
  CATPPUCCIN_LIGHT(
      new UITheme(
          "Catppuccin - Light",
          new Color(239, 241, 245),
          new Color(76, 79, 105),
          new Color(114, 135, 253),
          new Color(204, 208, 218))),

// Do not modify the custom theme here as this is a placeholder so that it appears in the Themes
// menu.
// ! UNCOMMENT THIS WHEN PARSER/AUTHFRAME HAS SUPPORT FOR CUSTOM THEME
// CUSTOM(new UITheme("Custom", null, null, null, null))
;

  final UITheme theme;

  Theme(UITheme theme) {
    this.theme = theme;
  }

  public static boolean colorsMatchTheme(Color PB, Color PF, Color SB, Color SF, Theme v) {
    return v.getPrimaryBackground() == PB
        && v.getPrimaryForeground() == PF
        && v.getSecondaryBackground() == SB
        && v.getSecondaryForeground() == SF;
  }

  public String getName() {
    return get().getName();
  }

  public Color getPrimaryBackground() {
    return get().getPrimaryBackground();
  }

  public Color getPrimaryForeground() {
    return get().getPrimaryForeground();
  }

  public Color getSecondaryBackground() {
    return get().getSecondaryBackground();
  }

  public Color getSecondaryForeground() {
    return get().getSecondaryForeground();
  }

  private UITheme get() {
    return theme;
  }

  public static Theme getFromName(String name) {
    return Arrays.stream(Theme.values())
        .filter(t -> t.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElse(RUNEDARK);
  }

  private static class UITheme {
    private final String name;
    private final Color mainBG;
    private final Color mainFG;
    private final Color buttonBG;
    private final Color buttonFG;

    public UITheme(
        String name,
        Color primaryBackground,
        Color primaryForeground,
        Color secondaryBackground,
        Color secondaryForeground) {
      this.name = name;
      this.mainBG = primaryBackground;
      this.mainFG = primaryForeground;
      this.buttonBG = secondaryBackground;
      this.buttonFG = secondaryForeground;
    }

    public String getName() {
      return name;
    }

    public Color getPrimaryBackground() {
      return mainBG;
    }

    public Color getPrimaryForeground() {
      return mainFG;
    }

    public Color getSecondaryBackground() {
      return buttonBG;
    }

    public Color getSecondaryForeground() {
      return buttonFG;
    }
  }
}
