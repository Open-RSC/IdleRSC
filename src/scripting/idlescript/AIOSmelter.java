package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This is AIOSmelter written for IdleRSC.
 *
 * <p>It is your standard Falador/AK smelter script. Batch bars MUST be toggles on to function
 * properly.
 *
 * <p>It has the following features:
 *
 * <p>* GUI * * All bars smeltable * Goldsmithing gauntlets support * Cannonball support *
 * Silver/gold/gem items craftable * All Crowns added - Kaila
 *
 * <p>CLI arg support (example:--scriptname "AIOSmelter" --scriptarguments "Al-Kharid" "Steel Bar")
 * do not work as parameters properly, should be used in arguments of launcher "Steel Bar" can be
 * changed to any of the "option" strings below.
 *
 * <p>todo add uranium support/no batching support - need to change how it functionally checks for
 * ores. -issue is that coleslaw smithing cape saves coal, so you need to check "primary" ore
 * amount, such as mith ore.
 *
 * @author Dvorak (modified by Searos)
 */
public class AIOSmelter extends IdleScript {
  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;
  int barId = -1;
  int primaryOreId = 0;
  int secondaryOreId = 0;
  int primaryOreInBank = 0;
  int secondaryOreInBank = 0;
  int barsInBank = 0;
  long startTime;
  int destinationId = -1;
  int[] barIds = {
    169, 170, 384, 171, 1041, 172, 173, 174, 408, 44, 1027, 283, 288, 296, 284, 289, 297, 285, 290,
    298, 286, 291, 299, 287, 292, 300, 543, 544, 524
  };
  String barName = "";
  String primaryName = "";
  String secondaryName = "";

  long startTimestamp = System.currentTimeMillis() / 1000L;
  int success = 0;

  String[] options =
      new String[] {
        "Bronze Bar",
        "Iron Bar",
        "Silver Bar",
        "Steel Bar",
        "Cannonballs",
        "Gold Bar",
        "Mithril Bar",
        "Adamantite Bar",
        "Runite Bar",
        "Holy symbol",
        "Unholy symbol",
        "Gold ring",
        "Gold necklace",
        "Gold amulet",
        "Sapphire ring",
        "Sapphire necklace",
        "Sapphire amulet",
        "Emerald ring",
        "Emerald necklace",
        "Emerald amulet",
        "Ruby ring",
        "Ruby necklace",
        "Ruby amulet",
        "Diamond ring",
        "Diamond necklace",
        "Diamond amulet",
        "Dragonstone ring",
        "Dragonstone necklace",
        "Dragonstone amulet",
        "Gold crown",
        "Sapphire crown",
        "Emerald crown",
        "Ruby crown",
        "Diamond crown",
        "Dragonstone crown"
      };

  String[] destinations = new String[] {"Falador", "Al-Kharid"};

  Map<Integer, Integer> ingredients = null;

  public int start(String parameters[]) {
    if (scriptStarted) {
      scriptStart();
    } else {
      if (parameters.length == 1) {
        if (!guiSetup) {
          setupGUI();
          guiSetup = true;
        }
        if (scriptStarted) {
          controller.quitIfAuthentic();
          scriptStart();
        }
      } else {
        try {
          // example for parameters here: "Al-Kharid" "Steel Bar"
          for (int i = 0; i < destinations.length; i++) {
            String option = destinations[i];

            if (option.toLowerCase().equals(parameters[0].toLowerCase())) {
              destinationId = i;
              break;
            }
          }

          for (int i = 0; i < options.length; i++) {
            String option = options[i];

            if (option.toLowerCase().equals(parameters[1].toLowerCase())) {
              barId = barIds[i];
              ingredients = ingredientsMapping.get(barId);
              break;
            }
          }

          if (barId == -1 || ingredients == null) {
            throw new Exception("Ingredients not selected!");
          }

          parseValues(); // has barID set by now
          scriptStarted = true;
        } catch (Exception e) {
          System.out.println("Could not parse parameters!");
          controller.displayMessage("@red@Could not parse parameters!");
          controller.stop();
        }
      }
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    if (isEnoughOre()) {
      if (controller.getNearestObjectById(118) == null) {
        controller.setStatus("Walking to furnace..");
        if (destinationId == 0) {
          controller.walkTo(318, 551, 0, true);
          controller.walkTo(311, 545, 0, true);
        }
        if (destinationId == 1) {
          controller.walkTo(84, 679, 0, true);
        }
      }
      Iterator<Entry<Integer, Integer>> iterator = ingredients.entrySet().iterator();
      int oreId = iterator.next().getKey();
      int mouldAnswer = -1;
      int gemAnswer = 0;

      if (oreId == 1057) { // do not use the cannonball mold on the furnace!
        oreId = 171;
      } else if (controller.getInventoryItemCount(293) > 0) {
        oreId = 172;
        mouldAnswer = 0;
      } else if (controller.getInventoryItemCount(295) > 0) {
        oreId = 172;
        mouldAnswer = 0; // was 1, Fixes menuing after crafting update
      } else if (controller.getInventoryItemCount(294) > 0) {
        oreId = 172;
        mouldAnswer = 0; // was 2, Fixes menuing after crafting update
      } else if (controller.getInventoryItemCount(1502) > 0) {
        oreId = 172;
        mouldAnswer = 0;
      }

      if (!controller.isAuthentic()) {
        gemAnswer = 0;
      } else { // for uranium
        if (controller.getInventoryItemCount(164) > 0) gemAnswer = 1;
        if (controller.getInventoryItemCount(163) > 0) gemAnswer = 2;
        if (controller.getInventoryItemCount(162) > 0) gemAnswer = 3;
        if (controller.getInventoryItemCount(161) > 0) gemAnswer = 4;
        if (controller.getInventoryItemCount(523) > 0) gemAnswer = 5;
      }
      if (controller.getInventoryItemCount(oreId) > 0
          && controller.getNearestObjectById(118) != null) {

        while (controller.isBatching()) controller.sleep(640);
        if (controller.getInventoryItemCount(699) > 0) { // wield gauntlets
          controller.setStatus("Wielding gauntlets..");
          controller.equipItem(controller.getInventoryItemSlotIndex(699));
          controller.sleep(618);
        }
        controller.setStatus("Smelting!");
        controller.sleepHandler(98, true);
        // if (controller.isBatching() == false) {
        controller.useItemIdOnObject(
            controller.getNearestObjectById(118)[0],
            controller.getNearestObjectById(118)[1],
            oreId);
        // }
        if (oreId == 171) {
          controller.sleep(
              3000); // cannonballs take way longer and can be interrupted by starting another one
        } else if (oreId == 172) {
          controller.sleep(800);
          controller.optionAnswer(mouldAnswer);
          controller.sleep(800);
          controller.optionAnswer(gemAnswer);
          controller.sleep(600);
          if (!controller.isAuthentic()) {
            while (controller.isBatching()) {
              controller.sleep(600);
            }
          }
        } else {
          controller.sleep(618);
        }

        while (controller.isBatching()) controller.sleep(340);
      }

    } else {
      controller.setStatus("Banking..");
      if (destinationId == 0) {
        controller.walkTo(318, 551, 0, true);
        controller.walkTo(329, 553, 0, true);
      }
      if (destinationId == 1) {
        controller.walkTo(87, 694, 0, true);
      }
      controller.openBank();

      for (int itemId : controller.getInventoryUniqueItemIds()) {
        if (itemId != 0 && itemId != 1263 && itemId != 1057) {
          controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
          controller.sleep(618);
        }
      }

      for (Map.Entry<Integer, Integer> entry : ingredients.entrySet()) {
        if (entry.getKey() == 699) continue;

        if (entry.getKey() == 151 || entry.getKey() == 153) {
          if (controller.getInventoryItemCount(1263) > 0)
            controller.withdrawItem(entry.getKey(), entry.getValue() - 1);
          else controller.withdrawItem(entry.getKey(), entry.getValue());

        } else {
          controller.withdrawItem(entry.getKey(), entry.getValue());
        }
        controller.sleep(618);
      }
      primaryOreInBank = controller.getBankItemCount(primaryOreId);
      secondaryOreInBank = controller.getBankItemCount(secondaryOreId);
      barsInBank = controller.getBankItemCount(barId);
    }
  }

  public boolean isEnoughOre() {
    for (Map.Entry<Integer, Integer> entry : ingredients.entrySet()) {
      if (entry.getKey() == 699) continue;

      if (controller.getInventoryItemCount(1263) > 0) {
        if (controller.getInventoryItemCount(entry.getKey())
            < entry.getValue()
                - 1) // this is why bot leaves after 1 ore when batching is off. When ores in inv
          // are less than par (bank) ores, it leaves.
          return false;
      } else {
        if (controller.getInventoryItemCount(entry.getKey())
            < entry
                .getValue()) // this is why bot leaves after 1 ore when batching is off. When ores
          // in inv are less than par (bank) ores, it leaves.
          return false;
      }
    }

    return true;
  }

  public static void centerWindow(Window frame) {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
    frame.setLocation(x, y);
  }

  public void parseValues() {
    startTime = System.currentTimeMillis();
    whatIsOreId();
    controller.displayMessage("@cya@AIOSmelter by Dvorak, Searos, and Kaila!");
    controller.displayMessage("@cya@Al-Kharid support added by Searos. Heh.");
    controller.displayMessage("@cya@Added Full GUI and more - Kaila.");
    controller.displayMessage(
        "@cya@REQUIRES Batch bars be toggle on in settings to work correctly!");
  }

  public void setupGUI() {
    JLabel header = new JLabel("AIOSmelter - Dvorak, Searos, and Kaila");
    JLabel header2 = new JLabel("Currently Does Not Support Uranium Smelting.");
    JLabel header3 = new JLabel("Start in Falador or Al-Kharid bank!");
    JLabel Label1 = new JLabel("CLI arg support (example:--scriptname \"AIOSmelter\" ");
    JLabel Label2 = new JLabel("--scriptarguments \"Al-Kharid\" \"Steel Bar\")");
    JLabel batchLabel = new JLabel("IMPORTANT: Batch Bars MUST be toggled ON in settings!!!");
    JLabel batchLabel2 = new JLabel("This ensures all bars crafted in 1 \"trip.\"");
    JComboBox<String> destination = new JComboBox<String>(destinations);
    JLabel barLabel = new JLabel("Bar Type:");
    JComboBox<String> barField = new JComboBox<String>(options);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            barId = barIds[barField.getSelectedIndex()];
            destinationId = destination.getSelectedIndex();
            ingredients = ingredientsMapping.get(barId);
            parseValues();
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            scriptStarted = true;
          }
        });

    scriptFrame = new JFrame(controller.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(header2);
    scriptFrame.add(header3);
    scriptFrame.add(Label1);
    scriptFrame.add(Label2);
    scriptFrame.add(batchLabel);
    scriptFrame.add(batchLabel2);
    scriptFrame.add(destination);
    scriptFrame.add(barLabel);
    scriptFrame.add(barField);
    scriptFrame.add(startScriptButton);

    centerWindow(scriptFrame);
    scriptFrame.setVisible(true);
    scriptFrame.pack();
  }

  public void whatIsOreId() {
    if (barId == 169) { // bronze Bars
      primaryOreId = 150;
      secondaryOreId = 202;
      barName = "Bronze Bars";
      primaryName = "Copper";
      secondaryName = "Tin";
    } else if (barId == 170) { // Iron Bars
      primaryOreId = 151;
      barName = "Iron Bars";
      primaryName = "Iron Ore";
      secondaryName = "N/A";
    } else if (barId == 384) { // Silver Bars
      primaryOreId = 383;
      barName = "Silver Bars";
      primaryName = "Silver Ore";
      secondaryName = "N/A";
    } else if (barId == 171) { // Steel Bars
      primaryOreId = 151;
      secondaryOreId = 155;
      barName = "Steel Bars";
      primaryName = "Iron Ore";
      secondaryName = "Coal Ore";
    } else if (barId == 1041) { // Cannonballs
      primaryOreId = 171;
      barName = "Cannonballs";
      primaryName = "Steel Bars";
      secondaryName = "N/A";
    } else if (barId == 172) { // Gold Bars
      primaryOreId = 152;
      barName = "Gold Bars";
      primaryName = "Gold Ore";
      secondaryName = "N/A";
    } else if (barId == 173) { // Mithril Bar
      primaryOreId = 153;
      secondaryOreId = 155;
      barName = "Mithril Bars";
      primaryName = "Mithril Ore";
      secondaryName = "Coal Ore";
    } else if (barId == 174) { // Addy Bar
      primaryOreId = 154;
      secondaryOreId = 155;
      barName = "Addy Bars";
      primaryName = "Addy Ore";
      secondaryName = "Coal Ore";
    } else if (barId == 408) { // Rune Bar
      primaryOreId = 409;
      secondaryOreId = 155;
      barName = "Rune Bars";
      primaryName = "Runite Ore";
      secondaryName = "Coal Ore";
    } else if (barId == 44 || barId == 1027) { // holy/unholy/etc
      primaryOreId = 29;
      barName = "Holy/Unholy Symbols";
      primaryName = "Silver Bar";
      secondaryName = "N/A";
    } else if (barId == 283 || barId == 288 || barId == 296) { // gold jewelry
      primaryOreId = 172;
      barName = "Gold Jewelry";
      primaryName = "Gold Ore";
      secondaryName = "N/A";
    } else if (barId == 284 || barId == 289 || barId == 297 || barId == 1504) { // Sapphire Jewelry
      primaryOreId = 164;
      secondaryOreId = 172;
      barName = "Sapphire Jewelry";
      primaryName = "Sapphires";
      secondaryName = "Gold Bars";
    } else if (barId == 285 || barId == 290 || barId == 298 || barId == 1505) { // Emerald Jewelry
      primaryOreId = 163;
      secondaryOreId = 172;
      barName = "Emerald Jewelry";
      primaryName = "Emeralds";
      secondaryName = "Gold Bars";
    } else if (barId == 286 || barId == 291 || barId == 299 || barId == 1506) { // Ruby Jewelry
      primaryOreId = 162;
      secondaryOreId = 172;
      barName = "Ruby Jewelry";
      primaryName = "Rubys";
      secondaryName = "Gold Bars";
    } else if (barId == 287 || barId == 292 || barId == 300 || barId == 1507) { // Diamond Jewelry
      primaryOreId = 172;
      secondaryOreId = 202;
      barName = "Diamond Jewelry";
      primaryName = "Diamonds";
      secondaryName = "Gold Bars";
    } else if (barId == 543
        || barId == 544
        || barId == 524
        || barId == 1508) { // Dragonstone Jewelry
      primaryOreId = 523;
      secondaryOreId = 172;
      barName = "Dragonstone Jewelry";
      primaryName = "Dragonstones";
      secondaryName = "Gold Bars";
    }
  }

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("very heavy")) success++;
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("retrieve a") || message.contains("make a")) success++;
  }

  public static String msToString(long milliseconds) {
    long sec = milliseconds / 1000;
    long min = sec / 60;
    long hour = min / 60;
    sec %= 60;
    min %= 60;
    DecimalFormat twoDigits = new DecimalFormat("00");

    return new String(
        twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec));
  }
  /** credit to chomp for toTimeToCompletion (from AA_Script) (totalBars, barsInBank, startTime) */
  public static String toTimeToCompletion(
      final int processed, final int remaining, final long time) {
    if (processed == 0) {
      return "0:00:00";
    }

    final double seconds = (System.currentTimeMillis() - time) / 1000.0;
    final double secondsPerItem = seconds / processed;
    final long ttl = (long) (secondsPerItem * remaining);
    return String.format("%d:%02d:%02d", ttl / 3600, (ttl % 3600) / 60, (ttl % 60));
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      String runTime = msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      try {
        float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (success * scale);
      } catch (Exception e) {
        // divide by zero
      }

      // controller.drawBoxAlpha(7, 7, 160, 21+14, 0xFF0000, 128);
      controller.drawString("@red@AIOSmelter @gre@by Dvorak + Kaila", 6, 21, 0xFFFFFF, 1);
      controller.drawString("@whi@_____________________", 6, 21 + 3, 0xFFFFFF, 1);
      controller.drawString(
          "@whi@" + primaryName + " in Bank: @gre@" + this.primaryOreInBank,
          6,
          21 + 17,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@" + secondaryName + " in Bank: @gre@" + this.secondaryOreInBank,
          6,
          21 + 17 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@" + barName + " in Bank: @gre@" + this.barsInBank,
          6,
          21 + 17 + 14 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@"
              + barName
              + " Smelted: @gre@"
              + String.format("%,d", success)
              + " @yel@(@gre@"
              + String.format("%,d", successPerHr)
              + "@yel@/@whi@hr@yel@)",
          6,
          21 + 17 + 28 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@whi@Time Remaining: " + toTimeToCompletion(success, primaryOreInBank, startTime),
          6,
          21 + 17 + 42 + 14,
          0xFFFFFF,
          1);
      controller.drawString("@whi@Runtime: " + runTime, 6, 21 + 17 + 56 + 14, 0xFFFFFF, 1);
      controller.drawString("@whi@________________", 6, 21 + 17 + 56 + 14 + 3, 0xFFFFFF, 1);
    }
  }

  Map<Integer, Map<Integer, Integer>> ingredientsMapping =
      new HashMap<Integer, Map<Integer, Integer>>() {
        {
          put(
              169,
              new HashMap<Integer, Integer>() { // bronze
                {
                  put(150, 15);
                  put(202, 15);
                }
              }); // bronze needs 1 copper and 1 tin
          put(
              170,
              new HashMap<Integer, Integer>() { // iron
                {
                  put(151, 30);
                }
              }); // iron needs 1 iron ore
          put(
              384,
              new HashMap<Integer, Integer>() { // silver
                {
                  put(383, 30);
                }
              }); // silver needs 1 silver ore
          put(
              171,
              new HashMap<Integer, Integer>() { // steel
                {
                  put(151, 10);
                  put(155, 20);
                }
              }); // steel needs 1 iron 2 coal
          put(
              1041,
              new HashMap<Integer, Integer>() { // cannonballs
                {
                  put(1057, 1); // cannonballs
                  put(171, 29);
                }
              });
          put(
              172,
              new HashMap<Integer, Integer>() { // gold
                {
                  put(152, 29); // gold
                  put(699, 1);
                }
              }); // gold needs 1 gold ore
          put(
              173,
              new HashMap<Integer, Integer>() { // mith bar
                {
                  put(153, 6);
                  put(155, 24); // coal
                }
              });
          put(
              174,
              new HashMap<Integer, Integer>() { // addy bar
                {
                  put(154, 4);
                  put(155, 24);
                }
              });
          put(
              408,
              new HashMap<Integer, Integer>() { // runite bar
                {
                  put(409, 3);
                  put(155, 24);
                }
              });
          put(
              44,
              new HashMap<Integer, Integer>() { // Holy symbol
                {
                  put(384, 29);
                  put(386, 1);
                }
              });
          put(
              1027,
              new HashMap<Integer, Integer>() { // Unholy symbol
                {
                  put(384, 29);
                  put(1026, 1);
                }
              });
          put(
              283,
              new HashMap<Integer, Integer>() { // Gold ring
                {
                  put(293, 1);
                  put(172, 29);
                }
              });
          put(
              288,
              new HashMap<Integer, Integer>() { // Gold necklace
                {
                  put(295, 1);
                  put(172, 29);
                }
              });
          put(
              296,
              new HashMap<Integer, Integer>() { // Gold amulet
                {
                  put(294, 1);
                  put(172, 29);
                }
              });
          put(
              284,
              new HashMap<Integer, Integer>() { // Sapphire ring
                {
                  put(293, 1);
                  put(172, 14);
                  put(164, 14);
                }
              });
          put(
              289,
              new HashMap<Integer, Integer>() { // Sapphire necklace
                {
                  put(295, 1);
                  put(172, 14);
                  put(164, 14);
                }
              });
          put(
              297,
              new HashMap<Integer, Integer>() { // Sapphire amulet
                {
                  put(294, 1);
                  put(172, 14);
                  put(164, 14);
                }
              });
          put(
              285,
              new HashMap<Integer, Integer>() { // Emerald ring
                {
                  put(293, 1);
                  put(172, 14);
                  put(163, 14);
                }
              });
          put(
              290,
              new HashMap<Integer, Integer>() { // Emerald necklace
                {
                  put(295, 1);
                  put(172, 14);
                  put(163, 14);
                }
              });
          put(
              298,
              new HashMap<Integer, Integer>() { // Emerald amulet
                {
                  put(294, 1);
                  put(172, 14);
                  put(163, 14);
                }
              });
          put(
              286,
              new HashMap<Integer, Integer>() { // Ruby ring
                {
                  put(293, 1);
                  put(172, 14);
                  put(162, 14);
                }
              });
          put(
              291,
              new HashMap<Integer, Integer>() { // Ruby necklace
                {
                  put(295, 1);
                  put(172, 14);
                  put(162, 14);
                }
              });
          put(
              299,
              new HashMap<Integer, Integer>() { // Ruby amulet
                {
                  put(294, 1);
                  put(172, 14);
                  put(162, 14);
                }
              });
          put(
              287,
              new HashMap<Integer, Integer>() { // Diamond ring
                {
                  put(293, 1);
                  put(172, 14);
                  put(161, 14);
                }
              });
          put(
              292,
              new HashMap<Integer, Integer>() { // Diamond necklace
                {
                  put(295, 1);
                  put(172, 14);
                  put(161, 14);
                }
              });
          put(
              300,
              new HashMap<Integer, Integer>() { // Diamond amulet
                {
                  put(294, 1);
                  put(172, 14);
                  put(161, 14);
                }
              });
          put(
              543,
              new HashMap<Integer, Integer>() { // Dragonstone ring
                {
                  put(293, 1);
                  put(172, 14);
                  put(523, 14);
                }
              });
          put(
              544,
              new HashMap<Integer, Integer>() { // Dragonstone necklace
                {
                  put(295, 1);
                  put(172, 14);
                  put(523, 14);
                }
              });
          put(
              524,
              new HashMap<Integer, Integer>() { // Dragonstone amulet
                {
                  put(294, 1);
                  put(172, 14);
                  put(523, 14);
                }
              });
          put(
              1503,
              new HashMap<Integer, Integer>() { // Gold Crown
                {
                  put(1502, 1);
                  put(172, 29);
                }
              });
          put(
              1504,
              new HashMap<Integer, Integer>() { // Sapp Crown
                {
                  put(1502, 1);
                  put(172, 14);
                  put(164, 14);
                }
              });
          put(
              1505,
              new HashMap<Integer, Integer>() { // Emerald Crown
                {
                  put(1502, 1);
                  put(172, 14);
                  put(163, 14);
                }
              });
          put(
              1506,
              new HashMap<Integer, Integer>() { // ruby Crown
                {
                  put(1502, 1);
                  put(172, 14);
                  put(162, 14);
                }
              });
          put(
              1507,
              new HashMap<Integer, Integer>() { // Diamond Crown
                {
                  put(1502, 1);
                  put(172, 14);
                  put(161, 14);
                }
              });
          put(
              1508,
              new HashMap<Integer, Integer>() { // Dragonstone Crown
                {
                  put(1502, 1);
                  put(172, 14);
                  put(523, 14);
                }
              });
        }
      };
}
