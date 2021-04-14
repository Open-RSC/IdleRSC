package scripting.apos;
import compatibility.apos.Script;
import java.awt.*;
import javax.swing.*;

public class YanilleLedge extends Script {

   private final int[][] LEDGES = {{601, 3558}, {601, 3562}};
   private int balance_success;
   private int sleep_at = 95;
   private int[] initial_xp;
   private long time;
   private String filename;
   private long[] screenshot = new long[4];
   int[] loot = {526, 527, 1277};
   
   boolean logOut = false;
   
   int fMode = 3;  
   String[] fModeName = {"Attack","Defence","Strength", "Controlled"};
   int[] fModeIdList = {2,3,1,0};

   public YanilleLedge() {
       balance_success = 0;
       initial_xp = new int[SKILL.length];
   }

   @Override
   public void init(String s) {
              Frame frame = new Frame("Select Fighting Mode");
       String choiceF = (String)JOptionPane.showInputDialog(frame,        "Fighting Mode Selection:\n", "Sinister Key Banker by: Abyte0",        JOptionPane.PLAIN_MESSAGE, null, fModeName, null);
       for(int i = 0; i < fModeName.length; i++)
       {
           if (fModeName[i].equals(choiceF))
           {
               fMode = fModeIdList[i];
               break;
           }
       }
       System.out.println("fMode = " + choiceF);
   }

   @Override
   public int main() {
       if(getFightMode() != fMode)
         setFightMode(fMode);
		 
       if (initial_xp[0] == 0) {
           for (int i = 0; i < SKILL.length; i++) {
               initial_xp[i] = getXpForLevel(i);
           }
           time = System.currentTimeMillis();
       }
       if (getFatigue() >= sleep_at) {
           useSleepingBag();
           return random(2000, 2100);
       }
       if (screenshot[0] != -1L) {
           return screenshot(System.currentTimeMillis());
       }
       int ledge;
       if (getY() < 3560) {
           ledge = 0;
       } else {
           ledge = 1;
       }
	   	
		if (getX() == 600) {
			logOut = false;
		System.out.println("logOut = false");
		walkTo(601, getY());
		return random (400, 800);
		}
	   
	   if (logOut == true) {
		if (getY() == 3563) {
			walkTo(600, getY());
			System.out.println("logOut = true");
			return random(400, 800);
			}
		if (getY() == 3557) {
			atObject(601, 3558);
			System.out.println("logOut = true");
			return random(400,800);
			}
		}
		
		for(int i = 0; i < loot.length; i++) {
			int[] groundLoot = getItemById(loot[i]);	
			if(groundLoot[1] == getX() && groundLoot[2] == getY() && getEmptySlots() > 0){
				if(groundLoot[0] != -1 && !inCombat()) {
					pickupItem(groundLoot[0], groundLoot[1], groundLoot[2]);
					return random(600, 1200);
				}
			}						
		}
       
       if (!isWalking() && !inCombat() && logOut == false) {
           atObject(LEDGES[ledge][0], LEDGES[ledge][1]);
           return random(110,180);
       }

	   if (inCombat()) {
		walkTo(getX(), getY());
		return random (100, 200);
		}
		
       return random(10,30);
	   

   }

   @Override
   public void paint() {
       int x = 10;
       int y = 30;
       drawString("Yanille Ledge Balance", x, y, 4, 0x00b500);
       y += 12;
       drawString("Successfully balanced " + balance_success + " times.", x, y, 1, 0xFFFFFF);
       y += 12;
       for (int i = 0; i < SKILL.length; ++i) {
           int[] xp = getXpStatistics(i);
           if (xp[2] > 0) {
               drawString(SKILL[i] + " XP Gained: " + xp[2] + " (" + xp[3] + " XP/h)", x, y, 1, 0xFFFFFF);
               y += 12;
           }
       }
       drawString("Runtime: " + getRunTime(), x, y, 1, 0xFFFFFF);
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

   public void stop() {
       //AutoLogin.setAutoLogin(false);
       logout();
       stopScript();
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

   @Override
   public void onChatMessage(String msg, String name, boolean pmod, boolean jmod) {
       super.onChatMessage(msg, name, pmod, jmod);
       if (jmod) {
           stop();
       }
   }

   @Override
   public void onPrivateMessage(String msg, String name, boolean pmod, boolean jmod) {
       super.onPrivateMessage(msg, name, pmod, jmod);
       if (jmod) {
           stop();
       }
   }

   @Override
   public void onServerMessage(String s) {
       if (s.contains("skillfully balance")) {
           balance_success++;
           return;
       }
       if (s.contains("just advanced")) {
           screenshot[0] = System.currentTimeMillis();
           filename = screenshot[0] + " - " + s;
           return;
       }
       if (s.contains("too tired")) {
           sleep_at = getFatigue() - 1;
           return;
       }
	   if (s.contains("standing here")) {
			logOut = true;;
		}
   }

   private int screenshot(long now) {
       if (now > screenshot[0] + 3000L) {
           screenshot[0] = -1L;
           return 50;
       }
       if (screenshot[1] == 0) {
           if (isPaintOverlay()) {
               screenshot[2] = 1L;
               setPaintOverlay(false);
           } else {
               screenshot[2] = -1L;
           }
           if (isRendering()) {
               screenshot[3] = 1L;
           } else {
               screenshot[3] = -1L;
               setRendering(true);
           }
           screenshot[1] = 1;
           return 50;
       }
       if (isSkipLines()) {
           setSkipLines(false);
       }
       if (now < screenshot[0] + 1000L) {
           return 50;
       }
       takeScreenshot(filename);
       screenshot[0] = -1L;
       screenshot[1] = 0;
       if (screenshot[2] == 1L) {
           setPaintOverlay(true);
       }
       if (screenshot[3] != 1L) {
           setRendering(false);
       }
       return 50;
   }
}