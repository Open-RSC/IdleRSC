package bot.ui.components;

import bot.Main;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicCheckBoxUI;

public class CustomCheckBox extends JCheckBox {

  public CustomCheckBox(String text, String tooltip, boolean defaultValue) {
    super(text);
    setUI(new CustomCheckBoxUI());
    setSelected(defaultValue);
    if (tooltip != null && !tooltip.isEmpty()) setToolTipText(tooltip);
  }

  public CustomCheckBox(String text, String tooltip) {
    this(text, tooltip, false);
  }

  static class CustomCheckBoxUI extends BasicCheckBoxUI {
    @Override
    protected void installDefaults(AbstractButton b) {
      super.installDefaults(b);
      b.setIcon(new CustomCheckIcon()); // Assign the custom icon here
    }
  }

  // Custom Icon Class
  static class CustomCheckIcon implements Icon {
    private static final int SIZE = 16;

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      // Draw checkbox background
      g2.setColor(Main.secondaryFG);
      g2.fillRoundRect(x, y, SIZE, SIZE, 4, 4);

      // Draw border
      g2.setColor(Main.primaryFG);
      g2.drawRoundRect(x, y, SIZE - 1, SIZE - 1, 4, 4);

      // If selected, draw checkmark
      AbstractButton button = (AbstractButton) c;
      if (button.isSelected()) {
        g2.setColor(Main.secondaryBG);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x + 3, y + 8, x + 7, y + 12);
        g2.drawLine(x + 7, y + 12, x + 13, y + 4);
      }

      g2.dispose();
    }

    @Override
    public int getIconWidth() {
      return SIZE;
    }

    @Override
    public int getIconHeight() {
      return SIZE;
    }
  }
}
