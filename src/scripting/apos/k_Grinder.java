package scripting.apos;
import compatibility.apos.Script;
import java.text.DecimalFormat;
import java.util.Locale;

public final class k_Grinder extends Script {

    /* unfinished kwuarm potions -> super strengths: 220,461,492,95 */

    private final int[] items = new int[3];
    private final int horn = 466;
	private final int groundHorn = 473;
    private final int scale = 467;
	private final int groundScale = 472;
	private final int pestel = 468;
	private final int[] xp_start = new int[SKILL.length];
    private int banked_count1;
	private int banked_count2;
    private boolean banked;
    private long start_time;
    private long bank_time;
    private long menu_time;
    private final DecimalFormat f = new DecimalFormat("#,##0");

    public k_Grinder(String ex) {
        //////super(ex);
    }

    @Override
    public void init(String params) {
        start_time = bank_time = menu_time = -1L;
        banked_count1 = 0;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            for (int i = 0; i < xp_start.length; ++i) {
                xp_start[i] = getXpForLevel(i);
            }
        }

        if (isQuestMenu()) {
            menu_time = -1L;
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(600, 800);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }

        if (isBanking()) {
            bank_time = -1L;
            int count = getInventoryCount(groundHorn);
            if (count > 0) {
                if (!banked) {
                    banked_count1 += count;
                    banked = true;
                }
                deposit(groundHorn, count);
                return random(600, 800);
            }
            int count2 = getInventoryCount(groundScale);
            if (count2 > 0) {
                if (!banked) {
                    banked_count2 += count2;
                    banked = true;
                }
                deposit(groundScale, count2);
                return random(600, 800);
            }
            count = getInventoryCount(horn);
            if (count > 29) {
                deposit(horn, count - 14);
                return random(600, 800);
            } else if (count < 29) {
                int w = 29 - count;
                int bc = bankCount(horn);
                if (w > bc) w = bc;
                if (w > 0) {
                    withdraw(horn, w);
                } else if (count == 0) {
                    System.out.println("Out of " + getItemNameId(horn));
                    return random(600, 800);
                }
            }
            count = getInventoryCount(scale);
            if (count > 29) {
                deposit(scale, count - 14);
                return random(600, 800);
            } else if (count < 29) {
                int w = 29 - count;
                int bc = bankCount(scale);
                if (w > bc) w = bc;
                if (w > 0) {
                    withdraw(scale, w);
                } else if (count == 0) {
                    System.out.println("Out of " + getItemNameId(scale));
                    setAutoLogin(false); stopScript();
                    return random(600, 800);
                }
            }
            count = getInventoryCount(pestel);
            if (count > 1) {
                deposit(pestel, count - 14);
                return random(600, 800);
            } else if (count < 1) {
                int w = 1 - count;
                int bc = bankCount(pestel);
                if (w > bc) w = bc;
                if (w > 0) {
                    withdraw(pestel, w);
                } else if (count == 0) {
                    System.out.println("No " + getItemNameId(pestel) + " found, stopping script");
                    setAutoLogin(false); stopScript();
                    return random(600, 800);
                }
            }
            closeBank();
            banked = false;
            return random(1280,1720);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }

        int index1 = getInventoryIndex(horn);
        int index2 = getInventoryIndex(pestel);
        if (index1 != -1 && index2 != -1) {
            useItemWithItem(index1, index2);
            return random(600, 800);
        }
        
		int index3 = getInventoryIndex(scale);
        int index4 = getInventoryIndex(pestel);
        if (index3 != -1 && index2 != -1) {
            useItemWithItem(index3, index2);
            return random(600, 800);
        }
		
        int[] banker = getNpcByIdNotTalk(BANKERS);
        if (banker[0] != -1) {
            talkToNpc(banker[0]);
            menu_time = System.currentTimeMillis();
        }
        return random(600, 800);
    }

    @Override
    public void paint() {
        int x = 320;
        int y = 46;
        final int color = 0x1E90FF;
        final int font = 1;
        drawString("kRiStOf's Grinder", x, y, font, color);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, 0xFFFFFF);
        y += 15;
        drawString("Banked Horns: " + f.format(banked_count1), x, y, font, 0xFFFFFF);
        y += 15;
		drawString("Banked Scales: " + f.format(banked_count2), x, y, font, 0xFFFFFF);
        y += 15;
		
        for (int i = 0; i < xp_start.length; ++i) {
            int gained = getXpForLevel(i) - xp_start[i];
            if (gained <= 0) {
                continue;
            }
            drawString(SKILL[i] + " XP: " + f.format(gained) +
                    " (" + per_hour(gained) + "/h)", x, y, font, 0xFFFFFF);
            y += 15;
        }
    }

    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        }
    }

    private String per_hour(int total) {
        long time = ((System.currentTimeMillis() - start_time) / 1000L);
        if (time < 1L) {
            time = 1L;
        }
        return f.format((total * 60L * 60L) / time);
    }

    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 3600L) {
            return f.format((secs / 3600L)) + " hours, " +
                    ((secs % 3600L) / 60L) + " mins, " +
                    (secs % 60L) + " secs.";
        }
        if (secs >= 60L) {
            return secs / 60L + " mins, " +
                    (secs % 60L) + " secs.";
        }
        return secs + " secs.";
    }
}