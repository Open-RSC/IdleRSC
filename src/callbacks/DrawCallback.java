package callbacks;

import bot.Main;
import controller.Controller;
import scripting.idlescript.IdleScript;

public class DrawCallback {

    private static long startTimestamp = System.currentTimeMillis() / 1000L;
    private static long startingXp = Long.MAX_VALUE;
    
    private static String statusText = "@red@Botting!";
    
    private static String levelUpSkill = "", levelUpLevel = "";
    private static String levelUpText = "";
    private static long levelUpTextTimeout = 0;
    private static boolean screenshotTaken = false;

    public static void drawHook() {
        Controller c = Main.getController();

        drawBotStatus(c);
        drawScript(c);

    }
    
    public static void setStatusText(String str) { 
    	statusText = str;
    }

    private static void drawBotStatus(Controller c) {
        int y = 130 + 14 + 14 + 14;
        String localStatusText = statusText;
        
        if(!Main.isRunning()) {
        	localStatusText = "@red@Idle.";
        }
        c.drawString("Status: " + localStatusText, 7, y, 0xFFFFFF, 1);

        y+= 14;
        c.drawString("Coords: @red@(@whi@" + String.valueOf(c.currentX()) + "@red@,@whi@" + String.valueOf(c.currentZ()) + "@red@)", 7, y, 0xFFFFFF, 1);

        y += 14;
        long totalXp = getTotalXp();
        startingXp = totalXp < startingXp ? totalXp : startingXp;
        long xpGained = totalXp - startingXp;
        long xpPerHr;
        try {
    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
    		float scale = (60 * 60) / timeRan;
    		xpPerHr = (int)(xpGained * scale);
        }
        catch(Exception e) {
            xpPerHr = 0;
        }
        c.drawString("XP Gained: @red@" + String.format("%,d", xpGained)
                       + " @whi@(@red@" + String.format("%,d", xpPerHr) + " @whi@xp/hr)", 7, y, 0xFFFFFF, 1);
        
        if(System.currentTimeMillis() / 1000L < levelUpTextTimeout) {
        	y += 14;
        	c.drawString(levelUpText, 7, y, 0xFFFFFF, 1);
        	if(screenshotTaken == false) {
        		c.takeScreenshot(c.getPlayerName() + "_" + levelUpLevel + "_" + levelUpSkill);
        		screenshotTaken = true;
        	}
        }
        
    }

    private static void drawScript(Controller c) {

        if(Main.isRunning() && Main.getCurrentRunningScript() != null) {
            if(Main.getCurrentRunningScript() instanceof IdleScript) {
                ((IdleScript)Main.getCurrentRunningScript()).paintInterrupt();
            }
        }
    }

    private static long getTotalXp() {
        Controller c = Main.getController();

        long result = 0;

        for(int statIndex = 0; statIndex < c.getStatCount(); statIndex++) {
            result += c.getStatXp(statIndex);
        }

        return result;

    }
    
    public static void displayAndScreenshotLevelUp(String statName, int level) {
    	screenshotTaken = false;
    	levelUpSkill = statName;
    	levelUpLevel = String.valueOf(level);
    	
    	levelUpText = "@red@" + String.valueOf(level) + " @whi@" + statName + "@red@!";
    	levelUpTextTimeout = System.currentTimeMillis() / 1000L + 15; //display for 15seconds
    }
}
