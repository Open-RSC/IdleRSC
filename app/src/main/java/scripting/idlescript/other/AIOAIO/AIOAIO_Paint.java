package scripting.idlescript.other.AIOAIO;

import bot.Main;
import controller.PaintBuilder.PaintBuilder;
import controller.PaintBuilder.RowBuilder;
import models.entities.ItemId;

public class AIOAIO_Paint {
  public static void paint(PaintBuilder paintBuilder, RowBuilder rowBuilder) {
    paintBuilder.setBorderColor(0xBD93F9);
    paintBuilder.setBackgroundColor(0x282A36, 125);
    paintBuilder.setTitleCenteredSingleColor(
        AIOAIO.state.currentSkill != null ? AIOAIO.state.currentSkill.getName() : "Initializing",
        0xcfdf1f,
        6);
    paintBuilder.addRow(
        rowBuilder.centeredSingleStringRow(
            AIOAIO.state.currentTask != null
                ? AIOAIO.state.currentTask.getName() + taskTimeRemaining()
                : "",
            0xffffff,
            1));
    paintBuilder.addRow(rowBuilder.centeredSingleStringRow(AIOAIO.state.status, 0xffffff, 1));
    paintBuilder.addRow(
        rowBuilder.multiItemSpriteRow(
            new int[] {
              ItemId.CASKET.getId(),
              ItemId.EYE_PATCH.getId(),
              ItemId.COINS.getId(),
              ItemId.EYE_PATCH.getId()
            },
            new int[] {100, 25, 140, 25},
            new String[] {
              paintBuilder.stringFormatInt(Main.getController().getTotalLevel()),
              "(+ " + (Main.getController().getTotalLevel() - AIOAIO.state.initLevel) + ")",
              formatValue(AIOAIO.state.lastCheckedBankValue),
              "(+ "
                  + formatValue(AIOAIO.state.lastCheckedBankValue - AIOAIO.state.initBankValue)
                  + ")"
            },
            new int[] {
              0xcfdf1f, 0x00ff00,
              0xcfdf1f, 0x00ff00,
            },
            0,
            22,
            0,
            0));
    paintBuilder.addRow(
        rowBuilder.centeredSingleStringRow("AIO AIO v" + AIOAIO.VERSION, 0xBD93F9, 1));
    paintBuilder.draw();
  }

  private static String taskTimeRemaining() {
    long timeRemaining = AIOAIO.state.endTime - System.currentTimeMillis();

    if (timeRemaining <= 0) {
      return " 00:00";
    }

    long totalSeconds = timeRemaining / 1000;
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;

    if (hours > 0) {
      return String.format(" %d:%02d:%02d", hours, minutes, seconds);
    } else {
      return String.format(" %02d:%02d", minutes, seconds);
    }
  }

  /**
   * Convert a number to a string that looks nice for cash -1 is returned as ? Everything else is
   * returned with 3 digits and an accompanying K/M/B I.e 126 is returned as 126 5896 is 5.9K 123456
   * is 123K 18203515 is 18.2M
   *
   * @param value long
   * @return String Nicely string represented value of the number
   */
  private static String formatValue(long value) {
    if (value == -1) return "?";

    if (value < 1000) {
      return String.valueOf(value);
    } else if (value < 1000000) {
      // Values in thousands with a single decimal place
      double thousands = value / 1000.0;
      if (thousands >= 100) {
        return String.format("%.0fK", thousands);
      } else {
        return String.format("%.1fK", thousands);
      }
    } else if (value < 1000000000) {
      // Values in millions with a single decimal place
      double millions = value / 1000000.0;
      if (millions >= 100) {
        return String.format("%.0fM", millions);
      } else {
        return String.format("%.1fM", millions);
      }
    } else {
      // Values in billions with a single decimal place
      double billions = value / 1000000000.0;
      return String.format("%.1fB", billions);
    }
  }
}
