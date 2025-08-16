package bot.logger;

class ConsoleColor {
  String style;
  String textColor;
  String backgroundColor;

  public ConsoleColor(TextColor textColor, BackgroundColor bgColor, Style style) {
    this.style = (style != null) ? style.getStyle() : "";
    this.textColor = (textColor != null) ? textColor.get() : "";
    this.backgroundColor = (bgColor != null) ? bgColor.get() : "";
  }

  public String getTextColor() {
    return textColor;
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public String getStyle() {
    return style;
  }

  public static String reset() {
    return "\u001B[0m";
  }

  public enum Style {
    BOLD("\u001B[1m"),
    UNDERLINE("\u001B[4m");

    private final String style;

    Style(String style) {
      this.style = style;
    }

    public String getStyle() {
      return style;
    }
  }

  public enum TextColor {
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    BRIGHT_BLACK("\u001B[90m"),
    BRIGHT_RED("\u001B[91m"),
    BRIGHT_GREEN("\u001B[92m"),
    BRIGHT_YELLOW("\u001B[93m"),
    BRIGHT_BLUE("\u001B[94m"),
    BRIGHT_PURPLE("\u001B[95m"),
    BRIGHT_CYAN("\u001B[96m"),
    BRIGHT_WHITE("\u001B[97m");

    private final String color;

    TextColor(String color) {
      this.color = color;
    }

    public String get() {
      return color;
    }
  }

  public enum BackgroundColor {
    BLACK("\u001B[40m"),
    RED("\u001B[101m"),
    GREEN("\u001B[42m"),
    YELLOW("\u001B[43m"),
    BLUE("\u001B[44m"),
    PURPLE("\u001B[45m"),
    CYAN("\u001B[46m"),
    WHITE("\u001B[47m"),
    BRIGHT_BLACK("\u001B[100m"),
    BRIGHT_RED("\u001B[41m"),
    BRIGHT_GREEN("\u001B[102m"),
    BRIGHT_YELLOW("\u001B[103m"),
    BRIGHT_BLUE("\u001B[104m"),
    BRIGHT_PURPLE("\u001B[105m"),
    BRIGHT_CYAN("\u001B[106m"),
    BRIGHT_WHITE("\u001B[107m");

    private final String color;

    BackgroundColor(String color) {
      this.color = color;
    }

    public String get() {
      return color;
    }
  }
}
