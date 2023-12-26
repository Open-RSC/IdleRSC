package controller.PaintBuilder;

// TODO: Use the largest number from (14 or spriteScaledHeight+2) to set row heights automatically

public class RowBuilder {
  public int[] ids, colors, scales, stringXOffsets;
  public String[] strings;
  public String type = "";
  public String text;
  public int itemId = -1;
  public int stringColor, fgColor, bgColor, borderColor;
  public int rowXOffset, rowHeight;
  public int spriteScale, spriteSpacing;
  public int stringXOffset, stringYOffset;
  public int progressBarWidth, progressBarHeight, currentProgress, maximumProgress;
  public boolean showGoal, showPercentage;
  /**
   * Builds a row with a single colored string
   *
   * @param text String -- String to paint
   * @param color color -- Color of the string. RGB "HTML" Color Example: 0x36E2D7
   * @param rowXOffset int -- Amount to offset the string's X from the paint's border
   * @return RowBuilder Row
   */
  public RowBuilder singleStringRow(String text, int color, int rowXOffset) {
    RowBuilder row = new RowBuilder();
    row.type = "SingleString";
    row.text = text;
    row.stringColor = color;
    row.rowXOffset = rowXOffset;
    row.rowHeight = 14;
    return row;
  }

  public RowBuilder centeredSingleStringRow(String text, int color) {
    RowBuilder row = new RowBuilder();
    row.type = "CenteredString";
    row.text = text;
    row.stringColor = color;
    row.rowHeight = 14;
    return row;
  }

  /**
   * Builds a row with multiple colored strings
   *
   * @param strings int[] -- Strings to draw
   * @param colors int[] -- Colors of the strings. RGB "HTML" Color Example: 0x36E2D7
   * @param stringXOffsets int[] -- Array of offsets for each string's X from the previous string's
   *     X. The first index is the amount offset from paint's border.
   * @return
   */
  public RowBuilder multipleStringRow(
      String[] strings, int[] colors, int rowXOffset, int[] stringXOffsets) {
    RowBuilder row = new RowBuilder();
    if (strings.length > 0
        && strings.length == colors.length
        && colors.length == stringXOffsets.length) {

      row.type = "MultipleStrings";
      row.strings = strings;
      row.colors = colors;
      row.stringXOffsets = stringXOffsets;
      row.rowHeight = 14;
      return row;
    }
    return errorArrayLengthMismatch();
  }

  /**
   * Builds a row of item sprites.
   *
   * @param ids int[] -- Array of item ids to paint
   * @param scales int[] -- Array of int to scale each sprite to. 100 is normal, lower is smaller,
   *     higher is bigger.
   * @param strings String[] -- Array of strings to paint for each item sprite
   * @param colors int[] -- Array of colors for each string. If given an empty array, it will
   *     default to white. RGB "HTML" Color Example: 0x36E2D7
   * @param rowXOffset int -- Amount to offset the row's X from the paint's border
   * @param rowHeight int -- Height of the row (to prevent clipping)
   * @param spriteSpacing int -- Spacing between each sprite
   * @param stringXOffset int -- X offset for the strings relative to the sprite's X. Negative is
   *     left while positive is right.
   * @param stringYOffset int -- Y offset for the strings relative to the sprite's Y. Negative is up
   *     while positive is down.
   * @return RowBuilder Row
   */
  public RowBuilder multiItemSpriteRow(
      int[] ids,
      int[] scales,
      String[] strings,
      int[] colors,
      int rowXOffset,
      int rowHeight,
      int spriteSpacing,
      int stringXOffset,
      int stringYOffset) {
    RowBuilder row = new RowBuilder();
    if (ids.length > 0
        && ids.length == scales.length
        && scales.length == strings.length
        && strings.length == colors.length) {
      row.type = "MultipleSprites";
      row.ids = ids;
      row.scales = scales;
      row.strings = strings;
      row.colors = colors;
      row.rowXOffset = rowXOffset;
      row.spriteSpacing = spriteSpacing;
      row.stringXOffset = stringXOffset;
      row.stringYOffset = stringYOffset;
      row.rowHeight = rowHeight;
      return row;
    }
    return errorArrayLengthMismatch();
  }

  /**
   * Builds a row with a single item sprite and string.
   *
   * @param itemId int -- Item id to draw the sprite for
   * @param itemString String -- String to draw
   * @param stringColor int -- Color of the string. RGB "HTML" Color Example: 0x36E2D7
   * @param rowXOffset int -- Amount to offset the row's X from the paint's border
   * @param rowHeight int -- Height of the row (to prevent clipping)
   * @param spriteScale int -- Scale of the items sprite. 100 is normal, lesser is smaller, greater
   *     is larger.
   * @param stringXOffset int -- X offset for the string relative to the sprite's X. Negative is
   *     left while positive is right.
   * @param stringYOffset int -- Y offset for the string relative to the sprite's Y. Negative is up
   *     while positive is down.
   * @return RowBuilder Row
   */
  public RowBuilder singleSpriteSingleStringRow(
      int itemId,
      String itemString,
      int stringColor,
      int rowXOffset,
      int rowHeight,
      int spriteScale,
      int stringXOffset,
      int stringYOffset) {
    RowBuilder row = new RowBuilder();
    row.type = "SingleSpriteSingleString";
    row.itemId = itemId;
    row.text = itemString;
    row.stringColor = stringColor;
    row.rowXOffset = rowXOffset;
    row.rowHeight = rowHeight;
    row.spriteScale = spriteScale;
    row.stringXOffset = stringXOffset;
    row.stringYOffset = stringYOffset;
    return row;
  }

  /**
   * Builds a row with a progress bar and a colored string.
   *
   * @param current int -- Current value to calculate the progress bar from
   * @param maximum int -- Maximum value to calculate the progress bar from
   * @param bgColor int -- Color of the progress bar's background. RGB "HTML" Color Example:
   *     0x36E2D7
   * @param fgColor int -- Color of the progress bar's foreground. RGB "HTML" Color Example:
   *     0x36E2D7
   * @param borderColor int -- Color of the progress bar's border. RGB "HTML" Color Example:
   *     0x36E2D7
   * @param barWidth int -- Width of the progress bar
   * @param barHeight int -- Height of the progress bar
   * @param showPercentage boolean -- Show the percentage on the bar
   * @param showGoal boolean -- Show the start an goal on the bar
   * @param string String -- Description of the progress bar
   * @param stringColor int-- Color of the text. RGB "HTML" Color Example: 0x36E2D7
   * @return
   */
  public RowBuilder progressBarRow(
      int current,
      int maximum,
      int bgColor,
      int fgColor,
      int borderColor,
      int rowXOffset,
      int barWidth,
      int barHeight,
      boolean showPercentage,
      boolean showGoal,
      String string,
      int stringColor,
      int stringXOffset,
      int stringYOffset) {
    RowBuilder row = new RowBuilder();
    row.type = "ProgressBar";
    row.currentProgress = current;
    row.maximumProgress = maximum;
    row.bgColor = bgColor;
    row.fgColor = fgColor;
    row.borderColor = borderColor;
    row.rowXOffset = rowXOffset;
    row.progressBarWidth = barWidth;
    row.progressBarHeight = barHeight;
    row.showPercentage = showPercentage;
    row.showGoal = showGoal;
    row.text = string;
    row.stringColor = stringColor;
    row.rowHeight = barHeight + 18;
    return row;
  }

  /**
   * Builds a row with a single item sprite and multiple strings.
   *
   * @param itemId int -- Id of item sprite to draw
   * @param scale int -- Scale of the items sprite. 100 is normal, lesser is smaller, greater is
   *     larger.
   * @param rowXOffset int -- Amount the row is offset from the paint's border
   * @param strings String[] -- Array of strings
   * @param colors int[] -- Array of colors for the strings
   * @param stringXOffsets int[] -- Array of offsets for each string's X from the previous string's
   *     X. The first index is the amount offset from the sprite's X.
   * @param stringYOffset int -- Amount up or down the strings are in the row. Negative is up while
   *     positive is down.
   * @param rowHeight int -- Height of the row. This is to prevent rows clipping with higher item
   *     sprite scaling.
   * @return
   */
  public RowBuilder singleSpriteMultipleStringRow(
      int itemId,
      int scale,
      int rowXOffset,
      String[] strings,
      int[] colors,
      int[] stringXOffsets,
      int stringYOffset,
      int rowHeight) {
    RowBuilder row = new RowBuilder();
    row.type = "SingleSpriteMultipleStrings";
    if (strings.length > 0
        && strings.length == colors.length
        && colors.length == stringXOffsets.length) {
      row.itemId = itemId;
      row.spriteScale = scale;
      row.strings = strings;
      row.colors = colors;
      row.stringXOffsets = stringXOffsets;
      row.stringYOffset = stringYOffset;
      row.rowXOffset = rowXOffset;
      row.rowHeight = rowHeight;
      return row;
    }
    return errorArrayLengthMismatch();
  }

  private RowBuilder errorArrayLengthMismatch() {
    RowBuilder row = new RowBuilder();
    row.type = "SingleString";
    row.text = "Row arrays have non-matching lengths";
    row.stringColor = 0xff0000;
    row.rowXOffset = 4;
    row.rowHeight = 14;
    return row;
  }
}
