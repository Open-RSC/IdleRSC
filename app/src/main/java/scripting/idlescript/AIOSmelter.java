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
 *     <p>Seatta - fixed holy/unholy symbol crafting and replaced ids with ItemIds
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
    ItemId.BRONZE_BAR.getId(),
    ItemId.IRON_BAR.getId(),
    ItemId.SILVER_BAR.getId(),
    ItemId.STEEL_BAR.getId(),
    ItemId.MULTI_CANNON_BALL.getId(),
    ItemId.GOLD_BAR.getId(),
    ItemId.MITHRIL_BAR.getId(),
    ItemId.ADAMANTITE_BAR.getId(),
    ItemId.RUNITE_BAR.getId(),
    ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.getId(),
    ItemId.UNSTRUNG_UNHOLY_SYMBOL_OF_ZAMORAK.getId(),
    ItemId.GOLD_RING.getId(),
    ItemId.GOLD_NECKLACE.getId(),
    ItemId.UNSTRUNG_GOLD_AMULET.getId(),
    ItemId.SAPPHIRE_RING.getId(),
    ItemId.SAPPHIRE_NECKLACE.getId(),
    ItemId.UNSTRUNG_SAPPHIRE_AMULET.getId(),
    ItemId.EMERALD_RING.getId(),
    ItemId.EMERALD_NECKLACE.getId(),
    ItemId.UNSTRUNG_EMERALD_AMULET.getId(),
    ItemId.RUBY_RING.getId(),
    ItemId.RUBY_NECKLACE.getId(),
    ItemId.UNSTRUNG_RUBY_AMULET.getId(),
    ItemId.DIAMOND_RING.getId(),
    ItemId.DIAMOND_NECKLACE.getId(),
    ItemId.UNSTRUNG_DIAMOND_AMULET.getId(),
    ItemId.DRAGONSTONE_RING.getId(),
    ItemId.DRAGONSTONE_NECKLACE.getId(),
    ItemId.UNSTRUNG_DRAGONSTONE_AMULET.getId(),
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
    if (!c.isAuthentic()) c.setBatchBars(true);
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

    // Fixes UI not showing up after stopping the script once in a session
    if (!c.isRunning()) guiSetup = false;
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
            c.walkTo(318, 551, 0, true, false);
            c.walkTo(311, 545, 0, true, false);
          }
          if (destinationId == 1) {
            c.walkTo(84, 679, 0, true, false);
          }
        }
        Iterator<Entry<Integer, Integer>> iterator = ingredients.entrySet().iterator();
        int oreId = iterator.next().getKey();
        int mouldAnswer = -1;
        int gemAnswer = 0;

        if (oreId
            == ItemId.CANNON_AMMO_MOULD.getId()) { // do not use the cannonball mold on the furnace!
          oreId = ItemId.STEEL_BAR.getId();
        } else if (c.getInventoryItemCount(ItemId.RING_MOULD.getId()) > 0) {
          oreId = ItemId.GOLD_BAR.getId();
          mouldAnswer = 0;
        } else if (c.getInventoryItemCount(ItemId.NECKLACE_MOULD.getId()) > 0) {
          oreId = ItemId.GOLD_BAR.getId();
          mouldAnswer = 0; // was 1, Fixes menuing after crafting update
        } else if (c.getInventoryItemCount(ItemId.AMULET_MOULD.getId()) > 0) {
          oreId = ItemId.GOLD_BAR.getId();
          mouldAnswer = 0; // was 2, Fixes menuing after crafting update
        } else if (c.getInventoryItemCount(ItemId.CROWN_MOULD.getId()) > 0) {
          oreId = ItemId.GOLD_BAR.getId();
          mouldAnswer = 0;
        } else if (c.getInventoryItemCount(ItemId.HOLY_SYMBOL_MOULD.getId()) > 0) {
          oreId = ItemId.SILVER_BAR.getId();
          mouldAnswer = 0;
        } else if (c.getInventoryItemCount(ItemId.UNHOLY_SYMBOL_MOULD.getId()) > 0) {
          oreId = ItemId.SILVER_BAR.getId();
          mouldAnswer = 1;
        }

        if (c.isAuthentic()) { // for uranium only
          if (c.getInventoryItemCount(ItemId.SAPPHIRE.getId()) > 0) gemAnswer = 1;
          if (c.getInventoryItemCount(ItemId.EMERALD.getId()) > 0) gemAnswer = 2;
          if (c.getInventoryItemCount(ItemId.RUBY.getId()) > 0) gemAnswer = 3;
          if (c.getInventoryItemCount(ItemId.DIAMOND.getId()) > 0) gemAnswer = 4;
          if (c.getInventoryItemCount(ItemId.DRAGONSTONE.getId()) > 0) gemAnswer = 5;
        }
        if (c.getInventoryItemCount(oreId) > 0 && c.getNearestObjectById(118) != null) {

          c.waitForBatching(false);
          if (c.getInventoryItemCount(ItemId.GAUNTLETS_OF_GOLDSMITHING.getId())
              > 0) { // wield gauntlets
            c.setStatus("Wielding gauntlets..");
            c.equipItem(c.getInventoryItemSlotIndex(ItemId.GAUNTLETS_OF_GOLDSMITHING.getId()));
            c.sleep(618);
          }
          c.setStatus("Smelting!");
          if (c.getShouldSleep()) c.sleepHandler(true);
          if (!c.isBatching()) {
            c.useItemIdOnObject(
                c.getNearestObjectById(118)[0], c.getNearestObjectById(118)[1], oreId);
            c.sleep(640); // added tick to resync the bot before checking batching
          }
          if (oreId == ItemId.STEEL_BAR.getId()) {
            c.sleep(
                3000); // cannonballs take way longer and can be interrupted by starting another one
          } else if (oreId == ItemId.GOLD_BAR.getId() || oreId == ItemId.SILVER_BAR.getId()) {
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
          c.walkTo(318, 551, 0, true, false);
          c.walkTo(329, 553, 0, true, false);
        }
        if (destinationId == 1) {
          c.walkTo(87, 694, 0, true, false);
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
        if (itemId != 0
            && itemId != ItemId.SLEEPING_BAG.getId()
            && itemId != ItemId.CANNON_AMMO_MOULD.getId()) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
          c.sleep(618);
        }
      }
      c.sleep(1280);
      for (Map.Entry<Integer, Integer> entry : ingredients.entrySet()) {
        if (entry.getKey() == ItemId.GAUNTLETS_OF_GOLDSMITHING.getId()) continue;

        if (entry.getKey() == ItemId.IRON_ORE.getId()
            || entry.getKey() == ItemId.MITHRIL_ORE.getId()) {
          if (c.getInventoryItemCount(ItemId.SLEEPING_BAG.getId()) > 0)
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
      if (entry.getKey() == ItemId.GAUNTLETS_OF_GOLDSMITHING.getId()) continue;

      if (c.getInventoryItemCount(ItemId.SLEEPING_BAG.getId()) > 0) {
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
    if (barId == ItemId.BRONZE_BAR.getId()) { // bronze Bars
      primaryOreId = ItemId.COPPER_ORE.getId();
      secondaryOreId = ItemId.TIN_ORE.getId();
      barName = "Bronze Bars";
      primaryName = "Copper";
      secondaryName = "Tin";
    } else if (barId == ItemId.IRON_BAR.getId()) { // Iron Bars
      primaryOreId = ItemId.IRON_ORE.getId();
      barName = "Iron Bars";
      primaryName = "Iron Ore";
      secondaryName = "N/A";
    } else if (barId == ItemId.SILVER_BAR.getId()) { // Silver Bars
      primaryOreId = ItemId.SILVER.getId();
      barName = "Silver Bars";
      primaryName = "Silver Ore";
      secondaryName = "N/A";
    } else if (barId == ItemId.STEEL_BAR.getId()) { // Steel Bars
      primaryOreId = ItemId.IRON_ORE.getId();
      secondaryOreId = ItemId.COAL.getId();
      barName = "Steel Bars";
      primaryName = "Iron Ore";
      secondaryName = "Coal Ore";
    } else if (barId == ItemId.MULTI_CANNON_BALL.getId()) { // Cannonballs
      primaryOreId = ItemId.STEEL_BAR.getId();
      barName = "Cannonballs";
      primaryName = "Steel Bars";
      secondaryName = "N/A";
    } else if (barId == ItemId.GOLD_BAR.getId()) { // Gold Bars
      primaryOreId = ItemId.GOLD.getId();
      barName = "Gold Bars";
      primaryName = "Gold Ore";
      secondaryName = "N/A";
    } else if (barId == ItemId.MITHRIL_BAR.getId()) { // Mithril Bar
      primaryOreId = ItemId.MITHRIL_ORE.getId();
      secondaryOreId = ItemId.COAL.getId();
      barName = "Mithril Bars";
      primaryName = "Mithril Ore";
      secondaryName = "Coal Ore";
    } else if (barId == ItemId.ADAMANTITE_BAR.getId()) { // Addy Bar
      primaryOreId = ItemId.ADAMANTITE_ORE.getId();
      secondaryOreId = ItemId.COAL.getId();
      barName = "Addy Bars";
      primaryName = "Addy Ore";
      secondaryName = "Coal Ore";
    } else if (barId == ItemId.RUNITE_BAR.getId()) { // Rune Bar
      primaryOreId = ItemId.RUNITE_ORE.getId();
      secondaryOreId = ItemId.COAL.getId();
      barName = "Rune Bars";
      primaryName = "Runite Ore";
      secondaryName = "Coal Ore";
    } else if (barId == ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.getId()
        || barId == ItemId.UNSTRUNG_UNHOLY_SYMBOL_OF_ZAMORAK.getId()) { // holy/unholy/etc
      primaryOreId = 29;
      barName = "Holy/Unholy Symbols";
      primaryName = "Silver Bar";
      secondaryName = "N/A";
    } else if (barId == ItemId.GOLD_RING.getId()
        || barId == ItemId.GOLD_NECKLACE.getId()
        || barId == ItemId.UNSTRUNG_GOLD_AMULET.getId()
        || barId == ItemId.GOLD_CROWN.getId()) { // gold jewelry
      primaryOreId = ItemId.GOLD_BAR.getId();
      barName = "Gold Jewelry";
      primaryName = "Gold Ore";
      secondaryName = "N/A";
    } else if (barId == ItemId.SAPPHIRE_RING.getId()
        || barId == ItemId.SAPPHIRE_NECKLACE.getId()
        || barId == ItemId.UNSTRUNG_SAPPHIRE_AMULET.getId()
        || barId == ItemId.SAPPHIRE_CROWN.getId()) { // Sapphire Jewelry
      primaryOreId = ItemId.SAPPHIRE.getId();
      secondaryOreId = ItemId.GOLD_BAR.getId();
      barName = "Sapphire Jewelry";
      primaryName = "Sapphires";
      secondaryName = "Gold Bars";
    } else if (barId == ItemId.EMERALD_RING.getId()
        || barId == ItemId.EMERALD_NECKLACE.getId()
        || barId == ItemId.UNSTRUNG_EMERALD_AMULET.getId()
        || barId == ItemId.EMERALD_CROWN.getId()) { // Emerald Jewelry
      primaryOreId = ItemId.EMERALD.getId();
      secondaryOreId = ItemId.GOLD_BAR.getId();
      barName = "Emerald Jewelry";
      primaryName = "Emeralds";
      secondaryName = "Gold Bars";
    } else if (barId == ItemId.RUBY_RING.getId()
        || barId == ItemId.RUBY_NECKLACE.getId()
        || barId == ItemId.UNSTRUNG_RUBY_AMULET.getId()
        || barId == ItemId.RUBY_CROWN.getId()) { // Ruby Jewelry
      primaryOreId = ItemId.RUBY.getId();
      secondaryOreId = ItemId.GOLD_BAR.getId();
      barName = "Ruby Jewelry";
      primaryName = "Rubys";
      secondaryName = "Gold Bars";
    } else if (barId == ItemId.DIAMOND_RING.getId()
        || barId == ItemId.DIAMOND_NECKLACE.getId()
        || barId == ItemId.UNSTRUNG_DIAMOND_AMULET.getId()
        || barId == ItemId.DIAMOND_CROWN.getId()) { // Diamond Jewelry
      primaryOreId = ItemId.GOLD_BAR.getId();
      secondaryOreId = ItemId.TIN_ORE.getId();
      barName = "Diamond Jewelry";
      primaryName = "Diamonds";
      secondaryName = "Gold Bars";
    } else if (barId == ItemId.DRAGONSTONE_RING.getId()
        || barId == ItemId.DRAGONSTONE_NECKLACE.getId()
        || barId == ItemId.UNSTRUNG_DRAGONSTONE_AMULET.getId()
        || barId == ItemId.DRAGONSTONE_CROWN.getId()) { // Dragonstone Jewelry
      primaryOreId = ItemId.DRAGONSTONE.getId();
      secondaryOreId = ItemId.GOLD_BAR.getId();
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
              ItemId.BRONZE_BAR.getId(),
              new HashMap<Integer, Integer>() { // bronze
                {
                  put(ItemId.COPPER_ORE.getId(), 15);
                  put(ItemId.TIN_ORE.getId(), 15);
                }
              }); // bronze needs 1 copper and 1 tin
          put(
              ItemId.IRON_BAR.getId(),
              new HashMap<Integer, Integer>() { // iron
                {
                  put(ItemId.IRON_ORE.getId(), 30);
                }
              }); // iron needs 1 iron ore
          put(
              ItemId.SILVER_BAR.getId(),
              new HashMap<Integer, Integer>() { // silver
                {
                  put(ItemId.SILVER.getId(), 30);
                }
              }); // silver needs 1 silver ore
          put(
              ItemId.STEEL_BAR.getId(),
              new HashMap<Integer, Integer>() { // steel
                {
                  put(ItemId.IRON_ORE.getId(), 10);
                  put(ItemId.COAL.getId(), 20);
                }
              }); // steel needs 1 iron 2 coal
          put(
              ItemId.MULTI_CANNON_BALL.getId(),
              new HashMap<Integer, Integer>() { // cannonballs
                {
                  put(ItemId.CANNON_AMMO_MOULD.getId(), 1); // cannonballs
                  put(ItemId.STEEL_BAR.getId(), 29);
                }
              });
          put(
              ItemId.GOLD_BAR.getId(),
              new HashMap<Integer, Integer>() { // gold
                {
                  put(ItemId.GOLD.getId(), 29); // gold
                  put(ItemId.GAUNTLETS_OF_GOLDSMITHING.getId(), 1);
                }
              }); // gold needs 1 gold ore
          put(
              ItemId.MITHRIL_BAR.getId(),
              new HashMap<Integer, Integer>() { // mith bar
                {
                  put(ItemId.MITHRIL_ORE.getId(), 6);
                  put(ItemId.COAL.getId(), 24); // coal
                }
              });
          put(
              ItemId.ADAMANTITE_BAR.getId(),
              new HashMap<Integer, Integer>() { // addy bar
                {
                  put(ItemId.ADAMANTITE_ORE.getId(), 4);
                  put(ItemId.COAL.getId(), 24);
                }
              });
          put(
              ItemId.RUNITE_BAR.getId(),
              new HashMap<Integer, Integer>() { // runite bar
                {
                  put(ItemId.RUNITE_ORE.getId(), 3);
                  put(ItemId.COAL.getId(), 24);
                }
              });
          put(
              ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.getId(),
              new HashMap<Integer, Integer>() { // Holy symbol
                {
                  put(ItemId.SILVER_BAR.getId(), 29);
                  put(ItemId.HOLY_SYMBOL_MOULD.getId(), 1);
                }
              });
          put(
              ItemId.UNSTRUNG_UNHOLY_SYMBOL_OF_ZAMORAK.getId(),
              new HashMap<Integer, Integer>() { // Unholy symbol
                {
                  put(ItemId.SILVER_BAR.getId(), 29);
                  put(ItemId.UNHOLY_SYMBOL_MOULD.getId(), 1);
                }
              });
          put(
              ItemId.GOLD_RING.getId(),
              new HashMap<Integer, Integer>() { // Gold ring
                {
                  put(ItemId.RING_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 29);
                }
              });
          put(
              ItemId.GOLD_NECKLACE.getId(),
              new HashMap<Integer, Integer>() { // Gold necklace
                {
                  put(ItemId.NECKLACE_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 29);
                }
              });
          put(
              ItemId.UNSTRUNG_GOLD_AMULET.getId(),
              new HashMap<Integer, Integer>() { // Gold amulet
                {
                  put(ItemId.AMULET_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 29);
                }
              });
          put(
              ItemId.SAPPHIRE_RING.getId(),
              new HashMap<Integer, Integer>() { // Sapphire ring
                {
                  put(ItemId.RING_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.SAPPHIRE.getId(), 14);
                }
              });
          put(
              ItemId.SAPPHIRE_NECKLACE.getId(),
              new HashMap<Integer, Integer>() { // Sapphire necklace
                {
                  put(ItemId.NECKLACE_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.SAPPHIRE.getId(), 14);
                }
              });
          put(
              ItemId.UNSTRUNG_SAPPHIRE_AMULET.getId(),
              new HashMap<Integer, Integer>() { // Sapphire amulet
                {
                  put(ItemId.AMULET_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.SAPPHIRE.getId(), 14);
                }
              });
          put(
              ItemId.EMERALD_RING.getId(),
              new HashMap<Integer, Integer>() { // Emerald ring
                {
                  put(ItemId.RING_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.EMERALD.getId(), 14);
                }
              });
          put(
              ItemId.EMERALD_NECKLACE.getId(),
              new HashMap<Integer, Integer>() { // Emerald necklace
                {
                  put(ItemId.NECKLACE_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.EMERALD.getId(), 14);
                }
              });
          put(
              ItemId.UNSTRUNG_EMERALD_AMULET.getId(),
              new HashMap<Integer, Integer>() { // Emerald amulet
                {
                  put(ItemId.AMULET_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.EMERALD.getId(), 14);
                }
              });
          put(
              ItemId.RUBY_RING.getId(),
              new HashMap<Integer, Integer>() { // Ruby ring
                {
                  put(ItemId.RING_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.RUBY.getId(), 14);
                }
              });
          put(
              ItemId.RUBY_NECKLACE.getId(),
              new HashMap<Integer, Integer>() { // Ruby necklace
                {
                  put(ItemId.NECKLACE_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.RUBY.getId(), 14);
                }
              });
          put(
              ItemId.UNSTRUNG_RUBY_AMULET.getId(),
              new HashMap<Integer, Integer>() { // Ruby amulet
                {
                  put(ItemId.AMULET_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.RUBY.getId(), 14);
                }
              });
          put(
              ItemId.DIAMOND_RING.getId(),
              new HashMap<Integer, Integer>() { // Diamond ring
                {
                  put(ItemId.RING_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.DIAMOND.getId(), 14);
                }
              });
          put(
              ItemId.DIAMOND_NECKLACE.getId(),
              new HashMap<Integer, Integer>() { // Diamond necklace
                {
                  put(ItemId.NECKLACE_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.DIAMOND.getId(), 14);
                }
              });
          put(
              ItemId.UNSTRUNG_DIAMOND_AMULET.getId(),
              new HashMap<Integer, Integer>() { // Diamond amulet
                {
                  put(ItemId.AMULET_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.DIAMOND.getId(), 14);
                }
              });
          put(
              ItemId.DRAGONSTONE_RING.getId(),
              new HashMap<Integer, Integer>() { // Dragonstone ring
                {
                  put(ItemId.RING_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.DRAGONSTONE.getId(), 14);
                }
              });
          put(
              ItemId.DRAGONSTONE_NECKLACE.getId(),
              new HashMap<Integer, Integer>() { // Dragonstone necklace
                {
                  put(ItemId.NECKLACE_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.DRAGONSTONE.getId(), 14);
                }
              });
          put(
              ItemId.UNSTRUNG_DRAGONSTONE_AMULET.getId(),
              new HashMap<Integer, Integer>() { // Dragonstone amulet
                {
                  put(ItemId.AMULET_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.DRAGONSTONE.getId(), 14);
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
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.SAPPHIRE.getId(), 14);
                }
              });
          put(
              ItemId.EMERALD_CROWN.getId(),
              new HashMap<Integer, Integer>() { // Emerald Crown
                {
                  put(ItemId.CROWN_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.EMERALD.getId(), 14);
                }
              });
          put(
              ItemId.RUBY_CROWN.getId(),
              new HashMap<Integer, Integer>() { // ruby Crown
                {
                  put(ItemId.CROWN_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.RUBY.getId(), 14);
                }
              });
          put(
              ItemId.DIAMOND_CROWN.getId(),
              new HashMap<Integer, Integer>() { // Diamond Crown
                {
                  put(ItemId.CROWN_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.DIAMOND.getId(), 14);
                }
              });
          put(
              ItemId.DRAGONSTONE_CROWN.getId(),
              new HashMap<Integer, Integer>() { // Dragonstone Crown
                {
                  put(ItemId.CROWN_MOULD.getId(), 1);
                  put(ItemId.GOLD_BAR.getId(), 14);
                  put(ItemId.DRAGONSTONE.getId(), 14);
                }
              });
        }
      };
}
