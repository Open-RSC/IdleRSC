package scripting.apos;
import compatibility.apos.Script;
//Abyte0
//2012-01-25
//Make Oak,Willow,Yew,Magic Longbows

/*
To Cut tree + Cut Log + Bank Untrung
abyte0_fletch oak
abyte0_fletch willow
abyte0_fletch yew
abyte0_fletch magic

To Withdraw Untring Add String Bank Bows
abyte0_fletch o
abyte0_fletch w
abyte0_fletch y
abyte0_fletch m
*/

public class Abyte0_Fletch extends Script 
{    
    int oakTree = 306;
    int oakLog = 632;
    int oakLongBow = 648;
    int oakLongBowU = 658;
    
    int willowTree = 307;
    int willowLog = 633;
    int willowLongBow = 650;
    int willowLongBowU = 660;
    
    int yewTree = 309;
    int yewLog = 635;
    int yewLongBow = 654;
    int yewLongBowU = 664;
    
    int magicTree = 310;
    int magicLog = 636;
    int magicLongBow = 656;
    int magicLongBowU = 666;
    
    int bowString = 676;
    int knife = 13;
    
    
    int xSpot,ySpot;
    
    boolean isCutting = true;
    int cuttingType; //0 = Oak,1=Willow,2=Yew,3=Magic
    
    int itemToDeposit;// = new int[]{};
    int itemToWithdraw;
    int treeId;
    int logId;
    
    int fmode = 3;
    
    public final void print(String gameText)
    {
        System.out.println(gameText);
    }
    
//    public Abyte0_Fletch(Extension e) {super(e);}    
    
    public void init(String param)
    {
        print("Seer Fletcher by Abyte0");
        print("Commands to cut: oak willow yew magic");
        print("Commands to string: o w y m");
        print("Version 0 r4");
        //r4 = added Logging Bank
            
        itemToDeposit = yewLongBowU;
        cuttingType = 2;
        xSpot = 518;
        ySpot = 473;
        treeId = yewTree;
        logId = yewLog;
        isCutting = true;
            
        if(param.equals("yew"))
        {
            print("Doing Yew");
        }
        else if(param.equals("oak"))
        {
            print("Doing Oak");
            itemToDeposit = oakLongBowU;
            cuttingType = 0;
            xSpot = 511;
            ySpot = 444;
            treeId = oakTree;
            logId = oakLog;
            isCutting = true;
        }
        else if(param.equals("willow"))
        {
            print("Doing Willow");
            itemToDeposit = willowLongBowU;
            cuttingType = 1;
            xSpot = 511;
            ySpot = 444;
            treeId = willowTree;
            logId = willowLog;
            isCutting = true;
        }
        else if(param.equals("magic"))
        {
            print("Doing Magic");
            itemToDeposit = magicLongBowU;
            cuttingType = 3;
            xSpot = 521;
            ySpot = 491;
            treeId = magicTree;
            logId = magicLog;
            isCutting = true;
        }
        else if(param.equals("o"))
        {
            print("Adding String to Oaks");
            itemToDeposit = oakLongBow;
            itemToWithdraw = oakLongBowU;
            isCutting = false;
        }
        else if(param.equals("w"))
        {
            print("Adding String to Willows");
            itemToDeposit = willowLongBow;
            itemToWithdraw = willowLongBowU;
            isCutting = false;
        }
        else if(param.equals("y"))
        {
            print("Adding String to Yews");
            itemToDeposit = yewLongBow;
            itemToWithdraw = yewLongBowU;
            isCutting = false;
        }
        else if(param.equals("m"))
        {
            print("Adding String to Magics");
            itemToDeposit = magicLongBow;
            itemToWithdraw = magicLongBowU;
            isCutting = false;
        }
        else
        {
            print("Default = Yew LongBow");
        }
    }    
    
    public int main() 
    {        
        if(getFightMode() != fmode) 
        {            
            setFightMode(fmode);            
            return 500;        
        }        
        if(getFatigue() > 85) 
        {
            useSleepingBag();            
            return random(800,1000);        
        }        
        if(isBanking())
        {
            if(getInventoryCount(itemToDeposit) > 0) 
            {
                deposit(itemToDeposit,getInventoryCount(itemToDeposit));
                return random(1000,1500);            
            }
            if(!isCutting)
            {
                if(getInventoryCount(bowString) < 14) 
                {                
                    withdraw(bowString,14-getInventoryCount(bowString));                
                    return random(1000,1500);            
                }
                else if(getInventoryCount(bowString) > 14) 
                {                
                    deposit(bowString,getInventoryCount(bowString));                
                    return random(1000,1500);            
                }
                if(getInventoryCount(itemToWithdraw) < 14) 
                {                
                    withdraw(itemToWithdraw,14-getInventoryCount(itemToWithdraw));                
                    return random(1000,1500);            
                }
                else if(getInventoryCount(itemToWithdraw) > 14) 
                {                
                    deposit(itemToWithdraw,getInventoryCount(itemToWithdraw));                
                    return random(1000,1500);            
                }
            }
            closeBank();  
            return random(1000,1500);
        }        
        if(isQuestMenu()) 
        {            
            if(getX() >= 498 && getX() <= 504 && getY() >= 447 && getY() <= 453) 
            {
                //si dans la banque
                answer(0);                
                return random(1000,1500);            
            }            
            else 
            {                
                answer(1);
                return random(200,400);                
            }        
        }
        
        if(!isCutting)
        {
            if(getInventoryCount(itemToWithdraw) == 0 || getInventoryCount(bowString) == 0)
            {
                return entreBanque();
            }
            else
            {
                useItemWithItem(getInventoryIndex(itemToWithdraw), getInventoryIndex(bowString));
                return random(500,600);
            }
        }
        else
        {
            if(getInventoryCount() < 30)
            {
                int banque = sortirBanque();
                if(banque != 0)
                    return banque;
                if(isAtApproxCoords(xSpot, ySpot, 8))
                {
                    //si on est au spot
                    int[] tree = getObjectById(treeId);
                    if(tree[0] != -1)
                    {
                        atObject(tree[1], tree[2]);
                        return random(500,600);
                    }
                    else
                    {
                        int c = cut();
                        if(c != 0)
                            return c;
                    }
                }
                else if(isAtApproxCoords(xSpot, ySpot, 30))
                {
                    walkTo(xSpot,ySpot);
                    return random(1000,3000);
                }
                else if(getY() < 465)
                {
                    //between Buildings
                    walkTo(510,465);
                    return random(999,2000);
                }
                else
                {
                    //near Corner Railling
                    walkTo(513,479);
                    return random(998,2000);
                }
            }
            else
            {
                //Si on veut aller a la banque
                //On cut en premier
                int c = cut();
                if(c != 0)
                    return c;
                
                //ensuite on essaie de banquer
                int banque = entreBanque();
                if(banque != 0)
                    return banque;
                
                //si on est trop loin on se rapproche
                if(getY() > 479)
                {
                    //near Corner Railling
                    walkTo(513,479);
                    return random(998,2000);
                }
                else if(getY() > 465)
                {
                    //between Buildings
                    walkTo(511,465);
                    return random(999,2000);
                }
                else
                {
                    //near bank
                    walkTo(500,454);
                    return random(200,500);
                }
            }
        }
        return random(800,1000);    
    }
    
    public int cut()
    {
        if(getInventoryCount(logId) > 0)
        {
            useItemWithItem(getInventoryIndex(knife), getInventoryIndex(logId));
            return random(500,600);
        }
        return 0;
    }
    
    
    public int sortirBanque()
    {
        if(getX() == 500 && getY() == 453)
        {
            //si devant porte on ouvre et on sort de la banque
            atObject(500,454);
            walkTo(500,454);
            return random(400, 2500);
        }
        if(getX() >= 498 && getX() <= 504 && getY() >= 447 && getY() <= 453)
        {
            //Si dans la banque
            //print("Walking to Door");    
            walkTo(500,453);
            return random(240, 2500);
        }
        return 0;
    }
    
    public int entreBanque()
    {
        if(getX() >= 498 && getX() <= 504 && getY() >= 447 && getY() <= 453)
        {
            //Si dans la banque
            int banker[] = getNpcByIdNotTalk(new int[]{95});
            if (banker[0] != -1 && !isBanking())
                talkToNpc(banker[0]);
            else
                print("No banker!"); 
            return random(240, 2500);
        }
        if(getX() == 500 && getY() == 454)
        {
            //si devant porte on ouvre et on entre dans la banque
            atObject(500,454);
            walkTo(500,453);
            return random(400, 2500);
        }
        return 0;
    }
    
} 