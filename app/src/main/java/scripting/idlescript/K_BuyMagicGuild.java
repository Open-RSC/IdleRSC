package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.*;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * Buys staffs or runes or staffs and runes from magicGuild
 *
 * <p>This bot supports the autostart Parameter autostart collects staffs
 *
 * @author Kaila
 */
public class K_BuyMagicGuild extends K_kailaScript {
  private final String[] options = new String[] {"Runes and Battlestaff", "Battlestaff", "Runes"};
  private final int[] runeIds = {
    ItemId.AIR_RUNE.getId(),
    ItemId.EARTH_RUNE.getId(),
    ItemId.WATER_RUNE.getId(),
    ItemId.FIRE_RUNE.getId(),
    ItemId.MIND_RUNE.getId(),
    ItemId.BODY_RUNE.getId()
  };
  private final int SHOPKEEPER_ID = 514;
  private final int SOUL_RUNE = ItemId.SOUL_RUNE.getId();
  private final int B_STAFF = ItemId.BATTLESTAFF.getId();
  private final int WITHDRAW_AMOUNT = 250000;
  private final int COINS = ItemId.COINS.getId();
  private int option = -1;
  private boolean scriptStarted = false;
  private boolean guiSetup = false;
  private boolean agilityCapeTeleport = false;
  private boolean buySouls = false;
  private int runesBought = 0;
  private int runesBanked = 0;
  private int staffsBought = 0;
  private int staffsBanked = 0;
  private final long startTimestamp = System.currentTimeMillis() / 1000L;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  /*
   * todo: add a "buy to 0" option, way more expensive so not a priority, only helps when crashed
   */
  public int start(String[] parameters) {
    if (parameters.length > 0 && !parameters[0].isEmpty()) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.displayMessage("Auto-starting, Runes and staffs", 0);
        System.out.println("Auto-starting, Runes and staffs");
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
      c.displayMessage("@red@magicGuildBuyer by Kaila!");
      c.displayMessage("@red@Buys staffs/Runes from magic Guild");
      c.displayMessage("@red@Start at magicGuild, or yanille bank!");
      c.displayMessage("@red@This bot supports the \"autostart\" Parameter");
      if (c.isInBank()) c.closeBank();
      if (c.currentY() < 1500) {
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
        ORSCharacter npc = c.getNearestNpcById(SHOPKEEPER_ID, false);

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
              if (buySouls && c.getShopItemCount(SOUL_RUNE) == 30) {
                c.shopBuy(SOUL_RUNE, 1); // only buy 1 at a time
                c.sleep(100);
              }
              if (c.isInShop()
                  && (c.getShopItemCount(runeIds[0]) > 20
                      || c.getShopItemCount(runeIds[1]) > 20
                      || c.getShopItemCount(runeIds[2]) > 20
                      || c.getShopItemCount(runeIds[3]) > 20
                      || c.getShopItemCount(runeIds[4]) > 20
                      || c.getShopItemCount(runeIds[5]) > 20)) {
                for (int runeId : runeIds) {
                  c.shopBuy(runeId, Math.max(c.getShopItemCount(runeId) - 20, 0));
                  c.sleep(100);
                }
              }
              c.sleep(200);

            } else if (option == 1) { // only staffs
              if (buySouls && c.getShopItemCount(SOUL_RUNE) == 30) {
                c.shopBuy(SOUL_RUNE, 1); // only buy 1 at a time
                c.sleep(100);
              }
              if (c.isInShop() && c.getShopItemCount(B_STAFF) == 5) {
                c.shopBuy(B_STAFF, 1); // c.getShopItemCount(bStaff)
                c.sleep(100);
              }
              c.sleep(200);

            } else if (option == 0) { // runes then staffs
              if (buySouls && c.getShopItemCount(SOUL_RUNE) == 30) {
                c.shopBuy(SOUL_RUNE, 1); // only buy 1 at a time
                c.sleep(200);
              }
              if (c.isInShop()
                  && (c.getShopItemCount(runeIds[0]) > 20
                      || c.getShopItemCount(runeIds[1]) > 20
                      || c.getShopItemCount(runeIds[2]) > 20
                      || c.getShopItemCount(runeIds[3]) > 20
                      || c.getShopItemCount(runeIds[4]) > 20
                      || c.getShopItemCount(runeIds[5]) > 20)) {
                for (int runeId : runeIds) {
                  c.shopBuy(runeId, Math.max(c.getShopItemCount(runeId) - 20, 0));
                  c.sleep(100);
                }
              }
              c.sleep(100);
              if (c.isInShop() && c.getShopItemCount(B_STAFF) == 5) {
                c.shopBuy(B_STAFF, 1);
                c.sleep(300);
              }
              c.sleep(200);
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
    staffsBought += c.getInventoryItemCount(B_STAFF);

    if (c.isInBank()) {

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != ItemId.COINS.getId() && itemId != ItemId.AGILITY_CAPE.getId()) {
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      runesBanked =
          c.getBankItemCount(runeIds[0])
              + c.getBankItemCount(runeIds[1])
              + c.getBankItemCount(runeIds[2])
              + c.getBankItemCount(runeIds[3]);
      staffsBanked = c.getBankItemCount(B_STAFF);
      c.sleep(1240);
      if (c.getInventoryItemCount(COINS) < WITHDRAW_AMOUNT) {
        if (c.getBankItemCount(COINS) < 100000) {
          c.log("You need some more coins to buy with...", "red");
          c.setAutoLogin(false);
          c.stop();
        }
        withdrawItem(COINS, WITHDRAW_AMOUNT);
      }

      c.closeBank();
    }
  }

  private void magicGuildDoorExit() {
    for (int i = 1; i <= 200; i++) {
      if (c.currentX() > 598 && c.currentX() < 601) {
        c.setStatus("@red@Crossing guild Gate..");
        c.atWallObject(599, 757); // gate won't break if someone else opens it
        c.sleep(4 * GAME_TICK);
      } else {
        break;
      }
    }
  }

  private void magicGuildDoorEnter() {
    for (int i = 1; i <= 200; i++) {
      if (c.currentX() < 599 && c.currentX() > 596) {
        c.setStatus("@red@Crossing guild Gate..");
        c.atWallObject(599, 757); // gate won't break if someone else opens it
        c.sleep(4 * GAME_TICK);
      } else {
        break;
      }
    }
  }

  private void goUpStairs() {
    for (int i = 1; i <= 200; i++) {
      if (c.currentY() < 1500 && c.currentX() > 598) {
        c.setStatus("@red@Crossing guild Gate..");
        c.atObject(602, 756); // gate won't break if someone else opens it
        c.sleep(4 * GAME_TICK);
      } else {
        break;
      }
    }
  }

  private void goDownStairs() {
    for (int i = 1; i <= 200; i++) {
      if (c.currentY() > 1500) {
        c.setStatus("@red@Going down Stairs..");
        c.atObject(602, 1700); // gate won't break if someone else opens it
        c.sleep(4 * GAME_TICK);
      } else {
        break;
      }
    }
  }

  public void walkToBank() {
    c.setStatus("@gre@Walking to bank..");
    c.walkTo(600, 1702);
    if (agilityCapeTeleport && (c.getInventoryItemCount(ItemId.AGILITY_CAPE.getId()) != 0)) {
      c.setStatus("@gre@Going to Bank. Casting agility cape teleport.");
      teleportAgilityCape();
      c.setStatus("@gre@Done Teleporting..");
      c.walkTo(582, 767);
      c.walkTo(579, 763);
      c.walkTo(584, 754);
      c.walkTo(585, 752);
    } else {
      c.walkTo(602, 1699);
      goDownStairs();
      c.walkTo(599, 757);
      magicGuildDoorExit();
      c.walkTo(591, 749);
      c.walkTo(584, 749);
      c.walkTo(584, 752);
    }
    totalTrips = totalTrips + 1;
    c.setStatus("@red@Done Walking..");
  }

  public void walkToSpot() {
    c.setStatus("@gre@Walking back to Spot..");
    c.walkTo(584, 752);
    c.walkTo(584, 749);
    c.walkTo(591, 749);
    c.walkTo(597, 757);
    magicGuildDoorEnter();
    c.walkTo(602, 759);
    goUpStairs();
    c.walkTo(600, 1702);
    c.setStatus("@red@Done Walking..");
  }

  public void setupGUI() {
    final JFrame scriptFrame = new JFrame("magicGuild Buyer ~ Kaila");
    JLabel headerLabel = new JLabel("Buys staffs/Runes from magicGuild");
    JLabel Label1 = new JLabel("Start at magicGuild or yanille bank");
    JLabel Label2 = new JLabel("This bot supports the \"autostart\" Parameter");
    JLabel Label3 = new JLabel("autostart collects staffs");
    JCheckBox soulCheckbox = new JCheckBox("Also Buy Soul Runes? (2500gp ea)", false);
    JComboBox<String> optionField = new JComboBox<>(options);
    JCheckBox agilityCapeCheckbox = new JCheckBox("99 agility Cape Teleport?", false);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          buySouls = soulCheckbox.isSelected();
          option = optionField.getSelectedIndex();
          agilityCapeTeleport = agilityCapeCheckbox.isSelected();
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
    scriptFrame.add(soulCheckbox);
    scriptFrame.add(agilityCapeCheckbox);
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
    int staffsPerHr = 0;
    long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
    try {
      float timeRan = currentTimeInSeconds - startTimestamp;
      float scale = (60 * 60) / timeRan;
      runesPerHr = (int) (runesBought * scale);
      staffsPerHr = (int) (staffsBought * scale);
    } catch (Exception e) {
      // divide by zero
    }

    int height = 21 + 14 + 14;
    if (option == 2) {
      height += 14 + 14;
    }

    c.drawString("@gre@magicGuildBuyer @whi@~ @mag@Kaila", 10, 21, 0xFFFFFF, 1);

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
          "@whi@staffs bought: @yel@"
              + String.format("%,d", staffsBought)
              + "@yel@ (@whi@"
              + String.format("%,d", staffsPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@staffs in bank: @yel@" + String.format("%,d", staffsBanked),
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
          "@whi@staffs bought: @yel@"
              + String.format("%,d", staffsBought)
              + "@yel@ (@whi@"
              + String.format("%,d", staffsPerHr)
              + "@yel@/@whi@hr@yel@)",
          10,
          21 + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@staffs in bank: @yel@" + String.format("%,d", staffsBanked),
          10,
          21 + (14 * 4),
          0xFFFFFF,
          1);
    }
  }
}
