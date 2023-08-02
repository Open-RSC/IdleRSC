package scripting.idlescript;

import java.awt.GridLayout;
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
 * @author Dvorak, rewritten by Kaila and Spilk
 */
public class S_AggieBuyer extends IdleScript {
  final String[] options = new String[] {"Newts"};

  final int[] loot = {465, 270};

  int option = -1;
  boolean scriptStarted = false;
  boolean guiSetup = false;

  int vialsBought = 0;
  int vialsBanked = 0;
  int newtsBought = 0;
  int newtsBanked = 0;

  final long startTimestamp = System.currentTimeMillis() / 1000L;

  public void startSequence() {
    controller.displayMessage("@red@AggieBuyer by Dvorak. Fixed by Kaila!");
    controller.displayMessage("@red@Start in Port Sarim or Fally East with GP!");
    controller.displayMessage("@red@This bot supports the \"autostart\" Parameter");
    controller.displayMessage("@red@autostart collects Newts then Vials");
    if (controller.isInBank()) {
      controller.closeBank();
    }
    if (controller.currentY() > 545) {
      bank();
      walkToAggie();
      controller.sleep(1380);
    }
  }

  public int start(String[] parameters) {
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
      guiSetup = false;
      scriptStarted = false;
      startSequence();
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {

    while (controller.isRunning()) {
      if (controller.getInventoryItemCount() < 30) {
        controller.setStatus("@gre@Buying stuff..");
        // changed from 230 to 149 (aggie)
        ORSCharacter npc = controller.getNearestNpcById(149, false);

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
            if (option == 0) { // only newts
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
        walkToAggie();
      }

      controller.sleep(100);
    }
  }

  public void walkToBank() {

    controller.setStatus("@gre@Walking to bank..");
    controller.walkTo(272, 632);

    // open shop door
    // old shop door was 371, 506 (end point 370, 506)
    if (controller.getObjectAtCoord(271, 632) == 2) {
      controller.setStatus("@red@Opening shop door..");
      controller.atObject(271, 632);
      controller.sleep(1000);
    }

    controller.walkTo(271, 632);
    controller.walkTo(274, 619);
    controller.walkTo(285, 610);
    controller.walkTo(289, 592);
    controller.walkTo(289, 577);
    controller.walkTo(287, 571);
    /*
    controller.walkTo(371, 499);
    controller.walkTo(365, 499);
    controller.walkTo(363, 497);
    controller.walkTo(357, 497);
    controller.walkTo(347, 497);
    controller.walkTo(342, 492);
    controller.walkTo(342, 488);

    controller.sleep(340);
    */
    // Open Tav gate, "while" gate wont break if someone else opens it
    /*
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
    */
    // open bank door
    if (controller.getObjectAtCoord(287, 571) == 64) {
      controller.atObject(287, 571);
      controller.sleep(1000);
    }

    controller.walkTo(285, 571);
    controller.sleep(340);
    controller.setStatus("@red@Done Walking..");
  }

  public int countLoot() {
    int count = 0;
    for (int j : loot) {
      count += controller.getInventoryItemCount(j);
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
        for (int j : loot) {
          if (controller.getInventoryItemCount(j) > 0) {
            controller.depositItem(
                j, controller.getInventoryItemCount(j)); // /////////////////////////////
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

  public void walkToAggie() {

    controller.setStatus("@gre@Walking back to Aggie..");
    controller.walkTo(286, 571);

    // open bank door
    if (controller.getObjectAtCoord(287, 571) == 64) {
      controller.atObject(287, 571);
      controller.sleep(1000);
    }

    controller.walkTo(287, 571);
    controller.walkTo(289, 577);
    controller.walkTo(289, 592);
    controller.walkTo(285, 610);
    controller.walkTo(274, 619);
    controller.walkTo(271, 632);

    /*
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
    */
    // open shop door
    if (controller.getObjectAtCoord(271, 632) == 2) {
      controller.setStatus("@red@Opening shop door..");
      controller.atObject(271, 632);
      controller.sleep(1000);
    }
    controller.sleep(340);
    controller.setStatus("@red@Done Walking..");
  }

  public void setupGUI() {
    final JFrame scriptFrame = new JFrame("AggieBuyer by Dvorak. Fixed by Kaila");
    JLabel headerLabel = new JLabel("Buys Newtsfrom Aggie");
    JLabel Label1 = new JLabel("Start in Port Sarim or Fally West with GP!");
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

          // controller.displayMessage("@red@AIOCooker by Dvorak. Let's party like it's 2004!");
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
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int vialsPerHr = 0;
      int newtsPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
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
      controller.drawString(
          "@gre@AggieBuyer @whi@by @gre@Dvorak & Kaila & spilk", 10, 21, 0xFFFFFF, 1);

      if (option == 1) {
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
      } else if (option == 0) {
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
