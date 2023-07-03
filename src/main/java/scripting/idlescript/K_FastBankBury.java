package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import orsc.ORSCharacter;

/**
 * Fast Bank Bury.
 *
 * <p>Selectable Bone id. start in bank with unnoted bones in bank. Will withdraw and bury bones.
 *
 * <p>@Author ~ Kaila
 */
/*
 * todo add gui and statistics.
 */
public final class K_FastBankBury extends K_kailaScript {
  private static int boneId = -1;
  private static final int[] boneIds = {
    20, // regular bones
    413, // big bones
    604, // bat bones
    814 // dragon bones
  };

  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      scriptStart();
    }

    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {
      if (c.getInventoryItemCount(boneId) < 1) {
        if (!c.isInBank()) {
          int[] bankerIds = {95, 224, 268, 540, 617, 792};
          ORSCharacter npc = c.getNearestNpcByIds(bankerIds, false);
          if (npc != null) {
            c.setStatus("@yel@Walking to Banker..");
            c.displayMessage("@yel@Walking to Banker..");
            c.walktoNPCAsync(npc.serverIndex);
            c.sleep(200);
          } else {
            c.log("@red@Error..");
            c.sleep(1000);
          }
        }
        c.setStatus("@yel@Banking..");
        c.displayMessage("@gre@Banking..");
        bank();
        c.sleep(1200);
      }
      if (c.getInventoryItemCount(boneId) > 0) {
        c.setStatus("@yel@Burying..");
        c.itemCommand(boneId);
        c.sleep(100);
      }
    }

    //	return 1000; //start() must return an int value now.
  }

  private void bank() {

    c.setStatus("@yel@Banking..");
    c.openBank();
    c.sleep(640);
    if (!c.isInBank()) {
      waitForBankOpen();
    } else {
      if (c.getInventoryItemCount(boneId) < 30) {
        c.withdrawItem(boneId, 30);
      }
      c.closeBank();
    }
  }

  private void setupGUI() {
    JLabel header = new JLabel("Fast Bone Bury");
    JLabel boneLabel = new JLabel("bone Type:");
    JComboBox<String> boneField =
        new JComboBox<>(new String[] {"Normal Bones", "Big Bones", "Bat Bones", "Dragon Bones"});
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          boneId = boneIds[boneField.getSelectedIndex()];
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
          c.displayMessage("@gre@" + '"' + "Fast Bone Bury" + '"' + " - by Kaila");
          c.displayMessage("@gre@Start in any bank");
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(boneLabel);
    scriptFrame.add(boneField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }
}
