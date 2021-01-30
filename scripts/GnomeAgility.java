import compatibility.sbot.Script;

public class GnomeAgility extends Script 
{
    public String[] getCommands()
    {
        return new String[]{"agility"}; 
    }
	public void ServerMessage(String message)
	{
	}
	int TotalStartXP = 0;
	int StartLevel = 0;
	int TripStartXP = 0;
	int BagCount = 0;
	int Trips = 1;
	long StartTime = 0;
    public void start(String command, String parameter[])
    {
		StartSleeper();
		TotalStartXP = GetExperience(16);
		StartLevel = GetStat(16);
		StartTime = (long)((int)(TickCount() / 1000));
		Println("##### Start Agility Experience: " + TotalStartXP + " (" + StartLevel + ")");
		while (Running())
		{
			
			Println("#### Gnome Agility Course - RichyT");
			Println("### " + ((long)((int)(TickCount() / 1000)) - StartTime) + " seconds have passed");
			Println("### Trip Number: " + Trips);
			Println("### Experience Gained Last Trip: " + (GetExperience(16) - TripStartXP));
			Println("### Experience Gained So Far: " + (GetExperience(16) - TotalStartXP));
			Println("### Levels Gained So Far: " + (GetStat(16) - StartLevel));
			Println("### Number of times used the sleeping bag so far: " + BagCount);
			TripStartXP = GetExperience(16);
			Trips++;

			Println("## Crossing Log...");
			ForceWalk(692,494);
			CrossLog();

			Println("## Climbing Net...");
			ForceWalk(692,502);
			ClimbNet();

			Println("## Climbing WatchTower...");
			ForceWalk(693,1450);
			ClimbWatchTower();

			Println("## Swinging...");
			ForceWalk(690,2395);
			Swing();

			Println("## Climbing Down...");
			ClimbDown();
			Wait(1000);

			Println("## Climbing Second Net...");
			ForceWalk(683,503);
			ClimbSecondNet();

			Println("## Entering Pipe...");
			ForceWalk(683,498);
			EnterPipe();

			Println("#### Heading back to the start");
			ForceWalk(687,494);
			ForceWalk(692,494);
		}
		Println("#### Script Ended ####");
    }
	public void EnterPipe()
	{
		while (GetY() > 494)
		{
			AtObject(683,497);
			long T = TickCount();
			while (GetY() > 494 && TickCount() - T < 8000)
				Wait(10);
		}
	}
	public void ClimbSecondNet()
	{
		while (GetY() > 501)
		{
			AtObject(683,502);
			long T = TickCount();
			while (GetY() > 501 && TickCount() - T < 8000)
				Wait(10);
		}
	}
	public void ClimbDown()
	{
		while (GetY() != 506)
		{
			AtObject(683,2396);
			long T = TickCount();
			while (GetY() != 506 && TickCount() - T < 2000)
				Wait(10);
		}
	}
	public void Swing()
	{
		while (GetX() != 685)
		{
			AtObject(689,2395);
			long T = TickCount();
			while (GetX() != 685&& TickCount() - T < 8000)
				Wait(10);
		}
	}
	public void ClimbWatchTower()
	{
		while (GetY() != 2394)
		{
			AtObject(693,1452);
			long T = TickCount();
			while (GetY() != 2394 && TickCount() - T < 8000)
				Wait(10);
		}
	}
	public void ClimbNet()
	{
		while (GetY() != 1448)
		{
			AtObject(692,503);
			long T = TickCount();
			while (GetY() != 1448 && TickCount() - T < 8000)
				Wait(10);
		}
	}
	public void CrossLog()
	{
		while (GetY() != 499)
		{
			AtObject(692,495);
			long T = TickCount();
			while (GetY() != 499 && TickCount() - T < 5000)
				Wait(10);
		}
	}
	public void StartSleeper()
	{
		new Thread
		(
			new Runnable()
			{
				public void run()
				{
					while (Running())
					{
						if (Fatigue() > 95 && !Sleeping())
						{
							Println("Fatigue is " + Fatigue() + ", using sleeping bag");
							Use(FindInv(1263));
							BagCount++;
							Wait(5000);
						}
						Wait(250);
					}
				}		
		}).start();
	}
}
