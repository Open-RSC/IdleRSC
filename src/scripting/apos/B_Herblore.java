package scripting.apos;
import compatibility.apos.Script;
/**kRiStOf's ChangeLog:
	10Jan17 - Edited to exclude regular defense potions to save secondaries for better pots.
	11Jan17 - Edited to include using unfinished pots along with clean herbs and grimy herbs, in that order
	16Jan18 - Added a check to deposit pots or herbs if more than 14 are withdrawn
*/

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.Date;

public class B_Herblore extends Script {

    private final int VIAL_OF_WATER = 464;
    //Extension e;
    private long banking;
    private long checkin;
    private int cleaned;
    private int made;
    private boolean unfinished;
    private int[] initial_xp;
    private long time;
    private long[] screenshot = new long[4];
    private String filename;
    private String name;
    private int potion_choice;
    private Potion potion;
//
    public B_Herblore(String e) {
        //
        //this.e = e;
    }

    @Override
    public void init(String s) {
        int option = JOptionPane.showOptionDialog(null, "Make unfinished only?", "Blood's Herblore", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Yes", "No"}, null);
        unfinished = (option == 0);
        String[] potion_names = {"Make all", "Attack Potion", "Cure Poison", "Strength Potion", "Stat Restore Potion", "Restore Prayer Potion", "Super Attack Potion", "Poison Antidote", "Fishing Potion", "Super Strength Potion", "Weapon Poison Potion", "Super Defense Potion", "Ranging Potion", "Potion of Zamorak"};
        String potion_name = (String) JOptionPane.showInputDialog(null, "What type of potion would you like to make?", "Herblore", JOptionPane.QUESTION_MESSAGE, null, potion_names, potion_names[0]);
        potion_choice = 0;
        for (int i = 0; i < potion_names.length; i++) {
            if (potion_names[i].equals(potion_name)) {
                potion_choice = i;
                break;
            }
        }
        banking = -1L;
        checkin = -1L;
        initial_xp = new int[SKILL.length];
        name = null;
        if (potion_choice == 0) {
            potion_choice++;
            potion = getPotion(potion_choice);
        } else {
            potion = getPotion(potion_choice);
            potion_choice = -1;
        }
    }

    @Override
    public int main() {
        if (initial_xp[0] == 0) {
            for (int i = 0; i < SKILL.length; i++) {
                initial_xp[i] = getXpForLevel(i);
            }
            time = System.currentTimeMillis();
            return 500;
        }
        if (getCurrentLevel(15) < potion.level) {
            System.out.println("Herblore level too low to make " + potion.potion_name);
            stop(false);
        }
        long now = System.currentTimeMillis();
        if (screenshot[0] != -1L) {
            return screenshot(now);
        }
        if (checkin == -1L || now > checkin + 900000L) {
            return checkin(now);
        }
        if (getFatigue() > 95) {
            useSleepingBag();
            return 1000;
        }
        if (banking != -1L) {
            if (System.currentTimeMillis() > banking + 10000L) {
                banking = -1L;
                return 10;
            }
            if (isQuestMenu()) {
                answer(0);
                return 10;
            }
            if (isBanking()) { //
                banking = -1L;
                return 10;
            }
            return 20;
        }
        int grimy = getInventoryIndex(potion.grimy);
        if (grimy != -1) {
            useItem(grimy);
            return 250;
        }
        int clean = getInventoryIndex(potion.clean);
        int vial = getInventoryIndex(VIAL_OF_WATER);
        if (vial != -1 && clean != -1) {
            useItemWithItem(vial, clean);
            return 250;
        }
        if (!unfinished) {
            int secondary = getInventoryIndex(potion.secondary);
            int unfinished = getInventoryIndex(potion.unfinished);
            if (unfinished != -1 && secondary != -1) {
                useItemWithItem(unfinished, secondary);
                return 250;
            }
        }
        if (isBanking()) {
            int count = getInventoryCount(potion.finished);
			System.out.println("line 121 banking");
            if (count > 0) {
                deposit(potion.finished, count);
                return 1000;
            }
            if (!unfinished) {
                count = getInventoryCount(potion.unfinished);
                if (count > 0) {
                    if (hasBankItem(potion.secondary)) {
                        withdraw(potion.secondary, count);
                    } else {
						deposit(potion.unfinished, count);
                        System.out.println("Out of " + getItemNameId(potion.secondary) + " for " + potion.potion_name);
                        stop(false);
                    }
                    closeBank();
                    return 1000;
                }
            }
            count = getInventoryCount(potion.unfinished);
            if (count > 0) {
                deposit(potion.unfinished, count);
                return 1000;
            }

            count = getInventoryCount(potion.clean, potion.grimy, potion.unfinished);
			if (!unfinished && count < 14 && hasBankItem(potion.unfinished) && bankCount(potion.secondary) > 14) {
				withdraw(potion.unfinished, 14 - count);
				System.out.println("line 148 count = " + count);
				return 1000;
			}
			
			count = getInventoryCount(potion.clean, potion.grimy, potion.unfinished);
            if (count < 14) {
                if (hasBankItem(potion.clean))
                    withdraw(potion.clean, 14 - count);
                else if (hasBankItem(potion.grimy))
                    withdraw(potion.grimy, 14 - count);
				else {
                    System.out.println("Out of " + getItemNameId(potion.grimy) + ", " + getItemNameId(potion.clean) + ", and " + getItemNameId(potion.unfinished) + " for " + potion.potion_name);
                    stop(false);
                }
            }

			count = getInventoryCount(potion.clean, potion.grimy, potion.unfinished);
			if (count > 14) {
				if (getInventoryCount(potion.grimy) > 14)
					deposit(potion.grimy, getInventoryCount(potion.grimy) - 14);
				else if (getInventoryCount(potion.clean) > 14)
					deposit(potion.clean, getInventoryCount(potion.clean) - 14);
				else if (getInventoryCount(potion.unfinished) > 14)
					deposit(potion.unfinished,getInventoryCount(potion.unfinished) - 14);
			}

            count = getInventoryCount(VIAL_OF_WATER);
            if (count < 14) {
                withdraw(VIAL_OF_WATER, 14 - count);
            }
            closeBank();
            return 1000;
        }
        int[] banker = getNpcByIdNotTalk(BANKERS);
        if (banker[0] != -1 && getEmptySlots() < 30) {
            talkToNpc(banker[0]);
            banking = System.currentTimeMillis();
            return 500;
        }
        return 1000;
    }

    private int[] getXpStatistics(int skill) {
        long time = ((System.currentTimeMillis() - this.time) / 1000L);
        if (time < 1L) {
            time = 1L;
        }
        int start_xp = initial_xp[skill];
        int current_xp = getXpForLevel(skill);
        int[] intArray = new int[4];
        intArray[0] = current_xp;
        intArray[1] = start_xp;
        intArray[2] = intArray[0] - intArray[1];
        intArray[3] = (int) ((((current_xp - start_xp) * 60L) * 60L) / time);
        return intArray;
    }

    private String getRunTime() {
        long millis = (System.currentTimeMillis() - time) / 1000;
        long second = millis % 60;
        long minute = (millis / 60) % 60;
        long hour = (millis / (60 * 60)) % 24;
        long day = (millis / (60 * 60 * 24));

        if (day > 0L) return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
        if (hour > 0L) return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
        if (minute > 0L) return String.format("%02d minutes, %02d seconds", minute, second);
        return String.format("%02d seconds", second);
    }

    private Potion getPotion(int number) {
        String[] potion_names = {"Make all", "Attack Potion", "Cure Poison", "Strength Potion", "Stat Restore Potion", "Restore Prayer Potion", "Super Attack Potion", "Poison Antidote", "Fishing Potion", "Super Strength Potion", "Weapon Poison Potion", "Super Defense Potion", "Ranging Potion", "Potion of Zamorak"};
        Potion[] potions = new Potion[potion_names.length];
        if (number >= potions.length) {
            return null;
        }
        int i = 1;
        potions[i] = new Potion(potion_names[i++], "Guam", 3, 25, 165, 2.5, 444, 270, 454, 474);
        potions[i] = new Potion(potion_names[i++], "Marrentill", 5, 37.5, 435, 3.75, 445, 473, 455, 566);
        potions[i] = new Potion(potion_names[i++], "Tarromin", 12, 50, 436, 5, 446, 220, 456, 222);
        potions[i] = new Potion(potion_names[i++], "Harralander", 22, 63, 437, 6, 447, 219, 457, 477);
//		potions[i] = new Potion(potion_names[i++], "Ranarr", 30, 75, 438, 8, 448, 471, 458, 480);
        potions[i] = new Potion(potion_names[i++], "Ranarr", 38, 87.5, 438, 8, 448, 469, 458, 483);
        potions[i] = new Potion(potion_names[i++], "Irit", 45, 100, 439, 9, 449, 270, 459, 486);
        potions[i] = new Potion(potion_names[i++], "Irit", 48, 106, 439, 9.25, 449, 473, 459, 569);
        potions[i] = new Potion(potion_names[i++], "Avantoe", 50, 113, 440, 10, 450, 469, 460, 489);
        potions[i] = new Potion(potion_names[i++], "Kwuarm", 55, 125, 441, 11.25, 451, 220, 461, 492);
        potions[i] = new Potion(potion_names[i++], "Kwuarm", 60, 137.5, 441, 11.25, 451, 472, 461, 572);
        potions[i] = new Potion(potion_names[i++], "Cadantine", 66, 150, 442, 12.5, 452, 471, 462, 495);
        potions[i] = new Potion(potion_names[i++], "Dwarf Weed", 72, 165, 443, 13.75, 453, 501, 463, 498);
        potions[i] = new Potion(potion_names[i++], "Torstol", 78, 175, 933, 15, 934, 936, 935, 963);
        return potions[number];
    }

    private void nextPotion() {
        potion_choice++;
        potion = getPotion(potion_choice);
    }

    private void stop(boolean emergency) {
        if (!emergency && potion_choice != -1) {
            nextPotion();
            if (potion != null) {
                return;
            }
        }
        setAutoLogin(false);
        logout();
        stopScript();
    }

    private int screenshot(long now) {
        if (now > screenshot[0] + 3000L) {
            screenshot[0] = -1L;
            return 50;
        }
        if (screenshot[1] == 0) {
            if (isPaintOverlay()) { // Get state of Paint
                screenshot[2] = 1L; // If it's on, remember this
                setPaintOverlay(false); // If it's on, turn it off for the screenshot
            } else {
                screenshot[2] = -1L; // If it's off, remember this
            }
            if (isRendering()) { // Get state of Graphics
                screenshot[3] = 1L; // If it's on, remember this
            } else {
                screenshot[3] = -1L; // If it's off, remember this
                setRendering(true); //If it's off, turn it on for the screenshot
            }
            //e.Hb = 20; // Mouse over skill menu  // to be removed 2.4
            //e.p = 420; // Mouse over skill menu  // to be removed 2.4
            screenshot[1] = 1;
            return 50;
        }
        if (isSkipLines()) { // to be uncommented 2.4
            setSkipLines(false);
        }
        if (now < screenshot[0] + 1000L) { // time for paint() to redraw...
            return 50;
        }
        takeScreenshot(filename); // Take screenshot
        screenshot[0] = -1L;
        screenshot[1] = 0;
        if (screenshot[2] == 1L) { // If Paint was enabled before the screenshot, turn it back on
//            PaintListener.toggle();
        }
        if (screenshot[2] == 1L) { // If Paint was enabled before the screenshot, turn it back on
            setPaintOverlay(true);
        }
        if (screenshot[3] != 1L) { // If the Graphics were off before the screenshot, turn them back off
            setRendering(false);
        }
        return 50;
    }
    //screenshot end

    @Override
    public void paint() {
        int x = 10;
        int y = 30;
        drawString("Blood Herbs", x, y, 4, 0x00b500);
		y += 12;
		drawString("Edited by kRiStOf",x+1, y, 1, 0x1E90FF);
        y += 15;
        if (cleaned > 0) {
            drawString(potion.herb_name + " cleaned: " + cleaned + " (" + (cleaned * potion.grimy_xp) + " xp)", x, y, 1, 0xFFFFFF);
            y += 12;
        }
        if (made > 0) {
            drawString(potion.potion_name + " made: " + made + " (" + (made * potion.xp) + " xp)", x, y, 1, 0xFFFFFF);
            y += 12;
        }
        for (int i = 0; i < SKILL.length; ++i) {
            int[] xp = getXpStatistics(i);
            if (xp[2] > 100) {
                drawString(SKILL[i] + " XP Gained: " + xp[2] + " (" + xp[3] + " XP/h)", x, y, 1, 0xFFFFFF);
                y += 12;
            }
        }
        drawString("Runtime: " + getRunTime(), x, y, 1, 0xFFFFFF);
    }

    @Override
    public void onChatMessage(String msg, String name, boolean pmod, boolean jmod) {
        System.out.println(name + ": " + msg);
        if (jmod) {
            stop(true);
        }
    }

    @Override
    public void onPrivateMessage(String msg, String name, boolean pmod, boolean jmod) {
        System.out.println(name + ": " + msg + " (PM)");
        if (jmod) {
            stop(true);
        }
    }

    @Override
    public void onServerMessage(String s) {
        if (s.contains("Banker is busy at the moment")) {
            banking = -1L;
            return;
        }
        if (s.contains("into your potion")) {
            made++;
            return;
        }
        if (s.contains("This herb is")) {
            cleaned++;
            return;
        }
        //screenshot start
        if (s.contains("just advanced")) {
            if (name == null) {
                name = getPlayerName(0);
            }
            screenshot[0] = System.currentTimeMillis();
            filename = new Date().getTime() + " - " + name + " - " + s;
        }
        //screenshot end
    }

    /*
        Edit this and use it to post data elsewhere if you wish
     */
    private int checkin(long now) {
        if (name == null) {
            name = getPlayerName(0);
        }
        if (!name.equals("blood")) {
            checkin = now;
            return 0;
        }
        System.out.print("Checking in... ");
        if (getX() <= 0) {
            System.out.println("Failed.");
            return 1000;
        }
        try {
            StringBuilder stats = new StringBuilder();
            for (int i = 0; i < SKILL.length; i++) {
                stats.append(getXpForLevel(i));
                if (i < SKILL.length - 1) {
                    stats.append(",");
                }
            }
            URL url = new URL(
                    "https://example.com/submit" +
                            "?name=" + URLEncoder.encode("blood", "UTF-8") +
                            "&skills=" + URLEncoder.encode(stats.toString(), "UTF-8")
            );

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.getInputStream();
            System.out.println("Done.");
        } catch (NoRouteToHostException | SocketTimeoutException e) {
            System.out.println("Failed.");
            return 1000;
        } catch (IOException ioe) {
            System.out.println("Failed.");
        }
        checkin = now;
        return 0;
    }

    private class Potion {
        public String potion_name;
        public String herb_name;
        public int level;
        public double xp;
        public int grimy;
        public double grimy_xp;
        public int clean;
        public int secondary;
        public int unfinished;
        public int finished;

        public Potion(String potion_name, String herb_name, int level, double xp, int grimy, double grimy_xp, int clean, int secondary, int unfinished, int finished) {
            this.potion_name = potion_name;
            this.herb_name = herb_name;
            this.level = level;
            this.xp = xp;
            this.grimy = grimy;
            this.grimy_xp = grimy_xp;
            this.clean = clean;
            this.secondary = secondary;
            this.unfinished = unfinished;
            this.finished = finished;
        }
    }
}