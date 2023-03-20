package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import orsc.ORSCharacter;

/**
 * Buys vials or newts in Taverly, banks in Falador
 *
 * <p>This bot supports the autostart Parameter autostart collects Newts then Vials
 *
 * @author Dvorak, rewritten by Kaila
 */
public class TaverlyBuyer extends IdleScript {
  String[] options = new String[] {"Vials", "Newts", "Newts then Vials", "Vials then newts"};

  int[] loot = {465, 270};

  int option = -1;
  boolean scriptStarted = false;
  boolean guiSetup = false;

  int vialsBought = 0;
  int vialsBanked = 0;
  int newtsBought = 0;
  int newtsBanked = 0;

  long startTimestamp = System.currentTimeMillis() / 1000L;

  public void startSequence() {
    controller.displayMessage("@red@TaverlyBuyer by Dvorak. Fixed by Kaila!");
    controller.displayMessage("@red@Start in Taverly or Fally West with GP!");
    controller.displayMessage("@red@This bot supports the \"autostart\" Parameter");
    controller.displayMessage("@red@autostart collects Newts then Vials");
    if (controller.isInBank() == true) {
      controller.closeBank();
    }
    if (controller.currentY() > 545) {
      bank();
      walkToTaverly();
      controller.sleep(1380);
    }
  }

  public int start(String parameters[]) {
    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        controller.displayMessage("Auto-starting, Newts then Vials", 0);
        System.out.println("Auto-starting, Newts then Vials");
        option = 2;
        startSequence();
        scriptStart();
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }

    if (scriptStarted) {
      startSequence();
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {

    while (controller.isRunning()) {
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
            if (option == 0) { // only vials
              if (controller.isInShop() && controller.getShopItemCount(465) > 0) {
                controller.shopBuy(465, controller.getShopItemCount(465));
              } else {
                controller.sleep(250);
              }
            } else if (option == 1) { // only newts
              if (controller.isInShop() && controller.getShopItemCount(270) > 0) {
                controller.shopBuy(270, controller.getShopItemCount(270));
              } else {
                controller.sleep(250);
              }
            } else if (option == 2) { // newts then  vials
              if (controller.isInShop() && controller.getShopItemCount(270) > 0) {
                controller.shopBuy(270, controller.getShopItemCount(270));
              }
              if (controller.isInShop() && controller.getShopItemCount(465) > 0) {
                controller.shopBuy(465, controller.getShopItemCount(465));
                controller.sleep(250);
              } else {
                controller.sleep(250);
              }
            } else { // vials then newts
              if (controller.isInShop() && controller.getShopItemCount(465) > 0) {
                controller.shopBuy(465, controller.getShopItemCount(465));
                controller.sleep(250);
              }
              if (controller.isInShop() && controller.getShopItemCount(270) > 0) {
                controller.shopBuy(270, controller.getShopItemCount(270));
              } else {
                controller.sleep(250);
              }
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
    controller.sleep(340);

    // Open Tav gate, "while" gate wont break if someone else opens it
    while (controller.currentX() == 342
        && controller.currentY() < 490
        && controller.currentY() > 485) {
      controller.atObject(341, 487);
      controller.sleep(640);
    }

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

  public int countLoot() {
    int count = 0;
    for (int i = 0; i < loot.length; i++) {
      count += controller.getInventoryItemCount(loot[i]);
    }

    return count;
  }

  public void bank() {

    controller.setStatus("@yel@Banking..");
    controller.openBank();
    controller.sleep(640);

    vialsBought += controller.getInventoryItemCount(465);
    newtsBought += controller.getInventoryItemCount(270);

    if (controller.isInBank()) {

      while (countLoot() > 0) {
        for (int i = 0; i < loot.length; i++) {
          if (controller.getInventoryItemCount(loot[i]) > 0) {
            controller.depositItem(
                loot[i],
                controller.getInventoryItemCount(loot[i])); // /////////////////////////////
            controller.sleep(250);
          }
        }
      }
      vialsBanked = controller.getBankItemCount(465);
      newtsBanked = controller.getBankItemCount(270);
      controller.sleep(100);

      controller.closeBank();
      controller.sleep(640);
    }
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
    controller.sleep(340);

    // Open Tav gate, "while" gate wont break if someone else opens it
    while (controller.currentX() == 341
        && controller.currentY() < 489
        && controller.currentY() > 486) {
      controller.atObject(341, 487);
      controller.sleep(640);
    }

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
    controller.sleep(340);
    controller.setStatus("@red@Done Walking..");
  }

  public static void centerWindow(Window frame) {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
    frame.setLocation(x, y);
  }

  public void setupGUI() {
    final JFrame scriptFrame = new JFrame("TaverlyBuyer by Dvorak. Fixed by Kaila");
    JLabel headerLabel = new JLabel("Buys Newts/Vials from Taverly");
    JLabel Label1 = new JLabel("Start in Taverly or Fally West with GP!");
    JLabel Label2 = new JLabel("This bot supports the \"autostart\" Parameter");
    JLabel Label3 = new JLabel("autostart collects Newts then Vials");
    JComboBox<String> optionField = new JComboBox<String>(options);
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            option = optionField.getSelectedIndex();

            scriptFrame.setVisible(false);
            scriptFrame.dispose();
            scriptStarted = true;

            // controller.displayMessage("@red@AIOCooker by Dvorak. Let's party like it's 2004!");
          }
        });

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(headerLabel);
    scriptFrame.add(Label1);
    scriptFrame.add(Label2);
    scriptFrame.add(Label3);
    scriptFrame.add(optionField);
    scriptFrame.add(startScriptButton);

    centerWindow(scriptFrame);
    scriptFrame.setVisible(true);
    scriptFrame.pack();
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int vialsPerHr = 0;
      int newtsPerHr = 0;
      try {
        float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
        float scale = (60 * 60) / timeRan;
        vialsPerHr = (int) (vialsBought * scale);
        newtsPerHr = (int) (newtsBought * scale);
      } catch (Exception e) {
        // divide by zero
      }

      int height = 21 + 14 + 14;
      if (option == 2) {
        height += 14 + 14;
      }

      controller.drawBoxAlpha(7, 7, 180, height, 0xFFFFFF, 128);
      controller.drawString("@gre@TaverlyBuyer @whi@by @gre@Dvorak & Kaila", 10, 21, 0xFFFFFF, 1);

      if (option == 0) {
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
      } else if (option == 1) {
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
            21 + 14 + 14,
            0xFFFFFF,
            1);
      } else {
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
        controller.drawString(
            "@gre@Newts bought: @whi@"
                + String.format("%,d", newtsBought)
                + " @gre@(@whi@"
                + String.format("%,d", newtsPerHr)
                + "@gre@/@whi@hr@gre@)",
            10,
            21 + 14 + 14 + 14,
            0xFFFFFF,
            1);
        controller.drawString(
            "@gre@Newts in bank: @whi@" + String.format("%,d", newtsBanked),
            10,
            21 + 14 + 14 + 14 + 14,
            0xFFFFFF,
            1);
      }
    }
  }
}
