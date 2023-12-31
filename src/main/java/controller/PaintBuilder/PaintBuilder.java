package controller.PaintBuilder;

import bot.Main;
import controller.Controller;
import java.awt.Color;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaintBuilder {
  private static final Controller c = Main.getController();
  RowBuilder rowBuilder = new RowBuilder();

  public String stringRunTime;
  public float runTimeSeconds, timeScale;
  public int colorRainbow;

  private static long startTime = System.currentTimeMillis();
  private int pWidth, pHeight, pX, pY;
  private int borderColor, bgColor, bgTransparency = 0;
  private int rowsY = 2;
  private String[] title;
  private int[] tColors, tXOffsets;
  private int tYOffset = 0;
  private float hue = 0f;
  private int rainbowSpeed = 4;
  private boolean isScriptPaint, isPlaceholderPaint, tCentered = false;

  private ArrayList<RowBuilder> rowData = new ArrayList<>();

  /**
   * @param width int -- Width of the paint
   * @param x int -- X coordinate for the top left corner of the paint
   * @param y int -- Y coordinate for the top left corner of the paint
   */
  public PaintBuilder(int x, int y, int width) {
    this.pWidth = width;
    this.pX = x;
    this.pY = y;
  }

  /**
   * Starts the PaintBuilder. PaintBuilder will not draw a custom paint if this is not called. Place
   * this at the beginning of start() in scripts.
   *
   * @param width int -- Width of the paint
   * @param x int -- X coordinate for the top left corner of the paint
   * @param y int -- Y coordinate for the top left corner of the paint
   */
  public void start(int x, int y, int width) {
    this.pWidth = width;
    this.pX = x;
    this.pY = y;
    startTime = System.currentTimeMillis();
    rowData.clear();
    isScriptPaint = true;
  }
  /**
   * Set the paint's top left X coordinate to a new value
   *
   * @param x int -- New X value
   */
  public void setX(int x) {
    this.pX = x;
  }

  /**
   * Set the paint's top left Y coordinate to a new value
   *
   * @param y int -- New Y value
   */
  public void setY(int y) {
    this.pY = y;
  }

  /**
   * Set the speed of colorRainbow cycling.
   *
   * @param speed int -- The speed at which colorRainbow updates. The default is speed 4.
   */
  public void setRainbowSpeed(int speed) {
    this.rainbowSpeed = speed;
  }
  /**
   * Y offset of where to start drawing rows relative to the paint's top border. Negative is up
   * while positive is down.
   *
   * @param rowsY int -- New Y value
   */
  public void setRowsY(int rowsY) {
    this.rowsY = rowsY;
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
   * @param newHeight int -- New height value
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
   * Get the paint's rowsY offset
   *
   * @return int
   */
  public int getRowsY() {
    return this.rowsY;
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
   * @param string String -- Title
   * @param color int -- Title color RGB "HTML" Color Example: 0x36E2D7
   * @param xOffset int -- X offset for the title from the paint's X
   * @param yOffset int -- Y offset for the title from the paint's Y
   */
  public void setTitleSingleColor(String string, int color, int xOffset, int yOffset) {
    this.title = new String[] {string};
    this.tColors = new int[] {color};
    this.tXOffsets = new int[] {xOffset};
    this.tYOffset = yOffset;
    this.tCentered = false;
  }

  /**
   * Adds a title header with a multiple colored strings
   *
   * @param strings String[] -- Array of strings
   * @param colors int[] -- Array of colors for the title strings. RGB "HTML" Color Example:
   *     0x36E2D7
   * @param xOffsets int[] -- Array of offsets for each string's X from the previous string's X. The
   *     first index is the amount offset from the paint's border.
   * @param yOffset int -- Y offset for the title's string from the paint's Y
   */
  public void setTitleMultipleColor(String[] strings, int[] colors, int[] xOffsets, int yOffset) {

    this.tYOffset = yOffset;
    if (strings.length > 0 && strings.length == colors.length && colors.length == xOffsets.length) {
      this.title = strings;
      this.tColors = colors;
      this.tXOffsets = xOffsets;
      this.tCentered = false;
    } else {
      this.title = new String[] {"Title arrays length mismatch"};
      this.tColors = new int[] {0xff4545};
      this.tXOffsets = new int[] {4};
    }
  }

  /**
   * Adds a centered title header with a single color
   *
   * @param string String -- Title
   * @param color int -- Title color RGB "HTML" Color Example: 0x36E2D7
   * @param yOffset int -- Y offset for the title from the paint's Y
   */
  public void setTitleCenteredSingleColor(String string, int color, int yOffset) {
    this.title = new String[] {string};
    this.tColors = new int[] {color};
    this.tYOffset = yOffset;
    this.tCentered = true;
  }

  /**
   * Adds a row.
   *
   * @param rowInfo RowBuilder -- Row information from controller.PaintBuilder.RowBuilder
   */
  public void addRow(RowBuilder rowInfo) {
    if (rowInfo != null) {
      rowData.add(rowInfo);
    } else {
      c.log("Failed to add row to PaintBuilder");
    }
  }

  /** Adds an empty row used to add a space between two rows. */
  public void addSpacerRow(int rowHeight) {
    rowHeight = rowHeight < 0 ? 0 : rowHeight;
    RowBuilder row = new RowBuilder();
    row.rowHeight = rowHeight;
    rowData.add(row);
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

  public void drawPlaceholderPaint() {
    isPlaceholderPaint = true;
    setBackgroundColor(0x282A36, 255);
    setBorderColor(0xff4545);
    addRow(rowBuilder.singleStringRow("This is a placeholder paint", colorRainbow, 32, 1));
    addRow(rowBuilder.singleStringRow("Override paintInterrupt to create one", 0xff4545, 4, 1));
    addRow(rowBuilder.singleStringRow("Run Time: " + stringRunTime, 0xff4545, 54, 1));
    draw();
    isPlaceholderPaint = false;
  }

  /**
   * Returns the amount the font needs to be offset to have correct placement in a row.
   *
   * @param fontSize 1-7
   * @return int
   */
  private int fixFontAnchoring(int fontSize) {
    int[] fontYOffsets = {11, 11, 12, 12, 14, 17, 19};
    return fontSize > 0 && fontSize < 8 ? fontYOffsets[fontSize - 1] : 0;
  }

  /** This is ran once per draw cycle. Put variables that need updating here */
  private void doUpdates() {
    hue = hue <= 0 ? 1f : hue - (float) (rainbowSpeed * 0.0005);

    colorRainbow = Color.HSBtoRGB(hue, 1f, 1f);
    stringRunTime = c.msToString(System.currentTimeMillis() - startTime);
    runTimeSeconds = ((System.currentTimeMillis() - startTime) / 1000);
    timeScale = (60 * 60) / runTimeSeconds;
  }

  /** Draws the paint. */
  public void draw() {
    if (c != null) {
      if (isScriptPaint || isPlaceholderPaint) {
        doUpdates();
        int cumulativeRowHeight = 0;

        // Draws a background for the paint
        if (bgColor != 0) c.drawBoxAlpha(pX, pY, pWidth, pHeight, bgColor, bgTransparency);

        // Draws a border for the paint
        if (borderColor != 0) c.drawBoxBorder(pX, pY, pWidth, pHeight, borderColor);

        // Draws a title at the top of the paint
        if (title != null) {
          int x = pX;
          int y = pY + tYOffset + fixFontAnchoring(6);
          for (int i = 0; i < title.length; i++) {
            if (tXOffsets != null) x += tXOffsets[i];
            String text = title[i];
            int color = tColors[i];
            if (tCentered) {
              y = pY + tYOffset + 4;
              c.drawCenteredString(text, pX + (pWidth / 2), y, color, 6);
            } else {
              c.drawString(text, x, y, color, 6);
            }
          }
          setRowsY((c.getStringHeight(6) + tYOffset + 2));
        }

        // Draw rows
        if (rowData != null && rowData.size() > 0) {
          for (int rowNum = 0; rowNum < rowData.size(); rowNum++) {
            RowBuilder r = rowData.get(rowNum);

            // Highlight row backgrounds for testing r.rowHeight
            /* if (rowNum % 2 == 1) {
              c.drawBoxAlpha(
                  pX, pY + cumulativeRowHeight + rowsY, pWidth, r.rowHeight, 0xffffff, 155);
            } else {
              c.drawBoxAlpha(
                  pX, pY + cumulativeRowHeight + rowsY, pWidth, r.rowHeight, 0x454545, 155);
            } */

            // Draws a row with three strings
            if (r.type.equals("MultipleStrings")) {
              int x = pX;
              int y = pY + rowsY + cumulativeRowHeight + fixFontAnchoring(r.fontSize);
              for (int i = 0; i < r.strings.length; i++) {
                x += r.stringXOffsets[i];
                String text = r.strings[i];
                int color = r.colors[i];
                if (text != null && color != 0) c.drawString(text, x, y, color, r.fontSize);
              }

              // Draws a row with a single string
            } else if (r.type.equals("SingleString")) {
              String text = r.text;
              int x = r.borderXOffset + pX;
              int y = pY + rowsY + cumulativeRowHeight + fixFontAnchoring(r.fontSize);
              int color = r.stringColor;

              if (text != null && color != 0) c.drawString(text, x, y, color, r.fontSize);

            } else if (r.type.equals("CenteredString")) {
              String text = r.text;
              int x = (pWidth / 2) + pX;
              int y = pY + rowsY + cumulativeRowHeight + (r.fontSize < 3 ? 4 : 5);
              int color = r.stringColor;

              if (text != null && color != 0) c.drawCenteredString(text, x, y, color, r.fontSize);

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
                int spriteX = r.borderXOffset + cumulativeSpacing;
                int spriteY =
                    pY
                        + rowsY
                        + cumulativeRowHeight
                        + (int) (c.getItemSpriteScaledWidth(id, scale) / 2);
                int stringX = pX + r.borderXOffset + stringXOffset + cumulativeSpacing;
                int stringY = pY + spriteY + r.stringYOffset + 11;

                c.drawItemSprite(id, spriteX, spriteY, scale, false);
                c.drawString(str, stringX, stringY, color, 1);
              }

              // Draws a row with an item sprite and multiple strings

            } else if (r.type.equals("SingleSpriteMultipleStrings")) {
              int id = r.itemId;
              int rowX = pX + r.borderXOffset;
              int spriteY = pY + rowsY + cumulativeRowHeight + 2;
              int stringY = spriteY + r.stringYOffset + fixFontAnchoring(r.fontSize);
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
              String str = r.text;
              int color = r.stringColor;
              int spriteY = pY + rowsY + cumulativeRowHeight + 2;
              int spriteX = pX + r.borderXOffset;
              int stringX = spriteX + r.stringXOffset;
              int stringY = spriteY + r.stringYOffset + (c.getStringHeight(1) / 2);
              c.drawItemSprite(id, spriteX, spriteY, scale, false);
              c.drawString(str, stringX, stringY, color, 1);

              // Draws a row with a progress bar and a description string
            } else if (r.type.equals("ProgressBar")) {
              int barX = pX + r.borderXOffset;
              int barY = pY + rowsY + cumulativeRowHeight + 14;
              c.drawCenteredString(
                  r.text, barX + (r.progressBarWidth / 2), barY - 10, r.stringColor, 1);
              c.drawProgressBar(
                  r.currentProgress,
                  r.maximumProgress,
                  r.bgColor,
                  r.fgColor,
                  r.borderColor,
                  barX,
                  barY,
                  r.progressBarWidth,
                  r.progressBarHeight,
                  r.showPercentage,
                  r.showGoal);
            }
            cumulativeRowHeight += r.rowHeight;
          }
        }
        this.setHeight(cumulativeRowHeight + rowsY + 2);
        if (rowData.size() > 0) rowData.clear();
      }

      if (!c.isRunning()) {
        if (isScriptPaint) isScriptPaint = false;
        if (rowData.size() > 0) rowData.clear();
        startTime = System.currentTimeMillis();
      }
    }
  }
}
