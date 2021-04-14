package scripting.apos;
import compatibility.apos.Script;
public final class S_GnomeFlaxBank extends Script {
 
    private int banked_count;
    private long start_time;
    private long bank_time;
    private static final int ID_FLAX = 675;
    private static final int ID_STRING = 676;
    private static final int ID_WHEEL = 121;
    
//    public S_GnomeFlaxBank(Extension ex) {
//        super(ex);
//    }
 
    @Override
    public void init(String params) {
       banked_count = 0;
       start_time = -1L;
       bank_time = -1L;
    }
 
    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (isBanking()) {
            int count = getInventoryCount(ID_FLAX);
            if (count > 0) {
                deposit(ID_FLAX, count);
                banked_count += count;
                return random(400, 500);
            }
            closeBank();
            return random(200, 300);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (isQuestMenu()) {
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(600, 800);
        }
        if (getInventoryCount() == MAX_INV_SIZE) {
            if (getY() > 1000) {
                int[] npc = getNpcByIdNotTalk(BANKERS);
                if (npc[0] != -1) {
                    talkToNpc(npc[0]);
                    return random(3100, 3500);
                }
            } else if (isAtApproxCoords(712, 516, 5)) {
                atObject(714, 516);
                return random (250, 250);
            }
        } else {
            if (getY() > 1000) {
                atObject(714,1460);
                return random(150, 200);
            } else if (isAtApproxCoords(712, 516, 5)) {
                atObject2(712, 517);
                return random(200, 200);
            }
        }
        /*
        if (getInventoryCount() == MAX_INV_SIZE) {
            int idx_flax = getInventoryIndex(ID_FLAX);
            if (idx_flax == -1) {
                if (isAtApproxCoords(714, 516, 5)) {
                    atObject(714, 516);
                    return random(300, 400);
                }
                return random(1000, 1500);
            } else {
                if (getY() > 1000) {
                    int[] npc = getNpcByIdNotTalk(BANKERS);
                    if (npc[0] != -1) {
                        if (!isAtApproxCoords(npc[1], npc[2], 4)) {
                            if (!isWalking()) {
                                _walkApprox(npc[1], npc[2], 2); 
                            }
                            return random(1000, 1500);
                        }
                        talkToNpc(npc[0]);
                        return random(200, 300);
                    }
                    return random(200, 300);
                } else {
                    atObject(714,516);
                    return random(600,800)
                }
            }
        } else {
            if (isAtApproxCoords(712, 516, 5)) {
                // pick flax
                atObject2(712, 517);
                return random(400, 600);
            }
            if (isAtApproxCoords(714, 1460, 4)) {
                atObject(714, 1460);
                return random(1000, 1500);
            }
            if (getY() > 1000) {
                if (!isWalking()) {
                    _walkApprox(714, 1460, 2);
                }
                return random(1000, 1500);
            }
            return random(1000, 1500);
        }
        */
        return random(400, 450);
    }
    
    private boolean _walkApprox(int x, int y, int range) {
        int dx, dy;
        int loop = 0;
        do {
            dx = x + random(-range, range);
            dy = y + random(-range, range);
            if ((++loop) > 300) {
                return false;
            }
        } while (!isReachable(dx, dy));
        walkTo(dx, dy);
        return true;
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 7200) {
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 3600 && secs < 7200) {
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }
 
    @Override
    public void paint() {
        final int gray = 0xC4C4C4;
        int y = 25;
        drawString("S GnomeFlax", 25, y, 1, gray);
        y += 15;
        drawString("Runtime: " + _getRuntime(), 25, y, 1, gray);
        y += 15;
        if (banked_count > 0) {
            drawString("Banked " + banked_count + " strings", 25, y, 1, gray);
        }
    }
}