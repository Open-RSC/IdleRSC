package scripting.idlescript;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * <b>Fast Bank Bury</b>
 *
 * <p>Selectable Bone id. start in bank with unnoted bones in bank. Will withdraw and bury bones.
 *
 * @see scripting.idlescript.K_kailaScript
 * @author Kaila
 */
public final class K_Bank_BuryBones extends K_kailaScript {
  private static int boneId = -1;
  private static int burySuccess = 0;
  private static final int[] boneIds = {
    ItemId.BONES.getId(),
    ItemId.BIG_BONES.getId(),
    ItemId.BAT_BONES.getId(),
    ItemId.DRAGON_BONES.getId()
  };
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
      guiSetup = false;
      scriptStarted = false;
      startTime = System.currentTimeMillis();
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
      bankBones = c.getBankItemCount(boneId);
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

  @Override
  public void serverMessageInterrupt(String message) {
    if (message.contains("You bury")) {
      burySuccess++;
    }
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int boneSuccessPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        boneSuccessPerHr = (int) (burySuccess * scale);
      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Fast Bone Bury @whi@~ @mag@Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Bones Buried: @gre@"
              + burySuccess
              + "@yel@ (@whi@"
              + String.format("%,d", (boneSuccessPerHr))
              + "@yel@/@whi@hr@yel@)",
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString("@whi@Bones in bank: @yel@" + bankBones, x, y + (14 * 2), 0xFFFFFF, 1);
      if (boneId == boneIds[0]) {
        c.drawString("@whi@Burying: @gre@Regular Bones", x, y + (14 * 3), 0xFFFFFF, 1);
      } else if (boneId == boneIds[1]) {
        c.drawString("@whi@Burying: @gre@Big Bones", x, y + (14 * 3), 0xFFFFFF, 1);
      } else if (boneId == boneIds[2]) {
        c.drawString("@whi@Burying: @gre@Bat Bones", x, y + (14 * 3), 0xFFFFFF, 1);
      } else if (boneId == boneIds[3]) {
        c.drawString("@whi@Burying: @gre@Dragon Bones", x, y + (14 * 3), 0xFFFFFF, 1);
      }
      c.drawString(
          "@whi@Time Remaining: " + c.timeToCompletion(burySuccess, bankBones, startTime),
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString("@whi@Runtime: " + runTime, x, y + (14 * 5), 0xFFFFFF, 1);
      c.drawString("@whi@________________________", x, y + (14 * 5) + 3, 0xFFFFFF, 1);
    }
  }
}
