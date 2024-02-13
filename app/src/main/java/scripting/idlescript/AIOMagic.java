package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import models.entities.ItemId;

/**
 * Performs all magic spells that require banking
 *
 * <p>High level alchemy Superheat Low level alchemy Curse
 *
 * <p>Enchant lvl-1 jewelry lvl2 lvl3 lvl4 lvl5
 *
 * <p>Varrock teleport Lumbridge teleport Falador teleport Camelot teleport Ardougne teleport
 * Watchtower teleport Charge
 *
 * @author Dvorak
 */
public class AIOMagic extends IdleScript {

  private static final Controller c = Main.getController();
  private final String[] spells =
      new String[] {
        "High level alchemy",
        "Superheat item",
        "Low level alchemy",
        "Enchant lvl-1 jewelry",
        "Enchant lvl-2 jewelry",
        "Enchant lvl-3 jewelry",
        "Enchant lvl-4 jewelry",
        "Enchant lvl-5 jewelry",
        "Varrock teleport",
        "Lumbridge teleport",
        "Falador teleport",
        "Camelot teleport",
        "Ardougne teleport",
        "Watchtower teleport",
        "Charge",
        "Curse"
      };

  private final String[] bars =
      new String[] {"Bronze", "Iron", "Silver", "Steel", "Gold", "Mithril", "Adamantite", "Runite"};

  private final String[] jewelry = new String[] {"Amulet", "Ring", "Crown"};
  private JFrame scriptFrame = null;
  private final int[] lootIds = {
    10, 169, 170, 171, 172, 173, 174, 384, 314, 315, 316, 317, 408, 522, 1314, 1315, 1316, 1317,
    1318, 1509, 1510, 1511, 1512, 1513, 1514, 1386
  }; // dragonstone items not supported. not like anyone will have thousands of those, right? xd

  private int spellId = -1;
  private int selectedSpellId = -1;
  private int selectedItemId = -1;
  private int selectedBarId = -1;
  private int selectedJewelryId = -1;
  private int selectedTaliId = -1;
  private int primaryOre = -1;
  private int primaryOreAmount = -1;
  private int secondaryOre = -1;
  private int secondaryOreAmount = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;
  private int success = 0;
  private boolean scriptStarted = false;
  private boolean guiSetup = false;

  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
      c.setStatus("@blu@Waiting for start..");
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      scriptStart();
    }
    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (selectedItemId == -1
          && selectedBarId == -1
          && selectedJewelryId == -1
          && selectedTaliId == -1) {
        // we are just teleporting
        c.setStatus("Casting!");
        c.castSpellOnSelf(spellId);
        c.sleep(1300);
      } else {
        if (selectedTaliId != -1) {
          // we are just withdrawing 27 talismans and casting curse on them
          int targetId = selectedTaliId;

          if (c.getInventoryItemCount(targetId) < 1) {
            c.setStatus("@blu@Banking.. ." + String.format("%,d", targetId));
            c.log("targetId:" + targetId + "," + "selectedTaliId:" + selectedTaliId + ",");
            c.openBank();

            for (int id : lootIds) {
              int amount = c.getInventoryItemCount(id);
              if (amount > 0) {
                c.depositItem(id, amount);
                c.sleep(618);
              }
            }

            c.withdrawItem(targetId, 30 - c.getInventoryItemCount());
            c.sleep(2000);
            c.closeBank();
          } else {
            c.setStatus("@blu@Casting!");
            c.castSpellOnInventoryItem(spellId, c.getInventoryItemSlotIndex(targetId));
            c.sleep(1300);
          }

        } else if (selectedItemId != -1 || selectedJewelryId != -1) {
          // we are just withdrawing 29 or 28 of a single item and casting a spell on it
          int targetId = selectedItemId != -1 ? selectedItemId : determineJewelryId();

          if (c.getInventoryItemCount(targetId) < 1) {
            c.setStatus("@blu@Banking..");
            c.openBank();

            for (int id : lootIds) {
              int amount = c.getInventoryItemCount(id);
              if (amount > 0) {
                c.depositItem(id, amount);
                c.sleep(618);
              }
            }

            c.withdrawItem(targetId, 30 - c.getInventoryItemCount());
            c.sleep(2000);
            c.closeBank();
          } else {
            c.setStatus("@blu@Casting!");
            if (targetId != ItemId.NATURE_RUNE.getId()) {
              c.castSpellOnInventoryItem(spellId, c.getInventoryItemSlotIndex(targetId));
            }
            c.sleep(1300);
          }

        } else {
          // we are doing superheat, which is a lot more complicated
          if (c.getInventoryItemCount(primaryOre) < 1) {
            c.setStatus("@blu@Banking..");
            c.openBank();

            for (int id : lootIds) {
              int amount = c.getInventoryItemCount(id);
              if (amount > 0) {
                c.depositItem(id, amount);
                c.sleep(618);
              }
            }

            c.withdrawItem(primaryOre, primaryOreAmount);
            c.withdrawItem(secondaryOre, secondaryOreAmount);
            c.sleep(2000);
            c.closeBank();
          } else {
            c.setStatus("@blu@Casting!");
            c.castSpellOnInventoryItem(spellId, c.getInventoryItemSlotIndex(primaryOre));
            c.sleep(1300);
          }
        }
      }
      if (c.getShouldSleep()) c.sleepHandler(true);
    }
  }

  public int determineJewelryId() {
    if (selectedJewelryId == 0) {
      // amulets
      switch (spellId) {
        case 3:
          return 302;
        case 13:
          return 303;
        case 24:
          return 304;
        case 30:
          return 305;
        case 42:
          return 522;
      }
    } else if (selectedJewelryId == 1) {
      // rings
      switch (spellId) {
        case 3:
          return 284;
        case 13:
          return 285;
        case 24:
          return 286;
        case 30:
          return 287;
        case 42:
          return 610;
      }
    } else if (selectedJewelryId == 2) {
      // crowns
      switch (spellId) {
        case 3:
          return 1504;
        case 13:
          return 1505;
        case 24:
          return 1506;
        case 30:
          return 1507;
        case 42:
          return 1508;
      }
    }

    return -1;
  }

  public void determineOreIds() {
    int sleepingBagModifier = c.getInventoryItemCount(1263);

    switch (selectedBarId) {
      case 0:
        primaryOre = 150;
        secondaryOre = 202;
        primaryOreAmount = secondaryOreAmount = 14;
        break;
      case 1:
        primaryOre = 151;
        primaryOreAmount = 29 - sleepingBagModifier;
        break;
      case 2:
        primaryOre = 383;
        primaryOreAmount = 29 - sleepingBagModifier;
        break;
      case 3:
        primaryOre = 151;
        primaryOreAmount = 9;
        secondaryOre = 155;
        secondaryOreAmount = 18;
        break;
      case 4:
        primaryOre = 152;
        primaryOreAmount = 29 - sleepingBagModifier;
        break;
      case 5:
        primaryOre = 153;
        primaryOreAmount = 5;
        secondaryOre = 155;
        secondaryOreAmount = 20;
        break;
      case 6:
        primaryOre = 154;
        primaryOreAmount = 4;
        secondaryOre = 155;
        secondaryOreAmount = 24;
        break;
      case 7:
        primaryOre = 409;
        primaryOreAmount = 3;
        secondaryOre = 155;
        secondaryOreAmount = 24;
        break;
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("succes") || message.contains("make a")) success++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int successPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        successPerHr = (int) (success * scale);
      } catch (Exception e) {
        // divide by zero
      }

      c.drawBoxAlpha(7, 7, 160, 21 + 14, 0xFF, 48);
      c.drawString("@whi@AIOMagic @blu@by @whi@Dvorak", 10, 21, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Casts: @blu@"
              + String.format("%,d", success)
              + " @whi@(@blu@"
              + String.format("%,d", successPerHr)
              + "@whi@/@blu@hr@whi@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
    }
  }

  public void setupGUI() {
    JLabel header = new JLabel("AIOMagic Options");

    JLabel spellLabel = new JLabel("Spell:");
    JComboBox<String> spellField = new JComboBox<>(spells);

    JLabel itemIdLabel = new JLabel("Item ID (alching)");
    JTextField itemIdField = new JTextField("118");

    JLabel barIdLabel = new JLabel("Bar type (superheat)");
    JComboBox<String> barIdField = new JComboBox<>(bars);

    JLabel jewelryIdLabel = new JLabel("Jewelry type (enchanting)");
    JComboBox<String> jewelryIdField = new JComboBox<>(jewelry);

    JLabel taliIdLabel = new JLabel("Item ID ( Talisman for Cursing)");
    JTextField taliIdField = new JTextField("1300");

    JButton startScriptButton = new JButton("Start");

    //        spellField.addActionListener(new ActionListener() {
    //        	@Override
    //        	public void actionPerformed(ActionEvent e) {
    //        		String text = spells[spellField.getSelectedIndex()];
    //
    //        		itemIdField.setEnabled(false);
    //        		barIdField.setEnabled(false);
    //        		jewelryIdField.setEnabled(false);
    //
    //        		if(text.contains("alchemy")) {
    //        			itemIdField.setEnabled(false);
    //        		} else if(text.contains("heat")) {
    //        			barIdField.setEnabled(false);
    //        		} else if(text.contains("jewelry")) {
    //        			jewelryIdField.setEnabled(false);
    //        		}
    //        	}
    //        });

    startScriptButton.addActionListener(
        e -> {
          selectedSpellId = spellField.getSelectedIndex();
          String text = spells[spellField.getSelectedIndex()];
          spellId = c.getSpellIdFromName(text);

          try {
            if (text.contains("alchemy")) {
              selectedItemId = Integer.parseInt(itemIdField.getText());
            } else if (text.contains("heat")) {
              selectedBarId = barIdField.getSelectedIndex();
            } else if (text.contains("jewelry")) {
              selectedJewelryId = jewelryIdField.getSelectedIndex();
            } else if (text.contains("Curse")) {
              selectedTaliId = Integer.parseInt(taliIdField.getText());
            }
          } catch (Exception exc) {
            c.displayMessage("Error parsing inputted values, please try again.");
            exc.printStackTrace();
            return;
          }

          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          determineOreIds();

          c.displayMessage("@red@AIOMagic by Dvorak. Let's party like it's 2004!");
        });

    //		itemIdField.setEnabled(true);
    //		barIdField.setEnabled(false);
    //		jewelryIdField.setEnabled(false);
    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(spellLabel);
    scriptFrame.add(spellField);
    scriptFrame.add(itemIdLabel);
    scriptFrame.add(itemIdField);
    scriptFrame.add(barIdLabel);
    scriptFrame.add(barIdField);
    scriptFrame.add(jewelryIdLabel);
    scriptFrame.add(jewelryIdField);
    scriptFrame.add(taliIdLabel);
    scriptFrame.add(taliIdField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }
}
