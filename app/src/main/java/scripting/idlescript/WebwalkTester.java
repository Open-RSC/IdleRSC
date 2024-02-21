package scripting.idlescript;

import bot.Main;
import java.util.Arrays;
import java.util.List;

/**
 * Tests walking around the map to random locations
 *
 * @author Red Bracket
 */
public class WebwalkTester extends IdleScript {

  private final List<List<Integer>> coords =
      Arrays.asList(
          Arrays.asList(111, 659), // North of Church
          Arrays.asList(116, 635), // By Goblin House
          Arrays.asList(115, 711), // Lummy mines
          Arrays.asList(118, 607), // Chicken pen
          Arrays.asList(162, 652)); // Southwest of HAM hideout
  private List<Integer> dest = coords.get(0);
  private int successCount = 0;

  public int start(String[] parameters) {
    if (Main.getController().currentX() == dest.get(0)
        && Main.getController().currentY() == dest.get(1)) {
      Main.getController().log("Reached dest! Picking new location..");
      dest = coords.get((int) (Math.random() * coords.size()));
      successCount += 1;
    }

    Main.getController().log("Webwalking to: " + dest.get(0) + ", " + dest.get(1));
    Main.getController().walkTowards(dest.get(0), dest.get(1));

    return 1000;
  }

  @Override
  public void paintInterrupt() {
    Main.getController().drawString("@red@Webwalk Tester", 6, 21, 0xFFFFFF, 1);
    Main.getController()
        .drawString(
            "@red@Current location: "
                + Main.getController().currentX()
                + ", "
                + Main.getController().currentY(),
            6,
            21 + 14 * 1,
            0xFFFFFF,
            1);
    Main.getController()
        .drawString(
            "@red@Current destination: " + dest.get(0) + ", " + dest.get(1),
            6,
            21 + 14 * 2,
            0xFFFFFF,
            1);
    Main.getController()
        .drawString("@red@Successful walks: " + successCount, 6, 21 + 14 * 3, 0xFFFFFF, 1);
  }
}
