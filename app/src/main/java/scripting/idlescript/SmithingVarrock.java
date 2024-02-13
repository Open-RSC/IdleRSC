package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * SmithingVarrock by Searos
 *
 * @author Searos Fixed by Kaila
 */
public class SmithingVarrock extends IdleScript {
  private static final Controller c = Main.getController();
  private JFrame scriptFrame = null;
  private boolean guiSetup = false;
  private boolean scriptStarted = false;
  private int barId = -1;
  private int ans1 = -1;
  private int ans2 = -1;
  private int ans3 = -1;
  private int ans4 = -1;
  private int barsLeft = -1;
  private int totalSmithed = 0;
  private final int[] barIds = {169, 170, 171, 173, 174, 408};
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }

    if (scriptStarted) {
      if (c.isInBank()) c.closeBank();
      guiSetup = false;
      scriptStarted = false;
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (c.getInventoryItemCount(barId) < 5 && !c.isInBank()) {
        c.setStatus("@gre@Banking..");
        c.walkTo(150, 507);
        banking();
        c.walkTo(150, 507);
        c.walkTo(149, 512);
        c.walkTo(148, 512);
      }
      if (c.getInventoryItemCount(barId) > 4) {
        if (controller.getShouldSleep()) controller.sleepHandler(true);
        c.setStatus("@gre@Smithing..");
        c.useItemIdOnObject(148, 513, barId);
        c.sleep(1000); // increased sleep time to fix menuing bug
        c.optionAnswer(ans1);
        c.sleep(600);
        c.optionAnswer(ans2);
        c.sleep(600);
        c.optionAnswer(ans3);
        c.sleep(600);
        if (!c.isAuthentic()) {
          c.optionAnswer(ans4);
          c.sleep(1000);
          c.waitForBatching(false);
        }
      }
      // c.sleep(320);
    }
    scriptStarted = false;
    guiSetup = false;
  }

  private void banking() {
    c.setStatus("@red@Banking");
    c.openBank();
    c.sleep(1000);

    if (c.isInBank()) {

      if (c.getBankItemCount(barId)
          < 30) { // stops making when 30 in bank to not mess up alignments/organization of bank!!!
        c.setStatus("@red@NO Bars in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
          return;
        }
      }
      if (c.getInventoryItemCount() > 1) {
        for (int itemId : c.getInventoryItemIds()) {
          if (itemId != 168 && itemId != 1263 && itemId != barId) {
            totalSmithed = totalSmithed + c.getInventoryItemCount(itemId);
            c.depositItem(itemId, c.getInventoryItemCount(itemId));
          }
        }
        c.sleep(320);
      }
      if (c.getInventoryItemCount(168) < 1) {
        c.withdrawItem(168, 1);
        c.sleep(320); // added sleep here
      }
      if (c.getInventoryItemCount(barId) < 28) {
        c.withdrawItem(barId, 29);
        c.sleep(320);
      }
      barsLeft = c.getBankItemCount(barId);
      c.closeBank();
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Smithing");
    JLabel hammerLabel = new JLabel("Start with Hammer! & Sleeping Bag if on Uranium ");
    JLabel batchLabel = new JLabel("Batch Bars MUST be toggled ON in settings!!!");
    JLabel batchLabel2 = new JLabel("This ensures All bars are Smithed per 1 Menu Cycle.");
    JLabel barLabel = new JLabel("Bar Type:");
    JComboBox<String> barField =
        new JComboBox<>(
            new String[] {"Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite"});
    JLabel ans1Label = new JLabel("Item Type:");
    JComboBox<String> ans1Field =
        new JComboBox<>(new String[] {"Weapon", "Armour", "Missile Heads"});
    JLabel ans2Label = new JLabel("Weapon Type");
    JComboBox<String> ans2Field =
        new JComboBox<>(new String[] {"Dagger", "Throwing Knife", "Sword", "Axe", "Mace"});
    JLabel ans3Label = new JLabel("How many per Options Menu");
    JComboBox<String> ans3Field = new JComboBox<>(new String[] {"1", "5", "10", "all"});
    ans3Field.setSelectedIndex(3);
    JLabel ans4Label = new JLabel("Null");
    JComboBox<String> ans4Field = new JComboBox<>(new String[] {"Null"});
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          ans1 = ans1Field.getSelectedIndex();
          ans2 = ans2Field.getSelectedIndex();
          ans3 = ans3Field.getSelectedIndex();
          if (ans3 > 0) c.setBatchBarsOn();
          ans4 = ans4Field.getSelectedIndex();
          barId = barIds[barField.getSelectedIndex()];
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          c.displayMessage("@gre@" + '"' + "heh" + '"' + " - Searos");
          c.displayMessage("@red@Smithing started @ran@ It's HAMMERTIME");
        });

    ans1Field.addActionListener(
        e -> {
          if (ans1Field.getSelectedIndex() == 0) {
            ans2Label.setText("Weapon Type");
            ans2Field.setModel(
                new JComboBox<>(new String[] {"Dagger", "Throwing Knife", "Sword", "Axe", "Mace"})
                    .getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          if (ans1Field.getSelectedIndex() == 1) {
            ans2Label.setText("Armour Type, select to update options below");
            ans2Field.setModel(
                new JComboBox<>(new String[] {"Helmet", "Shield", "Armour"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          if (ans1Field.getSelectedIndex() == 2) {
            ans2Label.setText("Missile Type");
            ans2Field.setModel(new JComboBox<>(new String[] {"Arrowheads"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          ans1 = ans1Field.getSelectedIndex();
        });
    ans2Field.addActionListener(
        e -> {
          if (ans2Field.getSelectedIndex() == 0 && ans1Field.getSelectedIndex() == 0) {
            ans3Label.setText("How many per Options Menu");
            ans3Field.setModel(new JComboBox<>(new String[] {"1", "5", "10", "all"}).getModel());
            ans4Label.setText("Null");
            ans4Field.setModel(new JComboBox<>(new String[] {"Null"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          if (ans2Field.getSelectedIndex() == 1 && ans1Field.getSelectedIndex() == 0) {
            ans3Label.setText("How many per Options Menu");
            ans3Field.setModel(new JComboBox<>(new String[] {"1", "5", "10", "all"}).getModel());
            ans4Label.setText("Null");
            ans4Field.setModel(new JComboBox<>(new String[] {"Null"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          if (ans2Field.getSelectedIndex() == 2 && ans1Field.getSelectedIndex() == 0) {
            ans3Label.setText("Sword Type");
            ans3Field.setModel(
                new JComboBox<>(new String[] {"Short", "Long", "Scimitar", "2h"}).getModel());
            ans4Label.setText("How many per Options Menu");
            ans4Field.setModel(new JComboBox<>(new String[] {"1", "5", "10", "all"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          if (ans2Field.getSelectedIndex() == 3 && ans1Field.getSelectedIndex() == 0) {
            ans3Label.setText("Axe Type");
            ans3Field.setModel(new JComboBox<>(new String[] {"Hatchet", "Battle"}).getModel());
            ans4Label.setText("How many per Options Menu");
            ans4Field.setModel(new JComboBox<>(new String[] {"1", "5", "10", "all"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          if (ans2Field.getSelectedIndex() == 4 && ans1Field.getSelectedIndex() == 0) {
            ans3Label.setText("How many per Options Menu");
            ans3Field.setModel(new JComboBox<>(new String[] {"1", "5", "10", "all"}).getModel());
            ans4Label.setText("Null");
            ans4Field.setModel(new JComboBox<>(new String[] {"Null"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          if (ans2Field.getSelectedIndex() == 0 && ans1Field.getSelectedIndex() == 1) {
            ans3Label.setText("Helmet Type");
            ans3Field.setModel(new JComboBox<>(new String[] {"Medium", "Large"}).getModel());
            ans4Label.setText("How many per Options Menu");
            ans4Field.setModel(new JComboBox<>(new String[] {"1", "5", "10", "all"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          if (ans2Field.getSelectedIndex() == 1 && ans1Field.getSelectedIndex() == 1) {
            ans3Label.setText("Shield Type");
            ans3Field.setModel(new JComboBox<>(new String[] {"Square", "Kite"}).getModel());
            ans4Label.setText("How many per Options Menu");
            ans4Field.setModel(new JComboBox<>(new String[] {"1", "5", "10", "all"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          if (ans2Field.getSelectedIndex() == 2 && ans1Field.getSelectedIndex() == 1) {
            ans3Label.setText("Armour Type");
            if (c.isAuthentic()) {
              ans3Field.setModel(
                  new JComboBox<>(
                          new String[] {"Chain Body", "Plate Body", "Plate Legs", "Plate Skirt"})
                      .getModel());
            } else {
              ans3Field.setModel(
                  new JComboBox<>(
                          new String[] {
                            "Chain Legs", "Chain Body", "Plate Body", "Plate Legs", "Plate Skirt"
                          })
                      .getModel());
            }
            ans4Label.setText("How many per Options Menu");
            ans4Field.setModel(new JComboBox<>(new String[] {"1", "5", "10", "all"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          if (ans2Field.getSelectedIndex() == 0 && ans1Field.getSelectedIndex() == 2) {
            ans3Label.setText("How many per Options Menu");
            ans3Field.setModel(new JComboBox<>(new String[] {"1", "5", "10", "all"}).getModel());
            ans4Label.setText("Null");
            ans4Field.setModel(new JComboBox<>(new String[] {"Null"}).getModel());
            scriptFrame.setVisible(false);
            scriptFrame.setVisible(true);
          }
          ans2 = ans2Field.getSelectedIndex();
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(hammerLabel);
    scriptFrame.add(batchLabel);
    scriptFrame.add(batchLabel2);
    scriptFrame.add(barLabel);
    scriptFrame.add(barField);
    scriptFrame.add(ans1Label);
    scriptFrame.add(ans1Field);
    scriptFrame.add(ans2Label);
    scriptFrame.add(ans2Field);
    scriptFrame.add(ans3Label);
    scriptFrame.add(ans3Field);
    scriptFrame.add(ans4Label);
    scriptFrame.add(ans4Field);
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
      c.drawBoxAlpha(7, 7, 142, 21 + 14 + 14 + 14, 0xFF0000, 64);
      c.drawString("@red@Smithing Varrock", 10, 21, 0xFFFFFF, 1);
      c.drawString("@gre@by Searos, fixed by Kaila", 10, 21 + 14, 0xFFFFFF, 1);
      c.drawString("@red@Bars Smithed: @yel@" + totalSmithed, 10, 21 + 14 + 14, 0xFFFFFF, 1);
      c.drawString("@red@Bars left in Bank: @yel@" + barsLeft, 10, 21 + 14 + 14 + 14, 0xFFFFFF, 1);
    }
  }
}
