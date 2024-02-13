package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.*;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import models.entities.ItemId;
import models.entities.MapPoint;
import orsc.ORSCharacter;

public class FoulRunecraft extends IdleScript {
  public static final Controller c = Main.getController();
  private JFrame frame;
  private String runtime, action;
  private long start;
  private int units, hourly, experience, choice = -1;
  private final int[] bankers = new int[] {95, 268, 224, 540, 617};
  private final int[] portals = new int[] {1228, 1229, 1230, 1231, 1232};
  private final int[] exits = new int[] {1214, 1215, 1216, 1217, 1218, 1219, 1220, 1221, 1222};
  private final int[] altars = new int[] {1191, 1193, 1195, 1197, 1199, 1201, 1203, 1205, 1207};
  private final int[] ruins = new int[] {1190, 1192, 1194, 1196, 1198, 1200, 1202, 1204, 1206};
  private final int[] talismans = new int[] {1300, 1301, 1302, 1303, 1304, 1305, 1306, 1307, 1308};
  private final int[] doubles = new int[] {11, 14, 19, 26, 35, 46, 59, 74, 91};
  private final int[] levels = new int[] {1, 1, 5, 9, 14, 20, 27, 35, 44};
  private final int[] runes = new int[] {33, 35, 32, 34, 31, 36, 46, 41, 40};
  private final String[] types = {
    "Air", "Mind", "Water", "Earth", "Fire", "Body", "Cosmic", "Chaos", "Nature", "Mining"
  };
  private MapPoint[] path, reversed;
  private final MapPoint[][] paths =
      new MapPoint[][] {
        new MapPoint[] {
          new MapPoint(283, 570),
          new MapPoint(289, 579),
          new MapPoint(298, 584),
          new MapPoint(306, 592)
        },
        new MapPoint[] {
          new MapPoint(331, 553),
          new MapPoint(326, 544),
          new MapPoint(324, 534),
          new MapPoint(319, 524),
          new MapPoint(311, 516),
          new MapPoint(303, 508),
          new MapPoint(303, 498),
          new MapPoint(309, 490),
          new MapPoint(311, 480),
          new MapPoint(310, 470),
          new MapPoint(309, 460),
          new MapPoint(306, 450),
          new MapPoint(299, 442)
        },
        new MapPoint[] {
          new MapPoint(220, 636),
          new MapPoint(210, 637),
          new MapPoint(202, 643),
          new MapPoint(192, 646),
          new MapPoint(185, 654),
          new MapPoint(176, 660),
          new MapPoint(170, 668),
          new MapPoint(161, 673),
          new MapPoint(153, 681),
          new MapPoint(150, 684),
        },
        new MapPoint[] {
          new MapPoint(103, 512),
          new MapPoint(94, 507),
          new MapPoint(84, 507),
          new MapPoint(76, 501),
          new MapPoint(69, 493),
          new MapPoint(65, 483),
          new MapPoint(65, 473),
          new MapPoint(63, 467),
        },
        new MapPoint[] {
          new MapPoint(90, 695),
          new MapPoint(84, 686),
          new MapPoint(76, 679),
          new MapPoint(72, 669),
          new MapPoint(66, 660),
          new MapPoint(62, 650),
          new MapPoint(57, 641),
          new MapPoint(52, 636)
        },
        new MapPoint[] {
          new MapPoint(216, 450),
          new MapPoint(223, 458),
          new MapPoint(224, 468),
          new MapPoint(224, 478),
          new MapPoint(229, 487),
          new MapPoint(236, 495),
          new MapPoint(236, 505),
          new MapPoint(243, 513),
          new MapPoint(253, 509),
          new MapPoint(260, 506)
        },
        new MapPoint[] {
          new MapPoint(174, 3526),
          new MapPoint(164, 3527),
          new MapPoint(156, 3535),
          new MapPoint(151, 3544),
          new MapPoint(149, 3554),
          new MapPoint(139, 3556),
          new MapPoint(129, 3556),
          new MapPoint(119, 3556),
          new MapPoint(109, 3556),
          new MapPoint(101, 3562),
          new MapPoint(105, 3565)
        },
        new MapPoint[] {
          new MapPoint(216, 450),
          new MapPoint(220, 440),
          new MapPoint(228, 434),
          new MapPoint(236, 428),
          new MapPoint(236, 418),
          new MapPoint(235, 408),
          new MapPoint(235, 398),
          new MapPoint(235, 388),
          new MapPoint(233, 378),
          new MapPoint(233, 378)
        },
        new MapPoint[] {
          new MapPoint(457, 756),
          new MapPoint(456, 759),
          new MapPoint(457, 769),
          new MapPoint(448, 774),
          new MapPoint(438, 775),
          new MapPoint(430, 782),
          new MapPoint(420, 787),
          new MapPoint(411, 792),
          new MapPoint(403, 800),
          new MapPoint(393, 803)
        }
      };
  private final Timer timer = new Timer();
  private final TimerTask task =
      new TimerTask() {
        @Override
        public void run() {
          if (!c.isRunning()) timer.cancel();
          if (choice == 9) units = (c.getStatXp(c.getStatId("Mining")) - experience) / 10;
          hourly = perHour(start, units);
          runtime = calculateTime(System.currentTimeMillis() - start);
        }
      };

  public int start(String[] parameters) {
    setupGUI();
    while (frame.isVisible()) {
      if (!c.isRunning()) {
        frame.dispose();
        return 1000;
      }
      c.sleep(500);
    }
    if (choice == -1) {
      c.stop();
      return 1000;
    }
    if (choice == 9) {
      action = "Mined";
      experience = c.getStatXp(c.getStatId("Mining"));
      while (c.isRunning()) c.sleep(miningLoop());
      return 1000;
    }
    if (choice == 8) {
      if (c.getPlayer().level < 79) {
        c.log("You must be 79+ combat to use this script!");
        c.stop();
      }
    }
    if (c.getCurrentStat(c.getStatId("Runecraft")) < levels[choice]) {
      c.log("You do not have high enough Runecraft!");
      c.stop();
    }
    if (!c.isItemInInventory(talismans[choice])) {
      c.log("You don't have a " + c.getItemName(talismans[choice]) + " in your inventory!");
      c.stop();
    }
    action = "Crafted";
    path = paths[choice];
    List<MapPoint> pathList = Arrays.asList(path.clone());
    Collections.reverse(pathList);
    reversed = pathList.toArray(new MapPoint[0]);
    experience = c.getStatXp(c.getStatId("Runecraft"));
    while (c.isRunning()) c.sleep(craftingLoop());
    return 1000;
  }

  private int craftingLoop() {
    if (c.getInventoryItemCount() >= 30 && c.isItemInInventory(talismans[choice])) {
      if (c.currentY() < 100) {
        if (choice == 7) {
          if (c.isCurrentlyWalking()) return ticks(1);
          for (int id : portals) {
            int[] portal = c.getNearestObjectById(id);
            if (portal != null && c.isReachable(portal[0], portal[1], true)) {
              c.atObject(portal[0], portal[1]);
              waitForMovement();
              while (c.isCurrentlyWalking()) c.sleep(ticks(1));
              return ticks(2);
            }
          }
        }
        int[] altar = c.getNearestObjectById(altars[choice]);
        if (altar != null) {
          if (choice == 7 && !c.isReachable(altar[0], altar[1], true)) return ticks(1);
          if (c.atObject(altar[0], altar[1])) {
            int count = c.getInventoryItemCount(1299);
            waitForMovement();
            while (c.isCurrentlyWalking()) c.sleep(ticks(1));
            if (count > c.getInventoryItemCount(1299)) {
              units += count * ((c.getBaseStat(c.getStatId("Runecraft")) / doubles[choice]) + 1);
            }
            return ticks(2);
          }
        }
      }
      int[] ruins = c.getNearestObjectById(this.ruins[choice]);
      if (ruins != null) {
        if (c.atObject(ruins[0], ruins[1])) {
          waitForMovement();
          while (c.isCurrentlyWalking()) c.sleep(ticks(1));
          return ticks(1);
        }
      }
      walkPath(path);
      return ticks(4);
    }
    if (c.currentY() < 100) {
      int[] portal = c.getNearestObjectById(exits[choice]);
      if (portal != null) {
        if (c.atObject(portal[0], portal[1])) {
          waitForMovement();
          while (c.isCurrentlyWalking()) c.sleep(ticks(1));
          return ticks(1);
        }
      }
    }
    if (choice == 8) {
      ORSCharacter shopkeeper = c.getNearestNpcById(522, false);
      if (shopkeeper != null) {
        if (!c.isInShop()) {
          c.npcCommand1(shopkeeper.serverIndex);
          waitForMovement();
          while (c.isCurrentlyWalking()) c.sleep(ticks(1));
          return ticks(2);
        }
        if (c.getInventoryItemCount(10) < 5 * (30 - c.getInventoryItemCount())
            || c.getInventoryItemCount(1299) < 30 - c.getInventoryItemCount()) {
          c.log("Out rune stones or coins!");
          c.log("Crafted " + units + " runes at " + hourly + " per hour.");
          c.stop();
          return ticks(1);
        }
        if (c.getShopItemCount(1299) == 0) {
          c.shopSell(1299, 30 - c.getInventoryItemCount());
          return ticks(1);
        }
        c.shopBuy(1299, c.getShopItemCount(1299));
        return ticks(1);
      }
    } else {
      ORSCharacter banker = c.getNearestNpcByIds(bankers, false);
      if (banker != null) {
        if (!c.isInBank()) {
          c.npcCommand1(banker.serverIndex);
          waitForMovement();
          while (c.isCurrentlyWalking()) c.sleep(ticks(1));
          return ticks(1);
        }
        if (c.getBankItemCount(ItemId.RUNE_STONE.getId()) < 30 - c.getInventoryItemCount()) {
          c.log("Out rune stones!");
          c.log("Crafted " + units + " runes at " + hourly + " per hour.");
          c.stop();
          return ticks(1);
        }
        if (c.isItemInInventory(runes[choice])) {
          c.depositItem(runes[choice], c.getInventoryItemCount(runes[choice]));
          return ticks(1);
        }
        c.withdrawItem(ItemId.RUNE_STONE.getId(), 30 - c.getInventoryItemCount());
        return ticks(1);
      }
    }
    walkPath(reversed);
    return ticks(4);
  }

  private int miningLoop() {
    if (c.getInventoryItemCount() < 30) {
      if (c.getPlayer().bubbleTimeout > 0) return ticks(1);
      int[] essence = c.getNearestObjectById(1227);
      if (essence != null) {
        c.atObject(essence[0], essence[1]);
        waitForMovement();
        while (c.isCurrentlyWalking()) c.sleep(ticks(1));
        return ticks(1);
      }
      if (c.getDistanceFromLocalPlayer(104, 524) > 5) {
        c.walkToAsync(104, 524, 0);
        return ticks(2);
      }
      ORSCharacter aburey = c.getNearestNpcById(54, false);
      if (aburey != null) {
        c.npcCommand1(c.getNearestNpcById(54, false).serverIndex);
        waitForMovement();
        while (c.isCurrentlyWalking()) c.sleep(ticks(1));
      }
      return ticks(1);
    }
    int[] portal = c.getNearestObjectById(1226);
    if (portal != null) {
      c.atObject(portal[0], portal[1]);
      waitForMovement();
      while (c.isCurrentlyWalking()) c.sleep(ticks(1));
      return ticks(1);
    }
    if (c.getDistanceFromLocalPlayer(103, 512) > 5) {
      c.walkToAsync(103, 512, 0);
      return ticks(2);
    }
    ORSCharacter banker = c.getNearestNpcById(95, false);
    if (!c.isInBank() && banker != null) {
      c.npcCommand1(banker.serverIndex);
      waitForMovement();
      while (c.isCurrentlyWalking()) c.sleep(ticks(1));
      return ticks(1);
    }
    c.depositItem(1299, c.getInventoryItemCount(1299));
    return ticks(1);
  }

  private void walkPath(MapPoint... path) {
    MapPoint dest = null;
    for (MapPoint point : path) {
      if (c.getDistanceFromLocalPlayer(point.getX(), point.getY()) < 20) {
        dest = point;
      }
    }
    if (dest != null) {
      c.walkToAsync(dest.getX(), dest.getY(), 0);
    }
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      controller.drawString("@bla@FoulRunecraft", 6, 36, 0xFFFFFF, 7);
      controller.drawString("@red@Foul@whi@Runecraft", 4, 34, 0xFFFFFF, 7);
      controller.drawString("@whi@Runtime: " + runtime, 6, 50, 0xFFFFFF, 1);
      controller.drawString("@whi@" + action + ": " + units, 6, 64, 0xFFFFFF, 1);
      controller.drawString("@whi@Per Hour: " + hourly, 6, 78, 0xFFFFFF, 1);
    }
  }

  public int ticks(int amount) {
    return amount * 660;
  }

  public int perHour(long start, int amount) {
    long seconds = (System.currentTimeMillis() - start) / 1000;
    if (seconds < 1) return 0;
    return Math.round((float) amount / seconds * 60 * 60);
  }

  public String calculateTime(long millis) {
    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
    long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
    long days = TimeUnit.MILLISECONDS.toDays(millis);
    if (days > 0) return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
    if (hours > 0) return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    return String.format("%02d:%02d", minutes, seconds);
  }

  private void waitForMovement() {
    long timeout = System.currentTimeMillis() + 1000L;
    while (!c.isCurrentlyWalking() && System.currentTimeMillis() <= timeout) c.sleep(100);
  }

  private void setupGUI() {
    frame = new JFrame("FoulRunecraft");
    JComboBox<String> comboBox = new JComboBox<>(types);
    JLabel label =
        new JLabel(
            "<html><center>Start Locations<br/>"
                + "Air - Falador East Bank<br/>"
                + "Mind - Falador West Bank<br/>"
                + "Water - Draynor Bank<br/>"
                + "Earth - Varrock East Bank<br/>"
                + "Fire - Al Kharid Bank<br/>"
                + "Body - Edgeville Bank<br/>"
                + "Cosmic - Zanaris Bank<br/>"
                + "Chaos - Edgeville Bank<br/>"
                + "Natures - Tai Bwo Wannai Store<br/>"
                + "Mining - Varrock East Bank</center></html>");
    comboBox.setSelectedIndex(0);
    JButton button = getjButton(comboBox);
    frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
    label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    frame.getContentPane().add(label);
    comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
    comboBox.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    frame.getContentPane().add(comboBox);
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    frame.getContentPane().add(button);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private JButton getjButton(JComboBox<String> comboBox) {
    JButton button = new JButton("Start");
    button.addActionListener(
        e -> {
          choice = comboBox.getSelectedIndex();
          start = System.currentTimeMillis();
          timer.scheduleAtFixedRate(task, 0, 1000);
          frame.dispose();
        });
    return button;
  }
}
