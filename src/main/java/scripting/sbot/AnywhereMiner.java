package scripting.sbot;

import compatibility.sbot.Script;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Objects;
import javax.swing.*;

public class AnywhereMiner extends Script implements ActionListener {

  JFrame fmeSettings;
  JPanel pnlSettings;
  JLabel lblMine;
  JLabel lblOre;
  JLabel lblFatigue;
  JComboBox cmbMineType;
  JComboBox cmbOreType;
  JTextField txtFatigue;
  JButton cmdStart;
  JButton cmdCancel;

  final int copper_rock_id = 100;
  final int iron_rock_id = 102;
  final int tin_rock_id = 104;
  final int mithril_rock_id = 106;
  final int adamantite_rock_id = 108;
  final int coal_rock_id = 110;
  final int gold_rock_id = 112;
  final int clay_rock_id = 114;
  final int silver_rock_id = 196;

  final int banker_id = 95;
  int fat_level = 95;
  final int sleepingbag_id = 1263;

  final int copper_ore = 150;
  final int iron_ore = 151;
  final int tin_ore = 202;
  final int gold = 152;
  final int silver = 383;
  final int mithril_ore = 153;
  final int coal = 155;
  final int adamantite_ore = 154;

  final int uncut_diamond = 157;
  final int uncut_ruby = 158;
  final int uncut_emerald = 159;
  final int uncut_sapphire = 160;

  final int[] ores_gems =
      new int[] {
        copper_ore,
        iron_ore,
        tin_ore,
        gold,
        silver,
        mithril_ore,
        coal,
        adamantite_ore,
        uncut_diamond,
        uncut_ruby,
        uncut_emerald,
        uncut_sapphire
      };

  boolean bank_ore = false;
  boolean run_script = false;

  public String[] getCommands() {
    return new String[] {"anywhereminer"};
  }

  public void start(String cmd, String[] params) {
    Msg("@red@Anywhere @whi@Miner + Banker - by @ran@e@ran@X@ran@e@ran@m@ran@p@ran@l@ran@a@ran@r");
    javax.swing.SwingUtilities.invokeLater(() -> LoadDialog());
    while (!run_script && Running()) Wait(100);
    if (run_script) {
      Msg("Running mining script..");
      RunScipt();
      Msg("Mining script stopped.");
    }
  }

  public void Msg(String msg) {
    if (Running()) {
      DisplayMessage("@gre@SBoT: @whi@" + msg, 3);
      Println("SBoT: " + msg.replaceAll("@...@", ""));
    }
  }

  public void LoadDialog() {
    JFrame.setDefaultLookAndFeelDecorated(true);
    fmeSettings = new JFrame("Mine & Bank, Anywhere!");
    pnlSettings = new JPanel(new GridLayout(4, 2));
    lblMine = new JLabel("Mine type:");
    lblOre = new JLabel("Ore type:");
    lblFatigue = new JLabel("Fatigue level:");
    cmbMineType = new JComboBox(new String[] {"PowerMine", "Mine+Bank"});
    cmbOreType =
        new JComboBox(
            new String[] {
              "Clay", "Tin", "Copper", "Iron", "Coal", "Mithril", "Adamantite", "Gold", "Silver"
            });
    txtFatigue = new JTextField("95");
    cmdCancel = new JButton("Cancel");
    cmdStart = new JButton("Start!");
    fmeSettings.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    fmeSettings.setResizable(false);
    cmdCancel.addActionListener(this);
    cmdStart.addActionListener(this);
    pnlSettings.add(lblMine);
    pnlSettings.add(cmbMineType);
    pnlSettings.add(lblOre);
    pnlSettings.add(cmbOreType);
    pnlSettings.add(lblFatigue);
    pnlSettings.add(txtFatigue);
    pnlSettings.add(cmdCancel);
    pnlSettings.add(cmdStart);
    fmeSettings.getRootPane().setDefaultButton(cmdStart);
    fmeSettings.getContentPane().add(pnlSettings, BorderLayout.CENTER);
    fmeSettings.pack();
    fmeSettings.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    if (Objects.equals(e.getActionCommand(), "Start!")) {
      run_script = true;
    } else {
      fmeSettings.dispose();
    }
  }

  public void RunScipt() {
    fmeSettings.dispose();
    int rock_id = 0;
    switch (cmbMineType.getSelectedIndex()) {
      case 0:
        break;
      case 1:
        bank_ore = true;
        break;
    }
    switch (cmbOreType.getSelectedIndex()) {
      case 0:
        rock_id = clay_rock_id;
        break;
      case 1:
        rock_id = tin_rock_id;
        break;
      case 2:
        rock_id = copper_rock_id;
        break;
      case 3:
        rock_id = iron_rock_id;
        break;
      case 4:
        rock_id = coal_rock_id;
        break;
      case 5:
        rock_id = mithril_rock_id;
        break;
      case 6:
        rock_id = adamantite_rock_id;
        break;
      case 7:
        rock_id = gold_rock_id;
        break;
      case 8:
        rock_id = silver_rock_id;
        break;
    }
    Msg(
        "Mining "
            + cmbOreType.getSelectedItem()
            + ", in "
            + cmbMineType.getSelectedItem()
            + " mode.");
    fat_level = Integer.parseInt(txtFatigue.getText());
    if (fat_level > 95 || fat_level < 5) fat_level = 95;
    fmeSettings.dispose();
    while (Running()) {
      if (GetNearestObject(rock_id)[0] > 0) {
        CheckSleep();
        AtObject(GetNearestObject(rock_id));
        Wait(Rand(500, 1500));
      }
      if (bank_ore && InvCount() == 30 && Running()) {
        Msg("Inventory full,");
        WalkToBank(1);
        DoBanking();
        WalkToBank(0);
        Msg("Mining...");
      }
    }
  }

  public void DoBanking() {
    Msg("Depositing ores & gems...");
    if (GetNearestNPC(banker_id) < 0) {
      Msg("Cannot find banker");
      bank_ore = false;
      return;
    }
    WaitForNPCMessage(banker_id, 10, "Good day,");
    WaitForQuestMenu();
    Answer(0);
    while (!Bank() && Running()) Wait(100);
    if (Bank() && Running()) {
      for (int i = 0; i < ores_gems.length - 1; i++) {
        if (InvCount(ores_gems[i]) > 0) {
          while (InvCount(ores_gems[i]) > 0) {
            Deposit(ores_gems[i], 1);
            Wait(Rand(100, 200));
          }
        }
      }
    }
    Wait(100);
    CloseBank();
  }

  public void WalkToBank(int way) {
    String fName = "";
    if (way == 1) {
      Msg("Walking to bank...");
      fName = "walktobank.txt";
    } else {
      Msg("Walking from bank to ores...");
      fName = "walkfrombank.txt";
    }
    try {
      File sFile = new File(fName);
      if (!sFile.exists()) {
        Msg("Error: Cannot find " + fName);
        bank_ore = false;
        return;
      }
      FileInputStream fStream = new FileInputStream(fName);
      BufferedReader in = new BufferedReader(new InputStreamReader(fStream));
      String cmd;
      while (in.ready() && Running()) {
        cmd = in.readLine();
        ParseCommand(cmd);
      }
      in.close();
    } catch (Exception e) {
      Print("Error: " + e.getMessage());
    }
  }

  public void ParseCommand(String cmd) {
    if (!cmd.contains("(") && !cmd.contains(")")) {
      Msg("Invalid command: " + cmd);
      return;
    }
    String dothing = cmd.substring(0, cmd.indexOf("("));
    String[] params = cmd.substring(cmd.indexOf("(") + 1, cmd.indexOf(")")).split(",");
    if (dothing.equalsIgnoreCase("Walk")) {
      Println("Walking to " + params[0] + "," + params[1]);
      ForceWalk(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
      Wait(100);
    }
    if (dothing.equalsIgnoreCase("OpenBankDoor")) {
      Println("Opening bank door at " + params[0] + "," + params[1]);
      while (ObjectAt(Integer.parseInt(params[0]), Integer.parseInt(params[1])) == 64
          && Running()) {
        AtObject(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
        Wait(200);
      }
    }
    if (dothing.equalsIgnoreCase("OpenDoor")) {
      Println("Opening door at " + params[0] + "," + params[1] + "," + params[2]);
      while (DoorAt(
                  Integer.parseInt(params[0]),
                  Integer.parseInt(params[1]),
                  Integer.parseInt(params[2]))
              != 2
          && Running()) {
        OpenDoor(
            Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
        Wait(200);
      }
    }
  }

  public void CheckSleep() {
    if (Fatigue() >= fat_level && Running()) {
      Msg("Sleeping...");
      while (!Sleeping() && Running()) {
        Use(FindInv(sleepingbag_id));
        long Time = TickCount();
        while (!Sleeping() && TickCount() - Time < 3000 && Running()) Wait(100);
      }
      Msg("Waiting for word...");
      while (Sleeping() && Running()) {
        Beep();
        Wait(1000);
      }
      Msg("Finished sleeping");
    }
  }

  public void WaitForQuestMenu() {
    while (!QuestMenu() && Running()) Wait(100);
  }

  public void WaitForNPCMessage(int type, int time, String message) {
    ResetLastNPCMessage();
    while (!LastNPCMessage().contains(message)) {
      ResetLastServerMessage();
      ResetLastNPCMessage();
      int id = GetNearestNPC(type);
      long T = TickCount();
      TalkToNPC(id);
      while (TickCount() - T < 8000
          && !LastServerMessage().contains("busy")
          && Objects.equals(LastNPCMessage(), "")) Wait(100);
      if (!LastServerMessage().contains("busy")) {
        T = TickCount();
        if (LastNPCMessage() != null)
          while (!LastNPCMessage().contains(message) && TickCount() - T < (time * 1000L)) Wait(100);
      }
      Wait(1000);
    }
  }

  public void ChatMessage(String message) {
    if (Running()) {
      Msg("Anti-Mod protection activated,");
      Msg("Chat recieved: " + message);
      Quit();
    }
  }

  public void TradeRequest(int PlayerID) {
    if (Running()) {
      Msg("Anti-Mod protection activated,");
      Msg("Trade recieved from: " + PlayerID);
      Quit();
    }
  }
}
