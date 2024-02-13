package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;

/**
 * This is AIOSmelter written for IdleRSC.
 *
 * <p>Standard Falador/AK smelter script.
 *
 * <p>Batch bars MUST be toggles on to function properly.
 *
 * <p>Features:
 *
 * <p>All bars smeltable, Goldsmithing gauntlets, Cannonballs, Silver/gold/gem Items, All Crowns.
 *
 * <p>CLI arg support (example:--scriptname "AIOSmelter" --scriptarguments "al-kharid" "steel bar")
 *
 * <p>"steel bar" can be changed to the string version of the bot "options"
 *
 * <p>Will not work as script selector parameters, should be used in arguments of launcher.
 *
 * @author Dvorak - origional script
 *     <p>Searos - modified and expanded
 *     <p>Kaila - full update, gui, rewrite
 */
/*
 * todo
 *      add uranium support/no batching support - need to change how it functionally checks for ores.
 *      -issue is that coleslaw smithing cape saves coal, so you need to check "primary" ore amount, such as mith ore.
 *      Fix script selector/cli parameters to work properly
 */
public class AIOSmelter extends IdleScript {
  private static final Controller c = Main.getController();
  private JFrame scriptFrame = null;
  private boolean guiSetup = false;
  private boolean scriptStarted = false;
  private int barId = -1;
  private int primaryOreId = 0;
  private int secondaryOreId = 0;
  private int primaryOreInBank = 0;
  private int secondaryOreInBank = 0;
  private int barsInBank = 0;
  private long startTime;
  private int destinationId = -1;
  private final int[] productIds = { // product smelting in same order as options
    169,
    170,
    384,
    171,
    1041,
    172,
    173,
    174,
    408,
    44,
    1027,
    283,
    288,
    296,
    284,
    289,
    297,
    285,
    290,
    298,
    286,
    291,
    299,
    287,
    292,
    300,
    543,
    544,
    524,
    ItemId.GOLD_CROWN.getId(),
    ItemId.SAPPHIRE_CROWN.getId(),
    ItemId.EMERALD_CROWN.getId(),
    ItemId.RUBY_CROWN.getId(),
    ItemId.DIAMOND_CROWN.getId(),
    ItemId.DRAGONSTONE_CROWN.getId()
  };
  private String barName = "";
  private String primaryName = "";
  private String secondaryName = "";
  private final long startTimestamp = System.currentTimeMillis() / 1000L;
  private int success = 0;
  private final String[] options =
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
  private final String[] destinations = new String[] {"Falador", "Al-Kharid"};
  private Map<Integer, Integer> ingredients = null;

  public int start(String[] parameters) {
    // c.quitIfAuthentic();
    c.setBatchBarsOn();
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      // scriptStarted = false;
      scriptStart();
    } /*else {
        try {
          // example for parameters here: "Al-Kharid" "Steel Bar"
          for (int i = 0; i < destinations.length; i++) {
            String option = destinations[i];

            if (option.equalsIgnoreCase(parameters[0])) {
              destinationId = i;
              break;
            }
          }

          for (int i = 0; i < options.length; i++) {
            String option = options[i];

            if (option.equalsIgnoreCase(parameters[1])) {
              barId = barIds[i];
              ingredients = ingredientsMapping.get(barId);
              break;
            }
          }

          if (barId == -1 || ingredients == null) {
            throw new Exception("Ingredients not selected!");
          }

          parseValues(); // has barID set by now
          if (!c.isAuthentic() && !orsc.Config.C_BATCH_PROGRESS_BAR) c.toggleBatchBars();
          scriptStarted = true;
        } catch (Exception e) {
          System.out.println("Could not parse parameters!");
          c.displayMessage("@red@Could not parse parameters!");
          c.stop();
        }
      }*/
    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (isEnoughOre()) {
        if (c.getNearestObjectById(118) == null) {
          c.setStatus("Walking to furnace..");
          if (destinationId == 0) {
            c.walkTo(318, 551, 0, true);
            c.walkTo(311, 545, 0, true);
          }
          if (destinationId == 1) {
            c.walkTo(84, 679, 0, true);
          }
        }
        Iterator<Entry<Integer, Integer>> iterator = ingredients.entrySet().iterator();
        int oreId = iterator.next().getKey();
        int mouldAnswer = -1;
        int gemAnswer = 0;

        if (oreId == 1057) { // do not use the cannonball mold on the furnace!
          oreId = 171;
        } else if (c.getInventoryItemCount(293) > 0) {
          oreId = 172;
          mouldAnswer = 0;
        } else if (c.getInventoryItemCount(295) > 0) {
          oreId = 172;
          mouldAnswer = 0; // was 1, Fixes menuing after crafting update
        } else if (c.getInventoryItemCount(294) > 0) {
          oreId = 172;
          mouldAnswer = 0; // was 2, Fixes menuing after crafting update
        } else if (c.getInventoryItemCount(1502) > 0) {
          oreId = 172;
          mouldAnswer = 0;
        }

        if (c.isAuthentic()) { // for uranium only
          if (c.getInventoryItemCount(164) > 0) gemAnswer = 1;
          if (c.getInventoryItemCount(163) > 0) gemAnswer = 2;
          if (c.getInventoryItemCount(162) > 0) gemAnswer = 3;
          if (c.getInventoryItemCount(161) > 0) gemAnswer = 4;
          if (c.getInventoryItemCount(523) > 0) gemAnswer = 5;
        }
        if (c.getInventoryItemCount(oreId) > 0 && c.getNearestObjectById(118) != null) {

          c.waitForBatching(false);
          if (c.getInventoryItemCount(699) > 0) { // wield gauntlets
            c.setStatus("Wielding gauntlets..");
            c.equipItem(c.getInventoryItemSlotIndex(699));
            c.sleep(618);
          }
          c.setStatus("Smelting!");
          if (c.getShouldSleep()) c.sleepHandler(true);
          if (!c.isBatching()) {
            c.useItemIdOnObject(
                c.getNearestObjectById(118)[0], c.getNearestObjectById(118)[1], oreId);
            c.sleep(640); // added tick to resync the bot before checking batching
          }
          if (oreId == 171) {
            c.sleep(
                3000); // cannonballs take way longer and can be interrupted by starting another one
          } else if (oreId == 172) {
            c.sleep(800);
            c.optionAnswer(mouldAnswer);
            c.sleep(800);
            c.optionAnswer(gemAnswer);
            c.sleep(640);
            if (!c.isAuthentic()) {
              c.waitForBatching(false);
            }
          } else c.sleep(640);
          c.waitForBatching(false);
        }

      } else {
        c.setStatus("Banking..");
        if (destinationId == 0) {
          c.walkTo(318, 551, 0, true);
          c.walkTo(329, 553, 0, true);
        }
        if (destinationId == 1) {
          c.walkTo(87, 694, 0, true);
        }
        bank();
      }
    }
  }

  private void bank() {
    c.setStatus("@gre@Banking..");
    c.openBank();
    c.sleep(2000);
    if (c.isInBank()) {
      for (int itemId : c.getInventoryUniqueItemIds()) {
        if (itemId != 0 && itemId != 1263 && itemId != 1057) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
          c.sleep(618);
        }
      }
      c.sleep(1280);
      for (Map.Entry<Integer, Integer> entry : ingredients.entrySet()) {
        if (entry.getKey() == 699) continue;

        if (entry.getKey() == 151 || entry.getKey() == 153) {
          if (c.getInventoryItemCount(1263) > 0)
            c.withdrawItem(entry.getKey(), entry.getValue() - 1);
          else c.withdrawItem(entry.getKey(), entry.getValue());

        } else {
          c.withdrawItem(entry.getKey(), entry.getValue());
        }
        c.sleep(618);
      }
      primaryOreInBank = c.getBankItemCount(primaryOreId);
      secondaryOreInBank = c.getBankItemCount(secondaryOreId);
      barsInBank = c.getBankItemCount(barId);
      c.closeBank();
    }
  }

  private boolean isEnoughOre() {
    for (Map.Entry<Integer, Integer> entry : ingredients.entrySet()) {
      if (entry.getKey() == 699) continue;

      if (c.getInventoryItemCount(1263) > 0) {
        if (c.getInventoryItemCount(entry.getKey())
            < entry.getValue()
                - 1) // this is why bot leaves after 1 ore when batching is off. When ores in inv
          // are less than par (bank) ores, it leaves.
          return false;
      } else {
        if (c.getInventoryItemCount(entry.getKey())
            < entry
                .getValue()) // this is why bot leaves after 1 ore when batching is off. When ores
          // in inv are less than par (bank) ores, it leaves.
          return false;
      }
    }

    return true;
  }

  private void parseValues() {
    startTime = System.currentTimeMillis();
    whatIsOreId();
    c.displayMessage("@cya@AIOSmelter by Dvorak, Searos, and Kaila!");
    c.displayMessage("@cya@Al-Kharid support added by Searos. Heh.");
    c.displayMessage("@cya@Added Full GUI and more - Kaila.");
    c.displayMessage("@cya@REQUIRES Batch bars be toggle on in settings to work correctly!");
  }

  private void setupGUI() {
    JLabel header = new JLabel("AIOSmelter - Dvorak, Searos, and Kaila");
    JLabel header2 = new JLabel("Currently Does Not Support Uranium Smelting.");
    JLabel header3 = new JLabel("Start in Falador or Al-Kharid bank!");
    JLabel Label1 = new JLabel("CLI arg support (example:--scriptname \"AIOSmelter\" ");
    JLabel Label2 = new JLabel("--scriptarguments \"Al-Kharid\" \"Steel Bar\")");
    JLabel batchLabel = new JLabel("IMPORTANT: Batch Bars MUST be toggled ON in settings!!!");
    JLabel batchLabel2 = new JLabel("This ensures all bars crafted in 1 \"trip.\"");
    JComboBox<String> destination = new JComboBox<>(destinations);
    destination.setSelectedIndex(1);
    JLabel barLabel = new JLabel("Bar Type:");
    JComboBox<String> barField = new JComboBox<>(options);
    barField.setSelectedIndex(3);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          barId = productIds[barField.getSelectedIndex()];
          destinationId = destination.getSelectedIndex();
          ingredients = ingredientsMapping.get(barId);
          parseValues();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

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

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  private void whatIsOreId() {
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

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int successPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (success * scale);
      } catch (Exception e) {
        // divide by zero
      }

      // c.drawBoxAlpha(7, 7, 160, 21+14, 0xFF0000, 128);
      c.drawString("@red@AIOSmelter @gre@by Dvorak + Kaila", 6, 21, 0xFFFFFF, 1);
      c.drawString("@whi@_____________________", 6, 21 + 3, 0xFFFFFF, 1);
      c.drawString(
          "@whi@" + primaryName + " in Bank: @gre@" + this.primaryOreInBank,
          6,
          21 + 17,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@" + secondaryName + " in Bank: @gre@" + this.secondaryOreInBank,
          6,
          21 + 17 + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@" + barName + " in Bank: @gre@" + this.barsInBank,
          6,
          21 + 17 + 14 + 14,
          0xFFFFFF,
          1);
      c.drawString(
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
      c.drawString(
          "@whi@Time Remaining: " + c.timeToCompletion(success, primaryOreInBank, startTime),
          6,
          21 + 17 + 42 + 14,
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, 6, 21 + 17 + 56 + 14, 0xFFFFFF, 1);
      c.drawString("@whi@________________", 6, 21 + 17 + 56 + 14 + 3, 0xFFFFFF, 1);
    }
  }

  final Map<Integer, Map<Integer, Integer>> ingredientsMapping =
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
              ItemId.GOLD_CROWN.getId(),
              new HashMap<Integer, Integer>() { // Gold Crown
                {
                  put(ItemId.CROWN_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 29);
                }
              });
          put(
              ItemId.SAPPHIRE_CROWN.getId(),
              new HashMap<Integer, Integer>() { // Sapp Crown
                {
                  put(ItemId.CROWN_MOULD.getId(), 1);
                  put(172, 14);
                  put(164, 14);
                }
              });
          put(
              ItemId.EMERALD_CROWN.getId(),
              new HashMap<Integer, Integer>() { // Emerald Crown
                {
                  put(ItemId.CROWN_MOULD.getId(), 1);
                  put(172, 14);
                  put(163, 14);
                }
              });
          put(
              ItemId.RUBY_CROWN.getId(),
              new HashMap<Integer, Integer>() { // ruby Crown
                {
                  put(ItemId.CROWN_MOULD.getId(), 1);
                  put(172, 14);
                  put(162, 14);
                }
              });
          put(
              ItemId.DIAMOND_CROWN.getId(),
              new HashMap<Integer, Integer>() { // Diamond Crown
                {
                  put(ItemId.CROWN_MOULD.getId(), 1);
                  put(172, 14);
                  put(161, 14);
                }
              });
          put(
              ItemId.DRAGONSTONE_CROWN.getId(),
              new HashMap<Integer, Integer>() { // Dragonstone Crown
                {
                  put(ItemId.CROWN_MOULD.getId(), 1);
                  put(172, 14);
                  put(523, 14);
                }
              });
        }
      };
}
