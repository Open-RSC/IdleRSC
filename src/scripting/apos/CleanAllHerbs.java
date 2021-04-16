package scripting.apos;

import java.awt.*;

import compatibility.apos.Script;

public class CleanAllHerbs extends Script {

    private final int[] HERB_IDS = {165, 435, 436, 437, 438, 439, 440, 441, 442, 443};
    private final int[] CLEAN_IDS = {444, 445, 446, 447, 448, 449, 450, 451, 452, 453};
    private Point BANK_CHEST = new Point(58,731);

    private boolean needToMove;
    private int cleanedCount = 0;
    private double xpGained = 0D;
    private long startTime = -1L;
    private long bankTime = -1L;
    private boolean waitForBank = false;


    public CleanAllHerbs(String ex) {
//        super(ex);
    }

    @Override
    public void init(String params) {
    }

    @Override
    public int main() {
        if (startTime == -1L) startTime = System.currentTimeMillis();

        // handle npc banking
        if (isQuestMenu()) {
            answer(0);
            return 1000;
        }

        if (isSleeping()) {
            return 500;
        }

        if (getFatigue() > 90) {
            useSleepingBag();
            return 1000;
        }

        if (waitForBank && !isBanking()) {
            if (System.currentTimeMillis() - bankTime > 10000) {
                waitForBank = false;
                bankTime = -1L;
            }
            return 50;
        }

        // withdraw any herb that needs cleaned in bank, working from lowest level to highest
        if (isBanking()) {
            waitForBank = false;
            bankTime = -1L;
            if (getInventoryCount(CLEAN_IDS) > 0) {
                for (int herb : CLEAN_IDS) {
                    if (getInventoryCount(herb) > 0) {
                        deposit(herb, getInventoryCount(herb));
                        return 600;
                    }
                }
            }
            if (getInventoryCount() == MAX_INV_SIZE) {
                closeBank();
                return 300;
            }
            for (int herb : HERB_IDS) {
                if (bankCount(herb) > 0) {
                    withdraw(herb, Math.max(29, bankCount(herb)));
                    return 600;
                }
            }
        }

        // just move one square to a reachable tile if we can
        if (needToMove) {
            needToMove = false;
            if (isReachable(getX() + 1, getY())) {
                walkTo(getX()+1,getY());
                return 1500;
            }
            if (isReachable(getX(), getY()+1)) {
                walkTo(getX(),getY()+1);
                return 1500;
            }
            if (isReachable(getX(), getY()-1)) {
                walkTo(getX(),getY()-1);
                return 1500;
            }
            if (isReachable(getX()-1, getY())) {
                walkTo(getX()-1,getY());
                return 1500;
            }
        }

        if (getInventoryCount(HERB_IDS) > 0) {
            useItem(getInventoryIndex(HERB_IDS));
            if (needToMove) return 1500;
            return 800;

        } else {
            // bank at shantay if we're near there
            bankTime = System.currentTimeMillis();
            if (distanceTo(BANK_CHEST.x, BANK_CHEST.y) < 10) {
                atObject(BANK_CHEST.x, BANK_CHEST.y);
                return 800;
            } else {
                int[] bankers = getNpcByIdNotTalk(BANKERS);
                if (bankers[0] != -1) {
                    talkToNpc(bankers[0]);
                    return 1500;
                } else {
                    stopScript();
                    System.out.println("Killing script - could not find a way to bank");
                }
            }
        }

        return 600;
    }

    @Override
    public void paint() {
        int y = 25;
        drawString("Herb Cleaner", 25, y, 1, 0xFFFFFF);
        y += 13;
        drawString("Runtime: " + get_time_since(startTime), 25, y, 1, 0xFFFFFF);
        y += 13;
        drawString("Cleaned: " + cleanedCount, 25, y, 1, 0xFFFFFF);
        y += 13;
        drawString("XP Gained: " + xpGained, 25, y, 1, 0xFFFFFF);
    }

    private static String get_time_since(long t) {
        long millis = (System.currentTimeMillis() - t) / 1000;
        long second = millis % 60;
        long minute = (millis / 60) % 60;
        long hour = (millis / (60 * 60)) % 24;
        long day = (millis / (60 * 60 * 24));

        if (day > 0L) {
            return String.format("%02d days, %02d hrs, %02d mins",
                    day, hour, minute);
        }
        if (hour > 0L) {
            return String.format("%02d hours, %02d mins, %02d secs",
                    hour, minute, second);
        }
        if (minute > 0L) {
            return String.format("%02d minutes, %02d seconds",
                    minute, second);
        }
        return String.format("%02d seconds", second);
    }

    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase();
        if (str.contains("you need a higher")) {
           stopScript();
           System.out.println("No more herbs with the level to identify!");
        } else if (str.contains("have been standing")) {
            needToMove = true;
        } else if (str.contains("guam")) {
            xpGained += 2.5;
            cleanedCount++;
        } else if (str.contains("garrentill")) {
            xpGained += 3.75;
            cleanedCount++;
        } else if (str.contains("tarromin")) {
            xpGained += 5;
            cleanedCount++;
        } else if (str.contains("harralander")) {
            xpGained += 6.25;
            cleanedCount++;
        } else if (str.contains("ranarr")) {
            xpGained += 7.5;
            cleanedCount++;
        } else if (str.contains("irit")) {
            xpGained += 8.75;
            cleanedCount++;
        } else if (str.contains("avantoe")) {
            xpGained += 10;
            cleanedCount++;
        }else if (str.contains("kwuarm")) {
            xpGained += 11.25;
            cleanedCount++;
        }else if (str.contains("cadantine")) {
            xpGained += 12.5;
            cleanedCount++;
        }else if (str.contains("dwarf")) {
            xpGained += 13.75;
            cleanedCount++;
        }else if (str.contains("torstol")) {
            xpGained += 15;
            cleanedCount++;
        } else if (str.contains("this chest")) {
            waitForBank = true;
        }
    }
}
