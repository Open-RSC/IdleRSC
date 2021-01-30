import compatibility.sbot.Script;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;

public class MultiFight extends Script {
	
	boolean run_script = false;
	public JFrame reportFrame,frame,settingsFrame;
	public JPanel reportPanel, statPanel, npcPanel, awPanel, alPanel, eatPanel, bonePanel, savePanel;
	public JLabel emptylabel1, pMode, pMins,pExp, pFished, pLevels, pSlept,empty,loops;
	public JLabel npcIds,dStats,defLbl,attLbl,strLbl,antiWander,maxXLbl,minXLbl,maxYLbl,minYLbl;
	public JLabel antiLock,d1IDLbl,d2IDLbl,d1XLbl,d1YLbl,d2XLbl,d2YLbl;
	public String FM, action, objects, objectType, stat,cMode,fName;
	public String mAtt,mDef,mStr;
	public JTabbedPane tabs = new JTabbedPane();
	public JTextField att,def,str,npc1,npc2,npc3,npc4,maxX,minX,maxY,minY,d1Dir,d2Dir;
	public JTextField d1ID,d2ID,d1X,d1Y,d2X,d2Y,foodId,eatAtHP,cT,wBPX,wBPY,mP,filName;
	public JTextField item1,item2,item3,item4,item5,item6,item7,item8,file,awRadius;
	public long minutes,starttime;
	public int expgained,slept,fLevels,killed,wX,wY,d1D,d2D,fS,loopage,maxPrayer,spell,x,y;
	public int cAtt,cDef,cStr,mX,mY,minXC,minYC,d1CId,d2CId,gOD,gOD2,d1XC,d1YC,d2XC,d2YC,eAHP,fID,fTime;
	public JLabel pAvKilled,attExp,defExp,strExp,stats,wantedStats,xpPerHr,secs;
	public JRadioButton Gate,Door,Gate2,Door2;
	public int startDefExp,startStrExp,startAttExp,radius;
	public int i[] = new int[9];
	public int npcArr[] = new int[4];
	public JComboBox fightMode;
	public boolean aw = false; 
	public boolean pI = false;
	public boolean bones = false;
	public boolean mage = false;
	public JComboBox spells;
	public long time;
	
	public void KeyPressed(int id)
    	{
       		if (id == 1012){
       			showReport();
       		}
       		if (id == 1013){
       			loadSettings();
       		}
       	}
	
	public String[] getCommands() {
	        return new String[]{"multifight"};
	}	
		
	public void addWidgets()  {
	        JFrame.setDefaultLookAndFeelDecorated(true);
	        frame = new JFrame("Bruncle's noob trainer: Settings");
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        frame.setSize(250,250);
	        
	        JTabbedPane tabbedPane = new JTabbedPane();
	        JLabel empty = new JLabel("");
		//----------------------------------------------------------------------//		
		JPanel statPanel = new JPanel(new GridLayout(4, 2));
		att = new JTextField("1", 2);
	       	def = new JTextField("1", 2);
	       	str = new JTextField("1", 2);
	       	dStats = new JLabel("Desired Stats:", SwingConstants.LEFT);
	       	dStats.setFont(new Font("Helvetica",Font.BOLD,20));
	       	defLbl = new JLabel("Highest Def level:", SwingConstants.LEFT);
	       	attLbl = new JLabel("Highest Att level:", SwingConstants.LEFT);
	       	strLbl = new JLabel("Highest Str level:", SwingConstants.LEFT);      	
	       	
	       	statPanel.add(dStats);
	       	statPanel.add(empty);
	       	statPanel.add(attLbl);
	       	statPanel.add(att);
	       	statPanel.add(defLbl);
	        statPanel.add(def);
	       	statPanel.add(strLbl);
	       	statPanel.add(str);
	       	
	       	//-----------------------------------------------------------------------//	       	
	       	JPanel npcPanel = new JPanel(new GridLayout(2, 4));
	       	npc1 = new JTextField("1",3);
	       	npc2 = new JTextField("1",3);
	       	npc3 = new JTextField("1",3);
	       	npc4 = new JTextField("1",3);
		cT = new JTextField("1",4);
		
	       	npcIds = new JLabel("NPC IDs:",SwingConstants.LEFT);
	       	npcIds.setFont(new Font("Helvetica",Font.BOLD,20));
	       	JLabel fTLbl = new JLabel("Fight Time (ms):");
	       		
	       	npcPanel.add(npcIds);
	       	npcPanel.add(npc1);
	       	npcPanel.add(npc2);
	       	npcPanel.add(npc3);
	       	npcPanel.add(npc4);
	       	npcPanel.add(fTLbl);
	       	npcPanel.add(cT);
	       	
	       	//-----------------------------------------------------------------------//
	       	JPanel iPanel = new JPanel(new GridLayout(5, 2));
	       	
	       	JLabel itemTtl = new JLabel("Item IDs:",SwingConstants.LEFT);
	       	itemTtl.setFont(new Font("Helvetica",Font.BOLD,20));
	       	JCheckBox items = new JCheckBox("Pick up items?");
	       	items.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	pI = true;
	       		}
	       	}
	       	);
	       	
	       	item1 = new JTextField("1",4);
	       	item2 = new JTextField("1",4);
	       	item3 = new JTextField("1",4);
	       	item4 = new JTextField("1",4);
	       	item5 = new JTextField("1",4);
	       	item6 = new JTextField("1",4);
	       	item7 = new JTextField("1",4);
	       	item8 = new JTextField("1",4);
	       	
	       	iPanel.add(itemTtl);
	       	iPanel.add(items);
	       	iPanel.add(item1);
	       	iPanel.add(item2);
	       	iPanel.add(item3);
	       	iPanel.add(item4);
	       	iPanel.add(item5);
	       	iPanel.add(item6);
	       	iPanel.add(item7);
	       	iPanel.add(item8);
	       	
	       	//------------------------------------------------------------------------//	      
	       	JPanel awPanel = new JPanel(new GridLayout(7, 2));
	       	
	       	antiWander = new JLabel("Anti Wander:", SwingConstants.LEFT);
	       	antiWander.setFont(new Font("Helvetica",Font.BOLD,20));
	       	awRadius = new JTextField("1",2);
	       	JCheckBox awYN = new JCheckBox("Use anti wander?");
	       	awYN.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	if (aw == false)
	        		aw = true;
	        	else 
	        		aw = false;
	       		}
	       	}
	       	);
	       	awPanel.add(antiWander);
	       	awPanel.add(awYN);
	       	awPanel.add(new JLabel("Radius:"));
	       	awPanel.add(awRadius);
	       	JButton help = new JButton("Help");
	       	help.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	JOptionPane.showMessageDialog(null,"Put the number of squares you want the anti wander square to be (measured from middle of the square to any side). Put 1 to range",
   			"Anti Wander Help",JOptionPane.PLAIN_MESSAGE);
	       		}
	       	}
	       	);
	       	awPanel.add(help);
	       	//----------------------------------------------------------------------//	       	
	       	JPanel alPanel = new JPanel(new GridLayout(5, 3));
	       	
	       	d1ID = new JTextField("1", 2);
	       	d2ID = new JTextField("1", 2);
	       	d1X = new JTextField("1", 2);
	       	d1Y = new JTextField("1", 2);
	       	d2X = new JTextField("1", 2);
	       	d2Y = new JTextField("1", 2);
	       	d1Dir = new JTextField("1", 1);
	       	d2Dir = new JTextField("1", 1);
	       	Gate = new JRadioButton("Gate");
	       	Gate.setActionCommand("Gate");
	       	Door = new JRadioButton("Door");
	       	Door.setActionCommand("Door");
	       	ButtonGroup doorGroup = new ButtonGroup();
	        doorGroup.add(Gate);
	        doorGroup.add(Door);
	        Door.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	gOD = 1;
	       		}
	       	}
	       	);
	       	Gate.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	gOD = 2;
	       		}
	       	}
	       	);
	       	Gate2 = new JRadioButton("Gate");
	       	Gate2.setActionCommand("Gate");
	       	Door2 = new JRadioButton("Door");
	       	Door2.setActionCommand("Door");
	       	ButtonGroup doorGroup2 = new ButtonGroup();
	        doorGroup2.add(Gate2);
	        doorGroup2.add(Door2);
	        Door2.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	gOD2 = 1;
	       		}
	       	}
	       	);
	       	Gate2.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	gOD2 = 2;
	       		}
	       	}
	       	);
	       	antiLock = new JLabel("Anti LockOut:", SwingConstants.LEFT);
	       	antiLock.setFont(new Font("Helvetica",Font.BOLD,20));
	       	d1IDLbl = new JLabel("Door/Gate closed id:", SwingConstants.LEFT);
	     	d2IDLbl = new JLabel("Door/Gate2 closed id:", SwingConstants.LEFT);
	       	d1XLbl = new JLabel("Door 1 X co-ord", SwingConstants.LEFT);
	       	d1YLbl = new JLabel("Door 1 Y co-ord", SwingConstants.LEFT);
	       	d2XLbl = new JLabel("Door 2 X co-ord", SwingConstants.LEFT);
	       	d2YLbl = new JLabel("Door 2 Y co-ord", SwingConstants.LEFT);
		JLabel d1DLbl = new JLabel("Door 1 direction");
		JLabel d2DLbl = new JLabel("Door 2 direction");
		
		alPanel.add(antiLock);
	       	alPanel.add(Gate);
	       	alPanel.add(Door);
	       	alPanel.add(Gate2);
	       	alPanel.add(Door2);	
	       	alPanel.add(d1IDLbl);	
	       	alPanel.add(d1ID);
	       	alPanel.add(d2IDLbl);
	       	alPanel.add(d2ID);
	       	alPanel.add(d1XLbl);
	       	alPanel.add(d1X);
	       	alPanel.add(d1YLbl);
	       	alPanel.add(d1Y);
	       	alPanel.add(d2XLbl);     	
	       	alPanel.add(d2X);
	       	alPanel.add(d2YLbl);
	       	alPanel.add(d2Y);
	       	alPanel.add(d1DLbl);
	       	alPanel.add(d1Dir);
	       	alPanel.add(d2DLbl);
	       	alPanel.add(d2Dir);
	       	
	       	//----------------------------------------------------------------------//
		JPanel eatPanel = new JPanel(new GridLayout(5, 1));
		JLabel eatLbl = new JLabel ("Eating:");
		eatLbl.setFont(new Font("Helvetica",Font.BOLD,20));
		
		JLabel eatAtHpLbl = new JLabel ("HP level to eat at:");
		eatAtHP = new JTextField("1",2);
		JLabel foodIdLbl = new JLabel ("Food ID:");
		foodId = new JTextField("3",2);
		
		eatPanel.add(eatLbl);
		eatPanel.add(eatAtHpLbl);
		eatPanel.add(eatAtHP);
		eatPanel.add(foodIdLbl);
		eatPanel.add(foodId);
		
		//-----------------------------------------------------------------------//
		JPanel bonePanel = new JPanel(new GridLayout(1,2));
		
		JCheckBox bYN = new JCheckBox("Bury Bones?");
	       	bYN.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	if (bones == false)
	        		bones = true;
	        	else 
	        		bones = false;
	        	i[8] = 20;
	       		}
	       	}
	       	);
	       	
	       	mP = new JTextField("1",2);
	       	
	       	bonePanel.add(bYN);
	       	bonePanel.add(new JLabel("Max prayer level:"));
	       	bonePanel.add(mP);
		
		
		//-----------------------------------------------------------------------//
		JPanel magePanel = new JPanel(new GridLayout(1,3));
		JCheckBox mYN = new JCheckBox("Mage?");
	       	mYN.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	if (mage == false)
	        		mage = true;
	        	else 
	        		mage = false;
	       		}
	       	}
	       	);
	       	String spellList[] = {"Wind Strike","Confuse","Water Strike","Earth Strike","Weaken","Fire Strike","Wind Bolt","Curse",
	       				"Water Bolt","Earth Bolt","Fire Bolt","Wind Blast","Water Blast","Earth Blast","Fire Blast"};
	       	spells = new JComboBox(spellList);
	       	magePanel.add(mYN);
	       	magePanel.add(spells);
		
		//-----------------------------------------------------------------------//		
		JPanel savePanel = new JPanel(new GridLayout(1, 3));
		JLabel saveLbl = new JLabel ("Save:");
		file = new JTextField("FileName",15);
		saveLbl.setFont(new Font("Helvetica",Font.BOLD,20));
		
		JButton save = new JButton("Save choices");
		save.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	        	run_script = true;
	        	cAtt = Integer.parseInt(att.getText());
	        	cDef = Integer.parseInt(def.getText());
	        	cStr = Integer.parseInt(str.getText()); 
	        	radius = Integer.parseInt(awRadius.getText()); 
	        	d1CId = Integer.parseInt(d1ID.getText());
	        	d2CId = Integer.parseInt(d2ID.getText());
	        	d1D = Integer.parseInt(d1Dir.getText());
	        	d2D = Integer.parseInt(d2Dir.getText());
	        	d1XC = Integer.parseInt(d1X.getText());
	        	d1YC = Integer.parseInt(d1Y.getText());
	        	d2XC = Integer.parseInt(d2X.getText());
	        	d2YC = Integer.parseInt(d2Y.getText());
	        	eAHP = Integer.parseInt(eatAtHP.getText());
	        	fID = Integer.parseInt(foodId.getText());
	        	npcArr[0] = Integer.parseInt(npc1.getText());
	        	npcArr[1] = Integer.parseInt(npc2.getText());
	        	npcArr[2] = Integer.parseInt(npc3.getText());
	        	npcArr[3] = Integer.parseInt(npc4.getText());
	        	fTime = Integer.parseInt(cT.getText());
	        	i[0] = Integer.parseInt(item1.getText());
	        	i[1] = Integer.parseInt(item2.getText());
	        	i[2] = Integer.parseInt(item3.getText());
	        	i[3] = Integer.parseInt(item4.getText());
	        	i[4] = Integer.parseInt(item5.getText());
	        	i[5] = Integer.parseInt(item6.getText());
	        	i[6] = Integer.parseInt(item7.getText());
	        	i[7] = Integer.parseInt(item8.getText());
	        	maxPrayer = Integer.parseInt(mP.getText());
	        	spell = spells.getSelectedIndex();
	        	fName = file.getText()+".mf";
	        	saveSettings(fName);
	        	frame.dispose();
	       		}
	       	}
	       	);
	       	savePanel.add(saveLbl);
	       	savePanel.add(file);	       	       		       	
	       	savePanel.add(save);
		
		tabbedPane.addTab("Stats",statPanel);
		tabbedPane.addTab("NPCs",npcPanel);
		tabbedPane.addTab("Items",iPanel);
		tabbedPane.addTab("Anti Wander",awPanel);
		tabbedPane.addTab("Anti Lock",alPanel);
		tabbedPane.addTab("Eating",eatPanel);
		tabbedPane.addTab("Bury Bones",bonePanel);
		tabbedPane.addTab("Mage",magePanel);
		tabbedPane.addTab("Save",savePanel);
		
		
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
	                
	        frame.pack();
	        frame.setVisible(true);
	}
			
	public void saveSettings(String fname){
		System.out.println("Writing to file "+fname);
		BufferedWriter out = null;
		try{
			out = new BufferedWriter( new FileWriter(fname, false));
			out.write(IntToStr(cAtt));
			out.newLine();
		        out.write(IntToStr(cDef));
			out.newLine();
		        out.write(IntToStr(cStr));
			out.newLine();
			out.write(IntToStr(radius));
			out.newLine();
			out.write(IntToStr(d1CId));
			out.newLine();
			out.write(IntToStr(d2CId));
			out.newLine();
			out.write(IntToStr(d1D));
			out.newLine();
			out.write(IntToStr(d2D));
			out.newLine();
			out.write(IntToStr(d1XC));
			out.newLine();
			out.write(IntToStr(d1YC));
			out.newLine();
			out.write(IntToStr(d2XC));
			out.newLine();
			out.write(IntToStr(d2YC));
			out.newLine();
			out.write(IntToStr(eAHP));
			out.newLine();
			out.write(IntToStr(fID));
			out.newLine();
			for (int z = 0; z <=3; z++){
				out.write(IntToStr(npcArr[z]));
				out.newLine();
			}
			out.write(IntToStr(fTime));
			out.newLine();
			out.write(IntToStr(wX));
			out.newLine();
			out.write(IntToStr(wY));
			out.newLine();
			for (int z = 0; z <=7; z++){
				out.write(IntToStr(i[z]));
				out.newLine();
			}
			out.write(IntToStr(maxPrayer));
			out.newLine();
			if (aw){
				out.write("1");
				out.newLine();
			}
			else {
				out.write("2");
				out.newLine();
			}
			out.write(IntToStr(gOD));
			out.newLine();
			out.write(IntToStr(gOD2));
			out.newLine();
			out.write(IntToStr(spell));
			out.newLine();
			if (mage)
				out.write("true");
			else
				out.write("false");			
		}
		catch (Exception e)
                	{
                        	System.err.println ("Error writing to file");
                	}
                try {
                	out.flush(); out.close();
                }
                catch (Exception e){
                        	System.err.println ("Error writing to file");
                }
	}
	
	public void loadSettings(){
		settingsFrame = new JFrame("Bruncle's Multi Fighter: Load Settings");
	        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        settingsFrame.setSize(new Dimension(130, 250));
	        JPanel settingsPanel = new JPanel(new GridLayout(6, 2));
	        
	        filName = new JTextField("file",15);
	        JButton load = new JButton("Load from file");
	        JButton start = new JButton("Change settings");
	        load.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		        	readFile(filName.getText());	
		        }
	        });
	        start.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		        	settingsFrame.dispose();
		        	try{
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				}
				catch (Exception e){
					System.out.println("Couldn't change look and feel");
				}
		        	addWidgets();	
		        }
	        });
	        settingsPanel.add(new JLabel("File Name:"));
	        settingsPanel.add(filName);
	        settingsPanel.add(load);
	        settingsPanel.add(start);
	        settingsFrame.getContentPane().add(settingsPanel, BorderLayout.CENTER);	        
	        settingsFrame.pack();
	        settingsFrame.setVisible(true);	        	        
	}
	
	public void readFile(String fname){
			BufferedReader input = null;
			try{
				System.out.println("Reading data from "+fname);
				input = new BufferedReader( new FileReader(fname+".mf") );
				cAtt = Integer.parseInt(input.readLine());
				cDef = Integer.parseInt(input.readLine());
				cStr = Integer.parseInt(input.readLine());
				radius = Integer.parseInt(input.readLine());
				d1CId = Integer.parseInt(input.readLine());
				d2CId = Integer.parseInt(input.readLine());
				d1D = Integer.parseInt(input.readLine());
				d2D = Integer.parseInt(input.readLine());
				d1XC = Integer.parseInt(input.readLine());
				d1YC = Integer.parseInt(input.readLine());
				d2XC = Integer.parseInt(input.readLine());
				d2YC = Integer.parseInt(input.readLine());
				eAHP = Integer.parseInt(input.readLine());
				fID = Integer.parseInt(input.readLine());
				for (int z = 0; z <=3; z++){
					npcArr[z] = Integer.parseInt(input.readLine());
				}
				fTime = Integer.parseInt(input.readLine());
				wX = Integer.parseInt(input.readLine());
				wY = Integer.parseInt(input.readLine());
				for (int z = 0; z <=7; z++){
					i[z] = Integer.parseInt(input.readLine());
				}
				maxPrayer = Integer.parseInt(input.readLine());
				if (Integer.parseInt(input.readLine()) == 1)
					aw = true;
				else
					aw = false;
				gOD = Integer.parseInt(input.readLine());
				gOD2 = Integer.parseInt(input.readLine());
				spell = Integer.parseInt(input.readLine());
				if (input.readLine().equalsIgnoreCase("true")) 
					mage = true;
				else
					mage = false;				
			}
			catch (FileNotFoundException ex) {
	      			JOptionPane.showMessageDialog(null,"Error: Invalid Filename",
	    			"Message Dialog",JOptionPane.PLAIN_MESSAGE);
	    		}
		    	catch (IOException ex){
		      		ex.printStackTrace();
		    	}
		    	finally {
		    	if (input != null){
			      try {
			          input.close();
			      }		  
			      catch (IOException ex) {
			        ex.printStackTrace();
			      }
			}
			}
			settingsFrame.dispose();
			run_script = true;
	}
	
	public void start(String command, String parameter[])
	{
	       		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            	public void run() {
	                	loadSettings();
	            	}
	        	});
	        	while(!run_script && Running())
	            		Wait(100);
	        	if(run_script) 
	        		starttime = System.currentTimeMillis();
	        		minutes = (System.currentTimeMillis() - starttime) / 1000; 
	        		killed = 1;
	            		runScript();	
	}
	
	public int spellId(){
		switch (spell){
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 4;
			case 4:
				return 5;
			case 5:
				return 6;
			case 6:
				return 8;
			case 7:
				return 9; 
			case 8: 
				return 11; //water bolt
			case 9:
				return 14;
			case 10:
				return 16;
			case 11:
				return 19;
			case 12:
				return 22;
			case 13:
				return 26;
			case 14:
				return 31;
		}
		return -1;
	}
	
	public boolean npc(){
		if (nNPC() != -1) 
			return true;
		else 
			return false;
	}
	
	public int nNPC(){	
		return GetNearestNPC(npcArr);
	}		
		
	public boolean sB(){
		if (InvCount(1263) > 0) 
			return true;
		else 
			return false;
	}
	
	public boolean pOB(){
		if (GetX() < x+radius && GetX() > x - radius && GetY() < y +radius && GetY() > x - radius || !aw)
			return false;
		else 
			return true;
	}
	
	public void Kill(){
		boolean inC = false;
		DisplayMessage("Killing time",3);
		if (Running() && !pOB()){
			DisplayMessage("Attacking npc",3);
			long sT = TickCount();
			if (nNPC() !=-1)
				if (!mage){
					AttackNPC(nNPC());
					while(TickCount() - sT <= fTime && !InCombat() && Running()) {
						Wait(1);				
					}
				}
				else {
					MagicNPC(nNPC(), spellId());
					Wait(1200);
				}
			while(InCombat() && Running()) {
				Wait(1);
				inC = true;				
			}
			if (inC){
				killed++;
				inC = false;				
			}
		}
	}
	
	public void Eat(){
		DisplayMessage("Eating",3);
		while (lHP() && Running() && sF()){
			Use(FindInv(fID));
			Wait(1000);
		}
	}	
	
	public void Sleep(){
		slept++;
		DisplayMessage("Sleeping..",3);
		while (!Sleeping() && Running()){
			Use(FindInv(1263));
			Wait(1500);
		}
		while (Sleeping()) Wait(1);
		Wait(500);
	}
	
	public void openGate(){
		new Thread
      		(
         		new Runnable()
         			{
            				public void run()
            				{
               					while (Running())
               						{	
               							if (lO() && Running()){
									DisplayMessage("Opening gate/door..",3);
									if (gOD == 2){
										while(ObjectAt(d1XC,d1YC) == d1CId){
											AtObject(d1XC,d1YC);
											Wait(1000);
										}
										while(ObjectAt(d2XC,d2YC) == d2CId){
											AtObject(d2XC,d2YC);
											Wait(1000);
										}
									}
									if (gOD == 1){
										while(DoorAt(d1XC,d1YC,d1D) == d1CId){
											OpenDoor(d1XC,d1YC,d2D);
											Wait(1000);
										}
										while(DoorAt(d2XC,d2YC,d2D) == d2CId){
											OpenDoor(d2XC,d2YC,d2D);
											Wait(1000);
										}
									}
								}
								if (GetFightMode() != fS && Running()){
										if (fS == 2)
											DisplayMessage("Switching fight mode to accurate",3);
										if (fS == 1)
											DisplayMessage("Switching fight mode to agressive",3);
										if (fS == 3)
											DisplayMessage("Switching fight mode to defensive",3);
										SetFightMode(fS);
								}
								while (pOB() && aw && Running() && !lO()){
               								DisplayMessage("Out of bounds on the full",3);
									WalkNoWait(x,y);
									Wait(400);
								}
							}
					}
		  }).start();
	}
	
	public String cTime(){
		long eTime = TickCount() - starttime;
		long hours = eTime / 1000 / 3600;
		eTime -= (hours * 3600 * 1000);
		long minutes = eTime / 1000 / 60;
		eTime -= (minutes * 60 * 1000); 
		long seconds = eTime / 1000;
		return (Long.toString(hours)+" Hours: "+ Long.toString(minutes) + " Minutes: "+ Long.toString(seconds)+
			" Seconds");
	}
	
	public void showReport(){
				minutes = (System.currentTimeMillis() - starttime) / 60000;
				long seconds = (System.currentTimeMillis() - starttime) / 1000;
				float hourlyExp = 0;
				float hrlyKilled = 0;
				int totalXp = (GetExperience(0)-startAttExp)+ (GetExperience(1)-startDefExp)+(GetExperience(2)-startStrExp);
				if (seconds > 0)
					hourlyExp = (((float)(totalXp)/(float)(seconds))*3600);	
				Println(".-.-.-.-.-.--.-.-.-.-.--.-.-.-.--.-.-.-.-.--.-.-.-.-.--.-.-.-.--.-.-");        	
		        	Println("You have been autoing for " + cTime());
		        	Println("You have killed "+killed+" npcs(types:"+npcArr[0]+","+npcArr[1]+","+npcArr[2]+","+npcArr[3]+")"); //"+npcArr[0]+","+npcArr[1]+","+npcArr[2]+","+npcArr[3]+")
		        	Println("You have slept " + slept+" times");
		        	if (seconds != 0) 
		        		hrlyKilled = (((float)(killed)/(float)(seconds))*3600);
		        		Println("You have killed an average of "+hrlyKilled+" npcs per hour");
		        	Println("Your current stats are "+GetStat(0)+","+GetStat(1)+","+GetStat(2));
		        	Println("Your desired stats are "+cAtt+","+cDef+","+cStr);
		        	Println("You have gained "+(GetExperience(0)-startAttExp)+" attack experience");
		        	Println("You have gained "+(GetExperience(1)-startDefExp)+" defense experience");
		        	Println("You have gained "+(GetExperience(2)-startStrExp)+" strength experience");
		        	Println("You gained an average "+hourlyExp+" exp per hour");
		        	time = TickCount();
	}						
	
	public boolean reportTime(){
		if((TickCount() - time) > (5*60000)){
			return true;
		}
		else 
			return false;
	}			
	
	public int cHP() {		
		return GetCurrentStat(3);	
	}
	
	public boolean sF(){
		if (InvCount(fID) > 0) 
			return true;
		else
			return false;
	}
	
	public boolean lHP(){
		if (cHP() <= eAHP)
			return true;
		else
			return false; 
	}
	
	public boolean sN(){
		if (Fatigue() < 99)
			return false;
		else
			return true;
	}
	public boolean fOB(){
		if (InvCount() == 30 && InvCount(20) > 0)
			return true;
		else
			return false;
	}
	
	public boolean lO(){
		if (gOD == 2 || gOD2 == 2){
				if (ObjectAt(d1XC,d1YC) == d1CId || ObjectAt(d2XC,d2YC) == d2CId)
					return true;
				else
					return false;
		}
		
		if (gOD == 1 || gOD2 == 1){
				if (DoorAt(d1XC,d1YC,d1D) == d1CId || (DoorAt(d2XC,d2YC,d2D) == d2CId))
					return true;
				else
					return false;
		}				
		else return false;
	}
	
	public boolean wFS(){
		if (GetFightMode() != fS)
			return true;
		else 
			return false;
	}
				
	public boolean fT(){
		if (GetStat(0) >= cAtt && GetStat(1) >= cDef && GetStat(2) >= cStr)
			return true;
		else
			return false;
	} 
	
	public boolean fTCS(){
		if (fS == 2){
			if (GetStat(0) >= cAtt)
			 	return true;
			else 
				return false;
		}
		if (fS == 3){
			if (GetStat(1) >= cDef)
			 	return true;
			else 
				return false;
		}
		if (fS == 1){
			if (GetStat(2) >= cStr)
			 	return true;
			else 
				return false;
		}
		else return false;
	}		
	
	public void tDS(){
		if (GetStat(0) < cAtt){
			fS = 2;
			DisplayMessage("Now training attack..",3);
		}
		if (GetStat(0) >= cAtt && GetStat(1) < cDef){
			fS = 3;
			DisplayMessage("Now training defense..",3);
		}
		if (GetStat(0) >= cAtt && GetStat(1) >= cDef && GetStat(2) < cStr){
			fS = 1;
			DisplayMessage("Now training strength..",3);
		}
		
	}
	
	public boolean iOB(int id){
		if (GetNearestItem(id) != null){
			if (GetNearestItem(id)[0] < x+radius && GetNearestItem(id)[0] > x - radius && GetNearestItem(id)[1] < y +radius && GetNearestItem(id)[1] > x - radius)
				return false;
			else
				return true;
		}
		return false;
	}
	
	public void tI(){
		int loop = 0;
		long sT = TickCount();
		while (!npc() && loop < 9){
			int id = i[loop];
			if (GetNearestItem(id)[0] != -1 && !npc() &&!iOB(id)){
				TakeItem(GetNearestItem(id)[0], GetNearestItem(id)[1], id);
				while(TickCount() - sT <= 700 && !npc()) Wait(1);
				DisplayMessage("Taking item "+id+" @ co-ordinates "+GetNearestItem(id)[0]+","+GetNearestItem(id)[1],3);
			}
			loop ++;
							
		}
	}	
	
	public void buryBones(){
		while (GetStat(5) < maxPrayer && !sN() && Running()){
			Use(FindInv(20));
			Wait(1500);
		}
	}
					        
	public void runScript() {
		openGate();
		startAttExp = GetExperience(0);
		startDefExp = GetExperience(1);
		startStrExp = GetExperience(2);
		CheckFighters(true);
		fS = 1;	
		tDS();
		if (aw){
				DisplayMessage("Anti Wander enabled..: radius "+radius,3);
				x = GetX(); y = GetY();
			}
		else
			DisplayMessage("Anti Wander disabled",3);
		time = TickCount();
		starttime = TickCount();
		Wait(500);
		while(Running() && sB() && !fT()){
			if (npc() && Running() && !lHP() && !sN() && !lO() && !wFS() && !fTCS() && !pOB() && !fOB())	
				Kill();			
			if (lHP() && Running())
				Eat();
			if (sN() && Running()) 
				Sleep();
			if (fTCS() && Running())
				tDS();
			if (!npc() && Running())
				tI();
			if (fOB() && Running() && !sN())
				buryBones();
			if (reportTime())
				showReport();
		}
		if (!sB())
			DisplayMessage("..You don't have a sleeping bag..",3);
		else if (!fT()) 
			DisplayMessage("Aww, you're going already?",3);
		else if (fT() && sB())
			DisplayMessage("W00t finished training!",3);
		DisplayMessage("@red@AutoFighter has been stopped..",3);		
	}	
}