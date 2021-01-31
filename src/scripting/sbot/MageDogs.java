package scripting.sbot;

import compatibility.sbot.Script;
public class MageDogs extends Script 
{

    public String[] getCommands()
    {
        return new String[]{"magedogs"}; 
    }

	int HP = 0;
	int BagCount = 0;
	int ShootCount = 0;
    public void start(String command, String parameter[])
    {
		Println("##### Start Magic Experience: " + GetExperience(6) + " (" + GetStat(6) + ")");
		while (Running())
			FightGuards();
		Println("#### Script Ended ####");
    }
	public void FightGuards()
	{
		if (Fatigue() < 90 && !Sleeping())
		{
			if (!InCombat() && Fatigue() < 90)
			{
				int ID = GetNearestNPC(262);
				if (ID == -1 || GetX() > 574 || GetX() < 563)
					ForceWalk(573,581);
				else {
					if (NPCX(ID) > 100)
					{
						MagicNPC(ID,6);
						long T = TickCount();
						while (!InCombat() && TickCount() - T < Rand(1500,1750))
							Wait(1);
						ShootCount++;
						Println("## Maging dog number: " + ShootCount);
					}
				}
			} else
				Wait(1);
		} else {
			if (!Sleeping())
			{
				Println("#### Fatigue is " + Fatigue() + ", using sleeping bag");
				Use(FindInv(1263));
				BagCount++;
				Wait(5000);
			}
			Wait(1);
		}
	}
}
