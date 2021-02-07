package scripting.sbot;

import compatibility.sbot.Script;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;

//Ty to Exemplar for teaching me how to integrate gui into script
public class GuildFisher2 extends Script implements ActionListener
{
    boolean Running = true;
    Thread reportThread;
    Graphics g;
    public boolean sleep = false; 
    public int fishes = 0; 
    public String fishtype = "bruncle"; 
    public long starttime,minutes;
    public String preferences[] = new String[2];
    public int slept = 0;
    public int startexp = 0;
    public int expgained = 0;
    public String FM,ctime;
    boolean run_script = false;
    public String cMode = " doing nothing";
    public int fLevels,sLevel;
   
    JFrame fishFrame, reportFrame;
    JPanel fishPanel, reportPanel;
    JLabel fishModeLabel, fishLabel, emptylabel1, pMode, pMins,pExp, pFished, pLevels, pSlept,empty;
    JButton save;
    JComboBox fishMode, fish ;

    public String[] getCommands()
    {
        return new String[]{"guildfish"};
    }
    public void ServerMessage(String message)
    {
      if (message.equals("@gam@You are too tired to catch this fish")||message.equals("@gre@You are too tired to gain experience, get some rest"))
         {sleep = true;
         DisplayMessage("You need to sleep..",2);}
      if (message.indexOf("@gam@You catch a ")>= 0)
          fishes++;
    }   
   
       public void start(String command, String parameter[])
       {
             javax.swing.SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   addWidgets();
               }
           });
           while(!run_script && Running())
                  Wait(100);
           if(run_script)
                  RunScipt();
      }
    // pTitle, pMode, pMins, pFished, pLevels, pSlept;
     private void setupReport() {
        reportFrame = new JFrame("Bruncle's Guild Fisher: Progress Report");
        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reportFrame.setSize(new Dimension(325, 400));

        reportPanel = new JPanel(new GridLayout(4, 8));

        reportFrame.getContentPane().add(reportPanel, BorderLayout.CENTER);
       
        pMode = new JLabel("You are currently"+cMode);
        pMode.setFont(new Font("Helvetica",Font.BOLD,18));
          pMins = new JLabel("You have been fishing for " + minutes+" minutes", SwingConstants.LEFT);
          pMins.setFont(new Font("Helvetica",Font.BOLD,12));
          pFished = new JLabel("You have fished " + fishes+ " "+fishtype+"s", SwingConstants.LEFT);
          pFished.setFont(new Font("Helvetica",Font.BOLD,12));
          pLevels = new JLabel("You have gained " + fLevels+ " fishing levels", SwingConstants.LEFT);
          pLevels.setFont(new Font("Helvetica",Font.BOLD,12));
          pExp= new JLabel("You have gained " + expgained+ " fishing experience", SwingConstants.LEFT);
          pExp.setFont(new Font("Helvetica",Font.BOLD,12));
          pSlept = new JLabel("You have slept " + slept+" times", SwingConstants.LEFT);
          pSlept.setFont(new Font("Helvetica",Font.BOLD,12));
        empty= new JLabel("" , SwingConstants.LEFT);
       
        reportPanel.add(pMode);
        reportPanel.add(empty);
        reportPanel.add(pMins);
        reportPanel.add(pFished);
        reportPanel.add(pLevels);
        reportPanel.add(pExp);
        reportPanel.add(pSlept);
   
   pMode.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
   pMins.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
   pFished.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
   pLevels.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
   pExp.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
   pSlept.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        reportFrame.pack();
        reportFrame.setVisible(true);
        }
       
        public void showReport(){
           fLevels = GetStat(10)-sLevel;
           minutes = (System.currentTimeMillis() - starttime) / 1000;
           expgained = (GetExperience(10) - startexp);
           pMode.setText("You are currently"+cMode);
           pMins.setText("You have been fishing for " + minutes+" seconds");
           pFished.setText("You have fished " + fishes+" "+fishtype+"s");
           pLevels.setText("You have gained " + fLevels+ " fishing levels");
           pExp.setText("You have gained " + expgained+ " fishing experience");
           pSlept.setText("You have slept " + slept+" times");
           SwingUtilities.updateComponentTreeUI(pMode);
           SwingUtilities.updateComponentTreeUI(pMins);
           SwingUtilities.updateComponentTreeUI(pFished);
           SwingUtilities.updateComponentTreeUI(pLevels);
           SwingUtilities.updateComponentTreeUI(pExp);
           SwingUtilities.updateComponentTreeUI(pSlept);
        }
       
       
        private void addWidgets() {
        fishFrame = new JFrame("Bruncle's Guild Fisher: Preferences");
        fishFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fishFrame.setSize(new Dimension(325, 400));

        fishPanel = new JPanel(new GridLayout(4, 8));
       
        fishFrame.getRootPane().setDefaultButton(save);

        fishFrame.getContentPane().add(fishPanel, BorderLayout.CENTER);
        String fishModes[] = {"Fish then cert","Fish then cook then cert"};
        String fishs[] = {"Lobster","Shark"};
        fishModeLabel = new JLabel("Fishing Mode?", SwingConstants.LEFT);
          
          emptylabel1 = new JLabel("");
          fishLabel = new JLabel("Fish what?", SwingConstants.LEFT);
          fishMode = new JComboBox(fishModes);
          save = new JButton("Save choices");
   fish = new JComboBox(fishs);
        save.addActionListener(this);
       
        fishPanel.add(fishModeLabel);
        fishPanel.add(fishMode);
        fishPanel.add(fishLabel);
        fishPanel.add(fish);
        fishPanel.add(save);
        fishPanel.add(emptylabel1);
          
        fishModeLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        fishLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        fishPanel.setBackground(Color.black);
        fish.setBackground(Color.black);
        fishMode.setBackground(Color.black);
        fishModeLabel.setForeground(Color.white);
        fishLabel.setForeground(Color.white);
        fishMode.setForeground(Color.white);
        fish.setForeground(Color.white);
   
        fishFrame.pack();
        fishFrame.setVisible(true);
        }

       public void actionPerformed(ActionEvent event) {
             Object chosenFishMode = fishMode.getSelectedItem();
           Object chosenFishType = fish.getSelectedItem();
           FM = chosenFishMode.toString();
           fishtype = chosenFishType.toString();   
           run_script = true;
       }
       
       
       public void RunScipt() {
       fishFrame.dispose();
       long time = System.currentTimeMillis(); starttime = time;
       sLevel=GetStat(10);
       setupReport();
       showReport();
       DisplayMessage("@red@Hello "+System.getProperty("user.name")+" I know where you live..", 3);
       Wait(1000);   
       DisplayMessage("@ran@Bruncle's guild lob fisher started", 3);
       int rawid = 0; int burntid = 0;
       startexp = GetExperience(10); int certs = 4;
       GetSettings();
       if (fishtype.equalsIgnoreCase ("Lobster")) { rawid = 372; burntid = 374;}
       if (fishtype.equalsIgnoreCase ("Shark")) {rawid = 545; burntid = 547;}
       DisplayMessage("You will "+FM+" "+fishtype,3);
       LastTime();
       Wait(2000);
       DisplayMessage("@red@Script Starting in 5 seconds..",3);
       Wait(1000);
       DisplayMessage("@gre@4",2);
       Wait(1000);
       DisplayMessage("@gre@3",2);
       Wait(1000);
       DisplayMessage("@gre@2",2);
       Wait(1000);
       DisplayMessage("@gre@1",2);
       Wait(1000);
       DisplayMessage("@ran@STARTING TO FISH "+fishtype,3);
       Wait(1000);
      while (Running())
      {
         showReport();
         Wait(1000);
      while (InvCount() < 30 && Running())
      {
         showReport();
         if (fishtype.equalsIgnoreCase("Lobster"))
         {
            int spot[] = GetNearestObject(376);
            AtObject(spot[0],spot[1]);
            cMode = " fishing lobsters";
            showReport();
            Wait(1000);  
            
            while(IsBatching() && InvCount() < 30) {
            	Wait(10);
            }
         }
         if (fishtype.equalsIgnoreCase("Shark"))
         {
            int spot[] = GetNearestObject(261);
            AtObject2(spot[0],spot[1]);
            Wait(1000);
            
            while(IsBatching() && InvCount() < 30) {
            	Wait(10);
            }
            cMode = " fishing sharks";
            showReport();
         }
         
      
         if (System.currentTimeMillis() - time > (5 * 60000))
         {
            cMode="Saving a report..";
            showReport();
            time = System.currentTimeMillis();
            Report();
         }
         if (sleep == true && Fatigue() >= 80 && Running())
         {
            cMode = " sleeping";
            showReport();
            while (!Sleeping() && Running())
            {
               Use(FindInv(1263));
               Wait(3000);
            }
            while (Sleeping()){Wait(5000); }
            Wait(3000);
            sleep = false;
            slept++;
         }
      }   
   if (InvCount() == 30 && Running())
   {
      showReport();
      if (FM.equalsIgnoreCase("Fish then cert") && Running())
      {
         cMode = " walking to certers";
         showReport();
         if (Running()){
         Wait(3000);
         Walk(602,506);
         WalkNoWait(605,503);
         Wait(2000);}
         while (GetX() == 603 && Running())
         {
         while (DoorAt(603,506,3) == 2)
         {
            cMode = " opening door";
            showReport();
            OpenDoor(603,506,3);
            Wait(2000);
         }
         WalkNoWait(605,503);
         Wait(1000);
         }
         if (Running())
         Walk(605,503);
         while (InvCount() == 30 && Running())
         {
               showReport();
               if (rawid == 372 && Running())
               {
                  cMode = " certing lobsters";
                  showReport();
                  while (!QuestMenu() && Running())
                  {
                     TalkToNPC(GetNearestNPC(369));
                     Wait(1500);
                  }
                  showReport();
                  if (Running()){
                  Wait(1000);
                  Answer(1);
                  while (!QuestMenu())Wait(500);
                  Wait(1000);
                  Answer(1);
                  while (!QuestMenu())Wait(500);
                  Wait(1000);
                  Answer(4);
                  Wait(3000);
                  Walk(603,505);}
               }
               if (rawid == 545 && Running() )
               {
                  cMode = " certing sharks";
                  showReport();
                  while (!QuestMenu() && Running())
                  {
                     TalkToNPC(GetNearestNPC(370));
                     Wait(1500);
                  }
                  Wait(1000);
                  Answer(1);
                  while (!QuestMenu())Wait(500);
                  Wait(1000);
                  Answer(1);
                  while (!QuestMenu())Wait(500);
                  Wait(1000);
                  Answer(4);
                  Wait(3000);
                  Walk(603,505);
               }
            }
            while (DoorAt(603,506,3) == 2 && Running())
            {
               cMode = " opening doors";
               showReport();
               OpenDoor(603,506,3);
               Wait(2000);
               WalkNoWait(602,507);
            }
            Walk(589,502);
      }
      if (FM.equalsIgnoreCase("Fish then cook then cert"))
      {
            if (Running()){
            cMode = " walking to range";
            showReport();
            Walk(587,508);
            ForceWalk(586,518);
            Wait(4000);}
            while (GetY() == 518 && Running())
            {
               showReport();
               while (DoorAt(586,519,0) == 2)
               {
                  OpenDoor(586,519,0);
                  Wait(2000);
               }
               ForceWalkNoWait(586,520);
               Wait(1000);
            }
            Wait(2000);
            while (InvCount(rawid) > 0 && Running())
            {
               cMode = " cooking fish";
               showReport();
               UseOnObject(583, 520, FindInv(rawid));
               Wait(2500);
               WaitForBatchFinish();
               if (Fatigue() >= 80 && Running())
                  {
                     while (!Sleeping() && Running())
                     {
                        Use(FindInv(1263));
                        Wait(3000);
                     }
                     while (Sleeping()) Wait(10000);
                     sleep = false;
                     slept++;
                  }
            }
            while (InvCount(burntid) > 0 && Running())
            {
               Drop(FindInv(burntid));
               Wait(1500);
               cMode = " dropping burnt fish";
               showReport();
            }
            if (Running()){
            ForceWalk(586,519);
            Wait(3000);}
            while (GetY() == 519 && Running())
            {
               cMode = " opening door at range";
               showReport();
               while (DoorAt(586,519,0) == 2)
               {
                  OpenDoor(586,519,0);
                  Wait(2000);
               }
               ForceWalkNoWait(586,518);
               Wait(1000);
            }
            if (Running()){
            ForceWalk(602,506);
            WalkNoWait(605,503);
            Wait(2000);}
            while (GetX() == 603 && Running())
            {
               cMode = " opening door at certers";
               showReport();
               while (DoorAt(603,506,3) == 2)
               {
                  OpenDoor(603,506,3);
                  Wait(2000);
               }
               WalkNoWait(605,503);
               Wait(1000);
            }
            if (Running())
            Walk(605,503);
            while (InvCount() == 30 && Running())
            {

               if (fishtype.equals("Lobster") && Running())
               {
                  cMode = " certing lobsters";
                  showReport();
                  if (InvCount(373) < 25 && InvCount(373) >= 20) certs = 3;
                  if (InvCount(373) < 20 && InvCount(373) >= 15) certs = 2;
                  
                  while (!QuestMenu() && Running())
                  {
                     TalkToNPC(GetNearestNPC(369));
                     Wait(1500);
                  }
                  Wait(1000);
                  Answer(1);
                  while (!QuestMenu())Wait(500);
                  Wait(1000);
                  Answer(0);
                  while (!QuestMenu())Wait(500);
                  Wait(2000);
				  System.out.println("certs");
				  System.out.println(certs);
                  Answer(certs);
                  Wait(3000);
                  Walk(603,505);
               }
               if (fishtype.equalsIgnoreCase("Shark") && Running())
               {
                  cMode = " certing shark";
                  if (InvCount(545) < 25 && InvCount(545) >= 20) certs = 3;
                  if (InvCount(545) < 20 && InvCount(545) >= 15) certs = 2;
                  if (InvCount(545) < 15 && InvCount(545) >= 10) certs = 2;
                  while (!QuestMenu() && Running())
                  {
                     TalkToNPC(GetNearestNPC(370));
                     Wait(1500);
                  }
                  Wait(1000);
                  Answer(1);
                  while (!QuestMenu())Wait(500);
                  Wait(1000);
                  Answer(0);
                  while (!QuestMenu())Wait(500);
                  Wait(1000);
                  Answer(certs);
                  Wait(3000);
                  Walk(603,505);
               }
            }
            while (GetX() != 602 && Running())
            {
               cMode = " opening door at certers";
               showReport();
               while (DoorAt(603,506,3) == 2)
               {
                  OpenDoor(603,506,3);
                  Wait(2000);
               }
               WalkNoWait(602,503);
               Wait(1000);
            }
            if (Running())
            cMode = " walking back to the pier";
            showReport();
            Walk(589,502);
            Wait(3000);
      }
    }
    
      }
   DisplayMessage("@dre@Stopped", 3);
  }
   
public void Report()
{
      Date date;
              date = new Date();
             ctime = date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
           minutes = (System.currentTimeMillis() - starttime) / 60000;
      FileOutputStream output;
           PrintStream P;
           System.out.println("Writing Report..");
           expgained = (GetExperience(10) - startexp);
           try
           {
              BufferedWriter out = null;
              out = new BufferedWriter( new FileWriter("guildfisher.txt", true));
              out.write("^^PrOgReSs RePoRt @ " + ctime+"..");
              out.newLine();
              out.write("After "+minutes+" minutes:");
              out.newLine();
              out.write("You have fished "+fishes+" "+fishtype+"[s]..");
              out.newLine();
              out.write("You have gained "+expgained +" fishing experience..");
              out.newLine();
              out.write("You have slept "+slept+" times..");
              out.newLine();
              out.close();
           }
           
            catch (Exception e)
                   {
                           System.err.println ("Error writing to file");
                   }
       
}
public void GetSettings()
{
   
}
public void LastTime()
{
   DisplayMessage("Last time you ran the script this was the progress report..:",3);
   int line = 0;
   try
         {
                                 FileInputStream fstream = new
               FileInputStream("guildfisher.txt");
            DataInputStream in =
                                         new DataInputStream(fstream);
                                 int b;
                                 while ((b = in.read()) != -1)
                                 {
                                    if (b == '\n')
                                    line++;
                                 }
                                 in.close();
                         }
                         catch (Exception e)
         {
            DisplayMessage("This is the first time you've run the script:O",3);
            System.err.println("File input error");
         }
                                 System.out.println("There are "+line+" lines in the text file..");
                                 int cline = 0;
                                 if (line >= 1774)
                                 {
                                    FileOutputStream out;
                         PrintStream p;

                         try
                         {
                       
                                    out = new FileOutputStream("guildfisher.txt");

                       
                                  p = new PrintStream( out );
      
                                   p.println ("");

                                  p.close();
                         }
                            catch (Exception e)
                          {
                                    System.err.println ("Error writing to file");
                          }
                            }
                        try {
                                 FileInputStream fstream2 = new
               FileInputStream("guildfisher.txt");
            DataInputStream in2 =
                                         new DataInputStream(fstream2);
                                 while (cline != (line - 5))
                                 {
                                    in2.readLine();
                                    cline++;
                                 }
                                 if (cline == line - 5)
                                 {
                                         DisplayMessage(in2.readLine(),3);
                                         Wait(1000);   
               DisplayMessage(in2.readLine(),3);
               Wait(1000);
               DisplayMessage(in2.readLine(),3);
               Wait(1000);
               DisplayMessage(in2.readLine(),3);
               Wait(1000);
               DisplayMessage(in2.readLine(),3);
               Wait(1000);
            }
                  in2.close();
             }
                         catch (Exception e)
         {   
            System.err.println("File input error");
         }      
                      
}
} 