package callbacks;

import bot.Main;
import compatibility.sbot.Script;
import controller.Controller;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import orsc.ORSCharacter;
import scripting.idlescript.IdleScript;

public class KeyCallback {

  private static class SwitchThread implements Runnable {
    private final Controller c;
    private final boolean attack;
    private final boolean strength;
    private final boolean defence;

    public SwitchThread(Controller c, boolean attack, boolean strength, boolean defence) {
      this.c = c;
      this.attack = attack;
      this.strength = strength;
      this.defence = defence;
    }
    /** Uses CLI provided stake switcher SwitchId to switch between atk/str/def items */
    @Override
    public void run() {
      if (attack) {
        ArrayList<Integer> items = Main.config.getAttackItems();

        if (items == null) {
          c.displayMessage("@red@IdleRSC@yel@: @gre@Attack items not configured! See README!");
          return;
        }
        c.displayMessage("@red@IdleRSC@yel@: @gre@Attack switching!");

        for (Integer id : items) {
          c.equipItem(c.getInventoryItemSlotIndex(id));
          c.sleep(640);
        }

        c.displayMessage("@red@IdleRSC@yel@: @gre@Attack switched!");

      } else if (strength) {
        ArrayList<Integer> items = Main.config.getStrengthItems();
        if (items == null) {
          c.displayMessage("@red@IdleRSC@yel@: @gre@Strength items not configured! See README!");
          return;
        }
        c.displayMessage("@red@IdleRSC@yel@: @red@Strength switching!");

        for (Integer id : items) {
          c.equipItem(c.getInventoryItemSlotIndex(id));
          c.sleep(640);
        }

        c.displayMessage("@red@IdleRSC@yel@: @red@Strength switched!");

      } else if (defence) {
        ArrayList<Integer> items = Main.config.getDefenceItems();
        if (items == null) {
          c.displayMessage("@red@IdleRSC@yel@: @gre@Defence items not configured! See README!");
          return;
        }

        c.displayMessage("@red@IdleRSC@yel@: @cya@Defense switching!");

        for (Integer id : items) {
          c.equipItem(c.getInventoryItemSlotIndex(id));
          c.sleep(640);
        }

        c.displayMessage("@red@IdleRSC@yel@: @cya@Defense switched!");
      }
    }
  }
  /**
   * Hook that Handles key presses and calls other methods
   *
   * @param key the KeyEvent to handle
   */
  public static void keyHook(KeyEvent key) {
    Controller c = Main.getController();
    char keyChar = key.getKeyChar();
    int keycode = key.getKeyCode();

    if (keycode == KeyEvent.VK_F5) {
      SwitchThread switchThread = new SwitchThread(c, true, false, false);
      Thread t = new Thread(switchThread);
      t.start();
    } else if (keycode == KeyEvent.VK_F6) {
      SwitchThread switchThread = new SwitchThread(c, false, true, false);
      Thread t = new Thread(switchThread);
      t.start();
    } else if (keycode == KeyEvent.VK_F7) {
      SwitchThread switchThread = new SwitchThread(c, false, false, true);
      Thread t = new Thread(switchThread);
      t.start();
    } else if (keycode == KeyEvent.VK_F8) {
      ORSCharacter npc = c.getNpcAtCoords(c.currentX(), c.currentY());
      int playerIndex = c.getPlayerAtCoord(c.currentX(), c.currentY());

      if (Main.config.getSpellId() == -1) {
        c.displayMessage("@red@IdleRSC@yel@: @gre@Spell not configured! See README!");
        return;
      }
      if (npc != null) {
        c.displayMessage("@red@IdleRSC@yel@: @gre@Casting spell!");
        c.castSpellOnNpc(npc.serverIndex, Main.config.getSpellId());
      } else if (playerIndex != -1) {
        c.displayMessage("@red@IdleRSC@yel@: @gre@Casting spell!");
        c.castSpellOnPlayer(Main.config.getSpellId(), playerIndex);
      } else {
        c.displayMessage("@red@IdleRSC@yel@: @gre@Not in combat! Cannot cast spell!");
      }
    } else if (keycode == KeyEvent.VK_F9) { // take screenshot with name = current timestamp
      c.takeScreenshot("");
      // F10 is already reserved for locking render
    } else if (keycode == KeyEvent.VK_F11) {
      if (Main.isRunning()) c.displayMessage("@red@IdleRSC@yel@: SCRIPT STOPPED");

      Main.setRunning(false);
      Main.showLoadScript();
    } else if (keycode == KeyEvent.VK_F12) {
      if (c != null) {
        c.chatMessage("::bothelp");
      }
    } else if (keycode == KeyEvent.VK_V) {
      if ((key.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
        c.paste();
      }
    } else {
      if (c != null
          && c.getShowBotPaint()
          && c.isRunning()
          && Main.getCurrentRunningScript() != null) {
        if (Main.getCurrentRunningScript() instanceof IdleScript) {
          ((IdleScript) Main.getCurrentRunningScript()).keyPressInterrupt(keycode);
        }
        if (Main.getCurrentRunningScript() instanceof Script) {
          ((Script) Main.getCurrentRunningScript()).KeyPressed(keycode);
        }
      }
    }
  }
}
