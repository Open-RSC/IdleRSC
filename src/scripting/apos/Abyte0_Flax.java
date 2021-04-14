package scripting.apos;
import compatibility.apos.Script;
//Abyte0
//2012-01-18
//Gnome City Bow String Maker

import java.util.Locale;

public class Abyte0_Flax extends Script
{
	private long menu_time;
	private long bank_time;
	int idFlax = 675;
	int idString = 676;
	int idBanker = 540;
	boolean power = false;

	public Abyte0_Flax(String e)
	{}

	public void print(String s)
	{
		System.out.println(s);
	}

	public void init(String param)
	{
		bank_time = -1L;
		menu_time = -1L;
		System.out.println("USAGE Abyte0_Flax nothing = banking OR power = powerflax");
		System.out.println("String Maker for Gnome City");
		System.out.println("Version 0.2 fix too far random bug");

		if(param.equals("power") || param.equals("Power") || param.equals("POWER"))
		{
			power = true;
			System.out.println("Dropping -NOT SUPPORTED YET EXCEPTION-");
		}
		else
		{
			power = false;
			System.out.println("Banking the -Bow String-");
		}
	}

	public int main()
	{
		// sleep if needed
		if(getFatigue() > 90)
		{
			useSleepingBag();
			return 1000;
		}
		if(isBanking())
		{
			bank_time = -1L;
			//On Depose les FLAX
			if(getInventoryCount(idString) > 0)
			{
				deposit(idString,getInventoryCount(idString));
				return random(500, 600);
			}
			//Si il reste uniquement le Bag
			if(getInventoryCount() == 1)
			{
				closeBank();
				return random(500, 600);
			}
			return random(500, 600);
		} else if (bank_time != -1) {
			if (System.currentTimeMillis() >= (bank_time + 8000L)) {
				bank_time = -1L;
			}
			return random(300, 400);
		}
		if(isQuestMenu())
		{
			menu_time = -1L;
			bank_time = System.currentTimeMillis();
			answer(0);
			return random(500, 600);
		} else if (menu_time != -1) {
			if (System.currentTimeMillis() >= (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(300, 400);
		}

		//Si plein de String
		if(getInventoryCount(idString) == 29)
		{
			//si pres des bankers
			if(isAtApproxCoords(715,1452,16))
			{
				int banker[] = getNpcByIdNotTalk(idBanker);
				if(banker[0] != -1)
				{
					menu_time = System.currentTimeMillis();
					talkToNpc(banker[0]);
				}
				return random(600, 800);
			}
			//si Pres du Spinner
			if(isAtApproxCoords(692,1468,16))
			{
				atObject(692, 1469);
				return random(500, 600);
			}
			//si sur le chemin public
			if(isAtApproxCoords(702,522,30))
			{
				if(getX() < 702)
					walkTo(702,522);
				else
					atObject(714,516);
				return 500;
			}
			//print("lost FULL");
			return random(100,200);
		}

		//Si PAS PLEIN
		if(getInventoryCount() < 30)
		{
			//si pres des bankers
			if(isAtApproxCoords(715,1452,16))
			{
				atObject(714,1460);
				return random(400,600);
			}
			//si Pres du Spinner
			if(isAtApproxCoords(692,1468,16))
			{
				atObject(692, 1469);
				return random(500, 600);
			}
			//si sur le chemin public
			if(isAtApproxCoords(702,522,35))///was 30 trying 35 to fix bug
			{
				//print("if i dont move its cause i cant reach 693 524, im at "+getX() + ","+ getY());
				atObject2(698, 521);
				return random(300,400);
			}
			print("lost EMPTY");
			return random(100,200);
		}

		//Si plein de Flax
		if(getInventoryCount(idFlax) == 29 || getInventoryCount(idString) + getInventoryCount(idFlax) == 29)
		{
			if(power)
			{
				if(getInventoryCount(idString) > 0)
					dropItem(getInventoryIndex(idString));
				return random(300,400);
			}
			//si pres des bankers
			if(isAtApproxCoords(715,1452,16))
			{
				atObject(714,1460);
				return random(400,600);
			}
			//si Pres du Spinner
			if(isAtApproxCoords(692,1468,16))
			{
				useItemOnObject(675, 121);
				return random(555, 666);
			}
			//si sur le chemin public
			if(isAtApproxCoords(702,522,30))
			{
				if(getX() > 702)
					walkTo(701,522);
				else
					atObject(692, 525);
				return 500;
			}
			return random(100,200);
		}
		return 100;
	}

	@Override
	public void onServerMessage(String str) {
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("busy")) {
			menu_time = -1L;
		}
	}
} 