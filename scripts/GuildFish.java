import compatibility.sbot.Script;

public class GuildFish extends Script
{

    public String[] getCommands()
    {
        return (new String[] {
            "guild"
        });
    }

    public void start(String s, String as[])
    {
        DisplayMessage("@cya@Personal tells you: This script owns, guild fish,cook,cert started!", 3);
        Wait(1000);
        for(; Running(); WalkNoWait(590, 502))
        {
            int i = Rand(1, 2);
            do
            {
                if(InvCount() >= 30 || !Running())
                    break;
                if(Fatigue() >= 86)
                {
                    for(; !Sleeping(); Wait(2000))
                        Use(FindInv(1263));

                    for(; Sleeping(); Wait(2000));
                }
                if(i == 1)
                {
                    AtObject(589, 501);
                    Wait(400);
                    if(InLastServerMessage("You have been standing"))
                    {
                        Wait(2000);
                        Walk(587, 502);
                        Wait(1000);
                        ResetLastServerMessage();
                    }
                } else
                {
                    WalkNoWait(591, 502);
                    WalkNoWait(588, 502);
                    AtObject(588, 500);
                    Wait(400);
                    if(InLastServerMessage("You have been standing"))
                    {
                        Wait(2000);
                        Walk(587, 502);
                        Wait(1000);
                        ResetLastServerMessage();
                    }
                }
            } while(true);
            WalkNoWait(586, 509);
            WalkNoWait(586, 515);
            WalkNoWait(586, 518);
            OpenDoor(586, 519, 0);
            WalkNoWait(583, 519);
            for(; InvCount(372) > 0 && Running(); Wait(800))
            {
                if(Fatigue() >= 100)
                {
                    for(; !Sleeping(); Wait(2000))
                        Use(FindInv(1263));

                    for(; Sleeping(); Wait(2000));
                }
                UseOnObject(583, 520, FindInv(372));
            }

            for(; InvCount(374) > 0 && Running(); Wait(1000))
                Drop(FindInv(374));

            WalkNoWait(586, 519);
            OpenDoor(586, 519, 0);
            WalkNoWait(589, 517);
            WalkNoWait(591, 515);
            WalkNoWait(594, 513);
            WalkNoWait(596, 511);
            WalkNoWait(598, 509);
            WalkNoWait(601, 507);
            WalkNoWait(602, 506);
            OpenDoor(603, 506, 3);
            WalkNoWait(603, 503);
            for(; !QuestMenu(); Wait(2000))
                TalkToNPC(369);

            Answer(1);
            for(; !QuestMenu(); Wait(2000));
            Answer(0);
            for(; !QuestMenu(); Wait(2000));
            if(InvCount(373) <= 9)
                Answer(0);
            else
            if(InvCount(373) <= 14)
                Answer(1);
            else
            if(InvCount(373) <= 19)
                Answer(2);
            else
            if(InvCount(373) <= 24)
                Answer(3);
            else
                Answer(4);
            Wait(500);
            for(; InvCount(373) > 5; Wait(100));
            OpenDoor(603, 506, 3);
            WalkNoWait(600, 503);
            WalkNoWait(597, 502);
            WalkNoWait(594, 502);
        }

        DisplayMessage("@cya@Personal tells you: Pretty uber right?? @ora@script terminated.", 3);
    }
}