package scripting.apos;
import compatibility.apos.Script;

public class Abyte0_FallyMiner extends Script
{    int oreID = 151;
	// Iron    
	int banker = 95; // Banker    // uncut gem id's    
	int gem1 = 160; // sapph   
	 int gem2 = 159; // emerald    
	int gem3 = 158; // ruby    
	int gem4 = 157; // diamond
	int fMode = 3;
	int mined;
	int cptTry = 0;
	int cptInventaireToBank = 30;
	public Abyte0_FallyMiner(String e)
	{
		//super(e);
	}    
	public void init( String params )
	{
		mined = 0;
		print("Started Abyte0 Fally Miner");
		print("Version 1.0");
		//Version 0 by ???
		//Version 1 by Abyte0
		//2  Ways doors opener
		//Bank OR Power Mine iron
		//Walk after random click to avoid some logout 5 minutes (mostly powermining)
		print("No param = Bank , Anything as param = Power Mining...");
		print("Fmode = " + fMode + " ,if you want a different fmode, change it in code");
		if(params.equals(""))
			cptInventaireToBank = 30;
		else
			cptInventaireToBank = 60;
	}    
	public int main()
	{
		return Mine();
	}    
	public int Mine()
	{
		if(getFightMode() != fMode)
		{
			setFightMode(fMode);
		}        
		if(getFatigue() > 90)
		{
			useSleepingBag();
			return 1000;
		}
		if(isBanking())
		{            
			// Deposit gems and ores            
			if(getInventoryCount(gem4) > 0)
			{                
				deposit(gem4,1);
				return 1000;
			}            
			if(getInventoryCount(gem3) > 0)
			{               
				deposit(gem3,1);
				return 1000;
			}            
			if(getInventoryCount(gem2) > 0)
			{                
				deposit(gem2,1);
				return 1000;
			}            
			if(getInventoryCount(gem1) > 0)
			{
				deposit(gem1,1);                
				return 1000;
			}            
			if(getInventoryCount(oreID) > 0)
			{
				mined += getInventoryCount(oreID);                
				deposit(oreID,getInventoryCount(oreID));
				print("Mined "+mined+" iron ore so far");
				return 1000;
			}              
			closeBank();
			return 1000;
		}
		if(isQuestMenu())
		{
			answer(0);
			return 1000 + random(300, 1200);
		}        
		// Open door       
		//if(distanceTo(getX(),getY(),287,571) < 5)            
		//	if(getObjectById(64).length > 0)
		//		atObject(getObjectById(64)[1],getObjectById(64)[2]);
		// Talk to banker        
		//if(distanceTo(286,571) < 6 && getInventoryCount() > 2)
		//{            
		//	talkToNpc(getNpcById(banker)[0]);
		//	return 2002 + random(100, 100);
		//} 
		if(getInventoryCount() == cptInventaireToBank)
		{
			if(getY() > 635)
			{                
				walkTo(313,635);
				return 1000 + random(300, 1200);
			}            
			if(getY() > 612)
			{
				walkTo(306,612);
				return 1000 + random(300, 1200);
			}
			if(getY() > 593)
			{
				print("Walking to 293,593");
				walkTo(293,593);
				return 1000 + random(300, 1200);
			}
			if(getY() > 583)
			{
				print("Walking to 292,583");
				walkTo(292,583);
				return 1000 + random(300, 1200);
			}
			if(getX() == 287 && getY() == 571)
			{	
				//Si a coter de la porte a exterieur
				print("Step Inside Bank");	
				atObject(287,571);
				walkTo(286,571);
				return random(100, 1500);
			}
			if(getX() >= 280 && getX() <= 286 && getY() >= 564 && getY() <= 573)
			{
				//Si dans la banque
				print("Talking to Banker");											
				if(!isBanking())
				{					
					int banker[] = getNpcByIdNotTalk(new int[]{95});										
					if (banker[0] != -1 && !isBanking())
					{						
						talkToNpc(banker[0]);						
						return random(2000,3000);
					}
				}	
				return random(231, 1500);
			}
			print("Walking to 287,571");
			walkTo(287,571);
			return 1000 + random(300, 1200);
		}
		else
		{
			if(getX() == 286 && getY() == 571)
			{	
				//Si a coter de la porte a linterieur
				print("Step Outside Bank");	
				atObject(287,571);
				walkTo(287,571);
				return random(121, 3500);
			}
			if(getX() >= 280 && getX() <= 286 && getY() >= 564 && getY() <= 573)
			{
				//Si dans la banque
				print("Walking to Door");	
				walkTo(286,571);
				return random(240, 2500);
			}
			if(getY() < 580)
			{
				walkTo(290,580);
				return 1000 + random(300, 1200);
			}
			if(getY() < 593)
			{
				walkTo(293,593);
				return 1000 + random(300, 1200);
			}            
			if(getY() < 612)
			{
				walkTo(306,612);                
				return 1000 + random(300, 1200);
			}
			if(getY() < 635)
			{                
				walkTo(313,635);
				return 1000 + random(300, 1200);
			}
			if(getY() < 641)
			{                
				walkTo(318,641);                
				return 1000 + random(300, 1200);
			}
			if(getX() != 318 && getY() != 641)
			{
				walkTo(318,641);
				print("Walking to rocks!");
				return random(1300, 2400);
			}
			if(distanceTo(318,641) < 5)
			{
				if(cptTry++ >= random(80,130))
				{
					//Si on a beaucoup dessaie on bouge pas loguer out
					walkTo(317,641);
					print("Moving because " + cptTry + " trys...");
					cptTry = 0;
					return random(1003,4221);
				}
				int[] rock = getObjectById(102);
				if(rock.length > 0 && distanceTo(318,641,rock[1],rock[2]) < 5) 
				{                    
					atObject(rock[1],rock[2]);
					cptTry++;
				}                
				return 600 + random(300, 1200);          
			}
			return random(400,1103);
		}
	}
	
	public final void print(String gameText)
	{
		System.out.println(gameText);
	}
}