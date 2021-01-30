import compatibility.sbot.Script;

public class LeetFletcher2 extends Script {
    public String[] getCommands() {
        return new String[]{"fletch"};
    }
    
    public void Banke() {
        while (!Bank() && Running()) {
            while (!QuestMenu()&& Running()) {
                int BankerID = GetNearestNPC(95);
                TalkToNPC(BankerID);
                long Time = System.currentTimeMillis();
                while (System.currentTimeMillis() - Time <= 2000 && !QuestMenu() && Running())
                    Wait(1);
            }
            Answer(0);
            long Time = System.currentTimeMillis();
            while (System.currentTimeMillis() - Time <= 5000 && !Bank() && Running())
                Wait(1);
        }
    }
    
    public void ChopOak() {
        while (Running() == true && InvCount(632) < 26 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            int OakCoords[] = GetNearestObject(306);
            AtObject(OakCoords[0],OakCoords[1]);
            Wait(800);
        }
    }
    
    public void CutOakShort() {
        while (Running() == true && InvCount(632) > 0 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            UseWithInventory(FindInv(13),FindInv(632));
            Wait(1000);
            if (QuestMenu() && Running() == true) {
                Answer(0);
            }
        }
    }
    
    public void TakeOakShort() {
        while (Running() == true && InvCount(649) < 13) {
            Withdraw(649,1);
            Wait(100);
        }
    }
    
    public void MakeOakShort() {
        while (Running() == true && InvCount(649) > 0 && InvCount(676) > 0) {
            UseWithInventory(FindInv(649),FindInv(676));
            Wait(100);
        }
    }
    
    public void CutOakLong() {
        while (Running() == true && InvCount(632) > 0 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            UseWithInventory(FindInv(13),FindInv(632));
            Wait(1000);
            if (QuestMenu() && Running() == true) {
                Answer(1);
            }
        }
    }
    
    public void TakeOakLong() {
        while (Running() == true && InvCount(648) < 13) {
            Withdraw(648,1);
            Wait(100);
        }
    }
    
    public void MakeOakLong() {
        while (Running() == true && InvCount(648) > 0 && InvCount(676) > 0 && Fatigue() < 97 && Sleeping() == false) {
            UseWithInventory(FindInv(648),FindInv(676));
            Wait(100);
        }
    }
    
    
    public void ChopWillow() {
        while (Running() == true && InvCount(633) < 26 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            int WillowCoords[] = GetNearestObject(307);
            AtObject(WillowCoords[0],WillowCoords[1]);
            Wait(800);
        }
    }
    
    public void CutWillowShort() {
        while (Running() == true && InvCount(633) > 0 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            UseWithInventory(FindInv(13),FindInv(633));
            Wait(1000);
            if (QuestMenu() && Running() == true) {
                Answer(0);
            }
        }
    }
    
    public void TakeWillowShort() {
        while (Running() == true && InvCount(651) < 13) {
            Withdraw(651,1);
            Wait(100);
        }
    }
    
    public void MakeWillowShort() {
        while (Running() == true && InvCount(651) > 0 && InvCount(676) > 0 && Fatigue() < 97 && Sleeping() == false) {
            UseWithInventory(FindInv(651),FindInv(676));
            Wait(100);
        }
    }
    
    public void CutWillowLong() {
        while (Running() == true && InvCount(633) > 0 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            UseWithInventory(FindInv(13),FindInv(633));
            Wait(1000);
            if (QuestMenu() && Running() == true) {
                Answer(1);
            }
        }
    }
    
    public void TakeWillowLong() {
        while (Running() == true && InvCount(650) < 13) {
            Withdraw(650,1);
            Wait(100);
        }
    }
    
    public void MakeWillowLong() {
        while (Running() == true && InvCount(650) > 0 && InvCount(676) > 0 && Fatigue() < 97 && Sleeping() == false) {
            UseWithInventory(FindInv(650),FindInv(676));
            Wait(100);
        }
    }
    
    public void ChopMaple() {
        while (Running() == true && InvCount(634) < 26 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            int MapleCoords[] = GetNearestObject(308);
            AtObject(MapleCoords[0],MapleCoords[1]);
            Wait(800);
        }
    }
    
    public void CutMapleShort() {
        while (Running() == true && InvCount(634) > 0 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            UseWithInventory(FindInv(13),FindInv(634));
            Wait(1000);
            if (QuestMenu() && Running() == true) {
                Answer(0);
            }
        }
    }
    
    public void TakeMapleShort() {
        while (Running() == true && InvCount(653) < 13) {
            Withdraw(653,1);
            Wait(100);
        }
    }
    
    public void MakeMapleShort() {
        while (Running() == true && InvCount(653) > 0 && InvCount(676) > 0 && Fatigue() < 97 && Sleeping() == false) {
            UseWithInventory(FindInv(653),FindInv(676));
            Wait(100);
        }
    }
    
    public void CutMapleLong() {
        while (Running() == true && InvCount(634) > 0 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            UseWithInventory(FindInv(13),FindInv(634));
            Wait(1000);
            if (QuestMenu() && Running() == true) {
                Answer(1);
            }
        }
    }
    
    public void TakeMapleLong() {
        while (Running() == true && InvCount(652) < 13) {
            Withdraw(652,1);
            Wait(100);
        }
    }
    
    public void MakeMapleLong() {
        while (Running() == true && InvCount(652) > 0 && InvCount(676) > 0 && Fatigue() < 97 && Sleeping() == false) {
            UseWithInventory(FindInv(652),FindInv(676));
            Wait(100);
        }
    }
    
    public void ChopYew() {
        while (Running() == true && InvCount(635) < 26 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            int YewCoords[] = GetNearestObject(309);
            AtObject(YewCoords[0],YewCoords[1]);
            Wait(800);
        }
    }
    
    public void CutYewShort() {
        while (Running() == true && InvCount(635) > 0 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            UseWithInventory(FindInv(13),FindInv(635));
            Wait(1000);
            if (QuestMenu() && Running() == true) {
                Answer(0);
            }
        }
    }
    
    public void TakeYewShort() {
        while (Running() == true && InvCount(655) < 13) {
            Withdraw(655,1);
            Wait(100);
        }
    }
    
    public void MakeYewShort() {
        while (Running() == true && InvCount(655) > 0 && InvCount(676) > 0 && Fatigue() < 97 && Sleeping() == false) {
            UseWithInventory(FindInv(655),FindInv(676));
            Wait(100);
        }
    }
    
    public void CutYewLong() {
        while (Running() == true && InvCount(635) > 0 && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() > 95) {
                Use(FindInv(1263));
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            UseWithInventory(FindInv(13),FindInv(635));
            Wait(1000);
            if (QuestMenu() && Running() == true) {
                Answer(1);
            }
        }
    }
    
    public void TakeYewLong() {
        while (Running() == true && InvCount(654) < 13) {
            Withdraw(654,1);
            Wait(100);
        }
    }
    
    public void MakeYewLong() {
        while (Running() == true && InvCount(654) > 0 && InvCount(676) > 0 && Fatigue() < 97 && Sleeping() == false) {
            UseWithInventory(FindInv(654),FindInv(676));
            Wait(100);
        }
    }
    
    public void TakeMagicShort() {
        while (Running() == true && InvCount(657) < 13) {
            Withdraw(657,1);
            Wait(100);
        }
    }
    
    public void MakeMagicShort() {
        while (Running() == true && InvCount(657) > 0 && InvCount(676) > 0 && Fatigue() < 97 && Sleeping() == false) {
            UseWithInventory(FindInv(657),FindInv(676));
            Wait(100);
        }
    }
    
    public void TakeMagicLong() {
        while (Running() == true && InvCount(656) < 13) {
            Withdraw(656,1);
            Wait(100);
        }
    }
    
    public void MakeMagicLong() {
        while (Running() == true && InvCount(656) > 0 && InvCount(676) > 0 && Fatigue() < 97 && Sleeping() == false) {
            UseWithInventory(FindInv(656),FindInv(676));
            Wait(100);
        }
    }
    
    public void WalkTrees() {
        while (Running() == true) {
            if (ObjectAt(500,454) != 63 && Running() == true) {
                AtObject(500,454);
                Wait(1000);
            }
            if (Running() == true) {
                Walk(503,455);
            }
            if (Running() == true) {
                Walk(508,451);
            }
            if (Running() == true) {
                Walk(510,447);
            }
            if (Running() == true) {
                Walk(512,455);
            }
            if (Running() == true) {
                Walk(500,453);
            }
        }
    }
    
    public void WalkToBank() {
        while (Running() == true) {
            if (Running() == true) {
                Walk(508,451);
            }
            if (Running() == true) {
                Walk(504,455);
            }
            if (Running() == true) {
                Walk(500,454);
            }
            if (ObjectAt(500,454) != 63 && Running() == true) {
                AtObject(500,454);
                Wait(1000);
            }
        }
    }
    
    
    public void WalkYew() {
        while (Running() == true) {
            if (ObjectAt(500,454) != 63 && Running() == true) {
                AtObject(500,454);
                Wait(1000);
            }
            if (Running() == true) {
                Walk(506,457);
            }
            if (Running() == true) {
                Walk(513,463);
            }
            if (Running() == true) {
                Walk(520,465);
            }
            if (Running() == true) {
                Walk(520,470);
            }
            if (Running() == true) {
                Walk(518,474);
            }
        }
    }
    
    public void BankYew() {
        while (Running() == true) {
            if (Running() == true) {
                Walk(520,467);
            }
            if (Running() == true) {
                Walk(513,464);
            }
            if (Running() == true) {
                Walk(506,459);
            }
            if (Running() == true) {
                Walk(501,455);
            }
            if (Running() == true) {
                Walk(500,454);
            }
            if (ObjectAt(500,454) != 63 && Running() == true) {
                AtObject(500,454);
                Wait(1000);
            }
        }
    }
    
    public void WalkBank() {
        while (Running() == true) {
            AtObject(525,1406);
            Wait(1000);
            if (Running() == true) {
                Walk(522,465);
            }
            while (DoorAt(522,465,1) == 2 && Running() == true) {
                OpenDoor(522,465,1);
                Wait(1000);
            }
            if (Running() == true) {
                Walk(520,461);
            }
            if (Running() == true) {
                Walk(513,457);
            }
            if (Running() == true) {
                Walk(505,455);
            }
            if (Running() == true) {
                Walk(500,454);
            }
            while (ObjectAt(500,454) != 63 && Running() == true) {
                AtObject(500,454);
                Wait(1000);
            }
            if (Running() == true) {
                Walk(500,453);
            }
        }
    }
    
    public void WalkFlax() {
        while (Running() == true) {
            if (ObjectAt(500,454) != 63 && Running() == true) {
                AtObject(500,454);
                Wait(1000);
            }
            if (Running() == true) {
                Walk(492,459);
            }
            if (Running() == true) {
                Walk(489,463);
            }
            if (Running() == true) {
                Walk(491,481);
            }
            if (Running() == true) {
                Walk(490,487);
            }
        }
    }
    
    public void PickFlax() {
        while (Running() == true && (InvCount(675) < 26)) {
            int FlaxCoords[] = GetNearestObject(313);
            AtObject2(FlaxCoords[0],FlaxCoords[1]);
            Wait(100);
        }
    }
    
    public void WalkSpin() {
        while (Running() == true) {
            if (Running() == true) {
                Walk(497,478);
            }
            if (Running() == true) {
                Walk(503,470);
            }
            if (Running() == true) {
                Walk(509,464);
            }
            if (Running() == true) {
                Walk(515,462);
            }
            if (Running() == true) {
                Walk(521,465);
            }
            while (DoorAt(522,465,1) == 2 && Running() == true) {
                OpenDoor(522,465,1);
                Wait(1000);
            }
            if (Running() == true) {
                Walk(525,463);
            }
            AtObject(525,462);
            Wait(1000);
        }
    }
    
    public void SpinFlax() {
        while (Running() == true && (InvCount(675) < 0) && Fatigue() < 97 && Sleeping() == false) {
            while (Running() == true && Sleeping() == false && Fatigue() < 95) {
                AtObject(524,1410);
                Wait(1000);
            }
            while (Running() == true && Sleeping() == true) {
                Wait(100);
            }
            int SpinCoords[] = GetNearestObject(121);
            UseOnObject(SpinCoords[0],SpinCoords[1],2);
            Wait(200);
        }
    }
    
    
    public void DepositFlax() {
        while (Running() == true && InvCount(676) > 13) {
            Deposit(676,1);
            Wait(100);
        }
    }
    
    public void TakeFlax() {
        while (Running() == true && InvCount(676) < 13) {
            Withdraw(676,1);
            Wait(150);
        }
    }
    
    public void BankBows() {
        while (InvCount(648) > 0); {
            Deposit(648,25);
            Deposit(648,1);
            Deposit(648,1);
            Wait(500);
        }
        while (InvCount(649) > 0); {
            Deposit(649,25);
            Deposit(649,1);
            Deposit(649,1);
            Wait(500);
        }
        while (InvCount(650) > 0); {
            Deposit(650,25);
            Deposit(650,1);
            Deposit(650,1);
            Wait(500);
        }
        while (InvCount(651) > 0); {
            Deposit(651,25);
            Deposit(651,1);
            Deposit(651,1);
            Wait(500);
        }
        while (InvCount(652) > 0); {
            Deposit(652,25);
            Deposit(652,1);
            Deposit(652,1);
            Wait(500);
        }
        while (InvCount(653) > 0); {
            Deposit(653,25);
            Deposit(653,1);
            Deposit(653,1);
            Wait(500);
        }
        while (InvCount(654) > 0); {
            Deposit(654,25);
            Deposit(654,1);
            Deposit(654,1);
            Wait(500);
        }
        while (InvCount(655) > 0); {
            Deposit(655,25);
            Deposit(655,1);
            Deposit(655,1);
            Wait(500);
        }
        while (InvCount(656) > 0); {
            Deposit(656,25);
            Deposit(656,1);
            Deposit(656,1);
            Wait(500);
        }
        while (InvCount(657) > 0); {
            Deposit(657,25);
            Deposit(657,1);
            Deposit(657,1);
            Wait(500);
        }
        CloseBank();
    }
    
    public void BankUnstrungBows() {
        while (InvCount(658) > 0); {
            Deposit(658,25);
            Deposit(658,1);
            Deposit(658,1);
            Wait(500);
        }
        while (InvCount(659) > 0); {
            Deposit(659,25);
            Deposit(659,1);
            Deposit(659,1);
            Wait(500);
        }
        while (InvCount(660) > 0); {
            Deposit(660,25);
            Deposit(660,1);
            Deposit(660,1);
            Wait(500);
        }
        while (InvCount(661) > 0); {
            Deposit(661,25);
            Deposit(661,1);
            Deposit(661,1);
            Wait(500);
        }
        while (InvCount(662) > 0); {
            Deposit(662,25);
            Deposit(662,1);
            Deposit(662,1);
            Wait(500);
        }
        while (InvCount(663) > 0); {
            Deposit(663,25);
            Deposit(663,1);
            Deposit(663,1);
            Wait(500);
        }
        while (InvCount(664) > 0); {
            Deposit(664,25);
            Deposit(664,1);
            Deposit(664,1);
            Wait(500);
        }
        while (InvCount(665) > 0); {
            Deposit(665,25);
            Deposit(665,1);
            Deposit(665,1);
            Wait(500);
        }
        while (InvCount(666) > 0); {
            Deposit(666,25);
            Deposit(666,1);
            Deposit(666,1);
            Wait(500);
        }
        while (InvCount(667) > 0); {
            Deposit(667,25);
            Deposit(667,1);
            Deposit(667,1);
            Wait(500);
        }
        CloseBank();
    }
    public void start(String command, String parameter[]) {
        DisplayMessage("@ora@*** @whi@Seer's Village Fletcher @ora@***",3);
        DisplayMessage("@ora@ *** @whi@Created By: AssPirate@ora@***",3);
        DisplayMessage("@ora@  *** @whi@ Version 1.0 9/14 @ora@***",3);
        while (Running()) {
            while (GetStat(8) > 0 && GetStat(8) < 15 && Running()) {
                while (GetStat(9) > 0  && GetStat(9) < 5 && Running()) {
                }
                while (GetStat(9) > 4 && GetStat(9) < 10 && Running()) {
                }
                while (GetStat(9) > 9 && GetStat(9) < 20 && Running()) {
                }
            }
            while (GetStat(8) > 14 && GetStat(8) < 30 && Running()) {
                while (GetStat(9) > 19 && GetStat(9) < 25 && Running()) {
                    WalkTrees();
                    ChopOak();
                    CutOakShort();
                    WalkToBank();
                    Banke();
                    BankUnstrungBows();
                    WalkFlax();
                    PickFlax();
                    WalkSpin();
                    SpinFlax();
                    WalkBank();
                    Banke();
                    DepositFlax();
                    TakeOakShort();
                    MakeOakShort();
                    Banke();
                    BankBows();
                    TakeFlax();
                    TakeOakShort();
                    MakeOakShort();
                    Banke();
                    BankBows();
                }
                while (GetStat(9) > 24 && GetStat(9) < 30 && Running()) {
                    WalkTrees();
                    ChopOak();
                    CutOakLong();
                    WalkToBank();
                    Banke();
                    BankUnstrungBows();
                    WalkFlax();
                    PickFlax();
                    WalkSpin();
                    SpinFlax();
                    WalkBank();
                    Banke();
                    DepositFlax();
                    TakeOakLong();
                    MakeOakLong();
                    Banke();
                    BankBows();
                    TakeFlax();
                    TakeOakLong();
                    MakeOakLong();
                    Banke();
                    BankBows();
                }
            }
            while (GetStat(8) > 29 && GetStat(8) < 45 && Running()) {
                while (GetStat(9) > 34 && GetStat(9) < 40 && Running()) {
                    WalkTrees();
                    ChopWillow();
                    CutWillowShort();
                    WalkToBank();
                    Banke();
                    BankUnstrungBows();
                    WalkFlax();
                    PickFlax();
                    WalkSpin();
                    SpinFlax();
                    WalkBank();
                    Banke();
                    DepositFlax();
                    TakeWillowShort();
                    MakeWillowShort();
                    Banke();
                    BankBows();
                    TakeFlax();
                    TakeWillowShort();
                    MakeWillowShort();
                    Banke();
                    BankBows();
                }
                while (GetStat(9) > 39 && GetStat(9) < 50 && Running()) {
                    WalkTrees();
                    ChopWillow();
                    CutWillowLong();
                    WalkToBank();
                    Banke();
                    BankUnstrungBows();
                    WalkFlax();
                    PickFlax();
                    WalkSpin();
                    SpinFlax();
                    WalkBank();
                    Banke();
                    DepositFlax();
                    TakeWillowLong();
                    MakeWillowLong();
                    Banke();
                    BankBows();
                    TakeFlax();
                    TakeWillowLong();
                    MakeWillowLong();
                    Banke();
                    BankBows();
                }
            }
            while (GetStat(8) > 44 && GetStat(8) < 60 && Running()) {
                while (GetStat(9) > 49 && GetStat(9) < 55 && Running()) {
                    
                }
                while (GetStat(9) > 54 && GetStat(9) < 65 && Running()) {
                    
                }
            }
            while (GetStat(8) > 59 && GetStat(8) < 75 && Running()) {
                while (GetStat(9) > 64 && GetStat(9) > 70 && Running()) {
                    WalkYew();
                    ChopYew();
                    CutYewShort();
                    WalkToBank();
                    Banke();
                    BankUnstrungBows();
                    WalkFlax();
                    PickFlax();
                    WalkSpin();
                    SpinFlax();
                    WalkBank();
                    Banke();
                    DepositFlax();
                    TakeYewShort();
                    MakeYewShort();
                    Banke();
                    BankBows();
                    TakeFlax();
                    TakeYewShort();
                    MakeYewShort();
                    Banke();
                    BankBows();
                }
                while (GetStat(9) > 69 && GetStat(9) < 80 && Running()) {
                    WalkYew();
                    ChopYew();
                    CutYewLong();
                    WalkToBank();
                    Banke();
                    BankUnstrungBows();
                    WalkFlax();
                    PickFlax();
                    WalkSpin();
                    SpinFlax();
                    WalkBank();
                    Banke();
                    DepositFlax();
                    TakeYewLong();
                    MakeYewLong();
                    Banke();
                    BankBows();
                    TakeFlax();
                    TakeYewLong();
                    MakeYewLong();
                    Banke();
                    BankBows();
                }
            }
            while (GetStat(8) > 74 && GetStat(8) < 100 && Running()) {
                while (GetStat(9) > 79 && GetStat(9) < 85 && Running()) {
                }
                while (GetStat(9) > 84 && GetStat(9) < 100 && Running()) {
                }
            }
            
        }
        DisplayMessage("@red@Script Finished", 3);
    }
}