package controller.PaintBuilder;

import bot.Main;
import controller.Controller;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaintBuilder {
  private static final Controller c = Main.getController();

  private static final long startTime = System.currentTimeMillis();
  private int pWidth, pHeight, pX, pY;
  private int borderColor, bgColor, bgTransparency = 0;
  private int rowsY = 0;

  public String stringRunTime;
  public float runTimeSeconds, timeScale;
  private String title, titleSecondary;
  private int tColor, tColorSecondary, tSize, tXOffset, tXOffsetSecondary, tYOffset = 0;

  private ArrayList<RowBuilder> rowData = new ArrayList<>();

  /**
   * @param width int -- Height of the paint
   * @param height int -- Width of the paint
   * @param x int -- X coordinate for the top left corner of the paint
   * @param y int -- Y coordinate for the top left corner of the paint
   * @param rowsY int -- Y coordinate where rows are drawn (X coordinate is passed to the rows when
   *     making them)
   * @param rowSpacing int -- Padding between rows in pixels
   */
  public PaintBuilder(int width, int height, int x, int y, int rowsY, int rowSpacing) {
    this.pWidth = width;
    this.pHeight = height;
    this.pX = x;
    this.pY = y;
    this.rowsY = rowsY;
  }

  /**
   * Set the paint's top left X coordinate to a new value
   *
   * @param newX int -- New X value
   */
  public void setX(int newX) {
    this.pX = newX;
  }

  /**
   * Set the paint's top left Y coordinate to a new value
   *
   * @param newY int -- New Y value
   */
  public void setY(int newY) {
    this.pY = newY;
  }

  /**
   * Set the paint's width to a new value
   *
   * @param newWidth int -- New width value
   */
  public void setWidth(int newWidth) {
    this.pWidth = newWidth;
  }

  /**
   * Set the paint's height to a new value
   *
   * @param newWidth int -- New height value
   */
  public void setHeight(int newHeight) {
    this.pHeight = newHeight;
  }

  /**
   * Get the paint's top left X coordinate
   *
   * @return int
   */
  public int getX() {
    return this.pX;
  }

  /**
   * Get the paint's top left Y coordinate
   *
   * @return int
   */
  public int getY() {
    return this.pY;
  }

  /**
   * Get the paint's width
   *
   * @return int
   */
  public int getWidth() {
    return this.pWidth;
  }

  /**
   * Get the paint's height
   *
   * @return int
   */
  public int getHeight() {
    return this.pHeight;
  }

  /**
   * Adds a border box of a specified color
   *
   * @param color int -- Color of the border box. RGB "HTML" Color Example: 0x36E2D7
   */
  public void setBorderColor(int color) {
    this.borderColor = color;
  }

  /**
   * Adds a background of a specified color and transparency
   *
   * @param color int -- Color of the background. RGB "HTML" Color Example: 0x36E2D7
   * @param transparency int -- Transparency of the background. 0 to 255.
   */
  public void setBackgroundColor(int color, int transparency) {
    this.bgColor = color;
    this.bgTransparency = transparency;
  }

  /**
   * Adds a title header with a single color
   *
   * @param titleString String -- Title
   * @param titleColor int -- Title color RGB "HTML" Color Example: 0x36E2D7
   * @param titleSize int -- Size of the title. 1 to 6
   * @param titleXOffset int -- X offset for the title from the paint's X
   * @param titleYOffset int -- Y offset for the title from the paint's Y
   */
  public void setTitleSingleColor(
      String titleString, int titleColor, int titleSize, int titleXOffset, int titleYOffset) {
    this.title = titleString;
    this.tColor = titleColor;
    this.tSize = titleSize;
    this.tXOffset = titleXOffset;
    this.tYOffset = titleYOffset;
  }

  /**
   * Adds a title header with a two colored strings
   *
   * @param titleString1 String -- First title string
   * @param titleString2 String -- Second title string
   * @param titleColor1 int -- Color of the FIRST title string. RGB "HTML" Color Example: 0x36E2D7
   * @param titleColor2 int -- Color of the SECOND title string. RGB "HTML" Color Example: 0x36E2D7
   * @param titleSize int -- Size of the title. 1 to 6
   * @param titleXOffset int -- X offset for the title's FIRST string from the paint's X
   * @param titleXOffset2 int -- X offset for the title's SECOND string from the FIRST string's X
   * @param titleYOffset int -- Y offset for the title's string from the paint's Y
   */
  public void setTitleMultiColor(
      String titleString1,
      String titleString2,
      int titleColor1,
      int titleColor2,
      int titleSize,
      int titleXOffset,
      int titleXOffset2,
      int titleYOffset) {
    this.title = titleString1;
    this.titleSecondary = titleString2;
    this.tColor = titleColor1;
    this.tColorSecondary = titleColor2;
    this.tSize = titleSize;
    this.tXOffset = titleXOffset;
    this.tXOffsetSecondary = titleXOffset2;
    this.tYOffset = titleYOffset;
  }

  /**
   * Updates a row. Add this in paintInterrupt() after a controller null check to update every the
   * row frame.
   *
   * @param rowNumber int -- Number of the row to update, starts at 1.
   * @param newRowInfo RowBuilder -- New row information to update the row with.
   */
  public void updateRow(int rowNumber, RowBuilder newRowInfo) {
    if (rowData != null && rowData.size() >= rowNumber && c != null && newRowInfo != null)
      rowData.set(rowNumber - 1, newRowInfo);
  }
  /**
   * Adds a row.
   *
   * @param rowInfo RowBuilder -- Row information
   */
  public void addRow(RowBuilder rowInfo) {
    if (rowInfo != null) {
      rowData.add(rowInfo);
    } else {
      c.log("Failed to add row to PaintBuilder");
    }
  }

  /**
   * Adds an empty row. Useful for initializing a row that will be modified by updateRow in the
   * paintInterrupt.
   */
  public void addEmptyRow() {
    rowData.add(new RowBuilder());
  }
  /**
   * Adds X empty rows. Useful for initializing a row that will be modified by updateRow in the
   * paintInterrupt.
   */
  public void addEmptyRows(int amount) {
    for (int i = 0; i < amount; i++) rowData.add(new RowBuilder());
  }

  /**
   * Returns a string for amount of something per hour
   *
   * @param finished int -- Amount of actions/items gained in this session
   * @return String
   */
  public String stringAmountPerHour(int finished) {
    int perHour = (timeScale > 300 ? 0 : (int) (finished * timeScale));
    return "(" + stringFormatInt(perHour) + "/hr)";
  }

  /**
   * Takes an int, shortens it, and returns it as a string.
   *
   * <p>Example: 1230000 gets returned as 1.23M
   *
   * @param amount int -- Amount to shorten
   * @return String
   */
  public String stringFormatInt(int amount) {
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    decimalFormat.setRoundingMode(RoundingMode.DOWN);

    return amount >= 1000000000
        ? decimalFormat.format((double) amount / 1000000000.0) + "B"
        : amount >= 1000000
            ? decimalFormat.format((double) amount / 1000000.0) + "M"
            : amount > 1000
                ? decimalFormat.format((double) amount / 1000.0) + "K"
                : String.valueOf(amount);
  }

  private void doUpdates() {
    stringRunTime = c.msToString(System.currentTimeMillis() - startTime);
    runTimeSeconds = ((System.currentTimeMillis() - startTime) / 1000);
    timeScale = (60 * 60) / runTimeSeconds;
  }

  /** Draws the paint. */
  public void draw() {
    if (c != null) {
      doUpdates();
      int cumulativeRowHeight = 0;

      // Draws a background for the paint
      if (bgColor != 0) c.drawBoxAlpha(pX, pY, pWidth, pHeight, bgColor, bgTransparency);

      // Draws a border for the paint
      if (borderColor != 0) c.drawBoxBorder(pX, pY, pWidth, pHeight, borderColor);

      // Draws a title string
      if (title != null) {
        int x1 = pX + tXOffset;
        int y = pY + tYOffset;
        int color1 = tColor;
        if (titleSecondary != null) {
          int x2 = x1 + tXOffsetSecondary;
          int color2 = tColorSecondary;
          c.drawString(title, x1, y, tColor, tSize);
          c.drawString(titleSecondary, x2, y, color2, tSize);
        } else {
          c.drawString(title, x1, y, color1, tSize);
        }
      }

      // Draw rows
      if (rowData != null && rowData.size() > 0) {
        for (int rowNum = 0; rowNum < rowData.size(); rowNum++) {
          RowBuilder r = rowData.get(rowNum);
          cumulativeRowHeight += r.rowHeight;

          // Draws a row with three strings
          if (r.type.equals("MultipleStrings")) {
            int x = pX + r.rowXOffset;
            int y = pY + rowsY + cumulativeRowHeight;
            for (int i = 0; i < r.strings.length; i++) {
              x += r.stringXOffsets[i];
              String text = r.strings[i];
              int color = r.colors[i];
              if (text != null && color != 0) c.drawString(text, x, y, color, 1);
            }

            // Draws a row with a single string
          } else if (r.type.equals("SingleString")) {
            String text1 = r.text;
            int x = r.rowXOffset + pX;
            int y = pY + rowsY + cumulativeRowHeight;
            int color = r.color1;

            c.drawString(text1, x, y, color, 1);

            // Draws a row with multiple item sprites and strings for each
          } else if (r.type.equals("MultipleSprites")) {
            if (r.colors == null) r.colors = new int[] {0xffffff};
            int cumulativeSpacing = 0;
            for (int i = 0; i < r.ids.length; i++) {
              String str = r.strings[i];
              int id = r.ids[i];
              int scale = r.scales[i];
              int stringXOffset = r.stringXOffset;
              int color = r.colors.length == r.ids.length ? r.colors[i] : r.colors[0];

              cumulativeSpacing += c.getItemSpriteScaledWidth(id, scale) + r.spriteSpacing;
              int spriteX = r.rowXOffset + cumulativeSpacing;
              int spriteY =
                  rowsY + cumulativeRowHeight + (int) (c.getItemSpriteScaledWidth(id, scale) / 2);
              int stringX = pX + r.rowXOffset + stringXOffset + cumulativeSpacing;
              int stringY = spriteY + r.stringYOffset;

              c.drawItemSprite(id, spriteX, spriteY, scale, false);
              c.drawString(str, stringX, stringY, color, 1);
            }

            // Draws a row with an item sprite and multiple strings

          } else if (r.type.equals("SingleSpriteMultipleStrings")) {
            int id = r.itemId;
            int rowX = pX + r.rowXOffset;
            int spriteY = rowsY + cumulativeRowHeight;
            int stringY = rowsY + cumulativeRowHeight + r.stringYOffset;
            int scale = r.spriteScale;

            c.drawItemSprite(id, rowX, spriteY, scale, false);

            int stringX = rowX;
            for (int i = 0; i < r.strings.length; i++) {
              stringX += r.stringXOffsets[i];
              int color = r.colors[i];
              String text = r.strings[i];
              if (text != null && color != 0) c.drawString(text, stringX, stringY, color, 1);
            }

            // Draws a row with an item sprite and one string
          } else if (r.type.equals("SingleSpriteSingleString")) {
            int id = r.itemId;
            int scale = r.spriteScale;
            String str1 = r.text;
            int color1 = r.color1;
            int spriteY = rowsY + cumulativeRowHeight;
            int spriteX = pX + r.rowXOffset;
            int stringX1 = spriteX + r.stringXOffset;
            int stringY = spriteY + r.stringYOffset;

            c.drawItemSprite(id, spriteX, spriteY, scale, false);
            c.drawString(str1, stringX1, stringY, color1, 1);
          }
        }
      }
    }
    rowData.clear();
  }
}
