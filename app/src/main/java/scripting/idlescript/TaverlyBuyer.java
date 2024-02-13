package scripting.idlescript;

import bot.Main;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * Buys vials or newts in Taverly, banks in Falador
 *
 * <p>This bot supports the autostart Parameter autostart collects Newts then Vials
 *
 * @author Dvorak, rewritten by Kaila
 */
public class TaverlyBuyer extends IdleScript {
  private int option = -1;
  private boolean scriptStarted = false;
  private boolean guiSetup = false;
  private int vialsBought = 0;
  private int vialsBanked = 0;
  private int newtsBought = 0;
  private int pestalBought = 0;
  private int pestalBanked = 0;
  private int newtsBanked = 0;
  private final int VIAL_ID = ItemId.EMPTY_VIAL.getId();
  private final int NEWT_ID = ItemId.EYE_OF_NEWT.getId();
  private final int PESTAL_ID = ItemId.PESTLE_AND_MORTAR.getId();
  private final String[] options =
      new String[] {"Newts then Vials", "Vials then newts", "Vials", "Newts", "Pestle and mortar"};
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
        controller.displayMessage("Auto-starting, Newts then Vials", 0);
        System.out.println("Auto-starting, Newts then Vials");
        option = 2;
        scriptStarted = true;
        guiSetup = true;
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }

    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      controller.displayMessage("@red@TaverlyBuyer by Dvorak. Fixed by Kaila!");
      controller.displayMessage("@red@Start in Taverly or Fally West with GP!");
      controller.displayMessage("@red@This bot supports the \"autostart\" Parameter");
      controller.displayMessage("@red@autostart collects Newts then Vials");
      if (controller.isInBank()) controller.closeBank();
      if (controller.currentY() > 545) {
        bank();
        walkToTaverly();
        controller.sleep(1380);
      }
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {

    while (controller.isRunning()) {
      if (controller.getNeedToMove()) controller.moveCharacter();
      if (controller.getShouldSleep()) controller.sleepHandler(true);
      if (controller.getInventoryItemCount() < 30) {
        controller.setStatus("@gre@Buying stuff..");
        ORSCharacter npc = controller.getNearestNpcById(230, false);

        if (npc != null) {

          if (!controller.isInShop()) {
            if (controller.isAuthentic()) {
              controller.talkToNpc(npc.serverIndex);
              controller.sleep(2000);
              controller.optionAnswer(0);
              controller.sleep(1000);
            } else {
              controller.npcCommand1(npc.serverIndex);
              controller.sleep(1000);
            }
          }

          if (controller.getInventoryItemCount() < 30) {
            switch (option) {
              case 0: // newts then  vials
                if (controller.isInShop() && controller.getShopItemCount(NEWT_ID) > 0) {
                  controller.shopBuy(NEWT_ID, controller.getShopItemCount(NEWT_ID));
                  controller.sleep(100);
                }
                if (controller.isInShop() && controller.getShopItemCount(VIAL_ID) > 0) {
                  controller.shopBuy(VIAL_ID, controller.getShopItemCount(VIAL_ID));
                  controller.sleep(250);
                } else {
                  controller.sleep(250);
                }
                break;
              case 1: // vials then newts
                if (controller.isInShop() && controller.getShopItemCount(VIAL_ID) > 0) {
                  controller.shopBuy(VIAL_ID, controller.getShopItemCount(VIAL_ID));
                  controller.sleep(250);
                }
                if (controller.isInShop() && controller.getShopItemCount(NEWT_ID) > 0) {
                  controller.shopBuy(NEWT_ID, controller.getShopItemCount(NEWT_ID));
                  controller.sleep(250);
                } else {
                  controller.sleep(250);
                }
                break;
              case 2: // only vials
                if (controller.isInShop() && controller.getShopItemCount(VIAL_ID) > 0) {
                  controller.shopBuy(VIAL_ID, controller.getShopItemCount(VIAL_ID));
                  controller.sleep(250);
                } else {
                  controller.sleep(250);
                }
                break;
              case 3: // only newts
                if (controller.isInShop() && controller.getShopItemCount(NEWT_ID) > 0) {
                  controller.shopBuy(NEWT_ID, controller.getShopItemCount(NEWT_ID));
                  controller.sleep(250);
                } else {
                  controller.sleep(250);
                }
                break;
              case 4:
                if (controller.isInShop() && controller.getShopItemCount(PESTAL_ID) > 0) {
                  controller.shopBuy(PESTAL_ID, controller.getShopItemCount(PESTAL_ID));
                  controller.sleep(250);
                } else {
                  controller.sleep(250);
                }
                break;
              default:
                controller.log("Unknown option");
            }
          }
        }

      } else {
        walkToBank();
        bank();
        walkToTaverly();
      }

      controller.sleep(100);
    }
  }

  public void bank() {

    controller.setStatus("@yel@Banking..");
    controller.openBank();
    controller.sleep(640);

    vialsBought += controller.getInventoryItemCount(VIAL_ID);
    newtsBought += controller.getInventoryItemCount(NEWT_ID);
    pestalBought += controller.getInventoryItemCount(PESTAL_ID);

    if (controller.isInBank()) {

      for (int itemId : controller.getInventoryItemIds()) {
        if (itemId != ItemId.COINS.getId()) {
          controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
        }
      }
      vialsBanked = controller.getBankItemCount(VIAL_ID);
      newtsBanked = controller.getBankItemCount(NEWT_ID);
      pestalBanked = controller.getBankItemCount(PESTAL_ID);
      controller.sleep(100);
      controller.closeBank();
    }
  }

  public void walkToBank() {

    controller.setStatus("@gre@Walking to bank..");
    controller.walkTo(370, 506);

    // open shop door
    if (controller.getObjectAtCoord(371, 506) == 2) {
      controller.setStatus("@red@Opening shop door..");
      controller.atObject(371, 506);
      controller.sleep(1000);
    }

    controller.walkTo(371, 506);
    controller.walkTo(371, 499);
    controller.walkTo(365, 499);
    controller.walkTo(363, 497);
    controller.walkTo(357, 497);
    controller.walkTo(347, 497);
    controller.walkTo(342, 492);
    controller.walkTo(342, 488);

    controller.walkTo(342, 487);
    controller.setStatus("@red@Crossing Tav Gate..");
    K_kailaScript.tavGateWestToEast();
    controller.setStatus("@gre@Walking to Fally West..");

    controller.walkTo(341, 488);
    controller.walkTo(337, 492);
    controller.walkTo(337, 496);
    controller.walkTo(327, 506);
    controller.walkTo(317, 516);
    controller.walkTo(317, 523);
    controller.walkTo(324, 530);
    controller.walkTo(324, 539);
    controller.walkTo(324, 549);
    controller.walkTo(327, 552);

    // open bank door
    if (controller.getObjectAtCoord(327, 552) == 64) {
      controller.atObject(327, 552);
      controller.sleep(1000);
    }

    controller.walkTo(328, 553);
    controller.sleep(340);
    controller.setStatus("@red@Done Walking..");
  }

  public void walkToTaverly() {

    controller.setStatus("@gre@Walking back to Taverly..");
    controller.walkTo(328, 553);

    // open bank door
    if (controller.getObjectAtCoord(327, 552) == 64) {
      controller.atObject(327, 552);
      controller.sleep(1000);
    }

    controller.walkTo(327, 552);
    controller.walkTo(324, 549);
    controller.walkTo(324, 539);
    controller.walkTo(324, 530);
    controller.walkTo(317, 523);
    controller.walkTo(317, 516);
    controller.walkTo(327, 506);
    controller.walkTo(337, 496);
    controller.walkTo(337, 492);

    controller.walkTo(341, 488);
    controller.setStatus("@red@Crossing Tav Gate..");
    K_kailaScript.tavGateEastToWest(); // this should fix bug getting stuck on near gate
    // Open Tav gate, gate wont break if someone else opens it
    controller.setStatus("@gre@Walking to Shop..");
    controller.walkTo(342, 492);
    controller.walkTo(347, 497);
    controller.walkTo(357, 497);
    controller.walkTo(363, 497);
    controller.walkTo(365, 499);
    controller.walkTo(371, 499);
    controller.walkTo(371, 506);

    // open shop door
    if (controller.getObjectAtCoord(371, 506) == 2) {
      controller.setStatus("@red@Opening shop door..");
      controller.atObject(371, 506);
      controller.sleep(1000);
    }
    controller.setStatus("@red@Done Walking..");
  }

  public void setupGUI() {
    final JFrame scriptFrame = new JFrame("TaverlyBuyer by Dvorak. Fixed by Kaila");
    JLabel headerLabel = new JLabel("Buys Newts/Vials from Taverly");
    JLabel Label1 = new JLabel("Start in Taverly or Fally West with GP!");
    JLabel Label2 = new JLabel("This bot supports the \"autostart\" Parameter");
    JLabel Label3 = new JLabel("autostart collects Newts then Vials");
    JComboBox<String> optionField = new JComboBox<>(options);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          option = optionField.getSelectedIndex();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(headerLabel);
    scriptFrame.add(Label1);
    scriptFrame.add(Label2);
    scriptFrame.add(Label3);
    scriptFrame.add(optionField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int vialsPerHr = 0;
      int newtsPerHr = 0;
      int pestalPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        vialsPerHr = (int) (vialsBought * scale);
        newtsPerHr = (int) (newtsBought * scale);
        pestalPerHr = (int) (pestalBought * scale);
      } catch (Exception e) {
        // divide by zero
      }

      int height = 21 + 14 + 14;
      if (option == 2) {
        height += 14 + 14;
      }

      controller.drawBoxAlpha(7, 7, 180, height, 0xFFFFFF, 128);
      controller.drawString("@gre@TaverlyBuyer @whi@by @gre@Dvorak & Kaila", 10, 21, 0xFFFFFF, 1);
      // "Newts then Vials", "Vials then newts", "Vials", "Newts", "Pestle and mort
      if (option == 0 || option == 1) { // newts and vials
        controller.drawString(
            "@gre@Vials bought: @whi@"
                + String.format("%,d", vialsBought)
                + " @gre@(@whi@"
                + String.format("%,d", vialsPerHr)
                + "@gre@/@whi@hr@gre@)",
            10,
            21 + 14,
            0xFFFFFF,
            1);
        controller.drawString(
            "@gre@Vials in bank: @whi@" + String.format("%,d", vialsBanked),
            10,
            21 + 14 + 14,
            0xFFFFFF,
            1);
      } else if (option == 2) { // vials
        controller.drawString(
            "@gre@Vials bought: @whi@"
                + String.format("%,d", vialsBought)
                + " @gre@(@whi@"
                + String.format("%,d", vialsPerHr)
                + "@gre@/@whi@hr@gre@)",
            10,
            21 + 14,
            0xFFFFFF,
            1);
        controller.drawString(
            "@gre@Vials in bank: @whi@" + String.format("%,d", vialsBanked),
            10,
            21 + (14 * 2),
            0xFFFFFF,
            1);
      } else if (option == 3) { // newts
        controller.drawString(
            "@gre@Newts bought: @whi@"
                + String.format("%,d", newtsBought)
                + " @gre@(@whi@"
                + String.format("%,d", newtsPerHr)
                + "@gre@/@whi@hr@gre@)",
            10,
            21 + 14,
            0xFFFFFF,
            1);
        controller.drawString(
            "@gre@Newts in bank: @whi@" + String.format("%,d", newtsBanked),
            10,
            21 + (14 * 2),
            0xFFFFFF,
            1);
      } else { // pestal
        controller.drawString(
            "@gre@Pestals bought: @whi@"
                + String.format("%,d", pestalBought)
                + " @gre@(@whi@"
                + String.format("%,d", pestalPerHr)
                + "@gre@/@whi@hr@gre@)",
            10,
            21 + 14,
            0xFFFFFF,
            1);
        controller.drawString(
            "@gre@Pestals in bank: @whi@" + String.format("%,d", pestalBanked),
            10,
            21 + (14 * 2),
            0xFFFFFF,
            1);
      }
    }
  }
}
