package callbacks;

import bot.Main;
import controller.Controller;
import scripting.idlescript.IdleScript;

public class DrawCallback {

    private static long startTimestamp = System.currentTimeMillis() / 1000L;
    private static long startingXp = Long.MAX_VALUE;
    
    private static String statusText = "@red@Botting!";

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
            xpPerHr = (xpGained / (((System.currentTimeMillis() / 1000L) - startTimestamp))) * (60*60);
        }
        catch(Exception e) {
            xpPerHr = 0;
        }
        c.drawString("XP Gained: @red@" + String.format("%,d", xpGained)
                       + " @whi@(@red@" + String.format("%,d", xpPerHr) + " @whi@xp/hr)", 7, y, 0xFFFFFF, 1);
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
}
