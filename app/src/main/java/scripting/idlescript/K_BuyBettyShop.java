package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * Buys newts or runes or newts and runes from Betty in port sarim
 *
 * <p>This bot supports the autostart Parameter autostart collects Newts
 *
 * @author Kaila
 */
public class K_BuyBettyShop extends K_kailaScript {
  private final String[] options = new String[] {"Runes then Newts", "Newts", "Elemental Runes"};
  private final int[] runeIds = {
    ItemId.AIR_RUNE.getId(),
    ItemId.EARTH_RUNE.getId(),
    ItemId.WATER_RUNE.getId(),
    ItemId.FIRE_RUNE.getId()
  };
  private int option = -1;
  private boolean scriptStarted = false;
  private boolean guiSetup = false;
  private boolean craftCapeTeleport = false;
  private int runesBought = 0;
  private int runesBanked = 0;
  private int newtsBought = 0;
  private int newtsBanked = 0;
  private final long startTimestamp = System.currentTimeMillis() / 1000L;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (parameters.length > 0 && !parameters[0].isEmpty()) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.displayMessage("Auto-starting, Runes and Newts", 0);
        System.out.println("Auto-starting, Runes and Newts");
        option = 0;
        guiSetup = true;
        scriptStarted = true;
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
      c.displayMessage("@red@BettyBuyer by Kaila!");
      c.displayMessage("@red@Buys Newts/Runes from Betty (Sarim)");
      c.displayMessage("@red@Start at Betty, Fally south, or Craft Guild!");
      c.displayMessage("@red@This bot supports the \"autostart\" Parameter");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 625 || c.currentX() > 275) {
        bank();
        walkToSpot();
        c.sleep(1380);
      }
      scriptStart();
    }
    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {

    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount() < 30) {
        c.setStatus("@gre@Buying stuff..");
        ORSCharacter npc = c.getNearestNpcById(149, false);

        if (npc != null) {

          if (!c.isInShop()) {
            if (c.isAuthentic()) {
              c.talkToNpc(npc.serverIndex);
              c.sleep(2000);
              c.optionAnswer(0);
              c.sleep(1000);
            } else {
              c.npcCommand1(npc.serverIndex);
              c.sleep(1000);
            }
          }

          if (c.getInventoryItemCount() < 30) {
            if (option == 2) { // only runes
              if (c.isInShop() && c.getShopItemCount(runeIds[0]) > 0
                  || c.getShopItemCount(runeIds[1]) > 0
                  || c.getShopItemCount(runeIds[2]) > 0
                  || c.getShopItemCount(runeIds[3]) > 0) {
                for (int runeId : runeIds) {
                  c.shopBuy(runeId, c.getShopItemCount(runeId));
                  c.sleep(250);
                }
              } else {
                c.sleep(250);
              }
            } else if (option == 1) { // only newts
              if (c.isInShop() && c.getShopItemCount(270) > 0) {
                c.shopBuy(270, c.getShopItemCount(270));
                c.sleep(250);
              } else {
                c.sleep(250);
              }
            } else if (option == 0) { // runes then newts
              if (c.isInShop() && c.getShopItemCount(runeIds[0]) > 0
                  || c.getShopItemCount(runeIds[1]) > 0
                  || c.getShopItemCount(runeIds[2]) > 0
                  || c.getShopItemCount(runeIds[3]) > 0) {
                for (int runeId : runeIds) {
                  c.shopBuy(runeId, c.getShopItemCount(runeId));
                  c.sleep(250);
                }
              }
              if (c.isInShop() && c.getShopItemCount(270) > 0) {
                c.shopBuy(270, c.getShopItemCount(270));
                c.sleep(250);
              } else {
                c.sleep(250);
              }
            }
          }
        }

      } else {
        walkToBank();
        bank();
        walkToSpot();
      }

      c.sleep(100);
    }
  }

  public void bank() {

    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);

    runesBought =
        runesBought
            + c.getInventoryItemCount(runeIds[0])
            + c.getInventoryItemCount(runeIds[1])
            + c.getInventoryItemCount(runeIds[2])
            + c.getInventoryItemCount(runeIds[3]);
    newtsBought += c.getInventoryItemCount(270);

    if (c.isInBank()) {

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != ItemId.COINS.getId() && itemId != ItemId.CRAFTING_CAPE.getId()) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      runesBanked =
          c.getBankItemCount(runeIds[0])
              + c.getBankItemCount(runeIds[1])
              + c.getBankItemCount(runeIds[2])
              + c.getBankItemCount(runeIds[3]);
      newtsBanked = c.getBankItemCount(270);
      c.sleep(100);
      c.closeBank();
    }
  }

  public void walkToBank() {
    c.setStatus("@gre@Walking to bank..");
    c.walkTo(270, 632);
    if (craftCapeTeleport && (c.getInventoryItemCount(ItemId.CRAFTING_CAPE.getId()) != 0)) {
      c.setStatus("@gre@Going to Bank. Casting craft cape teleport.");
      teleportCraftCape();
      c.walkTo(347, 600);
      craftGuildDoorEntering(-1);
      c.walkTo(347, 607);
      c.walkTo(346, 608);
    } else {
      c.walkTo(270, 622);
      c.walkTo(270, 613);
      c.walkTo(276, 607);
      c.walkTo(282, 601);
      c.walkTo(282, 591);
      c.walkTo(287, 572);
    }
    totalTrips = totalTrips + 1;
    c.setStatus("@red@Done Walking..");
  }

  public void walkToSpot() {
    c.setStatus("@gre@Walking back to Spot..");
    if (craftCapeTeleport && (c.getInventoryItemCount(ItemId.CRAFTING_CAPE.getId()) != 0)) {
      teleportCraftCape();
      c.walkTo(340, 598);
      c.walkTo(335, 598);
      c.walkTo(325, 608);
      c.walkTo(320, 615);
      c.walkTo(310, 625);
      c.walkTo(300, 625);
      c.walkTo(290, 625);
      c.walkTo(285, 625);
      c.walkTo(281, 629);
      c.walkTo(270, 629);
    } else {
      c.walkTo(287, 572);
      c.walkTo(282, 591);
      c.walkTo(282, 601);
      c.walkTo(276, 607);
      c.walkTo(270, 613);
      c.walkTo(270, 622);
    }
    c.walkTo(270, 632); // at door
    c.setStatus("@red@Done Walking..");
  }

  public void setupGUI() {
    final JFrame scriptFrame = new JFrame("Betty Buyer ~ Kaila");
    JLabel headerLabel = new JLabel("Buys Newts/Runes from Betty (Sarim)");
    JLabel Label1 = new JLabel("Start at Betty, Fally south, or Craft Guild!");
    JLabel Label2 = new JLabel("This bot supports the \"autostart\" Parameter");
    JLabel Label3 = new JLabel("autostart collects Newts");
    JComboBox<String> optionField = new JComboBox<>(options);
    JCheckBox craftCapeCheckbox = new JCheckBox("99 Crafting Cape Teleport?", false);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          option = optionField.getSelectedIndex();
          craftCapeTeleport = craftCapeCheckbox.isSelected();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;

          // c.displayMessage("@red@AIOCooker by Dvorak. Let's party like it's 2004!");
        });

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(headerLabel);
    scriptFrame.add(Label1);
    scriptFrame.add(Label2);
    scriptFrame.add(Label3);
    scriptFrame.add(optionField);
    scriptFrame.add(craftCapeCheckbox);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    int runesPerHr = 0;
    int newtsPerHr = 0;
    long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
    try {
      float timeRan = currentTimeInSeconds - startTimestamp;
      float scale = (60 * 60) / timeRan;
      runesPerHr = (int) (runesBought * scale);
      newtsPerHr = (int) (newtsBought * scale);
    } catch (Exception e) {
      // divide by zero
    }

    int height = 21 + 14 + 14;
    if (option == 2) {
      height += 14 + 14;
    }

    c.drawString("@gre@BettyBuyer @whi@~ @mag@Kaila", 10, 21, 0xFFFFFF, 1);

    if (option == 2) {
      c.drawString(
          "@whi@Runes bought: @yel@"
              + String.format("%,d", runesBought)
              + "@yel@ (@whi@"
              + String.format("%,d", runesPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Runes in bank: @yel@" + String.format("%,d", runesBanked),
          10,
          21 + (14 * 2),
          0xFFFFFF,
          1);
    } else if (option == 1) {
      c.drawString(
          "@whi@Newts bought: @yel@"
              + String.format("%,d", newtsBought)
              + "@yel@ (@whi@"
              + String.format("%,d", newtsPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Newts in bank: @yel@" + String.format("%,d", newtsBanked),
          10,
          21 + (14 * 2),
          0xFFFFFF,
          1);
    } else {
      c.drawString(
          "@whi@Runes bought: @yel@"
              + String.format("%,d", runesBought)
              + "@yel@ (@whi@"
              + String.format("%,d", runesPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Runes in bank: @yel@" + String.format("%,d", runesBanked),
          10,
          21 + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Newts bought: @yel@"
              + String.format("%,d", newtsBought)
              + "@yel@ (@whi@"
              + String.format("%,d", newtsPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Newts in bank: @yel@" + String.format("%,d", newtsBanked),
          10,
          21 + (14 * 4),
          0xFFFFFF,
          1);
    }
  }
}
