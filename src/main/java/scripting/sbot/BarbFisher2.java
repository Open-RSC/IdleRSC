package scripting.sbot;

import compatibility.sbot.Script;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BarbFisher2 extends Script implements ActionListener {
  Thread reportThread;
  Graphics g;
  public long time = System.currentTimeMillis(), starttime, minutes;
  public boolean run_script = false, Running = true;
  public int fishes = 0,
      slept = 0,
      startexp = 0,
      expgained = 0,
      fLevels = 0,
      sLevel = 0,
      XFire = 0,
      YFire = 0,
      DepId1 = 0,
      DepId2 = 0;
  public String[] preferences = new String[2];
  public String fishmode = "dunno";
  public String ctime, bankmode, cMode = " doing nothing";
  JFrame fishFrame, reportFrame;
  JPanel fishPanel, reportPanel;
  JLabel fishModeLabel, fishLabel, emptylabel1, pMode, pMins, pExp, pFished, pLevels, pSlept, empty;
  JButton save;
  JComboBox fishMode, bankMode;

  public String[] getCommands() {
    return new String[] {"barbfish"};
  }

  public void ServerMessage(String message) {
    if (message.contains("You catch a ")) fishes++;
  }

  public void start(String command, String[] parameter) {
    javax.swing.SwingUtilities.invokeLater(() -> addWidgets());
    while (!run_script && Running()) Wait(100);
    if (run_script) RunScipt();
  }

  public void WalkFromFireBarbVill() {
    ForceWalk(232, 502);
    Wait(100);
    while (ObjectAt(232, 503) == 64 && Running()) {
      cMode = " Opening door";
      showReport();
      AtObject(232, 503);
      Wait(2000);
    }
    cMode = " Walking back to fishing spot";
    showReport();
    if (Running()) ForceWalk(226, 503);
    if (Running()) ForceWalk(214, 501);
    if (Running()) ForceWalk(212, 501);
    Wait(100);
  }

  public void EatFish() {
    while (InvCount(357) > 0 && Running() && !Sleeping()) {
      Use(FindInv(357));
      Wait(100);
    }
    while (InvCount(359) > 0 && Running() && !Sleeping()) {
      Use(FindInv(359));
      Wait(100);
    }
  }

  public void WalkFromRangeBank() {
    while (GetY() > 1000 && Running()) {
      cMode = " climbing down ladder";
      showReport();
      AtObject(226, 1383);
      Wait(1000);
    }
    while (DoorAt(225, 444, 0) == 2 && Running()) {
      cMode = " Opening door";
      showReport();
      OpenDoor(225, 444, 0);
      Wait(2000);
    }
    cMode = " Walking to bank";
    showReport();
    if (Running()) ForceWalk(225, 445);
    if (Running()) ForceWalk(217, 446);
    Wait(100);
    while (ObjectAt(217, 447) != 63 && Running()) {
      cMode = " Opening bank door";
      showReport();
      AtObject(217, 447);
      Wait(1000);
    }
  }

  public void CookFish() {
    while (InvCount(356) > 0 && Running() && !Sleeping()) {
      cMode = " Cooking fish";
      showReport();
      if (Fatigue() <= 95 && Running()) {
        UseOnObject(XFire, YFire, FindInv(356));
        Wait(2600);
      }

      if (Fatigue() >= 95 && Running()) {
        Sleeptime();
        Wait(50);
      }
    }
    while (InvCount(358) > 0 && Running() && !Sleeping()) {
      cMode = " Cooking fish";
      showReport();
      UseOnObject(XFire, YFire, FindInv(358));
      Wait(2600);
      if (Fatigue() >= 95 && Running()) {
        Sleeptime();
        Wait(50);
      }
    }
  }

  public void WalkToRange() {
    cMode = " Walking to Range";
    showReport();
    if (Running()) ForceWalk(211, 501);
    if (Running()) ForceWalk(213, 497);
    if (Running()) ForceWalk(215, 493);
    if (Running()) ForceWalk(217, 488);
    if (Running()) ForceWalk(218, 483);
    if (Running()) ForceWalk(220, 478);
    if (Running()) ForceWalk(221, 470);
    if (Running()) ForceWalk(224, 466);
    if (Running()) ForceWalk(224, 458);
    if (Running()) ForceWalk(222, 449);
    if (Running()) ForceWalk(225, 445);
    if (Running())
      while (DoorAt(225, 444, 0) == 2 && Running()) {
        cMode = " Opening door";
        showReport();
        OpenDoor(225, 444, 0);
        Wait(2000);
      }
    if (Running()) ForceWalk(225, 441);
    Wait(100);
    while (GetY() < 1000 && Running()) {
      cMode = "Climging up ladder";
      showReport();
      AtObject(226, 439);
      Wait(1000);
    }
  }

  public void WalkFireBarbVill() {
    cMode = " Walking to fire";
    showReport();
    if (Running()) ForceWalk(213, 502);
    if (Running()) ForceWalk(221, 502);
    if (Running()) ForceWalk(225, 503);
    if (Running()) ForceWalk(232, 504);
    Wait(100);
    while (ObjectAt(232, 503) == 64 && Running()) {
      cMode = " Opening door";
      showReport();
      AtObject(232, 503);
      Wait(2000);
    }
    if (Running()) ForceWalk(235, 496);
    Wait(200);
  }

  public void DropBurnt() {
    while (InvCount(360) > 0 && Running()) {
      Drop(FindInv(360));
      Wait(1500);
      cMode = " dropping burnt fish";
      showReport();
    }
  }

  public void WalkFromBank() {
    while (ObjectAt(217, 447) != 63 && Running()) {
      cMode = " Opening bank door";
      showReport();
      AtObject(217, 447);
      Wait(1000);
    }
    cMode = " walking back to fishing spot";
    showReport();
    if (Running()) ForceWalk(220, 446);
    if (Running()) ForceWalk(222, 455);
    if (Running()) ForceWalk(224, 463);
    if (Running()) ForceWalk(223, 471);
    if (Running()) ForceWalk(221, 480);
    if (Running()) ForceWalk(217, 492);
    if (Running()) ForceWalk(211, 500);
    Wait(100);
  }

  public void TalkBanker() {
    while (!Bank() && Running()) {
      while (!QuestMenu() && Running()) {
        cMode = " Talking to banker";
        showReport();
        int BankerID = GetNearestNPC(95);
        TalkToNPC(BankerID);
        long Time = System.currentTimeMillis();
        while (System.currentTimeMillis() - Time <= 2000 && !QuestMenu() && Running()) Wait(3001);
      }
      Answer(0);
      long Time = System.currentTimeMillis();
      while (System.currentTimeMillis() - Time <= 5000 && !Bank() && Running()) Wait(3001);
    }

    while (InvCount(DepId1) > 0 && Running() && Bank()) {
      cMode = " Depositing fish";
      showReport();
      Deposit(DepId1, 1);
      Wait(100);
    }

    while (InvCount(DepId2) > 0 && Running() && Bank()) {
      cMode = " Depositing fish";
      showReport();
      Deposit(DepId2, 1);
      Wait(100);
    }
    ForceWalk(217, 448);
    Wait(100);
  }

  public void WalkBankNoCook() {
    cMode = " Walking to Bank";
    showReport();
    if (Running()) ForceWalk(211, 501);
    if (Running()) ForceWalk(213, 497);
    if (Running()) ForceWalk(215, 493);
    if (Running()) ForceWalk(217, 488);
    if (Running()) ForceWalk(218, 483);
    if (Running()) ForceWalk(220, 478);
    if (Running()) ForceWalk(221, 470);
    if (Running()) ForceWalk(224, 466);
    if (Running()) ForceWalk(224, 458);
    if (Running()) ForceWalk(222, 449);
    if (Running()) ForceWalk(217, 447);
    if (Running())
      while (ObjectAt(217, 447) != 63 && Running()) {
        cMode = " Opening Door";
        showReport();
        AtObject(217, 447);
        Wait(1000);
      }
  }

  public void Fish() {
    int[] spot = GetNearestObject(192);
    AtObject(spot[0], spot[1]);
    cMode = " Fishing";
    showReport();
    Wait(1000);
  }

  public void Sleeptime() {
    cMode = " sleeping";
    showReport();
    while (!Sleeping() && Running()) {
      Use(FindInv(1263));
      Wait(3000);
    }
    while (Sleeping()) {
      Wait(5000);
    }
    Wait(200);
    slept++;
  }

  private void setupReport() {
    reportFrame = new JFrame("Sags Multi Fisher: Progress Report");
    reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    reportFrame.setSize(new Dimension(325, 400));

    reportPanel = new JPanel(new GridLayout(4, 8));

    reportFrame.getContentPane().add(reportPanel, BorderLayout.CENTER);

    pMode = new JLabel("You are currently" + cMode);
    pMode.setFont(new Font("Helvetica", Font.BOLD, 18));
    pMins = new JLabel("You have been fishing for " + minutes + " minutes", SwingConstants.LEFT);
    pMins.setFont(new Font("Helvetica", Font.BOLD, 12));
    pFished = new JLabel("You have fished " + fishes + "fishes", SwingConstants.LEFT);
    pFished.setFont(new Font("Helvetica", Font.BOLD, 12));
    pLevels = new JLabel("You have gained " + fLevels + " fishing levels", SwingConstants.LEFT);
    pLevels.setFont(new Font("Helvetica", Font.BOLD, 12));
    pExp = new JLabel("You have gained " + expgained + " fishing experience", SwingConstants.LEFT);
    pExp.setFont(new Font("Helvetica", Font.BOLD, 12));
    pSlept = new JLabel("You have slept " + slept + " times", SwingConstants.LEFT);
    pSlept.setFont(new Font("Helvetica", Font.BOLD, 12));
    empty = new JLabel("", SwingConstants.LEFT);

    reportPanel.add(pMode);
    reportPanel.add(empty);
    reportPanel.add(pMins);
    reportPanel.add(pFished);
    reportPanel.add(pLevels);
    reportPanel.add(pExp);
    reportPanel.add(pSlept);

    pMode.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pMins.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pFished.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pLevels.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pExp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pSlept.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    reportFrame.pack();
    reportFrame.setVisible(true);
  }

  public void showReport() {
    fLevels = GetStat(10) - sLevel;
    minutes = (System.currentTimeMillis() - starttime) / 1000;
    expgained = (GetExperience(10) - startexp);
    pMode.setText("You are currently" + cMode);
    pMins.setText("You have been fishing for " + minutes + " seconds");
    pFished.setText("You have fished " + fishes + " fishes");
    pLevels.setText("You have gained " + fLevels + " fishing levels");
    pExp.setText("You have gained " + expgained + " fishing experience");
    pSlept.setText("You have slept " + slept + " times");
    SwingUtilities.updateComponentTreeUI(pMode);
    SwingUtilities.updateComponentTreeUI(pMins);
    SwingUtilities.updateComponentTreeUI(pFished);
    SwingUtilities.updateComponentTreeUI(pLevels);
    SwingUtilities.updateComponentTreeUI(pExp);
    SwingUtilities.updateComponentTreeUI(pSlept);
  }

  private void addWidgets() {
    fishFrame = new JFrame("Sags Multi Fisher: Preferences");
    fishFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    fishFrame.setSize(new Dimension(325, 400));

    fishPanel = new JPanel(new GridLayout(4, 8));

    fishFrame.getRootPane().setDefaultButton(save);

    fishFrame.getContentPane().add(fishPanel, BorderLayout.CENTER);
    String[] fishModes = {"Just Fish", "Fish Then Cook"};
    String[] bankModes = {"Banking", "No Banking"};
    fishModeLabel = new JLabel("Fishing Mode?", SwingConstants.LEFT);

    emptylabel1 = new JLabel("");
    fishLabel = new JLabel("Do what?", SwingConstants.LEFT);
    fishMode = new JComboBox(fishModes);
    save = new JButton("Save choices");
    bankMode = new JComboBox(bankModes);
    save.addActionListener(this);

    fishPanel.add(fishModeLabel);
    fishPanel.add(fishMode);
    fishPanel.add(fishLabel);
    fishPanel.add(bankMode);
    fishPanel.add(save);
    fishPanel.add(emptylabel1);

    fishModeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    fishLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    fishPanel.setBackground(Color.black);
    bankMode.setBackground(Color.black);
    fishMode.setBackground(Color.black);
    fishModeLabel.setForeground(Color.white);
    fishLabel.setForeground(Color.white);
    fishMode.setForeground(Color.white);
    bankMode.setForeground(Color.white);

    fishFrame.pack();
    fishFrame.setVisible(true);
  }

  public void actionPerformed(ActionEvent event) {
    Object chosenFishMode = fishMode.getSelectedItem();
    Object chosenBankMode = bankMode.getSelectedItem();
    fishmode = chosenFishMode.toString();
    bankmode = chosenBankMode.toString();
    run_script = true;
  }

  public void RunScipt() {
    fishFrame.dispose();
    long time = System.currentTimeMillis();
    starttime = time;
    if (fishmode.equalsIgnoreCase("Fish Then Cook") && bankmode.equalsIgnoreCase("Banking")) {
      DepId1 = 357;
      DepId2 = 359;
      XFire = 222;
      YFire = 1385;
    }
    if (fishmode.equalsIgnoreCase("Fish Then Cook") && bankmode.equalsIgnoreCase("No Banking")) {
      XFire = 234;
      YFire = 495;
    }
    if (fishmode.equalsIgnoreCase("Just Fish") && bankmode.equalsIgnoreCase("No Banking")) {
      DepId1 = 356;
      DepId2 = 358;
    }
    sLevel = GetStat(10);
    setupReport();
    showReport();
    Wait(1000);
    DisplayMessage(
        "@ran@S@ran@a@ran@g@ran@s @cya@Multi @blu@Barb @cya@Fisher @blu@Started @gre@ MUCH <3 To Bruncle for GUI!! Fixed for IdleRSC by Gah",
        3);
    startexp = GetExperience(10);

    while (Running()) {

      showReport();
      Wait(100);
      if (System.currentTimeMillis() - time > (5 * 60000)) {
        cMode = "Saving a report..";
        showReport();
        time = System.currentTimeMillis();
      }
      if (fishmode.equalsIgnoreCase("Just Fish") && bankmode.equalsIgnoreCase("No Banking")) {
        while (!Sleeping() && Running()) {
          Fish();

          if (Fatigue() >= 95 && Running()) {
            Sleeptime();
            Wait(50);
          }
        }
      }
      if (fishmode.equalsIgnoreCase("Just Fish") && bankmode.equalsIgnoreCase("Banking")) {
        while (!Sleeping() && InvCount() < 30 && Running()) {
          Fish();

          if (Fatigue() >= 95 && Running()) {
            Sleeptime();
            Wait(50);
          }
        }
        if (InvCount() == 30 && Running()) {
          WalkBankNoCook();
          TalkBanker();
          WalkFromBank();
        }
      }

      if (fishmode.equalsIgnoreCase("Fish Then Cook") && bankmode.equalsIgnoreCase("Banking")) {
        while (!Sleeping() && InvCount() < 30 && Running()) {
          Fish();

          if (Fatigue() >= 95 && Running()) {
            Sleeptime();
            Wait(50);
          }
        }
        if (InvCount() == 30 && Running()) {
          WalkToRange();
          CookFish();
          DropBurnt();
          WalkFromRangeBank();
          TalkBanker();
          WalkFromBank();
        }
      }
      if (fishmode.equalsIgnoreCase("Fish Then Cook") && bankmode.equalsIgnoreCase("No Banking")) {
        while (!Sleeping() && InvCount() < 30 && Running()) {
          Fish();

          if (Fatigue() >= 95 && Running()) {
            Sleeptime();
            Wait(50);
          }
        }
        if (InvCount() == 30 && Running()) {
          WalkFireBarbVill();
          CookFish();
          DropBurnt();
          EatFish();
          WalkFromFireBarbVill();
        }
      }
    }

    DisplayMessage("@red@Stopped", 3);
  }
}
