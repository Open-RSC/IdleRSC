package scripting.apos;
import compatibility.apos.Script;
import java.text.DecimalFormat;

import java.util.Locale;


public class EdgeEggs extends Script {

    private static final int SPIDER_EGGS = 219;
    private static final int LOBSTER = 373;
    private static final int LOBSTER_WITHDRAW_COUNT = 2;
    
    private static final int FIRST_EGGS_X = 208;
    private static final int FIRST_EGGS_Y = 3240;
    
    private static final int SECOND_EGGS_X = 209;
    private static final int SECOND_EGGS_Y = 3236;
    
    private static final int THIRD_EGGS_X = 204;
    private static final int THIRD_EGGS_Y = 3232;
    
    private static final int FOURTH_EGGS_X = 201;
    private static final int FOURTH_EGGS_Y = 3234;
    
    private State state = State.PICKING;
    
    private boolean firstEgg;
    private boolean secondEgg;
    private boolean thirdEgg;
    private boolean fourthEgg;
    
    private int eggCounter;
    private int lobCounter;
    private int tripCounter;
    
    private int attXp;
    private int strXp;
    private int defXp;
    private int hpXp;
    
    private int currentFightMode;
    private long startTime = -1L;
    
    private long lastTimeInFight = -1L;
    private long idleTime = 0;
    
    
    public EdgeEggs(String arg0) {
//        super(arg0);
    }
    
    @Override
    public void init(String arg0) {
        startTime = System.currentTimeMillis();
        
        attXp = getXpForLevel(0);
        defXp = getXpForLevel(1);
        strXp = getXpForLevel(2);
        hpXp = getXpForLevel(3);
    }
    
    @Override
    public int main() {
        if (!isLoggedIn()) {
            return 300;
        }
        if (getFightMode() != currentFightMode) {
            setFightMode(currentFightMode);
        }
        if (state == State.PICKING) {
            if (getInventoryCount(SPIDER_EGGS) == 27 && getInventoryCount(LOBSTER) == 0) {
                state = State.WALK_SPIDERS_TO_BANK;
                setEggsFalse();
                return random(200,1000);
            }
            //writeLine("1. "+ firstEgg +" " +secondEgg +" " + thirdEgg +" " +fourthEgg);

            
            if (!inCombat() && lastTimeInFight == -1L) {
                //writeLine("Last time in fight");
                lastTimeInFight = System.currentTimeMillis();
                return 1000;
            }
            
            if (inCombat()) {
                lastTimeInFight = -1L;
            }
            //writeLine("2. " +firstEgg +" " +secondEgg +" " + thirdEgg +" " +fourthEgg);
            if (pickedUpAllEggs()) {
                // Sest mee olime kombatis ning meil ei ole timerit.
                if (lastTimeInFight == -1L) {
                    return 400;
                }
                idleTime = System.currentTimeMillis() - lastTimeInFight;
                if (idleTime > 10000) {
                    autohop(true);
                    setEggsFalse();
                    //writeLine("Panime false. " +firstEgg +" " +secondEgg +" " + thirdEgg +" " +fourthEgg);
                    lastTimeInFight = -1L;
                    idleTime = 0;
                    //writeLine(firstEgg +" " +secondEgg +" " + thirdEgg +" " +fourthEgg);
                }
                return random(200,4000);
            }
            if (isItemAt(FIRST_EGGS_X, FIRST_EGGS_Y)) {
                return pickUpEggs(FIRST_EGGS_X, FIRST_EGGS_Y);
            } 
            firstEgg = true;
            if (isItemAt(SECOND_EGGS_X, SECOND_EGGS_Y)) {
                return pickUpEggs(SECOND_EGGS_X, SECOND_EGGS_Y);
            }
            secondEgg = true;
            if (isItemAt(THIRD_EGGS_X, THIRD_EGGS_Y)) {
                return pickUpEggs(THIRD_EGGS_X, THIRD_EGGS_Y);
            }
            thirdEgg = true;
            if (isItemAt(FOURTH_EGGS_X, FOURTH_EGGS_Y)) {
                return pickUpEggs(FOURTH_EGGS_X, FOURTH_EGGS_Y);
            }
            fourthEgg = true;
            return 4000;
        }
        if (state == State.WALK_SPIDERS_TO_BANK) {
            walkToBank();
            return random(200,1000);
        }
        if (state == State.BANKING) {
            if (getInventoryCount(SPIDER_EGGS) == 0 && getInventoryCount(LOBSTER) == LOBSTER_WITHDRAW_COUNT) {
                state = State.WALK_BANK_TO_SPIDERS;
                return random(200, 1000);
            }
            
            if (isBanking()) {
                int count = getInventoryCount(SPIDER_EGGS);
                    if (count > 0) {
                        deposit(SPIDER_EGGS, count);
                        eggCounter = eggCounter + 27;
                    }
                lobCounter = bankCount(LOBSTER);
                if (lobCounter < 10) {
                    takeScreenshot("out_of_lobs");
                    stopScript();
                }
                withdraw(LOBSTER, LOBSTER_WITHDRAW_COUNT);
                lobCounter = lobCounter - LOBSTER_WITHDRAW_COUNT;
                tripCounter++;
                closeBank();
                return random(300, 2000);
            }
            
            if (isQuestMenu()) {
                answer(0);
                return 1000;
            }
            //
            int banker [] = getNpcByIdNotTalk(BANKERS);
            if (banker[0] != -1) {
                talkToNpc(banker[0]);
                return 4000;
            }
        }
        if (state == State.WALK_BANK_TO_SPIDERS) {
            if (getFatigue() > 75) {
                useSleepingBag();
                return 4000;
            }
            walkToSpiders();
            return random(400, 1000);
        }
        return 1500;
    }
    
    @Override
    public void onServerMessage(String msg) {
        if (msg.contains("Welcome to")) {
            setEggsFalse();
        }
    }
    
    public void walkToSpiders() {
        // bank
        if (getObjectIdFromCoords(217, 447) == 64) {
            atObject(217, 447);
            return;
        }
        walkTo(218,464);
        // maja uks
        if (isAtApproxCoords(218, 463, 1)) {
            if (getWallObjectIdFromCoords(215, 3300) == 2) {
                atWallObject(215, 465);
                return;
            }
        }
        walkTo(217,467);
        // redel
        if (isAtApproxCoords(216, 468, 1)) {
            if (getObjectIdFromCoords(215, 468) == 6) {
                atObject(215, 468);
                return;
            }
        }
        walkTo(217,3279);
        walkTo(212, 3273);
        // lahtine aed
        if (isAtApproxCoords(212, 3273, 1)) {
            if (getObjectIdFromCoords(211, 3272) == 57) {
                atObject(211, 3272);
                return;
            }
        }
        walkTo(197,3267);
        walkTo(197, 3248);
        walkTo(205, 3240);
        // kinnine aed
        if (isAtApproxCoords(197, 3267, 1)) {
            if (getObjectIdFromCoords(196, 3266) == 305) {
                atObject(196, 3266);
                return;
            }
        }
        if (isAtApproxCoords(205, 3240, 3)) {
            state = State.PICKING;
            return;
        }
        
    }
    
    private void walkToBank() {
        
        // Kinnine aed
        if (isAtApproxCoords(197, 3264, 1)) {
            if (getObjectIdFromCoords(196, 3266) == 305) {
                atObject(196, 3266);
                return;
            }
        }
        walkTo(198, 3250);
        walkTo(197, 3264);
        walkTo(209,3273);
        walkTo(2017, 3284);
        walkTo(215, 3298);
        walkTo(222, 453);
        walkTo(217, 447);
        // Lahtine aed
        if (isAtApproxCoords(209, 3273, 1)) {
            if (getObjectIdFromCoords(211, 3272) == 57) {
                atObject(211, 3272);
                return;
            }
        }
        
        // Redel
        if (isAtApproxCoords(215, 3298, 2)) {
            if (getObjectIdFromCoords(215, 3300) == 5) {
                atObject(215, 3300);
                return;
            }
        }
        // maja uks
        if (isAtApproxCoords(216, 468, 2)) {
            if (getWallObjectIdFromCoords(215, 3300) == 2) {
                atWallObject(215, 465);
                return;
            }
        }
        // panga uks
        if (isAtApproxCoords(217, 447, 2)) {
            if (getWallObjectIdFromCoords(217, 447) == 64) {
                atWallObject(215, 465);
                return;
            }
            state = State.BANKING;
        }
    }
    
    private void setEggsFalse() {
        firstEgg = false; 
        secondEgg = false;
        thirdEgg = false;
        fourthEgg = false;
    }
    
    private boolean pickedUpAllEggs() {
        return firstEgg && secondEgg && thirdEgg && fourthEgg;
    }
    
    private int pickUpEggs(int x, int y) {
        if (isAtCoords(x, y)) {
            if (!inCombat()) {
                // eat lobsta, kui ruumi pole
                if (getInventoryCount() == MAX_INV_SIZE) {
                    int index = getInventoryIndex(LOBSTER);
                    if (getItemCommand(index).toLowerCase(Locale.ENGLISH).equals("eat")) {
                        useItem(index);
                        return random(200,400);
                    }
                }
                pickupItem(SPIDER_EGGS, x, y);
                return random(200,400);
            } 
        }
        walkTo(x, y);
        return random(200,400);
    }
    
    private boolean isAtCoords(int x, int y) {
        return isAtApproxCoords(x, y, 1);
    }
    
    public boolean isItemAt(int x, int y) {
        return super.isItemAt(SPIDER_EGGS, x, y);
    }
    
    @Override
    public void walkTo(int x, int y) {
        if (isReachable(x, y))
            super.walkTo(x, y);
    }
    
    private String getRuntime() {
        long secs = ((System.currentTimeMillis() - startTime) / 1000L);
        if (secs >= 3600) {
            return (secs / 3600) + " hours, " + ((secs % 3600) / 60)
                    + " mins, " + (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " + (secs % 60) + " secs.";
        }
        return secs + " secs.";

    }
    
    @Override
    public void paint() {
        final int white = 0xFFFFFF;
        int x = 105;
        int y = 40;
        drawString("Edge Eggs by H5d:", x, y, 1, white);
        y += 20;
        //drawString("IdleTime: " + idleTime, x, y, 1, white);
        //y += 20;
        drawString("Run time: " + getRuntime(), x, y, 1, white);
        y += 10;
        drawString("Current world: " + getWorld(), x, y, 1, white);
        y += 20;
        
        drawString("Lobs in bank: " +lobCounter, x, y, 1, 0xe6e600);
        y+=10;
        drawString("Trips made: " +tripCounter, x, y,1, white);
        y+=10;
        drawString("Eggs picked: " + eggCounter, x, y, 1, white);
        y += 20;
        
        
        int gainedStrXp = getXpForLevel(2) - strXp;
        int gainedAttXp = getXpForLevel(0) - attXp;
        int gainedDefXp = getXpForLevel(1) - defXp;
        int gainedHpXp = getXpForLevel(3) - hpXp;
        final int green = 0x7FFF00;
        drawString("Fightmode: " + convertFightMode(currentFightMode), x, y, 1, green);
        y +=10;
        if (gainedAttXp != 0) {
            drawString("Attack xp: " + xpConverter(gainedAttXp), x, y, 1, white);
            y += 10;
        }
        if (gainedStrXp != 0) {
            drawString("Strength xp: " + xpConverter(gainedStrXp), x, y, 1, white);
            y += 10;
        }
        if (gainedDefXp != 0) {
            drawString("Defense xp: " + xpConverter(gainedDefXp), x, y, 1, white);
            y += 10;
        }
        if (gainedHpXp != 0) {
            drawString("Hp xp: " + xpConverter(gainedHpXp), x, y, 1, white);
            y += 10;
        }
    
    }
    
    private String xpConverter(int xp) {
        if (xp > 1000) {
            double result = (((double)xp)/1000);
            return new DecimalFormat("#.##").format(result).concat(" k");
        } else {
            return Integer.toString(xp);
        }
    }
    
    private String convertFightMode(int fightMode) {
        switch (fightMode) {
        case 0:
            return "Controlled";
        case 1: 
            return "Strength";
        case 2: 
            return "Attack";
        default:
            return "Defense";
        }
    }
    
    public void onKeyPress(int keyCode) {
        if (keyCode == 113) {
            if (currentFightMode == 0) {
                currentFightMode = 1;
                setFightMode(1);
                writeLine("@gre@Fightmode is Strength");
            } else if (currentFightMode == 1) {
                currentFightMode = 2;
                setFightMode(2);
                writeLine("@gre@Fightmode is Attack");
            } else if (currentFightMode == 2) {
                currentFightMode = 3;
                setFightMode(3);
                writeLine("@gre@Fightmode is Defense");
            } else {
                currentFightMode = 0;
                setFightMode(0);
                writeLine("@gre@Fightmode is Controlled");
            }
        }
    }
    
    enum State {
        PICKING,
        WALK_SPIDERS_TO_BANK,
        BANKING,
        WALK_BANK_TO_SPIDERS
    }
}